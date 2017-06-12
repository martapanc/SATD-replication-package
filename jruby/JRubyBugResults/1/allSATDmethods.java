SATD id: 1		Size: 84
    public Operand buildCase(CaseNode caseNode, IRScope m) {
        // get the incoming case value
        Operand value = build(caseNode.getCaseNode(), m);

        // the CASE instruction
        Label endLabel = m.getNewLabel();
        Variable result = m.getNewTemporaryVariable();
        CASE_Instr caseInstr = new CASE_Instr(result, value, endLabel);
        m.addInstr(caseInstr);

        // lists to aggregate variables and bodies for whens
        List<Operand> variables = new ArrayList<Operand>();
        List<Label> labels = new ArrayList<Label>();

        Map<Label, Node> bodies = new HashMap<Label, Node>();

        // build each "when"
        for (Node aCase : caseNode.getCases().childNodes()) {
            WhenNode whenNode = (WhenNode)aCase;
            Label bodyLabel = m.getNewLabel();

            if (whenNode.getExpressionNodes() instanceof ListNode) {
                // multiple conditions for when
                for (Node expression : ((ListNode)whenNode.getExpressionNodes()).childNodes()) {
                    Variable eqqResult = m.getNewTemporaryVariable();

                    variables.add(eqqResult);
                    labels.add(bodyLabel);
                    
                    m.addInstr(new EQQ_Instr(eqqResult, build(expression, m), value));
                    m.addInstr(new BEQInstr(eqqResult, BooleanLiteral.TRUE, bodyLabel));
                }
            } else {
                Variable eqqResult = m.getNewTemporaryVariable();

                variables.add(eqqResult);
                labels.add(bodyLabel);

                m.addInstr(new EQQ_Instr(eqqResult, build(whenNode.getExpressionNodes(), m), value));
                m.addInstr(new BEQInstr(eqqResult, BooleanLiteral.TRUE, bodyLabel));
            }

            // SSS FIXME: This doesn't preserve original order of when clauses.  We could consider
            // preserving the order (or maybe not, since we would have to sort the constants first
            // in any case) for outputing jump tables in certain situations.
            //
            // add body to map for emitting later
            bodies.put(bodyLabel, whenNode.getBodyNode());
        }

        // build "else" if it exists
        if (caseNode.getElseNode() != null) {
            Label elseLbl = m.getNewLabel();
            caseInstr.setElse(elseLbl);

            bodies.put(elseLbl, caseNode.getElseNode());
        }

        // now emit bodies
        for (Map.Entry<Label, Node> entry : bodies.entrySet()) {
            m.addInstr(new LABEL_Instr(entry.getKey()));
            Operand bodyValue = build(entry.getValue(), m);
            // Local optimization of break results (followed by a copy & jump) to short-circuit the jump right away
            // rather than wait to do it during an optimization pass when a dead jump needs to be removed.
            Label tgt = endLabel;
            if (bodyValue instanceof BreakResult) {
                BreakResult br = (BreakResult)bodyValue;
                bodyValue = br._result;
                tgt = br._jumpTarget;
            }
            m.addInstr(new CopyInstr(result, bodyValue));
            m.addInstr(new JumpInstr(tgt));
        }

        // close it out
        m.addInstr(new LABEL_Instr(endLabel));
        caseInstr.setLabels(labels);
        caseInstr.setVariables(variables);

        // CON FIXME: I don't know how to make case be an expression...does that
        // logic need to go here?

        return result;
    }
*****************************************
*****************************************
SATD id: 10		Size: 17
    public static RubyClass createJavaClassClass(Ruby runtime, RubyModule javaModule) {
        // FIXME: Determine if a real allocator is needed here. Do people want to extend
        // JavaClass? Do we want them to do that? Can you Class.new(JavaClass)? Should
        // you be able to?
        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
        // this type and it can't be marshalled. Confirm. JRUBY-415
        RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.getClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
        
        result.includeModule(runtime.getModule("Comparable"));
        
        result.defineAnnotatedMethods(ClassJavaAddons.class);

        result.getMetaClass().undefineMethod("new");
        result.getMetaClass().undefineMethod("allocate");

        return result;
    }
*****************************************
*****************************************
SATD id: 100		Size: 16

    // TODO: Only used by interface implementation; eliminate it
    public static IRubyObject invokeMethodMissing(IRubyObject receiver, String name, IRubyObject[] args) {
        ThreadContext context = receiver.getRuntime().getCurrentContext();

        // store call information so method_missing impl can use it
        context.setLastCallStatusAndVisibility(CallType.FUNCTIONAL, Visibility.PUBLIC);

        if (name == "method_missing") {
            return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
        }

        IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);

        return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
    }
*****************************************
*****************************************
SATD id: 101		Size: 0
*****************************************
*****************************************
SATD id: 102		Size: 0
*****************************************
*****************************************
SATD id: 103		Size: 9
            // TODO: public only?
            initializeClass(javaClass);
        }
    }
    
    private void initializeInterface(Class javaClass) {
        Class superclass = javaClass.getSuperclass();
        Map staticNames;
        if (superclass == null) {
*****************************************
*****************************************
SATD id: 104		Size: 24
    // TODO: remove maxArgs - hits an assertion if maxArgs is removed - trying argumentsAsArray = true (CS)
    @CoreMethod(names = "execute", isModuleFunction = true, needsSelf = false, required = 1, argumentsAsArray = true)
    public abstract static class ExecuteNode extends CoreMethodNode {

        @Child private ForeignObjectAccessNode node;

        public ExecuteNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }

        public ExecuteNode(ExecuteNode prev) {
            this(prev.getContext(), prev.getSourceSection());
        }

        @Specialization
        public Object executeForeign(VirtualFrame frame, TruffleObject receiver, Object[] arguments) {
            if (node == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                this.node = ForeignObjectAccessNode.getAccess(Execute.create(Receiver.create(), arguments.length));
            }
            return node.executeForeign(frame, receiver, arguments);
        }

    }
*****************************************
*****************************************
SATD id: 105		Size: 10
    public ModeFlags(long flags) throws InvalidValueException {
    	// TODO: Ruby does not seem to care about invalid numeric mode values
    	// I am not sure if ruby overflows here also...
        this.flags = (int)flags;
        
        if (isReadOnly() && ((flags & APPEND) != 0)) {
            // MRI 1.8 behavior: this combination of flags is not allowed
            throw new InvalidValueException();
        }
    }
*****************************************
*****************************************
SATD id: 106		Size: 0
*****************************************
*****************************************
SATD id: 107		Size: 118
    public static RubyClass createFileClass(Ruby runtime) {
        RubyClass fileClass = runtime.defineClass("File", runtime.getClass("IO"), FILE_ALLOCATOR);
        CallbackFactory callbackFactory = runtime.callbackFactory(RubyFile.class);   
        RubyClass fileMetaClass = fileClass.getMetaClass();
        RubyString separator = runtime.newString("/");
        
        separator.freeze();
        fileClass.defineConstant("SEPARATOR", separator);
        fileClass.defineConstant("Separator", separator);
        
        RubyString altSeparator = runtime.newString(File.separatorChar == '/' ? "\\" : "/");
        altSeparator.freeze();
        fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
        
        RubyString pathSeparator = runtime.newString(File.pathSeparator);
        pathSeparator.freeze();
        fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
        
        // TODO: These were missing, so we're not handling them elsewhere?
        // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
        // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
        fileClass.setConstant("BINARY", runtime.newFixnum(IOModes.BINARY));
        fileClass.setConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
        fileClass.setConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
        fileClass.setConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
        fileClass.setConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
        
        // Create constants for open flags
        fileClass.setConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
        fileClass.setConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
        fileClass.setConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
        fileClass.setConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
        fileClass.setConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
        fileClass.setConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
        fileClass.setConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
        fileClass.setConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
        fileClass.setConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
        
        // Create constants for flock
        fileClass.setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
        fileClass.setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
        fileClass.setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
        fileClass.setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
        
        // Create Constants class
        RubyModule constants = fileClass.defineModuleUnder("Constants");
        
        // TODO: These were missing, so we're not handling them elsewhere?
        constants.setConstant("BINARY", runtime.newFixnum(32768));
        constants.setConstant("FNM_NOESCAPE", runtime.newFixnum(1));
        constants.setConstant("FNM_CASEFOLD", runtime.newFixnum(8));
        constants.setConstant("FNM_DOTMATCH", runtime.newFixnum(4));
        constants.setConstant("FNM_PATHNAME", runtime.newFixnum(2));
        
        // Create constants for open flags
        constants.setConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
        constants.setConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
        constants.setConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
        constants.setConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
        constants.setConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
        constants.setConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
        constants.setConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
        constants.setConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
        constants.setConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
        
        // Create constants for flock
        constants.setConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
        constants.setConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
        constants.setConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
        constants.setConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
        
        // TODO Singleton methods: atime, blockdev?, chardev?, chown, directory?
        // TODO Singleton methods: executable?, executable_real?,
        // TODO Singleton methods: ftype, grpowned?, lchmod, lchown, link, mtime, owned?
        // TODO Singleton methods: pipe?, readlink, setgid?, setuid?, socket?,
        // TODO Singleton methods: stat, sticky?, symlink, symlink?, umask, utime
        
        runtime.getModule("FileTest").extend_object(fileClass);
        
        fileMetaClass.defineFastMethod("basename", callbackFactory.getFastOptSingletonMethod("basename"));
        fileMetaClass.defineFastMethod("chmod", callbackFactory.getFastOptSingletonMethod("chmod"));
        fileMetaClass.defineFastMethod("chown", callbackFactory.getFastOptSingletonMethod("chown"));
        fileMetaClass.defineFastMethod("delete", callbackFactory.getFastOptSingletonMethod("unlink"));
        fileMetaClass.defineFastMethod("dirname", callbackFactory.getFastSingletonMethod("dirname", IRubyObject.class));
        fileMetaClass.defineFastMethod("expand_path", callbackFactory.getFastOptSingletonMethod("expand_path"));
        fileMetaClass.defineFastMethod("extname", callbackFactory.getFastSingletonMethod("extname", IRubyObject.class));
        fileMetaClass.defineFastMethod("fnmatch", callbackFactory.getFastOptSingletonMethod("fnmatch"));
        fileMetaClass.defineFastMethod("fnmatch?", callbackFactory.getFastOptSingletonMethod("fnmatch"));
        fileMetaClass.defineFastMethod("join", callbackFactory.getFastOptSingletonMethod("join"));
        fileMetaClass.defineFastMethod("lstat", callbackFactory.getFastSingletonMethod("lstat", IRubyObject.class));
        fileMetaClass.defineFastMethod("mtime", callbackFactory.getFastSingletonMethod("mtime", IRubyObject.class));
        fileMetaClass.defineFastMethod("ctime", callbackFactory.getFastSingletonMethod("ctime", IRubyObject.class));
        fileMetaClass.defineMethod("open", callbackFactory.getOptSingletonMethod("open"));
        fileMetaClass.defineFastMethod("rename", callbackFactory.getFastSingletonMethod("rename", IRubyObject.class, IRubyObject.class));
        fileMetaClass.defineFastMethod("size?", callbackFactory.getFastSingletonMethod("size_p", IRubyObject.class));
        fileMetaClass.defineFastMethod("split", callbackFactory.getFastSingletonMethod("split", IRubyObject.class));
        fileMetaClass.defineFastMethod("stat", callbackFactory.getFastSingletonMethod("lstat", IRubyObject.class));
        fileMetaClass.defineFastMethod("symlink", callbackFactory.getFastSingletonMethod("symlink", IRubyObject.class, IRubyObject.class));
        fileMetaClass.defineFastMethod("symlink?", callbackFactory.getFastSingletonMethod("symlink_p", IRubyObject.class));
        fileMetaClass.defineFastMethod("truncate", callbackFactory.getFastSingletonMethod("truncate", IRubyObject.class, IRubyObject.class));
        fileMetaClass.defineFastMethod("utime", callbackFactory.getFastOptSingletonMethod("utime"));
        fileMetaClass.defineFastMethod("unlink", callbackFactory.getFastOptSingletonMethod("unlink"));
        
        // TODO: Define instance methods: atime, chmod, chown, lchmod, lchown, lstat, mtime
        //defineMethod("flock", Arity.singleArgument());
        fileClass.defineFastMethod("chmod", callbackFactory.getFastMethod("chmod", IRubyObject.class));
        fileClass.defineFastMethod("chown", callbackFactory.getFastMethod("chown", IRubyObject.class));
        fileClass.defineFastMethod("ctime", callbackFactory.getFastMethod("ctime"));
        fileClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
        fileClass.defineFastMethod("path", callbackFactory.getFastMethod("path"));
        fileClass.defineFastMethod("stat", callbackFactory.getFastMethod("stat"));
        fileClass.defineFastMethod("truncate", callbackFactory.getFastMethod("truncate", IRubyObject.class));
        fileClass.defineFastMethod("flock", callbackFactory.getFastMethod("flock", IRubyObject.class));
        
        RubyFileStat.createFileStatClass(runtime);
        
        return fileClass;
    }
*****************************************
*****************************************
SATD id: 108		Size: 47
    public void callMethodMissingIfNecessary(SkinnyMethodAdapter mv, Label afterCall, Label okCall) {
        Label methodMissing = new Label();

        // if undefined, branch to method_missing
        mv.dup();
        mv.invokevirtual(p(DynamicMethod.class), "isUndefined", sig(boolean.class));
        mv.ifne(methodMissing);

        // if we're not attempting to invoke method_missing and method is not visible, branch to method_missing
        mv.aload(DISPATCHER_NAME_INDEX);
        mv.ldc("method_missing");
        // if it's method_missing, just invoke it
        mv.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
        mv.ifne(okCall);
        // check visibility
        mv.dup(); // dup method
        mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
        mv.invokevirtual(p(ThreadContext.class), "getFrameSelf", sig(IRubyObject.class));
        mv.aload(DISPATCHER_CALLTYPE_INDEX);
        mv.invokevirtual(p(DynamicMethod.class), "isCallableFrom", sig(boolean.class, params(IRubyObject.class, CallType.class)));
        mv.ifne(okCall);

        // invoke callMethodMissing
        mv.label(methodMissing);

        mv.aload(DISPATCHER_THREADCONTEXT_INDEX); // tc
        mv.swap(); // under method
        mv.aload(DISPATCHER_SELF_INDEX); // self
        mv.swap(); // under method
        mv.aload(DISPATCHER_NAME_INDEX); // name
        mv.aload(DISPATCHER_ARGS_INDEX); // args

        // caller
        mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
        mv.invokevirtual(p(ThreadContext.class), "getFrameSelf", sig(IRubyObject.class));

        mv.aload(DISPATCHER_CALLTYPE_INDEX); // calltype
        mv.aload(DISPATCHER_BLOCK_INDEX); // block

        // invoke callMethodMissing method directly
        // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
        mv.invokestatic(p(RuntimeHelpers.class), "callMethodMissing", sig(IRubyObject.class, 
                params(ThreadContext.class, IRubyObject.class, DynamicMethod.class, String.class, 
                                    IRubyObject[].class, IRubyObject.class, CallType.class, Block.class)));
        // if no exception raised, jump to end to leave result on stack for return
        mv.go_to(afterCall);
    }
*****************************************
*****************************************
SATD id: 109		Size: 26
    public void buildYield(Node node, IR_BuilderContext m, boolean expr) {
        final YieldNode yieldNode = (YieldNode) node;

        ArgumentsCallback argsCallback = getArgsCallback(yieldNode.getArgsNode());

        // TODO: This filtering is kind of gross...it would be nice to get some parser help here
        if (argsCallback == null || argsCallback.getArity() == 0) {
            m.getInvocationCompiler().yieldSpecific(argsCallback);
        } else if ((argsCallback.getArity() == 1 || argsCallback.getArity() == 2 || argsCallback.getArity() == 3) && yieldNode.getExpandArguments()) {
            // send it along as arity-specific, we don't need the array
            m.getInvocationCompiler().yieldSpecific(argsCallback);
        } else {
            CompilerCallback argsCallback2 = null;
            if (yieldNode.getArgsNode() != null) {
                argsCallback2 = new CompilerCallback() {
                    public void call(IR_BuilderContext m) {
                        build(yieldNode.getArgsNode(), m,true);
                    }
                };
            }

            m.getInvocationCompiler().yield(argsCallback2, yieldNode.getExpandArguments());
        }
        // TODO: don't require pop
        if (!expr) m.consumeCurrentValue();
    }
*****************************************
*****************************************
SATD id: 11		Size: 0
*****************************************
*****************************************
SATD id: 110		Size: 6
    public StaticScope getStaticScope() {
        // TODO: This is actually now returning the scope of whoever called Method#to_proc
        // which is obviously wrong; but there's no scope to provide for many methods.
        // It fixes JRUBY-2237, but needs a better solution.
        return staticScope;
    }
*****************************************
*****************************************
SATD id: 111		Size: 0
*****************************************
*****************************************
SATD id: 112		Size: 10
    // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
	private IOModes getModes(IRubyObject object) {
		if (object instanceof RubyString) {
			return new IOModes(getRuntime(), ((RubyString)object).getValue());
		} else if (object instanceof RubyFixnum) {
			return new IOModes(getRuntime(), ((RubyFixnum)object).getLongValue());
		}

		throw getRuntime().newTypeError("Invalid type for modes");
	}
*****************************************
*****************************************
SATD id: 113		Size: 33
    public static Errno errnoFromException(Throwable t) {
        if (t instanceof ClosedChannelException) {
            return Errno.EBADF;
        }

        // TODO: this is kinda gross
        if(t.getMessage() != null) {
            String errorMessage = t.getMessage();

            // All errors to sysread should be SystemCallErrors, but on a closed stream
            // Ruby returns an IOError.  Java throws same exception for all errors so
            // we resort to this hack...

            if ("Bad file descriptor".equals(errorMessage)) {
                return Errno.EBADF;
            } else if ("File not open".equals(errorMessage)) {
                return null;
            } else if ("An established connection was aborted by the software in your host machine".equals(errorMessage)) {
                return Errno.ECONNABORTED;
            } else if (t.getMessage().equals("Broken pipe")) {
                return Errno.EPIPE;
            } else if ("Connection reset by peer".equals(errorMessage) ||
                       "An existing connection was forcibly closed by the remote host".equals(errorMessage) ||
                    (Platform.IS_WINDOWS && errorMessage.contains("connection was aborted"))) {
                return Errno.ECONNRESET;
            } else if (errorMessage.equals("No space left on device")) {
                return Errno.ENOSPC;
            } else if (errorMessage.equals("Too many open files")) {
                return Errno.EMFILE;
            }
        }
        return null;
    }
*****************************************
*****************************************
SATD id: 114		Size: 5
    public Callback getBlockMethod(String method) {
        // TODO: This is probably BAD...
        return new ReflectionCallback(type, method, new Class[] { RubyKernel.IRUBY_OBJECT,
                RubyKernel.IRUBY_OBJECT }, false, true, Arity.fixed(2), false);
    }
*****************************************
*****************************************
SATD id: 115		Size: 22
    private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
        if (frame != previousFrame && // happens with native exceptions, should not filter those out
                frame.getLine() == previousFrame.getLine() &&
                frame.getName() != null && 
                frame.getName().equals(previousFrame.getName()) &&
                frame.getFile().equals(previousFrame.getFile())) {
            return;
        }
        
        RubyString traceLine;
        if (previousFrame.getName() != null) {
            traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `" + previousFrame.getName() + '\'');
        } else if (runtime.is1_9()) {
            // TODO: This probably isn't the best hack, but it works until we can have different
            // root frame setup for 1.9 easily.
            traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `<main>'");
        } else {
            traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1));
        }
        
        backtrace.append(traceLine);
    }
*****************************************
*****************************************
SATD id: 116		Size: 0
*****************************************
*****************************************
SATD id: 117		Size: 95
public static RubyClass createFileClass(Ruby runtime) {
    RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
    runtime.setFile(fileClass);
    RubyString separator = runtime.newString("/");

    fileClass.kindOf = new RubyModule.KindOf() {
            public boolean isKindOf(IRubyObject obj, RubyModule type) {
                return obj instanceof RubyFile;
            }
        };

    separator.freeze();
    fileClass.defineConstant("SEPARATOR", separator);
    fileClass.defineConstant("Separator", separator);

    if (File.separatorChar == '\\') {
        RubyString altSeparator = runtime.newString("\\");
        altSeparator.freeze();
        fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
    } else {
        fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
    }

    RubyString pathSeparator = runtime.newString(File.pathSeparator);
    pathSeparator.freeze();
    fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);

    // TODO: why are we duplicating the constants here, and then in
    // File::Constants below? File::Constants is included in IO.

    // TODO: These were missing, so we're not handling them elsewhere?
    // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
    // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
    fileClass.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
    fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
    fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
    fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
    fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
    fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));

    // Create constants for open flags
    fileClass.fastSetConstant("RDONLY", runtime.newFixnum(ModeFlags.RDONLY));
    fileClass.fastSetConstant("WRONLY", runtime.newFixnum(ModeFlags.WRONLY));
    fileClass.fastSetConstant("RDWR", runtime.newFixnum(ModeFlags.RDWR));
    fileClass.fastSetConstant("CREAT", runtime.newFixnum(ModeFlags.CREAT));
    fileClass.fastSetConstant("EXCL", runtime.newFixnum(ModeFlags.EXCL));
    fileClass.fastSetConstant("NOCTTY", runtime.newFixnum(ModeFlags.NOCTTY));
    fileClass.fastSetConstant("TRUNC", runtime.newFixnum(ModeFlags.TRUNC));
    fileClass.fastSetConstant("APPEND", runtime.newFixnum(ModeFlags.APPEND));
    fileClass.fastSetConstant("NONBLOCK", runtime.newFixnum(ModeFlags.NONBLOCK));

    // Create constants for flock
    fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
    fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
    fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
    fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));

    // Create Constants class
    RubyModule constants = fileClass.defineModuleUnder("Constants");

    // TODO: These were missing, so we're not handling them elsewhere?
    constants.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
    constants.fastSetConstant("SYNC", runtime.newFixnum(0x1000));
    constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
    constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
    constants.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
    constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
    constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));

    // Create constants for open flags
    constants.fastSetConstant("RDONLY", runtime.newFixnum(ModeFlags.RDONLY));
    constants.fastSetConstant("WRONLY", runtime.newFixnum(ModeFlags.WRONLY));
    constants.fastSetConstant("RDWR", runtime.newFixnum(ModeFlags.RDWR));
    constants.fastSetConstant("CREAT", runtime.newFixnum(ModeFlags.CREAT));
    constants.fastSetConstant("EXCL", runtime.newFixnum(ModeFlags.EXCL));
    constants.fastSetConstant("NOCTTY", runtime.newFixnum(ModeFlags.NOCTTY));
    constants.fastSetConstant("TRUNC", runtime.newFixnum(ModeFlags.TRUNC));
    constants.fastSetConstant("APPEND", runtime.newFixnum(ModeFlags.APPEND));
    constants.fastSetConstant("NONBLOCK", runtime.newFixnum(ModeFlags.NONBLOCK));

    // Create constants for flock
    constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
    constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
    constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
    constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));

    // File::Constants module is included in IO.
    runtime.getIO().includeModule(constants);

    runtime.getFileTest().extend_object(fileClass);

    fileClass.defineAnnotatedMethods(RubyFile.class);

    return fileClass;
}
*****************************************
*****************************************
SATD id: 118		Size: 14
    public IRubyObject setConstantInCurrent(String name, IRubyObject result) {
        RubyModule module;

        // FIXME: why do we check RubyClass and then use CRef?
        if (getRubyClass() == null) {
            // TODO: wire into new exception handling mechanism
            throw runtime.newTypeError("no class/module to define constant");
        }
        module = (RubyModule) peekCRef().getValue();
   
        setConstantInModule(name, module, result);
   
        return result;
    }
*****************************************
*****************************************
SATD id: 119		Size: 16
                    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                        // TODO: WRONG - get interfaces from class
                        if (arg.respondsTo("java_object")) {
                            IRubyObject interfaces = self.getMetaClass().getInstanceVariables().fastGetInstanceVariable("@java_interfaces");
                            assert interfaces instanceof RubyArray : "interface list was not an array";

                            return context.getRuntime().newBoolean(((RubyArray)interfaces)
                                    .op_diff(
                                        ((JavaClass)
                                            ((JavaObject)arg.dataGetStruct()).java_class()
                                        ).interfaces()
                                    ).equals(RubyArray.newArray(context.getRuntime())));
                        } else {
                            return RuntimeHelpers.invoke(context, self, "old_eqq", arg);
                        }
                    }
*****************************************
*****************************************
SATD id: 12		Size: 639
    public IRubyObject read(IRubyObject[] args) {
        int argCount = args.length;
        
        OpenFile myOpenFile = getOpenFileChecked();
        
        if (argCount == 0 || args[0].isNil()) {
            try {
                myOpenFile.checkReadable(getRuntime());

                if (args.length == 2) {
                    return readAll(args[1]);
                } else {
                    return readAll(getRuntime().getNil());
                }
            } catch (PipeException ex) {
                throw getRuntime().newErrnoEPIPEError();
            } catch (InvalidValueException ex) {
                throw getRuntime().newErrnoEINVALError();
            } catch (EOFException ex) {
                throw getRuntime().newEOFError();
            } catch (IOException ex) {
                throw getRuntime().newIOErrorFromException(ex);
            } catch (BadDescriptorException ex) {
                throw getRuntime().newErrnoEBADFError();
            }
        }
        
        int length = RubyNumeric.num2int(args[0]);
        
        if (length < 0) {
            throw getRuntime().newArgumentError("negative length " + length + " given");
        }
        
        RubyString str = null;
//        ByteList buffer = null;
        if (args.length == 1 || args[1].isNil()) {
//            buffer = new ByteList(length);
//            str = RubyString.newString(getRuntime(), buffer);
        } else {
            str = args[1].convertToString();
            str.modify(length);

            if (length == 0) {
                return str;
            }

//            buffer = str.getByteList();
        }

        try {
            myOpenFile.checkReadable(getRuntime());

            if (myOpenFile.getMainStream().feof()) {
                return getRuntime().getNil();
            }

            // TODO: Ruby locks the string here

            // READ_CHECK from MRI io.c
            readCheck(myOpenFile.getMainStream());

            // TODO: check buffer length again?
    //        if (RSTRING(str)->len != len) {
    //            rb_raise(rb_eRuntimeError, "buffer string modified");
    //        }

            // TODO: read into buffer using all the fread logic
    //        int read = openFile.getMainStream().fread(buffer);
            ByteList newBuffer = myOpenFile.getMainStream().fread(length);

            // TODO: Ruby unlocks the string here

            // TODO: change this to check number read into buffer once that's working
    //        if (read == 0) {
            
            if (newBuffer == null || newBuffer.length() == 0) {
                if (myOpenFile.getMainStream() == null) {
                    return getRuntime().getNil();
                }

                if (myOpenFile.getMainStream().feof()) {
                    // truncate buffer string to zero, if provided
                    if (str != null) {
                        str.setValue(ByteList.EMPTY_BYTELIST.dup());
                    }
                
                    return getRuntime().getNil();
                }

                if (length > 0) {
                    // I think this is only partly correct; sys fail based on errno in Ruby
                    throw getRuntime().newEOFError();
                }
            }


            // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?

            // FIXME: I don't like the null checks here
            if (str == null) {
                if (newBuffer == null) {
                    str = getRuntime().newString();
                } else {
                    str = RubyString.newString(getRuntime(), newBuffer);
                }
            } else {
                if (newBuffer == null) {
                    str.setValue(ByteList.EMPTY_BYTELIST.dup());
                } else {
                    str.setValue(newBuffer);
                }
            }
            str.setTaint(true);

            return str;
        } catch (EOFException ex) {
            throw getRuntime().newEOFError();
        } catch (PipeException ex) {
            throw getRuntime().newErrnoEPIPEError();
        } catch (InvalidValueException ex) {
            throw getRuntime().newErrnoEINVALError();
        } catch (IOException ex) {
            throw getRuntime().newIOErrorFromException(ex);
        } catch (BadDescriptorException ex) {
            throw getRuntime().newErrnoEBADFError();
        }
    }
    
    protected IRubyObject readAll(IRubyObject buffer) throws BadDescriptorException, EOFException, IOException {
        // TODO: handle writing into original buffer better
        
        RubyString str = null;
        if (buffer instanceof RubyString) {
            str = (RubyString)buffer;
        }
        
        // TODO: ruby locks the string here
        
        // READ_CHECK from MRI io.c
        if (openFile.getMainStream().readDataBuffered()) {
            openFile.checkClosed(getRuntime());
        }
        
        ByteList newBuffer = openFile.getMainStream().readall();

        // TODO same zero-length checks as file above

        if (str == null) {
            if (newBuffer == null) {
                str = RubyString.newStringShared(getRuntime(), ByteList.EMPTY_BYTELIST);
            } else {
                str = RubyString.newString(getRuntime(), newBuffer);
            }
        } else {
            if (newBuffer == null) {
                str.setValue(ByteList.EMPTY_BYTELIST.dup());
            } else {
                str.setValue(newBuffer);
            }
        }

        str.taint();

        return str;
//        long bytes = 0;
//        long n;
//
//        if (siz == 0) siz = BUFSIZ;
//        if (NIL_P(str)) {
//            str = rb_str_new(0, siz);
//        }
//        else {
//            rb_str_resize(str, siz);
//        }
//        for (;;) {
//            rb_str_locktmp(str);
//            READ_CHECK(fptr->f);
//            n = io_fread(RSTRING(str)->ptr+bytes, siz-bytes, fptr);
//            rb_str_unlocktmp(str);
//            if (n == 0 && bytes == 0) {
//                if (!fptr->f) break;
//                if (feof(fptr->f)) break;
//                if (!ferror(fptr->f)) break;
//                rb_sys_fail(fptr->path);
//            }
//            bytes += n;
//            if (bytes < siz) break;
//            siz += BUFSIZ;
//            rb_str_resize(str, siz);
//        }
//        if (bytes != siz) rb_str_resize(str, bytes);
//        OBJ_TAINT(str);
//
//        return str;
    }
    
    // TODO: There's a lot of complexity here due to error handling and
    // nonblocking IO; much of this goes away, but for now I'm just
    // having read call ChannelStream.fread directly.
//    protected int fread(int len, ByteList buffer) {
//        long n = len;
//        int c;
//        int saved_errno;
//
//        while (n > 0) {
//            c = read_buffered_data(ptr, n, fptr->f);
//            if (c < 0) goto eof;
//            if (c > 0) {
//                ptr += c;
//                if ((n -= c) <= 0) break;
//            }
//            rb_thread_wait_fd(fileno(fptr->f));
//            rb_io_check_closed(fptr);
//            clearerr(fptr->f);
//            TRAP_BEG;
//            c = getc(fptr->f);
//            TRAP_END;
//            if (c == EOF) {
//              eof:
//                if (ferror(fptr->f)) {
//                    switch (errno) {
//                      case EINTR:
//    #if defined(ERESTART)
//                      case ERESTART:
//    #endif
//                        clearerr(fptr->f);
//                        continue;
//                      case EAGAIN:
//    #if defined(EWOULDBLOCK) && EWOULDBLOCK != EAGAIN
//                      case EWOULDBLOCK:
//    #endif
//                        if (len > n) {
//                            clearerr(fptr->f);
//                        }
//                        saved_errno = errno;
//                        rb_warning("nonblocking IO#read is obsolete; use IO#readpartial or IO#sysread");
//                        errno = saved_errno;
//                    }
//                    if (len == n) return 0;
//                }
//                break;
//            }
//            *ptr++ = c;
//            n--;
//        }
//        return len - n;
//        
//    }

    /** Read a byte. On EOF throw EOFError.
     * 
     */
    @JRubyMethod(name = "readchar")
    public IRubyObject readchar() {
        IRubyObject c = getc();
        
        if (c.isNil()) throw getRuntime().newEOFError();
        
        return c;
    }
    
    @JRubyMethod
    public IRubyObject stat() {
        return getRuntime().newFileStat(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
    }

    /** 
     * <p>Invoke a block for each byte.</p>
     */
    @JRubyMethod(name = "each_byte", frame = true)
    public IRubyObject each_byte(Block block) {
    	try {
            Ruby runtime = getRuntime();
            ThreadContext context = runtime.getCurrentContext();
            OpenFile myOpenFile = getOpenFileChecked();
            
            while (true) {
                myOpenFile.checkReadable(runtime);
                
                // TODO: READ_CHECK from MRI
                
                int c = myOpenFile.getMainStream().fgetc();
                
                if (c == -1) {
                    // TODO: check for error, clear it, and wait until readable before trying once more
//                    if (ferror(f)) {
//                        clearerr(f);
//                        if (!rb_io_wait_readable(fileno(f)))
//                            rb_sys_fail(fptr->path);
//                        continue;
//                    }
                    break;
                }
                
                assert c < 256;
                block.yield(context, getRuntime().newFixnum(c));
            }

            // TODO: one more check for error
//            if (ferror(f)) rb_sys_fail(fptr->path);
            return this;
        } catch (PipeException ex) {
            throw getRuntime().newErrnoEPIPEError();
        } catch (InvalidValueException ex) {
            throw getRuntime().newErrnoEINVALError();
        } catch (BadDescriptorException e) {
            throw getRuntime().newErrnoEBADFError();
        } catch (EOFException e) {
            return getRuntime().getNil();
    	} catch (IOException e) {
    	    throw getRuntime().newIOError(e.getMessage());
        }
    }

    /** 
     * <p>Invoke a block for each line.</p>
     */
    @JRubyMethod(name = {"each_line", "each"}, optional = 1, frame = true)
    public RubyIO each_line(IRubyObject[] args, Block block) {
        ThreadContext context = getRuntime().getCurrentContext(); 
        ByteList separator = getSeparatorForGets(args);
        
        for (IRubyObject line = getline(separator); !line.isNil(); 
        	line = getline(separator)) {
            block.yield(context, line);
        }
        
        return this;
    }


    @JRubyMethod(name = "readlines", optional = 1)
    public RubyArray readlines(IRubyObject[] args) {
        ByteList separator;
        if (args.length > 0) {
            if (!getRuntime().getNilClass().isInstance(args[0]) &&
                !getRuntime().getString().isInstance(args[0])) {
                throw getRuntime().newTypeError(args[0], 
                        getRuntime().getString());
            } 
            separator = getSeparatorForGets(new IRubyObject[] { args[0] });
        } else {
            separator = getSeparatorForGets(IRubyObject.NULL_ARRAY);
        }

        RubyArray result = getRuntime().newArray();
        IRubyObject line;
        while (! (line = getline(separator)).isNil()) {
            result.append(line);
        }
        return result;
    }
    
    @JRubyMethod(name = "to_io")
    public RubyIO to_io() {
    	return this;
    }

    @Override
    public String toString() {
        return "RubyIO(" + openFile.getMode() + ", " + openFile.getMainStream().getDescriptor().getFileno() + ")";
    }
    
    /* class methods for IO */
    
    /** rb_io_s_foreach
    *
    */
    @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
    public static IRubyObject foreach(IRubyObject recv, IRubyObject[] args, Block block) {
        Ruby runtime = recv.getRuntime();
        int count = args.length;
        IRubyObject filename = args[0].convertToString();
        runtime.checkSafeString(filename);
       
        RubyString separator;
        if (count == 2) {
            separator = args[1].convertToString();
        } else {
            separator = runtime.getGlobalVariables().get("$/").convertToString();
        }
        
        RubyIO io = (RubyIO)RubyFile.open(runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
        
        if (!io.isNil()) {
            try {
                ByteList sep = separator.getByteList();
                IRubyObject str = io.getline(sep);
                ThreadContext context = runtime.getCurrentContext();
                while (!str.isNil()) {
                    block.yield(context, str);
                    str = io.getline(sep);
                }
            } finally {
                io.close();
            }
        }
       
        return runtime.getNil();
    }
   
    private static RubyIO registerSelect(Selector selector, IRubyObject obj, int ops) throws IOException {
       RubyIO ioObj;
       
       if (!(obj instanceof RubyIO)) {
           // invoke to_io
           if (!obj.respondsTo("to_io")) return null;

           ioObj = (RubyIO) obj.callMethod(obj.getRuntime().getCurrentContext(), "to_io");
       } else {
           ioObj = (RubyIO) obj;
       }
       
       Channel channel = ioObj.getChannel();
       if (channel == null || !(channel instanceof SelectableChannel)) {
           return null;
       }
       
       ((SelectableChannel) channel).configureBlocking(false);
       int real_ops = ((SelectableChannel) channel).validOps() & ops;
       SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
       
       if (key == null) {
           ((SelectableChannel) channel).register(selector, real_ops, obj);
       } else {
           key.interestOps(key.interestOps()|real_ops);
       }
       
       return ioObj;
   }
   
    @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
    public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
        return select_static(recv.getRuntime(), args);
    }
   
    public static IRubyObject select_static(Ruby runtime, IRubyObject[] args) {
       try {
           // FIXME: This needs to be ported
           boolean atLeastOneDescriptor = false;
           
           Set pending = new HashSet();
           Selector selector = Selector.open();
           if (!args[0].isNil()) {
               atLeastOneDescriptor = true;
               
               // read
               for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                   IRubyObject obj = (IRubyObject) i.next();
                   RubyIO ioObj = registerSelect(selector, obj, 
                           SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                   
                   if (ioObj!=null && ioObj.writeDataBuffered()) pending.add(obj);
               }
           }
           if (args.length > 1 && !args[1].isNil()) {
               atLeastOneDescriptor = true;
               // write
               for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                   IRubyObject obj = (IRubyObject) i.next();
                   registerSelect(selector, obj, SelectionKey.OP_WRITE);
               }
           }
           if (args.length > 2 && !args[2].isNil()) {
               atLeastOneDescriptor = true;
               // Java's select doesn't do anything about this, so we leave it be.
           }
           
           long timeout = 0;
           if(args.length > 3 && !args[3].isNil()) {
               if (args[3] instanceof RubyFloat) {
                   timeout = Math.round(((RubyFloat) args[3]).getDoubleValue() * 1000);
               } else {
                   timeout = Math.round(((RubyFixnum) args[3]).getDoubleValue() * 1000);
               }
               
               if (timeout < 0) {
                   throw runtime.newArgumentError("negative timeout given");
               }
           }
           
           if (!atLeastOneDescriptor) {
               return runtime.getNil();
           }
           
           if (pending.isEmpty()) {
               if (args.length > 3) {
                   if (timeout==0) {
                       selector.selectNow();
                   } else {
                       selector.select(timeout);                       
                   }
               } else {
                   selector.select();
               }
           } else {
               selector.selectNow();               
           }
           
           List r = new ArrayList();
           List w = new ArrayList();
           List e = new ArrayList();
           for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey) i.next();
               if ((key.interestOps() & key.readyOps()
                       & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                   r.add(key.attachment());
                   pending.remove(key.attachment());
               }
               if ((key.interestOps() & key.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                   w.add(key.attachment());
               }
           }
           r.addAll(pending);
           
           // make all sockets blocking as configured again
           for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey) i.next();
               SelectableChannel channel = key.channel();
               synchronized(channel.blockingLock()) {
                   boolean blocking = ((RubyIO) key.attachment()).getBlocking();
                   key.cancel();
                   channel.configureBlocking(blocking);
               }
           }
           selector.close();
           
           if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
               return runtime.getNil();
           }
           
           List ret = new ArrayList();
           
           ret.add(RubyArray.newArray(runtime, r));
           ret.add(RubyArray.newArray(runtime, w));
           ret.add(RubyArray.newArray(runtime, e));
           
           return RubyArray.newArray(runtime, ret);
       } catch(IOException e) {
           throw runtime.newIOError(e.getMessage());
       }
   }
   
    @JRubyMethod(name = "read", required = 1, optional = 2, meta = true)
    public static IRubyObject read(IRubyObject recv, IRubyObject[] args, Block block) {
       IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
       RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
       IRubyObject[] readArguments;
       
       if (args.length >= 2) {
           readArguments = new IRubyObject[] {args[1].convertToInteger()};
       } else {
           readArguments = new IRubyObject[] {};
       }
       
       try {
           
           if (args.length == 3) {
               file.seek(new IRubyObject[] {args[2].convertToInteger()});
           }
           
           return file.read(readArguments);
       } finally {
           file.close();
       }
   }
   
    @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
    public static RubyArray readlines(IRubyObject recv, IRubyObject[] args, Block block) {
        int count = args.length;

        IRubyObject[] fileArguments = new IRubyObject[]{args[0]};
        IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
        RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
        try {
            return file.readlines(separatorArguments);
        } finally {
            file.close();
        }
    }
   
    @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
    public static IRubyObject popen(IRubyObject recv, IRubyObject[] args, Block block) {
        Ruby runtime = recv.getRuntime();
        int mode;

        try {
            if (args.length == 1) {
                mode = ModeFlags.RDONLY;
            } else if (args[1] instanceof RubyFixnum) {
                mode = RubyFixnum.num2int(args[1]);
            } else {
                mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
            }

            ModeFlags modes = new ModeFlags(mode);
            IRubyObject cmdObj = args[0].convertToString();
            runtime.checkSafeString(cmdObj);
        
            Process process = new ShellLauncher(runtime).run(cmdObj);
            RubyIO io = new RubyIO(runtime, process, modes);

            if (block.isGiven()) {
                try {
                    return block.yield(runtime.getCurrentContext(), io);
                } finally {
                    if (io.openFile.isOpen()) {
                        io.close();
                    }
                    runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
                }
            }
            return io;
        } catch (InvalidValueException ex) {
            throw runtime.newErrnoEINVALError();
        } catch (IOException e) {
            throw runtime.newIOErrorFromException(e);
        } catch (InterruptedException e) {
            throw runtime.newThreadError("unexpected interrupt");
        }
    }

    // NIO based pipe
    @JRubyMethod(name = "pipe", meta = true)
    public static IRubyObject pipe(IRubyObject recv) throws Exception {
        // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
       Ruby runtime = recv.getRuntime();
       Pipe pipe = Pipe.open();
       
       RubyIO source = new RubyIO(runtime, pipe.source());
       RubyIO sink = new RubyIO(runtime, pipe.sink());
       
       sink.openFile.getMainStream().setSync(true);
       return runtime.newArrayNoCopy(new IRubyObject[]{
           source,
           sink
       });
   }
}
*****************************************
*****************************************
SATD id: 120		Size: 39
    public IRubyObject syswrite(IRubyObject obj) {
        try {
            RubyString string = obj.convertToString();
            
            OpenFile myOpenFile = getOpenFileChecked();
            
            myOpenFile.checkWritable(getRuntime());
            
            Stream writeStream = myOpenFile.getWriteStream();
            
            if (myOpenFile.isWriteBuffered()) {
                getRuntime().getWarnings().warn(
                        ID.SYSWRITE_BUFFERED_IO,
                        "syswrite for buffered IO");
            }
            
            if (!writeStream.getDescriptor().isWritable()) {
                myOpenFile.checkClosed(getRuntime());
            }
            
            int read = writeStream.getDescriptor().write(string.getByteList());
            
            if (read == -1) {
                // TODO? I think this ends up propagating from normal Java exceptions
                // sys_fail(openFile.getPath())
            }
            
            return getRuntime().newFixnum(read);
        } catch (InvalidValueException ex) {
            throw getRuntime().newErrnoEINVALError();
        } catch (PipeException ex) {
            throw getRuntime().newErrnoEPIPEError();
        } catch (BadDescriptorException e) {
            throw getRuntime().newErrnoEBADFError();
        } catch (IOException e) {
            e.printStackTrace();
            throw getRuntime().newSystemCallError(e.getMessage());
        }
    }
*****************************************
*****************************************
SATD id: 121		Size: 15

public Block cloneBlock(Binding binding) {
    // We clone dynamic scope because this will be a new instance of a block.  Any previously
    // captured instances of this block may still be around and we do not want to start
    // overwriting those values when we create a new one.
    // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
    binding = new Binding(
            binding.getSelf(),
            binding.getFrame().duplicate(),
            binding.getVisibility(),
            binding.getKlass(),
            binding.getDynamicScope());

    return new Block(this, binding);
}
*****************************************
*****************************************
SATD id: 122		Size: 2
    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
    public static final ByteList PARAGRAPH_DELIMETER = ByteList.create("PARAGRPH_DELIM_MRK_ER");
*****************************************
*****************************************
SATD id: 123		Size: 10
    private void loadClass(Ruby runtime, boolean wrap) {
      Script script = CompiledScriptLoader.loadScriptFromFile(runtime, is, searchName);
      if (script == null) {
        // we're depending on the side effect of the load, which loads the class but does not turn it into a script
        // I don't like it, but until we restructure the code a bit more, we'll need to quietly let it by here.
        return;
      }
      script.setFilename(scriptName);
      runtime.loadScript(script, wrap);
    }
*****************************************
*****************************************
SATD id: 124		Size: 40
    public IRubyObject bind(ThreadContext context, IRubyObject arg) {
        RubyArray sockaddr = (RubyArray) unpack_sockaddr_in(context, this, arg);

        try {
            IRubyObject addr = sockaddr.pop(context);
            IRubyObject port = sockaddr.pop(context);
            InetSocketAddress iaddr = new InetSocketAddress(
                    addr.convertToString().toString(), RubyNumeric.fix2int(port));

            Channel socketChannel = getChannel();
            if (socketChannel instanceof SocketChannel) {
                Socket socket = ((SocketChannel)socketChannel).socket();
                socket.bind(iaddr);
            } else if (socketChannel instanceof DatagramChannel) {
                DatagramSocket socket = ((DatagramChannel)socketChannel).socket();
                socket.bind(iaddr);
            } else {
                throw getRuntime().newErrnoENOPROTOOPTError();
            }
        } catch(UnknownHostException e) {
            throw sockerr(context.getRuntime(), "bind(2): unknown host");
        } catch(SocketException e) {
            handleSocketException(context.getRuntime(), e);
        } catch(IOException e) {
            e.printStackTrace();
            throw sockerr(context.getRuntime(), "bind(2): name or service not known");
        } catch (IllegalArgumentException iae) {
            throw sockerr(context.getRuntime(), iae.getMessage());
        } catch (Error e) {
            // Workaround for a bug in Sun's JDK 1.5.x, see
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
            Throwable cause = e.getCause();
            if (cause instanceof SocketException) {
                handleSocketException(context.getRuntime(), (SocketException)cause);
            } else {
                throw e;
            }
        }
        return RubyFixnum.zero(context.getRuntime());
    }
*****************************************
*****************************************
SATD id: 125		Size: 8
    public void buildConst(Node node, IR_BuilderContext m, boolean expr) {
        ConstNode constNode = (ConstNode) node;

        m.retrieveConstant(constNode.getName());
        // TODO: don't require pop
        if (!expr) m.consumeCurrentValue();
        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
    }
*****************************************
*****************************************
SATD id: 126		Size: 102
    private IRubyObject read19(IRubyObject[] args) {
        checkReadable();

        ByteList buf = null;
        int length = 0;
        boolean lengthGiven = false;
        int oldLength = 0;
        RubyString originalString = null;

        switch (args.length) {
        case 2:
            if (!args[1].isNil()) {
                originalString = args[1].convertToString();
                // must let original string know we're modifying, so shared buffers aren't damaged
                originalString.modify();
                buf = originalString.getByteList();
            }
        case 1:
            if (!args[0].isNil()) {
                length = RubyNumeric.fix2int(args[0]);
                lengthGiven = true;
                oldLength = length;

                if (length < 0) {
                    throw getRuntime().newArgumentError("negative length " + length + " given");
                }
                if (length > 0 && isEndOfString()) {
                    data.eof = true;
                    if (buf != null) buf.setRealSize(0);
                    return getRuntime().getNil();
                } else if (data.eof) {
                    if (buf != null) buf.setRealSize(0);
                    return getRuntime().getNil();
                }
                break;
            }
        case 0:
            oldLength = -1;
            length = data.internal.getByteList().length();

            if (length <= data.pos) {
                data.eof = true;
                if (buf == null) {
                    buf = new ByteList();
                } else {
                    buf.setRealSize(0);
                }

                return makeString(getRuntime(), buf, true);
            } else {
                length -= data.pos;
            }
            break;
        default:
            getRuntime().newArgumentError(args.length, 0);
        }

        if (buf == null) {
            int internalLength = data.internal.getByteList().length();

            if (internalLength > 0) {
                if (internalLength >= data.pos + length) {
                    buf = new ByteList(data.internal.getByteList(), (int) data.pos, length);
                } else {
                    int rest = (int) (data.internal.getByteList().length() - data.pos);

                    if (length > rest) length = rest;
                    buf = new ByteList(data.internal.getByteList(), (int) data.pos, length);
                }
            }
        } else {
            int rest = (int) (data.internal.getByteList().length() - data.pos);

            if (length > rest) length = rest;

            // Yow...this is still ugly
            byte[] target = buf.getUnsafeBytes();
            if (target.length > length) {
                System.arraycopy(data.internal.getByteList().getUnsafeBytes(), (int) data.pos, target, 0, length);
                buf.setBegin(0);
                buf.setRealSize(length);
            } else {
                target = new byte[length];
                System.arraycopy(data.internal.getByteList().getUnsafeBytes(), (int) data.pos, target, 0, length);
                buf.setBegin(0);
                buf.setRealSize(length);
                buf.setUnsafeBytes(target);
            }
        }

        if (buf == null) {
            if (!data.eof) buf = new ByteList();
            length = 0;
        } else {
            length = buf.length();
            data.pos += length;
        }

        if (oldLength < 0 || oldLength > length) data.eof = true;

        return originalString != null ? originalString : makeString(getRuntime(), buf, !lengthGiven);
    }
*****************************************
*****************************************
SATD id: 127		Size: 5
  @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      /* ENEBO: Lots of optz in 1.9 parser here*/
                    yyVal = new ForNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
    return yyVal;
  }
*****************************************
*****************************************
SATD id: 128		Size: 38
 * @author  jpetersen
 * FIXME convert to enum ?
 */
public final class Iter {
    /** No block given */
    public static final Iter ITER_NOT = new Iter("NOT");
    /** Block given before last method call ("previous") */
    public static final Iter ITER_PRE = new Iter("PRE");
    /** Is currently a block*/
    public static final Iter ITER_CUR = new Iter("CUR");

    private final String debug;

    private Iter(final String debug) {
        this.debug = debug;
    }

    public final boolean isNot() {
        return this == ITER_NOT;
    }

    public final boolean isPre() {
        return this == ITER_PRE;
    }

    public final boolean isCur() {
        return this == ITER_CUR;
    }

    public boolean isBlockGiven() {
        return !isNot();
    }

    @Override
    public String toString() {
        return debug;
    }
}
*****************************************
*****************************************
SATD id: 129		Size: 16
     * FIXME: any good reason to have two identical methods? (same as remove_class_variable)
     */
    public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
        String internedName = validateClassVariable(name.asSymbol());
        IRubyObject value;

        if ((value = deleteClassVariable(internedName)) != null) {
            return value;
        }

        if (fastIsClassVarDefined(internedName)) {
            throw cannotRemoveError(internedName);
        }

        throw getRuntime().newNameError("class variable " + internedName + " not defined for " + getName(), internedName);
    }
*****************************************
*****************************************
SATD id: 13		Size: 164
    public void Regexp(Regexp regexp) {
        if (!regexp.hasKnownValue() && !regexp.options.isOnce()) {
            if (regexp.getRegexp() instanceof CompoundString) {
                // FIXME: I don't like this custom logic for building CompoundString bits a different way :-\
                jvm.method().loadRuntime();
                { // negotiate RubyString pattern from parts
                    jvm.method().loadRuntime();
                    { // build RubyString[]
                        List<Operand> operands = ((CompoundString)regexp.getRegexp()).getPieces();
                        jvm.method().adapter.ldc(operands.size());
                        jvm.method().adapter.anewarray(p(RubyString.class));
                        for (int i = 0; i < operands.size(); i++) {
                            Operand operand = operands.get(i);
                            jvm.method().adapter.dup();
                            jvm.method().adapter.ldc(i);
                            visit(operand);
                            jvm.method().adapter.aastore();
                        }
                    }
                    jvm.method().adapter.ldc(regexp.options.toEmbeddedOptions());
                    jvm.method().adapter.invokestatic(p(RubyRegexp.class), "preprocessDRegexp", sig(RubyString.class, Ruby.class, RubyString[].class, int.class));
                }
                jvm.method().adapter.ldc(regexp.options.toEmbeddedOptions());
                jvm.method().adapter.invokestatic(p(RubyRegexp.class), "newDRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
            } else {
                jvm.method().loadRuntime();
                visit(regexp.getRegexp());
                jvm.method().adapter.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
                jvm.method().adapter.ldc(regexp.options.toEmbeddedOptions());
                jvm.method().adapter.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
            }
            jvm.method().adapter.dup();
            jvm.method().adapter.invokevirtual(p(RubyRegexp.class), "setLiteral", sig(void.class));
        } else {
            // FIXME: need to check this on cached path
            // context.runtime.getKCode() != rubyRegexp.getKCode()) {
            jvm.method().loadContext();
            visit(regexp.getRegexp());
            jvm.method().pushRegexp(regexp.options.toEmbeddedOptions());
        }
    }

    @Override
    public void ScopeModule(ScopeModule scopemodule) {
        jvm.method().adapter.aload(1);
        jvm.method().adapter.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
    }

    @Override
    public void Self(Self self) {
        // %self is in JVM-local-2 always
        jvm.method().loadLocal(2);
    }

    @Override
    public void Splat(Splat splat) {
        jvm.method().loadContext();
        visit(splat.getArray());
        jvm.method().invokeHelper("irSplat", RubyArray.class, ThreadContext.class, IRubyObject.class);
    }

    @Override
    public void StandardError(StandardError standarderror) {
        jvm.method().loadRuntime();
        jvm.method().adapter.invokevirtual(p(Ruby.class), "getStandardError", sig(RubyClass.class));
    }

    @Override
    public void StringLiteral(StringLiteral stringliteral) {
        jvm.method().pushString(stringliteral.getByteList());
    }

    @Override
    public void SValue(SValue svalue) {
        super.SValue(svalue);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void Symbol(Symbol symbol) {
        jvm.method().pushSymbol(symbol.getName());
    }

    @Override
    public void TemporaryVariable(TemporaryVariable temporaryvariable) {
        jvmLoadLocal(temporaryvariable);
    }

    @Override
    public void TemporaryLocalVariable(TemporaryLocalVariable temporarylocalvariable) {
        jvmLoadLocal(temporarylocalvariable);
    }

    @Override
    public void TemporaryFloatVariable(TemporaryFloatVariable temporaryfloatvariable) {
        jvmLoadLocal(temporaryfloatvariable);
    }

    @Override
    public void TemporaryFixnumVariable(TemporaryFixnumVariable temporaryfixnumvariable) {
        jvmLoadLocal(temporaryfixnumvariable);
    }

    @Override
    public void TemporaryBooleanVariable(TemporaryBooleanVariable temporarybooleanvariable) {
        jvmLoadLocal(temporarybooleanvariable);
    }

    @Override
    public void UndefinedValue(UndefinedValue undefinedvalue) {
        jvm.method().pushUndefined();
    }

    @Override
    public void UnexecutableNil(UnexecutableNil unexecutablenil) {
        throw new RuntimeException(this.getClass().getSimpleName() + " should never be directly executed!");
    }

    @Override
    public void WrappedIRClosure(WrappedIRClosure wrappedirclosure) {
        IRClosure closure = wrappedirclosure.getClosure();

        jvm.method().adapter.newobj(p(Block.class));
        jvm.method().adapter.dup();

        { // prepare block body (should be cached
            jvm.method().adapter.newobj(p(CompiledIRBlockBody.class));
            jvm.method().adapter.dup();

            // FIXME: This is inefficient because it's creating a new StaticScope every time
            String encodedScope = Helpers.encodeScope(closure.getStaticScope());
            jvm.method().loadContext();
            jvm.method().loadStaticScope();
            jvm.method().adapter.ldc(encodedScope);
            jvm.method().adapter.invokestatic(p(Helpers.class), "decodeScopeAndDetermineModule", sig(StaticScope.class, ThreadContext.class, StaticScope.class, String.class));

            jvm.method().adapter.ldc(Helpers.stringJoin(",", closure.getParameterList()));

            jvm.method().adapter.ldc(closure.getFileName());

            jvm.method().adapter.ldc(closure.getLineNumber());

            jvm.method().adapter.ldc(closure.isForLoopBody() || closure.isBeginEndBlock());

            jvm.method().adapter.ldc(closure.getHandle());

            jvm.method().adapter.ldc(closure.getArity().getValue());

            jvm.method().adapter.invokespecial(p(CompiledIRBlockBody.class), "<init>", sig(void.class, StaticScope.class, String.class, String.class, int.class, boolean.class, java.lang.invoke.MethodHandle.class, int.class));
        }

        { // prepare binding
            jvm.method().loadContext();
            visit(closure.getSelf());
            jvmLoadLocal(DYNAMIC_SCOPE);
            jvm.method().adapter.invokevirtual(p(ThreadContext.class), "currentBinding", sig(Binding.class, IRubyObject.class, DynamicScope.class));
        }

        jvm.method().adapter.invokespecial(p(Block.class), "<init>", sig(void.class, BlockBody.class, Binding.class));
    }

    private final JVM jvm;
    private IRScope currentScope;
    private int methodIndex = 0;
}
*****************************************
*****************************************
SATD id: 130		Size: 6
  @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    /* FIXME: Consider fixing node_assign itself rather than single case*/
                    ((Node)yyVal).setPosition(support.getPosition(((Node)yyVals[-2+yyTop])));
    return yyVal;
  }
*****************************************
*****************************************
SATD id: 131		Size: 8
    public void initCopy(IRubyObject original) {
        assert original != null;
        assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";

        setInstanceVariables(new HashMap(original.getInstanceVariables()));
        /* FIXME: finalizer should be dupped here */
        callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
    }
*****************************************
*****************************************
SATD id: 132		Size: 12
  @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    Node node = null;

                    /* FIXME: lose syntactical elements here (and others like this)*/
                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_concat(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
    return yyVal;
  }
*****************************************
*****************************************
SATD id: 133		Size: 2862
  public Object yyparse (RubyYaccLexer yyLex) throws java.io.IOException, yyException {
    if (yyMax <= 0) yyMax = 256;			// initial size
    int yyState = 0, yyStates[] = new int[yyMax];	// state stack
    Object yyVal = null, yyVals[] = new Object[yyMax];	// value stack
    int yyToken = -1;					// current input
    int yyErrorFlag = 0;				// #tokens to shift

    yyLoop: for (int yyTop = 0;; ++ yyTop) {
      if (yyTop >= yyStates.length) {			// dynamically increase
        int[] i = new int[yyStates.length+yyMax];
        System.arraycopy(yyStates, 0, i, 0, yyStates.length);
        yyStates = i;
        Object[] o = new Object[yyVals.length+yyMax];
        System.arraycopy(yyVals, 0, o, 0, yyVals.length);
        yyVals = o;
      }
      yyStates[yyTop] = yyState;
      yyVals[yyTop] = yyVal;

      yyDiscarded: for (;;) {	// discarding a token does not change stack
        int yyN;
        if ((yyN = yyDefRed[yyState]) == 0) {	// else [default] reduce (yyN)
          if (yyToken < 0) {
            yyToken = yyLex.advance() ? yyLex.token() : 0;
          }
          if ((yyN = yySindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < yyTable.length && yyCheck[yyN] == yyToken) {
            yyState = yyTable[yyN];		// shift to yyN
            yyVal = yyLex.value();
            yyToken = -1;
            if (yyErrorFlag > 0) -- yyErrorFlag;
            continue yyLoop;
          }
          if ((yyN = yyRindex[yyState]) != 0 && (yyN += yyToken) >= 0
              && yyN < yyTable.length && yyCheck[yyN] == yyToken)
            yyN = yyTable[yyN];			// reduce (yyN)
          else
            switch (yyErrorFlag) {
  
            case 0:
              yyerror("syntax error", yyExpecting(yyState), yyNames[yyToken]);
  
            case 1: case 2:
              yyErrorFlag = 3;
              do {
                if ((yyN = yySindex[yyStates[yyTop]]) != 0
                    && (yyN += yyErrorCode) >= 0 && yyN < yyTable.length
                    && yyCheck[yyN] == yyErrorCode) {
                  yyState = yyTable[yyN];
                  yyVal = yyLex.value();
                  continue yyLoop;
                }
              } while (-- yyTop >= 0);
              throw new yyException("irrecoverable syntax error");
  
            case 3:
              if (yyToken == 0) {
                throw new yyException("irrecoverable syntax error at end-of-file");
              }
              yyToken = -1;
              continue yyDiscarded;		// leave stack alone
            }
        }
        int yyV = yyTop + 1-yyLen[yyN];
        yyVal = yyDefault(yyV > yyTop ? null : yyVals[yyV]);
        switch (yyN) {
case 1:
					// line 275 "Ruby19Parser.y"
  {
                  lexer.setState(LexState.EXPR_BEG);
                  support.initTopLocalVariables();
              }
  break;
case 2:
					// line 278 "Ruby19Parser.y"
  {
  /* ENEBO: Removed !compile_for_eval which probably is to reduce warnings*/
                  if (((Node)yyVals[0+yyTop]) != null) {
                      /* last expression should not be void */
                      if (((Node)yyVals[0+yyTop]) instanceof BlockNode) {
                          support.checkUselessStatement(((BlockNode)yyVals[0+yyTop]).getLast());
                      } else {
                          support.checkUselessStatement(((Node)yyVals[0+yyTop]));
                      }
                  }
                  support.getResult().setAST(support.addRootNode(((Node)yyVals[0+yyTop]), getPosition(((Node)yyVals[0+yyTop]))));
              }
  break;
case 3:
					// line 291 "Ruby19Parser.y"
  {
                  Node node = ((Node)yyVals[-3+yyTop]);

                  if (((RescueBodyNode)yyVals[-2+yyTop]) != null) {
                      node = new RescueNode(getPosition(((Node)yyVals[-3+yyTop]), true), ((Node)yyVals[-3+yyTop]), ((RescueBodyNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                  } else if (((Node)yyVals[-1+yyTop]) != null) {
                      warnings.warn(ID.ELSE_WITHOUT_RESCUE, getPosition(((Node)yyVals[-3+yyTop])), "else without rescue is useless");
                      node = support.appendToBlock(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                  }
                  if (((Node)yyVals[0+yyTop]) != null) {
                      if (node == null) node = NilImplicitNode.NIL;
                      node = new EnsureNode(getPosition(((Node)yyVals[-3+yyTop])), node, ((Node)yyVals[0+yyTop]));
                  }

                  yyVal = node;
                }
  break;
case 4:
					// line 308 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) instanceof BlockNode) {
                        support.checkUselessStatements(((BlockNode)yyVals[-1+yyTop]));
                    }
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 6:
					// line 316 "Ruby19Parser.y"
  {
                    yyVal = support.newline_node(((Node)yyVals[0+yyTop]), getPosition(((Node)yyVals[0+yyTop]), true));
                }
  break;
case 7:
					// line 319 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((Node)yyVals[-2+yyTop]), support.newline_node(((Node)yyVals[0+yyTop]), getPosition(((Node)yyVals[0+yyTop]), true)));
                }
  break;
case 8:
					// line 322 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 9:
					// line 326 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_FNAME);
                }
  break;
case 10:
					// line 328 "Ruby19Parser.y"
  {
                    yyVal = new AliasNode(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 11:
					// line 331 "Ruby19Parser.y"
  {
                    yyVal = new VAliasNode(getPosition(((Token)yyVals[-2+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 12:
					// line 334 "Ruby19Parser.y"
  {
                    yyVal = new VAliasNode(getPosition(((Token)yyVals[-2+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue(), "$" + ((BackRefNode)yyVals[0+yyTop]).getType());
                }
  break;
case 13:
					// line 337 "Ruby19Parser.y"
  {
                    yyerror("can't make alias for the number variables");
                }
  break;
case 14:
					// line 340 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 15:
					// line 343 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), null);
                }
  break;
case 16:
					// line 346 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), null, ((Node)yyVals[-2+yyTop]));
                }
  break;
case 17:
					// line 349 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                        yyVal = new WhileNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                    } else {
                        yyVal = new WhileNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                    }
                }
  break;
case 18:
					// line 356 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-2+yyTop]) != null && ((Node)yyVals[-2+yyTop]) instanceof BeginNode) {
                        yyVal = new UntilNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((BeginNode)yyVals[-2+yyTop]).getBodyNode(), false);
                    } else {
                        yyVal = new UntilNode(getPosition(((Node)yyVals[-2+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), true);
                    }
                }
  break;
case 19:
					// line 363 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                    yyVal = new RescueNode(getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), new RescueBodyNode(getPosition(((Node)yyVals[-2+yyTop])), null, body, null), null);
                }
  break;
case 20:
					// line 367 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("BEGIN in method");
                    }
                    support.pushLocalScope();
                }
  break;
case 21:
					// line 372 "Ruby19Parser.y"
  {
                    support.getResult().addBeginNode(new PreExeNode(getPosition(((Node)yyVals[-1+yyTop])), support.getCurrentScope(), ((Node)yyVals[-1+yyTop])));
                    support.popCurrentScope();
                    yyVal = null;
                }
  break;
case 22:
					// line 377 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        warnings.warn(ID.END_IN_METHOD, getPosition(((Token)yyVals[-3+yyTop])), "END in method; use at_exit");
                    }
                    yyVal = new PostExeNode(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 23:
					// line 383 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 24:
					// line 387 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                }
  break;
case 25:
					// line 392 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));

                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();
                    if (asgnOp.equals("||")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnOrNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else if (asgnOp.equals("&&")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnAndNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                    }
                }
  break;
case 26:
					// line 408 "Ruby19Parser.y"
  {
  /* FIXME: arg_concat logic missing for opt_call_args*/
                    yyVal = support.new_opElementAsgnNode(getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 27:
					// line 412 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 28:
					// line 415 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 29:
					// line 418 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 30:
					// line 421 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                }
  break;
case 31:
					// line 424 "Ruby19Parser.y"
  {
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 32:
					// line 427 "Ruby19Parser.y"
  {
                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                }
  break;
case 33:
					// line 431 "Ruby19Parser.y"
  {
                    ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                    yyVal = ((MultipleAsgn19Node)yyVals[-2+yyTop]);
                    ((MultipleAsgn19Node)yyVals[-2+yyTop]).setPosition(support.union(((MultipleAsgn19Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 36:
					// line 440 "Ruby19Parser.y"
  {
                    yyVal = support.newAndNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 37:
					// line 443 "Ruby19Parser.y"
  {
                    yyVal = support.newOrNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 38:
					// line 446 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])));
                }
  break;
case 39:
					// line 449 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])));
                }
  break;
case 41:
					// line 454 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                }
  break;
case 44:
					// line 461 "Ruby19Parser.y"
  {
                    yyVal = new ReturnNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.ret_args(((Node)yyVals[0+yyTop]), getPosition(((Token)yyVals[-1+yyTop]))));
                }
  break;
case 45:
					// line 464 "Ruby19Parser.y"
  {
                    yyVal = new BreakNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.ret_args(((Node)yyVals[0+yyTop]), getPosition(((Token)yyVals[-1+yyTop]))));
                }
  break;
case 46:
					// line 467 "Ruby19Parser.y"
  {
                    yyVal = new NextNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.ret_args(((Node)yyVals[0+yyTop]), getPosition(((Token)yyVals[-1+yyTop]))));
                }
  break;
case 48:
					// line 473 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 49:
					// line 476 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 50:
					// line 481 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 51:
					// line 483 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                }
  break;
case 52:
					// line 489 "Ruby19Parser.y"
  {
                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 53:
					// line 492 "Ruby19Parser.y"
  {
                    yyVal = support.new_fcall(((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                }
  break;
case 54:
					// line 495 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 55:
					// line 498 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])); 
                }
  break;
case 56:
					// line 501 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 57:
					// line 504 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((Token)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
                }
  break;
case 58:
					// line 507 "Ruby19Parser.y"
  {
                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop])); /* .setPosFrom($2);*/
                }
  break;
case 59:
					// line 510 "Ruby19Parser.y"
  {
                    yyVal = support.new_yield(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 61:
					// line 516 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 62:
					// line 521 "Ruby19Parser.y"
  {
                    yyVal = ((MultipleAsgn19Node)yyVals[0+yyTop]);
                }
  break;
case 63:
					// line 524 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-2+yyTop])), support.newArrayNode(getPosition(((Token)yyVals[-2+yyTop])), ((Node)yyVals[-1+yyTop])), null, null);
                }
  break;
case 64:
					// line 529 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[0+yyTop])), ((ListNode)yyVals[0+yyTop]), null, null);
                }
  break;
case 65:
					// line 532 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(support.union(((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]).add(((Node)yyVals[0+yyTop])), null, null);
                }
  break;
case 66:
					// line 535 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-2+yyTop])), ((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), (ListNode) null);
                }
  break;
case 67:
					// line 538 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-4+yyTop])), ((ListNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 68:
					// line 541 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-1+yyTop])), ((ListNode)yyVals[-1+yyTop]), new StarNode(getPosition(null)), null);
                }
  break;
case 69:
					// line 544 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-3+yyTop])), ((ListNode)yyVals[-3+yyTop]), new StarNode(getPosition(null)), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 70:
					// line 547 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-1+yyTop])), null, ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 71:
					// line 550 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-3+yyTop])), null, ((Node)yyVals[-2+yyTop]), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 72:
					// line 553 "Ruby19Parser.y"
  {
                      yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[0+yyTop])), null, new StarNode(getPosition(null)), null);
                }
  break;
case 73:
					// line 556 "Ruby19Parser.y"
  {
                      yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-2+yyTop])), null, new StarNode(getPosition(null)), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 75:
					// line 561 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 76:
					// line 566 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 77:
					// line 569 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                }
  break;
case 78:
					// line 574 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                }
  break;
case 79:
					// line 577 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 80:
					// line 581 "Ruby19Parser.y"
  {
                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 81:
					// line 584 "Ruby19Parser.y"
  {
                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 82:
					// line 587 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 83:
					// line 590 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 84:
					// line 593 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 85:
					// line 596 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon2Node(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 86:
					// line 605 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon3Node(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 87:
					// line 614 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
                }
  break;
case 88:
					// line 618 "Ruby19Parser.y"
  {
                      /* if (!($$ = assignable($1, 0))) $$ = NEW_BEGIN(0);*/
                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 89:
					// line 622 "Ruby19Parser.y"
  {
                    yyVal = support.aryset(((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 90:
					// line 625 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 91:
					// line 628 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 92:
					// line 631 "Ruby19Parser.y"
  {
                    yyVal = support.attrset(((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 93:
					// line 634 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon2Node(position, ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 94:
					// line 643 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("dynamic constant assignment");
                    }

                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop]));

                    yyVal = new ConstDeclNode(position, null, new Colon3Node(position, (String) ((Token)yyVals[0+yyTop]).getValue()), NilImplicitNode.NIL);
                }
  break;
case 95:
					// line 652 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[0+yyTop]));
                }
  break;
case 96:
					// line 656 "Ruby19Parser.y"
  {
                    yyerror("class/module name must be CONSTANT");
                }
  break;
case 98:
					// line 661 "Ruby19Parser.y"
  {
                    yyVal = new Colon3Node(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 99:
					// line 664 "Ruby19Parser.y"
  {
                    yyVal = new Colon2Node(((Token)yyVals[0+yyTop]).getPosition(), null, (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 100:
					// line 667 "Ruby19Parser.y"
  {
                    yyVal = new Colon2Node(support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 104:
					// line 673 "Ruby19Parser.y"
  {
                   lexer.setState(LexState.EXPR_END);
                   yyVal = ((Token)yyVals[0+yyTop]);
               }
  break;
case 105:
					// line 677 "Ruby19Parser.y"
  {
                   lexer.setState(LexState.EXPR_END);
                   yyVal = ((Token)yyVals[0+yyTop]);
               }
  break;
case 106:
					// line 683 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 107:
					// line 686 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 108:
					// line 691 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 109:
					// line 694 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 110:
					// line 698 "Ruby19Parser.y"
  {
                    yyVal = new UndefNode(getPosition(((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 111:
					// line 701 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_FNAME);
                }
  break;
case 112:
					// line 703 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((Node)yyVals[-3+yyTop]), new UndefNode(getPosition(((Node)yyVals[-3+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue()));
                }
  break;
case 184:
					// line 722 "Ruby19Parser.y"
  {
                    yyVal = support.node_assign(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    /* FIXME: Consider fixing node_assign itself rather than single case*/
                    ((Node)yyVal).setPosition(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 185:
					// line 727 "Ruby19Parser.y"
  {
                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                    yyVal = support.node_assign(((Node)yyVals[-4+yyTop]), new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null));
                }
  break;
case 186:
					// line 732 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    String asgnOp = (String) ((Token)yyVals[-1+yyTop]).getValue();

                    if (asgnOp.equals("||")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnOrNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else if (asgnOp.equals("&&")) {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(((Node)yyVals[0+yyTop]));
                        yyVal = new OpAsgnAndNode(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.gettable2(((AssignableNode)yyVals[-2+yyTop])), ((AssignableNode)yyVals[-2+yyTop]));
                    } else {
                        ((AssignableNode)yyVals[-2+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-2+yyTop])), asgnOp, ((Node)yyVals[0+yyTop])));
                        ((AssignableNode)yyVals[-2+yyTop]).setPosition(support.union(((AssignableNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                        yyVal = ((AssignableNode)yyVals[-2+yyTop]);
                    }
                }
  break;
case 187:
					// line 748 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[-2+yyTop]));
                    ISourcePosition position = support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                    Node body = ((Node)yyVals[0+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[0+yyTop]);
                    Node rescueNode = new RescueNode(position, ((Node)yyVals[-2+yyTop]), new RescueBodyNode(position, null, body, null), null);

                    String asgnOp = (String) ((Token)yyVals[-3+yyTop]).getValue();
                    if (asgnOp.equals("||")) {
                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
                        yyVal = new OpAsgnOrNode(support.union(((AssignableNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop])), support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                    } else if (asgnOp.equals("&&")) {
                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(((Node)yyVals[-2+yyTop]));
                        yyVal = new OpAsgnAndNode(support.union(((AssignableNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop])), support.gettable2(((AssignableNode)yyVals[-4+yyTop])), ((AssignableNode)yyVals[-4+yyTop]));
                    } else {
                        ((AssignableNode)yyVals[-4+yyTop]).setValueNode(support.getOperatorCallNode(support.gettable2(((AssignableNode)yyVals[-4+yyTop])), asgnOp, ((Node)yyVals[-2+yyTop])));
                        ((AssignableNode)yyVals[-4+yyTop]).setPosition(support.union(((AssignableNode)yyVals[-4+yyTop]), ((Node)yyVals[-2+yyTop])));
                        yyVal = ((AssignableNode)yyVals[-4+yyTop]);
                    }
                }
  break;
case 188:
					// line 767 "Ruby19Parser.y"
  {
  /* FIXME: arg_concat missing for opt_call_args*/
                    yyVal = support.new_opElementAsgnNode(getPosition(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-5+yyTop]), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 189:
					// line 771 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 190:
					// line 774 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 191:
					// line 777 "Ruby19Parser.y"
  {
                    yyVal = new OpAsgnNode(getPosition(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-4+yyTop]), ((Node)yyVals[0+yyTop]), (String) ((Token)yyVals[-2+yyTop]).getValue(), (String) ((Token)yyVals[-1+yyTop]).getValue());
                }
  break;
case 192:
					// line 780 "Ruby19Parser.y"
  {
                    yyerror("constant re-assignment");
                }
  break;
case 193:
					// line 783 "Ruby19Parser.y"
  {
                    yyerror("constant re-assignment");
                }
  break;
case 194:
					// line 786 "Ruby19Parser.y"
  {
                    support.backrefAssignError(((Node)yyVals[-2+yyTop]));
                }
  break;
case 195:
					// line 789 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[-2+yyTop]));
                    support.checkExpression(((Node)yyVals[0+yyTop]));
    
                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                    yyVal = new DotNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), false, isLiteral);
                }
  break;
case 196:
					// line 796 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[-2+yyTop]));
                    support.checkExpression(((Node)yyVals[0+yyTop]));

                    boolean isLiteral = ((Node)yyVals[-2+yyTop]) instanceof FixnumNode && ((Node)yyVals[0+yyTop]) instanceof FixnumNode;
                    yyVal = new DotNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]), true, isLiteral);
                }
  break;
case 197:
					// line 803 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "+", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 198:
					// line 806 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "-", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 199:
					// line 809 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "*", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 200:
					// line 812 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "/", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 201:
					// line 815 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "%", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 202:
					// line 818 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 203:
					// line 821 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), getPosition(null)), "-@");
                }
  break;
case 204:
					// line 824 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(support.getOperatorCallNode(((FloatNode)yyVals[-2+yyTop]), "**", ((Node)yyVals[0+yyTop]), getPosition(null)), "-@");
                }
  break;
case 205:
					// line 827 "Ruby19Parser.y"
  {
                    if (support.isLiteral(((Node)yyVals[0+yyTop]))) {
                        yyVal = ((Node)yyVals[0+yyTop]);
                    } else {
                        yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "+@");
                    }
                }
  break;
case 206:
					// line 834 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "-@");
                }
  break;
case 207:
					// line 837 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "|", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 208:
					// line 840 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "^", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 209:
					// line 843 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "&", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 210:
					// line 846 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=>", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 211:
					// line 849 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 212:
					// line 852 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">=", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 213:
					// line 855 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 214:
					// line 858 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<=", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 215:
					// line 861 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "==", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 216:
					// line 864 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "===", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 217:
					// line 867 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "!=", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 218:
					// line 870 "Ruby19Parser.y"
  {
                    yyVal = support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                  /* ENEBO
                        $$ = match_op($1, $3);
                        if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                            $$ = reg_named_capture_assign($1->nd_lit, $$);
                        }
                  */
                }
  break;
case 219:
					// line 879 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), support.getMatchNode(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 220:
					// line 882 "Ruby19Parser.y"
  {
                    yyVal = new NotNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[0+yyTop])));
                }
  break;
case 221:
					// line 885 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[0+yyTop]), "~");
                }
  break;
case 222:
					// line 888 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), "<<", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 223:
					// line 891 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(((Node)yyVals[-2+yyTop]), ">>", ((Node)yyVals[0+yyTop]), getPosition(null));
                }
  break;
case 224:
					// line 894 "Ruby19Parser.y"
  {
                    yyVal = support.newAndNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 225:
					// line 897 "Ruby19Parser.y"
  {
                    yyVal = support.newOrNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 226:
					// line 900 "Ruby19Parser.y"
  {
                    /* ENEBO: arg surrounded by in_defined set/unset*/
                    yyVal = new DefinedNode(getPosition(((Token)yyVals[-2+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 227:
					// line 904 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(getPosition(((Node)yyVals[-5+yyTop])), support.getConditionNode(((Node)yyVals[-5+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 228:
					// line 907 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 229:
					// line 911 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    yyVal = ((Node)yyVals[0+yyTop]) != null ? ((Node)yyVals[0+yyTop]) : NilImplicitNode.NIL;
                }
  break;
case 231:
					// line 917 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 232:
					// line 920 "Ruby19Parser.y"
  {
                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                }
  break;
case 233:
					// line 923 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition(((ListNode)yyVals[-1+yyTop])), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                }
  break;
case 234:
					// line 927 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                    if (yyVal != null) ((Node)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 239:
					// line 936 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 240:
					// line 939 "Ruby19Parser.y"
  {
                    yyVal = support.arg_blk_pass(((Node)yyVals[-1+yyTop]), ((BlockPassNode)yyVals[0+yyTop]));
                }
  break;
case 241:
					// line 942 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition(((ListNode)yyVals[-1+yyTop])), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                }
  break;
case 242:
					// line 946 "Ruby19Parser.y"
  {
                    yyVal = support.arg_append(((Node)yyVals[-3+yyTop]), new HashNode(getPosition(null), ((ListNode)yyVals[-1+yyTop])));
                    yyVal = support.arg_blk_pass((Node)yyVal, ((BlockPassNode)yyVals[0+yyTop]));
                }
  break;
case 243:
					// line 950 "Ruby19Parser.y"
  {}
  break;
case 244:
					// line 952 "Ruby19Parser.y"
  {
                    yyVal = new Long(lexer.getCmdArgumentState().begin());
                }
  break;
case 245:
					// line 954 "Ruby19Parser.y"
  {
                    lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 246:
					// line 959 "Ruby19Parser.y"
  {
                    yyVal = new BlockPassNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 247:
					// line 963 "Ruby19Parser.y"
  {
                    yyVal = ((BlockPassNode)yyVals[0+yyTop]);
                }
  break;
case 249:
					// line 968 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(getPosition2(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 250:
					// line 971 "Ruby19Parser.y"
  {
                    yyVal = support.newSplatNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 251:
					// line 974 "Ruby19Parser.y"
  {
                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                    if (node != null) {
                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 252:
					// line 983 "Ruby19Parser.y"
  {
                    Node node = null;

                    /* FIXME: lose syntactical elements here (and others like this)*/
                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_concat(support.union(((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 253:
					// line 995 "Ruby19Parser.y"
  {
                    Node node = support.splat_array(((Node)yyVals[-2+yyTop]));

                    if (node != null) {
                        yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 254:
					// line 1004 "Ruby19Parser.y"
  {
                    Node node = null;

                    if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                        (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                        yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                    } else {
                        yyVal = support.arg_concat(support.union(((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                    }
                }
  break;
case 255:
					// line 1014 "Ruby19Parser.y"
  {
                     yyVal = support.newSplatNode(getPosition(((Token)yyVals[-1+yyTop])), ((Node)yyVals[0+yyTop]));  
                }
  break;
case 264:
					// line 1026 "Ruby19Parser.y"
  {
                    yyVal = new FCallNoArgNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 265:
					// line 1029 "Ruby19Parser.y"
  {
                    yyVal = new BeginNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
                }
  break;
case 266:
					// line 1032 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_ENDARG); 
                }
  break;
case 267:
					// line 1034 "Ruby19Parser.y"
  {
                    warnings.warning(ID.GROUPED_EXPRESSION, getPosition(((Token)yyVals[-3+yyTop])), "(...) interpreted as grouped expression");
                    yyVal = ((Node)yyVals[-2+yyTop]);
                }
  break;
case 268:
					// line 1038 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) != null) {
                        /* compstmt position includes both parens around it*/
                        ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                    }
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 269:
					// line 1045 "Ruby19Parser.y"
  {
                    yyVal = new Colon2Node(support.union(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-2+yyTop]), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 270:
					// line 1048 "Ruby19Parser.y"
  {
                    yyVal = new Colon3Node(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 271:
					// line 1051 "Ruby19Parser.y"
  {
                    ISourcePosition position = support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));
                    if (((Node)yyVals[-1+yyTop]) == null) {
                        yyVal = new ZArrayNode(position); /* zero length array */
                    } else {
                        yyVal = ((Node)yyVals[-1+yyTop]);
                        ((ISourcePositionHolder)yyVal).setPosition(position);
                    }
                }
  break;
case 272:
					// line 1060 "Ruby19Parser.y"
  {
                    yyVal = new HashNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]));
                }
  break;
case 273:
					// line 1063 "Ruby19Parser.y"
  {
                    yyVal = new ReturnNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
                }
  break;
case 274:
					// line 1066 "Ruby19Parser.y"
  {
                    yyVal = support.new_yield(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 275:
					// line 1069 "Ruby19Parser.y"
  {
                    yyVal = new YieldNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), null, false);
                }
  break;
case 276:
					// line 1072 "Ruby19Parser.y"
  {
                    yyVal = new YieldNode(((Token)yyVals[0+yyTop]).getPosition(), null, false);
                }
  break;
case 277:
					// line 1075 "Ruby19Parser.y"
  {
                    yyVal = new DefinedNode(getPosition(((Token)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 278:
					// line 1078 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
                }
  break;
case 279:
					// line 1081 "Ruby19Parser.y"
  {
                    yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                }
  break;
case 280:
					// line 1084 "Ruby19Parser.y"
  {
                    yyVal = new FCallNoArgBlockNode(support.union(((Token)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue(), ((IterNode)yyVals[0+yyTop]));
                }
  break;
case 282:
					// line 1088 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) != null && 
                          ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, getPosition(((Node)yyVals[-1+yyTop])), "Both block arg and actual block given.");
                    }
                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                    ((Node)yyVal).setPosition(support.union(((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])));
                }
  break;
case 283:
					// line 1096 "Ruby19Parser.y"
  {
                    yyVal = ((LambdaNode)yyVals[0+yyTop]);
                }
  break;
case 284:
					// line 1099 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 285:
					// line 1102 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
                }
  break;
case 286:
					// line 1105 "Ruby19Parser.y"
  {
                    lexer.getConditionState().begin();
                }
  break;
case 287:
					// line 1107 "Ruby19Parser.y"
  {
                    lexer.getConditionState().end();
                }
  break;
case 288:
					// line 1109 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                    yyVal = new WhileNode(support.union(((Token)yyVals[-6+yyTop]), ((Token)yyVals[0+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                }
  break;
case 289:
					// line 1113 "Ruby19Parser.y"
  {
                  lexer.getConditionState().begin();
                }
  break;
case 290:
					// line 1115 "Ruby19Parser.y"
  {
                  lexer.getConditionState().end();
                }
  break;
case 291:
					// line 1117 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                    yyVal = new UntilNode(getPosition(((Token)yyVals[-6+yyTop])), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
                }
  break;
case 292:
					// line 1121 "Ruby19Parser.y"
  {
                    yyVal = new CaseNode(support.union(((Token)yyVals[-4+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 293:
					// line 1124 "Ruby19Parser.y"
  {
                    yyVal = new CaseNode(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), null, ((Node)yyVals[-1+yyTop]));
                }
  break;
case 294:
					// line 1127 "Ruby19Parser.y"
  {
                    lexer.getConditionState().begin();
                }
  break;
case 295:
					// line 1129 "Ruby19Parser.y"
  {
                    lexer.getConditionState().end();
                }
  break;
case 296:
					// line 1131 "Ruby19Parser.y"
  {
                      /* ENEBO: Lots of optz in 1.9 parser here*/
                    yyVal = new ForNode(support.union(((Token)yyVals[-8+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]));
                }
  break;
case 297:
					// line 1135 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) {
                        yyerror("class definition in method body");
                    }
                    support.pushLocalScope();
                }
  break;
case 298:
					// line 1140 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                    yyVal = new ClassNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
                    support.popCurrentScope();
                }
  break;
case 299:
					// line 1146 "Ruby19Parser.y"
  {
                    yyVal = new Boolean(support.isInDef());
                    support.setInDef(false);
                }
  break;
case 300:
					// line 1149 "Ruby19Parser.y"
  {
                    yyVal = new Integer(support.getInSingle());
                    support.setInSingle(0);
                    support.pushLocalScope();
                }
  break;
case 301:
					// line 1153 "Ruby19Parser.y"
  {
                    yyVal = new SClassNode(support.union(((Token)yyVals[-7+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), ((Node)yyVals[-1+yyTop]));
                    support.popCurrentScope();
                    support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
                    support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
                }
  break;
case 302:
					// line 1159 "Ruby19Parser.y"
  {
                    if (support.isInDef() || support.isInSingle()) { 
                        yyerror("module definition in method body");
                    }
                    support.pushLocalScope();
                }
  break;
case 303:
					// line 1164 "Ruby19Parser.y"
  {
                    Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);

                    yyVal = new ModuleNode(support.union(((Token)yyVals[-4+yyTop]), ((Token)yyVals[0+yyTop])), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
                    support.popCurrentScope();
                }
  break;
case 304:
					// line 1170 "Ruby19Parser.y"
  {
                    support.setInDef(true);
                    support.pushLocalScope();
                }
  break;
case 305:
					// line 1173 "Ruby19Parser.y"
  {
                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                    Node body = ((Node)yyVals[-1+yyTop]); /*$5 == null ? NilImplicitNode.NIL : $5;*/

                    yyVal = new DefnNode(support.union(((Token)yyVals[-5+yyTop]), ((Token)yyVals[0+yyTop])), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                    support.popCurrentScope();
                    support.setInDef(false);
                }
  break;
case 306:
					// line 1181 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_FNAME);
                }
  break;
case 307:
					// line 1183 "Ruby19Parser.y"
  {
                    support.setInSingle(support.getInSingle() + 1);
                    support.pushLocalScope();
                    lexer.setState(LexState.EXPR_END); /* force for args */
                }
  break;
case 308:
					// line 1187 "Ruby19Parser.y"
  {
                    /* TODO: We should use implicit nil for body, but problem (punt til later)*/
                    Node body = ((Node)yyVals[-1+yyTop]); /*$8 == null ? NilImplicitNode.NIL : $8;*/

                    yyVal = new DefsNode(support.union(((Token)yyVals[-8+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((Token)yyVals[-4+yyTop]).getPosition(), (String) ((Token)yyVals[-4+yyTop]).getValue()), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                    support.popCurrentScope();
                    support.setInSingle(support.getInSingle() - 1);
                }
  break;
case 309:
					// line 1195 "Ruby19Parser.y"
  {
                    yyVal = new BreakNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
                }
  break;
case 310:
					// line 1198 "Ruby19Parser.y"
  {
                    yyVal = new NextNode(((Token)yyVals[0+yyTop]).getPosition(), NilImplicitNode.NIL);
                }
  break;
case 311:
					// line 1201 "Ruby19Parser.y"
  {
                    yyVal = new RedoNode(((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 312:
					// line 1204 "Ruby19Parser.y"
  {
                    yyVal = new RetryNode(((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 313:
					// line 1208 "Ruby19Parser.y"
  {
                    support.checkExpression(((Node)yyVals[0+yyTop]));
                    yyVal = ((Node)yyVals[0+yyTop]);
                    if (yyVal == null) yyVal = NilImplicitNode.NIL;
                }
  break;
case 320:
					// line 1222 "Ruby19Parser.y"
  {
                    yyVal = new IfNode(getPosition(((Token)yyVals[-4+yyTop])), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 322:
					// line 1227 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 324:
					// line 1232 "Ruby19Parser.y"
  {}
  break;
case 325:
					// line 1234 "Ruby19Parser.y"
  {
                     yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 326:
					// line 1237 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 327:
					// line 1241 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                }
  break;
case 328:
					// line 1244 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 329:
					// line 1248 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[0+yyTop])), ((ListNode)yyVals[0+yyTop]), null, null);
                }
  break;
case 330:
					// line 1251 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-3+yyTop])), ((ListNode)yyVals[-3+yyTop]), support.assignable(((Token)yyVals[0+yyTop]), null), null);
                }
  break;
case 331:
					// line 1254 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-5+yyTop])), ((ListNode)yyVals[-5+yyTop]), support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 332:
					// line 1257 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-2+yyTop])), ((ListNode)yyVals[-2+yyTop]), new StarNode(getPosition(null)), null);
                }
  break;
case 333:
					// line 1260 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((ListNode)yyVals[-4+yyTop])), ((ListNode)yyVals[-4+yyTop]), new StarNode(getPosition(null)), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 334:
					// line 1263 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-1+yyTop])), null, support.assignable(((Token)yyVals[0+yyTop]), null), null);
                }
  break;
case 335:
					// line 1266 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-3+yyTop])), null, support.assignable(((Token)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 336:
					// line 1269 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[0+yyTop])), null, new StarNode(getPosition(null)), null);
                }
  break;
case 337:
					// line 1272 "Ruby19Parser.y"
  {
                    yyVal = new MultipleAsgn19Node(getPosition(((Token)yyVals[-2+yyTop])), null, null, ((ListNode)yyVals[0+yyTop]));
                }
  break;
case 338:
					// line 1276 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 339:
					// line 1279 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-7+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 340:
					// line 1282 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 341:
					// line 1285 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 342:
					// line 1288 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 343:
					// line 1291 "Ruby19Parser.y"
  {
    /* FIXME, weird unnamed rest*/
                    yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, null);
                }
  break;
case 344:
					// line 1295 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 345:
					// line 1298 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 346:
					// line 1301 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 347:
					// line 1304 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 348:
					// line 1307 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 349:
					// line 1310 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 350:
					// line 1313 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 351:
					// line 1316 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 352:
					// line 1319 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(((BlockArgNode)yyVals[0+yyTop])), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 353:
					// line 1323 "Ruby19Parser.y"
  {
    /* was $$ = null;*/
                   yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 354:
					// line 1327 "Ruby19Parser.y"
  {
                    lexer.commandStart = true;
                    yyVal = ((ArgsNode)yyVals[0+yyTop]);
                }
  break;
case 355:
					// line 1332 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 356:
					// line 1335 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 357:
					// line 1338 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                }
  break;
case 359:
					// line 1344 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 360:
					// line 1349 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 361:
					// line 1352 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 362:
					// line 1356 "Ruby19Parser.y"
  {
                    support.new_bv(((Token)yyVals[0+yyTop]));
                }
  break;
case 363:
					// line 1359 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 364:
					// line 1363 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                    yyVal = lexer.getLeftParenBegin();
                    lexer.setLeftParenBegin(lexer.incrementParenNest());
                }
  break;
case 365:
					// line 1367 "Ruby19Parser.y"
  {
                    yyVal = new LambdaNode(support.union(((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                    lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
                }
  break;
case 366:
					// line 1373 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-2+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop])));
                }
  break;
case 367:
					// line 1377 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 368:
					// line 1382 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 369:
					// line 1385 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 370:
					// line 1389 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 371:
					// line 1391 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                }
  break;
case 372:
					// line 1396 "Ruby19Parser.y"
  {
                    if (((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                        throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, getPosition(((Node)yyVals[-1+yyTop])), "Both block arg and actual block given.");
                    }
                    yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                    ((Node)yyVal).setPosition(support.union(((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop])));
                }
  break;
case 373:
					// line 1403 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 374:
					// line 1406 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 375:
					// line 1410 "Ruby19Parser.y"
  {
                    yyVal = support.new_fcall(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 376:
					// line 1413 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 377:
					// line 1416 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 378:
					// line 1419 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]), null, null);
                }
  break;
case 379:
					// line 1422 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 380:
					// line 1425 "Ruby19Parser.y"
  {
                    yyVal = support.new_call(((Node)yyVals[-2+yyTop]), new Token("call", ((Node)yyVals[-2+yyTop]).getPosition()), ((Node)yyVals[0+yyTop]), null);
                }
  break;
case 381:
					// line 1428 "Ruby19Parser.y"
  {
                    yyVal = support.new_super(((Node)yyVals[0+yyTop]), ((Token)yyVals[-1+yyTop]));
                }
  break;
case 382:
					// line 1431 "Ruby19Parser.y"
  {
                    yyVal = new ZSuperNode(((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 383:
					// line 1434 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                        yyVal = support.new_fcall(new Token("[]", support.union(((Node)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                    } else {
                        yyVal = support.new_call(((Node)yyVals[-3+yyTop]), new Token("[]", support.union(((Node)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop]))), ((Node)yyVals[-1+yyTop]), null);
                    }
                }
  break;
case 384:
					// line 1442 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 385:
					// line 1444 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(getPosition(((Token)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    support.popCurrentScope();
                }
  break;
case 386:
					// line 1448 "Ruby19Parser.y"
  {
                    support.pushBlockScope();
                }
  break;
case 387:
					// line 1450 "Ruby19Parser.y"
  {
                    yyVal = new IterNode(support.union(((Token)yyVals[-4+yyTop]), ((Token)yyVals[0+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                    ((ISourcePositionHolder)yyVals[-5+yyTop]).setPosition(support.union(((ISourcePositionHolder)yyVals[-5+yyTop]), ((ISourcePositionHolder)yyVal)));
                    support.popCurrentScope();
                }
  break;
case 388:
					// line 1456 "Ruby19Parser.y"
  {
                    yyVal = support.newWhenNode(support.union(((Token)yyVals[-4+yyTop]), support.unwrapNewlineNode(((Node)yyVals[-1+yyTop]))), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 391:
					// line 1462 "Ruby19Parser.y"
  {
                    Node node;
                    if (((Node)yyVals[-3+yyTop]) != null) {
                        node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(getPosition(((Token)yyVals[-5+yyTop])), "$!")), ((Node)yyVals[-1+yyTop]));
                        if (((Node)yyVals[-1+yyTop]) != null) {
                            node.setPosition(support.unwrapNewlineNode(((Node)yyVals[-1+yyTop])).getPosition());
                        }
                    } else {
                        node = ((Node)yyVals[-1+yyTop]);
                    }
                    Node body = node == null ? NilImplicitNode.NIL : node;
                    yyVal = new RescueBodyNode(getPosition(((Token)yyVals[-5+yyTop]), true), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
                }
  break;
case 392:
					// line 1475 "Ruby19Parser.y"
  { yyVal = null; }
  break;
case 393:
					// line 1477 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
                }
  break;
case 394:
					// line 1480 "Ruby19Parser.y"
  {
                    yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
                    if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 396:
					// line 1486 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 398:
					// line 1491 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 401:
					// line 1497 "Ruby19Parser.y"
  {
                    /* FIXME: We may be intern'ing more than once.*/
                    yyVal = new SymbolNode(((Token)yyVals[0+yyTop]).getPosition(), ((String) ((Token)yyVals[0+yyTop]).getValue()).intern());
                }
  break;
case 403:
					// line 1503 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(getPosition(((Node)yyVals[0+yyTop]))).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
                    /*
                    NODE *node = $1;
                    if (!node) {
                        node = NEW_STR(STR_NEW0());
                    } else {
                        node = evstr2dstr(node);
                    }
                    $$ = node;
                    */
                }
  break;
case 404:
					// line 1516 "Ruby19Parser.y"
  {
                    yyVal = new StrNode(((Token)yyVals[-1+yyTop]).getPosition(), ByteList.create((String) ((Token)yyVals[0+yyTop]).getValue()));
                }
  break;
case 405:
					// line 1519 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 406:
					// line 1522 "Ruby19Parser.y"
  {
                    yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 407:
					// line 1526 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);

                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                    int extraLength = ((String) ((Token)yyVals[-2+yyTop]).getValue()).length() - 1;

                    /* We may need to subtract addition offset off of first */
                    /* string fragment (we optimistically take one off in*/
                    /* ParserSupport.literal_concat).  Check token length*/
                    /* and subtract as neeeded.*/
                    if ((((Node)yyVals[-1+yyTop]) instanceof DStrNode) && extraLength > 0) {
                      Node strNode = ((DStrNode)((Node)yyVals[-1+yyTop])).get(0);
                      assert strNode != null;
                      strNode.getPosition().adjustStartOffset(-extraLength);
                    }
                }
  break;
case 408:
					// line 1543 "Ruby19Parser.y"
  {
                    ISourcePosition position = support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop]));

                    if (((Node)yyVals[-1+yyTop]) == null) {
                        yyVal = new XStrNode(position, null);
                    } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                        yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
                    } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                        yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));

                        ((Node)yyVal).setPosition(position);
                    } else {
                        yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
                    }
                }
  break;
case 409:
					// line 1559 "Ruby19Parser.y"
  {
                    int options = ((RegexpNode)yyVals[0+yyTop]).getOptions();
                    Node node = ((Node)yyVals[-1+yyTop]);

                    if (node == null) {
                        yyVal = new RegexpNode(getPosition(((Token)yyVals[-2+yyTop])), ByteList.create(""), options & ~ReOptions.RE_OPTION_ONCE);
                    } else if (node instanceof StrNode) {
                        yyVal = new RegexpNode(((Node)yyVals[-1+yyTop]).getPosition(), (ByteList) ((StrNode) node).getValue().clone(), options & ~ReOptions.RE_OPTION_ONCE);
                    } else if (node instanceof DStrNode) {
                        yyVal = new DRegexpNode(getPosition(((Token)yyVals[-2+yyTop])), (DStrNode) node, options, (options & ReOptions.RE_OPTION_ONCE) != 0);
                    } else {
                        yyVal = new DRegexpNode(getPosition(((Token)yyVals[-2+yyTop])), options, (options & ReOptions.RE_OPTION_ONCE) != 0).add(node);
                    }
                }
  break;
case 410:
					// line 1574 "Ruby19Parser.y"
  {
                    yyVal = new ZArrayNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 411:
					// line 1577 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-1+yyTop]);
                }
  break;
case 412:
					// line 1581 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null));
                }
  break;
case 413:
					// line 1584 "Ruby19Parser.y"
  {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(getPosition(((ListNode)yyVals[-2+yyTop]))).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
                }
  break;
case 415:
					// line 1589 "Ruby19Parser.y"
  {
                     yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 416:
					// line 1593 "Ruby19Parser.y"
  {
                     yyVal = new ZArrayNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 417:
					// line 1596 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-1+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 418:
					// line 1601 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null));
                }
  break;
case 419:
					// line 1604 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
                }
  break;
case 420:
					// line 1608 "Ruby19Parser.y"
  {
                    yyVal = new StrNode(((Token)yyVals[0+yyTop]).getPosition(), ByteList.create(""));
                }
  break;
case 421:
					// line 1611 "Ruby19Parser.y"
  {
                    yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 422:
					// line 1615 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 423:
					// line 1618 "Ruby19Parser.y"
  {
                    yyVal = support.literal_concat(getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 424:
					// line 1622 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 425:
					// line 1625 "Ruby19Parser.y"
  {
                    yyVal = lexer.getStrTerm();
                    lexer.setStrTerm(null);
                    lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 426:
					// line 1629 "Ruby19Parser.y"
  {
                    lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
                    yyVal = new EvStrNode(support.union(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
                }
  break;
case 427:
					// line 1633 "Ruby19Parser.y"
  {
                   yyVal = lexer.getStrTerm();
                   lexer.setStrTerm(null);
                   lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 428:
					// line 1637 "Ruby19Parser.y"
  {
                   lexer.setStrTerm(((StrTerm)yyVals[-2+yyTop]));

                   yyVal = support.newEvStrNode(support.union(((Token)yyVals[-3+yyTop]), ((Token)yyVals[0+yyTop])), ((Node)yyVals[-1+yyTop]));
                }
  break;
case 429:
					// line 1643 "Ruby19Parser.y"
  {
                     yyVal = new GlobalVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 430:
					// line 1646 "Ruby19Parser.y"
  {
                     yyVal = new InstVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 431:
					// line 1649 "Ruby19Parser.y"
  {
                     yyVal = new ClassVarNode(((Token)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
                }
  break;
case 433:
					// line 1655 "Ruby19Parser.y"
  {
                     lexer.setState(LexState.EXPR_END);
                     yyVal = ((Token)yyVals[0+yyTop]);
                     ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])));
                }
  break;
case 438:
					// line 1664 "Ruby19Parser.y"
  {
                     lexer.setState(LexState.EXPR_END);

                     /* DStrNode: :"some text #{some expression}"*/
                     /* StrNode: :"some text"*/
                     /* EvStrNode :"#{some expression}"*/
                     if (((Node)yyVals[-1+yyTop]) == null) {
                       yyerror("empty symbol literal");
                     }
                     /* FIXME: No node here seems to be an empty string
                        instead of an error
                        if (!($$ = $2)) {
                        $$ = NEW_LIT(ID2SYM(rb_intern("")));
                        }
                     */

                     if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                         yyVal = new DSymbolNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])), ((DStrNode)yyVals[-1+yyTop]));
                     } else {
                       ISourcePosition position = support.union(((Node)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop]));

                       /* We substract one since tsymbeg is longer than one*/
                       /* and we cannot union it directly so we assume quote*/
                       /* is one character long and subtract for it.*/
                       position.adjustStartOffset(-1);
                       ((Node)yyVals[-1+yyTop]).setPosition(position);

                       yyVal = new DSymbolNode(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                       ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
                     }
                }
  break;
case 439:
					// line 1696 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 440:
					// line 1699 "Ruby19Parser.y"
  {
                     yyVal = ((FloatNode)yyVals[0+yyTop]);
                }
  break;
case 441:
					// line 1702 "Ruby19Parser.y"
  {
                     yyVal = support.negateInteger(((Node)yyVals[0+yyTop]));
                }
  break;
case 442:
					// line 1705 "Ruby19Parser.y"
  {
                     yyVal = support.negateFloat(((FloatNode)yyVals[0+yyTop]));
                }
  break;
case 448:
					// line 1710 "Ruby19Parser.y"
  { 
                    yyVal = new Token("nil", Tokens.kNIL, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 449:
					// line 1713 "Ruby19Parser.y"
  {
                    yyVal = new Token("self", Tokens.kSELF, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 450:
					// line 1716 "Ruby19Parser.y"
  { 
                    yyVal = new Token("true", Tokens.kTRUE, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 451:
					// line 1719 "Ruby19Parser.y"
  {
                    yyVal = new Token("false", Tokens.kFALSE, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 452:
					// line 1722 "Ruby19Parser.y"
  {
                    yyVal = new Token("__FILE__", Tokens.k__FILE__, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 453:
					// line 1725 "Ruby19Parser.y"
  {
                    yyVal = new Token("__LINE__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 454:
					// line 1728 "Ruby19Parser.y"
  {
                    yyVal = new Token("__ENCODING__", Tokens.k__LINE__, ((Token)yyVals[0+yyTop]).getPosition());
                }
  break;
case 455:
					// line 1732 "Ruby19Parser.y"
  {
                    yyVal = support.gettable(((Token)yyVals[0+yyTop]));
                }
  break;
case 456:
					// line 1736 "Ruby19Parser.y"
  {
                    yyVal = support.assignable(((Token)yyVals[0+yyTop]), NilImplicitNode.NIL);
                }
  break;
case 457:
					// line 1740 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 458:
					// line 1743 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 459:
					// line 1747 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 460:
					// line 1750 "Ruby19Parser.y"
  {
                   lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 461:
					// line 1752 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 462:
					// line 1755 "Ruby19Parser.y"
  {
                   yyerrok();
                   yyVal = null;
                }
  break;
case 463:
					// line 1761 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                    ((ISourcePositionHolder)yyVal).setPosition(support.union(((Token)yyVals[-2+yyTop]), ((Token)yyVals[0+yyTop])));
                    lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 464:
					// line 1766 "Ruby19Parser.y"
  {
                    yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                }
  break;
case 465:
					// line 1770 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 466:
					// line 1773 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-7+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 467:
					// line 1776 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 468:
					// line 1779 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 469:
					// line 1782 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 470:
					// line 1785 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 471:
					// line 1788 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 472:
					// line 1791 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 473:
					// line 1794 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-5+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 474:
					// line 1797 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 475:
					// line 1800 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((ListNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 476:
					// line 1803 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 477:
					// line 1806 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(support.union(((RestArgNode)yyVals[-3+yyTop]), ((BlockArgNode)yyVals[0+yyTop])), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 478:
					// line 1809 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((BlockArgNode)yyVals[0+yyTop]));
                }
  break;
case 479:
					// line 1812 "Ruby19Parser.y"
  {
                    yyVal = support.new_args(getPosition(null), null, null, null, null, null);
                }
  break;
case 480:
					// line 1816 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be a constant");
                }
  break;
case 481:
					// line 1819 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be an instance variable");
                }
  break;
case 482:
					// line 1822 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be a global variable");
                }
  break;
case 483:
					// line 1825 "Ruby19Parser.y"
  {
                    yyerror("formal argument cannot be a class variable");
                }
  break;
case 485:
					// line 1831 "Ruby19Parser.y"
  {
    /* FIXME: Resolve what the hell is going on*/
    /*                    if (support.is_local_id($1)) {
                        yyerror("formal argument must be local variable");
                        }*/
                     
                    support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 486:
					// line 1841 "Ruby19Parser.y"
  {
                    support.arg_var(((Token)yyVals[0+yyTop]));
                    yyVal = new ArgumentNode(((ISourcePositionHolder)yyVals[0+yyTop]).getPosition(), (String) ((Token)yyVals[0+yyTop]).getValue());
  /*
                    $$ = new ArgAuxiliaryNode($1.getPosition(), (String) $1.getValue(), 1);
  */
                }
  break;
case 487:
					// line 1848 "Ruby19Parser.y"
  {
                    yyVal = ((Node)yyVals[-1+yyTop]);
                    /*		    {
			ID tid = internal_id();
			arg_var(tid);
			if (dyna_in_block()) {
			    $2->nd_value = NEW_DVAR(tid);
			}
			else {
			    $2->nd_value = NEW_LVAR(tid);
			}
			$$ = NEW_ARGS_AUX(tid, 1);
			$$->nd_next = $2;*/
                }
  break;
case 488:
					// line 1863 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null), ((Node)yyVals[0+yyTop]));
                }
  break;
case 489:
					// line 1866 "Ruby19Parser.y"
  {
                    ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                    yyVal = ((ListNode)yyVals[-2+yyTop]);
                }
  break;
case 490:
					// line 1871 "Ruby19Parser.y"
  {
                    if (!support.is_local_id(((Token)yyVals[-2+yyTop]))) {
                        yyerror("formal argument must be local variable");
                    }
                    support.shadowing_lvar(((Token)yyVals[-2+yyTop]));
                    support.arg_var(((Token)yyVals[-2+yyTop]));
                    yyVal = new OptArgNode(getPosition(((Token)yyVals[-2+yyTop])), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 491:
					// line 1880 "Ruby19Parser.y"
  {
                    if (!support.is_local_id(((Token)yyVals[-2+yyTop]))) {
                        yyerror("formal argument must be local variable");
                    }
                    support.shadowing_lvar(((Token)yyVals[-2+yyTop]));
                    support.arg_var(((Token)yyVals[-2+yyTop]));
                    yyVal = new OptArgNode(getPosition(((Token)yyVals[-2+yyTop])), support.assignable(((Token)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
                }
  break;
case 492:
					// line 1889 "Ruby19Parser.y"
  {
                    yyVal = new BlockNode(getPosition(((Node)yyVals[0+yyTop]))).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 493:
					// line 1892 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 494:
					// line 1896 "Ruby19Parser.y"
  {
                    yyVal = new BlockNode(getPosition(((Node)yyVals[0+yyTop]))).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 495:
					// line 1899 "Ruby19Parser.y"
  {
                    yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                }
  break;
case 498:
					// line 1905 "Ruby19Parser.y"
  {
                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
                        yyerror("duplicate rest argument name");
                    }
                    support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                    yyVal = new RestArgNode(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), (String) ((Token)yyVals[0+yyTop]).getValue(), support.arg_var(((Token)yyVals[0+yyTop])));
                }
  break;
case 499:
					// line 1912 "Ruby19Parser.y"
  {
                    yyVal = new UnnamedRestArgNode(((Token)yyVals[0+yyTop]).getPosition(), support.getCurrentScope().getLocalScope().addVariable("*"));
                }
  break;
case 502:
					// line 1919 "Ruby19Parser.y"
  {
                    String identifier = (String) ((Token)yyVals[0+yyTop]).getValue();

                    if (!support.is_local_id(((Token)yyVals[0+yyTop]))) {
                        yyerror("block argument must be local variable");
                    }
                    support.shadowing_lvar(((Token)yyVals[0+yyTop]));
                    yyVal = new BlockArgNode(support.union(((Token)yyVals[-1+yyTop]), ((Token)yyVals[0+yyTop])), support.arg_var(((Token)yyVals[0+yyTop])), identifier);
                }
  break;
case 503:
					// line 1929 "Ruby19Parser.y"
  {
                    yyVal = ((BlockArgNode)yyVals[0+yyTop]);
                }
  break;
case 504:
					// line 1932 "Ruby19Parser.y"
  {
                    yyVal = null;
                }
  break;
case 505:
					// line 1936 "Ruby19Parser.y"
  {
                    if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
                        support.checkExpression(((Node)yyVals[0+yyTop]));
                    }
                    yyVal = ((Node)yyVals[0+yyTop]);
                }
  break;
case 506:
					// line 1942 "Ruby19Parser.y"
  {
                    lexer.setState(LexState.EXPR_BEG);
                }
  break;
case 507:
					// line 1944 "Ruby19Parser.y"
  {
                    if (((Node)yyVals[-1+yyTop]) == null) {
                        yyerror("can't define single method for ().");
                    } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
                        yyerror("can't define single method for literals.");
                    }
                    support.checkExpression(((Node)yyVals[-1+yyTop]));
                    yyVal = ((Node)yyVals[-1+yyTop]);
                }
  break;
case 508:
					// line 1954 "Ruby19Parser.y"
  {
                    yyVal = new ArrayNode(getPosition(null));
                }
  break;
case 509:
					// line 1957 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-1+yyTop]);
                }
  break;
case 511:
					// line 1962 "Ruby19Parser.y"
  {
                    yyVal = ((ListNode)yyVals[-2+yyTop]).addAll(((ListNode)yyVals[0+yyTop]));
                }
  break;
case 512:
					// line 1966 "Ruby19Parser.y"
  {
                    ISourcePosition position;
                    if (((Node)yyVals[-2+yyTop]) == null && ((Node)yyVals[0+yyTop]) == null) {
                        position = getPosition(((Token)yyVals[-1+yyTop]));
                    } else {
                        position = support.union(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                    }

                    yyVal = support.newArrayNode(position, ((Node)yyVals[-2+yyTop])).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 513:
					// line 1976 "Ruby19Parser.y"
  {
                    yyVal = support.newArrayNode(support.union(((Token)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])), new SymbolNode(getPosition(((Token)yyVals[-1+yyTop])), (String) ((Token)yyVals[-1+yyTop]).getValue())).add(((Node)yyVals[0+yyTop]));
                }
  break;
case 530:
					// line 1986 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 531:
					// line 1989 "Ruby19Parser.y"
  {
                    yyVal = ((Token)yyVals[0+yyTop]);
                }
  break;
case 535:
					// line 1994 "Ruby19Parser.y"
  {
                      yyerrok();
                }
  break;
case 538:
					// line 2000 "Ruby19Parser.y"
  {
                      yyerrok();
                }
  break;
case 539:
					// line 2004 "Ruby19Parser.y"
  {
                      yyVal = null;
                }
  break;
case 540:
					// line 2008 "Ruby19Parser.y"
  {  
                  yyVal = null;
                }
  break;
					// line 8029 "-"
        }
        yyTop -= yyLen[yyN];
        yyState = yyStates[yyTop];
        int yyM = yyLhs[yyN];
        if (yyState == 0 && yyM == 0) {
          yyState = yyFinal;
          if (yyToken < 0) {
            yyToken = yyLex.advance() ? yyLex.token() : 0;
          }
          if (yyToken == 0) {
            return yyVal;
          }
          continue yyLoop;
        }
        if ((yyN = yyGindex[yyM]) != 0 && (yyN += yyState) >= 0
            && yyN < yyTable.length && yyCheck[yyN] == yyState)
          yyState = yyTable[yyN];
        else
          yyState = yyDgoto[yyM];
        continue yyLoop;
      }
    }
  }

					// line 2013 "Ruby19Parser.y"

    /** The parse method use an lexer stream and parse it to an AST node 
     * structure
     */
    public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) {
        support.reset();
        support.setConfiguration(configuration);
        support.setResult(new RubyParserResult());
        
        lexer.reset();
        lexer.setSource(source);
        try {
   //yyparse(lexer, new jay.yydebug.yyAnim("JRuby", 9));
    //yyparse(lexer, new jay.yydebug.yyDebugAdapter());
            yyparse(lexer, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (yyException e) {
            e.printStackTrace();
        }
        
        return support.getResult();
    }

    // +++
    // Helper Methods
    
    void yyerrok() {}

    /**
     * Since we can recieve positions at times we know can be null we
     * need an extra safety net here.
     */
    private ISourcePosition getPosition2(ISourcePositionHolder pos) {
        return pos == null ? lexer.getPosition(null, false) : pos.getPosition();
    }

    private ISourcePosition getPosition(ISourcePositionHolder start) {
        return getPosition(start, false);
    }

    private ISourcePosition getPosition(ISourcePositionHolder start, boolean inclusive) {
        if (start != null) {
            return lexer.getPosition(start.getPosition(), inclusive);
        } 

        return lexer.getPosition(null, inclusive);
    }
}
*****************************************
*****************************************
SATD id: 134		Size: 6
     * FIXME: Should this be renamed to match its ruby name?
     */
    @JRubyMethod(name = "object_id", name2 = "__id__", module = true)
    public synchronized IRubyObject id() {
        return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
    }
*****************************************
*****************************************
SATD id: 135		Size: 34

/*
 * Fixme:  This does not have exact same semantics as RubyArray.join, but they
 * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
 */
public RubyString join(IRubyObject[] args) {
  boolean isTainted = false;
StringBuffer buffer = new StringBuffer();

for (int i = 0; i < args.length; i++) {
  if (args[i].isTaint()) {
    isTainted = true;
  }
  String element;
  if (args[i] instanceof RubyString) {
    element = args[i].toString();
  } else if (args[i] instanceof RubyArray) {
    // Fixme: Need infinite recursion check to put [...] and not go into a loop
    element = join(((RubyArray) args[i]).toJavaArray()).toString();
  } else {
    element = args[i].convertToString().toString();
  }

  chomp(buffer);
  if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
    buffer.append("/");
  }
  buffer.append(element);
}

    RubyString fixedStr = RubyString.newString(getRuntime(), buffer.toString());
    fixedStr.setTaint(isTainted);
    return fixedStr;
}
*****************************************
*****************************************
SATD id: 136		Size: 12

/** rb_ary_push_m
 * FIXME: Whis is this named "push_m"?
 */
@JRubyMethod(name = "push", required = 1, rest = true)
public RubyArray push_m(IRubyObject[] items) {
    for (int i = 0; i < items.length; i++) {
        append(items[i]);
    }

    return this;
}
*****************************************
*****************************************
SATD id: 137		Size: 14
        public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
            // shouldn't happen, but if a superclass is specified, it's not ours
            if (superClazz != null) {
                return null;
            }
            IRubyObject packageName = pkg.getInstanceVariable("@package_name");
            // again, shouldn't happen. TODO: might want to throw exception instead.
            if (packageName == null) return null;

            Ruby runtime = pkg.getRuntime();
            String className = packageName.asSymbol() + name;
            JavaClass javaClass = JavaClass.forName(runtime, className);
            return (RubyClass)get_proxy_class(runtime.getJavaSupport().getJavaUtilitiesModule(), javaClass);
        }
*****************************************
*****************************************
SATD id: 138		Size: 1
    public void endLoop(IR_Loop l) { _loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */ }
*****************************************
*****************************************
SATD id: 139		Size: 0
*****************************************
*****************************************
SATD id: 14		Size: 11
    public static RubyClass createClass(IRuby runtime, RubyClass baseClass) {
        // FIXME: If NativeException is expected to be used from Ruby code, it should provide
        // a real allocator to be used. Otherwise Class.new will fail, as will marshalling. JRUBY-415
    	RubyClass exceptionClass = runtime.defineClass(CLASS_NAME, baseClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
    	
		CallbackFactory callbackFactory = runtime.callbackFactory(NativeException.class);
		exceptionClass.defineMethod("cause", 
				callbackFactory.getMethod("cause"));		

		return exceptionClass;
    }
*****************************************
*****************************************
SATD id: 140		Size: 57
    public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
        DateTimeZone cachedZone = runtime.getTimezoneCache().get(zone);

        if (cachedZone != null) return cachedZone;

        String originalZone = zone;

        // Value of "TZ" property is of a bit different format,
        // which confuses the Java's TimeZone.getTimeZone(id) method,
        // and so, we need to convert it.

        Matcher tzMatcher = TZ_PATTERN.matcher(zone);
        if (tzMatcher.matches()) {                    
            String sign = tzMatcher.group(2);
            String hours = tzMatcher.group(3);
            String minutes = tzMatcher.group(4);
                
            // GMT+00:00 --> Etc/GMT, see "MRI behavior"
            // comment below.
            if (("00".equals(hours) || "0".equals(hours))
                    && (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
                zone = "Etc/GMT";
            } else {
                // Invert the sign, since TZ format and Java format
                // use opposite signs, sigh... Also, Java API requires
                // the sign to be always present, be it "+" or "-".
                sign = ("-".equals(sign)? "+" : "-");

                // Always use "GMT" since that's required by Java API.
                zone = "GMT" + sign + hours;

                if (minutes != null) {
                    zone += minutes;
                }
            }
        }

        // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
        // is *NOT* considered as a proper GMT/UTC time:
        //   ENV['TZ']="GMT"
        //   Time.now.gmt? ==> false
        //   ENV['TZ']="UTC"
        //   Time.now.utc? ==> false
        // Hence, we need to adjust for that.
        if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
            zone = "Etc/" + zone;
        }

        // For JRUBY-2759, when MET choose CET timezone to work around Joda
        if ("MET".equalsIgnoreCase(zone)) {
            zone = "CET";
        }

        DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
        runtime.getTimezoneCache().put(originalZone, dtz);
        return dtz;
    }
*****************************************
*****************************************
SATD id: 15		Size: 4
    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
        return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
    }
*****************************************
*****************************************
SATD id: 16		Size: 4
    protected IRubyObject constantTableStore(String name, IRubyObject value) {
        // FIXME: legal here? may want UnsupportedOperationException
        return delegate.constantTableStore(name, value);
    }
*****************************************
*****************************************
SATD id: 17		Size: 127
public abstract class BlockBody implements JumpTarget {
    // FIXME: Maybe not best place, but move it to a good home
    public static final int ZERO_ARGS = 0;
    public static final int MULTIPLE_ASSIGNMENT = 1;
    public static final int ARRAY = 2;
    public static final int SINGLE_RESTARG = 3;
    protected final int argumentType;
    
    public BlockBody(int argumentType) {
        this.argumentType = argumentType;
    }
    
    public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
        args = prepareArgumentsForCall(context, args, type);

        return yield(context, RubyArray.newArrayNoCopy(context.getRuntime(), args), null, null, true, binding, type);
    }

    // This should only be called by 1.8 (1.9 subclasses this to handle unusedBlock).
    public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, 
            Block.Type type, Block unusedBlock) {
        return call(context, args, binding, type);
    }

    public abstract IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type);

    public abstract IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
            RubyModule klass, boolean aValue, Binding binding, Block.Type type);
    
    public int getArgumentType() {
        return argumentType;
    }

<%= generated_arities %>
    
    public abstract StaticScope getStaticScope();

    public abstract Block cloneBlock(Binding binding);

    /**
     * What is the arity of this block?
     * 
     * @return the arity
     */
    public abstract Arity arity();
    
    /**
     * Is the current block a real yield'able block instead a null one
     * 
     * @return true if this is a valid block or false otherwise
     */
    public boolean isGiven() {
        return true;
    }
    
    /**
     * Compiled codes way of examining arguments
     * 
     * @param nodeId to be considered
     * @return something not linked to AST and a constant to make compiler happy
     */
    public static int asArgumentType(NodeType nodeId) {
        if (nodeId == null) return ZERO_ARGS;
        
        switch (nodeId) {
        case ZEROARGNODE: return ZERO_ARGS;
        case MULTIPLEASGNNODE: return MULTIPLE_ASSIGNMENT;
        case SVALUENODE: return SINGLE_RESTARG;
        }
        return ARRAY;
    }
    
    public IRubyObject[] prepareArgumentsForCall(ThreadContext context, IRubyObject[] args, Block.Type type) {
        switch (type) {
        case NORMAL: {
//            assert false : "can this happen?";
            if (args.length == 1 && args[0] instanceof RubyArray) {
                if (argumentType == MULTIPLE_ASSIGNMENT || argumentType == SINGLE_RESTARG) {
                    args = ((RubyArray) args[0]).toJavaArray();
                }
                break;
            }
        }
        case PROC: {
            if (args.length == 1 && args[0] instanceof RubyArray) {
                if (argumentType == MULTIPLE_ASSIGNMENT && argumentType != SINGLE_RESTARG) {
                    args = ((RubyArray) args[0]).toJavaArray();
                }
            }
            break;
        }
        case LAMBDA:
            if (argumentType == ARRAY && args.length != 1) {
                context.getRuntime().getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" + args.length + " for " + arity().getValue() + ")");
                if (args.length == 0) {
                    args = context.getRuntime().getSingleNilArray();
                } else {
                    args = new IRubyObject[] {context.getRuntime().newArrayNoCopy(args)};
                }
            } else {
                arity().checkArity(context.getRuntime(), args);
            }
            break;
        }
        
        return args;
    }
    
    public static NodeType getArgumentTypeWackyHack(IterNode iterNode) {
        NodeType argsNodeId = null;
        if (iterNode.getVarNode() != null && iterNode.getVarNode().getNodeType() != NodeType.ZEROARGNODE) {
            // if we have multiple asgn with just *args, need a special type for that
            argsNodeId = iterNode.getVarNode().getNodeType();
            if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
                MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
                if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
                    // FIXME: This is gross. Don't do this.
                    argsNodeId = NodeType.SVALUENODE;
                }
            }
        }
        
        return argsNodeId;
    }

    public static final BlockBody NULL_BODY = new NullBlockBody();
}
*****************************************
*****************************************
SATD id: 18		Size: 24
    private final ConcurrentHashMap<Class,JavaClass> javaClassCache =
        new ConcurrentHashMap<Class,JavaClass>(128);
    
    // FIXME: needs to be rethought
    private final Map matchCache = Collections.synchronizedMap(new HashMap(128));

    private Callback concreteProxyCallback;

    private RubyModule javaModule;
    private RubyModule javaUtilitiesModule;
    private RubyClass javaObjectClass;
    private RubyClass javaClassClass;
    private RubyClass javaArrayClass;
    private RubyClass javaProxyClass;
    private RubyModule javaInterfaceTemplate;
    private RubyModule packageModuleTemplate;
    private RubyClass arrayProxyClass;
    private RubyClass concreteProxyClass;
    
    
    public JavaSupport(Ruby ruby) {
        this.runtime = ruby;
        this.javaClassLoader = ruby.getJRubyClassLoader();
    }
*****************************************
*****************************************
SATD id: 19		Size: 10
    public static CallbackFactory createFactory(Ruby runtime, Class type, ClassLoader classLoader) {
        if(reflection) {
            return new ReflectionCallbackFactory(type);
        } else if(dumping) {
            return new DumpingInvocationCallbackFactory(runtime, type, dumpingPath);
        } else {
            // FIXME: No, I don't like it.
            return new InvocationCallbackFactory(runtime, type, (JRubyClassLoader)classLoader);
        }
    }
*****************************************
*****************************************
SATD id: 2		Size: 36
    public IRubyObject invoke(IRubyObject[] args) {
        if (args.length != 1 + getArity()) {
            throw getRuntime().newArgumentError(args.length, 1 + getArity());
        }

        IRubyObject invokee = args[0];
        if (! (invokee instanceof JavaObject)) {
            throw getRuntime().newTypeError("invokee not a java object");
        }
        Object javaInvokee = ((JavaObject) invokee).getValue();
        Object[] arguments = new Object[args.length - 1];
        convertArguments(getRuntime(), arguments, args, 1);

        if (! method.getDeclaringClass().isInstance(javaInvokee)) {
            throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                              "got" + javaInvokee.getClass().getName() + " wanted " +
                                              method.getDeclaringClass().getName() + ")");
        }
        
        //
        // this test really means, that this is a ruby-defined subclass of a java class
        //
        if (javaInvokee instanceof InternalJavaProxy &&
                // don't bother to check if final method, it won't
                // be there (not generated, can't be!)
                !Modifier.isFinal(method.getModifiers())) {
            JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                    .___getProxyClass();
            JavaProxyMethod jpm;
            if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
                    jpm.hasSuperImplementation()) {
                return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
            }
        }
        return invokeWithExceptionHandling(method, javaInvokee, arguments);
    }
*****************************************
*****************************************
SATD id: 20		Size: 11
        public static void setup(Ruby runtime) {
            RubyClass cFiber = runtime.defineClass("Fiber", runtime.getClass("Object"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
            CallbackFactory cb = runtime.callbackFactory(Fiber.class);
            cFiber.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
            cFiber.defineFastMethod("resume", cb.getFastOptMethod("resume"));
            // FIXME: Not sure what the semantics of transfer are
            //cFiber.defineFastMethod("transfer", cb.getFastOptMethod("transfer"));
            cFiber.defineFastMethod("alive?", cb.getFastMethod("alive_p"));
            cFiber.getMetaClass().defineFastMethod("yield", cb.getFastSingletonMethod("yield", IRubyObject.class));
            cFiber.getMetaClass().defineFastMethod("current", cb.getFastSingletonMethod("current"));
        }
*****************************************
*****************************************
SATD id: 21		Size: 21
    public void beginMethod(CompilerCallback argsCallback, StaticScope scope) {
        // fill in all vars with nil so compiler is happy about future accesses
        if (scope.getNumberOfVariables() > 0) {
            // if we don't have opt args, start after args (they will be assigned later)
            // this is for crap like def foo(a = (b = true; 1)) which numbers b before a
            // FIXME: only starting after required args, since opt args may access others
            // and rest args conflicts with compileRoot using "0" to indicate [] signature.
            int start = scope.getRequiredArgs();
            for (int i = start; i < scope.getNumberOfVariables(); i++) {
                methodCompiler.loadNil();
                assignLocalVariable(i, false);
            }

            // temp locals must start after last real local
            tempVariableIndex += scope.getNumberOfVariables();
        }

        if (argsCallback != null) {
            argsCallback.call(methodCompiler);
        }
    }
*****************************************
*****************************************
SATD id: 22		Size: 4
                        public void nextValue(BodyCompiler context, Object object, int index) {
                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
                            context.getVariableCompiler().assignLocalVariable(index, false);
                        }
*****************************************
*****************************************
SATD id: 23		Size: 35

public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject self, IRubyObject src, Binding binding) {
    Ruby runtime = src.getRuntime();
    DynamicScope evalScope;

    // in 1.9, eval scopes are local to the binding
    evalScope = binding.getEvalScope(runtime);

    // FIXME:  This determine module is in a strange location and should somehow be in block
    evalScope.getStaticScope().determineModule();

    Frame lastFrame = context.preEvalWithBinding(binding);
    try {
        // Binding provided for scope, use it
        RubyString source = src.convertToString();
        Node node = runtime.parseEval(source.getByteList(), binding.getFile(), evalScope, binding.getLine());
        Block block = binding.getFrame().getBlock();

        if (runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.TRUFFLE) {
            throw new UnsupportedOperationException();
        }

        // SSS FIXME: AST interpreter passed both a runtime (which comes from the source string)
        // and the thread-context rather than fetch one from the other.  Why is that?
        return Interpreter.interpretBindingEval(runtime, binding.getFile(), binding.getLine(), binding.getMethod(), node, self, block);
    } catch (JumpException.BreakJump bj) {
        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
    } catch (JumpException.RedoJump rj) {
        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
    } catch (StackOverflowError soe) {
        throw runtime.newSystemStackError("stack level too deep", soe);
    } finally {
        context.postEvalWithBinding(binding, lastFrame);
    }
}
*****************************************
*****************************************
SATD id: 24		Size: 23
    private void addConditionalForWhen(final WhenNode whenNode, List<ArgumentsCallback> conditionals, List<CompilerCallback> bodies, CompilerCallback body) {
        bodies.add(body);

        // If it's a single-arg when but contains an array, we know it's a real literal array
        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
        if (whenNode.getExpressionNodes() instanceof ArrayNode) {
            if (whenNode instanceof WhenOneArgNode) {
                // one arg but it's an array, treat it as a proper array
                conditionals.add(new ArgumentsCallback() {
                    public int getArity() {
                        return 1;
                    }

                    public void call(IR_BuilderContext m) {
                        build(whenNode.getExpressionNodes(), m, true);
                    }
                });
                return;
            }
        }
        // otherwise, use normal args buildr
        conditionals.add(getArgsCallback(whenNode.getExpressionNodes()));
    }
*****************************************
*****************************************
SATD id: 25		Size: 75
    public int run(NGContext context) {
        context.assertLoopbackClient();

        // FIXME: This is almost entirely duplicated from Main.java
        RubyInstanceConfig config = new RubyInstanceConfig();
        Main main = new Main(config);
        
        try {
            // populate commandline with NG-provided stuff
            config.processArguments(context.getArgs());
            config.setCurrentDirectory(context.getWorkingDirectory());
            config.setEnvironment(context.getEnv());

            return main.run();
        } catch (MainExitException mee) {
            if (!mee.isAborted()) {
                config.getOutput().println(mee.getMessage());
                if (mee.isUsageError()) {
                    main.printUsage();
                }
            }
            return mee.getStatus();
        } catch (OutOfMemoryError oome) {
            // produce a nicer error since Rubyists aren't used to seeing this
            System.gc();

            String memoryMax = SafePropertyAccessor.getProperty("jruby.memory.max");
            String message = "";
            if (memoryMax != null) {
                message = " of " + memoryMax;
            }
            config.getError().println("Error: Your application used more memory than the safety cap" + message + ".");
            config.getError().println("Specify -J-Xmx####m to increase it (#### = cap size in MB).");

            if (config.getVerbose()) {
                config.getError().println("Exception trace follows:");
                oome.printStackTrace();
            } else {
                config.getError().println("Specify -w for full OutOfMemoryError stack trace");
            }
            return 1;
        } catch (StackOverflowError soe) {
            // produce a nicer error since Rubyists aren't used to seeing this
            System.gc();

            String stackMax = SafePropertyAccessor.getProperty("jruby.stack.max");
            String message = "";
            if (stackMax != null) {
                message = " of " + stackMax;
            }
            config.getError().println("Error: Your application used more stack memory than the safety cap" + message + ".");
            config.getError().println("Specify -J-Xss####k to increase it (#### = cap size in KB).");

            if (config.getVerbose()) {
                config.getError().println("Exception trace follows:");
                soe.printStackTrace();
            } else {
                config.getError().println("Specify -w for full StackOverflowError stack trace");
            }
            return 1;
        } catch (UnsupportedClassVersionError ucve) {
            config.getError().println("Error: Some library (perhaps JRuby) was built with a later JVM version.");
            config.getError().println("Please use libraries built with the version you intend to use or an earlier one.");

            if (config.getVerbose()) {
                config.getError().println("Exception trace follows:");
                ucve.printStackTrace();
            } else {
                config.getError().println("Specify -w for full UnsupportedClassVersionError stack trace");
            }
            return 1;
        } catch (ThreadKill kill) {
            return 0;
        }
    }
*****************************************
*****************************************
SATD id: 26		Size: 31
            public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                ThreadContext context = getRuntime().getCurrentContext();

                Visibility savedVisibility = block.getVisibility();

                block.setVisibility(Visibility.PUBLIC);
                try {
                    IRubyObject valueInYield;
                    boolean aValue;
                    if (args.length == 1) {
                        valueInYield = args[0];
                        aValue = false;
                    } else {
                        valueInYield = RubyArray.newArray(getRuntime(), args);
                        aValue = true;
                    }
                    
                    // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
                    block = block.cloneBlock();
                    block.setSelf(RubyObject.this);
                    block.getFrame().setSelf(RubyObject.this);
                    // end hack
                    
                    return block.yield(context, valueInYield, RubyObject.this, context.getRubyClass(), aValue);
                    //TODO: Should next and return also catch here?
                } catch (JumpException.BreakJump bj) {
                        return (IRubyObject) bj.getValue();
                } finally {
                    block.setVisibility(savedVisibility);
                }
            }
*****************************************
*****************************************
SATD id: 27		Size: 16
    public static NodeType getArgumentTypeWackyHack(IterNode iterNode) {
        NodeType argsNodeId = null;
        if (iterNode.getVarNode() != null && iterNode.getVarNode().getNodeType() != NodeType.ZEROARGNODE) {
            // if we have multiple asgn with just *args, need a special type for that
            argsNodeId = iterNode.getVarNode().getNodeType();
            if (argsNodeId == NodeType.MULTIPLEASGNNODE) {
                MultipleAsgnNode multipleAsgnNode = (MultipleAsgnNode)iterNode.getVarNode();
                if (multipleAsgnNode.getHeadNode() == null && multipleAsgnNode.getArgsNode() != null) {
                    // FIXME: This is gross. Don't do this.
                    argsNodeId = NodeType.SVALUENODE;
                }
            }
        }
        
        return argsNodeId;
    }
*****************************************
*****************************************
SATD id: 28		Size: 18
            public void call(BodyCompiler context) {
                // FIXME: This is temporary since the variable compilers assume we want
                // args already on stack for assignment. We just pop and continue with
                // 1.9 args logic.
                context.consumeCurrentValue(); // args value
                context.consumeCurrentValue(); // passed block
                if (iterNode.getVarNode() != null) {
                    if (iterNode instanceof LambdaNode) {
                        final int required = argsNode.getRequiredArgsCount();
                        final int opt = argsNode.getOptionalArgsCount();
                        final int rest = argsNode.getRestArg();
                        context.getVariableCompiler().checkMethodArity(required, opt, rest);
                        compileMethodArgs(argsNode, context, true);
                    } else {
                        compileMethodArgs(argsNode, context, true);
                    }
                }
            }
*****************************************
*****************************************
SATD id: 29		Size: 9
        public void issueRedoEvent() {
            // FIXME: This isn't right for within ensured/rescued code
            if (currentLoopLabels != null) {
                issueLoopRedo();
            } else {
                // jump back to the top of the main body of this closure
                method.go_to(scopeStart);
            }
        }
*****************************************
*****************************************
SATD id: 3		Size: 31
            public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                ThreadContext context = getRuntime().getCurrentContext();

                Visibility savedVisibility = block.getVisibility();

                block.setVisibility(Visibility.PUBLIC);
                try {
                    IRubyObject valueInYield;
                    boolean aValue;
                    if (args.length == 1) {
                        valueInYield = args[0];
                        aValue = false;
                    } else {
                        valueInYield = RubyArray.newArray(getRuntime(), args);
                        aValue = true;
                    }
                    
                    // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
                    block = block.cloneBlock();
                    block.setSelf(RubyObject.this);
                    block.getFrame().setSelf(RubyObject.this);
                    // end hack
                    
                    return block.yield(context, valueInYield, RubyObject.this, context.getRubyClass(), aValue);
                    //TODO: Should next and return also catch here?
                } catch (JumpException.BreakJump bj) {
                        return (IRubyObject) bj.getValue();
                } finally {
                    block.setVisibility(savedVisibility);
                }
            }
*****************************************
*****************************************
SATD id: 30		Size: 9
        public void issueRedoEvent() {
            // FIXME: This isn't right for within ensured/rescued code
            if (currentLoopLabels != null) {
                issueLoopRedo();
            } else {
                // jump back to the top of the main body of this closure
                method.go_to(scopeStart);
            }
        }
*****************************************
*****************************************
SATD id: 31		Size: 192
        // create static init, for a monitor object
        SkinnyMethodAdapter clinitMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(void.class), null, null));
        clinitMethod.newobj(p(Object.class));
        clinitMethod.dup();
        clinitMethod.invokespecial(p(Object.class), "<init>", sig(void.class));
        clinitMethod.putstatic(pathName, "$monitor", ci(Object.class));
        
        // create constructor
        SkinnyMethodAdapter initMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class, IRubyObject.class), null, null));
        initMethod.aload(0);
        initMethod.invokespecial(p(Object.class), "<init>", sig(void.class));
        
        // store the wrapper
        initMethod.aload(0);
        initMethod.aload(1);
        initMethod.putfield(pathName, "$self", ci(IRubyObject.class));
        
        // end constructor
        initMethod.voidreturn();
        initMethod.end();
        
        // for each simple method name, implement the complex methods, calling the simple version
        for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
            String simpleName = entry.getKey();
            Set<String> nameSet = JavaUtil.getRubyNamesForJavaName(simpleName, entry.getValue());

            // set up a field for the CacheEntry
            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
            cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, simpleName, ci(CacheEntry.class), null, null).visitEnd();
            clinitMethod.getstatic(p(CacheEntry.class), "NULL_CACHE", ci(CacheEntry.class));
            clinitMethod.putstatic(pathName, simpleName, ci(CacheEntry.class));

            Set<String> implementedNames = new HashSet<String>();
            
            for (Method method : entry.getValue()) {
                Class[] paramTypes = method.getParameterTypes();
                Class returnType = method.getReturnType();

                String fullName = simpleName + prettyParams(paramTypes);
                if (implementedNames.contains(fullName)) continue;
                implementedNames.add(fullName);

                // indices for temp values
                int baseIndex = 1;
                for (Class paramType : paramTypes) {
                    if (paramType == double.class || paramType == long.class) {
                        baseIndex += 2;
                    } else {
                        baseIndex += 1;
                    }
                }
                int selfIndex = baseIndex;
                int rubyIndex = selfIndex + 1;
                
                SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                        cw.visitMethod(ACC_PUBLIC, simpleName, sig(returnType, paramTypes), null, null));
                mv.start();
                mv.line(1);
                
                // TODO: this code should really check if a Ruby equals method is implemented or not.
                if(simpleName.equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class && returnType == Boolean.TYPE) {
                    mv.line(2);
                    mv.aload(0);
                    mv.aload(1);
                    mv.invokespecial(p(Object.class), "equals", sig(Boolean.TYPE, params(Object.class)));
                    mv.ireturn();
                } else if(simpleName.equals("hashCode") && paramTypes.length == 0 && returnType == Integer.TYPE) {
                    mv.line(3);
                    mv.aload(0);
                    mv.invokespecial(p(Object.class), "hashCode", sig(Integer.TYPE));
                    mv.ireturn();
                } else if(simpleName.equals("toString") && paramTypes.length == 0 && returnType == String.class) {
                    mv.line(4);
                    mv.aload(0);
                    mv.invokespecial(p(Object.class), "toString", sig(String.class));
                    mv.areturn();
                } else {
                    mv.line(5);
                    
                    Label dispatch = new Label();
                    Label end = new Label();
                    Label recheckMethod = new Label();

                    // prepare temp locals
                    mv.aload(0);
                    mv.getfield(pathName, "$self", ci(IRubyObject.class));
                    mv.astore(selfIndex);
                    mv.aload(selfIndex);
                    mv.invokeinterface(p(IRubyObject.class), "getRuntime", sig(Ruby.class));
                    mv.astore(rubyIndex);

                    // Try to look up field for simple name
                    // get field; if nonnull, go straight to dispatch
                    mv.getstatic(pathName, simpleName, ci(CacheEntry.class));
                    mv.dup();
                    mv.aload(selfIndex);
                    mv.invokestatic(p(MiniJava.class), "isCacheOk", sig(boolean.class, params(CacheEntry.class, IRubyObject.class)));
                    mv.iftrue(dispatch);

                    // field is null, lock class and try to populate
                    mv.line(6);
                    mv.pop();
                    mv.getstatic(pathName, "$monitor", ci(Object.class));
                    mv.monitorenter();

                    // try/finally block to ensure unlock
                    Label tryStart = new Label();
                    Label tryEnd = new Label();
                    Label finallyStart = new Label();
                    Label finallyEnd = new Label();
                    mv.line(7);
                    mv.label(tryStart);

                    mv.aload(selfIndex);
                    for (String eachName : nameSet) {
                        mv.ldc(eachName);
                    }
                    mv.invokestatic(p(MiniJava.class), "searchWithCache", sig(CacheEntry.class, params(IRubyObject.class, String.class, nameSet.size())));

                    // store it
                    mv.putstatic(pathName, simpleName, ci(CacheEntry.class));

                    // all done with lookup attempts, release monitor
                    mv.getstatic(pathName, "$monitor", ci(Object.class));
                    mv.monitorexit();
                    mv.go_to(recheckMethod);

                    // end of try block
                    mv.label(tryEnd);

                    // finally block to release monitor
                    mv.label(finallyStart);
                    mv.line(9);
                    mv.getstatic(pathName, "$monitor", ci(Object.class));
                    mv.monitorexit();
                    mv.label(finallyEnd);
                    mv.athrow();

                    // exception handling for monitor release
                    mv.trycatch(tryStart, tryEnd, finallyStart, null);
                    mv.trycatch(finallyStart, finallyEnd, finallyStart, null);

                    // re-get, re-check method; if not null now, go to dispatch
                    mv.label(recheckMethod);
                    mv.line(10);
                    mv.getstatic(pathName, simpleName, ci(CacheEntry.class));
                    mv.dup();
                    mv.getfield(p(CacheEntry.class), "method", ci(DynamicMethod.class));
                    mv.invokevirtual(p(DynamicMethod.class), "isUndefined", sig(boolean.class));
                    mv.iffalse(dispatch);

                    // method still not available, call method_missing
                    mv.line(11);
                    mv.pop();
                    // exit monitor before making call
                    // FIXME: this not being in a finally is a little worrisome
                    mv.aload(selfIndex);
                    mv.ldc(simpleName);
                    coerceArgumentsToRuby(mv, paramTypes, rubyIndex);
                    mv.invokestatic(p(RuntimeHelpers.class), "invokeMethodMissing", sig(IRubyObject.class, IRubyObject.class, String.class, IRubyObject[].class));
                    mv.go_to(end);
                
                    // perform the dispatch
                    mv.label(dispatch);
                    mv.line(12, dispatch);
                    // get current context
                    mv.getfield(p(CacheEntry.class), "method", ci(DynamicMethod.class));
                    mv.aload(rubyIndex);
                    mv.invokevirtual(p(Ruby.class), "getCurrentContext", sig(ThreadContext.class));
                
                    // load self, class, and name
                    mv.aload(selfIndex);
                    mv.aload(selfIndex);
                    mv.invokeinterface(p(IRubyObject.class), "getMetaClass", sig(RubyClass.class));
                    mv.ldc(simpleName);
                
                    // coerce arguments
                    coerceArgumentsToRuby(mv, paramTypes, rubyIndex);
                
                    // load null block
                    mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
                
                    // invoke method
                    mv.line(13);
                    mv.invokevirtual(p(DynamicMethod.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
                
                    mv.label(end);
                    coerceResultAndReturn(mv, returnType);
                }                
                mv.end();
            }
        }
*****************************************
*****************************************
SATD id: 32		Size: 16
    private void beginInit() {
        ClassVisitor cv = getClassVisitor();

        initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", cg.sig(Void.TYPE), null, null));
        initMethod.start();
        initMethod.aload(THIS);
        initMethod.invokespecial(cg.p(Object.class), "<init>", cg.sig(Void.TYPE));
        
        cv.visitField(ACC_PRIVATE | ACC_FINAL, "$class", cg.ci(Class.class), null, null);
        
        // FIXME: this really ought to be in clinit, but it doesn't matter much
        initMethod.aload(THIS);
        initMethod.ldc(cg.c(classname));
        initMethod.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
        initMethod.putfield(classname, "$class", cg.ci(Class.class));
    }
*****************************************
*****************************************
SATD id: 33		Size: 5
    // FIXME: this should go somewhere more generic -- maybe IdUtil
    public static final boolean isRubyVariable(String name) {
        char c;
        return name.length() > 0 && ((c = name.charAt(0)) == '@' || (c <= 'Z' && c >= 'A'));
    }
*****************************************
*****************************************
SATD id: 34		Size: 20
    private static int parseSignalString(Ruby runtime, String value) {
        int startIndex = 0;
        boolean negative = value.startsWith("-");
        
        if (negative) startIndex++;
        
        boolean signalString = value.startsWith("SIG", startIndex);
        
        if (signalString) startIndex += 3;
       
        String signalName = value.substring(startIndex);
        
        // FIXME: This table will get moved into POSIX library so we can get all actual supported
        // signals.  This is a quick fix to support basic signals until that happens.
        for (int i = 0; i < signals.length; i++) {
            if (signals[i].equals(signalName)) return negative ? -i : i;
        }
        
        throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
    }
*****************************************
*****************************************
SATD id: 35		Size: 16
    public synchronized void ftruncate(long newLength) throws IOException {
        invalidateBuffer();
        FileChannel fileChannel = (FileChannel)descriptor.getChannel();
        if (newLength > fileChannel.size()) {
            // truncate can't lengthen files, so we save position, seek/write, and go back
            long position = fileChannel.position();
            int difference = (int)(newLength - fileChannel.size());
            
            fileChannel.position(fileChannel.size());
            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
            fileChannel.write(ByteBuffer.allocate(difference));
            fileChannel.position(position);
        } else {
            fileChannel.truncate(newLength);
        }        
    }
*****************************************
*****************************************
SATD id: 36		Size: 13
            // FIXME: we should also support orgs that use capitalized package
            // names (including, embarrassingly, the one I work for), but this
            // should be enabled by a system property, as the expected default
            // behavior for an upper-case value should be (and is) to treat it
            // as a class name, and raise an exception if it's not found 
            
//            try {
//                return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
//            } catch (Exception e) {
//                // but for those not hip to conventions and best practices,
//                // we'll try as a package
//                return getJavaPackageModule(runtime, fullName);
//            }
*****************************************
*****************************************
SATD id: 37		Size: 5
    public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
        // FIXME: We should be getting this from the runtime rather than assume none?
        //return runtime.getEncodingService().getEncoding(runtime.getDefaultExternalEncoding());
        return runtime.getEncodingService().getEncoding(KCode.NONE.getEncoding());
    }
*****************************************
*****************************************
SATD id: 38		Size: 11
    public DynamicScope getScope(String file) {
        if (asBlock) return existingScope;

        // FIXME: We should really not be creating the dynamic scope for the root
        // of the AST before parsing.  This makes us end up needing to readjust
        // this dynamic scope coming out of parse (and for local static scopes it
        // will always happen because of $~ and $_).
        // FIXME: Because we end up adjusting this after-the-fact, we can't use
        // any of the specific-size scopes.
        return new ManyVarsDynamicScope(staticScopeFactory.newLocalScope(null, file), existingScope);
    }
*****************************************
*****************************************
SATD id: 39		Size: 6
        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
        if (iter.getBlockBody() instanceof InterpretedBlock) {
            return InterpretedBlock.newInterpretedClosure(context, iter.getBlockBody(), self);
        } else {
            return Interpreted19Block.newInterpretedClosure(context, iter.getBlockBody(), self);
        }
*****************************************
*****************************************
SATD id: 4		Size: 4
    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
    public boolean is_local_id(String name) {
        return lexer.isIdentifierChar(name.charAt(0));
    }
*****************************************
*****************************************
SATD id: 40		Size: 25
            public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                String methodName = method.getName();
                int length = nargs == null ? 0 : nargs.length;

                // FIXME: wtf is this? Why would these use the class?
                if (methodName == "toString" && length == 0) {
                    return proxy.getClass().getName();
                } else if (methodName == "hashCode" && length == 0) {
                    return new Integer(proxy.getClass().hashCode());
                } else if (methodName == "equals" && length == 1) {
                    Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                    if (parameterTypes == null) {
                        parameterTypes = method.getParameterTypes();
                        parameterTypeCache.put(method, parameterTypes);
                    }
                    if (parameterTypes[0].equals(Object.class)) {
                        return Boolean.valueOf(proxy == nargs[0]);
                    }
                }
                
                IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(runtime, nargs);
                try {
                    return JavaUtil.convertRubyToJava(RuntimeHelpers.invoke(runtime.getCurrentContext(), wrapper, methodName, rubyArgs), method.getReturnType());
                } catch (RuntimeException e) { e.printStackTrace(); throw e; }
            }
*****************************************
*****************************************
SATD id: 41		Size: 6
    public InvokeDynamicInvocationCompiler(BaseBodyCompiler methodCompiler, SkinnyMethodAdapter method) {
        super(methodCompiler, method);

        // HACK: force clinit to be created
        methodCompiler.getScriptCompiler().getClassInitMethod();
    }
*****************************************
*****************************************
SATD id: 42		Size: 61
    private int hereDocumentIdentifier() throws IOException {
        int c = src.read(); 
        int term;

        int func = 0;
        if (c == '-') {
            c = src.read();
            func = STR_FUNC_INDENT;
        }
        
        ByteList markerValue;
        if (c == '\'' || c == '"' || c == '`') {
            if (c == '\'') {
                func |= str_squote;
            } else if (c == '"') {
                func |= str_dquote;
            } else {
                func |= str_xquote; 
            }

            markerValue = new ByteList();
            term = c;
            while ((c = src.read()) != EOF && c != term) {
                markerValue.append(c);
            }
            if (c == EOF) {
                throw new SyntaxException(SyntaxException.PID.STRING_MARKER_MISSING, getPosition(), 
                        getCurrentLine(), "unterminated here document identifier");
            }	
        } else {
            if (!isIdentifierChar(c)) {
                src.unread(c);
                if ((func & STR_FUNC_INDENT) != 0) {
                    src.unread('-');
                }
                return 0;
            }
            markerValue = new ByteList();
            term = '"';
            func |= str_dquote;
            do {
                markerValue.append(c);
            } while ((c = src.read()) != EOF && isIdentifierChar(c));

            src.unread(c);
        }

        ByteList lastLine = src.readLineBytes();
        lastLine.append('\n');
        lex_strterm = new HeredocTerm(markerValue, func, lastLine);

        if (term == '`') {
            yaccValue = new Token("`", getPosition());
            return Tokens.tXSTRING_BEG;
        }
        
        yaccValue = new Token("\"", getPosition());
        // Hacky: Advance position to eat newline here....
        getPosition();
        return Tokens.tSTRING_BEG;
    }
*****************************************
*****************************************
SATD id: 43		Size: 46
    public RootParseNode parse(String file, byte[] content, DynamicScope blockScope,
                           ParserConfiguration configuration) {
        List<ByteList> list = null;
        ByteList in = new ByteList(content, configuration.getDefaultEncoding());
        LexerSource lexerSource = new ByteListLexerSource(file, configuration.getLineNumber(), in,  list);
        // We only need to pass in current scope if we are evaluating as a block (which
        // is only done for evals).  We need to pass this in so that we can appropriately scope
        // down to captured scopes when we are parsing.
        if (blockScope != null) {
            configuration.parseAsBlock(blockScope);
        }

        RubyParser parser = new RubyParser(context, lexerSource, new RubyWarnings(configuration.getContext()));
        RubyParserResult result;
        try {
            result = parser.parse(configuration);
        } catch (IOException e) {
            // Enebo: We may want to change this error to be more specific,
            // but I am not sure which conditions leads to this...so lame message.
            throw new RaiseException(context.getCoreExceptions().syntaxError("Problem reading source: " + e, null));
        } catch (SyntaxException e) {
            switch (e.getPid()) {
                case UNKNOWN_ENCODING:
                case NOT_ASCII_COMPATIBLE:
                    throw new RaiseException(context.getCoreExceptions().argumentError(e.getMessage(), null));
                default:
                    StringBuilder buffer = new StringBuilder(100);
                    buffer.append(e.getFile()).append(':');
                    buffer.append(e.getLine() + 1).append(": ");
                    buffer.append(e.getMessage());

                    throw new RaiseException(context.getCoreExceptions().syntaxError(buffer.toString(), null));
            }
        }

        // If variables were added then we may need to grow the dynamic scope to match the static
        // one.
        // FIXME: Make this so we only need to check this for blockScope != null.  We cannot
        // currently since we create the DynamicScope for a LocalStaticScope before parse begins.
        // Refactoring should make this fixable.
        if (result.getScope() != null) {
            result.getScope().growIfNeeded();
        }

        return (RootParseNode) result.getAST();
    }
*****************************************
*****************************************
SATD id: 44		Size: 28
    public void beginMethod(CompilerCallback argsCallback, StaticScope scope) {
        // fill in all vars with nil so compiler is happy about future accesses
        if (scope.getNumberOfVariables() > 0) {
            // if we don't have opt args, start after args (they will be assigned later)
            // this is for crap like def foo(a = (b = true; 1)) which numbers b before a
            int start;
            if (scope.getOptionalArgs() > 0) {
                // we have opt args, must assign nil starting immediately after last req arg
                start = scope.getRequiredArgs();
            } else {
                // no opt args, start after all normal args
                start =
                        scope.getRequiredArgs() +
                        (scope.getRestArg() >= 0 ? 1 : 0);
            }
            for (int i = start; i < scope.getNumberOfVariables(); i++) {
                methodCompiler.loadNil();
                assignLocalVariable(i, false);
            }

            // temp locals must start after last real local
            tempVariableIndex += scope.getNumberOfVariables();
        }

        if (argsCallback != null) {
            argsCallback.call(methodCompiler);
        }
    }
*****************************************
*****************************************
SATD id: 45		Size: 556
    public byte[] formatInfinite(int width, int precision, double dval) {
//        if (arg == null || name != null) {
//            arg = args.next(name);
//            name = null;
//        }

//        if (!(arg instanceof RubyFloat)) {
//            // FIXME: what is correct 'recv' argument?
//            // (this does produce the desired behavior)
//            if (usePrefixForZero) {
//                arg = RubyKernel.new_float(arg,arg);
//            } else {
//                arg = RubyKernel.new_float19(arg,arg);
//            }
//        }
//        double dval = ((RubyFloat)arg).getDoubleValue();
        boolean hasPrecisionFlag = precision != PrintfSimpleTreeBuilder.DEFAULT;
        final char fchar = this.format;

        boolean nan = dval != dval;
        boolean inf = dval == Double.POSITIVE_INFINITY || dval == Double.NEGATIVE_INFINITY;
        boolean negative = dval < 0.0d || (dval == 0.0d && (new Float(dval)).equals(new Float(-0.0)));

        byte[] digits;
        int nDigits = 0;
        int exponent = 0;

        int len = 0;
        byte signChar;

        final ByteList buf = new ByteList();

        if (nan || inf) {
            if (nan) {
                digits = NAN_VALUE;
                len = NAN_VALUE.length;
            } else {
                digits = INFINITY_VALUE;
                len = INFINITY_VALUE.length;
            }
            if (negative) {
                signChar = '-';
                width--;
            } else if (hasPlusFlag) {
                signChar = '+';
                width--;
            } else if (hasSpaceFlag) {
                signChar = ' ';
                width--;
            } else {
                signChar = 0;
            }
            width -= len;

            if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                buf.fill(' ', width);
                width = 0;
            }
            if (signChar != 0) buf.append(signChar);

            if (width > 0 && !hasMinusFlag) {
                buf.fill('0', width);
                width = 0;
            }
            buf.append(digits);
            if (width > 0) buf.fill(' ', width);

//            offset++;
//            incomplete = false;
//            break;
            return buf.bytes();
        }

        final Locale locale = Locale.getDefault(); // TODO BJF Aug 13, 2016 Correct locale here?
        NumberFormat nf = Sprintf.getNumberFormat(locale);
        nf.setMaximumFractionDigits(Integer.MAX_VALUE);
        String str = nf.format(dval);

        // grrr, arghh, want to subclass sun.misc.FloatingDecimal, but can't,
        // so we must do all this (the next 70 lines of code), which has already
        // been done by FloatingDecimal.
        int strlen = str.length();
        digits = new byte[strlen];
        int nTrailingZeroes = 0;
        int i = negative ? 1 : 0;
        int decPos = 0;
        byte ival;
        int_loop:
        for (; i < strlen; ) {
            switch (ival = (byte) str.charAt(i++)) {
                case '0':
                    if (nDigits > 0) nTrailingZeroes++;

                    break; // switch
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (nTrailingZeroes > 0) {
                        for (; nTrailingZeroes > 0; nTrailingZeroes--) {
                            digits[nDigits++] = '0';
                        }
                    }
                    digits[nDigits++] = ival;
                    break; // switch
                case '.':
                    break int_loop;
            }
        }
        decPos = nDigits + nTrailingZeroes;
        dec_loop:
        for (; i < strlen; ) {
            switch (ival = (byte) str.charAt(i++)) {
                case '0':
                    if (nDigits > 0) {
                        nTrailingZeroes++;
                    } else {
                        exponent--;
                    }
                    break; // switch
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (nTrailingZeroes > 0) {
                        for (; nTrailingZeroes > 0; nTrailingZeroes--) {
                            digits[nDigits++] = '0';
                        }
                    }
                    digits[nDigits++] = ival;
                    break; // switch
                case 'E':
                    break dec_loop;
            }
        }
        if (i < strlen) {
            int expSign;
            int expVal = 0;
            if (str.charAt(i) == '-') {
                expSign = -1;
                i++;
            } else {
                expSign = 1;
            }
            for (; i < strlen; ) {
                expVal = expVal * 10 + ((int) str.charAt(i++) - (int) '0');
            }
            exponent += expVal * expSign;
        }
        exponent += decPos - nDigits;

        // gotta have at least a zero...
        if (nDigits == 0) {
            digits[0] = '0';
            nDigits = 1;
            exponent = 0;
        }

        // OK, we now have the significand in digits[0...nDigits]
        // and the exponent in exponent.  We're ready to format.

        int intDigits, intZeroes, intLength;
        int decDigits, decZeroes, decLength;
        byte expChar;

        if (negative) {
            signChar = '-';
            width--;
        } else if (hasPlusFlag) {
            signChar = '+';
            width--;
        } else if (hasSpaceFlag) {
            signChar = ' ';
            width--;
        } else {
            signChar = 0;
        }
        if (!hasPrecisionFlag) {
            precision = 6;
        }

        switch (fchar) {
            case 'E':
            case 'G':
                expChar = 'E';
                break;
            case 'e':
            case 'g':
                expChar = 'e';
                break;
            default:
                expChar = 0;
        }

        final byte decimalSeparator = (byte) Sprintf.getDecimalFormat(locale).getDecimalSeparator();

        switch (fchar) {
            case 'g':
            case 'G':
                // an empirically derived rule: precision applies to
                // significand length, irrespective of exponent

                // an official rule, clarified: if the exponent
                // <clarif>after adjusting for exponent form</clarif>
                // is < -4,  or the exponent <clarif>after adjusting
                // for exponent form</clarif> is greater than the
                // precision, use exponent form
                boolean expForm = (exponent + nDigits - 1 < -4 ||
                    exponent + nDigits > (precision == 0 ? 1 : precision));
                // it would be nice (and logical!) if exponent form
                // behaved like E/e, and decimal form behaved like f,
                // but no such luck. hence:
                if (expForm) {
                    // intDigits isn't used here, but if it were, it would be 1
                            /* intDigits = 1; */
                    decDigits = nDigits - 1;
                    // precision for G/g includes integer digits
                    precision = Math.max(0, precision - 1);

                    if (precision < decDigits) {
                        int n = round(digits, nDigits, precision, precision != 0);
                        if (n > nDigits) nDigits = n;
                        decDigits = Math.min(nDigits - 1, precision);
                    }
                    exponent += nDigits - 1;

                    boolean isSharp = hasFSharpFlag;

                    // deal with length/width

                    len++; // first digit is always printed

                    // MRI behavior: Be default, 2 digits
                    // in the exponent. Use 3 digits
                    // only when necessary.
                    // See comment for writeExp method for more details.
                    if (exponent > 99) {
                        len += 5; // 5 -> e+nnn / e-nnn
                    } else {
                        len += 4; // 4 -> e+nn / e-nn
                    }

                    if (isSharp) {
                        // in this mode, '.' is always printed
                        len++;
                    }

                    if (precision > 0) {
                        if (!isSharp) {
                            // MRI behavior: In this mode
                            // trailing zeroes are removed:
                            // 1.500E+05 -> 1.5E+05
                            int j = decDigits;
                            for (; j >= 1; j--) {
                                if (digits[j] == '0') {
                                    decDigits--;
                                } else {
                                    break;
                                }
                            }

                            if (decDigits > 0) {
                                len += 1; // '.' is printed
                                len += decDigits;
                            }
                        } else {
                            // all precision numebers printed
                            len += precision;
                        }
                    }

                    width -= len;

                    if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                        buf.fill(' ', width);
                        width = 0;
                    }
                    if (signChar != 0) {
                        buf.append(signChar);
                    }
                    if (width > 0 && !hasMinusFlag) {
                        buf.fill('0', width);
                        width = 0;
                    }

                    // now some data...
                    buf.append(digits[0]);

                    boolean dotToPrint = isSharp
                        || (precision > 0 && decDigits > 0);

                    if (dotToPrint) {
                        buf.append(decimalSeparator); // '.' // args.getDecimalSeparator()
                    }

                    if (precision > 0 && decDigits > 0) {
                        buf.append(digits, 1, decDigits);
                        precision -= decDigits;
                    }

                    if (precision > 0 && isSharp) {
                        buf.fill('0', precision);
                    }

                    writeExp(buf, exponent, expChar);

                    if (width > 0) {
                        buf.fill(' ', width);
                    }
                } else { // decimal form, like (but not *just* like!) 'f'
                    intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                    intZeroes = Math.max(0, exponent);
                    intLength = intDigits + intZeroes;
                    decDigits = nDigits - intDigits;
                    decZeroes = Math.max(0, -(decDigits + exponent));
                    decLength = decZeroes + decDigits;
                    precision = Math.max(0, precision - intLength);

                    if (precision < decDigits) {
                        int n = round(digits, nDigits, intDigits + precision - 1, precision != 0);
                        if (n > nDigits) {
                            // digits array shifted, update all
                            nDigits = n;
                            intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                            intLength = intDigits + intZeroes;
                            decDigits = nDigits - intDigits;
                            decZeroes = Math.max(0, -(decDigits + exponent));
                            precision = Math.max(0, precision - 1);
                        }
                        decDigits = precision;
                        decLength = decZeroes + decDigits;
                    }
                    len += intLength;
                    if (decLength > 0) {
                        len += decLength + 1;
                    } else {
                        if (hasFSharpFlag) {
                            len++; // will have a trailing '.'
                            if (precision > 0) { // g fills trailing zeroes if #
                                len += precision;
                            }
                        }
                    }

                    width -= len;

                    if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                        buf.fill(' ', width);
                        width = 0;
                    }
                    if (signChar != 0) {
                        buf.append(signChar);
                    }
                    if (width > 0 && !hasMinusFlag) {
                        buf.fill('0', width);
                        width = 0;
                    }
                    // now some data...
                    if (intLength > 0) {
                        if (intDigits > 0) { // s/b true, since intLength > 0
                            buf.append(digits, 0, intDigits);
                        }
                        if (intZeroes > 0) {
                            buf.fill('0', intZeroes);
                        }
                    } else {
                        // always need at least a 0
                        buf.append('0');
                    }
                    if (decLength > 0 || hasFSharpFlag) {
                        buf.append(decimalSeparator);
                    }
                    if (decLength > 0) {
                        if (decZeroes > 0) {
                            buf.fill('0', decZeroes);
                            precision -= decZeroes;
                        }
                        if (decDigits > 0) {
                            buf.append(digits, intDigits, decDigits);
                            precision -= decDigits;
                        }
                        if (hasFSharpFlag && precision > 0) {
                            buf.fill('0', precision);
                        }
                    }
                    if (hasFSharpFlag && precision > 0) buf.fill('0', precision);
                    if (width > 0) buf.fill(' ', width);
                }
                break;

            case 'f':
                intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                intZeroes = Math.max(0, exponent);
                intLength = intDigits + intZeroes;
                decDigits = nDigits - intDigits;
                decZeroes = Math.max(0, -(decDigits + exponent));
                decLength = decZeroes + decDigits;

                if (precision < decLength) {
                    if (precision < decZeroes) {
                        decDigits = 0;
                        decZeroes = precision;
                    } else {
                        int n = round(digits, nDigits, intDigits + precision - decZeroes - 1, false);
                        if (n > nDigits) {
                            // digits arr shifted, update all
                            nDigits = n;
                            intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                            intLength = intDigits + intZeroes;
                            decDigits = nDigits - intDigits;
                            decZeroes = Math.max(0, -(decDigits + exponent));
                            decLength = decZeroes + decDigits;
                        }
                        decDigits = precision - decZeroes;
                    }
                    decLength = decZeroes + decDigits;
                }
                if (precision > 0) {
                    len += Math.max(1, intLength) + 1 + precision;
                    // (1|intlen).prec
                } else {
                    len += Math.max(1, intLength);
                    // (1|intlen)
                    if (hasFSharpFlag) {
                        len++; // will have a trailing '.'
                    }
                }

                width -= len;

                if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                    buf.fill(' ', width);
                    width = 0;
                }
                if (signChar != 0) {
                    buf.append(signChar);
                }
                if (width > 0 && !hasMinusFlag) {
                    buf.fill('0', width);
                    width = 0;
                }
                // now some data...
                if (intLength > 0) {
                    if (intDigits > 0) { // s/b true, since intLength > 0
                        buf.append(digits, 0, intDigits);
                    }
                    if (intZeroes > 0) {
                        buf.fill('0', intZeroes);
                    }
                } else {
                    // always need at least a 0
                    buf.append('0');
                }
                if (precision > 0 || hasFSharpFlag) {
                    buf.append(decimalSeparator);
                }
                if (precision > 0) {
                    if (decZeroes > 0) {
                        buf.fill('0', decZeroes);
                        precision -= decZeroes;
                    }
                    if (decDigits > 0) {
                        buf.append(digits, intDigits, decDigits);
                        precision -= decDigits;
                    }
                    // fill up the rest with zeroes
                    if (precision > 0) {
                        buf.fill('0', precision);
                    }
                }
                if (width > 0) {
                    buf.fill(' ', width);
                }
                break;
            case 'E':
            case 'e':
                // intDigits isn't used here, but if it were, it would be 1
                        /* intDigits = 1; */
                decDigits = nDigits - 1;

                if (precision < decDigits) {
                    int n = round(digits, nDigits, precision, precision != 0);
                    if (n > nDigits) {
                        nDigits = n;
                    }
                    decDigits = Math.min(nDigits - 1, precision);
                }
                exponent += nDigits - 1;

                boolean isSharp = hasFSharpFlag;

                // deal with length/width

                len++; // first digit is always printed

                // MRI behavior: Be default, 2 digits
                // in the exponent. Use 3 digits
                // only when necessary.
                // See comment for writeExp method for more details.
                if (exponent > 99) {
                    len += 5; // 5 -> e+nnn / e-nnn
                } else {
                    len += 4; // 4 -> e+nn / e-nn
                }

                if (precision > 0) {
                    // '.' and all precision digits printed
                    len += 1 + precision;
                } else if (isSharp) {
                    len++;  // in this mode, '.' is always printed
                }

                width -= len;

                if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                    buf.fill(' ', width);
                    width = 0;
                }
                if (signChar != 0) {
                    buf.append(signChar);
                }
                if (width > 0 && !hasMinusFlag) {
                    buf.fill('0', width);
                    width = 0;
                }
                // now some data...
                buf.append(digits[0]);
                if (precision > 0) {
                    buf.append(decimalSeparator); // '.'
                    if (decDigits > 0) {
                        buf.append(digits, 1, decDigits);
                        precision -= decDigits;
                    }
                    if (precision > 0) buf.fill('0', precision);

                } else if (hasFSharpFlag) {
                    buf.append(decimalSeparator);
                }

                writeExp(buf, exponent, expChar);

                if (width > 0) buf.fill(' ', width);

        }
        return buf.bytes();
    }
*****************************************
*****************************************
SATD id: 46		Size: 9
    public static RubyModule findImplementerIfNecessary(RubyModule clazz, RubyModule implementationClass) {
        if (implementationClass.needsImplementer()) {
            // modules are included with a shim class; we must find that shim to handle super() appropriately
            return clazz.findImplementer(implementationClass);
        } else {
            // method is directly in a class, so just ensure we don't use any prepends
            return implementationClass.getMethodLocation();
        }
    }
*****************************************
*****************************************
SATD id: 47		Size: 34
    // not intended to be called directly by users (private)
    // OLD TODO from Ruby code:
    // This should be implemented in JavaClass.java, where we can
    // check for reserved Ruby names, conflicting methods, etc.
    @JRubyMethod(backtrace = true, visibility = Visibility.PRIVATE)
    public static IRubyObject implement(ThreadContext context, IRubyObject self, IRubyObject clazz) {
        Ruby runtime = context.getRuntime();

        if (!(clazz instanceof RubyModule)) {
            throw runtime.newTypeError(clazz, runtime.getModule());
        }

        RubyModule targetModule = (RubyModule)clazz;
        JavaClass javaClass = (JavaClass)self.getInstanceVariables().fastGetInstanceVariable("@java_class");
        
        Method[] javaInstanceMethods = javaClass.javaClass().getMethods();
        DynamicMethod dummyMethod = new org.jruby.internal.runtime.methods.JavaMethod(targetModule, Visibility.PUBLIC) {
            @Override
            public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                // dummy bodies for default impls
                return context.getRuntime().getNil();
            }
        };
        
        for (int i = 0; i < javaInstanceMethods.length; i++) {
            Method method = javaInstanceMethods[i];
            String name = method.getName();
            if (targetModule.searchMethod(name) != UndefinedMethod.INSTANCE) continue;
            
            targetModule.addMethod(name, dummyMethod);
        }
        
        return runtime.getNil();
    }
*****************************************
*****************************************
SATD id: 48		Size: 0
*****************************************
*****************************************
SATD id: 49		Size: 16
    public void runPeepHoleOptimization()
    {
        Map<Operand,Operand> valueMap = new HashMap<Operand,Operand>();
        for (IR_Instr i : _instrs) {
            // Reset value map if this instruction is the start/end of a basic block
            Operation iop = i._op;
            if (iop.startsBasicBlock() || iop.endsBasicBlock())
                valueMap = new HashMap<Operand,Operand>();

            // Simplify instruction and record mapping between target variable and simplified value
            Operand val = i.simplifyAndGetResult(valueMap);
            Operand res = i.getResult();
            if (val != null && res != null && res != val)
                valueMap.add(res, val);
        }
    }
*****************************************
*****************************************
SATD id: 5		Size: 16
    // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
    // perhaps security methods should find their own centralized home at some point.
    public void checkSafeString(IRubyObject object) {
        if (getSafeLevel() > 0 && object.isTaint()) {
            ThreadContext tc = getCurrentContext();
            if (tc.getFrameName() != null) {
                throw newSecurityError("Insecure operation - " + tc.getFrameName());
            }
            throw newSecurityError("Insecure operation: -r");
        }
        secure(4);
        if (!(object instanceof RubyString)) {
            throw newTypeError(
                "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
        }
    }
*****************************************
*****************************************
SATD id: 50		Size: 2
    // ruby constants for strings (should this be moved somewhere else?)
    public static final int STR_FUNC_ESCAPE=0x01;
*****************************************
*****************************************
SATD id: 51		Size: 81
    public void initialize(JavaClass javaClassObject, RubyModule proxy) {
        RubyClass proxyClass = (RubyClass)proxy;
        Class<?> superclass = javaClass.getSuperclass();

        final State state = new State(runtime, superclass);

        super.initializeBase(proxy);

        if ( javaClass.isArray() || javaClass.isPrimitive() ) {
            // see note below re: 2-field kludge
            javaClassObject.setProxyClass(proxyClass);
            javaClassObject.setProxyModule(proxy);
            return;
        }

        setupClassFields(javaClass, state);
        setupClassMethods(javaClass, state);
        setupClassConstructors(javaClass, state);

        javaClassObject.staticAssignedNames = Collections.unmodifiableMap(state.staticNames);
        javaClassObject.instanceAssignedNames = Collections.unmodifiableMap(state.instanceNames);

        proxyClass.setReifiedClass(javaClass);

        assert javaClassObject.proxyClass == null;
        javaClassObject.unfinishedProxyClass = proxyClass;

        // flag the class as a Java class proxy.
        proxy.setJavaProxy(true);
        proxy.getSingletonClass().setJavaProxy(true);

        // set parent to either package module or outer class
        final RubyModule parent;
        final Class<?> enclosingClass = javaClass.getEnclosingClass();
        if ( enclosingClass != null ) {
            parent = Java.getProxyClass(runtime, enclosingClass);
        } else {
            parent = Java.getJavaPackageModule(runtime, javaClass.getPackage());
        }
        proxy.setParent(parent);

        // set the Java class name and package
        if ( javaClass.isAnonymousClass() ) {
            String baseName = ""; // javaClass.getSimpleName() returns "" for anonymous
            if ( enclosingClass != null ) {
                // instead of an empty name anonymous classes will have a "conforming"
                // although not valid (by Ruby semantics) RubyClass name e.g. :
                // 'Java::JavaUtilConcurrent::TimeUnit::1' for $1 anonymous enum class
                // NOTE: if this turns out suitable shall do the same for method etc.
                final String className = javaClass.getName();
                final int length = className.length();
                final int offset = enclosingClass.getName().length();
                if ( length > offset && className.charAt(offset) != '$' ) {
                    baseName = className.substring( offset );
                }
                else if ( length > offset + 1 ) { // skip '$'
                    baseName = className.substring( offset + 1 );
                }
            }
            proxy.setBaseName( baseName );
        }
        else {
            proxy.setBaseName( javaClass.getSimpleName() );
        }

        installClassFields(proxyClass, state);
        installClassInstanceMethods(proxyClass, state);
        installClassConstructors(proxyClass, state);
        installClassClasses(javaClass, proxyClass);

        // FIXME: bit of a kludge here (non-interface classes assigned to both
        // class and module fields). simplifies proxy extender code, will go away
        // when JI is overhauled (and proxy extenders are deprecated).
        javaClassObject.setProxyClass(proxyClass);
        javaClassObject.setProxyModule(proxy);

        javaClassObject.applyProxyExtenders();

        // TODO: we can probably release our references to the constantFields
        // array and static/instance callback hashes at this point.
    }
*****************************************
*****************************************
SATD id: 52		Size: 2
    // small hack to save a cast later on
    boolean hasLocalMethod() { return true; }
*****************************************
*****************************************
SATD id: 53		Size: 556
    public byte[] formatInfinite(int width, int precision, double dval) {
//        if (arg == null || name != null) {
//            arg = args.next(name);
//            name = null;
//        }

//        if (!(arg instanceof RubyFloat)) {
//            // FIXME: what is correct 'recv' argument?
//            // (this does produce the desired behavior)
//            if (usePrefixForZero) {
//                arg = RubyKernel.new_float(arg,arg);
//            } else {
//                arg = RubyKernel.new_float19(arg,arg);
//            }
//        }
//        double dval = ((RubyFloat)arg).getDoubleValue();
        boolean hasPrecisionFlag = precision != PrintfSimpleTreeBuilder.DEFAULT;
        final char fchar = this.format;

        boolean nan = dval != dval;
        boolean inf = dval == Double.POSITIVE_INFINITY || dval == Double.NEGATIVE_INFINITY;
        boolean negative = dval < 0.0d || (dval == 0.0d && (new Float(dval)).equals(new Float(-0.0)));

        byte[] digits;
        int nDigits = 0;
        int exponent = 0;

        int len = 0;
        byte signChar;

        final ByteList buf = new ByteList();

        if (nan || inf) {
            if (nan) {
                digits = NAN_VALUE;
                len = NAN_VALUE.length;
            } else {
                digits = INFINITY_VALUE;
                len = INFINITY_VALUE.length;
            }
            if (negative) {
                signChar = '-';
                width--;
            } else if (hasPlusFlag) {
                signChar = '+';
                width--;
            } else if (hasSpaceFlag) {
                signChar = ' ';
                width--;
            } else {
                signChar = 0;
            }
            width -= len;

            if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                buf.fill(' ', width);
                width = 0;
            }
            if (signChar != 0) buf.append(signChar);

            if (width > 0 && !hasMinusFlag) {
                buf.fill('0', width);
                width = 0;
            }
            buf.append(digits);
            if (width > 0) buf.fill(' ', width);

//            offset++;
//            incomplete = false;
//            break;
            return buf.bytes();
        }

        final Locale locale = Locale.getDefault(); // TODO BJF Aug 13, 2016 Correct locale here?
        NumberFormat nf = Sprintf.getNumberFormat(locale);
        nf.setMaximumFractionDigits(Integer.MAX_VALUE);
        String str = nf.format(dval);

        // grrr, arghh, want to subclass sun.misc.FloatingDecimal, but can't,
        // so we must do all this (the next 70 lines of code), which has already
        // been done by FloatingDecimal.
        int strlen = str.length();
        digits = new byte[strlen];
        int nTrailingZeroes = 0;
        int i = negative ? 1 : 0;
        int decPos = 0;
        byte ival;
        int_loop:
        for (; i < strlen; ) {
            switch (ival = (byte) str.charAt(i++)) {
                case '0':
                    if (nDigits > 0) nTrailingZeroes++;

                    break; // switch
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (nTrailingZeroes > 0) {
                        for (; nTrailingZeroes > 0; nTrailingZeroes--) {
                            digits[nDigits++] = '0';
                        }
                    }
                    digits[nDigits++] = ival;
                    break; // switch
                case '.':
                    break int_loop;
            }
        }
        decPos = nDigits + nTrailingZeroes;
        dec_loop:
        for (; i < strlen; ) {
            switch (ival = (byte) str.charAt(i++)) {
                case '0':
                    if (nDigits > 0) {
                        nTrailingZeroes++;
                    } else {
                        exponent--;
                    }
                    break; // switch
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (nTrailingZeroes > 0) {
                        for (; nTrailingZeroes > 0; nTrailingZeroes--) {
                            digits[nDigits++] = '0';
                        }
                    }
                    digits[nDigits++] = ival;
                    break; // switch
                case 'E':
                    break dec_loop;
            }
        }
        if (i < strlen) {
            int expSign;
            int expVal = 0;
            if (str.charAt(i) == '-') {
                expSign = -1;
                i++;
            } else {
                expSign = 1;
            }
            for (; i < strlen; ) {
                expVal = expVal * 10 + ((int) str.charAt(i++) - (int) '0');
            }
            exponent += expVal * expSign;
        }
        exponent += decPos - nDigits;

        // gotta have at least a zero...
        if (nDigits == 0) {
            digits[0] = '0';
            nDigits = 1;
            exponent = 0;
        }

        // OK, we now have the significand in digits[0...nDigits]
        // and the exponent in exponent.  We're ready to format.

        int intDigits, intZeroes, intLength;
        int decDigits, decZeroes, decLength;
        byte expChar;

        if (negative) {
            signChar = '-';
            width--;
        } else if (hasPlusFlag) {
            signChar = '+';
            width--;
        } else if (hasSpaceFlag) {
            signChar = ' ';
            width--;
        } else {
            signChar = 0;
        }
        if (!hasPrecisionFlag) {
            precision = 6;
        }

        switch (fchar) {
            case 'E':
            case 'G':
                expChar = 'E';
                break;
            case 'e':
            case 'g':
                expChar = 'e';
                break;
            default:
                expChar = 0;
        }

        final byte decimalSeparator = (byte) Sprintf.getDecimalFormat(locale).getDecimalSeparator();

        switch (fchar) {
            case 'g':
            case 'G':
                // an empirically derived rule: precision applies to
                // significand length, irrespective of exponent

                // an official rule, clarified: if the exponent
                // <clarif>after adjusting for exponent form</clarif>
                // is < -4,  or the exponent <clarif>after adjusting
                // for exponent form</clarif> is greater than the
                // precision, use exponent form
                boolean expForm = (exponent + nDigits - 1 < -4 ||
                    exponent + nDigits > (precision == 0 ? 1 : precision));
                // it would be nice (and logical!) if exponent form
                // behaved like E/e, and decimal form behaved like f,
                // but no such luck. hence:
                if (expForm) {
                    // intDigits isn't used here, but if it were, it would be 1
                            /* intDigits = 1; */
                    decDigits = nDigits - 1;
                    // precision for G/g includes integer digits
                    precision = Math.max(0, precision - 1);

                    if (precision < decDigits) {
                        int n = round(digits, nDigits, precision, precision != 0);
                        if (n > nDigits) nDigits = n;
                        decDigits = Math.min(nDigits - 1, precision);
                    }
                    exponent += nDigits - 1;

                    boolean isSharp = hasFSharpFlag;

                    // deal with length/width

                    len++; // first digit is always printed

                    // MRI behavior: Be default, 2 digits
                    // in the exponent. Use 3 digits
                    // only when necessary.
                    // See comment for writeExp method for more details.
                    if (exponent > 99) {
                        len += 5; // 5 -> e+nnn / e-nnn
                    } else {
                        len += 4; // 4 -> e+nn / e-nn
                    }

                    if (isSharp) {
                        // in this mode, '.' is always printed
                        len++;
                    }

                    if (precision > 0) {
                        if (!isSharp) {
                            // MRI behavior: In this mode
                            // trailing zeroes are removed:
                            // 1.500E+05 -> 1.5E+05
                            int j = decDigits;
                            for (; j >= 1; j--) {
                                if (digits[j] == '0') {
                                    decDigits--;
                                } else {
                                    break;
                                }
                            }

                            if (decDigits > 0) {
                                len += 1; // '.' is printed
                                len += decDigits;
                            }
                        } else {
                            // all precision numebers printed
                            len += precision;
                        }
                    }

                    width -= len;

                    if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                        buf.fill(' ', width);
                        width = 0;
                    }
                    if (signChar != 0) {
                        buf.append(signChar);
                    }
                    if (width > 0 && !hasMinusFlag) {
                        buf.fill('0', width);
                        width = 0;
                    }

                    // now some data...
                    buf.append(digits[0]);

                    boolean dotToPrint = isSharp
                        || (precision > 0 && decDigits > 0);

                    if (dotToPrint) {
                        buf.append(decimalSeparator); // '.' // args.getDecimalSeparator()
                    }

                    if (precision > 0 && decDigits > 0) {
                        buf.append(digits, 1, decDigits);
                        precision -= decDigits;
                    }

                    if (precision > 0 && isSharp) {
                        buf.fill('0', precision);
                    }

                    writeExp(buf, exponent, expChar);

                    if (width > 0) {
                        buf.fill(' ', width);
                    }
                } else { // decimal form, like (but not *just* like!) 'f'
                    intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                    intZeroes = Math.max(0, exponent);
                    intLength = intDigits + intZeroes;
                    decDigits = nDigits - intDigits;
                    decZeroes = Math.max(0, -(decDigits + exponent));
                    decLength = decZeroes + decDigits;
                    precision = Math.max(0, precision - intLength);

                    if (precision < decDigits) {
                        int n = round(digits, nDigits, intDigits + precision - 1, precision != 0);
                        if (n > nDigits) {
                            // digits array shifted, update all
                            nDigits = n;
                            intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                            intLength = intDigits + intZeroes;
                            decDigits = nDigits - intDigits;
                            decZeroes = Math.max(0, -(decDigits + exponent));
                            precision = Math.max(0, precision - 1);
                        }
                        decDigits = precision;
                        decLength = decZeroes + decDigits;
                    }
                    len += intLength;
                    if (decLength > 0) {
                        len += decLength + 1;
                    } else {
                        if (hasFSharpFlag) {
                            len++; // will have a trailing '.'
                            if (precision > 0) { // g fills trailing zeroes if #
                                len += precision;
                            }
                        }
                    }

                    width -= len;

                    if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                        buf.fill(' ', width);
                        width = 0;
                    }
                    if (signChar != 0) {
                        buf.append(signChar);
                    }
                    if (width > 0 && !hasMinusFlag) {
                        buf.fill('0', width);
                        width = 0;
                    }
                    // now some data...
                    if (intLength > 0) {
                        if (intDigits > 0) { // s/b true, since intLength > 0
                            buf.append(digits, 0, intDigits);
                        }
                        if (intZeroes > 0) {
                            buf.fill('0', intZeroes);
                        }
                    } else {
                        // always need at least a 0
                        buf.append('0');
                    }
                    if (decLength > 0 || hasFSharpFlag) {
                        buf.append(decimalSeparator);
                    }
                    if (decLength > 0) {
                        if (decZeroes > 0) {
                            buf.fill('0', decZeroes);
                            precision -= decZeroes;
                        }
                        if (decDigits > 0) {
                            buf.append(digits, intDigits, decDigits);
                            precision -= decDigits;
                        }
                        if (hasFSharpFlag && precision > 0) {
                            buf.fill('0', precision);
                        }
                    }
                    if (hasFSharpFlag && precision > 0) buf.fill('0', precision);
                    if (width > 0) buf.fill(' ', width);
                }
                break;

            case 'f':
                intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                intZeroes = Math.max(0, exponent);
                intLength = intDigits + intZeroes;
                decDigits = nDigits - intDigits;
                decZeroes = Math.max(0, -(decDigits + exponent));
                decLength = decZeroes + decDigits;

                if (precision < decLength) {
                    if (precision < decZeroes) {
                        decDigits = 0;
                        decZeroes = precision;
                    } else {
                        int n = round(digits, nDigits, intDigits + precision - decZeroes - 1, false);
                        if (n > nDigits) {
                            // digits arr shifted, update all
                            nDigits = n;
                            intDigits = Math.max(0, Math.min(nDigits + exponent, nDigits));
                            intLength = intDigits + intZeroes;
                            decDigits = nDigits - intDigits;
                            decZeroes = Math.max(0, -(decDigits + exponent));
                            decLength = decZeroes + decDigits;
                        }
                        decDigits = precision - decZeroes;
                    }
                    decLength = decZeroes + decDigits;
                }
                if (precision > 0) {
                    len += Math.max(1, intLength) + 1 + precision;
                    // (1|intlen).prec
                } else {
                    len += Math.max(1, intLength);
                    // (1|intlen)
                    if (hasFSharpFlag) {
                        len++; // will have a trailing '.'
                    }
                }

                width -= len;

                if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                    buf.fill(' ', width);
                    width = 0;
                }
                if (signChar != 0) {
                    buf.append(signChar);
                }
                if (width > 0 && !hasMinusFlag) {
                    buf.fill('0', width);
                    width = 0;
                }
                // now some data...
                if (intLength > 0) {
                    if (intDigits > 0) { // s/b true, since intLength > 0
                        buf.append(digits, 0, intDigits);
                    }
                    if (intZeroes > 0) {
                        buf.fill('0', intZeroes);
                    }
                } else {
                    // always need at least a 0
                    buf.append('0');
                }
                if (precision > 0 || hasFSharpFlag) {
                    buf.append(decimalSeparator);
                }
                if (precision > 0) {
                    if (decZeroes > 0) {
                        buf.fill('0', decZeroes);
                        precision -= decZeroes;
                    }
                    if (decDigits > 0) {
                        buf.append(digits, intDigits, decDigits);
                        precision -= decDigits;
                    }
                    // fill up the rest with zeroes
                    if (precision > 0) {
                        buf.fill('0', precision);
                    }
                }
                if (width > 0) {
                    buf.fill(' ', width);
                }
                break;
            case 'E':
            case 'e':
                // intDigits isn't used here, but if it were, it would be 1
                        /* intDigits = 1; */
                decDigits = nDigits - 1;

                if (precision < decDigits) {
                    int n = round(digits, nDigits, precision, precision != 0);
                    if (n > nDigits) {
                        nDigits = n;
                    }
                    decDigits = Math.min(nDigits - 1, precision);
                }
                exponent += nDigits - 1;

                boolean isSharp = hasFSharpFlag;

                // deal with length/width

                len++; // first digit is always printed

                // MRI behavior: Be default, 2 digits
                // in the exponent. Use 3 digits
                // only when necessary.
                // See comment for writeExp method for more details.
                if (exponent > 99) {
                    len += 5; // 5 -> e+nnn / e-nnn
                } else {
                    len += 4; // 4 -> e+nn / e-nn
                }

                if (precision > 0) {
                    // '.' and all precision digits printed
                    len += 1 + precision;
                } else if (isSharp) {
                    len++;  // in this mode, '.' is always printed
                }

                width -= len;

                if (width > 0 && !hasZeroFlag && !hasMinusFlag) {
                    buf.fill(' ', width);
                    width = 0;
                }
                if (signChar != 0) {
                    buf.append(signChar);
                }
                if (width > 0 && !hasMinusFlag) {
                    buf.fill('0', width);
                    width = 0;
                }
                // now some data...
                buf.append(digits[0]);
                if (precision > 0) {
                    buf.append(decimalSeparator); // '.'
                    if (decDigits > 0) {
                        buf.append(digits, 1, decDigits);
                        precision -= decDigits;
                    }
                    if (precision > 0) buf.fill('0', precision);

                } else if (hasFSharpFlag) {
                    buf.append(decimalSeparator);
                }

                writeExp(buf, exponent, expChar);

                if (width > 0) buf.fill(' ', width);

        }
        return buf.bytes();
    }
*****************************************
*****************************************
SATD id: 54		Size: 0
*****************************************
*****************************************
SATD id: 55		Size: 9
    public Operand buildNext(final NextNode nextNode, IRExecutionScope s) {
        Operand rv = (nextNode.getValueNode() == null) ? Nil.NIL : build(nextNode.getValueNode(), s);
        // SSS FIXME: 1. Is the ordering correct? (poll before next)
        s.addInstr(new THREAD_POLL_Instr());
        // If a closure, the next is simply a return from the closure!
        // If a regular loop, the next is simply a jump to the end of the iteration
        s.addInstr((s instanceof IRClosure) ? new CLOSURE_RETURN_Instr(rv) : new JumpInstr(s.getCurrentLoop().iterEndLabel));
        return rv;
    }
*****************************************
*****************************************
SATD id: 56		Size: 66
    public void buildBlockArgsAssignment(Node node, IRScope s, int argIndex, boolean isSplat) {
        Variable v;
        switch (node.getNodeType()) {
            case ATTRASSIGNNODE: 
                v = s.getNewTemporaryVariable();
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                buildAttrAssignAssignment(node, s, v);
                break;
// SSS FIXME:
//
// There are also differences in variable scoping between 1.8 and 1.9 
// Ruby 1.8 is the buggy semantics if I understand correctly.
//
// The semantics of how this shadows other variables outside the block needs
// to be figured out during live var analysis.
            case DASGNNODE: {
                DAsgnNode dynamicAsgn = (DAsgnNode) node;
                v = getScopeNDown(s, dynamicAsgn.getDepth()).getLocalVariable(dynamicAsgn.getName());
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                break;
            }
            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
            case CLASSVARASGNNODE:
                v = s.getNewTemporaryVariable();
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                s.addInstr(new PUT_CVAR_Instr(new MetaObject(s), ((ClassVarAsgnNode)node).getName(), v));
                break;
            case CLASSVARDECLNODE:
                v = s.getNewTemporaryVariable();
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                s.addInstr(new PUT_CVAR_Instr(new MetaObject(s), ((ClassVarDeclNode)node).getName(), v));
                break;
            case CONSTDECLNODE:
                v = s.getNewTemporaryVariable();
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                buildConstDeclAssignment((ConstDeclNode) node, s, v);
                break;
            case GLOBALASGNNODE:
                v = s.getNewTemporaryVariable();
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                s.addInstr(new PUT_GLOBAL_VAR_Instr(((GlobalAsgnNode)node).getName(), v));
                break;
            case INSTASGNNODE:
                v = s.getNewTemporaryVariable();
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
                s.addInstr(new PUT_FIELD_Instr(s.getSelf(), ((InstAsgnNode)node).getName(), v));
                break;
            case LOCALASGNNODE: {
                LocalAsgnNode localVariable = (LocalAsgnNode) node;
                int depth = localVariable.getDepth();

                v = getScopeNDown(s, depth).getLocalVariable(localVariable.getName());
                s.addInstr(new RECV_CLOSURE_ARG_Instr(v, argIndex, isSplat));
                break;
            }
            case MULTIPLEASGNNODE:
                // SSS FIXME: Are we guaranteed that we splats dont head to multiple-assignment nodes!  i.e. |*(a,b)|?
                buildMultipleAsgnAssignment((MultipleAsgnNode) node, s, null);
                break;
            case ZEROARGNODE:
                throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
            default:
                throw new NotCompilableException("Can't build assignment node: " + node);
        }
    }
*****************************************
*****************************************
SATD id: 57		Size: 10
    public Operand buildAlias(final AliasNode alias, IRScope s) {
        String newName = "";//TODO: FIX breakage....alias.getNewName();
        String oldName = "";//TODO: FIX breakage....alias.getOldName();
        Operand[] args = new Operand[] { new MetaObject(s), new MethAddr(newName), new MethAddr(oldName) };
        s.recordMethodAlias(newName, oldName);
        s.addInstr(new RUBY_INTERNALS_CALL_Instr(null, MethAddr.DEFINE_ALIAS, args));

            // SSS FIXME: Can this return anything other than nil?
        return Nil.NIL;
    }
*****************************************
*****************************************
SATD id: 58		Size: 0
*****************************************
*****************************************
SATD id: 59		Size: 0
*****************************************
*****************************************
SATD id: 6		Size: 20
    private BlockBody createBlockBody(ThreadContext context, int index, String descriptor) throws NumberFormatException {
        String[] firstSplit = descriptor.split(",");
        String[] secondSplit = firstSplit[2].split(";");

        // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
        for (int i = 0; i < secondSplit.length; i++) {
            secondSplit[i] = secondSplit[i].intern();
        }

        BlockBody body = RuntimeHelpers.createCompiledBlockBody(
                context,
                this,
                firstSplit[0],
                Integer.parseInt(firstSplit[1]),
                secondSplit,
                Boolean.valueOf(firstSplit[3]),
                Integer.parseInt(firstSplit[4]),
                Boolean.valueOf(firstSplit[5]));
        return blockBodies[index] = body;
    }
*****************************************
*****************************************
SATD id: 60		Size: 0
*****************************************
*****************************************
SATD id: 61		Size: 29
    protected IRScope(IRScope s, IRScope lexicalParent) {
        this.lexicalParent = lexicalParent;
        this.manager = s.manager;
        this.fileName = s.fileName;
        this.lineNumber = s.lineNumber;
        this.staticScope = s.staticScope;
        this.threadPollInstrsCount = s.threadPollInstrsCount;
        this.nextClosureIndex = s.nextClosureIndex;
        this.temporaryVariableIndex = s.temporaryVariableIndex;
        this.floatVariableIndex = s.floatVariableIndex;
        this.instrList = new ArrayList<Instr>();
        this.nestedClosures = new ArrayList<IRClosure>();
        this.dfProbs = new HashMap<String, DataFlowProblem>();
        this.nextVarIndex = new HashMap<String, Integer>(); // SSS FIXME: clone!
        this.cfg = null;
        this.interpreterContext = null;
        this.linearizedBBList = null;

        this.flagsComputed = s.flagsComputed;
        this.flags = s.flags.clone();

        this.localVars = new HashMap<String, LocalVariable>(s.localVars);
        this.scopeId = globalScopeCount.getAndIncrement();
        this.state = ScopeState.INIT; // SSS FIXME: Is this correct?

        this.executedPasses = new ArrayList<CompilerPass>();

        setupLexicalContainment();
    }
*****************************************
*****************************************
SATD id: 62		Size: 50
    private Operand buildConditionalLoop(IRExecutionScope s, Node conditionNode, Node bodyNode, boolean isWhile, boolean isLoopHeadCondition)
    {
        if (isLoopHeadCondition && (   (isWhile && conditionNode.getNodeType().alwaysFalse()) 
                                    || (!isWhile && conditionNode.getNodeType().alwaysTrue())))
        {
            // we won't enter the loop -- just build the condition node
            build(conditionNode, s);
            return Nil.NIL;
        } 
        else {
            IRLoop loop = new IRLoop(s);
            s.startLoop(loop);
            s.addInstr(new LABEL_Instr(loop.loopStartLabel));

            if (isLoopHeadCondition) {
                Operand cv = build(conditionNode, s);
                s.addInstr(new BEQInstr(cv, isWhile ? BooleanLiteral.FALSE : BooleanLiteral.TRUE, loop.loopEndLabel));
            }
            s.addInstr(new LABEL_Instr(loop.iterStartLabel));

            // Looks like while can be treated as an expression!
            // So, capture the result of the body so that it can be returned.
            Variable whileResult = null;
            if (bodyNode != null) {
                Operand v = build(bodyNode, s);
                if (v != null) {
                    whileResult = s.getNewTemporaryVariable();
                    s.addInstr(new CopyInstr(whileResult, v));
                }
            }

                // SSS FIXME: Is this correctly placed ... at the end of the loop iteration?
            s.addInstr(new THREAD_POLL_Instr());

            s.addInstr(new LABEL_Instr(loop.iterEndLabel));
            if (isLoopHeadCondition) {
                // Issue a jump back to the head of the while loop
                s.addInstr(new JumpInstr(loop.loopStartLabel));
            }
            else {
                Operand cv = build(conditionNode, s);
                s.addInstr(new BEQInstr(cv, isWhile ? BooleanLiteral.TRUE : BooleanLiteral.FALSE, loop.iterStartLabel));
            }

            s.addInstr(new LABEL_Instr(loop.loopEndLabel));
            s.endLoop(loop);

            return whileResult;
        }
    }
*****************************************
*****************************************
SATD id: 63		Size: 7
    public Operand buildDefs(Node node, IR_Scope m) {
            // Class method
        return defineNewMethod(n, s, false);

            // SSS FIXME: Receiver -- this is the class meta object basically?
        // Operand receiver = build(defsNode.getReceiverNode(), s);
    }
*****************************************
*****************************************
SATD id: 64		Size: 1
    public final String fileName;    // SSS FIXME: Should this be a string literal or a string?
*****************************************
*****************************************
SATD id: 65		Size: 4
      // SSS FIXME: Should this be Operand or CompoundString?
      // Can it happen that symbols are built out of other than compound strings?  
      // Or can it happen during optimizations that this becomes a generic operand?
   public final CompoundString _symName;
*****************************************
*****************************************
SATD id: 66		Size: 40
    public enum JRubyImplementationMethod {
       // SSS FIXME: Note that compiler/impl/BaseBodyCompiler is using op_match2 for match() and and op_match for match2,
       // and we are replicating it here ... Is this a bug there?
       MATCH("op_match2"), 
       MATCH2("op_match"), 
       MATCH3("match3"),
       // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
       // It calls regular to_ary on the object.  But, if it encounters a method_missing, the value
       // is inserted into an 1-element array!
       // try "a,b,c = 1" first; then define Fixnum.to_ary method and try it again.
       // Ex: http://gist.github.com/163551
       TO_ARY("to_ary"),
       UNDEF_METHOD("undefMethod"),
       BLOCK_GIVEN("block_isGiven"),
       RT_IS_GLOBAL_DEFINED("runtime_isGlobalDefined"),
       RT_GET_OBJECT("runtime_getObject"),
       RT_GET_BACKREF("runtime_getBackref"),
       RTH_GET_DEFINED_CONSTANT_OR_BOUND_METHOD("getDefinedConstantOrBoundMethod"),
       SELF_METACLASS("self_metaClass"),
       SELF_HAS_INSTANCE_VARIABLE("self_hasInstanceVariable"),
       SELF_IS_METHOD_BOUND("self_isMethodBound"),
       TC_SAVE_ERR_INFO("threadContext_saveErrInfo"),
       TC_RESTORE_ERR_INFO("threadContext_restoreErrInfo"),
       TC_GET_CONSTANT_DEFINED("threadContext_getConstantDefined"),
       TC_GET_CURRENT_MODULE("threadContext_getCurrentModule"),
       BACKREF_IS_RUBY_MATCH_DATA("backref_isRubyMatchData"),
       METHOD_PUBLIC_ACCESSIBLE("methodIsPublicAccessible"),
       CLASS_VAR_DEFINED("isClassVarDefined"),
       FRAME_SUPER_METHOD_BOUND("frame_superMethodBound"),
       SET_WITHIN_DEFINED("setWithinDefined");

       public MethAddr methAddr;
       JRubyImplementationMethod(String methodName) {
           this.methAddr = new MethAddr(methodName);
       }

       public MethAddr getMethAddr() { 
           return this.methAddr; 
       }
    }
*****************************************
*****************************************
SATD id: 67		Size: 5
    public Operand fetchCompileTimeArrayElement(int argIndex, boolean getSubArray)
    {
        // SSS FIXME: This should never get called for constant svalues
        return null;
    }
*****************************************
*****************************************
SATD id: 68		Size: 4
    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
    // But, if the mehod is modified, we create a new method object which in turn gets a new token.  What makes sense??  Intuitively,
    // it seems the first one ... but let us see ...
    private CodeVersion token;   // Current code version token for this method -- can change during execution as methods get redefined!
*****************************************
*****************************************
SATD id: 69		Size: 11
    public void receiveBlockArgs(final IterNode node, IRScope s) {
        Node args = node.getVarNode();
        if (args instanceof ArgsNode) { // regular blocks
            receiveArgs((ArgsNode)args, s);
        } else if (args instanceof LocalAsgnNode) { // for loops
            // Use local var depth because for-loop uses vars from the surrounding scope
            // SSS FIXME: Verify that this is correct
            LocalAsgnNode lan = (LocalAsgnNode)args;
            s.addInstr(new ReceiveArgumentInstruction(s.getLocalVariable(lan.getName(), lan.getDepth()), 0));
        }
    }
*****************************************
*****************************************
SATD id: 7		Size: 81
    public void initialize(JavaClass javaClassObject, RubyModule proxy) {
        RubyClass proxyClass = (RubyClass)proxy;
        Class<?> superclass = javaClass.getSuperclass();

        final State state = new State(runtime, superclass);

        super.initializeBase(proxy);

        if ( javaClass.isArray() || javaClass.isPrimitive() ) {
            // see note below re: 2-field kludge
            javaClassObject.setProxyClass(proxyClass);
            javaClassObject.setProxyModule(proxy);
            return;
        }

        setupClassFields(javaClass, state);
        setupClassMethods(javaClass, state);
        setupClassConstructors(javaClass, state);

        javaClassObject.staticAssignedNames = Collections.unmodifiableMap(state.staticNames);
        javaClassObject.instanceAssignedNames = Collections.unmodifiableMap(state.instanceNames);

        proxyClass.setReifiedClass(javaClass);

        assert javaClassObject.proxyClass == null;
        javaClassObject.unfinishedProxyClass = proxyClass;

        // flag the class as a Java class proxy.
        proxy.setJavaProxy(true);
        proxy.getSingletonClass().setJavaProxy(true);

        // set parent to either package module or outer class
        final RubyModule parent;
        final Class<?> enclosingClass = javaClass.getEnclosingClass();
        if ( enclosingClass != null ) {
            parent = Java.getProxyClass(runtime, enclosingClass);
        } else {
            parent = Java.getJavaPackageModule(runtime, javaClass.getPackage());
        }
        proxy.setParent(parent);

        // set the Java class name and package
        if ( javaClass.isAnonymousClass() ) {
            String baseName = ""; // javaClass.getSimpleName() returns "" for anonymous
            if ( enclosingClass != null ) {
                // instead of an empty name anonymous classes will have a "conforming"
                // although not valid (by Ruby semantics) RubyClass name e.g. :
                // 'Java::JavaUtilConcurrent::TimeUnit::1' for $1 anonymous enum class
                // NOTE: if this turns out suitable shall do the same for method etc.
                final String className = javaClass.getName();
                final int length = className.length();
                final int offset = enclosingClass.getName().length();
                if ( length > offset && className.charAt(offset) != '$' ) {
                    baseName = className.substring( offset );
                }
                else if ( length > offset + 1 ) { // skip '$'
                    baseName = className.substring( offset + 1 );
                }
            }
            proxy.setBaseName( baseName );
        }
        else {
            proxy.setBaseName( javaClass.getSimpleName() );
        }

        installClassFields(proxyClass, state);
        installClassInstanceMethods(proxyClass, state);
        installClassConstructors(proxyClass, state);
        installClassClasses(javaClass, proxyClass);

        // FIXME: bit of a kludge here (non-interface classes assigned to both
        // class and module fields). simplifies proxy extender code, will go away
        // when JI is overhauled (and proxy extenders are deprecated).
        javaClassObject.setProxyClass(proxyClass);
        javaClassObject.setProxyModule(proxy);

        javaClassObject.applyProxyExtenders();

        // TODO: we can probably release our references to the constantFields
        // array and static/instance callback hashes at this point.
    }
*****************************************
*****************************************
SATD id: 70		Size: 50
    public Operand buildClass(Node node, IR_Scope s) {
        final ClassNode  classNode = (ClassNode) node;
        final Node       superNode = classNode.getSuperNode();
        final Colon3Node cpathNode = classNode.getCPath();

        Operand superClass = null;
        if (superNode != null)
            superClass = build(superNode, s);

            // By default, the container for this class is 's'
        Operand container = null;

            // Do we have a dynamic container?
        if (cpathNode instanceof Colon2Node) {
            Node leftNode = ((Colon2Node) cpathNode).getLeftNode();
            if (leftNode != null)
                container = build(leftNode, s);
        } else if (cpathNode instanceof Colon3Node) {
            container = new MetaObject(IR_Class.OBJECT);
        }

            // Build a new class and add it to the current scope (could be a script / module / class)
        String   className = cpathNode.getName();
        IR_Class c;
        Operand  cMetaObj;
        if (container == null) {
            c = new IR_Class(s, superClass, className, false);
            cMetaObj = new MetaObject(c);
            s.addClass(c);
        }
        else if (container instanceof MetaObject) {
            IR_Scope containerScope = ((MetaObject)container)._scope;
            c = new IR_Class(containerScope, superClass, className, false);
            cMetaObj = container;
            containerScope.addClass(c);
        }
        else {
            c = new IR_Class(container, superClass, className, false);
            cMetaObj = new MetaObject(c);
            s.addInstr(new PUT_CONST_Instr(container, className, cMetaObj));
            // SSS FIXME: What happens to the add class in this case??
        }

            // Build the class body!
        if (classNode.getBodyNode() != null)
            build(classNode.getBodyNode(), c);

            // Return a meta object corresponding to the class
        return cMetaObj;
    }
*****************************************
*****************************************
SATD id: 71		Size: 58
    public Operand buildAssignment(Node node, IR_Scope s, int argIndex) {
        Operand v;
        switch (node.getNodeType()) {
            case ATTRASSIGNNODE: 
                // INCOMPLETE
                return buildAttrAssignAssignment(node, s);
// SSS FIXME:
//
// There are also differences in variable scoping between 1.8 and 1.9 
// Ruby 1.8 is the buggy semantics if I understand correctly.
//
// The semantics of how this shadows other variables outside the block needs
// to be figured out during live var analysis.
            case DASGNNODE:
                v = new Variable(((DAsgnNode)node).getName());
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                return v;
            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
            case CLASSVARASGNNODE:
                v = s.getNewTmpVariable();
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                s.addInstr(new PUT_CVAR_Instr((IR_Class)s, ((ClassVarAsgnNode)node).getName(), v));
                return v;
            case CLASSVARDECLNODE:
                v = s.getNewTmpVariable();
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                s.addInstr(new PUT_CVAR_Instr((IR_Class)s, ((ClassVarDeclNode)node).getName(), v));
                return v;
            case CONSTDECLNODE:
                v = s.getNewTmpVariable();
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                return buildConstDeclAssignment(node, s, v);
            case GLOBALASGNNODE:
                v = s.getNewTmpVariable();
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                m.addInstr(new PUT_GLOBAL_VAR_Instr(((GlobalAsgnNode)node).getName(), v));
                return v;
            case INSTASGNNODE:
                v = s.getNewTmpVariable();
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
                s.addInstr(new PUT_FIELD_Instr(s.getSelf(), ((InstAsgnNode)node).getName(), v));
                return v;
            case LOCALASGNNODE:
                v = new Variable(node.getName());
                s.addInstr(new RECV_BLOCK_ARG_Instr(v, argIndex));
                return v;
/**
            case MULTIPLEASGNNODE:
                // INCOMPLETE
                return buildMultipleAsgnAssignment(node, m);
**/
            case ZEROARGNODE:
                throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
            default:
                throw new NotCompilableException("Can't build assignment node: " + node);
        }
    }
*****************************************
*****************************************
SATD id: 72		Size: 5
    public void buildArrayArguments(List<Operand> args, Node node, IRScope s) {
        // SSS FIXME: Where does this go?
        // m.setLinePosition(arrayNode.getPosition());
        args.add(buildArray(node, s));
    }
*****************************************
*****************************************
SATD id: 73		Size: 0
*****************************************
*****************************************
SATD id: 74		Size: 4
    // There's not a compelling reason to keep JavaClass instances in a weak map
    // (any proxies created are [were] kept in a non-weak map, so in most cases they will
    // stick around anyway), and some good reasons not to (JavaClass creation is
    // expensive, for one; many lookups are performed when passing parameters to/from
*****************************************
*****************************************
SATD id: 75		Size: 9
            public void branch(Compiler context) {
                // this could probably be more efficient, and just avoid popping values for each loop
                // when no values are being generated
                if (whileNode.getBodyNode() == null) {
                    context.loadNil();
                    return;
                }
                NodeCompilerFactory.getCompiler(whileNode.getBodyNode()).compile(whileNode.getBodyNode(), context);
            }
*****************************************
*****************************************
SATD id: 76		Size: 2
// This is a dummy scope; we should find a way to make that more explicit
private final StaticScope staticScope;
*****************************************
*****************************************
SATD id: 77		Size: 39
    public static IRubyObject s_readline(ThreadContext context, IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) throws IOException {
        Ruby runtime = context.getRuntime();
        ConsoleHolder holder = getHolder(runtime);
        if (holder.readline == null) {
            initReadline(runtime, holder); // not overridden, let's go
        }
        
        IRubyObject line = runtime.getNil();
        String v = null;
        while (true) {
            try {
                holder.readline.getTerminal().disableEcho();
                v = holder.readline.readLine(prompt.toString());
                break;
            } catch (IOException ioe) {
                if (RubyIO.restartSystemCall(ioe)) {
                    continue;
                }
                throw runtime.newIOErrorFromException(ioe);
            } finally {
                // This is for JRUBY-2988, since after a suspend the terminal seems
                // to need to be reinitialized. Since we can't easily detect suspension,
                // initialize after every readline. Probably not fast, but this is for
                // interactive terminals anyway...so who cares?
                try {holder.readline.getTerminal().initializeTerminal();} catch (Exception e) {}
                holder.readline.getTerminal().enableEcho();
            }
        }
        
        if (null != v) {
            if (add_to_hist.isTrue()) {
                holder.readline.getHistory().addToHistory(v);
            }

            /* Explicitly use UTF-8 here. c.f. history.addToHistory using line.asUTF8() */
            line = RubyString.newUnicodeString(recv.getRuntime(), v);
        }
        return line;
    }
*****************************************
*****************************************
SATD id: 78		Size: 20
    private void beginClassInit() {
        ClassVisitor cv = getClassVisitor();

        clinitMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(Void.TYPE), null, null));
        clinitMethod.start();

        if (invDynSupportInstaller != null) {
            // install invokedynamic bootstrapper
            // TODO need to abstract this setup behind another compiler interface
            try {
                invDynSupportInstaller.invoke(null, clinitMethod, classname);
            } catch (IllegalAccessException ex) {
                // ignore; we won't use invokedynamic
            } catch (IllegalArgumentException ex) {
                // ignore; we won't use invokedynamic
            } catch (InvocationTargetException ex) {
                // ignore; we won't use invokedynamic
            }
        }
    }
*****************************************
*****************************************
SATD id: 79		Size: 11
    public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
        checkReadable();
        ensureReadNonBuffered();
        
        ByteList byteList = new ByteList(number);
        
        // TODO this should entry into error handling somewhere
        int bytesRead = descriptor.read(number, byteList);
        
        return byteList;
    }
*****************************************
*****************************************
SATD id: 8		Size: 8
    // FIXME: Can get optimized for IEqlNode
    private IRubyObject whenNoTest(ThreadContext context, Ruby runtime, IRubyObject self, Block aBlock) {
        if (expressionNodes.interpret(runtime, context, self, aBlock).isTrue()) {
            return bodyNode.interpret(runtime, context, self, aBlock);
        }

        return null;
    }
*****************************************
*****************************************
SATD id: 80		Size: 7
            // TODO: catch exception if constant is already set by other
            // thread
            try {
                proxy.setConstant(field.getName(), JavaUtil.convertJavaToUsableRubyObject(proxy.getRuntime(), field.get(null)));
            } catch (IllegalAccessException iae) {
                // if we can't read it, we don't set it
            }
*****************************************
*****************************************
SATD id: 81		Size: 26
    public static JavaMethod createDeclaredSmart(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
        try {
            Method method = javaClass.getDeclaredMethod(methodName, argumentTypes);
            return create(runtime, method);
        } catch (NoSuchMethodException e) {
            // search through all declared methods to find a closest match
            MethodSearch: for (Method method : javaClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    Class<?>[] targetTypes = method.getParameterTypes();
                    TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                        if (i >= targetTypes.length) continue MethodSearch;
                        
                        if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                            continue TypeScan;
                        }
                    }
                    
                    // if we get here, we found a matching method, use it
                    // TODO: choose narrowest method by continuing to search
                    return create(runtime, method);
                }
            }
            throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                    methodName);
        }
    }
*****************************************
*****************************************
SATD id: 82		Size: 8
    protected void init(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
        this.visibility = visibility;
        this.implementationClass = implementationClass;
        // TODO: Determine whether we should perhaps store non-singleton class
        // in the implementationClass
        this.protectedClass = calculateProtectedClass(implementationClass);
        this.callConfig = callConfig;
    }
*****************************************
*****************************************
SATD id: 83		Size: 36
    public void OneFixnumArgNoBlockCallInstr(OneFixnumArgNoBlockCallInstr oneFixnumArgNoBlockCallInstr) {
        if (MethodIndex.getFastFixnumOpsMethod(oneFixnumArgNoBlockCallInstr.getName()) == null) {
            CallInstr(oneFixnumArgNoBlockCallInstr);
            return;
        }
        IRBytecodeAdapter m = jvmMethod();
        String name = oneFixnumArgNoBlockCallInstr.getName();
        long fixnum = oneFixnumArgNoBlockCallInstr.getFixnumArg();
        Operand receiver = oneFixnumArgNoBlockCallInstr.getReceiver();
        Variable result = oneFixnumArgNoBlockCallInstr.getResult();

        m.loadContext();

        // for visibility checking without requiring frame self
        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
        m.loadSelf(); // caller

        visit(receiver);

        String signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class));

        m.adapter.invokedynamic(
                "fixnumOperator:" + JavaNameMangler.mangleMethodName(name),
                signature,
                InvokeDynamicSupport.getFixnumOperatorHandle(),
                fixnum,
                "",
                0);

        if (result != null) {
            jvmStoreLocal(result);
        } else {
            // still need to drop, since all dyncalls return something (FIXME)
            m.adapter.pop();
        }
    }
*****************************************
*****************************************
SATD id: 84		Size: 17
    public static RubyBoolean exec(IRubyObject recv, IRubyObject[] args) {
        Ruby runtime = recv.getRuntime();
        int resultCode;
        try {
            // TODO: exec should replace the current process.
            // This could be possible with JNA. 
            resultCode = new ShellLauncher(runtime).runAndWait(args);
        } catch (Exception e) {
            resultCode = 127;
        }

        if (resultCode != 0) {
            throw runtime.newErrnoENOENTError("cannot execute");
        }

        return runtime.newBoolean(true);
    }
*****************************************
*****************************************
SATD id: 85		Size: 9
    public RubyBoolean kind_of(IRubyObject type) {
        // TODO: Generalize this type-checking code into IRubyObject helper.
        if (!type.isKindOf(getRuntime().getClass("Module"))) {
            // TODO: newTypeError does not offer enough for ruby error string...
            throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
        }

        return getRuntime().newBoolean(isKindOf((RubyModule)type));
    }
*****************************************
*****************************************
SATD id: 86		Size: 58

protected void initializeClass() {
    includeModule(getRuntime().getClasses().getEnumerableModule());

    // TODO: Implement tty? and isatty.  We have no real capability to
    // determine this from java, but if we could set tty status, then
    // we could invoke jruby differently to allow stdin to return true
    // on this.  This would allow things like cgi.rb to work properly.

    defineSingletonMethod("foreach", Arity.optional(), "foreach");
    defineSingletonMethod("readlines", Arity.optional(), "readlines");

    CallbackFactory callbackFactory = getRuntime().callbackFactory(RubyIO.class);
    defineMethod("<<", callbackFactory.getMethod("addString", IRubyObject.class));
    defineMethod("clone", callbackFactory.getMethod("clone_IO"));
    defineMethod("close", callbackFactory.getMethod("close"));
    defineMethod("closed?", callbackFactory.getMethod("closed"));
    defineMethod("each", callbackFactory.getOptMethod("each_line"));
    defineMethod("each_byte", callbackFactory.getMethod("each_byte"));
    defineMethod("each_line", callbackFactory.getOptMethod("each_line"));
    defineMethod("eof", callbackFactory.getMethod("eof"));
    defineMethod("eof?", callbackFactory.getMethod("eof"));
    defineMethod("fileno", callbackFactory.getMethod("fileno"));
    defineMethod("flush", callbackFactory.getMethod("flush"));
    defineMethod("fsync", callbackFactory.getMethod("fsync"));
    defineMethod("getc", callbackFactory.getMethod("getc"));
    defineMethod("gets", callbackFactory.getOptMethod("gets"));
    defineMethod("initialize", Arity.optional(), "initialize");
    defineMethod("lineno", callbackFactory.getMethod("lineno"));
    defineMethod("lineno=", callbackFactory.getMethod("lineno_set", RubyFixnum.class));
    defineMethod("pid", callbackFactory.getMethod("pid"));
    defineMethod("pos", callbackFactory.getMethod("pos"));
    defineMethod("pos=", callbackFactory.getMethod("pos_set", RubyFixnum.class));
    defineMethod("print", callbackFactory.getOptSingletonMethod("print"));
    defineMethod("printf", callbackFactory.getOptSingletonMethod("printf"));
    defineMethod("putc", callbackFactory.getMethod("putc", IRubyObject.class));
    defineMethod("puts", callbackFactory.getOptSingletonMethod("puts"));
    defineMethod("read", callbackFactory.getOptMethod("read"));
    defineMethod("readchar", callbackFactory.getMethod("readchar"));
    defineMethod("readline", callbackFactory.getOptMethod("readline"));
    defineMethod("readlines", callbackFactory.getOptMethod("readlines"));
    defineMethod("reopen", callbackFactory.getOptMethod("reopen", IRubyObject.class));
    defineMethod("rewind", callbackFactory.getMethod("rewind"));
    defineMethod("seek", callbackFactory.getOptMethod("seek"));
    defineMethod("sync", callbackFactory.getMethod("sync"));
    defineMethod("sync=", callbackFactory.getMethod("sync_set", RubyBoolean.class));
    defineMethod("sysread", callbackFactory.getMethod("sysread", RubyFixnum.class));
    defineMethod("syswrite", callbackFactory.getMethod("syswrite", IRubyObject.class));
    defineMethod("tell", callbackFactory.getMethod("pos"));
    defineMethod("to_i", callbackFactory.getMethod("fileno"));
    defineMethod("ungetc", callbackFactory.getMethod("ungetc", RubyFixnum.class));
    defineMethod("write", callbackFactory.getMethod("write", IRubyObject.class));

    // Constants for seek
    setConstant("SEEK_SET", getRuntime().newFixnum(IOHandler.SEEK_SET));
    setConstant("SEEK_CUR", getRuntime().newFixnum(IOHandler.SEEK_CUR));
    setConstant("SEEK_END", getRuntime().newFixnum(IOHandler.SEEK_END));
}
*****************************************
*****************************************
SATD id: 87		Size: 4
	public boolean remove(Object element) {
        IRubyObject deleted = delete(JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
        return deleted.isNil() ? false : true; // TODO: is this correct ?
	}
*****************************************
*****************************************
SATD id: 88		Size: 18
    public IRScope buildRoot(Node node) {
        // Top-level script!
        IRScript script = new IRScript("__file__", node.getPosition().getFile());
        IRClass  rootClass = script.dummyClass;
        IRMethod rootMethod = rootClass.getRootMethod();

        // Debug info: record file name
        rootMethod.addInstr(new FilenameInstr(node.getPosition().getFile()));

        // add a "self" recv here
        // TODO: is this right?
        rootMethod.addInstr(new ReceiveArgumentInstruction(rootClass.getSelf(), 0));

        RootNode rootNode = (RootNode) node;
        build(rootNode.getBodyNode(), rootMethod);

        return script;
    }
*****************************************
*****************************************
SATD id: 89		Size: 130
    public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
       try {
           Set pending = new HashSet();
           Set unselectable_reads = new HashSet();
           Set unselectable_writes = new HashSet();
           Selector selector = Selector.open();
           if (!args[0].isNil()) {
               // read
               checkArrayType(runtime, args[0]);
               for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                   IRubyObject obj = (IRubyObject) i.next();
                   RubyIO ioObj = convertToIO(context, obj);
                   if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                       if (ioObj.writeDataBuffered()) {
                           pending.add(obj);
                       }
                   } else {
                       if (( ioObj.openFile.getMode() & OpenFile.READABLE ) != 0) {
                           unselectable_reads.add(obj);
                       }
                   }
               }
           }

           if (args.length > 1 && !args[1].isNil()) {
               // write
               checkArrayType(runtime, args[1]);
               for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                   IRubyObject obj = (IRubyObject) i.next();
                   RubyIO ioObj = convertToIO(context, obj);
                   if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                       if (( ioObj.openFile.getMode() & OpenFile.WRITABLE ) != 0) {
                           unselectable_writes.add(obj);
                       }
                   }
               }
           }

           if (args.length > 2 && !args[2].isNil()) {
               checkArrayType(runtime, args[2]);
               // Java's select doesn't do anything about this, so we leave it be.
           }

           final boolean has_timeout = ( args.length > 3 && !args[3].isNil() );
           long timeout = 0;
           if(has_timeout) {
               IRubyObject timeArg = args[3];
               if (timeArg instanceof RubyFloat) {
                   timeout = Math.round(((RubyFloat) timeArg).getDoubleValue() * 1000);
               } else if (timeArg instanceof RubyFixnum) {
                   timeout = Math.round(((RubyFixnum) timeArg).getDoubleValue() * 1000);
               } else { // TODO: MRI also can hadle Bignum here
                   throw runtime.newTypeError("can't convert "
                           + timeArg.getMetaClass().getName() + " into time interval");
               }

               if (timeout < 0) {
                   throw runtime.newArgumentError("negative timeout given");
               }
           }
           
           if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
               if (has_timeout) {
                   if (timeout==0) {
                       selector.selectNow();
                   } else {
                       selector.select(timeout);                       
                   }
               } else {
                   selector.select();
               }
           } else {
               selector.selectNow();               
           }
           
           List r = new ArrayList();
           List w = new ArrayList();
           List e = new ArrayList();
           for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey) i.next();
               try {
                   int interestAndReady = key.interestOps() & key.readyOps();
                   if ((interestAndReady
                           & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                       r.add(key.attachment());
                       pending.remove(key.attachment());
                   }
                   if ((interestAndReady & (SelectionKey.OP_WRITE)) != 0) {
                       w.add(key.attachment());
                   }
               } catch (CancelledKeyException cke) {
                   // TODO: is this the right thing to do?
                   pending.remove(key.attachment());
                   e.add(key.attachment());
               }
           }
           r.addAll(pending);
           r.addAll(unselectable_reads);
           w.addAll(unselectable_writes);
           
           // make all sockets blocking as configured again
           for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey) i.next();
               SelectableChannel channel = key.channel();
               synchronized(channel.blockingLock()) {
                   RubyIO originalIO = (RubyIO) TypeConverter.convertToType(
                           (IRubyObject) key.attachment(), runtime.getIO(),
                           MethodIndex.TO_IO, "to_io");
                   boolean blocking = originalIO.getBlocking();
                   key.cancel();
                   channel.configureBlocking(blocking);
               }
           }
           selector.close();
           
           if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
               return runtime.getNil();
           }
           
           List ret = new ArrayList();
           
           ret.add(RubyArray.newArray(runtime, r));
           ret.add(RubyArray.newArray(runtime, w));
           ret.add(RubyArray.newArray(runtime, e));
           
           return RubyArray.newArray(runtime, ret);
       } catch(IOException e) {
           throw runtime.newIOError(e.getMessage());
       }
   }
*****************************************
*****************************************
SATD id: 9		Size: 11
    private void resetForWrite() throws IOException {
        if (descriptor.isSeekable()) {
            FileChannel fileChannel = (FileChannel)descriptor.getChannel();
            if (buffer.hasRemaining()) { // we have read ahead, and need to back up
                fileChannel.position(fileChannel.position() - buffer.remaining());
            }
        }
        // FIXME: Clearing read buffer here...is this appropriate?
        buffer.clear();
        reading = false;
    }
*****************************************
*****************************************
SATD id: 90		Size: 35
    public long lseek(ChannelFD fd, long offset, int type) {
        clear();

        if (fd.chSeek != null) {
            int adj = 0;
            try {
                switch (type) {
                    case SEEK_SET:
                        return fd.chSeek.position(offset).position();
                    case SEEK_CUR:
                        return fd.chSeek.position(fd.chSeek.position() - adj + offset).position();
                    case SEEK_END:
                        return fd.chSeek.position(fd.chSeek.size() + offset).position();
                    default:
                        errno = Errno.EINVAL;
                        return -1;
                }
            } catch (IllegalArgumentException e) {
                errno = Errno.EINVAL;
                return -1;
            } catch (IOException ioe) {
                errno = Helpers.errnoFromException(ioe);
                return -1;
            }
        } else if (fd.chSelect != null) {
            // TODO: It's perhaps just a coincidence that all the channels for
            // which we should raise are instanceof SelectableChannel, since
            // stdio is not...so this bothers me slightly. -CON
            errno = Errno.EPIPE;
            return -1;
        } else {
            errno = Errno.EPIPE;
            return -1;
        }
    }
*****************************************
*****************************************
SATD id: 91		Size: 229
    public static Class defineOldStyleImplClass(Ruby ruby, String name, String[] superTypeNames, Map<String, List<Method>> simpleToAll) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String pathName = name.replace('.', '/');
        
        // construct the class, implementing all supertypes
        cw.visit(V1_5, ACC_PUBLIC | ACC_SUPER, pathName, null, p(Object.class), superTypeNames);
        cw.visitSource(pathName + ".gen", null);
        
        // fields needed for dispatch and such
        cw.visitField(ACC_STATIC | ACC_PRIVATE, "$ruby", ci(Ruby.class), null, null).visitEnd();
        cw.visitField(ACC_STATIC | ACC_PRIVATE, "$rubyClass", ci(RubyClass.class), null, null).visitEnd();
        cw.visitField(ACC_PRIVATE | ACC_FINAL, "$self", ci(IRubyObject.class), null, null).visitEnd();
        
        // create constructor
        SkinnyMethodAdapter initMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class, IRubyObject.class), null, null));
        initMethod.aload(0);
        initMethod.invokespecial(p(Object.class), "<init>", sig(void.class));
        
        // store the wrapper
        initMethod.aload(0);
        initMethod.aload(1);
        initMethod.putfield(pathName, "$self", ci(IRubyObject.class));
        
        // end constructor
        initMethod.voidreturn();
        initMethod.end();
        
        // start setup method
        SkinnyMethodAdapter setupMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_STATIC | ACC_PUBLIC | ACC_SYNTHETIC, "__setup__", sig(void.class, RubyClass.class), null, null));
        setupMethod.start();
        
        // set RubyClass
        setupMethod.aload(0);
        setupMethod.dup();
        setupMethod.putstatic(pathName, "$rubyClass", ci(RubyClass.class));
        
        // set Ruby
        setupMethod.invokevirtual(p(RubyClass.class), "getClassRuntime", sig(Ruby.class));
        setupMethod.putstatic(pathName, "$ruby", ci(Ruby.class));
        
        // for each simple method name, implement the complex methods, calling the simple version
        for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
            String simpleName = entry.getKey();
            Set<String> nameSet = JavaUtil.getRubyNamesForJavaName(simpleName, entry.getValue());

            // set up a field for the CacheEntry
            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
            cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, simpleName, ci(CacheEntry.class), null, null).visitEnd();
            setupMethod.getstatic(p(CacheEntry.class), "NULL_CACHE", ci(CacheEntry.class));
            setupMethod.putstatic(pathName, simpleName, ci(CacheEntry.class));

            Set<String> implementedNames = new HashSet<String>();
            
            for (Method method : entry.getValue()) {
                Class[] paramTypes = method.getParameterTypes();
                Class returnType = method.getReturnType();

                String fullName = simpleName + prettyParams(paramTypes);
                if (implementedNames.contains(fullName)) continue;
                implementedNames.add(fullName);
                
                SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                        cw.visitMethod(ACC_PUBLIC, simpleName, sig(returnType, paramTypes), null, null));
                mv.start();
                mv.line(1);
                
                // TODO: this code should really check if a Ruby equals method is implemented or not.
                if(simpleName.equals("equals") && paramTypes.length == 1 && paramTypes[0] == Object.class && returnType == Boolean.TYPE) {
                    mv.line(2);
                    mv.aload(0);
                    mv.aload(1);
                    mv.invokespecial(p(Object.class), "equals", sig(Boolean.TYPE, params(Object.class)));
                    mv.ireturn();
                } else if(simpleName.equals("hashCode") && paramTypes.length == 0 && returnType == Integer.TYPE) {
                    mv.line(3);
                    mv.aload(0);
                    mv.invokespecial(p(Object.class), "hashCode", sig(Integer.TYPE));
                    mv.ireturn();
                } else if(simpleName.equals("toString") && paramTypes.length == 0 && returnType == String.class) {
                    mv.line(4);
                    mv.aload(0);
                    mv.invokespecial(p(Object.class), "toString", sig(String.class));
                    mv.areturn();
                } else {
                    mv.line(5);
                    
                    Label dispatch = new Label();
                    Label end = new Label();
                    Label recheckMethod = new Label();

                    // Try to look up field for simple name

                    // get field; if nonnull, go straight to dispatch
                    mv.getstatic(pathName, simpleName, ci(CacheEntry.class));
                    mv.dup();
                    mv.aload(0);
                    mv.getfield(pathName, "$self", ci(IRubyObject.class));
                    mv.invokestatic(p(MiniJava.class), "isCacheOk", sig(boolean.class, params(CacheEntry.class, IRubyObject.class)));
                    mv.iftrue(dispatch);

                    // field is null, lock class and try to populate
                    mv.line(6);
                    mv.pop();
                    mv.getstatic(pathName, "$rubyClass", ci(RubyClass.class));
                    mv.monitorenter();

                    // try/finally block to ensure unlock
                    Label tryStart = new Label();
                    Label tryEnd = new Label();
                    Label finallyStart = new Label();
                    Label finallyEnd = new Label();
                    mv.line(7);
                    mv.label(tryStart);

                    mv.aload(0);
                    mv.getfield(pathName, "$self", ci(IRubyObject.class));
                    for (String eachName : nameSet) {
                        mv.ldc(eachName);
                    }
                    mv.invokestatic(p(MiniJava.class), "searchWithCache", sig(CacheEntry.class, params(IRubyObject.class, String.class, nameSet.size())));

                    // store it
                    mv.putstatic(pathName, simpleName, ci(CacheEntry.class));

                    // all done with lookup attempts, release monitor
                    mv.getstatic(pathName, "$rubyClass", ci(RubyClass.class));
                    mv.monitorexit();
                    mv.go_to(recheckMethod);

                    // end of try block
                    mv.label(tryEnd);

                    // finally block to release monitor
                    mv.label(finallyStart);
                    mv.line(9);
                    mv.getstatic(pathName, "$rubyClass", ci(RubyClass.class));
                    mv.monitorexit();
                    mv.label(finallyEnd);
                    mv.athrow();

                    // exception handling for monitor release
                    mv.trycatch(tryStart, tryEnd, finallyStart, null);
                    mv.trycatch(finallyStart, finallyEnd, finallyStart, null);

                    // re-get, re-check method; if not null now, go to dispatch
                    mv.label(recheckMethod);
                    mv.line(10);
                    mv.getstatic(pathName, simpleName, ci(CacheEntry.class));
                    mv.dup();
                    mv.getfield(p(CacheEntry.class), "method", ci(DynamicMethod.class));
                    mv.invokevirtual(p(DynamicMethod.class), "isUndefined", sig(boolean.class));
                    mv.iffalse(dispatch);

                    // method still not available, call method_missing
                    mv.line(11);
                    mv.pop();
                    // exit monitor before making call
                    // FIXME: this not being in a finally is a little worrisome
                    mv.aload(0);
                    mv.getfield(pathName, "$self", ci(IRubyObject.class));
                    mv.ldc(simpleName);
                    coerceArgumentsToRuby(mv, paramTypes, pathName);
                    mv.invokestatic(p(RuntimeHelpers.class), "invokeMethodMissing", sig(IRubyObject.class, IRubyObject.class, String.class, IRubyObject[].class));
                    mv.go_to(end);
                
                    // perform the dispatch
                    mv.label(dispatch);
                    mv.line(12, dispatch);
                    // get current context
                    mv.getfield(p(CacheEntry.class), "method", ci(DynamicMethod.class));
                    mv.getstatic(pathName, "$ruby", ci(Ruby.class));
                    mv.invokevirtual(p(Ruby.class), "getCurrentContext", sig(ThreadContext.class));
                
                    // load self, class, and name
                    mv.aload(0);
                    mv.getfield(pathName, "$self", ci(IRubyObject.class));
                    mv.getstatic(pathName, "$rubyClass", ci(RubyClass.class));
                    mv.ldc(simpleName);
                
                    // coerce arguments
                    coerceArgumentsToRuby(mv, paramTypes, pathName);
                
                    // load null block
                    mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
                
                    // invoke method
                    mv.line(13);
                    mv.invokevirtual(p(DynamicMethod.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
                
                    mv.label(end);
                    coerceResultAndReturn(method, mv, returnType);
                }                
                mv.end();
            }
        }
        
        // end setup method
        setupMethod.voidreturn();
        setupMethod.end();
        
        // end class
        cw.visitEnd();
        
        // create the class
        byte[] bytes = cw.toByteArray();
        Class newClass;
        synchronized (ruby.getJRubyClassLoader()) {
            // try to load the specified name; only if that fails, try to define the class
            try {
                newClass = ruby.getJRubyClassLoader().loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                newClass = ruby.getJRubyClassLoader().defineClass(name, cw.toByteArray());
            }
        }
        
        if (DEBUG) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(name + ".class");
                fos.write(bytes);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {fos.close();} catch (Exception e) {}
            }
        }
        
        return newClass;
    }
*****************************************
*****************************************
SATD id: 92		Size: 39
    private static void coerceArgumentsToRuby(SkinnyMethodAdapter mv, Class[] paramTypes, String name) {
        // load arguments into IRubyObject[] for dispatch
        if (paramTypes.length != 0) {
            mv.pushInt(paramTypes.length);
            mv.anewarray(p(IRubyObject.class));

            // TODO: make this do specific-arity calling
            for (int i = 0; i < paramTypes.length; i++) {
                mv.dup();
                mv.pushInt(i);
                // convert to IRubyObject
                mv.getstatic(name, "ruby", ci(Ruby.class));
                if (paramTypes[i].isPrimitive()) {
                    if (paramTypes[i] == byte.class || paramTypes[i] == short.class || paramTypes[i] == char.class || paramTypes[i] == int.class) {
                        mv.iload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, int.class));
                    } else if (paramTypes[i] == long.class) {
                        mv.lload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, long.class));
                    } else if (paramTypes[i] == float.class) {
                        mv.fload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, float.class));
                    } else if (paramTypes[i] == double.class) {
                        mv.dload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, double.class));
                    } else if (paramTypes[i] == boolean.class) {
                        mv.iload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, boolean.class));
                    }
                } else {
                    mv.aload(i + 1);
                    mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, Object.class));
                }
                mv.aastore();
            }
        } else {
            mv.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
        }
    }
*****************************************
*****************************************
SATD id: 93		Size: 4
    public String toString() {
        // TODO: Make this more intelligible value
        return ""+flags;
    }
*****************************************
*****************************************
SATD id: 94		Size: 4
    public void emitRECV_ARG(RECV_ARG_Instr recvArg) {
        int index = getVariableIndex(recvArg._result);
        // TODO: need to get this back into the method signature...now is too late...
    }
*****************************************
*****************************************
SATD id: 95		Size: 9
    public RubyBoolean kind_of(IRubyObject type) {
        // TODO: Generalize this type-checking code into IRubyObject helper.
        if (!type.isKindOf(getRuntime().getClass("Module"))) {
            // TODO: newTypeError does not offer enough for ruby error string...
            throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
        }

        return getRuntime().newBoolean(isKindOf((RubyModule)type));
    }
*****************************************
*****************************************
SATD id: 96		Size: 10
    public synchronized IRubyObject status() {
        if (threadImpl.isAlive()) {
            // TODO: no java stringity
            return getRuntime().newString(status.toString().toLowerCase());
        } else if (exitingException != null) {
            return getRuntime().getNil();
        } else {
            return getRuntime().getFalse();
        }
    }
*****************************************
*****************************************
SATD id: 97		Size: 17
    public static RubyClass createJavaClassClass(Ruby runtime, RubyModule javaModule) {
        // FIXME: Determine if a real allocator is needed here. Do people want to extend
        // JavaClass? Do we want them to do that? Can you Class.new(JavaClass)? Should
        // you be able to?
        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
        // this type and it can't be marshalled. Confirm. JRUBY-415
        RubyClass result = javaModule.defineClassUnder("JavaClass", javaModule.getClass("JavaObject"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
        
        result.includeModule(runtime.getModule("Comparable"));
        
        result.defineAnnotatedMethods(ClassJavaAddons.class);

        result.getMetaClass().undefineMethod("new");
        result.getMetaClass().undefineMethod("allocate");

        return result;
    }
*****************************************
*****************************************
SATD id: 98		Size: 29
    public static RubyClass createStructClass(IRuby runtime) {
        // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
        // work with marshalling. Confirm behavior and ensure we're doing this correctly. JRUBY-415
        RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
        CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
        structClass.includeModule(runtime.getModule("Enumerable"));

        structClass.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));

        structClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
        structClass.defineMethod("clone", callbackFactory.getMethod("rbClone"));

        structClass.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));

        structClass.defineFastMethod("to_s", callbackFactory.getMethod("to_s"));
        structClass.defineFastMethod("inspect", callbackFactory.getMethod("inspect"));
        structClass.defineFastMethod("to_a", callbackFactory.getMethod("to_a"));
        structClass.defineFastMethod("values", callbackFactory.getMethod("to_a"));
        structClass.defineFastMethod("size", callbackFactory.getMethod("size"));
        structClass.defineFastMethod("length", callbackFactory.getMethod("size"));

        structClass.defineMethod("each", callbackFactory.getMethod("each"));
        structClass.defineFastMethod("[]", callbackFactory.getMethod("aref", IRubyObject.class));
        structClass.defineFastMethod("[]=", callbackFactory.getMethod("aset", IRubyObject.class, IRubyObject.class));

        structClass.defineFastMethod("members", callbackFactory.getMethod("members"));

        return structClass;
    }
*****************************************
*****************************************
SATD id: 99		Size: 13
    public JavaObject static_value() {
        try {
	    // TODO: Only setAccessible to account for pattern found by
	    // accessing constants included from a non-public interface.
	    // (aka java.util.zip.ZipConstants being implemented by many
	    // classes)
	    field.setAccessible(true);
            return JavaObject.wrap(getRuntime(), field.get(null));
        } catch (IllegalAccessException iae) {
	    throw new TypeError(getRuntime(),
				"illegal static value access: " + iae.getMessage());
        }
    }
*****************************************
*****************************************
