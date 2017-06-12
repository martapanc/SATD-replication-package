File path: code/core/src/main/java/org/hibernate/cfg/Configuration.java
Comment: ODO: Somehow add the newly created foreign keys to the internal collection
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       7]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
23794bf294 HHH-9792 - Clean up missed Configuration methods
a92ddea9ca HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc) - Gunnar's feedback
027840018b HHH-8191 Support Teradata 14.0
63a0f03c5a HHH-9654 - Adjust envers for 5.0 APIs + JAXB

Start block index: 1118
End block index: 1172
	protected void secondPassCompile() throws MappingException {
		log.debug( "processing extends queue" );

		processExtendsQueue();

		log.debug( "processing collection mappings" );

		Iterator iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			SecondPass sp = (SecondPass) iter.next();
			if ( ! (sp instanceof QuerySecondPass) ) {
				sp.doSecondPass( classes );
				iter.remove();
			}
		}

		log.debug( "processing native query and ResultSetMapping mappings" );
		iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			SecondPass sp = (SecondPass) iter.next();
			sp.doSecondPass( classes );
			iter.remove();
		}

		log.debug( "processing association property references" );

		iter = propertyReferences.iterator();
		while ( iter.hasNext() ) {
			Mappings.PropertyReference upr = (Mappings.PropertyReference) iter.next();

			PersistentClass clazz = getClassMapping( upr.referencedClass );
			if ( clazz == null ) {
				throw new MappingException(
						"property-ref to unmapped class: " +
						upr.referencedClass
					);
			}

			Property prop = clazz.getReferencedProperty( upr.propertyName );
			if ( upr.unique ) {
				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
			}
		}

		//TODO: Somehow add the newly created foreign keys to the internal collection

		log.debug( "processing foreign key constraints" );

		iter = getTableMappings();
		Set done = new HashSet();
		while ( iter.hasNext() ) {
			secondPassCompileForeignKeys( (Table) iter.next(), done );
		}

	}
