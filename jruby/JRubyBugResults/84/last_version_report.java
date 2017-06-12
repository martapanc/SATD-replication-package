public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
    Ruby runtime = context.getRuntime();

    // This is a fairly specific hack for empty string, but it does the job
    if (args.length == 1 && args[0].convertToString().isEmpty()) {
        throw runtime.newErrnoENOENTError(args[0].convertToString().toString());
    }

    int resultCode;
    boolean nativeFailed = false;
    try {
        try {
            String[] argv = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                argv[i] = args[i].asJavaString();
            }
            resultCode = runtime.getPosix().exec(null, argv);
            // Only here because native exec could not exec (always -1)
            nativeFailed = true;
        } catch (RaiseException e) {  // Not implemented error
            // Fall back onto our existing code if native not available
            // FIXME: Make jnr-posix Pure-Java backend do this as well
            resultCode = ShellLauncher.execAndWait(runtime, args);
        }
    } catch (RaiseException e) {
        throw e; // no need to wrap this exception
    } catch (Exception e) {
        throw runtime.newErrnoENOENTError("cannot execute");
    }

    if (nativeFailed) throw runtime.newErrnoENOENTError("cannot execute");

    exit(runtime, new IRubyObject[] {runtime.newFixnum(resultCode)}, true);

    // not reached
    return runtime.getNil();
}
