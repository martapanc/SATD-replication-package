2/report.java
Satd-method: public IRubyObject invoke(IRubyObject[] args) {
********************************************
********************************************
2/After/545da6c46  Fix JRUBY-6619: NoMethod diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7458b0531a 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* ___getProxyClass
* newTypeError
* hasSuperImplementation
* getMethod
* newArgumentError
* getValue
* getDeclaringClass
* isInstance
* isFinal
* getModifiers
* getSuperMethod
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getModifiers() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
2/After/6fdc42dcc  Partial fix for JRUBY-42 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7458b0531a 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* ___getProxyClass
* newTypeError
* hasSuperImplementation
* getMethod
* newArgumentError
* getValue
* getDeclaringClass
* isInstance
* isFinal
* getModifiers
* getSuperMethod
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getModifiers() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
2/After/7458b0531  fixes JRUBY-4599: Invoki diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        Object javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();
+        Object javaInvokee = null;
-        if (! method.getDeclaringClass().isInstance(javaInvokee)) {
-            throw getRuntime().newTypeError("invokee not instance of method's class (" +
-                                              "got" + javaInvokee.getClass().getName() + " wanted " +
-                                              method.getDeclaringClass().getName() + ")");
-        }
-        
-        //
-        // this test really means, that this is a ruby-defined subclass of a java class
-        //
-        if (javaInvokee instanceof InternalJavaProxy &&
-                // don't bother to check if final method, it won't
-                // be there (not generated, can't be!)
-                !Modifier.isFinal(method.getModifiers())) {
-            JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
-                    .___getProxyClass();
-            JavaProxyMethod jpm;
-            if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
-                    jpm.hasSuperImplementation()) {
-                return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
+        if (!isStatic()) {
+            javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();
+
+            if (! method.getDeclaringClass().isInstance(javaInvokee)) {
+                throw getRuntime().newTypeError("invokee not instance of method's class (" +
+                                                  "got" + javaInvokee.getClass().getName() + " wanted " +
+                                                  method.getDeclaringClass().getName() + ")");
+            }
+
+            //
+            // this test really means, that this is a ruby-defined subclass of a java class
+            //
+            if (javaInvokee instanceof InternalJavaProxy &&
+                    // don't bother to check if final method, it won't
+                    // be there (not generated, can't be!)
+                    !Modifier.isFinal(method.getModifiers())) {
+                JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
+                        .___getProxyClass();
+                JavaProxyMethod jpm;
+                if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
+                        jpm.hasSuperImplementation()) {
+                    return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
+                }

Lines added: 24. Lines removed: 20. Tot = 44
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7458b0531a 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* ___getProxyClass
* newTypeError
* hasSuperImplementation
* getMethod
* newArgumentError
* getValue
* getDeclaringClass
* isInstance
* isFinal
* getModifiers
* getSuperMethod
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getModifiers() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
2/After/7a8e66113  Fixes for JRUBY-4732: Cl diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();
+            javaInvokee = JavaUtil.unwrapJavaValue(getRuntime(), invokee, "invokee not a java object");

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7458b0531a 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* ___getProxyClass
* newTypeError
* hasSuperImplementation
* getMethod
* newArgumentError
* getValue
* getDeclaringClass
* isInstance
* isFinal
* getModifiers
* getSuperMethod
—————————
Method found in diff:	private Method getMethod(String name, Class... argTypes) {
-        Class jclass = getJavaObject().getJavaClass();
-            return jclass.getMethod(name, argTypes);
+            return getObject().getClass().getMethod(name, argTypes);
-            throw JavaMethod.newMethodNotFoundError(getRuntime(), jclass, name + CodegenUtils.prettyParams(argTypes), name);
+            throw JavaMethod.newMethodNotFoundError(getRuntime(), getObject().getClass(), name + CodegenUtils.prettyParams(argTypes), name);

Lines added: 2. Lines removed: 3. Tot = 5
—————————
Method found in diff:	public IRubyObject getValue(ThreadContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getModifiers() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
2/After/ec8d280eb  Fix for JRUBY-4799: Unca diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7458b0531a 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* ___getProxyClass
* newTypeError
* hasSuperImplementation
* getMethod
* newArgumentError
* getValue
* getDeclaringClass
* isInstance
* isFinal
* getModifiers
* getSuperMethod
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getModifiers() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
2/After/f38b8e5a3  Kinda sorta fix JRUBY-66 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7458b0531a 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getClass
* getName
* ___getProxyClass
* newTypeError
* hasSuperImplementation
* getMethod
* newArgumentError
* getValue
* getDeclaringClass
* isInstance
* isFinal
* getModifiers
* getSuperMethod
—————————
Method found in diff:	public Object getValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getModifiers() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
