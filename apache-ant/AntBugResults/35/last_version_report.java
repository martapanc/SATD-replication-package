    private static String[] getProcEnvCommand() {
        if (Os.isFamily("os/2")) {
            // OS/2 - use same mechanism as Windows 2000
            return new String[] {"cmd", "/c", "set"};
        } else if (Os.isFamily("windows")) {
            // Determine if we're running under XP/2000/NT or 98/95
            if (Os.isFamily("win9x")) {
                // Windows 98/95
                return new String[] {"command.com", "/c", "set"};
            } else {
                // Windows XP/2000/NT/2003
                return new String[] {"cmd", "/c", "set"};
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
            return new String[] {"env"};
        } else if (Os.isFamily("openvms")) {
            return new String[] {"show", "logical"};
        } else {
            // MAC OS 9 and previous
            // TODO: I have no idea how to get it, someone must fix it
            return null;
        }
    }
