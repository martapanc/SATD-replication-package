15/report.java
Satd-method: public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
********************************************
********************************************
15/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
get(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* read
* getBinaryStream
* toByteArray
* write
* useStreamsForBinary
* getBytes
********************************************
********************************************
15/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
get(
-				buf.append( args.get( i ) );
+				buf.append( args.get( i ) );
-		String type = ( String ) args.get( 1 );
-			return "{fn convert(" + args.get( 0 ) + " , " + type + ")}";
-			return "convert(" + args.get( 0 ) + " , " + type + "," + args.get( 2 ) + ")";
+		String type = ( String ) args.get( 1 );
+			return "{fn convert(" + args.get( 0 ) + " , " + type + ")}";
+			return "convert(" + args.get( 0 ) + " , " + type + "," + args.get( 2 ) + ")";
-		return ( Filter ) enabledFilters.get( filterName );
+		return enabledFilters.get( filterName );
-		return getters[i].get( component );
+		return getters[i].get( component );
-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
+	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {

Lines added containing method: 7. Lines removed containing method: 7. Tot = 14
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* read
* getBinaryStream
* toByteArray
* write
* useStreamsForBinary
* getBytes
—————————
Method found in diff:	protected final void read() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected final void write() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
15/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
get(
-			Type type = (Type) types.get( me.getKey() );
+			Type type = types.get( paramEntry.getKey() );
-			( (DelayedOperation) operationQueue.get(i) ).operate();
-					return ( (DelayedOperation) operationQueue.get(i++) ).getAddedInstance();
+					return operationQueue.get(i++).getAddedInstance();
-				DelayedOperation op = (DelayedOperation) operationQueue.get(i);
-		 * @see java.util.List#get(int)
-			if ( bag.size()>i && elementType.isSame( old, bag.get(i++), entityMode ) ) {
+			if ( bag.size()>i && elementType.isSame( old, bag.get(i++) ) ) {
-		if ( sn.size()>i && elemType.isSame( sn.get(i), entry, entityMode ) ) {
+		if ( sn.size()>i && elemType.isSame( sn.get(i), entry ) ) {
-        else context = (CollectionLoadContext)collectionLoadContexts.get(resultSet);
+			context = collectionLoadContexts.get(resultSet);
-		LoadingCollectionEntry rtn = ( LoadingCollectionEntry ) xrefLoadingCollectionEntries.get( key );
+		LoadingCollectionEntry rtn = xrefLoadingCollectionEntries.get( key );
-			context = ( EntityLoadContext ) entityLoadContexts.get( resultSet );
+			context = entityLoadContexts.get( resultSet );
-	    return (Type) parameterTypes.get(parameterName);
+	    return parameterTypes.get(parameterName);
-		return ( SessionNonFlushedChanges ) nonFlushedChangesByEntityMode.get( entityMode );
-		LinkedHashSet resolversForMode = ( LinkedHashSet ) entityNameResolvers.get( entityMode );
-		Set actualEntityNameResolvers = ( Set ) entityNameResolvers.get( entityMode );
-			rtn = (SessionImpl) childSessionsByEntityMode.get( entityMode );
-	public Object get(Object key) {
-		return map.get(k);
+	public V get(Object key) {
+		return map.get( new IdentityKey(key) );
-			return get( owner );
-		public Object get(Object owner) throws HibernateException {
-		public Object get(Object owner) throws HibernateException {
-		public Object get(Object owner) throws HibernateException {
-		public Object get(Object owner) throws HibernateException {
-		return tuplizers.get( entityMode );
-		Integer index = ( Integer ) propertyIndexes.get( propertyName );
+		Integer index = ( Integer ) propertyIndexes.get( propertyName );
-		return ( String ) inheritenceNodeNameMap.get( extractNodeName( ( Element ) entityInstance ) );
-		return ( String ) nodeNameToEntityNameMap.get( extractNodeName( ( Element ) entity ) );
-	public Object get(ResultSet rs, String name) throws SQLException {
+	public Object get(ResultSet rs, String name) throws SQLException {
-				Element value = (Element) elements.get(i);
-		String name = ( String ) getter.get( DOM );
-		String name = ( String ) getter.get( DOM );
-		Long id = ( Long ) getter.get( DOM );
-		Long id = ( Long ) getter.get( DOM );
-		cust = (Element) s.get( "Customer", "xyz123" );
-		acct = (Element) s.get( "Account", "abc123" );
-		cust = (Element) s.get( "Customer", "xyz123" );
-		acct = (Element) s.get( "Account", "abc123" );
-		acct = (Element)m.get("acc"); 
-		cust = ( Element ) s.get( "Customer", "xyz123" );
-		Element stuffElement = ( Element ) cust.element( "stuff" ).elements(  "foo" ).get( 0 );
-//		employer = (Element) s.get( "Employer", eid );
-			Element element = (Element) list.get(i);
-		Stock stock = ( Stock ) session.get( Stock.class, new Long( 1 ) );
-		Object rtn = dom4j.get( Stock.class.getName(), testData.stockId );
-		Element element = ( Element ) result.get( 0 );

Lines added containing method: 12. Lines removed containing method: 44. Tot = 56
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* read
* getBinaryStream
* toByteArray
* write
* useStreamsForBinary
* getBytes
—————————
Method found in diff:	protected final void read() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected final void write() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
15/Between/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
get(
-					String negatedToken = negated ? ( String ) NEGATIONS.get( token.toLowerCase(Locale.ROOT) ) : null;
+					String negatedToken = negated ? NEGATIONS.get( token.toLowerCase(Locale.ROOT) ) : null;
-		for (int i=0; i<size; i++) result[i+1] = (Type) typeList.get(i);
+			result[i+1] = (Type) typeList.get(i);
-		for (int i=0; i<size; i++) result[i+1] = valueList.get(i);
+			result[i+1] = valueList.get(i);
-				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre );
-			OuterJoinableAssociation oj = (OuterJoinableAssociation) associations.get(i);
-			Join join = (Join) joins.get(i);
-			if (result==null) result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
+				result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
-			for (long old = executionMinTime.get(); (time < old) && !executionMinTime.compareAndSet(old, time); old = executionMinTime.get());
-			for (long old = executionMaxTime.get(); (time > old) && !executionMaxTime.compareAndSet(old, time); old = executionMaxTime.get());
+			for (long old = executionMinTime.get(); (time < old) && !executionMinTime.compareAndSet(old, time); old = executionMinTime.get()) {}
+			for (long old = executionMaxTime.get(); (time > old) && !executionMaxTime.compareAndSet(old, time); old = executionMaxTime.get()) {}
-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
-	public Object get(ResultSet rs, String name) throws SQLException {
-	protected abstract Object get(ResultSet rs, String name) throws SQLException;
-		return get( rs, names[0] );
-		return get( rs, name );
-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
-			if ( Array.get(array, i)==element ) return i;
+			if ( Array.get(array, i)==element ) {
-	protected Object get(ResultSet rs, String name) throws SQLException {
-			if ( list.get(i)==element ) return i;
+			if ( list.get(i)==element ) {
-	public abstract Object get(ResultSet rs, String name) throws HibernateException, SQLException;
-			Object value = get(rs, name);
-	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException;
+	Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException;
-				.addProjection( AuditEntity.id().count( "id" ) ).getResultList().get( 0 ) == 2;
+				.addProjection( AuditEntity.id().count() ).getResultList().get( 0 ) == 2;

Lines added containing method: 10. Lines removed containing method: 22. Tot = 32
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* read
* getBinaryStream
* toByteArray
* write
* useStreamsForBinary
* getBytes
—————————
Method found in diff:	public int read() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void write(Writer writer) throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
