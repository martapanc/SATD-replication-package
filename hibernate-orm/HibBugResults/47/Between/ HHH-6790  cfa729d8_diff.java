diff --git a/documentation/src/main/docbook/manual/en-US/content/basic_mapping.xml b/documentation/src/main/docbook/manual/en-US/content/basic_mapping.xml
index 90ae93e598..5a6ce34ccb 100644
--- a/documentation/src/main/docbook/manual/en-US/content/basic_mapping.xml
+++ b/documentation/src/main/docbook/manual/en-US/content/basic_mapping.xml
@@ -887,2000 +887,2013 @@ class CustomerId implements Serializable {
    //implements equals and hashCode
 }
 
 @Entity 
 class User {
    @EmbeddedId UserId id;
    Integer age;
 }
 
 @Embeddable
 class UserId implements Serializable {
    String firstName;
    String lastName;
 
 
    //implements equals and hashCode
 }</programlisting>
 
           <para>Let's now rewrite these examples using the hbm.xml
           syntax.</para>
 
           <programlisting role="XML">&lt;composite-id
         name="propertyName"
         class="ClassName"
         mapped="true|false"
         access="field|property|ClassName"
         node="element-name|."&gt;
 
         &lt;key-property name="propertyName" type="typename" column="column_name"/&gt;
         &lt;key-many-to-one name="propertyName" class="ClassName" column="column_name"/&gt;
         ......
 &lt;/composite-id&gt;</programlisting>
 
           <para>First a simple example:</para>
 
           <programlisting role="XML">&lt;class name="User"&gt;
    &lt;composite-id name="id" class="UserId"&gt;
       &lt;key-property name="firstName" column="fld_firstname"/&gt;
       &lt;key-property name="lastName"/&gt;
    &lt;/composite-id&gt;
 &lt;/class&gt;</programlisting>
 
           <para>Then an example showing how an association can be
           mapped.</para>
 
           <programlisting role="XML">&lt;class name="Customer"&gt;
    &lt;composite-id name="id" class="CustomerId"&gt;
       &lt;key-property name="firstName" column="userfirstname_fk"/&gt;
       &lt;key-property name="lastName" column="userfirstname_fk"/&gt;
       &lt;key-property name="customerNumber"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="preferredCustomer"/&gt;
 
    &lt;many-to-one name="user"&gt;
       &lt;column name="userfirstname_fk" updatable="false" insertable="false"/&gt;
       &lt;column name="userlastname_fk" updatable="false" insertable="false"/&gt;
    &lt;/many-to-one&gt;
 &lt;/class&gt;
 
 &lt;class name="User"&gt;
    &lt;composite-id name="id" class="UserId"&gt;
       &lt;key-property name="firstName"/&gt;
       &lt;key-property name="lastName"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="age"/&gt;
 &lt;/class&gt;</programlisting>
 
           <para>Notice a few things in the previous example:</para>
 
           <itemizedlist>
             <listitem>
               <para>the order of the properties (and column) matters. It must
               be the same between the association and the primary key of the
               associated entity</para>
             </listitem>
 
             <listitem>
               <para>the many to one uses the same columns as the primary key
               and thus must be marked as read only
               (<literal>insertable</literal> and <literal>updatable</literal>
               to false).</para>
             </listitem>
 
             <listitem>
               <para>unlike with <classname>@MapsId</classname>, the id value
               of the associated entity is not transparently copied, check the
               <literal>foreign</literal> id generator for more
               information.</para>
             </listitem>
           </itemizedlist>
 
           <para>The last example shows how to map association directly in the
           embedded id component.</para>
 
           <programlisting role="XML">&lt;class name="Customer"&gt;
    &lt;composite-id name="id" class="CustomerId"&gt;
       &lt;key-many-to-one name="user"&gt;
          &lt;column name="userfirstname_fk"/&gt;
          &lt;column name="userlastname_fk"/&gt;
       &lt;/key-many-to-one&gt;
       &lt;key-property name="customerNumber"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="preferredCustomer"/&gt;
 &lt;/class&gt;
 
 &lt;class name="User"&gt;
    &lt;composite-id name="id" class="UserId"&gt;
       &lt;key-property name="firstName"/&gt;
       &lt;key-property name="lastName"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="age"/&gt;
 &lt;/class&gt;</programlisting>
 
           <para>This is the recommended approach to map composite identifier.
           The following options should not be considered unless some
           constraint are present.</para>
         </section>
 
         <section>
           <title>Multiple id properties without identifier type</title>
 
           <para>Another, arguably more natural, approach is to place
           <classname>@Id</classname> on multiple properties of your entity.
           This approach is only supported by Hibernate (not JPA compliant) but
           does not require an extra embeddable component.</para>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 class Customer implements Serializable {
    @Id @OneToOne
    @JoinColumns({
       @JoinColumn(name="userfirstname_fk", referencedColumnName="firstName"),
       @JoinColumn(name="userlastname_fk", referencedColumnName="lastName")
    })
    User user;
   
    @Id String customerNumber;
 
    boolean preferredCustomer;
 
    //implements equals and hashCode
 }
 
 @Entity 
 class User {
    @EmbeddedId UserId id;
    Integer age;
 }
 
 @Embeddable
 class UserId implements Serializable {
    String firstName;
    String lastName;
 
    //implements equals and hashCode
 }</programlisting>
 
           <para>In this case <classname>Customer</classname> is its own
           identifier representation: it must implement
           <classname>Serializable</classname> and must implement
           <methodname>equals()</methodname> and
           <methodname>hashCode()</methodname>.</para>
 
           <para>In hbm.xml, the same mapping is:</para>
 
           <programlisting role="XML">&lt;class name="Customer"&gt;
    &lt;composite-id&gt;
       &lt;key-many-to-one name="user"&gt;
          &lt;column name="userfirstname_fk"/&gt;
          &lt;column name="userlastname_fk"/&gt;
       &lt;/key-many-to-one&gt;
       &lt;key-property name="customerNumber"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="preferredCustomer"/&gt;
 &lt;/class&gt;
 
 &lt;class name="User"&gt;
    &lt;composite-id name="id" class="UserId"&gt;
       &lt;key-property name="firstName"/&gt;
       &lt;key-property name="lastName"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="age"/&gt;
 &lt;/class&gt;</programlisting>
         </section>
 
         <section>
           <title>Multiple id properties with with a dedicated identifier
           type</title>
 
           <para><classname>@IdClass</classname> on an entity points to the
           class (component) representing the identifier of the class. The
           properties marked <classname>@Id</classname> on the entity must have
           their corresponding property on the <classname>@IdClass</classname>.
           The return type of search twin property must be either identical for
           basic properties or must correspond to the identifier class of the
           associated entity for an association.</para>
 
           <warning>
             <para>This approach is inherited from the EJB 2 days and we
             recommend against its use. But, after all it's your application
             and Hibernate supports it.</para>
           </warning>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 @IdClass(CustomerId.class)
 class Customer implements Serializable {
    @Id @OneToOne
    @JoinColumns({
       @JoinColumn(name="userfirstname_fk", referencedColumnName="firstName"),
       @JoinColumn(name="userlastname_fk", referencedColumnName="lastName")
    }) 
    User user;
   
    @Id String customerNumber;
 
    boolean preferredCustomer;
 }
 
 class CustomerId implements Serializable {
    UserId user;
    String customerNumber;
 
    //implements equals and hashCode
 }
 
 @Entity 
 class User {
    @EmbeddedId UserId id;
    Integer age;
 
    //implements equals and hashCode
 }
 
 @Embeddable
 class UserId implements Serializable {
    String firstName;
    String lastName;
 
    //implements equals and hashCode
 }</programlisting>
 
           <para><classname>Customer</classname> and
           <classname>CustomerId</classname> do have the same properties
           <literal>customerNumber</literal> as well as
           <literal>user</literal>. <classname>CustomerId</classname> must be
           <classname>Serializable</classname> and implement
           <classname>equals()</classname> and
           <classname>hashCode()</classname>.</para>
 
           <para>While not JPA standard, Hibernate let's you declare the
           vanilla associated property in the
           <classname>@IdClass</classname>.</para>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 @IdClass(CustomerId.class)
 class Customer implements Serializable {
    @Id @OneToOne
    @JoinColumns({
       @JoinColumn(name="userfirstname_fk", referencedColumnName="firstName"),
       @JoinColumn(name="userlastname_fk", referencedColumnName="lastName")
    }) 
    User user;
   
    @Id String customerNumber;
 
    boolean preferredCustomer;
 }
 
 class CustomerId implements Serializable {
    @OneToOne User user;
    String customerNumber;
 
    //implements equals and hashCode
 }
 
 @Entity 
 class User {
    @EmbeddedId UserId id;
    Integer age;
 
    //implements equals and hashCode
 }
 
 @Embeddable
 class UserId implements Serializable {
   String firstName;
   String lastName;
 }</programlisting>
 
           <para>This feature is of limited interest though as you are likely
           to have chosen the <classname>@IdClass</classname> approach to stay
           JPA compliant or you have a quite twisted mind.</para>
 
           <para>Here are the equivalent on hbm.xml files:</para>
 
           <programlisting role="XML">&lt;class name="Customer"&gt;
    &lt;composite-id class="CustomerId" mapped="true"&gt;
       &lt;key-many-to-one name="user"&gt;
          &lt;column name="userfirstname_fk"/&gt;
          &lt;column name="userlastname_fk"/&gt;
       &lt;/key-many-to-one&gt;
       &lt;key-property name="customerNumber"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="preferredCustomer"/&gt;
 &lt;/class&gt;
 
 &lt;class name="User"&gt;
    &lt;composite-id name="id" class="UserId"&gt;
       &lt;key-property name="firstName"/&gt;
       &lt;key-property name="lastName"/&gt;
    &lt;/composite-id&gt;
 
    &lt;property name="age"/&gt;
 &lt;/class&gt;</programlisting>
         </section>
       </section>
 
       <section xml:id="mapping-declaration-id-generator" revision="2">
         <title>Identifier generator</title>
 
         <para>Hibernate can generate and populate identifier values for you
         automatically. This is the recommended approach over "business" or
         "natural" id (especially composite ids).</para>
 
         <para>Hibernate offers various generation strategies, let's explore
         the most common ones first that happens to be standardized by
         JPA:</para>
 
         <itemizedlist>
           <listitem>
             <para>IDENTITY: supports identity columns in DB2, MySQL, MS SQL
             Server, Sybase and HypersonicSQL. The returned identifier is of
             type <literal>long</literal>, <literal>short</literal> or
             <literal>int</literal>.</para>
           </listitem>
 
           <listitem>
             <para>SEQUENCE (called <literal>seqhilo</literal> in Hibernate):
             uses a hi/lo algorithm to efficiently generate identifiers of type
             <literal>long</literal>, <literal>short</literal> or
             <literal>int</literal>, given a named database sequence.</para>
           </listitem>
 
           <listitem>
             <para>TABLE (called
             <classname>MultipleHiLoPerTableGenerator</classname> in Hibernate)
             : uses a hi/lo algorithm to efficiently generate identifiers of
             type <literal>long</literal>, <literal>short</literal> or
             <literal>int</literal>, given a table and column as a source of hi
             values. The hi/lo algorithm generates identifiers that are unique
             only for a particular database.</para>
           </listitem>
 
           <listitem>
             <para>AUTO: selects <literal>IDENTITY</literal>,
             <literal>SEQUENCE</literal> or <literal>TABLE</literal> depending
             upon the capabilities of the underlying database.</para>
           </listitem>
         </itemizedlist>
 
         <important>
           <para>We recommend all new projects to use the new enhanced
           identifier generators. They are deactivated by default for entities
           using annotations but can be activated using
           <code>hibernate.id.new_generator_mappings=true</code>. These new
           generators are more efficient and closer to the JPA 2 specification
           semantic.</para>
 
           <para>However they are not backward compatible with existing
           Hibernate based application (if a sequence or a table is used for id
           generation). See XXXXXXX <xref linkend="ann-setup-properties" /> for
           more information on how to activate them.</para>
         </important>
 
         <para>To mark an id property as generated, use the
         <classname>@GeneratedValue</classname> annotation. You can specify the
         strategy used (default to <literal>AUTO</literal>) by setting
         <literal>strategy</literal>.</para>
 
         <programlisting role="JAVA">@Entity
 public class Customer {
    @Id @GeneratedValue
    Integer getId() { ... };
 }
 
 @Entity 
 public class Invoice {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Integer getId() { ... };
 }</programlisting>
 
         <para><literal>SEQUENCE</literal> and <literal>TABLE</literal> require
         additional configurations that you can set using
         <classname>@SequenceGenerator</classname> and
         <classname>@TableGenerator</classname>:</para>
 
         <itemizedlist>
           <listitem>
             <para><literal>name</literal>: name of the generator</para>
           </listitem>
 
           <listitem>
             <para><literal>table</literal> / <literal>sequenceName</literal>:
             name of the table or the sequence (defaulting respectively to
             <literal>hibernate_sequences</literal> and
             <literal>hibernate_sequence</literal>)</para>
           </listitem>
 
           <listitem>
             <para><literal>catalog</literal> /
             <literal>schema</literal>:</para>
           </listitem>
 
           <listitem>
             <para><literal>initialValue</literal>: the value from which the id
             is to start generating</para>
           </listitem>
 
           <listitem>
             <para><literal>allocationSize</literal>: the amount to increment
             by when allocating id numbers from the generator</para>
           </listitem>
         </itemizedlist>
 
         <para>In addition, the <classname>TABLE</classname> strategy also let
         you customize:</para>
 
         <itemizedlist>
           <listitem>
             <para><literal>pkColumnName</literal>: the column name containing
             the entity identifier</para>
           </listitem>
 
           <listitem>
             <para><literal>valueColumnName</literal>: the column name
             containing the identifier value</para>
           </listitem>
 
           <listitem>
             <para><literal>pkColumnValue</literal>: the entity
             identifier</para>
           </listitem>
 
           <listitem>
             <para><literal>uniqueConstraints</literal>: any potential column
             constraint on the table containing the ids</para>
           </listitem>
         </itemizedlist>
 
         <para>To link a table or sequence generator definition with an actual
         generated property, use the same name in both the definition
         <literal>name</literal> and the generator value
         <literal>generator</literal> as shown below.</para>
 
         <programlisting language="JAVA" role="JAVA">@Id 
 @GeneratedValue(
     strategy=GenerationType.SEQUENCE, 
     generator="SEQ_GEN")
 @javax.persistence.SequenceGenerator(
     name="SEQ_GEN",
     sequenceName="my_sequence",
     allocationSize=20
 )
 public Integer getId() { ... }        </programlisting>
 
         <para>The scope of a generator definition can be the application or
         the class. Class-defined generators are not visible outside the class
         and can override application level generators. Application level
         generators are defined in JPA's XML deployment descriptors (see XXXXXX
         <xref linkend="xml-overriding" />):</para>
 
         <programlisting language="JAVA" role="JAVA">&lt;table-generator name="EMP_GEN"
             table="GENERATOR_TABLE"
             pk-column-name="key"
             value-column-name="hi"
             pk-column-value="EMP"
             allocation-size="20"/&gt;
 
 //and the annotation equivalent
 
 @javax.persistence.TableGenerator(
     name="EMP_GEN",
     table="GENERATOR_TABLE",
     pkColumnName = "key",
     valueColumnName = "hi"
     pkColumnValue="EMP",
     allocationSize=20
 )
 
 &lt;sequence-generator name="SEQ_GEN" 
     sequence-name="my_sequence"
     allocation-size="20"/&gt;
 
 //and the annotation equivalent
 
 @javax.persistence.SequenceGenerator(
     name="SEQ_GEN",
     sequenceName="my_sequence",
     allocationSize=20
 )
          </programlisting>
 
         <para>If a JPA XML descriptor (like
         <filename>META-INF/orm.xml</filename>) is used to define the
         generators, <literal>EMP_GEN</literal> and <literal>SEQ_GEN</literal>
         are application level generators.</para>
 
         <note>
           <para>Package level definition is not supported by the JPA
           specification. However, you can use the
           <literal>@GenericGenerator</literal> at the package level (see <xref
           linkend="entity-hibspec-identifier" />).</para>
         </note>
 
         <para>These are the four standard JPA generators. Hibernate goes
         beyond that and provide additional generators or additional options as
         we will see below. You can also write your own custom identifier
         generator by implementing
         <classname>org.hibernate.id.IdentifierGenerator</classname>.</para>
 
         <para>To define a custom generator, use the
         <classname>@GenericGenerator</classname> annotation (and its plural
         counter part <classname>@GenericGenerators</classname>) that describes
         the class of the identifier generator or its short cut name (as
         described below) and a list of key/value parameters. When using
         <classname>@GenericGenerator</classname> and assigning it via
         <classname>@GeneratedValue.generator</classname>, the
         <classname>@GeneratedValue.strategy</classname> is ignored: leave it
         blank.</para>
 
         <programlisting language="JAVA" role="JAVA">@Id @GeneratedValue(generator="system-uuid")
 @GenericGenerator(name="system-uuid", strategy = "uuid")
 public String getId() {
 
 @Id @GeneratedValue(generator="trigger-generated")
 @GenericGenerator(
     name="trigger-generated", 
     strategy = "select",
     parameters = @Parameter(name="key", value = "socialSecurityNumber")
 )
 public String getId() {</programlisting>
 
         <para>The hbm.xml approach uses the optional
         <literal>&lt;generator&gt;</literal> child element inside
         <literal>&lt;id&gt;</literal>. If any parameters are required to
         configure or initialize the generator instance, they are passed using
         the <literal>&lt;param&gt;</literal> element.</para>
 
         <programlisting role="XML">&lt;id name="id" type="long" column="cat_id"&gt;
         &lt;generator class="org.hibernate.id.TableHiLoGenerator"&gt;
                 &lt;param name="table"&gt;uid_table&lt;/param&gt;
                 &lt;param name="column"&gt;next_hi_value_column&lt;/param&gt;
         &lt;/generator&gt;
 &lt;/id&gt;</programlisting>
 
         <section>
           <title>Various additional generators</title>
 
           <para>All generators implement the interface
           <literal>org.hibernate.id.IdentifierGenerator</literal>. This is a
           very simple interface. Some applications can choose to provide their
           own specialized implementations, however, Hibernate provides a range
           of built-in implementations. The shortcut names for the built-in
           generators are as follows: <variablelist>
               <varlistentry>
                 <term><literal>increment</literal></term>
 
                 <listitem>
                   <para>generates identifiers of type <literal>long</literal>,
                   <literal>short</literal> or <literal>int</literal> that are
                   unique only when no other process is inserting data into the
                   same table. <emphasis>Do not use in a
                   cluster.</emphasis></para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>identity</literal></term>
 
                 <listitem>
                   <para>supports identity columns in DB2, MySQL, MS SQL
                   Server, Sybase and HypersonicSQL. The returned identifier is
                   of type <literal>long</literal>, <literal>short</literal> or
                   <literal>int</literal>.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>sequence</literal></term>
 
                 <listitem>
                   <para>uses a sequence in DB2, PostgreSQL, Oracle, SAP DB,
                   McKoi or a generator in Interbase. The returned identifier
                   is of type <literal>long</literal>, <literal>short</literal>
                   or <literal>int</literal></para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>hilo</literal></term>
 
                 <listitem>
                   <para xml:id="mapping-declaration-id-hilodescription"
                   revision="1">uses a hi/lo algorithm to efficiently generate
                   identifiers of type <literal>long</literal>,
                   <literal>short</literal> or <literal>int</literal>, given a
                   table and column (by default
                   <literal>hibernate_unique_key</literal> and
                   <literal>next_hi</literal> respectively) as a source of hi
                   values. The hi/lo algorithm generates identifiers that are
                   unique only for a particular database.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>seqhilo</literal></term>
 
                 <listitem>
                   <para>uses a hi/lo algorithm to efficiently generate
                   identifiers of type <literal>long</literal>,
                   <literal>short</literal> or <literal>int</literal>, given a
                   named database sequence.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>uuid</literal></term>
 
                 <listitem>
                   <para>Generates a 128-bit UUID based on a custom algorithm.
                   The value generated is represented as a string of 32
                   hexidecimal digits. Users can also configure it to use a
                   separator (config parameter "separator") which separates the
                   hexidecimal digits into 8{sep}8{sep}4{sep}8{sep}4. Note
                   specifically that this is different than the IETF RFC 4122
                   representation of 8-4-4-4-12. If you need RFC 4122 compliant
                   UUIDs, consider using "uuid2" generator discussed
                   below.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>uuid2</literal></term>
 
                 <listitem>
                   <para>Generates a IETF RFC 4122 compliant (variant 2)
                   128-bit UUID. The exact "version" (the RFC term) generated
                   depends on the pluggable "generation strategy" used (see
                   below). Capable of generating values as
                   <classname>java.util.UUID</classname>,
                   <classname>java.lang.String</classname> or as a byte array
                   of length 16 (<literal>byte[16]</literal>). The "generation
                   strategy" is defined by the interface
                   <interfacename>org.hibernate.id.UUIDGenerationStrategy</interfacename>.
                   The generator defines 2 configuration parameters for
                   defining which generation strategy to use: <variablelist>
                       <varlistentry>
                         <term><literal>uuid_gen_strategy_class</literal></term>
 
                         <listitem>
                           <para>Names the UUIDGenerationStrategy class to
                           use</para>
                         </listitem>
                       </varlistentry>
 
                       <varlistentry>
                         <term><literal>uuid_gen_strategy</literal></term>
 
                         <listitem>
                           <para>Names the UUIDGenerationStrategy instance to
                           use</para>
                         </listitem>
                       </varlistentry>
                     </variablelist></para>
 
                   <para>Out of the box, comes with the following strategies:
                   <itemizedlist>
                       <listitem>
                         <para><classname>org.hibernate.id.uuid.StandardRandomStrategy</classname>
                         (the default) - generates "version 3" (aka, "random")
                         UUID values via the
                         <methodname>randomUUID</methodname> method of
                         <classname>java.util.UUID</classname></para>
                       </listitem>
 
                       <listitem>
                         <para><classname>org.hibernate.id.uuid.CustomVersionOneStrategy</classname>
                         - generates "version 1" UUID values, using IP address
                         since mac address not available. If you need mac
                         address to be used, consider leveraging one of the
                         existing third party UUID generators which sniff out
                         mac address and integrating it via the
                         <interfacename>org.hibernate.id.UUIDGenerationStrategy</interfacename>
                         contract. Two such libraries known at time of this
                         writing include <link xl:href="http://johannburkard.de/software/uuid/"/>
                         and <link xl:href="http://commons.apache.org/sandbox/id/uuid.html"/>
                         </para>
                       </listitem>
                     </itemizedlist></para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>guid</literal></term>
 
                 <listitem>
                   <para>uses a database-generated GUID string on MS SQL Server
                   and MySQL.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>native</literal></term>
 
                 <listitem>
                   <para>selects <literal>identity</literal>,
                   <literal>sequence</literal> or <literal>hilo</literal>
                   depending upon the capabilities of the underlying
                   database.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>assigned</literal></term>
 
                 <listitem>
                   <para>lets the application assign an identifier to the
                   object before <literal>save()</literal> is called. This is
                   the default strategy if no
                   <literal>&lt;generator&gt;</literal> element is
                   specified.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>select</literal></term>
 
                 <listitem>
                   <para>retrieves a primary key, assigned by a database
                   trigger, by selecting the row by some unique key and
                   retrieving the primary key value.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>foreign</literal></term>
 
                 <listitem>
                   <para>uses the identifier of another associated object. It
                   is usually used in conjunction with a
                   <literal>&lt;one-to-one&gt;</literal> primary key
                   association.</para>
                 </listitem>
               </varlistentry>
 
               <varlistentry>
                 <term><literal>sequence-identity</literal></term>
 
                 <listitem>
                   <para>a specialized sequence generation strategy that
                   utilizes a database sequence for the actual value
                   generation, but combines this with JDBC3 getGeneratedKeys to
                   return the generated identifier value as part of the insert
                   statement execution. This strategy is only supported on
                   Oracle 10g drivers targeted for JDK 1.4. Comments on these
                   insert statements are disabled due to a bug in the Oracle
                   drivers.</para>
                 </listitem>
               </varlistentry>
             </variablelist></para>
         </section>
 
         <section xml:id="mapping-declaration-id-hilo" revision="1">
           <title>Hi/lo algorithm</title>
 
           <para>The <literal>hilo</literal> and <literal>seqhilo</literal>
           generators provide two alternate implementations of the hi/lo
           algorithm. The first implementation requires a "special" database
           table to hold the next available "hi" value. Where supported, the
           second uses an Oracle-style sequence.</para>
 
           <programlisting role="XML">&lt;id name="id" type="long" column="cat_id"&gt;
         &lt;generator class="hilo"&gt;
                 &lt;param name="table"&gt;hi_value&lt;/param&gt;
                 &lt;param name="column"&gt;next_value&lt;/param&gt;
                 &lt;param name="max_lo"&gt;100&lt;/param&gt;
         &lt;/generator&gt;
 &lt;/id&gt;</programlisting>
 
           <programlisting role="XML">&lt;id name="id" type="long" column="cat_id"&gt;
         &lt;generator class="seqhilo"&gt;
                 &lt;param name="sequence"&gt;hi_value&lt;/param&gt;
                 &lt;param name="max_lo"&gt;100&lt;/param&gt;
         &lt;/generator&gt;
 &lt;/id&gt;</programlisting>
 
           <para>Unfortunately, you cannot use <literal>hilo</literal> when
           supplying your own <literal>Connection</literal> to Hibernate. When
           Hibernate uses an application server datasource to obtain
           connections enlisted with JTA, you must configure the
           <literal>hibernate.transaction.manager_lookup_class</literal>.</para>
         </section>
 
         <section xml:id="mapping-declaration-id-uuid">
           <title>UUID algorithm</title>
 
           <para>The UUID contains: IP address, startup time of the JVM that is
           accurate to a quarter second, system time and a counter value that
           is unique within the JVM. It is not possible to obtain a MAC address
           or memory address from Java code, so this is the best option without
           using JNI.</para>
         </section>
 
         <section xml:id="mapping-declaration-id-sequences">
           <title>Identity columns and sequences</title>
 
           <para>For databases that support identity columns (DB2, MySQL,
           Sybase, MS SQL), you can use <literal>identity</literal> key
           generation. For databases that support sequences (DB2, Oracle,
           PostgreSQL, Interbase, McKoi, SAP DB) you can use
           <literal>sequence</literal> style key generation. Both of these
           strategies require two SQL queries to insert a new object. For
           example:</para>
 
           <programlisting role="XML">&lt;id name="id" type="long" column="person_id"&gt;
         &lt;generator class="sequence"&gt;
                 &lt;param name="sequence"&gt;person_id_sequence&lt;/param&gt;
         &lt;/generator&gt;
 &lt;/id&gt;</programlisting>
 
           <programlisting role="XML">&lt;id name="id" type="long" column="person_id" unsaved-value="0"&gt;
         &lt;generator class="identity"/&gt;
 &lt;/id&gt;</programlisting>
 
           <para>For cross-platform development, the <literal>native</literal>
           strategy will, depending on the capabilities of the underlying
           database, choose from the <literal>identity</literal>,
           <literal>sequence</literal> and <literal>hilo</literal>
           strategies.</para>
         </section>
 
         <section xml:id="mapping-declaration-id-assigned">
           <title>Assigned identifiers</title>
 
           <para>If you want the application to assign identifiers, as opposed
           to having Hibernate generate them, you can use the
           <literal>assigned</literal> generator. This special generator uses
           the identifier value already assigned to the object's identifier
           property. The generator is used when the primary key is a natural
           key instead of a surrogate key. This is the default behavior if you
           do not specify <classname>@GeneratedValue</classname> nor
           <literal>&lt;generator&gt;</literal> elements.</para>
 
           <para>The <literal>assigned</literal> generator makes Hibernate use
           <literal>unsaved-value="undefined"</literal>. This forces Hibernate
           to go to the database to determine if an instance is transient or
           detached, unless there is a version or timestamp property, or you
           define <literal>Interceptor.isUnsaved()</literal>.</para>
         </section>
 
         <section xml:id="mapping-declaration-id-select">
           <title>Primary keys assigned by triggers</title>
 
           <para>Hibernate does not generate DDL with triggers. It is for
           legacy schemas only.</para>
 
           <programlisting role="XML">&lt;id name="id" type="long" column="person_id"&gt;
         &lt;generator class="select"&gt;
                 &lt;param name="key"&gt;socialSecurityNumber&lt;/param&gt;
         &lt;/generator&gt;
 &lt;/id&gt;</programlisting>
 
           <para>In the above example, there is a unique valued property named
           <literal>socialSecurityNumber</literal>. It is defined by the class,
           as a natural key and a surrogate key named
           <literal>person_id</literal>, whose value is generated by a
           trigger.</para>
         </section>
 
         <section>
           <title>Identity copy (foreign generator)</title>
 
           <para>Finally, you can ask Hibernate to copy the identifier from
           another associated entity. In the Hibernate jargon, it is known as a
           foreign generator but the JPA mapping reads better and is
           encouraged.</para>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 class MedicalHistory implements Serializable {
   @Id @OneToOne
   @JoinColumn(name = "person_id")
   Person patient;
 }
 
 @Entity
 public class Person implements Serializable {
   @Id @GeneratedValue Integer id;
 }</programlisting>
 
           <para>Or alternatively</para>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 class MedicalHistory implements Serializable {
   @Id Integer id;
 
   @MapsId @OneToOne
   @JoinColumn(name = "patient_id")
   Person patient;
 }
 
 @Entity
 class Person {
   @Id @GeneratedValue Integer id;
 }</programlisting>
 
           <para>In hbm.xml use the following approach:</para>
 
           <programlisting role="XML">&lt;class name="MedicalHistory"&gt;
    &lt;id name="id"&gt;
       &lt;generator class="foreign"&gt;
          &lt;param name="property"&gt;patient&lt;/param&gt;
       &lt;/generator&gt;
    &lt;/id&gt;
    &lt;one-to-one name="patient" class="Person" constrained="true"/&gt;
 &lt;/class&gt;</programlisting>
         </section>
       </section>
 
       <section xml:id="mapping-declaration-id-enhanced">
         <title>Enhanced identifier generators</title>
 
         <para>Starting with release 3.2.3, there are 2 new generators which
         represent a re-thinking of 2 different aspects of identifier
         generation. The first aspect is database portability; the second is
         optimization Optimization means that you do not have to query the
         database for every request for a new identifier value. These two new
         generators are intended to take the place of some of the named
         generators described above, starting in 3.3.x. However, they are
         included in the current releases and can be referenced by FQN.</para>
 
         <para>The first of these new generators is
         <literal>org.hibernate.id.enhanced.SequenceStyleGenerator</literal>
         which is intended, firstly, as a replacement for the
         <literal>sequence</literal> generator and, secondly, as a better
         portability generator than <literal>native</literal>. This is because
         <literal>native</literal> generally chooses between
         <literal>identity</literal> and <literal>sequence</literal> which have
         largely different semantics that can cause subtle issues in
         applications eyeing portability.
         <literal>org.hibernate.id.enhanced.SequenceStyleGenerator</literal>,
         however, achieves portability in a different manner. It chooses
         between a table or a sequence in the database to store its
         incrementing values, depending on the capabilities of the dialect
         being used. The difference between this and <literal>native</literal>
         is that table-based and sequence-based storage have the same exact
         semantic. In fact, sequences are exactly what Hibernate tries to
         emulate with its table-based generators. This generator has a number
         of configuration parameters: <itemizedlist spacing="compact">
             <listitem>
               <para><literal>sequence_name</literal> (optional, defaults to
               <literal>hibernate_sequence</literal>): the name of the sequence
               or table to be used.</para>
             </listitem>
 
             <listitem>
               <para><literal>initial_value</literal> (optional, defaults to
               <literal>1</literal>): the initial value to be retrieved from
               the sequence/table. In sequence creation terms, this is
               analogous to the clause typically named "STARTS WITH".</para>
             </listitem>
 
             <listitem>
               <para><literal>increment_size</literal> (optional - defaults to
               <literal>1</literal>): the value by which subsequent calls to
               the sequence/table should differ. In sequence creation terms,
               this is analogous to the clause typically named "INCREMENT
               BY".</para>
             </listitem>
 
             <listitem>
               <para><literal>force_table_use</literal> (optional - defaults to
               <literal>false</literal>): should we force the use of a table as
               the backing structure even though the dialect might support
               sequence?</para>
             </listitem>
 
             <listitem>
               <para><literal>value_column</literal> (optional - defaults to
               <literal>next_val</literal>): only relevant for table
               structures, it is the name of the column on the table which is
               used to hold the value.</para>
             </listitem>
 
             <listitem>
+              <para><literal>prefer_sequence_per_entity</literal> (optional -
+              defaults to <literal>false</literal>): should we create
+              separate sequence for each entity that share current generator
+              based on its name?</para>
+            </listitem>
+
+            <listitem>
+              <para><literal>sequence_per_entity_suffix</literal> (optional -
+              defaults to <literal>_SEQ</literal>): suffix added to the name
+              of a dedicated sequence.</para>
+            </listitem>
+
+            <listitem>
               <para><literal>optimizer</literal> (optional - defaults to
               <literal>none</literal>): See <xref
               linkend="mapping-declaration-id-enhanced-optimizers" /></para>
             </listitem>
           </itemizedlist></para>
 
         <para>The second of these new generators is
         <literal>org.hibernate.id.enhanced.TableGenerator</literal>, which is
         intended, firstly, as a replacement for the <literal>table</literal>
         generator, even though it actually functions much more like
         <literal>org.hibernate.id.MultipleHiLoPerTableGenerator</literal>, and
         secondly, as a re-implementation of
         <literal>org.hibernate.id.MultipleHiLoPerTableGenerator</literal> that
         utilizes the notion of pluggable optimizers. Essentially this
         generator defines a table capable of holding a number of different
         increment values simultaneously by using multiple distinctly keyed
         rows. This generator has a number of configuration parameters:
         <itemizedlist spacing="compact">
             <listitem>
               <para><literal>table_name</literal> (optional - defaults to
               <literal>hibernate_sequences</literal>): the name of the table
               to be used.</para>
             </listitem>
 
             <listitem>
               <para><literal>value_column_name</literal> (optional - defaults
               to <literal>next_val</literal>): the name of the column on the
               table that is used to hold the value.</para>
             </listitem>
 
             <listitem>
               <para><literal>segment_column_name</literal> (optional -
               defaults to <literal>sequence_name</literal>): the name of the
               column on the table that is used to hold the "segment key". This
               is the value which identifies which increment value to
               use.</para>
             </listitem>
 
             <listitem>
               <para><literal>segment_value</literal> (optional - defaults to
               <literal>default</literal>): The "segment key" value for the
               segment from which we want to pull increment values for this
               generator.</para>
             </listitem>
 
             <listitem>
               <para><literal>segment_value_length</literal> (optional -
               defaults to <literal>255</literal>): Used for schema generation;
               the column size to create this segment key column.</para>
             </listitem>
 
             <listitem>
               <para><literal>initial_value</literal> (optional - defaults to
               <literal>1</literal>): The initial value to be retrieved from
               the table.</para>
             </listitem>
 
             <listitem>
               <para><literal>increment_size</literal> (optional - defaults to
               <literal>1</literal>): The value by which subsequent calls to
               the table should differ.</para>
             </listitem>
 
             <listitem>
               <para><literal>optimizer</literal> (optional - defaults to
               <literal>??</literal>): See <xref
               linkend="mapping-declaration-id-enhanced-optimizers" />.</para>
             </listitem>
           </itemizedlist></para>
 
         <section xml:id="mapping-declaration-id-enhanced-optimizers">
           <title>Identifier generator optimization</title>
 
           <para>For identifier generators that store values in the database,
           it is inefficient for them to hit the database on each and every
           call to generate a new identifier value. Instead, you can group a
           bunch of them in memory and only hit the database when you have
           exhausted your in-memory value group. This is the role of the
           pluggable optimizers. Currently only the two enhanced generators
           (<xref linkend="mapping-declaration-id-enhanced" /> support this
           operation.</para>
 
           <itemizedlist spacing="compact">
             <listitem>
               <para><literal>none</literal> (generally this is the default if
               no optimizer was specified): this will not perform any
               optimizations and hit the database for each and every
               request.</para>
             </listitem>
 
             <listitem>
               <para><literal>hilo</literal>: applies a hi/lo algorithm around
               the database retrieved values. The values from the database for
               this optimizer are expected to be sequential. The values
               retrieved from the database structure for this optimizer
               indicates the "group number". The
               <literal>increment_size</literal> is multiplied by that value in
               memory to define a group "hi value".</para>
             </listitem>
 
             <listitem>
               <para><literal>pooled</literal>: as with the case of
               <literal>hilo</literal>, this optimizer attempts to minimize the
               number of hits to the database. Here, however, we simply store
               the starting value for the "next group" into the database
               structure rather than a sequential value in combination with an
               in-memory grouping algorithm. Here,
               <literal>increment_size</literal> refers to the values coming
               from the database.</para>
             </listitem>
           </itemizedlist>
         </section>
       </section>
 
       <section>
         <title>Partial identifier generation</title>
 
         <para>Hibernate supports the automatic generation of some of the
         identifier properties. Simply use the
         <classname>@GeneratedValue</classname> annotation on one or several id
         properties.</para>
 
         <warning>
           <para>The Hibernate team has always felt such a construct as
           fundamentally wrong. Try hard to fix your data model before using
           this feature.</para>
         </warning>
 
         <programlisting language="JAVA" role="JAVA">@Entity
 public class CustomerInventory implements Serializable {
   @Id
   @TableGenerator(name = "inventory",
     table = "U_SEQUENCES",
     pkColumnName = "S_ID",
     valueColumnName = "S_NEXTNUM",
     pkColumnValue = "inventory",
     allocationSize = 1000)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "inventory")
   Integer id;
 
 
   @Id @ManyToOne(cascade = CascadeType.MERGE)
   Customer customer;
 }
 
 @Entity
 public class Customer implements Serializable {
    @Id
    private int id;
 }</programlisting>
 
         <para>You can also generate properties inside an
         <classname>@EmbeddedId</classname> class.</para>
       </section>
     </section>
 
     <section>
       <title>Optimistic locking properties (optional)</title>
 
       <para>When using long transactions or conversations that span several
       database transactions, it is useful to store versioning data to ensure
       that if the same entity is updated by two conversations, the last to
       commit changes will be informed and not override the other
       conversation's work. It guarantees some isolation while still allowing
       for good scalability and works particularly well in read-often
       write-sometimes situations.</para>
 
       <para>You can use two approaches: a dedicated version number or a
       timestamp.</para>
 
       <para>A version or timestamp property should never be null for a
       detached instance. Hibernate will detect any instance with a null
       version or timestamp as transient, irrespective of what other
       <literal>unsaved-value</literal> strategies are specified.
       <emphasis>Declaring a nullable version or timestamp property is an easy
       way to avoid problems with transitive reattachment in Hibernate. It is
       especially useful for people using assigned identifiers or composite
       keys</emphasis>.</para>
 
       <section xml:id="entity-mapping-entity-version">
         <title>Version number</title>
 
         <para>You can add optimistic locking capability to an entity using the
         <literal>@Version</literal> annotation:</para>
 
         <programlisting language="JAVA" role="JAVA">@Entity
 public class Flight implements Serializable {
 ...
     @Version
     @Column(name="OPTLOCK")
     public Integer getVersion() { ... }
 }           </programlisting>
 
         <para>The version property will be mapped to the
         <literal>OPTLOCK</literal> column, and the entity manager will use it
         to detect conflicting updates (preventing lost updates you might
         otherwise see with the last-commit-wins strategy).</para>
 
         <para>The version column may be a numeric. Hibernate supports any kind
         of type provided that you define and implement the appropriate
         <classname>UserVersionType</classname>.</para>
 
         <para>The application must not alter the version number set up by
         Hibernate in any way. To artificially increase the version number,
         check in Hibernate Entity Manager's reference documentation
         <literal>LockModeType.OPTIMISTIC_FORCE_INCREMENT</literal> or
         <literal>LockModeType.PESSIMISTIC_FORCE_INCREMENT</literal>.</para>
 
         <para>If the version number is generated by the database (via a
         trigger for example), make sure to use
         <code>@org.hibernate.annotations.Generated(GenerationTime.ALWAYS).</code></para>
 
         <para>To declare a version property in hbm.xml, use:</para>
 
         <programlistingco role="XML">
           <areaspec>
             <area coords="2" xml:id="version1" />
 
             <area coords="3" xml:id="version2" />
 
             <area coords="4" xml:id="version3" />
 
             <area coords="5" xml:id="version4" />
 
             <area coords="6" xml:id="version5" />
 
             <area coords="7" xml:id="version6" />
 
             <area coords="8" xml:id="version7" />
           </areaspec>
 
           <programlisting>&lt;version
         column="version_column"
         name="propertyName"
         type="typename"
         access="field|property|ClassName"
         unsaved-value="null|negative|undefined"
         generated="never|always"
         insert="true|false"
         node="element-name|@attribute-name|element/@attribute|."
 /&gt;</programlisting>
 
           <calloutlist>
             <callout arearefs="version1">
               <para><literal>column</literal> (optional - defaults to the
               property name): the name of the column holding the version
               number.</para>
             </callout>
 
             <callout arearefs="version2">
               <para><literal>name</literal>: the name of a property of the
               persistent class.</para>
             </callout>
 
             <callout arearefs="version3">
               <para><literal>type</literal> (optional - defaults to
               <literal>integer</literal>): the type of the version
               number.</para>
             </callout>
 
             <callout arearefs="version4">
               <para><literal>access</literal> (optional - defaults to
               <literal>property</literal>): the strategy Hibernate uses to
               access the property value.</para>
             </callout>
 
             <callout arearefs="version5">
               <para><literal>unsaved-value</literal> (optional - defaults to
               <literal>undefined</literal>): a version property value that
               indicates that an instance is newly instantiated (unsaved),
               distinguishing it from detached instances that were saved or
               loaded in a previous session. <literal>Undefined</literal>
               specifies that the identifier property value should be
               used.</para>
             </callout>
 
             <callout arearefs="version6">
               <para><literal>generated</literal> (optional - defaults to
               <literal>never</literal>): specifies that this version property
               value is generated by the database. See the discussion of <link
               linkend="mapping-generated">generated properties</link> for more
               information.</para>
             </callout>
 
             <callout arearefs="version7">
               <para><literal>insert</literal> (optional - defaults to
               <literal>true</literal>): specifies whether the version column
               should be included in SQL insert statements. It can be set to
               <literal>false</literal> if the database column is defined with
               a default value of <literal>0</literal>.</para>
             </callout>
           </calloutlist>
         </programlistingco>
       </section>
 
       <section xml:id="mapping-declaration-timestamp" revision="4">
         <title>Timestamp</title>
 
         <para>Alternatively, you can use a timestamp. Timestamps are a less
         safe implementation of optimistic locking. However, sometimes an
         application might use the timestamps in other ways as well.</para>
 
         <para>Simply mark a property of type <classname>Date</classname> or
         <classname>Calendar</classname> as
         <classname>@Version</classname>.</para>
 
         <programlisting language="JAVA" role="JAVA">@Entity
 public class Flight implements Serializable {
 ...
     @Version
     public Date getLastUpdate() { ... }
 }           </programlisting>
 
         <para>When using timestamp versioning you can tell Hibernate where to
         retrieve the timestamp value from - database or JVM - by optionally
         adding the <classname>@org.hibernate.annotations.Source</classname>
         annotation to the property. Possible values for the value attribute of
         the annotation are
         <classname>org.hibernate.annotations.SourceType.VM</classname> and
         <classname>org.hibernate.annotations.SourceType.DB</classname>. The
         default is <classname>SourceType.DB</classname> which is also used in
         case there is no <classname>@Source</classname> annotation at
         all.</para>
 
         <para>Like in the case of version numbers, the timestamp can also be
         generated by the database instead of Hibernate. To do that, use
         <code>@org.hibernate.annotations.Generated(GenerationTime.ALWAYS).</code></para>
 
         <para>In hbm.xml, use the <literal>&lt;timestamp&gt;</literal>
         element:</para>
 
         <programlistingco role="XML">
           <areaspec>
             <area coords="2" xml:id="timestamp1" />
 
             <area coords="3" xml:id="timestamp2" />
 
             <area coords="4" xml:id="timestamp3" />
 
             <area coords="5" xml:id="timestamp4" />
 
             <area coords="6" xml:id="timestamp5" />
 
             <area coords="7" xml:id="timestamp6" />
           </areaspec>
 
           <programlisting>&lt;timestamp
         column="timestamp_column"
         name="propertyName"
         access="field|property|ClassName"
         unsaved-value="null|undefined"
         source="vm|db"
         generated="never|always"
         node="element-name|@attribute-name|element/@attribute|."
 /&gt;</programlisting>
 
           <calloutlist>
             <callout arearefs="timestamp1">
               <para><literal>column</literal> (optional - defaults to the
               property name): the name of a column holding the
               timestamp.</para>
             </callout>
 
             <callout arearefs="timestamp2">
               <para><literal>name</literal>: the name of a JavaBeans style
               property of Java type <literal>Date</literal> or
               <literal>Timestamp</literal> of the persistent class.</para>
             </callout>
 
             <callout arearefs="timestamp3">
               <para><literal>access</literal> (optional - defaults to
               <literal>property</literal>): the strategy Hibernate uses for
               accessing the property value.</para>
             </callout>
 
             <callout arearefs="timestamp4">
               <para><literal>unsaved-value</literal> (optional - defaults to
               <literal>null</literal>): a version property value that
               indicates that an instance is newly instantiated (unsaved),
               distinguishing it from detached instances that were saved or
               loaded in a previous session. <literal>Undefined</literal>
               specifies that the identifier property value should be
               used.</para>
             </callout>
 
             <callout arearefs="timestamp5">
               <para><literal>source</literal> (optional - defaults to
               <literal>vm</literal>): Where should Hibernate retrieve the
               timestamp value from? From the database, or from the current
               JVM? Database-based timestamps incur an overhead because
               Hibernate must hit the database in order to determine the "next
               value". It is safer to use in clustered environments. Not all
               <literal>Dialects</literal> are known to support the retrieval
               of the database's current timestamp. Others may also be unsafe
               for usage in locking due to lack of precision (Oracle 8, for
               example).</para>
             </callout>
 
             <callout arearefs="timestamp6">
               <para><literal>generated</literal> (optional - defaults to
               <literal>never</literal>): specifies that this timestamp
               property value is actually generated by the database. See the
               discussion of <link linkend="mapping-generated">generated
               properties</link> for more information.</para>
             </callout>
           </calloutlist>
         </programlistingco>
 
         <note>
           <title>Note</title>
 
           <para><literal>&lt;Timestamp&gt;</literal> is equivalent to
           <literal>&lt;version type="timestamp"&gt;</literal>. And
           <literal>&lt;timestamp source="db"&gt;</literal> is equivalent to
           <literal>&lt;version type="dbtimestamp"&gt;</literal></para>
         </note>
       </section>
     </section>
 
     <section xml:id="mapping-declaration-property" revision="4">
       <title>Property</title>
 
       <para>You need to decide which property needs to be made persistent in a
       given entity. This differs slightly between the annotation driven
       metadata and the hbm.xml files.</para>
 
       <section>
         <title>Property mapping with annotations</title>
 
         <para>In the annotations world, every non static non transient
         property (field or method depending on the access type) of an entity
         is considered persistent, unless you annotate it as
         <literal>@Transient</literal>. Not having an annotation for your
         property is equivalent to the appropriate <literal>@Basic</literal>
         annotation.</para>
 
         <para>The <literal>@Basic</literal> annotation allows you to declare
         the fetching strategy for a property. If set to
         <literal>LAZY</literal>, specifies that this property should be
         fetched lazily when the instance variable is first accessed. It
         requires build-time bytecode instrumentation, if your classes are not
         instrumented, property level lazy loading is silently ignored. The
         default is <literal>EAGER</literal>. You can also mark a property as
         not optional thanks to the <classname>@Basic.optional</classname>
         attribute. This will ensure that the underlying column are not
         nullable (if possible). Note that a better approach is to use the
         <classname>@NotNull</classname> annotation of the Bean Validation
         specification.</para>
 
         <para>Let's look at a few examples:</para>
 
         <programlisting language="JAVA" role="JAVA">public transient int counter; //transient property
 
 private String firstname; //persistent property
 
 @Transient
 String getLengthInMeter() { ... } //transient property
 
 String getName() {... } // persistent property
 
 @Basic
 int getLength() { ... } // persistent property
 
 @Basic(fetch = FetchType.LAZY)
 String getDetailedComment() { ... } // persistent property
 
 @Temporal(TemporalType.TIME)
 java.util.Date getDepartureTime() { ... } // persistent property           
 
 @Enumerated(EnumType.STRING)
 Starred getNote() { ... } //enum persisted as String in database</programlisting>
 
         <para><literal>counter</literal>, a transient field, and
         <literal>lengthInMeter</literal>, a method annotated as
         <literal>@Transient</literal>, and will be ignored by the Hibernate.
         <literal>name</literal>, <literal>length</literal>, and
         <literal>firstname</literal> properties are mapped persistent and
         eagerly fetched (the default for simple properties). The
         <literal>detailedComment</literal> property value will be lazily
         fetched from the database once a lazy property of the entity is
         accessed for the first time. Usually you don't need to lazy simple
         properties (not to be confused with lazy association fetching). The
         recommended alternative is to use the projection capability of JP-QL
         (Java Persistence Query Language) or Criteria queries.</para>
 
         <para>JPA support property mapping of all basic types supported by
         Hibernate (all basic Java types , their respective wrappers and
         serializable classes). Hibernate Annotations supports out of the box
         enum type mapping either into a ordinal column (saving the enum
         ordinal) or a string based column (saving the enum string
         representation): the persistence representation, defaulted to ordinal,
         can be overridden through the <literal>@Enumerated</literal>
         annotation as shown in the <literal>note</literal> property
         example.</para>
 
         <para>In plain Java APIs, the temporal precision of time is not
         defined. When dealing with temporal data you might want to describe
         the expected precision in database. Temporal data can have
         <literal>DATE</literal>, <literal>TIME</literal>, or
         <literal>TIMESTAMP</literal> precision (ie the actual date, only the
         time, or both). Use the <literal>@Temporal</literal> annotation to
         fine tune that.</para>
 
         <para><literal>@Lob</literal> indicates that the property should be
         persisted in a Blob or a Clob depending on the property type:
         <classname>java.sql.Clob</classname>,
         <classname>Character[]</classname>, <classname>char[]</classname> and
         java.lang.<classname>String</classname> will be persisted in a Clob.
         <classname>java.sql.Blob</classname>, <classname>Byte[]</classname>,
         <classname>byte[] </classname>and <classname>Serializable</classname>
         type will be persisted in a Blob.</para>
 
         <programlisting language="JAVA" role="JAVA">@Lob
 public String getFullText() {
     return fullText;
 }
 
 @Lob
 public byte[] getFullCode() {
     return fullCode;
 }</programlisting>
 
         <para>If the property type implements
         <classname>java.io.Serializable</classname> and is not a basic type,
         and if the property is not annotated with <literal>@Lob</literal>,
         then the Hibernate <literal>serializable</literal> type is
         used.</para>
 
         <section>
           <title>Type</title>
 
           <para>You can also manually specify a type using the
           <literal>@org.hibernate.annotations.Type</literal> and some
           parameters if needed. <classname>@Type.type</classname> could
           be:</para>
 
           <orderedlist spacing="compact">
             <listitem>
               <para>The name of a Hibernate basic type: <literal>integer,
               string, character, date, timestamp, float, binary, serializable,
               object, blob</literal> etc.</para>
             </listitem>
 
             <listitem>
               <para>The name of a Java class with a default basic type:
               <literal>int, float, char, java.lang.String, java.util.Date,
               java.lang.Integer, java.sql.Clob</literal> etc.</para>
             </listitem>
 
             <listitem>
               <para>The name of a serializable Java class.</para>
             </listitem>
 
             <listitem>
               <para>The class name of a custom type:
               <literal>com.illflow.type.MyCustomType</literal> etc.</para>
             </listitem>
           </orderedlist>
 
           <para>If you do not specify a type, Hibernate will use reflection
           upon the named property and guess the correct Hibernate type.
           Hibernate will attempt to interpret the name of the return class of
           the property getter using, in order, rules 2, 3, and 4.</para>
 
           <para><literal>@org.hibernate.annotations.TypeDef</literal> and
           <literal>@org.hibernate.annotations.TypeDefs</literal> allows you to
           declare type definitions. These annotations can be placed at the
           class or package level. Note that these definitions are global for
           the session factory (even when defined at the class level). If the
           type is used on a single entity, you can place the definition on the
           entity itself. Otherwise, it is recommended to place the definition
           at the package level. In the example below, when Hibernate
           encounters a property of class <literal>PhoneNumer</literal>, it
           delegates the persistence strategy to the custom mapping type
           <literal>PhoneNumberType</literal>. However, properties belonging to
           other classes, too, can delegate their persistence strategy to
           <literal>PhoneNumberType</literal>, by explicitly using the
           <literal>@Type</literal> annotation.</para>
 
           <note>
             <para>Package level annotations are placed in a file named
             <filename>package-info.java</filename> in the appropriate package.
             Place your annotations before the package declaration.</para>
           </note>
 
           <programlisting language="JAVA" role="JAVA">@TypeDef(
    name = "phoneNumber",
    defaultForType = PhoneNumber.class,
    typeClass = PhoneNumberType.class
 )
 
 @Entity
 public class ContactDetails {
    [...]
    private PhoneNumber localPhoneNumber;
    @Type(type="phoneNumber")
    private OverseasPhoneNumber overseasPhoneNumber;
    [...]
 }</programlisting>
 
           <para>The following example shows the usage of the
           <literal>parameters</literal> attribute to customize the
           TypeDef.</para>
 
           <programlisting language="JAVA" role="JAVA">//in org/hibernate/test/annotations/entity/package-info.java
 @TypeDefs(
     {
     @TypeDef(
         name="caster",
         typeClass = CasterStringType.class,
         parameters = {
             @Parameter(name="cast", value="lower")
         }
     )
     }
 )
 package org.hibernate.test.annotations.entity;
 
 //in org/hibernate/test/annotations/entity/Forest.java
 public class Forest {
     @Type(type="caster")
     public String getSmallText() {
     ...
 }      </programlisting>
 
           <para>When using composite user type, you will have to express
           column definitions. The <literal>@Columns</literal> has been
           introduced for that purpose.</para>
 
           <programlisting language="JAVA" role="JAVA">@Type(type="org.hibernate.test.annotations.entity.MonetaryAmountUserType")
 @Columns(columns = {
     @Column(name="r_amount"),
     @Column(name="r_currency")
 })
 public MonetaryAmount getAmount() {
     return amount;
 }
 
 
 public class MonetaryAmount implements Serializable {
     private BigDecimal amount;
     private Currency currency;
     ...
 }</programlisting>
         </section>
 
         <section>
           <title>Access type</title>
 
           <para>By default the access type of a class hierarchy is defined by
           the position of the <classname>@Id</classname> or
           <classname>@EmbeddedId</classname> annotations. If these annotations
           are on a field, then only fields are considered for persistence and
           the state is accessed via the field. If there annotations are on a
           getter, then only the getters are considered for persistence and the
           state is accessed via the getter/setter. That works well in practice
           and is the recommended approach.<note>
               <para>The placement of annotations within a class hierarchy has
               to be consistent (either field or on property) to be able to
               determine the default access type. It is recommended to stick to
               one single annotation placement strategy throughout your whole
               application.</para>
             </note></para>
 
           <para>However in some situations, you need to:</para>
 
           <itemizedlist>
             <listitem>
               <para>force the access type of the entity hierarchy</para>
             </listitem>
 
             <listitem>
               <para>override the access type of a specific entity in the class
               hierarchy</para>
             </listitem>
 
             <listitem>
               <para>override the access type of an embeddable type</para>
             </listitem>
           </itemizedlist>
 
           <para>The best use case is an embeddable class used by several
           entities that might not use the same access type. In this case it is
           better to force the access type at the embeddable class
           level.</para>
 
           <para>To force the access type on a given class, use the
           <classname>@Access</classname> annotation as showed below:</para>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 public class Order {
    @Id private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    @Embedded private Address address;
    public Address getAddress() { return address; }
    public void setAddress() { this.address = address; }
 }
 
 @Entity
 public class User {
    private Long id;
    @Id public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    private Address address;
    @Embedded public Address getAddress() { return address; }
    public void setAddress() { this.address = address; }
 }
 
 @Embeddable
 @Access(AcessType.PROPERTY)
 public class Address {
    private String street1;
    public String getStreet1() { return street1; }
    public void setStreet1() { this.street1 = street1; }
 
    private hashCode; //not persistent
 }</programlisting>
 
           <para>You can also override the access type of a single property
           while keeping the other properties standard.</para>
 
           <programlisting language="JAVA" role="JAVA">@Entity
 public class Order {
    @Id private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Transient private String userId;
    @Transient private String orderId;
 
    @Access(AccessType.PROPERTY)
    public String getOrderNumber() { return userId + ":" + orderId; }
    public void setOrderNumber() { this.userId = ...; this.orderId = ...; }
 }</programlisting>
 
           <para>In this example, the default access type is
           <classname>FIELD</classname> except for the
           <literal>orderNumber</literal> property. Note that the corresponding
           field, if any must be marked as <classname>@Transient</classname> or
           <code>transient</code>.</para>
 
           <note>
             <title>@org.hibernate.annotations.AccessType</title>
 
             <para>The annotation
             <classname>@org.hibernate.annotations.AccessType</classname>
             should be considered deprecated for FIELD and PROPERTY access. It
             is still useful however if you need to use a custom access
             type.</para>
           </note>
         </section>
 
         <section>
           <title>Optimistic lock</title>
 
           <para>It is sometimes useful to avoid increasing the version number
           even if a given property is dirty (particularly collections). You
           can do that by annotating the property (or collection) with
           <literal>@OptimisticLock(excluded=true)</literal>.</para>
 
           <para>More formally, specifies that updates to this property do not
           require acquisition of the optimistic lock.</para>
         </section>
 
         <section xml:id="entity-mapping-property-column">
           <title>Declaring column attributes</title>
 
           <para>The column(s) used for a property mapping can be defined using
           the <literal>@Column</literal> annotation. Use it to override
           default values (see the JPA specification for more information on
           the defaults). You can use this annotation at the property level for
           properties that are:</para>
 
           <itemizedlist>
             <listitem>
               <para>not annotated at all</para>
             </listitem>
 
             <listitem>
               <para>annotated with <literal>@Basic</literal></para>
             </listitem>
 
             <listitem>
               <para>annotated with <literal>@Version</literal></para>
             </listitem>
 
             <listitem>
               <para>annotated with <literal>@Lob</literal></para>
             </listitem>
 
             <listitem>
               <para>annotated with <literal>@Temporal</literal></para>
             </listitem>
           </itemizedlist>
 
           <programlisting language="JAVA" role="JAVA">
 @Entity
 public class Flight implements Serializable {
 ...
 @Column(updatable = false, name = "flight_name", nullable = false, length=50)
 public String getName() { ... }
             </programlisting>
 
           <para>The <literal>name</literal> property is mapped to the
           <literal>flight_name</literal> column, which is not nullable, has a
           length of 50 and is not updatable (making the property
           immutable).</para>
 
           <para>This annotation can be applied to regular properties as well
           as <literal>@Id</literal> or <literal>@Version</literal>
           properties.</para>
 
           <programlistingco>
             <areaspec>
               <area coords="2" xml:id="hm1" />
 
               <area coords="3" xml:id="hm2" />
 
               <area coords="4" xml:id="hm3" />
 
               <area coords="5" xml:id="hm4" />
 
               <area coords="6" xml:id="hm5" />
 
               <area coords="7" xml:id="hm6" />
 
               <area coords="8" xml:id="hm7" />
 
               <area coords="9" xml:id="hm8" />
 
               <area coords="10" xml:id="hm9" />
 
               <area coords="11" xml:id="hm10" />
             </areaspec>
 
             <programlisting>@Column(
     name="columnName";
     boolean unique() default false;
     boolean nullable() default true;
     boolean insertable() default true;
     boolean updatable() default true;
     String columnDefinition() default "";
     String table() default "";
     int length() default 255;
     int precision() default 0; // decimal precision
     int scale() default 0; // decimal scale</programlisting>
 
             <calloutlist>
               <callout arearefs="hm1">
                 <para><literal>name</literal> (optional): the column name
                 (default to the property name)</para>
               </callout>
 
               <callout arearefs="hm2">
                 <para><literal>unique</literal> (optional): set a unique
                 constraint on this column or not (default false)</para>
               </callout>
 
               <callout arearefs="hm3">
                 <para><literal>nullable</literal> (optional): set the column
                 as nullable (default true).</para>
               </callout>
 
               <callout arearefs="hm4">
                 <para><literal>insertable</literal> (optional): whether or not
                 the column will be part of the insert statement (default
                 true)</para>
               </callout>
 
               <callout arearefs="hm5">
                 <para><literal>updatable</literal> (optional): whether or not
                 the column will be part of the update statement (default
                 true)</para>
               </callout>
 
               <callout arearefs="hm6">
                 <para><literal>columnDefinition</literal> (optional): override
                 the sql DDL fragment for this particular column (non
                 portable)</para>
               </callout>
 
               <callout arearefs="hm7">
                 <para><literal>table</literal> (optional): define the targeted
                 table (default primary table)</para>
               </callout>
 
               <callout arearefs="hm8">
                 <para><literal>length</literal> (optional):
                 column length (default 255)</para>
               </callout>
 
               <callout arearefs="hm8">
                 <para><literal>precision</literal>
                 (optional): column decimal precision (default 0)</para>
               </callout>
 
               <callout arearefs="hm10">
                 <para><literal>scale</literal> (optional):
                 column decimal scale if useful (default 0)</para>
               </callout>
             </calloutlist>
           </programlistingco>
         </section>
 
         <section>
           <title>Formula</title>
 
           <para>Sometimes, you want the Database to do some computation for
           you rather than in the JVM, you might also create some kind of
           virtual column. You can use a SQL fragment (aka formula) instead of
           mapping a property into a column. This kind of property is read only
           (its value is calculated by your formula fragment).</para>
 
           <programlisting language="JAVA" role="JAVA">@Formula("obj_length * obj_height * obj_width")
 public long getObjectVolume()</programlisting>
 
           <para>The SQL fragment can be as complex as you want and even
           include subselects.</para>
         </section>
 
         <section>
           <title>Non-annotated property defaults</title>
 
           <para>If a property is not annotated, the following rules
           apply:<itemizedlist>
               <listitem>
                 <para>If the property is of a single type, it is mapped as
                 @Basic</para>
               </listitem>
 
               <listitem>
                 <para>Otherwise, if the type of the property is annotated as
                 @Embeddable, it is mapped as @Embedded</para>
               </listitem>
 
               <listitem>
                 <para>Otherwise, if the type of the property is
                 <classname>Serializable</classname>, it is mapped as
                 <classname>@Basic</classname> in a column holding the object
                 in its serialized version</para>
               </listitem>
 
               <listitem>
                 <para>Otherwise, if the type of the property is
                 <classname>java.sql.Clob</classname> or
                 <classname>java.sql.Blob</classname>, it is mapped as
                 <classname>@Lob</classname> with the appropriate
                 <classname>LobType</classname></para>
               </listitem>
             </itemizedlist></para>
         </section>
       </section>
 
       <section>
         <title>Property mapping with hbm.xml</title>
 
         <para>The <literal>&lt;property&gt;</literal> element declares a
         persistent JavaBean style property of the class.</para>
 
         <programlistingco role="XML">
           <areaspec>
             <area coords="2" xml:id="property1" />
 
             <area coords="3" xml:id="property2" />
 
             <area coords="4" xml:id="property3" />
 
             <areaset coords="" xml:id="property4-5">
               <area coords="5" xml:id="property4" />
 
               <area coords="6" xml:id="property5" />
             </areaset>
 
             <area coords="7" xml:id="property6" />
 
             <area coords="8" xml:id="property7" />
 
             <area coords="9" xml:id="property8" />
 
             <area coords="10" xml:id="property9" />
 
             <area coords="11" xml:id="property10" />
 
             <area coords="12" xml:id="property11" />
 
             <area coords="13" xml:id="property12" />
           </areaspec>
 
           <programlisting>&lt;property
         name="propertyName"
         column="column_name"
         type="typename"
         update="true|false"
         insert="true|false"
         formula="arbitrary SQL expression"
         access="field|property|ClassName"
         lazy="true|false"
         unique="true|false"
diff --git a/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
index 96ba9a8241..32a14eddea 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGenerator.java
@@ -1,67 +1,72 @@
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
 package org.hibernate.id;
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * The general contract between a class that generates unique
  * identifiers and the <tt>Session</tt>. It is not intended that
  * this interface ever be exposed to the application. It <b>is</b>
  * intended that users implement this interface to provide
  * custom identifier generation strategies.<br>
  * <br>
  * Implementors should provide a public default constructor.<br>
  * <br>
  * Implementations that accept configuration parameters should
  * also implement <tt>Configurable</tt>.
  * <br>
  * Implementors <em>must</em> be threadsafe
  *
  * @author Gavin King
  * @see PersistentIdentifierGenerator
  * @see Configurable
  */
 public interface IdentifierGenerator {
 
     /**
      * The configuration parameter holding the entity name
      */
     public static final String ENTITY_NAME = "entity_name";
-    
+
+    /**
+     * The configuration parameter holding the JPA entity name
+     */
+    public static final String JPA_ENTITY_NAME = "jpa_entity_name";
+
 	/**
 	 * Generate a new identifier.
 	 * @param session
 	 * @param object the entity or toplevel collection for which the id is being generated
 	 *
 	 * @return a new identifier
 	 * @throws HibernateException
 	 */
 	public Serializable generate(SessionImplementor session, Object object) 
 	throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
index d8b88cafed..c577e1a923 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/SequenceStyleGenerator.java
@@ -1,390 +1,406 @@
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
 package org.hibernate.id.enhanced;
 
 import java.io.Serializable;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
 import org.hibernate.id.Configurable;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 
 /**
  * Generates identifier values based on an sequence-style database structure.
  * Variations range from actually using a sequence to using a table to mimic
  * a sequence.  These variations are encapsulated by the {@link DatabaseStructure}
  * interface internally.
  * <p/>
+ * <b>NOTE</b> that by default we utilize a single database sequence for all
+ * generators.  The configuration parameter {@link #CONFIG_PREFER_SEQUENCE_PER_ENTITY}
+ * can be used to create dedicated sequence for each entity based on its name.
+ * Sequence suffix can be controlled with {@link #CONFIG_SEQUENCE_PER_ENTITY_SUFFIX}
+ * option.
+ * <p/>
  * General configuration parameters:
  * <table>
  * 	 <tr>
  *     <td><b>NAME</b></td>
  *     <td><b>DEFAULT</b></td>
  *     <td><b>DESCRIPTION</b></td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEQUENCE_PARAM}</td>
  *     <td>{@link #DEF_SEQUENCE_NAME}</td>
  *     <td>The name of the sequence/table to use to store/retrieve values</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #INITIAL_PARAM}</td>
  *     <td>{@link #DEFAULT_INITIAL_VALUE}</td>
  *     <td>The initial value to be stored for the given segment; the effect in terms of storage varies based on {@link Optimizer} and {@link DatabaseStructure}</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #INCREMENT_PARAM}</td>
  *     <td>{@link #DEFAULT_INCREMENT_SIZE}</td>
  *     <td>The increment size for the underlying segment; the effect in terms of storage varies based on {@link Optimizer} and {@link DatabaseStructure}</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #OPT_PARAM}</td>
  *     <td><i>depends on defined increment size</i></td>
  *     <td>Allows explicit definition of which optimization strategy to use</td>
  *   </tr>
  *     <td>{@link #FORCE_TBL_PARAM}</td>
  *     <td><b><i>false<i/></b></td>
  *     <td>Allows explicit definition of which optimization strategy to use</td>
  *   </tr>
  * </table>
  * <p/>
  * Configuration parameters used specifically when the underlying structure is a table:
  * <table>
  * 	 <tr>
  *     <td><b>NAME</b></td>
  *     <td><b>DEFAULT</b></td>
  *     <td><b>DESCRIPTION</b></td>
  *   </tr>
  *   <tr>
  *     <td>{@link #VALUE_COLUMN_PARAM}</td>
  *     <td>{@link #DEF_VALUE_COLUMN}</td>
  *     <td>The name of column which holds the sequence value for the given segment</td>
  *   </tr>
  * </table>
  *
  * @author Steve Ebersole
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class SequenceStyleGenerator
 		implements PersistentIdentifierGenerator, BulkInsertionCapableIdentifierGenerator, Configurable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			SequenceStyleGenerator.class.getName()
 	);
 
 	// general purpose parameters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public static final String SEQUENCE_PARAM = "sequence_name";
 	public static final String DEF_SEQUENCE_NAME = "hibernate_sequence";
 
 	public static final String INITIAL_PARAM = "initial_value";
 	public static final int DEFAULT_INITIAL_VALUE = 1;
 
 	public static final String INCREMENT_PARAM = "increment_size";
 	public static final int DEFAULT_INCREMENT_SIZE = 1;
 
 	public static final String OPT_PARAM = "optimizer";
 
 	public static final String FORCE_TBL_PARAM = "force_table_use";
 
+	public static final String CONFIG_PREFER_SEQUENCE_PER_ENTITY = "prefer_sequence_per_entity";
+	public static final String CONFIG_SEQUENCE_PER_ENTITY_SUFFIX = "sequence_per_entity_suffix";
+	public static final String DEF_SEQUENCE_SUFFIX = "_SEQ";
+
 
 	// table-specific parameters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public static final String VALUE_COLUMN_PARAM = "value_column";
 	public static final String DEF_VALUE_COLUMN = "next_val";
 
 
 	// state ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private DatabaseStructure databaseStructure;
 	private Optimizer optimizer;
 	private Type identifierType;
 
 	/**
 	 * Getter for property 'databaseStructure'.
 	 *
 	 * @return Value for property 'databaseStructure'.
 	 */
 	public DatabaseStructure getDatabaseStructure() {
 		return databaseStructure;
 	}
 
 	/**
 	 * Getter for property 'optimizer'.
 	 *
 	 * @return Value for property 'optimizer'.
 	 */
 	public Optimizer getOptimizer() {
 		return optimizer;
 	}
 
 	/**
 	 * Getter for property 'identifierType'.
 	 *
 	 * @return Value for property 'identifierType'.
 	 */
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 
 	// Configurable implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		this.identifierType = type;
 		boolean forceTableUse = ConfigurationHelper.getBoolean( FORCE_TBL_PARAM, params, false );
 
 		final String sequenceName = determineSequenceName( params, dialect );
 
 		final int initialValue = determineInitialValue( params );
 		int incrementSize = determineIncrementSize( params );
 
 		final String optimizationStrategy = determineOptimizationStrategy( params, incrementSize );
 		incrementSize = determineAdjustedIncrementSize( optimizationStrategy, incrementSize );
 
 		if ( dialect.supportsSequences() && !forceTableUse ) {
 			if ( !dialect.supportsPooledSequences() && OptimizerFactory.isPooledOptimizer( optimizationStrategy ) ) {
 				forceTableUse = true;
                 LOG.forcingTableUse();
 			}
 		}
 
 		this.databaseStructure = buildDatabaseStructure(
 				type,
 				params,
 				dialect,
 				forceTableUse,
 				sequenceName,
 				initialValue,
 				incrementSize
 		);
 		this.optimizer = OptimizerFactory.buildOptimizer(
 				optimizationStrategy,
 				identifierType.getReturnedClass(),
 				incrementSize,
 				ConfigurationHelper.getInt( INITIAL_PARAM, params, -1 )
 		);
 		this.databaseStructure.prepare( optimizer );
 	}
 
 	/**
 	 * Determine the name of the sequence (or table if this resolves to a physical table)
 	 * to use.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The sequence name
 	 */
 	protected String determineSequenceName(Properties params, Dialect dialect) {
+		String sequencePerEntitySuffix = ConfigurationHelper.getString( CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, params, DEF_SEQUENCE_SUFFIX );
+		// JPA_ENTITY_NAME value honors <class ... entity-name="..."> (HBM) and @Entity#name (JPA) overrides.
+		String sequenceName = ConfigurationHelper.getBoolean( CONFIG_PREFER_SEQUENCE_PER_ENTITY, params, false )
+				? params.getProperty( JPA_ENTITY_NAME ) + sequencePerEntitySuffix
+				: DEF_SEQUENCE_NAME;
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
-		String sequenceName = ConfigurationHelper.getString( SEQUENCE_PARAM, params, DEF_SEQUENCE_NAME );
+		sequenceName = ConfigurationHelper.getString( SEQUENCE_PARAM, params, sequenceName );
 		if ( sequenceName.indexOf( '.' ) < 0 ) {
 			sequenceName = normalizer.normalizeIdentifierQuoting( sequenceName );
 			String schemaName = params.getProperty( SCHEMA );
 			String catalogName = params.getProperty( CATALOG );
 			sequenceName = Table.qualify(
 					dialect.quote( catalogName ),
 					dialect.quote( schemaName ),
 					dialect.quote( sequenceName )
 			);
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 		return sequenceName;
 	}
 
 	/**
 	 * Determine the name of the column used to store the generator value in
 	 * the db.
 	 * <p/>
 	 * Called during {@link #configure configuration} <b>when resolving to a
 	 * physical table</b>.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect.
 	 * @return The value column name
 	 */
 	protected String determineValueColumnName(Properties params, Dialect dialect) {
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 		String name = ConfigurationHelper.getString( VALUE_COLUMN_PARAM, params, DEF_VALUE_COLUMN );
 		return dialect.quote( normalizer.normalizeIdentifierQuoting( name ) );
 	}
 
 	/**
 	 * Determine the initial sequence value to use.  This value is used when
 	 * initializing the {@link #getDatabaseStructure() database structure}
 	 * (i.e. sequence/table).
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The initial value
 	 */
 	protected int determineInitialValue(Properties params) {
 		return ConfigurationHelper.getInt( INITIAL_PARAM, params, DEFAULT_INITIAL_VALUE );
 	}
 
 	/**
 	 * Determine the increment size to be applied.  The exact implications of
 	 * this value depends on the {@link #getOptimizer() optimizer} being used.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The increment size
 	 */
 	protected int determineIncrementSize(Properties params) {
 		return ConfigurationHelper.getInt( INCREMENT_PARAM, params, DEFAULT_INCREMENT_SIZE );
 	}
 
 	/**
 	 * Determine the optimizer to use.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param incrementSize The {@link #determineIncrementSize determined increment size}
 	 * @return The optimizer strategy (name)
 	 */
 	protected String determineOptimizationStrategy(Properties params, int incrementSize) {
 		// if the increment size is greater than one, we prefer pooled optimization; but we first
 		// need to see if the user prefers POOL or POOL_LO...
 		String defaultPooledOptimizerStrategy = ConfigurationHelper.getBoolean( Environment.PREFER_POOLED_VALUES_LO, params, false )
 				? OptimizerFactory.StandardOptimizerDescriptor.POOLED_LO.getExternalName()
 				: OptimizerFactory.StandardOptimizerDescriptor.POOLED.getExternalName();
 		String defaultOptimizerStrategy = incrementSize <= 1
 				? OptimizerFactory.StandardOptimizerDescriptor.NONE.getExternalName()
 				: defaultPooledOptimizerStrategy;
 		return ConfigurationHelper.getString( OPT_PARAM, params, defaultOptimizerStrategy );
 	}
 
 	/**
 	 * In certain cases we need to adjust the increment size based on the
 	 * selected optimizer.  This is the hook to achieve that.
 	 *
 	 * @param optimizationStrategy The optimizer strategy (name)
 	 * @param incrementSize The {@link #determineIncrementSize determined increment size}
 	 * @return The adjusted increment size.
 	 */
 	protected int determineAdjustedIncrementSize(String optimizationStrategy, int incrementSize) {
 		if ( incrementSize > 1
 				&& OptimizerFactory.StandardOptimizerDescriptor.NONE.getExternalName().equals( optimizationStrategy ) ) {
             LOG.honoringOptimizerSetting(
 					OptimizerFactory.StandardOptimizerDescriptor.NONE.getExternalName(),
 					INCREMENT_PARAM,
 					incrementSize
 			);
 			incrementSize = 1;
 		}
 		return incrementSize;
 	}
 
 	/**
 	 * Build the database structure.
 	 *
 	 * @param type The Hibernate type of the identifier property
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect being used.
 	 * @param forceTableUse Should a table be used even if the dialect supports sequences?
 	 * @param sequenceName The name to use for the sequence or table.
 	 * @param initialValue The initial value.
 	 * @param incrementSize the increment size to use (after any adjustments).
 	 *
 	 * @return An abstraction for the actual database structure in use (table vs. sequence).
 	 */
 	protected DatabaseStructure buildDatabaseStructure(
 			Type type,
 			Properties params,
 			Dialect dialect,
 			boolean forceTableUse,
 			String sequenceName,
 			int initialValue,
 			int incrementSize) {
 		boolean useSequence = dialect.supportsSequences() && !forceTableUse;
 		if ( useSequence ) {
 			return new SequenceStructure( dialect, sequenceName, initialValue, incrementSize, type.getReturnedClass() );
 		}
 		else {
 			String valueColumnName = determineValueColumnName( params, dialect );
 			return new TableStructure( dialect, sequenceName, valueColumnName, initialValue, incrementSize, type.getReturnedClass() );
 		}
 	}
 
 
 	// IdentifierGenerator implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
 		return optimizer.generate( databaseStructure.buildCallback( session ) );
 	}
 
 
 	// PersistentIdentifierGenerator implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Object generatorKey() {
 		return databaseStructure.getName();
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return databaseStructure.sqlCreateStrings( dialect );
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return databaseStructure.sqlDropStrings( dialect );
 	}
 
 
 	// BulkInsertionCapableIdentifierGenerator implementation ~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsBulkInsertionIdentifierGeneration() {
 		// it does, as long as
 		// 		1) there is no (non-noop) optimizer in use
 		//		2) the underlying structure is a sequence
 		return OptimizerFactory.NoopOptimizer.class.isInstance( getOptimizer() )
 				&& getDatabaseStructure().isPhysicalSequence();
 	}
 
 	@Override
 	public String determineBulkInsertionIdentifierGenerationSelectFragment(Dialect dialect) {
 		return dialect.getSelectSequenceNextValString( getDatabaseStructure().getName() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index cae52f0be5..37e9467e05 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,353 +1,354 @@
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
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.Type;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
 	private final List columns = new ArrayList();
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 	public Iterator getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
+			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) tables.append(", ");
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if (typeName==null) {
 			throw new MappingException("No type name");
 		}
 		Type result = mappings.getTypeResolver().heuristicType(typeName, typeParameters);
 		if (result==null) {
 			String msg = "Could not determine type for: " + typeName;
 			if(table != null){
 				msg += ", at table: " + table.getName();
 			}
 			if(columns!=null && columns.size()>0) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException(msg);
 		}
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		if (typeName==null) {
 			if (className==null) {
 				throw new MappingException("you must specify types for a dynamic entity: " + propertyName);
 			}
 			typeName = ReflectHelper.reflectedPropertyClass(className, propertyName).getName();
 		}
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/DedicatedSequenceEntity1.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/DedicatedSequenceEntity1.java
new file mode 100644
index 0000000000..92619084ef
--- /dev/null
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/DedicatedSequenceEntity1.java
@@ -0,0 +1,28 @@
+package org.hibernate.test.annotations.id.generationmappings;
+
+import java.io.Serializable;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@Entity
+@Table(name = "DEDICATED_SEQ_TBL1")
+public class DedicatedSequenceEntity1 implements Serializable {
+	public static final String SEQUENCE_SUFFIX = "_GEN";
+
+	private Long id;
+
+	@Id
+	@GeneratedValue(generator = "SequencePerEntityGenerator")
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+}
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/DedicatedSequenceEntity2.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/DedicatedSequenceEntity2.java
new file mode 100644
index 0000000000..698fc77325
--- /dev/null
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/DedicatedSequenceEntity2.java
@@ -0,0 +1,28 @@
+package org.hibernate.test.annotations.id.generationmappings;
+
+import java.io.Serializable;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+/**
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
+ */
+@Entity(name = DedicatedSequenceEntity2.ENTITY_NAME)
+@Table(name = "DEDICATED_SEQ_TBL2")
+public class DedicatedSequenceEntity2 implements Serializable {
+	public static final String ENTITY_NAME = "DEDICATED2";
+
+	private Long id;
+
+	@Id
+	@GeneratedValue(generator = "SequencePerEntityGenerator")
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+}
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/NewGeneratorMappingsTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/NewGeneratorMappingsTest.java
index a2a5cbea5b..10c4fb7848 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/NewGeneratorMappingsTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/NewGeneratorMappingsTest.java
@@ -1,122 +1,156 @@
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
 package org.hibernate.test.annotations.id.generationmappings;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.enhanced.OptimizerFactory;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.id.enhanced.TableGenerator;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test mapping the {@link javax.persistence.GenerationType GenerationTypes} to the corresponding
  * hibernate generators using the new scheme
  *
  * @author Steve Ebersole
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class NewGeneratorMappingsTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] {
 				MinimalSequenceEntity.class,
 				CompleteSequenceEntity.class,
 				AutoEntity.class,
-				MinimalTableEntity.class
+				MinimalTableEntity.class,
+				DedicatedSequenceEntity1.class,
+				DedicatedSequenceEntity2.class
 		};
 	}
 
 	@Override
+	protected String[] getAnnotatedPackages() {
+		return new String[] { this.getClass().getPackage().getName() };
+	}
+
+	@Override
 	protected void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "" );
 	}
 
 	@Override
 	protected boolean createSchema() {
 		return false;
 	}
 
 	@Test
 	public void testMinimalSequenceEntity() {
 		final EntityPersister persister = sessionFactory().getEntityPersister( MinimalSequenceEntity.class.getName() );
 		IdentifierGenerator generator = persister.getIdentifierGenerator();
 		assertTrue( SequenceStyleGenerator.class.isInstance( generator ) );
 		SequenceStyleGenerator seqGenerator = (SequenceStyleGenerator) generator;
 		assertEquals( MinimalSequenceEntity.SEQ_NAME, seqGenerator.getDatabaseStructure().getName() );
 		// 1 is the annotation default
 		assertEquals( 1, seqGenerator.getDatabaseStructure().getInitialValue() );
 		// 50 is the annotation default
 		assertEquals( 50, seqGenerator.getDatabaseStructure().getIncrementSize() );
 		assertFalse( OptimizerFactory.NoopOptimizer.class.isInstance( seqGenerator.getOptimizer() ) );
 	}
 
 	@Test
 	public void testCompleteSequenceEntity() {
 		final EntityPersister persister = sessionFactory().getEntityPersister( CompleteSequenceEntity.class.getName() );
 		IdentifierGenerator generator = persister.getIdentifierGenerator();
 		assertTrue( SequenceStyleGenerator.class.isInstance( generator ) );
 		SequenceStyleGenerator seqGenerator = (SequenceStyleGenerator) generator;
 		assertEquals( "my_catalog.my_schema."+CompleteSequenceEntity.SEQ_NAME, seqGenerator.getDatabaseStructure().getName() );
 		assertEquals( 1000, seqGenerator.getDatabaseStructure().getInitialValue() );
 		assertEquals( 52, seqGenerator.getDatabaseStructure().getIncrementSize() );
 		assertFalse( OptimizerFactory.NoopOptimizer.class.isInstance( seqGenerator.getOptimizer() ) );
 	}
 
 	@Test
 	public void testAutoEntity() {
 		final EntityPersister persister = sessionFactory().getEntityPersister( AutoEntity.class.getName() );
 		IdentifierGenerator generator = persister.getIdentifierGenerator();
 		assertTrue( SequenceStyleGenerator.class.isInstance( generator ) );
 		SequenceStyleGenerator seqGenerator = (SequenceStyleGenerator) generator;
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, seqGenerator.getDatabaseStructure().getName() );
 		assertEquals( SequenceStyleGenerator.DEFAULT_INITIAL_VALUE, seqGenerator.getDatabaseStructure().getInitialValue() );
 		assertEquals( SequenceStyleGenerator.DEFAULT_INCREMENT_SIZE, seqGenerator.getDatabaseStructure().getIncrementSize() );
 	}
 
 	@Test
 	public void testMinimalTableEntity() {
 		final EntityPersister persister = sessionFactory().getEntityPersister( MinimalTableEntity.class.getName() );
 		IdentifierGenerator generator = persister.getIdentifierGenerator();
 		assertTrue( TableGenerator.class.isInstance( generator ) );
 		TableGenerator tabGenerator = (TableGenerator) generator;
 		assertEquals( MinimalTableEntity.TBL_NAME, tabGenerator.getTableName() );
 		assertEquals( TableGenerator.DEF_SEGMENT_COLUMN, tabGenerator.getSegmentColumnName() );
 		assertEquals( "MINIMAL_TBL", tabGenerator.getSegmentValue() );
 		assertEquals( TableGenerator.DEF_VALUE_COLUMN, tabGenerator.getValueColumnName() );
 		// 0 is the annotation default, but its expected to be treated as 1
 		assertEquals( 1, tabGenerator.getInitialValue() );
 		// 50 is the annotation default
 		assertEquals( 50, tabGenerator.getIncrementSize() );
 		assertTrue( OptimizerFactory.PooledOptimizer.class.isInstance( tabGenerator.getOptimizer() ) );
 	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-6790")
+	public void testSequencePerEntity() {
+		// Checking first entity.
+		EntityPersister persister = sessionFactory().getEntityPersister( DedicatedSequenceEntity1.class.getName() );
+		IdentifierGenerator generator = persister.getIdentifierGenerator();
+		assertTrue( SequenceStyleGenerator.class.isInstance( generator ) );
+		SequenceStyleGenerator seqGenerator = (SequenceStyleGenerator) generator;
+		assertEquals(
+				StringHelper.unqualifyEntityName( DedicatedSequenceEntity1.class.getName() ) + DedicatedSequenceEntity1.SEQUENCE_SUFFIX,
+				seqGenerator.getDatabaseStructure().getName()
+		);
+
+		// Checking second entity.
+		persister = sessionFactory().getEntityPersister( DedicatedSequenceEntity2.class.getName() );
+		generator = persister.getIdentifierGenerator();
+		assertTrue( SequenceStyleGenerator.class.isInstance( generator ) );
+		seqGenerator = (SequenceStyleGenerator) generator;
+		assertEquals(
+				DedicatedSequenceEntity2.ENTITY_NAME + DedicatedSequenceEntity1.SEQUENCE_SUFFIX,
+				seqGenerator.getDatabaseStructure().getName()
+		);
+	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/package-info.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/package-info.java
new file mode 100644
index 0000000000..6b9e7e5c6f
--- /dev/null
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/generationmappings/package-info.java
@@ -0,0 +1,12 @@
+@GenericGenerator(name = "SequencePerEntityGenerator",
+		strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
+		parameters = {
+				@Parameter(name = "prefer_sequence_per_entity", value = "true"),
+				@Parameter(name = "sequence_per_entity_suffix", value = DedicatedSequenceEntity1.SEQUENCE_SUFFIX)
+		}
+
+)
+package org.hibernate.test.annotations.id.generationmappings;
+
+import org.hibernate.annotations.GenericGenerator;
+import org.hibernate.annotations.Parameter;
\ No newline at end of file
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/IdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/IdTest.java
index 716a39f0ef..6c856ecd5e 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/IdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/id/sequences/IdTest.java
@@ -1,344 +1,367 @@
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
 package org.hibernate.test.annotations.id.sequences;
 
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.mapping.Column;
+import org.hibernate.test.annotations.id.generationmappings.DedicatedSequenceEntity1;
+import org.hibernate.test.annotations.id.generationmappings.DedicatedSequenceEntity2;
 import org.hibernate.test.annotations.id.sequences.entities.Ball;
 import org.hibernate.test.annotations.id.sequences.entities.BreakDance;
 import org.hibernate.test.annotations.id.sequences.entities.Computer;
 import org.hibernate.test.annotations.id.sequences.entities.Department;
 import org.hibernate.test.annotations.id.sequences.entities.Dog;
 import org.hibernate.test.annotations.id.sequences.entities.FirTree;
 import org.hibernate.test.annotations.id.sequences.entities.Footballer;
 import org.hibernate.test.annotations.id.sequences.entities.FootballerPk;
 import org.hibernate.test.annotations.id.sequences.entities.Furniture;
 import org.hibernate.test.annotations.id.sequences.entities.GoalKeeper;
 import org.hibernate.test.annotations.id.sequences.entities.Home;
 import org.hibernate.test.annotations.id.sequences.entities.Monkey;
 import org.hibernate.test.annotations.id.sequences.entities.Phone;
 import org.hibernate.test.annotations.id.sequences.entities.Shoe;
 import org.hibernate.test.annotations.id.sequences.entities.SoundSystem;
 import org.hibernate.test.annotations.id.sequences.entities.Store;
 import org.hibernate.test.annotations.id.sequences.entities.Tree;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 
 /**
  * @author Emmanuel Bernard
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 @SuppressWarnings("unchecked")
 @RequiresDialectFeature(DialectChecks.SupportsSequences.class)
 public class IdTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testGenericGenerator() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		SoundSystem system = new SoundSystem();
 		system.setBrand( "Genelec" );
 		system.setModel( "T234" );
 		Furniture fur = new Furniture();
 		s.persist( system );
 		s.persist( fur );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		system = ( SoundSystem ) s.get( SoundSystem.class, system.getId() );
 		fur = ( Furniture ) s.get( Furniture.class, fur.getId() );
 		assertNotNull( system );
 		assertNotNull( fur );
 		s.delete( system );
 		s.delete( fur );
 		tx.commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testGenericGenerators() throws Exception {
 		// Ensures that GenericGenerator annotations wrapped inside a GenericGenerators holder are bound correctly
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Monkey monkey = new Monkey();
 		s.persist( monkey );
 		s.flush();
 		assertNotNull( monkey.getId() );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testTableGenerator() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		Ball b = new Ball();
 		Dog d = new Dog();
 		Computer c = new Computer();
 		s.persist( b );
 		s.persist( d );
 		s.persist( c );
 		tx.commit();
 		s.close();
 		assertEquals( "table id not generated", new Integer( 1 ), b.getId() );
 		assertEquals(
 				"generator should not be shared", new Integer( 1 ), d
 						.getId()
 		);
 		assertEquals( "default value should work", new Long( 1 ), c.getId() );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Ball.class, new Integer( 1 ) ) );
 		s.delete( s.get( Dog.class, new Integer( 1 ) ) );
 		s.delete( s.get( Computer.class, new Long( 1 ) ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSequenceGenerator() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Shoe b = new Shoe();
 		s.persist( b );
 		tx.commit();
 		s.close();
 		assertNotNull( b.getId() );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Shoe.class, b.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testClassLevelGenerator() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Store b = new Store();
 		s.persist( b );
 		tx.commit();
 		s.close();
 		assertNotNull( b.getId() );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Store.class, b.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testMethodLevelGenerator() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Department b = new Department();
 		s.persist( b );
 		tx.commit();
 		s.close();
 		assertNotNull( b.getId() );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( s.get( Department.class, b.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDefaultSequence() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Home h = new Home();
 		s.persist( h );
 		tx.commit();
 		s.close();
 		assertNotNull( h.getId() );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Home reloadedHome = ( Home ) s.get( Home.class, h.getId() );
 		assertEquals( h.getId(), reloadedHome.getId() );
 		s.delete( reloadedHome );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParameterizedAuto() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Home h = new Home();
 		s.persist( h );
 		tx.commit();
 		s.close();
 		assertNotNull( h.getId() );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Home reloadedHome = ( Home ) s.get( Home.class, h.getId() );
 		assertEquals( h.getId(), reloadedHome.getId() );
 		s.delete( reloadedHome );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testIdInEmbeddableSuperclass() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		FirTree chrismasTree = new FirTree();
 		s.persist( chrismasTree );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		chrismasTree = ( FirTree ) s.get( FirTree.class, chrismasTree.getId() );
 		assertNotNull( chrismasTree );
 		s.delete( chrismasTree );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testIdClass() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Footballer fb = new Footballer( "David", "Beckam", "Arsenal" );
 		GoalKeeper keeper = new GoalKeeper( "Fabien", "Bartez", "OM" );
 		s.persist( fb );
 		s.persist( keeper );
 		tx.commit();
 		s.clear();
 
 		// lookup by id
 		tx = s.beginTransaction();
 		FootballerPk fpk = new FootballerPk( "David", "Beckam" );
 		fb = ( Footballer ) s.get( Footballer.class, fpk );
 		FootballerPk fpk2 = new FootballerPk( "Fabien", "Bartez" );
 		keeper = ( GoalKeeper ) s.get( GoalKeeper.class, fpk2 );
 		assertNotNull( fb );
 		assertNotNull( keeper );
 		assertEquals( "Beckam", fb.getLastname() );
 		assertEquals( "Arsenal", fb.getClub() );
 		assertEquals(
 				1, s.createQuery(
 						"from Footballer f where f.firstname = 'David'"
 				).list().size()
 		);
 		tx.commit();
 
 		// reattach by merge
 		tx = s.beginTransaction();
 		fb.setClub( "Bimbo FC" );
 		s.merge( fb );
 		tx.commit();
 
 		// reattach by saveOrUpdate
 		tx = s.beginTransaction();
 		fb.setClub( "Bimbo FC SA" );
 		s.saveOrUpdate( fb );
 		tx.commit();
 
 		// clean up
 		s.clear();
 		tx = s.beginTransaction();
 		fpk = new FootballerPk( "David", "Beckam" );
 		fb = ( Footballer ) s.get( Footballer.class, fpk );
 		assertEquals( "Bimbo FC SA", fb.getClub() );
 		s.delete( fb );
 		s.delete( keeper );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
+	@TestForIssue(jiraKey = "HHH-6790")
+	public void testSequencePerEntity() {
+		Session session = openSession();
+		session.beginTransaction();
+		DedicatedSequenceEntity1 entity1 = new DedicatedSequenceEntity1();
+		DedicatedSequenceEntity2 entity2 = new DedicatedSequenceEntity2();
+		session.persist( entity1 );
+		session.persist( entity2 );
+		session.getTransaction().commit();
+
+		assertEquals( 1, entity1.getId().intValue() );
+		assertEquals( 1, entity2.getId().intValue() );
+
+		session.close();
+	}
+
+	@Test
 	public void testColumnDefinition() {
 		Column idCol = ( Column ) configuration().getClassMapping( Ball.class.getName() )
 				.getIdentifierProperty().getValue().getColumnIterator().next();
 		assertEquals( "ball_id", idCol.getName() );
 	}
 
 	@Test
 	public void testLowAllocationSize() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		int size = 4;
 		BreakDance[] bds = new BreakDance[size];
 		for ( int i = 0; i < size; i++ ) {
 			bds[i] = new BreakDance();
 			s.persist( bds[i] );
 		}
 		s.flush();
 		for ( int i = 0; i < size; i++ ) {
 			assertEquals( i + 1, bds[i].id.intValue() );
 		}
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Ball.class, Shoe.class, Store.class,
 				Department.class, Dog.class, Computer.class, Home.class,
 				Phone.class, Tree.class, FirTree.class, Footballer.class,
 				SoundSystem.class, Furniture.class, GoalKeeper.class,
-				BreakDance.class, Monkey.class
+				BreakDance.class, Monkey.class, DedicatedSequenceEntity1.class,
+				DedicatedSequenceEntity2.class
 		};
 	}
 
 	@Override
 	protected String[] getAnnotatedPackages() {
 		return new String[] {
 				"org.hibernate.test.annotations",
-				"org.hibernate.test.annotations.id"
+				"org.hibernate.test.annotations.id",
+				"org.hibernate.test.annotations.id.generationmappings"
 		};
 	}
 
 	@Override
 	protected String[] getXmlFiles() {
 		return new String[] { "org/hibernate/test/annotations/orm.xml" };
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/BasicSequenceTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/BasicSequenceTest.java
index 63430b3dd1..4973ac1111 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/BasicSequenceTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/BasicSequenceTest.java
@@ -1,74 +1,97 @@
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
 package org.hibernate.test.idgen.enhanced.sequence;
 
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.id.IdentifierGeneratorHelper.BasicHolder;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertClassAssignability;
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
+ * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class BasicSequenceTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
-		return new String[] { "idgen/enhanced/sequence/Basic.hbm.xml" };
+		return new String[] { "idgen/enhanced/sequence/Basic.hbm.xml", "idgen/enhanced/sequence/Dedicated.hbm.xml" };
 	}
 
 	@Test
 	public void testNormalBoundary() {
 		EntityPersister persister = sessionFactory().getEntityPersister( Entity.class.getName() );
 		assertClassAssignability( SequenceStyleGenerator.class, persister.getIdentifierGenerator().getClass() );
 		SequenceStyleGenerator generator = ( SequenceStyleGenerator ) persister.getIdentifierGenerator();
 
 		int count = 5;
 		Entity[] entities = new Entity[count];
 		Session s = openSession();
 		s.beginTransaction();
 		for ( int i = 0; i < count; i++ ) {
 			entities[i] = new Entity( "" + ( i + 1 ) );
 			s.save( entities[i] );
 			long expectedId = i + 1;
 			assertEquals( expectedId, entities[i].getId().longValue() );
 			assertEquals( expectedId, generator.getDatabaseStructure().getTimesAccessed() );
 			assertEquals( expectedId, ( (BasicHolder) generator.getOptimizer().getLastSourceValue() ).getActualLongValue() );
 		}
 		s.getTransaction().commit();
 
 		s.beginTransaction();
 		for ( int i = 0; i < count; i++ ) {
 			assertEquals( i + 1, entities[i].getId().intValue() );
 			s.delete( entities[i] );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-6790")
+	public void testSequencePerEntity() {
+		final String overriddenEntityName = "SpecialEntity";
+		EntityPersister persister = sessionFactory().getEntityPersister( overriddenEntityName );
+		assertClassAssignability( SequenceStyleGenerator.class, persister.getIdentifierGenerator().getClass() );
+		SequenceStyleGenerator generator = (SequenceStyleGenerator) persister.getIdentifierGenerator();
+		assertEquals( overriddenEntityName + SequenceStyleGenerator.DEF_SEQUENCE_SUFFIX, generator.getDatabaseStructure().getName() );
+
+		Session s = openSession();
+		s.beginTransaction();
+		Entity entity1 = new Entity( "1" );
+		s.save( overriddenEntityName, entity1 );
+		Entity entity2 = new Entity( "2" );
+		s.save( overriddenEntityName, entity2 );
+		s.getTransaction().commit();
+
+		assertEquals( 1, entity1.getId().intValue() );
+		assertEquals( 2, entity2.getId().intValue() );
+	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/Dedicated.hbm.xml b/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/Dedicated.hbm.xml
new file mode 100644
index 0000000000..1a3cbda142
--- /dev/null
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/idgen/enhanced/sequence/Dedicated.hbm.xml
@@ -0,0 +1,22 @@
+<?xml version="1.0"?>
+<!DOCTYPE hibernate-mapping PUBLIC
+	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
+	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
+
+<!--
+    Demonstrates use of the enhanced sequence-based identifier
+    generator, with dedicated sequence for each entity.
+-->
+
+<hibernate-mapping package="org.hibernate.test.idgen.enhanced.sequence">
+
+    <class name="Entity" table="ID_SEQ_BSC_ENTITY" entity-name="SpecialEntity">
+        <id name="id" column="ID" type="long">
+            <generator class="org.hibernate.id.enhanced.SequenceStyleGenerator">
+                <param name="prefer_sequence_per_entity">true</param>
+            </generator>
+        </id>
+        <property name="name" type="string"/>
+	</class>
+
+</hibernate-mapping>
