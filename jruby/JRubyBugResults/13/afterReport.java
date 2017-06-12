13/report.java
Satd-method: public void Regexp(Regexp regexp) {
********************************************
********************************************
13/After/0825e699f  fix GH-2591 on master. k diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/18cde0df7  Separate varargs and spe diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
********************************************
********************************************
13/After/29371d935  Fix #2409: Splat --> Bui diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public Handle getHandle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Arity getArity() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isBeginEndBlock() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasKnownValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void SValue(SValue svalue) { error(svalue); }

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+    public Operand getArray() {
+    public Operand getArray() {
+        return array;
+    }

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
13/After/4e4935ed6  Fixes #4319.  JRuby can  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject dup() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/5861b1b9c  Fix __FILE__ in JIT. Fix diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/5f83b908f  Fixes #4328. Literal rat diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private String getFileName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public BigInteger getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/6da2fed17  Revert to uncached super diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) { error(svalue); }

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/89f548ea9  Fixes #2172: Symbols nee diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        jvmMethod().pushSymbol(symbol.getName());
+        jvmMethod().pushSymbol(symbol.getName(), symbol.getEncoding());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void pushRegexp(int options) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Handle getHandle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Arity getArity() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isBeginEndBlock() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void invokeHelper(String name, String sig) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void loadStaticScope() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void pushString(ByteList bl) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public void pushSymbol(String sym) {
-    public void pushSymbol(String sym) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public void loadContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void loadLocal(int i) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void pushUndefined() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void loadRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/c3b80aa0f  Fix defined?(::Object) l diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
13/After/c9c239074  Fixes #3046. Shellescape diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
********************************************
********************************************
13/After/cf2df89ba  Disable the AddLocalVarL diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
********************************************
********************************************
13/After/d211f1938  Fix #2409: Unbreak JIT diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        jvmMethod().loadContext();

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
Regexp(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isForLoopBody
* pushRegexp
* getHandle
* getArity
* isBeginEndBlock
* getPieces
* invokevirtual
* method
* newobj
* encodeScope
* anewarray
* getSelf
* aload
* invokespecial
* hasKnownValue
* SValue
* isOnce
* getClosure
* toEmbeddedOptions
* invokeHelper
* getClass
* getName
* loadStaticScope
* getArray
* aastore
* invokestatic
* getParameterList
* pushString
* getFileName
* getRegexp
* getByteList
* getSimpleName
* getStaticScope
* pushSymbol
* loadContext
* loadLocal
* getKCode
* stringJoin
* ldc
* pushUndefined
* getLineNumber
* getValue
* loadRuntime
* dup
—————————
Method found in diff:	public void SValue(SValue svalue) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
