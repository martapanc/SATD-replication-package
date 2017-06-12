diff --git a/commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java b/commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
index 018d04e947..a4d17b084d 100644
--- a/commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
+++ b/commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
@@ -1,198 +1,516 @@
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
  */
 package org.hibernate.annotations.common.util;
 
+import java.util.Arrays;
 import java.util.Iterator;
-import java.util.Collection;
+import java.util.StringTokenizer;
+
 
 /**
  * Complete duplication of {@link org.hibernate.util.StringHelper}.
  *
  * @author Emmanuel Bernard
- * @deprecated Use {@link org.hibernate.util.StringHelper} instead.
  */
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 
-	private StringHelper() {
+	private StringHelper() { /* static methods only - hide constructor */
 	}
 
+	/*public static boolean containsDigits(String string) {
+		for ( int i=0; i<string.length(); i++ ) {
+			if ( Character.isDigit( string.charAt(i) ) ) return true;
+		}
+		return false;
+	}*/
+
 	public static int lastIndexOfLetter(String string) {
-		return org.hibernate.util.StringHelper.lastIndexOfLetter( string );
+		for ( int i=0; i<string.length(); i++ ) {
+			char character = string.charAt(i);
+			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
+		}
+		return string.length()-1;
 	}
 
 	public static String join(String seperator, String[] strings) {
-		return org.hibernate.util.StringHelper.join( seperator, strings );
+		int length = strings.length;
+		if ( length == 0 ) return "";
+		StringBuffer buf = new StringBuffer( length * strings[0].length() )
+				.append( strings[0] );
+		for ( int i = 1; i < length; i++ ) {
+			buf.append( seperator ).append( strings[i] );
+		}
+		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
-		return org.hibernate.util.StringHelper.join( seperator, objects );
+		StringBuffer buf = new StringBuffer();
+		if ( objects.hasNext() ) buf.append( objects.next() );
+		while ( objects.hasNext() ) {
+			buf.append( seperator ).append( objects.next() );
+		}
+		return buf.toString();
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
-		return org.hibernate.util.StringHelper.add( x, sep, y );
+		String[] result = new String[x.length];
+		for ( int i = 0; i < x.length; i++ ) {
+			result[i] = x[i] + sep + y[i];
+		}
+		return result;
 	}
 
 	public static String repeat(String string, int times) {
-		return org.hibernate.util.StringHelper.repeat( string, times );
+		StringBuffer buf = new StringBuffer( string.length() * times );
+		for ( int i = 0; i < times; i++ ) buf.append( string );
+		return buf.toString();
+	}
+
+	public static String repeat(char character, int times) {
+		char[] buffer = new char[times];
+		Arrays.fill( buffer, character );
+		return new String( buffer );
 	}
 
+
 	public static String replace(String template, String placeholder, String replacement) {
-		return org.hibernate.util.StringHelper.replace( template, placeholder, replacement );
+		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String templates[], String placeholder, String replacement) {
-		return org.hibernate.util.StringHelper.replace( templates, placeholder, replacement );
+		String[] result = new String[templates.length];
+		for ( int i =0; i<templates.length; i++ ) {
+			result[i] = replace( templates[i], placeholder, replacement );
+		}
+		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
-		return org.hibernate.util.StringHelper.replace( template, placeholder, replacement, wholeWords );
+		if ( template == null ) {
+			return template;
+		}
+		int loc = template.indexOf( placeholder );
+		if ( loc < 0 ) {
+			return template;
+		}
+		else {
+			final boolean actuallyReplace = !wholeWords ||
+					loc + placeholder.length() == template.length() ||
+					!Character.isJavaIdentifierPart( template.charAt( loc + placeholder.length() ) );
+			String actualReplacement = actuallyReplace ? replacement : placeholder;
+			return new StringBuffer( template.substring( 0, loc ) )
+					.append( actualReplacement )
+					.append( replace( template.substring( loc + placeholder.length() ),
+							placeholder,
+							replacement,
+							wholeWords ) ).toString();
+		}
 	}
 
+
 	public static String replaceOnce(String template, String placeholder, String replacement) {
-		return org.hibernate.util.StringHelper.replaceOnce( template, placeholder, replacement );
+		if ( template == null ) {
+			return template; // returnign null!
+		}
+        int loc = template.indexOf( placeholder );
+		if ( loc < 0 ) {
+			return template;
+		}
+		else {
+			return new StringBuffer( template.substring( 0, loc ) )
+					.append( replacement )
+					.append( template.substring( loc + placeholder.length() ) )
+					.toString();
+		}
 	}
 
+
 	public static String[] split(String seperators, String list) {
-		return org.hibernate.util.StringHelper.split( seperators, list );
+		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
-		return org.hibernate.util.StringHelper.split( seperators, list, include );
+		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
+		String[] result = new String[ tokens.countTokens() ];
+		int i = 0;
+		while ( tokens.hasMoreTokens() ) {
+			result[i++] = tokens.nextToken();
+		}
+		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
-		return org.hibernate.util.StringHelper.unqualify( qualifiedName );
+		int loc = qualifiedName.lastIndexOf(".");
+		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( qualifiedName.lastIndexOf(".") + 1 );
 	}
 
-	public static String qualify(String prefix, String name) {
-		return org.hibernate.util.StringHelper.qualify( prefix, name );
+	public static String qualifier(String qualifiedName) {
+		int loc = qualifiedName.lastIndexOf(".");
+		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
-	public static String[] qualify(String prefix, String[] names) {
-		return org.hibernate.util.StringHelper.qualify( prefix, names );
+	/**
+	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
+	 * Imagine you have a class named <samp>'org.hibernate.util.StringHelper'</samp>; calling collapse on that
+	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
+	 *
+	 * @param name The name to collapse.
+	 * @return The collapsed name.
+	 */
+	public static String collapse(String name) {
+		if ( name == null ) {
+			return null;
+		}
+		int breakPoint = name.lastIndexOf( '.' );
+		if ( breakPoint < 0 ) {
+			return name;
+		}
+		return collapseQualifier( name.substring( 0, breakPoint ), true ) + name.substring( breakPoint ); // includes last '.'
 	}
 
-	public static String qualifier(String qualifiedName) {
-		return org.hibernate.util.StringHelper.qualifier( qualifiedName );
+	/**
+	 * Given a qualifier, collapse it.
+	 *
+	 * @param qualifier The qualifier to collapse.
+	 * @param includeDots Should we include the dots in the collapsed form?
+	 *
+	 * @return The collapsed form.
+	 */
+	public static String collapseQualifier(String qualifier, boolean includeDots) {
+		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
+		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
+		while ( tokenizer.hasMoreTokens() ) {
+			if ( includeDots ) {
+				collapsed += '.';
+			}
+			collapsed += tokenizer.nextToken().charAt( 0 );
+		}
+		return collapsed;
+	}
+
+	/**
+	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
+	 * 'org.hibernate.util.StringHelper' would become 'util.StringHelper'.
+	 *
+	 * @param name The (potentially) qualified name.
+	 * @param qualifierBase The qualifier base.
+	 *
+	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
+	 */
+	public static String partiallyUnqualify(String name, String qualifierBase) {
+		if ( name == null || ! name.startsWith( qualifierBase ) ) {
+			return name;
+		}
+		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
+	}
+
+	/**
+	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
+	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
+	 * 'org.hibernate.util.StringHelper' would become 'o.h.util.StringHelper'.
+	 *
+	 * @param name The (potentially) qualified name.
+	 * @param qualifierBase The qualifier base.
+	 *
+	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
+	 */
+	public static String collapseQualifierBase(String name, String qualifierBase) {
+		if ( name == null || ! name.startsWith( qualifierBase ) ) {
+			return collapse( name );
+		}
+		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
-		return org.hibernate.util.StringHelper.suffix( columns, suffix );
+		if ( suffix == null ) return columns;
+		String[] qualified = new String[columns.length];
+		for ( int i = 0; i < columns.length; i++ ) {
+			qualified[i] = suffix( columns[i], suffix );
+		}
+		return qualified;
+	}
+
+	private static String suffix(String name, String suffix) {
+		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
-		return org.hibernate.util.StringHelper.root( qualifiedName );
+		int loc = qualifiedName.indexOf( "." );
+		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
-		return org.hibernate.util.StringHelper.unroot( qualifiedName );
+		int loc = qualifiedName.indexOf( "." );
+		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
-		return org.hibernate.util.StringHelper.booleanValue( tfString );
+		String trimmed = tfString.trim().toLowerCase();
+		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
-		return org.hibernate.util.StringHelper.toString( array );
+		int len = array.length;
+		if ( len == 0 ) return "";
+		StringBuffer buf = new StringBuffer( len * 12 );
+		for ( int i = 0; i < len - 1; i++ ) {
+			buf.append( array[i] ).append(", ");
+		}
+		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
-		return org.hibernate.util.StringHelper.multiply( string, placeholders, replacements );
+		String[] result = new String[]{string};
+		while ( placeholders.hasNext() ) {
+			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
+		}
+		return result;
 	}
 
-	public static int countUnquoted(String string, char character) {
-		return org.hibernate.util.StringHelper.countUnquoted( string, character );
+	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
+		String[] results = new String[replacements.length * strings.length];
+		int n = 0;
+		for ( int i = 0; i < replacements.length; i++ ) {
+			for ( int j = 0; j < strings.length; j++ ) {
+				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
+			}
+		}
+		return results;
 	}
 
-	public static int[] locateUnquoted(String string, char character) {
-		return org.hibernate.util.StringHelper.locateUnquoted( string, character );
+	public static int countUnquoted(String string, char character) {
+		if ( '\'' == character ) {
+			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
+		}
+		if (string == null)
+			return 0;
+		// Impl note: takes advantage of the fact that an escpaed single quote
+		// embedded within a quote-block can really be handled as two seperate
+		// quote-blocks for the purposes of this method...
+		int count = 0;
+		int stringLength = string.length();
+		boolean inQuote = false;
+		for ( int indx = 0; indx < stringLength; indx++ ) {
+			char c = string.charAt( indx );
+			if ( inQuote ) {
+				if ( '\'' == c ) {
+					inQuote = false;
+				}
+			}
+			else if ( '\'' == c ) {
+				inQuote = true;
+			}
+			else if ( c == character ) {
+				count++;
+			}
+		}
+		return count;
 	}
 
 	public static boolean isNotEmpty(String string) {
-		return org.hibernate.util.StringHelper.isNotEmpty( string );
+		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
-		return org.hibernate.util.StringHelper.isEmpty( string );
+		return string == null || string.length() == 0;
+	}
+
+	public static String qualify(String prefix, String name) {
+		if ( name == null || prefix == null ) {
+			throw new NullPointerException();
+		}
+		return new StringBuffer( prefix.length() + name.length() + 1 )
+				.append(prefix)
+				.append('.')
+				.append(name)
+				.toString();
+	}
+
+	public static String[] qualify(String prefix, String[] names) {
+		if ( prefix == null ) return names;
+		int len = names.length;
+		String[] qualified = new String[len];
+		for ( int i = 0; i < len; i++ ) {
+			qualified[i] = qualify( prefix, names[i] );
+		}
+		return qualified;
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
-		return org.hibernate.util.StringHelper.firstIndexOfChar( sqlString, string, startindex );
+		int matchAt = -1;
+		for ( int i = 0; i < string.length(); i++ ) {
+			int curMatch = sqlString.indexOf( string.charAt( i ), startindex );
+			if ( curMatch >= 0 ) {
+				if ( matchAt == -1 ) { // first time we find match!
+					matchAt = curMatch;
+				}
+				else {
+					matchAt = Math.min( matchAt, curMatch );
+				}
+			}
+		}
+		return matchAt;
 	}
 
 	public static String truncate(String string, int length) {
-		return org.hibernate.util.StringHelper.truncate( string, length );
+		if ( string.length() <= length ) {
+			return string;
+		}
+		else {
+			return string.substring( 0, length );
+		}
 	}
 
 	public static String generateAlias(String description) {
-		return org.hibernate.util.StringHelper.generateAlias( description );
+		return generateAliasRoot(description) + '_';
 	}
 
+	/**
+	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
+	 * Loader do <em>not</em> have to use aliases of this form.
+	 *
+	 * @param description The base name (usually an entity-name or collection-role)
+	 * @param unique A uniquing value
+	 *
+	 * @return an alias of the form <samp>foo1_</samp>
+	 */
 	public static String generateAlias(String description, int unique) {
-		return org.hibernate.util.StringHelper.generateAlias( description, unique );
+		return generateAliasRoot(description) +
+			Integer.toString(unique) +
+			'_';
+	}
+
+	/**
+	 * Generates a root alias by truncating the "root name" defined by
+	 * the incoming decription and removing/modifying any non-valid
+	 * alias characters.
+	 *
+	 * @param description The root name from which to generate a root alias.
+	 * @return The generated root alias.
+	 */
+	private static String generateAliasRoot(String description) {
+		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
+				.toLowerCase()
+		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
+				.replace( '$', '_' ); //classname may be an inner class
+		result = cleanAlias( result );
+		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
+			return result + "x"; //ick!
+		}
+		else {
+			return result;
+		}
+	}
+
+	/**
+	 * Clean the generated alias by removing any non-alpha characters from the
+	 * beginning.
+	 *
+	 * @param alias The generated alias to be cleaned.
+	 * @return The cleaned alias, stripped of any leading non-alpha characters.
+	 */
+	private static String cleanAlias(String alias) {
+		char[] chars = alias.toCharArray();
+		// short cut check...
+		if ( !Character.isLetter( chars[0] ) ) {
+			for ( int i = 1; i < chars.length; i++ ) {
+				// as soon as we encounter our first letter, return the substring
+				// from that position
+				if ( Character.isLetter( chars[i] ) ) {
+					return alias.substring( i );
+				}
+			}
+		}
+		return alias;
 	}
 
 	public static String unqualifyEntityName(String entityName) {
-		return org.hibernate.util.StringHelper.unqualifyEntityName( entityName );
+		String result = unqualify(entityName);
+		int slashPos = result.indexOf( '/' );
+		if ( slashPos > 0 ) {
+			result = result.substring( 0, slashPos - 1 );
+		}
+		return result;
 	}
 
 	public static String toUpperCase(String str) {
-		return org.hibernate.util.StringHelper.toUpperCase( str );
+		return str==null ? null : str.toUpperCase();
 	}
 
 	public static String toLowerCase(String str) {
-		return org.hibernate.util.StringHelper.toLowerCase( str );
+		return str==null ? null : str.toLowerCase();
 	}
 
 	public static String moveAndToBeginning(String filter) {
-		return org.hibernate.util.StringHelper.moveAndToBeginning( filter );
+		if ( filter.trim().length()>0 ){
+			filter += " and ";
+			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
+		}
+		return filter;
 	}
 
 	/**
-	 * Not a direct copy from {@link org.hibernate.util.StringHelper}, this is instead directly copied
-	 * from {@link org.hibernate.util.ArrayHelper}.
+	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
-	 * @param coll the collection
-	 * @return The int array
-	 * @deprecated Use {@link org.hibernate.util.ArrayHelper#toIntArray} instead.
+	 * @param name The name to check.
+	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
-	public static int[] toIntArray(Collection coll) {
-		return org.hibernate.util.ArrayHelper.toIntArray( coll );
-	}
-
 	public static boolean isQuoted(String name) {
-		return org.hibernate.util.StringHelper.isQuoted( name );
+		return name != null && name.length() != 0 && name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`';
 	}
 
+	/**
+	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
+	 * return name.
+	 *
+	 * @param name The name to quote.
+	 * @return The quoted version.
+	 */
 	public static String quote(String name) {
-		return org.hibernate.util.StringHelper.quote( name );
+		if ( name == null || name.length() == 0 || isQuoted( name ) ) {
+			return name;
+		}
+		else {
+			return new StringBuffer( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
+		}
 	}
 
+	/**
+	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
+	 *
+	 * @param name The name to be unquoted.
+	 * @return The unquoted version.
+	 */
 	public static String unquote(String name) {
-		return org.hibernate.util.StringHelper.unquote( name );
+		if ( isQuoted( name ) ) {
+			return name.substring( 1, name.length() - 1 );
+		}
+		else {
+			return name;
+		}
 	}
 }
\ No newline at end of file
