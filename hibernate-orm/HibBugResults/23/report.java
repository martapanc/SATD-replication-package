File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: This inner class implements a case statement....perhaps im being a bit over-clever here
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2739
End block index: 2825
	// This inner class implements a case statement....perhaps im being a bit over-clever here
	abstract static class CollectionType {
		private String xmlTag;

		public abstract Collection create(Element node, String path, PersistentClass owner,
				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;

		CollectionType(String xmlTag) {
			this.xmlTag = xmlTag;
		}

		public String toString() {
			return xmlTag;
		}

		private static final CollectionType MAP = new CollectionType( "map" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				Map map = new Map( owner );
				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
				return map;
			}
		};
		private static final CollectionType SET = new CollectionType( "set" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				Set set = new Set( owner );
				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
				return set;
			}
		};
		private static final CollectionType LIST = new CollectionType( "list" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				List list = new List( owner );
				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
				return list;
			}
		};
		private static final CollectionType BAG = new CollectionType( "bag" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				Bag bag = new Bag( owner );
				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
				return bag;
			}
		};
		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				IdentifierBag bag = new IdentifierBag( owner );
				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
				return bag;
			}
		};
		private static final CollectionType ARRAY = new CollectionType( "array" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				Array array = new Array( owner );
				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
				return array;
			}
		};
		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
			public Collection create(Element node, String path, PersistentClass owner,
					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
				PrimitiveArray array = new PrimitiveArray( owner );
				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
				return array;
			}
		};
		private static final HashMap INSTANCES = new HashMap();

		static {
			INSTANCES.put( MAP.toString(), MAP );
			INSTANCES.put( BAG.toString(), BAG );
			INSTANCES.put( IDBAG.toString(), IDBAG );
			INSTANCES.put( SET.toString(), SET );
			INSTANCES.put( LIST.toString(), LIST );
			INSTANCES.put( ARRAY.toString(), ARRAY );
			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
		}

		public static CollectionType collectionTypeFromString(String xmlTagName) {
			return (CollectionType) INSTANCES.get( xmlTagName );
		}
	}
