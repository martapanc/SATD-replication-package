File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2287
End block index: 2365
	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {

		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );

		Iterator iter = node.elementIterator();
		while ( iter.hasNext() ) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();

			if ( "index".equals( name ) || "map-key".equals( name ) ) {
				SimpleValue value = new SimpleValue( map.getCollectionTable() );
				bindSimpleValue(
						subnode,
						value,
						map.isOneToMany(),
						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
						mappings
					);
				if ( !value.isTypeSpecified() ) {
					throw new MappingException( "map index element must specify a type: "
						+ map.getRole() );
				}
				map.setIndex( value );
				map.setIndexNodeName( subnode.attributeValue("node") );
			}
			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
				ManyToOne mto = new ManyToOne( map.getCollectionTable() );
				bindManyToOne(
						subnode,
						mto,
						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
						map.isOneToMany(),
						mappings
					);
				map.setIndex( mto );

			}
			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
				Component component = new Component( map );
				bindComposite(
						subnode,
						component,
						map.getRole() + ".index",
						map.isOneToMany(),
						mappings,
						inheritedMetas
					);
				map.setIndex( component );
			}
			else if ( "index-many-to-any".equals( name ) ) {
				Any any = new Any( map.getCollectionTable() );
				bindAny( subnode, any, map.isOneToMany(), mappings );
				map.setIndex( any );
			}
		}

		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
		boolean indexIsFormula = false;
		Iterator colIter = map.getIndex().getColumnIterator();
		while ( colIter.hasNext() ) {
			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
		}

		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
			PersistentClass referenced = mappings.getClass( entityName );
			IndexBackref ib = new IndexBackref();
			ib.setName( '_' + node.attributeValue( "name" ) + "IndexBackref" );
			ib.setUpdateable( false );
			ib.setSelectable( false );
			ib.setCollectionRole( map.getRole() );
			ib.setEntityName( map.getOwner().getEntityName() );
			ib.setValue( map.getIndex() );
			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
			// ).setNullable(false);
			referenced.addProperty( ib );
		}
	}
