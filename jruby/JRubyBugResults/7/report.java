File path: core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
Comment: / FIXME: bit of a kludge here (non-interface classes assigned to both
Initial commit id: be313444
Final commit id: 55927e21
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 23
End block index: 103
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
