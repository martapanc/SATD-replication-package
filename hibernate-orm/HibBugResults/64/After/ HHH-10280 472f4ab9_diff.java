diff --git a/hibernate-core/src/main/java/org/hibernate/Hibernate.java b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
index 3b8170a61c..178de31f91 100644
--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
@@ -1,190 +1,183 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate;
 
 import java.util.Iterator;
 
-import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * <ul>
  * <li>Provides access to the full range of Hibernate built-in types. <tt>Type</tt>
  * instances may be used to bind values to query parameters.
  * <li>A factory for new <tt>Blob</tt>s and <tt>Clob</tt>s.
  * <li>Defines static methods for manipulation of proxies.
  * </ul>
  *
  * @author Gavin King
  * @see java.sql.Clob
  * @see java.sql.Blob
  * @see org.hibernate.type.Type
  */
 
 public final class Hibernate {
 	/**
 	 * Cannot be instantiated.
 	 */
 	private Hibernate() {
 		throw new UnsupportedOperationException();
 	}
 
 
 	/**
 	 * Force initialization of a proxy or persistent collection.
 	 * <p/>
 	 * Note: This only ensures intialization of a proxy object or collection;
 	 * it is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
 	 *
 	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
 	 * @throws HibernateException if we can't initialize the proxy at this time, eg. the <tt>Session</tt> was closed
 	 */
 	public static void initialize(Object proxy) throws HibernateException {
 		if ( proxy == null ) {
 			return;
 		}
 
 		if ( proxy instanceof HibernateProxy ) {
 			( (HibernateProxy) proxy ).getHibernateLazyInitializer().initialize();
 		}
 		else if ( proxy instanceof PersistentCollection ) {
 			( (PersistentCollection) proxy ).forceInitialization();
 		}
 	}
 
 	/**
 	 * Check if the proxy or persistent collection is initialized.
 	 *
 	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
 	 * @return true if the argument is already initialized, or is not a proxy or collection
 	 */
 	@SuppressWarnings("SimplifiableIfStatement")
 	public static boolean isInitialized(Object proxy) {
 		if ( proxy instanceof HibernateProxy ) {
 			return !( (HibernateProxy) proxy ).getHibernateLazyInitializer().isUninitialized();
 		}
 		else if ( proxy instanceof PersistentCollection ) {
 			return ( (PersistentCollection) proxy ).wasInitialized();
 		}
 		else {
 			return true;
 		}
 	}
 
 	/**
 	 * Get the true, underlying class of a proxied persistent class. This operation
 	 * will initialize a proxy by side-effect.
 	 *
 	 * @param proxy a persistable object or proxy
 	 * @return the true class of the instance
 	 * @throws HibernateException
 	 */
 	public static Class getClass(Object proxy) {
 		if ( proxy instanceof HibernateProxy ) {
 			return ( (HibernateProxy) proxy ).getHibernateLazyInitializer()
 					.getImplementation()
 					.getClass();
 		}
 		else {
 			return proxy.getClass();
 		}
 	}
 
 	/**
 	 * Obtain a lob creator for the given session.
 	 *
 	 * @param session The session for which to obtain a lob creator
 	 *
 	 * @return The log creator reference
 	 */
 	public static LobCreator getLobCreator(Session session) {
 		return getLobCreator( (SessionImplementor) session );
 	}
 
 	/**
 	 * Obtain a lob creator for the given session.
 	 *
 	 * @param session The session for which to obtain a lob creator
 	 *
 	 * @return The log creator reference
 	 */
 	public static LobCreator getLobCreator(SessionImplementor session) {
 		return session.getFactory()
 				.getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getLobCreator( session );
 	}
 
 	/**
 	 * Close an {@link Iterator} instances obtained from {@link org.hibernate.Query#iterate()} immediately
 	 * instead of waiting until the session is closed or disconnected.
 	 *
 	 * @param iterator an Iterator created by iterate()
 	 *
 	 * @throws HibernateException Indicates a problem closing the Hibernate iterator.
 	 * @throws IllegalArgumentException If the Iterator is not a "Hibernate Iterator".
 	 *
 	 * @see Query#iterate()
 	 */
 	public static void close(Iterator iterator) throws HibernateException {
 		if ( iterator instanceof HibernateIterator ) {
 			( (HibernateIterator) iterator ).close();
 		}
 		else {
 			throw new IllegalArgumentException( "not a Hibernate iterator" );
 		}
 	}
 
 	/**
 	 * Check if the property is initialized. If the named property does not exist
 	 * or is not persistent, this method always returns <tt>true</tt>.
 	 *
 	 * @param proxy The potential proxy
 	 * @param propertyName the name of a persistent attribute of the object
 	 * @return true if the named property of the object is not listed as uninitialized; false otherwise
 	 */
 	public static boolean isPropertyInitialized(Object proxy, String propertyName) {
 		final Object entity;
 		if ( proxy instanceof HibernateProxy ) {
 			final LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				return false;
 			}
 			else {
 				entity = li.getImplementation();
 			}
 		}
 		else {
 			entity = proxy;
 		}
 
 		if ( entity instanceof PersistentAttributeInterceptable ) {
 			PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) entity ).$$_hibernate_getInterceptor();
-			if ( interceptor != null && interceptor instanceof LazyAttributeLoader ) {
-				return ( (LazyAttributeLoader) interceptor ).isAttributeLoaded( propertyName );
+			if ( interceptor != null && interceptor instanceof LazyAttributeLoadingInterceptor ) {
+				return ( (LazyAttributeLoadingInterceptor) interceptor ).isAttributeLoaded( propertyName );
 			}
 		}
 
-		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
-			final FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
-			return interceptor == null || interceptor.isInitialized( propertyName );
-		}
-
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
deleted file mode 100644
index 72573c358d..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
+++ /dev/null
@@ -1,93 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.internal;
-
-import java.io.ByteArrayInputStream;
-import java.io.DataInputStream;
-import java.io.IOException;
-import java.util.Set;
-
-import javassist.bytecode.ClassFile;
-
-import org.hibernate.bytecode.buildtime.spi.AbstractInstrumenter;
-import org.hibernate.bytecode.buildtime.spi.BasicClassFilter;
-import org.hibernate.bytecode.buildtime.spi.ClassDescriptor;
-import org.hibernate.bytecode.buildtime.spi.Logger;
-import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
-import org.hibernate.bytecode.internal.javassist.FieldHandled;
-import org.hibernate.bytecode.spi.ClassTransformer;
-import org.hibernate.internal.log.DeprecationLogger;
-
-/**
- * Strategy for performing build-time instrumentation of persistent classes in order to enable
- * field-level interception using Javassist.
- *
- * @author Steve Ebersole
- * @author Muga Nishizawa
- */
-public class JavassistInstrumenter extends AbstractInstrumenter {
-
-	private static final BasicClassFilter CLASS_FILTER = new BasicClassFilter();
-
-	private final BytecodeProviderImpl provider = new BytecodeProviderImpl();
-
-	/**
-	 * Constructs the Javassist-based instrumenter.
-	 *
-	 * @param logger Logger to use
-	 * @param options Instrumentation options
-	 */
-	public JavassistInstrumenter(Logger logger, Options options) {
-		super( logger, options );
-		DeprecationLogger.DEPRECATION_LOGGER.logDeprecatedBytecodeEnhancement();
-	}
-
-	@Override
-	protected ClassDescriptor getClassDescriptor(byte[] bytecode) throws IOException {
-		return new CustomClassDescriptor( bytecode );
-	}
-
-	@Override
-	protected ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames) {
-		if ( descriptor.isInstrumented() ) {
-			logger.debug( "class [" + descriptor.getName() + "] already instrumented" );
-			return null;
-		}
-		else {
-			return provider.getTransformer( CLASS_FILTER, new CustomFieldFilter( descriptor, classNames ) );
-		}
-	}
-
-	private static class CustomClassDescriptor implements ClassDescriptor {
-		private final byte[] bytes;
-		private final ClassFile classFile;
-
-		public CustomClassDescriptor(byte[] bytes) throws IOException {
-			this.bytes = bytes;
-			this.classFile = new ClassFile( new DataInputStream( new ByteArrayInputStream( bytes ) ) );
-		}
-
-		public String getName() {
-			return classFile.getName();
-		}
-
-		public boolean isInstrumented() {
-			final String[] interfaceNames = classFile.getInterfaces();
-			for ( String interfaceName : interfaceNames ) {
-				if ( FieldHandled.class.getName().equals( interfaceName ) ) {
-					return true;
-				}
-			}
-			return false;
-		}
-
-		public byte[] getBytes() {
-			return bytes;
-		}
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/package-info.java
deleted file mode 100644
index 4cd928eec6..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/package-info.java
+++ /dev/null
@@ -1,11 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-/**
- * Javassist support internals
- */
-package org.hibernate.bytecode.buildtime.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
deleted file mode 100644
index c325444dfd..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
+++ /dev/null
@@ -1,421 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-import java.io.ByteArrayInputStream;
-import java.io.DataInputStream;
-import java.io.File;
-import java.io.FileInputStream;
-import java.io.FileOutputStream;
-import java.io.IOException;
-import java.io.OutputStream;
-import java.util.HashSet;
-import java.util.Set;
-import java.util.zip.CRC32;
-import java.util.zip.ZipEntry;
-import java.util.zip.ZipInputStream;
-import java.util.zip.ZipOutputStream;
-
-import org.hibernate.bytecode.spi.ByteCodeHelper;
-import org.hibernate.bytecode.spi.ClassTransformer;
-
-/**
- * Provides the basic templating of how instrumentation should occur.
- *
- * @author Steve Ebersole
- */
-public abstract class AbstractInstrumenter implements Instrumenter {
-	private static final int ZIP_MAGIC = 0x504B0304;
-	private static final int CLASS_MAGIC = 0xCAFEBABE;
-
-	protected final Logger logger;
-	protected final Options options;
-
-	/**
-	 * Creates the basic instrumentation strategy.
-	 *
-	 * @param logger The bridge to the environment's logging system.
-	 * @param options User-supplied options.
-	 */
-	public AbstractInstrumenter(Logger logger, Options options) {
-		this.logger = logger;
-		this.options = options;
-	}
-
-	/**
-	 * Given the bytecode of a java class, retrieve the descriptor for that class.
-	 *
-	 * @param byecode The class bytecode.
-	 *
-	 * @return The class's descriptor
-	 *
-	 * @throws Exception Indicates problems access the bytecode.
-	 */
-	protected abstract ClassDescriptor getClassDescriptor(byte[] byecode) throws Exception;
-
-	/**
-	 * Create class transformer for the class.
-	 *
-	 * @param descriptor The descriptor of the class to be instrumented.
-	 * @param classNames The names of all classes to be instrumented; the "pipeline" if you will.
-	 *
-	 * @return The transformer for the given class; may return null to indicate that transformation should
-	 * be skipped (ala already instrumented).
-	 */
-	protected abstract ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames);
-
-	/**
-	 * The main instrumentation entry point.  Given a set of files, perform instrumentation on each discovered class
-	 * file.
-	 *
-	 * @param files The files.
-	 */
-	public void execute(Set<File> files) {
-		final Set<String> classNames = new HashSet<String>();
-
-		if ( options.performExtendedInstrumentation() ) {
-			logger.debug( "collecting class names for extended instrumentation determination" );
-			try {
-				for ( Object file1 : files ) {
-					final File file = (File) file1;
-					collectClassNames( file, classNames );
-				}
-			}
-			catch ( ExecutionException ee ) {
-				throw ee;
-			}
-			catch ( Exception e ) {
-				throw new ExecutionException( e );
-			}
-		}
-
-		logger.info( "starting instrumentation" );
-		try {
-			for ( File file : files ) {
-				processFile( file, classNames );
-			}
-		}
-		catch ( ExecutionException ee ) {
-			throw ee;
-		}
-		catch ( Exception e ) {
-			throw new ExecutionException( e );
-		}
-	}
-
-	/**
-	 * Extract the names of classes from file, adding them to the classNames collection.
-	 * <p/>
-	 * IMPL NOTE : file here may be either a class file or a jar.  If a jar, all entries in the jar file are
-	 * processed.
-	 *
-	 * @param file The file from which to extract class metadata (descriptor).
-	 * @param classNames The collected class name collection.
-	 *
-	 * @throws Exception indicates problems accessing the file or its contents.
-	 */
-	private void collectClassNames(File file, final Set<String> classNames) throws Exception {
-		if ( isClassFile( file ) ) {
-			final byte[] bytes = ByteCodeHelper.readByteCode( file );
-			final ClassDescriptor descriptor = getClassDescriptor( bytes );
-			classNames.add( descriptor.getName() );
-		}
-		else if ( isJarFile( file ) ) {
-			final ZipEntryHandler collector = new ZipEntryHandler() {
-				public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
-					if ( !entry.isDirectory() ) {
-						// see if the entry represents a class file
-						final DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
-						if ( din.readInt() == CLASS_MAGIC ) {
-							classNames.add( getClassDescriptor( byteCode ).getName() );
-						}
-					}
-				}
-			};
-			final ZipFileProcessor processor = new ZipFileProcessor( collector );
-			processor.process( file );
-		}
-	}
-
-	/**
-	 * Does this file represent a compiled class?
-	 *
-	 * @param file The file to check.
-	 *
-	 * @return True if the file is a class; false otherwise.
-	 *
-	 * @throws IOException Indicates problem access the file.
-	 */
-	protected final boolean isClassFile(File file) throws IOException {
-		return checkMagic( file, CLASS_MAGIC );
-	}
-
-	/**
-	 * Does this file represent a zip file of some format?
-	 *
-	 * @param file The file to check.
-	 *
-	 * @return True if the file is n archive; false otherwise.
-	 *
-	 * @throws IOException Indicates problem access the file.
-	 */
-	protected final boolean isJarFile(File file) throws IOException {
-		return checkMagic( file, ZIP_MAGIC );
-	}
-
-	protected final boolean checkMagic(File file, long magic) throws IOException {
-		final DataInputStream in = new DataInputStream( new FileInputStream( file ) );
-		try {
-			final int m = in.readInt();
-			return magic == m;
-		}
-		finally {
-			in.close();
-		}
-	}
-
-	/**
-	 * Actually process the file by applying instrumentation transformations to any classes it contains.
-	 * <p/>
-	 * Again, just like with {@link #collectClassNames} this method can handle both class and archive files.
-	 *
-	 * @param file The file to process.
-	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
-	 * specifies to perform {@link org.hibernate.bytecode.buildtime.spi.Instrumenter.Options#performExtendedInstrumentation() extended} instrumentation.
-	 *
-	 * @throws Exception Indicates an issue either access files or applying the transformations.
-	 */
-	protected void processFile(File file, Set<String> classNames) throws Exception {
-		if ( isClassFile( file ) ) {
-			logger.debug( "processing class file : " + file.getAbsolutePath() );
-			processClassFile( file, classNames );
-		}
-		else if ( isJarFile( file ) ) {
-			logger.debug( "processing jar file : " + file.getAbsolutePath() );
-			processJarFile( file, classNames );
-		}
-		else {
-			logger.debug( "ignoring file : " + file.getAbsolutePath() );
-		}
-	}
-
-	/**
-	 * Process a class file.  Delegated to from {@link #processFile} in the case of a class file.
-	 *
-	 * @param file The class file to process.
-	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
-	 * specifies to perform {@link org.hibernate.bytecode.buildtime.spi.Instrumenter.Options#performExtendedInstrumentation() extended} instrumentation.
-	 *
-	 * @throws Exception Indicates an issue either access files or applying the transformations.
-	 */
-	protected void processClassFile(File file, Set<String> classNames) throws Exception {
-		final byte[] bytes = ByteCodeHelper.readByteCode( file );
-		final ClassDescriptor descriptor = getClassDescriptor( bytes );
-		final ClassTransformer transformer = getClassTransformer( descriptor, classNames );
-		if ( transformer == null ) {
-			logger.debug( "no trasformer for class file : " + file.getAbsolutePath() );
-			return;
-		}
-
-		logger.info( "processing class : " + descriptor.getName() + ";  file = " + file.getAbsolutePath() );
-		final byte[] transformedBytes = transformer.transform(
-				getClass().getClassLoader(),
-				descriptor.getName(),
-				null,
-				null,
-				descriptor.getBytes()
-		);
-
-		final OutputStream out = new FileOutputStream( file );
-		try {
-			out.write( transformedBytes );
-			out.flush();
-		}
-		finally {
-			try {
-				out.close();
-			}
-			catch ( IOException ignore) {
-				// intentionally empty
-			}
-		}
-	}
-
-	/**
-	 * Process an archive file.  Delegated to from {@link #processFile} in the case of an archive file.
-	 *
-	 * @param file The archive file to process.
-	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
-	 * specifies to perform {@link org.hibernate.bytecode.buildtime.spi.Instrumenter.Options#performExtendedInstrumentation() extended} instrumentation.
-	 *
-	 * @throws Exception Indicates an issue either access files or applying the transformations.
-	 */
-	protected void processJarFile(final File file, final Set<String> classNames) throws Exception {
-		final File tempFile = File.createTempFile(
-				file.getName(),
-				null,
-				new File( file.getAbsoluteFile().getParent() )
-		);
-
-		try {
-			final FileOutputStream fout = new FileOutputStream( tempFile, false );
-			try {
-				final ZipOutputStream out = new ZipOutputStream( fout );
-				final ZipEntryHandler transformer = new ZipEntryHandler() {
-					public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
-						logger.debug( "starting zip entry : " + entry.toString() );
-						if ( !entry.isDirectory() ) {
-							// see if the entry represents a class file
-							final DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
-							if ( din.readInt() == CLASS_MAGIC ) {
-								final ClassDescriptor descriptor = getClassDescriptor( byteCode );
-								final ClassTransformer transformer = getClassTransformer( descriptor, classNames );
-								if ( transformer == null ) {
-									logger.debug( "no transformer for zip entry :  " + entry.toString() );
-								}
-								else {
-									logger.info( "processing class : " + descriptor.getName() + ";  entry = " + file.getAbsolutePath() );
-									byteCode = transformer.transform(
-											getClass().getClassLoader(),
-											descriptor.getName(),
-											null,
-											null,
-											descriptor.getBytes()
-									);
-								}
-							}
-							else {
-								logger.debug( "ignoring zip entry : " + entry.toString() );
-							}
-						}
-
-						final ZipEntry outEntry = new ZipEntry( entry.getName() );
-						outEntry.setMethod( entry.getMethod() );
-						outEntry.setComment( entry.getComment() );
-						outEntry.setSize( byteCode.length );
-
-						if ( outEntry.getMethod() == ZipEntry.STORED ){
-							final CRC32 crc = new CRC32();
-							crc.update( byteCode );
-							outEntry.setCrc( crc.getValue() );
-							outEntry.setCompressedSize( byteCode.length );
-						}
-						out.putNextEntry( outEntry );
-						out.write( byteCode );
-						out.closeEntry();
-					}
-				};
-
-				final ZipFileProcessor processor = new ZipFileProcessor( transformer );
-				processor.process( file );
-				out.close();
-			}
-			finally{
-				fout.close();
-			}
-
-			if ( file.delete() ) {
-				final File newFile = new File( tempFile.getAbsolutePath() );
-				if( ! newFile.renameTo( file ) ) {
-					throw new IOException( "can not rename " + tempFile + " to " + file );
-				}
-			}
-			else {
-				throw new IOException( "can not delete " + file );
-			}
-		}
-		finally {
-			if ( ! tempFile.delete() ) {
-				logger.info( "Unable to cleanup temporary jar file : " + tempFile.getAbsolutePath() );
-			}
-		}
-	}
-
-	/**
-	 * Allows control over what exactly to transform.
-	 */
-	protected class CustomFieldFilter implements FieldFilter {
-		private final ClassDescriptor descriptor;
-		private final Set classNames;
-
-		public CustomFieldFilter(ClassDescriptor descriptor, Set classNames) {
-			this.descriptor = descriptor;
-			this.classNames = classNames;
-		}
-
-		public boolean shouldInstrumentField(String className, String fieldName) {
-			if ( descriptor.getName().equals( className ) ) {
-				logger.trace( "accepting transformation of field [" + className + "." + fieldName + "]" );
-				return true;
-			}
-			else {
-				logger.trace( "rejecting transformation of field [" + className + "." + fieldName + "]" );
-				return false;
-			}
-		}
-
-		public boolean shouldTransformFieldAccess(
-				String transformingClassName,
-				String fieldOwnerClassName,
-				String fieldName) {
-			if ( descriptor.getName().equals( fieldOwnerClassName ) ) {
-				logger.trace( "accepting transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]" );
-				return true;
-			}
-			else if ( options.performExtendedInstrumentation() && classNames.contains( fieldOwnerClassName ) ) {
-				logger.trace( "accepting extended transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]" );
-				return true;
-			}
-			else {
-				logger.trace( "rejecting transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]; caller = " + transformingClassName  );
-				return false;
-			}
-		}
-	}
-
-	/**
-	 * General strategy contract for handling entries in an archive file.
-	 */
-	private static interface ZipEntryHandler {
-		/**
-		 * Apply strategy to the given archive entry.
-		 *
-		 * @param entry The archive file entry.
-		 * @param byteCode The bytes making up the entry
-		 *
-		 * @throws Exception Problem handling entry
-		 */
-		public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception;
-	}
-
-	/**
-	 * Applies {@link ZipEntryHandler} strategies to the entries of an archive file.
-	 */
-	private static class ZipFileProcessor {
-		private final ZipEntryHandler entryHandler;
-
-		public ZipFileProcessor(ZipEntryHandler entryHandler) {
-			this.entryHandler = entryHandler;
-		}
-
-		public void process(File file) throws Exception {
-			final ZipInputStream zip = new ZipInputStream( new FileInputStream( file ) );
-
-			try {
-				ZipEntry entry;
-				while ( (entry = zip.getNextEntry()) != null ) {
-					final byte[] bytes = ByteCodeHelper.readByteCode( zip );
-					entryHandler.handleEntry( entry, bytes );
-					zip.closeEntry();
-				}
-			}
-			finally {
-				zip.close();
-			}
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
deleted file mode 100644
index 3d855cdcf1..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
+++ /dev/null
@@ -1,65 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-import java.util.Arrays;
-import java.util.HashSet;
-import java.util.Set;
-
-/**
- * BasicClassFilter provides class filtering based on a series of packages to
- * be included and/or a series of explicit class names to be included.  If
- * neither is specified, then no restrictions are applied.
- *
- * @author Steve Ebersole
- */
-public class BasicClassFilter implements ClassFilter {
-	private final String[] includedPackages;
-	private final Set<String> includedClassNames = new HashSet<String>();
-	private final boolean isAllEmpty;
-
-	/**
-	 * Constructs a BasicClassFilter with given configuration.
-	 */
-	public BasicClassFilter() {
-		this( null, null );
-	}
-
-	/**
-	 * Constructs a BasicClassFilter with standard set of configuration.
-	 *
-	 * @param includedPackages Name of packages whose classes should be accepted.
-	 * @param includedClassNames Name of classes that should be accepted.
-	 */
-	public BasicClassFilter(String[] includedPackages, String[] includedClassNames) {
-		this.includedPackages = includedPackages;
-		if ( includedClassNames != null ) {
-			this.includedClassNames.addAll( Arrays.asList( includedClassNames ) );
-		}
-
-		isAllEmpty = ( this.includedPackages == null || this.includedPackages.length == 0 )
-				&& ( this.includedClassNames.isEmpty() );
-	}
-
-	@Override
-	public boolean shouldInstrumentClass(String className) {
-		return isAllEmpty ||
-				includedClassNames.contains( className ) ||
-				isInIncludedPackage( className );
-	}
-
-	private boolean isInIncludedPackage(String className) {
-		if ( includedPackages != null ) {
-			for ( String includedPackage : includedPackages ) {
-				if ( className.startsWith( includedPackage ) ) {
-					return true;
-				}
-			}
-		}
-		return false;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassDescriptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassDescriptor.java
deleted file mode 100644
index 4ab310d6e0..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassDescriptor.java
+++ /dev/null
@@ -1,36 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-/**
- * Contract describing the information Hibernate needs in terms of instrumenting
- * a class, either via ant task or dynamic classloader.
- *
- * @author Steve Ebersole
- */
-public interface ClassDescriptor {
-	/**
-	 * The name of the class.
-	 *
-	 * @return The class name.
-	 */
-	public String getName();
-
-	/**
-	 * Determine if the class is already instrumented.
-	 *
-	 * @return True if already instrumented; false otherwise.
-	 */
-	public boolean isInstrumented();
-
-	/**
-	 * The bytes making up the class' bytecode.
-	 *
-	 * @return The bytecode bytes.
-	 */
-	public byte[] getBytes();
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
deleted file mode 100644
index a3882abb05..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
+++ /dev/null
@@ -1,23 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-/**
- * Used to determine whether a class should be instrumented.
- *
- * @author Steve Ebersole
- */
-public interface ClassFilter {
-	/**
-	 * Should this class be included in instrumentation.
-	 *
-	 * @param className The name of the class to check
-	 *
-	 * @return {@literal true} to include class in instrumentation; {@literal false} otherwise.
-	 */
-	public boolean shouldInstrumentClass(String className);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
deleted file mode 100644
index 871560e771..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
+++ /dev/null
@@ -1,43 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-/**
- * Indicates problem performing the instrumentation execution.
- *
- * @author Steve Ebersole
- */
-@SuppressWarnings( {"UnusedDeclaration"})
-public class ExecutionException extends RuntimeException {
-	/**
-	 * Constructs an ExecutionException.
-	 *
-	 * @param message The message explaining the exception condition
-	 */
-	public ExecutionException(String message) {
-		super( message );
-	}
-
-	/**
-	 * Constructs an ExecutionException.
-	 *
-	 * @param cause The underlying cause.
-	 */
-	public ExecutionException(Throwable cause) {
-		super( cause );
-	}
-
-	/**
-	 * Constructs an ExecutionException.
-	 *
-	 * @param message The message explaining the exception condition
-	 * @param cause The underlying cause.
-	 */
-	public ExecutionException(String message, Throwable cause) {
-		super( message, cause );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/FieldFilter.java
deleted file mode 100644
index e8568d72b8..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/FieldFilter.java
+++ /dev/null
@@ -1,35 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-/**
- * Used to determine whether a field reference should be instrumented.
- *
- * @author Steve Ebersole
- */
-public interface FieldFilter {
-	/**
-	 * Should this field definition be instrumented?
-	 *
-	 * @param className The name of the class currently being processed
-	 * @param fieldName The name of the field being checked.
-	 * @return True if we should instrument this field.
-	 */
-	public boolean shouldInstrumentField(String className, String fieldName);
-
-	/**
-	 * Should we instrument *access to* the given field.  This differs from
-	 * {@link #shouldInstrumentField} in that here we are talking about a particular usage of
-	 * a field.
-	 *
-	 * @param transformingClassName The class currently being transformed.
-	 * @param fieldOwnerClassName The name of the class owning this field being checked.
-	 * @param fieldName The name of the field being checked.
-	 * @return True if this access should be transformed.
-	 */
-	public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
deleted file mode 100644
index 81758df20d..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
+++ /dev/null
@@ -1,36 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-import java.io.File;
-import java.util.Set;
-
-/**
- * Basic contract for performing instrumentation.
- *
- * @author Steve Ebersole
- */
-public interface Instrumenter {
-	/**
-	 * Perform the instrumentation.
-	 *
-	 * @param files The file on which to perform instrumentation
-	 */
-	public void execute(Set<File> files);
-
-	/**
-	 * Instrumentation options.
-	 */
-	public static interface Options {
-		/**
-		 * Should we enhance references to class fields outside the class itself?
-		 *
-		 * @return {@literal true}/{@literal false}
-		 */
-		public boolean performExtendedInstrumentation();
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
deleted file mode 100644
index d5b743ed9a..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
+++ /dev/null
@@ -1,50 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.buildtime.spi;
-
-/**
- * Provides an abstraction for how instrumentation does logging because it is usually run in environments (Ant/Maven)
- * with their own logging infrastructure.  This abstraction allows proper bridging.
- *
- * @author Steve Ebersole
- */
-public interface Logger {
-	/**
-	 * Log a message with TRACE semantics.
-	 *
-	 * @param message The message to log.
-	 */
-	public void trace(String message);
-
-	/**
-	 * Log a message with DEBUG semantics.
-	 *
-	 * @param message The message to log.
-	 */
-	public void debug(String message);
-
-	/**
-	 * Log a message with INFO semantics.
-	 *
-	 * @param message The message to log.
-	 */
-	public void info(String message);
-
-	/**
-	 * Log a message with WARN semantics.
-	 *
-	 * @param message The message to log.
-	 */
-	public void warn(String message);
-
-	/**
-	 * Log a message with ERROR semantics.
-	 *
-	 * @param message The message to log.
-	 */
-	public void error(String message);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/package-info.java
deleted file mode 100644
index 5346ae9c8f..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/package-info.java
+++ /dev/null
@@ -1,13 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-/**
- * Package defining build-time bytecode code enhancement (instrumentation) support.
- *
- * This package should mostly be considered deprecated in favor of {@link org.hibernate.bytecode.enhance}
- */
-package org.hibernate.bytecode.buildtime.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/internal/EntityEnhancer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/internal/EntityEnhancer.java
index 8a4b47b414..01ac314c6b 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/internal/EntityEnhancer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/internal/EntityEnhancer.java
@@ -1,356 +1,356 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.enhance.internal;
 
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import javassist.CannotCompileException;
 import javassist.CtClass;
 import javassist.CtField;
 import javassist.Modifier;
 import javassist.NotFoundException;
 
 import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;
 import org.hibernate.bytecode.enhance.internal.tracker.SimpleCollectionTracker;
 import org.hibernate.bytecode.enhance.internal.tracker.SimpleFieldTracker;
 import org.hibernate.bytecode.enhance.spi.CollectionTracker;
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.EnhancementException;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
 import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
-import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 
 /**
  * enhancer for regular entities
  *
  * @author <a href="mailto:lbarreiro@redhat.com">Luis Barreiro</a>
  */
 public class EntityEnhancer extends Enhancer {
 
 	public EntityEnhancer(EnhancementContext context) {
 		super( context );
 	}
 
 	// assuming the number of fields is not very high, SimpleFieldTracker implementation it's the fastest
 	private static final String DIRTY_TRACKER_IMPL = SimpleFieldTracker.class.getName();
 	private static final String COLLECTION_TRACKER_IMPL = SimpleCollectionTracker.class.getName();
 
 	public void enhance(CtClass managedCtClass) {
 		// add the ManagedEntity interface
 		managedCtClass.addInterface( managedEntityCtClass );
 
 		addEntityInstanceHandling( managedCtClass );
 		addEntityEntryHandling( managedCtClass );
 		addLinkedPreviousHandling( managedCtClass );
 		addLinkedNextHandling( managedCtClass );
 		addInterceptorHandling( managedCtClass );
 
 		if ( enhancementContext.doDirtyCheckingInline( managedCtClass ) ) {
 			addInLineDirtyHandling( managedCtClass );
 		}
 
 		new PersistentAttributesEnhancer( enhancementContext ).enhance( managedCtClass );
 	}
 
 	private void addEntityInstanceHandling(CtClass managedCtClass) {
 		try {
 			MethodWriter.write(
 					managedCtClass,
 					"public Object %s() { return this; }",
 					EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME
 			);
 		}
 		catch (CannotCompileException cce) {
 			throw new EnhancementException(
 					String.format(
 							Locale.ROOT,
 							"Could not enhance entity class [%s] to add EntityEntry getter",
 							managedCtClass.getName()
 					),
 					cce
 			);
 		}
 	}
 
 	private void addEntityEntryHandling(CtClass managedCtClass) {
 		FieldWriter.addFieldWithGetterAndSetter(
 				managedCtClass, entityEntryCtClass,
 				EnhancerConstants.ENTITY_ENTRY_FIELD_NAME,
 				EnhancerConstants.ENTITY_ENTRY_GETTER_NAME,
 				EnhancerConstants.ENTITY_ENTRY_SETTER_NAME
 		);
 	}
 
 	private void addLinkedPreviousHandling(CtClass managedCtClass) {
 		FieldWriter.addFieldWithGetterAndSetter(
 				managedCtClass, managedEntityCtClass,
 				EnhancerConstants.PREVIOUS_FIELD_NAME,
 				EnhancerConstants.PREVIOUS_GETTER_NAME,
 				EnhancerConstants.PREVIOUS_SETTER_NAME
 		);
 	}
 
 	private void addLinkedNextHandling(CtClass managedCtClass) {
 		FieldWriter.addFieldWithGetterAndSetter(
 				managedCtClass, managedEntityCtClass,
 				EnhancerConstants.NEXT_FIELD_NAME,
 				EnhancerConstants.NEXT_GETTER_NAME,
 				EnhancerConstants.NEXT_SETTER_NAME
 		);
 	}
 
 	private void addInLineDirtyHandling(CtClass managedCtClass) {
 		try {
 			managedCtClass.addInterface( classPool.get( SelfDirtinessTracker.class.getName() ) );
 
 			FieldWriter.addField(
 					managedCtClass,
 					classPool.get( DirtyTracker.class.getName() ),
 					EnhancerConstants.TRACKER_FIELD_NAME
 			);
 			FieldWriter.addField(
 					managedCtClass,
 					classPool.get( CollectionTracker.class.getName() ),
 					EnhancerConstants.TRACKER_COLLECTION_NAME
 			);
 
 			createDirtyTrackerMethods( managedCtClass );
 		}
 		catch (NotFoundException nfe) {
 			nfe.printStackTrace();
 		}
 	}
 
 	private void createDirtyTrackerMethods(CtClass managedCtClass) {
 		try {
 			MethodWriter.write(
 					managedCtClass,
 							"public void %1$s(String name) {%n" +
 							"  if (%2$s == null) { %2$s = new %3$s(); }%n" +
 							"  %2$s.add(name);%n" +
 							"}",
 					EnhancerConstants.TRACKER_CHANGER_NAME,
 					EnhancerConstants.TRACKER_FIELD_NAME,
 					DIRTY_TRACKER_IMPL
 			);
 
 			createCollectionDirtyCheckMethod( managedCtClass );
 			createCollectionDirtyCheckGetFieldsMethod( managedCtClass );
 			createClearDirtyCollectionMethod( managedCtClass );
 
 			MethodWriter.write(
 					managedCtClass,
 							"public String[] %1$s() {%n" +
 							"  if(%3$s == null) {%n" +
 							"    return (%2$s == null) ? new String[0] : %2$s.get();%n" +
 							"  } else {%n" +
 							"    if (%2$s == null) %2$s = new %5$s();%n" +
 							"    %4$s(%2$s);%n" +
 							"    return %2$s.get();%n" +
 							"  }%n" +
 							"}",
 					EnhancerConstants.TRACKER_GET_NAME,
 					EnhancerConstants.TRACKER_FIELD_NAME,
 					EnhancerConstants.TRACKER_COLLECTION_NAME,
 					EnhancerConstants.TRACKER_COLLECTION_CHANGED_FIELD_NAME,
 					DIRTY_TRACKER_IMPL
 			);
 
 			MethodWriter.write(
 					managedCtClass,
 							"public boolean %1$s() {%n" +
 							"  return (%2$s != null && !%2$s.isEmpty()) || %3$s();%n" +
 							"}",
 					EnhancerConstants.TRACKER_HAS_CHANGED_NAME,
 					EnhancerConstants.TRACKER_FIELD_NAME,
 					EnhancerConstants.TRACKER_COLLECTION_CHANGED_NAME
 			);
 
 			MethodWriter.write(
 					managedCtClass,
 							"public void %1$s() {%n" +
 							"  if (%2$s != null) { %2$s.clear(); }%n" +
 							"  %3$s();%n" +
 							"}",
 					EnhancerConstants.TRACKER_CLEAR_NAME,
 					EnhancerConstants.TRACKER_FIELD_NAME,
 					EnhancerConstants.TRACKER_COLLECTION_CLEAR_NAME
 			);
 
 			MethodWriter.write(
 					managedCtClass,
 							"public void %1$s(boolean f) {%n" +
 							"  if (%2$s == null) %2$s = new %3$s();%n  %2$s.suspend(f);%n" +
 							"}",
 					EnhancerConstants.TRACKER_SUSPEND_NAME,
 					EnhancerConstants.TRACKER_FIELD_NAME  ,
 					DIRTY_TRACKER_IMPL
 			);
 
 			MethodWriter.write(
 					managedCtClass,
 							"public %s %s() { return %s; }",
 					CollectionTracker.class.getName(),
 					EnhancerConstants.TRACKER_COLLECTION_GET_NAME,
 					EnhancerConstants.TRACKER_COLLECTION_NAME
 			);
 		}
 		catch (CannotCompileException cce) {
 			cce.printStackTrace();
 		}
 	}
 
 	private List<CtField> collectCollectionFields(CtClass managedCtClass) {
 		final List<CtField> collectionList = new LinkedList<CtField>();
 		try {
 			for ( CtField ctField : managedCtClass.getDeclaredFields() ) {
 				// skip static fields and skip fields added by enhancement
 				if ( Modifier.isStatic( ctField.getModifiers() ) || ctField.getName().startsWith( "$$_hibernate_" ) ) {
 					continue;
 				}
 				if ( enhancementContext.isPersistentField( ctField ) ) {
 					for ( CtClass ctClass : ctField.getType().getInterfaces() ) {
 						if ( PersistentAttributesHelper.isAssignable( ctClass, Collection.class.getName() ) ) {
 							collectionList.add( ctField );
 							break;
 						}
 					}
 				}
 			}
 		}
 		catch (NotFoundException ignored) {
 		}
 		return collectionList;
 	}
 
 	private void createCollectionDirtyCheckMethod(CtClass managedCtClass) {
 		try {
 			final StringBuilder body = new StringBuilder();
 
 			body.append(
 					String.format(
 									"private boolean %1$s() {%n" +
 									"  if (%2$s == null) { return false; }%n%n",
 							EnhancerConstants.TRACKER_COLLECTION_CHANGED_NAME,
 							EnhancerConstants.TRACKER_COLLECTION_NAME
 					)
 			);
 
 			for ( CtField ctField : collectCollectionFields( managedCtClass ) ) {
 				if ( !enhancementContext.isMappedCollection( ctField ) ) {
 					body.append(
 							String.format(
 											"  // collection field [%1$s]%n" +
 											"  if (%1$s == null && %2$s.getSize(\"%1$s\") != -1) { return true; }%n" +
 											"  if (%1$s != null && %2$s.getSize(\"%1$s\") != %1$s.size()) { return true; }%n%n",
 									ctField.getName(),
 									EnhancerConstants.TRACKER_COLLECTION_NAME
 							)
 					);
 				}
 			}
 			body.append( "  return false;%n}" );
 
 			MethodWriter.write( managedCtClass, body.toString() );
 		}
 		catch (CannotCompileException cce) {
 			cce.printStackTrace();
 		}
 	}
 
 	private void createCollectionDirtyCheckGetFieldsMethod(CtClass managedCtClass) {
 		try {
 			final StringBuilder body = new StringBuilder();
 
 			body.append(
 					String.format(
 									"private void %1$s(%3$s tracker) {%n" +
 									"  if (%2$s == null) { return; }%n%n",
 							EnhancerConstants.TRACKER_COLLECTION_CHANGED_FIELD_NAME,
 							EnhancerConstants.TRACKER_COLLECTION_NAME,
 							DirtyTracker.class.getName()
 					)
 			);
 
 			for ( CtField ctField : collectCollectionFields( managedCtClass ) ) {
 				if ( !enhancementContext.isMappedCollection( ctField ) ) {
 					body.append(
 							String.format(
 											"  // Collection field [%1$s]%n" +
 											"  if (%1$s == null && %2$s.getSize(\"%1$s\") != -1) { tracker.add(\"%1$s\"); }%n" +
 											"  if (%1$s != null && %2$s.getSize(\"%1$s\") != %1$s.size()) { tracker.add(\"%1$s\"); }%n%n",
 									ctField.getName(),
 									EnhancerConstants.TRACKER_COLLECTION_NAME
 							)
 					);
 				}
 			}
 			body.append( "}" );
 
 			MethodWriter.write( managedCtClass, body.toString() );
 		}
 		catch (CannotCompileException cce) {
 			cce.printStackTrace();
 		}
 	}
 
 	private void createClearDirtyCollectionMethod(CtClass managedCtClass) throws CannotCompileException {
 		try {
 			final StringBuilder body = new StringBuilder();
 
 			body.append(
 					String.format(
 							"private void %1$s() {%n" +
 									"  if (%2$s == null) { %2$s = new %3$s(); }%n" +
 									"  %4$s lazyInterceptor = null;%n",
 							EnhancerConstants.TRACKER_COLLECTION_CLEAR_NAME,
 							EnhancerConstants.TRACKER_COLLECTION_NAME,
 							COLLECTION_TRACKER_IMPL,
-							LazyAttributeLoader.class.getName()
+							LazyAttributeLoadingInterceptor.class.getName()
 					)
 			);
 
 			if ( PersistentAttributesHelper.isAssignable( managedCtClass, PersistentAttributeInterceptable.class.getName() ) ) {
 				body.append(
 						String.format(
 										"  if(%1$s != null && %1$s instanceof %2$s) lazyInterceptor = (%2$s) %1$s;%n%n",
 								EnhancerConstants.INTERCEPTOR_FIELD_NAME,
-								LazyAttributeLoader.class.getName()
+								LazyAttributeLoadingInterceptor.class.getName()
 						)
 				);
 			}
 
 			for ( CtField ctField : collectCollectionFields( managedCtClass ) ) {
 				if ( !enhancementContext.isMappedCollection( ctField ) ) {
 					body.append(
 							String.format(
 										"  // collection field [%1$s]%n" +
 										"  if (lazyInterceptor == null || lazyInterceptor.isAttributeLoaded(\"%1$s\")) {%n" +
 										"    if (%1$s == null) { %2$s.add(\"%1$s\", -1); }%n" +
 										"    else { %2$s.add(\"%1$s\", %1$s.size()); }%n" +
 										"  }%n%n",
 									ctField.getName(),
 									EnhancerConstants.TRACKER_COLLECTION_NAME
 							)
 					);
 				}
 			}
 			body.append( "}" );
 
 			MethodWriter.write( managedCtClass, body.toString() );
 		}
 		catch (CannotCompileException cce) {
 			cce.printStackTrace();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/LazyPropertyInitializer.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/LazyPropertyInitializer.java
index 37cbad8d40..fec68f021c 100755
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/LazyPropertyInitializer.java
@@ -1,45 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.bytecode.instrumentation.spi;
+package org.hibernate.bytecode.enhance.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Contract for controlling how lazy properties get initialized.
  * 
  * @author Gavin King
  */
 public interface LazyPropertyInitializer {
 
 	/**
 	 * Marker value for uninitialized properties.
 	 */
 	public static final Serializable UNFETCHED_PROPERTY = new Serializable() {
 		@Override
 		public String toString() {
 			return "<lazy>";
 		}
 
 		public Object readResolve() {
 			return UNFETCHED_PROPERTY;
 		}
 	};
 
 	/**
 	 * Initialize the property, and return its new value.
 	 *
 	 * @param fieldName The name of the field being initialized
 	 * @param entity The entity on which the initialization is occurring
 	 * @param session The session from which the initialization originated.
 	 *
 	 * @return ?
 	 */
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/interceptor/LazyAttributeLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/interceptor/LazyAttributeLoadingInterceptor.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/interceptor/LazyAttributeLoader.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/interceptor/LazyAttributeLoadingInterceptor.java
index 13a7986212..71168f762c 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/interceptor/LazyAttributeLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/enhance/spi/interceptor/LazyAttributeLoadingInterceptor.java
@@ -1,300 +1,300 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 
 package org.hibernate.bytecode.enhance.spi.interceptor;
 
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Set;
 
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.enhance.internal.tracker.SimpleFieldTracker;
 import org.hibernate.bytecode.enhance.spi.CollectionTracker;
 import org.hibernate.bytecode.enhance.spi.interceptor.Helper.Consumer;
 import org.hibernate.bytecode.enhance.spi.interceptor.Helper.LazyInitializationWork;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.jboss.logging.Logger;
 
 /**
  * Interceptor that loads attributes lazily
  *
  * @author Luis Barreiro
  */
-public class LazyAttributeLoader implements PersistentAttributeInterceptor, Consumer {
-	private static final Logger log = Logger.getLogger( LazyAttributeLoader.class );
+public class LazyAttributeLoadingInterceptor implements PersistentAttributeInterceptor, Consumer {
+	private static final Logger log = Logger.getLogger( LazyAttributeLoadingInterceptor.class );
 
 	private transient SessionImplementor session;
 
 	private final Set<String> lazyFields;
 	private final String entityName;
 
 	private String sessionFactoryUuid;
 	private boolean allowLoadOutsideTransaction;
 
 	private final SimpleFieldTracker initializedFields = new SimpleFieldTracker();
 
-	public LazyAttributeLoader(SessionImplementor session, Set<String> lazyFields, String entityName) {
+	public LazyAttributeLoadingInterceptor(SessionImplementor session, Set<String> lazyFields, String entityName) {
 		this.session = session;
 		this.lazyFields = lazyFields;
 		this.entityName = entityName;
 
 		this.allowLoadOutsideTransaction = session.getFactory().getSessionFactoryOptions().isInitializeLazyStateOutsideTransactionsEnabled();
 		if ( this.allowLoadOutsideTransaction ) {
 			this.sessionFactoryUuid = session.getFactory().getUuid();
 		}
 	}
 
 	protected final Object intercept(Object target, String attributeName, Object value) {
 		if ( !isAttributeLoaded( attributeName ) ) {
 			return loadAttribute( target, attributeName );
 		}
 		return value;
 	}
 
 	private Object loadAttribute(final Object target, final String attributeName) {
 		return new Helper( this ).performWork(
 				new LazyInitializationWork() {
 					@Override
 					public Object doWork(SessionImplementor session, boolean isTemporarySession) {
 						final EntityPersister persister = session.getFactory().getEntityPersister( getEntityName() );
 
 						if ( isTemporarySession ) {
 							final Serializable id = persister.getIdentifier( target, null );
 
 							// Add an entry for this entity in the PC of the temp Session
 							// NOTE : a few arguments that would be nice to pass along here...
 							//		1) loadedState if we know any
 							final Object[] loadedState = null;
 							//		2) does a row exist in the db for this entity?
 							final boolean existsInDb = true;
 							// NOTE2: the final boolean is 'lazyPropertiesAreUnfetched' which is another
 							//	place where a "single lazy fetch group" shows up
 							session.getPersistenceContext().addEntity(
 									target,
 									Status.READ_ONLY,
 									loadedState,
 									session.generateEntityKey( id, persister ),
 									persister.getVersion( target ),
 									LockMode.NONE,
 									existsInDb,
 									persister,
 									true,
 									true
 							);
 						}
 
 						final LazyPropertyInitializer initializer = (LazyPropertyInitializer) persister;
 						final Object loadedValue = initializer.initializeLazyProperty(
 								attributeName,
 								target,
 								session
 						);
 
 						initializedFields.add( attributeName );
 						takeCollectionSizeSnapshot( target, attributeName, loadedValue );
 						return loadedValue;
 					}
 
 					@Override
 					public String getEntityName() {
 						return entityName;
 					}
 
 					@Override
 					public String getAttributeName() {
 						return attributeName;
 					}
 				}
 		);
 	}
 
 	public final void setSession(SessionImplementor session) {
 		this.session = session;
 	}
 
 	public final void unsetSession() {
 		this.session = null;
 	}
 
 	public boolean isAttributeLoaded(String fieldName) {
 		return lazyFields == null || !lazyFields.contains( fieldName ) || initializedFields.contains( fieldName );
 	}
 
-	public boolean isUninitialized() {
+	public boolean hasAnyUninitializedAttributes() {
 		if ( lazyFields != null ) {
 			for ( String fieldName : lazyFields ) {
 				if ( !initializedFields.contains( fieldName ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public void setLoaded(String attributeName) {
 		initializedFields.add( attributeName );
 	}
 
 	public String[] getiInitializedFields() {
 		return initializedFields.get();
 	}
 
 	@Override
 	public String toString() {
 		return "LazyAttributeLoader(entityName=" + entityName + " ,lazyFields=" + lazyFields + ')';
 	}
 
 	//
 
 	private void takeCollectionSizeSnapshot(Object target, String fieldName, Object value) {
 		if ( value != null && value instanceof Collection && target instanceof SelfDirtinessTracker ) {
 			CollectionTracker tracker = ( (SelfDirtinessTracker) target ).$$_hibernate_getCollectionTracker();
 			if ( tracker == null ) {
 				( (SelfDirtinessTracker) target ).$$_hibernate_clearDirtyAttributes();
 			}
 			tracker.add( fieldName, ( (Collection) value ).size() );
 		}
 	}
 
 	@Override
 	public boolean readBoolean(Object obj, String name, boolean oldValue) {
 		return (Boolean) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public byte readByte(Object obj, String name, byte oldValue) {
 		return (Byte) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public char readChar(Object obj, String name, char oldValue) {
 		return (Character) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public char writeChar(Object obj, String name, char oldValue, char newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public short readShort(Object obj, String name, short oldValue) {
 		return (Short) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public short writeShort(Object obj, String name, short oldValue, short newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public int readInt(Object obj, String name, int oldValue) {
 		return (Integer) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public int writeInt(Object obj, String name, int oldValue, int newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public float readFloat(Object obj, String name, float oldValue) {
 		return (Float) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public float writeFloat(Object obj, String name, float oldValue, float newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public double readDouble(Object obj, String name, double oldValue) {
 		return (Double) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public double writeDouble(Object obj, String name, double oldValue, double newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public long readLong(Object obj, String name, long oldValue) {
 		return (Long) intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public long writeLong(Object obj, String name, long oldValue, long newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public Object readObject(Object obj, String name, Object oldValue) {
 		return intercept( obj, name, oldValue );
 	}
 
 	@Override
 	public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
 		if ( lazyFields != null && lazyFields.contains( name ) ) {
 			initializedFields.add( name );
 		}
 		return newValue;
 	}
 
 	@Override
 	public SessionImplementor getLinkedSession() {
 		return session;
 	}
 
 	@Override
 	public boolean allowLoadOutsideTransaction() {
 		return allowLoadOutsideTransaction;
 	}
 
 	@Override
 	public String getSessionFactoryUuid() {
 		return sessionFactoryUuid;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
deleted file mode 100644
index 866ea9cbf5..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
+++ /dev/null
@@ -1,164 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.instrumentation.internal;
-
-import java.util.HashSet;
-import java.util.Set;
-
-import org.hibernate.bytecode.instrumentation.internal.javassist.JavassistHelper;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.engine.spi.SessionImplementor;
-
-/**
- * Helper class for dealing with enhanced entity classes.
- *
- * These operations are expensive.  They are only meant to be used when code does not have access to a
- * SessionFactory (namely from the instrumentation tasks).  When code has access to a SessionFactory,
- * {@link org.hibernate.bytecode.spi.EntityInstrumentationMetadata} should be used instead to query the
- * instrumentation state.  EntityInstrumentationMetadata is accessed from the
- * {@link org.hibernate.persister.entity.EntityPersister} via the
- * {@link org.hibernate.persister.entity.EntityPersister#getInstrumentationMetadata()} method.
- *
- * @author Steve Ebersole
- */
-public class FieldInterceptionHelper {
-	private static final Set<Delegate> INSTRUMENTATION_DELEGATES = buildInstrumentationDelegates();
-
-	private static Set<Delegate> buildInstrumentationDelegates() {
-		final HashSet<Delegate> delegates = new HashSet<Delegate>();
-		delegates.add( JavassistDelegate.INSTANCE );
-		return delegates;
-	}
-
-	/**
-	 * Utility to check to see if a given entity class is instrumented.
-	 *
-	 * @param entityClass The entity class to check
-	 *
-	 * @return {@code true} if it has been instrumented; {@code false} otherwise
-	 */
-	public static boolean isInstrumented(Class entityClass) {
-		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
-			if ( delegate.isInstrumented( entityClass ) ) {
-				return true;
-			}
-		}
-		return false;
-	}
-
-	/**
-	 * Utility to check to see if a given object is an instance of an instrumented class.  If the instance
-	 * is {@code null}, the check returns {@code false}
-	 *
-	 * @param object The object to check
-	 *
-	 * @return {@code true} if it has been instrumented; {@code false} otherwise
-	 */
-	public static boolean isInstrumented(Object object) {
-		return object != null && isInstrumented( object.getClass() );
-	}
-
-	/**
-	 * Assuming the given object is an enhanced entity, extract and return its interceptor.  Will
-	 * return {@code null} if object is {@code null}, or if the object was deemed to not be
-	 * instrumented
-	 *
-	 * @param object The object from which to extract the interceptor
-	 *
-	 * @return The extracted interceptor, or {@code null}
-	 */
-	public static FieldInterceptor extractFieldInterceptor(Object object) {
-		if ( object == null ) {
-			return null;
-		}
-		FieldInterceptor interceptor = null;
-		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
-			interceptor = delegate.extractInterceptor( object );
-			if ( interceptor != null ) {
-				break;
-			}
-		}
-		return interceptor;
-	}
-
-	/**
-	 * Assuming the given object is an enhanced entity, inject a field interceptor.
-	 *
-	 * @param entity The entity instance
-	 * @param entityName The entity name
-	 * @param uninitializedFieldNames The names of any uninitialized fields
-	 * @param session The session
-	 *
-	 * @return The injected interceptor
-	 */
-	public static FieldInterceptor injectFieldInterceptor(
-			Object entity,
-			String entityName,
-			Set uninitializedFieldNames,
-			SessionImplementor session) {
-		if ( entity == null ) {
-			return null;
-		}
-		FieldInterceptor interceptor = null;
-		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
-			interceptor = delegate.injectInterceptor( entity, entityName, uninitializedFieldNames, session );
-			if ( interceptor != null ) {
-				break;
-			}
-		}
-		return interceptor;
-	}
-
-	private static interface Delegate {
-		public boolean isInstrumented(Class classToCheck);
-		public FieldInterceptor extractInterceptor(Object entity);
-		public FieldInterceptor injectInterceptor(Object entity, String entityName, Set uninitializedFieldNames, SessionImplementor session);
-	}
-
-	private static class JavassistDelegate implements Delegate {
-		public static final JavassistDelegate INSTANCE = new JavassistDelegate();
-		public static final String MARKER = "org.hibernate.bytecode.internal.javassist.FieldHandled";
-
-		@Override
-		public boolean isInstrumented(Class classToCheck) {
-			for ( Class definedInterface : classToCheck.getInterfaces() ) {
-				if ( MARKER.equals( definedInterface.getName() ) ) {
-					return true;
-				}
-			}
-			return false;
-		}
-
-		@Override
-		public FieldInterceptor extractInterceptor(Object entity) {
-			for ( Class definedInterface : entity.getClass().getInterfaces() ) {
-				if ( MARKER.equals( definedInterface.getName() ) ) {
-					return JavassistHelper.extractFieldInterceptor( entity );
-				}
-			}
-			return null;
-		}
-
-		@Override
-		public FieldInterceptor injectInterceptor(
-				Object entity,
-				String entityName,
-				Set uninitializedFieldNames,
-				SessionImplementor session) {
-			for ( Class definedInterface : entity.getClass().getInterfaces() ) {
-				if ( MARKER.equals( definedInterface.getName() ) ) {
-					return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
-				}
-			}
-			return null;
-		}
-	}
-
-	private FieldInterceptionHelper() {
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
deleted file mode 100644
index a3ccd70c5c..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
+++ /dev/null
@@ -1,145 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.instrumentation.internal.javassist;
-
-import java.io.Serializable;
-import java.util.Set;
-
-import org.hibernate.bytecode.instrumentation.spi.AbstractFieldInterceptor;
-import org.hibernate.bytecode.internal.javassist.FieldHandler;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.LazyInitializer;
-
-/**
- * A field-level interceptor that initializes lazily fetched properties.
- * This interceptor can be attached to classes instrumented by Javassist.
- * Note that this implementation assumes that the instance variable
- * name is the same as the name of the persistent property that must
- * be loaded.
- * </p>
- * Note: most of the interesting functionality here is farmed off
- * to the super-class.  The stuff here mainly acts as an adapter to the
- * Javassist-specific functionality, routing interception through
- * the super-class's intercept() method
- *
- * @author Steve Ebersole
- */
-final class FieldInterceptorImpl extends AbstractFieldInterceptor implements FieldHandler, Serializable {
-
-	FieldInterceptorImpl(SessionImplementor session, Set uninitializedFields, String entityName) {
-		super( session, uninitializedFields, entityName );
-	}
-
-
-	// FieldHandler impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	@Override
-	public boolean readBoolean(Object target, String name, boolean oldValue) {
-		return (Boolean) intercept( target, name, oldValue );
-	}
-	@Override
-	public byte readByte(Object target, String name, byte oldValue) {
-		return (Byte) intercept( target, name, oldValue );
-	}
-	@Override
-	public char readChar(Object target, String name, char oldValue) {
-		return (Character) intercept( target, name, oldValue );
-	}
-	@Override
-	public double readDouble(Object target, String name, double oldValue) {
-		return (Double) intercept( target, name, oldValue );
-	}
-	@Override
-	public float readFloat(Object target, String name, float oldValue) {
-		return (Float) intercept( target, name, oldValue );
-	}
-	@Override
-	public int readInt(Object target, String name, int oldValue) {
-		return (Integer) intercept( target, name, oldValue );
-	}
-	@Override
-	public long readLong(Object target, String name, long oldValue) {
-		return (Long) intercept( target, name, oldValue );
-	}
-	@Override
-	public short readShort(Object target, String name, short oldValue) {
-		return (Short) intercept( target, name, oldValue );
-	}
-	@Override
-	public Object readObject(Object target, String name, Object oldValue) {
-		Object value = intercept( target, name, oldValue );
-		if ( value instanceof HibernateProxy ) {
-			final LazyInitializer li = ( (HibernateProxy) value ).getHibernateLazyInitializer();
-			if ( li.isUnwrap() ) {
-				value = li.getImplementation();
-			}
-		}
-		return value;
-	}
-	@Override
-	public boolean writeBoolean(Object target, String name, boolean oldValue, boolean newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public byte writeByte(Object target, String name, byte oldValue, byte newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public char writeChar(Object target, String name, char oldValue, char newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public double writeDouble(Object target, String name, double oldValue, double newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public float writeFloat(Object target, String name, float oldValue, float newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public int writeInt(Object target, String name, int oldValue, int newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public long writeLong(Object target, String name, long oldValue, long newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public short writeShort(Object target, String name, short oldValue, short newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public Object writeObject(Object target, String name, Object oldValue, Object newValue) {
-		dirty();
-		intercept( target, name, oldValue );
-		return newValue;
-	}
-	@Override
-	public String toString() {
-		return "FieldInterceptorImpl(entityName=" + getEntityName() +
-				",dirty=" + isDirty() +
-				",uninitializedFields=" + getUninitializedFields() +
-				')';
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
deleted file mode 100644
index 439152a30a..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
+++ /dev/null
@@ -1,54 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.instrumentation.internal.javassist;
-
-import java.util.Set;
-
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.bytecode.internal.javassist.FieldHandled;
-import org.hibernate.engine.spi.SessionImplementor;
-
-/**
- * Javassist specific helper
- *
- * @author Steve Ebersole
- */
-public class JavassistHelper {
-	private JavassistHelper() {
-	}
-
-	/**
-	 * Perform the Javassist-specific field interceptor extraction
-	 *
-	 * @param entity The entity from which to extract the interceptor
-	 *
-	 * @return The extracted interceptor
-	 */
-	public static FieldInterceptor extractFieldInterceptor(Object entity) {
-		return (FieldInterceptor) ( (FieldHandled) entity ).getFieldHandler();
-	}
-
-	/**
-	 * Perform the Javassist-specific field interceptor injection
-	 *
-	 * @param entity The entity instance
-	 * @param entityName The entity name
-	 * @param uninitializedFieldNames The names of any uninitialized fields
-	 * @param session The session
-	 *
-	 * @return The generated and injected interceptor
-	 */
-	public static FieldInterceptor injectFieldInterceptor(
-			Object entity,
-			String entityName,
-			Set uninitializedFieldNames,
-			SessionImplementor session) {
-		final FieldInterceptorImpl fieldInterceptor = new FieldInterceptorImpl( session, uninitializedFieldNames, entityName );
-		( (FieldHandled) entity ).setFieldHandler( fieldInterceptor );
-		return fieldInterceptor;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/package-info.java
deleted file mode 100644
index c4c268ef36..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/package-info.java
+++ /dev/null
@@ -1,11 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-/**
- * Javassist support internals
- */
-package org.hibernate.bytecode.instrumentation.internal.javassist;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/package-info.java
deleted file mode 100644
index 0130fa3d2e..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/package-info.java
+++ /dev/null
@@ -1,11 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-/**
- * Bytecode instrumentation internals
- */
-package org.hibernate.bytecode.instrumentation.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/package.html b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/package.html
deleted file mode 100755
index d0a85d1bab..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/package.html
+++ /dev/null
@@ -1,15 +0,0 @@
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
-  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
-  -->
-
-<html>
-<head></head>
-<body>
-<p>
-	This package implements an interception mechanism for lazy property fetching, based on bytecode instrumentation.
-</p>
-</body>
-</html>
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
deleted file mode 100644
index 0c45cf2ebc..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
+++ /dev/null
@@ -1,145 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.instrumentation.spi;
-import java.io.Serializable;
-import java.util.Set;
-
-import org.hibernate.LazyInitializationException;
-import org.hibernate.engine.spi.SessionImplementor;
-
-/**
- * Base support for FieldInterceptor implementations.
- *
- * @author Steve Ebersole
- */
-public abstract class AbstractFieldInterceptor implements FieldInterceptor, Serializable {
-
-	private transient SessionImplementor session;
-	private Set uninitializedFields;
-	private final String entityName;
-
-	private transient boolean initializing;
-	private boolean dirty;
-
-	protected AbstractFieldInterceptor(SessionImplementor session, Set uninitializedFields, String entityName) {
-		this.session = session;
-		this.uninitializedFields = uninitializedFields;
-		this.entityName = entityName;
-	}
-
-
-	// FieldInterceptor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	@Override
-	public final void setSession(SessionImplementor session) {
-		this.session = session;
-	}
-
-	@Override
-	public final boolean isInitialized() {
-		return uninitializedFields == null || uninitializedFields.size() == 0;
-	}
-
-	@Override
-	public final boolean isInitialized(String field) {
-		return uninitializedFields == null || !uninitializedFields.contains( field );
-	}
-
-	@Override
-	public final void dirty() {
-		dirty = true;
-	}
-
-	@Override
-	public final boolean isDirty() {
-		return dirty;
-	}
-
-	@Override
-	public final void clearDirty() {
-		dirty = false;
-	}
-
-
-	// subclass accesses ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	/**
-	 * Interception of access to the named field
-	 *
-	 * @param target The call target
-	 * @param fieldName The name of the field.
-	 * @param value The value.
-	 *
-	 * @return ?
-	 */
-	protected final Object intercept(Object target, String fieldName, Object value) {
-		if ( initializing ) {
-			return value;
-		}
-
-		if ( uninitializedFields != null && uninitializedFields.contains( fieldName ) ) {
-			if ( session == null ) {
-				throw new LazyInitializationException( "entity with lazy properties is not associated with a session" );
-			}
-			else if ( !session.isOpen() || !session.isConnected() ) {
-				throw new LazyInitializationException( "session is not connected" );
-			}
-
-			final Object result;
-			initializing = true;
-			try {
-				result = ( (LazyPropertyInitializer) session.getFactory().getEntityPersister( entityName ) )
-						.initializeLazyProperty( fieldName, target, session );
-			}
-			finally {
-				initializing = false;
-			}
-			// let's assume that there is only one lazy fetch group, for now!
-			uninitializedFields = null;
-			return result;
-		}
-		else {
-			return value;
-		}
-	}
-
-	/**
-	 * Access to the session
-	 *
-	 * @return The associated session
-	 */
-	public final SessionImplementor getSession() {
-		return session;
-	}
-
-	/**
-	 * Access to all currently uninitialized fields
-	 *
-	 * @return The name of all currently uninitialized fields
-	 */
-	public final Set getUninitializedFields() {
-		return uninitializedFields;
-	}
-
-	/**
-	 * Access to the intercepted entity name
-	 *
-	 * @return The entity name
-	 */
-	public final String getEntityName() {
-		return entityName;
-	}
-
-	/**
-	 * Is the instance currently initializing?
-	 *
-	 * @return true/false.
-	 */
-	public final boolean isInitializing() {
-		return initializing;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
deleted file mode 100755
index 044e687f0a..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
+++ /dev/null
@@ -1,56 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.instrumentation.spi;
-
-import org.hibernate.engine.spi.SessionImplementor;
-
-/**
- * Contract for field interception handlers.
- *
- * @author Steve Ebersole
- */
-public interface FieldInterceptor {
-
-	/**
-	 * Use to associate the entity to which we are bound to the given session.
-	 *
-	 * @param session The session to which we are now associated.
-	 */
-	public void setSession(SessionImplementor session);
-
-	/**
-	 * Is the entity to which we are bound completely initialized?
-	 *
-	 * @return True if the entity is initialized; otherwise false.
-	 */
-	public boolean isInitialized();
-
-	/**
-	 * The the given field initialized for the entity to which we are bound?
-	 *
-	 * @param field The name of the field to check
-	 * @return True if the given field is initialized; otherwise false.
-	 */
-	public boolean isInitialized(String field);
-
-	/**
-	 * Forcefully mark the entity as being dirty.
-	 */
-	public void dirty();
-
-	/**
-	 * Is the entity considered dirty?
-	 *
-	 * @return True if the entity is dirty; otherwise false.
-	 */
-	public boolean isDirty();
-
-	/**
-	 * Clear the internal dirty flag.
-	 */
-	public void clearDirty();
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/package-info.java
deleted file mode 100644
index bf446643b3..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/package-info.java
+++ /dev/null
@@ -1,13 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-/**
- * Package defining bytecode code enhancement (instrumentation) support.
- *
- * This package should mostly be considered deprecated in favor of {@link org.hibernate.bytecode.enhance}
- */
-package org.hibernate.bytecode.instrumentation.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
index ca6e980d34..ed47006768 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
@@ -1,171 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Modifier;
-import java.util.Set;
 
-import org.hibernate.bytecode.buildtime.spi.ClassFilter;
-import org.hibernate.bytecode.buildtime.spi.FieldFilter;
-import org.hibernate.bytecode.instrumentation.internal.javassist.JavassistHelper;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.BytecodeProvider;
-import org.hibernate.bytecode.spi.ClassTransformer;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
-import org.hibernate.bytecode.spi.NotInstrumentedException;
 import org.hibernate.bytecode.spi.ProxyFactoryFactory;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
-import org.hibernate.engine.spi.SessionImplementor;
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
 
-	@Override
-	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter) {
-		return new JavassistClassTransformer( classFilter, fieldFilter );
-	}
-
-	@Override
-	public EntityInstrumentationMetadata getEntityInstrumentationMetadata(Class entityClass) {
-		return new EntityInstrumentationMetadataImpl( entityClass );
-	}
-
-	private static class EntityInstrumentationMetadataImpl implements EntityInstrumentationMetadata {
-		private final Class entityClass;
-		private final boolean isInstrumented;
-
-		private EntityInstrumentationMetadataImpl(Class entityClass) {
-			this.entityClass = entityClass;
-			this.isInstrumented = FieldHandled.class.isAssignableFrom( entityClass );
-		}
-
-		@Override
-		public String getEntityName() {
-			return entityClass.getName();
-		}
-
-		@Override
-		public boolean isInstrumented() {
-			return isInstrumented;
-		}
-
-		@Override
-		public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
-			if ( !entityClass.isInstance( entity ) ) {
-				throw new IllegalArgumentException(
-						String.format(
-								"Passed entity instance [%s] is not of expected type [%s]",
-								entity,
-								getEntityName()
-						)
-				);
-			}
-			if ( ! isInstrumented() ) {
-				throw new NotInstrumentedException( String.format( "Entity class [%s] is not instrumented", getEntityName() ) );
-			}
-			return JavassistHelper.extractFieldInterceptor( entity );
-		}
-
-		@Override
-		public FieldInterceptor injectInterceptor(
-				Object entity,
-				String entityName,
-				Set uninitializedFieldNames,
-				SessionImplementor session) throws NotInstrumentedException {
-			if ( !entityClass.isInstance( entity ) ) {
-				throw new IllegalArgumentException(
-						String.format(
-								"Passed entity instance [%s] is not of expected type [%s]",
-								entity,
-								getEntityName()
-						)
-				);
-			}
-			if ( ! isInstrumented() ) {
-				throw new NotInstrumentedException( String.format( "Entity class [%s] is not instrumented", getEntityName() ) );
-			}
-			return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
-		}
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
deleted file mode 100644
index cf6034ab5a..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
+++ /dev/null
@@ -1,59 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.internal.javassist;
-
-/**
- * Contract for deciding whether fields should be read and/or write intercepted.
- *
- * @author Muga Nishizawa
- * @author Steve Ebersole
- */
-public interface FieldFilter {
-	/**
-	 * Should the given field be read intercepted?
-	 *
-	 * @param desc The field descriptor
-	 * @param name The field name
-	 *
-	 * @return true if the given field should be read intercepted; otherwise
-	 * false.
-	 */
-	boolean handleRead(String desc, String name);
-
-	/**
-	 * Should the given field be write intercepted?
-	 *
-	 * @param desc The field descriptor
-	 * @param name The field name
-	 *
-	 * @return true if the given field should be write intercepted; otherwise
-	 * false.
-	 */
-	boolean handleWrite(String desc, String name);
-
-	/**
-	 * Should read access to the given field be intercepted?
-	 *
-	 * @param fieldOwnerClassName The class where the field being accessed is defined
-	 * @param fieldName The name of the field being accessed
-	 *
-	 * @return true if the given field read access should be write intercepted; otherwise
-	 * false.
-	 */
-	boolean handleReadAccess(String fieldOwnerClassName, String fieldName);
-
-	/**
-	 * Should write access to the given field be intercepted?
-	 *
-	 * @param fieldOwnerClassName The class where the field being accessed is defined
-	 * @param fieldName The name of the field being accessed
-	 *
-	 * @return true if the given field write access should be write intercepted; otherwise
-	 * false.
-	 */
-	boolean handleWriteAccess(String fieldOwnerClassName, String fieldName);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandled.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandled.java
deleted file mode 100644
index c401690ec1..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandled.java
+++ /dev/null
@@ -1,29 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.internal.javassist;
-
-/**
- * Interface introduced to the enhanced class in order to be able to
- * inject a {@link FieldHandler} to define the interception behavior.
- *
- * @author Muga Nishizawa
- */
-public interface FieldHandled {
-	/**
-	 * Inject the field interception handler to be used.
-	 *
-	 * @param handler The field interception handler.
-	 */
-	public void setFieldHandler(FieldHandler handler);
-
-	/**
-	 * Access to the current field interception handler.
-	 *
-	 * @return The current field interception handler.
-	 */
-	public FieldHandler getFieldHandler();
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
deleted file mode 100644
index e61f93d556..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
+++ /dev/null
@@ -1,224 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.internal.javassist;
-
-/**
- * The interface defining how interception of a field should be handled.
- *
- * @author Muga Nishizawa
- */
-@SuppressWarnings("UnusedDeclaration")
-public interface FieldHandler {
-
-	/**
-	 * Called to handle writing an int value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	int writeInt(Object obj, String name, int oldValue, int newValue);
-
-	/**
-	 * Called to handle writing a char value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	char writeChar(Object obj, String name, char oldValue, char newValue);
-
-	/**
-	 * Called to handle writing a byte value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	byte writeByte(Object obj, String name, byte oldValue, byte newValue);
-
-	/**
-	 * Called to handle writing a boolean value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue);
-
-	/**
-	 * Called to handle writing a short value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	short writeShort(Object obj, String name, short oldValue, short newValue);
-
-	/**
-	 * Called to handle writing a float value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	float writeFloat(Object obj, String name, float oldValue, float newValue);
-
-	/**
-	 * Called to handle writing a double value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	double writeDouble(Object obj, String name, double oldValue, double newValue);
-
-	/**
-	 * Called to handle writing a long value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument
-	 */
-	long writeLong(Object obj, String name, long oldValue, long newValue);
-
-	/**
-	 * Called to handle writing an Object value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 * @param newValue The new field value.
-	 *
-	 * @return The new value, typically the same as the newValue argument; may be different for entity references
-	 */
-	Object writeObject(Object obj, String name, Object oldValue, Object newValue);
-
-	/**
-	 * Called to handle reading an int value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	int readInt(Object obj, String name, int oldValue);
-
-	/**
-	 * Called to handle reading a char value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	char readChar(Object obj, String name, char oldValue);
-
-	/**
-	 * Called to handle reading a byte value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	byte readByte(Object obj, String name, byte oldValue);
-
-	/**
-	 * Called to handle reading a boolean value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	boolean readBoolean(Object obj, String name, boolean oldValue);
-
-	/**
-	 * Called to handle reading a short value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	short readShort(Object obj, String name, short oldValue);
-
-	/**
-	 * Called to handle reading a float value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	float readFloat(Object obj, String name, float oldValue);
-
-	/**
-	 * Called to handle reading a double value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	double readDouble(Object obj, String name, double oldValue);
-
-	/**
-	 * Called to handle reading a long value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	long readLong(Object obj, String name, long oldValue);
-
-	/**
-	 * Called to handle reading an Object value to a given field.
-	 *
-	 * @param obj The object instance on which the write was invoked
-	 * @param name The name of the field being written
-	 * @param oldValue The old field value
-	 *
-	 * @return The field value
-	 */
-	Object readObject(Object obj, String name, Object oldValue);
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
deleted file mode 100644
index 78064f6ee0..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
+++ /dev/null
@@ -1,734 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.internal.javassist;
-
-import java.io.DataInputStream;
-import java.io.DataOutputStream;
-import java.io.File;
-import java.io.FileInputStream;
-import java.io.FileOutputStream;
-import java.util.List;
-
-import javassist.CannotCompileException;
-import javassist.ClassPool;
-import javassist.bytecode.AccessFlag;
-import javassist.bytecode.BadBytecode;
-import javassist.bytecode.Bytecode;
-import javassist.bytecode.ClassFile;
-import javassist.bytecode.CodeAttribute;
-import javassist.bytecode.CodeIterator;
-import javassist.bytecode.ConstPool;
-import javassist.bytecode.Descriptor;
-import javassist.bytecode.FieldInfo;
-import javassist.bytecode.MethodInfo;
-import javassist.bytecode.Opcode;
-import javassist.bytecode.StackMapTable;
-import javassist.bytecode.stackmap.MapMaker;
-
-/**
- * The thing that handles actual class enhancement in regards to
- * intercepting field accesses.
- *
- * @author Muga Nishizawa
- * @author Steve Ebersole
- * @author Dustin Schultz
- */
-public class FieldTransformer {
-
-	private static final String EACH_READ_METHOD_PREFIX = "$javassist_read_";
-
-	private static final String EACH_WRITE_METHOD_PREFIX = "$javassist_write_";
-
-	private static final String FIELD_HANDLED_TYPE_NAME = FieldHandled.class.getName();
-
-	private static final String HANDLER_FIELD_NAME = "$JAVASSIST_READ_WRITE_HANDLER";
-
-	private static final String FIELD_HANDLER_TYPE_NAME = FieldHandler.class.getName();
-
-	private static final String HANDLER_FIELD_DESCRIPTOR = 'L' + FIELD_HANDLER_TYPE_NAME.replace( '.', '/' ) + ';';
-
-	private static final String GETFIELDHANDLER_METHOD_NAME = "getFieldHandler";
-
-	private static final String SETFIELDHANDLER_METHOD_NAME = "setFieldHandler";
-
-	private static final String GETFIELDHANDLER_METHOD_DESCRIPTOR = "()" + HANDLER_FIELD_DESCRIPTOR;
-
-	private static final String SETFIELDHANDLER_METHOD_DESCRIPTOR = "(" + HANDLER_FIELD_DESCRIPTOR + ")V";
-
-	private final FieldFilter filter;
-	private final ClassPool classPool;
-
-	FieldTransformer(FieldFilter f, ClassPool c) {
-		filter = f;
-		classPool = c;
-	}
-
-	/**
-	 * Transform the class contained in the given file, writing the result back to the same file.
-	 *
-	 * @param file The file containing the class to be transformed
-	 *
-	 * @throws Exception Indicates a problem performing the transformation
-	 */
-	public void transform(File file) throws Exception {
-		final DataInputStream in = new DataInputStream( new FileInputStream( file ) );
-		final ClassFile classfile = new ClassFile( in );
-		transform( classfile );
-
-		final DataOutputStream out = new DataOutputStream( new FileOutputStream( file ) );
-		try {
-			classfile.write( out );
-		}
-		finally {
-			out.close();
-		}
-	}
-
-	/**
-	 * Transform the class defined by the given ClassFile descriptor.  The ClassFile descriptor itself is mutated
-	 *
-	 * @param classFile The class file descriptor
-	 *
-	 * @throws Exception Indicates a problem performing the transformation
-	 */
-	public void transform(ClassFile classFile) throws Exception {
-		if ( classFile.isInterface() ) {
-			return;
-		}
-		try {
-			addFieldHandlerField( classFile );
-			addGetFieldHandlerMethod( classFile );
-			addSetFieldHandlerMethod( classFile );
-			addFieldHandledInterface( classFile );
-			addReadWriteMethods( classFile );
-			transformInvokevirtualsIntoPutAndGetfields( classFile );
-		}
-		catch (CannotCompileException e) {
-			throw new RuntimeException( e.getMessage(), e );
-		}
-	}
-
-	private void addFieldHandlerField(ClassFile classfile) throws CannotCompileException {
-		final ConstPool constPool = classfile.getConstPool();
-		final FieldInfo fieldInfo = new FieldInfo( constPool, HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR );
-		fieldInfo.setAccessFlags( AccessFlag.PRIVATE | AccessFlag.TRANSIENT );
-		classfile.addField( fieldInfo );
-	}
-
-	private void addGetFieldHandlerMethod(ClassFile classfile) throws CannotCompileException, BadBytecode {
-		final ConstPool constPool = classfile.getConstPool();
-		final int thisClassInfo = constPool.getThisClassInfo();
-		final MethodInfo getterMethodInfo = new MethodInfo(
-				constPool,
-				GETFIELDHANDLER_METHOD_NAME,
-				GETFIELDHANDLER_METHOD_DESCRIPTOR
-		);
-
-		/* local variable | this | */
-		final Bytecode code = new Bytecode( constPool, 2, 1 );
-		// aload_0 // load this
-		code.addAload( 0 );
-		// getfield // get field "$JAVASSIST_CALLBACK" defined already
-		code.addOpcode( Opcode.GETFIELD );
-		final int fieldIndex = constPool.addFieldrefInfo( thisClassInfo, HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR );
-		code.addIndex( fieldIndex );
-		// areturn // return the value of the field
-		code.addOpcode( Opcode.ARETURN );
-		getterMethodInfo.setCodeAttribute( code.toCodeAttribute() );
-		getterMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
-		final CodeAttribute codeAttribute = getterMethodInfo.getCodeAttribute();
-		if ( codeAttribute != null ) {
-			final StackMapTable smt = MapMaker.make( classPool, getterMethodInfo );
-			codeAttribute.setAttribute( smt );
-		}
-		classfile.addMethod( getterMethodInfo );
-	}
-
-	private void addSetFieldHandlerMethod(ClassFile classfile) throws CannotCompileException, BadBytecode {
-		final ConstPool constPool = classfile.getConstPool();
-		final int thisClassInfo = constPool.getThisClassInfo();
-		final MethodInfo methodInfo = new MethodInfo(
-				constPool,
-				SETFIELDHANDLER_METHOD_NAME,
-				SETFIELDHANDLER_METHOD_DESCRIPTOR
-		);
-
-		/* local variables | this | callback | */
-		final Bytecode code = new Bytecode(constPool, 3, 3);
-		// aload_0 : load this
-		code.addAload( 0 );
-		// aload_1 : load callback
-		code.addAload( 1 );
-		// putfield // put field "$JAVASSIST_CALLBACK" defined already
-		code.addOpcode( Opcode.PUTFIELD );
-		final int fieldIndex = constPool.addFieldrefInfo( thisClassInfo, HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR );
-		code.addIndex( fieldIndex );
-		// return
-		code.addOpcode( Opcode.RETURN );
-		methodInfo.setCodeAttribute( code.toCodeAttribute() );
-		methodInfo.setAccessFlags( AccessFlag.PUBLIC );
-		final CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
-		if ( codeAttribute != null ) {
-			final StackMapTable smt = MapMaker.make( classPool, methodInfo );
-			codeAttribute.setAttribute( smt );
-		}
-		classfile.addMethod( methodInfo );
-	}
-
-	private void addFieldHandledInterface(ClassFile classfile) {
-		final String[] interfaceNames = classfile.getInterfaces();
-		final String[] newInterfaceNames = new String[interfaceNames.length + 1];
-		System.arraycopy( interfaceNames, 0, newInterfaceNames, 0, interfaceNames.length );
-		newInterfaceNames[newInterfaceNames.length - 1] = FIELD_HANDLED_TYPE_NAME;
-		classfile.setInterfaces( newInterfaceNames );
-	}
-
-	private void addReadWriteMethods(ClassFile classfile) throws CannotCompileException, BadBytecode {
-		final List fields = classfile.getFields();
-		for ( Object field : fields ) {
-			final FieldInfo finfo = (FieldInfo) field;
-			if ( (finfo.getAccessFlags() & AccessFlag.STATIC) == 0 && (!finfo.getName().equals( HANDLER_FIELD_NAME )) ) {
-				// case of non-static field
-				if ( filter.handleRead( finfo.getDescriptor(), finfo.getName() ) ) {
-					addReadMethod( classfile, finfo );
-				}
-				if ( filter.handleWrite( finfo.getDescriptor(), finfo.getName() ) ) {
-					addWriteMethod( classfile, finfo );
-				}
-			}
-		}
-	}
-
-	private void addReadMethod(ClassFile classfile, FieldInfo finfo) throws CannotCompileException, BadBytecode {
-		final ConstPool constPool = classfile.getConstPool();
-		final int thisClassInfo = constPool.getThisClassInfo();
-		final String readMethodDescriptor = "()" + finfo.getDescriptor();
-		final MethodInfo readMethodInfo = new MethodInfo(
-				constPool,
-				EACH_READ_METHOD_PREFIX + finfo.getName(),
-				readMethodDescriptor
-		);
-
-		/* local variables | target obj | each oldvalue | */
-		final Bytecode code = new Bytecode(constPool, 5, 3);
-		// aload_0
-		code.addAload( 0 );
-		// getfield // get each field
-		code.addOpcode( Opcode.GETFIELD );
-		final int baseFieldIndex = constPool.addFieldrefInfo( thisClassInfo, finfo.getName(), finfo.getDescriptor() );
-		code.addIndex( baseFieldIndex );
-		// aload_0
-		code.addAload( 0 );
-		// invokeinterface : invoke Enabled.getInterceptFieldCallback()
-		final int enabledClassIndex = constPool.addClassInfo( FIELD_HANDLED_TYPE_NAME );
-		code.addInvokeinterface(
-				enabledClassIndex,
-				GETFIELDHANDLER_METHOD_NAME,
-				GETFIELDHANDLER_METHOD_DESCRIPTOR,
-				1
-		);
-		// ifnonnull
-		code.addOpcode( Opcode.IFNONNULL );
-		code.addIndex( 4 );
-		// *return // each type
-		addTypeDependDataReturn( code, finfo.getDescriptor() );
-		// *store_1 // each type
-		addTypeDependDataStore( code, finfo.getDescriptor(), 1 );
-		// aload_0
-		code.addAload( 0 );
-		// invokeinterface // invoke Enabled.getInterceptFieldCallback()
-		code.addInvokeinterface(
-				enabledClassIndex,
-				GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
-				1
-		);
-		// aload_0
-		code.addAload( 0 );
-		// ldc // name of the field
-		code.addLdc( finfo.getName() );
-		// *load_1 // each type
-		addTypeDependDataLoad( code, finfo.getDescriptor(), 1 );
-		// invokeinterface // invoke Callback.read*() // each type
-		addInvokeFieldHandlerMethod(
-				classfile, code, finfo.getDescriptor(),
-				true
-		);
-		// *return // each type
-		addTypeDependDataReturn( code, finfo.getDescriptor() );
-
-		readMethodInfo.setCodeAttribute( code.toCodeAttribute() );
-		readMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
-		final CodeAttribute codeAttribute = readMethodInfo.getCodeAttribute();
-		if ( codeAttribute != null ) {
-			final StackMapTable smt = MapMaker.make( classPool, readMethodInfo );
-			codeAttribute.setAttribute( smt );
-		}
-		classfile.addMethod( readMethodInfo );
-	}
-
-	private void addWriteMethod(ClassFile classfile, FieldInfo finfo) throws CannotCompileException, BadBytecode {
-		final ConstPool constPool = classfile.getConstPool();
-		final int thisClassInfo = constPool.getThisClassInfo();
-		final String writeMethodDescriptor = "(" + finfo.getDescriptor() + ")V";
-		final MethodInfo writeMethodInfo = new MethodInfo(
-				constPool,
-				EACH_WRITE_METHOD_PREFIX+ finfo.getName(),
-				writeMethodDescriptor
-		);
-
-		/* local variables | target obj | each oldvalue | */
-		final Bytecode code = new Bytecode(constPool, 6, 3);
-		// aload_0
-		code.addAload( 0 );
-		// invokeinterface : enabled.getInterceptFieldCallback()
-		final int enabledClassIndex = constPool.addClassInfo( FIELD_HANDLED_TYPE_NAME );
-		code.addInvokeinterface(
-				enabledClassIndex,
-				GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
-				1
-		);
-		// ifnonnull (label1)
-		code.addOpcode( Opcode.IFNONNULL );
-		code.addIndex( 9 );
-		// aload_0
-		code.addAload( 0 );
-		// *load_1
-		addTypeDependDataLoad( code, finfo.getDescriptor(), 1 );
-		// putfield
-		code.addOpcode( Opcode.PUTFIELD );
-		final int baseFieldIndex = constPool.addFieldrefInfo( thisClassInfo, finfo.getName(), finfo.getDescriptor() );
-		code.addIndex( baseFieldIndex );
-		code.growStack( -Descriptor.dataSize( finfo.getDescriptor() ) );
-		// return ;
-		code.addOpcode( Opcode.RETURN );
-		// aload_0
-		code.addAload( 0 );
-		// dup
-		code.addOpcode( Opcode.DUP );
-		// invokeinterface // enabled.getInterceptFieldCallback()
-		code.addInvokeinterface(
-				enabledClassIndex,
-				GETFIELDHANDLER_METHOD_NAME,
-				GETFIELDHANDLER_METHOD_DESCRIPTOR,
-				1
-		);
-		// aload_0
-		code.addAload( 0 );
-		// ldc // field name
-		code.addLdc( finfo.getName() );
-		// aload_0
-		code.addAload( 0 );
-		// getfield // old value of the field
-		code.addOpcode( Opcode.GETFIELD );
-		code.addIndex( baseFieldIndex );
-		code.growStack( Descriptor.dataSize( finfo.getDescriptor() ) - 1 );
-		// *load_1
-		addTypeDependDataLoad( code, finfo.getDescriptor(), 1 );
-		// invokeinterface // callback.write*(..)
-		addInvokeFieldHandlerMethod( classfile, code, finfo.getDescriptor(), false );
-		// putfield // new value of the field
-		code.addOpcode( Opcode.PUTFIELD );
-		code.addIndex( baseFieldIndex );
-		code.growStack( -Descriptor.dataSize( finfo.getDescriptor() ) );
-		// return
-		code.addOpcode( Opcode.RETURN );
-
-		writeMethodInfo.setCodeAttribute( code.toCodeAttribute() );
-		writeMethodInfo.setAccessFlags( AccessFlag.PUBLIC );
-		final CodeAttribute codeAttribute = writeMethodInfo.getCodeAttribute();
-		if ( codeAttribute != null ) {
-			final StackMapTable smt = MapMaker.make( classPool, writeMethodInfo );
-			codeAttribute.setAttribute( smt );
-		}
-		classfile.addMethod( writeMethodInfo );
-	}
-
-	private void transformInvokevirtualsIntoPutAndGetfields(ClassFile classfile) throws CannotCompileException, BadBytecode {
-		for ( Object o : classfile.getMethods() ) {
-			final MethodInfo methodInfo = (MethodInfo) o;
-			final String methodName = methodInfo.getName();
-			if ( methodName.startsWith( EACH_READ_METHOD_PREFIX )
-					|| methodName.startsWith( EACH_WRITE_METHOD_PREFIX )
-					|| methodName.equals( GETFIELDHANDLER_METHOD_NAME )
-					|| methodName.equals( SETFIELDHANDLER_METHOD_NAME ) ) {
-				continue;
-			}
-
-			final CodeAttribute codeAttr = methodInfo.getCodeAttribute();
-			if ( codeAttr == null ) {
-				continue;
-			}
-
-			final CodeIterator iter = codeAttr.iterator();
-			while ( iter.hasNext() ) {
-				int pos = iter.next();
-				pos = transformInvokevirtualsIntoGetfields( classfile, iter, pos );
-				transformInvokevirtualsIntoPutfields( classfile, iter, pos );
-			}
-			final StackMapTable smt = MapMaker.make( classPool, methodInfo );
-			codeAttr.setAttribute( smt );
-		}
-	}
-
-	private int transformInvokevirtualsIntoGetfields(ClassFile classfile, CodeIterator iter, int pos) {
-		final ConstPool constPool = classfile.getConstPool();
-		final int c = iter.byteAt( pos );
-		if ( c != Opcode.GETFIELD ) {
-			return pos;
-		}
-
-		final int index = iter.u16bitAt( pos + 1 );
-		final String fieldName = constPool.getFieldrefName( index );
-		final String className = constPool.getFieldrefClassName( index );
-		if ( !filter.handleReadAccess( className, fieldName ) ) {
-			return pos;
-		}
-
-		final String fieldReaderMethodDescriptor = "()" + constPool.getFieldrefType( index );
-		final int fieldReaderMethodIndex = constPool.addMethodrefInfo(
-				constPool.getThisClassInfo(),
-				EACH_READ_METHOD_PREFIX + fieldName,
-				fieldReaderMethodDescriptor
-		);
-		iter.writeByte( Opcode.INVOKEVIRTUAL, pos );
-		iter.write16bit( fieldReaderMethodIndex, pos + 1 );
-		return pos;
-	}
-
-	private int transformInvokevirtualsIntoPutfields(ClassFile classfile, CodeIterator iter, int pos) {
-		final ConstPool constPool = classfile.getConstPool();
-		final int c = iter.byteAt( pos );
-		if ( c != Opcode.PUTFIELD ) {
-			return pos;
-		}
-
-		final int index = iter.u16bitAt( pos + 1 );
-		final String fieldName = constPool.getFieldrefName( index );
-		final String className = constPool.getFieldrefClassName( index );
-		if ( !filter.handleWriteAccess( className, fieldName ) ) {
-			return pos;
-		}
-
-		final String fieldWriterMethodDescriptor = "(" + constPool.getFieldrefType( index ) + ")V";
-		final int fieldWriterMethodIndex = constPool.addMethodrefInfo(
-				constPool.getThisClassInfo(),
-				EACH_WRITE_METHOD_PREFIX + fieldName,
-				fieldWriterMethodDescriptor
-		);
-		iter.writeByte( Opcode.INVOKEVIRTUAL, pos );
-		iter.write16bit( fieldWriterMethodIndex, pos + 1 );
-		return pos;
-	}
-
-	private static void addInvokeFieldHandlerMethod(
-			ClassFile classfile,
-			Bytecode code,
-			String typeName,
-			boolean isReadMethod) {
-		final ConstPool constPool = classfile.getConstPool();
-		// invokeinterface
-		final int callbackTypeIndex = constPool.addClassInfo( FIELD_HANDLER_TYPE_NAME );
-		if ( ( typeName.charAt( 0 ) == 'L' )
-				&& ( typeName.charAt( typeName.length() - 1 ) == ';' )
-				|| ( typeName.charAt( 0 ) == '[' ) ) {
-			// reference type
-			final int indexOfL = typeName.indexOf( 'L' );
-			String type;
-			if ( indexOfL == 0 ) {
-				// not array
-				type = typeName.substring( 1, typeName.length() - 1 );
-				type = type.replace( '/', '.' );
-			}
-			else if ( indexOfL == -1 ) {
-				// array of primitive type
-				// do nothing
-				type = typeName;
-			}
-			else {
-				// array of reference type
-				type = typeName.replace( '/', '.' );
-			}
-
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readObject",
-						"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
-						4
-				);
-				// checkcast
-				code.addCheckcast( type );
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeObject",
-						"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
-						5
-				);
-				// checkcast
-				code.addCheckcast( type );
-			}
-		}
-		else if ( typeName.equals( "Z" ) ) {
-			// boolean
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readBoolean",
-						"(Ljava/lang/Object;Ljava/lang/String;Z)Z",
-						4
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeBoolean",
-						"(Ljava/lang/Object;Ljava/lang/String;ZZ)Z",
-						5
-				);
-			}
-		}
-		else if ( typeName.equals( "B" ) ) {
-			// byte
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readByte",
-						"(Ljava/lang/Object;Ljava/lang/String;B)B",
-						4
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeByte",
-						"(Ljava/lang/Object;Ljava/lang/String;BB)B",
-						5
-				);
-			}
-		}
-		else if ( typeName.equals( "C" ) ) {
-			// char
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readChar",
-						"(Ljava/lang/Object;Ljava/lang/String;C)C",
-						4
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeChar",
-						"(Ljava/lang/Object;Ljava/lang/String;CC)C",
-						5
-				);
-			}
-		}
-		else if ( typeName.equals( "I" ) ) {
-			// int
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readInt",
-						"(Ljava/lang/Object;Ljava/lang/String;I)I",
-						4
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeInt",
-						"(Ljava/lang/Object;Ljava/lang/String;II)I",
-						5
-				);
-			}
-		}
-		else if ( typeName.equals( "S" ) ) {
-			// short
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readShort",
-						"(Ljava/lang/Object;Ljava/lang/String;S)S",
-						4
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeShort",
-						"(Ljava/lang/Object;Ljava/lang/String;SS)S",
-						5
-				);
-			}
-		}
-		else if ( typeName.equals( "D" ) ) {
-			// double
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readDouble",
-						"(Ljava/lang/Object;Ljava/lang/String;D)D",
-						5
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeDouble",
-						"(Ljava/lang/Object;Ljava/lang/String;DD)D",
-						7
-				);
-			}
-		}
-		else if ( typeName.equals( "F" ) ) {
-			// float
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readFloat",
-						"(Ljava/lang/Object;Ljava/lang/String;F)F",
-						4
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeFloat",
-						"(Ljava/lang/Object;Ljava/lang/String;FF)F",
-						5
-				);
-			}
-		}
-		else if ( typeName.equals( "J" ) ) {
-			// long
-			if ( isReadMethod ) {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"readLong",
-						"(Ljava/lang/Object;Ljava/lang/String;J)J",
-						5
-				);
-			}
-			else {
-				code.addInvokeinterface(
-						callbackTypeIndex,
-						"writeLong",
-						"(Ljava/lang/Object;Ljava/lang/String;JJ)J",
-						7
-				);
-			}
-		}
-		else {
-			// bad type
-			throw new RuntimeException( "bad type: " + typeName );
-		}
-	}
-
-	private static void addTypeDependDataLoad(Bytecode code, String typeName, int i) {
-		if ( typeName.charAt( 0 ) == 'L'
-				&& typeName.charAt( typeName.length() - 1 ) == ';'
-				|| typeName.charAt( 0 ) == '[' ) {
-			// reference type
-			code.addAload( i );
-		}
-		else if ( typeName.equals( "Z" )
-				|| typeName.equals( "B" )
-				|| typeName.equals( "C" )
-				|| typeName.equals( "I" )
-				|| typeName.equals( "S" ) ) {
-			// boolean, byte, char, int, short
-			code.addIload( i );
-		}
-		else if ( typeName.equals( "D" ) ) {
-			// double
-			code.addDload( i );
-		}
-		else if ( typeName.equals( "F" ) ) {
-			// float
-			code.addFload( i );
-		}
-		else if ( typeName.equals( "J" ) ) {
-			// long
-			code.addLload( i );
-		}
-		else {
-			// bad type
-			throw new RuntimeException( "bad type: " + typeName );
-		}
-	}
-
-	private static void addTypeDependDataStore(Bytecode code, String typeName, int i) {
-		if ( typeName.charAt( 0 ) == 'L'
-				&& typeName.charAt( typeName.length() - 1 ) == ';'
-				|| typeName.charAt( 0 ) == '[' ) {
-			// reference type
-			code.addAstore( i );
-		}
-		else if ( typeName.equals( "Z" )
-				|| typeName.equals( "B" )
-				|| typeName.equals( "C" )
-				|| typeName.equals( "I" )
-				|| typeName.equals( "S" ) ) {
-			// boolean, byte, char, int, short
-			code.addIstore( i );
-		}
-		else if ( typeName.equals( "D" ) ) {
-			// double
-			code.addDstore( i );
-		}
-		else if ( typeName.equals( "F" ) ) {
-			// float
-			code.addFstore( i );
-		}
-		else if ( typeName.equals( "J" ) ) {
-			// long
-			code.addLstore( i );
-		}
-		else {
-			// bad type
-			throw new RuntimeException( "bad type: " + typeName );
-		}
-	}
-
-	private static void addTypeDependDataReturn(Bytecode code, String typeName) {
-		if ( typeName.charAt( 0 ) == 'L'
-				&& typeName.charAt( typeName.length() - 1 ) == ';'
-				|| typeName.charAt( 0 ) == '[') {
-			// reference type
-			code.addOpcode( Opcode.ARETURN );
-		}
-		else if ( typeName.equals( "Z" )
-				|| typeName.equals( "B" )
-				|| typeName.equals( "C" )
-				|| typeName.equals( "I" )
-				|| typeName.equals( "S" ) ) {
-			// boolean, byte, char, int, short
-			code.addOpcode( Opcode.IRETURN );
-		}
-		else if ( typeName.equals( "D" ) ) {
-			// double
-			code.addOpcode( Opcode.DRETURN );
-		}
-		else if ( typeName.equals( "F" ) ) {
-			// float
-			code.addOpcode( Opcode.FRETURN );
-		}
-		else if ( typeName.equals( "J" ) ) {
-			// long
-			code.addOpcode( Opcode.LRETURN );
-		}
-		else {
-			// bad type
-			throw new RuntimeException( "bad type: " + typeName );
-		}
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
deleted file mode 100644
index 7df661fbc5..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
+++ /dev/null
@@ -1,145 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.internal.javassist;
-
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.DataInputStream;
-import java.io.DataOutputStream;
-import java.io.IOException;
-import java.security.ProtectionDomain;
-
-import javassist.ClassClassPath;
-import javassist.ClassPool;
-import javassist.bytecode.ClassFile;
-
-import org.hibernate.HibernateException;
-import org.hibernate.bytecode.buildtime.spi.ClassFilter;
-import org.hibernate.bytecode.spi.AbstractClassTransformerImpl;
-import org.hibernate.internal.CoreMessageLogger;
-
-import org.jboss.logging.Logger;
-
-/**
- * Enhance the classes allowing them to implements InterceptFieldEnabled
- * This interface is then used by Hibernate for some optimizations.
- *
- * @author Emmanuel Bernard
- * @author Steve Ebersole
- * @author Dustin Schultz
- */
-public class JavassistClassTransformer extends AbstractClassTransformerImpl {
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			JavassistClassTransformer.class.getName()
-	);
-
-	/**
-	 * Constructs the JavassistClassTransformer
-	 *
-	 * @param classFilter The filter used to determine which classes to transform
-	 * @param fieldFilter The filter used to determine which fields to transform
-	 */
-	public JavassistClassTransformer(ClassFilter classFilter, org.hibernate.bytecode.buildtime.spi.FieldFilter fieldFilter) {
-		super( classFilter, fieldFilter );
-	}
-
-	@Override
-	protected byte[] doTransform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer) {
-		ClassFile classfile;
-		try {
-			// WARNING: classfile only
-			classfile = new ClassFile( new DataInputStream( new ByteArrayInputStream( classfileBuffer ) ) );
-		}
-		catch (IOException e) {
-			LOG.unableToBuildEnhancementMetamodel( className );
-			return classfileBuffer;
-		}
-
-		final ClassPool cp = new ClassPool();
-		cp.appendSystemPath();
-		cp.appendClassPath( new ClassClassPath( this.getClass() ) );
-		cp.appendClassPath( new ClassClassPath( classfile.getClass() ) );
-
-		try {
-			cp.makeClassIfNew( new ByteArrayInputStream( classfileBuffer ) );
-		}
-		catch (IOException e) {
-			throw new RuntimeException( e.getMessage(), e );
-		}
-
-		final FieldTransformer transformer = getFieldTransformer( classfile, cp );
-		if ( transformer != null ) {
-			LOG.debugf( "Enhancing %s", className );
-
-			DataOutputStream out = null;
-			try {
-				transformer.transform( classfile );
-				final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
-				out = new DataOutputStream( byteStream );
-				classfile.write( out );
-				return byteStream.toByteArray();
-			}
-			catch (Exception e) {
-				LOG.unableToTransformClass( e.getMessage() );
-				throw new HibernateException( "Unable to transform class: " + e.getMessage() );
-			}
-			finally {
-				try {
-					if ( out != null ) {
-						out.close();
-					}
-				}
-				catch (IOException e) {
-					//swallow
-				}
-			}
-		}
-		return classfileBuffer;
-	}
-
-	protected FieldTransformer getFieldTransformer(final ClassFile classfile, final ClassPool classPool) {
-		if ( alreadyInstrumented( classfile ) ) {
-			return null;
-		}
-		return new FieldTransformer(
-				new FieldFilter() {
-					public boolean handleRead(String desc, String name) {
-						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
-					}
-
-					public boolean handleWrite(String desc, String name) {
-						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
-					}
-
-					public boolean handleReadAccess(String fieldOwnerClassName, String fieldName) {
-						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
-					}
-
-					public boolean handleWriteAccess(String fieldOwnerClassName, String fieldName) {
-						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
-					}
-				},
-				classPool
-		);
-	}
-
-	private boolean alreadyInstrumented(ClassFile classfile) {
-		final String[] interfaces = classfile.getInterfaces();
-		for ( String anInterface : interfaces ) {
-			if ( FieldHandled.class.getName().equals( anInterface ) ) {
-				return true;
-			}
-		}
-		return false;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
deleted file mode 100644
index c12c49354a..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
+++ /dev/null
@@ -1,70 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.internal.javassist;
-
-import java.io.IOException;
-
-import javassist.CannotCompileException;
-import javassist.ClassPool;
-import javassist.CtClass;
-import javassist.NotFoundException;
-
-import org.hibernate.HibernateException;
-
-/**
- * A ClassLoader implementation applying Class transformations as they are being loaded.
- *
- * @author Steve Ebersole
- */
-@SuppressWarnings("UnusedDeclaration")
-public class TransformingClassLoader extends ClassLoader {
-	private ClassLoader parent;
-	private ClassPool classPool;
-
-	TransformingClassLoader(ClassLoader parent, String[] classpaths) {
-		this.parent = parent;
-		this.classPool = new ClassPool( true );
-		for ( String classpath : classpaths ) {
-			try {
-				classPool.appendClassPath( classpath );
-			}
-			catch (NotFoundException e) {
-				throw new HibernateException(
-						"Unable to resolve requested classpath for transformation [" +
-								classpath + "] : " + e.getMessage()
-				);
-			}
-		}
-	}
-
-	@Override
-	protected Class findClass(String name) throws ClassNotFoundException {
-		try {
-			final CtClass cc = classPool.get( name );
-			// todo : modify the class definition if not already transformed...
-			final byte[] b = cc.toBytecode();
-			return defineClass( name, b, 0, b.length );
-		}
-		catch (NotFoundException e) {
-			throw new ClassNotFoundException();
-		}
-		catch (IOException e) {
-			throw new ClassNotFoundException();
-		}
-		catch (CannotCompileException e) {
-			throw new ClassNotFoundException();
-		}
-	}
-
-	/**
-	 * Used to release resources.  Call when done with the ClassLoader
-	 */
-	public void release() {
-		classPool = null;
-		parent = null;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
deleted file mode 100644
index bdbecd7dcd..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
+++ /dev/null
@@ -1,63 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.spi;
-
-import java.security.ProtectionDomain;
-
-import org.hibernate.bytecode.buildtime.spi.ClassFilter;
-import org.hibernate.bytecode.buildtime.spi.FieldFilter;
-
-/**
- * Basic implementation of the {@link ClassTransformer} contract.
- *
- * @author Emmanuel Bernard
- * @author Steve Ebersole
- */
-public abstract class AbstractClassTransformerImpl implements ClassTransformer {
-	protected final ClassFilter classFilter;
-	protected final FieldFilter fieldFilter;
-
-	protected AbstractClassTransformerImpl(ClassFilter classFilter, FieldFilter fieldFilter) {
-		this.classFilter = classFilter;
-		this.fieldFilter = fieldFilter;
-	}
-
-	@Override
-	public byte[] transform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer) {
-		// to be safe...
-		className = className.replace( '/', '.' );
-		if ( classFilter.shouldInstrumentClass( className ) ) {
-			return doTransform( loader, className, classBeingRedefined, protectionDomain, classfileBuffer );
-		}
-		else {
-			return classfileBuffer;
-		}
-	}
-
-	/**
-	 * Delegate the transformation call from {@link #transform}
-	 *
-	 * @param loader The class loader to use
-	 * @param className The name of the class to transform
-	 * @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
-	 * @param protectionDomain The protection domain of the class being (re)defined
-	 * @param classfileBuffer The bytes of the class file.
-	 *
-	 * @return The transformed (enhanced/instrumented) bytes.
-	 */
-	protected abstract byte[] doTransform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java
new file mode 100644
index 0000000000..76bb75203f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeEnhancementMetadata.java
@@ -0,0 +1,61 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.bytecode.spi;
+
+import java.util.Set;
+
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * Encapsulates bytecode enhancement information about a particular entity.
+ *
+ * @author Steve Ebersole
+ */
+public interface BytecodeEnhancementMetadata {
+	/**
+	 * The name of the entity to which this metadata applies.
+	 *
+	 * @return The entity name
+	 */
+	String getEntityName();
+
+	/**
+	 * Has the entity class been bytecode enhanced for lazy loading?
+	 *
+	 * @return {@code true} indicates the entity class is enhanced for Hibernate use
+	 * in lazy loading; {@code false} indicates it is not
+	 */
+	boolean isEnhancedForLazyLoading();
+
+	/**
+	 * Build and inject an interceptor instance into the enhanced entity.
+	 *
+	 * @param entity The entity into which built interceptor should be injected
+	 * @param uninitializedFieldNames The name of fields marked as lazy
+	 * @param session The session to which the entity instance belongs.
+	 *
+	 * @return The built and injected interceptor
+	 *
+	 * @throws NotInstrumentedException Thrown if {@link #isEnhancedForLazyLoading()} returns {@code false}
+	 */
+	LazyAttributeLoadingInterceptor injectInterceptor(
+			Object entity,
+			Set<String> uninitializedFieldNames,
+			SessionImplementor session) throws NotInstrumentedException;
+
+	/**
+	 * Extract the field interceptor instance from the enhanced entity.
+	 *
+	 * @param entity The entity from which to extract the interceptor
+	 *
+	 * @return The extracted interceptor
+	 *
+	 * @throws NotInstrumentedException Thrown if {@link #isEnhancedForLazyLoading()} returns {@code false}
+	 */
+	LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java
index 18c9f2bb5c..562e3554df 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java
@@ -1,64 +1,39 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.spi;
 
-import org.hibernate.bytecode.buildtime.spi.ClassFilter;
-import org.hibernate.bytecode.buildtime.spi.FieldFilter;
-
 /**
  * Contract for providers of bytecode services to Hibernate.
  * <p/>
- * Bytecode requirements break down into basically 3 areas<ol>
+ * Bytecode requirements break down into the following areas<ol>
  *     <li>proxy generation (both for runtime-lazy-loading and basic proxy generation) {@link #getProxyFactoryFactory()}</li>
  *     <li>bean reflection optimization {@link #getReflectionOptimizer}</li>
- *     <li>field-access instrumentation {@link #getTransformer}</li>
  * </ol>
  *
  * @author Steve Ebersole
  */
 public interface BytecodeProvider {
 	/**
 	 * Retrieve the specific factory for this provider capable of
 	 * generating run-time proxies for lazy-loading purposes.
 	 *
 	 * @return The provider specific factory.
 	 */
-	public ProxyFactoryFactory getProxyFactoryFactory();
+	ProxyFactoryFactory getProxyFactoryFactory();
 
 	/**
 	 * Retrieve the ReflectionOptimizer delegate for this provider
 	 * capable of generating reflection optimization components.
 	 *
 	 * @param clazz The class to be reflected upon.
 	 * @param getterNames Names of all property getters to be accessed via reflection.
 	 * @param setterNames Names of all property setters to be accessed via reflection.
 	 * @param types The types of all properties to be accessed.
 	 * @return The reflection optimization delegate.
 	 */
-	public ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types);
-
-	/**
-	 * Generate a ClassTransformer capable of performing bytecode manipulation.
-	 *
-	 * @param classFilter filter used to limit which classes are to be instrumented
-	 * via this ClassTransformer.
-	 * @param fieldFilter filter used to limit which fields are to be instrumented
-	 * via this ClassTransformer.
-	 * @return The appropriate ClassTransformer.
-	 */
-	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter);
-
-	/**
-	 * Retrieve the interception metadata for the particular entity type.
-	 *
-	 * @param entityClass The entity class.  Note: we pass class here instead of the usual "entity name" because
-	 * only real classes can be instrumented.
-	 *
-	 * @return The metadata
-	 */
-	public EntityInstrumentationMetadata getEntityInstrumentationMetadata(Class entityClass);
+	ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
index 6d106d95f6..ecbb8a6c89 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
@@ -1,38 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.bytecode.spi;
 
+import java.lang.instrument.IllegalClassFormatException;
 import java.security.ProtectionDomain;
 
 /**
  * A persistence provider provides an instance of this interface
  * to the PersistenceUnitInfo.addTransformer method.
  * The supplied transformer instance will get called to transform
  * entity class files when they are loaded and redefined.  The transformation
  * occurs before the class is defined by the JVM
  *
  * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
  * @author Emmanuel Bernard
  */
-public interface ClassTransformer {
+public interface ClassTransformer extends javax.persistence.spi.ClassTransformer {
 	/**
 	 * Invoked when a class is being loaded or redefined to add hooks for persistence bytecode manipulation.
 	 *
 	 * @param loader the defining class loaderof the class being transformed.  It may be null if using bootstrap loader
-	 * @param classname The name of the class being transformed
+	 * @param className The name of the class being transformed
 	 * @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
 	 * @param protectionDomain ProtectionDomain of the class being (re)-defined
 	 * @param classfileBuffer The input byte buffer in class file format
 	 * @return A well-formed class file that can be loaded
 	 */
+	@Override
 	public byte[] transform(
 			ClassLoader loader,
-			String classname,
-			Class classBeingRedefined,
+			String className,
+			Class<?> classBeingRedefined,
 			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer);
+			byte[] classfileBuffer) throws IllegalClassFormatException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java
deleted file mode 100644
index d3be93e236..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/spi/EntityInstrumentationMetadata.java
+++ /dev/null
@@ -1,63 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.bytecode.spi;
-
-import java.util.Set;
-
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.engine.spi.SessionImplementor;
-
-/**
- * Encapsulates bytecode instrumentation information about a particular entity.
- *
- * @author Steve Ebersole
- */
-public interface EntityInstrumentationMetadata {
-	/**
-	 * The name of the entity to which this metadata applies.
-	 *
-	 * @return The entity name
-	 */
-	public String getEntityName();
-
-	/**
-	 * Has the entity class been bytecode instrumented?
-	 *
-	 * @return {@code true} indicates the entity class is instrumented for Hibernate use; {@code false}
-	 * indicates it is not
-	 */
-	public boolean isInstrumented();
-
-	/**
-	 * Build and inject a field interceptor instance into the instrumented entity.
-	 *
-	 * @param entity The entity into which built interceptor should be injected
-	 * @param entityName The name of the entity
-	 * @param uninitializedFieldNames The name of fields marked as lazy
-	 * @param session The session to which the entity instance belongs.
-	 *
-	 * @return The built and injected interceptor
-	 *
-	 * @throws NotInstrumentedException Thrown if {@link #isInstrumented()} returns {@code false}
-	 */
-	public FieldInterceptor injectInterceptor(
-			Object entity,
-			String entityName,
-			Set uninitializedFieldNames,
-			SessionImplementor session) throws NotInstrumentedException;
-
-	/**
-	 * Extract the field interceptor instance from the instrumented entity.
-	 *
-	 * @param entity The entity from which to extract the interceptor
-	 *
-	 * @return The extracted interceptor
-	 *
-	 * @throws NotInstrumentedException Thrown if {@link #isInstrumented()} returns {@code false}
-	 */
-	public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException;
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java
index b9c5a13b7c..0b477975d5 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java
@@ -1,647 +1,634 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.IOException;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryExtraState;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * A base implementation of EntityEntry
  *
  * @author Gavin King
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  * @author Gunnar Morling
  * @author Sanne Grinovero  <sanne@hibernate.org>
  */
 public abstract class AbstractEntityEntry implements Serializable, EntityEntry {
 	protected final Serializable id;
 	protected Object[] loadedState;
 	protected Object version;
 	protected final EntityPersister persister; // permanent but we only need the entityName state in a non transient way
 	protected transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
 	protected final transient Object rowId;
 	protected final transient PersistenceContext persistenceContext;
 	protected EntityEntryExtraState next;
 
 	/**
 	 * Holds several boolean and enum typed attributes in a very compact manner. Enum values are stored in 4 bits
 	 * (where 0 represents {@code null}, and each enum value is represented by its ordinal value + 1), thus allowing
 	 * for up to 15 values per enum. Boolean values are stored in one bit.
 	 * <p>
 	 * The value is structured as follows:
 	 *
 	 * <pre>
 	 * 1 - Lock mode
 	 * 2 - Status
 	 * 3 - Previous Status
 	 * 4 - existsInDatabase
 	 * 5 - isBeingReplicated
 	 * 6 - loadedWithLazyPropertiesUnfetched; NOTE: this is not updated when properties are fetched lazily!
 	 *
 	 * 0000 0000 | 0000 0000 | 0654 3333 | 2222 1111
 	 * </pre>
 	 * Use {@link #setCompressedValue(org.hibernate.engine.internal.AbstractEntityEntry.EnumState, Enum)},
 	 * {@link #getCompressedValue(org.hibernate.engine.internal.AbstractEntityEntry.EnumState)} etc
 	 * to access the enums and booleans stored in this value.
 	 * <p>
 	 * Representing enum values by their ordinal value is acceptable for our case as this value itself is never
 	 * serialized or deserialized and thus is not affected should ordinal values change.
 	 */
 	private transient int compressedState;
 
 	/**
 	 * @deprecated the tenantId and entityMode parameters where removed: this constructor accepts but ignores them.
 	 * Use the other constructor!
 	 */
 	@Deprecated
 	public AbstractEntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
 			final String tenantId,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
 		this( status, loadedState, rowId, id, version, lockMode, existsInDatabase,
 				persister,disableVersionIncrement, lazyPropertiesAreUnfetched, persistenceContext );
 	}
 
 	public AbstractEntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
 		setCompressedValue( EnumState.STATUS, status );
 		// not useful strictly speaking but more explicit
 		setCompressedValue( EnumState.PREVIOUS_STATUS, null );
 		// only retain loaded state if the status is not Status.READ_ONLY
 		if ( status != Status.READ_ONLY ) {
 			this.loadedState = loadedState;
 		}
 		this.id=id;
 		this.rowId=rowId;
 		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, existsInDatabase );
 		this.version=version;
 		setCompressedValue( EnumState.LOCK_MODE, lockMode );
 		setCompressedValue( BooleanState.IS_BEING_REPLICATED, disableVersionIncrement );
 		setCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED, lazyPropertiesAreUnfetched );
 		this.persister=persister;
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * This for is used during custom deserialization handling
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	protected AbstractEntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 			final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
 		this.id = id;
 		setCompressedValue( EnumState.STATUS, status );
 		setCompressedValue( EnumState.PREVIOUS_STATUS, previousStatus );
 		this.loadedState = loadedState;
 		setDeletedState( deletedState );
 		this.version = version;
 		setCompressedValue( EnumState.LOCK_MODE, lockMode );
 		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, existsInDatabase );
 		setCompressedValue( BooleanState.IS_BEING_REPLICATED, isBeingReplicated );
 		setCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED, loadedWithLazyPropertiesUnfetched );
 		this.rowId = null; // this is equivalent to the old behavior...
 		this.persistenceContext = persistenceContext;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return getCompressedValue( EnumState.LOCK_MODE );
 	}
 
 	@Override
 	public void setLockMode(LockMode lockMode) {
 		setCompressedValue( EnumState.LOCK_MODE, lockMode );
 	}
 
 
 	@Override
 	public Status getStatus() {
 		return getCompressedValue( EnumState.STATUS );
 	}
 
 	private Status getPreviousStatus() {
 		return getCompressedValue( EnumState.PREVIOUS_STATUS );
 	}
 
 	@Override
 	public void setStatus(Status status) {
 		if ( status == Status.READ_ONLY ) {
 			//memory optimization
 			loadedState = null;
 		}
 
 		final Status currentStatus = this.getStatus();
 
 		if ( currentStatus != status ) {
 			setCompressedValue( EnumState.PREVIOUS_STATUS, currentStatus );
 			setCompressedValue( EnumState.STATUS, status );
 		}
 	}
 
 	@Override
 	public Serializable getId() {
 		return id;
 	}
 
 	@Override
 	public Object[] getLoadedState() {
 		return loadedState;
 	}
 
 	private static final Object[] DEFAULT_DELETED_STATE = null;
 
 	@Override
 	public Object[] getDeletedState() {
 		final EntityEntryExtraStateHolder extra = getExtraState( EntityEntryExtraStateHolder.class );
 		return extra != null ? extra.getDeletedState() : DEFAULT_DELETED_STATE;
 	}
 
 	@Override
 	public void setDeletedState(Object[] deletedState) {
 		EntityEntryExtraStateHolder extra = getExtraState( EntityEntryExtraStateHolder.class );
 		if ( extra == null && deletedState == DEFAULT_DELETED_STATE ) {
 			//this is the default value and we do not store the extra state
 			return;
 		}
 		if ( extra == null ) {
 			extra = new EntityEntryExtraStateHolder();
 			addExtraState( extra );
 		}
 		extra.setDeletedState( deletedState );
 	}
 
 	@Override
 	public boolean isExistsInDatabase() {
 		return getCompressedValue( BooleanState.EXISTS_IN_DATABASE );
 	}
 
 	@Override
 	public Object getVersion() {
 		return version;
 	}
 
 	@Override
 	public EntityPersister getPersister() {
 		return persister;
 	}
 
 	@Override
 	public EntityKey getEntityKey() {
 		if ( cachedEntityKey == null ) {
 			if ( getId() == null ) {
 				throw new IllegalStateException( "cannot generate an EntityKey when id is null.");
 			}
 			cachedEntityKey = new EntityKey( getId(), getPersister() );
 		}
 		return cachedEntityKey;
 	}
 
 	@Override
 	public String getEntityName() {
 		return persister == null ? null : persister.getEntityName();
 
 	}
 
 	@Override
 	public boolean isBeingReplicated() {
 		return getCompressedValue( BooleanState.IS_BEING_REPLICATED );
 	}
 
 	@Override
 	public Object getRowId() {
 		return rowId;
 	}
 
 	@Override
 	public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
 		this.loadedState = updatedState;
 		setLockMode( LockMode.WRITE );
 
 		if ( getPersister().isVersioned() ) {
 			this.version = nextVersion;
 			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 		}
 
-		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
-			final FieldInterceptor interceptor = getPersister().getInstrumentationMetadata().extractInterceptor( entity );
-			if ( interceptor != null ) {
-				interceptor.clearDirty();
-			}
-		}
-
 		if( entity instanceof SelfDirtinessTracker ) {
 			( (SelfDirtinessTracker) entity ).$$_hibernate_clearDirtyAttributes();
 		}
 
 		getPersistenceContext().getSession()
 				.getFactory()
 				.getCustomEntityDirtinessStrategy()
 				.resetDirty( entity, getPersister(), (Session) getPersistenceContext().getSession() );
 	}
 
 	@Override
 	public void postDelete() {
 		setCompressedValue( EnumState.PREVIOUS_STATUS, getStatus() );
 		setCompressedValue( EnumState.STATUS, Status.GONE );
 		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, false );
 	}
 
 	@Override
 	public void postInsert(Object[] insertedState) {
 		setCompressedValue( BooleanState.EXISTS_IN_DATABASE, true );
 	}
 
 	@Override
 	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
 		if ( getStatus() == Status.SAVING ) {
 			return true;
 		}
 		else if ( earlyInsert ) {
 			return !isExistsInDatabase();
 		}
 		else {
 			return session.getPersistenceContext().getNullifiableEntityKeys().contains( getEntityKey() );
 		}
 	}
 
 	@Override
 	public Object getLoadedValue(String propertyName) {
 		if ( loadedState == null || propertyName == null ) {
 			return null;
 		}
 		else {
 			final int propertyIndex = ( (UniqueKeyLoadable) persister ).getPropertyIndex( propertyName );
 			return loadedState[propertyIndex];
 		}
 	}
 
 	@Override
 	public boolean requiresDirtyCheck(Object entity) {
 		return isModifiableEntity()
 				&& ( !isUnequivocallyNonDirty( entity ) );
 	}
 
 	@SuppressWarnings( {"SimplifiableIfStatement"})
 	private boolean isUnequivocallyNonDirty(Object entity) {
-		if (entity instanceof SelfDirtinessTracker) {
+		if ( entity instanceof SelfDirtinessTracker ) {
 			return ! ( (SelfDirtinessTracker) entity ).$$_hibernate_hasDirtyAttributes();
 		}
 
 		final CustomEntityDirtinessStrategy customEntityDirtinessStrategy =
 				getPersistenceContext().getSession().getFactory().getCustomEntityDirtinessStrategy();
 		if ( customEntityDirtinessStrategy.canDirtyCheck( entity, getPersister(), (Session) getPersistenceContext().getSession() ) ) {
 			return ! customEntityDirtinessStrategy.isDirty( entity, getPersister(), (Session) getPersistenceContext().getSession() );
 		}
 
 		if ( getPersister().hasMutableProperties() ) {
 			return false;
 		}
 
-		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
-			// the entity must be instrumented (otherwise we cant check dirty flag) and the dirty flag is false
-			return ! getPersister().getInstrumentationMetadata().extractInterceptor( entity ).isDirty();
-		}
-
 		return false;
 	}
 
 	@Override
 	public boolean isModifiableEntity() {
 		final Status status = getStatus();
 		final Status previousStatus = getPreviousStatus();
 		return getPersister().isMutable()
 				&& status != Status.READ_ONLY
 				&& ! ( status == Status.DELETED && previousStatus == Status.READ_ONLY );
 	}
 
 	@Override
 	public void forceLocked(Object entity, Object nextVersion) {
 		version = nextVersion;
 		loadedState[ persister.getVersionProperty() ] = version;
 		// TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
 		//noinspection deprecation
 		setLockMode( LockMode.FORCE );
 		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 	}
 
 	@Override
 	public boolean isReadOnly() {
 		final Status status = getStatus();
 		if (status != Status.MANAGED && status != Status.READ_ONLY) {
 			throw new HibernateException("instance was not in a valid state");
 		}
 		return status == Status.READ_ONLY;
 	}
 
 	@Override
 	public void setReadOnly(boolean readOnly, Object entity) {
 		if ( readOnly == isReadOnly() ) {
 			// simply return since the status is not being changed
 			return;
 		}
 		if ( readOnly ) {
 			setStatus( Status.READ_ONLY );
 			loadedState = null;
 		}
 		else {
 			if ( ! persister.isMutable() ) {
 				throw new IllegalStateException( "Cannot make an immutable entity modifiable." );
 			}
 			setStatus( Status.MANAGED );
 			loadedState = getPersister().getPropertyValues( entity );
 			getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 					persister,
 					id,
 					loadedState,
 					null,
 					CachedNaturalIdValueSource.LOAD
 			);
 		}
 	}
 
 	@Override
 	public String toString() {
 		return "EntityEntry" +
 				MessageHelper.infoString( getPersister().getEntityName(), id ) +
 				'(' + getStatus() + ')';
 	}
 
 	@Override
 	public boolean isLoadedWithLazyPropertiesUnfetched() {
 		return getCompressedValue( BooleanState.LOADED_WITH_LAZY_PROPERTIES_UNFETCHED );
 	}
 
 	@Override
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		final Status previousStatus = getPreviousStatus();
 		oos.writeObject( getEntityName() );
 		oos.writeObject( id );
 		oos.writeObject( getStatus().name() );
 		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
 		// todo : potentially look at optimizing these two arrays
 		oos.writeObject( loadedState );
 		oos.writeObject( getDeletedState() );
 		oos.writeObject( version );
 		oos.writeObject( getLockMode().toString() );
 		oos.writeBoolean( isExistsInDatabase() );
 		oos.writeBoolean( isBeingReplicated() );
 		oos.writeBoolean( isLoadedWithLazyPropertiesUnfetched() );
 	}
 
 
 	@Override
 	public void addExtraState(EntityEntryExtraState extraState) {
 		if ( next == null ) {
 			next = extraState;
 		}
 		else {
 			next.addExtraState( extraState );
 		}
 	}
 
 	@Override
 	public <T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType) {
 		if ( next == null ) {
 			return null;
 		}
 		if ( extraStateType.isAssignableFrom( next.getClass() ) ) {
 			return (T) next;
 		}
 		else {
 			return next.getExtraState( extraStateType );
 		}
 	}
 
 	public PersistenceContext getPersistenceContext(){
 		return persistenceContext;
 	}
 
 	/**
 	 * Saves the value for the given enum property.
 	 *
 	 * @param state
 	 *            identifies the value to store
 	 * @param value
 	 *            the value to store; The caller must make sure that it matches
 	 *            the given identifier
 	 */
 	protected <E extends Enum<E>> void setCompressedValue(EnumState<E> state, E value) {
 		// reset the bits for the given property to 0
 		compressedState &= state.getUnsetMask();
 		// store the numeric representation of the enum value at the right offset
 		compressedState |= ( state.getValue( value ) << state.getOffset() );
 	}
 
 	/**
 	 * Gets the current value of the given enum property.
 	 *
 	 * @param state
 	 *            identifies the value to store
 	 * @return the current value of the specified property
 	 */
 	protected <E extends Enum<E>> E getCompressedValue(EnumState<E> state) {
 		// restore the numeric value from the bits at the right offset and return the corresponding enum constant
 		final int index = ( ( compressedState & state.getMask() ) >> state.getOffset() ) - 1;
 		return index == - 1 ? null : state.getEnumConstants()[index];
 	}
 
 	/**
 	 * Saves the value for the given boolean flag.
 	 *
 	 * @param state
 	 *            identifies the value to store
 	 * @param value
 	 *            the value to store
 	 */
 	protected void setCompressedValue(BooleanState state, boolean value) {
 		compressedState &= state.getUnsetMask();
 		compressedState |= ( state.getValue( value ) << state.getOffset() );
 	}
 
 	/**
 	 * Gets the current value of the given boolean flag.
 	 *
 	 * @param state
 	 *            identifies the value to store
 	 * @return the current value of the specified flag
 	 */
 	protected boolean getCompressedValue(BooleanState state) {
 		return ( ( compressedState & state.getMask() ) >> state.getOffset() ) == 1;
 	}
 
 	/**
 	 * Represents an enum value stored within a number value, using four bits starting at a specified offset.
 	 *
 	 * @author Gunnar Morling
 	 */
 	protected static class EnumState<E extends Enum<E>> {
 
 		protected static final EnumState<LockMode> LOCK_MODE = new EnumState<LockMode>( 0, LockMode.class );
 		protected static final EnumState<Status> STATUS = new EnumState<Status>( 4, Status.class );
 		protected static final EnumState<Status> PREVIOUS_STATUS = new EnumState<Status>( 8, Status.class );
 
 		protected final int offset;
 		protected final E[] enumConstants;
 		protected final int mask;
 		protected final int unsetMask;
 
 		private EnumState(int offset, Class<E> enumType) {
 			final E[] enumConstants = enumType.getEnumConstants();
 
 			// In case any of the enums cannot be stored in 4 bits anymore, we'd have to re-structure the compressed
 			// state int
 			if ( enumConstants.length > 15 ) {
 				throw new AssertionFailure( "Cannot store enum type " + enumType.getName() + " in compressed state as"
 						+ " it has too many values." );
 			}
 
 			this.offset = offset;
 			this.enumConstants = enumConstants;
 
 			// a mask for reading the four bits, starting at the right offset
 			this.mask = 0xF << offset;
 
 			// a mask for setting the four bits at the right offset to 0
 			this.unsetMask = 0xFFFF & ~mask;
 		}
 
 		/**
 		 * Returns the numeric value to be stored for the given enum value.
 		 */
 		private int getValue(E value) {
 			return value != null ? value.ordinal() + 1 : 0;
 		}
 
 		/**
 		 * Returns the offset within the number value at which this enum value is stored.
 		 */
 		private int getOffset() {
 			return offset;
 		}
 
 		/**
 		 * Returns the bit mask for reading this enum value from the number value storing it.
 		 */
 		private int getMask() {
 			return mask;
 		}
 
 		/**
 		 * Returns the bit mask for resetting this enum value from the number value storing it.
 		 */
 		private int getUnsetMask() {
 			return unsetMask;
 		}
 
 		/**
 		 * Returns the constants of the represented enum which is cached for performance reasons.
 		 */
 		private E[] getEnumConstants() {
 			return enumConstants;
 		}
 	}
 
 	/**
 	 * Represents a boolean flag stored within a number value, using one bit at a specified offset.
 	 *
 	 * @author Gunnar Morling
 	 */
 	protected enum BooleanState {
 
 		EXISTS_IN_DATABASE(13),
 		IS_BEING_REPLICATED(14),
 		LOADED_WITH_LAZY_PROPERTIES_UNFETCHED(15);
 
 		private final int offset;
 		private final int mask;
 		private final int unsetMask;
 
 		private BooleanState(int offset) {
 			this.offset = offset;
 			this.mask = 0x1 << offset;
 			this.unsetMask = 0xFFFF & ~mask;
 		}
 
 		private int getValue(boolean value) {
 			return value ? 1 : 0;
 		}
 
 		/**
 		 * Returns the offset within the number value at which this boolean flag is stored.
 		 */
 		private int getOffset() {
 			return offset;
 		}
 
 		/**
 		 * Returns the bit mask for reading this flag from the number value storing it.
 		 */
 		private int getMask() {
 			return mask;
 		}
 
 		/**
 		 * Returns the bit mask for resetting this flag from the number value storing it.
 		 */
 		private int getUnsetMask() {
 			return unsetMask;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
index 9d6c010752..eca4719d1b 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
@@ -1,383 +1,383 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.TransientObjectException;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Algorithms related to foreign key constraint transparency
  *
  * @author Gavin King
  */
 public final class ForeignKeys {
 
 	/**
 	 * Delegate for handling nullifying ("null"ing-out) non-cascaded associations
 	 */
 	public static class Nullifier {
 		private final boolean isDelete;
 		private final boolean isEarlyInsert;
 		private final SessionImplementor session;
 		private final Object self;
 
 		/**
 		 * Constructs a Nullifier
 		 *
 		 * @param self The entity
 		 * @param isDelete Are we in the middle of a delete action?
 		 * @param isEarlyInsert Is this an early insert (INSERT generated id strategy)?
 		 * @param session The session
 		 */
 		public Nullifier(Object self, boolean isDelete, boolean isEarlyInsert, SessionImplementor session) {
 			this.isDelete = isDelete;
 			this.isEarlyInsert = isEarlyInsert;
 			this.session = session;
 			this.self = self;
 		}
 
 		/**
 		 * Nullify all references to entities that have not yet been inserted in the database, where the foreign key
 		 * points toward that entity.
 		 *
 		 * @param values The entity attribute values
 		 * @param types The entity attribute types
 		 */
 		public void nullifyTransientReferences(final Object[] values, final Type[] types) {
 			for ( int i = 0; i < types.length; i++ ) {
 				values[i] = nullifyTransientReferences( values[i], types[i] );
 			}
 		}
 
 		/**
 		 * Return null if the argument is an "unsaved" entity (ie. one with no existing database row), or the
 		 * input argument otherwise.  This is how Hibernate avoids foreign key constraint violations.
 		 *
 		 * @param value An entity attribute value
 		 * @param type An entity attribute type
 		 *
 		 * @return {@code null} if the argument is an unsaved entity; otherwise return the argument.
 		 */
 		private Object nullifyTransientReferences(final Object value, final Type type) {
 			if ( value == null ) {
 				return null;
 			}
 			else if ( type.isEntityType() ) {
 				final EntityType entityType = (EntityType) type;
 				if ( entityType.isOneToOne() ) {
 					return value;
 				}
 				else {
 					final String entityName = entityType.getAssociatedEntityName();
 					return isNullifiable( entityName, value ) ? null : value;
 				}
 			}
 			else if ( type.isAnyType() ) {
 				return isNullifiable( null, value ) ? null : value;
 			}
 			else if ( type.isComponentType() ) {
 				final CompositeType actype = (CompositeType) type;
 				final Object[] subvalues = actype.getPropertyValues( value, session );
 				final Type[] subtypes = actype.getSubtypes();
 				boolean substitute = false;
 				for ( int i = 0; i < subvalues.length; i++ ) {
 					final Object replacement = nullifyTransientReferences( subvalues[i], subtypes[i] );
 					if ( replacement != subvalues[i] ) {
 						substitute = true;
 						subvalues[i] = replacement;
 					}
 				}
 				if ( substitute ) {
 					// todo : need to account for entity mode on the CompositeType interface :(
 					actype.setPropertyValues( value, subvalues, EntityMode.POJO );
 				}
 				return value;
 			}
 			else {
 				return value;
 			}
 		}
 
 		/**
 		 * Determine if the object already exists in the database,
 		 * using a "best guess"
 		 *
 		 * @param entityName The name of the entity
 		 * @param object The entity instance
 		 */
 		private boolean isNullifiable(final String entityName, Object object)
 				throws HibernateException {
 			if ( object == LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				// this is the best we can do...
 				return false;
 			}
 
 			if ( object instanceof HibernateProxy ) {
 				// if its an uninitialized proxy it can't be transient
 				final LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 				if ( li.getImplementation( session ) == null ) {
 					return false;
 					// ie. we never have to null out a reference to
 					// an uninitialized proxy
 				}
 				else {
 					//unwrap it
 					object = li.getImplementation();
 				}
 			}
 
 			// if it was a reference to self, don't need to nullify
 			// unless we are using native id generation, in which
 			// case we definitely need to nullify
 			if ( object == self ) {
 				return isEarlyInsert
 						|| ( isDelete && session.getFactory().getDialect().hasSelfReferentialForeignKeyBug() );
 			}
 
 			// See if the entity is already bound to this session, if not look at the
 			// entity identifier and assume that the entity is persistent if the
 			// id is not "unsaved" (that is, we rely on foreign keys to keep
 			// database integrity)
 
 			final EntityEntry entityEntry = session.getPersistenceContext().getEntry( object );
 			if ( entityEntry == null ) {
 				return isTransient( entityName, object, null, session );
 			}
 			else {
 				return entityEntry.isNullifiable( isEarlyInsert, session );
 			}
 
 		}
 
 	}
 
 	/**
 	 * Is this instance persistent or detached?
 	 * <p/>
 	 * If <tt>assumed</tt> is non-null, don't hit the database to make the determination, instead assume that
 	 * value; the client code must be prepared to "recover" in the case that this assumed result is incorrect.
 	 *
 	 * @param entityName The name of the entity
 	 * @param entity The entity instance
 	 * @param assumed The assumed return value, if avoiding database hit is desired
 	 * @param session The session
 	 *
 	 * @return {@code true} if the given entity is not transient (meaning it is either detached/persistent)
 	 */
 	@SuppressWarnings("SimplifiableIfStatement")
 	public static boolean isNotTransient(String entityName, Object entity, Boolean assumed, SessionImplementor session) {
 		if ( entity instanceof HibernateProxy ) {
 			return true;
 		}
 
 		if ( session.getPersistenceContext().isEntryFor( entity ) ) {
 			return true;
 		}
 
 		// todo : shouldnt assumed be revered here?
 
 		return !isTransient( entityName, entity, assumed, session );
 	}
 
 	/**
 	 * Is this instance, which we know is not persistent, actually transient?
 	 * <p/>
 	 * If <tt>assumed</tt> is non-null, don't hit the database to make the determination, instead assume that
 	 * value; the client code must be prepared to "recover" in the case that this assumed result is incorrect.
 	 *
 	 * @param entityName The name of the entity
 	 * @param entity The entity instance
 	 * @param assumed The assumed return value, if avoiding database hit is desired
 	 * @param session The session
 	 *
 	 * @return {@code true} if the given entity is transient (unsaved)
 	 */
 	public static boolean isTransient(String entityName, Object entity, Boolean assumed, SessionImplementor session) {
 		if ( entity == LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 			// an unfetched association can only point to
 			// an entity that already exists in the db
 			return false;
 		}
 
 		// let the interceptor inspect the instance to decide
 		Boolean isUnsaved = session.getInterceptor().isTransient( entity );
 		if ( isUnsaved != null ) {
 			return isUnsaved;
 		}
 
 		// let the persister inspect the instance to decide
 		final EntityPersister persister = session.getEntityPersister( entityName, entity );
 		isUnsaved = persister.isTransient( entity, session );
 		if ( isUnsaved != null ) {
 			return isUnsaved;
 		}
 
 		// we use the assumed value, if there is one, to avoid hitting
 		// the database
 		if ( assumed != null ) {
 			return assumed;
 		}
 
 		// hit the database, after checking the session cache for a snapshot
 		final Object[] snapshot = session.getPersistenceContext().getDatabaseSnapshot(
 				persister.getIdentifier( entity, session ),
 				persister
 		);
 		return snapshot == null;
 
 	}
 
 	/**
 	 * Return the identifier of the persistent or transient object, or throw
 	 * an exception if the instance is "unsaved"
 	 * <p/>
 	 * Used by OneToOneType and ManyToOneType to determine what id value should
 	 * be used for an object that may or may not be associated with the session.
 	 * This does a "best guess" using any/all info available to use (not just the
 	 * EntityEntry).
 	 *
 	 * @param entityName The name of the entity
 	 * @param object The entity instance
 	 * @param session The session
 	 *
 	 * @return The identifier
 	 *
 	 * @throws TransientObjectException if the entity is transient (does not yet have an identifier)
 	 */
 	public static Serializable getEntityIdentifierIfNotUnsaved(
 			final String entityName,
 			final Object object,
 			final SessionImplementor session) throws TransientObjectException {
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Serializable id = session.getContextEntityIdentifier( object );
 			if ( id == null ) {
 				// context-entity-identifier returns null explicitly if the entity
 				// is not associated with the persistence context; so make some
 				// deeper checks...
 				if ( isTransient( entityName, object, Boolean.FALSE, session ) ) {
 					throw new TransientObjectException(
 							"object references an unsaved transient instance - save the transient instance before flushing: " +
 									(entityName == null ? session.guessEntityName( object ) : entityName)
 					);
 				}
 				id = session.getEntityPersister( entityName, object ).getIdentifier( object, session );
 			}
 			return id;
 		}
 	}
 
 	/**
 	 * Find all non-nullable references to entities that have not yet
 	 * been inserted in the database, where the foreign key
 	 * is a reference to an unsaved transient entity. .
 	 *
 	 * @param entityName - the entity name
 	 * @param entity - the entity instance
 	 * @param values - insertable properties of the object (including backrefs),
 	 * possibly with substitutions
 	 * @param isEarlyInsert - true if the entity needs to be executed as soon as possible
 	 * (e.g., to generate an ID)
 	 * @param session - the session
 	 *
 	 * @return the transient unsaved entity dependencies that are non-nullable,
 	 *         or null if there are none.
 	 */
 	public static NonNullableTransientDependencies findNonNullableTransientEntities(
 			String entityName,
 			Object entity,
 			Object[] values,
 			boolean isEarlyInsert,
 			SessionImplementor session) {
 		final Nullifier nullifier = new Nullifier( entity, false, isEarlyInsert, session );
 		final EntityPersister persister = session.getEntityPersister( entityName, entity );
 		final String[] propertyNames = persister.getPropertyNames();
 		final Type[] types = persister.getPropertyTypes();
 		final boolean[] nullability = persister.getPropertyNullability();
 		final NonNullableTransientDependencies nonNullableTransientEntities = new NonNullableTransientDependencies();
 		for ( int i = 0; i < types.length; i++ ) {
 			collectNonNullableTransientEntities(
 					nullifier,
 					values[i],
 					propertyNames[i],
 					types[i],
 					nullability[i],
 					session,
 					nonNullableTransientEntities
 			);
 		}
 		return nonNullableTransientEntities.isEmpty() ? null : nonNullableTransientEntities;
 	}
 
 	private static void collectNonNullableTransientEntities(
 			Nullifier nullifier,
 			Object value,
 			String propertyName,
 			Type type,
 			boolean isNullable,
 			SessionImplementor session,
 			NonNullableTransientDependencies nonNullableTransientEntities) {
 		if ( value == null ) {
 			return;
 		}
 
 		if ( type.isEntityType() ) {
 			final EntityType entityType = (EntityType) type;
 			if ( !isNullable
 					&& !entityType.isOneToOne()
 					&& nullifier.isNullifiable( entityType.getAssociatedEntityName(), value ) ) {
 				nonNullableTransientEntities.add( propertyName, value );
 			}
 		}
 		else if ( type.isAnyType() ) {
 			if ( !isNullable && nullifier.isNullifiable( null, value ) ) {
 				nonNullableTransientEntities.add( propertyName, value );
 			}
 		}
 		else if ( type.isComponentType() ) {
 			final CompositeType actype = (CompositeType) type;
 			final boolean[] subValueNullability = actype.getPropertyNullability();
 			if ( subValueNullability != null ) {
 				final String[] subPropertyNames = actype.getPropertyNames();
 				final Object[] subvalues = actype.getPropertyValues( value, session );
 				final Type[] subtypes = actype.getSubtypes();
 				for ( int j = 0; j < subvalues.length; j++ ) {
 					collectNonNullableTransientEntities(
 							nullifier,
 							subvalues[j],
 							subPropertyNames[j],
 							subtypes[j],
 							subValueNullability[j],
 							session,
 							nonNullableTransientEntities
 					);
 				}
 			}
 		}
 	}
 
 	/**
 	 * Disallow instantiation
 	 */
 	private ForeignKeys() {
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
index 478a59b984..ad6764eb24 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Nullability.java
@@ -1,207 +1,207 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.util.Iterator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyValueException;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Implements the algorithm for validating property values for illegal null values
  * 
  * @author Gavin King
  */
 public final class Nullability {
 	private final SessionImplementor session;
 	private final boolean checkNullability;
 
 	/**
 	 * Constructs a Nullability
 	 *
 	 * @param session The session
 	 */
 	public Nullability(SessionImplementor session) {
 		this.session = session;
 		this.checkNullability = session.getFactory().getSettings().isCheckNullability();
 	}
 	/**
 	 * Check nullability of the class persister properties
 	 *
 	 * @param values entity properties
 	 * @param persister class persister
 	 * @param isUpdate whether it is intended to be updated or saved
 	 *
 	 * @throws PropertyValueException Break the nullability of one property
 	 * @throws HibernateException error while getting Component values
 	 */
 	public void checkNullability(
 			final Object[] values,
 			final EntityPersister persister,
 			final boolean isUpdate) throws HibernateException {
 		/*
 		 * Typically when Bean Validation is on, we don't want to validate null values
 		 * at the Hibernate Core level. Hence the checkNullability setting.
 		 */
 		if ( checkNullability ) {
 			/*
 			  * Algorithm
 			  * Check for any level one nullability breaks
 			  * Look at non null components to
 			  *   recursively check next level of nullability breaks
 			  * Look at Collections contraining component to
 			  *   recursively check next level of nullability breaks
 			  *
 			  *
 			  * In the previous implementation, not-null stuffs where checked
 			  * filtering by level one only updateable
 			  * or insertable columns. So setting a sub component as update="false"
 			  * has no effect on not-null check if the main component had good checkeability
 			  * In this implementation, we keep this feature.
 			  * However, I never see any documentation mentioning that, but it's for
 			  * sure a limitation.
 			  */
 
 			final boolean[] nullability = persister.getPropertyNullability();
 			final boolean[] checkability = isUpdate ?
 				persister.getPropertyUpdateability() :
 				persister.getPropertyInsertability();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 
 			for ( int i = 0; i < values.length; i++ ) {
 
 				if ( checkability[i] && values[i]!= LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 					final Object value = values[i];
 					if ( !nullability[i] && value == null ) {
 
 						//check basic level one nullablilty
 						throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								persister.getPropertyNames()[i]
 							);
 
 					}
 					else if ( value != null ) {
 						//values is not null and is checkable, we'll look deeper
 						final String breakProperties = checkSubElementsNullability( propertyTypes[i], value );
 						if ( breakProperties != null ) {
 							throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								buildPropertyPath( persister.getPropertyNames()[i], breakProperties )
 							);
 						}
 
 					}
 				}
 
 			}
 		}
 	}
 
 	/**
 	 * check sub elements-nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param propertyType type to check
 	 * @param value value to check
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkSubElementsNullability(Type propertyType, Object value) throws HibernateException {
 		if ( propertyType.isComponentType() ) {
 			return checkComponentNullability( value, (CompositeType) propertyType );
 		}
 
 		if ( propertyType.isCollectionType() ) {
 			// persistent collections may have components
 			final CollectionType collectionType = (CollectionType) propertyType;
 			final Type collectionElementType = collectionType.getElementType( session.getFactory() );
 
 			if ( collectionElementType.isComponentType() ) {
 				// check for all components values in the collection
 				final CompositeType componentType = (CompositeType) collectionElementType;
 				final Iterator itr = CascadingActions.getLoadedElementsIterator( session, collectionType, value );
 				while ( itr.hasNext() ) {
 					final Object compositeElement = itr.next();
 					if ( compositeElement != null ) {
 						return checkComponentNullability( compositeElement, componentType );
 					}
 				}
 			}
 		}
 
 		return null;
 	}
 
 	/**
 	 * check component nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param value component properties
 	 * @param compositeType component not-nullable type
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkComponentNullability(Object value, CompositeType compositeType) throws HibernateException {
 		// IMPL NOTE : we currently skip checking "any" and "many to any" mappings.
 		//
 		// This is not the best solution.  But atm there is a mismatch between AnyType#getPropertyNullability
 		// and the fact that cascaded-saves for "many to any" mappings are not performed until after this nullability
 		// check.  So the nullability check fails for transient entity elements with generated identifiers because
 		// the identifier is not yet generated/assigned (is null)
 		//
 		// The more correct fix would be to cascade saves of the many-to-any elements before the Nullability checking
 
 		if ( compositeType.isAnyType() ) {
 			return null;
 		}
 
 		final boolean[] nullability = compositeType.getPropertyNullability();
 		if ( nullability != null ) {
 			//do the test
 			final Object[] subValues = compositeType.getPropertyValues( value, session );
 			final Type[] propertyTypes = compositeType.getSubtypes();
 			for ( int i = 0; i < subValues.length; i++ ) {
 				final Object subValue = subValues[i];
 				if ( !nullability[i] && subValue==null ) {
 					return compositeType.getPropertyNames()[i];
 				}
 				else if ( subValue != null ) {
 					final String breakProperties = checkSubElementsNullability( propertyTypes[i], subValue );
 					if ( breakProperties != null ) {
 						return buildPropertyPath( compositeType.getPropertyNames()[i], breakProperties );
 					}
 				}
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Return a well formed property path. Basically, it will return parent.child
 	 *
 	 * @param parent parent in path
 	 * @param child child in path
 	 *
 	 * @return parent-child path
 	 */
 	private static String buildPropertyPath(String parent, String child) {
 		return parent + '.' + child;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index d71c5d8d8e..a04c712a37 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1230 +1,1230 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.ConcurrentMap;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
-import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.CollectionType;
 
 import org.jboss.logging.Logger;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
  * <p/>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			StatefulPersistenceContext.class.getName()
 	);
 
 	private static final boolean TRACE_ENABLED = LOG.isTraceEnabled();
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	private EntityEntryContext entityEntryContext;
 //	private Map<Object,EntityEntry> entityEntries;
 
 	// Entity proxies, by EntityKey
 	private ConcurrentMap<EntityKey, Object> proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map<EntityKey, Object> entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map<Object, PersistentCollection> arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private IdentityMap<PersistentCollection, CollectionEntry> collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map<CollectionKey, PersistentCollection> collectionsByKey;
 
 	// Set of EntityKeys of deleted objects
 	private HashSet<EntityKey> nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet<AssociationKey> nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List<PersistentCollection> nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map<CollectionKey,PersistentCollection> unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map<Object,Object> parentsByChild;
 
 	private int cascading;
 	private int loadCounter;
 	private int removeOrphanBeforeUpdatesCounter;
 	private boolean flushing;
 
 	private boolean defaultReadOnly;
 	private boolean hasNonReadOnlyEntities;
 
 	private LoadContexts loadContexts;
 	private BatchFetchQueue batchFetchQueue;
 
 
 	/**
 	 * Constructs a PersistentContext, bound to the given session.
 	 *
 	 * @param session The session "owning" this context.
 	 */
 	public StatefulPersistenceContext(SessionImplementor session) {
 		this.session = session;
 
 		entitiesByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap<EntityUniqueKey, Object>( INIT_COLL_SIZE );
 		//noinspection unchecked
 		proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>( INIT_COLL_SIZE, .75f, 1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK, null );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntryContext = new EntityEntryContext();
 //		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = new IdentityHashMap<Object,Object>( INIT_COLL_SIZE );
 
 		collectionsByKey = new HashMap<CollectionKey, PersistentCollection>( INIT_COLL_SIZE );
 		arrayHolders = new IdentityHashMap<Object, PersistentCollection>( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet<EntityKey>();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet<AssociationKey>( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList<PersistentCollection>( INIT_COLL_SIZE );
 	}
 
 	@Override
 	public boolean isStateless() {
 		return false;
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	@Override
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(INIT_COLL_SIZE);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		return ( unownedCollections == null ) ? null : unownedCollections.remove( key );
 	}
 
 	@Override
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	@Override
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			if ( o == null ) {
 				//entry may be GCd
 				continue;
 			}
 			((HibernateProxy) o).getHibernateLazyInitializer().unsetSession();
 		}
 
 		for ( Entry<Object, EntityEntry> objectEntityEntryEntry : entityEntryContext.reentrantSafeEntityEntries() ) {
 			// todo : I dont think this need be reentrant safe
 			if ( objectEntityEntryEntry.getKey() instanceof PersistentAttributeInterceptable ) {
 				final PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) objectEntityEntryEntry.getKey() ).$$_hibernate_getInterceptor();
-				if ( interceptor instanceof LazyAttributeLoader ) {
-					( (LazyAttributeLoader) interceptor ).unsetSession();
+				if ( interceptor instanceof LazyAttributeLoadingInterceptor ) {
+					( (LazyAttributeLoadingInterceptor) interceptor ).unsetSession();
 				}
 			}
 		}
 
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
 		}
 
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntryContext.clear();
 //		entityEntries.clear();
 		parentsByChild.clear();
 		entitySnapshotsByKey.clear();
 		collectionsByKey.clear();
 		collectionEntries.clear();
 		if ( unownedCollections != null ) {
 			unownedCollections.clear();
 		}
 		proxiesByKey.clear();
 		nullifiableEntityKeys.clear();
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.clear();
 		}
 		// defaultReadOnly is unaffected by clear()
 		hasNonReadOnlyEntities = false;
 		if ( loadContexts != null ) {
 			loadContexts.cleanup();
 		}
 		naturalIdXrefDelegate.clear();
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	@Override
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	@Override
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus( status );
 		setHasNonReadOnlyEnties( status );
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		entityEntryContext.downgradeLocks();
 //		// Downgrade locks
 //		for ( EntityEntry o : entityEntries.values() ) {
 //			o.setLockMode( LockMode.NONE );
 //		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
 		final Object cached = entitySnapshotsByKey.get( key );
 		if ( cached != null ) {
 			return cached == NO_ROW ? null : (Object[]) cached;
 		}
 		else {
 			final Object[] snapshot = persister.getDatabaseSnapshot( id, session );
 			entitySnapshotsByKey.put( key, snapshot == null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	@Override
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		persister = locateProperPersister( persister );
 
 		// let's first see if it is part of the natural id cache...
 		final Object[] cachedValue = naturalIdHelper.findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					dbValue
 			);
 			return dbValue;
 		}
 		else {
 			// for a mutable natural there is a likelihood that the the information will already be
 			// snapshot-cached.
 			final int[] props = persister.getNaturalIdentifierProperties();
 			final Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW || entitySnapshot == null ) {
 				return null;
 			}
 
 			final Object[] naturalIdSnapshotSubSet = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshotSubSet[i] = entitySnapshot[ props[i] ];
 			}
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					naturalIdSnapshotSubSet
 			);
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
 	private EntityPersister locateProperPersister(EntityPersister persister) {
 		return session.getFactory().getEntityPersister( persister.getRootEntityName() );
 	}
 
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		final Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException(
 					"persistence context reported no row snapshot for "
 							+ MessageHelper.infoString( key.getEntityName(), key.getIdentifier() )
 			);
 		}
 		return (Object[]) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put( key, entity );
 		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 	}
 
 	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get( key );
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey( key );
 	}
 
 	@Override
 	public Object removeEntity(EntityKey key) {
 		final Object entity = entitiesByKey.remove( key );
 		final Iterator itr = entitiesByUniqueKey.values().iterator();
 		while ( itr.hasNext() ) {
 			if ( itr.next() == entity ) {
 				itr.remove();
 			}
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
 		entitySnapshotsByKey.remove( key );
 		nullifiableEntityKeys.remove( key );
 		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 		getBatchFetchQueue().removeSubselect( key );
 		return entity;
 	}
 
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get( euk );
 	}
 
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put( euk, entity );
 	}
 
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntryContext.getEntityEntry( entity );
 	}
 
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntryContext.removeEntityEntry( entity );
 	}
 
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntryContext.hasEntityEntry( entity );
 	}
 
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get( coll );
 	}
 
 	@Override
 	public EntityEntry addEntity(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final EntityKey entityKey,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 		addEntity( entityKey, entity );
 		return addEntry(
 				entity,
 				status,
 				loadedState,
 				null,
 				entityKey.getIdentifier(),
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 		);
 	}
 
 	@Override
 	public EntityEntry addEntry(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 
 		final EntityEntry e;
 
 		if( (entity instanceof ManagedEntity) &&  ((ManagedEntity) entity).$$_hibernate_getEntityEntry() != null && status == Status.READ_ONLY) {
 			e = ((ManagedEntity) entity).$$_hibernate_getEntityEntry();
 			e.setStatus( status  );
 		}
 		else {
 			final EntityEntryFactory entityEntryFactory = persister.getEntityEntryFactory();
 			e = entityEntryFactory.createEntityEntry(
 					status,
 					loadedState,
 					rowId,
 					id,
 					version,
 					lockMode,
 					existsInDatabase,
 					persister,
 					disableVersionIncrement,
 					lazyPropertiesAreUnfetched,
 					this
 			);
 		}
 
 		entityEntryContext.addEntityEntry( entity, e );
 //		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties( status );
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey( collection );
 	}
 
 	@Override
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
 	@Override
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( !Hibernate.isInitialized( value ) ) {
 			final HibernateProxy proxy = (HibernateProxy) value;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy( li, proxy );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
 			final HibernateProxy proxy = (HibernateProxy) value;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			li.setIdentifier( id );
 			reassociateProxy( li, proxy );
 		}
 	}
 
 	/**
 	 * Associate a proxy that was instantiated by another session with this session
 	 *
 	 * @param li The proxy initializer.
 	 * @param proxy The proxy to reassociate.
 	 */
 	private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) {
 		if ( li.getSession() != this.getSession() ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
 			final EntityKey key = session.generateEntityKey( li.getIdentifier(), persister );
 		  	// any earlier proxy takes precedence
 			proxiesByKey.putIfAbsent( key, proxy );
 			proxy.getHibernateLazyInitializer().setSession( session );
 		}
 	}
 
 	@Override
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
 						"object was an uninitialized proxy for " + li.getEntityName()
 				);
 			}
 			//unwrap the object and return
 			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	@Override
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy( li, proxy );
 			//initialize + unwrap the object and return it
 			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	@Override
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
 		final Object entity = getEntity( key );
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		final boolean alreadyNarrow = concreteProxyClass.isInstance( proxy );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			// If an impl is passed, there is really no point in creating a proxy.
 			// It would just be extra processing.  Just return the impl
 			if ( object != null ) {
 				proxiesByKey.remove( key );
 				return object;
 			}
 
 			// Similarly, if the original HibernateProxy is initialized, there
 			// is again no point in creating a proxy.  Just return the impl
 			final HibernateProxy originalHibernateProxy = (HibernateProxy) proxy;
 			if ( !originalHibernateProxy.getHibernateLazyInitializer().isUninitialized() ) {
 				final Object impl = originalHibernateProxy.getHibernateLazyInitializer().getImplementation();
 				// can we return it?
 				if ( concreteProxyClass.isInstance( impl ) ) {
 					proxiesByKey.remove( key );
 					return impl;
 				}
 			}
 
 
 			// Otherwise, create the narrowed proxy
 			final HibernateProxy narrowedProxy = (HibernateProxy) persister.createProxy( key.getIdentifier(), session );
 
 			// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
 			final boolean readOnlyOrig = originalHibernateProxy.getHibernateLazyInitializer().isReadOnly();
 			narrowedProxy.getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 
 			return narrowedProxy;
 		}
 		else {
 
 			if ( object != null ) {
 				final LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 				li.setImplementation( object );
 			}
 			return proxy;
 		}
 	}
 
 	@Override
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
 		if ( !persister.hasProxy() ) {
 			return impl;
 		}
 		final Object proxy = proxiesByKey.get( key );
 		return ( proxy != null ) ? narrowProxy( proxy, persister, key, impl ) : impl;
 	}
 
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		final EntityEntry e = getEntry( impl );
 		if ( e == null ) {
 			return impl;
 		}
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	@Override
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		// todo : we really just need to add a split in the notions of:
 		//		1) collection key
 		//		2) collection owner key
 		// these 2 are not always the same.  Same is true in the case of ToOne associations with property-ref...
 		final EntityPersister ownerPersister = collectionPersister.getOwnerEntityPersister();
 		if ( ownerPersister.getIdentifierType().getReturnedClass().isInstance( key ) ) {
 			return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 		}
 
 		// we have a property-ref type mapping for the collection key.  But that could show up a few ways here...
 		//
 		//		1) The incoming key could be the entity itself...
 		if ( ownerPersister.isInstance( key ) ) {
 			final Serializable owenerId = ownerPersister.getIdentifier( key, session );
 			if ( owenerId == null ) {
 				return null;
 			}
 			return getEntity( session.generateEntityKey( owenerId, ownerPersister ) );
 		}
 
 		final CollectionType collectionType = collectionPersister.getCollectionType();
 
 		//		2) The incoming key is most likely the collection key which we need to resolve to the owner key
 		//			find the corresponding owner instance
 		//			a) try by EntityUniqueKey
 		if ( collectionType.getLHSPropertyName() != null ) {
 			final Object owner = getEntity(
 					new EntityUniqueKey(
 							ownerPersister.getEntityName(),
 							collectionType.getLHSPropertyName(),
 							key,
 							collectionPersister.getKeyType(),
 							ownerPersister.getEntityMode(),
 							session.getFactory()
 					)
 			);
 			if ( owner != null ) {
 				return owner;
 			}
 
 			//		b) try by EntityKey, which means we need to resolve owner-key -> collection-key
 			//			IMPL NOTE : yes if we get here this impl is very non-performant, but PersistenceContext
 			//					was never designed to handle this case; adding that capability for real means splitting
 			//					the notions of:
 			//						1) collection key
 			//						2) collection owner key
 			// 					these 2 are not always the same (same is true in the case of ToOne associations with
 			// 					property-ref).  That would require changes to (at least) CollectionEntry and quite
 			//					probably changes to how the sql for collection initializers are generated
 			//
 			//			We could also possibly see if the referenced property is a natural id since we already have caching
 			//			in place of natural id snapshots.  BUt really its better to just do it the right way ^^ if we start
 			// 			going that route
 			final Serializable ownerId = ownerPersister.getIdByUniqueKey( key, collectionType.getLHSPropertyName(), session );
 			return getEntity( session.generateEntityKey( ownerId, ownerPersister ) );
 		}
 
 		// as a last resort this is what the old code did...
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
 	@Override
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
 		final CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
 			return null;
 		}
 
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
 		final Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
 	@Override
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection) {
 		return getLoadedCollectionOwnerIdOrNull( getCollectionEntry( collection ) );
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param ce The collection entry
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	private Serializable getLoadedCollectionOwnerIdOrNull(CollectionEntry ce) {
 		if ( ce == null || ce.getLoadedKey() == null || ce.getLoadedPersister() == null ) {
 			return null;
 		}
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// get the ID from collection.getOwner()
 		return ce.getLoadedPersister().getCollectionType().getIdOfOwnerOrNull( ce.getLoadedKey(), session );
 	}
 
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
 		addCollection( collection, ce, id );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		final CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
 	@Override
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 			throws HibernateException {
 		addCollection( collection, persister );
 	}
 
 	/**
 	 * Add an collection to the cache, with a given collection entry.
 	 *
 	 * @param coll The collection for which we are adding an entry.
 	 * @param entry The entry representing the collection.
 	 * @param key The key of the collection's entry.
 	 */
 	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
 		collectionEntries.put( coll, entry );
 		final CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		final PersistentCollection old = collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
 				throw new AssertionFailure( "bug adding collection twice" );
 			}
 			// or should it actually throw an exception?
 			old.unsetSession( session );
 			collectionEntries.remove( old );
 			// watch out for a case where old is still referenced
 			// somewhere in the object graph! (which is a user error)
 		}
 	}
 
 	/**
 	 * Add a collection to the cache, creating a new collection entry for it
 	 *
 	 * @param collection The collection for which we are adding an entry.
 	 * @param persister The collection persister
 	 */
 	private void addCollection(PersistentCollection collection, CollectionPersister persister) {
 		final CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
 	@Override
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
 			throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
 			final CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
 	@Override
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
 			throws HibernateException {
 		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
 		ce.postInitialize( collection );
 		addCollection( collection, ce, id );
 		return ce;
 	}
 
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add( collection );
 	}
 
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
 			if ( TRACE_ENABLED ) {
 				LOG.trace( "Initializing non-lazy collections" );
 			}
 
 			//do this work only at the very highest level of the load
 			//don't let this method be called recursively
 			loadCounter++;
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					nonlazyCollections.remove( size - 1 ).forceInitialization();
 				}
 			}
 			finally {
 				loadCounter--;
 				clearNullProperties();
 			}
 		}
 	}
 
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get( array );
 	}
 
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove( array );
 	}
 
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry( coll ).getSnapshot();
 	}
 
 	@Override
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
 			coll = getCollectionHolder( collection );
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
 				final Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
 					final PersistentCollection pc = wrappers.next();
 					if ( pc.isWrapper( collection ) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
 		return (coll == null) ? null : getCollectionEntry( coll );
 	}
 
 	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get( key );
 	}
 
 	@Override
 	public void addProxy(EntityKey key, Object proxy) {
 		proxiesByKey.put( key, proxy );
 	}
 
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	@Override
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	@Override
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	@Override
 	public int getNumberOfManagedEntities() {
 		return entityEntryContext.getNumberOfManagedEntities();
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return null;
 	}
 
 	@Override
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	@Override
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	@Override
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	@Override
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	@Override
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	@Override
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	@Override
 	public void setFlushing(boolean flushing) {
 		final boolean afterFlush = this.flushing && ! flushing;
 		this.flushing = flushing;
 		if ( afterFlush ) {
 			getNaturalIdHelper().cleanupFromSynchronizations();
 		}
 	}
 
 	public boolean isRemovingOrphanBeforeUpates() {
 		return removeOrphanBeforeUpdatesCounter > 0;
 	}
 
 	public void beginRemoveOrphanBeforeUpdates() {
 		if ( getCascadeLevel() < 1 ) {
 			throw new IllegalStateException( "Attempt to remove orphan when not cascading." );
 		}
 		if ( removeOrphanBeforeUpdatesCounter >= getCascadeLevel() ) {
 			throw new IllegalStateException(
 					String.format(
 							"Cascade level [%d] is out of sync with removeOrphanBeforeUpdatesCounter [%d] before incrementing removeOrphanBeforeUpdatesCounter",
 							getCascadeLevel(),
 							removeOrphanBeforeUpdatesCounter
 					)
 			);
 		}
 		removeOrphanBeforeUpdatesCounter++;
 	}
 
 	public void endRemoveOrphanBeforeUpdates() {
 		if ( getCascadeLevel() < 1 ) {
 			throw new IllegalStateException( "Finished removing orphan when not cascading." );
 		}
 		if ( removeOrphanBeforeUpdatesCounter > getCascadeLevel() ) {
 			throw new IllegalStateException(
 					String.format(
 							"Cascade level [%d] is out of sync with removeOrphanBeforeUpdatesCounter [%d] before decrementing removeOrphanBeforeUpdatesCounter",
 							getCascadeLevel(),
 							removeOrphanBeforeUpdatesCounter
 					)
 			);
 		}
 		removeOrphanBeforeUpdatesCounter--;
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
 	@Override
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	@Override
 	public void afterLoad() {
 		loadCounter--;
 	}
 
 	@Override
 	public boolean isLoadFinished() {
 		return loadCounter == 0;
 	}
 
 	@Override
 	public String toString() {
 		return "PersistenceContext[entityKeys=" + entitiesByKey.keySet()
 				+ ",collectionKeys=" + collectionsByKey.keySet() + "]";
 	}
 
 	@Override
 	public Entry<Object,EntityEntry>[] reentrantSafeEntityEntries() {
 		return entityEntryContext.reentrantSafeEntityEntries();
 	}
 
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
 				// remove wrong entry
 				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		for ( Entry<Object,EntityEntry> me : reentrantSafeEntityEntries() ) {
 			final EntityEntry entityEntry = me.getValue();
 			// does this entity entry pertain to the entity persister in which we are interested (owner)?
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				final Object entityEntryInstance = me.getKey();
 
 				//check if the managed object is the parent
 				boolean found = isFoundInParent(
 						propertyName,
 						childEntity,
 						persister,
 						collectionPersister,
 						entityEntryInstance
 				);
 
 				if ( !found && mergeMap != null ) {
 					//check if the detached object being merged is the parent
 					final Object unmergedInstance = mergeMap.get( entityEntryInstance );
 					final Object unmergedChild = mergeMap.get( childEntity );
 					if ( unmergedInstance != null && unmergedChild != null ) {
 						found = isFoundInParent(
 								propertyName,
 								unmergedChild,
 								persister,
 								collectionPersister,
 								unmergedInstance
 						);
 						LOG.debugf(
 								"Detached object being merged (corresponding with a managed entity) has a collection that [%s] the detached child.",
 								( found ? "contains" : "does not contain" )
 						);
 					}
 				}
 
 				if ( found ) {
 					return entityEntry.getId();
 				}
 
 			}
 		}
 
 		// if we get here, it is possible that we have a proxy 'in the way' of the merge map resolution...
 		// 		NOTE: decided to put this here rather than in the above loop as I was nervous about the performance
 		//		of the loop-in-loop especially considering this is far more likely the 'edge case'
 		if ( mergeMap != null ) {
 			for ( Object o : mergeMap.entrySet() ) {
 				final Entry mergeMapEntry = (Entry) o;
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = (HibernateProxy) mergeMapEntry.getKey();
 					if ( persister.isSubclassEntityName( proxy.getHibernateLazyInitializer().getEntityName() ) ) {
 						boolean found = isFoundInParent(
 								propertyName,
 								childEntity,
 								persister,
 								collectionPersister,
 								mergeMap.get( proxy )
 						);
 						LOG.debugf(
 								"Detached proxy being merged has a collection that [%s] the managed child.",
 								(found ? "contains" : "does not contain")
 						);
 						if ( !found ) {
 							found = isFoundInParent(
 									propertyName,
 									mergeMap.get( childEntity ),
 									persister,
 									collectionPersister,
 									mergeMap.get( proxy )
 							);
 							LOG.debugf(
 									"Detached proxy being merged has a collection that [%s] the detached child being merged..",
 									(found ? "contains" : "does not contain")
 							);
 						}
 						if ( found ) {
 							return proxy.getHibernateLazyInitializer().getIdentifier();
 						}
 					}
 				}
 			}
 		}
 
 		return null;
 	}
 
 	private boolean isFoundInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent) {
 		final Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 		final EntityPersister persister = session.getFactory().getEntityPersister( entity );
 		final CollectionPersister cp = session.getFactory().getCollectionPersister( entity + '.' + property );
 
 	    // try cache lookup first
 		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				Object index = getIndexInParent( property, childEntity, persister, cp, parent );
 
 				if (index==null && mergeMap!=null) {
 					final Object unMergedInstance = mergeMap.get( parent );
 					final Object unMergedChild = mergeMap.get( childEntity );
 					if ( unMergedInstance != null && unMergedChild != null ) {
 						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 						LOG.debugf(
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
index af92fe6315..ac486ddbe6 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
@@ -1,394 +1,394 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Functionality relating to the Hibernate two-phase loading process, that may be reused by persisters
  * that do not use the Loader framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TwoPhaseLoad.class.getName()
 	);
 
 	private TwoPhaseLoad() {
 	}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
 	 *
 	 * @param persister The persister for the hydrated entity
 	 * @param id The entity identifier
 	 * @param values The entity values
 	 * @param rowId The rowId for the entity
 	 * @param object An optional instance for the entity being loaded
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Whether properties defined as lazy are yet un-fetched
 	 * @param session The Session
 	 */
 	public static void postHydrate(
 			final EntityPersister persister,
 			final Serializable id,
 			final Object[] values,
 			final Object rowId,
 			final Object object,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final SessionImplementor session) {
 		final Object version = Versioning.getVersion( values, persister );
 		session.getPersistenceContext().addEntry(
 				object,
 				Status.LOADING,
 				values,
 				rowId,
 				id,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnFetched
 			);
 
 		if ( version != null && LOG.isTraceEnabled() ) {
 			final String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 					: "null";
 			LOG.tracef( "Version: %s", versionStr );
 		}
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
 	 * <p/>
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
 	 *
 	 * @param entity The entity being loaded
 	 * @param readOnly Is the entity being loaded as read-only
 	 * @param session The Session
 	 * @param preLoadEvent The (re-used) pre-load event
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent) {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
 		doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent );
 	}
 
 	private static void doInitializeEntity(
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent) throws HibernateException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityPersister persister = entityEntry.getPersister();
 		final Serializable id = entityEntry.getId();
 		final Object[] hydratedState = entityEntry.getLoadedState();
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Resolving associations for %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		final Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!= PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				hydratedState[i] = types[i].resolve( value, session, entity );
 			}
 		}
 
 		//Must occur after resolving identifiers!
 		if ( session.isEventSource() ) {
 			preLoadEvent.setEntity( entity ).setState( hydratedState ).setId( id ).setPersister( persister );
 
 			final EventListenerGroup<PreLoadEventListener> listenerGroup = session
 					.getFactory()
 					.getServiceRegistry()
 					.getService( EventListenerRegistry.class )
 					.getEventListenerGroup( EventType.PRE_LOAD );
 			for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPreLoad( preLoadEvent );
 			}
 		}
 
 		persister.setPropertyValues( entity, hydratedState );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
 			if ( debugEnabled ) {
 				LOG.debugf(
 						"Adding entity to second-level cache: %s",
 						MessageHelper.infoString( persister, id, session.getFactory() )
 				);
 			}
 
 			final Object version = Versioning.getVersion( hydratedState, persister );
 			final CacheEntry entry = persister.buildCacheEntry( entity, hydratedState, version, session );
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 			final Object cacheKey = cache.generateCacheKey( id, persister, factory, session.getTenantIdentifier() );
 
 			// explicit handling of caching for rows just inserted and then somehow forced to be read
 			// from the database *within the same transaction*.  usually this is done by
 			// 		1) Session#refresh, or
 			// 		2) Session#clear + some form of load
 			//
 			// we need to be careful not to clobber the lock here in the cache so that it can be rolled back if need be
 			if ( session.getPersistenceContext().wasInsertedDuringTransaction( persister, id ) ) {
 				cache.update(
 						session,
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						version,
 						version
 				);
 			}
 			else {
 				final SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
 				try {
 					eventListenerManager.cachePutStart();
 					final boolean put = cache.putFromLoad(
 							session,
 							cacheKey,
 							persister.getCacheEntryStructure().structure( entry ),
 							session.getTimestamp(),
 							version,
 							useMinimalPuts( session, entityEntry )
 					);
 
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().secondLevelCachePut( cache.getRegion().getName() );
 					}
 				}
 				finally {
 					eventListenerManager.cachePutEnd();
 				}
 			}
 		}
 
 		if ( persister.hasNaturalIdentifier() ) {
 			persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					persistenceContext.getNaturalIdHelper().extractNaturalIdValues( hydratedState, persister )
 			);
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
 			final Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReallyReadOnly = ( (HibernateProxy) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
 			persistenceContext.setEntryStatus( entityEntry, Status.READ_ONLY );
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
 					//after setting values to object
 					hydratedState,
 					session
 			);
 			persistenceContext.setEntryStatus( entityEntry, Status.MANAGED );
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 		);
 
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Done materializing entity %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 	}
 	
 	/**
 	 * PostLoad cannot occur during initializeEntity, as that call occurs *before*
 	 * the Set collections are added to the persistence context by Loader.
 	 * Without the split, LazyInitializationExceptions can occur in the Entity's
 	 * postLoad if it acts upon the collection.
 	 *
 	 * HHH-6043
 	 * 
 	 * @param entity The entity
 	 * @param session The Session
 	 * @param postLoadEvent The (re-used) post-load event
 	 */
 	public static void postLoad(
 			final Object entity,
 			final SessionImplementor session,
 			final PostLoadEvent postLoadEvent) {
 		
 		if ( session.isEventSource() ) {
 			final PersistenceContext persistenceContext
 					= session.getPersistenceContext();
 			final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 
 			postLoadEvent.setEntity( entity ).setId( entityEntry.getId() ).setPersister( entityEntry.getPersister() );
 
 			final EventListenerGroup<PostLoadEventListener> listenerGroup = session.getFactory()
 							.getServiceRegistry()
 							.getService( EventListenerRegistry.class )
 							.getEventListenerGroup( EventType.POST_LOAD );
 			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPostLoad( postLoadEvent );
 			}
 		}
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
 		return ( session.getFactory().getSettings().isMinimalPutsEnabled()
 				&& session.getCacheMode()!=CacheMode.REFRESH )
 				|| ( entityEntry.getPersister().hasLazyProperties()
 				&& entityEntry.isLoadedWithLazyPropertiesUnfetched()
 				&& entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
 	 *
 	 * @param key The entity key
 	 * @param object The entity instance
 	 * @param persister The entity persister
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
 	 * @param session The Session
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final SessionImplementor session) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				null,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnFetched
 		);
 	}
 
 	/**
 	 * Same as {@link #addUninitializedEntity}, but here for an entity from the second level cache
 	 *
 	 * @param key The entity key
 	 * @param object The entity instance
 	 * @param persister The entity persister
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
 	 * @param version The version
 	 * @param session The Session
 	 */
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final Object version,
 			final SessionImplementor session) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnFetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
index 762e78baeb..b069f01a6c 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
@@ -1,529 +1,514 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.action.internal.AbstractEntityInsertAction;
 import org.hibernate.action.internal.EntityIdentityInsertAction;
 import org.hibernate.action.internal.EntityInsertAction;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.CascadePoint;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryExtraState;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * A convenience bas class for listeners responding to save events.
  *
  * @author Steve Ebersole.
  */
 public abstract class AbstractSaveEventListener extends AbstractReassociateEventListener {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractSaveEventListener.class );
 
 	public static enum EntityState {
 		PERSISTENT, TRANSIENT, DETACHED, DELETED
 	}
 
 	/**
 	 * Prepares the save call using the given requested id.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param requestedId The id to which to associate the entity.
 	 * @param entityName The name of the entity being saved.
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 *
 	 * @return The id used to save the entity.
 	 */
 	protected Serializable saveWithRequestedId(
 			Object entity,
 			Serializable requestedId,
 			String entityName,
 			Object anything,
 			EventSource source) {
 		return performSave(
 				entity,
 				requestedId,
 				source.getEntityPersister( entityName, entity ),
 				false,
 				anything,
 				source,
 				true
 		);
 	}
 
 	/**
 	 * Prepares the save call using a newly generated id.
 	 *
 	 * @param entity The entity to be saved
 	 * @param entityName The entity-name for the entity to be saved
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable saveWithGeneratedId(
 			Object entity,
 			String entityName,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 		EntityPersister persister = source.getEntityPersister( entityName, entity );
 		Serializable generatedId = persister.getIdentifierGenerator().generate( source, entity );
 		if ( generatedId == null ) {
 			throw new IdentifierGenerationException( "null id generated for:" + entity.getClass() );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR ) {
 			return source.getIdentifier( entity );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			return performSave( entity, null, persister, true, anything, source, requiresImmediateIdAccess );
 		}
 		else {
 			// TODO: define toString()s for generators
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Generated identifier: %s, using strategy: %s",
 						persister.getIdentifierType().toLoggableString( generatedId, source.getFactory() ),
 						persister.getIdentifierGenerator().getClass().getName()
 				);
 			}
 
 			return performSave( entity, generatedId, persister, false, anything, source, true );
 		}
 	}
 
 	/**
 	 * Prepares the save call by checking the session caches for a pre-existing
 	 * entity and performing any lifecycle callbacks.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param id The id by which to save the entity.
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Is an identity column being used?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session from which the event originated.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSave(
 			Object entity,
 			Serializable id,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Saving {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 		}
 
 		final EntityKey key;
 		if ( !useIdentityColumn ) {
 			key = source.generateEntityKey( id, persister );
 			Object old = source.getPersistenceContext().getEntity( key );
 			if ( old != null ) {
 				if ( source.getPersistenceContext().getEntry( old ).getStatus() == Status.DELETED ) {
 					source.forceFlush( source.getPersistenceContext().getEntry( old ) );
 				}
 				else {
 					throw new NonUniqueObjectException( id, persister.getEntityName() );
 				}
 			}
 			persister.setIdentifier( entity, id, source );
 		}
 		else {
 			key = null;
 		}
 
 		if ( invokeSaveLifecycle( entity, persister, source ) ) {
 			return id; //EARLY EXIT
 		}
 
 		return performSaveOrReplicate(
 				entity,
 				key,
 				persister,
 				useIdentityColumn,
 				anything,
 				source,
 				requiresImmediateIdAccess
 		);
 	}
 
 	protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		// Sub-insertions should occur before containing insertion so
 		// Try to do the callback now
 		if ( persister.implementsLifecycle() ) {
 			LOG.debug( "Calling onSave()" );
 			if ( ((Lifecycle) entity).onSave( source ) ) {
 				LOG.debug( "Insertion vetoed by onSave()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Performs all the actual work needed to save an entity (well to get the save moved to
 	 * the execution queue).
 	 *
 	 * @param entity The entity to be saved
 	 * @param key The id to be used for saving the entity (or null, in the case of identity columns)
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Should an identity column be used for id generation?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of the current event.
 	 * @param requiresImmediateIdAccess Is access to the identifier required immediately
 	 * after the completion of the save?  persist(), for example, does not require this...
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSaveOrReplicate(
 			Object entity,
 			EntityKey key,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		Serializable id = key == null ? null : key.getIdentifier();
 
 		boolean inTxn = source.isTransactionInProgress();
 		boolean shouldDelayIdentityInserts = !inTxn && !requiresImmediateIdAccess;
 
 		// Put a placeholder in entries, so we don't recurse back and try to save() the
 		// same object again. QUESTION: should this be done before onSave() is called?
 		// likewise, should it be done before onUpdate()?
 		EntityEntry original = source.getPersistenceContext().addEntry(
 				entity,
 				Status.SAVING,
 				null,
 				null,
 				id,
 				null,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				false,
 				false
 		);
 
 		cascadeBeforeSave( source, persister, entity, anything );
 
 		Object[] values = persister.getPropertyValuesToInsert( entity, getMergeMap( anything ), source );
 		Type[] types = persister.getPropertyTypes();
 
 		boolean substitute = substituteValuesIfNecessary( entity, id, values, persister, source );
 
 		if ( persister.hasCollections() ) {
 			substitute = substitute || visitCollectionsBeforeSave( entity, id, values, types, source );
 		}
 
 		if ( substitute ) {
 			persister.setPropertyValues( entity, values );
 		}
 
 		TypeHelper.deepCopy(
 				values,
 				types,
 				persister.getPropertyUpdateability(),
 				values,
 				source
 		);
 
 		AbstractEntityInsertAction insert = addInsertAction(
 				values, id, entity, persister, useIdentityColumn, source, shouldDelayIdentityInserts
 		);
 
 		// postpone initializing id in case the insert has non-nullable transient dependencies
 		// that are not resolved until cascadeAfterSave() is executed
 		cascadeAfterSave( source, persister, entity, anything );
 		if ( useIdentityColumn && insert.isEarlyInsert() ) {
 			if ( !EntityIdentityInsertAction.class.isInstance( insert ) ) {
 				throw new IllegalStateException(
 						"Insert should be using an identity column, but action is of unexpected type: " +
 								insert.getClass().getName()
 				);
 			}
 			id = ((EntityIdentityInsertAction) insert).getGeneratedId();
 
 			insert.handleNaturalIdPostSaveNotifications( id );
 		}
 
-		markInterceptorDirty( entity, persister, source );
-
 		EntityEntry newEntry = source.getPersistenceContext().getEntry( entity );
 
 		if ( newEntry != original ) {
 			EntityEntryExtraState extraState = newEntry.getExtraState( EntityEntryExtraState.class );
 			if ( extraState == null ) {
 				newEntry.addExtraState( original.getExtraState( EntityEntryExtraState.class ) );
 			}
 		}
 
 		return id;
 	}
 
 	private AbstractEntityInsertAction addInsertAction(
 			Object[] values,
 			Serializable id,
 			Object entity,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			EventSource source,
 			boolean shouldDelayIdentityInserts) {
 		if ( useIdentityColumn ) {
 			EntityIdentityInsertAction insert = new EntityIdentityInsertAction(
 					values, entity, persister, isVersionIncrementDisabled(), source, shouldDelayIdentityInserts
 			);
 			source.getActionQueue().addAction( insert );
 			return insert;
 		}
 		else {
 			Object version = Versioning.getVersion( values, persister );
 			EntityInsertAction insert = new EntityInsertAction(
 					id, values, entity, version, persister, isVersionIncrementDisabled(), source
 			);
 			source.getActionQueue().addAction( insert );
 			return insert;
 		}
 	}
 
-	private void markInterceptorDirty(Object entity, EntityPersister persister, EventSource source) {
-		if ( persister.getInstrumentationMetadata().isInstrumented() ) {
-			FieldInterceptor interceptor = persister.getInstrumentationMetadata().injectInterceptor(
-					entity,
-					persister.getEntityName(),
-					null,
-					source
-			);
-			interceptor.dirty();
-		}
-	}
-
 	protected Map getMergeMap(Object anything) {
 		return null;
 	}
 
 	/**
 	 * After the save, will te version number be incremented
 	 * if the instance is modified?
 	 *
 	 * @return True if the version will be incremented on an entity change after save;
 	 *         false otherwise.
 	 */
 	protected boolean isVersionIncrementDisabled() {
 		return false;
 	}
 
 	protected boolean visitCollectionsBeforeSave(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			Type[] types,
 			EventSource source) {
 		WrapVisitor visitor = new WrapVisitor( source );
 		// substitutes into values by side-effect
 		visitor.processEntityPropertyValues( values, types );
 		return visitor.isSubstitutionRequired();
 	}
 
 	/**
 	 * Perform any property value substitution that is necessary
 	 * (interceptor callback, version initialization...)
 	 *
 	 * @param entity The entity
 	 * @param id The entity identifier
 	 * @param values The snapshot entity state
 	 * @param persister The entity persister
 	 * @param source The originating session
 	 *
 	 * @return True if the snapshot state changed such that
 	 *         reinjection of the values into the entity is required.
 	 */
 	protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		boolean substitute = source.getInterceptor().onSave(
 				entity,
 				id,
 				values,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		//keep the existing version number in the case of replicate!
 		if ( persister.isVersioned() ) {
 			substitute = Versioning.seedVersion(
 					values,
 					persister.getVersionProperty(),
 					persister.getVersionType(),
 					source
 			) || substitute;
 		}
 		return substitute;
 	}
 
 	/**
 	 * Handles the calls needed to perform pre-save cascades for the given entity.
 	 *
 	 * @param source The session from whcih the save event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity to be saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeBeforeSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to many-to-one BEFORE the parent is saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			Cascade.cascade(
 					getCascadeAction(),
 					CascadePoint.BEFORE_INSERT_AFTER_DELETE,
 					source,
 					persister,
 					entity,
 					anything
 			);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	/**
 	 * Handles to calls needed to perform post-save cascades.
 	 *
 	 * @param source The session from which the event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity beng saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeAfterSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to collections AFTER the collection owner was saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			Cascade.cascade(
 					getCascadeAction(),
 					CascadePoint.AFTER_INSERT_BEFORE_DELETE,
 					source,
 					persister,
 					entity,
 					anything
 			);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected abstract CascadingAction getCascadeAction();
 
 	/**
 	 * Determine whether the entity is persistent, detached, or transient
 	 *
 	 * @param entity The entity to check
 	 * @param entityName The name of the entity
 	 * @param entry The entity's entry in the persistence context
 	 * @param source The originating session.
 	 *
 	 * @return The state.
 	 */
 	protected EntityState getEntityState(
 			Object entity,
 			String entityName,
 			EntityEntry entry, //pass this as an argument only to avoid double looking
 			SessionImplementor source) {
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( entry != null ) { // the object is persistent
 
 			//the entity is associated with the session, so check its status
 			if ( entry.getStatus() != Status.DELETED ) {
 				// do nothing for persistent instances
 				if ( traceEnabled ) {
 					LOG.tracev( "Persistent instance of: {0}", getLoggableName( entityName, entity ) );
 				}
 				return EntityState.PERSISTENT;
 			}
 			// ie. e.status==DELETED
 			if ( traceEnabled ) {
 				LOG.tracev( "Deleted instance of: {0}", getLoggableName( entityName, entity ) );
 			}
 			return EntityState.DELETED;
 		}
 		// the object is transient or detached
 
 		// the entity is not associated with the session, so
 		// try interceptor and unsaved-value
 
 		if ( ForeignKeys.isTransient( entityName, entity, getAssumedUnsaved(), source ) ) {
 			if ( traceEnabled ) {
 				LOG.tracev( "Transient instance of: {0}", getLoggableName( entityName, entity ) );
 			}
 			return EntityState.TRANSIENT;
 		}
 		if ( traceEnabled ) {
 			LOG.tracev( "Detached instance of: {0}", getLoggableName( entityName, entity ) );
 		}
 		return EntityState.DETACHED;
 	}
 
 	protected String getLoggableName(String entityName, Object entity) {
 		return entityName == null ? entity.getClass().getName() : entityName;
 	}
 
 	protected Boolean getAssumedUnsaved() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractVisitor.java
index 76f8779f6f..85ff7ba815 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractVisitor.java
@@ -1,160 +1,160 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import org.hibernate.HibernateException;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Abstract superclass of algorithms that walk
  * a tree of property values of an entity, and
  * perform specific functionality for collections,
  * components and associated entities.
  *
  * @author Gavin King
  */
 public abstract class AbstractVisitor {
 
 	private final EventSource session;
 
 	AbstractVisitor(EventSource session) {
 		this.session = session;
 	}
 
 	/**
 	 * Dispatch each property value to processValue().
 	 *
 	 * @param values
 	 * @param types
 	 * @throws HibernateException
 	 */
 	void processValues(Object[] values, Type[] types) throws HibernateException {
 		for ( int i=0; i<types.length; i++ ) {
 			if ( includeProperty(values, i) ) {
 				processValue( i, values, types );
 			}
 		}
 	}
 	
 	/**
 	 * Dispatch each property value to processValue().
 	 *
 	 * @param values
 	 * @param types
 	 * @throws HibernateException
 	 */
 	public void processEntityPropertyValues(Object[] values, Type[] types) throws HibernateException {
 		for ( int i=0; i<types.length; i++ ) {
 			if ( includeEntityProperty(values, i) ) {
 				processValue( i, values, types );
 			}
 		}
 	}
 	
 	void processValue(int i, Object[] values, Type[] types) {
 		processValue( values[i], types[i] );
 	}
 	
 	boolean includeEntityProperty(Object[] values, int i) {
 		return includeProperty(values, i);
 	}
 	
 	boolean includeProperty(Object[] values, int i) {
 		return values[i]!= LazyPropertyInitializer.UNFETCHED_PROPERTY;
 	}
 
 	/**
 	 * Visit a component. Dispatch each property
 	 * to processValue().
 	 * @param component
 	 * @param componentType
 	 * @throws HibernateException
 	 */
 	Object processComponent(Object component, CompositeType componentType) throws HibernateException {
 		if (component!=null) {
 			processValues(
 				componentType.getPropertyValues(component, session),
 				componentType.getSubtypes()
 			);
 		}
 		return null;
 	}
 
 	/**
 	 * Visit a property value. Dispatch to the
 	 * correct handler for the property type.
 	 * @param value
 	 * @param type
 	 * @throws HibernateException
 	 */
 	final Object processValue(Object value, Type type) throws HibernateException {
 
 		if ( type.isCollectionType() ) {
 			//even process null collections
 			return processCollection( value, (CollectionType) type );
 		}
 		else if ( type.isEntityType() ) {
 			return processEntity( value, (EntityType) type );
 		}
 		else if ( type.isComponentType() ) {
 			return processComponent( value, (CompositeType) type );
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Walk the tree starting from the given entity.
 	 *
 	 * @param object
 	 * @param persister
 	 * @throws HibernateException
 	 */
 	void process(Object object, EntityPersister persister)
 	throws HibernateException {
 		processEntityPropertyValues(
 			persister.getPropertyValues( object ),
 			persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Visit a collection. Default superclass
 	 * implementation is a no-op.
 	 * @param collection
 	 * @param type
 	 * @throws HibernateException
 	 */
 	Object processCollection(Object collection, CollectionType type)
 	throws HibernateException {
 		return null;
 	}
 
 	/**
 	 * Visit a many-to-one or one-to-one associated
 	 * entity. Default superclass implementation is
 	 * a no-op.
 	 * @param value
 	 * @param entityType
 	 * @throws HibernateException
 	 */
 	Object processEntity(Object value, EntityType entityType)
 	throws HibernateException {
 		return null;
 	}
 
 	final EventSource getSession() {
 		return session;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
index 96ff7b8b6c..3a02e65e1a 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
@@ -1,677 +1,673 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Arrays;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.action.internal.DelayedPostInsertIdentifier;
 import org.hibernate.action.internal.EntityUpdateAction;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * An event that occurs for each entity instance at flush time
  *
  * @author Gavin King
  */
 public class DefaultFlushEntityEventListener implements FlushEntityEventListener {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultFlushEntityEventListener.class );
 
 	/**
 	 * make sure user didn't mangle the id
 	 */
 	public void checkId(Object object, EntityPersister persister, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( id != null && id instanceof DelayedPostInsertIdentifier ) {
 			// this is a situation where the entity id is assigned by a post-insert generator
 			// and was saved outside the transaction forcing it to be delayed
 			return;
 		}
 
 		if ( persister.canExtractIdOutOfEntity() ) {
 
 			Serializable oid = persister.getIdentifier( object, session );
 			if ( id == null ) {
 				throw new AssertionFailure( "null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)" );
 			}
 			if ( !persister.getIdentifierType().isEqual( id, oid, session.getFactory() ) ) {
 				throw new HibernateException(
 						"identifier of an instance of " + persister.getEntityName() + " was altered from "
 								+ id + " to " + oid
 				);
 			}
 		}
 
 	}
 
 	private void checkNaturalId(
 			EntityPersister persister,
 			EntityEntry entry,
 			Object[] current,
 			Object[] loaded,
 			SessionImplementor session) {
 		if ( persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY ) {
 			if ( !persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// SHORT-CUT: if the natural id is mutable (!immutable), no need to do the below checks
 				// EARLY EXIT!!!
 				return;
 			}
 
 			final int[] naturalIdentifierPropertiesIndexes = persister.getNaturalIdentifierProperties();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 			final boolean[] propertyUpdateability = persister.getPropertyUpdateability();
 
 			final Object[] snapshot = loaded == null
 					? session.getPersistenceContext().getNaturalIdSnapshot( entry.getId(), persister )
 					: session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( loaded, persister );
 
 			for ( int i = 0; i < naturalIdentifierPropertiesIndexes.length; i++ ) {
 				final int naturalIdentifierPropertyIndex = naturalIdentifierPropertiesIndexes[i];
 				if ( propertyUpdateability[naturalIdentifierPropertyIndex] ) {
 					// if the given natural id property is updatable (mutable), there is nothing to check
 					continue;
 				}
 
 				final Type propertyType = propertyTypes[naturalIdentifierPropertyIndex];
 				if ( !propertyType.isEqual( current[naturalIdentifierPropertyIndex], snapshot[i] ) ) {
 					throw new HibernateException(
 							String.format(
 									"An immutable natural identifier of entity %s was altered from %s to %s",
 									persister.getEntityName(),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											snapshot[i],
 											session.getFactory()
 									),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											current[naturalIdentifierPropertyIndex],
 											session.getFactory()
 									)
 							)
 					);
 				}
 			}
 		}
 	}
 
 	/**
 	 * Flushes a single entity's state to the database, by scheduling
 	 * an update action, if necessary
 	 */
 	public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
 		final Object entity = event.getEntity();
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final EntityPersister persister = entry.getPersister();
 		final Status status = entry.getStatus();
 		final Type[] types = persister.getPropertyTypes();
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck( entity );
 
 		final Object[] values = getValues( entity, entry, mightBeDirty, session );
 
 		event.setPropertyValues( values );
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values );
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if ( substitute ) {
 				persister.setPropertyValues( entity, values );
 			}
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor( session, entity ).processEntityPropertyValues( values, types );
 			}
 		}
 
 	}
 
 	private Object[] getValues(Object entity, EntityEntry entry, boolean mightBeDirty, SessionImplementor session) {
 		final Object[] loadedState = entry.getLoadedState();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 
 		final Object[] values;
 		if ( status == Status.DELETED ) {
 			//grab its state saved at deletion
 			values = entry.getDeletedState();
 		}
 		else if ( !mightBeDirty && loadedState != null ) {
 			values = loadedState;
 		}
 		else {
 			checkId( entity, persister, entry.getId(), session );
 
 			// grab its current state
 			values = persister.getPropertyValues( entity );
 
 			checkNaturalId( persister, entry, values, loadedState, session );
 		}
 		return values;
 	}
 
 	private boolean wrapCollections(
 			EventSource session,
 			EntityPersister persister,
 			Type[] types,
 			Object[] values
 	) {
 		if ( persister.hasCollections() ) {
 
 			// wrap up any new collections directly referenced by the object
 			// or its components
 
 			// NOTE: we need to do the wrap here even if its not "dirty",
 			// because collections need wrapping but changes to _them_
 			// don't dirty the container. Also, for versioned data, we
 			// need to wrap before calling searchForDirtyCollections
 
 			WrapVisitor visitor = new WrapVisitor( session );
 			// substitutes into values by side-effect
 			visitor.processEntityPropertyValues( values, types );
 			return visitor.isSubstitutionRequired();
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isUpdateNecessary(final FlushEntityEvent event, final boolean mightBeDirty) {
 		final Status status = event.getEntityEntry().getStatus();
 		if ( mightBeDirty || status == Status.DELETED ) {
 			// compare to cached state (ignoring collections unless versioned)
 			dirtyCheck( event );
 			if ( isUpdateNecessary( event ) ) {
 				return true;
 			}
 			else {
-				if ( event.getEntityEntry().getPersister().getInstrumentationMetadata().isInstrumented() ) {
-					event.getEntityEntry()
-							.getPersister()
-							.getInstrumentationMetadata()
-							.extractInterceptor( event.getEntity() )
-							.clearDirty();
+				if ( SelfDirtinessTracker.class.isInstance( event.getEntity() ) ) {
+					( (SelfDirtinessTracker) event.getEntity() ).$$_hibernate_clearDirtyAttributes();
 				}
 				event.getSession()
 						.getFactory()
 						.getCustomEntityDirtinessStrategy()
 						.resetDirty( event.getEntity(), event.getEntityEntry().getPersister(), event.getSession() );
 				return false;
 			}
 		}
 		else {
 			return hasDirtyCollections( event, event.getEntityEntry().getPersister(), status );
 		}
 	}
 
 	private boolean scheduleUpdate(final FlushEntityEvent event) {
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final Object entity = event.getEntity();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 		final Object[] values = event.getPropertyValues();
 
 		if ( LOG.isTraceEnabled() ) {
 			if ( status == Status.DELETED ) {
 				if ( !persister.isMutable() ) {
 					LOG.tracev(
 							"Updating immutable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 					);
 				}
 				else if ( !entry.isModifiableEntity() ) {
 					LOG.tracev(
 							"Updating non-modifiable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 					);
 				}
 				else {
 					LOG.tracev(
 							"Updating deleted entity: ",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 					);
 				}
 			}
 			else {
 				LOG.tracev(
 						"Updating entity: {0}",
 						MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 				);
 			}
 		}
 
 		final boolean intercepted = !entry.isBeingReplicated() && handleInterception( event );
 
 		// increment the version number (if necessary)
 		final Object nextVersion = getNextVersion( event );
 
 		// if it was dirtied by a collection only
 		int[] dirtyProperties = event.getDirtyProperties();
 		if ( event.isDirtyCheckPossible() && dirtyProperties == null ) {
 			if ( !intercepted && !event.hasDirtyCollection() ) {
 				throw new AssertionFailure( "dirty, but no dirty properties" );
 			}
 			dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
 		}
 
 		// check nullability but do not doAfterTransactionCompletion command execute
 		// we'll use scheduled updates for that.
 		new Nullability( session ).checkNullability( values, persister, true );
 
 		// schedule the update
 		// note that we intentionally do _not_ pass in currentPersistentState!
 		session.getActionQueue().addAction(
 				new EntityUpdateAction(
 						entry.getId(),
 						values,
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						( status == Status.DELETED && !entry.isModifiableEntity() ?
 								persister.getPropertyValues( entity ) :
 								entry.getLoadedState() ),
 						entry.getVersion(),
 						nextVersion,
 						entity,
 						entry.getRowId(),
 						persister,
 						session
 				)
 		);
 
 		return intercepted;
 	}
 
 	protected boolean handleInterception(FlushEntityEvent event) {
 		SessionImplementor session = event.getSession();
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		Object entity = event.getEntity();
 
 		//give the Interceptor a chance to modify property values
 		final Object[] values = event.getPropertyValues();
 		final boolean intercepted = invokeInterceptor( session, entity, entry, values, persister );
 
 		//now we might need to recalculate the dirtyProperties array
 		if ( intercepted && event.isDirtyCheckPossible() && !event.isDirtyCheckHandledByInterceptor() ) {
 			int[] dirtyProperties;
 			if ( event.hasDatabaseSnapshot() ) {
 				dirtyProperties = persister.findModified( event.getDatabaseSnapshot(), values, entity, session );
 			}
 			else {
 				dirtyProperties = persister.findDirty( values, entry.getLoadedState(), entity, session );
 			}
 			event.setDirtyProperties( dirtyProperties );
 		}
 
 		return intercepted;
 	}
 
 	protected boolean invokeInterceptor(
 			SessionImplementor session,
 			Object entity,
 			EntityEntry entry,
 			final Object[] values,
 			EntityPersister persister) {
 		return session.getInterceptor().onFlushDirty(
 				entity,
 				entry.getId(),
 				values,
 				entry.getLoadedState(),
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Convience method to retreive an entities next version value
 	 */
 	private Object getNextVersion(FlushEntityEvent event) throws HibernateException {
 
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		if ( persister.isVersioned() ) {
 
 			Object[] values = event.getPropertyValues();
 
 			if ( entry.isBeingReplicated() ) {
 				return Versioning.getVersion( values, persister );
 			}
 			else {
 				int[] dirtyProperties = event.getDirtyProperties();
 
 				final boolean isVersionIncrementRequired = isVersionIncrementRequired(
 						event,
 						entry,
 						persister,
 						dirtyProperties
 				);
 
 				final Object nextVersion = isVersionIncrementRequired ?
 						Versioning.increment( entry.getVersion(), persister.getVersionType(), event.getSession() ) :
 						entry.getVersion(); //use the current version
 
 				Versioning.setVersion( values, nextVersion, persister );
 
 				return nextVersion;
 			}
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private boolean isVersionIncrementRequired(
 			FlushEntityEvent event,
 			EntityEntry entry,
 			EntityPersister persister,
 			int[] dirtyProperties
 	) {
 		final boolean isVersionIncrementRequired = entry.getStatus() != Status.DELETED && (
 				dirtyProperties == null ||
 						Versioning.isVersionIncrementRequired(
 								dirtyProperties,
 								event.hasDirtyCollection(),
 								persister.getPropertyVersionability()
 						)
 		);
 		return isVersionIncrementRequired;
 	}
 
 	/**
 	 * Performs all necessary checking to determine if an entity needs an SQL update
 	 * to synchronize its state to the database. Modifies the event by side-effect!
 	 * Note: this method is quite slow, avoid calling if possible!
 	 */
 	protected final boolean isUpdateNecessary(FlushEntityEvent event) throws HibernateException {
 
 		EntityPersister persister = event.getEntityEntry().getPersister();
 		Status status = event.getEntityEntry().getStatus();
 
 		if ( !event.isDirtyCheckPossible() ) {
 			return true;
 		}
 		else {
 
 			int[] dirtyProperties = event.getDirtyProperties();
 			if ( dirtyProperties != null && dirtyProperties.length != 0 ) {
 				return true; //TODO: suck into event class
 			}
 			else {
 				return hasDirtyCollections( event, persister, status );
 			}
 
 		}
 	}
 
 	private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
 		if ( isCollectionDirtyCheckNecessary( persister, status ) ) {
 			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(
 					event.getSession(),
 					persister.getPropertyVersionability()
 			);
 			visitor.processEntityPropertyValues( event.getPropertyValues(), persister.getPropertyTypes() );
 			boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
 			event.setHasDirtyCollection( hasDirtyCollections );
 			return hasDirtyCollections;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isCollectionDirtyCheckNecessary(EntityPersister persister, Status status) {
 		return ( status == Status.MANAGED || status == Status.READ_ONLY ) &&
 				persister.isVersioned() &&
 				persister.hasCollections();
 	}
 
 	/**
 	 * Perform a dirty check, and attach the results to the event
 	 */
 	protected void dirtyCheck(final FlushEntityEvent event) throws HibernateException {
 
 		final Object entity = event.getEntity();
 		final Object[] values = event.getPropertyValues();
 		final SessionImplementor session = event.getSession();
 		final EntityEntry entry = event.getEntityEntry();
 		final EntityPersister persister = entry.getPersister();
 		final Serializable id = entry.getId();
 		final Object[] loadedState = entry.getLoadedState();
 
 		int[] dirtyProperties = session.getInterceptor().findDirty(
 				entity,
 				id,
 				values,
 				loadedState,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		if ( dirtyProperties == null ) {
 			if ( entity instanceof SelfDirtinessTracker ) {
 				if ( ( (SelfDirtinessTracker) entity ).$$_hibernate_hasDirtyAttributes() ) {
 					dirtyProperties = persister.resolveAttributeIndexes( ( (SelfDirtinessTracker) entity ).$$_hibernate_getDirtyAttributes() );
 				}
 				else {
 					dirtyProperties = new int[0];
 				}
 			}
 			else {
 				// see if the custom dirtiness strategy can tell us...
 				class DirtyCheckContextImpl implements CustomEntityDirtinessStrategy.DirtyCheckContext {
 					int[] found;
 
 					@Override
 					public void doDirtyChecking(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 						found = new DirtyCheckAttributeInfoImpl( event ).visitAttributes( attributeChecker );
 						if ( found != null && found.length == 0 ) {
 							found = null;
 						}
 					}
 				}
 				DirtyCheckContextImpl context = new DirtyCheckContextImpl();
 				session.getFactory().getCustomEntityDirtinessStrategy().findDirty(
 						entity,
 						persister,
 						(Session) session,
 						context
 				);
 				dirtyProperties = context.found;
 			}
 		}
 
 		event.setDatabaseSnapshot( null );
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties == null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			try {
 				session.getEventListenerManager().dirtyCalculationStart();
 
 				interceptorHandledDirtyCheck = false;
 				// object loaded by update()
 				cannotDirtyCheck = loadedState == null;
 				if ( !cannotDirtyCheck ) {
 					// dirty check against the usual snapshot of the entity
 					dirtyProperties = persister.findDirty( values, loadedState, entity, session );
 				}
 				else if ( entry.getStatus() == Status.DELETED && !event.getEntityEntry().isModifiableEntity() ) {
 					// A non-modifiable (e.g., read-only or immutable) entity needs to be have
 					// references to transient entities set to null before being deleted. No other
 					// fields should be updated.
 					if ( values != entry.getDeletedState() ) {
 						throw new IllegalStateException(
 								"Entity has status Status.DELETED but values != entry.getDeletedState"
 						);
 					}
 					// Even if loadedState == null, we can dirty-check by comparing currentState and
 					// entry.getDeletedState() because the only fields to be updated are those that
 					// refer to transient entities that are being set to null.
 					// - currentState contains the entity's current property values.
 					// - entry.getDeletedState() contains the entity's current property values with
 					//   references to transient entities set to null.
 					// - dirtyProperties will only contain properties that refer to transient entities
 					final Object[] currentState = persister.getPropertyValues( event.getEntity() );
 					dirtyProperties = persister.findDirty( entry.getDeletedState(), currentState, entity, session );
 					cannotDirtyCheck = false;
 				}
 				else {
 					// dirty check against the database snapshot, if possible/necessary
 					final Object[] databaseSnapshot = getDatabaseSnapshot( session, persister, id );
 					if ( databaseSnapshot != null ) {
 						dirtyProperties = persister.findModified( databaseSnapshot, values, entity, session );
 						cannotDirtyCheck = false;
 						event.setDatabaseSnapshot( databaseSnapshot );
 					}
 				}
 			}
 			finally {
 				session.getEventListenerManager().dirtyCalculationEnd( dirtyProperties != null );
 			}
 		}
 		else {
 			// the Interceptor handled the dirty checking
 			cannotDirtyCheck = false;
 			interceptorHandledDirtyCheck = true;
 		}
 
 		logDirtyProperties( id, dirtyProperties, persister );
 
 		event.setDirtyProperties( dirtyProperties );
 		event.setDirtyCheckHandledByInterceptor( interceptorHandledDirtyCheck );
 		event.setDirtyCheckPossible( !cannotDirtyCheck );
 
 	}
 
 	private class DirtyCheckAttributeInfoImpl implements CustomEntityDirtinessStrategy.AttributeInformation {
 		private final FlushEntityEvent event;
 		private final EntityPersister persister;
 		private final int numberOfAttributes;
 		private int index;
 
 		private DirtyCheckAttributeInfoImpl(FlushEntityEvent event) {
 			this.event = event;
 			this.persister = event.getEntityEntry().getPersister();
 			this.numberOfAttributes = persister.getPropertyNames().length;
 		}
 
 		@Override
 		public EntityPersister getContainingPersister() {
 			return persister;
 		}
 
 		@Override
 		public int getAttributeIndex() {
 			return index;
 		}
 
 		@Override
 		public String getName() {
 			return persister.getPropertyNames()[index];
 		}
 
 		@Override
 		public Type getType() {
 			return persister.getPropertyTypes()[index];
 		}
 
 		@Override
 		public Object getCurrentValue() {
 			return event.getPropertyValues()[index];
 		}
 
 		Object[] databaseSnapshot;
 
 		@Override
 		public Object getLoadedValue() {
 			if ( databaseSnapshot == null ) {
 				databaseSnapshot = getDatabaseSnapshot( event.getSession(), persister, event.getEntityEntry().getId() );
 			}
 			return databaseSnapshot[index];
 		}
 
 		public int[] visitAttributes(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 			databaseSnapshot = null;
 			index = 0;
 
 			final int[] indexes = new int[numberOfAttributes];
 			int count = 0;
 			for (; index < numberOfAttributes; index++ ) {
 				if ( attributeChecker.isDirty( this ) ) {
 					indexes[count++] = index;
 				}
 			}
 			return Arrays.copyOf( indexes, count );
 		}
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
 		if ( dirtyProperties != null && dirtyProperties.length > 0 && LOG.isTraceEnabled() ) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[dirtyProperties.length];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[dirtyProperties[i]];
 			}
 			LOG.tracev(
 					"Found dirty properties [{0}] : {1}",
 					MessageHelper.infoString( persister.getEntityName(), id ),
 					dirtyPropertyNames
 			);
 		}
 	}
 
 	private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
 		if ( persister.isSelectBeforeUpdateRequired() ) {
 			Object[] snapshot = session.getPersistenceContext()
 					.getDatabaseSnapshot( id, persister );
 			if ( snapshot == null ) {
 				//do we even really need this? the update will fail anyway....
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 			return snapshot;
 		}
 		// TODO: optimize away this lookup for entities w/o unsaved-value="undefined"
 		final EntityKey entityKey = session.generateEntityKey( id, persister );
 		return session.getPersistenceContext().getCachedDatabaseSnapshot( entityKey );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
index 833b6aedc0..d73f0d143b 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
@@ -1,508 +1,500 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.CascadePoint;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EntityCopyObserver;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.MergeEvent;
 import org.hibernate.event.spi.MergeEventListener;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default copy event listener used by hibernate for copying entities
  * in response to generated copy events.
  *
  * @author Gavin King
  */
 public class DefaultMergeEventListener extends AbstractSaveEventListener implements MergeEventListener {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultMergeEventListener.class );
 
 	private String entityCopyObserverStrategy;
 
 	@Override
 	protected Map getMergeMap(Object anything) {
 		return ( (MergeContext) anything ).invertMap();
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event) throws HibernateException {
 		final EntityCopyObserver entityCopyObserver = createEntityCopyObserver( event.getSession().getFactory() );
 		final MergeContext mergeContext = new MergeContext( event.getSession(), entityCopyObserver );
 		try {
 			onMerge( event, mergeContext );
 			entityCopyObserver.topLevelMergeComplete( event.getSession() );
 		}
 		finally {
 			entityCopyObserver.clear();
 			mergeContext.clear();
 		}
 	}
 
 	private EntityCopyObserver createEntityCopyObserver(SessionFactoryImplementor sessionFactory) {
 		final ServiceRegistry serviceRegistry = sessionFactory.getServiceRegistry();
 		if ( entityCopyObserverStrategy == null ) {
 			final ConfigurationService configurationService
 					= serviceRegistry.getService( ConfigurationService.class );
 			entityCopyObserverStrategy = configurationService.getSetting(
 					"hibernate.event.merge.entity_copy_observer",
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					EntityCopyNotAllowedObserver.SHORT_NAME
 			);
 			LOG.debugf( "EntityCopyObserver strategy: %s", entityCopyObserverStrategy );
 		}
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 		return strategySelector.resolveStrategy( EntityCopyObserver.class, entityCopyObserverStrategy );
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
 
 		final MergeContext copyCache = (MergeContext) copiedAlready;
 		final EventSource source = event.getSession();
 		final Object original = event.getOriginal();
 
 		if ( original != null ) {
 
 			final Object entity;
 			if ( original instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) original ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
 					LOG.trace( "Ignoring uninitialized proxy" );
 					event.setResult( source.load( li.getEntityName(), li.getIdentifier() ) );
 					return; //EARLY EXIT!
 				}
 				else {
 					entity = li.getImplementation();
 				}
 			}
 			else {
 				entity = original;
 			}
 
 			if ( copyCache.containsKey( entity ) &&
 					( copyCache.isOperatedOn( entity ) ) ) {
 				LOG.trace( "Already in merge process" );
 				event.setResult( entity );
 			}
 			else {
 				if ( copyCache.containsKey( entity ) ) {
 					LOG.trace( "Already in copyCache; setting in merge process" );
 					copyCache.setOperatedOn( entity, true );
 				}
 				event.setEntity( entity );
 				EntityState entityState = null;
 
 				// Check the persistence context for an entry relating to this
 				// entity to be merged...
 				EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 				if ( entry == null ) {
 					EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 					Serializable id = persister.getIdentifier( entity, source );
 					if ( id != null ) {
 						final EntityKey key = source.generateEntityKey( id, persister );
 						final Object managedEntity = source.getPersistenceContext().getEntity( key );
 						entry = source.getPersistenceContext().getEntry( managedEntity );
 						if ( entry != null ) {
 							// we have specialized case of a detached entity from the
 							// perspective of the merge operation.  Specifically, we
 							// have an incoming entity instance which has a corresponding
 							// entry in the current persistence context, but registered
 							// under a different entity instance
 							entityState = EntityState.DETACHED;
 						}
 					}
 				}
 
 				if ( entityState == null ) {
 					entityState = getEntityState( entity, event.getEntityName(), entry, source );
 				}
 
 				switch ( entityState ) {
 					case DETACHED:
 						entityIsDetached( event, copyCache );
 						break;
 					case TRANSIENT:
 						entityIsTransient( event, copyCache );
 						break;
 					case PERSISTENT:
 						entityIsPersistent( event, copyCache );
 						break;
 					default: //DELETED
 						throw new ObjectDeletedException(
 								"deleted instance passed to merge",
 								null,
 								getLoggableName( event.getEntityName(), entity )
 						);
 				}
 			}
 
 		}
 
 	}
 
 	protected void entityIsPersistent(MergeEvent event, Map copyCache) {
 		LOG.trace( "Ignoring persistent instance" );
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		( (MergeContext) copyCache ).put( entity, entity, true );  //before cascade!
 
 		cascadeOnMerge( source, persister, entity, copyCache );
 		copyValues( persister, entity, entity, source, copyCache );
 
 		event.setResult( entity );
 	}
 
 	protected void entityIsTransient(MergeEvent event, Map copyCache) {
 
 		LOG.trace( "Merging transient instance" );
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final String entityName = event.getEntityName();
 		final EntityPersister persister = source.getEntityPersister( entityName, entity );
 
 		final Serializable id = persister.hasIdentifierProperty() ?
 				persister.getIdentifier( entity, source ) :
 				null;
 		if ( copyCache.containsKey( entity ) ) {
 			persister.setIdentifier( copyCache.get( entity ), id, source );
 		}
 		else {
 			( (MergeContext) copyCache ).put( entity, source.instantiate( persister, id ), true ); //before cascade!
 		}
 		final Object copy = copyCache.get( entity );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		//cascadeOnMerge(event, persister, entity, copyCache, Cascades.CASCADE_BEFORE_MERGE);
 		super.cascadeBeforeSave( source, persister, entity, copyCache );
 		copyValues( persister, entity, copy, source, copyCache, ForeignKeyDirection.FROM_PARENT );
 
 		saveTransientEntity( copy, entityName, event.getRequestedId(), source, copyCache );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		super.cascadeAfterSave( source, persister, entity, copyCache );
 		copyValues( persister, entity, copy, source, copyCache, ForeignKeyDirection.TO_PARENT );
 
 		event.setResult( copy );
 	}
 
 	private void saveTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache) {
 		//this bit is only *really* absolutely necessary for handling
 		//requestedId, but is also good if we merge multiple object
 		//graphs, since it helps ensure uniqueness
 		if ( requestedId == null ) {
 			saveWithGeneratedId( entity, entityName, copyCache, source, false );
 		}
 		else {
 			saveWithRequestedId( entity, requestedId, entityName, copyCache, source );
 		}
 	}
 
 	protected void entityIsDetached(MergeEvent event, Map copyCache) {
 
 		LOG.trace( "Merging detached instance" );
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 		final String entityName = persister.getEntityName();
 
 		Serializable id = event.getRequestedId();
 		if ( id == null ) {
 			id = persister.getIdentifier( entity, source );
 		}
 		else {
 			// check that entity id = requestedId
 			Serializable entityId = persister.getIdentifier( entity, source );
 			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getFactory() ) ) {
 				throw new HibernateException( "merge requested with id not matching id of passed entity" );
 			}
 		}
 
 		String previousFetchProfile = source.getLoadQueryInfluencers().getInternalFetchProfile();
 		source.getLoadQueryInfluencers().setInternalFetchProfile( "merge" );
 		//we must clone embedded composite identifiers, or
 		//we will get back the same instance that we pass in
 		final Serializable clonedIdentifier = (Serializable) persister.getIdentifierType()
 				.deepCopy( id, source.getFactory() );
 		final Object result = source.get( entityName, clonedIdentifier );
 		source.getLoadQueryInfluencers().setInternalFetchProfile( previousFetchProfile );
 
 		if ( result == null ) {
 			//TODO: we should throw an exception if we really *know* for sure
 			//      that this is a detached instance, rather than just assuming
 			//throw new StaleObjectStateException(entityName, id);
 
 			// we got here because we assumed that an instance
 			// with an assigned id was detached, when it was
 			// really persistent
 			entityIsTransient( event, copyCache );
 		}
 		else {
 			( (MergeContext) copyCache ).put( entity, result, true ); //before cascade!
 
 			final Object target = source.getPersistenceContext().unproxy( result );
 			if ( target == entity ) {
 				throw new AssertionFailure( "entity was not detached" );
 			}
 			else if ( !source.getEntityName( target ).equals( entityName ) ) {
 				throw new WrongClassException(
 						"class of the given object did not match class of persistent copy",
 						event.getRequestedId(),
 						entityName
 				);
 			}
 			else if ( isVersionChanged( entity, source, persister, target ) ) {
 				if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 					source.getFactory().getStatisticsImplementor()
 							.optimisticFailure( entityName );
 				}
 				throw new StaleObjectStateException( entityName, id );
 			}
 
 			// cascade first, so that all unsaved objects get their
 			// copy created before we actually copy
 			cascadeOnMerge( source, persister, entity, copyCache );
 			copyValues( persister, entity, target, source, copyCache );
 
 			//copyValues works by reflection, so explicitly mark the entity instance dirty
 			markInterceptorDirty( entity, target, persister );
 
 			event.setResult( result );
 		}
 
 	}
 
 	private void markInterceptorDirty(final Object entity, final Object target, EntityPersister persister) {
-		if ( persister.getInstrumentationMetadata().isInstrumented() ) {
-			FieldInterceptor interceptor = persister.getInstrumentationMetadata().extractInterceptor( target );
-			if ( interceptor != null ) {
-				interceptor.dirty();
-			}
-		}
-
 		// for enhanced entities, copy over the dirty attributes
 		if ( entity instanceof SelfDirtinessTracker && target instanceof SelfDirtinessTracker ) {
 			// clear, because setting the embedded attributes dirties them
 			( (SelfDirtinessTracker) target ).$$_hibernate_clearDirtyAttributes();
 
 			for ( String fieldName : ( (SelfDirtinessTracker) entity ).$$_hibernate_getDirtyAttributes() ) {
 				( (SelfDirtinessTracker) target ).$$_hibernate_trackChange( fieldName );
 			}
 		}
 	}
 
 	private boolean isVersionChanged(Object entity, EventSource source, EntityPersister persister, Object target) {
 		if ( !persister.isVersioned() ) {
 			return false;
 		}
 		// for merging of versioned entities, we consider the version having
 		// been changed only when:
 		// 1) the two version values are different;
 		//      *AND*
 		// 2) The target actually represents database state!
 		//
 		// This second condition is a special case which allows
 		// an entity to be merged during the same transaction
 		// (though during a seperate operation) in which it was
 		// originally persisted/saved
 		boolean changed = !persister.getVersionType().isSame(
 				persister.getVersion( target ),
 				persister.getVersion( entity )
 		);
 
 		// TODO : perhaps we should additionally require that the incoming entity
 		// version be equivalent to the defined unsaved-value?
 		return changed && existsInDatabase( target, source, persister );
 	}
 
 	private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
 		EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			Serializable id = persister.getIdentifier( entity, source );
 			if ( id != null ) {
 				final EntityKey key = source.generateEntityKey( id, persister );
 				final Object managedEntity = source.getPersistenceContext().getEntity( key );
 				entry = source.getPersistenceContext().getEntry( managedEntity );
 			}
 		}
 
 		return entry != null && entry.isExistsInDatabase();
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache) {
 		final Object[] copiedValues = TypeHelper.replace(
 				persister.getPropertyValues( entity ),
 				persister.getPropertyValues( target ),
 				persister.getPropertyTypes(),
 				source,
 				target,
 				copyCache
 		);
 
 		persister.setPropertyValues( target, copiedValues );
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 
 		final Object[] copiedValues;
 
 		if ( foreignKeyDirection == ForeignKeyDirection.TO_PARENT ) {
 			// this is the second pass through on a merge op, so here we limit the
 			// replacement to associations types (value types were already replaced
 			// during the first pass)
 			copiedValues = TypeHelper.replaceAssociations(
 					persister.getPropertyValues( entity ),
 					persister.getPropertyValues( target ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 		else {
 			copiedValues = TypeHelper.replace(
 					persister.getPropertyValues( entity ),
 					persister.getPropertyValues( target ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 
 		persister.setPropertyValues( target, copiedValues );
 	}
 
 	/**
 	 * Perform any cascades needed as part of this copy event.
 	 *
 	 * @param source The merge event being processed.
 	 * @param persister The persister of the entity being copied.
 	 * @param entity The entity being copied.
 	 * @param copyCache A cache of already copied instance.
 	 */
 	protected void cascadeOnMerge(
 			final EventSource source,
 			final EntityPersister persister,
 			final Object entity,
 			final Map copyCache
 	) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			Cascade.cascade(
 					getCascadeAction(),
 					CascadePoint.BEFORE_MERGE,
 					source,
 					persister,
 					entity,
 					copyCache
 			);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 
 	@Override
 	protected CascadingAction getCascadeAction() {
 		return CascadingActions.MERGE;
 	}
 
 	@Override
 	protected Boolean getAssumedUnsaved() {
 		return Boolean.FALSE;
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
 	protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 			throws HibernateException {
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
 	protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 			throws HibernateException {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
index 196bd256fa..6ce85af19e 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
@@ -1,212 +1,212 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal.log;
 
 import org.jboss.logging.BasicLogger;
 import org.jboss.logging.Logger;
 import org.jboss.logging.annotations.LogMessage;
 import org.jboss.logging.annotations.Message;
 import org.jboss.logging.annotations.MessageLogger;
 import org.jboss.logging.annotations.ValidIdRange;
 
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * Class to consolidate logging about usage of deprecated features.
  *
  * @author Steve Ebersole
  */
 @MessageLogger( projectCode = "HHH" )
 @ValidIdRange( min = 90000001, max = 90001000 )
 public interface DeprecationLogger extends BasicLogger {
 	public static final DeprecationLogger DEPRECATION_LOGGER = Logger.getMessageLogger(
 			DeprecationLogger.class,
 			"org.hibernate.orm.deprecation"
 	);
 
 	/**
 	 * Log about usage of deprecated Scanner setting
 	 */
 	@LogMessage( level = INFO )
 	@Message(
 			value = "Found usage of deprecated setting for specifying Scanner [hibernate.ejb.resource_scanner]; " +
 					"use [hibernate.archive.scanner] instead",
 			id = 90000001
 	)
 	public void logDeprecatedScannerSetting();
 
 	/**
 	 * Log message indicating the use of multiple EntityModes for a single entity.
 	 */
 	@LogMessage( level = WARN )
 	@Message(
 			value = "Support for an entity defining multiple entity-modes is deprecated",
 			id = 90000002
 	)
 	public void logDeprecationOfMultipleEntityModeSupport();
 
 	/**
 	 * Log message indicating the use of DOM4J EntityMode.
 	 */
 	@LogMessage( level = WARN )
 	@Message(
 			value = "Use of DOM4J entity-mode is considered deprecated",
 			id = 90000003
 	)
 	public void logDeprecationOfDomEntityModeSupport();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been " +
 					"removed, embed-xml attributes are no longer supported and should be removed from mappings.",
 			id = 90000004
 	)
 	public void logDeprecationOfEmbedXmlSupport();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Defining an entity [%s] with no physical id attribute is no longer supported; please map the " +
 					"identifier to a physical entity attribute",
 			id = 90000005
 	)
 	public void logDeprecationOfNonNamedIdAttribute(String entityName);
 
 	/**
 	 * Log a warning about an attempt to specify no-longer-supported NamingStrategy
 	 *
 	 * @param setting - The old setting that indicates the NamingStrategy to use
 	 * @param implicitInstead - The new setting that indicates the ImplicitNamingStrategy to use
 	 * @param physicalInstead - The new setting that indicates the PhysicalNamingStrategy to use
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via setting [%s]; NamingStrategy " +
 					"has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy; use [%s] or [%s], respectively, instead.",
 			id = 90000006
 	)
 	void logDeprecatedNamingStrategySetting(String setting, String implicitInstead, String physicalInstead);
 
 	/**
 	 * Log a warning about an attempt to specify unsupported NamingStrategy
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via command-line argument [--naming]. " +
 					"NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy; use [--implicit-naming] or [--physical-naming], respectively, instead.",
 			id = 90000007
 	)
 	void logDeprecatedNamingStrategyArgument();
 
 	/**
 	 * Log a warning about an attempt to specify unsupported NamingStrategy
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via Ant task argument. " +
 					"NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy.",
 			id = 90000008
 	)
 	void logDeprecatedNamingStrategyAntArgument();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "The outer-join attribute on <many-to-many> has been deprecated. " +
 					"Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, " +
 					"<bag>, <idbag>, or <list>, which will only initialize entities (not as " +
 					"a proxy) as needed.",
 			id = 90000009
 	)
 	void deprecatedManyToManyOuterJoin();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "The fetch attribute on <many-to-many> has been deprecated. " +
 					"Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, " +
 					"<bag>, <idbag>, or <list>, which will only initialize entities (not as " +
 					"a proxy) as needed.",
 			id = 90000010
 	)
 	void deprecatedManyToManyFetch();
 
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "org.hibernate.hql.spi.TemporaryTableBulkIdStrategy (temporary) has been deprecated in favor of the" +
 					" more specific org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy (local_temporary).",
 			id = 90000011
 	)
 	void logDeprecationOfTemporaryTableBulkIdStrategy();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead.  Support for obsolete DTD/XSD namespaces may be removed at any time.",
 			id = 90000012)
 	void recognizedObsoleteHibernateNamespace(
 			String oldHibernateNamespace,
 			String hibernateNamespace);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 90000013,
 			value = "Named ConnectionProvider [%s] has been deprecated in favor of %s; that provider will be used instead.  Update your settings"
 	)
 	void connectionProviderClassDeprecated(
 			String providerClassName,
 			String actualProviderClassName);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 90000014,
 			value = "Found use of deprecated [%s] sequence-based id generator; " +
 					"use org.hibernate.id.enhanced.SequenceStyleGenerator instead.  " +
 					"See Hibernate Domain Model Mapping Guide for details."
 	)
 	void deprecatedSequenceGenerator(String generatorImpl);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 90000015,
 			value = "Found use of deprecated [%s] table-based id generator; " +
 					"use org.hibernate.id.enhanced.TableGenerator instead.  " +
 					"See Hibernate Domain Model Mapping Guide for details."
 	)
 	void deprecatedTableGenerator(String generatorImpl);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 90000016,
 			value = "Found use of deprecated 'collection property' syntax in HQL/JPQL query [%2$s.%1$s]; " +
 					"use collection function syntax instead [%1$s(%2$s)]."
 	)
 	void logDeprecationOfCollectionPropertiesInHql(String collectionPropertyName, String alias);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 90000017,
 			value = "Found use of deprecated entity-type selector syntax in HQL/JPQL query ['%1$s.class']; use TYPE operator instead : type(%1$s)"
 	)
 	void logDeprecationOfClassEntityTypeSelector(String path);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 90000018,
 			value = "Found use of deprecated transaction factory setting [%s]; use the new TransactionCoordinatorBuilder settings [%s] instead"
 	)
 	void logDeprecatedTransactionFactorySetting(String legacySettingName, String updatedSettingName);
 
-	@LogMessage(level = WARN)
-	@Message(
-			id = 90000019,
-			value = "You are using the deprecated legacy bytecode enhancement feature which has been superseded by a vastly improved bytecode enhancer."
-	)
-	void logDeprecatedBytecodeEnhancement();
+//	@LogMessage(level = WARN)
+//	@Message(
+//			id = 90000019,
+//			value = "You are using the deprecated legacy bytecode enhancement feature which has been superseded by a vastly improved bytecode enhancer."
+//	)
+//	void logDeprecatedBytecodeEnhancement();
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java b/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java
index 0c010f16ef..acb222fbc1 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java
@@ -1,116 +1,116 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal.util;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Renders entities and query parameters to a nicely readable string.
  *
  * @author Gavin King
  */
 public final class EntityPrinter {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( EntityPrinter.class );
 
 	private SessionFactoryImplementor factory;
 
 	/**
 	 * Renders an entity to a string.
 	 *
 	 * @param entityName the entity name
 	 * @param entity an actual entity object, not a proxy!
 	 *
 	 * @return the entity rendered to a string
 	 */
 	public String toString(String entityName, Object entity) throws HibernateException {
 		EntityPersister entityPersister = factory.getEntityPersister( entityName );
 
 		if ( entityPersister == null ) {
 			return entity.getClass().getName();
 		}
 
 		Map<String, String> result = new HashMap<String, String>();
 
 		if ( entityPersister.hasIdentifierProperty() ) {
 			result.put(
 					entityPersister.getIdentifierPropertyName(),
 					entityPersister.getIdentifierType().toLoggableString(
 							entityPersister.getIdentifier( entity ),
 							factory
 					)
 			);
 		}
 
 		Type[] types = entityPersister.getPropertyTypes();
 		String[] names = entityPersister.getPropertyNames();
 		Object[] values = entityPersister.getPropertyValues( entity );
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( !names[i].startsWith( "_" ) ) {
 				String strValue = values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ?
 						values[i].toString() :
 						types[i].toLoggableString( values[i], factory );
 				result.put( names[i], strValue );
 			}
 		}
 		return entityName + result.toString();
 	}
 
 	public String toString(Type[] types, Object[] values) throws HibernateException {
 		StringBuilder buffer = new StringBuilder();
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( types[i] != null ) {
 				buffer.append( types[i].toLoggableString( values[i], factory ) ).append( ", " );
 			}
 		}
 		return buffer.toString();
 	}
 
 	public String toString(Map<String, TypedValue> namedTypedValues) throws HibernateException {
 		Map<String, String> result = new HashMap<String, String>();
 		for ( Map.Entry<String, TypedValue> entry : namedTypedValues.entrySet() ) {
 			result.put(
 					entry.getKey(), entry.getValue().getType().toLoggableString(
 							entry.getValue().getValue(),
 							factory
 					)
 			);
 		}
 		return result.toString();
 	}
 
 	// Cannot use Map as an argument because it clashes with the previous method (due to type erasure)
 	public void toString(Iterable<Map.Entry<EntityKey, Object>> entitiesByEntityKey) throws HibernateException {
 		if ( !LOG.isDebugEnabled() || !entitiesByEntityKey.iterator().hasNext() ) {
 			return;
 		}
 
 		LOG.debug( "Listing entities:" );
 		int i = 0;
 		for ( Map.Entry<EntityKey, Object> entityKeyAndEntity : entitiesByEntityKey ) {
 			if ( i++ > 20 ) {
 				LOG.debug( "More......" );
 				break;
 			}
 			LOG.debug( toString( entityKeyAndEntity.getKey().getEntityName(), entityKeyAndEntity.getValue() ) );
 		}
 	}
 
 	public EntityPrinter(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index ecfe0cff93..ddcf61e043 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1580 +1,1577 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
-import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
-import org.hibernate.engine.spi.PersistentAttributeInterceptable;
-import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 				SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractEntityPersister.class );
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryHelper cacheEntryHelper;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityEntryFactory entityEntryFactory;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	protected final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 
 	private final List<Integer> lobProperties = new ArrayList<Integer>();
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set<String> affectingFetchProfileNames = new HashSet<String>();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	private final boolean useReferenceCacheEntries;
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 	}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	protected abstract String filterFragment(String alias, Set<String> treatAsDeclarations);
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains( entityName );
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		System.arraycopy( sqlLazyUpdateStrings, 1, result, 1, getTableSpan() - 1 );
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 *
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[getTableSpan()];
 			for ( int property : dirtyProperties ) {
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan( property ) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 						Versioning.isVersionIncrementRequired(
 								dirtyProperties,
 								hasDirtyCollection,
 								getPropertyVersionability()
 						);
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
 	}
 
 	@SuppressWarnings("UnnecessaryBoxing")
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 
 		if ( entityMetamodel.isMutable() ) {
 			this.entityEntryFactory = MutableEntityEntryFactory.INSTANCE;
 		}
 		else {
 			this.entityEntryFactory = ImmutableEntityEntryFactory.INSTANCE;
 		}
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate(
 					factory.getDialect(),
 					factory.getSqlFunctionRegistry()
 			);
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( (Column) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName(
 					factory.getDialect()
 			);
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ?
 				"( " + persistentClass.getWhere() + ") " :
 				null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate(
 						sqlWhereString,
 						factory.getDialect(),
 						factory.getSqlFunctionRegistry()
 				);
 
 		// PROPERTIES
 
-		final boolean lazyAvailable = isInstrumented() || entityMetamodel.isLazyLoadingBytecodeEnhanced();
+		final boolean lazyAvailable = isInstrumented();
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = (Selectable) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect(), prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column) thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
 			if ( prop.isLob() && getFactory().getDialect().forceLobAsLastValue() ) {
 				lobProperties.add( i );
 			}
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = (Selectable) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column) thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
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
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
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
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = (CascadeStyle) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = (FetchMode) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = (Boolean) iter.next();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
 		// Check if we can use Reference Cached entities in 2lc
 		// todo : should really validate that the cache access type is read-only
 		boolean refCacheEntries = true;
 		if ( !factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled() ) {
 			refCacheEntries = false;
 		}
 
 		// for now, limit this to just entities that:
 		// 		1) are immutable
 		if ( entityMetamodel.isMutable() ) {
 			refCacheEntries = false;
 		}
 
 		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
 		for ( Type type : getSubclassPropertyTypeClosure() ) {
 			if ( type.isAssociationType() ) {
 				refCacheEntries = false;
 			}
 		}
 
 		useReferenceCacheEntries = refCacheEntries;
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 
 	}
 
 	protected CacheEntryHelper buildCacheEntryHelper() {
 		if ( cacheAccessStrategy == null ) {
 			// the entity defined no caching...
 			return NoopCacheEntryHelper.INSTANCE;
 		}
 
 		if ( canUseReferenceCacheEntries() ) {
 			entityMetamodel.setLazy( false );
 			// todo : do we also need to unset proxy factory?
 			return new ReferenceCacheEntryHelper( this );
 		}
 
 		return factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled()
 				? new StructuredCacheEntryHelper( this )
 				: new StandardCacheEntryHelper( this );
 	}
 
 	public boolean canUseReferenceCacheEntries() {
 		return useReferenceCacheEntries;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( String lazyPropertyName : lazyPropertyNames ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyName );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int colNumber : colNumbers ) {
 				if ( colNumber != -1 ) {
 					columnNumbers.add( colNumber );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int formNumber : formNumbers ) {
 				if ( formNumber != -1 ) {
 					formulaNumbers.add( formNumber );
 				}
 			}
 		}
 
 		if ( columnNumbers.size() == 0 && formulaNumbers.size() == 0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect(
 				ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers )
 		);
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session) {
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 
 		if ( hasCollections() ) {
 			final Type type = getPropertyType( fieldName );
 			if ( type.isCollectionType() ) {
 				// we have a condition where a collection attribute is being access via enhancement:
 				// 		we can circumvent all the rest and just return the PersistentCollection
 				final CollectionType collectionType = (CollectionType) type;
 				final CollectionPersister persister = factory.getCollectionPersister( collectionType.getRole() );
 
 				// Get/create the collection, and make sure it is initialized!  This initialized part is
 				// different from proxy-based scenarios where we have to create the PersistentCollection
 				// reference "ahead of time" to add as a reference to the proxy.  For bytecode solutions
 				// we are not creating the PersistentCollection ahead of time, but instead we are creating
 				// it on first request through the enhanced entity.
 
 				// see if there is already a collection instance associated with the session
 				// 		NOTE : can this ever happen?
 				final Serializable key = getCollectionKey( persister, entity, entry, session );
 				PersistentCollection collection = session.getPersistenceContext().getCollection( new CollectionKey( persister, key ) );
 				if ( collection == null ) {
 					collection = collectionType.instantiate( session, persister, key );
 					collection.setOwner( entity );
 					session.getPersistenceContext().addUninitializedCollection( persister, collection, key );
 				}
 
 				// Initialize it
 				session.initializeCollection( collection, false );
 
 				if ( collectionType.isArrayType() ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 
 				// EARLY EXIT!!!
 				return collection;
 			}
 		}
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString(
 							this,
 							id,
 							getFactory()
 					), fieldName
 			);
 		}
 
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final EntityRegionAccessStrategy cache = getCacheAccessStrategy();
 			final Object cacheKey = cache.generateCacheKey(id, this, session.getFactory(), session.getTenantIdentifier() );
 			final Object ce = CacheHelper.fromSharedCache( session, cacheKey, cache );
 			if ( ce != null ) {
 				final CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure( ce, factory );
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	protected Serializable getCollectionKey(
 			CollectionPersister persister,
 			Object owner,
 			EntityEntry ownerEntry,
 			SessionImplementor session) {
 		final CollectionType collectionType = persister.getCollectionType();
 
 		if ( ownerEntry != null ) {
 			// this call only works when the owner is associated with the Session, which is not always the case
 			return collectionType.getKeyOfOwner( owner, session );
 		}
 
 		if ( collectionType.getLHSPropertyName() == null ) {
 			// collection key is defined by the owning entity identifier
 			return persister.getOwnerEntityPersister().getIdentifier( owner, session );
 		}
 		else {
 			return (Serializable) persister.getOwnerEntityPersister().getPropertyValue( owner, collectionType.getLHSPropertyName() );
 		}
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
 		if ( !hasLazyProperties() ) {
 			throw new AssertionFailure( "no lazy properties" );
 		}
 
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
 						ps = session.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet(
 								rs,
 								lazyPropertyColumnAliases[j],
 								session,
 								entity
 						);
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getJdbcCoordinator().getResourceRegistry().release( ps );
 					session.getJdbcCoordinator().afterStatementExecution();
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
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
 					disassembledValues[lazyPropertyNumbers[j]],
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
 			snapshot[lazyPropertyNumbers[j]] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSessionFactoryOptions().isJdbcBatchVersionedData();
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
 			LOG.tracev(
 					"Getting current persistent state for: {0}", MessageHelper.infoString(
 							this,
 							id,
 							getFactory()
 					)
 			);
 		}
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 							values[i] = types[i].hydrate(
 									rs,
 									getPropertyAliases( "", i ),
 									session,
 									null
 							); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session)
 			throws HibernateException {
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
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
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
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
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
 			final String columnReference = StringHelper.replace(
 					columnTemplate,
 					Template.TEMPLATE,
 					uniquePropertyTableAlias
 			);
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace(
 					formulaTemplate,
 					Template.TEMPLATE,
 					uniquePropertyTableAlias
 			);
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
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.INSERT );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.ALWAYS );
 	}
 
 	private String generateGeneratedValuesSelectString(final GenerationTiming generationTimingToMatch) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment(
 				getRootAlias(),
 				new InclusionChecker() {
 					@Override
 					public boolean includeProperty(int propertyNumber) {
 						final InDatabaseValueGenerationStrategy generationStrategy
 								= entityMetamodel.getInDatabaseValueGenerationStrategies()[propertyNumber];
 						return generationStrategy != null
 								&& timingsMatch( generationStrategy.getGenerationTiming(), generationTimingToMatch );
 					}
 				}
 		);
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
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 				.append(
 						StringHelper.join(
 								"=? and ",
 								aliasedIdColumns
 						)
 				)
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
@@ -3247,2168 +3244,2158 @@ public abstract class AbstractEntityPersister
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
 
 		// apply any pre-update in-memory value generation
 		if ( getEntityMetamodel().hasPreUpdateGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesUpdate() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( (Session) session, object );
 					setPropertyValue( object, i, fields[i] );
 					// todo : probably best to add to dirtyFields if not-null
 				}
 			}
 		}
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && !isMutable() ) {
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
 		else if ( !isModifiableEntity( entry ) ) {
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
 		// apply any pre-insert in-memory value generation
 		preInsertInMemoryValueGeneration( fields, object, session );
 
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
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		// apply any pre-insert in-memory value generation
 		preInsertInMemoryValueGeneration( fields, object, session );
 
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
 
 	private void preInsertInMemoryValueGeneration(Object[] fields, Object object, SessionImplementor session) {
 		if ( getEntityMetamodel().hasPreInsertGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesInsert() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( (Session) session, object );
 					setPropertyValue( object, i, fields[i] );
 				}
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
 			if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
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
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator( alias ), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator( alias ), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
 	}
 
 	@Override
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin(
 				alias,
 				innerJoin,
 				includeSubclasses,
 				Collections.<String>emptySet()
 		).toFromFragmentString();
 	}
 
 	@Override
 	public String fromJoinFragment(
 			String alias,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toFromFragmentString();
 	}
 
 	@Override
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin(
 				alias,
 				innerJoin,
 				includeSubclasses,
 				Collections.<String>emptySet()
 		).toWhereFragmentString();
 	}
 
 	@Override
 	public String whereJoinFragment(
 			String alias,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(
 			String name,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// IMPL NOTE : all joins join to the pk of the driving table
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() );
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int j = 1; j < tableSpan; j++ ) {
 			final JoinType joinType = determineSubclassTableJoinType(
 					j,
 					innerJoin,
 					includeSubclasses,
 					treatAsDeclarations
 			);
 
 			if ( joinType != null && joinType != JoinType.NONE ) {
 				join.addJoin(
 						getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						joinType
 				);
 			}
 		}
 		return join;
 	}
 
 	protected JoinType determineSubclassTableJoinType(
 			int subclassTableNumber,
 			boolean canInnerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 
 		if ( isClassOrSuperclassTable( subclassTableNumber ) ) {
 			final boolean shouldInnerJoin = canInnerJoin
 					&& !isInverseTable( subclassTableNumber )
 					&& !isNullableTable( subclassTableNumber );
 			// the table is either this persister's driving table or (one of) its super class persister's driving
 			// tables which can be inner joined as long as the `shouldInnerJoin` condition resolves to true
 			return shouldInnerJoin ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN;
 		}
 
 		// otherwise we have a subclass table and need to look a little deeper...
 
 		// IMPL NOTE : By default includeSubclasses indicates that all subclasses should be joined and that each
 		// subclass ought to be joined by outer-join.  However, TREAT-AS always requires that an inner-join be used
 		// so we give TREAT-AS higher precedence...
 
 		if ( isSubclassTableIndicatedByTreatAsDeclarations( subclassTableNumber, treatAsDeclarations ) ) {
 			return JoinType.INNER_JOIN;
 		}
 
 		if ( includeSubclasses
 				&& !isSubclassTableSequentialSelect( subclassTableNumber )
 				&& !isSubclassTableLazy( subclassTableNumber ) ) {
 			return JoinType.LEFT_OUTER_JOIN;
 		}
 
 		return JoinType.NONE;
 	}
 
 	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
 			int subclassTableNumber,
 			Set<String> treatAsDeclarations) {
 		return false;
 	}
 
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int i = 1; i < tableNumbers.length; i++ ) {
 			final int j = tableNumbers[i];
 			jf.addJoin(
 					getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j )
 							? JoinType.LEFT_OUTER_JOIN
 							: JoinType.INNER_JOIN
 			);
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(
 			final int[] subclassColumnNumbers,
 			final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate(
 						subalias,
 						columnReaderTemplates[columnNumber],
 						columnAliases[columnNumber]
 				);
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
 		return StringHelper.join(
 				"=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) )
 		) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 			final int[] columnNumbers,
 			final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias(
 				getRootAlias(),
 				drivingTable
 		); //we *could* regerate this inside each called method!
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
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	/**
 	 * Post-construct is a callback for AbstractEntityPersister subclasses to call after they are all done with their
 	 * constructor processing.  It allows AbstractEntityPersister to extend its construction after all subclass-specific
 	 * details have been handled.
 	 *
 	 * @param mapping The mapping
 	 *
 	 * @throws MappingException Indicates a problem accessing the Mapping
 	 */
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths( mapping );
 
 		//doLateInit();
 		prepareEntityIdentifierDefinition();
 	}
 
 	private void doLateInit() {
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
 			identityDelegate = ( (PostInsertIdentifierGenerator) getIdentifierGenerator() )
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
 
 	public final void postInstantiate() throws MappingException {
 		doLateInit();
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 		doPostInstantiate();
 	}
 
 	protected void doPostInstantiate() {
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
 				LockMode.UPGRADE_SKIPLOCKED,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_SKIPLOCKED )
 		);
 		loaders.put(
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
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC ) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 
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
 		return load( id, optionalObject, new LockOptions().setLockMode( lockMode ), session );
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
 
 		final UniqueEntityLoader loader = getAppropriateLoader( lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEntityGraph(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().getFetchGraph() != null || session.getLoadQueryInfluencers()
 				.getLoadGraph() != null;
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		for ( String s : session.getLoadQueryInfluencers().getEnabledFetchProfileNames() ) {
 			if ( affectingFetchProfileNames.contains( s ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	protected UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan(
 				lockOptions.getLockMode()
 		) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return (UniqueEntityLoader) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( isAffectedByEntityGraph( session ) ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return (UniqueEntityLoader) getLoaders().get( lockOptions.getLockMode() );
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
 		final boolean[] propsToUpdate = new boolean[entityMetamodel.getPropertySpan()];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty()] ) {
 			propsToUpdate[getVersionProperty()] =
 					Versioning.isVersionIncrementRequired(
 							dirtyProperties,
 							hasDirtyCollection,
 							getPropertyVersionability()
 					);
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
 	 *
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 *
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
 	 *
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 *
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
 				String propertyName = entityMetamodel.getProperties()[props[i]].getName();
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
 		return (VersionType) locateVersionType();
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
-		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
-			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata()
-					.extractInterceptor( entity );
-			if ( interceptor != null ) {
-				interceptor.setSession( session );
-			}
-			else {
-				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
+		if ( getEntityMetamodel().getBytecodeEnhancementMetadata().isEnhancedForLazyLoading() ) {
+			LazyAttributeLoadingInterceptor interceptor = getEntityMetamodel().getBytecodeEnhancementMetadata().extractInterceptor( entity );
+			if ( interceptor == null ) {
+				getEntityMetamodel().getBytecodeEnhancementMetadata().injectInterceptor(
 						entity,
-						getEntityName(),
 						null,
 						session
 				);
-				fieldInterceptor.dirty();
 			}
-		}
-
-		if ( entity instanceof PersistentAttributeInterceptable ) {
-			PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) entity ).$$_hibernate_getInterceptor();
-			if ( interceptor != null && interceptor instanceof LazyAttributeLoader ) {
-				( (LazyAttributeLoader) interceptor ).setSession( session );
+			else {
+				interceptor.setSession( session );
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( !hasNaturalIdentifier() ) {
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
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final EntityRegionAccessStrategy cache = getCacheAccessStrategy();
 			final Object ck = cache.generateCacheKey( id, this, session.getFactory(), session.getTenantIdentifier() );
 			final Object ce = CacheHelper.fromSharedCache( session, ck, getCacheAccessStrategy() );
 			if ( ce != null ) {
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
 		// skip proxy instantiation if entity is bytecode enhanced
-		return entityMetamodel.isLazy() && !entityMetamodel.isLazyLoadingBytecodeEnhanced();
+		return entityMetamodel.isLazy() && !entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
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
-		return entityMetamodel.isInstrumented();
+		return entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && getEntityMetamodel().isVersionGenerated();
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability()[getVersionProperty()];
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
 
 	/**
 	 * @deprecated no simple, direct replacement
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return null;
 	}
 
 	/**
 	 * @deprecated no simple, direct replacement
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return null;
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
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
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
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure( "no insert-generated properties" );
 		}
 		processGeneratedProperties(
 				id,
 				entity,
 				state,
 				session,
 				sqlInsertGeneratedValuesSelectString,
 				GenerationTiming.INSERT
 		);
 	}
 
 	public void processUpdateGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure( "no update-generated properties" );
 		}
 		processGeneratedProperties(
 				id,
 				entity,
 				state,
 				session,
 				sqlUpdateGeneratedValuesSelectString,
 				GenerationTiming.ALWAYS
 		);
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session,
 			String selectionSQL,
 			GenerationTiming matchTiming) {
 		// force immediate execution of the insert batch (if one)
 		session.getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 										MessageHelper.infoString( this, id, getFactory() )
 						);
 					}
 					int propertyIndex = -1;
 					for ( NonIdentifierAttribute attribute : entityMetamodel.getProperties() ) {
 						propertyIndex++;
 						final ValueGeneration valueGeneration = attribute.getValueGenerationStrategy();
 						if ( isReadRequired( valueGeneration, matchTiming ) ) {
 							final Object hydratedState = attribute.getType().hydrate(
 									rs, getPropertyAliases(
 											"",
 											propertyIndex
 									), session, entity
 							);
 							state[propertyIndex] = attribute.getType().resolve( hydratedState, session, entity );
 							setPropertyValue( entity, propertyIndex, state[propertyIndex] );
 						}
 					}
 //					for ( int i = 0; i < getPropertySpan(); i++ ) {
 //						if ( includeds[i] != ValueInclusion.NONE ) {
 //							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 //							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 //							setPropertyValue( entity, i, state[i] );
 //						}
 //					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	/**
 	 * Whether the given value generation strategy requires to read the value from the database or not.
 	 */
 	private boolean isReadRequired(ValueGeneration valueGeneration, GenerationTiming matchTiming) {
 		return valueGeneration != null &&
 				valueGeneration.getValueGenerator() == null &&
 				timingsMatch( valueGeneration.getGenerationTiming(), matchTiming );
 	}
 
 	private boolean timingsMatch(GenerationTiming timing, GenerationTiming matchTiming) {
 		return
 				( matchTiming == GenerationTiming.INSERT && timing.includesInsert() ) ||
 						( matchTiming == GenerationTiming.ALWAYS && timing.includesUpdate() );
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
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException(
 					"persistent class did not define a natural-id : " + MessageHelper.infoString(
 							this
 					)
 			);
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[getPropertySpan()];
 		Type[] extractionTypes = new Type[naturalIdPropertyCount];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[naturalIdPropertyIndexes[i]];
 			naturalIdMarkers[naturalIdPropertyIndexes[i]] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 				.append(
 						StringHelper.join(
 								"=? and ",
 								aliasedIdColumns
 						)
 				)
 				.append( "=?" )
 				.append( whereJoinFragment( getRootAlias(), true, false ) )
 				.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[naturalIdPropertyCount];
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate(
 								rs, getPropertyAliases(
 										"",
 										naturalIdPropertyIndexes[i]
 								), session, null
 						);
 						if ( extractionTypes[i].isEntityType() ) {
 							snapshot[i] = extractionTypes[i].resolve( snapshot[i], session, owner );
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
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
 			PreparedStatement ps = session
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
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					final Object hydratedId = getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 					return (Serializable) getIdentifierType().resolve( hydratedId, session, null );
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
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
 		boolean[] nullness = new boolean[naturalIdValues.length];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( !hasNaturalIdentifier() ) {
 			throw new HibernateException(
 					"Attempt to build natural-id -> PK resolution query for entity that does not define natural id"
 			);
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && !ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
 		return generateEntityIdByNaturalIdSql( valueNullness );
 	}
 
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable;
 	}
 
 	private boolean determineNaturalIdNullability() {
 		boolean[] nullability = getPropertyNullability();
 		for ( int position : getNaturalIdentifierProperties() ) {
 			// if any individual property is nullable, return false
 			if ( nullability[position] ) {
 				return false;
 			}
 		}
 		// return true if we found no individually nullable properties
 		return true;
 	}
 
 	private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
 		EntityPersister rootPersister = getFactory().getEntityPersister( getRootEntityName() );
 		if ( rootPersister != this ) {
 			if ( rootPersister instanceof AbstractEntityPersister ) {
 				return ( (AbstractEntityPersister) rootPersister ).generateEntityIdByNaturalIdSql( valueNullness );
 			}
 		}
 
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		int valuesIndex = -1;
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			valuesIndex++;
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			if ( valueNullness != null && valueNullness[valuesIndex] ) {
 				whereClause.append( StringHelper.join( " is null and ", aliasedPropertyColumns ) ).append( " is null" );
 			}
 			else {
 				whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 			}
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
-	public EntityInstrumentationMetadata getInstrumentationMetadata() {
-		return entityMetamodel.getInstrumentationMetadata();
+	public BytecodeEnhancementMetadata getInstrumentationMetadata() {
+		return entityMetamodel.getBytecodeEnhancementMetadata();
 	}
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
 	protected String determineTableName(Table table, JdbcEnvironment jdbcEnvironment) {
 		if ( table.getSubselect() != null ) {
 			return "( " + table.getSubselect() + " )";
 		}
 
 		return jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				table.getQualifiedTableName(),
 				jdbcEnvironment.getDialect()
 		);
 	}
 
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
 		return this.entityEntryFactory;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public interface CacheEntryHelper {
 		CacheEntryStructure getCacheEntryStructure();
 
 		CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
 
 
 	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityIdentifierDefinition entityIdentifierDefinition;
 	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
 	private Iterable<AttributeDefinition> attributeDefinitions;
 
 	@Override
 	public void generateEntityDefinition() {
 		prepareEntityIdentifierDefinition();
 		collectAttributeDefinitions();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		return entityIdentifierDefinition;
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return attributeDefinitions;
 	}
 
 
 	private void prepareEntityIdentifierDefinition() {
 		if ( entityIdentifierDefinition != null ) {
 			return;
 		}
 		final Type idType = getIdentifierType();
 
 		if ( !idType.isComponentType() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildSimpleEncapsulatedIdentifierDefinition( this );
 			return;
 		}
 
 		final CompositeType cidType = (CompositeType) idType;
 		if ( !cidType.isEmbedded() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildEncapsulatedCompositeIdentifierDefinition( this );
 			return;
 		}
 
 		entityIdentifierDefinition =
 				EntityIdentifierDefinitionHelper.buildNonEncapsulatedCompositeIdentifierDefinition( this );
 	}
 
 	private void collectAttributeDefinitions(
 			Map<String, AttributeDefinition> attributeDefinitionsByName,
 			EntityMetamodel metamodel) {
 		for ( int i = 0; i < metamodel.getPropertySpan(); i++ ) {
 			final AttributeDefinition attributeDefinition = metamodel.getProperties()[i];
 			// Don't replace an attribute definition if it is already in attributeDefinitionsByName
 			// because the new value will be from a subclass.
 			final AttributeDefinition oldAttributeDefinition = attributeDefinitionsByName.get(
 					attributeDefinition.getName()
 			);
 			if ( oldAttributeDefinition != null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracef(
 							"Ignoring subclass attribute definition [%s.%s] because it is defined in a superclass ",
 							entityMetamodel.getName(),
 							attributeDefinition.getName()
 					);
 				}
 			}
 			else {
 				attributeDefinitionsByName.put( attributeDefinition.getName(), attributeDefinition );
 			}
 		}
 
 		// see if there are any subclass persisters...
 		final Set<String> subClassEntityNames = metamodel.getSubclassEntityNames();
 		if ( subClassEntityNames == null ) {
 			return;
 		}
 
 		// see if we can find the persisters...
 		for ( String subClassEntityName : subClassEntityNames ) {
 			if ( metamodel.getName().equals( subClassEntityName ) ) {
 				// skip it
 				continue;
 			}
 			try {
 				final EntityPersister subClassEntityPersister = factory.getEntityPersister( subClassEntityName );
 				collectAttributeDefinitions( attributeDefinitionsByName, subClassEntityPersister.getEntityMetamodel() );
 			}
 			catch (MappingException e) {
 				throw new IllegalStateException(
 						String.format(
 								"Could not locate subclass EntityPersister [%s] while processing EntityPersister [%s]",
 								subClassEntityName,
 								metamodel.getName()
 						),
 						e
 				);
 			}
 		}
 	}
 
 	private void collectAttributeDefinitions() {
 		// todo : I think this works purely based on luck atm
 		// 		specifically in terms of the sub/super class entity persister(s) being available.  Bit of chicken-egg
 		// 		problem there:
 		//			* If I do this during postConstruct (as it is now), it works as long as the
 		//			super entity persister is already registered, but I don't think that is necessarily true.
 		//			* If I do this during postInstantiate then lots of stuff in postConstruct breaks if we want
 		//			to try and drive SQL generation on these (which we do ultimately).  A possible solution there
 		//			would be to delay all SQL generation until postInstantiate
 
 		Map<String, AttributeDefinition> attributeDefinitionsByName = new LinkedHashMap<String, AttributeDefinition>();
 		collectAttributeDefinitions( attributeDefinitionsByName, getEntityMetamodel() );
 
 
 //		EntityMetamodel currentEntityMetamodel = this.getEntityMetamodel();
 //		while ( currentEntityMetamodel != null ) {
 //			for ( int i = 0; i < currentEntityMetamodel.getPropertySpan(); i++ ) {
 //				attributeDefinitions.add( currentEntityMetamodel.getProperties()[i] );
 //			}
 //			// see if there is a super class EntityMetamodel
 //			final String superEntityName = currentEntityMetamodel.getSuperclass();
 //			if ( superEntityName != null ) {
 //				currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //			}
 //			else {
 //				currentEntityMetamodel = null;
 //			}
 //		}
 
 		this.attributeDefinitions = Collections.unmodifiableList(
 				new ArrayList<AttributeDefinition>( attributeDefinitionsByName.values() )
 		);
 //		// todo : leverage the attribute definitions housed on EntityMetamodel
 //		// 		for that to work, we'd have to be able to walk our super entity persister(s)
 //		this.attributeDefinitions = new Iterable<AttributeDefinition>() {
 //			@Override
 //			public Iterator<AttributeDefinition> iterator() {
 //				return new Iterator<AttributeDefinition>() {
 ////					private final int numberOfAttributes = countSubclassProperties();
 ////					private final int numberOfAttributes = entityMetamodel.getPropertySpan();
 //
 //					EntityMetamodel currentEntityMetamodel = entityMetamodel;
 //					int numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //
 //					private int currentAttributeNumber;
 //
 //					@Override
 //					public boolean hasNext() {
 //						return currentEntityMetamodel != null
 //								&& currentAttributeNumber < numberOfAttributesInCurrentEntityMetamodel;
 //					}
 //
 //					@Override
 //					public AttributeDefinition next() {
 //						final int attributeNumber = currentAttributeNumber;
 //						currentAttributeNumber++;
 //						final AttributeDefinition next = currentEntityMetamodel.getProperties()[ attributeNumber ];
 //
 //						if ( currentAttributeNumber >= numberOfAttributesInCurrentEntityMetamodel ) {
 //							// see if there is a super class EntityMetamodel
 //							final String superEntityName = currentEntityMetamodel.getSuperclass();
 //							if ( superEntityName != null ) {
 //								currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //								if ( currentEntityMetamodel != null ) {
 //									numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //									currentAttributeNumber = 0;
 //								}
 //							}
 //						}
 //
 //						return next;
 //					}
 //
 //					@Override
 //					public void remove() {
 //						throw new UnsupportedOperationException( "Remove operation not supported here" );
 //					}
 //				};
 //			}
 //		};
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
index 2a43d37e27..f81797a6df 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
@@ -1,796 +1,796 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.cache.spi.OptimisticCacheSource;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Contract describing mapping information and persistence logic for a particular strategy of entity mapping.  A given
  * persister instance corresponds to a given mapped entity class.
  * <p/>
  * Implementations must be thread-safe (preferably immutable).
  * <p/>
  * Unless a custom {@link org.hibernate.persister.spi.PersisterFactory} is used, it is expected
  * that implementations of EntityPersister define a constructor accepting the following arguments:<ol>
  *     <li>
  *         {@link org.hibernate.mapping.PersistentClass} - describes the metadata about the entity
  *         to be handled by the persister
  *     </li>
  *     <li>
  *         {@link EntityRegionAccessStrategy} - the second level caching strategy for this entity
  *     </li>
  *     <li>
  *         {@link NaturalIdRegionAccessStrategy} - the second level caching strategy for the natural-id
  *         defined for this entity, if one
  *     </li>
  *     <li>
  *         {@link org.hibernate.persister.spi.PersisterCreationContext} - access to additional
  *         information useful while constructing the persister.
  *     </li>
  * </ol>
  *
  * @author Gavin King
  * @author Steve Ebersole
  *
  * @see org.hibernate.persister.spi.PersisterFactory
  * @see org.hibernate.persister.spi.PersisterClassResolver
  */
 public interface EntityPersister extends OptimisticCacheSource, EntityDefinition {
 
 	/**
 	 * The property name of the "special" identifier property in HQL
 	 */
 	public static final String ENTITY_ID = "id";
 
 	/**
 	 * Generate the entity definition for this object. This must be done for all
 	 * entity persisters before calling {@link #postInstantiate()}.
 	 */
 	public void generateEntityDefinition();
 
 	/**
 	 * Finish the initialization of this object. {@link #generateEntityDefinition()}
 	 * must be called for all entity persisters before calling this method.
 	 * <p/>
 	 * Called only once per {@link org.hibernate.SessionFactory} lifecycle,
 	 * after all entity persisters have been instantiated.
 	 *
 	 * @throws org.hibernate.MappingException Indicates an issue in the metadata.
 	 */
 	public void postInstantiate() throws MappingException;
 
 	/**
 	 * Return the SessionFactory to which this persister "belongs".
 	 *
 	 * @return The owning SessionFactory.
 	 */
 	public SessionFactoryImplementor getFactory();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     // stuff that is persister-centric and/or EntityInfo-centric ~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the EntityEntryFactory indicated for the entity mapped by this persister.
 	 *
 	 * @return The proper EntityEntryFactory.
 	 */
 	public EntityEntryFactory getEntityEntryFactory();
 
 	/**
 	 * Returns an object that identifies the space in which identifiers of
 	 * this entity hierarchy are unique.  Might be a table name, a JNDI URL, etc.
 	 *
 	 * @return The root entity name.
 	 */
 	public String getRootEntityName();
 
 	/**
 	 * The entity name which this persister maps.
 	 *
 	 * @return The name of the entity which this persister maps.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Retrieve the underlying entity metamodel instance...
 	 *
 	 *@return The metamodel
 	 */
 	public EntityMetamodel getEntityMetamodel();
 
 	/**
 	 * Determine whether the given name represents a subclass entity
 	 * (or this entity itself) of the entity mapped by this persister.
 	 *
 	 * @param entityName The entity name to be checked.
 	 * @return True if the given entity name represents either the entity
 	 * mapped by this persister or one of its subclass entities; false
 	 * otherwise.
 	 */
 	public boolean isSubclassEntityName(String entityName);
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class only.
 	 * <p/>
 	 * For most implementations, this returns the complete set of table names
 	 * to which instances of the mapped entity are persisted (not accounting
 	 * for superclass entity mappings).
 	 *
 	 * @return The property spaces.
 	 */
 	public Serializable[] getPropertySpaces();
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class and its subclasses.
 	 * <p/>
 	 * Much like {@link #getPropertySpaces()}, except that here we include subclass
 	 * entity spaces.
 	 *
 	 * @return The query spaces.
 	 */
 	public Serializable[] getQuerySpaces();
 
 	/**
 	 * Determine whether this entity supports dynamic proxies.
 	 *
 	 * @return True if the entity has dynamic proxy support; false otherwise.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections.
 	 *
 	 * @return True if the entity does contain persistent collections; false otherwise.
 	 */
 	public boolean hasCollections();
 
 	/**
 	 * Determine whether any properties of this entity are considered mutable.
 	 *
 	 * @return True if any properties of the entity are mutable; false otherwise (meaning none are).
 	 */
 	public boolean hasMutableProperties();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections
 	 * which are fetchable by subselect?
 	 *
 	 * @return True if the entity contains collections fetchable by subselect; false otherwise.
 	 */
 	public boolean hasSubselectLoadableCollections();
 
 	/**
 	 * Determine whether this entity has any non-none cascading.
 	 *
 	 * @return True if the entity has any properties with a cascade other than NONE;
 	 * false otherwise (aka, no cascading).
 	 */
 	public boolean hasCascades();
 
 	/**
 	 * Determine whether instances of this entity are considered mutable.
 	 *
 	 * @return True if the entity is considered mutable; false otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Determine whether the entity is inherited one or more other entities.
 	 * In other words, is this entity a subclass of other entities.
 	 *
 	 * @return True if other entities extend this entity; false otherwise.
 	 */
 	public boolean isInherited();
 
 	/**
 	 * Are identifiers of this entity assigned known before the insert execution?
 	 * Or, are they generated (in the database) by the insert execution.
 	 *
 	 * @return True if identifiers for this entity are generated by the insert
 	 * execution.
 	 */
 	public boolean isIdentifierAssignedByInsert();
 
 	/**
 	 * Get the type of a particular property by name.
 	 *
 	 * @param propertyName The name of the property for which to retrieve
 	 * the type.
 	 * @return The type.
 	 * @throws org.hibernate.MappingException Typically indicates an unknown
 	 * property name.
 	 */
 	public Type getPropertyType(String propertyName) throws MappingException;
 
 	/**
 	 * Compare the two snapshots to determine if they represent dirty state.
 	 *
 	 * @param currentState The current snapshot
 	 * @param previousState The baseline snapshot
 	 * @param owner The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all dirty properties, or null if no properties
 	 * were dirty.
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session);
 
 	/**
 	 * Compare the two snapshots to determine if they represent modified state.
 	 *
 	 * @param old The baseline snapshot
 	 * @param current The current snapshot
 	 * @param object The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all modified properties, or null if no properties
 	 * were modified.
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session);
 
 	/**
 	 * Determine whether the entity has a particular property holding
 	 * the identifier value.
 	 *
 	 * @return True if the entity has a specific property holding identifier value.
 	 */
 	public boolean hasIdentifierProperty();
 
 	/**
 	 * Determine whether detached instances of this entity carry their own
 	 * identifier value.
 	 * <p/>
 	 * The other option is the deprecated feature where users could supply
 	 * the id during session calls.
 	 *
 	 * @return True if either (1) {@link #hasIdentifierProperty()} or
 	 * (2) the identifier is an embedded composite identifier; false otherwise.
 	 */
 	public boolean canExtractIdOutOfEntity();
 
 	/**
 	 * Determine whether optimistic locking by column is enabled for this
 	 * entity.
 	 *
 	 * @return True if optimistic locking by column (i.e., <version/> or
 	 * <timestamp/>) is enabled; false otherwise.
 	 */
 	public boolean isVersioned();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the type of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or null, if not versioned.
 	 */
 	public VersionType getVersionType();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the index of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or -66, if not versioned.
 	 */
 	public int getVersionProperty();
 
 	/**
 	 * Determine whether this entity defines a natural identifier.
 	 *
 	 * @return True if the entity defines a natural id; false otherwise.
 	 */
 	public boolean hasNaturalIdentifier();
 
 	/**
 	 * If the entity defines a natural id ({@link #hasNaturalIdentifier()}), which
 	 * properties make up the natural id.
 	 *
 	 * @return The indices of the properties making of the natural id; or
 	 * null, if no natural id is defined.
 	 */
 	public int[] getNaturalIdentifierProperties();
 
 	/**
 	 * Retrieve the current state of the natural-id properties from the database.
 	 *
 	 * @param id The identifier of the entity for which to retrieve the natural-id values.
 	 * @param session The session from which the request originated.
 	 * @return The natural-id snapshot.
 	 */
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session);
 
 	/**
 	 * Determine which identifier generation strategy is used for this entity.
 	 *
 	 * @return The identifier generation strategy.
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 
 	/**
 	 * Determine whether this entity defines any lazy properties (ala
 	 * bytecode instrumentation).
 	 *
 	 * @return True if the entity has properties mapped as lazy; false otherwise.
 	 */
 	public boolean hasLazyProperties();
 
 	/**
 	 * Load the id for the entity based on the natural id.
 	 */
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session);
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance
 	 */
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance, using a natively generated identifier (optional operation)
 	 */
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Delete a persistent instance
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Update a persistent instance
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException;
 
 	/**
 	 * Get the Hibernate types of the class properties
 	 */
 	public Type[] getPropertyTypes();
 
 	/**
 	 * Get the names of the class properties - doesn't have to be the names of the
 	 * actual Java properties (used for XML generation only)
 	 */
 	public String[] getPropertyNames();
 
 	/**
 	 * Get the "insertability" of the properties of this class
 	 * (does the property appear in an SQL INSERT)
 	 */
 	public boolean[] getPropertyInsertability();
 
 	/**
 	 * Which of the properties of this class are database generated values on insert?
 	 *
 	 * @deprecated Replaced internally with InMemoryValueGenerationStrategy / InDatabaseValueGenerationStrategy
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions();
 
 	/**
 	 * Which of the properties of this class are database generated values on update?
 	 *
 	 * @deprecated Replaced internally with InMemoryValueGenerationStrategy / InDatabaseValueGenerationStrategy
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions();
 
 	/**
 	 * Get the "updateability" of the properties of this class
 	 * (does the property appear in an SQL UPDATE)
 	 */
 	public boolean[] getPropertyUpdateability();
 
 	/**
 	 * Get the "checkability" of the properties of this class
 	 * (is the property dirty checked, does the cache need
 	 * to be updated)
 	 */
 	public boolean[] getPropertyCheckability();
 
 	/**
 	 * Get the nullability of the properties of this class
 	 */
 	public boolean[] getPropertyNullability();
 
 	/**
 	 * Get the "versionability" of the properties of this class
 	 * (is the property optimistic-locked)
 	 */
 	public boolean[] getPropertyVersionability();
 	public boolean[] getPropertyLaziness();
 	/**
 	 * Get the cascade styles of the properties (optional operation)
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles();
 
 	/**
 	 * Get the identifier type
 	 */
 	public Type getIdentifierType();
 
 	/**
 	 * Get the name of the identifier property (or return null) - need not return the
 	 * name of an actual Java property
 	 */
 	public String getIdentifierPropertyName();
 
 	/**
 	 * Should we always invalidate the cache instead of
 	 * recaching updated state
 	 */
 	public boolean isCacheInvalidationRequired();
 	/**
 	 * Should lazy properties of this entity be cached?
 	 */
 	public boolean isLazyPropertiesCacheable();
 	/**
 	 * Does this class have a cache.
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache (optional operation)
 	 */
 	public EntityRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 
 	/**
 	 * Does this class have a natural id cache
 	 */
 	public boolean hasNaturalIdCache();
 	
 	/**
 	 * Get the NaturalId cache (optional operation)
 	 */
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy();
 
 	/**
 	 * Get the user-visible metadata for the class (optional operation)
 	 */
 	public ClassMetadata getClassMetadata();
 
 	/**
 	 * Is batch loading enabled?
 	 */
 	public boolean isBatchLoadable();
 
 	/**
 	 * Is select snapshot before update enabled?
 	 */
 	public boolean isSelectBeforeUpdateRequired();
 
 	/**
 	 * Get the current database state of the object, in a "hydrated" form, without
 	 * resolving identifiers
 	 * @return null if there is no row in the database
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session);
 
 	/**
 	 * Get the current version of the object, or return null if there is no row for
 	 * the given identifier. In the case of unversioned data, return any object
 	 * if the row exists.
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Has the class actually been bytecode instrumented?
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Does this entity define any properties as being database generated on insert?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasInsertGeneratedProperties();
 
 	/**
 	 * Does this entity define any properties as being database generated on update?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasUpdateGeneratedProperties();
 
 	/**
 	 * Does this entity contain a version property that is defined
 	 * to be database generated?
 	 *
 	 * @return true if this entity contains a version property and that
 	 * property has been marked as generated.
 	 */
 	public boolean isVersionPropertyGenerated();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is tuplizer-centric, but is passed a session ~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Called just after the entities properties have been initialized
 	 */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Called just after the entity has been reassociated with the session
 	 */
 	public void afterReassociate(Object entity, SessionImplementor session);
 
 	/**
 	 * Create a new proxy instance
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Is this a new transient instance?
 	 */
 	public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Return the values of the insertable properties of the object (including backrefs)
 	 */
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is Tuplizer-centric ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The persistent class, or null
 	 */
 	public Class getMappedClass();
 
 	/**
 	 * Does the class implement the {@link org.hibernate.classic.Lifecycle} interface.
 	 */
 	public boolean implementsLifecycle();
 
 	/**
 	 * Get the proxy interface that instances of <em>this</em> concrete class will be
 	 * cast to (optional operation).
 	 */
 	public Class getConcreteProxyClass();
 
 	/**
 	 * Set the given values to the mapped properties of the given object
 	 */
 	public void setPropertyValues(Object object, Object[] values);
 
 	/**
 	 * Set the value of a particular property
 	 */
 	public void setPropertyValue(Object object, int i, Object value);
 
 	/**
 	 * Return the (loaded) values of the mapped properties of the object (not including backrefs)
 	 */
 	public Object[] getPropertyValues(Object object);
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, int i) throws HibernateException;
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, String propertyName);
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead
 	 */
 	@Deprecated
 	@SuppressWarnings( {"JavaDoc"})
 	public Serializable getIdentifier(Object object) throws HibernateException;
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @param entity The entity for which to get the identifier
 	 * @param session The session from which the request originated
 	 *
 	 * @return The identifier
 	 */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Get the version number (or timestamp) from the object's version property (or return null if not versioned)
 	 */
 	public Object getVersion(Object object) throws HibernateException;
 
 	/**
 	 * Create a class instance initialized with the given identifier
 	 *
 	 * @param id The identifier value to use (may be null to represent no value)
 	 * @param session The session from which the request originated.
 	 *
 	 * @return The instantiated entity.
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
 	/**
 	 * Is the given object an instance of this entity?
 	 */
 	public boolean isInstance(Object object);
 
 	/**
 	 * Does the given instance have any uninitialized lazy properties?
 	 */
 	public boolean hasUninitializedLazyProperties(Object object);
 
 	/**
 	 * Set the identifier and version of the given instance back to its "unsaved" value.
 	 *
 	 * @param entity The entity instance
 	 * @param currentId The currently assigned identifier value.
 	 * @param currentVersion The currently assigned version value.
 	 * @param session The session from which the request originated.
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
 	/**
 	 * A request has already identified the entity-name of this persister as the mapping for the given instance.
 	 * However, we still need to account for possible subclassing and potentially re-route to the more appropriate
 	 * persister.
 	 * <p/>
 	 * For example, a request names <tt>Animal</tt> as the entity-name which gets resolved to this persister.  But the
 	 * actual instance is really an instance of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  So, here the
 	 * <tt>Animal</tt> persister is being asked to return the persister specific to <tt>Cat</tt>.
 	 * <p/>
 	 * It is also possible that the instance is actually an <tt>Animal</tt> instance in the above example in which
 	 * case we would return <tt>this</tt> from this method.
 	 *
 	 * @param instance The entity instance
 	 * @param factory Reference to the SessionFactory
 	 *
 	 * @return The appropriate persister
 	 *
 	 * @throws HibernateException Indicates that instance was deemed to not be a subclass of the entity mapped by
 	 * this persister.
 	 */
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
 
 	public EntityMode getEntityMode();
 	public EntityTuplizer getEntityTuplizer();
 
-	public EntityInstrumentationMetadata getInstrumentationMetadata();
+	public BytecodeEnhancementMetadata getInstrumentationMetadata();
 	
 	public FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 
 	/**
 	 * Converts an array of attribute names to a set of indexes, according to the entity metamodel
 	 *
 	 * @param attributeNames Array of names to be resolved
 	 *
 	 * @return A set of unique indexes of the attribute names found in the metamodel
 	 */
 	public int[] resolveAttributeIndexes(String[] attributeNames);
 
 	public boolean canUseReferenceCacheEntries();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/property/access/spi/EnhancedGetterMethodImpl.java b/hibernate-core/src/main/java/org/hibernate/property/access/spi/EnhancedGetterMethodImpl.java
index a88e25f56d..dc9e21b268 100644
--- a/hibernate-core/src/main/java/org/hibernate/property/access/spi/EnhancedGetterMethodImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/access/spi/EnhancedGetterMethodImpl.java
@@ -1,152 +1,152 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.property.access.spi;
 
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.PropertyAccessException;
-import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 /**
  * @author Steve Ebersole
  */
 public class EnhancedGetterMethodImpl implements Getter {
 	private static final CoreMessageLogger LOG = messageLogger( EnhancedGetterMethodImpl.class );
 
 	private final Class containerClass;
 	private final String propertyName;
 	private final Method getterMethod;
 
 	public EnhancedGetterMethodImpl(Class containerClass, String propertyName, Method getterMethod) {
 		this.containerClass = containerClass;
 		this.propertyName = propertyName;
 		this.getterMethod = getterMethod;
 	}
 
 	private boolean isAttributeLoaded(Object owner) {
 		if ( owner instanceof PersistentAttributeInterceptable ) {
 			PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) owner ).$$_hibernate_getInterceptor();
-			if ( interceptor != null && interceptor instanceof LazyAttributeLoader ) {
-				return ( (LazyAttributeLoader) interceptor ).isAttributeLoaded( propertyName );
+			if ( interceptor != null && interceptor instanceof LazyAttributeLoadingInterceptor ) {
+				return ( (LazyAttributeLoadingInterceptor) interceptor ).isAttributeLoaded( propertyName );
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object get(Object owner) {
 		try {
 
 			// We don't want to trigger lazy loading of byte code enhanced attributes
 			if ( isAttributeLoaded( owner ) ) {
 				return getterMethod.invoke( owner );
 			}
 			return null;
 
 		}
 		catch (InvocationTargetException ite) {
 			throw new PropertyAccessException(
 					ite,
 					"Exception occurred inside",
 					false,
 					containerClass,
 					propertyName
 			);
 		}
 		catch (IllegalAccessException iae) {
 			throw new PropertyAccessException(
 					iae,
 					"IllegalAccessException occurred while calling",
 					false,
 					containerClass,
 					propertyName
 			);
 			//cannot occur
 		}
 		catch (IllegalArgumentException iae) {
 			LOG.illegalPropertyGetterArgument( containerClass.getName(), propertyName );
 			throw new PropertyAccessException(
 					iae,
 					"IllegalArgumentException occurred calling",
 					false,
 					containerClass,
 					propertyName
 			);
 		}
 	}
 
 	@Override
 	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
 		return get( owner );
 	}
 
 	@Override
 	public Class getReturnType() {
 		return getterMethod.getReturnType();
 	}
 
 	@Override
 	public Member getMember() {
 		return getterMethod;
 	}
 
 	@Override
 	public String getMethodName() {
 		return getterMethod.getName();
 	}
 
 	@Override
 	public Method getMethod() {
 		return getterMethod;
 	}
 
 	private Object writeReplace() throws ObjectStreamException {
 		return new SerialForm( containerClass, propertyName, getterMethod );
 	}
 
 	private static class SerialForm implements Serializable {
 		private final Class containerClass;
 		private final String propertyName;
 
 		private final Class declaringClass;
 		private final String methodName;
 
 		private SerialForm(Class containerClass, String propertyName, Method method) {
 			this.containerClass = containerClass;
 			this.propertyName = propertyName;
 			this.declaringClass = method.getDeclaringClass();
 			this.methodName = method.getName();
 		}
 
 		private Object readResolve() {
 			return new EnhancedGetterMethodImpl( containerClass, propertyName, resolveMethod() );
 		}
 
 		@SuppressWarnings("unchecked")
 		private Method resolveMethod() {
 			try {
 				return declaringClass.getDeclaredMethod( methodName );
 			}
 			catch (NoSuchMethodException e) {
 				throw new PropertyAccessSerializationException(
 						"Unable to resolve getter method on deserialization : " + declaringClass.getName() + "#" + methodName
 				);
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
deleted file mode 100644
index 00269900b1..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
+++ /dev/null
@@ -1,120 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.tool.instrument;
-
-import java.io.File;
-import java.util.ArrayList;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Set;
-
-import org.hibernate.bytecode.buildtime.spi.Instrumenter;
-import org.hibernate.bytecode.buildtime.spi.Logger;
-
-import org.apache.tools.ant.BuildException;
-import org.apache.tools.ant.DirectoryScanner;
-import org.apache.tools.ant.Project;
-import org.apache.tools.ant.Task;
-import org.apache.tools.ant.types.FileSet;
-
-/**
- * Super class for all Hibernate instrumentation tasks.  Provides the basic templating of how instrumentation
- * should occur; subclasses simply plug in to that process appropriately for the given bytecode provider.
- *
- * @author Steve Ebersole
- */
-public abstract class BasicInstrumentationTask extends Task implements Instrumenter.Options {
-
-	private final LoggerBridge logger = new LoggerBridge();
-
-	private List filesets = new ArrayList();
-	private boolean extended;
-
-	// deprecated option...
-	private boolean verbose;
-
-	public void addFileset(FileSet set) {
-		this.filesets.add( set );
-	}
-
-	protected final Iterator filesets() {
-		return filesets.iterator();
-	}
-
-	public boolean isExtended() {
-		return extended;
-	}
-
-	public void setExtended(boolean extended) {
-		this.extended = extended;
-	}
-
-	public boolean isVerbose() {
-		return verbose;
-	}
-
-	public void setVerbose(boolean verbose) {
-		this.verbose = verbose;
-	}
-
-	public final boolean performExtendedInstrumentation() {
-		return isExtended();
-	}
-
-	protected abstract Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options);
-
-	@Override
-	public void execute() throws BuildException {
-		try {
-			buildInstrumenter( logger, this )
-					.execute( collectSpecifiedFiles() );
-		}
-		catch ( Throwable t ) {
-			throw new BuildException( t );
-		}
-	}
-
-	private Set collectSpecifiedFiles() {
-		HashSet files = new HashSet();
-		Project project = getProject();
-		Iterator filesets = filesets();
-		while ( filesets.hasNext() ) {
-			FileSet fs = ( FileSet ) filesets.next();
-			DirectoryScanner ds = fs.getDirectoryScanner( project );
-			String[] includedFiles = ds.getIncludedFiles();
-			File d = fs.getDir( project );
-			for ( int i = 0; i < includedFiles.length; ++i ) {
-				files.add( new File( d, includedFiles[i] ) );
-			}
-		}
-		return files;
-	}
-
-	protected class LoggerBridge implements Logger {
-		public void trace(String message) {
-			log( message, Project.MSG_VERBOSE );
-		}
-
-		public void debug(String message) {
-			log( message, Project.MSG_DEBUG );
-		}
-
-		public void info(String message) {
-			log( message, Project.MSG_INFO );
-		}
-
-		public void warn(String message) {
-			log( message, Project.MSG_WARN );
-		}
-
-		public void error(String message) {
-			log( message, Project.MSG_ERR );
-		}
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
deleted file mode 100644
index 2f082bfbb8..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
+++ /dev/null
@@ -1,54 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.tool.instrument.javassist;
-
-import org.hibernate.bytecode.buildtime.internal.JavassistInstrumenter;
-import org.hibernate.bytecode.buildtime.spi.Instrumenter;
-import org.hibernate.bytecode.buildtime.spi.Logger;
-import org.hibernate.tool.instrument.BasicInstrumentationTask;
-
-/**
- * An Ant task for instrumenting persistent classes in order to enable
- * field-level interception using Javassist.
- * <p/>
- * In order to use this task, typically you would define a a taskdef
- * similiar to:<pre>
- * <taskdef name="instrument" classname="org.hibernate.tool.instrument.javassist.InstrumentTask">
- *     <classpath refid="lib.class.path"/>
- * </taskdef>
- * </pre>
- * where <tt>lib.class.path</tt> is an ANT path reference containing all the
- * required Hibernate and Javassist libraries.
- * <p/>
- * And then use it like:<pre>
- * <instrument verbose="true">
- *     <fileset dir="${testclasses.dir}/org/hibernate/test">
- *         <include name="yadda/yadda/**"/>
- *         ...
- *     </fileset>
- * </instrument>
- * </pre>
- * where the nested ANT fileset includes the class you would like to have
- * instrumented.
- * <p/>
- * Optionally you can chose to enable "Extended Instrumentation" if desired
- * by specifying the extended attriubute on the task:<pre>
- * <instrument verbose="true" extended="true">
- *     ...
- * </instrument>
- * </pre>
- * See the Hibernate manual regarding this option.
- *
- * @author Muga Nishizawa
- * @author Steve Ebersole
- */
-public class InstrumentTask extends BasicInstrumentationTask {
-	@Override
-	protected Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options) {
-		return new JavassistInstrumenter( logger, options );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/instrument/package.html b/hibernate-core/src/main/java/org/hibernate/tool/instrument/package.html
deleted file mode 100755
index d729643310..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/tool/instrument/package.html
+++ /dev/null
@@ -1,18 +0,0 @@
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
-  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
-  -->
-
-<html>
-<head></head>
-<body>
-<p>
-	The <tt>instrument</tt> tool for adding field-interception hooks
-	to persistent classes using built-time bytecode processing. Use
-	this tool only if you wish to take advantage of lazy property
-	fetching (the <tt>&lt;lazy&gt;</tt> mapping element).
-</p>
-</body>
-</html>
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index 9682ee2901..82bfaf83cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,712 +1,712 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.id.Assigned;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 	private static final CoreMessageLogger LOG = messageLogger( AbstractEntityTuplizer.class );
 
 	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
 
 	private final EntityMetamodel entityMetamodel;
 
 	private final Getter idGetter;
 	private final Setter idSetter;
 
 	protected final Getter[] getters;
 	protected final Setter[] setters;
 	protected final int propertySpan;
 	protected final boolean hasCustomAccessors;
 	private final Instantiator instantiator;
 	private final ProxyFactory proxyFactory;
 	private final CompositeType identifierMapperType;
 
 	public Type getIdentifierMapperType() {
 		return identifierMapperType;
 	}
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 *
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 *
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 *
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 *
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
 		getters = new Getter[propertySpan];
 		setters = new Setter[propertySpan];
 
 		Iterator itr = mappingInfo.getPropertyClosureIterator();
 		boolean foundCustomAccessor = false;
 		int i = 0;
 		while ( itr.hasNext() ) {
 			//TODO: redesign how PropertyAccessors are acquired...
 			Property property = (Property) itr.next();
 			getters[i] = buildPropertyGetter( property, mappingInfo );
 			setters[i] = buildPropertySetter( property, mappingInfo );
 			if ( !property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
 		instantiator = buildInstantiator( mappingInfo );
 
-		if ( entityMetamodel.isLazy() && !entityMetamodel.isLazyLoadingBytecodeEnhanced() ) {
+		if ( entityMetamodel.isLazy() && !entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if ( proxyFactory == null ) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 		Component mapper = mappingInfo.getIdentifierMapper();
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = (CompositeType) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
 					(ComponentType) identifierMapperType
 			);
 		}
 	}
 
 	/**
 	 * Retreives the defined entity-name for the tuplized entity.
 	 *
 	 * @return The entity-name.
 	 */
 	protected String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	/**
 	 * Retrieves the defined entity-names for any subclasses defined for this
 	 * entity.
 	 *
 	 * @return Any subclass entity-names.
 	 */
 	protected Set getSubclassEntityNames() {
 		return entityMetamodel.getSubclassEntityNames();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity) throws HibernateException {
 		return getIdentifier( entity, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		final Object id;
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			id = entity;
 		}
 		else if ( HibernateProxy.class.isInstance( entity ) ) {
 			id = ( (HibernateProxy) entity ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			if ( idGetter == null ) {
 				if ( identifierMapperType == null ) {
 					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
 				}
 				else {
 					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
 				}
 			}
 			else {
 				id = idGetter.get( entity );
 			}
 		}
 
 		try {
 			return (Serializable) id;
 		}
 		catch (ClassCastException cce) {
 			StringBuilder msg = new StringBuilder( "Identifier classes must be serializable. " );
 			if ( id != null ) {
 				msg.append( id.getClass().getName() ).append( " is not serializable. " );
 			}
 			if ( cce.getMessage() != null ) {
 				msg.append( cce.getMessage() );
 			}
 			throw new ClassCastException( msg.toString() );
 		}
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		setIdentifier( entity, id, null );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			if ( entity != id ) {
 				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
 				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
 			}
 		}
 		else if ( idSetter != null ) {
 			idSetter.set( entity, id, getFactory() );
 		}
 		else if ( identifierMapperType != null ) {
 			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
 		}
 	}
 
 	private static interface MappedIdentifierValueMarshaller {
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
 	}
 
 	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
 
 	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
 			ComponentType mappedIdClassComponentType,
 			ComponentType virtualIdComponent) {
 		// so basically at this point we know we have a "mapped" composite identifier
 		// which is an awful way to say that the identifier is represented differently
 		// in the entity and in the identifier value.  The incoming value should
 		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
 		// should be an instance of the entity class as defined by metamodel.
 		//
 		// However, even within that we have 2 potential scenarios:
 		//		1) @IdClass types and entity @Id property types match
 		//			- return a NormalMappedIdentifierValueMarshaller
 		//		2) They do not match
 		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 		boolean wereAllEquivalent = true;
 		// the sizes being off is a much bigger problem that should have been caught already...
 		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
 					&& !mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
 				wereAllEquivalent = false;
 				break;
 			}
 		}
 
 		return wereAllEquivalent
 				? new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
 				: new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(
 				virtualIdComponent,
 				mappedIdClassComponentType
 		);
 	}
 
 	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private NormalMappedIdentifierValueMarshaller(
 				ComponentType virtualIdComponent,
 				ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			virtualIdComponent.setPropertyValues(
 					entity,
 					mappedIdentifierType.getPropertyValues( id, session ),
 					entityMode
 			);
 		}
 	}
 
 	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 			implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(
 				ComponentType virtualIdComponent,
 				ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			final Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			final Type[] subTypes = virtualIdComponent.getSubtypes();
 			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
 			final Iterable<PersistEventListener> persistEventListeners = persistEventListeners( session );
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			final int length = subTypes.length;
 			for ( int i = 0; i < length; i++ ) {
 				if ( propertyValues[i] == null ) {
 					throw new HibernateException( "No part of a composite identifier may be null" );
 				}
 				//JPA 2 @MapsId + @IdClass points to the pk of the entity
 				if ( subTypes[i].isAssociationType() && !copierSubTypes[i].isAssociationType() ) {
 					// we need a session to handle this use case
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final Object subId;
 					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
 						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
 					}
 					else {
 						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 						if ( pcEntry != null ) {
 							subId = pcEntry.getId();
 						}
 						else {
 							LOG.debug( "Performing implicit derived identity cascade" );
 							final PersistEvent event = new PersistEvent(
 									null,
 									propertyValues[i],
 									(EventSource) session
 							);
 							for ( PersistEventListener listener : persistEventListeners ) {
 								listener.onPersist( event );
 							}
 							pcEntry = persistenceContext.getEntry( propertyValues[i] );
 							if ( pcEntry == null || pcEntry.getId() == null ) {
 								throw new HibernateException( "Unable to process implicit derived identity cascade" );
 							}
 							else {
 								subId = pcEntry.getId();
 							}
 						}
 					}
 					propertyValues[i] = subId;
 				}
 			}
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
 			final Object[] injectionValues = new Object[extractedValues.length];
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
 				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
 				if ( virtualPropertyType.isEntityType() && !idClassPropertyType.isEntityType() ) {
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
 					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
 							session.getFactory().getEntityPersister( associatedEntityName )
 					);
 					// it is conceivable there is a proxy, so check that first
 					Object association = persistenceContext.getProxy( entityKey );
 					if ( association == null ) {
 						// otherwise look for an initialized version
 						association = persistenceContext.getEntity( entityKey );
 					}
 					injectionValues[i] = association;
 				}
 				else {
 					injectionValues[i] = extractedValues[i];
 				}
 			}
 			virtualIdComponent.setPropertyValues( entity, injectionValues, entityMode );
 		}
 	}
 
 	private static Iterable<PersistEventListener> persistEventListeners(SessionImplementor session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PERSIST )
 				.listeners();
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	@Override
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
 		//noinspection StatementWithEmptyBody
 		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
 		}
 		else {
 			//reset the id
 			Serializable result = entityMetamodel.getIdentifierProperty()
 					.getUnsavedValue()
 					.getDefaultValue( currentId );
 			setIdentifier( entity, result, session );
 			//reset the version
 			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
 			if ( entityMetamodel.isVersioned() ) {
 				setPropertyValue(
 						entity,
 						entityMetamodel.getVersionPropertyIndex(),
 						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
 				);
 			}
 		}
 	}
 
 	@Override
 	public Object getVersion(Object entity) throws HibernateException {
 		if ( !entityMetamodel.isVersioned() ) {
 			return null;
 		}
 		return getters[entityMetamodel.getVersionPropertyIndex()].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		return !hasUninitializedLazyProperties( entity );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		boolean getAll = shouldGetAllProperties( entity );
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			NonIdentifierAttribute property = entityMetamodel.getProperties()[j];
 			if ( getAll || !property.isLazy() ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			result[j] = getters[j].getForInsert( entity, mergeMap, session );
 		}
 		return result;
 	}
 
 	@Override
 	public Object getPropertyValue(Object entity, int i) throws HibernateException {
 		return getters[i].get( entity );
 	}
 
 	@Override
 	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
 		int loc = propertyPath.indexOf( '.' );
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if ( index == null ) {
 			propertyPath = PropertyPath.IDENTIFIER_MAPPER_PROPERTY + "." + propertyPath;
 			loc = propertyPath.indexOf( '.' );
 			basePropertyName = loc > 0
 					? propertyPath.substring( 0, loc )
 					: propertyPath;
 		}
 		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		final Object baseValue = getPropertyValue( entity, index );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) entityMetamodel.getPropertyTypes()[index],
 					baseValue,
 					propertyPath.substring( loc + 1 )
 			);
 		}
 		else {
 			return baseValue;
 		}
 	}
 
 	/**
 	 * Extract a component property value.
 	 *
 	 * @param type The component property types.
 	 * @param component The component instance itself.
 	 * @param propertyPath The property path for the property to be extracted.
 	 *
 	 * @return The property value extracted.
 	 */
 	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
 		final int loc = propertyPath.indexOf( '.' );
 		final String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		final int index = findSubPropertyIndex( type, basePropertyName );
 		final Object baseValue = type.getPropertyValue( component, index );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) type.getSubtypes()[index],
 					baseValue,
 					propertyPath.substring( loc + 1 )
 			);
 		}
 		else {
 			return baseValue;
 		}
 
 	}
 
 	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
 		final String[] propertyNames = type.getPropertyNames();
 		for ( int index = 0; index < propertyNames.length; index++ ) {
 			if ( subPropertyName.equals( propertyNames[index] ) ) {
 				return index;
 			}
 		}
 		throw new MappingException( "component property not found: " + subPropertyName );
 	}
 
 	@Override
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		boolean setAll = !entityMetamodel.hasLazyProperties();
 
 		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
 			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				setters[j].set( entity, values[j], getFactory() );
 			}
 		}
 	}
 
 	@Override
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
 		setters[i].set( entity, value, getFactory() );
 	}
 
 	@Override
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
 		setters[entityMetamodel.getPropertyIndex( propertyName )].set( entity, value, getFactory() );
 	}
 
 	@Override
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
 	@Override
 	public final Object instantiate(Serializable id, SessionImplementor session) {
 		Object result = getInstantiator().instantiate( id );
 		if ( id != null ) {
 			setIdentifier( result, id, session );
 		}
 		return result;
 	}
 
 	@Override
 	public final Object instantiate() throws HibernateException {
 		return instantiate( null, null );
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		// the default is to simply not lazy fetch properties for now...
 		return false;
 	}
 
 	@Override
 	public final boolean isInstance(Object object) {
 		return getInstantiator().isInstance( object );
 	}
 
 	@Override
 	public boolean hasProxy() {
-		return entityMetamodel.isLazy() && !entityMetamodel.isLazyLoadingBytecodeEnhanced();
+		return entityMetamodel.isLazy() && !entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
 	}
 
 	@Override
 	public final Object createProxy(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		return getProxyFactory().getProxy( id, session );
 	}
 
 	@Override
 	public boolean isLifecycleImplementor() {
 		return false;
 	}
 
 	protected final EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	protected final SessionFactoryImplementor getFactory() {
 		return entityMetamodel.getSessionFactory();
 	}
 
 	protected final Instantiator getInstantiator() {
 		return instantiator;
 	}
 
 	protected final ProxyFactory getProxyFactory() {
 		return proxyFactory;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
 	}
 
 	@Override
 	public Getter getIdentifierGetter() {
 		return idGetter;
 	}
 
 	@Override
 	public Getter getVersionGetter() {
 		if ( getEntityMetamodel().isVersioned() ) {
 			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
 		}
 		return null;
 	}
 
 	@Override
 	public Getter getGetter(int i) {
 		return getters[i];
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonEnhancedPojoImpl.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonEnhancedPojoImpl.java
new file mode 100644
index 0000000000..ba0c39d1ad
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonEnhancedPojoImpl.java
@@ -0,0 +1,50 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.tuple.entity;
+
+import java.util.Set;
+
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
+import org.hibernate.bytecode.spi.NotInstrumentedException;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BytecodeEnhancementMetadataNonEnhancedPojoImpl implements BytecodeEnhancementMetadata {
+	private final Class entityClass;
+	private final String errorMsg;
+
+	public BytecodeEnhancementMetadataNonEnhancedPojoImpl(Class entityClass) {
+		this.entityClass = entityClass;
+		this.errorMsg = "Entity class [" + entityClass.getName() + "] is not enhanced";
+	}
+
+	@Override
+	public String getEntityName() {
+		return entityClass.getName();
+	}
+
+	@Override
+	public boolean isEnhancedForLazyLoading() {
+		return false;
+	}
+
+	@Override
+	public LazyAttributeLoadingInterceptor injectInterceptor(
+			Object entity,
+			Set<String> uninitializedFieldNames,
+			SessionImplementor session) throws NotInstrumentedException {
+		throw new NotInstrumentedException( errorMsg );
+	}
+
+	@Override
+	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+		throw new NotInstrumentedException( errorMsg );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/NonPojoInstrumentationMetadata.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java
similarity index 57%
rename from hibernate-core/src/main/java/org/hibernate/tuple/entity/NonPojoInstrumentationMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java
index 1dd7ea7f9e..9e853e67db 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/NonPojoInstrumentationMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataNonPojoImpl.java
@@ -1,49 +1,50 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import java.util.Set;
 
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.bytecode.spi.NotInstrumentedException;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * @author Steve Ebersole
  */
-public class NonPojoInstrumentationMetadata implements EntityInstrumentationMetadata {
+public class BytecodeEnhancementMetadataNonPojoImpl implements BytecodeEnhancementMetadata {
 	private final String entityName;
 	private final String errorMsg;
 
-	public NonPojoInstrumentationMetadata(String entityName) {
+	public BytecodeEnhancementMetadataNonPojoImpl(String entityName) {
 		this.entityName = entityName;
 		this.errorMsg = "Entity [" + entityName + "] is non-pojo, and therefore not instrumented";
 	}
 
 	@Override
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@Override
-	public boolean isInstrumented() {
+	public boolean isEnhancedForLazyLoading() {
 		return false;
 	}
 
 	@Override
-	public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+	public LazyAttributeLoadingInterceptor injectInterceptor(
+			Object entity,
+			Set<String> uninitializedFieldNames,
+			SessionImplementor session) throws NotInstrumentedException {
 		throw new NotInstrumentedException( errorMsg );
 	}
 
 	@Override
-	public FieldInterceptor injectInterceptor(
-			Object entity, String entityName, Set uninitializedFieldNames, SessionImplementor session)
-			throws NotInstrumentedException {
+	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
 		throw new NotInstrumentedException( errorMsg );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java
new file mode 100644
index 0000000000..6c91c9c73f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/BytecodeEnhancementMetadataPojoImpl.java
@@ -0,0 +1,87 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.tuple.entity;
+
+import java.util.Set;
+
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
+import org.hibernate.bytecode.spi.NotInstrumentedException;
+import org.hibernate.engine.spi.PersistentAttributeInterceptable;
+import org.hibernate.engine.spi.PersistentAttributeInterceptor;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BytecodeEnhancementMetadataPojoImpl implements BytecodeEnhancementMetadata {
+	private final Class entityClass;
+	private final boolean enhancedForLazyLoading;
+
+	public BytecodeEnhancementMetadataPojoImpl(Class entityClass) {
+		this.entityClass = entityClass;
+		this.enhancedForLazyLoading = PersistentAttributeInterceptable.class.isAssignableFrom( entityClass );
+	}
+
+	@Override
+	public String getEntityName() {
+		return entityClass.getName();
+	}
+
+	@Override
+	public boolean isEnhancedForLazyLoading() {
+		return enhancedForLazyLoading;
+	}
+
+	@Override
+	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+		if ( !enhancedForLazyLoading ) {
+			throw new NotInstrumentedException( "Entity class [" + entityClass.getName() + "] is not enhanced for lazy loading" );
+		}
+
+		if ( !entityClass.isInstance( entity ) ) {
+			throw new IllegalArgumentException(
+					String.format(
+							"Passed entity instance [%s] is not of expected type [%s]",
+							entity,
+							getEntityName()
+					)
+			);
+		}
+
+		final PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) entity ).$$_hibernate_getInterceptor();
+		if ( interceptor == null ) {
+			return null;
+		}
+
+		return (LazyAttributeLoadingInterceptor) interceptor;
+	}
+
+	@Override
+	public LazyAttributeLoadingInterceptor injectInterceptor(
+			Object entity,
+			Set<String> uninitializedFieldNames,
+			SessionImplementor session) throws NotInstrumentedException {
+		if ( !enhancedForLazyLoading ) {
+			throw new NotInstrumentedException( "Entity class [" + entityClass.getName() + "] is not enhanced for lazy loading" );
+		}
+
+		if ( !entityClass.isInstance( entity ) ) {
+			throw new IllegalArgumentException(
+					String.format(
+							"Passed entity instance [%s] is not of expected type [%s]",
+							entity,
+							getEntityName()
+					)
+			);
+		}
+
+		final LazyAttributeLoadingInterceptor interceptor = new LazyAttributeLoadingInterceptor( session, uninitializedFieldNames, getEntityName() );
+		( (PersistentAttributeInterceptable) entity ).$$_hibernate_setInterceptor( interceptor );
+		return interceptor;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 88b07dff82..93b6fc2537 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,1109 +1,1102 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
-import org.hibernate.cfg.Environment;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
-import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.persister.entity.AbstractEntityPersister;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.IdentifierProperty;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.ValueGenerator;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 	private static final CoreMessageLogger LOG = messageLogger( EntityMetamodel.class );
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
 	private final AbstractEntityPersister persister;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
 	private final IdentifierProperty identifierAttribute;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
 	private final NonIdentifierAttribute[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	// value generations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final boolean hasPreInsertGeneratedValues;
 	private final boolean hasPreUpdateGeneratedValues;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 
 	private final InMemoryValueGenerationStrategy[] inMemoryValueGenerationStrategies;
 	private final InDatabaseValueGenerationStrategy[] inDatabaseValueGenerationStrategies;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 	private final boolean hasCacheableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final OptimisticLockStyle optimisticLockStyle;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
-	private final EntityInstrumentationMetadata instrumentationMetadata;
-	private final boolean lazyLoadingBytecodeEnhanced;
+	private final BytecodeEnhancementMetadata bytecodeEnhancementMetadata;
 
 	public EntityMetamodel(
 			PersistentClass persistentClass,
 			AbstractEntityPersister persister,
 			SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 		this.persister = persister;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierAttribute = PropertyFactory.buildIdentifierAttribute(
 				persistentClass,
 				sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = persistentClass.isVersioned();
 
-		instrumentationMetadata = persistentClass.hasPojoRepresentation()
-				? Environment.getBytecodeProvider().getEntityInstrumentationMetadata( persistentClass.getMappedClass() )
-				: new NonPojoInstrumentationMetadata( persistentClass.getEntityName() );
-
-		lazyLoadingBytecodeEnhanced = ( persistentClass.getMappedClass() != null
-				&& PersistentAttributeInterceptable.class.isAssignableFrom( persistentClass.getMappedClass() ) );
+		if ( persistentClass.hasPojoRepresentation() ) {
+			bytecodeEnhancementMetadata = new BytecodeEnhancementMetadataPojoImpl( persistentClass.getMappedClass() );
+		}
+		else {
+			bytecodeEnhancementMetadata = new BytecodeEnhancementMetadataNonPojoImpl( persistentClass.getEntityName() );
+		}
 
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
 		properties = new NonIdentifierAttribute[propertySpan];
 		List<Integer> naturalIdNumbers = new ArrayList<Integer>();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		// generated value strategies ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.inMemoryValueGenerationStrategies = new InMemoryValueGenerationStrategy[propertySpan];
 		this.inDatabaseValueGenerationStrategies = new InDatabaseValueGenerationStrategy[propertySpan];
 
 		boolean foundPreInsertGeneratedValues = false;
 		boolean foundPreUpdateGeneratedValues = false;
 		boolean foundPostInsertGeneratedValues = false;
 		boolean foundPostUpdateGeneratedValues = false;
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty(
 						persister,
 						sessionFactory,
 						i,
 						prop,
-						instrumentationMetadata.isInstrumented()
+						bytecodeEnhancementMetadata.isEnhancedForLazyLoading()
 				);
 			}
 			else {
 				properties[i] = PropertyFactory.buildEntityBasedAttribute(
 						persister,
 						sessionFactory,
 						i,
 						prop,
-						instrumentationMetadata.isInstrumented()
+						bytecodeEnhancementMetadata.isEnhancedForLazyLoading()
 				);
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-			boolean lazy = prop.isLazy() && ( instrumentationMetadata.isInstrumented() || lazyLoadingBytecodeEnhanced );
+			boolean lazy = prop.isLazy() && bytecodeEnhancementMetadata.isEnhancedForLazyLoading();
 			if ( lazy ) {
 				hasLazy = true;
 			}
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			// generated value strategies ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			GenerationStrategyPair pair = buildGenerationStrategyPair( sessionFactory, prop );
 			inMemoryValueGenerationStrategies[i] = pair.getInMemoryStrategy();
 			inDatabaseValueGenerationStrategies[i] = pair.getInDatabaseStrategy();
 
 			if ( pair.getInMemoryStrategy() != null ) {
 				final GenerationTiming timing = pair.getInMemoryStrategy().getGenerationTiming();
 				if ( timing != GenerationTiming.NEVER ) {
 					final ValueGenerator generator = pair.getInMemoryStrategy().getValueGenerator();
 					if ( generator != null ) {
 						// we have some level of generation indicated
 						if ( timing == GenerationTiming.INSERT ) {
 							foundPreInsertGeneratedValues = true;
 						}
 						else if ( timing == GenerationTiming.ALWAYS ) {
 							foundPreInsertGeneratedValues = true;
 							foundPreUpdateGeneratedValues = true;
 						}
 					}
 				}
 			}
 			if (  pair.getInDatabaseStrategy() != null ) {
 				final GenerationTiming timing =  pair.getInDatabaseStrategy().getGenerationTiming();
 				if ( timing == GenerationTiming.INSERT ) {
 					foundPostInsertGeneratedValues = true;
 				}
 				else if ( timing == GenerationTiming.ALWAYS ) {
 					foundPostInsertGeneratedValues = true;
 					foundPostUpdateGeneratedValues = true;
 				}
 			}
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = persistentClass.getNaturalIdCacheRegionName() != null;
 		}
 
 		this.hasPreInsertGeneratedValues = foundPreInsertGeneratedValues;
 		this.hasPreUpdateGeneratedValues = foundPreUpdateGeneratedValues;
 		this.hasInsertGeneratedValues = foundPostInsertGeneratedValues;
 		this.hasUpdateGeneratedValues = foundPostUpdateGeneratedValues;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable(name);
 		}
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 					ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 					ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
 		optimisticLockStyle = persistentClass.getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
 	private static GenerationStrategyPair buildGenerationStrategyPair(
 			final SessionFactoryImplementor sessionFactory,
 			final Property mappingProperty) {
 		final ValueGeneration valueGeneration = mappingProperty.getValueGenerationStrategy();
 		if ( valueGeneration != null && valueGeneration.getGenerationTiming() != GenerationTiming.NEVER ) {
 			// the property is generated in full. build the generation strategy pair.
 			if ( valueGeneration.getValueGenerator() != null ) {
 				// in-memory generation
 				return new GenerationStrategyPair(
 						FullInMemoryValueGenerationStrategy.create( valueGeneration )
 				);
 			}
 			else {
 				// in-db generation
 				return new GenerationStrategyPair(
 						create(
 								sessionFactory,
 								mappingProperty,
 								valueGeneration
 						)
 				);
 			}
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			final CompositeGenerationStrategyPairBuilder builder = new CompositeGenerationStrategyPairBuilder( mappingProperty );
 			interpretPartialCompositeValueGeneration( sessionFactory, (Component) mappingProperty.getValue(), builder );
 			return builder.buildPair();
 		}
 
 		return NO_GEN_PAIR;
 	}
 
 	private static final GenerationStrategyPair NO_GEN_PAIR = new GenerationStrategyPair();
 
 	private static void interpretPartialCompositeValueGeneration(
 			SessionFactoryImplementor sessionFactory,
 			Component composite,
 			CompositeGenerationStrategyPairBuilder builder) {
 		Iterator subProperties = composite.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			final Property subProperty = (Property) subProperties.next();
 			builder.addPair( buildGenerationStrategyPair( sessionFactory, subProperty ) );
 		}
 	}
 
 	public static InDatabaseValueGenerationStrategyImpl create(
 			SessionFactoryImplementor sessionFactoryImplementor,
 			Property mappingProperty,
 			ValueGeneration valueGeneration) {
 		final int numberOfMappedColumns = mappingProperty.getType().getColumnSpan( sessionFactoryImplementor );
 		if ( numberOfMappedColumns == 1 ) {
 			return new InDatabaseValueGenerationStrategyImpl(
 					valueGeneration.getGenerationTiming(),
 					valueGeneration.referenceColumnInSql(),
 					new String[] { valueGeneration.getDatabaseGeneratedReferencedColumnValue() }
 
 			);
 		}
 		else {
 			if ( valueGeneration.getDatabaseGeneratedReferencedColumnValue() != null ) {
 				LOG.debugf(
 						"Value generator specified column value in reference to multi-column attribute [%s -> %s]; ignoring",
 						mappingProperty.getPersistentClass(),
 						mappingProperty.getName()
 				);
 			}
 			return new InDatabaseValueGenerationStrategyImpl(
 					valueGeneration.getGenerationTiming(),
 					valueGeneration.referenceColumnInSql(),
 					new String[numberOfMappedColumns]
 			);
 		}
 	}
 
 	public static class GenerationStrategyPair {
 		private final InMemoryValueGenerationStrategy inMemoryStrategy;
 		private final InDatabaseValueGenerationStrategy inDatabaseStrategy;
 
 		public GenerationStrategyPair() {
 			this( NoInMemoryValueGenerationStrategy.INSTANCE, NoInDatabaseValueGenerationStrategy.INSTANCE );
 		}
 
 		public GenerationStrategyPair(FullInMemoryValueGenerationStrategy inMemoryStrategy) {
 			this( inMemoryStrategy, NoInDatabaseValueGenerationStrategy.INSTANCE );
 		}
 
 		public GenerationStrategyPair(InDatabaseValueGenerationStrategyImpl inDatabaseStrategy) {
 			this( NoInMemoryValueGenerationStrategy.INSTANCE, inDatabaseStrategy );
 		}
 
 		public GenerationStrategyPair(
 				InMemoryValueGenerationStrategy inMemoryStrategy,
 				InDatabaseValueGenerationStrategy inDatabaseStrategy) {
 			// perform some normalization.  Also check that only one (if any) strategy is specified
 			if ( inMemoryStrategy == null ) {
 				inMemoryStrategy = NoInMemoryValueGenerationStrategy.INSTANCE;
 			}
 			if ( inDatabaseStrategy == null ) {
 				inDatabaseStrategy = NoInDatabaseValueGenerationStrategy.INSTANCE;
 			}
 
 			if ( inMemoryStrategy.getGenerationTiming() != GenerationTiming.NEVER
 					&& inDatabaseStrategy.getGenerationTiming() != GenerationTiming.NEVER ) {
 				throw new ValueGenerationStrategyException(
 						"in-memory and in-database value generation are mutually exclusive"
 				);
 			}
 
 			this.inMemoryStrategy = inMemoryStrategy;
 			this.inDatabaseStrategy = inDatabaseStrategy;
 		}
 
 		public InMemoryValueGenerationStrategy getInMemoryStrategy() {
 			return inMemoryStrategy;
 		}
 
 		public InDatabaseValueGenerationStrategy getInDatabaseStrategy() {
 			return inDatabaseStrategy;
 		}
 	}
 
 	public static class ValueGenerationStrategyException extends HibernateException {
 		public ValueGenerationStrategyException(String message) {
 			super( message );
 		}
 
 		public ValueGenerationStrategyException(String message, Throwable cause) {
 			super( message, cause );
 		}
 	}
 
 	private static class CompositeGenerationStrategyPairBuilder {
 		private final Property mappingProperty;
 
 		private boolean hadInMemoryGeneration;
 		private boolean hadInDatabaseGeneration;
 
 		private List<InMemoryValueGenerationStrategy> inMemoryStrategies;
 		private List<InDatabaseValueGenerationStrategy> inDatabaseStrategies;
 
 		public CompositeGenerationStrategyPairBuilder(Property mappingProperty) {
 			this.mappingProperty = mappingProperty;
 		}
 
 		public void addPair(GenerationStrategyPair generationStrategyPair) {
 			add( generationStrategyPair.getInMemoryStrategy() );
 			add( generationStrategyPair.getInDatabaseStrategy() );
 		}
 
 		private void add(InMemoryValueGenerationStrategy inMemoryStrategy) {
 			if ( inMemoryStrategies == null ) {
 				inMemoryStrategies = new ArrayList<InMemoryValueGenerationStrategy>();
 			}
 			inMemoryStrategies.add( inMemoryStrategy );
 
 			if ( inMemoryStrategy.getGenerationTiming() != GenerationTiming.NEVER ) {
 				hadInMemoryGeneration = true;
 			}
 		}
 
 		private void add(InDatabaseValueGenerationStrategy inDatabaseStrategy) {
 			if ( inDatabaseStrategies == null ) {
 				inDatabaseStrategies = new ArrayList<InDatabaseValueGenerationStrategy>();
 			}
 			inDatabaseStrategies.add( inDatabaseStrategy );
 
 			if ( inDatabaseStrategy.getGenerationTiming() != GenerationTiming.NEVER ) {
 				hadInDatabaseGeneration = true;
 			}
 		}
 
 		public GenerationStrategyPair buildPair() {
 			if ( hadInMemoryGeneration && hadInDatabaseGeneration ) {
 				throw new ValueGenerationStrategyException(
 						"Composite attribute [" + mappingProperty.getName() + "] contained both in-memory"
 								+ " and in-database value generation"
 				);
 			}
 			else if ( hadInMemoryGeneration ) {
 				throw new NotYetImplementedException( "Still need to wire in composite in-memory value generation" );
 
 			}
 			else if ( hadInDatabaseGeneration ) {
 				final Component composite = (Component) mappingProperty.getValue();
 
 				// we need the numbers to match up so we can properly handle 'referenced sql column values'
 				if ( inDatabaseStrategies.size() != composite.getPropertySpan() ) {
 					throw new ValueGenerationStrategyException(
 							"Internal error : mismatch between number of collected in-db generation strategies" +
 									" and number of attributes for composite attribute : " + mappingProperty.getName()
 					);
 				}
 
 				// the base-line values for the aggregated InDatabaseValueGenerationStrategy we will build here.
 				GenerationTiming timing = GenerationTiming.INSERT;
 				boolean referenceColumns = false;
 				String[] columnValues = new String[ composite.getColumnSpan() ];
 
 				// start building the aggregate values
 				int propertyIndex = -1;
 				int columnIndex = 0;
 				Iterator subProperties = composite.getPropertyIterator();
 				while ( subProperties.hasNext() ) {
 					propertyIndex++;
 					final Property subProperty = (Property) subProperties.next();
 					final InDatabaseValueGenerationStrategy subStrategy = inDatabaseStrategies.get( propertyIndex );
 
 					if ( subStrategy.getGenerationTiming() == GenerationTiming.ALWAYS ) {
 						// override the base-line to the more often "ALWAYS"...
 						timing = GenerationTiming.ALWAYS;
 
 					}
 					if ( subStrategy.referenceColumnsInSql() ) {
 						// override base-line value
 						referenceColumns = true;
 					}
 					if ( subStrategy.getReferencedColumnValues() != null ) {
 						if ( subStrategy.getReferencedColumnValues().length != subProperty.getColumnSpan() ) {
 							throw new ValueGenerationStrategyException(
 									"Internal error : mismatch between number of collected 'referenced column values'" +
 											" and number of columns for composite attribute : " + mappingProperty.getName() +
 											'.' + subProperty.getName()
 							);
 						}
 						System.arraycopy(
 								subStrategy.getReferencedColumnValues(),
 								0,
 								columnValues,
 								columnIndex,
 								subProperty.getColumnSpan()
 						);
 					}
 				}
 
 				// then use the aggregated values to build the InDatabaseValueGenerationStrategy
 				return new GenerationStrategyPair(
 						new InDatabaseValueGenerationStrategyImpl( timing, referenceColumns, columnValues )
 				);
 			}
 			else {
 				return NO_GEN_PAIR;
 			}
 		}
 	}
 
 	private static class NoInMemoryValueGenerationStrategy implements InMemoryValueGenerationStrategy {
 		/**
 		 * Singleton access
 		 */
 		public static final NoInMemoryValueGenerationStrategy INSTANCE = new NoInMemoryValueGenerationStrategy();
 
 		@Override
 		public GenerationTiming getGenerationTiming() {
 			return GenerationTiming.NEVER;
 		}
 
 		@Override
 		public ValueGenerator getValueGenerator() {
 			return null;
 		}
 	}
 
 	private static class FullInMemoryValueGenerationStrategy implements InMemoryValueGenerationStrategy {
 		private final GenerationTiming timing;
 		private final ValueGenerator generator;
 
 		private FullInMemoryValueGenerationStrategy(GenerationTiming timing, ValueGenerator generator) {
 			this.timing = timing;
 			this.generator = generator;
 		}
 
 		public static FullInMemoryValueGenerationStrategy create(ValueGeneration valueGeneration) {
 			return new FullInMemoryValueGenerationStrategy(
 					valueGeneration.getGenerationTiming(),
 					valueGeneration.getValueGenerator()
 			);
 		}
 
 		@Override
 		public GenerationTiming getGenerationTiming() {
 			return timing;
 		}
 
 		@Override
 		public ValueGenerator getValueGenerator() {
 			return generator;
 		}
 	}
 
 	private static class NoInDatabaseValueGenerationStrategy implements InDatabaseValueGenerationStrategy {
 		/**
 		 * Singleton access
 		 */
 		public static final NoInDatabaseValueGenerationStrategy INSTANCE = new NoInDatabaseValueGenerationStrategy();
 
 		@Override
 		public GenerationTiming getGenerationTiming() {
 			return GenerationTiming.NEVER;
 		}
 
 		@Override
 		public boolean referenceColumnsInSql() {
 			return true;
 		}
 
 		@Override
 		public String[] getReferencedColumnValues() {
 			return null;
 		}
 	}
 
 	private static class InDatabaseValueGenerationStrategyImpl implements InDatabaseValueGenerationStrategy {
 		private final GenerationTiming timing;
 		private final boolean referenceColumnInSql;
 		private final String[] referencedColumnValues;
 
 		private InDatabaseValueGenerationStrategyImpl(
 				GenerationTiming timing,
 				boolean referenceColumnInSql,
 				String[] referencedColumnValues) {
 			this.timing = timing;
 			this.referenceColumnInSql = referenceColumnInSql;
 			this.referencedColumnValues = referencedColumnValues;
 		}
 
 		@Override
 		public GenerationTiming getGenerationTiming() {
 			return timing;
 		}
 
 		@Override
 		public boolean referenceColumnsInSql() {
 			return referenceColumnInSql;
 		}
 
 		@Override
 		public String[] getReferencedColumnValues() {
 			return referencedColumnValues;
 		}
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( isInsertGenerated( runtimeProperty ) ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean isInsertGenerated(NonIdentifierAttribute property) {
 		return property.getValueGenerationStrategy() != null
 				&& property.getValueGenerationStrategy().getGenerationTiming() != GenerationTiming.NEVER;
 	}
 
 	private boolean isInsertGenerated(Property property) {
 		return property.getValueGenerationStrategy() != null
 				&& property.getValueGenerationStrategy().getGenerationTiming() != GenerationTiming.NEVER;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			final Property prop = ( Property ) subProperties.next();
 			if ( isInsertGenerated( prop ) ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( (Component) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( isUpdateGenerated( runtimeProperty ) ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private static boolean isUpdateGenerated(Property property) {
 		return property.getValueGenerationStrategy() != null
 				&& property.getValueGenerationStrategy().getGenerationTiming() == GenerationTiming.ALWAYS;
 	}
 
 	private static boolean isUpdateGenerated(NonIdentifierAttribute property) {
 		return property.getValueGenerationStrategy() != null
 				&& property.getValueGenerationStrategy().getGenerationTiming() == GenerationTiming.ALWAYS;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = (Property) subProperties.next();
 			if ( isUpdateGenerated( prop ) ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public boolean isNaturalIdentifierInsertGenerated() {
 		// the intention is for this call to replace the usage of the old ValueInclusion stuff (as exposed from
 		// persister) in SelectGenerator to determine if it is safe to use the natural identifier to find the
 		// insert-generated identifier.  That wont work if the natural-id is also insert-generated.
 		//
 		// Assumptions:
 		//		* That code checks that there is a natural identifier before making this call, so we assume the same here
 		// 		* That code assumes a non-composite natural-id, so we assume the same here
 		final InDatabaseValueGenerationStrategy strategy = inDatabaseValueGenerationStrategies[ naturalIdPropertyNumbers[0] ];
 		return strategy != null && strategy.getGenerationTiming() != GenerationTiming.NEVER;
 	}
 
 	public boolean isVersionGenerated() {
 		final InDatabaseValueGenerationStrategy strategy = inDatabaseValueGenerationStrategies[ versionPropertyIndex ];
 		return strategy != null && strategy.getGenerationTiming() != GenerationTiming.NEVER;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 
 	public boolean isNaturalIdentifierCached() {
 		return hasNaturalIdentifier() && hasCacheableNaturalId;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
 		return identifierAttribute;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
 	public NonIdentifierAttribute[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index;
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
 	public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasPreInsertGeneratedValues() {
 		return hasPreInsertGeneratedValues;
 	}
 
 	public boolean hasPreUpdateGeneratedValues() {
 		return hasPreUpdateGeneratedValues;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public InMemoryValueGenerationStrategy[] getInMemoryValueGenerationStrategies() {
 		return inMemoryValueGenerationStrategies;
 	}
 
 	public InDatabaseValueGenerationStrategy[] getInDatabaseValueGenerationStrategies() {
 		return inDatabaseValueGenerationStrategies;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	/**
 	 * Whether or not this class can be lazy (ie intercepted)
 	 */
 	public boolean isInstrumented() {
-		return instrumentationMetadata.isInstrumented();
-	}
-
-	public EntityInstrumentationMetadata getInstrumentationMetadata() {
-		return instrumentationMetadata;
+		return bytecodeEnhancementMetadata.isEnhancedForLazyLoading();
 	}
 
-	public boolean isLazyLoadingBytecodeEnhanced() {
-		return this.lazyLoadingBytecodeEnhanced;
+	public BytecodeEnhancementMetadata getBytecodeEnhancementMetadata() {
+		return bytecodeEnhancementMetadata;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index d81a98f947..862b9f0dd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,353 +1,340 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tuple.entity;
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoader;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.engine.spi.PersistentAttributeInterceptor;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.PojoInstantiator;
 import org.hibernate.type.CompositeType;
 
 /**
  * An {@link EntityTuplizer} specific to the pojo entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class PojoEntityTuplizer extends AbstractEntityTuplizer {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( PojoEntityTuplizer.class );
 
 	private final Class mappedClass;
 	private final Class proxyInterface;
 	private final boolean lifecycleImplementor;
 	private final Set<String> lazyPropertyNames;
 	private final ReflectionOptimizer optimizer;
-	private final boolean isInstrumented;
+	private final boolean isBytecodeEnhanced;
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getMappedClass();
 		this.proxyInterface = mappedEntity.getProxyInterface();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
-		this.isInstrumented = entityMetamodel.isInstrumented();
+		this.isBytecodeEnhanced = entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();
 
 		Iterator iter = mappedEntity.getPropertyClosureIterator();
 		Set<String> tmpLazyPropertyNames = new HashSet<String>( );
 		while ( iter.hasNext() ) {
 			Property property = (Property) iter.next();
 			if ( property.isLazy() ) {
 				tmpLazyPropertyNames.add( property.getName() );
 			}
 		}
 		lazyPropertyNames = tmpLazyPropertyNames.isEmpty() ? null : Collections.unmodifiableSet( tmpLazyPropertyNames );
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[i].getMethodName();
 			setterNames[i] = setters[i].getMethodName();
 			propTypes[i] = getters[i].getReturnType();
 		}
 
 		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
 					mappedClass,
 					getterNames,
 					setterNames,
 					propTypes
 			);
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 	}
 
 	@Override
 	protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
 		// determine all interfaces needed by the resulting proxy
 		
 		/*
 		 * We need to preserve the order of the interfaces they were put into the set, since javassist will choose the
 		 * first one's class-loader to construct the proxy class with. This is also the reason why HibernateProxy.class
 		 * should be the last one in the order (on JBossAS7 its class-loader will be org.hibernate module's class-
 		 * loader, which will not see the classes inside deployed apps.  See HHH-3078
 		 */
 		Set<Class> proxyInterfaces = new java.util.LinkedHashSet<Class>();
 
 		Class mappedClass = persistentClass.getMappedClass();
 		Class proxyInterface = persistentClass.getProxyInterface();
 
 		if ( proxyInterface != null && !mappedClass.equals( proxyInterface ) ) {
 			if ( !proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		Iterator<Subclass> subclasses = persistentClass.getSubclassIterator();
 		while ( subclasses.hasNext() ) {
 			final Subclass subclass = subclasses.next();
 			final Class subclassProxy = subclass.getProxyInterface();
 			final Class subclassClass = subclass.getMappedClass();
 			if ( subclassProxy != null && !subclassClass.equals( subclassProxy ) ) {
 				if ( !subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Iterator properties = persistentClass.getPropertyIterator();
 		Class clazz = persistentClass.getMappedClass();
 		while ( properties.hasNext() ) {
 			Property property = (Property) properties.next();
 			Method method = property.getGetter( clazz ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.gettersOfLazyClassesCannotBeFinal( persistentClass.getEntityName(), property.getName() );
 			}
 			method = property.getSetter( clazz ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.settersOfLazyClassesCannotBeFinal( persistentClass.getEntityName(), property.getName() );
 			}
 		}
 
 		Method idGetterMethod = idGetter == null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter == null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod == null || proxyInterface == null ?
 				null :
 				ReflectHelper.getMethod( proxyInterface, idGetterMethod );
 		Method proxySetIdentifierMethod = idSetterMethod == null || proxyInterface == null ?
 				null :
 				ReflectHelper.getMethod( proxyInterface, idSetterMethod );
 
 		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					persistentClass.hasEmbeddedIdentifier() ?
 							(CompositeType) persistentClass.getIdentifier().getType() :
 							null
 			);
 		}
 		catch (HibernateException he) {
 			LOG.unableToCreateProxyFactory( getEntityName(), he );
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(
 			PersistentClass persistentClass,
 			Getter idGetter,
 			Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory( getFactory() );
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
 	@Override
 	protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( persistentClass, null );
 		}
 		else {
 			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			setPropertyValuesWithOptimizer( entity, values );
 		}
 		else {
 			super.setPropertyValues( entity, values );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValues( entity );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValuesToInsert( entity, mergeMap, session );
 		}
 	}
 
 	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
 		optimizer.getAccessOptimizer().setPropertyValues( object, values );
 	}
 
 	protected Object[] getPropertyValuesWithOptimizer(Object object) {
 		return optimizer.getAccessOptimizer().getPropertyValues( object );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return mappedClass;
 	}
 
 	@Override
 	public boolean isLifecycleImplementor() {
 		return lifecycleImplementor;
 	}
 
 	@Override
 	protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
 	}
 
 	@Override
 	protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return proxyInterface;
 	}
 
 	//TODO: need to make the majority of this functionality into a top-level support class for custom impl support
 
 	@Override
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
-		if ( isInstrumented() ) {
-			Set<String> lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
-					lazyPropertyNames : null;
-			//TODO: if we support multiple fetch groups, we would need
-			//      to clone the set of lazy properties!
-			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
-		}
-
 		// new bytecode enhancement lazy interception
 		if ( entity instanceof PersistentAttributeInterceptable ) {
 			if ( lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ) {
-				PersistentAttributeInterceptor interceptor = new LazyAttributeLoader( session, lazyPropertyNames, getEntityName() );
+				PersistentAttributeInterceptor interceptor = new LazyAttributeLoadingInterceptor( session, lazyPropertyNames, getEntityName() );
 				( (PersistentAttributeInterceptable) entity ).$$_hibernate_setInterceptor( interceptor );
 			}
 		}
 
-		//also clear the fields that are marked as dirty in the dirtyness tracker
+		// also clear the fields that are marked as dirty in the dirtyness tracker
 		if ( entity instanceof SelfDirtinessTracker ) {
 			( (SelfDirtinessTracker) entity ).$$_hibernate_clearDirtyAttributes();
 		}
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		if ( getEntityMetamodel().hasLazyProperties() ) {
 			if ( entity instanceof PersistentAttributeInterceptable ) {
 				PersistentAttributeInterceptor interceptor = ( (PersistentAttributeInterceptable) entity ).$$_hibernate_getInterceptor();
-				if ( interceptor != null && interceptor instanceof LazyAttributeLoader ) {
-					return ( (LazyAttributeLoader) interceptor ).isUninitialized();
+				if ( interceptor != null && interceptor instanceof LazyAttributeLoadingInterceptor ) {
+					return ( (LazyAttributeLoadingInterceptor) interceptor ).hasAnyUninitializedAttributes();
 				}
 			}
-			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
-			return callback != null && !callback.isInitialized();
-		}
-		else {
-			return false;
 		}
+
+		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
-		return isInstrumented;
+		return isBytecodeEnhanced;
 	}
 
 	@Override
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		final Class concreteEntityClass = entityInstance.getClass();
 		if ( concreteEntityClass == getMappedClass() ) {
 			return getEntityName();
 		}
 		else {
 			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
 			if ( entityName == null ) {
 				throw new HibernateException(
 						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
 								+ " expected instance/subclass of [" + getEntityName() + "]"
 				);
 			}
 			return entityName;
 		}
 	}
 
 	@Override
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
index 13050a8ded..fdd0f08156 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
@@ -1,363 +1,363 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Map;
 
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
 import org.hibernate.tuple.NonIdentifierAttribute;
 
 /**
  * Collection of convenience methods relating to operations across arrays of types...
  *
  * @author Steve Ebersole
  */
 public class TypeHelper {
 	/**
 	 * Disallow instantiation
 	 */
 	private TypeHelper() {
 	}
 
 	/**
 	 * Deep copy a series of values from one array to another...
 	 *
 	 * @param values The values to copy (the source)
 	 * @param types The value types
 	 * @param copy an array indicating which values to include in the copy
 	 * @param target The array into which to copy the values
 	 * @param session The originating session
 	 */
 	public static void deepCopy(
 			final Object[] values,
 			final Type[] types,
 			final boolean[] copy,
 			final Object[] target,
 			final SessionImplementor session) {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( copy[i] ) {
 				if ( values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| values[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 					target[i] = values[i];
 				}
 				else {
 					target[i] = types[i].deepCopy( values[i], session
 						.getFactory() );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Apply the {@link Type#beforeAssemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param session The originating session
 	 */
 	public static void beforeAssemble(
 			final Serializable[] row,
 			final Type[] types,
 			final SessionImplementor session) {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( row[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 				&& row[i] != PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				types[i].beforeAssemble( row[i], session );
 			}
 		}
 	}
 
 	/**
 	 * Apply the {@link Type#assemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @return The assembled state
 	 */
 	public static Object[] assemble(
 			final Serializable[] row,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner) {
 		Object[] assembled = new Object[row.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				assembled[i] = row[i];
 			}
 			else {
 				assembled[i] = types[i].assemble( row[i], session, owner );
 			}
 		}
 		return assembled;
 	}
 
 	/**
 	 * Apply the {@link Type#disassemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param nonCacheable An array indicating which values to include in the disassembled state
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 *
 	 * @return The disassembled state
 	 */
 	public static Serializable[] disassemble(
 			final Object[] row,
 			final Type[] types,
 			final boolean[] nonCacheable,
 			final SessionImplementor session,
 			final Object owner) {
 		Serializable[] disassembled = new Serializable[row.length];
 		for ( int i = 0; i < row.length; i++ ) {
 			if ( nonCacheable!=null && nonCacheable[i] ) {
 				disassembled[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 			else if ( row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				disassembled[i] = (Serializable) row[i];
 			}
 			else {
 				disassembled[i] = types[i].disassemble( row[i], session, owner );
 			}
 		}
 		return disassembled;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replace(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 				|| original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else if ( target[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				// Should be no need to check for target[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN
 				// because PropertyAccessStrategyBackRefImpl.get( object ) returns
 				// PropertyAccessStrategyBackRefImpl.UNKNOWN, so target[i] == original[i].
 				//
 				// We know from above that original[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY &&
 				// original[i] != PropertyAccessStrategyBackRefImpl.UNKNOWN;
 				// This is a case where the entity being merged has a lazy property
 				// that has been initialized. Copy the initialized value from original.
 				if ( types[i].isMutable() ) {
 					copied[i] = types[i].deepCopy( original[i], session.getFactory() );
 				}
 				else {
 					copied[i] = original[i];
 				}
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 * @param foreignKeyDirection FK directionality to be applied to the replacement
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replace(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache, foreignKeyDirection );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values, as long as the corresponding
 	 * {@link Type} is an association.
 	 * <p/>
 	 * If the corresponding type is a component type, then apply {@link Type#replace} across the component
 	 * subtypes but do not replace the component value itself.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 * @param foreignKeyDirection FK directionality to be applied to the replacement
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replaceAssociations(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| original[i] == PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else if ( types[i].isComponentType() ) {
 				// need to extract the component values and check for subtype replacements...
 				CompositeType componentType = ( CompositeType ) types[i];
 				Type[] subtypes = componentType.getSubtypes();
 				Object[] origComponentValues = original[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues( original[i], session );
 				Object[] targetComponentValues = target[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues( target[i], session );
 				replaceAssociations( origComponentValues, targetComponentValues, subtypes, session, null, copyCache, foreignKeyDirection );
 				copied[i] = target[i];
 			}
 			else if ( !types[i].isAssociationType() ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache, foreignKeyDirection );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Determine if any of the given field values are dirty, returning an array containing
 	 * indices of the dirty fields.
 	 * <p/>
 	 * If it is determined that no fields are dirty, null is returned.
 	 *
 	 * @param properties The property definitions
 	 * @param currentState The current state of the entity
 	 * @param previousState The baseline state of the entity
 	 * @param includeColumns Columns to be included in the dirty checking, per property
 	 * @param anyUninitializedProperties Does the entity currently hold any uninitialized property values?
 	 * @param session The session from which the dirty check request originated.
 	 * 
 	 * @return Array containing indices of the dirty properties, or null if no properties considered dirty.
 	 */
 	public static int[] findDirty(
 			final NonIdentifierAttribute[] properties,
 			final Object[] currentState,
 			final Object[] previousState,
 			final boolean[][] includeColumns,
 			final boolean anyUninitializedProperties,
 			final SessionImplementor session) {
 		int[] results = null;
 		int count = 0;
 		int span = properties.length;
 
 		for ( int i = 0; i < span; i++ ) {
 			final boolean dirty = currentState[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 					&& properties[i].isDirtyCheckable( anyUninitializedProperties )
 					&& properties[i].getType().isDirty( previousState[i], currentState[i], includeColumns[i], session );
 			if ( dirty ) {
 				if ( results == null ) {
 					results = new int[span];
 				}
 				results[count++] = i;
 			}
 		}
 
 		if ( count == 0 ) {
 			return null;
 		}
 		else {
 			int[] trimmed = new int[count];
 			System.arraycopy( results, 0, trimmed, 0, count );
 			return trimmed;
 		}
 	}
 
 	/**
 	 * Determine if any of the given field values are modified, returning an array containing
 	 * indices of the modified fields.
 	 * <p/>
 	 * If it is determined that no fields are dirty, null is returned.
 	 *
 	 * @param properties The property definitions
 	 * @param currentState The current state of the entity
 	 * @param previousState The baseline state of the entity
 	 * @param includeColumns Columns to be included in the mod checking, per property
 	 * @param anyUninitializedProperties Does the entity currently hold any uninitialized property values?
 	 * @param session The session from which the dirty check request originated.
 	 *
 	 * @return Array containing indices of the modified properties, or null if no properties considered modified.
 	 */
 	public static int[] findModified(
 			final NonIdentifierAttribute[] properties,
 			final Object[] currentState,
 			final Object[] previousState,
 			final boolean[][] includeColumns,
 			final boolean anyUninitializedProperties,
 			final SessionImplementor session) {
 		int[] results = null;
 		int count = 0;
 		int span = properties.length;
 
 		for ( int i = 0; i < span; i++ ) {
 			final boolean modified = currentState[i]!=LazyPropertyInitializer.UNFETCHED_PROPERTY
 					&& properties[i].isDirtyCheckable(anyUninitializedProperties)
 					&& properties[i].getType().isModified( previousState[i], currentState[i], includeColumns[i], session );
 
 			if ( modified ) {
 				if ( results == null ) {
 					results = new int[span];
 				}
 				results[count++] = i;
 			}
 		}
 
 		if ( count == 0 ) {
 			return null;
 		}
 		else {
 			int[] trimmed = new int[count];
 			System.arraycopy( results, 0, trimmed, 0, count );
 			return trimmed;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/AbstractEnhancerTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/AbstractEnhancerTestTask.java
index f12165bd0b..f23a52a736 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/AbstractEnhancerTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/AbstractEnhancerTestTask.java
@@ -1,63 +1,64 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.service.ServiceRegistry;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestTask;
 
 /**
  * @author Luis Barreiro
  */
 public abstract class AbstractEnhancerTestTask implements EnhancerTestTask {
 
 	private ServiceRegistry serviceRegistry;
 	private SessionFactory factory;
 
 	public final void prepare(Configuration config) {
 		config.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 
 		Class<?>[] resources = getAnnotatedClasses();
 		for ( Class<?> resource : resources ) {
 			config.addAnnotatedClass( resource );
 		}
 
 		StandardServiceRegistryBuilder serviceBuilder = new StandardServiceRegistryBuilder( );
 		serviceBuilder.addService( ClassLoaderService.class, new ClassLoaderServiceImpl( Thread.currentThread().getContextClassLoader() ) );
 
 		serviceBuilder.applySettings( config.getProperties() );
 		serviceRegistry = serviceBuilder.build();
 		factory = config.buildSessionFactory( serviceRegistry );
 	}
 
 	public final void complete() {
 		try {
 			cleanup();
 		}
 		finally {
 			factory.close();
 			factory = null;
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 				serviceRegistry = null;
 			}
 		}
 	}
 
 	protected SessionFactory getFactory() {
 		return factory;
 	}
 
 	protected abstract void cleanup();
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/DecompileUtils.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/DecompileUtils.java
deleted file mode 100644
index 5d616e122d..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/DecompileUtils.java
+++ /dev/null
@@ -1,135 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.bytecode.enhancement;
-
-import java.io.File;
-import java.io.IOException;
-import java.util.Collections;
-import java.util.HashSet;
-import java.util.Set;
-import javax.tools.JavaCompiler;
-import javax.tools.JavaFileObject;
-import javax.tools.StandardJavaFileManager;
-import javax.tools.StandardLocation;
-import javax.tools.ToolProvider;
-
-import org.hibernate.bytecode.enhance.spi.EnhancerConstants;
-import org.hibernate.engine.spi.CompositeOwner;
-import org.hibernate.engine.spi.CompositeTracker;
-import org.hibernate.engine.spi.ManagedEntity;
-import org.hibernate.engine.spi.PersistentAttributeInterceptor;
-import org.hibernate.engine.spi.SelfDirtinessTracker;
-import org.hibernate.internal.CoreLogging;
-import org.hibernate.internal.CoreMessageLogger;
-
-import com.sun.tools.classfile.ConstantPoolException;
-import com.sun.tools.javap.JavapTask;
-
-import static org.junit.Assert.assertNull;
-import static org.junit.Assert.assertTrue;
-
-/**
- * utility class to use in bytecode enhancement tests
- *
- * @author Luis Barreiro
- */
-public abstract class DecompileUtils {
-
-	private static final CoreMessageLogger log = CoreLogging.messageLogger( DecompileUtils.class );
-
-	public static void decompileDumpedClass(String workingDir, String className) {
-		try {
-			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
-			StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, null, null );
-			fileManager.setLocation(
-					StandardLocation.CLASS_OUTPUT,
-					Collections.singletonList( new File( workingDir ) )
-			);
-
-			JavapTask javapTask = new JavapTask();
-			String filename = workingDir + File.separator + getFilenameForClassName( className );
-			for ( JavaFileObject jfo : fileManager.getJavaFileObjects( filename ) ) {
-				try {
-					Set<String> interfaceNames = new HashSet<String>();
-					Set<String> fieldNames = new HashSet<String>();
-					Set<String> methodNames = new HashSet<String>();
-
-					JavapTask.ClassFileInfo info = javapTask.read( jfo );
-
-					log.infof( "decompiled class [%s]", info.cf.getName() );
-
-					for ( int i : info.cf.interfaces ) {
-						interfaceNames.add( info.cf.constant_pool.getClassInfo( i ).getName() );
-						log.debugf( "declared iFace  = ", info.cf.constant_pool.getClassInfo( i ).getName() );
-					}
-					for ( com.sun.tools.classfile.Field f : info.cf.fields ) {
-						fieldNames.add( f.getName( info.cf.constant_pool ) );
-						log.debugf( "declared field  = ", f.getName( info.cf.constant_pool ) );
-					}
-					for ( com.sun.tools.classfile.Method m : info.cf.methods ) {
-						methodNames.add( m.getName( info.cf.constant_pool ) );
-						log.debugf( "declared method = ", m.getName( info.cf.constant_pool ) );
-					}
-
-					// checks signature against known interfaces
-					if ( interfaceNames.contains( PersistentAttributeInterceptor.class.getName() ) ) {
-						assertTrue( fieldNames.contains( EnhancerConstants.INTERCEPTOR_FIELD_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.INTERCEPTOR_GETTER_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.INTERCEPTOR_SETTER_NAME ) );
-					}
-					if ( interfaceNames.contains( ManagedEntity.class.getName() ) ) {
-						assertTrue( methodNames.contains( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME ) );
-
-						assertTrue( fieldNames.contains( EnhancerConstants.ENTITY_ENTRY_FIELD_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME ) );
-
-						assertTrue( fieldNames.contains( EnhancerConstants.PREVIOUS_FIELD_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.PREVIOUS_GETTER_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.PREVIOUS_SETTER_NAME ) );
-
-						assertTrue( fieldNames.contains( EnhancerConstants.NEXT_FIELD_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.NEXT_GETTER_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.NEXT_SETTER_NAME ) );
-					}
-					if ( interfaceNames.contains( SelfDirtinessTracker.class.getName() ) ) {
-						assertTrue( fieldNames.contains( EnhancerConstants.TRACKER_FIELD_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_CHANGER_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_GET_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_CLEAR_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_HAS_CHANGED_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_SUSPEND_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_COLLECTION_GET_NAME ) );
-					}
-					if ( interfaceNames.contains( CompositeTracker.class.getName() ) ) {
-						assertTrue( fieldNames.contains( EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER ) );
-					}
-					if ( interfaceNames.contains( CompositeOwner.class.getName() ) ) {
-						assertTrue( fieldNames.contains( EnhancerConstants.TRACKER_CHANGER_NAME ) );
-						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_CHANGER_NAME ) );
-					}
-				}
-				catch (ConstantPoolException e) {
-					e.printStackTrace();
-				}
-			}
-		}
-		catch (IOException ioe) {
-			assertNull( "Failed to open class file", ioe );
-		}
-		catch (RuntimeException re) {
-			log.warnf( re, "WARNING: UNABLE DECOMPILE DUE TO %s", re.getMessage() );
-		}
-	}
-
-	private static String getFilenameForClassName(String className) {
-		return className.replace( '.', File.separatorChar ) + JavaFileObject.Kind.CLASS.extension;
-	}
-
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
index d2210141b4..14276c5813 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTest.java
@@ -1,125 +1,126 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.bytecode.enhancement.association.ManyToManyAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.OneToManyAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.association.OneToOneAssociationTestTask;
 import org.hibernate.test.bytecode.enhancement.basic.BasicEnhancementTestTask;
 import org.hibernate.test.bytecode.enhancement.dirty.DirtyTrackingTestTask;
 import org.hibernate.test.bytecode.enhancement.field.FieldAccessBidirectionalTestTasK;
 import org.hibernate.test.bytecode.enhancement.field.FieldAccessEnhancementTestTask;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask1;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask2;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask3;
 import org.hibernate.test.bytecode.enhancement.join.HHH3949TestTask4;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyBasicFieldNotInitializedTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyCollectionLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyLoadingIntegrationTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.LazyLoadingTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.basic.LazyBasicFieldAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.lazy.basic.LazyBasicPropertyAccessTestTask;
 import org.hibernate.test.bytecode.enhancement.merge.CompositeMergeTestTask;
 import org.hibernate.test.bytecode.enhancement.pk.EmbeddedPKTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyCollectionWithClearedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyCollectionWithClosedSessionTestTask;
 import org.hibernate.test.bytecode.enhancement.ondemandload.LazyEntityLoadingWithClosedSessionTestTask;
 import org.junit.Test;
 
 /**
  * @author Luis Barreiro
  */
 public class EnhancerTest extends BaseUnitTestCase {
 
 	@Test
 	public void testBasic() {
 		EnhancerTestUtils.runEnhancerTestTask( BasicEnhancementTestTask.class );
 	}
 
 	@Test
 	public void testDirty() {
 		EnhancerTestUtils.runEnhancerTestTask( DirtyTrackingTestTask.class );
 	}
 
 	@Test
 	public void testAssociation() {
 		EnhancerTestUtils.runEnhancerTestTask( OneToOneAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( OneToManyAssociationTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( ManyToManyAssociationTestTask.class );
 	}
 
 	@Test
 	public void testLazy() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyLoadingTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyLoadingIntegrationTestTask.class );
 
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicPropertyAccessTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicFieldAccessTestTask.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-10055" )
 	public void testLazyCollectionHandling() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionLoadingTestTask.class );
 	}
 
 	@Test(timeout = 10000)
 	@TestForIssue( jiraKey = "HHH-10055" )
 	@FailureExpected( jiraKey = "HHH-10055" )
 	public void testOnDemand() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionWithClearedSessionTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyCollectionWithClosedSessionTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( LazyEntityLoadingWithClosedSessionTestTask.class );
 	}
 
 	@Test
 	public void testMerge() {
 		EnhancerTestUtils.runEnhancerTestTask( CompositeMergeTestTask.class );
 	}
 
 	@Test
 	public void testEmbeddedPK() {
 		EnhancerTestUtils.runEnhancerTestTask( EmbeddedPKTestTask.class );
 	}
 
 	@Test
 	public void testFieldAccess() {
 		EnhancerTestUtils.runEnhancerTestTask( FieldAccessEnhancementTestTask.class );
 		EnhancerTestUtils.runEnhancerTestTask( FieldAccessBidirectionalTestTasK.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-3949" )
 	@FailureExpected( jiraKey = "HHH-3949" )
 	public void testJoinFetchLazyToOneAttributeHql() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask1.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-3949" )
 	@FailureExpected( jiraKey = "HHH-3949" )
 	public void testJoinFetchLazyToOneAttributeHql2() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask2.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-3949" )
 	@FailureExpected( jiraKey = "HHH-3949" )
 	public void testHHH3949() {
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask3.class );
 		EnhancerTestUtils.runEnhancerTestTask( HHH3949TestTask4.class );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9937")
 	public void testLazyBasicFieldNotInitialized() {
 		EnhancerTestUtils.runEnhancerTestTask( LazyBasicFieldNotInitializedTestTask.class );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/association/OneToOneAssociationTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/association/OneToOneAssociationTestTask.java
index f860465b24..1767b82a79 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/association/OneToOneAssociationTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/association/OneToOneAssociationTestTask.java
@@ -1,54 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.association;
 
 import java.util.UUID;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 
 /**
  * @author Luis Barreiro
  */
 public class OneToOneAssociationTestTask extends AbstractEnhancerTestTask {
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Customer.class, User.class};
 	}
 
 	public void prepare() {
 	}
 
 	public void execute() {
 		User user = new User();
 		user.setLogin( UUID.randomUUID().toString() );
 
 		Customer customer = new Customer();
 		customer.setUser( user );
 
 		Assert.assertEquals( customer, user.getCustomer() );
 
 		// check dirty tracking is set automatically with bi-directional association management
 		EnhancerTestUtils.checkDirtyTracking( user, "login", "customer" );
 
 		User anotherUser = new User();
 		anotherUser.setLogin( UUID.randomUUID().toString() );
 
 		customer.setUser( anotherUser );
 
 		Assert.assertNull( user.getCustomer() );
 		Assert.assertEquals( customer, anotherUser.getCustomer() );
 
 		user.setCustomer( new Customer() );
 
 		Assert.assertEquals( user, user.getCustomer().getUser() );
 	}
 
 	protected void cleanup() {
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicEnhancementTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicEnhancementTestTask.java
index a563acaa76..5c516c4fc3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicEnhancementTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicEnhancementTestTask.java
@@ -1,72 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.basic;
 
 import org.hibernate.engine.spi.ManagedEntity;
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Luis Barreiro
  */
 public class BasicEnhancementTestTask extends AbstractEnhancerTestTask {
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {SimpleEntity.class};
 	}
 
 	public void prepare() {
 	}
 
 	public void execute() {
 		SimpleEntity entity = new SimpleEntity();
 
 		// Call the new ManagedEntity methods
 		assertTyping( ManagedEntity.class, entity );
 		ManagedEntity managedEntity = (ManagedEntity) entity;
 		assertSame( entity, managedEntity.$$_hibernate_getEntityInstance() );
 
 		assertNull( managedEntity.$$_hibernate_getEntityEntry() );
 		managedEntity.$$_hibernate_setEntityEntry( EnhancerTestUtils.makeEntityEntry() );
 		assertNotNull( managedEntity.$$_hibernate_getEntityEntry() );
 		managedEntity.$$_hibernate_setEntityEntry( null );
 		assertNull( managedEntity.$$_hibernate_getEntityEntry() );
 
 		managedEntity.$$_hibernate_setNextManagedEntity( managedEntity );
 		managedEntity.$$_hibernate_setPreviousManagedEntity( managedEntity );
 		assertSame( managedEntity, managedEntity.$$_hibernate_getNextManagedEntity() );
 		assertSame( managedEntity, managedEntity.$$_hibernate_getPreviousManagedEntity() );
 
 		// Add an attribute interceptor...
 		assertTyping( PersistentAttributeInterceptable.class, entity );
 		PersistentAttributeInterceptable interceptableEntity = (PersistentAttributeInterceptable) entity;
 
 		assertNull( interceptableEntity.$$_hibernate_getInterceptor() );
 		interceptableEntity.$$_hibernate_setInterceptor( new ObjectAttributeMarkerInterceptor() );
 		assertNotNull( interceptableEntity.$$_hibernate_getInterceptor() );
 
 
 		assertNull( EnhancerTestUtils.getFieldByReflection( entity, "anUnspecifiedObject" ) );
 		entity.setAnObject( new Object() );
 
 		assertSame( EnhancerTestUtils.getFieldByReflection( entity, "anUnspecifiedObject" ), ObjectAttributeMarkerInterceptor.WRITE_MARKER );
 		assertSame( entity.getAnObject(), ObjectAttributeMarkerInterceptor.READ_MARKER );
 
 		entity.setAnObject( null );
 		assertSame( EnhancerTestUtils.getFieldByReflection( entity, "anUnspecifiedObject" ), ObjectAttributeMarkerInterceptor.WRITE_MARKER );
 	}
 
 	protected void cleanup() {
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicInSessionTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicInSessionTest.java
index dce1a74ea6..02ad8fb5eb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicInSessionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/basic/BasicInSessionTest.java
@@ -1,76 +1,76 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.basic;
 
 import java.net.URL;
 import java.net.URLClassLoader;
 
 import org.hibernate.Session;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 import org.junit.Test;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicInSessionTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] {MyEntity.class};
 	}
 
 	@Test
 	public void testIt() {
 		Session s = openSession();
 		s.beginTransaction();
 		s.save( new MyEntity( 1L ) );
 		s.save( new MyEntity( 2L ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		MyEntity myEntity1 = s.get( MyEntity.class, 1L );
 		MyEntity myEntity2 = s.get( MyEntity.class, 2L );
 
 		assertNotNull( myEntity1.$$_hibernate_getEntityInstance() );
 		assertSame( myEntity1, myEntity1.$$_hibernate_getEntityInstance() );
 		assertNotNull( myEntity1.$$_hibernate_getEntityEntry() );
 		assertNull( myEntity1.$$_hibernate_getPreviousManagedEntity() );
 		assertNotNull( myEntity1.$$_hibernate_getNextManagedEntity() );
 
 		assertNotNull( myEntity2.$$_hibernate_getEntityInstance() );
 		assertSame( myEntity2, myEntity2.$$_hibernate_getEntityInstance() );
 		assertNotNull( myEntity2.$$_hibernate_getEntityEntry() );
 		assertNotNull( myEntity2.$$_hibernate_getPreviousManagedEntity() );
 		assertNull( myEntity2.$$_hibernate_getNextManagedEntity() );
 
 		s.createQuery( "delete MyEntity" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 
 		assertNull( myEntity1.$$_hibernate_getEntityEntry() );
 	}
 
 	@Test
 	public void enhacementTest() {
 		try {
 			EnhancerTestUtils.enhanceAndDecompile( SimpleEntity.class, new URLClassLoader( new URL[0] ) );
 		}
 		catch (Exception e) {
 			e.printStackTrace();
 			Assert.fail( "Unexpected exception in EnhancerTestUtils.enhanceAndDecompile(): " + e.getMessage() );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/dirty/DirtyTrackingTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/dirty/DirtyTrackingTestTask.java
index 624726f17a..7d32845186 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/dirty/DirtyTrackingTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/dirty/DirtyTrackingTestTask.java
@@ -1,91 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.dirty;
 
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 
 /**
  * @author Luis Barreiro
  */
 public class DirtyTrackingTestTask extends AbstractEnhancerTestTask {
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {SimpleEntity.class};
 	}
 
 	public void prepare() {
 	}
 
 	public void execute() {
 		SimpleEntity entity = new SimpleEntity();
 
 		// Basic single field
 		entity.getSomeNumber();
 		EnhancerTestUtils.checkDirtyTracking( entity );
 		entity.setSomeNumber( 1l );
 		EnhancerTestUtils.checkDirtyTracking( entity, "someNumber" );
 		EnhancerTestUtils.clearDirtyTracking( entity );
 		entity.setSomeNumber( entity.getSomeNumber() );
 		EnhancerTestUtils.checkDirtyTracking( entity );
 
 		// Basic multi-field (Id properties are not flagged as dirty)
 		entity.setId( 2l );
 		entity.setActive( !entity.isActive() );
 		entity.setSomeNumber( 193L );
 		EnhancerTestUtils.checkDirtyTracking( entity, "active", "someNumber" );
 		EnhancerTestUtils.clearDirtyTracking( entity );
 
 		// Setting the same value should not make it dirty
 		entity.setSomeNumber( 193L );
 		EnhancerTestUtils.checkDirtyTracking( entity );
 
 		// Collection
 		List<String> strings = new ArrayList<String>();
 		strings.add( "FooBar" );
 		entity.setSomeStrings( strings );
 		EnhancerTestUtils.checkDirtyTracking( entity, "someStrings" );
 		EnhancerTestUtils.clearDirtyTracking( entity );
 
 		strings.add( "BarFoo" );
 		EnhancerTestUtils.checkDirtyTracking( entity, "someStrings" );
 		EnhancerTestUtils.clearDirtyTracking( entity );
 
 		// Association: this should not set the entity to dirty
 		Set<Integer> intSet = new HashSet<Integer>();
 		intSet.add( 42 );
 		entity.setSomeInts( intSet );
 		EnhancerTestUtils.checkDirtyTracking( entity );
 
 		// testing composite object
 		Address address = new Address();
 		entity.setAddress( address );
 		address.setCity( "Arendal" );
 		EnhancerTestUtils.checkDirtyTracking( entity, "address" );
 		EnhancerTestUtils.clearDirtyTracking( entity );
 
 		// make sure that new composite instances are cleared
 		Address address2 = new Address();
 		entity.setAddress( address2 );
 		address.setStreet1( "Heggedalveien" );
 		EnhancerTestUtils.checkDirtyTracking( entity, "address" );
 
 		Country country = new Country();
 		address2.setCountry( country );
 		country.setName( "Norway" );
 		EnhancerTestUtils.checkDirtyTracking( entity, "address", "address.country" );
 
 	}
 
 	protected void cleanup() {
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessBidirectionalTestTasK.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessBidirectionalTestTasK.java
index 3cc500331b..9fcf765499 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessBidirectionalTestTasK.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessBidirectionalTestTasK.java
@@ -1,82 +1,82 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.field;
 
 import java.util.UUID;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.OneToOne;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 
 /**
  * @author Luis Barreiro
  */
 public class FieldAccessBidirectionalTestTasK extends AbstractEnhancerTestTask {
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Customer.class, User.class};
 	}
 
 	public void prepare() {
 	}
 
 	public void execute() {
 		User user = new User();
 		user.login = UUID.randomUUID().toString();
 
 		Customer customer = new Customer();
 		customer.user = user;
 
 		Assert.assertEquals( customer, EnhancerTestUtils.getFieldByReflection( user, "customer" ) );
 
 		// check dirty tracking is set automatically with bi-directional association management
 		EnhancerTestUtils.checkDirtyTracking( user, "login", "customer" );
 
 		User anotherUser = new User();
 		anotherUser.login = UUID.randomUUID().toString();
 
 		customer.user = anotherUser;
 
 		Assert.assertNull( user.customer );
 		Assert.assertEquals( customer, EnhancerTestUtils.getFieldByReflection( anotherUser, "customer" ) );
 
 		user.customer = new Customer();
 		Assert.assertEquals( user, user.customer.user );
 	}
 
 	protected void cleanup() {
 	}
 
 	@Entity public class Customer {
 
 		@Id public int id;
 
 		@OneToOne(fetch = FetchType.LAZY) public User user;
 
 		public String firstName;
 
 		public String lastName;
 
 		public int version;
 	}
 
 	@Entity public class User {
 
 		@Id public int id;
 
 		public String login;
 
 		public String password;
 
 		@OneToOne(mappedBy = "user", fetch = FetchType.LAZY) public Customer customer;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessEnhancementTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessEnhancementTestTask.java
index ae021074e4..a33a48b4a2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessEnhancementTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/field/FieldAccessEnhancementTestTask.java
@@ -1,90 +1,90 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.field;
 
 import java.util.Arrays;
 import java.util.List;
 import java.util.Set;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.OneToMany;
 
 import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.hibernate.test.bytecode.enhancement.basic.ObjectAttributeMarkerInterceptor;
 import org.junit.Assert;
 
 /**
  * @author Luis Barreiro
  */
 public class FieldAccessEnhancementTestTask extends AbstractEnhancerTestTask {
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {SimpleEntity.class};
 	}
 
 	public void prepare() {
 	}
 
 	public void execute() {
 		// test uses ObjectAttributeMarkerInterceptor to ensure that field access is routed through enhanced methods
 
 		SimpleEntity entity = new SimpleEntity();
 		( (PersistentAttributeInterceptable) entity ).$$_hibernate_setInterceptor( new ObjectAttributeMarkerInterceptor() );
 
 		Object decoy = new Object();
 		entity.anUnspecifiedObject = decoy;
 
 		Object gotByReflection = EnhancerTestUtils.getFieldByReflection( entity, "anUnspecifiedObject" );
 		Assert.assertNotSame( gotByReflection, decoy );
 		Assert.assertSame( gotByReflection, ObjectAttributeMarkerInterceptor.WRITE_MARKER );
 
 		Object entityObject = entity.anUnspecifiedObject;
 
 		Assert.assertNotSame( entityObject, decoy );
 		Assert.assertSame( entityObject, ObjectAttributeMarkerInterceptor.READ_MARKER );
 
 		// do some more calls on the various types, without the interceptor
 		( (PersistentAttributeInterceptable) entity ).$$_hibernate_setInterceptor( null );
 
 		entity.id = 1234567890l;
 		Assert.assertEquals( entity.id, 1234567890l );
 
 		entity.name = "Entity Name";
 		Assert.assertSame( entity.name, "Entity Name" );
 
 		entity.active = true;
 		Assert.assertTrue( entity.active );
 
 		entity.someStrings = Arrays.asList( "A", "B", "C", "D" );
 		Assert.assertArrayEquals( new String[] { "A", "B", "C", "D" }, entity.someStrings.toArray() );
 	}
 
 	protected void cleanup() {
 	}
 
 	@Entity public class SimpleEntity {
 
 		@Id public long id;
 
 		public String name;
 
 		public boolean active;
 
 		public long someNumber;
 
 		public int anInt;
 
 		public Object anUnspecifiedObject;
 
 		public List<String> someStrings;
 
 		@OneToMany public Set<Integer> someInts;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyBasicFieldNotInitializedTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyBasicFieldNotInitializedTestTask.java
index 7a6362ae2e..3ae9a1c6c1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyBasicFieldNotInitializedTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyBasicFieldNotInitializedTestTask.java
@@ -1,95 +1,94 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.lazy;
 
 
 import javax.persistence.Basic;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 
 /**
  * @author Gail Badner
  */
 public class LazyBasicFieldNotInitializedTestTask extends AbstractEnhancerTestTask {
 
 	private Long entityId;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Entity.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Entity entity = new Entity();
 		entity.setDescription( "desc" );
 		s.persist( entity );
 		entityId = entity.getId();
 
 		s.getTransaction().commit();
 		s.clear();
 		s.close();
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 		Entity entity = s.get( Entity.class, entityId );
 		Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanup() {
 	}
 
 	@javax.persistence.Entity
 	@Table(name = "lazy_field_not_init")
 	public static class Entity {
 		@Id
 		@GeneratedValue
 		private Long id;
 
 		@Basic(fetch = FetchType.LAZY)
 		private String description;
 
 		public Long getId() {
 			return id;
 		}
 
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		public String getDescription() {
 			return description;
 		}
 
 		public void setDescription(String description) {
 			this.description = description;
 		}
 	}
 
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyCollectionLoadingTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyCollectionLoadingTestTask.java
index a316504d7d..d7ab8c4001 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyCollectionLoadingTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyCollectionLoadingTestTask.java
@@ -1,101 +1,99 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.lazy;
 
 import java.util.ArrayList;
-import java.util.Collection;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.proxy.HibernateProxy;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
-import org.junit.Assert;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 
 import static org.hamcrest.CoreMatchers.equalTo;
 import static org.hamcrest.CoreMatchers.instanceOf;
 import static org.hamcrest.CoreMatchers.not;
 import static org.hamcrest.CoreMatchers.notNullValue;
 import static org.hamcrest.CoreMatchers.sameInstance;
 import static org.hamcrest.MatcherAssert.assertThat;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Simple test for lazy collection handling in the new bytecode support.
  * Prior to HHH-10055 lazy collections were simply not handled.  The tests
  * initially added for HHH-10055 cover the more complicated case of handling
  * lazy collection initialization outside of a transaction; that is a bigger
  * fix, and I first want to get collection handling to work here in general.
  *
  * @author Steve Ebersole
  */
 public class LazyCollectionLoadingTestTask extends AbstractEnhancerTestTask {
 	private static final int CHILDREN_SIZE = 10;
 	private Long parentID;
 	private Long lastChildID;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Parent.class, Child.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "false" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Parent parent = new Parent();
 		parent.setChildren( new ArrayList<Child>() );
 		for ( int i = 0; i < CHILDREN_SIZE; i++ ) {
 			final Child child = new Child();
 			child.setParent( parent );
 			s.persist( child );
 			lastChildID = child.getId();
 		}
 		s.persist( parent );
 		parentID = parent.getId();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Parent parent = s.load( Parent.class, parentID );
 		assertThat( parent, notNullValue() );
 		assertThat( parent, not( instanceOf( HibernateProxy.class ) ) );
 		assertThat( parent, not( instanceOf( HibernateProxy.class ) ) );
 		assertFalse( Hibernate.isPropertyInitialized( parent, "children" ) );
 		EnhancerTestUtils.checkDirtyTracking( parent );
 
 		List children1 = parent.getChildren();
 		List children2 = parent.getChildren();
 
 		assertTrue( Hibernate.isPropertyInitialized( parent, "children" ) );
 		EnhancerTestUtils.checkDirtyTracking( parent );
 
 		assertThat( children1, sameInstance( children2 ) );
 		assertThat( children1.size(), equalTo( CHILDREN_SIZE ) );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanup() {
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingIntegrationTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingIntegrationTestTask.java
index 7ea64c7906..43ac5f9f98 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingIntegrationTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingIntegrationTestTask.java
@@ -1,88 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.lazy;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 
 /**
  * @author Luis Barreiro
  */
 public class LazyLoadingIntegrationTestTask extends AbstractEnhancerTestTask {
 
 	private static final int CHILDREN_SIZE = 10;
 	private Long parentID;
 	private Long lastChildID;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Parent.class, Child.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Parent parent = new Parent();
 		parent.setChildren( new ArrayList<Child>( CHILDREN_SIZE ) );
 		for ( int i = 0; i < CHILDREN_SIZE; i++ ) {
 			final Child child = new Child();
 			// Association management should kick in here
 			child.setParent( parent );
 			s.persist( child );
 			lastChildID = child.getId();
 		}
 		s.persist( parent );
 		parentID = parent.getId();
 
 		s.getTransaction().commit();
 		s.clear();
 		s.close();
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Child loadedChild = s.load( Child.class, lastChildID );
 		EnhancerTestUtils.checkDirtyTracking( loadedChild );
 
 		loadedChild.setName( "Barrabas" );
 		EnhancerTestUtils.checkDirtyTracking( loadedChild, "name" );
 
 		Parent loadedParent = loadedChild.getParent();
 		EnhancerTestUtils.checkDirtyTracking( loadedChild, "name" );
 		EnhancerTestUtils.checkDirtyTracking( loadedParent );
 
 		List<Child> loadedChildren = new ArrayList<Child>( loadedParent.getChildren() );
 		loadedChildren.remove( 0 );
 		loadedChildren.remove( loadedChild );
 		loadedParent.setChildren( loadedChildren );
 
 		EnhancerTestUtils.checkDirtyTracking( loadedParent, "children" );
 		Assert.assertNull( loadedChild.parent );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanup() {
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingTestTask.java
index dfd456f96e..9b88d30fd1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/LazyLoadingTestTask.java
@@ -1,98 +1,98 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.lazy;
 
 import java.util.ArrayList;
 import java.util.Collection;
 
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.proxy.HibernateProxy;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 
 /**
  * @author Luis Barreiro
  */
 public class LazyLoadingTestTask extends AbstractEnhancerTestTask {
 
 	private static final int CHILDREN_SIZE = 10;
 	private Long parentID;
 	private Long lastChildID;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Parent.class, Child.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Parent parent = new Parent();
 		parent.setChildren(new ArrayList<Child>());
 		for ( int i = 0; i < CHILDREN_SIZE; i++ ) {
 			final Child child = new Child();
 			child.setParent( parent );
 			s.persist( child );
 			lastChildID = child.getId();
 		}
 		s.persist( parent );
 		parentID = parent.getId();
 
 		s.getTransaction().commit();
 		s.clear();
 		s.close();
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Child loadedChild = s.load( Child.class, lastChildID );
 
 		Object parentByReflection = EnhancerTestUtils.getFieldByReflection( loadedChild, "parent" );
 		Assert.assertNull( "Lazy field 'parent' is initialized", parentByReflection );
 		Assert.assertFalse( loadedChild instanceof HibernateProxy );
 
 		Parent loadedParent = loadedChild.getParent();
 
 		EnhancerTestUtils.checkDirtyTracking( loadedChild );
 
 		parentByReflection = EnhancerTestUtils.getFieldByReflection( loadedChild, "parent" );
 		Object childrenByReflection = EnhancerTestUtils.getFieldByReflection( loadedParent, "children" );
 		Assert.assertNotNull( "Lazy field 'parent' is not loaded", parentByReflection );
 		Assert.assertNull( "Lazy field 'children' is initialized", childrenByReflection );
 		Assert.assertFalse( loadedParent instanceof HibernateProxy );
 		Assert.assertTrue( parentID.equals( loadedParent.id ) );
 
 		Collection<Child> loadedChildren = loadedParent.getChildren();
 
 		EnhancerTestUtils.checkDirtyTracking( loadedChild );
 		EnhancerTestUtils.checkDirtyTracking( loadedParent );
 
 		childrenByReflection = EnhancerTestUtils.getFieldByReflection( loadedParent, "children" );
 		Assert.assertNotNull( "Lazy field 'children' is not loaded", childrenByReflection );
 		Assert.assertFalse( loadedChildren instanceof HibernateProxy );
 		Assert.assertEquals( CHILDREN_SIZE, loadedChildren.size() );
 		Assert.assertTrue( loadedChildren.contains( loadedChild ) );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanup() {
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicFieldAccessTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicFieldAccessTestTask.java
index f82808c016..83f8f9e4f8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicFieldAccessTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicFieldAccessTestTask.java
@@ -1,145 +1,145 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.lazy.basic;
 
 
 import javax.persistence.Basic;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 
 import org.junit.Assert;
 
 /**
  * @author Gail Badner
  */
 public class LazyBasicFieldAccessTestTask extends AbstractEnhancerTestTask {
 
 	private Long entityId;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Entity.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Entity entity = new Entity();
 		entity.setDescription( "desc" );
 		s.persist( entity );
 		entityId = entity.getId();
 
 		s.getTransaction().commit();
 		s.clear();
 		s.close();
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Entity entity = s.get( Entity.class, entityId );
 
 		Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		EnhancerTestUtils.checkDirtyTracking( entity );
 
 		Assert.assertEquals( "desc", entity.getDescription() );
 		Assert.assertTrue( Hibernate.isPropertyInitialized( entity, "description" ) );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity.setDescription( "desc1" );
 		s.update( entity );
 
 		//Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		EnhancerTestUtils.checkDirtyTracking( entity, "description" );
 
 		Assert.assertEquals( "desc1", entity.getDescription() );
 		Assert.assertTrue( Hibernate.isPropertyInitialized( entity, "description" ) );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity = s.get( Entity.class, entityId );
 		Assert.assertEquals( "desc1", entity.getDescription() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity.setDescription( "desc2" );
 		entity = (Entity) s.merge( entity );
 
 		//Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		EnhancerTestUtils.checkDirtyTracking( entity, "description" );
 
 		Assert.assertEquals( "desc2", entity.getDescription() );
 		Assert.assertTrue( Hibernate.isPropertyInitialized( entity, "description" ) );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity = s.get( Entity.class, entityId );
 		Assert.assertEquals( "desc2", entity.getDescription() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanup() {
 	}
 
 	@javax.persistence.Entity
 	@Table(name = "lazy_field_access")
 	public static class Entity {
 		private Long id;
 
 		private String description;
 
 		@Id
 		@GeneratedValue
 		public Long getId() {
 			return id;
 		}
 
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		@Basic(fetch = FetchType.LAZY)
 		public String getDescription() {
 			return description;
 		}
 
 		public void setDescription(String description) {
 			this.description = description;
 		}
 	}
 
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicPropertyAccessTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicPropertyAccessTestTask.java
index 0742c981df..d284e65335 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicPropertyAccessTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/lazy/basic/LazyBasicPropertyAccessTestTask.java
@@ -1,147 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.lazy.basic;
 
 
 import javax.persistence.Access;
 import javax.persistence.AccessType;
 import javax.persistence.Basic;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 import org.junit.Assert;
 
 /**
  * @author Gail Badner
  */
 public class LazyBasicPropertyAccessTestTask extends AbstractEnhancerTestTask {
 
 	private Long entityId;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {Entity.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Entity entity = new Entity();
 		entity.setDescription( "desc" );
 		s.persist( entity );
 		entityId = entity.getId();
 
 		s.getTransaction().commit();
 		s.clear();
 		s.close();
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 
 		Entity entity = s.get( Entity.class, entityId );
 
 		Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		EnhancerTestUtils.checkDirtyTracking( entity );
 
 		Assert.assertEquals( "desc", entity.getDescription() );
 		Assert.assertTrue( Hibernate.isPropertyInitialized( entity, "description" ) );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity.setDescription( "desc1" );
 		s.update( entity );
 
 		// Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		EnhancerTestUtils.checkDirtyTracking( entity, "description" );
 
 		Assert.assertEquals( "desc1", entity.getDescription() );
 		Assert.assertTrue( Hibernate.isPropertyInitialized( entity, "description" ) );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity = s.get( Entity.class, entityId );
 		Assert.assertEquals( "desc1", entity.getDescription() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity.setDescription( "desc2" );
 		entity = (Entity) s.merge( entity );
 
 		//Assert.assertFalse( Hibernate.isPropertyInitialized( entity, "description" ) );
 		EnhancerTestUtils.checkDirtyTracking( entity, "description" );
 
 		Assert.assertEquals( "desc2", entity.getDescription() );
 		Assert.assertTrue( Hibernate.isPropertyInitialized( entity, "description" ) );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		entity = s.get( Entity.class, entityId );
 		Assert.assertEquals( "desc2", entity.getDescription() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	protected void cleanup() {
 	}
 
 	@javax.persistence.Entity
 	@Access(AccessType.FIELD )
 	@Table(name="lazy_property_access")
 	public static class Entity {
 		@Id
 		@GeneratedValue
 		private Long id;
 
 		@Basic(fetch = FetchType.LAZY)
 		private String description;
 
 		public Long getId() {
 			return id;
 		}
 
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		public String getDescription() {
 			return description;
 		}
 
 		public void setDescription(String description) {
 			this.description = description;
 		}
 	}
 
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/merge/CompositeMergeTestTask.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/merge/CompositeMergeTestTask.java
index d37d968935..fb79374f18 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/merge/CompositeMergeTestTask.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/merge/CompositeMergeTestTask.java
@@ -1,158 +1,157 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.bytecode.enhancement.merge;
 
 import java.util.Arrays;
 import java.util.List;
 import javax.persistence.Basic;
 import javax.persistence.CollectionTable;
-import javax.persistence.Column;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.Table;
 
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 
 import org.hibernate.test.bytecode.enhancement.AbstractEnhancerTestTask;
-import org.hibernate.test.bytecode.enhancement.EnhancerTestUtils;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
 
 import org.junit.Assert;
 
 /**
  * @author Luis Barreiro
  */
 public class CompositeMergeTestTask extends AbstractEnhancerTestTask {
 
 	private long entityId;
 
 	public Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {ParentEntity.class, Address.class, Country.class};
 	}
 
 	public void prepare() {
 		Configuration cfg = new Configuration();
 		cfg.setProperty( Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		super.prepare( cfg );
 
 		ParentEntity parent = new ParentEntity();
 		parent.description = "desc";
 		parent.address = new Address();
 		parent.address.street = "Sesame street";
 		parent.address.country = new Country();
 		parent.address.country.name = "Suriname";
 		parent.address.country.languages = Arrays.asList( "english", "spanish" );
 
 		parent.lazyField = new byte[100];
 
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 		s.persist( parent );
 		s.getTransaction().commit();
 		s.close();
 
 		EnhancerTestUtils.checkDirtyTracking( parent );
 		entityId = parent.id;
 	}
 
 	public void execute() {
 		Session s = getFactory().openSession();
 		s.beginTransaction();
 		ParentEntity parent = s.get( ParentEntity.class, entityId );
 		s.getTransaction().commit();
 		s.close();
 
 		EnhancerTestUtils.checkDirtyTracking( parent );
 
 		parent.address.country.name = "Paraguai";
 
 		EnhancerTestUtils.checkDirtyTracking( parent, "address.country" );
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		ParentEntity mergedParent = (ParentEntity) s.merge( parent );
 		EnhancerTestUtils.checkDirtyTracking( parent, "address.country" );
 		EnhancerTestUtils.checkDirtyTracking( mergedParent, "address.country" );
 		s.getTransaction().commit();
 		s.close();
 
 		EnhancerTestUtils.checkDirtyTracking( parent, "address.country" );
 		EnhancerTestUtils.checkDirtyTracking( mergedParent );
 
 		mergedParent.address.country.name = "Honduras";
 
 		EnhancerTestUtils.checkDirtyTracking( mergedParent, "address.country" );
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		s.saveOrUpdate( mergedParent );
 		EnhancerTestUtils.checkDirtyTracking( mergedParent, "address.country" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = getFactory().openSession();
 		s.beginTransaction();
 		parent = s.get( ParentEntity.class, entityId );
 		s.getTransaction().commit();
 		s.close();
 
 		Assert.assertEquals( "Honduras", parent.address.country.name );
 	}
 
 	protected void cleanup() {
 	}
 
 	@Entity
 	@Table(name = "parent_entity")
 	private static class ParentEntity {
 
 		@Id
 		@GeneratedValue
 		private long id;
 
 		private String description;
 
 		@Embedded
 		private Address address;
 
 		@Basic(fetch = FetchType.LAZY)
 		private byte[] lazyField;
 
 	}
 
 	@Embeddable
 	@Table(name = "address")
 	private static class Address {
 
 		private String street;
 
 		@Embedded
 		private Country country;
 
 	}
 
 	@Embeddable
 	@Table(name = "country")
 	private static class Country {
 
 		private String name;
 
 		@ElementCollection
 		@CollectionTable(name = "languages", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
 		List<String> languages;
 
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 4d9c695250..94ca492e92 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,846 +1,846 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
-import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
+import org.hibernate.tuple.entity.BytecodeEnhancementMetadataNonPojoImpl;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(
 				final PersistentClass persistentClass,
 				final EntityRegionAccessStrategy cacheAccessStrategy,
 				final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 				final PersisterCreationContext creationContext) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
-		public EntityInstrumentationMetadata getInstrumentationMetadata() {
-			return new NonPojoInstrumentationMetadata( null );
+		public BytecodeEnhancementMetadata getInstrumentationMetadata() {
+			return new BytecodeEnhancementMetadataNonPojoImpl( null );
 		}
 
 		@Override
 		public void generateEntityDefinition() {
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public EntityEntryFactory getEntityEntryFactory() {
 			return MutableEntityEntryFactory.INSTANCE;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			throw new NotYetImplementedException();
 		}
 
         @Override
         public int[] resolveAttributeIndexes(String[] attributeNames) {
             return null;
         }
 
 		@Override
 		public boolean canUseReferenceCacheEntries() {
 			return false;
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(
 				Collection collectionBinding,
 				CollectionRegionAccessStrategy cacheAccessStrategy,
 				PersisterCreationContext creationContext) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public CollectionPersister getCollectionPersister() {
 			return this;
 		}
 
 		public CollectionType getCollectionType() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionIndexDefinition getIndexDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionElementDefinition getElementDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
 
 		@Override
 		public String getMappedByProperty() {
 			return null;
 		}
 
 		@Override
 		public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java
deleted file mode 100755
index accfd14d2f..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java
+++ /dev/null
@@ -1,113 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.buildtime;
-
-import org.junit.Test;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
-import org.hibernate.test.instrument.cases.Executable;
-import org.hibernate.test.instrument.cases.TestCustomColumnReadAndWrite;
-import org.hibernate.test.instrument.cases.TestDirtyCheckExecutable;
-import org.hibernate.test.instrument.cases.TestFetchAllExecutable;
-import org.hibernate.test.instrument.cases.TestInjectFieldInterceptorExecutable;
-import org.hibernate.test.instrument.cases.TestIsPropertyInitializedExecutable;
-import org.hibernate.test.instrument.cases.TestLazyBasicFieldAccessExecutable;
-import org.hibernate.test.instrument.cases.TestLazyBasicPropertyAccessExecutable;
-import org.hibernate.test.instrument.cases.TestLazyExecutable;
-import org.hibernate.test.instrument.cases.TestLazyManyToOneExecutable;
-import org.hibernate.test.instrument.cases.TestLazyPropertyCustomTypeExecutable;
-import org.hibernate.test.instrument.cases.TestManyToOneProxyExecutable;
-import org.hibernate.test.instrument.cases.TestSharedPKOneToOneExecutable;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.testing.Skip;
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-/**
- * @author Gavin King
- */
-@Skip(
-		message = "domain classes not instrumented for build-time instrumentation testing",
-		condition = InstrumentTest.SkipCheck.class
-)
-public class InstrumentTest extends BaseUnitTestCase {
-	@Test
-	public void testDirtyCheck() throws Exception {
-		execute( new TestDirtyCheckExecutable() );
-	}
-
-	@Test
-	public void testFetchAll() throws Exception {
-		execute( new TestFetchAllExecutable() );
-	}
-
-	@Test
-	public void testLazy() throws Exception {
-		execute( new TestLazyExecutable() );
-	}
-
-	@Test
-	public void testLazyManyToOne() throws Exception {
-		execute( new TestLazyManyToOneExecutable() );
-	}
-
-	@Test
-	public void testSetFieldInterceptor() throws Exception {
-		execute( new TestInjectFieldInterceptorExecutable() );
-	}
-
-	@Test
-	public void testPropertyInitialized() throws Exception {
-		execute( new TestIsPropertyInitializedExecutable() );
-	}
-
-	@Test
-	public void testManyToOneProxy() throws Exception {
-		execute( new TestManyToOneProxyExecutable() );
-	}
-
-	@Test
-	public void testLazyPropertyCustomTypeExecutable() throws Exception {
-		execute( new TestLazyPropertyCustomTypeExecutable() );
-	}
-
-	@Test
-	public void testLazyBasicFieldAccess() throws Exception {
-		execute( new TestLazyBasicFieldAccessExecutable() );
-	}
-
-	@Test
-	public void testLazyBasicPropertyAccess() throws Exception {
-		execute( new TestLazyBasicPropertyAccessExecutable() );
-	}
-
-	@Test
-	public void testSharedPKOneToOne() throws Exception {
-		execute( new TestSharedPKOneToOneExecutable() );
-	}
-
-	@Test
-	public void testCustomColumnReadAndWrite() throws Exception {
-		execute( new TestCustomColumnReadAndWrite() );
-	}	
-	
-	private void execute(Executable executable) throws Exception {
-		executable.prepare();
-		try {
-			executable.execute();
-		}
-		finally {
-			executable.complete();
-		}
-	}
-
-	public static class SkipCheck implements Skip.Matcher {
-		@Override
-		public boolean isMatch() {
-			return ! FieldInterceptionHelper.isInstrumented( new Document() );
-		}
-	}
-}
-
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java
deleted file mode 100644
index b3738284c8..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java
+++ /dev/null
@@ -1,70 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-
-import org.hibernate.SessionFactory;
-import org.hibernate.boot.MetadataSources;
-import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
-import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
-import org.hibernate.bytecode.spi.InstrumentedClassLoader;
-import org.hibernate.cfg.Environment;
-import org.hibernate.service.ServiceRegistry;
-
-/**
- * @author Steve Ebersole
- */
-public abstract class AbstractExecutable implements Executable {
-	private ServiceRegistry serviceRegistry;
-	private SessionFactory factory;
-
-    @Override
-	public final void prepare() {
-		BootstrapServiceRegistryBuilder bsrb = new BootstrapServiceRegistryBuilder();
-		// make sure we pick up the TCCL, and make sure its the isolated CL...
-		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
-		if ( classLoader == null ) {
-			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
-		}
-		if ( !InstrumentedClassLoader.class.isInstance( classLoader ) ) {
-			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
-		}
-		bsrb.applyClassLoader( classLoader );
-
-		serviceRegistry = new StandardServiceRegistryBuilder( bsrb.build() )
-				.applySetting( Environment.HBM2DDL_AUTO, "create-drop" )
-				.build();
-
-		MetadataSources metadataSources = new MetadataSources( serviceRegistry );
-		for ( String resource : getResources() ) {
-			metadataSources.addResource( resource );
-		}
-
-		factory = metadataSources.buildMetadata().buildSessionFactory();
-	}
-
-    @Override
-	public final void complete() {
-		try {
-			cleanup();
-		}
-		finally {
-			factory.close();
-			StandardServiceRegistryBuilder.destroy( serviceRegistry );
-		}
-	}
-
-	protected SessionFactory getFactory() {
-		return factory;
-	}
-
-	protected void cleanup() {
-	}
-
-	protected String[] getResources() {
-		return new String[] { "org/hibernate/test/instrument/domain/Documents.hbm.xml" };
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/Executable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/Executable.java
deleted file mode 100644
index 9e448ee778..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/Executable.java
+++ /dev/null
@@ -1,17 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-
-
-/**
- * @author Steve Ebersole
- */
-public interface Executable {
-	public void prepare();
-	public void execute() throws Exception;
-	public void complete();
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestCustomColumnReadAndWrite.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestCustomColumnReadAndWrite.java
deleted file mode 100644
index f285540229..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestCustomColumnReadAndWrite.java
+++ /dev/null
@@ -1,76 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-
-import static junit.framework.Assert.assertEquals;
-import static junit.framework.Assert.assertFalse;
-import static junit.framework.Assert.assertTrue;
-
-/**
- * @author Rob.Hasselbaum
- */
-public class TestCustomColumnReadAndWrite extends AbstractExecutable {
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		final double SIZE_IN_KB = 20480;
-		final double SIZE_IN_MB = SIZE_IN_KB / 1024d;
-		Owner o = new Owner();
-		Document doc = new Document();
-		Folder fol = new Folder();
-		o.setName("gavin");
-		doc.setName("Hibernate in Action");
-		doc.setSummary("blah");
-		doc.updateText("blah blah");	
-		fol.setName("books");
-		doc.setOwner(o);
-		doc.setFolder(fol);
-		doc.setSizeKb(SIZE_IN_KB);
-		fol.getDocuments().add(doc);
-		s.persist(o);
-		s.persist(fol);
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		
-		// Check value conversion on insert
-		// Value returned by Oracle native query is a Types.NUMERIC, which is mapped to a BigDecimalType;
-		// Cast returned value to Number then call Number.doubleValue() so it works on all dialects.
-		Double sizeViaSql =
-				( (Number)s.createSQLQuery("select size_mb from documents").uniqueResult() )
-						.doubleValue();
-		assertEquals( SIZE_IN_MB, sizeViaSql, 0.01d );
-
-		// Test explicit fetch of all properties
-		doc = (Document) s.createQuery("from Document fetch all properties").uniqueResult();
-		assertTrue( Hibernate.isPropertyInitialized( doc, "sizeKb" ) );
-		assertEquals( SIZE_IN_KB, doc.getSizeKb() );
-		t.commit();
-		s.close();		
-
-		// Test lazy fetch with custom read
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		doc = (Document) s.get( Document.class, doc.getId() );
-		assertFalse( Hibernate.isPropertyInitialized( doc, "sizeKb" ) );
-		assertEquals( SIZE_IN_KB, doc.getSizeKb() );
-		s.delete(doc);
-		s.delete( doc.getOwner() );
-		s.delete( doc.getFolder() );
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestDirtyCheckExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestDirtyCheckExecutable.java
deleted file mode 100644
index ffefb3de49..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestDirtyCheckExecutable.java
+++ /dev/null
@@ -1,54 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-import java.util.Iterator;
-import java.util.List;
-
-import junit.framework.Assert;
-
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Folder;
-
-/**
- * @author Steve Ebersole
- */
-public class TestDirtyCheckExecutable extends AbstractExecutable {
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Folder pics = new Folder();
-		pics.setName("pics");
-		Folder docs = new Folder();
-		docs.setName("docs");
-		s.persist(docs);
-		s.persist(pics);
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		List list = s.createCriteria(Folder.class).list();
-		for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
-			Folder f = (Folder) iter.next();
-			Assert.assertFalse( f.nameWasread );
-		}
-		t.commit();
-		s.close();
-
-		for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
-			Folder f = (Folder) iter.next();
-			Assert.assertFalse( f.nameWasread );
-		}
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		s.createQuery("delete from Folder").executeUpdate();
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestFetchAllExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestFetchAllExecutable.java
deleted file mode 100644
index 095487672d..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestFetchAllExecutable.java
+++ /dev/null
@@ -1,53 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-import junit.framework.Assert;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-
-/**
- * @author Steve Ebersole
- */
-public class TestFetchAllExecutable extends AbstractExecutable {
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Owner o = new Owner();
-		Document doc = new Document();
-		Folder fol = new Folder();
-		o.setName("gavin");
-		doc.setName("Hibernate in Action");
-		doc.setSummary("blah");
-		doc.updateText("blah blah");
-		fol.setName("books");
-		doc.setOwner(o);
-		doc.setFolder(fol);
-		fol.getDocuments().add(doc);
-		s.persist(o);
-		s.persist(fol);
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document fetch all properties").uniqueResult();
-		Assert.assertTrue( Hibernate.isPropertyInitialized( doc, "summary" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( doc, "upperCaseName" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( doc, "owner" ) );
-		Assert.assertEquals( doc.getSummary(), "blah" );
-		s.delete(doc);
-		s.delete( doc.getOwner() );
-		s.delete( doc.getFolder() );
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestFetchingLazyToOneExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestFetchingLazyToOneExecutable.java
deleted file mode 100644
index e76cc1a487..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestFetchingLazyToOneExecutable.java
+++ /dev/null
@@ -1,175 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.SessionFactory;
-import org.hibernate.boot.MetadataSources;
-import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
-import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
-import org.hibernate.bytecode.spi.InstrumentedClassLoader;
-import org.hibernate.cfg.AvailableSettings;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
-import org.hibernate.service.ServiceRegistry;
-
-import org.hibernate.testing.ServiceRegistryBuilder;
-import org.hibernate.test.instrument.domain.Passport;
-import org.hibernate.test.instrument.domain.Person;
-
-import static org.junit.Assert.assertFalse;
-import static org.junit.Assert.assertNotNull;
-import static org.junit.Assert.assertSame;
-import static org.junit.Assert.assertTrue;
-
-/**
- * @author Steve Ebersole
- */
-public class TestFetchingLazyToOneExecutable implements Executable {
-	private ServiceRegistry serviceRegistry;
-	private SessionFactory factory;
-
-	@Override
-	public void execute() throws Exception {
-		doBaselineAssertions();
-
-		doFetchNonMappedBySideAssertions();
-		doFetchMappedBySideAssertions();
-	}
-
-	private void doBaselineAssertions() {
-		{
-			// First, load from the non-owning side by id.  Person#passport should be uninitialized
-			Session s = factory.openSession();
-			s.beginTransaction();
-			Person person = (Person) s.get( Person.class, 1 );
-			assertTrue( Hibernate.isInitialized( person ) );
-			assertFalse( Hibernate.isPropertyInitialized( person, "passport" ) );
-			assertNotNull( person.getPassport() );
-			s.getTransaction().commit();
-			s.close();
-		}
-
-		{
-			// Then, load from the owning side by id.  Passport#person should be uninitialized
-			Session s = factory.openSession();
-			s.beginTransaction();
-			Passport passport = (Passport) s.get( Passport.class, 1 );
-			assertTrue( Hibernate.isInitialized( passport ) );
-			assertFalse( Hibernate.isPropertyInitialized( passport, "person" ) );
-			assertNotNull( passport.getPerson() );
-			s.getTransaction().commit();
-			s.close();
-		}
-	}
-
-	private void doFetchNonMappedBySideAssertions() {
-		// try to eagerly fetch the association from the owning (non-mappedBy) side
-		Session s = factory.openSession();
-		s.beginTransaction();
-// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-// the whole question here is design, and whether the `fetch all properties` should be needed
-//		Passport p = (Passport) s.createQuery( "select p from Passport p join fetch p.person" ).uniqueResult();
-// versus:
-// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		Passport p = (Passport) s.createQuery( "select p from Passport p fetch all properties join fetch p.person" ).uniqueResult();
-		assertTrue( Hibernate.isInitialized( p ) );
-		assertTrue(
-				"Assertion that the eager fetch of non-mappedBy association (Passport#person) was performed properly",
-				Hibernate.isPropertyInitialized( p, "person" )
-		);
-		assertNotNull( p.getPerson() );
-		assertTrue( Hibernate.isInitialized( p.getPerson() ) );
-		assertSame( p, p.getPerson().getPassport() );
-		s.getTransaction().commit();
-		s.close();
-
-	}
-
-	private void doFetchMappedBySideAssertions() {
-		// try to eagerly fetch the association from the non-owning (mappedBy) side
-		Session s = factory.openSession();
-		s.beginTransaction();
-// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-// the whole question here is design, and whether the `fetch all properties` should be needed
-//		Person p  = (Person) s.createQuery( "select p from Person p join fetch p.passport" ).uniqueResult();
-// versus:
-// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		Person p  = (Person) s.createQuery( "select p from Person p fetch all properties join fetch p.passport" ).uniqueResult();
-		assertTrue( Hibernate.isInitialized( p ) );
-		assertTrue(
-				"Assertion that the eager fetch of mappedBy association (Person#passport) was performed properly",
-				Hibernate.isPropertyInitialized( p, "passport" )
-		);
-		assertNotNull( p.getPassport() );
-		assertTrue( Hibernate.isInitialized( p.getPassport() ) );
-		assertSame( p, p.getPassport().getPerson() );
-		s.getTransaction().commit();
-		s.close();
-	}
-
-	@Override
-	public final void prepare() {
-		BootstrapServiceRegistryBuilder bsrb = new BootstrapServiceRegistryBuilder();
-		// make sure we pick up the TCCL, and make sure its the isolated CL...
-		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
-		if ( classLoader == null ) {
-			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
-		}
-		if ( !InstrumentedClassLoader.class.isInstance( classLoader ) ) {
-			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
-		}
-		bsrb.applyClassLoader( classLoader );
-
-		serviceRegistry = new StandardServiceRegistryBuilder( bsrb.build() )
-				.applySetting( Environment.HBM2DDL_AUTO, "create-drop" )
-				.applySetting( AvailableSettings.USE_SECOND_LEVEL_CACHE, "false" )
-				.build();
-
-		MetadataSources metadataSources = new MetadataSources( serviceRegistry );
-		metadataSources.addAnnotatedClass( Person.class );
-		metadataSources.addAnnotatedClass( Passport.class );
-
-		factory = metadataSources.buildMetadata().buildSessionFactory();
-
-		createData();
-	}
-
-	private void createData() {
-		Person steve = new Person( "Steve" );
-		Passport passport = new Passport( steve, "123456789", "Acme Emirates" );
-
-		Session s = factory.openSession();
-		s.beginTransaction();
-		s.save( steve );
-		s.save( passport );
-		s.getTransaction().commit();
-		s.close();
-	}
-
-	@Override
-	public final void complete() {
-		try {
-			cleanupData();
-		}
-		finally {
-			factory.close();
-			StandardServiceRegistryBuilder.destroy( serviceRegistry );
-		}
-	}
-
-	private void cleanupData() {
-		Session s = factory.openSession();
-		s.beginTransaction();
-		s.createQuery( "delete Passport" ).executeUpdate();
-		s.createQuery( "delete Person" ).executeUpdate();
-		s.getTransaction().commit();
-		s.close();
-	}
-
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java
deleted file mode 100644
index aef33c0fd5..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java
+++ /dev/null
@@ -1,24 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-
-import java.util.HashSet;
-
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
-
-import org.hibernate.test.instrument.domain.Document;
-
-/**
- * @author Steve Ebersole
- */
-public class TestInjectFieldInterceptorExecutable extends AbstractExecutable {
-	public void execute() {
-		Document doc = new Document();
-		FieldInterceptionHelper.injectFieldInterceptor( doc, "Document", new HashSet(), null );
-		doc.getId();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestIsPropertyInitializedExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestIsPropertyInitializedExecutable.java
deleted file mode 100644
index 0a8769f9e9..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestIsPropertyInitializedExecutable.java
+++ /dev/null
@@ -1,56 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-//$Id: $
-package org.hibernate.test.instrument.cases;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-import junit.framework.Assert;
-
-/**
- * @author Steve Ebersole
- */
-public class TestIsPropertyInitializedExecutable extends AbstractExecutable {
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Owner o = new Owner();
-		Document doc = new Document();
-		Folder fol = new Folder();
-		o.setName("gavin");
-		doc.setName("Hibernate in Action");
-		doc.setSummary("blah");
-		doc.updateText("blah blah");
-		fol.setName("books");
-		doc.setOwner(o);
-		doc.setFolder(fol);
-		fol.getDocuments().add(doc);
-		Assert.assertTrue( Hibernate.isPropertyInitialized( doc, "summary" ) );
-		s.persist(o);
-		s.persist(fol);
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		doc = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( doc, "summary" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( doc, "upperCaseName" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( doc, "owner" ) );
-		s.delete(doc);
-		s.delete( doc.getOwner() );
-		s.delete( doc.getFolder() );
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyBasicFieldAccessExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyBasicFieldAccessExecutable.java
deleted file mode 100644
index 1bff66a384..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyBasicFieldAccessExecutable.java
+++ /dev/null
@@ -1,128 +0,0 @@
-package org.hibernate.test.instrument.cases;
-import org.junit.Assert;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-
-/**
- * @author Andrei Ivanov
- */
-public class TestLazyBasicFieldAccessExecutable extends AbstractExecutable {
-	protected String[] getResources() {
-		return new String[] {"org/hibernate/test/instrument/domain/Documents.hbm.xml"};
-	}
-
-	public void execute() throws Exception {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Owner o = new Owner();
-		Document doc = new Document();
-		Folder fol = new Folder();
-		o.setName( "gavin" );
-		doc.setName( "Hibernate in Action" );
-		doc.setSummary( "blah" );
-		doc.updateText( "blah blah" );
-		fol.setName( "books" );
-		doc.setOwner( o );
-		doc.setFolder( fol );
-		fol.getDocuments().add( doc );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( doc, "summary" ) );
-		s.persist( o );
-		s.persist( fol );
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// update with lazy property initialized
-		doc.setName( "Doc Name" );
-		doc.setSummary( "u" );
-		s.update( doc );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// merge with lazy property initialized and updated
-		doc.setName( "Doc Name 1" );
-		doc.setSummary( "v" );
-		Document docManaged = (Document) s.merge( doc );
-		try {
-			Assert.assertEquals("v", docManaged.getSummary());
-			Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		docManaged = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// merge with lazy property initialized in doc; uninitialized in docManaged.
-		doc.setSummary( "w" );
-		Assert.assertSame( docManaged, s.merge( doc ) );
-		Assert.assertEquals( "w", docManaged.getSummary() );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		docManaged = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// initialize docManaged.getSummary
-		Assert.assertEquals( "w", docManaged.getSummary() );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// merge with lazy property initialized in both doc and docManaged.
-		doc.setSummary( "x" );
-		Assert.assertSame( docManaged, s.merge( doc ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		Assert.assertEquals( "x", docManaged.getSummary() );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		Document docWithLazySummary = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docWithLazySummary, "summary" ) );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// summary should still be uninitialized.
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docWithLazySummary, "summary" ) );
-		docWithLazySummary.setName( "new name" );
-		// merge the Document with an uninitialized summary
-		docManaged = (Document) s.merge( docWithLazySummary );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		docManaged = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// initialize docManaged.getSummary
-		Assert.assertEquals( "x", docManaged.getSummary() );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// merge the Document with an uninitialized summary
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docWithLazySummary, "summary" ) );
-		docManaged = (Document) s.merge( docWithLazySummary );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		Assert.assertEquals( "x", docManaged.getSummary() );
-		s.getTransaction().commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyBasicPropertyAccessExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyBasicPropertyAccessExecutable.java
deleted file mode 100644
index 668437eff1..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyBasicPropertyAccessExecutable.java
+++ /dev/null
@@ -1,122 +0,0 @@
-package org.hibernate.test.instrument.cases;
-import org.junit.Assert;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-
-/**
- * @author Andrei Ivanov
- */
-public class TestLazyBasicPropertyAccessExecutable extends AbstractExecutable {
-	protected String[] getResources() {
-		return new String[] {"org/hibernate/test/instrument/domain/DocumentsPropAccess.hbm.xml"};
-	}
-
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Owner o = new Owner();
-		Document doc = new Document();
-		Folder fol = new Folder();
-		o.setName( "gavin" );
-		doc.setName( "Hibernate in Action" );
-		doc.setSummary( "blah" );
-		doc.updateText( "blah blah" );
-		fol.setName( "books" );
-		doc.setOwner( o );
-		doc.setFolder( fol );
-		fol.getDocuments().add( doc );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( doc, "summary" ) );
-		s.persist( o );
-		s.persist( fol );
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// update with lazy property initialized
-		doc.setName( "Doc Name" );
-		doc.setSummary( "u" );
-		s.update( doc );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// merge with lazy property initialized and updated
-		doc.setName( "Doc Name 1" );
-		doc.setSummary( "v" );
-		Document docManaged = (Document) s.merge( doc );
-		Assert.assertEquals( "v", docManaged.getSummary() );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		docManaged = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// merge with lazy property initialized in doc; uninitialized in docManaged.
-		doc.setSummary( "w" );
-		Assert.assertSame( docManaged, s.merge( doc ) );
-		Assert.assertEquals( "w", docManaged.getSummary() );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		docManaged = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// initialize docManaged.getSummary
-		Assert.assertEquals( "w", docManaged.getSummary() );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// merge with lazy property initialized in both doc and docManaged.
-		doc.setSummary( "x" );
-		Assert.assertSame( docManaged, s.merge( doc ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		Assert.assertEquals( "x", docManaged.getSummary() );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		Document docWithLazySummary = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docWithLazySummary, "summary" ) );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// summary should still be uninitialized.
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docWithLazySummary, "summary" ) );
-		docWithLazySummary.setName( "new name" );
-		// merge the Document with an uninitialized summary
-		docManaged = (Document) s.merge( docWithLazySummary );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		s.getTransaction().commit();
-		s.close();
-
-		s = getFactory().openSession();
-		s.getTransaction().begin();
-		// get the Document with an uninitialized summary
-		docManaged = (Document) s.get( Document.class, doc.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// initialize docManaged.getSummary
-		Assert.assertEquals( "x", docManaged.getSummary() );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		// merge the Document with an uninitialized summary
-		Assert.assertFalse( Hibernate.isPropertyInitialized( docWithLazySummary, "summary" ) );
-		docManaged = (Document) s.merge( docWithLazySummary );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( docManaged, "summary" ) );
-		Assert.assertEquals( "x", docManaged.getSummary() );
-		s.getTransaction().commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyExecutable.java
deleted file mode 100644
index 216799f52c..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyExecutable.java
+++ /dev/null
@@ -1,213 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-import junit.framework.TestCase;
-
-import org.hibernate.CacheMode;
-import org.hibernate.Hibernate;
-import org.hibernate.LockMode;
-import org.hibernate.Session;
-import org.hibernate.SessionFactory;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-
-/**
- * @author Steve Ebersole
- */
-public class TestLazyExecutable extends AbstractExecutable {
-	public void execute() {
-		// The following block is repeated 100 times to reproduce HHH-2627.
-		// Without the fix, Oracle will run out of cursors using 10g with
-		// a default installation (ORA-01000: maximum open cursors exceeded).
-		// The number of loops may need to be adjusted depending on the how
-		// Oracle is configured.
-		// Note: The block is not indented to avoid a lot of irrelevant differences.
-		for ( int i=0; i<100; i++ ) {
-
-		SessionFactory factory = getFactory();
-		Session s = factory.openSession();
-		Transaction t = s.beginTransaction();
-		Owner o = new Owner();
-		Document doc = new Document();
-		Folder fol = new Folder();
-		o.setName("gavin");
-		doc.setName("Hibernate in Action");
-		doc.setSummary("blah");
-		doc.updateText("blah blah");
-		fol.setName("books");
-		doc.setOwner(o);
-		doc.setFolder(fol);
-		fol.getDocuments().add(doc);
-		s.save(o);
-		s.save(fol);
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = ( Document ) s.get( Document.class, doc.getId() );
-		TestCase.assertTrue( Hibernate.isPropertyInitialized(doc, "weirdProperty"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "name"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "upperCaseName"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "folder"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "owner"));
-		doc.getUpperCaseName();  // should force initialization
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "weirdProperty"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "upperCaseName"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "folder"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "owner"));
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		doc.getName();
-		TestCase.assertEquals( doc.getText(), "blah blah" );
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		doc.getName();
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "summary"));
-		TestCase.assertEquals( doc.getText(), "blah blah" );
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "summary"));
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		doc.setName("HiA");
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		TestCase.assertEquals( doc.getName(), "HiA" );
-		TestCase.assertEquals( doc.getText(), "blah blah" );
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		doc.getText();
-		doc.setName("HiA second edition");
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "weirdProperty"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "name"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "upperCaseName"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "owner"));
-		TestCase.assertEquals( doc.getName(), "HiA second edition" );
-		TestCase.assertEquals( doc.getText(), "blah blah" );
-		TestCase.assertEquals( doc.getUpperCaseName(), "HIA SECOND EDITION" );
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "weirdProperty"));
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "upperCaseName"));
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		t.commit();
-		s.close();
-
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		s.lock(doc, LockMode.NONE);
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertEquals( doc.getText(), "blah blah" );
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "text"));
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		t.commit();
-		s.close();
-
-		doc.setName("HiA2");
-
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		s.saveOrUpdate(doc);
-		s.flush();
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertEquals( doc.getText(), "blah blah" );
-		TestCase.assertTrue(Hibernate.isPropertyInitialized(doc, "text"));
-		doc.updateText("blah blah blah blah");
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = ( Document ) s.createQuery("from Document").uniqueResult();
-		TestCase.assertEquals( doc.getName(), "HiA2" );
-		TestCase.assertEquals( doc.getText(), "blah blah blah blah" );
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.load( Document.class, doc.getId() );
-		doc.getName();
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "text"));
-		TestCase.assertFalse(Hibernate.isPropertyInitialized(doc, "summary"));
-		t.commit();
-		s.close();
-
-		s = factory.openSession();
-		s.setCacheMode( CacheMode.IGNORE );
-		t = s.beginTransaction();
-		doc = (Document) s.createQuery("from Document").uniqueResult();
-		//s.delete(doc);
-		s.delete( doc.getFolder() );
-		s.delete( doc.getOwner() );
-		s.flush();
-		t.commit();
-		s.close();
-
-		}
-
-	}
-
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyManyToOneExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyManyToOneExecutable.java
deleted file mode 100644
index 17ff35cd41..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyManyToOneExecutable.java
+++ /dev/null
@@ -1,79 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-import junit.framework.Assert;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Document;
-import org.hibernate.test.instrument.domain.Folder;
-import org.hibernate.test.instrument.domain.Owner;
-
-/**
- * @author Steve Ebersole
- */
-public class TestLazyManyToOneExecutable extends AbstractExecutable {
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Owner gavin = new Owner();
-		Document hia = new Document();
-		Folder fol = new Folder();
-		gavin.setName("gavin");
-		hia.setName("Hibernate in Action");
-		hia.setSummary("blah");
-		hia.updateText("blah blah");
-		fol.setName("books");
-		hia.setOwner(gavin);
-		hia.setFolder(fol);
-		fol.getDocuments().add(hia);
-		s.persist(gavin);
-		s.persist(fol);
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		hia = (Document) s.createCriteria(Document.class).uniqueResult();
-		Assert.assertEquals( hia.getFolder().getClass(), Folder.class);
-		fol = hia.getFolder();
-		Assert.assertTrue( Hibernate.isInitialized(fol) );
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		hia = (Document) s.createCriteria(Document.class).uniqueResult();
-		Assert.assertSame( hia.getFolder(), s.load(Folder.class, fol.getId()) );
-		Assert.assertTrue( Hibernate.isInitialized( hia.getFolder() ) );
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		fol = (Folder) s.get(Folder.class, fol.getId());
-		hia = (Document) s.createCriteria(Document.class).uniqueResult();
-		Assert.assertSame( fol, hia.getFolder() );
-		fol = hia.getFolder();
-		Assert.assertTrue( Hibernate.isInitialized(fol) );
-		t.commit();
-		s.close();
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		fol = (Folder) s.load(Folder.class, fol.getId());
-		hia = (Document) s.createCriteria(Document.class).uniqueResult();
-		Assert.assertNotSame( fol, hia.getFolder() );
-		fol = hia.getFolder();
-		Assert.assertTrue( Hibernate.isInitialized(fol) );
-		s.delete(hia.getFolder());
-		s.delete(hia.getOwner());
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java
deleted file mode 100644
index c5a66c74d6..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java
+++ /dev/null
@@ -1,98 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-
-import java.util.Iterator;
-
-import org.hibernate.Session;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
-
-import org.hibernate.test.instrument.domain.Problematic;
-import junit.framework.Assert;
-
-/**
- * {@inheritDoc}
- *
- * @author Steve Ebersole
- */
-public class TestLazyPropertyCustomTypeExecutable extends AbstractExecutable {
-
-	protected String[] getResources() {
-		return new String[] { "org/hibernate/test/instrument/domain/Problematic.hbm.xml" };
-	}
-
-	public void execute() throws Exception {
-		Session s = getFactory().openSession();
-		Problematic p = new Problematic();
-		try {
-			s.beginTransaction();
-			p.setName( "whatever" );
-			p.setBytes( new byte[] { 1, 0, 1, 1, 0 } );
-			s.save( p );
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-
-		// this access should be ok because p1 is not a lazy proxy 
-		s = getFactory().openSession();
-		try {
-			s.beginTransaction();
-			Problematic p1 = (Problematic) s.get( Problematic.class, p.getId() );
-			Assert.assertTrue( FieldInterceptionHelper.isInstrumented( p1 ) );
-			p1.getRepresentation();
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-		
-		s = getFactory().openSession();
-		try {
-			s.beginTransaction();
-			Problematic p1 = (Problematic) s.createQuery( "from Problematic" ).setReadOnly(true ).list().get( 0 );
-			p1.getRepresentation();
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-		
-		s = getFactory().openSession();
-		try {
-			s.beginTransaction();
-			Problematic p1 = (Problematic) s.load( Problematic.class, p.getId() );
-			Assert.assertFalse( FieldInterceptionHelper.isInstrumented( p1 ) );
-			p1.setRepresentation( p.getRepresentation() );
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-	}
-
-	protected void cleanup() {
-		Session s = getFactory().openSession();
-		s.beginTransaction();
-		Iterator itr = s.createQuery( "from Problematic" ).list().iterator();
-		while ( itr.hasNext() ) {
-			Problematic p = (Problematic) itr.next();
-			s.delete( p );
-		}
-		s.getTransaction().commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestManyToOneProxyExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestManyToOneProxyExecutable.java
deleted file mode 100644
index f05b725369..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestManyToOneProxyExecutable.java
+++ /dev/null
@@ -1,73 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-import junit.framework.Assert;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.Entity;
-
-/**
- *
- * @author Steve Ebersole
- */
-public class TestManyToOneProxyExecutable extends AbstractExecutable {
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		Entity root = new Entity( "root" );
-		Entity child1 = new Entity( "child1" );
-		Entity child2 = new Entity( "child2" );
-		root.setChild( child1 );
-		child1.setSibling( child2 );
-		Entity gChild1 = new Entity( "grandchild 1" );
-		Entity gChild2 = new Entity( "grandchild 2" );
-		child1.setChild( gChild1 );
-		gChild1.setSibling( gChild2 );
-		s.save( root );
-		t.commit();
-		s.close();
-
-		// NOTE : child is mapped with lazy="proxy"; sibling with lazy="no-proxy"...
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		// load root
-		root = ( Entity ) s.get( Entity.class, root.getId() );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "sibling" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( root, "child" ) );
-
-		// get a handle to the child1 proxy reference (and make certain that
-		// this does not force the lazy properties of the root entity
-		// to get initialized.
-		child1 = root.getChild();
-		Assert.assertFalse( Hibernate.isInitialized( child1 ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "sibling" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( child1, "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( child1, "sibling" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( child1, "child" ) );
-
-		child1.getName();
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "sibling" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( child1, "name" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( child1, "sibling" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( child1, "child" ) );
-
-		gChild1 = child1.getChild();
-		Assert.assertFalse( Hibernate.isInitialized( gChild1 ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "sibling" ) );
-
-		s.delete( root );
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestSharedPKOneToOneExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestSharedPKOneToOneExecutable.java
deleted file mode 100644
index 407b49e8c9..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestSharedPKOneToOneExecutable.java
+++ /dev/null
@@ -1,75 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.cases;
-import junit.framework.Assert;
-
-import org.hibernate.Hibernate;
-import org.hibernate.Session;
-import org.hibernate.Transaction;
-import org.hibernate.test.instrument.domain.EntityWithOneToOnes;
-import org.hibernate.test.instrument.domain.OneToOneNoProxy;
-import org.hibernate.test.instrument.domain.OneToOneProxy;
-
-/**
- *
- * @author Gail Badner
- */
-public class TestSharedPKOneToOneExecutable extends AbstractExecutable {
-
-	protected String[] getResources() {
-		return new String[] {"org/hibernate/test/instrument/domain/SharedPKOneToOne.hbm.xml"};
-	}
-
-	public void execute() {
-		Session s = getFactory().openSession();
-		Transaction t = s.beginTransaction();
-		EntityWithOneToOnes root = new EntityWithOneToOnes( "root" );
-		OneToOneProxy oneToOneProxy = new OneToOneProxy( "oneToOneProxy" );
-		root.setOneToOneProxy( oneToOneProxy );
-		oneToOneProxy.setEntity( root );
-		OneToOneNoProxy oneToOneNoProxy = new OneToOneNoProxy( "oneToOneNoProxy" );
-		root.setOneToOneNoProxy( oneToOneNoProxy );
-		oneToOneNoProxy.setEntity( root );
-
-		s.save( root );
-		t.commit();
-		s.close();
-
-		// NOTE : oneToOneProxy is mapped with lazy="proxy"; oneToOneNoProxy with lazy="no-proxy"...
-
-		s = getFactory().openSession();
-		t = s.beginTransaction();
-		// load root
-		root = ( EntityWithOneToOnes ) s.load( EntityWithOneToOnes.class, root.getId() );
-		Assert.assertFalse( Hibernate.isInitialized( root ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "oneToOneProxy" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "oneToOneNoProxy" ) );
-
-		root.getName();
-		Assert.assertTrue( Hibernate.isInitialized( root ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( root, "name" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( root, "oneToOneProxy" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "oneToOneNoProxy" ) );
-
-		// get a handle to the oneToOneProxy proxy reference (and make certain that
-		// this does not force the lazy properties of the root entity
-		// to get initialized.
-		root.getOneToOneProxy();
-		Assert.assertTrue( Hibernate.isInitialized( oneToOneProxy ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( root.getOneToOneProxy(), "name" ) );
-		Assert.assertFalse( Hibernate.isPropertyInitialized( root, "oneToOneNoProxy" ) );
-
-		root.getOneToOneNoProxy();
-		Assert.assertTrue( Hibernate.isPropertyInitialized( root, "oneToOneNoProxy" ) );
-		Assert.assertTrue( Hibernate.isPropertyInitialized( root.getOneToOneNoProxy(), "name") );
-
-		s.delete( root );
-		t.commit();
-		s.close();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
deleted file mode 100644
index eeca21f232..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
+++ /dev/null
@@ -1,116 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.Arrays;
-
-import org.hibernate.HibernateException;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.type.StandardBasicTypes;
-import org.hibernate.usertype.UserType;
-
-/**
- * A simple byte[]-based custom type.
- */
-public class CustomBlobType implements UserType {
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object nullSafeGet(ResultSet rs, String names[], SessionImplementor session, Object owner) throws SQLException {
-		// cast just to make sure...
-		return StandardBasicTypes.BINARY.nullSafeGet( rs, names[0], session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void nullSafeSet(PreparedStatement ps, Object value, int index, SessionImplementor session) throws SQLException, HibernateException {
-		// cast just to make sure...
-		StandardBasicTypes.BINARY.nullSafeSet( ps, value, index, session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object deepCopy(Object value) {
-		byte result[] = null;
-
-		if ( value != null ) {
-			byte bytes[] = ( byte[] ) value;
-
-			result = new byte[bytes.length];
-			System.arraycopy( bytes, 0, result, 0, bytes.length );
-		}
-
-		return result;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isMutable() {
-		return true;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int[] sqlTypes() {
-		return new int[] { Types.VARBINARY };
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class returnedClass() {
-		return byte[].class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean equals(Object x, Object y) {
-		return Arrays.equals( ( byte[] ) x, ( byte[] ) y );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object assemble(Serializable arg0, Object arg1)
-			throws HibernateException {
-		return null;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Serializable disassemble(Object arg0)
-			throws HibernateException {
-		return null;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int hashCode(Object arg0)
-			throws HibernateException {
-		return 0;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object replace(Object arg0, Object arg1, Object arg2)
-			throws HibernateException {
-		return null;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Document.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Document.java
deleted file mode 100755
index e2183b6300..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Document.java
+++ /dev/null
@@ -1,129 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-//$Id: Document.java 9538 2006-03-04 00:17:57Z steve.ebersole@jboss.com $
-package org.hibernate.test.instrument.domain;
-import java.util.Date;
-
-/**
- * @author Gavin King
- */
-public class Document {
-	private Long id;
-	private String name;
-	private String upperCaseName;
-	private String summary;
-	private String text;
-	private Owner owner;
-	private Folder folder;
-	private double sizeKb;
-	private Date lastTextModification = new Date();
-	/**
-	 * @return Returns the folder.
-	 */
-	public Folder getFolder() {
-		return folder;
-	}
-	/**
-	 * @param folder The folder to set.
-	 */
-	public void setFolder(Folder folder) {
-		this.folder = folder;
-	}
-	/**
-	 * @return Returns the owner.
-	 */
-	public Owner getOwner() {
-		return owner;
-	}
-	/**
-	 * @param owner The owner to set.
-	 */
-	public void setOwner(Owner owner) {
-		this.owner = owner;
-	}
-	/**
-	 * @return Returns the id.
-	 */
-	public Long getId() {
-		return id;
-	}
-	/**
-	 * @param id The id to set.
-	 */
-	public void setId(Long id) {
-		this.id = id;
-	}
-	/**
-	 * @return Returns the name.
-	 */
-	public String getName() {
-		return name;
-	}
-	/**
-	 * @param name The name to set.
-	 */
-	public void setName(String name) {
-		this.name = name;
-	}
-	/**
-	 * @return Returns the summary.
-	 */
-	public String getSummary() {
-		return summary;
-	}
-	/**
-	 * @param summary The summary to set.
-	 */
-	public void setSummary(String summary) {
-		this.summary = summary;
-	}
-	/**
-	 * @return Returns the text.
-	 */
-	public String getText() {
-		return text;
-	}
-	/**
-	 * @param text The text to set.
-	 */
-	private void setText(String text) {
-		this.text = text;
-	}
-	/**
-	 * @return Returns the upperCaseName.
-	 */
-	public String getUpperCaseName() {
-		return upperCaseName;
-	}
-	/**
-	 * @param upperCaseName The upperCaseName to set.
-	 */
-	public void setUpperCaseName(String upperCaseName) {
-		this.upperCaseName = upperCaseName;
-	}
-	/**
-	 * @param sizeKb The size in KBs.
-	 */
-	public void setSizeKb(double sizeKb) {
-		this.sizeKb = sizeKb;
-	}
-	/**
-	 * @return The size in KBs.
-	 */
-	public double getSizeKb() {
-		return sizeKb;
-	}	
-	
-	public void updateText(String newText) {
-		if ( !newText.equals(text) ) {
-			this.text = newText;
-			lastTextModification = new Date();
-		}
-	}
-	
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml
deleted file mode 100755
index b730f9dda0..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Documents.hbm.xml
+++ /dev/null
@@ -1,78 +0,0 @@
-<?xml version="1.0"?>
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
-  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
-  -->
-<!DOCTYPE hibernate-mapping PUBLIC
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<!--
-
-  This mapping demonstrates
-
-     (1) use of lazy properties - this feature requires buildtime
-         bytecode instrumentation; we don't think this is a very
-         necessary feature, but provide it for completeleness; if
-         Hibernate encounters uninstrumented classes, lazy property
-         fetching will be silently disabled, to enable testing
-
-     (2) use of a formula to define a "derived property"
-
--->
-
-<hibernate-mapping package="org.hibernate.test.instrument.domain" default-access="field">
-
-    <class name="Folder" table="folders">
-    	<id name="id">
-    		<generator class="increment"/>
-    	</id>
-    	<property name="name" not-null="true" length="50"/>
-    	<many-to-one name="parent"/>
-    	<bag name="subfolders" inverse="true" cascade="save-update">
-    		<key column="parent"/>
-    		<one-to-many class="Folder"/>
-    	</bag>
-    	<bag name="documents" inverse="true" cascade="all-delete-orphan">
-    		<key column="folder"/>
-    		<one-to-many class="Document"/>
-    	</bag>
-	</class>
-
-	<class name="Owner" table="owners" lazy="false">
-   		<id name="id">
-    		<generator class="increment"/>
-    	</id>
-    	<property name="name" not-null="true" length="50"/>
-    </class>
-
-	<class name="Document" table="documents">
-   		<id name="id">
-    		<generator class="increment"/>
-    	</id>
-    	<property name="name" not-null="true" length="50"/>
-    	<property name="upperCaseName" formula="upper(name)" lazy="true"/>
-    	<property name="summary" column="`summary`" not-null="true" length="200" lazy="true"/>
-    	<many-to-one name="folder" not-null="true" lazy="no-proxy"/>
-    	<many-to-one name="owner" not-null="true" lazy="no-proxy" fetch="select"/>
-    	<property name="text" not-null="true" length="2000" lazy="true"/>
-    	<property name="lastTextModification" not-null="true" lazy="true" access="field"/>
-    	<property name="sizeKb" lazy="true">
-    		<column name="size_mb"
-    			read="size_mb * 1024.0"
-    			write="? / cast( 1024.0 as float )"/>
-    	</property>
-    </class>
-
-    <class name="Entity" table="entity">
-        <id name="id" column="ID" type="long">
-            <generator class="increment"/>
-        </id>
-        <property name="name" column="NAME" type="string" lazy="true"/>
-        <many-to-one name="child" column="PRNT_ID" class="Entity" lazy="proxy" cascade="all" />
-        <many-to-one name="sibling" column="RIGHT_ID" class="Entity" lazy="no-proxy" cascade="all" />
-    </class>
-
-</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/DocumentsPropAccess.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/DocumentsPropAccess.hbm.xml
deleted file mode 100755
index 33e1227c3f..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/DocumentsPropAccess.hbm.xml
+++ /dev/null
@@ -1,78 +0,0 @@
-<?xml version="1.0"?>
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
-  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
-  -->
-<!DOCTYPE hibernate-mapping PUBLIC
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<!--
-
-  This mapping demonstrates
-
-     (1) use of lazy properties - this feature requires buildtime
-         bytecode instrumentation; we don't think this is a very
-         necessary feature, but provide it for completeleness; if
-         Hibernate encounters uninstrumented classes, lazy property
-         fetching will be silently disabled, to enable testing
-
-     (2) use of a formula to define a "derived property"
-
--->
-
-<hibernate-mapping package="org.hibernate.test.instrument.domain" default-access="property">
-
-    <class name="Folder" table="folders">
-    	<id name="id">
-    		<generator class="increment"/>
-    	</id>
-    	<property name="name" not-null="true" length="50"/>
-    	<many-to-one name="parent"/>
-    	<bag name="subfolders" inverse="true" cascade="save-update">
-    		<key column="parent"/>
-    		<one-to-many class="Folder"/>
-    	</bag>
-    	<bag name="documents" inverse="true" cascade="all-delete-orphan">
-    		<key column="folder"/>
-    		<one-to-many class="Document"/>
-    	</bag>
-	</class>
-
-	<class name="Owner" table="owners" lazy="false">
-   		<id name="id">
-    		<generator class="increment"/>
-    	</id>
-    	<property name="name" not-null="true" length="50"/>
-    </class>
-
-	<class name="Document" table="documents">
-   		<id name="id">
-    		<generator class="increment"/>
-    	</id>
-    	<property name="name" not-null="true" length="50"/>
-    	<property name="upperCaseName" formula="upper(name)" lazy="true"/>
-    	<property name="summary" column="`summary`" not-null="true" length="200" lazy="true"/>
-    	<many-to-one name="folder" not-null="true" lazy="no-proxy"/>
-    	<many-to-one name="owner" not-null="true" lazy="no-proxy" fetch="select"/>
-    	<property name="text" not-null="true" length="2000" lazy="true"/>
-    	<property name="lastTextModification" not-null="true" lazy="true" access="field"/>
-    	<property name="sizeKb" lazy="true">
-    		<column name="size_mb"
-    			read="size_mb * 1024.0"
-    			write="? / cast( 1024.0 as float )"/>
-    	</property>
-    </class>
-
-    <class name="Entity" table="entity">
-        <id name="id" column="ID" type="long">
-            <generator class="increment"/>
-        </id>
-        <property name="name" column="NAME" type="string" lazy="true"/>
-        <many-to-one name="child" column="PRNT_ID" class="Entity" lazy="proxy" cascade="all" />
-        <many-to-one name="sibling" column="RIGHT_ID" class="Entity" lazy="no-proxy" cascade="all" />
-    </class>
-
-</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Entity.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Entity.java
deleted file mode 100644
index d2b9a272b0..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Entity.java
+++ /dev/null
@@ -1,59 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-
-/**
- * todo: describe Entity
- *
- * @author Steve Ebersole
- */
-public class Entity {
-	private Long id;
-	private String name;
-	private Entity child;
-	private Entity sibling;
-
-	public Entity() {
-	}
-
-	public Entity(String name) {
-		this.name = name;
-	}
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public void setName(String name) {
-		this.name = name;
-	}
-
-	public Entity getChild() {
-		return child;
-	}
-
-	public void setChild(Entity child) {
-		this.child = child;
-	}
-
-	public Entity getSibling() {
-		return sibling;
-	}
-
-	public void setSibling(Entity sibling) {
-		this.sibling = sibling;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/EntityWithOneToOnes.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/EntityWithOneToOnes.java
deleted file mode 100644
index 4b75952c6d..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/EntityWithOneToOnes.java
+++ /dev/null
@@ -1,57 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-
-/**
- * @author Gail Badner
- */
-public class EntityWithOneToOnes {
-	private Long id;
-	private String name;
-	private OneToOneNoProxy oneToOneNoProxy;
-	private OneToOneProxy oneToOneProxy;
-
-	public EntityWithOneToOnes() {
-	}
-
-	public EntityWithOneToOnes(String name) {
-		this.name = name;
-	}
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public void setName(String name) {
-		this.name = name;
-	}
-
-	public OneToOneNoProxy getOneToOneNoProxy() {
-		return oneToOneNoProxy;
-	}
-
-	public void setOneToOneNoProxy(OneToOneNoProxy oneToOneNoProxy) {
-		this.oneToOneNoProxy = oneToOneNoProxy;
-	}
-
-	public OneToOneProxy getOneToOneProxy() {
-		return oneToOneProxy;
-	}
-
-	public void setOneToOneProxy(OneToOneProxy oneToOneProxy) {
-		this.oneToOneProxy = oneToOneProxy;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Folder.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Folder.java
deleted file mode 100755
index b2b306855b..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Folder.java
+++ /dev/null
@@ -1,86 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-//$Id: Folder.java 9538 2006-03-04 00:17:57Z steve.ebersole@jboss.com $
-package org.hibernate.test.instrument.domain;
-import java.util.ArrayList;
-import java.util.Collection;
-
-/**
- * @author Gavin King
- */
-public class Folder {
-	private Long id;
-	private String name;
-	private Folder parent;
-	private Collection subfolders = new ArrayList();
-	private Collection documents = new ArrayList();
-
-	public boolean nameWasread;
-	
-	/**
-	 * @return Returns the id.
-	 */
-	public Long getId() {
-		return id;
-	}
-	/**
-	 * @param id The id to set.
-	 */
-	public void setId(Long id) {
-		this.id = id;
-	}
-	/**
-	 * @return Returns the name.
-	 */
-	public String getName() {
-		nameWasread = true;
-		return name;
-	}
-	/**
-	 * @param name The name to set.
-	 */
-	public void setName(String name) {
-		this.name = name;
-	}
-	/**
-	 * @return Returns the documents.
-	 */
-	public Collection getDocuments() {
-		return documents;
-	}
-	/**
-	 * @param documents The documents to set.
-	 */
-	public void setDocuments(Collection documents) {
-		this.documents = documents;
-	}
-	/**
-	 * @return Returns the parent.
-	 */
-	public Folder getParent() {
-		return parent;
-	}
-	/**
-	 * @param parent The parent to set.
-	 */
-	public void setParent(Folder parent) {
-		this.parent = parent;
-	}
-	/**
-	 * @return Returns the subfolders.
-	 */
-	public Collection getSubfolders() {
-		return subfolders;
-	}
-	/**
-	 * @param subfolders The subfolders to set.
-	 */
-	public void setSubfolders(Collection subfolders) {
-		this.subfolders = subfolders;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/OneToOneNoProxy.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/OneToOneNoProxy.java
deleted file mode 100644
index ac7370ea3f..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/OneToOneNoProxy.java
+++ /dev/null
@@ -1,52 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-
-/**
- * @author Gail Badner
- */
-public class OneToOneNoProxy {
-	private Long entityId;
-	private String name;
-	private EntityWithOneToOnes entity;
-
-	public OneToOneNoProxy() {}
-	public OneToOneNoProxy(String name) {
-		this.name = name;
-	}
-	/**
-	 * @return Returns the id.
-	 */
-	public Long getEntityId() {
-		return entityId;
-	}
-	/**
-	 * @param entityId The id to set.
-	 */
-	public void setEntityId(Long entityId) {
-		this.entityId = entityId;
-	}
-	/**
-	 * @return Returns the name.
-	 */
-	public String getName() {
-		return name;
-	}
-	/**
-	 * @param name The name to set.
-	 */
-	public void setName(String name) {
-		this.name = name;
-	}
-	public EntityWithOneToOnes getEntity() {
-		return entity;
-	}
-	public void setEntity(EntityWithOneToOnes entity) {
-		this.entity = entity;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/OneToOneProxy.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/OneToOneProxy.java
deleted file mode 100644
index 1ca58ebaeb..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/OneToOneProxy.java
+++ /dev/null
@@ -1,52 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-
-/**
- * @author Gail Badner
- */
-public class OneToOneProxy {
-	private Long entityId;
-	private String name;
-	private EntityWithOneToOnes entity;
-
-	public OneToOneProxy() {}
-	public OneToOneProxy(String name) {
-		this.name = name;
-	}
-	/**
-	 * @return Returns the id.
-	 */
-	public Long getEntityId() {
-		return entityId;
-	}
-	/**
-	 * @param entityId The id to set.
-	 */
-	public void setEntityId(Long entityId) {
-		this.entityId = entityId;
-	}
-	/**
-	 * @return Returns the name.
-	 */
-	public String getName() {
-		return name;
-	}
-	/**
-	 * @param name The name to set.
-	 */
-	public void setName(String name) {
-		this.name = name;
-	}
-	public EntityWithOneToOnes getEntity() {
-		return entity;
-	}
-	public void setEntity(EntityWithOneToOnes entity) {
-		this.entity = entity;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Owner.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Owner.java
deleted file mode 100755
index 1deabfb655..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Owner.java
+++ /dev/null
@@ -1,42 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-
-//$Id: Owner.java 9538 2006-03-04 00:17:57Z steve.ebersole@jboss.com $
-package org.hibernate.test.instrument.domain;
-
-
-/**
- * @author Gavin King
- */
-public class Owner {
-	private Long id;
-	private String name;
-	/**
-	 * @return Returns the id.
-	 */
-	public Long getId() {
-		return id;
-	}
-	/**
-	 * @param id The id to set.
-	 */
-	public void setId(Long id) {
-		this.id = id;
-	}
-	/**
-	 * @return Returns the name.
-	 */
-	public String getName() {
-		return name;
-	}
-	/**
-	 * @param name The name to set.
-	 */
-	public void setName(String name) {
-		this.name = name;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Passport.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Passport.java
deleted file mode 100644
index 438946f7d3..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Passport.java
+++ /dev/null
@@ -1,106 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-import java.util.Calendar;
-import java.util.Date;
-import java.util.GregorianCalendar;
-import javax.persistence.CascadeType;
-import javax.persistence.FetchType;
-import javax.persistence.GeneratedValue;
-import javax.persistence.Id;
-import javax.persistence.JoinColumn;
-import javax.persistence.OneToOne;
-
-import org.hibernate.annotations.GenericGenerator;
-import org.hibernate.annotations.LazyToOne;
-import org.hibernate.annotations.LazyToOneOption;
-
-/**
- * @author Steve Ebersole
- */
-@javax.persistence.Entity
-public class Passport {
-	private Integer id;
-	private Person person;
-	private String number;
-	private String issuingCountry;
-	private Date issueDate;
-	private Date expirationDate;
-
-	public Passport() {
-	}
-
-	public Passport(Person person, String number, String issuingCountry) {
-		this.person = person;
-		this.number = number;
-		this.issuingCountry = issuingCountry;
-
-		this.issueDate = new Date();
-
-		final GregorianCalendar calendar = new GregorianCalendar();
-		calendar.setTime( issueDate );
-		calendar.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) + 10 );
-		this.expirationDate = calendar.getTime();
-
-		this.person.setPassport( this );
-	}
-
-	@Id
-	@GeneratedValue(generator="increment")
-	@GenericGenerator(name="increment", strategy = "increment")
-	public Integer getId() {
-		return id;
-	}
-
-	public void setId(Integer id) {
-		this.id = id;
-	}
-
-	@OneToOne(fetch= FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.PERSIST})
-	@LazyToOne(value = LazyToOneOption.NO_PROXY)
-	@JoinColumn(name="person_id")
-	public Person getPerson() {
-		return person;
-	}
-
-	public void setPerson(Person person) {
-		this.person = person;
-	}
-
-	public String getNumber() {
-		return number;
-	}
-
-	public void setNumber(String number) {
-		this.number = number;
-	}
-
-	public String getIssuingCountry() {
-		return issuingCountry;
-	}
-
-	public void setIssuingCountry(String issuingCountry) {
-		this.issuingCountry = issuingCountry;
-	}
-
-	public Date getIssueDate() {
-		return issueDate;
-	}
-
-	public void setIssueDate(Date issueDate) {
-		this.issueDate = issueDate;
-	}
-
-	public Date getExpirationDate() {
-		return expirationDate;
-	}
-
-	public void setExpirationDate(Date expirationDate) {
-		this.expirationDate = expirationDate;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Person.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Person.java
deleted file mode 100644
index 438335ff68..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Person.java
+++ /dev/null
@@ -1,63 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-import javax.persistence.CascadeType;
-import javax.persistence.FetchType;
-import javax.persistence.GeneratedValue;
-import javax.persistence.Id;
-import javax.persistence.OneToOne;
-
-import org.hibernate.annotations.GenericGenerator;
-import org.hibernate.annotations.LazyToOne;
-import org.hibernate.annotations.LazyToOneOption;
-
-/**
- * @author Steve Ebersole
- */
-@javax.persistence.Entity
-public class Person {
-	private Integer id;
-	private String name;
-	private Passport passport;
-
-	public Person() {
-	}
-
-	public Person(String name) {
-		this.name = name;
-	}
-
-	@Id
-	@GeneratedValue(generator="increment")
-	@GenericGenerator(name="increment", strategy = "increment")
-	public Integer getId() {
-		return id;
-	}
-
-	public void setId(Integer id) {
-		this.id = id;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public void setName(String name) {
-		this.name = name;
-	}
-
-	@OneToOne(fetch= FetchType.LAZY, mappedBy="person",cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
-	@LazyToOne(value = LazyToOneOption.NO_PROXY)
-	public Passport getPassport() {
-		return passport;
-	}
-
-	public void setPassport(Passport passport) {
-		this.passport = passport;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Problematic.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Problematic.hbm.xml
deleted file mode 100644
index 05a1ab11f9..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Problematic.hbm.xml
+++ /dev/null
@@ -1,20 +0,0 @@
-<?xml version="1.0"?>
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
-  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
-  -->
-<!DOCTYPE hibernate-mapping PUBLIC
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<hibernate-mapping package="org.hibernate.test.instrument.domain">
-    <class name="Problematic">
-        <id name="id" type="long" column="ID">
-            <generator class="increment" />
-        </id>
-        <property name="name" type="string" column="NAME" />
-        <property name="bytes" type="org.hibernate.test.instrument.domain.CustomBlobType" column="DATA" lazy="true" />
-    </class>
-</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Problematic.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Problematic.java
deleted file mode 100644
index d5e18923a9..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/Problematic.java
+++ /dev/null
@@ -1,76 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.domain;
-
-
-/**
- * {@inheritDoc}
- *
- * @author Steve Ebersole
- */
-public class Problematic {
-	private Long id;
-	private String name;
-	private byte[] bytes;
-
-	private Representation representation;
-
-	public Long getId() {
-		return id;
-	}
-
-	public void setId(Long id) {
-		this.id = id;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public void setName(String name) {
-		this.name = name;
-	}
-
-	public byte[] getBytes() {
-		return bytes;
-	}
-
-	public void setBytes(byte[] bytes) {
-		this.bytes = bytes;
-	}
-
-	public Representation getRepresentation() {
-		if ( representation == null ) {
-			representation =  ( ( bytes == null ) ? null : new Representation( bytes ) );
-		}
-		return representation;
-	}
-
-	public void setRepresentation(Representation rep) {
-		bytes = rep.getBytes();
-	}
-
-	public static class Representation {
-		private byte[] bytes;
-
-		public Representation(byte[] bytes) {
-			this.bytes = bytes;
-		}
-
-		public byte[] getBytes() {
-			return bytes;
-		}
-
-		public String toString() {
-			String result = "";
-			for ( int i = 0; i < bytes.length; i++ ) {
-				result += bytes[i];
-			}
-			return result;
-		}
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/SharedPKOneToOne.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/SharedPKOneToOne.hbm.xml
deleted file mode 100644
index 5e73f6fe2a..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/SharedPKOneToOne.hbm.xml
+++ /dev/null
@@ -1,57 +0,0 @@
-<?xml version="1.0"?>
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
-  ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
-  -->
-<!DOCTYPE hibernate-mapping PUBLIC
-	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
-	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
-
-<!--
-
-  This mapping demonstrates shared PK one-to-one associations using
-  lazy="proxy" and lazy="no-proxy".
-
-  Implementation note: This test does not include any other
-  lazy properties, and allows testing special case code in
-  AbstractEntityPersister.initializeLazyPropertiesFromDatastore()
-  (lazy select string will be null) and OneToOne.nullSafeGet()
-  (ResultSet arg is ignored and the owner's ID is returned).
-
--->
-
-<hibernate-mapping package="org.hibernate.test.instrument.domain" default-access="field">
-
-    <class name="EntityWithOneToOnes">
-        <id name="id" column="ID" type="long">
-            <generator class="increment"/>
-        </id>
-        <one-to-one name="oneToOneNoProxy" class="OneToOneNoProxy" lazy="no-proxy" cascade="all" />
-        <one-to-one name="oneToOneProxy" class="OneToOneProxy" lazy="proxy" cascade="all" />
-        <property name="name"/>
-    </class>
-
-    <class name="OneToOneNoProxy">
-        <id name="entityId">
-            <generator class="foreign">
-                <param name="property">entity</param>
-            </generator>
-        </id>
-        <one-to-one name="entity" class="EntityWithOneToOnes" constrained="true"/>
-        <property name="name"/>
-    </class>
-
-    <class name="OneToOneProxy">
-        <id name="entityId">
-            <generator class="foreign">
-                <param name="property">entity</param>
-            </generator>
-        </id>
-        <one-to-one name="entity" class="EntityWithOneToOnes" constrained="true"/>
-        <property name="name"/>
-    </class>
-
-
-</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
deleted file mode 100644
index 5827f8c4d3..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
+++ /dev/null
@@ -1,178 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.runtime;
-
-import java.lang.reflect.InvocationTargetException;
-
-import org.hibernate.HibernateException;
-import org.hibernate.bytecode.buildtime.spi.BasicClassFilter;
-import org.hibernate.bytecode.buildtime.spi.FieldFilter;
-import org.hibernate.bytecode.spi.BytecodeProvider;
-import org.hibernate.bytecode.spi.InstrumentedClassLoader;
-import org.hibernate.dialect.AbstractHANADialect;
-import org.hibernate.dialect.MySQLDialect;
-
-import org.hibernate.testing.FailureExpected;
-import org.hibernate.testing.SkipForDialect;
-import org.hibernate.testing.TestForIssue;
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-import org.hibernate.testing.junit4.ClassLoadingIsolater;
-import org.junit.Rule;
-import org.junit.Test;
-
-/**
- * @author Steve Ebersole
- */
-public abstract class AbstractTransformingClassLoaderInstrumentTestCase extends BaseUnitTestCase {
-
-	@Rule
-	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
-			new ClassLoadingIsolater.IsolatedClassLoaderProvider() {
-				final BytecodeProvider provider = buildBytecodeProvider();
-
-				@Override
-				public ClassLoader buildIsolatedClassLoader() {
-					return new InstrumentedClassLoader(
-							Thread.currentThread().getContextClassLoader(),
-							provider.getTransformer(
-									new BasicClassFilter( new String[] { "org.hibernate.test.instrument" }, null ),
-									new FieldFilter() {
-										public boolean shouldInstrumentField(String className, String fieldName) {
-											return className.startsWith( "org.hibernate.test.instrument.domain" );
-										}
-										public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName) {
-											return fieldOwnerClassName.startsWith( "org.hibernate.test.instrument.domain" )
-													&& transformingClassName.equals( fieldOwnerClassName );
-										}
-									}
-							)
-					);
-				}
-
-				@Override
-				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
-					// nothing to do
-				}
-			}
-	);
-
-	protected abstract BytecodeProvider buildBytecodeProvider();
-
-
-	// the tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	@Test
-	public void testSetFieldInterceptor() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestInjectFieldInterceptorExecutable" );
-	}
-
-	@Test
-	public void testDirtyCheck() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestDirtyCheckExecutable" );
-	}
-
-	@Test
-	@TestForIssue( jiraKey = "HHH-9476" )
-	public void testEagerFetchLazyToOne() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestFetchingLazyToOneExecutable" );
-	}
-
-	@Test
-    @SkipForDialect( value = { MySQLDialect.class, AbstractHANADialect.class }, comment = "wrong sql in mapping, mysql/hana need double type, but it is float type in mapping")
-	public void testFetchAll() throws Exception {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestFetchAllExecutable" );
-	}
-
-	@Test
-    @SkipForDialect( value = { MySQLDialect.class, AbstractHANADialect.class }, comment = "wrong sql in mapping, mysql/hana need double type, but it is float type in mapping")
-	public void testLazy() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyExecutable" );
-	}
-
-	@Test
-    @SkipForDialect( value = { MySQLDialect.class, AbstractHANADialect.class }, comment = "wrong sql in mapping, mysql/hana need double type, but it is float type in mapping")
-	public void testLazyManyToOne() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyManyToOneExecutable" );
-	}
-
-	@Test
-    @SkipForDialect( value = { MySQLDialect.class, AbstractHANADialect.class }, comment = "wrong sql in mapping, mysql/hana need double type, but it is float type in mapping")
-	public void testPropertyInitialized() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestIsPropertyInitializedExecutable" );
-	}
-
-	@Test
-	public void testManyToOneProxy() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestManyToOneProxyExecutable" );
-	}
-
-	@Test
-	public void testLazyPropertyCustomType() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyPropertyCustomTypeExecutable" );
-	}
-
-	@Test
-	@FailureExpected( jiraKey = "HHH-9984")
-	@TestForIssue( jiraKey = "HHH-9984")
-	public void testLazyBasicFieldAccess() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyBasicFieldAccessExecutable" );
-	}
-
-	@Test
-	@TestForIssue( jiraKey = "HHH-5255")
-	public void testLazyBasicPropertyAccess() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyBasicPropertyAccessExecutable" );
-	}
-
-	@Test
-	public void testSharedPKOneToOne() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestSharedPKOneToOneExecutable" );
-	}
-
-	@Test
-    @SkipForDialect( value = { MySQLDialect.class, AbstractHANADialect.class }, comment = "wrong sql in mapping, mysql/hana need double type, but it is float type in mapping")
-	public void testCustomColumnReadAndWrite() {
-		executeExecutable( "org.hibernate.test.instrument.cases.TestCustomColumnReadAndWrite" );
-	}
-
-	// reflection code to ensure isolation into the created classloader ~~~~~~~
-
-	private static final Class[] SIG = new Class[] {};
-	private static final Object[] ARGS = new Object[] {};
-
-	public void executeExecutable(String name) {
-		Class execClass = null;
-		Object executable = null;
-		try {
-			execClass = Thread.currentThread().getContextClassLoader().loadClass( name );
-			executable = execClass.newInstance();
-		}
-		catch( Throwable t ) {
-			throw new HibernateException( "could not load executable", t );
-		}
-		try {
-			execClass.getMethod( "prepare", SIG ).invoke( executable, ARGS );
-			execClass.getMethod( "execute", SIG ).invoke( executable, ARGS );
-		}
-		catch ( NoSuchMethodException e ) {
-			throw new HibernateException( "could not exeucte executable", e );
-		}
-		catch ( IllegalAccessException e ) {
-			throw new HibernateException( "could not exeucte executable", e );
-		}
-		catch ( InvocationTargetException e ) {
-			throw new HibernateException( "could not exeucte executable", e.getTargetException() );
-		}
-		finally {
-			try {
-				execClass.getMethod( "complete", SIG ).invoke( executable, ARGS );
-			}
-			catch ( Throwable ignore ) {
-			}
-		}
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java
deleted file mode 100644
index 49641c1b59..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java
+++ /dev/null
@@ -1,19 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.test.instrument.runtime;
-
-import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
-import org.hibernate.bytecode.spi.BytecodeProvider;
-
-/**
- * @author Steve Ebersole
- */
-public class JavassistInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
-	protected BytecodeProvider buildBytecodeProvider() {
-		return new BytecodeProviderImpl();
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java b/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java
index 9d4287f3df..728a76ab66 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java
@@ -1,131 +1,132 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.lazycache;
 
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
+import org.hibernate.engine.spi.PersistentAttributeInterceptable;
+
 import org.hibernate.testing.Skip;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 @Skip( condition = InstrumentCacheTest.SkipMatcher.class, message = "Test domain classes not instrumented" )
 public class InstrumentCacheTest extends BaseCoreFunctionalTestCase {
 	public static class SkipMatcher implements Skip.Matcher {
 		@Override
 		public boolean isMatch() {
-			return ! FieldInterceptionHelper.isInstrumented( Document.class );
+			return ! PersistentAttributeInterceptable.class.isAssignableFrom( Document.class );
 		}
 	}
 
 	public String[] getMappings() {
 		return new String[] { "lazycache/Documents.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
 	}
 
 	public boolean overrideCacheStrategy() {
 		return false;
 	}
 
 	@Test
 	public void testInitFromCache() {
 		Session s;
 		Transaction tx;
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.persist( new Document("HiA", "Hibernate book", "Hibernate is....") );
 		tx.commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.createQuery("from Document fetch all properties").uniqueResult();
 		tx.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		Document d = (Document) s.createCriteria(Document.class).uniqueResult();
 		assertFalse( Hibernate.isPropertyInitialized(d, "text") );
 		assertFalse( Hibernate.isPropertyInitialized(d, "summary") );
 		assertEquals( "Hibernate is....", d.getText() );
 		assertTrue( Hibernate.isPropertyInitialized(d, "text") );
 		assertTrue( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 
 		assertEquals( 2, sessionFactory().getStatistics().getPrepareStatementCount() );
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		d = (Document) s.get(Document.class, d.getId());
 		assertFalse( Hibernate.isPropertyInitialized(d, "text") );
 		assertFalse( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testInitFromCache2() {
 		Session s;
 		Transaction tx;
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.persist( new Document("HiA", "Hibernate book", "Hibernate is....") );
 		tx.commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.createQuery("from Document fetch all properties").uniqueResult();
 		tx.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		Document d = (Document) s.createCriteria(Document.class).uniqueResult();
 		assertFalse( Hibernate.isPropertyInitialized(d, "text") );
 		assertFalse( Hibernate.isPropertyInitialized(d, "summary") );
 		assertEquals( "Hibernate is....", d.getText() );
 		assertTrue( Hibernate.isPropertyInitialized(d, "text") );
 		assertTrue( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getPrepareStatementCount() );
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		d = (Document) s.get(Document.class, d.getId());
 		assertTrue( Hibernate.isPropertyInitialized(d, "text") );
 		assertTrue( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java b/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java
index 52e338131e..32064fbcec 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java
@@ -1,94 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.lazyonetoone;
 import java.util.Date;
 
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
+import org.hibernate.engine.spi.PersistentAttributeInterceptable;
+
 import org.hibernate.testing.Skip;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 @Skip(
 		condition = LazyOneToOneTest.DomainClassesInstrumentedMatcher.class,
 		message = "Test domain classes were not instrumented"
 )
 public class LazyOneToOneTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "lazyonetoone/Person.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.MAX_FETCH_DEPTH, "2");
 		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
 	}
 
 	@Test
 	public void testLazy() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Person p = new Person("Gavin");
 		Person p2 = new Person("Emmanuel");
 		Employee e = new Employee(p);
 		new Employment(e, "JBoss");
 		Employment old = new Employment(e, "IFA");
 		old.setEndDate( new Date() );
 		s.persist(p);
 		s.persist(p2);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Person) s.createQuery("from Person where name='Gavin'").uniqueResult();
 		//assertFalse( Hibernate.isPropertyInitialized(p, "employee") );
 		assertSame( p.getEmployee().getPerson(), p );
 		assertTrue( Hibernate.isInitialized( p.getEmployee().getEmployments() ) );
 		assertEquals( p.getEmployee().getEmployments().size(), 1 );
 		p2 = (Person) s.createQuery("from Person where name='Emmanuel'").uniqueResult();
 		assertNull( p2.getEmployee() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Person) s.get(Person.class, "Gavin");
 		//assertFalse( Hibernate.isPropertyInitialized(p, "employee") );
 		assertSame( p.getEmployee().getPerson(), p );
 		assertTrue( Hibernate.isInitialized( p.getEmployee().getEmployments() ) );
 		assertEquals( p.getEmployee().getEmployments().size(), 1 );
 		p2 = (Person) s.get(Person.class, "Emmanuel");
 		assertNull( p2.getEmployee() );
 		s.delete(p2);
 		s.delete(old);
 		s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	public static class DomainClassesInstrumentedMatcher implements Skip.Matcher {
 		@Override
 		public boolean isMatch() {
 			// we match (to skip) when the classes are *not* instrumented...
-			return ! FieldInterceptionHelper.isInstrumented( Person.class );
+			return ! PersistentAttributeInterceptable.class.isAssignableFrom( Person.class );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index 472cabc7b9..c312a36e25 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,719 +1,719 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
-import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
+import org.hibernate.tuple.entity.BytecodeEnhancementMetadataNonPojoImpl;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	@SuppressWarnings("UnusedParameters")
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			PersisterCreationContext creationContext) {
 		this.factory = creationContext.getSessionFactory();
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
 		return MutableEntityEntryFactory.INSTANCE;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void generateEntityDefinition() {
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 			);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 			);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session )
 			);
 			TwoPhaseLoad.postLoad( clone, session, new PostLoadEvent( (EventSource) session ) );
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return UnstructuredCacheEntry.INSTANCE;
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
-	public EntityInstrumentationMetadata getInstrumentationMetadata() {
-		return new NonPojoInstrumentationMetadata( getEntityName() );
+	public BytecodeEnhancementMetadata getInstrumentationMetadata() {
+		return new BytecodeEnhancementMetadataNonPojoImpl( getEntityName() );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		throw new NotYetImplementedException();
 	}
 
     @Override
     public int[] resolveAttributeIndexes(String[] attributeNames) {
         return null;
     }
 
 	@Override
 	public boolean canUseReferenceCacheEntries() {
 		return false;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java b/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java
index 1a08446804..ef18164914 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java
@@ -1,524 +1,524 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.ops;
 
 import java.util.Map;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.criterion.Projections;
+import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class SaveOrUpdateTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Override
 	protected void addSettings(Map settings) {
 		settings.put( AvailableSettings.GENERATE_STATISTICS, "true" );
 		settings.put( AvailableSettings.STATEMENT_BATCH_SIZE, "0" );
 	}
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {"ops/Node.hbm.xml"};
 	}
 
 	@Override
 	protected String getCacheConcurrencyStrategy() {
 		return "nonstrict-read-write";
 	}
 
 	@Test
 	public void testSaveOrUpdateDeepTree() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Node root = new Node( "root" );
 		Node child = new Node( "child" );
 		Node grandchild = new Node( "grandchild" );
 		root.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		grandchild.setDescription( "the grand child" );
 		Node grandchild2 = new Node( "grandchild2" );
 		child.addChild( grandchild2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 1 );
 		clearCounts();
 
 		Node child2 = new Node( "child2" );
 		Node grandchild3 = new Node( "grandchild3" );
 		child2.addChild( grandchild3 );
 		root.addChild( child2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( grandchild );
 		s.delete( grandchild2 );
 		s.delete( grandchild3 );
 		s.delete( child );
 		s.delete( child2 );
 		s.delete( root );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateDeepTreeWithGeneratedId() {
-		boolean instrumented = FieldInterceptionHelper.isInstrumented( new NumberedNode() );
+		boolean instrumented = PersistentAttributeInterceptable.class.isAssignableFrom( NumberedNode.class );
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		NumberedNode child = new NumberedNode( "child" );
 		NumberedNode grandchild = new NumberedNode( "grandchild" );
 		root.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		child = ( NumberedNode ) root.getChildren().iterator().next();
 		grandchild = ( NumberedNode ) child.getChildren().iterator().next();
 		grandchild.setDescription( "the grand child" );
 		NumberedNode grandchild2 = new NumberedNode( "grandchild2" );
 		child.addChild( grandchild2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( instrumented ? 1 : 3 );
 		clearCounts();
 
 		NumberedNode child2 = new NumberedNode( "child2" );
 		NumberedNode grandchild3 = new NumberedNode( "grandchild3" );
 		child2.addChild( grandchild3 );
 		root.addChild( child2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( instrumented ? 0 : 4 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from NumberedNode where name like 'grand%'" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode where name like 'child%'" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode" ).executeUpdate();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateTree() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Node root = new Node( "root" );
 		Node child = new Node( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		clearCounts();
 
 		root.setDescription( "The root node" );
 		child.setDescription( "The child node" );
 
 		Node secondChild = new Node( "second child" );
 
 		root.addChild( secondChild );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from Node where parent is not null" ).executeUpdate();
 		s.createQuery( "delete from Node" ).executeUpdate();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateTreeWithGeneratedId() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		clearCounts();
 
 		root.setDescription( "The root node" );
 		child.setDescription( "The child node" );
 
 		NumberedNode secondChild = new NumberedNode( "second child" );
 
 		root.addChild( secondChild );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from NumberedNode where parent is not null" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode" ).executeUpdate();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateManaged() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		s.saveOrUpdate( root );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		assertFalse( s.contains( child ) );
 		s.flush();
 		assertTrue( s.contains( child ) );
 		tx.commit();
 
 		assertTrue( root.getChildren().contains( child ) );
 		assertEquals( root.getChildren().size(), 1 );
 
 		tx = s.beginTransaction();
 		assertEquals(
 				Long.valueOf( 2 ),
 				s.createCriteria( NumberedNode.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult()
 		);
 		s.delete( root );
 		s.delete( child );
 		tx.commit();
 		s.close();
 	}
 
 
 	@Test
 	public void testSaveOrUpdateGot() {
 		clearCounts();
 
-		boolean instrumented = FieldInterceptionHelper.isInstrumented( new NumberedNode() );
+		boolean instrumented = PersistentAttributeInterceptable.class.isAssignableFrom( NumberedNode.class );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( instrumented ? 0 : 1 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		root = ( NumberedNode ) s.get( NumberedNode.class, new Long( root.getId() ) );
 		Hibernate.initialize( root.getChildren() );
 		tx.commit();
 		s.close();
 
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		assertTrue( s.contains( child ) );
 		tx.commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( instrumented ? 0 : 1 );
 
 		tx = s.beginTransaction();
 		assertEquals(
 				s.createCriteria( NumberedNode.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult(),
 		        new Long( 2 )
 		);
 		s.delete( root );
 		s.delete( child );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateGotWithMutableProp() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Node root = new Node( "root" );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( 0 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		root = ( Node ) s.get( Node.class, "root" );
 		Hibernate.initialize( root.getChildren() );
 		tx.commit();
 		s.close();
 
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Node child = new Node( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		assertTrue( s.contains( child ) );
 		tx.commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 1 ); //note: will fail here if no second-level cache
 
 		tx = s.beginTransaction();
 		assertEquals(
 				s.createCriteria( Node.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult(),
 		        new Long( 2 )
 		);
 		s.delete( root );
 		s.delete( child );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEvictThenSaveOrUpdate() {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Node parent = new Node( "1:parent" );
 		Node child = new Node( "2:child" );
 		Node grandchild = new Node( "3:grandchild" );
 		parent.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( parent );
 		s.getTransaction().commit();
 		s.close();
 
 		Session s1 = openSession();
 		s1.getTransaction().begin();
 		child = ( Node ) s1.load( Node.class, "2:child" );
 		assertTrue( s1.contains( child ) );
 		assertFalse( Hibernate.isInitialized( child ) );
 		assertTrue( s1.contains( child.getParent() ) );
 		assertTrue( Hibernate.isInitialized( child ) );
 		assertFalse( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertTrue( s1.contains( child ) );
 		s1.evict( child );
 		assertFalse( s1.contains( child ) );
 		assertTrue( s1.contains( child.getParent() ) );
 
 		Session s2 = openSession();
 		try {
 			s2.getTransaction().begin();
 			s2.saveOrUpdate( child );
 			fail();
 		}
 		catch ( HibernateException ex ) {
 			// expected because parent is connected to s1
 		}
 		finally {
 			s2.getTransaction().rollback();
 		}
 		s2.close();
 
 		s1.evict( child.getParent() );
 		assertFalse( s1.contains( child.getParent() ) );
 
 		s2 = openSession();
 		s2.getTransaction().begin();
 		s2.saveOrUpdate( child );
 		assertTrue( s2.contains( child ) );
 		assertFalse( s1.contains( child ) );
 		assertTrue( s2.contains( child.getParent() ) );
 		assertFalse( s1.contains( child.getParent() ) );
 		assertFalse( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertEquals( 1, child.getChildren().size() );
 		assertEquals( "1:parent", child.getParent().getName() );
 		assertTrue( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertNull( child.getParent().getDescription() );
 		assertTrue( Hibernate.isInitialized( child.getParent() ) );
 
 		s1.getTransaction().commit();
 		s2.getTransaction().commit();
 		s1.close();
 		s2.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( s.get( Node.class, "3:grandchild" ) );
 		s.delete( s.get( Node.class, "2:child" ) );
 		s.delete( s.get( Node.class, "1:parent" ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSavePersistentEntityWithUpdate() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		root.setName( "a name" );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		root = ( NumberedNode ) s.get( NumberedNode.class, root.getId() );
 		assertEquals( "a name", root.getName() );
 		root.setName( "a new name" );
 		s.save( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( 1 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		root = ( NumberedNode ) s.get( NumberedNode.class, root.getId() );
 		assertEquals( "a new name", root.getName() );
 		s.delete( root );
 		tx.commit();
 		s.close();
 	}
 
 	private void clearCounts() {
 		sessionFactory().getStatistics().clear();
 	}
 
 	private void assertInsertCount(int count) {
 		int inserts = ( int ) sessionFactory().getStatistics().getEntityInsertCount();
 		assertEquals( count, inserts );
 	}
 
 	private void assertUpdateCount(int count) {
 		int updates = ( int ) sessionFactory().getStatistics().getEntityUpdateCount();
 		assertEquals( count, updates );
 	}
 }
 
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/internal/core/JpaFlushEntityEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/internal/core/JpaFlushEntityEventListener.java
index f73c24988c..8240bf38ad 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/internal/core/JpaFlushEntityEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/internal/core/JpaFlushEntityEventListener.java
@@ -1,74 +1,74 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.event.internal.core;
 
 import org.hibernate.SessionFactory;
-import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.internal.DefaultFlushEntityEventListener;
 import org.hibernate.jpa.event.internal.jpa.CallbackRegistryConsumer;
 import org.hibernate.jpa.event.spi.jpa.CallbackRegistry;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PreUpdate operation
  *
  * @author Emmanuel Bernard
  */
 public class JpaFlushEntityEventListener extends DefaultFlushEntityEventListener implements CallbackRegistryConsumer {
 	private CallbackRegistry callbackRegistry;
 
 	public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
 		this.callbackRegistry = callbackRegistry;
 	}
 
 	public JpaFlushEntityEventListener() {
 		super();
 	}
 
 	public JpaFlushEntityEventListener(CallbackRegistry callbackRegistry) {
 		super();
 		this.callbackRegistry = callbackRegistry;
 	}
 
 	@Override
 	protected boolean invokeInterceptor(
 			SessionImplementor session,
 			Object entity,
 			EntityEntry entry,
 			Object[] values,
 			EntityPersister persister) {
 		boolean isDirty = false;
 		if ( entry.getStatus() != Status.DELETED ) {
 			if ( callbackRegistry.preUpdate( entity ) ) {
 				isDirty = copyState( entity, persister.getPropertyTypes(), values, session.getFactory() );
 			}
 		}
 		return super.invokeInterceptor( session, entity, entry, values, persister ) || isDirty;
 	}
 
 	private boolean copyState(Object entity, Type[] types, Object[] state, SessionFactory sf) {
 		// copy the entity state into the state array and return true if the state has changed
 		ClassMetadata metadata = sf.getClassMetadata( entity.getClass() );
 		Object[] newState = metadata.getPropertyValues( entity );
 		int size = newState.length;
 		boolean isDirty = false;
 		for ( int index = 0; index < size ; index++ ) {
 			if ( ( state[index] == LazyPropertyInitializer.UNFETCHED_PROPERTY &&
 					newState[index] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) ||
 					( state[index] != newState[index] && !types[index].isEqual( state[index], newState[index] ) ) ) {
 				isDirty = true;
 				state[index] = newState[index];
 			}
 		}
 		return isDirty;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/enhance/EnhancingClassTransformerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/enhance/EnhancingClassTransformerImpl.java
index fe44aed7a3..b61096f250 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/enhance/EnhancingClassTransformerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/enhance/EnhancingClassTransformerImpl.java
@@ -1,54 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.internal.enhance;
 
 import java.lang.instrument.IllegalClassFormatException;
 import java.security.ProtectionDomain;
 import java.util.ArrayList;
 import java.util.Collection;
-import javax.persistence.spi.ClassTransformer;
 
 import org.hibernate.bytecode.enhance.spi.Enhancer;
+import org.hibernate.bytecode.spi.ClassTransformer;
 
 /**
  * @author Steve Ebersole
  */
 public class EnhancingClassTransformerImpl implements ClassTransformer {
 	private final Collection<String> classNames;
 
 	private Enhancer enhancer;
 
 	public EnhancingClassTransformerImpl(Collection<String> incomingClassNames) {
 		this.classNames = new ArrayList<String>( incomingClassNames.size() );
 		this.classNames.addAll( incomingClassNames );
 	}
 
 	@Override
 	public byte[] transform(
 			ClassLoader loader,
 			String className,
 			Class<?> classBeingRedefined,
 			ProtectionDomain protectionDomain,
 			byte[] classfileBuffer) throws IllegalClassFormatException {
 		if ( enhancer == null ) {
 			enhancer = new Enhancer( new EnhancementContextImpl( classNames, loader ) );
 		}
 
 		try {
 			return enhancer.enhance( className, classfileBuffer );
 		}
 		catch (final Exception e) {
 			throw new IllegalClassFormatException( "Error performing enhancement" ) {
 				@Override
 				public synchronized Throwable getCause() {
 					return e;
 				}
 			};
 		}
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/instrument/InterceptFieldClassFileTransformer.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/instrument/InterceptFieldClassFileTransformer.java
deleted file mode 100644
index 97f6cf0342..0000000000
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/instrument/InterceptFieldClassFileTransformer.java
+++ /dev/null
@@ -1,68 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.jpa.internal.instrument;
-import java.lang.instrument.IllegalClassFormatException;
-import java.security.ProtectionDomain;
-import java.util.ArrayList;
-import java.util.Collection;
-import java.util.List;
-
-import org.hibernate.bytecode.buildtime.spi.ClassFilter;
-import org.hibernate.bytecode.buildtime.spi.FieldFilter;
-import org.hibernate.bytecode.spi.ClassTransformer;
-import org.hibernate.cfg.Environment;
-
-/**
- * Enhance the classes allowing them to implements InterceptFieldEnabled
- * This interface is then used by Hibernate for some optimizations.
- *
- * @author Emmanuel Bernard
- */
-public class InterceptFieldClassFileTransformer implements javax.persistence.spi.ClassTransformer {
-	private ClassTransformer classTransformer;
-
-	public InterceptFieldClassFileTransformer(Collection<String> entities) {
-		final List<String> copyEntities = new ArrayList<String>( entities.size() );
-		copyEntities.addAll( entities );
-		classTransformer = Environment.getBytecodeProvider().getTransformer(
-				//TODO change it to a static class to make it faster?
-				new ClassFilter() {
-					public boolean shouldInstrumentClass(String className) {
-						return copyEntities.contains( className );
-					}
-				},
-				//TODO change it to a static class to make it faster?
-				new FieldFilter() {
-					@Override
-					public boolean shouldInstrumentField(String className, String fieldName) {
-						return true;
-					}
-					@Override
-					public boolean shouldTransformFieldAccess(
-							String transformingClassName, String fieldOwnerClassName, String fieldName
-					) {
-						return true;
-					}
-				}
-		);
-	}
-	@Override
-	public byte[] transform(
-			ClassLoader loader,
-			String className,
-			Class<?> classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer ) throws IllegalClassFormatException {
-		try {
-			return classTransformer.transform( loader, className, classBeingRedefined,
-					protectionDomain, classfileBuffer );
-		}
-		catch (Exception e) {
-			throw new IllegalClassFormatException( e.getMessage() );
-		}
-	}
-}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/util/PersistenceUtilHelper.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/util/PersistenceUtilHelper.java
index a4b7538051..42f1137220 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/util/PersistenceUtilHelper.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/util/PersistenceUtilHelper.java
@@ -1,413 +1,423 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.internal.util;
 
 import java.io.Serializable;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.WeakHashMap;
 import javax.persistence.spi.LoadState;
 
 import org.hibernate.HibernateException;
-import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
 import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.engine.spi.PersistentAttributeInterceptable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Central delegate for handling calls from:<ul>
  *     <li>{@link javax.persistence.PersistenceUtil#isLoaded(Object)}</li>
  *     <li>{@link javax.persistence.PersistenceUtil#isLoaded(Object, String)}</li>
  *     <li>{@link javax.persistence.spi.ProviderUtil#isLoaded(Object)}</li>
  *     <li>{@link javax.persistence.spi.ProviderUtil#isLoadedWithReference(Object, String)}</li>
  *     <li>{@link javax.persistence.spi.ProviderUtil#isLoadedWithoutReference(Object, String)}li>
  * </ul>
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 public final class PersistenceUtilHelper {
 	private PersistenceUtilHelper() {
 	}
 
 	/**
 	 * Determine if the given object reference represents loaded state.  The reference may be to an entity or a
 	 * persistent collection.
 	 * <p/>
 	 * Return is defined as follows:<ol>
 	 *     <li>
 	 *         If the reference is a {@link HibernateProxy}, we return {@link LoadState#LOADED} if
 	 *         {@link org.hibernate.proxy.LazyInitializer#isUninitialized()} returns {@code false}; else we return
 	 *         {@link LoadState#NOT_LOADED}
 	 *     </li>
 	 *     <li>
 	 *         If the reference is an enhanced (by Hibernate) entity, we return {@link LoadState#LOADED} if
-	 *         {@link org.hibernate.bytecode.instrumentation.spi.FieldInterceptor#isInitialized()} returns {@code true};
-	 *         else we return {@link LoadState#NOT_LOADED}
+	 *         {@link LazyAttributeLoadingInterceptor#hasAnyUninitializedAttributes()} returns {@code false};
+	 *         otherwise we return {@link LoadState#NOT_LOADED}
 	 *     </li>
 	 *     <li>
 	 *         If the reference is a {@link PersistentCollection}, we return {@link LoadState#LOADED} if
 	 *         {@link org.hibernate.collection.spi.PersistentCollection#wasInitialized()} returns {@code true}; else
 	 *         we return {@link LoadState#NOT_LOADED}
 	 *     </li>
 	 *     <li>
 	 *         In all other cases we return {@link LoadState#UNKNOWN}
 	 *     </li>
 	 * </ol>
 	 *
 	 *
 	 * @param reference The object reference to check.
 	 *
 	 * @return The appropriate LoadState (see above)
 	 */
 	public static LoadState isLoaded(Object reference) {
 		if ( reference instanceof HibernateProxy ) {
 			final boolean isInitialized = !( (HibernateProxy) reference ).getHibernateLazyInitializer().isUninitialized();
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
-		else if ( FieldInterceptionHelper.isInstrumented( reference ) ) {
-			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( reference );
-			final boolean isInitialized = interceptor == null || interceptor.isInitialized();
+		else if ( reference instanceof PersistentAttributeInterceptable ) {
+			boolean isInitialized = isInitialized( (PersistentAttributeInterceptable) reference );
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
 		else if ( reference instanceof PersistentCollection ) {
 			final boolean isInitialized = ( (PersistentCollection) reference ).wasInitialized();
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
 		else {
 			return LoadState.UNKNOWN;
 		}
 	}
 
+	@SuppressWarnings("SimplifiableIfStatement")
+	private static boolean isInitialized(PersistentAttributeInterceptable interceptable) {
+		final LazyAttributeLoadingInterceptor interceptor = extractInterceptor( interceptable );
+		return interceptable == null || !interceptor.hasAnyUninitializedAttributes();
+	}
+
+	private static LazyAttributeLoadingInterceptor extractInterceptor(PersistentAttributeInterceptable interceptable) {
+		return (LazyAttributeLoadingInterceptor) interceptable.$$_hibernate_getInterceptor();
+
+	}
+
 	/**
 	 * Is the given attribute (by name) loaded?  This form must take care to not access the attribute (trigger
 	 * initialization).
 	 *
 	 * @param entity The entity
 	 * @param attributeName The name of the attribute to check
 	 * @param cache The cache we maintain of attribute resolutions
 	 *
 	 * @return The LoadState
 	 */
 	public static LoadState isLoadedWithoutReference(Object entity, String attributeName, MetadataCache cache) {
 		boolean sureFromUs = false;
 		if ( entity instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				// we have an uninitialized proxy, the attribute cannot be loaded
 				return LoadState.NOT_LOADED;
 			}
 			else {
 				// swap the proxy with target (for proper class name resolution)
 				entity = li.getImplementation();
 			}
 			sureFromUs = true;
 		}
 
 		// we are instrumenting but we can't assume we are the only ones
-		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
-			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
-			final boolean isInitialized = interceptor == null || interceptor.isInitialized( attributeName );
+		if ( entity instanceof PersistentAttributeInterceptable ) {
+			final LazyAttributeLoadingInterceptor interceptor = extractInterceptor( (PersistentAttributeInterceptable) entity );
+			final boolean isInitialized = interceptor == null || interceptor.isAttributeLoaded( attributeName );
 			LoadState state;
 			if (isInitialized && interceptor != null) {
 				// attributeName is loaded according to bytecode enhancement, but is it loaded as far as association?
 				// it's ours, we can read
 				try {
 					final Class entityClass = entity.getClass();
 					final Object attributeValue = cache.getClassMetadata( entityClass )
 							.getAttributeAccess( attributeName )
 							.extractValue( entity );
 					state = isLoaded( attributeValue );
 
 					// it's ours so we know it's loaded
 					if ( state == LoadState.UNKNOWN ) {
 						state = LoadState.LOADED;
 					}
 				}
 				catch (AttributeExtractionException ignore) {
 					state = LoadState.UNKNOWN;
 				}
 			}
 			else if ( interceptor != null ) {
 				state = LoadState.NOT_LOADED;
 			}
 			else if ( sureFromUs ) {
 				// property is loaded according to bytecode enhancement, but is it loaded as far as association?
 				// it's ours, we can read
 				try {
 					final Class entityClass = entity.getClass();
 					final Object attributeValue = cache.getClassMetadata( entityClass )
 							.getAttributeAccess( attributeName )
 							.extractValue( entity );
 					state = isLoaded( attributeValue );
 
 					// it's ours so we know it's loaded
 					if ( state == LoadState.UNKNOWN ) {
 						state = LoadState.LOADED;
 					}
 				}
 				catch (AttributeExtractionException ignore) {
 					state = LoadState.UNKNOWN;
 				}
 			}
 			else {
 				state = LoadState.UNKNOWN;
 			}
 
 			return state;
 		}
 		else {
 			return LoadState.UNKNOWN;
 		}
 	}
 
 
 	/**
 	 * Is the given attribute (by name) loaded?  This form must take care to not access the attribute (trigger
 	 * initialization).
 	 *
 	 * @param entity The entity
 	 * @param attributeName The name of the attribute to check
 	 * @param cache The cache we maintain of attribute resolutions
 	 *
 	 * @return The LoadState
 	 */
 	public static LoadState isLoadedWithReference(Object entity, String attributeName, MetadataCache cache) {
 		if ( entity instanceof HibernateProxy ) {
 			final LazyInitializer li = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				// we have an uninitialized proxy, the attribute cannot be loaded
 				return LoadState.NOT_LOADED;
 			}
 			else {
 				// swap the proxy with target (for proper class name resolution)
 				entity = li.getImplementation();
 			}
 		}
 
 		try {
 			final Class entityClass = entity.getClass();
 			final Object attributeValue = cache.getClassMetadata( entityClass )
 					.getAttributeAccess( attributeName )
 					.extractValue( entity );
 			return isLoaded( attributeValue );
 		}
 		catch (AttributeExtractionException ignore) {
 			return LoadState.UNKNOWN;
 		}
 	}
 
 
 	public static class AttributeExtractionException extends HibernateException {
 		public AttributeExtractionException(String message) {
 			super( message );
 		}
 
 		public AttributeExtractionException(String message, Throwable cause) {
 			super( message, cause );
 		}
 	}
 
 	public static interface AttributeAccess {
 		public Object extractValue(Object owner) throws AttributeExtractionException;
 	}
 
 	public static class FieldAttributeAccess implements AttributeAccess {
 		private final String name;
 		private final Field field;
 
 		public FieldAttributeAccess(Field field) {
 			this.name = field.getName();
 			try {
 				field.setAccessible( true );
 			}
 			catch (Exception e) {
 				this.field = null;
 				return;
 			}
 			this.field = field;
 		}
 
 		@Override
 		public Object extractValue(Object owner) {
 			if ( field == null ) {
 				throw new AttributeExtractionException( "Attribute (field) " + name + " is not accessible" );
 			}
 
 			try {
 				return field.get( owner );
 			}
 			catch ( IllegalAccessException e ) {
 				throw new AttributeExtractionException(
 						"Unable to access attribute (field): " + field.getDeclaringClass().getName() + "#" + name,
 						e
 				);
 			}
 		}
 	}
 
 	public static class MethodAttributeAccess implements AttributeAccess {
 		private final String name;
 		private final Method method;
 
 		public MethodAttributeAccess(String attributeName, Method method) {
 			this.name = attributeName;
 			try {
 				method.setAccessible( true );
 			}
 			catch (Exception e) {
 				this.method = null;
 				return;
 			}
 			this.method = method;
 		}
 
 		@Override
 		public Object extractValue(Object owner) {
 			if ( method == null ) {
 				throw new AttributeExtractionException( "Attribute (method) " + name + " is not accessible" );
 			}
 
 			try {
 				return method.invoke( owner );
 			}
 			catch ( IllegalAccessException e ) {
 				throw new AttributeExtractionException(
 						"Unable to access attribute (method): " + method.getDeclaringClass().getName() + "#" + name,
 						e
 				);
 			}
 			catch ( InvocationTargetException e ) {
 				throw new AttributeExtractionException(
 						"Unable to access attribute (method): " + method.getDeclaringClass().getName() + "#" + name,
 						e.getCause()
 				);
 			}
 		}
 	}
 
 	private static class NoSuchAttributeAccess implements AttributeAccess {
 		private final Class clazz;
 		private final String attributeName;
 
 		public NoSuchAttributeAccess(Class clazz, String attributeName) {
 			this.clazz = clazz;
 			this.attributeName = attributeName;
 		}
 
 		@Override
 		public Object extractValue(Object owner) throws AttributeExtractionException {
 			throw new AttributeExtractionException( "No such attribute : " + clazz.getName() + "#" + attributeName );
 		}
 	}
 
 	public static class ClassMetadataCache {
 		private final Class specifiedClass;
 		private List<Class<?>> classHierarchy;
 		private Map<String, AttributeAccess> attributeAccessMap = new HashMap<String, AttributeAccess>();
 
 		public ClassMetadataCache(Class<?> clazz) {
 			this.specifiedClass = clazz;
 			this.classHierarchy = findClassHierarchy( clazz );
 		}
 
 		private static List<Class<?>> findClassHierarchy(Class<?> clazz) {
 			List<Class<?>> classes = new ArrayList<Class<?>>();
 			Class<?> current = clazz;
 			do {
 				classes.add( current );
 				current = current.getSuperclass();
 			} while ( current != null );
 
 			return classes;
 		}
 
 		public AttributeAccess getAttributeAccess(String attributeName) {
 			AttributeAccess attributeAccess = attributeAccessMap.get( attributeName );
 			if ( attributeAccess == null ) {
 				attributeAccess = buildAttributeAccess( attributeName );
 				attributeAccessMap.put( attributeName, attributeAccess );
 			}
 			return attributeAccess;
 		}
 
 		private AttributeAccess buildAttributeAccess(String attributeName) {
 			for ( Class clazz : classHierarchy ) {
 				try {
 					final Field field = clazz.getDeclaredField( attributeName );
 					if ( field != null ) {
 						return new FieldAttributeAccess( field );
 					}
 				}
 				catch ( NoSuchFieldException e ) {
 					final Method method = getMethod( clazz, attributeName );
 					if ( method != null ) {
 						return new MethodAttributeAccess( attributeName, method );
 					}
 				}
 			}
 
 			//we could not find any match
 			return new NoSuchAttributeAccess( specifiedClass, attributeName );
 		}
 	}
 
 	/**
 	 * Returns the method with the specified name or <code>null</code> if it does not exist.
 	 *
 	 * @param clazz The class to check.
 	 * @param attributeName The attribute name.
 	 *
 	 * @return Returns the method with the specified name or <code>null</code> if it does not exist.
 	 */
 	private static Method getMethod(Class<?> clazz, String attributeName) {
 		try {
 			char[] string = attributeName.toCharArray();
 			string[0] = Character.toUpperCase( string[0] );
 			String casedAttributeName = new String( string );
 			try {
 				return clazz.getDeclaredMethod( "get" + casedAttributeName );
 			}
 			catch ( NoSuchMethodException e ) {
 				return clazz.getDeclaredMethod( "is" + casedAttributeName );
 			}
 		}
 		catch ( NoSuchMethodException e ) {
 			return null;
 		}
 	}
 
 	/**
 	 * Cache hierarchy and member resolution in a weak hash map
 	 */
 	//TODO not really thread-safe
 	public static class MetadataCache implements Serializable {
 		private transient Map<Class<?>, ClassMetadataCache> classCache = new WeakHashMap<Class<?>, ClassMetadataCache>();
 
 
 		private void readObject(java.io.ObjectInputStream stream) {
 			classCache = new WeakHashMap<Class<?>, ClassMetadataCache>();
 		}
 
 		ClassMetadataCache getClassMetadata(Class<?> clazz) {
 			ClassMetadataCache classMetadataCache = classCache.get( clazz );
 			if ( classMetadataCache == null ) {
 				classMetadataCache = new ClassMetadataCache( clazz );
 				classCache.put( clazz, classMetadataCache );
 			}
 			return classMetadataCache;
 		}
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java
index 7911df9691..a13f310218 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,627 +1,627 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.test.ejb3configuration;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Map;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
-import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
+import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.spi.Bootstrap;
 import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
 import org.hibernate.jpa.test.SettingsGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
-import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
+import org.hibernate.tuple.entity.BytecodeEnhancementMetadataNonPojoImpl;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 import org.junit.Assert;
 import org.junit.Test;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest {
 	@Test
 	@SuppressWarnings("unchecked")
 	public void testPersisterClassProvider() {
 		Map settings = SettingsGenerator.generateSettings(
 				PersisterClassResolverInitiator.IMPL_NAME, GoofyPersisterClassProvider.class,
 				AvailableSettings.LOADED_CLASSES, Arrays.asList( Bell.class )
 		);
 		try {
 			EntityManagerFactory entityManagerFactory = Bootstrap.getEntityManagerFactoryBuilder(
 					new PersistenceUnitDescriptorAdapter(),
 					settings
 			).build();
 			entityManagerFactory.close();
 		}
 		catch ( PersistenceException e ) {
             Assert.assertNotNull( e.getCause() );
 			Assert.assertNotNull( e.getCause().getCause() );
 			Assert.assertEquals( GoofyException.class, e.getCause().getCause().getClass() );
 
 		}
 	}
 
 	public static class GoofyPersisterClassProvider implements PersisterClassResolver {
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		@SuppressWarnings( {"UnusedParameters"})
 		public GoofyProvider(
 				org.hibernate.mapping.PersistentClass persistentClass,
 				org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 				NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 				PersisterCreationContext creationContext) {
 			throw new GoofyException();
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
-		public EntityInstrumentationMetadata getInstrumentationMetadata() {
-			return new NonPojoInstrumentationMetadata( getEntityName() );
+		public BytecodeEnhancementMetadata getInstrumentationMetadata() {
+			return new BytecodeEnhancementMetadataNonPojoImpl( getEntityName() );
 		}
 
 		@Override
 		public void generateEntityDefinition() {
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public EntityEntryFactory getEntityEntryFactory() {
 			return MutableEntityEntryFactory.INSTANCE;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 		
         @Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 		
 		@Override
         public boolean hasNaturalIdCache() {
             return false;
         }
 
         @Override
         public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
             return null;
         }
 
         @Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "Not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			return null;
 		}
 
 		@Override
 		public int[] resolveAttributeIndexes(String[] attributeNames) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean canUseReferenceCacheEntries() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			return null;
 		}
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/InstrumentedClassLoader.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/InstrumentedClassLoader.java
similarity index 98%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/InstrumentedClassLoader.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/InstrumentedClassLoader.java
index 046e3dbfe0..cae92678a2 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/InstrumentedClassLoader.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/InstrumentedClassLoader.java
@@ -1,103 +1,101 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-
-//$Id$
-package org.hibernate.jpa.test.instrument;
+package org.hibernate.jpa.test.enhancement;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.instrument.IllegalClassFormatException;
 import java.util.List;
 
 import org.hibernate.jpa.internal.enhance.EnhancingClassTransformerImpl;
 
 /**
  * @author Emmanuel Bernard
  * @author Dustin Schultz
  */
 public class InstrumentedClassLoader extends ClassLoader {
 	private List<String> entities;
 
 	public InstrumentedClassLoader(ClassLoader parent) {
 		super( parent );
 	}
 
 	@Override
 	public Class<?> loadClass(String name) throws ClassNotFoundException {
 		// Do not instrument the following packages
 		if (name != null
 				&& (name.startsWith("java.lang.") || 
 					name.startsWith("java.util.")))
 			return getParent().loadClass(name);
 		Class c = findLoadedClass( name );
 		if ( c != null ) return c;
 
 		byte[] transformed = loadClassBytes(name);
 		
 		return defineClass( name, transformed, 0, transformed.length );
 	}
 	
 	/**
 	 * Specialized {@link ClassLoader#loadClass(String)} that returns the class
 	 * as a byte array.
 	 * 
 	 * @param name
 	 * @return
 	 * @throws ClassNotFoundException
 	 */
 	public byte[] loadClassBytes(String name) throws ClassNotFoundException {
 		InputStream is = this.getResourceAsStream( name.replace( ".", "/" ) + ".class" );
 		if ( is == null ) throw new ClassNotFoundException( name );
 		byte[] buffer = new byte[409600];
 		byte[] originalClass = new byte[0];
 		int r = 0;
 		try {
 			r = is.read( buffer );
 		}
 		catch (IOException e) {
 			throw new ClassNotFoundException( name + " not found", e );
 		}
 		while ( r >= buffer.length ) {
 			byte[] temp = new byte[ originalClass.length + buffer.length ];
 			System.arraycopy( originalClass, 0, temp, 0, originalClass.length );
 			System.arraycopy( buffer, 0, temp, originalClass.length, buffer.length );
 			originalClass = temp;
 		}
 		if ( r != -1 ) {
 			byte[] temp = new byte[ originalClass.length + r ];
 			System.arraycopy( originalClass, 0, temp, 0, originalClass.length );
 			System.arraycopy( buffer, 0, temp, originalClass.length, r );
 			originalClass = temp;
 		}
 		try {
 			is.close();
 		}
 		catch (IOException e) {
 			throw new ClassNotFoundException( name + " not found", e );
 		}
 		EnhancingClassTransformerImpl t = new EnhancingClassTransformerImpl( entities );
 		byte[] transformed = new byte[0];
 		try {
 			transformed = t.transform(
 					getParent(),
 					name,
 					null,
 					null,
 					originalClass
 			);
 		}
 		catch (IllegalClassFormatException e) {
 			throw new ClassNotFoundException( name + " not found", e );
 		}
 		
 		return transformed;
 	}
 
 	public void setEntities(List<String> entities) {
 		this.entities = entities;
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/InterceptFieldClassFileTransformerTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/InterceptFieldClassFileTransformerTest.java
similarity index 94%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/InterceptFieldClassFileTransformerTest.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/InterceptFieldClassFileTransformerTest.java
index 569557410e..a3daa90548 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/InterceptFieldClassFileTransformerTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/InterceptFieldClassFileTransformerTest.java
@@ -1,112 +1,109 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-
-//$Id$
-package org.hibernate.jpa.test.instrument;
+package org.hibernate.jpa.test.enhancement;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
-import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.List;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.CtMethod;
 import javassist.bytecode.AttributeInfo;
 import javassist.bytecode.StackMapTable;
 
 import org.hibernate.engine.spi.Managed;
 import org.hibernate.engine.spi.ManagedComposite;
 import org.hibernate.engine.spi.ManagedEntity;
+import org.hibernate.jpa.test.enhancement.cases.domain.Simple;
 
 import org.hibernate.testing.TestForIssue;
-import org.hibernate.testing.junit4.ExtraAssertions;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Dustin Schultz
  */
 public class InterceptFieldClassFileTransformerTest {
 	
 	private List<String> entities = new ArrayList<String>();
 	private InstrumentedClassLoader loader = null;
 	
 	@Before
 	public void setup() {
-		entities.add( "org.hibernate.jpa.test.instrument.Simple" );
+		entities.add( Simple.class.getName() );
 		// use custom class loader which enhances the class
 		InstrumentedClassLoader cl = new InstrumentedClassLoader( Thread.currentThread().getContextClassLoader() );
 		cl.setEntities( entities );
 		this.loader = cl;
 	}
 	
 	/**
 	 * Tests that class file enhancement works.
 	 * 
 	 * @throws Exception in case the test fails.
 	 */
     @Test
 	public void testEnhancement() throws Exception {
 		// sanity check that the class is unmodified and does not contain getFieldHandler()
 		assertFalse( implementsManaged( Simple.class ) );
 
 		Class clazz = loader.loadClass( entities.get( 0 ) );
 
 		// enhancement would have added the ManagedEntity interface...
 		assertTrue( implementsManaged( clazz ) );
 	}
 
 	private boolean implementsManaged(Class clazz) {
 		for ( Class intf : clazz.getInterfaces() ) {
 			if ( Managed.class.getName().equals( intf.getName() )
 					|| ManagedEntity.class.getName().equals( intf.getName() )
 					|| ManagedComposite.class.getName().equals( intf.getName() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Tests that methods that were enhanced by javassist have
 	 * StackMapTables for java verification. Without these,
 	 * java.lang.VerifyError's occur in JDK7.
 	 * 
 	 * @throws ClassNotFoundException
 	 * @throws InstantiationException
 	 * @throws IllegalAccessException
 	 * @throws IOException
 	 */
 	@Test
 	@TestForIssue(jiraKey = "HHH-7747")
 	public void testStackMapTableEnhancment() throws ClassNotFoundException,
 			InstantiationException, IllegalAccessException, IOException {
 		byte[] classBytes = loader.loadClassBytes(entities.get(0));
 		ClassPool classPool = new ClassPool();
 		CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
 				classBytes));
 		for (CtMethod ctMethod : ctClass.getMethods()) {
 			//Only check methods that were added by javassist
 			if (ctMethod.getName().startsWith("$javassist_")) {
 				AttributeInfo attributeInfo = ctMethod
 						.getMethodInfo().getCodeAttribute()
 						.getAttribute(StackMapTable.tag);
 				Assert.assertNotNull(attributeInfo);
 				StackMapTable smt = (StackMapTable)attributeInfo;
 				Assert.assertNotNull(smt.get());
 			}
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/AbstractExecutable.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/AbstractExecutable.java
similarity index 89%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/AbstractExecutable.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/AbstractExecutable.java
index f650896100..fdfe88f719 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/AbstractExecutable.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/AbstractExecutable.java
@@ -1,181 +1,192 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.jpa.test.instrument.cases;
+package org.hibernate.jpa.test.enhancement.cases;
 
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import javax.persistence.EntityManager;
 import javax.persistence.SharedCacheMode;
 import javax.persistence.ValidationMode;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 
-import org.hibernate.bytecode.spi.InstrumentedClassLoader;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateEntityManagerFactory;
 import org.hibernate.jpa.HibernatePersistenceProvider;
 import org.hibernate.jpa.boot.spi.Bootstrap;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
 
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestTask;
+
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
-public abstract class AbstractExecutable implements Executable {
+public abstract class AbstractExecutable implements EnhancerTestTask {
 	private static final Dialect dialect = Dialect.getDialect();
 	private HibernateEntityManagerFactory entityManagerFactory;
 	private EntityManager em;
 
     @Override
 	public final void prepare() {
 		// make sure we pick up the TCCL, and make sure its the isolated CL...
 		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
 		if ( classLoader == null ) {
 			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
 		}
-		if ( !InstrumentedClassLoader.class.isInstance( classLoader ) ) {
-			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
-		}
+//		if ( !InstrumentedClassLoader.class.isInstance( classLoader ) ) {
+//			throw new RuntimeException( "Isolated ClassLoader not yet set as TCCL" );
+//		}
 
 		entityManagerFactory =  Bootstrap.getEntityManagerFactoryBuilder(
 				buildPersistenceUnitDescriptor( getClass().getSimpleName() ),
 				buildSettings(),
 				classLoader
 		).build().unwrap( HibernateEntityManagerFactory.class );
+
+		prepared();
 	}
 
-    @Override
+	protected void prepared() {
+
+	}
+
+	@Override
 	public final void complete() {
 		try {
 			cleanup();
 		}
 		finally {
 			if ( em != null && em.isOpen() ) {
 				em.close();
 			}
 			em = null;
 			entityManagerFactory.close();
 			entityManagerFactory = null;
 		}
 	}
 
+	protected HibernateEntityManagerFactory getEntityManagerFactory() {
+		return entityManagerFactory;
+	}
+
 	protected EntityManager getOrCreateEntityManager() {
 		if ( em == null || !em.isOpen() ) {
 			em = entityManagerFactory.createEntityManager();
 		}
 		return em;
 	}
 
 	protected void cleanup() {
 	}
 
 	private Map buildSettings() {
 		Map<Object, Object> settings = Environment.getProperties();
 		ArrayList<Class> classes = new ArrayList<Class>();
 		classes.addAll( Arrays.asList( getAnnotatedClasses() ) );
 		settings.put( AvailableSettings.LOADED_CLASSES, classes );
 		settings.put( org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		settings.put( org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		settings.put( org.hibernate.cfg.AvailableSettings.DIALECT, dialect.getClass().getName() );
 		return settings;
 	}
 
 	private PersistenceUnitDescriptor buildPersistenceUnitDescriptor(final String puName) {
 		return new PersistenceUnitDescriptor() {
 			private final String name = puName;
 
 			@Override public URL getPersistenceUnitRootUrl() {
 				return null;
 			}
 
 			@Override
 			public String getName() {
 				return name;
 			}
 
 			@Override
 			public String getProviderClassName() {
 				return HibernatePersistenceProvider.class.getName();
 			}
 
 			@Override
 			public boolean isUseQuotedIdentifiers() {
 				return false;
 			}
 
 			@Override
 			public boolean isExcludeUnlistedClasses() {
 				return false;
 			}
 
 			@Override
 			public PersistenceUnitTransactionType getTransactionType() {
 				return null;
 			}
 
 			@Override
 			public ValidationMode getValidationMode() {
 				return null;
 			}
 
 			@Override
 			public SharedCacheMode getSharedCacheMode() {
 				return null;
 			}
 
 			@Override
 			public List<String> getManagedClassNames() {
 				return null;
 			}
 
 			@Override
 			public List<String> getMappingFileNames() {
 				return null;
 			}
 
 			@Override
 			public List<URL> getJarFileUrls() {
 				return null;
 			}
 
 			@Override
 			public Object getNonJtaDataSource() {
 				return null;
 			}
 
 			@Override
 			public Object getJtaDataSource() {
 				return null;
 			}
 
 			@Override
 			public Properties getProperties() {
 				return null;
 			}
 
 			@Override
 			public ClassLoader getClassLoader() {
 				return null;
 			}
 
 			@Override
 			public ClassLoader getTempClassLoader() {
 				return null;
 			}
 
 			@Override
 			public void pushClassTransformer(Collection<String> entityClassNames) {
 			}
 		};
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/TestLazyPropertyOnPreUpdateExecutable.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/TestLazyPropertyOnPreUpdateExecutable.java
similarity index 87%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/TestLazyPropertyOnPreUpdateExecutable.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/TestLazyPropertyOnPreUpdateExecutable.java
index c47b854942..ce864be74d 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/TestLazyPropertyOnPreUpdateExecutable.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/TestLazyPropertyOnPreUpdateExecutable.java
@@ -1,112 +1,116 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.jpa.test.instrument.cases;
+package org.hibernate.jpa.test.enhancement.cases;
 
 import java.util.Arrays;
 import javax.persistence.EntityManager;
 
 import org.hibernate.Hibernate;
-import org.hibernate.jpa.test.instrument.domain.EntityWithLazyProperty;
+import org.hibernate.jpa.test.enhancement.cases.domain.EntityWithLazyProperty;
+import org.hibernate.persister.entity.EntityPersister;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  *
  */
 public class TestLazyPropertyOnPreUpdateExecutable extends AbstractExecutable {
+	@Override
+	protected void prepared() {
+		final EntityPersister ep = getEntityManagerFactory().getSessionFactory().getEntityPersister( EntityWithLazyProperty.class.getName() );
+		assertTrue( ep.getInstrumentationMetadata().isEnhancedForLazyLoading() );
+	}
 
 	@Override
-	public void execute() throws Exception {
+	public void execute() {
 		EntityWithLazyProperty entity;
 		EntityManager em = getOrCreateEntityManager();
 
 		byte[] testArray = new byte[]{0x2A};
 
 		//persist the test entity.
 		em.getTransaction().begin();
 		entity = new EntityWithLazyProperty();
 		entity.setSomeField("TEST");
 		entity.setLazyData(testArray);
 		em.persist(entity);
 		em.getTransaction().commit();
 		em.close();
 
 		checkLazyField(entity, em, testArray);
 
 		/**
 		 * Set a non lazy field, therefore the lazyData field will be LazyPropertyInitializer.UNFETCHED_PROPERTY
 		 * for both state and newState so the field should not change. This should no longer cause a ClassCastException.
 		 */
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		entity = em.find(EntityWithLazyProperty.class, entity.getId());
 		entity.setSomeField("TEST1");
 		assertFalse( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		em.getTransaction().commit();
 		assertFalse( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		em.close();
 
 		checkLazyField(entity, em, testArray);
 
 		/**
 		 * Set the updateLazyFieldInPreUpdate flag so that the lazy field is updated from within the
 		 * PreUpdate annotated callback method. So state == LazyPropertyInitializer.UNFETCHED_PROPERTY and
 		 * newState == EntityWithLazyProperty.PRE_UPDATE_VALUE. This should no longer cause a ClassCastException.
 		 */
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		entity = em.find(EntityWithLazyProperty.class, entity.getId());
 		entity.setUpdateLazyFieldInPreUpdate(true);
 		entity.setSomeField("TEST2");
 		assertFalse( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		em.getTransaction().commit();
 		assertTrue( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		em.close();
 
 		checkLazyField(entity, em, EntityWithLazyProperty.PRE_UPDATE_VALUE);
 
 		/**
 		 * Set the updateLazyFieldInPreUpdate flag so that the lazy field is updated from within the
 		 * PreUpdate annotated callback method and also set the lazyData field directly to testArray1. When we reload we
 		 * should get EntityWithLazyProperty.PRE_UPDATE_VALUE.
 		 */
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		entity = em.find(EntityWithLazyProperty.class, entity.getId());
 		entity.setUpdateLazyFieldInPreUpdate(true);
 		assertFalse( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		entity.setLazyData(testArray);
 		assertTrue( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		entity.setSomeField("TEST3");
 		em.getTransaction().commit();
 		em.close();
 
 		checkLazyField( entity, em, EntityWithLazyProperty.PRE_UPDATE_VALUE);
 	}
 
 	private void checkLazyField(EntityWithLazyProperty entity, EntityManager em, byte[] expected) {
 		// reload the entity and check the lazy value matches what we expect.
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		entity = em.find(EntityWithLazyProperty.class, entity.getId());
 		assertFalse( Hibernate.isPropertyInitialized( entity, "lazyData") );
 		assertTrue( Arrays.equals( expected, entity.getLazyData() ) );
 		assertTrue( Hibernate.isPropertyInitialized( entity, "lazyData" ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 
 	@Override
 	public Class[] getAnnotatedClasses() {
-		return new Class[]{
-				EntityWithLazyProperty.class
-		};
+		return new Class[] { EntityWithLazyProperty.class };
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/domain/EntityWithLazyProperty.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/EntityWithLazyProperty.java
similarity index 86%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/domain/EntityWithLazyProperty.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/EntityWithLazyProperty.java
index 504fdab9b5..44125be50c 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/domain/EntityWithLazyProperty.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/EntityWithLazyProperty.java
@@ -1,71 +1,76 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.jpa.test.instrument.domain;
-
-import javax.persistence.*;
+package org.hibernate.jpa.test.enhancement.cases.domain;
+
+import javax.persistence.Basic;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.PreUpdate;
 
 /**
  * Test entity with a lazy property which requires build time instrumentation.
  *
  * @author Martin Ball
  */
 @Entity
 public class EntityWithLazyProperty {
 
     public static final byte[] PRE_UPDATE_VALUE = new byte[]{0x2A, 0x2A, 0x2A, 0x2A};
 
     @Id
     @GeneratedValue
     private Long id;
 
     @Basic(fetch = FetchType.LAZY)
     private byte[] lazyData;
 
     private String someField;
 
     private boolean updateLazyFieldInPreUpdate;
 
     public Long getId() {
         return id;
     }
 
     public void setId(final Long id) {
         this.id = id;
     }
 
     public byte[] getLazyData() {
         return lazyData;
     }
 
     public void setLazyData(final byte[] lazyData) {
         this.lazyData = lazyData;
     }
 
     public String getSomeField() {
         return someField;
     }
 
     public void setSomeField(String someField) {
         this.someField = someField;
     }
 
     public boolean isUpdateLazyFieldInPreUpdate() {
         return updateLazyFieldInPreUpdate;
     }
 
     public void setUpdateLazyFieldInPreUpdate(boolean updateLazyFieldInPreUpdate) {
         this.updateLazyFieldInPreUpdate = updateLazyFieldInPreUpdate;
     }
 
     @PreUpdate
     public void onPreUpdate() {
         //Allow the update of the lazy field from within the pre update to check that this does not break things.
         if(isUpdateLazyFieldInPreUpdate()) {
             this.setLazyData(PRE_UPDATE_VALUE);
         }
     }
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/Simple.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/Simple.java
similarity index 80%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/Simple.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/Simple.java
index 3e8f63a27f..c434b7bea9 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/Simple.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/Simple.java
@@ -1,47 +1,43 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 
 //$Id$
-package org.hibernate.jpa.test.instrument;
+package org.hibernate.jpa.test.enhancement.cases.domain;
 
 import java.util.Collection;
 
 import javax.persistence.Entity;
 
-import org.hibernate.jpa.internal.instrument.InterceptFieldClassFileTransformer;
-
 
 /**
- * A simple entity to be enhanced by the {@link InterceptFieldClassFileTransformer}
- * 
  * @author Emmanuel Bernard
  * @author Dustin Schultz
  */
 @Entity
 public class Simple {
 	private String name;
 	
 	// Have an additional attribute that will ensure that the enhanced classes
 	// will see all class attributes of an entity without CNFEs
 	private Collection<SimpleRelation> relations;
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public Collection<SimpleRelation> getRelations() {
 		return relations;
 	}
 
 	public void setRelations(Collection<SimpleRelation> relations) {
 		this.relations = relations;
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/SimpleRelation.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/SimpleRelation.java
similarity index 90%
rename from hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/SimpleRelation.java
rename to hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/SimpleRelation.java
index 7576366365..8920cefb8d 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/SimpleRelation.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/cases/domain/SimpleRelation.java
@@ -1,27 +1,27 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.jpa.test.instrument;
+package org.hibernate.jpa.test.enhancement.cases.domain;
 
 /**
  * A simple entity relation used by {@link Simple} to ensure that enhanced
  * classes load all classes.
  * 
  * @author Dustin Schultz
  */
 public class SimpleRelation {
 
 	private String blah;
 
 	public String getBlah() {
 		return blah;
 	}
 
 	public void setBlah(String blah) {
 		this.blah = blah;
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/runtime/JpaRuntimeEnhancementTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/runtime/JpaRuntimeEnhancementTest.java
new file mode 100644
index 0000000000..ea90b9a14e
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/enhancement/runtime/JpaRuntimeEnhancementTest.java
@@ -0,0 +1,120 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.jpa.test.enhancement.runtime;
+
+import org.hibernate.jpa.test.enhancement.cases.TestLazyPropertyOnPreUpdateExecutable;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils;
+import org.junit.Test;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JpaRuntimeEnhancementTest extends BaseUnitTestCase {
+//
+//	@Rule
+//	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
+//			new ClassLoadingIsolater.IsolatedClassLoaderProvider() {
+//				@Override
+//				public ClassLoader buildIsolatedClassLoader() {
+//					final EnhancementContext enhancementContext = new DefaultEnhancementContext() {
+//						@Override
+//						public boolean doFieldAccessEnhancement(CtClass classDescriptor) {
+//							return classDescriptor.getPackageName().startsWith( "org.hibernate.jpa.test.enhancement.domain" );
+//						}
+//					};
+//
+//					final Enhancer enhancer = new Enhancer( enhancementContext );
+//
+//					return new InstrumentedClassLoader(
+//							Thread.currentThread().getContextClassLoader(),
+//							new ClassTransformer() {
+//								@Override
+//								public byte[] transform(
+//										ClassLoader loader,
+//										String className,
+//										Class<?> classBeingRedefined,
+//										ProtectionDomain protectionDomain,
+//										byte[] classfileBuffer) throws IllegalClassFormatException {
+//
+//									try {
+//										return enhancer.enhance( className, classfileBuffer );
+//									}
+//									catch (final Exception e) {
+//										throw new IllegalClassFormatException( "Error performing enhancement" ) {
+//											@Override
+//											public synchronized Throwable getCause() {
+//												return e;
+//											}
+//										};
+//									}
+//								}
+//							}
+//					);
+//				}
+//
+//				@Override
+//				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
+//					// nothing to do
+//				}
+//			}
+//	);
+
+
+	// the tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * Test for HHH-7573.
+	 * Load some test data into an entity which has a lazy property and a @PreUpdate callback, then reload and update a
+	 * non lazy field which will trigger the PreUpdate lifecycle callback.
+	 * @throws Exception
+	 */
+	@Test
+	@TestForIssue( jiraKey = "HHH-7573" )
+	public void LazyPropertyOnPreUpdate() throws Exception {
+		EnhancerTestUtils.runEnhancerTestTask( TestLazyPropertyOnPreUpdateExecutable.class );
+	}
+
+//	// reflection code to ensure isolation into the created classloader ~~~~~~~
+//
+//	private static final Class[] SIG = new Class[] {};
+//	private static final Object[] ARGS = new Object[] {};
+//
+//	public void executeExecutable(String name) {
+//		Class execClass = null;
+//		Object executable = null;
+//		try {
+//			execClass = Thread.currentThread().getContextClassLoader().loadClass( name );
+//			executable = execClass.newInstance();
+//		}
+//		catch( Throwable t ) {
+//			throw new HibernateException( "could not load executable", t );
+//		}
+//		try {
+//			execClass.getMethod( "prepare", SIG ).invoke( executable, ARGS );
+//			execClass.getMethod( "execute", SIG ).invoke( executable, ARGS );
+//		}
+//		catch ( NoSuchMethodException e ) {
+//			throw new HibernateException( "could not exeucte executable", e );
+//		}
+//		catch ( IllegalAccessException e ) {
+//			throw new HibernateException( "could not exeucte executable", e );
+//		}
+//		catch ( InvocationTargetException e ) {
+//			throw new HibernateException( "could not exeucte executable", e.getTargetException() );
+//		}
+//		finally {
+//			try {
+//				execClass.getMethod( "complete", SIG ).invoke( executable, ARGS );
+//			}
+//			catch ( Throwable ignore ) {
+//			}
+//		}
+//	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/Executable.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/Executable.java
deleted file mode 100644
index f0c2a5ac33..0000000000
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/cases/Executable.java
+++ /dev/null
@@ -1,19 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.jpa.test.instrument.cases;
-
-import java.util.Map;
-
-/**
- * @author Steve Ebersole
- */
-public interface Executable {
-	public void prepare();
-	public void execute() throws Exception;
-	public void complete();
-	public Class[] getAnnotatedClasses();
-}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
deleted file mode 100644
index 3cc9ea03a8..0000000000
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
+++ /dev/null
@@ -1,112 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.jpa.test.instrument.runtime;
-
-import java.lang.reflect.InvocationTargetException;
-
-import org.junit.Rule;
-import org.junit.Test;
-
-import org.hibernate.HibernateException;
-import org.hibernate.bytecode.buildtime.spi.BasicClassFilter;
-import org.hibernate.bytecode.buildtime.spi.FieldFilter;
-import org.hibernate.bytecode.spi.BytecodeProvider;
-import org.hibernate.bytecode.spi.InstrumentedClassLoader;
-import org.hibernate.testing.TestForIssue;
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-import org.hibernate.testing.junit4.ClassLoadingIsolater;
-
-/**
- * @author Steve Ebersole
- */
-public abstract class AbstractTransformingClassLoaderInstrumentTestCase extends BaseUnitTestCase {
-
-	@Rule
-	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
-			new ClassLoadingIsolater.IsolatedClassLoaderProvider() {
-				final BytecodeProvider provider = buildBytecodeProvider();
-
-				@Override
-				public ClassLoader buildIsolatedClassLoader() {
-					return new InstrumentedClassLoader(
-							Thread.currentThread().getContextClassLoader(),
-							provider.getTransformer(
-									new BasicClassFilter( new String[] { "org.hibernate.jpa.test.instrument" }, null ),
-									new FieldFilter() {
-										public boolean shouldInstrumentField(String className, String fieldName) {
-											return className.startsWith( "org.hibernate.jpa.test.instrument.domain" );
-										}
-										public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName) {
-											return fieldOwnerClassName.startsWith( "org.hibernate.jpa.test.instrument.domain" )
-													&& transformingClassName.equals( fieldOwnerClassName );
-										}
-									}
-							)
-					);
-				}
-
-				@Override
-				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
-					// nothing to do
-				}
-			}
-	);
-
-	protected abstract BytecodeProvider buildBytecodeProvider();
-
-
-	// the tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	/**
-	 * Test for HHH-7573.
-	 * Load some test data into an entity which has a lazy property and a @PreUpdate callback, then reload and update a
-	 * non lazy field which will trigger the PreUpdate lifecycle callback.
-	 * @throws Exception
-	 */
-	@Test
-	@TestForIssue( jiraKey = "HHH-7573" )
-	public void LazyPropertyOnPreUpdate() throws Exception {
-		executeExecutable( "org.hibernate.jpa.test.instrument.cases.TestLazyPropertyOnPreUpdateExecutable" );
-	}
-
-	// reflection code to ensure isolation into the created classloader ~~~~~~~
-
-	private static final Class[] SIG = new Class[] {};
-	private static final Object[] ARGS = new Object[] {};
-
-	public void executeExecutable(String name) {
-		Class execClass = null;
-		Object executable = null;
-		try {
-			execClass = Thread.currentThread().getContextClassLoader().loadClass( name );
-			executable = execClass.newInstance();
-		}
-		catch( Throwable t ) {
-			throw new HibernateException( "could not load executable", t );
-		}
-		try {
-			execClass.getMethod( "prepare", SIG ).invoke( executable, ARGS );
-			execClass.getMethod( "execute", SIG ).invoke( executable, ARGS );
-		}
-		catch ( NoSuchMethodException e ) {
-			throw new HibernateException( "could not exeucte executable", e );
-		}
-		catch ( IllegalAccessException e ) {
-			throw new HibernateException( "could not exeucte executable", e );
-		}
-		catch ( InvocationTargetException e ) {
-			throw new HibernateException( "could not exeucte executable", e.getTargetException() );
-		}
-		finally {
-			try {
-				execClass.getMethod( "complete", SIG ).invoke( executable, ARGS );
-			}
-			catch ( Throwable ignore ) {
-			}
-		}
-	}
-}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/runtime/JavassistInstrumentationTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/runtime/JavassistInstrumentationTest.java
deleted file mode 100644
index 4ef0d4236b..0000000000
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/instrument/runtime/JavassistInstrumentationTest.java
+++ /dev/null
@@ -1,19 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.jpa.test.instrument.runtime;
-
-import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
-import org.hibernate.bytecode.spi.BytecodeProvider;
-
-/**
- * @author Steve Ebersole
- */
-public class JavassistInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
-	protected BytecodeProvider buildBytecodeProvider() {
-		return new BytecodeProviderImpl();
-	}
-}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/DecompileUtils.java b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/DecompileUtils.java
new file mode 100644
index 0000000000..8a432a6eaa
--- /dev/null
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/DecompileUtils.java
@@ -0,0 +1,123 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.testing.bytecode.enhancement;
+
+import java.io.File;
+import javax.tools.JavaFileObject;
+
+import org.hibernate.internal.CoreLogging;
+import org.hibernate.internal.CoreMessageLogger;
+//
+//import com.sun.tools.classfile.ConstantPoolException;
+//import com.sun.tools.javap.JavapTask;
+
+import static org.junit.Assert.assertNull;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * utility class to use in bytecode enhancement tests
+ *
+ * @author Luis Barreiro
+ */
+public abstract class DecompileUtils {
+	private static final CoreMessageLogger log = CoreLogging.messageLogger( DecompileUtils.class );
+
+	// Had to gut this because Java does not make JAVAP available via ToolProvider
+	//		instead on has to rely on tools.jar classes directly :(
+
+	public static void decompileDumpedClass(String workingDir, String className) {
+//		try {
+//			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
+//			StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, null, null );
+//			fileManager.setLocation(
+//					StandardLocation.CLASS_OUTPUT,
+//					Collections.singletonList( new File( workingDir ) )
+//			);
+//
+//			JavapTask javapTask = new JavapTask();
+//			String filename = workingDir + File.separator + getFilenameForClassName( className );
+//			for ( JavaFileObject jfo : fileManager.getJavaFileObjects( filename ) ) {
+//				try {
+//					Set<String> interfaceNames = new HashSet<String>();
+//					Set<String> fieldNames = new HashSet<String>();
+//					Set<String> methodNames = new HashSet<String>();
+//
+//					JavapTask.ClassFileInfo info = javapTask.read( jfo );
+//
+//					log.infof( "decompiled class [%s]", info.cf.getName() );
+//
+//					for ( int i : info.cf.interfaces ) {
+//						interfaceNames.add( info.cf.constant_pool.getClassInfo( i ).getName() );
+//						log.debugf( "declared iFace  = ", info.cf.constant_pool.getClassInfo( i ).getName() );
+//					}
+//					for ( com.sun.tools.classfile.Field f : info.cf.fields ) {
+//						fieldNames.add( f.getName( info.cf.constant_pool ) );
+//						log.debugf( "declared field  = ", f.getName( info.cf.constant_pool ) );
+//					}
+//					for ( com.sun.tools.classfile.Method m : info.cf.methods ) {
+//						methodNames.add( m.getName( info.cf.constant_pool ) );
+//						log.debugf( "declared method = ", m.getName( info.cf.constant_pool ) );
+//					}
+//
+//					// checks signature against known interfaces
+//					if ( interfaceNames.contains( PersistentAttributeInterceptor.class.getName() ) ) {
+//						assertTrue( fieldNames.contains( EnhancerConstants.INTERCEPTOR_FIELD_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.INTERCEPTOR_GETTER_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.INTERCEPTOR_SETTER_NAME ) );
+//					}
+//					if ( interfaceNames.contains( ManagedEntity.class.getName() ) ) {
+//						assertTrue( methodNames.contains( EnhancerConstants.ENTITY_INSTANCE_GETTER_NAME ) );
+//
+//						assertTrue( fieldNames.contains( EnhancerConstants.ENTITY_ENTRY_FIELD_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.ENTITY_ENTRY_GETTER_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.ENTITY_ENTRY_SETTER_NAME ) );
+//
+//						assertTrue( fieldNames.contains( EnhancerConstants.PREVIOUS_FIELD_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.PREVIOUS_GETTER_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.PREVIOUS_SETTER_NAME ) );
+//
+//						assertTrue( fieldNames.contains( EnhancerConstants.NEXT_FIELD_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.NEXT_GETTER_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.NEXT_SETTER_NAME ) );
+//					}
+//					if ( interfaceNames.contains( SelfDirtinessTracker.class.getName() ) ) {
+//						assertTrue( fieldNames.contains( EnhancerConstants.TRACKER_FIELD_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_CHANGER_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_GET_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_CLEAR_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_HAS_CHANGED_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_SUSPEND_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_COLLECTION_GET_NAME ) );
+//					}
+//					if ( interfaceNames.contains( CompositeTracker.class.getName() ) ) {
+//						assertTrue( fieldNames.contains( EnhancerConstants.TRACKER_COMPOSITE_FIELD_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_COMPOSITE_SET_OWNER ) );
+//					}
+//					if ( interfaceNames.contains( CompositeOwner.class.getName() ) ) {
+//						assertTrue( fieldNames.contains( EnhancerConstants.TRACKER_CHANGER_NAME ) );
+//						assertTrue( methodNames.contains( EnhancerConstants.TRACKER_CHANGER_NAME ) );
+//					}
+//				}
+//				catch (ConstantPoolException e) {
+//					e.printStackTrace();
+//				}
+//			}
+//		}
+//		catch (IOException ioe) {
+//			assertNull( "Failed to open class file", ioe );
+//		}
+//		catch (RuntimeException re) {
+//			log.warnf( re, "WARNING: UNABLE DECOMPILE DUE TO %s", re.getMessage() );
+//		}
+	}
+
+	private static String getFilenameForClassName(String className) {
+		return className.replace( '.', File.separatorChar ) + JavaFileObject.Kind.CLASS.extension;
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestContext.java b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestContext.java
similarity index 91%
rename from hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestContext.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestContext.java
index 421ab8113b..619273f66b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestContext.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestContext.java
@@ -1,24 +1,24 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.test.bytecode.enhancement;
+package org.hibernate.testing.bytecode.enhancement;
 
 import javassist.CtClass;
 
 import org.hibernate.bytecode.enhance.spi.DefaultEnhancementContext;
 
 /**
  * Enhancement context used in tests
  *
  * @author Luis Barreiro
  */
 public class EnhancerTestContext extends DefaultEnhancementContext {
 
 	@Override
 	public boolean doFieldAccessEnhancement(CtClass classDescriptor) {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestTask.java b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestTask.java
similarity index 88%
rename from hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestTask.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestTask.java
index a21d0a6d01..fbc2598e0b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestTask.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestTask.java
@@ -1,22 +1,22 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.test.bytecode.enhancement;
+package org.hibernate.testing.bytecode.enhancement;
 
 /**
  * @author Luis Barreiro
  */
 public interface EnhancerTestTask {
 
 	Class<?>[] getAnnotatedClasses();
 
 	void prepare();
 
 	void execute();
 
 	void complete();
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestUtils.java
similarity index 99%
rename from hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java
rename to hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestUtils.java
index ca97c4a786..3df21bb923 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/enhancement/EnhancerTestUtils.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/bytecode/enhancement/EnhancerTestUtils.java
@@ -1,221 +1,221 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.test.bytecode.enhancement;
+package org.hibernate.testing.bytecode.enhancement;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.Field;
 import java.util.Arrays;
 
 import javassist.ClassPool;
 import javassist.CtClass;
 import javassist.LoaderClassPath;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.enhance.spi.EnhancementContext;
 import org.hibernate.bytecode.enhance.spi.Enhancer;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * utility class to use in bytecode enhancement tests
  *
  * @author Steve Ebersole
  */
 public abstract class EnhancerTestUtils extends BaseUnitTestCase {
 
 	private static EnhancementContext enhancementContext = new EnhancerTestContext();
 
 	private static String workingDir = System.getProperty( "java.io.tmpdir" );
 
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( EnhancerTestUtils.class );
 
 	/**
 	 * method that performs the enhancement of a class
 	 * also checks the signature of enhanced entities methods using 'javap' decompiler
 	 */
 	public static Class<?> enhanceAndDecompile(Class<?> classToEnhance, ClassLoader cl) throws Exception {
 		CtClass entityCtClass = generateCtClassForAnEntity( classToEnhance );
 
 		byte[] original = entityCtClass.toBytecode();
 		byte[] enhanced = new Enhancer( enhancementContext ).enhance( entityCtClass.getName(), original );
 		assertFalse( "entity was not enhanced", Arrays.equals( original, enhanced ) );
 		log.infof( "enhanced entity [%s]", entityCtClass.getName() );
 
 		ClassPool cp = new ClassPool( false );
 		cp.appendClassPath( new LoaderClassPath( cl ) );
 		CtClass enhancedCtClass = cp.makeClass( new ByteArrayInputStream( enhanced ) );
 
 		enhancedCtClass.debugWriteFile( workingDir );
 		DecompileUtils.decompileDumpedClass( workingDir, classToEnhance.getName() );
 
 		Class<?> enhancedClass = enhancedCtClass.toClass( cl, EnhancerTestUtils.class.getProtectionDomain() );
 		assertNotNull( enhancedClass );
 		return enhancedClass;
 	}
 
 	private static CtClass generateCtClassForAnEntity(Class<?> entityClassToEnhance) throws Exception {
 		ClassPool cp = new ClassPool( false );
 		ClassLoader cl = EnhancerTestUtils.class.getClassLoader();
 		return cp.makeClass( cl.getResourceAsStream( entityClassToEnhance.getName().replace( '.', '/' ) + ".class" ) );
 	}
 	/* --- */
 
 	@SuppressWarnings("unchecked")
 	public static void runEnhancerTestTask(final Class<? extends EnhancerTestTask> task) {
 
 		EnhancerTestTask taskObject = null;
 		ClassLoader defaultCL = Thread.currentThread().getContextClassLoader();
 		try {
 			ClassLoader cl = EnhancerTestUtils.getEnhancerClassLoader( task.getPackage().getName() );
 			EnhancerTestUtils.setupClassLoader( cl, task );
 			EnhancerTestUtils.setupClassLoader( cl, task.newInstance().getAnnotatedClasses() );
 
 			Thread.currentThread().setContextClassLoader( cl );
 			taskObject = ( (Class<? extends EnhancerTestTask>) cl.loadClass( task.getName() ) ).newInstance();
 
 			taskObject.prepare();
 			taskObject.execute();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not execute task", e );
 		}
 		finally {
 			try {
 				if ( taskObject != null ) {
 					taskObject.complete();
 				}
 			}
 			catch (Throwable ignore) {
 			}
 			Thread.currentThread().setContextClassLoader( defaultCL );
 		}
 	}
 
 	private static void setupClassLoader(ClassLoader cl, Class<?>... classesToLoad) {
 		for ( Class<?> classToLoad : classesToLoad ) {
 			try {
 				cl.loadClass( classToLoad.getName() );
 			}
 			catch (ClassNotFoundException e) {
 				e.printStackTrace();
 			}
 		}
 	}
 
 	private static ClassLoader getEnhancerClassLoader(final String packageName) {
 		return new ClassLoader() {
 			@Override
 			public Class<?> loadClass(String name) throws ClassNotFoundException {
 				if ( !name.startsWith( packageName ) ) {
 					return getParent().loadClass( name );
 				}
 				final Class c = findLoadedClass( name );
 				if ( c != null ) {
 					return c;
 				}
 
 				final InputStream is = this.getResourceAsStream(  name.replace( '.', '/' ) + ".class" );
 				if ( is == null ) {
 					throw new ClassNotFoundException( name + " not found" );
 				}
 
 				try {
 					final byte[] original = new byte[is.available()];
 					new BufferedInputStream( is ).read( original );
 
 					final byte[] enhanced = new Enhancer( enhancementContext ).enhance( name, original );
 
 					File f = new File( workingDir + File.separator + name.replace( ".", File.separator ) + ".class" );
 					f.getParentFile().mkdirs();
 					f.createNewFile();
 					FileOutputStream out = new FileOutputStream( f );
 					out.write( enhanced );
 					out.close();
 
 					return defineClass( name, enhanced, 0, enhanced.length );
 				}
 				catch (Throwable t) {
 					throw new ClassNotFoundException( name + " not found", t );
 				} finally {
 					try {
 						is.close();
 					}
 					catch (IOException e) { // ignore
 					}
 				}
 			}
 		};
 	}
 
 	public static Object getFieldByReflection(Object entity, String fieldName) {
 		try {
 			Field field =  entity.getClass().getDeclaredField( fieldName );
 			field.setAccessible( true );
 			return field.get( entity );
 		}
 		catch (NoSuchFieldException e) {
 			fail( "Fail to get field '" + fieldName + "' in entity " + entity );
 		}
 		catch (IllegalAccessException e) {
 			fail( "Fail to get field '" + fieldName + "' in entity " + entity );
 		}
 		return null;
 	}
 
 	/**
 	 * clears the dirty set for an entity
 	 */
 	public static void clearDirtyTracking(Object entityInstance) {
 		( (SelfDirtinessTracker) entityInstance ).$$_hibernate_clearDirtyAttributes();
 	}
 
 	/**
 	 * compares the dirty fields of an entity with a set of expected values
 	 */
 	public static void checkDirtyTracking(Object entityInstance, String... dirtyFields) {
 		final SelfDirtinessTracker selfDirtinessTracker = (SelfDirtinessTracker) entityInstance;
 		assertEquals( dirtyFields.length > 0, selfDirtinessTracker.$$_hibernate_hasDirtyAttributes() );
 		String[] tracked = selfDirtinessTracker.$$_hibernate_getDirtyAttributes();
 		assertEquals( dirtyFields.length, tracked.length );
 		assertTrue( Arrays.asList( tracked ).containsAll( Arrays.asList( dirtyFields ) ) );
 	}
 
 	public static EntityEntry makeEntityEntry() {
 		return MutableEntityEntryFactory.INSTANCE.createEntityEntry(
 				Status.MANAGED,
 				null,
 				null,
 				1,
 				null,
 				LockMode.NONE,
 				false,
 				null,
 				false,
 				false,
 				null
 		);
 	}
 
 }
