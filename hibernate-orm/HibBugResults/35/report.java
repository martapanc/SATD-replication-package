File path: hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
Comment: todo : YUCK!!!
Initial commit id: 8a5415d3
Final commit id: 66ce8b7f
   Bugs between [      12]:
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8fe5460ec0 HHH-8741 - More checkstyle cleanups
cd590470c0 HHH-8741 - More checkstyle cleanups
cf903b78f0 HHH-8354 - New dirty-checking options based on bytecode enhancement
0433a539b4 HHH-3078 copyright and refactored package
228d14e8bc HHH-3078 Fixed bug with multiple classloaders and proxy class
aef27fec41 HHH-6735 Let Core use InstrumentationService instead of FieldIinterceptionHelper directly
dc7feab061 HHH-6498 HHH-6337 : Updates to support single-table inheritance using new metamodel
e540089783 HHH-6480 - Develop component binding for new metamodel
46102a2be3 HHH-6471 - Redesign how EntityBinding models hierarchy-shared information
24edf42c04 HHH-6371 - Develop metamodel binding creation using a push approach
e339dac91e HHH-6371 - Develop metamodel binding creation using a push approach
   Bugs after [      10]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
1e44e7420b HHH-10267 - Support defining lazy attribute fetch groups
472f4ab9ef HHH-10280 - Remove legacy bytecode enhancement artifacts
6d77ac39c5 HHH-9937 - bytecode enhancement - expose loaded state in LazyAttributeLoader
c6fa2b1df1 HHH-8558 - Bytecode enhancer: lazy loading support
5b1da92498 HHH-9838 - Leverage ClassLoaderService during JavassistLazyInitializer#getProxyFactory
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups

Start block index: 79
End block index: 113
	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
		super( entityMetamodel, mappedEntity );
		this.mappedClass = mappedEntity.getMappedClass();
		this.proxyInterface = mappedEntity.getProxyInterface();
		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );

		Iterator iter = mappedEntity.getPropertyClosureIterator();
		while ( iter.hasNext() ) {
			Property property = (Property) iter.next();
			if ( property.isLazy() ) {
				lazyPropertyNames.add( property.getName() );
			}
		}

		String[] getterNames = new String[propertySpan];
		String[] setterNames = new String[propertySpan];
		Class[] propTypes = new Class[propertySpan];
		for ( int i = 0; i < propertySpan; i++ ) {
			getterNames[i] = getters[i].getMethodName();
			setterNames[i] = setters[i].getMethodName();
			propTypes[i] = getters[i].getReturnType();
		}

		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
			optimizer = null;
		}
		else {
			// todo : YUCK!!!
			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
//			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
//					mappedClass, getterNames, setterNames, propTypes
//			);
		}

	}
