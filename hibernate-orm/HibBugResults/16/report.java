File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: NONE might be a better option moving forward in the case of callable
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 756
End block index: 764
	private static ExecuteUpdateResultCheckStyle getResultCheckStyle(Element element, boolean callable) throws MappingException {
		Attribute attr = element.attribute( "check" );
		if ( attr == null ) {
			// use COUNT as the default.  This mimics the old behavior, although
			// NONE might be a better option moving forward in the case of callable
			return ExecuteUpdateResultCheckStyle.COUNT;
		}
		return ExecuteUpdateResultCheckStyle.parse( attr.getValue() );
	}
