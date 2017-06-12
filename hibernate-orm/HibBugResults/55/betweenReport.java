55/report.java
Satd-method: public final class PropertyAccessorFactory {
********************************************
********************************************
55/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
********************************************
********************************************
55/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			accessorClass = ReflectHelper.classForName(accessorName);
+			accessorClass = ReflectHelper.classForName( accessorName );

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public AccessType getPropertyAccessor(XAnnotatedElement element) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Getter getGetter(Class theClass, String name) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAccessorPropertyName( EntityMode mode ) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Class classForName(String name, Class caller) throws ClassNotFoundException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public AccessType getPropertyAccessor(XAnnotatedElement element) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAccessorPropertyName( EntityMode mode ) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType(Criteria criteria, String propertyPath)

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	    else if ( EntityMode.DOM4J.equals( mode ) ) {
-	    	//TODO: passing null here, because this method is not really used for DOM4J at the moment
-	    	//      but it is still a bug, if we don't get rid of this!
-		    return getDom4jPropertyAccessor( property.getAccessorPropertyName( mode ), property.getType(), null );
-	    }
-	public static PropertyAccessor getDom4jPropertyAccessor(String nodeName, Type type, SessionFactoryImplementor factory)
-	throws MappingException {
-		//TODO: need some caching scheme? really comes down to decision
-		//      regarding amount of state (if any) kept on PropertyAccessors
-		return new Dom4jAccessor( nodeName, type, factory );
-	}
-

Lines added: 0. Lines removed: 12. Tot = 12
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public PropertyAccessor getPropertyAccessor(Class clazz) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAccessorPropertyName( EntityMode mode ) {
-		if ( mode == EntityMode.DOM4J ) {
-			return nodeName;
-		}
-		else {
-			return getName();
-		}
+		return getName();

Lines added: 1. Lines removed: 6. Tot = 7
—————————
Method found in diff:	public Type getType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-6360  1d26ac1e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	}	/**
+	}
+
+	/**
+     * Retrieves a PropertyAccessor instance based on the given property definition and
+     * entity mode.
+     *
+     * @param property The property for which to retrieve an accessor.
+     * @param mode The mode for the resulting entity.
+     * @return An appropriate accessor.
+     * @throws MappingException
+     */
+	public static PropertyAccessor getPropertyAccessor(AttributeBinding property, EntityMode mode) throws MappingException {
+		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
+	    if ( null == mode || EntityMode.POJO.equals( mode ) ) {
+		    return getPojoPropertyAccessor( property.getPropertyAccessorName() );
+	    }
+	    else if ( EntityMode.MAP.equals( mode ) ) {
+		    return getDynamicMapPropertyAccessor();
+	    }
+	    else {
+		    throw new MappingException( "Unknown entity mode [" + mode + "]" );
+	    }
+	}
+
+	/**

Lines added: 25. Lines removed: 1. Tot = 26
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {
-	}	/**
+	}

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	private static Getter getGetter(Property mappingProperty) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	/**
-     * Retrieves a PropertyAccessor instance based on the given property definition and
-     * entity mode.
-     *
-     * @param property The property for which to retrieve an accessor.
-     * @param mode The mode for the resulting entity.
-     * @return An appropriate accessor.
-     * @throws MappingException
-     */
-	public static PropertyAccessor getPropertyAccessor(AttributeBinding property, EntityMode mode) throws MappingException {
-		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
-	    if ( null == mode || EntityMode.POJO.equals( mode ) ) {
-		    return getPojoPropertyAccessor( property.getPropertyAccessorName() );
-	    }
-	    else if ( EntityMode.MAP.equals( mode ) ) {
-		    return getDynamicMapPropertyAccessor();
-	    }
-	    else {
-		    throw new MappingException( "Unknown entity mode [" + mode + "]" );
-	    }
-	}

Lines added: 0. Lines removed: 21. Tot = 21
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private static Getter getGetter(Property mappingProperty) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	private static Type getType(String className, ServiceRegistry serviceRegistry) {
-	private static Type getType(String className, ServiceRegistry serviceRegistry) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public String getPropertyAccessorName() {
-	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
-	public static Class classForName(String className, ServiceRegistry serviceRegistry) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {
-	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
55/Between/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-    /**
-     * Retrieves a PropertyAccessor instance based on the given property definition and
-     * entity mode.
-     *
-     * @param property The property for which to retrieve an accessor.
-     * @param mode The mode for the resulting entity.
-     * @return An appropriate accessor.
-     * @throws MappingException
-     */
+	/**
+	 * Retrieves a PropertyAccessor instance based on the given property definition and
+	 * entity mode.
+	 *
+	 * @param property The property for which to retrieve an accessor.
+	 * @param mode The mode for the resulting entity.
+	 *
+	 * @return An appropriate accessor.
+	 *
+	 * @throws MappingException
+	 */
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
+	 *
-		else if ( "noop".equals(pojoAccessorStrategy) ) {
+		else if ( "noop".equals( pojoAccessorStrategy ) ) {
-			throw new MappingException("could not find PropertyAccessor class: " + accessorName, cnfe);
+			throw new MappingException( "could not find PropertyAccessor class: " + accessorName, cnfe );
-			throw new MappingException("could not instantiate PropertyAccessor class: " + accessorName, e);
+			throw new MappingException( "could not instantiate PropertyAccessor class: " + accessorName, e );
-	private PropertyAccessorFactory() {}
+	private PropertyAccessorFactory() {
+	}
-		if ( type==null ) {
-			type = optionalClass==null || optionalClass==Map.class ? "map" : "property";
+		if ( type == null ) {
+			type = optionalClass == null || optionalClass == Map.class ? "map" : "property";
-		return getPropertyAccessor(type);
+		return getPropertyAccessor( type );
-		if ( type==null || "property".equals(type) ) {
+		if ( type == null || "property".equals( type ) ) {
-		if ( "field".equals(type) ) {
+		if ( "field".equals( type ) ) {
-		if ( "map".equals(type) ) {
+		if ( "map".equals( type ) ) {
-		if ( "embedded".equals(type) ) {
+		if ( "embedded".equals( type ) ) {
-		if ( "noop".equals(type)) {
+		if ( "noop".equals( type ) ) {
-		return resolveCustomAccessor(type);
+		return resolveCustomAccessor( type );

Lines added: 35. Lines removed: 31. Tot = 66
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public PropertyAccessor getPropertyAccessor(Class clazz) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAccessorPropertyName( EntityMode mode ) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType() throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public static Class classForName(String className, Map<String,Class<?>> properties, ClassLoaderService classLoaderService) {
-    public static Class classForName(String className, Map<String,Class<?>> properties, ClassLoaderService classLoaderService) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-
-		if ( type==null ) type = optionalClass==null || optionalClass==Map.class ? "map" : "property";
+		if ( type==null ) {
+			type = optionalClass==null || optionalClass==Map.class ? "map" : "property";
+		}
-		if ( type==null || "property".equals(type) ) return BASIC_PROPERTY_ACCESSOR;
-		if ( "field".equals(type) ) return DIRECT_PROPERTY_ACCESSOR;
-		if ( "map".equals(type) ) return MAP_ACCESSOR;
-		if ( "embedded".equals(type) ) return EMBEDDED_PROPERTY_ACCESSOR;
-		if ( "noop".equals(type)) return NOOP_ACCESSOR;
+		if ( type==null || "property".equals(type) ) {
+			return BASIC_PROPERTY_ACCESSOR;
+		}
+		if ( "field".equals(type) ) {
+			return DIRECT_PROPERTY_ACCESSOR;
+		}
+		if ( "map".equals(type) ) {
+			return MAP_ACCESSOR;
+		}
+		if ( "embedded".equals(type) ) {
+			return EMBEDDED_PROPERTY_ACCESSOR;
+		}
+		if ( "noop".equals(type)) {
+			return NOOP_ACCESSOR;
+		}

Lines added: 18. Lines removed: 7. Tot = 25
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private String getType(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public PropertyAccessor getPropertyAccessor(Class clazz) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Getter getGetter(Class theClass, String name) throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAccessorPropertyName( EntityMode mode ) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private Object newInstance(Class type) throws Exception {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Class classForName(String name, Class caller) throws ClassNotFoundException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
55/Between/ HHH-9837  9e063ffa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-public final class PropertyAccessorFactory {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
PropertyAccessorFactory 
-public final class PropertyAccessorFactory {

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getPropertyAccessor
* getGetter
* getAccessorPropertyName
* getType
* newInstance
* getPropertyAccessorName
* classForName
* getSetter
—————————
Method found in diff:	public AccessType getPropertyAccessor(XAnnotatedElement element) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public static Getter getGetter(Class theClass, String name) throws MappingException {
-	public static Getter getGetter(Class theClass, String name) throws MappingException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getAccessorPropertyName( EntityMode mode ) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType() throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getPropertyAccessorName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static Class classForName(String name, Class caller) throws ClassNotFoundException {
-			ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
+			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {
-		return getPropertyAccessor(clazz).getSetter(clazz, name);
+		return getPropertyAccessStrategy( clazz ).buildPropertyAccess( clazz, name ).getSetter();

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
