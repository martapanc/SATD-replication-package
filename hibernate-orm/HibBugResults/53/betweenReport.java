53/report.java
Satd-method: protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
********************************************
********************************************
53/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            LOG.unableToCreateProxyFactory(getEntityName(), he);
+			LOG.unableToCreateProxyFactory( getEntityName(), he );

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildProxyFactory(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* unableToCreateProxyFactory
********************************************
********************************************
53/Between/ HHH-8741  8fe5460e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildProxyFactory(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* unableToCreateProxyFactory
********************************************
********************************************
53/Between/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildProxyFactory(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* unableToCreateProxyFactory
********************************************
********************************************
53/Between/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
buildProxyFactory(
-	protected abstract ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter);
-			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
-	protected ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter) {
-	protected ProxyFactory buildProxyFactory(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
-		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
-//		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();

Lines added containing method: 0. Lines removed containing method: 6. Tot = 6
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* unableToCreateProxyFactory
********************************************
********************************************
