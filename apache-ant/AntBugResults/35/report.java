File path: src/main/org/apache/tools/ant/taskdefs/Execute.java
Comment: ODO: I have no idea how to get it
Initial commit id: efb4de40
Final commit id: abec7e48
   Bugs between [       9]:
abec7e48e PR 52706: allow command launcher to be selected via a task.  Submitted by Vimil Saju
ff41336fc provide a Map based method to access environment variables and use that.  Don't use System.getenv() on OpenVMS.  PR 49366
c25de7702 Move Process stream closure into a new public static method, closeStreams(Process). PR: 28565
08fc13867 Close process streams in waitFor(Process). PR: 28565
14211b597 I/O-intensive processes hung when started by Execute.spawn() PR: 23893/26852. Submitted by: Daniel Spilker
1b72f9a64 Add Windows 2003 support to getProcEnvCommand() + minor refactoring. PR:  28067 Submitted by:	Irene Rusman
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
8e7167b58 The user.dir trick doesn't seem to work on OS X anymore, PR 23400
3396e7c32 Remove direct call to deprecated project, location, tasktype Task field, replaced by an accessor way into tasks. Remove too some unused variable declaration and some unused imports. PR: 22515 Submitted by: Emmanuel Feller ( Emmanuel dot Feller at free dot fr)
   Bugs after [       1]:
43844a7e6 PR 56748 Spelling fixes, submitted by Ville Skyttä

Start block index: 235
End block index: 278
    private static String[] getProcEnvCommand() {
        if (Os.isFamily("os/2")) {
            // OS/2 - use same mechanism as Windows 2000
            String[] cmd = {"cmd", "/c", "set" };
            return cmd;
        } else if (Os.isFamily("windows")) {
            // Determine if we're running under XP/2000/NT or 98/95
            if (!Os.isFamily("win9x")) {
                // Windows XP/2000/NT
                String[] cmd = {"cmd", "/c", "set" };
                return cmd;
            } else {
                // Windows 98/95
                String[] cmd = {"command.com", "/c", "set" };
                return cmd;
            }
        } else if (Os.isFamily("z/os") || Os.isFamily("unix")) {
            // On most systems one could use: /bin/sh -c env

            // Some systems have /bin/env, others /usr/bin/env, just try
            String[] cmd = new String[1];
            if (new File("/bin/env").canRead()) {
                cmd[0] = "/bin/env";
            } else if (new File("/usr/bin/env").canRead()) {
                cmd[0] = "/usr/bin/env";
            } else {
                // rely on PATH
                cmd[0] = "env";
            }
            return cmd;
        } else if (Os.isFamily("netware") || Os.isFamily("os/400")) {
            // rely on PATH
            String[] cmd = {"env"};
            return cmd;
        } else if (Os.isFamily("openvms")) {
            String[] cmd = {"show", "logical"};
            return cmd;
        } else {
            // MAC OS 9 and previous
            //TODO: I have no idea how to get it, someone must fix it
            String[] cmd = null;
            return cmd;
        }
    }
