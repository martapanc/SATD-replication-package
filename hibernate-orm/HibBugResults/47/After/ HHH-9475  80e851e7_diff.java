diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
index a0a5c7a5aa..9cce99dfc2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
@@ -1,430 +1,428 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Random;
+import javax.persistence.AttributeOverride;
+import javax.persistence.AttributeOverrides;
+import javax.persistence.MapKeyClass;
+
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
-import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionPropertyHolder;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.sql.Template;
 
-import javax.persistence.AttributeOverride;
-import javax.persistence.AttributeOverrides;
-import javax.persistence.MapKeyClass;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.Random;
-
 /**
  * Implementation to bind a Map
  *
  * @author Emmanuel Bernard
  */
 public class MapBinder extends CollectionBinder {
 	public MapBinder(boolean sorted) {
 		super( sorted );
 	}
 
 	public boolean isMap() {
 		return true;
 	}
 
 	protected Collection createCollection(PersistentClass persistentClass) {
 		return new org.hibernate.mapping.Map( getBuildingContext().getMetadataCollector(), persistentClass );
 	}
 
 	@Override
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final MetadataBuildingContext buildingContext) {
 		return new CollectionSecondPass( buildingContext, MapBinder.this.collection ) {
 			public void secondPass(Map persistentClasses, Map inheritedMetas)
 					throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, buildingContext
 				);
 				bindKeyFromAssociationTable(
 						collType, persistentClasses, mapKeyPropertyName, property, isEmbedded, buildingContext,
 						mapKeyColumns, mapKeyManyToManyColumns,
 						inverseColumns != null ? inverseColumns[0].getPropertyName() : null
 				);
 			}
 		};
 	}
 
 	private void bindKeyFromAssociationTable(
 			XClass collType,
 			Map persistentClasses,
 			String mapKeyPropertyName,
 			XProperty property,
 			boolean isEmbedded,
 			MetadataBuildingContext buildingContext,
 			Ejb3Column[] mapKeyColumns,
 			Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			String targetPropertyName) {
 		if ( mapKeyPropertyName != null ) {
 			//this is an EJB3 @MapKey
 			PersistentClass associatedClass = (PersistentClass) persistentClasses.get( collType.getName() );
 			if ( associatedClass == null ) throw new AnnotationException( "Associated class not found: " + collType );
 			Property mapProperty = BinderHelper.findPropertyByName( associatedClass, mapKeyPropertyName );
 			if ( mapProperty == null ) {
 				throw new AnnotationException(
 						"Map key property not found: " + collType + "." + mapKeyPropertyName
 				);
 			}
 			org.hibernate.mapping.Map map = (org.hibernate.mapping.Map) this.collection;
 			Value indexValue = createFormulatedValue(
 					mapProperty.getValue(), map, targetPropertyName, associatedClass, buildingContext
 			);
 			map.setIndex( indexValue );
 		}
 		else {
 			//this is a true Map mapping
 			//TODO ugly copy/pastle from CollectionBinder.bindManyToManySecondPass
 			String mapKeyType;
 			Class target = void.class;
 			/*
 			 * target has priority over reflection for the map key type
 			 * JPA 2 has priority
 			 */
 			if ( property.isAnnotationPresent( MapKeyClass.class ) ) {
 				target = property.getAnnotation( MapKeyClass.class ).value();
 			}
 			if ( !void.class.equals( target ) ) {
 				mapKeyType = target.getName();
 			}
 			else {
 				mapKeyType = property.getMapKey().getName();
 			}
 			PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( mapKeyType );
 			boolean isIndexOfEntities = collectionEntity != null;
 			ManyToOne element = null;
 			org.hibernate.mapping.Map mapValue = (org.hibernate.mapping.Map) this.collection;
 			if ( isIndexOfEntities ) {
 				element = new ManyToOne( buildingContext.getMetadataCollector(), mapValue.getCollectionTable() );
 				mapValue.setIndex( element );
 				element.setReferencedEntityName( mapKeyType );
 				//element.setFetchMode( fetchMode );
 				//element.setLazy( fetchMode != FetchMode.JOIN );
 				//make the second join non lazy
 				element.setFetchMode( FetchMode.JOIN );
 				element.setLazy( false );
 				//does not make sense for a map key element.setIgnoreNotFound( ignoreNotFound );
 			}
 			else {
 				XClass keyXClass;
 				AnnotatedClassType classType;
 				if ( BinderHelper.PRIMITIVE_NAMES.contains( mapKeyType ) ) {
 					classType = AnnotatedClassType.NONE;
 					keyXClass = null;
 				}
 				else {
 					try {
 						keyXClass = buildingContext.getBuildingOptions().getReflectionManager().classForName( mapKeyType );
 					}
 					catch (ClassLoadingException e) {
 						throw new AnnotationException( "Unable to find class: " + mapKeyType, e );
 					}
 					classType = buildingContext.getMetadataCollector().getClassType( keyXClass );
 					//force in case of attribute override
 					boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 							|| property.isAnnotationPresent( AttributeOverrides.class );
 					if ( isEmbedded || attributeOverride ) {
 						classType = AnnotatedClassType.EMBEDDABLE;
 					}
 				}
 
 				CollectionPropertyHolder holder = PropertyHolderBuilder.buildPropertyHolder(
 						mapValue,
 						StringHelper.qualify( mapValue.getRole(), "mapkey" ),
 						keyXClass,
 						property,
 						propertyHolder,
 						buildingContext
 				);
 
 
 				// 'propertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				propertyHolder.startingProperty( property );
 				holder.prepare( property );
 
 				PersistentClass owner = mapValue.getOwner();
 				AccessType accessType;
 				// FIXME support @Access for collection of elements
 				// String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					accessType = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" )
 							? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					accessType = prop.getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 					EntityBinder entityBinder = new EntityBinder();
 
 					PropertyData inferredData;
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "index", keyXClass );
 					}
 					else {
 						//"key" is the JPA 2 prefix for map keys
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "key", keyXClass );
 					}
 
 					//TODO be smart with isNullable
 					Component component = AnnotationBinder.fillComponent(
 							holder,
 							inferredData,
 							accessType,
 							true,
 							entityBinder,
 							false,
 							false,
 							true,
 							buildingContext,
 							inheritanceStatePerClass
 					);
 					mapValue.setIndex( component );
 				}
 				else {
 					SimpleValueBinder elementBinder = new SimpleValueBinder();
 					elementBinder.setBuildingContext( buildingContext );
 					elementBinder.setReturnedClassName( mapKeyType );
 
 					Ejb3Column[] elementColumns = mapKeyColumns;
 					if ( elementColumns == null || elementColumns.length == 0 ) {
 						elementColumns = new Ejb3Column[1];
 						Ejb3Column column = new Ejb3Column();
 						column.setImplicit( false );
 						column.setNullable( true );
 						column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 						column.setLogicalColumnName( Collection.DEFAULT_KEY_COLUMN_NAME );
 						//TODO create an EMPTY_JOINS collection
 						column.setJoins( new HashMap<String, Join>() );
 						column.setBuildingContext( buildingContext );
 						column.bind();
 						elementColumns[0] = column;
 					}
 					//override the table
 					for (Ejb3Column column : elementColumns) {
 						column.setTable( mapValue.getCollectionTable() );
 					}
 					elementBinder.setColumns( elementColumns );
 					//do not call setType as it extract the type from @Type
 					//the algorithm generally does not apply for map key anyway
 					elementBinder.setKey(true);
 					elementBinder.setType(
 							property,
 							keyXClass,
 							this.collection.getOwnerEntityName(),
 							holder.mapKeyAttributeConverterDescriptor( property, keyXClass )
 					);
 					elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 					elementBinder.setAccessType( accessType );
 					mapValue.setIndex( elementBinder.make() );
 				}
 			}
 			//FIXME pass the Index Entity JoinColumns
 			if ( !collection.isOneToMany() ) {
 				//index column shoud not be null
 				for (Ejb3JoinColumn col : mapKeyManyToManyColumns) {
 					col.forceNotNull();
 				}
 			}
 			if ( isIndexOfEntities ) {
 				bindManytoManyInverseFk(
 						collectionEntity,
 						mapKeyManyToManyColumns,
 						element,
 						false, //a map key column has no unique constraint
 						buildingContext
 				);
 			}
 		}
 	}
 
 	protected Value createFormulatedValue(
 			Value value,
 			Collection collection,
 			String targetPropertyName,
 			PersistentClass associatedClass,
 			MetadataBuildingContext buildingContext) {
 		Value element = collection.getElement();
 		String fromAndWhere = null;
 		if ( !( element instanceof OneToMany ) ) {
 			String referencedPropertyName = null;
 			if ( element instanceof ToOne ) {
 				referencedPropertyName = ( (ToOne) element ).getReferencedPropertyName();
 			}
 			else if ( element instanceof DependantValue ) {
 				//TODO this never happen I think
 				if ( propertyName != null ) {
 					referencedPropertyName = collection.getReferencedPropertyName();
 				}
 				else {
 					throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 				}
 			}
 			Iterator referencedEntityColumns;
 			if ( referencedPropertyName == null ) {
 				referencedEntityColumns = associatedClass.getIdentifier().getColumnIterator();
 			}
 			else {
 				Property referencedProperty = associatedClass.getRecursiveProperty( referencedPropertyName );
 				referencedEntityColumns = referencedProperty.getColumnIterator();
 			}
 			String alias = "$alias$";
 			StringBuilder fromAndWhereSb = new StringBuilder( " from " )
 					.append( associatedClass.getTable().getName() )
 							//.append(" as ") //Oracle doesn't support it in subqueries
 					.append( " " )
 					.append( alias ).append( " where " );
 			Iterator collectionTableColumns = element.getColumnIterator();
 			while ( collectionTableColumns.hasNext() ) {
 				Column colColumn = (Column) collectionTableColumns.next();
 				Column refColumn = (Column) referencedEntityColumns.next();
 				fromAndWhereSb.append( alias ).append( '.' ).append( refColumn.getQuotedName() )
 						.append( '=' ).append( colColumn.getQuotedName() ).append( " and " );
 			}
 			fromAndWhere = fromAndWhereSb.substring( 0, fromAndWhereSb.length() - 5 );
 		}
 
 		if ( value instanceof Component ) {
 			Component component = (Component) value;
 			Iterator properties = component.getPropertyIterator();
 			Component indexComponent = new Component( getBuildingContext().getMetadataCollector(), collection );
 			indexComponent.setComponentClassName( component.getComponentClassName() );
 			while ( properties.hasNext() ) {
 				Property current = (Property) properties.next();
 				Property newProperty = new Property();
 				newProperty.setCascade( current.getCascade() );
 				newProperty.setValueGenerationStrategy( current.getValueGenerationStrategy() );
 				newProperty.setInsertable( false );
 				newProperty.setUpdateable( false );
 				newProperty.setMetaAttributes( current.getMetaAttributes() );
 				newProperty.setName( current.getName() );
 				newProperty.setNaturalIdentifier( false );
 				//newProperty.setOptimisticLocked( false );
 				newProperty.setOptional( false );
 				newProperty.setPersistentClass( current.getPersistentClass() );
 				newProperty.setPropertyAccessorName( current.getPropertyAccessorName() );
 				newProperty.setSelectable( current.isSelectable() );
 				newProperty.setValue(
 						createFormulatedValue(
 								current.getValue(), collection, targetPropertyName, associatedClass, buildingContext
 						)
 				);
 				indexComponent.addProperty( newProperty );
 			}
 			return indexComponent;
 		}
 		else if ( value instanceof SimpleValue ) {
 			SimpleValue sourceValue = (SimpleValue) value;
 			SimpleValue targetValue;
 			if ( value instanceof ManyToOne ) {
 				ManyToOne sourceManyToOne = (ManyToOne) sourceValue;
 				ManyToOne targetManyToOne = new ManyToOne( getBuildingContext().getMetadataCollector(), collection.getCollectionTable() );
 				targetManyToOne.setFetchMode( FetchMode.DEFAULT );
 				targetManyToOne.setLazy( true );
 				//targetValue.setIgnoreNotFound( ); does not make sense for a map key
 				targetManyToOne.setReferencedEntityName( sourceManyToOne.getReferencedEntityName() );
 				targetValue = targetManyToOne;
 			}
 			else {
 				targetValue = new SimpleValue( getBuildingContext().getMetadataCollector(), collection.getCollectionTable() );
-				targetValue.setTypeName( sourceValue.getTypeName() );
-				targetValue.setTypeParameters( sourceValue.getTypeParameters() );
+				targetValue.copyTypeFrom( sourceValue );
 			}
 			Iterator columns = sourceValue.getColumnIterator();
 			Random random = new Random();
 			while ( columns.hasNext() ) {
 				Object current = columns.next();
 				Formula formula = new Formula();
 				String formulaString;
 				if ( current instanceof Column ) {
 					formulaString = ( (Column) current ).getQuotedName();
 				}
 				else if ( current instanceof Formula ) {
 					formulaString = ( (Formula) current ).getFormula();
 				}
 				else {
 					throw new AssertionFailure( "Unknown element in column iterator: " + current.getClass() );
 				}
 				if ( fromAndWhere != null ) {
 					formulaString = Template.renderWhereStringTemplate( formulaString, "$alias$", new HSQLDialect() );
 					formulaString = "(select " + formulaString + fromAndWhere + ")";
 					formulaString = StringHelper.replace(
 							formulaString,
 							"$alias$",
 							"a" + random.nextInt( 16 )
 					);
 				}
 				formula.setFormula( formulaString );
 				targetValue.addFormula( formula );
 
 			}
 			return targetValue;
 		}
 		else {
 			throw new AssertionFailure( "Unknown type encounters for map key: " + value.getClass() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 09f12cee3e..71c4773ff7 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,686 +1,694 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.Types;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.internal.AttributeConverterDescriptorNonAutoApplicableImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.LobTypeMappings;
 import org.hibernate.type.descriptor.sql.NationalizedTypeMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final MetadataImplementor metadata;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties typeParameters;
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDescriptor attributeConverterDescriptor;
 	private Type type;
 
 	public SimpleValue(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	public SimpleValue(MetadataImplementor metadata, Table table) {
 		this( metadata );
 		this.table = table;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	@Override
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) {
 			columns.add(column);
 		}
 		column.setValue(this);
 		column.setTypeIndex( columns.size() - 1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add( formula );
 	}
 
 	@Override
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	@Override
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 
 	public List getConstraintColumns() {
 		return columns;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		if ( typeName != null && typeName.startsWith( AttributeConverterTypeAdapter.NAME_PREFIX ) ) {
 			final String converterClassName = typeName.substring( AttributeConverterTypeAdapter.NAME_PREFIX.length() );
 			final ClassLoaderService cls = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			try {
 				final Class<AttributeConverter> converterClass = cls.classForName( converterClassName );
 				attributeConverterDescriptor = new AttributeConverterDescriptorNonAutoApplicableImpl( converterClass.newInstance() );
 				return;
 			}
 			catch (Exception e) {
 				log.logBadHbmAttributeConverterType( typeName, e.getMessage() );
 			}
 		}
 
 		this.typeName = typeName;
 	}
 
 	public void makeNationalized() {
 		this.isNationalized = true;
 	}
 
 	public boolean isNationalized() {
 		return isNationalized;
 	}
 
 	public void makeLob() {
 		this.isLob = true;
 	}
 
 	public boolean isLob() {
 		return isLob;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	@Override
 	public void createForeignKey() throws MappingException {}
 
 	@Override
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	private IdentifierGenerator identifierGenerator;
 
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 
 		if ( identifierGenerator != null ) {
 			return identifierGenerator;
 		}
 
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
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
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
 				if ( iter.hasNext() ) {
 					tables.append(", ");
 				}
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
 		final ConfigurationService cs = metadata.getMetadataBuildingOptions().getServiceRegistry()
 				.getService( ConfigurationService.class );
 
 		params.put(
 				AvailableSettings.PREFER_POOLED_VALUES_LO,
 				cs.getSetting( AvailableSettings.PREFER_POOLED_VALUES_LO, StandardConverters.BOOLEAN, false )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		identifierGenerator = identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 
 		return identifierGenerator;
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
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Object selectable = itr.next();
 			if ( selectable instanceof Formula ) {
 				// if there are *any* formulas, then the Value overall is
 				// considered nullable
 				return true;
 			}
 			else if ( !( (Column) selectable ).isNullable() ) {
 				// if there is a single non-nullable column, the Value
 				// overall is considered non-nullable.
 				return false;
 			}
 		}
 		// nullable by default
 		return true;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = metadata.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( attributeConverterDescriptor == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "Attribute types for a dynamic entity must be explicitly specified: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName, metadata.getMetadataBuildingOptions().getServiceRegistry().getService( ClassLoaderService.class ) ).getName();
 			// todo : to fully support isNationalized here we need do the process hinted at above
 			// 		essentially, much of the logic from #buildAttributeConverterTypeAdapter wrt resolving
 			//		a (1) SqlTypeDescriptor, a (2) JavaTypeDescriptor and dynamically building a BasicType
 			// 		combining them.
 			return;
 		}
 
 		// we had an AttributeConverter...
 		type = buildAttributeConverterTypeAdapter();
 	}
 
 	/**
 	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
 	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
 	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
 	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
 	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
 	 * to bind/extract through the converter.
 	 * <p/>
 	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
 	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
 	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
 	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
 	 * VARCHAR/CHAR):<ul>
 	 *     <li>
 	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
 	 *         into a String and pass that String to setString.  The conversion is handled by calling
 	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
 	 *     </li>
 	 *     <li>
 	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
 	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
 	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
 	 *     </li>
 	 * </ul>
 	 *
 	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 	 */
 	@SuppressWarnings("unchecked")
 	private Type buildAttributeConverterTypeAdapter() {
 		// todo : validate the number of columns present here?
 
 		final Class entityAttributeJavaType = attributeConverterDescriptor.getDomainType();
 		final Class databaseColumnJavaType = attributeConverterDescriptor.getJdbcType();
 
 
 		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.
 		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 
 
 		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
 		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
 		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
 		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
 		// 		VARCHAR/CHAR
 		int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 		if ( isLob() ) {
 			if ( LobTypeMappings.INSTANCE.hasCorrespondingLobCode( jdbcTypeCode ) ) {
 				jdbcTypeCode = LobTypeMappings.INSTANCE.getCorrespondingLobCode( jdbcTypeCode );
 			}
 			else {
 				if ( Serializable.class.isAssignableFrom( entityAttributeJavaType ) ) {
 					jdbcTypeCode = Types.BLOB;
 				}
 				else {
 					throw new IllegalArgumentException(
 							String.format(
 									Locale.ROOT,
 									"JDBC type-code [%s (%s)] not known to have a corresponding LOB equivalent, and Java type is not Serializable (to use BLOB)",
 									jdbcTypeCode,
 									JdbcTypeNameMapper.getTypeName( jdbcTypeCode )
 							)
 					);
 				}
 			}
 		}
 		if ( isNationalized() ) {
 			jdbcTypeCode = NationalizedTypeMappings.INSTANCE.getCorrespondingNationalizedCode( jdbcTypeCode );
 		}
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDescriptor.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptorAdapter,
 				entityAttributeJavaType,
 				databaseColumnJavaType,
 				entityAttributeJavaTypeDescriptor
 		);
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
 
+	public void copyTypeFrom( SimpleValue sourceValue ) {
+		setTypeName( sourceValue.getTypeName() );
+		setTypeParameters( sourceValue.getTypeParameters() );
+
+		type = sourceValue.type;
+		attributeConverterDescriptor = sourceValue.attributeConverterDescriptor;
+	}
+
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
 
 	public void setJpaAttributeConverterDescriptor(AttributeConverterDescriptor attributeConverterDescriptor) {
 		this.attributeConverterDescriptor = attributeConverterDescriptor;
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				Selectable column = columns.get(i);
 				if (column instanceof Column){
 					columnsNames[i] = ((Column) column).getName();
 				}
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							classLoaderService.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, e );
 		}
 	}
 
 	private static final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/map/ColorType.java b/hibernate-core/src/test/java/org/hibernate/test/converter/map/ColorType.java
new file mode 100644
index 0000000000..ed19fa39cc
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/map/ColorType.java
@@ -0,0 +1,42 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.converter.map;
+
+/**
+ * @author Steve Ebersole
+ * an enum-like class (converters are technically not allowed to apply to enums)
+ */
+public class ColorType {
+	public static ColorType BLUE = new ColorType( "blue" );
+	public static ColorType RED = new ColorType( "red" );
+	public static ColorType YELLOW = new ColorType( "yellow" );
+
+	private final String color;
+
+	public ColorType(String color) {
+		this.color = color;
+	}
+
+	public String toExternalForm() {
+		return color;
+	}
+
+	public static ColorType fromExternalForm(String color) {
+		if ( BLUE.color.equals( color ) ) {
+			return BLUE;
+		}
+		else if ( RED.color.equals( color ) ) {
+			return RED;
+		}
+		else if ( YELLOW.color.equals( color ) ) {
+			return YELLOW;
+		}
+		else {
+			throw new RuntimeException( "Unknown color : " + color );
+		}
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/map/ColorTypeConverter.java b/hibernate-core/src/test/java/org/hibernate/test/converter/map/ColorTypeConverter.java
new file mode 100644
index 0000000000..a83208e7ca
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/map/ColorTypeConverter.java
@@ -0,0 +1,27 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.converter.map;
+
+import javax.persistence.AttributeConverter;
+import javax.persistence.Converter;
+
+/**
+ * @author Steve Ebersole
+ */
+@Converter(autoApply = true)
+public class ColorTypeConverter implements AttributeConverter<ColorType, String> {
+
+	@Override
+	public String convertToDatabaseColumn(ColorType attribute) {
+		return attribute == null ? null : attribute.toExternalForm();
+	}
+
+	@Override
+	public ColorType convertToEntityAttribute(String dbData) {
+		return dbData == null ? null : ColorType.fromExternalForm( dbData );
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyAttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyAttributeConverterTest.java
new file mode 100644
index 0000000000..c091f29406
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyAttributeConverterTest.java
@@ -0,0 +1,382 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.converter.map;
+
+import java.util.HashMap;
+import java.util.Map;
+import javax.persistence.AttributeConverter;
+import javax.persistence.CascadeType;
+import javax.persistence.Convert;
+import javax.persistence.Converter;
+import javax.persistence.Entity;
+import javax.persistence.EnumType;
+import javax.persistence.Enumerated;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.MapKey;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+import org.hibernate.Transaction;
+
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+
+/**
+ * @author Janario Oliveira
+ */
+public class MapKeyAttributeConverterTest extends BaseNonConfigCoreFunctionalTestCase {
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+				MapEntity.class, MapValue.class,
+
+				ColorTypeConverter.class,
+
+				CustomColorTypeConverter.class,
+				ImplicitEnumMapKeyConverter.class,
+				ExplicitEnumMapKeyConverter.class,
+				ImplicitEnumMapKeyOverridedConverter.class
+		};
+	}
+
+	@Test
+	public void testImplicitType() {
+		MapValue mapValue = create();
+		mapValue.implicitType = ColorType.BLUE;
+		mapValue.mapEntity.implicitType.put( mapValue.implicitType, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.implicitType.size() );
+		MapValue foundValue = found.implicitType.get( ColorType.BLUE );
+		assertEquals( ColorType.BLUE, foundValue.implicitType );
+
+		assertEquals( "blue", findDatabaseValue( foundValue, "implicitType" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testExplicitType() {
+		MapValue mapValue = create();
+		mapValue.explicitType = ColorType.RED;
+		mapValue.mapEntity.explicitType.put( mapValue.explicitType, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.explicitType.size() );
+		MapValue foundValue = found.explicitType.get( ColorType.RED );
+		assertEquals( ColorType.RED, foundValue.explicitType );
+
+		assertEquals( "COLOR-red", findDatabaseValue( foundValue, "explicitType" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumDefaultType() {
+		MapValue mapValue = create();
+		mapValue.enumDefault = EnumMapKey.VALUE_1;
+		mapValue.mapEntity.enumDefaultType.put( mapValue.enumDefault, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumDefaultType.size() );
+		MapValue foundValue = found.enumDefaultType.get( EnumMapKey.VALUE_1 );
+		assertEquals( EnumMapKey.VALUE_1, foundValue.enumDefault );
+
+		assertEquals( 0, findDatabaseValue( foundValue, "enumDefault" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumExplicitOrdinalType() {
+		MapValue mapValue = create();
+		mapValue.enumExplicitOrdinal = EnumMapKey.VALUE_2;
+		mapValue.mapEntity.enumExplicitOrdinalType.put( mapValue.enumExplicitOrdinal, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumExplicitOrdinalType.size() );
+		MapValue foundValue = found.enumExplicitOrdinalType.get( EnumMapKey.VALUE_2 );
+		assertEquals( EnumMapKey.VALUE_2, foundValue.enumExplicitOrdinal );
+
+		assertEquals( 1, findDatabaseValue( foundValue, "enumExplicitOrdinal" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumExplicitStringType() {
+		MapValue mapValue = create();
+		mapValue.enumExplicitString = EnumMapKey.VALUE_1;
+		mapValue.mapEntity.enumExplicitStringType.put( mapValue.enumExplicitString, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumExplicitStringType.size() );
+		MapValue foundValue = found.enumExplicitStringType.get( EnumMapKey.VALUE_1 );
+		assertEquals( EnumMapKey.VALUE_1, foundValue.enumExplicitString );
+
+		assertEquals( "VALUE_1", findDatabaseValue( foundValue, "enumExplicitString" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumExplicitType() {
+		MapValue mapValue = create();
+		mapValue.enumExplicit = EnumMapKey.VALUE_2;
+		mapValue.mapEntity.enumExplicitType.put( mapValue.enumExplicit, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumExplicitType.size() );
+		MapValue foundValue = found.enumExplicitType.get( EnumMapKey.VALUE_2 );
+		assertEquals( EnumMapKey.VALUE_2, foundValue.enumExplicit );
+
+		assertEquals( "2", findDatabaseValue( foundValue, "enumExplicit" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumImplicitType() {
+		MapValue mapValue = create();
+		mapValue.enumImplicit = ImplicitEnumMapKey.VALUE_2;
+		mapValue.mapEntity.enumImplicitType.put( mapValue.enumImplicit, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumImplicitType.size() );
+		MapValue foundValue = found.enumImplicitType.get( ImplicitEnumMapKey.VALUE_2 );
+		assertEquals( ImplicitEnumMapKey.VALUE_2, foundValue.enumImplicit );
+
+		assertEquals( "I2", findDatabaseValue( foundValue, "enumImplicit" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumImplicitOverrideOrdinalType() {
+		MapValue mapValue = create();
+		mapValue.enumImplicitOverrideOrdinal = ImplicitEnumMapKey.VALUE_1;
+		mapValue.mapEntity.enumImplicitOverrideOrdinalType.put( mapValue.enumImplicitOverrideOrdinal, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumImplicitOverrideOrdinalType.size() );
+		MapValue foundValue = found.enumImplicitOverrideOrdinalType.get( ImplicitEnumMapKey.VALUE_1 );
+		assertEquals( ImplicitEnumMapKey.VALUE_1, foundValue.enumImplicitOverrideOrdinal );
+
+		assertEquals( 0, findDatabaseValue( foundValue, "enumImplicitOverrideOrdinal" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumImplicitOverrideStringType() {
+		MapValue mapValue = create();
+		mapValue.enumImplicitOverrideString = ImplicitEnumMapKey.VALUE_2;
+		mapValue.mapEntity.enumImplicitOverrideStringType.put( mapValue.enumImplicitOverrideString, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumImplicitOverrideStringType.size() );
+		MapValue foundValue = found.enumImplicitOverrideStringType.get( ImplicitEnumMapKey.VALUE_2 );
+		assertEquals( ImplicitEnumMapKey.VALUE_2, foundValue.enumImplicitOverrideString );
+
+		assertEquals( "VALUE_2", findDatabaseValue( foundValue, "enumImplicitOverrideString" ) );
+		getSession().close();
+	}
+
+	@Test
+	public void testEnumImplicitOverridedType() {
+		MapValue mapValue = create();
+		mapValue.enumImplicitOverrided = ImplicitEnumMapKey.VALUE_1;
+		mapValue.mapEntity.enumImplicitOverridedType.put( mapValue.enumImplicitOverrided, mapValue );
+
+		MapEntity found = persist( mapValue.mapEntity );
+
+		assertEquals( 1, found.enumImplicitOverridedType.size() );
+		MapValue foundValue = found.enumImplicitOverridedType.get( ImplicitEnumMapKey.VALUE_1 );
+		assertEquals( ImplicitEnumMapKey.VALUE_1, foundValue.enumImplicitOverrided );
+
+		assertEquals( "O1", findDatabaseValue( foundValue, "enumImplicitOverrided" ) );
+		getSession().close();
+	}
+
+
+	private MapValue create() {
+		MapEntity mapEntity = new MapEntity();
+		return new MapValue( mapEntity );
+	}
+
+	private MapEntity persist(MapEntity mapEntity) {
+		Transaction tx = openSession().getTransaction();
+		tx.begin();
+		mapEntity = (MapEntity) getSession().merge( mapEntity );
+
+		tx.commit();
+		getSession().close();
+
+		mapEntity = openSession().get( MapEntity.class, mapEntity.id );
+		return mapEntity;
+	}
+
+	private Object findDatabaseValue(MapValue mapValue, String column) {
+		return getSession()
+				.createSQLQuery( "select mv." + column + " from map_value mv where mv.id=:id" )
+				.setParameter( "id", mapValue.id )
+				.uniqueResult();
+	}
+
+	@Entity
+	@Table(name = "map_entity")
+	public static class MapEntity {
+		@Id
+		@GeneratedValue(strategy = GenerationType.IDENTITY)
+		private Integer id;
+
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "implicitType")
+		private Map<ColorType, MapValue> implicitType = new HashMap<ColorType, MapValue>();
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "explicitType")
+		private Map<ColorType, MapValue> explicitType = new HashMap<ColorType, MapValue>();
+
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumDefault")
+		private Map<EnumMapKey, MapValue> enumDefaultType = new HashMap<EnumMapKey, MapValue>();
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumExplicitOrdinal")
+		private Map<EnumMapKey, MapValue> enumExplicitOrdinalType = new HashMap<EnumMapKey, MapValue>();
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumExplicitString")
+		private Map<EnumMapKey, MapValue> enumExplicitStringType = new HashMap<EnumMapKey, MapValue>();
+
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumExplicit")
+		private Map<EnumMapKey, MapValue> enumExplicitType = new HashMap<EnumMapKey, MapValue>();
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumImplicit")
+		private Map<ImplicitEnumMapKey, MapValue> enumImplicitType = new HashMap<ImplicitEnumMapKey, MapValue>();
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumImplicitOverrideOrdinal")
+		private Map<ImplicitEnumMapKey, MapValue> enumImplicitOverrideOrdinalType = new HashMap<ImplicitEnumMapKey, MapValue>();
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumImplicitOverrideString")
+		private Map<ImplicitEnumMapKey, MapValue> enumImplicitOverrideStringType = new HashMap<ImplicitEnumMapKey, MapValue>();
+
+		@OneToMany(mappedBy = "mapEntity", cascade = CascadeType.ALL)
+		@MapKey(name = "enumImplicitOverrided")
+		private Map<ImplicitEnumMapKey, MapValue> enumImplicitOverridedType = new HashMap<ImplicitEnumMapKey, MapValue>();
+	}
+
+	@Entity
+	@Table(name = "map_value")
+	public static class MapValue {
+		@Id
+		@GeneratedValue(strategy = GenerationType.IDENTITY)
+		private Integer id;
+		@ManyToOne
+		@JoinColumn(name = "map_entity_id")
+		private MapEntity mapEntity;
+
+		private ColorType implicitType;
+		@Convert(converter = CustomColorTypeConverter.class)
+		private ColorType explicitType;
+
+		private EnumMapKey enumDefault;
+		@Enumerated
+		private EnumMapKey enumExplicitOrdinal;
+		@Enumerated(EnumType.STRING)
+		private EnumMapKey enumExplicitString;
+		@Convert(converter = ExplicitEnumMapKeyConverter.class)
+		private EnumMapKey enumExplicit;
+
+		private ImplicitEnumMapKey enumImplicit;
+		@Enumerated
+		private ImplicitEnumMapKey enumImplicitOverrideOrdinal;
+		@Enumerated(EnumType.STRING)
+		private ImplicitEnumMapKey enumImplicitOverrideString;
+
+		@Convert(converter = ImplicitEnumMapKeyOverridedConverter.class)
+		private ImplicitEnumMapKey enumImplicitOverrided;
+
+		protected MapValue() {
+		}
+
+		public MapValue(MapEntity mapEntity) {
+			this.mapEntity = mapEntity;
+		}
+	}
+
+	public enum EnumMapKey {
+		VALUE_1,
+		VALUE_2
+	}
+
+	public enum ImplicitEnumMapKey {
+		VALUE_1,
+		VALUE_2
+	}
+
+
+	@Converter
+	public static class CustomColorTypeConverter implements AttributeConverter<ColorType, String> {
+		@Override
+		public String convertToDatabaseColumn(ColorType attribute) {
+			return attribute == null ? null : "COLOR-" + attribute.toExternalForm();
+		}
+
+		@Override
+		public ColorType convertToEntityAttribute(String dbData) {
+			return dbData == null ? null : ColorType.fromExternalForm( dbData.substring( 6 ) );
+		}
+	}
+
+	@Converter
+	public static class ExplicitEnumMapKeyConverter implements AttributeConverter<EnumMapKey, String> {
+		@Override
+		public String convertToDatabaseColumn(EnumMapKey attribute) {
+			return attribute == null ? null : attribute.name().substring( attribute.name().length() - 1 );
+		}
+
+		@Override
+		public EnumMapKey convertToEntityAttribute(String dbData) {
+			return dbData == null ? null : EnumMapKey.valueOf( "VALUE_" + dbData );
+		}
+	}
+
+	@Converter(autoApply = true)
+	public static class ImplicitEnumMapKeyConverter implements AttributeConverter<ImplicitEnumMapKey, String> {
+		@Override
+		public String convertToDatabaseColumn(ImplicitEnumMapKey attribute) {
+			return attribute == null ? null : "I" + attribute.name().substring( attribute.name().length() - 1 );
+		}
+
+		@Override
+		public ImplicitEnumMapKey convertToEntityAttribute(String dbData) {
+			return dbData == null ? null : ImplicitEnumMapKey.valueOf( "VALUE_" + dbData.substring( 1 ) );
+		}
+	}
+
+
+	@Converter
+	public static class ImplicitEnumMapKeyOverridedConverter implements AttributeConverter<ImplicitEnumMapKey, String> {
+		@Override
+		public String convertToDatabaseColumn(ImplicitEnumMapKey attribute) {
+			return attribute == null ? null :
+					( "O" + attribute.name().substring( attribute.name().length() - 1 ) );
+		}
+
+		@Override
+		public ImplicitEnumMapKey convertToEntityAttribute(String dbData) {
+			return dbData == null ? null : ImplicitEnumMapKey.valueOf( "VALUE_" + dbData.substring( 1 ) );
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyConversionTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyConversionTest.java
index 5911163f6e..9a7d513144 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyConversionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/map/MapKeyConversionTest.java
@@ -1,127 +1,78 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.converter.map;
 
 import java.util.HashMap;
 import java.util.Map;
-import javax.persistence.AttributeConverter;
 import javax.persistence.CollectionTable;
-import javax.persistence.Converter;
 import javax.persistence.ElementCollection;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.Table;
 
 import org.hibernate.Session;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-8529" )
 public class MapKeyConversionTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] { Customer.class, ColorTypeConverter.class };
 	}
 
 	@Test
 	public void testElementCollectionConversion() {
 		Session session = openSession();
 		session.getTransaction().begin();
 		Customer customer = new Customer( 1 );
 		customer.colors.put( ColorType.BLUE, "favorite" );
 		session.persist( customer );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.getTransaction().begin();
 		assertEquals( 1, session.get( Customer.class, 1 ).colors.size() );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.getTransaction().begin();
 		customer = session.get( Customer.class, 1 );
 		session.delete( customer );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Customer" )
 	@Table( name = "CUST" )
 	public static class Customer {
 		@Id
 		private Integer id;
 
 		@ElementCollection(fetch = FetchType.EAGER)
 		@CollectionTable( name = "cust_color", joinColumns = @JoinColumn( name = "cust_fk" ) )
 		private Map<ColorType, String> colors = new HashMap<ColorType, String>();
 
 		public Customer() {
 		}
 
 		public Customer(Integer id) {
 			this.id = id;
 		}
 	}
-
-
-	// an enum-like class (converters are technically not allowed to apply to enums)
-	public static class ColorType {
-		public static ColorType BLUE = new ColorType( "blue" );
-		public static ColorType RED = new ColorType( "red" );
-		public static ColorType YELLOW = new ColorType( "yellow" );
-
-		private final String color;
-
-		public ColorType(String color) {
-			this.color = color;
-		}
-
-		public String toExternalForm() {
-			return color;
-		}
-
-		public static ColorType fromExternalForm(String color) {
-			if ( BLUE.color.equals( color ) ) {
-				return BLUE;
-			}
-			else if ( RED.color.equals( color ) ) {
-				return RED;
-			}
-			else if ( YELLOW.color.equals( color ) ) {
-				return YELLOW;
-			}
-			else {
-				throw new RuntimeException( "Unknown color : " + color );
-			}
-		}
-	}
-
-	@Converter( autoApply = true )
-	public static class ColorTypeConverter implements AttributeConverter<ColorType, String> {
-
-		@Override
-		public String convertToDatabaseColumn(ColorType attribute) {
-			return attribute == null ? null : attribute.toExternalForm();
-		}
-
-		@Override
-		public ColorType convertToEntityAttribute(String dbData) {
-			return ColorType.fromExternalForm( dbData );
-		}
-	}
 }
