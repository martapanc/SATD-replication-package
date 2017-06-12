    public static RubyClass createFileClass(Ruby runtime) {
        ThreadContext context = runtime.getCurrentContext();

        RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);

        runtime.setFile(fileClass);

        fileClass.defineAnnotatedMethods(RubyFile.class);

        fileClass.index = ClassIndex.FILE;
        fileClass.setReifiedClass(RubyFile.class);

        fileClass.kindOf = new RubyModule.KindOf() {
            @Override
            public boolean isKindOf(IRubyObject obj, RubyModule type) {
                return obj instanceof RubyFile;
            }
        };

        // file separator constants
        RubyString separator = runtime.newString("/");
        separator.freeze(context);
        fileClass.defineConstant("SEPARATOR", separator);
        fileClass.defineConstant("Separator", separator);

        if (File.separatorChar == '\\') {
            RubyString altSeparator = runtime.newString("\\");
            altSeparator.freeze(context);
            fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
        } else {
            fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
        }

        // path separator
        RubyString pathSeparator = runtime.newString(File.pathSeparator);
        pathSeparator.freeze(context);
        fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);

        // For JRUBY-5276, physically define FileTest methods on File's singleton
        fileClass.getSingletonClass().defineAnnotatedMethods(RubyFileTest.FileTestFileMethods.class);

        // Create Constants class
        RubyModule constants = fileClass.defineModuleUnder("Constants");

        // open flags
        for (OpenFlags f : OpenFlags.values()) {
            // Strip off the O_ prefix, so they become File::RDONLY, and so on
            final String name = f.name();
            if (name.startsWith("O_")) {
                final String cname = name.substring(2);
                // Special case for handling ACCMODE, since constantine will generate
                // an invalid value if it is not defined by the platform.
                final RubyFixnum cvalue = f == OpenFlags.O_ACCMODE
                        ? runtime.newFixnum(ModeFlags.ACCMODE)
                        : runtime.newFixnum(f.intValue());
                constants.setConstant(cname, cvalue);
            }
        }

        // case handling, escaping, path and dot matching
        constants.setConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
        constants.setConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
        constants.setConstant("FNM_SYSCASE", runtime.newFixnum(FNM_SYSCASE));
        constants.setConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
        constants.setConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));

        // flock operations
        constants.setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
        constants.setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
        constants.setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
        constants.setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));

        // File::Constants module is included in IO.
        runtime.getIO().includeModule(constants);

        return fileClass;
    }
