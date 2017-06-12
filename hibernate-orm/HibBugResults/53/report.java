File path: hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
Comment: ODO: design new lifecycle for ProxyFactory
Initial commit id: 8a5415d3
Final commit id: 66ce8b7f
   Bugs between [       4]:
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8fe5460ec0 HHH-8741 - More checkstyle cleanups
cd590470c0 HHH-8741 - More checkstyle cleanups
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
   Bugs after [       4]:
1e44e7420b HHH-10267 - Support defining lazy attribute fetch groups
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups

Start block index: 107
End block index: 126
    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {

		ProxyFactory pf = new MapProxyFactory();
		try {
			//TODO: design new lifecycle for ProxyFactory
			pf.postInstantiate(
					getEntityName(),
					null,
					null,
					null,
					null,
					null
			);
		}
		catch ( HibernateException he ) {
            LOG.unableToCreateProxyFactory(getEntityName(), he);
			pf = null;
		}
		return pf;
	}
