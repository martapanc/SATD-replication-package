diff --git a/documentation/src/main/docbook/devguide/en-US/Locking.xml b/documentation/src/main/docbook/devguide/en-US/Locking.xml
index 492adba889..245feb7a3e 100644
--- a/documentation/src/main/docbook/devguide/en-US/Locking.xml
+++ b/documentation/src/main/docbook/devguide/en-US/Locking.xml
@@ -1,320 +1,326 @@
 <?xml version='1.0' encoding='utf-8' ?>
 
 <chapter xmlns="http://docbook.org/ns/docbook" xmlns:xl="http://www.w3.org/1999/xlink" >
   <title>Locking</title>
   <para>
     Locking refers to actions taken to prevent data in a relational database from changing between the time it is read
     and the time that it is used.
   </para>
   <para>
     Your locking strategy can be either <firstterm>optimistic</firstterm> or <firstterm>pessimistic</firstterm>.
   </para>
   <variablelist>
     <title>Locking strategies</title>
     <varlistentry>
       <term>Optimistic</term>
       <listitem>
         <para>
           Optimistic locking assumes that multiple transactions can complete without affecting each other, and that
           therefore transactions can proceed without locking the data resources that they affect. Before committing,
           each transaction verifies that no other transaction has modified its data. If the check reveals conflicting
           modifications, the committing transaction rolls back<footnote><para><link xl:href="http://en.wikipedia.org/wiki/Optimistic_locking" /></para></footnote>.
         </para>
       </listitem>
     </varlistentry>
     <varlistentry>
       <term>Pessimistic</term>
       <listitem>
         <para>
           Pessimistic locking assumes that concurrent transactions will conflict with each other, and requires resources
           to be locked after they are read and only unlocked after the application has finished using the data.
         </para>
       </listitem>
     </varlistentry>
   </variablelist>
   <para>
     Hibernate provides mechanisms for implementing both types of locking in your applications.
   </para>
   <section>
     <title>Optimistic</title>
     <para>
       When your application uses long transactions or conversations that span several database transactions, you can
       store versioning data, so that if the same entity is updated by two conversations, the last to commit changes is
       informed of the conflict, and does not override the other conversation's work. This approach guarantees some
       isolation, but scales well and works particularly well in <firstterm>Read-Often Write-Sometimes</firstterm>
       situations.
     </para>
     <para>
       Hibernate provides two different mechanisms for storing versioning information, a dedicated version number or a
       timestamp.
     </para>
     <variablelist>
       <varlistentry>
         <term>Version number</term>
         <listitem>
           <para>
 
           </para>
         </listitem>
       </varlistentry>
       <varlistentry>
         <term>Timestamp</term>
         <listitem>
           <para>
 
           </para>
         </listitem>
       </varlistentry>
     </variablelist>
     <note>
       <para>
         A version or timestamp property can never be null for a detached instance. Hibernate detects any instance with a
         null version or timestamp as transient, regardless of other unsaved-value strategies that you specify. Declaring
         a nullable version or timestamp property is an easy way to avoid problems with transitive reattachment in
         Hibernate, especially useful if you use assigned identifiers or composite keys.
       </para>
     </note>
     
     <section>
       <title>Dedicated version number</title>
       <para>
         The version number mechanism for optimistic locking is provided through a <literal>@Version</literal>
         annotation.
       </para>
       <example>
         <title>The @Version annotation</title>
         <programlisting language="Java" role="JAVA"><xi:include href="extras/version_annotation.java" xmlns:xi="http://www.w3.org/2001/XInclude" parse="text" /></programlisting>
         <para>
           Here, the version property is mapped to the <literal>OPTLOCK</literal> column, and the entity manager uses it
           to detect conflicting updates, and prevent the loss of updates that would be overwritten by a
           <firstterm>last-commit-wins</firstterm> strategy.
         </para>
       </example>
       <para>
         The version column can be any kind of type, as long as you define and implement the appropriate
         <classname>UserVersionType</classname>.
       </para>
       <para>
         Your application is forbidden from altering the version number set by Hibernate. To artificially increase the
         version number, see the documentation for properties
         <property>LockModeType.OPTIMISTIC_FORCE_INCREMENT</property> or
         <property>LockModeType.PESSIMISTIC_FORCE_INCREMENTcheck</property> in the Hibernate Entity Manager reference
         documentation.
       </para>
       <note>
         <title>Database-generated version numbers</title>
         <para>
           If the version number is generated by the database, such as a trigger, use the annotation
           <literal>@org.hibernate.annotations.Generated(GenerationTime.ALWAYS)</literal>.
         </para>
       </note>
       <example>
         <title>Declaring a version property in <filename>hbm.xml</filename></title>
         <programlisting language="XML" role="XML"><xi:include href="extras/version_property.xml"
         xmlns:xi="http://www.w3.org/2001/XInclude" parse="text" /></programlisting>
         <informaltable>
           <tgroup cols="2">
             <tbody>
               <row>
                 <entry>column</entry>
                 <entry><para>The name of the column holding the version number. Optional, defaults to the property
                 name. </para></entry>
               </row>
               <row>
                 <entry>name</entry>
                 <entry><para>The name of a property of the persistent class.</para></entry>
               </row>
               <row>
                 <entry>type</entry>
                 <entry><para>The type of the version number. Optional, defaults to
                 <literal>integer</literal>.</para></entry>
               </row>
               <row>
                 <entry>access</entry>
                 <entry><para>Hibernate's strategy for accessing the property value. Optional, defaults to
                 <literal>property</literal>.</para></entry>
               </row>
               <row>
                 <entry>unsaved-value</entry>
                 <entry><para>Indicates that an instance is newly instantiated and thus unsaved. This distinguishes it
                 from detached instances that were saved or loaded in a previous session. The default value,
                 <literal>undefined</literal>, indicates that the identifier property value should be
                 used. Optional.</para></entry>
               </row>
               <row>
                 <entry>generated</entry>
                 <entry><para>Indicates that the version property value is generated by the database. Optional, defaults
                 to <literal>never</literal>.</para></entry>
               </row>
               <row>
                 <entry>insert</entry>
                 <entry><para>Whether or not to include the <code>version</code> column in SQL <code>insert</code>
                 statements. Defaults to <literal>true</literal>, but you can set it to <literal>false</literal> if the
                 database column is defined with a default value of <literal>0</literal>.</para></entry>
               </row>
             </tbody>
           </tgroup>
         </informaltable>
       </example>
     </section>
     
     <section>
       <title>Timestamp</title>
       <para>
         Timestamps are a less reliable way of optimistic locking than version numbers, but can be used by applications
         for other purposes as well. Timestamping is automatically used if you the <code>@Version</code> annotation on a
         <type>Date</type> or <type>Calendar</type>.
       </para>
       <example>
         <title>Using timestamps for optimistic locking</title>
         <programlisting language="Java" role="JAVA"><xi:include href="extras/timestamp_version.java" xmlns:xi="http://www.w3.org/2001/XInclude" parse="text" /></programlisting>
       </example>
       <para>
         Hibernate can retrieve the timestamp value from the database or the JVM, by reading the value you specify for
         the <code>@org.hibernate.annotations.Source</code> annotation. The value can be either
         <literal>org.hibernate.annotations.SourceType.DB</literal> or
         <literal>org.hibernate.annotations.SourceType.VM</literal>. The default behavior is to use the database, and is
         also used if you don't specify the annotation at all.
       </para>
       <para>
         The timestamp can also be generated by the database instead of Hibernate, if you use the
         <code>@org.hibernate.annotations.Generated(GenerationTime.ALWAYS)</code> annotation.
       </para>
       <example>
         <title>The timestamp element in <filename>hbm.xml</filename></title>
         <programlisting language="XML" role="XML"><xi:include href="extras/timestamp_version.xml"
         xmlns:xi="http://www.w3.org/2001/XInclude" parse="text" /></programlisting>
         <informaltable>
           <tgroup cols="2">
             <tbody>
               <row>
                 <entry>column</entry>
                 <entry><para>The name of the column which holds the timestamp. Optional, defaults to the property
                 namel</para></entry>
               </row>
               <row>
                 <entry>name</entry>
                 <entry><para>The name of a JavaBeans style property of Java type Date or Timestamp of the persistent
                 class.</para></entry>
               </row>
               <row>
                 <entry>access</entry>
                 <entry><para>The strategy Hibernate uses to access the property value. Optional, defaults to
                 <literal>property</literal>.</para></entry>
               </row>
               <row>
                 <entry>unsaved-value</entry> <entry><para>A version property which indicates than instance is newly
                 instantiated, and unsaved. This distinguishes it from detached instances that were saved or loaded in a
                 previous session. The default value of <literal>undefined</literal> indicates that Hibernate uses the
                 identifier property value.</para></entry>
               </row>
               <row>
                 <entry>source</entry>
                 <entry><para>Whether Hibernate retrieves the timestamp from the database or the current
                 JVM. Database-based timestamps incur an overhead because Hibernate needs to query the database each time
                 to determine the incremental next value. However, database-derived timestamps are safer to use in a
                 clustered environment. Not all database dialects are known to support the retrieval of the database's
                 current timestamp. Others may also be unsafe for locking, because of lack of precision.</para></entry>
               </row>
               <row>
                 <entry>generated</entry>
                 <entry><para>Whether the timestamp property value is generated by the database. Optional, defaults to
                 <literal>never</literal>.</para></entry>
               </row>
             </tbody>
           </tgroup>
         </informaltable>
       </example>
     </section>
 
   </section>
   
   <section>
     <title>Pessimistic</title>
     <para>
       Typically, you only need to specify an isolation level for the JDBC connections and let the database handle
       locking issues. If you do need to obtain exclusive pessimistic locks or re-obtain locks at the start of a new
       transaction, Hibernate gives you the tools you need.
     </para>
     <note>
       <para>
         Hibernate always uses the locking mechanism of the database, and never lock objects in memory.
       </para>
     </note>
     <section>
       <title>The <classname>LockMode</classname> class</title>
       <para>
         The <classname>LockMode</classname> class defines the different lock levels that Hibernate can acquire.
       </para>
       <informaltable>
         <tgroup cols="2">
           <tbody>
             <row>
               <entry>LockMode.WRITE</entry>
               <entry><para>acquired automatically when Hibernate updates or inserts a row.</para></entry>
             </row>
             <row>
               <entry>LockMode.UPGRADE</entry>
               <entry><para>acquired upon explicit user request using <code>SELECT ... FOR UPDATE</code> on databases
               which support that syntax.</para></entry>
             </row>
             <row>
               <entry>LockMode.UPGRADE_NOWAIT</entry>
               <entry><para>acquired upon explicit user request using a <code>SELECT ... FOR UPDATE NOWAIT</code> in
               Oracle.</para></entry>
             </row>
             <row>
+              <entry>LockMode.UPGRADE_SKIPLOCKED</entry>
+              <entry><para>acquired upon explicit user request using a <code>SELECT ... FOR UPDATE SKIP LOCKED</code> in
+              Oracle, or <code>SELECT ... with (rowlock,updlock,readpast) in SQL Server</code>.</para></entry>
+            </row>
+            <row>
               <entry>LockMode.READ</entry>
               <entry><para>acquired automatically when Hibernate reads data under <phrase>Repeatable Read</phrase> or
               <phrase>Serializable</phrase> isolation level. It can be re-acquired by explicit user
               request.</para></entry>
             </row>
             <row>
               <entry>LockMode.NONE</entry>
               <entry><para>The absence of a lock. All objects switch to this lock mode at the end of a
               Transaction. Objects associated with the session via a call to <methodname>update()</methodname> or
               <methodname>saveOrUpdate()</methodname> also start out in this lock mode. </para></entry>
             </row>
           </tbody>
         </tgroup>
       </informaltable>
       <para>
         The explicit user request mentioned above occurs as a consequence of any of the following actions:
       </para>
       <itemizedlist>
         <listitem>
           <para>
             A call to <methodname>Session.load()</methodname>, specifying a LockMode.
           </para>
         </listitem>
         <listitem>
           <para>
             A call to <methodname>Session.lock()</methodname>.
           </para>
         </listitem>
         <listitem>
           <para>
             A call to <methodname>Query.setLockMode()</methodname>.
           </para>
         </listitem>
       </itemizedlist>
       <para>
-        If you call <methodname>Session.load()</methodname> with option <option>UPGRADE</option> or
-        <option>UPGRADE_NOWAIT</option>, and the requested object is not already loaded by the session, the object is
-        loaded using <code>SELECT ... FOR UPDATE</code>. If you call <methodname>load()</methodname> for an object that
-        is already loaded with a less restrictive lock than the one you request, Hibernate calls
-        <methodname>lock()</methodname> for that object.
+        If you call <methodname>Session.load()</methodname> with option <option>UPGRADE</option>,
+        <option>UPGRADE_NOWAIT</option> or <option>UPGRADE_SKIPLOCKED</option>, and the requested object is not already
+        loaded by the session, the object is loaded using <code>SELECT ... FOR UPDATE</code>. If you call 
+        <methodname>load()</methodname> for an object that is already loaded with a less restrictive lock than the one 
+        you request, Hibernate calls <methodname>lock()</methodname> for that object.
       </para>
       <para>
         <methodname>Session.lock()</methodname> performs a version number check if the specified lock mode is
-        <literal>READ</literal>, <literal>UPGRADE</literal>, or <literal>UPGRADE_NOWAIT</literal>. In the case of
-        <literal>UPGRADE</literal> or <literal>UPGRADE_NOWAIT</literal>, <code>SELECT ... FOR UPDATE</code> syntax is
-        used.
+        <literal>READ</literal>, <literal>UPGRADE</literal>, <literal>UPGRADE_NOWAIT</literal> or 
+        <literal>UPGRADE_SKIPLOCKED</literal>. In the case of <literal>UPGRADE</literal>, 
+        <literal>UPGRADE_NOWAIT</literal> or <literal>UPGRADE_SKIPLOCKED</literal>, <code>SELECT ... FOR UPDATE</code> 
+	syntax is used.
       </para>
       <para>
         If the requested lock mode is not supported by the database, Hibernate uses an appropriate alternate mode
         instead of throwing an exception. This ensures that applications are portable.
       </para>
     </section>
   </section>
 </chapter>
diff --git a/hibernate-core/src/main/java/org/hibernate/LockMode.java b/hibernate-core/src/main/java/org/hibernate/LockMode.java
index 4895986be1..cde6b6bdf0 100644
--- a/hibernate-core/src/main/java/org/hibernate/LockMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/LockMode.java
@@ -1,147 +1,156 @@
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
 package org.hibernate;
 
 /**
  * Instances represent a lock mode for a row of a relational
  * database table. It is not intended that users spend much
  * time worrying about locking since Hibernate usually
  * obtains exactly the right lock level automatically.
  * Some "advanced" users may wish to explicitly specify lock
  * levels.
  *
  * @author Gavin King
  * @see Session#lock(Object, LockMode)
  */
 public enum LockMode {
 	/**
 	 * No lock required. If an object is requested with this lock
 	 * mode, a <tt>READ</tt> lock will be obtained if it is
 	 * necessary to actually read the state from the database,
 	 * rather than pull it from a cache.<br>
 	 * <br>
 	 * This is the "default" lock mode.
 	 */
 	NONE( 0 ),
 	/**
 	 * A shared lock. Objects in this lock mode were read from
 	 * the database in the current transaction, rather than being
 	 * pulled from a cache.
 	 */
 	READ( 5 ),
 	/**
 	 * An upgrade lock. Objects loaded in this lock mode are
 	 * materialized using an SQL <tt>select ... for update</tt>.
 	 *
 	 * @deprecated instead use PESSIMISTIC_WRITE
 	 */
     @Deprecated
 	UPGRADE( 10 ),
 	/**
 	 * Attempt to obtain an upgrade lock, using an Oracle-style
 	 * <tt>select for update nowait</tt>. The semantics of
 	 * this lock mode, once obtained, are the same as
 	 * <tt>UPGRADE</tt>.
 	 */
 	UPGRADE_NOWAIT( 10 ),
+
+	/**
+	 * Attempt to obtain an upgrade lock, using an Oracle-style
+	 * <tt>select for update skip locked</tt>. The semantics of
+	 * this lock mode, once obtained, are the same as
+	 * <tt>UPGRADE</tt>.
+	 */
+	UPGRADE_SKIPLOCKED( 10 ),
+
 	/**
 	 * A <tt>WRITE</tt> lock is obtained when an object is updated
 	 * or inserted.   This lock mode is for internal use only and is
 	 * not a valid mode for <tt>load()</tt> or <tt>lock()</tt> (both
 	 * of which throw exceptions if WRITE is specified).
 	 */
 	WRITE( 10 ),
 
 	/**
 	 * Similiar to {@link #UPGRADE} except that, for versioned entities,
 	 * it results in a forced version increment.
 	 *
 	 * @deprecated instead use PESSIMISTIC_FORCE_INCREMENT
 	 */
     @Deprecated
 	FORCE( 15 ),
 
 	/**
 	 *  start of javax.persistence.LockModeType equivalent modes
 	 */
 
 	/**
 	 * Optimisticly assume that transaction will not experience contention for
 	 * entities.  The entity version will be verified near the transaction end.
 	 */
 	OPTIMISTIC( 6 ),
 
 	/**
 	 * Optimisticly assume that transaction will not experience contention for
 	 * entities.  The entity version will be verified and incremented near the transaction end.
 	 */
 	OPTIMISTIC_FORCE_INCREMENT( 7 ),
 
 	/**
 	 * Implemented as PESSIMISTIC_WRITE.
 	 * TODO:  introduce separate support for PESSIMISTIC_READ
 	 */
 	PESSIMISTIC_READ( 12 ),
 
 	/**
 	 * Transaction will obtain a database lock immediately.
 	 * TODO:  add PESSIMISTIC_WRITE_NOWAIT
 	 */
 	PESSIMISTIC_WRITE( 13 ),
 
 	/**
 	 * Transaction will immediately increment the entity version.
 	 */
 	PESSIMISTIC_FORCE_INCREMENT( 17 );
 	private final int level;
 
 	private LockMode(int level) {
 		this.level = level;
 	}
 
 	/**
 	 * Check if this lock mode is more restrictive than the given lock mode.
 	 *
 	 * @param mode LockMode to check
 	 *
 	 * @return true if this lock mode is more restrictive than given lock mode
 	 */
 	public boolean greaterThan(LockMode mode) {
 		return level > mode.level;
 	}
 
 	/**
 	 * Check if this lock mode is less restrictive than the given lock mode.
 	 *
 	 * @param mode LockMode to check
 	 *
 	 * @return true if this lock mode is less restrictive than given lock mode
 	 */
 	public boolean lessThan(LockMode mode) {
 		return level < mode.level;
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/LockOptions.java b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
index 016a8ced22..6de28a4a9a 100644
--- a/hibernate-core/src/main/java/org/hibernate/LockOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
@@ -1,296 +1,302 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2012, Red Hat Inc. or third-party contributors as
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
 	 * NONE represents LockMode.NONE (timeout + scope do not apply)
 	 */
 	public static final LockOptions NONE = new LockOptions(LockMode.NONE);
 
 	/**
 	 * READ represents LockMode.READ (timeout + scope do not apply)
 	 */
 	public static final LockOptions READ = new LockOptions(LockMode.READ);
 
 	/**
 	 * UPGRADE represents LockMode.UPGRADE (will wait forever for lock and
 	 * scope of false meaning only entity is locked)
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
 
+	/**
+	 * Indicates that rows that are already locked should be skipped.
+	 * @see #getTimeOut()
+	 */
+	public static final int SKIP_LOCKED = -2;
+
 	private LockMode lockMode = LockMode.NONE;
 	private int timeout = WAIT_FOREVER;
 
 	//initialize lazily as LockOptions is frequently created without needing this
 	private Map<String,LockMode> aliasSpecificLockModes = null;
 
 	public LockOptions() {
 	}
 
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
 	 * Iterator for accessing Alias (key) and LockMode (value) as Map.Entry
 	 *
 	 * @return Iterator for accessing the Map.Entry's
 	 */
 	public Iterator getAliasLockIterator() {
 		if ( aliasSpecificLockModes == null ) {
 			return Collections.emptyList().iterator();
 		}
 		return aliasSpecificLockModes.entrySet().iterator();
 	}
 
 	/**
 	 * Currently needed for follow-on locking
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
-	 * {@link #NO_WAIT} and {@link #WAIT_FOREVER} represent 2 "magic" values.
+	 * {@link #NO_WAIT}, {@link #WAIT_FOREVER} or {@link #SKIP_LOCKED} represent 3 "magic" values.
 	 *
-	 * @return timeout in milliseconds, or {@link #NO_WAIT} or {@link #WAIT_FOREVER}
+	 * @return timeout in milliseconds, {@link #NO_WAIT}, {@link #WAIT_FOREVER} or {@link #SKIP_LOCKED}
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
 
 	private boolean scope=false;
 
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
 	 * Set the cope.
 	 *
 	 * @param scope The new scope setting
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setScope(boolean scope) {
 		this.scope = scope;
 		return this;
 	}
 
 	public LockOptions makeCopy() {
 		final LockOptions copy = new LockOptions();
 		copy( this, copy );
 		return copy;
 	}
 
 	/**
 	 * Perform a shallow copy
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
 		return destination;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
index f98bf456cd..4986925842 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
@@ -1,403 +1,406 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.dom4j.Element;
 
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.type.Type;
 
 /**
  * @author Emmanuel Bernard
  */
 public abstract class ResultSetMappingBinder {
 	/**
 	 * Build a ResultSetMappingDefinition given a containing element for the "return-XXX" elements
 	 *
 	 * @param resultSetElem The element containing the return definitions.
 	 * @param path No clue...
 	 * @param mappings The current processing state.
 	 * @return The description of the mappings...
 	 */
 	protected static ResultSetMappingDefinition buildResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		String resultSetName = resultSetElem.attribute( "name" ).getValue();
 		if ( path != null ) {
 			resultSetName = path + '.' + resultSetName;
 		}
 		ResultSetMappingDefinition definition = new ResultSetMappingDefinition( resultSetName );
 
 		int cnt = 0;
 		Iterator returns = resultSetElem.elementIterator();
 		while ( returns.hasNext() ) {
 			cnt++;
 			Element returnElem = (Element) returns.next();
 			String name = returnElem.getName();
 			if ( "return-scalar".equals( name ) ) {
 				String column = returnElem.attributeValue( "column" );
 				String typeFromXML = HbmBinder.getTypeFromXML( returnElem );
 				Type type = null;
 				if(typeFromXML!=null) {
 					type = mappings.getTypeResolver().heuristicType( typeFromXML );
 					if ( type == null ) {
 						throw new MappingException( "could not determine type " + type );
 					}
 				}
 				definition.addQueryReturn( new NativeSQLQueryScalarReturn( column, type ) );
 			}
 			else if ( "return".equals( name ) ) {
 				definition.addQueryReturn( bindReturn( returnElem, mappings, cnt ) );
 			}
 			else if ( "return-join".equals( name ) ) {
 				definition.addQueryReturn( bindReturnJoin( returnElem, mappings ) );
 			}
 			else if ( "load-collection".equals( name ) ) {
 				definition.addQueryReturn( bindLoadCollection( returnElem, mappings ) );
 			}
 		}
 		return definition;
 	}
 
 	private static NativeSQLQueryRootReturn bindReturn(Element returnElem, Mappings mappings, int elementCount) {
 		String alias = returnElem.attributeValue( "alias" );
 		if( StringHelper.isEmpty( alias )) {
 			alias = "alias_" + elementCount; // hack/workaround as sqlquery impl depend on having a key.
 		}
 
 		String entityName = HbmBinder.getEntityName(returnElem, mappings);
 		if(entityName==null) {
 			throw new MappingException( "<return alias='" + alias + "'> must specify either a class or entity-name");
 		}
 		LockMode lockMode = getLockMode( returnElem.attributeValue( "lock-mode" ) );
 
 		PersistentClass pc = mappings.getClass( entityName );
 		java.util.Map propertyResults = bindPropertyResults(alias, returnElem, pc, mappings );
 
 		return new NativeSQLQueryRootReturn(
 				alias,
 				entityName,
 				propertyResults,
 				lockMode
 			);
 	}
 
 	private static NativeSQLQueryJoinReturn bindReturnJoin(Element returnElem, Mappings mappings) {
 		String alias = returnElem.attributeValue( "alias" );
 		String roleAttribute = returnElem.attributeValue( "property" );
 		LockMode lockMode = getLockMode( returnElem.attributeValue( "lock-mode" ) );
 		int dot = roleAttribute.lastIndexOf( '.' );
 		if ( dot == -1 ) {
 			throw new MappingException(
 					"Role attribute for sql query return [alias=" + alias +
 					"] not formatted correctly {owningAlias.propertyName}"
 				);
 		}
 		String roleOwnerAlias = roleAttribute.substring( 0, dot );
 		String roleProperty = roleAttribute.substring( dot + 1 );
 
 		//FIXME: get the PersistentClass
 		java.util.Map propertyResults = bindPropertyResults(alias, returnElem, null, mappings );
 
 		return new NativeSQLQueryJoinReturn(
 				alias,
 				roleOwnerAlias,
 				roleProperty,
 				propertyResults, // TODO: bindpropertyresults(alias, returnElem)
 				lockMode
 			);
 	}
 
 	private static NativeSQLQueryCollectionReturn bindLoadCollection(Element returnElem, Mappings mappings) {
 		String alias = returnElem.attributeValue( "alias" );
 		String collectionAttribute = returnElem.attributeValue( "role" );
 		LockMode lockMode = getLockMode( returnElem.attributeValue( "lock-mode" ) );
 		int dot = collectionAttribute.lastIndexOf( '.' );
 		if ( dot == -1 ) {
 			throw new MappingException(
 					"Collection attribute for sql query return [alias=" + alias +
 					"] not formatted correctly {OwnerClassName.propertyName}"
 				);
 		}
 		String ownerClassName = HbmBinder.getClassName( collectionAttribute.substring( 0, dot ), mappings );
 		String ownerPropertyName = collectionAttribute.substring( dot + 1 );
 
 		//FIXME: get the PersistentClass
 		java.util.Map propertyResults = bindPropertyResults(alias, returnElem, null, mappings );
 
 		return new NativeSQLQueryCollectionReturn(
 				alias,
 				ownerClassName,
 				ownerPropertyName,
 				propertyResults,
 				lockMode
 			);
 	}
 
 	private static java.util.Map bindPropertyResults(
 			String alias, Element returnElement, PersistentClass pc, Mappings mappings
 	) {
 
 		HashMap propertyresults = new HashMap(); // maybe a concrete SQLpropertyresult type, but Map is exactly what is required at the moment
 
 		Element discriminatorResult = returnElement.element("return-discriminator");
 		if(discriminatorResult!=null) {
 			ArrayList resultColumns = getResultColumns(discriminatorResult);
 			propertyresults.put("class", ArrayHelper.toStringArray( resultColumns ) );
 		}
 		Iterator iterator = returnElement.elementIterator("return-property");
 		List properties = new ArrayList();
 		List propertyNames = new ArrayList();
 		while ( iterator.hasNext() ) {
 			Element propertyresult = (Element) iterator.next();
 			String name = propertyresult.attributeValue("name");
 			if ( pc == null || name.indexOf( '.') == -1) { //if dotted and not load-collection nor return-join
 				//regular property
 				properties.add(propertyresult);
 				propertyNames.add(name);
 			}
 			else {
 				/**
 				 * Reorder properties
 				 * 1. get the parent property
 				 * 2. list all the properties following the expected one in the parent property
 				 * 3. calculate the lowest index and insert the property
 				 */
 				if (pc == null)
 					throw new MappingException("dotted notation in <return-join> or <load_collection> not yet supported");
 				int dotIndex = name.lastIndexOf( '.' );
 				String reducedName = name.substring( 0, dotIndex );
 				Value value = pc.getRecursiveProperty( reducedName ).getValue();
 				Iterator parentPropIter;
 				if ( value instanceof Component ) {
 					Component comp = (Component) value;
 					parentPropIter = comp.getPropertyIterator();
 				}
 				else if ( value instanceof ToOne ) {
 					ToOne toOne = (ToOne) value;
 					PersistentClass referencedPc = mappings.getClass( toOne.getReferencedEntityName() );
 					if ( toOne.getReferencedPropertyName() != null ) {
 						try {
 							parentPropIter = ( (Component) referencedPc.getRecursiveProperty( toOne.getReferencedPropertyName() ).getValue() ).getPropertyIterator();
 						} catch (ClassCastException e) {
 							throw new MappingException("dotted notation reference neither a component nor a many/one to one", e);
 						}
 					}
 					else {
 						try {
 							if ( referencedPc.getIdentifierMapper() == null ) {
 								parentPropIter = ( (Component) referencedPc.getIdentifierProperty().getValue() ).getPropertyIterator();
 							}
 							else {
 								parentPropIter = referencedPc.getIdentifierMapper().getPropertyIterator();
 							}
 						}
 						catch (ClassCastException e) {
 							throw new MappingException("dotted notation reference neither a component nor a many/one to one", e);
 						}
 					}
 				}
 				else {
 					throw new MappingException("dotted notation reference neither a component nor a many/one to one");
 				}
 				boolean hasFollowers = false;
 				List followers = new ArrayList();
 				while ( parentPropIter.hasNext() ) {
 					String currentPropertyName = ( (Property) parentPropIter.next() ).getName();
 					String currentName = reducedName + '.' + currentPropertyName;
 					if (hasFollowers) {
 						followers.add( currentName );
 					}
 					if ( name.equals( currentName ) ) hasFollowers = true;
 				}
 
 				int index = propertyNames.size();
 				int followersSize = followers.size();
 				for (int loop = 0 ; loop < followersSize ; loop++) {
 					String follower = (String) followers.get(loop);
 					int currentIndex = getIndexOfFirstMatchingProperty(propertyNames, follower);
 					index = currentIndex != -1 && currentIndex < index ? currentIndex : index;
 				}
 				propertyNames.add(index, name);
 				properties.add(index, propertyresult);
 			}
 		}
 
 		Set uniqueReturnProperty = new HashSet();
 		iterator = properties.iterator();
 		while ( iterator.hasNext() ) {
 			Element propertyresult = (Element) iterator.next();
 			String name = propertyresult.attributeValue("name");
 			if ( "class".equals(name) ) {
 				throw new MappingException(
 						"class is not a valid property name to use in a <return-property>, use <return-discriminator> instead"
 					);
 			}
 			//TODO: validate existing of property with the chosen name. (secondpass )
 			ArrayList allResultColumns = getResultColumns(propertyresult);
 
 			if ( allResultColumns.isEmpty() ) {
 				throw new MappingException(
 						"return-property for alias " + alias +
 						" must specify at least one column or return-column name"
 					);
 			}
 			if ( uniqueReturnProperty.contains( name ) ) {
 				throw new MappingException(
 						"duplicate return-property for property " + name +
 						" on alias " + alias
 					);
 			}
 			uniqueReturnProperty.add(name);
 
 			// the issue here is that for <return-join/> representing an entity collection,
 			// the collection element values (the property values of the associated entity)
 			// are represented as 'element.{propertyname}'.  Thus the StringHelper.root()
 			// here puts everything under 'element' (which additionally has significant
 			// meaning).  Probably what we need to do is to something like this instead:
 			//      String root = StringHelper.root( name );
 			//      String key = root; // by default
 			//      if ( !root.equals( name ) ) {
 			//	        // we had a dot
 			//          if ( !root.equals( alias ) {
 			//              // the root does not apply to the specific alias
 			//              if ( "elements".equals( root ) {
 			//                  // we specifically have a <return-join/> representing an entity collection
 			//                  // and this <return-property/> is one of that entity's properties
 			//                  key = name;
 			//              }
 			//          }
 			//      }
 			// but I am not clear enough on the intended purpose of this code block, especially
 			// in relation to the "Reorder properties" code block above...
 //			String key = StringHelper.root( name );
 			String key = name;
 			ArrayList intermediateResults = (ArrayList) propertyresults.get( key );
 			if (intermediateResults == null) {
 				propertyresults.put( key, allResultColumns );
 			}
 			else {
 				intermediateResults.addAll( allResultColumns );
 			}
 		}
 
 		Iterator entries = propertyresults.entrySet().iterator();
 		while ( entries.hasNext() ) {
 			Map.Entry entry = (Map.Entry) entries.next();
 			if (entry.getValue() instanceof ArrayList) {
 				ArrayList list = (ArrayList) entry.getValue();
 				entry.setValue( list.toArray( new String[ list.size() ] ) );
 			}
 		}
 		return propertyresults.isEmpty() ? Collections.EMPTY_MAP : propertyresults;
 	}
 
 	private static int getIndexOfFirstMatchingProperty(List propertyNames, String follower) {
 		int propertySize = propertyNames.size();
 		for (int propIndex = 0 ; propIndex < propertySize ; propIndex++) {
 			if ( ( (String) propertyNames.get(propIndex) ).startsWith( follower ) ) {
 				return propIndex;
 			}
 		}
 		return -1;
 	}
 
 	private static ArrayList getResultColumns(Element propertyresult) {
 		String column = unquote(propertyresult.attributeValue("column"));
 		ArrayList allResultColumns = new ArrayList();
 		if(column!=null) allResultColumns.add(column);
 		Iterator resultColumns = propertyresult.elementIterator("return-column");
 		while ( resultColumns.hasNext() ) {
 			Element element = (Element) resultColumns.next();
 			allResultColumns.add( unquote(element.attributeValue("name")) );
 		}
 		return allResultColumns;
 	}
 
 	private static String unquote(String name) {
 		if (name!=null && name.charAt(0)=='`') {
 			name=name.substring( 1, name.length()-1 );
 		}
 		return name;
 	}
 
 	private static LockMode getLockMode(String lockMode) {
 		if ( lockMode == null || "read".equals( lockMode ) ) {
 			return LockMode.READ;
 		}
 		else if ( "none".equals( lockMode ) ) {
 			return LockMode.NONE;
 		}
 		else if ( "upgrade".equals( lockMode ) ) {
 			return LockMode.UPGRADE;
 		}
 		else if ( "upgrade-nowait".equals( lockMode ) ) {
 			return LockMode.UPGRADE_NOWAIT;
 		}
+		else if ( "upgrade-skiplocked".equals( lockMode )) {
+			return LockMode.UPGRADE_SKIPLOCKED;
+		}
 		else if ( "write".equals( lockMode ) ) {
 			return LockMode.WRITE;
 		}
 		else if ( "force".equals( lockMode ) ) {
 			return LockMode.FORCE;
 		}
 		else if ( "optimistic".equals( lockMode ) ) {
 			return LockMode.OPTIMISTIC;
 		}
 		else if ( "optimistic_force_increment".equals( lockMode ) ) {
 			return LockMode.OPTIMISTIC_FORCE_INCREMENT;
 		}
 		else if ( "pessimistic_read".equals( lockMode ) ) {
 			return LockMode.PESSIMISTIC_READ;
 		}
 		else if ( "pessimistic_write".equals( lockMode ) ) {
 			return LockMode.PESSIMISTIC_WRITE;
 		}
 		else if ( "pessimistic_force_increment".equals( lockMode ) ) {
 			return LockMode.PESSIMISTIC_FORCE_INCREMENT;
 		}
 		else {
 			throw new MappingException( "unknown lockmode" );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 05669ecb79..d9b5d6fc96 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,2479 +1,2477 @@
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
 package org.hibernate.dialect;
 
-import java.io.InputStream;
-import java.io.OutputStream;
-import java.sql.Blob;
-import java.sql.CallableStatement;
-import java.sql.Clob;
-import java.sql.NClob;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.Properties;
-import java.util.Set;
-
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
-import org.hibernate.dialect.function.CastFunction;
-import org.hibernate.dialect.function.SQLFunction;
-import org.hibernate.dialect.function.SQLFunctionTemplate;
-import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
-import org.hibernate.dialect.function.StandardSQLFunction;
-import org.hibernate.dialect.lock.LockingStrategy;
-import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
-import org.hibernate.dialect.lock.OptimisticLockingStrategy;
-import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
-import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
-import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
-import org.hibernate.dialect.lock.SelectLockingStrategy;
+import org.hibernate.dialect.function.*;
+import org.hibernate.dialect.lock.*;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.persister.entity.Lockable;
-import org.hibernate.sql.ANSICaseFragment;
-import org.hibernate.sql.ANSIJoinFragment;
-import org.hibernate.sql.CaseFragment;
-import org.hibernate.sql.ForUpdateFragment;
-import org.hibernate.sql.JoinFragment;
+import org.hibernate.sql.*;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.jboss.logging.Logger;
 
+import java.io.InputStream;
+import java.io.OutputStream;
+import java.sql.*;
+import java.util.*;
+
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.
  * Subclasses implement Hibernate compatibility with different systems.<br>
  * <br>
  * Subclasses should provide a public default constructor that <tt>register()</tt>
  * a set of type mappings and default Hibernate properties.<br>
  * <br>
  * Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 public abstract class Dialect implements ConversionContext {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dialect.class.getName());
 
 	public static final String DEFAULT_BATCH_SIZE = "15";
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 	
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
 		
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		String dialectName = Environment.getProperties().getProperty( Environment.DIALECT );
 		return instantiateDialect( dialectName );
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
 		String dialectName = props.getProperty( Environment.DIALECT );
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
 			return ( Dialect ) ReflectHelper.classForName( dialectName ).newInstance();
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
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		String result = typeNames.get( code );
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
 		String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length ));
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
 
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
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
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setBinaryStream( 1L );  // the BLOB just read during the load phase of merge
 					InputStream detachedStream = original.getBinaryStream();      // the BLOB from the detached state
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
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the CLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the CLOB from the detached state
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
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the NCLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the NCLOB from the detached state
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
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 		String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
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
 		String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					"No Hibernate type mapping for java.sql.Types code: " +
 					code +
 					", length: " +
 					length
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
 		sqlFunctions.put( name.toLowerCase(), function );
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
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add(word);
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
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
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return TableHiLoGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
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
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
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
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 	/**
 	 * Build delegate managing LIMIT clause.
 	 *
 	 * @param sql SQL query.
 	 * @param selection Selection criteria. {@code null} in case of unlimited number of rows.
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler buildLimitHandler(String sql, RowSelection selection) {
 		return new LegacyLimitHandler( this, sql, selection );
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
         switch ( lockMode ) {
             case PESSIMISTIC_FORCE_INCREMENT:
                 return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_WRITE:
                 return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_READ:
                 return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
             case OPTIMISTIC:
                 return new OptimisticLockingStrategy( lockable, lockMode );
             case OPTIMISTIC_FORCE_INCREMENT:
                 return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
             default:
                 return new SelectLockingStrategy( lockable, lockMode );
         }
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
         LockMode lockMode = lockOptions.getLockMode();
         return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
     @SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
        switch ( lockMode ) {
             case UPGRADE:
                 return getForUpdateString();
             case PESSIMISTIC_READ:
                 return getReadLockString( timeout );
             case PESSIMISTIC_WRITE:
                 return getWriteLockString( timeout );
             case UPGRADE_NOWAIT:
             case FORCE:
             case PESSIMISTIC_FORCE_INCREMENT:
                 return getForUpdateNowaitString();
+            case UPGRADE_SKIPLOCKED:
+                return getForUpdateSkipLockedString();
             default:
                 return "";
         }
     }
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan(lockMode) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
+	* Retrieves the <tt>FOR UPDATE SKIP LOCKED</tt> syntax specific to this dialect.
+	*
+	* @return The appropriate <tt>FOR UPDATE SKIP LOCKED</tt> clause string.
+	*/
+	public String getForUpdateSkipLockedString() {
+		// by default we report no support for SKIP_LOCKED lock semantics
+		return getForUpdateString();
+	}
+
+	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
+ 	/**
+	 * Get the <tt>FOR UPDATE OF column_list SKIP LOCKED</tt> fragment appropriate
+	 * for this dialect given the aliases of the columns to be write locked.
+	 *
+	 * @param aliases The columns to be write locked.
+	 * @return The appropriate <tt>FOR UPDATE colunm_list SKIP LOCKED</tt> clause string.
+	 */
+	public String getForUpdateSkipLockedString(String aliases) {
+		return getForUpdateString( aliases );
+	}
+
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 * @deprecated use {@code appendLockHint(LockOptions,String)} instead
 	 */
 	@Deprecated
 	public String appendLockHint(LockMode mode, String tableName) {
 		return appendLockHint( new LockOptions( mode ), tableName );
 	}
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param lockOptions The lock options to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockOptions lockOptions, String tableName){
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return false;
 	}
 
 	/**
 	 * Generate a temporary table name given the base table.
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		return "HT_" + baseTableName;
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Get any fragments needing to be postfixed to the command for
 	 * temporary table creation.
 	 *
 	 * @return Any required postfix.
 	 */
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	/**
 	 * Command used to drop a temporary table.
 	 *
 	 * @return The command used to drop a temporary table.
 	 */
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	/**
 	 * Does the dialect require that temporary table DDL statements occur in
 	 * isolation from other statements?  This would be the case if the creation
 	 * would cause any current transaction to get committed implicitly.
 	 * <p/>
 	 * JDBC defines a standard way to query for this information via the
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
 	 * method.  However, that does not distinguish between temporary table
 	 * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
 	 * transaction commit via its driver, even though that is not the case for
 	 * temporary table DDL.
 	 * <p/>
 	 * Possible return values and their meanings:<ul>
 	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
 	 * in isolation.</li>
 	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
 	 * temporary table DDL in isolation.</li>
 	 * <li><i>null</i> - defer to the JDBC driver response in regards to
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
 	 * </ul>
 	 *
 	 * @return see the result matrix above.
 	 */
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return null;
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by position*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by name*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet}.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
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
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * @return {@code true} if the underlying Database supports case insensitive like comparison, {@code false} otherwise.
 	 * The default is {@code false}.
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
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation command
 	 */
 	public String getCreateSchemaCommand(String schemaName) {
 		return "create schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop command
 	 */
 	public String getDropSchemaCommand(String schemaName) {
 		return "drop schema " + schemaName;
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
 	 * Do we need to drop constraints before dropping tables in this dialect?
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
 		StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( constraintName )
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
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
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
 
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 	
 	public String getDropTableString( String tableName ) {
 		StringBuilder buf = new StringBuilder( "drop table " );
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
 	 * @return Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
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
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase() );
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
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 	
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
-	
+
 	public String getNotExpression( String expression ) {
 		return "not " + expression;
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
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
index 9086ec00d5..8ec1d401a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle10gDialect.java
@@ -1,48 +1,66 @@
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
 package org.hibernate.dialect;
+import org.hibernate.LockOptions;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.JoinFragment;
 
 
 /**
  * A dialect specifically for use with Oracle 10g.
  * <p/>
  * The main difference between this dialect and {@link Oracle9iDialect}
  * is the use of "ANSI join syntax".  This dialect also retires the use
  * of the <tt>oracle.jdbc.driver</tt> package in favor of 
  * <tt>oracle.jdbc</tt>.
  *
  * @author Steve Ebersole
  */
 public class Oracle10gDialect extends Oracle9iDialect {
 
 	public Oracle10gDialect() {
 		super();
 	}
 
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
+
+	public String getWriteLockString(int timeout) {
+		if ( timeout == LockOptions.SKIP_LOCKED ) {
+			return  getForUpdateSkipLockedString();
+		}
+		else {
+			return super.getWriteLockString(timeout);
+		}
+	}
+
+	public String getForUpdateSkipLockedString() {
+		return " for update skip locked";
+	}
+
+	public String getForUpdateSkipLockedString(String aliases) {
+		return getForUpdateString() + " of " + aliases + " skip locked";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
index 613257219d..0ae137cb5f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
@@ -1,203 +1,205 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Microsoft SQL Server 2000
  *
  * @author Gavin King
  */
 public class SQLServerDialect extends AbstractTransactSQLDialect {
 	
 	private static final int PARAM_LIST_SIZE_LIMIT = 2100;
 
 	public SQLServerDialect() {
 		registerColumnType( Types.VARBINARY, "image" );
 		registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "image" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.BOOLEAN, "bit" );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(second, ?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(minute, ?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(hour, ?1)" ) );
 		registerFunction( "locate", new StandardSQLFunction( "charindex", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(?1, ?3)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1) * 8" ) );
 
 		registerFunction( "trim", new AnsiTrimEmulationFunction() );
 
 		registerKeyword( "top" );
 	}
 
 	@Override
     public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	static int getAfterSelectInsertPoint(String sql) {
 		int selectIndex = sql.toLowerCase().indexOf( "select" );
 		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
 		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
 	}
 
 	@Override
     public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
 				.toString();
 	}
 
 	/**
 	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
 	 */
 	@Override
     public String appendIdentitySelectToInsert(String insertSQL) {
 		return insertSQL + " select scope_identity()";
 	}
 
 	@Override
     public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
     public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
     public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
     public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
     public char closeQuote() {
 		return ']';
 	}
 
 	@Override
     public char openQuote() {
 		return '[';
 	}
 
 	@Override
     public String appendLockHint(LockOptions lockOptions, String tableName) {
 		LockMode mode = lockOptions.getLockMode();
 		switch ( mode ) {
 			case UPGRADE:
 			case UPGRADE_NOWAIT:
 			case PESSIMISTIC_WRITE:
 			case WRITE:
 				return tableName + " with (updlock, rowlock)";
 			case PESSIMISTIC_READ:
 				return tableName + " with (holdlock, rowlock)";
+            case UPGRADE_SKIPLOCKED:
+                return tableName + " with (updlock, rowlock, readpast)";
 			default:
 				return tableName;
 		}
 	}
 
 	// The current_timestamp is more accurate, but only known to be supported
 	// in SQL Server 7.0 and later (i.e., Sybase not known to support it at all)
 	@Override
     public String getCurrentTimestampSelectString() {
 		return "select current_timestamp";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
     public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
     public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
 	@Override
     public boolean supportsCircularCascadeDeleteConstraints() {
 		// SQL Server (at least up through 2005) does not support defining
 		// cascade delete constraints which can circle back to the mutating
 		// table
 		return false;
 	}
 
 	@Override
     public boolean supportsLobValueChangePropogation() {
 		// note: at least my local SQL Server 2005 Express shows this not working...
 		return false;
 	}
 
 	@Override
     public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false; // here assume SQLServer2005 using snapshot isolation, which does not have this problem
 	}
 
 	@Override
     public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false; // here assume SQLServer2005 using snapshot isolation, which does not have this problem
 	}
 
     /**
      * {@inheritDoc}
      *
      * @see org.hibernate.dialect.Dialect#getSqlTypeDescriptorOverride(int)
      */
     @Override
     protected SqlTypeDescriptor getSqlTypeDescriptorOverride( int sqlCode ) {
         return sqlCode == Types.TINYINT ? SmallIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride(sqlCode);
     }
 
 	/* (non-Javadoc)
 		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
 		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java b/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
index 6beb3c40f6..daea92d3d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/lock/AbstractSelectLockingStrategy.java
@@ -1,72 +1,88 @@
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
 package org.hibernate.dialect.lock;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.persister.entity.Lockable;
 
 /**
  * Base {@link LockingStrategy} implementation to support implementations
  * based on issuing <tt>SQL</tt> <tt>SELECT</tt> statements
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractSelectLockingStrategy implements LockingStrategy {
 	private final Lockable lockable;
 	private final LockMode lockMode;
 	private final String waitForeverSql;
 
 	protected AbstractSelectLockingStrategy(Lockable lockable, LockMode lockMode) {
 		this.lockable = lockable;
 		this.lockMode = lockMode;
 		this.waitForeverSql = generateLockString( LockOptions.WAIT_FOREVER );
 	}
 
 	protected Lockable getLockable() {
 		return lockable;
 	}
 
 	protected LockMode getLockMode() {
 		return lockMode;
 	}
 
 	protected abstract String generateLockString(int lockTimeout);
 
 	protected String determineSql(int timeout) {
-		return timeout == LockOptions.WAIT_FOREVER
-				? waitForeverSql
-				: timeout == LockOptions.NO_WAIT
-						? getNoWaitSql()
-						: generateLockString( timeout );
+		if ( timeout == LockOptions.WAIT_FOREVER) {
+			return waitForeverSql;
+		}
+		else if ( timeout == LockOptions.NO_WAIT) {
+			return getNoWaitSql();
+		}
+		else if ( timeout == LockOptions.SKIP_LOCKED) {
+			return getSkipLockedSql();
+		}
+		else {
+			return generateLockString( timeout );
+		}
 	}
 
 	private String noWaitSql;
 
 	public String getNoWaitSql() {
 		if ( noWaitSql == null ) {
 			noWaitSql = generateLockString( LockOptions.NO_WAIT );
 		}
 		return noWaitSql;
 	}
+
+	private String skipLockedSql;
+
+	public String getSkipLockedSql() {
+		if ( skipLockedSql == null ) {
+			skipLockedSql = generateLockString( LockOptions.SKIP_LOCKED );
+		}
+		return skipLockedSql;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java b/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java
index 3168734251..fb4362a0ac 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java
@@ -1,105 +1,106 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.internal.util;
 
 import javax.persistence.LockModeType;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.LockMode;
 
 /**
  * Helper to deal with conversions (both directions) between {@link org.hibernate.LockMode} and
  * {@link javax.persistence.LockModeType}.
  *
  * @author Steve Ebersole
  */
 public class LockModeConverter {
 	/**
 	 * Convert from the Hibernate specific LockMode to the JPA defined LockModeType.
 	 *
 	 * @param lockMode The Hibernate LockMode.
 	 *
 	 * @return The JPA LockModeType
 	 */
 	public static LockModeType convertToLockModeType(LockMode lockMode) {
 		if ( lockMode == LockMode.NONE ) {
 			return LockModeType.NONE;
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC || lockMode == LockMode.READ ) {
 			return LockModeType.OPTIMISTIC;
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT || lockMode == LockMode.WRITE ) {
 			return LockModeType.OPTIMISTIC_FORCE_INCREMENT;
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return LockModeType.PESSIMISTIC_READ;
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE
 				|| lockMode == LockMode.UPGRADE
-				|| lockMode == LockMode.UPGRADE_NOWAIT ) {
+				|| lockMode == LockMode.UPGRADE_NOWAIT
+                || lockMode == LockMode.UPGRADE_SKIPLOCKED) {
 			return LockModeType.PESSIMISTIC_WRITE;
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT
 				|| lockMode == LockMode.FORCE ) {
 			return LockModeType.PESSIMISTIC_FORCE_INCREMENT;
 		}
 		throw new AssertionFailure( "unhandled lock mode " + lockMode );
 	}
 
 
 	/**
 	 * Convert from JPA defined LockModeType to Hibernate specific LockMode.
 	 *
 	 * @param lockMode The JPA LockModeType
 	 *
 	 * @return The Hibernate LockMode.
 	 */
 	public static LockMode convertToLockMode(LockModeType lockMode) {
 		switch ( lockMode ) {
 			case READ:
 			case OPTIMISTIC: {
 				return LockMode.OPTIMISTIC;
 			}
 			case OPTIMISTIC_FORCE_INCREMENT:
 			case WRITE: {
 				return LockMode.OPTIMISTIC_FORCE_INCREMENT;
 			}
 			case PESSIMISTIC_READ: {
 				return LockMode.PESSIMISTIC_READ;
 			}
 			case PESSIMISTIC_WRITE: {
 				return LockMode.PESSIMISTIC_WRITE;
 			}
 			case PESSIMISTIC_FORCE_INCREMENT: {
 				return LockMode.PESSIMISTIC_FORCE_INCREMENT;
 			}
 			case NONE: {
 				return LockMode.NONE;
 			}
 			default: {
 				throw new AssertionFailure( "Unknown LockModeType: " + lockMode );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 0d07bbc6e8..b935327103 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1272 +1,1274 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
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
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
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
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
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
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected static interface AfterLoadAction {
 		public void afterLoad(SessionImplementor session, Object entity, Loadable persister);
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
-			LOG.usingFollowOnLocking();
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
-			lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
-			lockOptions.setScope( parameters.getLockOptions().getScope() );
-			afterLoadActions.add(
-					new AfterLoadAction() {
-						@Override
-						public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
-							( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
+			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
+				LOG.usingFollowOnLocking();
+				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
+				lockOptions.setScope( parameters.getLockOptions().getScope() );
+				afterLoadActions.add(
+						new AfterLoadAction() {
+							@Override
+							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
+								( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
+							}
 						}
-					}
-			);
-			parameters.setLockOptions( new LockOptions() );
-			return true;
+				);
+				parameters.setLockOptions( new LockOptions() );
+				return true;
+			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuilder( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
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
 			final SessionImplementor session,
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
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
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
 	        final SessionImplementor session,
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
 				if ( ! keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0] )
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session) throws SQLException {
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
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
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
 			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
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
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
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
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
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
 				// physical row after the "last processed", we need to jump
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
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
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
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
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
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
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
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
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final ResultSet rs = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final Statement st = rs.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
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
 			LOG.debugf( "Result set row: %s", count );
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
 				subselectResultKeys.add(keys);
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
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
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
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
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
 			final SessionImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
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
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 		
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
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
 							throw new HibernateException( "Could not locate EntityEntry immediately after two-phase load" );
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
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
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 	        final EntityKey[] keys,
 	        final Loadable[] persisters,
 	        final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 	        final Object optionalOwner,
 	        final Serializable optionalKey,
 	        final CollectionPersister persister,
 	        final CollectionAliases descriptor,
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index 521ffac762..e1caffb218 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,288 +1,289 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.criteria;
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
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
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
 	private final Set querySpaces;
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
 
 		initFromWalker(walker);
 		
 		userAliases = walker.getUserAliases();
 		resultTypes = walker.getResultTypes();
 		includeInResultRow = walker.includeInResultRow();
 		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
 		postInstantiate();
 
 	}
 	
 	public ScrollableResults scroll(SessionImplementor session, ScrollMode scrollMode) 
 	throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode(scrollMode);
 		return scroll(qp, resultTypes, null, session);
 	}
 
 	public List list(SessionImplementor session) 
 	throws HibernateException {
 		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 	throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session),
 				getResultRowAliases()
 		);
 	}
 			
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		final Object[] result;
 		if ( translator.hasProjection() ) {
 			Type[] types = translator.getProjectedTypes();
 			result = new Object[types.length];
 			String[] columnAliases = translator.getProjectedColumnAliases();
 			for ( int i=0, pos=0; i<result.length; i++ ) {
 				int numColumns = types[i].getColumnSpan( session.getFactory() );
 				if ( numColumns > 1 ) {
 			    	String[] typeColumnAliases = ArrayHelper.slice( columnAliases, pos, numColumns );
 					result[i] = types[i].nullSafeGet(rs, typeColumnAliases, session, null);
 				}
 				else {
 					result[i] = types[i].nullSafeGet(rs, columnAliases[pos], session, null);
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
 			Object[] result = new Object[ resultRowLength ];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInResultRow[i] ) result[j++] = row[i];
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
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		if ( dialect.useFollowOnLocking() ) {
-			// Dialect prefers to perform locking in a separate step
-			LOG.usingFollowOnLocking();
-
-			final LockMode lockMode = determineFollowOnLockMode( lockOptions );
-			final LockOptions lockOptionsToUse = new LockOptions( lockMode );
-			lockOptionsToUse.setTimeOut( lockOptions.getTimeOut() );
-			lockOptionsToUse.setScope( lockOptions.getScope() );
-
-			afterLoadActions.add(
-					new AfterLoadAction() {
-						@Override
-						public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
-							( (Session) session ).buildLockRequest( lockOptionsToUse )
-									.lock( persister.getEntityName(), entity );
-						}
-					}
-			);
-			parameters.setLockOptions( new LockOptions() );
-			return sql;
+            final LockMode lockMode = determineFollowOnLockMode( lockOptions );
+            if( lockMode != LockMode.UPGRADE_SKIPLOCKED ) {
+				// Dialect prefers to perform locking in a separate step
+				LOG.usingFollowOnLocking();
+
+				final LockOptions lockOptionsToUse = new LockOptions( lockMode );
+				lockOptionsToUse.setTimeOut( lockOptions.getTimeOut() );
+				lockOptionsToUse.setScope( lockOptions.getScope() );
+
+				afterLoadActions.add(
+						new AfterLoadAction() {
+								@Override
+								public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
+									( (Session) session ).buildLockRequest( lockOptionsToUse )
+										.lock( persister.getEntityName(), entity );
+								}
+				        }
+				);
+				parameters.setLockOptions( new LockOptions() );
+				return sql;
+			}
 		}
-
 		final LockOptions locks = new LockOptions(lockOptions.getLockMode());
 		locks.setScope( lockOptions.getScope());
 		locks.setTimeOut( lockOptions.getTimeOut());
 
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 		final String[] drivingSqlAliases = getAliases();
 		for ( int i = 0; i < drivingSqlAliases.length; i++ ) {
 			final LockMode lockMode = lockOptions.getAliasSpecificLockMode( drivingSqlAliases[i] );
 			if ( lockMode != null ) {
 				final Lockable drivingPersister = ( Lockable ) getEntityPersisters()[i];
 				final String rootSqlAlias = drivingPersister.getRootTableAlias( drivingSqlAliases[i] );
 				locks.setAliasSpecificLockMode( rootSqlAlias, lockMode );
 				if ( keyColumnNames != null ) {
 					keyColumnNames.put( rootSqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 				}
 			}
 		}
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.getAliasLockCount() > 1 ) {
 			// > 1 here because criteria always uses alias map for the root lock mode (under 'this_')
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		final String[] entityAliases = getAliases();
 		if ( entityAliases == null ) {
 			return null;
 		}
 		final int size = entityAliases.length;
 		LockMode[] lockModesArray = new LockMode[size];
 		for ( int i=0; i<size; i++ ) {
 			LockMode lockMode = lockOptions.getAliasSpecificLockMode( entityAliases[i] );
 			lockModesArray[i] = lockMode==null ? lockOptions.getLockMode() : lockMode;
 		}
 		return lockModesArray;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 	
 	protected String getQueryIdentifier() { 
 		return "[CRITERIA] " + getSQLString(); 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index d38e31b7ac..8beed0980b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -913,3942 +913,3949 @@ public abstract class AbstractEntityPersister
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		i = 0;
 		boolean foundFormula = false;
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			thisClassProperties.add( singularAttributeBinding );
 
 			propertySubclassNames[i] = ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName();
 
 			int span = singularAttributeBinding.getSimpleValueSpan();
 			propertyColumnSpans[i] = span;
 
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			boolean[] propertyColumnInsertability = new boolean[span];
 			boolean[] propertyColumnUpdatability = new boolean[span];
 
 			int k = 0;
 
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				colAliases[k] = valueBinding.getSimpleValue().getAlias( factory.getDialect() );
 				if ( valueBinding.isDerived() ) {
 					foundFormula = true;
 					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) valueBinding.getSimpleValue() ).getExpression(), factory );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) valueBinding.getSimpleValue();
 					colNames[k] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colReaderTemplates[k] = getTemplateFromColumn( col, factory );
 					colWriters[k] = col.getWriteFragment() == null ? "?" : col.getWriteFragment();
 				}
 				propertyColumnInsertability[k] = valueBinding.isIncludeInInsert();
 				propertyColumnUpdatability[k] = valueBinding.isIncludeInUpdate();
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			propertyColumnUpdateable[i] = propertyColumnUpdatability;
 			propertyColumnInsertable[i] = propertyColumnInsertability;
 
 			if ( lazyAvailable && singularAttributeBinding.isLazy() ) {
 				lazyProperties.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNames.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping());
 				lazyColAliases.add( colAliases );
 			}
 
 
 			// TODO: fix this when backrefs are working
 			//propertySelectable[i] = singularAttributeBinding.isBackRef();
 			propertySelectable[i] = true;
 
 			propertyUniqueness[i] = singularAttributeBinding.isAlternateUniqueKey();
 			
 			// TODO: Does this need AttributeBindings wired into lobProperties?  Currently in Property only.
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		List<String> columns = new ArrayList<String>();
 		List<Boolean> columnsLazy = new ArrayList<Boolean>();
 		List<String> columnReaderTemplates = new ArrayList<String>();
 		List<String> aliases = new ArrayList<String>();
 		List<String> formulas = new ArrayList<String>();
 		List<String> formulaAliases = new ArrayList<String>();
 		List<String> formulaTemplates = new ArrayList<String>();
 		List<Boolean> formulasLazy = new ArrayList<Boolean>();
 		List<Type> types = new ArrayList<Type>();
 		List<String> names = new ArrayList<String>();
 		List<String> classes = new ArrayList<String>();
 		List<String[]> templates = new ArrayList<String[]>();
 		List<String[]> propColumns = new ArrayList<String[]>();
 		List<String[]> propColumnReaders = new ArrayList<String[]>();
 		List<String[]> propColumnReaderTemplates = new ArrayList<String[]>();
 		List<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
 		List<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
 		List<Boolean> definedBySubclass = new ArrayList<Boolean>();
 		List<int[]> propColumnNumbers = new ArrayList<int[]>();
 		List<int[]> propFormulaNumbers = new ArrayList<int[]>();
 		List<Boolean> columnSelectables = new ArrayList<Boolean>();
 		List<Boolean> propNullables = new ArrayList<Boolean>();
 
 		for ( AttributeBinding attributeBinding : entityBinding.getSubEntityAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			names.add( singularAttributeBinding.getAttribute().getName() );
 			classes.add( ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName() );
 			boolean isDefinedBySubclass = ! thisClassProperties.contains( singularAttributeBinding );
 			definedBySubclass.add( isDefinedBySubclass );
 			propNullables.add( singularAttributeBinding.isNullable() || isDefinedBySubclass ); //TODO: is this completely correct?
 			types.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 			final int span = singularAttributeBinding.getSimpleValueSpan();
 			String[] cols = new String[ span ];
 			String[] readers = new String[ span ];
 			String[] readerTemplates = new String[ span ];
 			String[] forms = new String[ span ];
 			int[] colnos = new int[ span ];
 			int[] formnos = new int[ span ];
 			int l = 0;
 			Boolean lazy = singularAttributeBinding.isLazy() && lazyAvailable;
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( valueBinding.isDerived() ) {
 					DerivedValue derivedValue = DerivedValue.class.cast( valueBinding.getSimpleValue() );
 					String template = getTemplateFromString( derivedValue.getExpression(), factory );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( derivedValue.getExpression() );
 					formulaAliases.add( derivedValue.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = org.hibernate.metamodel.relational.Column.class.cast( valueBinding.getSimpleValue() );
 					String colName = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( col.getAlias( factory.getDialect() ) );
 					columnsLazy.add( lazy );
 					// TODO: properties only selectable if they are non-plural???
 					columnSelectables.add( singularAttributeBinding.getAttribute().isSingular() );
 
 					readers[l] =
 							col.getReadFragment() == null ?
 									col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 									col.getReadFragment();
 					String readerTemplate = getTemplateFromColumn( col, factory );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			if ( singularAttributeBinding.isAssociation() ) {
 				AssociationAttributeBinding associationAttributeBinding =
 						( AssociationAttributeBinding ) singularAttributeBinding;
 				cascades.add( associationAttributeBinding.getCascadeStyle() );
 				joinedFetchesList.add( associationAttributeBinding.getFetchMode() );
 			}
 			else {
 				cascades.add( CascadeStyles.NONE );
 				joinedFetchesList.add( FetchMode.SELECT );
 			}
 		}
 
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = cascades.toArray( new CascadeStyle[ cascades.size() ] );
 		subclassPropertyFetchModeClosure = joinedFetchesList.toArray( new FetchMode[ joinedFetchesList.size() ] );
 
 		propertyDefinedOnSubclass = ArrayHelper.toBooleanArray( definedBySubclass );
 
 		List<FilterConfiguration> filterDefaultConditions = new ArrayList<FilterConfiguration>();
 		for ( FilterDefinition filterDefinition : entityBinding.getFilterDefinitions() ) {
 			filterDefaultConditions.add(new FilterConfiguration(filterDefinition.getFilterName(), 
 						filterDefinition.getDefaultFilterCondition(), true, null, null, null));
 		}
 		filterHelper = new FilterHelper( filterDefaultConditions, factory);
 
 		temporaryIdTableName = null;
 		temporaryIdTableDDL = null;
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	public String getTemplateFromColumn(org.hibernate.metamodel.relational.Column column, SessionFactoryImplementor factory) {
 		String templateString;
 		if ( column.getReadFragment() != null ) {
 			templateString = getTemplateFromString( column.getReadFragment(), factory );
 		}
 		else {
 			String columnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			templateString = Template.TEMPLATE + '.' + columnName;
 		}
 		return templateString;
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString( this, id, getFactory() ), fieldName );
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
 		if ( !hasLazyProperties() ) throw new AssertionFailure( "no lazy properties" );
 
 		LOG.trace( "Initializing lazy properties from datastore" );
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 				);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
 		LOG.trace( "Initializing lazy properties from second-level cache" );
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
 		LOG.trace( "Done initializing lazy properties" );
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}
 
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current persistent state for: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) throws HibernateException {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"resolving unique key [%s] to identifier for entity [%s]",
 					key,
 					getEntityName()
 			);
 		}
 
 		int propertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		if ( propertyIndex < 0 ) {
 			throw new HibernateException(
 					"Could not determine Type for property [" + uniquePropertyName + "] on entity [" + getEntityName() + "]"
 			);
 		}
 		Type propertyType = getSubclassPropertyType( propertyIndex );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve unique property [%s] to identifier for entity [%s]",
 							uniquePropertyName,
 							getEntityName()
 					),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	protected String generateIdByUniqueKeySelectString(String uniquePropertyName) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "resolve id by unique property [" + getEntityName() + "." + uniquePropertyName + "]" );
 		}
 
 		final String rooAlias = getRootAlias();
 
 		select.setFromClause( fromTableFragment( rooAlias ) + fromJoinFragment( rooAlias, true, false ) );
 
 		SelectFragment selectFragment = new SelectFragment();
 		selectFragment.addColumns( rooAlias, getIdentifierColumnNames(), getIdentifierAliases() );
 		select.setSelectClause( selectFragment );
 
 		StringBuilder whereClauseBuffer = new StringBuilder();
 		final int uniquePropertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		final String uniquePropertyTableAlias = generateTableAlias(
 				rooAlias,
 				getSubclassPropertyTableNumber( uniquePropertyIndex )
 		);
 		String sep = "";
 		for ( String columnTemplate : getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex] ) {
 			if ( columnTemplate == null ) {
 				continue;
 			}
 			final String columnReference = StringHelper.replace( columnTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace( formulaTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( formulaReference ).append( "=?" );
 			sep = " and ";
 		}
 		whereClauseBuffer.append( whereJoinFragment( rooAlias, true, false ) );
 
 		select.setWhereClause( whereClauseBuffer.toString() );
 
 		return select.setOuterJoins( "", "" ).toStatementString();
 	}
 
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
 	}
 
 	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
 					// ValueInclusion.PARTIAL would indicate parts of a component need to
 					// be included in the select; currently we then just render the entire
 					// component into the select clause in that case.
 					public boolean includeProperty(int propertyNumber) {
 						return inclusions[propertyNumber] != ValueInclusion.NONE;
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
+		lockers.put( LockMode.UPGRADE_SKIPLOCKED, generateLocker( LockMode.UPGRADE_SKIPLOCKED ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	public static String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	@Override
 	public String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 
 	//This is really ugly, but necessary:
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(EntityBinding model) throws MappingException {
 
 		// ALIASES
 
 		// TODO: Fix when subclasses are working (HHH-6337)
 		//internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = ( Component ) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = ( Selectable ) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex(propertyName);
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping );
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockMode, getFactory(), loadQueryInfluencers );
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoaderBuilder.getBuilder( getFactory() )
 				.buildLoader( this, batchSize, lockOptions, getFactory(), loadQueryInfluencers );
 	}
 
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) 
 					&& !lobProperties.contains( i ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i),
 						propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				update.addColumns( getPropertyColumnNames(i),
 						propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = true;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 					? getPropertyUpdateability() //optimistic-lock="all", include all updatable properties
 					: includeProperty; 			 //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert,
 			boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i] );
 			}
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session,
 			boolean isUpdate) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1, isUpdate );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index,
 	        boolean isUpdate ) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i )) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( !isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 
 		return index;
 
 	}
 	
 	private int dehydrateId( 
 			final Serializable id,
 			final Object rowId,
 			final PreparedStatement ps,
 	        final SessionImplementor session,
 			int index ) throws SQLException {
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			return 1;
 		} else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			return getIdentifierColumnSpan();
 		}
 		return 0;
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Hydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( sequentialSelect );
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialResultSet );
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialSelect );
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0} (native id)", getEntityName() );
 			if ( isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session, false );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() )
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index, false );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( insert ), insert, -1 );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( insert );
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index, true );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( update ), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( update );
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( delete ), id, j, expectation, delete );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( delete );
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
         if ( LOG.isDebugEnabled() ) {
             LOG.debugf( "Static SQL for entity: %s", getEntityName() );
             if ( sqlLazySelectString != null ) {
 				LOG.debugf( " Lazy select: %s", sqlLazySelectString );
 			}
             if ( sqlVersionSelectString != null ) {
 				LOG.debugf( " Version select: %s", sqlVersionSelectString );
 			}
             if ( sqlSnapshotSelectString != null ) {
 				LOG.debugf( " Snapshot select: %s", sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf( " Insert %s: %s", j, getSQLInsertStrings()[j] );
                 LOG.debugf( " Update %s: %s", j, getSQLUpdateStrings()[j] );
                 LOG.debugf( " Delete %s: %s", j, getSQLDeleteStrings()[j] );
 			}
             if ( sqlIdentityInsertString != null ) {
 				LOG.debugf( " Identity insert: %s", sqlIdentityInsertString );
 			}
             if ( sqlUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (all fields): %s", sqlUpdateByRowIdString );
 			}
             if ( sqlLazyUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (non-lazy fields): %s", sqlLazyUpdateByRowIdString );
 			}
             if ( sqlInsertGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Insert-generated property select: %s", sqlInsertGeneratedValuesSelectString );
 			}
             if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Update-generated property select: %s", sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
+				LockMode.UPGRADE_SKIPLOCKED,
+				disableForUpdate ?
+						readLoader :
+						createEntityLoader( LockMode.UPGRADE_SKIPLOCKED )
+			);
+		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingActions.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingActions.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryHelper.getCacheEntryStructure();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 		return cacheEntryHelper.buildCacheEntry( entity, state, version, session );
 	}
 
 	public boolean hasNaturalIdCache() {
 		return naturalIdRegionAccessStrategy != null;
 	}
 	
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( ! hasNaturalIdentifier() ) {
 			return;
 		}
 
 		if ( getEntityMetamodel().hasImmutableNaturalId() ) {
 			// we assume there were no changes to natural id during detachment for now, that is validated later
 			// during flush.
 			return;
 		}
 
 		final NaturalIdHelper naturalIdHelper = session.getPersistenceContext().getNaturalIdHelper();
 		final Serializable id = getIdentifier( entity, session );
 
 		// for reattachment of mutable natural-ids, we absolutely positively have to grab the snapshot from the
 		// database, because we have no other way to know if the state changed while detached.
 		final Object[] naturalIdSnapshot;
 		final Object[] entitySnapshot = session.getPersistenceContext().getDatabaseSnapshot( id, this );
 		if ( entitySnapshot == StatefulPersistenceContext.NO_ROW ) {
 			naturalIdSnapshot = null;
 		}
 		else {
 			naturalIdSnapshot = naturalIdHelper.extractNaturalIdValues( entitySnapshot, this );
 		}
 
 		naturalIdHelper.removeSharedNaturalIdCrossReference( this, id, naturalIdSnapshot );
 		naturalIdHelper.manageLocalNaturalIdCrossReference(
 				this,
 				id,
 				naturalIdHelper.extractNaturalIdValues( entity, this ),
 				naturalIdSnapshot,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( hasCache() ) {
 			CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i] );
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 					}
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 			);
 		}
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		final boolean[] valueNullness = determineValueNullness( naturalIdValues );
 		final String sqlEntityIdByNaturalIdString = determinePkByNaturalIdQuery( valueNullness );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Object naturalIdValue = naturalIdValues[loop++];
 					if ( naturalIdValue != null ) {
 						final Type type = getPropertyTypes()[idPosition];
 						type.nullSafeSet( ps, naturalIdValue, positions, session );
 						positions += type.getColumnSpan( session.getFactory() );
 					}
 				}
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private boolean[] determineValueNullness(Object[] naturalIdValues) {
 		boolean[] nullness = new boolean[ naturalIdValues.length ];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( ! hasNaturalIdentifier() ) {
 			throw new HibernateException( "Attempt to build natural-id -> PK resolution query for entity that does not define natural id" );
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && ! ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
index 375199aac9..71895a88dd 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ForUpdateFragment.java
@@ -1,122 +1,139 @@
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
 package org.hibernate.sql;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Gavin King
  */
 public class ForUpdateFragment {
 	private final StringBuilder aliases = new StringBuilder();
 	private boolean isNowaitEnabled;
+	private boolean isSkipLockedEnabled;
 	private final Dialect dialect;
 	private LockMode lockMode;
 	private LockOptions lockOptions;
 
 	public ForUpdateFragment(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	public ForUpdateFragment(Dialect dialect, LockOptions lockOptions, Map keyColumnNames) throws QueryException {
 		this( dialect );
 		LockMode upgradeType = null;
 		Iterator iter = lockOptions.getAliasLockIterator();
 		this.lockOptions =  lockOptions;
 
 		if ( !iter.hasNext()) {  // no tables referenced
 			final LockMode lockMode = lockOptions.getLockMode();
 			if ( LockMode.READ.lessThan( lockMode ) ) {
 				upgradeType = lockMode;
 				this.lockMode = lockMode;
 			}
 		}
 
 		while ( iter.hasNext() ) {
 			final Map.Entry me = ( Map.Entry ) iter.next();
 			final LockMode lockMode = ( LockMode ) me.getValue();
 			if ( LockMode.READ.lessThan( lockMode ) ) {
 				final String tableAlias = ( String ) me.getKey();
 				if ( dialect.forUpdateOfColumns() ) {
 					String[] keyColumns = ( String[] ) keyColumnNames.get( tableAlias ); //use the id column alias
 					if ( keyColumns == null ) {
 						throw new IllegalArgumentException( "alias not found: " + tableAlias );
 					}
 					keyColumns = StringHelper.qualify( tableAlias, keyColumns );
 					for ( int i = 0; i < keyColumns.length; i++ ) {
 						addTableAlias( keyColumns[i] );
 					}
 				}
 				else {
 					addTableAlias( tableAlias );
 				}
 				if ( upgradeType != null && lockMode != upgradeType ) {
 					throw new QueryException( "mixed LockModes" );
 				}
 				upgradeType = lockMode;
 			}
 		}
 
 		if ( upgradeType == LockMode.UPGRADE_NOWAIT ) {
 			setNowaitEnabled( true );
 		}
+
+		if ( upgradeType == LockMode.UPGRADE_SKIPLOCKED ) {
+			setSkipLockedEnabled( true );
+		}
 	}
 
 	public ForUpdateFragment addTableAlias(String alias) {
 		if ( aliases.length() > 0 ) {
 			aliases.append( ", " );
 		}
 		aliases.append( alias );
 		return this;
 	}
 
 	public String toFragmentString() {
 		if ( lockOptions!= null ) {
 			return dialect.getForUpdateString( aliases.toString(), lockOptions );
 		}
 		else if ( aliases.length() == 0) {
 			if ( lockMode != null ) {
 				return dialect.getForUpdateString( lockMode );
 			}
 			return "";
 		}
 		// TODO:  pass lockmode
-		return isNowaitEnabled ?
-				dialect.getForUpdateNowaitString( aliases.toString() ) :
-				dialect.getForUpdateString( aliases.toString() );
+		if(isNowaitEnabled) {
+			return dialect.getForUpdateNowaitString( aliases.toString() );
+		}
+		else if (isSkipLockedEnabled) {
+			return dialect.getForUpdateSkipLockedString( aliases.toString() );
+		}
+		else {
+			return dialect.getForUpdateString( aliases.toString() );
+		}
 	}
 
 	public ForUpdateFragment setNowaitEnabled(boolean nowait) {
 		isNowaitEnabled = nowait;
 		return this;
 	}
+
+	public ForUpdateFragment setSkipLockedEnabled(boolean skipLocked) {
+		isSkipLockedEnabled = skipLocked;
+		return this;
+	}
+
 }
diff --git a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd
index 5a3706a537..01ff3d390c 100644
--- a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd
+++ b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-3.0.dtd
@@ -1,1070 +1,1070 @@
 <!-- Hibernate Mapping DTD.
 
 <!DOCTYPE hibernate-mapping PUBLIC 
     "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
     "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 An instance of this XML document may contain mappings for an arbitrary 
 number of classes. The class mappings may contain associations to classes
 mapped in the same document or in another document. No class may be 
 mapped more than once. Each document may also contain definitions of an
 arbitrary number of queries, and import declarations of arbitrary classes. 
 
 -->
 
 <!--
 	The document root.
  -->
 
 <!ELEMENT hibernate-mapping (
 	meta*,
 	identifier-generator*,
 	typedef*,
 	filter-def*,
 	import*,
 	(class|subclass|joined-subclass|union-subclass)*,
     resultset*,
 	(query|sql-query)*,
 	filter-def*,
 	fetch-profile*,
     database-object*
 )>
 	<!ATTLIST hibernate-mapping schema CDATA #IMPLIED>									<!-- default: none -->
 	<!ATTLIST hibernate-mapping catalog CDATA #IMPLIED>									<!-- default: none -->
 	<!ATTLIST hibernate-mapping default-cascade CDATA "none">
 	<!ATTLIST hibernate-mapping default-access CDATA "property">
 	<!ATTLIST hibernate-mapping default-lazy (true|false) "true">
 	<!ATTLIST hibernate-mapping auto-import (true|false) "true">
 	<!ATTLIST hibernate-mapping package CDATA #IMPLIED>									<!-- default: none -->
 
 <!--
 	<meta.../> is used to assign meta-level attributes to a class
 	or property.  Is currently used by codegenerator as a placeholder for
 	values that is not directly related to OR mappings.
 -->
 <!ELEMENT meta (#PCDATA)>
 	<!ATTLIST meta attribute CDATA #REQUIRED>
 	<!ATTLIST meta inherit (true|false) "true">
 
 <!--
     <identifier-generator.../> allows customized short-naming of IdentifierGenerator implementations.
 -->
 <!ELEMENT identifier-generator EMPTY>
     <!ATTLIST identifier-generator name CDATA #REQUIRED>
     <!ATTLIST identifier-generator class CDATA #REQUIRED>
 
 <!--
 	<typedef.../> allows defining a customized type mapping for a Hibernate type. May
 	contain parameters for parameterizable types.
 -->
 <!ELEMENT typedef (param*)>
 	<!ATTLIST typedef class CDATA #REQUIRED>
 	<!ATTLIST typedef name CDATA #REQUIRED>
 
 <!--
 	IMPORT element definition; an explicit query language "import"
 -->
 <!ELEMENT import EMPTY>
 	<!ATTLIST import class CDATA #REQUIRED>
 	<!ATTLIST import rename CDATA #IMPLIED>	<!-- default: unqualified class name -->
 
 <!--
 	Root entity mapping.  Poorly named as entities do not have to be represented by 
 	classes at all.  Mapped entities may be represented via different methodologies 
 	(POJO, Map, Dom4j).
 -->
 <!ELEMENT class (
  	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
     tuplizer*,
 	(id|composite-id),
 	discriminator?,
 	natural-id?,
 	(version|timestamp)?,
 	(property|many-to-one|one-to-one|component|dynamic-component|properties|any|map|set|list|bag|idbag|array|primitive-array)*,
 	((join*,subclass*)|joined-subclass*|union-subclass*),
 	loader?,sql-insert?,sql-update?,sql-delete?,
 	filter*,
     fetch-profile*,
     resultset*,
 	(query|sql-query)*
 )>
 	<!ATTLIST class entity-name CDATA #IMPLIED>
 	<!ATTLIST class name CDATA #IMPLIED>                            <!-- this is the class name -->
 	<!ATTLIST class proxy CDATA #IMPLIED>							<!-- default: no proxy interface -->
 	<!ATTLIST class lazy (true|false) #IMPLIED>
 	<!ATTLIST class table CDATA #IMPLIED>							<!-- default: unqualified classname -->
 	<!ATTLIST class schema CDATA #IMPLIED>							<!-- default: none -->
 	<!ATTLIST class catalog CDATA #IMPLIED>							<!-- default: none -->
 	<!ATTLIST class subselect CDATA #IMPLIED>
 	<!ATTLIST class discriminator-value CDATA #IMPLIED>				<!-- default: unqualified class name | none -->
 	<!ATTLIST class mutable (true|false) "true">
 	<!ATTLIST class abstract (true|false) #IMPLIED>
 	<!ATTLIST class polymorphism (implicit|explicit) "implicit">
 	<!ATTLIST class where CDATA #IMPLIED>							<!-- default: none -->
 	<!ATTLIST class persister CDATA #IMPLIED>
 	<!ATTLIST class dynamic-update (true|false) "false">
 	<!ATTLIST class dynamic-insert (true|false) "false">
 	<!ATTLIST class batch-size CDATA #IMPLIED>
 	<!ATTLIST class select-before-update (true|false) "false">
 	<!ATTLIST class optimistic-lock (none|version|dirty|all) "version">
 	<!ATTLIST class check CDATA #IMPLIED>							<!-- default: none -->
 	<!ATTLIST class rowid CDATA #IMPLIED>
 	<!ATTLIST class node CDATA #IMPLIED>
 
 <!--
     TUPLIZER element; defines tuplizer to use for a component/entity for a given entity-mode
 -->
 <!ELEMENT tuplizer EMPTY>
     <!ATTLIST tuplizer entity-mode (pojo|dom4j|dynamic-map) #IMPLIED>   <!-- entity mode for which tuplizer is in effect -->
     <!ATTLIST tuplizer class CDATA #REQUIRED>                           <!-- the tuplizer class to use -->
 
 <!--
 	FILTER-DEF element; top-level filter definition.
 -->
 <!ELEMENT filter-def (#PCDATA|filter-param)*>
 	<!ATTLIST filter-def name CDATA #REQUIRED> <!-- The filter name -->
 	<!ATTLIST filter-def condition CDATA #IMPLIED>
 
 <!--
 	FILTER-PARAM element; qualifies parameters found within a FILTER-DEF
 	condition.
 -->
 <!ELEMENT filter-param EMPTY>
 	<!ATTLIST filter-param name CDATA #REQUIRED> <!-- The parameter name -->
 	<!ATTLIST filter-param type CDATA #REQUIRED> <!-- The parameter type -->
 
 <!--
 	FILTER element; used to apply a filter.
 -->
 <!ELEMENT filter (#PCDATA)>
 	<!ATTLIST filter name CDATA #REQUIRED>
 	<!ATTLIST filter condition CDATA #IMPLIED>
 
 <!--
 -->
 <!ELEMENT fetch-profile (fetch*)>
     <!ATTLIST fetch-profile name CDATA #REQUIRED>
 
 <!--
     The <fetch> element defines a single path to which the fetch
     refers, as well as the style of fetch to apply.  The 'root' of the
     path is different depending upon the context in which the
     containing <fetch-profile/> occurs; within a <class/> element,
     the entity-name of the containing class mapping is assumed...
 -->
 <!ELEMENT fetch EMPTY>
     <!ATTLIST fetch entity CDATA #IMPLIED> <!-- Implied as long as the containing fetch profile is contained in a class mapping -->
     <!ATTLIST fetch association CDATA #REQUIRED>
     <!ATTLIST fetch style (join|select) "join">
 
 <!-- A join allows some properties of a class to be persisted to a second table -->
 
 <!ELEMENT join ( 
 	subselect?,
 	comment?,
 	key,
 	(property|many-to-one|component|dynamic-component|any)*,
 	sql-insert?,sql-update?,sql-delete?
 )>
 	<!ATTLIST join table CDATA #REQUIRED>
 	<!ATTLIST join schema CDATA #IMPLIED>						<!-- default: none -->
 	<!ATTLIST join catalog CDATA #IMPLIED>						<!-- default: none -->
 	<!ATTLIST join subselect CDATA #IMPLIED>
 	<!ATTLIST join fetch (join|select) "join">
 	<!ATTLIST join inverse (true|false) "false">
 	<!ATTLIST join optional (true|false) "false">
 
 <!-- A natural-id element allows declaration of the unique business key -->
 
 <!ELEMENT natural-id ( (property|many-to-one|component|dynamic-component|any)* )>
 	<!ATTLIST natural-id mutable (true|false) "false">
 
 <!-- Declares the id type, column and generation algorithm for an entity class.
 If a name attribut is given, the id is exposed to the application through the 
 named property of the class. If not, the id is only exposed to the application 
 via Session.getIdentifier() -->
 
 <!ELEMENT id (meta*,column*,type?,generator?)>
 	<!ATTLIST id name CDATA #IMPLIED>
 	<!ATTLIST id node CDATA #IMPLIED>
 	<!ATTLIST id access CDATA #IMPLIED>
 	<!ATTLIST id column CDATA #IMPLIED>
 	<!ATTLIST id type CDATA #IMPLIED>
 	<!ATTLIST id length CDATA #IMPLIED>
 	<!ATTLIST id unsaved-value CDATA #IMPLIED>					<!-- any|none|null|undefined|0|-1|... -->
 
 <!-- A composite key may be modelled by a java class with a property for each 
 key column. The class must implement java.io.Serializable and reimplement equals() 
 and hashCode(). -->
 
 <!ELEMENT composite-id ( meta*, (key-property|key-many-to-one)+, generator? )>
 	<!ATTLIST composite-id class CDATA #IMPLIED>
 	<!ATTLIST composite-id mapped (true|false) "false">
 	<!ATTLIST composite-id name CDATA #IMPLIED>
 	<!ATTLIST composite-id node CDATA #IMPLIED>
 	<!ATTLIST composite-id access CDATA #IMPLIED>
 	<!ATTLIST composite-id unsaved-value (undefined|any|none) "undefined"> 
 
 <!-- Polymorphic data requires a column holding a class discriminator value. This
 value is not directly exposed to the application. -->
 
 <!ELEMENT discriminator ((column|formula)?)>
 	<!ATTLIST discriminator column CDATA #IMPLIED>				<!-- default: "class"|none -->
 	<!ATTLIST discriminator formula CDATA #IMPLIED>
 	<!ATTLIST discriminator type CDATA "string">
 	<!ATTLIST discriminator not-null (true|false) "true">
 	<!ATTLIST discriminator length CDATA #IMPLIED>
 	<!ATTLIST discriminator force (true|false) "false">
 	<!ATTLIST discriminator insert (true|false) "true">
 	
 <!-- Versioned data requires a column holding a version number. This is exposed to the
 application through a property of the Java class. -->
 
 <!ELEMENT version (meta*,column*)>
 	<!ATTLIST version name CDATA #REQUIRED>
 	<!ATTLIST version node CDATA #IMPLIED>
 	<!ATTLIST version access CDATA #IMPLIED>
 	<!ATTLIST version column CDATA #IMPLIED>
 	<!ATTLIST version type CDATA "integer">
 	<!ATTLIST version unsaved-value (null|negative|undefined) "undefined">
     <!ATTLIST version generated (never|always) "never">
     <!ATTLIST version insert (true|false) #IMPLIED>
 
 <!ELEMENT timestamp (meta*)>
 	<!ATTLIST timestamp name CDATA #REQUIRED>
 	<!ATTLIST timestamp node CDATA #IMPLIED>
 	<!ATTLIST timestamp column CDATA #IMPLIED>
 	<!ATTLIST timestamp access CDATA #IMPLIED>
 	<!ATTLIST timestamp unsaved-value (null|undefined) "null">
     <!ATTLIST timestamp source (vm|db) "vm">
     <!ATTLIST timestamp generated (never|always) "never">
 
 
 <!--
 	Subclass declarations are nested beneath the root class declaration to achieve
 	polymorphic persistence with the table-per-hierarchy mapping strategy.
 
 	See the note on the class element regarding <pojo/> vs. @name usage...
 -->
 <!ELEMENT subclass (
  	meta*,
     tuplizer*,
 	synchronize*,
 	(property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|idbag|array|primitive-array)*,
 	join*, 
 	subclass*,
 	loader?,sql-insert?,sql-update?,sql-delete?,
     fetch-profile*,
     resultset*,
 	(query|sql-query)*
 )>
 	<!ATTLIST subclass entity-name CDATA #IMPLIED>
 	<!ATTLIST subclass name CDATA #IMPLIED>
 	<!ATTLIST subclass proxy CDATA #IMPLIED>							<!-- default: no proxy interface -->
 	<!ATTLIST subclass discriminator-value CDATA #IMPLIED>				<!-- default: unqualified class name | none -->
 	<!ATTLIST subclass dynamic-update (true|false) "false">
 	<!ATTLIST subclass dynamic-insert (true|false) "false">
 	<!ATTLIST subclass select-before-update (true|false) "false">
 	<!ATTLIST subclass extends CDATA #IMPLIED>							<!-- default: empty when a toplevel, otherwise the nearest class definition -->
 	<!ATTLIST subclass lazy (true|false) #IMPLIED>
 	<!ATTLIST subclass abstract (true|false) #IMPLIED>
 	<!ATTLIST subclass persister CDATA #IMPLIED>
 	<!ATTLIST subclass batch-size CDATA #IMPLIED>
 	<!ATTLIST subclass node CDATA #IMPLIED>
 
 <!--
 	Joined subclasses are used for the normalized table-per-subclass mapping strategy
 
 	See the note on the class element regarding <pojo/> vs. @name usage...
 -->
 <!ELEMENT joined-subclass (
 	meta*,
 	subselect?,
 	synchronize*,
 	comment?,
     tuplizer*,
 	key,
 	(property|many-to-one|one-to-one|component|dynamic-component|properties|any|map|set|list|bag|idbag|array|primitive-array)*, 
 	joined-subclass*,
 	loader?,sql-insert?,sql-update?,sql-delete?,
     fetch-profile*,
     resultset*,
 	(query|sql-query)*
 )>
 	<!ATTLIST joined-subclass entity-name CDATA #IMPLIED>
 	<!ATTLIST joined-subclass name CDATA #IMPLIED>
 	<!ATTLIST joined-subclass proxy CDATA #IMPLIED>				 		<!-- default: no proxy interface -->
 	<!ATTLIST joined-subclass table CDATA #IMPLIED>				 		<!-- default: unqualified class name -->
 	<!ATTLIST joined-subclass schema CDATA #IMPLIED>
 	<!ATTLIST joined-subclass catalog CDATA #IMPLIED>
 	<!ATTLIST joined-subclass subselect CDATA #IMPLIED>
 	<!ATTLIST joined-subclass dynamic-update (true|false) "false">
 	<!ATTLIST joined-subclass dynamic-insert (true|false) "false">
 	<!ATTLIST joined-subclass select-before-update (true|false) "false">
 	<!ATTLIST joined-subclass extends CDATA #IMPLIED>			 		<!-- default: none when toplevel, otherwise the nearest class definition -->
 	<!ATTLIST joined-subclass lazy (true|false) #IMPLIED>
 	<!ATTLIST joined-subclass abstract (true|false) #IMPLIED>
 	<!ATTLIST joined-subclass persister CDATA #IMPLIED>
 	<!ATTLIST joined-subclass check CDATA #IMPLIED>				 		<!-- default: none -->
 	<!ATTLIST joined-subclass batch-size CDATA #IMPLIED>
 	<!ATTLIST joined-subclass node CDATA #IMPLIED>
 
 <!--
 	Union subclasses are used for the table-per-concrete-class mapping strategy
 
 	See the note on the class element regarding <pojo/> vs. @name usage...
 -->
 <!ELEMENT union-subclass (
  	meta*,
 	subselect?,
 	synchronize*,
 	comment?,
     tuplizer*,
 	(property|many-to-one|one-to-one|component|dynamic-component|properties|any|map|set|list|bag|idbag|array|primitive-array)*,
 	union-subclass*,
 	loader?,sql-insert?,sql-update?,sql-delete?,
     fetch-profile*,
     resultset*,
 	(query|sql-query)*
 )>
 	<!ATTLIST union-subclass entity-name CDATA #IMPLIED>
 	<!ATTLIST union-subclass name CDATA #IMPLIED>
 	<!ATTLIST union-subclass proxy CDATA #IMPLIED>						<!-- default: no proxy interface -->
 	<!ATTLIST union-subclass table CDATA #IMPLIED>						<!-- default: unqualified class name -->
 	<!ATTLIST union-subclass schema CDATA #IMPLIED>
 	<!ATTLIST union-subclass catalog CDATA #IMPLIED>
 	<!ATTLIST union-subclass subselect CDATA #IMPLIED>
 	<!ATTLIST union-subclass dynamic-update (true|false) "false">
 	<!ATTLIST union-subclass dynamic-insert (true|false) "false">
 	<!ATTLIST union-subclass select-before-update (true|false) "false">
 	<!ATTLIST union-subclass extends CDATA #IMPLIED>					<!-- default: none when toplevel, otherwise the nearest class definition -->
 	<!ATTLIST union-subclass lazy (true|false) #IMPLIED>
 	<!ATTLIST union-subclass abstract (true|false) #IMPLIED>
 	<!ATTLIST union-subclass persister CDATA #IMPLIED>
 	<!ATTLIST union-subclass check CDATA #IMPLIED>						<!-- default: none -->
 	<!ATTLIST union-subclass batch-size CDATA #IMPLIED>
 	<!ATTLIST union-subclass node CDATA #IMPLIED>
 
 <!-- Property of an entity class or component, component-element, composite-id, etc. 
 JavaBeans style properties are mapped to table columns. -->
 
 <!ELEMENT property (meta*,(column|formula)*,type?)>
 	<!ATTLIST property name CDATA #REQUIRED>
 	<!ATTLIST property node CDATA #IMPLIED>
 	<!ATTLIST property access CDATA #IMPLIED>
 	<!ATTLIST property type CDATA #IMPLIED>
 	<!ATTLIST property column CDATA #IMPLIED>
 	<!ATTLIST property length CDATA #IMPLIED>
 	<!ATTLIST property precision CDATA #IMPLIED>
 	<!ATTLIST property scale CDATA #IMPLIED>
 	<!ATTLIST property not-null (true|false) #IMPLIED>
 	<!ATTLIST property unique (true|false) "false">
 	<!ATTLIST property unique-key CDATA #IMPLIED>
 	<!ATTLIST property index CDATA #IMPLIED>				<!-- include the columns spanned by this property in an index -->
 	<!ATTLIST property update (true|false) #IMPLIED>
 	<!ATTLIST property insert (true|false) #IMPLIED>
 	<!ATTLIST property optimistic-lock (true|false) "true">	<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST property formula CDATA #IMPLIED>
 	<!ATTLIST property lazy (true|false) "false">
     <!ATTLIST property generated (never|insert|always) "never">
 
 <!-- Declares the type of the containing property (overrides an eventually existing type
 attribute of the property). May contain param elements to customize a ParametrizableType. -->
 <!ELEMENT type (param*)>
 	<!ATTLIST type name CDATA #REQUIRED>
 	
 <!-- Declares an association between two entities (Or from a component, component element,
 etc. to an entity). -->
 
 <!ELEMENT many-to-one (meta*,(column|formula)*)>
 	<!ATTLIST many-to-one name CDATA #REQUIRED>
 	<!ATTLIST many-to-one access CDATA #IMPLIED>
 	<!ATTLIST many-to-one class CDATA #IMPLIED>
 	<!ATTLIST many-to-one entity-name CDATA #IMPLIED>
 	<!ATTLIST many-to-one column CDATA #IMPLIED>
 	<!ATTLIST many-to-one not-null (true|false) #IMPLIED>
 	<!ATTLIST many-to-one unique (true|false) "false">
 	<!ATTLIST many-to-one unique-key CDATA #IMPLIED>
 	<!ATTLIST many-to-one index CDATA #IMPLIED>
 	<!ATTLIST many-to-one cascade CDATA #IMPLIED>
 	<!ATTLIST many-to-one outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST many-to-one fetch (join|select) #IMPLIED>
 	<!ATTLIST many-to-one update (true|false) "true">
 	<!ATTLIST many-to-one insert (true|false) "true">
 	<!ATTLIST many-to-one optimistic-lock (true|false) "true">	<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST many-to-one foreign-key CDATA #IMPLIED>
 	<!ATTLIST many-to-one property-ref CDATA #IMPLIED>
 	<!ATTLIST many-to-one formula CDATA #IMPLIED>
 	<!ATTLIST many-to-one lazy (false|proxy|no-proxy) #IMPLIED>
 	<!ATTLIST many-to-one not-found (exception|ignore) "exception">
 	<!ATTLIST many-to-one node CDATA #IMPLIED>
 	<!ATTLIST many-to-one embed-xml (true|false) "true">
 	
 <!-- Declares a one-to-one association between two entities (Or from a component, 
 component element, etc. to an entity). -->
 
 <!ELEMENT one-to-one (meta*,formula*)>
 	<!ATTLIST one-to-one name CDATA #REQUIRED>
 	<!ATTLIST one-to-one formula CDATA #IMPLIED>
 	<!ATTLIST one-to-one access CDATA #IMPLIED>
 	<!ATTLIST one-to-one class CDATA #IMPLIED>
 	<!ATTLIST one-to-one entity-name CDATA #IMPLIED>
 	<!ATTLIST one-to-one cascade CDATA #IMPLIED>
 	<!ATTLIST one-to-one outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST one-to-one fetch (join|select) #IMPLIED>
 	<!ATTLIST one-to-one constrained (true|false) "false">
 	<!ATTLIST one-to-one foreign-key CDATA #IMPLIED>
 	<!ATTLIST one-to-one property-ref CDATA #IMPLIED>
 	<!ATTLIST one-to-one lazy (false|proxy|no-proxy) #IMPLIED>
 	<!ATTLIST one-to-one node CDATA #IMPLIED>
 	<!ATTLIST one-to-one embed-xml (true|false) "true">
 	
 <!-- A property embedded in a composite identifier or map index (always not-null). -->
 
 <!ELEMENT key-property (meta*,column*,type?)>
 	<!ATTLIST key-property name CDATA #REQUIRED>
 	<!ATTLIST key-property access CDATA #IMPLIED>
 	<!ATTLIST key-property type CDATA #IMPLIED>
 	<!ATTLIST key-property column CDATA #IMPLIED>
 	<!ATTLIST key-property length CDATA #IMPLIED>
 	<!ATTLIST key-property node CDATA #IMPLIED>
 
 <!-- A many-to-one association embedded in a composite identifier or map index 
 (always not-null, never cascade). -->
 
 <!ELEMENT key-many-to-one (meta*,column*)>
 	<!ATTLIST key-many-to-one name CDATA #REQUIRED>
 	<!ATTLIST key-many-to-one access CDATA #IMPLIED>
 	<!ATTLIST key-many-to-one class CDATA #IMPLIED>
 	<!ATTLIST key-many-to-one entity-name CDATA #IMPLIED>
 	<!ATTLIST key-many-to-one column CDATA #IMPLIED>
 	<!ATTLIST key-many-to-one foreign-key CDATA #IMPLIED>
 	<!ATTLIST key-many-to-one lazy (false|proxy) #IMPLIED>
 
 <!-- An "any" association is a polymorphic association to any table with
 the given identifier type. The first listed column is a VARCHAR column 
 holding the name of the class (for that row). -->
 
 <!ELEMENT any (meta*,meta-value*,column,column+)>
 	<!ATTLIST any id-type CDATA #REQUIRED>
 	<!ATTLIST any meta-type CDATA #IMPLIED>			 	<!--- default: Hibernate.STRING -->
 	<!ATTLIST any name CDATA #REQUIRED>
 	<!ATTLIST any access CDATA #IMPLIED>
 	<!ATTLIST any insert (true|false) "true">
 	<!ATTLIST any update (true|false) "true">
 	<!ATTLIST any cascade CDATA #IMPLIED>
 	<!ATTLIST any index CDATA #IMPLIED>					<!-- include the columns spanned by this association in an index -->
 	<!ATTLIST any optimistic-lock (true|false) "true">	<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST any lazy (true|false) "false">
 	<!ATTLIST any node CDATA #IMPLIED>
 	
 <!ELEMENT meta-value EMPTY>
 	<!ATTLIST meta-value value CDATA #REQUIRED>
 	<!ATTLIST meta-value class CDATA #REQUIRED>
 
 <!-- A component is a user-defined class, persisted along with its containing entity
 to the table of the entity class. JavaBeans style properties of the component are
 mapped to columns of the table of the containing entity. A null component reference
 is mapped to null values in all columns and vice versa. Components do not support
 shared reference semantics. -->
 
 <!ELEMENT component (
 	meta*,
     tuplizer*,
 	parent?,
 	(property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|array|primitive-array)*
 )>
 	<!ATTLIST component class CDATA #IMPLIED>
 	<!ATTLIST component name CDATA #REQUIRED>
 	<!ATTLIST component access CDATA #IMPLIED>
 	<!ATTLIST component unique (true|false) "false">
 	<!ATTLIST component update (true|false) "true">
 	<!ATTLIST component insert (true|false) "true">
 	<!ATTLIST component lazy (true|false) "false">
 	<!ATTLIST component optimistic-lock (true|false) "true">
 	<!ATTLIST component node CDATA #IMPLIED>
 	
 <!-- A dynamic-component maps columns of the database entity to a java.util.Map 
 at the Java level -->
 
 <!ELEMENT dynamic-component (
 	(property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|array|primitive-array)*
 )>
 	<!ATTLIST dynamic-component name CDATA #REQUIRED>
 	<!ATTLIST dynamic-component access CDATA #IMPLIED>
 	<!ATTLIST dynamic-component unique (true|false) "false">
 	<!ATTLIST dynamic-component update (true|false) "true">
 	<!ATTLIST dynamic-component insert (true|false) "true">
 	<!ATTLIST dynamic-component optimistic-lock (true|false) "true">
 	<!ATTLIST dynamic-component node CDATA #IMPLIED>
 
 <!-- properties declares that the contained properties form an alternate key. The name
 attribute allows an alternate key to be used as the target of a property-ref. -->
 
 <!ELEMENT properties (
 	(property|many-to-one|component|dynamic-component)*
 )>
 	<!ATTLIST properties name CDATA #REQUIRED>
 	<!ATTLIST properties unique (true|false) "false">
 	<!ATTLIST properties insert (true|false) "true">
 	<!ATTLIST properties update (true|false) "true">
 	<!ATTLIST properties optimistic-lock (true|false) "true">
 	<!ATTLIST properties node CDATA #IMPLIED>
 	
 <!-- The parent element maps a property of the component class as a pointer back to
 the owning entity. -->
 
 <!ELEMENT parent EMPTY>
 	<!ATTLIST parent name CDATA #REQUIRED>
 
 <!-- Collection declarations nested inside a class declaration indicate a foreign key 
 relationship from the collection table to the enclosing class. -->
 
 <!ELEMENT map (
 	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
 	key, 
 	(map-key|composite-map-key|map-key-many-to-many|index|composite-index|index-many-to-many|index-many-to-any), 
 	(element|one-to-many|many-to-many|composite-element|many-to-any),
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
 	filter*
 )>
 	<!ATTLIST map name CDATA #REQUIRED>
 	<!ATTLIST map access CDATA #IMPLIED>
 	<!ATTLIST map table CDATA #IMPLIED>																<!-- default: name -->
 	<!ATTLIST map schema CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST map subselect CDATA #IMPLIED>
 	<!ATTLIST map catalog CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST map lazy (true|false|extra) #IMPLIED>
 	<!ATTLIST map mutable (true|false) "true">
 	<!ATTLIST map inverse (true|false) "false">
 	<!ATTLIST map sort CDATA "unsorted">														 	<!-- unsorted|natural|"comparator class", default: unsorted -->
 	<!ATTLIST map cascade CDATA #IMPLIED>
 	<!ATTLIST map order-by CDATA #IMPLIED>													 		<!-- default: none -->
 	<!ATTLIST map where CDATA #IMPLIED>																<!-- default: none -->
 	<!ATTLIST map batch-size CDATA #IMPLIED>
 	<!ATTLIST map outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST map fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST map check CDATA #IMPLIED>																<!-- default: none -->	
 	<!ATTLIST map persister CDATA #IMPLIED>														
 	<!ATTLIST map collection-type CDATA #IMPLIED>	
 	<!ATTLIST map optimistic-lock (true|false) "true">		<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST map node CDATA #IMPLIED>
 	<!ATTLIST map embed-xml (true|false) "true">
 	
 <!ELEMENT set (
 	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
 	key, 
 	(element|one-to-many|many-to-many|composite-element|many-to-any),
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
 	filter*
 )>
 	<!ATTLIST set name CDATA #REQUIRED>
 	<!ATTLIST set access CDATA #IMPLIED>
 	<!ATTLIST set table CDATA #IMPLIED>																<!-- default: name -->
 	<!ATTLIST set schema CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST set catalog CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST set subselect CDATA #IMPLIED>
 	<!ATTLIST set lazy (true|false|extra) #IMPLIED>
 	<!ATTLIST set sort CDATA "unsorted">														 	<!-- unsorted|natural|"comparator class" -->
 	<!ATTLIST set inverse (true|false) "false">
 	<!ATTLIST set mutable (true|false) "true">
 	<!ATTLIST set cascade CDATA #IMPLIED>
 	<!ATTLIST set order-by CDATA #IMPLIED>													 		<!-- default: none -->
 	<!ATTLIST set where CDATA #IMPLIED>																<!-- default: none -->
 	<!ATTLIST set batch-size CDATA #IMPLIED>
 	<!ATTLIST set outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST set fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST set persister CDATA #IMPLIED>	
 	<!ATTLIST set collection-type CDATA #IMPLIED>														
 	<!ATTLIST set check CDATA #IMPLIED>																<!-- default: none -->
 	<!ATTLIST set optimistic-lock (true|false) "true">		<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST set node CDATA #IMPLIED>
 	<!ATTLIST set embed-xml (true|false) "true">
 
 <!ELEMENT bag (
 	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
 	key, 
 	(element|one-to-many|many-to-many|composite-element|many-to-any),
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
 	filter*
 )>
 	<!ATTLIST bag name CDATA #REQUIRED>
 	<!ATTLIST bag access CDATA #IMPLIED>
 	<!ATTLIST bag table CDATA #IMPLIED>																<!-- default: name -->
 	<!ATTLIST bag schema CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST bag catalog CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST bag subselect CDATA #IMPLIED>
 	<!ATTLIST bag lazy (true|false|extra) #IMPLIED>
 	<!ATTLIST bag inverse (true|false) "false">
 	<!ATTLIST bag mutable (true|false) "true">
 	<!ATTLIST bag cascade CDATA #IMPLIED>
 	<!ATTLIST bag order-by CDATA #IMPLIED>													 		<!-- default: none -->
 	<!ATTLIST bag where CDATA #IMPLIED>																<!-- default: none -->
 	<!ATTLIST bag batch-size CDATA #IMPLIED>
 	<!ATTLIST bag outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST bag fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST bag persister CDATA #IMPLIED>															
 	<!ATTLIST bag collection-type CDATA #IMPLIED>	
 	<!ATTLIST bag check CDATA #IMPLIED>																<!-- default: none -->
 	<!ATTLIST bag optimistic-lock (true|false) "true">		<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST bag node CDATA #IMPLIED>
 	<!ATTLIST bag embed-xml (true|false) "true">
 
 <!ELEMENT idbag (
 	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
 	collection-id,
 	key, 
 	(element|many-to-many|composite-element|many-to-any),
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
 	filter*
 )>
 	<!ATTLIST idbag name CDATA #REQUIRED>
 	<!ATTLIST idbag access CDATA #IMPLIED>
 	<!ATTLIST idbag table CDATA #IMPLIED>															<!-- default: name -->
 	<!ATTLIST idbag schema CDATA #IMPLIED>														 	<!-- default: none -->
 	<!ATTLIST idbag catalog CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST idbag subselect CDATA #IMPLIED>
 	<!ATTLIST idbag lazy (true|false|extra) #IMPLIED>
 	<!ATTLIST idbag mutable (true|false) "true">
 	<!ATTLIST idbag cascade CDATA #IMPLIED>
 	<!ATTLIST idbag order-by CDATA #IMPLIED>													 	<!-- default: none -->
 	<!ATTLIST idbag where CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST idbag batch-size CDATA #IMPLIED>
 	<!ATTLIST idbag outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST idbag fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST idbag persister CDATA #IMPLIED>															
 	<!ATTLIST idbag collection-type CDATA #IMPLIED>
 	<!ATTLIST idbag check CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST idbag optimistic-lock (true|false) "true">	<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST idbag node CDATA #IMPLIED>
 	<!ATTLIST idbag embed-xml (true|false) "true">
 
 <!ELEMENT list (
 	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
 	key, 
 	(index|list-index), 
 	(element|one-to-many|many-to-many|composite-element|many-to-any),
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
 	filter*
 )>
 	<!ATTLIST list name CDATA #REQUIRED>
 	<!ATTLIST list access CDATA #IMPLIED>
 	<!ATTLIST list table CDATA #IMPLIED>														 	<!-- default: name -->
 	<!ATTLIST list schema CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST list catalog CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST list subselect CDATA #IMPLIED>
 	<!ATTLIST list lazy (true|false|extra) #IMPLIED>
 	<!ATTLIST list inverse (true|false) "false">
 	<!ATTLIST list mutable (true|false) "true">
 	<!ATTLIST list cascade CDATA #IMPLIED>
 	<!ATTLIST list where CDATA #IMPLIED>														 	<!-- default: none -->
 	<!ATTLIST list batch-size CDATA #IMPLIED>
 	<!ATTLIST list outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST list fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST list persister CDATA #IMPLIED>																
 	<!ATTLIST list collection-type CDATA #IMPLIED>
 	<!ATTLIST list check CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST list optimistic-lock (true|false) "true">		<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST list node CDATA #IMPLIED>
 	<!ATTLIST list embed-xml (true|false) "true">
 
 <!ELEMENT array (
 	meta*,
 	subselect?,
 	cache?,
 	synchronize*,
 	comment?,
 	key, 
 	(index|list-index), 
 	(element|one-to-many|many-to-many|composite-element|many-to-any),
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?
 )>
 	<!ATTLIST array name CDATA #REQUIRED>
 	<!ATTLIST array access CDATA #IMPLIED>
 	<!ATTLIST array table CDATA #IMPLIED>															<!-- default: name -->
 	<!ATTLIST array schema CDATA #IMPLIED>													 		<!-- default: none -->
 	<!ATTLIST array catalog CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST array subselect CDATA #IMPLIED>
 	<!ATTLIST array inverse (true|false) "false">
 	<!ATTLIST array mutable (true|false) "true">
 	<!ATTLIST array element-class CDATA #IMPLIED>
 	<!ATTLIST array cascade CDATA #IMPLIED>
 	<!ATTLIST array where CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST array batch-size CDATA #IMPLIED>
 	<!ATTLIST array outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST array fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST array persister CDATA #IMPLIED>															
 	<!ATTLIST array collection-type CDATA #IMPLIED>
 	<!ATTLIST array check CDATA #IMPLIED>															<!-- default: none -->
 	<!ATTLIST array optimistic-lock (true|false) "true">	<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST array node CDATA #IMPLIED>
 	<!ATTLIST array embed-xml (true|false) "true">
 
 <!ELEMENT primitive-array (
 	meta*, 
 	subselect?,
 	cache?, 
 	synchronize*,
 	comment?,
 	key, 
 	(index|list-index), 
 	element,
 	loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?
 )>
 	<!ATTLIST primitive-array name CDATA #REQUIRED>
 	<!ATTLIST primitive-array access CDATA #IMPLIED>
 	<!ATTLIST primitive-array table CDATA #IMPLIED>									<!-- default: name -->
 	<!ATTLIST primitive-array schema CDATA #IMPLIED>								<!-- default: none -->
 	<!ATTLIST primitive-array catalog CDATA #IMPLIED>								<!-- default: none -->
 	<!ATTLIST primitive-array subselect CDATA #IMPLIED>
 	<!ATTLIST primitive-array mutable (true|false) "true">
 	<!ATTLIST primitive-array where CDATA #IMPLIED>									<!-- default: none -->
 	<!ATTLIST primitive-array batch-size CDATA #IMPLIED>
 	<!ATTLIST primitive-array outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST primitive-array fetch (join|select|subselect) #IMPLIED>
 	<!ATTLIST primitive-array persister CDATA #IMPLIED>																
 	<!ATTLIST primitive-array collection-type CDATA #IMPLIED>
 	<!ATTLIST primitive-array check CDATA #IMPLIED>									<!-- default: none -->
 	<!ATTLIST primitive-array optimistic-lock (true|false) "true">		<!-- only supported for properties of a class (not component) -->
 	<!ATTLIST primitive-array node CDATA #IMPLIED>
 	<!ATTLIST primitive-array embed-xml (true|false) "true">
 
 <!-- Declares the element type of a collection of basic type -->
 
 <!ELEMENT element ( (column|formula)*, type? )>
 	<!ATTLIST element column CDATA #IMPLIED>
 	<!ATTLIST element node CDATA #IMPLIED>
 	<!ATTLIST element formula CDATA #IMPLIED>
 	<!ATTLIST element type CDATA #IMPLIED>
 	<!ATTLIST element length CDATA #IMPLIED>
 	<!ATTLIST element precision CDATA #IMPLIED>
 	<!ATTLIST element scale CDATA #IMPLIED>
 	<!ATTLIST element not-null (true|false) "false">
 	<!ATTLIST element unique (true|false) "false">
 
 <!-- One to many association. This tag declares the entity-class
 element type of a collection and specifies a one-to-many relational model -->
 
 <!ELEMENT one-to-many EMPTY>
 	<!ATTLIST one-to-many class CDATA #IMPLIED>
 	<!ATTLIST one-to-many not-found (exception|ignore) "exception">
 	<!ATTLIST one-to-many node CDATA #IMPLIED>
 	<!ATTLIST one-to-many embed-xml (true|false) "true">
 	<!ATTLIST one-to-many entity-name CDATA #IMPLIED>
 	<!-- No column declaration attributes required in this case. The primary
 	key column of the associated class is already mapped elsewhere.-->
 
 <!-- Many to many association. This tag declares the entity-class
 element type of a collection and specifies a many-to-many relational model -->
 
 <!ELEMENT many-to-many (meta*,(column|formula)*,filter*)>
 	<!ATTLIST many-to-many class CDATA #IMPLIED>
 	<!ATTLIST many-to-many node CDATA #IMPLIED>
 	<!ATTLIST many-to-many embed-xml (true|false) "true">
 	<!ATTLIST many-to-many entity-name CDATA #IMPLIED>
 	<!ATTLIST many-to-many column CDATA #IMPLIED>
 	<!ATTLIST many-to-many formula CDATA #IMPLIED>
 	<!ATTLIST many-to-many not-found (exception|ignore) "exception">
 	<!ATTLIST many-to-many outer-join (true|false|auto) #IMPLIED>
 	<!ATTLIST many-to-many fetch (join|select) #IMPLIED>
 	<!ATTLIST many-to-many lazy (false|proxy) #IMPLIED>
 	<!ATTLIST many-to-many foreign-key CDATA #IMPLIED>
 	<!ATTLIST many-to-many unique (true|false) "false">
 	<!ATTLIST many-to-many where CDATA #IMPLIED>
 	<!ATTLIST many-to-many order-by CDATA #IMPLIED>
 	<!ATTLIST many-to-many property-ref CDATA #IMPLIED>
 
 <!-- A composite element allows a collection to hold instances of an arbitrary 
 class, without the requirement of joining to an entity table. Composite elements
 have component semantics - no shared references and ad hoc null value semantics. 
 Composite elements may not hold nested collections. -->
 
 <!ELEMENT composite-element ( 
 	(meta*),
 	parent?,
 	tuplizer*,
 	(property|many-to-one|any|nested-composite-element)* 
 )>
 	<!ATTLIST composite-element class CDATA #REQUIRED>
 	<!ATTLIST composite-element node CDATA #IMPLIED>
 
 <!ELEMENT nested-composite-element ( 
 	parent?,
 	tuplizer*,
 	(property|many-to-one|any|nested-composite-element)* 
 )>
 	<!ATTLIST nested-composite-element class CDATA #REQUIRED>
 	<!ATTLIST nested-composite-element name CDATA #REQUIRED>
 	<!ATTLIST nested-composite-element access CDATA #IMPLIED>
 	<!ATTLIST nested-composite-element node CDATA #IMPLIED>
 	
 <!-- Declares the column name of a foreign key. -->
 
 <!ELEMENT key (column*)>
 	<!ATTLIST key column CDATA #IMPLIED>
 	<!ATTLIST key property-ref CDATA #IMPLIED>
 	<!ATTLIST key foreign-key CDATA #IMPLIED>
 	<!ATTLIST key on-delete (cascade|noaction) "noaction">
 	<!ATTLIST key not-null (true|false) #IMPLIED>
 	<!ATTLIST key update (true|false) #IMPLIED>
 	<!ATTLIST key unique (true|false) #IMPLIED>
 	
 <!-- Declares the type and column mapping for a collection index (array or
 list index, or key of a map). -->
 
 <!ELEMENT list-index (column?)>
 	<!ATTLIST list-index column CDATA #IMPLIED>
 	<!ATTLIST list-index base CDATA "0">
 
 <!ELEMENT map-key ((column|formula)*,type?)>
 	<!ATTLIST map-key column CDATA #IMPLIED>
 	<!ATTLIST map-key formula CDATA #IMPLIED>
 	<!ATTLIST map-key type CDATA #IMPLIED>
 	<!ATTLIST map-key length CDATA #IMPLIED>
 	<!ATTLIST map-key node CDATA #IMPLIED>
 
 <!ELEMENT index (column*)>
 	<!ATTLIST index column CDATA #IMPLIED>
 	<!ATTLIST index type CDATA #IMPLIED>			<!-- required for maps -->
 	<!ATTLIST index length CDATA #IMPLIED>
 
 <!-- Many to many association mapped to the key of a map. ie. a map keyed
 on entities. -->
 
 <!ELEMENT map-key-many-to-many ((column|formula)*)>
 	<!ATTLIST map-key-many-to-many class CDATA #IMPLIED>
 	<!ATTLIST map-key-many-to-many entity-name CDATA #IMPLIED>
 	<!ATTLIST map-key-many-to-many column CDATA #IMPLIED>
 	<!ATTLIST map-key-many-to-many formula CDATA #IMPLIED>
 	<!ATTLIST map-key-many-to-many foreign-key CDATA #IMPLIED>
 
 <!ELEMENT index-many-to-many (column*)>
 	<!ATTLIST index-many-to-many class CDATA #REQUIRED>
 	<!ATTLIST index-many-to-many entity-name CDATA #IMPLIED>
 	<!ATTLIST index-many-to-many column CDATA #IMPLIED>
 	<!ATTLIST index-many-to-many foreign-key CDATA #IMPLIED>
 
 <!-- Composite index of a map ie. a map keyed on components. -->
 
 <!ELEMENT composite-map-key ( (key-property|key-many-to-one)+ )>
 	<!ATTLIST composite-map-key class CDATA #REQUIRED>
 
 <!ELEMENT composite-index ( (key-property|key-many-to-one)+ )>
 	<!ATTLIST composite-index class CDATA #REQUIRED>
 
 <!-- A "many to any" defines a polymorphic association to any table 
 with the given identifier type. The first listed column is a VARCHAR column 
 holding the name of the class (for that row). -->
 
 <!ELEMENT many-to-any (meta-value*,column, column+)>
 	<!ATTLIST many-to-any id-type CDATA #REQUIRED>
 	<!ATTLIST many-to-any meta-type CDATA #IMPLIED>			<!--- default: Hibernate.CLASS -->
 
 <!ELEMENT index-many-to-any (column, column+)>
 	<!ATTLIST index-many-to-any id-type CDATA #REQUIRED>
 	<!ATTLIST index-many-to-any meta-type CDATA #IMPLIED>	<!--- default: Hibernate.CLASS -->
 
 <!ELEMENT collection-id (meta*, column*, generator)>
 	<!ATTLIST collection-id column CDATA #REQUIRED>
 	<!ATTLIST collection-id type CDATA #REQUIRED>
 	<!ATTLIST collection-id length CDATA #IMPLIED>
 	
 <!-- Generators generate unique identifiers. The class attribute specifies a Java 
 class implementing an id generation algorithm. -->
 
 <!ELEMENT generator (param*)>
 	<!ATTLIST generator class CDATA #REQUIRED>
 <!ELEMENT param (#PCDATA)>
 	<!ATTLIST param name CDATA #REQUIRED>
 
 <!-- The column element is an alternative to column attributes and required for 
 mapping associations to classes with composite ids. -->
 
 <!ELEMENT column (comment?)>
 	<!ATTLIST column name CDATA #REQUIRED>
 	<!ATTLIST column length CDATA #IMPLIED>						<!-- default: 255 -->
 	<!ATTLIST column precision CDATA #IMPLIED>
 	<!ATTLIST column scale CDATA #IMPLIED>
 	<!ATTLIST column not-null (true|false) #IMPLIED>		 	<!-- default: false (except for id properties) -->
 	<!ATTLIST column unique (true|false) #IMPLIED>			 	<!-- default: false (except for id properties) -->
 	<!ATTLIST column unique-key CDATA #IMPLIED>					<!-- default: no unique key -->
 	<!ATTLIST column sql-type CDATA #IMPLIED>					<!-- override default column type for hibernate type -->
 	<!ATTLIST column index CDATA #IMPLIED>
 	<!ATTLIST column check CDATA #IMPLIED>						<!-- default: no check constraint -->
     <!ATTLIST column default CDATA #IMPLIED>                    <!-- default: no default value -->
     <!ATTLIST column read CDATA #IMPLIED>                       <!-- default: column name -->
     <!ATTLIST column write CDATA #IMPLIED>                      <!-- default: parameter placeholder ('?') -->
 
 <!-- The formula and subselect elements allow us to map derived properties and 
 entities. -->
 
 <!ELEMENT formula (#PCDATA)>
 <!ELEMENT subselect (#PCDATA)>
 
 <!-- The cache element enables caching of an entity class. -->
 <!ELEMENT cache EMPTY>
 	<!ATTLIST cache usage (read-only|read-write|nonstrict-read-write|transactional) #REQUIRED>				
 	<!ATTLIST cache region CDATA #IMPLIED>						<!-- default: class or collection role name -->
 	<!ATTLIST cache include (all|non-lazy) "all">
 
 <!-- The comment element allows definition of a database table or column comment. -->
 
 <!ELEMENT comment (#PCDATA)>
 
 <!-- The loader element allows specification of a named query to be used for fetching
 an entity or collection -->
 
 <!ELEMENT loader EMPTY>
 	<!ATTLIST loader query-ref CDATA #REQUIRED>
 
 <!-- The query element declares a named Hibernate query string -->
 
 <!ELEMENT query (#PCDATA|query-param)*>
 	<!ATTLIST query name CDATA #REQUIRED>
 	<!ATTLIST query flush-mode (auto|never|always) #IMPLIED>
 	<!ATTLIST query cacheable (true|false) "false">
 	<!ATTLIST query cache-region CDATA #IMPLIED>
 	<!ATTLIST query fetch-size CDATA #IMPLIED>
 	<!ATTLIST query timeout CDATA #IMPLIED>
 	<!ATTLIST query cache-mode (get|ignore|normal|put|refresh) #IMPLIED>
     <!ATTLIST query read-only (true|false) #IMPLIED>
     <!ATTLIST query comment CDATA #IMPLIED>
 
 <!-- The sql-query element declares a named SQL query string -->
 
 <!ELEMENT sql-query (#PCDATA|return-scalar|return|return-join|load-collection|synchronize|query-param)*>
 	<!ATTLIST sql-query name CDATA #REQUIRED>
     <!ATTLIST sql-query resultset-ref CDATA #IMPLIED>
 	<!ATTLIST sql-query flush-mode (auto|never|always) #IMPLIED>
 	<!ATTLIST sql-query cacheable (true|false) "false">
 	<!ATTLIST sql-query cache-region CDATA #IMPLIED>
 	<!ATTLIST sql-query fetch-size CDATA #IMPLIED>
 	<!ATTLIST sql-query timeout CDATA #IMPLIED>
 	<!ATTLIST sql-query cache-mode (get|ignore|normal|put|refresh) #IMPLIED>
     <!ATTLIST sql-query read-only (true|false) #IMPLIED>
     <!ATTLIST sql-query comment CDATA #IMPLIED>
 	<!ATTLIST sql-query callable (true|false) "false">
 
 <!-- The query-param element is used only by tools that generate
 finder methods for named queries -->
 
 <!ELEMENT query-param EMPTY>
 	<!ATTLIST query-param name CDATA #REQUIRED>
 	<!ATTLIST query-param type CDATA #REQUIRED>
 
 <!-- The resultset element declares a named resultset mapping definition for SQL queries -->
 <!ELEMENT resultset (return-scalar|return|return-join|load-collection)*>
 	<!ATTLIST resultset name CDATA #REQUIRED>
 
 <!--
 	Defines a return component for a sql-query.  Alias refers to the alias
 	used in the actual sql query; lock-mode specifies the locking to be applied
 	when the query is executed.  The class, collection, and role attributes are mutually exclusive;
 	class refers to the class name of a "root entity" in the object result; collection refers
 	to a collection of a given class and is used to define custom sql to load that owned collection
 	and takes the form "ClassName.propertyName"; role refers to the property path for an eager fetch
 	and takes the form "owningAlias.propertyName"
 -->
 <!ELEMENT return (return-discriminator?,return-property)*>
 	<!ATTLIST return alias CDATA #IMPLIED>
 	<!ATTLIST return entity-name CDATA #IMPLIED>
 	<!ATTLIST return class CDATA #IMPLIED>
-	<!ATTLIST return lock-mode (none|read|upgrade|upgrade-nowait|write) "read">	
+	<!ATTLIST return lock-mode (none|read|upgrade|upgrade-nowait|upgrade-skiplocked|write) "read">	
 
 <!ELEMENT return-property (return-column*)> 
 	<!ATTLIST return-property name CDATA #REQUIRED>
 	<!ATTLIST return-property column CDATA #IMPLIED>
 
 <!ELEMENT return-column EMPTY> 
 	<!ATTLIST return-column name CDATA #REQUIRED>
 
 <!ELEMENT return-discriminator EMPTY> 
 	<!ATTLIST return-discriminator column CDATA #REQUIRED>
 	
 <!ELEMENT return-join (return-property)*> 
 	<!ATTLIST return-join alias CDATA #REQUIRED>
 	<!ATTLIST return-join property CDATA #REQUIRED>
-	<!ATTLIST return-join lock-mode (none|read|upgrade|upgrade-nowait|write) "read">
+	<!ATTLIST return-join lock-mode (none|read|upgrade|upgrade-nowait|upgrade-skiplocked|write) "read">
 
 <!ELEMENT load-collection (return-property)*> 
 	<!ATTLIST load-collection alias CDATA #REQUIRED>
 	<!ATTLIST load-collection role CDATA #REQUIRED>
-	<!ATTLIST load-collection lock-mode (none|read|upgrade|upgrade-nowait|write) "read">
+	<!ATTLIST load-collection lock-mode (none|read|upgrade|upgrade-nowait|upgrade-skiplocked|write) "read">
 
 <!ELEMENT return-scalar EMPTY>
 	<!ATTLIST return-scalar column CDATA #REQUIRED>
 	<!ATTLIST return-scalar type CDATA #IMPLIED>
 
 <!ELEMENT synchronize EMPTY>
 	<!ATTLIST synchronize table CDATA #REQUIRED>
 	
 <!-- custom sql operations -->
 <!ELEMENT sql-insert (#PCDATA)>
 	<!ATTLIST sql-insert callable (true|false) "false">
 	<!ATTLIST sql-insert check (none|rowcount|param) #IMPLIED>
 
 <!ELEMENT sql-update (#PCDATA)>
 	<!ATTLIST sql-update callable (true|false) "false">
 	<!ATTLIST sql-update check (none|rowcount|param) #IMPLIED>
 
 <!ELEMENT sql-delete (#PCDATA)>
 	<!ATTLIST sql-delete callable (true|false) "false">
 	<!ATTLIST sql-delete check (none|rowcount|param) #IMPLIED>
 
 <!ELEMENT sql-delete-all (#PCDATA)>
 	<!ATTLIST sql-delete-all callable (true|false) "false">
 	<!ATTLIST sql-delete-all check (none|rowcount|param) #IMPLIED>
 
 <!--
     Element for defining "auxiliary" database objects.  Must be one of two forms:
 
     #1 :
         <database-object>
             <definition class="CustomClassExtendingAuxiliaryObject"/>
         </database-object>
 
     #2 :
         <database-object>
             <create>CREATE OR REPLACE ....</create>
             <drop>DROP ....</drop>
         </database-object>
 -->
 <!ELEMENT database-object ( (definition|(create,drop)), dialect-scope* )>
 
 <!ELEMENT definition EMPTY>
     <!ATTLIST definition class CDATA #REQUIRED>
 
 <!ELEMENT create (#PCDATA)>
 <!ELEMENT drop (#PCDATA)>
 
 <!--
     dialect-scope element allows scoping auxiliary-objects to a particular
     Hibernate dialect implementation.
 -->
 <!ELEMENT dialect-scope (#PCDATA)>
     <!ATTLIST dialect-scope name CDATA #REQUIRED>
 
diff --git a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
index 166c95cc0d..530bff1d22 100644
--- a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
+++ b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
@@ -788,1020 +788,1021 @@ arbitrary number of queries, and import declarations of arbitrary classes.
     <xs:attribute name="fetch" default="join" type="fetch-attribute"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="optional" default="false" type="xs:boolean"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	Joined subclasses are used for the normalized table-per-subclass mapping strategy
   	See the note on the class element regarding <pojo/> vs. @name usage...
   -->
   <xs:complexType name="joined-subclass-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="properties" type="properties-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="idbag" type="idbag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
       <xs:element name="joined-subclass" minOccurs="0" maxOccurs="unbounded" type="joined-subclass-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
       <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
       <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="query" type="query-element"/>
         <xs:element name="sql-query" type="sql-query-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="abstract" type="xs:boolean"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/>
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
     <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="extends" type="xs:string"/> <!-- default: none when toplevel, otherwise the nearest class definition -->
     <xs:attribute name="lazy" type="xs:boolean"/>
     <xs:attribute name="name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
     <xs:attribute name="schema" type="xs:string"/>
     <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: unqualified class name -->
   </xs:complexType>
 
   <!-- Declares the column name of a foreign key. -->
   <xs:complexType name="key-element">
     <xs:sequence>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="not-null" type="xs:boolean"/>
     <xs:attribute name="on-delete" default="noaction">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="cascade"/>
           <xs:enumeration value="noaction"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
     <xs:attribute name="property-ref" type="xs:string"/>
     <xs:attribute name="unique" type="xs:boolean"/>
     <xs:attribute name="update" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- A many-to-one association embedded in a composite identifier or map index 
   (always not-null, never cascade). -->
   <xs:complexType name="key-many-to-one-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="lazy" type="lazy-attribute"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- A property embedded in a composite identifier or map index (always not-null). -->
   <xs:complexType name="key-property-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
       <xs:element name="type" minOccurs="0" type="type-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="list-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="index" type="index-element"/>
         <xs:element name="list-index" type="list-index-element"/>
       </xs:choice>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!-- Declares the type and column mapping for a collection index (array or
   list index, or key of a map). -->
   <xs:complexType name="list-index-element">
     <xs:sequence>
       <xs:element name="column" minOccurs="0" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="base" default="0" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="load-collection-element">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-property" type="return-property-element"/>
     </xs:sequence>
     <xs:attribute name="alias" use="required" type="xs:string"/>
     <xs:attribute name="lock-mode" default="read" type="lock-mode-attribute"/>
     <xs:attribute name="role" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- The loader element allows specification of a named query to be used for fetching
   an entity or collection -->
   <xs:complexType name="loader-element">
     <xs:attribute name="query-ref" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- A "many to any" defines a polymorphic association to any table 
   with the given identifier type. The first listed column is a VARCHAR column 
   holding the name of the class (for that row). -->
   <xs:complexType name="many-to-any-element">
     <xs:sequence>
       <xs:element name="meta-value" minOccurs="0" maxOccurs="unbounded" type="meta-value-element"/>
       <xs:element name="column" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="id-type" use="required" type="xs:string"/>
     <xs:attribute name="meta-type" type="xs:string"/> <!--- default: Hibernate.CLASS -->
   </xs:complexType>
 
   <!-- Many to many association. This tag declares the entity-class
   element type of a collection and specifies a many-to-many relational model -->
   <xs:complexType name="many-to-many-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="fetch" type="fetch-attribute"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="lazy" type="lazy-attribute"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-found" default="exception" type="not-found-attribute"/>
     <xs:attribute name="order-by" type="xs:string"/>
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="property-ref" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="where" type="xs:string"/>
   </xs:complexType>
 
   <!-- Declares an association between two entities (Or from a component, component element,
   etc. to an entity). -->
   <xs:complexType name="many-to-one-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="fetch" type="fetch-attribute"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="index" type="xs:string"/>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-no-proxy"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-found" default="exception" type="not-found-attribute"/>
     <xs:attribute name="not-null" type="xs:boolean"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="property-ref" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="unique-key" type="xs:string"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- Collection declarations nested inside a class declaration indicate a foreign key 
   relationship from the collection table to the enclosing class. -->
   <xs:complexType name="map-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="map-key">
           <xs:complexType>
             <xs:sequence>
               <xs:choice minOccurs="0" maxOccurs="unbounded">
                 <xs:element name="column" type="column-element"/>
                 <!-- The formula and subselect elements allow us to map derived properties and 
                 entities. -->
                 <xs:element name="formula" type="xs:string"/>
               </xs:choice>
               <xs:element name="type" minOccurs="0" type="type-element"/>
             </xs:sequence>
             <xs:attribute name="column" type="xs:string"/>
             <xs:attribute name="formula" type="xs:string"/>
             <xs:attribute name="length" type="xs:string"/>
             <xs:attribute name="node" type="xs:string"/>
             <xs:attribute name="type" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!-- Composite index of a map ie. a map keyed on components. -->
         <xs:element name="composite-map-key">
           <xs:complexType>
             <xs:sequence>
               <xs:choice maxOccurs="unbounded">
                 <xs:element name="key-property" type="key-property-element"/>
                 <xs:element name="key-many-to-one" type="key-many-to-one-element"/>
               </xs:choice>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!-- Many to many association mapped to the key of a map. ie. a map keyed
         on entities. -->
         <xs:element name="map-key-many-to-many">
           <xs:complexType>
             <xs:sequence>
               <xs:choice minOccurs="0" maxOccurs="unbounded">
                 <xs:element name="column" type="column-element"/>
                 <!-- The formula and subselect elements allow us to map derived properties and 
                 entities. -->
                 <xs:element name="formula" type="xs:string"/>
               </xs:choice>
             </xs:sequence>
             <xs:attribute name="class" type="xs:string"/>
             <xs:attribute name="column" type="xs:string"/>
             <xs:attribute name="entity-name" type="xs:string"/>
             <xs:attribute name="foreign-key" type="xs:string"/>
             <xs:attribute name="formula" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <xs:element name="index" type="index-element"/>
         <xs:element name="composite-index">
           <xs:complexType>
             <xs:sequence>
               <xs:choice maxOccurs="unbounded">
                 <xs:element name="key-property" type="key-property-element"/>
                 <xs:element name="key-many-to-one" type="key-many-to-one-element"/>
               </xs:choice>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <xs:element name="index-many-to-many">
           <xs:complexType>
             <xs:sequence>
               <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="column" type="xs:string"/>
             <xs:attribute name="entity-name" type="xs:string"/>
             <xs:attribute name="foreign-key" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <xs:element name="index-many-to-any">
           <xs:complexType>
             <xs:sequence>
               <xs:element name="column" type="column-element"/>
             </xs:sequence>
             <xs:attribute name="id-type" use="required" type="xs:string"/>
             <xs:attribute name="meta-type" type="xs:string"/> <!--- default: Hibernate.CLASS -->
           </xs:complexType>
         </xs:element>
       </xs:choice>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="sort" default="unsorted" type="xs:string"/> <!-- unsorted|natural|"comparator class", default: unsorted -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!--
   	<meta.../> is used to assign meta-level attributes to a class
   	or property.  Is currently used by codegenerator as a placeholder for
   	values that is not directly related to OR mappings.
   -->
   <xs:complexType name="meta-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="attribute" use="required" type="xs:string"/>
         <xs:attribute name="inherit" default="true" type="xs:boolean"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <xs:complexType name="meta-value-element">
     <xs:attribute name="class" use="required" type="xs:string"/>
     <xs:attribute name="value" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="nested-composite-element-element">
     <xs:sequence>
       <xs:element name="parent" minOccurs="0" type="parent-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="nested-composite-element" type="nested-composite-element-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="class" use="required" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
   </xs:complexType>
 
   <!-- One to many association. This tag declares the entity-class
   element type of a collection and specifies a one-to-many relational model -->
   <xs:complexType name="one-to-many-element">
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-found" default="exception" type="not-found-attribute"/>
   </xs:complexType>
 
   <!-- Declares a one-to-one association between two entities (Or from a component, 
   component element, etc. to an entity). -->
   <xs:complexType name="one-to-one-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <!-- The formula and subselect elements allow us to map derived properties and 
       entities. -->
       <xs:element name="formula" minOccurs="0" maxOccurs="unbounded" type="xs:string"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="constrained" default="false" type="xs:boolean"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="fetch" type="fetch-attribute"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-no-proxy"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="property-ref" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="param-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="name" use="required" type="xs:string"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- The parent element maps a property of the component class as a pointer back to
   the owning entity. -->
   <xs:complexType name="parent-element">
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="primitive-array-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="index" type="index-element"/>
         <xs:element name="list-index" type="list-index-element"/>
       </xs:choice>
       <xs:element name="element" type="element-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!-- properties declares that the contained properties form an alternate key. The name
   attribute allows an alternate key to be used as the target of a property-ref. -->
   <xs:complexType name="properties-element">
     <xs:sequence>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- Property of an entity class or component, component-element, composite-id, etc. 
   JavaBeans style properties are mapped to table columns. -->
   <xs:complexType name="property-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
       <xs:element name="type" minOccurs="0" type="type-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="generated" default="never">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="always"/>
           <xs:enumeration value="insert"/>
           <xs:enumeration value="never"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
     <xs:attribute name="index" type="xs:string"/> <!-- include the columns spanned by this property in an index -->
     <xs:attribute name="insert" type="xs:boolean"/>
     <xs:attribute name="lazy" default="false" type="xs:boolean"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-null" type="xs:boolean"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="precision" type="xs:string"/>
     <xs:attribute name="scale" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="unique-key" type="xs:string"/>
     <xs:attribute name="update" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- The query element declares a named Hibernate query string -->
   <xs:complexType name="query-element" mixed="true">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="query-param" type="query-param-element"/>
     </xs:sequence>
     <xs:attribute name="cache-mode" type="cache-mode-attribute"/>
     <xs:attribute name="cache-region" type="xs:string"/>
     <xs:attribute name="cacheable" default="false" type="xs:boolean"/>
     <xs:attribute name="comment" type="xs:string"/>
     <xs:attribute name="fetch-size" type="xs:string"/>
     <xs:attribute name="flush-mode" type="flush-mode-attribute"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="read-only" type="xs:boolean"/>
     <xs:attribute name="timeout" type="xs:string"/>
   </xs:complexType>
 
   <!-- The query-param element is used only by tools that generate
   finder methods for named queries -->
   <xs:complexType name="query-param-element">
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="type" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- The resultset element declares a named resultset mapping definition for SQL queries -->
   <xs:complexType name="resultset-element">
     <xs:choice minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-scalar" type="return-scalar-element"/>
       <xs:element name="return" type="return-element"/>
       <xs:element name="return-join" type="return-join-element"/>
       <xs:element name="load-collection" type="load-collection-element"/>
     </xs:choice>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	Defines a return component for a sql-query.  Alias refers to the alias
   	used in the actual sql query; lock-mode specifies the locking to be applied
   	when the query is executed.  The class, collection, and role attributes are mutually exclusive;
   	class refers to the class name of a "root entity" in the object result; collection refers
   	to a collection of a given class and is used to define custom sql to load that owned collection
   	and takes the form "ClassName.propertyName"; role refers to the property path for an eager fetch
   	and takes the form "owningAlias.propertyName"
   -->
   <xs:complexType name="return-element">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-discriminator" minOccurs="0">
         <xs:complexType>
           <xs:attribute name="column" use="required" type="xs:string"/>
         </xs:complexType>
       </xs:element>
       <xs:element name="return-property" type="return-property-element"/>
     </xs:sequence>
     <xs:attribute name="alias" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="lock-mode" default="read" type="lock-mode-attribute"/>
   </xs:complexType>
 
   <xs:complexType name="return-join-element">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-property" type="return-property-element"/>
     </xs:sequence>
     <xs:attribute name="alias" use="required" type="xs:string"/>
     <xs:attribute name="lock-mode" default="read" type="lock-mode-attribute"/>
     <xs:attribute name="property" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="return-property-element">
     <xs:sequence>
       <xs:element name="return-column" minOccurs="0" maxOccurs="unbounded">
         <xs:complexType>
           <xs:attribute name="name" use="required" type="xs:string"/>
         </xs:complexType>
       </xs:element>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="return-scalar-element">
     <xs:attribute name="column" use="required" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="set-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="sort" default="unsorted" type="xs:string"/> <!-- unsorted|natural|"comparator class" -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <xs:complexType name="sql-delete-all-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <xs:complexType name="sql-delete-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- custom sql operations -->
   <xs:complexType name="sql-insert-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- The sql-query element declares a named SQL query string -->
   <xs:complexType name="sql-query-element" mixed="true">
     <xs:choice minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-scalar" type="return-scalar-element"/>
       <xs:element name="return" type="return-element"/>
       <xs:element name="return-join" type="return-join-element"/>
       <xs:element name="load-collection" type="load-collection-element"/>
       <xs:element name="synchronize" type="synchronize-element"/>
       <xs:element name="query-param" type="query-param-element"/>
     </xs:choice>
     <xs:attribute name="cache-mode" type="cache-mode-attribute"/>
     <xs:attribute name="cache-region" type="xs:string"/>
     <xs:attribute name="cacheable" default="false" type="xs:boolean"/>
     <xs:attribute name="callable" default="false" type="xs:boolean"/>
     <xs:attribute name="comment" type="xs:string"/>
     <xs:attribute name="fetch-size" type="xs:string"/>
     <xs:attribute name="flush-mode" type="flush-mode-attribute"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="read-only" type="xs:boolean"/>
     <xs:attribute name="resultset-ref" type="xs:string"/>
     <xs:attribute name="timeout" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="sql-update-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!--
   	Subclass declarations are nested beneath the root class declaration to achieve
   	polymorphic persistence with the table-per-hierarchy mapping strategy.
   	See the note on the class element regarding <pojo/> vs. @name usage...
   -->
   <xs:complexType name="subclass-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="idbag" type="idbag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
       <xs:element name="join" minOccurs="0" maxOccurs="unbounded" type="join-element"/>
       <xs:element name="subclass" minOccurs="0" maxOccurs="unbounded" type="subclass-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
       <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
       <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="query" type="query-element"/>
         <xs:element name="sql-query" type="sql-query-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="abstract" type="xs:boolean"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="discriminator-value" type="xs:string"/> <!-- default: unqualified class name | none -->
     <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
     <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="extends" type="xs:string"/> <!-- default: empty when a toplevel, otherwise the nearest class definition -->
     <xs:attribute name="lazy" type="xs:boolean"/>
     <xs:attribute name="name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
     <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
   </xs:complexType>
 
   <xs:complexType name="synchronize-element">
     <xs:attribute name="table" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
       TUPLIZER element; defines tuplizer to use for a component/entity for a given entity-mode
   -->
   <xs:complexType name="tuplizer-element">
     <xs:attribute name="class" use="required" type="xs:string"/> <!-- the tuplizer class to use -->
     <xs:attribute name="entity-mode"> <!-- entity mode for which tuplizer is in effect -->
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="dom4j"/>
           <xs:enumeration value="dynamic-map"/>
           <xs:enumeration value="pojo"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
   </xs:complexType>
 
   <!-- Declares the type of the containing property (overrides an eventually existing type
   attribute of the property). May contain param elements to customize a ParametrizableType. -->
   <xs:complexType name="type-element">
     <xs:sequence>
       <xs:element name="param" minOccurs="0" maxOccurs="unbounded" type="param-element"/>
     </xs:sequence>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	Union subclasses are used for the table-per-concrete-class mapping strategy
   	See the note on the class element regarding <pojo/> vs. @name usage...
   -->
   <xs:complexType name="union-subclass-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="properties" type="properties-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="idbag" type="idbag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
       <xs:element name="union-subclass" minOccurs="0" maxOccurs="unbounded" type="union-subclass-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
       <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
       <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="query" type="query-element"/>
         <xs:element name="sql-query" type="sql-query-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="abstract" type="xs:boolean"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/>
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
     <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="extends" type="xs:string"/> <!-- default: none when toplevel, otherwise the nearest class definition -->
     <xs:attribute name="lazy" type="xs:boolean"/>
     <xs:attribute name="name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
     <xs:attribute name="schema" type="xs:string"/>
     <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: unqualified class name -->
   </xs:complexType>
 
   <xs:simpleType name="cache-mode-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="get"/>
       <xs:enumeration value="ignore"/>
       <xs:enumeration value="normal"/>
       <xs:enumeration value="put"/>
       <xs:enumeration value="refresh"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="check-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="none"/>
       <xs:enumeration value="param"/>
       <xs:enumeration value="rowcount"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="fetch-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="join"/>
       <xs:enumeration value="select"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="fetch-attribute-with-subselect">
     <xs:restriction base="xs:token">
       <xs:enumeration value="join"/>
       <xs:enumeration value="select"/>
       <xs:enumeration value="subselect"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="flush-mode-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="always"/>
       <xs:enumeration value="auto"/>
       <xs:enumeration value="never"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="generated-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="always"/>
       <xs:enumeration value="never"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="lazy-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="false"/>
       <xs:enumeration value="proxy"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="lazy-attribute-with-extra">
     <xs:restriction base="xs:token">
       <xs:enumeration value="extra"/>
       <xs:enumeration value="false"/>
       <xs:enumeration value="true"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="lazy-attribute-with-no-proxy">
     <xs:restriction base="xs:token">
       <xs:enumeration value="false"/>
       <xs:enumeration value="no-proxy"/>
       <xs:enumeration value="proxy"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="lock-mode-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="none"/>
       <xs:enumeration value="read"/>
       <xs:enumeration value="upgrade"/>
       <xs:enumeration value="upgrade-nowait"/>
+      <xs:enumeration value="upgrade-skiplocked"/>
       <xs:enumeration value="write"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="not-found-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="exception"/>
       <xs:enumeration value="ignore"/>
     </xs:restriction>
   </xs:simpleType>
 
   <xs:simpleType name="outer-join-attribute">
     <xs:restriction base="xs:token">
       <xs:enumeration value="auto"/>
       <xs:enumeration value="false"/>
       <xs:enumeration value="true"/>
     </xs:restriction>
   </xs:simpleType>
 
 </xs:schema>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
index 96c8fc2482..dd97788db5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
@@ -101,1144 +101,1155 @@ public class ParentChildTest extends LegacyTestCase {
 		f.setBag(list);
 		List list2 = new ArrayList();
 		list2.add(f);
 		baz.setBag(list2);
 		s.save(f);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.OVERWRITE);
 		// HHH-2378
 		SessionImpl x = (SessionImpl)s;
 		EntityEntry entry = x.getPersistenceContext().getEntry( baz );
 		assertNull(entry.getVersion());
 		// ~~~~~~~
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.IGNORE);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.delete(f);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryOneToOne() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Serializable id = s.save( new Parent() );
 		assertTrue( s.createQuery( "from Parent p left join fetch p.child" ).list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Parent p = (Parent) s.createQuery("from Parent p left join fetch p.child").uniqueResult();
 		assertTrue( p.getChild()==null );
 		s.createQuery( "from Parent p join p.child c where c.x > 0" ).list();
 		s.createQuery( "from Child c join c.parent p where p.x > 0" ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.get(Parent.class, id) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testProxyReuse() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		FooProxy foo = new Foo();
 		FooProxy foo2 = new Foo();
 		Serializable id = s.save(foo);
 		Serializable id2 = s.save(foo2);
 		foo2.setInt( 1234567 );
 		foo.setInt(1234);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		foo2 = (FooProxy) s.load(Foo.class, id2);
 		assertFalse( Hibernate.isInitialized( foo ) );
 		Hibernate.initialize( foo2 );
 		Hibernate.initialize(foo);
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		assertTrue( foo2.getComponent().getImportantDates().length == 4 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo.setKey( "xyzid" );
 		foo.setFloat( new Float( 1.2f ) );
 		foo2.setKey( (String) id ); //intentionally id, not id2!
 		foo2.setFloat( new Float( 1.3f ) );
 		foo2.getDependent().setKey( null );
 		foo2.getComponent().getSubcomponent().getFee().setKey(null);
 		assertFalse( foo2.getKey().equals( id ) );
 		s.save( foo );
 		s.update( foo2 );
 		assertEquals( foo2.getKey(), id );
 		assertTrue( foo2.getInt() == 1234567 );
 		assertEquals( foo.getKey(), "xyzid" );
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		assertTrue( foo.getInt() == 1234567 );
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		String feekey = foo.getDependent().getKey();
 		String fookey = foo.getKey();
 		s.delete( foo );
 		s.delete( s.get(Foo.class, id2) );
 		s.delete( s.get(Foo.class, "xyzid") );
 // here is the issue (HHH-4092).  After the deletes above there are 2 Fees and a Glarch unexpectedly hanging around
 		assertEquals( 2, doDelete( s, "from java.lang.Object" ) );
 		t.commit();
 		s.close();
 		
 		//to account for new id rollback shit
 		foo.setKey(fookey);
 		foo.getDependent().setKey( feekey );
 		foo.getComponent().setGlarch( null );
 		foo.getComponent().setSubcomponent(null);
 		
 		s = openSession();
 		t = s.beginTransaction();
 		//foo.getComponent().setGlarch(null); //no id property!
 		s.replicate( foo, ReplicationMode.OVERWRITE );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Foo refoo = (Foo) s.get(Foo.class, id);
 		assertEquals( feekey, refoo.getDependent().getKey() );
 		s.delete(refoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testComplexCriteria() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		baz.setDefaults();
 		Map topGlarchez = new HashMap();
 		baz.setTopGlarchez(topGlarchez);
 		Glarch g1 = new Glarch();
 		g1.setName("g1");
 		s.save(g1);
 		Glarch g2 = new Glarch();
 		g2.setName("g2");
 		s.save(g2);
 		g1.setProxyArray( new GlarchProxy[] {g2} );
 		topGlarchez.put( new Character('1'),g1 );
 		topGlarchez.put( new Character('2'), g2);
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		s.save(foo1);
 		s.save(foo2);
 		baz.getFooSet().add(foo1);
 		baz.getFooSet().add(foo2);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		LockMode lockMode = supportsLockingNullableSideOfJoin( getDialect() ) ? LockMode.UPGRADE : LockMode.READ;
 
 		Criteria crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") )
 			.createCriteria("proxyArray")
 				.add( Restrictions.eqProperty("name", "name") )
 				.add( Restrictions.eq("name", "g2") )
 				.add( Restrictions.gt("x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") )
 			.add( Restrictions.eq("string", "a string") )
 			.add( Restrictions.lt("integer", new Integer(-665) ) );
 		crit.createCriteria("fooArray")
 				// this is the bit causing the problems; creating the criteria on fooArray does not add it to FROM,
 				// and so restriction below leads to an invalid reference.
 			.add( Restrictions.eq("string", "a string") )
 			.setLockMode(lockMode);
 
 		List list = crit.list();
 		assertTrue( list.size()==2 );
 		
 		s.createCriteria(Glarch.class).setLockMode(LockMode.UPGRADE).list();
 		s.createCriteria(Glarch.class).setLockMode(Criteria.ROOT_ALIAS, LockMode.UPGRADE).list();
 		
 		g2.setName(null);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		
 		list = s.createCriteria(Baz.class).add( Restrictions.isEmpty("fooSet") ).list();
 		assertEquals( list.size(), 0 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.isNotEmpty("fooSet") ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.sizeEq("fooSet", 2) ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 		
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class)
 			.setLockMode(lockMode);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.gt( "x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 		list = crit.list();
 
 		assertTrue( list.size()==4 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 
 		list = s.createCriteria(Baz.class)
 			.createCriteria("fooSet")
 				.createCriteria("foo")
 					.createCriteria("component.glarch")
 						.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		list = s.createCriteria(Baz.class)
 			.createAlias("fooSet", "foo")
 			.createAlias("foo.foo", "foo2")
 			.setLockMode("foo2", lockMode)
 			.add( Restrictions.isNull("foo2.component.glarch") )
 			.createCriteria("foo2.component.glarch")
 				.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 
 		list = crit.list();
 		assertTrue( list.size()==2 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 		
 		s.createCriteria(Child.class).setFetchMode("parent", FetchMode.JOIN).list();
 
 		doDelete( s, "from Glarch g" );
 		s.delete( s.get(Foo.class, foo1.getKey() ) );
 		s.delete( s.get(Foo.class, foo2.getKey() ) );
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testArrayHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b left join fetch b.fooArray").uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testArrayCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria(Baz.class).createCriteria( "fooArray" ).uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b").uniqueResult();
 		assertFalse( Hibernate.isInitialized( baz.getFoo() ) );
 		assertTrue( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria( Baz.class ).uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneGet() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.get( Baz.class, baz.getCode() );
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhere() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getParts().add(p1);
 		baz.getParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.parts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhereManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setMoreParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getMoreParts().add(p1);
 		baz.getMoreParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.moreParts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getMoreParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setName("s");
 		s1.setCount(0);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		Container c = new Container();
 		Contained cd = new Contained();
 		List bag = new ArrayList();
 		bag.add(cd);
 		c.setBag(bag);
 		List l = new ArrayList();
 		l.add(s1);
 		l.add(s3);
 		l.add(s2);
 		c.setOneToMany(l);
 		l = new ArrayList();
 		l.add(s1);
 		l.add(null);
 		l.add(s2);
 		c.setManyToMany(l);
 		s.save(c);
 		Container cx = new Container();
 		s.save(cx);
 		Simple sx = new Simple( Long.valueOf(5) );
 		sx.setCount(5);
 		sx.setName("s");
 		s.save( sx );
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.oneToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.manyToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.oneToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.manyToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.oneToMany[2 - 2].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.manyToMany[(3+1)/4-1].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		if ( ! ( getDialect() instanceof MySQLDialect ) && !(getDialect() instanceof org.hibernate.dialect.TimesTenDialect) ) {
 			assertTrue(
 					s.createQuery( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" )
 							.list()
 							.size() == 1
 			);
 		}
 		assertTrue( s.contains(cd) );
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) )  {
 			s.createFilter( c.getBag(), "where 0 in elements(this.bag)" ).list();
 			s.createFilter( c.getBag(), "where 0 in elements(this.lazyBag)" ).list();
 		}
 		s.createQuery( "select count(comp.name) from ContainerX c join c.components comp" ).list();
 		s.delete(cd);
 		s.delete(c);
 		s.delete(s1);
 		s.delete(s2);
 		s.delete(s3);
 		s.delete(cx);
 		s.delete(sx);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		Child c = new Child();
 		c.setParent(p);
 		p.setChild(c);
 		s.save(p);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 parent", p!=null );
 		c.setCount(32);
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 update", p.getCount()==66 );
 		assertTrue( "1-1 update", c.getCount()==32 );
 		assertTrue(
 			"1-1 query",
 				s.createQuery( "from Child c where c.parent.count=66" ).list().size()==1
 		);
 		assertTrue(
 			"1-1 query",
 			( (Object[]) s.createQuery( "from Parent p join p.child c where p.count=66" ).list().get(0) ).length==2
 		);
 		s.createQuery( "select c, c.parent from Child c order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent from Child c where c.parent.count=66 order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent, c.parent.count from Child c order by c.parent.count" ).iterate();
 		List result = s.createQuery( "FROM Parent AS p WHERE p.count = ?" )
 				.setParameter( 0, new Integer(66), StandardBasicTypes.INTEGER )
 				.list();
 		assertEquals( "1-1 query", 1, result.size() );
 		s.delete(c); s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentNullChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		s.save(p);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( p.getChild()==null );
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( "null 1-1 update", p.getCount()==66 );
 		assertTrue( p.getChild()==null );
 		s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		c.setManyToMany( new ArrayList() );
 		c.setBag( new ArrayList() );
 		Simple s1 = new Simple( Long.valueOf(12) );
 		Simple s2 = new Simple( Long.valueOf(-1) );
 		s1.setCount(123); s2.setCount(654);
 		Contained c1 = new Contained();
 		c1.setBag( new ArrayList() );
 		c1.getBag().add(c);
 		c.getBag().add(c1);
 		c.getManyToMany().add(s1);
 		c.getManyToMany().add(s2);
 		Serializable cid = s.save(c);
 		s.save( s1 );
 		s.save( s2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==1 );
 		assertTrue( c.getManyToMany().size()==2 );
 		c1 = (Contained) c.getBag().iterator().next();
 		assertTrue( c.getBag().size()==1 );
 		c.getBag().remove(c1);
 		c1.getBag().remove(c);
 		assertTrue( c.getManyToMany().remove(0)!=null );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==0 );
 		assertTrue( c.getManyToMany().size()==1 );
 		c1 = (Contained) s.load( Contained.class, new Long(c1.getId()) );
 		assertTrue( c1.getBag().size()==0 );
 		assertEquals( 1, doDelete( s, "from ContainerX c" ) );
 		assertEquals( 1, doDelete( s, "from Contained" ) );
 		assertEquals( 2, doDelete( s, "from Simple" ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testContainer() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Simple x = new Simple( Long.valueOf(1) );
 		x.setCount(123);
 		Simple y = new Simple( Long.valueOf(0) );
 		y.setCount(456);
 		s.save( x );
 		s.save( y );
 		List o2m = new ArrayList();
 		o2m.add(x); o2m.add(null); o2m.add(y);
 		List m2m = new ArrayList();
 		m2m.add(x); m2m.add(null); m2m.add(y);
 		c.setOneToMany(o2m); c.setManyToMany(m2m);
 		List comps = new ArrayList();
 		Container.ContainerInnerClass ccic = new Container.ContainerInnerClass();
 		ccic.setName("foo");
 		ccic.setSimple(x);
 		comps.add(ccic);
 		comps.add(null);
 		ccic = new Container.ContainerInnerClass();
 		ccic.setName("bar");
 		ccic.setSimple(y);
 		comps.add(ccic);
 		HashSet compos = new HashSet();
 		compos.add(ccic);
 		c.setComposites(compos);
 		c.setComponents(comps);
 		One one = new One();
 		Many many = new Many();
 		HashSet manies = new HashSet();
 		manies.add(many);
 		one.setManies(manies);
 		many.setOne(one);
 		ccic.setMany(many);
 		ccic.setOne(one);
 		s.save(one);
 		s.save(many);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Long count = (Long) s.createQuery("select count(*) from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'").uniqueResult();
 		assertTrue( count.intValue()==1 );
 		List res = s.createQuery(
 				"select c, s from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'"
 		).list();
 		assertTrue(res.size()==1);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		ccic = (Container.ContainerInnerClass) c.getComponents().get(2);
 		assertTrue( ccic.getMany().getOne()==ccic.getOne() );
 		assertTrue( c.getComponents().size()==3 );
 		assertTrue( c.getComposites().size()==1 );
 		assertTrue( c.getOneToMany().size()==3 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(2)!=null );
 		for ( int i=0; i<3; i++ ) {
 			assertTrue( c.getManyToMany().get(i) == c.getOneToMany().get(i) );
 		}
 		Object o1 = c.getOneToMany().get(0);
 		Object o2 = c.getOneToMany().remove(2);
 		c.getOneToMany().set(0, o2);
 		c.getOneToMany().set(1, o1);
 		o1 = c.getComponents().remove(2);
 		c.getComponents().set(0, o1);
 		c.getManyToMany().set( 0, c.getManyToMany().get(2) );
 		Container.ContainerInnerClass ccic2 = new Container.ContainerInnerClass();
 		ccic2.setName("foo");
 		ccic2.setOne(one);
 		ccic2.setMany(many);
 		ccic2.setSimple( (Simple) s.load(Simple.class, new Long(0) ) );
 		c.getComposites().add(ccic2);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( c.getOneToMany().size()==2 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(1)!=null );
 		( (Container.ContainerInnerClass) c.getComponents().get(0) ).setName("a different name");
 		( (Container.ContainerInnerClass) c.getComposites().iterator().next() ).setName("once again");
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( ( (Container.ContainerInnerClass) c.getComponents().get(0) ).getName().equals("a different name") );
 		Iterator iter = c.getComposites().iterator();
 		boolean found = false;
 		while ( iter.hasNext() ) {
 			if ( ( (Container.ContainerInnerClass) iter.next() ).getName().equals("once again") ) found = true;
 		}
 		assertTrue(found);
 		c.getOneToMany().clear();
 		c.getManyToMany().clear();
 		c.getComposites().clear();
 		c.getComponents().clear();
 		doDelete( s, "from Simple" );
 		doDelete( s, "from Many" );
 		doDelete( s, "from One" );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		assertTrue( c.getComponents().size()==0 );
 		assertTrue( c.getComposites().size()==0 );
 		assertTrue( c.getOneToMany().size()==0 );
 		assertTrue( c.getManyToMany().size()==0 );
 		s.delete(c);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeCompositeElements() throws Exception {
 		Container c = new Container();
 		List list = new ArrayList();
 		c.setCascades(list);
 		Container.ContainerInnerClass cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		Session s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 
 		c = new Container();
 		s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		list = new ArrayList();
 		c.setCascades(list);
 		cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBag() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Contained c1 = new Contained();
 		Contained c2 = new Contained();
 		c.setBag( new ArrayList() );
 		c.getBag().add(c1);
 		c.getBag().add(c2);
 		c1.getBag().add(c);
 		c2.getBag().add(c);
 		s.save(c);
 		c.getBag().add(c2);
 		c2.getBag().add(c);
 		c.getLazyBag().add(c1);
 		c1.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		c.getLazyBag().size();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c3 = new Contained();
 		//c.getBag().add(c3);
 		//c3.getBag().add(c);
 		c.getLazyBag().add(c3);
 		c3.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c4 = new Contained();
 		c.getLazyBag().add(c4);
 		c4.getLazyBag().add(c);
 		assertTrue( c.getLazyBag().size()==3 ); //forces initialization
 		//s.save(c4);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Iterator i = c.getBag().iterator();
 		int j=0;
 		while ( i.hasNext() ) {
 			assertTrue( i.next()!=null );
 			j++;
 		}
 		assertTrue(j==3);
 		assertTrue( c.getLazyBag().size()==3 );
 		s.delete(c);
 		c.getBag().remove(c2);
 		Iterator iter = c.getBag().iterator();
 		j=0;
 		while ( iter.hasNext() ) {
 			j++;
 			s.delete( iter.next() );
 		}
 		assertTrue(j==2);
 		s.delete( s.load(Contained.class, new Long( c4.getId() ) ) );
 		s.delete( s.load(Contained.class, new Long( c3.getId() ) ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCircularCascade() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Circular c = new Circular();
 		c.setClazz(Circular.class);
 		c.setOther( new Circular() );
 		c.getOther().setOther( new Circular() );
 		c.getOther().getOther().setOther(c);
 		c.setAnyEntity( c.getOther() );
 		String id = (String) s.save(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		c = (Circular) s.load(Circular.class, id);
 		c.getOther().getOther().setClazz(Foo.class);
 		tx.commit();
 		s.close();
 		c.getOther().setClazz(Qux.class);
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate(c);
 		tx.commit();
 		s.close();
 		c.getOther().getOther().setClazz(Bar.class);
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		c = (Circular) s.load(Circular.class, id);
 		assertTrue( c.getOther().getOther().getClazz()==Bar.class);
 		assertTrue( c.getOther().getClazz()==Qux.class);
 		assertTrue( c.getOther().getOther().getOther()==c);
 		assertTrue( c.getAnyEntity()==c.getOther() );
 		assertEquals( 3, doDelete( s, "from Universe" ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteEmpty() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		assertEquals( 0, doDelete( s, "from Simple" ) );
 		assertEquals( 0, doDelete( s, "from Universe" ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLocking() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setCount(1);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		Simple s4 = new Simple( Long.valueOf(4) );
 		s4.setCount(4);
+		Simple s5 = new Simple( Long.valueOf(5) );
+		s5.setCount(5);
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		s.save( s4 );
+		s.save( s5 );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.WRITE );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.READ || s.getCurrentLockMode(s1)==LockMode.NONE ); //depends if cache is enabled
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s4 = (Simple) s.get(Simple.class, new Long(4), LockMode.UPGRADE_NOWAIT);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
+		s5 = (Simple) s.get(Simple.class, new Long(5), LockMode.UPGRADE_SKIPLOCKED);
+		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.UPGRADE );
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s4 = (Simple) s.load(Simple.class, new Long(4), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
+		s5 = (Simple) s.load(Simple.class, new Long(5), LockMode.UPGRADE);
+		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s.lock(s1, LockMode.UPGRADE_NOWAIT);
 		s.lock(s4, LockMode.NONE);
+		s.lock(s5, LockMode.UPGRADE_SKIPLOCKED);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
+		assertTrue( s.getCurrentLockMode(s5)==LockMode.UPGRADE_SKIPLOCKED );
 
 		tx.commit();
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
+		assertTrue( s.getCurrentLockMode(s5)==LockMode.NONE );
 
 		s.lock(s1, LockMode.READ); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.READ );
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE_NOWAIT); //upgrade
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE_NOWAIT );
 		s.lock(s4, LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 
 		s4.setName("s4");
 		s.flush();
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.WRITE );
 		tx.commit();
 
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
+		assertTrue( s.getCurrentLockMode(s5)==LockMode.NONE );
 
-		s.delete(s1); s.delete(s2); s.delete(s3); s.delete(s4);
+		s.delete(s1); s.delete(s2); s.delete(s3); s.delete(s4); s.delete(s5);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testObjectType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Parent g = new Parent();
 		Foo foo = new Foo();
 		g.setAny(foo);
 		s.save(g);
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (Parent) s.load( Parent.class, new Long( g.getId() ) );
 		assertTrue( g.getAny()!=null && g.getAny() instanceof FooProxy );
 		s.delete( g.getAny() );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoadAfterNonExists() throws HibernateException, SQLException {
 		Session session = openSession();
 		if ( ( getDialect() instanceof MySQLDialect ) || ( getDialect() instanceof IngresDialect ) ) {
 			session.doWork(
 					new AbstractWork() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							connection.setTransactionIsolation( Connection.TRANSACTION_READ_COMMITTED );
 						}
 					}
 			);
 		}
 		session.getTransaction().begin();
 
 		// First, prime the fixture session to think the entity does not exist
 		try {
 			session.load( Simple.class, new Long(-1) );
 			fail();
 		}
 		catch(ObjectNotFoundException onfe) {
 			// this is correct
 		}
 
 		// Next, lets create that entity "under the covers"
 		Session anotherSession = sessionFactory().openSession();
 		anotherSession.beginTransaction();
 		Simple myNewSimple = new Simple( Long.valueOf(-1) );
 		myNewSimple.setName("My under the radar Simple entity");
 		myNewSimple.setAddress("SessionCacheTest.testLoadAfterNonExists");
 		myNewSimple.setCount(1);
 		myNewSimple.setDate( new Date() );
 		myNewSimple.setPay( Float.valueOf( 100000000 ) );
 		anotherSession.save( myNewSimple );
 		anotherSession.getTransaction().commit();
 		anotherSession.close();
 
 		// Now, lets make sure the original session can see the created row...
 		session.clear();
 		try {
 			Simple dummy = (Simple) session.get( Simple.class, Long.valueOf(-1) );
 			assertNotNull("Unable to locate entity Simple with id = -1", dummy);
 			session.delete( dummy );
 		}
 		catch(ObjectNotFoundException onfe) {
 			fail("Unable to locate entity Simple with id = -1");
 		}
 		session.getTransaction().commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
index 181f64627a..229651d1f5 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
@@ -1,1288 +1,1291 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.spi;
 
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.EntityExistsException;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.EntityTransaction;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.LockTimeoutException;
 import javax.persistence.NoResultException;
 import javax.persistence.NonUniqueResultException;
 import javax.persistence.OptimisticLockException;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PessimisticLockException;
 import javax.persistence.PessimisticLockScope;
 import javax.persistence.Query;
 import javax.persistence.QueryTimeoutException;
 import javax.persistence.StoredProcedureQuery;
 import javax.persistence.SynchronizationType;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.Tuple;
 import javax.persistence.TupleElement;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Selection;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.transaction.Status;
 import javax.transaction.SystemException;
 import javax.transaction.TransactionManager;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.lock.LockingStrategyException;
 import org.hibernate.dialect.lock.OptimisticEntityLockException;
 import org.hibernate.dialect.lock.PessimisticEntityLockException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.synchronization.spi.AfterCompletionAction;
 import org.hibernate.engine.transaction.synchronization.spi.ExceptionMapper;
 import org.hibernate.engine.transaction.synchronization.spi.ManagedFlushChecker;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateEntityManagerFactory;
 import org.hibernate.jpa.QueryHints;
 import org.hibernate.jpa.criteria.ValueHandlerFactory;
 import org.hibernate.jpa.criteria.compile.CompilableCriteria;
 import org.hibernate.jpa.criteria.compile.CriteriaCompiler;
 import org.hibernate.jpa.criteria.expression.CompoundSelectionImpl;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.QueryImpl;
 import org.hibernate.jpa.internal.StoredProcedureQueryImpl;
 import org.hibernate.jpa.internal.TransactionImpl;
 import org.hibernate.jpa.internal.util.CacheModeHelper;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.transform.BasicTransformerAdapter;
 import org.hibernate.type.Type;
 
 
 /**
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public abstract class AbstractEntityManagerImpl implements HibernateEntityManagerImplementor, Serializable {
 	private static final long serialVersionUID = 78818181L;
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            AbstractEntityManagerImpl.class.getName());
 
 	private static final List<String> entityManagerSpecificProperties = new ArrayList<String>();
 
 	static {
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_SCOPE );
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_TIMEOUT );
 		entityManagerSpecificProperties.add( AvailableSettings.FLUSH_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_STORE_MODE );
 		entityManagerSpecificProperties.add( QueryHints.SPEC_HINT_TIMEOUT );
 	}
 
 	private EntityManagerFactoryImpl entityManagerFactory;
 	protected transient TransactionImpl tx = new TransactionImpl( this );
 	protected PersistenceContextType persistenceContextType;
 	private SynchronizationType synchronizationType;
 	private PersistenceUnitTransactionType transactionType;
 	private Map<String, Object> properties;
 	private LockOptions lockOptions;
 
 	protected AbstractEntityManagerImpl(
 			EntityManagerFactoryImpl entityManagerFactory,
 			PersistenceContextType type,
 			SynchronizationType synchronizationType,
 			PersistenceUnitTransactionType transactionType,
 			Map properties) {
 		this.entityManagerFactory = entityManagerFactory;
 		this.persistenceContextType = type;
 		this.synchronizationType = synchronizationType;
 		this.transactionType = transactionType;
 
 		this.lockOptions = new LockOptions();
 		this.properties = new HashMap<String, Object>();
 		for ( String key : entityManagerSpecificProperties ) {
 			if ( entityManagerFactory.getProperties().containsKey( key ) ) {
 				this.properties.put( key, entityManagerFactory.getProperties().get( key ) );
 			}
 			if ( properties != null && properties.containsKey( key ) ) {
 				this.properties.put( key, properties.get( key ) );
 			}
 		}
 	}
 
 //	protected PersistenceUnitTransactionType transactionType() {
 //		return transactionType;
 //	}
 //
 //	protected SynchronizationType synchronizationType() {
 //		return synchronizationType;
 //	}
 //
 //	public boolean shouldAutoJoinTransactions() {
 //		// the Session should auto join only if using non-JTA transactions or if the synchronization type
 //		// was specified as SYNCHRONIZED
 //		return transactionType != PersistenceUnitTransactionType.JTA
 //				|| synchronizationType == SynchronizationType.SYNCHRONIZED;
 //	}
 
 	public PersistenceUnitTransactionType getTransactionType() {
 		return transactionType;
 	}
 
 	protected void postInit() {
 		//register in Sync if needed
 		if ( transactionType == PersistenceUnitTransactionType.JTA
 				&& synchronizationType == SynchronizationType.SYNCHRONIZED ) {
 			joinTransaction( false );
 		}
 
 		setDefaultProperties();
 		applyProperties();
 	}
 
 	private void applyProperties() {
 		getSession().setFlushMode( ConfigurationHelper.getFlushMode( properties.get( AvailableSettings.FLUSH_MODE ) ) );
 		setLockOptions( this.properties, this.lockOptions );
 		getSession().setCacheMode(
 				CacheModeHelper.interpretCacheMode(
 						currentCacheStoreMode(),
 						currentCacheRetrieveMode()
 				)
 		);
 	}
 
 	private Query applyProperties(Query query) {
 		if ( lockOptions.getLockMode() != LockMode.NONE ) {
 			query.setLockMode( getLockMode(lockOptions.getLockMode()));
 		}
 		Object queryTimeout;
 		if ( (queryTimeout = getProperties().get(QueryHints.SPEC_HINT_TIMEOUT)) != null ) {
 			query.setHint ( QueryHints.SPEC_HINT_TIMEOUT, queryTimeout );
 		}
 		Object lockTimeout;
 		if( (lockTimeout = getProperties().get( AvailableSettings.LOCK_TIMEOUT ))!=null){
 			query.setHint( AvailableSettings.LOCK_TIMEOUT, lockTimeout );
 		}
 		return query;
 	}
 
 	private CacheRetrieveMode currentCacheRetrieveMode() {
 		return determineCacheRetrieveMode( properties );
 	}
 
 	private CacheRetrieveMode determineCacheRetrieveMode(Map<String, Object> settings) {
 		return ( CacheRetrieveMode ) settings.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 	}
 
 	private CacheStoreMode currentCacheStoreMode() {
 		return determineCacheStoreMode( properties );
 	}
 
 	private CacheStoreMode determineCacheStoreMode(Map<String, Object> settings) {
 		return ( CacheStoreMode ) properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE );
 	}
 
 	private void setLockOptions(Map<String, Object> props, LockOptions options) {
 		Object lockScope = props.get( AvailableSettings.LOCK_SCOPE );
 		if ( lockScope instanceof String && PessimisticLockScope.valueOf( ( String ) lockScope ) == PessimisticLockScope.EXTENDED ) {
 			options.setScope( true );
 		}
 		else if ( lockScope instanceof PessimisticLockScope ) {
 			boolean extended = PessimisticLockScope.EXTENDED.equals( lockScope );
 			options.setScope( extended );
 		}
 		else if ( lockScope != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_SCOPE + ": " + lockScope );
 		}
 
 		Object lockTimeout = props.get( AvailableSettings.LOCK_TIMEOUT );
 		int timeout = 0;
 		boolean timeoutSet = false;
 		if ( lockTimeout instanceof String ) {
 			timeout = Integer.parseInt( ( String ) lockTimeout );
 			timeoutSet = true;
 		}
 		else if ( lockTimeout instanceof Number ) {
 			timeout = ( (Number) lockTimeout ).intValue();
 			timeoutSet = true;
 		}
 		else if ( lockTimeout != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_TIMEOUT + ": " + lockTimeout );
 		}
 		if ( timeoutSet ) {
-			if ( timeout < 0 ) {
+            if ( timeout == LockOptions.SKIP_LOCKED ) {
+                options.setTimeOut( LockOptions.SKIP_LOCKED );
+            }
+			else if ( timeout < 0 ) {
 				options.setTimeOut( LockOptions.WAIT_FOREVER );
 			}
 			else if ( timeout == 0 ) {
 				options.setTimeOut( LockOptions.NO_WAIT );
 			}
 			else {
 				options.setTimeOut( timeout );
 			}
 		}
 	}
 
 	/**
 	 * Sets the default property values for the properties the entity manager supports and which are not already explicitly
 	 * set.
 	 */
 	private void setDefaultProperties() {
 		if ( properties.get( AvailableSettings.FLUSH_MODE ) == null ) {
 			properties.put( AvailableSettings.FLUSH_MODE, getSession().getFlushMode().toString() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_SCOPE ) == null ) {
 			this.properties.put( AvailableSettings.LOCK_SCOPE, PessimisticLockScope.EXTENDED.name() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_TIMEOUT ) == null ) {
 			properties.put( AvailableSettings.LOCK_TIMEOUT, LockOptions.WAIT_FOREVER );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheModeHelper.DEFAULT_RETRIEVE_MODE );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheModeHelper.DEFAULT_STORE_MODE );
 		}
 	}
 
 	public Query createQuery(String jpaqlString) {
 		try {
 			return applyProperties( new QueryImpl<Object>( getSession().createQuery( jpaqlString ), this ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public <T> TypedQuery<T> createQuery(String jpaqlString, Class<T> resultClass) {
 		try {
 			// do the translation
 			org.hibernate.Query hqlQuery = getSession().createQuery( jpaqlString );
 
 			resultClassChecking( resultClass, hqlQuery );
 
 			// finally, build/return the query instance
 			return new QueryImpl<T>( hqlQuery, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	protected void resultClassChecking(Class resultClass, org.hibernate.Query hqlQuery) {
 		// make sure the query is a select -> HHH-7192
 		final SessionImplementor session = unwrap( SessionImplementor.class );
 		final HQLQueryPlan queryPlan = session.getFactory().getQueryPlanCache().getHQLQueryPlan(
 				hqlQuery.getQueryString(),
 				false,
 				session.getLoadQueryInfluencers().getEnabledFilters()
 		);
 		if ( queryPlan.getTranslators()[0].isManipulationStatement() ) {
 			throw new IllegalArgumentException( "Update/delete queries cannot be typed" );
 		}
 
 		// do some return type validation checking
 		if ( Object[].class.equals( resultClass ) ) {
 			// no validation needed
 		}
 		else if ( Tuple.class.equals( resultClass ) ) {
 			TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer( hqlQuery );
 			hqlQuery.setResultTransformer( tupleTransformer  );
 		}
 		else {
 			final Class dynamicInstantiationClass = queryPlan.getDynamicInstantiationResultType();
 			if ( dynamicInstantiationClass != null ) {
 				if ( ! resultClass.isAssignableFrom( dynamicInstantiationClass ) ) {
 					throw new IllegalArgumentException(
 							"Mismatch in requested result type [" + resultClass.getName() +
 									"] and actual result type [" + dynamicInstantiationClass.getName() + "]"
 					);
 				}
 			}
 			else if ( hqlQuery.getReturnTypes().length == 1 ) {
 				// if we have only a single return expression, its java type should match with the requested type
 				if ( !resultClass.isAssignableFrom( hqlQuery.getReturnTypes()[0].getReturnedClass() ) ) {
 					throw new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									hqlQuery.getReturnTypes()[0].getReturnedClass() + "]"
 					);
 				}
 			}
 			else {
 				throw new IllegalArgumentException(
 						"Cannot create TypedQuery for query with more than one return using requested result type [" +
 								resultClass.getName() + "]"
 				);
 			}
 		}
 	}
 
 	public static class TupleBuilderTransformer extends BasicTransformerAdapter {
 		private List<TupleElement<?>> tupleElements;
 		private Map<String,HqlTupleElementImpl> tupleElementsByAlias;
 
 		public TupleBuilderTransformer(org.hibernate.Query hqlQuery) {
 			final Type[] resultTypes = hqlQuery.getReturnTypes();
 			final int tupleSize = resultTypes.length;
 
 			this.tupleElements = CollectionHelper.arrayList( tupleSize );
 
 			final String[] aliases = hqlQuery.getReturnAliases();
 			final boolean hasAliases = aliases != null && aliases.length > 0;
 			this.tupleElementsByAlias = hasAliases
 					? CollectionHelper.<String, HqlTupleElementImpl>mapOfSize( tupleSize )
 					: Collections.<String, HqlTupleElementImpl>emptyMap();
 
 			for ( int i = 0; i < tupleSize; i++ ) {
 				final HqlTupleElementImpl tupleElement = new HqlTupleElementImpl(
 						i,
 						aliases == null ? null : aliases[i],
 						resultTypes[i]
 				);
 				tupleElements.add( tupleElement );
 				if ( hasAliases ) {
 					final String alias = aliases[i];
 					if ( alias != null ) {
 						tupleElementsByAlias.put( alias, tupleElement );
 					}
 				}
 			}
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			if ( tuple.length != tupleElements.size() ) {
 				throw new IllegalArgumentException(
 						"Size mismatch between tuple result [" + tuple.length + "] and expected tuple elements [" +
 								tupleElements.size() + "]"
 				);
 			}
 			return new HqlTupleImpl( tuple );
 		}
 
 		public static class HqlTupleElementImpl<X> implements TupleElement<X> {
 			private final int position;
 			private final String alias;
 			private final Type hibernateType;
 
 			public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
 				this.position = position;
 				this.alias = alias;
 				this.hibernateType = hibernateType;
 			}
 
 			@Override
 			public Class getJavaType() {
 				return hibernateType.getReturnedClass();
 			}
 
 			@Override
 			public String getAlias() {
 				return alias;
 			}
 
 			public int getPosition() {
 				return position;
 			}
 
 			public Type getHibernateType() {
 				return hibernateType;
 			}
 		}
 
 		public class HqlTupleImpl implements Tuple {
 			private Object[] tuple;
 
 			public HqlTupleImpl(Object[] tuple) {
 				this.tuple = tuple;
 			}
 
 			@Override
 			public <X> X get(String alias, Class<X> type) {
 				return (X) get( alias );
 			}
 
 			@Override
 			public Object get(String alias) {
 				HqlTupleElementImpl tupleElement = tupleElementsByAlias.get( alias );
 				if ( tupleElement == null ) {
 					throw new IllegalArgumentException( "Unknown alias [" + alias + "]" );
 				}
 				return tuple[ tupleElement.getPosition() ];
 			}
 
 			@Override
 			public <X> X get(int i, Class<X> type) {
 				return (X) get( i );
 			}
 
 			@Override
 			public Object get(int i) {
 				if ( i < 0 ) {
 					throw new IllegalArgumentException( "requested tuple index must be greater than zero" );
 				}
 				if ( i > tuple.length ) {
 					throw new IllegalArgumentException( "requested tuple index exceeds actual tuple size" );
 				}
 				return tuple[i];
 			}
 
 			@Override
 			public Object[] toArray() {
 				// todo : make a copy?
 				return tuple;
 			}
 
 			@Override
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 
 			@Override
 			public <X> X get(TupleElement<X> tupleElement) {
 				if ( HqlTupleElementImpl.class.isInstance( tupleElement ) ) {
 					return get( ( (HqlTupleElementImpl) tupleElement ).getPosition(), tupleElement.getJavaType() );
 				}
 				else {
 					return get( tupleElement.getAlias(), tupleElement.getJavaType() );
 				}
 			}
 		}
 	}
 
 	public <T> QueryImpl<T> createQuery(
 			String jpaqlString,
 			Class<T> resultClass,
 			Selection selection,
 			Options options) {
 		try {
 			org.hibernate.Query hqlQuery = getSession().createQuery( jpaqlString );
 
 			if ( options.getValueHandlers() == null ) {
 				if ( options.getResultMetadataValidator() != null ) {
 					options.getResultMetadataValidator().validate( hqlQuery.getReturnTypes() );
 				}
 			}
 
 			// determine if we need a result transformer
 			List tupleElements = Tuple.class.equals( resultClass )
 					? ( ( CompoundSelectionImpl<Tuple> ) selection ).getCompoundSelectionItems()
 					: null;
 			if ( options.getValueHandlers() != null || tupleElements != null ) {
 				hqlQuery.setResultTransformer(
 						new CriteriaQueryTransformer( options.getValueHandlers(), tupleElements )
 				);
 			}
 			return new QueryImpl<T>( hqlQuery, this, options.getNamedParameterExplicitTypes() );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	private static class CriteriaQueryTransformer extends BasicTransformerAdapter {
 		private final List<ValueHandlerFactory.ValueHandler> valueHandlers;
 		private final List tupleElements;
 
 		private CriteriaQueryTransformer(List<ValueHandlerFactory.ValueHandler> valueHandlers, List tupleElements) {
 			// todo : should these 2 sizes match *always*?
 			this.valueHandlers = valueHandlers;
 			this.tupleElements = tupleElements;
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			final Object[] valueHandlerResult;
 			if ( valueHandlers == null ) {
 				valueHandlerResult = tuple;
 			}
 			else {
 				valueHandlerResult = new Object[tuple.length];
 				for ( int i = 0; i < tuple.length; i++ ) {
 					ValueHandlerFactory.ValueHandler valueHandler = valueHandlers.get( i );
 					valueHandlerResult[i] = valueHandler == null
 							? tuple[i]
 							: valueHandler.convert( tuple[i] );
 				}
 			}
 
 			return tupleElements == null
 					? valueHandlerResult.length == 1 ? valueHandlerResult[0] : valueHandlerResult
 					: new TupleImpl( tuple );
 
 		}
 
 		private class TupleImpl implements Tuple {
 			private final Object[] tuples;
 
 			private TupleImpl(Object[] tuples) {
 				if ( tuples.length != tupleElements.size() ) {
 					throw new IllegalArgumentException(
 							"Size mismatch between tuple result [" + tuples.length
 									+ "] and expected tuple elements [" + tupleElements.size() + "]"
 					);
 				}
 				this.tuples = tuples;
 			}
 
 			public <X> X get(TupleElement<X> tupleElement) {
 				int index = tupleElements.indexOf( tupleElement );
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Requested tuple element did not correspond to element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return ( X ) tuples[index];
 			}
 
 			public Object get(String alias) {
 				int index = -1;
 				if ( alias != null ) {
 					alias = alias.trim();
 					if ( alias.length() > 0 ) {
 						int i = 0;
 						for ( TupleElement selection : ( List<TupleElement> ) tupleElements ) {
 							if ( alias.equals( selection.getAlias() ) ) {
 								index = i;
 								break;
 							}
 							i++;
 						}
 					}
 				}
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Given alias [" + alias + "] did not correspond to an element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return tuples[index];
 			}
 
 			public <X> X get(String alias, Class<X> type) {
 				return ( X ) get( alias );
 			}
 
 			public Object get(int i) {
 				if ( i >= tuples.length ) {
 					throw new IllegalArgumentException(
 							"Given index [" + i + "] was outside the range of result tuple size [" + tuples.length + "] "
 					);
 				}
 				return tuples[i];
 			}
 
 			public <X> X get(int i, Class<X> type) {
 				return ( X ) get( i );
 			}
 
 			public Object[] toArray() {
 				return tuples;
 			}
 
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 		}
 	}
 
 	private CriteriaCompiler criteriaCompiler;
 
 	protected CriteriaCompiler criteriaCompiler() {
 		if ( criteriaCompiler == null ) {
 			criteriaCompiler = new CriteriaCompiler( this );
 		}
 		return criteriaCompiler;
 	}
 
 	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
 		return (TypedQuery<T>) criteriaCompiler().compile( (CompilableCriteria) criteriaQuery );
 	}
 
 	@Override
 	public Query createQuery(CriteriaUpdate criteriaUpdate) {
 		return criteriaCompiler().compile( (CompilableCriteria) criteriaUpdate );
 	}
 
 	@Override
 	public Query createQuery(CriteriaDelete criteriaDelete) {
 		return criteriaCompiler().compile( (CompilableCriteria) criteriaDelete );
 	}
 
 	public Query createNamedQuery(String name) {
 		try {
 			org.hibernate.Query namedQuery = getSession().getNamedQuery( name );
 			try {
 				return new QueryImpl( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
 		try {
 			/*
 			 * Get the named query.
 			 * If the named query is a SQL query, get the expected returned type from the query definition
 			 * or its associated result set mapping
 			 * If the named query is a HQL query, use getReturnType()
 			 */
 			org.hibernate.Query namedQuery = getSession().getNamedQuery( name );
 			//TODO clean this up to avoid downcasting
 			final SessionFactoryImplementor factoryImplementor = entityManagerFactory.getSessionFactory();
 			final NamedSQLQueryDefinition queryDefinition = factoryImplementor.getNamedSQLQuery( name );
 			try {
 				if ( queryDefinition != null ) {
 					Class<?> actualReturnedClass;
 
 					final NativeSQLQueryReturn[] queryReturns;
 					if ( queryDefinition.getQueryReturns() != null ) {
 						queryReturns = queryDefinition.getQueryReturns();
 					}
 					else if ( queryDefinition.getResultSetRef() != null ) {
 						final ResultSetMappingDefinition rsMapping = factoryImplementor.getResultSetMapping(
 								queryDefinition.getResultSetRef()
 						);
 						queryReturns = rsMapping.getQueryReturns();
 					}
 					else {
 						throw new AssertionFailure( "Unsupported named query model. Please report the bug in Hibernate EntityManager");
 					}
 					if ( queryReturns.length > 1 ) {
 						throw new IllegalArgumentException( "Cannot create TypedQuery for query with more than one return" );
 					}
 					final NativeSQLQueryReturn nativeSQLQueryReturn = queryReturns[0];
 					if ( nativeSQLQueryReturn instanceof NativeSQLQueryRootReturn ) {
 						final String entityClassName = ( ( NativeSQLQueryRootReturn ) nativeSQLQueryReturn ).getReturnEntityName();
 						try {
 							actualReturnedClass = ReflectHelper.classForName( entityClassName, AbstractEntityManagerImpl.class );
 						}
 						catch ( ClassNotFoundException e ) {
 							throw new AssertionFailure( "Unable to instantiate class declared on named native query: " + name + " " + entityClassName );
 						}
 						if ( !resultClass.isAssignableFrom( actualReturnedClass ) ) {
 							throw buildIncompatibleException( resultClass, actualReturnedClass );
 						}
 					}
 					else {
 						//TODO support other NativeSQLQueryReturn type. For now let it go.
 					}
 				}
 				else {
 					resultClassChecking( resultClass, namedQuery );
 				}
 				return new QueryImpl<T>( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
 		return new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									actualResultClass + "]"
 					);
 	}
 
 	public Query createNativeQuery(String sqlString) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public Query createNativeQuery(String sqlString, Class resultClass) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			q.addEntity( "alias1", resultClass.getName(), LockMode.READ );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public Query createNativeQuery(String sqlString, String resultSetMapping) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			q.setResultSetMapping( resultSetMapping );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
 		try {
 			ProcedureCall procedureCall = getSession().createStoredProcedureCall( procedureName );
 			return new StoredProcedureQueryImpl( procedureCall, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
 		try {
 			ProcedureCall procedureCall = getSession().createStoredProcedureCall( procedureName, resultClasses );
 			return new StoredProcedureQueryImpl( procedureCall, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
 		throw new NotYetImplementedException();
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
 		try {
 			return ( T ) getSession().load( entityClass, ( Serializable ) primaryKey );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey) {
 		return find( entityClass, primaryKey, null, null );
 	}
 
 	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
 		return find( entityClass, primaryKey, null, properties );
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType) {
 		return find( entityClass, primaryKey, lockModeType, null );
 	}
 
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
 		CacheMode previousCacheMode = getSession().getCacheMode();
 		CacheMode cacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			getSession().setCacheMode( cacheMode );
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				return ( A ) getSession().get(
 						entityClass, ( Serializable ) primaryKey, 
 						lockOptions
 				);
 			}
 			else {
 				return ( A ) getSession().get( entityClass, ( Serializable ) primaryKey );
 			}
 		}
 		catch ( EntityNotFoundException ignored ) {
 			// DefaultLoadEventListener.returnNarrowedProxy may throw ENFE (see HHH-7861 for details),
 			// which find() should not throw.  Find() should return null if the entity was not found.
 			if ( LOG.isDebugEnabled() ) {
 				String entityName = entityClass != null ? entityClass.getName(): null;
 				String identifierValue = primaryKey != null ? primaryKey.toString() : null ;
 				LOG.ignoringEntityNotFound( entityName, identifierValue );
 			}
 			return null;
 		}
 		catch ( ObjectDeletedException e ) {
 			//the spec is silent about people doing remove() find() on the same PC
 			return null;
 		}
 		catch ( ObjectNotFoundException e ) {
 			//should not happen on the entity itself with get
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			getSession().setCacheMode( previousCacheMode );
 		}
 	}
 
 	public CacheMode determineAppropriateLocalCacheMode(Map<String, Object> localProperties) {
 		CacheRetrieveMode retrieveMode = null;
 		CacheStoreMode storeMode = null;
 		if ( localProperties != null ) {
 			retrieveMode = determineCacheRetrieveMode( localProperties );
 			storeMode = determineCacheStoreMode( localProperties );
 		}
 		if ( retrieveMode == null ) {
 			// use the EM setting
 			retrieveMode = determineCacheRetrieveMode( this.properties );
 		}
 		if ( storeMode == null ) {
 			// use the EM setting
 			storeMode = determineCacheStoreMode( this.properties );
 		}
 		return CacheModeHelper.interpretCacheMode( storeMode, retrieveMode );
 	}
 
 	private void checkTransactionNeeded() {
 		if ( persistenceContextType == PersistenceContextType.TRANSACTION && !isTransactionInProgress() ) {
 			//no need to mark as rollback, no tx in progress
 			throw new TransactionRequiredException(
 					"no transaction is in progress for a TRANSACTION type persistence context"
 			);
 		}
 	}
 
 	public void persist(Object entity) {
 		checkTransactionNeeded();
 		try {
 			getSession().persist( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage() );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A merge(A entity) {
 		checkTransactionNeeded();
 		try {
 			return ( A ) getSession().merge( entity );
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw new IllegalArgumentException( sse );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	public void remove(Object entity) {
 		checkTransactionNeeded();
 		try {
 			getSession().delete( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	public void refresh(Object entity) {
 		refresh( entity, null, null );
 	}
 
 	public void refresh(Object entity, Map<String, Object> properties) {
 		refresh( entity, null, properties );
 	}
 
 	public void refresh(Object entity, LockModeType lockModeType) {
 		refresh( entity, lockModeType, null );
 	}
 
 	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkTransactionNeeded();
 		CacheMode previousCacheMode = getSession().getCacheMode();
 		CacheMode localCacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			getSession().setCacheMode( localCacheMode );
 			if ( !getSession().contains( entity ) ) {
 				throw new IllegalArgumentException( "Entity not managed" );
 			}
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				getSession().refresh( entity, lockOptions );
 			}
 			else {
 				getSession().refresh( entity );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			getSession().setCacheMode( previousCacheMode );
 		}
 	}
 
 	public boolean contains(Object entity) {
 		try {
 			if ( entity != null
 					&& !( entity instanceof HibernateProxy )
 					&& getSession().getSessionFactory().getClassMetadata( entity.getClass() ) == null ) {
 				throw new IllegalArgumentException( "Not an entity:" + entity.getClass() );
 			}
 			return getSession().contains( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public LockModeType getLockMode(Object entity) {
 		if ( !contains( entity ) ) {
 			throw new IllegalArgumentException( "entity not in the persistence context" );
 		}
 		return getLockModeType( getSession().getCurrentLockMode( entity ) );
 	}
 
 	public void setProperty(String s, Object o) {
 		if ( entityManagerSpecificProperties.contains( s ) ) {
 			properties.put( s, o );
 			applyProperties();
         } else LOG.debugf("Trying to set a property which is not supported on entity manager level");
 	}
 
 	public Map<String, Object> getProperties() {
 		return Collections.unmodifiableMap( properties );
 	}
 
 	public void flush() {
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 		try {
 			getSession().flush();
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	/**
 	 * return a Session
 	 *
 	 * @throws IllegalStateException if the entity manager is closed
 	 */
 	public abstract Session getSession();
 
 	/**
 	 * Return a Session (even if the entity manager is closed).
 	 *
 	 * @return A session.
 	 */
 	protected abstract Session getRawSession();
 
 	public EntityTransaction getTransaction() {
 		if ( transactionType == PersistenceUnitTransactionType.JTA ) {
 			throw new IllegalStateException( "A JTA EntityManager cannot use getTransaction()" );
 		}
 		return tx;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityManagerFactoryImpl getEntityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public HibernateEntityManagerFactory getFactory() {
 		return entityManagerFactory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaBuilder getCriteriaBuilder() {
 		return getEntityManagerFactory().getCriteriaBuilder();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Metamodel getMetamodel() {
 		return getEntityManagerFactory().getMetamodel();
 	}
 
 	public void setFlushMode(FlushModeType flushModeType) {
 		if ( flushModeType == FlushModeType.AUTO ) {
 			getSession().setFlushMode( FlushMode.AUTO );
 		}
 		else if ( flushModeType == FlushModeType.COMMIT ) {
 			getSession().setFlushMode( FlushMode.COMMIT );
 		}
 		else {
 			throw new AssertionFailure( "Unknown FlushModeType: " + flushModeType );
 		}
 	}
 
 	public void clear() {
 		try {
 			getSession().clear();
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public void detach(Object entity) {
 		try {
 			getSession().evict( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * Hibernate can be set in various flush modes that are unknown to
 	 * JPA 2.0. This method can then return null.
 	 * If it returns null, do em.unwrap(Session.class).getFlushMode() to get the
 	 * Hibernate flush mode
 	 */
 	public FlushModeType getFlushMode() {
 		FlushMode mode = getSession().getFlushMode();
 		if ( mode == FlushMode.AUTO ) {
 			return FlushModeType.AUTO;
 		}
 		else if ( mode == FlushMode.COMMIT ) {
 			return FlushModeType.COMMIT;
 		}
 		else {
 			// otherwise this is an unknown mode for EJB3
 			return null;
 		}
 	}
 
 	public void lock(Object entity, LockModeType lockMode) {
 		lock( entity, lockMode, null );
 	}
 
 	public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		LockOptions lockOptions = null;
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 
 		try {
 			if ( !contains( entity ) ) {
 				throw new IllegalArgumentException( "entity not in the persistence context" );
 			}
 			lockOptions = getLockRequest( lockModeType, properties );
 			getSession().buildLockRequest( lockOptions ).lock( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 	}
 
 	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
 		LockOptions lockOptions = new LockOptions();
 		LockOptions.copy( this.lockOptions, lockOptions );
 		lockOptions.setLockMode( getLockMode( lockModeType ) );
 		if ( properties != null ) {
 			setLockOptions( properties, lockOptions );
 		}
 		return lockOptions;
 	}
 
 	@SuppressWarnings("deprecation")
 	private static LockModeType getLockModeType(LockMode lockMode) {
 		//TODO check that if we have UPGRADE_NOWAIT we have a timeout of zero?
 		return LockModeTypeHelper.getLockModeType( lockMode );
 	}
 
 
 	private static LockMode getLockMode(LockModeType lockMode) {
 		return LockModeTypeHelper.getLockMode( lockMode );
 	}
 
 	public boolean isTransactionInProgress() {
 		return ( ( SessionImplementor ) getRawSession() ).isTransactionInProgress();
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return (SessionFactoryImplementor) getRawSession().getSessionFactory();
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> clazz) {
 		if ( Session.class.isAssignableFrom( clazz ) ) {
 			return ( T ) getSession();
 		}
 		if ( SessionImplementor.class.isAssignableFrom( clazz ) ) {
 			return ( T ) getSession();
 		}
 		if ( EntityManager.class.isAssignableFrom( clazz ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap " + clazz );
 	}
 
 	protected void markAsRollback() {
         LOG.debugf("Mark transaction for rollback");
 		if ( tx.isActive() ) {
 			tx.setRollbackOnly();
 		}
 		else {
 			//no explicit use of the tx. boundaries methods
 			if ( PersistenceUnitTransactionType.JTA == transactionType ) {
 				TransactionManager transactionManager = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
 				if ( transactionManager == null ) {
 					throw new PersistenceException(
 							"Using a JTA persistence context wo setting hibernate.transaction.manager_lookup_class"
 					);
 				}
 				try {
                     if ( transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION ) {
                         transactionManager.setRollbackOnly();
                     }
 				}
 				catch ( SystemException e ) {
 					throw new PersistenceException( "Unable to set the JTA transaction as RollbackOnly", e );
 				}
 			}
 		}
 	}
 
 	@Override
 	public boolean isJoinedToTransaction() {
 		final SessionImplementor session = (SessionImplementor) getSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		return isOpen() && transaction.getJoinStatus() == JoinStatus.JOINED;
 	}
 
 	@Override
 	public void joinTransaction() {
 		if( !isOpen() ){
 			throw new IllegalStateException( "EntityManager is closed" );
 		}
 		joinTransaction( true );
 	}
