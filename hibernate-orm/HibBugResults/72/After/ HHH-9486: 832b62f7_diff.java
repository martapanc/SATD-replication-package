diff --git a/hibernate-core/src/main/java/org/hibernate/LockOptions.java b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
index 051b792cca..3024ec24e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/LockOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
@@ -1,309 +1,331 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate;
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 /**
  * Contains locking details (LockMode, Timeout and Scope).
  * 
  * @author Scott Marlow
  */
 public class LockOptions implements Serializable {
 	/**
 	 * Represents LockMode.NONE (timeout + scope do not apply).
 	 */
 	public static final LockOptions NONE = new LockOptions(LockMode.NONE);
 
 	/**
 	 * Represents LockMode.READ (timeout + scope do not apply).
 	 */
 	public static final LockOptions READ = new LockOptions(LockMode.READ);
 
 	/**
 	 * Represents LockMode.UPGRADE (will wait forever for lock and scope of false meaning only entity is locked).
 	 */
 	@SuppressWarnings("deprecation")
 	public static final LockOptions UPGRADE = new LockOptions(LockMode.UPGRADE);
 
 	/**
 	 * Indicates that the database should not wait at all to acquire the pessimistic lock.
 	 * @see #getTimeOut
 	 */
 	public static final int NO_WAIT = 0;
 
 	/**
 	 * Indicates that there is no timeout for the acquisition.
 	 * @see #getTimeOut
 	 */
 	public static final int WAIT_FOREVER = -1;
 
 	/**
 	 * Indicates that rows that are already locked should be skipped.
 	 * @see #getTimeOut()
 	 */
 	public static final int SKIP_LOCKED = -2;
 
 	private LockMode lockMode = LockMode.NONE;
 	private int timeout = WAIT_FOREVER;
 
 	private Map<String,LockMode> aliasSpecificLockModes;
 
+	private Boolean followOnLocking;
+
 	/**
 	 * Constructs a LockOptions with all default options.
 	 */
 	public LockOptions() {
 	}
 
 	/**
 	 * Constructs a LockOptions with the given lock mode.
 	 *
 	 * @param lockMode The lock mode to use
 	 */
 	public LockOptions( LockMode lockMode) {
 		this.lockMode = lockMode;
 	}
 
 
 	/**
 	 * Retrieve the overall lock mode in effect for this set of options.
 	 * <p/>
 	 * In certain contexts (hql and criteria), lock-modes can be defined in an
 	 * even more granular {@link #setAliasSpecificLockMode(String, LockMode) per-alias} fashion
 	 *
 	 * @return The overall lock mode.
 	 */
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	/**
 	 * Set the overall {@link LockMode} to be used.  The default is
 	 * {@link LockMode#NONE}
 	 *
 	 * @param lockMode The new overall lock mode to use.
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setLockMode(LockMode lockMode) {
 		this.lockMode = lockMode;
 		return this;
 	}
 
 	/**
 	 * Specify the {@link LockMode} to be used for a specific query alias.
 	 *
 	 * @param alias used to reference the LockMode.
 	 * @param lockMode The lock mode to apply to the given alias
 	 * @return this LockRequest instance for operation chaining.
 	 *
 	 * @see Query#setLockMode(String, LockMode)
 	 * @see Criteria#setLockMode(LockMode)
 	 * @see Criteria#setLockMode(String, LockMode)
 	 */
 	public LockOptions setAliasSpecificLockMode(String alias, LockMode lockMode) {
 		if ( aliasSpecificLockModes == null ) {
 			aliasSpecificLockModes = new HashMap<String,LockMode>();
 		}
 		aliasSpecificLockModes.put( alias, lockMode );
 		return this;
 	}
 
 	/**
 	 * Get the {@link LockMode} explicitly specified for the given alias via
 	 * {@link #setAliasSpecificLockMode}
 	 * <p/>
 	 * Differs from {@link #getEffectiveLockMode} in that here we only return
 	 * explicitly specified alias-specific lock modes.
 	 *
 	 * @param alias The alias for which to locate the explicit lock mode.
 	 *
 	 * @return The explicit lock mode for that alias.
 	 */
 	public LockMode getAliasSpecificLockMode(String alias) {
 		if ( aliasSpecificLockModes == null ) {
 			return null;
 		}
 		return aliasSpecificLockModes.get( alias );
 	}
 
 	/**
 	 * Determine the {@link LockMode} to apply to the given alias.  If no
 	 * mode was explicitly {@link #setAliasSpecificLockMode set}, the
 	 * {@link #getLockMode overall mode} is returned.  If the overall lock mode is
 	 * <tt>null</tt> as well, {@link LockMode#NONE} is returned.
 	 * <p/>
 	 * Differs from {@link #getAliasSpecificLockMode} in that here we fallback to we only return
 	 * the overall lock mode.
 	 *
 	 * @param alias The alias for which to locate the effective lock mode.
 	 *
 	 * @return The effective lock mode.
 	 */
 	public LockMode getEffectiveLockMode(String alias) {
 		LockMode lockMode = getAliasSpecificLockMode( alias );
 		if ( lockMode == null ) {
 			lockMode = this.lockMode;
 		}
 		return lockMode == null ? LockMode.NONE : lockMode;
 	}
 
 	/**
 	 * Does this LockOptions object define alias-specific lock modes?
 	 *
 	 * @return {@code true} if this LockOptions object define alias-specific lock modes; {@code false} otherwise.
 	 */
 	public boolean hasAliasSpecificLockModes() {
 		return aliasSpecificLockModes != null
 				&& ! aliasSpecificLockModes.isEmpty();
 	}
 
 	/**
 	 * Get the number of aliases that have specific lock modes defined.
 	 *
 	 * @return the number of explicitly defined alias lock modes.
 	 */
 	public int getAliasLockCount() {
 		if ( aliasSpecificLockModes == null ) {
 			return 0;
 		}
 		return aliasSpecificLockModes.size();
 	}
 
 	/**
 	 * Iterator for accessing Alias (key) and LockMode (value) as Map.Entry.
 	 *
 	 * @return Iterator for accessing the Map.Entry's
 	 */
 	public Iterator<Map.Entry<String,LockMode>> getAliasLockIterator() {
 		return getAliasSpecificLocks().iterator();
 	}
 
 	/**
 	 * Iterable access to alias (key) and LockMode (value) as Map.Entry.
 	 *
 	 * @return Iterable for accessing the Map.Entry's
 	 */
 	public Iterable<Map.Entry<String,LockMode>> getAliasSpecificLocks() {
 		if ( aliasSpecificLockModes == null ) {
 			return Collections.emptyList();
 		}
 		return aliasSpecificLockModes.entrySet();
 	}
 
 	/**
 	 * Currently needed for follow-on locking.
 	 *
 	 * @return The greatest of all requested lock modes.
 	 */
 	public LockMode findGreatestLockMode() {
 		LockMode lockModeToUse = getLockMode();
 		if ( lockModeToUse == null ) {
 			lockModeToUse = LockMode.NONE;
 		}
 
 		if ( aliasSpecificLockModes == null ) {
 			return lockModeToUse;
 		}
 
 		for ( LockMode lockMode : aliasSpecificLockModes.values() ) {
 			if ( lockMode.greaterThan( lockModeToUse ) ) {
 				lockModeToUse = lockMode;
 			}
 		}
 
 		return lockModeToUse;
 	}
 
 	/**
 	 * Retrieve the current timeout setting.
 	 * <p/>
 	 * The timeout is the amount of time, in milliseconds, we should instruct the database
 	 * to wait for any requested pessimistic lock acquisition.
 	 * <p/>
 	 * {@link #NO_WAIT}, {@link #WAIT_FOREVER} or {@link #SKIP_LOCKED} represent 3 "magic" values.
 	 *
 	 * @return timeout in milliseconds, {@link #NO_WAIT}, {@link #WAIT_FOREVER} or {@link #SKIP_LOCKED}
 	 */
 	public int getTimeOut() {
 		return timeout;
 	}
 
 	/**
 	 * Set the timeout setting.
 	 * <p/>
 	 * See {@link #getTimeOut} for a discussion of meaning.
 	 *
 	 * @param timeout The new timeout setting.
 	 *
 	 * @return this (for method chaining).
 	 *
 	 * @see #getTimeOut
 	 */
 	public LockOptions setTimeOut(int timeout) {
 		this.timeout = timeout;
 		return this;
 	}
 
 	private boolean scope;
 
 	/**
 	 * Retrieve the current lock scope setting.
 	 * <p/>
 	 * "scope" is a JPA defined term.  It is basically a cascading of the lock to associations.
 	 *
 	 * @return true if locking will be extended to owned associations
 	 */
 	public boolean getScope() {
 		return scope;
 	}
 
 	/**
 	 * Set the scope.
 	 *
 	 * @param scope The new scope setting
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setScope(boolean scope) {
 		this.scope = scope;
 		return this;
 	}
 
 	/**
+	 * Retrieve the current follow-on-locking setting.
+	 *
+	 * @return true if follow-on-locking is enabled
+	 */
+	public Boolean getFollowOnLocking() {
+		return followOnLocking;
+	}
+
+	/**
+	 * Set the the follow-on-locking setting.
+	 * @param followOnLocking The new follow-on-locking setting
+	 * @return this (for method chaining).
+	 */
+	public LockOptions setFollowOnLocking(Boolean followOnLocking) {
+		this.followOnLocking = followOnLocking;
+		return this;
+	}
+
+	/**
 	 * Make a copy.
 	 *
 	 * @return The copy
 	 */
 	public LockOptions makeCopy() {
 		final LockOptions copy = new LockOptions();
 		copy( this, copy );
 		return copy;
 	}
 
 	/**
 	 * Perform a shallow copy.
 	 *
 	 * @param source Source for the copy (copied from)
 	 * @param destination Destination for the copy (copied to)
 	 *
 	 * @return destination
 	 */
 	public static LockOptions copy(LockOptions source, LockOptions destination) {
 		destination.setLockMode( source.getLockMode() );
 		destination.setScope( source.getScope() );
 		destination.setTimeOut( source.getTimeOut() );
 		if ( source.aliasSpecificLockModes != null ) {
 			destination.aliasSpecificLockModes = new HashMap<String,LockMode>( source.aliasSpecificLockModes );
 		}
+		destination.setFollowOnLocking( source.getFollowOnLocking() );
 		return destination;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java b/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
index a92d218793..67f591969b 100644
--- a/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
+++ b/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
@@ -1,120 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.annotations;
 
+import org.hibernate.engine.spi.QueryParameters;
+
 /**
  * Consolidation of hints available to Hibernate JPA queries.  Mainly used to define features available on
  * Hibernate queries that have no corollary in JPA queries.
  */
 public class QueryHints {
 	/**
 	 * Disallow instantiation.
 	 */
 	private QueryHints() {
 	}
 
 	/**
 	 * The cache mode to use.
 	 *
 	 * @see org.hibernate.Query#setCacheMode
 	 * @see org.hibernate.SQLQuery#setCacheMode
 	 */
 	public static final String CACHE_MODE = "org.hibernate.cacheMode";
 
 	/**
 	 * The cache region to use.
 	 *
 	 * @see org.hibernate.Query#setCacheRegion
 	 * @see org.hibernate.SQLQuery#setCacheRegion
 	 */
 	public static final String CACHE_REGION = "org.hibernate.cacheRegion";
 
 	/**
 	 * Are the query results cacheable?
 	 *
 	 * @see org.hibernate.Query#setCacheable
 	 * @see org.hibernate.SQLQuery#setCacheable
 	 */
 	public static final String CACHEABLE = "org.hibernate.cacheable";
 
 	/**
 	 * Is the query callable?  Note: only valid for named native sql queries.
 	 */
 	public static final String CALLABLE = "org.hibernate.callable";
 
 	/**
 	 * Defines a comment to be applied to the SQL sent to the database.
 	 *
 	 * @see org.hibernate.Query#setComment
 	 * @see org.hibernate.SQLQuery#setComment
 	 */
 	public static final String COMMENT = "org.hibernate.comment";
 
 	/**
 	 * Defines the JDBC fetch size to use.
 	 *
 	 * @see org.hibernate.Query#setFetchSize
 	 * @see org.hibernate.SQLQuery#setFetchSize
 	 */
 	public static final String FETCH_SIZE = "org.hibernate.fetchSize";
 
 	/**
 	 * The flush mode to associate with the execution of the query.
 	 *
 	 * @see org.hibernate.Query#setFlushMode
 	 * @see org.hibernate.SQLQuery#setFlushMode
 	 * @see org.hibernate.Session#setFlushMode
 	 */
 	public static final String FLUSH_MODE = "org.hibernate.flushMode";
 
 	/**
 	 * Should entities returned from the query be set in read only mode?
 	 *
 	 * @see org.hibernate.Query#setReadOnly
 	 * @see org.hibernate.SQLQuery#setReadOnly
 	 * @see org.hibernate.Session#setReadOnly
 	 */
 	public static final String READ_ONLY = "org.hibernate.readOnly";
 
 	/**
 	 * Apply a Hibernate query timeout, which is defined in <b>seconds</b>.
 	 *
 	 * @see org.hibernate.Query#setTimeout
 	 * @see org.hibernate.SQLQuery#setTimeout
 	 */
 	public static final String TIMEOUT_HIBERNATE = "org.hibernate.timeout";
 
 	/**
 	 * Apply a JPA query timeout, which is defined in <b>milliseconds</b>.
 	 */
 	public static final String TIMEOUT_JPA = "javax.persistence.query.timeout";
 
 	/**
 	 * Available to apply lock mode to a native SQL query since JPA requires that
 	 * {@link javax.persistence.Query#setLockMode} throw an IllegalStateException if called for a native query.
 	 * <p/>
 	 * Accepts a {@link javax.persistence.LockModeType} or a {@link org.hibernate.LockMode}
 	 */
 	public static final String NATIVE_LOCKMODE = "org.hibernate.lockMode";
 	
 	/**
 	 * Hint providing a "fetchgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).
 	 * 
 	 * Note: Currently, attributes that are not specified are treated as FetchType.LAZY or FetchType.EAGER depending
 	 * on the attribute's definition in metadata, rather than forcing FetchType.LAZY.
 	 */
 	public static final String FETCHGRAPH = "javax.persistence.fetchgraph";
 	
 	/**
 	 * Hint providing a "loadgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).  Attributes that are not specified are treated as
 	 * FetchType.LAZY or FetchType.EAGER depending on the attribute's definition in metadata
 	 */
 	public static final String LOADGRAPH = "javax.persistence.loadgraph";
 
+	/**
+	 * Hint to enable/disable the follow-on-locking mechanism provided by {@link org.hibernate.dialect.Dialect#useFollowOnLocking(QueryParameters)}.
+	 * A value of {@code true} enables follow-on-locking, whereas a value of {@code false} disables it.
+	 * If the value is {@code null}, the the {@code Dialect} strategy is going to be used instead.
+	 *
+	 * @since 5.2
+	 */
+	public static final String FOLLOW_ON_LOCKING = "hibernate.query.followOnLocking";
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 7ce80a5c87..c04afb47a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -476,1019 +476,1019 @@ public interface AvailableSettings {
 	 */
 	String IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS = "hibernate.discriminator.implicit_for_joined";
 
 	/**
 	 * The legacy behavior of Hibernate is to not use discriminators for joined inheritance (Hibernate does not need
 	 * the discriminator...).  However, some JPA providers do need the discriminator for handling joined inheritance.
 	 * In the interest of portability this capability has been added to Hibernate too.
 	 * <p/>
 	 * Existing applications rely (implicitly or explicitly) on Hibernate ignoring any DiscriminatorColumn declarations
 	 * on joined inheritance hierarchies.  This setting allows these applications to maintain the legacy behavior
 	 * of DiscriminatorColumn annotations being ignored when paired with joined inheritance.
 	 * <p/>
 	 * See Hibernate Jira issue HHH-6911 for additional background info.
 	 *
 	 * @see MetadataBuilder#enableExplicitDiscriminatorsForJoinedSubclassSupport
 	 * @see #IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	String IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS = "hibernate.discriminator.ignore_explicit_for_joined";
 
 	/**
 	 * Enable nationalized character support on all string / clob based attribute ( string, char, clob, text etc ).
 	 *
 	 * Default is {@code false}.
 	 *
 	 * @see MetadataBuilder#enableGlobalNationalizedCharacterDataSupport(boolean)
 	 */
 	String USE_NATIONALIZED_CHARACTER_DATA = "hibernate.use_nationalized_character_data";
 	/**
 	 * The deprecated name.  Use {@link #SCANNER} or {@link #SCANNER_ARCHIVE_INTERPRETER} instead.
 	 */
 	String SCANNER_DEPRECATED = "hibernate.ejb.resource_scanner";
 
 	/**
 	 * Pass an implementation of {@link org.hibernate.boot.archive.scan.spi.Scanner}.
 	 * Accepts either:<ul>
 	 *     <li>an actual instance</li>
 	 *     <li>a reference to a Class that implements Scanner</li>
 	 *     <li>a fully qualified name (String) of a Class that implements Scanner</li>
 	 * </ul>
 	 *
 	 * @see org.hibernate.boot.MetadataBuilder#applyScanner
 	 */
 	String SCANNER = "hibernate.archive.scanner";
 
 	/**
 	 * Pass {@link org.hibernate.boot.archive.spi.ArchiveDescriptorFactory} to use
 	 * in the scanning process.  Accepts either:<ul>
 	 *     <li>an ArchiveDescriptorFactory instance</li>
 	 *     <li>a reference to a Class that implements ArchiveDescriptorFactory</li>
 	 *     <li>a fully qualified name (String) of a Class that implements ArchiveDescriptorFactory</li>
 	 * </ul>
 	 * <p/>
 	 * See information on {@link org.hibernate.boot.archive.scan.spi.Scanner}
 	 * about expected constructor forms.
 	 *
 	 * @see #SCANNER
 	 * @see org.hibernate.boot.archive.scan.spi.Scanner
 	 * @see org.hibernate.boot.archive.scan.spi.AbstractScannerImpl
 	 * @see MetadataBuilder#applyArchiveDescriptorFactory
 	 */
 	String SCANNER_ARCHIVE_INTERPRETER = "hibernate.archive.interpreter";
 
 	/**
 	 * Identifies a comma-separate list of values indicating the types of
 	 * things we should auto-detect during scanning.  Allowable values include:<ul>
 	 *     <li>"class" - discover classes - .class files are discovered as managed classes</li>
 	 *     <li>"hbm" - discover hbm mapping files - hbm.xml files are discovered as mapping files</li>
 	 * </ul>
 	 *
 	 * @see org.hibernate.boot.MetadataBuilder#applyScanOptions
 	 */
 	String SCANNER_DISCOVERY = "hibernate.archive.autodetection";
 
 	/**
 	 * Used to specify the {@link org.hibernate.boot.model.naming.ImplicitNamingStrategy} class to use.  The following
 	 * short-names are defined for this setting:<ul>
 	 *     <li>"default" -> {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl}</li>
 	 *     <li>"jpa" -> {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl}</li>
 	 *     <li>"legacy-jpa" -> {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl}</li>
 	 *     <li>"legacy-hbm" -> {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl}</li>
 	 *     <li>"component-path" -> {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl}</li>
 	 * </ul>
 	 *
 	 * The default is defined by the ImplicitNamingStrategy registered under the "default" key.  If that happens to
 	 * be empty, the fallback is to use {@link org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl}.
 	 *
 	 * @see MetadataBuilder#applyImplicitNamingStrategy
 	 *
 	 * @since 5.0
 	 */
 	String IMPLICIT_NAMING_STRATEGY = "hibernate.implicit_naming_strategy";
 
 	/**
 	 * Used to specify the {@link org.hibernate.boot.model.naming.PhysicalNamingStrategy} class to use.
 	 *
 	 * @see MetadataBuilder#applyPhysicalNamingStrategy
 	 *
 	 * @since 5.0
 	 */
 	String PHYSICAL_NAMING_STRATEGY = "hibernate.physical_naming_strategy";
 
 	/**
 	 * Used to specify the order in which metadata sources should be processed.  Value
 	 * is a delimited-list whose elements are defined by {@link org.hibernate.cfg.MetadataSourceType}.
 	 * <p/>
 	 * Default is {@code "hbm,class"} which indicates to process {@code hbm.xml} files followed by
 	 * annotations (combined with {@code orm.xml} mappings).
 	 *
 	 * @see MetadataBuilder#applySourceProcessOrdering(org.hibernate.cfg.MetadataSourceType...)
 	 */
 	String ARTIFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
 
 	/**
 	 * Specifies whether to automatically quote any names that are deemed keywords.  Auto-quoting
 	 * is disabled by default. Set to true to enable it.
 	 *
 	 * @since 5.0
 	 */
 	String KEYWORD_AUTO_QUOTING_ENABLED = "hibernate.auto_quote_keyword";
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// SessionFactoryBuilder level settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Setting used to name the Hibernate {@link org.hibernate.SessionFactory}.
 	 *
 	 * Naming the SessionFactory allows for it to be properly serialized across JVMs as
 	 * long as the same name is used on each JVM.
 	 *
 	 * If {@link #SESSION_FACTORY_NAME_IS_JNDI} is set to {@code true}, this is also the
 	 * name under which the SessionFactory is bound into JNDI on startup and from which
 	 * it can be obtained from JNDI.
 	 *
 	 * @see #SESSION_FACTORY_NAME_IS_JNDI
 	 * @see org.hibernate.internal.SessionFactoryRegistry
 	 */
 	String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Does the value defined by {@link #SESSION_FACTORY_NAME} represent a JNDI namespace into which
 	 * the {@link org.hibernate.SessionFactory} should be bound and made accessible?
 	 *
 	 * Defaults to {@code true} for backwards compatibility.
 	 *
 	 * Set this to {@code false} if naming a SessionFactory is needed for serialization purposes, but
 	 * no writable JNDI context exists in the runtime environment or if the user simply does not want
 	 * JNDI to be used.
 	 *
 	 * @see #SESSION_FACTORY_NAME
 	 */
 	String SESSION_FACTORY_NAME_IS_JNDI = "hibernate.session_factory_name_is_jndi";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	String SHOW_SQL ="hibernate.show_sql";
 
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	String FORMAT_SQL ="hibernate.format_sql";
 
 	/**
 	 * Add comments to the generated SQL
 	 */
 	String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 
 	/**
 	 * The default batch size for batch fetching
 	 */
 	String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 
 	/**
 	 * Specifies how Hibernate should acquire JDBC connections.  Should generally only configure
 	 * this or {@link #RELEASE_CONNECTIONS}, not both
 	 *
 	 * @see org.hibernate.ConnectionAcquisitionMode
 	 *
 	 * @since 5.1
 	 *
 	 * @deprecated (since 5.2) use {@link #CONNECTION_HANDLING} instead
 	 */
 	@Deprecated
 	String ACQUIRE_CONNECTIONS = "hibernate.connection.acquisition_mode";
 
 	/**
 	 * Specifies how Hibernate should release JDBC connections.  Should generally only configure
 	 * this or {@link #ACQUIRE_CONNECTIONS}, not both
 	 *
 	 * @see org.hibernate.ConnectionReleaseMode
 	 *
 	 * @deprecated (since 5.2) use {@link #CONNECTION_HANDLING} instead
 	 */
 	@Deprecated
 	String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 
 	/**
 	 * Specifies how Hibernate should manage JDBC connections in terms of acquiring and releasing.
 	 * Supersedes {@link #ACQUIRE_CONNECTIONS} and {@link #RELEASE_CONNECTIONS}
 	 *
 	 * @see org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode
 	 *
 	 * @since 5.2
 	 */
 	String CONNECTION_HANDLING = "hibernate.connection.handling_mode";
 
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 
 	String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * The {@link org.hibernate.exception.spi.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * Default precedence of null values in {@code ORDER BY} clause.  Supported options: {@code none} (default),
 	 * {@code first}, {@code last}.
 	 */
 	String DEFAULT_NULL_ORDERING = "hibernate.order_by.default_null_ordering";
 
 	/**
 	 * Enable fetching JDBC statement warning for logging.
 	 *
 	 * Values are {@code true}  or {@code false} .
 	 * Default value is {@link org.hibernate.dialect.Dialect#isJdbcLogWarningsEnabledByDefault()}
 	 *
 	 * @since 5.1
 	 */
 	String LOG_JDBC_WARNINGS =  "hibernate.jdbc.log.warnings";
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// c3p0 connection pooling specific settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * A setting prefix used to indicate settings that target the hibernate-c3p0 integration
 	 */
 	String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 
 	/**
 	 * Idle time beforeQuery a C3P0 pooled connection is validated
 	 */
 	String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// proxool connection pooling specific settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * A setting prefix used to indicate settings that target the hibernate-proxool integration
 	 */
 	String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
 	 */
 	@Deprecated
 	String PROXOOL_PREFIX = PROXOOL_CONFIG_PREFIX;
 
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	String PROXOOL_XML = "hibernate.proxool.xml";
 
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Second-level cache settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The {@link org.hibernate.cache.spi.RegionFactory} implementation.  Can refer to:<ul>
 	 *     <li>an Object implementing {@link org.hibernate.cache.spi.RegionFactory}</li>
 	 *     <li>a Class implementing {@link org.hibernate.cache.spi.RegionFactory}</li>
 	 *     <li>FQN of a Class implementing {@link org.hibernate.cache.spi.RegionFactory}</li>
 	 * </ul>
 	 */
 	String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * Allow control to specify the {@link org.hibernate.cache.spi.CacheKeysFactory} impl to use.
 	 * Can refer to:<ul>
 	 *     <li>an Object implementing {@link org.hibernate.cache.spi.CacheKeysFactory}</li>
 	 *     <li>a Class implementing {@link org.hibernate.cache.spi.CacheKeysFactory}</li>
 	 *     <li>FQN of a Class implementing {@link org.hibernate.cache.spi.CacheKeysFactory}</li>
 	 * </ul>
 	 *
 	 * @since 5.2 - note that currently this is only honored for hibernate-infinispan
 	 */
 	String CACHE_KEYS_FACTORY = "hibernate.cache.keys_factory";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 
 	/**
 	 * The {@link org.hibernate.cache.spi.QueryCacheFactory} implementation class.
 	 */
 	String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enables the automatic eviction of a bi-directional association's collection cache when an element in the
 	 * ManyToOne collection is added/updated/removed without properly managing the change on the OneToMany side.
 	 */
 	String AUTO_EVICT_COLLECTION_CACHE = "hibernate.cache.auto_evict_collection_cache";
 
 	/**
 	 * Enable direct storage of entity references into the second level cache when applicable (immutable data, etc).
 	 * Default is to not store direct references.
 	 */
 	String USE_DIRECT_REFERENCE_CACHE_ENTRIES = "hibernate.cache.use_reference_entries";
 
 
 
 
 
 
 	// Still to categorize
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
 	String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
 	/**
 	 * Should all database identifiers be quoted.  A {@code true}/{@code false} option.
 	 */
 	String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Assuming {@link #GLOBALLY_QUOTED_IDENTIFIERS}, this allows such global quoting
 	 * to skip column-definitions as defined by {@link javax.persistence.Column},
 	 * {@link javax.persistence.JoinColumn}, etc.
 	 * <p/>
 	 * JPA states that column-definitions are subject to global quoting, so by default this setting
 	 * is {@code false} for JPA compliance.  Set to {@code true} to avoid column-definitions
 	 * being quoted due to global quoting (they will still be quoted if explicitly quoted in the
 	 * annotation/xml).
 	 */
 	String GLOBALLY_QUOTED_IDENTIFIERS_SKIP_COLUMN_DEFINITIONS = "hibernate.globally_quoted_identifiers_skip_column_definitions";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 *
 	 * @deprecated Use {@link #PREFERRED_POOLED_OPTIMIZER} instead
 	 */
 	@Deprecated
 	String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * When a generator specified an increment-size and an optimizer was not explicitly specified, which of
 	 * the "pooled" optimizers should be preferred?  Can specify an optimizer short name or an Optimizer
 	 * impl FQN.
 	 */
 	String PREFERRED_POOLED_OPTIMIZER = "hibernate.id.optimizer.pooled.preferred";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE}
 	 */
 	@Deprecated
 	String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_MAX_SIZE}
 	 */
 	@Deprecated
 	String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * The maximum number of entries including:
 	 * <ul>
 	 *     <li>{@link org.hibernate.engine.query.spi.HQLQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.FilterQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.NativeSQLQueryPlan}</li>
 	 * </ul>
 	 * 
 	 * maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 */
 	String QUERY_PLAN_CACHE_MAX_SIZE = "hibernate.query.plan_cache_max_size";
 
 	/**
 	 * The maximum number of {@link ParameterMetadataImpl} maintained
 	 * by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 */
 	String QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE = "hibernate.query.plan_parameter_metadata_max_size";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// SchemaManagementTool settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Setting to perform SchemaManagementTool actions automatically as part of
 	 * the SessionFactory lifecycle.  Valid options are defined by the
 	 * {@link org.hibernate.tool.schema.Action} enum.
 	 * <p/>
 	 * Interpreted in combination with {@link #HBM2DDL_DATABASE_ACTION} and
 	 * {@link #HBM2DDL_SCRIPTS_ACTION}.  If no value is specified, the default
 	 * is "none" ({@link org.hibernate.tool.schema.Action#NONE}).
 	 *
 	 * @see org.hibernate.tool.schema.Action
 	 */
 	String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Setting to perform SchemaManagementTool actions against the database directly via JDBC
 	 * automatically as part of the SessionFactory lifecycle.  Valid options are defined by the
 	 * {@link org.hibernate.tool.schema.Action} enum.
 	 * <p/>
 	 * Interpreted in combination with {@link #HBM2DDL_AUTO}.  If no value is specified, the default
 	 * is "none" ({@link org.hibernate.tool.schema.Action#NONE}).
 	 *
 	 * @see org.hibernate.tool.schema.Action
 	 */
 	String HBM2DDL_DATABASE_ACTION = "javax.persistence.schema-generation.database.action";
 
 	/**
 	 * Setting to perform SchemaManagementTool actions writing the commands into a DDL script file.
 	 * Valid options are defined by the {@link org.hibernate.tool.schema.Action} enum.
 	 * <p/>
 	 * Interpreted in combination with {@link #HBM2DDL_AUTO}.  If no value is specified, the default
 	 * is "none" ({@link org.hibernate.tool.schema.Action#NONE}).
 	 *
 	 * @see org.hibernate.tool.schema.Action
 	 */
 	String HBM2DDL_SCRIPTS_ACTION = "javax.persistence.schema-generation.scripts.action";
 
 	/**
 	 * Allows passing a specific {@link java.sql.Connection} instance to be used by SchemaManagementTool.
 	 * <p/>
 	 * May also be used to determine the values for {@value #HBM2DDL_DB_NAME},
 	 * {@value #HBM2DDL_DB_MAJOR_VERSION} and {@value #HBM2DDL_DB_MINOR_VERSION}.
 	 */
 	String HBM2DDL_CONNECTION = "javax.persistence.schema-generation-connection";
 
 	/**
 	 * Specifies the name of the database provider in cases where a Connection to the underlying database is
 	 * not available (aka, mainly in generating scripts).  In such cases, a value for this setting
 	 * *must* be specified.
 	 * <p/>
 	 * The value of this setting is expected to match the value returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseProductName()} for the target database.
 	 * <p/>
 	 * Additionally specifying {@value #HBM2DDL_DB_MAJOR_VERSION} and/or {@value #HBM2DDL_DB_MINOR_VERSION}
 	 * may be required to understand exactly how to generate the required schema commands.
 	 *
 	 * @see #HBM2DDL_DB_MAJOR_VERSION
 	 * @see #HBM2DDL_DB_MINOR_VERSION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String HBM2DDL_DB_NAME = "javax.persistence.database-product-name";
 
 	/**
 	 * Specifies the major version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where {@value #HBM2DDL_DB_NAME} does not provide enough distinction.
 
 	 * @see #HBM2DDL_DB_NAME
 	 * @see #HBM2DDL_DB_MINOR_VERSION
 	 */
 	String HBM2DDL_DB_MAJOR_VERSION = "javax.persistence.database-major-version";
 
 	/**
 	 * Specifies the minor version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where the combination of {@value #HBM2DDL_DB_NAME} and {@value #HBM2DDL_DB_MAJOR_VERSION} does not provide
 	 * enough distinction.
 	 *
 	 * @see #HBM2DDL_DB_NAME
 	 * @see #HBM2DDL_DB_MAJOR_VERSION
 	 */
 	String HBM2DDL_DB_MINOR_VERSION = "javax.persistence.database-minor-version";
 
 	/**
 	 * Specifies whether schema generation commands for schema creation are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SourceType} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #HBM2DDL_CREATE_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SourceType
 	 */
 	String HBM2DDL_CREATE_SOURCE = "javax.persistence.schema-generation.create-source";
 
 	/**
 	 * Specifies whether schema generation commands for schema dropping are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SourceType} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #HBM2DDL_DROP_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SourceType
 	 */
 	String HBM2DDL_DROP_SOURCE = "javax.persistence.schema-generation.drop-source";
 
 	/**
 	 * Specifies the CREATE script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 * <p/>
 	 * Hibernate historically also accepted {@link #HBM2DDL_IMPORT_FILES} for a similar purpose.  This setting
 	 * should be preferred over {@link #HBM2DDL_IMPORT_FILES} moving forward
 	 *
 	 * @see #HBM2DDL_CREATE_SOURCE
 	 * @see #HBM2DDL_IMPORT_FILES
 	 */
 	String HBM2DDL_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source";
 
 	/**
 	 * Specifies the DROP script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 *
 	 * @see #HBM2DDL_DROP_SOURCE
 	 */
 	String HBM2DDL_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source";
 
 	/**
 	 * For cases where the {@value #HBM2DDL_SCRIPTS_ACTION} value indicates that schema creation commands should
 	 * be written to DDL script file, {@value #HBM2DDL_SCRIPTS_CREATE_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #HBM2DDL_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String HBM2DDL_SCRIPTS_CREATE_TARGET = "javax.persistence.schema-generation.scripts.create-target";
 
 	/**
 	 * For cases where the {@value #HBM2DDL_SCRIPTS_ACTION} value indicates that schema drop commands should
 	 * be written to DDL script file, {@value #HBM2DDL_SCRIPTS_DROP_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #HBM2DDL_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String HBM2DDL_SCRIPTS_DROP_TARGET = "javax.persistence.schema-generation.scripts.drop-target";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed beforeQuery the statements of the
 	 * following files.
 	 * <p/>
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 * <p/>
 	 * The default value is <tt>/import.sql</tt>
 	 * <p/>
 	 * {@link #HBM2DDL_CREATE_SCRIPT_SOURCE} / {@link #HBM2DDL_DROP_SCRIPT_SOURCE} should be preferred
 	 * moving forward
 	 */
 	String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * JPA variant of {@link #HBM2DDL_IMPORT_FILES}
 	 * <p/>
 	 * Specifies a {@link java.io.Reader} configured for reading of the SQL load script or a string designating the
 	 * file {@link java.net.URL} for the SQL load script.
 	 * <p/>
 	 * A "SQL load script" is a script that performs some database initialization (INSERT, etc).
 	 */
 	String HBM2DDL_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
 
 	/**
 	 * Reference to the {@link org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor} implementation class
 	 * to use for parsing source/import files as defined by {@link #HBM2DDL_CREATE_SCRIPT_SOURCE},
 	 * {@link #HBM2DDL_DROP_SCRIPT_SOURCE} or {@link #HBM2DDL_IMPORT_FILES}.
 	 * <p/>
 	 * Reference may refer to an instance, a Class implementing ImportSqlCommandExtractor of the FQN
 	 * of the ImportSqlCommandExtractor implementation.  If the FQN is given, the implementation
 	 * must provide a no-arg constructor.
 	 * <p/>
 	 * The default value is {@link org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor}.
 	 */
 	String HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR = "hibernate.hbm2ddl.import_files_sql_extractor";
 
 	/**
 	 * Specifies whether to automatically create also the database schema/catalog.
 	 * The default is false.
 	 *
 	 * @since 5.0
 	 */
 	String HBM2DLL_CREATE_NAMESPACES = "hibernate.hbm2dll.create_namespaces";
 
 	/**
 	 * The JPA variant of {@link #HBM2DLL_CREATE_NAMESPACES}
 	 * <p/>
 	 * Specifies whether the persistence provider is to create the database schema(s) in addition to creating
 	 * database objects (tables, sequences, constraints, etc).  The value of this boolean property should be set
 	 * to {@code true} if the persistence provider is to create schemas in the database or to generate DDL that
 	 * contains "CREATE SCHEMA" commands.  If this property is not supplied (or is explicitly {@code false}), the
 	 * provider should not attempt to create database schemas.
 	 */
 	String HBM2DLL_CREATE_SCHEMAS = "javax.persistence.create-database-schemas";
 
 	/**
 	 * Used to specify the {@link org.hibernate.tool.schema.spi.SchemaFilterProvider} to be used by
 	 * create, drop, migrate and validate operations on the database schema.  SchemaFilterProvider
 	 * provides filters that can be used to limit the scope of these operations to specific namespaces,
 	 * tables and sequences. All objects are included by default.
 	 *
 	 * @since 5.1
 	 */
 	String HBM2DDL_FILTER_PROVIDER = "hibernate.hbm2ddl.schema_filter_provider";
 
 	/**
 	 * Identifies the delimiter to use to separate schema management statements in script outputs
 	 */
 	String HBM2DDL_DELIMITER = "hibernate.hbm2ddl.delimiter";
 
 
 	String JMX_ENABLED = "hibernate.jmx.enabled";
 	String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 	String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 
 	/**
 	 * Setting to identify a {@link org.hibernate.CustomEntityDirtinessStrategy} to use.  May point to
 	 * either a class name or instance.
 	 */
 	String CUSTOM_ENTITY_DIRTINESS_STRATEGY = "hibernate.entity_dirtiness_strategy";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	String MULTI_TENANT = "hibernate.multiTenancy";
 
 	/**
 	 * Names a {@link org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider} implementation to
 	 * use.  As MultiTenantConnectionProvider is also a service, can be configured directly through the
 	 * {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}
 	 *
 	 * @since 4.1
 	 */
 	String MULTI_TENANT_CONNECTION_PROVIDER = "hibernate.multi_tenant_connection_provider";
 
 	/**
 	 * Names a {@link org.hibernate.context.spi.CurrentTenantIdentifierResolver} implementation to use.
 	 * <p/>
 	 * Can be<ul>
 	 *     <li>CurrentTenantIdentifierResolver instance</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation {@link Class} reference</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation class name</li>
 	 * </ul>
 	 *
 	 * @since 4.1
 	 */
 	String MULTI_TENANT_IDENTIFIER_RESOLVER = "hibernate.tenant_identifier_resolver";
 
 	/**
 	 * Names a {@link org.hibernate.Interceptor} implementation to be applied to the
 	 * {@link org.hibernate.SessionFactory} and propagated to each Session created from the SessionFactory.
 	 * This setting identifies an Interceptor which is effectively a singleton across all the Sessions
 	 * opened from the SessionFactory to which it is applied; the same instance will be passed to each Session.
 	 * <p/>
 	 * See {@link #SESSION_SCOPED_INTERCEPTOR} for an approach to create unique Interceptor instances for each Session
 	 * <p/>
 	 * Can reference<ul>
 	 *     <li>Interceptor instance</li>
 	 *     <li>Interceptor implementation {@link Class} reference</li>
 	 *     <li>Interceptor implementation class name</li>
 	 * </ul>
 	 *
 	 * @since 5.0
 	 */
 	String INTERCEPTOR = "hibernate.session_factory.interceptor";
 
 	/**
 	 * Names a {@link org.hibernate.Interceptor} implementation to be applied to the
 	 * {@link org.hibernate.SessionFactory} and propagated to each Session created from the SessionFactory.
 	 * This setting identifies an Interceptor implementation that is to be applied to every Session opened
 	 * from the SessionFactory, but unlike {@link #INTERCEPTOR} a unique instance of the Interceptor is
 	 * used for each Session.
 	 * <p/>
 	 * Can reference<ul>
 	 *     <li>Interceptor implementation {@link Class} reference</li>
 	 *     <li>Interceptor implementation class name</li>
 	 * </ul>
 	 * Note specifically that this setting cannot name an Interceptor instance.
 	 *
 	 * @since 5.2
 	 */
 	String SESSION_SCOPED_INTERCEPTOR = "hibernate.session_factory.session_scoped_interceptor";
 
 	/**
 	 * Names a {@link org.hibernate.resource.jdbc.spi.StatementInspector} implementation to be applied to
 	 * the {@link org.hibernate.SessionFactory}.  Can reference<ul>
 	 *     <li>StatementInspector instance</li>
 	 *     <li>StatementInspector implementation {@link Class} reference</li>
 	 *     <li>StatementInspector implementation class name (FQN)</li>
 	 * </ul>
 	 *
 	 * @since 5.0
 	 */
 	String STATEMENT_INSPECTOR = "hibernate.session_factory.statement_inspector";
 
 	String ENABLE_LAZY_LOAD_NO_TRANS = "hibernate.enable_lazy_load_no_trans";
 
 	String HQL_BULK_ID_STRATEGY = "hibernate.hql.bulk_id_strategy";
 
 	/**
 	 * Names the {@link org.hibernate.loader.BatchFetchStyle} to use.  Can specify either the
 	 * {@link org.hibernate.loader.BatchFetchStyle} name (insensitively), or a
 	 * {@link org.hibernate.loader.BatchFetchStyle} instance.
 	 * 
 	 * {@code LEGACY} is the default value.
 	 */
 	String BATCH_FETCH_STYLE = "hibernate.batch_fetch_style";
 	
 	/**
 	 * A transaction can be rolled back by another thread ("tracking by thread")
 	 * -- not the original application. Examples of this include a JTA
 	 * transaction timeout handled by a background reaper thread.  The ability
 	 * to handle this situation requires checking the Thread ID every time
 	 * Session is called.  This can certainly have performance considerations.
 	 * 
 	 * Default is <code>true</code> (enabled).
 	 */
 	String JTA_TRACK_BY_THREAD = "hibernate.jta.track_by_thread";
 
 	String JACC_CONTEXT_ID = "hibernate.jacc_context_id";
 	String JACC_PREFIX = "hibernate.jacc";
 	String JACC_ENABLED = "hibernate.jacc.enabled";
 
 	/**
 	 * If enabled, allows schema update and validation to support synonyms.  Due
 	 * to the possibility that this would return duplicate tables (especially in
 	 * Oracle), this is disabled by default.
 	 */
 	String ENABLE_SYNONYMS = "hibernate.synonyms";
 
 	/**
 	 * Identifies a comma-separate list of values to specify extra table types,
 	 * other than the default "TABLE" value, to recognize as defining a physical table
 	 * by schema update, creation and validation.
 	 *
 	 * @since 5.0
 	 */
 	String EXTRA_PHYSICAL_TABLE_TYPES = "hibernate.hbm2dll.extra_physical_table_types";
 
 	/**
 	 * Unique columns and unique keys both use unique constraints in most dialects.
 	 * SchemaUpdate needs to create these constraints, but DB's
 	 * support for finding existing constraints is extremely inconsistent. Further,
 	 * non-explicitly-named unique constraints use randomly generated characters.
 	 * 
 	 * Therefore, select from these strategies.
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#DROP_RECREATE_QUIETLY} (DEFAULT):
 	 * 			Attempt to drop, then (re-)create each unique constraint.
 	 * 			Ignore any exceptions thrown.
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#RECREATE_QUIETLY}:
 	 * 			attempt to (re-)create unique constraints,
 	 * 			ignoring exceptions thrown if the constraint already existed
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#SKIP}:
 	 * 			do not attempt to create unique constraints on a schema update
 	 */
 	String UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY = "hibernate.schema_update.unique_constraint_strategy";
 
 	/**
 	 * Enable statistics collection
 	 */
 	String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	/**
 	 * A setting to control whether to {@link org.hibernate.engine.internal.StatisticalLoggingSessionEventListener} is
 	 * enabled on all Sessions (unless explicitly disabled for a given Session).  The default value of this
 	 * setting is determined by the value for {@link #GENERATE_STATISTICS}, meaning that if collection of statistics
 	 * is enabled logging of Session metrics is enabled by default too.
 	 */
 	String LOG_SESSION_METRICS = "hibernate.session.events.log";
 
 	/**
 	 * Defines a default {@link org.hibernate.SessionEventListener} to be applied to opened Sessions.
 	 */
 	String AUTO_SESSION_EVENTS_LISTENER = "hibernate.session.events.auto";
 
 	/**
 	 * Global setting for whether NULL parameter bindings should be passed to database
 	 * procedure/function calls as part of {@link org.hibernate.procedure.ProcedureCall}
 	 * handling.  Implicitly Hibernate will not pass the NULL, the intention being to allow
 	 * any default argumnet values to be applied.
 	 * <p/>
 	 * This defines a global setting, which can them be controlled per parameter via
 	 * {@link org.hibernate.procedure.ParameterRegistration#enablePassingNulls(boolean)}
 	 * <p/>
 	 * Values are {@code true} (pass the NULLs) or {@code false} (do not pass the NULLs).
 	 */
 	String PROCEDURE_NULL_PARAM_PASSING = "hibernate.proc.param_null_passing";
 
-	/*
+	/**
 	 * Enable instantiation of composite/embedded objects when all of its attribute values are {@code null}.
 	 * The default (and historical) behavior is that a {@code null} reference will be used to represent the
 	 * composite when all of its attributes are {@code null}
 	 *
 	 * @since 5.1
 	 */
 	String CREATE_EMPTY_COMPOSITES_ENABLED = "hibernate.create_empty_composites.enabled";
 
 	/**
 	 * Setting that allows access to the underlying {@link org.hibernate.Transaction}, even
 	 * when using a JTA since normal JPA operations prohibit this behavior.
 	 * <p/>
 	 * Values are {@code true} grants access, {@code false} does not.
 	 * <p/>
 	 * The default behavior is to allow access unless the session is bootstrapped via JPA.
 	 */
 	String ALLOW_JTA_TRANSACTION_ACCESS = "hibernate.jta.allowTransactionAccess";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java
index ac9a41036a..0c7d9e9bf5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryHintDefinition.java
@@ -1,135 +1,157 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import javax.persistence.LockModeType;
 import javax.persistence.NamedQuery;
 import javax.persistence.QueryHint;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
+import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.QueryHints;
 import org.hibernate.internal.util.LockModeConverter;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 public class QueryHintDefinition {
 	private final Map<String, Object> hintsMap;
 
 	public QueryHintDefinition(final QueryHint[] hints) {
 		if ( hints == null || hints.length == 0 ) {
 			hintsMap = Collections.emptyMap();
 		}
 		else {
 			final Map<String, Object> hintsMap = new HashMap<String, Object>();
 			for ( QueryHint hint : hints ) {
 				hintsMap.put( hint.name(), hint.value() );
 			}
 			this.hintsMap = hintsMap;
 		}
 	}
 
 
 	public CacheMode getCacheMode(String query) {
 		String hitName = QueryHints.CACHE_MODE;
 		String value =(String) hintsMap.get( hitName );
 		if ( value == null ) {
 			return null;
 		}
 		try {
 			return CacheMode.interpretExternalSetting( value );
 		}
 		catch ( MappingException e ) {
 			throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hitName, e );
 		}
 	}
 
 	public FlushMode getFlushMode(String query) {
 		String hitName = QueryHints.FLUSH_MODE;
 		String value =(String)  hintsMap.get( hitName );
 		if ( value == null ) {
 			return null;
 		}
 		try {
 			return FlushMode.interpretExternalSetting( value );
 		}
 		catch ( MappingException e ) {
 			throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hitName, e );
 		}
 	}
 
+	public LockMode getLockMode(String query) {
+		String hitName = QueryHints.NATIVE_LOCKMODE;
+		String value =(String) hintsMap.get( hitName );
+		if ( value == null ) {
+			return null;
+		}
+		try {
+			return LockMode.fromExternalForm( value );
+		}
+		catch ( MappingException e ) {
+			throw new AnnotationException( "Unknown LockMode in hint: " + query + ":" + hitName, e );
+		}
+	}
+
 	public boolean getBoolean(String query, String hintName) {
 		String value =(String)  hintsMap.get( hintName );
 		if ( value == null ) {
 			return false;
 		}
 		if ( value.equalsIgnoreCase( "true" ) ) {
 			return true;
 		}
 		else if ( value.equalsIgnoreCase( "false" ) ) {
 			return false;
 		}
 		else {
 			throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hintName );
 		}
 
 	}
 
 	public String getString(String query, String hintName) {
 		return (String) hintsMap.get( hintName );
 	}
 
 	public Integer getInteger(String query, String hintName) {
 		String value = (String) hintsMap.get( hintName );
 		if ( value == null ) {
 			return null;
 		}
 		try {
 			return Integer.decode( value );
 		}
 		catch ( NumberFormatException nfe ) {
 			throw new AnnotationException( "Not an integer in hint: " + query + ":" + hintName, nfe );
 		}
 	}
 
 	public Integer getTimeout(String queryName) {
 		Integer timeout = getInteger( queryName, QueryHints.TIMEOUT_JPA );
 
 		if ( timeout != null ) {
 			// convert milliseconds to seconds
 			timeout = (int) Math.round( timeout.doubleValue() / 1000.0 );
 		}
 		else {
 			// timeout is already in seconds
 			timeout = getInteger( queryName, QueryHints.TIMEOUT_HIBERNATE );
 		}
 		return timeout;
 	}
 
 	public LockOptions determineLockOptions(NamedQuery namedQueryAnnotation) {
 		LockModeType lockModeType = namedQueryAnnotation.lockMode();
 		Integer lockTimeoutHint = getInteger( namedQueryAnnotation.name(), "javax.persistence.lock.timeout" );
+		Boolean followOnLocking = getBoolean( namedQueryAnnotation.name(), QueryHints.FOLLOW_ON_LOCKING );
+
+		return determineLockOptions(lockModeType, lockTimeoutHint, followOnLocking);
+	}
+
+	private LockOptions determineLockOptions(LockModeType lockModeType, Integer lockTimeoutHint, Boolean followOnLocking) {
 
-		LockOptions lockOptions = new LockOptions( LockModeConverter.convertToLockMode( lockModeType ) );
+		LockOptions lockOptions = new LockOptions( LockModeConverter.convertToLockMode( lockModeType ) )
+				.setFollowOnLocking( followOnLocking );
 		if ( lockTimeoutHint != null ) {
 			lockOptions.setTimeOut( lockTimeoutHint );
 		}
 
 		return lockOptions;
 	}
 
 	public Map<String, Object> getHintsMap() {
 		return hintsMap;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 7eb7f61153..fbac5d1cff 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,1063 +1,1064 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.DatabaseMetaData;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.ScrollMode;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Sequence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
 import org.hibernate.engine.jdbc.env.spi.AnsiSqlKeywords;
 import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
 import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
 import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.loader.BatchLoadSizingStrategy;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.tool.schema.internal.StandardAuxiliaryDatabaseObjectExporter;
 import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.internal.StandardSequenceExporter;
 import org.hibernate.tool.schema.internal.StandardTableExporter;
 import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
  * with different systems.  Subclasses should provide a public default constructor that register a set of type
  * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 @SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( Dialect.class );
 
 	/**
 	 * Defines a default batch size constant
 	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
 
 	/**
 	 * Defines a "no batching" batch size constant
 	 */
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 
 	/**
 	 * Characters used as closing for quoting SQL identifiers
 	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<>();
 	private final Set<String> sqlKeywords = new HashSet<>();
 
 	private final UniqueDelegate uniqueDelegate;
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
 		// register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
 
 		if(supportsPartitionBy()) {
 			registerKeyword( "PARTITION" );
 		}
 
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
 		final String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
 			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Allows the Dialect to contribute additional types
 	 *
 	 * @param typeContributions Callback to contribute the types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
 		// by default, nothing to do
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		final String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
 		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
 	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
 			if ( original != target ) {
 				try {
 					// the BLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setBinaryStream( 1L );
 					// the BLOB from the detached state
 					final InputStream detachedStream = original.getBinaryStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
 			if ( original != target ) {
 				try {
 					// the CLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the CLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
 			if ( original != target ) {
 				try {
 					// the NCLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the NCLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator(
 						session
 				);
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		final String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Whether or not the given type name has been registered for this dialect (including both hibernate type names and
 	 * custom-registered type names).
 	 *
 	 * @param typeName the type name.
 	 *
 	 * @return true if the given string has been registered either as a hibernate type or as a custom-registered one
 	 */
 	public boolean isTypeNameRegistered(final String typeName) {
 		return this.typeNames.containsTypeName( typeName );
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format(
 							"No Hibernate type mapping for type [code=%s, length=%s]",
 							code,
 							length
 					)
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase( Locale.ROOT ), function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( getIdentityColumnSupport().supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else {
 			return SequenceStyleGenerator.class;
 		}
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the appropriate {@link IdentityColumnSupport}
 	 *
 	 * @return the IdentityColumnSupport
 	 * @since 5.1
 	 */
 	public IdentityColumnSupport getIdentityColumnSupport(){
 		return new IdentityColumnSupportImpl();
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
 	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		if ( getQuerySequencesString() == null ) {
 			return SequenceInformationExtractorNoOpImpl.INSTANCE;
 		}
 		else {
 			return SequenceInformationExtractorLegacyImpl.INSTANCE;
 		}
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns the delegate managing LIMIT clause.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler getLimitHandler() {
 		return new LegacyLimitHandler( this );
 	}
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come beforeQuery other parameters
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getLimitString(String query, int offset, int limit) {
@@ -1653,1166 +1654,1181 @@ public abstract class Dialect implements ConversionContext {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * Does this dialect support case insensitive LIKE restrictions?
 	 *
 	 * @return {@code true} if the underlying database supports case insensitive like comparison,
 	 * {@code false} otherwise.  The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 * <p/>
 	 * The maximum here should account for the fact that Hibernate often needs to append "uniqueing" information
 	 * to the end of generated aliases.  That "uniqueing" information will be added to the end of a identifier
 	 * generated to the length specified here; so be sure to leave some room (generally speaking 5 positions will
 	 * suffice).
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		// When tokens are checked for keywords, they are always compared against the lower-case version of the token.
 		// For instance, Template#renderWhereStringTemplate transforms all tokens to lower-case too.
 		sqlKeywords.add(word.toLowerCase(Locale.ROOT));
 	}
 
 	/**
 	 * @deprecated These are only ever used (if at all) from the code that handles identifier quoting.  So
 	 * see {@link #buildIdentifierHelper} instead
 	 */
 	@Deprecated
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 	/**
 	 * Build the IdentifierHelper indicated by this Dialect for handling identifier conversions.
 	 * Returning {@code null} is allowed and indicates that Hibernate should fallback to building a
 	 * "standard" helper.  In the fallback path, any changes made to the IdentifierHelperBuilder
 	 * during this call will still be incorporated into the built IdentifierHelper.
 	 * <p/>
 	 * The incoming builder will have the following set:<ul>
 	 *     <li>{@link IdentifierHelperBuilder#isGloballyQuoteIdentifiers()}</li>
 	 *     <li>{@link IdentifierHelperBuilder#getUnquotedCaseStrategy()} - initialized to UPPER</li>
 	 *     <li>{@link IdentifierHelperBuilder#getQuotedCaseStrategy()} - initialized to MIXED</li>
 	 * </ul>
 	 * <p/>
 	 * By default Hibernate will do the following:<ul>
 	 *     <li>Call {@link IdentifierHelperBuilder#applyIdentifierCasing(DatabaseMetaData)}
 	 *     <li>Call {@link IdentifierHelperBuilder#applyReservedWords(DatabaseMetaData)}
 	 *     <li>Applies {@link AnsiSqlKeywords#sql2003()} as reserved words</li>
 	 *     <li>Applies the {#link #sqlKeywords} collected here as reserved words</li>
 	 *     <li>Applies the Dialect's NameQualifierSupport, if it defines one</li>
 	 * </ul>
 	 *
 	 * @param builder A semi-configured IdentifierHelper builder.
 	 * @param dbMetaData Access to the metadata returned from the driver if needed and if available.  WARNING: may be {@code null}
 	 *
 	 * @return The IdentifierHelper instance to use, or {@code null} to indicate Hibernate should use its fallback path
 	 *
 	 * @throws SQLException Accessing the DatabaseMetaData can throw it.  Just re-throw and Hibernate will handle.
 	 *
 	 * @see #getNameQualifierSupport()
 	 */
 	public IdentifierHelper buildIdentifierHelper(
 			IdentifierHelperBuilder builder,
 			DatabaseMetaData dbMetaData) throws SQLException {
 		builder.applyIdentifierCasing( dbMetaData );
 
 		builder.applyReservedWords( dbMetaData );
 		builder.applyReservedWords( AnsiSqlKeywords.INSTANCE.sql2003() );
 		builder.applyReservedWords( sqlKeywords );
 
 		builder.setNameQualifierSupport( getNameQualifierSupport() );
 
 		return builder.build();
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private StandardTableExporter tableExporter = new StandardTableExporter( this );
 	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter( this );
 	private StandardIndexExporter indexExporter = new StandardIndexExporter( this );
 	private StandardForeignKeyExporter foreignKeyExporter = new StandardForeignKeyExporter( this );
 	private StandardUniqueKeyExporter uniqueKeyExporter = new StandardUniqueKeyExporter( this );
 	private StandardAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter = new StandardAuxiliaryDatabaseObjectExporter( this );
 
 	public Exporter<Table> getTableExporter() {
 		return tableExporter;
 	}
 
 	public Exporter<Sequence> getSequenceExporter() {
 		return sequenceExporter;
 	}
 
 	public Exporter<Index> getIndexExporter() {
 		return indexExporter;
 	}
 
 	public Exporter<ForeignKey> getForeignKeyExporter() {
 		return foreignKeyExporter;
 	}
 
 	public Exporter<Constraint> getUniqueKeyExporter() {
 		return uniqueKeyExporter;
 	}
 
 	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
 		return auxiliaryObjectExporter;
 	}
 
 	/**
 	 * Does this dialect support catalog creation?
 	 *
 	 * @return True if the dialect supports catalog creation; false otherwise.
 	 */
 	public boolean canCreateCatalog() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL command used to create the named catalog
 	 *
 	 * @param catalogName The name of the catalog to be created.
 	 *
 	 * @return The creation commands
 	 */
 	public String[] getCreateCatalogCommand(String catalogName) {
 		throw new UnsupportedOperationException( "No create catalog syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * Get the SQL command used to drop the named catalog
 	 *
 	 * @param catalogName The name of the catalog to be dropped.
 	 *
 	 * @return The drop commands
 	 */
 	public String[] getDropCatalogCommand(String catalogName) {
 		throw new UnsupportedOperationException( "No drop catalog syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * Does this dialect support schema creation?
 	 *
 	 * @return True if the dialect supports schema creation; false otherwise.
 	 */
 	public boolean canCreateSchema() {
 		return true;
 	}
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation commands
 	 */
 	public String[] getCreateSchemaCommand(String schemaName) {
 		return new String[] {"create schema " + schemaName};
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop commands
 	 */
 	public String[] getDropSchemaCommand(String schemaName) {
 		return new String[] {"drop schema " + schemaName};
 	}
 
 	/**
 	 * Get the SQL command used to retrieve the current schema name.  Works in conjunction
 	 * with {@link #getSchemaNameResolver()}, unless the return from there does not need this
 	 * information.  E.g., a custom impl might make use of the Java 1.7 addition of
 	 * the {@link java.sql.Connection#getSchema()} method
 	 *
 	 * @return The current schema retrieval SQL
 	 */
 	public String getCurrentSchemaCommand() {
 		return null;
 	}
 
 	/**
 	 * Get the strategy for determining the schema name of a Connection
 	 *
 	 * @return The schema name resolver strategy
 	 */
 	public SchemaNameResolver getSchemaNameResolver() {
 		return DefaultSchemaNameResolver.INSTANCE;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints beforeQuery dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * The syntax for the suffix used to add a column to a table (optional).
 	 *
 	 * @return The suffix "add column" fragment.
 	 */
 	public String getAddColumnSuffixString() {
 		return "";
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String foreignKeyDefinition) {
 		return new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " " )
 				.append( foreignKeyDefinition )
 				.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
 	/**
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied beforeQuery the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied beforeQuery the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied afterQuery the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied afterQuery the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied beforeQuery the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied beforeQuery the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied afterQuery the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied afterQuery the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase(Locale.ROOT) );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support `count(distinct a,b)`?
 	 *
 	 * @return True if the database supports counting distinct tuples; false otherwise.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 
 	/**
 	 * If {@link #supportsTupleDistinctCounts()} is true, does the Dialect require the tuple to be wrapped with parens?
 	 *
 	 * @return boolean
 	 */
 	public boolean requiresParensForTupleDistinctCounts() {
 		return false;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
+	@Deprecated
 	public boolean useFollowOnLocking() {
+		return useFollowOnLocking( null );
+	}
+
+	/**
+	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
+	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
+	 * by subsequent selects.
+	 *
+	 * @param parameters query parameters
+	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
+	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
+	 * @since 5.2
+	 */
+	public boolean useFollowOnLocking(QueryParameters parameters) {
 		return false;
 	}
 
 	/**
 	 * Negate an expression
 	 *
 	 * @param expression The expression to negate
 	 *
 	 * @return The negated expression
 	 */
 	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
 	 * Get the UniqueDelegate supported by this dialect
 	 *
 	 * @return The UniqueDelegate
 	 */
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support adding Unique constraints via create and alter table ?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a unique constraint to a table.
 	 *
 	 * @param constraintName The name of the unique constraint.
 	 * @return The "add unique" fragment
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " unique ";
 	}
 
 	/**
 	 * Is the combination of not-null and unique supported?
 	 *
 	 * @return deprecated
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 
 	/**
 	 * Apply a hint to the query.  The entire query is provided, allowing the Dialect full control over the placement
 	 * and syntax of the hint.  By default, ignore the hint and simply return the query.
 	 *
 	 * @param query The query to which to apply the hint.
 	 * @param hints The  hints to apply
 	 * @return The modified SQL
 	 */
 	public String getQueryHintString(String query, List<String> hints) {
 		return query;
 	}
 
 	/**
 	 * Certain dialects support a subset of ScrollModes.  Provide a default to be used by Criteria and Query.
 	 *
 	 * @return ScrollMode
 	 */
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.SCROLL_INSENSITIVE;
 	}
 
 	/**
 	 * Does this dialect support tuples in subqueries?  Ex:
 	 * delete from Table1 where (col1, col2) in (select col1, col2 from Table2)
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsTuplesInSubqueries() {
 		return true;
 	}
 
 	public CallableStatementSupport getCallableStatementSupport() {
 		// most databases do not support returning cursors (ref_cursor)...
 		return StandardCallableStatementSupport.NO_REF_CURSOR_INSTANCE;
 	}
 
 	/**
 	 * By default interpret this based on DatabaseMetaData.
 	 *
 	 * @return
 	 */
 	public NameQualifierSupport getNameQualifierSupport() {
 		return null;
 	}
 
 	protected final BatchLoadSizingStrategy STANDARD_DEFAULT_BATCH_LOAD_SIZING_STRATEGY = new BatchLoadSizingStrategy() {
 		@Override
 		public int determineOptimalBatchLoadSize(int numberOfKeyColumns, int numberOfKeys) {
 			return 50;
 		}
 	};
 
 	public BatchLoadSizingStrategy getDefaultBatchLoadSizingStrategy() {
 		return STANDARD_DEFAULT_BATCH_LOAD_SIZING_STRATEGY;
 	}
 
 	/**
 	 * Does the fetching JDBC statement warning for logging is enabled by default
 	 *
 	 * @return boolean
 	 *
 	 * @since 5.1
 	 */
 	public boolean isJdbcLogWarningsEnabledByDefault() {
 		return true;
 	}
 
 	public void augmentRecognizedTableTypes(List<String> tableTypesList) {
 		// noihing to do
 	}
 
 	/**
 	 * Does the underlying database support partition by
 	 *
 	 * @return boolean
 	 *
 	 * @since 5.2
 	 */
 	public boolean supportsPartitionBy() {
 		return false;
 	}
 
 	/**
 	 * Override the DatabaseMetaData#supportsNamedParameters()
 	 *
 	 * @return boolean
 	 *
 	 * @throws SQLException Accessing the DatabaseMetaData can throw it.  Just re-throw and Hibernate will handle.
 	 */
 	public boolean supportsNamedParameters(DatabaseMetaData databaseMetaData) throws SQLException {
 		return databaseMetaData != null && databaseMetaData.supportsNamedParameters();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index ef593f8839..26bfe58273 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,680 +1,711 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.List;
 import java.util.Locale;
+import java.util.regex.Pattern;
 
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Oracle 8i.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings("deprecation")
 public class Oracle8iDialect extends Dialect {
 
+	private static final Pattern DISTINCT_KEYWORD_PATTERN = Pattern.compile( "\\bdistinct\\b" );
+
+	private static final Pattern GROUP_BY_KEYWORD_PATTERN = Pattern.compile( "\\bgroup by\\b" );
+
+	private static final Pattern ORDER_BY_KEYWORD_PATTERN = Pattern.compile( "\\border by\\b" );
+
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			sql = sql.trim();
 			boolean isForUpdate = false;
 			if (sql.toLowerCase(Locale.ROOT).endsWith( " for update" )) {
 				sql = sql.substring( 0, sql.length() - 11 );
 				isForUpdate = true;
 			}
 
 			final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 			if (hasOffset) {
 				pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 			}
 			else {
 				pagingSelect.append( "select * from ( " );
 			}
 			pagingSelect.append( sql );
 			if (hasOffset) {
 				pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 			}
 			else {
 				pagingSelect.append( " ) where rownum <= ?" );
 			}
 
 			if (isForUpdate) {
 				pagingSelect.append( " for update" );
 			}
 
 			return pagingSelect.toString();
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 	};
 
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
 	/**
 	 * Constructs a Oracle8iDialect
 	 */
 	public Oracle8iDialect() {
 		super();
 		registerCharacterTypeMappings();
 		registerNumericTypeMappings();
 		registerDateTimeTypeMappings();
 		registerLargeObjectTypeMappings();
 		registerReverseHibernateTypeMappings();
 		registerFunctions();
 		registerDefaultProperties();
 	}
 
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	protected void registerNumericTypeMappings() {
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 
 		registerColumnType( Types.BOOLEAN, "number(1,0)" );
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "date" );
 	}
 
 	protected void registerLargeObjectTypeMappings() {
 		registerColumnType( Types.BINARY, 2000, "raw($l)" );
 		registerColumnType( Types.BINARY, "long raw" );
 
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.LONGVARCHAR, "long" );
 		registerColumnType( Types.LONGVARBINARY, "long raw" );
 	}
 
 	protected void registerReverseHibernateTypeMappings() {
 	}
 
 	protected void registerFunctions() {
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "bitand", new StandardSQLFunction("bitand") );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
 		registerFunction( "soundex", new StandardSQLFunction("soundex") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP) );
 
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 
 		registerFunction( "last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
 		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
 		registerFunction( "systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 
 		registerFunction( "rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false) );
 		registerFunction( "rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", "") );
 		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER) );
 		registerFunction( "instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER) );
 		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
 		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
 		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING) );
 		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER) );
 		registerFunction( "mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 		registerFunction( "nvl2", new StandardSQLFunction("nvl2") );
 		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE) );
 		registerFunction( "months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT) );
 		registerFunction( "next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE) );
 
 		registerFunction( "str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 	}
 
 	protected void registerDefaultProperties() {
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 		getDefaultProperties().setProperty( Environment.BATCH_VERSIONED_DATA, "false" );
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 
 	// features which change between 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	/**
 	 * Map case support to the Oracle DECODE function.  Oracle did not
 	 * add support for CASE until 9i.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase(Locale.ROOT).endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
 		if (hasOffset) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if (hasOffset) {
 			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	/**
 	 * Allows access to the basic {@link Dialect#getSelectClauseNullString}
 	 * implementation...
 	 *
 	 * @param sqlType The {@link java.sql.Types} mapping type code
 	 * @return The appropriate select cluse fragment
 	 */
 	public String getBasicSelectClauseNullString(int sqlType) {
 		return super.getSelectClauseNullString( sqlType );
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		switch(sqlType) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				return "to_char(null)";
 			case Types.DATE:
 			case Types.TIMESTAMP:
 			case Types.TIME:
 				return "to_date(null)";
 			default:
 				return "to_number(null)";
 		}
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
 
 	// features which remain constant across 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		//starts with 1, implicitly
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return    " select sequence_name from all_sequences"
 				+ "  union"
 				+ " select synonym_name"
 				+ "   from all_synonyms us, all_sequences asq"
 				+ "  where asq.sequence_name = us.table_name"
 				+ "    and asq.sequence_owner = us.table_owner";
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "(", ")", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
 
 	};
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				// interpreting Oracle exceptions is much much more precise based on their specific vendor codes.
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 
 				// lock timeouts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( errorCode == 30006 ) {
 					// ORA-30006: resource busy; acquire with WAIT timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( errorCode == 54 ) {
 					// ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( 4021 == errorCode ) {
 					// ORA-04021 timeout occurred while waiting to lock object
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 
 
 				// deadlocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 60 == errorCode ) {
 					// ORA-00060: deadlock detected while waiting for resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 				else if ( 4020 == errorCode ) {
 					// ORA-04020 deadlock detected while trying to lock object
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 
 				// query cancelled ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1013 == errorCode ) {
 					// ORA-01013: user requested cancel of current operation
 					throw new QueryTimeoutException(  message, sqlException, sql );
 				}
 
 
 				// data integrity violation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1407 == errorCode ) {
 					// ORA-01407: cannot update column to NULL
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new GlobalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						final String name = super.generateIdTableName( baseName );
 						return name.length() > 30 ? name.substring( 0, 30 ) : name;
 					}
 
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create global temporary table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "on commit delete rows";
 					}
 				},
 				AfterUseAction.CLEAN
 		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 	
 	@Override
 	public boolean forceLobAsLastValue() {
 		return true;
 	}
 
+	/**
+	 * For Oracle, the FOR UPDATE clause cannot be applied when using ORDER BY, DISTINCT or views.
+	 * @param parameters
+	 * @return
+	 @see <a href="https://docs.oracle.com/database/121/SQLRF/statements_10002.htm#SQLRF01702">Oracle FOR UPDATE restrictions</a>
+	 */
 	@Override
-	public boolean useFollowOnLocking() {
-		return true;
+	public boolean useFollowOnLocking(QueryParameters parameters) {
+
+		if (parameters != null ) {
+			String lowerCaseSQL = parameters.getFilteredSQL().toLowerCase();
+
+			return
+				DISTINCT_KEYWORD_PATTERN.matcher( lowerCaseSQL ).find() ||
+				GROUP_BY_KEYWORD_PATTERN.matcher( lowerCaseSQL ).find() ||
+				(
+					parameters.hasRowSelection() &&
+						(
+							ORDER_BY_KEYWORD_PATTERN.matcher( lowerCaseSQL ).find() ||
+							parameters.getRowSelection().getFirstRow() != null
+						)
+				);
+		}
+		else {
+			return true;
+		}
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 	
 	@Override
 	public String getQueryHintString(String sql, List<String> hints) {
 		final String hint = StringHelper.join( ", ", hints.iterator() );
 		
 		if ( StringHelper.isEmpty( hint ) ) {
 			return sql;
 		}
 
 		final int pos = sql.indexOf( "select" );
 		if ( pos > -1 ) {
 			final StringBuilder buffer = new StringBuilder( sql.length() + hint.length() + 8 );
 			if ( pos > 0 ) {
 				buffer.append( sql.substring( 0, pos ) );
 			}
 			buffer.append( "select /*+ " ).append( hint ).append( " */" )
 					.append( sql.substring( pos + "select".length() ) );
 			sql = buffer.toString();
 		}
 
 		return sql;
 	}
 	
 	@Override
 	public int getMaxAliasLength() {
 		// Oracle's max identifier length is 30, but Hibernate needs to add "uniqueing info" so we account for that,
 		return 20;
 	}
 
 	@Override
 	public CallableStatementSupport getCallableStatementSupport() {
 		// Oracle supports returning cursors
 		return StandardCallableStatementSupport.REF_CURSOR_INSTANCE;
 	}
 
 	@Override
 	public boolean canCreateSchema() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsPartitionBy() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
index c7eb3ce7dc..867673ce7f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
@@ -1,268 +1,269 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.model.relational.QualifiedNameImpl;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.identity.Teradata14IdentityColumnSupport;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
+import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Index;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for the Teradata database
  */
 public class Teradata14Dialect extends TeradataDialect {
 	/**
 	 * Constructor
 	 */
 	StandardIndexExporter TeraIndexExporter = null;
 
 	public Teradata14Dialect() {
 		super();
 		//registerColumnType data types
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.BINARY, "VARBYTE(100)" );
 		registerColumnType( Types.LONGVARBINARY, "VARBYTE(32000)" );
 		registerColumnType( Types.LONGVARCHAR, "VARCHAR(32000)" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIME, "current_time" ) );
 		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.DATE, "current_date" ) );
 
 		TeraIndexExporter =  new TeradataIndexExporter( this );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "Add";
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * <tt>java.sql.Types</tt> typecode.
 	 *
 	 * @param code <tt>java.sql.Types</tt> typecode
 	 * @param length the length or precision of the column
 	 * @param precision the precision of the column
 	 * @param scale the scale of the column
 	 *
 	 * @return the database type name
 	 *
 	 * @throws HibernateException
 	 */
 	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		/*
 		 * We might want a special case for 19,2. This is very common for money types
 		 * and here it is converted to 18,1
 		 */
 		float f = precision > 0 ? (float) scale / (float) precision : 0;
 		int p = ( precision > 38 ? 38 : precision );
 		int s = ( precision > 38 ? (int) ( 38.0 * f ) : ( scale > 38 ? 38 : scale ) );
 		return super.getTypeName( code, length, p, s );
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		statement.registerOutParameter( col, Types.REF );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement cs) throws SQLException {
 		boolean isResultSet = cs.execute();
 		while ( !isResultSet && cs.getUpdateCount() != -1 ) {
 			isResultSet = cs.getMoreResults();
 		}
 		return cs.getResultSet();
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 
 			int errorCode = sqle.getErrorCode();
 			if ( errorCode == 27003 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == 2700 ) {
 				constraintName = extractUsingTemplate( "Referential constraint", "violation:", sqle.getMessage() );
 			}
 			else if ( errorCode == 5317 ) {
 				constraintName = extractUsingTemplate( "Check constraint (", ") violated.", sqle.getMessage() );
 			}
 
 			if ( constraintName != null ) {
 				int i = constraintName.indexOf( '.' );
 				if ( i != -1 ) {
 					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 			return constraintName;
 		}
 	};
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		String sMsg = " Locking row for write ";
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return sMsg + " nowait ";
 		}
 		return sMsg;
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		String sMsg = " Locking row for read  ";
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return sMsg + " nowait ";
 		}
 		return sMsg;
 	}
 
 	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString() + " " + sql;
 	}
 
 	@Override
-	public boolean useFollowOnLocking() {
+	public boolean useFollowOnLocking(QueryParameters parameters) {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
 	@Override
 	public Exporter<Index> getIndexExporter() {
 		return TeraIndexExporter;
 	}
 
 
 	private class TeradataIndexExporter extends StandardIndexExporter implements Exporter<Index> {
 
 		public TeradataIndexExporter(Dialect dialect) {
 			super(dialect);
 		}
 
 		@Override
 		public String[] getSqlCreateStrings(Index index, Metadata metadata) {
 			final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
 			final String tableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 					index.getTable().getQualifiedTableName(),
 					jdbcEnvironment.getDialect()
 			);
 
 			final String indexNameForCreation;
 			if ( getDialect().qualifyIndexName() ) {
 				indexNameForCreation = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 						new QualifiedNameImpl(
 								index.getTable().getQualifiedTableName().getCatalogName(),
 								index.getTable().getQualifiedTableName().getSchemaName(),
 								jdbcEnvironment.getIdentifierHelper().toIdentifier( index.getName() )
 						),
 						jdbcEnvironment.getDialect()
 				);
 			}
 			else {
 				indexNameForCreation = index.getName();
 			}
 
 			StringBuilder colBuf = new StringBuilder("");
 			boolean first = true;
 			Iterator<Column> columnItr = index.getColumnIterator();
 			while ( columnItr.hasNext() ) {
 				final Column column = columnItr.next();
 				if ( first ) {
 					first = false;
 				}
 				else {
 					colBuf.append( ", " );
 				}
 				colBuf.append( ( column.getQuotedName( jdbcEnvironment.getDialect() )) );
 			}
 			colBuf.append( ")" );
 
 			final StringBuilder buf = new StringBuilder()
 					.append( "create index " )
 					.append( indexNameForCreation )
 					.append(  "(" + colBuf  )
 					.append( " on " )
 					.append( tableName );
 
 			return new String[] { buf.toString() };
 		}
 	}
 
 	@Override
 	public IdentityColumnSupport getIdentityColumnSupport() {
 		return new Teradata14IdentityColumnSupport();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
index 5cb45af260..d343a8fe02 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
@@ -1,502 +1,503 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.ReplicationMode;
 import org.hibernate.TransientPropertyValueException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class CascadingActions {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			CascadingAction.class.getName()
 	);
 
 	/**
 	 * Disallow instantiation
 	 */
 	private CascadingActions() {
 	}
 
 	/**
 	 * @see org.hibernate.Session#delete(Object)
 	 */
 	public static final CascadingAction DELETE = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled) {
 			LOG.tracev( "Cascading to delete: {0}", entityName );
 			session.delete( entityName, child, isCascadeDeleteEnabled, (Set) anything );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// delete does cascade to uninitialized collections
 			return getAllElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			// orphans should be deleted during delete
 			return true;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_DELETE";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#lock(Object, org.hibernate.LockMode)
 	 */
 	public static final CascadingAction LOCK = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled) {
 			LOG.tracev( "Cascading to lock: {0}", entityName );
 			LockMode lockMode = LockMode.NONE;
 			LockOptions lr = new LockOptions();
 			if ( anything instanceof LockOptions ) {
 				LockOptions lockOptions = (LockOptions) anything;
 				lr.setTimeOut( lockOptions.getTimeOut() );
 				lr.setScope( lockOptions.getScope() );
+				lr.setFollowOnLocking( lockOptions.getFollowOnLocking() );
 				if ( lockOptions.getScope() ) {
 					lockMode = lockOptions.getLockMode();
 				}
 			}
 			lr.setLockMode( lockMode );
 			session.buildLockRequest( lr ).lock( entityName, child );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// lock doesn't cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			//TODO: should orphans really be deleted during lock???
 			return false;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_LOCK";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#refresh(Object)
 	 */
 	public static final CascadingAction REFRESH = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to refresh: {0}", entityName );
 			session.refresh( entityName, child, (Map) anything );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// refresh doesn't cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			return false;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_REFRESH";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#evict(Object)
 	 */
 	public static final CascadingAction EVICT = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to evict: {0}", entityName );
 			session.evict( child );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// evicts don't cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			return false;
 		}
 
 		@Override
 		public boolean performOnLazyProperty() {
 			return false;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_EVICT";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#saveOrUpdate(Object)
 	 */
 	public static final CascadingAction SAVE_UPDATE = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to save or update: {0}", entityName );
 			session.saveOrUpdate( entityName, child );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// saves / updates don't cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			// orphans should be deleted during save/update
 			return true;
 		}
 
 		@Override
 		public boolean performOnLazyProperty() {
 			return false;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_SAVE_UPDATE";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#merge(Object)
 	 */
 	public static final CascadingAction MERGE = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to merge: {0}", entityName );
 			session.merge( entityName, child, (Map) anything );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// merges don't cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			// orphans should not be deleted during merge??
 			return false;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_MERGE";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#persist(Object)
 	 */
 	public static final CascadingAction PERSIST = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to persist: {0}" + entityName );
 			session.persist( entityName, child, (Map) anything );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// persists don't cascade to uninitialized collections
 			return getAllElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			return false;
 		}
 
 		@Override
 		public boolean performOnLazyProperty() {
 			return false;
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_PERSIST";
 		}
 	};
 
 	/**
 	 * Execute persist during flush time
 	 *
 	 * @see org.hibernate.Session#persist(Object)
 	 */
 	public static final CascadingAction PERSIST_ON_FLUSH = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to persist on flush: {0}", entityName );
 			session.persistOnFlush( entityName, child, (Map) anything );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// persists don't cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			return true;
 		}
 
 		@Override
 		public boolean requiresNoCascadeChecking() {
 			return true;
 		}
 
 		@Override
 		public void noCascade(
 				EventSource session,
 				Object parent,
 				EntityPersister persister,
 				Type propertyType,
 				int propertyIndex) {
 			if ( propertyType.isEntityType() ) {
 				Object child = persister.getPropertyValue( parent, propertyIndex );
 				String childEntityName = ((EntityType) propertyType).getAssociatedEntityName( session.getFactory() );
 
 				if ( child != null
 						&& !isInManagedState( child, session )
 						&& !(child instanceof HibernateProxy) //a proxy cannot be transient and it breaks ForeignKeys.isTransient
 						&& ForeignKeys.isTransient( childEntityName, child, null, session ) ) {
 					String parentEntiytName = persister.getEntityName();
 					String propertyName = persister.getPropertyNames()[propertyIndex];
 					throw new TransientPropertyValueException(
 							"object references an unsaved transient instance - save the transient instance beforeQuery flushing",
 							childEntityName,
 							parentEntiytName,
 							propertyName
 					);
 
 				}
 			}
 		}
 
 		@Override
 		public boolean performOnLazyProperty() {
 			return false;
 		}
 
 		private boolean isInManagedState(Object child, EventSource session) {
 			EntityEntry entry = session.getPersistenceContext().getEntry( child );
 			return entry != null &&
 					(
 							entry.getStatus() == Status.MANAGED ||
 									entry.getStatus() == Status.READ_ONLY ||
 									entry.getStatus() == Status.SAVING
 					);
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_PERSIST_ON_FLUSH";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#replicate
 	 */
 	public static final CascadingAction REPLICATE = new BaseCascadingAction() {
 		@Override
 		public void cascade(
 				EventSource session,
 				Object child,
 				String entityName,
 				Object anything,
 				boolean isCascadeDeleteEnabled)
 				throws HibernateException {
 			LOG.tracev( "Cascading to replicate: {0}", entityName );
 			session.replicate( entityName, child, (ReplicationMode) anything );
 		}
 
 		@Override
 		public Iterator getCascadableChildrenIterator(
 				EventSource session,
 				CollectionType collectionType,
 				Object collection) {
 			// replicate does cascade to uninitialized collections
 			return getLoadedElementsIterator( session, collectionType, collection );
 		}
 
 		@Override
 		public boolean deleteOrphans() {
 			return false; //I suppose?
 		}
 
 		@Override
 		public String toString() {
 			return "ACTION_REPLICATE";
 		}
 	};
 
 	public abstract static class BaseCascadingAction implements CascadingAction {
 		@Override
 		public boolean requiresNoCascadeChecking() {
 			return false;
 		}
 
 		@Override
 		public void noCascade(EventSource session, Object parent, EntityPersister persister, Type propertyType, int propertyIndex) {
 		}
 
 		@Override
 		public boolean performOnLazyProperty() {
 			return true;
 		}
 	}
 
 	/**
 	 * Given a collection, get an iterator of all its children, loading them
 	 * from the database if necessary.
 	 *
 	 * @param session The session within which the cascade is occuring.
 	 * @param collectionType The mapping type of the collection.
 	 * @param collection The collection instance.
 	 *
 	 * @return The children iterator.
 	 */
 	private static Iterator getAllElementsIterator(
 			EventSource session,
 			CollectionType collectionType,
 			Object collection) {
 		return collectionType.getElementsIterator( collection, session );
 	}
 
 	/**
 	 * Iterate just the elements of the collection that are already there. Don't load
 	 * any new elements from the database.
 	 */
 	public static Iterator getLoadedElementsIterator(
 			SharedSessionContractImplementor session,
 			CollectionType collectionType,
 			Object collection) {
 		if ( collectionIsInitialized( collection ) ) {
 			// handles arrays and newly instantiated collections
 			return collectionType.getElementsIterator( collection, session );
 		}
 		else {
 			// does not handle arrays (thats ok, cos they can't be lazy)
 			// or newly instantiated collections, so we can do the cast
 			return ((PersistentCollection) collection).queuedAdditionIterator();
 		}
 	}
 
 	private static boolean collectionIsInitialized(Object collection) {
 		return !(collection instanceof PersistentCollection) || ((PersistentCollection) collection).wasInitialized();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java b/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java
index 024c270fc2..96c537d12a 100644
--- a/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java
+++ b/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java
@@ -1,127 +1,130 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import static org.hibernate.annotations.QueryHints.CACHEABLE;
 import static org.hibernate.annotations.QueryHints.CACHE_MODE;
 import static org.hibernate.annotations.QueryHints.CACHE_REGION;
 import static org.hibernate.annotations.QueryHints.COMMENT;
 import static org.hibernate.annotations.QueryHints.FETCHGRAPH;
 import static org.hibernate.annotations.QueryHints.FETCH_SIZE;
 import static org.hibernate.annotations.QueryHints.FLUSH_MODE;
+import static org.hibernate.annotations.QueryHints.FOLLOW_ON_LOCKING;
 import static org.hibernate.annotations.QueryHints.LOADGRAPH;
 import static org.hibernate.annotations.QueryHints.NATIVE_LOCKMODE;
 import static org.hibernate.annotations.QueryHints.READ_ONLY;
 import static org.hibernate.annotations.QueryHints.TIMEOUT_HIBERNATE;
 import static org.hibernate.annotations.QueryHints.TIMEOUT_JPA;
 
 /**
  * Defines the supported JPA query hints
  *
  * @author Steve Ebersole
  */
 public class QueryHints {
 	/**
 	 * The hint key for specifying a query timeout per Hibernate O/RM, which defines the timeout in seconds.
 	 *
 	 * @deprecated use {@link #SPEC_HINT_TIMEOUT} instead
 	 */
 	@Deprecated
 	public static final String HINT_TIMEOUT = TIMEOUT_HIBERNATE;
 
 	/**
 	 * The hint key for specifying a query timeout per JPA, which defines the timeout in milliseconds
 	 */
 	public static final String SPEC_HINT_TIMEOUT = TIMEOUT_JPA;
 
 	/**
 	 * The hint key for specifying a comment which is to be embedded into the SQL sent to the database.
 	 */
 	public static final String HINT_COMMENT = COMMENT;
 
 	/**
 	 * The hint key for specifying a JDBC fetch size, used when executing the resulting SQL.
 	 */
 	public static final String HINT_FETCH_SIZE = FETCH_SIZE;
 
 	/**
 	 * The hint key for specifying whether the query results should be cached for the next (cached) execution of the
 	 * "same query".
 	 */
 	public static final String HINT_CACHEABLE = CACHEABLE;
 
 	/**
 	 * The hint key for specifying the name of the cache region (within Hibernate's query result cache region)
 	 * to use for storing the query results.
 	 */
 	public static final String HINT_CACHE_REGION = CACHE_REGION;
 
 	/**
 	 * The hint key for specifying that objects loaded into the persistence context as a result of this query execution
 	 * should be associated with the persistence context as read-only.
 	 */
 	public static final String HINT_READONLY = READ_ONLY;
 
 	/**
 	 * The hint key for specifying the cache mode ({@link org.hibernate.CacheMode}) to be in effect for the
 	 * execution of the hinted query.
 	 */
 	public static final String HINT_CACHE_MODE = CACHE_MODE;
 
 	/**
 	 * The hint key for specifying the flush mode ({@link org.hibernate.FlushMode}) to be in effect for the
 	 * execution of the hinted query.
 	 */
 	public static final String HINT_FLUSH_MODE = FLUSH_MODE;
 
 	public static final String HINT_NATIVE_LOCKMODE = NATIVE_LOCKMODE;
 	
 	/**
 	 * Hint providing a "fetchgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).
 	 * 
 	 * Note: Currently, attributes that are not specified are treated as FetchType.LAZY or FetchType.EAGER depending
 	 * on the attribute's definition in metadata, rather than forcing FetchType.LAZY.
 	 */
 	public static final String HINT_FETCHGRAPH = FETCHGRAPH;
 	
 	/**
 	 * Hint providing a "loadgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).  Attributes that are not specified are treated as
 	 * FetchType.LAZY or FetchType.EAGER depending on the attribute's definition in metadata
 	 */
 	public static final String HINT_LOADGRAPH = LOADGRAPH;
 
+	public static final String HINT_FOLLOW_ON_LOCKING = FOLLOW_ON_LOCKING;
+
 	private static final Set<String> HINTS = buildHintsSet();
 
 	private static Set<String> buildHintsSet() {
 		HashSet<String> hints = new HashSet<String>();
 		hints.add( HINT_TIMEOUT );
 		hints.add( SPEC_HINT_TIMEOUT );
 		hints.add( HINT_COMMENT );
 		hints.add( HINT_FETCH_SIZE );
 		hints.add( HINT_CACHE_REGION );
 		hints.add( HINT_CACHEABLE );
 		hints.add( HINT_READONLY );
 		hints.add( HINT_CACHE_MODE );
 		hints.add( HINT_FLUSH_MODE );
 		hints.add( HINT_NATIVE_LOCKMODE );
 		hints.add( HINT_FETCHGRAPH );
 		hints.add( HINT_LOADGRAPH );
 		return java.util.Collections.unmodifiableSet( hints );
 	}
 
 	public static Set<String> getDefinedHints() {
 		return HINTS;
 	}
 
 	protected QueryHints() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 42f249efa5..9ac4ec113f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1251 +1,1252 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Abstract superclass of object loading (and querying) strategies. This class implements
  * useful common functionality that concrete loaders delegate to. It is not intended that this
  * functionality would be directly accessed by client code. (Hence, all methods of this class
  * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
  * <tt>Loadable</tt> interface, which is the contract between this class and
  * <tt>EntityPersister</tt>s that may be loaded by it.<br>
  * <br>
  * The present implementation is able to load any number of columns of entities and at most
  * one collection role per query.
  *
  * @author Gavin King
  * @see org.hibernate.persister.entity.Loadable
  */
 public abstract class Loader {
 	protected static final CoreMessageLogger LOG = CoreLogging.messageLogger( Loader.class );
 	protected static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
 
 	private final SessionFactoryImplementor factory;
 	private volatile ColumnNameCache columnNameCache;
 
 	private final boolean referenceCachingEnabled;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 		this.referenceCachingEnabled = factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled();
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	public abstract String getSQLString();
 
 	/**
 	 * An array of persisters of entity classes contained in each row of results;
 	 * implemented by all subclasses
 	 *
 	 * @return The entity persisters.
 	 */
 	protected abstract Loadable[] getEntityPersisters();
 
 	/**
 	 * An array indicating whether the entities have eager property fetching
 	 * enabled.
 	 *
 	 * @return Eager property fetching indicators.
 	 */
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return null;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner").  The
 	 * indexes contained here are relative to the result of
 	 * {@link #getEntityPersisters}.
 	 *
 	 * @return The owner indicators (see discussion above).
 	 */
 	protected int[] getOwners() {
 		return null;
 	}
 
 	/**
 	 * An array of the owner types corresponding to the {@link #getOwners()}
 	 * returns.  Indices indicating no owner would be null here.
 	 *
 	 * @return The types for the owners.
 	 */
 	protected EntityType[] getOwnerAssociationTypes() {
 		return null;
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only
 	 * collection loaders return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return null;
 	}
 
 	/**
 	 * Get the index of the entity that owns the collection, or -1
 	 * if there is no owner in the query results (ie. in the case of a
 	 * collection initializer) or no collection.
 	 */
 	protected int[] getCollectionOwners() {
 		return null;
 	}
 
 	protected int[][] getCompositeKeyManyToOneTargetIndices() {
 		return null;
 	}
 
 	/**
 	 * What lock options does this load entities with?
 	 *
 	 * @param lockOptions a collection of lock options specified dynamically via the Query interface
 	 */
 	//protected abstract LockOptions[] getLockOptions(Map lockOptions);
 	protected abstract LockMode[] getLockModes(LockOptions lockOptions);
 
 	/**
 	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
 	 * empty superclass implementation merely returns its first
 	 * argument.
 	 */
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		return sql;
 	}
 
 	/**
 	 * Does this query return objects that might be already cached
 	 * by the session, whose lock mode may need upgrading
 	 */
 	protected boolean upgradeLocks() {
 		return false;
 	}
 
 	/**
 	 * Return false is this loader is a batch entity loader
 	 */
 	protected boolean isSingleRowLoader() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL table aliases of entities whose
 	 * associations are subselect-loadable, returning
 	 * null if this loader does not support subselect
 	 * loading
 	 */
 	protected String[] getAliases() {
 		return null;
 	}
 
 	/**
 	 * Modify the SQL, adding lock hints and comments, if necessary
 	 */
 	protected String preprocessSQL(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		sql = applyLocks( sql, parameters, dialect, afterLoadActions );
 
 		// Keep this here, rather than moving to Select.  Some Dialects may need the hint to be appended to the very
 		// end or beginning of the finalized SQL statement, so wait until everything is processed.
 		if ( parameters.getQueryHints() != null && parameters.getQueryHints().size() > 0 ) {
 			sql = dialect.getQueryHintString( sql, parameters.getQueryHints() );
 		}
 
 		return getFactory().getSessionFactoryOptions().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
-		if ( dialect.useFollowOnLocking() ) {
+		if ( ( parameters.getLockOptions().getFollowOnLocking() == null && dialect.useFollowOnLocking( parameters ) ) ||
+				( parameters.getLockOptions().getFollowOnLocking() != null && parameters.getLockOptions().getFollowOnLocking() ) ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				if ( lockOptions.getLockMode() != LockMode.NONE ) {
 					LOG.usingFollowOnLocking();
 				}
 				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
 				lockOptions.setScope( parameters.getLockOptions().getScope() );
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock(
 										persister.getEntityName(),
 										entity
 								);
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			if ( lockOptions.getLockMode() == LockMode.NONE && lockModeToUse == LockMode.NONE ) {
 				return lockModeToUse;
 			}
 			else {
 				LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 			}
 		}
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return "/* " + comment + " */ " + sql;
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	public List doQueryAndInitializeNonLazyCollections(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException, SQLException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 		if ( queryParameters.isReadOnlyInitialized() ) {
 			// The read-only/modifiable mode for the query was explicitly set.
 			// Temporarily set the default read-only/modifiable setting to the query's setting.
 			persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 		}
 		else {
 			// The read-only/modifiable setting for the query was not initialized.
 			// Use the default read-only/modifiable from the persistence context instead.
 			queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 		}
 		persistenceContext.beforeLoad();
 		List result;
 		try {
 			try {
 				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 *
 	 * @return The loaded "row".
 	 *
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 					resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
 					sqle,
 					"could not read next row of results",
 					getSQLString()
 			);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 				);
 				if ( !keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0]
 							)
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch (SQLException sqle) {
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
 					sqle,
 					"could not doAfterTransactionCompletion sequential read of results (forward)",
 					getSQLString()
 			);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 *
 	 * @return The loaded "row".
 	 *
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
 			// key value upon which to perform the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 			);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch (SQLException sqle) {
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
 					sqle,
 					"could not perform sequential read of results (forward)",
 					getSQLString()
 			);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 *
 	 * @return The loaded "row".
 	 *
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// afterQuery the last physical row for the current logical row;
 			// thus if we are afterQuery the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 				);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row afterQuery the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 				);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 					);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 				);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch (SQLException sqle) {
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
 					sqle,
 					"could not doAfterTransactionCompletion sequential read of results (forward)",
 					getSQLString()
 			);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SharedSessionContractImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey(
 					optionalId, session.getEntityPersister(
 							optionalEntityName,
 							optionalObject
 					)
 			);
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final LockMode[] lockModesArray,
 			final EntityKey optionalObjectKey,
 			final List hydratedObjects,
 			final EntityKey[] keys,
 			boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final LockMode[] lockModesArray,
 			final EntityKey optionalObjectKey,
 			final List hydratedObjects,
 			final EntityKey[] keys,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet(
 				persisters,
 				queryParameters,
 				resultSet,
 				session,
 				keys,
 				lockModesArray,
 				hydratedObjects
 		);
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 				resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation( entity );
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple(
 				getResultRow( row, resultSet, session ),
 				getResultRowAliases()
 		)
 				;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SharedSessionContractImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[entitySpan - 1] = session.generateEntityKey( optionalId, persisters[entitySpan - 1] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate(
 					resultSet,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null
 			);
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SharedSessionContractImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i = 0; i < collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners != null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[collectionOwners[i]] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 				);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet(
 					rs,
 					queryParameters,
 					session,
 					returnProxies,
 					forcedResultTransformer,
 					maxRows,
 					afterLoadActions
 			);
 		}
 		finally {
 			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SharedSessionContractImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 			if ( DEBUG_ENABLED ) {
 				LOG.debugf( "Result set row: %s", count );
 			}
 			Object result = getRowFromResultSet(
 					rs,
 					session,
 					queryParameters,
 					lockModesArray,
 					optionalObjectKey,
 					hydratedObjects,
 					keys,
 					returnProxies,
 					forcedResultTransformer
 			);
 			results.add( result );
 			if ( createSubselects ) {
 				subselectResultKeys.add( keys );
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for ( Loadable loadable : loadables ) {
 			if ( loadable.hasSubselectLoadableCollections() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private static Set[] transpose(List keys) {
 		Set[] result = new Set[( (EntityKey[]) keys.get( 0 ) ).length];
 		for ( int j = 0; j < result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( Object key : keys ) {
 				result[j].add( ( (EntityKey[]) key )[j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SharedSessionContractImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose( keys );
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final String subselectQueryString = SubselectFetch.createSubselectFetchQueryFragment( queryParameters );
 			for ( Object key : keys ) {
 				final EntityKey[] rowKeys = (EntityKey[]) key;
 				for ( int i = 0; i < rowKeys.length; i++ ) {
 
 					if ( rowKeys[i] != null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								subselectQueryString,
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 						);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters() != null ) {
 			final Map namedParameterLocMap = new HashMap();
 			for ( String name : queryParameters.getNamedParameters().keySet() ) {
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs( name )
 				);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SharedSessionContractImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SharedSessionContractImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( collectionPersister.isArray() ) {
 					//for arrays, we should end the collection load beforeQuery resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects != null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.initializeEntity( hydratedObject, readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( !collectionPersister.isArray() ) {
 					//for sets, we should end the collection load afterQuery resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
 				}
 			}
 		}
 
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur afterQuery
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException(
 									"Could not locate EntityEntry immediately afterQuery two-phase load"
 							);
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SharedSessionContractImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( (ResultSet) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 *
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately afterQuery being read from the ResultSet?
 	 *
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 *
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index 9479626213..9924898a85 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,294 +1,295 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader.criteria;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A <tt>Loader</tt> for <tt>Criteria</tt> queries. Note that criteria queries are
  * more like multi-object <tt>load()</tt>s than like HQL queries.
  *
  * @author Gavin King
  */
 public class CriteriaLoader extends OuterJoinLoader {
 
 	//TODO: this class depends directly upon CriteriaImpl, 
 	//      in the impl package ... add a CriteriaImplementor 
 	//      interface
 
 	//NOTE: unlike all other Loaders, this one is NOT
 	//      multithreaded, or cacheable!!
 
 	private final CriteriaQueryTranslator translator;
 	private final Set<Serializable> querySpaces;
 	private final Type[] resultTypes;
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final boolean[] includeInResultRow;
 	private final int resultRowLength;
 
 	public CriteriaLoader(
 			final OuterJoinLoadable persister,
 			final SessionFactoryImplementor factory,
 			final CriteriaImpl criteria,
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) throws HibernateException {
 		super( factory, loadQueryInfluencers );
 
 		translator = new CriteriaQueryTranslator(
 				factory,
 				criteria,
 				rootEntityName,
 				CriteriaQueryTranslator.ROOT_SQL_ALIAS
 		);
 
 		querySpaces = translator.getQuerySpaces();
 
 		CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister,
 				translator,
 				factory,
 				criteria,
 				rootEntityName,
 				loadQueryInfluencers
 		);
 
 		initFromWalker( walker );
 
 		userAliases = walker.getUserAliases();
 		resultTypes = walker.getResultTypes();
 		includeInResultRow = walker.includeInResultRow();
 		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
 		postInstantiate();
 
 	}
 
 	public ScrollableResultsImplementor scroll(SharedSessionContractImplementor session, ScrollMode scrollMode)
 			throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode( scrollMode );
 		return scroll( qp, resultTypes, null, session );
 	}
 
 	public List list(SharedSessionContractImplementor session)
 			throws HibernateException {
 		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 
 	@Override
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
 	@Override
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 
 	@Override
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	@Override
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session ),
 				getResultRowAliases()
 		);
 	}
 
 	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		final Object[] result;
 		if ( translator.hasProjection() ) {
 			Type[] types = translator.getProjectedTypes();
 			result = new Object[types.length];
 			String[] columnAliases = translator.getProjectedColumnAliases();
 			for ( int i = 0, pos = 0; i < result.length; i++ ) {
 				int numColumns = types[i].getColumnSpan( session.getFactory() );
 				if ( numColumns > 1 ) {
 					String[] typeColumnAliases = ArrayHelper.slice( columnAliases, pos, numColumns );
 					result[i] = types[i].nullSafeGet( rs, typeColumnAliases, session, null );
 				}
 				else {
 					result[i] = types[i].nullSafeGet( rs, columnAliases[pos], session, null );
 				}
 				pos += numColumns;
 			}
 		}
 		else {
 			result = toResultRow( row );
 		}
 		return result;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( resultRowLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[resultRowLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInResultRow[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		final LockOptions lockOptions = parameters.getLockOptions();
 
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && ( lockOptions.getAliasLockCount() == 0
 						|| ( lockOptions.getAliasLockCount() == 1 && lockOptions
 						.getAliasSpecificLockMode( "this_" ) == LockMode.NONE )
 				) ) ) {
 			return sql;
 		}
 
-		if ( dialect.useFollowOnLocking() ) {
-			final LockMode lockMode = determineFollowOnLockMode( lockOptions );
-			if ( lockMode != LockMode.UPGRADE_SKIPLOCKED ) {
+		if ( ( parameters.getLockOptions().getFollowOnLocking() == null && dialect.useFollowOnLocking( parameters ) ) ||
+			( parameters.getLockOptions().getFollowOnLocking() != null && parameters.getLockOptions().getFollowOnLocking() ) ) {
+            final LockMode lockMode = determineFollowOnLockMode( lockOptions );
+            if( lockMode != LockMode.UPGRADE_SKIPLOCKED ) {
 				// Dialect prefers to perform locking in a separate step
 				LOG.usingFollowOnLocking();
 
 				final LockOptions lockOptionsToUse = new LockOptions( lockMode );
 				lockOptionsToUse.setTimeOut( lockOptions.getTimeOut() );
 				lockOptionsToUse.setScope( lockOptions.getScope() );
 
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptionsToUse )
 										.lock( persister.getEntityName(), entity );
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return sql;
 			}
 		}
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
 		final Map<String,String[]> keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 		final String[] drivingSqlAliases = getAliases();
 		for ( int i = 0; i < drivingSqlAliases.length; i++ ) {
 			final LockMode lockMode = lockOptions.getAliasSpecificLockMode( drivingSqlAliases[i] );
 			if ( lockMode != null ) {
 				final Lockable drivingPersister = (Lockable) getEntityPersisters()[i];
 				final String rootSqlAlias = drivingPersister.getRootTableAlias( drivingSqlAliases[i] );
 				locks.setAliasSpecificLockMode( rootSqlAlias, lockMode );
 				if ( keyColumnNames != null ) {
 					keyColumnNames.put( rootSqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 				}
 			}
 		}
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 
 	@Override
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.getAliasLockCount() > 1 ) {
 			// > 1 here because criteria always uses alias map for the root lock mode (under 'this_')
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		final String[] entityAliases = getAliases();
 		if ( entityAliases == null ) {
 			return null;
 		}
 		final int size = entityAliases.length;
 		LockMode[] lockModesArray = new LockMode[size];
 		for ( int i = 0; i < size; i++ ) {
 			LockMode lockMode = lockOptions.getAliasSpecificLockMode( entityAliases[i] );
 			lockModesArray[i] = lockMode == null ? lockOptions.getLockMode() : lockMode;
 		}
 		return lockModesArray;
 	}
 
 	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	@Override
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 
 	@Override
 	protected String getQueryIdentifier() {
 		return "[CRITERIA] " + getSQLString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java b/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
index c145e4aa7d..366de4afb2 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
@@ -1,1524 +1,1539 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import java.io.Serializable;
 import java.time.Instant;
 import java.time.LocalDateTime;
 import java.time.OffsetDateTime;
 import java.time.ZonedDateTime;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;
 import java.util.Set;
 import java.util.Spliterator;
 import java.util.Spliterators;
 import java.util.stream.Stream;
 import java.util.stream.StreamSupport;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.NoResultException;
 import javax.persistence.Parameter;
 import javax.persistence.TemporalType;
 import javax.persistence.TransactionRequiredException;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.ScrollMode;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.engine.query.spi.EntityGraphQueryHint;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.spi.ExceptionConverter;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.hibernate.internal.EntityManagerMessageLogger;
 import org.hibernate.internal.HEMLogging;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jpa.QueryHints;
 import org.hibernate.jpa.TypedParameterValue;
 import org.hibernate.jpa.graph.internal.EntityGraphImpl;
 import org.hibernate.jpa.internal.util.CacheModeHelper;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
 import org.hibernate.property.access.spi.BuiltInPropertyAccessStrategies;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.PropertyAccess;
 import org.hibernate.query.ParameterMetadata;
 import org.hibernate.query.QueryParameter;
 import org.hibernate.query.spi.QueryImplementor;
 import org.hibernate.query.spi.QueryParameterBinding;
 import org.hibernate.query.spi.QueryParameterListBinding;
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 import static org.hibernate.LockOptions.WAIT_FOREVER;
 import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_SCOPE;
 import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_TIMEOUT;
 import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_RETRIEVE_MODE;
 import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_STORE_MODE;
 import static org.hibernate.jpa.AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE;
 import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
 import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
 import static org.hibernate.jpa.QueryHints.HINT_CACHE_REGION;
 import static org.hibernate.jpa.QueryHints.HINT_COMMENT;
 import static org.hibernate.jpa.QueryHints.HINT_FETCHGRAPH;
 import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;
 import static org.hibernate.jpa.QueryHints.HINT_FLUSH_MODE;
+import static org.hibernate.jpa.QueryHints.HINT_FOLLOW_ON_LOCKING;
 import static org.hibernate.jpa.QueryHints.HINT_LOADGRAPH;
 import static org.hibernate.jpa.QueryHints.HINT_READONLY;
 import static org.hibernate.jpa.QueryHints.HINT_TIMEOUT;
 import static org.hibernate.jpa.QueryHints.SPEC_HINT_TIMEOUT;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractProducedQuery<R> implements QueryImplementor<R> {
 	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( AbstractProducedQuery.class );
 
 	private final SharedSessionContractImplementor producer;
 	private final ParameterMetadata parameterMetadata;
 	private final QueryParameterBindingsImpl queryParameterBindings;
 
 	private FlushMode flushMode;
 	private CacheStoreMode cacheStoreMode;
 	private CacheRetrieveMode cacheRetrieveMode;
 	private boolean cacheable;
 	private String cacheRegion;
 	private Boolean readOnly;
 
 	private LockOptions lockOptions = new LockOptions();
 
 	private String comment;
 	private final List<String> dbHints = new ArrayList<>();
 
 	private ResultTransformer resultTransformer;
 	private RowSelection queryOptions = new RowSelection();
 
 	private EntityGraphQueryHint entityGraphQueryHint;
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	public AbstractProducedQuery(
 			SharedSessionContractImplementor producer,
 			ParameterMetadata parameterMetadata) {
 		this.producer = producer;
 		this.parameterMetadata = parameterMetadata;
 		this.queryParameterBindings = QueryParameterBindingsImpl.from( parameterMetadata, producer.getFactory() );
 	}
 
 	@Override
 	public SharedSessionContractImplementor getProducer() {
 		return producer;
 	}
 
 	@Override
 	public FlushMode getHibernateFlushMode() {
 		return flushMode;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setHibernateFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 
 	@Override
 	public QueryImplementor setFlushMode(FlushMode flushMode) {
 		return setHibernateFlushMode( flushMode );
 	}
 
 	@Override
 	public FlushModeType getFlushMode() {
 		return ( flushMode == null ?
 				getProducer().getFlushMode() :
 				FlushModeTypeHelper.getFlushModeType( flushMode )
 		);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setFlushMode(FlushModeType flushModeType) {
 		setHibernateFlushMode( FlushModeTypeHelper.getFlushMode( flushModeType ) );
 		return this;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return CacheModeHelper.interpretCacheMode( cacheStoreMode, cacheRetrieveMode );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setCacheMode(CacheMode cacheMode) {
 		this.cacheStoreMode = CacheModeHelper.interpretCacheStoreMode( cacheMode );
 		this.cacheRetrieveMode = CacheModeHelper.interpretCacheRetrieveMode( cacheMode );
 		return this;
 	}
 
 	@Override
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	@Override
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setCacheRegion(String cacheRegion) {
 		this.cacheRegion = cacheRegion;
 		return this;
 	}
 
 	@Override
 	public Integer getTimeout() {
 		return queryOptions.getTimeout();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setTimeout(int timeout) {
 		queryOptions.setTimeout( timeout );
 		return this;
 	}
 
 	@Override
 	public Integer getFetchSize() {
 		return queryOptions.getFetchSize();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setFetchSize(int fetchSize) {
 		queryOptions.setFetchSize( fetchSize );
 		return this;
 	}
 
 	@Override
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				producer.getPersistenceContext().isDefaultReadOnly() :
 				readOnly
 		);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setReadOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		return this;
 	}
 
 	@Override
 	public LockOptions getLockOptions() {
 		return lockOptions;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setLockOptions(LockOptions lockOptions) {
 		this.lockOptions.setLockMode( lockOptions.getLockMode() );
 		this.lockOptions.setScope( lockOptions.getScope() );
 		this.lockOptions.setTimeOut( lockOptions.getTimeOut() );
+		this.lockOptions.setFollowOnLocking( lockOptions.getFollowOnLocking() );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setLockMode(String alias, LockMode lockMode) {
 		lockOptions.setAliasSpecificLockMode( alias, lockMode );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setLockMode(LockModeType lockModeType) {
 		if ( !LockModeType.NONE.equals( lockModeType ) ) {
 			if ( !isSelect() ) {
 				throw new IllegalStateException( "Illegal attempt to set lock mode on a non-SELECT query" );
 			}
 		}
 		lockOptions.setLockMode( LockModeTypeHelper.getLockMode( lockModeType ) );
 		return this;
 	}
 
 	@Override
 	public String getComment() {
 		return comment;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor addQueryHint(String hint) {
 		this.dbHints.add( hint );
 		return this;
 	}
 
 	@Override
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	@Override
 	public String[] getNamedParameters() {
 		return ArrayHelper.toStringArray( getParameterMetadata().getNamedParameterNames() );
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, Instant value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, Instant value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value) {
 		locateBinding( parameter ).setBindValue( value );
 		return this;
 	}
 
 	@SuppressWarnings("unchecked")
 	private <P> QueryParameterBinding<P> locateBinding(Parameter<P> parameter) {
 		if ( parameter instanceof QueryParameter ) {
 			return queryParameterBindings.getBinding( (QueryParameter) parameter );
 		}
 		else if ( parameter.getName() != null ) {
 			return queryParameterBindings.getBinding( parameter.getName() );
 		}
 		else if ( parameter.getPosition() != null ) {
 			return queryParameterBindings.getBinding( parameter.getPosition() );
 		}
 
 		throw getExceptionConverter().convert(
 				new IllegalArgumentException( "Could not resolve binding for given parameter reference [" + parameter + "]" )
 		);
 	}
 
 	private  <P> QueryParameterBinding<P> locateBinding(QueryParameter<P> parameter) {
 		return queryParameterBindings.getBinding( parameter );
 	}
 
 	private <P> QueryParameterBinding<P> locateBinding(String name) {
 		return queryParameterBindings.getBinding( name );
 	}
 
 	private <P> QueryParameterBinding<P> locateBinding(int position) {
 		return queryParameterBindings.getBinding( position );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(Parameter<P> parameter, P value) {
 		if ( value instanceof TypedParameterValue ) {
 			setParameter( parameter, ( (TypedParameterValue) value ).getValue(), ( (TypedParameterValue) value ).getType() );
 		}
 		else if ( value instanceof Collection ) {
 			locateListBinding( parameter ).setBindValues( (Collection) value );
 		}
 		else {
 			locateBinding( parameter ).setBindValue( value );
 		}
 
 		return this;
 	}
 
 	@SuppressWarnings("unchecked")
 	private <P> void setParameter(Parameter<P> parameter, Object value, Type type) {
 		if ( parameter instanceof QueryParameter ) {
 			setParameter( (QueryParameter) parameter, value, type );
 		}
 		else if ( value == null ) {
 			locateBinding( parameter ).setBindValue( null, type );
 		}
 		else if ( value instanceof Collection ) {
 			locateListBinding( parameter ).setBindValues( (Collection) value, type );
 		}
 		else {
 			locateBinding( parameter ).setBindValue( (P) value, type );
 		}
 	}
 
 	private QueryParameterListBinding locateListBinding(Parameter parameter) {
 		if ( parameter instanceof QueryParameter ) {
 			return queryParameterBindings.getQueryParameterListBinding( (QueryParameter) parameter );
 		}
 		else {
 			return queryParameterBindings.getQueryParameterListBinding( parameter.getName() );
 		}
 	}
 
 	private QueryParameterListBinding locateListBinding(String name) {
 		return queryParameterBindings.getQueryParameterListBinding( name );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Object value) {
 		if ( value instanceof TypedParameterValue ) {
 			final TypedParameterValue  typedValueWrapper = (TypedParameterValue) value;
 			setParameter( name, typedValueWrapper.getValue(), typedValueWrapper.getType() );
 		}
 		else if ( value instanceof Collection ) {
 			setParameterList( name, (Collection) value );
 		}
 		else {
 			queryParameterBindings.getBinding( name ).setBindValue( value );
 		}
 
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Object value) {
 		if ( value instanceof TypedParameterValue ) {
 			final TypedParameterValue typedParameterValue = (TypedParameterValue) value;
 			setParameter( position, typedParameterValue.getValue(), typedParameterValue.getType() );
 		}
 		if ( value instanceof Collection ) {
 			setParameterList( Integer.toString( position ), (Collection) value );
 		}
 		else {
 			queryParameterBindings.getBinding( position ).setBindValue( value );
 		}
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value, Type type) {
 		queryParameterBindings.getBinding( parameter ).setBindValue( value, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Object value, Type type) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Object value, Type type) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( parameter ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor<R> setParameterList(QueryParameter<P> parameter, Collection<P> values) {
 		queryParameterBindings.getQueryParameterListBinding( parameter ).setBindValues( values );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Collection values) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( values );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Collection values, Type type) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( values, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Object[] values, Type type) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( Arrays.asList( values ), type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Object[] values) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( Arrays.asList( values ) );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( (QueryParameter) param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( (QueryParameter) param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public Set<Parameter<?>> getParameters() {
 		return parameterMetadata.collectAllParametersJpa();
 	}
 
 	@Override
 	public Parameter<?> getParameter(String name) {
 		try {
 			return parameterMetadata.getQueryParameter( name );
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> Parameter<T> getParameter(String name, Class<T> type) {
 		try {
 			final QueryParameter parameter = parameterMetadata.getQueryParameter( name );
 			if ( !parameter.getParameterType().isAssignableFrom( type ) ) {
 				throw new IllegalArgumentException(
 						"The type [" + parameter.getParameterType().getName() +
 								"] associated with the parameter corresponding to name [" + name +
 								"] is not assignable to requested Java type [" + type.getName() + "]"
 				);
 			}
 			return parameter;
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	public Parameter<?> getParameter(int position) {
 		// It is important to understand that there are 2 completely distinct conceptualization of
 		// "positional parameters" in play here:
 		//		1) The legacy Hibernate concept is akin to JDBC PreparedStatement parameters.  Very limited and
 		//			deprecated since 5.x.  These are numbered starting from 0 and kept in the
 		//			ParameterMetadata positional-parameter array keyed by this zero-based position
 		//		2) JPA's definition is really just a named parameter, but expected to explicitly be
 		//			sequential intergers starting from 0 (ZERO); they can repeat.
 		//
 		// It is considered illegal to mix positional-parameter with named parameters of any kind.  So therefore.
 		// if ParameterMetadata reports that it has any positional-parameters it is talking about the
 		// legacy Hibernate concept.
 		// lookup jpa-based positional parameters first by name.
 		try {
 			if ( parameterMetadata.getPositionalParameterCount() == 0 ) {
 				return parameterMetadata.getQueryParameter( Integer.toString( position ) );
 			}
 			// fallback to oridinal lookup
 			return parameterMetadata.getQueryParameter( position );
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> Parameter<T> getParameter(int position, Class<T> type) {
 		try {
 			final QueryParameter parameter = parameterMetadata.getQueryParameter( position );
 			if ( !parameter.getParameterType().isAssignableFrom( type ) ) {
 				throw new IllegalArgumentException(
 						"The type [" + parameter.getParameterType().getName() +
 								"] associated with the parameter corresponding to position [" + position +
 								"] is not assignable to requested Java type [" + type.getName() + "]"
 				);
 			}
 			return parameter;
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	public boolean isBound(Parameter<?> parameter) {
 		return queryParameterBindings.isBound( (QueryParameter) parameter );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T getParameterValue(Parameter<T> parameter) {
 		return (T) queryParameterBindings.getBinding( (QueryParameter) parameter ).getBindValue();
 	}
 
 	@Override
 	public Object getParameterValue(String name) {
 		return queryParameterBindings.getBinding( name ).getBindValue();
 	}
 
 	@Override
 	public Object getParameterValue(int position) {
 		return queryParameterBindings.getBinding( position ).getBindValue();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setProperties(Object bean) {
 		Class clazz = bean.getClass();
 		String[] params = getNamedParameters();
 		for ( String namedParam : params ) {
 			try {
 				final PropertyAccess propertyAccess = BuiltInPropertyAccessStrategies.BASIC.getStrategy().buildPropertyAccess(
 						clazz,
 						namedParam
 				);
 				final Getter getter = propertyAccess.getGetter();
 				final Class retType = getter.getReturnType();
 				final Object object = getter.get( bean );
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, (Collection) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( namedParam, (Object[]) object );
 				}
 				else {
 					Type type = determineType( namedParam, retType );
 					setParameter( namedParam, object, type );
 				}
 			}
 			catch (PropertyNotFoundException pnfe) {
 				// ignore
 			}
 		}
 		return this;
 	}
 
 	protected Type determineType(String namedParam, Class retType) {
 		Type type = queryParameterBindings.getBinding( namedParam ).getBindType();
 		if ( type == null ) {
 			type = parameterMetadata.getQueryParameter( namedParam ).getType();
 		}
 		if ( type == null ) {
 			type = getProducer().getFactory().resolveParameterBindType( retType );
 		}
 		return type;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setProperties(Map map) {
 		final String[] namedParameterNames = getNamedParameters();
 		for ( String paramName : namedParameterNames ) {
 			final Object object = map.get( paramName );
 			if ( object == null ) {
 				if ( map.containsKey( paramName ) ) {
 					setParameter( paramName, null, determineType( paramName, null ) );
 				}
 			}
 			else {
 				Class retType = object.getClass();
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( paramName, (Collection) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( paramName, (Object[]) object );
 				}
 				else {
 					setParameter( paramName, object, determineType( paramName, retType ) );
 				}
 			}
 		}
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setResultTransformer(ResultTransformer transformer) {
 		this.resultTransformer = transformer;
 		return this;
 	}
 
 	@Override
 	public RowSelection getQueryOptions() {
 		return queryOptions;
 	}
 
 	@Override
 	public int getMaxResults() {
 		// to be JPA compliant this method returns an int - specifically the "magic number" Integer.MAX_VALUE defined by the spec.
 		// For access to the Integer (for checking), use #getQueryOptions#getMaxRows instead
 		return queryOptions.getMaxRows() == null ? Integer.MAX_VALUE : queryOptions.getMaxRows();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setMaxResults(int maxResult) {
 		if ( maxResult <= 0 ) {
 			// treat zero and negatives specially as meaning no limit...
 			queryOptions.setMaxRows( null );
 		}
 		else {
 			queryOptions.setMaxRows( maxResult );
 		}
 		return this;
 	}
 
 	@Override
 	public int getFirstResult() {
 		// to be JPA compliant this method returns an int - specifically the "magic number" 0 (ZERO) defined by the spec.
 		// For access to the Integer (for checking), use #getQueryOptions#getFirstRow instead
 		return queryOptions.getFirstRow() == null ? 0 : queryOptions.getFirstRow();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setFirstResult(int startPosition) {
 		queryOptions.setFirstRow( startPosition );
 		return this;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public Set<String> getSupportedHints() {
 		return QueryHints.getDefinedHints();
 	}
 
 	@Override
 	public Map<String, Object> getHints() {
 		// Technically this should rollback, but that's insane :)
 		// If the TCK ever adds a check for this, we may need to change this behavior
 		getProducer().checkOpen( false );
 
 		final Map<String,Object> hints = new HashMap<>();
 		collectBaselineHints( hints );
 		collectHints( hints );
 		return hints;
 	}
 
 	protected void collectBaselineHints(Map<String, Object> hints) {
 		// nothing to do in this form
 	}
 
 	protected void collectHints(Map<String, Object> hints) {
 		if ( getQueryOptions().getTimeout() != null ) {
 			hints.put( HINT_TIMEOUT, getQueryOptions().getTimeout() );
 			hints.put( SPEC_HINT_TIMEOUT, getQueryOptions().getTimeout() * 1000 );
 		}
 
 		if ( getLockOptions().getTimeOut() != WAIT_FOREVER ) {
 			hints.put( JPA_LOCK_TIMEOUT, getLockOptions().getTimeOut() );
 		}
 
 		if ( getLockOptions().getScope() ) {
 			hints.put( JPA_LOCK_SCOPE, getLockOptions().getScope() );
 		}
 
 		if ( getLockOptions().hasAliasSpecificLockModes() && canApplyAliasSpecificLockModeHints() ) {
 			for ( Map.Entry<String, LockMode> entry : getLockOptions().getAliasSpecificLocks() ) {
 				hints.put(
 						ALIAS_SPECIFIC_LOCK_MODE + '.' + entry.getKey(),
 						entry.getValue().name()
 				);
 			}
 		}
 
 		putIfNotNull( hints, HINT_COMMENT, getComment() );
 		putIfNotNull( hints, HINT_FETCH_SIZE, getQueryOptions().getFetchSize() );
 		putIfNotNull( hints, HINT_FLUSH_MODE, getHibernateFlushMode() );
 
 		if ( cacheStoreMode != null || cacheRetrieveMode != null ) {
 			putIfNotNull( hints, HINT_CACHE_MODE, CacheModeHelper.interpretCacheMode( cacheStoreMode, cacheRetrieveMode ) );
 			putIfNotNull( hints, JPA_SHARED_CACHE_RETRIEVE_MODE, cacheRetrieveMode );
 			putIfNotNull( hints, JPA_SHARED_CACHE_STORE_MODE, cacheStoreMode );
 		}
 
 		if ( isCacheable() ) {
 			hints.put( HINT_CACHEABLE, true );
 			putIfNotNull( hints, HINT_CACHE_REGION, getCacheRegion() );
 		}
 
 		if ( isReadOnly() ) {
 			hints.put( HINT_READONLY, true );
 		}
 
 		if ( entityGraphQueryHint != null ) {
 			hints.put( entityGraphQueryHint.getHintName(), entityGraphQueryHint.getOriginEntityGraph() );
 		}
 	}
 
 	protected void putIfNotNull(Map<String, Object> hints, String hintName, Enum hintValue) {
 		// centralized spot to handle the decision whether to put enums directly into the hints map
 		// or whether to put the enum name
 		if ( hintValue != null ) {
 			hints.put( hintName, hintValue );
 //			hints.put( hintName, hintValue.name() );
 		}
 	}
 
 	protected void putIfNotNull(Map<String, Object> hints, String hintName, Object hintValue) {
 		if ( hintValue != null ) {
 			hints.put( hintName, hintValue );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setHint(String hintName, Object value) {
 		getProducer().checkOpen( true );
 		boolean applied = false;
 		try {
 			if ( HINT_TIMEOUT.equals( hintName ) ) {
 				applied = applyTimeoutHint( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( SPEC_HINT_TIMEOUT.equals( hintName ) ) {
 				// convert milliseconds to seconds
 				int timeout = (int)Math.round( ConfigurationHelper.getInteger( value ).doubleValue() / 1000.0 );
 				applied = applyTimeoutHint( timeout );
 			}
 			else if ( JPA_LOCK_TIMEOUT.equals( hintName ) ) {
 				applied = applyLockTimeoutHint( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( HINT_COMMENT.equals( hintName ) ) {
 				applied = applyCommentHint( (String) value );
 			}
 			else if ( HINT_FETCH_SIZE.equals( hintName ) ) {
 				applied = applyFetchSizeHint( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( HINT_CACHEABLE.equals( hintName ) ) {
 				applied = applyCacheableHint( ConfigurationHelper.getBoolean( value ) );
 			}
 			else if ( HINT_CACHE_REGION.equals( hintName ) ) {
 				applied = applyCacheRegionHint( (String) value );
 			}
 			else if ( HINT_READONLY.equals( hintName ) ) {
 				applied = applyReadOnlyHint( ConfigurationHelper.getBoolean( value ) );
 			}
 			else if ( HINT_FLUSH_MODE.equals( hintName ) ) {
 				applied = applyFlushModeHint( ConfigurationHelper.getFlushMode( value ) );
 			}
 			else if ( HINT_CACHE_MODE.equals( hintName ) ) {
 				applied = applyCacheModeHint( ConfigurationHelper.getCacheMode( value ) );
 			}
 			else if ( JPA_SHARED_CACHE_RETRIEVE_MODE.equals( hintName ) ) {
 				final CacheRetrieveMode retrieveMode = value != null ? CacheRetrieveMode.valueOf( value.toString() ) : null;
 				applied = applyJpaCacheRetrieveMode( retrieveMode );
 			}
 			else if ( JPA_SHARED_CACHE_STORE_MODE.equals( hintName ) ) {
 				final CacheStoreMode storeMode = value != null ? CacheStoreMode.valueOf( value.toString() ) : null;
 				applied = applyJpaCacheStoreMode( storeMode );
 			}
 			else if ( QueryHints.HINT_NATIVE_LOCKMODE.equals( hintName ) ) {
 				applied = applyNativeQueryLockMode( value );
 			}
 			else if ( hintName.startsWith( ALIAS_SPECIFIC_LOCK_MODE ) ) {
 				if ( canApplyAliasSpecificLockModeHints() ) {
 					// extract the alias
 					final String alias = hintName.substring( ALIAS_SPECIFIC_LOCK_MODE.length() + 1 );
 					// determine the LockMode
 					try {
 						final LockMode lockMode = LockModeTypeHelper.interpretLockMode( value );
 						applyAliasSpecificLockModeHint( alias, lockMode );
 					}
 					catch ( Exception e ) {
 						log.unableToDetermineLockModeValue( hintName, value );
 						applied = false;
 					}
 				}
 				else {
 					applied = false;
 				}
 			}
 			else if ( HINT_FETCHGRAPH.equals( hintName ) || HINT_LOADGRAPH.equals( hintName ) ) {
 				if (value instanceof EntityGraphImpl ) {
 					applyEntityGraphQueryHint( new EntityGraphQueryHint( hintName, (EntityGraphImpl) value ) );
 				}
 				else {
 					log.warnf( "The %s hint was set, but the value was not an EntityGraph!", hintName );
 				}
 				applied = true;
 			}
+			else if ( HINT_FOLLOW_ON_LOCKING.equals( hintName ) ) {
+				applied = applyFollowOnLockingHint( ConfigurationHelper.getBoolean( value ) );
+			}
 			else {
 				log.ignoringUnrecognizedQueryHint( hintName );
 			}
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( "Value for hint" );
 		}
 
 		if ( !applied ) {
 			log.debugf( "Skipping unsupported query hint [%s]", hintName );
 		}
 
 		return this;
 	}
 
 	protected boolean applyJpaCacheRetrieveMode(CacheRetrieveMode mode) {
 		this.cacheRetrieveMode = mode;
 		return true;
 	}
 
 	protected boolean applyJpaCacheStoreMode(CacheStoreMode storeMode) {
 		this.cacheStoreMode = storeMode;
 		return true;
 	}
 
 	protected boolean applyNativeQueryLockMode(Object value) {
 		if ( !isNativeQuery() ) {
 			throw new IllegalStateException(
 					"Illegal attempt to set lock mode on non-native query via hint; use Query#setLockMode instead"
 			);
 		}
 
 		return false;
 	}
 
 	/**
 	 * Apply the query timeout hint.
 	 *
 	 * @param timeout The timeout (in seconds!) specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyTimeoutHint(int timeout) {
 		setTimeout( timeout );
 		return true;
 	}
 
 	/**
 	 * Apply the lock timeout (in seconds!) hint
 	 *
 	 * @param timeout The timeout (in seconds!) specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyLockTimeoutHint(int timeout) {
 		getLockOptions().setTimeOut( timeout );
 		return true;
 	}
 
 	/**
 	 * Apply the comment hint.
 	 *
 	 * @param comment The comment specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCommentHint(String comment) {
 		setComment( comment );
 		return true;
 	}
 
 	/**
 	 * Apply the fetch size hint
 	 *
 	 * @param fetchSize The fetch size specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyFetchSizeHint(int fetchSize) {
 		setFetchSize( fetchSize );
 		return true;
 	}
 
 	/**
 	 * Apply the cacheable (true/false) hint.
 	 *
 	 * @param isCacheable The value specified as hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCacheableHint(boolean isCacheable) {
 		setCacheable( isCacheable );
 		return true;
 	}
 
 	/**
 	 * Apply the cache region hint
 	 *
 	 * @param regionName The name of the cache region specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCacheRegionHint(String regionName) {
 		setCacheRegion( regionName );
 		return true;
 	}
 
 	/**
 	 * Apply the read-only (true/false) hint.
 	 *
 	 * @param isReadOnly The value specified as hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyReadOnlyHint(boolean isReadOnly) {
 		setReadOnly( isReadOnly );
 		return true;
 	}
 
 	/**
 	 * Apply the CacheMode hint.
 	 *
 	 * @param cacheMode The CacheMode value specified as a hint.
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCacheModeHint(CacheMode cacheMode) {
 		setCacheMode( cacheMode );
 		return true;
 	}
 
 	/**
 	 * Apply the FlushMode hint.
 	 *
 	 * @param flushMode The FlushMode value specified as hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyFlushModeHint(FlushMode flushMode) {
 		setFlushMode( flushMode );
 		return true;
 	}
 
 	/**
 	 * Can alias-specific lock modes be applied?
 	 *
 	 * @return {@code true} indicates they can be applied, {@code false} otherwise.
 	 */
 	protected boolean canApplyAliasSpecificLockModeHints() {
 		// only procedure/function calls cannot i believe
 		return true;
 	}
 
 	protected boolean applyLockModeTypeHint(LockModeType lockModeType) {
 		getLockOptions().setLockMode( LockModeTypeHelper.getLockMode( lockModeType ) );
 		return true;
 	}
 
 	protected boolean applyHibernateLockModeHint(LockMode lockMode) {
 		getLockOptions().setLockMode( lockMode );
 		return true;
 	}
 
 	/**
 	 * Apply the alias specific lock modes.  Assumes {@link #canApplyAliasSpecificLockModeHints()} has already been
 	 * called and returned {@code true}.
 	 *
 	 * @param alias The alias to apply the 'lockMode' to.
 	 * @param lockMode The LockMode to apply.
 	 */
 	protected void applyAliasSpecificLockModeHint(String alias, LockMode lockMode) {
 		getLockOptions().setAliasSpecificLockMode( alias, lockMode );
 	}
 
 	/**
 	 * Used from HEM code as a (hopefully temporary) means to apply a custom query plan
 	 * in regards to a JPA entity graph.
 	 *
 	 * @param hint The entity graph hint object
 	 */
 	protected void applyEntityGraphQueryHint(EntityGraphQueryHint hint) {
 		this.entityGraphQueryHint = hint;
 	}
 
 	/**
+	 * Apply the follow-on-locking hint.
+	 *
+	 * @param followOnLocking The follow-on-locking strategy.
+	 */
+	protected boolean applyFollowOnLockingHint(Boolean followOnLocking) {
+		getLockOptions().setFollowOnLocking( followOnLocking );
+		return true;
+	}
+
+	/**
 	 * Is the query represented here a native (SQL) query?
 	 *
 	 * @return {@code true} if it is a native query; {@code false} otherwise
 	 */
 	protected abstract boolean isNativeQuery();
 
 	@Override
 	public LockModeType getLockMode() {
 		return LockModeTypeHelper.getLockModeType( lockOptions.getLockMode() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T unwrap(Class<T> cls) {
 		if ( cls.isInstance( getProducer() ) ) {
 			return (T) getProducer();
 		}
 		if ( cls.isInstance( getParameterMetadata() ) ) {
 			return (T) getParameterMetadata();
 		}
 		if ( cls.isInstance( queryParameterBindings ) ) {
 			return (T) queryParameterBindings;
 		}
 		if ( cls.isInstance( this ) ) {
 			return (T) this;
 		}
 
 		throw new HibernateException( "Could not unwrap this [" + toString() + "] as requested Java type [" + cls.getName() + "]" );
 //		throw new IllegalArgumentException( "Could not unwrap this [" + toString() + "] as requested Java type [" + cls.getName() + "]" );
 	}
 
 	public QueryParameters getQueryParameters() {
 		final HQLQueryPlan entityGraphHintedQueryPlan;
 		if ( entityGraphQueryHint == null) {
 			entityGraphHintedQueryPlan = null;
 		}
 		else {
 			queryParameterBindings.verifyParametersBound( false );
 
 			// todo : ideally we'd update the instance state related to queryString but that is final atm
 
 			final String expandedQuery = queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() );
 			entityGraphHintedQueryPlan = new HQLQueryPlan(
 					expandedQuery,
 					false,
 					getProducer().getLoadQueryInfluencers().getEnabledFilters(),
 					getProducer().getFactory(),
 					entityGraphQueryHint
 			);
 		}
 
 		QueryParameters queryParameters = new QueryParameters(
 				getPositionalParameterTypes(),
 				getPositionalParameterValues(),
 				getNamedParameterMap(),
 				getLockOptions(),
 				queryOptions,
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
 				dbHints,
 				null,
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 		queryParameters.setQueryPlan( entityGraphHintedQueryPlan );
 		return queryParameters;
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Type[] getPositionalParameterTypes() {
 		return queryParameterBindings.collectPositionalBindTypes();
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Object[] getPositionalParameterValues() {
 		return queryParameterBindings.collectPositionalBindValues();
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Map<String, TypedValue> getNamedParameterMap() {
 		return queryParameterBindings.collectNamedParameterBindings();
 	}
 
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 
 	protected void beforeQuery() {
 		queryParameterBindings.verifyParametersBound( isCallable() );
 
 		assert sessionFlushMode == null;
 		assert sessionCacheMode == null;
 
 		if ( flushMode != null ) {
 			sessionFlushMode = getProducer().getHibernateFlushMode();
 			getProducer().setHibernateFlushMode( flushMode );
 		}
 		final CacheMode effectiveCacheMode = CacheModeHelper.effectiveCacheMode( cacheStoreMode, cacheRetrieveMode );
 		if ( effectiveCacheMode != null ) {
 			sessionCacheMode = getProducer().getCacheMode();
 			getProducer().setCacheMode( effectiveCacheMode );
 		}
 	}
 
 	protected void afterQuery() {
 		if ( sessionFlushMode != null ) {
 			getProducer().setHibernateFlushMode( sessionFlushMode );
 			sessionFlushMode = null;
 		}
 		if ( sessionCacheMode != null ) {
 			getProducer().setCacheMode( sessionCacheMode );
 			sessionCacheMode = null;
 		}
 	}
 
 	@Override
 	public Iterator<R> iterate() {
 		beforeQuery();
 		try {
 			return doIterate();
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	protected Iterator<R> doIterate() {
 		return getProducer().iterate(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	@Override
 	public ScrollableResultsImplementor scroll() {
 		return scroll( getProducer().getJdbcServices().getJdbcEnvironment().getDialect().defaultScrollMode() );
 	}
 
 	@Override
 	public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
 		beforeQuery();
 		try {
 			return doScroll( scrollMode );
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
 		QueryParameters queryParameters = getQueryParameters();
 		queryParameters.setScrollMode( scrollMode );
 		return getProducer().scroll(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				queryParameters
 		);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Stream<R> stream() {
 		final ScrollableResultsImplementor scrollableResults = scroll( ScrollMode.FORWARD_ONLY );
 		final ScrollableResultsIterator<R> iterator = new ScrollableResultsIterator<>( scrollableResults );
 		final Spliterator<R> spliterator = Spliterators.spliteratorUnknownSize( iterator, Spliterator.NONNULL );
 
 		final Stream<R> stream = StreamSupport.stream( spliterator, false );
 		stream.onClose( scrollableResults::close );
 
 		return stream;
 	}
 
 	@Override
 	public Optional<R> uniqueResultOptional() {
 		return Optional.ofNullable( uniqueResult() );
 	}
 
 	@Override
 	public List<R> list() {
 		beforeQuery();
 		try {
 			return doList();
 		}
 		catch (QueryExecutionRequestException he) {
 			throw new IllegalStateException( he );
 		}
 		catch (TypeMismatchException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getExceptionConverter().convert( he );
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	protected boolean isCallable() {
 		return false;
 	}
 
 	@SuppressWarnings("unchecked")
 	protected List<R> doList() {
 		if ( lockOptions.getLockMode() != null && lockOptions.getLockMode() != LockMode.NONE ) {
 			if ( !getProducer().isTransactionInProgress() ) {
 				throw new TransactionRequiredException( "no transaction is in progress" );
 			}
 		}
 
 		return getProducer().list(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	public QueryParameterBindingsImpl getQueryParameterBindings() {
 		return queryParameterBindings;
 	}
 
 	@Override
 	public R uniqueResult() {
 		return uniqueElement( list() );
 	}
 
 	@Override
 	public R getSingleResult() {
 		try {
 			final List<R> list = list();
 			if ( list.size() == 0 ) {
 				throw new NoResultException( "No entity found for query" );
 			}
 			return uniqueElement( list );
 		}
 		catch ( HibernateException e ) {
 			if ( getProducer().getFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 				throw getExceptionConverter().convert( e );
 			}
 			else {
 				throw e;
 			}
 		}
 	}
 
 	public static <R> R uniqueElement(List<R> list) throws NonUniqueResultException {
 		int size = list.size();
 		if ( size == 0 ) {
 			return null;
 		}
 		R first = list.get( 0 );
 		for ( int i = 1; i < size; i++ ) {
 			if ( list.get( i ) != first ) {
 				throw new NonUniqueResultException( list.size() );
 			}
 		}
 		return first;
 	}
 
 	@Override
 	public int executeUpdate() throws HibernateException {
 		if ( ! getProducer().isTransactionInProgress() ) {
 			throw getProducer().getExceptionConverter().convert(
 					new TransactionRequiredException(
 							"Executing an update/delete query"
 					)
 			);
 		}
 		beforeQuery();
 		try {
 			return doExecuteUpdate();
 		}
 		catch ( QueryExecutionRequestException e) {
 			throw new IllegalStateException( e );
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e );
 		}
 		catch ( HibernateException e) {
 			if ( getProducer().getFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 				throw getExceptionConverter().convert( e );
 			}
 			else {
 				throw e;
 			}
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	protected int doExecuteUpdate() {
 		return getProducer().executeUpdate(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	protected String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return getProducer().bestGuessEntityName( val );
 	}
 
 	@Override
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	@Override
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	@Override
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Type determineProperBooleanType(String name, Object value, Type defaultType) {
 		final QueryParameterBinding binding = getQueryParameterBindings().getBinding( name );
 		return binding.getBindType() != null
 				? binding.getBindType()
 				: defaultType;
 	}
 
 	@Override
 	public Type determineProperBooleanType(int position, Object value, Type defaultType) {
 		final QueryParameterBinding binding = getQueryParameterBindings().getBinding( position );
 		return binding.getBindType() != null
 				? binding.getBindType()
 				: defaultType;
 	}
 
 	private boolean isSelect() {
 		return getProducer().getFactory().getQueryPlanCache()
 				.getHQLQueryPlan( getQueryString(), false, Collections.<String, Filter>emptyMap() )
 				.isSelect();
 	}
 
 	protected ExceptionConverter getExceptionConverter(){
 		return producer.getExceptionConverter();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/OracleFollowOnLockingTest.java b/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/OracleFollowOnLockingTest.java
new file mode 100644
index 0000000000..98026288db
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/OracleFollowOnLockingTest.java
@@ -0,0 +1,448 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.dialect.functional;
+
+import java.util.List;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.LockModeType;
+import javax.persistence.NamedQuery;
+import javax.persistence.PersistenceException;
+import javax.persistence.QueryHint;
+
+import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
+import org.hibernate.Session;
+import org.hibernate.annotations.QueryHints;
+import org.hibernate.boot.SessionFactoryBuilder;
+import org.hibernate.dialect.Oracle8iDialect;
+import org.hibernate.exception.SQLGrammarException;
+
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.hibernate.test.util.jdbc.SQLStatementInterceptor;
+import org.junit.Before;
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.fail;
+
+/**
+ * @author Vlad Mihalcea
+ */
+@RequiresDialect(value = { Oracle8iDialect.class })
+@TestForIssue(jiraKey = "HHH-9486")
+public class OracleFollowOnLockingTest extends
+		BaseNonConfigCoreFunctionalTestCase {
+
+	private SQLStatementInterceptor sqlStatementInterceptor;
+
+	@Override
+	protected void configureSessionFactoryBuilder(SessionFactoryBuilder sfb) {
+		sqlStatementInterceptor = new SQLStatementInterceptor( sfb );
+	}
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] {
+				Product.class
+		};
+	}
+
+	@Before
+	public void init() {
+		final Session session = openSession();
+		session.beginTransaction();
+
+		for ( int i = 0; i < 50; i++ ) {
+			Product product = new Product();
+			product.name = "Product " + i % 10;
+			session.persist( product );
+		}
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Override
+	protected boolean isCleanupTestDataRequired() {
+		return true;
+	}
+
+	@Test
+	public void testPessimisticLockWithMaxResultsThenNoFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select p from Product p", Product.class )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
+						.setMaxResults( 10 )
+						.getResultList();
+
+		assertEquals( 10, products.size() );
+		assertEquals( 1, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithFirstResultsThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select p from Product p", Product.class )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
+						.setFirstResult( 40 )
+						.setMaxResults( 10 )
+						.getResultList();
+
+		assertEquals( 10, products.size() );
+		assertEquals( 11, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithNamedQueryExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products = session.createNamedQuery(
+			"product_by_name", Product.class )
+		.getResultList();
+
+		assertEquals( 50, products.size() );
+		assertEquals( 51, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithCountDistinctThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products = session.createQuery(
+			"select p from Product p where ( select count(distinct p1.id) from Product p1 ) > 0 ", Product.class )
+		.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ).setFollowOnLocking( false ) )
+		.getResultList();
+
+		assertEquals( 50, products.size() );
+		assertEquals( 1, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithFirstResultsWhileExplicitlyDisablingFollowOnLockingThenFails() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		try {
+			List<Product> products =
+					session.createQuery(
+							"select p from Product p", Product.class )
+							.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+													 .setFollowOnLocking( false ) )
+							.setFirstResult( 40 )
+							.setMaxResults( 10 )
+							.getResultList();
+			fail( "Should throw exception since Oracle does not support ORDER BY if follow on locking is disabled" );
+		}
+		catch ( PersistenceException expected ) {
+			assertEquals(
+					SQLGrammarException.class,
+					expected.getCause().getClass()
+			);
+		}
+	}
+
+	@Test
+	public void testPessimisticLockWithFirstResultsWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select p from Product p", Product.class )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+												 .setFollowOnLocking( true ) )
+						.setFirstResult( 40 )
+						.setMaxResults( 10 )
+						.getResultList();
+
+		assertEquals( 10, products.size() );
+		assertEquals( 11, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+
+	@Test
+	public void testPessimisticLockWithMaxResultsAndOrderByThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select p from Product p order by p.id", Product.class )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
+						.setMaxResults( 10 )
+						.getResultList();
+
+		assertEquals( 10, products.size() );
+		assertEquals( 11, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithMaxResultsAndOrderByWhileExplicitlyDisablingFollowOnLockingThenFails() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		try {
+			List<Product> products =
+					session.createQuery(
+							"select p from Product p order by p.id",
+							Product.class
+					)
+							.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+													 .setFollowOnLocking( false ) )
+							.setMaxResults( 10 )
+							.getResultList();
+			fail( "Should throw exception since Oracle does not support ORDER BY if follow on locking is disabled" );
+		}
+		catch ( PersistenceException expected ) {
+			assertEquals(
+					SQLGrammarException.class,
+					expected.getCause().getClass()
+			);
+		}
+	}
+
+	@Test
+	public void testPessimisticLockWithMaxResultsAndOrderByWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select p from Product p order by p.id", Product.class )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+												 .setFollowOnLocking( true ) )
+						.setMaxResults( 10 )
+						.getResultList();
+
+		assertEquals( 10, products.size() );
+		assertEquals( 11, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithDistinctThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select distinct p from Product p",
+						Product.class
+				)
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
+						.getResultList();
+
+		assertEquals( 50, products.size() );
+		assertEquals( 51, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithDistinctWhileExplicitlyDisablingFollowOnLockingThenFails() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		try {
+			List<Product> products =
+					session.createQuery(
+							"select distinct p from Product p where p.id > 40",
+							Product.class
+					)
+							.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+													 .setFollowOnLocking( false ) )
+							.getResultList();
+			fail( "Should throw exception since Oracle does not support DISTINCT if follow on locking is disabled" );
+		}
+		catch ( PersistenceException expected ) {
+			assertEquals(
+					SQLGrammarException.class,
+					expected.getCause().getClass()
+			);
+		}
+	}
+
+	@Test
+	public void testPessimisticLockWithDistinctWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Product> products =
+				session.createQuery(
+						"select distinct p from Product p where p.id > 40" )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+												 .setFollowOnLocking( true ) )
+						.setMaxResults( 10 )
+						.getResultList();
+
+		assertEquals( 10, products.size() );
+		assertEquals( 11, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithGroupByThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Object[]> products =
+				session.createQuery(
+						"select count(p), p " +
+								"from Product p " +
+								"group by p.id, p.name " )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE ) )
+						.getResultList();
+
+		assertEquals( 50, products.size() );
+		assertEquals( 51, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testPessimisticLockWithGroupByWhileExplicitlyDisablingFollowOnLockingThenFails() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		try {
+			List<Object[]> products =
+					session.createQuery(
+							"select count(p), p " +
+									"from Product p " +
+									"group by p.id, p.name " )
+							.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+													 .setFollowOnLocking( false ) )
+							.getResultList();
+			fail( "Should throw exception since Oracle does not support GROUP BY if follow on locking is disabled" );
+		}
+		catch ( PersistenceException expected ) {
+			assertEquals(
+					SQLGrammarException.class,
+					expected.getCause().getClass()
+			);
+		}
+	}
+
+	@Test
+	public void testPessimisticLockWithGroupByWhileExplicitlyEnablingFollowOnLockingThenFollowOnLocking() {
+
+		final Session session = openSession();
+		session.beginTransaction();
+
+		sqlStatementInterceptor.getSqlQueries().clear();
+
+		List<Object[]> products =
+				session.createQuery(
+						"select count(p), p " +
+								"from Product p " +
+								"group by p.id, p.name " )
+						.setLockOptions( new LockOptions( LockMode.PESSIMISTIC_WRITE )
+												 .setFollowOnLocking( true ) )
+						.getResultList();
+
+		assertEquals( 50, products.size() );
+		assertEquals( 51, sqlStatementInterceptor.getSqlQueries().size() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@NamedQuery(
+			name = "product_by_name",
+			query = "select p from Product p where p.name is not null",
+			lockMode = LockModeType.PESSIMISTIC_WRITE,
+			hints = @QueryHint(name = QueryHints.FOLLOW_ON_LOCKING, value = "true")
+	)
+	@Entity(name = "Product")
+	public static class Product {
+
+		@Id
+		@GeneratedValue
+		private Long id;
+
+		private String name;
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java b/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
index cd21d114ae..2c0015e1e6 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
@@ -1,204 +1,204 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Container class for different implementation of the {@link DialectCheck} interface.
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 abstract public class DialectChecks {
 	public static class SupportsSequences implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsSequences();
 		}
 	}
 
 	public static class SupportsExpectedLobUsagePattern implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsExpectedLobUsagePattern();
 		}
 	}
 
 	public static class UsesInputStreamToInsertBlob implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.useInputStreamToInsertBlob();
 		}
 	}
 
 	public static class SupportsIdentityColumns implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.getIdentityColumnSupport().supportsIdentityColumns();
 		}
 	}
 
 	public static class SupportsColumnCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsColumnCheck();
 		}
 	}
 
 	public static class SupportsEmptyInListCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsEmptyInList();
 		}
 	}
 
 	public static class CaseSensitiveCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.areStringComparisonsCaseInsensitive();
 		}
 	}
 
 	public static class SupportsResultSetPositioningOnForwardOnlyCursorCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsResultSetPositionQueryMethodsOnForwardOnlyCursor();
 		}
 	}
 
 	public static class SupportsCascadeDeleteCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsCascadeDelete();
 		}
 	}
 
 	public static class SupportsCircularCascadeDeleteCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsCircularCascadeDeleteConstraints();
 		}
 	}
 
 	public static class SupportsUnboundedLobLocatorMaterializationCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsExpectedLobUsagePattern() && dialect.supportsUnboundedLobLocatorMaterialization();
 		}
 	}
 
 	public static class SupportSubqueryAsLeftHandSideInPredicate implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsSubselectAsInPredicateLHS();
 		}
 	}
 
 	public static class SupportLimitCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLimit();
 		}
 	}
 
 	public static class SupportLimitAndOffsetCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLimit() && dialect.supportsLimitOffset();
 		}
 	}
 
 	public static class SupportsParametersInInsertSelectCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsParametersInInsertSelect();
 		}
 	}
 
 	public static class HasSelfReferentialForeignKeyBugCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.hasSelfReferentialForeignKeyBug();
 		}
 	}
 
 	public static class SupportsRowValueConstructorSyntaxCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsRowValueConstructorSyntax();
 		}
 	}
 
 	public static class SupportsRowValueConstructorSyntaxInInListCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsRowValueConstructorSyntaxInInList();
 		}
 	}
 
 	public static class DoesReadCommittedCauseWritersToBlockReadersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.doesReadCommittedCauseWritersToBlockReaders();
 		}
 	}
 
 	public static class DoesReadCommittedNotCauseWritersToBlockReadersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return ! dialect.doesReadCommittedCauseWritersToBlockReaders();
 		}
 	}
 
 	public static class DoesRepeatableReadCauseReadersToBlockWritersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.doesRepeatableReadCauseReadersToBlockWriters();
 		}
 	}
 
 	public static class DoesRepeatableReadNotCauseReadersToBlockWritersCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return ! dialect.doesRepeatableReadCauseReadersToBlockWriters();
 		}
 	}
 
 	public static class SupportsExistsInSelectCheck implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsExistsInSelect();
 		}
 	}
 	
 	public static class SupportsLobValueChangePropogation implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLobValueChangePropogation();
 		}
 	}
 	
 	public static class SupportsLockTimeouts implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsLockTimeouts();
 		}
 	}
 
 	public static class DoubleQuoteQuoting implements DialectCheck {
 		@Override
 		public boolean isMatch(Dialect dialect) {
 			return '\"' == dialect.openQuote() && '\"' == dialect.closeQuote();
 		}
 	}
 
 	public static class SupportSchemaCreation implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.canCreateSchema();
 		}
 	}
 
 	public static class SupportCatalogCreation implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.canCreateCatalog();
 		}
 	}
 
 	public static class DoesNotSupportRowValueConstructorSyntax implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsRowValueConstructorSyntax() == false;
 		}
 	}
 
 	public static class DoesNotSupportFollowOnLocking implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
-			return !dialect.useFollowOnLocking();
+			return !dialect.useFollowOnLocking( null );
 		}
 	}
 
 	public static class SupportPartitionBy implements DialectCheck {
 		public boolean isMatch(Dialect dialect) {
 			return dialect.supportsPartitionBy();
 		}
 	}
 }
