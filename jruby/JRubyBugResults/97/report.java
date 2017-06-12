File path: jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
Comment: / TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here
Initial commit id: f9b28c47
Final commit id: 14226fde
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 1206
End block index: 1222
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
