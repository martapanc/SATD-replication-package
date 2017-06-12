File path: src/core/org/apache/jmeter/threads/ThreadGroup.java
Comment: TODO Is this silent exception intended
Initial commit id: 5115846aa
Final commit id: 31cd377de
   Bugs between [       3]:
e7cdf2296 Bug 60299 - Thread Group with Scheduler : Weird behaviour when End-Time is in the past Bugzilla Id: 60299
7f629396b Bug 57193: Correctly close ol tag in javadoc Bugzilla Id: 57193
534034cb8 Bug 53975 - Variables replacement doesnt work with option "Delay thread creation until needed" Bugzilla Id: 53975
   Bugs after [       3]:
ea7682133 Bug 60564 - Migrating LogKit to SLF4J - core/testelement,threads,util,visualizers package Contributed by Woonsan Ko This closes #273 Bugzilla Id: 60564
463871336 Bug 60530 - Add API to create JMeter threads while test is running Fix tests failure Bugzilla Id: 60530
bd3b94bb5 Bug 60530 - Add API to create JMeter threads while test is running Based on a contribution by Logan Mauzaize & Maxime Chassagneux Bugzilla Id: 60530

Start block index: 495
End block index: 501
    private void pause(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Is this silent exception intended
        }
    }

    *********************** Method when SATD was removed **************************

    private void pause(long ms){
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
