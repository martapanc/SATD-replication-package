File path: src/org/jruby/java/MiniJava.java
Comment: / TODO: make this an array so it's not as much class metadata
Initial commit id: ba40f9cc
Final commit id: 02b8e01b
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 356
End block index: 584
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
