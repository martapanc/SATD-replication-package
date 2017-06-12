69/report.java
Satd-method: public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {
********************************************
********************************************
69/Between/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getPropertyAccessor(
-	public static PropertyAccessor getPropertyAccessor(AttributeBinding property, EntityMode mode) throws MappingException {
-		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
-		//	return mappedProperty.getPropertyAccessor( null );
-		return getPropertyAccessor( mappedProperty ).getGetter(
-		return getPropertyAccessor( mappedProperty ).getSetter(
-	private PropertyAccessor getPropertyAccessor(AttributeBinding mappedProperty) throws MappingException {
-		return PropertyAccessorFactory.getPropertyAccessor(

Lines added containing method: 0. Lines removed containing method: 7. Tot = 7
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessorName
—————————
Method found in diff:	-	public String getPropertyAccessorName() {
-	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
69/Between/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	    if ( null == mode || EntityMode.POJO.equals( mode ) ) {
-		    return getPojoPropertyAccessor( property.getPropertyAccessorName() );
-	    }
-	    else if ( EntityMode.MAP.equals( mode ) ) {
-		    return getDynamicMapPropertyAccessor();
-	    }
-	    else {
-		    throw new MappingException( "Unknown entity mode [" + mode + "]" );
-	    }
+		if ( null == mode || EntityMode.POJO.equals( mode ) ) {
+			return getPojoPropertyAccessor( property.getPropertyAccessorName() );
+		}
+		else if ( EntityMode.MAP.equals( mode ) ) {
+			return getDynamicMapPropertyAccessor();
+		}
+		else {
+			throw new MappingException( "Unknown entity mode [" + mode + "]" );
+		}

Lines added: 9. Lines removed: 9. Tot = 18
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getPropertyAccessor(
-		return getPropertyAccessor(type);
+		return getPropertyAccessor( type );
-			return mappedProperty.getPropertyAccessor(null);
+			return mappedProperty.getPropertyAccessor( null );

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessorName
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
69/Between/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getPropertyAccessor(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessorName
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
69/Between/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getPropertyAccessor(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessorName
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
69/Between/ HHH-9837  9e063ffa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
getPropertyAccessor(
-	public PropertyAccessor getPropertyAccessor(Class clazz) {
-		return property.getPropertyAccessor( attributeDeclarer )
-	public PropertyAccessor getPropertyAccessor(Class clazz) {
-		return getPropertyAccessor(clazz).getGetter( clazz, name );
-		return getPropertyAccessor(clazz).getSetter(clazz, name);
-	public PropertyAccessor getPropertyAccessor(Class clazz) throws MappingException {
-		return PropertyAccessorFactory.getPropertyAccessor( clazz, getPropertyAccessorName() );
-	//      1) PropertyAccessorFactory.getPropertyAccessor() takes references to both a
-	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {
-	public static PropertyAccessor getPropertyAccessor(Class optionalClass, String type) throws MappingException {
-		return getPropertyAccessor( type );
-	public static PropertyAccessor getPropertyAccessor(String type) throws MappingException {
-						PropertyAccessorFactory.getPropertyAccessor( resultClass, null ),
-						PropertyAccessorFactory.getPropertyAccessor( "field" )
-		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
-			PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( null );
-			return mappedProperty.getPropertyAccessor( null );
-		return PropertyAccessorFactory.getPropertyAccessor( accessorType );

Lines added containing method: 0. Lines removed containing method: 18. Tot = 18
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessorName
—————————
Method found in diff:	public String getPropertyAccessorName();

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
