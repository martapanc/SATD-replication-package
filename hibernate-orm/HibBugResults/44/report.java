File path: commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
Comment: ck!
Initial commit id: 82e5fa8c
Final commit id: 59ec451c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 414
End block index: 426
	private static String generateAliasRoot(String description) {
		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
				.toLowerCase()
		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
				.replace( '$', '_' ); //classname may be an inner class
		result = cleanAlias( result );
		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
			return result + "x"; //ick!
		}
		else {
			return result;
		}
	}
