File path: code/core/src/main/java/org/hibernate/mapping/UniqueKey.java
Comment: ODO: improve this hack!
Initial commit id: d8d6d82e
Final commit id: f4c36a10
   Bugs between [       0]:

   Bugs after [      11]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
8ec17e68e7 HHH-8741 - More checkstyle cleanups
9030fa015e HHH-8217 Make generated constraint names short and non-random
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
1d9b7a06a5 HHH-7969 initial @Table#indexes support
41397f22d1 HHH-7797 Use unique indexes on nullable columns for DB2.  Correctly handle @UniqueConstraint table annotations on second passes.
7b05f4aed8 HHH-7797 Initial attempt at using UniqueDelegate within metamodel
4204f2c5fe HHH-7797 Finished first take on UniqueDelegate and rolled into .cfg and .mapping
3a995f574d HHH-7797 Block "unique" syntax on a column if a constraint can/will be used

Start block index: 32
End block index: 49
	public String sqlConstraintString(Dialect dialect, String constraintName, String defaultCatalog,
									  String defaultSchema) {
		StringBuffer buf = new StringBuffer(
				dialect.getAddPrimaryKeyConstraintString( constraintName )
		).append( '(' );
		Iterator iter = getColumnIterator();
		boolean nullable = false;
		while ( iter.hasNext() ) {
			Column column = (Column) iter.next();
			if ( !nullable && column.isNullable() ) nullable = true;
			buf.append( column.getQuotedName( dialect ) );
			if ( iter.hasNext() ) buf.append( ", " );
		}
		return !nullable || dialect.supportsNotNullUnique() ?
				StringHelper.replace( buf.append( ')' ).toString(), "primary key", "unique" ) :
				//TODO: improve this hack!
				null;
	}
