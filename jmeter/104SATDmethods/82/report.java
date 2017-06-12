File path: src/core/org/apache/jmeter/util/JMeterUtils.java
Comment: TODO - does not appear to be called directly
Initial commit id: 96c6175b1
Final commit id: 3392c7314
   Bugs between [      21]:
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
f315102a3 Bug 52783 - oro.patterncache.size property never used due to early init
ea4d5caba Bug 52694 - Deadlock in GUI related to non AWT Threads updating GUI
39299a9dc Bug 52552 - Help reference only works in English
2538970c5 Bug 50170 - Bytes reported by http sampler is after GUnZip Add an optional property to allow change the method to get response size.
4eb5abab7 Bug 37156 - Formatted view of Request in Results Tree
a800c7cd4 Bug 47127 -  Unable to change language to pl_PL
8f3fd0153 Bug 43006 - NPE if icon.properties file not found
b538b19cc Bug 41903 - ViewResultsFullVisualizer : status column looks bad when you do copy and paste
563cd138a Bug 40933, 40945 - optional matching of embedded resource URLs
3a20b51c2 Bug 38250 - allow locale of zh_CN and zh_TW Close properties files after use
32dd63464 Bug 29920 - change default locale if necessary to ensure default properties are picked up when English is chosen.
   Bugs after [       2]:
9418f1a3d Bug 60589 - Migrate LogKit to SLF4J - Drop avalon, logkit and excalibur with backward compatibility for 3rd party modules Part 1 of PR #254 Contributed by Woonsan Ko
03a2728d2 Bug 59995 - Allow user to change font size with 2 new menu items and use "jmeter.hidpi.scale.factor" for scaling fonts Contributed by UbikLoadPack Bugzilla Id: 59995

Start block index: 424
End block index: 443
    //TODO - does not appear to be called directly
    public static Vector getControllers(Properties properties)
    {
        String name = "controller.";
        Vector v = new Vector();
        Enumeration names = properties.keys();
        while (names.hasMoreElements())
        {
            String prop = (String) names.nextElement();
            if (prop.startsWith(name))
            {
                Object o =
                    instantiate(
                        properties.getProperty(prop),
                        "org.apache.jmeter.control.SamplerController");
                v.addElement(o);
            }
        }
        return v;
    }

*********************** Method when SATD was removed **************************

(deprecated, removed)

-    // TODO - does not appear to be called directly
-    @Deprecated
-    public static Vector<Object> getControllers(Properties properties) {
-        String name = "controller."; // $NON-NLS-1$
-        Vector<Object> v = new Vector<>();
-        Enumeration<?> names = properties.keys();
-        while (names.hasMoreElements()) {
-            String prop = (String) names.nextElement();
-            if (prop.startsWith(name)) {
-                Object o = instantiate(properties.getProperty(prop),
-                        "org.apache.jmeter.control.SamplerController"); // $NON-NLS-1$
-                v.addElement(o);
-            }
-        }
-        return v;
-    }
