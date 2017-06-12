File path: code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
Comment: ODO: need some caching scheme? really comes down to decision
Initial commit id: d8d6d82e
Final commit id: 4a4f636c
   Bugs between [       0]:

   Bugs after [       6]:
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
7308e14fed HHH-9803 - Checkstyle fix ups
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
1d26ac1e12 HHH-6360 : Build basic properties from an AttributeBinding

Start block index: 93
End block index: 98
	public static PropertyAccessor getDom4jPropertyAccessor(String nodeName, Type type, SessionFactoryImplementor factory) 
	throws MappingException {
		//TODO: need some caching scheme? really comes down to decision 
		//      regarding amount of state (if any) kept on PropertyAccessors
		return new Dom4jAccessor( nodeName, type, factory );
	}
