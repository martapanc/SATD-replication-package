File path: code/core/src/main/java/org/hibernate/mapping/SimpleValue.java
Comment: hortcut
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       1]:
1e48334398 [HHH-2048] Incomplete MappingException at org.hibernate.mapping.SimpleValue
   Bugs after [      17]:
9aa164ed27 HHH-10818 - Allow AttributeConverter on attributes marked as Lob (REALLY this time)
c893577efc HHH-5393 : MappingException when @MapKeyColumn refers to a column mapped in embeddable map value
1d20ea4f60 HHH-10413 : byte[] as the version attribute broken
78de650efe HHH-10643 - Attribute 'foreignKeyDefinition' of @javax.persistence.ForeignKey ignored by schema exporter
6036f00781 HHH-10429 - Change SimpleValue isIdentityColumn method to return true if the generator class used extends from IdentityGenerator instead of simply being equal to IdentityGenerator
153b8f26cc HHH-10381 - Introduce a ThreadLocal-based pooled-lo optimizer to avoid locking
80e851e7d0 HHH-9475 Cannot mix @MapKey with @Convert - Moved classes to reuse in test case; Added copy of type from referenced MapKey
0cf66b85e0 HHH-10050 - AttributeConverter should supports ParameterizedType if autoApply is true
3ac508882c HHH-9615 - Allow AttributeConverter on attributes marked as Lob
5f5e5f82c4 HHH-9593 Remove superfluous references to enclosing class
855956135e HHH-9948 - SequenceStyleGenerator uses potentially incorrect name for table/sequence in DML statements
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
7308e14fed HHH-9803 - Checkstyle fix ups
38c004431d HHH-9599 - AnnotationException occurs when applying @Nationalized and @Convert annotations to the same field
22730624fc HHH-9042 - Envers fails with @Converter and AttributeConverter

Start block index: 234
End block index: 245
	public boolean isNullable() {
		if ( hasFormula() ) return true;
		boolean nullable = true;
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			if ( !( (Column) iter.next() ).isNullable() ) {
				nullable = false;
				return nullable; //shortcut
			}
		}
		return nullable;
	}
