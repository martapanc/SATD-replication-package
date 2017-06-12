File path: src/functions/org/apache/jmeter/functions/Jexl2Function.java
Comment: TODO should the engine be static?
Initial commit id: 516ff04df
Final commit id: 9de489add
   Bugs between [       3]:
9de489add Bug 56708 - __jexl2 doesnt scale with multiple CPU cores Bugzilla Id: 56708
ea92414e2 Bug 54199 - Move to Java 6 add @Override Bugzilla Id: 54199
4171b5a6f Bug 52680 - Mention version in which function was introduced
   Bugs after [       2]:
223933936 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
f03a8bdbe Bug 57114 - Performance : Functions that only have values as instance variable should not synchronize execute Bugzilla Id: 57114

Start block index: 47
End block index: 55
    // TODO should the engine be static?
    private static final JexlEngine jexl = new JexlEngine();

*********************** Method when SATD was removed **************************

(removed)

-    // TODO should the engine be static?
-    private static final JexlEngine jexl = new JexlEngine();
