File path: hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
Comment: ODO: this is temporary in that the end result will probably not take a Property reference per-se.
Initial commit id: 1d26ac1e
Final commit id: 9e063ffa
   Bugs between [       5]:
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
7308e14fed HHH-9803 - Checkstyle fix ups
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
   Bugs after [       0]:


Start block index: 71
End block index: 82
	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {
		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
	    if ( null == mode || EntityMode.POJO.equals( mode ) ) {
		    return getPojoPropertyAccessor( property.getPropertyAccessorName() );
	    }
	    else if ( EntityMode.MAP.equals( mode ) ) {
		    return getDynamicMapPropertyAccessor();
	    }
	    else {
		    throw new MappingException( "Unknown entity mode [" + mode + "]" );
	    }
	}
