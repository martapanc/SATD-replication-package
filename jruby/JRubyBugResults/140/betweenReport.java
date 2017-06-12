140/report.java
Satd-method: public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
********************************************
********************************************
140/Between/011aa5b87  fixes JRUBY-3907: Time.s diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/025450fd9  fixes JRUBY-3914: In 1.9 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/06778cfaf  Fix for JRUBY-5056: Time diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/0a93732b7  Fix JRUBY-3560: Time.loc diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+        TimeZone tz = TimeZone.getTimeZone(getEnvTimeZone(runtime).toString());
+            
+            tz = TimeZone.getTimeZone(zone);
+        } else {
+            if (LONG_TZNAME.containsKey(zone)) tz.setID(LONG_TZNAME.get(zone.toUpperCase()));
+            tz = TimeZone.getTimeZone(zone);
-        // For JRUBY-2759, when MET choose CET timezone to work around Joda
-        if ("MET".equalsIgnoreCase(zone)) {
-            zone = "CET";
-        }
-
-        DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
+        DateTimeZone dtz = DateTimeZone.forTimeZone(tz);

Lines added: 7. Lines removed: 6. Tot = 13
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(
+        TimeZone tz = TimeZone.getTimeZone(getEnvTimeZone(runtime).toString());
+            tz = TimeZone.getTimeZone(zone);
+            tz = TimeZone.getTimeZone(zone);
-        DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));

Lines added containing method: 3. Lines removed containing method: 1. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/1b919d492  Fixes (and more) for JRU diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/3aadd8a94  Fix by Colin Jones for J diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/6e0d3d549  fixes JRUBY-4166: [1.9]  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/98d7de504  Fix fir JRUBY-2788: Make diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/a3a0d2308  Fix for JRUBY-4784: Lost diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/d5e194d55  Fix by Aurelian Oancea f diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/dcf114683  Fix for JRUBY-5008: [19] diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
140/Between/fe7990091  Fix JRUBY-4842: Time#utc diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getTimeZone(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getTimezoneCache
* matcher
* matches
* forTimeZone
* equalsIgnoreCase
* group
********************************************
********************************************
