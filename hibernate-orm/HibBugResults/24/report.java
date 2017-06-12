File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: this is done here 'cos we might only know the type here (ugly!)
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2161
End block index: 2198
	private static Property createProperty(
			final Value value,
	        final String propertyName,
			final String className,
	        final Element subnode,
	        final Mappings mappings,
			java.util.Map inheritedMetas) throws MappingException {

		if ( StringHelper.isEmpty( propertyName ) ) {
			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
		}

		value.setTypeUsingReflection( className, propertyName );

		// this is done here 'cos we might only know the type here (ugly!)
		// TODO: improve this a lot:
		if ( value instanceof ToOne ) {
			ToOne toOne = (ToOne) value;
			String propertyRef = toOne.getReferencedPropertyName();
			if ( propertyRef != null ) {
				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
			}
		}
		else if ( value instanceof Collection ) {
			Collection coll = (Collection) value;
			String propertyRef = coll.getReferencedPropertyName();
			// not necessarily a *unique* property reference
			if ( propertyRef != null ) {
				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
			}
		}

		value.createForeignKey();
		Property prop = new Property();
		prop.setValue( value );
		bindProperty( subnode, prop, mappings, inheritedMetas );
		return prop;
	}
