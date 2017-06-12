89/report.java
Satd-method: public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
********************************************
********************************************
89/Between/0b7aed72d  Alternative fix for JRUB diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/0c8b72d57  Fix for JRUBY-3237: IO.p diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/15832d3b4  Fix JRUBY-5514: Errno::E diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    if (ioObj.getHandler().readDataBuffered()) {
+                    if (ioObj.getOpenFile().getMainStreamSafe().readDataBuffered()) {
+        } catch (BadDescriptorException e) {
+            throw runtime.newErrnoEBADFError();
-            throw runtime.newIOError(e.getMessage());
+            throw runtime.newIOErrorFromException(e);

Lines added: 4. Lines removed: 2. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

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
Method found in diff:	public RaiseException newArgumentError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newIOError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray newArray() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/1df3506c0  Fix for JRUBY-4908: IO.p diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/20924a2ab  fix JRUBY-5436: File.ope diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/2270b5875  Fix JRUBY-5389: IO.popen diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/23c2ef709  fix JRUBY-4932: IO.popen diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/26351a1be  Fix JRUBY-5487: Kernel#s diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-                    
+
+                    // already buffered data? don't bother selecting
+                    if (ioObj.getHandler().readDataBuffered()) {
+                        unselectable_reads.add(obj);
+                    }

Lines added: 5. Lines removed: 1. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/2ae49952b  Fix IO#autoclose= to tak diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/2e751d669  Fix JRUBY-5503: Timeout: diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/3487bba2f  Fixed JRUBY-4116 and JRU diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public RubyClass getIO() {

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
Method found in diff:	public IRubyObject getNil() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/387104937  Fix for JRUBY-4908: IO.p diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/434b81819  Fix for JRUBY-3483: Redi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/4579aa254  Fix for JRUBY-3869: 'put diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/49f6aa509  Fix for JRUBY-3405: pope diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/4d687865f  Change ordering of selec diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-           for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
-               SelectionKey key = (SelectionKey) i.next();
+           Set<SelectionKey> keys = selector.keys(); // get keys before close
+           selector.close(); // close unregisters all channels, so we can safely reset blocking modes
+           for (SelectionKey key : keys) {
-           selector.close();

Lines added: 3. Lines removed: 3. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/5b3ecffcc  Partial fix for JRUBY-33 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public String remove(int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/5c55540d7  fix JRUBY-5400: [1.9] IO diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/60f3d6628  Fix for JRUBY-1079: IO.s diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

--- a/src/org/jruby/util/io/OpenFile.java
+++ b/src/org/jruby/util/io/OpenFile.java
-                        runtime.getDescriptors().remove(Integer.valueOf(pipe.getFileno()));
+                        runtime.unregisterDescriptor(pipe.getFileno());
-                        runtime.getDescriptors().remove(Integer.valueOf(main.getFileno()));
+                        runtime.unregisterDescriptor(main.getFileno());
--- a/test/test_io.rb
+++ b/test/test_io.rb
+  def test_sysopen
+    ensure_files @file
+
+    fno = IO::sysopen(@file, "r", 0124) # not creating, mode is ignored
+    assert_instance_of(Fixnum, fno)
+    assert_raises(Errno::EINVAL) { IO.open(fno, "w") } # not writable
+    IO.open(fno, "r") do |io|
+      assert_equal(fno, io.fileno)
+      assert(!io.closed?)
+    end
+    assert_raises(Errno::EBADF) { IO.open(fno, "r") } # fd is closed
+    File.open(@file) do |f|
+      mode = (f.stat.mode & 0777) # only comparing lower 9 bits
+      assert(mode > 0124)
+    end
+
+    File.delete(@file)
+    fno = IO::sysopen(@file, "w", 0611) # creating, mode is enforced
+    File.open(@file) do |f|
+      mode = (f.stat.mode & 0777)
+      assert_equal(0611, mode)
+    end
+  end
+

Lines added: 28. Lines removed: 4. Tot = 32
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newArgumentError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newIOError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray newArray() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/6ce486ad8  Fix for JRUBY-3799: Bug  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray keys() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/713c1c7a6  Possible fix for JRUBY-4 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/71bcc5a48  fixes JRUBY-4652: [1.9]  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/7bc5d6b33  Fix JRUBY-4960: 1.9: put diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/82c0b9b8b  Fix for JRUBY-3071: Ille diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/864087b8c  Fix JRUBY-5532: IO.forea diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/891409e4b  Improve and expand fix f diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/94392e172  Fix all locations where  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            Map<RubyIO, Boolean> blocking = new HashMap();
+            
+
+                    // save blocking state
+                    if (ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
+                    
+
+                    // save blocking state
+                    if (!blocking.containsKey(ioObj) && ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
+
-            Set<SelectionKey> keys = selector.keys(); // get keys before close
-            for (SelectionKey key : keys) {
-                SelectableChannel channel = key.channel();
+            for (Map.Entry blockingEntry : blocking.entrySet()) {
+                SelectableChannel channel = (SelectableChannel)((RubyIO)blockingEntry.getKey()).getChannel();
-                    RubyIO originalIO = (RubyIO)TypeConverter.convertToType(
-                            (IRubyObject)key.attachment(), runtime.getIO(), "to_io");
-                    boolean blocking = originalIO.getBlocking();
-                    key.cancel();
-                    channel.configureBlocking(blocking);
+                    channel.configureBlocking((Boolean)blockingEntry.getValue());

Lines added: 13. Lines removed: 8. Tot = 21
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/96c1df8be  Fix for JRUBY-4650 in 1. diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/99a5b3189  Additional fix for JRUBY diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/a1f900fc2  Fix for JRUBY-4504: Inst diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/b03c7b478  fixes JRUBY-4011: IO.bin diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/b89d8a699  Fix JRUBY-4595: [1.9] Fi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/c97a0fb3f  Fix for JRUBY-5124: Bund diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/cbe056d42  Fix for JRUBY-3652: IO.p diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/d60304cf1  Fix for JRUBY-5193: a Fi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/d9f4b889d  Add a fix for JRUBY-5076 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            selector = Selector.open();
+            selector = SelectorFactory.openWithRetryFrom(context.getRuntime(), SelectorProvider.provider());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public void cancel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray keys() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/dc1096071  Fix JRUBY-3857: IO.fsync diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/df5769364  Fix for JRUBY-3198: Stri diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
89/Between/fdd28b583  Fix for JRUBY-3778: clon diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
select_static(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* selectNow
* getName
* select
* getDoubleValue
* channel
* iterator
* getList
* getBlocking
* getMode
* getMetaClass
* getIO
* newArgumentError
* interestOps
* keys
* selectedKeys
* remove
* getNil
* newIOError
* attachment
* readyOps
* blockingLock
* writeDataBuffered
* newArray
* getMessage
* round
* addAll
* isNil
* open
* configureBlocking
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
