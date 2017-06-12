20/report.java
Satd-method: 
********************************************
********************************************
20/After/ HHH-5126  0013a90d_diff.java
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
-import org.hibernate.type.TypeFactory;
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
+import java.util.regex.Pattern;
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

Lines added containing method: 226. Lines removed containing method: 32. Tot = 258
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
20/After/ HHH-5914  303691c8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+
-import java.util.Properties;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.type.AnyType;
-import org.hibernate.type.BigDecimalType;
-import org.hibernate.type.BigIntegerType;
-import org.hibernate.type.BinaryType;
-import org.hibernate.type.BlobType;
-import org.hibernate.type.BooleanType;
-import org.hibernate.type.ByteType;
-import org.hibernate.type.CalendarDateType;
-import org.hibernate.type.CalendarType;
-import org.hibernate.type.CharArrayType;
-import org.hibernate.type.CharacterArrayType;
-import org.hibernate.type.CharacterType;
-import org.hibernate.type.ClassType;
-import org.hibernate.type.ClobType;
-import org.hibernate.type.CurrencyType;
-import org.hibernate.type.DateType;
-import org.hibernate.type.DoubleType;
-import org.hibernate.type.FloatType;
-import org.hibernate.type.ImageType;
-import org.hibernate.type.IntegerType;
-import org.hibernate.type.LocaleType;
-import org.hibernate.type.LongType;
-import org.hibernate.type.ManyToOneType;
-import org.hibernate.type.MaterializedBlobType;
-import org.hibernate.type.MaterializedClobType;
-import org.hibernate.type.ObjectType;
-import org.hibernate.type.SerializableType;
-import org.hibernate.type.ShortType;
-import org.hibernate.type.StringType;
-import org.hibernate.type.TextType;
-import org.hibernate.type.TimeType;
-import org.hibernate.type.TimeZoneType;
-import org.hibernate.type.TimestampType;
-import org.hibernate.type.TrueFalseType;
-import org.hibernate.type.Type;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.type.WrapperBinaryType;
-import org.hibernate.type.YesNoType;
-import org.hibernate.usertype.CompositeUserType;
-	/**
-	 * Hibernate <tt>boolean</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BOOLEAN} instead.
-	 */
-	public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
-	/**
-	 * Hibernate <tt>true_false</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TRUE_FALSE} instead.
-	 */
-	public static final TrueFalseType TRUE_FALSE = TrueFalseType.INSTANCE;
-	/**
-	 * Hibernate <tt>yes_no</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#YES_NO} instead.
-	 */
-	public static final YesNoType YES_NO = YesNoType.INSTANCE;
-	/**
-	 * Hibernate <tt>byte</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BYTE} instead.
-	 */
-	public static final ByteType BYTE = ByteType.INSTANCE;
-	/**
-	 * Hibernate <tt>short</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#SHORT} instead.
-	 */
-	public static final ShortType SHORT = ShortType.INSTANCE;
-	/**
-	 * Hibernate <tt>integer</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#INTEGER} instead.
-	 */
-	public static final IntegerType INTEGER = IntegerType.INSTANCE;
-	/**
-	 * Hibernate <tt>long</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#LONG} instead.
-	 */
-	public static final LongType LONG = LongType.INSTANCE;
-	/**
-	 * Hibernate <tt>float</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#FLOAT} instead.
-	 */
-	public static final FloatType FLOAT = FloatType.INSTANCE;
-	/**
-	 * Hibernate <tt>double</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#DOUBLE} instead.
-	 */
-	public static final DoubleType DOUBLE = DoubleType.INSTANCE;
-	/**
-	 * Hibernate <tt>big_integer</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BIG_INTEGER} instead.
-	 */
-	public static final BigIntegerType BIG_INTEGER = BigIntegerType.INSTANCE;
-	/**
-	 * Hibernate <tt>big_decimal</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BIG_DECIMAL} instead.
-	 */
-	public static final BigDecimalType BIG_DECIMAL = BigDecimalType.INSTANCE;
-	/**
-	 * Hibernate <tt>character</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHARACTER} instead.
-	 */
-	public static final CharacterType CHARACTER = CharacterType.INSTANCE;
-	/**
-	 * Hibernate <tt>string</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#STRING} instead.
-	 */
-	public static final StringType STRING = StringType.INSTANCE;
-	/**
-	 * Hibernate <tt>time</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIME} instead.
-	 */
-	public static final TimeType TIME = TimeType.INSTANCE;
-	/**
-	 * Hibernate <tt>date</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#DATE} instead.
-	 */
-	public static final DateType DATE = DateType.INSTANCE;
-	/**
-	 * Hibernate <tt>timestamp</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIMESTAMP} instead.
-	 */
-	public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
-	/**
-	 * Hibernate <tt>binary</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BINARY} instead.
-	 */
-	public static final BinaryType BINARY = BinaryType.INSTANCE;
-	/**
-	 * Hibernate <tt>wrapper-binary</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#WRAPPER_BINARY} instead.
-	 */
-	public static final WrapperBinaryType WRAPPER_BINARY = WrapperBinaryType.INSTANCE;
-	/**
-	 * Hibernate char[] type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHAR_ARRAY} instead.
-	 */
-	public static final CharArrayType CHAR_ARRAY = CharArrayType.INSTANCE;
-	/**
-	 * Hibernate Character[] type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHARACTER_ARRAY} instead.
-	 */
-	public static final CharacterArrayType CHARACTER_ARRAY = CharacterArrayType.INSTANCE;
-	/**
-	 * Hibernate <tt>image</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#IMAGE} instead.
-	 */
-	public static final ImageType IMAGE = ImageType.INSTANCE;
-	/**
-	 * Hibernate <tt>text</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TEXT} instead.
-	 */
-	public static final TextType TEXT = TextType.INSTANCE;
-	/**
-	 * Hibernate <tt>materialized_blob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#MATERIALIZED_BLOB} instead.
-	 */
-	public static final MaterializedBlobType MATERIALIZED_BLOB = MaterializedBlobType.INSTANCE;
-	/**
-	 * Hibernate <tt>materialized_clob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#MATERIALIZED_CLOB} instead.
-	 */
-	public static final MaterializedClobType MATERIALIZED_CLOB = MaterializedClobType.INSTANCE;
-	/**
-	 * Hibernate <tt>blob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BLOB} instead.
-	 */
-	public static final BlobType BLOB = BlobType.INSTANCE;
-	/**
-	 * Hibernate <tt>clob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CLOB} instead.
-	 */
-	public static final ClobType CLOB = ClobType.INSTANCE;
-	/**
-	 * Hibernate <tt>calendar</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CALENDAR} instead.
-	 */
-	public static final CalendarType CALENDAR = CalendarType.INSTANCE;
-	/**
-	 * Hibernate <tt>calendar_date</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CALENDAR_DATE} instead.
-	 */
-	public static final CalendarDateType CALENDAR_DATE = CalendarDateType.INSTANCE;
-	/**
-	 * Hibernate <tt>locale</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#LOCALE} instead.
-	 */
-	public static final LocaleType LOCALE = LocaleType.INSTANCE;
-	/**
-	 * Hibernate <tt>currency</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CURRENCY} instead.
-	 */
-	public static final CurrencyType CURRENCY = CurrencyType.INSTANCE;
-	/**
-	 * Hibernate <tt>timezone</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIMEZONE} instead.
-	 */
-	public static final TimeZoneType TIMEZONE = TimeZoneType.INSTANCE;
-	/**
-	 * Hibernate <tt>class</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CLASS} instead.
-	 */
-	public static final ClassType CLASS = ClassType.INSTANCE;
-	/**
-	 * Hibernate <tt>serializable</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#SERIALIZABLE} instead.
-	 */
-	public static final SerializableType SERIALIZABLE = SerializableType.INSTANCE;
-
-
-	/**
-	 * Hibernate <tt>object</tt> type.
-	 * @deprecated Use {@link ObjectType#INSTANCE} instead.
-	 */
-	public static final ObjectType OBJECT = ObjectType.INSTANCE;
-
-	/**
-	 * A Hibernate <tt>serializable</tt> type.
-	 *
-	 * @param serializableClass The {@link java.io.Serializable} implementor class.
-	 *
-	 * @return
-	 *
-	 * @deprecated Use {@link SerializableType#SerializableType} instead.
-	 */
-	@SuppressWarnings({ "unchecked" })
-	public static Type serializable(Class serializableClass) {
-		return new SerializableType( serializableClass );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#any} instead.
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type any(Type metaType, Type identifierType) {
-		return new AnyType( metaType, identifierType );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#entity} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration", "deprecation" })
-	public static Type entity(Class persistentClass) {
-		return entity( persistentClass.getName() );
-	}
-	private static class NoScope implements TypeFactory.TypeScope {
-		public static final NoScope INSTANCE = new NoScope();
-
-		public SessionFactoryImplementor resolveFactory() {
-			throw new HibernateException( "Cannot access SessionFactory from here" );
-		}
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#entity} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type entity(String entityName) {
-		return new ManyToOneType( NoScope.INSTANCE, entityName );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type custom(Class userTypeClass) {
-		return custom( userTypeClass, null );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type custom(Class userTypeClass, String[] parameterNames, String[] parameterValues) {
-		return custom( userTypeClass, toProperties( parameterNames, parameterValues ) );	}
-
-	private static Properties toProperties(String[] parameterNames, String[] parameterValues) {
-		if ( parameterNames == null || parameterNames.length == 0 ) {
-			return null;
-		}
-		Properties parameters = new Properties();
-		for ( int i = 0; i < parameterNames.length; i ++ ) {
-			parameters.put( parameterNames[i], parameterValues[i] );
-		}
-		return parameters;
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration", "unchecked" })
-	public static Type custom(Class userTypeClass, Properties parameters) {
-		if ( CompositeUserType.class.isAssignableFrom( userTypeClass ) ) {
-			return TypeFactory.customComponent( userTypeClass, parameters, NoScope.INSTANCE );
-		}
-		else {
-			return TypeFactory.custom( userTypeClass, parameters, NoScope.INSTANCE );
-		}
-	}
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
-import java.io.Serializable;
-import java.sql.Types;
-import java.util.Calendar;
-import java.util.Date;
-import java.util.Properties;
+import java.io.Serializable;
+import java.sql.Types;
+import java.util.Calendar;
+import java.util.Date;
+import java.util.Properties;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.Hibernate;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.type.StandardBasicTypes;
-import org.jboss.logging.Logger;
-				type = Hibernate.MATERIALIZED_CLOB.getName();
+				type = StandardBasicTypes.MATERIALIZED_CLOB.getName();
-				type = Hibernate.MATERIALIZED_BLOB.getName();
+				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
-import org.hibernate.Hibernate;
+import org.hibernate.type.StandardBasicTypes;
-			new TypedValue( Hibernate.INTEGER, new Integer(size), EntityMode.POJO ) 
+			new TypedValue( StandardBasicTypes.INTEGER, size, EntityMode.POJO )
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-		registerFunction( "sign", new StandardSQLFunction("sign", Hibernate.INTEGER) );
-
-		registerFunction( "acos", new StandardSQLFunction("acos", Hibernate.DOUBLE) );
-		registerFunction( "asin", new StandardSQLFunction("asin", Hibernate.DOUBLE) );
-		registerFunction( "atan", new StandardSQLFunction("atan", Hibernate.DOUBLE) );
-		registerFunction( "cos", new StandardSQLFunction("cos", Hibernate.DOUBLE) );
-		registerFunction( "cot", new StandardSQLFunction("cot", Hibernate.DOUBLE) );
-		registerFunction( "exp", new StandardSQLFunction("exp", Hibernate.DOUBLE) );
-		registerFunction( "ln", new StandardSQLFunction("ln", Hibernate.DOUBLE) );
-		registerFunction( "log", new StandardSQLFunction("log", Hibernate.DOUBLE) );
-		registerFunction( "sin", new StandardSQLFunction("sin", Hibernate.DOUBLE) );
-		registerFunction( "sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE) );
-		registerFunction( "cbrt", new StandardSQLFunction("cbrt", Hibernate.DOUBLE) );
-		registerFunction( "tan", new StandardSQLFunction("tan", Hibernate.DOUBLE) );
-		registerFunction( "radians", new StandardSQLFunction("radians", Hibernate.DOUBLE) );
-		registerFunction( "degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE) );
-
-		registerFunction( "stddev", new StandardSQLFunction("stddev", Hibernate.DOUBLE) );
-		registerFunction( "variance", new StandardSQLFunction("variance", Hibernate.DOUBLE) );
-
-		registerFunction( "random", new NoArgSQLFunction("random", Hibernate.DOUBLE) );
+		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
+
+		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
+		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
+		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
+		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
+		registerFunction( "cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
+		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
+		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
+		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
+		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
+		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
+		registerFunction( "cbrt", new StandardSQLFunction("cbrt", StandardBasicTypes.DOUBLE) );
+		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
+		registerFunction( "radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
+		registerFunction( "degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
+
+		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
+		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
+
+		registerFunction( "random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
-		registerFunction( "chr", new StandardSQLFunction("chr", Hibernate.CHARACTER) );
+		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
-		registerFunction( "substr", new StandardSQLFunction("substr", Hibernate.STRING) );
+		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
-		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", Hibernate.STRING) );
-		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", Hibernate.STRING) );
+		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING) );
+		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING) );
-		registerFunction( "ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER) );
-		registerFunction( "char_length", new StandardSQLFunction("char_length", Hibernate.LONG) );
-		registerFunction( "bit_length", new StandardSQLFunction("bit_length", Hibernate.LONG) );
-		registerFunction( "octet_length", new StandardSQLFunction("octet_length", Hibernate.LONG) );
+		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
+		registerFunction( "char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
+		registerFunction( "bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
+		registerFunction( "octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
-		registerFunction( "current_date", new NoArgSQLFunction("current_date", Hibernate.DATE, false) );
-		registerFunction( "current_time", new NoArgSQLFunction("current_time", Hibernate.TIME, false) );
-		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP, false) );
-		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", Hibernate.TIMESTAMP ) );
-		registerFunction( "localtime", new NoArgSQLFunction("localtime", Hibernate.TIME, false) );
-		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", Hibernate.TIMESTAMP, false) );
-		registerFunction( "now", new NoArgSQLFunction("now", Hibernate.TIMESTAMP) );
-		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", Hibernate.STRING) );
-
-		registerFunction( "current_user", new NoArgSQLFunction("current_user", Hibernate.STRING, false) );
-		registerFunction( "session_user", new NoArgSQLFunction("session_user", Hibernate.STRING, false) );
-		registerFunction( "user", new NoArgSQLFunction("user", Hibernate.STRING, false) );
-		registerFunction( "current_database", new NoArgSQLFunction("current_database", Hibernate.STRING, true) );
-		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", Hibernate.STRING, true) );
+		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
+		registerFunction( "current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
+		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
+		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false) );
+		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false) );
+		registerFunction( "now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING) );
+
+		registerFunction( "current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false) );
+		registerFunction( "session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false) );
+		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
+		registerFunction( "current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true) );
+		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true) );
-		registerFunction( "to_char", new StandardSQLFunction("to_char", Hibernate.STRING) );
-		registerFunction( "to_date", new StandardSQLFunction("to_date", Hibernate.DATE) );
-		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", Hibernate.TIMESTAMP) );
-		registerFunction( "to_number", new StandardSQLFunction("to_number", Hibernate.BIG_DECIMAL) );
+		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
+		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE) );
+		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "to_number", new StandardSQLFunction("to_number", StandardBasicTypes.BIG_DECIMAL) );
-		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "(","||",")" ) );
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
-		registerFunction( "str", new SQLFunctionTemplate(Hibernate.STRING, "cast(?1 as varchar)") );
+		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
-	 * Constraint-name extractor for Postgres contraint violation exceptions.
+	 * Constraint-name extractor for Postgres constraint violation exceptions.
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-		registerFunction( "concat", new StandardSQLFunction("concat", Hibernate.STRING) );
+		registerFunction( "concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING) );
-		registerFunction( "to_char", new StandardSQLFunction("to_char",Hibernate.STRING) );
-		registerFunction( "to_date", new StandardSQLFunction("to_date",Hibernate.TIMESTAMP) );
-		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false) );
-		registerFunction( "getdate", new NoArgSQLFunction("getdate", Hibernate.TIMESTAMP, false) );
+		registerFunction( "to_char", new StandardSQLFunction("to_char",StandardBasicTypes.STRING) );
+		registerFunction( "to_date", new StandardSQLFunction("to_date",StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false) );
+		registerFunction( "getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP, false) );
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
-	/**
-	 * {@inheritDoc} 
-	 */
+	@Override
-		return Hibernate.STRING;
+		return StandardBasicTypes.STRING;
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
-			List argsToUse = new ArrayList();
+			List<String> argsToUse = new ArrayList<String>();
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
-	 * Here we always return {@link Hibernate#STRING}.
+	 * Here we always return {@link StandardBasicTypes#STRING}.
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
- * TODO : javadoc
+ * Centralized definition of standard ANSI SQL aggregation functions
-			super( "count", Hibernate.LONG );
+			super( "count", StandardBasicTypes.LONG );
-			super( "avg", Hibernate.DOUBLE );
+			super( "avg", StandardBasicTypes.DOUBLE );
-			if ( firstArgumentType == Hibernate.BIG_INTEGER ) {
-				return Hibernate.BIG_INTEGER;
+			if ( firstArgumentType == StandardBasicTypes.BIG_INTEGER ) {
+				return StandardBasicTypes.BIG_INTEGER;
-			else if ( firstArgumentType == Hibernate.BIG_DECIMAL ) {
-				return Hibernate.BIG_DECIMAL;
+			else if ( firstArgumentType == StandardBasicTypes.BIG_DECIMAL ) {
+				return StandardBasicTypes.BIG_DECIMAL;
-			else if ( firstArgumentType == Hibernate.LONG
-					|| firstArgumentType == Hibernate.SHORT
-					|| firstArgumentType == Hibernate.INTEGER ) {
-				return Hibernate.LONG;
+			else if ( firstArgumentType == StandardBasicTypes.LONG
+					|| firstArgumentType == StandardBasicTypes.SHORT
+					|| firstArgumentType == StandardBasicTypes.INTEGER ) {
+				return StandardBasicTypes.LONG;
-			else if ( firstArgumentType == Hibernate.FLOAT || firstArgumentType == Hibernate.DOUBLE)  {
-				return Hibernate.DOUBLE;
+			else if ( firstArgumentType == StandardBasicTypes.FLOAT || firstArgumentType == StandardBasicTypes.DOUBLE)  {
+				return StandardBasicTypes.DOUBLE;
-				return Hibernate.DOUBLE;
+				return StandardBasicTypes.DOUBLE;
-				return Hibernate.LONG;
+				return StandardBasicTypes.LONG;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.Hibernate;
-import org.hibernate.type.Type;
+
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
- * Contract for nodes representing logcial BETWEEN (ternary) operators.
+ * Contract for nodes representing logical BETWEEN (ternary) operators.
-		return Hibernate.BOOLEAN;
+		return StandardBasicTypes.BOOLEAN;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.Hibernate;
+
+import antlr.SemanticException;
+
+import org.hibernate.type.StandardBasicTypes;
-import antlr.SemanticException;
-				expectedType = getType() == HqlSqlTokenTypes.PLUS ? Hibernate.DOUBLE : rhType;
+				expectedType = getType() == HqlSqlTokenTypes.PLUS ? StandardBasicTypes.DOUBLE : rhType;
-					expectedType = Hibernate.DOUBLE;
+					expectedType = StandardBasicTypes.DOUBLE;
-					return Hibernate.DOUBLE; //BLIND GUESS!
+					return StandardBasicTypes.DOUBLE; //BLIND GUESS!
-					if ( lhType==Hibernate.DOUBLE || rhType==Hibernate.DOUBLE ) return Hibernate.DOUBLE;
-					if ( lhType==Hibernate.FLOAT || rhType==Hibernate.FLOAT ) return Hibernate.FLOAT;
-					if ( lhType==Hibernate.BIG_DECIMAL || rhType==Hibernate.BIG_DECIMAL ) return Hibernate.BIG_DECIMAL;
-					if ( lhType==Hibernate.BIG_INTEGER || rhType==Hibernate.BIG_INTEGER ) return Hibernate.BIG_INTEGER;
-					if ( lhType==Hibernate.LONG || rhType==Hibernate.LONG ) return Hibernate.LONG;
-					if ( lhType==Hibernate.INTEGER || rhType==Hibernate.INTEGER ) return Hibernate.INTEGER;
+					if ( lhType== StandardBasicTypes.DOUBLE || rhType==StandardBasicTypes.DOUBLE ) {
+						return StandardBasicTypes.DOUBLE;
+					}
+					if ( lhType==StandardBasicTypes.FLOAT || rhType==StandardBasicTypes.FLOAT ) {
+						return StandardBasicTypes.FLOAT;
+					}
+					if ( lhType==StandardBasicTypes.BIG_DECIMAL || rhType==StandardBasicTypes.BIG_DECIMAL ) {
+						return StandardBasicTypes.BIG_DECIMAL;
+					}
+					if ( lhType==StandardBasicTypes.BIG_INTEGER || rhType==StandardBasicTypes.BIG_INTEGER ) {
+						return StandardBasicTypes.BIG_INTEGER;
+					}
+					if ( lhType==StandardBasicTypes.LONG || rhType==StandardBasicTypes.LONG ) {
+						return StandardBasicTypes.LONG;
+					}
+					if ( lhType==StandardBasicTypes.INTEGER || rhType==StandardBasicTypes.INTEGER ) {
+						return StandardBasicTypes.INTEGER;
+					}
-				return Hibernate.DOUBLE;
+				return StandardBasicTypes.DOUBLE;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
-import org.hibernate.Hibernate;
+import antlr.SemanticException;
+import antlr.collections.AST;
+
+import org.hibernate.type.StandardBasicTypes;
-import antlr.SemanticException;
-import antlr.collections.AST;
-	/**
-	 * 
-	 */
+
-		return Hibernate.BOOLEAN;
+		return StandardBasicTypes.BOOLEAN;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
-import org.hibernate.Hibernate;
-import org.hibernate.QueryException;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.type.LiteralType;
-import org.hibernate.type.Type;
-
-/**
- * Represents a boolean literal within a query.
- *
- * @author Steve Ebersole
- */
-public class BooleanLiteralNode extends LiteralNode implements ExpectedTypeAwareNode {
-	private Type expectedType;
-
-	public Type getDataType() {
-		return expectedType == null ? Hibernate.BOOLEAN : expectedType;
-	}
-
-	public Boolean getValue() {
-		return getType() == TRUE ? Boolean.TRUE : Boolean.FALSE;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setExpectedType(Type expectedType) {
-		this.expectedType = expectedType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Type getExpectedType() {
-		return expectedType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String getRenderText(SessionFactoryImplementor sessionFactory) {
-		try {
-			return typeAsLiteralType().objectToSQLString( getValue(), sessionFactory.getDialect() );
-		}
-		catch( Throwable t ) {
-			throw new QueryException( "Unable to render boolean literal value", t );
-		}
-	}
-
-	private LiteralType typeAsLiteralType() {
-		return (LiteralType) getDataType();
-
-	}
-}
+
+import org.hibernate.QueryException;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.type.LiteralType;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * Represents a boolean literal within a query.
+ *
+ * @author Steve Ebersole
+ */
+public class BooleanLiteralNode extends LiteralNode implements ExpectedTypeAwareNode {
+	private Type expectedType;
+
+	public Type getDataType() {
+		return expectedType == null ? StandardBasicTypes.BOOLEAN : expectedType;
+	}
+
+	public Boolean getValue() {
+		return getType() == TRUE ? Boolean.TRUE : Boolean.FALSE;
+	}
+
+	@Override
+	public void setExpectedType(Type expectedType) {
+		this.expectedType = expectedType;
+	}
+
+	@Override
+	public Type getExpectedType() {
+		return expectedType;
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public String getRenderText(SessionFactoryImplementor sessionFactory) {
+		try {
+			return typeAsLiteralType().objectToSQLString( getValue(), sessionFactory.getDialect() );
+		}
+		catch( Throwable t ) {
+			throw new QueryException( "Unable to render boolean literal value", t );
+		}
+	}
+
+	private LiteralType typeAsLiteralType() {
+		return (LiteralType) getDataType();
+
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.Hibernate;
+
+import antlr.SemanticException;
+
+import org.hibernate.type.StandardBasicTypes;
-import antlr.SemanticException;
-				return Hibernate.INTEGER;
+				return StandardBasicTypes.INTEGER;
-				return Hibernate.FLOAT;
+				return StandardBasicTypes.FLOAT;
-				return Hibernate.LONG;
+				return StandardBasicTypes.LONG;
-				return Hibernate.DOUBLE;
+				return StandardBasicTypes.DOUBLE;
-				return Hibernate.BIG_INTEGER;
+				return StandardBasicTypes.BIG_INTEGER;
-				return Hibernate.BIG_DECIMAL;
+				return StandardBasicTypes.BIG_DECIMAL;
-				return Hibernate.STRING;
+				return StandardBasicTypes.STRING;
-				return Hibernate.BOOLEAN;
+				return StandardBasicTypes.BOOLEAN;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-		return Hibernate.BOOLEAN;
+		return StandardBasicTypes.BOOLEAN;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
-import org.hibernate.Hibernate;
+
-import org.hibernate.type.Type;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
-				q.addSelectScalar( getFunction( "count", q ).getReturnType( Hibernate.LONG, q.getFactory() ) );
+				q.addSelectScalar( getFunction( "count", q ).getReturnType( StandardBasicTypes.LONG, q.getFactory() ) );
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
+
-import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.internal.util.MarkerObject;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.type.StandardBasicTypes;
-import org.hibernate.internal.util.MarkerObject;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.internal.util.StringHelper;
-			setParameter( position, val, Hibernate.SERIALIZABLE );
+			setParameter( position, val, StandardBasicTypes.SERIALIZABLE );
-				type = Hibernate.SERIALIZABLE;
+				type = StandardBasicTypes.SERIALIZABLE;
-			return Hibernate.entity(clazz);
+			return ( (Session) session ).getTypeHelper().entity( clazz );
-		setParameter(position, val, Hibernate.STRING);
+		setParameter(position, val, StandardBasicTypes.STRING);
-		setParameter(position, new Character(val), Hibernate.CHARACTER);
+		setParameter(position, new Character(val), StandardBasicTypes.CHARACTER);
-		Type typeToUse = determineType( position, valueToUse, Hibernate.BOOLEAN );
+		Type typeToUse = determineType( position, valueToUse, StandardBasicTypes.BOOLEAN );
-		setParameter(position, new Byte(val), Hibernate.BYTE);
+		setParameter(position, new Byte(val), StandardBasicTypes.BYTE);
-		setParameter(position, new Short(val), Hibernate.SHORT);
+		setParameter(position, new Short(val), StandardBasicTypes.SHORT);
-		setParameter(position, new Integer(val), Hibernate.INTEGER);
+		setParameter(position, new Integer(val), StandardBasicTypes.INTEGER);
-		setParameter(position, new Long(val), Hibernate.LONG);
+		setParameter(position, new Long(val), StandardBasicTypes.LONG);
-		setParameter(position, new Float(val), Hibernate.FLOAT);
+		setParameter(position, new Float(val), StandardBasicTypes.FLOAT);
-		setParameter(position, new Double(val), Hibernate.DOUBLE);
+		setParameter(position, new Double(val), StandardBasicTypes.DOUBLE);
-		setParameter(position, val, Hibernate.BINARY);
+		setParameter(position, val, StandardBasicTypes.BINARY);
-		setParameter(position, val, Hibernate.TEXT);
+		setParameter(position, val, StandardBasicTypes.TEXT);
-		setParameter(position, val, Hibernate.SERIALIZABLE);
+		setParameter(position, val, StandardBasicTypes.SERIALIZABLE);
-		setParameter(position, date, Hibernate.DATE);
+		setParameter(position, date, StandardBasicTypes.DATE);
-		setParameter(position, date, Hibernate.TIME);
+		setParameter(position, date, StandardBasicTypes.TIME);
-		setParameter(position, date, Hibernate.TIMESTAMP);
+		setParameter(position, date, StandardBasicTypes.TIMESTAMP);
-		setParameter( position, val, Hibernate.entity( resolveEntityName( val ) ) );
+		setParameter( position, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
-		setParameter(position, locale, Hibernate.LOCALE);
+		setParameter(position, locale, StandardBasicTypes.LOCALE);
-		setParameter(position, calendar, Hibernate.CALENDAR);
+		setParameter(position, calendar, StandardBasicTypes.CALENDAR);
-		setParameter(position, calendar, Hibernate.CALENDAR_DATE);
+		setParameter(position, calendar, StandardBasicTypes.CALENDAR_DATE);
-		setParameter(name, val, Hibernate.BINARY);
+		setParameter(name, val, StandardBasicTypes.BINARY);
-		setParameter(name, val, Hibernate.TEXT);
+		setParameter(name, val, StandardBasicTypes.TEXT);
-		Type typeToUse = determineType( name, valueToUse, Hibernate.BOOLEAN );
+		Type typeToUse = determineType( name, valueToUse, StandardBasicTypes.BOOLEAN );
-		setParameter(name, new Byte(val), Hibernate.BYTE);
+		setParameter(name, new Byte(val), StandardBasicTypes.BYTE);
-		setParameter(name, new Character(val), Hibernate.CHARACTER);
+		setParameter(name, new Character(val), StandardBasicTypes.CHARACTER);
-		setParameter(name, date, Hibernate.DATE);
+		setParameter(name, date, StandardBasicTypes.DATE);
-		setParameter(name, new Double(val), Hibernate.DOUBLE);
+		setParameter(name, new Double(val), StandardBasicTypes.DOUBLE);
-		setParameter( name, val, Hibernate.entity( resolveEntityName( val ) ) );
+		setParameter( name, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
-		setParameter(name, new Float(val), Hibernate.FLOAT);
+		setParameter(name, new Float(val), StandardBasicTypes.FLOAT);
-		setParameter(name, new Integer(val), Hibernate.INTEGER);
+		setParameter(name, new Integer(val), StandardBasicTypes.INTEGER);
-		setParameter(name, locale, Hibernate.LOCALE);
+		setParameter(name, locale, StandardBasicTypes.LOCALE);
-		setParameter(name, calendar, Hibernate.CALENDAR);
+		setParameter(name, calendar, StandardBasicTypes.CALENDAR);
-		setParameter(name, calendar, Hibernate.CALENDAR_DATE);
+		setParameter(name, calendar, StandardBasicTypes.CALENDAR_DATE);
-		setParameter(name, new Long(val), Hibernate.LONG);
+		setParameter(name, new Long(val), StandardBasicTypes.LONG);
-		setParameter(name, val, Hibernate.SERIALIZABLE);
+		setParameter(name, val, StandardBasicTypes.SERIALIZABLE);
-		setParameter(name, new Short(val), Hibernate.SHORT);
+		setParameter(name, new Short(val), StandardBasicTypes.SHORT);
-		setParameter(name, val, Hibernate.STRING);
+		setParameter(name, val, StandardBasicTypes.STRING);
-		setParameter(name, date, Hibernate.TIME);
+		setParameter(name, date, StandardBasicTypes.TIME);
-		setParameter(name, date, Hibernate.TIMESTAMP);
+		setParameter(name, date, StandardBasicTypes.TIMESTAMP);
-		setParameter(position, number, Hibernate.BIG_DECIMAL);
+		setParameter(position, number, StandardBasicTypes.BIG_DECIMAL);
-		setParameter(name, number, Hibernate.BIG_DECIMAL);
+		setParameter(name, number, StandardBasicTypes.BIG_DECIMAL);
-		setParameter(position, number, Hibernate.BIG_INTEGER);
+		setParameter(position, number, StandardBasicTypes.BIG_INTEGER);
-		setParameter(name, number, Hibernate.BIG_INTEGER);
+		setParameter(name, number, StandardBasicTypes.BIG_INTEGER);
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
-import org.hibernate.Hibernate;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.type.StandardBasicTypes;
-import org.jboss.logging.Logger;
-		return (BigDecimal) getFinal(col, Hibernate.BIG_DECIMAL);
+		return (BigDecimal) getFinal(col, StandardBasicTypes.BIG_DECIMAL);
-		return (BigInteger) getFinal(col, Hibernate.BIG_INTEGER);
+		return (BigInteger) getFinal(col, StandardBasicTypes.BIG_INTEGER);
-		return (byte[]) getFinal(col, Hibernate.BINARY);
+		return (byte[]) getFinal(col, StandardBasicTypes.BINARY);
-		return (String) getFinal(col, Hibernate.TEXT);
+		return (String) getFinal(col, StandardBasicTypes.TEXT);
-		return (Blob) getNonFinal(col, Hibernate.BLOB);
+		return (Blob) getNonFinal(col, StandardBasicTypes.BLOB);
-		return (Clob) getNonFinal(col, Hibernate.CLOB);
+		return (Clob) getNonFinal(col, StandardBasicTypes.CLOB);
-		return (Boolean) getFinal(col, Hibernate.BOOLEAN);
+		return (Boolean) getFinal(col, StandardBasicTypes.BOOLEAN);
-		return (Byte) getFinal(col, Hibernate.BYTE);
+		return (Byte) getFinal(col, StandardBasicTypes.BYTE);
-		return (Character) getFinal(col, Hibernate.CHARACTER);
+		return (Character) getFinal(col, StandardBasicTypes.CHARACTER);
-		return (Date) getNonFinal(col, Hibernate.TIMESTAMP);
+		return (Date) getNonFinal(col, StandardBasicTypes.TIMESTAMP);
-		return (Calendar) getNonFinal(col, Hibernate.CALENDAR);
+		return (Calendar) getNonFinal(col, StandardBasicTypes.CALENDAR);
-		return (Double) getFinal(col, Hibernate.DOUBLE);
+		return (Double) getFinal(col, StandardBasicTypes.DOUBLE);
-		return (Float) getFinal(col, Hibernate.FLOAT);
+		return (Float) getFinal(col, StandardBasicTypes.FLOAT);
-		return (Integer) getFinal(col, Hibernate.INTEGER);
+		return (Integer) getFinal(col, StandardBasicTypes.INTEGER);
-		return (Long) getFinal(col, Hibernate.LONG);
+		return (Long) getFinal(col, StandardBasicTypes.LONG);
-		return (Short) getFinal(col, Hibernate.SHORT);
+		return (Short) getFinal(col, StandardBasicTypes.SHORT);
-		return (String) getFinal(col, Hibernate.STRING);
+		return (String) getFinal(col, StandardBasicTypes.STRING);
-		return (Locale) getFinal(col, Hibernate.LOCALE);
+		return (Locale) getFinal(col, StandardBasicTypes.LOCALE);
-		return (TimeZone) getNonFinal(col, Hibernate.TIMEZONE);
+		return (TimeZone) getNonFinal(col, StandardBasicTypes.TIMEZONE);
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-			return Hibernate.INTEGER;
+			return StandardBasicTypes.INTEGER;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
-import java.io.Serializable;
-import java.util.ArrayList;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import org.hibernate.AssertionFailure;
-import org.hibernate.Hibernate;
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.QueryException;
-import org.hibernate.cache.access.EntityRegionAccessStrategy;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.Versioning;
-import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.mapping.Column;
-import org.hibernate.mapping.Join;
-import org.hibernate.mapping.KeyValue;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.mapping.Selectable;
-import org.hibernate.mapping.Subclass;
-import org.hibernate.mapping.Table;
-import org.hibernate.sql.CaseFragment;
-import org.hibernate.sql.SelectFragment;
-import org.hibernate.type.Type;
-
-/**
- * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
- * mapping strategy
- *
- * @author Gavin King
- */
-public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
-
-	// the class hierarchy structure
-	private final int tableSpan;
-	private final String[] tableNames;
-	private final String[] naturalOrderTableNames;
-	private final String[][] tableKeyColumns;
-	private final String[][] tableKeyColumnReaders;
-	private final String[][] tableKeyColumnReaderTemplates;
-	private final String[][] naturalOrderTableKeyColumns;
-	private final String[][] naturalOrderTableKeyColumnReaders;
-	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
-	private final boolean[] naturalOrderCascadeDeleteEnabled;
-
-	private final String[] spaces;
-
-	private final String[] subclassClosure;
-
-	private final String[] subclassTableNameClosure;
-	private final String[][] subclassTableKeyColumnClosure;
-	private final boolean[] isClassOrSuperclassTable;
-
-	// properties of this class, including inherited properties
-	private final int[] naturalOrderPropertyTableNumbers;
-	private final int[] propertyTableNumbers;
-
-	// the closure of all properties in the entire hierarchy including
-	// subclasses and superclasses of this class
-	private final int[] subclassPropertyTableNumberClosure;
-
-	// the closure of all columns used by the entire hierarchy including
-	// subclasses and superclasses of this class
-	private final int[] subclassColumnTableNumberClosure;
-	private final int[] subclassFormulaTableNumberClosure;
-
-	private final boolean[] subclassTableSequentialSelect;
-	private final boolean[] subclassTableIsLazyClosure;
-	
-	// subclass discrimination works by assigning particular
-	// values to certain combinations of null primary key
-	// values in the outer join using an SQL CASE
-	private final Map subclassesByDiscriminatorValue = new HashMap();
-	private final String[] discriminatorValues;
-	private final String[] notNullColumnNames;
-	private final int[] notNullColumnTableNumbers;
-
-	private final String[] constraintOrderedTableNames;
-	private final String[][] constraintOrderedKeyColumnNames;
-
-	private final String discriminatorSQLString;
-
-	//INITIALIZATION:
-
-	public JoinedSubclassEntityPersister(
-			final PersistentClass persistentClass,
-			final EntityRegionAccessStrategy cacheAccessStrategy,
-			final SessionFactoryImplementor factory,
-			final Mapping mapping) throws HibernateException {
-
-		super( persistentClass, cacheAccessStrategy, factory );
-
-		// DISCRIMINATOR
-
-		final Object discriminatorValue;
-		if ( persistentClass.isPolymorphic() ) {
-			try {
-				discriminatorValue = new Integer( persistentClass.getSubclassId() );
-				discriminatorSQLString = discriminatorValue.toString();
-			}
-			catch (Exception e) {
-				throw new MappingException("Could not format discriminator value to SQL string", e );
-			}
-		}
-		else {
-			discriminatorValue = null;
-			discriminatorSQLString = null;
-		}
-
-		if ( optimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION ) {
-			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
-		}
-
-		//MULTITABLES
-
-		final int idColumnSpan = getIdentifierColumnSpan();
-
-		ArrayList tables = new ArrayList();
-		ArrayList keyColumns = new ArrayList();
-		ArrayList keyColumnReaders = new ArrayList();
-		ArrayList keyColumnReaderTemplates = new ArrayList();
-		ArrayList cascadeDeletes = new ArrayList();
-		Iterator titer = persistentClass.getTableClosureIterator();
-		Iterator kiter = persistentClass.getKeyClosureIterator();
-		while ( titer.hasNext() ) {
-			Table tab = (Table) titer.next();
-			KeyValue key = (KeyValue) kiter.next();
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			tables.add(tabname);
-			String[] keyCols = new String[idColumnSpan];
-			String[] keyColReaders = new String[idColumnSpan];
-			String[] keyColReaderTemplates = new String[idColumnSpan];
-			Iterator citer = key.getColumnIterator();
-			for ( int k=0; k<idColumnSpan; k++ ) {
-				Column column = (Column) citer.next();
-				keyCols[k] = column.getQuotedName( factory.getDialect() );
-				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
-				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
-			}
-			keyColumns.add(keyCols);
-			keyColumnReaders.add(keyColReaders);
-			keyColumnReaderTemplates.add(keyColReaderTemplates);
-			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
-		}
-		
-		//Span of the tables directly mapped by this entity and super-classes, if any
-		int coreTableSpan = tables.size();
-		
-		Iterator joinIter = persistentClass.getJoinClosureIterator();
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-			
-			Table tab = join.getTable();
-			 
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			tables.add(tabname);
-			
-			KeyValue key = join.getKey();
-			int joinIdColumnSpan = 	key.getColumnSpan();		
-			
-			String[] keyCols = new String[joinIdColumnSpan];
-			String[] keyColReaders = new String[joinIdColumnSpan];
-			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
-						
-			Iterator citer = key.getColumnIterator();
-			
-			for ( int k=0; k<joinIdColumnSpan; k++ ) {
-				Column column = (Column) citer.next();
-				keyCols[k] = column.getQuotedName( factory.getDialect() );
-				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
-				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
-			}
-			keyColumns.add(keyCols);
-			keyColumnReaders.add(keyColReaders);
-			keyColumnReaderTemplates.add(keyColReaderTemplates);
-			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
-		}
-		
-		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
-		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray(keyColumns);
-		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray(keyColumnReaders);
-		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray(keyColumnReaderTemplates);
-		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray(cascadeDeletes);
-
-		ArrayList subtables = new ArrayList();
-		ArrayList isConcretes = new ArrayList();
-		ArrayList isDeferreds = new ArrayList();
-		ArrayList isLazies = new ArrayList();
-		
-		keyColumns = new ArrayList();
-		titer = persistentClass.getSubclassTableClosureIterator();
-		while ( titer.hasNext() ) {
-			Table tab = (Table) titer.next();
-			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
-			isDeferreds.add(Boolean.FALSE);
-			isLazies.add(Boolean.FALSE);
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			subtables.add(tabname);
-			String[] key = new String[idColumnSpan];
-			Iterator citer = tab.getPrimaryKey().getColumnIterator();
-			for ( int k=0; k<idColumnSpan; k++ ) {
-				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
-			}
-			keyColumns.add(key);
-		}
-		
-		//Add joins
-		joinIter = persistentClass.getSubclassJoinClosureIterator();
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-			
-			Table tab = join.getTable();
-			 
-			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
-			isDeferreds.add( new Boolean( join.isSequentialSelect() ) );
-			isLazies.add(new Boolean(join.isLazy()));
-			
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			subtables.add(tabname);
-			String[] key = new String[idColumnSpan];
-			Iterator citer = tab.getPrimaryKey().getColumnIterator();
-			for ( int k=0; k<idColumnSpan; k++ ) {
-				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
-			}
-			keyColumns.add(key);
-						
-		}
-				
-		String [] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray(subtables);
-		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(keyColumns);
-		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
-		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
-		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
-		
-		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
-		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
-		int currentPosition = 0;
-		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0 ; i--, currentPosition++ ) {
-			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
-			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
-		} 
-
-		/**
-		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
-		 * For the Client entity:
-		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
-		 * added to the meta-data when the annotated entities are processed.
-		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
-		 * the first table as it will the driving table.
-		 * tableNames -> CLIENT, PERSON
-		 */
-				
-		tableSpan = naturalOrderTableNames.length;
- 		tableNames = reverse(naturalOrderTableNames, coreTableSpan);
-		tableKeyColumns = reverse(naturalOrderTableKeyColumns, coreTableSpan);
-		tableKeyColumnReaders = reverse(naturalOrderTableKeyColumnReaders, coreTableSpan);
-		tableKeyColumnReaderTemplates = reverse(naturalOrderTableKeyColumnReaderTemplates, coreTableSpan);
-		subclassTableNameClosure = reverse(naturalOrderSubclassTableNameClosure, coreTableSpan);
-		subclassTableKeyColumnClosure = reverse(naturalOrderSubclassTableKeyColumnClosure, coreTableSpan);
- 
-		spaces = ArrayHelper.join(
-				tableNames,
-				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
-		);
-
-		// Custom sql
-		customSQLInsert = new String[tableSpan];
-		customSQLUpdate = new String[tableSpan];
-		customSQLDelete = new String[tableSpan];
-		insertCallable = new boolean[tableSpan];
-		updateCallable = new boolean[tableSpan];
-		deleteCallable = new boolean[tableSpan];
-		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
-		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
-		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
-
-		PersistentClass pc = persistentClass;
-		int jk = coreTableSpan-1;
-		while (pc!=null) {
-			customSQLInsert[jk] = pc.getCustomSQLInsert();
-			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
-			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[jk], insertCallable[jk] )
-		                                  : pc.getCustomSQLInsertCheckStyle();
-			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
-			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
-			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
-		                                  : pc.getCustomSQLUpdateCheckStyle();
-			customSQLDelete[jk] = pc.getCustomSQLDelete();
-			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
-			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
-		                                  : pc.getCustomSQLDeleteCheckStyle();
-			jk--;
-			pc = pc.getSuperclass();
-		}
-		
-		if ( jk != -1 ) {
-			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
-		}
- 
-		joinIter = persistentClass.getJoinClosureIterator();
-		int j = coreTableSpan;
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-			
-			customSQLInsert[j] = join.getCustomSQLInsert();
-			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
-			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
-		                                  : join.getCustomSQLInsertCheckStyle();
-			customSQLUpdate[j] = join.getCustomSQLUpdate();
-			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
-			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
-		                                  : join.getCustomSQLUpdateCheckStyle();
-			customSQLDelete[j] = join.getCustomSQLDelete();
-			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
-			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
-		                                  : join.getCustomSQLDeleteCheckStyle();
-			j++;
-		}
-		
-		// PROPERTIES
-		int hydrateSpan = getPropertySpan();
-		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
-		propertyTableNumbers = new int[hydrateSpan];
-		Iterator iter = persistentClass.getPropertyClosureIterator();
-		int i=0;
-		while( iter.hasNext() ) {
-			Property prop = (Property) iter.next();
-			String tabname = prop.getValue().getTable().getQualifiedName(
-				factory.getDialect(),
-				factory.getSettings().getDefaultCatalogName(),
-				factory.getSettings().getDefaultSchemaName()
-			);
-			propertyTableNumbers[i] = getTableId(tabname, tableNames);
-			naturalOrderPropertyTableNumbers[i] = getTableId(tabname, naturalOrderTableNames);
-			i++;
-		}
-
-		// subclass closure properties
-
-		//TODO: code duplication with SingleTableEntityPersister
-
-		ArrayList columnTableNumbers = new ArrayList();
-		ArrayList formulaTableNumbers = new ArrayList();
-		ArrayList propTableNumbers = new ArrayList();
-
-		iter = persistentClass.getSubclassPropertyClosureIterator();
-		while ( iter.hasNext() ) {
-			Property prop = (Property) iter.next();
-			Table tab = prop.getValue().getTable();
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			Integer tabnum = new Integer( getTableId(tabname, subclassTableNameClosure) );
-  			propTableNumbers.add(tabnum);
-
-			Iterator citer = prop.getColumnIterator();
-			while ( citer.hasNext() ) {
-				Selectable thing = (Selectable) citer.next();
-				if ( thing.isFormula() ) {
-					formulaTableNumbers.add(tabnum);
-				}
-				else {
-					columnTableNumbers.add(tabnum);
-				}
-			}
-
-		}
-
-		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnTableNumbers);
-		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTableNumbers);
-		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaTableNumbers);
-
-		// SUBCLASSES
- 
-		int subclassSpan = persistentClass.getSubclassSpan() + 1;
-		subclassClosure = new String[subclassSpan];
-		subclassClosure[subclassSpan-1] = getEntityName();
-		if ( persistentClass.isPolymorphic() ) {
-			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
-			discriminatorValues = new String[subclassSpan];
-			discriminatorValues[subclassSpan-1] = discriminatorSQLString;
-			notNullColumnTableNumbers = new int[subclassSpan];
-			final int id = getTableId(
-				persistentClass.getTable().getQualifiedName(
-						factory.getDialect(),
-						factory.getSettings().getDefaultCatalogName(),
-						factory.getSettings().getDefaultSchemaName()
-				),
-				subclassTableNameClosure
-			);
-			notNullColumnTableNumbers[subclassSpan-1] = id;
-			notNullColumnNames = new String[subclassSpan];
-			notNullColumnNames[subclassSpan-1] =  subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
-		}
-		else {
-			discriminatorValues = null;
-			notNullColumnTableNumbers = null;
-			notNullColumnNames = null;
-		}
-
-		iter = persistentClass.getSubclassIterator();
-		int k=0;
-		while ( iter.hasNext() ) {
-			Subclass sc = (Subclass) iter.next();
-			subclassClosure[k] = sc.getEntityName();
-			try {
-				if ( persistentClass.isPolymorphic() ) {
-					// we now use subclass ids that are consistent across all
-					// persisters for a class hierarchy, so that the use of
-					// "foo.class = Bar" works in HQL
-					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
-					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
-					discriminatorValues[k] = subclassId.toString();
-					int id = getTableId(
-						sc.getTable().getQualifiedName(
-								factory.getDialect(),
-								factory.getSettings().getDefaultCatalogName(),
-								factory.getSettings().getDefaultSchemaName()
-						),
-						subclassTableNameClosure
-					);
-					notNullColumnTableNumbers[k] = id;
-					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
-				}
-			}
-			catch (Exception e) {
-				throw new MappingException("Error parsing discriminator value", e );
-			}
-			k++;
-		}
-
-		initLockers();
-
-		initSubclassPropertyAliasesMap(persistentClass);
-
-		postConstruct(mapping);
-
-	}
-
-	protected boolean isSubclassTableSequentialSelect(int j) {
-		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
-	}
-	
-	
-	/*public void postInstantiate() throws MappingException {
-		super.postInstantiate();
-		//TODO: other lock modes?
-		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
-	}*/
-
-	public String getSubclassPropertyTableName(int i) {
-		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
-	}
-
-	public Type getDiscriminatorType() {
-		return Hibernate.INTEGER;
-	}
-
-	public String getDiscriminatorSQLValue() {
-		return discriminatorSQLString;
-	}
-
-
-	public String getSubclassForDiscriminatorValue(Object value) {
-		return (String) subclassesByDiscriminatorValue.get(value);
-	}
-
-	public Serializable[] getPropertySpaces() {
-		return spaces; // don't need subclass tables, because they can't appear in conditions
-	}
-
-
-	protected String getTableName(int j) {
-		return naturalOrderTableNames[j];
-	}
-
-	protected String[] getKeyColumns(int j) {
-		return naturalOrderTableKeyColumns[j];
-	}
-
-	protected boolean isTableCascadeDeleteEnabled(int j) {
-		return naturalOrderCascadeDeleteEnabled[j];
-	}
-
-	protected boolean isPropertyOfTable(int property, int j) {
-		return naturalOrderPropertyTableNumbers[property]==j;
-	}
-
-	/**
-	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
-	 * depending upon the value of the <tt>lock</tt> parameter
-	 */
-	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
-	throws HibernateException {
-
-		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
-
-		final UniqueEntityLoader loader = hasQueryLoader() ?
-				getQueryLoader() :
-				this.loader;
-		try {
-
-			final Object result = loader.load(id, optionalObject, session);
-
-			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
-
-			return result;
-
-		}
-		catch (SQLException sqle) {
-			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
-		}
-	}*/
-
-	private static final void reverse(Object[] objects, int len) {
-		Object[] temp = new Object[len];
-		for (int i=0; i<len; i++) {
-			temp[i] = objects[len-i-1];
-		}
-		for (int i=0; i<len; i++) {
-			objects[i] = temp[i];
-		}
-	}
-
-	
-	/**
-	 * Reverse the first n elements of the incoming array
-	 * @param objects
-	 * @param n
-	 * @return New array with the first n elements in reversed order 
-	 */
-	private static final String[] reverse(String [] objects, int n) {
-		
-		int size = objects.length;
-		String[] temp = new String[size];
-		
-		for (int i=0; i<n; i++) {
-			temp[i] = objects[n-i-1];
-		}
-		
-		for (int i=n; i < size; i++) {
-			temp[i] =  objects[i];
-		}
-		
-		return temp;
-	}
-		
-	/**
-	 * Reverse the first n elements of the incoming array
-	 * @param objects
-	 * @param n
-	 * @return New array with the first n elements in reversed order 
-	 */
-	private static final String[][] reverse(String[][] objects, int n) {
-		int size = objects.length;
-		String[][] temp = new String[size][];
-		for (int i=0; i<n; i++) {
-			temp[i] = objects[n-i-1];
-		}
-		
-		for (int i=n; i<size; i++) {
-			temp[i] = objects[i];
-		}
-		
-		return temp;
-	}
-	
-	
-	
-	public String fromTableFragment(String alias) {
-		return getTableName() + ' ' + alias;
-	}
-
-	public String getTableName() {
-		return tableNames[0];
-	}
-
-	private static int getTableId(String tableName, String[] tables) {
-		for ( int j=0; j<tables.length; j++ ) {
-			if ( tableName.equals( tables[j] ) ) {
-				return j;
-			}
-		}
-		throw new AssertionFailure("Table " + tableName + " not found");
-	}
-
-	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
-		if ( hasSubclasses() ) {
-			select.setExtraSelectList( discriminatorFragment(name), getDiscriminatorAlias() );
-		}
-	}
-
-	private CaseFragment discriminatorFragment(String alias) {
-		CaseFragment cases = getFactory().getDialect().createCaseFragment();
-
-		for ( int i=0; i<discriminatorValues.length; i++ ) {
-			cases.addWhenColumnNotNull(
-				generateTableAlias( alias, notNullColumnTableNumbers[i] ),
-				notNullColumnNames[i],
-				discriminatorValues[i]
-			);
-		}
-
-		return cases;
-	}
-
-	public String filterFragment(String alias) {
-		return hasWhere() ?
-			" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
-			"";
-	}
-
-	public String generateFilterConditionAlias(String rootAlias) {
-		return generateTableAlias( rootAlias, tableSpan-1 );
-	}
-
-	public String[] getIdentifierColumnNames() {
-		return tableKeyColumns[0];
-	}
-
-	public String[] getIdentifierColumnReaderTemplates() {
-		return tableKeyColumnReaderTemplates[0];
-	}
-
-	public String[] getIdentifierColumnReaders() {
-		return tableKeyColumnReaders[0];
-	}		
-	
-	public String[] toColumns(String alias, String propertyName) throws QueryException {
-
-		if ( ENTITY_CLASS.equals(propertyName) ) {
-			// This doesn't actually seem to work but it *might*
-			// work on some dbs. Also it doesn't work if there
-			// are multiple columns of results because it
-			// is not accounting for the suffix:
-			// return new String[] { getDiscriminatorColumnName() };
-
-			return new String[] { discriminatorFragment(alias).toFragmentString() };
-		}
-		else {
-			return super.toColumns(alias, propertyName);
-		}
-
-	}
-
-	protected int[] getPropertyTableNumbersInSelect() {
-		return propertyTableNumbers;
-	}
-
-	protected int getSubclassPropertyTableNumber(int i) {
-		return subclassPropertyTableNumberClosure[i];
-	}
-
-	public int getTableSpan() {
-		return tableSpan;
-	}
-
-	public boolean isMultiTable() {
-		return true;
-	}
-
-	protected int[] getSubclassColumnTableNumberClosure() {
-		return subclassColumnTableNumberClosure;
-	}
-
-	protected int[] getSubclassFormulaTableNumberClosure() {
-		return subclassFormulaTableNumberClosure;
-	}
-
-	protected int[] getPropertyTableNumbers() {
-		return naturalOrderPropertyTableNumbers;
-	}
-
-	protected String[] getSubclassTableKeyColumns(int j) {
-		return subclassTableKeyColumnClosure[j];
-	}
-
-	public String getSubclassTableName(int j) {
-		return subclassTableNameClosure[j];
-	}
-
-	public int getSubclassTableSpan() {
-		return subclassTableNameClosure.length;
-	}
-
-	protected boolean isSubclassTableLazy(int j) {
-		return subclassTableIsLazyClosure[j];
-	}
-	
-	
-	protected boolean isClassOrSuperclassTable(int j) {
-		return isClassOrSuperclassTable[j];
-	}
-
-	public String getPropertyTableName(String propertyName) {
-		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
-		if ( index == null ) {
-			return null;
-		}
-		return tableNames[ propertyTableNumbers[ index.intValue() ] ];
-	}
-
-	public String[] getConstraintOrderedTableNameClosure() {
-		return constraintOrderedTableNames;
-	}
-
-	public String[][] getContraintOrderedTableKeyColumnClosure() {
-		return constraintOrderedKeyColumnNames;
-	}
-
-	public String getRootTableName() {
-		return naturalOrderTableNames[0];
-	}
-
-	public String getRootTableAlias(String drivingAlias) {
-		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
-	}
-
-	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
-		if ( "class".equals( propertyPath ) ) {
-			// special case where we need to force incloude all subclass joins
-			return Declarer.SUBCLASS;
-		}
-		return super.getSubclassPropertyDeclarer( propertyPath );
-	}
-}
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.Map;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.HibernateException;
+import org.hibernate.MappingException;
+import org.hibernate.QueryException;
+import org.hibernate.cache.access.EntityRegionAccessStrategy;
+import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.Mapping;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.Versioning;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.Join;
+import org.hibernate.mapping.KeyValue;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.mapping.Selectable;
+import org.hibernate.mapping.Subclass;
+import org.hibernate.mapping.Table;
+import org.hibernate.sql.CaseFragment;
+import org.hibernate.sql.SelectFragment;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
+ * mapping strategy
+ *
+ * @author Gavin King
+ */
+public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
+
+	// the class hierarchy structure
+	private final int tableSpan;
+	private final String[] tableNames;
+	private final String[] naturalOrderTableNames;
+	private final String[][] tableKeyColumns;
+	private final String[][] tableKeyColumnReaders;
+	private final String[][] tableKeyColumnReaderTemplates;
+	private final String[][] naturalOrderTableKeyColumns;
+	private final String[][] naturalOrderTableKeyColumnReaders;
+	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
+	private final boolean[] naturalOrderCascadeDeleteEnabled;
+
+	private final String[] spaces;
+
+	private final String[] subclassClosure;
+
+	private final String[] subclassTableNameClosure;
+	private final String[][] subclassTableKeyColumnClosure;
+	private final boolean[] isClassOrSuperclassTable;
+
+	// properties of this class, including inherited properties
+	private final int[] naturalOrderPropertyTableNumbers;
+	private final int[] propertyTableNumbers;
+
+	// the closure of all properties in the entire hierarchy including
+	// subclasses and superclasses of this class
+	private final int[] subclassPropertyTableNumberClosure;
+
+	// the closure of all columns used by the entire hierarchy including
+	// subclasses and superclasses of this class
+	private final int[] subclassColumnTableNumberClosure;
+	private final int[] subclassFormulaTableNumberClosure;
+
+	private final boolean[] subclassTableSequentialSelect;
+	private final boolean[] subclassTableIsLazyClosure;
+	
+	// subclass discrimination works by assigning particular
+	// values to certain combinations of null primary key
+	// values in the outer join using an SQL CASE
+	private final Map subclassesByDiscriminatorValue = new HashMap();
+	private final String[] discriminatorValues;
+	private final String[] notNullColumnNames;
+	private final int[] notNullColumnTableNumbers;
+
+	private final String[] constraintOrderedTableNames;
+	private final String[][] constraintOrderedKeyColumnNames;
+
+	private final String discriminatorSQLString;
+
+	//INITIALIZATION:
+
+	public JoinedSubclassEntityPersister(
+			final PersistentClass persistentClass,
+			final EntityRegionAccessStrategy cacheAccessStrategy,
+			final SessionFactoryImplementor factory,
+			final Mapping mapping) throws HibernateException {
+
+		super( persistentClass, cacheAccessStrategy, factory );
+
+		// DISCRIMINATOR
+
+		final Object discriminatorValue;
+		if ( persistentClass.isPolymorphic() ) {
+			try {
+				discriminatorValue = new Integer( persistentClass.getSubclassId() );
+				discriminatorSQLString = discriminatorValue.toString();
+			}
+			catch (Exception e) {
+				throw new MappingException("Could not format discriminator value to SQL string", e );
+			}
+		}
+		else {
+			discriminatorValue = null;
+			discriminatorSQLString = null;
+		}
+
+		if ( optimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION ) {
+			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
+		}
+
+		//MULTITABLES
+
+		final int idColumnSpan = getIdentifierColumnSpan();
+
+		ArrayList tables = new ArrayList();
+		ArrayList keyColumns = new ArrayList();
+		ArrayList keyColumnReaders = new ArrayList();
+		ArrayList keyColumnReaderTemplates = new ArrayList();
+		ArrayList cascadeDeletes = new ArrayList();
+		Iterator titer = persistentClass.getTableClosureIterator();
+		Iterator kiter = persistentClass.getKeyClosureIterator();
+		while ( titer.hasNext() ) {
+			Table tab = (Table) titer.next();
+			KeyValue key = (KeyValue) kiter.next();
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			tables.add(tabname);
+			String[] keyCols = new String[idColumnSpan];
+			String[] keyColReaders = new String[idColumnSpan];
+			String[] keyColReaderTemplates = new String[idColumnSpan];
+			Iterator citer = key.getColumnIterator();
+			for ( int k=0; k<idColumnSpan; k++ ) {
+				Column column = (Column) citer.next();
+				keyCols[k] = column.getQuotedName( factory.getDialect() );
+				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
+				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
+			}
+			keyColumns.add(keyCols);
+			keyColumnReaders.add(keyColReaders);
+			keyColumnReaderTemplates.add(keyColReaderTemplates);
+			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
+		}
+		
+		//Span of the tables directly mapped by this entity and super-classes, if any
+		int coreTableSpan = tables.size();
+		
+		Iterator joinIter = persistentClass.getJoinClosureIterator();
+		while ( joinIter.hasNext() ) {
+			Join join = (Join) joinIter.next();
+			
+			Table tab = join.getTable();
+			 
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			tables.add(tabname);
+			
+			KeyValue key = join.getKey();
+			int joinIdColumnSpan = 	key.getColumnSpan();		
+			
+			String[] keyCols = new String[joinIdColumnSpan];
+			String[] keyColReaders = new String[joinIdColumnSpan];
+			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
+						
+			Iterator citer = key.getColumnIterator();
+			
+			for ( int k=0; k<joinIdColumnSpan; k++ ) {
+				Column column = (Column) citer.next();
+				keyCols[k] = column.getQuotedName( factory.getDialect() );
+				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
+				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
+			}
+			keyColumns.add(keyCols);
+			keyColumnReaders.add(keyColReaders);
+			keyColumnReaderTemplates.add(keyColReaderTemplates);
+			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
+		}
+		
+		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
+		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray(keyColumns);
+		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray(keyColumnReaders);
+		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray(keyColumnReaderTemplates);
+		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray(cascadeDeletes);
+
+		ArrayList subtables = new ArrayList();
+		ArrayList isConcretes = new ArrayList();
+		ArrayList isDeferreds = new ArrayList();
+		ArrayList isLazies = new ArrayList();
+		
+		keyColumns = new ArrayList();
+		titer = persistentClass.getSubclassTableClosureIterator();
+		while ( titer.hasNext() ) {
+			Table tab = (Table) titer.next();
+			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
+			isDeferreds.add(Boolean.FALSE);
+			isLazies.add(Boolean.FALSE);
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			subtables.add(tabname);
+			String[] key = new String[idColumnSpan];
+			Iterator citer = tab.getPrimaryKey().getColumnIterator();
+			for ( int k=0; k<idColumnSpan; k++ ) {
+				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
+			}
+			keyColumns.add(key);
+		}
+		
+		//Add joins
+		joinIter = persistentClass.getSubclassJoinClosureIterator();
+		while ( joinIter.hasNext() ) {
+			Join join = (Join) joinIter.next();
+			
+			Table tab = join.getTable();
+			 
+			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
+			isDeferreds.add( new Boolean( join.isSequentialSelect() ) );
+			isLazies.add(new Boolean(join.isLazy()));
+			
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			subtables.add(tabname);
+			String[] key = new String[idColumnSpan];
+			Iterator citer = tab.getPrimaryKey().getColumnIterator();
+			for ( int k=0; k<idColumnSpan; k++ ) {
+				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
+			}
+			keyColumns.add(key);
+						
+		}
+				
+		String [] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray(subtables);
+		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(keyColumns);
+		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
+		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
+		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
+		
+		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
+		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
+		int currentPosition = 0;
+		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0 ; i--, currentPosition++ ) {
+			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
+			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
+		} 
+
+		/**
+		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
+		 * For the Client entity:
+		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
+		 * added to the meta-data when the annotated entities are processed.
+		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
+		 * the first table as it will the driving table.
+		 * tableNames -> CLIENT, PERSON
+		 */
+				
+		tableSpan = naturalOrderTableNames.length;
+ 		tableNames = reverse(naturalOrderTableNames, coreTableSpan);
+		tableKeyColumns = reverse(naturalOrderTableKeyColumns, coreTableSpan);
+		tableKeyColumnReaders = reverse(naturalOrderTableKeyColumnReaders, coreTableSpan);
+		tableKeyColumnReaderTemplates = reverse(naturalOrderTableKeyColumnReaderTemplates, coreTableSpan);
+		subclassTableNameClosure = reverse(naturalOrderSubclassTableNameClosure, coreTableSpan);
+		subclassTableKeyColumnClosure = reverse(naturalOrderSubclassTableKeyColumnClosure, coreTableSpan);
+ 
+		spaces = ArrayHelper.join(
+				tableNames,
+				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
+		);
+
+		// Custom sql
+		customSQLInsert = new String[tableSpan];
+		customSQLUpdate = new String[tableSpan];
+		customSQLDelete = new String[tableSpan];
+		insertCallable = new boolean[tableSpan];
+		updateCallable = new boolean[tableSpan];
+		deleteCallable = new boolean[tableSpan];
+		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
+		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
+		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
+
+		PersistentClass pc = persistentClass;
+		int jk = coreTableSpan-1;
+		while (pc!=null) {
+			customSQLInsert[jk] = pc.getCustomSQLInsert();
+			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
+			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[jk], insertCallable[jk] )
+		                                  : pc.getCustomSQLInsertCheckStyle();
+			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
+			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
+			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
+		                                  : pc.getCustomSQLUpdateCheckStyle();
+			customSQLDelete[jk] = pc.getCustomSQLDelete();
+			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
+			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
+		                                  : pc.getCustomSQLDeleteCheckStyle();
+			jk--;
+			pc = pc.getSuperclass();
+		}
+		
+		if ( jk != -1 ) {
+			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
+		}
+ 
+		joinIter = persistentClass.getJoinClosureIterator();
+		int j = coreTableSpan;
+		while ( joinIter.hasNext() ) {
+			Join join = (Join) joinIter.next();
+			
+			customSQLInsert[j] = join.getCustomSQLInsert();
+			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
+			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
+		                                  : join.getCustomSQLInsertCheckStyle();
+			customSQLUpdate[j] = join.getCustomSQLUpdate();
+			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
+			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
+		                                  : join.getCustomSQLUpdateCheckStyle();
+			customSQLDelete[j] = join.getCustomSQLDelete();
+			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
+			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
+		                                  : join.getCustomSQLDeleteCheckStyle();
+			j++;
+		}
+		
+		// PROPERTIES
+		int hydrateSpan = getPropertySpan();
+		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
+		propertyTableNumbers = new int[hydrateSpan];
+		Iterator iter = persistentClass.getPropertyClosureIterator();
+		int i=0;
+		while( iter.hasNext() ) {
+			Property prop = (Property) iter.next();
+			String tabname = prop.getValue().getTable().getQualifiedName(
+				factory.getDialect(),
+				factory.getSettings().getDefaultCatalogName(),
+				factory.getSettings().getDefaultSchemaName()
+			);
+			propertyTableNumbers[i] = getTableId(tabname, tableNames);
+			naturalOrderPropertyTableNumbers[i] = getTableId(tabname, naturalOrderTableNames);
+			i++;
+		}
+
+		// subclass closure properties
+
+		//TODO: code duplication with SingleTableEntityPersister
+
+		ArrayList columnTableNumbers = new ArrayList();
+		ArrayList formulaTableNumbers = new ArrayList();
+		ArrayList propTableNumbers = new ArrayList();
+
+		iter = persistentClass.getSubclassPropertyClosureIterator();
+		while ( iter.hasNext() ) {
+			Property prop = (Property) iter.next();
+			Table tab = prop.getValue().getTable();
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			Integer tabnum = new Integer( getTableId(tabname, subclassTableNameClosure) );
+  			propTableNumbers.add(tabnum);
+
+			Iterator citer = prop.getColumnIterator();
+			while ( citer.hasNext() ) {
+				Selectable thing = (Selectable) citer.next();
+				if ( thing.isFormula() ) {
+					formulaTableNumbers.add(tabnum);
+				}
+				else {
+					columnTableNumbers.add(tabnum);
+				}
+			}
+
+		}
+
+		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnTableNumbers);
+		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTableNumbers);
+		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaTableNumbers);
+
+		// SUBCLASSES
+ 
+		int subclassSpan = persistentClass.getSubclassSpan() + 1;
+		subclassClosure = new String[subclassSpan];
+		subclassClosure[subclassSpan-1] = getEntityName();
+		if ( persistentClass.isPolymorphic() ) {
+			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
+			discriminatorValues = new String[subclassSpan];
+			discriminatorValues[subclassSpan-1] = discriminatorSQLString;
+			notNullColumnTableNumbers = new int[subclassSpan];
+			final int id = getTableId(
+				persistentClass.getTable().getQualifiedName(
+						factory.getDialect(),
+						factory.getSettings().getDefaultCatalogName(),
+						factory.getSettings().getDefaultSchemaName()
+				),
+				subclassTableNameClosure
+			);
+			notNullColumnTableNumbers[subclassSpan-1] = id;
+			notNullColumnNames = new String[subclassSpan];
+			notNullColumnNames[subclassSpan-1] =  subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
+		}
+		else {
+			discriminatorValues = null;
+			notNullColumnTableNumbers = null;
+			notNullColumnNames = null;
+		}
+
+		iter = persistentClass.getSubclassIterator();
+		int k=0;
+		while ( iter.hasNext() ) {
+			Subclass sc = (Subclass) iter.next();
+			subclassClosure[k] = sc.getEntityName();
+			try {
+				if ( persistentClass.isPolymorphic() ) {
+					// we now use subclass ids that are consistent across all
+					// persisters for a class hierarchy, so that the use of
+					// "foo.class = Bar" works in HQL
+					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
+					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
+					discriminatorValues[k] = subclassId.toString();
+					int id = getTableId(
+						sc.getTable().getQualifiedName(
+								factory.getDialect(),
+								factory.getSettings().getDefaultCatalogName(),
+								factory.getSettings().getDefaultSchemaName()
+						),
+						subclassTableNameClosure
+					);
+					notNullColumnTableNumbers[k] = id;
+					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
+				}
+			}
+			catch (Exception e) {
+				throw new MappingException("Error parsing discriminator value", e );
+			}
+			k++;
+		}
+
+		initLockers();
+
+		initSubclassPropertyAliasesMap(persistentClass);
+
+		postConstruct(mapping);
+
+	}
+
+	protected boolean isSubclassTableSequentialSelect(int j) {
+		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
+	}
+	
+	
+	/*public void postInstantiate() throws MappingException {
+		super.postInstantiate();
+		//TODO: other lock modes?
+		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
+	}*/
+
+	public String getSubclassPropertyTableName(int i) {
+		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
+	}
+
+	public Type getDiscriminatorType() {
+		return StandardBasicTypes.INTEGER;
+	}
+
+	public String getDiscriminatorSQLValue() {
+		return discriminatorSQLString;
+	}
+
+
+	public String getSubclassForDiscriminatorValue(Object value) {
+		return (String) subclassesByDiscriminatorValue.get(value);
+	}
+
+	public Serializable[] getPropertySpaces() {
+		return spaces; // don't need subclass tables, because they can't appear in conditions
+	}
+
+
+	protected String getTableName(int j) {
+		return naturalOrderTableNames[j];
+	}
+
+	protected String[] getKeyColumns(int j) {
+		return naturalOrderTableKeyColumns[j];
+	}
+
+	protected boolean isTableCascadeDeleteEnabled(int j) {
+		return naturalOrderCascadeDeleteEnabled[j];
+	}
+
+	protected boolean isPropertyOfTable(int property, int j) {
+		return naturalOrderPropertyTableNumbers[property]==j;
+	}
+
+	/**
+	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
+	 * depending upon the value of the <tt>lock</tt> parameter
+	 */
+	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
+	throws HibernateException {
+
+		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
+
+		final UniqueEntityLoader loader = hasQueryLoader() ?
+				getQueryLoader() :
+				this.loader;
+		try {
+
+			final Object result = loader.load(id, optionalObject, session);
+
+			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
+
+			return result;
+
+		}
+		catch (SQLException sqle) {
+			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
+		}
+	}*/
+
+	private static final void reverse(Object[] objects, int len) {
+		Object[] temp = new Object[len];
+		for (int i=0; i<len; i++) {
+			temp[i] = objects[len-i-1];
+		}
+		for (int i=0; i<len; i++) {
+			objects[i] = temp[i];
+		}
+	}
+
+	
+	/**
+	 * Reverse the first n elements of the incoming array
+	 * @param objects
+	 * @param n
+	 * @return New array with the first n elements in reversed order 
+	 */
+	private static final String[] reverse(String [] objects, int n) {
+		
+		int size = objects.length;
+		String[] temp = new String[size];
+		
+		for (int i=0; i<n; i++) {
+			temp[i] = objects[n-i-1];
+		}
+		
+		for (int i=n; i < size; i++) {
+			temp[i] =  objects[i];
+		}
+		
+		return temp;
+	}
+		
+	/**
+	 * Reverse the first n elements of the incoming array
+	 * @param objects
+	 * @param n
+	 * @return New array with the first n elements in reversed order 
+	 */
+	private static final String[][] reverse(String[][] objects, int n) {
+		int size = objects.length;
+		String[][] temp = new String[size][];
+		for (int i=0; i<n; i++) {
+			temp[i] = objects[n-i-1];
+		}
+		
+		for (int i=n; i<size; i++) {
+			temp[i] = objects[i];
+		}
+		
+		return temp;
+	}
+	
+	
+	
+	public String fromTableFragment(String alias) {
+		return getTableName() + ' ' + alias;
+	}
+
+	public String getTableName() {
+		return tableNames[0];
+	}
+
+	private static int getTableId(String tableName, String[] tables) {
+		for ( int j=0; j<tables.length; j++ ) {
+			if ( tableName.equals( tables[j] ) ) {
+				return j;
+			}
+		}
+		throw new AssertionFailure("Table " + tableName + " not found");
+	}
+
+	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
+		if ( hasSubclasses() ) {
+			select.setExtraSelectList( discriminatorFragment(name), getDiscriminatorAlias() );
+		}
+	}
+
+	private CaseFragment discriminatorFragment(String alias) {
+		CaseFragment cases = getFactory().getDialect().createCaseFragment();
+
+		for ( int i=0; i<discriminatorValues.length; i++ ) {
+			cases.addWhenColumnNotNull(
+				generateTableAlias( alias, notNullColumnTableNumbers[i] ),
+				notNullColumnNames[i],
+				discriminatorValues[i]
+			);
+		}
+
+		return cases;
+	}
+
+	public String filterFragment(String alias) {
+		return hasWhere() ?
+			" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
+			"";
+	}
+
+	public String generateFilterConditionAlias(String rootAlias) {
+		return generateTableAlias( rootAlias, tableSpan-1 );
+	}
+
+	public String[] getIdentifierColumnNames() {
+		return tableKeyColumns[0];
+	}
+
+	public String[] getIdentifierColumnReaderTemplates() {
+		return tableKeyColumnReaderTemplates[0];
+	}
+
+	public String[] getIdentifierColumnReaders() {
+		return tableKeyColumnReaders[0];
+	}		
+	
+	public String[] toColumns(String alias, String propertyName) throws QueryException {
+
+		if ( ENTITY_CLASS.equals(propertyName) ) {
+			// This doesn't actually seem to work but it *might*
+			// work on some dbs. Also it doesn't work if there
+			// are multiple columns of results because it
+			// is not accounting for the suffix:
+			// return new String[] { getDiscriminatorColumnName() };
+
+			return new String[] { discriminatorFragment(alias).toFragmentString() };
+		}
+		else {
+			return super.toColumns(alias, propertyName);
+		}
+
+	}
+
+	protected int[] getPropertyTableNumbersInSelect() {
+		return propertyTableNumbers;
+	}
+
+	protected int getSubclassPropertyTableNumber(int i) {
+		return subclassPropertyTableNumberClosure[i];
+	}
+
+	public int getTableSpan() {
+		return tableSpan;
+	}
+
+	public boolean isMultiTable() {
+		return true;
+	}
+
+	protected int[] getSubclassColumnTableNumberClosure() {
+		return subclassColumnTableNumberClosure;
+	}
+
+	protected int[] getSubclassFormulaTableNumberClosure() {
+		return subclassFormulaTableNumberClosure;
+	}
+
+	protected int[] getPropertyTableNumbers() {
+		return naturalOrderPropertyTableNumbers;
+	}
+
+	protected String[] getSubclassTableKeyColumns(int j) {
+		return subclassTableKeyColumnClosure[j];
+	}
+
+	public String getSubclassTableName(int j) {
+		return subclassTableNameClosure[j];
+	}
+
+	public int getSubclassTableSpan() {
+		return subclassTableNameClosure.length;
+	}
+
+	protected boolean isSubclassTableLazy(int j) {
+		return subclassTableIsLazyClosure[j];
+	}
+	
+	
+	protected boolean isClassOrSuperclassTable(int j) {
+		return isClassOrSuperclassTable[j];
+	}
+
+	public String getPropertyTableName(String propertyName) {
+		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
+		if ( index == null ) {
+			return null;
+		}
+		return tableNames[ propertyTableNumbers[ index.intValue() ] ];
+	}
+
+	public String[] getConstraintOrderedTableNameClosure() {
+		return constraintOrderedTableNames;
+	}
+
+	public String[][] getContraintOrderedTableKeyColumnClosure() {
+		return constraintOrderedKeyColumnNames;
+	}
+
+	public String getRootTableName() {
+		return naturalOrderTableNames[0];
+	}
+
+	public String getRootTableAlias(String drivingAlias) {
+		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
+	}
+
+	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
+		if ( "class".equals( propertyPath ) ) {
+			// special case where we need to force incloude all subclass joins
+			return Declarer.SUBCLASS;
+		}
+		return super.getSubclassPropertyDeclarer( propertyPath );
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
+
+
-import org.hibernate.Hibernate;
+import org.hibernate.type.StandardBasicTypes;
-		return Hibernate.INTEGER;
+		return StandardBasicTypes.INTEGER;
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+
+
+
-import org.hibernate.Hibernate;
-		return value==null ?
-				"null" :
-				Hibernate.entity( HibernateProxyHelper.getClassWithoutInitializingProxy(value) )
-						.toLoggableString(value, factory);
+		return value == null
+				? "null"
+				: factory.getTypeHelper()
+						.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
+						.toLoggableString( value, factory );
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
-            if (this.factory != null) LOG.scopingTypesToSessionFactoryAfterAlreadyScoped(this.factory, factory);
-            else LOG.trace("Scoping types to session factory " + factory);
+            if (this.factory != null) {
+				LOG.scopingTypesToSessionFactoryAfterAlreadyScoped( this.factory, factory );
+			}
+            else {
+				LOG.trace( "Scoping types to session factory " + factory );
+			}
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
-import org.hibernate.Hibernate;
+import org.hibernate.type.StandardBasicTypes;
-		generator.configure( Hibernate.LONG, properties, dialect );
+		generator.configure( StandardBasicTypes.LONG, properties, dialect );
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
-import org.hibernate.Hibernate;
+import org.hibernate.type.StandardBasicTypes;
-		generator.configure( Hibernate.LONG, properties, dialect );
+		generator.configure( StandardBasicTypes.LONG, properties, dialect );
--- a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
-import org.hibernate.Hibernate;
+import org.hibernate.type.StandardBasicTypes;
-		generator.configure( Hibernate.LONG, properties, dialect );
+		generator.configure( StandardBasicTypes.LONG, properties, dialect );
--- a/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.id.enhanced;
-
-import java.util.Properties;
-
-import org.hibernate.Hibernate;
-import org.hibernate.MappingException;
-import org.hibernate.cfg.Environment;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.cfg.ObjectNameNormalizer;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.id.PersistentIdentifierGenerator;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.fail;
-
-/**
- * Tests that SequenceStyleGenerator configures itself as expected in various scenarios
- *
- * @author Steve Ebersole
- */
-@SuppressWarnings({ "deprecation" })
-public class SequenceStyleConfigUnitTest extends BaseUnitTestCase {
-	private void assertClassAssignability(Class expected, Class actual) {
-		if ( ! expected.isAssignableFrom( actual ) ) {
-			fail( "Actual type [" + actual.getName() + "] is not assignable to expected type [" + expected.getName() + "]" );
-		}
-	}
-
-
-	/**
-	 * Test all params defaulted with a dialect supporting sequences
-	 */
-	@Test
-	public void testDefaultedSequenceBackedConfiguration() {
-		Dialect dialect = new SequenceDialect();
-		Properties props = buildGeneratorPropertiesBase();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	private Properties buildGeneratorPropertiesBase() {
-		Properties props = new Properties();
-		props.put(
-				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
-				new ObjectNameNormalizer() {
-					protected boolean isUseQuotedIdentifiersGlobally() {
-						return false;
-					}
-
-					protected NamingStrategy getNamingStrategy() {
-						return null;
-					}
-				}
-		);
-		return props;
-	}
-
-	/**
-	 * Test all params defaulted with a dialect which does not support sequences
-	 */
-	@Test
-	public void testDefaultedTableBackedConfiguration() {
-		Dialect dialect = new TableDialect();
-		Properties props = buildGeneratorPropertiesBase();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test default optimizer selection for sequence backed generators
-	 * based on the configured increment size; both in the case of the
-	 * dialect supporting pooled sequences (pooled) and not (hilo)
-	 */
-	@Test
-	public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
-
-		// for dialects which do not support pooled sequences, we default to pooled+table
-		Dialect dialect = new SequenceDialect();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-
-		// for dialects which do support pooled sequences, we default to pooled+sequence
-		dialect = new PooledSequenceDialect();
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test default optimizer selection for table backed generators
-	 * based on the configured increment size.  Here we always prefer
-	 * pooled.
-	 */
-	@Test
-	public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
-		Dialect dialect = new TableDialect();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test forcing of table as backing strucuture with dialect supporting sequences
-	 */
-	@Test
-	public void testForceTableUse() {
-		Dialect dialect = new SequenceDialect();
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.FORCE_TBL_PARAM, "true" );
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test explicitly specifying both optimizer and increment
-	 */
-	@Test
-	public void testExplicitOptimizerWithExplicitIncrementSize() {
-		// with sequence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		final Dialect dialect = new SequenceDialect();
-
-		// optimizer=none w/ increment > 1 => should honor optimizer
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.NONE );
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( 1, generator.getOptimizer().getIncrementSize() );
-		assertEquals( 1, generator.getDatabaseStructure().getIncrementSize() );
-
-		// optimizer=hilo w/ increment > 1 => hilo
-		props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.HILO );
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.HiLoOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
-		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
-
-		// optimizer=pooled w/ increment > 1 => hilo
-		props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.POOL );
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		// because the dialect reports to not support pooled seqyences, the expectation is that we will
-		// use a table for the backing structure...
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
-		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
-	}
-
-	@Test
-	public void testPreferPooledLoSettingHonored() {
-		final Dialect dialect = new PooledSequenceDialect();
-
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-
-		props.setProperty( Environment.PREFER_POOLED_VALUES_LO, "true" );
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledLoOptimizer.class, generator.getOptimizer().getClass() );
-	}
-
-	private static class TableDialect extends Dialect {
-		public boolean supportsSequences() {
-			return false;
-		}
-	}
-
-	private static class SequenceDialect extends Dialect {
-		public boolean supportsSequences() {
-			return true;
-		}
-		public boolean supportsPooledSequences() {
-			return false;
-		}
-		public String getSequenceNextValString(String sequenceName) throws MappingException {
-			return "";
-		}
-	}
-
-	private static class PooledSequenceDialect extends SequenceDialect {
-		public boolean supportsPooledSequences() {
-			return true;
-		}
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.id.enhanced;
+
+import java.util.Properties;
+
+import org.hibernate.Hibernate;
+import org.hibernate.MappingException;
+import org.hibernate.cfg.Environment;
+import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.id.PersistentIdentifierGenerator;
+import org.hibernate.type.StandardBasicTypes;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.fail;
+
+/**
+ * Tests that SequenceStyleGenerator configures itself as expected in various scenarios
+ *
+ * @author Steve Ebersole
+ */
+@SuppressWarnings({ "deprecation" })
+public class SequenceStyleConfigUnitTest extends BaseUnitTestCase {
+	private void assertClassAssignability(Class expected, Class actual) {
+		if ( ! expected.isAssignableFrom( actual ) ) {
+			fail( "Actual type [" + actual.getName() + "] is not assignable to expected type [" + expected.getName() + "]" );
+		}
+	}
+
+
+	/**
+	 * Test all params defaulted with a dialect supporting sequences
+	 */
+	@Test
+	public void testDefaultedSequenceBackedConfiguration() {
+		Dialect dialect = new SequenceDialect();
+		Properties props = buildGeneratorPropertiesBase();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	private Properties buildGeneratorPropertiesBase() {
+		Properties props = new Properties();
+		props.put(
+				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
+				new ObjectNameNormalizer() {
+					protected boolean isUseQuotedIdentifiersGlobally() {
+						return false;
+					}
+
+					protected NamingStrategy getNamingStrategy() {
+						return null;
+					}
+				}
+		);
+		return props;
+	}
+
+	/**
+	 * Test all params defaulted with a dialect which does not support sequences
+	 */
+	@Test
+	public void testDefaultedTableBackedConfiguration() {
+		Dialect dialect = new TableDialect();
+		Properties props = buildGeneratorPropertiesBase();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test default optimizer selection for sequence backed generators
+	 * based on the configured increment size; both in the case of the
+	 * dialect supporting pooled sequences (pooled) and not (hilo)
+	 */
+	@Test
+	public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
+
+		// for dialects which do not support pooled sequences, we default to pooled+table
+		Dialect dialect = new SequenceDialect();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+
+		// for dialects which do support pooled sequences, we default to pooled+sequence
+		dialect = new PooledSequenceDialect();
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test default optimizer selection for table backed generators
+	 * based on the configured increment size.  Here we always prefer
+	 * pooled.
+	 */
+	@Test
+	public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
+		Dialect dialect = new TableDialect();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test forcing of table as backing strucuture with dialect supporting sequences
+	 */
+	@Test
+	public void testForceTableUse() {
+		Dialect dialect = new SequenceDialect();
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.FORCE_TBL_PARAM, "true" );
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test explicitly specifying both optimizer and increment
+	 */
+	@Test
+	public void testExplicitOptimizerWithExplicitIncrementSize() {
+		// with sequence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		final Dialect dialect = new SequenceDialect();
+
+		// optimizer=none w/ increment > 1 => should honor optimizer
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.NONE );
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( 1, generator.getOptimizer().getIncrementSize() );
+		assertEquals( 1, generator.getDatabaseStructure().getIncrementSize() );
+
+		// optimizer=hilo w/ increment > 1 => hilo
+		props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.HILO );
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.HiLoOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
+		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
+
+		// optimizer=pooled w/ increment > 1 => hilo
+		props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.POOL );
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		// because the dialect reports to not support pooled seqyences, the expectation is that we will
+		// use a table for the backing structure...
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
+		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
+	}
+
+	@Test
+	public void testPreferPooledLoSettingHonored() {
+		final Dialect dialect = new PooledSequenceDialect();
+
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+
+		props.setProperty( Environment.PREFER_POOLED_VALUES_LO, "true" );
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledLoOptimizer.class, generator.getOptimizer().getClass() );
+	}
+
+	private static class TableDialect extends Dialect {
+		public boolean supportsSequences() {
+			return false;
+		}
+	}
+
+	private static class SequenceDialect extends Dialect {
+		public boolean supportsSequences() {
+			return true;
+		}
+		public boolean supportsPooledSequences() {
+			return false;
+		}
+		public String getSequenceNextValString(String sequenceName) throws MappingException {
+			return "";
+		}
+	}
+
+	private static class PooledSequenceDialect extends SequenceDialect {
+		public boolean supportsPooledSequences() {
+			return true;
+		}
+	}
+}
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
+import org.hibernate.type.StandardBasicTypes;
-		q.setParameter( "departureDate", airFrance.getDepartureDate(), Hibernate.DATE );
+		q.setParameter( "departureDate", airFrance.getDepartureDate(), StandardBasicTypes.DATE );
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-		return new Type[]{Hibernate.BIG_DECIMAL, Hibernate.CURRENCY};
+		return new Type[]{ StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.CURRENCY };
-		return property == 0 ? (Object) ma.getAmount() : (Object) ma.getCurrency();
+		return property == 0 ? ma.getAmount() : ma.getCurrency();
-		BigDecimal amt = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet( rs, names[0], session);
-		Currency cur = (Currency) Hibernate.CURRENCY.nullSafeGet( rs, names[1], session );
+		BigDecimal amt = StandardBasicTypes.BIG_DECIMAL.nullSafeGet( rs, names[0], session);
+		Currency cur = StandardBasicTypes.CURRENCY.nullSafeGet( rs, names[1], session );
-		Hibernate.BIG_DECIMAL.nullSafeSet( st, amt, index, session );
-		Hibernate.CURRENCY.nullSafeSet( st, cur, index + 1, session );
+		StandardBasicTypes.BIG_DECIMAL.nullSafeSet( st, amt, index, session );
+		StandardBasicTypes.CURRENCY.nullSafeSet( st, cur, index + 1, session );
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
-//$Id$
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-	public static final String[] PROPERTY_NAMES = new String[]{"high", "middle", "low", "other"};
-	public static final Type[] TYPES = new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER};
-
+	public static final String[] PROPERTY_NAMES = new String[]{
+			"high", "middle", "low", "other"
+	};
+	public static final Type[] TYPES = new Type[]{
+			StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER
+	};
-		Integer highval = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[0], aSessionImplementor );
-		Integer midval = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[1], aSessionImplementor );
-		Integer lowval = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[2], aSessionImplementor );
-		Integer other = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[3], aSessionImplementor );
+		Integer highval = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[0], aSessionImplementor );
+		Integer midval = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[1], aSessionImplementor );
+		Integer lowval = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[2], aSessionImplementor );
+		Integer other = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[3], aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getHigh(), index, aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getMiddle(), index + 1, aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getLow(), index + 2, aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getOther(), index + 3, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getHigh(), index, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getMiddle(), index + 1, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getLow(), index + 2, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getOther(), index + 3, aSessionImplementor );
--- a/hibernate-core/src/test/java/org/hibernate/test/component/basic/ComponentTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/component/basic/ComponentTest.java
+import org.hibernate.type.StandardBasicTypes;
-			f.setFormula( yearFunction.render( Hibernate.INTEGER, args, null ) );
+			f.setFormula( yearFunction.render( StandardBasicTypes.INTEGER, args, null ) );
--- a/hibernate-core/src/test/java/org/hibernate/test/compositeelement/CompositeElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/compositeelement/CompositeElementTest.java
+import org.hibernate.type.StandardBasicTypes;
-			f.setFormula( lengthFunction.render( Hibernate.INTEGER, args, null ) );
+			f.setFormula( lengthFunction.render( StandardBasicTypes.INTEGER, args, null ) );
--- a/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
+import org.hibernate.type.StandardBasicTypes;
-					new Type[] { Hibernate.INTEGER, Hibernate.INTEGER }
+					new Type[] { StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER }
-					new Type[] { Hibernate.INTEGER, Hibernate.INTEGER }
+					new Type[] { StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER }
--- a/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
-//$Id: MonetoryAmountUserType.java 6235 2005-03-29 03:17:49Z oneovthafew $
+
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
-		return new Type[] { Hibernate.BIG_DECIMAL, Hibernate.CURRENCY };
+		return new Type[] { StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.CURRENCY };
-		return property==0 ? (Object) ma.getAmount() : (Object) ma.getCurrency();
+		return property==0 ? ma.getAmount() : ma.getCurrency();
-		BigDecimal amt = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet( rs, names[0], session );
-		Currency cur = (Currency) Hibernate.CURRENCY.nullSafeGet( rs, names[1], session );
+		BigDecimal amt = StandardBasicTypes.BIG_DECIMAL.nullSafeGet( rs, names[0], session );
+		Currency cur = StandardBasicTypes.CURRENCY.nullSafeGet( rs, names[1], session );
-		Hibernate.BIG_DECIMAL.nullSafeSet(st, amt, index, session);
-		Hibernate.CURRENCY.nullSafeSet(st, cur, index+1, session);
+		StandardBasicTypes.BIG_DECIMAL.nullSafeSet(st, amt, index, session);
+		StandardBasicTypes.CURRENCY.nullSafeSet(st, cur, index+1, session);
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
-package org.hibernate.test.hql;
-
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import org.hibernate.Hibernate;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.type.IntegerType;
-import org.hibernate.usertype.EnhancedUserType;
-
-/**
- * A custom type for mapping {@link org.hibernate.test.hql.Classification} instances
- * to the respective db column.
- * </p>
- * THis is largely intended to mimic JDK5 enum support in JPA.  Here we are
- * using the approach of storing the ordinal values, rather than the names.
- *
- * @author Steve Ebersole
- */
-public class ClassificationType implements EnhancedUserType {
-
-	public int[] sqlTypes() {
-		return new int[] { Types.TINYINT };
-	}
-
-	public Class returnedClass() {
-		return Classification.class;
-	}
-
-	public boolean equals(Object x, Object y) throws HibernateException {
-		if ( x == null && y == null ) {
-			return false;
-		}
-		else if ( x != null ) {
-			return x.equals( y );
-		}
-		else {
-			return y.equals( x );
-		}
-	}
-
-	public int hashCode(Object x) throws HibernateException {
-		return x.hashCode();
-	}
-
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
-		Integer ordinal = IntegerType.INSTANCE.nullSafeGet( rs, names[0], session );
-		return Classification.valueOf( ordinal );
-	}
-
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
-		Integer ordinal = value == null ? null : new Integer( ( ( Classification ) value ).ordinal() );
-		Hibernate.INTEGER.nullSafeSet( st, ordinal, index, session );
-	}
-
-	public Object deepCopy(Object value) throws HibernateException {
-		return value;
-	}
-
-	public boolean isMutable() {
-		return false;
-	}
-
-	public Serializable disassemble(Object value) throws HibernateException {
-		return ( Classification ) value;
-	}
-
-	public Object assemble(Serializable cached, Object owner) throws HibernateException {
-		return cached;
-	}
-
-	public Object replace(Object original, Object target, Object owner) throws HibernateException {
-		return original;
-	}
-
-	public String objectToSQLString(Object value) {
-		return extractOrdinalString( value );
-	}
-
-	public String toXMLString(Object value) {
-		return extractName( value );
-	}
-
-	public Object fromXMLString(String xmlValue) {
-		return Classification.valueOf( xmlValue );
-	}
-
-	private String extractName(Object obj) {
-		return ( ( Classification ) obj ).name();
-	}
-
-	private int extractOrdinal(Object value) {
-		return ( ( Classification ) value ).ordinal();
-	}
-
-	private String extractOrdinalString(Object value) {
-		return Integer.toString( extractOrdinal( value ) );
-	}
-}
+package org.hibernate.test.hql;
+
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.usertype.EnhancedUserType;
+
+/**
+ * A custom type for mapping {@link org.hibernate.test.hql.Classification} instances
+ * to the respective db column.
+ * </p>
+ * THis is largely intended to mimic JDK5 enum support in JPA.  Here we are
+ * using the approach of storing the ordinal values, rather than the names.
+ *
+ * @author Steve Ebersole
+ */
+public class ClassificationType implements EnhancedUserType {
+
+	public int[] sqlTypes() {
+		return new int[] { Types.TINYINT };
+	}
+
+	public Class returnedClass() {
+		return Classification.class;
+	}
+
+	public boolean equals(Object x, Object y) throws HibernateException {
+		if ( x == null && y == null ) {
+			return false;
+		}
+		else if ( x != null ) {
+			return x.equals( y );
+		}
+		else {
+			return y.equals( x );
+		}
+	}
+
+	public int hashCode(Object x) throws HibernateException {
+		return x.hashCode();
+	}
+
+	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+		Integer ordinal = StandardBasicTypes.INTEGER.nullSafeGet( rs, names[0], session );
+		return Classification.valueOf( ordinal );
+	}
+
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+		Integer ordinal = value == null ? null : new Integer( ( ( Classification ) value ).ordinal() );
+		StandardBasicTypes.INTEGER.nullSafeSet( st, ordinal, index, session );
+	}
+
+	public Object deepCopy(Object value) throws HibernateException {
+		return value;
+	}
+
+	public boolean isMutable() {
+		return false;
+	}
+
+	public Serializable disassemble(Object value) throws HibernateException {
+		return ( Classification ) value;
+	}
+
+	public Object assemble(Serializable cached, Object owner) throws HibernateException {
+		return cached;
+	}
+
+	public Object replace(Object original, Object target, Object owner) throws HibernateException {
+		return original;
+	}
+
+	public String objectToSQLString(Object value) {
+		return extractOrdinalString( value );
+	}
+
+	public String toXMLString(Object value) {
+		return extractName( value );
+	}
+
+	public Object fromXMLString(String xmlValue) {
+		return Classification.valueOf( xmlValue );
+	}
+
+	private String extractName(Object obj) {
+		return ( ( Classification ) obj ).name();
+	}
+
+	private int extractOrdinal(Object value) {
+		return ( ( Classification ) value ).ordinal();
+	}
+
+	private String extractOrdinalString(Object value) {
+		return Integer.toString( extractOrdinal( value ) );
+	}
+}
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
-package org.hibernate.test.instrument.domain;
-
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.Arrays;
-import org.hibernate.Hibernate;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.usertype.UserType;
-
-/**
- * A simple byte[]-based custom type.
- */
-public class CustomBlobType implements UserType {
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object nullSafeGet(ResultSet rs, String names[], SessionImplementor session, Object owner) throws SQLException {
-		// cast just to make sure...
-		return Hibernate.BINARY.nullSafeGet( rs, names[0], session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void nullSafeSet(PreparedStatement ps, Object value, int index, SessionImplementor session) throws SQLException, HibernateException {
-		// cast just to make sure...
-		Hibernate.BINARY.nullSafeSet( ps, value, index, session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object deepCopy(Object value) {
-		byte result[] = null;
-
-		if ( value != null ) {
-			byte bytes[] = ( byte[] ) value;
-
-			result = new byte[bytes.length];
-			System.arraycopy( bytes, 0, result, 0, bytes.length );
-		}
-
-		return result;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isMutable() {
-		return true;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int[] sqlTypes() {
-		return new int[] { Types.VARBINARY };
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class returnedClass() {
-		return byte[].class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean equals(Object x, Object y) {
-		return Arrays.equals( ( byte[] ) x, ( byte[] ) y );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object assemble(Serializable arg0, Object arg1)
-			throws HibernateException {
-		return null;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Serializable disassemble(Object arg0)
-			throws HibernateException {
-		return null;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int hashCode(Object arg0)
-			throws HibernateException {
-		return 0;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object replace(Object arg0, Object arg1, Object arg2)
-			throws HibernateException {
-		return null;
-	}
+package org.hibernate.test.instrument.domain;
+
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.Arrays;
+import org.hibernate.Hibernate;
+import org.hibernate.HibernateException;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.usertype.UserType;
+
+/**
+ * A simple byte[]-based custom type.
+ */
+public class CustomBlobType implements UserType {
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object nullSafeGet(ResultSet rs, String names[], SessionImplementor session, Object owner) throws SQLException {
+		// cast just to make sure...
+		return StandardBasicTypes.BINARY.nullSafeGet( rs, names[0], session );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void nullSafeSet(PreparedStatement ps, Object value, int index, SessionImplementor session) throws SQLException, HibernateException {
+		// cast just to make sure...
+		StandardBasicTypes.BINARY.nullSafeSet( ps, value, index, session );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object deepCopy(Object value) {
+		byte result[] = null;
+
+		if ( value != null ) {
+			byte bytes[] = ( byte[] ) value;
+
+			result = new byte[bytes.length];
+			System.arraycopy( bytes, 0, result, 0, bytes.length );
+		}
+
+		return result;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isMutable() {
+		return true;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public int[] sqlTypes() {
+		return new int[] { Types.VARBINARY };
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class returnedClass() {
+		return byte[].class;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean equals(Object x, Object y) {
+		return Arrays.equals( ( byte[] ) x, ( byte[] ) y );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object assemble(Serializable arg0, Object arg1)
+			throws HibernateException {
+		return null;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Serializable disassemble(Object arg0)
+			throws HibernateException {
+		return null;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public int hashCode(Object arg0)
+			throws HibernateException {
+		return 0;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object replace(Object arg0, Object arg1, Object arg2)
+			throws HibernateException {
+		return null;
+	}
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+
-import org.hibernate.Hibernate;
-import org.hibernate.engine.EntityKey;
+import org.hibernate.type.StandardBasicTypes;
-	private static final Type[] TYPES = new Type[] { Hibernate.STRING };
+	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
-		return Hibernate.STRING;
+		return StandardBasicTypes.STRING;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+import org.hibernate.type.StandardBasicTypes;
-				.setParameter( 0, foo, Hibernate.entity(Foo.class) )
+				.setParameter( 0, foo, s.getTypeHelper().entity(Foo.class) )
-						.setParameter( 0, new Date(), Hibernate.DATE )
+						.setParameter( 0, new Date(), StandardBasicTypes.DATE )
-				.setParameter( 0, foo.getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
-				.setParameter( 0, foo.getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
-				.setParameter( 0, foo.getFoo().getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
-		s.createQuery( "from Foo foo where foo.foo = ?" ).setParameter( 0, foo.getFoo(), Hibernate.entity(Foo.class) ).list();
+		s.createQuery( "from Foo foo where foo.foo = ?" ).setParameter( 0, foo.getFoo(), s.getTypeHelper().entity(Foo.class) ).list();
-				.setParameter( 0, foo.getFoo().getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
-				.setParameter( 0, foo.getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
-				.setParameter( 0, new Boolean(true), Hibernate.BOOLEAN )
+				.setParameter( 0, new Boolean(true), StandardBasicTypes.BOOLEAN )
-				.setParameter( 0, new Date(), Hibernate.DATE )
+				.setParameter( 0, new Date(), StandardBasicTypes.DATE )
-				.setParameter( 0, bar, Hibernate.entity(Foo.class) )
-				.setParameter( 1, new Long(1234), Hibernate.LONG )
-				.setParameter( 2, new Integer(12), Hibernate.INTEGER )
-				.setParameter( 3, "id", Hibernate.STRING )
+				.setParameter( 0, bar, s.getTypeHelper().entity(Foo.class) )
+				.setParameter( 1, new Long(1234), StandardBasicTypes.LONG )
+				.setParameter( 2, new Integer(12), StandardBasicTypes.INTEGER )
+				.setParameter( 3, "id", StandardBasicTypes.STRING )
-				.setParameter( 0, bar, Hibernate.entity(Foo.class) )
-				.setParameter( 1, new Long(1234), Hibernate.LONG )
-				.setParameter( 2, "More Stuff", Hibernate.STRING )
+				.setParameter( 0, bar, s.getTypeHelper().entity(Foo.class) )
+				.setParameter( 1, new Long(1234), StandardBasicTypes.LONG )
+				.setParameter( 2, "More Stuff", StandardBasicTypes.STRING )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
-				.setParameter( 0, new java.sql.Date(123), Hibernate.DATE )
+				.setParameter( 0, new java.sql.Date(123), StandardBasicTypes.DATE )
-		assertEquals( 8, doDelete( s, "from Qux q where q.stuff=?", "foo", Hibernate.STRING ) );
+		assertEquals( 8, doDelete( s, "from Qux q where q.stuff=?", "foo", StandardBasicTypes.STRING ) );
-						0, im, Hibernate.entity( Immutable.class )
+						0, im, s.getTypeHelper().entity( Immutable.class )
-					.setParameter( 0, foo, Hibernate.entity( Foo.class ) )
+					.setParameter( 0, foo, s.getTypeHelper().entity( Foo.class ) )
-				.setParameter( 0, oid, Hibernate.LONG )
-				.setParameter( 1, new Character('O'), Hibernate.CHARACTER )
+				.setParameter( 0, oid, StandardBasicTypes.LONG )
+				.setParameter( 1, new Character('O'), StandardBasicTypes.CHARACTER )
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FumTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FumTest.java
+import org.hibernate.type.StandardBasicTypes;
-				.setParameter( 0, new Short(fiShort), Hibernate.SHORT )
+				.setParameter( 0, new Short(fiShort), StandardBasicTypes.SHORT )
-				.setParameter( 0, new Date(), Hibernate.DATE )
+				.setParameter( 0, new Date(), StandardBasicTypes.DATE )
-				.setParameter( 0, "fooid", Hibernate.STRING )
+				.setParameter( 0, "fooid", StandardBasicTypes.STRING )
-				.setParameter( 0, d.getId().getDetailId(), Hibernate.STRING )
+				.setParameter( 0, d.getId().getDetailId(), StandardBasicTypes.STRING )
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
+import org.hibernate.Session;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.type.ManyToOneType;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.TypeFactory;
-		IntegerType.INSTANCE.getSqlTypeDescriptor().getSqlType(), StringType.INSTANCE.getSqlTypeDescriptor().getSqlType()
+			IntegerType.INSTANCE.getSqlTypeDescriptor().getSqlType(),
+			StringType.INSTANCE.getSqlTypeDescriptor().getSqlType()
-		IntegerType.INSTANCE, Hibernate.entity(Glarch.class)
+			IntegerType.INSTANCE,
+			new ManyToOneType(
+					new TypeFactory.TypeScope() {
+						@Override
+						public SessionFactoryImplementor resolveFactory() {
+							// todo : can we tie this into org.hibernate.type.TypeFactory.TypeScopeImpl() somehow?
+							throw new HibernateException( "Cannot access SessionFactory from here" );
+						}
+					},
+					Glarch.class.getName()
+			)
-		GlarchProxy g = (GlarchProxy) Hibernate.entity(Glarch.class).nullSafeGet(rs, names[1], session, owner);
+		GlarchProxy g = (GlarchProxy) ( (Session) session ).getTypeHelper().entity( Glarch.class ).nullSafeGet(rs, names[1], session, owner);
-		Hibernate.INTEGER.nullSafeSet(st, c, index, session);
-		Hibernate.entity(Glarch.class).nullSafeSet(st, g, index+1, session);
+		StandardBasicTypes.INTEGER.nullSafeSet(st, c, index, session);
+		( (Session) session ).getTypeHelper().entity( Glarch.class ).nullSafeSet(st, g, index+1, session);
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+import org.hibernate.type.StandardBasicTypes;
-				.setParameter( 0, new Integer(66), Hibernate.INTEGER )
+				.setParameter( 0, new Integer(66), StandardBasicTypes.INTEGER )
--- a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+import org.hibernate.type.StandardBasicTypes;
-										new Type[] { Hibernate.STRING }
+										new Type[] { StandardBasicTypes.STRING }
-								.add( Projections.sqlProjection( "555 as studentNumber", new String[]{ "studentNumber" }, new Type[] { Hibernate.LONG } ) )
+								.add( Projections.sqlProjection( "555 as studentNumber", new String[]{ "studentNumber" }, new Type[] { StandardBasicTypes.LONG } ) )
-				return ReflectHelper.getConstructor( Student.class, new Type[] {Hibernate.LONG, studentNametype} );
+				return ReflectHelper.getConstructor( Student.class, new Type[] {StandardBasicTypes.LONG, studentNametype} );
--- a/hibernate-core/src/test/java/org/hibernate/test/version/db/DbVersionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/version/db/DbVersionTest.java
+
-import org.hibernate.Hibernate;
+import org.hibernate.type.StandardBasicTypes;
-		assertFalse( "owner version not incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertFalse( "owner version not incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
-		assertFalse( "owner version not incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertFalse( "owner version not incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
-		Timestamp steveTimestamp = ( Timestamp ) steve.getTimestamp();
+		Timestamp steveTimestamp = steve.getTimestamp();
-		assertTrue( "owner version was incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertTrue( "owner version was incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
-		assertTrue( "owner version was incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertTrue( "owner version was incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );

Lines added containing method: 1756. Lines removed containing method: 2005. Tot = 3761
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
20/After/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
+++ b/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
+++ b/hibernate-core/src/main/java/org/hibernate/PropertyAccessException.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
+++ b/hibernate-core/src/main/java/org/hibernate/PropertyValueException.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/action/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/CollectionAction.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/action/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/EntityAction.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/cglib/BytecodeProviderImpl.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cache/AbstractJndiBoundCacheProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/AbstractJndiBoundCacheProvider.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cache/QueryKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/QueryKey.java
-import org.hibernate.transform.ResultTransformer;
-import org.hibernate.util.EqualsHelper;
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.compare.EqualsHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cache/entry/CollectionCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/entry/CollectionCacheEntry.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/RegionFactoryCacheProviderBridge.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/impl/bridge/RegionFactoryCacheProviderBridge.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ColumnsBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ColumnsBuilder.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+import org.hibernate.internal.util.ConfigHelper;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.SerializationHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.xml.MappingReader;
+import org.hibernate.internal.util.xml.OriginImpl;
+import org.hibernate.internal.util.xml.XMLHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.ConfigHelper;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.SerializationHelper;
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.XMLHelper;
-import org.hibernate.util.xml.MappingReader;
-import org.hibernate.util.xml.Origin;
-import org.hibernate.util.xml.OriginImpl;
-import org.hibernate.util.xml.XmlDocument;
-import org.hibernate.util.xml.XmlDocumentImpl;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.internal.util.collections.JoinedIterator;
+import org.hibernate.internal.util.xml.Origin;
+import org.hibernate.internal.util.xml.XmlDocument;
+import org.hibernate.internal.util.xml.XmlDocumentImpl;
-	 * Default value is {@link org.hibernate.util.DTDEntityResolver}
+	 * Default value is {@link org.hibernate.internal.util.xml.DTDEntityResolver}
--- a/hibernate-core/src/main/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/EJB3DTDEntityResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/EJB3DTDEntityResolver.java
-import org.hibernate.util.DTDEntityResolver;
+import org.hibernate.internal.util.xml.DTDEntityResolver;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/EJB3NamingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/EJB3NamingStrategy.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+import org.hibernate.internal.util.ConfigHelper;
-import org.hibernate.util.ConfigHelper;
-	 * The maximum number of strong references maintained by {@link org.hibernate.util.SoftLimitMRUCache}. Default is 128.
+	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
-	 * The maximum number of soft references maintained by {@link org.hibernate.util.SoftLimitMRUCache}. Default is 2048.
+	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
-			InputStream stream = ConfigHelper.getResourceAsStream("/hibernate.properties");
+			InputStream stream = ConfigHelper.getResourceAsStream( "/hibernate.properties" );
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ExtendsQueueEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ExtendsQueueEntry.java
-import org.hibernate.util.xml.XmlDocument;
+import org.hibernate.internal.util.xml.XmlDocument;
--- a/hibernate-core/src/main/java/org/hibernate/util/ExternalSessionFactoryConfig.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
-package org.hibernate.util;
+package org.hibernate.cfg;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
+import org.hibernate.internal.util.StringHelper;
-		if ( StringHelper.isNotEmpty( customListenersString) ) {
+		if ( StringHelper.isNotEmpty( customListenersString ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.JoinedIterator;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.xml.XmlDocument;
+import org.hibernate.internal.util.xml.XmlDocument;
-				entity.setEntityPersisterClass( ReflectHelper.classForName( persisterNode
-					.getValue() ) );
+				entity.setEntityPersisterClass( ReflectHelper.classForName(
+						persisterNode
+								.getValue()
+				) );
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
-		if( StringHelper.isEmpty(alias)) {
+		if( StringHelper.isEmpty( alias )) {
-			propertyresults.put("class", ArrayHelper.toStringArray(resultColumns) );
+			propertyresults.put("class", ArrayHelper.toStringArray( resultColumns ) );
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/WrappedInferredData.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/WrappedInferredData.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/IdBagBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/IdBagBinder.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ListBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ListBinder.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/ResultsetMappingSecondPass.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAMetadataProvider.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverridenAnnotationReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/JPAOverridenAnnotationReader.java
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/XMLContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/reflection/XMLContext.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/GroupsPerOperation.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/GroupsPerOperation.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.internal.util.collections.EmptyIterator;
+import org.hibernate.internal.util.collections.IdentitySet;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.EmptyIterator;
-import org.hibernate.util.IdentitySet;
-import org.hibernate.util.MarkerObject;
+import org.hibernate.internal.util.MarkerObject;
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
-		return CollectionHelper.EMPTY_COLLECTION; 
+		return CollectionHelper.EMPTY_COLLECTION;
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-			" and ",
-			StringHelper.suffix( columns, " = ?" )
+				" and ",
+				StringHelper.suffix( columns, " = ?" )
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-			" or ",
-			StringHelper.suffix( columns, " is not null" )
+				" or ",
+				StringHelper.suffix( columns, " is not null" )
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-			" and ",
-			StringHelper.suffix( columns, " is null" )
+				" and ",
+				StringHelper.suffix( columns, " is null" )
--- a/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-			ArrayHelper.addAll(result, aliases);
+			ArrayHelper.addAll( result, aliases );
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-			StringHelper.add(xcols, getOp(), ycols)
+			StringHelper.add( xcols, getOp(), ycols )
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-		return StringHelper.replace( sql, "{alias}", criteriaQuery.getSQLAlias(criteria) );
+		return StringHelper.replace( sql, "{alias}", criteriaQuery.getSQLAlias( criteria ) );
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-		return StringHelper.replace( groupBy, "{alias}", criteriaQuery.getSQLAlias(criteria) );
+		return StringHelper.replace( groupBy, "{alias}", criteriaQuery.getSQLAlias( criteria ) );
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DerbyDialect.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+import org.hibernate.internal.util.JdbcExceptionHelper;
-import org.hibernate.exception.JDBCExceptionHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
-			int errorCode = JDBCExceptionHelper.extractErrorCode( sqle );
+			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
-			int errorCode = JDBCExceptionHelper.extractErrorCode( sqle );
+			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
-import org.hibernate.exception.JDBCExceptionHelper;
+import org.hibernate.internal.util.JdbcExceptionHelper;
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
+			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+import org.hibernate.internal.util.JdbcExceptionHelper;
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.exception.JDBCExceptionHelper;
-			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
+			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
-import org.hibernate.exception.JDBCExceptionHelper;
+import org.hibernate.internal.util.JdbcExceptionHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
-			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
+			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
-import org.hibernate.exception.JDBCExceptionHelper;
+import org.hibernate.internal.util.JdbcExceptionHelper;
-				int sqlState = Integer.valueOf( JDBCExceptionHelper.extractSqlState(sqle)).intValue();
+				int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle )).intValue();
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-			.append( StringHelper.join(", ", foreignKey) )
+			.append( StringHelper.join( ", ", foreignKey ) )
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TypeNames.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
-import org.hibernate.util.MarkerObject;
+import org.hibernate.internal.util.MarkerObject;
--- a/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/CascadeStyle.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/CascadeStyle.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-			return ArrayHelper.toString(styles);
+			return ArrayHelper.toString( styles );
--- a/hibernate-core/src/main/java/org/hibernate/engine/JoinHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/JoinHelper.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/JoinSequence.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/JoinSequence.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
+import org.hibernate.internal.util.collections.IdentityMap;
-import org.hibernate.util.IdentityMap;
-import org.hibernate.util.MarkerObject;
+import org.hibernate.internal.util.MarkerObject;
--- a/hibernate-core/src/main/java/org/hibernate/engine/SubselectFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SubselectFetch.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+
-import org.hibernate.util.JDBCExceptionReporter;
-
+	private static final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
+	@SuppressWarnings( {"UnnecessaryBoxing"})
-			return new Integer( findColumn( ( String ) args[0] ) );
+			return Integer.valueOf( findColumn( ( String ) args[0] ) );
-				JDBCExceptionReporter.logExceptions( ex, buf.toString() );
+				sqlExceptionHelper.logExceptions( ex, buf.toString() );
+	@SuppressWarnings( {"UnnecessaryBoxing"})
-		actualArgs[0] = new Integer( columnIndex );
+		actualArgs[0] = Integer.valueOf( columnIndex );
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/BasicFormatterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/BasicFormatterImpl.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfoExtracter.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/TypeInfoExtracter.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
+import java.sql.Statement;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
+
+	// SQLException ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
-	 * @param sqle The exception to convert
+	 * @param sqlException The exception to convert
-	public JDBCException convert(SQLException sqle, String message) {
-		return convert( sqle, message, "n/a" );
+	public JDBCException convert(SQLException sqlException, String message) {
+		return convert( sqlException, message, "n/a" );
-	 * @param sqle The exception to convert
+	 * @param sqlException The exception to convert
-	public JDBCException convert(SQLException sqle, String message, String sql) {
-		logExceptions( sqle, message + " [" + sql + "]" );
-		return sqlExceptionConverter.convert( sqle, message, sql );
+	public JDBCException convert(SQLException sqlException, String message, String sql) {
+		logExceptions( sqlException, message + " [" + sql + "]" );
+		return sqlExceptionConverter.convert( sqlException, message, sql );
-	 * Log any {@link java.sql.SQLWarning}s registered with the connection.
+	 * Log the given (and any nested) exception.
-	 * @param connection The connection to check for warnings.
+	 * @param sqlException The exception to log
+	 * @param message The message text to use as a preamble.
-	public void logAndClearWarnings(Connection connection) {
-		if ( log.isWarnEnabled() ) {
-			try {
-				logWarnings( connection.getWarnings() );
+	public void logExceptions(SQLException sqlException, String message) {
+		if ( log.isErrorEnabled() ) {
+			if ( log.isDebugEnabled() ) {
+				message = StringHelper.isNotEmpty( message ) ? message : DEFAULT_EXCEPTION_MSG;
+				log.debug( message, sqlException );
-			catch ( SQLException sqle ) {
-				//workaround for WebLogic
-				log.debug( "could not log warnings", sqle );
+			while ( sqlException != null ) {
+				StringBuffer buf = new StringBuffer( 30 )
+						.append( "SQL Error: " )
+						.append( sqlException.getErrorCode() )
+						.append( ", SQLState: " )
+						.append( sqlException.getSQLState() );
+				log.warn( buf.toString() );
+				log.error( sqlException.getMessage() );
+				sqlException = sqlException.getNextException();
-		try {
-			//Sybase fail if we don't do that, sigh...
-			connection.clearWarnings();
-		}
-		catch ( SQLException sqle ) {
-			log.debug( "could not clear warnings", sqle );
-		}
+	}
+
+
+	// SQLWarning ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * Contract for handling {@link SQLWarning warnings}
+	 */
+	public static interface WarningHandler {
+		/**
+		 * Should processing be done? Allows short-circuiting if not.
+		 *
+		 * @return True to process warnings, false otherwise.
+		 */
+		public boolean doProcess();
+
+		/**
+		 * Prepare for processing of a {@link SQLWarning warning} stack.
+		 * <p/>
+		 * Note that the warning here is also the first passed to {@link #handleWarning}
+		 *
+		 * @param warning The first warning in the stack.
+		 */
+		public void prepare(SQLWarning warning);
+		/**
+		 * Handle an individual warning in the stack.
+		 *
+		 * @param warning The warning to handle.
+		 */
+		public void handleWarning(SQLWarning warning);
-	 * Log the given (and any nested) warning.
-	 *
-	 * @param warning The warning
+	 * Basic support for {@link WarningHandler} implementations which log
-	public void logWarnings(SQLWarning warning) {
-		logWarnings( warning, null );
+	public static abstract class WarningHandlerLoggingSupport implements WarningHandler {
+		public final void handleWarning(SQLWarning warning) {
+			StringBuffer buf = new StringBuffer( 30 )
+					.append( "SQL Warning Code: " )
+					.append( warning.getErrorCode() )
+					.append( ", SQLState: " )
+					.append( warning.getSQLState() );
+			logWarning( buf.toString(), warning.getMessage() );
+		}
+
+		/**
+		 * Delegate to log common details of a {@link SQLWarning warning}
+		 *
+		 * @param description A description of the warning
+		 * @param message The warning message
+		 */
+		protected abstract void logWarning(String description, String message);
+	}
+
+	public static class StandardWarningHandler extends WarningHandlerLoggingSupport {
+		private final String introMessage;
+
+		public StandardWarningHandler(String introMessage) {
+			this.introMessage = introMessage;
+		}
+
+		public boolean doProcess() {
+			return log.isWarnEnabled();
+		}
+
+		public void prepare(SQLWarning warning) {
+			log.debug( introMessage, warning );
+		}
+
+		@Override
+		protected void logWarning(String description, String message) {
+			log.warn( description );
+			log.warn( message );
+		}
+	}
+
+	public static StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler( DEFAULT_WARNING_MSG );
+
+	public void walkWarnings(SQLWarning warning, WarningHandler handler) {
+		if ( warning == null || handler.doProcess() ) {
+			return;
+		}
+		handler.prepare( warning );
+		while ( warning != null ) {
+			handler.handleWarning( warning );
+			warning = warning.getNextWarning();
+		}
-	 * Log the given (and any nested) warning.
+	 * Standard (legacy) behavior for logging warnings associated with a JDBC {@link Connection} and clearing them.
+	 * <p/>
+	 * Calls {@link #handleAndClearWarnings(Connection, WarningHandler)} using {@link #STANDARD_WARNING_HANDLER}
-	 * @param warning The warning
-	 * @param message The message text to use as a preamble.
+	 * @param connection The JDBC connection potentially containing warnings
-	public void logWarnings(SQLWarning warning, String message) {
-		if ( log.isWarnEnabled() ) {
-			if ( log.isDebugEnabled() && warning != null ) {
-				message = StringHelper.isNotEmpty( message ) ? message : DEFAULT_WARNING_MSG;
-				log.debug( message, warning );
-			}
-			while ( warning != null ) {
-				StringBuffer buf = new StringBuffer( 30 )
-						.append( "SQL Warning: " )
-						.append( warning.getErrorCode() )
-						.append( ", SQLState: " )
-						.append( warning.getSQLState() );
-				log.warn( buf.toString() );
-				log.warn( warning.getMessage() );
-				warning = warning.getNextWarning();
-			}
-		}
+	public void logAndClearWarnings(Connection connection) {
+		handleAndClearWarnings( connection, STANDARD_WARNING_HANDLER );
-	 * Log the given (and any nested) exception.
+	 * General purpose handling of warnings associated with a JDBC {@link Connection}.
-	 * @param sqlException The exception to log
+	 * @param connection The JDBC connection potentially containing warnings
+	 * @param handler The handler for each individual warning in the stack.
+	 * @see #walkWarnings
-	public void logExceptions(SQLException sqlException) {
-		logExceptions( sqlException, null );
+	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
+	public void handleAndClearWarnings(Connection connection, WarningHandler handler) {
+		try {
+			walkWarnings( connection.getWarnings(), handler );
+		}
+		catch (SQLException sqle) {
+			//workaround for WebLogic
+			log.debug( "could not log warnings", sqle );
+		}
+		try {
+			//Sybase fail if we don't do that, sigh...
+			connection.clearWarnings();
+		}
+		catch (SQLException sqle) {
+			log.debug( "could not clear warnings", sqle );
+		}
-	 * Log the given (and any nested) exception.
+	 * General purpose handling of warnings associated with a JDBC {@link Statement}.
-	 * @param sqlException The exception to log
-	 * @param message The message text to use as a preamble.
+	 * @param statement The JDBC statement potentially containing warnings
+	 * @param handler The handler for each individual warning in the stack.
+	 * @see #walkWarnings
-	public void logExceptions(SQLException sqlException, String message) {
-		if ( log.isErrorEnabled() ) {
-			if ( log.isDebugEnabled() ) {
-				message = StringHelper.isNotEmpty( message ) ? message : DEFAULT_EXCEPTION_MSG;
-				log.debug( message, sqlException );
-			}
-			while ( sqlException != null ) {
-				StringBuffer buf = new StringBuffer( 30 )
-						.append( "SQL Error: " )
-						.append( sqlException.getErrorCode() )
-						.append( ", SQLState: " )
-						.append( sqlException.getSQLState() );
-				log.warn( buf.toString() );
-				log.error( sqlException.getMessage() );
-				sqlException = sqlException.getNextException();
-			}
+	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
+	public void handleAndClearWarnings(Statement statement, WarningHandler handler) {
+		try {
+			walkWarnings( statement.getWarnings(), handler );
+		}
+		catch (SQLException sqlException) {
+			//workaround for WebLogic
+			log.debug( "could not log warnings", sqlException );
+		}
+		try {
+			//Sybase fail if we don't do that, sigh...
+			statement.clearWarnings();
+		}
+		catch (SQLException sqle) {
+			log.debug( "could not clear warnings", sqle );
+
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
-import org.hibernate.util.IdentityMap;
+import org.hibernate.internal.util.collections.IdentityMap;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.EmptyIterator;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.IdentitySet;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.collections.EmptyIterator;
+import org.hibernate.internal.util.collections.JoinedIterator;
+import org.hibernate.internal.util.collections.IdentitySet;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/NativeSQLQueryPlan.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/ParamLocationRecognizer.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/ParameterParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/ParameterParser.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
+import org.hibernate.internal.util.collections.SoftLimitMRUCache;
-import org.hibernate.util.SimpleMRUCache;
-import org.hibernate.util.SoftLimitMRUCache;
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.collections.SimpleMRUCache;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/sql/NativeSQLQuerySpecification.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaTransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jta/JtaTransactionFactory.java
-import org.hibernate.util.JTAHelper;
-					return JTAHelper.isInProgress( ut.getStatus() );
+					return JtaStatusHelper.isActive( ut );
-				return ut != null && JTAHelper.isInProgress( ut.getStatus() );
+				return ut != null && JtaStatusHelper.isActive( ut );
--- a/hibernate-core/src/main/java/org/hibernate/event/EventListeners.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/EventListeners.java
-import org.hibernate.util.Cloneable;
+import org.hibernate.internal.util.Cloneable;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
+import org.hibernate.internal.util.collections.IdentityMap;
+import org.hibernate.internal.util.collections.LazyIterator;
-import org.hibernate.util.IdentityMap;
-import org.hibernate.util.LazyIterator;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
+import org.hibernate.internal.util.collections.IdentitySet;
-import org.hibernate.util.IdentitySet;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultPersistEventListener.java
-import org.hibernate.util.IdentityMap;
+import org.hibernate.internal.util.collections.IdentityMap;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
-import org.hibernate.util.IdentityMap;
+import org.hibernate.internal.util.collections.IdentityMap;
--- a/hibernate-core/src/main/java/org/hibernate/event/def/EventCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/EventCache.java
-import org.hibernate.util.IdentityMap;
+import org.hibernate.internal.util.collections.IdentityMap;
-	private Map entityToOperatedOnFlagMap = IdentityMap.instantiate(10);
+	private Map entityToOperatedOnFlagMap = IdentityMap.instantiate( 10 );
--- a/hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
+import org.hibernate.internal.util.JdbcExceptionHelper;
-		String sqlStateClassCode = JDBCExceptionHelper.extractSqlStateClassCode( sqlException );
-		Integer errorCode = new Integer( JDBCExceptionHelper.extractErrorCode( sqlException ) );
+		String sqlStateClassCode = JdbcExceptionHelper.extractSqlStateClassCode( sqlException );
+		Integer errorCode = new Integer( JdbcExceptionHelper.extractErrorCode( sqlException ) );
--- a/hibernate-core/src/main/java/org/hibernate/exception/JDBCExceptionHelper.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.exception;
-
-import org.hibernate.JDBCException;
-import org.hibernate.util.JDBCExceptionReporter;
-
-import java.sql.SQLException;
-
-/**
- * Implementation of JDBCExceptionHelper.
- *
- * @author Steve Ebersole
- */
-public final class JDBCExceptionHelper {
-
-	private JDBCExceptionHelper() {
-	}
-
-	/**
-	 * Converts the given SQLException into Hibernate's JDBCException hierarchy, as well as performing
-	 * appropriate logging.
-	 *
-	 * @param converter    The converter to use.
-	 * @param sqlException The exception to convert.
-	 * @param message      An optional error message.
-	 * @return The converted JDBCException.
-	 */
-	public static JDBCException convert(SQLExceptionConverter converter, SQLException sqlException, String message) {
-		return convert( converter, sqlException, message, "???" );
-	}
-
-	/**
-	 * Converts the given SQLException into Hibernate's JDBCException hierarchy, as well as performing
-	 * appropriate logging.
-	 *
-	 * @param converter    The converter to use.
-	 * @param sqlException The exception to convert.
-	 * @param message      An optional error message.
-	 * @return The converted JDBCException.
-	 */
-	public static JDBCException convert(SQLExceptionConverter converter, SQLException sqlException, String message, String sql) {
-		JDBCExceptionReporter.logExceptions( sqlException, message + " [" + sql + "]" );
-		return converter.convert( sqlException, message, sql );
-	}
-
-	/**
-	 * For the given SQLException, locates the vendor-specific error code.
-	 *
-	 * @param sqlException The exception from which to extract the SQLState
-	 * @return The error code.
-	 */
-	public static int extractErrorCode(SQLException sqlException) {
-		int errorCode = sqlException.getErrorCode();
-		SQLException nested = sqlException.getNextException();
-		while ( errorCode == 0 && nested != null ) {
-			errorCode = nested.getErrorCode();
-			nested = nested.getNextException();
-		}
-		return errorCode;
-	}
-
-	/**
-	 * For the given SQLException, locates the X/Open-compliant SQLState.
-	 *
-	 * @param sqlException The exception from which to extract the SQLState
-	 * @return The SQLState code, or null.
-	 */
-	public static String extractSqlState(SQLException sqlException) {
-		String sqlState = sqlException.getSQLState();
-		SQLException nested = sqlException.getNextException();
-		while ( sqlState == null && nested != null ) {
-			sqlState = nested.getSQLState();
-			nested = nested.getNextException();
-		}
-		return sqlState;
-	}
-
-	/**
-	 * For the given SQLException, locates the X/Open-compliant SQLState's class code.
-	 *
-	 * @param sqlException The exception from which to extract the SQLState class code
-	 * @return The SQLState class code, or null.
-	 */
-	public static String extractSqlStateClassCode(SQLException sqlException) {
-		return determineSqlStateClassCode( extractSqlState( sqlException ) );
-	}
-
-	public static String determineSqlStateClassCode(String sqlState) {
-		if ( sqlState == null || sqlState.length() < 2 ) {
-			return sqlState;
-		}
-		return sqlState.substring( 0, 2 );
-	}
-}
--- a/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java
+import org.hibernate.internal.util.JdbcExceptionHelper;
-		String sqlState = JDBCExceptionHelper.extractSqlState( sqlException );
+		String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
-			String sqlStateClassCode = JDBCExceptionHelper.determineSqlStateClassCode( sqlState );
+			String sqlStateClassCode = JdbcExceptionHelper.determineSqlStateClassCode( sqlState );
--- a/hibernate-core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/QuerySplitter.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/QuerySplitter.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlParser.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-			return ArrayHelper.toIntArray( ( ArrayList ) o );
+			return ArrayHelper.toIntArray( (ArrayList) o );
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/ParameterTranslationsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/ParameterTranslationsImpl.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.IdentitySet;
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.collections.IdentitySet;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/AbstractStatementExecutor.java
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.JDBCExceptionReporter;
-import org.hibernate.util.StringHelper;
-					JDBCExceptionReporter.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
+					persister.getFactory()
+							.getServiceRegistry()
+							.getService( JdbcServices.class )
+							.getSqlExceptionHelper()
+							.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
-	private static JDBCExceptionReporter.WarningHandler CREATION_WARNING_HANDLER = new JDBCExceptionReporter.WarningHandlerLoggingSupport() {
+	private static SqlExceptionHelper.WarningHandler CREATION_WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/exec/MultiTableUpdateExecutor.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractMapComponentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractMapComponentNode.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AbstractNullnessCheckNode.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ComponentJoin.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ComponentJoin.java
-import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElement.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementFactory.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/FromElementType.java
-import java.util.Iterator;
-import org.hibernate.type.CollectionType;
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.type.TypeFactory;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IdentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IdentNode.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IntoClause.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/IntoClause.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/JavaConstantNode.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Node.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/Node.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ResultVariableRefNode.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-		return StringHelper.join( ", ", getWalker().getSelectClause().getColumnNames()[ scalarColumnIndex ] );
+		return StringHelper.join( ", ", getWalker().getSelectClause().getColumnNames()[scalarColumnIndex] );
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTPrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/ASTPrinter.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/AliasGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/AliasGenerator.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
-import java.util.Map;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.param.CollectionFilterKeyParameterSpecification;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.StringHelper;
-						ArrayHelper.fillArray( "?", type.getColumnSpan( walker.getSessionFactoryHelper().getFactory() ) )
+						ArrayHelper.fillArray(
+								"?", type.getColumnSpan(
+								walker.getSessionFactoryHelper().getFactory()
+						)
+						)
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/PathHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/PathHelper.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/SyntheticAndFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/SyntheticAndFactory.java
-import org.hibernate.util.StringHelper;
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.internal.util.StringHelper;
-import antlr.ASTFactory;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/GroupByParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/GroupByParser.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/OrderByParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/OrderByParser.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/ParserHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/ParserHelper.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/PreprocessingParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/PreprocessingParser.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
-import org.hibernate.exception.JDBCExceptionHelper;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/WhereParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/WhereParser.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/AbstractUUIDGenerator.java
-import org.hibernate.util.BytesHelper;
+import org.hibernate.internal.util.BytesHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IncrementGenerator.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/UUIDGenerator.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/OptimizerFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/OptimizerFactory.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/factory/DefaultIdentifierGeneratorFactory.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/uuid/CustomVersionOneStrategy.java
-import org.hibernate.util.BytesHelper;
+import org.hibernate.internal.util.BytesHelper;
--- a/hibernate-core/src/main/java/org/hibernate/id/uuid/Helper.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/uuid/Helper.java
-import org.hibernate.util.BytesHelper;
+import org.hibernate.internal.util.BytesHelper;
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.MarkerObject;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.MarkerObject;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.exception.JDBCExceptionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/util/FilterHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/FilterHelper.java
-package org.hibernate.util;
+package org.hibernate.impl;
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.util.StringHelper;
-			filterConditions[filterCount] = StringHelper.replace( filterConditions[filterCount],
+			filterConditions[filterCount] = StringHelper.replace(
+					filterConditions[filterCount],
-					":" + filterNames[filterCount] + "." );
+					":" + filterNames[filterCount] + "."
+			);
--- a/hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-import org.hibernate.exception.JDBCExceptionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.EmptyIterator;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.collections.EmptyIterator;
-				ReflectHelper.classForName(className);
+				ReflectHelper.classForName( className );
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
-import org.hibernate.engine.transaction.spi.TransactionObserver;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/util/BytesHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/BytesHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util;
--- a/hibernate-core/src/main/java/org/hibernate/util/Cloneable.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/Cloneable.java
-package org.hibernate.util;
+package org.hibernate.internal.util;
--- a/hibernate-core/src/main/java/org/hibernate/util/ConfigHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.util;
+package org.hibernate.internal.util;
- * @author Steve
+ * @todo : Update usages to use {@link org.hibernate.service.classloading.spi.ClassLoaderService}
+ *
+ * @author Steve Ebersole
-	public static final URL locateConfig(final String path) {
+	public static URL locateConfig(final String path) {
-	public static final URL findAsResource(final String path) {
+	public static URL findAsResource(final String path) {
-	public static final InputStream getConfigStream(final String path) throws HibernateException {
+	public static InputStream getConfigStream(final String path) throws HibernateException {
-	public static final Reader getConfigStreamReader(final String path) throws HibernateException {
+	public static Reader getConfigStreamReader(final String path) throws HibernateException {
-	public static final Properties getConfigProperties(String path) throws HibernateException {
+	public static Properties getConfigProperties(String path) throws HibernateException {
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/JdbcExceptionHelper.java
+package org.hibernate.internal.util;
+
+import java.sql.SQLException;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JdbcExceptionHelper {
+	/**
+	 * For the given SQLException, locates the vendor-specific error code.
+	 *
+	 * @param sqlException The exception from which to extract the SQLState
+	 * @return The error code.
+	 */
+	public static int extractErrorCode(SQLException sqlException) {
+		int errorCode = sqlException.getErrorCode();
+		SQLException nested = sqlException.getNextException();
+		while ( errorCode == 0 && nested != null ) {
+			errorCode = nested.getErrorCode();
+			nested = nested.getNextException();
+		}
+		return errorCode;
+	}
+
+	/**
+	 * For the given SQLException, locates the X/Open-compliant SQLState.
+	 *
+	 * @param sqlException The exception from which to extract the SQLState
+	 * @return The SQLState code, or null.
+	 */
+	public static String extractSqlState(SQLException sqlException) {
+		String sqlState = sqlException.getSQLState();
+		SQLException nested = sqlException.getNextException();
+		while ( sqlState == null && nested != null ) {
+			sqlState = nested.getSQLState();
+			nested = nested.getNextException();
+		}
+		return sqlState;
+	}
+
+	/**
+	 * For the given SQLException, locates the X/Open-compliant SQLState's class code.
+	 *
+	 * @param sqlException The exception from which to extract the SQLState class code
+	 * @return The SQLState class code, or null.
+	 */
+	public static String extractSqlStateClassCode(SQLException sqlException) {
+		return determineSqlStateClassCode( extractSqlState( sqlException ) );
+	}
+
+	public static String determineSqlStateClassCode(String sqlState) {
+		if ( sqlState == null || sqlState.length() < 2 ) {
+			return sqlState;
+		}
+		return sqlState.substring( 0, 2 );
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/util/MarkerObject.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/MarkerObject.java
-package org.hibernate.util;
+package org.hibernate.internal.util;
--- a/hibernate-core/src/main/java/org/hibernate/util/ReflectHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util;
--- a/hibernate-core/src/main/java/org/hibernate/util/SerializationHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util;
--- a/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util;
-import java.util.regex.Pattern;
+import org.hibernate.internal.util.collections.ArrayHelper;
-	 * Imagine you have a class named <samp>'org.hibernate.util.StringHelper'</samp>; calling collapse on that
+	 * Imagine you have a class named <samp>'org.hibernate.internal.util.StringHelper'</samp>; calling collapse on that
-	 * 'org.hibernate.util.StringHelper' would become 'util.StringHelper'.
+	 * 'org.hibernate.internal.util.StringHelper' would become 'util.StringHelper'.
-	 * 'org.hibernate.util.StringHelper' would become 'o.h.util.StringHelper'.
+	 * 'org.hibernate.internal.util.StringHelper' would become 'o.h.util.StringHelper'.
--- a/hibernate-core/src/main/java/org/hibernate/util/ArrayHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/CollectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/CollectionHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/EmptyIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/EmptyIterator.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/IdentityMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/IdentityMap.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/IdentitySet.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/IdentitySet.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/JoinedIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterator.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/LRUMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/LRUMap.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/LazyIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/LazyIterator.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/SimpleMRUCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/SimpleMRUCache.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/SingletonIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/SingletonIterator.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/SoftLimitMRUCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/SoftLimitMRUCache.java
-package org.hibernate.util;
+package org.hibernate.internal.util.collections;
--- a/hibernate-core/src/main/java/org/hibernate/util/CalendarComparator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/compare/CalendarComparator.java
-package org.hibernate.util;
+package org.hibernate.internal.util.compare;
--- a/hibernate-core/src/main/java/org/hibernate/util/ComparableComparator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/compare/ComparableComparator.java
-package org.hibernate.util;
+package org.hibernate.internal.util.compare;
--- a/hibernate-core/src/main/java/org/hibernate/util/EqualsHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/compare/EqualsHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util.compare;
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/config/ConfigurationHelper.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/util/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/package.html
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
-  ~
-	Utility classes.
+	Internal utility classes.
--- a/hibernate-core/src/main/java/org/hibernate/util/DTDEntityResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-package org.hibernate.util;
+package org.hibernate.internal.util.xml;
+import org.hibernate.internal.util.ConfigHelper;
+
- * various systemId URLs to local classpath lookups<ol>
+ * various systemId URLs to local classpath look ups<ol>
-
--- a/hibernate-core/src/main/java/org/hibernate/util/xml/ErrorLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/ErrorLogger.java
-package org.hibernate.util.xml;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/util/xml/MappingReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/MappingReader.java
-package org.hibernate.util.xml;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/util/xml/Origin.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/Origin.java
-package org.hibernate.util.xml;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/util/xml/OriginImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/OriginImpl.java
-package org.hibernate.util.xml;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/util/XMLHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java
-package org.hibernate.util;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/util/xml/XmlDocument.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XmlDocument.java
-package org.hibernate.util.xml;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/util/xml/XmlDocumentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XmlDocumentImpl.java
-package org.hibernate.util.xml;
+package org.hibernate.internal.util.xml;
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
-import org.hibernate.util.JDBCExceptionReporter;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+	private static SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
-				JDBCExceptionReporter.logExceptions( sqle, "could not extract row counts from CallableStatement" );
+				sqlExceptionHelper.logExceptions( sqle, "could not extract row counts from CallableStatement" );
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+import org.hibernate.cfg.ExternalSessionFactoryConfig;
-import org.hibernate.util.ExternalSessionFactoryConfig;
--- a/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/GeneratedCollectionAliases.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/GeneratedCollectionAliases.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-		this(CollectionHelper.EMPTY_MAP, persister, string);
+		this( CollectionHelper.EMPTY_MAP, persister, string);
--- a/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-		lockModeArray = ArrayHelper.fillArray(lockOptions.getLockMode(), joins);
+		lockModeArray = ArrayHelper.fillArray( lockOptions.getLockMode(), joins );
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/OuterJoinableAssociation.java
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/PropertyPath.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/PropertyPath.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionJoinWalker.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BatchingCollectionInitializer.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-			int[] batchSizesToCreate = ArrayHelper.getBatchSizes(maxBatchSize);
+			int[] batchSizesToCreate = ArrayHelper.getBatchSizes( maxBatchSize );
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/CollectionJoinWalker.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyJoinWalker.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.StringHelper;
-		whereString.insert( 0, StringHelper.moveAndToBeginning(filter) );
+		whereString.insert( 0, StringHelper.moveAndToBeginning( filter ) );
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.transform.CacheableResultTransformer;
-import org.hibernate.transform.TupleSubsetResultTransformer;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/ColumnCollectionAliases.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/ColumnCollectionAliases.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
-import org.hibernate.HibernateException;
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/CascadeEntityJoinWalker.java
-import org.hibernate.LockMode;
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-				ArrayHelper.join( 
-						collectionPersister.getKeyColumnNames(), 
+				ArrayHelper.join(
+						collectionPersister.getKeyColumnNames(),
-					),
+				),
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
-import org.hibernate.exception.JDBCExceptionHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Array.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Array.java
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.collections.EmptyIterator;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.EmptyIterator;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
-import org.hibernate.type.ComponentType;
-import org.hibernate.type.EmbeddedComponentType;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.collections.JoinedIterator;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
-import org.hibernate.util.JoinedIterator;
+import org.hibernate.internal.util.collections.JoinedIterator;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Index.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.JoinedIterator;
-import org.hibernate.util.EmptyIterator;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.SingletonIterator;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.EmptyIterator;
+import org.hibernate.internal.util.collections.SingletonIterator;
-			return ReflectHelper.classForName(proxyInterfaceName);
+			return ReflectHelper.classForName( proxyInterfaceName );
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Property.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-				!ArrayHelper.isAllFalse(columnInsertability)
+				!ArrayHelper.isAllFalse( columnInsertability )
--- a/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.SingletonIterator;
+import org.hibernate.internal.util.collections.SingletonIterator;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleAuxiliaryDatabaseObject.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SingleTableSubclass.java
-import org.hibernate.util.JoinedIterator;
+import org.hibernate.internal.util.collections.JoinedIterator;
-		return new JoinedIterator( 
+		return new JoinedIterator(
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Subclass.java
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.SingletonIterator;
+import org.hibernate.internal.util.collections.JoinedIterator;
+import org.hibernate.internal.util.collections.SingletonIterator;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.util.CollectionHelper;
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-			referencedEntityName = ReflectHelper.reflectedPropertyClass(className, propertyName).getName();
+			referencedEntityName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
--- a/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.FilterHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.impl.FilterHelper;
+import org.hibernate.internal.util.StringHelper;
-		return i + ArrayHelper.countTrue(elementColumnIsSettable);
+		return i + ArrayHelper.countTrue( elementColumnIsSettable );
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/ElementPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/ElementPropertyMapping.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-			return StringHelper.qualify(alias, elementColumns);
+			return StringHelper.qualify( alias, elementColumns );
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-		String[] rowSelectColumnNames = ArrayHelper.join(keyColumnNames, elementColumnNames);
+		String[] rowSelectColumnNames = ArrayHelper.join( keyColumnNames, elementColumnNames );
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.FilterHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.impl.FilterHelper;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-				String[] columnReaderTemplateSlice = ArrayHelper.slice(columnReaderTemplates, begin, length);
+				String[] columnReaderTemplateSlice = ArrayHelper.slice( columnReaderTemplates, begin, length );
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
-import java.util.HashMap;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.compare.EqualsHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.EqualsHelper;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.type.AbstractType;
-import org.hibernate.util.ArrayHelper;
-		naturalOrderTableNames = ArrayHelper.toStringArray(tables);
+		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+import org.hibernate.internal.util.MarkerObject;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.MarkerObject;
-		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(joinKeyColumns);
+		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.collections.JoinedIterator;
+import org.hibernate.internal.util.collections.SingletonIterator;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.SingletonIterator;
--- a/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
-			if ( !ReflectHelper.isPublic(theClass, method) ) {
+			if ( !ReflectHelper.isPublic( theClass, method ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
-			accessorClass = ReflectHelper.classForName(accessorName);
+			accessorClass = ReflectHelper.classForName( accessorName );
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
+import org.hibernate.internal.util.MarkerObject;
-import org.hibernate.util.MarkerObject;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/cglib/CGLIBLazyInitializer.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
-import org.hibernate.util.CollectionHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
-import java.util.Collections;
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/sql/CaseFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/CaseFragment.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-		cases.put( StringHelper.qualify(alias, columnName), value );
+		cases.put( StringHelper.qualify( alias, columnName ), value );
--- a/hibernate-core/src/main/java/org/hibernate/sql/ConditionFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ConditionFragment.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
-import java.util.HashMap;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-		this.columnName = StringHelper.qualify(alias, columnName);
+		this.columnName = StringHelper.qualify( alias, columnName );
--- a/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/sql/Select.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/Select.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-			if ( StringHelper.isNotEmpty(whereClause) ) {
+			if ( StringHelper.isNotEmpty( whereClause ) ) {
--- a/hibernate-core/src/main/java/org/hibernate/sql/SelectFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/SelectFragment.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
-		columns.add( StringHelper.replace(formula, Template.TEMPLATE, tableAlias) );
+		columns.add( StringHelper.replace( formula, Template.TEMPLATE, tableAlias ) );
--- a/hibernate-core/src/main/java/org/hibernate/sql/Template.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/Template.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
+import org.hibernate.HibernateException;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.exception.SQLExceptionConverter;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.mapping.Table;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-import org.hibernate.HibernateException;
-import org.hibernate.exception.JDBCExceptionHelper;
-import org.hibernate.exception.SQLExceptionConverter;
-import org.hibernate.mapping.Table;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.util.StringHelper;
-
-								StringHelper.toLowerCase(catalog), 
+								StringHelper.toLowerCase( catalog ),
-			catch (SQLException sqle) {
-				throw JDBCExceptionHelper.convert(
-                        sqlExceptionConverter,
-				        sqle,
-				        "could not get table metadata: " + name
-					);
+			catch (SQLException sqlException) {
+				throw new SqlExceptionHelper( sqlExceptionConverter )
+						.convert( sqlException, "could not get table metadata: " + name );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
-import org.hibernate.util.JDBCExceptionReporter;
-				JDBCExceptionReporter.logAndClearWarnings( connection );
+				new SqlExceptionHelper().logAndClearWarnings( connection );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.internal.util.JdbcExceptionHelper;
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ConfigHelper;
-import org.hibernate.util.JDBCExceptionReporter;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ConfigHelper;
-	 * @param settings The 'parsed' settings.
+	 * @param jdbcServices The jdbc services
-	 * @deprecated use {@link org.hibernate.cfg.Environment.HBM2DDL_IMPORT_FILE}
+	 * @deprecated use {@link org.hibernate.cfg.Environment#HBM2DDL_IMPORT_FILES}
+		final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
+
-					JDBCExceptionReporter.logAndClearWarnings( connectionHelper.getConnection() );
+					sqlExceptionHelper.logAndClearWarnings( connectionHelper.getConnection() );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExportTask.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdateTask.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-		return ArrayHelper.toStringArray(files);
+		return ArrayHelper.toStringArray( files );
-					(NamingStrategy) ReflectHelper.classForName(namingStrategy).newInstance()
+					(NamingStrategy) ReflectHelper.classForName( namingStrategy ).newInstance()
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
-import org.hibernate.cfg.Settings;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidatorTask.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.ReflectHelper;
-		return ArrayHelper.toStringArray(files);
+		return ArrayHelper.toStringArray( files );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionHelper.java
-import org.hibernate.util.JDBCExceptionReporter;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
-		JDBCExceptionReporter.logAndClearWarnings( connection );
+		new SqlExceptionHelper().logAndClearWarnings( connection );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionProviderConnectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SuppliedConnectionProviderConnectionHelper.java
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
-import org.hibernate.util.JDBCExceptionReporter;
-			JDBCExceptionReporter.logAndClearWarnings( connection );
+			new SqlExceptionHelper().logAndClearWarnings( connection );
--- a/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
-import org.hibernate.util.ArrayHelper;
-
--- a/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/Dom4jInstantiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/Dom4jInstantiator.java
-import org.hibernate.util.XMLHelper;
+import org.hibernate.internal.util.xml.XMLHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-		constructor = ReflectHelper.getDefaultConstructor(mappedClass);
+		constructor = ReflectHelper.getDefaultConstructor( mappedClass );
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentTuplizerFactory.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizerFactory.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
-import org.hibernate.util.EqualsHelper;
+import org.hibernate.internal.util.compare.EqualsHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-				metaType.sqlTypes(mapping),
-				identifierType.sqlTypes(mapping)
-			);
+				metaType.sqlTypes( mapping ),
+				identifierType.sqlTypes( mapping )
+		);
--- a/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ByteArrayBlobType.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+import org.hibernate.internal.util.MarkerObject;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.MarkerObject;
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.EqualsHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.compare.EqualsHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeResolver.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/AbstractTypeDescriptor.java
-import org.hibernate.util.ComparableComparator;
-import org.hibernate.util.EqualsHelper;
+import org.hibernate.internal.util.compare.ComparableComparator;
+import org.hibernate.internal.util.compare.EqualsHelper;
-				? (Comparator<T>)ComparableComparator.INSTANCE
+				? (Comparator<T>) ComparableComparator.INSTANCE
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarDateTypeDescriptor.java
+import org.hibernate.internal.util.compare.CalendarComparator;
-import org.hibernate.util.CalendarComparator;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/CalendarTypeDescriptor.java
+import org.hibernate.internal.util.compare.CalendarComparator;
-import org.hibernate.util.CalendarComparator;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/ClassTypeDescriptor.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/SerializableTypeDescriptor.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UUIDTypeDescriptor.java
+import org.hibernate.internal.util.BytesHelper;
-import org.hibernate.util.BytesHelper;
--- a/hibernate-core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.util;
-
-import java.sql.Connection;
-import java.sql.SQLException;
-import java.sql.SQLWarning;
-import java.sql.Statement;
-
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
-
-public final class JDBCExceptionReporter {
-	public static final Logger log = LoggerFactory.getLogger(JDBCExceptionReporter.class);
-	public static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
-	public static final String DEFAULT_WARNING_MSG = "SQL Warning";
-
-	private JDBCExceptionReporter() {}
-
-	/**
-	 * Standard (legacy) behavior for logging warnings associated with a JDBC {@link Connection} and clearing them.
-	 * <p/>
-	 * Calls {@link #handleAndClearWarnings(Connection, WarningHandler)} using {@link #STANDARD_WARNING_HANDLER}
-	 *
-	 * @param connection The JDBC connection potentially containing warnings
-	 */
-	public static void logAndClearWarnings(Connection connection) {
-		handleAndClearWarnings( connection, STANDARD_WARNING_HANDLER );
-	}
-
-	/**
-	 * General purpose handling of warnings associated with a JDBC {@link Connection}.
-	 *
-	 * @param connection The JDBC connection potentially containing warnings
-	 * @param handler The handler for each individual warning in the stack.
-	 *
-	 * @see #walkWarnings
-	 */
-	@SuppressWarnings({ "ThrowableResultOfMethodCallIgnored" })
-	public static void handleAndClearWarnings(Connection connection, WarningHandler handler) {
-		try {
-			walkWarnings( connection.getWarnings(), handler );
-		}
-		catch ( SQLException sqle ) {
-			//workaround for WebLogic
-			log.debug( "could not log warnings", sqle );
-		}
-		try {
-			//Sybase fail if we don't do that, sigh...
-			connection.clearWarnings();
-		}
-		catch ( SQLException sqle ) {
-			log.debug( "could not clear warnings", sqle );
-		}
-	}
-
-	/**
-	 * General purpose handling of warnings associated with a JDBC {@link Statement}.
-	 *
-	 * @param statement The JDBC statement potentially containing warnings
-	 * @param handler The handler for each individual warning in the stack.
-	 *
-	 * @see #walkWarnings
-	 */
-	@SuppressWarnings({ "ThrowableResultOfMethodCallIgnored" })
-	public static void handleAndClearWarnings(Statement statement, WarningHandler handler) {
-		try {
-			walkWarnings( statement.getWarnings(), handler );
-		}
-		catch ( SQLException sqle ) {
-			//workaround for WebLogic
-			log.debug( "could not log warnings", sqle );
-		}
-		try {
-			//Sybase fail if we don't do that, sigh...
-			statement.clearWarnings();
-		}
-		catch ( SQLException sqle ) {
-			log.debug( "could not clear warnings", sqle );
-		}
-	}
-
-	/**
-	 * Log the given warning and all of its nested warnings, preceded with the {@link #DEFAULT_WARNING_MSG default message}
-	 *
-	 * @param warning The warning to log
-	 *
-	 * @deprecated Use {@link #walkWarnings} instead
-	 */
-	@Deprecated()
-	@SuppressWarnings({ "UnusedDeclaration" })
-	public static void logWarnings(SQLWarning warning) {
-		walkWarnings( warning, STANDARD_WARNING_HANDLER );
-	}
-
-	/**
-	 * Log the given warning and all of its nested warnings, preceded with the given message
-	 *
-	 * @param warning The warning to log
-	 * @param message The prologue message
-	 *
-	 * @deprecated Use {@link #walkWarnings} instead
-	 */
-	@Deprecated()
-	@SuppressWarnings({ "UnusedDeclaration" })
-	public static void logWarnings(SQLWarning warning, String message) {
-		final WarningHandler handler = StringHelper.isNotEmpty(message)
-				? new StandardWarningHandler( message )
-				: STANDARD_WARNING_HANDLER;
-		walkWarnings( warning, handler );
-	}
-
-	/**
-	 * Contract for handling {@link SQLWarning warnings}
-	 */
-	public static interface WarningHandler {
-		/**
-		 * Should processing be done?  Allows short-circuiting if not.
-		 *
-		 * @return True to process warnings, false otherwise.
-		 */
-		public boolean doProcess();
-
-		/**
-		 * Prepare for processing of a {@link SQLWarning warning} stack.
-		 * <p/>
-		 * Note that the warning here is also the first passed to {@link #handleWarning}
-		 *
-		 * @param warning The first warning in the stack.
-		 */
-		public void prepare(SQLWarning warning);
-
-		/**
-		 * Handle an individual warning in the stack.
-		 *
-		 * @param warning The warning to handle.
-		 */
-		public void handleWarning(SQLWarning warning);
-	}
-
-	/**
-	 * Basic support for {@link WarningHandler} implementations which log
-	 */
-	public static abstract class WarningHandlerLoggingSupport implements WarningHandler {
-		public final void handleWarning(SQLWarning warning) {
-			StringBuffer buf = new StringBuffer(30)
-					.append( "SQL Warning Code: ").append( warning.getErrorCode() )
-					.append( ", SQLState: ").append( warning.getSQLState() );
-			logWarning( buf.toString(), warning.getMessage() );
-		}
-
-		/**
-		 * Delegate to log common details of a {@link SQLWarning warning}
-		 *
-		 * @param description A description of the warning
-		 * @param message The warning message
-		 */
-		protected abstract void logWarning(String description, String message);
-	}
-
-	public static class StandardWarningHandler extends WarningHandlerLoggingSupport {
-		private final String introMessage;
-
-		public StandardWarningHandler(String introMessage) {
-			this.introMessage = introMessage;
-		}
-
-		public boolean doProcess() {
-			return log.isWarnEnabled();
-		}
-
-		public void prepare(SQLWarning warning) {
-			log.debug( introMessage, warning );
-		}
-
-		@Override
-		protected void logWarning(String description, String message) {
-			log.warn( description );
-			log.warn( message );
-		}
-	}
-
-	public static StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler( DEFAULT_WARNING_MSG );
-
-	public static void walkWarnings(SQLWarning warning, WarningHandler handler) {
-		if ( warning == null || handler.doProcess() ) {
-			return;
-		}
-		handler.prepare( warning );
-		while ( warning != null ) {
-			handler.handleWarning( warning );
-			warning = warning.getNextWarning();
-		}
-	}
-
-	public static void logExceptions(SQLException ex) {
-		logExceptions(ex, null);
-	}
-
-	public static void logExceptions(SQLException ex, String message) {
-		if ( log.isErrorEnabled() ) {
-			if ( log.isDebugEnabled() ) {
-				message = StringHelper.isNotEmpty(message) ? message : DEFAULT_EXCEPTION_MSG;
-				log.debug( message, ex );
-			}
-			while (ex != null) {
-				StringBuffer buf = new StringBuffer(30)
-						.append( "SQL Error: " )
-				        .append( ex.getErrorCode() )
-				        .append( ", SQLState: " )
-				        .append( ex.getSQLState() );
-				log.warn( buf.toString() );
-				log.error( ex.getMessage() );
-				ex = ex.getNextException();
-			}
-		}
-	}
-}
-
-
-
-
-
-
--- a/hibernate-core/src/main/java/org/hibernate/util/JTAHelper.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
-package org.hibernate.util;
-
-import javax.transaction.Status;
-import javax.transaction.SystemException;
-import javax.transaction.TransactionManager;
-
-import org.hibernate.TransactionException;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.service.jta.platform.spi.JtaPlatform;
-
-/**
- * @author Gavin King
- */
-public final class JTAHelper {
-
-	private JTAHelper() {}
-
-	public static boolean isRollback(int status) {
-		return status==Status.STATUS_MARKED_ROLLBACK ||
-		       status==Status.STATUS_ROLLING_BACK ||
-		       status==Status.STATUS_ROLLEDBACK;
-	}
-
-	public static boolean isInProgress(int status) {
-		return status==Status.STATUS_ACTIVE ||
-		       status==Status.STATUS_MARKED_ROLLBACK;
-	}
-
-	/**
-	 * Return true if a JTA transaction is in progress
-	 * and false in *every* other cases (including in a JDBC transaction).
-	 */
-	public static boolean isTransactionInProgress(SessionFactoryImplementor factory) {
-		TransactionManager tm = factory.getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
-		try {
-			return tm != null && isTransactionInProgress( tm.getTransaction() );
-		}
-		catch (SystemException se) {
-			throw new TransactionException( "could not obtain JTA Transaction", se );
-		}
-	}
-
-	public static boolean isTransactionInProgress(javax.transaction.Transaction tx) throws SystemException {
-		return tx != null && JTAHelper.isInProgress( tx.getStatus() );
-	}
-
-	public static boolean isMarkedForRollback(int status) {
-		return status == Status.STATUS_MARKED_ROLLBACK;
-	}
-
-	public static boolean isMarkedForRollback(javax.transaction.Transaction tx) throws SystemException {
-		return isMarkedForRollback( tx.getStatus() );
-	}
-}
--- a/hibernate-core/src/test/java/org/hibernate/cache/QueryKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/cache/QueryKeyTest.java
-import org.hibernate.HibernateException;
-import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.type.SerializationException;
-import org.hibernate.util.SerializationHelper;
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/AlternativeNamingStrategy.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/AlternativeNamingStrategy.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/ImageTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/ImageTest.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-		if (!ArrayHelper.isEquals(val1, val2)) {
+		if (!ArrayHelper.isEquals( val1, val2 )) {
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/TextTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/lob/TextTest.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
-		if (!ArrayHelper.isEquals(val1, val2)) {
+		if (!ArrayHelper.isEquals( val1, val2 )) {
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/JPAOverridenAnnotationReaderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/JPAOverridenAnnotationReaderTest.java
+import org.hibernate.internal.util.xml.XMLHelper;
-import org.hibernate.util.XMLHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/XMLContextTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/XMLContextTest.java
-import org.hibernate.util.XMLHelper;
+import org.hibernate.internal.util.xml.XMLHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/tuplizer/DynamicInstantiator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/tuplizer/DynamicInstantiator.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/CacheableFileTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/CacheableFileTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/ConfigurationSerializationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/ConfigurationSerializationTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/backref/map/compkey/BackrefCompositeMapKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/backref/map/compkey/BackrefCompositeMapKeyTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/connections/ConnectionManagementTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/connections/ConnectionManagementTestCase.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
-		dc = (DetachedCriteria) SerializationHelper.deserialize(bytes);
+		dc = (DetachedCriteria) SerializationHelper.deserialize( bytes );
--- a/hibernate-core/src/test/java/org/hibernate/test/dialect/unit/lockhint/AbstractLockHintTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dialect/unit/lockhint/AbstractLockHintTest.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer/MyEntityInstantiator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer/MyEntityInstantiator.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityInstantiator.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dynamicentity/tuplizer2/MyEntityInstantiator.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/basic/Dom4jTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/entitymode/dom4j/basic/Dom4jTest.java
-import org.hibernate.util.XMLHelper;
+import org.hibernate.internal.util.xml.XMLHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/exception/SQLExceptionConversionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/exception/SQLExceptionConversionTest.java
-// $Id: SQLExceptionConversionTest.java 11339 2007-03-23 12:51:38Z steve.ebersole@jboss.com $
-import java.sql.SQLException;
-import org.hibernate.JDBCException;
-import org.hibernate.util.JDBCExceptionReporter;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingOrderByTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingOrderByTest.java
-import org.hibernate.HibernateException;
-import org.hibernate.Query;
-import org.hibernate.QueryException;
-import org.hibernate.ScrollableResults;
-import org.hibernate.TypeMismatchException;
-import org.hibernate.dialect.DB2Dialect;
-import org.hibernate.dialect.HSQLDialect;
-import org.hibernate.dialect.IngresDialect;
-import org.hibernate.dialect.MySQLDialect;
-import org.hibernate.dialect.Oracle8iDialect;
-import org.hibernate.dialect.PostgreSQLDialect;
-import org.hibernate.dialect.SQLServerDialect;
-import org.hibernate.dialect.Sybase11Dialect;
-import org.hibernate.dialect.SybaseASE15Dialect;
-import org.hibernate.dialect.SybaseAnywhereDialect;
-import org.hibernate.dialect.SybaseDialect;
-import org.hibernate.hql.ast.QuerySyntaxException;
-import org.hibernate.persister.entity.DiscriminatorType;
-import org.hibernate.stat.QueryStatistics;
-import org.hibernate.transform.DistinctRootEntityResultTransformer;
-import org.hibernate.transform.Transformers;
-import org.hibernate.type.ComponentType;
-import org.hibernate.type.ManyToOneType;
-import org.hibernate.type.Type;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/QueryTranslatorTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/QueryTranslatorTestCase.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-		result.put( "=", new Integer( StringHelper.countUnquoted(sql, '=') ) );
+		result.put( "=", new Integer( StringHelper.countUnquoted( sql, '=' ) ) );
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/TypeInfoTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/TypeInfoTest.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+import org.hibernate.internal.util.collections.IdentityMap;
-import org.hibernate.util.IdentityMap;
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/lock/JPALockTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/lock/JPALockTest.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+import org.hibernate.internal.util.compare.EqualsHelper;
-import org.hibernate.util.EqualsHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+import org.hibernate.internal.util.SerializationHelper;
+import org.hibernate.internal.util.collections.JoinedIterator;
-import org.hibernate.util.JoinedIterator;
-import org.hibernate.util.SerializationHelper;
-		Session s3 = (Session) SerializationHelper.deserialize( SerializationHelper.serialize(s2) );
+		Session s3 = (Session) SerializationHelper.deserialize( SerializationHelper.serialize( s2 ) );
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/LegacyTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/LegacyTestCase.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/BlobLocatorTest.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
-		entity.setBlobLocator( s.getLobHelper().createBlob( empty ) );
-		s.getTransaction().commit();
-		s.close();
-
-		s = openSession();
-		s.beginTransaction();
+		entity.setBlobLocator( s.getLobHelper().createBlob( empty ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
-		if ( entity.getBlobLocator() != null) {
-			assertEquals( empty.length, entity.getBlobLocator().length() );
+		if ( entity.getBlobLocator() != null) {
+			assertEquals( empty.length, entity.getBlobLocator().length() );
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/ClobLocatorTest.java
-import org.hibernate.dialect.H2Dialect;
-import org.hibernate.type.descriptor.java.DataHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.type.descriptor.java.DataHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/lob/LongByteArrayTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lob/LongByteArrayTest.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/mappingexception/MappingExceptionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/mappingexception/MappingExceptionTest.java
+import org.hibernate.internal.util.ConfigHelper;
-import org.hibernate.util.ConfigHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/AbstractOperationTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/AbstractOperationTestCase.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/orphan/OrphanTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/orphan/OrphanTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
-		prod = (Product) SerializationHelper.clone(prod);
+		prod = (Product) SerializationHelper.clone( prod );
--- a/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-				return ReflectHelper.getConstructor( Student.class, new Type[] { Hibernate.LONG, studentNametype } );
+				return ReflectHelper.getConstructor( Student.class, new Type[] {Hibernate.LONG, studentNametype} );
--- a/hibernate-core/src/test/java/org/hibernate/test/readonly/ReadOnlyCriteriaQueryTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/readonly/ReadOnlyCriteriaQueryTest.java
+import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.util.SerializationHelper;
-		dc = (DetachedCriteria) SerializationHelper.deserialize(bytes);
+		dc = (DetachedCriteria) SerializationHelper.deserialize( bytes );
--- a/hibernate-core/src/test/java/org/hibernate/test/sql/hand/custom/CustomSQLTestSupport.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/sql/hand/custom/CustomSQLTestSupport.java
-import org.hibernate.util.ArrayHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/sql/hand/query/NativeSQLQueriesTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/sql/hand/query/NativeSQLQueriesTest.java
+import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.util.ArrayHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/stateless/fetching/StatelessSessionFetchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/stateless/fetching/StatelessSessionFetchingTest.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/test/tm/CMTTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/tm/CMTTest.java
-import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.util.SerializationHelper;
-import java.sql.Connection;
--- a/hibernate-core/src/test/java/org/hibernate/test/util/StringHelperTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/util/StringHelperTest.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/annotations/HibernateTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/annotations/HibernateTestCase.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
--- a/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/type/TypeTest.java
-import org.hibernate.util.ArrayHelper;
-import org.hibernate.util.SerializationHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.internal.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
+import org.hibernate.internal.util.SerializationHelper;
--- a/hibernate-core/src/test/java/org/hibernate/util/StringHelperTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/util/StringHelperTest.java
+import org.hibernate.internal.util.StringHelper;
+
-	private static final String STRING_HELPER_FQN = "org.hibernate.util.StringHelper";
+	private static final String STRING_HELPER_FQN = "org.hibernate.internal.util.StringHelper";
-		assertEquals( "o.h.u.StringHelper", StringHelper.collapse( STRING_HELPER_FQN ) );
+		assertEquals( "o.h.i.u.StringHelper", StringHelper.collapse( STRING_HELPER_FQN ) );
-		assertEquals( "util.StringHelper", StringHelper.partiallyUnqualify( STRING_HELPER_FQN, BASE_PACKAGE ) );
+		assertEquals( "internal.util.StringHelper", StringHelper.partiallyUnqualify( STRING_HELPER_FQN, BASE_PACKAGE ) );
-		assertEquals( "o.h.util.StringHelper", StringHelper.collapseQualifierBase( STRING_HELPER_FQN, BASE_PACKAGE ) );
+		assertEquals( "o.h.internal.util.StringHelper", StringHelper.collapseQualifierBase( STRING_HELPER_FQN, BASE_PACKAGE ) );
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/EhCacheProvider.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/EhCacheProvider.java
-import org.hibernate.util.ConfigHelper;
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.ConfigHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/CurrentEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/CurrentEntityManagerImpl.java
-import java.util.Map;
-import javax.persistence.PersistenceContextType;
-import javax.persistence.TypedQuery;
-import javax.persistence.spi.PersistenceUnitTransactionType;
-
-import org.hibernate.util.JTAHelper;
+import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
+import org.hibernate.service.jta.platform.spi.JtaPlatform;
+
+import javax.persistence.PersistenceContextType;
+import javax.persistence.spi.PersistenceUnitTransactionType;
+import javax.transaction.TransactionManager;
+import java.util.Map;
+		TransactionManager tm = sfi.getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
-		if ( !JTAHelper.isTransactionInProgress( sfi ) ) {
+		if ( ! JtaStatusHelper.isActive( tm ) ) {
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.internal.util.xml.OriginImpl;
+import org.hibernate.internal.util.xml.XmlDocument;
-import org.hibernate.util.CollectionHelper;
-import org.hibernate.util.ReflectHelper;
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.xml.MappingReader;
-import org.hibernate.util.xml.OriginImpl;
-import org.hibernate.util.xml.XmlDocument;
+import org.hibernate.internal.util.xml.MappingReader;
-				Class interceptorClass = ReflectHelper.classForName( sessionInterceptorClassname, Ejb3Configuration.class );
+				Class interceptorClass = ReflectHelper.classForName(
+						sessionInterceptorClassname, Ejb3Configuration.class
+				);
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/CriteriaQueryCompiler.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/CriteriaQueryCompiler.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.util.StringHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/Callback.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/Callback.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
-import org.hibernate.util.IdentityMap;
+import org.hibernate.internal.util.collections.IdentityMap;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
-import org.hibernate.util.IdentityMap;
+import org.hibernate.internal.util.collections.IdentityMap;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/ListenerCallback.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/ListenerCallback.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/AbstractAttribute.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/AbstractAttribute.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/JarVisitorFactory.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/JarVisitorFactory.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/NativeScanner.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/NativeScanner.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/PersistenceXmlLoader.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/PersistenceXmlLoader.java
-import org.hibernate.util.StringHelper;
+import org.hibernate.internal.util.StringHelper;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/ProgrammaticConfTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/ProgrammaticConfTest.java
-import org.hibernate.util.ConfigHelper;
+import org.hibernate.internal.util.ConfigHelper;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
-import org.hibernate.util.ConfigHelper;
+import org.hibernate.internal.util.ConfigHelper;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/EntityInstantiator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/EntityInstantiator.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
- * @author Hern&aacute;n Chanfreau
+ * @author Hern&aacute;n Chanfreau
-        // Put entity on entityName cache after mapping it from the map representation
-        versionsReader.getFirstLevelCache().putOnEntityNameCache(primaryKey, revision, ret, entityName);
-        
+        // Put entity on entityName cache after mapping it from the map representation
+        versionsReader.getFirstLevelCache().putOnEntityNameCache(primaryKey, revision, ret, entityName);
+        
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
-import org.hibernate.util.ReflectHelper;
+import org.hibernate.internal.util.ReflectHelper;
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/id/EmbeddedIdMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/id/EmbeddedIdMapper.java
+import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.util.ReflectHelper;
-            Object subObj = ReflectHelper.getDefaultConstructor(getter.getReturnType()).newInstance();
+            Object subObj = ReflectHelper.getDefaultConstructor( getter.getReturnType() ).newInstance();
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
-import org.hibernate.util.ComparableComparator;
+import org.hibernate.internal.util.compare.ComparableComparator;
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
-import org.hibernate.cfg.Environment;
+import org.hibernate.internal.util.compare.ComparableComparator;
-import org.hibernate.util.ComparableComparator;
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
-import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.util.ComparableComparator;
+import org.hibernate.internal.util.compare.ComparableComparator;
--- a/hibernate-oscache/src/main/java/org/hibernate/cache/OSCacheProvider.java
+++ b/hibernate-oscache/src/main/java/org/hibernate/cache/OSCacheProvider.java
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-		String cron = OSCACHE_PROPERTIES.getProperty( StringHelper.qualify(region, OSCACHE_CRON) );
+		String cron = OSCACHE_PROPERTIES.getProperty( StringHelper.qualify( region, OSCACHE_CRON ) );
--- a/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
+++ b/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
+import org.hibernate.internal.util.ConfigHelper;
+import org.hibernate.internal.util.StringHelper;
-import org.hibernate.util.StringHelper;
-import org.hibernate.util.ConfigHelper;

Lines added containing method: 1256. Lines removed containing method: 1610. Tot = 2866
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
20/After/ HHH-6155  ff74ceaa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
--- a/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- *
- */
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ *
+ */
-import java.util.Properties;
-import org.hibernate.cache.access.AccessType;
-import org.hibernate.cfg.Settings;
-
-/**
- * Contract for building second level cache regions.
- * <p/>
- * Implementors should define a constructor in one of two forms:<ul>
- * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
- * <li>MyRegionFactoryImpl()</li>
- * </ul>
- * Use the first when we need to read config properties prior to
- * {@link #start} being called.  For an example, have a look at
- * {@link org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge}
- * where we need the properties in order to determine which legacy 
- * {@link CacheProvider} to use so that we can answer the
- * {@link #isMinimalPutsEnabledByDefault()} question for the
- * {@link org.hibernate.cfg.SettingsFactory}.
- *
- * @author Steve Ebersole
- */
-public interface RegionFactory {
-
-	/**
-	 * Lifecycle callback to perform any necessary initialization of the
-	 * underlying cache implementation(s).  Called exactly once during the
-	 * construction of a {@link org.hibernate.impl.SessionFactoryImpl}.
-	 *
-	 * @param settings The settings in effect.
-	 * @param properties The defined cfg properties
-	 * @throws CacheException Indicates problems starting the L2 cache impl;
-	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
-	 * building.
-	 */
-	public void start(Settings settings, Properties properties) throws CacheException;
-
-	/**
-	 * Lifecycle callback to perform any necessary cleanup of the underlying
-	 * cache implementation(s).  Called exactly once during
-	 * {@link org.hibernate.SessionFactory#close}.
-	 */
-	public void stop();
-
-	/**
-	 * By default should we perform "minimal puts" when using this second
-	 * level cache implementation?
-	 *
-	 * @return True if "minimal puts" should be performed by default; false
-	 * otherwise.
-	 */
-	public boolean isMinimalPutsEnabledByDefault();
-
-	/**
-	 * Get the default access type for {@link EntityRegion entity} and
-	 * {@link CollectionRegion collection} regions.
-	 *
-	 * @return This factory's default access type.
-	 */
-	public AccessType getDefaultAccessType();
-
-	/**
-	 * Generate a timestamp.
-	 * <p/>
-	 * This is generally used for cache content locking/unlocking purposes
-	 * depending upon the access-strategy being used.
-	 *
-	 * @return The generated timestamp.
-	 */
-	public long nextTimestamp();
-
-	/**
-	 * Build a cache region specialized for storing entity data.
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @param metadata Information regarding the type of data to be cached
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
-
-	/**
-	 * Build a cache region specialized for storing collection data.
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @param metadata Information regarding the type of data to be cached
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
-
-	/**
-	 * Build a cache region specialized for storing query results
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
-
-	/**
-	 * Build a cache region specialized for storing update-timestamps data.
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
-}
+import java.util.Properties;
+import org.hibernate.cache.access.AccessType;
+import org.hibernate.cfg.Settings;
+
+/**
+ * Contract for building second level cache regions.
+ * <p/>
+ * Implementors should define a constructor in one of two forms:<ul>
+ * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
+ * <li>MyRegionFactoryImpl()</li>
+ * </ul>
+ * Use the first when we need to read config properties prior to
+ * {@link #start} being called.  For an example, have a look at
+ * {@link org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge}
+ * where we need the properties in order to determine which legacy 
+ * {@link CacheProvider} to use so that we can answer the
+ * {@link #isMinimalPutsEnabledByDefault()} question for the
+ * {@link org.hibernate.cfg.SettingsFactory}.
+ *
+ * @author Steve Ebersole
+ */
+public interface RegionFactory {
+
+	/**
+	 * Lifecycle callback to perform any necessary initialization of the
+	 * underlying cache implementation(s).  Called exactly once during the
+	 * construction of a {@link org.hibernate.internal.SessionFactoryImpl}.
+	 *
+	 * @param settings The settings in effect.
+	 * @param properties The defined cfg properties
+	 * @throws CacheException Indicates problems starting the L2 cache impl;
+	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
+	 * building.
+	 */
+	public void start(Settings settings, Properties properties) throws CacheException;
+
+	/**
+	 * Lifecycle callback to perform any necessary cleanup of the underlying
+	 * cache implementation(s).  Called exactly once during
+	 * {@link org.hibernate.SessionFactory#close}.
+	 */
+	public void stop();
+
+	/**
+	 * By default should we perform "minimal puts" when using this second
+	 * level cache implementation?
+	 *
+	 * @return True if "minimal puts" should be performed by default; false
+	 * otherwise.
+	 */
+	public boolean isMinimalPutsEnabledByDefault();
+
+	/**
+	 * Get the default access type for {@link EntityRegion entity} and
+	 * {@link CollectionRegion collection} regions.
+	 *
+	 * @return This factory's default access type.
+	 */
+	public AccessType getDefaultAccessType();
+
+	/**
+	 * Generate a timestamp.
+	 * <p/>
+	 * This is generally used for cache content locking/unlocking purposes
+	 * depending upon the access-strategy being used.
+	 *
+	 * @return The generated timestamp.
+	 */
+	public long nextTimestamp();
+
+	/**
+	 * Build a cache region specialized for storing entity data.
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @param metadata Information regarding the type of data to be cached
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
+
+	/**
+	 * Build a cache region specialized for storing collection data.
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @param metadata Information regarding the type of data to be cached
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
+
+	/**
+	 * Build a cache region specialized for storing query results
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
+
+	/**
+	 * Build a cache region specialized for storing update-timestamps data.
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
+}
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
--- a/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
--- a/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
- * @see org.hibernate.impl.SessionFactoryImpl
+ * @see org.hibernate.internal.SessionFactoryImpl
--- a/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
-import java.sql.Connection;
-import org.hibernate.ConnectionReleaseMode;
+
-import org.hibernate.MultiTenancyStrategy;
-import org.hibernate.service.ServiceRegistry;
- * @see org.hibernate.impl.SessionFactoryImpl
+ * @see org.hibernate.internal.SessionFactoryImpl
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
-import org.hibernate.event.EventListeners;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
-	 * name, with values corresponding to the {@link org.hibernate.impl.FilterImpl}
+	 * name, with values corresponding to the {@link org.hibernate.internal.FilterImpl}
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
-import org.hibernate.impl.IteratorImpl;
+import org.hibernate.internal.IteratorImpl;
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CollectionFilterImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/ConnectionObserverStatsBridge.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/ConnectionObserverStatsBridge.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/FilterHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FilterHelper.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/FilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/QueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/QueryImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/ScrollableResultsImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObjectFactory.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObserverChain.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
-import org.hibernate.internal.CoreMessageLogger;
--- a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/TransactionEnvironmentImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
-package org.hibernate.impl;
+package org.hibernate.internal;
--- a/hibernate-core/src/main/java/org/hibernate/impl/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/internal/package.html
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
-  ~
-	This package contains implementations of the
-	central Hibernate APIs, especially the
-	Hibernate session.
+    An internal package containing mostly implementations of central Hibernate APIs of the
+    {@link org.hibernate} package.
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
-import org.hibernate.impl.SessionFactoryObjectFactory;
+import org.hibernate.internal.SessionFactoryObjectFactory;
--- a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
-import org.hibernate.impl.SessionFactoryObjectFactory;
+import org.hibernate.internal.SessionFactoryObjectFactory;
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
-import org.hibernate.impl.FetchingScrollableResultsImpl;
-import org.hibernate.impl.ScrollableResultsImpl;
+import org.hibernate.internal.FetchingScrollableResultsImpl;
+import org.hibernate.internal.ScrollableResultsImpl;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
-import org.hibernate.type.ComponentType;
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
-import org.hibernate.impl.IteratorImpl;
+import org.hibernate.internal.IteratorImpl;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
-import org.hibernate.impl.FilterHelper;
+import org.hibernate.internal.FilterHelper;
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
-import org.hibernate.impl.FilterHelper;
+import org.hibernate.internal.FilterHelper;
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
--- a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
--- a/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
-import org.hibernate.impl.SessionImpl;
--- a/hibernate-core/src/test/java/org/hibernate/test/immutable/entitywithmutablecollection/AbstractEntityWithOneToManyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/immutable/entitywithmutablecollection/AbstractEntityWithOneToManyTest.java
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
--- a/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
--- a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
-		return org.hibernate.impl.QueryImpl.class.isInstance( query );
+		return org.hibernate.internal.QueryImpl.class.isInstance( query );
-		( (org.hibernate.impl.QueryImpl) query ).getLockOptions().setAliasSpecificLockMode( alias, lockMode );
+		( (org.hibernate.internal.QueryImpl) query ).getLockOptions().setAliasSpecificLockMode( alias, lockMode );
-		( (org.hibernate.impl.QueryImpl) query ).getLockOptions().setLockMode(
+		( (org.hibernate.internal.QueryImpl) query ).getLockOptions().setLockMode(
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/criteria/basic/ExpressionsTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/criteria/basic/ExpressionsTest.java
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/cacheable/cachemodes/SharedCacheModesTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/cacheable/cachemodes/SharedCacheModesTest.java
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
-		org.hibernate.impl.QueryImpl hqlQuery = (org.hibernate.impl.QueryImpl) jpaQuery.getHibernateQuery();
+		org.hibernate.internal.QueryImpl hqlQuery = (org.hibernate.internal.QueryImpl) jpaQuery.getHibernateQuery();
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
-import org.hibernate.event.EventListeners;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;

Lines added containing method: 262. Lines removed containing method: 278. Tot = 540
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
