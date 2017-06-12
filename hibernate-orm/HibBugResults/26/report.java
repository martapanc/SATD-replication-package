File path: code/core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
Comment: todo : currently expects that the individual with expressions apply to the same sql table join
Initial commit id: d8d6d82e
Final commit id: a9b1425f
   Bugs between [       0]:

   Bugs after [       6]:
360317eedf HHH-6200 - Split org.hibernate.hql package into api/spi/internal
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes
0816d00e59 HHH-5986 - Refactor org.hibernate.util package for spi/internal split
2641842ba9 HHH-5843 - Avoid useless branches during HQL parsing when trace logging is disabled
adbe3920f1 HHH-5843 - Avoid useless branches during HQL parsing when trace logging is disabled

Start block index: 357
End block index: 389
		public void visit(AST node) {
			// todo : currently expects that the individual with expressions apply to the same sql table join.
			//      This may not be the case for joined-subclass where the property values
			//      might be coming from different tables in the joined hierarchy.  At some
			//      point we should expand this to support that capability.  However, that has
			//      some difficulties:
			//          1) the biggest is how to handle ORs when the individual comparisons are
			//              linked to different sql joins.
			//          2) here we would need to track each comparison individually, along with
			//              the join alias to which it applies and then pass that information
			//              back to the FromElement so it can pass it along to the JoinSequence

			if ( node instanceof DotNode ) {
				DotNode dotNode = ( DotNode ) node;
				FromElement fromElement = dotNode.getFromElement();
				if ( referencedFromElement != null ) {
					if ( fromElement != referencedFromElement ) {
						throw new HibernateException( "with-clause referenced two different from-clause elements" );
					}
				}
				else {
					referencedFromElement = fromElement;
					joinAlias = extractAppliedAlias( dotNode );
					// todo : temporary
					//      needed because currently persister is the one that
					//      creates and renders the join fragments for inheritence
					//      hierarchies...
					if ( !joinAlias.equals( referencedFromElement.getTableAlias() ) ) {
						throw new InvalidWithClauseException( "with clause can only reference columns in the driving table" );
					}
				}
			}
		}
