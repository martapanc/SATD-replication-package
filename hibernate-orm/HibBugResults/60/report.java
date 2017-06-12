File path: code/core/src/main/java/org/hibernate/type/AnyType.java
Comment: ODO: is this right??
Initial commit id: d8d6d82e
Final commit id: dc7cdf9d
   Bugs between [       0]:

   Bugs after [      10]:
4d6cda1548 HHH-11173 - Fix text serialization of uninitialized lazy attributes
59ed7fa29b HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (continued fixing of hibernate-core test failures)
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
961b5e8977 HHH-10069 - ClassCastException between CompositeCustomType and ComponentType part 2
47b8ed5121 HHH-10073 Removing methods scheduled for removal in 5.0
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
cd590470c0 HHH-8741 - More checkstyle cleanups
9938937fe7 HHH-8637 - Downcasting with TREAT operator should also filter results by the specified Type

Start block index: 171
End block index: 173
	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
		throw new UnsupportedOperationException(); //TODO: is this right??
	}
