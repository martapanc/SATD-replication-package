33/report.java
Satd-method: public Object intercept(
********************************************
********************************************
33/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
intercept(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getParameterTypes
* getCallback
* substring
* identityHashCode
* getReturnType
* startsWith
********************************************
********************************************
33/Between/ HHH-6025  82d2ef4b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		public Object intercept(

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
intercept(
-		public Object intercept(
-		return ( ( Boolean ) intercept( target, name, oldValue  ? Boolean.TRUE : Boolean.FALSE ) )
-		return ( ( Byte ) intercept( target, name, new Byte( oldValue ) ) ).byteValue();
-		return ( ( Character ) intercept( target, name, new Character( oldValue ) ) )
-		return ( ( Double ) intercept( target, name, new Double( oldValue ) ) )
-		return ( ( Float ) intercept( target, name, new Float( oldValue ) ) )
-		return ( ( Integer ) intercept( target, name, new Integer( oldValue ) ) )
-		return ( ( Long ) intercept( target, name, new Long( oldValue ) ) ).longValue();
-		return ( ( Short ) intercept( target, name, new Short( oldValue ) ) )
-		Object value = intercept( target, name, oldValue );
-		intercept( target, name, oldValue ? Boolean.TRUE : Boolean.FALSE );
-		intercept( target, name, new Byte( oldValue ) );
-		intercept( target, name, new Character( oldValue ) );
-		intercept( target, name, new Double( oldValue ) );
-		intercept( target, name, new Float( oldValue ) );
-		intercept( target, name, new Integer( oldValue ) );
-		intercept( target, name, new Long( oldValue ) );
-		intercept( target, name, new Short( oldValue ) );
-		intercept( target, name, oldValue );

Lines added containing method: 0. Lines removed containing method: 19. Tot = 19
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getParameterTypes
* getCallback
* substring
* identityHashCode
* getReturnType
* startsWith
—————————
Method found in diff:	-		public String getName() {
-		public String getName() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
