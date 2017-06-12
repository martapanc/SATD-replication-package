diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
index d31a6c2bb7..d374505df2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
@@ -1,132 +1,133 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Provides centralized normalization of how database object names are handled.
  *
  * @author Steve Ebersole
  */
 public abstract class ObjectNameNormalizer {
 
 	/**
 	 * Helper contract for dealing with {@link NamingStrategy} in different situations.
 	 */
 	public static interface NamingStrategyHelper {
 		/**
 		 * Called when the user supplied no explicit name/identifier for the given database object.
 		 *
 		 * @param strategy The naming strategy in effect
 		 *
 		 * @return The implicit name
 		 */
 		public String determineImplicitName(NamingStrategy strategy);
 
 		/**
 		 * Called when the user has supplied an explicit name for the database object.
 		 *
 		 * @param strategy The naming strategy in effect
 		 * @param name The {@link ObjectNameNormalizer#normalizeIdentifierQuoting normalized} explicit object name.
 		 *
 		 * @return The strategy-handled name.
 		 */
 		public String handleExplicitName(NamingStrategy strategy, String name);
 	}
 
 	/**
 	 * Performs the actual contract of normalizing a database name.
 	 *
 	 * @param explicitName The name the user explicitly gave for the database object.
 	 * @param helper The {@link NamingStrategy} helper.
 	 *
 	 * @return The normalized identifier.
 	 */
 	public String normalizeDatabaseIdentifier(final String explicitName, NamingStrategyHelper helper) {
+		String objectName = null;
 		// apply naming strategy
 		if ( StringHelper.isEmpty( explicitName ) ) {
 			// No explicit name given, so allow the naming strategy the chance
 			//    to determine it based on the corresponding mapped java name
-			final String objectName = helper.determineImplicitName( getNamingStrategy() );
-			// Conceivable that the naming strategy could return a quoted identifier, or
-			//    that user enabled <delimited-identifiers/>
-			return normalizeIdentifierQuoting( objectName );
+			objectName = helper.determineImplicitName( getNamingStrategy() );
 		}
 		else {
 			// An explicit name was given:
 			//    in some cases we allow the naming strategy to "fine tune" these, but first
 			//    handle any quoting for consistent handling in naming strategies
-			String objectName = normalizeIdentifierQuoting( explicitName );
+			objectName = normalizeIdentifierQuoting( explicitName );
 			objectName = helper.handleExplicitName( getNamingStrategy(), objectName );
 			return normalizeIdentifierQuoting( objectName );
 		}
+        // Conceivable that the naming strategy could return a quoted identifier, or
+			//    that user enabled <delimited-identifiers/>
+		return normalizeIdentifierQuoting( objectName );
 	}
 
 	/**
 	 * Allow normalizing of just the quoting aspect of identifiers.  This is useful for
 	 * schema and catalog in terms of initially making this public.
 	 * <p/>
 	 * This implements the rules set forth in JPA 2 (section "2.13 Naming of Database Objects") which
 	 * states that the double-quote (") is the character which should be used to denote a <tt>quoted
 	 * identifier</tt>.  Here, we handle recognizing that and converting it to the more elegant
 	 * bactick (`) approach used in Hibernate..  Additionally we account for applying what JPA2 terms
 	 *  
 	 *
 	 * @param identifier The identifier to be quoting-normalized.
 	 * @return The identifier accounting for any quoting that need be applied.
 	 */
 	public String normalizeIdentifierQuoting(String identifier) {
 		if ( StringHelper.isEmpty( identifier ) ) {
 			return null;
 		}
 
 		// Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
 		if ( identifier.startsWith( "\"" ) && identifier.endsWith( "\"" ) ) {
 			return '`' + identifier.substring( 1, identifier.length() - 1 ) + '`';
 		}
 
 		// If the user has requested "global" use of quoted identifiers, quote this identifier (using back ticks)
 		// if not already
 		if ( isUseQuotedIdentifiersGlobally() && ! ( identifier.startsWith( "`" ) && identifier.endsWith( "`" ) ) ) {
 			return '`' + identifier + '`';
 		}
 
 		return identifier;
 	}
 
 	/**
 	 * Retrieve whether the user requested that all database identifiers be quoted.
 	 *
 	 * @return True if the user requested that all database identifiers be quoted, false otherwise.
 	 */
 	protected abstract boolean isUseQuotedIdentifiersGlobally();
 
 	/**
 	 * Get the current {@link NamingStrategy}.
 	 *
 	 * @return The current {@link NamingStrategy}.
 	 */
 	protected abstract NamingStrategy getNamingStrategy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
index 557f01cf1e..de62b326a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
@@ -1,661 +1,664 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  *
  */
 package org.hibernate.internal.util;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.StringTokenizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 
 	private StringHelper() { /* static methods only - hide constructor */
 	}
 	
 	/*public static boolean containsDigits(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			if ( Character.isDigit( string.charAt(i) ) ) return true;
 		}
 		return false;
 	}*/
 
 	public static int lastIndexOfLetter(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			char character = string.charAt(i);
 			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
 		}
 		return string.length()-1;
 	}
 
 	public static String join(String seperator, String[] strings) {
 		int length = strings.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * strings[0].length() )
 				.append( strings[0] );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( seperator ).append( strings[i] );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
 		StringBuilder buf = new StringBuilder();
 		if ( objects.hasNext() ) buf.append( objects.next() );
 		while ( objects.hasNext() ) {
 			buf.append( seperator ).append( objects.next() );
 		}
 		return buf.toString();
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
 		String[] result = new String[x.length];
 		for ( int i = 0; i < x.length; i++ ) {
 			result[i] = x[i] + sep + y[i];
 		}
 		return result;
 	}
 
 	public static String repeat(String string, int times) {
 		StringBuilder buf = new StringBuilder( string.length() * times );
 		for ( int i = 0; i < times; i++ ) buf.append( string );
 		return buf.toString();
 	}
 
 	public static String repeat(char character, int times) {
 		char[] buffer = new char[times];
 		Arrays.fill( buffer, character );
 		return new String( buffer );
 	}
 
 
 	public static String replace(String template, String placeholder, String replacement) {
 		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String templates[], String placeholder, String replacement) {
 		String[] result = new String[templates.length];
 		for ( int i =0; i<templates.length; i++ ) {
 			result[i] = replace( templates[i], placeholder, replacement );
 		}
 		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
 		return replace( template, placeholder, replacement, wholeWords, false );
 	}
 
 	public static String replace(String template,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		if ( template == null ) {
 			return template;
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			String beforePlaceholder = template.substring( 0, loc );
 			String afterPlaceholder = template.substring( loc + placeholder.length() );
 			return replace( beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary );
 		}
 	}
 
 
 	public static String replace(String beforePlaceholder,
 								 String afterPlaceholder,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		final boolean actuallyReplace =
 				! wholeWords ||
 				afterPlaceholder.length() == 0 ||
 				! Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
 		boolean encloseInParens =
 				actuallyReplace &&
 				encloseInParensIfNecessary &&
 				! ( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
 				! ( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );		
 		StringBuilder buf = new StringBuilder( beforePlaceholder );
 		if ( encloseInParens ) {
 			buf.append( '(' );
 		}
 		buf.append( actuallyReplace ? replacement : placeholder );
 		if ( encloseInParens ) {
 			buf.append( ')' );
 		}
 		buf.append(
 				replace(
 						afterPlaceholder,
 						placeholder,
 						replacement,
 						wholeWords,
 						encloseInParensIfNecessary
 				)
 		);
 		return buf.toString();
 	}
 
 	public static char getLastNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = str.length() - 1 ; i >= 0 ; i-- ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static char getFirstNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = 0 ; i < str.length() ; i++ ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static String replaceOnce(String template, String placeholder, String replacement) {
 		if ( template == null ) {
 			return template; // returnign null!
 		}
         int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			return new StringBuilder( template.substring( 0, loc ) )
 					.append( replacement )
 					.append( template.substring( loc + placeholder.length() ) )
 					.toString();
 		}
 	}
 
 
 	public static String[] split(String seperators, String list) {
 		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
 		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
 		String[] result = new String[ tokens.countTokens() ];
 		int i = 0;
 		while ( tokens.hasMoreTokens() ) {
 			result[i++] = tokens.nextToken();
 		}
 		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( qualifiedName.lastIndexOf(".") + 1 );
 	}
 
 	public static String qualifier(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
 	/**
 	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
 	 * Imagine you have a class named <samp>'org.hibernate.internal.util.StringHelper'</samp>; calling collapse on that
 	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
 	 *
 	 * @param name The name to collapse.
 	 * @return The collapsed name.
 	 */
 	public static String collapse(String name) {
 		if ( name == null ) {
 			return null;
 		}
 		int breakPoint = name.lastIndexOf( '.' );
 		if ( breakPoint < 0 ) {
 			return name;
 		}
 		return collapseQualifier( name.substring( 0, breakPoint ), true ) + name.substring( breakPoint ); // includes last '.'
 	}
 
 	/**
 	 * Given a qualifier, collapse it.
 	 *
 	 * @param qualifier The qualifier to collapse.
 	 * @param includeDots Should we include the dots in the collapsed form?
 	 *
 	 * @return The collapsed form.
 	 */
 	public static String collapseQualifier(String qualifier, boolean includeDots) {
 		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
 		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
 		while ( tokenizer.hasMoreTokens() ) {
 			if ( includeDots ) {
 				collapsed += '.';
 			}
 			collapsed += tokenizer.nextToken().charAt( 0 );
 		}
 		return collapsed;
 	}
 
 	/**
 	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
 	 */
 	public static String partiallyUnqualify(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return name;
 		}
 		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
 	}
 
 	/**
 	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
 	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'o.h.util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
 	 */
 	public static String collapseQualifierBase(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return collapse( name );
 		}
 		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
 		if ( suffix == null ) return columns;
 		String[] qualified = new String[columns.length];
 		for ( int i = 0; i < columns.length; i++ ) {
 			qualified[i] = suffix( columns[i], suffix );
 		}
 		return qualified;
 	}
 
 	private static String suffix(String name, String suffix) {
 		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
 		String trimmed = tfString.trim().toLowerCase();
 		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
 		int len = array.length;
 		if ( len == 0 ) return "";
 		StringBuilder buf = new StringBuilder( len * 12 );
 		for ( int i = 0; i < len - 1; i++ ) {
 			buf.append( array[i] ).append(", ");
 		}
 		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
 		String[] result = new String[]{string};
 		while ( placeholders.hasNext() ) {
 			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
 		}
 		return result;
 	}
 
 	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
 		String[] results = new String[replacements.length * strings.length];
 		int n = 0;
 		for ( int i = 0; i < replacements.length; i++ ) {
 			for ( int j = 0; j < strings.length; j++ ) {
 				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
 			}
 		}
 		return results;
 	}
 
 	public static int countUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null)
 			return 0;
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int count = 0;
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public static int[] locateUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null) {
 			return new int[0];
 		}
 
 		ArrayList locations = new ArrayList( 20 );
 
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				locations.add( indx );
 			}
 		}
 		return ArrayHelper.toIntArray( locations );
 	}
 
 	public static boolean isNotEmpty(String string) {
 		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
 		return string == null || string.length() == 0;
 	}
 
 	public static String qualify(String prefix, String name) {
 		if ( name == null || prefix == null ) {
 			throw new NullPointerException();
 		}
 		return new StringBuilder( prefix.length() + name.length() + 1 )
 				.append(prefix)
 				.append('.')
 				.append(name)
 				.toString();
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) return names;
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		int matchAt = -1;
 		for ( int i = 0; i < string.length(); i++ ) {
 			int curMatch = sqlString.indexOf( string.charAt( i ), startindex );
 			if ( curMatch >= 0 ) {
 				if ( matchAt == -1 ) { // first time we find match!
 					matchAt = curMatch;
 				}
 				else {
 					matchAt = Math.min( matchAt, curMatch );
 				}
 			}
 		}
 		return matchAt;
 	}
 
 	public static String truncate(String string, int length) {
 		if ( string.length() <= length ) {
 			return string;
 		}
 		else {
 			return string.substring( 0, length );
 		}
 	}
 
 	public static String generateAlias(String description) {
 		return generateAliasRoot(description) + '_';
 	}
 
 	/**
 	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
 	 * Loader do <em>not</em> have to use aliases of this form.
 	 *
 	 * @param description The base name (usually an entity-name or collection-role)
 	 * @param unique A uniquing value
 	 *
 	 * @return an alias of the form <samp>foo1_</samp>
 	 */
 	public static String generateAlias(String description, int unique) {
 		return generateAliasRoot(description) +
 			Integer.toString(unique) +
 			'_';
 	}
 
 	/**
 	 * Generates a root alias by truncating the "root name" defined by
 	 * the incoming decription and removing/modifying any non-valid
 	 * alias characters.
 	 *
 	 * @param description The root name from which to generate a root alias.
 	 * @return The generated root alias.
 	 */
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
 
 	/**
 	 * Clean the generated alias by removing any non-alpha characters from the
 	 * beginning.
 	 *
 	 * @param alias The generated alias to be cleaned.
 	 * @return The cleaned alias, stripped of any leading non-alpha characters.
 	 */
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
 
 	public static String unqualifyEntityName(String entityName) {
 		String result = unqualify(entityName);
 		int slashPos = result.indexOf( '/' );
 		if ( slashPos > 0 ) {
 			result = result.substring( 0, slashPos - 1 );
 		}
 		return result;
 	}
 	
 	public static String toUpperCase(String str) {
 		return str==null ? null : str.toUpperCase();
 	}
 	
 	public static String toLowerCase(String str) {
 		return str==null ? null : str.toLowerCase();
 	}
 
 	public static String moveAndToBeginning(String filter) {
 		if ( filter.trim().length()>0 ){
 			filter += " and ";
 			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
 		}
 		return filter;
 	}
 
 	/**
 	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
 	 * @param name The name to check.
 	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
 	public static boolean isQuoted(String name) {
 		return name != null && name.length() != 0 && name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`';
 	}
 
 	/**
 	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
 	 * return name.
 	 *
 	 * @param name The name to quote.
 	 * @return The quoted version.
 	 */
 	public static String quote(String name) {
-		if ( name == null || name.length() == 0 || isQuoted( name ) ) {
+		if ( isEmpty( name ) || isQuoted( name ) ) {
 			return name;
 		}
-		else {
-			return new StringBuilder( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
-		}
+// Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
+        else if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) {
+            name = name.substring( 1, name.length() - 1 );
+        }
+
+		return new StringBuilder( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		if ( isQuoted( name ) ) {
 			return name.substring( 1, name.length() - 1 );
 		}
 		else {
 			return name;
 		}
 	}
 
 	/**
 	 * Determine if the given name is quoted.  It is considered quoted if either:
 	 * <ol>
 	 * <li>starts AND ends with backticks (`)</li>
 	 * <li>starts with dialect-specified {@link org.hibernate.dialect.Dialect#openQuote() open-quote}
 	 * 		AND ends with dialect-specified {@link org.hibernate.dialect.Dialect#closeQuote() close-quote}</li>
 	 * </ol>
 	 *
 	 * @param name The name to check
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return True if quoted, false otherwise
 	 */
 	public static boolean isQuoted(String name, Dialect dialect) {
 		return name != null && name.length() != 0
 				&& ( name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`'
 				|| name.charAt( 0 ) == dialect.openQuote() && name.charAt( name.length() - 1 ) == dialect.closeQuote() );
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param name The name to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name, Dialect dialect) {
 		if ( isQuoted( name, dialect ) ) {
 			return name.substring( 1, name.length() - 1 );
 		}
 		else {
 			return name;
 		}
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param names The names to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted versions.
 	 */
 	public static String[] unquote(String[] names, Dialect dialect) {
 		if ( names == null ) {
 			return null;
 		}
 		String[] unquoted = new String[ names.length ];
 		for ( int i = 0; i < names.length; i++ ) {
 			unquoted[i] = unquote( names[i], dialect );
 		}
 		return unquoted;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index 8f3f861b4f..cd3f5acc2d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
@@ -1,91 +1,92 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 
 package org.hibernate.metamodel;
 
 import java.util.Map;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 
 /**
  * @author Steve Ebersole
  */
 public interface Metadata {
 	/**
 	 * Exposes the options used to produce a {@link Metadata} instance.
 	 */
 	public static interface Options {
 		public SourceProcessingOrder getSourceProcessingOrder();
 		public NamingStrategy getNamingStrategy();
 		public SharedCacheMode getSharedCacheMode();
 		public AccessType getDefaultAccessType();
 		public boolean useNewIdentifierGenerators();
+        public boolean isGloballyQuotedIdentifiers();
 		public String getDefaultSchemaName();
 		public String getDefaultCatalogName();
 	}
 
 	public Options getOptions();
 
 	public SessionFactoryBuilder getSessionFactoryBuilder();
 
 	public SessionFactory buildSessionFactory();
 
 	public Iterable<EntityBinding> getEntityBindings();
 
 	public EntityBinding getEntityBinding(String entityName);
 
 	/**
 	 * Get the "root" entity binding
 	 * @param entityName
 	 * @return the "root entity binding; simply returns entityBinding if it is the root entity binding
 	 */
 	public EntityBinding getRootEntityBinding(String entityName);
 
 	public Iterable<PluralAttributeBinding> getCollectionBindings();
 
 	public Iterable<TypeDef> getTypeDefinitions();
 
 	public Iterable<FilterDefinition> getFilterDefinitions();
 
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions();
 
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();
 
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions();
 
 	public Iterable<Map.Entry<String, String>> getImports();
 
 	public IdGenerator getIdGenerator(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
index f677e3adb1..141f295d72 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
@@ -1,144 +1,144 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.relational;
 
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Models an identifier (name).
  *
  * @author Steve Ebersole
  */
 public class Identifier {
 	private final String name;
 	private final boolean isQuoted;
 
 	/**
 	 * Means to generate an {@link Identifier} instance from its simple name
 	 *
 	 * @param name The name
 	 *
 	 * @return
 	 */
 	public static Identifier toIdentifier(String name) {
-		if ( name == null ) {
+		if ( StringHelper.isEmpty( name ) ) {
 			return null;
 		}
 		final String trimmedName = name.trim();
 		if ( isQuoted( trimmedName ) ) {
 			final String bareName = trimmedName.substring( 1, trimmedName.length() - 1 );
 			return new Identifier( bareName, true );
 		}
 		else {
 			return new Identifier( trimmedName, false );
 		}
 	}
 
 	private static boolean isQuoted(String name) {
 		return name.startsWith( "`" ) && name.endsWith( "`" );
 	}
 
 	/**
 	 * Constructs an identifier instance.
 	 *
 	 * @param name The identifier text.
 	 * @param quoted Is this a quoted identifier?
 	 */
 	public Identifier(String name, boolean quoted) {
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new IllegalIdentifierException( "Identifier text cannot be null" );
 		}
 		if ( isQuoted( name ) ) {
 			throw new IllegalIdentifierException( "Identifier text should not contain quote markers (`)" );
 		}
 		this.name = name;
 		this.isQuoted = quoted;
 	}
 
 	/**
 	 * Get the identifiers name (text)
 	 *
 	 * @return The name
 	 */
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * Is this a quoted identifier>
 	 *
 	 * @return True if this is a quote identifier; false otherwise.
 	 */
 	public boolean isQuoted() {
 		return isQuoted;
 	}
 
 	/**
 	 * If this is a quoted identifier, then return the identifier name
 	 * enclosed in dialect-specific open- and end-quotes; otherwise,
 	 * simply return the identifier name.
 	 *
 	 * @param dialect
 	 * @return if quoted, identifier name enclosed in dialect-specific
 	 *         open- and end-quotes; otherwise, the identifier name.
 	 */
 	public String encloseInQuotesIfQuoted(Dialect dialect) {
 		return isQuoted ?
 				new StringBuilder( name.length() + 2 )
 						.append( dialect.openQuote() )
 						.append( name )
 						.append( dialect.closeQuote() )
 						.toString() :
 				name;
 	}
 
 	@Override
 	public String toString() {
 		return isQuoted
 				? '`' + getName() + '`'
 				: getName();
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		Identifier that = (Identifier) o;
 
 		return isQuoted == that.isQuoted
 				&& name.equals( that.name );
 	}
 
 	@Override
 	public int hashCode() {
 		return name.hashCode();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ColumnRelationalState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ColumnRelationalState.java
index 17687d1d92..ae141bcbd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ColumnRelationalState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ColumnRelationalState.java
@@ -1,60 +1,62 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.relational.state;
 
 import java.util.Set;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.relational.Size;
 
 /**
  * @author Gail Badner
  */
 public interface ColumnRelationalState extends SimpleValueRelationalState {
 	NamingStrategy getNamingStrategy();
 
+    boolean isGloballyQuotedIdentifiers();
+
 	String getExplicitColumnName();
 
 	boolean isUnique();
 
 	Size getSize();
 
 	boolean isNullable();
 
 	String getCheckCondition();
 
 	String getDefault();
 
 	String getSqlType();
 
 	String getCustomWriteFragment();
 
 	String getCustomReadFragment();
 
 	String getComment();
 
 	Set<String> getUniqueKeys();
 
 	Set<String> getIndexes();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ValueCreator.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ValueCreator.java
index 3c5963c5b2..3e97cce8cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ValueCreator.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/state/ValueCreator.java
@@ -1,127 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.relational.state;
 
 import org.hibernate.MappingException;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.relational.Value;
 
 /**
  * @author Gail Badner
  */
 public class ValueCreator {
 
 	public static Column createColumn(TableSpecification table,
 									  String attributeName,
 									  ColumnRelationalState state,
 									  boolean forceNonNullable,
 									  boolean forceUnique
 	) {
 		final String explicitName = state.getExplicitColumnName();
 		final String logicalColumnName = state.getNamingStrategy().logicalColumnName( explicitName, attributeName );
-		final String columnName =
+		String columnName =
 				explicitName == null ?
 						state.getNamingStrategy().propertyToColumnName( attributeName ) :
 						state.getNamingStrategy().columnName( explicitName );
 // todo : find out the purpose of these logical bindings
 //			mappings.addColumnBinding( logicalColumnName, column, table );
 
 		if ( columnName == null ) {
 			throw new IllegalArgumentException( "columnName must be non-null." );
 		}
+        if( state.isGloballyQuotedIdentifiers()){
+            columnName = StringHelper.quote( columnName );
+        }
 		Column value = table.getOrCreateColumn( columnName );
 		value.initialize( state, forceNonNullable, forceUnique );
 		return value;
 	}
 
 	public static DerivedValue createDerivedValue(TableSpecification table,
 												  DerivedValueRelationalState state) {
 		return table.getOrCreateDerivedValue( state.getFormula() );
 	}
 
 	public static SimpleValue createSimpleValue(TableSpecification table,
 												String attributeName,
 												SimpleValueRelationalState state,
 												boolean forceNonNullable,
 												boolean forceUnique
 	) {
 		if ( state instanceof ColumnRelationalState ) {
 			ColumnRelationalState columnRelationalState = ColumnRelationalState.class.cast( state );
 			return createColumn( table, attributeName, columnRelationalState, forceNonNullable, forceUnique );
 		}
 		else if ( state instanceof DerivedValueRelationalState ) {
 			return createDerivedValue( table, DerivedValueRelationalState.class.cast( state ) );
 		}
 		else {
 			throw new MappingException( "unknown relational state:" + state.getClass().getName() );
 		}
 	}
 
 	public static Tuple createTuple(TableSpecification table,
 									String attributeName,
 									TupleRelationalState state,
 									boolean forceNonNullable,
 									boolean forceUnique
 	) {
 		Tuple tuple = table.createTuple( "[" + attributeName + "]" );
 		for ( SimpleValueRelationalState valueState : state.getRelationalStates() ) {
 			tuple.addValue( createSimpleValue( table, attributeName, valueState, forceNonNullable, forceUnique ) );
 		}
 		return tuple;
 	}
 
 	public static Value createValue(TableSpecification table,
 									String attributeName,
 									ValueRelationalState state,
 									boolean forceNonNullable,
 									boolean forceUnique) {
 		Value value = null;
 		if ( SimpleValueRelationalState.class.isInstance( state ) ) {
 			value = createSimpleValue(
 					table,
 					attributeName,
 					SimpleValueRelationalState.class.cast( state ),
 					forceNonNullable,
 					forceUnique
 			);
 		}
 		else if ( TupleRelationalState.class.isInstance( state ) ) {
 			value = createTuple(
 					table,
 					attributeName,
 					TupleRelationalState.class.cast( state ),
 					forceNonNullable,
 					forceUnique
 			);
 		}
 		else {
 			throw new MappingException( "Unexpected type of RelationalState" + state.getClass().getName() );
 		}
 		return value;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java
index e1831773ac..a7c3e67a69 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java
@@ -1,189 +1,194 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.domain.Superclass;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassHierarchy;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassType;
 import org.hibernate.metamodel.source.annotations.entity.EntityBinder;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.annotations.global.FetchProfileBinder;
 import org.hibernate.metamodel.source.annotations.global.FilterDefBinder;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.global.QueryBinder;
 import org.hibernate.metamodel.source.annotations.global.TableBinder;
 import org.hibernate.metamodel.source.annotations.global.TypeDefBinder;
 import org.hibernate.metamodel.source.annotations.util.ConfiguredClassHierarchyBuilder;
 import org.hibernate.metamodel.source.annotations.xml.OrmXmlParser;
+import org.hibernate.metamodel.source.annotations.xml.PseudoJpaDotNames;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Main class responsible to creating and binding the Hibernate meta-model from annotations.
  * This binder only has to deal with the (jandex) annotation index/repository. XML configuration is already processed
  * and pseudo annotations are created.
  *
  * @author Hardy Ferentschik
  * @see org.hibernate.metamodel.source.annotations.xml.OrmXmlParser
  */
 public class AnnotationBinder implements Binder {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AnnotationBinder.class.getName()
 	);
 
-	private final MetadataImplementor metadata;
+	private final MetadataImpl metadata;
 
 	private Index index;
 	private ClassLoaderService classLoaderService;
 
 	public AnnotationBinder(MetadataImpl metadata) {
 		this.metadata = metadata;
 	}
 
 	@Override
 	@SuppressWarnings( { "unchecked" })
 	public void prepare(MetadataSources sources) {
 		// create a jandex index from the annotated classes
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : sources.getAnnotatedClasses() ) {
 			indexClass( indexer, clazz.getName().replace( '.', '/' ) + ".class" );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : sources.getAnnotatedPackages() ) {
 			indexClass( indexer, packageName.replace( '.', '/' ) + "/package-info.class" );
 		}
 
 		index = indexer.complete();
 
 		List<JaxbRoot<XMLEntityMappings>> mappings = new ArrayList<JaxbRoot<XMLEntityMappings>>();
 		for ( JaxbRoot<?> root : sources.getJaxbRootList() ) {
 			if ( root.getRoot() instanceof XMLEntityMappings ) {
 				mappings.add( (JaxbRoot<XMLEntityMappings>) root );
 			}
 		}
 		if ( !mappings.isEmpty() ) {
 			// process the xml configuration
 			final OrmXmlParser ormParser = new OrmXmlParser( metadata );
 			index = ormParser.parseAndUpdateIndex( mappings, index );
 		}
+
+        if( index.getAnnotations( PseudoJpaDotNames.DEFAULT_DELIMITED_IDENTIFIERS ) != null ) {
+            metadata.setGloballyQuotedIdentifiers( true );
+        }
 	}
 
 	/**
 	 * Adds the class w/ the specified name to the jandex index.
 	 *
 	 * @param indexer The jandex indexer
 	 * @param className the fully qualified class name to be indexed
 	 */
 	private void indexClass(Indexer indexer, String className) {
 		InputStream stream = classLoaderService().locateResourceStream( className );
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
 	}
 
 	private ClassLoaderService classLoaderService() {
 		if ( classLoaderService == null ) {
 			classLoaderService = metadata.getServiceRegistry().getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public void bindIndependentMetadata(MetadataSources sources) {
 		TypeDefBinder.bind( metadata, index );
 	}
 
 	@Override
 	public void bindTypeDependentMetadata(MetadataSources sources) {
 		IdGeneratorBinder.bind( metadata, index );
 	}
 
 	@Override
 	public void bindMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		AnnotationBindingContext context = new AnnotationBindingContext( index, metadata.getServiceRegistry() );
 		// need to order our annotated entities into an order we can process
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				context
 		);
 
 		// now we process each hierarchy one at the time
 		Hierarchical parent = null;
 		for ( ConfiguredClassHierarchy<EntityClass> hierarchy : hierarchies ) {
 			for ( EntityClass entityClass : hierarchy ) {
 				// for classes annotated w/ @Entity we create a EntityBinding
 				if ( ConfiguredClassType.ENTITY.equals( entityClass.getConfiguredClassType() ) ) {
 					LOG.bindingEntityFromAnnotatedClass( entityClass.getName() );
 					EntityBinder entityBinder = new EntityBinder( metadata, entityClass, parent );
 					EntityBinding binding = entityBinder.bind();
 					parent = binding.getEntity();
 				}
 				// for classes annotated w/ @MappedSuperclass we just create the domain instance
 				// the attribute bindings will be part of the first entity subclass
 				else if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( entityClass.getConfiguredClassType() ) ) {
 					parent = new Superclass( entityClass.getName(), parent );
 				}
 				// for classes which are not annotated at all we create the NonEntity domain class
 				// todo - not sure whether this is needed. It might be that we don't need this information (HF)
 				else {
 					parent = new NonEntity( entityClass.getName(), parent );
 				}
 			}
 		}
 	}
 
 	@Override
 	public void bindMappingDependentMetadata(MetadataSources sources) {
 		TableBinder.bind( metadata, index );
 		FetchProfileBinder.bind( metadata, index );
 		QueryBinder.bind( metadata, index );
 		FilterDefBinder.bind( metadata, index );
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java
index 522cca04a9..4e3c63e164 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java
@@ -1,217 +1,224 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.attribute.state.relational;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.jandex.AnnotationInstance;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.attribute.ColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * @author Hardy Ferentschik
  */
 public class ColumnRelationalStateImpl implements ColumnRelationalState {
 	private final NamingStrategy namingStrategy;
 	private final String columnName;
 	private final boolean unique;
 	private final boolean nullable;
+    private final boolean globallyQuotedIdentifiers;
 	private final Size size;
 	private final String checkCondition;
 	private final String customWriteFragment;
 	private final String customReadFragment;
 	private final Set<String> indexes;
 
 	// todo - what about these annotations !?
 	private String defaultString;
 	private String sqlType;
 	private String comment;
 	private Set<String> uniqueKeys = new HashSet<String>();
 
 
 	public ColumnRelationalStateImpl(SimpleAttribute attribute, MetadataImplementor meta) {
 		ColumnValues columnValues = attribute.getColumnValues();
 		namingStrategy = meta.getOptions().getNamingStrategy();
+        globallyQuotedIdentifiers = meta.isGloballyQuotedIdentifiers();
 		columnName = columnValues.getName().isEmpty() ? attribute.getName() : columnValues.getName();
 		unique = columnValues.isUnique();
 		nullable = columnValues.isNullable();
 		size = createSize( columnValues.getLength(), columnValues.getScale(), columnValues.getPrecision() );
 		checkCondition = parseCheckAnnotation( attribute );
 		indexes = parseIndexAnnotation( attribute );
 
 		String[] readWrite;
 		List<AnnotationInstance> columnTransformerAnnotations = getAllColumnTransformerAnnotations( attribute );
 		readWrite = createCustomReadWrite( columnTransformerAnnotations );
 		customReadFragment = readWrite[0];
 		customWriteFragment = readWrite[1];
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
-	@Override
+    @Override
+    public boolean isGloballyQuotedIdentifiers() {
+        return globallyQuotedIdentifiers;
+    }
+
+    @Override
 	public String getExplicitColumnName() {
 		return columnName;
 	}
 
 	@Override
 	public boolean isUnique() {
 		return unique;
 	}
 
 	@Override
 	public Size getSize() {
 		return size;
 	}
 
 	@Override
 	public boolean isNullable() {
 		return nullable;
 	}
 
 	@Override
 	public String getCheckCondition() {
 		return checkCondition;
 	}
 
 	@Override
 	public String getDefault() {
 		return defaultString;
 	}
 
 	@Override
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	@Override
 	public String getCustomWriteFragment() {
 		return customWriteFragment;
 	}
 
 	@Override
 	public String getCustomReadFragment() {
 		return customReadFragment;
 	}
 
 	@Override
 	public String getComment() {
 		return comment;
 	}
 
 	@Override
 	public Set<String> getUniqueKeys() {
 		return uniqueKeys;
 	}
 
 	@Override
 	public Set<String> getIndexes() {
 		return indexes;
 	}
 
 	private Size createSize(int length, int scale, int precision) {
 		Size size = new Size();
 		size.setLength( length );
 		size.setScale( scale );
 		size.setPrecision( precision );
 		return size;
 	}
 
 	private List<AnnotationInstance> getAllColumnTransformerAnnotations(SimpleAttribute attribute) {
 		List<AnnotationInstance> allColumnTransformerAnnotations = new ArrayList<AnnotationInstance>();
 
 		// not quite sure about the usefulness of @ColumnTransformers (HF)
 		AnnotationInstance columnTransformersAnnotations = attribute.getIfExists( HibernateDotNames.COLUMN_TRANSFORMERS );
 		if ( columnTransformersAnnotations != null ) {
 			AnnotationInstance[] annotationInstances = allColumnTransformerAnnotations.get( 0 ).value().asNestedArray();
 			allColumnTransformerAnnotations.addAll( Arrays.asList( annotationInstances ) );
 		}
 
 		AnnotationInstance columnTransformerAnnotation = attribute.getIfExists( HibernateDotNames.COLUMN_TRANSFORMER );
 		if ( columnTransformerAnnotation != null ) {
 			allColumnTransformerAnnotations.add( columnTransformerAnnotation );
 		}
 		return allColumnTransformerAnnotations;
 	}
 
 	private String[] createCustomReadWrite(List<AnnotationInstance> columnTransformerAnnotations) {
 		String[] readWrite = new String[2];
 
 		boolean alreadyProcessedForColumn = false;
 		for ( AnnotationInstance annotationInstance : columnTransformerAnnotations ) {
 			String forColumn = annotationInstance.value( "forColumn" ) == null ?
 					null : annotationInstance.value( "forColumn" ).asString();
 
 			if ( forColumn != null && !forColumn.equals( columnName ) ) {
 				continue;
 			}
 
 			if ( alreadyProcessedForColumn ) {
 				throw new AnnotationException( "Multiple definition of read/write conditions for column " + columnName );
 			}
 
 			readWrite[0] = annotationInstance.value( "read" ) == null ?
 					null : annotationInstance.value( "read" ).asString();
 			readWrite[1] = annotationInstance.value( "write" ) == null ?
 					null : annotationInstance.value( "write" ).asString();
 
 			alreadyProcessedForColumn = true;
 		}
 		return readWrite;
 	}
 
 	private String parseCheckAnnotation(SimpleAttribute attribute) {
 		String checkCondition = null;
 		AnnotationInstance checkAnnotation = attribute.getIfExists( HibernateDotNames.CHECK );
 		if ( checkAnnotation != null ) {
 			checkCondition = checkAnnotation.value( "constraints" ).toString();
 		}
 		return checkCondition;
 	}
 
 	private Set<String> parseIndexAnnotation(SimpleAttribute attribute) {
 		Set<String> indexNames = new HashSet<String>();
 		AnnotationInstance indexAnnotation = attribute.getIfExists( HibernateDotNames.INDEX );
 		if ( indexAnnotation != null ) {
 			String indexName = indexAnnotation.value( "name" ).toString();
 			indexNames.add( indexName );
 		}
 		return indexNames;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index a92845c8d5..457f925967 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,795 +1,808 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.GenerationType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.ResultCheckStyle;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
+import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.MappedAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.AttributeBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.DiscriminatorBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.ManyToOneBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ColumnRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ManyToOneRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.TupleRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.entity.state.binding.EntityBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Creates the domain and relational metamodel for a configured class and <i>binds</i> them together.
  *
  * @author Hardy Ferentschik
  */
 public class EntityBinder {
 	private final EntityClass entityClass;
 	private final MetadataImplementor meta;
 	private final Hierarchical superType;
 
 	public EntityBinder(MetadataImplementor metadata, EntityClass entityClass, Hierarchical superType) {
 		this.entityClass = entityClass;
 		this.meta = metadata;
 		this.superType = superType;
 	}
 
 	public EntityBinding bind() {
 		EntityBinding entityBinding = new EntityBinding();
 		EntityBindingStateImpl entityBindingState = new EntityBindingStateImpl( superType, entityClass );
 
 		bindJpaEntityAnnotation( entityBindingState );
 		bindHibernateEntityAnnotation( entityBindingState ); // optional hibernate specific @org.hibernate.annotations.Entity
 		bindTable( entityBinding );
 
 		// bind entity level annotations
 		bindWhereFilter( entityBindingState );
 		bindJpaCaching( entityBindingState );
 		bindHibernateCaching( entityBindingState );
 		bindProxy( entityBindingState );
 		bindSynchronize( entityBindingState );
 		bindCustomSQL( entityBindingState );
 		bindRowId( entityBindingState );
 		bindBatchSize( entityBindingState );
 		entityBinding.initialize( meta, entityBindingState );
 
 		bindInheritance( entityBinding );
 
 		// bind all attributes - simple as well as associations
 		bindAttributes( entityBinding );
 		bindEmbeddedAttributes( entityBinding );
 
 		// take care of the id, attributes and relations
 		if ( entityClass.isEntityRoot() ) {
 			bindId( entityBinding );
 		}
 
 		bindTableUniqueConstraints( entityBinding );
 
 		// last, but not least we initialize and register the new EntityBinding
 		meta.addEntity( entityBinding );
 		return entityBinding;
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 		if ( tableAnnotation == null ) {
 			return;
 		}
 		TableSpecification table = entityBinding.getBaseTable();
 		bindUniqueConstraints( tableAnnotation, table );
 	}
 
 	/**
 	 * Bind {@link javax.persistence.UniqueConstraint} to table as a {@link UniqueKey}
 	 *
 	 * @param tableAnnotation JPA annotations which has a {@code uniqueConstraints} attribute.
 	 * @param table Table which the UniqueKey bind to.
 	 */
 	private void bindUniqueConstraints(AnnotationInstance tableAnnotation, TableSpecification table) {
 		AnnotationValue value = tableAnnotation.value( "uniqueConstraints" );
 		if ( value == null ) {
 			return;
 		}
 		AnnotationInstance[] uniqueConstraints = value.asNestedArray();
 		for ( AnnotationInstance unique : uniqueConstraints ) {
 			String name = unique.value( "name" ).asString();
 			UniqueKey uniqueKey = table.getOrCreateUniqueKey( name );
 			String[] columnNames = unique.value( "columnNames" ).asStringArray();
 			if ( columnNames.length == 0 ) {
 				//todo throw exception?
 			}
 			for ( String columnName : columnNames ) {
 				uniqueKey.addColumn( table.getOrCreateColumn( columnName ) );
 			}
 		}
 	}
 
 	private void bindInheritance(EntityBinding entityBinding) {
 		entityBinding.setInheritanceType( entityClass.getInheritanceType() );
 		switch ( entityClass.getInheritanceType() ) {
 			case SINGLE_TABLE: {
 				bindDiscriminatorColumn( entityBinding );
 				break;
 			}
 			case JOINED: {
 				// todo
 				break;
 			}
 			case TABLE_PER_CLASS: {
 				// todo
 				break;
 			}
 			default: {
 				// do nothing
 			}
 		}
 	}
 
 	private void bindDiscriminatorColumn(EntityBinding entityBinding) {
 		final Map<DotName, List<AnnotationInstance>> typeAnnotations = JandexHelper.getTypeAnnotations(
 				entityClass.getClassInfo()
 		);
 		SimpleAttribute discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( typeAnnotations );
 		bindSingleMappedAttribute( entityBinding, entityBinding.getEntity(), discriminatorAttribute );
 	}
 
 	private void bindWhereFilter(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		if ( whereAnnotation != null ) {
 			// no null check needed, it is a required attribute
-			String clause = whereAnnotation.value( "clause" ).asString();
-			entityBindingState.setWhereFilter( clause );
+			entityBindingState.setWhereFilter( JandexHelper.getValueAsString( whereAnnotation, "clause" ) );
 		}
 	}
 
 	private void bindHibernateCaching(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance cacheAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( cacheAnnotation == null ) {
 			return;
 		}
 
 		String region;
 		if ( cacheAnnotation.value( "region" ) != null ) {
 			region = cacheAnnotation.value( "region" ).asString();
 		}
 		else {
 			region = entityBindingState.getEntityName();
 		}
 
 		boolean cacheLazyProperties = true;
 		if ( cacheAnnotation.value( "include" ) != null ) {
 			String tmp = cacheAnnotation.value( "include" ).asString();
 			if ( "all".equalsIgnoreCase( tmp ) ) {
 				cacheLazyProperties = true;
 			}
 			else if ( "non-lazy".equalsIgnoreCase( tmp ) ) {
 				cacheLazyProperties = false;
 			}
 			else {
 				throw new AnnotationException( "Unknown lazy property annotations: " + tmp );
 			}
 		}
 
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.valueOf(
 				cacheAnnotation.value( "usage" ).asEnum()
 		);
 		Caching caching = new Caching( region, strategy.toAccessType(), cacheLazyProperties );
 		entityBindingState.setCaching( caching );
 	}
 
 	// This does not take care of any inheritance of @Cacheable within a class hierarchy as specified in JPA2.
 	// This is currently not supported (HF)
 	private void bindJpaCaching(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance cacheAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( cacheAnnotation != null && cacheAnnotation.value() != null ) {
 			cacheable = cacheAnnotation.value().asBoolean();
 		}
 
 		Caching caching = null;
 		switch ( meta.getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				caching = createCachingForCacheableAnnotation( entityBindingState );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheable ) {
 					caching = createCachingForCacheableAnnotation( entityBindingState );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheAnnotation == null || cacheable ) {
 					caching = createCachingForCacheableAnnotation( entityBindingState );
 				}
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				break;
 			}
 		}
 		if ( caching != null ) {
 			entityBindingState.setCaching( caching );
 		}
 	}
 
 	private void bindProxy(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance proxyAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PROXY
 		);
 		boolean lazy = true;
 		String proxyInterfaceClass = null;
 
 		if ( proxyAnnotation != null ) {
 			AnnotationValue lazyValue = proxyAnnotation.value( "lazy" );
 			if ( lazyValue != null ) {
 				lazy = lazyValue.asBoolean();
 			}
 
 			AnnotationValue proxyClassValue = proxyAnnotation.value( "proxyClass" );
 			if ( proxyClassValue != null ) {
 				proxyInterfaceClass = proxyClassValue.asString();
 			}
 		}
 
 		entityBindingState.setLazy( lazy );
 		entityBindingState.setProxyInterfaceName( proxyInterfaceClass );
 	}
 
 	private void bindSynchronize(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 
 		if ( synchronizeAnnotation != null ) {
 			String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			for ( String tableName : tableNames ) {
 				entityBindingState.addSynchronizedTableName( tableName );
 			}
 		}
 	}
 
 	private void bindCustomSQL(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		entityBindingState.setCustomInsert( createCustomSQL( sqlInsertAnnotation ) );
 
 		AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		entityBindingState.setCustomUpdate( createCustomSQL( sqlUpdateAnnotation ) );
 
 		AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		entityBindingState.setCustomDelete( createCustomSQL( sqlDeleteAnnotation ) );
 
 		AnnotationInstance sqlDeleteAllAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE_ALL
 		);
 		if ( sqlDeleteAllAnnotation != null ) {
 			entityBindingState.setCustomDelete( createCustomSQL( sqlDeleteAllAnnotation ) );
 		}
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSQLAnnotation) {
 		if ( customSQLAnnotation == null ) {
 			return null;
 		}
 
 		String sql = customSQLAnnotation.value( "sql" ).asString();
 		boolean isCallable = false;
 		AnnotationValue callableValue = customSQLAnnotation.value( "callable" );
 		if ( callableValue != null ) {
 			isCallable = callableValue.asBoolean();
 		}
 
 		ResultCheckStyle checkStyle = ResultCheckStyle.NONE;
 		AnnotationValue checkStyleValue = customSQLAnnotation.value( "check" );
 		if ( checkStyleValue != null ) {
 			checkStyle = Enum.valueOf( ResultCheckStyle.class, checkStyleValue.asEnum() );
 		}
 
 		return new CustomSQL(
 				sql,
 				isCallable,
 				Enum.valueOf( ExecuteUpdateResultCheckStyle.class, checkStyle.toString() )
 		);
 	}
 
 	private void bindRowId(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 
 		if ( rowIdAnnotation != null ) {
 			entityBindingState.setRowId( rowIdAnnotation.value().asString() );
 		}
 	}
 
 	private void bindBatchSize(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 
 		if ( batchSizeAnnotation != null ) {
 			entityBindingState.setBatchSize( batchSizeAnnotation.value( "size" ).asInt() );
 		}
 	}
 
 	private Caching createCachingForCacheableAnnotation(EntityBindingStateImpl entityBindingState) {
 		String region = entityBindingState.getEntityName();
 		RegionFactory regionFactory = meta.getServiceRegistry().getService( RegionFactory.class );
 		AccessType defaultAccessType = regionFactory.getDefaultAccessType();
 		return new Caching( region, defaultAccessType, true );
 	}
 
-	private Schema.Name createSchemaName() {
-		String schema = null;
-		String catalog = null;
+    private Table createTable() {
+        String schmaName = null;
+        String catalogName = null;
+        String tableName = null;
+        AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
+                entityClass.getClassInfo(), JPADotNames.TABLE
+        );
+        if ( tableAnnotation != null ) {
+            schmaName = JandexHelper.getValueAsString( tableAnnotation, "schema" );
+            catalogName = JandexHelper.getValueAsString( tableAnnotation, "catalog" );
+            tableName = JandexHelper.getValueAsString( tableAnnotation, "name" );
+        }
+
+
+        if ( StringHelper.isEmpty( tableName ) ) {
+            tableName = meta.getNamingStrategy().classToTableName( entityClass.getPrimaryTableName() );
+
+        }
+        else {
+            tableName = meta.getNamingStrategy().tableName( tableName );
+        }
+        if ( meta.isGloballyQuotedIdentifiers() ) {
+            schmaName = StringHelper.quote( schmaName );
+            catalogName = StringHelper.quote( catalogName );
+            tableName = StringHelper.quote( tableName );
+        }
+        final Identifier tableNameIdentifier = Identifier.toIdentifier( tableName );
+        final Schema schema = meta.getDatabase().getSchema( new Schema.Name( schmaName, catalogName ) );
+        Table table = schema.getTable( tableNameIdentifier );
+        if ( table == null ) {
+            table = schema.createTable( tableNameIdentifier );
+        }
+        return table;
+    }
 
-		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
-				entityClass.getClassInfo(), JPADotNames.TABLE
-		);
-		if ( tableAnnotation != null ) {
-			AnnotationValue schemaValue = tableAnnotation.value( "schema" );
-			AnnotationValue catalogValue = tableAnnotation.value( "catalog" );
-
-			schema = schemaValue != null ? schemaValue.asString() : null;
-			catalog = catalogValue != null ? catalogValue.asString() : null;
-		}
-
-		return new Schema.Name( schema, catalog );
-	}
 
 	private void bindTable(EntityBinding entityBinding) {
-		final Schema schema = meta.getDatabase().getSchema( createSchemaName() );
-		final Identifier tableName = Identifier.toIdentifier( entityClass.getPrimaryTableName() );
-		org.hibernate.metamodel.relational.Table table = schema.getTable( tableName );
-		if ( table == null ) {
-			table = schema.createTable( tableName );
-		}
+        Table table = createTable();
 		entityBinding.setBaseTable( table );
 
 		AnnotationInstance checkAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CHECK
 		);
 		if ( checkAnnotation != null ) {
 			table.addCheckConstraint( checkAnnotation.value( "constraints" ).asString() );
 		}
 	}
 
 	private void bindId(EntityBinding entityBinding) {
 		switch ( entityClass.getIdType() ) {
 			case SIMPLE: {
 				bindSingleIdAnnotation( entityBinding );
 				break;
 			}
 			case COMPOSED: {
 				// todo
 				break;
 			}
 			case EMBEDDED: {
 				bindEmbeddedIdAnnotation( entityBinding );
 				break;
 			}
 			default: {
 			}
 		}
 	}
 
 
 	private void bindJpaEntityAnnotation(EntityBindingStateImpl entityBindingState) {
 		AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.ENTITY
 		);
 		String name;
 		if ( jpaEntityAnnotation.value( "name" ) == null ) {
 			name = entityClass.getName();
 		}
 		else {
 			name = jpaEntityAnnotation.value( "name" ).asString();
 		}
 		entityBindingState.setJpaEntityName( name );
 	}
 
 	private void bindEmbeddedIdAnnotation(EntityBinding entityBinding) {
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.EMBEDDED_ID
 		);
 
 		String idName = JandexHelper.getPropertyName( idAnnotation.target() );
 		MappedAttribute idAttribute = entityClass.getMappedAttribute( idName );
 		if ( !( idAttribute instanceof SimpleAttribute ) ) {
 			throw new AssertionFailure( "Unexpected attribute type for id attribute" );
 		}
 
 		SingularAttribute attribute = entityBinding.getEntity().getOrCreateComponentAttribute( idName );
 
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 
 		TupleRelationalStateImpl state = new TupleRelationalStateImpl();
 		EmbeddableClass embeddableClass = entityClass.getEmbeddedClasses().get( idName );
 		for ( SimpleAttribute attr : embeddableClass.getSimpleAttributes() ) {
 			state.addValueState( new ColumnRelationalStateImpl( attr, meta ) );
 		}
 		attributeBinding.initialize( state );
 		Map<String, String> parms = new HashMap<String, String>( 1 );
 		parms.put( IdentifierGenerator.ENTITY_NAME, entityBinding.getEntity().getName() );
 		IdGenerator generator = new IdGenerator( "NAME", "assigned", parms );
 		entityBinding.getEntityIdentifier().setIdGenerator( generator );
 		// entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindSingleIdAnnotation(EntityBinding entityBinding) {
 		// we know we are dealing w/ a single @Id, but potentially it is defined in a mapped super class
 		ConfiguredClass configuredClass = entityClass;
 		EntityClass superEntity = entityClass.getEntityParent();
 		Hierarchical container = entityBinding.getEntity();
 		Iterator<SimpleAttribute> iter = null;
 		while ( configuredClass != null && configuredClass != superEntity ) {
 			iter = configuredClass.getIdAttributes().iterator();
 			if ( iter.hasNext() ) {
 				break;
 			}
 			configuredClass = configuredClass.getParent();
 			container = container.getSuperType();
 		}
 
 		// if we could not find the attribute our assumptions were wrong
 		if ( iter == null || !iter.hasNext() ) {
 			throw new AnnotationException(
 					String.format(
 							"Unable to find id attribute for class %s",
 							entityClass.getName()
 					)
 			);
 		}
 
 		// now that we have the id attribute we can create the attribute and binding
 		MappedAttribute idAttribute = iter.next();
 		Attribute attribute = container.getOrCreateSingularAttribute( idAttribute.getName() );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize( new ColumnRelationalStateImpl( (SimpleAttribute) idAttribute, meta ) );
 		bindSingleIdGeneratedValue( entityBinding, idAttribute.getName() );
 	}
 
 	private void bindSingleIdGeneratedValue(EntityBinding entityBinding, String idPropertyName) {
 		AnnotationInstance generatedValueAnn = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.GENERATED_VALUE
 		);
 		if ( generatedValueAnn == null ) {
 			return;
 		}
 
 		String idName = JandexHelper.getPropertyName( generatedValueAnn.target() );
 		if ( !idPropertyName.equals( idName ) ) {
 			throw new AssertionFailure(
 					String.format(
 							"Attribute[%s.%s] with @GeneratedValue doesn't have a @Id.",
 							entityClass.getName(),
 							idPropertyName
 					)
 			);
 		}
 		String generator = JandexHelper.getValueAsString( generatedValueAnn, "generator" );
 		IdGenerator idGenerator = null;
 		if ( StringHelper.isNotEmpty( generator ) ) {
 			idGenerator = meta.getIdGenerator( generator );
 			if ( idGenerator == null ) {
 				throw new MappingException(
 						String.format(
 								"@GeneratedValue on %s.%s referring an undefined generator [%s]",
 								entityClass.getName(),
 								idName,
 								generator
 						)
 				);
 			}
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 		GenerationType generationType = JandexHelper.getValueAsEnum(
 				generatedValueAnn,
 				"strategy",
 				GenerationType.class
 		);
 		String strategy = IdGeneratorBinder.generatorType(
-				generationType,
-				meta.getOptions().useNewIdentifierGenerators()
-		);
+                generationType,
+                meta.getOptions().useNewIdentifierGenerators()
+        );
 		if ( idGenerator != null && !strategy.equals( idGenerator.getStrategy() ) ) {
 			//todo how to ?
 			throw new MappingException(
 					String.format(
 							"Inconsistent Id Generation strategy of @GeneratedValue on %s.%s",
 							entityClass.getName(),
 							idName
 					)
 			);
 		}
 		if ( idGenerator == null ) {
 			idGenerator = new IdGenerator( "NAME", strategy, new HashMap<String, String>() );
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 //        entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindAttributes(EntityBinding entityBinding) {
 		// bind the attributes of this entity
 		AttributeContainer entity = entityBinding.getEntity();
 		bindAttributes( entityBinding, entity, entityClass );
 
 		// bind potential mapped super class attributes
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsPotentialMappedSuperclassAttributes( parent ) ) {
 			bindAttributes( entityBinding, superTypeContainer, parent );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private boolean containsPotentialMappedSuperclassAttributes(ConfiguredClass parent) {
 		return parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) );
 	}
 
 	private void bindAttributes(EntityBinding entityBinding, AttributeContainer attributeContainer, ConfiguredClass configuredClass) {
 		for ( SimpleAttribute simpleAttribute : configuredClass.getSimpleAttributes() ) {
 			bindSingleMappedAttribute(
 					entityBinding,
 					attributeContainer,
 					simpleAttribute
 			);
 		}
 		for ( AssociationAttribute associationAttribute : configuredClass.getAssociationAttributes() ) {
 			bindAssociationAttribute(
 					entityBinding,
 					attributeContainer,
 					associationAttribute
 			);
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding) {
 		AttributeContainer entity = entityBinding.getEntity();
 		bindEmbeddedAttributes( entityBinding, entity, entityClass );
 
 		// bind potential mapped super class embeddables
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsPotentialMappedSuperclassAttributes( parent ) ) {
 			bindEmbeddedAttributes( entityBinding, superTypeContainer, parent );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding, AttributeContainer attributeContainer, ConfiguredClass configuredClass) {
 		for ( Map.Entry<String, EmbeddableClass> entry : configuredClass.getEmbeddedClasses().entrySet() ) {
 			String attributeName = entry.getKey();
 			EmbeddableClass embeddedClass = entry.getValue();
 			SingularAttribute component = attributeContainer.getOrCreateComponentAttribute( attributeName );
 			for ( SimpleAttribute simpleAttribute : embeddedClass.getSimpleAttributes() ) {
 				bindSingleMappedAttribute(
 						entityBinding,
 						component.getAttributeContainer(),
 						simpleAttribute
 				);
 			}
 			for ( AssociationAttribute associationAttribute : embeddedClass.getAssociationAttributes() ) {
 				bindAssociationAttribute(
 						entityBinding,
 						component.getAttributeContainer(),
 						associationAttribute
 				);
 			}
 		}
 	}
 
 	private void bindAssociationAttribute(EntityBinding entityBinding, AttributeContainer container, AssociationAttribute associationAttribute) {
 		switch ( associationAttribute.getAssociationType() ) {
 			case MANY_TO_ONE: {
 				container.getOrCreateSingularAttribute( associationAttribute.getName() );
 				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding(
 						associationAttribute.getName()
 				);
 
 				ManyToOneAttributeBindingState bindingState = new ManyToOneBindingStateImpl( associationAttribute );
 				manyToOneAttributeBinding.initialize( bindingState );
 
 				ManyToOneRelationalStateImpl relationalState = new ManyToOneRelationalStateImpl();
 				if ( entityClass.hasOwnTable() ) {
 					ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 							associationAttribute, meta
 					);
 					relationalState.addValueState( columnRelationsState );
 				}
 				manyToOneAttributeBinding.initialize( relationalState );
 				break;
 			}
 			default: {
 				// todo
 			}
 		}
 	}
 
 	private void bindSingleMappedAttribute(EntityBinding entityBinding, AttributeContainer container, SimpleAttribute simpleAttribute) {
 		if ( simpleAttribute.isId() ) {
 			return;
 		}
 
 		Attribute attribute = container.getOrCreateSingularAttribute( simpleAttribute.getName() );
 		SimpleAttributeBinding attributeBinding;
 
 		if ( simpleAttribute.isDiscriminator() ) {
 			EntityDiscriminator entityDiscriminator = entityBinding.makeEntityDiscriminator( attribute );
 			DiscriminatorBindingState bindingState = new DiscriminatorBindingStateImpl( simpleAttribute );
 			entityDiscriminator.initialize( bindingState );
 			attributeBinding = entityDiscriminator.getValueBinding();
 		}
 		else if ( simpleAttribute.isVersioned() ) {
 			attributeBinding = entityBinding.makeVersionBinding( attribute );
 			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
 			attributeBinding.initialize( bindingState );
 		}
 		else {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
 			attributeBinding.initialize( bindingState );
 		}
 
 		if ( entityClass.hasOwnTable() ) {
 			ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 					simpleAttribute, meta
 			);
 			TupleRelationalStateImpl relationalState = new TupleRelationalStateImpl();
 			relationalState.addValueState( columnRelationsState );
 
 			attributeBinding.initialize( relationalState );
 		}
 	}
 
 	private void bindHibernateEntityAnnotation(EntityBindingStateImpl entityBindingState) {
 		// initialize w/ the defaults
 		boolean mutable = true;
 		boolean dynamicInsert = false;
 		boolean dynamicUpdate = false;
 		boolean selectBeforeUpdate = false;
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		OptimisticLockType optimisticLock = OptimisticLockType.VERSION;
 
 		AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		if ( hibernateEntityAnnotation != null ) {
 			if ( hibernateEntityAnnotation.value( "mutable" ) != null ) {
 				mutable = hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "dynamicInsert" ) != null ) {
 				dynamicInsert = hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "dynamicUpdate" ) != null ) {
 				dynamicUpdate = hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null ) {
 				selectBeforeUpdate = hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 				polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 			}
 
 			if ( hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 				optimisticLock = OptimisticLockType.valueOf(
 						hibernateEntityAnnotation.value( "optimisticLock" ).asEnum()
 				);
 			}
 
 			if ( hibernateEntityAnnotation.value( "persister" ) != null ) {
 				final String persisterClassName = ( hibernateEntityAnnotation.value( "persister" ).toString() );
 				entityBindingState.setPersisterClass( meta.<EntityPersister>locateClassByName( persisterClassName ) );
 			}
 		}
 
 		// also check for the immutable annotation
 		AnnotationInstance immutableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		if ( immutableAnnotation != null ) {
 			mutable = false;
 		}
 
 		entityBindingState.setMutable( mutable );
 		entityBindingState.setDynamicInsert( dynamicInsert );
 		entityBindingState.setDynamicUpdate( dynamicUpdate );
 		entityBindingState.setSelectBeforeUpdate( selectBeforeUpdate );
 		entityBindingState.setExplicitPolymorphism( PolymorphismType.EXPLICIT.equals( polymorphism ) );
 		entityBindingState.setOptimisticLock( optimisticLock );
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
index d14b96afe0..cfeb5c1699 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityClass.java
@@ -1,212 +1,202 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.AccessType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.MappingException;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 
 /**
  * Represents an entity or mapped superclass configured via annotations/xml.
  *
  * @author Hardy Ferentschik
  */
 public class EntityClass extends ConfiguredClass {
 	private final AccessType hierarchyAccessType;
 	private final InheritanceType inheritanceType;
 	private final boolean hasOwnTable;
 	private final String primaryTableName;
 	private final IdType idType;
 	private final EntityClass jpaEntityParent;
 
 	public EntityClass(ClassInfo classInfo,
 					   EntityClass parent,
 					   AccessType hierarchyAccessType,
 					   InheritanceType inheritanceType,
 					   AnnotationBindingContext context) {
 
 		super( classInfo, hierarchyAccessType, parent, context );
 		this.hierarchyAccessType = hierarchyAccessType;
 		this.inheritanceType = inheritanceType;
 		this.idType = determineIdType();
 		this.jpaEntityParent = findJpaEntitySuperClass();
 		this.hasOwnTable = definesItsOwnTable();
 		this.primaryTableName = determinePrimaryTableName();
 	}
 
 	/**
 	 * @return Returns the next JPA super entity for this entity class or {@code null} in case there is none.
 	 */
 	public EntityClass getEntityParent() {
 		return jpaEntityParent;
 	}
 
 	/**
 	 * @return Returns {@code true} is this entity class is the root of the class hierarchy in the JPA sense, which
 	 *         means there are no more super classes which are annotated with @Entity. There can, however, be mapped superclasses
 	 *         or non entities in the actual java type hierarchy.
 	 */
 	public boolean isEntityRoot() {
 		return jpaEntityParent == null;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return inheritanceType;
 	}
 
 	public IdType getIdType() {
 		return idType;
 	}
 
 	public boolean hasOwnTable() {
 		return hasOwnTable;
 	}
-
+    //todo change a better method name
 	public String getPrimaryTableName() {
 		return primaryTableName;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityClass" );
 		sb.append( "{name=" ).append( getConfiguredClass().getSimpleName() );
 		sb.append( ", hierarchyAccessType=" ).append( hierarchyAccessType );
 		sb.append( ", inheritanceType=" ).append( inheritanceType );
 		sb.append( ", hasOwnTable=" ).append( hasOwnTable );
 		sb.append( ", primaryTableName='" ).append( primaryTableName ).append( '\'' );
 		sb.append( ", idType=" ).append( idType );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	private boolean definesItsOwnTable() {
 		// mapped super classes don't have their own tables
 		if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( getConfiguredClassType() ) ) {
 			return false;
 		}
 
 		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceType ) ) {
 			if ( isEntityRoot() ) {
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	private EntityClass findJpaEntitySuperClass() {
 		ConfiguredClass tmpConfiguredClass = this.getParent();
 		while ( tmpConfiguredClass != null ) {
 			if ( ConfiguredClassType.ENTITY.equals( tmpConfiguredClass.getConfiguredClassType() ) ) {
 				return (EntityClass) tmpConfiguredClass;
 			}
 			tmpConfiguredClass = tmpConfiguredClass.getParent();
 		}
 		return null;
 	}
 
 	private String determinePrimaryTableName() {
 		String tableName = null;
 		if ( hasOwnTable() ) {
 			tableName = getConfiguredClass().getSimpleName();
-			AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
-					getClassInfo(), JPADotNames.TABLE
-			);
-			if ( tableAnnotation != null ) {
-				AnnotationValue value = tableAnnotation.value( "name" );
-				String tmp = value == null ? null : value.asString();
-				if ( tmp != null && !tmp.isEmpty() ) {
-					tableName = tmp;
-				}
-			}
 		}
 		else if ( getParent() != null
 				&& !getParent().getConfiguredClassType().equals( ConfiguredClassType.MAPPED_SUPERCLASS ) ) {
 			tableName = ( (EntityClass) getParent() ).getPrimaryTableName();
 		}
 		return tableName;
 	}
 
 	private IdType determineIdType() {
 		List<AnnotationInstance> idAnnotations = findIdAnnotations( JPADotNames.ID );
 		List<AnnotationInstance> embeddedIdAnnotations = findIdAnnotations( JPADotNames.EMBEDDED_ID );
 
 		if ( !idAnnotations.isEmpty() && !embeddedIdAnnotations.isEmpty() ) {
 			throw new MappingException(
 					"@EmbeddedId and @Id cannot be used together. Check the configuration for " + getName() + "."
 			);
 		}
 
 		if ( !embeddedIdAnnotations.isEmpty() ) {
 			if ( embeddedIdAnnotations.size() == 1 ) {
 				return IdType.EMBEDDED;
 			}
 			else {
 				throw new AnnotationException( "Multiple @EmbeddedId annotations are not allowed" );
 			}
 		}
 
 		if ( !idAnnotations.isEmpty() ) {
 			if ( idAnnotations.size() == 1 ) {
 				return IdType.SIMPLE;
 			}
 			else {
 				return IdType.COMPOSED;
 			}
 		}
 		return IdType.NONE;
 	}
 
 	private List<AnnotationInstance> findIdAnnotations(DotName idAnnotationType) {
 		List<AnnotationInstance> idAnnotationList = new ArrayList<AnnotationInstance>();
 		if ( getClassInfo().annotations().get( idAnnotationType ) != null ) {
 			idAnnotationList.addAll( getClassInfo().annotations().get( idAnnotationType ) );
 		}
 		ConfiguredClass parent = getParent();
 		while ( parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) ) ) {
 			if ( parent.getClassInfo().annotations().get( idAnnotationType ) != null ) {
 				idAnnotationList.addAll( parent.getClassInfo().annotations().get( idAnnotationType ) );
 			}
 			parent = parent.getParent();
 
 		}
 		return idAnnotationList;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
index 1868a02f0e..287b56df45 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
@@ -1,312 +1,309 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.entity.state.binding;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.EntityBindingState;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Hardy Ferentschik
  */
 public class EntityBindingStateImpl implements EntityBindingState {
 	private String entityName;
 
 	private final String className;
 	private String proxyInterfaceName;
 
 	private final Hierarchical superType;
 	private final boolean isRoot;
 	private final InheritanceType inheritanceType;
 
 
 	private Caching caching;
 
 	private boolean mutable;
 	private boolean explicitPolymorphism;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private OptimisticLockType optimisticLock;
 
 	private Class<EntityPersister> persisterClass;
 
 	private boolean lazy;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames;
 
 	public EntityBindingStateImpl(Hierarchical superType, EntityClass entityClass) {
 		this.className = entityClass.getName();
 		this.superType = superType;
 		this.isRoot = entityClass.isEntityRoot();
 		this.inheritanceType = entityClass.getInheritanceType();
 		this.synchronizedTableNames = new HashSet<String>();
 		this.batchSize = -1;
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return entityName;
 	}
 
 	public void setJpaEntityName(String entityName) {
 		this.entityName = entityName;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	public String getEntityName() {
 		return className;
 	}
 
 	@Override
 	public String getClassName() {
 		return className;
 	}
 
 	@Override
 	public Class<EntityTuplizer> getCustomEntityTuplizerClass() {
 		return null; // todo : implement method body
 	}
 
 	@Override
 	public Hierarchical getSuperType() {
 		return superType;
 	}
 
 	public void setCaching(Caching caching) {
 		this.caching = caching;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
 		this.explicitPolymorphism = explicitPolymorphism;
 	}
 
 	public void setWhereFilter(String whereFilter) {
 		this.whereFilter = whereFilter;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public void setOptimisticLock(OptimisticLockType optimisticLock) {
 		this.optimisticLock = optimisticLock;
 	}
 
 	public void setPersisterClass(Class<EntityPersister> persisterClass) {
 		this.persisterClass = persisterClass;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public void addSynchronizedTableName(String tableName) {
 		synchronizedTableNames.add( tableName );
 	}
 
 	public void setCustomInsert(CustomSQL customInsert) {
 		this.customInsert = customInsert;
 	}
 
 	public void setCustomUpdate(CustomSQL customUpdate) {
 		this.customUpdate = customUpdate;
 	}
 
 	public void setCustomDelete(CustomSQL customDelete) {
 		this.customDelete = customDelete;
 	}
 
 	@Override
 	public boolean isRoot() {
 		return isRoot;
 
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return inheritanceType;
 	}
 
 	@Override
 	public Caching getCaching() {
 		return caching;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		// not needed for annotations!? (HF)
 		return null;
 	}
 
 	@Override
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	@Override
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	@Override
 	public String getRowId() {
 		return rowId;
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	@Override
 	public int getOptimisticLockMode() {
-		if ( optimisticLock == OptimisticLockType.ALL ) {
-			return Versioning.OPTIMISTIC_LOCK_ALL;
-		}
-		else if ( optimisticLock == OptimisticLockType.NONE ) {
-			return Versioning.OPTIMISTIC_LOCK_NONE;
-		}
-		else if ( optimisticLock == OptimisticLockType.DIRTY ) {
-			return Versioning.OPTIMISTIC_LOCK_DIRTY;
-		}
-		else if ( optimisticLock == OptimisticLockType.VERSION ) {
-			return Versioning.OPTIMISTIC_LOCK_VERSION;
-		}
-		else {
-			throw new AssertionFailure( "Unexpected optimistic lock type: " + optimisticLock );
-		}
+        switch ( optimisticLock ){
+            case ALL:
+                return Versioning.OPTIMISTIC_LOCK_ALL;
+            case NONE:
+                return Versioning.OPTIMISTIC_LOCK_NONE;
+            case DIRTY:
+                return Versioning.OPTIMISTIC_LOCK_DIRTY;
+            case VERSION:
+                return Versioning.OPTIMISTIC_LOCK_VERSION;
+            default:
+                throw new AssertionFailure( "Unexpected optimistic lock type: " + optimisticLock );
+        }
 	}
 
 	@Override
 	public Class<EntityPersister> getCustomEntityPersisterClass() {
 		return persisterClass;
 	}
 
 	@Override
 	public Boolean isAbstract() {
 		// no annotations equivalent
 		return false;
 	}
 
 	@Override
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	@Override
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	@Override
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	@Override
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/PseudoJpaDotNames.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/PseudoJpaDotNames.java
index 5e94be1079..de9a703dba 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/PseudoJpaDotNames.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/PseudoJpaDotNames.java
@@ -1,44 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.xml;
 
 import org.jboss.jandex.DotName;
 
 /**
  * Pseudo JPA Annotation name to distinguish Annotations defined in <persistence-unit-metadata>
  *
  * @author Strong Liu
  */
 public interface PseudoJpaDotNames {
-	public static final DotName DEFAULT_ACCESS = DotName.createSimple( "default.access" );
-	public static final DotName DEFAULT_DELIMITED_IDENTIFIERS = DotName.createSimple( "default.delimited.identifiers" );
-	public static final DotName DEFAULT_ENTITY_LISTENERS = DotName.createSimple( "default.entity.listeners" );
-	public static final DotName DEFAULT_POST_LOAD = DotName.createSimple( "default.entity.listener.post.load" );
-	public static final DotName DEFAULT_POST_PERSIST = DotName.createSimple( "default.entity.listener.post.persist" );
-	public static final DotName DEFAULT_POST_REMOVE = DotName.createSimple( "default.entity.listener.post.remove" );
-	public static final DotName DEFAULT_POST_UPDATE = DotName.createSimple( "default.entity.listener.post.update" );
-	public static final DotName DEFAULT_PRE_PERSIST = DotName.createSimple( "default.entity.listener.pre.persist" );
-	public static final DotName DEFAULT_PRE_REMOVE = DotName.createSimple( "default.entity.listener.pre.remove" );
-	public static final DotName DEFAULT_PRE_UPDATE = DotName.createSimple( "default.entity.listener.pre.update" );
+	DotName DEFAULT_ACCESS = DotName.createSimple( "default.access" );
+	DotName DEFAULT_DELIMITED_IDENTIFIERS = DotName.createSimple( "default.delimited.identifiers" );
+	DotName DEFAULT_ENTITY_LISTENERS = DotName.createSimple( "default.entity.listeners" );
+	DotName DEFAULT_POST_LOAD = DotName.createSimple( "default.entity.listener.post.load" );
+	DotName DEFAULT_POST_PERSIST = DotName.createSimple( "default.entity.listener.post.persist" );
+	DotName DEFAULT_POST_REMOVE = DotName.createSimple( "default.entity.listener.post.remove" );
+	DotName DEFAULT_POST_UPDATE = DotName.createSimple( "default.entity.listener.post.update" );
+	DotName DEFAULT_PRE_PERSIST = DotName.createSimple( "default.entity.listener.pre.persist" );
+	DotName DEFAULT_PRE_REMOVE = DotName.createSimple( "default.entity.listener.pre.remove" );
+	DotName DEFAULT_PRE_UPDATE = DotName.createSimple( "default.entity.listener.pre.update" );
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index 55bea122cb..6df7a03f28 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,380 +1,385 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.OverriddenMappingDefaults;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
  * {@link org.hibernate.metamodel.source.spi.Binder}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor implements HbmBindingContext {
 	private final MetadataImplementor metadata;
 	private final JaxbRoot<XMLHibernateMapping> jaxbRoot;
 
 	private final XMLHibernateMapping hibernateMapping;
 
 	private final MappingDefaults mappingDefaults;
 	private final MetaAttributeContext metaAttributeContext;
 
 	private final boolean autoImport;
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, JaxbRoot<XMLHibernateMapping> jaxbRoot) {
 		this.metadata = metadata;
 		this.jaxbRoot = jaxbRoot;
 
 		this.hibernateMapping = jaxbRoot.getRoot();
 		this.mappingDefaults = new OverriddenMappingDefaults(
 				metadata.getMappingDefaults(),
 				hibernateMapping.getPackage(),
 				hibernateMapping.getSchema(),
 				hibernateMapping.getCatalog(),
 				null,
 				null,
 				hibernateMapping.getDefaultCascade(),
 				hibernateMapping.getDefaultAccess(),
 				hibernateMapping.isDefaultLazy()
 		);
 
 		autoImport = hibernateMapping.isAutoImport();
 
 		metaAttributeContext = extractMetaAttributes();
 	}
 
 	private MetaAttributeContext extractMetaAttributes() {
 		return hibernateMapping.getMeta() == null
 				? new MetaAttributeContext( metadata.getMetaAttributeContext() )
 				: HbmHelper.extractMetaAttributeContext( hibernateMapping.getMeta(), true, metadata.getMetaAttributeContext() );
 	}
 
 	@Override
 	public boolean isAutoImport() {
 		return autoImport;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return jaxbRoot.getOrigin();
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return metadata.getServiceRegistry();
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return metadata.getOptions().getNamingStrategy();
 	}
 
-	@Override
+    @Override
+    public boolean isGloballyQuotedIdentifiers() {
+        return metadata.isGloballyQuotedIdentifiers();
+    }
+
+    @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return metadata;
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return metadata.locateClassByName( name );
 	}
 
 	@Override
 	public JavaType makeJavaType(String className) {
 		return metadata.makeJavaType( className );
 	}
 
 	public void bindIndependentMetadata() {
 		bindDatabaseObjectDefinitions();
 		bindTypeDefinitions();
 	}
 
 	private void bindDatabaseObjectDefinitions() {
 		if ( hibernateMapping.getDatabaseObject() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : hibernateMapping.getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classLoaderService().classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							jaxbRoot.getOrigin()
 					);
 				}
 			}
 			else {
 				Set<String> dialectScopes = new HashSet<String>();
 				if ( databaseObjectElement.getDialectScope() != null ) {
 					for ( XMLHibernateMapping.XMLDatabaseObject.XMLDialectScope dialectScope : databaseObjectElement.getDialectScope() ) {
 						dialectScopes.add( dialectScope.getName() );
 					}
 				}
 				auxiliaryDatabaseObject = new BasicAuxiliaryDatabaseObjectImpl(
 						databaseObjectElement.getCreate(),
 						databaseObjectElement.getDrop(),
 						dialectScopes
 				);
 			}
 			metadata.addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 	}
 
 	private void bindTypeDefinitions() {
 		if ( hibernateMapping.getTypedef() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLTypedef typedef : hibernateMapping.getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( XMLParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition( new TypeDef( typedef.getName(), typedef.getClazz(), parameters ) );
 		}
 	}
 
 	public void bindTypeDependentMetadata() {
 		bindFilterDefinitions();
 		bindIdentifierGenerators();
 	}
 
 	private void bindFilterDefinitions() {
 		if(hibernateMapping.getFilterDef() == null){
 			return;
 		}
 		for ( XMLHibernateMapping.XMLFilterDef filterDefinition : hibernateMapping.getFilterDef() ) {
 			final String name = filterDefinition.getName();
 			final Map<String,Type> parameters = new HashMap<String, Type>();
 			String condition = null;
 			for ( Object o : filterDefinition.getContent() ) {
 				if ( o instanceof String ) {
 					// represents the condition
 					if ( condition != null ) {
 						// log?
 					}
 					condition = (String) o;
 				}
 				else if ( o instanceof XMLHibernateMapping.XMLFilterDef.XMLFilterParam ) {
 					final XMLHibernateMapping.XMLFilterDef.XMLFilterParam paramElement = (XMLHibernateMapping.XMLFilterDef.XMLFilterParam) o;
 					// todo : should really delay this resolution until later to allow typedef names
 					parameters.put(
 							paramElement.getName(),
 							metadata.getTypeResolver().heuristicType( paramElement.getType() )
 					);
 				}
 				else {
 					throw new MappingException( "Unrecognized nested filter content", jaxbRoot.getOrigin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void bindIdentifierGenerators() {
 		if ( hibernateMapping.getIdentifierGenerator() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLIdentifierGenerator identifierGeneratorElement : hibernateMapping.getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
 	public void bindMappingMetadata(List<String> processedEntityNames) {
 		if ( hibernateMapping.getClazzOrSubclassOrJoinedSubclass() == null ) {
 			return;
 		}
 		for ( Object clazzOrSubclass : hibernateMapping.getClazzOrSubclassOrJoinedSubclass() ) {
 			if ( XMLHibernateMapping.XMLClass.class.isInstance( clazzOrSubclass ) ) {
 				XMLHibernateMapping.XMLClass clazz =
 						XMLHibernateMapping.XMLClass.class.cast( clazzOrSubclass );
 				new RootEntityBinder( this, clazz ).process( clazz );
 			}
 			else if ( XMLSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLJoinedSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLUnionSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"unknown type of class or subclass: " +
 								clazzOrSubclass.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	public void bindMappingDependentMetadata() {
 		bindFetchProfiles();
 		bindImports();
 		bindResultSetMappings();
 		bindNamedQueries();
 	}
 
 	private void bindFetchProfiles(){
 		if(hibernateMapping.getFetchProfile() == null){
 			return;
 		}
 		bindFetchProfiles( hibernateMapping.getFetchProfile(),null );
 	}
 
 	public void bindFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( XMLFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( XMLFetchProfileElement.XMLFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							jaxbRoot.getOrigin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void bindImports() {
 		if ( hibernateMapping.getImport() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLImport importValue : hibernateMapping.getImport() ) {
 			String className = getClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void bindResultSetMappings() {
 		if ( hibernateMapping.getResultset() == null ) {
 			return;
 		}
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void bindNamedQueries() {
 		if ( hibernateMapping.getQueryOrSqlQuery() == null ) {
 			return;
 		}
 		for ( Object queryOrSqlQuery : hibernateMapping.getQueryOrSqlQuery() ) {
 			if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	private ClassLoaderService classLoaderService;
 
 	private ClassLoaderService classLoaderService() {
 		if ( classLoaderService == null ) {
 			classLoaderService = metadata.getServiceRegistry().getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public String extractEntityName(XMLHibernateMapping.XMLClass entityClazz) {
 		return HbmHelper.extractEntityName( entityClazz, mappingDefaults.getPackageName() );
 	}
 
 	@Override
 	public String getClassName(String unqualifiedName) {
 		return HbmHelper.getClassName( unqualifiedName, mappingDefaults.getPackageName() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
index 0bd764d189..e4ac9542b8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
@@ -1,331 +1,336 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.hbm;
 
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.InLineView;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmDiscriminatorBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLCompositeId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 class RootEntityBinder extends AbstractEntityBinder {
 
 	RootEntityBinder(HbmBindingContext bindingContext, XMLClass xmlClazz) {
 		super( bindingContext, xmlClazz );
 	}
 
 	public boolean isRoot() {
 		return true;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return InheritanceType.SINGLE_TABLE;
 	}
 
 	public void process(XMLClass xmlClazz) {
 		String entityName = getBindingContext().extractEntityName( xmlClazz );
 		if ( entityName == null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 
 		EntityBinding entityBinding = new EntityBinding();
 		basicEntityBinding( xmlClazz, entityBinding, null );
 		basicTableBinding( xmlClazz, entityBinding );
 
 		bindIdentifier( xmlClazz, entityBinding );
 		bindDiscriminator( xmlClazz, entityBinding );
 		bindVersionOrTimestamp( xmlClazz, entityBinding );
 
 		// called createClassProperties in HBMBinder...
 		buildAttributeBindings( xmlClazz, entityBinding );
 
 		getMetadata().addEntity( entityBinding );
 	}
 
 	private void basicTableBinding(XMLClass xmlClazz,
 								   EntityBinding entityBinding) {
 		final Schema schema = getMetadata().getDatabase().getSchema( getSchemaName() );
 
 		final String subSelect =
 				xmlClazz.getSubselectAttribute() == null ? xmlClazz.getSubselect() : xmlClazz.getSubselectAttribute();
 		if ( subSelect != null ) {
 			final String logicalName = entityBinding.getEntity().getName();
 			InLineView inLineView = schema.getInLineView( logicalName );
 			if ( inLineView == null ) {
 				inLineView = schema.createInLineView( logicalName, subSelect );
 			}
 			entityBinding.setBaseTable( inLineView );
 		}
 		else {
-			final Identifier tableName = Identifier.toIdentifier( getClassTableName( xmlClazz, entityBinding, null ) );
+            String classTableName = getClassTableName( xmlClazz, entityBinding, null );
+            if(getBindingContext().isGloballyQuotedIdentifiers()){
+                classTableName = StringHelper.quote( classTableName );
+            }
+			final Identifier tableName = Identifier.toIdentifier( classTableName );
 			org.hibernate.metamodel.relational.Table table = schema.getTable( tableName );
 			if ( table == null ) {
 				table = schema.createTable( tableName );
 			}
 			entityBinding.setBaseTable( table );
 			String comment = xmlClazz.getComment();
 			if ( comment != null ) {
 				table.addComment( comment.trim() );
 			}
 			String check = xmlClazz.getCheck();
 			if ( check != null ) {
 				table.addCheckConstraint( check );
 			}
 		}
 	}
 
 	private void bindIdentifier(XMLClass xmlClazz,
 								EntityBinding entityBinding) {
 		if ( xmlClazz.getId() != null ) {
 			bindSimpleId( xmlClazz.getId(), entityBinding );
 			return;
 		}
 
 		if ( xmlClazz.getCompositeId() != null ) {
 			bindCompositeId( xmlClazz.getCompositeId(), entityBinding );
 		}
 
 		throw new InvalidMappingException(
 				"Entity [" + entityBinding.getEntity().getName() + "] did not contain identifier mapping",
 				getBindingContext().getOrigin()
 		);
 	}
 
 	private void bindSimpleId(XMLId id, EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
 				entityBinding.getEntity().getJavaType().getName(),
 				getBindingContext(),
 				entityBinding.getMetaAttributeContext(),
 				id
 		);
 		// boolean (true here) indicates that by default column names should be guessed
 		HbmSimpleValueRelationalStateContainer relationalStateContainer = new HbmSimpleValueRelationalStateContainer(
 				getBindingContext(), true, id
 		);
 		if ( relationalStateContainer.getRelationalStates().size() > 1 ) {
 			throw new MappingException( "ID is expected to be a single column, but has more than 1 value" );
 		}
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeSimpleIdAttributeBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalStateContainer.getRelationalStates().get( 0 ) );
 
 		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
 		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		// if ( !id.isTypeSpecified() ) {
 		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
 		// );
 		// }
 		// }
 		// else {
 		// bindSimpleValue( idNode, id, false, propertyName, mappings );
 		// PojoRepresentation pojo = entity.getPojoRepresentation();
 		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
 		//
 		// Property prop = new Property();
 		// prop.setValue( id );
 		// bindProperty( idNode, prop, mappings, inheritedMetas );
 		// entity.setIdentifierProperty( prop );
 		// }
 
 //		if ( propertyName == null ) {
 //			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 //		}
 //		else {
 //			bindSimpleValue( idNode, id, false, propertyName, mappings );
 //		}
 //
 //		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 //			if ( !id.isTypeSpecified() ) {
 //				throw new MappingException( "must specify an identifier type: "
 //					+ entity.getEntityName() );
 //			}
 //		}
 //		else {
 //			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 //		}
 //
 //		if ( propertyName != null ) {
 //			Property prop = new Property();
 //			prop.setValue( id );
 //			bindProperty( idNode, prop, mappings, inheritedMetas );
 //			entity.setIdentifierProperty( prop );
 //		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 //		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(XMLCompositeId compositeId, EntityBinding entityBinding) {
 		final String explicitName = compositeId.getName();
 
 //		String propertyName = idNode.attributeValue( "name" );
 //		Component id = new Component( mappings, entity );
 //		entity.setIdentifier( id );
 //		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 //		if ( propertyName == null ) {
 //			entity.setEmbeddedIdentifier( id.isEmbedded() );
 //			if ( id.isEmbedded() ) {
 //				// todo : what is the implication of this?
 //				id.setDynamic( !entity.hasPojoRepresentation() );
 //				/*
 //				 * Property prop = new Property(); prop.setName("id");
 //				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 //				 * entity.setIdentifierProperty(prop);
 //				 */
 //			}
 //		}
 //		else {
 //			Property prop = new Property();
 //			prop.setValue( id );
 //			bindProperty( idNode, prop, mappings, inheritedMetas );
 //			entity.setIdentifierProperty( prop );
 //		}
 //
 //		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private void bindDiscriminator(XMLClass xmlEntityClazz,
 								   EntityBinding entityBinding) {
 		if ( xmlEntityClazz.getDiscriminator() == null ) {
 			return;
 		}
 
 		DiscriminatorBindingState bindingState = new HbmDiscriminatorBindingState(
 				entityBinding.getEntity().getJavaType().getName(),
 				entityBinding.getEntity().getName(),
 				getBindingContext(),
 				xmlEntityClazz
 		);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState = convertToSimpleValueRelationalStateIfPossible(
 				new HbmSimpleValueRelationalStateContainer(
 						getBindingContext(),
 						true,
 						xmlEntityClazz.getDiscriminator()
 				)
 		);
 
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeEntityDiscriminator( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	private void bindVersionOrTimestamp(XMLClass xmlEntityClazz,
 										EntityBinding entityBinding) {
 		if ( xmlEntityClazz.getVersion() != null ) {
 			bindVersion(
 					xmlEntityClazz.getVersion(),
 					entityBinding
 			);
 		}
 		else if ( xmlEntityClazz.getTimestamp() != null ) {
 			bindTimestamp(
 					xmlEntityClazz.getTimestamp(),
 					entityBinding
 			);
 		}
 	}
 
 	protected void bindVersion(XMLHibernateMapping.XMLClass.XMLVersion version,
 							   EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState =
 				new HbmSimpleAttributeBindingState(
 						entityBinding.getEntity().getJavaType().getName(),
 						getBindingContext(),
 						entityBinding.getMetaAttributeContext(),
 						version
 				);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								getBindingContext(),
 								true,
 								version
 						)
 				);
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeVersionBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	protected void bindTimestamp(XMLHibernateMapping.XMLClass.XMLTimestamp timestamp,
 								 EntityBinding entityBinding) {
 
 		SimpleAttributeBindingState bindingState =
 				new HbmSimpleAttributeBindingState(
 						entityBinding.getEntity().getJavaType().getName(),
 						getBindingContext(),
 						entityBinding.getMetaAttributeContext(),
 						timestamp
 				);
 
 		// relational model has not been bound yet
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								getBindingContext(),
 								true,
 								timestamp
 						)
 				);
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeVersionBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
index 53b2b222bf..e4680699e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
@@ -1,272 +1,276 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.hbm.state.relational;
 
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
 
 // TODO: remove duplication after Id, Discriminator, Version, Timestamp, and Property extend a common interface.
 
 /**
  * @author Gail Badner
  */
 public class HbmColumnRelationalState implements ColumnRelationalState {
 	private final HbmSimpleValueRelationalStateContainer container;
 	private final String explicitColumnName;
 	private final Size size;
 	private final boolean isNullable;
 	private final boolean isUnique;
 	private final String checkCondition;
 	private final String defaultColumnValue;
 	private final String sqlType;
 	private final String customWrite;
 	private final String customRead;
 	private final String comment;
 	private final Set<String> uniqueKeys;
 	private final Set<String> indexes;
 
 	/* package-protected */
 	HbmColumnRelationalState(XMLColumnElement columnElement,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = columnElement.getName();
 		this.size = createSize( columnElement.getLength(), columnElement.getScale(), columnElement.getPrecision() );
 		this.isNullable = !MappingHelper.getBooleanValue( columnElement.isNotNull(), true );
 		this.isUnique = MappingHelper.getBooleanValue( columnElement.isUnique(), true );
 		this.checkCondition = columnElement.getCheck();
 		this.defaultColumnValue = columnElement.getDefault();
 		this.sqlType = columnElement.getSqlType();
 		this.customWrite = columnElement.getWrite();
 		if ( customWrite != null && !customWrite.matches( "[^?]*\\?[^?]*" ) ) {
 			throw new MappingException( "write expression must contain exactly one value placeholder ('?') character" );
 		}
 		this.customRead = columnElement.getRead();
 		this.comment = columnElement.getComment() == null ? null : columnElement.getComment().trim();
 		this.uniqueKeys = MappingHelper.getStringValueTokens( columnElement.getUniqueKey(), ", " );
 		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
 		this.indexes = MappingHelper.getStringValueTokens( columnElement.getIndex(), ", " );
 		this.indexes.addAll( container.getPropertyIndexes() );
 	}
 
 	HbmColumnRelationalState(XMLPropertyElement property,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = property.getName();
 		this.size = createSize( property.getLength(), property.getScale(), property.getPrecision() );
 		this.isUnique = MappingHelper.getBooleanValue( property.isUnique(), true );
 		this.isNullable = !MappingHelper.getBooleanValue( property.isNotNull(), true );
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = MappingHelper.getStringValueTokens( property.getUniqueKey(), ", " );
 		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
 		this.indexes = MappingHelper.getStringValueTokens( property.getIndex(), ", " );
 		this.indexes.addAll( container.getPropertyIndexes() );
 	}
 
 	HbmColumnRelationalState(XMLManyToOneElement manyToOne,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = manyToOne.getName();
 		this.size = new Size();
 		this.isNullable = !MappingHelper.getBooleanValue( manyToOne.isNotNull(), false );
 		this.isUnique = manyToOne.isUnique();
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = MappingHelper.getStringValueTokens( manyToOne.getUniqueKey(), ", " );
 		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
 		this.indexes = MappingHelper.getStringValueTokens( manyToOne.getIndex(), ", " );
 		this.indexes.addAll( container.getPropertyIndexes() );
 	}
 
 	HbmColumnRelationalState(XMLId id,
 							 HbmSimpleValueRelationalStateContainer container) {
 		if ( id.getColumn() != null && !id.getColumn().isEmpty() ) {
 			throw new IllegalArgumentException( "This method should not be called with non-empty id.getColumnElement()" );
 		}
 		this.container = container;
 		this.explicitColumnName = id.getName();
 		this.size = createSize( id.getLength(), null, null );
 		this.isNullable = false;
 		this.isUnique = true;
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	HbmColumnRelationalState(XMLDiscriminator discriminator,
 							 HbmSimpleValueRelationalStateContainer container) {
 		if ( discriminator.getColumn() != null ) {
 			throw new IllegalArgumentException(
 					"This method should not be called with null discriminator.getColumnElement()"
 			);
 		}
 		this.container = container;
 		this.explicitColumnName = null;
 		this.size = createSize( discriminator.getLength(), null, null );
 		this.isNullable = false;
 		this.isUnique = true;
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	HbmColumnRelationalState(XMLVersion version,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = version.getColumnAttribute();
 		if ( version.getColumn() != null && !version.getColumn().isEmpty() ) {
 			throw new IllegalArgumentException(
 					"This method should not be called with non-empty version.getColumnElement()"
 			);
 		}
 		// TODO: should set default
 		this.size = new Size();
 		this.isNullable = false;
 		this.isUnique = false;
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	HbmColumnRelationalState(XMLTimestamp timestamp,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = timestamp.getColumn();
 		// TODO: should set default
 		this.size = new Size();
 		this.isNullable = false;
 		this.isUnique = true; // well, it should hopefully be unique...
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return container.getNamingStrategy();
 	}
 
+    public boolean isGloballyQuotedIdentifiers(){
+        return  container.isGloballyQuotedIdentifiers();
+    }
+
 	public String getExplicitColumnName() {
 		return explicitColumnName;
 	}
 
 	public Size getSize() {
 		return size;
 	}
 
 	protected static Size createSize(String length, String scale, String precision) {
 		// TODO: should this set defaults if length, scale, precision is not specified?
 		Size size = new Size();
 		if ( length != null ) {
 			size.setLength( Integer.parseInt( length ) );
 		}
 		if ( scale != null ) {
 			size.setScale( Integer.parseInt( scale ) );
 		}
 		if ( precision != null ) {
 			size.setPrecision( Integer.parseInt( precision ) );
 		}
 		// TODO: is there an attribute for lobMultiplier?
 		return size;
 	}
 
 	public boolean isNullable() {
 		return isNullable;
 	}
 
 	public boolean isUnique() {
 		return isUnique;
 	}
 
 	public String getCheckCondition() {
 		return checkCondition;
 	}
 
 	public String getDefault() {
 		return defaultColumnValue;
 	}
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public String getCustomWriteFragment() {
 		return customWrite;
 	}
 
 	public String getCustomReadFragment() {
 		return customRead;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public Set<String> getUniqueKeys() {
 		return uniqueKeys;
 	}
 
 	public Set<String> getIndexes() {
 		return indexes;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
index b05a197388..bcca4af761 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
@@ -1,223 +1,225 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.hbm.state.relational;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
 import org.hibernate.metamodel.relational.state.SimpleValueRelationalState;
 import org.hibernate.metamodel.relational.state.TupleRelationalState;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.spi.BindingContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmSimpleValueRelationalStateContainer implements TupleRelationalState {
 	private final BindingContext bindingContext;
 	private final Set<String> propertyUniqueKeys;
 	private final Set<String> propertyIndexes;
 	private final List<SimpleValueRelationalState> simpleValueStates;
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 
 	public BindingContext getBindingContext() {
 		return bindingContext;
 	}
-
+    public boolean isGloballyQuotedIdentifiers(){
+        return getBindingContext().isGloballyQuotedIdentifiers();
+    }
 	public NamingStrategy getNamingStrategy() {
 		return getBindingContext().getNamingStrategy();
 	}
 
 	// TODO: remove duplication after Id, Discriminator, Version, Timestamp, and Property extend a common interface.
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLId id) {
 		this( bindingContext, id.getColumn() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( id.getColumn() == null && ! autoColumnCreation ) {
 				throw new MappingException( "No columns to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( id, this ) );
 		}
 		else if ( id.getColumn() != null ) {
 			throw new MappingException( "column attribute may not be used together with <column> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( id.getTypeAttribute() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLDiscriminator discriminator) {
 		this( bindingContext, discriminator.getFormula(), discriminator.getColumn() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( discriminator.getColumn() == null && discriminator.getFormula() == null &&  ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( discriminator, this ) );
 		}
 		else if ( discriminator.getColumn() != null || discriminator.getFormula() != null) {
 			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( discriminator.getType() == null ? "string" : discriminator.getType() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLVersion version) {
 		this( bindingContext, version.getColumn() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( version.getColumn() == null && ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( version, this ) );
 		}
 		else if ( version.getColumn() != null ) {
 			throw new MappingException( "column attribute may not be used together with <column> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( version.getType() == null ? "integer" : version.getType() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLTimestamp timestamp) {
 		this( bindingContext, null );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( timestamp.getColumn() == null && ! autoColumnCreation ) {
 				throw new MappingException( "No columns to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( timestamp, this ) );
 		}
 		else if ( timestamp.getColumn() != null ) {
 			throw new MappingException( "column attribute may not be used together with <column> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( "db".equals( timestamp.getSource() ) ? "dbtimestamp" : "timestamp" );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLPropertyElement property) {
 		this( bindingContext, property.getColumnOrFormula() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( property.getColumn() == null && property.getFormula() == null &&  ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( property, this ) );
 		}
 		else if ( property.getColumn() != null || property.getFormula() != null) {
 			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( property.getTypeAttribute() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLManyToOneElement manyToOne) {
 		this( bindingContext, manyToOne.getColumnOrFormula() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( manyToOne.getColumn() == null && manyToOne.getFormula() == null &&  ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( manyToOne, this ) );
 		}
 		else if ( manyToOne.getColumn() != null || manyToOne.getFormula() != null) {
 			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
 		}
 	}
 
 	private HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			String formulaElement,
 			XMLColumnElement columnElement) {
 		this( bindingContext,
 				formulaElement != null
 						? Collections.singletonList( formulaElement )
 						: columnElement != null
 								? Collections.singletonList( columnElement )
 								: Collections.<Object>emptyList()
 		);
 	}
 
 	private HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			List mappedColumnsOrFormulas) {
 		this.bindingContext = bindingContext;
 		this.propertyUniqueKeys = Collections.emptySet();
 		this.propertyIndexes = Collections.emptySet();
 		simpleValueStates = new ArrayList<SimpleValueRelationalState>(
 							mappedColumnsOrFormulas == null || mappedColumnsOrFormulas.isEmpty()
 									? 1
 									: mappedColumnsOrFormulas.size()
 		);
 		if ( mappedColumnsOrFormulas != null && ! mappedColumnsOrFormulas.isEmpty() ) {
 			for ( Object mappedColumnOrFormula : mappedColumnsOrFormulas ) {
 				simpleValueStates.add( createColumnOrFormulaRelationalState( this, mappedColumnOrFormula ) );
 			}
 		}
 	}
 
 	private static SimpleValueRelationalState createColumnOrFormulaRelationalState(
 			HbmSimpleValueRelationalStateContainer container,
 			Object columnOrFormula) {
 		if ( XMLColumnElement.class.isInstance( columnOrFormula ) ) {
 			return new HbmColumnRelationalState(
 					XMLColumnElement.class.cast( columnOrFormula ),
 					container
 			);
 		}
 		else if ( String.class.isInstance( columnOrFormula ) ) {
 			return new HbmDerivedValueRelationalState( String.class.cast( columnOrFormula ) );
 		}
 		throw new MappingException( "unknown type of column or formula: " + columnOrFormula.getClass().getName() );
 	}
 
 	public List<SimpleValueRelationalState> getRelationalStates() {
 		return simpleValueStates;
 	}
 
 	Set<String> getPropertyUniqueKeys() {
 		return propertyUniqueKeys;
 	}
 
 	Set<String> getPropertyIndexes() {
 		return propertyIndexes;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
index 4342235468..0bb2ea2cce 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
@@ -1,179 +1,196 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.internal;
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.config.spi.ConfigurationService;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataBuilderImpl implements MetadataBuilder {
 	private final MetadataSources sources;
 	private final OptionsImpl options;
 
 	public MetadataBuilderImpl(MetadataSources sources) {
 		this.sources = sources;
 		this.options = new OptionsImpl( sources.getServiceRegistry() );
 	}
 
 	@Override
 	public MetadataBuilder with(NamingStrategy namingStrategy) {
 		this.options.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(SourceProcessingOrder sourceProcessingOrder) {
 		this.options.sourceProcessingOrder = sourceProcessingOrder;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
 		this.options.sharedCacheMode = sharedCacheMode;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(AccessType accessType) {
 		this.options.defaultCacheAccessType = accessType;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
 		this.options.useNewIdentifierGenerators = enabled;
 		return this;
 	}
 
 	@Override
 	public Metadata buildMetadata() {
 		return new MetadataImpl( sources, options );
 	}
 
 	private static class OptionsImpl implements Metadata.Options {
 		private SourceProcessingOrder sourceProcessingOrder = SourceProcessingOrder.HBM_FIRST;
 		private NamingStrategy namingStrategy = EJB3NamingStrategy.INSTANCE;
 		private SharedCacheMode sharedCacheMode = SharedCacheMode.ENABLE_SELECTIVE;
 		private AccessType defaultCacheAccessType;
         private boolean useNewIdentifierGenerators;
+        private boolean globallyQuotedIdentifiers;
 		private String defaultSchemaName;
 		private String defaultCatalogName;
 
 		public OptionsImpl(BasicServiceRegistry serviceRegistry) {
 			ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
 
 			// cache access type
 			defaultCacheAccessType = configService.getSetting(
 					AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY,
 					new ConfigurationService.Converter<AccessType>() {
 						@Override
 						public AccessType convert(Object value) {
 							return AccessType.fromExternalName( value.toString() );
 						}
 					}
 			);
 
 			useNewIdentifierGenerators = configService.getSetting(
 					AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS,
 					new ConfigurationService.Converter<Boolean>() {
 						@Override
 						public Boolean convert(Object value) {
 							return Boolean.parseBoolean( value.toString() );
 						}
 					},
 					false
 			);
 
 			defaultSchemaName = configService.getSetting(
 					AvailableSettings.DEFAULT_SCHEMA,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
 			defaultCatalogName = configService.getSetting(
 					AvailableSettings.DEFAULT_CATALOG,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
+
+            globallyQuotedIdentifiers = configService.getSetting(
+                    AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS,
+                    new ConfigurationService.Converter<Boolean>() {
+                        @Override
+                        public Boolean convert(Object value) {
+                            return Boolean.parseBoolean( value.toString() );
+                        }
+                    },
+                    false
+            );
 		}
 
 
 		@Override
 		public SourceProcessingOrder getSourceProcessingOrder() {
 			return sourceProcessingOrder;
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		@Override
 		public AccessType getDefaultAccessType() {
 			return defaultCacheAccessType;
 		}
 
 		@Override
 		public SharedCacheMode getSharedCacheMode() {
 			return sharedCacheMode;
 		}
 
 		@Override
         public boolean useNewIdentifierGenerators() {
             return useNewIdentifierGenerators;
         }
 
-		@Override
+        @Override
+        public boolean isGloballyQuotedIdentifiers() {
+            return globallyQuotedIdentifiers;
+        }
+
+        @Override
 		public String getDefaultSchemaName() {
 			return defaultSchemaName;
 		}
 
 		@Override
 		public String getDefaultCatalogName() {
 			return defaultCatalogName;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index acb9001c29..5584f9f330 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,532 +1,543 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
+import org.apache.commons.collections.functors.FalsePredicate;
 import org.jboss.logging.Logger;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.hbm.HbmBinder;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 	private ClassLoaderService classLoaderService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database = new Database();
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
 	// todo : keep as part of Database?
 	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
+    private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final Binder[] binders;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			binders = new Binder[] {
 					new HbmBinder( this ),
 					new AnnotationBinder( this )
 			};
 		}
 		else {
 			binders = new Binder[] {
 					new AnnotationBinder( this ),
 					new HbmBinder( this )
 			};
 		}
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( binders, metadataSources );
 		bindIndependentMetadata( binders, metadataSources );
 		bindTypeDependentMetadata( binders, metadataSources );
 		bindMappingMetadata( binders, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( binders, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
 	private void prepare(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
 		if ( def == null || def.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + def );
 		}
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
 		idGenerators.put( generator.getName(), generator );
 	}
 
 	@Override
 	public IdGenerator getIdGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGenerators.get( name );
 	}
 	@Override
 	public void registerIdentifierGenerator(String name, String generatorClassName) {
 		 identifierGeneratorFactory.register( name, classLoaderService().classForName( generatorClassName ) );
 	}
 
 	@Override
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		if ( auxiliaryDatabaseObject == null ) {
 			throw new IllegalArgumentException( "Auxiliary database object is null." );
 		}
 		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 	}
 
 	@Override
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition object or name is null: " + def.getQueryString() );
 		}
 		namedNativeQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryDefs.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition object or name is null: " + def.getQueryString() );
 		}
 		namedQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryDefs.values();
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null || resultSetMappingDefinition.getName() == null ) {
 			throw new IllegalArgumentException( "Resultset mappping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null || typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition object or name is null: " + typeDef.getTypeClass() );
 		}
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService(){
 		if(classLoaderService==null){
 			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService().classForName( name );
 	}
 
 	@Override
 	public JavaType makeJavaType(String className) {
 		return new JavaType( className, classLoaderService() );
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
 		if ( rootEntityBinding == null ) {
 			EntityBinding entityBinding = entityBindingMap.get( entityName );
 			if ( entityBinding == null ) {
 				throw new IllegalStateException( "Unknown entity binding: " + entityName );
 			}
 			if ( entityBinding.isRoot() ) {
 				rootEntityBinding = entityBinding;
 			}
 			else {
 				if ( entityBinding.getEntity().getSuperType() == null ) {
 					throw new IllegalStateException( "Entity binding has no root: " + entityName );
 				}
 				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
 			}
 			rootEntityBindingMap.put( entityName, rootEntityBinding );
 		}
 		return rootEntityBinding;
 	}
 
 	public Iterable<EntityBinding> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	public void addEntity(EntityBinding entityBinding) {
 		final String entityName = entityBinding.getEntity().getName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, entityBinding );
 	}
 
 	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
 	public Iterable<PluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getEntityBinding().getEntity().getName();
 		final String attributeName = pluralAttributeBinding.getAttribute().getName();
 		final String collectionRole = owningEntityName + '.' + attributeName;
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, pluralAttributeBinding );
 	}
 
 	public void addImport(String importName, String entityName) {
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	public Iterable<Map.Entry<String, String>> getImports() {
 		return imports.entrySet();
 	}
 
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		return sessionFactoryBuilder;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
-	@Override
+    @Override
+    public boolean isGloballyQuotedIdentifiers() {
+        return globallyQuotedIdentifiers || getOptions().isGloballyQuotedIdentifiers();
+    }
+
+    public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
+       this.globallyQuotedIdentifiers = globallyQuotedIdentifiers;
+    }
+
+    @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return globalMetaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return this;
 	}
 
 	private static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	private static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	private static final String DEFAULT_CASCADE = "none";
 	private static final String DEFAULT_PROPERTY_ACCESS = "property";
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getExplicitType();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		AttributeBinding idBinding = entityBinding.getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
 	}
 
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getCascadeStyle() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean areAssociationsLazy() {
 			return true;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
index 609052e6b3..ae45398884 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
@@ -1,47 +1,49 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.spi;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public interface BindingContext {
-	public ServiceRegistry getServiceRegistry();
+    public ServiceRegistry getServiceRegistry();
 
-	public NamingStrategy getNamingStrategy();
+    public NamingStrategy getNamingStrategy();
 
-	public MappingDefaults getMappingDefaults();
+    public MappingDefaults getMappingDefaults();
 
-	public MetaAttributeContext getMetaAttributeContext();
+    public MetaAttributeContext getMetaAttributeContext();
 
-	public MetadataImplementor getMetadataImplementor();
+    public MetadataImplementor getMetadataImplementor();
 
-	public <T> Class<T> locateClassByName(String name);
+    public <T> Class<T> locateClassByName(String name);
 
-	public JavaType makeJavaType(String className);
+    public JavaType makeJavaType(String className);
+
+    public boolean isGloballyQuotedIdentifiers();
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
index 787968c82f..4a417a7021 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
@@ -1,70 +1,77 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.entity;
 
 import org.junit.After;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Hardy Ferentschik
  */
 public abstract class BaseAnnotationBindingTestCase extends BaseUnitTestCase {
 	protected MetadataSources sources;
 	protected MetadataImpl meta;
 
 	@After
 	public void tearDown() {
 		sources = null;
 		meta = null;
-	}
+    }
+
+    public void buildMetadataSources(String ormPath, Class<?>... classes) {
+        sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
+        if(ormPath!=null){
+            sources.addResource( ormPath );
+        }
+        for ( Class clazz : classes ) {
+            sources.addAnnotatedClass( clazz );
+        }
+    }
 
 	public void buildMetadataSources(Class<?>... classes) {
-		sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
-		for ( Class clazz : classes ) {
-			sources.addAnnotatedClass( clazz );
-		}
+		buildMetadataSources( null, classes );
 	}
 
 	public EntityBinding getEntityBinding(Class<?> clazz) {
 		if ( meta == null ) {
 			meta = (MetadataImpl) sources.buildMetadata();
 		}
 		return meta.getEntityBinding( clazz.getName() );
 	}
 
 	public EntityBinding getRootEntityBinding(Class<?> clazz) {
 		if ( meta == null ) {
 			meta = (MetadataImpl) sources.buildMetadata();
 		}
 		return meta.getRootEntityBinding( clazz.getName() );
 	}
 
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTests.java
new file mode 100644
index 0000000000..b2a14a5abd
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/QuotedIdentifierTests.java
@@ -0,0 +1,69 @@
+package org.hibernate.metamodel.source.annotations.entity;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+import org.junit.Test;
+
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.relational.Identifier;
+
+import static org.junit.Assert.assertEquals;
+
+/**
+ * @author Strong Liu
+ */
+public class QuotedIdentifierTests extends BaseAnnotationBindingTestCase {
+    String ormPath = "org/hibernate/metamodel/source/annotations/xml/orm-quote-identifier.xml";
+
+    @Test
+    public void testDelimitedIdentifiers() {
+        buildMetadataSources( ormPath, Item.class, Item2.class, Item3.class, Item4.class );
+        EntityBinding item = getEntityBinding( Item.class );
+        assertIdentifierEquals( "`Item`",item );
+
+        item = getEntityBinding( Item2.class );
+        assertIdentifierEquals( "`TABLE_ITEM2`",item );
+
+        item = getEntityBinding( Item3.class );
+        assertIdentifierEquals( "`TABLE_ITEM3`",item );
+
+        item = getEntityBinding( Item4.class );
+        assertIdentifierEquals( "`TABLE_ITEM4`",item );
+
+
+    }
+
+    private void assertIdentifierEquals(String expected, EntityBinding realValue) {
+        org.hibernate.metamodel.relational.Table table = (org.hibernate.metamodel.relational.Table) realValue.getBaseTable();
+        assertEquals( Identifier.toIdentifier( expected ), table.getTableName() );
+    }
+
+    @Entity
+    private static class Item {
+        @Id
+        Long id;
+    }
+
+    @Entity
+    @Table(name = "TABLE_ITEM2")
+    private static class Item2 {
+        @Id
+        Long id;
+    }
+
+    @Entity
+    @Table(name = "`TABLE_ITEM3`")
+    private static class Item3 {
+        @Id
+        Long id;
+    }
+
+    @Entity
+    @Table(name = "\"TABLE_ITEM4\"")
+    private static class Item4 {
+        @Id
+        Long id;
+    }
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java
index 5e3cbc326f..7263938361 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/TableNameTest.java
@@ -1,219 +1,219 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.Iterator;
 import java.util.Set;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Inheritance;
 import javax.persistence.Table;
 
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.util.ConfiguredClassHierarchyBuilder;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertTrue;
 import static org.junit.Assert.assertFalse;
 
 /**
  * @author Hardy Ferentschik
  */
 public class TableNameTest extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleInheritanceDefaultTableName() {
 		@Entity
 		class A {
 			@Id
 			@GeneratedValue
 			private int id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, A.class, B.class );
 		AnnotationBindingContext context = new AnnotationBindingContext( index, serviceRegistry );
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				context
 		);
 		assertEquals( "There should be only one hierarchy", 1, hierarchies.size() );
 
 		Iterator<EntityClass> iter = hierarchies.iterator().next().iterator();
 		EntityClass entityClass = iter.next();
 		ClassInfo info = entityClass.getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( A.class.getName() ), info.name() );
 		assertTrue( entityClass.hasOwnTable() );
 		Assert.assertEquals(
 				"wrong inheritance type", InheritanceType.SINGLE_TABLE, entityClass.getInheritanceType()
 		);
 		Assert.assertEquals(
 				"wrong table name", "A", entityClass.getPrimaryTableName()
 		);
 
 		assertTrue( iter.hasNext() );
 		entityClass = iter.next();
 		info = entityClass.getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( B.class.getName() ), info.name() );
 		assertFalse( entityClass.hasOwnTable() );
 		Assert.assertEquals(
 				"wrong inheritance type", InheritanceType.SINGLE_TABLE, entityClass.getInheritanceType()
 		);
 		Assert.assertEquals(
 				"wrong table name", "A", entityClass.getPrimaryTableName()
 		);
 
 		assertFalse( iter.hasNext() );
 	}
 
 	@Test
 	public void testTablePerClassDefaultTableName() {
 		@Entity
 		@Inheritance(strategy = javax.persistence.InheritanceType.TABLE_PER_CLASS)
 		class A {
 			@Id
 			@GeneratedValue
 			private int id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, A.class, B.class );
 		AnnotationBindingContext context = new AnnotationBindingContext( index, serviceRegistry );
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				context
 		);
 		assertEquals( "There should be only one hierarchy", 1, hierarchies.size() );
 
 		Iterator<EntityClass> iter = hierarchies.iterator().next().iterator();
 		EntityClass entityClass = iter.next();
 		ClassInfo info = entityClass.getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( A.class.getName() ), info.name() );
 		assertTrue( entityClass.hasOwnTable() );
 		Assert.assertEquals(
 				"wrong inheritance type", InheritanceType.TABLE_PER_CLASS, entityClass.getInheritanceType()
 		);
 		Assert.assertEquals(
 				"wrong table name", "A", entityClass.getPrimaryTableName()
 		);
 
 		assertTrue( iter.hasNext() );
 		entityClass = iter.next();
 		info = entityClass.getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( B.class.getName() ), info.name() );
 		assertTrue( entityClass.hasOwnTable() );
 		Assert.assertEquals(
 				"wrong inheritance type", InheritanceType.TABLE_PER_CLASS, entityClass.getInheritanceType()
 		);
 		Assert.assertEquals(
 				"wrong table name", "B", entityClass.getPrimaryTableName()
 		);
 
 		assertFalse( iter.hasNext() );
 	}
 
 	@Test
 	public void testJoinedSubclassDefaultTableName() {
 		@Entity
 		@Inheritance(strategy = javax.persistence.InheritanceType.JOINED)
 		@Table(name = "FOO")
 		class A {
 			@Id
 			@GeneratedValue
 			private int id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		AnnotationBindingContext context = new AnnotationBindingContext( index, serviceRegistry );
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				context
 		);
 		assertEquals( "There should be only one hierarchy", 1, hierarchies.size() );
 
 		Iterator<EntityClass> iter = hierarchies.iterator().next().iterator();
 		EntityClass entityClass = iter.next();
 		ClassInfo info = entityClass.getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( A.class.getName() ), info.name() );
 		assertTrue( entityClass.hasOwnTable() );
 		Assert.assertEquals(
 				"wrong inheritance type", InheritanceType.JOINED, entityClass.getInheritanceType()
 		);
 		Assert.assertEquals(
-				"wrong table name", "FOO", entityClass.getPrimaryTableName()
+				"wrong table name", "A", entityClass.getPrimaryTableName()
 		);
 
 		assertTrue( iter.hasNext() );
 		entityClass = iter.next();
 		info = entityClass.getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( B.class.getName() ), info.name() );
 		assertTrue( entityClass.hasOwnTable() );
 		Assert.assertEquals(
 				"wrong inheritance type", InheritanceType.JOINED, entityClass.getInheritanceType()
 		);
 		Assert.assertEquals(
 				"wrong table name", "B", entityClass.getPrimaryTableName()
 		);
 
 		assertFalse( iter.hasNext() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/resources/org/hibernate/metamodel/source/annotations/xml/orm-quote-identifier.xml b/hibernate-core/src/test/resources/org/hibernate/metamodel/source/annotations/xml/orm-quote-identifier.xml
new file mode 100644
index 0000000000..9f50560d69
--- /dev/null
+++ b/hibernate-core/src/test/resources/org/hibernate/metamodel/source/annotations/xml/orm-quote-identifier.xml
@@ -0,0 +1,11 @@
+<?xml version="1.0" encoding="UTF-8"?>
+
+<entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm"
+                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
+                 version="2.0">
+    <persistence-unit-metadata>
+        <persistence-unit-defaults>
+            <delimited-identifiers/>
+        </persistence-unit-defaults>
+    </persistence-unit-metadata>
+</entity-mappings>
\ No newline at end of file
