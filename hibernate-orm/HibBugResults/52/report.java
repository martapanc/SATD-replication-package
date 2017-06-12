File path: code/core/src/main/java/org/hibernate/mapping/Column.java
Comment: ODO: deprecated
Initial commit id: d8d6d82e
Final commit id: d7cc102b
   Bugs between [       0]:

   Bugs after [      12]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
7308e14fed HHH-9803 - Checkstyle fix ups
1361925bc7 HHH-9722
8ec17e68e7 HHH-8741 - More checkstyle cleanups
a2287b6c6d HHH-8371 Consider explicit column name's "_" in alias creation
79073a98f0 HHH-8073 Corrected column alias creation
2725a7d49e HHH-4084 @UniqueConstraint(columnNames="") causes StringIndexOutOfBoundsException
3e69b7bd53 HHH-7725 - Make handling multi-table bulk HQL operations more pluggable
bcae560079 HHH-2304 Wrong type detection for sql type char(x) columns
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
33074dc2dc HHH-6069 - Tests moved

Start block index: 59
End block index: 70
	public void setName(String name) {
		if (
			name.charAt(0)=='`' ||
			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
		) {
			quoted=true;
			this.name=name.substring( 1, name.length()-1 );
		}
		else {
			this.name = name;
		}
	}
