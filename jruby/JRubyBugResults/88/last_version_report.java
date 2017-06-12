public IRScope buildRoot(RootNode rootNode) {
    String file = rootNode.getPosition().getFile();
    StaticScope staticScope = rootNode.getStaticScope();

    // Top-level script!
    IRScript script = new IRScript("__file__", file, rootNode.getStaticScope());
    IRClass  rootClass = script.getRootClass();
    IRMethod rootMethod = rootClass.getRootMethod();

    // Debug info: record file name
    rootMethod.addInstr(new FilenameInstr(file));

    // Get going!
    build(rootNode.getBodyNode(), rootMethod);

    return script;
}
