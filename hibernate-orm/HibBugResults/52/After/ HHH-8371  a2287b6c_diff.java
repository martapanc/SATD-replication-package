diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
index f5711ef8d6..92ca7eb057 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
@@ -1,780 +1,781 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.BitSet;
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
-			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
+			// Include "_".  See HHH-8073
+			if ( !Character.isLetter(character) && !('_'==character) ) return i-1;
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
 
 	public static String joinWithQualifier(String[] values, String qualifier, String deliminator) {
 		int length = values.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * values[0].length() )
 				.append( qualify( qualifier, values[0] ) );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( deliminator ).append( qualify( qualifier, values[i] ) );
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
 		final String[] result = new String[x.length];
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
 
 	public static String repeat(String string, int times, String deliminator) {
 		StringBuilder buf = new StringBuilder(  ( string.length() * times ) + ( deliminator.length() * (times-1) ) )
 				.append( string );
 		for ( int i = 1; i < times; i++ ) {
 			buf.append( deliminator ).append( string );
 		}
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
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
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
 			throw new NullPointerException( "prefix or name were null attempting to build qualified name" );
 		}
 		return prefix + '.' + name;
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) {
 			return names;
 		}
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 
 	public static String[] qualifyIfNot(String prefix, String[] names) {
 		if ( prefix == null ) {
 			return names;
 		}
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			if ( names[i].indexOf( '.' ) < 0 ) {
 				qualified[i] = qualify( prefix, names[i] );
 			}
 			else {
 				qualified[i] = names[i];
 			}
 		}
 		return qualified;
 	}
 
 	public static int firstIndexOfChar(String sqlString, BitSet keys, int startindex) {
 		for ( int i = startindex, size = sqlString.length(); i < size; i++ ) {
 			if ( keys.get( sqlString.charAt( i ) ) ) {
 				return i;
 			}
 		}
 		return -1;
 
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		BitSet keys = new BitSet();
 		for ( int i = 0, size = string.length(); i < size; i++ ) {
 			keys.set( string.charAt( i ) );
 		}
 		return firstIndexOfChar( sqlString, keys, startindex );
 
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
 		if ( isEmpty( name ) || isQuoted( name ) ) {
 			return name;
 		}
 // Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
         else if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) {
             name = name.substring( 1, name.length() - 1 );
         }
 
 		return new StringBuilder( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		return isQuoted( name ) ? name.substring( 1, name.length() - 1 ) : name;
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
 		return name != null
 				&&
 					name.length() != 0
 				&& (
 					name.charAt( 0 ) == '`'
 					&&
 					name.charAt( name.length() - 1 ) == '`'
 					||
 					name.charAt( 0 ) == dialect.openQuote()
 					&&
 					name.charAt( name.length() - 1 ) == dialect.closeQuote()
 				);
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
 		return isQuoted( name, dialect ) ? name.substring( 1, name.length() - 1 ) : name;
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
 
 
 	public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";
 
 	public static StringBuilder buildBatchFetchRestrictionFragment(
 			String alias,
 			String[] columnNames,
 			Dialect dialect) {
 		// the general idea here is to just insert a placeholder that we can easily find later...
 		if ( columnNames.length == 1 ) {
 			// non-composite key
 			return new StringBuilder( StringHelper.qualify( alias, columnNames[0] ) )
 					.append( " in (" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 		}
 		else {
 			// composite key - the form to use here depends on what the dialect supports.
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				// use : (col1, col2) in ( (?,?), (?,?), ... )
 				StringBuilder builder = new StringBuilder();
 				builder.append( "(" );
 				boolean firstPass = true;
 				String deliminator = "";
 				for ( String columnName : columnNames ) {
 					builder.append( deliminator ).append( StringHelper.qualify( alias, columnName ) );
 					if ( firstPass ) {
 						firstPass = false;
 						deliminator = ",";
 					}
 				}
 				builder.append( ") in (" );
 				builder.append( BATCH_ID_PLACEHOLDER );
 				builder.append( ")" );
 				return builder;
 			}
 			else {
 				// use : ( (col1 = ? and col2 = ?) or (col1 = ? and col2 = ?) or ... )
 				//		unfortunately most of this building needs to be held off until we know
 				//		the exact number of ids :(
 				return new StringBuilder( "(" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 			}
 		}
 	}
 
 	public static String expandBatchIdPlaceholder(
 			String sql,
 			Serializable[] ids,
 			String alias,
 			String[] keyColumnNames,
 			Dialect dialect) {
 		if ( keyColumnNames.length == 1 ) {
 			// non-composite
 			return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( "?", ids.length, "," ) );
 		}
 		else {
 			// composite
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				final String tuple = "(" + StringHelper.repeat( "?", keyColumnNames.length, "," );
 				return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( tuple, ids.length, "," ) );
 			}
 			else {
 				final String keyCheck = joinWithQualifier( keyColumnNames, alias, " and " );
 				return replace( sql, BATCH_ID_PLACEHOLDER, repeat( keyCheck, ids.length, " or " ) );
 			}
 		}
 	}
 	
 	/**
 	 * Takes a String s and returns a new String[1] with s as the only element.
 	 * If s is null or "", return String[0].
 	 * 
 	 * @param s
 	 * @return String[]
 	 */
 	public static String[] toArrayElement(String s) {
 		return ( s == null || s.length() == 0 ) ? new String[0] : new String[] { s };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
index 2c0dccc331..618accde88 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
@@ -1,367 +1,367 @@
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
 package org.hibernate.mapping;
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.Template;
 
 /**
  * A column of a relational database table
  * @author Gavin King
  */
 public class Column implements Selectable, Serializable, Cloneable {
 
 	public static final int DEFAULT_LENGTH = 255;
 	public static final int DEFAULT_PRECISION = 19;
 	public static final int DEFAULT_SCALE = 2;
 
 	private int length=DEFAULT_LENGTH;
 	private int precision=DEFAULT_PRECISION;
 	private int scale=DEFAULT_SCALE;
 	private Value value;
 	private int typeIndex = 0;
 	private String name;
 	private boolean nullable=true;
 	private boolean unique=false;
 	private String sqlType;
 	private Integer sqlTypeCode;
 	private boolean quoted=false;
 	int uniqueInteger;
 	private String checkConstraint;
 	private String comment;
 	private String defaultValue;
 	private String customWrite;
 	private String customRead;
 
 	public Column() {
 	}
 
 	public Column(String columnName) {
 		setName(columnName);
 	}
 
 	public int getLength() {
 		return length;
 	}
 	public void setLength(int length) {
 		this.length = length;
 	}
 	public Value getValue() {
 		return value;
 	}
 	public void setValue(Value value) {
 		this.value= value;
 	}
 	public String getName() {
 		return name;
 	}
 	public void setName(String name) {
 		if (
 			StringHelper.isNotEmpty( name ) &&
 			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
 		) {
 			quoted=true;
 			this.name=name.substring( 1, name.length()-1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/** returns quoted name as it would be in the mapping file. */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect d) {
 		return quoted ?
 			d.openQuote() + name + d.closeQuote() :
 			name;
 	}
 	
 	@Override
 	public String getAlias(Dialect dialect) {
 		final int lastLetter = StringHelper.lastIndexOfLetter( name );
-		String suffix = Integer.toString(uniqueInteger) + '_';
+		final String suffix = Integer.toString(uniqueInteger) + '_';
 
 		String alias = name;
 		if ( lastLetter == -1 ) {
 			alias = "column";
 		}
 		else if ( name.length() > lastLetter + 1 ) {
 			alias = name.substring( 0, lastLetter + 1 );
 		}
 
 		boolean useRawName = name.length() + suffix.length() <= dialect.getMaxAliasLength()
 				&& !quoted && !name.toLowerCase().equals( "rowid" );
 		if ( !useRawName ) {
 			if ( suffix.length() >= dialect.getMaxAliasLength() ) {
 				throw new MappingException( String.format(
 						"Unique suffix [%s] length must be less than maximum [%d]",
 						suffix, dialect.getMaxAliasLength() ) );
 			}
 			if ( alias.length() + suffix.length() > dialect.getMaxAliasLength() ) {
 				alias = alias.substring( 0, dialect.getMaxAliasLength() - suffix.length() );
 			}
 		}
 		return alias + suffix;
 	}
 	
 	/**
 	 * Generate a column alias that is unique across multiple tables
 	 */
 	public String getAlias(Dialect dialect, Table table) {
 		return getAlias(dialect) + table.getUniqueInteger() + '_';
 	}
 
 	public boolean isNullable() {
 		return nullable;
 	}
 
 	public void setNullable(boolean nullable) {
 		this.nullable=nullable;
 	}
 
 	public int getTypeIndex() {
 		return typeIndex;
 	}
 	public void setTypeIndex(int typeIndex) {
 		this.typeIndex = typeIndex;
 	}
 
 	public boolean isUnique() {
 		return unique;
 	}
 
 	//used also for generation of FK names!
 	public int hashCode() {
 		return isQuoted() ?
 			name.hashCode() :
 			name.toLowerCase().hashCode();
 	}
 
 	public boolean equals(Object object) {
 		return object instanceof Column && equals( (Column) object );
 	}
 
 	public boolean equals(Column column) {
 		if (null == column) return false;
 		if (this == column) return true;
 
 		return isQuoted() ? 
 			name.equals(column.name) :
 			name.equalsIgnoreCase(column.name);
 	}
 
     public int getSqlTypeCode(Mapping mapping) throws MappingException {
         org.hibernate.type.Type type = getValue().getType();
         try {
             int sqlTypeCode = type.sqlTypes( mapping )[getTypeIndex()];
             if ( getSqlTypeCode() != null && getSqlTypeCode() != sqlTypeCode ) {
                 throw new MappingException( "SQLType code's does not match. mapped as " + sqlTypeCode + " but is " + getSqlTypeCode() );
             }
             return sqlTypeCode;
         }
         catch ( Exception e ) {
             throw new MappingException(
                     "Could not determine type for column " +
                             name +
                             " of type " +
                             type.getClass().getName() +
                             ": " +
                             e.getClass().getName(),
                     e
             );
         }
     }
 
     /**
      * Returns the underlying columns sqltypecode.
      * If null, it is because the sqltype code is unknown.
      *
      * Use #getSqlTypeCode(Mapping) to retreive the sqltypecode used
      * for the columns associated Value/Type.
      *
      * @return sqlTypeCode if it is set, otherwise null.
      */
     public Integer getSqlTypeCode() {
         return sqlTypeCode;
     }
 
     public void setSqlTypeCode(Integer typeCode) {
         sqlTypeCode=typeCode;
     }
 
     public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
         if ( sqlType == null ) {
             sqlType = dialect.getTypeName( getSqlTypeCode( mapping ), getLength(), getPrecision(), getScale() );
         }
         return sqlType;
     }
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public void setSqlType(String sqlType) {
 		this.sqlType = sqlType;
 	}
 
 	public void setUnique(boolean unique) {
 		this.unique = unique;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getName() + ')';
 	}
 
 	public String getCheckConstraint() {
 		return checkConstraint;
 	}
 
 	public void setCheckConstraint(String checkConstraint) {
 		this.checkConstraint = checkConstraint;
 	}
 
 	public boolean hasCheckConstraint() {
 		return checkConstraint!=null;
 	}
 
 	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		return hasCustomRead()
 				? Template.renderWhereStringTemplate( customRead, dialect, functionRegistry )
 				: Template.TEMPLATE + '.' + getQuotedName( dialect );
 	}
 
 	public boolean hasCustomRead() {
 		return ( customRead != null && customRead.length() > 0 );
 	}
 
 	public String getReadExpr(Dialect dialect) {
 		return hasCustomRead() ? customRead : getQuotedName( dialect );
 	}
 	
 	public String getWriteExpr() {
 		return ( customWrite != null && customWrite.length() > 0 ) ? customWrite : "?";
 	}
 	
 	public boolean isFormula() {
 		return false;
 	}
 
 	public String getText(Dialect d) {
 		return getQuotedName(d);
 	}
 	public String getText() {
 		return getName();
 	}
 	
 	public int getPrecision() {
 		return precision;
 	}
 	public void setPrecision(int scale) {
 		this.precision = scale;
 	}
 
 	public int getScale() {
 		return scale;
 	}
 	public void setScale(int scale) {
 		this.scale = scale;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public String getDefaultValue() {
 		return defaultValue;
 	}
 
 	public void setDefaultValue(String defaultValue) {
 		this.defaultValue = defaultValue;
 	}
 
 	public String getCustomWrite() {
 		return customWrite;
 	}
 
 	public void setCustomWrite(String customWrite) {
 		this.customWrite = customWrite;
 	}
 
 	public String getCustomRead() {
 		return customRead;
 	}
 
 	public void setCustomRead(String customRead) {
 		this.customRead = customRead;
 	}
 
 	public String getCanonicalName() {
 		return quoted ? name : name.toLowerCase();
 	}
 
 	/**
 	 * Shallow copy, the value is not copied
 	 */
 	@Override
 	public Column clone() {
 		Column copy = new Column();
 		copy.setLength( length );
 		copy.setScale( scale );
 		copy.setValue( value );
 		copy.setTypeIndex( typeIndex );
 		copy.setName( getQuotedName() );
 		copy.setNullable( nullable );
 		copy.setPrecision( precision );
 		copy.setUnique( unique );
 		copy.setSqlType( sqlType );
 		copy.setSqlTypeCode( sqlTypeCode );
 		copy.uniqueInteger = uniqueInteger; //usually useless
 		copy.setCheckConstraint( checkConstraint );
 		copy.setComment( comment );
 		copy.setDefaultValue( defaultValue );
 		copy.setCustomRead( customRead );
 		copy.setCustomWrite( customWrite );
 		return copy;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/mapping/AliasTest.java b/hibernate-core/src/test/java/org/hibernate/test/mapping/AliasTest.java
index 66ffeee483..92b7db6bfd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/mapping/AliasTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/mapping/AliasTest.java
@@ -1,68 +1,119 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.mapping;
 
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
 
 import java.util.Iterator;
 
+import org.hibernate.HibernateException;
+import org.hibernate.Session;
 import org.hibernate.mapping.Table;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * Column aliases utilize {@link Table#getUniqueInteger()} for naming.  The
  * unique integer used to be statically generated by the Table class, meaning
  * it was dependent on mapping order.  HHH-2448 made the alias names
  * deterministic by having Configuration determine the unique integers on its
  * second pass over the Tables tree map.  AliasTest and
  * {@link MappingReorderedAliasTest} ensure that the unique integers are the
  * same, regardless of mapping ordering.
  * 
  * @author Brett Meyer
  */
-@TestForIssue( jiraKey = "HHH-2448" )
 public class AliasTest extends BaseCoreFunctionalTestCase {
 	
+	/**
+	 * Column aliases utilize {@link Table#getUniqueInteger()} for naming.  The unique integer used to be statically
+	 * generated by the Table class, meaning it was dependent on mapping order.  HHH-2448 made the alias names
+	 * deterministic by having Configuration determine the unique integers on its second pass over the Tables tree map.
+	 * AliasTest and {@link MappingReorderedAliasTest} ensure that the unique integers are the same, regardless of
+	 * mapping ordering.
+	 */
 	@Test
+	@TestForIssue( jiraKey = "HHH-2448" )
 	public void testAliasOrdering() {
 		Iterator<Table> tables = configuration().getTableMappings();
 		Table table1 = null;
 		Table table2 = null;
 		while ( tables.hasNext() ) {
 			Table table = tables.next();
 			if ( table.getName().equals( "Table1" ) ) {
 				table1 = table;
 			}
 			else if ( table.getName().equals( "Table2" ) ) {
 				table2 = table;
 			}
 		}
 		
 		assertTrue( table1.getUniqueInteger() < table2.getUniqueInteger() );
 	}
+	
+	@Test
+	@TestForIssue( jiraKey = "HHH-8371" )
+	public final void testUnderscoreInColumnName() throws Throwable {
+		final Session s = openSession();
+		s.getTransaction().begin();
+		
+		UserEntity user = new UserEntity();
+		user.setName( "foo" );
+		s.persist(user);
+		final ConfEntity conf =  new ConfEntity();
+		conf.setConfKey("counter");
+		conf.setConfValue("3");
+		final UserConfEntity uc = new UserConfEntity();
+		uc.setUser(user);
+		uc.setConf(conf);
+		conf.getUserConf().add(uc);
+		s.persist(conf);
+
+		s.getTransaction().commit();
+		s.clear();
+		
+		s.getTransaction().begin();
+		user = (UserEntity) s.get(UserEntity.class, user.getId());
+
+		try {
+			s.flush();
+		}
+		catch ( HibernateException e ) {
+			// original issue from HHH-8371
+			fail( "The explicit column name's underscore(s) were not considered during alias creation." );
+		}
+		
+		assertNotNull( user );
+		assertEquals( user.getName(), "foo" );
+
+		s.getTransaction().commit();
+		s.close();
+	}
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
-		return new Class<?>[] { Table1.class, Table2.class };
+		return new Class<?>[] { Table1.class, Table2.class, ConfEntity.class, UserConfEntity.class, UserEntity.class };
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/mapping/ConfEntity.java b/hibernate-core/src/test/java/org/hibernate/test/mapping/ConfEntity.java
new file mode 100644
index 0000000000..3b4a136f7c
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/mapping/ConfEntity.java
@@ -0,0 +1,54 @@
+package org.hibernate.test.mapping;
+
+import static javax.persistence.CascadeType.ALL;
+
+import java.io.Serializable;
+import java.util.HashSet;
+import java.util.Set;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.Id;
+import javax.persistence.IdClass;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "CONF")
+@IdClass(ConfId.class)
+public class ConfEntity implements Serializable{
+
+	private static final long serialVersionUID = -5089484717715507169L;
+
+	@Id
+	@Column(name = "confKey")
+	private String confKey;
+
+	@Id
+	@Column(name = "confValue")
+	private String confValue;
+
+	@OneToMany(mappedBy="conf", cascade = ALL, orphanRemoval = true, fetch = FetchType.LAZY)
+	private Set<UserConfEntity> userConf = new HashSet<UserConfEntity>();
+	
+	public String getConfKey() {
+		return confKey;
+	}
+
+	public void setConfKey(String confKey) {
+		this.confKey = confKey;
+	}
+
+	public String getConfValue() {
+		return confValue;
+	}
+
+	public void setConfValue(String confValue) {
+		this.confValue = confValue;
+	}
+
+	public Set<UserConfEntity> getUserConf() {
+		return userConf;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/mapping/ConfId.java b/hibernate-core/src/test/java/org/hibernate/test/mapping/ConfId.java
new file mode 100644
index 0000000000..72a95cd0fd
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/mapping/ConfId.java
@@ -0,0 +1,67 @@
+package org.hibernate.test.mapping;
+
+import java.io.Serializable;
+
+public class ConfId  implements Serializable{
+
+	private static final long serialVersionUID = -6722022851594514199L;
+
+	private String confKey;
+
+	private String confValue;
+	
+	public ConfId(){
+	}
+
+	public ConfId(String confKey, String confValue) {
+		this.confKey = confKey;
+		this.confValue = confValue;
+	}
+
+	public String getConfKey() {
+		return confKey;
+	}
+
+	public void setConfKey(String confKey) {
+		this.confKey = confKey;
+	}
+
+	public String getConfValue() {
+		return confValue;
+	}
+
+	public void setConfValue(String confValue) {
+		this.confValue = confValue;
+	}
+
+	@Override
+	public int hashCode() {
+		final int prime = 31;
+		int result = 1;
+		result = prime * result + ((confKey == null) ? 0 : confKey.hashCode());
+		result = prime * result + ((confValue == null) ? 0 : confValue.hashCode());
+		return result;
+	}
+
+	@Override
+	public boolean equals(Object obj) {
+		if (this == obj)
+			return true;
+		if (obj == null)
+			return false;
+		if (getClass() != obj.getClass())
+			return false;
+		ConfId other = (ConfId) obj;
+		if (confKey == null) {
+			if (other.confKey != null)
+				return false;
+		} else if (!confKey.equals(other.confKey))
+			return false;
+		else if (confValue == null) {
+			if (other.confValue != null)
+				return false;
+		} else if (!confValue.equals(other.confValue))
+			return false;
+		return true;
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/mapping/UserConfEntity.java b/hibernate-core/src/test/java/org/hibernate/test/mapping/UserConfEntity.java
new file mode 100644
index 0000000000..3e95a6bab1
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/mapping/UserConfEntity.java
@@ -0,0 +1,48 @@
+package org.hibernate.test.mapping;
+
+import java.io.Serializable;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.IdClass;
+import javax.persistence.JoinColumn;
+import javax.persistence.JoinColumns;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "USER_CONFS")
+@IdClass(UserConfId.class)
+public class UserConfEntity implements Serializable{
+	
+	private static final long serialVersionUID = 9153314908821604322L;
+
+	@Id
+	@ManyToOne
+	@JoinColumn(name="user_id")
+	private UserEntity user;
+	
+	@Id
+	@ManyToOne
+	@JoinColumns({
+			@JoinColumn(name="cnf_key", referencedColumnName="confKey"),
+			@JoinColumn(name="cnf_value", referencedColumnName="confValue")})
+	private ConfEntity conf;
+
+	public ConfEntity getConf() {
+		return conf;
+	}
+
+	public void setConf(ConfEntity conf) {
+		this.conf = conf;
+	}
+
+
+	public UserEntity getUser() {
+		return user;
+	}
+
+	public void setUser(UserEntity user) {
+		this.user = user;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/mapping/UserConfId.java b/hibernate-core/src/test/java/org/hibernate/test/mapping/UserConfId.java
new file mode 100644
index 0000000000..2965a95f06
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/mapping/UserConfId.java
@@ -0,0 +1,70 @@
+package org.hibernate.test.mapping;
+
+import java.io.Serializable;
+
+
+
+public class UserConfId  implements Serializable{
+	
+	private static final long serialVersionUID = -161134972658451944L;
+
+	private Long user;
+
+	private ConfId conf;
+	
+	public UserConfId(){
+	}
+
+	public UserConfId(Long user, ConfId conf) {
+		this.user = user;
+		this.conf = conf;
+	}
+
+	public Long getUser() {
+		return user;
+	}
+
+	public void setUser(Long user) {
+		this.user = user;
+	}
+
+
+	public ConfId getConf() {
+		return conf;
+	}
+
+	public void setConf(ConfId conf) {
+		this.conf = conf;
+	}
+
+	@Override
+	public int hashCode() {
+		final int prime = 31;
+		int result = 1;
+		result = prime * result + ((conf == null) ? 0 : conf.hashCode());
+		result = prime * result + ((user == null) ? 0 : user.hashCode());
+		return result;
+	}
+
+	@Override
+	public boolean equals(Object obj) {
+		if (this == obj)
+			return true;
+		if (obj == null)
+			return false;
+		if (getClass() != obj.getClass())
+			return false;
+		UserConfId other = (UserConfId) obj;
+		if (conf == null) {
+			if (other.conf != null)
+				return false;
+		} else if (!conf.equals(other.conf))
+			return false;
+		if (user == null) {
+			if (other.user != null)
+				return false;
+		} else if (!user.equals(other.user))
+			return false;
+		return true;
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/mapping/UserEntity.java b/hibernate-core/src/test/java/org/hibernate/test/mapping/UserEntity.java
new file mode 100644
index 0000000000..135c500744
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/mapping/UserEntity.java
@@ -0,0 +1,58 @@
+package org.hibernate.test.mapping;
+
+import static javax.persistence.CascadeType.ALL;
+import static javax.persistence.FetchType.EAGER;
+
+import java.io.Serializable;
+import java.util.HashSet;
+import java.util.Set;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
+import javax.persistence.OrderColumn;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "USER")
+public class UserEntity implements Serializable{
+
+	private static final long serialVersionUID = 1L;
+
+	@Id
+	@GeneratedValue
+	@Column(name = "user_id")
+	private Long id;
+
+	@OrderColumn(name = "cnf_order")
+	@OneToMany(mappedBy="user", fetch = EAGER, cascade = ALL, orphanRemoval = true)
+	private Set<UserConfEntity> confs =  new HashSet<UserConfEntity>();
+	
+	private String name;
+	
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public Set<UserConfEntity> getConfs() {
+		return confs;
+	}
+
+	public void setConfs(Set<UserConfEntity> confs) {
+		this.confs = confs;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
