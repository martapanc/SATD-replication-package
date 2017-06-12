    public int run(NGContext context) {
        context.assertLoopbackClient();

        RubyInstanceConfig config = new RubyInstanceConfig();
        Main main = new Main(config);
        
        config.setCurrentDirectory(context.getWorkingDirectory());
        config.setEnvironment(context.getEnv());

        // reuse one cache of compiled bodies
        config.setClassCache(classCache);

        return main.run(context.getArgs()).getStatus();
    }
