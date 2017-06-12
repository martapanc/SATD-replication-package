File path: src/core/org/apache/jmeter/util/JMeterUtils.java
Comment: TODO probably not needed
Initial commit id: c932ee6a2
Final commit id: 3392c7314
   Bugs between [      10]:
c93177faa Bug 60266 - Usability/ UX : It should not be possible to close/exit/Revert/Load/Load a recent project or create from template a JMeter plan or open a new one if a test is running Bugzilla Id: 60266
01618c3e6 Bug 60018 - Timer : Add a factor to apply on pauses Bugzilla Id: 60018
a7efa9efe Bug 60125 - Report / Dashboard : Dashboard cannot be generated if the default delimiter is \t Bugzilla Id: 60125
ebb9c4e45 Bug 58100 - Performance enhancements : Replace Random by ThreadLocalRandom Bugzilla Id: 58100
0d6bdceb1 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
176640e49 Bug 57193: Add javadoc @param, @return and @throws tags, or fill  them. Bugzilla Id: 57193
eb3c238dc Bug 57193: Escape &, < and > in javadoc Bugzilla Id: 57193
935ec9c09 Bug 55623 - Invalid/unexpected configuration values should not be silently ignored Bugzilla Id: 55623
cb248782e Bug 54268 - Improve CPU and memory usage Factor out code Bugzilla Id: 54268
a96760645 Bug 53311 - JMeterUtils#runSafe should not throw Error when interrupted
   Bugs after [       2]:
9418f1a3d Bug 60589 - Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules Part 1 of PR #254 Contributed by Woonsan Ko
03a2728d2 Bug 59995 - Allow user to change font size with 2 new menu items and use "jmeter.hidpi.scale.factor" for scaling fonts Contributed by UbikLoadPack Bugzilla Id: 59995

Start block index: 902
End block index: 934
    // TODO probably not needed
    public static Object instantiate(String className, String impls) {
        if (className != null) {
            className = className.trim();
        }

        if (impls != null) {
            impls = impls.trim();
        }

        try {
            Class<?> c = Class.forName(impls);
            try {
                Class<?> o = Class.forName(className);
                Object res = o.newInstance();
                if (c.isInstance(res)) {
                    return res;
                }
                throw new IllegalArgumentException(className + " is not an instance of " + impls);
            } catch (ClassNotFoundException e) {
                log.error("Error loading class " + className + ": class is not found");
            } catch (IllegalAccessException e) {
                log.error("Error loading class " + className + ": does not have access");
            } catch (InstantiationException e) {
                log.error("Error loading class " + className + ": could not instantiate");
            } catch (NoClassDefFoundError e) {
                log.error("Error loading class " + className + ": couldn't find class " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            log.error("Error loading class " + impls + ": was not found.");
        }
        return null;
    }

*********************** Method when SATD was removed **************************

(deprecated, removed)

-    // TODO probably not needed
-    @Deprecated
-    public static Object instantiate(String className, String impls) {
-        if (className != null) {
-            className = className.trim();
-        }
-
-        if (impls != null) {
-            impls = impls.trim();
-        }
-
-        try {
-            Class<?> c = Class.forName(impls);
-            try {
-                Class<?> o = Class.forName(className);
-                Object res = o.newInstance();
-                if (c.isInstance(res)) {
-                    return res;
-                }
-                throw new IllegalArgumentException(className + " is not an instance of " + impls);
-            } catch (ClassNotFoundException e) {
-                log.error("Error loading class " + className + ": class is not found");
-            } catch (IllegalAccessException e) {
-                log.error("Error loading class " + className + ": does not have access");
-            } catch (InstantiationException e) {
-                log.error("Error loading class " + className + ": could not instantiate");
-            } catch (NoClassDefFoundError e) {
-                log.error("Error loading class " + className + ": couldn't find class " + e.getMessage());
-            }
-        } catch (ClassNotFoundException e) {
-            log.error("Error loading class " + impls + ": was not found.");
-        }
-        return null;
-    }
