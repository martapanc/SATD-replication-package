File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: todo : what is the implication of this?
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 418
End block index: 445
	private static void bindCompositeId(Element idNode, RootClass entity, Mappings mappings,
			java.util.Map inheritedMetas) throws MappingException {
		String propertyName = idNode.attributeValue( "name" );
		Component id = new Component( entity );
		entity.setIdentifier( id );
		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
		if ( propertyName == null ) {
			entity.setEmbeddedIdentifier( id.isEmbedded() );
			if ( id.isEmbedded() ) {
				// todo : what is the implication of this?
				id.setDynamic( !entity.hasPojoRepresentation() );
				/*
				 * Property prop = new Property(); prop.setName("id");
				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
				 * entity.setIdentifierProperty(prop);
				 */
			}
		}
		else {
			Property prop = new Property();
			prop.setValue( id );
			bindProperty( idNode, prop, mappings, inheritedMetas );
			entity.setIdentifierProperty( prop );
		}

		makeIdentifier( idNode, id, mappings );

	}
