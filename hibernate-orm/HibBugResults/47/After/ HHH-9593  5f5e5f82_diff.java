diff --git a/hibernate-core/src/main/java/org/hibernate/boot/cfgxml/internal/JaxbCfgProcessor.java b/hibernate-core/src/main/java/org/hibernate/boot/cfgxml/internal/JaxbCfgProcessor.java
index d28bc254fd..a0cd747f24 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/cfgxml/internal/JaxbCfgProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/cfgxml/internal/JaxbCfgProcessor.java
@@ -1,265 +1,263 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.boot.cfgxml.internal;
 
-import java.io.IOException;
-import java.io.InputStream;
-import java.net.URL;
-import java.util.ArrayList;
-import java.util.Iterator;
-import java.util.List;
+import org.hibernate.HibernateException;
+import org.hibernate.boot.jaxb.Origin;
+import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgHibernateConfiguration;
+import org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.internal.util.config.ConfigurationException;
+import org.hibernate.internal.util.xml.XsdException;
+import org.jboss.logging.Logger;
+import org.xml.sax.SAXException;
+
 import javax.xml.XMLConstants;
 import javax.xml.bind.JAXBContext;
 import javax.xml.bind.JAXBException;
 import javax.xml.bind.Unmarshaller;
 import javax.xml.bind.ValidationEvent;
 import javax.xml.bind.ValidationEventHandler;
 import javax.xml.bind.ValidationEventLocator;
 import javax.xml.namespace.QName;
 import javax.xml.stream.XMLEventFactory;
 import javax.xml.stream.XMLEventReader;
 import javax.xml.stream.XMLInputFactory;
 import javax.xml.stream.XMLStreamException;
 import javax.xml.stream.events.Namespace;
 import javax.xml.stream.events.StartElement;
 import javax.xml.stream.events.XMLEvent;
 import javax.xml.stream.util.EventReaderDelegate;
 import javax.xml.transform.stream.StreamSource;
 import javax.xml.validation.Schema;
 import javax.xml.validation.SchemaFactory;
-
-import org.hibernate.HibernateException;
-import org.hibernate.boot.jaxb.Origin;
-import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgHibernateConfiguration;
-import org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
-import org.hibernate.internal.util.config.ConfigurationException;
-import org.hibernate.internal.util.xml.XsdException;
-
-import org.jboss.logging.Logger;
-
-import org.xml.sax.SAXException;
+import java.io.IOException;
+import java.io.InputStream;
+import java.net.URL;
+import java.util.ArrayList;
+import java.util.Iterator;
+import java.util.List;
 
 /**
  * @author Steve Ebersole
  */
 public class JaxbCfgProcessor {
 	private static final Logger log = Logger.getLogger( JaxbCfgProcessor.class );
 
 	public static final String HIBERNATE_CONFIGURATION_URI = "http://www.hibernate.org/xsd/orm/cfg";
 
 	private final ClassLoaderService classLoaderService;
 	private final LocalXmlResourceResolver xmlResourceResolver;
 
 	public JaxbCfgProcessor(ClassLoaderService classLoaderService) {
 		this.classLoaderService = classLoaderService;
 		this.xmlResourceResolver = new LocalXmlResourceResolver( classLoaderService );
 	}
 
 	public JaxbCfgHibernateConfiguration unmarshal(InputStream stream, Origin origin) {
 		try {
 			XMLEventReader staxReader = staxFactory().createXMLEventReader( stream );
 			try {
 				return unmarshal( staxReader, origin );
 			}
 			finally {
 				try {
 					staxReader.close();
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 		}
 		catch ( XMLStreamException e ) {
 			throw new HibernateException( "Unable to create stax reader", e );
 		}
 	}
 
 	private XMLInputFactory staxFactory;
 
 	private XMLInputFactory staxFactory() {
 		if ( staxFactory == null ) {
 			staxFactory = buildStaxFactory();
 		}
 		return staxFactory;
 	}
 
 	@SuppressWarnings( { "UnnecessaryLocalVariable" })
 	private XMLInputFactory buildStaxFactory() {
 		XMLInputFactory staxFactory = XMLInputFactory.newInstance();
 		staxFactory.setXMLResolver( xmlResourceResolver );
 		return staxFactory;
 	}
 
 	@SuppressWarnings( { "unchecked" })
 	private JaxbCfgHibernateConfiguration unmarshal(XMLEventReader staxEventReader, final Origin origin) {
 		XMLEvent event;
 		try {
 			event = staxEventReader.peek();
 			while ( event != null && !event.isStartElement() ) {
 				staxEventReader.nextEvent();
 				event = staxEventReader.peek();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Error accessing stax stream", e );
 		}
 
 		if ( event == null ) {
 			throw new HibernateException( "Could not locate root element" );
 		}
 
 		if ( !isNamespaced( event.asStartElement() ) ) {
 			// if the elements are not namespaced, wrap the reader in a reader which will namespace them as pulled.
 			log.debug( "cfg.xml document did not define namespaces; wrapping in custom event reader to introduce namespace information" );
 			staxEventReader = new NamespaceAddingEventReader( staxEventReader, HIBERNATE_CONFIGURATION_URI );
 		}
 
 		final ContextProvidingValidationEventHandler handler = new ContextProvidingValidationEventHandler();
 		try {
 			JAXBContext jaxbContext = JAXBContext.newInstance( JaxbCfgHibernateConfiguration.class );
 			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 			unmarshaller.setSchema( schema() );
 			unmarshaller.setEventHandler( handler );
 			return (JaxbCfgHibernateConfiguration) unmarshaller.unmarshal( staxEventReader );
 		}
 		catch ( JAXBException e ) {
 			throw new ConfigurationException(
 					"Unable to perform unmarshalling at line number " + handler.getLineNumber()
 							+ " and column " + handler.getColumnNumber()
 							+ " in " + origin.getType().name() + " " + origin.getName()
 							+ ". Message: " + handler.getMessage(), e
 			);
 		}
 	}
 
 	private boolean isNamespaced(StartElement startElement) {
 		return ! "".equals( startElement.getName().getNamespaceURI() );
 	}
 
 	private Schema schema;
 
 	private Schema schema() {
 		if ( schema == null ) {
 			schema = resolveLocalSchema( "org/hibernate/hibernate-configuration-4.0.xsd" );
 		}
 		return schema;
 	}
 
 	private Schema resolveLocalSchema(String schemaName) {
 		return resolveLocalSchema( schemaName, XMLConstants.W3C_XML_SCHEMA_NS_URI );
 	}
 
 	private Schema resolveLocalSchema(String schemaName, String schemaLanguage) {
 		URL url = classLoaderService.locateResource( schemaName );
 		if ( url == null ) {
 			throw new XsdException( "Unable to locate schema [" + schemaName + "] via classpath", schemaName );
 		}
 		try {
 			InputStream schemaStream = url.openStream();
 			try {
 				StreamSource source = new StreamSource( url.openStream() );
 				SchemaFactory schemaFactory = SchemaFactory.newInstance( schemaLanguage );
 				return schemaFactory.newSchema( source );
 			}
 			catch ( SAXException e ) {
 				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
 			}
 			catch ( IOException e ) {
 				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
 			}
 			finally {
 				try {
 					schemaStream.close();
 				}
 				catch ( IOException e ) {
 					log.debugf( "Problem closing schema stream [%s]", e.toString() );
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new XsdException( "Stream error handling schema url [" + url.toExternalForm() + "]", schemaName );
 		}
 	}
 
 	static class ContextProvidingValidationEventHandler implements ValidationEventHandler {
 		private int lineNumber;
 		private int columnNumber;
 		private String message;
 
 		@Override
 		public boolean handleEvent(ValidationEvent validationEvent) {
 			ValidationEventLocator locator = validationEvent.getLocator();
 			lineNumber = locator.getLineNumber();
 			columnNumber = locator.getColumnNumber();
 			message = validationEvent.getMessage();
 			return false;
 		}
 
 		public int getLineNumber() {
 			return lineNumber;
 		}
 
 		public int getColumnNumber() {
 			return columnNumber;
 		}
 
 		public String getMessage() {
 			return message;
 		}
 	}
 
-	public class NamespaceAddingEventReader extends EventReaderDelegate {
+	public static class NamespaceAddingEventReader extends EventReaderDelegate {
 		private final XMLEventFactory xmlEventFactory;
 		private final String namespaceUri;
 
 		public NamespaceAddingEventReader(XMLEventReader reader, String namespaceUri) {
 			this( reader, XMLEventFactory.newInstance(), namespaceUri );
 		}
 
 		public NamespaceAddingEventReader(XMLEventReader reader, XMLEventFactory xmlEventFactory, String namespaceUri) {
 			super( reader );
 			this.xmlEventFactory = xmlEventFactory;
 			this.namespaceUri = namespaceUri;
 		}
 
 		private StartElement withNamespace(StartElement startElement) {
 			// otherwise, wrap the start element event to provide a default namespace mapping
 			final List<Namespace> namespaces = new ArrayList<Namespace>();
 			namespaces.add( xmlEventFactory.createNamespace( "", namespaceUri ) );
 			Iterator<?> originalNamespaces = startElement.getNamespaces();
 			while ( originalNamespaces.hasNext() ) {
 				namespaces.add( (Namespace) originalNamespaces.next() );
 			}
 			return xmlEventFactory.createStartElement(
 					new QName( namespaceUri, startElement.getName().getLocalPart() ),
 					startElement.getAttributes(),
 					namespaces.iterator()
 			);
 		}
 
 		@Override
 		public XMLEvent nextEvent() throws XMLStreamException {
 			XMLEvent event = super.nextEvent();
 			if ( event.isStartElement() ) {
 				return withNamespace( event.asStartElement() );
 			}
 			return event;
 		}
 
 		@Override
 		public XMLEvent peek() throws XMLStreamException {
 			XMLEvent event = super.peek();
 			if ( event.isStartElement() ) {
 				return withNamespace( event.asStartElement() );
 			}
 			else {
 				return event;
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
index bb16a79a06..ca6e980d34 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
@@ -1,171 +1,171 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Modifier;
 import java.util.Set;
 
 import org.hibernate.bytecode.buildtime.spi.ClassFilter;
 import org.hibernate.bytecode.buildtime.spi.FieldFilter;
 import org.hibernate.bytecode.instrumentation.internal.javassist.JavassistHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.BytecodeProvider;
 import org.hibernate.bytecode.spi.ClassTransformer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.bytecode.spi.NotInstrumentedException;
 import org.hibernate.bytecode.spi.ProxyFactoryFactory;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Bytecode provider implementation for Javassist.
  *
  * @author Steve Ebersole
  */
 public class BytecodeProviderImpl implements BytecodeProvider {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			BytecodeProviderImpl.class.getName()
 	);
 
 	@Override
 	public ProxyFactoryFactory getProxyFactoryFactory() {
 		return new ProxyFactoryFactoryImpl();
 	}
 
 	@Override
 	public ReflectionOptimizer getReflectionOptimizer(
 			Class clazz,
 			String[] getterNames,
 			String[] setterNames,
 			Class[] types) {
 		FastClass fastClass;
 		BulkAccessor bulkAccessor;
 		try {
 			fastClass = FastClass.create( clazz );
 			bulkAccessor = BulkAccessor.create( clazz, getterNames, setterNames, types );
 			if ( !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() ) ) {
 				if ( fastClass == null ) {
 					bulkAccessor = null;
 				}
 				else {
 					//test out the optimizer:
 					final Object instance = fastClass.newInstance();
 					bulkAccessor.setPropertyValues( instance, bulkAccessor.getPropertyValues( instance ) );
 				}
 			}
 		}
 		catch ( Throwable t ) {
 			fastClass = null;
 			bulkAccessor = null;
 			if ( LOG.isDebugEnabled() ) {
 				int index = 0;
 				if (t instanceof BulkAccessorException) {
 					index = ( (BulkAccessorException) t ).getIndex();
 				}
 				if ( index >= 0 ) {
 					LOG.debugf(
 							"Reflection optimizer disabled for %s [%s: %s (property %s)]",
 							clazz.getName(),
 							StringHelper.unqualify( t.getClass().getName() ),
 							t.getMessage(),
 							setterNames[index]
 					);
 				}
 				else {
 					LOG.debugf(
 							"Reflection optimizer disabled for %s [%s: %s]",
 							clazz.getName(),
 							StringHelper.unqualify( t.getClass().getName() ),
 							t.getMessage()
 					);
 				}
 			}
 		}
 
 		if ( fastClass != null && bulkAccessor != null ) {
 			return new ReflectionOptimizerImpl(
 					new InstantiationOptimizerAdapter( fastClass ),
 					new AccessOptimizerAdapter( bulkAccessor, clazz )
 			);
 		}
 
 		return null;
 	}
 
 	@Override
 	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter) {
 		return new JavassistClassTransformer( classFilter, fieldFilter );
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getEntityInstrumentationMetadata(Class entityClass) {
 		return new EntityInstrumentationMetadataImpl( entityClass );
 	}
 
-	private class EntityInstrumentationMetadataImpl implements EntityInstrumentationMetadata {
+	private static class EntityInstrumentationMetadataImpl implements EntityInstrumentationMetadata {
 		private final Class entityClass;
 		private final boolean isInstrumented;
 
 		private EntityInstrumentationMetadataImpl(Class entityClass) {
 			this.entityClass = entityClass;
 			this.isInstrumented = FieldHandled.class.isAssignableFrom( entityClass );
 		}
 
 		@Override
 		public String getEntityName() {
 			return entityClass.getName();
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return isInstrumented;
 		}
 
 		@Override
 		public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
 			if ( !entityClass.isInstance( entity ) ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Passed entity instance [%s] is not of expected type [%s]",
 								entity,
 								getEntityName()
 						)
 				);
 			}
 			if ( ! isInstrumented() ) {
 				throw new NotInstrumentedException( String.format( "Entity class [%s] is not instrumented", getEntityName() ) );
 			}
 			return JavassistHelper.extractFieldInterceptor( entity );
 		}
 
 		@Override
 		public FieldInterceptor injectInterceptor(
 				Object entity,
 				String entityName,
 				Set uninitializedFieldNames,
 				SessionImplementor session) throws NotInstrumentedException {
 			if ( !entityClass.isInstance( entity ) ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Passed entity instance [%s] is not of expected type [%s]",
 								entity,
 								getEntityName()
 						)
 				);
 			}
 			if ( ! isInstrumented() ) {
 				throw new NotInstrumentedException( String.format( "Entity class [%s] is not instrumented", getEntityName() ) );
 			}
 			return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java
index 589b483fe6..0fefae4f98 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/SqlGenerator.java
@@ -1,421 +1,421 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.hibernate.NullPrecedence;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.SqlGeneratorBase;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.FunctionNode;
 import org.hibernate.hql.internal.ast.tree.Node;
 import org.hibernate.hql.internal.ast.tree.ParameterContainer;
 import org.hibernate.hql.internal.ast.tree.ParameterNode;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.type.Type;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 
 /**
  * Generates SQL by overriding callback methods in the base class, which does
  * the actual SQL AST walking.
  *
  * @author Joshua Davis
  * @author Steve Ebersole
  */
 public class SqlGenerator extends SqlGeneratorBase implements ErrorReporter {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SqlGenerator.class );
 
 	public static boolean REGRESSION_STYLE_CROSS_JOINS;
 
 	/**
 	 * all append invocations on the buf should go through this Output instance variable.
 	 * The value of this variable may be temporarily substituted by sql function processing code
 	 * to catch generated arguments.
 	 * This is because sql function templates need arguments as separate string chunks
 	 * that will be assembled into the target dialect-specific function call.
 	 */
 	private SqlWriter writer = new DefaultWriter();
 
 	private ParseErrorHandler parseErrorHandler;
 	private SessionFactoryImplementor sessionFactory;
 	private LinkedList<SqlWriter> outputStack = new LinkedList<SqlWriter>();
 	private final ASTPrinter printer = new ASTPrinter( SqlTokenTypes.class );
 	private List<ParameterSpecification> collectedParameters = new ArrayList<ParameterSpecification>();
 
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int traceDepth;
 
 	@Override
 	public void traceIn(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = StringHelper.repeat( '-', ( traceDepth++ * 2 ) ) + "-> ";
 		String traceText = ruleName + " (" + buildTraceNodeName( tree ) + ")";
 		LOG.trace( prefix + traceText );
 	}
 
 	private String buildTraceNodeName(AST tree) {
 		return tree == null
 				? "???"
 				: tree.getText() + " [" + printer.getTokenTypeName( tree.getType() ) + "]";
 	}
 
 	@Override
 	public void traceOut(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = "<-" + StringHelper.repeat( '-', ( --traceDepth * 2 ) ) + " ";
 		LOG.trace( prefix + ruleName );
 	}
 
 	public List<ParameterSpecification> getCollectedParameters() {
 		return collectedParameters;
 	}
 
 	@Override
 	protected void out(String s) {
 		writer.clause( s );
 	}
 
 	@Override
 	protected void out(AST n) {
 		if ( n instanceof Node ) {
 			out( ( (Node) n ).getRenderText( sessionFactory ) );
 		}
 		else {
 			super.out( n );
 		}
 
 		if ( n instanceof ParameterNode ) {
 			collectedParameters.add( ( (ParameterNode) n ).getHqlParameterSpecification() );
 		}
 		else if ( n instanceof ParameterContainer ) {
 			if ( ( (ParameterContainer) n ).hasEmbeddedParameters() ) {
 				ParameterSpecification[] specifications = ( (ParameterContainer) n ).getEmbeddedParameters();
 				if ( specifications != null ) {
 					collectedParameters.addAll( Arrays.asList( specifications ) );
 				}
 			}
 		}
 	}
 
 	@Override
 	protected void betweenFunctionArguments() {
 		writer.betweenFunctionArguments();
 	}
 
 	@Override
 	public void reportError(RecognitionException e) {
 		parseErrorHandler.reportError( e );
 	}
 
 	@Override
 	public void reportError(String s) {
 		parseErrorHandler.reportError( s );
 	}
 
 	@Override
 	public void reportWarning(String s) {
 		parseErrorHandler.reportWarning( s );
 	}
 
 	public ParseErrorHandler getParseErrorHandler() {
 		return parseErrorHandler;
 	}
 
 	public SqlGenerator(SessionFactoryImplementor sfi) {
 		super();
 		parseErrorHandler = new ErrorCounter();
 		sessionFactory = sfi;
 	}
 
 	public String getSQL() {
 		return getStringBuilder().toString();
 	}
 
 	@Override
 	protected void optionalSpace() {
 		int c = getLastChar();
 		switch ( c ) {
 			case -1:
 				return;
 			case ' ':
 				return;
 			case ')':
 				return;
 			case '(':
 				return;
 			default:
 				out( " " );
 		}
 	}
 
 	@Override
 	protected void beginFunctionTemplate(AST node, AST nameNode) {
 		// NOTE for AGGREGATE both nodes are the same; for METHOD the first is the METHOD, the second is the
 		// 		METHOD_NAME
 		FunctionNode functionNode = (FunctionNode) node;
 		SQLFunction sqlFunction = functionNode.getSQLFunction();
 		if ( sqlFunction == null ) {
 			// if SQLFunction is null we just write the function out as it appears in the hql statement
 			super.beginFunctionTemplate( node, nameNode );
 		}
 		else {
 			// this function has a registered SQLFunction -> redirect output and catch the arguments
 			outputStack.addFirst( writer );
 			if ( node.getType() == CAST ) {
 				writer = new CastFunctionArguments();
 			}
 			else {
 				writer = new StandardFunctionArguments();
 			}
 		}
 	}
 
 	@Override
 	protected void endFunctionTemplate(AST node) {
 		FunctionNode functionNode = (FunctionNode) node;
 		SQLFunction sqlFunction = functionNode.getSQLFunction();
 		if ( sqlFunction == null ) {
 			super.endFunctionTemplate( node );
 		}
 		else {
 			final Type functionType = functionNode.getFirstArgumentType();
 			// this function has a registered SQLFunction -> redirect output and catch the arguments
 			FunctionArgumentsCollectingWriter functionArguments = (FunctionArgumentsCollectingWriter) writer;
 			writer = outputStack.removeFirst();
 			out( sqlFunction.render( functionType, functionArguments.getArgs(), sessionFactory ) );
 		}
 	}
 
 	// --- Inner classes (moved here from sql-gen.g) ---
 
 	/**
 	 * Writes SQL fragments.
 	 */
 	interface SqlWriter {
 		void clause(String clause);
 
 		void betweenFunctionArguments();
 	}
 
 	interface FunctionArgumentsCollectingWriter extends SqlWriter {
 		public List getArgs();
 	}
 
 	/**
 	 * SQL function processing code redirects generated SQL output to an instance of this class
 	 * which catches function arguments.
 	 */
-	class StandardFunctionArguments implements FunctionArgumentsCollectingWriter {
+	static class StandardFunctionArguments implements FunctionArgumentsCollectingWriter {
 		private int argInd;
 		private final List<String> args = new ArrayList<String>( 3 );
 
 		@Override
 		public void clause(String clause) {
 			if ( argInd == args.size() ) {
 				args.add( clause );
 			}
 			else {
 				args.set( argInd, args.get( argInd ) + clause );
 			}
 		}
 
 		@Override
 		public void betweenFunctionArguments() {
 			++argInd;
 		}
 
 		public List getArgs() {
 			return args;
 		}
 	}
 
 	/**
 	 * SQL function processing code redirects generated SQL output to an instance of this class
 	 * which catches function arguments.
 	 */
-	class CastFunctionArguments implements FunctionArgumentsCollectingWriter {
+	static class CastFunctionArguments implements FunctionArgumentsCollectingWriter {
 		private String castExpression;
 		private String castTargetType;
 
 		private boolean startedType;
 
 		@Override
 		public void clause(String clause) {
 			if ( startedType ) {
 				if ( castTargetType == null ) {
 					castTargetType = clause;
 				}
 				else {
 					castTargetType += clause;
 				}
 			}
 			else {
 				if ( castExpression == null ) {
 					castExpression = clause;
 				}
 				else {
 					castExpression += clause;
 				}
 			}
 		}
 
 		@Override
 		public void betweenFunctionArguments() {
 			if ( startedType ) {
 				throw new QueryException( "CAST function should only have 2 arguments" );
 			}
 			startedType = true;
 		}
 
 		public List getArgs() {
 			List<String> rtn = CollectionHelper.arrayList( 2 );
 			rtn.add( castExpression );
 			rtn.add( castTargetType );
 			return rtn;
 		}
 	}
 
 	/**
 	 * The default SQL writer.
 	 */
 	class DefaultWriter implements SqlWriter {
 		@Override
 		public void clause(String clause) {
 			getStringBuilder().append( clause );
 		}
 
 		@Override
 		public void betweenFunctionArguments() {
 			getStringBuilder().append( ", " );
 		}
 	}
 
 	public static void panic() {
 		throw new QueryException( "TreeWalker: panic" );
 	}
 
 	@Override
 	protected void fromFragmentSeparator(AST a) {
 		// check two "adjecent" nodes at the top of the from-clause tree
 		AST next = a.getNextSibling();
 		if ( next == null || !hasText( a ) ) {
 			return;
 		}
 
 		FromElement left = (FromElement) a;
 		FromElement right = (FromElement) next;
 
 		///////////////////////////////////////////////////////////////////////
 		// HACK ALERT !!!!!!!!!!!!!!!!!!!!!!!!!!!!
 		// Attempt to work around "ghost" ImpliedFromElements that occasionally
 		// show up between the actual things being joined.  This consistently
 		// occurs from index nodes (at least against many-to-many).  Not sure
 		// if there are other conditions
 		//
 		// Essentially, look-ahead to the next FromElement that actually
 		// writes something to the SQL
 		while ( right != null && !hasText( right ) ) {
 			right = (FromElement) right.getNextSibling();
 		}
 		if ( right == null ) {
 			return;
 		}
 		///////////////////////////////////////////////////////////////////////
 
 		if ( !hasText( right ) ) {
 			return;
 		}
 
 		if ( right.getRealOrigin() == left ||
 				( right.getRealOrigin() != null && right.getRealOrigin() == left.getRealOrigin() ) ) {
 			// right represents a joins originating from left; or
 			// both right and left reprersent joins originating from the same FromElement
 			if ( right.getJoinSequence() != null && right.getJoinSequence().isThetaStyle() ) {
 				writeCrossJoinSeparator();
 			}
 			else {
 				out( " " );
 			}
 		}
 		else {
 			// these are just two unrelated table references
 			writeCrossJoinSeparator();
 		}
 	}
 
 	private void writeCrossJoinSeparator() {
 		if ( REGRESSION_STYLE_CROSS_JOINS ) {
 			out( ", " );
 		}
 		else {
 			out( sessionFactory.getDialect().getCrossJoinSeparator() );
 		}
 	}
 
 	@Override
 	protected void nestedFromFragment(AST d, AST parent) {
 		// check a set of parent/child nodes in the from-clause tree
 		// to determine if a comma is required between them
 		if ( d != null && hasText( d ) ) {
 			if ( parent != null && hasText( parent ) ) {
 				// again, both should be FromElements
 				FromElement left = (FromElement) parent;
 				FromElement right = (FromElement) d;
 				if ( right.getRealOrigin() == left ) {
 					// right represents a joins originating from left...
 					if ( right.getJoinSequence() != null && right.getJoinSequence().isThetaStyle() ) {
 						out( ", " );
 					}
 					else {
 						out( " " );
 					}
 				}
 				else {
 					// not so sure this is even valid subtree.  but if it was, it'd
 					// represent two unrelated table references...
 					out( ", " );
 				}
 			}
 			out( d );
 		}
 	}
 
 	@Override
 	protected String renderOrderByElement(String expression, String order, String nulls) {
 		final NullPrecedence nullPrecedence = NullPrecedence.parse( nulls,
 																	sessionFactory.getSettings()
 																			.getDefaultNullPrecedence()
 		);
 		return sessionFactory.getDialect().renderOrderByElement( expression, null, order, nullPrecedence );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
index 15e882b452..ed40473c08 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
@@ -1,495 +1,495 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the {@link SQLQuery} contract.
  *
  * @author Max Andersen
  * @author Steve Ebersole
  */
 public class SQLQueryImpl extends AbstractQueryImpl implements SQLQuery {
 
 	private List<NativeSQLQueryReturn> queryReturns;
 	private List<ReturnBuilder> queryReturnBuilders;
 	private boolean autoDiscoverTypes;
 
 	private Collection<String> querySpaces;
 
 	private final boolean callable;
 	private final LockOptions lockOptions = new LockOptions();
 
 	/**
 	 * Constructs a SQLQueryImpl given a sql query defined in the mappings.
 	 *
 	 * @param queryDef The representation of the defined <sql-query/>.
 	 * @param session The session to which this SQLQueryImpl belongs.
 	 * @param parameterMetadata Metadata about parameters found in the query.
 	 */
 	SQLQueryImpl(NamedSQLQueryDefinition queryDef, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( queryDef.getQueryString(), queryDef.getFlushMode(), session, parameterMetadata );
 		if ( queryDef.getResultSetRef() != null ) {
 			ResultSetMappingDefinition definition = session.getFactory()
 					.getResultSetMapping( queryDef.getResultSetRef() );
 			if ( definition == null ) {
 				throw new MappingException(
 						"Unable to find resultset-ref definition: " +
 								queryDef.getResultSetRef()
 				);
 			}
 			this.queryReturns = new ArrayList<NativeSQLQueryReturn>( Arrays.asList( definition.getQueryReturns() ) );
 		}
 		else if ( queryDef.getQueryReturns() != null && queryDef.getQueryReturns().length > 0 ) {
 			this.queryReturns = new ArrayList<NativeSQLQueryReturn>( Arrays.asList( queryDef.getQueryReturns() ) );
 		}
 		else {
 			this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
 		}
 
 		this.querySpaces = queryDef.getQuerySpaces();
 		this.callable = queryDef.isCallable();
 	}
 
 	SQLQueryImpl(String sql, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		this( sql, false, session, parameterMetadata );
 	}
 
 	SQLQueryImpl(String sql, boolean callable, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( sql, null, session, parameterMetadata );
 		this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
 		this.querySpaces = null;
 		this.callable = callable;
 	}
 
 	@Override
 	public List<NativeSQLQueryReturn> getQueryReturns() {
 		prepareQueryReturnsIfNecessary();
 		return queryReturns;
 	}
 
 	@Override
 	public Collection<String> getSynchronizedQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	public boolean isCallable() {
 		return callable;
 	}
 
 	@Override
 	public List list() throws HibernateException {
 		verifyParameters();
 		before();
 
 		Map namedParams = getNamedParams();
 		NativeSQLQuerySpecification spec = generateQuerySpecification( namedParams );
 
 		try {
 			return getSession().list( spec, getQueryParameters( namedParams ) );
 		}
 		finally {
 			after();
 		}
 	}
 
 	private NativeSQLQuerySpecification generateQuerySpecification(Map namedParams) {
 		return new NativeSQLQuerySpecification(
 				expandParameterLists( namedParams ),
 				queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] ),
 				querySpaces
 		);
 	}
 
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
 		verifyParameters();
 		before();
 
 		Map namedParams = getNamedParams();
 		NativeSQLQuerySpecification spec = generateQuerySpecification( namedParams );
 
 		QueryParameters qp = getQueryParameters( namedParams );
 		qp.setScrollMode( scrollMode );
 
 		try {
 			return getSession().scroll( spec, qp );
 		}
 		finally {
 			after();
 		}
 	}
 
 	public ScrollableResults scroll() throws HibernateException {
 		return scroll( session.getFactory().getDialect().defaultScrollMode() );
 	}
 
 	public Iterator iterate() throws HibernateException {
 		throw new UnsupportedOperationException( "SQL queries do not currently support iteration" );
 	}
 
 	@Override
 	public QueryParameters getQueryParameters(Map namedParams) {
 		QueryParameters qp = super.getQueryParameters( namedParams );
 		qp.setCallable( callable );
 		qp.setAutoDiscoverScalarTypes( autoDiscoverTypes );
 		return qp;
 	}
 
 	@Override
 	protected void verifyParameters() {
 		// verifyParameters is called at the start of all execution type methods, so we use that here to perform
 		// some preparation work.
 		prepareQueryReturnsIfNecessary();
 		verifyParameters( callable );
 		boolean noReturns = queryReturns == null || queryReturns.isEmpty();
 		if ( noReturns ) {
 			this.autoDiscoverTypes = noReturns;
 		}
 		else {
 			for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
 				if ( queryReturn instanceof NativeSQLQueryScalarReturn ) {
 					NativeSQLQueryScalarReturn scalar = (NativeSQLQueryScalarReturn) queryReturn;
 					if ( scalar.getType() == null ) {
 						autoDiscoverTypes = true;
 						break;
 					}
 				}
 				else if ( NativeSQLQueryConstructorReturn.class.isInstance( queryReturn ) ) {
 					autoDiscoverTypes = true;
 					break;
 				}
 			}
 		}
 	}
 
 	private void prepareQueryReturnsIfNecessary() {
 		if ( queryReturnBuilders != null ) {
 			if ( !queryReturnBuilders.isEmpty() ) {
 				if ( queryReturns != null ) {
 					queryReturns.clear();
 					queryReturns = null;
 				}
 				queryReturns = new ArrayList<NativeSQLQueryReturn>();
 				for ( ReturnBuilder builder : queryReturnBuilders ) {
 					queryReturns.add( builder.buildReturn() );
 				}
 				queryReturnBuilders.clear();
 			}
 			queryReturnBuilders = null;
 		}
 	}
 
 	@Override
 	public String[] getReturnAliases() throws HibernateException {
 		throw new UnsupportedOperationException( "SQL queries do not currently support returning aliases" );
 	}
 
 	@Override
 	public Type[] getReturnTypes() throws HibernateException {
 		throw new UnsupportedOperationException( "not yet implemented for SQL queries" );
 	}
 
 	public Query setLockMode(String alias, LockMode lockMode) {
 		throw new UnsupportedOperationException( "cannot set the lock mode for a native SQL query" );
 	}
 
 	public Query setLockOptions(LockOptions lockOptions) {
 		throw new UnsupportedOperationException( "cannot set lock options for a native SQL query" );
 	}
 
 	@Override
 	public LockOptions getLockOptions() {
 		//we never need to apply locks to the SQL, however the native-sql loader handles this specially
 		return lockOptions;
 	}
 
 	public SQLQuery addScalar(final String columnAlias, final Type type) {
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add(
 				new ReturnBuilder() {
 					public NativeSQLQueryReturn buildReturn() {
 						return new NativeSQLQueryScalarReturn( columnAlias, type );
 					}
 				}
 		);
 		return this;
 	}
 
 	public SQLQuery addScalar(String columnAlias) {
 		return addScalar( columnAlias, null );
 	}
 
 	public RootReturn addRoot(String tableAlias, String entityName) {
 		RootReturnBuilder builder = new RootReturnBuilder( tableAlias, entityName );
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add( builder );
 		return builder;
 	}
 
 	public RootReturn addRoot(String tableAlias, Class entityType) {
 		return addRoot( tableAlias, entityType.getName() );
 	}
 
 	public SQLQuery addEntity(String entityName) {
 		return addEntity( StringHelper.unqualify( entityName ), entityName );
 	}
 
 	public SQLQuery addEntity(String alias, String entityName) {
 		addRoot( alias, entityName );
 		return this;
 	}
 
 	public SQLQuery addEntity(String alias, String entityName, LockMode lockMode) {
 		addRoot( alias, entityName ).setLockMode( lockMode );
 		return this;
 	}
 
 	public SQLQuery addEntity(Class entityType) {
 		return addEntity( entityType.getName() );
 	}
 
 	public SQLQuery addEntity(String alias, Class entityClass) {
 		return addEntity( alias, entityClass.getName() );
 	}
 
 	public SQLQuery addEntity(String alias, Class entityClass, LockMode lockMode) {
 		return addEntity( alias, entityClass.getName(), lockMode );
 	}
 
 	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		FetchReturnBuilder builder = new FetchReturnBuilder( tableAlias, ownerTableAlias, joinPropertyName );
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add( builder );
 		return builder;
 	}
 
 	public SQLQuery addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		addFetch( tableAlias, ownerTableAlias, joinPropertyName );
 		return this;
 	}
 
 	public SQLQuery addJoin(String alias, String path) {
 		createFetchJoin( alias, path );
 		return this;
 	}
 
 	private FetchReturn createFetchJoin(String tableAlias, String path) {
 		int loc = path.indexOf( '.' );
 		if ( loc < 0 ) {
 			throw new QueryException( "not a property path: " + path );
 		}
 		final String ownerTableAlias = path.substring( 0, loc );
 		final String joinedPropertyName = path.substring( loc + 1 );
 		return addFetch( tableAlias, ownerTableAlias, joinedPropertyName );
 	}
 
 	public SQLQuery addJoin(String alias, String path, LockMode lockMode) {
 		createFetchJoin( alias, path ).setLockMode( lockMode );
 		return this;
 	}
 
 	public SQLQuery setResultSetMapping(String name) {
 		ResultSetMappingDefinition mapping = session.getFactory().getResultSetMapping( name );
 		if ( mapping == null ) {
 			throw new MappingException( "Unknown SqlResultSetMapping [" + name + "]" );
 		}
 		NativeSQLQueryReturn[] returns = mapping.getQueryReturns();
 		queryReturns.addAll( Arrays.asList( returns ) );
 		return this;
 	}
 
 	public SQLQuery addSynchronizedQuerySpace(String querySpace) {
 		if ( querySpaces == null ) {
 			querySpaces = new ArrayList<String>();
 		}
 		querySpaces.add( querySpace );
 		return this;
 	}
 
 	public SQLQuery addSynchronizedEntityName(String entityName) {
 		return addQuerySpaces( getSession().getFactory().getEntityPersister( entityName ).getQuerySpaces() );
 	}
 
 	public SQLQuery addSynchronizedEntityClass(Class entityClass) {
 		return addQuerySpaces( getSession().getFactory().getEntityPersister( entityClass.getName() ).getQuerySpaces() );
 	}
 
 	private SQLQuery addQuerySpaces(Serializable[] spaces) {
 		if ( spaces != null ) {
 			if ( querySpaces == null ) {
 				querySpaces = new ArrayList<String>();
 			}
 			querySpaces.addAll( Arrays.asList( (String[]) spaces ) );
 		}
 		return this;
 	}
 
 	public int executeUpdate() throws HibernateException {
 		Map namedParams = getNamedParams();
 		before();
 		try {
 			return getSession().executeNativeUpdate(
 					generateQuerySpecification( namedParams ),
 					getQueryParameters( namedParams )
 			);
 		}
 		finally {
 			after();
 		}
 	}
 
-	private class RootReturnBuilder implements RootReturn, ReturnBuilder {
+	private static class RootReturnBuilder implements RootReturn, ReturnBuilder {
 		private final String alias;
 		private final String entityName;
 		private LockMode lockMode = LockMode.READ;
 		private Map<String, String[]> propertyMappings;
 
 		private RootReturnBuilder(String alias, String entityName) {
 			this.alias = alias;
 			this.entityName = entityName;
 		}
 
 		public RootReturn setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public RootReturn setDiscriminatorAlias(String alias) {
 			addProperty( "class", alias );
 			return this;
 		}
 
 		public RootReturn addProperty(String propertyName, String columnAlias) {
 			addProperty( propertyName ).addColumnAlias( columnAlias );
 			return this;
 		}
 
 		public ReturnProperty addProperty(final String propertyName) {
 			if ( propertyMappings == null ) {
 				propertyMappings = new HashMap<String, String[]>();
 			}
 			return new ReturnProperty() {
 				public ReturnProperty addColumnAlias(String columnAlias) {
 					String[] columnAliases = propertyMappings.get( propertyName );
 					if ( columnAliases == null ) {
 						columnAliases = new String[] {columnAlias};
 					}
 					else {
 						String[] newColumnAliases = new String[columnAliases.length + 1];
 						System.arraycopy( columnAliases, 0, newColumnAliases, 0, columnAliases.length );
 						newColumnAliases[columnAliases.length] = columnAlias;
 						columnAliases = newColumnAliases;
 					}
 					propertyMappings.put( propertyName, columnAliases );
 					return this;
 				}
 			};
 		}
 
 		public NativeSQLQueryReturn buildReturn() {
 			return new NativeSQLQueryRootReturn( alias, entityName, propertyMappings, lockMode );
 		}
 	}
 
-	private class FetchReturnBuilder implements FetchReturn, ReturnBuilder {
+	private static class FetchReturnBuilder implements FetchReturn, ReturnBuilder {
 		private final String alias;
 		private String ownerTableAlias;
 		private final String joinedPropertyName;
 		private LockMode lockMode = LockMode.READ;
 		private Map<String, String[]> propertyMappings;
 
 		private FetchReturnBuilder(String alias, String ownerTableAlias, String joinedPropertyName) {
 			this.alias = alias;
 			this.ownerTableAlias = ownerTableAlias;
 			this.joinedPropertyName = joinedPropertyName;
 		}
 
 		public FetchReturn setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public FetchReturn addProperty(String propertyName, String columnAlias) {
 			addProperty( propertyName ).addColumnAlias( columnAlias );
 			return this;
 		}
 
 		public ReturnProperty addProperty(final String propertyName) {
 			if ( propertyMappings == null ) {
 				propertyMappings = new HashMap<String, String[]>();
 			}
 			return new ReturnProperty() {
 				public ReturnProperty addColumnAlias(String columnAlias) {
 					String[] columnAliases = propertyMappings.get( propertyName );
 					if ( columnAliases == null ) {
 						columnAliases = new String[] {columnAlias};
 					}
 					else {
 						String[] newColumnAliases = new String[columnAliases.length + 1];
 						System.arraycopy( columnAliases, 0, newColumnAliases, 0, columnAliases.length );
 						newColumnAliases[columnAliases.length] = columnAlias;
 						columnAliases = newColumnAliases;
 					}
 					propertyMappings.put( propertyName, columnAliases );
 					return this;
 				}
 			};
 		}
 
 		public NativeSQLQueryReturn buildReturn() {
 			return new NativeSQLQueryJoinReturn(
 					alias,
 					ownerTableAlias,
 					joinedPropertyName,
 					propertyMappings,
 					lockMode
 			);
 		}
 	}
 
 	private interface ReturnBuilder {
 		NativeSQLQueryReturn buildReturn();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterable.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterable.java
index ab393436cb..4518a67278 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterable.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterable.java
@@ -1,95 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal.util.collections;
 
 import java.util.Iterator;
 import java.util.List;
 
 
 /**
  * An JoinedIterable is an Iterable that wraps a number of Iterables.
  *
  * This class makes multiple iterables look like one to the caller.
  * When any method from the Iterator interface is called on the
  * Iterator object returned by {@link #iterator()}, the JoinedIterable
  * will delegate to a single underlying Iterator. The JoinedIterable will
  * invoke the iterator on each Iterable, in sequence, until all Iterators
  * are exhausted.
  *
  * @author Gail Badner (adapted from JoinedIterator)
  */
 public class JoinedIterable<T> implements Iterable<T> {
 	private final TypeSafeJoinedIterator<T> iterator;
 
 	public JoinedIterable(List<Iterable<T>> iterables) {
 		if ( iterables == null ) {
 			throw new NullPointerException( "Unexpected null iterables argument" );
 		}
 		iterator = new TypeSafeJoinedIterator<T>( iterables );
 	}
 
 	public Iterator<T> iterator() {
 		return iterator;
 	}
 
-	private class TypeSafeJoinedIterator<T> implements Iterator<T> {
+	private static class TypeSafeJoinedIterator<T> implements Iterator<T> {
 
 		// wrapped iterators
 		private List<Iterable<T>> iterables;
 
 		// index of current iterator in the wrapped iterators array
 		private int currentIterableIndex;
 
 		// the current iterator
 		private Iterator<T> currentIterator;
 
 		// the last used iterator
 		private Iterator<T> lastUsedIterator;
 
 		public TypeSafeJoinedIterator(List<Iterable<T>> iterables) {
 			this.iterables = iterables;
 		}
 
 		public boolean hasNext() {
 			updateCurrentIterator();
 			return currentIterator.hasNext();
 		}
 
 		public T next() {
 			updateCurrentIterator();
 			return currentIterator.next();
 		}
 
 		public void remove() {
 			updateCurrentIterator();
 			lastUsedIterator.remove();
 		}
 
 		// call this before any Iterator method to make sure that the current Iterator
 		// is not exhausted
 		@SuppressWarnings( {"unchecked"})
 		protected void updateCurrentIterator() {
 
 			if ( currentIterator == null) {
 				if( iterables.size() == 0  ) {
 					currentIterator = EmptyIterator.INSTANCE;
 				}
 				else {
 					currentIterator = iterables.get( 0 ).iterator();
 				}
 				// set last used iterator here, in case the user calls remove
 				// before calling hasNext() or next() (although they shouldn't)
 				lastUsedIterator = currentIterator;
 			}
 
 			while (! currentIterator.hasNext() && currentIterableIndex < iterables.size() - 1) {
 				currentIterableIndex++;
 				currentIterator = iterables.get( currentIterableIndex ).iterator();
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index d02a7c3eac..2bb1d2da1d 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,651 +1,651 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.lang.annotation.Annotation;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AttributeConverterDefinition;
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
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
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
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition attributeConverterDefinition;
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
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
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
 				attributeConverterDefinition = new AttributeConverterDefinition( converterClass.newInstance(), false );
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
 
 		if ( attributeConverterDefinition == null ) {
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
 
 		final Class entityAttributeJavaType = attributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = attributeConverterDefinition.getDatabaseColumnType();
 
 
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
 				attributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDefinition.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
 				attributeConverterDefinition.getAttributeConverter(),
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
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition attributeConverterDefinition) {
 		this.attributeConverterDefinition = attributeConverterDefinition;
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
 
-	private final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
+	private static final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaUpdateImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaUpdateImpl.java
index 714c1c9f25..33b4a6cf88 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaUpdateImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/CriteriaUpdateImpl.java
@@ -1,147 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.criteria;
 
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Expression;
 import javax.persistence.criteria.Path;
 import javax.persistence.criteria.Predicate;
 import javax.persistence.metamodel.SingularAttribute;
 
 import org.hibernate.jpa.criteria.compile.RenderingContext;
 import org.hibernate.jpa.criteria.path.SingularAttributePath;
 
 /**
  * Hibernate implementation of the JPA 2.1 {@link CriteriaUpdate} contract.
  *
  * @author Steve Ebersole
  */
 public class CriteriaUpdateImpl<T> extends AbstractManipulationCriteriaQuery<T> implements CriteriaUpdate<T> {
 	private List<Assignment> assignments = new ArrayList<Assignment>();
 
 	public CriteriaUpdateImpl(CriteriaBuilderImpl criteriaBuilder) {
 		super( criteriaBuilder );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <Y, X extends Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> singularAttribute, X value) {
 		final Path<Y> attributePath = getRoot().get( singularAttribute );
 		final Expression valueExpression = value == null
 				? criteriaBuilder().nullLiteral( attributePath.getJavaType() )
 				: criteriaBuilder().literal( value );
 		addAssignment( attributePath, valueExpression );
 		return this;
 	}
 
 	@Override
 	public <Y> CriteriaUpdate<T> set(
 			SingularAttribute<? super T, Y> singularAttribute,
 			Expression<? extends Y> value) {
 		addAssignment( getRoot().get( singularAttribute ), value );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <Y, X extends Y> CriteriaUpdate<T> set(Path<Y> attributePath, X value) {
 		final Expression valueExpression = value == null
 				? criteriaBuilder().nullLiteral( attributePath.getJavaType() )
 				: criteriaBuilder().literal( value );
 		addAssignment( attributePath, valueExpression );
 		return this;
 	}
 
 	@Override
 	public <Y> CriteriaUpdate<T> set(Path<Y> attributePath, Expression<? extends Y> value) {
 		addAssignment( attributePath, value );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public CriteriaUpdate<T> set(String attributeName, Object value) {
 		final Path attributePath = getRoot().get( attributeName );
 		final Expression valueExpression = value == null
 				? criteriaBuilder().nullLiteral( attributePath.getJavaType() )
 				: criteriaBuilder().literal( value );
 		addAssignment( attributePath, valueExpression );
 		return this;
 	}
 
 	protected <Y> void addAssignment(Path<Y> attributePath, Expression<? extends Y> value) {
 		if ( ! PathImplementor.class.isInstance( attributePath ) ) {
 			throw new IllegalArgumentException( "Unexpected path implementation type : " + attributePath.getClass().getName() );
 		}
 		if ( ! SingularAttributePath.class.isInstance( attributePath ) ) {
 			throw new IllegalArgumentException(
 					"Attribute path for assignment must represent a singular attribute ["
 							+ ( (PathImplementor) attributePath ).getPathIdentifier() + "]"
 			);
 		}
 		if ( value == null ) {
 			throw new IllegalArgumentException( "Assignment value expression cannot be null. Did you mean to pass null as a literal?" );
 		}
 		assignments.add( new Assignment<Y>( (SingularAttributePath<Y>) attributePath, value ) );
 	}
 
 	@Override
 	public CriteriaUpdate<T> where(Expression<Boolean> restriction) {
 		setRestriction( restriction );
 		return this;
 	}
 
 	@Override
 	public CriteriaUpdate<T> where(Predicate... restrictions) {
 		setRestriction( restrictions );
 		return this;
 	}
 
 	@Override
 	public void validate() {
 		super.validate();
 		if ( assignments.isEmpty() ) {
 			throw new IllegalStateException( "No assignments specified as part of UPDATE criteria" );
 		}
 	}
 
 	@Override
 	protected String renderQuery(RenderingContext renderingContext) {
 		final StringBuilder jpaql = new StringBuilder( "update " );
 		renderRoot( jpaql, renderingContext );
 		renderAssignments( jpaql, renderingContext );
 		renderRestrictions( jpaql, renderingContext );
 
 		return jpaql.toString();
 	}
 
 	private void renderAssignments(StringBuilder jpaql, RenderingContext renderingContext) {
 		jpaql.append( " set " );
 		boolean first = true;
 		for ( Assignment assignment : assignments ) {
 			if ( ! first ) {
 				jpaql.append( ", " );
 			}
 			jpaql.append( assignment.attributePath.render( renderingContext ) )
 					.append( " = " )
 					.append( assignment.value.render( renderingContext ) );
 			first = false;
 		}
 	}
 
-	private class Assignment<A> {
+	private static class Assignment<A> {
 		private final SingularAttributePath<A> attributePath;
 		private final ExpressionImplementor<? extends A> value;
 
 		private Assignment(SingularAttributePath<A> attributePath, Expression<? extends A> value) {
 			this.attributePath = attributePath;
 			this.value = (ExpressionImplementor) value;
 		}
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/mapper/relation/AbstractToOneMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/mapper/relation/AbstractToOneMapper.java
index 86576811d9..38b741a12e 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/mapper/relation/AbstractToOneMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/mapper/relation/AbstractToOneMapper.java
@@ -1,138 +1,138 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.envers.internal.entities.mapper.relation;
 
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.envers.boot.internal.EnversService;
 import org.hibernate.envers.internal.entities.EntityConfiguration;
 import org.hibernate.envers.internal.entities.PropertyData;
 import org.hibernate.envers.internal.entities.mapper.PersistentCollectionChangeData;
 import org.hibernate.envers.internal.entities.mapper.PropertyMapper;
 import org.hibernate.envers.internal.reader.AuditReaderImplementor;
 import org.hibernate.envers.internal.tools.ReflectionTools;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Base class for property mappers that manage to-one relation.
  *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public abstract class AbstractToOneMapper implements PropertyMapper {
 	private final ServiceRegistry serviceRegistry;
 	private final PropertyData propertyData;
 
 	protected AbstractToOneMapper(ServiceRegistry serviceRegistry, PropertyData propertyData) {
 		this.serviceRegistry = serviceRegistry;
 		this.propertyData = propertyData;
 	}
 
 	@Override
 	public boolean mapToMapFromEntity(
 			SessionImplementor session,
 			Map<String, Object> data,
 			Object newObj,
 			Object oldObj) {
 		return false;
 	}
 
 	@Override
 	public void mapToEntityFromMap(
 			EnversService enversService,
 			Object obj,
 			Map data,
 			Object primaryKey,
 			AuditReaderImplementor versionsReader,
 			Number revision) {
 		if ( obj != null ) {
 			nullSafeMapToEntityFromMap( enversService, obj, data, primaryKey, versionsReader, revision );
 		}
 	}
 
 	@Override
 	public List<PersistentCollectionChangeData> mapCollectionChanges(
 			SessionImplementor session,
 			String referencingPropertyName,
 			PersistentCollection newColl,
 			Serializable oldColl,
 			Serializable id) {
 		return null;
 	}
 
 	/**
 	 * @param enversService The EnversService
 	 * @param entityName Entity name.
 	 *
 	 * @return Entity class, name and information whether it is audited or not.
 	 */
 	protected EntityInfo getEntityInfo(EnversService enversService, String entityName) {
 		EntityConfiguration entCfg = enversService.getEntitiesConfigurations().get( entityName );
 		boolean isRelationAudited = true;
 		if ( entCfg == null ) {
 			// a relation marked as RelationTargetAuditMode.NOT_AUDITED
 			entCfg = enversService.getEntitiesConfigurations().getNotVersionEntityConfiguration( entityName );
 			isRelationAudited = false;
 		}
 		final Class entityClass = ReflectionTools.loadClass( entCfg.getEntityClassName(), enversService.getClassLoaderService() );
 		return new EntityInfo( entityClass, entityName, isRelationAudited );
 	}
 
 	protected void setPropertyValue(Object targetObject, Object value) {
 		final Setter setter = ReflectionTools.getSetter( targetObject.getClass(), propertyData, serviceRegistry );
 		setter.set( targetObject, value, null );
 	}
 
 	/**
 	 * @return Bean property that represents the relation.
 	 */
 	protected PropertyData getPropertyData() {
 		return propertyData;
 	}
 
 	/**
 	 * Parameter {@code obj} is never {@code null}.
 	 */
 	public abstract void nullSafeMapToEntityFromMap(
 			EnversService enversService,
 			Object obj,
 			Map data,
 			Object primaryKey,
 			AuditReaderImplementor versionsReader,
 			Number revision);
 
 	/**
 	 * Simple descriptor of an entity.
 	 */
-	protected class EntityInfo {
+	protected static class EntityInfo {
 		private final Class entityClass;
 		private final String entityName;
 		private final boolean audited;
 
 		public EntityInfo(Class entityClass, String entityName, boolean audited) {
 			this.entityClass = entityClass;
 			this.entityName = entityName;
 			this.audited = audited;
 		}
 
 		public Class getEntityClass() {
 			return entityClass;
 		}
 
 		public String getEntityName() {
 			return entityName;
 		}
 
 		public boolean isAudited() {
 			return audited;
 		}
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index 3ad697f413..62f4548560 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,468 +1,468 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.junit4;
 
 import java.io.InputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
 import org.hibernate.testing.cache.CachingRegionFactory;
 import org.junit.After;
 import org.junit.Before;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"deprecation"} )
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private Configuration configuration;
 	private StandardServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	protected Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
 	protected StandardServiceRegistryImpl serviceRegistry() {
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
 	protected void buildSessionFactory() {
 		// for now, build the configuration to get all the property settings
 		configuration = constructAndConfigureConfiguration();
 		BootstrapServiceRegistry bootRegistry = buildBootstrapServiceRegistry();
 		serviceRegistry = buildServiceRegistry( bootRegistry, configuration );
 		// this is done here because Configuration does not currently support 4.0 xsd
 		afterConstructAndConfigureConfiguration( configuration );
 		sessionFactory = ( SessionFactoryImplementor ) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 	protected void rebuildSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		try {
 			sessionFactory.close();
 			sessionFactory = null;
 			configuration = null;
 			serviceRegistry.destroy();
 			serviceRegistry = null;
 		}
 		catch (Exception ignore) {
 		}
 
 		buildSessionFactory();
 	}
 
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructAndConfigureConfiguration();
 		afterConstructAndConfigureConfiguration( cfg );
 		return cfg;
 	}
 
 	protected Configuration constructAndConfigureConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		return cfg;
 	}
 
 	private void afterConstructAndConfigureConfiguration(Configuration cfg) {
 		addMappings( cfg );
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration();
 		configuration.setProperty( AvailableSettings.CACHE_REGION_FACTORY, CachingRegionFactory.class.getName() );
 		configuration.setProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 			final String secondSchemaName = createSecondSchema();
 			if ( StringHelper.isNotEmpty( secondSchemaName ) ) {
 				if ( !( getDialect() instanceof H2Dialect ) ) {
 					throw new UnsupportedOperationException( "Only H2 dialect supports creation of second schema." );
 				}
 				Helper.createH2Schema( secondSchemaName, configuration );
 			}
 		}
 		configuration.setImplicitNamingStrategy( ImplicitNamingStrategyLegacyJpaImpl.INSTANCE );
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
 			configuration.setProperty( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY, getCacheConcurrencyStrategy() );
 			configuration.setSharedCacheMode( SharedCacheMode.ALL );
 		}
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 	}
 
 	protected BootstrapServiceRegistry buildBootstrapServiceRegistry() {
 		final BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
 		prepareBootstrapRegistryBuilder( builder );
 		return builder.build();
 	}
 
 	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 	}
 
 	protected StandardServiceRegistryImpl buildServiceRegistry(BootstrapServiceRegistry bootRegistry, Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 
 		StandardServiceRegistryBuilder cfgRegistryBuilder = configuration.getStandardServiceRegistryBuilder();
 
 		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder( bootRegistry, cfgRegistryBuilder.getAggregatedCfgXml() )
 				.applySettings( properties );
 
 		prepareBasicRegistryBuilder( registryBuilder );
 		return (StandardServiceRegistryImpl) registryBuilder.build();
 	}
 
 	protected void prepareBasicRegistryBuilder(StandardServiceRegistryBuilder serviceRegistryBuilder) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	/**
 	 * Feature supported only by H2 dialect.
 	 * @return Provide not empty name to create second schema.
 	 */
 	protected String createSecondSchema() {
 		return null;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
 		if ( serviceRegistry != null ) {
 			if ( serviceRegistry.isActive() ) {
 				try {
 					serviceRegistry.destroy();
 				}
 				catch (Exception ignore) {
 				}
 				fail( "StandardServiceRegistry was not closed down as expected" );
 			}
 		}
 		serviceRegistry=null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
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
 		completeStrayTransaction();
 
 		if ( isCleanupTestDataRequired() ) {
 			cleanupTestData();
 		}
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 
 	}
 
 	private void completeStrayTransaction() {
 		if ( session == null ) {
 			// nothing to do
 			return;
 		}
 
 		if ( ( (SessionImplementor) session ).isClosed() ) {
 			// nothing to do
 			return;
 		}
 
 		if ( !session.isConnected() ) {
 			// nothing to do
 			return;
 		}
 
 		final TransactionCoordinator.TransactionDriver tdc =
 				( (SessionImplementor) session ).getTransactionCoordinator().getTransactionDriverControl();
 
 		if ( tdc.getStatus().canRollback() ) {
 			session.getTransaction().rollback();
 		}
 	}
 
 	protected void cleanupCache() {
 		if ( sessionFactory != null ) {
 			sessionFactory.getCache().evictAllRegions();
 		}
 	}
 	
 	protected boolean isCleanupTestDataRequired() {
 		return false;
 	}
 	
 	protected void cleanupTestData() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete from java.lang.Object" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			session.close();
 		}
 		session = null;
 	}
 
-	public class RollbackWork implements Work {
+	public static class RollbackWork implements Work {
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
 						l = 0;
 					}
 					l = l + 1 ;
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
diff --git a/tooling/metamodel-generator/src/main/java/org/hibernate/jpamodelgen/JPAMetaModelEntityProcessor.java b/tooling/metamodel-generator/src/main/java/org/hibernate/jpamodelgen/JPAMetaModelEntityProcessor.java
index b427b31a41..19fd667611 100644
--- a/tooling/metamodel-generator/src/main/java/org/hibernate/jpamodelgen/JPAMetaModelEntityProcessor.java
+++ b/tooling/metamodel-generator/src/main/java/org/hibernate/jpamodelgen/JPAMetaModelEntityProcessor.java
@@ -1,323 +1,323 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpamodelgen;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import javax.annotation.processing.AbstractProcessor;
 import javax.annotation.processing.ProcessingEnvironment;
 import javax.annotation.processing.RoundEnvironment;
 import javax.annotation.processing.SupportedAnnotationTypes;
 import javax.annotation.processing.SupportedOptions;
 import javax.lang.model.SourceVersion;
 import javax.lang.model.element.AnnotationMirror;
 import javax.lang.model.element.Element;
 import javax.lang.model.element.ElementKind;
 import javax.lang.model.element.TypeElement;
 import javax.lang.model.type.DeclaredType;
 import javax.lang.model.type.ExecutableType;
 import javax.lang.model.type.TypeKind;
 import javax.lang.model.type.TypeMirror;
 import javax.lang.model.util.ElementFilter;
 import javax.lang.model.util.SimpleTypeVisitor6;
 import javax.tools.Diagnostic;
 
 import org.hibernate.jpamodelgen.annotation.AnnotationMetaEntity;
 import org.hibernate.jpamodelgen.model.MetaEntity;
 import org.hibernate.jpamodelgen.util.Constants;
 import org.hibernate.jpamodelgen.util.StringUtil;
 import org.hibernate.jpamodelgen.util.TypeUtils;
 import org.hibernate.jpamodelgen.xml.JpaDescriptorParser;
 
 /**
  * Main annotation processor.
  *
  * @author Max Andersen
  * @author Hardy Ferentschik
  * @author Emmanuel Bernard
  */
 @SupportedAnnotationTypes({
 		"javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embeddable"
 })
 @SupportedOptions({
 		JPAMetaModelEntityProcessor.DEBUG_OPTION,
 		JPAMetaModelEntityProcessor.PERSISTENCE_XML_OPTION,
 		JPAMetaModelEntityProcessor.ORM_XML_OPTION,
 		JPAMetaModelEntityProcessor.FULLY_ANNOTATION_CONFIGURED_OPTION,
 		JPAMetaModelEntityProcessor.LAZY_XML_PARSING,
 		JPAMetaModelEntityProcessor.ADD_GENERATION_DATE,
 		JPAMetaModelEntityProcessor.ADD_GENERATED_ANNOTATION,
 		JPAMetaModelEntityProcessor.ADD_SUPPRESS_WARNINGS_ANNOTATION
 })
 public class JPAMetaModelEntityProcessor extends AbstractProcessor {
 	public static final String DEBUG_OPTION = "debug";
 	public static final String PERSISTENCE_XML_OPTION = "persistenceXml";
 	public static final String ORM_XML_OPTION = "ormXml";
 	public static final String FULLY_ANNOTATION_CONFIGURED_OPTION = "fullyAnnotationConfigured";
 	public static final String LAZY_XML_PARSING = "lazyXmlParsing";
 	public static final String ADD_GENERATION_DATE = "addGenerationDate";
 	public static final String ADD_GENERATED_ANNOTATION = "addGeneratedAnnotation";
 	public static final String ADD_SUPPRESS_WARNINGS_ANNOTATION = "addSuppressWarningsAnnotation";
 
 	private static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;
 
 	private Context context;
 
 	@Override
 	public void init(ProcessingEnvironment env) {
 		super.init( env );
 		context = new Context( env );
 		context.logMessage(
 				Diagnostic.Kind.NOTE, "Hibernate JPA 2 Static-Metamodel Generator " + Version.getVersionString()
 		);
 
 		String tmp = env.getOptions().get( JPAMetaModelEntityProcessor.ADD_GENERATED_ANNOTATION );
 		if ( tmp != null ) {
 			boolean addGeneratedAnnotation = Boolean.parseBoolean( tmp );
 			context.setAddGeneratedAnnotation( addGeneratedAnnotation );
 		}
 
 		tmp = env.getOptions().get( JPAMetaModelEntityProcessor.ADD_GENERATION_DATE );
 		boolean addGenerationDate = Boolean.parseBoolean( tmp );
 		context.setAddGenerationDate( addGenerationDate );
 
 		tmp = env.getOptions().get( JPAMetaModelEntityProcessor.ADD_SUPPRESS_WARNINGS_ANNOTATION );
 		boolean addSuppressWarningsAnnotation = Boolean.parseBoolean( tmp );
 		context.setAddSuppressWarningsAnnotation( addSuppressWarningsAnnotation );
 
 		tmp = env.getOptions().get( JPAMetaModelEntityProcessor.FULLY_ANNOTATION_CONFIGURED_OPTION );
 		boolean fullyAnnotationConfigured = Boolean.parseBoolean( tmp );
 
 		if ( !fullyAnnotationConfigured ) {
 			JpaDescriptorParser parser = new JpaDescriptorParser( context );
 			parser.parseXml();
 			if ( context.isFullyXmlConfigured() ) {
 				createMetaModelClasses();
 			}
 		}
 	}
 
 	@Override
 	public SourceVersion getSupportedSourceVersion() {
 		return SourceVersion.latestSupported();
 	}
 
 	@Override
 	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {
 		// see also METAGEN-45
 		if ( roundEnvironment.processingOver() || annotations.size() == 0 ) {
 			return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
 		}
 
 		if ( context.isFullyXmlConfigured() ) {
 			context.logMessage(
 					Diagnostic.Kind.OTHER,
 					"Skipping the processing of annotations since persistence unit is purely xml configured."
 			);
 			return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
 		}
 
 		Set<? extends Element> elements = roundEnvironment.getRootElements();
 		for ( Element element : elements ) {
 			if ( isJPAEntity( element ) ) {
 				context.logMessage( Diagnostic.Kind.OTHER, "Processing annotated class " + element.toString() );
 				handleRootElementAnnotationMirrors( element );
 			}
 		}
 
 		createMetaModelClasses();
 		return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
 	}
 
 	private void createMetaModelClasses() {
 		for ( MetaEntity entity : context.getMetaEntities() ) {
 			if ( context.isAlreadyGenerated( entity.getQualifiedName() ) ) {
 				continue;
 			}
 			context.logMessage( Diagnostic.Kind.OTHER, "Writing meta model for entity " + entity );
 			ClassWriter.writeFile( entity, context );
 			context.markGenerated( entity.getQualifiedName() );
 		}
 
 		// we cannot process the delayed entities in any order. There might be dependencies between them.
 		// we need to process the top level entities first
 		Collection<MetaEntity> toProcessEntities = context.getMetaEmbeddables();
 		while ( !toProcessEntities.isEmpty() ) {
 			Set<MetaEntity> processedEntities = new HashSet<MetaEntity>();
 			int toProcessCountBeforeLoop = toProcessEntities.size();
 			for ( MetaEntity entity : toProcessEntities ) {
 				// see METAGEN-36
 				if ( context.isAlreadyGenerated( entity.getQualifiedName() ) ) {
 					processedEntities.add( entity );
 					continue;
 				}
 				if ( modelGenerationNeedsToBeDeferred( toProcessEntities, entity ) ) {
 					continue;
 				}
 				context.logMessage(
 						Diagnostic.Kind.OTHER, "Writing meta model for embeddable/mapped superclass" + entity
 				);
 				ClassWriter.writeFile( entity, context );
 				context.markGenerated( entity.getQualifiedName() );
 				processedEntities.add( entity );
 			}
 			toProcessEntities.removeAll( processedEntities );
 			if ( toProcessEntities.size() >= toProcessCountBeforeLoop ) {
 				context.logMessage(
 						Diagnostic.Kind.ERROR, "Potential endless loop in generation of entities."
 				);
 			}
 		}
 	}
 
 	private boolean modelGenerationNeedsToBeDeferred(Collection<MetaEntity> entities, MetaEntity containedEntity) {
 		ContainsAttributeTypeVisitor visitor = new ContainsAttributeTypeVisitor(
 				containedEntity.getTypeElement(), context
 		);
 		for ( MetaEntity entity : entities ) {
 			if ( entity.equals( containedEntity ) ) {
 				continue;
 			}
 			for ( Element subElement : ElementFilter.fieldsIn( entity.getTypeElement().getEnclosedElements() ) ) {
 				TypeMirror mirror = subElement.asType();
 				if ( !TypeKind.DECLARED.equals( mirror.getKind() ) ) {
 					continue;
 				}
 				boolean contains = mirror.accept( visitor, subElement );
 				if ( contains ) {
 					return true;
 				}
 			}
 			for ( Element subElement : ElementFilter.methodsIn( entity.getTypeElement().getEnclosedElements() ) ) {
 				TypeMirror mirror = subElement.asType();
 				if ( !TypeKind.DECLARED.equals( mirror.getKind() ) ) {
 					continue;
 				}
 				boolean contains = mirror.accept( visitor, subElement );
 				if ( contains ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private boolean isJPAEntity(Element element) {
 		return TypeUtils.containsAnnotation(
 				element,
 				Constants.ENTITY,
 				Constants.MAPPED_SUPERCLASS,
 				Constants.EMBEDDABLE
 		);
 	}
 
 	private void handleRootElementAnnotationMirrors(final Element element) {
 		List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
 		for ( AnnotationMirror mirror : annotationMirrors ) {
 			if ( !ElementKind.CLASS.equals( element.getKind() ) ) {
 				continue;
 			}
 
 			String fqn = ( (TypeElement) element ).getQualifiedName().toString();
 			MetaEntity alreadyExistingMetaEntity = tryGettingExistingEntityFromContext( mirror, fqn );
 			if ( alreadyExistingMetaEntity != null && alreadyExistingMetaEntity.isMetaComplete() ) {
 				String msg = "Skipping processing of annotations for " + fqn + " since xml configuration is metadata complete.";
 				context.logMessage( Diagnostic.Kind.OTHER, msg );
 				continue;
 			}
 
 			boolean requiresLazyMemberInitialization = false;
 			AnnotationMetaEntity metaEntity;
 			if ( TypeUtils.containsAnnotation( element, Constants.EMBEDDABLE ) ||
 					TypeUtils.containsAnnotation( element, Constants.MAPPED_SUPERCLASS ) ) {
 				requiresLazyMemberInitialization = true;
 			}
 
 			metaEntity = new AnnotationMetaEntity( (TypeElement) element, context, requiresLazyMemberInitialization );
 
 			if ( alreadyExistingMetaEntity != null ) {
 				metaEntity.mergeInMembers( alreadyExistingMetaEntity );
 			}
 			addMetaEntityToContext( mirror, metaEntity );
 		}
 	}
 
 	private MetaEntity tryGettingExistingEntityFromContext(AnnotationMirror mirror, String fqn) {
 		MetaEntity alreadyExistingMetaEntity = null;
 		if ( TypeUtils.isAnnotationMirrorOfType( mirror, Constants.ENTITY )
 				|| TypeUtils.isAnnotationMirrorOfType( mirror, Constants.MAPPED_SUPERCLASS )) {
 			alreadyExistingMetaEntity = context.getMetaEntity( fqn );
 		}
 		else if ( TypeUtils.isAnnotationMirrorOfType( mirror, Constants.EMBEDDABLE ) ) {
 			alreadyExistingMetaEntity = context.getMetaEmbeddable( fqn );
 		}
 		return alreadyExistingMetaEntity;
 	}
 
 	private void addMetaEntityToContext(AnnotationMirror mirror, AnnotationMetaEntity metaEntity) {
 		if ( TypeUtils.isAnnotationMirrorOfType( mirror, Constants.ENTITY ) ) {
 			context.addMetaEntity( metaEntity.getQualifiedName(), metaEntity );
 		}
 		else if ( TypeUtils.isAnnotationMirrorOfType( mirror, Constants.MAPPED_SUPERCLASS ) ) {
 			context.addMetaEntity( metaEntity.getQualifiedName(), metaEntity );
 		}
 		else if ( TypeUtils.isAnnotationMirrorOfType( mirror, Constants.EMBEDDABLE ) ) {
 			context.addMetaEmbeddable( metaEntity.getQualifiedName(), metaEntity );
 		}
 	}
 
 
-	class ContainsAttributeTypeVisitor extends SimpleTypeVisitor6<Boolean, Element> {
+	static class ContainsAttributeTypeVisitor extends SimpleTypeVisitor6<Boolean, Element> {
 
 		private Context context;
 		private TypeElement type;
 
 		ContainsAttributeTypeVisitor(TypeElement elem, Context context) {
 			this.context = context;
 			this.type = elem;
 		}
 
 		@Override
 		public Boolean visitDeclared(DeclaredType declaredType, Element element) {
 			TypeElement returnedElement = (TypeElement) context.getTypeUtils().asElement( declaredType );
 
 			String fqNameOfReturnType = returnedElement.getQualifiedName().toString();
 			String collection = Constants.COLLECTIONS.get( fqNameOfReturnType );
 			if ( collection != null ) {
 				TypeMirror collectionElementType = TypeUtils.getCollectionElementType(
 						declaredType, fqNameOfReturnType, null, context
 				);
 				returnedElement = (TypeElement) context.getTypeUtils().asElement( collectionElementType );
 			}
 
 			if ( type.getQualifiedName().toString().equals( returnedElement.getQualifiedName().toString() ) ) {
 				return Boolean.TRUE;
 			}
 			else {
 				return Boolean.FALSE;
 			}
 		}
 
 		@Override
 		public Boolean visitExecutable(ExecutableType t, Element element) {
 			if ( !element.getKind().equals( ElementKind.METHOD ) ) {
 				return Boolean.FALSE;
 			}
 
 			String string = element.getSimpleName().toString();
 			if ( !StringUtil.isProperty( string, TypeUtils.toTypeString( t.getReturnType() ) ) ) {
 				return Boolean.FALSE;
 			}
 
 			TypeMirror returnType = t.getReturnType();
 			return returnType.accept( this, element );
 		}
 	}
 }
