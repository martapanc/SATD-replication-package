75/report.java
Satd-method: private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
********************************************
********************************************
75/Between/ HHH-1904  06ab0652_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-2304  bcae5600_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-2394  05dcc209_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		final String tableName = filterElement.attributeValue("table");
+		Iterator aliasesIterator = filterElement.elementIterator("aliases");
+		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
+		while (aliasesIterator.hasNext()){
+			Element alias = (Element) aliasesIterator.next();
+			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
+		}
-		filterable.addFilter( name, tableName, condition );
+		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
+		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
+		filterable.addFilter(name, condition, autoAliasInjection, aliasTables);

Lines added: 9. Lines removed: 2. Tot = 11
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	-	public void addFilter(String name, String condition) {
-	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
75/Between/ HHH-2394  5cb8d3a8_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		filterable.addFilter(name, condition, autoAliasInjection, aliasTables);
+		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(Filter filter) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-2394  ee01d806_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		final String tableName = filterElement.attributeValue("table");
-		filterable.addFilter( name, condition );
+		filterable.addFilter( name, tableName, condition );

Lines added: 2. Lines removed: 1. Tot = 3
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-2907  77825fef_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-2907  fd57a751_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-4358  23a62802_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-5916  4ffba763_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-5916  ddfcc44d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-5916  e18799b0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-5986  0816d00e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6097  ad17f89c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6098  6504cb6d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.SessionBuilder;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionImpl.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.event.EventListeners;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, StatelessSessionImpl.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, StatelessSessionImpl.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/HibernateLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
-package org.hibernate;
+package org.hibernate.internal;
+
+import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
- * Defines internationalized messages for this hibernate-core, with IDs ranging from 00001 to 10000 inclusively. New messages must
- * be added after the last message defined to ensure message codes are unique.
+ * The jboss-logging {@link MessageLogger} for the hibernate-core module.  It reserves message ids ranging from
+ * 00001 to 10000 inclusively.
+ * <p/>
+ * New messages must be added after the last message defined to ensure message codes are unique.
-// TODO: tracef
-public interface HibernateLogger extends BasicLogger {
+public interface CoreMessageLogger extends BasicLogger {
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ConfigHelper.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ConfigHelper.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SerializationHelper.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SerializationHelper.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JndiHelper.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JndiHelper.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, DTDEntityResolver.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DTDEntityResolver.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/ErrorLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/ErrorLogger.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ErrorLogger.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ErrorLogger.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/MappingReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/MappingReader.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, MappingReader.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, MappingReader.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, XMLHelper.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, XMLHelper.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/Expectations.java
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
-package org.hibernate.jdbc;
-
-import java.sql.CallableStatement;
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import java.sql.Types;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.StaleStateException;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
-import org.hibernate.exception.GenericJDBCException;
-import org.jboss.logging.Logger;
-
-/**
- * Holds various often used {@link Expectation} definitions.
- *
- * @author Steve Ebersole
- */
-public class Expectations {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Expectations.class.getName());
-	private static SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
-
-	public static final int USUAL_EXPECTED_COUNT = 1;
-	public static final int USUAL_PARAM_POSITION = 1;
-
-
-	// Base Expectation impls ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public static class BasicExpectation implements Expectation {
-		private final int expectedRowCount;
-
-		protected BasicExpectation(int expectedRowCount) {
-			this.expectedRowCount = expectedRowCount;
-			if ( expectedRowCount < 0 ) {
-				throw new IllegalArgumentException( "Expected row count must be greater than zero" );
-			}
-		}
-
-		public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {
-			rowCount = determineRowCount( rowCount, statement );
-			if ( batchPosition < 0 ) {
-				checkNonBatched( rowCount );
-			}
-			else {
-				checkBatched( rowCount, batchPosition );
-			}
-		}
-
-		private void checkBatched(int rowCount, int batchPosition) {
-            if (rowCount == -2) LOG.debugf("Success of batch update unknown: %s", batchPosition);
-            else if (rowCount == -3) throw new BatchFailedException("Batch update failed: " + batchPosition);
-			else {
-                if (expectedRowCount > rowCount) throw new StaleStateException(
-                                                                               "Batch update returned unexpected row count from update ["
-                                                                               + batchPosition + "]; actual row count: " + rowCount
-                                                                               + "; expected: " + expectedRowCount);
-				if ( expectedRowCount < rowCount ) {
-					String msg = "Batch update returned unexpected row count from update [" +
-					             batchPosition + "]; actual row count: " + rowCount +
-					             "; expected: " + expectedRowCount;
-					throw new BatchedTooManyRowsAffectedException( msg, expectedRowCount, rowCount, batchPosition );
-				}
-			}
-		}
-
-		private void checkNonBatched(int rowCount) {
-			if ( expectedRowCount > rowCount ) {
-				throw new StaleStateException(
-						"Unexpected row count: " + rowCount + "; expected: " + expectedRowCount
-				);
-			}
-			if ( expectedRowCount < rowCount ) {
-				String msg = "Unexpected row count: " + rowCount + "; expected: " + expectedRowCount;
-				throw new TooManyRowsAffectedException( msg, expectedRowCount, rowCount );
-			}
-		}
-
-		public int prepare(PreparedStatement statement) throws SQLException, HibernateException {
-			return 0;
-		}
-
-		public boolean canBeBatched() {
-			return true;
-		}
-
-		protected int determineRowCount(int reportedRowCount, PreparedStatement statement) {
-			return reportedRowCount;
-		}
-	}
-
-	public static class BasicParamExpectation extends BasicExpectation {
-		private final int parameterPosition;
-		protected BasicParamExpectation(int expectedRowCount, int parameterPosition) {
-			super( expectedRowCount );
-			this.parameterPosition = parameterPosition;
-		}
-
-		@Override
-        public int prepare(PreparedStatement statement) throws SQLException, HibernateException {
-			toCallableStatement( statement ).registerOutParameter( parameterPosition, Types.NUMERIC );
-			return 1;
-		}
-
-		@Override
-        public boolean canBeBatched() {
-			return false;
-		}
-
-		@Override
-        protected int determineRowCount(int reportedRowCount, PreparedStatement statement) {
-			try {
-				return toCallableStatement( statement ).getInt( parameterPosition );
-			}
-			catch( SQLException sqle ) {
-				sqlExceptionHelper.logExceptions( sqle, "could not extract row counts from CallableStatement" );
-				throw new GenericJDBCException( "could not extract row counts from CallableStatement", sqle );
-			}
-		}
-
-		private CallableStatement toCallableStatement(PreparedStatement statement) {
-			if ( ! CallableStatement.class.isInstance( statement ) ) {
-				throw new HibernateException( "BasicParamExpectation operates exclusively on CallableStatements : " + statement.getClass() );
-			}
-			return ( CallableStatement ) statement;
-		}
-	}
-
-
-	// Various Expectation instances ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public static final Expectation NONE = new Expectation() {
-		public void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {
-			// explicitly doAfterTransactionCompletion no checking...
-		}
-
-		public int prepare(PreparedStatement statement) {
-			return 0;
-		}
-
-		public boolean canBeBatched() {
-			return true;
-		}
-	};
-
-	public static final Expectation BASIC = new BasicExpectation( USUAL_EXPECTED_COUNT );
-
-	public static final Expectation PARAM = new BasicParamExpectation( USUAL_EXPECTED_COUNT, USUAL_PARAM_POSITION );
-
-
-	public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {
-		if ( style == ExecuteUpdateResultCheckStyle.NONE ) {
-			return NONE;
-		}
-		else if ( style == ExecuteUpdateResultCheckStyle.COUNT ) {
-			return BASIC;
-		}
-		else if ( style == ExecuteUpdateResultCheckStyle.PARAM ) {
-			return PARAM;
-		}
-		else {
-			throw new HibernateException( "unknown check style : " + style );
-		}
-	}
-
-	private Expectations() {
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.jdbc;
+
+import java.sql.CallableStatement;
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.sql.Types;
+import org.hibernate.HibernateException;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.StaleStateException;
+import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.exception.GenericJDBCException;
+
+import org.jboss.logging.Logger;
+
+/**
+ * Holds various often used {@link Expectation} definitions.
+ *
+ * @author Steve Ebersole
+ */
+public class Expectations {
+
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Expectations.class.getName());
+	private static SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
+
+	public static final int USUAL_EXPECTED_COUNT = 1;
+	public static final int USUAL_PARAM_POSITION = 1;
+
+
+	// Base Expectation impls ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public static class BasicExpectation implements Expectation {
+		private final int expectedRowCount;
+
+		protected BasicExpectation(int expectedRowCount) {
+			this.expectedRowCount = expectedRowCount;
+			if ( expectedRowCount < 0 ) {
+				throw new IllegalArgumentException( "Expected row count must be greater than zero" );
+			}
+		}
+
+		public final void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {
+			rowCount = determineRowCount( rowCount, statement );
+			if ( batchPosition < 0 ) {
+				checkNonBatched( rowCount );
+			}
+			else {
+				checkBatched( rowCount, batchPosition );
+			}
+		}
+
+		private void checkBatched(int rowCount, int batchPosition) {
+            if (rowCount == -2) LOG.debugf("Success of batch update unknown: %s", batchPosition);
+            else if (rowCount == -3) throw new BatchFailedException("Batch update failed: " + batchPosition);
+			else {
+                if (expectedRowCount > rowCount) throw new StaleStateException(
+                                                                               "Batch update returned unexpected row count from update ["
+                                                                               + batchPosition + "]; actual row count: " + rowCount
+                                                                               + "; expected: " + expectedRowCount);
+				if ( expectedRowCount < rowCount ) {
+					String msg = "Batch update returned unexpected row count from update [" +
+					             batchPosition + "]; actual row count: " + rowCount +
+					             "; expected: " + expectedRowCount;
+					throw new BatchedTooManyRowsAffectedException( msg, expectedRowCount, rowCount, batchPosition );
+				}
+			}
+		}
+
+		private void checkNonBatched(int rowCount) {
+			if ( expectedRowCount > rowCount ) {
+				throw new StaleStateException(
+						"Unexpected row count: " + rowCount + "; expected: " + expectedRowCount
+				);
+			}
+			if ( expectedRowCount < rowCount ) {
+				String msg = "Unexpected row count: " + rowCount + "; expected: " + expectedRowCount;
+				throw new TooManyRowsAffectedException( msg, expectedRowCount, rowCount );
+			}
+		}
+
+		public int prepare(PreparedStatement statement) throws SQLException, HibernateException {
+			return 0;
+		}
+
+		public boolean canBeBatched() {
+			return true;
+		}
+
+		protected int determineRowCount(int reportedRowCount, PreparedStatement statement) {
+			return reportedRowCount;
+		}
+	}
+
+	public static class BasicParamExpectation extends BasicExpectation {
+		private final int parameterPosition;
+		protected BasicParamExpectation(int expectedRowCount, int parameterPosition) {
+			super( expectedRowCount );
+			this.parameterPosition = parameterPosition;
+		}
+
+		@Override
+        public int prepare(PreparedStatement statement) throws SQLException, HibernateException {
+			toCallableStatement( statement ).registerOutParameter( parameterPosition, Types.NUMERIC );
+			return 1;
+		}
+
+		@Override
+        public boolean canBeBatched() {
+			return false;
+		}
+
+		@Override
+        protected int determineRowCount(int reportedRowCount, PreparedStatement statement) {
+			try {
+				return toCallableStatement( statement ).getInt( parameterPosition );
+			}
+			catch( SQLException sqle ) {
+				sqlExceptionHelper.logExceptions( sqle, "could not extract row counts from CallableStatement" );
+				throw new GenericJDBCException( "could not extract row counts from CallableStatement", sqle );
+			}
+		}
+
+		private CallableStatement toCallableStatement(PreparedStatement statement) {
+			if ( ! CallableStatement.class.isInstance( statement ) ) {
+				throw new HibernateException( "BasicParamExpectation operates exclusively on CallableStatements : " + statement.getClass() );
+			}
+			return ( CallableStatement ) statement;
+		}
+	}
+
+
+	// Various Expectation instances ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public static final Expectation NONE = new Expectation() {
+		public void verifyOutcome(int rowCount, PreparedStatement statement, int batchPosition) {
+			// explicitly doAfterTransactionCompletion no checking...
+		}
+
+		public int prepare(PreparedStatement statement) {
+			return 0;
+		}
+
+		public boolean canBeBatched() {
+			return true;
+		}
+	};
+
+	public static final Expectation BASIC = new BasicExpectation( USUAL_EXPECTED_COUNT );
+
+	public static final Expectation PARAM = new BasicParamExpectation( USUAL_EXPECTED_COUNT, USUAL_PARAM_POSITION );
+
+
+	public static Expectation appropriateExpectation(ExecuteUpdateResultCheckStyle style) {
+		if ( style == ExecuteUpdateResultCheckStyle.NONE ) {
+			return NONE;
+		}
+		else if ( style == ExecuteUpdateResultCheckStyle.COUNT ) {
+			return BASIC;
+		}
+		else if ( style == ExecuteUpdateResultCheckStyle.PARAM ) {
+			return PARAM;
+		}
+		else {
+			throw new HibernateException( "unknown check style : " + style );
+		}
+	}
+
+	private Expectations() {
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, HibernateService.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HibernateService.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionFactoryStub.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryStub.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, StatisticsService.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, StatisticsService.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    protected static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Loader.class.getName());
+    protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/BasicCollectionLoader.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicCollectionLoader.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicCollectionLoader.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/OneToManyLoader.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, OneToManyLoader.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, OneToManyLoader.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLCustomQuery.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SQLCustomQuery.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SQLCustomQuery.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
-import java.util.ArrayList;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Map;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.MappingException;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.query.sql.NativeSQLQueryCollectionReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryNonScalarReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
-import org.hibernate.loader.BasicLoader;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.ColumnEntityAliases;
-import org.hibernate.loader.DefaultEntityAliases;
-import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.GeneratedCollectionAliases;
-import org.hibernate.loader.custom.CollectionFetchReturn;
-import org.hibernate.loader.custom.CollectionReturn;
-import org.hibernate.loader.custom.ColumnCollectionAliases;
-import org.hibernate.loader.custom.EntityFetchReturn;
-import org.hibernate.loader.custom.FetchReturn;
-import org.hibernate.loader.custom.NonScalarReturn;
-import org.hibernate.loader.custom.Return;
-import org.hibernate.loader.custom.RootReturn;
-import org.hibernate.loader.custom.ScalarReturn;
-import org.hibernate.persister.collection.SQLLoadableCollection;
-import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.SQLLoadable;
-import org.hibernate.type.EntityType;
-import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
-
-/**
- * Responsible for processing the series of {@link org.hibernate.engine.query.sql.NativeSQLQueryReturn returns}
- * defined by a {@link org.hibernate.engine.query.sql.NativeSQLQuerySpecification} and
- * breaking them down into a series of {@link Return returns} for use within the
- * {@link org.hibernate.loader.custom.CustomLoader}.
- *
- * @author Gavin King
- * @author Max Andersen
- * @author Steve Ebersole
- */
-public class SQLQueryReturnProcessor {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       SQLQueryReturnProcessor.class.getName());
-
-	private NativeSQLQueryReturn[] queryReturns;
-
-//	private final List persisters = new ArrayList();
-
-	private final Map alias2Return = new HashMap();
-	private final Map alias2OwnerAlias = new HashMap();
-
-	private final Map alias2Persister = new HashMap();
-	private final Map alias2Suffix = new HashMap();
-
-	private final Map alias2CollectionPersister = new HashMap();
-	private final Map alias2CollectionSuffix = new HashMap();
-
-	private final Map entityPropertyResultMaps = new HashMap();
-	private final Map collectionPropertyResultMaps = new HashMap();
-
-//	private final List scalarTypes = new ArrayList();
-//	private final List scalarColumnAliases = new ArrayList();
-
-	private final SessionFactoryImplementor factory;
-
-//	private List collectionOwnerAliases = new ArrayList();
-//	private List collectionAliases = new ArrayList();
-//	private List collectionPersisters = new ArrayList();
-//	private List collectionResults = new ArrayList();
-
-	private int entitySuffixSeed = 0;
-	private int collectionSuffixSeed = 0;
-
-
-	public SQLQueryReturnProcessor(NativeSQLQueryReturn[] queryReturns, SessionFactoryImplementor factory) {
-		this.queryReturns = queryReturns;
-		this.factory = factory;
-	}
-
-	/*package*/ class ResultAliasContext {
-		public SQLLoadable getEntityPersister(String alias) {
-			return ( SQLLoadable ) alias2Persister.get( alias );
-		}
-
-		public SQLLoadableCollection getCollectionPersister(String alias) {
-			return ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
-		}
-
-		public String getEntitySuffix(String alias) {
-			return ( String ) alias2Suffix.get( alias );
-		}
-
-		public String getCollectionSuffix(String alias) {
-			return ( String ) alias2CollectionSuffix.get ( alias );
-		}
-
-		public String getOwnerAlias(String alias) {
-			return ( String ) alias2OwnerAlias.get( alias );
-		}
-
-		public Map getPropertyResultsMap(String alias) {
-			return internalGetPropertyResultsMap( alias );
-		}
-	}
-
-	private Map internalGetPropertyResultsMap(String alias) {
-		NativeSQLQueryReturn rtn = ( NativeSQLQueryReturn ) alias2Return.get( alias );
-		if ( rtn instanceof NativeSQLQueryNonScalarReturn ) {
-			return ( ( NativeSQLQueryNonScalarReturn ) rtn ).getPropertyResultsMap();
-		}
-		else {
-			return null;
-		}
-	}
-
-	private boolean hasPropertyResultMap(String alias) {
-		Map propertyMaps = internalGetPropertyResultsMap( alias );
-		return propertyMaps != null && ! propertyMaps.isEmpty();
-	}
-
-	public ResultAliasContext process() {
-		// first, break down the returns into maps keyed by alias
-		// so that role returns can be more easily resolved to their owners
-		for ( int i = 0; i < queryReturns.length; i++ ) {
-			if ( queryReturns[i] instanceof NativeSQLQueryNonScalarReturn ) {
-				NativeSQLQueryNonScalarReturn rtn = ( NativeSQLQueryNonScalarReturn ) queryReturns[i];
-				alias2Return.put( rtn.getAlias(), rtn );
-				if ( rtn instanceof NativeSQLQueryJoinReturn ) {
-					NativeSQLQueryJoinReturn fetchReturn = ( NativeSQLQueryJoinReturn ) rtn;
-					alias2OwnerAlias.put( fetchReturn.getAlias(), fetchReturn.getOwnerAlias() );
-				}
-			}
-		}
-
-		// Now, process the returns
-		for ( int i = 0; i < queryReturns.length; i++ ) {
-			processReturn( queryReturns[i] );
-		}
-
-		return new ResultAliasContext();
-	}
-
-	public List generateCustomReturns(boolean queryHadAliases) {
-		List customReturns = new ArrayList();
-		Map customReturnsByAlias = new HashMap();
-		for ( int i = 0; i < queryReturns.length; i++ ) {
-			if ( queryReturns[i] instanceof NativeSQLQueryScalarReturn ) {
-				NativeSQLQueryScalarReturn rtn = ( NativeSQLQueryScalarReturn ) queryReturns[i];
-				customReturns.add( new ScalarReturn( rtn.getType(), rtn.getColumnAlias() ) );
-			}
-			else if ( queryReturns[i] instanceof NativeSQLQueryRootReturn ) {
-				NativeSQLQueryRootReturn rtn = ( NativeSQLQueryRootReturn ) queryReturns[i];
-				String alias = rtn.getAlias();
-				EntityAliases entityAliases;
-				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
-					entityAliases = new DefaultEntityAliases(
-							( Map ) entityPropertyResultMaps.get( alias ),
-							( SQLLoadable ) alias2Persister.get( alias ),
-							( String ) alias2Suffix.get( alias )
-					);
-				}
-				else {
-					entityAliases = new ColumnEntityAliases(
-							( Map ) entityPropertyResultMaps.get( alias ),
-							( SQLLoadable ) alias2Persister.get( alias ),
-							( String ) alias2Suffix.get( alias )
-					);
-				}
-				RootReturn customReturn = new RootReturn(
-						alias,
-						rtn.getReturnEntityName(),
-						entityAliases,
-						rtn.getLockMode()
-				);
-				customReturns.add( customReturn );
-				customReturnsByAlias.put( rtn.getAlias(), customReturn );
-			}
-			else if ( queryReturns[i] instanceof NativeSQLQueryCollectionReturn ) {
-				NativeSQLQueryCollectionReturn rtn = ( NativeSQLQueryCollectionReturn ) queryReturns[i];
-				String alias = rtn.getAlias();
-				SQLLoadableCollection persister = ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
-				boolean isEntityElements = persister.getElementType().isEntityType();
-				CollectionAliases collectionAliases;
-				EntityAliases elementEntityAliases = null;
-				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
-					collectionAliases = new GeneratedCollectionAliases(
-							( Map ) collectionPropertyResultMaps.get( alias ),
-							( SQLLoadableCollection ) alias2CollectionPersister.get( alias ),
-							( String ) alias2CollectionSuffix.get( alias )
-					);
-					if ( isEntityElements ) {
-						elementEntityAliases = new DefaultEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
-						);
-					}
-				}
-				else {
-					collectionAliases = new ColumnCollectionAliases(
-							( Map ) collectionPropertyResultMaps.get( alias ),
-							( SQLLoadableCollection ) alias2CollectionPersister.get( alias )
-					);
-					if ( isEntityElements ) {
-						elementEntityAliases = new ColumnEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
-						);
-					}
-				}
-				CollectionReturn customReturn = new CollectionReturn(
-						alias,
-						rtn.getOwnerEntityName(),
-						rtn.getOwnerProperty(),
-						collectionAliases,
-				        elementEntityAliases,
-						rtn.getLockMode()
-				);
-				customReturns.add( customReturn );
-				customReturnsByAlias.put( rtn.getAlias(), customReturn );
-			}
-			else if ( queryReturns[i] instanceof NativeSQLQueryJoinReturn ) {
-				NativeSQLQueryJoinReturn rtn = ( NativeSQLQueryJoinReturn ) queryReturns[i];
-				String alias = rtn.getAlias();
-				FetchReturn customReturn;
-				NonScalarReturn ownerCustomReturn = ( NonScalarReturn ) customReturnsByAlias.get( rtn.getOwnerAlias() );
-				if ( alias2CollectionPersister.containsKey( alias ) ) {
-					SQLLoadableCollection persister = ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
-					boolean isEntityElements = persister.getElementType().isEntityType();
-					CollectionAliases collectionAliases;
-					EntityAliases elementEntityAliases = null;
-					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
-						collectionAliases = new GeneratedCollectionAliases(
-								( Map ) collectionPropertyResultMaps.get( alias ),
-								persister,
-								( String ) alias2CollectionSuffix.get( alias )
-						);
-						if ( isEntityElements ) {
-							elementEntityAliases = new DefaultEntityAliases(
-									( Map ) entityPropertyResultMaps.get( alias ),
-									( SQLLoadable ) alias2Persister.get( alias ),
-									( String ) alias2Suffix.get( alias )
-							);
-						}
-					}
-					else {
-						collectionAliases = new ColumnCollectionAliases(
-								( Map ) collectionPropertyResultMaps.get( alias ),
-								persister
-						);
-						if ( isEntityElements ) {
-							elementEntityAliases = new ColumnEntityAliases(
-									( Map ) entityPropertyResultMaps.get( alias ),
-									( SQLLoadable ) alias2Persister.get( alias ),
-									( String ) alias2Suffix.get( alias )
-							);
-						}
-					}
-					customReturn = new CollectionFetchReturn(
-							alias,
-							ownerCustomReturn,
-							rtn.getOwnerProperty(),
-							collectionAliases,
-					        elementEntityAliases,
-							rtn.getLockMode()
-					);
-				}
-				else {
-					EntityAliases entityAliases;
-					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
-						entityAliases = new DefaultEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
-						);
-					}
-					else {
-						entityAliases = new ColumnEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
-						);
-					}
-					customReturn = new EntityFetchReturn(
-							alias,
-							entityAliases,
-							ownerCustomReturn,
-							rtn.getOwnerProperty(),
-							rtn.getLockMode()
-					);
-				}
-				customReturns.add( customReturn );
-				customReturnsByAlias.put( alias, customReturn );
-			}
-		}
-		return customReturns;
-	}
-
-	private SQLLoadable getSQLLoadable(String entityName) throws MappingException {
-		EntityPersister persister = factory.getEntityPersister( entityName );
-		if ( !(persister instanceof SQLLoadable) ) {
-			throw new MappingException( "class persister is not SQLLoadable: " + entityName );
-		}
-		return (SQLLoadable) persister;
-	}
-
-	private String generateEntitySuffix() {
-		return BasicLoader.generateSuffixes( entitySuffixSeed++, 1 )[0];
-	}
-
-	private String generateCollectionSuffix() {
-		return collectionSuffixSeed++ + "__";
-	}
-
-	private void processReturn(NativeSQLQueryReturn rtn) {
-		if ( rtn instanceof NativeSQLQueryScalarReturn ) {
-			processScalarReturn( ( NativeSQLQueryScalarReturn ) rtn );
-		}
-		else if ( rtn instanceof NativeSQLQueryRootReturn ) {
-			processRootReturn( ( NativeSQLQueryRootReturn ) rtn );
-		}
-		else if ( rtn instanceof NativeSQLQueryCollectionReturn ) {
-			processCollectionReturn( ( NativeSQLQueryCollectionReturn ) rtn );
-		}
-		else {
-			processJoinReturn( ( NativeSQLQueryJoinReturn ) rtn );
-		}
-	}
-
-	private void processScalarReturn(NativeSQLQueryScalarReturn typeReturn) {
-//		scalarColumnAliases.add( typeReturn.getColumnAlias() );
-//		scalarTypes.add( typeReturn.getType() );
-	}
-
-	private void processRootReturn(NativeSQLQueryRootReturn rootReturn) {
-		if ( alias2Persister.containsKey( rootReturn.getAlias() ) ) {
-			// already been processed...
-			return;
-		}
-
-		SQLLoadable persister = getSQLLoadable( rootReturn.getReturnEntityName() );
-		addPersister( rootReturn.getAlias(), rootReturn.getPropertyResultsMap(), persister );
-	}
-
-	/**
-	 * @param propertyResult
-	 * @param persister
-	 */
-	private void addPersister(String alias, Map propertyResult, SQLLoadable persister) {
-		alias2Persister.put( alias, persister );
-		String suffix = generateEntitySuffix();
-        LOG.trace("Mapping alias [" + alias + "] to entity-suffix [" + suffix + "]");
-		alias2Suffix.put( alias, suffix );
-		entityPropertyResultMaps.put( alias, propertyResult );
-	}
-
-	private void addCollection(String role, String alias, Map propertyResults) {
-		SQLLoadableCollection collectionPersister = ( SQLLoadableCollection ) factory.getCollectionPersister( role );
-		alias2CollectionPersister.put( alias, collectionPersister );
-		String suffix = generateCollectionSuffix();
-        LOG.trace("Mapping alias [" + alias + "] to collection-suffix [" + suffix + "]");
-		alias2CollectionSuffix.put( alias, suffix );
-		collectionPropertyResultMaps.put( alias, propertyResults );
-
-		if ( collectionPersister.isOneToMany() || collectionPersister.isManyToMany() ) {
-			SQLLoadable persister = ( SQLLoadable ) collectionPersister.getElementPersister();
-			addPersister( alias, filter( propertyResults ), persister );
-		}
-	}
-
-	private Map filter(Map propertyResults) {
-		Map result = new HashMap( propertyResults.size() );
-
-		String keyPrefix = "element.";
-
-		Iterator iter = propertyResults.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry element = ( Map.Entry ) iter.next();
-			String path = ( String ) element.getKey();
-			if ( path.startsWith( keyPrefix ) ) {
-				result.put( path.substring( keyPrefix.length() ), element.getValue() );
-			}
-		}
-
-		return result;
-	}
-
-	private void processCollectionReturn(NativeSQLQueryCollectionReturn collectionReturn) {
-		// we are initializing an owned collection
-		//collectionOwners.add( new Integer(-1) );
-//		collectionOwnerAliases.add( null );
-		String role = collectionReturn.getOwnerEntityName() + '.' + collectionReturn.getOwnerProperty();
-		addCollection(
-				role,
-				collectionReturn.getAlias(),
-				collectionReturn.getPropertyResultsMap()
-		);
-	}
-
-	private void processJoinReturn(NativeSQLQueryJoinReturn fetchReturn) {
-		String alias = fetchReturn.getAlias();
-//		if ( alias2Persister.containsKey( alias ) || collectionAliases.contains( alias ) ) {
-		if ( alias2Persister.containsKey( alias ) || alias2CollectionPersister.containsKey( alias ) ) {
-			// already been processed...
-			return;
-		}
-
-		String ownerAlias = fetchReturn.getOwnerAlias();
-
-		// Make sure the owner alias is known...
-		if ( !alias2Return.containsKey( ownerAlias ) ) {
-			throw new HibernateException( "Owner alias [" + ownerAlias + "] is unknown for alias [" + alias + "]" );
-		}
-
-		// If this return's alias has not been processed yet, do so b4 further processing of this return
-		if ( !alias2Persister.containsKey( ownerAlias ) ) {
-			NativeSQLQueryNonScalarReturn ownerReturn = ( NativeSQLQueryNonScalarReturn ) alias2Return.get(ownerAlias);
-			processReturn( ownerReturn );
-		}
-
-		SQLLoadable ownerPersister = ( SQLLoadable ) alias2Persister.get( ownerAlias );
-		Type returnType = ownerPersister.getPropertyType( fetchReturn.getOwnerProperty() );
-
-		if ( returnType.isCollectionType() ) {
-			String role = ownerPersister.getEntityName() + '.' + fetchReturn.getOwnerProperty();
-			addCollection( role, alias, fetchReturn.getPropertyResultsMap() );
-//			collectionOwnerAliases.add( ownerAlias );
-		}
-		else if ( returnType.isEntityType() ) {
-			EntityType eType = ( EntityType ) returnType;
-			String returnEntityName = eType.getAssociatedEntityName();
-			SQLLoadable persister = getSQLLoadable( returnEntityName );
-			addPersister( alias, fetchReturn.getPropertyResultsMap(), persister );
-		}
-
-	}
-
-//	public List getCollectionAliases() {
-//		return collectionAliases;
-//	}
-//
-//	/*public List getCollectionOwners() {
-//		return collectionOwners;
-//	}*/
-//
-//	public List getCollectionOwnerAliases() {
-//		return collectionOwnerAliases;
-//	}
-//
-//	public List getCollectionPersisters() {
-//		return collectionPersisters;
-//	}
-//
-//	public Map getAlias2Persister() {
-//		return alias2Persister;
-//	}
-//
-//	/*public boolean isCollectionInitializer() {
-//		return isCollectionInitializer;
-//	}*/
-//
-////	public List getPersisters() {
-////		return persisters;
-////	}
-//
-//	public Map getAlias2OwnerAlias() {
-//		return alias2OwnerAlias;
-//	}
-//
-//	public List getScalarTypes() {
-//		return scalarTypes;
-//	}
-//	public List getScalarColumnAliases() {
-//		return scalarColumnAliases;
-//	}
-//
-//	public List getPropertyResults() {
-//		return propertyResults;
-//	}
-//
-//	public List getCollectionPropertyResults() {
-//		return collectionResults;
-//	}
-//
-//
-//	public Map getAlias2Return() {
-//		return alias2Return;
-//	}
-}
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
+import org.hibernate.HibernateException;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.MappingException;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.query.sql.NativeSQLQueryCollectionReturn;
+import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
+import org.hibernate.engine.query.sql.NativeSQLQueryNonScalarReturn;
+import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
+import org.hibernate.loader.BasicLoader;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.ColumnEntityAliases;
+import org.hibernate.loader.DefaultEntityAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.GeneratedCollectionAliases;
+import org.hibernate.loader.custom.CollectionFetchReturn;
+import org.hibernate.loader.custom.CollectionReturn;
+import org.hibernate.loader.custom.ColumnCollectionAliases;
+import org.hibernate.loader.custom.EntityFetchReturn;
+import org.hibernate.loader.custom.FetchReturn;
+import org.hibernate.loader.custom.NonScalarReturn;
+import org.hibernate.loader.custom.Return;
+import org.hibernate.loader.custom.RootReturn;
+import org.hibernate.loader.custom.ScalarReturn;
+import org.hibernate.persister.collection.SQLLoadableCollection;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.SQLLoadable;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+import org.jboss.logging.Logger;
+
+/**
+ * Responsible for processing the series of {@link org.hibernate.engine.query.sql.NativeSQLQueryReturn returns}
+ * defined by a {@link org.hibernate.engine.query.sql.NativeSQLQuerySpecification} and
+ * breaking them down into a series of {@link Return returns} for use within the
+ * {@link org.hibernate.loader.custom.CustomLoader}.
+ *
+ * @author Gavin King
+ * @author Max Andersen
+ * @author Steve Ebersole
+ */
+public class SQLQueryReturnProcessor {
+
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
+                                                                       SQLQueryReturnProcessor.class.getName());
+
+	private NativeSQLQueryReturn[] queryReturns;
+
+//	private final List persisters = new ArrayList();
+
+	private final Map alias2Return = new HashMap();
+	private final Map alias2OwnerAlias = new HashMap();
+
+	private final Map alias2Persister = new HashMap();
+	private final Map alias2Suffix = new HashMap();
+
+	private final Map alias2CollectionPersister = new HashMap();
+	private final Map alias2CollectionSuffix = new HashMap();
+
+	private final Map entityPropertyResultMaps = new HashMap();
+	private final Map collectionPropertyResultMaps = new HashMap();
+
+//	private final List scalarTypes = new ArrayList();
+//	private final List scalarColumnAliases = new ArrayList();
+
+	private final SessionFactoryImplementor factory;
+
+//	private List collectionOwnerAliases = new ArrayList();
+//	private List collectionAliases = new ArrayList();
+//	private List collectionPersisters = new ArrayList();
+//	private List collectionResults = new ArrayList();
+
+	private int entitySuffixSeed = 0;
+	private int collectionSuffixSeed = 0;
+
+
+	public SQLQueryReturnProcessor(NativeSQLQueryReturn[] queryReturns, SessionFactoryImplementor factory) {
+		this.queryReturns = queryReturns;
+		this.factory = factory;
+	}
+
+	/*package*/ class ResultAliasContext {
+		public SQLLoadable getEntityPersister(String alias) {
+			return ( SQLLoadable ) alias2Persister.get( alias );
+		}
+
+		public SQLLoadableCollection getCollectionPersister(String alias) {
+			return ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
+		}
+
+		public String getEntitySuffix(String alias) {
+			return ( String ) alias2Suffix.get( alias );
+		}
+
+		public String getCollectionSuffix(String alias) {
+			return ( String ) alias2CollectionSuffix.get ( alias );
+		}
+
+		public String getOwnerAlias(String alias) {
+			return ( String ) alias2OwnerAlias.get( alias );
+		}
+
+		public Map getPropertyResultsMap(String alias) {
+			return internalGetPropertyResultsMap( alias );
+		}
+	}
+
+	private Map internalGetPropertyResultsMap(String alias) {
+		NativeSQLQueryReturn rtn = ( NativeSQLQueryReturn ) alias2Return.get( alias );
+		if ( rtn instanceof NativeSQLQueryNonScalarReturn ) {
+			return ( ( NativeSQLQueryNonScalarReturn ) rtn ).getPropertyResultsMap();
+		}
+		else {
+			return null;
+		}
+	}
+
+	private boolean hasPropertyResultMap(String alias) {
+		Map propertyMaps = internalGetPropertyResultsMap( alias );
+		return propertyMaps != null && ! propertyMaps.isEmpty();
+	}
+
+	public ResultAliasContext process() {
+		// first, break down the returns into maps keyed by alias
+		// so that role returns can be more easily resolved to their owners
+		for ( int i = 0; i < queryReturns.length; i++ ) {
+			if ( queryReturns[i] instanceof NativeSQLQueryNonScalarReturn ) {
+				NativeSQLQueryNonScalarReturn rtn = ( NativeSQLQueryNonScalarReturn ) queryReturns[i];
+				alias2Return.put( rtn.getAlias(), rtn );
+				if ( rtn instanceof NativeSQLQueryJoinReturn ) {
+					NativeSQLQueryJoinReturn fetchReturn = ( NativeSQLQueryJoinReturn ) rtn;
+					alias2OwnerAlias.put( fetchReturn.getAlias(), fetchReturn.getOwnerAlias() );
+				}
+			}
+		}
+
+		// Now, process the returns
+		for ( int i = 0; i < queryReturns.length; i++ ) {
+			processReturn( queryReturns[i] );
+		}
+
+		return new ResultAliasContext();
+	}
+
+	public List generateCustomReturns(boolean queryHadAliases) {
+		List customReturns = new ArrayList();
+		Map customReturnsByAlias = new HashMap();
+		for ( int i = 0; i < queryReturns.length; i++ ) {
+			if ( queryReturns[i] instanceof NativeSQLQueryScalarReturn ) {
+				NativeSQLQueryScalarReturn rtn = ( NativeSQLQueryScalarReturn ) queryReturns[i];
+				customReturns.add( new ScalarReturn( rtn.getType(), rtn.getColumnAlias() ) );
+			}
+			else if ( queryReturns[i] instanceof NativeSQLQueryRootReturn ) {
+				NativeSQLQueryRootReturn rtn = ( NativeSQLQueryRootReturn ) queryReturns[i];
+				String alias = rtn.getAlias();
+				EntityAliases entityAliases;
+				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
+					entityAliases = new DefaultEntityAliases(
+							( Map ) entityPropertyResultMaps.get( alias ),
+							( SQLLoadable ) alias2Persister.get( alias ),
+							( String ) alias2Suffix.get( alias )
+					);
+				}
+				else {
+					entityAliases = new ColumnEntityAliases(
+							( Map ) entityPropertyResultMaps.get( alias ),
+							( SQLLoadable ) alias2Persister.get( alias ),
+							( String ) alias2Suffix.get( alias )
+					);
+				}
+				RootReturn customReturn = new RootReturn(
+						alias,
+						rtn.getReturnEntityName(),
+						entityAliases,
+						rtn.getLockMode()
+				);
+				customReturns.add( customReturn );
+				customReturnsByAlias.put( rtn.getAlias(), customReturn );
+			}
+			else if ( queryReturns[i] instanceof NativeSQLQueryCollectionReturn ) {
+				NativeSQLQueryCollectionReturn rtn = ( NativeSQLQueryCollectionReturn ) queryReturns[i];
+				String alias = rtn.getAlias();
+				SQLLoadableCollection persister = ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
+				boolean isEntityElements = persister.getElementType().isEntityType();
+				CollectionAliases collectionAliases;
+				EntityAliases elementEntityAliases = null;
+				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
+					collectionAliases = new GeneratedCollectionAliases(
+							( Map ) collectionPropertyResultMaps.get( alias ),
+							( SQLLoadableCollection ) alias2CollectionPersister.get( alias ),
+							( String ) alias2CollectionSuffix.get( alias )
+					);
+					if ( isEntityElements ) {
+						elementEntityAliases = new DefaultEntityAliases(
+								( Map ) entityPropertyResultMaps.get( alias ),
+								( SQLLoadable ) alias2Persister.get( alias ),
+								( String ) alias2Suffix.get( alias )
+						);
+					}
+				}
+				else {
+					collectionAliases = new ColumnCollectionAliases(
+							( Map ) collectionPropertyResultMaps.get( alias ),
+							( SQLLoadableCollection ) alias2CollectionPersister.get( alias )
+					);
+					if ( isEntityElements ) {
+						elementEntityAliases = new ColumnEntityAliases(
+								( Map ) entityPropertyResultMaps.get( alias ),
+								( SQLLoadable ) alias2Persister.get( alias ),
+								( String ) alias2Suffix.get( alias )
+						);
+					}
+				}
+				CollectionReturn customReturn = new CollectionReturn(
+						alias,
+						rtn.getOwnerEntityName(),
+						rtn.getOwnerProperty(),
+						collectionAliases,
+				        elementEntityAliases,
+						rtn.getLockMode()
+				);
+				customReturns.add( customReturn );
+				customReturnsByAlias.put( rtn.getAlias(), customReturn );
+			}
+			else if ( queryReturns[i] instanceof NativeSQLQueryJoinReturn ) {
+				NativeSQLQueryJoinReturn rtn = ( NativeSQLQueryJoinReturn ) queryReturns[i];
+				String alias = rtn.getAlias();
+				FetchReturn customReturn;
+				NonScalarReturn ownerCustomReturn = ( NonScalarReturn ) customReturnsByAlias.get( rtn.getOwnerAlias() );
+				if ( alias2CollectionPersister.containsKey( alias ) ) {
+					SQLLoadableCollection persister = ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
+					boolean isEntityElements = persister.getElementType().isEntityType();
+					CollectionAliases collectionAliases;
+					EntityAliases elementEntityAliases = null;
+					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
+						collectionAliases = new GeneratedCollectionAliases(
+								( Map ) collectionPropertyResultMaps.get( alias ),
+								persister,
+								( String ) alias2CollectionSuffix.get( alias )
+						);
+						if ( isEntityElements ) {
+							elementEntityAliases = new DefaultEntityAliases(
+									( Map ) entityPropertyResultMaps.get( alias ),
+									( SQLLoadable ) alias2Persister.get( alias ),
+									( String ) alias2Suffix.get( alias )
+							);
+						}
+					}
+					else {
+						collectionAliases = new ColumnCollectionAliases(
+								( Map ) collectionPropertyResultMaps.get( alias ),
+								persister
+						);
+						if ( isEntityElements ) {
+							elementEntityAliases = new ColumnEntityAliases(
+									( Map ) entityPropertyResultMaps.get( alias ),
+									( SQLLoadable ) alias2Persister.get( alias ),
+									( String ) alias2Suffix.get( alias )
+							);
+						}
+					}
+					customReturn = new CollectionFetchReturn(
+							alias,
+							ownerCustomReturn,
+							rtn.getOwnerProperty(),
+							collectionAliases,
+					        elementEntityAliases,
+							rtn.getLockMode()
+					);
+				}
+				else {
+					EntityAliases entityAliases;
+					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
+						entityAliases = new DefaultEntityAliases(
+								( Map ) entityPropertyResultMaps.get( alias ),
+								( SQLLoadable ) alias2Persister.get( alias ),
+								( String ) alias2Suffix.get( alias )
+						);
+					}
+					else {
+						entityAliases = new ColumnEntityAliases(
+								( Map ) entityPropertyResultMaps.get( alias ),
+								( SQLLoadable ) alias2Persister.get( alias ),
+								( String ) alias2Suffix.get( alias )
+						);
+					}
+					customReturn = new EntityFetchReturn(
+							alias,
+							entityAliases,
+							ownerCustomReturn,
+							rtn.getOwnerProperty(),
+							rtn.getLockMode()
+					);
+				}
+				customReturns.add( customReturn );
+				customReturnsByAlias.put( alias, customReturn );
+			}
+		}
+		return customReturns;
+	}
+
+	private SQLLoadable getSQLLoadable(String entityName) throws MappingException {
+		EntityPersister persister = factory.getEntityPersister( entityName );
+		if ( !(persister instanceof SQLLoadable) ) {
+			throw new MappingException( "class persister is not SQLLoadable: " + entityName );
+		}
+		return (SQLLoadable) persister;
+	}
+
+	private String generateEntitySuffix() {
+		return BasicLoader.generateSuffixes( entitySuffixSeed++, 1 )[0];
+	}
+
+	private String generateCollectionSuffix() {
+		return collectionSuffixSeed++ + "__";
+	}
+
+	private void processReturn(NativeSQLQueryReturn rtn) {
+		if ( rtn instanceof NativeSQLQueryScalarReturn ) {
+			processScalarReturn( ( NativeSQLQueryScalarReturn ) rtn );
+		}
+		else if ( rtn instanceof NativeSQLQueryRootReturn ) {
+			processRootReturn( ( NativeSQLQueryRootReturn ) rtn );
+		}
+		else if ( rtn instanceof NativeSQLQueryCollectionReturn ) {
+			processCollectionReturn( ( NativeSQLQueryCollectionReturn ) rtn );
+		}
+		else {
+			processJoinReturn( ( NativeSQLQueryJoinReturn ) rtn );
+		}
+	}
+
+	private void processScalarReturn(NativeSQLQueryScalarReturn typeReturn) {
+//		scalarColumnAliases.add( typeReturn.getColumnAlias() );
+//		scalarTypes.add( typeReturn.getType() );
+	}
+
+	private void processRootReturn(NativeSQLQueryRootReturn rootReturn) {
+		if ( alias2Persister.containsKey( rootReturn.getAlias() ) ) {
+			// already been processed...
+			return;
+		}
+
+		SQLLoadable persister = getSQLLoadable( rootReturn.getReturnEntityName() );
+		addPersister( rootReturn.getAlias(), rootReturn.getPropertyResultsMap(), persister );
+	}
+
+	/**
+	 * @param propertyResult
+	 * @param persister
+	 */
+	private void addPersister(String alias, Map propertyResult, SQLLoadable persister) {
+		alias2Persister.put( alias, persister );
+		String suffix = generateEntitySuffix();
+        LOG.trace("Mapping alias [" + alias + "] to entity-suffix [" + suffix + "]");
+		alias2Suffix.put( alias, suffix );
+		entityPropertyResultMaps.put( alias, propertyResult );
+	}
+
+	private void addCollection(String role, String alias, Map propertyResults) {
+		SQLLoadableCollection collectionPersister = ( SQLLoadableCollection ) factory.getCollectionPersister( role );
+		alias2CollectionPersister.put( alias, collectionPersister );
+		String suffix = generateCollectionSuffix();
+        LOG.trace("Mapping alias [" + alias + "] to collection-suffix [" + suffix + "]");
+		alias2CollectionSuffix.put( alias, suffix );
+		collectionPropertyResultMaps.put( alias, propertyResults );
+
+		if ( collectionPersister.isOneToMany() || collectionPersister.isManyToMany() ) {
+			SQLLoadable persister = ( SQLLoadable ) collectionPersister.getElementPersister();
+			addPersister( alias, filter( propertyResults ), persister );
+		}
+	}
+
+	private Map filter(Map propertyResults) {
+		Map result = new HashMap( propertyResults.size() );
+
+		String keyPrefix = "element.";
+
+		Iterator iter = propertyResults.entrySet().iterator();
+		while ( iter.hasNext() ) {
+			Map.Entry element = ( Map.Entry ) iter.next();
+			String path = ( String ) element.getKey();
+			if ( path.startsWith( keyPrefix ) ) {
+				result.put( path.substring( keyPrefix.length() ), element.getValue() );
+			}
+		}
+
+		return result;
+	}
+
+	private void processCollectionReturn(NativeSQLQueryCollectionReturn collectionReturn) {
+		// we are initializing an owned collection
+		//collectionOwners.add( new Integer(-1) );
+//		collectionOwnerAliases.add( null );
+		String role = collectionReturn.getOwnerEntityName() + '.' + collectionReturn.getOwnerProperty();
+		addCollection(
+				role,
+				collectionReturn.getAlias(),
+				collectionReturn.getPropertyResultsMap()
+		);
+	}
+
+	private void processJoinReturn(NativeSQLQueryJoinReturn fetchReturn) {
+		String alias = fetchReturn.getAlias();
+//		if ( alias2Persister.containsKey( alias ) || collectionAliases.contains( alias ) ) {
+		if ( alias2Persister.containsKey( alias ) || alias2CollectionPersister.containsKey( alias ) ) {
+			// already been processed...
+			return;
+		}
+
+		String ownerAlias = fetchReturn.getOwnerAlias();
+
+		// Make sure the owner alias is known...
+		if ( !alias2Return.containsKey( ownerAlias ) ) {
+			throw new HibernateException( "Owner alias [" + ownerAlias + "] is unknown for alias [" + alias + "]" );
+		}
+
+		// If this return's alias has not been processed yet, do so b4 further processing of this return
+		if ( !alias2Persister.containsKey( ownerAlias ) ) {
+			NativeSQLQueryNonScalarReturn ownerReturn = ( NativeSQLQueryNonScalarReturn ) alias2Return.get(ownerAlias);
+			processReturn( ownerReturn );
+		}
+
+		SQLLoadable ownerPersister = ( SQLLoadable ) alias2Persister.get( ownerAlias );
+		Type returnType = ownerPersister.getPropertyType( fetchReturn.getOwnerProperty() );
+
+		if ( returnType.isCollectionType() ) {
+			String role = ownerPersister.getEntityName() + '.' + fetchReturn.getOwnerProperty();
+			addCollection( role, alias, fetchReturn.getPropertyResultsMap() );
+//			collectionOwnerAliases.add( ownerAlias );
+		}
+		else if ( returnType.isEntityType() ) {
+			EntityType eType = ( EntityType ) returnType;
+			String returnEntityName = eType.getAssociatedEntityName();
+			SQLLoadable persister = getSQLLoadable( returnEntityName );
+			addPersister( alias, fetchReturn.getPropertyResultsMap(), persister );
+		}
+
+	}
+
+//	public List getCollectionAliases() {
+//		return collectionAliases;
+//	}
+//
+//	/*public List getCollectionOwners() {
+//		return collectionOwners;
+//	}*/
+//
+//	public List getCollectionOwnerAliases() {
+//		return collectionOwnerAliases;
+//	}
+//
+//	public List getCollectionPersisters() {
+//		return collectionPersisters;
+//	}
+//
+//	public Map getAlias2Persister() {
+//		return alias2Persister;
+//	}
+//
+//	/*public boolean isCollectionInitializer() {
+//		return isCollectionInitializer;
+//	}*/
+//
+////	public List getPersisters() {
+////		return persisters;
+////	}
+//
+//	public Map getAlias2OwnerAlias() {
+//		return alias2OwnerAlias;
+//	}
+//
+//	public List getScalarTypes() {
+//		return scalarTypes;
+//	}
+//	public List getScalarColumnAliases() {
+//		return scalarColumnAliases;
+//	}
+//
+//	public List getPropertyResults() {
+//		return propertyResults;
+//	}
+//
+//	public List getCollectionPropertyResults() {
+//		return collectionResults;
+//	}
+//
+//
+//	public Map getAlias2Return() {
+//		return alias2Return;
+//	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/CollectionElementLoader.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/RootClass.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, RootClass.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, RootClass.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityIdentifier.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, EntityIdentifier.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityIdentifier.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/AbstractSimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/AbstractSimpleValue.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, AbstractSimpleValue.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AbstractSimpleValue.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/ForeignKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/ForeignKey.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, AbstractConstraint.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AbstractConstraint.class.getName());
-				if (LOG.isEnabled(Level.WARN)) LOG.attemptToMapColumnToNoTargetColumn(sourceColumn.toLoggableString(), getName());
+				if (LOG.isEnabled( Level.WARN )) LOG.attemptToMapColumnToNoTargetColumn(sourceColumn.toLoggableString(), getName());
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/ExtendsQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/ExtendsQueue.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ExtendsQueue.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ExtendsQueue.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/Metadata.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Metadata.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Metadata.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataSourceQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataSourceQueue.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, MetadataSourceQueue.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, MetadataSourceQueue.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractPropertyMapping.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, NamedQueryLoader.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NamedQueryLoader.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
+++ b/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Printer.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Printer.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicPropertyAccessor.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicPropertyAccessor.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/javassist/JavassistLazyInitializer.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/secure/JACCConfiguration.java
+++ b/hibernate-core/src/main/java/org/hibernate/secure/JACCConfiguration.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JACCConfiguration.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JACCConfiguration.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, AbstractServiceRegistryImpl.class.getName() );
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractServiceRegistryImpl.class.getName() );
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicServiceRegistryImpl.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicServiceRegistryImpl.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.service.spi.BasicServiceInitiator;
-	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, SessionFactoryServiceRegistryImpl.class.getName() );
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, SessionFactoryServiceRegistryImpl.class.getName() );
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-        LOG.autoCommitMode(autocommit);
+        LOG.autoCommitMode( autocommit );
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/AbstractDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/AbstractDialectResolver.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverSet.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, DialectResolverSet.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DialectResolverSet.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/StandardDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/StandardDialectResolver.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JmxServiceImpl.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JmxServiceImpl.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceImpl.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JndiServiceImpl.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JndiServiceImpl.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JtaPlatformInitiator.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JtaPlatformInitiator.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/WebSphereJtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/WebSphereJtaPlatform.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, WebSphereJtaPlatform.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, WebSphereJtaPlatform.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, OrderByFragmentParser.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, OrderByFragmentParser.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ConcurrentStatisticsImpl.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ConcurrentStatisticsImpl.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, StatisticsInitiator.class.getName() );
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatisticsInitiator.class.getName() );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseExporter.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseExporter.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, DatabaseExporter.class.getName() );
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DatabaseExporter.class.getName() );
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, DatabaseMetaData.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DatabaseMetaData.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SchemaExport.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaExport.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SchemaUpdate.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaUpdate.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SchemaValidator.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaValidator.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/TableMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/TableMetadata.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, TableMetadata.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TableMetadata.class.getName());
-        LOG.tableFound(cat + schem + name);
-        LOG.columns(columns.keySet());
+        LOG.tableFound( cat + schem + name );
+        LOG.columns( columns.keySet() );
-            LOG.foreignKeys(foreignKeys.keySet());
-            LOG.indexes(indexes.keySet());
+            LOG.foreignKeys( foreignKeys.keySet() );
+            LOG.indexes( indexes.keySet() );
--- a/hibernate-core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, PojoInstantiator.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoInstantiator.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(
-			HibernateLogger.class,
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/Dom4jEntityTuplizer.java
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-import java.io.Serializable;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.Map;
-import org.dom4j.Element;
-import org.hibernate.EntityMode;
-import org.hibernate.EntityNameResolver;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.Getter;
-import org.hibernate.property.PropertyAccessor;
-import org.hibernate.property.PropertyAccessorFactory;
-import org.hibernate.property.Setter;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.proxy.dom4j.Dom4jProxyFactory;
-import org.hibernate.tuple.Dom4jInstantiator;
-import org.hibernate.tuple.Instantiator;
-import org.hibernate.type.CompositeType;
-import org.jboss.logging.Logger;
-
-/**
- * An {@link EntityTuplizer} specific to the dom4j entity mode.
- *
- * @author Steve Ebersole
- * @author Gavin King
- */
-public class Dom4jEntityTuplizer extends AbstractEntityTuplizer {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Dom4jEntityTuplizer.class.getName());
-
-	private Map inheritenceNodeNameMap = new HashMap();
-
-	Dom4jEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
-		super( entityMetamodel, mappedEntity );
-		inheritenceNodeNameMap.put( mappedEntity.getNodeName(), mappedEntity.getEntityName() );
-		Iterator itr = mappedEntity.getSubclassClosureIterator();
-		while( itr.hasNext() ) {
-			final PersistentClass mapping = ( PersistentClass ) itr.next();
-			inheritenceNodeNameMap.put( mapping.getNodeName(), mapping.getEntityName() );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityMode getEntityMode() {
-		return EntityMode.DOM4J;
-	}
-
-	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
-		if ( mappedProperty.isBackRef() ) {
-			return mappedProperty.getPropertyAccessor(null);
-		}
-		else {
-			return PropertyAccessorFactory.getDom4jPropertyAccessor(
-					mappedProperty.getNodeName(),
-					mappedProperty.getType(),
-					getEntityMetamodel().getSessionFactory()
-				);
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Instantiator buildInstantiator(PersistentClass persistentClass) {
-		return new Dom4jInstantiator( persistentClass );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public Serializable getIdentifier(Object entityOrId) throws HibernateException {
-		return getIdentifier( entityOrId, null );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public Serializable getIdentifier(Object entityOrId, SessionImplementor session) {
-		if ( entityOrId instanceof Element ) {
-			return super.getIdentifier( entityOrId, session );
-		}
-		else {
-			//it was not embedded, so the argument is just an id
-			return (Serializable) entityOrId;
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
-		HashSet proxyInterfaces = new HashSet();
-		proxyInterfaces.add( HibernateProxy.class );
-		proxyInterfaces.add( Element.class );
-
-		ProxyFactory pf = new Dom4jProxyFactory();
-		try {
-			pf.postInstantiate(
-					getEntityName(),
-					Element.class,
-					proxyInterfaces,
-					null,
-					null,
-					mappingInfo.hasEmbeddedIdentifier() ?
-			                (CompositeType) mappingInfo.getIdentifier().getType() :
-			                null
-			);
-		}
-		catch ( HibernateException he ) {
-            LOG.unableToCreateProxyFactory(getEntityName(), he);
-			pf = null;
-		}
-		return pf;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getMappedClass() {
-		return Element.class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getConcreteProxyClass() {
-		return Element.class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isInstrumented() {
-		return false;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityNameResolver[] getEntityNameResolvers() {
-		return new EntityNameResolver[] { new BasicEntityNameResolver( getEntityName(), inheritenceNodeNameMap ) };
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
-		return ( String ) inheritenceNodeNameMap.get( extractNodeName( ( Element ) entityInstance ) );
-	}
-
-	public static String extractNodeName(Element element) {
-		return element.getName();
-	}
-
-	public static class BasicEntityNameResolver implements EntityNameResolver {
-		private final String rootEntityName;
-		private final Map nodeNameToEntityNameMap;
-
-		public BasicEntityNameResolver(String rootEntityName, Map nodeNameToEntityNameMap) {
-			this.rootEntityName = rootEntityName;
-			this.nodeNameToEntityNameMap = nodeNameToEntityNameMap;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public String resolveEntityName(Object entity) {
-		return ( String ) nodeNameToEntityNameMap.get( extractNodeName( ( Element ) entity ) );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		@Override
-        public boolean equals(Object obj) {
-			return rootEntityName.equals( ( ( BasicEntityNameResolver ) obj ).rootEntityName );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		@Override
-        public int hashCode() {
-			return rootEntityName.hashCode();
-		}
-	}
-}
+import java.io.Serializable;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.Map;
+import org.dom4j.Element;
+import org.hibernate.EntityMode;
+import org.hibernate.EntityNameResolver;
+import org.hibernate.HibernateException;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.property.Getter;
+import org.hibernate.property.PropertyAccessor;
+import org.hibernate.property.PropertyAccessorFactory;
+import org.hibernate.property.Setter;
+import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.proxy.ProxyFactory;
+import org.hibernate.proxy.dom4j.Dom4jProxyFactory;
+import org.hibernate.tuple.Dom4jInstantiator;
+import org.hibernate.tuple.Instantiator;
+import org.hibernate.type.CompositeType;
+import org.jboss.logging.Logger;
+
+/**
+ * An {@link EntityTuplizer} specific to the dom4j entity mode.
+ *
+ * @author Steve Ebersole
+ * @author Gavin King
+ */
+public class Dom4jEntityTuplizer extends AbstractEntityTuplizer {
+
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dom4jEntityTuplizer.class.getName());
+
+	private Map inheritenceNodeNameMap = new HashMap();
+
+	Dom4jEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
+		super( entityMetamodel, mappedEntity );
+		inheritenceNodeNameMap.put( mappedEntity.getNodeName(), mappedEntity.getEntityName() );
+		Iterator itr = mappedEntity.getSubclassClosureIterator();
+		while( itr.hasNext() ) {
+			final PersistentClass mapping = ( PersistentClass ) itr.next();
+			inheritenceNodeNameMap.put( mapping.getNodeName(), mapping.getEntityName() );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public EntityMode getEntityMode() {
+		return EntityMode.DOM4J;
+	}
+
+	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
+		if ( mappedProperty.isBackRef() ) {
+			return mappedProperty.getPropertyAccessor(null);
+		}
+		else {
+			return PropertyAccessorFactory.getDom4jPropertyAccessor(
+					mappedProperty.getNodeName(),
+					mappedProperty.getType(),
+					getEntityMetamodel().getSessionFactory()
+				);
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
+		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
+		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Instantiator buildInstantiator(PersistentClass persistentClass) {
+		return new Dom4jInstantiator( persistentClass );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public Serializable getIdentifier(Object entityOrId) throws HibernateException {
+		return getIdentifier( entityOrId, null );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public Serializable getIdentifier(Object entityOrId, SessionImplementor session) {
+		if ( entityOrId instanceof Element ) {
+			return super.getIdentifier( entityOrId, session );
+		}
+		else {
+			//it was not embedded, so the argument is just an id
+			return (Serializable) entityOrId;
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
+		HashSet proxyInterfaces = new HashSet();
+		proxyInterfaces.add( HibernateProxy.class );
+		proxyInterfaces.add( Element.class );
+
+		ProxyFactory pf = new Dom4jProxyFactory();
+		try {
+			pf.postInstantiate(
+					getEntityName(),
+					Element.class,
+					proxyInterfaces,
+					null,
+					null,
+					mappingInfo.hasEmbeddedIdentifier() ?
+			                (CompositeType) mappingInfo.getIdentifier().getType() :
+			                null
+			);
+		}
+		catch ( HibernateException he ) {
+            LOG.unableToCreateProxyFactory(getEntityName(), he);
+			pf = null;
+		}
+		return pf;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class getMappedClass() {
+		return Element.class;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class getConcreteProxyClass() {
+		return Element.class;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isInstrumented() {
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public EntityNameResolver[] getEntityNameResolvers() {
+		return new EntityNameResolver[] { new BasicEntityNameResolver( getEntityName(), inheritenceNodeNameMap ) };
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
+		return ( String ) inheritenceNodeNameMap.get( extractNodeName( ( Element ) entityInstance ) );
+	}
+
+	public static String extractNodeName(Element element) {
+		return element.getName();
+	}
+
+	public static class BasicEntityNameResolver implements EntityNameResolver {
+		private final String rootEntityName;
+		private final Map nodeNameToEntityNameMap;
+
+		public BasicEntityNameResolver(String rootEntityName, Map nodeNameToEntityNameMap) {
+			this.rootEntityName = rootEntityName;
+			this.nodeNameToEntityNameMap = nodeNameToEntityNameMap;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public String resolveEntityName(Object entity) {
+		return ( String ) nodeNameToEntityNameMap.get( extractNodeName( ( Element ) entity ) );
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		@Override
+        public boolean equals(Object obj) {
+			return rootEntityName.equals( ( ( BasicEntityNameResolver ) obj ).rootEntityName );
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		@Override
+        public int hashCode() {
+			return rootEntityName.hashCode();
+		}
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
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
- */
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
-import java.util.Map;
-import org.hibernate.EntityMode;
-import org.hibernate.EntityNameResolver;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.Getter;
-import org.hibernate.property.PropertyAccessor;
-import org.hibernate.property.PropertyAccessorFactory;
-import org.hibernate.property.Setter;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.proxy.map.MapProxyFactory;
-import org.hibernate.tuple.DynamicMapInstantiator;
-import org.hibernate.tuple.Instantiator;
-import org.jboss.logging.Logger;
-
-/**
- * An {@link EntityTuplizer} specific to the dynamic-map entity mode.
- *
- * @author Steve Ebersole
- * @author Gavin King
- */
-public class DynamicMapEntityTuplizer extends AbstractEntityTuplizer {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       DynamicMapEntityTuplizer.class.getName());
-
-	DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
-		super(entityMetamodel, mappedEntity);
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityMode getEntityMode() {
-		return EntityMode.MAP;
-	}
-
-	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
-		if ( mappedProperty.isBackRef() ) {
-			return mappedProperty.getPropertyAccessor(null);
-		}
-		else {
-			return PropertyAccessorFactory.getDynamicMapPropertyAccessor();
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Instantiator buildInstantiator(PersistentClass mappingInfo) {
-        return new DynamicMapInstantiator( mappingInfo );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
-
-		ProxyFactory pf = new MapProxyFactory();
-		try {
-			//TODO: design new lifecycle for ProxyFactory
-			pf.postInstantiate(
-					getEntityName(),
-					null,
-					null,
-					null,
-					null,
-					null
-			);
-		}
-		catch ( HibernateException he ) {
-            LOG.unableToCreateProxyFactory(getEntityName(), he);
-			pf = null;
-		}
-		return pf;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getMappedClass() {
-		return Map.class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getConcreteProxyClass() {
-		return Map.class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isInstrumented() {
-		return false;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityNameResolver[] getEntityNameResolvers() {
-		return new EntityNameResolver[] { BasicEntityNameResolver.INSTANCE };
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
-		return extractEmbeddedEntityName( ( Map ) entityInstance );
-	}
-
-	public static String extractEmbeddedEntityName(Map entity) {
-		return ( String ) entity.get( DynamicMapInstantiator.KEY );
-	}
-
-	public static class BasicEntityNameResolver implements EntityNameResolver {
-		public static final BasicEntityNameResolver INSTANCE = new BasicEntityNameResolver();
-
-		/**
-		 * {@inheritDoc}
-		 */
-		public String resolveEntityName(Object entity) {
-			final String entityName = extractEmbeddedEntityName( ( Map ) entity );
-			if ( entityName == null ) {
-				throw new HibernateException( "Could not determine type of dynamic map entity" );
-			}
-			return entityName;
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		@Override
-        public boolean equals(Object obj) {
-			return getClass().equals( obj.getClass() );
-		}
-
-		/**
-		 * {@inheritDoc}
-		 */
-		@Override
-        public int hashCode() {
-			return getClass().hashCode();
-		}
-	}
-}
+import java.util.Map;
+import org.hibernate.EntityMode;
+import org.hibernate.EntityNameResolver;
+import org.hibernate.HibernateException;
+import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.property.Getter;
+import org.hibernate.property.PropertyAccessor;
+import org.hibernate.property.PropertyAccessorFactory;
+import org.hibernate.property.Setter;
+import org.hibernate.proxy.ProxyFactory;
+import org.hibernate.proxy.map.MapProxyFactory;
+import org.hibernate.tuple.DynamicMapInstantiator;
+import org.hibernate.tuple.Instantiator;
+import org.jboss.logging.Logger;
+
+/**
+ * An {@link EntityTuplizer} specific to the dynamic-map entity mode.
+ *
+ * @author Steve Ebersole
+ * @author Gavin King
+ */
+public class DynamicMapEntityTuplizer extends AbstractEntityTuplizer {
+
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
+                                                                       DynamicMapEntityTuplizer.class.getName());
+
+	DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
+		super(entityMetamodel, mappedEntity);
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public EntityMode getEntityMode() {
+		return EntityMode.MAP;
+	}
+
+	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
+		if ( mappedProperty.isBackRef() ) {
+			return mappedProperty.getPropertyAccessor(null);
+		}
+		else {
+			return PropertyAccessorFactory.getDynamicMapPropertyAccessor();
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
+		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
+		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Instantiator buildInstantiator(PersistentClass mappingInfo) {
+        return new DynamicMapInstantiator( mappingInfo );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
+
+		ProxyFactory pf = new MapProxyFactory();
+		try {
+			//TODO: design new lifecycle for ProxyFactory
+			pf.postInstantiate(
+					getEntityName(),
+					null,
+					null,
+					null,
+					null,
+					null
+			);
+		}
+		catch ( HibernateException he ) {
+            LOG.unableToCreateProxyFactory(getEntityName(), he);
+			pf = null;
+		}
+		return pf;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class getMappedClass() {
+		return Map.class;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class getConcreteProxyClass() {
+		return Map.class;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isInstrumented() {
+		return false;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public EntityNameResolver[] getEntityNameResolvers() {
+		return new EntityNameResolver[] { BasicEntityNameResolver.INSTANCE };
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
+		return extractEmbeddedEntityName( ( Map ) entityInstance );
+	}
+
+	public static String extractEmbeddedEntityName(Map entity) {
+		return ( String ) entity.get( DynamicMapInstantiator.KEY );
+	}
+
+	public static class BasicEntityNameResolver implements EntityNameResolver {
+		public static final BasicEntityNameResolver INSTANCE = new BasicEntityNameResolver();
+
+		/**
+		 * {@inheritDoc}
+		 */
+		public String resolveEntityName(Object entity) {
+			final String entityName = extractEmbeddedEntityName( ( Map ) entity );
+			if ( entityName == null ) {
+				throw new HibernateException( "Could not determine type of dynamic map entity" );
+			}
+			return entityName;
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		@Override
+        public boolean equals(Object obj) {
+			return getClass().equals( obj.getClass() );
+		}
+
+		/**
+		 * {@inheritDoc}
+		 */
+		@Override
+        public int hashCode() {
+			return getClass().hashCode();
+		}
+	}
+}
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, EntityMetamodel.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, PojoEntityTuplizer.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoEntityTuplizer.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicTypeRegistry.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicTypeRegistry.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, DbTimestampType.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DbTimestampType.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, EnumType.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EnumType.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/NullableType.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, NullableType.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NullableType.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, TypeFactory.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TypeFactory.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/JdbcTypeNameMapper.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JdbcTypeNameMapper.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JdbcTypeNameMapper.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/DataHelper.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, DataHelper.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DataHelper.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicBinder.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicBinder.class.getName());
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicExtractor.class.getName());
+    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicExtractor.class.getName());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
-import static org.hibernate.testing.TestLogger.LOG;
-import junit.framework.TestCase;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.cfg.AnnotationConfiguration;
+import org.hibernate.cfg.Configuration;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
-public class BackquoteTest extends TestCase {
+public class BackquoteTest extends BaseUnitTestCase {
+	private static final Logger log = Logger.getLogger( BackquoteTest.class );
-	@Override
+	@Before
-	@Override
+	@After
+	@Test
+	@TestForIssue( jiraKey = "ANN-718" )
-			AnnotationConfiguration config = new AnnotationConfiguration();
+			Configuration config = new Configuration();
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
+	@Test
+	@TestForIssue( jiraKey = "HHH-4647" )
-    		AnnotationConfiguration config = new AnnotationConfiguration();
+    		Configuration config = new Configuration();
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
-// $Id$
- * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
- *
-
-import static org.hibernate.testing.TestLogger.LOG;
-import junit.framework.TestCase;
+
+import org.jboss.logging.Logger;
+
-import org.hibernate.cfg.AnnotationConfiguration;
+import org.hibernate.cfg.Configuration;
+import org.junit.After;
+import org.junit.Before;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
-public class FetchProfileTest extends TestCase {
+@TestForIssue( jiraKey = "HHH-4812" )
+public class FetchProfileTest extends BaseUnitTestCase {
+	private static final Logger log = Logger.getLogger( FetchProfileTest.class );
-	@Override
+	@Before
-	@Override
+	@After
-		AnnotationConfiguration config = new AnnotationConfiguration();
+		Configuration config = new Configuration();
-		AnnotationConfiguration config = new AnnotationConfiguration();
+		Configuration config = new Configuration();
-            LOG.trace("success");
+            log.trace("success");
-		AnnotationConfiguration config = new AnnotationConfiguration();
+		Configuration config = new Configuration();
-            LOG.trace("success");
+            log.trace("success");
-		AnnotationConfiguration config = new AnnotationConfiguration();
+		Configuration config = new Configuration();
-            LOG.trace("success");
+            log.trace("success");
-		AnnotationConfiguration config = new AnnotationConfiguration();
+		Configuration config = new Configuration();
-		config = new AnnotationConfiguration();
+		config = new Configuration();
-            LOG.trace("success");
+            log.trace("success");
-		AnnotationConfiguration config = new AnnotationConfiguration();
+		Configuration config = new Configuration();
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/fkcircularity/FkCircularityTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/fkcircularity/FkCircularityTest.java
-import static org.hibernate.testing.TestLogger.LOG;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.testing.TestForIssue;
+	private static final Logger log = Logger.getLogger( FkCircularityTest.class );
-                LOG.debug(s);
+                log.debug(s);
-            LOG.debug("success");
+            log.debug("success");
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
-                LOG.debug(s);
+                log.debug(s);
-            LOG.debug("success");
+            log.debug("success");
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/id/EnumIdTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/id/EnumIdTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( EnumIdTest.class );
+
-        LOG.debug(mercuryFromDb.toString());
+        log.debug(mercuryFromDb.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/id/JoinColumnOverrideTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/id/JoinColumnOverrideTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( JoinColumnOverrideTest.class );
+
-                LOG.debug(s);
+                log.debug(s);
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/id/sequences/EnumIdTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/id/sequences/EnumIdTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( EnumIdTest.class );
+
-        LOG.debug(mercuryFromDb.toString());
+        log.debug(mercuryFromDb.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/id/sequences/JoinColumnOverrideTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/id/sequences/JoinColumnOverrideTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( JoinColumnOverrideTest.class );
+
-                LOG.debug(s);
+                log.debug(s);
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/immutable/ImmutableTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/immutable/ImmutableTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( ImmutableTest.class );
+
-            LOG.debug("success");
+            log.debug("success");
-            LOG.debug("success");
+            log.debug("success");
-            LOG.debug("succes");
+            log.debug("succes");
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
-import static org.hibernate.testing.TestLogger.LOG;
-import junit.framework.TestCase;
+
+import org.jboss.logging.Logger;
+
+import junit.framework.TestCase;
+
+	private static final Logger log = Logger.getLogger( NamingStrategyTest.class );
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
-                LOG.info("testWithEJB3NamingStrategy table = " + table.getName());
+                log.info("testWithEJB3NamingStrategy table = " + table.getName());
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
-            LOG.debug(writer.toString());
+            log.debug(writer.toString());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( NaturalIdOnSingleManyToOneTest.class );
+
-        LOG.warn("Commented out test");
+        log.warn("Commented out test");
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/primarykey/NullablePrimaryKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/primarykey/NullablePrimaryKeyTest.java
-import static org.hibernate.testing.TestLogger.LOG;
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( NullablePrimaryKeyTest.class );
-                LOG.debug(s);
+                log.debug(s);
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/LogListener.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/LogListener.java
-import static org.hibernate.testing.TestLogger.LOG;
+
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( LogListener.class );
-        LOG.debug("Logging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
+        log.debug("Logging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
-
-        LOG.debug("NoLogging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
+        log.debug("NoLogging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/OtherLogListener.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/reflection/OtherLogListener.java
-import static org.hibernate.testing.TestLogger.LOG;
+
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( OtherLogListener.class );
-        LOG.debug("Logging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
+        log.debug("Logging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
-
-        LOG.debug("NoLogging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
+        log.debug("NoLogging entity " + entity.getClass().getName() + " with hashCode: " + entity.hashCode());
--- a/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/cache/SQLFunctionsInterSystemsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/dialect/functional/cache/SQLFunctionsInterSystemsTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( SQLFunctionsInterSystemsTest.class );
+
-        LOG.debug("levinson: just bfore b.getClob()");
+        log.debug("levinson: just bfore b.getClob()");
-            LOG.info("Dialect does not list any no-arg functions");
+            log.info("Dialect does not list any no-arg functions");
-        LOG.info("Using function named [" + functionName + "] for 'function as alias' test");
+        log.info("Using function named [" + functionName + "] for 'function as alias' test");
--- a/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
-import static org.hibernate.testing.TestLogger.LOG;
+import org.jboss.logging.Logger;
+
-import org.hibernate.EntityMode;
+	private static final Logger log = Logger.getLogger( DynamicFilterTest.class );
-        LOG.info( "Starting HQL filter tests" );
+        log.info( "Starting HQL filter tests" );
-        LOG.info( "HQL against Salesperson..." );
+        log.info( "HQL against Salesperson..." );
-        LOG.info( "HQL against Product..." );
+        log.info( "HQL against Product..." );
-        LOG.info("Starting HQL filter with custom SQL get/set tests");
+        log.info("Starting HQL filter with custom SQL get/set tests");
-        LOG.info( "HQL against Product..." );
+        log.info( "HQL against Product..." );
-        LOG.info("Starting Criteria-query filter tests");
+        log.info("Starting Criteria-query filter tests");
-        LOG.info("Criteria query against Salesperson...");
+        log.info("Criteria query against Salesperson...");
-        LOG.info("Criteria query against Product...");
+        log.info("Criteria query against Product...");
-        LOG.info("Starting Criteria-subquery filter tests");
+        log.info("Starting Criteria-subquery filter tests");
-        LOG.info("Criteria query against Department with a subquery on Salesperson in the APAC reqion...");
+        log.info("Criteria query against Department with a subquery on Salesperson in the APAC reqion...");
-        LOG.info("Criteria query against Department with a subquery on Salesperson in the FooBar reqion...");
+        log.info("Criteria query against Department with a subquery on Salesperson in the FooBar reqion...");
-        LOG.info("Criteria query against Order with a subquery for line items with a subquery on product and sold by a given sales person...");
+        log.info("Criteria query against Order with a subquery for line items with a subquery on product and sold by a given sales person...");
-        LOG.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month");
+        log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month");
-        LOG.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of 4 months ago");
+        log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of 4 months ago");
-        LOG.info("Starting HQL subquery with filters tests");
+        log.info("Starting HQL subquery with filters tests");
-        LOG.info("query against Department with a subquery on Salesperson in the APAC reqion...");
+        log.info("query against Department with a subquery on Salesperson in the APAC reqion...");
-        LOG.info("query against Department with a subquery on Salesperson in the FooBar reqion...");
+        log.info("query against Department with a subquery on Salesperson in the FooBar reqion...");
-        LOG.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region for a given buyer");
+        log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region for a given buyer");
-        LOG.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month");
+        log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month");
-        LOG.info(
+        log.info(
-        LOG.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month with named types");
+        log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month with named types");
-        LOG.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month with mixed types");
+        log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month with mixed types");
-        LOG.info("Starting get() filter tests (eager assoc. fetching).");
+        log.info("Starting get() filter tests (eager assoc. fetching).");
-        LOG.info("Performing get()...");
+        log.info("Performing get()...");
-        LOG.info("Starting one-to-many collection loader filter tests.");
+        log.info("Starting one-to-many collection loader filter tests.");
-        LOG.info("Performing load of Department...");
+        log.info("Performing load of Department...");
-        LOG.info("Starting one-to-many collection loader filter tests.");
+        log.info("Starting one-to-many collection loader filter tests.");
-        LOG.debug("Performing query of Salespersons");
+        log.debug("Performing query of Salespersons");
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
-import static org.hibernate.testing.junit4.ExtraAssertions.assertClassAssignability;
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertFalse;
-import static org.junit.Assert.assertNotNull;
-import static org.junit.Assert.assertNull;
-import static org.junit.Assert.assertSame;
-import static org.junit.Assert.assertTrue;
-import static org.junit.Assert.fail;
-
+import static org.hibernate.testing.junit4.ExtraAssertions.assertClassAssignability;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertNull;
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
+
+	private static final Logger log = Logger.getLogger( ASTParserLoadingTest.class );
+
-            LOG.trace("expected failure...", qe);
+            log.trace("expected failure...", qe);
-            LOG.trace("expected failure...", qe);
+            log.trace("expected failure...", qe);
-            LOG.trace("expected failure...", qe);
+            log.trace("expected failure...", qe);
-            LOG.trace("expected failure...", qe);
+            log.trace("expected failure...", qe);
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/cascade/CascadeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/cascade/CascadeTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( CascadeTest.class );
+
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception", e );
+				log.trace( "handled expected exception", e );
-				LOG.trace( "handled expected exception : " + e );
+				log.trace( "handled expected exception : " + e );
-				LOG.trace( "handled expected exception : " + e );
+				log.trace( "handled expected exception : " + e );
-			LOG.warn( "unable to cleanup test data : " + t );
+			log.warn( "unable to cleanup test data : " + t );
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( FooBarTest.class );
-            LOG.info(arr[0] + " " + arr[1] + " " + arr[2] + " " + arr[3]);
+            log.info(arr[0] + " " + arr[1] + " " + arr[2] + " " + arr[3]);
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( SQLFunctionsTest.class );
-            LOG.info("Dialect does not list any no-arg functions");
+            log.info("Dialect does not list any no-arg functions");
-        LOG.info("Using function named [" + functionName + "] for 'function as alias' test");
+        log.info("Using function named [" + functionName + "] for 'function as alias' test");
--- a/hibernate-core/src/test/java/org/hibernate/test/stateless/fetching/StatelessSessionFetchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/stateless/fetching/StatelessSessionFetchingTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( StatelessSessionFetchingTest.class );
+
-            LOG.debug("prefixed table name : " + baseTableName + " -> " + prefixed);
+            log.debug("prefixed table name : " + baseTableName + " -> " + prefixed);
--- a/hibernate-core/src/test/java/org/hibernate/test/typeparameters/DefaultValueIntegerType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/typeparameters/DefaultValueIntegerType.java
-import static org.hibernate.testing.TestLogger.LOG;
+
+
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( DefaultValueIntegerType.class );
-            LOG.trace("binding null to parameter: " + index);
+            log.trace("binding null to parameter: " + index);
-            LOG.trace("binding " + value + " to parameter: " + index);
+            log.trace("binding " + value + " to parameter: " + index);
--- a/hibernate-core/src/test/java/org/hibernate/testing/tm/SimpleJtaTransactionImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/testing/tm/SimpleJtaTransactionImpl.java
-import static org.hibernate.testing.TestLogger.LOG;
-import java.sql.Connection;
-import java.sql.SQLException;
-import java.util.LinkedList;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+import java.util.LinkedList;
+
+import org.jboss.logging.Logger;
+	private static final Logger log = Logger.getLogger( SimpleJtaTransactionImpl.class );
-            LOG.trace("on commit, status was marked for rollback-only");
+            log.trace("on commit, status was marked for rollback-only");
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/AbstractEhCacheRegionFactory.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/AbstractEhCacheRegionFactory.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
+
+import org.hibernate.cache.CacheDataDescription;
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.CollectionRegion;
+import org.hibernate.cache.EntityRegion;
+import org.hibernate.cache.QueryResultsRegion;
+import org.hibernate.cache.RegionFactory;
+import org.hibernate.cache.TimestampsRegion;
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/EhCache.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCache.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
+import org.hibernate.cache.Cache;
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.Timestamper;
+
-    private static final EhCacheLogger LOG = Logger.getMessageLogger(EhCacheLogger.class, EhCache.class.getName());
+    private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(EhCacheMessageLogger.class, EhCache.class.getName());
-	 * @throws CacheException
+	 * @throws org.hibernate.cache.CacheException
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/EhCacheLogger.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheMessageLogger.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
- * Defines internationalized messages for this hibernate-ehcache, with IDs ranging from 20001 to 25000 inclusively. New messages
- * must be added after the last message defined to ensure message codes are unique.
+ * The jboss-logging {@link MessageLogger} for the hibernate-ehcache module.  It reserves message ids ranging from
+ * 20001 to 25000 inclusively.
+ * <p/>
+ * New messages must be added after the last message defined to ensure message codes are unique.
-public interface EhCacheLogger extends HibernateLogger {
+public interface EhCacheMessageLogger extends CoreMessageLogger {
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/EhCacheProvider.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheProvider.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
+
+import org.hibernate.cache.Cache;
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.CacheProvider;
+import org.hibernate.cache.Timestamper;
- * Use <code>hibernate.cache.provider_class=org.hibernate.cache.EhCacheProvider</code>
+ * Use <code>hibernate.cache.provider_class=org.hibernate.cache.internal.EhCacheProvider</code>
- * Use <code>hibernate.cache.provider_class=org.hibernate.cache.EhCacheProvider</code> in the Hibernate configuration
+ * Use <code>hibernate.cache.provider_class=org.hibernate.cache.internal.EhCacheProvider</code> in the Hibernate configuration
-    private static final EhCacheLogger LOG = Logger.getMessageLogger(EhCacheLogger.class, EhCacheProvider.class.getName());
+    private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(EhCacheMessageLogger.class, EhCacheProvider.class.getName());
-     * @throws CacheException inter alia, if a cache of the same name already exists
+     * @throws org.hibernate.cache.CacheException inter alia, if a cache of the same name already exists
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/EhCacheRegionFactory.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheRegionFactory.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/SingletonEhCacheProvider.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/SingletonEhCacheProvider.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
+import org.hibernate.cache.Cache;
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.CacheProvider;
+import org.hibernate.cache.Timestamper;
+
-    private static final EhCacheLogger LOG = Logger.getMessageLogger(EhCacheLogger.class, SingletonEhCacheProvider.class.getName());
+    private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(EhCacheMessageLogger.class, SingletonEhCacheProvider.class.getName());
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/SingletonEhCacheRegionFactory.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/internal/SingletonEhCacheRegionFactory.java
-package org.hibernate.cache;
+package org.hibernate.cache.internal;
--- a/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheTest.java
+++ b/hibernate-ehcache/src/test/java/org/hibernate/test/cache/ehcache/EhCacheTest.java
-import org.hibernate.cache.EhCacheProvider;
+import org.hibernate.cache.internal.EhCacheProvider;
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractQueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractQueryImpl.java
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(
-			EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
+			EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
-import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    public static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    public static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    public static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class, QueryImpl.class.getName());
+    public static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class, QueryImpl.class.getName());
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/connection/InjectedDataSourceConnectionProvider.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/connection/InjectedDataSourceConnectionProvider.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/CriteriaQueryCompiler.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/CriteriaQueryCompiler.java
-import org.hibernate.ejb.EntityManagerLogger;
+
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackResolver.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackResolver.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerLogger.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/internal/EntityManagerMessageLogger.java
-package org.hibernate.ejb;
+package org.hibernate.ejb.internal;
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
- * Defines internationalized messages for this hibernate-entitymanager, with IDs ranging from 15001 to 20000 inclusively. New
- * messages must be added after the last message defined to ensure message codes are unique.
+ * The jboss-logging {@link MessageLogger} for the hibernate-entitymanager module.  It reserves message ids ranging from
+ * 15001 to 20000 inclusively.
+ * <p/>
+ * New messages must be added after the last message defined to ensure message codes are unique.
-public interface EntityManagerLogger extends HibernateLogger {
+public interface EntityManagerMessageLogger extends CoreMessageLogger {
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/AttributeFactory.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/AttributeFactory.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/MetadataContext.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/metamodel/MetadataContext.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/AbstractJarVisitor.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/AbstractJarVisitor.java
-import org.hibernate.ejb.EntityManagerLogger;
+
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
+
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/ExplodedJarVisitor.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/ExplodedJarVisitor.java
-import org.hibernate.ejb.EntityManagerLogger;
+
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
+
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/FileZippedJarVisitor.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/FileZippedJarVisitor.java
-import org.hibernate.ejb.EntityManagerLogger;
+
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
+
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/InputStreamZippedJarVisitor.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/InputStreamZippedJarVisitor.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
+
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/JarVisitorFactory.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/JarVisitorFactory.java
-import org.hibernate.ejb.EntityManagerLogger;
+
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/PersistenceXmlLoader.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/packaging/PersistenceXmlLoader.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/NamingHelper.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/NamingHelper.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class, NamingHelper.class.getName());
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class, NamingHelper.class.getName());
--- a/hibernate-entitymanager/src/main/java/org/hibernate/engine/EJB3CascadingAction.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/engine/EJB3CascadingAction.java
-import org.hibernate.ejb.EntityManagerLogger;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
+    private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
-
+	private static final Logger log = Logger.getLogger( BaseEntityManagerFunctionalTestCase.class );
+
-		LOG.trace( "Building session factory" );
+		log.trace( "Building session factory" );
-            LOG.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
+            log.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
-            LOG.warn("The EntityManager is not closed. Closing it.");
+            log.warn("The EntityManager is not closed. Closing it.");
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/Cat.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/Cat.java
-import static org.hibernate.testing.TestLogger.LOG;
-import java.io.Serializable;
-import java.util.ArrayList;
-import java.util.Calendar;
-import java.util.Collections;
-import java.util.Date;
-import java.util.GregorianCalendar;
-import java.util.List;
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.Calendar;
+import java.util.Collections;
+import java.util.Date;
+import java.util.GregorianCalendar;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+	private static final Logger log = Logger.getLogger( Cat.class );
-        LOG.debug("PostUpdate for: " + this.toString());
+        log.debug("PostUpdate for: " + this.toString());
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/emops/RemoveTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/emops/RemoveTest.java
-import static org.hibernate.testing.TestLogger.LOG;
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( RemoveTest.class );
+
-            LOG.debug("success");
+            log.debug("success");
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/exception/ExceptionTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/exception/ExceptionTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( ExceptionTest.class );
+
-            LOG.debug("success");
+            log.debug("success");
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/LockTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/LockTest.java
+import org.jboss.logging.Logger;
+
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( LockTest.class );
+
-            LOG.info("skipping testContendedPessimisticLock");
+            log.info("skipping testContendedPessimisticLock");
-            LOG.info("testContendedPessimisticLock: got write lock");
+            log.info("testContendedPessimisticLock: got write lock");
-                        LOG.info("testContendedPessimisticLock: (BG) about to issue (PESSIMISTIC_READ) query against write-locked entity");
+                        log.info("testContendedPessimisticLock: (BG) about to issue (PESSIMISTIC_READ) query against write-locked entity");
-            LOG.info("testContendedPessimisticLock:  wait on BG thread");
+            log.info("testContendedPessimisticLock:  wait on BG thread");
-            LOG.info("testContendedPessimisticLock:  BG thread completed transaction");
+            log.info("testContendedPessimisticLock:  BG thread completed transaction");
-            LOG.info("skipping testContendedPessimisticReadLockTimeout");
+            log.info("skipping testContendedPessimisticReadLockTimeout");
-            LOG.info("testContendedPessimisticReadLockTimeout: got write lock");
+            log.info("testContendedPessimisticReadLockTimeout: got write lock");
-                        LOG.info("testContendedPessimisticReadLockTimeout: (BG) about to read write-locked entity");
+                        log.info("testContendedPessimisticReadLockTimeout: (BG) about to read write-locked entity");
-                        LOG.info("testContendedPessimisticReadLockTimeout: (BG) read write-locked entity");
+                        log.info("testContendedPessimisticReadLockTimeout: (BG) read write-locked entity");
-                            LOG.info("testContendedPessimisticReadLockTimeout: (BG) got expected timeout exception");
+                            log.info("testContendedPessimisticReadLockTimeout: (BG) got expected timeout exception");
-                            LOG.info("Expected LockTimeoutException but got unexpected exception", e);
+                            log.info("Expected LockTimeoutException but got unexpected exception", e);
-            LOG.info("skipping testContendedPessimisticWriteLockTimeout");
+            log.info("skipping testContendedPessimisticWriteLockTimeout");
-            LOG.info("testContendedPessimisticWriteLockTimeout: got write lock");
+            log.info("testContendedPessimisticWriteLockTimeout: got write lock");
-                        LOG.info("testContendedPessimisticWriteLockTimeout: (BG) about to read write-locked entity");
+                        log.info("testContendedPessimisticWriteLockTimeout: (BG) about to read write-locked entity");
-                        LOG.info("testContendedPessimisticWriteLockTimeout: (BG) read write-locked entity");
+                        log.info("testContendedPessimisticWriteLockTimeout: (BG) read write-locked entity");
-                            LOG.info("testContendedPessimisticWriteLockTimeout: (BG) got expected timeout exception");
+                            log.info("testContendedPessimisticWriteLockTimeout: (BG) got expected timeout exception");
-                            LOG.info("Expected LockTimeoutException but got unexpected exception", e);
+                            log.info("Expected LockTimeoutException but got unexpected exception", e);
-            LOG.info("skipping testContendedPessimisticWriteLockNoWait");
+            log.info("skipping testContendedPessimisticWriteLockNoWait");
-            LOG.info("testContendedPessimisticWriteLockNoWait: got write lock");
+            log.info("testContendedPessimisticWriteLockNoWait: got write lock");
-                        LOG.info("testContendedPessimisticWriteLockNoWait: (BG) about to read write-locked entity");
+                        log.info("testContendedPessimisticWriteLockNoWait: (BG) about to read write-locked entity");
-                        LOG.info("testContendedPessimisticWriteLockNoWait: (BG) read write-locked entity");
+                        log.info("testContendedPessimisticWriteLockNoWait: (BG) read write-locked entity");
-                            LOG.info("testContendedPessimisticWriteLockNoWait: (BG) got expected timeout exception");
+                            log.info("testContendedPessimisticWriteLockNoWait: (BG) got expected timeout exception");
-                            LOG.info("Expected LockTimeoutException but got unexpected exception", e);
+                            log.info("Expected LockTimeoutException but got unexpected exception", e);
-            LOG.info("skipping testQueryTimeout");
+            log.info("skipping testQueryTimeout");
-            LOG.info("testQueryTimeout: got write lock");
+            log.info("testQueryTimeout: got write lock");
-                        LOG.info("testQueryTimeout: (BG) about to read write-locked entity");
+                        log.info("testQueryTimeout: (BG) about to read write-locked entity");
-                        LOG.info("testQueryTimeout: (BG) read write-locked entity");
+                        log.info("testQueryTimeout: (BG) read write-locked entity");
-                            LOG.info("testQueryTimeout: name read =" + name);
+                            log.info("testQueryTimeout: name read =" + name);
-                            LOG.info("testQueryTimeout: (BG) got expected timeout exception");
+                            log.info("testQueryTimeout: (BG) got expected timeout exception");
-                            LOG.info("testQueryTimeout: Expected LockTimeoutException but got unexpected exception", e);
+                            log.info("testQueryTimeout: Expected LockTimeoutException but got unexpected exception", e);
-            LOG.info("skipping testQueryTimeout");
+            log.info("skipping testQueryTimeout");
-            LOG.info("testQueryTimeout: got write lock");
+            log.info("testQueryTimeout: got write lock");
-                        LOG.info("testQueryTimeout: (BG) about to read write-locked entity");
+                        log.info("testQueryTimeout: (BG) about to read write-locked entity");
-                        LOG.info("testQueryTimeout: (BG) read write-locked entity");
+                        log.info("testQueryTimeout: (BG) read write-locked entity");
-                            LOG.info("testQueryTimeout: name read =" + name);
+                            log.info("testQueryTimeout: name read =" + name);
-                            LOG.info("testQueryTimeout: (BG) got expected timeout exception");
+                            log.info("testQueryTimeout: (BG) got expected timeout exception");
-                            LOG.info("testQueryTimeout: Expected LockTimeoutException but got unexpected exception", e);
+                            log.info("testQueryTimeout: Expected LockTimeoutException but got unexpected exception", e);
-            LOG.info("skipping testLockTimeoutEMProps");
+            log.info("skipping testLockTimeoutEMProps");
-            LOG.info("testLockTimeoutEMProps: got write lock");
+            log.info("testLockTimeoutEMProps: got write lock");
-                        LOG.info("testLockTimeoutEMProps: (BG) about to read write-locked entity");
+                        log.info("testLockTimeoutEMProps: (BG) about to read write-locked entity");
-                        LOG.info("testLockTimeoutEMProps: (BG) read write-locked entity");
+                        log.info("testLockTimeoutEMProps: (BG) read write-locked entity");
-                            LOG.info("testLockTimeoutEMProps: (BG) got expected timeout exception");
+                            log.info("testLockTimeoutEMProps: (BG) got expected timeout exception");
-                            LOG.info("Expected LockTimeoutException but got unexpected exception", e);
+                            log.info("Expected LockTimeoutException but got unexpected exception", e);
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/ClassesAuditingData.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/ClassesAuditingData.java
-import org.hibernate.envers.EnversLogger;
+import org.hibernate.envers.internal.EnversMessageLogger;
-    public static final EnversLogger LOG = Logger.getMessageLogger(EnversLogger.class, ClassesAuditingData.class.getName());
+    public static final EnversMessageLogger LOG = Logger.getMessageLogger(EnversMessageLogger.class, ClassesAuditingData.class.getName());
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/AuditMetadataGenerator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/AuditMetadataGenerator.java
-import org.hibernate.envers.EnversLogger;
+import org.hibernate.envers.internal.EnversMessageLogger;
-    public static final EnversLogger LOG = Logger.getMessageLogger(EnversLogger.class, AuditMetadataGenerator.class.getName());
+    public static final EnversMessageLogger LOG = Logger.getMessageLogger(EnversMessageLogger.class, AuditMetadataGenerator.class.getName());
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/CollectionMetadataGenerator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/CollectionMetadataGenerator.java
-import org.hibernate.envers.EnversLogger;
+import org.hibernate.envers.internal.EnversMessageLogger;
-    public static final EnversLogger LOG = Logger.getMessageLogger(EnversLogger.class, CollectionMetadataGenerator.class.getName());
+    public static final EnversMessageLogger LOG = Logger.getMessageLogger(EnversMessageLogger.class, CollectionMetadataGenerator.class.getName());
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
-	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, EnversIntegrator.class.getName() );
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, EnversIntegrator.class.getName() );
--- a/hibernate-envers/src/main/java/org/hibernate/envers/EnversLogger.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/internal/EnversMessageLogger.java
-package org.hibernate.envers;
+package org.hibernate.envers.internal;
-import org.hibernate.HibernateLogger;
+import org.hibernate.internal.CoreMessageLogger;
+
- * Defines internationalized messages for this hibernate-envers, with IDs ranging from 25001 to 30000 inclusively. New messages must
- * be added after the last message defined to ensure message codes are unique.
+ * The jboss-logging {@link MessageLogger} for the hibernate-envers module.  It reserves message ids ranging from
+ * 25001 to 30000 inclusively.
+ * <p/>
+ * New messages must be added after the last message defined to ensure message codes are unique.
-public interface EnversLogger extends HibernateLogger {
+public interface EnversMessageLogger extends CoreMessageLogger {
--- a/hibernate-envers/src/main/java/org/hibernate/envers/reader/FirstLevelCache.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/reader/FirstLevelCache.java
-import org.hibernate.envers.EnversLogger;
+import org.hibernate.envers.internal.EnversMessageLogger;
- * @author Hern&aacute;n Chanfreau
+ * @author Hern&aacute;n Chanfreau
-    public static final EnversLogger LOG = Logger.getMessageLogger(EnversLogger.class, FirstLevelCache.class.getName());
+    public static final EnversMessageLogger LOG = Logger.getMessageLogger(EnversMessageLogger.class, FirstLevelCache.class.getName());
-    /**
-     * cache for resolve an object for a given id, revision and entityName.
-     */
+    /**
+     * cache for resolve an object for a given id, revision and entityName.
+     */
-    /**
-     * used to resolve the entityName for a given id, revision and entity.
-     */
-    private final Map<Triple<Object, Number, Object>, String> entityNameCache;
-
+    /**
+     * used to resolve the entityName for a given id, revision and entity.
+     */
+    private final Map<Triple<Object, Number, Object>, String> entityNameCache;
+
-        entityNameCache = newHashMap();
+        entityNameCache = newHashMap();
-        LOG.debugf("Resolving object from First Level Cache: EntityName:%s - primaryKey:%s - revision:%s", entityName, id, revision);
+        LOG.debugf("Resolving object from First Level Cache: EntityName:%s - primaryKey:%s - revision:%s", entityName, id, revision);
-        LOG.debugf("Caching entity on First Level Cache:  - primaryKey:%s - revision:%s - entityName:%s", id, revision, entityName);
+        LOG.debugf("Caching entity on First Level Cache:  - primaryKey:%s - revision:%s - entityName:%s", id, revision, entityName);
-
-    /**
-     * Adds the entityName into the cache. The key is a triple make with primaryKey, revision and entity
-     * @param entityName, value of the cache
-     * @param id, primaryKey
-     * @param revision, revision number
-     * @param entity, object retrieved by envers
-     */
-    public void putOnEntityNameCache(Object id, Number revision, Object entity, String entityName) {
+
+    /**
+     * Adds the entityName into the cache. The key is a triple make with primaryKey, revision and entity
+     * @param entityName, value of the cache
+     * @param id, primaryKey
+     * @param revision, revision number
+     * @param entity, object retrieved by envers
+     */
+    public void putOnEntityNameCache(Object id, Number revision, Object entity, String entityName) {
-                   entityName);
-    	entityNameCache.put(make(id, revision, entity), entityName);
-    }
-
-    /**
-     * Gets the entityName from the cache. The key is a triple make with primaryKey, revision and entity
-     * @param entityName, value of the cache
-     * @param id, primaryKey
-     * @param revision, revision number
-     * @param entity, object retrieved by envers
-     */
-    public String getFromEntityNameCache(Object id, Number revision, Object entity) {
+                   entityName);
+    	entityNameCache.put(make(id, revision, entity), entityName);
+    }
+
+    /**
+     * Gets the entityName from the cache. The key is a triple make with primaryKey, revision and entity
+     * @param entityName, value of the cache
+     * @param id, primaryKey
+     * @param revision, revision number
+     * @param entity, object retrieved by envers
+     */
+    public String getFromEntityNameCache(Object id, Number revision, Object entity) {
-                   entity);
-    	return entityNameCache.get(make(id, revision, entity));
-    }
-
-	/**
-	 * @param id
-	 *            , primaryKey
-	 * @param revision
-	 *            , revision number
-	 * @param entity
-	 *            , object retrieved by envers
-	 * @return true if entityNameCache contains the triple
-	 */
-    public boolean containsEntityName(Object id, Number revision, Object entity) {
-    	return entityNameCache.containsKey(make(id, revision, entity));
-    }
+                   entity);
+    	return entityNameCache.get(make(id, revision, entity));
+    }
+
+	/**
+	 * @param id
+	 *            , primaryKey
+	 * @param revision
+	 *            , revision number
+	 * @param entity
+	 *            , object retrieved by envers
+	 * @return true if entityNameCache contains the triple
+	 */
+    public boolean containsEntityName(Object id, Number revision, Object entity) {
+    	return entityNameCache.containsKey(make(id, revision, entity));
+    }
--- a/hibernate-envers/src/main/java/org/hibernate/envers/strategy/ValidTimeAuditStrategy.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/strategy/ValidTimeAuditStrategy.java
-import org.hibernate.envers.EnversLogger;
+import org.hibernate.envers.internal.EnversMessageLogger;
+
-
-/**
+
+/**
- *
- * @author Stephanie Pau
- * @author Adam Warski (adam at warski dot org)
+ *
+ * @author Stephanie Pau
+ * @author Adam Warski (adam at warski dot org)
- */
-@Deprecated
+ */
+@Deprecated
-
-    public static final EnversLogger LOG = Logger.getMessageLogger(EnversLogger.class, ValidTimeAuditStrategy.class.getName());
-
+
+    public static final EnversMessageLogger LOG = Logger.getMessageLogger(EnversMessageLogger.class, ValidTimeAuditStrategy.class.getName());
+
-	}
-
-}
+	}
+
+}
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/components/DefaultValueComponents.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/integration/components/DefaultValueComponents.java
-import static org.hibernate.testing.TestLogger.*;
+import org.jboss.logging.Logger;
+	private static final Logger log = Logger.getLogger( DefaultValueComponents.class );
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error(getAuditReader().getRevisions(
+        log.error(getAuditReader().getRevisions(
-        LOG.error("------------ id0 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id0 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
-        LOG.error("------------ id1 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id1 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
-        LOG.error("------------ id2 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id2 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
-        LOG.error("------------ id3 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id3 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
-        LOG.error("------------ id4 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id4 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
-        LOG.error("------------ id5 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id5 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
-        LOG.error("------------ id6 -------------");
-        LOG.error(ent1.toString());
-        LOG.error(ent2.toString());
+        log.error("------------ id6 -------------");
+        log.error(ent1.toString());
+        log.error(ent2.toString());
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( AbstractGeneralDataRegionTestCase.class );
+
-			LOG.error( e.getMessage(), e );
+			log.error( e.getMessage(), e );
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
-
-    public static final String REGION_PREFIX = "test";
+	private static final Logger log = Logger.getLogger( AbstractNonFunctionalTestCase.class );
+
+	public static final String REGION_PREFIX = "test";
-            LOG.warn("Interrupted during sleep", e);
+            log.warn("Interrupted during sleep", e);
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( AbstractCollectionRegionAccessStrategyTestCase.class );
-					LOG.debug( "Interrupted" );
+					log.debug( "Interrupted" );
-					LOG.error( "Error", e );
+					log.error( "Error", e );
-					LOG.error( "node1 caught exception", e );
+					log.error( "node1 caught exception", e );
-					LOG.error( "node2 caught exception", e );
+					log.error( "node2 caught exception", e );
-			LOG.error( e.getMessage(), e );
+			log.error( e.getMessage(), e );
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( AbstractEntityRegionAccessStrategyTestCase.class );
-            LOG.error("node1 saw an exception", node1Exception);
+            log.error("node1 saw an exception", node1Exception);
-            LOG.error("node2 saw an exception", node2Exception);
+            log.error("node2 saw an exception", node2Exception);
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
-                    LOG.error("node2 caught exception", e);
+                    log.error("node2 caught exception", e);
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
-                    LOG.debug("Transaction began, get initial value");
+                    log.debug("Transaction began, get initial value");
-                    LOG.debug("Now update value");
+                    log.debug("Now update value");
-                    LOG.debug("Notify the read latch");
+                    log.debug("Notify the read latch");
-                    LOG.debug("Await commit");
+                    log.debug("Await commit");
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
-                    LOG.debug("Completion latch countdown");
+                    log.debug("Completion latch countdown");
-                    LOG.debug("Transaction began, await read latch");
+                    log.debug("Transaction began, await read latch");
-                    LOG.debug("Read latch acquired, verify local access strategy");
+                    log.debug("Read latch acquired, verify local access strategy");
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
-                    LOG.debug("Completion latch countdown");
+                    log.debug("Completion latch countdown");
-            LOG.debug("Call evict all locally");
+            log.debug("Call evict all locally");
-            LOG.error(e.getMessage(), e);
+            log.error(e.getMessage(), e);
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
-   @Override
+	private static final Logger log = Logger.getLogger( AbstractTransactionalAccessTestCase.class );
+
+	@Override
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
-                    LOG.error("node1 caught exception", e);
+                    log.error("node1 caught exception", e);
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
-import static org.hibernate.testing.TestLogger.LOG;
+
-import org.hibernate.cache.infinispan.util.CacheHelper;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cache.infinispan.util.CacheHelper;
+	private static final Logger log = Logger.getLogger( CacheAccessListener.class );
-    HashSet modified = new HashSet();
+	HashSet modified = new HashSet();
-            LOG.info("Modified node " + key);
+            log.info("Modified node " + key);
-            LOG.info("Created node " + key);
+            log.info("Created node " + key);
-            LOG.info("Visited node " + key);
+            log.info("Visited node " + key);
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
-import org.hibernate.test.cache.infinispan.functional.Item;
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
-import org.junit.After;
-import org.junit.Before;
+	private static final Logger log = Logger.getLogger( IsolatedClassLoaderTest.class );
+
-		LOG.info( "TCCL is " + cl.getParent() );
+		log.info( "TCCL is " + cl.getParent() );
-				LOG.info( "Caught exception as desired", e );
+				log.info( "Caught exception as desired", e );
-		LOG.info( "TCCL is " + cl );
+		log.info( "TCCL is " + cl );
-//      LOG.info("First query (get count for branch + " + branch + " ) on node0 done, contents of local query cache are: " + TestingUtil.printCache(localQueryCache));
+//      log.info("First query (get count for branch + " + branch + " ) on node0 done, contents of local query cache are: " + TestingUtil.printCache(localQueryCache));
-		LOG.info( "Repeat first query (get count for branch + " + branch + " ) on remote node" );
+		log.info( "Repeat first query (get count for branch + " + branch + " ) on remote node" );
-		LOG.info( "First query on node 1 done" );
+		log.info( "First query on node 1 done" );
-		LOG.info( "Do query Smith's branch" );
+		log.info( "Do query Smith's branch" );
-		LOG.info( "Do query Jone's balance" );
+		log.info( "Do query Jone's balance" );
-		LOG.info( "Second set of queries on node0 done" );
+		log.info( "Second set of queries on node0 done" );
-		LOG.info( "Repeat second set of queries on node1" );
+		log.info( "Repeat second set of queries on node1" );
-		LOG.info( "Again query Smith's branch" );
+		log.info( "Again query Smith's branch" );
-		LOG.info( "Again query Jone's balance" );
+		log.info( "Again query Jone's balance" );
-		LOG.info( "Second set of queries on node1 done" );
+		log.info( "Second set of queries on node1 done" );
-		LOG.info( "Third set of queries on node0 done" );
+		log.info( "Third set of queries on node0 done" );
-		LOG.info( "Standard entities created" );
+		log.info( "Standard entities created" );
-		LOG.info( "Region usage state cleared" );
+		log.info( "Region usage state cleared" );
-		LOG.info( "Entities modified" );
+		log.info( "Entities modified" );
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
-import static org.hibernate.testing.TestLogger.LOG;
+
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( SelectedClassnameClassLoader.class );
-    private String[] includedClasses = null;
+	private String[] includedClasses = null;
-        LOG.debug("created " + this);
+        log.debug("created " + this);
-        LOG.trace("loadClass(" + name + "," + resolve + ")");
+        log.trace("loadClass(" + name + "," + resolve + ")");
-        LOG.trace("findClass(" + name + ")");
+        log.trace("findClass(" + name + ")");
-        LOG.info("createClass(" + name + ")");
+        log.info("createClass(" + name + ")");
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( SessionRefreshTestCase.class );
+
-		LOG.debug( "Contents when re-reading from local: " + TestingUtil.printCache( localCache ) );
+		log.debug( "Contents when re-reading from local: " + TestingUtil.printCache( localCache ) );
-		LOG.debug( "Contents after refreshing in remote: " + TestingUtil.printCache( localCache ) );
+		log.debug( "Contents after refreshing in remote: " + TestingUtil.printCache( localCache ) );
-		LOG.debug( "Contents after creating a new session: " + TestingUtil.printCache( localCache ) );
+		log.debug( "Contents after creating a new session: " + TestingUtil.printCache( localCache ) );
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
+	private static final Logger log = Logger.getLogger( QueryRegionImplTestCase.class );
-					LOG.debug( "Transaction began, get value for key" );
+					log.debug( "Transaction began, get value for key" );
-					LOG.debug( "Put value2" );
+					log.debug( "Put value2" );
-					LOG.debug( "Put finished for value2, await writer latch" );
+					log.debug( "Put finished for value2, await writer latch" );
-					LOG.debug( "Writer latch finished" );
+					log.debug( "Writer latch finished" );
-					LOG.debug( "Transaction committed" );
+					log.debug( "Transaction committed" );
-					LOG.error( "Interrupted waiting for latch", e );
+					log.error( "Interrupted waiting for latch", e );
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestSupport.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestSupport.java
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
-
+	private static final Logger log = Logger.getLogger( CacheTestSupport.class );
-    private static final String PREFER_IPV4STACK = "java.net.preferIPv4Stack";
+	private static final String PREFER_IPV4STACK = "java.net.preferIPv4Stack";
-            LOG.warn("Interrupted during sleep", e);
+            log.warn("Interrupted during sleep", e);
--- a/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
+++ b/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
-    public static final ProxoolLogger LOG = Logger.getMessageLogger(ProxoolLogger.class, ProxoolConnectionProvider.class.getName());
+    public static final ProxoolMessageLogger LOG = Logger.getMessageLogger(ProxoolMessageLogger.class, ProxoolConnectionProvider.class.getName());
--- a/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolLogger.java
+++ b/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolMessageLogger.java
-import org.hibernate.HibernateLogger;
+
+import org.hibernate.internal.CoreMessageLogger;
+
- * Defines internationalized messages for this hibernate-proxool, with IDs ranging from 30001 to 35000 inclusively. New messages
- * must be added after the last message defined to ensure message codes are unique.
+ * The jboss-logging {@link MessageLogger} for the hibernate-proxool module.  It reserves message ids ranging from
+ * 30001 to 35000 inclusively.
+ * <p/>
+ * New messages must be added after the last message defined to ensure message codes are unique.
-public interface ProxoolLogger extends HibernateLogger {
+public interface ProxoolMessageLogger extends CoreMessageLogger {
--- a/hibernate-testing/src/main/java/org/hibernate/testing/SkipLog.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/SkipLog.java
-import org.hibernate.testing.junit4.SkipMarker;
+import org.jboss.logging.Logger;
-import static org.hibernate.testing.TestLogger.LOG;
+import org.hibernate.testing.junit4.SkipMarker;
+	private static final Logger log = Logger.getLogger( SkipLog.class );
+
-		LOG.info( "*** skipping test [" + skipMarker.getTestName() + "] - " + skipMarker.getReason(), new Exception() );
+		log.info( "*** skipping test [" + skipMarker.getTestName() + "] - " + skipMarker.getReason(), new Exception() );
-		LOG.info( "*** skipping test - " + message, new Exception() );
+		log.info( "*** skipping test - " + message, new Exception() );
--- a/hibernate-testing/src/main/java/org/hibernate/testing/TestLogger.java
+++ /dev/null
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.testing;
-import org.jboss.logging.BasicLogger;
-import org.jboss.logging.Logger;
-import org.jboss.logging.MessageLogger;
-
-/**
- * Logging support for the hibernate-testing module
- */
-@MessageLogger( projectCode = "HHH" )
-public interface TestLogger extends BasicLogger {
-    public static final TestLogger LOG = Logger.getMessageLogger(TestLogger.class, TestLogger.class.getName());
-}
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseUnitTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseUnitTestCase.java
-import org.hibernate.testing.TestLogger;
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( BaseUnitTestCase.class );
+
-			TestLogger.LOG.warn( "Cleaning up unfinished transaction" );
+			log.warn( "Cleaning up unfinished transaction" );
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/CustomRunner.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/CustomRunner.java
-import static org.hibernate.testing.TestLogger.LOG;
-
+import org.jboss.logging.Logger;
+
+	private static final Logger log = Logger.getLogger( CustomRunner.class );
-			LOG.trace( "adding test " + Helper.extractTestName( frameworkMethod ) + " [#" + testCount + "]" );
+			log.trace( "adding test " + Helper.extractTestName( frameworkMethod ) + " [#" + testCount + "]" );
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/FailureExpectedHandler.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/FailureExpectedHandler.java
+import org.jboss.logging.Logger;
+
-import org.hibernate.testing.TestLogger;
+	private static final Logger log = Logger.getLogger( FailureExpectedHandler.class );
+
+
-				TestLogger.LOG.infof(
+				log.infof(

Lines added: 2147. Lines removed: 1962. Tot = 4109
********************************************
********************************************
75/Between/ HHH-6100  01fe115a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6100  ae5d030a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6101  38068e14_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6110  814b5149_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6196  fb44ad93_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDefaultFilterCondition() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition();

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6330  4a4f636c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public String getDefaultFilterCondition() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6371  18215076_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6371  594f689d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(
-//				parseFilter( subElement, entityBinding );

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6447  c7421837_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6724  a351c520_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-        LOG.debugf("Applying filter [%s] as [%s]", name, condition);
+		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(String name, String condition) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6791  bec88716_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6813  bc6f5d8a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6817  6c7379c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-6970  57e9b485_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-6970  d50a66bc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-7300  8fa530a2_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-7387  3edb72db_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-7580  7976e239_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		//      map directly) sometime during Configuration.buildSessionFactory
+		//      map directly) sometime during Configuration.build

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-7807  456dfd83_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-7860  2883cb85_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-7908  cbbadea5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-8159  459c061e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-8159  b51164ae_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(Filter filter) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-8217  9030fa01_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-8276  803c73c5_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-8520  36770456_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-8662  412d5d6f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
********************************************
********************************************
75/Between/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void debug(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void addFilter(Filter filter) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-9388  1cba9802_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(Filter filter) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-9388  52f2c3a0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(Filter filter) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
75/Between/ HHH-9490  9caca0ce_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
parseFilter(
-			parseFilter( filter, collection, mappings );
-				parseFilter( subnode, persistentClass, mappings );
-	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {

Lines added containing method: 0. Lines removed containing method: 3. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* debug
* attributeValue
* addFilter
* getDefaultFilterCondition
* getTextTrim
* getFilterDefinition
—————————
Method found in diff:	public void addFilter(Filter filter) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDefaultFilterCondition() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException;

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
