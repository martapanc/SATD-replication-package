20/report.java
Satd-method: 
********************************************
********************************************
20/Between/ HHH-5126  d1515a29_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/hibernate-core/src/main/antlr/hql.g
+++ b/hibernate-core/src/main/antlr/hql.g
-	|   COLON^ identifier
+	|   parameter
-	|   PARAM^ (NUM_INT)?
+	;
+
+parameter
+	: COLON^ identifier
+	| PARAM^ (NUM_INT)?
+	| parameter
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
-		if ( vals.size() == 1 ) {
-			// short-circuit for performance...
+
+		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
+		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
+		String placeholder =
+				new StringBuffer( paramPrefix.length() + name.length() )
+						.append( paramPrefix ).append(  name )
+						.toString();
+
+		if ( query == null ) {
+			return query;
+		}
+		int loc = query.indexOf( placeholder );
+
+		if ( loc < 0 ) {
+			return query;
+		}
+
+		String beforePlaceholder = query.substring( 0, loc );
+		String afterPlaceholder =  query.substring( loc + placeholder.length() );
+
+		// check if placeholder is already immediately enclosed in parentheses
+		// (ignoring whitespace)
+		boolean isEnclosedInParens =
+				StringHelper.getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' &&
+				StringHelper.getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')';
+
+		if ( vals.size() == 1  && isEnclosedInParens ) {
+			// short-circuit for performance when only 1 value and the
+			// placeholder is already enclosed in parentheses...
-		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
-		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
-		return StringHelper.replace( query, paramPrefix + name, list.toString(), true );
+		return StringHelper.replace(
+				beforePlaceholder,
+				afterPlaceholder,
+				placeholder.toString(),
+				list.toString(),
+				true,
+				true
+		);
--- a/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
+		return replace( template, placeholder, replacement, wholeWords, false );
+	}
+
+	public static String replace(String template,
+								 String placeholder,
+								 String replacement,
+								 boolean wholeWords,
+								 boolean encloseInParensIfNecessary) {
-			final boolean actuallyReplace = !wholeWords ||
-					loc + placeholder.length() == template.length() ||
-					!Character.isJavaIdentifierPart( template.charAt( loc + placeholder.length() ) );
-			String actualReplacement = actuallyReplace ? replacement : placeholder;
-			return new StringBuffer( template.substring( 0, loc ) )
-					.append( actualReplacement )
-					.append( replace( template.substring( loc + placeholder.length() ),
-							placeholder,
-							replacement,
-							wholeWords ) ).toString();
+			String beforePlaceholder = template.substring( 0, loc );
+			String afterPlaceholder = template.substring( loc + placeholder.length() );
+			return replace( beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary );
+		}
+	}
+
+
+	public static String replace(String beforePlaceholder,
+								 String afterPlaceholder,
+								 String placeholder,
+								 String replacement,
+								 boolean wholeWords,
+								 boolean encloseInParensIfNecessary) {
+		final boolean actuallyReplace =
+				! wholeWords ||
+				afterPlaceholder.length() == 0 ||
+				! Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
+		boolean encloseInParens =
+				actuallyReplace &&
+				encloseInParensIfNecessary &&
+				! ( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
+				! ( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );		
+		StringBuilder buf = new StringBuilder( beforePlaceholder );
+		if ( encloseInParens ) {
+			buf.append( '(' );
+		}
+		buf.append( actuallyReplace ? replacement : placeholder );
+		if ( encloseInParens ) {
+			buf.append( ')' );
+		}
+		buf.append(
+				replace(
+						afterPlaceholder,
+						placeholder,
+						replacement,
+						wholeWords,
+						encloseInParensIfNecessary
+				)
+		);
+		return buf.toString();
+	}
+
+	public static char getLastNonWhitespaceCharacter(String str) {
+		if ( str != null && str.length() > 0 ) {
+			for ( int i = str.length() - 1 ; i >= 0 ; i-- ) {
+				char ch = str.charAt( i );
+				if ( ! Character.isWhitespace( ch ) ) {
+					return ch;
+				}
+			}
+		return '\0';
+	public static char getFirstNonWhitespaceCharacter(String str) {
+		if ( str != null && str.length() > 0 ) {
+			for ( int i = 0 ; i < str.length() ; i++ ) {
+				char ch = str.charAt( i );
+				if ( ! Character.isWhitespace( ch ) ) {
+					return ch;
+				}
+			}
+		}
+		return '\0';
+	}
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/cid/CompositeIdTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/cid/CompositeIdTest.java
-        Query query=s.createQuery( "from SomeEntity e where e.id in (:idList)" );
+        Query query=s.createQuery( "from SomeEntity e where e.id in :idList" );
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+
+		query = s.createQuery( "from LineItem l where l.id in :idList" );
+		query.setParameterList( "idList", list );
+		assertEquals( 2, query.list().size() );
+
+
+		s.createQuery( "from Human where name.last in ?1" )
+				.setParameterList( "1", params )
+				.list();
+
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
+		assertTranslation( "from LineItem l where l.id in :idList" );
+		translator = createNewQueryTranslator("from LineItem l where l.id in ?");
+		assertInExist("'in' should be translated to 'and'", false, translator);
+		translator = createNewQueryTranslator("from Animal a where a.id in ?");
+		assertInExist("only translate tuple with 'in' syntax", true, translator);
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
+		q = s.createQuery("from Simple s where s.name in :several");
+		q.setProperties(single);
+		assertTrue( q.list().get(0)==simple );
+
+		q = s.createQuery("from Simple s where s.name in :stuff");
+		q.setProperties(single);
+		assertTrue( q.list().get(0)==simple );
+
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLLoaderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLLoaderTest.java
-			
-			
+
+			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in :names", "category", Category.class);
+			query.setParameterList("names", str);
+			query.uniqueResult();
+
+			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in (:names)", "category", Category.class);
+			str = new String[] { "WannaBeFound" };
+			query.setParameterList("names", str);
+			query.uniqueResult();
+
+			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in :names", "category", Category.class);
+			query.setParameterList("names", str);			
+			query.uniqueResult();
+
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
+import java.util.HashSet;
+import java.util.Set;
+
-
+		Query q = em.createQuery( "select item from Item item where item.name in :names" );
+		//test hint in value and string
+		q.setHint( "org.hibernate.fetchSize", 10 );
+		q.setHint( "org.hibernate.fetchSize", "10" );
+		List params = new ArrayList();
+		params.add( item.getName() );
+		q.setParameter( "names", params );
+		List result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 1, result.size() );
+
+		q = em.createQuery( "select item from Item item where item.name in :names" );
+		//test hint in value and string
+		q.setHint( "org.hibernate.fetchSize", 10 );
+		q.setHint( "org.hibernate.fetchSize", "10" );
+		params.add( item2.getName() );
+		q.setParameter( "names", params );
+		result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 2, result.size() );
+
+		q = em.createQuery( "select item from Item item where item.name in ?1" );
+		params = new ArrayList();
+		params.add( item.getName() );
+		params.add( item2.getName() );
+		q.setParameter( "1", params );
+		result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 2, result.size() );
+		em.remove( result.get( 0 ) );
+		em.remove( result.get( 1 ) );
+		em.getTransaction().commit();
+
+		em.close();
+	}
+
+	public void testParameterListInExistingParens() throws Exception {
+		final Item item = new Item( "Mouse", "Micro$oft mouse" );
+		final Item item2 = new Item( "Computer", "Dell computer" );
+
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		em.persist( item );
+		em.persist( item2 );
+		assertTrue( em.contains( item ) );
+		em.getTransaction().commit();
+
+		em.getTransaction().begin();
+		q = em.createQuery( "select item from Item item where item.name in ( \n :names \n)\n" );
+		//test hint in value and string
+		q.setHint( "org.hibernate.fetchSize", 10 );
+		q.setHint( "org.hibernate.fetchSize", "10" );
+		params = new ArrayList();
+		params.add( item.getName() );
+		params.add( item2.getName() );
+		q.setParameter( "names", params );
+		result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 2, result.size() );
+
-		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.brand in (?1)" );
+		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.brand in ?1" );

Lines added containing method: 225. Lines removed containing method: 31. Tot = 256
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
