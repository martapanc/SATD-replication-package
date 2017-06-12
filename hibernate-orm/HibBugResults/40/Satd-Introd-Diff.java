diff --git a/code/core/src/main/java/org/hibernate/exception/Configurable.java b/code/core/src/main/java/org/hibernate/exception/Configurable.java
new file mode 100644
index 0000000000..be0fe0b49d
--- /dev/null
+++ b/code/core/src/main/java/org/hibernate/exception/Configurable.java
@@ -0,0 +1,24 @@
+// $Id: Configurable.java 4746 2004-11-11 20:57:28Z steveebersole $
+package org.hibernate.exception;
+
+import org.hibernate.HibernateException;
+
+import java.util.Properties;
+
+/**
+ * The Configurable interface defines the contract for SQLExceptionConverter impls that
+ * want to be configured prior to usage given the currently defined Hibernate properties.
+ *
+ * @author Steve Ebersole
+ */
+public interface Configurable {
+	// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.
+
+	/**
+	 * Configure the component, using the given settings and properties.
+	 *
+	 * @param properties All defined startup properties.
+	 * @throws HibernateException Indicates a configuration exception.
+	 */
+	public void configure(Properties properties) throws HibernateException;
+}
