diff --git a/documentation/src/main/docbook/devguide/en-US/Envers.xml b/documentation/src/main/docbook/devguide/en-US/Envers.xml
index fe981b5223..86651fb8f8 100644
--- a/documentation/src/main/docbook/devguide/en-US/Envers.xml
+++ b/documentation/src/main/docbook/devguide/en-US/Envers.xml
@@ -1,1330 +1,1342 @@
 <?xml version='1.0' encoding='utf-8'?>
 
 <chapter xmlns="http://docbook.org/ns/docbook" xmlns:xl="http://www.w3.org/1999/xlink" >
     <info>
         <title>Envers</title>
         <abstract>
             <para>
                 The aim of Hibernate Envers is to provide historical versioning of your application's entity data.  Much
                 like source control management tools such as Subversion or Git, Hibernate Envers manages a notion of revisions
                 if your application data through the use of audit tables.  Each transaction relates to one global revision number
                 which can be used to identify groups of changes (much like a change set in source control).  As the revisions
                 are global, having a revision number, you can query for various entities at that revision, retrieving a
                 (partial) view of the database at that revision. You can find a revision number having a date, and the other
                 way round, you can get the date at which a revision was committed.
             </para>
         </abstract>
     </info>
 
     <section>
         <title>Basics</title>
 
         <para>
             To audit changes that are performed on an entity, you only need two things: the
             <literal>hibernate-envers</literal> jar on the classpath and an <literal>@Audited</literal> annotation
             on the entity.
         </para>
 
         <important>
             <para>
                 Unlike in previous versions, you no longer need to specify listeners in the Hibernate configuration
                 file. Just putting the Envers jar on the classpath is enough - listeners will be registered
                 automatically.
             </para>
         </important>
 
         <para>
             And that's all - you can create, modify and delete the entities as always. If you look at the generated
             schema for your entities, or at the data persisted by Hibernate, you will notice that there are no changes.
             However, for each audited entity, a new table is introduced - <literal>entity_table_AUD</literal>,
             which stores the historical data, whenever you commit a transaction. Envers automatically creates audit
             tables if <literal>hibernate.hbm2ddl.auto</literal> option is set to <literal>create</literal>,
             <literal>create-drop</literal> or <literal>update</literal>. Otherwise, to export complete database schema
             programatically, use <literal>org.hibernate.envers.tools.hbm2ddl.EnversSchemaGenerator</literal>. Appropriate DDL
             statements can be also generated with Ant task described later in this manual.
         </para>
 
         <para>
             Instead of annotating the whole class and auditing all properties, you can annotate
             only some persistent properties with <literal>@Audited</literal>. This will cause only
             these properties to be audited.
         </para>
 
         <para>
             The audit (history) of an entity can be accessed using the <literal>AuditReader</literal> interface, which
             can be obtained having an open <literal>EntityManager</literal> or <literal>Session</literal> via
             the <literal>AuditReaderFactory</literal>. See the javadocs for these classes for details on the
             functionality offered.
         </para>
     </section>
 
     <section xml:id="envers-configuration">
         <title>Configuration</title>
         <para>
             It is possible to configure various aspects of Hibernate Envers behavior, such as table names, etc.
         </para>
 
         <table frame="topbot">
             <title>Envers Configuration Properties</title>
             <tgroup cols="3">
                 <colspec colname="c1" colwidth="1*"/>
                 <colspec colname="c2" colwidth="1*"/>
                 <colspec colname="c2" colwidth="1*"/>
 
                 <thead>
                     <row>
                         <entry>Property name</entry>
                         <entry>Default value</entry>
                         <entry>Description</entry>
                     </row>
                 </thead>
 
                 <tbody>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.audit_table_prefix</property>
                         </entry>
                         <entry>
                         </entry>
                         <entry>
                             String that will be prepended to the name of an audited entity to create the name of the
                             entity, that will hold audit information.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.audit_table_suffix</property>
                         </entry>
                         <entry>
                             _AUD
                         </entry>
                         <entry>
                             String that will be appended to the name of an audited entity to create the name of the
                             entity, that will hold audit information. If you audit an entity with a table name Person,
                             in the default setting Envers will generate a <literal>Person_AUD</literal> table to store
                             historical data.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.revision_field_name</property>
                         </entry>
                         <entry>
                             REV
                         </entry>
                         <entry>
                             Name of a field in the audit entity that will hold the revision number.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.revision_type_field_name</property>
                         </entry>
                         <entry>
                             REVTYPE
                         </entry>
                         <entry>
                             Name of a field in the audit entity that will hold the type of the revision (currently,
                             this can be: add, mod, del).
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.revision_on_collection_change</property>
                         </entry>
                         <entry>
                             true
                         </entry>
                         <entry>
                             Should a revision be generated when a not-owned relation field changes (this can be either
                             a collection in a one-to-many relation, or the field using "mappedBy" attribute in a
                             one-to-one relation).
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.do_not_audit_optimistic_locking_field</property>
                         </entry>
                         <entry>
                             true
                         </entry>
                         <entry>
                             When true, properties to be used for optimistic locking, annotated with
                             <literal>@Version</literal>, will be automatically not audited (their history won't be
                             stored; it normally doesn't make sense to store it).
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.store_data_at_delete</property>
                         </entry>
                         <entry>
                             false
                         </entry>
                         <entry>
                             Should the entity data be stored in the revision when the entity is deleted (instead of only
                             storing the id and all other properties as null). This is not normally needed, as the data is
                             present in the last-but-one revision. Sometimes, however, it is easier and more efficient to
                             access it in the last revision (then the data that the entity contained before deletion is
                             stored twice).
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.default_schema</property>
                         </entry>
                         <entry>
                             null (same schema as table being audited)
                         </entry>
                         <entry>
                             The default schema name that should be used for audit tables. Can be overridden using the
                             <literal>@AuditTable(schema="...")</literal> annotation. If not present, the schema will
                             be the same as the schema of the table being audited.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.default_catalog</property>
                         </entry>
                         <entry>
                             null (same catalog as table being audited)
                         </entry>
                         <entry>
                             The default catalog name that should be used for audit tables. Can be overridden using the
                             <literal>@AuditTable(catalog="...")</literal> annotation. If not present, the catalog will
                             be the same as the catalog of the normal tables.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.audit_strategy</property>
                         </entry>
                         <entry>
                             org.hibernate.envers.strategy.DefaultAuditStrategy
                         </entry>
                         <entry>
                             The audit strategy that should be used when persisting audit data. The default stores only
                             the revision, at which an entity was modified. An alternative, the
                             <literal>org.hibernate.envers.strategy.ValidityAuditStrategy</literal> stores both the
                             start revision and the end revision. Together these define when an audit row was valid,
                             hence the name ValidityAuditStrategy.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.audit_strategy_validity_end_rev_field_name</property>
                         </entry>
                         <entry>
                             REVEND
                         </entry>
                         <entry>
                             The column name that will hold the end revision number in audit entities. This property is
                             only valid if the validity audit strategy is used.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.audit_strategy_validity_store_revend_timestamp</property>
                         </entry>
                         <entry>
                             false
                         </entry>
                         <entry>
                             Should the timestamp of the end revision be stored, until which the data was valid, in
                             addition to the end revision itself.  This is useful to be able to purge old Audit records
                             out of a relational database by using table partitioning.  Partitioning requires a column
                             that exists within the table.  This property is only evaluated if the ValidityAuditStrategy
                             is used.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.audit_strategy_validity_revend_timestamp_field_name</property>
                         </entry>
                         <entry>
                             REVEND_TSTMP
                         </entry>
                         <entry>
                             Column name of the timestamp of the end revision until which the data was valid.  Only used
                             if the ValidityAuditStrategy is used, and
                             <property>org.hibernate.envers.audit_strategy_validity_store_revend_timestamp</property>
                             evaluates to true
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.use_revision_entity_with_native_id</property>
                         </entry>
                         <entry>
                             true
                         </entry>
                         <entry>
                             Boolean flag that determines the strategy of revision number generation. Default
                             implementation of revision entity uses native identifier generator. If current database
                             engine does not support identity columns, users are advised to set this property to false.
                             In this case revision numbers are created by preconfigured
                             <classname>org.hibernate.id.enhanced.SequenceStyleGenerator</classname>. See:
                             <orderedlist>
                                 <listitem><classname>org.hibernate.envers.DefaultRevisionEntity</classname></listitem>
                                 <listitem><classname>org.hibernate.envers.enhanced.SequenceIdRevisionEntity</classname></listitem>
                             </orderedlist>
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.track_entities_changed_in_revision</property>
                         </entry>
                         <entry>
                             false
                         </entry>
                         <entry>
                             Should entity types, that have been modified during each revision, be tracked. The default
                             implementation creates <literal>REVCHANGES</literal> table that stores entity names
                             of modified persistent objects. Single record encapsulates the revision identifier
                             (foreign key to <literal>REVINFO</literal> table) and a string value. For more
                             information refer to <xref linkend="envers-tracking-modified-entities-revchanges"/>
                             and <xref linkend="envers-tracking-modified-entities-queries"/>.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.global_with_modified_flag</property>
                         </entry>
                         <entry>
                             false, can be individually overriden with <literal>@Audited(withModifiedFlag=true)</literal>
                         </entry>
                         <entry>
                             Should property modification flags be stored for all audited entities and all properties.
                             When set to true, for all properties an additional boolean column in the audit tables will
                             be created, filled with information if the given property changed in the given revision.
                             When set to false, such column can be added to selected entities or properties using the
                             <literal>@Audited</literal> annotation.
                             For more information refer to <xref linkend="envers-tracking-properties-changes"/>
                             and <xref linkend="envers-tracking-properties-changes-queries"/>.
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.modified_flag_suffix</property>
                         </entry>
                         <entry>
                             _MOD
                         </entry>
                         <entry>
                             The suffix for columns storing "Modified Flags".
                             For example: a property called "age", will by default get modified flag with column name "age_MOD".
                         </entry>
                     </row>
                     <row>
                         <entry>
                             <property>org.hibernate.envers.embeddable_set_ordinal_field_name</property>
                         </entry>
                         <entry>
                             SETORDINAL
                         </entry>
                         <entry>
                             Name of column used for storing ordinal of the change in sets of embeddable elements.
                         </entry>
                     </row>
                     <row>
                         <entry>
+                            <property>org.hibernate.envers.cascade_delete_revision</property>
+                        </entry>
+                        <entry>
+                            false
+                        </entry>
+                        <entry>
+                            While deleting revision entry, remove data of associated audited entities.
+                            Requires database support for cascade row removal. 
+                        </entry>
+                    </row>
+                    <row>
+                        <entry>
                             <property>org.hibernate.envers.allow_identifier_reuse</property>
                         </entry>
                         <entry>
                             false
                         </entry>
                         <entry>
                             Guarantees proper validity audit strategy behavior when application reuses identifiers
                             of deleted entities. Exactly one row with <literal>null</literal> end date exists
                             for each identifier.
                         </entry>
                     </row>
                 </tbody>
             </tgroup>
         </table>
 
         <important>
             <para>
                 The following configuration options have been added recently and should be regarded as experimental:
                 <orderedlist>
                     <listitem>
                         org.hibernate.envers.track_entities_changed_in_revision
                     </listitem>
                     <listitem>
                         org.hibernate.envers.using_modified_flag
                     </listitem>
                     <listitem>
                         org.hibernate.envers.modified_flag_suffix
                     </listitem>
                 </orderedlist>
             </para>
         </important>
     </section>
 
     <section>
         <title>Additional mapping annotations</title>
 
         <para>
             The name of the audit table can be set on a per-entity basis, using the
             <literal>@AuditTable</literal> annotation. It may be tedious to add this
             annotation to every audited entity, so if possible, it's better to use a prefix/suffix.
         </para>
 
         <para>
             If you have a mapping with secondary tables, audit tables for them will be generated in
             the same way (by adding the prefix and suffix). If you wish to overwrite this behaviour,
             you can use the <literal>@SecondaryAuditTable</literal> and
             <literal>@SecondaryAuditTables</literal> annotations.
         </para>
 
         <para>
             If you'd like to override auditing behaviour of some fields/properties inherited from
             <interfacename>@Mappedsuperclass</interfacename> or in an embedded component, you can
             apply the <literal>@AuditOverride(s)</literal> annotation on the subtype or usage site
             of the component.
         </para>
 
         <para>
             If you want to audit a relation mapped with <literal>@OneToMany+@JoinColumn</literal>,
             please see <xref linkend="envers-mappingexceptions"/> for a description of the additional
             <literal>@AuditJoinTable</literal> annotation that you'll probably want to use.
         </para>
 
         <para>
             If you want to audit a relation, where the target entity is not audited (that is the case for example with
             dictionary-like entities, which don't change and don't have to be audited), just annotate it with
             <literal>@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)</literal>. Then, while reading historic
             versions of your entity, the relation will always point to the "current" related entity. By default Envers
             throws <classname>javax.persistence.EntityNotFoundException</classname> when "current" entity does not
             exist in the database. Apply <literal>@NotFound(action = NotFoundAction.IGNORE)</literal> annotation
             to silence the exception and assign null value instead. Hereby solution causes implicit eager loading
             of to-one relations.
         </para>
 
         <para>
             If you'd like to audit properties of a superclass of an entity, which are not explicitly audited (which
             don't have the <literal>@Audited</literal> annotation on any properties or on the class), you can list the
             superclasses in the <literal>auditParents</literal> attribute of the <interfacename>@Audited</interfacename>
             annotation. Please note that <literal>auditParents</literal> feature has been deprecated. Use
             <literal>@AuditOverride(forClass = SomeEntity.class, isAudited = true/false)</literal> instead.
         </para>
     </section>
 
     <section>
         <title>Choosing an audit strategy</title>
         <para>
             After the basic configuration it is important to choose the audit strategy that will be used to persist
             and retrieve audit information. There is a trade-off between the performance of persisting and the
             performance of querying the audit information. Currently there two audit strategies.
         </para>
         <orderedlist>
             <listitem>
                 <para>
                     The default audit strategy persists the audit data together with a start revision. For each row
                     inserted, updated or deleted in an audited table, one or more rows are inserted in the audit
                     tables, together with the start revision of its validity. Rows in the audit tables are never
                     updated after insertion.  Queries of audit information use subqueries to select the applicable
                     rows in the audit tables.  These subqueries are notoriously slow and difficult to index.
                 </para>
             </listitem>
             <listitem>
                 <para>
                     The alternative is a validity audit strategy. This strategy stores the start-revision and the
                     end-revision of audit information. For each row inserted, updated or deleted in an audited table,
                     one or more rows are inserted in the audit tables, together with the start revision of its
                     validity. But at the same time the end-revision field of the previous audit rows (if available)
                     are set to this revision.  Queries on the audit information can then use 'between start and end
                     revision' instead of subqueries as used by the default audit strategy.
                 </para>
                 <para>
                     The consequence of this strategy is that persisting audit information will be a bit slower,
                     because of the extra updates involved, but retrieving audit information will be a lot faster.
                     This can be improved by adding extra indexes.
                 </para>
             </listitem>
         </orderedlist>
     </section>
 
     <section xml:id="envers-revisionlog">
         <title>Revision Log</title>
         <subtitle>Logging data for revisions</subtitle>
 
         <para>
             When Envers starts a new revision, it creates a new <firstterm>revision entity</firstterm> which stores
             information about the revision.  By default, that includes just
         </para>
         <orderedlist>
             <listitem>
                 <para>
                     <firstterm>revision number</firstterm> - An integral value (<literal>int/Integer</literal> or
                     <literal>long/Long</literal>).  Essentially the primary key of the revision
                 </para>
             </listitem>
             <listitem>
                 <para>
                     <firstterm>revision timestamp</firstterm> - either a <literal>long/Long</literal> or
                     <classname>java.util.Date</classname> value representing the instant at which the revision was made.
                     When using a <classname>java.util.Date</classname>, instead of a <literal>long/Long</literal> for
                     the revision timestamp, take care not to store it to a column data type which will loose precision.
                 </para>
             </listitem>
         </orderedlist>
 
         <para>
             Envers handles this information as an entity.  By default it uses its own internal class to act as the
             entity, mapped to the <literal>REVINFO</literal> table.
             You can, however, supply your own approach to collecting this information which might be useful to
             capture additional details such as who made a change or the ip address from which the request came.  There
             are 2 things you need to make this work.
         </para>
         <orderedlist>
             <listitem>
                 <para>
                     First, you will need to tell Envers about the entity you wish to use.  Your entity must use the
                     <interfacename>@org.hibernate.envers.RevisionEntity</interfacename> annotation.  It must
                     define the 2 attributes described above annotated with
                     <interfacename>@org.hibernate.envers.RevisionNumber</interfacename> and
                     <interfacename>@org.hibernate.envers.RevisionTimestamp</interfacename>, respectively.  You can extend
                     from <classname>org.hibernate.envers.DefaultRevisionEntity</classname>, if you wish, to inherit all
                     these required behaviors.
                 </para>
                 <para>
                     Simply add the custom revision entity as you do your normal entities.  Envers will "find it".  Note
                     that it is an error for there to be multiple entities marked as
                     <interfacename>@org.hibernate.envers.RevisionEntity</interfacename>
                 </para>
             </listitem>
             <listitem>
                 <para>
                     Second, you need to tell Envers how to create instances of your revision entity which is handled
                     by the <methodname>newRevision</methodname> method of the
                     <interfacename>org.jboss.envers.RevisionListener</interfacename> interface.
                 </para>
                 <para>
                     You tell Envers your custom <interfacename>org.hibernate.envers.RevisionListener</interfacename>
                     implementation to use by specifying it on the
                     <interfacename>@org.hibernate.envers.RevisionEntity</interfacename> annotation, using the
                     <methodname>value</methodname> attribute. If your <interfacename>RevisionListener</interfacename>
                     class is inaccessible from <interfacename>@RevisionEntity</interfacename> (e.g. exists in a different
                     module), set <property>org.hibernate.envers.revision_listener</property> property to it's fully
                     qualified name. Class name defined by the configuration parameter overrides revision entity's
                     <methodname>value</methodname> attribute.
                 </para>
             </listitem>
         </orderedlist>
         <programlisting><![CDATA[@Entity
 @RevisionEntity( MyCustomRevisionListener.class )
 public class MyCustomRevisionEntity {
     ...
 }
 
 public class MyCustomRevisionListener implements RevisionListener {
     public void newRevision(Object revisionEntity) {
         ( (MyCustomRevisionEntity) revisionEntity )...;
     }
 }
 ]]></programlisting>
 
         <para>
             An alternative method to using the <interfacename>org.hibernate.envers.RevisionListener</interfacename>
             is to instead call the <methodname>getCurrentRevision</methodname> method of the
             <interfacename>org.hibernate.envers.AuditReader</interfacename> interface to obtain the current revision,
             and fill it with desired information.  The method accepts a <literal>persist</literal> parameter indicating
             whether the revision entity should be persisted prior to returning from this method. <literal>true</literal>
             ensures that the returned entity has access to its identifier value (revision number), but the revision
             entity will be persisted regardless of whether there are any audited entities changed. <literal>false</literal>
             means that the revision number will be <literal>null</literal>, but the revision entity will be persisted
             only if some audited entities have changed.
         </para>
 
 
         <example>
             <title>Example of storing username with revision</title>
 
             <programlisting>
                 <filename>ExampleRevEntity.java</filename><![CDATA[
 
 package org.hibernate.envers.example;
 
 import org.hibernate.envers.RevisionEntity;
 import org.hibernate.envers.DefaultRevisionEntity;
 
 import javax.persistence.Entity;
 
 @Entity
 @RevisionEntity(ExampleListener.class)
 public class ExampleRevEntity extends DefaultRevisionEntity {
     private String username;
 
     public String getUsername() { return username; }
     public void setUsername(String username) { this.username = username; }
 }]]></programlisting>
 
             <programlisting>
                 <filename>ExampleListener.java</filename><![CDATA[
 
 package org.hibernate.envers.example;
 
 import org.hibernate.envers.RevisionListener;
 import org.jboss.seam.security.Identity;
 import org.jboss.seam.Component;
 
 public class ExampleListener implements RevisionListener {
     public void newRevision(Object revisionEntity) {
         ExampleRevEntity exampleRevEntity = (ExampleRevEntity) revisionEntity;
         Identity identity =
             (Identity) Component.getInstance("org.jboss.seam.security.identity");
 
         exampleRevEntity.setUsername(identity.getUsername());
     }
 }]]></programlisting>
 
         </example>
 
         <section xml:id="envers-tracking-modified-entities-revchanges">
             <title>Tracking entity names modified during revisions</title>
             <para>
                 By default entity types that have been changed in each revision are not being tracked. This implies the
                 necessity to query all tables storing audited data in order to retrieve changes made during
                 specified revision. Envers provides a simple mechanism that creates <literal>REVCHANGES</literal>
                 table which stores entity names of modified persistent objects. Single record encapsulates the revision
                 identifier (foreign key to <literal>REVINFO</literal> table) and a string value.
             </para>
             <para>
                 Tracking of modified entity names can be enabled in three different ways:
             </para>
             <orderedlist>
                 <listitem>
                     <para>
                         Set <property>org.hibernate.envers.track_entities_changed_in_revision</property> parameter to
                         <literal>true</literal>. In this case
                         <classname>org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity</classname> will
                         be implicitly used as the revision log entity.
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         Create a custom revision entity that extends
                         <classname>org.hibernate.envers.DefaultTrackingModifiedEntitiesRevisionEntity</classname> class.
                     </para>
                     <programlisting>
 <![CDATA[@Entity
 @RevisionEntity
 public class ExtendedRevisionEntity
              extends DefaultTrackingModifiedEntitiesRevisionEntity {
     ...
 }]]></programlisting>
                 </listitem>
                 <listitem>
                     <para>
                         Mark an appropriate field of a custom revision entity with
                         <interfacename>@org.hibernate.envers.ModifiedEntityNames</interfacename> annotation. The property is
                         required to be of <literal><![CDATA[Set<String>]]></literal> type.
                     </para>
                     <programlisting>
 <![CDATA[@Entity
 @RevisionEntity
 public class AnnotatedTrackingRevisionEntity {
     ...
 
     @ElementCollection
     @JoinTable(name = "REVCHANGES", joinColumns = @JoinColumn(name = "REV"))
     @Column(name = "ENTITYNAME")
     @ModifiedEntityNames
     private Set<String> modifiedEntityNames;
     
     ...
 }]]></programlisting>
                 </listitem>
             </orderedlist>
             <para>
                 Users, that have chosen one of the approaches listed above, can retrieve all entities modified in a
                 specified revision by utilizing API described in <xref linkend="envers-tracking-modified-entities-queries"/>.
             </para>
             <para>
                 Users are also allowed to implement custom mechanism of tracking modified entity types. In this case, they
                 shall pass their own implementation of
                 <interfacename>org.hibernate.envers.EntityTrackingRevisionListener</interfacename> interface as the value
                 of <interfacename>@org.hibernate.envers.RevisionEntity</interfacename> annotation.
                 <interfacename>EntityTrackingRevisionListener</interfacename> interface exposes one method that notifies
                 whenever audited entity instance has been added, modified or removed within current revision boundaries.
             </para>
 
             <example>
                 <title>Custom implementation of tracking entity classes modified during revisions</title>
                 <programlisting>
                     <filename>CustomEntityTrackingRevisionListener.java</filename>
 <![CDATA[
 public class CustomEntityTrackingRevisionListener
              implements EntityTrackingRevisionListener {
     @Override
     public void entityChanged(Class entityClass, String entityName,
                               Serializable entityId, RevisionType revisionType,
                               Object revisionEntity) {
         String type = entityClass.getName();
         ((CustomTrackingRevisionEntity)revisionEntity).addModifiedEntityType(type);
     }
 
     @Override
     public void newRevision(Object revisionEntity) {
     }
 }]]></programlisting>
                 <programlisting>
                     <filename>CustomTrackingRevisionEntity.java</filename>
 <![CDATA[
 @Entity
 @RevisionEntity(CustomEntityTrackingRevisionListener.class)
 public class CustomTrackingRevisionEntity {
     @Id
     @GeneratedValue
     @RevisionNumber
     private int customId;
 
     @RevisionTimestamp
     private long customTimestamp;
 
     @OneToMany(mappedBy="revision", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
     private Set<ModifiedEntityTypeEntity> modifiedEntityTypes =
                                               new HashSet<ModifiedEntityTypeEntity>();
     
     public void addModifiedEntityType(String entityClassName) {
         modifiedEntityTypes.add(new ModifiedEntityTypeEntity(this, entityClassName));
     }
     
     ...
 }
 ]]></programlisting>
                 <programlisting>
                     <filename>ModifiedEntityTypeEntity.java</filename>
 <![CDATA[
 @Entity
 public class ModifiedEntityTypeEntity {
     @Id
     @GeneratedValue
     private Integer id;
 
     @ManyToOne
     private CustomTrackingRevisionEntity revision;
     
     private String entityClassName;
     
     ...
 }
 ]]></programlisting>
                 <programlisting><![CDATA[CustomTrackingRevisionEntity revEntity =
     getAuditReader().findRevision(CustomTrackingRevisionEntity.class, revisionNumber);
 Set<ModifiedEntityTypeEntity> modifiedEntityTypes = revEntity.getModifiedEntityTypes()]]></programlisting>
             </example>
         </section>
 
     </section>
 
     <section xml:id="envers-tracking-properties-changes">
         <title>Tracking entity changes at property level</title>
         <para>
             By default the only information stored by Envers are revisions of modified entities.
             This approach lets user create audit queries based on historical values of entity's properties.
 
             Sometimes it is useful to store additional metadata for each revision, when you are interested also in
             the type of changes, not only about the resulting values. The feature described in
             <xref linkend="envers-tracking-modified-entities-revchanges"/>
             makes it possible to tell which entities were modified in given revision.
 
             Feature described here takes it one step further. "Modification Flags" enable Envers to track which
             properties of audited entities were modified in a given revision.
         </para>
         <para>
             Tracking entity changes at property level can be enabled by:
         </para>
         <orderedlist>
             <listitem>
                 <para>
                     setting <property>org.hibernate.envers.global_with_modified_flag</property> configuration
                     property to <literal>true</literal>.  This global switch will cause adding modification flags
                     for all audited properties in all audited entities.
                 </para>
             </listitem>
             <listitem>
                 <para>
                     using <literal>@Audited(withModifiedFlag=true)</literal> on a property or on an entity.
                 </para>
             </listitem>
         </orderedlist>
         <para>
             The trade-off coming with this functionality is an increased size of
             audit tables and a very little, almost negligible, performance drop
             during audit writes. This is due to the fact that every tracked
             property has to have an accompanying boolean column in the
             schema that stores information about the property's modifications. Of
             course it is Envers' job to fill these columns accordingly - no additional work by the
             developer is required. Because of costs mentioned, it is recommended
             to enable the feature selectively, when needed with use of the
             granular configuration means described above.
         </para>
         <para>
             To see how "Modified Flags" can be utilized, check out the very
             simple query API that uses them: <xref linkend="envers-tracking-properties-changes-queries"/>.
         </para>
     </section>
 
     <section xml:id="envers-queries">
 
         <title>Queries</title>
 
         <para>
             You can think of historic data as having two dimension. The first - horizontal -
             is the state of the database at a given revision. Thus, you can
             query for entities as they were at revision N. The second - vertical - are the
             revisions, at which entities changed. Hence, you can query for revisions,
             in which a given entity changed.
         </para>
 
         <para>
             The queries in Envers are similar to Hibernate Criteria queries, so if you are common with them,
             using Envers queries will be much easier.
         </para>
 
         <para>
             The main limitation of the current queries implementation is that you cannot
             traverse relations. You can only specify constraints on the ids of the
             related entities, and only on the "owning" side of the relation. This however
             will be changed in future releases.
         </para>
 
         <para>
             Please note, that queries on the audited data will be in many cases much slower
             than corresponding queries on "live" data, as they involve correlated subselects.
         </para>
 
         <para>
             In the future, queries will be improved both in terms of speed and possibilities, when using the valid-time
             audit strategy, that is when storing both start and end revisions for entities. See
             <xref linkend="envers-configuration"/>.
         </para>
 
         <section xml:id="entities-at-revision">
 
             <title>Querying for entities of a class at a given revision</title>
 
             <para>
                 The entry point for this type of queries is:
             </para>
 
             <programlisting><![CDATA[AuditQuery query = getAuditReader()
     .createQuery()
     .forEntitiesAtRevision(MyEntity.class, revisionNumber);]]></programlisting>
 
             <para>
                 You can then specify constraints, which should be met by the entities returned, by
                 adding restrictions, which can be obtained using the <literal>AuditEntity</literal>
                 factory class. For example, to select only entities, where the "name" property
                 is equal to "John":
             </para>
 
             <programlisting><![CDATA[query.add(AuditEntity.property("name").eq("John"));]]></programlisting>
 
             <para>
                 And to select only entites that are related to a given entity:
             </para>
 
             <programlisting><![CDATA[query.add(AuditEntity.property("address").eq(relatedEntityInstance));
 // or
 query.add(AuditEntity.relatedId("address").eq(relatedEntityId));]]></programlisting>
 
             <para>
                 You can limit the number of results, order them, and set aggregations and projections
                 (except grouping) in the usual way.
                 When your query is complete, you can obtain the results by calling the
                 <literal>getSingleResult()</literal> or <literal>getResultList()</literal> methods.
             </para>
 
             <para>
                 A full query, can look for example like this:
             </para>
 
             <programlisting><![CDATA[List personsAtAddress = getAuditReader().createQuery()
     .forEntitiesAtRevision(Person.class, 12)
     .addOrder(AuditEntity.property("surname").desc())
     .add(AuditEntity.relatedId("address").eq(addressId))
     .setFirstResult(4)
     .setMaxResults(2)
     .getResultList();]]></programlisting>
 
         </section>
 
         <section xml:id="revisions-of-entity">
 
             <title>Querying for revisions, at which entities of a given class changed</title>
 
             <para>
                 The entry point for this type of queries is:
             </para>
 
             <programlisting><![CDATA[AuditQuery query = getAuditReader().createQuery()
     .forRevisionsOfEntity(MyEntity.class, false, true);]]></programlisting>
 
             <para>
                 You can add constraints to this query in the same way as to the previous one.
                 There are some additional possibilities:
             </para>
 
             <orderedlist>
                 <listitem>
                     <para>
                         using <literal>AuditEntity.revisionNumber()</literal> you can specify constraints, projections
                         and order on the revision number, in which the audited entity was modified
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         similarly, using <literal>AuditEntity.revisionProperty(propertyName)</literal> you can specify constraints,
                         projections and order on a property of the revision entity, corresponding to the revision
                         in which the audited entity was modified
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         <literal>AuditEntity.revisionType()</literal> gives you access as above to the type of
                         the revision (ADD, MOD, DEL).
                     </para>
                 </listitem>
             </orderedlist>
 
             <para>
                 Using these methods,
                 you can order the query results by revision number, set projection or constraint
                 the revision number to be greater or less than a specified value, etc. For example, the
                 following query will select the smallest revision number, at which entity of class
                 <literal>MyEntity</literal> with id <literal>entityId</literal> has changed, after revision
                 number 42:
             </para>
 
             <programlisting><![CDATA[Number revision = (Number) getAuditReader().createQuery()
     .forRevisionsOfEntity(MyEntity.class, false, true)
     .setProjection(AuditEntity.revisionNumber().min())
     .add(AuditEntity.id().eq(entityId))
     .add(AuditEntity.revisionNumber().gt(42))
     .getSingleResult();]]></programlisting>
 
             <para>
                 The second additional feature you can use in queries for revisions is the ability
                 to maximalize/minimize a property. For example, if you want to select the
                 revision, at which the value of the <literal>actualDate</literal> for a given entity
                 was larger then a given value, but as small as possible:
             </para>
 
             <programlisting><![CDATA[Number revision = (Number) getAuditReader().createQuery()
     .forRevisionsOfEntity(MyEntity.class, false, true)
     // We are only interested in the first revision
     .setProjection(AuditEntity.revisionNumber().min())
     .add(AuditEntity.property("actualDate").minimize()
         .add(AuditEntity.property("actualDate").ge(givenDate))
         .add(AuditEntity.id().eq(givenEntityId)))
     .getSingleResult();
 ]]></programlisting>
 
             <para>
                 The <literal>minimize()</literal> and <literal>maximize()</literal> methods return a criteria,
                 to which you can add constraints, which must be met by the entities with the
                 maximized/minimized properties. <literal>AggregatedAuditExpression#computeAggregationInInstanceContext()</literal>
                 enables the possibility to compute aggregated expression in the context of each entity instance
                 separately. It turns out useful when querying for latest revisions of all entities of a particular type.
             </para>
 
             <para>
                 You probably also noticed that there are two boolean parameters, passed when
                 creating the query. The first one, <literal>selectEntitiesOnly</literal>, is only valid when
                 you don't set an explicit projection. If true, the result of the query will be
                 a list of entities (which changed at revisions satisfying the specified
                 constraints).
             </para>
 
             <para>
                 If false, the result will be a list of three element arrays. The
                 first element will be the changed entity instance. The second will be an entity
                 containing revision data (if no custom entity is used, this will be an instance
                 of <literal>DefaultRevisionEntity</literal>). The third will be the type of the
                 revision (one of the values of the <literal>RevisionType</literal> enumeration:
                 ADD, MOD, DEL).
             </para>
 
             <para>
                 The second parameter, <literal>selectDeletedEntities</literal>, specifies if revisions,
                 in which the entity was deleted should be included in the results. If yes, such entities
                 will have the revision type DEL and all fields, except the id,
                 <literal>null</literal>.
             </para>
 
         </section>
 
         <section xml:id="envers-tracking-properties-changes-queries">
 
             <title>Querying for revisions of entity that modified given property</title>
 
             <para>
                 For the two types of queries described above it's possible to use
                 special Audit criteria called
                 <literal>hasChanged()</literal>
                 and
                 <literal>hasNotChanged()</literal>
                 that makes use of the functionality
                 described in <xref linkend="envers-tracking-properties-changes"/>.
                 They're best suited for vertical queries,
                 however existing API doesn't restrict their usage for horizontal
                 ones.
 
                 Let's have a look at following examples:
             </para>
 
             <programlisting><![CDATA[AuditQuery query = getAuditReader().createQuery()
     .forRevisionsOfEntity(MyEntity.class, false, true)
     .add(AuditEntity.id().eq(id));
     .add(AuditEntity.property("actualDate").hasChanged())]]>
             </programlisting>
 
             <para>
                 This query will return all revisions of MyEntity with given id,
                 where the
                 <property>actualDate</property>
                 property has been changed.
                 Using this query we won't get all other revisions in which
                 <property>actualDate</property>
                 wasn't touched. Of course nothing prevents user from combining
                 hasChanged condition with some additional criteria - add method
                 can be used here in a normal way.
             </para>
 
             <programlisting><![CDATA[AuditQuery query = getAuditReader().createQuery()
     .forEntitiesAtRevision(MyEntity.class, revisionNumber)
     .add(AuditEntity.property("prop1").hasChanged())
     .add(AuditEntity.property("prop2").hasNotChanged());]]>
             </programlisting>
 
             <para>
                 This query will return horizontal slice for MyEntity at the time
                 revisionNumber was generated. It will be limited to revisions
                 that modified
                 <property>prop1</property>
                 but not <property>prop2</property>.
                 Note that the result set will usually also contain revisions
                 with numbers lower than the revisionNumber, so we cannot read
                 this query as "Give me all MyEntities changed in revisionNumber
                 with
                 <property>prop1</property>
                 modified and
                 <property>prop2</property>
                 untouched". To get such result we have to use the
                 <literal>forEntitiesModifiedAtRevision</literal> query:
             </para>
 
             <programlisting><![CDATA[AuditQuery query = getAuditReader().createQuery()
     .forEntitiesModifiedAtRevision(MyEntity.class, revisionNumber)
     .add(AuditEntity.property("prop1").hasChanged())
     .add(AuditEntity.property("prop2").hasNotChanged());]]>
             </programlisting>
 
         </section>
 
 
         <section xml:id="envers-tracking-modified-entities-queries">
             <title>Querying for entities modified in a given revision</title>
             <para>
                 The basic query allows retrieving entity names and corresponding Java classes changed in a specified revision:
             </para>
             <programlisting><![CDATA[Set<Pair<String, Class>> modifiedEntityTypes = getAuditReader()
     .getCrossTypeRevisionChangesReader().findEntityTypes(revisionNumber);]]></programlisting>
             <para>
                 Other queries (also accessible from <interfacename>org.hibernate.envers.CrossTypeRevisionChangesReader</interfacename>):
             </para>
             <orderedlist>
                 <listitem>
                     <para>
                         <firstterm><methodname>List<![CDATA[<Object>]]> findEntities(Number)</methodname></firstterm>
                         - Returns snapshots of all audited entities changed (added, updated and removed) in a given revision.
                         Executes <literal>n+1</literal> SQL queries, where <literal>n</literal> is a number of different entity
                         classes modified within specified revision.
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         <firstterm><methodname>List<![CDATA[<Object>]]> findEntities(Number, RevisionType)</methodname></firstterm>
                         - Returns snapshots of all audited entities changed (added, updated or removed) in a given revision
                         filtered by modification type. Executes <literal>n+1</literal> SQL queries, where <literal>n</literal>
                         is a number of different entity classes modified within specified revision.
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         <firstterm><methodname><![CDATA[Map<RevisionType, List<Object>>]]> findEntitiesGroupByRevisionType(Number)</methodname></firstterm>
                         - Returns a map containing lists of entity snapshots grouped by modification operation (e.g.
                         addition, update and removal). Executes <literal>3n+1</literal> SQL queries, where <literal>n</literal>
                         is a number of different entity classes modified within specified revision.
                     </para>
                 </listitem>
             </orderedlist>
             <para>
                 Note that methods described above can be legally used only when default mechanism of
                 tracking changed entity names is enabled (see <xref linkend="envers-tracking-modified-entities-revchanges"/>).
             </para>
         </section>
 
     </section>
 
     <section>
         <title>Conditional auditing</title>
         <para>
             Envers persists audit data in reaction to various Hibernate events (e.g. post update, post insert, and
             so on), using a series of even listeners from the <literal>org.hibernate.envers.event.spi</literal>
             package. By default, if the Envers jar is in the classpath, the event listeners are auto-registered with
             Hibernate.
         </para>
         <para>
             Conditional auditing can be implemented by overriding some of the Envers event listeners.
             To use customized Envers event listeners, the following steps are needed:
             <orderedlist>
                 <listitem>
                     <para>
                         Turn off automatic Envers event listeners registration by setting the
                         <literal>hibernate.listeners.envers.autoRegister</literal> Hibernate property to
                         <literal>false</literal>.
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         Create subclasses for appropriate event listeners. For example, if you want to
                         conditionally audit entity insertions, extend the
                         <literal>org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl</literal>
                         class. Place the conditional-auditing logic in the subclasses, call the super method if
                         auditing should be performed.
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         Create your own implementation of <literal>org.hibernate.integrator.spi.Integrator</literal>,
                         similar to <literal>org.hibernate.envers.event.spi.EnversIntegrator</literal>. Use your event
                         listener classes instead of the default ones.
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         For the integrator to be automatically used when Hibernate starts up, you will need to add a
                         <literal>META-INF/services/org.hibernate.integrator.spi.Integrator</literal> file to your jar.
                         The file should contain the fully qualified name of the class implementing the interface.
                     </para>
                 </listitem>
             </orderedlist>
         </para>
     </section>
 
     <section>
         <title>Understanding the Envers Schema</title>
 
         <para>
             For each audited entity (that is, for each entity containing at least one audited field), an audit table is
             created.  By default, the audit table's name is created by adding a "_AUD" suffix to the original table name,
             but this can be overridden by specifying a different suffix/prefix in the configuration or per-entity using
             the <interfacename>@org.hibernate.envers.AuditTable</interfacename> annotation.
         </para>
 
         <orderedlist>
             <title>Audit table columns</title>
             <listitem>
                 <para>
                     id of the original entity (this can be more then one column in the case of composite primary keys)
                 </para>
             </listitem>
             <listitem>
                 <para>
                     revision number - an integer.  Matches to the revision number in the revision entity table.
                 </para>
             </listitem>
             <listitem>
                 <para>
                     revision type - a small integer
                 </para>
             </listitem>
             <listitem>
                 <para>
                     audited fields from the original entity
                 </para>
             </listitem>
         </orderedlist>
 
         <para>
             The primary key of the audit table is the combination of the original id of the entity and the revision
             number - there can be at most one historic entry for a given entity instance at a given revision.
         </para>
 
         <para>
             The current entity data is stored in the original table and in the audit table.  This is a duplication of
             data, however as this solution makes the query system much more powerful, and as memory is cheap, hopefully
             this won't be a major drawback for the users.  A row in the audit table with entity id ID, revision N and
             data D means: entity with id ID has data D from revision N upwards.  Hence, if we want to find an entity at
             revision M, we have to search for a row in the audit table, which has the revision number smaller or equal
             to M, but as large as possible. If no such row is found, or a row with a "deleted" marker is found, it means
             that the entity didn't exist at that revision.
         </para>
 
         <para>
             The "revision type" field can currently have three values: 0, 1, 2, which means ADD, MOD and DEL,
             respectively. A row with a revision of type DEL will only contain the id of the entity and no data (all
             fields NULL), as it only serves as a marker saying "this entity was deleted at that revision".
         </para>
 
         <para>
             Additionally, there is a revision entity table which contains the information about the
             global revision.  By default the generated table is named <database class="table">REVINFO</database> and
             contains just 2 columns: <database class="field">ID</database> and <database class="field">TIMESTAMP</database>.
             A row is inserted into this table on each new revision, that is, on each commit of a transaction, which
             changes audited data.  The name of this table can be configured, the name of its columns as well as adding
             additional columns can be achieved as discussed in <xref linkend="envers-revisionlog"/>.
         </para>
 
         <para>
             While global revisions are a good way to provide correct auditing of relations, some people have pointed out
             that this may be a bottleneck in systems, where data is very often modified.  One viable solution is to
             introduce an option to have an entity "locally revisioned", that is revisions would be created for it
             independently.  This wouldn't enable correct versioning of relations, but wouldn't also require the
             <database class="table">REVINFO</database> table.  Another possibility is to introduce a notion of
             "revisioning groups": groups of entities which share revision numbering.  Each such group would have to
             consist of one or more strongly connected component of the graph induced by relations between entities.
             Your opinions on the subject are very welcome on the forum! :)
         </para>
 
     </section>
 
     <section xml:id="envers-generateschema">
         <title>Generating schema with Ant</title>
 
         <para>
             If you'd like to generate the database schema file with the Hibernate Tools Ant task,
             you'll probably notice that the generated file doesn't contain definitions of audit
             tables. To generate also the audit tables, you simply need to use
             <literal>org.hibernate.tool.ant.EnversHibernateToolTask</literal> instead of the usual
             <literal>org.hibernate.tool.ant.HibernateToolTask</literal>. The former class extends
             the latter, and only adds generation of the version entities. So you can use the task
             just as you used to.
         </para>
 
         <para>
             For example:
         </para>
 
         <programlisting><![CDATA[<target name="schemaexport" depends="build-demo"
   description="Exports a generated schema to DB and file">
   <taskdef name="hibernatetool"
     classname="org.hibernate.tool.ant.EnversHibernateToolTask"
     classpathref="build.demo.classpath"/>
 
   <hibernatetool destdir=".">
     <classpath>
       <fileset refid="lib.hibernate" />
       <path location="${build.demo.dir}" />
       <path location="${build.main.dir}" />
     </classpath>
     <jpaconfiguration persistenceunit="ConsolePU" />
     <hbm2ddl
       drop="false"
       create="true"
       export="false"
       outputfilename="versioning-ddl.sql"
       delimiter=";"
       format="true"/>
   </hibernatetool>
 </target>]]></programlisting>
 
         <para>
             Will generate the following schema:
         </para>
 
         <programlisting><![CDATA[
     create table Address (
         id integer generated by default as identity (start with 1),
         flatNumber integer,
         houseNumber integer,
         streetName varchar(255),
         primary key (id)
     );
 
     create table Address_AUD (
         id integer not null,
         REV integer not null,
         flatNumber integer,
         houseNumber integer,
         streetName varchar(255),
         REVTYPE tinyint,
         primary key (id, REV)
     );
 
     create table Person (
         id integer generated by default as identity (start with 1),
         name varchar(255),
         surname varchar(255),
         address_id integer,
         primary key (id)
     );
 
     create table Person_AUD (
         id integer not null,
         REV integer not null,
         name varchar(255),
         surname varchar(255),
         REVTYPE tinyint,
         address_id integer,
         primary key (id, REV)
     );
 
     create table REVINFO (
         REV integer generated by default as identity (start with 1),
         REVTSTMP bigint,
         primary key (REV)
     );
 
     alter table Person
         add constraint FK8E488775E4C3EA63
         foreign key (address_id)
         references Address;
     ]]></programlisting>
     </section>
 
 
     <section xml:id="envers-mappingexceptions">
         <title>Mapping exceptions</title>
 
         <section>
 
             <title>What isn't and will not be supported</title>
 
             <para>
                 Bags (the corresponding Java type is List), as they can contain non-unique elements.
                 The reason is that persisting, for example a bag of String-s, violates a principle
                 of relational databases: that each table is a set of tuples. In case of bags,
                 however (which require a join table), if there is a duplicate element, the two
                 tuples corresponding to the elements will be the same. Hibernate allows this,
                 however Envers (or more precisely: the database connector) will throw an exception
                 when trying to persist two identical elements, because of a unique constraint violation.
             </para>
 
             <para>
                 There are at least two ways out if you need bag semantics:
             </para>
 
             <orderedlist>
                 <listitem>
                     <para>
                         use an indexed collection, with the <literal>@IndexColumn</literal> annotation, or
                     </para>
                 </listitem>
                 <listitem>
                     <para>
                         provide a unique id for your elements with the <literal>@CollectionId</literal> annotation.
                     </para>
                 </listitem>
             </orderedlist>
 
         </section>
 
         <section>
 
             <title>What isn't and <emphasis>will</emphasis> be supported</title>
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 0cea5956ae..64166cd818 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1325,1901 +1325,1902 @@ public final class HbmBinder {
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
         }
 
 		boolean isLazyable = "property".equals( node.getName() ) ||
 				"component".equals( node.getName() ) ||
 				"many-to-one".equals( node.getName() ) ||
 				"one-to-one".equals( node.getName() ) ||
 				"any".equals( node.getName() );
 		if ( isLazyable ) {
 			Attribute lazyNode = node.attribute( "lazy" );
 			property.setLazy( lazyNode != null && "true".equals( lazyNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
 			LOG.debug( msg );
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuilder columns = new StringBuilder();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollection(Element node, Collection collection, String className,
 			String path, Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		// ROLENAME
 		collection.setRole(path);
 
 		Attribute inverseNode = node.attribute( "inverse" );
 		if ( inverseNode != null ) {
 			collection.setInverse( "true".equals( inverseNode.getValue() ) );
 		}
 
 		Attribute mutableNode = node.attribute( "mutable" );
 		if ( mutableNode != null ) {
 			collection.setMutable( !"false".equals( mutableNode.getValue() ) );
 		}
 
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );
 
 		Attribute orderNode = node.attribute( "order-by" );
 		if ( orderNode != null ) {
 			collection.setOrderBy( orderNode.getValue() );
 		}
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) {
 			collection.setWhere( whereNode.getValue() );
 		}
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) {
 			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		collection.setNodeName( nodeName );
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		collection.setEmbedded( embed==null || "true".equals(embed) );
 
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				collection.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find collection persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		Attribute typeNode = node.attribute( "collection-type" );
 		if ( typeNode != null ) {
 			String typeName = typeNode.getValue();
 			TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 			else {
 				collection.setTypeName( typeName );
 			}
 		}
 
 		// FETCH STRATEGY
 
 		initOuterJoinFetchSetting( node, collection );
 
 		if ( "subselect".equals( node.attributeValue("fetch") ) ) {
 			collection.setSubselectLoadable(true);
 			collection.getOwner().setSubselectLoadableCollections(true);
 		}
 
 		initLaziness( node, collection, mappings, "true", mappings.isDefaultLazy() );
 		//TODO: suck this into initLaziness!
 		if ( "extra".equals( node.attributeValue("lazy") ) ) {
 			collection.setLazy(true);
 			collection.setExtraLazy(true);
 		}
 
 		Element oneToManyNode = node.element( "one-to-many" );
 		if ( oneToManyNode != null ) {
 			OneToMany oneToMany = new OneToMany( mappings, collection.getOwner() );
 			collection.setElement( oneToMany );
 			bindOneToMany( oneToManyNode, oneToMany, mappings );
 			// we have to set up the table later!! yuck
 		}
 		else {
 			// TABLE
 			Attribute tableNode = node.attribute( "table" );
 			String tableName;
 			if ( tableNode != null ) {
 				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				tableName = mappings.getNamingStrategy().collectionTableName(
 						collection.getOwner().getEntityName(),
 						logicalOwnerTableName ,
 						null,
 						null,
 						path
 				);
 				if ( ownerTable.isQuoted() ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 			Attribute schemaNode = node.attribute( "schema" );
 			String schema = schemaNode == null ?
 					mappings.getSchemaName() : schemaNode.getValue();
 
 			Attribute catalogNode = node.attribute( "catalog" );
 			String catalog = catalogNode == null ?
 					mappings.getCatalogName() : catalogNode.getValue();
 
 			Table table = mappings.addTable(
 					schema,
 					catalog,
 					tableName,
 					getSubselect( node ),
 					false
 				);
 			collection.setCollectionTable( table );
 			bindComment(table, node);
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// SORT
 		Attribute sortedAtt = node.attribute( "sort" );
 		// unsorted, natural, comparator.class.name
 		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
 			collection.setSorted( false );
 		}
 		else {
 			collection.setSorted( true );
 			String comparatorClassName = sortedAtt.getValue();
 			if ( !comparatorClassName.equals( "natural" ) ) {
 				collection.setComparatorClassName(comparatorClassName);
 			}
 		}
 
 		// ORPHAN DELETE (used for programmer error detection)
 		Attribute cascadeAtt = node.attribute( "cascade" );
 		if ( cascadeAtt != null && cascadeAtt.getValue().indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, collection );
 		// set up second pass
 		if ( collection instanceof List ) {
 			mappings.addSecondPass( new ListSecondPass( node, mappings, (List) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof Map ) {
 			mappings.addSecondPass( new MapSecondPass( node, mappings, (Map) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof IdentifierCollection ) {
 			mappings.addSecondPass( new IdentifierCollectionSecondPass(
 					node,
 					mappings,
 					collection,
 					inheritedMetas
 				) );
 		}
 		else {
 			mappings.addSecondPass( new CollectionSecondPass( node, mappings, collection, inheritedMetas ) );
 		}
 
 		Iterator iter = node.elementIterator( "filter" );
 		while ( iter.hasNext() ) {
 			final Element filter = (Element) iter.next();
 			parseFilter( filter, collection, mappings );
 		}
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			collection.getSynchronizedTables().add(
 				( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Element element = node.element( "loader" );
 		if ( element != null ) {
 			collection.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 
 		collection.setReferencedPropertyName( node.element( "key" ).attributeValue( "property-ref" ) );
 	}
 
 	private static void initLaziness(
 			Element node,
 			Fetchable fetchable,
 			Mappings mappings,
 			String proxyVal,
 			boolean defaultLazy
 	) {
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean isLazyTrue = lazyNode == null ?
 				defaultLazy && fetchable.isLazy() : //fetch="join" overrides default laziness
 				lazyNode.getValue().equals(proxyVal); //fetch="join" overrides default laziness
 		fetchable.setLazy( isLazyTrue );
 	}
 
 	private static void initLaziness(
 			Element node,
 			ToOne fetchable,
 			Mappings mappings,
 			boolean defaultLazy
 	) {
 		if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
 			fetchable.setUnwrapProxy(true);
 			fetchable.setLazy( true );
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness( node, fetchable, mappings, "proxy", defaultLazy );
 		}
 	}
 
 	private static void bindColumnsOrFormula(Element node, SimpleValue simpleValue, String path,
 			boolean isNullable, Mappings mappings) {
 		Attribute formulaNode = node.attribute( "formula" );
 		if ( formulaNode != null ) {
 			Formula f = new Formula();
 			f.setFormula( formulaNode.getText() );
 			simpleValue.addFormula( f );
 		}
 		else {
 			bindColumns( node, simpleValue, isNullable, true, path, mappings );
 		}
 	}
 
 	private static void bindComment(Table table, Element node) {
 		Element comment = node.element("comment");
 		if (comment!=null) table.setComment( comment.getTextTrim() );
 	}
 
 	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
 			boolean isNullable, Mappings mappings) throws MappingException {
 
 		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
 		initOuterJoinFetchSetting( node, manyToOne );
 		initLaziness( node, manyToOne, mappings, true );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) {
 			manyToOne.setReferencedPropertyName( ukName.getValue() );
 		}
 		manyToOne.setReferenceToPrimaryKey( manyToOne.getReferencedPropertyName() == null );
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
 			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
 				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
 			}
 		}
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( !manyToOne.isLogicalOneToOne() ) {
 				throw new MappingException(
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToOne.setEmbedded( "true".equals( embed ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 		oneToOne.setReferenceToPrimaryKey( oneToOne.getReferencedPropertyName() == null );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
 		);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName("_identifierMapper");
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				uk.setName( Constraint.generateName( uk.generatedConstraintNamePrefix(),
 						table, uk.getColumns() ) );
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
+			toOne.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue( "on-delete" ) ) );
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 		);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			Iterator aliasesIterator = filterElement.elementIterator("aliases");
 			java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 			while (aliasesIterator.hasNext()){
 				Element alias = (Element) aliasesIterator.next();
 				aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 			}
 			if ( debugEnabled ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 			boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 			collection.addManyToManyFilter(name, condition, autoAliasInjection, aliasTables, null);
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinitionBuilder().setName( queryName )
 				.setQuery( query )
 				.setCacheable( cacheable )
 				.setCacheRegion( region )
 				.setTimeout( timeout )
 				.setFetchSize( fetchSize )
 				.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
 				.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
 				.setReadOnly( readOnly )
 				.setComment( comment )
 				.setParameterTypes( getParameterTypes( queryElem ) )
 				.createNamedQueryDefinition();
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static OptimisticLockStyle getOptimisticLockStyle(Attribute olAtt) throws MappingException {
 		if ( olAtt == null ) {
 			return OptimisticLockStyle.VERSION;
 		}
 
 		final String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return OptimisticLockStyle.VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return OptimisticLockStyle.DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return OptimisticLockStyle.ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return OptimisticLockStyle.NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable && !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.build
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		Iterator aliasesIterator = filterElement.elementIterator("aliases");
 		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 		while (aliasesIterator.hasNext()){
 			Element alias = (Element) aliasesIterator.next();
 			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 	
 	private static class ResolveUserTypeMappingSecondPass implements SecondPass{
 
 		private SimpleValue simpleValue;
 		private String typeName;
 		private Mappings mappings;
 		private Properties parameters;
 
 		public ResolveUserTypeMappingSecondPass(SimpleValue simpleValue,
 				String typeName, Mappings mappings, Properties parameters) {
 			this.simpleValue=simpleValue;
 			this.typeName=typeName;
 			this.parameters=parameters;
 			this.mappings=mappings;
 		}
 
 		@Override
 		public void doSecondPass(java.util.Map persistentClasses)
 				throws MappingException {
 			resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);		
 		}
 		
 	}
 }
diff --git a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
index 530bff1d22..ae67440487 100644
--- a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
+++ b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
@@ -1,1808 +1,1816 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <!-- Hibernate Mapping DTD.
 
 An instance of this XML document may contain mappings for an arbitrary 
 number of classes. The class mappings may contain associations to classes
 mapped in the same document or in another document. No class may be 
 mapped more than once. Each document may also contain definitions of an
 arbitrary number of queries, and import declarations of arbitrary classes. 
 
 -->
 <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
            xmlns="http://www.hibernate.org/xsd/hibernate-mapping"
            targetNamespace="http://www.hibernate.org/xsd/hibernate-mapping"
            elementFormDefault="qualified"
            version="4.0">
 
   <!--
   	The document root.
    -->
   <xs:element name="hibernate-mapping">
     <xs:complexType>
       <xs:sequence>
         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
         <!--
             <identifier-generator.../> allows customized short-naming of IdentifierGenerator implementations.
         -->
         <xs:element name="identifier-generator" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="name" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!--
         	<typedef.../> allows defining a customized type mapping for a Hibernate type. May
         	contain parameters for parameterizable types.
         -->
         <xs:element name="typedef" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:sequence>
               <xs:element name="param" minOccurs="0" maxOccurs="unbounded" type="param-element"/>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="name" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!--
         	FILTER-DEF element; top-level filter definition.
         -->
         <xs:element name="filter-def" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType mixed="true">
             <xs:sequence minOccurs="0" maxOccurs="unbounded">
               <!--
               	FILTER-PARAM element; qualifies parameters found within a FILTER-DEF
               	condition.
               -->
               <xs:element name="filter-param">
                 <xs:complexType>
                   <xs:attribute name="name" use="required" type="xs:string"/> <!-- The parameter name -->
                   <xs:attribute name="type" use="required" type="xs:string"/> <!-- The parameter type -->
                 </xs:complexType>
               </xs:element>
             </xs:sequence>
             <xs:attribute name="condition" type="xs:string"/>
             <xs:attribute name="name" use="required" type="xs:string"/> <!-- The filter name -->
           </xs:complexType>
         </xs:element>
         <!--
         	IMPORT element definition; an explicit query language "import"
         -->
         <xs:element name="import" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="rename" type="xs:string"/> <!-- default: unqualified class name -->
           </xs:complexType>
         </xs:element>
         <xs:choice minOccurs="0" maxOccurs="unbounded">
           <!--
           	Root entity mapping.  Poorly named as entities do not have to be represented by 
           	classes at all.  Mapped entities may be represented via different methodologies 
           	(POJO, Map, Dom4j).
           -->
           <xs:element name="class">
             <xs:complexType>
               <xs:sequence>
                 <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                 <xs:element name="subselect" minOccurs="0" type="xs:string"/>
                 <xs:element name="cache" minOccurs="0" type="cache-element"/>
                 <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
                 <!-- The comment element allows definition of a database table or column comment. -->
                 <xs:element name="comment" minOccurs="0" type="xs:string"/>
                 <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
                 <xs:choice>
                   <!-- Declares the id type, column and generation algorithm for an entity class.
                   If a name attribut is given, the id is exposed to the application through the 
                   named property of the class. If not, the id is only exposed to the application 
                   via Session.getIdentifier() -->
                   <xs:element name="id">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                         <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
                         <xs:element name="type" minOccurs="0" type="type-element"/>
                         <xs:element name="generator" minOccurs="0" type="generator-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="column" type="xs:string"/>
                       <xs:attribute name="length" type="xs:string"/>
                       <xs:attribute name="name" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="type" type="xs:string"/>
                       <xs:attribute name="unsaved-value" type="xs:string"/> <!-- any|none|null|undefined|0|-1|... -->
                     </xs:complexType>
                   </xs:element>
                   <!-- A composite key may be modelled by a java class with a property for each 
                   key column. The class must implement java.io.Serializable and reimplement equals() 
                   and hashCode(). -->
                   <xs:element name="composite-id">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                         <xs:choice maxOccurs="unbounded">
                           <xs:element name="key-property" type="key-property-element"/>
                           <xs:element name="key-many-to-one" type="key-many-to-one-element"/>
                         </xs:choice>
                         <xs:element name="generator" minOccurs="0" type="generator-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="class" type="xs:string"/>
                       <xs:attribute name="mapped" default="false" type="xs:boolean"/>
                       <xs:attribute name="name" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="unsaved-value" default="undefined">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="any"/>
                             <xs:enumeration value="none"/>
                             <xs:enumeration value="undefined"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                     </xs:complexType>
                   </xs:element>
                 </xs:choice>
                 <!-- Polymorphic data requires a column holding a class discriminator value. This
                 value is not directly exposed to the application. -->
                 <xs:element name="discriminator" minOccurs="0">
                   <xs:complexType>
                     <xs:sequence>
                       <xs:choice minOccurs="0">
                         <xs:element name="column" type="column-element"/>
                         <!-- The formula and subselect elements allow us to map derived properties and 
                         entities. -->
                         <xs:element name="formula" type="xs:string"/>
                       </xs:choice>
                     </xs:sequence>
                     <xs:attribute name="column" type="xs:string"/> <!-- default: "class"|none -->
                     <xs:attribute name="force" default="false" type="xs:boolean"/>
                     <xs:attribute name="formula" type="xs:string"/>
                     <xs:attribute name="insert" default="true" type="xs:boolean"/>
                     <xs:attribute name="length" type="xs:string"/>
                     <xs:attribute name="not-null" default="true" type="xs:boolean"/>
                     <xs:attribute name="type" default="string" type="xs:string"/>
                   </xs:complexType>
                 </xs:element>
                 <!-- A natural-id element allows declaration of the unique business key -->
                 <xs:element name="natural-id" minOccurs="0">
                   <xs:complexType>
                     <xs:sequence>
                       <xs:choice minOccurs="0" maxOccurs="unbounded">
                         <xs:element name="property" type="property-element"/>
                         <xs:element name="many-to-one" type="many-to-one-element"/>
                         <xs:element name="component" type="component-element"/>
                         <xs:element name="dynamic-component" type="dynamic-component-element"/>
                         <xs:element name="any" type="any-element"/>
                       </xs:choice>
                     </xs:sequence>
                     <xs:attribute name="mutable" default="false" type="xs:boolean"/>
                   </xs:complexType>
                 </xs:element>
                 <xs:choice minOccurs="0">
                   <!-- Versioned data requires a column holding a version number. This is exposed to the
                   application through a property of the Java class. -->
                   <xs:element name="version">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                         <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="column" type="xs:string"/>
                       <xs:attribute name="generated" default="never" type="generated-attribute"/>
                       <xs:attribute name="insert" type="xs:boolean"/>
                       <xs:attribute name="name" use="required" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="type" default="integer" type="xs:string"/>
                       <xs:attribute name="unsaved-value" default="undefined">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="negative"/>
                             <xs:enumeration value="null"/>
                             <xs:enumeration value="undefined"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                     </xs:complexType>
                   </xs:element>
                   <xs:element name="timestamp">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="column" type="xs:string"/>
                       <xs:attribute name="generated" default="never" type="generated-attribute"/>
                       <xs:attribute name="name" use="required" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="source" default="vm">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="db"/>
                             <xs:enumeration value="vm"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                       <xs:attribute name="unsaved-value" default="null">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="null"/>
                             <xs:enumeration value="undefined"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                     </xs:complexType>
                   </xs:element>
                 </xs:choice>
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
                 <xs:choice>
                   <xs:sequence>
                     <xs:element name="join" minOccurs="0" maxOccurs="unbounded" type="join-element"/>
                     <xs:element name="subclass" minOccurs="0" maxOccurs="unbounded" type="subclass-element"/>
                   </xs:sequence>
                   <xs:element name="joined-subclass" minOccurs="0" maxOccurs="unbounded" type="joined-subclass-element"/>
                   <xs:element name="union-subclass" minOccurs="0" maxOccurs="unbounded" type="union-subclass-element"/>
                 </xs:choice>
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
               <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
               <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
               <xs:attribute name="discriminator-value" type="xs:string"/> <!-- default: unqualified class name | none -->
               <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
               <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
               <xs:attribute name="entity-name" type="xs:string"/>
               <xs:attribute name="lazy" type="xs:boolean"/>
               <xs:attribute name="mutable" default="true" type="xs:boolean"/>
               <xs:attribute name="name" type="xs:string"/> <!-- this is the class name -->
               <xs:attribute name="node" type="xs:string"/>
               <xs:attribute name="optimistic-lock" default="version">
                 <xs:simpleType>
                   <xs:restriction base="xs:token">
                     <xs:enumeration value="all"/>
                     <xs:enumeration value="dirty"/>
                     <xs:enumeration value="none"/>
                     <xs:enumeration value="version"/>
                   </xs:restriction>
                 </xs:simpleType>
               </xs:attribute>
               <xs:attribute name="persister" type="xs:string"/>
               <xs:attribute name="polymorphism" default="implicit">
                 <xs:simpleType>
                   <xs:restriction base="xs:token">
                     <xs:enumeration value="explicit"/>
                     <xs:enumeration value="implicit"/>
                   </xs:restriction>
                 </xs:simpleType>
               </xs:attribute>
               <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
               <xs:attribute name="rowid" type="xs:string"/>
               <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
               <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
               <xs:attribute name="subselect" type="xs:string"/>
               <xs:attribute name="table" type="xs:string"/> <!-- default: unqualified classname -->
               <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
             </xs:complexType>
           </xs:element>
           <xs:element name="subclass" type="subclass-element"/>
           <xs:element name="joined-subclass" type="joined-subclass-element"/>
           <xs:element name="union-subclass" type="union-subclass-element"/>
         </xs:choice>
         <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
         <xs:choice minOccurs="0" maxOccurs="unbounded">
           <xs:element name="query" type="query-element"/>
           <xs:element name="sql-query" type="sql-query-element"/>
         </xs:choice>
         <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
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
         <xs:element name="database-object" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:sequence>
               <xs:choice>
                 <xs:element name="definition">
                   <xs:complexType>
                     <xs:attribute name="class" use="required" type="xs:string"/>
                   </xs:complexType>
                 </xs:element>
                 <xs:sequence>
                   <xs:element name="create" type="xs:string"/>
                   <xs:element name="drop" type="xs:string"/>
                 </xs:sequence>
               </xs:choice>
               <!--
                   dialect-scope element allows scoping auxiliary-objects to a particular
                   Hibernate dialect implementation.
               -->
               <xs:element name="dialect-scope" minOccurs="0" maxOccurs="unbounded">
                 <xs:complexType mixed="true">
                   <xs:simpleContent>
                     <xs:extension base="xs:string">
                       <xs:attribute name="name" use="required" type="xs:string"/>
                     </xs:extension>
                   </xs:simpleContent>
                 </xs:complexType>
               </xs:element>
             </xs:sequence>
           </xs:complexType>
         </xs:element>
       </xs:sequence>
       <xs:attribute name="auto-import" default="true" type="xs:boolean"/>
       <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
       <xs:attribute name="default-access" default="property" type="xs:string"/>
       <xs:attribute name="default-cascade" default="none" type="xs:string"/>
       <xs:attribute name="default-lazy" default="true" type="xs:boolean"/>
       <xs:attribute name="package" type="xs:string"/> <!-- default: none -->
       <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     </xs:complexType>
   </xs:element>
 
   <!-- An "any" association is a polymorphic association to any table with
   the given identifier type. The first listed column is a VARCHAR column 
   holding the name of the class (for that row). -->
   <xs:complexType name="any-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="meta-value" minOccurs="0" maxOccurs="unbounded" type="meta-value-element"/>
       <xs:element name="column" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="id-type" use="required" type="xs:string"/>
     <xs:attribute name="index" type="xs:string"/> <!-- include the columns spanned by this association in an index -->
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="lazy" default="false" type="xs:boolean"/>
     <xs:attribute name="meta-type" type="xs:string"/> <!--- default: Hibernate.STRING -->
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <xs:complexType name="array-element">
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
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="element-class" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
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
 
   <xs:complexType name="bag-element">
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
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!-- The cache element enables caching of an entity class. -->
   <xs:complexType name="cache-element">
     <xs:attribute name="include" default="all">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="all"/>
           <xs:enumeration value="non-lazy"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
     <xs:attribute name="region" type="xs:string"/> <!-- default: class or collection role name -->
     <xs:attribute name="usage" use="required">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="nonstrict-read-write"/>
           <xs:enumeration value="read-only"/>
           <xs:enumeration value="read-write"/>
           <xs:enumeration value="transactional"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
   </xs:complexType>
 
   <!-- The column element is an alternative to column attributes and required for 
   mapping associations to classes with composite ids. -->
   <xs:complexType name="column-element">
     <xs:sequence>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
     </xs:sequence>
     <xs:attribute name="check" type="xs:string"/> <!-- default: no check constraint -->
     <xs:attribute name="default" type="xs:string"/> <!-- default: no default value -->
     <xs:attribute name="index" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/> <!-- default: 255 -->
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="not-null" type="xs:boolean"/> <!-- default: false (except for id properties) -->
     <xs:attribute name="precision" type="xs:string"/>
     <xs:attribute name="read" type="xs:string"/> <!-- default: column name -->
     <xs:attribute name="scale" type="xs:string"/>
     <xs:attribute name="sql-type" type="xs:string"/> <!-- override default column type for hibernate type -->
     <xs:attribute name="unique" type="xs:boolean"/> <!-- default: false (except for id properties) -->
     <xs:attribute name="unique-key" type="xs:string"/> <!-- default: no unique key -->
     <xs:attribute name="write" type="xs:string"/> <!-- default: parameter placeholder ('?') -->
   </xs:complexType>
 
   <!-- A component is a user-defined class, persisted along with its containing entity
   to the table of the entity class. JavaBeans style properties of the component are
   mapped to columns of the table of the containing entity. A null component reference
   is mapped to null values in all columns and vice versa. Components do not support
   shared reference semantics. -->
   <xs:complexType name="component-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:element name="parent" minOccurs="0" type="parent-element"/>
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
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="lazy" default="false" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- A composite element allows a collection to hold instances of an arbitrary 
   class, without the requirement of joining to an entity table. Composite elements
   have component semantics - no shared references and ad hoc null value semantics. 
   Composite elements may not hold nested collections. -->
   <xs:complexType name="composite-element-element">
     <xs:sequence>
       <xs:sequence>
         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       </xs:sequence>
       <xs:element name="parent" minOccurs="0" type="parent-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="nested-composite-element" type="nested-composite-element-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="class" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
   </xs:complexType>
 
   <!-- A dynamic-component maps columns of the database entity to a java.util.Map 
   at the Java level -->
   <xs:complexType name="dynamic-component-element">
     <xs:sequence>
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
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- Declares the element type of a collection of basic type -->
   <xs:complexType name="element-element">
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
     <xs:attribute name="not-null" default="false" type="xs:boolean"/>
     <xs:attribute name="precision" type="xs:string"/>
     <xs:attribute name="scale" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
   </xs:complexType>
 
   <!--
   -->
   <xs:complexType name="fetch-profile-element">
     <xs:sequence>
       <!--
           The <fetch> element defines a single path to which the fetch
           refers, as well as the style of fetch to apply.  The 'root' of the
           path is different depending upon the context in which the
           containing <fetch-profile/> occurs; within a <class/> element,
           the entity-name of the containing class mapping is assumed...
       -->
       <xs:element name="fetch" minOccurs="0" maxOccurs="unbounded">
         <xs:complexType>
           <xs:attribute name="association" use="required" type="xs:string"/>
           <xs:attribute name="entity" type="xs:string"/> <!-- Implied as long as the containing fetch profile is contained in a class mapping -->
           <xs:attribute name="style" default="join">
             <xs:simpleType>
               <xs:restriction base="xs:token">
                 <xs:enumeration value="join"/>
                 <xs:enumeration value="select"/>
               </xs:restriction>
             </xs:simpleType>
           </xs:attribute>
         </xs:complexType>
       </xs:element>
     </xs:sequence>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	FILTER element; used to apply a filter.
   -->
   <xs:complexType name="filter-element" mixed="true">
     <xs:sequence>
     	<xs:element name="aliases" type="alias-table" minOccurs="0" maxOccurs="unbounded"/>
     </xs:sequence>
   	<xs:attribute name="condition" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="autoAliasInjection" default="true" type="xs:string"/>
   </xs:complexType>
   
   <xs:complexType name="alias-table">
   	<xs:simpleContent>
   		<xs:extension base="xs:string">
   			<xs:attribute name="alias" type="xs:string" use="required"/>
   			<xs:attribute name="table" type="xs:string" use="required"/>
   		</xs:extension>	
   	</xs:simpleContent>
   </xs:complexType>
 
   <!-- Generators generate unique identifiers. The class attribute specifies a Java 
   class implementing an id generation algorithm. -->
   <xs:complexType name="generator-element">
     <xs:sequence>
       <xs:element name="param" minOccurs="0" maxOccurs="unbounded" type="param-element"/>
     </xs:sequence>
     <xs:attribute name="class" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="idbag-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="collection-id">
         <xs:complexType>
           <xs:sequence>
             <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
             <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
             <xs:element name="generator" type="generator-element"/>
           </xs:sequence>
           <xs:attribute name="column" use="required" type="xs:string"/>
           <xs:attribute name="length" type="xs:string"/>
           <xs:attribute name="type" use="required" type="xs:string"/>
         </xs:complexType>
       </xs:element>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
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
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <xs:complexType name="index-element">
     <xs:sequence>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/> <!-- required for maps -->
   </xs:complexType>
 
   <!-- A join allows some properties of a class to be persisted to a second table -->
   <xs:complexType name="join-element">
     <xs:sequence>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="any" type="any-element"/>
       </xs:choice>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
     </xs:sequence>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
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
+    <xs:attribute name="on-delete" default="noaction">
+      <xs:simpleType>
+        <xs:restriction base="xs:token">
+          <xs:enumeration value="cascade"/>
+          <xs:enumeration value="noaction"/>
+        </xs:restriction>
+      </xs:simpleType>
+    </xs:attribute>
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
       <xs:enumeration value="upgrade-skiplocked"/>
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
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/GlobalConfiguration.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/GlobalConfiguration.java
index 1bcf57eb30..61516f9b71 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/GlobalConfiguration.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/GlobalConfiguration.java
@@ -1,198 +1,208 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.envers.configuration.internal;
 
 import java.util.Properties;
 
 import org.hibernate.MappingException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.envers.RevisionListener;
 import org.hibernate.envers.configuration.EnversSettings;
 import org.hibernate.envers.internal.tools.ReflectionTools;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  * @author Nicolas Doroskevich
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  * @author Michal Skowronek (mskowr at o2 dot pl)
  */
 public class GlobalConfiguration {
 	// Should a revision be generated when a not-owned relation field changes
 	private final boolean generateRevisionsForCollections;
 
 	// Should the optimistic locking property of an entity be considered unversioned
 	private final boolean doNotAuditOptimisticLockingField;
 
 	// Should entity data be stored when it is deleted
 	private final boolean storeDataAtDelete;
 
 	// The default name of the schema of audit tables.
 	private final String defaultSchemaName;
 
 	// The default name of the catalog of the audit tables.
 	private final String defaultCatalogName;
 
 	// Should Envers track (persist) entity names that have been changed during each revision.
 	private boolean trackEntitiesChangedInRevision;
 
 	// Revision listener class name.
 	private final Class<? extends RevisionListener> revisionListenerClass;
 
 	// Should Envers use modified property flags by default
 	private boolean globalWithModifiedFlag;
 
 	// Indicates that user defined global behavior for modified flags feature
 	private boolean hasGlobalSettingForWithModifiedFlag;
 
 	// Suffix to be used for modified flags columns
 	private String modifiedFlagSuffix;
 
 	// Use revision entity with native id generator
 	private final boolean useRevisionEntityWithNativeId;
+	
+	// While deleting revision entry, remove data of associated audited entities
+	private final boolean cascadeDeleteRevision;
 
 	// Support reused identifiers of previously deleted entities
 	private final boolean allowIdentifierReuse;
 
 	/*
 		 Which operator to use in correlated subqueries (when we want a property to be equal to the result of
 		 a correlated subquery, for example: e.p <operator> (select max(e2.p) where e2.p2 = e.p2 ...).
 		 Normally, this should be "=". However, HSQLDB has an issue related to that, so as a workaround,
 		 "in" is used. See {@link org.hibernate.envers.test.various.HsqlTest}.
 	*/
 	private final String correlatedSubqueryOperator;
 
 	public GlobalConfiguration(Properties properties, ClassLoaderService classLoaderService) {
 		generateRevisionsForCollections = ConfigurationHelper.getBoolean(
 				EnversSettings.REVISION_ON_COLLECTION_CHANGE, properties, true
 		);
 
 		doNotAuditOptimisticLockingField = ConfigurationHelper.getBoolean(
 				EnversSettings.DO_NOT_AUDIT_OPTIMISTIC_LOCKING_FIELD, properties, true
 		);
 
 		storeDataAtDelete = ConfigurationHelper.getBoolean( EnversSettings.STORE_DATA_AT_DELETE, properties, false );
 
 		defaultSchemaName = properties.getProperty( EnversSettings.DEFAULT_SCHEMA, null );
 		defaultCatalogName = properties.getProperty( EnversSettings.DEFAULT_CATALOG, null );
 
 		correlatedSubqueryOperator = HSQLDialect.class.getName()
 				.equals( properties.get( Environment.DIALECT ) ) ? "in" : "=";
 
 		trackEntitiesChangedInRevision = ConfigurationHelper.getBoolean(
 				EnversSettings.TRACK_ENTITIES_CHANGED_IN_REVISION, properties, false
 		);
+		
+		cascadeDeleteRevision = ConfigurationHelper.getBoolean(
+				"org.hibernate.envers.cascade_delete_revision", properties, false );
 
 		useRevisionEntityWithNativeId = ConfigurationHelper.getBoolean(
 				EnversSettings.USE_REVISION_ENTITY_WITH_NATIVE_ID, properties, true
 		);
 
 		hasGlobalSettingForWithModifiedFlag = properties.get( EnversSettings.GLOBAL_WITH_MODIFIED_FLAG ) != null;
 		globalWithModifiedFlag = ConfigurationHelper.getBoolean(
 				EnversSettings.GLOBAL_WITH_MODIFIED_FLAG, properties, false
 		);
 		modifiedFlagSuffix = ConfigurationHelper.getString(
 				EnversSettings.MODIFIED_FLAG_SUFFIX, properties, "_MOD"
 		);
 
 		final String revisionListenerClassName = properties.getProperty( EnversSettings.REVISION_LISTENER, null );
 		if ( revisionListenerClassName != null ) {
 			try {
 				revisionListenerClass = ReflectionTools.loadClass( revisionListenerClassName, classLoaderService );
 			}
 			catch (ClassLoadingException e) {
 				throw new MappingException(
 						"Revision listener class not found: " + revisionListenerClassName + ".",
 						e
 				);
 			}
 		}
 		else {
 			revisionListenerClass = null;
 		}
 
 		allowIdentifierReuse = ConfigurationHelper.getBoolean(
 				EnversSettings.ALLOW_IDENTIFIER_REUSE, properties, false
 		);
 	}
 
 	public boolean isGenerateRevisionsForCollections() {
 		return generateRevisionsForCollections;
 	}
 
 	public boolean isDoNotAuditOptimisticLockingField() {
 		return doNotAuditOptimisticLockingField;
 	}
 
 	public String getCorrelatedSubqueryOperator() {
 		return correlatedSubqueryOperator;
 	}
 
 	public boolean isStoreDataAtDelete() {
 		return storeDataAtDelete;
 	}
 
 	public String getDefaultSchemaName() {
 		return defaultSchemaName;
 	}
 
 	public String getDefaultCatalogName() {
 		return defaultCatalogName;
 	}
 
 	public boolean isTrackEntitiesChangedInRevision() {
 		return trackEntitiesChangedInRevision;
 	}
 
 	public void setTrackEntitiesChangedInRevision(boolean trackEntitiesChangedInRevision) {
 		this.trackEntitiesChangedInRevision = trackEntitiesChangedInRevision;
 	}
 
 	public Class<? extends RevisionListener> getRevisionListenerClass() {
 		return revisionListenerClass;
 	}
 
 	public boolean hasSettingForUsingModifiedFlag() {
 		return hasGlobalSettingForWithModifiedFlag;
 	}
 
 	public boolean isGlobalWithModifiedFlag() {
 		return globalWithModifiedFlag;
 	}
 
 	public String getModifiedFlagSuffix() {
 		return modifiedFlagSuffix;
 	}
 
 	public boolean isUseRevisionEntityWithNativeId() {
 		return useRevisionEntityWithNativeId;
 	}
+	
+	public boolean isCascadeDeleteRevision() {
+		return cascadeDeleteRevision;
+	}
 
 	public boolean isAllowIdentifierReuse() {
 		return allowIdentifierReuse;
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/AuditMetadataGenerator.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/AuditMetadataGenerator.java
index a117e85c96..926a81ac26 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/AuditMetadataGenerator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/internal/metadata/AuditMetadataGenerator.java
@@ -1,777 +1,780 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.envers.configuration.internal.metadata;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.dom4j.Element;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.envers.RelationTargetAuditMode;
 import org.hibernate.envers.configuration.internal.AuditEntitiesConfiguration;
 import org.hibernate.envers.configuration.internal.GlobalConfiguration;
 import org.hibernate.envers.configuration.internal.metadata.reader.ClassAuditingData;
 import org.hibernate.envers.configuration.internal.metadata.reader.PropertyAuditingData;
 import org.hibernate.envers.internal.EnversMessageLogger;
 import org.hibernate.envers.internal.entities.EntityConfiguration;
 import org.hibernate.envers.internal.entities.IdMappingData;
 import org.hibernate.envers.internal.entities.mapper.CompositeMapperBuilder;
 import org.hibernate.envers.internal.entities.mapper.ExtendedPropertyMapper;
 import org.hibernate.envers.internal.entities.mapper.MultiPropertyMapper;
 import org.hibernate.envers.internal.entities.mapper.SubclassPropertyMapper;
 import org.hibernate.envers.internal.tools.StringTools;
 import org.hibernate.envers.internal.tools.Triple;
 import org.hibernate.envers.strategy.AuditStrategy;
 import org.hibernate.envers.strategy.ValidityAuditStrategy;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.ManyToOneType;
 import org.hibernate.type.OneToOneType;
 import org.hibernate.type.TimestampType;
 import org.hibernate.type.Type;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  * @author Sebastian Komander
  * @author Tomasz Bech
  * @author Stephanie Pau at Markit Group Plc
  * @author Hern&aacute;n Chanfreau
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  * @author Michal Skowronek (mskowr at o2 dot pl)
  */
 public final class AuditMetadataGenerator {
 	private static final EnversMessageLogger LOG = Logger.getMessageLogger(
 			EnversMessageLogger.class,
 			AuditMetadataGenerator.class.getName()
 	);
 
 	private final Configuration cfg;
 	private final GlobalConfiguration globalCfg;
 	private final AuditEntitiesConfiguration verEntCfg;
 	private final AuditStrategy auditStrategy;
 	private final ClassLoaderService classLoaderService;
 	private final Element revisionInfoRelationMapping;
 
 	/*
 	 * Generators for different kinds of property values/types.
 	 */
 	private final BasicMetadataGenerator basicMetadataGenerator;
 	private final ComponentMetadataGenerator componentMetadataGenerator;
 	private final IdMetadataGenerator idMetadataGenerator;
 	private final ToOneRelationMetadataGenerator toOneRelationMetadataGenerator;
 
 	/*
 	 * Here information about already generated mappings will be accumulated.
 	 */
 	private final Map<String, EntityConfiguration> entitiesConfigurations;
 	private final Map<String, EntityConfiguration> notAuditedEntitiesConfigurations;
 
 	private final AuditEntityNameRegister auditEntityNameRegister;
 
 	// Map entity name -> (join descriptor -> element describing the "versioned" join)
 	private final Map<String, Map<Join, Element>> entitiesJoins;
 
 	public AuditMetadataGenerator(
 			Configuration cfg, GlobalConfiguration globalCfg,
 			AuditEntitiesConfiguration verEntCfg,
 			AuditStrategy auditStrategy, ClassLoaderService classLoaderService,
 			Element revisionInfoRelationMapping,
 			AuditEntityNameRegister auditEntityNameRegister) {
 		this.cfg = cfg;
 		this.globalCfg = globalCfg;
 		this.verEntCfg = verEntCfg;
 		this.auditStrategy = auditStrategy;
 		this.classLoaderService = classLoaderService;
 		this.revisionInfoRelationMapping = revisionInfoRelationMapping;
 
 		this.basicMetadataGenerator = new BasicMetadataGenerator();
 		this.componentMetadataGenerator = new ComponentMetadataGenerator( this );
 		this.idMetadataGenerator = new IdMetadataGenerator( this );
 		this.toOneRelationMetadataGenerator = new ToOneRelationMetadataGenerator( this );
 
 		this.auditEntityNameRegister = auditEntityNameRegister;
 
 		entitiesConfigurations = new HashMap<String, EntityConfiguration>();
 		notAuditedEntitiesConfigurations = new HashMap<String, EntityConfiguration>();
 		entitiesJoins = new HashMap<String, Map<Join, Element>>();
 	}
 
 	/**
 	 * Clones the revision info relation mapping, so that it can be added to other mappings. Also, the name of
 	 * the property and the column are set properly.
 	 *
 	 * @return A revision info mapping, which can be added to other mappings (has no parent).
 	 */
 	private Element cloneAndSetupRevisionInfoRelationMapping() {
 		final Element revMapping = (Element) revisionInfoRelationMapping.clone();
 		revMapping.addAttribute( "name", verEntCfg.getRevisionFieldName() );
+		if ( globalCfg.isCascadeDeleteRevision() ) {
+			revMapping.addAttribute( "on-delete", "cascade" );
+	    } 
 
 		MetadataTools.addOrModifyColumn( revMapping, verEntCfg.getRevisionFieldName() );
 
 		return revMapping;
 	}
 
 	void addRevisionInfoRelation(Element anyMapping) {
 		anyMapping.add( cloneAndSetupRevisionInfoRelationMapping() );
 	}
 
 	void addRevisionType(Element anyMapping, Element anyMappingEnd) {
 		final Element revTypeProperty = MetadataTools.addProperty(
 				anyMapping,
 				verEntCfg.getRevisionTypePropName(),
 				verEntCfg.getRevisionTypePropType(),
 				true,
 				false
 		);
 		revTypeProperty.addAttribute( "type", "org.hibernate.envers.internal.entities.RevisionTypeType" );
 
 		// Adding the end revision, if appropriate
 		addEndRevision( anyMappingEnd );
 	}
 
 	private void addEndRevision(Element anyMapping) {
 		// Add the end-revision field, if the appropriate strategy is used.
 		if ( auditStrategy instanceof ValidityAuditStrategy ) {
 			final Element endRevMapping = (Element) revisionInfoRelationMapping.clone();
 			endRevMapping.setName( "many-to-one" );
 			endRevMapping.addAttribute( "name", verEntCfg.getRevisionEndFieldName() );
 			MetadataTools.addOrModifyColumn( endRevMapping, verEntCfg.getRevisionEndFieldName() );
 
 			anyMapping.add( endRevMapping );
 
 			if ( verEntCfg.isRevisionEndTimestampEnabled() ) {
 				// add a column for the timestamp of the end revision
 				final String revisionInfoTimestampSqlType = TimestampType.INSTANCE.getName();
 				final Element timestampProperty = MetadataTools.addProperty(
 						anyMapping,
 						verEntCfg.getRevisionEndTimestampFieldName(),
 						revisionInfoTimestampSqlType,
 						true,
 						true,
 						false
 				);
 				MetadataTools.addColumn(
 						timestampProperty,
 						verEntCfg.getRevisionEndTimestampFieldName(),
 						null,
 						null,
 						null,
 						null,
 						null,
 						null
 				);
 			}
 		}
 	}
 
 	private void addValueInFirstPass(
 			Element parent,
 			Value value,
 			CompositeMapperBuilder currentMapper,
 			String entityName,
 			EntityXmlMappingData xmlMappingData,
 			PropertyAuditingData propertyAuditingData,
 			boolean insertable,
 			boolean processModifiedFlag) {
 		final Type type = value.getType();
 		final boolean isBasic = basicMetadataGenerator.addBasic(
 				parent,
 				propertyAuditingData,
 				value,
 				currentMapper,
 				insertable,
 				false
 		);
 
 		if ( isBasic ) {
 			// The property was mapped by the basic generator.
 		}
 		else if ( type instanceof ComponentType ) {
 			componentMetadataGenerator.addComponent(
 					parent, propertyAuditingData, value, currentMapper,
 					entityName, xmlMappingData, true
 			);
 		}
 		else {
 			if ( !processedInSecondPass( type ) ) {
 				// If we got here in the first pass, it means the basic mapper didn't map it, and none of the
 				// above branches either.
 				throwUnsupportedTypeException( type, entityName, propertyAuditingData.getName() );
 			}
 			return;
 		}
 		addModifiedFlagIfNeeded( parent, propertyAuditingData, processModifiedFlag );
 	}
 
 	private boolean processedInSecondPass(Type type) {
 		return type instanceof ComponentType || type instanceof ManyToOneType ||
 				type instanceof OneToOneType || type instanceof CollectionType;
 	}
 
 	private void addValueInSecondPass(
 			Element parent,
 			Value value,
 			CompositeMapperBuilder currentMapper,
 			String entityName,
 			EntityXmlMappingData xmlMappingData,
 			PropertyAuditingData propertyAuditingData,
 			boolean insertable,
 			boolean processModifiedFlag) {
 		final Type type = value.getType();
 
 		if ( type instanceof ComponentType ) {
 			componentMetadataGenerator.addComponent(
 					parent,
 					propertyAuditingData,
 					value,
 					currentMapper,
 					entityName,
 					xmlMappingData,
 					false
 			);
 			// mod flag field has been already generated in first pass
 			return;
 		}
 		else if ( type instanceof ManyToOneType ) {
 			toOneRelationMetadataGenerator.addToOne(
 					parent,
 					propertyAuditingData,
 					value,
 					currentMapper,
 					entityName,
 					insertable
 			);
 		}
 		else if ( type instanceof OneToOneType ) {
 			final OneToOne oneToOne = (OneToOne) value;
 			if ( oneToOne.getReferencedPropertyName() != null ) {
 				toOneRelationMetadataGenerator.addOneToOneNotOwning(
 						propertyAuditingData,
 						value,
 						currentMapper,
 						entityName
 				);
 			}
 			else {
 				// @OneToOne relation marked with @PrimaryKeyJoinColumn
 				toOneRelationMetadataGenerator.addOneToOnePrimaryKeyJoinColumn(
 						propertyAuditingData,
 						value,
 						currentMapper,
 						entityName,
 						insertable
 				);
 			}
 		}
 		else if ( type instanceof CollectionType ) {
 			final CollectionMetadataGenerator collectionMetadataGenerator = new CollectionMetadataGenerator(
 					this,
 					(Collection) value,
 					currentMapper,
 					entityName,
 					xmlMappingData,
 					propertyAuditingData
 			);
 			collectionMetadataGenerator.addCollection();
 		}
 		else {
 			return;
 		}
 		addModifiedFlagIfNeeded( parent, propertyAuditingData, processModifiedFlag );
 	}
 
 	private void addModifiedFlagIfNeeded(
 			Element parent,
 			PropertyAuditingData propertyAuditingData,
 			boolean processModifiedFlag) {
 		if ( processModifiedFlag && propertyAuditingData.isUsingModifiedFlag() ) {
 			MetadataTools.addModifiedFlagProperty(
 					parent,
 					propertyAuditingData.getName(),
 					globalCfg.getModifiedFlagSuffix()
 			);
 		}
 	}
 
 	void addValue(
 			Element parent, Value value, CompositeMapperBuilder currentMapper, String entityName,
 			EntityXmlMappingData xmlMappingData, PropertyAuditingData propertyAuditingData,
 			boolean insertable, boolean firstPass, boolean processModifiedFlag) {
 		if ( firstPass ) {
 			addValueInFirstPass(
 					parent, value, currentMapper, entityName,
 					xmlMappingData, propertyAuditingData, insertable, processModifiedFlag
 			);
 		}
 		else {
 			addValueInSecondPass(
 					parent, value, currentMapper, entityName,
 					xmlMappingData, propertyAuditingData, insertable, processModifiedFlag
 			);
 		}
 	}
 
 	private void addProperties(
 			Element parent,
 			Iterator<Property> properties,
 			CompositeMapperBuilder currentMapper,
 			ClassAuditingData auditingData,
 			String entityName,
 			EntityXmlMappingData xmlMappingData,
 			boolean firstPass) {
 		while ( properties.hasNext() ) {
 			final Property property = properties.next();
 			final String propertyName = property.getName();
 			final PropertyAuditingData propertyAuditingData = auditingData.getPropertyAuditingData( propertyName );
 			if ( propertyAuditingData != null ) {
 				addValue(
 						parent,
 						property.getValue(),
 						currentMapper,
 						entityName,
 						xmlMappingData,
 						propertyAuditingData,
 						property.isInsertable(),
 						firstPass,
 						true
 				);
 			}
 		}
 	}
 
 	private boolean checkPropertiesAudited(Iterator<Property> properties, ClassAuditingData auditingData) {
 		while ( properties.hasNext() ) {
 			final Property property = properties.next();
 			final String propertyName = property.getName();
 			final PropertyAuditingData propertyAuditingData = auditingData.getPropertyAuditingData( propertyName );
 			if ( propertyAuditingData == null ) {
 				return false;
 			}
 		}
 
 		return true;
 	}
 
 	protected String getSchema(String schemaFromAnnotation, Table table) {
 		// Get the schema from the annotation ...
 		String schema = schemaFromAnnotation;
 		// ... if empty, try using the default ...
 		if ( StringTools.isEmpty( schema ) ) {
 			schema = globalCfg.getDefaultSchemaName();
 
 			// ... if still empty, use the same as the normal table.
 			if ( StringTools.isEmpty( schema ) ) {
 				schema = table.getSchema();
 			}
 		}
 
 		return schema;
 	}
 
 	protected String getCatalog(String catalogFromAnnotation, Table table) {
 		// Get the catalog from the annotation ...
 		String catalog = catalogFromAnnotation;
 		// ... if empty, try using the default ...
 		if ( StringTools.isEmpty( catalog ) ) {
 			catalog = globalCfg.getDefaultCatalogName();
 
 			// ... if still empty, use the same as the normal table.
 			if ( StringTools.isEmpty( catalog ) ) {
 				catalog = table.getCatalog();
 			}
 		}
 
 		return catalog;
 	}
 
 	@SuppressWarnings({"unchecked"})
 	private void createJoins(PersistentClass pc, Element parent, ClassAuditingData auditingData) {
 		final Iterator<Join> joins = pc.getJoinIterator();
 		final Map<Join, Element> joinElements = new HashMap<Join, Element>();
 		entitiesJoins.put( pc.getEntityName(), joinElements );
 
 		while ( joins.hasNext() ) {
 			Join join = joins.next();
 
 			// Checking if all of the join properties are audited
 			if ( !checkPropertiesAudited( join.getPropertyIterator(), auditingData ) ) {
 				continue;
 			}
 
 			// Determining the table name. If there is no entry in the dictionary, just constructing the table name
 			// as if it was an entity (by appending/prepending configured strings).
 			final String originalTableName = join.getTable().getName();
 			String auditTableName = auditingData.getSecondaryTableDictionary().get( originalTableName );
 			if ( auditTableName == null ) {
 				auditTableName = verEntCfg.getAuditEntityName( originalTableName );
 			}
 
 			final String schema = getSchema( auditingData.getAuditTable().schema(), join.getTable() );
 			final String catalog = getCatalog( auditingData.getAuditTable().catalog(), join.getTable() );
 
 			final Element joinElement = MetadataTools.createJoin( parent, auditTableName, schema, catalog );
 			joinElements.put( join, joinElement );
 
 			final Element joinKey = joinElement.addElement( "key" );
 			MetadataTools.addColumns( joinKey, join.getKey().getColumnIterator() );
 			MetadataTools.addColumn( joinKey, verEntCfg.getRevisionFieldName(), null, null, null, null, null, null );
 		}
 	}
 
 	@SuppressWarnings({"unchecked"})
 	private void addJoins(
 			PersistentClass pc,
 			CompositeMapperBuilder currentMapper,
 			ClassAuditingData auditingData,
 			String entityName,
 			EntityXmlMappingData xmlMappingData,
 			boolean firstPass) {
 		final Iterator<Join> joins = pc.getJoinIterator();
 
 		while ( joins.hasNext() ) {
 			final Join join = joins.next();
 			final Element joinElement = entitiesJoins.get( entityName ).get( join );
 
 			if ( joinElement != null ) {
 				addProperties(
 						joinElement,
 						join.getPropertyIterator(),
 						currentMapper,
 						auditingData,
 						entityName,
 						xmlMappingData,
 						firstPass
 				);
 			}
 		}
 	}
 
 	@SuppressWarnings({"unchecked"})
 	private Triple<Element, ExtendedPropertyMapper, String> generateMappingData(
 			PersistentClass pc, EntityXmlMappingData xmlMappingData, AuditTableData auditTableData,
 			IdMappingData idMapper) {
 		final Element classMapping = MetadataTools.createEntity(
 				xmlMappingData.getMainXmlMapping(),
 				auditTableData,
 				pc.getDiscriminatorValue(),
 				pc.isAbstract()
 		);
 		final ExtendedPropertyMapper propertyMapper = new MultiPropertyMapper();
 
 		// Checking if there is a discriminator column
 		if ( pc.getDiscriminator() != null ) {
 			final Element discriminatorElement = classMapping.addElement( "discriminator" );
 			// Database column or SQL formula allowed to distinguish entity types
 			MetadataTools.addColumnsOrFormulas( discriminatorElement, pc.getDiscriminator().getColumnIterator() );
 			discriminatorElement.addAttribute( "type", pc.getDiscriminator().getType().getName() );
 		}
 
 		// Adding the id mapping
 		classMapping.add( (Element) idMapper.getXmlMapping().clone() );
 
 		// Adding the "revision type" property
 		addRevisionType( classMapping, classMapping );
 
 		return Triple.make( classMapping, propertyMapper, null );
 	}
 
 	private Triple<Element, ExtendedPropertyMapper, String> generateInheritanceMappingData(
 			PersistentClass pc, EntityXmlMappingData xmlMappingData, AuditTableData auditTableData,
 			String inheritanceMappingType) {
 		final String extendsEntityName = verEntCfg.getAuditEntityName( pc.getSuperclass().getEntityName() );
 		final Element classMapping = MetadataTools.createSubclassEntity(
 				xmlMappingData.getMainXmlMapping(),
 				inheritanceMappingType,
 				auditTableData,
 				extendsEntityName,
 				pc.getDiscriminatorValue(),
 				pc.isAbstract()
 		);
 
 		// The id and revision type is already mapped in the parent
 
 		// Getting the property mapper of the parent - when mapping properties, they need to be included
 		final String parentEntityName = pc.getSuperclass().getEntityName();
 
 		final EntityConfiguration parentConfiguration = entitiesConfigurations.get( parentEntityName );
 		if ( parentConfiguration == null ) {
 			throw new MappingException(
 					"Entity '" + pc.getEntityName() + "' is audited, but its superclass: '" +
 							parentEntityName + "' is not."
 			);
 		}
 
 		final ExtendedPropertyMapper parentPropertyMapper = parentConfiguration.getPropertyMapper();
 		final ExtendedPropertyMapper propertyMapper = new SubclassPropertyMapper(
 				new MultiPropertyMapper(),
 				parentPropertyMapper
 		);
 
 		return Triple.make( classMapping, propertyMapper, parentEntityName );
 	}
 
 	@SuppressWarnings({"unchecked"})
 	public void generateFirstPass(
 			PersistentClass pc,
 			ClassAuditingData auditingData,
 			EntityXmlMappingData xmlMappingData,
 			boolean isAudited) {
 		final String schema = getSchema( auditingData.getAuditTable().schema(), pc.getTable() );
 		final String catalog = getCatalog( auditingData.getAuditTable().catalog(), pc.getTable() );
 
 		if ( !isAudited ) {
 			final String entityName = pc.getEntityName();
 			final IdMappingData idMapper = idMetadataGenerator.addId( pc, false );
 
 			if ( idMapper == null ) {
 				// Unsupported id mapping, e.g. key-many-to-one. If the entity is used in auditing, an exception
 				// will be thrown later on.
 				LOG.debugf(
 						"Unable to create auditing id mapping for entity %s, because of an unsupported Hibernate id mapping (e.g. key-many-to-one)",
 						entityName
 				);
 				return;
 			}
 
 			final ExtendedPropertyMapper propertyMapper = null;
 			final String parentEntityName = null;
 			final EntityConfiguration entityCfg = new EntityConfiguration(
 					entityName,
 					pc.getClassName(),
 					idMapper,
 					propertyMapper,
 					parentEntityName
 			);
 			notAuditedEntitiesConfigurations.put( entityName, entityCfg );
 			return;
 		}
 
 		final String entityName = pc.getEntityName();
 		LOG.debugf( "Generating first-pass auditing mapping for entity %s", entityName );
 
 		final String auditEntityName = verEntCfg.getAuditEntityName( entityName );
 		final String auditTableName = verEntCfg.getAuditTableName( entityName, pc.getTable().getName() );
 
 		// Registering the audit entity name, now that it is known
 		auditEntityNameRegister.register( auditEntityName );
 
 		final AuditTableData auditTableData = new AuditTableData( auditEntityName, auditTableName, schema, catalog );
 
 		// Generating a mapping for the id
 		final IdMappingData idMapper = idMetadataGenerator.addId( pc, true );
 
 		final InheritanceType inheritanceType = InheritanceType.get( pc );
 
 		// These properties will be read from the mapping data
 		final Element classMapping;
 		final ExtendedPropertyMapper propertyMapper;
 		final String parentEntityName;
 
 		final Triple<Element, ExtendedPropertyMapper, String> mappingData;
 
 		// Reading the mapping data depending on inheritance type (if any)
 		switch ( inheritanceType ) {
 			case NONE:
 				mappingData = generateMappingData( pc, xmlMappingData, auditTableData, idMapper );
 				break;
 
 			case SINGLE:
 				mappingData = generateInheritanceMappingData( pc, xmlMappingData, auditTableData, "subclass" );
 				break;
 
 			case JOINED:
 				mappingData = generateInheritanceMappingData( pc, xmlMappingData, auditTableData, "joined-subclass" );
 
 				// Adding the "key" element with all id columns...
 				final Element keyMapping = mappingData.getFirst().addElement( "key" );
 				MetadataTools.addColumns( keyMapping, pc.getTable().getPrimaryKey().columnIterator() );
 
 				// ... and the revision number column, read from the revision info relation mapping.
 				keyMapping.add( (Element) cloneAndSetupRevisionInfoRelationMapping().element( "column" ).clone() );
 				break;
 
 			case TABLE_PER_CLASS:
 				mappingData = generateInheritanceMappingData( pc, xmlMappingData, auditTableData, "union-subclass" );
 				break;
 
 			default:
 				throw new AssertionError( "Impossible enum value." );
 		}
 
 		classMapping = mappingData.getFirst();
 		propertyMapper = mappingData.getSecond();
 		parentEntityName = mappingData.getThird();
 
 		xmlMappingData.setClassMapping( classMapping );
 
 		// Mapping unjoined properties
 		addProperties(
 				classMapping, pc.getUnjoinedPropertyIterator(), propertyMapper,
 				auditingData, pc.getEntityName(), xmlMappingData,
 				true
 		);
 
 		// Creating and mapping joins (first pass)
 		createJoins( pc, classMapping, auditingData );
 		addJoins( pc, propertyMapper, auditingData, pc.getEntityName(), xmlMappingData, true );
 
 		// Storing the generated configuration
 		final EntityConfiguration entityCfg = new EntityConfiguration(
 				auditEntityName,
 				pc.getClassName(),
 				idMapper,
 				propertyMapper,
 				parentEntityName
 		);
 		entitiesConfigurations.put( pc.getEntityName(), entityCfg );
 	}
 
 	@SuppressWarnings({"unchecked"})
 	public void generateSecondPass(
 			PersistentClass pc,
 			ClassAuditingData auditingData,
 			EntityXmlMappingData xmlMappingData) {
 		final String entityName = pc.getEntityName();
 		LOG.debugf( "Generating second-pass auditing mapping for entity %s", entityName );
 
 		final CompositeMapperBuilder propertyMapper = entitiesConfigurations.get( entityName ).getPropertyMapper();
 
 		// Mapping unjoined properties
 		final Element parent = xmlMappingData.getClassMapping();
 
 		addProperties(
 				parent,
 				pc.getUnjoinedPropertyIterator(),
 				propertyMapper,
 				auditingData,
 				entityName,
 				xmlMappingData,
 				false
 		);
 
 		// Mapping joins (second pass)
 		addJoins( pc, propertyMapper, auditingData, entityName, xmlMappingData, false );
 	}
 
 	public Map<String, EntityConfiguration> getEntitiesConfigurations() {
 		return entitiesConfigurations;
 	}
 
 	// Getters for generators and configuration
 
 	BasicMetadataGenerator getBasicMetadataGenerator() {
 		return basicMetadataGenerator;
 	}
 
 	Configuration getCfg() {
 		return cfg;
 	}
 
 	GlobalConfiguration getGlobalCfg() {
 		return globalCfg;
 	}
 
 	AuditEntitiesConfiguration getVerEntCfg() {
 		return verEntCfg;
 	}
 
 	AuditStrategy getAuditStrategy() {
 		return auditStrategy;
 	}
 
 	ClassLoaderService getClassLoaderService() {
 		return classLoaderService;
 	}
 
 	AuditEntityNameRegister getAuditEntityNameRegister() {
 		return auditEntityNameRegister;
 	}
 
 	void throwUnsupportedTypeException(Type type, String entityName, String propertyName) {
 		final String message = "Type not supported for auditing: " + type.getClass().getName() +
 				", on entity " + entityName + ", property '" + propertyName + "'.";
 
 		throw new MappingException( message );
 	}
 
 	/**
 	 * Reads the id mapping data of a referenced entity.
 	 *
 	 * @param entityName Name of the entity which is the source of the relation.
 	 * @param referencedEntityName Name of the entity which is the target of the relation.
 	 * @param propertyAuditingData Auditing data of the property that is the source of the relation.
 	 * @param allowNotAuditedTarget Are not-audited target entities allowed.
 	 *
 	 * @return The id mapping data of the related entity.
 	 *
 	 * @throws MappingException If a relation from an audited to a non-audited entity is detected, which is not
 	 * mapped using {@link RelationTargetAuditMode#NOT_AUDITED}.
 	 */
 	IdMappingData getReferencedIdMappingData(
 			String entityName, String referencedEntityName,
 			PropertyAuditingData propertyAuditingData,
 			boolean allowNotAuditedTarget) {
 		EntityConfiguration configuration = getEntitiesConfigurations().get( referencedEntityName );
 		if ( configuration == null ) {
 			final RelationTargetAuditMode relationTargetAuditMode = propertyAuditingData.getRelationTargetAuditMode();
 			configuration = getNotAuditedEntitiesConfigurations().get( referencedEntityName );
 
 			if ( configuration == null || !allowNotAuditedTarget || !RelationTargetAuditMode.NOT_AUDITED.equals(
 					relationTargetAuditMode
 			) ) {
 				throw new MappingException(
 						"An audited relation from " + entityName + "."
 								+ propertyAuditingData.getName() + " to a not audited entity " + referencedEntityName + "!"
 								+ (allowNotAuditedTarget ?
 								" Such mapping is possible, but has to be explicitly defined using @Audited(targetAuditMode = NOT_AUDITED)." :
 								"")
 				);
 			}
 		}
 
 		return configuration.getIdMappingData();
 	}
 
 	/**
 	 * Get the notAuditedEntitiesConfigurations property.
 	 *
 	 * @return the notAuditedEntitiesConfigurations property value
 	 */
 	public Map<String, EntityConfiguration> getNotAuditedEntitiesConfigurations() {
 		return notAuditedEntitiesConfigurations;
 	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/AbstractRevisionEntityRemovalTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/AbstractRevisionEntityRemovalTest.java
new file mode 100644
index 0000000000..2b8b5b48a0
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/AbstractRevisionEntityRemovalTest.java
@@ -0,0 +1,108 @@
+package org.hibernate.envers.test.integration.reventity.removal;
+
+import java.util.ArrayList;
+import java.util.Map;
+import javax.persistence.EntityManager;
+
+import org.junit.Assert;
+import org.junit.Test;
+
+import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
+import org.hibernate.envers.test.Priority;
+import org.hibernate.envers.test.entities.StrTestEntity;
+import org.hibernate.envers.test.entities.manytomany.ListOwnedEntity;
+import org.hibernate.envers.test.entities.manytomany.ListOwningEntity;
+import org.hibernate.testing.DialectChecks;
+import org.hibernate.testing.RequiresDialectFeature;
+import org.hibernate.testing.TestForIssue;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@TestForIssue( jiraKey = "HHH-7807" )
+@RequiresDialectFeature(DialectChecks.SupportsCascadeDeleteCheck.class)
+public abstract class AbstractRevisionEntityRemovalTest extends BaseEnversJPAFunctionalTestCase {
+	@Override
+	protected void addConfigOptions(Map options) {
+		options.put( "org.hibernate.envers.cascade_delete_revision", "true" );
+	}
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+				StrTestEntity.class, ListOwnedEntity.class, ListOwningEntity.class,
+				getRevisionEntityClass()
+		};
+	}
+
+	@Test
+	@Priority(10)
+	public void initData() {
+		EntityManager em = getEntityManager();
+
+		// Revision 1 - simple entity
+		em.getTransaction().begin();
+		em.persist( new StrTestEntity( "data" ) );
+		em.getTransaction().commit();
+
+		// Revision 2 - many-to-many relation
+		em.getTransaction().begin();
+		ListOwnedEntity owned = new ListOwnedEntity( 1, "data" );
+		ListOwningEntity owning = new ListOwningEntity( 1, "data" );
+		owned.setReferencing( new ArrayList<ListOwningEntity>() );
+		owning.setReferences( new ArrayList<ListOwnedEntity>() );
+		owned.getReferencing().add( owning );
+		owning.getReferences().add( owned );
+		em.persist( owned );
+		em.persist( owning );
+		em.getTransaction().commit();
+
+		em.getTransaction().begin();
+		Assert.assertEquals( 1, countRecords( em, "STR_TEST_AUD" ) );
+		Assert.assertEquals( 1, countRecords( em, "ListOwned_AUD" ) );
+		Assert.assertEquals( 1, countRecords( em, "ListOwning_AUD" ) );
+		Assert.assertEquals( 1, countRecords( em, "ListOwning_ListOwned_AUD" ) );
+		em.getTransaction().commit();
+
+		em.close();
+	}
+
+	@Test
+	@Priority(9)
+	public void testRemoveExistingRevisions() {
+		EntityManager em = getEntityManager();
+		removeRevision( em, 1 );
+		removeRevision( em, 2 );
+		em.close();
+	}
+
+	@Test
+	@Priority(8)
+	public void testEmptyAuditTables() {
+		EntityManager em = getEntityManager();
+		em.getTransaction().begin();
+
+		Assert.assertEquals( 0, countRecords( em, "STR_TEST_AUD" ) );
+		Assert.assertEquals( 0, countRecords( em, "ListOwned_AUD" ) );
+		Assert.assertEquals( 0, countRecords( em, "ListOwning_AUD" ) );
+		Assert.assertEquals( 0, countRecords( em, "ListOwning_ListOwned_AUD" ) );
+
+		em.getTransaction().commit();
+		em.close();
+	}
+
+	private int countRecords(EntityManager em, String tableName) {
+		return ( (Number) em.createNativeQuery( "SELECT COUNT(*) FROM " + tableName ).getSingleResult() ).intValue();
+	}
+
+	private void removeRevision(EntityManager em, Number number) {
+		em.getTransaction().begin();
+		Object entity = em.find( getRevisionEntityClass(), number );
+		Assert.assertNotNull( entity );
+		em.remove( entity );
+		em.getTransaction().commit();
+		Assert.assertNull( em.find( getRevisionEntityClass(), number ) );
+	}
+
+	protected abstract Class<?> getRevisionEntityClass();
+}
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/RemoveDefaultRevisionEntity.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/RemoveDefaultRevisionEntity.java
new file mode 100644
index 0000000000..af2cf50ad6
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/RemoveDefaultRevisionEntity.java
@@ -0,0 +1,13 @@
+package org.hibernate.envers.test.integration.reventity.removal;
+
+import org.hibernate.envers.enhanced.SequenceIdRevisionEntity;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+public class RemoveDefaultRevisionEntity extends AbstractRevisionEntityRemovalTest {
+	@Override
+	protected Class<?> getRevisionEntityClass() {
+		return SequenceIdRevisionEntity.class;
+	}
+}
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/RemoveTrackingRevisionEntity.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/RemoveTrackingRevisionEntity.java
new file mode 100644
index 0000000000..05f4a1bf9a
--- /dev/null
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/reventity/removal/RemoveTrackingRevisionEntity.java
@@ -0,0 +1,21 @@
+package org.hibernate.envers.test.integration.reventity.removal;
+
+import java.util.Map;
+
+import org.hibernate.envers.enhanced.SequenceIdTrackingModifiedEntitiesRevisionEntity;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+public class RemoveTrackingRevisionEntity extends AbstractRevisionEntityRemovalTest {
+	@Override
+	public void addConfigOptions(Map configuration) {
+		super.addConfigOptions( configuration );
+		configuration.put("org.hibernate.envers.track_entities_changed_in_revision", "true");
+	}
+
+	@Override
+	protected Class<?> getRevisionEntityClass() {
+		return SequenceIdTrackingModifiedEntitiesRevisionEntity.class;
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java b/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
index f9e29799d8..3616d96d18 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/DialectChecks.java
@@ -1,172 +1,178 @@
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
 			return dialect.supportsIdentityColumns();
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
 
+	public static class SupportsCascadeDeleteCheck implements DialectCheck {
+		public boolean isMatch(Dialect dialect) {
+			return dialect.supportsCascadeDelete();
+		}
+	}
+
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
 }
