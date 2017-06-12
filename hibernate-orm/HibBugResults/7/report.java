File path: code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
Comment: Does this dialect support the FOR UPDATE syntax?
Initial commit id: d8d6d82e
Final commit id: 5fc70fc5
   Bugs between [       0]:

   Bugs after [      10]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
aa3f913857 HHH-11194 - Add setting to allow enabling legacy 4.x LimitHandler behavior (removed delegation).
eec01edcca HHH-10876 - DefaultIdentifierGeneratorFactory does not consider the hibernate.id.new_generator_mappings setting
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
2731fe541a HHH-100084 - Refactor Identity Column support methods into IdentityColumnSupport interface
11ae0f72c8 HHH-9166 handle nested exceptions with TemplatedViolatedConstraintNameExtracter
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
ae43670290 HHH-9724 - More complete "temp table" coverage and allow Dialects to influence which strategy is used
06b6135a11 HHH-9724 - More complete "temp table" coverage and allow Dialects to influence which strategy is used - initial work
4615ae1018 - HHH-9324: Avoids creation of LimitHandler instances for every query.

Start block index: 521
End block index: 524
	public boolean supportsForUpdate() {
		// Does this dialect support the FOR UPDATE syntax?
		return false;
	}
