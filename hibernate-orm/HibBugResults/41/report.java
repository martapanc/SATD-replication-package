File path: code/core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
Comment: VERY IMPORTANT!!!! - This class needs to be free of any static references
Initial commit id: d8d6d82e
Final commit id: 19791a6c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 15
End block index: 23
	// VERY IMPORTANT!!!! - This class needs to be free of any static references
	// to any CGLIB or Javassist classes.  Otherwise, users will always need both
	// on their classpaths no matter which (if either) they use.
	//
	// Another option here would be to remove the Hibernate.isPropertyInitialized()
	// method and have the users go through the SessionFactory to get this information.

	private FieldInterceptionHelper() {
	}
