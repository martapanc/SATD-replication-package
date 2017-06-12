1;FIXME Per the RegionFactory class Javadoc;cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java;c49ef2e2f;d2c88d55d
2;// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value;src/main/java/org/hibernate/cfg/SettingsFactory.java;d8d6d82e3;91d444423
3;// Do we need to drop constraints before dropping tables in this dialect?;src/main/java/org/hibernate/dialect/Cache71Dialect.java;d8d6d82e3;87e3f0fd2
4;// Does this dialect support check constraints?;src/main/java/org/hibernate/dialect/Cache71Dialect.java;d8d6d82e3;5fc70fc5a
5;// Does this dialect support FOR UPDATE OF;src/main/java/org/hibernate/dialect/Cache71Dialect.java;d8d6d82e3;5fc70fc5a
6;// Does this dialect support the FOR UPDATE syntax?;src/main/java/org/hibernate/dialect/Cache71Dialect.java;d8d6d82e3;5fc70fc5a
7;// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?;src/main/java/org/hibernate/dialect/Cache71Dialect.java;d8d6d82e3;5fc70fc5a
8;// Does this dialect support the UNIQUE column syntax?;src/main/java/org/hibernate/dialect/Cache71Dialect.java;d8d6d82e3;49c8a8e4f
9;// FIXME hack to work around fact that calling;cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java;9ccd912bd;9e7e49d1f
10;// FIXME Hacky workaround to JBCACHE-1202;cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java;161e5cc19;834be2837
11;// get the right object from the list ... would it be easier to just call getEntity() ??;src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java;d8d6d82e3;06b0faaf5
12;// hack/workaround as sqlquery impl depend on having a key.;src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java;d8d6d82e3;9caca0ce3
13;// implicit joins are always(?) ok to reuse;src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java;a0663f0d6;153c4e32e
14;// is this really necessary?;src/main/java/org/hibernate/type/AbstractBynaryType.java;d8d6d82e3;7308e14fe
15;// NONE might be a better option moving forward in the case of callable;src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
16;// orphans should not be deleted during copy??;src/main/java/org/hibernate/engine/CascadingAction.java;d8d6d82e3;3402ba3a6
17;// perhaps this should be an exception since it is only ever used;src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java;d8d6d82e3;fe8c7183d
18;// polymorphism not really handled completely correctly;src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java;dc7cdf9d8;8e2f2a9da
19;// short cut check...;commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java;82e5fa8c7;59ec451c2
20;// short-circuit for performance...;src/main/java/org/hibernate/impl/AbstractQueryImpl.java;0013a90d2;d1515a291
21;// this class has no proxies (so do a shortcut);src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java;eecee618c;f74c5a7fa
22;// This inner class implements a case statement....perhaps im being a bit over-clever here;src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
23;// this is done here 'cos we might only know the type here (ugly!);src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
24;// this is done here 'cos we might only know the type here (ugly!);src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java;819f8da9e;594f689d9
25;// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification;src/main/java/org/hibernate/loader/hql/QueryLoader.java;d8d6d82e3;6cabc326b
26;// todo : currently expects that the individual with expressions apply to the same sql table join;src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java;d8d6d82e3;a9b1425f3
27;// todo : potentially look at optimizing these two arrays;src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java;5c4dacb83;3e5a8b660
28;// todo : remove;src/main/java/org/hibernate/engine/TransactionHelper.java;d8d6d82e3;21cc90fbf
29;// todo : remove;src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java;dc7cdf9d8;8e2f2a9da
30;// todo : remove;src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java;baeb6dc40;66ce8b7fb
31;// TODO : remove these last two as batcher is no longer managing connections;src/main/java/org/hibernate/jdbc/Batcher.java;d8d6d82e3;97fef96b9
32;// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...;src/main/java/org/hibernate/engine/TransactionHelper.java;d8d6d82e3;08d9fe211
33;// TODO : should remove this exposure;src/main/java/org/hibernate/impl/SessionImpl.java;d8d6d82e3;b006a6c3c
34;// todo : we can remove this once the deprecated ctor can be made private...;core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java;1851bffce;a6ca833e2
35;// todo : what else to do here?;src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java;d8d6d82e3;82d2ef4b1
36;// todo : what is the implication of this?;src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
37;// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;src/main/java/org/hibernate/internal/NamedQueryRepository.java;7d99ca57f;87e3f0fd2
38;// todo : YUCK!!!;src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java;d8d6d82e3;66ce8b7fb
39;// TODO : YUCK!!! fix after HHH-1907 is complete;src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java;8a5415d36;87e3f0fd2
40;// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?;src/main/java/org/hibernate/persister/spi/PersisterFactory.java;ddfcc44d7;996d56773
41;// TODO: it would be better if this was done at the higher level by Printer;src/main/java/org/hibernate/type/CollectionType.java;d8d6d82e3;4a4f636ca
42;// TODO: It would be really;src/main/java/org/hibernate/persister/entity/PropertyMapping.java;d8d6d82e3;af1061a42
43;// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey();src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
44;// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.;src/main/java/org/hibernate/exception/Configurable.java;d8d6d82e3;a806626a2
45;// VERY IMPORTANT!!!! - This class needs to be free of any static references;src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java;d8d6d82e3;19791a6c7
46;// we have to set up the table later!! yuck;src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
47;// why does this matter?;src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java;b3791bc3c;8e2f2a9da
48;//Icky workaround for MySQL bug:;src/main/java/org/hibernate/cfg/Configuration.java;d8d6d82e3;377c30007
49;//is this ok?;src/main/java/org/hibernate/property/Dom4jAccessor.java;d8d6d82e3;4a4f636ca
50;//shortcut;src/main/java/org/hibernate/mapping/SimpleValue.java;d8d6d82e3;9caca0ce3
51;//TODO: better to degrade to lazy="false" if uninstrumented;src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
52;//TODO: code duplication with JoinedSubclassEntityPersister;src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java;5457b6c70;66ce8b7fb
53;//TODO: copy/paste from insertRows();src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java;d8d6d82e3;129c0f134
54;//TODO: copy/paste from recreate();src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java;d8d6d82e3;129c0f134
55;//TODO: deprecated;src/main/java/org/hibernate/mapping/Column.java;d8d6d82e3;d7cc102b0
56;//TODO: design new lifecycle for ProxyFactory;src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java;8a5415d36;66ce8b7fb
57;//TODO: get SQL rendering out of this package!;src/main/java/org/hibernate/criterion/BetweenExpression.java;d8d6d82e3;8c28ba846
58;//TODO: ideally we need the construction of PropertyAccessor to take the following:;src/main/java/org/hibernate/property/PropertyAccessorFactory.java;d8d6d82e3;9e063ffa2
59;//TODO: implement caching?! proxies?!;src/main/java/org/hibernate/type/EntityType.java;d8d6d82e3;1c3491445
60;//TODO: improve this hack!;src/main/java/org/hibernate/mapping/UniqueKey.java;d8d6d82e3;f4c36a10f
61;//TODO: inefficient;src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java;a102bf2c3;18079f346
62;//TODO: is there a more elegant way than downcasting?;src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java;d8d6d82e3;87e3f0fd2
63;//TODO: is this right??;src/main/java/org/hibernate/type/AnyType.java;d8d6d82e3;dc7cdf9d8
64;//TODO: need some caching scheme? really comes down to decision;src/main/java/org/hibernate/property/PropertyAccessorFactory.java;d8d6d82e3;4a4f636ca
65;//TODO: put this stuff back in to read snapshot from;src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java;d8d6d82e3;a9b1425f3
66;//TODO: really bad;src/main/java/org/hibernate/cfg/HbmBinder.java;d8d6d82e3;9caca0ce3
67;//TODO: redesign how PropertyAccessors are acquired...;src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java;8a5415d36;66ce8b7fb
68;//TODO: reuse the PostLoadEvent...;src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java;eecee618c;f74c5a7fa
69;//TODO: Somehow add the newly created foreign keys to the internal collection;src/main/java/org/hibernate/cfg/Configuration.java;d8d6d82e3;9caca0ce3
70;//TODO: this is a bit arbitrary;src/main/java/org/hibernate/type/EntityType.java;d8d6d82e3;9938937fe
71;//TODO: this is temporary in that the end result will probably not take a Property reference per-se.;src/main/java/org/hibernate/property/PropertyAccessorFactory.java;d8d6d82e3;66ce8b7fb
72;//TODO: this is temporary in that the end result will probably not take a Property reference per-se.;src/main/java/org/hibernate/property/PropertyAccessorFactory.java;1d26ac1e1;9e063ffa2
73;//TODO: to handle concurrent writes correctly;src/main/java/org/hibernate/cache/UpdateTimestampsCache.java;d8d6d82e3;4aa9cbe5b
74;//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective;core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java;8bd2ab12e;39b0774ae
75;//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective;src/main/java/org/hibernate/stat/StatisticsImpl.java;d8d6d82e3;f93d1412a
76;//use of trim() here is ugly?;src/main/java/org/hibernate/loader/Loader.java;d8d6d82e3;c46daa4cf
77;//work around a bug in all known connection pools....;src/main/java/org/hibernate/jdbc/AbstractBatcher.java;a9b1425f3;3712e1ad7
78;//work around a bug in all known connection pools....;src/main/java/org/hibernate/jdbc/AbstractBatcher.java;d8d6d82e3;3712e1ad7
79;//work around a bug in all known connection pools....;src/main/java/org/hibernate/jdbc/AbstractBatcher.java;d8d6d82e3;b006a6c3c
80;//yuck!;src/main/java/org/hibernate/criterion/Example.java;d8d6d82e3;8c28ba846
81;but needed for collections with a "." node mapping;src/main/java/org/hibernate/property/Dom4jAccessor.java;d8d6d82e3;4a4f636ca
82;since getTypeName() actually throws an exception!;src/main/java/org/hibernate/dialect/function/CastFunction.java;d8d6d82e3;c97075c3c