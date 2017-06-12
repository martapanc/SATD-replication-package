File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: ODO: really bad
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 1536
End block index: 1566
	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
			boolean isNullable, Mappings mappings) throws MappingException {

		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
		initOuterJoinFetchSetting( node, manyToOne );
		initLaziness( node, manyToOne, mappings, true );

		Attribute ukName = node.attribute( "property-ref" );
		if ( ukName != null ) {
			manyToOne.setReferencedPropertyName( ukName.getValue() );
		}

		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );

		String embed = node.attributeValue( "embed-xml" );
		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );

		String notFound = node.attributeValue( "not-found" );
		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );

		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
			}
		}

		Attribute fkNode = node.attribute( "foreign-key" );
		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );

		validateCascade( node, path );
	}
