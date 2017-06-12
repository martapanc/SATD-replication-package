File path: code/core/src/main/java/org/hibernate/type/CollectionType.java
Comment: TODO: it would be better if this was done at the higher level by Printer
Initial commit id: d8d6d82e
Final commit id: 4a4f636c
   Bugs between [       0]:

   Bugs after [      22]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
122f00f30c HHH-11173 : Change CollectionType#renderLoggableString to return '<uninitialized>' when collection is uninitialized
4d6cda1548 HHH-11173 - Fix text serialization of uninitialized lazy attributes
0a2a5c622e HHH-11097 - Performance problem if cached entity has attribute state with an expensive toString() method (LOB, etc)
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
0385b1436a HHH-10602 - Retrieve cached value with enable_lazy_load_no_trans throws an exception
efa72a8333 HHH-5855 : Merge causes a duplicated "insert" of a child entity in lazy collection
47b8ed5121 HHH-10073 Removing methods scheduled for removal in 5.0
93f56ecf5d HHH-9777 : Dereferenced collections are not processed properly
e07eef3db1 HHH-9777 : Dereferenced collections are not processed properly
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
7308e14fed HHH-9803 - Checkstyle fix ups
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8fe5460ec0 HHH-8741 - More checkstyle cleanups
cd590470c0 HHH-8741 - More checkstyle cleanups
9938937fe7 HHH-8637 - Downcasting with TREAT operator should also filter results by the specified Type
cb1b9a05af HHH-7928 - Regression caused by HHH-6361
32e8765615 HHH-7771 : Deprecate obsolete Type, AssociationType, and TypeFactory methods
f9049a1fd2 HHH-6361 formatting
7bb43baf0b HHH-6361: Patch ensuring that collection events have the correct stored snapshot after merging a detached entity into the persistencecontext
30ea167c41 HHH-7359 Added a new method to MessageHelper to intelligently handle property-ref issues in logging.  Rolled it out to as many MessageHelper users as possible.

Start block index: 150
End block index: 166
	protected String renderLoggableString(Object value, SessionFactoryImplementor factory)
			throws HibernateException {
		if ( Element.class.isInstance( value ) ) {
			// for DOM4J "collections" only
			// TODO: it would be better if this was done at the higher level by Printer
			return ( ( Element ) value ).asXML();
		}
		else {
			List list = new ArrayList();
			Type elemType = getElementType( factory );
			Iterator iter = getElementsIterator( value );
			while ( iter.hasNext() ) {
				list.add( elemType.toLoggableString( iter.next(), factory ) );
			}
			return list.toString();
		}
	}
