File path: src/main/org/apache/tools/ant/Project.java
Comment: Should move to a separate public class - and have API to add
Initial commit id: df59903f
Final commit id: fbe839d1
   Bugs between [       0]:

   Bugs after [      24]:
b0d9f8aa0 NPE in Project.setDefault.  PR 49803
11b928d06 Avoid ConcurrentModificationException when iteratong over life-maps.  PR 48310
f00dea8ea fix NPE when logging an exception without message.  PR 47623.
afb49574c Don't use getReference in <isreference> unless really required.  PR 44822.
b8a829b11 make allowNativeBasedir work even when nested, PR 45711.  fixes PR 30569 at the same time.
695ce2ed1 Enhance performance of Project.fireMessageLoggedEvent (PR 45651)
c1142173a new attribute allowNativeBasedir for <ant>.  Not yet documented.  Not yet complete (doesn't work when basedir is a user or inherited property).  Work in progress.  Needed for PR 30569.
a7e2a93ee Remove synchronization around logging of messages in order to avoid potential deadlock - see PR 45194
fa52b460c Add a magic property that lists the targets that have been specified in order to run the current project.  Based on patch by Colm Smyth (just like rev663051 was).  PR 44980
629a2649e Obtain subproject instance from Project to satisfy the remote chance of Project having been subclassed. PR: 17901
2c68e6ab6 Add subproject Executor to Executor interface + its use to Project & Ant task. Move keep-going awareness to Executors. PR: 22901 Submitted by: Alexey Solofnenko Reviewed by: Matt Benson
39868ef36 Properties.propertyNames() should be used instead of .keys(): fix for previous fix - use getProperty() and not get() fix for PropertySet PR: 27261
7e145de4f Properties.propertyNames() should be used instead of .keys(). PR: 27261 Obtained from: Mike Murray
284174e86 Refactored Target invocation into org.apache.tools.ant.Executor implementations. PR: 21421, 29248
c692a67a3 Try to get the dependency analysis right this time while preserving BC. PR: 29977
e0b5c6045 Javadoc fixes for RuntimeConfigurable, Project, Location PR: 30160 Submitted by: Jesse Glick (jglick at netbeans dot org)
c672e8778 Added multiple targets to <ant> and <antcall> using nested <target> elements. PR: 5270, 8148, 17071, 6368, 29623
e4209b284 filterset used by filtertask doesn't respect loglevel PR:  27568 Obtained from: Marcel Schutte
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
6356d19ab better reporting of linkage error in checkTaskClass PR: 26332 Obtained from: Jesse Glick
12383504a References not passed by antcall PR: 21724
b7c2f5f64 Add make's keep-going feature into ANT. PR: 21144 Obtained from: Alexey Solofnenko
7573ef75f Ensure logging message flag is reset Reported By Darin Swanson PR:	21386
987c943d0 Flush output of Java task when finished. Propagate indication of whether line is terminated or not through to project and tasks PR: 16555

Start block index: 2055
End block index: 2073
    // Should move to a separate public class - and have API to add
    // listeners, etc.
    private static class AntRefTable extends Hashtable {
        Project project;
        public AntRefTable(Project project) {
            super();
            this.project=project;
        }

        public Object get(Object key) {
            //System.out.println("AntRefTable.get " + key);
            Object o=super.get(key);
            if( o instanceof UnknownElement ) {
                ((UnknownElement)o).maybeConfigure();
                o=((UnknownElement)o).getTask();
            }
            return o;
        }
    }
