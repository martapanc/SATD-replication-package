diff --git a/documentation/src/main/docbook/manual/en-US/content/performance.xml b/documentation/src/main/docbook/manual/en-US/content/performance.xml
index 6ca4d017d8..a156785683 100644
--- a/documentation/src/main/docbook/manual/en-US/content/performance.xml
+++ b/documentation/src/main/docbook/manual/en-US/content/performance.xml
@@ -1,1693 +1,1715 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
   ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
   ~ distributed under license by Red Hat Middleware LLC.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
   -->
 <!DOCTYPE chapter PUBLIC "-//OASIS//DTD DocBook XML V4.5//EN"
 "http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd" [
 <!ENTITY % BOOK_ENTITIES SYSTEM "../HIBERNATE_-_Relational_Persistence_for_Idiomatic_Java.ent">
 %BOOK_ENTITIES;
 ]>
 <chapter id="performance">
   <title>Improving performance</title>
 
   <section id="performance-fetching" revision="2">
     <title>Fetching strategies</title>
 
     <para>Hibernate uses a <emphasis>fetching strategy</emphasis> to retrieve
     associated objects if the application needs to navigate the association.
     Fetch strategies can be declared in the O/R mapping metadata, or
     over-ridden by a particular HQL or <literal>Criteria</literal>
     query.</para>
 
     <para>Hibernate3 defines the following fetching strategies:</para>
 
     <itemizedlist>
       <listitem>
         <para><emphasis>Join fetching</emphasis>: Hibernate retrieves the
         associated instance or collection in the same
         <literal>SELECT</literal>, using an <literal>OUTER
         JOIN</literal>.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>Select fetching</emphasis>: a second
         <literal>SELECT</literal> is used to retrieve the associated entity or
         collection. Unless you explicitly disable lazy fetching by specifying
         <literal>lazy="false"</literal>, this second select will only be
         executed when you access the association.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>Subselect fetching</emphasis>: a second
         <literal>SELECT</literal> is used to retrieve the associated
         collections for all entities retrieved in a previous query or fetch.
         Unless you explicitly disable lazy fetching by specifying
         <literal>lazy="false"</literal>, this second select will only be
         executed when you access the association.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>Batch fetching</emphasis>: an optimization strategy
         for select fetching. Hibernate retrieves a batch of entity instances
         or collections in a single <literal>SELECT</literal> by specifying a
         list of primary or foreign keys.</para>
       </listitem>
     </itemizedlist>
 
     <para>Hibernate also distinguishes between:</para>
 
     <itemizedlist>
       <listitem>
         <para><emphasis>Immediate fetching</emphasis>: an association,
         collection or attribute is fetched immediately when the owner is
         loaded.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>Lazy collection fetching</emphasis>: a collection is
         fetched when the application invokes an operation upon that
         collection. This is the default for collections.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>"Extra-lazy" collection fetching</emphasis>:
         individual elements of the collection are accessed from the database
         as needed. Hibernate tries not to fetch the whole collection into
         memory unless absolutely needed. It is suitable for large
         collections.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>Proxy fetching</emphasis>: a single-valued association
         is fetched when a method other than the identifier getter is invoked
         upon the associated object.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>"No-proxy" fetching</emphasis>: a single-valued
         association is fetched when the instance variable is accessed.
         Compared to proxy fetching, this approach is less lazy; the
         association is fetched even when only the identifier is accessed. It
         is also more transparent, since no proxy is visible to the
         application. This approach requires buildtime bytecode instrumentation
         and is rarely necessary.</para>
       </listitem>
 
       <listitem>
         <para><emphasis>Lazy attribute fetching</emphasis>: an attribute or
         single valued association is fetched when the instance variable is
         accessed. This approach requires buildtime bytecode instrumentation
         and is rarely necessary.</para>
       </listitem>
     </itemizedlist>
 
     <para>We have two orthogonal notions here: <emphasis>when</emphasis> is
     the association fetched and <emphasis>how</emphasis> is it fetched. It is
     important that you do not confuse them. We use <literal>fetch</literal> to
     tune performance. We can use <literal>lazy</literal> to define a contract
     for what data is always available in any detached instance of a particular
     class.</para>
 
     <section id="performance-fetching-lazy">
       <title>Working with lazy associations</title>
 
       <para>By default, Hibernate3 uses lazy select fetching for collections
       and lazy proxy fetching for single-valued associations. These defaults
       make sense for most associations in the majority of applications.</para>
 
       <para>If you set <literal>hibernate.default_batch_fetch_size</literal>,
       Hibernate will use the batch fetch optimization for lazy fetching. This
       optimization can also be enabled at a more granular level.</para>
 
       <para>Please be aware that access to a lazy association outside of the
       context of an open Hibernate session will result in an exception. For
       example:</para>
 
       <programlisting role="JAVA">s = sessions.openSession();
 Transaction tx = s.beginTransaction();
             
 User u = (User) s.createQuery("from User u where u.name=:userName")
     .setString("userName", userName).uniqueResult();
 Map permissions = u.getPermissions();
 
 tx.commit();
 s.close();
 
 Integer accessLevel = (Integer) permissions.get("accounts");  // Error!</programlisting>
 
       <para>Since the permissions collection was not initialized when the
       <literal>Session</literal> was closed, the collection will not be able
       to load its state. <emphasis>Hibernate does not support lazy
       initialization for detached objects</emphasis>. This can be fixed by
       moving the code that reads from the collection to just before the
       transaction is committed.</para>
 
       <para>Alternatively, you can use a non-lazy collection or association,
       by specifying <literal>lazy="false"</literal> for the association
       mapping. However, it is intended that lazy initialization be used for
       almost all collections and associations. If you define too many non-lazy
       associations in your object model, Hibernate will fetch the entire
       database into memory in every transaction.</para>
 
       <para>On the other hand, you can use join fetching, which is non-lazy by
       nature, instead of select fetching in a particular transaction. We will
       now explain how to customize the fetching strategy. In Hibernate3, the
       mechanisms for choosing a fetch strategy are identical for single-valued
       associations and collections.</para>
     </section>
 
     <section id="performance-fetching-custom" revision="4">
       <title>Tuning fetch strategies</title>
 
       <para>Select fetching (the default) is extremely vulnerable to N+1
       selects problems, so we might want to enable join fetching in the
       mapping document:</para>
 
       <programlisting role="XML">&lt;set name="permissions"
             fetch="join"&gt;
     &lt;key column="userId"/&gt;
     &lt;one-to-many class="Permission"/&gt;
 &lt;/set</programlisting>
 
       <programlisting role="XML">&lt;many-to-one name="mother" class="Cat" fetch="join"/&gt;</programlisting>
 
       <para>The <literal>fetch</literal> strategy defined in the mapping
       document affects:</para>
 
       <itemizedlist>
         <listitem>
           <para>retrieval via <literal>get()</literal> or
           <literal>load()</literal></para>
         </listitem>
 
         <listitem>
           <para>retrieval that happens implicitly when an association is
           navigated</para>
         </listitem>
 
         <listitem>
           <para><literal>Criteria</literal> queries</para>
         </listitem>
 
         <listitem>
           <para>HQL queries if <literal>subselect</literal> fetching is
           used</para>
         </listitem>
       </itemizedlist>
 
       <para>Irrespective of the fetching strategy you use, the defined
       non-lazy graph is guaranteed to be loaded into memory. This might,
       however, result in several immediate selects being used to execute a
       particular HQL query.</para>
 
       <para>Usually, the mapping document is not used to customize fetching.
       Instead, we keep the default behavior, and override it for a particular
       transaction, using <literal>left join fetch</literal> in HQL. This tells
       Hibernate to fetch the association eagerly in the first select, using an
       outer join. In the <literal>Criteria</literal> query API, you would use
       <literal>setFetchMode(FetchMode.JOIN)</literal>.</para>
 
       <para>If you want to change the fetching strategy used by
       <literal>get()</literal> or <literal>load()</literal>, you can use a
       <literal>Criteria</literal> query. For example:</para>
 
       <programlisting role="JAVA">User user = (User) session.createCriteria(User.class)
                 .setFetchMode("permissions", FetchMode.JOIN)
                 .add( Restrictions.idEq(userId) )
                 .uniqueResult();</programlisting>
 
       <para>This is Hibernate's equivalent of what some ORM solutions call a
       "fetch plan".</para>
 
       <para>A completely different approach to problems with N+1 selects is to
       use the second-level cache.</para>
     </section>
 
     <section id="performance-fetching-proxies" revision="2">
       <title>Single-ended association proxies</title>
 
       <para>Lazy fetching for collections is implemented using Hibernate's own
       implementation of persistent collections. However, a different mechanism
       is needed for lazy behavior in single-ended associations. The target
       entity of the association must be proxied. Hibernate implements lazy
       initializing proxies for persistent objects using runtime bytecode
       enhancement which is accessed via the CGLIB library.</para>
 
       <para>At startup, Hibernate3 generates proxies by default for all
       persistent classes and uses them to enable lazy fetching of
       <literal>many-to-one</literal> and <literal>one-to-one</literal>
       associations.</para>
 
       <para>The mapping file may declare an interface to use as the proxy
       interface for that class, with the <literal>proxy</literal> attribute.
       By default, Hibernate uses a subclass of the class. <emphasis>The
       proxied class must implement a default constructor with at least package
       visibility. This constructor is recommended for all persistent
       classes</emphasis>.</para>
 
       <para>There are potential problems to note when extending this approach
       to polymorphic classes.For example:</para>
 
       <programlisting role="XML">&lt;class name="Cat" proxy="Cat"&gt;
     ......
     &lt;subclass name="DomesticCat"&gt;
         .....
     &lt;/subclass&gt;
 &lt;/class&gt;</programlisting>
 
       <para>Firstly, instances of <literal>Cat</literal> will never be
       castable to <literal>DomesticCat</literal>, even if the underlying
       instance is an instance of <literal>DomesticCat</literal>:</para>
 
       <programlisting role="JAVA">Cat cat = (Cat) session.load(Cat.class, id);  // instantiate a proxy (does not hit the db)
 if ( cat.isDomesticCat() ) {                  // hit the db to initialize the proxy
     DomesticCat dc = (DomesticCat) cat;       // Error!
     ....
 }</programlisting>
 
       <para>Secondly, it is possible to break proxy
       <literal>==</literal>:</para>
 
       <programlisting role="JAVA">Cat cat = (Cat) session.load(Cat.class, id);            // instantiate a Cat proxy
 DomesticCat dc = 
         (DomesticCat) session.load(DomesticCat.class, id);  // acquire new DomesticCat proxy!
 System.out.println(cat==dc);                            // false</programlisting>
 
       <para>However, the situation is not quite as bad as it looks. Even
       though we now have two references to different proxy objects, the
       underlying instance will still be the same object:</para>
 
       <programlisting role="JAVA">cat.setWeight(11.0);  // hit the db to initialize the proxy
 System.out.println( dc.getWeight() );  // 11.0</programlisting>
 
       <para>Third, you cannot use a CGLIB proxy for a <literal>final</literal>
       class or a class with any <literal>final</literal> methods.</para>
 
       <para>Finally, if your persistent object acquires any resources upon
       instantiation (e.g. in initializers or default constructor), then those
       resources will also be acquired by the proxy. The proxy class is an
       actual subclass of the persistent class.</para>
 
       <para>These problems are all due to fundamental limitations in Java's
       single inheritance model. To avoid these problems your persistent
       classes must each implement an interface that declares its business
       methods. You should specify these interfaces in the mapping file where
       <literal>CatImpl</literal> implements the interface
       <literal>Cat</literal> and <literal>DomesticCatImpl</literal> implements
       the interface <literal>DomesticCat</literal>. For example:</para>
 
       <programlisting role="XML">&lt;class name="CatImpl" proxy="Cat"&gt;
     ......
     &lt;subclass name="DomesticCatImpl" proxy="DomesticCat"&gt;
         .....
     &lt;/subclass&gt;
 &lt;/class&gt;</programlisting>
 
       <para>Then proxies for instances of <literal>Cat</literal> and
       <literal>DomesticCat</literal> can be returned by
       <literal>load()</literal> or <literal>iterate()</literal>.</para>
 
       <programlisting role="JAVA">Cat cat = (Cat) session.load(CatImpl.class, catid);
 Iterator iter = session.createQuery("from CatImpl as cat where cat.name='fritz'").iterate();
 Cat fritz = (Cat) iter.next();</programlisting>
 
       <note>
         <title>Note</title>
 
         <para><literal>list()</literal> does not usually return
         proxies.</para>
       </note>
 
       <para>Relationships are also lazily initialized. This means you must
       declare any properties to be of type <literal>Cat</literal>, not
       <literal>CatImpl</literal>.</para>
 
       <para>Certain operations do <emphasis>not</emphasis> require proxy
       initialization:</para>
 
       <itemizedlist spacing="compact">
         <listitem>
           <para><literal>equals()</literal>: if the persistent class does not
           override <literal>equals()</literal></para>
         </listitem>
 
         <listitem>
           <para><literal>hashCode()</literal>: if the persistent class does
           not override <literal>hashCode()</literal></para>
         </listitem>
 
         <listitem>
           <para>The identifier getter method</para>
         </listitem>
       </itemizedlist>
 
       <para>Hibernate will detect persistent classes that override
       <literal>equals()</literal> or <literal>hashCode()</literal>.</para>
 
       <para>By choosing <literal>lazy="no-proxy"</literal> instead of the
       default <literal>lazy="proxy"</literal>, you can avoid problems
       associated with typecasting. However, buildtime bytecode instrumentation
       is required, and all operations will result in immediate proxy
       initialization.</para>
     </section>
 
     <section id="performance-fetching-initialization" revision="1">
       <title>Initializing collections and proxies</title>
 
       <para>A <literal>LazyInitializationException</literal> will be thrown by
       Hibernate if an uninitialized collection or proxy is accessed outside of
       the scope of the <literal>Session</literal>, i.e., when the entity
       owning the collection or having the reference to the proxy is in the
       detached state.</para>
 
       <para>Sometimes a proxy or collection needs to be initialized before
       closing the <literal>Session</literal>. You can force initialization by
       calling <literal>cat.getSex()</literal> or
       <literal>cat.getKittens().size()</literal>, for example. However, this
       can be confusing to readers of the code and it is not convenient for
       generic code.</para>
 
       <para>The static methods <literal>Hibernate.initialize()</literal> and
       <literal>Hibernate.isInitialized()</literal>, provide the application
       with a convenient way of working with lazily initialized collections or
       proxies. <literal>Hibernate.initialize(cat)</literal> will force the
       initialization of a proxy, <literal>cat</literal>, as long as its
       <literal>Session</literal> is still open. <literal>Hibernate.initialize(
       cat.getKittens() )</literal> has a similar effect for the collection of
       kittens.</para>
 
       <para>Another option is to keep the <literal>Session</literal> open
       until all required collections and proxies have been loaded. In some
       application architectures, particularly where the code that accesses
       data using Hibernate, and the code that uses it are in different
       application layers or different physical processes, it can be a problem
       to ensure that the <literal>Session</literal> is open when a collection
       is initialized. There are two basic ways to deal with this issue:</para>
 
       <itemizedlist>
         <listitem>
           <para>In a web-based application, a servlet filter can be used to
           close the <literal>Session</literal> only at the end of a user
           request, once the rendering of the view is complete (the
           <emphasis>Open Session in View</emphasis> pattern). Of course, this
           places heavy demands on the correctness of the exception handling of
           your application infrastructure. It is vitally important that the
           <literal>Session</literal> is closed and the transaction ended
           before returning to the user, even when an exception occurs during
           rendering of the view. See the Hibernate Wiki for examples of this
           "Open Session in View" pattern.</para>
         </listitem>
 
         <listitem>
           <para>In an application with a separate business tier, the business
           logic must "prepare" all collections that the web tier needs before
           returning. This means that the business tier should load all the
           data and return all the data already initialized to the
           presentation/web tier that is required for a particular use case.
           Usually, the application calls
           <literal>Hibernate.initialize()</literal> for each collection that
           will be needed in the web tier (this call must occur before the
           session is closed) or retrieves the collection eagerly using a
           Hibernate query with a <literal>FETCH</literal> clause or a
           <literal>FetchMode.JOIN</literal> in <literal>Criteria</literal>.
           This is usually easier if you adopt the <emphasis>Command</emphasis>
           pattern instead of a <emphasis>Session Facade</emphasis>.</para>
         </listitem>
 
         <listitem>
           <para>You can also attach a previously loaded object to a new
           <literal>Session</literal> with <literal>merge()</literal> or
           <literal>lock()</literal> before accessing uninitialized collections
           or other proxies. Hibernate does not, and certainly
           <emphasis>should</emphasis> not, do this automatically since it
           would introduce impromptu transaction semantics.</para>
         </listitem>
       </itemizedlist>
 
       <para>Sometimes you do not want to initialize a large collection, but
       still need some information about it, like its size, for example, or a
       subset of the data.</para>
 
       <para>You can use a collection filter to get the size of a collection
       without initializing it:</para>
 
       <programlisting role="JAVA">( (Integer) s.createFilter( collection, "select count(*)" ).list().get(0) ).intValue()</programlisting>
 
       <para>The <literal>createFilter()</literal> method is also used to
       efficiently retrieve subsets of a collection without needing to
       initialize the whole collection:</para>
 
       <programlisting role="JAVA">s.createFilter( lazyCollection, "").setFirstResult(0).setMaxResults(10).list();</programlisting>
     </section>
 
     <section id="performance-fetching-batch">
       <title>Using batch fetching</title>
 
       <para>Using batch fetching, Hibernate can load several uninitialized
       proxies if one proxy is accessed. Batch fetching is an optimization of
       the lazy select fetching strategy. There are two ways you can configure
       batch fetching: on the class level and the collection level.</para>
 
       <para>Batch fetching for classes/entities is easier to understand.
       Consider the following example: at runtime you have 25
       <literal>Cat</literal> instances loaded in a <literal>Session</literal>,
       and each <literal>Cat</literal> has a reference to its
       <literal>owner</literal>, a <literal>Person</literal>. The
       <literal>Person</literal> class is mapped with a proxy,
       <literal>lazy="true"</literal>. If you now iterate through all cats and
       call <literal>getOwner()</literal> on each, Hibernate will, by default,
       execute 25 <literal>SELECT</literal> statements to retrieve the proxied
       owners. You can tune this behavior by specifying a
       <literal>batch-size</literal> in the mapping of
       <literal>Person</literal>:</para>
 
       <programlisting role="XML">&lt;class name="Person" batch-size="10"&gt;...&lt;/class&gt;</programlisting>
 
       <para>Hibernate will now execute only three queries: the pattern is 10,
       10, 5.</para>
 
       <para>You can also enable batch fetching of collections. For example, if
       each <literal>Person</literal> has a lazy collection of
       <literal>Cat</literal>s, and 10 persons are currently loaded in the
       <literal>Session</literal>, iterating through all persons will generate
       10 <literal>SELECT</literal>s, one for every call to
       <literal>getCats()</literal>. If you enable batch fetching for the
       <literal>cats</literal> collection in the mapping of
       <literal>Person</literal>, Hibernate can pre-fetch collections:</para>
 
       <programlisting role="XML">&lt;class name="Person"&gt;
     &lt;set name="cats" batch-size="3"&gt;
         ...
     &lt;/set&gt;
 &lt;/class&gt;</programlisting>
 
       <para>With a <literal>batch-size</literal> of 3, Hibernate will load 3,
       3, 3, 1 collections in four <literal>SELECT</literal>s. Again, the value
       of the attribute depends on the expected number of uninitialized
       collections in a particular <literal>Session</literal>.</para>
 
       <para>Batch fetching of collections is particularly useful if you have a
       nested tree of items, i.e. the typical bill-of-materials pattern.
       However, a <emphasis>nested set</emphasis> or a <emphasis>materialized
       path</emphasis> might be a better option for read-mostly trees.</para>
     </section>
 
     <section id="performance-fetching-subselect">
       <title>Using subselect fetching</title>
 
       <para>If one lazy collection or single-valued proxy has to be fetched,
       Hibernate will load all of them, re-running the original query in a
       subselect. This works in the same way as batch-fetching but without the
       piecemeal loading.</para>
 
       <!-- TODO: Write more about this -->
     </section>
 
     <section id="performance-fetching-profiles">
       <title>Fetch profiles</title>
 
       <para>Another way to affect the fetching strategy for loading associated
       objects is through something called a fetch profile, which is a named
       configuration associated with the
       <interfacename>org.hibernate.SessionFactory</interfacename> but enabled,
       by name, on the <interfacename>org.hibernate.Session</interfacename>.
       Once enabled on a <interfacename>org.hibernate.Session</interfacename>,
       the fetch profile will be in affect for that
       <interfacename>org.hibernate.Session</interfacename> until it is
       explicitly disabled.</para>
 
       <para>So what does that mean? Well lets explain that by way of an
       example which show the different available approaches to configure a
       fetch profile:</para>
 
       <example>
         <title>Specifying a fetch profile using
         <classname>@FetchProfile</classname></title>
 
         <programlisting role="XML">@Entity
 @FetchProfile(name = "customer-with-orders", fetchOverrides = {
    @FetchProfile.FetchOverride(entity = Customer.class, association = "orders", mode = FetchMode.JOIN)
 })
 public class Customer {
    @Id
    @GeneratedValue
    private long id;
 
    private String name;
 
    private long customerNumber;
 
    @OneToMany
    private Set&lt;Order&gt; orders;
 
    // standard getter/setter
    ...
 }</programlisting>
       </example>
 
       <example>
         <title>Specifying a fetch profile using
         <literal>&lt;fetch-profile&gt;</literal> outside
         <literal>&lt;class&gt;</literal> node</title>
 
         <programlisting role="XML">&lt;hibernate-mapping&gt;
     &lt;class name="Customer"&gt;
         ...
         &lt;set name="orders" inverse="true"&gt;
             &lt;key column="cust_id"/&gt;
             &lt;one-to-many class="Order"/&gt;
         &lt;/set&gt;
     &lt;/class&gt;
     &lt;class name="Order"&gt;
         ...
     &lt;/class&gt;
     &lt;fetch-profile name="customer-with-orders"&gt;
         &lt;fetch entity="Customer" association="orders" style="join"/&gt;
     &lt;/fetch-profile&gt;
 &lt;/hibernate-mapping&gt;
 </programlisting>
       </example>
 
       <example>
         <title>Specifying a fetch profile using
         <literal>&lt;fetch-profile&gt;</literal> inside
         <literal>&lt;class&gt;</literal> node</title>
 
         <programlisting role="XML">&lt;hibernate-mapping&gt;
     &lt;class name="Customer"&gt;
         ...
         &lt;set name="orders" inverse="true"&gt;
             &lt;key column="cust_id"/&gt;
             &lt;one-to-many class="Order"/&gt;
         &lt;/set&gt;
         &lt;fetch-profile name="customer-with-orders"&gt;
             &lt;fetch association="orders" style="join"/&gt;
         &lt;/fetch-profile&gt;
     &lt;/class&gt;
     &lt;class name="Order"&gt;
         ...
     &lt;/class&gt;
 &lt;/hibernate-mapping&gt;
 </programlisting>
       </example>
 
       <para>Now normally when you get a reference to a particular customer,
       that customer's set of orders will be lazy meaning we will not yet have
       loaded those orders from the database. Normally this is a good thing.
       Now lets say that you have a certain use case where it is more efficient
       to load the customer and their orders together. One way certainly is to
       use "dynamic fetching" strategies via an HQL or criteria queries. But
       another option is to use a fetch profile to achieve that. The following
       code will load both the customer <emphasis>and</emphasis>their
       orders:</para>
 
       <example>
         <title>Activating a fetch profile for a given
         <classname>Session</classname></title>
 
         <programlisting role="JAVA">Session session = ...;
 session.enableFetchProfile( "customer-with-orders" );  // name matches from mapping
 Customer customer = (Customer) session.get( Customer.class, customerId );
 </programlisting>
       </example>
 
       <note>
         <para><classname>@FetchProfile </classname>definitions are global and
         it does not matter on which class you place them. You can place the
         <classname>@FetchProfile</classname> annotation either onto a class or
         package (package-info.java). In order to define multiple fetch
         profiles for the same class or package
         <classname>@FetchProfiles</classname> can be used.</para>
       </note>
 
       <para>Currently only join style fetch profiles are supported, but they
       plan is to support additional styles. See <ulink
       url="http://opensource.atlassian.com/projects/hibernate/browse/HHH-3414">HHH-3414</ulink>
       for details.</para>
     </section>
 
     <section id="performance-fetching-lazyproperties">
       <title>Using lazy property fetching</title>
 
       <para>Hibernate3 supports the lazy fetching of individual properties.
       This optimization technique is also known as <emphasis>fetch
       groups</emphasis>. Please note that this is mostly a marketing feature;
       optimizing row reads is much more important than optimization of column
       reads. However, only loading some properties of a class could be useful
       in extreme cases. For example, when legacy tables have hundreds of
       columns and the data model cannot be improved.</para>
 
       <para>To enable lazy property loading, set the <literal>lazy</literal>
       attribute on your particular property mappings:</para>
 
       <programlisting role="XML">&lt;class name="Document"&gt;
        &lt;id name="id"&gt;
         &lt;generator class="native"/&gt;
     &lt;/id&gt;
     &lt;property name="name" not-null="true" length="50"/&gt;
     &lt;property name="summary" not-null="true" length="200" lazy="true"/&gt;
     &lt;property name="text" not-null="true" length="2000" lazy="true"/&gt;
 &lt;/class&gt;</programlisting>
 
       <para>Lazy property loading requires buildtime bytecode instrumentation.
       If your persistent classes are not enhanced, Hibernate will ignore lazy
       property settings and return to immediate fetching.</para>
 
       <para>For bytecode instrumentation, use the following Ant task:</para>
 
       <programlisting role="XML">&lt;target name="instrument" depends="compile"&gt;
     &lt;taskdef name="instrument" classname="org.hibernate.tool.instrument.InstrumentTask"&gt;
         &lt;classpath path="${jar.path}"/&gt;
         &lt;classpath path="${classes.dir}"/&gt;
         &lt;classpath refid="lib.class.path"/&gt;
     &lt;/taskdef&gt;
 
     &lt;instrument verbose="true"&gt;
         &lt;fileset dir="${testclasses.dir}/org/hibernate/auction/model"&gt;
             &lt;include name="*.class"/&gt;
         &lt;/fileset&gt;
     &lt;/instrument&gt;
 &lt;/target&gt;</programlisting>
 
       <para>A different way of avoiding unnecessary column reads, at least for
       read-only transactions, is to use the projection features of HQL or
       Criteria queries. This avoids the need for buildtime bytecode processing
       and is certainly a preferred solution.</para>
 
       <para>You can force the usual eager fetching of properties using
       <literal>fetch all properties</literal> in HQL.</para>
     </section>
   </section>
 
   <section id="performance-cache" revision="1">
     <title>The Second Level Cache</title>
 
     <para>A Hibernate <literal>Session</literal> is a transaction-level cache
     of persistent data. It is possible to configure a cluster or JVM-level
     (<literal>SessionFactory</literal>-level) cache on a class-by-class and
     collection-by-collection basis. You can even plug in a clustered cache. Be
     aware that caches are not aware of changes made to the persistent store by
     another application. They can, however, be configured to regularly expire
     cached data.</para>
 
     <para revision="1">You have the option to tell Hibernate which caching
     implementation to use by specifying the name of a class that implements
     <literal>org.hibernate.cache.spi.CacheProvider</literal> using the property
     <literal>hibernate.cache.provider_class</literal>. Hibernate is bundled
     with a number of built-in integrations with the open-source cache
     providers that are listed in <xref linkend="cacheproviders" />. You can
     also implement your own and plug it in as outlined above. Note that
     versions prior to Hibernate 3.2 use EhCache as the default cache
     provider.</para>
 
     <table frame="topbot" id="cacheproviders" revision="1">
       <title>Cache Providers</title>
 
       <tgroup align="left" cols="5" colsep="1" rowsep="1">
         <colspec colname="c1" colwidth="1*" />
 
         <colspec colname="c2" colwidth="3*" />
 
         <colspec colname="c3" colwidth="1*" />
 
         <colspec colname="c4" colwidth="1*" />
 
         <colspec colname="c5" colwidth="1*" />
 
         <thead>
           <row>
             <entry>Cache</entry>
 
             <entry>Provider class</entry>
 
             <entry>Type</entry>
 
             <entry>Cluster Safe</entry>
 
             <entry>Query Cache Supported</entry>
           </row>
         </thead>
 
         <tbody>
           <row>
-            <entry>Hashtable (not intended for production use)</entry>
+            <entry>ConcurrentHashMap (only for testing purpose, in hibernate-testing module)</entry>
 
-            <entry><literal>org.hibernate.cache.internal.HashtableCacheProvider</literal></entry>
+            <entry><literal>org.hibernate.testing.cache.CachingRegionFactory</literal></entry>
 
             <entry>memory</entry>
 
             <entry></entry>
 
             <entry>yes</entry>
           </row>
 
           <row>
             <entry>EHCache</entry>
 
-            <entry><literal>org.hibernate.cache.internal.EhCacheProvider</literal></entry>
+            <entry><literal>org.hibernate.cache.internal.EhCacheRegionFactory</literal></entry>
 
             <entry>memory, disk</entry>
 
             <entry></entry>
 
             <entry>yes</entry>
           </row>
 
-          <row>
+<!--          <row>
             <entry>OSCache</entry>
 
             <entry><literal>org.hibernate.cache.OSCacheProvider</literal></entry>
 
             <entry>memory, disk</entry>
 
             <entry></entry>
 
             <entry>yes</entry>
           </row>
 
           <row>
             <entry>SwarmCache</entry>
 
             <entry><literal>org.hibernate.cache.SwarmCacheProvider</literal></entry>
 
             <entry>clustered (ip multicast)</entry>
 
             <entry>yes (clustered invalidation)</entry>
 
             <entry></entry>
           </row>
 
           <row>
             <entry>JBoss Cache 1.x</entry>
 
             <entry><literal>org.hibernate.cache.TreeCacheProvider</literal></entry>
 
             <entry>clustered (ip multicast), transactional</entry>
 
             <entry>yes (replication)</entry>
 
             <entry>yes (clock sync req.)</entry>
           </row>
 
           <row>
             <entry>JBoss Cache 2</entry>
 
             <entry><literal>org.hibernate.cache.jbc.JBossCacheRegionFactory</literal></entry>
 
             <entry>clustered (ip multicast), transactional</entry>
 
             <entry>yes (replication or invalidation)</entry>
 
             <entry>yes (clock sync req.)</entry>
+          </row> -->
+          <row>
+            <entry>Infinispan</entry>
+
+            <entry><literal>org.hibernate.cache.infinispan.InfinispanRegionFactory</literal></entry>
+
+            <entry>clustered (ip multicast), transactional</entry>
+
+            <entry>yes (replication or invalidation)</entry>
+
+            <entry>yes (clock sync req.)</entry>
           </row>
         </tbody>
       </tgroup>
     </table>
 
     <section id="performance-cache-mapping" revision="2">
       <title>Cache mappings</title>
 
       <para>As we have done in previous chapters we are looking at the two
       different possibiltites to configure caching. First configuration via
       annotations and then via Hibernate mapping files.</para>
 
       <para>By default, entities are not part of the second level cache and we
       recommend you to stick to this setting. However, you can override this
       by setting the <literal>shared-cache-mode</literal> element in your
       <filename>persistence.xml</filename> file or by using the
       <literal>javax.persistence.sharedCache.mode </literal>property in your
       configuration. The following values are possible:</para>
 
       <itemizedlist>
         <listitem>
           <para><literal>ENABLE_SELECTIVE</literal> (Default and recommended
           value): entities are not cached unless explicitly marked as
           cacheable.</para>
         </listitem>
 
         <listitem>
           <para><literal>DISABLE_SELECTIVE</literal>: entities are cached
           unless explicitly marked as not cacheable.</para>
         </listitem>
 
         <listitem>
           <para><literal>ALL</literal>: all entities are always cached even if
           marked as non cacheable.</para>
         </listitem>
 
         <listitem>
           <para><literal>NONE</literal>: no entity are cached even if marked
           as cacheable. This option can make sense to disable second-level
           cache altogether.</para>
         </listitem>
       </itemizedlist>
 
       <para>The cache concurrency strategy used by default can be set globaly
       via the
       <literal>hibernate.cache.default_cache_concurrency_strategy</literal>
       configuration property. The values for this property are:</para>
 
       <itemizedlist>
         <listitem>
           <para><literal>read-only</literal></para>
         </listitem>
 
         <listitem>
           <para><literal>read-write</literal></para>
         </listitem>
 
         <listitem>
           <para><literal>nonstrict-read-write</literal></para>
         </listitem>
 
         <listitem>
           <para><literal>transactional</literal></para>
         </listitem>
       </itemizedlist>
 
       <note>
         <para>It is recommended to define the cache concurrency strategy per
         entity rather than using a global one. Use the
         <classname>@org.hibernate.annotations.Cache</classname> annotation for
         that.</para>
       </note>
 
       <example id="example-cache-concurrency-with-cache-annotation">
         <title>Definition of cache concurrency strategy via
         <classname>@Cache</classname></title>
 
         <programlisting language="JAVA" role="JAVA">@Entity 
 @Cacheable
 @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
 public class Forest { ... }</programlisting>
       </example>
 
       <para>Hibernate also let's you cache the content of a collection or the
       identifiers if the collection contains other entities. Use the
       <classname>@Cache</classname> annotation on the collection
       property.</para>
 
       <example>
         <title>Caching collections using annotations</title>
 
         <programlisting language="JAVA" role="JAVA">@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
 @JoinColumn(name="CUST_ID")
 @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
 public SortedSet&lt;Ticket&gt; getTickets() {
     return tickets;
 }</programlisting>
       </example>
 
       <para><xref linkend="example-cache-annotation-with-attributes" />shows
       the<literal> @org.hibernate.annotations.Cache</literal> annotations with
       its attributes. It allows you to define the caching strategy and region
       of a given second level cache.</para>
 
       <example id="example-cache-annotation-with-attributes">
         <title><classname>@Cache</classname> annotation with
         attributes</title>
 
         <programlistingco>
           <areaspec>
             <area coords="2" id="cache-hm1" />
 
             <area coords="3" id="cache-hm2" />
 
             <area coords="4" id="cache-hm3" />
           </areaspec>
 
           <programlisting>@Cache(
     CacheConcurrencyStrategy usage();
     String region() default "";
     String include() default "all";
 )</programlisting>
 
           <calloutlist>
             <callout arearefs="cache-hm1">
               <para>usage: the given cache concurrency strategy (NONE,
               READ_ONLY, NONSTRICT_READ_WRITE, READ_WRITE,
               TRANSACTIONAL)</para>
             </callout>
 
             <callout arearefs="cache-hm2">
               <para>region (optional): the cache region (default to the fqcn
               of the class or the fq role name of the collection)</para>
             </callout>
 
             <callout arearefs="cache-hm3">
               <para><literal>include</literal> (optional): all to include all
               properties, non-lazy to only include non lazy properties
               (default all).</para>
             </callout>
           </calloutlist>
         </programlistingco>
       </example>
 
       <para>Let's now take a look at Hibernate mapping files. There the
       <literal>&lt;cache&gt;</literal> element of a class or collection
       mapping is used to configure the second level cache. Looking at <xref
       linkend="example-hibernate-cache-mapping-element" /> the parallels to
       anotations is obvious.</para>
 
       <example id="example-hibernate-cache-mapping-element">
         <title>The Hibernate <literal>&lt;cache&gt;</literal> mapping
         element</title>
 
         <programlistingco>
           <areaspec>
             <area coords="2" id="cache1" />
 
             <area coords="3" id="cache2" />
 
             <area coords="4" id="cache3" />
           </areaspec>
 
           <programlisting>&lt;cache
     usage="transactional|read-write|nonstrict-read-write|read-only"
     region="RegionName"
     include="all|non-lazy"
 /&gt;</programlisting>
 
           <calloutlist>
             <callout arearefs="cache1">
               <para><literal>usage</literal> (required) specifies the caching
               strategy: <literal>transactional</literal>,
               <literal>read-write</literal>,
               <literal>nonstrict-read-write</literal> or
               <literal>read-only</literal></para>
             </callout>
 
             <callout arearefs="cache2">
               <para><literal>region</literal> (optional: defaults to the class
               or collection role name): specifies the name of the second level
               cache region</para>
             </callout>
 
             <callout arearefs="cache3">
               <para><literal>include</literal> (optional: defaults to
               <literal>all</literal>) <literal>non-lazy</literal>: specifies
               that properties of the entity mapped with
               <literal>lazy="true"</literal> cannot be cached when
               attribute-level lazy fetching is enabled</para>
             </callout>
           </calloutlist>
         </programlistingco>
       </example>
 
       <para>Alternatively to <literal>&lt;cache&gt;</literal>, you can use
       <literal>&lt;class-cache&gt;</literal> and
       <literal>&lt;collection-cache&gt;</literal> elements in
       <literal>hibernate.cfg.xml</literal>.</para>
 
       <para>Let's now have a closer look at the different usage
       strategies</para>
     </section>
 
     <section id="performance-cache-readonly">
       <title>Strategy: read only</title>
 
       <para>If your application needs to read, but not modify, instances of a
       persistent class, a <literal>read-only</literal> cache can be used. This
       is the simplest and optimal performing strategy. It is even safe for use
       in a cluster.</para>
     </section>
 
     <section id="performance-cache-readwrite">
       <title>Strategy: read/write</title>
 
       <para>If the application needs to update data, a
       <literal>read-write</literal> cache might be appropriate. This cache
       strategy should never be used if serializable transaction isolation
       level is required. If the cache is used in a JTA environment, you must
       specify the property
       <literal>hibernate.transaction.manager_lookup_class</literal> and naming
       a strategy for obtaining the JTA <literal>TransactionManager</literal>.
       In other environments, you should ensure that the transaction is
       completed when <literal>Session.close()</literal> or
       <literal>Session.disconnect()</literal> is called. If you want to use
       this strategy in a cluster, you should ensure that the underlying cache
       implementation supports locking. The built-in cache providers
       <emphasis>do not</emphasis> support locking.</para>
     </section>
 
     <section id="performance-cache-nonstrict">
       <title>Strategy: nonstrict read/write</title>
 
       <para>If the application only occasionally needs to update data (i.e. if
       it is extremely unlikely that two transactions would try to update the
       same item simultaneously), and strict transaction isolation is not
       required, a <literal>nonstrict-read-write</literal> cache might be
       appropriate. If the cache is used in a JTA environment, you must specify
       <literal>hibernate.transaction.manager_lookup_class</literal>. In other
       environments, you should ensure that the transaction is completed when
       <literal>Session.close()</literal> or
       <literal>Session.disconnect()</literal> is called.</para>
     </section>
 
     <section id="performance-cache-transactional">
       <title>Strategy: transactional</title>
 
       <para>The <literal>transactional</literal> cache strategy provides
       support for fully transactional cache providers such as JBoss TreeCache.
       Such a cache can only be used in a JTA environment and you must specify
       <literal>hibernate.transaction.manager_lookup_class</literal>.</para>
     </section>
 
     <section id="performance-cache-compat-matrix">
       <title>Cache-provider/concurrency-strategy compatibility</title>
 
       <important>
         <para>None of the cache providers support all of the cache concurrency
         strategies.</para>
       </important>
 
       <para>The following table shows which providers are compatible with
       which concurrency strategies.</para>
 
       <table frame="topbot">
         <title>Cache Concurrency Strategy Support</title>
 
         <tgroup align="left" cols="5" colsep="1" rowsep="1">
           <colspec colname="c1" colwidth="1*" />
 
           <colspec colname="c2" colwidth="1*" />
 
           <colspec colname="c3" colwidth="1*" />
 
           <colspec colname="c4" colwidth="1*" />
 
           <colspec colname="c5" colwidth="1*" />
 
           <thead>
             <row>
               <entry>Cache</entry>
 
               <entry>read-only</entry>
 
               <entry>nonstrict-read-write</entry>
 
               <entry>read-write</entry>
 
               <entry>transactional</entry>
             </row>
           </thead>
 
           <tbody>
             <row>
-              <entry>Hashtable (not intended for production use)</entry>
+              <entry>ConcurrentHashMap (not intended for production use)</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
               <entry></entry>
             </row>
 
             <row>
               <entry>EHCache</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
-              <entry></entry>
+              <entry>yes</entry>
             </row>
 
-            <row>
+ <!--           <row>
               <entry>OSCache</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
               <entry></entry>
             </row>
 
             <row>
               <entry>SwarmCache</entry>
 
               <entry>yes</entry>
 
               <entry>yes</entry>
 
               <entry></entry>
 
               <entry></entry>
             </row>
 
             <row>
               <entry>JBoss Cache 1.x</entry>
 
               <entry>yes</entry>
 
               <entry></entry>
 
               <entry></entry>
 
               <entry>yes</entry>
             </row>
 
             <row>
               <entry>JBoss Cache 2</entry>
 
               <entry>yes</entry>
 
               <entry></entry>
 
               <entry></entry>
 
               <entry>yes</entry>
+            </row> -->
+            <row>
+              <entry>Infinispan</entry>
+
+              <entry>yes</entry>
+
+              <entry></entry>
+
+              <entry></entry>
+
+              <entry>yes</entry>
             </row>
           </tbody>
         </tgroup>
       </table>
     </section>
   </section>
 
   <section id="performance-sessioncache" revision="2">
     <title>Managing the caches</title>
 
     <para>Whenever you pass an object to <literal>save()</literal>,
     <literal>update()</literal> or <literal>saveOrUpdate()</literal>, and
     whenever you retrieve an object using <literal>load()</literal>,
     <literal>get()</literal>, <literal>list()</literal>,
     <literal>iterate()</literal> or <literal>scroll()</literal>, that object
     is added to the internal cache of the <literal>Session</literal>.</para>
 
     <para>When <literal>flush()</literal> is subsequently called, the state of
     that object will be synchronized with the database. If you do not want
     this synchronization to occur, or if you are processing a huge number of
     objects and need to manage memory efficiently, the
     <literal>evict()</literal> method can be used to remove the object and its
     collections from the first-level cache.</para>
 
     <example>
       <title>Explcitly evicting a cached instance from the first level cache
       using <methodname>Session.evict()</methodname></title>
 
       <programlisting role="JAVA">ScrollableResult cats = sess.createQuery("from Cat as cat").scroll(); //a huge result set
 while ( cats.next() ) {
     Cat cat = (Cat) cats.get(0);
     doSomethingWithACat(cat);
     sess.evict(cat);
 }</programlisting>
     </example>
 
     <para>The <literal>Session</literal> also provides a
     <literal>contains()</literal> method to determine if an instance belongs
     to the session cache.</para>
 
     <para>To evict all objects from the session cache, call
     <literal>Session.clear()</literal></para>
 
     <para>For the second-level cache, there are methods defined on
     <literal>SessionFactory</literal> for evicting the cached state of an
     instance, entire class, collection instance or entire collection
     role.</para>
 
     <example>
       <title>Second-level cache eviction via
       <methodname>SessionFactoty.evict() </methodname>and
       <methodname>SessionFacyory.evictCollection()</methodname></title>
 
       <programlisting role="JAVA">sessionFactory.evict(Cat.class, catId); //evict a particular Cat
 sessionFactory.evict(Cat.class);  //evict all Cats
 sessionFactory.evictCollection("Cat.kittens", catId); //evict a particular collection of kittens
 sessionFactory.evictCollection("Cat.kittens"); //evict all kitten collections</programlisting>
     </example>
 
     <para>The <literal>CacheMode</literal> controls how a particular session
     interacts with the second-level cache:</para>
 
     <itemizedlist>
       <listitem>
         <para><literal>CacheMode.NORMAL</literal>: will read items from and
         write items to the second-level cache</para>
       </listitem>
 
       <listitem>
         <para><literal>CacheMode.GET</literal>: will read items from the
         second-level cache. Do not write to the second-level cache except when
         updating data</para>
       </listitem>
 
       <listitem>
         <para><literal>CacheMode.PUT</literal>: will write items to the
         second-level cache. Do not read from the second-level cache</para>
       </listitem>
 
       <listitem>
         <para><literal>CacheMode.REFRESH</literal>: will write items to the
         second-level cache. Do not read from the second-level cache. Bypass
         the effect of <literal>hibernate.cache.use_minimal_puts</literal>
         forcing a refresh of the second-level cache for all items read from
         the database</para>
       </listitem>
     </itemizedlist>
 
     <para>To browse the contents of a second-level or query cache region, use
     the <literal>Statistics</literal> API:</para>
 
     <example>
       <title>Browsing the second-level cache entries via the
       <classname>Statistics</classname> API</title>
 
       <programlisting role="JAVA">Map cacheEntries = sessionFactory.getStatistics()
         .getSecondLevelCacheStatistics(regionName)
         .getEntries();</programlisting>
     </example>
 
     <para>You will need to enable statistics and, optionally, force Hibernate
     to keep the cache entries in a more readable format:</para>
 
     <example>
       <title>Enabling Hibernate statistics</title>
 
       <programlisting>hibernate.generate_statistics true
 hibernate.cache.use_structured_entries true</programlisting>
     </example>
   </section>
 
   <section id="performance-querycache" revision="1">
     <title>The Query Cache</title>
 
     <para>Query result sets can also be cached. This is only useful for
     queries that are run frequently with the same parameters.</para>
 
     <section id="performance-querycache-enable">
       <title>Enabling query caching</title>
 
       <para>Caching of query results introduces some overhead in terms of your
       applications normal transactional processing. For example, if you cache
       results of a query against Person Hibernate will need to keep track of
       when those results should be invalidated because changes have been
       committed against Person. That, coupled with the fact that most
       applications simply gain no benefit from caching query results, leads
       Hibernate to disable caching of query results by default. To use query
       caching, you will first need to enable the query cache:</para>
 
       <programlisting>hibernate.cache.use_query_cache true</programlisting>
 
       <para>This setting creates two new cache regions: <itemizedlist>
           <listitem>
             <para><classname>org.hibernate.cache.internal.StandardQueryCache</classname>,
             holding the cached query results</para>
           </listitem>
 
           <listitem>
             <para><classname>org.hibernate.cache.spi.UpdateTimestampsCache</classname>,
             holding timestamps of the most recent updates to queryable tables.
             These are used to validate the results as they are served from the
             query cache.</para>
           </listitem>
         </itemizedlist></para>
 
       <important>
         <para>If you configure your underlying cache implementation to use
         expiry or timeouts is very important that the cache timeout of the
         underlying cache region for the UpdateTimestampsCache be set to a
         higher value than the timeouts of any of the query caches. In fact, we
         recommend that the the UpdateTimestampsCache region not be configured
         for expiry at all. Note, in particular, that an LRU cache expiry
         policy is never appropriate.</para>
       </important>
 
       <para>As mentioned above, most queries do not benefit from caching or
       their results. So by default, individual queries are not cached even
       after enabling query caching. To enable results caching for a particular
       query, call <literal>org.hibernate.Query.setCacheable(true)</literal>.
       This call allows the query to look for existing cache results or add its
       results to the cache when it is executed.</para>
 
       <note>
         <para>The query cache does not cache the state of the actual entities
         in the cache; it caches only identifier values and results of value
         type. For this reaso, the query cache should always be used in
         conjunction with the second-level cache for those entities expected to
         be cached as part of a query result cache (just as with collection
         caching).</para>
       </note>
     </section>
 
     <section id="performance-querycache-regions">
       <title>Query cache regions</title>
 
       <para>If you require fine-grained control over query cache expiration
       policies, you can specify a named cache region for a particular query by
       calling <literal>Query.setCacheRegion()</literal>.</para>
 
       <programlisting role="JAVA">List blogs = sess.createQuery("from Blog blog where blog.blogger = :blogger")
         .setEntity("blogger", blogger)
         .setMaxResults(15)
         .setCacheable(true)
         .setCacheRegion("frontpages")
         .list();</programlisting>
 
       <para>If you want to force the query cache to refresh one of its regions
       (disregard any cached results it finds there) you can use
       <literal>org.hibernate.Query.setCacheMode(CacheMode.REFRESH)</literal>.
       In conjunction with the region you have defined for the given query,
       Hibernate will selectively force the results cached in that particular
       region to be refreshed. This is particularly useful in cases where
       underlying data may have been updated via a separate process and is a
       far more efficient alternative to bulk eviction of the region via
       <literal>org.hibernate.SessionFactory.evictQueries()</literal>.</para>
     </section>
   </section>
 
   <section id="performance-collections">
     <title>Understanding Collection performance</title>
 
     <para>In the previous sections we have covered collections and their
     applications. In this section we explore some more issues in relation to
     collections at runtime.</para>
 
     <section id="performance-collections-taxonomy">
       <title>Taxonomy</title>
 
       <para>Hibernate defines three basic kinds of collections:</para>
 
       <itemizedlist>
         <listitem>
           <para>collections of values</para>
         </listitem>
 
         <listitem>
           <para>one-to-many associations</para>
         </listitem>
 
         <listitem>
           <para>many-to-many associations</para>
         </listitem>
       </itemizedlist>
 
       <para>This classification distinguishes the various table and foreign
       key relationships but does not tell us quite everything we need to know
       about the relational model. To fully understand the relational structure
       and performance characteristics, we must also consider the structure of
       the primary key that is used by Hibernate to update or delete collection
       rows. This suggests the following classification:</para>
 
       <itemizedlist>
         <listitem>
           <para>indexed collections</para>
         </listitem>
 
         <listitem>
           <para>sets</para>
         </listitem>
 
         <listitem>
           <para>bags</para>
         </listitem>
       </itemizedlist>
 
       <para>All indexed collections (maps, lists, and arrays) have a primary
       key consisting of the <literal>&lt;key&gt;</literal> and
       <literal>&lt;index&gt;</literal> columns. In this case, collection
       updates are extremely efficient. The primary key can be efficiently
       indexed and a particular row can be efficiently located when Hibernate
       tries to update or delete it.</para>
 
       <para>Sets have a primary key consisting of
       <literal>&lt;key&gt;</literal> and element columns. This can be less
       efficient for some types of collection element, particularly composite
       elements or large text or binary fields, as the database may not be able
       to index a complex primary key as efficiently. However, for one-to-many
       or many-to-many associations, particularly in the case of synthetic
       identifiers, it is likely to be just as efficient. If you want
       <literal>SchemaExport</literal> to actually create the primary key of a
       <literal>&lt;set&gt;</literal>, you must declare all columns as
       <literal>not-null="true"</literal>.</para>
 
       <para><literal>&lt;idbag&gt;</literal> mappings define a surrogate key,
       so they are efficient to update. In fact, they are the best case.</para>
 
       <para>Bags are the worst case since they permit duplicate element values
       and, as they have no index column, no primary key can be defined.
       Hibernate has no way of distinguishing between duplicate rows. Hibernate
       resolves this problem by completely removing in a single
       <literal>DELETE</literal> and recreating the collection whenever it
       changes. This can be inefficient.</para>
 
       <para>For a one-to-many association, the "primary key" may not be the
       physical primary key of the database table. Even in this case, the above
       classification is still useful. It reflects how Hibernate "locates"
       individual rows of the collection.</para>
     </section>
 
     <section id="performance-collections-mostefficientupdate">
       <title>Lists, maps, idbags and sets are the most efficient collections
       to update</title>
 
       <para>From the discussion above, it should be clear that indexed
       collections and sets allow the most efficient operation in terms of
       adding, removing and updating elements.</para>
 
       <para>There is, arguably, one more advantage that indexed collections
       have over sets for many-to-many associations or collections of values.
       Because of the structure of a <literal>Set</literal>, Hibernate does not
       <literal>UPDATE</literal> a row when an element is "changed". Changes to
       a <literal>Set</literal> always work via <literal>INSERT</literal> and
       <literal>DELETE</literal> of individual rows. Once again, this
       consideration does not apply to one-to-many associations.</para>
 
       <para>After observing that arrays cannot be lazy, you can conclude that
       lists, maps and idbags are the most performant (non-inverse) collection
       types, with sets not far behind. You can expect sets to be the most
       common kind of collection in Hibernate applications. This is because the
       "set" semantics are most natural in the relational model.</para>
 
       <para>However, in well-designed Hibernate domain models, most
       collections are in fact one-to-many associations with
       <literal>inverse="true"</literal>. For these associations, the update is
       handled by the many-to-one end of the association, and so considerations
       of collection update performance simply do not apply.</para>
     </section>
 
     <section id="performance-collections-mostefficentinverse">
       <title>Bags and lists are the most efficient inverse collections</title>
 
       <para>There is a particular case, however, in which bags, and also
       lists, are much more performant than sets. For a collection with
       <literal>inverse="true"</literal>, the standard bidirectional
       one-to-many relationship idiom, for example, we can add elements to a
       bag or list without needing to initialize (fetch) the bag elements. This
       is because, unlike a <literal>set</literal>,
       <literal>Collection.add()</literal> or
       <literal>Collection.addAll()</literal> must always return true for a bag
       or <literal>List</literal>. This can make the following common code much
       faster:</para>
 
       <programlisting role="JAVA">Parent p = (Parent) sess.load(Parent.class, id);
 Child c = new Child();
 c.setParent(p);
 p.getChildren().add(c);  //no need to fetch the collection!
 sess.flush();</programlisting>
     </section>
 
     <section id="performance-collections-oneshotdelete">
       <title>One shot delete</title>
 
       <para>Deleting collection elements one by one can sometimes be extremely
       inefficient. Hibernate knows not to do that in the case of an
       newly-empty collection (if you called <literal>list.clear()</literal>,
       for example). In this case, Hibernate will issue a single
       <literal>DELETE</literal>.</para>
 
       <para>Suppose you added a single element to a collection of size twenty
       and then remove two elements. Hibernate will issue one
       <literal>INSERT</literal> statement and two <literal>DELETE</literal>
       statements, unless the collection is a bag. This is certainly
       desirable.</para>
 
       <para>However, suppose that we remove eighteen elements, leaving two and
       then add thee new elements. There are two possible ways to
       proceed</para>
 
       <itemizedlist>
         <listitem>
           <para>delete eighteen rows one by one and then insert three
           rows</para>
         </listitem>
 
         <listitem>
           <para>remove the whole collection in one SQL
           <literal>DELETE</literal> and insert all five current elements one
           by one</para>
         </listitem>
       </itemizedlist>
 
       <para>Hibernate cannot know that the second option is probably quicker.
       It would probably be undesirable for Hibernate to be that intuitive as
       such behavior might confuse database triggers, etc.</para>
 
       <para>Fortunately, you can force this behavior (i.e. the second
       strategy) at any time by discarding (i.e. dereferencing) the original
       collection and returning a newly instantiated collection with all the
       current elements.</para>
 
       <para>One-shot-delete does not apply to collections mapped
       <literal>inverse="true"</literal>.</para>
     </section>
   </section>
 
   <section id="performance-monitoring" revision="1">
     <title>Monitoring performance</title>
 
     <para>Optimization is not much use without monitoring and access to
     performance numbers. Hibernate provides a full range of figures about its
     internal operations. Statistics in Hibernate are available per
     <literal>SessionFactory</literal>.</para>
 
     <section id="performance-monitoring-sf" revision="2">
       <title>Monitoring a SessionFactory</title>
 
       <para>You can access <literal>SessionFactory</literal> metrics in two
       ways. Your first option is to call
       <literal>sessionFactory.getStatistics()</literal> and read or display
       the <literal>Statistics</literal> yourself.</para>
 
       <para>Hibernate can also use JMX to publish metrics if you enable the
       <literal>StatisticsService</literal> MBean. You can enable a single
       MBean for all your <literal>SessionFactory</literal> or one per factory.
       See the following code for minimalistic configuration examples:</para>
 
       <programlisting role="JAVA">// MBean service registration for a specific SessionFactory
 Hashtable tb = new Hashtable();
 tb.put("type", "statistics");
 tb.put("sessionFactory", "myFinancialApp");
 ObjectName on = new ObjectName("hibernate", tb); // MBean object name
 
 StatisticsService stats = new StatisticsService(); // MBean implementation
 stats.setSessionFactory(sessionFactory); // Bind the stats to a SessionFactory
 server.registerMBean(stats, on); // Register the Mbean on the server</programlisting>
 
       <programlisting role="JAVA">// MBean service registration for all SessionFactory's
 Hashtable tb = new Hashtable();
 tb.put("type", "statistics");
 tb.put("sessionFactory", "all");
 ObjectName on = new ObjectName("hibernate", tb); // MBean object name
 
 StatisticsService stats = new StatisticsService(); // MBean implementation
 server.registerMBean(stats, on); // Register the MBean on the server</programlisting>
 
       <para>You can activate and deactivate the monitoring for a
       <literal>SessionFactory</literal>:</para>
 
       <itemizedlist>
         <listitem>
           <para>at configuration time, set
           <literal>hibernate.generate_statistics</literal> to
           <literal>false</literal></para>
         </listitem>
       </itemizedlist>
 
       <itemizedlist>
         <listitem>
           <para>at runtime:
           <literal>sf.getStatistics().setStatisticsEnabled(true)</literal> or
           <literal>hibernateStatsBean.setStatisticsEnabled(true)</literal></para>
         </listitem>
       </itemizedlist>
 
       <para>Statistics can be reset programmatically using the
       <literal>clear()</literal> method. A summary can be sent to a logger
       (info level) using the <literal>logSummary()</literal> method.</para>
     </section>
 
     <section id="performance-monitoring-metrics" revision="1">
       <title>Metrics</title>
 
       <para>Hibernate provides a number of metrics, from basic information to
       more specialized information that is only relevant in certain scenarios.
       All available counters are described in the
       <literal>Statistics</literal> interface API, in three categories:</para>
 
       <itemizedlist>
         <listitem>
           <para>Metrics related to the general <literal>Session</literal>
           usage, such as number of open sessions, retrieved JDBC connections,
           etc.</para>
         </listitem>
 
         <listitem>
           <para>Metrics related to the entities, collections, queries, and
           caches as a whole (aka global metrics).</para>
         </listitem>
 
         <listitem>
           <para>Detailed metrics related to a particular entity, collection,
           query or cache region.</para>
         </listitem>
       </itemizedlist>
 
       <para>For example, you can check the cache hit, miss, and put ratio of
       entities, collections and queries, and the average time a query needs.
       Be aware that the number of milliseconds is subject to approximation in
       Java. Hibernate is tied to the JVM precision and on some platforms this
       might only be accurate to 10 seconds.</para>
 
       <para>Simple getters are used to access the global metrics (i.e. not
       tied to a particular entity, collection, cache region, etc.). You can
       access the metrics of a particular entity, collection or cache region
       through its name, and through its HQL or SQL representation for queries.
       Please refer to the <literal>Statistics</literal>,
       <literal>EntityStatistics</literal>,
       <literal>CollectionStatistics</literal>,
       <literal>SecondLevelCacheStatistics</literal>, and
       <literal>QueryStatistics</literal> API Javadoc for more information. The
       following code is a simple example:</para>
 
       <programlisting role="JAVA">Statistics stats = HibernateUtil.sessionFactory.getStatistics();
 
 double queryCacheHitCount  = stats.getQueryCacheHitCount();
 double queryCacheMissCount = stats.getQueryCacheMissCount();
 double queryCacheHitRatio =
   queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount);
 
 log.info("Query Hit ratio:" + queryCacheHitRatio);
 
 EntityStatistics entityStats =
   stats.getEntityStatistics( Cat.class.getName() );
 long changes =
         entityStats.getInsertCount()
         + entityStats.getUpdateCount()
         + entityStats.getDeleteCount();
 log.info(Cat.class.getName() + " changed " + changes + "times"  );</programlisting>
 
       <para>You can work on all entities, collections, queries and region
       caches, by retrieving the list of names of entities, collections,
       queries and region caches using the following methods:
       <literal>getQueries()</literal>, <literal>getEntityNames()</literal>,
       <literal>getCollectionRoleNames()</literal>, and
       <literal>getSecondLevelCacheRegionNames()</literal>.</para>
     </section>
   </section>
 </chapter>
diff --git a/etc/hibernate.properties b/etc/hibernate.properties
index 668ea03a45..7609848e7b 100644
--- a/etc/hibernate.properties
+++ b/etc/hibernate.properties
@@ -1,529 +1,527 @@
 ######################
 ### Query Language ###
 ######################
 
 ## define query language constants / function names
 
 hibernate.query.substitutions yes 'Y', no 'N'
 
 
 ## select the classic query parser
 
 #hibernate.query.factory_class org.hibernate.hql.internal.classic.ClassicQueryTranslatorFactory
 
 
 
 #################
 ### Platforms ###
 #################
 
 ## JNDI Datasource
 
 #hibernate.connection.datasource jdbc/test
 #hibernate.connection.username db2
 #hibernate.connection.password db2
 
 
 ## HypersonicSQL
 
 hibernate.dialect org.hibernate.dialect.HSQLDialect
 hibernate.connection.driver_class org.hsqldb.jdbcDriver
 hibernate.connection.username sa
 hibernate.connection.password
 hibernate.connection.url jdbc:hsqldb:./build/db/hsqldb/hibernate
 #hibernate.connection.url jdbc:hsqldb:hsql://localhost
 #hibernate.connection.url jdbc:hsqldb:test
 
 ## H2 (www.h2database.com)
 #hibernate.dialect org.hibernate.dialect.H2Dialect
 #hibernate.connection.driver_class org.h2.Driver
 #hibernate.connection.username sa
 #hibernate.connection.password
 #hibernate.connection.url jdbc:h2:mem:./build/db/h2/hibernate
 #hibernate.connection.url jdbc:h2:testdb/h2test
 #hibernate.connection.url jdbc:h2:mem:imdb1
 #hibernate.connection.url jdbc:h2:tcp://dbserv:8084/sample; 	
 #hibernate.connection.url jdbc:h2:ssl://secureserv:8085/sample; 	
 #hibernate.connection.url jdbc:h2:ssl://secureserv/testdb;cipher=AES
 
 ## MySQL
 
 #hibernate.dialect org.hibernate.dialect.MySQLDialect
 #hibernate.dialect org.hibernate.dialect.MySQLInnoDBDialect
 #hibernate.dialect org.hibernate.dialect.MySQLMyISAMDialect
 #hibernate.connection.driver_class com.mysql.jdbc.Driver
 #hibernate.connection.url jdbc:mysql:///test
 #hibernate.connection.username gavin
 #hibernate.connection.password
 
 
 ## Oracle
 
 #hibernate.dialect org.hibernate.dialect.Oracle8iDialect
 #hibernate.dialect org.hibernate.dialect.Oracle9iDialect
 #hibernate.dialect org.hibernate.dialect.Oracle10gDialect
 #hibernate.connection.driver_class oracle.jdbc.driver.OracleDriver
 #hibernate.connection.username ora
 #hibernate.connection.password ora
 #hibernate.connection.url jdbc:oracle:thin:@localhost:1521:orcl
 #hibernate.connection.url jdbc:oracle:thin:@localhost:1522:XE
 
 
 ## PostgreSQL
 
 #hibernate.dialect org.hibernate.dialect.PostgreSQLDialect
 #hibernate.connection.driver_class org.postgresql.Driver
 #hibernate.connection.url jdbc:postgresql:template1
 #hibernate.connection.username pg
 #hibernate.connection.password
 
 
 ## DB2
 
 #hibernate.dialect org.hibernate.dialect.DB2Dialect
 #hibernate.connection.driver_class com.ibm.db2.jcc.DB2Driver
 #hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver
 #hibernate.connection.url jdbc:db2://localhost:50000/somename
 #hibernate.connection.url jdbc:db2:somename
 #hibernate.connection.username db2
 #hibernate.connection.password db2
 
 ## TimesTen
 
 #hibernate.dialect org.hibernate.dialect.TimesTenDialect
 #hibernate.connection.driver_class com.timesten.jdbc.TimesTenDriver
 #hibernate.connection.url jdbc:timesten:direct:test
 #hibernate.connection.username
 #hibernate.connection.password 
 
 ## DB2/400
 
 #hibernate.dialect org.hibernate.dialect.DB2400Dialect
 #hibernate.connection.username user
 #hibernate.connection.password password
 
 ## Native driver
 #hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver
 #hibernate.connection.url jdbc:db2://systemname
 
 ## Toolbox driver
 #hibernate.connection.driver_class com.ibm.as400.access.AS400JDBCDriver
 #hibernate.connection.url jdbc:as400://systemname
 
 
 ## Derby (not supported!)
 
 #hibernate.dialect org.hibernate.dialect.DerbyDialect
 #hibernate.connection.driver_class org.apache.derby.jdbc.EmbeddedDriver
 #hibernate.connection.username
 #hibernate.connection.password
 #hibernate.connection.url jdbc:derby:build/db/derby/hibernate;create=true
 
 
 ## Sybase
 
 #hibernate.dialect org.hibernate.dialect.SybaseDialect
 #hibernate.connection.driver_class com.sybase.jdbc2.jdbc.SybDriver
 #hibernate.connection.username sa
 #hibernate.connection.password sasasa
 #hibernate.connection.url jdbc:sybase:Tds:co3061835-a:5000/tempdb
 
 
 ## Mckoi SQL
 
 #hibernate.dialect org.hibernate.dialect.MckoiDialect
 #hibernate.connection.driver_class com.mckoi.JDBCDriver
 #hibernate.connection.url jdbc:mckoi:///
 #hibernate.connection.url jdbc:mckoi:local://C:/mckoi1.0.3/db.conf
 #hibernate.connection.username admin
 #hibernate.connection.password nimda
 
 
 ## SAP DB
 
 #hibernate.dialect org.hibernate.dialect.SAPDBDialect
 #hibernate.connection.driver_class com.sap.dbtech.jdbc.DriverSapDB
 #hibernate.connection.url jdbc:sapdb://localhost/TST
 #hibernate.connection.username TEST
 #hibernate.connection.password TEST
 #hibernate.query.substitutions yes 'Y', no 'N'
 
 
 ## MS SQL Server
 
 #hibernate.dialect org.hibernate.dialect.SQLServerDialect
 #hibernate.connection.username sa
 #hibernate.connection.password sa
 
 ## JSQL Driver
 #hibernate.connection.driver_class com.jnetdirect.jsql.JSQLDriver
 #hibernate.connection.url jdbc:JSQLConnect://1E1/test
 
 ## JTURBO Driver
 #hibernate.connection.driver_class com.newatlanta.jturbo.driver.Driver
 #hibernate.connection.url jdbc:JTurbo://1E1:1433/test
 
 ## WebLogic Driver
 #hibernate.connection.driver_class weblogic.jdbc.mssqlserver4.Driver
 #hibernate.connection.url jdbc:weblogic:mssqlserver4:1E1:1433
 
 ## Microsoft Driver (not recommended!)
 #hibernate.connection.driver_class com.microsoft.jdbc.sqlserver.SQLServerDriver
 #hibernate.connection.url jdbc:microsoft:sqlserver://1E1;DatabaseName=test;SelectMethod=cursor
 
 ## The New Microsoft Driver 
 #hibernate.connection.driver_class com.microsoft.sqlserver.jdbc.SQLServerDriver
 #hibernate.connection.url jdbc:sqlserver://localhost
 
 ## jTDS (since version 0.9)
 #hibernate.connection.driver_class net.sourceforge.jtds.jdbc.Driver
 #hibernate.connection.url jdbc:jtds:sqlserver://1E1/test
 
 ## Interbase
 
 #hibernate.dialect org.hibernate.dialect.InterbaseDialect
 #hibernate.connection.username sysdba
 #hibernate.connection.password masterkey
 
 ## DO NOT specify hibernate.connection.sqlDialect
 
 ## InterClient
 
 #hibernate.connection.driver_class interbase.interclient.Driver
 #hibernate.connection.url jdbc:interbase://localhost:3060/C:/firebird/test.gdb
 
 ## Pure Java
 
 #hibernate.connection.driver_class org.firebirdsql.jdbc.FBDriver
 #hibernate.connection.url jdbc:firebirdsql:localhost/3050:/firebird/test.gdb
 
 
 ## Pointbase
 
 #hibernate.dialect org.hibernate.dialect.PointbaseDialect
 #hibernate.connection.driver_class com.pointbase.jdbc.jdbcUniversalDriver
 #hibernate.connection.url jdbc:pointbase:embedded:sample
 #hibernate.connection.username PBPUBLIC
 #hibernate.connection.password PBPUBLIC
 
 
 ## Ingres
 
 ## older versions (before Ingress 2006)
 
 #hibernate.dialect org.hibernate.dialect.IngresDialect
 #hibernate.connection.driver_class ca.edbc.jdbc.EdbcDriver
 #hibernate.connection.url jdbc:edbc://localhost:II7/database
 #hibernate.connection.username user
 #hibernate.connection.password password
 
 ## Ingres 2006 or later
 
 #hibernate.dialect org.hibernate.dialect.IngresDialect
 #hibernate.connection.driver_class com.ingres.jdbc.IngresDriver
 #hibernate.connection.url jdbc:ingres://localhost:II7/database;CURSOR=READONLY;auto=multi
 #hibernate.connection.username user
 #hibernate.connection.password password
 
 ## Mimer SQL
 
 #hibernate.dialect org.hibernate.dialect.MimerSQLDialect
 #hibernate.connection.driver_class com.mimer.jdbc.Driver
 #hibernate.connection.url jdbc:mimer:multi1
 #hibernate.connection.username hibernate
 #hibernate.connection.password hibernate
 
 
 ## InterSystems Cache
 
 #hibernate.dialect org.hibernate.dialect.Cache71Dialect
 #hibernate.connection.driver_class com.intersys.jdbc.CacheDriver
 #hibernate.connection.username _SYSTEM
 #hibernate.connection.password SYS
 #hibernate.connection.url jdbc:Cache://127.0.0.1:1972/HIBERNATE
 
 
 #################################
 ### Hibernate Connection Pool ###
 #################################
 
 hibernate.connection.pool_size 1
 
 
 
 ###########################
 ### C3P0 Connection Pool###
 ###########################
 
 #hibernate.c3p0.max_size 2
 #hibernate.c3p0.min_size 2
 #hibernate.c3p0.timeout 5000
 #hibernate.c3p0.max_statements 100
 #hibernate.c3p0.idle_test_period 3000
 #hibernate.c3p0.acquire_increment 2
 #hibernate.c3p0.validate false
 
 
 
 ##############################
 ### Proxool Connection Pool###
 ##############################
 
 ## Properties for external configuration of Proxool
 
 hibernate.proxool.pool_alias pool1
 
 ## Only need one of the following
 
 #hibernate.proxool.existing_pool true
 #hibernate.proxool.xml proxool.xml
 #hibernate.proxool.properties proxool.properties
 
 
 
 #################################
 ### Plugin ConnectionProvider ###
 #################################
 
 ## use a custom ConnectionProvider (if not set, Hibernate will choose a built-in ConnectionProvider using hueristics)
 
 #hibernate.connection.provider_class org.hibernate.connection.DriverManagerConnectionProvider
 #hibernate.connection.provider_class org.hibernate.connection.DatasourceConnectionProvider
 #hibernate.connection.provider_class org.hibernate.connection.C3P0ConnectionProvider
 #hibernate.connection.provider_class org.hibernate.connection.ProxoolConnectionProvider
 
 
 
 #######################
 ### Transaction API ###
 #######################
 
 ## Enable automatic flush during the JTA beforeCompletion() callback
 ## (This setting is relevant with or without the Transaction API)
 
 #hibernate.transaction.flush_before_completion
 
 
 ## Enable automatic session close at the end of transaction
 ## (This setting is relevant with or without the Transaction API)
 
 #hibernate.transaction.auto_close_session
 
 
 ## the Transaction API abstracts application code from the underlying JTA or JDBC transactions
 
 #hibernate.transaction.factory_class org.hibernate.transaction.JTATransactionFactory
 #hibernate.transaction.factory_class org.hibernate.transaction.JDBCTransactionFactory
 
 
 ## to use JTATransactionFactory, Hibernate must be able to locate the UserTransaction in JNDI
 ## default is java:comp/UserTransaction
 ## you do NOT need this setting if you specify hibernate.transaction.manager_lookup_class
 
 #jta.UserTransaction jta/usertransaction
 #jta.UserTransaction javax.transaction.UserTransaction
 #jta.UserTransaction UserTransaction
 
 
 ## to use the second-level cache with JTA, Hibernate must be able to obtain the JTA TransactionManager
 
 #hibernate.transaction.manager_lookup_class org.hibernate.transaction.JBossTransactionManagerLookup
 #hibernate.transaction.manager_lookup_class org.hibernate.transaction.WeblogicTransactionManagerLookup
 #hibernate.transaction.manager_lookup_class org.hibernate.transaction.WebSphereTransactionManagerLookup
 #hibernate.transaction.manager_lookup_class org.hibernate.transaction.OrionTransactionManagerLookup
 #hibernate.transaction.manager_lookup_class org.hibernate.transaction.ResinTransactionManagerLookup
 
 
 
 ##############################
 ### Miscellaneous Settings ###
 ##############################
 
 ## print all generated SQL to the console
 
 #hibernate.show_sql true
 
 
 ## format SQL in log and console
 
 hibernate.format_sql true
 
 
 ## add comments to the generated SQL
 
 #hibernate.use_sql_comments true
 
 
 ## generate statistics
 
 #hibernate.generate_statistics true
 
 
 ## auto schema export
 
 #hibernate.hbm2ddl.auto create-drop
 #hibernate.hbm2ddl.auto create
 #hibernate.hbm2ddl.auto update
 #hibernate.hbm2ddl.auto validate
 
 
 ## specify a default schema and catalog for unqualified tablenames
 
 #hibernate.default_schema test
 #hibernate.default_catalog test
 
 
 ## enable ordering of SQL UPDATEs by primary key
 
 #hibernate.order_updates true
 
 
 ## set the maximum depth of the outer join fetch tree
 
 hibernate.max_fetch_depth 1
 
 
 ## set the default batch size for batch fetching
 
 #hibernate.default_batch_fetch_size 8
 
 
 ## rollback generated identifier values of deleted entities to default values
 
 #hibernate.use_identifer_rollback true
 
 
 ## enable bytecode reflection optimizer (disabled by default)
 
 #hibernate.bytecode.use_reflection_optimizer true
 
 
 
 #####################
 ### JDBC Settings ###
 #####################
 
 ## specify a JDBC isolation level
 
 #hibernate.connection.isolation 4
 
 
 ## enable JDBC autocommit (not recommended!)
 
 #hibernate.connection.autocommit true
 
 
 ## set the JDBC fetch size
 
 #hibernate.jdbc.fetch_size 25
 
 
 ## set the maximum JDBC 2 batch size (a nonzero value enables batching)
 
 #hibernate.jdbc.batch_size 5
 #hibernate.jdbc.batch_size 0
 
 
 ## enable batch updates even for versioned data
 
 hibernate.jdbc.batch_versioned_data true
 
 
 ## enable use of JDBC 2 scrollable ResultSets (specifying a Dialect will cause Hibernate to use a sensible default)
 
 #hibernate.jdbc.use_scrollable_resultset true
 
 
 ## use streams when writing binary types to / from JDBC
 
 hibernate.jdbc.use_streams_for_binary true
 
 
 ## use JDBC 3 PreparedStatement.getGeneratedKeys() to get the identifier of an inserted row
 
 #hibernate.jdbc.use_get_generated_keys false
 
 
 ## choose a custom JDBC batcher
 
 # hibernate.jdbc.factory_class
 
 
 ## enable JDBC result set column alias caching 
 ## (minor performance enhancement for broken JDBC drivers)
 
 # hibernate.jdbc.wrap_result_sets
 
 
 ## choose a custom SQL exception converter
 
 #hibernate.jdbc.sql_exception_converter
 
 
 
 ##########################
 ### Second-level Cache ###
 ##########################
 
 ## optimize chache for minimal "puts" instead of minimal "gets" (good for clustered cache)
 
 #hibernate.cache.use_minimal_puts true
 
 
 ## set a prefix for cache region names
 
 hibernate.cache.region_prefix hibernate.test
 
 
 ## disable the second-level cache
 
 #hibernate.cache.use_second_level_cache false
 
 
 ## enable the query cache
 
 #hibernate.cache.use_query_cache true
 
 
 ## store the second-level cache entries in a more human-friendly format
 
 #hibernate.cache.use_structured_entries true
 
 
 ## choose a cache implementation
 
-#hibernate.cache.provider_class org.hibernate.cache.EhCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.EmptyCacheProvider
-hibernate.cache.provider_class org.hibernate.cache.internal.HashtableCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.TreeCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.OSCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.SwarmCacheProvider
-
+#hibernate.cache.region.factory_class org.hibernate.cache.infinispan.InfinispanRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.infinispan.JndiInfinispanRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.internal.EhCacheRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.internal.SingletonEhCacheRegionFactory
+hibernate.cache.region.factory_class org.hibernate.cache.internal.NoCachingRegionFactory
 
 ## choose a custom query cache implementation
 
 #hibernate.cache.query_cache_factory
 
 
 
 ############
 ### JNDI ###
 ############
 
 ## specify a JNDI name for the SessionFactory
 
 #hibernate.session_factory_name hibernate/session_factory
 
 
 ## Hibernate uses JNDI to bind a name to a SessionFactory and to look up the JTA UserTransaction;
 ## if hibernate.jndi.* are not specified, Hibernate will use the default InitialContext() which
 ## is the best approach in an application server
 
 #file system
 #hibernate.jndi.class com.sun.jndi.fscontext.RefFSContextFactory
 #hibernate.jndi.url file:/
 
 #WebSphere
 #hibernate.jndi.class com.ibm.websphere.naming.WsnInitialContextFactory
 #hibernate.jndi.url iiop://localhost:900/
 
diff --git a/etc/hibernate.properties.template b/etc/hibernate.properties.template
index 9445826986..8b7b015083 100644
--- a/etc/hibernate.properties.template
+++ b/etc/hibernate.properties.template
@@ -1,488 +1,487 @@
-######################
-### Query Language ###
-######################
-
-## define query language constants / function names
-
-hibernate.query.substitutions true 1, false 0, yes 'Y', no 'N'
-
-
-## Query translator factory class
-
-hibernate.query.factory_class @QUERY_TRANSLATOR_FACTORY@
-
-#################
-### Platforms ###
-#################
-
-hibernate.dialect @HIBERNATE_DIALECT@
-hibernate.connection.driver_class @DRIVER_CLASS@
-hibernate.connection.username @DB_USERNAME@
-hibernate.connection.password @DB_PASSWORD@
-hibernate.connection.url @DB_URL@
-
-## JNDI Datasource
-
-#hibernate.connection.datasource jdbc/test
-#hibernate.connection.username db2
-#hibernate.connection.password db2
-
-
-## HypersonicSQL
-
-#hibernate.dialect org.hibernate.dialect.HSQLDialect
-#hibernate.connection.driver_class org.hsqldb.jdbcDriver
-#hibernate.connection.username sa
-#hibernate.connection.password
-#hibernate.connection.url jdbc:hsqldb:hsql://localhost
-#hibernate.connection.url jdbc:hsqldb:test
-#hibernate.connection.url jdbc:hsqldb:.
-
-
-## MySQL
-
-
-#hibernate.dialect org.hibernate.dialect.MySQLDialect
-#hibernate.dialect org.hibernate.dialect.MySQLInnoDBDialect
-#hibernate.dialect org.hibernate.dialect.MySQLMyISAMDialect
-#hibernate.connection.driver_class org.gjt.mm.mysql.Driver
-#hibernate.connection.driver_class com.mysql.jdbc.Driver
-#hibernate.connection.url jdbc:mysql:///test
-#hibernate.connection.username gavin
-#hibernate.connection.password
-
-
-## Oracle
-
-#hibernate.dialect org.hibernate.dialect.OracleDialect
-#hibernate.dialect org.hibernate.dialect.Oracle9Dialect
-#hibernate.connection.driver_class oracle.jdbc.driver.OracleDriver
-#hibernate.connection.username ora
-#hibernate.connection.password ora
-#hibernate.connection.url jdbc:oracle:thin:@localhost:1521:test
-
-
-## PostgreSQL
-
-#hibernate.dialect org.hibernate.dialect.PostgreSQLDialect
-#hibernate.connection.driver_class org.postgresql.Driver
-#hibernate.connection.url jdbc:postgresql:template1
-#hibernate.connection.username pg
-#hibernate.connection.password
-#hibernate.query.substitutions yes 'Y', no 'N'
-
-
-## DB2
-
-#hibernate.dialect org.hibernate.dialect.DB2Dialect
-#hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver
-#hibernate.connection.url jdbc:db2:test
-#hibernate.connection.username db2
-#hibernate.connection.password db2
-
-## TimesTen (not supported yet)
-
-#hibernate.dialect org.hibernate.dialect.TimesTenDialect
-#hibernate.connection.driver_class com.timesten.jdbc.TimesTenDriver
-#hibernate.connection.url jdbc:timesten:direct:test
-#hibernate.connection.username
-#hibernate.connection.password 
-
-## DB2/400
-
-#hibernate.dialect org.hibernate.dialect.DB2400Dialect
-#hibernate.connection.username user
-#hibernate.connection.password password
-
-## Native driver
-#hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver
-#hibernate.connection.url jdbc:db2://systemname
-
-## Toolbox driver
-#hibernate.connection.driver_class com.ibm.as400.access.AS400JDBCDriver
-#hibernate.connection.url jdbc:as400://systemname
-
-
-## Derby (Not supported!)
-
-#hibernate.dialect org.hibernate.dialect.DerbyDialect
-#hibernate.connection.driver_class org.apache.derby.jdbc.EmbeddedDriver
-#hibernate.connection.username
-#hibernate.connection.password
-#hibernate.connection.url jdbc:derby:/test;create=true
-
-
-## Sybase
-
-#hibernate.dialect org.hibernate.dialect.SybaseDialect
-#hibernate.connection.driver_class com.sybase.jdbc2.jdbc.SybDriver
-#hibernate.connection.username sa
-#hibernate.connection.password sasasa
-#hibernate.connection.url jdbc:sybase:Tds:co3061835-a:5000/tempdb
-
-
-## Mckoi SQL
-
-#hibernate.dialect org.hibernate.dialect.MckoiDialect
-#hibernate.connection.driver_class com.mckoi.JDBCDriver
-#hibernate.connection.url jdbc:mckoi:///
-#hibernate.connection.url jdbc:mckoi:local://C:/mckoi1.00/db.conf
-#hibernate.connection.username admin
-#hibernate.connection.password nimda
-
-
-## SAP DB
-
-#hibernate.dialect org.hibernate.dialect.SAPDBDialect
-#hibernate.connection.driver_class com.sap.dbtech.jdbc.DriverSapDB
-#hibernate.connection.url jdbc:sapdb://localhost/TST
-#hibernate.connection.username TEST
-#hibernate.connection.password TEST
-#hibernate.query.substitutions yes 'Y', no 'N'
-
-
-## MS SQL Server
-
-#hibernate.dialect org.hibernate.dialect.SQLServerDialect
-#hibernate.connection.username sa
-#hibernate.connection.password sa
-
-## JSQL Driver
-#hibernate.connection.driver_class com.jnetdirect.jsql.JSQLDriver
-#hibernate.connection.url jdbc:JSQLConnect://1E1/test
-
-## JTURBO Driver
-#hibernate.connection.driver_class com.newatlanta.jturbo.driver.Driver
-#hibernate.connection.url jdbc:JTurbo://1E1:1433/test
-
-## WebLogic Driver
-#hibernate.connection.driver_class weblogic.jdbc.mssqlserver4.Driver
-#hibernate.connection.url jdbc:weblogic:mssqlserver4:1E1:1433
-
-## Microsoft Driver (not recommended!)
-#hibernate.connection.driver_class com.microsoft.jdbc.sqlserver.SQLServerDriver
-#hibernate.connection.url jdbc:microsoft:sqlserver://1E1;DatabaseName=test;SelectMethod=cursor
-
-## jTDS (since version 0.9)
-#hibernate.connection.driver_class net.sourceforge.jtds.jdbc.Driver
-#hibernate.connection.url jdbc:jtds:sqlserver://1E1/test
-
-## Interbase
-
-#hibernate.dialect org.hibernate.dialect.InterbaseDialect
-#hibernate.connection.username sysdba
-#hibernate.connection.password masterkey
-
-## DO NOT specify hibernate.connection.sqlDialect
-
-## InterClient
-
-#hibernate.connection.driver_class interbase.interclient.Driver
-#hibernate.connection.url jdbc:interbase://localhost:3060/C:/firebird/test.gdb
-
-## Pure Java
-
-#hibernate.connection.driver_class org.firebirdsql.jdbc.FBDriver
-#hibernate.connection.url jdbc:firebirdsql:localhost/3050:/firebird/test.gdb
-
-
-## Pointbase
-
-#hibernate.dialect org.hibernate.dialect.PointbaseDialect
-#hibernate.connection.driver_class com.pointbase.jdbc.jdbcUniversalDriver
-#hibernate.connection.url jdbc:pointbase:embedded:sample
-#hibernate.connection.username PBPUBLIC
-#hibernate.connection.password PBPUBLIC
-
-
-## Ingres
-
-#hibernate.dialect org.hibernate.dialect.IngresDialect 
-#hibernate.connection.driver_class ca.edbc.jdbc.EdbcDriver 
-#hibernate.connection.url jdbc:edbc://localhost:II7/database 
-#hibernate.connection.username user 
-#hibernate.connection.password password
-
-
-## Mimer SQL
-
-#hibernate.dialect org.hibernate.dialect.MimerSQLDialect
-#hibernate.connection.driver_class com.mimer.jdbc.Driver
-#hibernate.connection.url jdbc:mimer:multi1
-#hibernate.connection.username hibernate
-#hibernate.connection.password hibernate
-
-
-
-#################################
-### Hibernate Connection Pool ###
-#################################
-
-hibernate.connection.pool_size 1
-
-
-
-###########################
-### C3P0 Connection Pool###
-###########################
-
-#hibernate.c3p0.max_size 2
-#hibernate.c3p0.min_size 2
-#hibernate.c3p0.timeout 5000
-#hibernate.c3p0.max_statements 100
-#hibernate.c3p0.idle_test_period 3000
-#hibernate.c3p0.acquire_increment 2
-#hibernate.c3p0.validate false
-
-
-
-##############################
-### Proxool Connection Pool###
-##############################
-
-## Properties for external configuration of Proxool
-
-hibernate.proxool.pool_alias pool1
-
-## Only need one of the following
-
-#hibernate.proxool.existing_pool true
-#hibernate.proxool.xml proxool.xml
-#hibernate.proxool.properties proxool.properties
-
-
-
-#################################
-### Plugin ConnectionProvider ###
-#################################
-
-## use a custom ConnectionProvider (if not set, Hibernate will choose a built-in ConnectionProvider using hueristics)
-
-#hibernate.connection.provider_class org.hibernate.connection.DriverManagerConnectionProvider
-#hibernate.connection.provider_class org.hibernate.connection.DatasourceConnectionProvider
-#hibernate.connection.provider_class org.hibernate.connection.C3P0ConnectionProvider
-#hibernate.connection.provider_class org.hibernate.connection.DBCPConnectionProvider
-#hibernate.connection.provider_class org.hibernate.connection.ProxoolConnectionProvider
-
-
-
-#######################
-### Transaction API ###
-#######################
-
-## Enable automatic flush during the JTA beforeCompletion() callback
-## (This setting is relevant with or without the Transaction API)
-
-#hibernate.transaction.flush_before_completion
-
-
-## Enable automatic session close at the end of transaction
-## (This setting is relevant with or without the Transaction API)
-
-#hibernate.transaction.auto_close_session
-
-
-## the Transaction API abstracts application code from the underlying JTA or JDBC transactions
-
-#hibernate.transaction.factory_class org.hibernate.transaction.JTATransactionFactory
-#hibernate.transaction.factory_class org.hibernate.transaction.JDBCTransactionFactory
-
-
-## to use JTATransactionFactory, Hibernate must be able to locate the UserTransaction in JNDI
-## default is java:comp/UserTransaction
-## you do NOT need this setting if you specify hibernate.transaction.manager_lookup_class
-
-#jta.UserTransaction jta/usertransaction
-#jta.UserTransaction javax.transaction.UserTransaction
-#jta.UserTransaction UserTransaction
-
-
-## to use the second-level cache with JTA, Hibernate must be able to obtain the JTA TransactionManager
-
-#hibernate.transaction.manager_lookup_class org.hibernate.transaction.JBossTransactionManagerLookup
-#hibernate.transaction.manager_lookup_class org.hibernate.transaction.WeblogicTransactionManagerLookup
-#hibernate.transaction.manager_lookup_class org.hibernate.transaction.WebSphereTransactionManagerLookup
-#hibernate.transaction.manager_lookup_class org.hibernate.transaction.OrionTransactionManagerLookup
-#hibernate.transaction.manager_lookup_class org.hibernate.transaction.ResinTransactionManagerLookup
-
-
-
-##############################
-### Miscellaneous Settings ###
-##############################
-
-## print all generated SQL to the console
-
-#hibernate.show_sql true
-
-
-## add comments to the generated SQL
-
-#hibernate.use_sql_comments true
-
-
-## generate statistics
-
-#hibernate.generate_statistics true
-
-
-## auto schema export
-
-#hibernate.hbm2ddl.auto create-drop
-#hibernate.hbm2ddl.auto create
-#hibernate.hbm2ddl.auto update
-
-
-## specify a default schema and catalog for unqualified tablenames
-
-#hibernate.default_schema test
-#hibernate.default_catalog test
-
-
-## enable ordering of SQL UPDATEs by primary key
-
-hibernate.order_updates true
-
-
-## set the maximum depth of the outer join fetch tree
-
-hibernate.max_fetch_depth 1
-
-
-## set the default batch size for batch fetching
-
-hibernate.default_batch_fetch_size 8
-
-
-## rollback generated identifier values of deleted entities to default values
-
-#hibernate.use_identifer_rollback true
-
-
-## enable bytecode reflection optimizer (disabled by default)
-
-#hibernate.bytecode.use_reflection_optimizer true
-
-
-
-#####################
-### JDBC Settings ###
-#####################
-
-## specify a JDBC isolation level
-
-#hibernate.connection.isolation 4
-
-
-## enable JDBC autocommit (not recommended!)
-
-#hibernate.connection.autocommit true
-
-
-## set the JDBC fetch size
-
-#hibernate.jdbc.fetch_size 25
-
-
-## set the maximum JDBC 2 batch size (a nonzero value enables batching)
-
-#hibernate.jdbc.batch_size 5
-
-
-## enable batch updates even for versioned data
-
-hibernate.jdbc.batch_versioned_data true
-
-
-## enable use of JDBC 2 scrollable ResultSets (specifying a Dialect will cause Hibernate to use a sensible default)
-
-#hibernate.jdbc.use_scrollable_resultset true
-
-
-## use streams when writing binary types to / from JDBC
-
-hibernate.jdbc.use_streams_for_binary true
-
-
-## use JDBC 3 PreparedStatement.getGeneratedKeys() to get the identifier of an inserted row
-
-#hibernate.jdbc.use_get_generated_keys false
-
-
-## choose a custom JDBC batcher
-
-# hibernate.jdbc.factory_class
-
-
-## choose a custom SQL exception converter
-
-#hibernate.jdbc.sql_exception_converter
-
-
-
-##########################
-### Second-level Cache ###
-##########################
-
-## optimize chache for minimal "puts" instead of minimal "gets" (good for clustered cache)
-
-#hibernate.cache.use_minimal_puts true
-
-
-## set a prefix for cache region names
-
-hibernate.cache.region_prefix hibernate.test
-
-
-## disable the second-level cache
-
-#hibernate.cache.use_second_level_cache false
-
-
-## enable the query cache
-
-#hibernate.cache.use_query_cache true
-
-
-## store the second-level cache entries in a more human-friendly format
-
-#hibernate.cache.use_structured_entries true
-
-
-## choose a cache implementation
-
-#hibernate.cache.provider_class org.hibernate.cache.EhCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.EmptyCacheProvider
-hibernate.cache.provider_class org.hibernate.cache.HashtableCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.TreeCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.OSCacheProvider
-#hibernate.cache.provider_class org.hibernate.cache.SwarmCacheProvider
-
-
-## choose a custom query cache implementation
-
-#hibernate.cache.query_cache_factory
-
-
-
-############
-### JNDI ###
-############
-
-## specify a JNDI name for the SessionFactory
-
-#hibernate.session_factory_name hibernate/session_factory
-
-
-## Hibernate uses JNDI to bind a name to a SessionFactory and to look up the JTA UserTransaction;
-## if hibernate.jndi.* are not specified, Hibernate will use the default InitialContext() which
-## is the best approach in an application server
-
-#file system
-#hibernate.jndi.class com.sun.jndi.fscontext.RefFSContextFactory
-#hibernate.jndi.url file:/
-
-#WebSphere
-#hibernate.jndi.class com.ibm.websphere.naming.WsnInitialContextFactory
-#hibernate.jndi.url iiop://localhost:900/
-
+######################
+### Query Language ###
+######################
+
+## define query language constants / function names
+
+hibernate.query.substitutions true 1, false 0, yes 'Y', no 'N'
+
+
+## Query translator factory class
+
+hibernate.query.factory_class @QUERY_TRANSLATOR_FACTORY@
+
+#################
+### Platforms ###
+#################
+
+hibernate.dialect @HIBERNATE_DIALECT@
+hibernate.connection.driver_class @DRIVER_CLASS@
+hibernate.connection.username @DB_USERNAME@
+hibernate.connection.password @DB_PASSWORD@
+hibernate.connection.url @DB_URL@
+
+## JNDI Datasource
+
+#hibernate.connection.datasource jdbc/test
+#hibernate.connection.username db2
+#hibernate.connection.password db2
+
+
+## HypersonicSQL
+
+#hibernate.dialect org.hibernate.dialect.HSQLDialect
+#hibernate.connection.driver_class org.hsqldb.jdbcDriver
+#hibernate.connection.username sa
+#hibernate.connection.password
+#hibernate.connection.url jdbc:hsqldb:hsql://localhost
+#hibernate.connection.url jdbc:hsqldb:test
+#hibernate.connection.url jdbc:hsqldb:.
+
+
+## MySQL
+
+
+#hibernate.dialect org.hibernate.dialect.MySQLDialect
+#hibernate.dialect org.hibernate.dialect.MySQLInnoDBDialect
+#hibernate.dialect org.hibernate.dialect.MySQLMyISAMDialect
+#hibernate.connection.driver_class org.gjt.mm.mysql.Driver
+#hibernate.connection.driver_class com.mysql.jdbc.Driver
+#hibernate.connection.url jdbc:mysql:///test
+#hibernate.connection.username gavin
+#hibernate.connection.password
+
+
+## Oracle
+
+#hibernate.dialect org.hibernate.dialect.OracleDialect
+#hibernate.dialect org.hibernate.dialect.Oracle9Dialect
+#hibernate.connection.driver_class oracle.jdbc.driver.OracleDriver
+#hibernate.connection.username ora
+#hibernate.connection.password ora
+#hibernate.connection.url jdbc:oracle:thin:@localhost:1521:test
+
+
+## PostgreSQL
+
+#hibernate.dialect org.hibernate.dialect.PostgreSQLDialect
+#hibernate.connection.driver_class org.postgresql.Driver
+#hibernate.connection.url jdbc:postgresql:template1
+#hibernate.connection.username pg
+#hibernate.connection.password
+#hibernate.query.substitutions yes 'Y', no 'N'
+
+
+## DB2
+
+#hibernate.dialect org.hibernate.dialect.DB2Dialect
+#hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver
+#hibernate.connection.url jdbc:db2:test
+#hibernate.connection.username db2
+#hibernate.connection.password db2
+
+## TimesTen (not supported yet)
+
+#hibernate.dialect org.hibernate.dialect.TimesTenDialect
+#hibernate.connection.driver_class com.timesten.jdbc.TimesTenDriver
+#hibernate.connection.url jdbc:timesten:direct:test
+#hibernate.connection.username
+#hibernate.connection.password 
+
+## DB2/400
+
+#hibernate.dialect org.hibernate.dialect.DB2400Dialect
+#hibernate.connection.username user
+#hibernate.connection.password password
+
+## Native driver
+#hibernate.connection.driver_class COM.ibm.db2.jdbc.app.DB2Driver
+#hibernate.connection.url jdbc:db2://systemname
+
+## Toolbox driver
+#hibernate.connection.driver_class com.ibm.as400.access.AS400JDBCDriver
+#hibernate.connection.url jdbc:as400://systemname
+
+
+## Derby (Not supported!)
+
+#hibernate.dialect org.hibernate.dialect.DerbyDialect
+#hibernate.connection.driver_class org.apache.derby.jdbc.EmbeddedDriver
+#hibernate.connection.username
+#hibernate.connection.password
+#hibernate.connection.url jdbc:derby:/test;create=true
+
+
+## Sybase
+
+#hibernate.dialect org.hibernate.dialect.SybaseDialect
+#hibernate.connection.driver_class com.sybase.jdbc2.jdbc.SybDriver
+#hibernate.connection.username sa
+#hibernate.connection.password sasasa
+#hibernate.connection.url jdbc:sybase:Tds:co3061835-a:5000/tempdb
+
+
+## Mckoi SQL
+
+#hibernate.dialect org.hibernate.dialect.MckoiDialect
+#hibernate.connection.driver_class com.mckoi.JDBCDriver
+#hibernate.connection.url jdbc:mckoi:///
+#hibernate.connection.url jdbc:mckoi:local://C:/mckoi1.00/db.conf
+#hibernate.connection.username admin
+#hibernate.connection.password nimda
+
+
+## SAP DB
+
+#hibernate.dialect org.hibernate.dialect.SAPDBDialect
+#hibernate.connection.driver_class com.sap.dbtech.jdbc.DriverSapDB
+#hibernate.connection.url jdbc:sapdb://localhost/TST
+#hibernate.connection.username TEST
+#hibernate.connection.password TEST
+#hibernate.query.substitutions yes 'Y', no 'N'
+
+
+## MS SQL Server
+
+#hibernate.dialect org.hibernate.dialect.SQLServerDialect
+#hibernate.connection.username sa
+#hibernate.connection.password sa
+
+## JSQL Driver
+#hibernate.connection.driver_class com.jnetdirect.jsql.JSQLDriver
+#hibernate.connection.url jdbc:JSQLConnect://1E1/test
+
+## JTURBO Driver
+#hibernate.connection.driver_class com.newatlanta.jturbo.driver.Driver
+#hibernate.connection.url jdbc:JTurbo://1E1:1433/test
+
+## WebLogic Driver
+#hibernate.connection.driver_class weblogic.jdbc.mssqlserver4.Driver
+#hibernate.connection.url jdbc:weblogic:mssqlserver4:1E1:1433
+
+## Microsoft Driver (not recommended!)
+#hibernate.connection.driver_class com.microsoft.jdbc.sqlserver.SQLServerDriver
+#hibernate.connection.url jdbc:microsoft:sqlserver://1E1;DatabaseName=test;SelectMethod=cursor
+
+## jTDS (since version 0.9)
+#hibernate.connection.driver_class net.sourceforge.jtds.jdbc.Driver
+#hibernate.connection.url jdbc:jtds:sqlserver://1E1/test
+
+## Interbase
+
+#hibernate.dialect org.hibernate.dialect.InterbaseDialect
+#hibernate.connection.username sysdba
+#hibernate.connection.password masterkey
+
+## DO NOT specify hibernate.connection.sqlDialect
+
+## InterClient
+
+#hibernate.connection.driver_class interbase.interclient.Driver
+#hibernate.connection.url jdbc:interbase://localhost:3060/C:/firebird/test.gdb
+
+## Pure Java
+
+#hibernate.connection.driver_class org.firebirdsql.jdbc.FBDriver
+#hibernate.connection.url jdbc:firebirdsql:localhost/3050:/firebird/test.gdb
+
+
+## Pointbase
+
+#hibernate.dialect org.hibernate.dialect.PointbaseDialect
+#hibernate.connection.driver_class com.pointbase.jdbc.jdbcUniversalDriver
+#hibernate.connection.url jdbc:pointbase:embedded:sample
+#hibernate.connection.username PBPUBLIC
+#hibernate.connection.password PBPUBLIC
+
+
+## Ingres
+
+#hibernate.dialect org.hibernate.dialect.IngresDialect 
+#hibernate.connection.driver_class ca.edbc.jdbc.EdbcDriver 
+#hibernate.connection.url jdbc:edbc://localhost:II7/database 
+#hibernate.connection.username user 
+#hibernate.connection.password password
+
+
+## Mimer SQL
+
+#hibernate.dialect org.hibernate.dialect.MimerSQLDialect
+#hibernate.connection.driver_class com.mimer.jdbc.Driver
+#hibernate.connection.url jdbc:mimer:multi1
+#hibernate.connection.username hibernate
+#hibernate.connection.password hibernate
+
+
+
+#################################
+### Hibernate Connection Pool ###
+#################################
+
+hibernate.connection.pool_size 1
+
+
+
+###########################
+### C3P0 Connection Pool###
+###########################
+
+#hibernate.c3p0.max_size 2
+#hibernate.c3p0.min_size 2
+#hibernate.c3p0.timeout 5000
+#hibernate.c3p0.max_statements 100
+#hibernate.c3p0.idle_test_period 3000
+#hibernate.c3p0.acquire_increment 2
+#hibernate.c3p0.validate false
+
+
+
+##############################
+### Proxool Connection Pool###
+##############################
+
+## Properties for external configuration of Proxool
+
+hibernate.proxool.pool_alias pool1
+
+## Only need one of the following
+
+#hibernate.proxool.existing_pool true
+#hibernate.proxool.xml proxool.xml
+#hibernate.proxool.properties proxool.properties
+
+
+
+#################################
+### Plugin ConnectionProvider ###
+#################################
+
+## use a custom ConnectionProvider (if not set, Hibernate will choose a built-in ConnectionProvider using hueristics)
+
+#hibernate.connection.provider_class org.hibernate.connection.DriverManagerConnectionProvider
+#hibernate.connection.provider_class org.hibernate.connection.DatasourceConnectionProvider
+#hibernate.connection.provider_class org.hibernate.connection.C3P0ConnectionProvider
+#hibernate.connection.provider_class org.hibernate.connection.DBCPConnectionProvider
+#hibernate.connection.provider_class org.hibernate.connection.ProxoolConnectionProvider
+
+
+
+#######################
+### Transaction API ###
+#######################
+
+## Enable automatic flush during the JTA beforeCompletion() callback
+## (This setting is relevant with or without the Transaction API)
+
+#hibernate.transaction.flush_before_completion
+
+
+## Enable automatic session close at the end of transaction
+## (This setting is relevant with or without the Transaction API)
+
+#hibernate.transaction.auto_close_session
+
+
+## the Transaction API abstracts application code from the underlying JTA or JDBC transactions
+
+#hibernate.transaction.factory_class org.hibernate.transaction.JTATransactionFactory
+#hibernate.transaction.factory_class org.hibernate.transaction.JDBCTransactionFactory
+
+
+## to use JTATransactionFactory, Hibernate must be able to locate the UserTransaction in JNDI
+## default is java:comp/UserTransaction
+## you do NOT need this setting if you specify hibernate.transaction.manager_lookup_class
+
+#jta.UserTransaction jta/usertransaction
+#jta.UserTransaction javax.transaction.UserTransaction
+#jta.UserTransaction UserTransaction
+
+
+## to use the second-level cache with JTA, Hibernate must be able to obtain the JTA TransactionManager
+
+#hibernate.transaction.manager_lookup_class org.hibernate.transaction.JBossTransactionManagerLookup
+#hibernate.transaction.manager_lookup_class org.hibernate.transaction.WeblogicTransactionManagerLookup
+#hibernate.transaction.manager_lookup_class org.hibernate.transaction.WebSphereTransactionManagerLookup
+#hibernate.transaction.manager_lookup_class org.hibernate.transaction.OrionTransactionManagerLookup
+#hibernate.transaction.manager_lookup_class org.hibernate.transaction.ResinTransactionManagerLookup
+
+
+
+##############################
+### Miscellaneous Settings ###
+##############################
+
+## print all generated SQL to the console
+
+#hibernate.show_sql true
+
+
+## add comments to the generated SQL
+
+#hibernate.use_sql_comments true
+
+
+## generate statistics
+
+#hibernate.generate_statistics true
+
+
+## auto schema export
+
+#hibernate.hbm2ddl.auto create-drop
+#hibernate.hbm2ddl.auto create
+#hibernate.hbm2ddl.auto update
+
+
+## specify a default schema and catalog for unqualified tablenames
+
+#hibernate.default_schema test
+#hibernate.default_catalog test
+
+
+## enable ordering of SQL UPDATEs by primary key
+
+hibernate.order_updates true
+
+
+## set the maximum depth of the outer join fetch tree
+
+hibernate.max_fetch_depth 1
+
+
+## set the default batch size for batch fetching
+
+hibernate.default_batch_fetch_size 8
+
+
+## rollback generated identifier values of deleted entities to default values
+
+#hibernate.use_identifer_rollback true
+
+
+## enable bytecode reflection optimizer (disabled by default)
+
+#hibernate.bytecode.use_reflection_optimizer true
+
+
+
+#####################
+### JDBC Settings ###
+#####################
+
+## specify a JDBC isolation level
+
+#hibernate.connection.isolation 4
+
+
+## enable JDBC autocommit (not recommended!)
+
+#hibernate.connection.autocommit true
+
+
+## set the JDBC fetch size
+
+#hibernate.jdbc.fetch_size 25
+
+
+## set the maximum JDBC 2 batch size (a nonzero value enables batching)
+
+#hibernate.jdbc.batch_size 5
+
+
+## enable batch updates even for versioned data
+
+hibernate.jdbc.batch_versioned_data true
+
+
+## enable use of JDBC 2 scrollable ResultSets (specifying a Dialect will cause Hibernate to use a sensible default)
+
+#hibernate.jdbc.use_scrollable_resultset true
+
+
+## use streams when writing binary types to / from JDBC
+
+hibernate.jdbc.use_streams_for_binary true
+
+
+## use JDBC 3 PreparedStatement.getGeneratedKeys() to get the identifier of an inserted row
+
+#hibernate.jdbc.use_get_generated_keys false
+
+
+## choose a custom JDBC batcher
+
+# hibernate.jdbc.factory_class
+
+
+## choose a custom SQL exception converter
+
+#hibernate.jdbc.sql_exception_converter
+
+
+
+##########################
+### Second-level Cache ###
+##########################
+
+## optimize chache for minimal "puts" instead of minimal "gets" (good for clustered cache)
+
+#hibernate.cache.use_minimal_puts true
+
+
+## set a prefix for cache region names
+
+hibernate.cache.region_prefix hibernate.test
+
+
+## disable the second-level cache
+
+#hibernate.cache.use_second_level_cache false
+
+
+## enable the query cache
+
+#hibernate.cache.use_query_cache true
+
+
+## store the second-level cache entries in a more human-friendly format
+
+#hibernate.cache.use_structured_entries true
+
+
+## choose a cache implementation
+
+#hibernate.cache.region.factory_class org.hibernate.cache.infinispan.InfinispanRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.infinispan.JndiInfinispanRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.internal.EhCacheRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.internal.SingletonEhCacheRegionFactory
+#hibernate.cache.region.factory_class org.hibernate.cache.internal.NoCachingRegionFactory
+
+
+## choose a custom query cache implementation
+
+#hibernate.cache.query_cache_factory
+
+
+
+############
+### JNDI ###
+############
+
+## specify a JNDI name for the SessionFactory
+
+#hibernate.session_factory_name hibernate/session_factory
+
+
+## Hibernate uses JNDI to bind a name to a SessionFactory and to look up the JTA UserTransaction;
+## if hibernate.jndi.* are not specified, Hibernate will use the default InitialContext() which
+## is the best approach in an application server
+
+#file system
+#hibernate.jndi.class com.sun.jndi.fscontext.RefFSContextFactory
+#hibernate.jndi.url file:/
+
+#WebSphere
+#hibernate.jndi.class com.ibm.websphere.naming.WsnInitialContextFactory
+#hibernate.jndi.url iiop://localhost:900/
+
diff --git a/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java b/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
index 999b584057..64f2c98337 100644
--- a/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
@@ -1,81 +1,79 @@
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
 package org.hibernate;
 
 import java.util.Map;
 
+import org.jboss.logging.Logger;
+
 import org.hibernate.cfg.Environment;
+import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Describes the methods for multi-tenancy understood by Hibernate.
  *
  * @author Steve Ebersole
  */
 public enum MultiTenancyStrategy {
+
 	/**
 	 * Multi-tenancy implemented by use of discriminator columns.
 	 */
 	DISCRIMINATOR,
 	/**
 	 * Multi-tenancy implemented as separate schemas.
 	 */
 	SCHEMA,
 	/**
 	 * Multi-tenancy implemented as separate databases.
 	 */
 	DATABASE,
 	/**
 	 * No multi-tenancy
 	 */
 	NONE;
-
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			MultiTenancyStrategy.class.getName()
+	);
 	public static MultiTenancyStrategy determineMultiTenancyStrategy(Map properties) {
 		final Object strategy = properties.get( Environment.MULTI_TENANT );
 		if ( strategy == null ) {
 			return MultiTenancyStrategy.NONE;
 		}
 
 		if ( MultiTenancyStrategy.class.isInstance( strategy ) ) {
 			return (MultiTenancyStrategy) strategy;
 		}
 
 		final String strategyName = strategy.toString();
-		if ( MultiTenancyStrategy.DISCRIMINATOR.name().equals( strategyName ) ) {
-			return MultiTenancyStrategy.DISCRIMINATOR;
-		}
-		else if ( MultiTenancyStrategy.SCHEMA.name().equals( strategyName ) ) {
-			return MultiTenancyStrategy.SCHEMA;
-		}
-		else if ( MultiTenancyStrategy.DATABASE.name().equals( strategyName ) ) {
-			return MultiTenancyStrategy.DATABASE;
-		}
-		else if ( MultiTenancyStrategy.NONE.name().equals( strategyName ) ) {
-			return MultiTenancyStrategy.NONE;
+		try {
+			return MultiTenancyStrategy.valueOf( strategyName.toUpperCase() );
 		}
-		else {
-			// todo log?
+		catch ( RuntimeException e ) {
+			LOG.warn( "Unknown multi tenancy strategy [ " +strategyName +" ], using MultiTenancyStrategy.NONE." );
 			return MultiTenancyStrategy.NONE;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/HashtableCache.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/HashtableCache.java
deleted file mode 100644
index 519f9f5b2e..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/HashtableCache.java
+++ /dev/null
@@ -1,114 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal;
-
-import java.util.Collections;
-import java.util.Hashtable;
-import java.util.Map;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.Cache;
-
-/**
- * A lightweight implementation of the <tt>Cache</tt> interface
- * @author Gavin King
- */
-public class HashtableCache implements Cache {
-	
-	private final Map hashtable = new Hashtable();
-	private final String regionName;
-	
-	public HashtableCache(String regionName) {
-		this.regionName = regionName;
-	}
-
-	public String getRegionName() {
-		return regionName;
-	}
-
-	public Object read(Object key) throws CacheException {
-		return hashtable.get(key);
-	}
-
-	public Object get(Object key) throws CacheException {
-		return hashtable.get(key);
-	}
-
-	public void update(Object key, Object value) throws CacheException {
-		put(key, value);
-	}
-	
-	public void put(Object key, Object value) throws CacheException {
-		hashtable.put(key, value);
-	}
-
-	public void remove(Object key) throws CacheException {
-		hashtable.remove(key);
-	}
-
-	public void clear() throws CacheException {
-		hashtable.clear();
-	}
-
-	public void destroy() throws CacheException {
-
-	}
-
-	public void lock(Object key) throws CacheException {
-		// local cache, so we use synchronization
-	}
-
-	public void unlock(Object key) throws CacheException {
-		// local cache, so we use synchronization
-	}
-
-	public long nextTimestamp() {
-		return Timestamper.next();
-	}
-
-	public int getTimeout() {
-		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
-	}
-
-	public long getSizeInMemory() {
-		return -1;
-	}
-
-	public long getElementCountInMemory() {
-		return hashtable.size();
-	}
-
-	public long getElementCountOnDisk() {
-		return 0;
-	}
-	
-	public Map toMap() {
-		return Collections.unmodifiableMap(hashtable);
-	}
-
-	public String toString() {
-		return "HashtableCache(" + regionName + ')';
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java
deleted file mode 100644
index 30c455a44d..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java
+++ /dev/null
@@ -1,86 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal;
-
-import java.util.Properties;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.NoCachingEnabledException;
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.CacheProvider;
-
-/**
- * Implementation of NoCacheProvider.
- *
- * @author Steve Ebersole
- */
-@Deprecated
-public class NoCacheProvider implements CacheProvider {
-	/**
-	 * Configure the cache
-	 *
-	 * @param regionName the name of the cache region
-	 * @param properties configuration settings
-	 *
-	 * @throws org.hibernate.cache.CacheException
-	 */
-	public Cache buildCache(String regionName, Properties properties) throws CacheException {
-		throw new NoCachingEnabledException();
-	}
-
-	/**
-	 * Generate a timestamp
-	 */
-	public long nextTimestamp() {
-		// This, is used by SessionFactoryImpl to hand to the generated SessionImpl;
-		// was the only reason I could see that we cannot just use null as
-		// Settings.cacheProvider
-		return System.currentTimeMillis() / 100;
-	}
-
-	/**
-	 * Callback to perform any necessary initialization of the underlying cache implementation during SessionFactory
-	 * construction.
-	 *
-	 * @param properties current configuration settings.
-	 */
-	public void start(Properties properties) throws CacheException {
-		// this is called by SessionFactory irregardless; we just disregard here;
-		// could also add a check to SessionFactory to only conditionally call start
-	}
-
-	/**
-	 * Callback to perform any necessary cleanup of the underlying cache implementation during SessionFactory.close().
-	 */
-	public void stop() {
-		// this is called by SessionFactory irregardless; we just disregard here;
-		// could also add a check to SessionFactory to only conditionally call stop
-	}
-
-	public boolean isMinimalPutsEnabledByDefault() {
-		// this is called from SettingsFactory irregardless; trivial to simply disregard
-		return false;
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/Timestamper.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/Timestamper.java
index fc9c9d1e54..84494a75ba 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/Timestamper.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/Timestamper.java
@@ -1,59 +1,58 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.internal;
 
+import java.util.concurrent.atomic.AtomicLong;
+
 /**
  * Generates increasing identifiers (in a single VM only). Not valid across multiple VMs.  Identifiers are not
  * necessarily strictly increasing, but usually are.
  */
 public final class Timestamper {
-	private static short counter = 0;
-	private static long time;
 	private static final int BIN_DIGITS = 12;
 	public static final short ONE_MS = 1<<BIN_DIGITS;
-	
+	private static final AtomicLong VALUE = new AtomicLong();
 	public static long next() {
-		synchronized(Timestamper.class) {
-			long newTime = System.currentTimeMillis() << BIN_DIGITS;
-			if (time<newTime) {
-				time = newTime;
-				counter = 0;
-			}
-			else if (counter < ONE_MS - 1 ) {
-				counter++;
-			}
-			
-			return time + counter;
-		}
+        while (true) {
+            long base = System.currentTimeMillis() << BIN_DIGITS;
+            long maxValue = base + ONE_MS - 1;
+
+            for (long current = VALUE.get(), update = Math.max(base, current + 1); update < maxValue;
+                 current = VALUE.get(), update = Math.max(base, current + 1)) {
+                if (VALUE.compareAndSet(current, update)) {
+                    return update;
+                }
+            }
+        }
 	}
 
 	private Timestamper() {
 	}
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/CollectionAccessStrategyAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/CollectionAccessStrategyAdapter.java
deleted file mode 100644
index 54907784c9..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/CollectionAccessStrategyAdapter.java
+++ /dev/null
@@ -1,114 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheConcurrencyStrategy;
-import org.hibernate.cache.spi.CollectionRegion;
-import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.cfg.Settings;
-
-/**
- * Adapter specifically bridging {@link CollectionRegionAccessStrategy} to {@link CacheConcurrencyStrategy}.
- *
- * @author Steve Ebersole
- */
-public class CollectionAccessStrategyAdapter implements CollectionRegionAccessStrategy {
-	private final CollectionRegion region;
-	private final CacheConcurrencyStrategy ccs;
-	private final Settings settings;
-
-	public CollectionAccessStrategyAdapter(CollectionRegion region, CacheConcurrencyStrategy ccs, Settings settings) {
-		this.region = region;
-		this.ccs = ccs;
-		this.settings = settings;
-	}
-
-	public CollectionRegion getRegion() {
-		return region;
-	}
-
-	public Object get(Object key, long txTimestamp) throws CacheException {
-		return ccs.get( key, txTimestamp );
-	}
-
-	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
-		return putFromLoad( key, value, txTimestamp, version, settings.isMinimalPutsEnabled() );
-	}
-
-	public boolean putFromLoad(
-			Object key, 
-			Object value,
-			long txTimestamp,
-			Object version,
-			boolean minimalPutOverride) throws CacheException {
-		return ccs.put( key, value, txTimestamp, version, region.getCacheDataDescription().getVersionComparator(), minimalPutOverride );
-	}
-
-	public SoftLock lockItem(Object key, Object version) throws CacheException {
-		return ccs.lock( key, version );
-	}
-
-	public SoftLock lockRegion() throws CacheException {
-		// no-op; CCS did not have such a concept
-		return null;
-	}
-
-	public void unlockItem(Object key, SoftLock lock) throws CacheException {
-		ccs.release( key, lock );
-	}
-
-	public void unlockRegion(SoftLock lock) throws CacheException {
-		// again, CCS did not have such a concept; but a reasonable
-		// proximity is to clear the cache after transaction *as long as*
-		// the underlying cache is not JTA aware.
-		if ( !region.isTransactionAware() ) {
-			ccs.clear();
-		}
-	}
-
-	public void remove(Object key) throws CacheException {
-		ccs.evict( key );
-	}
-
-	public void removeAll() throws CacheException {
-		// again, CCS did not have such a concept; however a reasonable
-		// proximity is to clear the cache.  For non-transaction aware
-		// caches, we will also do a clear at the end of the transaction
-		ccs.clear();
-	}
-
-	public void evict(Object key) throws CacheException {
-		ccs.remove( key );
-	}
-
-	public void evictAll() throws CacheException {
-		ccs.clear();
-	}
-
-	public void destroy() {
-		ccs.destroy();
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/CollectionRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/CollectionRegionAdapter.java
deleted file mode 100644
index af5ececce2..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/CollectionRegionAdapter.java
+++ /dev/null
@@ -1,81 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.CacheConcurrencyStrategy;
-import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.CollectionRegion;
-import org.hibernate.cache.spi.NonstrictReadWriteCache;
-import org.hibernate.cache.spi.OptimisticCache;
-import org.hibernate.cache.spi.ReadOnlyCache;
-import org.hibernate.cache.spi.ReadWriteCache;
-import org.hibernate.cache.spi.TransactionalCache;
-import org.hibernate.cache.spi.access.AccessType;
-import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
-import org.hibernate.cfg.Settings;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Adapter specifically bridging {@link CollectionRegion} to {@link Cache}.
- *
- * @author Steve Ebersole
- */
-public class CollectionRegionAdapter extends BaseTransactionalDataRegionAdapter implements CollectionRegion {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       CollectionRegionAdapter.class.getName());
-
-	public CollectionRegionAdapter(Cache underlyingCache, Settings settings, CacheDataDescription metadata) {
-		super( underlyingCache, settings, metadata );
-		if ( underlyingCache instanceof OptimisticCache ) {
-			( ( OptimisticCache ) underlyingCache ).setSource( new OptimisticCacheSourceAdapter( metadata ) );
-		}
-	}
-
-	public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
-		CacheConcurrencyStrategy ccs;
-		if ( AccessType.READ_ONLY.equals( accessType ) ) {
-            if (metadata.isMutable()) LOG.readOnlyCacheConfiguredForMutableCollection(getName());
-			ccs = new ReadOnlyCache();
-		}
-		else if ( AccessType.READ_WRITE.equals( accessType ) ) {
-			ccs = new ReadWriteCache();
-		}
-		else if ( AccessType.NONSTRICT_READ_WRITE.equals( accessType ) ) {
-			ccs = new NonstrictReadWriteCache();
-		}
-		else if ( AccessType.TRANSACTIONAL.equals( accessType ) ) {
-			ccs = new TransactionalCache();
-		}
-		else {
-			throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
-		}
-		ccs.setCache( underlyingCache );
-		return new CollectionAccessStrategyAdapter( this, ccs, settings );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/EntityAccessStrategyAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/EntityAccessStrategyAdapter.java
deleted file mode 100644
index 6ba18e29cc..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/EntityAccessStrategyAdapter.java
+++ /dev/null
@@ -1,132 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheConcurrencyStrategy;
-import org.hibernate.cache.spi.EntityRegion;
-import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.cfg.Settings;
-
-/**
- * Adapter specifically bridging {@link EntityRegionAccessStrategy} to {@link CacheConcurrencyStrategy}.
- *
- * @author Steve Ebersole
- */
-public class EntityAccessStrategyAdapter implements EntityRegionAccessStrategy {
-	private final EntityRegion region;
-	private final CacheConcurrencyStrategy ccs;
-	private final Settings settings;
-
-	public EntityAccessStrategyAdapter(EntityRegion region, CacheConcurrencyStrategy ccs, Settings settings) {
-		this.region = region;
-		this.ccs = ccs;
-		this.settings = settings;
-	}
-
-	public EntityRegion getRegion() {
-		return region;
-	}
-
-	public Object get(Object key, long txTimestamp) throws CacheException {
-		return ccs.get( key, txTimestamp );
-	}
-
-	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
-		return putFromLoad( key, value, txTimestamp, version, settings.isMinimalPutsEnabled() );
-	}
-
-	public boolean putFromLoad(
-			Object key,
-			Object value,
-			long txTimestamp,
-			Object version,
-			boolean minimalPutOverride) throws CacheException {
-		return ccs.put( key, value, txTimestamp, version, region.getCacheDataDescription().getVersionComparator(), minimalPutOverride );
-	}
-
-	public SoftLock lockItem(Object key, Object version) throws CacheException {
-		return ccs.lock( key, version );
-	}
-
-	public SoftLock lockRegion() throws CacheException {
-		// no-op; CCS did not have such a concept
-		return null;
-	}
-
-	public void unlockItem(Object key, SoftLock lock) throws CacheException {
-		ccs.release( key, lock );
-	}
-
-	public void unlockRegion(SoftLock lock) throws CacheException {
-		// again, CCS did not have such a concept; but a reasonable
-		// proximity is to clear the cache after transaction *as long as*
-		// the underlying cache is not JTA aware.
-		if ( !region.isTransactionAware() ) {
-			ccs.clear();
-		}
-	}
-
-	public boolean insert(Object key, Object value, Object version) throws CacheException {
-		return ccs.insert( key, value, version );
-	}
-
-	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
-		return ccs.afterInsert( key, value, version );
-	}
-
-	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
-			throws CacheException {
-		return ccs.update( key, value, currentVersion, previousVersion );
-	}
-
-	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
-			throws CacheException {
-		return ccs.afterUpdate( key, value, currentVersion, lock );
-	}
-
-	public void remove(Object key) throws CacheException {
-		ccs.evict( key );
-	}
-
-	public void removeAll() throws CacheException {
-		// again, CCS did not have such a concept; however a reasonable
-		// proximity is to clear the cache.  For non-transaction aware
-		// caches, we will also do a clear at the end of the transaction
-		ccs.clear();
-	}
-
-	public void evict(Object key) throws CacheException {
-		ccs.remove( key );
-	}
-
-	public void evictAll() throws CacheException {
-		ccs.clear();
-	}
-
-	public void destroy() {
-		ccs.destroy();
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/EntityRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/EntityRegionAdapter.java
deleted file mode 100644
index c19ff829f0..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/EntityRegionAdapter.java
+++ /dev/null
@@ -1,80 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.CacheConcurrencyStrategy;
-import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.EntityRegion;
-import org.hibernate.cache.spi.NonstrictReadWriteCache;
-import org.hibernate.cache.spi.OptimisticCache;
-import org.hibernate.cache.spi.ReadOnlyCache;
-import org.hibernate.cache.spi.ReadWriteCache;
-import org.hibernate.cache.spi.TransactionalCache;
-import org.hibernate.cache.spi.access.AccessType;
-import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
-import org.hibernate.cfg.Settings;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Adapter specifically bridging {@link EntityRegion} to {@link org.hibernate.cache.spi.Cache}.
- *
- * @author Steve Ebersole
- */
-public class EntityRegionAdapter extends BaseTransactionalDataRegionAdapter implements EntityRegion {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityRegionAdapter.class.getName());
-
-	public EntityRegionAdapter(Cache underlyingCache, Settings settings, CacheDataDescription metadata) {
-		super( underlyingCache, settings, metadata );
-		if ( underlyingCache instanceof OptimisticCache ) {
-			( (OptimisticCache) underlyingCache ).setSource( new OptimisticCacheSourceAdapter( metadata ) );
-		}
-	}
-
-	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
-		CacheConcurrencyStrategy ccs;
-		if ( AccessType.READ_ONLY.equals( accessType ) ) {
-            if (metadata.isMutable()) LOG.readOnlyCacheConfiguredForMutableCollection(getName());
-			ccs = new ReadOnlyCache();
-		}
-		else if ( AccessType.READ_WRITE.equals( accessType ) ) {
-			ccs = new ReadWriteCache();
-		}
-		else if ( AccessType.NONSTRICT_READ_WRITE.equals( accessType ) ) {
-			ccs = new NonstrictReadWriteCache();
-		}
-		else if ( AccessType.TRANSACTIONAL.equals( accessType ) ) {
-			ccs = new TransactionalCache();
-		}
-		else {
-			throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
-		}
-		ccs.setCache( underlyingCache );
-		return new EntityAccessStrategyAdapter( this, ccs, settings );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/RegionFactoryCacheProviderBridge.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/RegionFactoryCacheProviderBridge.java
deleted file mode 100644
index 47f756678e..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/RegionFactoryCacheProviderBridge.java
+++ /dev/null
@@ -1,125 +0,0 @@
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
-package org.hibernate.cache.internal.bridge;
-
-import java.util.Properties;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.internal.NoCacheProvider;
-import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.CacheProvider;
-import org.hibernate.cache.spi.CollectionRegion;
-import org.hibernate.cache.spi.EntityRegion;
-import org.hibernate.cache.spi.QueryResultsRegion;
-import org.hibernate.cache.spi.RegionFactory;
-import org.hibernate.cache.spi.TimestampsRegion;
-import org.hibernate.cache.spi.access.AccessType;
-import org.hibernate.cfg.Environment;
-import org.hibernate.cfg.Settings;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.internal.util.config.ConfigurationHelper;
-
-/**
- * Acts as a bridge between the {@link RegionFactory} contract and the older
- * {@link CacheProvider} contract.
- *
- * @author Steve Ebersole
- */
-public class RegionFactoryCacheProviderBridge implements RegionFactory {
-	public static final String DEF_PROVIDER = NoCacheProvider.class.getName();
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       RegionFactoryCacheProviderBridge.class.getName());
-
-	private CacheProvider cacheProvider;
-	private Settings settings;
-
-	public RegionFactoryCacheProviderBridge(Properties properties) {
-		String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, DEF_PROVIDER );
-        LOG.cacheProvider(providerClassName);
-		try {
-			cacheProvider = ( CacheProvider ) ReflectHelper.classForName( providerClassName ).newInstance();
-		}
-		catch ( Exception cnfe ) {
-			throw new CacheException( "could not instantiate CacheProvider [" + providerClassName + "]", cnfe );
-		}
-	}
-
-	public void start(Settings settings, Properties properties) throws CacheException {
-		this.settings = settings;
-		cacheProvider.start( properties );
-	}
-
-	public void stop() {
-		cacheProvider.stop();
-		cacheProvider = null;
-	}
-
-	public boolean isMinimalPutsEnabledByDefault() {
-		return cacheProvider.isMinimalPutsEnabledByDefault();
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public AccessType getDefaultAccessType() {
-		// we really have no idea
-		return null;
-	}
-
-	public long nextTimestamp() {
-		return cacheProvider.nextTimestamp();
-	}
-
-	public CacheProvider getCacheProvider() {
-		return cacheProvider;
-	}
-
-	public EntityRegion buildEntityRegion(
-			String regionName,
-			Properties properties,
-			CacheDataDescription metadata) throws CacheException {
-		return new EntityRegionAdapter( cacheProvider.buildCache( regionName, properties ), settings, metadata );
-	}
-
-	public CollectionRegion buildCollectionRegion(
-			String regionName,
-			Properties properties,
-			CacheDataDescription metadata) throws CacheException {
-		return new CollectionRegionAdapter( cacheProvider.buildCache( regionName, properties ), settings, metadata );
-	}
-
-	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
-		return new QueryResultsRegionAdapter( cacheProvider.buildCache( regionName, properties ), settings );
-	}
-
-	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
-		return new TimestampsRegionAdapter( cacheProvider.buildCache( regionName, properties ), settings );
-	}
-
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/TimestampsRegionAdapter.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/TimestampsRegionAdapter.java
deleted file mode 100644
index 1f05acf9ce..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/TimestampsRegionAdapter.java
+++ /dev/null
@@ -1,39 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
-
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.TimestampsRegion;
-import org.hibernate.cfg.Settings;
-
-/**
- * Adapter specifically bridging {@link TimestampsRegion} to {@link org.hibernate.cache.spi.Cache}.
-*
-* @author Steve Ebersole
- */
-public class TimestampsRegionAdapter extends BaseGeneralDataRegionAdapter implements TimestampsRegion {
-	protected TimestampsRegionAdapter(Cache underlyingCache, Settings settings) {
-		super( underlyingCache, settings );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/AbstractJndiBoundCacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/AbstractJndiBoundCacheProvider.java
deleted file mode 100644
index a063366074..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/AbstractJndiBoundCacheProvider.java
+++ /dev/null
@@ -1,111 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Properties;
-import javax.naming.Context;
-import javax.naming.InitialContext;
-import javax.naming.NamingException;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.cfg.Environment;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.jndi.JndiHelper;
-
-import org.jboss.logging.Logger;
-
-/**
- * Support for CacheProvider implementations which are backed by caches bound
- * into JNDI namespace.
- *
- * @author Steve Ebersole
- */
-public abstract class AbstractJndiBoundCacheProvider implements CacheProvider {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       AbstractJndiBoundCacheProvider.class.getName());
-
-	private Object cache;
-
-	protected void prepare(Properties properties) {
-		// Do nothing; subclasses may override.
-	}
-
-	protected void release() {
-		// Do nothing; subclasses may override.
-	}
-
-	/**
-	 * Callback to perform any necessary initialization of the underlying cache implementation during SessionFactory
-	 * construction.
-	 *
-	 * @param properties current configuration settings.
-	 */
-	public final void start(Properties properties) throws CacheException {
-		String jndiNamespace = properties.getProperty( Environment.CACHE_NAMESPACE );
-		if ( StringHelper.isEmpty( jndiNamespace ) ) {
-			throw new CacheException( "No JNDI namespace specified for cache" );
-		}
-		cache = locateCache( jndiNamespace, JndiHelper.extractJndiProperties( properties ) );
-		prepare( properties );
-	}
-
-	/**
-	 * Callback to perform any necessary cleanup of the underlying cache
-	 * implementation during SessionFactory.close().
-	 */
-	public final void stop() {
-		release();
-		cache = null;
-	}
-
-	private Object locateCache(String jndiNamespace, Properties jndiProperties) {
-
-		Context ctx = null;
-		try {
-			ctx = new InitialContext( jndiProperties );
-			return ctx.lookup( jndiNamespace );
-		}
-		catch (NamingException ne) {
-			String msg = "Unable to retreive Cache from JNDI [" + jndiNamespace + "]";
-            LOG.unableToRetrieveCache(jndiNamespace, ne.getMessage());
-			throw new CacheException( msg );
-		}
-		finally {
-			if ( ctx != null ) {
-				try {
-					ctx.close();
-				}
-				catch( NamingException ne ) {
-                    LOG.unableToReleaseContext(ne.getMessage());
-				}
-			}
-		}
-	}
-
-	public Object getCache() {
-		return cache;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/Cache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/Cache.java
deleted file mode 100644
index e591a50072..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/Cache.java
+++ /dev/null
@@ -1,132 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Map;
-
-import org.hibernate.cache.CacheException;
-
-/**
- * Implementors define a caching algorithm. All implementors
- * <b>must</b> be threadsafe.
- *
- * @deprecated As of 3.3; see <a href="package.html"/> for details.
- */
-public interface Cache {
-	/**
-	 * Get an item from the cache
-	 * @param key
-	 * @return the cached object or <tt>null</tt>
-	 * @throws org.hibernate.cache.CacheException
-	 */
-	public Object read(Object key) throws CacheException;
-	/**
-	 * Get an item from the cache, nontransactionally
-	 * @param key
-	 * @return the cached object or <tt>null</tt>
-	 * @throws CacheException
-	 */
-	public Object get(Object key) throws CacheException;
-	/**
-	 * Add an item to the cache, nontransactionally, with
-	 * failfast semantics
-	 * @param key
-	 * @param value
-	 * @throws CacheException
-	 */
-	public void put(Object key, Object value) throws CacheException;
-	/**
-	 * Add an item to the cache
-	 * @param key
-	 * @param value
-	 * @throws CacheException
-	 */
-	public void update(Object key, Object value) throws CacheException;
-	/**
-	 * Remove an item from the cache
-	 */
-	public void remove(Object key) throws CacheException;
-	/**
-	 * Clear the cache
-	 */
-	public void clear() throws CacheException;
-	/**
-	 * Clean up
-	 */
-	public void destroy() throws CacheException;
-	/**
-	 * If this is a clustered cache, lock the item
-	 */
-	public void lock(Object key) throws CacheException;
-	/**
-	 * If this is a clustered cache, unlock the item
-	 */
-	public void unlock(Object key) throws CacheException;
-	/**
-	 * Generate a timestamp
-	 */
-	public long nextTimestamp();
-	/**
-	 * Get a reasonable "lock timeout"
-	 */
-	public int getTimeout();
-	
-	/**
-	 * Get the name of the cache region
-	 */
-	public String getRegionName();
-
-	/**
-	 * The number of bytes is this cache region currently consuming in memory.
-	 *
-	 * @return The number of bytes consumed by this region; -1 if unknown or
-	 * unsupported.
-	 */
-	public long getSizeInMemory();
-
-	/**
-	 * The count of entries currently contained in the regions in-memory store.
-	 *
-	 * @return The count of entries in memory; -1 if unknown or unsupported.
-	 */
-	public long getElementCountInMemory();
-
-	/**
-	 * The count of entries currently contained in the regions disk store.
-	 *
-	 * @return The count of entries on disk; -1 if unknown or unsupported.
-	 */
-	public long getElementCountOnDisk();
-	
-	/**
-	 * optional operation
-	 */
-	public Map toMap();
-}
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheConcurrencyStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheConcurrencyStrategy.java
deleted file mode 100644
index ff593635fb..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheConcurrencyStrategy.java
+++ /dev/null
@@ -1,197 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Comparator;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.access.SoftLock;
-
-/**
- * Implementors manage transactional access to cached data. Transactions
- * pass in a timestamp indicating transaction start time. Two different
- * implementation patterns are provided for.<ul>
- * <li>A transaction-aware cache implementation might be wrapped by a
- * "synchronous" concurrency strategy, where updates to the cache are written
- * to the cache inside the transaction.</li>
- * <li>A non transaction-aware cache would be wrapped by an "asynchronous"
- * concurrency strategy, where items are merely "soft locked" during the 
- * transaction and then updated during the "after transaction completion"
- * phase; the soft lock is not an actual lock on the database row -
- * only upon the cached representation of the item.</li>
- * </ul>
- * <p/>
- * In terms of entity caches, the expected call sequences are: <ul>
- * <li><b>DELETES</b> : {@link #lock} -> {@link #evict} -> {@link #release}</li>
- * <li><b>UPDATES</b> : {@link #lock} -> {@link #update} -> {@link #afterUpdate}</li>
- * <li><b>INSERTS</b> : {@link #insert} -> {@link #afterInsert}</li>
- * </ul>
- * <p/>
- * In terms of collection caches, all modification actions actually just
- * invalidate the entry(s).  The call sequence here is:
- * {@link #lock} -> {@link #evict} -> {@link #release}
- * <p/>
- * Note that, for an asynchronous cache, cache invalidation must be a two 
- * step process (lock->release, or lock-afterUpdate), since this is the only 
- * way to guarantee consistency with the database for a nontransactional cache
- * implementation. For a synchronous cache, cache invalidation is a single 
- * step process (evict, or update). Hence, this interface defines a three
- * step process, to cater for both models.
- * <p/>
- * Note that query result caching does not go through a concurrency strategy; they
- * are managed directly against the underlying {@link Cache cache regions}.
- *
- * @deprecated As of 3.3; see <a href="package.html"/> for details.
- */
-public interface CacheConcurrencyStrategy {
-
-	/**
-	 * Attempt to retrieve an object from the cache. Mainly used in attempting
-	 * to resolve entities/collections from the second level cache.
-	 *
-	 * @param key
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @return the cached object or <tt>null</tt>
-	 * @throws org.hibernate.cache.CacheException
-	 */
-	public Object get(Object key, long txTimestamp) throws CacheException;
-
-	/**
-	 * Attempt to cache an object, after loading from the database.
-	 *
-	 * @param key
-	 * @param value
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @param version the item version number
-	 * @param versionComparator a comparator used to compare version numbers
-	 * @param minimalPut indicates that the cache should avoid a put is the item is already cached
-	 * @return <tt>true</tt> if the object was successfully cached
-	 * @throws CacheException
-	 */
-	public boolean put(
-			Object key, 
-			Object value, 
-			long txTimestamp, 
-			Object version, 
-			Comparator versionComparator,
-			boolean minimalPut) 
-	throws CacheException;
-
-	/**
-	 * We are going to attempt to update/delete the keyed object. This
-	 * method is used by "asynchronous" concurrency strategies.
-	 * <p/>
-	 * The returned object must be passed back to release(), to release the
-	 * lock. Concurrency strategies which do not support client-visible
-	 * locks may silently return null.
-	 * 
-	 * @param key
-	 * @param version 
-	 * @throws CacheException
-	 */
-	public SoftLock lock(Object key, Object version) throws CacheException;
-
-	/**
-	 * Called after an item has become stale (before the transaction completes).
-	 * This method is used by "synchronous" concurrency strategies.
-	 */
-	public void evict(Object key) throws CacheException;
-
-	/**
-	 * Called after an item has been updated (before the transaction completes),
-	 * instead of calling evict().
-	 * This method is used by "synchronous" concurrency strategies.
-	 */
-	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException;
-
-	/**
-	 * Called after an item has been inserted (before the transaction completes),
-	 * instead of calling evict().
-	 * This method is used by "synchronous" concurrency strategies.
-	 */
-	public boolean insert(Object key, Object value, Object currentVersion) throws CacheException;
-	
-	
-	/**
-	 * Called when we have finished the attempted update/delete (which may or 
-	 * may not have been successful), after transaction completion.
-	 * This method is used by "asynchronous" concurrency strategies.
-	 * @param key
-	 * @throws CacheException
-	 */
-	public void release(Object key, SoftLock lock) throws CacheException;
-	/**
-	 * Called after an item has been updated (after the transaction completes),
-	 * instead of calling release().
-	 * This method is used by "asynchronous" concurrency strategies.
-	 */
-	public boolean afterUpdate(Object key, Object value, Object version, SoftLock lock)
-	throws CacheException;
-	/**
-	 * Called after an item has been inserted (after the transaction completes),
-	 * instead of calling release().
-	 * This method is used by "asynchronous" concurrency strategies.
-	 */
-	public boolean afterInsert(Object key, Object value, Object version) 
-	throws CacheException;
-	
-	
-	/**
-	 * Evict an item from the cache immediately (without regard for transaction
-	 * isolation).
-	 * @param key
-	 * @throws CacheException
-	 */
-	public void remove(Object key) throws CacheException;
-	/**
-	 * Evict all items from the cache immediately.
-	 * @throws CacheException
-	 */
-	public void clear() throws CacheException;
-	/**
-	 * Clean up all resources.
-	 */
-	public void destroy();
-	/**
-	 * Set the underlying cache implementation.
-	 * @param cache
-	 */
-	public void setCache(Cache cache);
-		
-	/**
-	 * Get the cache region name
-	 */
-	public String getRegionName();
-	
-	/**
-	 * Get the wrapped cache implementation
-	 */
-	public Cache getCache();
-}
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheProvider.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheProvider.java
deleted file mode 100644
index 2e28daf04f..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheProvider.java
+++ /dev/null
@@ -1,68 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Properties;
-
-import org.hibernate.cache.CacheException;
-
-/**
- * Support for pluggable caches.
- *
- * @author Gavin King
- * @deprecated As of 3.3; see <a href="package.html"/> for details.
- */
-public interface CacheProvider {
-
-	/**
-	 * Configure the cache
-	 *
-	 * @param regionName the name of the cache region
-	 * @param properties configuration settings
-	 * @throws org.hibernate.cache.CacheException
-	 */
-	public Cache buildCache(String regionName, Properties properties) throws CacheException;
-
-	/**
-	 * Generate a timestamp
-	 */
-	public long nextTimestamp();
-
-	/**
-	 * Callback to perform any necessary initialization of the underlying cache implementation
-	 * during SessionFactory construction.
-	 *
-	 * @param properties current configuration settings.
-	 */
-	public void start(Properties properties) throws CacheException;
-
-	/**
-	 * Callback to perform any necessary cleanup of the underlying cache implementation
-	 * during SessionFactory.close().
-	 */
-	public void stop();
-	
-	public boolean isMinimalPutsEnabledByDefault();
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/NonstrictReadWriteCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/NonstrictReadWriteCache.java
deleted file mode 100644
index efd34e0b1c..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/NonstrictReadWriteCache.java
+++ /dev/null
@@ -1,178 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Comparator;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Caches data that is sometimes updated without ever locking the cache.
- * If concurrent access to an item is possible, this concurrency strategy
- * makes no guarantee that the item returned from the cache is the latest
- * version available in the database. Configure your cache timeout accordingly!
- * This is an "asynchronous" concurrency strategy.
- *
- * @author Gavin King
- * @see ReadWriteCache for a much stricter algorithm
- */
-@Deprecated
-public class NonstrictReadWriteCache implements CacheConcurrencyStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       NonstrictReadWriteCache.class.getName());
-
-	private Cache cache;
-
-	public NonstrictReadWriteCache() {
-	}
-
-	public void setCache(Cache cache) {
-		this.cache = cache;
-	}
-
-	public Cache getCache() {
-		return cache;
-	}
-
-	/**
-	 * Get the most recent version, if available.
-	 */
-	public Object get(Object key, long txTimestamp) throws CacheException {
-        LOG.debugf("Cache lookup: %s", key);
-
-		Object result = cache.get( key );
-        if (result != null) LOG.debugf("Cache hit: %s", key);
-        else LOG.debugf("Cache miss: %s", key);
-		return result;
-	}
-
-	/**
-	 * Add an item to the cache.
-	 */
-	public boolean put(
-			Object key,
-	        Object value,
-	        long txTimestamp,
-	        Object version,
-	        Comparator versionComparator,
-	        boolean minimalPut) throws CacheException {
-		if ( minimalPut && cache.get( key ) != null ) {
-            LOG.debugf("Item already cached: %s", key);
-			return false;
-		}
-        LOG.debugf("Caching: %s", key);
-
-		cache.put( key, value );
-		return true;
-
-	}
-
-	/**
-	 * Do nothing.
-	 *
-	 * @return null, no lock
-	 */
-	public SoftLock lock(Object key, Object version) throws CacheException {
-		return null;
-	}
-
-	public void remove(Object key) throws CacheException {
-        LOG.debugf("Removing: %s", key);
-		cache.remove( key );
-	}
-
-	public void clear() throws CacheException {
-        LOG.debugf("Clearing");
-		cache.clear();
-	}
-
-	public void destroy() {
-		try {
-			cache.destroy();
-		}
-		catch ( Exception e ) {
-            LOG.unableToDestroyCache(e.getMessage());
-		}
-	}
-
-	/**
-	 * Invalidate the item
-	 */
-	public void evict(Object key) throws CacheException {
-        LOG.debugf("Invalidating: %s", key);
-		cache.remove( key );
-	}
-
-	/**
-	 * Invalidate the item
-	 */
-	public boolean insert(Object key, Object value, Object currentVersion) {
-		return false;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) {
-		evict( key );
-		return false;
-	}
-
-	/**
-	 * Invalidate the item (again, for safety).
-	 */
-	public void release(Object key, SoftLock lock) throws CacheException {
-        LOG.debugf("Invalidating: %s", key);
-		cache.remove( key );
-	}
-
-	/**
-	 * Invalidate the item (again, for safety).
-	 */
-	public boolean afterUpdate(Object key, Object value, Object version, SoftLock lock) throws CacheException {
-		release( key, lock );
-		return false;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
-		return false;
-	}
-
-	public String getRegionName() {
-		return cache.getRegionName();
-	}
-
-	@Override
-    public String toString() {
-		return cache + "(nonstrict-read-write)";
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/OptimisticCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/OptimisticCache.java
deleted file mode 100644
index 40dcaf6b9b..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/OptimisticCache.java
+++ /dev/null
@@ -1,87 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-/**
- * A contract for transactional cache implementations which support
- * optimistic locking of items within the cache.
- * <p/>
- * The optimistic locking capabilities are only utilized for
- * the entity cache regions.
- * <p/>
- * Unlike the methods on the {@link Cache} interface, all the methods
- * here will only ever be called from access scenarios where versioned
- * data is actually a possibility (i.e., entity data).  Be sure to consult
- * with {@link OptimisticCacheSource#isVersioned()} to determine whether
- * versioning is actually in effect.
- *
- * @author Steve Ebersole
- */
-public interface OptimisticCache extends Cache {
-	/**
-	 * Indicates the "source" of the cached data.  Currently this will
-	 * only ever represent an {@link org.hibernate.persister.entity.EntityPersister}.
-	 * <p/>
-	 * Made available to the cache so that it can access certain information
-	 * about versioning strategy.
-	 *
-	 * @param source The source.
-	 */
-	public void setSource(OptimisticCacheSource source);
-
-	/**
-	 * Called during {@link CacheConcurrencyStrategy#insert} processing for
-	 * transactional strategies.  Indicates we have just performed an insert
-	 * into the DB and now need to cache that entity's data.
-	 *
-	 * @param key The cache key.
-	 * @param value The data to be cached.
-	 * @param currentVersion The entity's version; or null if not versioned.
-	 */
-	public void writeInsert(Object key, Object value, Object currentVersion);
-
-	/**
-	 * Called during {@link CacheConcurrencyStrategy#update} processing for
-	 * transactional strategies.  Indicates we have just performed an update
-	 * against the DB and now need to cache the updated state.
-	 *
-	 * @param key The cache key.
-	 * @param value The data to be cached.
-	 * @param currentVersion The entity's current version
-	 * @param previousVersion The entity's previous version (before the update);
-	 * or null if not versioned.
-	 */
-	public void writeUpdate(Object key, Object value, Object currentVersion, Object previousVersion);
-
-	/**
-	 * Called during {@link CacheConcurrencyStrategy#put} processing for
-	 * transactional strategies.  Indicates we have just loaded an entity's
-	 * state from the database and need it cached.
-	 *
-	 * @param key The cache key.
-	 * @param value The data to be cached.
-	 * @param currentVersion The entity's version; or null if not versioned.
-	 */
-	public void writeLoad(Object key, Object value, Object currentVersion);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/ReadOnlyCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/ReadOnlyCache.java
deleted file mode 100644
index cf8d62d945..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/ReadOnlyCache.java
+++ /dev/null
@@ -1,165 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Comparator;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Caches data that is never updated.
- * @see org.hibernate.cache.spi.CacheConcurrencyStrategy
- */
-@Deprecated
-public class ReadOnlyCache implements CacheConcurrencyStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ReadOnlyCache.class.getName());
-
-	private Cache cache;
-
-	public ReadOnlyCache() {}
-
-	public void setCache(Cache cache) {
-		this.cache=cache;
-	}
-
-	public Cache getCache() {
-		return cache;
-	}
-
-	public String getRegionName() {
-		return cache.getRegionName();
-	}
-
-	public synchronized Object get(Object key, long timestamp) throws CacheException {
-		Object result = cache.get(key);
-        if (result != null) LOG.debugf("Cache hit: %s", key);
-		return result;
-	}
-
-	/**
-	 * Unsupported!
-	 */
-	public SoftLock lock(Object key, Object version) {
-        LOG.invalidEditOfReadOnlyItem(key);
-		throw new UnsupportedOperationException("Can't write to a readonly object");
-	}
-
-	public synchronized boolean put(
-			Object key,
-			Object value,
-			long timestamp,
-			Object version,
-			Comparator versionComparator,
-			boolean minimalPut)
-	throws CacheException {
-		if ( minimalPut && cache.get(key)!=null ) {
-            LOG.debugf("Item already cached: %s", key);
-			return false;
-		}
-        LOG.debugf("Caching: %s", key);
-		cache.put(key, value);
-		return true;
-	}
-
-	/**
-	 * Unsupported!
-	 */
-	public void release(Object key, SoftLock lock) {
-        LOG.invalidEditOfReadOnlyItem(key);
-		//throw new UnsupportedOperationException("Can't write to a readonly object");
-	}
-
-	public void clear() throws CacheException {
-		cache.clear();
-	}
-
-	public void remove(Object key) throws CacheException {
-		cache.remove(key);
-	}
-
-	public void destroy() {
-		try {
-			cache.destroy();
-		}
-		catch (Exception e) {
-            LOG.unableToDestroyCache(e.getMessage());
-		}
-	}
-
-	/**
-	 * Unsupported!
-	 */
-	public boolean afterUpdate(Object key, Object value, Object version, SoftLock lock) throws CacheException {
-        LOG.invalidEditOfReadOnlyItem(key);
-		throw new UnsupportedOperationException("Can't write to a readonly object");
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
-        LOG.debugf("Caching after insert: %s", key);
-		cache.update(key, value);
-		return true;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public void evict(Object key) throws CacheException {
-		// noop
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean insert(Object key, Object value, Object currentVersion) {
-		return false;
-	}
-
-	/**
-	 * Unsupported!
-	 */
-	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) {
-        LOG.invalidEditOfReadOnlyItem(key);
-		throw new UnsupportedOperationException("Can't write to a readonly object");
-	}
-
-	@Override
-    public String toString() {
-		return cache + "(read-only)";
-	}
-
-}
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/ReadWriteCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/ReadWriteCache.java
deleted file mode 100644
index 89ab5e0f97..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/ReadWriteCache.java
+++ /dev/null
@@ -1,500 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.io.Serializable;
-import java.util.Comparator;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Caches data that is sometimes updated while maintaining the semantics of
- * "read committed" isolation level. If the database is set to "repeatable
- * read", this concurrency strategy <em>almost</em> maintains the semantics.
- * Repeatable read isolation is compromised in the case of concurrent writes.
- * This is an "asynchronous" concurrency strategy.<br>
- * <br>
- * If this strategy is used in a cluster, the underlying cache implementation
- * must support distributed hard locks (which are held only momentarily). This
- * strategy also assumes that the underlying cache implementation does not do
- * asynchronous replication and that state has been fully replicated as soon
- * as the lock is released.
- *
- * @see NonstrictReadWriteCache for a faster algorithm
- * @see org.hibernate.cache.spi.CacheConcurrencyStrategy
- */
-@Deprecated
-public class ReadWriteCache implements CacheConcurrencyStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ReadWriteCache.class.getName());
-
-	private Cache cache;
-	private int nextLockId;
-
-	public ReadWriteCache() {}
-
-	public void setCache(Cache cache) {
-		this.cache=cache;
-	}
-
-	public Cache getCache() {
-		return cache;
-	}
-
-	public String getRegionName() {
-		return cache.getRegionName();
-	}
-
-	/**
-	 * Generate an id for a new lock. Uniqueness per cache instance is very
-	 * desirable but not absolutely critical. Must be called from one of the
-	 * synchronized methods of this class.
-	 */
-	private int nextLockId() {
-		if (nextLockId==Integer.MAX_VALUE) nextLockId = Integer.MIN_VALUE;
-		return nextLockId++;
-	}
-
-	/**
-	 * Do not return an item whose timestamp is later than the current
-	 * transaction timestamp. (Otherwise we might compromise repeatable
-	 * read unnecessarily.) Do not return an item which is soft-locked.
-	 * Always go straight to the database instead.<br>
-	 * <br>
-	 * Note that since reading an item from that cache does not actually
-	 * go to the database, it is possible to see a kind of phantom read
-	 * due to the underlying row being updated after we have read it
-	 * from the cache. This would not be possible in a lock-based
-	 * implementation of repeatable read isolation. It is also possible
-	 * to overwrite changes made and committed by another transaction
-	 * after the current transaction read the item from the cache. This
-	 * problem would be caught by the update-time version-checking, if
-	 * the data is versioned or timestamped.
-	 */
-	public synchronized Object get(Object key, long txTimestamp) throws CacheException {
-        LOG.debugf("Cache lookup: %s", key);
-		Lockable lockable = (Lockable)cache.get(key);
-		boolean gettable = lockable != null && lockable.isGettable(txTimestamp);
-		if (gettable) {
-            LOG.debugf("Cache hit: %s", key);
-            return ((Item)lockable).getValue();
-        }
-        if (lockable == null) LOG.debugf("Cache miss: %s", key);
-        else LOG.debugf("Cached item was locked: %s", key);
-        return null;
-	}
-
-	/**
-	 * Stop any other transactions reading or writing this item to/from
-	 * the cache. Send them straight to the database instead. (The lock
-	 * does time out eventually.) This implementation tracks concurrent
-	 * locks of transactions which simultaneously attempt to write to an
-	 * item.
-	 */
-	public synchronized SoftLock lock(Object key, Object version) throws CacheException {
-        LOG.debugf("Invalidating: %s", key);
-		try {
-			cache.lock(key);
-
-			Lockable lockable = (Lockable) cache.get(key);
-			long timeout = cache.nextTimestamp() + cache.getTimeout();
-			final Lock lock = (lockable==null) ?
-				new Lock( timeout, nextLockId(), version ) :
-				lockable.lock( timeout, nextLockId() );
-			cache.update(key, lock);
-			return lock;
-		}
-		finally {
-			cache.unlock(key);
-		}
-
-	}
-
-	/**
-	 * Do not add an item to the cache unless the current transaction
-	 * timestamp is later than the timestamp at which the item was
-	 * invalidated. (Otherwise, a stale item might be re-added if the
-	 * database is operating in repeatable read isolation mode.)
-	 * For versioned data, don't add the item unless it is the later
-	 * version.
-	 */
-	public synchronized boolean put(
-			Object key,
-			Object value,
-			long txTimestamp,
-			Object version,
-			Comparator versionComparator,
-			boolean minimalPut)
-	throws CacheException {
-        LOG.debugf("Caching: %s", key);
-
-		try {
-			cache.lock(key);
-
-			Lockable lockable = (Lockable) cache.get(key);
-
-			boolean puttable = lockable==null ||
-				lockable.isPuttable(txTimestamp, version, versionComparator);
-
-			if (puttable) {
-				cache.put( key, new Item( value, version, cache.nextTimestamp() ) );
-                LOG.debugf("Cached: %s", key);
-				return true;
-			}
-            if (lockable.isLock()) LOG.debugf("Cached item was locked: %s", key);
-            else LOG.debugf("Item already cached: %s", key);
-            return false;
-		}
-		finally {
-			cache.unlock(key);
-		}
-	}
-
-	/**
-	 * decrement a lock and put it back in the cache
-	 */
-	private void decrementLock(Object key, Lock lock) throws CacheException {
-		//decrement the lock
-		lock.unlock( cache.nextTimestamp() );
-		cache.update(key, lock);
-	}
-
-	/**
-	 * Release the soft lock on the item. Other transactions may now
-	 * re-cache the item (assuming that no other transaction holds a
-	 * simultaneous lock).
-	 */
-	public synchronized void release(Object key, SoftLock clientLock) throws CacheException {
-        LOG.debugf("Releasing: %s", key);
-
-		try {
-			cache.lock(key);
-
-			Lockable lockable = (Lockable) cache.get(key);
-			if ( isUnlockable(clientLock, lockable) ) {
-				decrementLock(key, (Lock) lockable);
-			}
-			else {
-				handleLockExpiry(key);
-			}
-		}
-		finally {
-			cache.unlock(key);
-		}
-	}
-
-	void handleLockExpiry(Object key) throws CacheException {
-        LOG.expired(key);
-		long ts = cache.nextTimestamp() + cache.getTimeout();
-		// create new lock that times out immediately
-		Lock lock = new Lock( ts, nextLockId(), null );
-		lock.unlock(ts);
-		cache.update(key, lock);
-	}
-
-	public void clear() throws CacheException {
-		cache.clear();
-	}
-
-	public void remove(Object key) throws CacheException {
-		cache.remove(key);
-	}
-
-	public void destroy() {
-		try {
-			cache.destroy();
-		}
-		catch (Exception e) {
-            LOG.unableToDestroyCache(e.getMessage());
-		}
-	}
-
-	/**
-	 * Re-cache the updated state, if and only if there there are
-	 * no other concurrent soft locks. Release our lock.
-	 */
-	public synchronized boolean afterUpdate(Object key, Object value, Object version, SoftLock clientLock)
-	throws CacheException {
-
-        LOG.debugf("Updating: %s", key);
-
-		try {
-			cache.lock(key);
-
-			Lockable lockable = (Lockable) cache.get(key);
-			if ( isUnlockable(clientLock, lockable) ) {
-				Lock lock = (Lock) lockable;
-				if ( lock.wasLockedConcurrently() ) {
-					// just decrement the lock, don't recache
-					// (we don't know which transaction won)
-					decrementLock(key, lock);
-					return false;
-				}
-                // recache the updated state
-                cache.update(key, new Item(value, version, cache.nextTimestamp()));
-                LOG.debugf("Updated: %s", key);
-                return true;
-			}
-            handleLockExpiry(key);
-            return false;
-		}
-		finally {
-			cache.unlock(key);
-		}
-	}
-
-	/**
-	 * Add the new item to the cache, checking that no other transaction has
-	 * accessed the item.
-	 */
-	public synchronized boolean afterInsert(Object key, Object value, Object version)
-	throws CacheException {
-
-        LOG.debugf("Inserting: %s", key);
-		try {
-			cache.lock(key);
-
-			Lockable lockable = (Lockable) cache.get(key);
-			if (lockable==null) {
-				cache.update( key, new Item( value, version, cache.nextTimestamp() ) );
-                LOG.debugf("Inserted: %s", key);
-				return true;
-			}
-            return false;
-		}
-		finally {
-			cache.unlock(key);
-		}
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public void evict(Object key) throws CacheException {
-		// noop
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean insert(Object key, Object value, Object currentVersion) {
-		return false;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) {
-		return false;
-	}
-
-	/**
-	 * Is the client's lock commensurate with the item in the cache?
-	 * If it is not, we know that the cache expired the original
-	 * lock.
-	 */
-	private boolean isUnlockable(SoftLock clientLock, Lockable myLock)
-	throws CacheException {
-		//null clientLock is remotely possible but will never happen in practice
-		return myLock!=null &&
-			myLock.isLock() &&
-			clientLock!=null &&
-			( (Lock) clientLock ).getId()==( (Lock) myLock ).getId();
-	}
-
-	public static interface Lockable {
-		public Lock lock(long timeout, int id);
-		public boolean isLock();
-		public boolean isGettable(long txTimestamp);
-		public boolean isPuttable(long txTimestamp, Object newVersion, Comparator comparator);
-	}
-
-	/**
-	 * An item of cached data, timestamped with the time it was cached,.
-	 * @see ReadWriteCache
-	 */
-	public static final class Item implements Serializable, Lockable {
-
-		private final long freshTimestamp;
-		private final Object value;
-		private final Object version;
-
-		public Item(Object value, Object version, long currentTimestamp) {
-			this.value = value;
-			this.version = version;
-			freshTimestamp = currentTimestamp;
-		}
-		/**
-		 * The timestamp on the cached data
-		 */
-		public long getFreshTimestamp() {
-			return freshTimestamp;
-		}
-		/**
-		 * The actual cached data
-		 */
-		public Object getValue() {
-			return value;
-		}
-
-		/**
-		 * Lock the item
-		 */
-		public Lock lock(long timeout, int id) {
-			return new Lock(timeout, id, version);
-		}
-		/**
-		 * Not a lock!
-		 */
-		public boolean isLock() {
-			return false;
-		}
-		/**
-		 * Is this item visible to the timestamped
-		 * transaction?
-		 */
-		public boolean isGettable(long txTimestamp) {
-			return freshTimestamp < txTimestamp;
-		}
-
-		/**
-		 * Don't overwite already cached items
-		 */
-		public boolean isPuttable(long txTimestamp, Object newVersion, Comparator comparator) {
-			// we really could refresh the item if it
-			// is not a lock, but it might be slower
-			//return freshTimestamp < txTimestamp
-			return version!=null && comparator.compare(version, newVersion) < 0;
-		}
-
-		@Override
-        public String toString() {
-			return "Item{version=" + version +
-				",freshTimestamp=" + freshTimestamp;
-		}
-	}
-
-	/**
-	 * A soft lock which supports concurrent locking,
-	 * timestamped with the time it was released
-	 * @author Gavin King
-	 */
-	public static final class Lock implements Serializable, Lockable, SoftLock {
-		private long unlockTimestamp = -1;
-		private int multiplicity = 1;
-		private boolean concurrentLock = false;
-		private long timeout;
-		private final int id;
-		private final Object version;
-
-		public Lock(long timeout, int id, Object version) {
-			this.timeout = timeout;
-			this.id = id;
-			this.version = version;
-		}
-
-		public long getUnlockTimestamp() {
-			return unlockTimestamp;
-		}
-		/**
-		 * Increment the lock, setting the
-		 * new lock timeout
-		 */
-		public Lock lock(long timeout, int id) {
-			concurrentLock = true;
-			multiplicity++;
-			this.timeout = timeout;
-			return this;
-		}
-		/**
-		 * Decrement the lock, setting the unlock
-		 * timestamp if now unlocked
-		 * @param currentTimestamp
-		 */
-		public void unlock(long currentTimestamp) {
-			if ( --multiplicity == 0 ) {
-				unlockTimestamp = currentTimestamp;
-			}
-		}
-
-		/**
-		 * Can the timestamped transaction re-cache this
-		 * locked item now?
-		 */
-		public boolean isPuttable(long txTimestamp, Object newVersion, Comparator comparator) {
-			if (timeout < txTimestamp) return true;
-			if (multiplicity>0) return false;
-			return version==null ?
-				unlockTimestamp < txTimestamp :
-				comparator.compare(version, newVersion) < 0; //by requiring <, we rely on lock timeout in the case of an unsuccessful update!
-		}
-
-		/**
-		 * Was this lock held concurrently by multiple
-		 * transactions?
-		 */
-		public boolean wasLockedConcurrently() {
-			return concurrentLock;
-		}
-		/**
-		 * Yes, this is a lock
-		 */
-		public boolean isLock() {
-			return true;
-		}
-		/**
-		 * locks are not returned to the client!
-		 */
-		public boolean isGettable(long txTimestamp) {
-			return false;
-		}
-
-		public int getId() { return id; }
-
-		@Override
-        public String toString() {
-			return "Lock{id=" + id +
-				",version=" + version +
-				",multiplicity=" + multiplicity +
-				",unlockTimestamp=" + unlockTimestamp;
-		}
-
-	}
-
-	@Override
-    public String toString() {
-		return cache + "(read-write)";
-	}
-
-}
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
index 15df7d6f39..8cce5ee857 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
@@ -1,151 +1,146 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
 import org.hibernate.service.Service;
 
 /**
  * Contract for building second level cache regions.
  * <p/>
  * Implementors should define a constructor in one of two forms:<ul>
  * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
  * <li>MyRegionFactoryImpl()</li>
  * </ul>
  * Use the first when we need to read config properties prior to
- * {@link #start(Settings, Properties)} being called.  For an example, have a look at
- * {@link org.hibernate.cache.internal.bridge.RegionFactoryCacheProviderBridge}
- * where we need the properties in order to determine which legacy
- * {@link CacheProvider} to use so that we can answer the
- * {@link #isMinimalPutsEnabledByDefault()} question for the
- * {@link org.hibernate.cfg.SettingsFactory}.
+ * {@link #start(Settings, Properties)} being called.
  *
  * @author Steve Ebersole
  */
 public interface RegionFactory extends Service {
 
 	/**
 	 * Lifecycle callback to perform any necessary initialization of the
 	 * underlying cache implementation(s).  Called exactly once during the
 	 * construction of a {@link org.hibernate.internal.SessionFactoryImpl}.
 	 *
 	 * @param settings The settings in effect.
 	 * @param properties The defined cfg properties
 	 *
 	 * @throws org.hibernate.cache.CacheException Indicates problems starting the L2 cache impl;
 	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
 	 * building.
 	 */
 	public void start(Settings settings, Properties properties) throws CacheException;
 
 	/**
 	 * Lifecycle callback to perform any necessary cleanup of the underlying
 	 * cache implementation(s).  Called exactly once during
 	 * {@link org.hibernate.SessionFactory#close}.
 	 */
 	public void stop();
 
 	/**
 	 * By default should we perform "minimal puts" when using this second
 	 * level cache implementation?
 	 *
 	 * @return True if "minimal puts" should be performed by default; false
 	 *         otherwise.
 	 */
 	public boolean isMinimalPutsEnabledByDefault();
 
 	/**
 	 * Get the default access type for {@link EntityRegion entity} and
 	 * {@link CollectionRegion collection} regions.
 	 *
 	 * @return This factory's default access type.
 	 */
 	public AccessType getDefaultAccessType();
 
 	/**
 	 * Generate a timestamp.
 	 * <p/>
 	 * This is generally used for cache content locking/unlocking purposes
 	 * depending upon the access-strategy being used.
 	 *
 	 * @return The generated timestamp.
 	 */
 	public long nextTimestamp();
 
 	/**
 	 * Build a cache region specialized for storing entity data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing collection data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing query results
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing update-timestamps data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 *
 	 * @return The built region
 	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalCache.java
deleted file mode 100644
index f408404f30..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionalCache.java
+++ /dev/null
@@ -1,183 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.spi;
-
-import java.util.Comparator;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Support for fully transactional cache implementations like
- * JBoss TreeCache. Note that this might be a less scalable
- * concurrency strategy than <tt>ReadWriteCache</tt>. This is
- * a "synchronous" concurrency strategy.
- *
- * @author Gavin King
- */
-@Deprecated
-public class TransactionalCache implements CacheConcurrencyStrategy {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TransactionalCache.class.getName());
-
-	private Cache cache;
-
-	public String getRegionName() {
-		return cache.getRegionName();
-	}
-
-	public Object get(Object key, long txTimestamp) throws CacheException {
-        LOG.debugf("Cache lookup: %s", key);
-		Object result = cache.read( key );
-        if (result == null) LOG.debugf("Cache miss: %s", key);
-        else LOG.debugf("Cache hit: %s", key);
-		return result;
-	}
-
-	public boolean put(
-			Object key,
-	        Object value,
-	        long txTimestamp,
-	        Object version,
-	        Comparator versionComparator,
-	        boolean minimalPut) throws CacheException {
-		if ( minimalPut && cache.read( key ) != null ) {
-            LOG.debugf("Item already cached: %s", key);
-			return false;
-		}
-        LOG.debugf("Caching: %s", key);
-		if ( cache instanceof OptimisticCache ) {
-			( ( OptimisticCache ) cache ).writeLoad( key, value, version );
-		}
-		else {
-			cache.put( key, value );
-		}
-		return true;
-	}
-
-	/**
-	 * Do nothing, returning null.
-	 */
-	public SoftLock lock(Object key, Object version) throws CacheException {
-		//noop
-		return null;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public void release(Object key, SoftLock clientLock) throws CacheException {
-		//noop
-	}
-
-	public boolean update(
-			Object key,
-	        Object value,
-	        Object currentVersion,
-	        Object previousVersion) throws CacheException {
-        LOG.debugf("Updating: %s", key);
-		if ( cache instanceof OptimisticCache ) {
-			( ( OptimisticCache ) cache ).writeUpdate( key, value, currentVersion, previousVersion );
-		}
-		else {
-			cache.update( key, value );
-		}
-		return true;
-	}
-
-	public boolean insert(
-			Object key,
-	        Object value,
-	        Object currentVersion) throws CacheException {
-        LOG.debugf("Inserting: %s", key);
-		if ( cache instanceof OptimisticCache ) {
-			( ( OptimisticCache ) cache ).writeInsert( key, value, currentVersion );
-		}
-		else {
-			cache.update( key, value );
-		}
-		return true;
-	}
-
-	public void evict(Object key) throws CacheException {
-		cache.remove( key );
-	}
-
-	public void remove(Object key) throws CacheException {
-        LOG.debugf("Removing: %s", key);
-		cache.remove( key );
-	}
-
-	public void clear() throws CacheException {
-        LOG.debugf("Clearing");
-		cache.clear();
-	}
-
-	public void destroy() {
-		try {
-			cache.destroy();
-		}
-		catch ( Exception e ) {
-            LOG.unableToDestroyCache(e.getMessage());
-		}
-	}
-
-	public void setCache(Cache cache) {
-		this.cache = cache;
-	}
-
-	public Cache getCache() {
-		return cache;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean afterInsert(
-			Object key,
-	        Object value,
-	        Object version) throws CacheException {
-		return false;
-	}
-
-	/**
-	 * Do nothing.
-	 */
-	public boolean afterUpdate(
-			Object key,
-	        Object value,
-	        Object version,
-	        SoftLock clientLock) throws CacheException {
-		return false;
-	}
-
-	@Override
-    public String toString() {
-		return cache + "(transactional)";
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
index faf27cc846..8ffb684d91 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
@@ -1,140 +1,107 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.cache.spi.access;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.EntityRegion;
 
 /**
  * Contract for managing transactional and concurrent access to cached entity
  * data.  The expected call sequences related to various operations are:<ul>
  * <li><b>INSERTS</b> : {@link #insert} -> {@link #afterInsert}</li>
  * <li><b>UPDATES</b> : {@link #lockItem} -> {@link #update} -> {@link #afterUpdate}</li>
  * <li><b>DELETES</b> : {@link #lockItem} -> {@link #remove} -> {@link #unlockItem}</li>
  * </ul>
  * <p/>
  * There is another usage pattern that is used to invalidate entries
  * after performing "bulk" HQL/SQL operations:
  * {@link #lockRegion} -> {@link #removeAll} -> {@link #unlockRegion}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface EntityRegionAccessStrategy extends RegionAccessStrategy{
 
 	/**
 	 * Get the wrapped entity cache region
 	 *
 	 * @return The underlying region
 	 */
 	public EntityRegion getRegion();
 
 	/**
 	 * Called after an item has been inserted (before the transaction completes),
 	 * instead of calling evict().
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param version The item's version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean insert(Object key, Object value, Object version) throws CacheException;
 
 	/**
 	 * Called after an item has been inserted (after the transaction completes),
 	 * instead of calling release().
 	 * This method is used by "asynchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param version The item's version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean afterInsert(Object key, Object value, Object version) throws CacheException;
 
 	/**
 	 * Called after an item has been updated (before the transaction completes),
 	 * instead of calling evict(). This method is used by "synchronous" concurrency
 	 * strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param currentVersion The item's current version value
 	 * @param previousVersion The item's previous version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException;
 
 	/**
 	 * Called after an item has been updated (after the transaction completes),
 	 * instead of calling release().  This method is used by "asynchronous"
 	 * concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param currentVersion The item's current version value
 	 * @param previousVersion The item's previous version value
 	 * @param lock The lock previously obtained from {@link #lockItem}
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException;
-
-	/**
-	 * Called after an item has become stale (before the transaction completes).
-	 * This method is used by "synchronous" concurrency strategies.
-	 *
-	 * @param key The key of the item to remove
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void remove(Object key) throws CacheException;
-
-	/**
-	 * Called to evict data from the entire region
-	 *
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void removeAll() throws CacheException;
-
-	/**
-	 * Forcibly evict an item from the cache immediately without regard for transaction
-	 * isolation.
-	 *
-	 * @param key The key of the item to remove
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void evict(Object key) throws CacheException;
-
-	/**
-	 * Forcibly evict all items from the cache immediately without regard for transaction
-	 * isolation.
-	 *
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void evictAll() throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index d502a96ba0..2d1becf941 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,531 +1,524 @@
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
 package org.hibernate.cfg;
 
 /**
  * @author Steve Ebersole
  */
 public interface AvailableSettings {
 	/**
 	 * Names a {@literal JNDI} namespace into which the {@link org.hibernate.SessionFactory} should be bound.
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Names the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} to use for obtaining
 	 * JDBC connections.  Can either reference an instance of
 	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} or a {@link Class} or {@link String}
 	 * reference to the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} implementation
 	 * class.
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 
 	/**
 	 * Names the {@literal JDBC} driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 
 	/**
 	 * Names the {@literal JDBC} connection url.
 	 */
 	public static final String URL ="hibernate.connection.url";
 
 	/**
 	 * Names the connection user.  This might mean one of 2 things in out-of-the-box Hibernate
 	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider}: <ul>
 	 *     <li>The username used to pass along to creating the JDBC connection</li>
 	 *     <li>The username used to obtain a JDBC connection from a data source</li>
 	 * </ul>
 	 */
 	public static final String USER ="hibernate.connection.username";
 
 	/**
 	 * Names the connection password.  See usage discussion on {@link #USER}
 	 */
 	public static final String PASS ="hibernate.connection.password";
 
 	/**
 	 * Names the {@literal JDBC} transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 
 	/**
 	 * Names the {@literal JDBC} autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 
 	/**
 	 * Maximum number of inactive connections for the built-in Hibernate connection pool.
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 
 	/**
 	 * Names a {@link javax.sql.DataSource}.  Can either reference a {@link javax.sql.DataSource} instance or
 	 * a {@literal JNDI} name under which to locate the {@link javax.sql.DataSource}.
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 
 	/**
 	 * Names a prefix used to define arbitrary JDBC connection properties.  These properties are passed along to
 	 * the {@literal JDBC} provider when creating a connection.
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * Names the {@literal JNDI} {@link javax.naming.InitialContext} class.
 	 *
 	 * @see javax.naming.Context#INITIAL_CONTEXT_FACTORY
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 
 	/**
 	 * Names the {@literal JNDI} provider/connection url
 	 *
 	 * @see javax.naming.Context#PROVIDER_URL
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 
 	/**
 	 * Names a prefix used to define arbitrary {@literal JNDI} {@link javax.naming.InitialContext} properties.  These
 	 * properties are passed along to {@link javax.naming.InitialContext#InitialContext(java.util.Hashtable)}
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 
 	/**
 	 * Names the Hibernate {@literal SQL} {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
 	 * Names any additional {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} implementations to
 	 * register with the standard {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}.
 	 */
 	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
 
 
 	/**
 	 * A default database schema (owner) name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
 	/**
 	 * A default database catalog name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	public static final String SHOW_SQL ="hibernate.show_sql";
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	public static final String FORMAT_SQL ="hibernate.format_sql";
 	/**
 	 * Add comments to the generated SQL
 	 */
 	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 	/**
 	 * The default batch size for batch fetching
 	 */
 	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 	/**
 	 * An XSLT resource used to generate "custom" XML
 	 */
 	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 	/**
 	 * Idle time before a C3P0 pooled connection is validated
 	 */
 	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
 	 */
 	public static final String PROXOOL_PREFIX = "hibernate.proxool";
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	public static final String PROXOOL_XML = "hibernate.proxool.xml";
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 	/**
 	 * Specifies how Hibernate should release JDBC connections.
 	 */
 	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 
 	/**
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 
 	/**
 	 * Names the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use for integrating
 	 * with {@literal JTA} systems.  Can reference either a {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
 	 * instance or the name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation class
 	 * @since 4.0
 	 */
 	public static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
 
 	/**
 	 * Names the {@link org.hibernate.transaction.TransactionManagerLookup} implementation to use for obtaining
 	 * reference to the {@literal JTA} {@link javax.transaction.TransactionManager}
 	 *
 	 * @deprecated See {@link #JTA_PLATFORM}
 	 */
 	@Deprecated
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 *
 	 * @deprecated See {@link #JTA_PLATFORM}
 	 */
 	@Deprecated
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
 	/**
-	 * The <tt>CacheProvider</tt> implementation class
-	 *
-	 * @deprecated See {@link #CACHE_REGION_FACTORY}
-	 */
-	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
-
-	/**
 	 * The {@link org.hibernate.cache.spi.RegionFactory} implementation class
 	 */
 	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 	/**
 	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
 	 */
 	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 	/**
 	 * The <tt>QueryCacheFactory</tt> implementation class.
 	 */
 	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enable statistics collection
 	 */
 	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
 	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
 	 */
 	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed before the statements of the
 	 * following files.
 	 *
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 *
 	 * The default value is <tt>/import.sql</tt>
 	 */
 	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * The {@link org.hibernate.exception.spi.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	public static final String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	public static final String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
     public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
     /**
      * The jacc context id of the deployment
      */
     public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
 
 	/**
 	 * Should all database identifiers be quoted.
 	 */
 	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 */
 	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	public static final String MULTI_TENANT = "hibernate.multiTenancy";
 
 	/**
 	 * Names the {@link ClassLoader} used to load user application classes.
 	 * @since 4.0
 	 */
 	public static final String APP_CLASSLOADER = "hibernate.classLoader.application";
 
 	/**
 	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
 	 * @since 4.0
 	 */
 	public static final String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
 
 	/**
 	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
 	 * the {@link ClassLoader} that loaded this class.
 	 * @since 4.0
 	 */
 	public static final String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
 
 	/**
 	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
 	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
 	 * @since 4.0
 	 */
 	public static final String ENVIRONMENT_CLASSLOADER = "hibernate.classLoader.environment";
 
 
 	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 
 	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 
 
 	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	public static final String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	public static final String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	public static final String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 	public static final String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.TransactionManager} references.
 	 * @since 4.0
 	 */
 	public static final String JTA_CACHE_TM = "hibernate.jta.cacheTransactionManager";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.UserTransaction} references.
 	 * @since 4.0
 	 */
 	public static final String JTA_CACHE_UT = "hibernate.jta.cacheUserTransaction";
 
 	/**
 	 * Setting used to give the name of the default {@link org.hibernate.annotations.CacheConcurrencyStrategy}
 	 * to use when either {@link javax.persistence.Cacheable @Cacheable} or
 	 * {@link org.hibernate.annotations.Cache @Cache} is used.  {@link org.hibernate.annotations.Cache @Cache(strategy="..")} is used to override.
 	 */
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
 
 	/**
 	 * Setting which indicates whether or not the new {@link org.hibernate.id.IdentifierGenerator} are used
 	 * for AUTO, TABLE and SEQUENCE.
 	 * Default to false to keep backward compatibility.
 	 */
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index 01928b1b96..651daf23bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,359 +1,354 @@
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.sql.Connection;
 import java.sql.Timestamp;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Version;
 import org.hibernate.bytecode.spi.BytecodeProvider;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 
 /**
  * Provides access to configuration info passed in <tt>Properties</tt> objects.
  * <br><br>
  * Hibernate has two property scopes:
  * <ul>
  * <li><b>Factory-level</b> properties may be passed to the <tt>SessionFactory</tt> when it
  * instantiated. Each instance might have different property values. If no
  * properties are specified, the factory calls <tt>Environment.getProperties()</tt>.
  * <li><b>System-level</b> properties are shared by all factory instances and are always
  * determined by the <tt>Environment</tt> properties.
  * </ul>
  * The only system-level properties are
  * <ul>
  * <li><tt>hibernate.jdbc.use_streams_for_binary</tt>
  * <li><tt>hibernate.cglib.use_reflection_optimizer</tt>
  * </ul>
  * <tt>Environment</tt> properties are populated by calling <tt>System.getProperties()</tt>
  * and then from a resource named <tt>/hibernate.properties</tt> if it exists. System
  * properties override properties specified in <tt>hibernate.properties</tt>.<br>
  * <br>
  * The <tt>SessionFactory</tt> is controlled by the following properties.
  * Properties may be either be <tt>System</tt> properties, properties
  * defined in a resource named <tt>/hibernate.properties</tt> or an instance of
  * <tt>java.util.Properties</tt> passed to
  * <tt>Configuration.buildSessionFactory()</tt><br>
  * <br>
  * <table>
  * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
  * <tr>
  *   <td><tt>hibernate.dialect</tt></td>
  *   <td>classname of <tt>org.hibernate.dialect.Dialect</tt> subclass</td>
  * </tr>
  * <tr>
- *   <td><tt>hibernate.cache.provider_class</tt></td>
- *   <td>classname of <tt>org.hibernate.cache.spi.CacheProvider</tt>
- *   subclass (if not specified EHCache is used)</td>
- * </tr>
- * <tr>
  *   <td><tt>hibernate.connection.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.service.jdbc.connections.spi.ConnectionProvider</tt>
  *   subclass (if not specified hueristics are used)</td>
  * </tr>
  * <tr><td><tt>hibernate.connection.username</tt></td><td>database username</td></tr>
  * <tr><td><tt>hibernate.connection.password</tt></td><td>database password</td></tr>
  * <tr>
  *   <td><tt>hibernate.connection.url</tt></td>
  *   <td>JDBC URL (when using <tt>java.sql.DriverManager</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.driver_class</tt></td>
  *   <td>classname of JDBC driver</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.isolation</tt></td>
  *   <td>JDBC transaction isolation level (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  *   <td><tt>hibernate.connection.pool_size</tt></td>
  *   <td>the maximum size of the connection pool (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.datasource</tt></td>
  *   <td>databasource JNDI name (when using <tt>javax.sql.Datasource</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.url</tt></td><td>JNDI <tt>InitialContext</tt> URL</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.class</tt></td><td>JNDI <tt>InitialContext</tt> classname</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.max_fetch_depth</tt></td>
  *   <td>maximum depth of outer join fetching</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.batch_size</tt></td>
  *   <td>enable use of JDBC2 batch API for drivers which support it</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.fetch_size</tt></td>
  *   <td>set the JDBC fetch size</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_scrollable_resultset</tt></td>
  *   <td>enable use of JDBC2 scrollable resultsets (you only need this specify
  *   this property when using user supplied connections)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_getGeneratedKeys</tt></td>
  *   <td>enable use of JDBC3 PreparedStatement.getGeneratedKeys() to retrieve
  *   natively generated keys after insert. Requires JDBC3+ driver and JRE1.4+</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.hbm2ddl.auto</tt></td>
  *   <td>enable auto DDL export</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_schema</tt></td>
  *   <td>use given schema name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_catalog</tt></td>
  *   <td>use given catalog name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.session_factory_name</tt></td>
  *   <td>If set, the factory attempts to bind this name to itself in the
  *   JNDI context. This name is also used to support cross JVM <tt>
  *   Session</tt> (de)serialization.</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.manager_lookup_class</tt></td>
  *   <td>classname of <tt>org.hibernate.transaction.TransactionManagerLookup</tt>
  *   implementor</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.factory_class</tt></td>
  *   <td>the factory to use for instantiating <tt>Transaction</tt>s.
  *   (Defaults to <tt>JdbcTransactionFactory</tt>.)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.query.substitutions</tt></td><td>query language token substitutions</td>
  * </tr>
  * </table>
  *
  * @see org.hibernate.SessionFactory
  * @author Gavin King
  */
 public final class Environment implements AvailableSettings {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Environment.class.getName());
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
 
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
 	/**
 	 * Issues warnings to the user when any obsolete or renamed property names are used.
 	 *
 	 * @param configurationValues The specified properties.
 	 */
 	public static void verifyProperties(Map<?,?> configurationValues) {
 		final Map propertiesToAdd = new HashMap();
 		for ( Map.Entry entry : configurationValues.entrySet() ) {
 			final Object replacementKey = OBSOLETE_PROPERTIES.get( entry.getKey() );
 			if ( replacementKey != null ) {
 				LOG.unsupportedProperty( entry.getKey(), replacementKey );
 			}
 			final Object renamedKey = RENAMED_PROPERTIES.get( entry.getKey() );
 			if ( renamedKey != null ) {
 				LOG.renamedProperty( entry.getKey(), renamedKey );
 				propertiesToAdd.put( renamedKey, entry.getValue() );
 			}
 		}
 		configurationValues.putAll( propertiesToAdd );
 	}
 
 	static {
 
         LOG.version(Version.getVersionString());
 
 		ISOLATION_LEVELS.put( (Connection.TRANSACTION_NONE), "NONE" );
 		ISOLATION_LEVELS.put( (Connection.TRANSACTION_READ_UNCOMMITTED), "READ_UNCOMMITTED" );
 		ISOLATION_LEVELS.put( (Connection.TRANSACTION_READ_COMMITTED), "READ_COMMITTED" );
 		ISOLATION_LEVELS.put( (Connection.TRANSACTION_REPEATABLE_READ), "REPEATABLE_READ" );
 		ISOLATION_LEVELS.put( (Connection.TRANSACTION_SERIALIZABLE), "SERIALIZABLE" );
 
 		GLOBAL_PROPERTIES = new Properties();
 		//Set USE_REFLECTION_OPTIMIZER to false to fix HHH-227
 		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.FALSE.toString() );
 
 		try {
 			InputStream stream = ConfigHelper.getResourceAsStream( "/hibernate.properties" );
 			try {
 				GLOBAL_PROPERTIES.load(stream);
                 LOG.propertiesLoaded(ConfigurationHelper.maskOut(GLOBAL_PROPERTIES, PASS));
 			}
 			catch (Exception e) {
                 LOG.unableToLoadProperties();
 			}
 			finally {
 				try{
 					stream.close();
 				}
 				catch (IOException ioe){
                     LOG.unableToCloseStreamError(ioe);
 				}
 			}
 		}
 		catch (HibernateException he) {
             LOG.propertiesNotFound();
 		}
 
 		try {
 			GLOBAL_PROPERTIES.putAll( System.getProperties() );
 		}
 		catch (SecurityException se) {
             LOG.unableToCopySystemProperties();
 		}
 
 		verifyProperties(GLOBAL_PROPERTIES);
 
 		ENABLE_BINARY_STREAMS = ConfigurationHelper.getBoolean(USE_STREAMS_FOR_BINARY, GLOBAL_PROPERTIES);
         if (ENABLE_BINARY_STREAMS) {
 			LOG.usingStreams();
 		}
 
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
         if (ENABLE_REFLECTION_OPTIMIZER) {
 			LOG.usingReflectionOptimizer();
 		}
 
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
         if (JVM_HAS_TIMESTAMP_BUG) {
 			LOG.usingTimestampWorkaround();
 		}
 	}
 
 	public static BytecodeProvider getBytecodeProvider() {
 		return BYTECODE_PROVIDER_INSTANCE;
 	}
 
 	/**
 	 * Does this JVM's implementation of {@link java.sql.Timestamp} have a bug in which the following is true:<code>
 	 * new java.sql.Timestamp( x ).getTime() != x
 	 * </code>
 	 * <p/>
 	 * NOTE : IBM JDK 1.3.1 the only known JVM to exhibit this behavior.
 	 *
 	 * @return True if the JVM's {@link Timestamp} implementa
 	 */
 	public static boolean jvmHasTimestampBug() {
 		return JVM_HAS_TIMESTAMP_BUG;
 	}
 
 	/**
 	 * Should we use streams to bind binary types to JDBC IN parameters?
 	 *
 	 * @return True if streams should be used for binary data handling; false otherwise.
 	 *
 	 * @see #USE_STREAMS_FOR_BINARY
 	 */
 	public static boolean useStreamsForBinary() {
 		return ENABLE_BINARY_STREAMS;
 	}
 
 	/**
 	 * Should we use reflection optimization?
 	 *
 	 * @return True if reflection optimization should be used; false otherwise.
 	 *
 	 * @see #USE_REFLECTION_OPTIMIZER
 	 * @see #getBytecodeProvider()
 	 * @see BytecodeProvider#getReflectionOptimizer
 	 */
 	public static boolean useReflectionOptimizer() {
 		return ENABLE_REFLECTION_OPTIMIZER;
 	}
 
 	/**
 	 * Disallow instantiation
 	 */
 	private Environment() {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Return <tt>System</tt> properties, extended by any properties specified
 	 * in <tt>hibernate.properties</tt>.
 	 * @return Properties
 	 */
 	public static Properties getProperties() {
 		Properties copy = new Properties();
 		copy.putAll(GLOBAL_PROPERTIES);
 		return copy;
 	}
 
 	/**
 	 * Get the name of a JDBC transaction isolation level
 	 *
 	 * @see java.sql.Connection
 	 * @param isolation as defined by <tt>java.sql.Connection</tt>
 	 * @return a human-readable name
 	 */
 	public static String isolationLevelToString(int isolation) {
 		return (String) ISOLATION_LEVELS.get( isolation );
 	}
 
 	public static BytecodeProvider buildBytecodeProvider(Properties properties) {
 		String provider = ConfigurationHelper.getString( BYTECODE_PROVIDER, properties, "javassist" );
         LOG.bytecodeProvider(provider);
 		return buildBytecodeProvider( provider );
 	}
 
 	private static BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 		}
 
         LOG.unknownBytecodeProvider( providerName );
 		return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java b/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
index 327e9eb91b..1dbc61e1fa 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
@@ -1,342 +1,342 @@
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
 package org.hibernate.cfg;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 /**
  * Defines support for various externally configurable SessionFactory(s), for
  * example, {@link org.hibernate.jmx.HibernateService JMX} or the JCA
  * adapter.
  *
  * @author Steve Ebersole
  */
 public abstract class ExternalSessionFactoryConfig {
 	private String mapResources;
 	private String dialect;
 	private String defaultSchema;
 	private String defaultCatalog;
 	private String maximumFetchDepth;
 	private String jdbcFetchSize;
 	private String jdbcBatchSize;
 	private String batchVersionedDataEnabled;
 	private String jdbcScrollableResultSetEnabled;
 	private String getGeneratedKeysEnabled;
 	private String streamsForBinaryEnabled;
 	private String reflectionOptimizationEnabled;
 	private String querySubstitutions;
 	private String showSqlEnabled;
 	private String commentsEnabled;
-	private String cacheProviderClass;
+	private String cacheRegionFactory;
 	private String cacheProviderConfig;
 	private String cacheRegionPrefix;
 	private String secondLevelCacheEnabled;
 	private String minimalPutsEnabled;
 	private String queryCacheEnabled;
 
 	private Map additionalProperties;
 	private Set excludedPropertyNames = new HashSet();
 
 	protected Set getExcludedPropertyNames() {
 		return excludedPropertyNames;
 	}
 
 	public final String getMapResources() {
 		return mapResources;
 	}
 
 	public final void setMapResources(String mapResources) {
 		this.mapResources = mapResources;
 	}
 
 	public void addMapResource(String mapResource) {
 		if ( mapResources==null || mapResources.length()==0 ) {
 			mapResources = mapResource.trim();
 		}
 		else {
 			mapResources += ", " + mapResource.trim();
 		}
 	}
 
 	public final String getDialect() {
 		return dialect;
 	}
 
 	public final void setDialect(String dialect) {
 		this.dialect = dialect;
 	}
 
 	public final String getDefaultSchema() {
 		return defaultSchema;
 	}
 
 	public final void setDefaultSchema(String defaultSchema) {
 		this.defaultSchema = defaultSchema;
 	}
 
 	public final String getDefaultCatalog() {
 		return defaultCatalog;
 	}
 
 	public final void setDefaultCatalog(String defaultCatalog) {
 		this.defaultCatalog = defaultCatalog;
 	}
 
 	public final String getMaximumFetchDepth() {
 		return maximumFetchDepth;
 	}
 
 	public final void setMaximumFetchDepth(String maximumFetchDepth) {
 		verifyInt( maximumFetchDepth );
 		this.maximumFetchDepth = maximumFetchDepth;
 	}
 
 	public final String getJdbcFetchSize() {
 		return jdbcFetchSize;
 	}
 
 	public final void setJdbcFetchSize(String jdbcFetchSize) {
 		verifyInt( jdbcFetchSize );
 		this.jdbcFetchSize = jdbcFetchSize;
 	}
 
 	public final String getJdbcBatchSize() {
 		return jdbcBatchSize;
 	}
 
 	public final void setJdbcBatchSize(String jdbcBatchSize) {
 		verifyInt( jdbcBatchSize );
 		this.jdbcBatchSize = jdbcBatchSize;
 	}
 
 	public final String getBatchVersionedDataEnabled() {
 		return batchVersionedDataEnabled;
 	}
 
 	public final void setBatchVersionedDataEnabled(String batchVersionedDataEnabled) {
 		this.batchVersionedDataEnabled = batchVersionedDataEnabled;
 	}
 
 	public final String getJdbcScrollableResultSetEnabled() {
 		return jdbcScrollableResultSetEnabled;
 	}
 
 	public final void setJdbcScrollableResultSetEnabled(String jdbcScrollableResultSetEnabled) {
 		this.jdbcScrollableResultSetEnabled = jdbcScrollableResultSetEnabled;
 	}
 
 	public final String getGetGeneratedKeysEnabled() {
 		return getGeneratedKeysEnabled;
 	}
 
 	public final void setGetGeneratedKeysEnabled(String getGeneratedKeysEnabled) {
 		this.getGeneratedKeysEnabled = getGeneratedKeysEnabled;
 	}
 
 	public final String getStreamsForBinaryEnabled() {
 		return streamsForBinaryEnabled;
 	}
 
 	public final void setStreamsForBinaryEnabled(String streamsForBinaryEnabled) {
 		this.streamsForBinaryEnabled = streamsForBinaryEnabled;
 	}
 
 	public final String getReflectionOptimizationEnabled() {
 		return reflectionOptimizationEnabled;
 	}
 
 	public final void setReflectionOptimizationEnabled(String reflectionOptimizationEnabled) {
 		this.reflectionOptimizationEnabled = reflectionOptimizationEnabled;
 	}
 
 	public final String getQuerySubstitutions() {
 		return querySubstitutions;
 	}
 
 	public final void setQuerySubstitutions(String querySubstitutions) {
 		this.querySubstitutions = querySubstitutions;
 	}
 
 	public final String getShowSqlEnabled() {
 		return showSqlEnabled;
 	}
 
 	public final void setShowSqlEnabled(String showSqlEnabled) {
 		this.showSqlEnabled = showSqlEnabled;
 	}
 
 	public final String getCommentsEnabled() {
 		return commentsEnabled;
 	}
 
 	public final void setCommentsEnabled(String commentsEnabled) {
 		this.commentsEnabled = commentsEnabled;
 	}
 
 	public final String getSecondLevelCacheEnabled() {
 		return secondLevelCacheEnabled;
 	}
 
 	public final void setSecondLevelCacheEnabled(String secondLevelCacheEnabled) {
 		this.secondLevelCacheEnabled = secondLevelCacheEnabled;
 	}
 
-	public final String getCacheProviderClass() {
-		return cacheProviderClass;
+	public final String getCacheRegionFactory() {
+		return cacheRegionFactory;
 	}
 
-	public final void setCacheProviderClass(String cacheProviderClass) {
-		this.cacheProviderClass = cacheProviderClass;
+	public final void setCacheRegionFactory(String cacheRegionFactory) {
+		this.cacheRegionFactory = cacheRegionFactory;
 	}
 
 	public String getCacheProviderConfig() {
 		return cacheProviderConfig;
 	}
 
 	public void setCacheProviderConfig(String cacheProviderConfig) {
 		this.cacheProviderConfig = cacheProviderConfig;
 	}
 
 	public final String getCacheRegionPrefix() {
 		return cacheRegionPrefix;
 	}
 
 	public final void setCacheRegionPrefix(String cacheRegionPrefix) {
 		this.cacheRegionPrefix = cacheRegionPrefix;
 	}
 
 	public final String getMinimalPutsEnabled() {
 		return minimalPutsEnabled;
 	}
 
 	public final void setMinimalPutsEnabled(String minimalPutsEnabled) {
 		this.minimalPutsEnabled = minimalPutsEnabled;
 	}
 
 	public final String getQueryCacheEnabled() {
 		return queryCacheEnabled;
 	}
 
 	public final void setQueryCacheEnabled(String queryCacheEnabled) {
 		this.queryCacheEnabled = queryCacheEnabled;
 	}
 
 	public final void addAdditionalProperty(String name, String value) {
 		if ( !getExcludedPropertyNames().contains( name ) ) {
 			if ( additionalProperties == null ) {
 				additionalProperties = new HashMap();
 			}
 			additionalProperties.put( name, value );
 		}
 	}
 
 	protected final Configuration buildConfiguration() {
 
 		Configuration cfg = new Configuration().setProperties( buildProperties() );
 
 
 		String[] mappingFiles = ConfigurationHelper.toStringArray( mapResources, " ,\n\t\r\f" );
 		for ( int i = 0; i < mappingFiles.length; i++ ) {
 			cfg.addResource( mappingFiles[i] );
 		}
 
 		return cfg;
 	}
 
 	protected final Properties buildProperties() {
 		Properties props = new Properties();
 		setUnlessNull( props, Environment.DIALECT, dialect );
 		setUnlessNull( props, Environment.DEFAULT_SCHEMA, defaultSchema );
 		setUnlessNull( props, Environment.DEFAULT_CATALOG, defaultCatalog );
 		setUnlessNull( props, Environment.MAX_FETCH_DEPTH, maximumFetchDepth );
 		setUnlessNull( props, Environment.STATEMENT_FETCH_SIZE, jdbcFetchSize );
 		setUnlessNull( props, Environment.STATEMENT_BATCH_SIZE, jdbcBatchSize );
 		setUnlessNull( props, Environment.BATCH_VERSIONED_DATA, batchVersionedDataEnabled );
 		setUnlessNull( props, Environment.USE_SCROLLABLE_RESULTSET, jdbcScrollableResultSetEnabled );
 		setUnlessNull( props, Environment.USE_GET_GENERATED_KEYS, getGeneratedKeysEnabled );
 		setUnlessNull( props, Environment.USE_STREAMS_FOR_BINARY, streamsForBinaryEnabled );
 		setUnlessNull( props, Environment.USE_REFLECTION_OPTIMIZER, reflectionOptimizationEnabled );
 		setUnlessNull( props, Environment.QUERY_SUBSTITUTIONS, querySubstitutions );
 		setUnlessNull( props, Environment.SHOW_SQL, showSqlEnabled );
 		setUnlessNull( props, Environment.USE_SQL_COMMENTS, commentsEnabled );
-		setUnlessNull( props, Environment.CACHE_PROVIDER, cacheProviderClass );
+		setUnlessNull( props, Environment.CACHE_REGION_FACTORY, cacheRegionFactory );
 		setUnlessNull( props, Environment.CACHE_PROVIDER_CONFIG, cacheProviderConfig );
 		setUnlessNull( props, Environment.CACHE_REGION_PREFIX, cacheRegionPrefix );
 		setUnlessNull( props, Environment.USE_MINIMAL_PUTS, minimalPutsEnabled );
 		setUnlessNull( props, Environment.USE_SECOND_LEVEL_CACHE, secondLevelCacheEnabled );
 		setUnlessNull( props, Environment.USE_QUERY_CACHE, queryCacheEnabled );
 
 		Map extraProperties = getExtraProperties();
 		if ( extraProperties != null ) {
 			addAll( props, extraProperties );
 		}
 
 		if ( additionalProperties != null ) {
 			addAll( props, additionalProperties );
 		}
 
 		return props;
 	}
 
 	protected void addAll( Properties target, Map source ) {
 		Iterator itr = source.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String propertyName = ( String ) entry.getKey();
 			final String propertyValue = ( String ) entry.getValue();
 			if ( propertyName != null && propertyValue != null ) {
 				// Make sure we don't override previous set values
 				if ( !target.keySet().contains( propertyName ) ) {
 					if ( !getExcludedPropertyNames().contains( propertyName) ) {
 						target.put( propertyName, propertyValue );
 					}
 				}
 			}
 		}
 	}
 
 	protected Map getExtraProperties() {
 		return null;
 	}
 
 	private void setUnlessNull(Properties props, String key, String value) {
 		if ( value != null ) {
 			props.setProperty( key, value );
 		}
 	}
 
 	private void verifyInt(String value)
 	{
 		if ( value != null ) {
 			Integer.parseInt( value );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 1286675c25..d3fa342450 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,410 +1,396 @@
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
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
+import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
-import org.hibernate.cache.internal.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
         LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
         LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.debugf( "Connection release mode: %s", releaseModeName );
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
                 LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
         if ( defaultSchema != null ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
         if (defaultCatalog != null) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
         if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
         LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
         LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
         LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
         Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
         LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
         LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
         if (prefix != null) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
         LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
         LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
 		if ( "validate".equals(autoSchemaExport) ) {
 			settings.setAutoValidateSchema( true );
 		}
 		if ( "update".equals(autoSchemaExport) ) {
 			settings.setAutoUpdateSchema( true );
 		}
 		if ( "create".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema( true );
 		}
 		if ( "create-drop".equals( autoSchemaExport ) ) {
 			settings.setAutoCreateSchema( true );
 			settings.setAutoDropSchema( true );
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
         LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debugf("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
-				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.internal.StandardQueryCacheFactory"
+				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
 		);
         LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( queryCacheFactoryClassName )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
-	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
+	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
-		if ( regionFactoryClassName == null && cachingEnabled ) {
-			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
-			if ( providerClassName != null ) {
-				// legacy behavior, apply the bridge...
-				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
-			}
-		}
-		if ( regionFactoryClassName == null ) {
+		if ( regionFactoryClassName == null || !cachingEnabled) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
-
-	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
-		String className = ConfigurationHelper.getString(
-				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
-		);
-        LOG.debugf( "Query translator: %s", className );
-		try {
-			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
-					.classForName( className )
-					.newInstance();
-		}
-		catch (Exception e) {
-			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
-		}
-	}
-
+	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
-		if ( regionFactoryClassName == null && cachingEnabled ) {
-			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
-			if ( providerClassName != null ) {
-				// legacy behavior, apply the bridge...
-				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
-			}
-		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
+
+	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
+		String className = ConfigurationHelper.getString(
+				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
+		);
+		LOG.debugf( "Query translator: %s", className );
+		try {
+			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
+					.classForName( className )
+					.newInstance();
+		}
+		catch ( Exception e ) {
+			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java
index 6321c7d01c..2d2913d7a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateServiceMBean.java
@@ -1,337 +1,337 @@
 //$Id: HibernateServiceMBean.java 10860 2006-11-22 00:02:55Z steve.ebersole@jboss.com $
 package org.hibernate.jmx;
 import org.hibernate.HibernateException;
 
 /**
  * Hibernate JMX Management API
  * @see HibernateService
  * @author John Urberg, Gavin King
  * @deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
  */
 @Deprecated
 public interface HibernateServiceMBean {
 
 	/**
 	 * The Hibernate mapping files (might be overridden by subclasses
 	 * that want to specify the mapping files by some other mechanism)
 	 * @return String
 	 */
 	public String getMapResources();
 	/**
 	 * Specify the Hibernate mapping files
 	 * @param mappingFiles
 	 */
 	public void setMapResources(String mappingFiles);
 	/**
 	 * Add a mapping file
 	 * @param mapResource
 	 */
 	public void addMapResource(String mapResource);
 
 	/**
 	 * Set a property
 	 * @param property the property name
 	 * @param value the property value
 	 */
 	public void setProperty(String property, String value);
 
 	/**
 	 * Get a property
 	 * @param property the property name
 	 * @return the property value
 	 */
 	public String getProperty(String property);
 
 	/**
 	 * Display the properties
 	 * @return a list of property names and values
 	 */
 	public String getPropertyList();
 
 	/**
 	 * The JNDI name of the datasource to use in this <tt>SessionFactory</tt>
 	 * @return String
 	 */
 	public String getDatasource();
 	/**
 	 * Set the JNDI name of the datasource to use in this <tt>SessionFactory</tt>
 	 * @param datasource
 	 */
 	public void setDatasource(String datasource);
 
 	/**
 	 * Log into the database with this name
 	 * @return String
 	 */
 	public String getUserName();
 	/**
 	 * Log into the database with this name
 	 * @param userName
 	 */
 	public void setUserName(String userName);
 
 	/**
 	 * Log into the database with this password
 	 * @return String
 	 */
 	public String getPassword();
 	/**
 	 * Log into the database with this password
 	 * @param password
 	 */
 	public void setPassword(String password);
 
 	/**
 	 * The JNDI name of the dialect class to use in this <tt>SessionFactory</tt>
 	 * @return String
 	 */
 	public String getDialect();
 	/**
 	 * The name of the dialect class to use in this <tt>SessionFactory</tt>
 	 * @param dialect fully qualified class name of <tt>Dialect</tt> subclass
 	 * @see org.hibernate.dialect.Dialect
 	 */
 	public void setDialect(String dialect);
 
 	/**
 	 * The JNDI name to bind to the <tt>SessionFactory</tt>
 	 * @return String
 	 */
 	public String getJndiName();
 	/**
 	 * The JNDI name to bind to the <tt>SessionFactory</tt>
 	 * @param jndiName
 	 */
 	public void setJndiName(String jndiName);
 
 	/**
 	 * The fully qualified class name of the Hibernate {@link org.hibernate.engine.transaction.spi.TransactionFactory}
 	 * implementation to use
 	 *
 	 * @return the class name
 	 */
 	public String getTransactionStrategy();
 
 	/**
 	 * Set the fully qualified class name of the Hibernate {@link org.hibernate.engine.transaction.spi.TransactionFactory}
 	 * implementation to use.
 	 *
 	 * @param txnStrategy the class name
 	 */
 	public void setTransactionStrategy(String txnStrategy);
 
 	/**
 	 * The JNDI name of the JTA UserTransaction object (used only be <tt>JtaTransaction</tt>).
 	 * @return the JNDI name
 	 * @see org.hibernate.engine.transaction.internal.jta.JtaTransaction
 	 */
 	public String getUserTransactionName();
 	/**
 	 * Set the JNDI name of the JTA UserTransaction object (used only by <tt>JtaTransaction</tt>).
 	 * @param utName the JNDI name
 	 * @see org.hibernate.engine.transaction.internal.jta.JtaTransaction
 	 */
 	public void setUserTransactionName(String utName);
 
 	/**
 	 * Get the name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use.
 	 *
 	 * @return The name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use.
 	 */
 	public String getJtaPlatformName();
 
 	/**
 	 * Sets the name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use.
 	 *
 	 * @param name The implementation class name.
 	 */
 	public void setJtaPlatformName(String name);
 
 	/**
 	 * Is SQL logging enabled?
 	 */
 	public String getShowSqlEnabled();
 	/**
 	 * Enable logging of SQL to console
 	 */
 	public void setShowSqlEnabled(String showSql);
 	/**
 	 * Get the maximum outer join fetch depth
 	 */
 	public String getMaximumFetchDepth();
 	/**
 	 * Set the maximum outer join fetch depth
 	 */
 	public void setMaximumFetchDepth(String fetchDepth);
 	/**
 	 * Get the maximum JDBC batch size
 	 */
 	public String getJdbcBatchSize();
 	/**
 	 * Set the maximum JDBC batch size
 	 */
 	public void setJdbcBatchSize(String batchSize);
 	/**
 	 * Get the JDBC fetch size
 	 */
 	public String getJdbcFetchSize();
 	/**
 	 * Set the JDBC fetch size
 	 */
 	public void setJdbcFetchSize(String fetchSize);
 	/**
 	 * Get the query language substitutions
 	 */
 	public String getQuerySubstitutions();
 	/**
 	 * Set the query language substitutions
 	 */
 	public void setQuerySubstitutions(String querySubstitutions);
 	/**
 	 * Get the default schema
 	 */
 	public String getDefaultSchema();
 	/**
 	 * Set the default schema
 	 */
 	public void setDefaultSchema(String schema);
 	/**
 	 * Get the default catalog
 	 */
 	public String getDefaultCatalog();
 	/**
 	 * Set the default catalog
 	 */
 	public void setDefaultCatalog(String catalog);
 	/**
 	 * Is use of scrollable resultsets enabled?
 	 */
 	public String getJdbcScrollableResultSetEnabled();
 	/**
 	 * Enable or disable the use of scrollable resultsets 
 	 */
 	public void setJdbcScrollableResultSetEnabled(String enabled);
 	/**
 	 * Is use of JDBC3 <tt>getGeneratedKeys()</tt> enabled?
 	 */
 	public String getGetGeneratedKeysEnabled();
 	/**
 	 * Enable or disable the use <tt>getGeneratedKeys()</tt> 
 	 */
 	public void setGetGeneratedKeysEnabled(String enabled);
 	/**
 	 * Get the second-level cache provider class name
 	 */
-	public String getCacheProviderClass();
+	public String getCacheRegionFactory();
 	/**
 	 * Set the second-level cache provider class name
 	 */
-	public void setCacheProviderClass(String providerClassName);
+	public void setCacheRegionFactory(String cacheRegionFactory);
 	/**
 	 * For cache providers which support this setting, get the
 	 * provider's specific configuration resource.
 	 */
 	public String getCacheProviderConfig();
 	/**
 	 * For cache providers which support this setting, specify the
 	 * provider's specific configuration resource.
 	 */
 	public void setCacheProviderConfig(String cacheProviderConfig);
 	/**
 	 * Is the query cache enabled?
 	 */
 	public String getQueryCacheEnabled();
 	/**
 	 * Enable or disable the query cache
 	 */
 	public void setQueryCacheEnabled(String enabled);
 	/**
 	 * Is the second-level cache enabled?
 	 */
 	public String getSecondLevelCacheEnabled();
 	/**
 	 * Enable or disable the second-level cache
 	 */
 	public void setSecondLevelCacheEnabled(String enabled);
 	/**
 	 * Get the cache region prefix
 	 */
 	public String getCacheRegionPrefix();
 	/**
 	 * Set the cache region prefix
 	 */
 	public void setCacheRegionPrefix(String prefix);
 	/**
 	 * Is the second-level cache optimized for miminal puts?
 	 */
 	public String getMinimalPutsEnabled();
 	/**
 	 * Enable or disable optimization of second-level cache
 	 * for minimal puts 
 	 */
 	public void setMinimalPutsEnabled(String enabled);
 	/**
 	 * Are SQL comments enabled?
 	 */
 	public String getCommentsEnabled();
 	/**
 	 * Enable or disable the inclusion of comments in
 	 * generated SQL
 	 */
 	public void setCommentsEnabled(String enabled);
 	/**
 	 * Is JDBC batch update for versioned entities enabled?
 	 */
 	public String getBatchVersionedDataEnabled();
 	/**
 	 * Enable or disable the use of batch updates for
 	 * versioned entities
 	 */
 	public void setBatchVersionedDataEnabled(String enabled);
 	
 	/**
 	 * Enable automatic flushing of the Session when JTA transaction ends.
 	 */
 	public void setFlushBeforeCompletionEnabled(String enabled);
 	/**
 	 * Is automatic Session flusing enabled?
 	 */
 	public String getFlushBeforeCompletionEnabled();
 
 	/**
 	 * Enable automatic closing of Session when JTA transaction ends.
 	 */
 	public void setAutoCloseSessionEnabled(String enabled);
 	/**
 	 * Is automatic Session closing enabled?
 	 */
 	public String getAutoCloseSessionEnabled();
 
 	/**
 	 * Export the <tt>CREATE</tt> DDL to the database
 	 * @throws HibernateException
 	 */
 	public void createSchema() throws HibernateException;
 	/**
 	 * Export the <tt>DROP</tt> DDL to the database
 	 * @throws HibernateException
 	 */
 	public void dropSchema() throws HibernateException;
 
 
 	/**
 	 * Create the <tt>SessionFactory</tt> and bind to the jndi name on startup
 	 */
 	public void start() throws HibernateException;
 	/**
 	 * Unbind the <tt>SessionFactory</tt> or stub from JNDI
 	 */
 	public void stop();
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CacheTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CacheTest.java
deleted file mode 100644
index 8d95027816..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CacheTest.java
+++ /dev/null
@@ -1,159 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.test.legacy;
-
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.CacheConcurrencyStrategy;
-import org.hibernate.cache.spi.CacheProvider;
-import org.hibernate.cache.internal.HashtableCacheProvider;
-import org.hibernate.cache.spi.ReadWriteCache;
-import org.hibernate.cache.spi.access.SoftLock;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-import static org.junit.Assert.assertTrue;
-
-public class CacheTest extends BaseUnitTestCase {
-	@Test
-	public void testCaches() throws Exception {
-		doTestCache( new HashtableCacheProvider() );
-	}
-
-	public void doTestCache(CacheProvider cacheProvider) throws Exception {
-
-		Cache cache = cacheProvider.buildCache( String.class.getName(), System.getProperties() );
-
-		long longBefore = cache.nextTimestamp();
-
-		Thread.sleep( 15 );
-
-		long before = cache.nextTimestamp();
-
-		Thread.sleep( 15 );
-
-		//cache.setTimeout(1000);
-		CacheConcurrencyStrategy ccs = new ReadWriteCache();
-		ccs.setCache( cache );
-
-		// cache something
-
-		assertTrue( ccs.put( "foo", "foo", before, null, null, false ) );
-
-		Thread.sleep( 15 );
-
-		long after = cache.nextTimestamp();
-
-		assertTrue( ccs.get( "foo", longBefore ) == null );
-		assertTrue( ccs.get( "foo", after ).equals( "foo" ) );
-
-		assertTrue( !ccs.put( "foo", "foo", before, null, null, false ) );
-
-		// update it:
-
-		SoftLock lock = ccs.lock( "foo", null );
-
-		assertTrue( ccs.get( "foo", after ) == null );
-		assertTrue( ccs.get( "foo", longBefore ) == null );
-
-		assertTrue( !ccs.put( "foo", "foo", before, null, null, false ) );
-
-		Thread.sleep( 15 );
-
-		long whileLocked = cache.nextTimestamp();
-
-		assertTrue( !ccs.put( "foo", "foo", whileLocked, null, null, false ) );
-
-		Thread.sleep( 15 );
-
-		ccs.release( "foo", lock );
-
-		assertTrue( ccs.get( "foo", after ) == null );
-		assertTrue( ccs.get( "foo", longBefore ) == null );
-
-		assertTrue( !ccs.put( "foo", "bar", whileLocked, null, null, false ) );
-		assertTrue( !ccs.put( "foo", "bar", after, null, null, false ) );
-
-		Thread.sleep( 15 );
-
-		long longAfter = cache.nextTimestamp();
-
-		assertTrue( ccs.put( "foo", "baz", longAfter, null, null, false ) );
-
-		assertTrue( ccs.get( "foo", after ) == null );
-		assertTrue( ccs.get( "foo", whileLocked ) == null );
-
-		Thread.sleep( 15 );
-
-		long longLongAfter = cache.nextTimestamp();
-
-		assertTrue( ccs.get( "foo", longLongAfter ).equals( "baz" ) );
-
-		// update it again, with multiple locks:
-
-		SoftLock lock1 = ccs.lock( "foo", null );
-		SoftLock lock2 = ccs.lock( "foo", null );
-
-		assertTrue( ccs.get( "foo", longLongAfter ) == null );
-
-		Thread.sleep( 15 );
-
-		whileLocked = cache.nextTimestamp();
-
-		assertTrue( !ccs.put( "foo", "foo", whileLocked, null, null, false ) );
-
-		Thread.sleep( 15 );
-
-		ccs.release( "foo", lock2 );
-
-		Thread.sleep( 15 );
-
-		long betweenReleases = cache.nextTimestamp();
-
-		assertTrue( !ccs.put( "foo", "bar", betweenReleases, null, null, false ) );
-		assertTrue( ccs.get( "foo", betweenReleases ) == null );
-
-		Thread.sleep( 15 );
-
-		ccs.release( "foo", lock1 );
-
-		assertTrue( !ccs.put( "foo", "bar", whileLocked, null, null, false ) );
-
-		Thread.sleep( 15 );
-
-		longAfter = cache.nextTimestamp();
-
-		assertTrue( ccs.put( "foo", "baz", longAfter, null, null, false ) );
-		assertTrue( ccs.get( "foo", whileLocked ) == null );
-
-		Thread.sleep( 15 );
-
-		longLongAfter = cache.nextTimestamp();
-
-		assertTrue( ccs.get( "foo", longLongAfter ).equals( "baz" ) );
-
-	}
-
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
index ed38df34a7..9cb6d6fa80 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
@@ -1,288 +1,288 @@
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
 package org.hibernate.test.multitenancy.schema;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
-import org.hibernate.cache.internal.HashtableCacheProvider;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.testing.cache.CachingRegionFactory;
 import org.hibernate.tool.hbm2ddl.ConnectionHelper;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class SchemaBasedMultiTenancyTest extends BaseUnitTestCase {
 	private DriverManagerConnectionProviderImpl acmeProvider;
 	private DriverManagerConnectionProviderImpl jbossProvider;
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private SessionFactoryImplementor sessionFactory;
 
 	@Before
 	public void setUp() {
 		acmeProvider = ConnectionProviderBuilder.buildConnectionProvider( "acme" );
 		jbossProvider = ConnectionProviderBuilder.buildConnectionProvider( "jboss" );
 		AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = new AbstractMultiTenantConnectionProvider() {
 			@Override
 			protected ConnectionProvider getAnyConnectionProvider() {
 				return acmeProvider;
 			}
 
 			@Override
 			protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
 				if ( "acme".equals( tenantIdentifier ) ) {
 					return acmeProvider;
 				}
 				else if ( "jboss".equals( tenantIdentifier ) ) {
 					return jbossProvider;
 				}
 				throw new HibernateException( "Unknown tenant identifier" );
 			}
 		};
 
 		Configuration cfg = new Configuration();
 		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE );
-		cfg.setProperty( Environment.CACHE_PROVIDER, HashtableCacheProvider.class.getName() );
+		cfg.setProperty( Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName() );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.addAnnotatedClass( Customer.class );
 
 		cfg.buildMappings();
 		RootClass meta = (RootClass) cfg.getClassMapping( Customer.class.getName() );
 		meta.setCacheConcurrencyStrategy( "read-write" );
 
 		// do the acme export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = acmeProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						acmeProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute(		 // so stupid...
 						   false,	 // do not script the export (write it to file)
 						   true,	 // do run it against the database
 						   false,	 // do not *just* perform the drop
 						   false	// do not *just* perform the create
 		);
 
 		// do the jboss export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = jbossProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						jbossProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute( 		// so stupid...
 				false, 	// do not script the export (write it to file)
 				true, 	// do run it against the database
 				false, 	// do not *just* perform the drop
 				false	// do not *just* perform the create
 		);
 
 		serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder( cfg.getProperties() )
 				.addService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider )
 				.buildServiceRegistry();
 
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 		if ( jbossProvider != null ) {
 			jbossProvider.stop();
 		}
 		if ( acmeProvider != null ) {
 			acmeProvider.stop();
 		}
 	}
 
 	@Test
 	public void testBasicExpectedBehavior() {
 		Session session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 		try {
 			session.beginTransaction();
 			Customer check = (Customer) session.get( Customer.class, steve.getId() );
 			Assert.assertNull( "tenancy not properly isolated", check );
 		}
 		finally {
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testSameIdentifiers() {
 		// create a customer 'steve' in jboss
 		Session session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		// now, create a customer 'john' in acme
 		session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 		session.beginTransaction();
 		Customer john = new Customer( 1L, "john" );
 		session.save( john );
 		session.getTransaction().commit();
 		session.close();
 
 		sessionFactory.getStatisticsImplementor().clear();
 
 		// make sure we get the correct people back, from cache
 		// first, jboss
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		// make sure the same works from datastore too
 		sessionFactory.getStatisticsImplementor().clear();
 		sessionFactory.getCache().evictEntityRegions();
 		// first jboss
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = sessionFactory.withOptions().tenantIdentifier( "jboss" ).openSession();
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = sessionFactory.withOptions().tenantIdentifier( "acme" ).openSession();
 		session.beginTransaction();
 		session.delete( john );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/resources/hibernate.properties b/hibernate-core/src/test/resources/hibernate.properties
index 721eef9ff8..fb9cc13c6a 100644
--- a/hibernate-core/src/test/resources/hibernate.properties
+++ b/hibernate-core/src/test/resources/hibernate.properties
@@ -1,36 +1,36 @@
 #
 # Hibernate, Relational Persistence for Idiomatic Java
 #
 # Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 # indicated by the @author tags or express copyright attribution
 # statements applied by the authors.  All third-party contributions are
 # distributed under license by Red Hat Inc.
 #
 # This copyrighted material is made available to anyone wishing to use, modify,
 # copy, or redistribute it subject to the terms and conditions of the GNU
 # Lesser General Public License, as published by the Free Software Foundation.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 # or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 # for more details.
 #
 # You should have received a copy of the GNU Lesser General Public License
 # along with this distribution; if not, write to:
 # Free Software Foundation, Inc.
 # 51 Franklin Street, Fifth Floor
 # Boston, MA  02110-1301  USA
 #
 hibernate.dialect org.hibernate.dialect.H2Dialect
 hibernate.connection.driver_class org.h2.Driver
 hibernate.connection.url jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE
 hibernate.connection.username sa
 
 hibernate.connection.pool_size 5
 
 hibernate.show_sql true
 
 hibernate.max_fetch_depth 5
 
 hibernate.cache.region_prefix hibernate.test
-hibernate.cache.provider_class org.hibernate.cache.internal.HashtableCacheProvider
+hibernate.cache.region.factory_class org.hibernate.testing.cache.CachingRegionFactory
diff --git a/hibernate-core/src/test/resources/log4j.properties b/hibernate-core/src/test/resources/log4j.properties
index 55c7e4968d..1f7070d830 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,10 +1,10 @@
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 
 
 log4j.rootLogger=info, stdout
 
 log4j.logger.org.hibernate.tool.hbm2ddl=debug
-
+log4j.logger.org.hibernate.testing.cache=debug
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCache.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCache.java
deleted file mode 100644
index cf8173f459..0000000000
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCache.java
+++ /dev/null
@@ -1,274 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
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
- */
-package org.hibernate.cache.internal;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import net.sf.ehcache.CacheManager;
-import net.sf.ehcache.Element;
-import org.jboss.logging.Logger;
-
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.CacheException;
-
-/**
- * EHCache plugin for Hibernate
- * <p/>
- * EHCache uses a {@link net.sf.ehcache.store.MemoryStore} and a
- * {@link net.sf.ehcache.store.DiskStore}.
- * The {@link net.sf.ehcache.store.DiskStore} requires that both keys and values be {@link java.io.Serializable}.
- * However the MemoryStore does not and in ehcache-1.2 nonSerializable Objects are permitted. They are discarded
- * if an attempt it made to overflow them to Disk or to replicate them to remote cache peers.
- *
- * @author Greg Luck
- * @author Emmanuel Bernard
- */
-@Deprecated
-public class EhCache implements Cache {
-
-    private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(EhCacheMessageLogger.class, EhCache.class.getName());
-
-	private static final int SIXTY_THOUSAND_MS = 60000;
-
-	private net.sf.ehcache.Ehcache cache;
-
-	/**
-	 * Creates a new Hibernate pluggable cache based on a cache name.
-	 * <p/>
-	 *
-	 * @param cache The underlying EhCache instance to use.
-	 */
-	public EhCache(net.sf.ehcache.Ehcache cache) {
-		this.cache = cache;
-	}
-
-	/**
-	 * Gets a value of an element which matches the given key.
-	 *
-	 * @param key the key of the element to return.
-	 * @return The value placed into the cache with an earlier put, or null if not found or expired
-	 * @throws org.hibernate.cache.CacheException
-	 */
-	public Object get(Object key) throws CacheException {
-		try {
-            LOG.debugf("Key: %s", key);
-            if (key == null) return null;
-            Element element = cache.get(key);
-            if (element == null) {
-                LOG.debugf("Element for %s is null", key);
-				return null;
-			}
-            return element.getObjectValue();
-		}
-		catch (net.sf.ehcache.CacheException e) {
-			throw new CacheException( e );
-		}
-	}
-
-	public Object read(Object key) throws CacheException {
-		return get( key );
-	}
-
-
-	/**
-	 * Puts an object into the cache.
-	 *
-	 * @param key   a key
-	 * @param value a value
-	 * @throws CacheException if the {@link CacheManager}
-	 *                        is shutdown or another {@link Exception} occurs.
-	 */
-	public void update(Object key, Object value) throws CacheException {
-		put( key, value );
-	}
-
-	/**
-	 * Puts an object into the cache.
-	 *
-	 * @param key   a key
-	 * @param value a value
-	 * @throws CacheException if the {@link CacheManager}
-	 *                        is shutdown or another {@link Exception} occurs.
-	 */
-	public void put(Object key, Object value) throws CacheException {
-		try {
-			Element element = new Element( key, value );
-			cache.put( element );
-		}
-		catch (IllegalArgumentException e) {
-			throw new CacheException( e );
-		}
-		catch (IllegalStateException e) {
-			throw new CacheException( e );
-		}
-		catch (net.sf.ehcache.CacheException e) {
-			throw new CacheException( e );
-		}
-
-	}
-
-	/**
-	 * Removes the element which matches the key.
-	 * <p/>
-	 * If no element matches, nothing is removed and no Exception is thrown.
-	 *
-	 * @param key the key of the element to remove
-	 * @throws CacheException
-	 */
-	public void remove(Object key) throws CacheException {
-		try {
-			cache.remove( key );
-		}
-		catch (ClassCastException e) {
-			throw new CacheException( e );
-		}
-		catch (IllegalStateException e) {
-			throw new CacheException( e );
-		}
-		catch (net.sf.ehcache.CacheException e) {
-			throw new CacheException( e );
-		}
-	}
-
-	/**
-	 * Remove all elements in the cache, but leave the cache
-	 * in a useable state.
-	 *
-	 * @throws CacheException
-	 */
-	public void clear() throws CacheException {
-		try {
-			cache.removeAll();
-		}
-		catch (IllegalStateException e) {
-			throw new CacheException( e );
-		}
-		catch (net.sf.ehcache.CacheException e) {
-			throw new CacheException( e );
-		}
-	}
-
-	/**
-	 * Remove the cache and make it unuseable.
-	 *
-	 * @throws CacheException
-	 */
-	public void destroy() throws CacheException {
-		try {
-			cache.getCacheManager().removeCache( cache.getName() );
-		}
-		catch (IllegalStateException e) {
-			throw new CacheException( e );
-		}
-		catch (net.sf.ehcache.CacheException e) {
-			throw new CacheException( e );
-		}
-	}
-
-	/**
-	 * Calls to this method should perform there own synchronization.
-	 * It is provided for distributed caches. Because EHCache is not distributed
-	 * this method does nothing.
-	 */
-	public void lock(Object key) throws CacheException {
-	}
-
-	/**
-	 * Calls to this method should perform there own synchronization.
-	 * It is provided for distributed caches. Because EHCache is not distributed
-	 * this method does nothing.
-	 */
-	public void unlock(Object key) throws CacheException {
-	}
-
-	/**
-	 * Gets the next timestamp;
-	 */
-	public long nextTimestamp() {
-		return Timestamper.next();
-	}
-
-	/**
-	 * Returns the lock timeout for this cache.
-	 */
-	public int getTimeout() {
-		// 60 second lock timeout
-		return Timestamper.ONE_MS * SIXTY_THOUSAND_MS;
-	}
-
-	public String getRegionName() {
-		return cache.getName();
-	}
-
-	/**
-	 * Warning: This method can be very expensive to run. Allow approximately 1 second
-	 * per 1MB of entries. Running this method could create liveness problems
-	 * because the object lock is held for a long period
-	 * <p/>
-	 *
-	 * @return the approximate size of memory ehcache is using for the MemoryStore for this cache
-	 */
-	public long getSizeInMemory() {
-		try {
-			return cache.calculateInMemorySize();
-		}
-		catch (Throwable t) {
-			return -1;
-		}
-	}
-
-	public long getElementCountInMemory() {
-		try {
-			return cache.getMemoryStoreSize();
-		}
-		catch (net.sf.ehcache.CacheException ce) {
-			throw new CacheException( ce );
-		}
-	}
-
-	public long getElementCountOnDisk() {
-		return cache.getDiskStoreSize();
-	}
-
-	public Map toMap() {
-		try {
-			Map result = new HashMap();
-			Iterator iter = cache.getKeys().iterator();
-			while ( iter.hasNext() ) {
-				Object key = iter.next();
-				result.put( key, cache.get( key ).getObjectValue() );
-			}
-			return result;
-		}
-		catch (Exception e) {
-			throw new CacheException( e );
-		}
-	}
-
-	@Override
-    public String toString() {
-		return "EHCache(" + getRegionName() + ')';
-	}
-
-}
\ No newline at end of file
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheProvider.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheProvider.java
deleted file mode 100644
index 5e5e367a28..0000000000
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheProvider.java
+++ /dev/null
@@ -1,171 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal;
-
-import java.net.URL;
-import java.util.Properties;
-import net.sf.ehcache.CacheManager;
-
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheProvider;
-import org.hibernate.cfg.Environment;
-import org.hibernate.internal.util.ConfigHelper;
-import org.hibernate.internal.util.StringHelper;
-import org.jboss.logging.Logger;
-
-/**
- * Cache Provider plugin for Hibernate
- *
- * Use <code>hibernate.cache.provider_class=org.hibernate.cache.internal.EhCacheProvider</code>
- * in Hibernate 3.x or later
- *
- * Taken from EhCache 0.9 distribution
- * @author Greg Luck
- * @author Emmanuel Bernard
- */
-/**
- * Cache Provider plugin for ehcache-1.2. New in this provider are ehcache support for multiple
- * Hibernate session factories, each with its own ehcache configuration, and non Serializable keys and values.
- * Ehcache-1.2 also has many other features such as cluster support and listeners, which can be used seamlessly simply
- * by configurion in ehcache.xml.
- * <p/>
- * Use <code>hibernate.cache.provider_class=org.hibernate.cache.internal.EhCacheProvider</code> in the Hibernate configuration
- * to enable this provider for Hibernate's second level cache.
- * <p/>
- * When configuring multiple ehcache CacheManagers, as you would where you have multiple Hibernate Configurations and
- * multiple SessionFactories, specify in each Hibernate configuration the ehcache configuration using
- * the property <code>hibernate.cache.provider_configuration_file_resource_path</code> An example to set an ehcache configuration
- * called ehcache-2.xml would be <code>hibernate.cache.provider_configuration_file_resource_path=/ehcache-2.xml</code>. If the leading
- * slash is not there one will be added. The configuration file will be looked for in the root of the classpath.
- * <p/>
- * Updated for ehcache-1.2. Note this provider requires ehcache-1.2.jar. Make sure ehcache-1.1.jar or earlier
- * is not in the classpath or it will not work.
- * <p/>
- * See http://ehcache.sf.net for documentation on ehcache
- * <p/>
- *
- * @author Greg Luck
- * @author Emmanuel Bernard
- */
-@Deprecated
-public class EhCacheProvider implements CacheProvider {
-
-    private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(EhCacheMessageLogger.class, EhCacheProvider.class.getName());
-
-	private CacheManager manager;
-
-    /**
-     * Builds a Cache.
-     * <p>
-     * Even though this method provides properties, they are not used.
-     * Properties for EHCache are specified in the ehcache.xml file.
-     * Configuration will be read from ehcache.xml for a cache declaration
-     * where the name attribute matches the name parameter in this builder.
-     *
-     * @param name the name of the cache. Must match a cache configured in ehcache.xml
-     * @param properties not used
-     * @return a newly built cache will be built and initialised
-     * @throws org.hibernate.cache.CacheException inter alia, if a cache of the same name already exists
-     */
-    public Cache buildCache(String name, Properties properties) throws CacheException {
-	    try {
-            net.sf.ehcache.Cache cache = manager.getCache(name);
-            if (cache == null) {
-                LOG.unableToFindConfiguration(name);
-                manager.addCache(name);
-                cache = manager.getCache(name);
-                LOG.debugf("Started EHCache region: %s", name);
-            }
-            return new EhCache(cache);
-	    }
-        catch (net.sf.ehcache.CacheException e) {
-            throw new CacheException(e);
-        }
-    }
-
-    /**
-     * Returns the next timestamp.
-     */
-    public long nextTimestamp() {
-        return Timestamper.next();
-    }
-
-	/**
-	 * Callback to perform any necessary initialization of the underlying cache implementation
-	 * during SessionFactory construction.
-	 *
-	 * @param properties current configuration settings.
-	 */
-	public void start(Properties properties) throws CacheException {
-		if (manager != null) {
-            LOG.attemptToRestartAlreadyStartedEhCacheProvider();
-            return;
-        }
-        try {
-            String configurationResourceName = null;
-            if (properties != null) {
-                configurationResourceName = (String) properties.get( Environment.CACHE_PROVIDER_CONFIG );
-            }
-            if ( StringHelper.isEmpty( configurationResourceName ) ) {
-                manager = new CacheManager();
-            } else {
-                URL url = loadResource(configurationResourceName);
-                manager = new CacheManager(url);
-            }
-        } catch (net.sf.ehcache.CacheException e) {
-			//yukky! Don't you have subclasses for that!
-			//TODO race conditions can happen here
-			if (e.getMessage().startsWith("Cannot parseConfiguration CacheManager. Attempt to create a new instance of " +
-                    "CacheManager using the diskStorePath")) {
-                throw new CacheException("Attempt to restart an already started EhCacheProvider. Use sessionFactory.close() " +
-                    " between repeated calls to buildSessionFactory. Consider using net.sf.ehcache.hibernate.SingletonEhCacheProvider."
-						, e );
-            }
-            throw e;
-        }
-	}
-
-	private URL loadResource(String configurationResourceName) {
-		URL url = ConfigHelper.locateConfig( configurationResourceName );
-        LOG.debugf("Creating EhCacheProvider from a specified resource: %s Resolved to URL: %s", configurationResourceName, url);
-        return url;
-    }
-
-	/**
-	 * Callback to perform any necessary cleanup of the underlying cache implementation
-	 * during SessionFactory.close().
-	 */
-	public void stop() {
-		if (manager != null) {
-            manager.shutdown();
-            manager = null;
-        }
-	}
-
-	public boolean isMinimalPutsEnabledByDefault() {
-		return false;
-	}
-
-}
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/SingletonEhCacheProvider.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/SingletonEhCacheProvider.java
deleted file mode 100644
index 86eec961ed..0000000000
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/SingletonEhCacheProvider.java
+++ /dev/null
@@ -1,185 +0,0 @@
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
-package org.hibernate.cache.internal;
-import java.net.URL;
-import java.util.Properties;
-import net.sf.ehcache.CacheManager;
-import net.sf.ehcache.util.ClassLoaderUtil;
-import org.jboss.logging.Logger;
-import org.jboss.logging.Logger.Level;
-
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheProvider;
-
-/**
- * Singleton cache Provider plugin for Hibernate 3.2 and ehcache-1.2. New in this provider is support for
- * non Serializable keys and values. This provider works as a Singleton. No matter how many Hibernate Configurations
- * you have, only one ehcache CacheManager is used. See EhCacheProvider for a non-singleton implementation.
- * <p/>
- * Ehcache-1.2 also has many other features such as cluster support and listeners, which can be used seamlessly simply
- * by configurion in ehcache.xml.
- * <p/>
- * Use <code>hibernate.cache.provider_class=net.sf.ehcache.hibernate.SingletonEhCacheProvider</code> in the Hibernate configuration
- * to enable this provider for Hibernate's second level cache.
- * <p/>
- * Updated for ehcache-1.2. Note this provider requires ehcache-1.2.jar. Make sure ehcache-1.1.jar or earlier
- * is not in the classpath or it will not work.
- * <p/>
- * See http://ehcache.sf.net for documentation on ehcache
- * <p/>
- *
- * @author Greg Luck
- * @author Emmanuel Bernard
- * @version $Id: SingletonEhCacheProvider.java 744 2008-08-16 20:10:49Z gregluck $
- */
-@Deprecated
-public final class SingletonEhCacheProvider implements CacheProvider {
-
-	/**
-	 * The Hibernate system property specifying the location of the ehcache configuration file name.
-	 * <p/
-	 * If not set, ehcache.xml will be looked for in the root of the classpath.
-	 * <p/>
-	 * If set to say ehcache-1.xml, ehcache-1.xml will be looked for in the root of the classpath.
-	 */
-	public static final String NET_SF_EHCACHE_CONFIGURATION_RESOURCE_NAME = "net.sf.ehcache.configurationResourceName";
-
-    private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(EhCacheMessageLogger.class, SingletonEhCacheProvider.class.getName());
-
-	/**
-	 * To be backwardly compatible with a lot of Hibernate code out there, allow multiple starts and stops on the
-	 * one singleton CacheManager. Keep a count of references to only stop on when only one reference is held.
-	 */
-	private static int referenceCount;
-
-	private CacheManager manager;
-
-
-	/**
-	 * Builds a Cache.
-	 * <p/>
-	 * Even though this method provides properties, they are not used.
-	 * Properties for EHCache are specified in the ehcache.xml file.
-	 * Configuration will be read from ehcache.xml for a cache declaration
-	 * where the name attribute matches the name parameter in this builder.
-	 *
-	 * @param name the name of the cache. Must match a cache configured in ehcache.xml
-	 * @param properties not used
-	 *
-	 * @return a newly built cache will be built and initialised
-	 *
-	 * @throws org.hibernate.cache.CacheException
-	 *          inter alia, if a cache of the same name already exists
-	 */
-	public final Cache buildCache(String name, Properties properties) throws CacheException {
-		try {
-			net.sf.ehcache.Ehcache cache = manager.getEhcache( name );
-			if ( cache == null ) {
-                LOG.unableToFindEhCacheConfiguration(name);
-				manager.addCache( name );
-				cache = manager.getEhcache( name );
-                LOG.debugf("Started EHCache region: %s", name);
-			}
-			return new EhCache( cache );
-		}
-		catch ( net.sf.ehcache.CacheException e ) {
-			throw new CacheException( e );
-		}
-	}
-
-	/**
-	 * Returns the next timestamp.
-	 */
-	public final long nextTimestamp() {
-		return Timestamper.next();
-	}
-
-	/**
-	 * Callback to perform any necessary initialization of the underlying cache implementation
-	 * during SessionFactory construction.
-	 * <p/>
-	 *
-	 * @param properties current configuration settings.
-	 */
-	public final void start(Properties properties) throws CacheException {
-		String configurationResourceName = null;
-		if ( properties != null ) {
-			configurationResourceName = ( String ) properties.get( NET_SF_EHCACHE_CONFIGURATION_RESOURCE_NAME );
-		}
-		if ( configurationResourceName == null || configurationResourceName.length() == 0 ) {
-			manager = CacheManager.create();
-			referenceCount++;
-		}
-		else {
-			if ( !configurationResourceName.startsWith( "/" ) ) {
-				configurationResourceName = "/" + configurationResourceName;
-                if (LOG.isDebugEnabled()) {
-                    LOG.debugf("Prepending / to %s. It should be placed in the root of the classpath rather than in a package.",
-                               configurationResourceName);
-				}
-			}
-			URL url = loadResource( configurationResourceName );
-			manager = CacheManager.create( url );
-			referenceCount++;
-		}
-	}
-
-	private URL loadResource(String configurationResourceName) {
-		ClassLoader standardClassloader = ClassLoaderUtil.getStandardClassLoader();
-		URL url = null;
-        if (standardClassloader != null) url = standardClassloader.getResource(configurationResourceName);
-        if (url == null) url = this.getClass().getResource(configurationResourceName);
-        if (LOG.isDebugEnabled()) LOG.debugf("Creating EhCacheProvider from a specified resource: %s Resolved to URL: %s",
-                                             configurationResourceName,
-                                             url);
-        if (url == null && LOG.isEnabled(Level.WARN)) LOG.unableToLoadConfiguration(configurationResourceName);
-		return url;
-	}
-
-	/**
-	 * Callback to perform any necessary cleanup of the underlying cache implementation
-	 * during SessionFactory.close().
-	 */
-	public void stop() {
-		if ( manager != null ) {
-			referenceCount--;
-			if ( referenceCount == 0 ) {
-				manager.shutdown();
-			}
-			manager = null;
-		}
-	}
-
-	/**
-	 * Not sure what this is supposed to do.
-	 *
-	 * @return false to be safe
-	 */
-	public final boolean isMinimalPutsEnabledByDefault() {
-		return false;
-	}
-
-}
diff --git a/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheRegionTest.java b/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheRegionTest.java
index 0ae210e9b8..62588a4ed6 100644
--- a/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheRegionTest.java
+++ b/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheRegionTest.java
@@ -1,31 +1,31 @@
 package org.hibernate.test.cache.ehcache;
 
-import org.hibernate.cache.spi.ReadWriteCache.Item;
-import org.hibernate.cache.internal.EhCacheProvider;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import java.util.Map;
 
+import net.sf.ehcache.hibernate.EhCacheRegionFactory;
+
 /**
  * @author Alex Snaps
  */
 public class EhCacheRegionTest extends EhCacheTest {
 	@Override
 	protected void configCache(final Configuration cfg) {
-		cfg.setProperty( Environment.CACHE_PROVIDER, EhCacheProvider.class.getName() );
+		cfg.setProperty( Environment.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName() );
 		cfg.setProperty( Environment.CACHE_PROVIDER_CONFIG, "ehcache.xml" );
 	}
 
 	@Override
 	protected Map getMapFromCacheEntry(final Object entry) {
 		final Map map;
-		if ( entry instanceof Item ) {
-			map = (Map) ( (Item) entry ).getValue();
-		}
-		else {
+//		if ( entry instanceof Item ) {
+//			map = (Map) ( (Item) entry ).getValue();
+//		}
+//		else {
 			map = (Map) entry;
-		}
+//		}
 		return map;
 	}
 }
diff --git a/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheTest.java b/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheTest.java
index 8df295409f..c0190c8b0c 100644
--- a/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheTest.java
+++ b/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheTest.java
@@ -1,208 +1,207 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.cache.ehcache;
 
 import java.util.Map;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.cache.internal.EhCacheProvider;
-import org.hibernate.cache.spi.ReadWriteCache;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  * @author Alex Snaps
  */
 public abstract class EhCacheTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String getBaseForMappings() {
 		return "org/hibernate/test/cache/ehcache/";
 	}
 
 	@Override
 	public String[] getMappings() {
 		return new String[] { "Item.hbm.xml" };
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return "read-write";
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.USE_STRUCTURED_CACHE, "true" );
 		configCache(cfg);
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName() );
 	}
 
 	protected abstract void configCache(final Configuration cfg);
 
 	@Test
 	public void testQueryCacheInvalidation() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Item i = new Item();
 		i.setName("widget");
 		i.setDescription("A really top-quality, full-featured widget.");
 		s.persist(i);
 		t.commit();
 		s.close();
 
 		SecondLevelCacheStatistics slcs = s.getSessionFactory().getStatistics()
 				.getSecondLevelCacheStatistics( Item.class.getName() );
 
 		assertEquals( slcs.getPutCount(), 1 );
 		assertEquals( slcs.getElementCountInMemory(), 1 );
 		assertEquals( slcs.getEntries().size(), 1 );
 
 		s = openSession();
 		t = s.beginTransaction();
 		i = (Item) s.get( Item.class, i.getId() );
 
 		assertEquals( slcs.getHitCount(), 1 );
 		assertEquals( slcs.getMissCount(), 0 );
 
 		i.setDescription("A bog standard item");
 
 		t.commit();
 		s.close();
 
 		assertEquals( slcs.getPutCount(), 2 );
 
 		Object entry = slcs.getEntries().get( i.getId() );
 		Map map;
 		map = getMapFromCacheEntry(entry);
 		assertTrue( map.get("description").equals("A bog standard item") );
 		assertTrue( map.get("name").equals("widget") );
 
 		// cleanup
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( i );
 		t.commit();
 		s.close();
 	}
 
 	protected abstract Map getMapFromCacheEntry(final Object entry);
 
 	@Test
 	public void testEmptySecondLevelCacheEntry() throws Exception {
 		sessionFactory().getCache().evictEntityRegion( Item.class.getName() );
 		Statistics stats = sessionFactory().getStatistics();
 		stats.clear();
 		SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics( Item.class.getName() );
         Map cacheEntries = statistics.getEntries();
 		assertEquals( 0, cacheEntries.size() );
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing", "UnusedAssignment"})
 	@Test
 	public void testStaleWritesLeaveCacheConsistent() {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		VersionedItem item = new VersionedItem();
 		item.setName( "steve" );
 		item.setDescription( "steve's item" );
 		s.save( item );
 		txn.commit();
 		s.close();
 
 		Long initialVersion = item.getVersion();
 
 		// manually revert the version property
 		item.setVersion( Long.valueOf( item.getVersion().longValue() - 1 ) );
 
 		try {
 			s = openSession();
 			txn = s.beginTransaction();
 			s.update( item );
 			txn.commit();
 			s.close();
 			fail( "expected stale write to fail" );
 		}
 		catch( Throwable expected ) {
 			// expected behavior here
 			if ( txn != null ) {
 				try {
 					txn.rollback();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		finally {
 			if ( s != null && s.isOpen() ) {
 				try {
 					s.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 
 		// check the version value in the cache...
 		SecondLevelCacheStatistics slcs = sessionFactory().getStatistics()
 				.getSecondLevelCacheStatistics( VersionedItem.class.getName() );
 
 		Object entry = slcs.getEntries().get( item.getId() );
 		Long cachedVersionValue;
-		if ( entry instanceof ReadWriteCache.Lock ) {
-			//FIXME don't know what to test here
-			cachedVersionValue = Long.valueOf( ((ReadWriteCache.Lock) entry).getUnlockTimestamp() );
-		} else if(entry.getClass().getName().equals("net.sf.ehcache.hibernate.strategy.AbstractReadWriteEhcacheAccessStrategy$Lock")) {
+//		if ( entry instanceof ReadWriteCache.Lock ) {
+//			//FIXME don't know what to test here
+//			cachedVersionValue = Long.valueOf( ((ReadWriteCache.Lock) entry).getUnlockTimestamp() );
+//		} else
+		if(entry.getClass().getName().equals("net.sf.ehcache.hibernate.strategy.AbstractReadWriteEhcacheAccessStrategy$Lock")) {
 			//FIXME don't know what to test here
 		} else {
 			cachedVersionValue = ( Long ) getMapFromCacheEntry(entry).get( "_version" );
 			assertEquals( initialVersion.longValue(), cachedVersionValue.longValue() );
 		}
 
 
 		// cleanup
 		s = openSession();
 		txn = s.beginTransaction();
 		item = ( VersionedItem ) s.load( VersionedItem.class, item.getId() );
 		s.delete( item );
 		txn.commit();
 		s.close();
 
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/bundles/cfgxmlpar/org/hibernate/ejb/test/pack/cfgxmlpar/hibernate.cfg.xml b/hibernate-entitymanager/src/test/bundles/cfgxmlpar/org/hibernate/ejb/test/pack/cfgxmlpar/hibernate.cfg.xml
index afcd6d6778..9a806c5241 100644
--- a/hibernate-entitymanager/src/test/bundles/cfgxmlpar/org/hibernate/ejb/test/pack/cfgxmlpar/hibernate.cfg.xml
+++ b/hibernate-entitymanager/src/test/bundles/cfgxmlpar/org/hibernate/ejb/test/pack/cfgxmlpar/hibernate.cfg.xml
@@ -1,28 +1,28 @@
 <!DOCTYPE hibernate-configuration PUBLIC
         "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
         "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
 
 <hibernate-configuration>
     <session-factory>
         <property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
         <property name="hibernate.connection.driver_class">org.h2.Driver</property>
         <property name="hibernate.connection.username">sa</property>
         <property name="hibernate.connection.password"></property>
         <property name="hibernate.connection.url">jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE</property>
         <property name="hibernate.cache.use_query_cache">true</property>
         <property name="hibernate.cache.region_prefix">hibernate.test</property>
         <property name="hibernate.jdbc.use_streams_for_binary">true</property>
         <property name="hibernate.jdbc.batch_size">0</property>
         <property name="hibernate.max_fetch_depth">3</property>
         <property name="hibernate.hbm2ddl.auto">create-drop</property>
         <property name="hibernate.generate_statistics">true</property>
-        <property name="hibernate.cache.provider_class">org.hibernate.cache.internal.HashtableCacheProvider</property>
+        <property name="hibernate.cache.region.factory_class">org.hibernate.testing.cache.CachingRegionFactory</property>
         <mapping class="org.hibernate.ejb.test.Item"/>
         <mapping class="org.hibernate.ejb.test.Cat"/>
         <mapping class="org.hibernate.ejb.test.Kitten"/>
         <mapping class="org.hibernate.ejb.test.Distributor"/>
         <class-cache class="org.hibernate.ejb.test.Item" usage="read-write"/>
         <collection-cache collection="org.hibernate.ejb.test.Item.distributors" usage="read-write" region="RegionName"/>
         <event type="pre-insert"/>
     </session-factory>
 </hibernate-configuration>
\ No newline at end of file
diff --git a/hibernate-entitymanager/src/test/bundles/defaultpar/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/defaultpar/META-INF/persistence.xml
index 5172fcaa65..bc9e2a2052 100644
--- a/hibernate-entitymanager/src/test/bundles/defaultpar/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/defaultpar/META-INF/persistence.xml
@@ -1,20 +1,20 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!-- example of a default persistence.xml -->
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="defaultpar">
         <class>org.hibernate.ejb.test.pack.defaultpar.Lighter</class>
         <validation-mode>CALLBACK</validation-mode>
         <properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+            <property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/defaultpar_1_0/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/defaultpar_1_0/META-INF/persistence.xml
index ed4065d754..c64713de3a 100644
--- a/hibernate-entitymanager/src/test/bundles/defaultpar_1_0/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/defaultpar_1_0/META-INF/persistence.xml
@@ -1,19 +1,19 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!-- example of a default persistence.xml -->
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"
              version="1.0">
     <persistence-unit name="defaultpar_1_0">
         <class>org.hibernate.ejb.test.pack.defaultpar.Lighter</class>
         <properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/excludehbmpar/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/excludehbmpar/META-INF/persistence.xml
index 7e0e9a8755..8b77ab369d 100644
--- a/hibernate-entitymanager/src/test/bundles/excludehbmpar/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/excludehbmpar/META-INF/persistence.xml
@@ -1,20 +1,20 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!-- example of a default persistence.xml -->
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="excludehbmpar" transaction-type="RESOURCE_LOCAL">
         <mapping-file>META-INF/orm2.xml</mapping-file>
         <properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
             <property name="hibernate.archive.autodetection" value="class"/>
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/explicitpar/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/explicitpar/META-INF/persistence.xml
index 25d45dbd85..34eca4c6da 100644
--- a/hibernate-entitymanager/src/test/bundles/explicitpar/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/explicitpar/META-INF/persistence.xml
@@ -1,47 +1,47 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="manager1" transaction-type="RESOURCE_LOCAL">
         <jar-file>@buildDirName@/packages/externaljar.jar</jar-file>
         <class>org.hibernate.ejb.test.Cat</class>
 		<class>org.hibernate.ejb.test.Kitten</class>
         <class>org.hibernate.ejb.test.Distributor</class>
         <class>org.hibernate.ejb.test.Item</class>
         <exclude-unlisted-classes>true</exclude-unlisted-classes>
         <properties>
             <!-- custom scanner test -->
             <property name="hibernate.ejb.resource_scanner" value="org.hibernate.ejb.test.packaging.CustomScanner"/>
 
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.cache.use_query_cache" value="true"/>
             <property name="hibernate.cache.region_prefix" value="hibernate.test"/>
             <property name="hibernate.jdbc.use_streams_for_binary" value="true"/>
             <property name="hibernate.jdbc.batch_size" value="0"/>
             <property name="hibernate.max_fetch_depth" value="3"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
             <property name="hibernate.generate_statistics" value="true"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
             <property name="hibernate.ejb.naming_strategy" value="org.hibernate.ejb.test.MyNamingStrategy"/>
             <!-- test naming strategy and fall back to element content -->
             <!-- property name="hibernate.ejb.naming_strategy">org.hibernate.ejb.test.MyNamingStrategy</property -->
 
             <!-- cache configuration -->
             <property name="hibernate.ejb.classcache.org.hibernate.ejb.test.Item" value="read-write"/>
             <property name="hibernate.ejb.collectioncache.org.hibernate.ejb.test.Item.distributors"
                       value="read-write, RegionName"/>
 
             <!-- event overriding -->
             <property name="hibernate.ejb.event.pre-insert" value="org.hibernate.ejb.test.NoOpListener"/>
             <!-- remove JACC and validator -->
 
             <!-- alternatively to <class> and <property> declarations, you can use a regular hibernate.cfg.xml file -->
             <!-- property name="hibernate.ejb.cfgfile" value="/org/hibernate/ejb/test/hibernate.cfg.xml"/ -->
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/explodedpar/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/explodedpar/META-INF/persistence.xml
index 7420f88654..1c40c44874 100644
--- a/hibernate-entitymanager/src/test/bundles/explodedpar/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/explodedpar/META-INF/persistence.xml
@@ -1,18 +1,18 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!-- example of a default persistence.xml -->
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="explodedpar" transaction-type="RESOURCE_LOCAL">
         <properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/overridenpar/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/overridenpar/META-INF/persistence.xml
index 75e34638a5..84f4d72bac 100644
--- a/hibernate-entitymanager/src/test/bundles/overridenpar/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/overridenpar/META-INF/persistence.xml
@@ -1,14 +1,14 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="overridenpar">
         <jta-data-source>java:/unreachableDS</jta-data-source>
 		<properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/space par/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/space par/META-INF/persistence.xml
index 879b62bb74..f7ffd56cc5 100644
--- a/hibernate-entitymanager/src/test/bundles/space par/META-INF/persistence.xml	
+++ b/hibernate-entitymanager/src/test/bundles/space par/META-INF/persistence.xml	
@@ -1,18 +1,18 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!-- example of a default persistence.xml -->
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="space par">
         <properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
         </properties>
     </persistence-unit>
 </persistence>
diff --git a/hibernate-entitymanager/src/test/bundles/war/WEB-INF/classes/META-INF/persistence.xml b/hibernate-entitymanager/src/test/bundles/war/WEB-INF/classes/META-INF/persistence.xml
index 6b575739ee..0c5d311825 100644
--- a/hibernate-entitymanager/src/test/bundles/war/WEB-INF/classes/META-INF/persistence.xml
+++ b/hibernate-entitymanager/src/test/bundles/war/WEB-INF/classes/META-INF/persistence.xml
@@ -1,20 +1,20 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <!-- example of a default persistence.xml -->
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
              version="2.0">
     <persistence-unit name="defaultpar">
         <class>org.hibernate.ejb.test.pack.defaultpar.Lighter</class>
         <properties>
             <property name="hibernate.dialect" value="@db.dialect@"/>
             <property name="hibernate.connection.driver_class" value="@jdbc.driver@"/>
             <property name="hibernate.connection.username" value="@jdbc.user@"/>
             <property name="hibernate.connection.password" value="@jdbc.pass@"/>
             <property name="hibernate.connection.url" value="@jdbc.url@"/>
             <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
-            <property name="hibernate.cache.provider_class" value="org.hibernate.cache.internal.HashtableCacheProvider"/>
+			<property name="hibernate.cache.region.factory_class" value="org.hibernate.testing.cache.CachingRegionFactory" />
         </properties>
     </persistence-unit>
 </persistence>
 
diff --git a/hibernate-entitymanager/src/test/resources/hibernate.properties b/hibernate-entitymanager/src/test/resources/hibernate.properties
index 1425248df7..8b06686d9f 100644
--- a/hibernate-entitymanager/src/test/resources/hibernate.properties
+++ b/hibernate-entitymanager/src/test/resources/hibernate.properties
@@ -1,38 +1,38 @@
 #
 # Hibernate, Relational Persistence for Idiomatic Java
 #
 # Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 # indicated by the @author tags or express copyright attribution
 # statements applied by the authors.  All third-party contributions are
 # distributed under license by Red Hat Inc.
 #
 # This copyrighted material is made available to anyone wishing to use, modify,
 # copy, or redistribute it subject to the terms and conditions of the GNU
 # Lesser General Public License, as published by the Free Software Foundation.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 # or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 # for more details.
 #
 # You should have received a copy of the GNU Lesser General Public License
 # along with this distribution; if not, write to:
 # Free Software Foundation, Inc.
 # 51 Franklin Street, Fifth Floor
 # Boston, MA  02110-1301  USA
 #
 hibernate.dialect org.hibernate.dialect.H2Dialect
 hibernate.connection.driver_class org.h2.Driver
 hibernate.connection.url jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE
 hibernate.connection.username sa
 
 hibernate.connection.pool_size 5
 
 hibernate.show_sql true
 
 hibernate.max_fetch_depth 5
 
 hibernate.cache.region_prefix hibernate.test
-hibernate.cache.provider_class org.hibernate.cache.internal.HashtableCacheProvider
+hibernate.cache.region.factory_class org.hibernate.testing.cache.CachingRegionFactory
 
 hibernate.jdbc.batch_size 0
diff --git a/hibernate-entitymanager/src/test/resources/org/hibernate/ejb/test/hibernate.cfg.xml b/hibernate-entitymanager/src/test/resources/org/hibernate/ejb/test/hibernate.cfg.xml
index 6de66dd45c..c540676068 100644
--- a/hibernate-entitymanager/src/test/resources/org/hibernate/ejb/test/hibernate.cfg.xml
+++ b/hibernate-entitymanager/src/test/resources/org/hibernate/ejb/test/hibernate.cfg.xml
@@ -1,27 +1,27 @@
 <!DOCTYPE hibernate-configuration PUBLIC
         "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
         "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
 
 <hibernate-configuration>
     <session-factory>
         <property name="hibernate.dialect">org.hibernate.dialect.HSQLDialect</property>
         <property name="hibernate.connection.driver_class">org.hsqldb.jdbcDriver</property>
         <property name="hibernate.connection.username">sa</property>
         <property name="hibernate.connection.password"></property>
         <property name="hibernate.connection.url">jdbc:hsqldb:.</property>
         <property name="hibernate.cache.use_query_cache">true</property>
         <property name="hibernate.cache.region_prefix">hibernate.test</property>
         <property name="hibernate.jdbc.use_streams_for_binary">true</property>
         <property name="hibernate.jdbc.batch_size">0</property>
         <property name="hibernate.max_fetch_depth">3</property>
         <property name="hibernate.hbm2ddl.auto">create-drop</property>
         <property name="hibernate.generate_statistics">true</property>
-        <property name="hibernate.cache.provider_class">org.hibernate.cache.internal.HashtableCacheProvider</property>
+        <property name="hibernate.cache.region.factory_class">org.hibernate.testing.cache.CachingRegionFactory</property>
         <mapping class="org.hibernate.ejb.test.Item"/>
         <mapping class="org.hibernate.ejb.test.Cat"/>
 		<mapping class="org.hibernate.ejb.test.Kitten"/>
         <mapping class="org.hibernate.ejb.test.Distributor"/>
         <class-cache class="org.hibernate.ejb.test.Item" usage="read-write"/>
         <collection-cache collection="org.hibernate.ejb.test.Item.distributors" usage="read-write" region="RegionName"/>
     </session-factory>
 </hibernate-configuration>
\ No newline at end of file
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/cache/HibernateSecLvlQueryCache.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/cache/HibernateSecLvlQueryCache.java
index a3a399f54b..817f614ed6 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/cache/HibernateSecLvlQueryCache.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/cache/HibernateSecLvlQueryCache.java
@@ -1,72 +1,72 @@
 package org.hibernate.envers.test.integration.cache;
 
 import org.hibernate.MappingException;
-import org.hibernate.cache.internal.EhCacheProvider;
+import org.hibernate.cache.internal.EhCacheRegionFactory;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.envers.RevisionType;
 import org.hibernate.envers.query.criteria.RevisionTypeAuditExpression;
 import org.hibernate.envers.test.AbstractSessionTest;
 import org.hibernate.envers.test.Priority;
 import org.hibernate.envers.test.entities.StrTestEntity;
 import org.hibernate.testing.TestForIssue;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.net.URISyntaxException;
 
 /**
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class HibernateSecLvlQueryCache extends AbstractSessionTest {
     private static final String QUERY_CACHE_REGION = "queryCacheRegion";
 
     @Override
     protected void initMappings() throws MappingException, URISyntaxException {
         config.addAnnotatedClass(StrTestEntity.class);
         config.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
         config.setProperty(Environment.USE_QUERY_CACHE, "true");
-        config.setProperty(Environment.CACHE_PROVIDER, EhCacheProvider.class.getName());
+        config.setProperty(Environment.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName());
         config.setProperty(Environment.CACHE_PROVIDER_CONFIG, "ehcache-test.xml");
     }
 
     @Test
     @Priority(10)
     public void initData() {
         // Revision 1
         getSession().getTransaction().begin();
         StrTestEntity ste = new StrTestEntity("data");
         getSession().persist(ste);
         getSession().getTransaction().commit();
 
         // Evicting old query cache.
         getSession().getSessionFactory().getCache().evictQueryRegion(QUERY_CACHE_REGION);
     }
 
     @Test
     @TestForIssue(jiraKey="HHH-5025")
     public void testSecLvlCacheWithRevisionTypeDiskPersistent() throws InterruptedException {
         // Cached query that requires serializing RevisionType variable when persisting to disk.
         getAuditReader().createQuery().forEntitiesAtRevision(StrTestEntity.class, 1)
                                       .add(new RevisionTypeAuditExpression(RevisionType.ADD, "="))
                                       .setCacheable(true).setCacheRegion(QUERY_CACHE_REGION).getResultList();
 
         // Waiting max 3 seconds for cached data to persist to disk.
         for (int i=0; i<30; i++) {
             if (getQueryCacheSize() > 0) {
                 break;
             }
 
             Thread.sleep(100);
         }
 
         Assert.assertTrue(getQueryCacheSize() > 0);
     }
 
     private int getQueryCacheSize() {
         // Statistics are not notified about persisting cached data failure. However, cache entry gets evicted.
         // See DiskWriteTask.call() method (net.sf.ehcache.store.compound.factories.DiskStorageFactory).
         SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) getSession().getSessionFactory();
         return sessionFactoryImplementor.getQueryCache(QUERY_CACHE_REGION).getRegion().toMap().size();
     }
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
new file mode 100644
index 0000000000..62b9cc960e
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
@@ -0,0 +1,360 @@
+package org.hibernate.testing.cache;
+
+import java.io.Serializable;
+import java.util.Comparator;
+import java.util.UUID;
+import java.util.concurrent.atomic.AtomicLong;
+import java.util.concurrent.locks.ReentrantReadWriteLock;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+abstract class AbstractReadWriteAccessStrategy extends BaseRegionAccessStrategy {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, AbstractReadWriteAccessStrategy.class.getName()
+	);
+	private final UUID uuid = UUID.randomUUID();
+	private final AtomicLong nextLockId = new AtomicLong();
+	private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
+	protected java.util.concurrent.locks.Lock readLock = reentrantReadWriteLock.readLock();
+	protected java.util.concurrent.locks.Lock writeLock = reentrantReadWriteLock.writeLock();
+
+	/**
+	 * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
+	 * after the start of this transaction.
+	 */
+	@Override
+	public final Object get(Object key, long txTimestamp) throws CacheException {
+		try {
+			readLock.lock();
+			Lockable item = (Lockable) getInternalRegion().get( key );
+
+			boolean readable = item != null && item.isReadable( txTimestamp );
+			if ( readable ) {
+				return item.getValue();
+			}
+			else {
+				return null;
+			}
+		}
+		finally {
+			readLock.unlock();
+		}
+	}
+
+	abstract Comparator getVersionComparator();
+
+	/**
+	 * Returns <code>false</code> and fails to put the value if there is an existing un-writeable item mapped to this
+	 * key.
+	 */
+	@Override
+	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+			throws CacheException {
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) getInternalRegion().get( key );
+			boolean writeable = item == null || item.isWriteable( txTimestamp, version, getVersionComparator() );
+			if ( writeable ) {
+				getInternalRegion().put( key, new Item( value, version, getInternalRegion().nextTimestamp() ) );
+				return true;
+			}
+			else {
+				return false;
+			}
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+	/**
+	 * Soft-lock a cache item.
+	 */
+	public final SoftLock lockItem(Object key, Object version) throws CacheException {
+
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) getInternalRegion().get( key );
+			long timeout = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
+			final Lock lock = ( item == null ) ? new Lock( timeout, uuid, nextLockId(), version ) : item.lock(
+					timeout,
+					uuid,
+					nextLockId()
+			);
+			getInternalRegion().put( key, lock );
+			return lock;
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+	/**
+	 * Soft-unlock a cache item.
+	 */
+	public final void unlockItem(Object key, SoftLock lock) throws CacheException {
+
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) getInternalRegion().get( key );
+
+			if ( ( item != null ) && item.isUnlockable( lock ) ) {
+				decrementLock( key, (Lock) item );
+			}
+			else {
+				handleLockExpiry( key, item );
+			}
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+	private long nextLockId() {
+		return nextLockId.getAndIncrement();
+	}
+
+	/**
+	 * Unlock and re-put the given key, lock combination.
+	 */
+	protected void decrementLock(Object key, Lock lock) {
+		lock.unlock( getInternalRegion().nextTimestamp() );
+		getInternalRegion().put( key, lock );
+	}
+
+	/**
+	 * Handle the timeout of a previous lock mapped to this key
+	 */
+	protected void handleLockExpiry(Object key, Lockable lock) {
+		LOG.expired(key);
+		long ts = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
+		// create new lock that times out immediately
+		Lock newLock = new Lock( ts, uuid, nextLockId.getAndIncrement(), null );
+		newLock.unlock( ts );
+		getInternalRegion().put( key, newLock );
+	}
+
+	/**
+	 * Interface type implemented by all wrapper objects in the cache.
+	 */
+	protected static interface Lockable {
+
+		/**
+		 * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
+		 */
+		public boolean isReadable(long txTimestamp);
+
+		/**
+		 * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
+		 * transaction started at the given time.
+		 */
+		public boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
+
+		/**
+		 * Returns the enclosed value.
+		 */
+		public Object getValue();
+
+		/**
+		 * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
+		 */
+		public boolean isUnlockable(SoftLock lock);
+
+		/**
+		 * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
+		 * time.  The returned Lock object can be used to unlock the entry in the future.
+		 */
+		public Lock lock(long timeout, UUID uuid, long lockId);
+	}
+
+	/**
+	 * Wrapper type representing unlocked items.
+	 */
+	protected final static class Item implements Serializable, Lockable {
+
+		private static final long serialVersionUID = 1L;
+		private final Object value;
+		private final Object version;
+		private final long timestamp;
+
+		/**
+		 * Creates an unlocked item wrapping the given value with a version and creation timestamp.
+		 */
+		Item(Object value, Object version, long timestamp) {
+			this.value = value;
+			this.version = version;
+			this.timestamp = timestamp;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public boolean isReadable(long txTimestamp) {
+			return txTimestamp > timestamp;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
+			return version != null && versionComparator.compare( version, newVersion ) < 0;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public Object getValue() {
+			return value;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public boolean isUnlockable(SoftLock lock) {
+			return false;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public Lock lock(long timeout, UUID uuid, long lockId) {
+			return new Lock( timeout, uuid, lockId, version );
+		}
+	}
+
+	/**
+	 * Wrapper type representing locked items.
+	 */
+	protected final static class Lock implements Serializable, Lockable, SoftLock {
+
+		private static final long serialVersionUID = 2L;
+
+		private final UUID sourceUuid;
+		private final long lockId;
+		private final Object version;
+
+		private long timeout;
+		private boolean concurrent;
+		private int multiplicity = 1;
+		private long unlockTimestamp;
+
+		/**
+		 * Creates a locked item with the given identifiers and object version.
+		 */
+		Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
+			this.timeout = timeout;
+			this.lockId = lockId;
+			this.version = version;
+			this.sourceUuid = sourceUuid;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public boolean isReadable(long txTimestamp) {
+			return false;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
+			if ( txTimestamp > timeout ) {
+				// if timedout then allow write
+				return true;
+			}
+			if ( multiplicity > 0 ) {
+				// if still locked then disallow write
+				return false;
+			}
+			return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(
+					version,
+					newVersion
+			) < 0;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public Object getValue() {
+			return null;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public boolean isUnlockable(SoftLock lock) {
+			return equals( lock );
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		@Override
+		public boolean equals(Object o) {
+			if ( o == this ) {
+				return true;
+			}
+			else if ( o instanceof Lock ) {
+				return ( lockId == ( (Lock) o ).lockId ) && sourceUuid.equals( ( (Lock) o ).sourceUuid );
+			}
+			else {
+				return false;
+			}
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		@Override
+		public int hashCode() {
+			int hash = ( sourceUuid != null ? sourceUuid.hashCode() : 0 );
+			int temp = (int) lockId;
+			for ( int i = 1; i < Long.SIZE / Integer.SIZE; i++ ) {
+				temp ^= ( lockId >>> ( i * Integer.SIZE ) );
+			}
+			return hash + temp;
+		}
+
+		/**
+		 * Returns true if this Lock has been concurrently locked by more than one transaction.
+		 */
+		public boolean wasLockedConcurrently() {
+			return concurrent;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public Lock lock(long timeout, UUID uuid, long lockId) {
+			concurrent = true;
+			multiplicity++;
+			this.timeout = timeout;
+			return this;
+		}
+
+		/**
+		 * Unlocks this Lock, and timestamps the unlock event.
+		 */
+		public void unlock(long timestamp) {
+			if ( --multiplicity == 0 ) {
+				unlockTimestamp = timestamp;
+			}
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public String toString() {
+			StringBuilder sb = new StringBuilder( "Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId );
+			return sb.toString();
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseGeneralDataRegionAdapter.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java
similarity index 55%
rename from hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseGeneralDataRegionAdapter.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java
index 37a17fb75f..ad785d1837 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseGeneralDataRegionAdapter.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java
@@ -1,57 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
+package org.hibernate.testing.cache;
 
-import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.Cache;
+import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.GeneralDataRegion;
-import org.hibernate.cfg.Settings;
+import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 
 /**
- * {@inheritDoc}
- *
- * @author Steve Ebersole
+ * @author Strong Liu
  */
-public abstract class BaseGeneralDataRegionAdapter extends BaseRegionAdapter implements GeneralDataRegion {
-
-	protected BaseGeneralDataRegionAdapter(Cache underlyingCache, Settings settings) {
-		super( underlyingCache, settings );
-	}
+class BaseCollectionRegionAccessStrategy extends BaseRegionAccessStrategy implements CollectionRegionAccessStrategy {
+	private final CollectionRegionImpl region;
 
-	public Object get(Object key) throws CacheException {
-		return underlyingCache.get( key );
+	@Override
+	protected BaseGeneralDataRegion getInternalRegion() {
+		return region;
 	}
 
-	public void put(Object key, Object value) throws CacheException {
-		underlyingCache.put( key, value );
+	@Override
+	protected boolean isDefaultMinimalPutOverride() {
+		return region.getSettings().isMinimalPutsEnabled();
 	}
 
-	public void evict(Object key) throws CacheException {
-		underlyingCache.remove( key );
+	@Override
+	public CollectionRegion getRegion() {
+		return region;
 	}
 
-	public void evictAll() throws CacheException {
-		underlyingCache.clear();
+	BaseCollectionRegionAccessStrategy(CollectionRegionImpl region) {
+		this.region = region;
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java
new file mode 100644
index 0000000000..80d47c7f9d
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java
@@ -0,0 +1,85 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.EntityRegion;
+import org.hibernate.cache.spi.GeneralDataRegion;
+import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+class BaseEntityRegionAccessStrategy extends BaseRegionAccessStrategy implements EntityRegionAccessStrategy {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, BaseEntityRegionAccessStrategy.class.getName()
+	);
+	private final EntityRegionImpl region;
+
+	BaseEntityRegionAccessStrategy(EntityRegionImpl region) {
+		this.region = region;
+	}
+
+
+	@Override
+	public EntityRegion getRegion() {
+		return region;
+	}
+
+	@Override
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
+		return putFromLoad( key, value, 0, version );
+	}
+
+	@Override
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
+		return true;
+	}
+
+	@Override
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
+			throws CacheException {
+		return false;
+	}
+
+	@Override
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+			throws CacheException {
+		return false;
+	}
+
+	@Override
+	protected BaseGeneralDataRegion getInternalRegion() {
+		return region;
+	}
+
+	@Override
+	protected boolean isDefaultMinimalPutOverride() {
+		return region.getSettings().isMinimalPutsEnabled();
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseGeneralDataRegion.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseGeneralDataRegion.java
new file mode 100644
index 0000000000..112b29e4bf
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseGeneralDataRegion.java
@@ -0,0 +1,91 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import java.util.concurrent.locks.Lock;
+import java.util.concurrent.locks.ReentrantReadWriteLock;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.GeneralDataRegion;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+class BaseGeneralDataRegion extends BaseRegion implements GeneralDataRegion {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, BaseGeneralDataRegion.class.getName()
+	);
+
+	BaseGeneralDataRegion(String name) {
+		super( name );
+	}
+
+	@Override
+	public Object get(Object key) throws CacheException {
+		LOG.debugf( "Cache lookup : key[%s]", key );
+		if ( key == null ) {
+			return null;
+		}
+		Object result = cache.get( key );
+		if ( result != null ) {
+			LOG.debugf( "Cache hit: %s", key );
+		}
+		return result;
+	}
+
+	@Override
+	public void put(Object key, Object value) throws CacheException {
+		LOG.debugf( "Caching : [%s] -> [%s]", key, value );
+		if ( key == null || value == null ) {
+			LOG.debug( "Key or Value is null" );
+			return;
+		}
+		cache.put( key, value );
+	}
+
+	@Override
+	public void evict(Object key) throws CacheException {
+		LOG.debugf( "Invalidating: %s", key );
+		if ( key == null ) {
+			LOG.debug( "Key is null" );
+			return;
+		}
+		cache.remove( key );
+	}
+
+	@Override
+	public void evictAll() throws CacheException {
+		LOG.debug( "evict cache" );
+		cache.clear();
+	}
+
+
+
+
+
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseRegionAdapter.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegion.java
similarity index 57%
rename from hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseRegionAdapter.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegion.java
index 36d5408666..d55932b5f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseRegionAdapter.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegion.java
@@ -1,87 +1,92 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
+package org.hibernate.testing.cache;
 
+import java.util.Collections;
 import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.Cache;
+import org.hibernate.cache.internal.Timestamper;
 import org.hibernate.cache.spi.Region;
-import org.hibernate.cfg.Settings;
 
 /**
- * Basic adapter bridging between {@link Region} and {@link Cache}.
- *
- * @author Steve Ebersole
+ * @author Strong Liu
  */
-public abstract class BaseRegionAdapter implements Region {
-	protected final Cache underlyingCache;
-	protected final Settings settings;
+class BaseRegion implements Region {
+	protected final Map cache = new ConcurrentHashMap();
+	private final String name;
 
-	protected BaseRegionAdapter(Cache underlyingCache, Settings settings) {
-		this.underlyingCache = underlyingCache;
-		this.settings = settings;
+	BaseRegion(String name) {
+		this.name = name;
 	}
 
-	public String getName() {
-		return underlyingCache.getRegionName();
+	@Override
+	public boolean contains(Object key) {
+		return key != null ? cache.containsKey( key ) : false;
 	}
 
-	public void clear() throws CacheException {
-		underlyingCache.clear();
+	@Override
+	public String getName() {
+		return name;
 	}
 
+	@Override
 	public void destroy() throws CacheException {
-		underlyingCache.destroy();
-	}
-
-	public boolean contains(Object key) {
-		// safer to utilize the toMap() as oposed to say get(key) != null
-		return underlyingCache.toMap().containsKey( key );
+		cache.clear();
 	}
 
+	@Override
 	public long getSizeInMemory() {
-		return underlyingCache.getSizeInMemory();
+		return -1;
 	}
 
+	@Override
 	public long getElementCountInMemory() {
-		return underlyingCache.getElementCountInMemory();
+		return cache.size();
 	}
 
+	@Override
 	public long getElementCountOnDisk() {
-		return underlyingCache.getElementCountOnDisk();
+		return 0;
 	}
 
+	@Override
 	public Map toMap() {
-		return underlyingCache.toMap();
+		return Collections.unmodifiableMap( cache );
 	}
 
+	@Override
 	public long nextTimestamp() {
-		return underlyingCache.nextTimestamp();
+		return Timestamper.next();
 	}
 
+	@Override
 	public int getTimeout() {
-		return underlyingCache.getTimeout();
+		return Timestamper.ONE_MS * 60000;
 	}
+
 }
+
+
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
new file mode 100644
index 0000000000..5df9e342f2
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
@@ -0,0 +1,109 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.GeneralDataRegion;
+import org.hibernate.cache.spi.access.RegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+abstract class BaseRegionAccessStrategy implements RegionAccessStrategy {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, BaseRegionAccessStrategy.class.getName()
+	);
+
+	protected abstract BaseGeneralDataRegion getInternalRegion();
+	protected abstract boolean isDefaultMinimalPutOverride();
+	@Override
+	public Object get(Object key, long txTimestamp) throws CacheException {
+		return getInternalRegion().get( key );
+	}
+
+	@Override
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
+		return putFromLoad( key, value, txTimestamp, version, isDefaultMinimalPutOverride() );
+	}
+
+	@Override
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+			throws CacheException {
+
+		if ( key == null || value == null ) {
+			return false;
+		}
+		if ( minimalPutOverride && getInternalRegion().contains( key ) ) {
+			LOG.debugf( "Item already cached: %s", key );
+			return false;
+		}
+		LOG.debugf( "Caching: %s", key );
+		getInternalRegion().put( key, value );
+		return true;
+
+	}
+
+	@Override
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
+		return null;
+	}
+
+	@Override
+	public SoftLock lockRegion() throws CacheException {
+		return null;
+	}
+
+	@Override
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
+	}
+
+	@Override
+	public void unlockRegion(SoftLock lock) throws CacheException {
+		evictAll();
+	}
+
+	@Override
+	public void remove(Object key) throws CacheException {
+		evict( key );
+	}
+
+	@Override
+	public void removeAll() throws CacheException {
+		evictAll();
+	}
+
+	@Override
+	public void evict(Object key) throws CacheException {
+		getInternalRegion().evict( key );
+	}
+
+	@Override
+	public void evictAll() throws CacheException {
+		getInternalRegion().evictAll();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseTransactionalDataRegionAdapter.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseTransactionalDataRegion.java
similarity index 65%
rename from hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseTransactionalDataRegionAdapter.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseTransactionalDataRegion.java
index aedcd4f77a..ae0a7be761 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/BaseTransactionalDataRegionAdapter.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseTransactionalDataRegion.java
@@ -1,55 +1,50 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
+package org.hibernate.testing.cache;
 
-import org.hibernate.cache.spi.Cache;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.TransactionAwareCache;
 import org.hibernate.cache.spi.TransactionalDataRegion;
-import org.hibernate.cfg.Settings;
 
 /**
- * {@inheritDoc}
- *
- * @author Steve Ebersole
+ * @author Strong Liu
  */
-public abstract class BaseTransactionalDataRegionAdapter
-		extends BaseRegionAdapter
-		implements TransactionalDataRegion {
-
-	protected final CacheDataDescription metadata;
+class BaseTransactionalDataRegion extends BaseGeneralDataRegion implements TransactionalDataRegion {
+	private final CacheDataDescription metadata;
 
-	protected BaseTransactionalDataRegionAdapter(Cache underlyingCache, Settings settings, CacheDataDescription metadata) {
-		super( underlyingCache, settings );
+	BaseTransactionalDataRegion(String name, CacheDataDescription metadata) {
+		super( name );
 		this.metadata = metadata;
 	}
 
-	public boolean isTransactionAware() {
-		return underlyingCache instanceof TransactionAwareCache;
-	}
-
+	@Override
 	public CacheDataDescription getCacheDataDescription() {
 		return metadata;
 	}
+
+	@Override
+	public boolean isTransactionAware() {
+		return false;
+	}
+
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/CachingRegionFactory.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/CachingRegionFactory.java
new file mode 100644
index 0000000000..b942455456
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/CachingRegionFactory.java
@@ -0,0 +1,116 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import java.util.Properties;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.internal.Timestamper;
+import org.hibernate.cache.spi.CacheDataDescription;
+import org.hibernate.cache.spi.CollectionRegion;
+import org.hibernate.cache.spi.EntityRegion;
+import org.hibernate.cache.spi.QueryResultsRegion;
+import org.hibernate.cache.spi.RegionFactory;
+import org.hibernate.cache.spi.TimestampsRegion;
+import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.cfg.Settings;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+public class CachingRegionFactory implements RegionFactory {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, CachingRegionFactory.class.getName()
+	);
+	private Settings settings;
+	public CachingRegionFactory() {
+		LOG.warn( "CachingRegionFactory should be only used for testing." );
+	}
+
+	public CachingRegionFactory(Properties properties) {
+		//add here to avoid run into catch
+		LOG.warn( "CachingRegionFactory should be only used for testing." );
+	}
+
+	@Override
+	public void start(Settings settings, Properties properties) throws CacheException {
+		this.settings=settings;
+	}
+
+	@Override
+	public void stop() {
+	}
+
+	@Override
+	public boolean isMinimalPutsEnabledByDefault() {
+		return false;
+	}
+
+	@Override
+	public AccessType getDefaultAccessType() {
+		return AccessType.NONSTRICT_READ_WRITE;
+	}
+
+	@Override
+	public long nextTimestamp() {
+		return Timestamper.next();
+	}
+
+	@Override
+	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
+			throws CacheException {
+		return new EntityRegionImpl( regionName, metadata, settings );
+	}
+
+	@Override
+	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
+			throws CacheException {
+		return new CollectionRegionImpl( regionName, metadata, settings );
+	}
+
+	@Override
+	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
+		return new QueryResultsRegionImpl( regionName );
+	}
+
+	@Override
+	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
+		return new TimestampsRegionImpl( regionName );
+	}
+
+	private static class QueryResultsRegionImpl extends BaseGeneralDataRegion implements QueryResultsRegion {
+		QueryResultsRegionImpl(String name) {
+			super( name );
+		}
+	}
+
+	private static class TimestampsRegionImpl extends BaseGeneralDataRegion implements TimestampsRegion {
+		TimestampsRegionImpl(String name) {
+			super( name );
+		}
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/CollectionRegionImpl.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/CollectionRegionImpl.java
new file mode 100644
index 0000000000..5158cde72a
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/CollectionRegionImpl.java
@@ -0,0 +1,74 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.CacheDataDescription;
+import org.hibernate.cache.spi.CollectionRegion;
+import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
+import org.hibernate.cfg.Settings;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+class CollectionRegionImpl extends BaseTransactionalDataRegion implements CollectionRegion {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, CollectionRegionImpl.class.getName()
+	);
+	private final Settings settings;
+	CollectionRegionImpl(String name, CacheDataDescription metadata, Settings settings) {
+		super( name, metadata );
+		this.settings=settings;
+	}
+
+	public Settings getSettings() {
+		return settings;
+	}
+
+	@Override
+	public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
+		switch ( accessType ) {
+			case READ_ONLY:
+				if ( getCacheDataDescription().isMutable() ) {
+					LOG.warnf( "read-only cache configured for mutable collection [ %s ]", getName() );
+				}
+				return new ReadOnlyCollectionRegionAccessStrategy( this );
+			case READ_WRITE:
+				 return new ReadWriteCollectionRegionAccessStrategy( this );
+			case NONSTRICT_READ_WRITE:
+				return new NonstrictReadWriteCollectionRegionAccessStrategy( this );
+			case TRANSACTIONAL:
+
+				throw new UnsupportedOperationException( "doesn't support this access strategy" );
+			default:
+				throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
+		}
+	}
+
+
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/EntityRegionImpl.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/EntityRegionImpl.java
new file mode 100644
index 0000000000..2c5d6e66d4
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/EntityRegionImpl.java
@@ -0,0 +1,78 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.CacheDataDescription;
+import org.hibernate.cache.spi.EntityRegion;
+import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.cfg.Settings;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+class EntityRegionImpl extends BaseTransactionalDataRegion implements EntityRegion {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, EntityRegionImpl.class.getName()
+	);
+	private final Settings settings;
+
+
+	EntityRegionImpl(String name, CacheDataDescription metadata, Settings settings) {
+		super( name, metadata );
+		this.settings = settings;
+
+	}
+
+	public Settings getSettings() {
+		return settings;
+	}
+
+	@Override
+	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
+		switch ( accessType ) {
+			case READ_ONLY:
+				if ( getCacheDataDescription().isMutable() ) {
+					LOG.warnf( "read-only cache configured for mutable entity [ %s ]", getName() );
+				}
+				return new ReadOnlyEntityRegionAccessStrategy( this );
+			case READ_WRITE:
+				return new ReadWriteEntityRegionAccessStrategy( this );
+			case NONSTRICT_READ_WRITE:
+				return new NonstrictReadWriteEntityRegionAccessStrategy( this );
+			case TRANSACTIONAL:
+//				throw new UnsupportedOperationException( "doesn't support this access strategy" );
+				return new NonstrictReadWriteEntityRegionAccessStrategy( this );
+
+			default:
+				throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
+		}
+
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/OptimisticCacheSourceAdapter.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java
similarity index 56%
rename from hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/OptimisticCacheSourceAdapter.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java
index 2fe6592e21..b92d45d655 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/OptimisticCacheSourceAdapter.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java
@@ -1,50 +1,48 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
+package org.hibernate.testing.cache;
 
-import java.util.Comparator;
+import org.jboss.logging.Logger;
 
-import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.OptimisticCacheSource;
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * {@inheritDoc}
-*
-* @author Steve Ebersole
-*/
-public class OptimisticCacheSourceAdapter implements OptimisticCacheSource {
-	private final CacheDataDescription dataDescription;
+ * @author Strong Liu
+ */
+class NonstrictReadWriteCollectionRegionAccessStrategy extends BaseCollectionRegionAccessStrategy {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, NonstrictReadWriteCollectionRegionAccessStrategy.class.getName()
+	);
 
-	public OptimisticCacheSourceAdapter(CacheDataDescription dataDescription) {
-		this.dataDescription = dataDescription;
+	NonstrictReadWriteCollectionRegionAccessStrategy(CollectionRegionImpl region) {
+		super( region );
 	}
-
-	public boolean isVersioned() {
-		return dataDescription.isVersioned();
+	@Override
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
+		evict( key );
 	}
 
-	public Comparator getVersionComparator() {
-		return dataDescription.getVersionComparator();
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/HashtableCacheProvider.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java
similarity index 50%
rename from hibernate-core/src/main/java/org/hibernate/cache/internal/HashtableCacheProvider.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java
index ea4830d43e..49127e60ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/HashtableCacheProvider.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java
@@ -1,68 +1,61 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal;
+package org.hibernate.testing.cache;
 
-import java.util.Properties;
+import org.jboss.logging.Logger;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.CacheProvider;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * A simple in-memory Hashtable-based cache impl.
- * 
- * @author Gavin King
+ * @author Strong Liu
  */
-public class HashtableCacheProvider implements CacheProvider {
-
-	public Cache buildCache(String regionName, Properties properties) throws CacheException {
-		return new HashtableCache( regionName );
+class NonstrictReadWriteEntityRegionAccessStrategy extends BaseEntityRegionAccessStrategy {
+	NonstrictReadWriteEntityRegionAccessStrategy(EntityRegionImpl region) {
+		super( region );
 	}
 
-	public long nextTimestamp() {
-		return Timestamper.next();
+	@Override
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
+		evict( key );
 	}
 
-	/**
-	 * Callback to perform any necessary initialization of the underlying cache implementation
-	 * during SessionFactory construction.
-	 *
-	 * @param properties current configuration settings.
-	 */
-	public void start(Properties properties) throws CacheException {
+	@Override
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
+		return false;
 	}
 
-	/**
-	 * Callback to perform any necessary cleanup of the underlying cache implementation
-	 * during SessionFactory.close().
-	 */
-	public void stop() {
+	@Override
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
+		return false;
 	}
 
-	public boolean isMinimalPutsEnabledByDefault() {
+	@Override
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
+			throws CacheException {
+		evict( key );
 		return false;
 	}
-
 }
-
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyCollectionRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyCollectionRegionAccessStrategy.java
new file mode 100644
index 0000000000..0af0438aa1
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyCollectionRegionAccessStrategy.java
@@ -0,0 +1,59 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+class ReadOnlyCollectionRegionAccessStrategy extends BaseCollectionRegionAccessStrategy {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, ReadOnlyCollectionRegionAccessStrategy.class.getName()
+	);
+
+	ReadOnlyCollectionRegionAccessStrategy(CollectionRegionImpl region) {
+		super( region );
+	}
+
+	@Override
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
+		LOG.invalidEditOfReadOnlyItem( key );
+	}
+
+	@Override
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
+		LOG.invalidEditOfReadOnlyItem( key );
+		throw new UnsupportedOperationException( "Can't write to a readonly object" );
+	}
+
+	@Override
+	public void remove(Object key) throws CacheException {
+	}
+
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java
new file mode 100644
index 0000000000..33e3177016
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java
@@ -0,0 +1,86 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.internal.CoreMessageLogger;
+
+/**
+ * @author Strong Liu
+ */
+class ReadOnlyEntityRegionAccessStrategy extends BaseEntityRegionAccessStrategy {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class, ReadOnlyEntityRegionAccessStrategy.class.getName()
+	);
+
+	ReadOnlyEntityRegionAccessStrategy(EntityRegionImpl region) {
+		super( region );
+	}
+
+	@Override
+	public void remove(Object key) throws CacheException {
+	}
+
+	@Override
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
+		return false; //wait until tx complete, see afterInsert().
+	}
+
+	@Override
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
+		getInternalRegion().put( key, value ); //save into cache since the tx is completed
+		return true;
+	}
+
+
+	@Override
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
+		LOG.invalidEditOfReadOnlyItem( key );
+	}
+
+	@Override
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
+		LOG.invalidEditOfReadOnlyItem( key );
+		throw new UnsupportedOperationException( "Can't write to a readonly object" );
+	}
+
+	@Override
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
+			throws CacheException {
+		LOG.invalidEditOfReadOnlyItem( key );
+		throw new UnsupportedOperationException( "Can't write to a readonly object" );
+	}
+
+	@Override
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+			throws CacheException {
+		LOG.invalidEditOfReadOnlyItem( key );
+		throw new UnsupportedOperationException( "Can't write to a readonly object" );
+	}
+
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/QueryResultsRegionAdapter.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java
similarity index 51%
rename from hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/QueryResultsRegionAdapter.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java
index 16bf631dee..2876074584 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/bridge/QueryResultsRegionAdapter.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java
@@ -1,39 +1,64 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.cache.internal.bridge;
+package org.hibernate.testing.cache;
 
-import org.hibernate.cache.spi.Cache;
-import org.hibernate.cache.spi.QueryResultsRegion;
-import org.hibernate.cfg.Settings;
+import java.util.Comparator;
+
+import org.hibernate.cache.spi.CollectionRegion;
+import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 
 /**
- * Adapter specifically briding {@link org.hibernate.cache.spi.QueryResultsRegion} to {@link Cache}.
-*
-* @author Steve Ebersole
+ * @author Strong Liu
  */
-public class QueryResultsRegionAdapter extends BaseGeneralDataRegionAdapter implements QueryResultsRegion {
-	protected QueryResultsRegionAdapter(Cache underlyingCache, Settings settings) {
-		super( underlyingCache, settings );
+class ReadWriteCollectionRegionAccessStrategy extends AbstractReadWriteAccessStrategy
+		implements CollectionRegionAccessStrategy {
+
+	private final CollectionRegionImpl region;
+
+	ReadWriteCollectionRegionAccessStrategy(CollectionRegionImpl region) {
+		this.region = region;
+	}
+
+	@Override
+	Comparator getVersionComparator() {
+		return region.getCacheDataDescription().getVersionComparator();
+	}
+
+	@Override
+	protected BaseGeneralDataRegion getInternalRegion() {
+		return region;
 	}
+
+	@Override
+	protected boolean isDefaultMinimalPutOverride() {
+		return region.getSettings().isMinimalPutsEnabled();
+	}
+
+	@Override
+	public CollectionRegion getRegion() {
+		return region;
+	}
+
+
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
new file mode 100644
index 0000000000..99bb1c0bc9
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
@@ -0,0 +1,127 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.testing.cache;
+
+import java.util.Comparator;
+
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.spi.EntityRegion;
+import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
+
+/**
+ * @author Strong Liu
+ */
+class ReadWriteEntityRegionAccessStrategy extends AbstractReadWriteAccessStrategy
+		implements EntityRegionAccessStrategy {
+	private final EntityRegionImpl region;
+
+	ReadWriteEntityRegionAccessStrategy(EntityRegionImpl region) {
+		this.region = region;
+	}
+
+	@Override
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
+		return false;
+	}
+
+	@Override
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
+			throws CacheException {
+		return false;
+	}
+
+	@Override
+	public void evict(Object key) throws CacheException {
+	}
+
+	@Override
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
+
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) region.get( key );
+			if ( item == null ) {
+				region.put( key, new Item( value, version, region.nextTimestamp() ) );
+				return true;
+			}
+			else {
+				return false;
+			}
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+
+	@Override
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+			throws CacheException {
+		try {
+			writeLock.lock();
+			Lockable item = (Lockable) region.get( key );
+
+			if ( item != null && item.isUnlockable( lock ) ) {
+				Lock lockItem = (Lock) item;
+				if ( lockItem.wasLockedConcurrently() ) {
+					decrementLock( key, lockItem );
+					return false;
+				}
+				else {
+					region.put( key, new Item( value, currentVersion, region.nextTimestamp() ) );
+					return true;
+				}
+			}
+			else {
+				handleLockExpiry( key, item );
+				return false;
+			}
+		}
+		finally {
+			writeLock.unlock();
+		}
+	}
+
+
+	@Override
+	protected BaseGeneralDataRegion getInternalRegion() {
+		return region;
+	}
+
+	@Override
+	protected boolean isDefaultMinimalPutOverride() {
+		return region.getSettings().isMinimalPutsEnabled();
+	}
+
+	@Override
+	Comparator getVersionComparator() {
+		return region.getCacheDataDescription().getVersionComparator();
+	}
+
+	@Override
+	public EntityRegion getRegion() {
+		return region;
+	}
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index fa2073e3ed..0e0a67bb0a 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,413 +1,413 @@
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
 package org.hibernate.testing.junit4;
 
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
-import org.hibernate.cache.internal.HashtableCacheProvider;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
+import org.hibernate.testing.cache.CachingRegionFactory;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private Configuration configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected Session openSession() throws HibernateException {
 		session = sessionFactory().openSession();
 		return session;
 	}
 
 	protected Session openSession(Interceptor interceptor) throws HibernateException {
 		session = sessionFactory().withOptions().interceptor( interceptor ).openSession();
 		return session;
 	}
 
 
 	// before/after test class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@BeforeClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void buildSessionFactory() {
 		configuration = buildConfiguration();
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		addMappings( cfg );
 		cfg.buildMappings();
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 		return cfg;
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration()
-				.setProperty( Environment.CACHE_PROVIDER, HashtableCacheProvider.class.getName() );
+				.setProperty(Environment.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName()  );
 		configuration.setProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return configuration;
 	}
 
 	protected void configure(Configuration configuration) {
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource(
 						getBaseForMappings() + mapping,
 						getClass().getClassLoader()
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				configuration.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				configuration.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				configuration.addInputStream( is );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		// todo : rename to getOrmXmlFiles()
 		return NO_MAPPINGS;
 	}
 
 	protected void applyCacheSettings(Configuration configuration) {
 		if ( getCacheConcurrencyStrategy() != null ) {
 			Iterator itr = configuration.getClassMappings();
 			while ( itr.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) itr.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
 				}
 			}
 			itr = configuration.getCollectionMappings();
 			while ( itr.hasNext() ) {
 				Collection coll = (Collection) itr.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 		afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 	}
 
 	protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 	}
 
 	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder( properties ).buildServiceRegistry();
 		applyServices( serviceRegistry );
 		return serviceRegistry;
 	}
 
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
 	protected void rebuildSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		serviceRegistry.destroy();
 
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 
 	// before/after each test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 	}
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			if ( session.isConnected() ) {
 				session.doWork( new RollbackWork() );
 			}
 			session.close();
 		}
 		session = null;
 	}
 
 	public class RollbackWork implements Work {
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	protected void cleanupTest() throws Exception {
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing"})
 	protected void assertAllDataRemoved() {
 		if ( !createSchema() ) {
 			return; // no tables were created...
 		}
 		if ( !Boolean.getBoolean( VALIDATE_DATA_CLEANUP ) ) {
 			return;
 		}
 
 		Session tmpSession = sessionFactory.openSession();
 		try {
 			List list = tmpSession.createQuery( "select o from java.lang.Object o" ).list();
 
 			Map<String,Integer> items = new HashMap<String,Integer>();
 			if ( !list.isEmpty() ) {
 				for ( Object element : list ) {
 					Integer l = items.get( tmpSession.getEntityName( element ) );
 					if ( l == null ) {
 						l = Integer.valueOf( 0 );
 					}
 					l = Integer.valueOf( l.intValue() + 1 ) ;
 					items.put( tmpSession.getEntityName( element ), l );
 					System.out.println( "Data left: " + element );
 				}
 				fail( "Data is left in the database: " + items.toString() );
 			}
 		}
 		finally {
 			try {
 				tmpSession.close();
 			}
 			catch( Throwable t ) {
 				// intentionally empty
 			}
 		}
 	}
 
 	protected boolean readCommittedIsolationMaintained(String scenario) {
 		int isolation = java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
 		Session testSession = null;
 		try {
 			testSession = openSession();
 			isolation = testSession.doReturningWork(
 					new AbstractReturningWork<Integer>() {
 						@Override
 						public Integer execute(Connection connection) throws SQLException {
 							return connection.getTransactionIsolation();
 						}
 					}
 			);
 		}
 		catch( Throwable ignore ) {
 		}
 		finally {
 			if ( testSession != null ) {
 				try {
 					testSession.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		if ( isolation < java.sql.Connection.TRANSACTION_READ_COMMITTED ) {
 			SkipLog.reportSkip( "environment does not support at least read committed isolation", scenario );
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 
 }
