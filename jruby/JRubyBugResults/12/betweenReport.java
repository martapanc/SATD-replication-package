12/report.java
Satd-method: public IRubyObject read(IRubyObject[] args) {
********************************************
********************************************
12/Between/03f8a3762  Fix for JRUBY-1047, add  diff.java
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
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/0c8b72d57  Fix for JRUBY-3237: IO.p diff.java
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
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/23c2ef709  fix JRUBY-4932: IO.popen diff.java
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
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

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
12/Between/24a1c73de  fix JRUBY-2869, and part diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/282588e76  Fix JRUBY-3008 by not fl diff.java
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
Method found in diff:	public void checkReadable() throws IOException {

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
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/28cedab88  Fix JRUBY-3019 by resett diff.java
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
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/31d26f76b  Fix JRUBY-3009.  Convert diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/3487bba2f  Fixed JRUBY-4116 and JRU diff.java
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
Method found in diff:	public void checkSafeString(IRubyObject object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getNil() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyClass getNilClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyClass getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String remove(int i) {
+            if (securityRestricted) {
+                // do nothing, we can't modify the history without
+                // accessing private members of History.
+                return "";
+            }

Lines added: 5. Lines removed: 0. Tot = 5
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyClass getString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject setValue(IRubyObject newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/3c58a8a75  Fixed regression in JRUB diff.java
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
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {
--- a/src/org/jruby/util/ShellLauncher.java
+++ b/src/org/jruby/util/ShellLauncher.java
+
+        // The assumption here is that the 'in' stream provides
+        // proper available() support. If available() always
+        // returns 0, we'll hang!
+
--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
+
+    /**
+     * Used to work-around blocking problems with STDIN. In most cases <code>null</code>.
+     * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
+     * for more details. You probably should not use it.
+     */
+    private InputStream baseInputStream;
-     * Construct a new ChannelDescriptor with the given channel, fil enumber, mode flags,
+     * Construct a new ChannelDescriptor with the given channel, file number, mode flags,
-
+
+    /**
+     * Special constructor to create the ChannelDescriptor out of the stream, file number,
+     * mode flags, and file descriptor object. The channel will be created from the
+     * provided stream. The channel will be kept open until all ChannelDescriptor
+     * references to it have been closed. <b>Note:</b> in most cases, you should not
+     * use this constructor, it's reserved mostly for STDIN.
+     *
+     * @param baseInputStream The stream to create the channel for the new descriptor
+     * @param fileno The file number for the new descriptor
+     * @param originalModes The mode flags for the new descriptor
+     * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
+     */
+    public ChannelDescriptor(InputStream baseInputStream, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor) {
+        // The reason why we need the stream is to be able to invoke available() on it.
+        // STDIN in Java is non-interruptible, non-selectable, and attempt to read
+        // on such stream might lead to thread being blocked without *any* way to unblock it.
+        // That's where available() comes it, so at least we could check whether
+        // anything is available to be read without blocking.
+        this(Channels.newChannel(baseInputStream), fileno, originalModes, fileDescriptor, new AtomicInteger(1));
+        this.baseInputStream = baseInputStream;
+    }
+
-     * Construct a new ChannelDescriptor with the given channel, fil enumber,
+     * Construct a new ChannelDescriptor with the given channel, file number,
+     * This is intentionally non-public, since it should not be really
+     * used outside of very limited use case (handling of STDIN).
+     * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
+     * for more info.
+     */
+    /*package-protected*/ InputStream getBaseInputStream() {
+        return baseInputStream;
+    }
+
+    /
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
-        return new BufferedInputStream(Channels.newInputStream((ReadableByteChannel)descriptor.getChannel()));
+        InputStream in = descriptor.getBaseInputStream();
+        if (in == null) {
+            return new BufferedInputStream(Channels.newInputStream((ReadableByteChannel)descriptor.getChannel()));
+        } else {
+            return in;
+        }
--- a/test/test_launching_by_shell_script.rb
+++ b/test/test_launching_by_shell_script.rb
+  WINDOWS = Config::CONFIG['host_os'] =~ /Windows|mswin/
+  def jruby_with_pipe(pipe, *args)
+    prev_in_process = JRuby.runtime.instance_config.run_ruby_in_process
+    JRuby.runtime.instance_config.run_ruby_in_process = false
+    `#{pipe} | #{RUBY} #{args.join(' ')}`
+   ensure
+    JRuby.runtime.instance_config.run_ruby_in_process = prev_in_process
+  end
+
+  def test_system_call_without_stdin_data_doesnt_hang
+    out = jruby("-e 'system \"dir\"'")
+    assert(out =~ /COPYING.LGPL/)
+  end
+
+  if !WINDOWS
+    def test_system_call_with_stdin_data_doesnt_hang
+      out = jruby_with_pipe("echo 'vvs'", "-e 'system \"cat\"'")
+      assert_equal("vvs\n", out)
+    end
+  end
+

Lines added: 78. Lines removed: 8. Tot = 86
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean readDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor getDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor dup() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/434b81819  Fix for JRUBY-3483: Redi diff.java
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
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/4579aa254  Fix for JRUBY-3869: put diff.java
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
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/45ff75add  Fix for JRUBY-2998.  Use diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/49f6aa509  Fix for JRUBY-3405: pope diff.java
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
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/4ad8f70a8  Improved fix for JRUBY-2 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        RubyString separator;
-        if (count == 2) {
-            if (args[1].isNil()) {
-                separator = runtime.newString();
-            } else {
-                separator = args[1].convertToString();
-            }
-        } else {
-            separator = runtime.getGlobalVariables().get("$/").convertToString();
-        }
-
+        ByteList separator = getSeparatorFromArgs(runtime, args, 1);
+
-                ByteList sep = separator.getByteList();
-                IRubyObject str = io.getline(sep);
+                IRubyObject str = io.getline(separator);
-                    str = io.getline(sep);
+                    str = io.getline(separator);

Lines added: 4. Lines removed: 14. Tot = 18
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
Method found in diff:	public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(ByteList separator) {
-                int newline;
-                if (separator.length() < 1) {
-                    // We'll never match this one.
-                    // This is our way of saying that separator is empty string.
-                    newline = 256;
-                } else {
-                    newline = separator.get(separator.length() - 1) & 0xFF;
-                }
+                int newline = separator.get(separator.length() - 1) & 0xFF;

Lines added: 1. Lines removed: 8. Tot = 9
********************************************
********************************************
12/Between/4d687865f  Change ordering of selec diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/593f867f0  Fix for JRUBY-2644, TCPS diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
+    public synchronized void addBlockingThread(RubyThread thread) {
+    public synchronized void removeBlockingThread(RubyThread thread) {
+                io.addBlockingThread(this);
+                io.removeBlockingThread(this);

Lines added containing method: 4. Lines removed containing method: 0. Tot = 4
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
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

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
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray keys() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/5b3ecffcc  Partial fix for JRUBY-33 diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String remove(int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {
-            e.printStackTrace();

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
12/Between/60f3d6628  Fix for JRUBY-1079: IO.s diff.java
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
Method found in diff:	public RubyString newStringShared(ByteList byteList) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, PipeException, InvalidValueException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

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
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
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

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

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
12/Between/666dcb838  Fix JRUBY-2281 by making diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/6967d56b0  Various fixes to the nu diff.java
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
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {
+        } else if (descriptor.isNull()) {
+            return new ByteList(0);

Lines added: 2. Lines removed: 0. Tot = 2
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(IRubyObject recv, IRubyObject[] args, boolean tryToYield, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

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
Method found in diff:	public IRubyObject getline(ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor dup() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/6ce486ad8  Fix for JRUBY-3799: Bug  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
+      x.report("#{read_iter}.times { f.sysread(#{size}) }") {
+          read_iter.times { f.sysread(size) }
-            context.getThread().beforeBlockingCall();
+            context.getThread().beforeBlockingCall();
+            context.getThread().select(this, SelectionKey.OP_READ);
-                    io.removeBlockingThread(this);
+                        io.removeBlockingThread(this);
+      timeout(0.1) { client.sysread(1024) }

Lines added containing method: 6. Lines removed containing method: 2. Tot = 8
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
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ThreadContext getCurrentContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized IRubyObject run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray keys() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/713c1c7a6  Possible fix for JRUBY-4 diff.java
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
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/71bcc5a48  fixes JRUBY-4652: [1.9]  diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/7bc5d6b33  Fix JRUBY-4960: 1.9: put diff.java
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
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/82c0b9b8b  Fix for JRUBY-3071: Ille diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/94392e172  Fix all locations where  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
-                    IRubyObject readItems = io.read(new IRubyObject[]{recv.getRuntime().newFixnum(1024*16)});
+                        IRubyObject readItems = io.read(new IRubyObject[]{recv.getRuntime().newFixnum(1024*16)});

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
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
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/96c1df8be  Fix for JRUBY-4650 in 1. diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/9709fc2ea  Fix for JRUBY-2615, inte diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
-                    public int read() throws IOException {
+                    int read = inChannel.read(buf);

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
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
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/a1f900fc2  Fix for JRUBY-4504: Inst diff.java
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
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/ae65e95de  Fix for JRUBY-2625, pend diff.java
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
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public synchronized int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/afe466dba  IO#readpartial fixes: -  diff.java
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
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/b03c7b478  fixes JRUBY-4011: IO.bin diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getline(Ruby runtime, ByteList separator) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/b0693ba5a  Probable fix for JRUBY-2 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
-        return read(args);

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
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
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/b1d615bc7  Fixes for EOF logic in C diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                if (length > 0) {
-                    // I think this is only partly correct; sys fail based on errno in Ruby
-                    throw getRuntime().newEOFError();
-                }
+                // Removed while working on JRUBY-2386, since fixes for that
+                // modified EOF logic such that this check is not really valid.
+                // We expect that an EOFException will be thrown now in EOF
+                // cases.
+//                if (length > 0) {
+//                    // I think this is only partly correct; sys fail based on errno in Ruby
+//                    throw getRuntime().newEOFError();
+//                }

Lines added: 8. Lines removed: 4. Tot = 12
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
Method found in diff:	public FileDescriptor getFileDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static ChannelDescriptor open(String cwd, String path, ModeFlags flags) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public synchronized int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor dup() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/c97a0fb3f  Fix for JRUBY-5124: Bund diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/cbe056d42  Fix for JRUBY-3652: IO.p diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/cdf5c7674  partially fix JRUBY-2869 diff.java
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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/d9f4b889d  Add a fix for JRUBY-5076 diff.java
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
Method found in diff:	public void cancel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

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
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized IRubyObject run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public RubyArray keys() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/dc1096071  Fix JRUBY-3857: IO.fsync diff.java
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
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/df5769364  Fix for JRUBY-3198: Stri diff.java
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
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ByteList getByteList() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setValue(CharSequence value) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/e85e442e2  Fix for JRUBY-2164. Add  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
+
+    /**
+     * Write the bytes in the specified byte list to the associated channel.
+     *
+     * @param buf the byte list containing the bytes to be written
+     * @param offset the offset to start at. this is relative to the begin variable in the but
+     * @param len the amount of bytes to write. this should not be longer than the buffer
+     * @return the number of bytes actually written
+     * @throws java.io.IOException if there is an exception during IO
+     * @throws org.jruby.util.io.BadDescriptorException if the associated
+     * channel is already closed
+     */
+    public int write(ByteList buf, int offset, int len) throws IOException, BadDescriptorException {
+        checkOpen();
+
+        return internalWrite(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin()+offset, len));
+    }
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
+        int len = buffer.position();
-        descriptor.write(buffer);
+        int n = descriptor.write(buffer);
+
+        if(n != len) {
+            // TODO: check the return value here
+        }
+
-            descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
+
+            int n = descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
+            if(n != buf.length()) {
+                // TODO: check the return value here
+            }

Lines added: 31. Lines removed: 4. Tot = 35
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
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

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
Method found in diff:	public IRubyObject getline(ByteList separator) {
-                        if (c == -1) {
-                            // TODO: clear error, wait for it to become readable
-                            break;
+                        if(c == -1) {
+                            if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
+                                if(!(waitReadable(((ChannelStream)readStream).getDescriptor()))) {
+                                    throw getRuntime().newIOError("bad file descriptor: " + openFile.getPath());
+                                }
+
+                                continue;
+                            } else {
+                                break;
+                            }

Lines added: 10. Lines removed: 3. Tot = 13
—————————
Method found in diff:	public synchronized boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor dup() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/Between/fdd28b583  Fix for JRUBY-3778: clon diff.java
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
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

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
Method found in diff:	protected void checkClosed() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
