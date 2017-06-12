File path: code/core/src/main/java/org/hibernate/engine/CascadingAction.java
Comment: orphans should not be deleted during copy??
Initial commit id: d8d6d82e
Final commit id: 3402ba3a
   Bugs between [       0]:

   Bugs after [       3]:
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
36ba1bcafb HHH-6192 - Split org.hibernate.collection package up into api/sip/internal
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes

Start block index: 276
End block index: 279
		public boolean deleteOrphans() {
			// orphans should not be deleted during copy??
			return false;
		}
