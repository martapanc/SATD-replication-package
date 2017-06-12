/*
 * Copyright 2003-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.jmeter.protocol.http.parser;

import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * HtmlParsers can parse HTML content to obtain URLs.
 * 
 * @author <a href="mailto:jsalvata@apache.org">Jordi Salvat i Alabart</a>
 * @version $Revision$ updated on $Date$
 */
public abstract class HTMLParser {

    private static final Logger log = LoggingManager.getLoggerForClass();

	// Cache of parsers - parsers must be re-usable
	private static Hashtable parsers = new Hashtable(3);

	public final static String PARSER_CLASSNAME = "htmlParser.className"; // $NON-NLS-1$

	public final static String DEFAULT_PARSER = 
        "org.apache.jmeter.protocol.http.parser.HtmlParserHTMLParser"; // $NON-NLS-1$

    private static final String JAVA_UTIL_LINKED_HASH_SET = "java.util.LinkedHashSet"; // $NON-NLS-1$

	/**
	 * Protected constructor to prevent instantiation except from within
	 * subclasses.
	 */
	protected HTMLParser() {
	}

	public static final HTMLParser getParser() {
		return getParser(JMeterUtils.getPropDefault(PARSER_CLASSNAME, DEFAULT_PARSER));
	}

	public static final synchronized HTMLParser getParser(String htmlParserClassName) {

		// Is there a cached parser?
		HTMLParser pars = (HTMLParser) parsers.get(htmlParserClassName);
		if (pars != null) {
			log.debug("Fetched " + htmlParserClassName);
			return pars;
		}

		try {
			Object clazz = Class.forName(htmlParserClassName).newInstance();
			if (clazz instanceof HTMLParser) {
				pars = (HTMLParser) clazz;
			} else {
				throw new HTMLParseError(new ClassCastException(htmlParserClassName));
			}
		} catch (InstantiationException e) {
			throw new HTMLParseError(e);
		} catch (IllegalAccessException e) {
			throw new HTMLParseError(e);
		} catch (ClassNotFoundException e) {
			throw new HTMLParseError(e);
		}
		log.info("Created " + htmlParserClassName);
		if (pars.isReusable()) {
			parsers.put(htmlParserClassName, pars);// cache the parser
		}

		return pars;
	}

	/**
	 * Get the URLs for all the resources that a browser would automatically
	 * download following the download of the HTML content, that is: images,
	 * stylesheets, javascript files, applets, etc...
	 * <p>
	 * URLs should not appear twice in the returned iterator.
	 * <p>
	 * Malformed URLs can be reported to the caller by having the Iterator
	 * return the corresponding RL String. Overall problems parsing the html
	 * should be reported by throwing an HTMLParseException.
	 * 
	 * @param html
	 *            HTML code
	 * @param baseUrl
	 *            Base URL from which the HTML code was obtained
	 * @return an Iterator for the resource URLs
	 */
	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl) throws HTMLParseException {
		// The Set is used to ignore duplicated binary files.
		// Using a LinkedHashSet to avoid unnecessary overhead in iterating
		// the elements in the set later on. As a side-effect, this will keep
		// them roughly in order, which should be a better model of browser
		// behaviour.

		Collection col;

		// N.B. LinkedHashSet is Java 1.4
		if (hasLinkedHashSet) {
			try {
				col = (Collection) Class.forName(JAVA_UTIL_LINKED_HASH_SET).newInstance();
			} catch (Exception e) {
				throw new Error("Should not happen:" + e.toString());
			}
		} else {
			col = new java.util.HashSet(); // TODO: improve JDK1.3 solution
		}

		return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(col));

		// An additional note on using HashSets to store URLs: I just
		// discovered that obtaining the hashCode of a java.net.URL implies
		// a domain-name resolution process. This means significant delays
		// can occur, even more so if the domain name is not resolvable.
		// Whether this can be a problem in practical situations I can't tell,
		// but
		// thought I'd keep a note just in case...
		// BTW, note that using a Vector and removing duplicates via scan
		// would not help, since URL.equals requires name resolution too.
		// The above problem has now been addressed with the URLString and
		// URLCollection classes.

	}

	// See whether we can use LinkedHashSet or not:
	private static final boolean hasLinkedHashSet;
	static {
		boolean b;
		try {
			Class.forName(JAVA_UTIL_LINKED_HASH_SET);
			b = true;
		} catch (ClassNotFoundException e) {
			b = false;
		}
		hasLinkedHashSet = b;
	}

	/**
	 * Get the URLs for all the resources that a browser would automatically
	 * download following the download of the HTML content, that is: images,
	 * stylesheets, javascript files, applets, etc...
	 * <p>
	 * All URLs should be added to the Collection.
	 * <p>
	 * Malformed URLs can be reported to the caller by having the Iterator
	 * return the corresponding RL String. Overall problems parsing the html
	 * should be reported by throwing an HTMLParseException.
	 * 
	 * N.B. The Iterator returns URLs, but the Collection will contain objects
	 * of class URLString.
	 * 
	 * @param html
	 *            HTML code
	 * @param baseUrl
	 *            Base URL from which the HTML code was obtained
	 * @param coll
	 *            URLCollection
	 * @return an Iterator for the resource URLs
	 */
	public abstract Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, URLCollection coll)
			throws HTMLParseException;

	/**
	 * Get the URLs for all the resources that a browser would automatically
	 * download following the download of the HTML content, that is: images,
	 * stylesheets, javascript files, applets, etc...
	 * 
	 * N.B. The Iterator returns URLs, but the Collection will contain objects
	 * of class URLString.
	 * 
	 * @param html
	 *            HTML code
	 * @param baseUrl
	 *            Base URL from which the HTML code was obtained
	 * @param coll
	 *            Collection - will contain URLString objects, not URLs
	 * @return an Iterator for the resource URLs
	 */
	public Iterator getEmbeddedResourceURLs(byte[] html, URL baseUrl, Collection coll) throws HTMLParseException {
		return getEmbeddedResourceURLs(html, baseUrl, new URLCollection(coll));
	}

	/**
	 * Parsers should over-ride this method if the parser class is re-usable, in
	 * which case the class will be cached for the next getParser() call.
	 * 
	 * @return true if the Parser is reusable
	 */
	protected boolean isReusable() {
		return false;
	}
}