File path: src/org/jruby/internal/runtime/methods/DynamicMethod.erb
Comment: / TODO: Determine whether we should perhaps store non-singleton class
Initial commit id: f9b01edc
Final commit id: 885e2352
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 88
End block index: 95
    protected void init(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
        this.visibility = visibility;
        this.implementationClass = implementationClass;
        // TODO: Determine whether we should perhaps store non-singleton class
        // in the implementationClass
        this.protectedClass = calculateProtectedClass(implementationClass);
        this.callConfig = callConfig;
    }
