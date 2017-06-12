File path: src/core/org/apache/jmeter/util/JSR223TestElement.java
Comment: Hack: bsh-2.0b5.jar BshScriptEngine implements Compilable but throws
Initial commit id: 290f9776
Final commit id: 3fb51fc0
   Bugs between [       5]:
3fb51fc00 Bug 60813 - JSR223 Test element : Take into account JMeterStopTestNowException, JMeterStopTestException and JMeterStopThreadException Bugzilla Id: 60813
ea7682133 Bug 60564 - Migrating LogKit to SLF4J - core/testelement,threads,util,visualizers package Contributed by Woonsan Ko This closes #273 Bugzilla Id: 60564
2e36b4f4d Bug 59945 For all JSR223 elements, if script language has not been chosen on the UI, the script will be interpreted as a groovy script A getXXX method should not change state, so read every time and default (slight performance loss) Add javadocs Drop unused log field Increment serialVersionId
4f208f5c5 Bug 56554 : the script cache key is now automatically generated #resolve #83 Bugzilla Id: 56554
f023972db Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
   Bugs after [       0]:


Start block index: 137
End block index: 206
    protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {
        if (bindings == null) {
            bindings = scriptEngine.createBindings();
        }
        populateBindings(bindings);
        File scriptFile = new File(getFilename());
        // Hack: bsh-2.0b5.jar BshScriptEngine implements Compilable but throws "java.lang.Error: unimplemented"
        boolean supportsCompilable = scriptEngine instanceof Compilable
                && !(scriptEngine.getClass().getName().equals("bsh.engine.BshScriptEngine"));
        if (!StringUtils.isEmpty(getFilename())) {
            if (scriptFile.exists() && scriptFile.canRead()) {
                BufferedReader fileReader = null;
                try {
                    if (supportsCompilable) {
                        String cacheKey =
                                getScriptLanguage()+"#"+
                                scriptFile.getAbsolutePath()+"#"+
                                        scriptFile.lastModified();
                        CompiledScript compiledScript =
                                compiledScriptsCache.get(cacheKey);
                        if (compiledScript==null) {
                            synchronized (compiledScriptsCache) {
                                compiledScript =
                                        compiledScriptsCache.get(cacheKey);
                                if (compiledScript==null) {
                                    // TODO Charset ?
                                    fileReader = new BufferedReader(new FileReader(scriptFile),
                                            (int)scriptFile.length());
                                    compiledScript =
                                            ((Compilable) scriptEngine).compile(fileReader);
                                    compiledScriptsCache.put(cacheKey, compiledScript);
                                }
                            }
                        }
                        return compiledScript.eval(bindings);
                    } else {
                        // TODO Charset ?
                        fileReader = new BufferedReader(new FileReader(scriptFile),
                                (int)scriptFile.length());
                        return scriptEngine.eval(fileReader, bindings);
                    }
                } finally {
                    IOUtils.closeQuietly(fileReader);
                }
            }  else {
                throw new ScriptException("Script file '"+scriptFile.getAbsolutePath()+"' does not exist or is unreadable for element:"+getName());
            }
        } else if (!StringUtils.isEmpty(getScript())){
            if (supportsCompilable && !StringUtils.isEmpty(cacheKey)) {
                CompiledScript compiledScript =
                        compiledScriptsCache.get(cacheKey);
                if (compiledScript==null) {
                    synchronized (compiledScriptsCache) {
                        compiledScript =
                                compiledScriptsCache.get(cacheKey);
                        if (compiledScript==null) {
                            compiledScript =
                                    ((Compilable) scriptEngine).compile(getScript());
                            compiledScriptsCache.put(cacheKey, compiledScript);
                        }
                    }
                }
                return compiledScript.eval(bindings);
            } else {
                return scriptEngine.eval(getScript(), bindings);
            }
        } else {
            throw new ScriptException("Both script file and script text are empty for element:"+getName());
        }
    }

*********************** Method when SATD was removed **************************

    protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings)
            throws IOException, ScriptException {
        if (bindings == null) {
            bindings = scriptEngine.createBindings();
        }
        populateBindings(bindings);
        File scriptFile = new File(getFilename());
        // Hack: bsh-2.0b5.jar BshScriptEngine implements Compilable but throws
        // "java.lang.Error: unimplemented"
        boolean supportsCompilable = scriptEngine instanceof Compilable
                && !("bsh.engine.BshScriptEngine".equals(scriptEngine.getClass().getName())); // NOSONAR // $NON-NLS-1$
        try {
            if (!StringUtils.isEmpty(getFilename())) {
                if (scriptFile.exists() && scriptFile.canRead()) {
                    if (supportsCompilable) {
                        String cacheKey = getScriptLanguage() + "#" + // $NON-NLS-1$
                                scriptFile.getAbsolutePath() + "#" + // $NON-NLS-1$
                                scriptFile.lastModified();
                        CompiledScript compiledScript = compiledScriptsCache.get(cacheKey);
                        if (compiledScript == null) {
                            synchronized (compiledScriptsCache) {
                                compiledScript = compiledScriptsCache.get(cacheKey);
                                if (compiledScript == null) {
                                    // TODO Charset ?
                                    try (BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile),
                                            (int) scriptFile.length())) {
                                        compiledScript = ((Compilable) scriptEngine).compile(fileReader);
                                        compiledScriptsCache.put(cacheKey, compiledScript);
                                    }
                                }
                            }
                        }
                        return compiledScript.eval(bindings);
                    } else {
                        // TODO Charset ?
                        try (BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile),
                                (int) scriptFile.length())) {
                            return scriptEngine.eval(fileReader, bindings);
                        }
                    }
                } else {
                    throw new ScriptException("Script file '" + scriptFile.getAbsolutePath()
                            + "' does not exist or is unreadable for element:" + getName());
                }
            } else if (!StringUtils.isEmpty(getScript())) {
                if (supportsCompilable && !StringUtils.isEmpty(cacheKey)) {
                    computeScriptMD5();
                    CompiledScript compiledScript = compiledScriptsCache.get(this.scriptMd5);
                    if (compiledScript == null) {
                        synchronized (compiledScriptsCache) {
                            compiledScript = compiledScriptsCache.get(this.scriptMd5);
                            if (compiledScript == null) {
                                compiledScript = ((Compilable) scriptEngine).compile(getScript());
                                compiledScriptsCache.put(this.scriptMd5, compiledScript);
                            }
                        }
                    }

                    return compiledScript.eval(bindings);
                } else {
                    return scriptEngine.eval(getScript(), bindings);
                }
            } else {
                throw new ScriptException("Both script file and script text are empty for element:" + getName());
            }
        } catch (ScriptException ex) {
            Throwable rootCause = ex.getCause();
            if(isStopCondition(rootCause)) {
                throw (RuntimeException) ex.getCause();
            } else {
                throw ex;
            }
        }
    }
