File path: code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
Comment: Do we need to drop constraints before dropping tables in this dialect?
Initial commit id: d8d6d82e
Final commit id: 87e3f0fd
   Bugs between [       0]:

   Bugs after [       3]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
aa3f913857 HHH-11194 - Add setting to allow enabling legacy 4.x LimitHandler behavior (removed delegation).
eec01edcca HHH-10876 - DefaultIdentifierGeneratorFactory does not consider the hibernate.id.new_generator_mappings setting

Start block index: 425
End block index: 428
	public boolean dropConstraints() {
		// Do we need to drop constraints before dropping tables in this dialect?
		return true;
	}
