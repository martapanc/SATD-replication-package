File path: commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
Comment: short cut check...
Initial commit id: 82e5fa8c
Final commit id: 59ec451c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 435
End block index: 448
	private static String cleanAlias(String alias) {
		char[] chars = alias.toCharArray();
		// short cut check...
		if ( !Character.isLetter( chars[0] ) ) {
			for ( int i = 1; i < chars.length; i++ ) {
				// as soon as we encounter our first letter, return the substring
				// from that position
				if ( Character.isLetter( chars[i] ) ) {
					return alias.substring( i );
				}
			}
		}
		return alias;
	}
