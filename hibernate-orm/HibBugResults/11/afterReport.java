11/report.java
Satd-method: protected void activateLocalClusterNode() {
********************************************
********************************************
11/After/ [HHH-3817 eb601601_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+            establishInternalNodes();
-            if (tx != null)
-               resume(tx);
+            resume(tx);

Lines added: 2. Lines removed: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
activateLocalClusterNode(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* addCacheListener
* getVersion
* currentThread
* getCacheMode
* addNode
* setVersion
* getChild
* getMessage
* getClassLoader
* registerContextClassLoader
* isActive
* isUseRegionBasedMarshalling
* getConfiguration
* setResident
* getRegion
* getContextClassLoader
* activate
* getRoot
* isClusteredReplication
—————————
Method found in diff:	public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident, DataVersion version)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean isClusteredReplication(Cache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
11/After/ HHH-4028  b6bfb72d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
activateLocalClusterNode(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* addCacheListener
* getVersion
* currentThread
* getCacheMode
* addNode
* setVersion
* getChild
* getMessage
* getClassLoader
* registerContextClassLoader
* isActive
* isUseRegionBasedMarshalling
* getConfiguration
* setResident
* getRegion
* getContextClassLoader
* activate
* getRoot
* isClusteredReplication
********************************************
********************************************
