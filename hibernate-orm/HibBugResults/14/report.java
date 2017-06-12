File path: hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
Comment: implicit joins are always(?) ok to reuse
Initial commit id: a0663f0d
Final commit id: 153c4e32
   Bugs between [       1]:
153c4e32ef HHH-9090 : HQL parser is trying to reuse parent implied join for subquery
   Bugs after [       8]:
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
17b8d2e294 HHH-11538 - Skip generating joins for entity references used in equality and nullness predicates
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
e64d028306 HHH-10122 - Deprecate ".class" property-style entity-type-expression format
6bb8d03595 HHH-9637 : Join is reused when 2 explicit joins are used for the same ToOne association
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
6c404c30f7 HHH-9642 : Join fetch not performing join fetch for @Embeddable with @OneToMany

Start block index: 527
End block index: 540
	private boolean canReuse(FromElement fromElement, String requestedAlias) {
		// implicit joins are always(?) ok to reuse
		if ( isImplicitJoin( fromElement ) ) {
			return true;
		}

		// if the from-clauses are the same, we can be a little more aggressive in terms of what we reuse
		if ( fromElement.getFromClause() == getWalker().getCurrentFromClause() ) {
			return true;
		}

		// otherwise (subquery case) dont reuse the fromElement if we are processing the from-clause of the subquery
		return getWalker().getCurrentClauseType() != SqlTokenTypes.FROM;
	}
