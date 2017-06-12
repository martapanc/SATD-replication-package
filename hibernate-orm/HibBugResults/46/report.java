File path: code/core/src/main/java/org/hibernate/property/Dom4jAccessor.java
Comment: s this ok?
Initial commit id: d8d6d82e
Final commit id: 4a4f636c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 244
End block index: 255
		public void set(Object target, Object value, SessionFactoryImplementor factory) 
		throws HibernateException {
			Element owner = ( Element ) target;
			if ( !super.propertyType.isXMLElement() ) { //kinda ugly, but needed for collections with a "." node mapping
				if (value==null) {
					owner.setText(null); //is this ok?
				}
				else {
					super.propertyType.setToXMLNode(owner, value, factory);
				}
			}
		}
