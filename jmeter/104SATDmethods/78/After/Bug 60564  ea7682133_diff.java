diff --git a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
index 2a350c36c..68acaab64 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
@@ -1,664 +1,661 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.testelement;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.gui.Searchable;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.testelement.property.MapProperty;
 import org.apache.jmeter.testelement.property.MultiProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.PropertyIteratorImpl;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  */
 public abstract class AbstractTestElement implements TestElement, Serializable, Searchable {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AbstractTestElement.class);
 
     private final Map<String, JMeterProperty> propMap =
         Collections.synchronizedMap(new LinkedHashMap<String, JMeterProperty>());
 
     /**
      * Holds properties added when isRunningVersion is true
      */
     private transient Set<JMeterProperty> temporaryProperties;
 
     private transient boolean runningVersion = false;
 
     // Thread-specific variables saved here to save recalculation
     private transient JMeterContext threadContext = null;
 
     private transient String threadName = null;
 
     @Override
     public Object clone() {
         try {
             TestElement clonedElement = this.getClass().newInstance();
 
             PropertyIterator iter = propertyIterator();
             while (iter.hasNext()) {
                 clonedElement.setProperty(iter.next().clone());
             }
             clonedElement.setRunningVersion(runningVersion);
             return clonedElement;
         } catch (InstantiationException | IllegalAccessException e) {
             throw new AssertionError(e); // clone should never return null
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void clear() {
         propMap.clear();
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Default implementation - does nothing
      */
     @Override
     public void clearTestElementChildren(){
         // NOOP
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void removeProperty(String key) {
         propMap.remove(key);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean equals(Object o) {
         if (o instanceof AbstractTestElement) {
             return ((AbstractTestElement) o).propMap.equals(propMap);
         } else {
             return false;
         }
     }
 
     // TODO temporary hack to avoid unnecessary bug reports for subclasses
 
     /**
      * {@inheritDoc}
      */
     @Override
     public int hashCode(){
         return System.identityHashCode(this);
     }
 
     /*
      * URGENT: TODO - sort out equals and hashCode() - at present equal
      * instances can/will have different hashcodes - problem is, when a proper
      * hashcode is used, tests stop working, e.g. listener data disappears when
      * switching views... This presumably means that instances currently
      * regarded as equal, aren't really equal.
      *
      * @see java.lang.Object#hashCode()
      */
     // This would be sensible, but does not work:
     // public int hashCode()
     // {
     // return propMap.hashCode();
     // }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void addTestElement(TestElement el) {
         mergeIn(el);
     }
 
     @Override
     public void setName(String name) {
         setProperty(TestElement.NAME, name);
     }
 
     @Override
     public String getName() {
         return getPropertyAsString(TestElement.NAME);
     }
 
     @Override
     public void setComment(String comment){
         setProperty(new StringProperty(TestElement.COMMENTS, comment));
     }
 
     @Override
     public String getComment(){
         return getProperty(TestElement.COMMENTS).getStringValue();
     }
 
     /**
      * Get the named property. If it doesn't exist, a new NullProperty object is
      * created with the same name and returned.
      */
     @Override
     public JMeterProperty getProperty(String key) {
         JMeterProperty prop = propMap.get(key);
         if (prop == null) {
             prop = new NullProperty(key);
         }
         return prop;
     }
     
     /**
      * Null property are wrapped in a {@link NullProperty}
      * This method avoids this wrapping
      * for internal use only
      * @since 3.1
      */
     private JMeterProperty getRawProperty(String key) {
         return propMap.get(key);
     }
 
     @Override
     public void traverse(TestElementTraverser traverser) {
         PropertyIterator iter = propertyIterator();
         traverser.startTestElement(this);
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
         traverser.endTestElement(this);
     }
 
     protected void traverseProperty(TestElementTraverser traverser, JMeterProperty value) {
         traverser.startProperty(value);
         if (value instanceof TestElementProperty) {
             ((TestElement) value.getObjectValue()).traverse(traverser);
         } else if (value instanceof CollectionProperty) {
             traverseCollection((CollectionProperty) value, traverser);
         } else if (value instanceof MapProperty) {
             traverseMap((MapProperty) value, traverser);
         }
         traverser.endProperty(value);
     }
 
     protected void traverseMap(MapProperty map, TestElementTraverser traverser) {
         PropertyIterator iter = map.valueIterator();
         while (iter.hasNext()) {
             traverseProperty(traverser, iter.next());
         }
     }
 
     protected void traverseCollection(CollectionProperty col, TestElementTraverser traverser) {
         for (JMeterProperty jMeterProperty :  col) {
             traverseProperty(traverser, jMeterProperty);
         }
     }
 
     @Override
     public int getPropertyAsInt(String key) {
         return getProperty(key).getIntValue();
     }
 
     @Override
     public int getPropertyAsInt(String key, int defaultValue) {
         JMeterProperty jmp = getRawProperty(key);
         return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getIntValue();
     }
 
     @Override
     public boolean getPropertyAsBoolean(String key) {
         return getProperty(key).getBooleanValue();
     }
 
     @Override
     public boolean getPropertyAsBoolean(String key, boolean defaultVal) {
         JMeterProperty jmp = getRawProperty(key);
         return jmp == null || jmp instanceof NullProperty ? defaultVal : jmp.getBooleanValue();
     }
 
     @Override
     public float getPropertyAsFloat(String key) {
         return getProperty(key).getFloatValue();
     }
 
     @Override
     public long getPropertyAsLong(String key) {
         return getProperty(key).getLongValue();
     }
 
     @Override
     public long getPropertyAsLong(String key, long defaultValue) {
         JMeterProperty jmp = getRawProperty(key);
         return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getLongValue();
     }
 
     @Override
     public double getPropertyAsDouble(String key) {
         return getProperty(key).getDoubleValue();
     }
 
     @Override
     public String getPropertyAsString(String key) {
         return getProperty(key).getStringValue();
     }
 
     @Override
     public String getPropertyAsString(String key, String defaultValue) {
         JMeterProperty jmp = getRawProperty(key);
         return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getStringValue();
     }
 
     /**
      * Add property to test element
      * @param property {@link JMeterProperty} to add to current Test Element
      * @param clone clone property
      */
     protected void addProperty(JMeterProperty property, boolean clone) {
         JMeterProperty propertyToPut = property;
         if(clone) {
             propertyToPut = property.clone();
         }
         if (isRunningVersion()) {
             setTemporary(propertyToPut);
         } else {
             clearTemporary(property);
         }
         JMeterProperty prop = getProperty(property.getName());
 
         if (prop instanceof NullProperty || (prop instanceof StringProperty && prop.getStringValue().isEmpty())) {
             propMap.put(property.getName(), propertyToPut);
         } else {
             prop.mergeIn(propertyToPut);
         }
     }
 
     /**
      * Add property to test element without cloning it
      * @param property {@link JMeterProperty}
      */
     protected void addProperty(JMeterProperty property) {
         addProperty(property, false);
     }
 
     /**
      * Remove property from temporaryProperties
      * @param property {@link JMeterProperty}
      */
     protected void clearTemporary(JMeterProperty property) {
         if (temporaryProperties != null) {
             temporaryProperties.remove(property);
         }
     }
 
     /**
      * Log the properties of the test element
      *
      * @see TestElement#setProperty(JMeterProperty)
      */
     protected void logProperties() {
         if (log.isDebugEnabled()) {
             PropertyIterator iter = propertyIterator();
             while (iter.hasNext()) {
                 JMeterProperty prop = iter.next();
-                log.debug("Property " + prop.getName() + " is temp? " + isTemporary(prop) + " and is a "
-                        + prop.getObjectValue());
+                log.debug("Property {} is temp? {} and is a {}", prop.getName(), isTemporary(prop),
+                        prop.getObjectValue());
             }
         }
     }
 
     @Override
     public void setProperty(JMeterProperty property) {
         if (isRunningVersion()) {
             if (getProperty(property.getName()) instanceof NullProperty) {
                 addProperty(property);
             } else {
                 getProperty(property.getName()).setObjectValue(property.getObjectValue());
             }
         } else {
             propMap.put(property.getName(), property);
         }
     }
 
     @Override
     public void setProperty(String name, String value) {
         setProperty(new StringProperty(name, value));
     }
 
     /**
      * Create a String property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, String value, String dflt) {
         if (dflt.equals(value)) {
             removeProperty(name);
         } else {
             setProperty(new StringProperty(name, value));
         }
     }
 
     @Override
     public void setProperty(String name, boolean value) {
         setProperty(new BooleanProperty(name, value));
     }
 
     /**
      * Create a boolean property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, boolean value, boolean dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new BooleanProperty(name, value));
         }
     }
 
     @Override
     public void setProperty(String name, int value) {
         setProperty(new IntegerProperty(name, value));
     }
 
     /**
      * Create an int property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, int value, int dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new IntegerProperty(name, value));
         }
     }
     
     @Override
     public void setProperty(String name, long value) {
         setProperty(new LongProperty(name, value));
     }
     
     /**
      * Create a long property - but only if it is not the default.
      * This is intended for use when adding new properties to JMeter
      * so that JMX files are not expanded unnecessarily.
      *
      * N.B. - must agree with the default applied when reading the property.
      *
      * @param name property name
      * @param value current value
      * @param dflt default
      */
     @Override
     public void setProperty(String name, long value, long dflt) {
         if (value == dflt) {
             removeProperty(name);
         } else {
             setProperty(new LongProperty(name, value));
         }
     }
 
     @Override
     public PropertyIterator propertyIterator() {
         return new PropertyIteratorImpl(propMap.values());
     }
 
     /**
      * Add to this the properties of element (by reference)
      * @param element {@link TestElement}
      */
     protected void mergeIn(TestElement element) {
         PropertyIterator iter = element.propertyIterator();
         while (iter.hasNext()) {
             JMeterProperty prop = iter.next();
             addProperty(prop, false);
         }
     }
 
     /**
      * Returns the runningVersion.
      */
     @Override
     public boolean isRunningVersion() {
         return runningVersion;
     }
 
     /**
      * Sets the runningVersion.
      *
      * @param runningVersion
      *            the runningVersion to set
      */
     @Override
     public void setRunningVersion(boolean runningVersion) {
         this.runningVersion = runningVersion;
         PropertyIterator iter = propertyIterator();
         while (iter.hasNext()) {
             iter.next().setRunningVersion(runningVersion);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void recoverRunningVersion() {
         Iterator<Map.Entry<String, JMeterProperty>>  iter = propMap.entrySet().iterator();
         while (iter.hasNext()) {
             Map.Entry<String, JMeterProperty> entry = iter.next();
             JMeterProperty prop = entry.getValue();
             if (isTemporary(prop)) {
                 iter.remove();
                 clearTemporary(prop);
             } else {
                 prop.recoverRunningVersion(this);
             }
         }
         emptyTemporary();
     }
 
     /**
      * Clears temporaryProperties
      */
     protected void emptyTemporary() {
         if (temporaryProperties != null) {
             temporaryProperties.clear();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean isTemporary(JMeterProperty property) {
         if (temporaryProperties == null) {
             return false;
         } else {
             return temporaryProperties.contains(property);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void setTemporary(JMeterProperty property) {
         if (temporaryProperties == null) {
             temporaryProperties = new LinkedHashSet<>();
         }
         temporaryProperties.add(property);
         if (property instanceof MultiProperty) {
             for (JMeterProperty jMeterProperty : (MultiProperty) property) {
                 setTemporary(jMeterProperty);
             }
         }
     }
 
     /**
      * @return Returns the threadContext.
      */
     @Override
     public JMeterContext getThreadContext() {
         if (threadContext == null) {
             /*
              * Only samplers have the thread context set up by JMeterThread at
              * present, so suppress the warning for now
              */
-            // log.warn("ThreadContext was not set up - should only happen in
-            // JUnit testing..."
-            // ,new Throwable("Debug"));
             threadContext = JMeterContextService.getContext();
         }
         return threadContext;
     }
 
     /**
      * @param inthreadContext
      *            The threadContext to set.
      */
     @Override
     public void setThreadContext(JMeterContext inthreadContext) {
         if (threadContext != null) {
             if (inthreadContext != threadContext) {
                 throw new RuntimeException("Attempting to reset the thread context");
             }
         }
         this.threadContext = inthreadContext;
     }
 
     /**
      * @return Returns the threadName.
      */
     @Override
     public String getThreadName() {
         return threadName;
     }
 
     /**
      * @param inthreadName
      *            The threadName to set.
      */
     @Override
     public void setThreadName(String inthreadName) {
         if (threadName != null) {
             if (!threadName.equals(inthreadName)) {
                 throw new RuntimeException("Attempting to reset the thread name");
             }
         }
         this.threadName = inthreadName;
     }
 
     public AbstractTestElement() {
         super();
     }
 
     /**
      * {@inheritDoc}
      */
     // Default implementation
     @Override
     public boolean canRemove() {
         return true;
     }
 
     // Moved from JMeter class
     @Override
     public boolean isEnabled() {
         return getProperty(TestElement.ENABLED) instanceof NullProperty || getPropertyAsBoolean(TestElement.ENABLED);
     }
 
     @Override
     public void setEnabled(boolean enabled) {
         setProperty(new BooleanProperty(TestElement.ENABLED, enabled));
     }
 
     /** 
      * {@inheritDoc}}
      */
     @Override
     public List<String> getSearchableTokens() {
         List<String> result = new ArrayList<>(25);
         PropertyIterator iterator = propertyIterator();
         while(iterator.hasNext()) {
             JMeterProperty jMeterProperty = iterator.next();    
             result.add(jMeterProperty.getName());
             result.add(jMeterProperty.getStringValue());
         }
         return result;
     }
     
     /**
      * Add to result the values of propertyNames
      * @param result List of values of propertyNames
      * @param propertyNames Set of names of properties to extract
      */
     protected final void addPropertiesValues(List<String> result, Set<String> propertyNames) {
         PropertyIterator iterator = propertyIterator();
         while(iterator.hasNext()) {
             JMeterProperty jMeterProperty = iterator.next();
             if(propertyNames.contains(jMeterProperty.getName())) {
                 result.add(jMeterProperty.getStringValue());
             }
         }
     } 
 }
diff --git a/src/core/org/apache/jmeter/testelement/TestPlan.java b/src/core/org/apache/jmeter/testelement/TestPlan.java
index acb8922ee..6af88cbff 100644
--- a/src/core/org/apache/jmeter/testelement/TestPlan.java
+++ b/src/core/org/apache/jmeter/testelement/TestPlan.java
@@ -1,283 +1,283 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.testelement;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.net.MalformedURLException;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.NewDriver;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class TestPlan extends AbstractTestElement implements Serializable, TestStateListener {
-    private static final long serialVersionUID = 233L;
+    private static final long serialVersionUID = 234L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TestPlan.class);
 
     //+ JMX field names - do not change values
     private static final String FUNCTIONAL_MODE = "TestPlan.functional_mode"; //$NON-NLS-1$
 
     private static final String USER_DEFINED_VARIABLES = "TestPlan.user_defined_variables"; //$NON-NLS-1$
 
     private static final String SERIALIZE_THREADGROUPS = "TestPlan.serialize_threadgroups"; //$NON-NLS-1$
 
     private static final String CLASSPATHS = "TestPlan.user_define_classpath"; //$NON-NLS-1$
 
     private static final String TEARDOWN_ON_SHUTDOWN = "TestPlan.tearDown_on_shutdown"; //$NON-NLS-1$
 
     //- JMX field names
 
     private static final String CLASSPATH_SEPARATOR = ","; //$NON-NLS-1$
 
     private static final String BASEDIR = "basedir";
 
     private transient List<AbstractThreadGroup> threadGroups = new LinkedList<>();
 
     // There's only 1 test plan, so can cache the mode here
     private static volatile boolean functionalMode = false;
 
     public TestPlan() {
         super();
     }
 
     public TestPlan(String name) {
         setName(name);
     }
 
     // create transient item
     protected Object readResolve(){
         threadGroups = new LinkedList<>();
         return this;
     }
 
     public void prepareForPreCompile()
     {
         getVariables().setRunningVersion(true);
     }
 
     /**
      * Fetches the functional mode property
      *
      * @return functional mode
      */
     public boolean isFunctionalMode() {
         return getPropertyAsBoolean(FUNCTIONAL_MODE);
     }
 
     public void setUserDefinedVariables(Arguments vars) {
         setProperty(new TestElementProperty(USER_DEFINED_VARIABLES, vars));
     }
 
     public JMeterProperty getUserDefinedVariablesAsProperty() {
         return getProperty(USER_DEFINED_VARIABLES);
     }
 
     public String getBasedir() {
         return getPropertyAsString(BASEDIR);
     }
 
     // Does not appear to be used yet
     public void setBasedir(String b) {
         setProperty(BASEDIR, b);
     }
 
     public Arguments getArguments() {
         return getVariables();
     }
 
     public Map<String, String> getUserDefinedVariables() {
         Arguments args = getVariables();
         return args.getArgumentsAsMap();
     }
 
     private Arguments getVariables() {
         Arguments args = (Arguments) getProperty(USER_DEFINED_VARIABLES).getObjectValue();
         if (args == null) {
             args = new Arguments();
             setUserDefinedVariables(args);
         }
         return args;
     }
 
     public void setFunctionalMode(boolean funcMode) {
         setProperty(new BooleanProperty(FUNCTIONAL_MODE, funcMode));
         setGlobalFunctionalMode(funcMode);
     }
 
     /**
      * Set JMeter in functional mode
      * @param funcMode boolean functional mode
      */
     private static void setGlobalFunctionalMode(boolean funcMode) {
         functionalMode = funcMode;
     }
 
     /**
      * Gets the static copy of the functional mode
      *
      * @return mode
      */
     public static boolean getFunctionalMode() {
         return functionalMode;
     }
 
     public void setSerialized(boolean serializeTGs) {
         setProperty(new BooleanProperty(SERIALIZE_THREADGROUPS, serializeTGs));
     }
 
     public void setTearDownOnShutdown(boolean tearDown) {
         setProperty(TEARDOWN_ON_SHUTDOWN, tearDown, false);
     }
 
     public boolean isTearDownOnShutdown() {
         return getPropertyAsBoolean(TEARDOWN_ON_SHUTDOWN, false);
     }
 
     /**
      * Set the classpath for the test plan. If the classpath is made up from
      * more then one path, the parts must be separated with
      * {@link TestPlan#CLASSPATH_SEPARATOR}.
      *
      * @param text
      *            the classpath to be set
      */
     public void setTestPlanClasspath(String text) {
         setProperty(CLASSPATHS,text);
     }
 
     public void setTestPlanClasspathArray(String[] text) {
         StringBuilder cat = new StringBuilder();
         for (int idx=0; idx < text.length; idx++) {
             if (idx > 0) {
                 cat.append(CLASSPATH_SEPARATOR);
             }
             cat.append(text[idx]);
         }
         this.setTestPlanClasspath(cat.toString());
     }
 
     public String[] getTestPlanClasspathArray() {
         return JOrphanUtils.split(this.getTestPlanClasspath(),CLASSPATH_SEPARATOR);
     }
 
     /**
      * Returns the classpath
      * @return classpath
      */
     public String getTestPlanClasspath() {
         return getPropertyAsString(CLASSPATHS);
     }
 
     /**
      * Fetch the serialize threadgroups property
      *
      * @return serialized setting
      */
     public boolean isSerialized() {
         return getPropertyAsBoolean(SERIALIZE_THREADGROUPS);
     }
 
     public void addParameter(String name, String value) {
         getVariables().addArgument(name, value);
     }
 
     @Override
     public void addTestElement(TestElement tg) {
         super.addTestElement(tg);
         if (tg instanceof AbstractThreadGroup && !isRunningVersion()) {
             addThreadGroup((AbstractThreadGroup) tg);
         }
     }
 
     /**
      * Adds a feature to the AbstractThreadGroup attribute of the TestPlan object.
      *
      * @param group
      *            the feature to be added to the AbstractThreadGroup attribute
      */
     public void addThreadGroup(AbstractThreadGroup group) {
         threadGroups.add(group);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         try {
             FileServer.getFileServer().closeFiles();
         } catch (IOException e) {
             log.error("Problem closing files at end of test", e);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded(String host) {
         testEnded();
 
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted() {
         if (getBasedir() != null && getBasedir().length() > 0) {
             try {
                 FileServer.getFileServer().setBasedir(FileServer.getFileServer().getBaseDir() + getBasedir());
             } catch (IllegalStateException e) {
-                log.error("Failed to set file server base dir with " + getBasedir(), e);
+                log.error("Failed to set file server base dir with {}", getBasedir(), e);
             }
         }
         // we set the classpath
         String[] paths = this.getTestPlanClasspathArray();
         for (String path : paths) {
             try {
                 NewDriver.addURL(path);
-                log.info("added " + path + " to classpath");
+                log.info("added {} to classpath", path);
             } catch (MalformedURLException e) {
                 // TODO Should we continue the test or fail ?
-                log.error("Error adding " + path + " to classpath", e);
+                log.error("Error adding {} to classpath", path, e);
             }
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
 }
diff --git a/src/core/org/apache/jmeter/testelement/property/AbstractProperty.java b/src/core/org/apache/jmeter/testelement/property/AbstractProperty.java
index a21c2a6a2..dc92d4377 100644
--- a/src/core/org/apache/jmeter/testelement/property/AbstractProperty.java
+++ b/src/core/org/apache/jmeter/testelement/property/AbstractProperty.java
@@ -1,418 +1,418 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.testelement.property;
 
 import java.util.Collection;
 import java.util.Map;
 
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public abstract class AbstractProperty implements JMeterProperty {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     //TODO consider using private logs for each derived class
-    protected static final Logger log = LoggingManager.getLoggerForClass();
+    protected static final Logger log = LoggerFactory.getLogger(AbstractProperty.class);
 
     private String name;
 
     private transient boolean runningVersion = false;
 
     public AbstractProperty(String name) {
         if (name == null) {
             throw new IllegalArgumentException("Name cannot be null");
         }
         this.name = name;
     }
 
     public AbstractProperty() {
         this("");
     }
 
     protected boolean isEqualType(JMeterProperty prop) {
         if (this.getClass().equals(prop.getClass())) {
             return true;
         } else {
             return false;
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean isRunningVersion() {
         return runningVersion;
     }
 
     /** {@inheritDoc} */
     @Override
     public String getName() {
         return name;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setName(String name) {
         if (name == null) {
             throw new IllegalArgumentException("Name cannot be null");
         }
         this.name = name;
     }
 
     /** {@inheritDoc} */
     @Override
     public void setRunningVersion(boolean runningVersion) {
         this.runningVersion = runningVersion;
     }
 
     protected PropertyIterator getIterator(Collection<JMeterProperty> values) {
         return new PropertyIteratorImpl(values);
     }
 
     /** {@inheritDoc} */
     @Override
     public AbstractProperty clone() {
         try {
             AbstractProperty prop = (AbstractProperty) super.clone();
             prop.name = name;
             prop.runningVersion = runningVersion;
             return prop;
         } catch (CloneNotSupportedException e) {
             throw new AssertionError(e); // clone should never return null
         }
     }
 
     /**
      * Returns 0 if string is invalid or null.
      *
      * @see JMeterProperty#getIntValue()
      */
     @Override
     public int getIntValue() {
         String val = getStringValue();
         if (val == null || val.length()==0) {
             return 0;
         }
         try {
             return Integer.parseInt(val);
         } catch (NumberFormatException e) {
             return 0;
         }
     }
 
     /**
      * Returns 0 if string is invalid or null.
      *
      * @see JMeterProperty#getLongValue()
      */
     @Override
     public long getLongValue() {
         String val = getStringValue();
         if (val == null || val.length()==0) {
             return 0;
         }
         try {
             return Long.parseLong(val);
         } catch (NumberFormatException e) {
             return 0;
         }
     }
 
     /**
      * Returns 0 if string is invalid or null.
      *
      * @see JMeterProperty#getDoubleValue()
      */
     @Override
     public double getDoubleValue() {
         String val = getStringValue();
         if (val == null || val.length()==0) {
             return 0;
         }
         try {
             return Double.parseDouble(val);
         } catch (NumberFormatException e) {
             log.error("Tried to parse a non-number string to an integer", e);
             return 0;
         }
     }
 
     /**
      * Returns 0 if string is invalid or null.
      *
      * @see JMeterProperty#getFloatValue()
      */
     @Override
     public float getFloatValue() {
         String val = getStringValue();
         if (val == null || val.length()==0) {
             return 0;
         }
         try {
             return Float.parseFloat(val);
         } catch (NumberFormatException e) {
             log.error("Tried to parse a non-number string to an integer", e);
             return 0;
         }
     }
 
     /**
      * Returns false if string is invalid or null.
      *
      * @see JMeterProperty#getBooleanValue()
      */
     @Override
     public boolean getBooleanValue() {
         String val = getStringValue();
         if (val == null || val.length()==0) {
             return false;
         }
         return Boolean.parseBoolean(val);
     }
 
     /**
      * Determines if the two objects are equal by comparing names and values
      *
      * @return true if names are equal and values are equal (or both null)
      */
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof JMeterProperty)) {
             return false;
         }
         if (this == o) {
             return true;
         }
         JMeterProperty jpo = (JMeterProperty) o;
         if (!name.equals(jpo.getName())) {
             return false;
         }
         Object o1 = getObjectValue();
         Object o2 = jpo.getObjectValue();
         return o1 == null ? o2 == null : o1.equals(o2);
     }
 
     /** {@inheritDoc} */
     @Override
     public int hashCode() {
         int result = 17;
         result = result * 37 + name.hashCode();// name cannot be null
         Object o = getObjectValue();
         result = result * 37 + (o == null ? 0 : o.hashCode());
         return result;
     }
 
     /**
      * Compares two JMeterProperty object values. N.B. Does not compare names
      *
      * @param arg0
      *            JMeterProperty to compare against
      * @return 0 if equal values or both values null; -1 otherwise
      * @see Comparable#compareTo(Object)
      */
     @Override
     public int compareTo(JMeterProperty arg0) {
         // We don't expect the string values to ever be null. But (as in
         // bug 19499) sometimes they are. So have null compare less than
         // any other value. Log a warning so we can try to find the root
         // cause of the null value.
         String val = getStringValue();
         String val2 = arg0.getStringValue();
         if (val == null) {
-            log.warn("Warning: Unexpected null value for property: " + name);
+            log.warn("Warning: Unexpected null value for property: {}", name);
 
             if (val2 == null) {
                 // Two null values -- return equal
                 return 0;
             } else {
                 return -1;
             }
         }
         return val.compareTo(val2);
     }
 
     /**
      * Get the property type for this property. Used to convert raw values into
      * JMeterProperties.
      *
      * @return property type of this property
      */
     protected Class<? extends JMeterProperty> getPropertyType() {
         return getClass();
     }
 
     protected JMeterProperty getBlankProperty() {
         try {
             JMeterProperty prop = getPropertyType().newInstance();
             if (prop instanceof NullProperty) {
                 return new StringProperty();
             }
             return prop;
         } catch (Exception e) {
             return new StringProperty();
         }
     }
 
     protected static JMeterProperty getBlankProperty(Object item) {
         if (item == null) {
             return new NullProperty();
         }
         if (item instanceof String) {
             return new StringProperty("", item.toString());
         } else if (item instanceof Boolean) {
             return new BooleanProperty("", ((Boolean) item).booleanValue());
         } else if (item instanceof Float) {
             return new FloatProperty("", ((Float) item).floatValue());
         } else if (item instanceof Double) {
             return new DoubleProperty("", ((Double) item).doubleValue());
         } else if (item instanceof Integer) {
             return new IntegerProperty("", ((Integer) item).intValue());
         } else if (item instanceof Long) {
             return new LongProperty("", ((Long) item).longValue());
         } else {
             return new StringProperty("", item.toString());
         }
     }
 
     /**
      * Convert a collection of objects into JMeterProperty objects.
      * 
      * @param coll Collection of any type of object
      * @return Collection of JMeterProperty objects
      */
     protected Collection<JMeterProperty> normalizeList(Collection<?> coll) {
         if (coll.isEmpty()) {
             @SuppressWarnings("unchecked") // empty collection, local var is here to allow SuppressWarnings
             Collection<JMeterProperty> okColl = (Collection<JMeterProperty>) coll;
             return okColl;
         }
         try {
             @SuppressWarnings("unchecked") // empty collection
             Collection<JMeterProperty> newColl = coll.getClass().newInstance();
             for (Object item : coll) {
                 newColl.add(convertObject(item));
             }
             return newColl;
         } catch (Exception e) {// should not happen
-            log.error("Cannot create copy of "+coll.getClass().getName(),e);
+            log.error("Cannot create copy of {}", coll.getClass(), e);
             return null;
         }
     }
 
     /**
      * Given a Map, it converts the Map into a collection of JMeterProperty
      * objects, appropriate for a MapProperty object.
      *
      * @param coll
      *            Map to convert
      * @return converted Map
      */
     protected Map<String, JMeterProperty> normalizeMap(Map<?,?> coll) {
         if (coll.isEmpty()) {
             @SuppressWarnings("unchecked")// empty collection ok to cast, local var is here to allow SuppressWarnings
             Map<String, JMeterProperty> emptyColl = (Map<String, JMeterProperty>) coll;
             return emptyColl;
         }
         try {
             @SuppressWarnings("unchecked") // empty collection
             Map<String, JMeterProperty> newColl = coll.getClass().newInstance();
             for (Map.Entry<?,?> entry : ((Map<?,?>)coll).entrySet()) {
                 Object key = entry.getKey();
                 Object prop = entry.getValue();
                 String item=null;
                 if (key instanceof String) {
                     item = (String) key;
                 } else {
                     if (key != null) {
-                        log.error("Expected key type String, found: "+key.getClass().getName());
+                        log.error("Expected key type String, found: {}", key.getClass());
                         item = key.toString();
                     }
                 }
                 newColl.put(item, convertObject(prop));
             }
             return newColl;
         } catch (Exception e) {// should not happen
-            log.error("Cannot create copy of "+coll.getClass().getName(),e);
+            log.error("Cannot create copy of {}", coll.getClass(), e);
             return null;
         }
     }
 
     public static JMeterProperty createProperty(Object item) {
         JMeterProperty prop = makeProperty(item);
         if (prop == null) {
             prop = getBlankProperty(item);
         }
         return prop;
     }
 
     /**
      * Create a JMeterProperty from an object.
      * The object can be one of:
      * <ul>
      * <li>JMeterProperty - returned unchanged</li>
      * <li>TestElement =&gt; TestElementProperty with the same name</li>
      * <li>Map|Collection =&gt; Map|CollectionProperty with the name = item.hashCode</li>
      * </ul>
      * @param item object to be turned into a propery
      * @return the JMeterProperty
      */
     protected static JMeterProperty makeProperty(Object item) {
         if (item instanceof JMeterProperty) {
             return (JMeterProperty) item;
         }
         if (item instanceof TestElement) {
             return new TestElementProperty(((TestElement) item).getName(),
                     (TestElement) item);
         }
         if (item instanceof Collection<?>) {
             return new CollectionProperty(Integer.toString(item.hashCode()), (Collection<?>) item);
         }
         if (item instanceof Map<?, ?>) {
             return new MapProperty(Integer.toString(item.hashCode()), (Map<?, ?>) item);
         }
         return null;
     }
 
     protected JMeterProperty convertObject(Object item) {
         JMeterProperty prop = makeProperty(item);
         if (prop == null) {
             prop = getBlankProperty();
             prop.setName(Integer.toString(item.hashCode()));
             prop.setObjectValue(item);
         }
         return prop;
     }
 
     /**
      * Provides the string representation of the property.
      *
      * @return the string value
      */
     @Override
     public String toString() {
         // N.B. Other classes rely on this returning just the string.
         return getStringValue();
     }
 
     /** {@inheritDoc} */
     @Override
     public void mergeIn(JMeterProperty prop) {
         // NOOP
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java b/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java
index 0d550e4eb..76784dd51 100644
--- a/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java
+++ b/src/core/org/apache/jmeter/threads/FindTestElementsUpToRootTraverser.java
@@ -1,103 +1,103 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * HashTreeTraverser implementation that stores in a Stack all 
  * the Test Elements on the path to a particular node.
  */
 public class FindTestElementsUpToRootTraverser implements HashTreeTraverser {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FindTestElementsUpToRootTraverser.class);
 
     private final LinkedList<TestElement> stack = new LinkedList<>();
 
     /**
      * Node to find in TestTree
      */
     private final Object nodeToFind;
     /**
      * Once we find the node in the Tree we stop recording nodes
      */
     private boolean stopRecording = false;
 
     /**
      * @param nodeToFind Node to find
      */
     public FindTestElementsUpToRootTraverser(Object nodeToFind) {
         this.nodeToFind = nodeToFind;
     }
 
     /** {@inheritDoc} */
     @Override
     public void addNode(Object node, HashTree subTree) {
         if(stopRecording) {
             return;
         }
         if(node == nodeToFind) {
             this.stopRecording = true;
         }
         stack.addLast((TestElement) node);        
     }
 
     /** {@inheritDoc} */
     @Override
     public void subtractNode() {
         if(stopRecording) {
             return;
         }
-        if(log.isDebugEnabled()) {
-            log.debug("Subtracting node, stack size = " + stack.size());
+        if (log.isDebugEnabled()) {
+            log.debug("Subtracting node, stack size = {}", stack.size());
         }
         stack.removeLast();        
     }
 
     /** {@inheritDoc} */
     @Override
     public void processPath() {
         //NOOP
     }
 
     /**
      * Returns all controllers that where in Tree down to nodeToFind in reverse order (from leaf to root)
      * @return List of {@link Controller}
      */
     public List<Controller> getControllersToRoot() {
         List<Controller> result = new ArrayList<>(stack.size());
         LinkedList<TestElement> stackLocalCopy = new LinkedList<>(stack);
         while(!stackLocalCopy.isEmpty()) {
             TestElement te = stackLocalCopy.getLast();
             if(te instanceof Controller) {
                 result.add((Controller)te);
             }
             stackLocalCopy.removeLast();
         }
         return result;
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index 880b1d52b..c030abbc2 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,999 +1,1016 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.ReentrantLock;
 
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.TransactionSampler;
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleMonitor;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.AbstractScopedAssertion;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
 import org.apache.jorphan.collections.ListedHashTree;
 import org.apache.jorphan.collections.SearchByClass;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JMeterStopTestException;
 import org.apache.jorphan.util.JMeterStopTestNowException;
 import org.apache.jorphan.util.JMeterStopThreadException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The JMeter interface to the sampling process, allowing JMeter to see the
  * timing, add listeners for sampling events and to stop the sampling process.
  *
  */
 public class JMeterThread implements Runnable, Interruptible {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMeterThread.class);
 
     public static final String PACKAGE_OBJECT = "JMeterThread.pack"; // $NON-NLS-1$
 
     public static final String LAST_SAMPLE_OK = "JMeterThread.last_sample_ok"; // $NON-NLS-1$
 
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
     private static final float TIMER_FACTOR = JMeterUtils.getPropDefault("timer.factor", 1.0f);
 
     /**
      * 1 as float
      */
     private static final float ONE_AS_FLOAT = 1.0f;
 
     private static final boolean APPLY_TIMER_FACTOR = Float.compare(TIMER_FACTOR,ONE_AS_FLOAT) != 0;
 
     private final Controller threadGroupLoopController;
 
     private final HashTree testTree;
 
     private final TestCompiler compiler;
 
     private final JMeterThreadMonitor monitor;
 
     private final JMeterVariables threadVars;
 
     // Note: this is only used to implement TestIterationListener#testIterationStart
     // Since this is a frequent event, it makes sense to create the list once rather than scanning each time
     // The memory used will be released when the thread finishes
     private final Collection<TestIterationListener> testIterationStartListeners;
 
     private final Collection<SampleMonitor> sampleMonitors;
 
     private final ListenerNotifier notifier;
 
     /*
      * The following variables are set by StandardJMeterEngine.
      * This is done before start() is called, so the values will be published to the thread safely
      * TODO - consider passing them to the constructor, so that they can be made final
      * (to avoid adding lots of parameters, perhaps have a parameter wrapper object.
      */
     private String threadName;
 
     private int initialDelay = 0;
 
     private int threadNum = 0;
 
     private long startTime = 0;
 
     private long endTime = 0;
 
     private boolean scheduler = false;
     // based on this scheduler is enabled or disabled
 
     // Gives access to parent thread threadGroup
     private AbstractThreadGroup threadGroup;
 
     private StandardJMeterEngine engine = null; // For access to stop methods.
 
     /*
      * The following variables may be set/read from multiple threads.
      */
     private volatile boolean running; // may be set from a different thread
 
     private volatile boolean onErrorStopTest;
 
     private volatile boolean onErrorStopTestNow;
 
     private volatile boolean onErrorStopThread;
 
     private volatile boolean onErrorStartNextLoop;
 
     private volatile Sampler currentSampler;
 
     private final ReentrantLock interruptLock = new ReentrantLock(); // ensure that interrupt cannot overlap with shutdown
 
     public JMeterThread(HashTree test, JMeterThreadMonitor monitor, ListenerNotifier note) {
         this.monitor = monitor;
         threadVars = new JMeterVariables();
         testTree = test;
         compiler = new TestCompiler(testTree);
         threadGroupLoopController = (Controller) testTree.getArray()[0];
         SearchByClass<TestIterationListener> threadListenerSearcher = new SearchByClass<>(TestIterationListener.class); // TL - IS
         test.traverse(threadListenerSearcher);
         testIterationStartListeners = threadListenerSearcher.getSearchResults();
         SearchByClass<SampleMonitor> sampleMonitorSearcher = new SearchByClass<>(SampleMonitor.class);
         test.traverse(sampleMonitorSearcher);
         sampleMonitors = sampleMonitorSearcher.getSearchResults();
         notifier = note;
         running = true;
     }
 
     public void setInitialContext(JMeterContext context) {
         threadVars.putAll(context.getVariables());
     }
 
     /**
      * Enable the scheduler for this JMeterThread.
      *
      * @param sche
      *            flag whether the scheduler should be enabled
      */
     public void setScheduled(boolean sche) {
         this.scheduler = sche;
     }
 
     /**
      * Set the StartTime for this Thread.
      *
      * @param stime the StartTime value.
      */
     public void setStartTime(long stime) {
         startTime = stime;
     }
 
     /**
      * Get the start time value.
      *
      * @return the start time value.
      */
     public long getStartTime() {
         return startTime;
     }
 
     /**
      * Set the EndTime for this Thread.
      *
      * @param etime
      *            the EndTime value.
      */
     public void setEndTime(long etime) {
         endTime = etime;
     }
 
     /**
      * Get the end time value.
      *
      * @return the end time value.
      */
     public long getEndTime() {
         return endTime;
     }
 
     /**
      * Check if the scheduled time is completed.
      */
     private void stopSchedulerIfNeeded() {
         long now = System.currentTimeMillis();
         long delay = now - endTime;
         if (delay >= 0) {
             running = false;
-            log.info("Stopping because end time detected by thread: " + threadName);
+            log.info("Stopping because end time detected by thread: {}", threadName);
         }
     }
 
     /**
      * Wait until the scheduled start time if necessary
      *
      */
     private void startScheduler() {
         long delay = startTime - System.currentTimeMillis();
         delayBy(delay, "startScheduler");
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
     @Override
     public void run() {
         // threadContext is not thread-safe, so keep within thread
         JMeterContext threadContext = JMeterContextService.getContext();
         LoopIterationListener iterationListener = null;
 
         try {
             iterationListener = initRun(threadContext);
             while (running) {
                 Sampler sam = threadGroupLoopController.next();
                 while (running && sam != null) {
                     processSampler(sam, null, threadContext);
                     threadContext.cleanAfterSample();
                     
                     // restart of the next loop 
                     // - was requested through threadContext
                     // - or the last sample failed AND the onErrorStartNextLoop option is enabled
                     if(threadContext.isRestartNextLoop()
                             || (onErrorStartNextLoop
                                     && !TRUE.equals(threadContext.getVariables().get(LAST_SAMPLE_OK)))) 
                     {
-                        if(log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
-                                log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
+                        if (log.isDebugEnabled() && onErrorStartNextLoop && !threadContext.isRestartNextLoop()) {
+                            log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
                         }
-                        
+
                         triggerEndOfLoopOnParentControllers(sam, threadContext);
                         sam = null;
                         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
                         threadContext.setRestartNextLoop(false);
                     }
                     else {
                         sam = threadGroupLoopController.next();
                     }
                 }
                 
                 if (threadGroupLoopController.isDone()) {
                     running = false;
-                    log.info("Thread is done: " + threadName);
+                    log.info("Thread is done: {}", threadName);
                 }
             }
         }
         // Might be found by contoller.next()
         catch (JMeterStopTestException e) { // NOSONAR
-            log.info("Stopping Test: " + e.toString()); 
+            if (log.isInfoEnabled()) {
+                log.info("Stopping Test: {}", e.toString());
+            }
             stopTest();
         }
         catch (JMeterStopTestNowException e) { // NOSONAR
-            log.info("Stopping Test Now: " + e.toString());
+            if (log.isInfoEnabled()) {
+                log.info("Stopping Test Now: {}", e.toString());
+            }
             stopTestNow();
         } catch (JMeterStopThreadException e) { // NOSONAR
-            log.info("Stop Thread seen for thread " + getThreadName()+", reason:"+ e.toString());
+            if (log.isInfoEnabled()) {
+                log.info("Stop Thread seen for thread {}, reason: {}", getThreadName(), e.toString());
+            }
         } catch (Exception | JMeterError e) {
             log.error("Test failed!", e);
         } catch (ThreadDeath e) {
             throw e; // Must not ignore this one
         } finally {
             currentSampler = null; // prevent any further interrupts
             try {
                 interruptLock.lock();  // make sure current interrupt is finished, prevent another starting yet
                 threadContext.clear();
-                log.info("Thread finished: " + threadName);
+                log.info("Thread finished: {}", threadName);
                 threadFinished(iterationListener);
                 monitor.threadFinished(this); // Tell the monitor we are done
                 JMeterContextService.removeContext(); // Remove the ThreadLocal entry
             }
             finally {
                 interruptLock.unlock(); // Allow any pending interrupt to complete (OK because currentSampler == null)
             }
         }
     }
 
     /**
      * Trigger end of loop on parent controllers up to Thread Group
      * @param sam Sampler Base sampler
      * @param threadContext 
      */
     private void triggerEndOfLoopOnParentControllers(Sampler sam, JMeterContext threadContext) {
         TransactionSampler transactionSampler = null;
         if(sam instanceof TransactionSampler) {
             transactionSampler = (TransactionSampler) sam;
         }
 
         Sampler realSampler = findRealSampler(sam);
         if(realSampler == null) {
             throw new IllegalStateException("Got null subSampler calling findRealSampler for:"+sam.getName()+", sam:"+sam);
         }
         // Find parent controllers of current sampler
         FindTestElementsUpToRootTraverser pathToRootTraverser = new FindTestElementsUpToRootTraverser(realSampler);
         testTree.traverse(pathToRootTraverser);
         
         // Trigger end of loop condition on all parent controllers of current sampler
         List<Controller> controllersToReinit = pathToRootTraverser.getControllersToRoot();
         for (Controller parentController : controllersToReinit) {
             if(parentController instanceof AbstractThreadGroup) {
                 AbstractThreadGroup tg = (AbstractThreadGroup) parentController;
                 tg.startNextLoop();
             } else {
                 parentController.triggerEndOfLoop();
             }
         }
         
         // bug 52968
         // When using Start Next Loop option combined to TransactionController.
         // if an error occurs in a Sample (child of TransactionController) 
         // then we still need to report the Transaction in error (and create the sample result)
         if(transactionSampler != null) {
             SamplePackage transactionPack = compiler.configureTransactionSampler(transactionSampler);
             doEndTransactionSampler(transactionSampler, null, transactionPack, threadContext);
         }
     }
 
     /**
      * Find the Real sampler (Not TransactionSampler) that really generated an error
      * The Sampler provided is not always the "real" one, it can be a TransactionSampler, 
      * if there are some other controllers (SimpleController or other implementations) between this TransactionSampler and the real sampler, 
      * triggerEndOfLoop will not be called for those controllers leaving them in "ugly" state.
      * the following method will try to find the sampler that really generate an error
      * @param sampler
      * @return {@link Sampler}
      */
     private Sampler findRealSampler(Sampler sampler) {
         Sampler realSampler = sampler;
         while(realSampler instanceof TransactionSampler) {
             realSampler = ((TransactionSampler) realSampler).getSubSampler();
         }
         return realSampler;
     }
 
     /**
      * Process the current sampler, handling transaction samplers.
      *
      * @param current sampler
      * @param parent sampler
      * @param threadContext
      * @return SampleResult if a transaction was processed
      */
     private SampleResult processSampler(Sampler current, Sampler parent, JMeterContext threadContext) {
         SampleResult transactionResult = null;
         try {
             // Check if we are running a transaction
             TransactionSampler transactionSampler = null;
             if(current instanceof TransactionSampler) {
                 transactionSampler = (TransactionSampler) current;
             }
             // Find the package for the transaction
             SamplePackage transactionPack = null;
             if(transactionSampler != null) {
                 transactionPack = compiler.configureTransactionSampler(transactionSampler);
 
                 // Check if the transaction is done
                 if(transactionSampler.isTransactionDone()) {
                     transactionResult = doEndTransactionSampler(transactionSampler, 
                             parent, 
                             transactionPack,
                             threadContext);
                     // Transaction is done, we do not have a sampler to sample
                     current = null;
                 }
                 else {
                     Sampler prev = current;
                     // It is the sub sampler of the transaction that will be sampled
                     current = transactionSampler.getSubSampler();
                     if (current instanceof TransactionSampler) {
                         SampleResult res = processSampler(current, prev, threadContext);// recursive call
                         threadContext.setCurrentSampler(prev);
                         current = null;
                         if (res != null) {
                             transactionSampler.addSubSamplerResult(res);
                         }
                     }
                 }
             }
 
             // Check if we have a sampler to sample
             if(current != null) {
                 executeSamplePackage(current, transactionSampler, transactionPack, threadContext);
             }
             
             if (scheduler) {
                 // checks the scheduler to stop the iteration
                 stopSchedulerIfNeeded();
             }
         } catch (JMeterStopTestException e) { // NOSONAR
-            log.info("Stopping Test: " + e.toString());
+            if (log.isInfoEnabled()) {
+                log.info("Stopping Test: {}", e.toString());
+            }
             stopTest();
         } catch (JMeterStopThreadException e) { // NOSONAR
-            log.info("Stopping Thread: " + e.toString());
+            if (log.isInfoEnabled()) {
+                log.info("Stopping Thread: {}", e.toString());
+            }
             stopThread();
         } catch (Exception e) {
             if (current != null) {
-                log.error("Error while processing sampler '"+current.getName()+"' :", e);
+                log.error("Error while processing sampler: '{}'.", current.getName(), e);
             } else {
-                log.error("", e);
+                log.error("Error while processing sampler.", e);
             }
         }
         return transactionResult;
     }
 
     /*
      * Execute the sampler with its pre/post processors, timers, assertions
      * Broadcast the result to the sample listeners
      */
     private void executeSamplePackage(Sampler current,
             TransactionSampler transactionSampler,
             SamplePackage transactionPack,
             JMeterContext threadContext) {
         
         threadContext.setCurrentSampler(current);
         // Get the sampler ready to sample
         SamplePackage pack = compiler.configureSampler(current);
         runPreProcessors(pack.getPreProcessors());
 
         // Hack: save the package for any transaction controllers
         threadVars.putObject(PACKAGE_OBJECT, pack);
 
         delay(pack.getTimers());
         Sampler sampler = pack.getSampler();
         sampler.setThreadContext(threadContext);
         // TODO should this set the thread names for all the subsamples?
         // might be more efficient than fetching the name elsewhere
         sampler.setThreadName(threadName);
         TestBeanHelper.prepare(sampler);
 
         // Perform the actual sample
         currentSampler = sampler;
         if(!sampleMonitors.isEmpty()) {
             for(SampleMonitor sampleMonitor : sampleMonitors) {
                 sampleMonitor.sampleStarting(sampler);
             }
         }
         SampleResult result = null;
         try {
             result = sampler.sample(null);
         } finally {
             if(!sampleMonitors.isEmpty()) {
                 for(SampleMonitor sampleMonitor : sampleMonitors) {
                     sampleMonitor.sampleEnded(sampler);
                 }
             }
         }
         currentSampler = null;
 
         // If we got any results, then perform processing on the result
         if (result != null) {
             int nbActiveThreadsInThreadGroup = threadGroup.getNumberOfThreads();
             int nbTotalActiveThreads = JMeterContextService.getNumberOfThreads();
             result.setGroupThreads(nbActiveThreadsInThreadGroup);
             result.setAllThreads(nbTotalActiveThreads);
             result.setThreadName(threadName);
             SampleResult[] subResults = result.getSubResults();
             if(subResults != null) {
                 for (SampleResult subResult : subResults) {
                     subResult.setGroupThreads(nbActiveThreadsInThreadGroup);
                     subResult.setAllThreads(nbTotalActiveThreads);
                     subResult.setThreadName(threadName);
                 }
             }
             threadContext.setPreviousResult(result);
             runPostProcessors(pack.getPostProcessors());
             checkAssertions(pack.getAssertions(), result, threadContext);
             // Do not send subsamples to listeners which receive the transaction sample
             List<SampleListener> sampleListeners = getSampleListeners(pack, transactionPack, transactionSampler);
             notifyListeners(sampleListeners, result);
             compiler.done(pack);
             // Add the result as subsample of transaction if we are in a transaction
             if(transactionSampler != null) {
                 transactionSampler.addSubSamplerResult(result);
             }
 
             // Check if thread or test should be stopped
             if (result.isStopThread() || (!result.isSuccessful() && onErrorStopThread)) {
                 stopThread();
             }
             if (result.isStopTest() || (!result.isSuccessful() && onErrorStopTest)) {
                 stopTest();
             }
             if (result.isStopTestNow() || (!result.isSuccessful() && onErrorStopTestNow)) {
                 stopTestNow();
             }
             if(result.isStartNextThreadLoop()) {
                 threadContext.setRestartNextLoop(true);
             }
         } else {
             compiler.done(pack); // Finish up
         }
     }
 
     private SampleResult doEndTransactionSampler(
                             TransactionSampler transactionSampler, 
                             Sampler parent,
                             SamplePackage transactionPack,
                             JMeterContext threadContext) {
         SampleResult transactionResult;
         // Get the transaction sample result
         transactionResult = transactionSampler.getTransactionResult();
         transactionResult.setThreadName(threadName);
         transactionResult.setGroupThreads(threadGroup.getNumberOfThreads());
         transactionResult.setAllThreads(JMeterContextService.getNumberOfThreads());
 
         // Check assertions for the transaction sample
         checkAssertions(transactionPack.getAssertions(), transactionResult, threadContext);
         // Notify listeners with the transaction sample result
         if (!(parent instanceof TransactionSampler)) {
             notifyListeners(transactionPack.getSampleListeners(), transactionResult);
         }
         compiler.done(transactionPack);
         return transactionResult;
     }
 
     /**
      * Get the SampleListeners for the sampler. Listeners who receive transaction sample
      * will not be in this list.
      *
      * @param samplePack
      * @param transactionPack
      * @param transactionSampler
      * @return the listeners who should receive the sample result
      */
     private List<SampleListener> getSampleListeners(SamplePackage samplePack, SamplePackage transactionPack, TransactionSampler transactionSampler) {
         List<SampleListener> sampleListeners = samplePack.getSampleListeners();
         // Do not send subsamples to listeners which receive the transaction sample
         if(transactionSampler != null) {
             List<SampleListener> onlySubSamplerListeners = new ArrayList<>();
             List<SampleListener> transListeners = transactionPack.getSampleListeners();
             for(SampleListener listener : sampleListeners) {
                 // Check if this instance is present in transaction listener list
                 boolean found = false;
                 for(SampleListener trans : transListeners) {
                     // Check for the same instance
                     if(trans == listener) {
                         found = true;
                         break;
                     }
                 }
                 if(!found) {
                     onlySubSamplerListeners.add(listener);
                 }
             }
             sampleListeners = onlySubSamplerListeners;
         }
         return sampleListeners;
     }
 
     /**
      * @param threadContext
      * @return the iteration listener 
      */
     private IterationListener initRun(JMeterContext threadContext) {
         threadContext.setVariables(threadVars);
         threadContext.setThreadNum(getThreadNum());
         threadContext.getVariables().put(LAST_SAMPLE_OK, TRUE);
         threadContext.setThread(this);
         threadContext.setThreadGroup(threadGroup);
         threadContext.setEngine(engine);
         testTree.traverse(compiler);
         if (scheduler) {
             // set the scheduler to start
             startScheduler();
         }
 
         rampUpDelay(); // TODO - how to handle thread stopped here
-        log.info("Thread started: " + Thread.currentThread().getName());
+        if (log.isInfoEnabled()) {
+            log.info("Thread started: {}", Thread.currentThread().getName());
+        }
         /*
          * Setting SamplingStarted before the controllers are initialised allows
          * them to access the running values of functions and variables (however
          * it does not seem to help with the listeners)
          */
         threadContext.setSamplingStarted(true);
         
         threadGroupLoopController.initialize();
         IterationListener iterationListener = new IterationListener();
         threadGroupLoopController.addIterationListener(iterationListener);
 
         threadStarted();
         return iterationListener;
     }
 
     private void threadStarted() {
         JMeterContextService.incrNumberOfThreads();
         threadGroup.incrNumberOfThreads();
         GuiPackage gp =GuiPackage.getInstance();
         if (gp != null) {// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         ThreadListenerTraverser startup = new ThreadListenerTraverser(true);
         testTree.traverse(startup); // call ThreadListener.threadStarted()
     }
 
     private void threadFinished(LoopIterationListener iterationListener) {
         ThreadListenerTraverser shut = new ThreadListenerTraverser(false);
         testTree.traverse(shut); // call ThreadListener.threadFinished()
         JMeterContextService.decrNumberOfThreads();
         threadGroup.decrNumberOfThreads();
         GuiPackage gp = GuiPackage.getInstance();
         if (gp != null){// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         if (iterationListener != null) { // probably not possible, but check anyway
             threadGroupLoopController.removeIterationListener(iterationListener);
         }
     }
 
     // N.B. This is only called at the start and end of a thread, so there is not
     // necessary to cache the search results, thus saving memory
     private static class ThreadListenerTraverser implements HashTreeTraverser {
         private final boolean isStart;
 
         private ThreadListenerTraverser(boolean start) {
             isStart = start;
         }
 
         @Override
         public void addNode(Object node, HashTree subTree) {
             if (node instanceof ThreadListener) {
                 ThreadListener tl = (ThreadListener) node;
                 if (isStart) {
                     tl.threadStarted();
                 } else {
                     tl.threadFinished();
                 }
             }
         }
 
         @Override
         public void subtractNode() {
             // NOOP
         }
 
         @Override
         public void processPath() {
             // NOOP
         }
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     /**
      * Set running flag to false which will interrupt JMeterThread on next flag test.
      * This is a clean shutdown.
      */
     public void stop() { // Called by StandardJMeterEngine, TestAction and AccessLogSampler
         running = false;
-        log.info("Stopping: " + threadName);
+        log.info("Stopping: {}", threadName);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt(){
         try {
             interruptLock.lock();
             Sampler samp = currentSampler; // fetch once; must be done under lock
             if (samp instanceof Interruptible){ // (also protects against null)
-                log.warn("Interrupting: " + threadName + " sampler: " +samp.getName());
+                if (log.isWarnEnabled()) {
+                    log.warn("Interrupting: {} sampler: {}", threadName, samp.getName());
+                }
                 try {
                     boolean found = ((Interruptible)samp).interrupt();
                     if (!found) {
                         log.warn("No operation pending");
                     }
                     return found;
                 } catch (Exception e) { // NOSONAR
-                    log.warn("Caught Exception interrupting sampler: "+e.toString());
+                    if (log.isWarnEnabled()) {
+                        log.warn("Caught Exception interrupting sampler: {}", e.toString());
+                    }
+                }
+            } else if (samp != null) {
+                if (log.isWarnEnabled()) {
+                    log.warn("Sampler is not Interruptible: {}", samp.getName());
                 }
-            } else if (samp != null){
-                log.warn("Sampler is not Interruptible: "+samp.getName());
             }
         } finally {
             interruptLock.unlock();            
         }
         return false;
     }
 
     private void stopTest() {
         running = false;
-        log.info("Stop Test detected by thread: " + threadName);
+        log.info("Stop Test detected by thread: {}", threadName);
         if (engine != null) {
             engine.askThreadsToStop();
         }
     }
 
     private void stopTestNow() {
         running = false;
-        log.info("Stop Test Now detected by thread: " + threadName);
+        log.info("Stop Test Now detected by thread: {}", threadName);
         if (engine != null) {
             engine.stopTest();
         }
     }
 
     private void stopThread() {
         running = false;
-        log.info("Stop Thread detected by thread: " + threadName);
+        log.info("Stop Thread detected by thread: {}", threadName);
     }
 
     private void checkAssertions(List<Assertion> assertions, SampleResult parent, JMeterContext threadContext) {
         for (Assertion assertion : assertions) {
             TestBeanHelper.prepare((TestElement) assertion);
             if (assertion instanceof AbstractScopedAssertion){
                 AbstractScopedAssertion scopedAssertion = (AbstractScopedAssertion) assertion;
                 String scope = scopedAssertion.fetchScope();
                 if (scopedAssertion.isScopeParent(scope) || scopedAssertion.isScopeAll(scope) || scopedAssertion.isScopeVariable(scope)){
                     processAssertion(parent, assertion);
                 }
                 if (scopedAssertion.isScopeChildren(scope) || scopedAssertion.isScopeAll(scope)){
                     SampleResult[] children = parent.getSubResults();
                     boolean childError = false;
                     for (SampleResult childSampleResult : children) {
                         processAssertion(childSampleResult, assertion);
                         if (!childSampleResult.isSuccessful()) {
                             childError = true;
                         }
                     }
                     // If parent is OK, but child failed, add a message and flag the parent as failed
                     if (childError && parent.isSuccessful()) {
                         AssertionResult assertionResult = new AssertionResult(((AbstractTestElement)assertion).getName());
                         assertionResult.setResultForFailure("One or more sub-samples failed");
                         parent.addAssertionResult(assertionResult);
                         parent.setSuccessful(false);
                     }
                 }
             } else {
                 processAssertion(parent, assertion);
             }
         }
         threadContext.getVariables().put(LAST_SAMPLE_OK, Boolean.toString(parent.isSuccessful()));
     }
 
     private void processAssertion(SampleResult result, Assertion assertion) {
         AssertionResult assertionResult;
         try {
             assertionResult = assertion.getResult(result);
         } catch (ThreadDeath e) {
             throw e;
         } catch (JMeterError e) {
-            log.error("Error processing Assertion ",e);
+            log.error("Error processing Assertion.", e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         } catch (Exception e) {
-            log.error("Exception processing Assertion ",e);
+            log.error("Exception processing Assertion.", e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         }
         result.setSuccessful(result.isSuccessful() && !(assertionResult.isError() || assertionResult.isFailure()));
         result.addAssertionResult(assertionResult);
     }
 
     private void runPostProcessors(List<PostProcessor> extractors) {
         for (PostProcessor ex : extractors) {
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     private void runPreProcessors(List<PreProcessor> preProcessors) {
         for (PreProcessor ex : preProcessors) {
             if (log.isDebugEnabled()) {
-                log.debug("Running preprocessor: " + ((AbstractTestElement) ex).getName());
+                log.debug("Running preprocessor: {}", ((AbstractTestElement) ex).getName());
             }
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     private void delay(List<Timer> timers) {
         long totalDelay = 0;
         for (Timer timer : timers) {
             TestBeanHelper.prepare((TestElement) timer);
             long delay = timer.delay();
             if(APPLY_TIMER_FACTOR && 
                     timer.isModifiable()) {
-                if(log.isDebugEnabled()) {
-                    log.debug("Applying TIMER_FACTOR:"
-                            +TIMER_FACTOR + " on timer:"
-                            +((TestElement)timer).getName()
-                            + " for thread:"+getThreadName());
+                if (log.isDebugEnabled()) {
+                    log.debug("Applying TIMER_FACTOR:{} on timer:{} for thread:{}", TIMER_FACTOR,
+                            ((TestElement) timer).getName(), getThreadName());
                 }
                 delay = Math.round(delay * TIMER_FACTOR);
             }
             totalDelay += delay;
         }
         if (totalDelay > 0) {
             try {
                 if(scheduler) {
                     // We reduce pause to ensure end of test is not delayed by a sleep ending after test scheduled end
                     // See Bug 60049
                     long now = System.currentTimeMillis();
                     if(now + totalDelay > endTime) {
                         totalDelay = endTime - now;
                     }
                 }
                 TimeUnit.MILLISECONDS.sleep(totalDelay);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
                 Thread.currentThread().interrupt();
             }
         }
     }
 
     void notifyTestListeners() {
         threadVars.incIteration();
         for (TestIterationListener listener : testIterationStartListeners) {
             if (listener instanceof TestElement) {
                 listener.testIterationStart(new LoopIterationEvent(threadGroupLoopController, threadVars.getIteration()));
                 ((TestElement) listener).recoverRunningVersion();
             } else {
                 listener.testIterationStart(new LoopIterationEvent(threadGroupLoopController, threadVars.getIteration()));
             }
         }
     }
 
     private void notifyListeners(List<SampleListener> listeners, SampleResult result) {
         SampleEvent event = new SampleEvent(result, threadGroup.getName(), threadVars);
         notifier.notifyListeners(event, listeners);
 
     }
 
     /**
      * Set rampup delay for JMeterThread Thread
      * @param delay Rampup delay for JMeterThread
      */
     public void setInitialDelay(int delay) {
         initialDelay = delay;
     }
 
     /**
      * Initial delay if ramp-up period is active for this threadGroup.
      */
     private void rampUpDelay() {
         delayBy(initialDelay, "RampUp");
     }
 
     /**
      * Wait for delay with RAMPUP_GRANULARITY
      * @param delay delay in ms
      * @param type Delay type
      */
     protected final void delayBy(long delay, String type) {
         if (delay > 0) {
             long start = System.currentTimeMillis();
             long end = start + delay;
             long now;
             long pause = RAMPUP_GRANULARITY;
             while(running && (now = System.currentTimeMillis()) < end) {
                 long togo = end - now;
                 if (togo < pause) {
                     pause = togo;
                 }
                 try {
                     TimeUnit.MILLISECONDS.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
-                    if (running) { // NOSONAR Don't bother reporting stop test interruptions 
-                        log.warn(type+" delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+delay);
+                    if (running) { 
+                        log.warn("{} delay for {} was interrupted. Waited {} milli-seconds out of {}", type, threadName,
+                                (now - start), delay);
                     }
                     Thread.currentThread().interrupt();
                     break;
                 }
             }
         }
     }
 
     /**
      * Returns the threadNum.
      *
      * @return the threadNum
      */
     public int getThreadNum() {
         return threadNum;
     }
 
     /**
      * Sets the threadNum.
      *
      * @param threadNum
      *            the threadNum to set
      */
     public void setThreadNum(int threadNum) {
         this.threadNum = threadNum;
     }
 
     private class IterationListener implements LoopIterationListener {
         /**
          * {@inheritDoc}
          */
         @Override
         public void iterationStart(LoopIterationEvent iterEvent) {
             notifyTestListeners();
         }
     }
 
     /**
      * Save the engine instance for access to the stop methods
      *
      * @param engine the engine which is used
      */
     public void setEngine(StandardJMeterEngine engine) {
         this.engine = engine;
     }
 
     /**
      * Should Test stop on sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopTest(boolean b) {
         onErrorStopTest = b;
     }
 
     /**
      * Should Test stop abruptly on sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopTestNow(boolean b) {
         onErrorStopTestNow = b;
     }
 
     /**
      * Should Thread stop on Sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStopThread(boolean b) {
         onErrorStopThread = b;
     }
 
     /**
      * Should Thread start next loop on Sampler error?
      *
      * @param b -
      *            true or false
      */
     public void setOnErrorStartNextLoop(boolean b) {
         onErrorStartNextLoop = b;
     }
 
     public void setThreadGroup(AbstractThreadGroup group) {
         this.threadGroup = group;
     }
 
     /**
      * @return {@link ListedHashTree}
      */
     public ListedHashTree getTestTree() {
         return (ListedHashTree) testTree;
     }
 
     /**
      * @return {@link ListenerNotifier}
      */
     public ListenerNotifier getNotifier() {
         return notifier;
     }
 
 }
diff --git a/src/core/org/apache/jmeter/threads/ListenerNotifier.java b/src/core/org/apache/jmeter/threads/ListenerNotifier.java
index cfa8568d9..577c12cd9 100644
--- a/src/core/org/apache/jmeter/threads/ListenerNotifier.java
+++ b/src/core/org/apache/jmeter/threads/ListenerNotifier.java
@@ -1,76 +1,76 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 /////////////////////////////////////////
 ////////
 //////// This code is mostly unused at present
 //////// it seems that only notifyListeners()
 //////// is used.
 ////////
 //////// However, it does look useful.
 //////// And it may one day be used...
 ////////
 /////////////////////////////////////////
 
 package org.apache.jmeter.threads;
 
 import java.util.List;
 
 //import org.apache.commons.collections.Buffer;
 //import org.apache.commons.collections.BufferUtils;
 //import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.TestElement;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Processes sample events. <br/>
  * The current implementation processes events in the calling thread
  * using {@link #notifyListeners(SampleEvent, List)} <br/>
  * Thread safe class 
  */
 public class ListenerNotifier {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ListenerNotifier.class);
 
 
     /**
      * Notify a list of listeners that a sample has occurred.
      *
      * @param res
      *            the sample event that has occurred. Must be non-null.
      * @param listeners
      *            a list of the listeners which should be notified. This list
      *            must not be null and must contain only SampleListener
      *            elements.
      */
     public void notifyListeners(SampleEvent res, List<SampleListener> listeners) {
         for (SampleListener sampleListener : listeners) {
             try {
                 TestBeanHelper.prepare((TestElement) sampleListener);
                 sampleListener.sampleOccurred(res);
             } catch (RuntimeException e) {
-                log.error("Detected problem in Listener: ", e);
+                log.error("Detected problem in Listener.", e);
                 log.info("Continuing to process further listeners");
             }
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/threads/RemoteThreadsListenerImpl.java b/src/core/org/apache/jmeter/threads/RemoteThreadsListenerImpl.java
index a64c2c065..e7743839a 100644
--- a/src/core/org/apache/jmeter/threads/RemoteThreadsListenerImpl.java
+++ b/src/core/org/apache/jmeter/threads/RemoteThreadsListenerImpl.java
@@ -1,115 +1,112 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.io.IOException;
 import java.lang.reflect.Modifier;
 import java.rmi.RemoteException;
 import java.rmi.server.UnicastRemoteObject;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.ClassFinder;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * RMI Implementation, client side code (ie executed on Controller)
  * @since 2.10
  */
 public class RemoteThreadsListenerImpl extends UnicastRemoteObject implements
         RemoteThreadsListener, ThreadListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteThreadsListenerImpl.class);
     private final List<RemoteThreadsLifeCycleListener> listeners = new ArrayList<>();
 
     /**
      * 
      */
-    private static final long serialVersionUID = 4790505101521183660L;
+    private static final long serialVersionUID = 1L;
     /**
      * 
      */
     private static final int DEFAULT_LOCAL_PORT = 
             JMeterUtils.getPropDefault("client.rmi.localport", 0); // $NON-NLS-1$
 
     /**
      * @throws RemoteException if failed to export object
      */
     public RemoteThreadsListenerImpl() throws RemoteException {
         super(DEFAULT_LOCAL_PORT);
         try {
             List<String> listClasses = ClassFinder.findClassesThatExtend(
                     JMeterUtils.getSearchPaths(), 
                     new Class[] {RemoteThreadsLifeCycleListener.class }); 
             for (String strClassName : listClasses) {
                 try {
-                    if(log.isDebugEnabled()) {
-                        log.debug("Loading class: "+ strClassName);
-                    }
+                    log.debug("Loading class: {}", strClassName);
                     Class<?> commandClass = Class.forName(strClassName);
                     if (!Modifier.isAbstract(commandClass.getModifiers())) {
-                        if(log.isDebugEnabled()) {
-                            log.debug("Instantiating: "+ commandClass.getName());
-                        }
+                        log.debug("Instantiating: {}", commandClass);
                         RemoteThreadsLifeCycleListener listener = (RemoteThreadsLifeCycleListener) commandClass.newInstance();
                         listeners.add(listener);
                     }
                 } catch (Exception e) {
-                    log.error("Exception registering "+RemoteThreadsLifeCycleListener.class.getName() + " with implementation:"+strClassName, e);
+                    log.error("Exception registering {} with implementation: {}", RemoteThreadsLifeCycleListener.class,
+                            strClassName, e);
                 }
             }
         } catch (IOException e) {
-            log.error("Exception finding implementations of "+RemoteThreadsLifeCycleListener.class, e);
+            log.error("Exception finding implementations of {}", RemoteThreadsLifeCycleListener.class, e);
         }
     }
 
     /**
      * 
      * @see RemoteThreadsListener#threadStarted()
      */
     @Override
     public void threadStarted() {
         JMeterContextService.incrNumberOfThreads();
         GuiPackage gp =GuiPackage.getInstance();
         if (gp != null) {// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         for (RemoteThreadsLifeCycleListener listener : listeners) {
             listener.threadNumberIncreased(JMeterContextService.getNumberOfThreads());
         }
     }
 
     /* (non-Javadoc)
      * @see org.apache.jmeter.samplers.RemoteThreadsListener#threadFinished()
      */
     @Override
     public void threadFinished() {
         JMeterContextService.decrNumberOfThreads();
         GuiPackage gp =GuiPackage.getInstance();
         if (gp != null) {// check there is a GUI
             gp.getMainFrame().updateCounts();
         }
         for (RemoteThreadsLifeCycleListener listener : listeners) {
             listener.threadNumberDecreased(JMeterContextService.getNumberOfThreads());
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/RemoteThreadsListenerWrapper.java b/src/core/org/apache/jmeter/threads/RemoteThreadsListenerWrapper.java
index f73b3da85..dc7b29a61 100644
--- a/src/core/org/apache/jmeter/threads/RemoteThreadsListenerWrapper.java
+++ b/src/core/org/apache/jmeter/threads/RemoteThreadsListenerWrapper.java
@@ -1,66 +1,66 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.io.Serializable;
 import java.rmi.RemoteException;
 
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.ThreadListener;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * server side wrapper, used to notify RMI client 
  * @since 2.10
  */
 public class RemoteThreadsListenerWrapper extends AbstractTestElement implements ThreadListener, Serializable,
         NoThreadClone {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(RemoteThreadsListenerWrapper.class);
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     private RemoteThreadsListener listener;
 
     public RemoteThreadsListenerWrapper(RemoteThreadsListener l) {
         listener = l;
     }
 
     public RemoteThreadsListenerWrapper() {
     }
 
     @Override
     public void threadStarted() {
         try {
             listener.threadStarted();
         } catch (RemoteException err) {
-            log.error("", err); // $NON-NLS-1$
+            log.error("Exception invoking listener on threadStarted.", err); // $NON-NLS-1$
         }
     }
 
     @Override
     public void threadFinished() {
         try {
             listener.threadFinished();
         } catch (RemoteException err) {
-            log.error("", err); // $NON-NLS-1$
+            log.error("Exception invoking listener on threadFinished.", err); // $NON-NLS-1$
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/TestCompiler.java b/src/core/org/apache/jmeter/threads/TestCompiler.java
index 7aafde388..c6a8656b5 100644
--- a/src/core/org/apache/jmeter/threads/TestCompiler.java
+++ b/src/core/org/apache/jmeter/threads/TestCompiler.java
@@ -1,315 +1,319 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.TransactionController;
 import org.apache.jmeter.control.TransactionSampler;
 import org.apache.jmeter.engine.event.LoopIterationListener;
 import org.apache.jmeter.engine.util.ConfigMergabilityIndicator;
 import org.apache.jmeter.engine.util.NoConfigMerge;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.collections.HashTreeTraverser;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * HashTreeTraverser implementation that traverses the Test Tree to build:
  * <ul>
  *  <li>A map with key Sampler and as value the associated SamplePackage</li>
  *  <li>A map with key TransactionController and as value the associated SamplePackage</li>
  * </ul>
  */
 public class TestCompiler implements HashTreeTraverser {
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TestCompiler.class);
 
     /**
      * This set keeps track of which ObjectPairs have been seen.
      * It seems to be used to prevent adding a child to a parent if the child has already been added.
      * If the ObjectPair (child, parent) is present, then the child has been added.
      * Otherwise, the child is added to the parent and the pair is added to the Set.
      */
     private static final Set<ObjectPair> PAIRING = new HashSet<>();
 
     private final LinkedList<TestElement> stack = new LinkedList<>();
 
     private final Map<Sampler, SamplePackage> samplerConfigMap = new HashMap<>();
 
     private final Map<TransactionController, SamplePackage> transactionControllerConfigMap =
             new HashMap<>();
 
     private final HashTree testTree;
 
     public TestCompiler(HashTree testTree) {
         this.testTree = testTree;
     }
 
     /**
      * Clears the pairing Set Called by StandardJmeterEngine at the start of a
      * test run.
      */
     public static void initialize() {
         // synch is probably not needed as only called before run starts
         synchronized (PAIRING) {
             PAIRING.clear();
         }
     }
 
     /**
      * Configures sampler from SamplePackage extracted from Test plan and returns it
      * @param sampler {@link Sampler}
      * @return {@link SamplePackage}
      */
     public SamplePackage configureSampler(Sampler sampler) {
         SamplePackage pack = samplerConfigMap.get(sampler);
         pack.setSampler(sampler);
         configureWithConfigElements(sampler, pack.getConfigs());
         return pack;
     }
 
     /**
      * Configures Transaction Sampler from SamplePackage extracted from Test plan and returns it
      * @param transactionSampler {@link TransactionSampler}
      * @return {@link SamplePackage}
      */
     public SamplePackage configureTransactionSampler(TransactionSampler transactionSampler) {
         TransactionController controller = transactionSampler.getTransactionController();
         SamplePackage pack = transactionControllerConfigMap.get(controller);
         pack.setSampler(transactionSampler);
         return pack;
     }
 
     /**
      * Reset pack to its initial state
      * @param pack the {@link SamplePackage} to reset
      */
     public void done(SamplePackage pack) {
         pack.recoverRunningVersion();
     }
 
     /** {@inheritDoc} */
     @Override
     public void addNode(Object node, HashTree subTree) {
         stack.addLast((TestElement) node);
     }
 
     /** {@inheritDoc} */
     @Override
     public void subtractNode() {
-        LOG.debug("Subtracting node, stack size = " + stack.size());
+        if (log.isDebugEnabled()) {
+            log.debug("Subtracting node, stack size = {}", stack.size());
+        }
         TestElement child = stack.getLast();
         trackIterationListeners(stack);
         if (child instanceof Sampler) {
             saveSamplerConfigs((Sampler) child);
         }
         else if(child instanceof TransactionController) {
             saveTransactionControllerConfigs((TransactionController) child);
         }
         stack.removeLast();
         if (!stack.isEmpty()) {
             TestElement parent = stack.getLast();
             boolean duplicate = false;
             // Bug 53750: this condition used to be in ObjectPair#addTestElements()
             if (parent instanceof Controller && (child instanceof Sampler || child instanceof Controller)) {
                 if (parent instanceof TestCompilerHelper) {
                     TestCompilerHelper te = (TestCompilerHelper) parent;
                     duplicate = !te.addTestElementOnce(child);
                 } else { // this is only possible for 3rd party controllers by default
                     ObjectPair pair = new ObjectPair(child, parent);
                     synchronized (PAIRING) {// Called from multiple threads
                         if (!PAIRING.contains(pair)) {
                             parent.addTestElement(child);
                             PAIRING.add(pair);
                         } else {
                             duplicate = true;
                         }
                     }
                 }
             }
             if (duplicate) {
-                LOG.warn("Unexpected duplicate for " + parent.getClass().getName() + " and " + child.getClass().getName());
+                if (log.isWarnEnabled()) {
+                    log.warn("Unexpected duplicate for {} and {}", parent.getClass(), child.getClass());
+                }
             }
         }
     }
 
     private void trackIterationListeners(LinkedList<TestElement> pStack) {
         TestElement child = pStack.getLast();
         if (child instanceof LoopIterationListener) {
             ListIterator<TestElement> iter = pStack.listIterator(pStack.size());
             while (iter.hasPrevious()) {
                 TestElement item = iter.previous();
                 if (item == child) {
                     continue;
                 }
                 if (item instanceof Controller) {
                     TestBeanHelper.prepare(child);
                     ((Controller) item).addIterationListener((LoopIterationListener) child);
                     break;
                 }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public void processPath() {
     }
 
     private void saveSamplerConfigs(Sampler sam) {
         List<ConfigTestElement> configs = new LinkedList<>();
         List<Controller> controllers = new LinkedList<>();
         List<SampleListener> listeners = new LinkedList<>();
         List<Timer> timers = new LinkedList<>();
         List<Assertion> assertions = new LinkedList<>();
         LinkedList<PostProcessor> posts = new LinkedList<>();
         LinkedList<PreProcessor> pres = new LinkedList<>();
         for (int i = stack.size(); i > 0; i--) {
             addDirectParentControllers(controllers, stack.get(i - 1));
             List<PreProcessor>  tempPre = new LinkedList<>();
             List<PostProcessor> tempPost = new LinkedList<>();
             for (Object item : testTree.list(stack.subList(0, i))) {
                 if (item instanceof ConfigTestElement) {
                     configs.add((ConfigTestElement) item);
                 }
                 if (item instanceof SampleListener) {
                     listeners.add((SampleListener) item);
                 }
                 if (item instanceof Timer) {
                     timers.add((Timer) item);
                 }
                 if (item instanceof Assertion) {
                     assertions.add((Assertion) item);
                 }
                 if (item instanceof PostProcessor) {
                     tempPost.add((PostProcessor) item);
                 }
                 if (item instanceof PreProcessor) {
                     tempPre.add((PreProcessor) item);
                 }
             }
             pres.addAll(0, tempPre);
             posts.addAll(0, tempPost);
         }
 
         SamplePackage pack = new SamplePackage(configs, listeners, timers, assertions,
                 posts, pres, controllers);
         pack.setSampler(sam);
         pack.setRunningVersion(true);
         samplerConfigMap.put(sam, pack);
     }
 
     private void saveTransactionControllerConfigs(TransactionController tc) {
         List<ConfigTestElement> configs = new LinkedList<>();
         List<Controller> controllers = new LinkedList<>();
         List<SampleListener> listeners = new LinkedList<>();
         List<Timer> timers = new LinkedList<>();
         List<Assertion> assertions = new LinkedList<>();
         LinkedList<PostProcessor> posts = new LinkedList<>();
         LinkedList<PreProcessor> pres = new LinkedList<>();
         for (int i = stack.size(); i > 0; i--) {
             addDirectParentControllers(controllers, stack.get(i - 1));
             for (Object item : testTree.list(stack.subList(0, i))) {
                 if (item instanceof SampleListener) {
                     listeners.add((SampleListener) item);
                 }
                 if (item instanceof Assertion) {
                     assertions.add((Assertion) item);
                 }
             }
         }
 
         SamplePackage pack = new SamplePackage(configs, listeners, timers, assertions,
                 posts, pres, controllers);
         pack.setSampler(new TransactionSampler(tc, tc.getName()));
         pack.setRunningVersion(true);
         transactionControllerConfigMap.put(tc, pack);
     }
 
     /**
      * @param controllers
      * @param maybeController
      */
     private void addDirectParentControllers(List<Controller> controllers, TestElement maybeController) {
         if (maybeController instanceof Controller) {
-            LOG.debug("adding controller: " + maybeController + " to sampler config");
+            log.debug("adding controller: {} to sampler config", maybeController);
             controllers.add((Controller) maybeController);
         }
     }
 
     private static class ObjectPair
     {
         private final TestElement child;
         private final TestElement parent;
 
         public ObjectPair(TestElement child, TestElement parent) {
             this.child = child;
             this.parent = parent;
         }
 
         /** {@inheritDoc} */
         @Override
         public int hashCode() {
             return child.hashCode() + parent.hashCode();
         }
 
         /** {@inheritDoc} */
         @Override
         public boolean equals(Object o) {
             if (o instanceof ObjectPair) {
                 return child == ((ObjectPair) o).child && parent == ((ObjectPair) o).parent;
             }
             return false;
         }
     }
 
     private void configureWithConfigElements(Sampler sam, List<ConfigTestElement> configs) {
         sam.clearTestElementChildren();
         for (ConfigTestElement config  : configs) {
             if (!(config instanceof NoConfigMerge)) 
             {
                 if(sam instanceof ConfigMergabilityIndicator) {
                     if(((ConfigMergabilityIndicator)sam).applies(config)) {
                         sam.addTestElement(config);
                     }
                 } else {
                     // Backward compatibility
                     sam.addTestElement(config);
                 }
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/threads/ThreadGroup.java b/src/core/org/apache/jmeter/threads/ThreadGroup.java
index 6e0982877..cd17608ca 100644
--- a/src/core/org/apache/jmeter/threads/ThreadGroup.java
+++ b/src/core/org/apache/jmeter/threads/ThreadGroup.java
@@ -1,672 +1,673 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.threads;
 
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.engine.StandardJMeterEngine;
 import org.apache.jmeter.engine.TreeCloner;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.ListedHashTree;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * ThreadGroup holds the settings for a JMeter thread group.
  * 
  * This class is intended to be ThreadSafe.
  */
 public class ThreadGroup extends AbstractThreadGroup {
-    private static final long serialVersionUID = 281L;
+    private static final long serialVersionUID = 282L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ThreadGroup.class);
     
     private static final String DATE_FIELD_FORMAT = "yyyy/MM/dd HH:mm:ss"; //$NON-NLS-1$
 
     private static final long WAIT_TO_DIE = JMeterUtils.getPropDefault("jmeterengine.threadstop.wait", 5 * 1000); // 5 seconds
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
     //+ JMX entries - do not change the string values
 
     /** Ramp-up time */
     public static final String RAMP_TIME = "ThreadGroup.ramp_time";
 
     /** Whether thread startup is delayed until required */
     public static final String DELAYED_START = "ThreadGroup.delayedStart";
 
     /** Whether scheduler is being used */
     public static final String SCHEDULER = "ThreadGroup.scheduler";
 
     /** Scheduler absolute start time */
     public static final String START_TIME = "ThreadGroup.start_time";
 
     /** Scheduler absolute end time */
     public static final String END_TIME = "ThreadGroup.end_time";
 
     /** Scheduler duration, overrides end time */
     public static final String DURATION = "ThreadGroup.duration";
 
     /** Scheduler start delay, overrides start time */
     public static final String DELAY = "ThreadGroup.delay";
 
     //- JMX entries
 
     private transient Thread threadStarter;
 
     // List of active threads
     private final Map<JMeterThread, Thread> allThreads = new ConcurrentHashMap<>();
     
     private final transient Object addThreadLock = new Object();
 
     /**
      * Is test (still) running?
      */
     private volatile boolean running = false;
 
     /**
      * Thread Group number
      */
     private int groupNumber;
 
     /**
      * Are we using delayed startup?
      */
     private boolean delayedStartup;
 
     /**
      * Thread safe class
      */
     private ListenerNotifier notifier;
 
     /**
      * This property will be cloned
      */
     private ListedHashTree threadGroupTree;
 
     /**
      * No-arg constructor.
      */
     public ThreadGroup() {
         super();
     }
 
     /**
      * Set whether scheduler is being used
      *
      * @param scheduler true is scheduler is to be used
      */
     public void setScheduler(boolean scheduler) {
         setProperty(new BooleanProperty(SCHEDULER, scheduler));
     }
 
     /**
      * Get whether scheduler is being used
      *
      * @return true if scheduler is being used
      */
     public boolean getScheduler() {
         return getPropertyAsBoolean(SCHEDULER);
     }
 
     /**
      * Set the absolute StartTime value.
      *
      * @param stime -
      *            the StartTime value.
      */
     public void setStartTime(long stime) {
         setProperty(new LongProperty(START_TIME, stime));
     }
 
     /**
      * Get the absolute start time value.
      *
      * @return the start time value.
      */
     public long getStartTime() {
         return getPropertyAsLong(START_TIME);
     }
 
     /**
      * Get the desired duration of the thread group test run
      *
      * @return the duration (in secs)
      */
     public long getDuration() {
         return getPropertyAsLong(DURATION);
     }
 
     /**
      * Set the desired duration of the thread group test run
      *
      * @param duration
      *            in seconds
      */
     public void setDuration(long duration) {
         setProperty(new LongProperty(DURATION, duration));
     }
 
     /**
      * Get the startup delay
      *
      * @return the delay (in secs)
      */
     public long getDelay() {
         return getPropertyAsLong(DELAY);
     }
 
     /**
      * Set the startup delay
      *
      * @param delay
      *            in seconds
      */
     public void setDelay(long delay) {
         setProperty(new LongProperty(DELAY, delay));
     }
 
     /**
      * Set the EndTime value.
      *
      * @param etime -
      *            the EndTime value.
      */
     public void setEndTime(long etime) {
         setProperty(new LongProperty(END_TIME, etime));
     }
 
     /**
      * Get the end time value.
      *
      * @return the end time value.
      */
     public long getEndTime() {
         return getPropertyAsLong(END_TIME);
     }
 
     /**
      * Set the ramp-up value.
      *
      * @param rampUp
      *            the ramp-up value.
      */
     public void setRampUp(int rampUp) {
         setProperty(new IntegerProperty(RAMP_TIME, rampUp));
     }
 
     /**
      * Get the ramp-up value.
      *
      * @return the ramp-up value.
      */
     public int getRampUp() {
         return getPropertyAsInt(ThreadGroup.RAMP_TIME);
     }
 
     private boolean isDelayedStartup() {
         return getPropertyAsBoolean(DELAYED_START);
     }
 
     /**
      * This will schedule the time for the JMeterThread.
      *
      * @param thread JMeterThread
      * @param now in milliseconds
      */
     private void scheduleThread(JMeterThread thread, long now) {
 
         // if true the Scheduler is enabled
         if (getScheduler()) {
             // set the start time for the Thread
             if (getDelay() > 0) {// Duration is in seconds
                 thread.setStartTime(getDelay() * 1000 + now);
             } else {
                 long start = getStartTime();
                 if (start < now) {
                     start = now; // Force a sensible start time
                 }                
                 thread.setStartTime(start);
             }
 
             // set the endtime for the Thread
             if (getDuration() > 0) {// Duration is in seconds
                 thread.setEndTime(getDuration() * 1000 + (thread.getStartTime()));
             } else {
                 if( getEndTime() <= now ) {
                     SimpleDateFormat sdf = new SimpleDateFormat(DATE_FIELD_FORMAT);
                     throw new JMeterStopTestException("End Time ("
                             + sdf.format(new Date(getEndTime()))+") of Scheduler for Thread Group "+getName() 
                             + " is in the past, fix value of End Time field");
                 }
                 thread.setEndTime(getEndTime());
             }
 
             // Enables the scheduler
             thread.setScheduled(true);
         }
     }
 
     @Override
     public void start(int groupNum, ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
         this.running = true;
         this.groupNumber = groupNum;
         this.notifier = notifier;
         this.threadGroupTree = threadGroupTree;
         int numThreads = getNumThreads();
         int rampUpPeriodInSeconds = getRampUp();
         float perThreadDelayInMillis = (float) (rampUpPeriodInSeconds * 1000) / (float) getNumThreads();
 
         delayedStartup = isDelayedStartup(); // Fetch once; needs to stay constant
-        log.info("Starting thread group number " + groupNumber
-                + " threads " + numThreads
-                + " ramp-up " + rampUpPeriodInSeconds
-                + " perThread " + perThreadDelayInMillis
-                + " delayedStart=" + delayedStartup);
+        log.info("Starting thread group... number={} threads={} ramp-up={} perThread={} delayedStart={}", groupNumber,
+                numThreads, rampUpPeriodInSeconds, perThreadDelayInMillis, delayedStartup);
         if (delayedStartup) {
             threadStarter = new Thread(new ThreadStarter(notifier, threadGroupTree, engine), getName()+"-ThreadStarter");
             threadStarter.setDaemon(true);
             threadStarter.start();
             // N.B. we don't wait for the thread to complete, as that would prevent parallel TGs
         } else {
             long now = System.currentTimeMillis(); // needs to be same time for all threads in the group
             final JMeterContext context = JMeterContextService.getContext();
             for (int threadNum = 0; running && threadNum < numThreads; threadNum++) {
                 startNewThread(notifier, threadGroupTree, engine, threadNum, context, now, (int)(threadNum * perThreadDelayInMillis));
             }
         }
-        log.info("Started thread group number "+groupNumber);
+        log.info("Started thread group number {}", groupNumber);
     }
 
     /**
      * Start a new {@link JMeterThread} and registers it
      * @param notifier {@link ListenerNotifier}
      * @param threadGroupTree {@link ListedHashTree}
      * @param engine {@link StandardJMeterEngine}
      * @param threadNum Thread number
      * @param context {@link JMeterContext}
      * @param now Nom in milliseconds
      * @param delay int delay in milliseconds
      * @return {@link JMeterThread} newly created
      */
     private JMeterThread startNewThread(ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine,
             int threadNum, final JMeterContext context, long now, int delay) {
         JMeterThread jmThread = makeThread(notifier, threadGroupTree, engine, threadNum, context);
         scheduleThread(jmThread, now); // set start and end time
         jmThread.setInitialDelay(delay);
         Thread newThread = new Thread(jmThread, jmThread.getThreadName());
         registerStartedThread(jmThread, newThread);
         newThread.start();
         return jmThread;
     }
     /**
      * Register Thread when it starts
      * @param jMeterThread {@link JMeterThread}
      * @param newThread Thread
      */
     private void registerStartedThread(JMeterThread jMeterThread, Thread newThread) {
         allThreads.put(jMeterThread, newThread);
     }
 
     /**
      * Create {@link JMeterThread} cloning threadGroupTree
      * @param notifier {@link ListenerNotifier}
      * @param threadGroupTree {@link ListedHashTree}
      * @param engine {@link StandardJMeterEngine}
      * @param threadNumber int thread number
      * @param context {@link JMeterContext}
      * @return {@link JMeterThread}
      */
     private JMeterThread makeThread(
             ListenerNotifier notifier, ListedHashTree threadGroupTree,
             StandardJMeterEngine engine, int threadNumber, 
             JMeterContext context) { // N.B. Context needs to be fetched in the correct thread
         boolean onErrorStopTest = getOnErrorStopTest();
         boolean onErrorStopTestNow = getOnErrorStopTestNow();
         boolean onErrorStopThread = getOnErrorStopThread();
         boolean onErrorStartNextLoop = getOnErrorStartNextLoop();
         String groupName = getName();
         final JMeterThread jmeterThread = new JMeterThread(cloneTree(threadGroupTree), this, notifier);
         jmeterThread.setThreadNum(threadNumber);
         jmeterThread.setThreadGroup(this);
         jmeterThread.setInitialContext(context);
         final String threadName = groupName + " " + groupNumber + "-" + (threadNumber + 1);
         jmeterThread.setThreadName(threadName);
         jmeterThread.setEngine(engine);
         jmeterThread.setOnErrorStopTest(onErrorStopTest);
         jmeterThread.setOnErrorStopTestNow(onErrorStopTestNow);
         jmeterThread.setOnErrorStopThread(onErrorStopThread);
         jmeterThread.setOnErrorStartNextLoop(onErrorStartNextLoop);
         return jmeterThread;
     }
 
     @Override
     public JMeterThread addNewThread(int delay, StandardJMeterEngine engine) {
         long now = System.currentTimeMillis();
         JMeterContext context = JMeterContextService.getContext();
         JMeterThread newJmThread;
         int numThreads;
         synchronized (addThreadLock) {
             numThreads = getNumThreads();
             setNumThreads(numThreads + 1);
         }
         newJmThread = startNewThread(notifier, threadGroupTree, engine, numThreads, context, now, delay);
         JMeterContextService.addTotalThreads( 1 );
-        log.info("Started new thread in group " + groupNumber );
+        log.info("Started new thread in group {}", groupNumber);
         return newJmThread;
     }
 
     /**
      * Stop thread called threadName:
      * <ol>
      *  <li>stop JMeter thread</li>
      *  <li>interrupt JMeter thread</li>
      *  <li>interrupt underlying thread</li>
      * </ol>
      * @param threadName String thread name
      * @param now boolean for stop
      * @return true if thread stopped
      */
     @Override
     public boolean stopThread(String threadName, boolean now) {
         for(Entry<JMeterThread, Thread> entry : allThreads.entrySet()) {
             JMeterThread thrd = entry.getKey();
             if (thrd.getThreadName().equals(threadName)) {
                 stopThread(thrd, entry.getValue(), now);
                 return true;
             }
         }
         return false;
     }
     
     /**
      * Hard Stop JMeterThread thrd and interrupt JVM Thread if interrupt is true
      * @param jmeterThread {@link JMeterThread}
      * @param jvmThread {@link Thread}
      * @param interrupt Interrupt thread or not
      */
     private void stopThread(JMeterThread jmeterThread, Thread jvmThread, boolean interrupt) {
         jmeterThread.stop();
         jmeterThread.interrupt(); // interrupt sampler if possible
         if (interrupt && jvmThread != null) { // Bug 49734
             jvmThread.interrupt(); // also interrupt JVM thread
         }
     }
 
     /**
      * Called by JMeterThread when it finishes
      */
     @Override
     public void threadFinished(JMeterThread thread) {
-        log.debug("Ending thread " + thread.getThreadName());
+        if (log.isDebugEnabled()) {
+            log.debug("Ending thread {}", thread.getThreadName());
+        }
         allThreads.remove(thread);
     }
 
     /**
      * For each thread, invoke:
      * <ul> 
      * <li>{@link JMeterThread#stop()} - set stop flag</li>
      * <li>{@link JMeterThread#interrupt()} - interrupt sampler</li>
      * <li>{@link Thread#interrupt()} - interrupt JVM thread</li>
      * </ul> 
      */
     @Override
     public void tellThreadsToStop() {
         running = false;
         if (delayedStartup) {
             try {
                 threadStarter.interrupt();
             } catch (Exception e) {
                 log.warn("Exception occured interrupting ThreadStarter", e);
             }
         }
         
         for (Entry<JMeterThread, Thread> entry : allThreads.entrySet()) {
             stopThread(entry.getKey(), entry.getValue(), true);
         }
     }
 
 
     /**
      * For each thread, invoke:
      * <ul> 
      * <li>{@link JMeterThread#stop()} - set stop flag</li>
      * </ul> 
      */
     @Override
     public void stop() {
         running = false;
         if (delayedStartup) {
             try {
                 threadStarter.interrupt();
             } catch (Exception e) {
                 log.warn("Exception occured interrupting ThreadStarter", e);
             }            
         }
         for (JMeterThread item : allThreads.keySet()) {
             item.stop();
         }
     }
 
     /**
      * @return number of active threads
      */
     @Override
     public int numberOfActiveThreads() {
         return allThreads.size();
     }
 
     /**
      * @return boolean true if all threads stopped
      */
     @Override
     public boolean verifyThreadsStopped() {
         boolean stoppedAll = true;
         if (delayedStartup) {
             stoppedAll = verifyThreadStopped(threadStarter);
         }
         for (Thread t : allThreads.values()) {
             stoppedAll = stoppedAll && verifyThreadStopped(t);
         }
         return stoppedAll;
     }
 
     /**
      * Verify thread stopped and return true if stopped successfully
      * @param thread Thread
      * @return boolean
      */
     private boolean verifyThreadStopped(Thread thread) {
         boolean stopped = true;
         if (thread != null && thread.isAlive()) {
             try {
                 thread.join(WAIT_TO_DIE);
             } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
             if (thread.isAlive()) {
                 stopped = false;
-                log.warn("Thread won't exit: " + thread.getName());
+                if (log.isWarnEnabled()) {
+                    log.warn("Thread won't exit: {}", thread.getName());
+                }
             }
         }
         return stopped;
     }
 
     /**
      * Wait for all Group Threads to stop
      */
     @Override
     public void waitThreadsStopped() {
         if (delayedStartup) {
             waitThreadStopped(threadStarter);
         }
         for (Thread t : allThreads.values()) {
             waitThreadStopped(t);
         }
     }
 
     /**
      * Wait for thread to stop
      * @param thread Thread
      */
     private void waitThreadStopped(Thread thread) {
         if (thread != null) {
             while (thread.isAlive()) {
                 try {
                     thread.join(WAIT_TO_DIE);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 }
             }
         }
     }
 
     /**
      * @param tree {@link ListedHashTree}
      * @return a clone of tree
      */
     private ListedHashTree cloneTree(ListedHashTree tree) {
         TreeCloner cloner = new TreeCloner(true);
         tree.traverse(cloner);
         return cloner.getClonedTree();
     }
 
     /**
      * Pause ms milliseconds
      * @param ms long milliseconds
      */
     private void pause(long ms){
         try {
             TimeUnit.MILLISECONDS.sleep(ms);
         } catch (InterruptedException e) {
             Thread.currentThread().interrupt();
         }
     }
 
     /**
      * Starts Threads using ramp up
      */
     class ThreadStarter implements Runnable {
 
         private final ListenerNotifier notifier;
         private final ListedHashTree threadGroupTree;
         private final StandardJMeterEngine engine;
         private final JMeterContext context;
 
         public ThreadStarter(ListenerNotifier notifier, ListedHashTree threadGroupTree, StandardJMeterEngine engine) {
             super();
             this.notifier = notifier;
             this.threadGroupTree = threadGroupTree;
             this.engine = engine;
             // Store context from Root Thread to pass it to created threads
             this.context = JMeterContextService.getContext();
             
         }
         
 
         /**
          * Wait for delay with RAMPUP_GRANULARITY
          * @param delay delay in ms
          */
         private void delayBy(long delay) {
             if (delay > 0) {
                 long start = System.currentTimeMillis();
                 long end = start + delay;
                 long now;
                 long pause = RAMPUP_GRANULARITY; // maximum pause to use
                 while(running && (now = System.currentTimeMillis()) < end) {
                     long togo = end - now;
                     if (togo < pause) {
                         pause = togo;
                     }
                     pause(pause); // delay between checks
                 }
             }
         }
         
         @Override
         public void run() {
             try {
                 // Copy in ThreadStarter thread context from calling Thread
                 JMeterContextService.getContext().setVariables(this.context.getVariables());
                 long now = System.currentTimeMillis(); // needs to be constant for all threads
                 long endtime = 0;
                 final boolean usingScheduler = getScheduler();
                 if (usingScheduler) {
                     // set the start time for the Thread
                     if (getDelay() > 0) {// Duration is in seconds
                         delayBy(getDelay() * 1000);
                     } else {
                         long start = getStartTime();
                         if (start >= now) {
                             delayBy(start-now);
                         } 
                         // else start immediately
                     }
                     // set the endtime for the Thread
                     endtime = getDuration();
                     if (endtime > 0) {// Duration is in seconds, starting from when the threads start
                         endtime = endtime *1000 + System.currentTimeMillis();
                     } else {
                         if( getEndTime() <= now ) {
                             SimpleDateFormat sdf = new SimpleDateFormat(DATE_FIELD_FORMAT);
                             throw new JMeterStopTestException("End Time ("
                                     + sdf.format(new Date(getEndTime()))+") of Scheduler for Thread Group "+getName() 
                                     + " is in the past, fix value of End Time field");
                         }
                         endtime = getEndTime();
                     }
                 }
                 final int numThreads = getNumThreads();
                 final int perThreadDelayInMillis = Math.round((float) (getRampUp() * 1000) / (float) numThreads);
                 for (int threadNumber = 0; running && threadNumber < numThreads; threadNumber++) {
                     if (threadNumber > 0) {
                         pause(perThreadDelayInMillis); // ramp-up delay (except first)
                     }
                     if (usingScheduler && System.currentTimeMillis() > endtime) {
                         break; // no point continuing beyond the end time
                     }
                     JMeterThread jmThread = makeThread(notifier, threadGroupTree, engine, threadNumber, context);
                     jmThread.setInitialDelay(0);   // Already waited
                     if (usingScheduler) {
                         jmThread.setScheduled(true);
                         jmThread.setEndTime(endtime);
                     }
                     Thread newThread = new Thread(jmThread, jmThread.getThreadName());
                     newThread.setDaemon(false); // ThreadStarter is daemon, but we don't want sampler threads to be so too
                     registerStartedThread(jmThread, newThread);
                     newThread.start();
                 }
             } catch (Exception ex) {
-                log.error("An error occured scheduling delay start of threads for Thread Group:"+getName(), ex);
+                log.error("An error occured scheduling delay start of threads for Thread Group: {}", getName(), ex);
             }
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/util/BSFTestElement.java b/src/core/org/apache/jmeter/util/BSFTestElement.java
index 226e9b226..46a617cae 100644
--- a/src/core/org/apache/jmeter/util/BSFTestElement.java
+++ b/src/core/org/apache/jmeter/util/BSFTestElement.java
@@ -1,133 +1,137 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.io.File;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.Serializable;
 import java.util.Properties;
 
 import org.apache.bsf.BSFEngine;
 import org.apache.bsf.BSFException;
 import org.apache.bsf.BSFManager;
 import org.apache.commons.io.FileUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public abstract class BSFTestElement extends ScriptingTestElement
     implements Serializable
 {
-    private static final long serialVersionUID = 234L;
+    private static final long serialVersionUID = 235L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BSFTestElement.class);
 
     static {
         log.info("Registering JMeter version of JavaScript engine as work-round for BSF-22");
         BSFManager.registerScriptingEngine("javascript", //$NON-NLS-1$
                 "org.apache.jmeter.util.BSFJavaScriptEngine", //$NON-NLS-1$
                 new String[]{"js"}); //$NON-NLS-1$
     }
 
     public BSFTestElement() {
         super();
     }
 
     protected BSFManager getManager() throws BSFException {
         BSFManager mgr = new BSFManager();
         initManager(mgr);
         return mgr;
     }
 
     protected void initManager(BSFManager mgr) throws BSFException{
         final String label = getName();
         final String fileName = getFilename();
         final String scriptParameters = getParameters();
         // Use actual class name for log
-        final Logger logger = LoggingManager.getLoggerForShortName(getClass().getName());
+        final Logger logger = LoggerFactory.getLogger(getClass());
         mgr.declareBean("log", logger, Logger.class); // $NON-NLS-1$
         mgr.declareBean("Label",label, String.class); // $NON-NLS-1$
         mgr.declareBean("FileName",fileName, String.class); // $NON-NLS-1$
         mgr.declareBean("Parameters", scriptParameters, String.class); // $NON-NLS-1$
         String [] args=JOrphanUtils.split(scriptParameters, " ");//$NON-NLS-1$
         mgr.declareBean("args",args,args.getClass());//$NON-NLS-1$
         // Add variables for access to context and variables
         JMeterContext jmctx = JMeterContextService.getContext();
         JMeterVariables vars = jmctx.getVariables();
         Properties props = JMeterUtils.getJMeterProperties();
 
         mgr.declareBean("ctx", jmctx, jmctx.getClass()); // $NON-NLS-1$
         mgr.declareBean("vars", vars, vars.getClass()); // $NON-NLS-1$
         mgr.declareBean("props", props, props.getClass()); // $NON-NLS-1$
         // For use in debugging:
         mgr.declareBean("OUT", System.out, PrintStream.class); // $NON-NLS-1$
 
         // Most subclasses will need these:
         Sampler sampler = jmctx.getCurrentSampler();
         mgr.declareBean("sampler", sampler, Sampler.class);
         SampleResult prev = jmctx.getPreviousResult();
         mgr.declareBean("prev", prev, SampleResult.class);
     }
 
     protected void processFileOrScript(BSFManager mgr) throws BSFException{
         BSFEngine bsfEngine = mgr.loadScriptingEngine(getScriptLanguage());
         final String scriptFile = getFilename();
         if (scriptFile.length() == 0) {
             bsfEngine.exec("[script]",0,0,getScript());
         } else {// we have a file, read and process it
             try {
                 String script=FileUtils.readFileToString(new File(scriptFile));
                 bsfEngine.exec(scriptFile,0,0,script);
             } catch (IOException e) {
-                log.warn(e.getLocalizedMessage());
-                throw new BSFException(BSFException.REASON_IO_ERROR,"Problem reading script file",e);
+                if (log.isWarnEnabled()) {
+                    log.warn("Exception executing script. {}", e.getLocalizedMessage());
+                }
+                throw new BSFException(BSFException.REASON_IO_ERROR, "Problem reading script file", e);
             }
         }
     }
 
     protected Object evalFileOrScript(BSFManager mgr) throws BSFException{
         BSFEngine bsfEngine = mgr.loadScriptingEngine(getScriptLanguage());
         final String scriptFile = getFilename();
         if (scriptFile.length() == 0) {
             return bsfEngine.eval("[script]",0,0,getScript());
         } else {// we have a file, read and process it
             try {
                 String script=FileUtils.readFileToString(new File(scriptFile));
                 return bsfEngine.eval(scriptFile,0,0,script);
             } catch (IOException e) {
-                log.warn(e.getLocalizedMessage());
+                if (log.isWarnEnabled()) {
+                    log.warn("Exception evaluating script. {}", e.getLocalizedMessage());
+                }
                 throw new BSFException(BSFException.REASON_IO_ERROR,"Problem reading script file",e);
             }
         }
     }
 
     public String getScriptLanguage() {
         return scriptLanguage;
     }
 
     public void setScriptLanguage(String s) {
         scriptLanguage = s;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/BeanShellServer.java b/src/core/org/apache/jmeter/util/BeanShellServer.java
index c5f421da4..2ae4a562d 100644
--- a/src/core/org/apache/jmeter/util/BeanShellServer.java
+++ b/src/core/org/apache/jmeter/util/BeanShellServer.java
@@ -1,111 +1,111 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements a BeanShell server to allow access to JMeter variables and
  * methods.
  *
  * To enable, define the JMeter property: beanshell.server.port (see
  * JMeter.java) beanshell.server.file (optional, startup file)
  *
  */
 public class BeanShellServer implements Runnable {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanShellServer.class);
 
     private final int serverport;
 
     private final String serverfile;
 
     /**
      * Constructor which sets the port for this server and the path to an
      * optional init file
      * 
      * @param port
      *            the port for the server to use
      * @param file
      *            the path to an init file, or an empty string, if no init file
      *            should be used
      */
     public BeanShellServer(int port, String file) {
         super();
         serverfile = file;// can be the empty string
         serverport = port;
     }
 
     // For use by the server script
     static String getprop(String s) {
         return JMeterUtils.getPropDefault(s, s);
     }
 
     // For use by the server script
     static void setprop(String s, String v) {
         JMeterUtils.getJMeterProperties().setProperty(s, v);
     }
 
     @Override
     public void run() {
 
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
 
         try {
             Class<?> interpreter = loader.loadClass("bsh.Interpreter");//$NON-NLS-1$
             Object instance = interpreter.newInstance();
             Class<String> string = String.class;
             Class<Object> object = Object.class;
 
             Method eval = interpreter.getMethod("eval", new Class[] { string });//$NON-NLS-1$
             Method setObj = interpreter.getMethod("set", new Class[] { string, object });//$NON-NLS-1$
             Method setInt = interpreter.getMethod("set", new Class[] { string, int.class });//$NON-NLS-1$
             Method source = interpreter.getMethod("source", new Class[] { string });//$NON-NLS-1$
 
             setObj.invoke(instance, new Object[] { "t", this });//$NON-NLS-1$
             setInt.invoke(instance, new Object[] { "portnum", Integer.valueOf(serverport) });//$NON-NLS-1$
 
             if (serverfile.length() > 0) {
                 try {
                     source.invoke(instance, new Object[] { serverfile });
-                } catch (InvocationTargetException e1) {
-                    log.warn("Could not source " + serverfile);
-                    Throwable t= e1.getCause();
-                    if (t != null) {
-                        log.warn(t.toString());
-                        if(t instanceof Error) {
-                            throw (Error)t;
-                        }
+                } catch (InvocationTargetException ite) {
+                    Throwable cause = ite.getCause();
+                    if (log.isWarnEnabled()) {
+                        log.warn("Could not source, {}. {}", serverfile,
+                                (cause != null) ? cause.toString() : ite.toString());
+                    }
+                    if (cause instanceof Error) {
+                        throw (Error) cause;
                     }
                 }
             }
             eval.invoke(instance, new Object[] { "setAccessibility(true);" });//$NON-NLS-1$
             eval.invoke(instance, new Object[] { "server(portnum);" });//$NON-NLS-1$
 
         } catch (ClassNotFoundException e) {
             log.error("Beanshell Interpreter not found");
         } catch (Exception e) {
-            log.error("Problem starting BeanShell server ", e);
+            log.error("Problem starting BeanShell server", e);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/util/CustomX509TrustManager.java b/src/core/org/apache/jmeter/util/CustomX509TrustManager.java
index e6b933971..f36af2b1a 100644
--- a/src/core/org/apache/jmeter/util/CustomX509TrustManager.java
+++ b/src/core/org/apache/jmeter/util/CustomX509TrustManager.java
@@ -1,103 +1,108 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
 
 package org.apache.jmeter.util;
 
 import java.security.cert.CertificateException;
 import java.security.cert.X509Certificate;
 
 import javax.net.ssl.X509TrustManager;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
 
 /**
  * Custom TrustManager ignores all certificate errors
  *
  * TODO: implement conditional checking and logging
  *
  * (Derived from AuthSSLX509TrustManager in HttpClient contrib directory)
  */
 
 public class CustomX509TrustManager implements X509TrustManager
 {
     private final X509TrustManager defaultTrustManager;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CustomX509TrustManager.class);
 
     public CustomX509TrustManager(final X509TrustManager defaultTrustManager) {
         super();
         if (defaultTrustManager == null) {
             throw new IllegalArgumentException("Trust manager may not be null");
         }
         this.defaultTrustManager = defaultTrustManager;
     }
 
     /**
      * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String)
      */
     @Override
-    public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
-        if (certificates != null && log.isDebugEnabled()) {
-            for (int c = 0; c < certificates.length; c++) {
-                X509Certificate cert = certificates[c];
-                log.debug(" Client certificate " + (c + 1) + ":");
-                log.debug("  Subject DN: " + cert.getSubjectDN());
-                log.debug("  Signature Algorithm: " + cert.getSigAlgName());
-                log.debug("  Valid from: " + cert.getNotBefore() );
-                log.debug("  Valid until: " + cert.getNotAfter());
-                log.debug("  Issuer: " + cert.getIssuerDN());
+    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
+        if (log.isDebugEnabled() && certificates != null) {
+            for (int i = 0; i < certificates.length; i++) {
+                X509Certificate cert = certificates[i];
+                log.debug(
+                        " Client certificate {}:\n"
+                        + "  Subject DN: {}\n"
+                        + "  Signature Algorithm: {}\n"
+                        + "  Valid from: {}\n"
+                        + "  Valid until: {}\n"
+                        + "  Issuer: {}",
+                        (i + 1),
+                        cert.getSubjectDN(),
+                        cert.getSigAlgName(),
+                        cert.getNotBefore(),
+                        cert.getNotAfter(),
+                        cert.getIssuerDN());
             }
         }
-//        try {
-//            defaultTrustManager.checkClientTrusted(certificates,authType);
-//        } catch (CertificateException e){
-//            log.warn("Ignoring failed Client trust check: "+e.getMessage());
-//        }
     }
 
     /**
      * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String)
      */
     @Override
     public void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
-        if (certificates != null && log.isDebugEnabled()) {
-            for (int c = 0; c < certificates.length; c++) {
-                X509Certificate cert = certificates[c];
-                log.debug(" Server certificate " + (c + 1) + ":");
-                log.debug("  Subject DN: " + cert.getSubjectDN());
-                log.debug("  Signature Algorithm: " + cert.getSigAlgName());
-                log.debug("  Valid from: " + cert.getNotBefore() );
-                log.debug("  Valid until: " + cert.getNotAfter());
-                log.debug("  Issuer: " + cert.getIssuerDN());
+        if (log.isDebugEnabled() && certificates != null) {
+            for (int i = 0; i < certificates.length; i++) {
+                X509Certificate cert = certificates[i];
+                log.debug(
+                        " Server certificate {}:\n"
+                        + "  Subject DN: {}\n"
+                        + "  Signature Algorithm: {}\n"
+                        + "  Valid from: {}\n"
+                        + "  Valid until: {}\n"
+                        + "  Issuer: {}",
+                        (i + 1),
+                        cert.getSubjectDN(),
+                        cert.getSigAlgName(),
+                        cert.getNotBefore(),
+                        cert.getNotAfter(),
+                        cert.getIssuerDN());
             }
         }
-//        try{
-//            defaultTrustManager.checkServerTrusted(certificates,authType);
-//        } catch (CertificateException e){
-//            log.warn("Ignoring failed Server trust check: "+e.getMessage());
-//        }
     }
 
     /**
      * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
      */
     @Override
     public X509Certificate[] getAcceptedIssuers() {
         return this.defaultTrustManager.getAcceptedIssuers();
     }
 }
diff --git a/src/core/org/apache/jmeter/util/Document.java b/src/core/org/apache/jmeter/util/Document.java
index aaa02293c..b9efd22bd 100644
--- a/src/core/org/apache/jmeter/util/Document.java
+++ b/src/core/org/apache/jmeter/util/Document.java
@@ -1,86 +1,86 @@
 /* 
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  * 
  *   http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed  under the  License is distributed on an "AS IS" BASIS,
  * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  * implied.
  * 
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.jmeter.util;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.apache.tika.metadata.Metadata;
 import org.apache.tika.parser.AutoDetectParser;
 import org.apache.tika.parser.ParseContext;
 import org.apache.tika.parser.Parser;
 import org.apache.tika.sax.BodyContentHandler;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.xml.sax.ContentHandler;
 
 public class Document {
     
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Document.class);
     
     // Maximum size to convert a document to text (default 10Mb)
     private static final int MAX_DOCUMENT_SIZE =
         JMeterUtils.getPropDefault("document.max_size", 10 * 1024 * 1024); // $NON-NLS-1$
 
     /**
      * Convert to text plain a lot of kind of document (like odt, ods, odp,
      * doc(x), xls(x), ppt(x), pdf, mp3, mp4, etc.) with Apache Tika
      *
      * @param document
      *            binary representation of the document
      * @return text from document without format
      */
     public static String getTextFromDocument(byte[] document) {
         String errMissingTika = JMeterUtils.getResString("view_results_response_missing_tika"); // $NON-NLS-1$
         String response = errMissingTika;
         Parser parser = new AutoDetectParser();
         ContentHandler handler = new BodyContentHandler(MAX_DOCUMENT_SIZE > 0 ? MAX_DOCUMENT_SIZE : -1); // -1 to disable the write limit
         Metadata metadata = new Metadata();
         ParseContext context = new ParseContext();
         InputStream stream = new ByteArrayInputStream(document); // open the stream
         try {
             parser.parse(stream, handler, metadata, context);
             response = handler.toString();
         } catch (Exception e) {
             response = e.toString();
-            log.warn("Error document parsing:", e);
+            log.warn("Error document parsing.", e);
         } catch (NoClassDefFoundError e) {
             // put a warning if tika-app.jar missing (or some dependencies in only tika-core|parsers packages are using)
             if (!System.getProperty("java.class.path").contains("tika-app")) { // $NON-NLS-1$ $NON-NLS-2$ 
                 log.warn(errMissingTika);
             } else {
                 log.warn(errMissingTika, e);
             }
         } finally {
             try {
                 stream.close(); // close the stream
             } catch (IOException ioe) {
                 log.warn("Error closing document stream", ioe);// $NON-NLS-1$
             }
         }
 
         if (response.length() == 0 && document.length > 0) {
-            log.warn("Probably: " + errMissingTika);// $NON-NLS-1$
+            log.warn("Probably: {}", errMissingTika);// $NON-NLS-1$
             response = errMissingTika;
         }
         return response;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java b/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
index a2ad727dd..654e911b7 100644
--- a/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
+++ b/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
@@ -1,268 +1,270 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
 
 package org.apache.jmeter.util;
 
 import java.io.IOException;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.net.UnknownHostException;
 import java.security.GeneralSecurityException;
 
 import javax.net.ssl.SSLContext;
 import javax.net.ssl.SSLSocket;
 import javax.net.ssl.SSLSocketFactory;
 
 import org.apache.commons.httpclient.ConnectTimeoutException;
 import org.apache.commons.httpclient.params.HttpConnectionParams;
 import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Derived from EasySSLProtocolFactory
  *
  * Used by JsseSSLManager to set up the Commons HttpClient and Java https socket handling
  */
 
 public class HttpSSLProtocolSocketFactory
     extends SSLSocketFactory // for java sockets
     implements SecureProtocolSocketFactory { // for Commons Httpclient sockets
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HttpSSLProtocolSocketFactory.class);
 
     private static final String PROTOCOL_LIST =
             JMeterUtils.getPropDefault("https.socket.protocols", ""); // $NON-NLS-1$ $NON-NLS-2$
 
     private static final String[] protocols = PROTOCOL_LIST.split(" "); // $NON-NLS-1$
 
     static {
-        if (!PROTOCOL_LIST.isEmpty()){
-            log.info("Using protocol list: "+PROTOCOL_LIST);
+        if (!PROTOCOL_LIST.isEmpty()) {
+            log.info("Using protocol list: {}", PROTOCOL_LIST);
         }
     }
 
     private final JsseSSLManager sslManager;
 
     private final int CPS; // Characters per second to emulate
 
     public HttpSSLProtocolSocketFactory(JsseSSLManager sslManager) {
         this(sslManager, 0);
     }
 
     public HttpSSLProtocolSocketFactory(JsseSSLManager sslManager, int cps) {
         super();
         this.sslManager = sslManager;
         CPS=cps;
     }
 
 
     private void setSocket(Socket socket){
         if (!(socket instanceof SSLSocket)) {
             throw new IllegalArgumentException("Expected SSLSocket");
         }
         SSLSocket sock = (SSLSocket) socket;
         if (!PROTOCOL_LIST.isEmpty()) {
             try {
                 sock.setEnabledProtocols(protocols);
             } catch (IllegalArgumentException e) {
-                log.warn("Could not set protocol list: " + PROTOCOL_LIST + ".");
-                log.warn("Valid protocols are: " + join(sock.getSupportedProtocols()));
+                if (log.isWarnEnabled()) {
+                    log.warn("Could not set protocol list: {}.", PROTOCOL_LIST);
+                    log.warn("Valid protocols are: {}", join(sock.getSupportedProtocols()));
+                }
             }
         }
     }
 
     private String join(String[] strings) {
         StringBuilder sb = new StringBuilder();
         for (int i=0;i<strings.length;i++){
             if (i>0) {
                 sb.append(' ');
             }
             sb.append(strings[i]);
         }
         return sb.toString();
     }
 
     private SSLSocketFactory getSSLSocketFactory() throws IOException {
         try {
             SSLContext sslContext = this.sslManager.getContext();
             return sslContext.getSocketFactory();
         } catch (GeneralSecurityException ex) {
             throw new IOException("Rethrown as IOE", ex);
         }
     }
 
     /*
      * Wraps the socket in a slow SSL socket if necessary
      */
     private Socket wrapSocket(Socket sock){
         if (CPS>0) {
             return new SlowSSLSocket((SSLSocket) sock, CPS);
         }
         return sock;
     }
 
     /**
      * Attempts to get a new socket connection to the given host within the given time limit.
      *
      * @param host the host name/IP
      * @param port the port on the host
      * @param localAddress the local host name/IP to bind the socket to
      * @param localPort the port on the local machine
      * @param params {@link HttpConnectionParams Http connection parameters}
      *
      * @return Socket a new socket
      *
      * @throws IOException if an I/O error occurs while creating the socket
      * @throws UnknownHostException if the IP address of the host cannot be
      * determined
      */
     @Override
     public Socket createSocket(
         final String host,
         final int port,
         final InetAddress localAddress,
         final int localPort,
         final HttpConnectionParams params
     ) throws IOException, UnknownHostException, ConnectTimeoutException {
         if (params == null) {
             throw new IllegalArgumentException("Parameters may not be null");
         }
         int timeout = params.getConnectionTimeout();
 
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket socket;
         if (timeout == 0) {
             socket = sslfac.createSocket(host, port, localAddress, localPort);
         } else {
             socket = sslfac.createSocket();
             SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
             SocketAddress remoteaddr = new InetSocketAddress(host, port);
             socket.bind(localaddr);
             socket.connect(remoteaddr, timeout);
         }
         setSocket(socket);
         return wrapSocket(socket);
     }
 
     /**
      * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
      */
     @Override
     public Socket createSocket(String host, int port)
         throws IOException, UnknownHostException {
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket sock = sslfac.createSocket(
             host,
             port
         );
         setSocket(sock);
         return wrapSocket(sock);
     }
 
     /**
      * @see javax.net.SocketFactory#createSocket()
      */
     @Override
     public Socket createSocket() throws IOException, UnknownHostException {
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket sock = sslfac.createSocket();
         setSocket(sock);
         return wrapSocket(sock);
     }
 
     /**
      * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
      */
     @Override
     public Socket createSocket(
         Socket socket,
         String host,
         int port,
         boolean autoClose)
         throws IOException, UnknownHostException {
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket sock = sslfac.createSocket(
             socket,
             host,
             port,
             autoClose
         );
         setSocket(sock);
         return wrapSocket(sock);
     }
 
     /**
      * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
      */
     @Override
     public Socket createSocket(
         String host,
         int port,
         InetAddress clientHost,
         int clientPort)
         throws IOException, UnknownHostException {
 
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket sock = sslfac.createSocket(
             host,
             port,
             clientHost,
             clientPort
         );
         setSocket(sock);
         return wrapSocket(sock);
     }
 
     @Override
     public Socket createSocket(InetAddress host, int port) throws IOException {
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket sock=sslfac.createSocket(host,port);
         setSocket(sock);
         return wrapSocket(sock);
     }
 
     @Override
     public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
         SSLSocketFactory sslfac = getSSLSocketFactory();
         Socket sock=sslfac.createSocket(address, port, localAddress, localPort);
         setSocket(sock);
         return wrapSocket(sock);
     }
 
     @Override
     public String[] getDefaultCipherSuites() {
         try {
             SSLSocketFactory sslfac = getSSLSocketFactory();
             return sslfac.getDefaultCipherSuites();
         } catch (IOException ex) {
             return new String[] {};
         }
     }
 
     @Override
     public String[] getSupportedCipherSuites() {
         try {
             SSLSocketFactory sslfac = getSSLSocketFactory();
             return sslfac.getSupportedCipherSuites();
         } catch (IOException ex) {
             return new String[] {};
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/util/JSR223TestElement.java b/src/core/org/apache/jmeter/util/JSR223TestElement.java
index d0a49bf82..9ffc2f998 100644
--- a/src/core/org/apache/jmeter/util/JSR223TestElement.java
+++ b/src/core/org/apache/jmeter/util/JSR223TestElement.java
@@ -1,290 +1,290 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.Map;
 import java.util.Properties;
 
 import javax.script.Bindings;
 import javax.script.Compilable;
 import javax.script.CompiledScript;
 import javax.script.ScriptEngine;
 import javax.script.ScriptEngineManager;
 import javax.script.ScriptException;
 
 import org.apache.commons.codec.digest.DigestUtils;
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public abstract class JSR223TestElement extends ScriptingTestElement
     implements Serializable, TestStateListener
 {
-    private static final long serialVersionUID = 231L;
+    private static final long serialVersionUID = 232L;
         
     /**
      * Cache of compiled scripts
      */
     @SuppressWarnings("unchecked") // LRUMap does not support generics (yet)
     private static final Map<String, CompiledScript> compiledScriptsCache = 
             Collections.synchronizedMap(
                     new LRUMap(JMeterUtils.getPropDefault("jsr223.compiled_scripts_cache_size", 100)));
 
     /** If not empty then script in ScriptText will be compiled and cached */
     private String cacheKey = "";
     
     /** md5 of the script, used as an unique key for the cache */
     private String scriptMd5 = null;
     
     /**
      * Initialization On Demand Holder pattern
      */
     private static class LazyHolder {
         private LazyHolder() {
             super();
         }
         public static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
     }
  
     /**
      * @return ScriptEngineManager singleton
      */
     public static ScriptEngineManager getInstance() {
             return LazyHolder.INSTANCE;
     }
 
     public JSR223TestElement() {
         super();
     }
 
     /**
      * @return {@link ScriptEngine} for language defaulting to groovy if language is not set
      * @throws ScriptException when no {@link ScriptEngine} could be found
      */
     protected ScriptEngine getScriptEngine() throws ScriptException {
         String lang = getScriptLanguageWithDefault();
         ScriptEngine scriptEngine = getInstance().getEngineByName(lang);
         if (scriptEngine == null) {
             throw new ScriptException("Cannot find engine named: '"+lang+"', ensure you set language field in JSR223 Test Element: "+getName());
         }
 
         return scriptEngine;
     }
 
     /**
      * @return script language or DEFAULT_SCRIPT_LANGUAGE if none is set
      */
     private String getScriptLanguageWithDefault() {
         String lang = getScriptLanguage();
         if (StringUtils.isNotEmpty(lang)) {
             return lang;
         }
         return DEFAULT_SCRIPT_LANGUAGE;
     }
 
     /**
      * Populate variables to be passed to scripts
      * @param bindings Bindings
      */
     protected void populateBindings(Bindings bindings) {
         final String label = getName();
         final String fileName = getFilename();
         final String scriptParameters = getParameters();
         // Use actual class name for log
-        final Logger logger = LoggingManager.getLoggerForShortName(getClass().getName());
+        final Logger logger = LoggerFactory.getLogger(getClass());
         bindings.put("log", logger); // $NON-NLS-1$ (this name is fixed)
         bindings.put("Label", label); // $NON-NLS-1$ (this name is fixed)
         bindings.put("FileName", fileName); // $NON-NLS-1$ (this name is fixed)
         bindings.put("Parameters", scriptParameters); // $NON-NLS-1$ (this name is fixed)
         String [] args=JOrphanUtils.split(scriptParameters, " ");//$NON-NLS-1$
         bindings.put("args", args); // $NON-NLS-1$ (this name is fixed)
         // Add variables for access to context and variables
         JMeterContext jmctx = JMeterContextService.getContext();
         bindings.put("ctx", jmctx); // $NON-NLS-1$ (this name is fixed)
         JMeterVariables vars = jmctx.getVariables();
         bindings.put("vars", vars); // $NON-NLS-1$ (this name is fixed)
         Properties props = JMeterUtils.getJMeterProperties();
         bindings.put("props", props); // $NON-NLS-1$ (this name is fixed)
         // For use in debugging:
         bindings.put("OUT", System.out); // NOSONAR $NON-NLS-1$ (this name is fixed)
 
         // Most subclasses will need these:
         Sampler sampler = jmctx.getCurrentSampler();
         bindings.put("sampler", sampler); // $NON-NLS-1$ (this name is fixed)
         SampleResult prev = jmctx.getPreviousResult();
         bindings.put("prev", prev); // $NON-NLS-1$ (this name is fixed)
     }
 
 
     /**
      * This method will run inline script or file script with special behaviour for file script:
      * - If ScriptEngine implements Compilable script will be compiled and cached
      * - If not if will be run
      * @param scriptEngine ScriptEngine
      * @param bindings {@link Bindings} might be null
      * @return Object returned by script
      * @throws IOException when reading the script fails
      * @throws ScriptException when compiling or evaluation of the script fails
      */
     protected Object processFileOrScript(ScriptEngine scriptEngine, Bindings bindings) throws IOException, ScriptException {
         if (bindings == null) {
             bindings = scriptEngine.createBindings();
         }
         populateBindings(bindings);
         File scriptFile = new File(getFilename()); 
         // Hack: bsh-2.0b5.jar BshScriptEngine implements Compilable but throws "java.lang.Error: unimplemented"
         boolean supportsCompilable = scriptEngine instanceof Compilable 
                 && !("bsh.engine.BshScriptEngine".equals(scriptEngine.getClass().getName())); // NOSONAR $NON-NLS-1$
         if (!StringUtils.isEmpty(getFilename())) {
             if (scriptFile.exists() && scriptFile.canRead()) {
                 if (supportsCompilable) {
                     String cacheKey = 
                             getScriptLanguage()+"#"+ // $NON-NLS-1$
                             scriptFile.getAbsolutePath()+"#"+  // $NON-NLS-1$
                                     scriptFile.lastModified();
                     CompiledScript compiledScript = 
                             compiledScriptsCache.get(cacheKey);
                     if (compiledScript==null) {
                         synchronized (compiledScriptsCache) {
                             compiledScript = 
                                     compiledScriptsCache.get(cacheKey);
                             if (compiledScript==null) {
                                 // TODO Charset ?
                                 try ( BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile), 
                                         (int)scriptFile.length())) {
                                     compiledScript = 
                                             ((Compilable) scriptEngine).compile(fileReader);
                                     compiledScriptsCache.put(cacheKey, compiledScript);
                                 }
                             }
                         }
                     }
                     return compiledScript.eval(bindings);
                 } else {
                     // TODO Charset ?
                     try ( BufferedReader fileReader = new BufferedReader(new FileReader(scriptFile), 
                             (int)scriptFile.length())) {
                         return scriptEngine.eval(fileReader, bindings);
                     }
                 }
             }  else {
                 throw new ScriptException("Script file '"+scriptFile.getAbsolutePath()+"' does not exist or is unreadable for element:"+getName());
             }
         } else if (!StringUtils.isEmpty(getScript())) {
             if (supportsCompilable && !StringUtils.isEmpty(cacheKey)) {
                 computeScriptMD5();
                 CompiledScript compiledScript = compiledScriptsCache.get(this.scriptMd5);
                 if (compiledScript == null) {
                     synchronized (compiledScriptsCache) {
                         compiledScript = compiledScriptsCache.get(this.scriptMd5);
                         if (compiledScript == null) {
                             compiledScript = ((Compilable) scriptEngine).compile(getScript());
                             compiledScriptsCache.put(this.scriptMd5, compiledScript);
                         }
                     }
                 }
                 
                 return compiledScript.eval(bindings);
             } else {
                 return scriptEngine.eval(getScript(), bindings);
             }
         } else {
             throw new ScriptException("Both script file and script text are empty for element:"+getName());            
         }
     }
 
     /**
      * compute MD5 if it is null
      */
     private void computeScriptMD5() {
         // compute the md5 of the script if needed
         if(scriptMd5 == null) {
             scriptMd5 = DigestUtils.md5Hex(getScript());
         }
     }
 
     /**
      * @return the cacheKey
      */
     public String getCacheKey() {
         return cacheKey;
     }
 
     /**
      * @param cacheKey the cacheKey to set
      */
     public void setCacheKey(String cacheKey) {
         this.cacheKey = cacheKey;
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted()
      */
     @Override
     public void testStarted() {
         // NOOP
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testStarted(java.lang.String)
      */
     @Override
     public void testStarted(String host) {
         // NOOP   
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded()
      */
     @Override
     public void testEnded() {
         testEnded("");
     }
 
     /**
      * @see org.apache.jmeter.testelement.TestStateListener#testEnded(java.lang.String)
      */
     @Override
     public void testEnded(String host) {
         compiledScriptsCache.clear();
         this.scriptMd5 = null;
     }
     
     public String getScriptLanguage() {
         return scriptLanguage;
     }
 
     public void setScriptLanguage(String s) {
         scriptLanguage = s;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/JsseSSLManager.java b/src/core/org/apache/jmeter/util/JsseSSLManager.java
index 025e9d2bc..457baa593 100644
--- a/src/core/org/apache/jmeter/util/JsseSSLManager.java
+++ b/src/core/org/apache/jmeter/util/JsseSSLManager.java
@@ -1,419 +1,424 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.net.HttpURLConnection;
 import java.net.Socket;
 import java.security.GeneralSecurityException;
 import java.security.Principal;
 import java.security.PrivateKey;
 import java.security.Provider;
 import java.security.SecureRandom;
 import java.security.cert.X509Certificate;
 
 import javax.net.ssl.HostnameVerifier;
 import javax.net.ssl.HttpsURLConnection;
 import javax.net.ssl.KeyManager;
 import javax.net.ssl.KeyManagerFactory;
 import javax.net.ssl.SSLContext;
 import javax.net.ssl.SSLSession;
 import javax.net.ssl.TrustManager;
 import javax.net.ssl.TrustManagerFactory;
 import javax.net.ssl.X509ExtendedKeyManager;
 import javax.net.ssl.X509KeyManager;
 import javax.net.ssl.X509TrustManager;
 
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
 import org.apache.jmeter.util.keystore.JmeterKeyStore;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The SSLManager handles the KeyStore information for JMeter. Basically, it
  * handles all the logic for loading and initializing all the JSSE parameters
  * and selecting the alias to authenticate against if it is available.
  * SSLManager will try to automatically select the client certificate for you,
  * but if it can't make a decision, it will pop open a dialog asking you for
  * more information.
  *
  * TODO: does not actually prompt
  *
  */
 public class JsseSSLManager extends SSLManager {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JsseSSLManager.class);
 
     private static final String HTTPS = "https"; // $NON-NLS-1$
 
     // Temporary fix to allow default protocol to be changed
     private static final String DEFAULT_SSL_PROTOCOL =
         JMeterUtils.getPropDefault("https.default.protocol","TLS"); // $NON-NLS-1$ // $NON-NLS-2$
 
     // Allow reversion to original shared session context
     private static final boolean SHARED_SESSION_CONTEXT =
         JMeterUtils.getPropDefault("https.sessioncontext.shared",false); // $NON-NLS-1$
 
     /**
      * Characters per second, used to slow down sockets
      */
     public static final int CPS = JMeterUtils.getPropDefault("httpclient.socket.https.cps", 0); // $NON-NLS-1$
 
     static {
-        log.info("Using default SSL protocol: "+DEFAULT_SSL_PROTOCOL);
-        log.info("SSL session context: "+(SHARED_SESSION_CONTEXT ? "shared" : "per-thread"));
+        if (log.isInfoEnabled()) {
+            log.info("Using default SSL protocol: {}", DEFAULT_SSL_PROTOCOL);
+            log.info("SSL session context: {}", (SHARED_SESSION_CONTEXT ? "shared" : "per-thread"));
 
-        if (CPS > 0) {
-            log.info("Setting up HTTPS SlowProtocol, cps="+CPS);
+            if (CPS > 0) {
+                log.info("Setting up HTTPS SlowProtocol, cps={}", CPS);
+            }
         }
-
     }
 
     /**
      * Cache the SecureRandom instance because it takes a long time to create
      */
     private SecureRandom rand;
 
     private Provider pro = null; // TODO why not use the super class value?
 
     private SSLContext defaultContext; // If we are using a single session
     private ThreadLocal<SSLContext> threadlocal; // Otherwise
 
     /**
      * Create the SSLContext, and wrap all the X509KeyManagers with
      * our X509KeyManager so that we can choose our alias.
      *
      * @param provider
      *            Description of Parameter
      */
     public JsseSSLManager(Provider provider) {
-        log.debug("ssl Provider =  " + provider);
+        log.debug("ssl Provider = {}", provider);
         setProvider(provider);
         if (null == this.rand) { // Surely this is always null in the constructor?
             this.rand = new SecureRandom();
         }
         try {
             if (SHARED_SESSION_CONTEXT) {
                 log.debug("Creating shared context");
                 this.defaultContext = createContext();
             } else {
                 this.threadlocal = new ThreadLocal<>();
             }
 
             HttpsURLConnection.setDefaultSSLSocketFactory(new HttpSSLProtocolSocketFactory(this, CPS));
             HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                 @Override
                 public boolean verify(String hostname, SSLSession session) {
                     return true;
                 }
             });
 
             /*
              * Also set up HttpClient defaults
              */
             Protocol protocol = new Protocol(
                     JsseSSLManager.HTTPS,
                     (ProtocolSocketFactory) new HttpSSLProtocolSocketFactory(this, CPS),
                     443);
             Protocol.registerProtocol(JsseSSLManager.HTTPS, protocol);
             log.debug("SSL stuff all set");
         } catch (GeneralSecurityException ex) {
             log.error("Could not set up SSLContext", ex);
         }
         log.debug("JsseSSLManager installed");
     }
 
     /**
      * Sets the Context attribute of the JsseSSLManager object
      *
      * @param conn
      *            The new Context value
      */
     @Override
     public void setContext(HttpURLConnection conn) {
         if (conn instanceof HttpsURLConnection) {
 /*
  * No point doing this on a per-connection basis, as there is currently no way to configure it.
  * So we leave it to the defaults set up in the SSL Context
  *
  */
 //          HttpsURLConnection secureConn = (HttpsURLConnection) conn;
 //          secureConn.setSSLSocketFactory(this.getContext().getSocketFactory());
         } else {
-            log.warn("Unexpected HttpURLConnection class: "+conn.getClass().getName());
+            if (log.isWarnEnabled()) {
+                log.warn("Unexpected HttpURLConnection class: {}", conn.getClass().getName());
+            }
         }
     }
 
     /**
      * Sets the Provider attribute of the JsseSSLManager object
      *
      * @param p
      *            The new Provider value
      */
     @Override
     protected final void setProvider(Provider p) {
         super.setProvider(p);
         if (null == this.pro) {
             this.pro = p;
         }
     }
 
     /**
      * Returns the SSLContext we are using. This is either a context per thread,
      * or, for backwards compatibility, a single shared context.
      *
      * @return The Context value
      * @throws GeneralSecurityException
      *             when constructing the context fails
      */
     public SSLContext getContext() throws GeneralSecurityException {
         if (SHARED_SESSION_CONTEXT) {
             if (log.isDebugEnabled()){
-                log.debug("Using shared SSL context for: "+Thread.currentThread().getName());
+                log.debug("Using shared SSL context for: {}", Thread.currentThread().getName());
             }
             return this.defaultContext;
         }
 
         SSLContext sslContext = this.threadlocal.get();
         if (sslContext == null) {
             if (log.isDebugEnabled()){
-                log.debug("Creating threadLocal SSL context for: "+Thread.currentThread().getName());
+                log.debug("Creating threadLocal SSL context for: {}", Thread.currentThread().getName());
             }
             sslContext = createContext();
             this.threadlocal.set(sslContext);
         }
         if (log.isDebugEnabled()){
-            log.debug("Using threadLocal SSL context for: "+Thread.currentThread().getName());
+            log.debug("Using threadLocal SSL context for: {}", Thread.currentThread().getName());
         }
         return sslContext;
     }
 
     /**
      * Resets the SSLContext if using per-thread contexts.
      *
      */
     public void resetContext() {
         if (!SHARED_SESSION_CONTEXT) {
             log.debug("Clearing session context for current thread");
             this.threadlocal.set(null);
         }
     }
     
     /*
      * 
      * Creates new SSL context
      * 
      * @return SSL context
      * 
      * @throws GeneralSecurityException when the algorithm for the context can
      * not be found or the keys have problems
      */
     private SSLContext createContext() throws GeneralSecurityException {
         SSLContext context;
         if (pro != null) {
             context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL, pro); // $NON-NLS-1$
         } else {
             context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL); // $NON-NLS-1$
         }
         KeyManagerFactory managerFactory =
             KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
         JmeterKeyStore keys = this.getKeyStore();
         managerFactory.init(null, defaultpw == null ? new char[]{} : defaultpw.toCharArray());
         KeyManager[] managers = managerFactory.getKeyManagers();
         KeyManager[] newManagers = new KeyManager[managers.length];
-        
-        log.debug(keys.getClass().toString());
+
+        if (log.isDebugEnabled()) {
+            log.debug("JmeterKeyStore type: {}", keys.getClass());
+        }
 
         // Now wrap the default managers with our key manager
         for (int i = 0; i < managers.length; i++) {
             if (managers[i] instanceof X509KeyManager) {
                 X509KeyManager manager = (X509KeyManager) managers[i];
                 newManagers[i] = new WrappedX509KeyManager(manager, keys);
             } else {
                 newManagers[i] = managers[i];
             }
         }
 
         // Get the default trust managers
         TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
                 TrustManagerFactory.getDefaultAlgorithm());
         tmfactory.init(this.getTrustStore());
 
         // Wrap the defaults in our custom trust manager
         TrustManager[] trustmanagers = tmfactory.getTrustManagers();
         for (int i = 0; i < trustmanagers.length; i++) {
             if (trustmanagers[i] instanceof X509TrustManager) {
                 trustmanagers[i] = new CustomX509TrustManager(
                     (X509TrustManager)trustmanagers[i]);
             }
         }
         context.init(newManagers, trustmanagers, this.rand);
         if (log.isDebugEnabled()){
             String[] dCiphers = context.getSocketFactory().getDefaultCipherSuites();
             String[] sCiphers = context.getSocketFactory().getSupportedCipherSuites();
             int len = (dCiphers.length > sCiphers.length) ? dCiphers.length : sCiphers.length;
             for (int i = 0; i < len; i++) {
                 if (i < dCiphers.length) {
-                    log.debug("Default Cipher: " + dCiphers[i]);
+                    log.debug("Default Cipher: {}", dCiphers[i]);
                 }
                 if (i < sCiphers.length) {
-                    log.debug("Supported Cipher: " + sCiphers[i]);
+                    log.debug("Supported Cipher: {}", sCiphers[i]);
                 }
             }
         }
         return context;
     }
 
     /**
      * This is the X509KeyManager we have defined for the sole purpose of
      * selecting the proper key and certificate based on the keystore available.
      *
      */
     private static class WrappedX509KeyManager extends X509ExtendedKeyManager {
 
         /**
          * The parent X509KeyManager.
          * This is used for the methods {@link #getServerAliases(String, Principal[])}
          *  and {@link #chooseServerAlias(String, Principal[], Socket)}
          */
         private final X509KeyManager manager;
 
         /**
          * The KeyStore this KeyManager uses.
          * This is used for the remaining X509KeyManager methods: 
          * {@link #getClientAliases(String, Principal[])},
          * {@link #getCertificateChain(String)},
          * {@link #getPrivateKey(String)} and
          * {@link #chooseClientAlias(String[], Principal[], Socket)}
          */
         private final JmeterKeyStore store;
 
         /**
          * Instantiate a new WrappedX509KeyManager.
          *
          * @param parent
          *            The parent X509KeyManager
          * @param ks
          *            The KeyStore we derive our client certs and keys from
          */
         public WrappedX509KeyManager(X509KeyManager parent, JmeterKeyStore ks) {
             this.manager = parent;
             this.store = ks;
         }
 
         /**
          * Compiles the list of all client aliases with a private key.
          *
          * @param keyType the key algorithm type name (RSA, DSA, etc.)
          * @param issuers  the CA certificates we are narrowing our selection on.
          * 
          * @return the array of aliases; may be empty
          */
         @Override
         public String[] getClientAliases(String keyType, Principal[] issuers) {
             log.debug("WrappedX509Manager: getClientAliases: ");
             // implementation moved to JmeterKeystore as only that has the keyType info
             return this.store.getClientAliases(keyType, issuers);
         }
 
         /**
          * Get the list of server aliases for the SSLServerSockets. This is not
          * used in JMeter.
          *
          * @param keyType
          *            the type of private key the server expects (RSA, DSA,
          *            etc.)
          * @param issuers
          *            the CA certificates we are narrowing our selection on.
          * @return the ServerAliases value
          */
         @Override
         public String[] getServerAliases(String keyType, Principal[] issuers) {
             log.debug("WrappedX509Manager: getServerAliases: ");
             return this.manager.getServerAliases(keyType, issuers);
         }
 
         /**
          * Get the Certificate chain for a particular alias
          *
          * @param alias
          *            The client alias
          * @return The CertificateChain value
          */
         @Override
         public X509Certificate[] getCertificateChain(String alias) {
-            log.debug("WrappedX509Manager: getCertificateChain(" + alias + ")");
+            log.debug("WrappedX509Manager: getCertificateChain({})", alias);
             return this.store.getCertificateChain(alias);
         }
 
         /**
          * Get the Private Key for a particular alias
          *
          * @param alias
          *            The client alias
          * @return The PrivateKey value
          */
         @Override
         public PrivateKey getPrivateKey(String alias) {
             PrivateKey privateKey = this.store.getPrivateKey(alias);
-            log.debug("WrappedX509Manager: getPrivateKey: " + privateKey);
+            log.debug("WrappedX509Manager: getPrivateKey: {}", privateKey);
             return privateKey;
         }
 
         /**
          * Select the Alias we will authenticate as if Client authentication is
          * required by the server we are connecting to. We get the list of
          * aliases, and if there is only one alias we automatically select it.
          * If there are more than one alias that has a private key, we prompt
          * the user to choose which alias using a combo box. Otherwise, we
          * simply provide a text box, which may or may not work. The alias does
          * have to match one in the keystore.
          *
          * TODO? - does not actually allow the user to choose an alias at present
          * 
          * @param keyType the key algorithm type name(s), ordered with the most-preferred key type first.
          * @param issuers the list of acceptable CA issuer subject names or null if it does not matter which issuers are used.
          * @param socket the socket to be used for this connection. 
          *     This parameter can be null, which indicates that implementations are free to select an alias applicable to any socket.
          * 
          * @see javax.net.ssl.X509KeyManager#chooseClientAlias(String[], Principal[], Socket)
          */
         @Override
         public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
             if(log.isDebugEnabled()) {
-                log.debug("keyType: " + keyType[0]);
+                log.debug("keyType: {}", keyType[0]);
             }
             String alias = this.store.getAlias();
             if(log.isDebugEnabled()) {
-                log.debug("Client alias:'"+alias+"'");
+                log.debug("Client alias: '{}'", alias);
             }
             return alias;
         }
 
         /**
          * Choose the server alias for the SSLServerSockets. This are not used
          * in JMeter.
          *
          * @see javax.net.ssl.X509KeyManager#chooseServerAlias(String, Principal[], Socket)
          */
         @Override
         public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
             return this.manager.chooseServerAlias(keyType, issuers, socket);
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/util/NameUpdater.java b/src/core/org/apache/jmeter/util/NameUpdater.java
index 7d652f311..5a84ad42a 100644
--- a/src/core/org/apache/jmeter/util/NameUpdater.java
+++ b/src/core/org/apache/jmeter/util/NameUpdater.java
@@ -1,190 +1,190 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 /*
  * Created on Jun 13, 2003
  */
 package org.apache.jmeter.util;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.Enumeration;
 import java.util.Properties;
 
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public final class NameUpdater {
     private static final Properties nameMap;
     // Read-only access after class has been initialised
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(NameUpdater.class);
 
     private static final String NAME_UPDATER_PROPERTIES = 
             "META-INF/resources/org.apache.jmeter.nameupdater.properties"; // $NON-NLS-1$
 
     static {
         nameMap = new Properties();
         FileInputStream fis = null;
         File f = new File(JMeterUtils.getJMeterHome(),
                 JMeterUtils.getPropDefault("upgrade_properties", // $NON-NLS-1$
                         "/bin/upgrade.properties")); // $NON-NLS-1$
         try {
             fis = new FileInputStream(f);
             nameMap.load(fis);
         } catch (FileNotFoundException e) {
-            log.error("Could not find upgrade file: ", e);
+            log.error("Could not find upgrade file.", e);
         } catch (IOException e) {
-            log.error("Error processing upgrade file: "+f.getPath(), e);
+            log.error("Error processing upgrade file: {}", f, e);
         } finally {
             JOrphanUtils.closeQuietly(fis);
         }
 
         //load additionnal name conversion rules from plugins
         Enumeration<URL> enu = null;
 
         try {
            enu = JMeterUtils.class.getClassLoader().getResources(NAME_UPDATER_PROPERTIES);
         } catch (IOException e) {
-           log.error("Error in finding additional nameupdater.properties files: ", e);
+           log.error("Error in finding additional nameupdater.properties files.", e);
         }
 
         if(enu != null) {
             while(enu.hasMoreElements()) {
                 URL ressourceUrl = enu.nextElement();
-                log.info("Processing "+ressourceUrl.toString());
+                log.info("Processing {}", ressourceUrl);
                 Properties prop = new Properties();
                 InputStream is = null;
                 try {
                     is = ressourceUrl.openStream();
                     prop.load(is);
                 } catch (IOException e) {
-                    log.error("Error processing upgrade file: " + ressourceUrl.getPath(), e);
+                    log.error("Error processing upgrade file: {}", ressourceUrl.getPath(), e);
                 } finally {
                     JOrphanUtils.closeQuietly(is);
                 }
 
                 @SuppressWarnings("unchecked") // names are Strings
                 Enumeration<String> propertyNames = (Enumeration<String>) prop.propertyNames();
                 while (propertyNames.hasMoreElements()) {
                     String key = propertyNames.nextElement();
                     if (!nameMap.containsKey(key)) {
                        nameMap.put(key, prop.get(key));
-                       log.info("Added additional nameMap entry: " + key);
+                       log.info("Added additional nameMap entry: {}", key);
                     } else {
-                       log.warn("Additional nameMap entry: '" + key + "' rejected as already defined.");
+                       log.warn("Additional nameMap entry: '{}' rejected as already defined.", key);
                     }
                 }
             }
         }
     }
 
     /**
      * Looks up the class name; if that does not exist in the map, 
      * then defaults to the input name.
      * 
      * @param className the classname from the script file
      * @return the class name to use, possibly updated.
      */
     public static String getCurrentName(String className) {
         if (nameMap.containsKey(className)) {
             String newName = nameMap.getProperty(className);
-            log.info("Upgrading class " + className + " to " + newName);
+            log.info("Upgrading class {} to {}", className, newName);
             return newName;
         }
         return className;
     }
 
     /**
      * Looks up test element / gui class combination; if that
      * does not exist in the map, then defaults to getCurrentName(testClassName).
      *
      * @param testClassName - test element class name
      * @param guiClassName - associated gui class name
      * @return new test class name
      */
     public static String getCurrentTestName(String testClassName, String guiClassName) {
         String key = testClassName + "|" + guiClassName;
         if (nameMap.containsKey(key)) {
             String newName = nameMap.getProperty(key);
-            log.info("Upgrading " + key + " to " + newName);
+            log.info("Upgrading {} to {}", key, newName);
             return newName;
         }
         return getCurrentName(testClassName);
     }
 
     /**
      * Looks up class name / property name combination; if that
      * does not exist in the map, then defaults to input property name.
      *
      * @param propertyName - property name to check
      * @param className - class name containing the property
      * @return possibly updated property name
      */
     public static String getCurrentName(String propertyName, String className) {
         String key = className + "/" + propertyName;
         if (nameMap.containsKey(key)) {
             String newName = nameMap.getProperty(key);
-            log.info("Upgrading property " + propertyName + " to " + newName);
+            log.info("Upgrading property {} to {}", propertyName, newName);
             return newName;
         }
         return propertyName;
     }
 
     /**
      * Looks up class name . property name / value combination;
      * if that does not exist in the map, returns the original value.
      * 
      * @param value the value to be checked
      * @param propertyName the name of the property
      * @param className the class containing the propery.
      * @return the value, updated if necessary
      */
     public static String getCurrentName(String value, String propertyName, String className) {
         String key = className + "." + propertyName + "/" + value;
         if (nameMap.containsKey(key)) {
             String newValue = nameMap.getProperty(key);
-            log.info("Upgrading value " + value + " to " + newValue);
+            log.info("Upgrading value {} to {}", value, newValue);
             return newValue;
         }
         return value;
     }
 
     /**
      * Private constructor to prevent instantiation.
      */
     private NameUpdater() {
     }
 
     /**
      * Check if a key is in the map; intended for use by 
      * {@link org.apache.jmeter.save.SaveService#checkClasses() SaveService#checkClasses()}
      * only.
      * 
      * @param key name of the key to check
      * @return true if the key is in the map
      */
     public static boolean isMapped(String key) {
         return nameMap.containsKey(key);
     }
 }
diff --git a/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java b/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java
index 2180eb95a..6c9b0de11 100644
--- a/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java
+++ b/src/core/org/apache/jmeter/util/PropertiesBasedPrefixResolver.java
@@ -1,99 +1,97 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.apache.commons.lang3.StringUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
 import org.apache.xml.utils.PrefixResolver;
 import org.apache.xml.utils.PrefixResolverDefault;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Node;
 
 /**
  * {@link PrefixResolver} implementation that loads prefix configuration from jmeter property xpath.namespace.config
  */
 public class PropertiesBasedPrefixResolver extends PrefixResolverDefault {
-    private static final Logger logger = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(PropertiesBasedPrefixResolver.class);
     private static final String XPATH_NAMESPACE_CONFIG = "xpath.namespace.config";
     private static final Map<String, String> NAMESPACE_MAP = new HashMap<>();
     static {
         String pathToNamespaceConfig = JMeterUtils.getPropDefault(XPATH_NAMESPACE_CONFIG, "");
         if(!StringUtils.isEmpty(pathToNamespaceConfig)) {
             Properties properties = new Properties();
             InputStream inputStream = null;
             try {
                 File pathToNamespaceConfigFile = JMeterUtils.findFile(pathToNamespaceConfig);
                 if(!pathToNamespaceConfigFile.exists()) {
-                    logger.error("Cannot find configured file:'"+
-                            pathToNamespaceConfig+"' in property:'"+XPATH_NAMESPACE_CONFIG+"', file does not exist");
+                    log.error("Cannot find configured file:'{}' in property:'{}', file does not exist",
+                            pathToNamespaceConfig, XPATH_NAMESPACE_CONFIG);
                 } else { 
                     if(!pathToNamespaceConfigFile.canRead()) {
-                        logger.error("Cannot read configured file:'"+
-                                pathToNamespaceConfig+"' in property:'"+XPATH_NAMESPACE_CONFIG+"'");
+                        log.error("Cannot read configured file:'{}' in property:'{}'", pathToNamespaceConfig,
+                                XPATH_NAMESPACE_CONFIG);
                     } else {
                         inputStream = new BufferedInputStream(new FileInputStream(pathToNamespaceConfigFile));
                         properties.load(inputStream);
                         properties.entrySet();
                         for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                             NAMESPACE_MAP.put((String) entry.getKey(), (String) entry.getValue());
                         }
-                        logger.info("Read following XPath namespace configuration "+ 
-                                NAMESPACE_MAP);
+                        log.info("Read following XPath namespace configuration {}", NAMESPACE_MAP);
                     }
                 }
             } catch(IOException e) {
-                logger.error("Error loading namespaces from file:'"+
-                        pathToNamespaceConfig+"', message:"+e.getMessage(),e);
+                log.error("Error loading namespaces from file:'{}', message: {}", pathToNamespaceConfig, e.getMessage(), e);
             } finally {
                 JOrphanUtils.closeQuietly(inputStream);
             }
         }
     }
     /**
      * @param xpathExpressionContext Node
      */
     public PropertiesBasedPrefixResolver(Node xpathExpressionContext) {
         super(xpathExpressionContext);
     }
 
     /**
      * Searches prefix in NAMESPACE_MAP, if it fails to find it defaults to parent implementation
      * @param prefix Prefix
      * @param namespaceContext Node
      */
     @Override
     public String getNamespaceForPrefix(String prefix, Node namespaceContext) {
         String namespace = NAMESPACE_MAP.get(prefix);
         if(namespace==null) {
             return super.getNamespaceForPrefix(prefix, namespaceContext);
         } else {
             return namespace;
         }
     }
 }
diff --git a/src/core/org/apache/jmeter/util/SSLManager.java b/src/core/org/apache/jmeter/util/SSLManager.java
index 022ac081d..400e51f7f 100644
--- a/src/core/org/apache/jmeter/util/SSLManager.java
+++ b/src/core/org/apache/jmeter/util/SSLManager.java
@@ -1,299 +1,301 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.InputStream;
 import java.net.HttpURLConnection;
 import java.security.KeyStore;
 import java.security.Provider;
 import java.security.Security;
 import java.util.Locale;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.keystore.JmeterKeyStore;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The SSLManager handles the KeyStore information for JMeter. Basically, it
  * handles all the logic for loading and initializing all the JSSE parameters
  * and selecting the alias to authenticate against if it is available.
  * SSLManager will try to automatically select the client certificate for you,
  * but if it can't make a decision, it will pop open a dialog asking you for
  * more information.
  *
  * TODO? - N.B. does not currently allow the selection of a client certificate.
  *
  */
 public abstract class SSLManager {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SSLManager.class);
 
     private static final String SSL_TRUST_STORE = "javax.net.ssl.trustStore";// $NON-NLS-1$
 
     private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword"; // $NON-NLS-1$ NOSONAR no hard coded password
 
     public static final String JAVAX_NET_SSL_KEY_STORE = "javax.net.ssl.keyStore"; // $NON-NLS-1$
 
     private static final String JAVAX_NET_SSL_KEY_STORE_TYPE = "javax.net.ssl.keyStoreType"; // $NON-NLS-1$
 
     private static final String PKCS12 = "pkcs12"; // $NON-NLS-1$
 
     /** Singleton instance of the manager */
     //@GuardedBy("this")
     private static SSLManager manager;
 
     private static final boolean IS_SSL_SUPPORTED = true;
 
     /** Cache the KeyStore instance */
     private volatile JmeterKeyStore keyStore;
 
     /** Cache the TrustStore instance - null if no truststore name was provided */
     private KeyStore trustStore = null;
     // Have we yet tried to load the truststore?
     private volatile boolean truststoreLoaded=false;
 
     /** Have the password available */
     protected String defaultpw = System.getProperty(KEY_STORE_PASSWORD);
 
     private int keystoreAliasStartIndex;
 
     private int keystoreAliasEndIndex;
 
     private String clientCertAliasVarName;
 
     /**
      * Resets the SSLManager so that we can create a new one with a new keystore
      */
     public static synchronized void reset() {
         SSLManager.manager = null;
     }
 
     public abstract void setContext(HttpURLConnection conn);
 
     /**
      * Default implementation of setting the Provider
      *
      * @param provider
      *            the provider to use
      */
     protected void setProvider(Provider provider) {
         if (null != provider) {
             Security.addProvider(provider);
         }
     }
     
     /**
      * Opens and initializes the KeyStore. If the password for the KeyStore is
      * not set, this method will prompt you to enter it. Unfortunately, there is
      * no PasswordEntryField available from JOptionPane.
      *
      * @return the configured {@link JmeterKeyStore}
      */
     protected synchronized JmeterKeyStore getKeyStore() {
         if (null == this.keyStore) {
             String fileName = System.getProperty(JAVAX_NET_SSL_KEY_STORE,""); // empty if not provided
             String fileType = System.getProperty(JAVAX_NET_SSL_KEY_STORE_TYPE, // use the system property to determine the type
                     fileName.toLowerCase(Locale.ENGLISH).endsWith(".p12") ? PKCS12 : "JKS"); // otherwise use the name
-            log.info("JmeterKeyStore Location: " + fileName + " type " + fileType);
+            log.info("JmeterKeyStore Location: {} type {}", fileName, fileType);
             try {
                 this.keyStore = JmeterKeyStore.getInstance(fileType, keystoreAliasStartIndex, keystoreAliasEndIndex, clientCertAliasVarName);
                 log.info("KeyStore created OK");
             } catch (Exception e) {
                 this.keyStore = null;
                 throw new RuntimeException("Could not create keystore: "+e.getMessage(), e);
             }
             InputStream fileInputStream = null;
             try {
                 File initStore = new File(fileName);
 
                 if (fileName.length() >0 && initStore.exists()) {
                     fileInputStream = new BufferedInputStream(new FileInputStream(initStore));
                     this.keyStore.load(fileInputStream, getPassword());
                     if (log.isInfoEnabled()) {
-                        log.info("Total of " + keyStore.getAliasCount() + " aliases loaded OK from keystore");
+                        log.info("Total of {} aliases loaded OK from keystore", keyStore.getAliasCount());
                     }
                 } else {
                     log.warn("Keystore file not found, loading empty keystore");
                     this.defaultpw = ""; // Ensure not null
                     this.keyStore.load(null, "");
                 }
             } catch (Exception e) {
-                log.error("Problem loading keystore: " +e.getMessage(), e);
+                log.error("Problem loading keystore: {}", e.getMessage(), e);
             } finally {
                 JOrphanUtils.closeQuietly(fileInputStream);
             }
 
-            log.debug("JmeterKeyStore type: " + this.keyStore.getClass().toString());
+            if (log.isDebugEnabled()) {
+                log.debug("JmeterKeyStore type: {}", this.keyStore.getClass());
+            }
         }
 
         return this.keyStore;
     }
 
     /*
      * The password can be defined as a property; this dialogue is provided to allow it
      * to be entered at run-time.
      *
      * However, this does not gain much, as the dialogue does not (yet) support hidden input ...
      *
     */
     private String getPassword() {
         String password = this.defaultpw;
         if (null == password) {
             final GuiPackage guiInstance = GuiPackage.getInstance();
             if (guiInstance != null) {
                 synchronized (this) { // TODO is sync really needed?
                     this.defaultpw = JOptionPane.showInputDialog(
                             guiInstance.getMainFrame(),
                             JMeterUtils.getResString("ssl_pass_prompt"),  // $NON-NLS-1$
                             JMeterUtils.getResString("ssl_pass_title"),  // $NON-NLS-1$
                             JOptionPane.QUESTION_MESSAGE);
                     System.setProperty(KEY_STORE_PASSWORD, this.defaultpw);
                     password = this.defaultpw;
                 }
             } else {
                 log.warn("No password provided, and no GUI present so cannot prompt");
             }
         }
         return password;
     }
 
     /**
      * Opens and initializes the TrustStore.
      *
      * There are 3 possibilities:
      * - no truststore name provided, in which case the default Java truststore should be used
      * - truststore name is provided, and loads OK
      * - truststore name is provided, but is not found or does not load OK, in which case an empty
      * truststore is created
      *
      * If the KeyStore object cannot be created, then this is currently treated the same
      * as if no truststore name was provided.
      *
      * @return truststore
      * - null: use Java truststore
      * - otherwise, the truststore, which may be empty if the file could not be loaded.
      *
      */
     protected KeyStore getTrustStore() {
         if (!truststoreLoaded) {
 
             truststoreLoaded=true;// we've tried ...
 
             String fileName = System.getProperty(SSL_TRUST_STORE);
             if (fileName == null) {
                 return null;
             }
             log.info("TrustStore Location: " + fileName);
 
             try {
                 this.trustStore = KeyStore.getInstance("JKS");
                 log.info("TrustStore created OK, Type: JKS");
             } catch (Exception e) {
                 this.trustStore = null;
                 throw new RuntimeException("Problem creating truststore: "+e.getMessage(), e);
             }
 
             InputStream fileInputStream = null;
             try {
                 File initStore = new File(fileName);
 
                 if (initStore.exists()) {
                     fileInputStream = new BufferedInputStream(new FileInputStream(initStore));
                     this.trustStore.load(fileInputStream, null);
                     log.info("Truststore loaded OK from file");
                 } else {
                     log.info("Truststore file not found, loading empty truststore");
                     this.trustStore.load(null, null);
                 }
             } catch (Exception e) {
                 throw new RuntimeException("Can't load TrustStore: " + e.getMessage(), e);
             } finally {
                 JOrphanUtils.closeQuietly(fileInputStream);
             }
         }
 
         return this.trustStore;
     }
 
     /**
      * Protected Constructor to remove the possibility of directly instantiating
      * this object. Create the SSLContext, and wrap all the X509KeyManagers with
      * our X509KeyManager so that we can choose our alias.
      */
     protected SSLManager() {
     }
 
     /**
      * Static accessor for the SSLManager object. The SSLManager is a singleton.
      *
      * @return the singleton {@link SSLManager}
      */
     public static synchronized SSLManager getInstance() {
         if (null == SSLManager.manager) {
             SSLManager.manager = new JsseSSLManager(null);
         }
 
         return SSLManager.manager;
     }
 
     /**
      * Test whether SSL is supported or not.
      *
      * @return flag whether SSL is supported
      */
     public static boolean isSSLSupported() {
         return SSLManager.IS_SSL_SUPPORTED;
     }
 
     /**
      * Configure Keystore
      * 
      * @param preload
      *            flag whether the keystore should be opened within this method,
      *            or the opening should be delayed
      * @param startIndex
      *            first index to consider for a key
      * @param endIndex
      *            last index to consider for a key
      * @param clientCertAliasVarName
      *            name of the default key, if empty the first key will be used
      *            as default key
      */
     public void configureKeystore(boolean preload, int startIndex, int endIndex, String clientCertAliasVarName) {
         this.keystoreAliasStartIndex = startIndex;
         this.keystoreAliasEndIndex = endIndex;
         this.clientCertAliasVarName = clientCertAliasVarName;
         if(preload) {
             keyStore = getKeyStore();
         }
     }
 
     /**
      * Destroy Keystore
      */
     public void destroyKeystore() {
         keyStore=null;
     }
 }
diff --git a/src/core/org/apache/jmeter/util/XPathUtil.java b/src/core/org/apache/jmeter/util/XPathUtil.java
index 8c6f9ff14..93a9f61a4 100644
--- a/src/core/org/apache/jmeter/util/XPathUtil.java
+++ b/src/core/org/apache/jmeter/util/XPathUtil.java
@@ -1,477 +1,480 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintWriter;
 import java.io.StringReader;
 import java.io.StringWriter;
 import java.nio.charset.StandardCharsets;
 import java.util.List;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.OutputKeys;
 import javax.xml.transform.Source;
 import javax.xml.transform.Transformer;
 import javax.xml.transform.TransformerException;
 import javax.xml.transform.TransformerFactory;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.sax.SAXSource;
 import javax.xml.transform.stream.StreamResult;
 
 import org.apache.jmeter.assertions.AssertionResult;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
 import org.apache.xml.utils.PrefixResolver;
 import org.apache.xpath.XPathAPI;
 import org.apache.xpath.objects.XObject;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.tidy.Tidy;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 /**
  * This class provides a few utility methods for dealing with XML/XPath.
  */
 public class XPathUtil {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(XPathUtil.class);
 
     private XPathUtil() {
         super();
     }
 
     //@GuardedBy("this")
     private static DocumentBuilderFactory documentBuilderFactory;
 
     /**
      * Returns a suitable document builder factory.
      * Caches the factory in case the next caller wants the same options.
      *
      * @param validate should the parser validate documents?
      * @param whitespace should the parser eliminate whitespace in element content?
      * @param namespace should the parser be namespace aware?
      *
      * @return javax.xml.parsers.DocumentBuilderFactory
      */
     private static synchronized DocumentBuilderFactory makeDocumentBuilderFactory(boolean validate, boolean whitespace,
             boolean namespace) {
         if (XPathUtil.documentBuilderFactory == null || documentBuilderFactory.isValidating() != validate
                 || documentBuilderFactory.isNamespaceAware() != namespace
                 || documentBuilderFactory.isIgnoringElementContentWhitespace() != whitespace) {
             // configure the document builder factory
             documentBuilderFactory = DocumentBuilderFactory.newInstance();
             documentBuilderFactory.setValidating(validate);
             documentBuilderFactory.setNamespaceAware(namespace);
             documentBuilderFactory.setIgnoringElementContentWhitespace(whitespace);
         }
         return XPathUtil.documentBuilderFactory;
     }
 
     /**
      * Create a DocumentBuilder using the makeDocumentFactory func.
      *
      * @param validate should the parser validate documents?
      * @param whitespace should the parser eliminate whitespace in element content?
      * @param namespace should the parser be namespace aware?
      * @param downloadDTDs if true, parser should attempt to resolve external entities
      * @return document builder
      * @throws ParserConfigurationException if {@link DocumentBuilder} can not be created for the wanted configuration
      */
     public static DocumentBuilder makeDocumentBuilder(boolean validate, boolean whitespace, boolean namespace, boolean downloadDTDs)
             throws ParserConfigurationException {
         DocumentBuilder builder = makeDocumentBuilderFactory(validate, whitespace, namespace).newDocumentBuilder();
         builder.setErrorHandler(new MyErrorHandler(validate, false));
         if (!downloadDTDs){
             EntityResolver er = new EntityResolver(){
                 @Override
                 public InputSource resolveEntity(String publicId, String systemId)
                         throws SAXException, IOException {
                     return new InputSource(new ByteArrayInputStream(new byte[]{}));
                 }
             };
             builder.setEntityResolver(er);
         }
         return builder;
     }
 
     /**
      * Utility function to get new Document
      *
      * @param stream - Document Input stream
      * @param validate - Validate Document (not Tidy)
      * @param whitespace - Element Whitespace (not Tidy)
      * @param namespace - Is Namespace aware. (not Tidy)
      * @param tolerant - Is tolerant - i.e. use the Tidy parser
      * @param quiet - set Tidy quiet
      * @param showWarnings - set Tidy warnings
      * @param report_errors - throw TidyException if Tidy detects an error
      * @param isXml - is document already XML (Tidy only)
      * @param downloadDTDs - if true, try to download external DTDs
      * @return document
      * @throws ParserConfigurationException when no {@link DocumentBuilder} can be constructed for the wanted configuration
      * @throws SAXException if parsing fails
      * @throws IOException if an I/O error occurs while parsing
      * @throws TidyException if a ParseError is detected and <code>report_errors</code> is <code>true</code>
      */
     public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
             boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml, boolean downloadDTDs)
             throws ParserConfigurationException, SAXException, IOException, TidyException {
         return makeDocument(stream, validate, whitespace, namespace,
                 tolerant, quiet, showWarnings, report_errors, isXml, downloadDTDs, null);
     }
 
     /**
      * Utility function to get new Document
      *
      * @param stream - Document Input stream
      * @param validate - Validate Document (not Tidy)
      * @param whitespace - Element Whitespace (not Tidy)
      * @param namespace - Is Namespace aware. (not Tidy)
      * @param tolerant - Is tolerant - i.e. use the Tidy parser
      * @param quiet - set Tidy quiet
      * @param showWarnings - set Tidy warnings
      * @param report_errors - throw TidyException if Tidy detects an error
      * @param isXml - is document already XML (Tidy only)
      * @param downloadDTDs - if true, try to download external DTDs
      * @param tidyOut OutputStream for Tidy pretty-printing
      * @return document
      * @throws ParserConfigurationException if {@link DocumentBuilder} can not be created for the wanted configuration
      * @throws SAXException if parsing fails
      * @throws IOException if I/O error occurs while parsing
      * @throws TidyException if a ParseError is detected and <code>report_errors</code> is <code>true</code>
      */
     public static Document makeDocument(InputStream stream, boolean validate, boolean whitespace, boolean namespace,
             boolean tolerant, boolean quiet, boolean showWarnings, boolean report_errors, boolean isXml, boolean downloadDTDs, 
             OutputStream tidyOut)
             throws ParserConfigurationException, SAXException, IOException, TidyException {
         Document doc;
         if (tolerant) {
             doc = tidyDoc(stream, quiet, showWarnings, report_errors, isXml, tidyOut);
         } else {
             doc = makeDocumentBuilder(validate, whitespace, namespace, downloadDTDs).parse(stream);
         }
         return doc;
     }
 
     /**
      * Create a document using Tidy
      *
      * @param stream - input
      * @param quiet - set Tidy quiet?
      * @param showWarnings - show Tidy warnings?
      * @param report_errors - log errors and throw TidyException?
      * @param isXML - treat document as XML?
      * @param out OutputStream, null if no output required
      * @return the document
      *
      * @throws TidyException if a ParseError is detected and report_errors is true
      */
     private static Document tidyDoc(InputStream stream, boolean quiet, boolean showWarnings, boolean report_errors,
             boolean isXML, OutputStream out) throws TidyException {
         StringWriter sw = new StringWriter();
         Tidy tidy = makeTidyParser(quiet, showWarnings, isXML, sw);
         Document doc = tidy.parseDOM(stream, out);
         doc.normalize();
         if (tidy.getParseErrors() > 0) {
             if (report_errors) {
-                log.error("TidyException: " + sw.toString());
+                log.error("TidyException: {}", sw);
                 throw new TidyException(tidy.getParseErrors(),tidy.getParseWarnings());
             }
-            log.warn("Tidy errors: " + sw.toString());
+            log.warn("Tidy errors: {}", sw);
         }
         return doc;
     }
 
     /**
      * Create a Tidy parser with the specified settings.
      *
      * @param quiet - set the Tidy quiet flag?
      * @param showWarnings - show Tidy warnings?
      * @param isXml - treat the content as XML?
      * @param stringWriter - if non-null, use this for Tidy errorOutput
      * @return the Tidy parser
      */
     public static Tidy makeTidyParser(boolean quiet, boolean showWarnings, boolean isXml, StringWriter stringWriter) {
         Tidy tidy = new Tidy();
         tidy.setInputEncoding(StandardCharsets.UTF_8.name());
         tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
         tidy.setQuiet(quiet);
         tidy.setShowWarnings(showWarnings);
         tidy.setMakeClean(true);
         tidy.setXmlTags(isXml);
         if (stringWriter != null) {
             tidy.setErrout(new PrintWriter(stringWriter));
         }
         return tidy;
     }
 
     static class MyErrorHandler implements ErrorHandler {
         private final boolean val;
         private final boolean tol;
 
         private final String type;
 
         MyErrorHandler(boolean validate, boolean tolerate) {
             val = validate;
             tol = tolerate;
             type = "Val=" + val + " Tol=" + tol;
         }
 
         @Override
         public void warning(SAXParseException ex) throws SAXException {
-            log.info("Type=" + type + " " + ex);
+            if (log.isInfoEnabled()) {
+                log.info("Type={}. {}", type, ex.toString());
+            }
             if (val && !tol){
                 throw new SAXException(ex);
             }
         }
 
         @Override
         public void error(SAXParseException ex) throws SAXException {
-            log.warn("Type=" + type + " " + ex);
+            if (log.isWarnEnabled()) {
+                log.warn("Type={}. {}", type, ex.toString());
+            }
             if (val && !tol) {
                 throw new SAXException(ex);
             }
         }
 
         @Override
         public void fatalError(SAXParseException ex) throws SAXException {
-            log.error("Type=" + type + " " + ex);
+            log.error("Type={}. {}", type, ex.toString());
             if (val && !tol) {
                 throw new SAXException(ex);
             }
         }
     }
     
     /**
      * Return value for node
      * @param node Node
      * @return String
      */
     private static String getValueForNode(Node node) {
         StringWriter sw = new StringWriter();
         try {
             Transformer t = TransformerFactory.newInstance().newTransformer();
             t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
             t.transform(new DOMSource(node), new StreamResult(sw));
         } catch (TransformerException e) {
             sw.write(e.getMessageAndLocation());
         }
         return sw.toString();
     }
 
     /**
      * Extract NodeList using expression
      * @param document {@link Document}
      * @param xPathExpression XPath expression
      * @return {@link NodeList}
      * @throws TransformerException when the internally used xpath engine fails
      */
     public static NodeList selectNodeList(Document document, String xPathExpression) throws TransformerException {
         XObject xObject = XPathAPI.eval(document, xPathExpression, getPrefixResolver(document));
         return xObject.nodelist();
     }
 
     /**
      * Put in matchStrings results of evaluation
      * @param document XML document
      * @param xPathQuery XPath Query
      * @param matchStrings List of strings that will be filled
      * @param fragment return fragment
      * @throws TransformerException when the internally used xpath engine fails
      */
     public static void putValuesForXPathInList(Document document, 
             String xPathQuery,
             List<String> matchStrings, boolean fragment) throws TransformerException {
         putValuesForXPathInList(document, xPathQuery, matchStrings, fragment, -1);
     }
     
     
     /**
      * Put in matchStrings results of evaluation
      * @param document XML document
      * @param xPathQuery XPath Query
      * @param matchStrings List of strings that will be filled
      * @param fragment return fragment
      * @param matchNumber match number
      * @throws TransformerException when the internally used xpath engine fails
      */
     public static void putValuesForXPathInList(Document document, 
             String xPathQuery,
             List<String> matchStrings, boolean fragment, 
             int matchNumber) throws TransformerException {
         String val = null;
         XObject xObject = XPathAPI.eval(document, xPathQuery, getPrefixResolver(document));
         final int objectType = xObject.getType();
         if (objectType == XObject.CLASS_NODESET) {
             NodeList matches = xObject.nodelist();
             int length = matches.getLength();
             int indexToMatch = matchNumber;
             if(matchNumber == 0 && length>0) {
                 indexToMatch = JMeterUtils.getRandomInt(length)+1;
             } 
             for (int i = 0 ; i < length; i++) {
                 Node match = matches.item(i);
                 if(indexToMatch >= 0 && indexToMatch != (i+1)) {
                     continue;
                 }
                 if ( match instanceof Element ){
                     if (fragment){
                         val = getValueForNode(match);
                     } else {
                         // elements have empty nodeValue, but we are usually interested in their content
                         final Node firstChild = match.getFirstChild();
                         if (firstChild != null) {
                             val = firstChild.getNodeValue();
                         } else {
                             val = match.getNodeValue(); // TODO is this correct?
                         }
                     }
                 } else {
                    val = match.getNodeValue();
                 }
                 matchStrings.add(val);
             }
         } else if (objectType == XObject.CLASS_NULL
                 || objectType == XObject.CLASS_UNKNOWN
                 || objectType == XObject.CLASS_UNRESOLVEDVARIABLE) {
-            log.warn("Unexpected object type: "+xObject.getTypeString()+" returned for: "+xPathQuery);
+            if (log.isWarnEnabled()) {
+                log.warn("Unexpected object type: {} returned for: {}", xObject.getTypeString(), xPathQuery);
+            }
         } else {
             val = xObject.toString();
             matchStrings.add(val);
       }
     }
 
     /**
      * 
      * @param document XML Document
      * @return {@link PrefixResolver}
      */
     private static PrefixResolver getPrefixResolver(Document document) {
         PropertiesBasedPrefixResolver propertiesBasedPrefixResolver =
                 new PropertiesBasedPrefixResolver(document.getDocumentElement());
         return propertiesBasedPrefixResolver;
     }
 
     /**
      * Validate xpathString is a valid XPath expression
      * @param document XML Document
      * @param xpathString XPATH String
      * @throws TransformerException if expression fails to evaluate
      */
     public static void validateXPath(Document document, String xpathString) throws TransformerException {
         if (XPathAPI.eval(document, xpathString, getPrefixResolver(document)) == null) {
             // We really should never get here
             // because eval will throw an exception
             // if xpath is invalid, but whatever, better
             // safe
             throw new IllegalArgumentException("xpath eval of '" + xpathString + "' was null");
         }
     }
 
     /**
      * Fills result
      * @param result {@link AssertionResult}
      * @param doc XML Document
      * @param xPathExpression XPath expression
      * @param isNegated flag whether a non-match should be considered a success
      */
     public static void computeAssertionResult(AssertionResult result,
             Document doc, 
             String xPathExpression,
             boolean isNegated) {
         try {
             XObject xObject = XPathAPI.eval(doc, xPathExpression, getPrefixResolver(doc));
             switch (xObject.getType()) {
                 case XObject.CLASS_NODESET:
                     NodeList nodeList = xObject.nodelist();
-                    if (nodeList == null || nodeList.getLength() == 0) {
-                        if (log.isDebugEnabled()) {
-                            log.debug(new StringBuilder("nodeList null no match  ").append(xPathExpression).toString());
-                        }
+                    final int len = (nodeList != null) ? nodeList.getLength() : 0;
+                    log.debug("nodeList length {}", len);
+                    if (len == 0) {
+                        log.debug("nodeList null no match by xpath expression: {}", xPathExpression);
                         result.setFailure(!isNegated);
                         result.setFailureMessage("No Nodes Matched " + xPathExpression);
                         return;
                     }
-                    if (log.isDebugEnabled()) {
-                        log.debug("nodeList length " + nodeList.getLength());
-                        if (!isNegated) {
-                            for (int i = 0; i < nodeList.getLength(); i++){
-                                log.debug(new StringBuilder("nodeList[").append(i).append("] ").append(nodeList.item(i)).toString());
-                            }
+                    if (log.isDebugEnabled() && !isNegated) {
+                        for (int i = 0; i < len; i++) {
+                            log.debug("nodeList[{}]: {}", i, nodeList.item(i));
                         }
                     }
                     result.setFailure(isNegated);
                     if (isNegated) {
                         result.setFailureMessage("Specified XPath was found... Turn off negate if this is not desired");
                     }
                     return;
                 case XObject.CLASS_BOOLEAN:
                     if (!xObject.bool()){
                         result.setFailure(!isNegated);
                         result.setFailureMessage("No Nodes Matched " + xPathExpression);
                     }
                     return;
                 default:
                     result.setFailure(true);
                     result.setFailureMessage("Cannot understand: " + xPathExpression);
                     return;
             }
         } catch (TransformerException e) {
             result.setError(true);
             result.setFailureMessage(
                     new StringBuilder("TransformerException: ")
                     .append(e.getMessage())
                     .append(" for:")
                     .append(xPathExpression)
                     .toString());
         }
     }
     
     /**
      * Formats XML
      * @param xml string to format
      * @return String formatted XML
      */
     public static String formatXml(String xml){
         try {
             Transformer serializer= TransformerFactory.newInstance().newTransformer();
             serializer.setOutputProperty(OutputKeys.INDENT, "yes");
             serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
             Source xmlSource = new SAXSource(new InputSource(new StringReader(xml)));
             StringWriter stringWriter = new StringWriter();
             StreamResult res = new StreamResult(stringWriter);
             serializer.transform(xmlSource, res);
             return stringWriter.toString();
         } catch (Exception e) {
             return xml;
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/util/keystore/JmeterKeyStore.java b/src/core/org/apache/jmeter/util/keystore/JmeterKeyStore.java
index f676d79b3..493d705ce 100644
--- a/src/core/org/apache/jmeter/util/keystore/JmeterKeyStore.java
+++ b/src/core/org/apache/jmeter/util/keystore/JmeterKeyStore.java
@@ -1,319 +1,321 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.util.keystore;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.security.KeyStore;
 import java.security.KeyStoreException;
 import java.security.NoSuchAlgorithmException;
 import java.security.Principal;
 import java.security.PrivateKey;
 import java.security.UnrecoverableKeyException;
 import java.security.cert.Certificate;
 import java.security.cert.CertificateException;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.threads.JMeterContextService;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Use this Keystore for JMeter specific KeyStores.
  *
  */
 public final class JmeterKeyStore {
 
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JmeterKeyStore.class);
 
     private final KeyStore store;
 
     /** first index to consider for a key */
     private final int startIndex;
 
     /** last index to consider for a key */
     private final int endIndex;
 
     /** name of the default alias */
     private String clientCertAliasVarName;
 
     private String[] names = new String[0]; // default empty array to prevent NPEs
     private Map<String, PrivateKey> privateKeyByAlias = new HashMap<>();
     private Map<String, X509Certificate[]> certsByAlias = new HashMap<>();
 
     //@GuardedBy("this")
     private int last_user;
 
 
     /**
      * @param type
      *            type of the {@link KeyStore}
      * @param startIndex which keys should be considered starting from <code>0</code>
      * @param endIndex which keys should be considered up to <code>count - 1</code>
      * @param clientCertAliasVarName name for the default key, if empty use the first key available
      * @throws KeyStoreException
      *             when the type of the keystore is not supported
      * @throws IllegalArgumentException
      *             when <code>startIndex</code> &lt; 0, <code>endIndex</code>
      *             &lt; 0 or <code>endIndex</code> &lt; </code>startIndex</code>
      */
     private JmeterKeyStore(String type, int startIndex, int endIndex, String clientCertAliasVarName) throws KeyStoreException {
         if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
             throw new IllegalArgumentException("Invalid index(es). Start="+startIndex+", end="+endIndex);
         }
         this.store = KeyStore.getInstance(type);
         this.startIndex = startIndex;
         this.endIndex = endIndex;
         this.clientCertAliasVarName = clientCertAliasVarName;
     }
 
     /**
      * Process the input stream and try to read the keys from the store
      *
      * @param is
      *            {@link InputStream} from which the store should be loaded
      * @param pword
      *            the password used to check the integrity of the store
      * @throws IOException
      *             if there is a problem decoding or reading the store. A bad
      *             password might be the cause for this, or an empty store
      * @throws CertificateException
      *             if any of the certificated in the store can not be loaded
      * @throws NoSuchAlgorithmException
      *             if the algorithm to check the integrity of the store can not
      *             be found
      * @throws KeyStoreException
      *             if the store has not been initialized (should not happen
      *             here)
      * @throws UnrecoverableKeyException
      *             if the key can not be recovered from the store (should not
      *             happen here, either)
      */
     public void load(InputStream is, String pword) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
         char[] pw = pword==null ? null : pword.toCharArray();
         store.load(is, pw);
     
         List<String> v_names = new ArrayList<>();
         this.privateKeyByAlias = new HashMap<>();
         this.certsByAlias = new HashMap<>();
 
         if (null != is){ // No point checking an empty keystore
             PrivateKey _key = null;
             int index = 0;
             Enumeration<String> aliases = store.aliases();
             while (aliases.hasMoreElements()) {
                 String alias = aliases.nextElement();
                 if (store.isKeyEntry(alias)) {
                     if (index >= startIndex && index <= endIndex) {
                         _key = (PrivateKey) store.getKey(alias, pw);
                         if (null == _key) {
                             throw new IOException("No key found for alias: " + alias); // Should not happen
                         }
                         Certificate[] chain = store.getCertificateChain(alias);
                         if (null == chain) {
                             throw new IOException("No certificate chain found for alias: " + alias);
                         }
                         v_names.add(alias);
                         X509Certificate[] x509certs = new X509Certificate[chain.length];
                         for (int i = 0; i < x509certs.length; i++) {
                             x509certs[i] = (X509Certificate)chain[i];
                         }
 
                         privateKeyByAlias.put(alias, _key);
                         certsByAlias.put(alias, x509certs);
                     }
                     index++;
                 }
             }
     
             if (null == _key) {
                 throw new IOException("No key(s) found");
             }
             if (index <= endIndex-startIndex) {
-                LOG.warn("Did not find all requested aliases. Start="+startIndex
-                        +", end="+endIndex+", found="+certsByAlias.size());
+                if (log.isWarnEnabled()) {
+                    log.warn("Did not find all requested aliases. Start={}, end={}, found={}",
+                            startIndex, endIndex, certsByAlias.size());
+                }
             }
         }
     
         /*
          * Note: if is == null, the arrays will be empty
          */
         this.names = v_names.toArray(new String[v_names.size()]);
     }
 
 
     /**
      * Get the ordered certificate chain for a specific alias.
      *
      * @param alias
      *            the alias for which the certificate chain should be given
      * @return the certificate chain for the alias
      * @throws IllegalArgumentException
      *             if no chain could be found for the alias
      */
     public X509Certificate[] getCertificateChain(String alias) {
         X509Certificate[] result = this.certsByAlias.get(alias);
         if(result != null) {
             return result;
         }
         // API expects null not empty array, see http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/X509KeyManager.html
         throw new IllegalArgumentException("No certificate found for alias:'"+alias+"'");
     }
 
     /**
      * Get the next or only alias.
      * 
      * @return the next or only alias.
      * @throws IllegalArgumentException
      *             if {@link JmeterKeyStore#clientCertAliasVarName
      *             clientCertAliasVarName} is not empty and no key for this
      *             alias could be found
      */
     public String getAlias() {
         if(!StringUtils.isEmpty(clientCertAliasVarName)) {
             // We return even if result is null
             String aliasName = JMeterContextService.getContext().getVariables().get(clientCertAliasVarName);
             if(StringUtils.isEmpty(aliasName)) {
-                LOG.error("No var called '"+clientCertAliasVarName+"' found");
+                log.error("No var called '{}' found", clientCertAliasVarName);
                 throw new IllegalArgumentException("No var called '"+clientCertAliasVarName+"' found");
             }
             return aliasName;
         }
         int length = this.names.length;
         if (length == 0) { // i.e. is == null
             return null;
         }
         return this.names[getIndexAndIncrement(length)];
     }
 
     public int getAliasCount() {
         return this.names.length;
     }
 
     public String getAlias(int index) {
         int length = this.names.length;
         if (length == 0 && index == 0) { // i.e. is == null
             return null;
         }
         if (index >= length || index < 0) {
             throw new ArrayIndexOutOfBoundsException(index);
         }
         return this.names[index];
     }
 
     /**
      * Return the private Key for a specific alias
      *
      * @param alias
      *            the name of the alias for the private key
      * @return the private key for the given <code>alias</code>
      * @throws IllegalArgumentException
      *             when no private key could be found
      */
     public PrivateKey getPrivateKey(String alias) {
         PrivateKey pk = this.privateKeyByAlias.get(alias);
         if(pk != null) {
             return pk;
         }
         throw new IllegalArgumentException("No PrivateKey found for alias:'"+alias+"'");
     }
 
     /**
      * Create a keystore which returns a range of aliases (if available)
      * 
      * @param type
      *            store type (e.g. JKS)
      * @param startIndex
      *            first index (from 0)
      * @param endIndex
      *            last index (to count -1)
      * @param clientCertAliasVarName
      *            name of the default key to, if empty the first key will be
      *            used as default key
      * @return the keystore
      * @throws KeyStoreException
      *             when the type of the store is not supported
      * @throws IllegalArgumentException
      *             when <code>startIndex</code> &lt; 0, <code>endIndex</code>
      *             &lt; 0, or <code>endIndex</code> &lt; <code>startIndex</code>
      */
     public static JmeterKeyStore getInstance(String type, int startIndex, int endIndex, String clientCertAliasVarName) throws KeyStoreException  {
         return new JmeterKeyStore(type, startIndex, endIndex, clientCertAliasVarName);
     }
 
     /**
      * Create a keystore which returns the first alias only.
      * 
      * @param type
      *            of the store e.g. JKS
      * @return the keystore
      * @throws KeyStoreException
      *             when the type of the store is not supported
      */
     public static JmeterKeyStore getInstance(String type) throws KeyStoreException {
         return getInstance(type, 0, 0, null);
     }
 
     /**
      * Gets current index and increment by rolling if index is equal to length
      * @param length Number of keys to roll
      */
     private int getIndexAndIncrement(int length) {
         synchronized(this) {
             int result = last_user++;
             if (last_user >= length) {
                 last_user = 0;
             }
             return result;
         }
     }
 
     /**
      * Compiles the list of all client aliases with a private key.
      * TODO Currently, keyType and issuers are both ignored.
      *
      * @param keyType the key algorithm type name (RSA, DSA, etc.)
      * @param issuers  the CA certificates we are narrowing our selection on.
      * 
      * @return the array of aliases; may be empty
      */
     public String[] getClientAliases(String keyType, Principal[] issuers) {
         int count = getAliasCount();
         String[] aliases = new String[count];
         for(int i = 0; i < aliases.length; i++) {
 //            if (keys[i].getAlgorithm().equals(keyType)){
 //                
 //            }
             aliases[i] = this.names[i];
         }
         if(aliases.length>0) {
             return aliases;
         } else {
             // API expects null not empty array, see http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/X509KeyManager.html
             return null;
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/visualizers/gui/AbstractVisualizer.java b/src/core/org/apache/jmeter/visualizers/gui/AbstractVisualizer.java
index 84ea71579..4574eb27a 100644
--- a/src/core/org/apache/jmeter/visualizers/gui/AbstractVisualizer.java
+++ b/src/core/org/apache/jmeter/visualizers/gui/AbstractVisualizer.java
@@ -1,359 +1,359 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.visualizers.gui;
 
 import java.awt.Component;
 import java.awt.Container;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.SavePropertyDialog;
 import org.apache.jmeter.gui.UnsharedComponent;
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.reporters.AbstractListenerElement;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jorphan.gui.ComponentUtil;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This is the base class for JMeter GUI components which can display test
  * results in some way. It provides the following conveniences to developers:
  * <ul>
  * <li>Implements the
  * {@link org.apache.jmeter.gui.JMeterGUIComponent JMeterGUIComponent} interface
  * that allows your Gui visualizer to "plug-in" to the JMeter GUI environment.
  * Provides implementations for the following methods:
  * <ul>
  * <li>{@link org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement) configure(TestElement)}.
  * Any additional parameters of your Visualizer need to be handled by you.</li>
  * <li>{@link org.apache.jmeter.gui.JMeterGUIComponent#createTestElement() createTestElement()}.
  * For most purposes, the default
  * {@link org.apache.jmeter.reporters.ResultCollector ResultCollector} created
  * by this method is sufficient.</li>
  * <li>{@link org.apache.jmeter.gui.JMeterGUIComponent#getMenuCategories getMenuCategories()}.
  * To control where in the GUI your visualizer can be added.</li>
  * <li>{@link org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement) modifyTestElement(TestElement)}.
  * Again, additional parameters you require have to be handled by you.</li>
  * <li>{@link org.apache.jmeter.gui.JMeterGUIComponent#createPopupMenu() createPopupMenu()}.</li>
  * </ul>
  * </li>
  * <li>Provides convenience methods to help you make a JMeter-compatible GUI:
  * <ul>
  * <li>{@link #makeTitlePanel()}. Returns a panel that includes the name of
  * the component, and a FilePanel that allows users to control what file samples
  * are logged to.</li>
  * <li>{@link #getModel()} and {@link #setModel(ResultCollector)} methods for
  * setting and getting the model class that handles the receiving and logging of
  * sample results.</li>
  * </ul>
  * </li>
  * </ul>
  * For most developers, making a new visualizer is primarly for the purpose of
  * either calculating new statistics on the sample results that other
  * visualizers don't calculate, or displaying the results visually in a new and
  * interesting way. Making a new visualizer for either of these purposes is easy -
  * just extend this class and implement the
  * {@link org.apache.jmeter.visualizers.Visualizer#add add(SampleResult)}
  * method and display the results as you see fit. This AbstractVisualizer and
  * the default
  * {@link org.apache.jmeter.reporters.ResultCollector ResultCollector} handle
  * logging and registering to receive SampleEvents for you - all you need to do
  * is include the JPanel created by makeTitlePanel somewhere in your gui to
  * allow users set the log file.
  * <p>
  * If you are doing more than that, you may need to extend
  * {@link org.apache.jmeter.reporters.ResultCollector ResultCollector} as well
  * and modify the {@link #configure(TestElement)},
  * {@link #modifyTestElement(TestElement)}, and {@link #createTestElement()}
  * methods to create and modify your alternate ResultCollector. For an example
  * of this, see the
  * {@link org.apache.jmeter.visualizers.MailerVisualizer MailerVisualizer}.
  */
 public abstract class AbstractVisualizer
     extends AbstractListenerGui
     implements Visualizer, ChangeListener, UnsharedComponent, Clearable
     {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
     /** Logging. */
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(AbstractVisualizer.class);
     
     /** File Extensions */
     private static final String[] EXTS = { ".xml", ".jtl", ".csv" }; // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
 
     /** A panel allowing results to be saved. */
     private final FilePanel filePanel;
 
     /** A checkbox choosing whether or not only errors should be logged. */
     private final JCheckBox errorLogging;
 
     /* A checkbox choosing whether or not only successes should be logged. */
     private final JCheckBox successOnlyLogging;
 
     protected ResultCollector collector = new ResultCollector();
 
     protected boolean isStats = false;
 
     public AbstractVisualizer() {
         super();
 
         // errorLogging and successOnlyLogging are mutually exclusive
         errorLogging = new JCheckBox(JMeterUtils.getResString("log_errors_only")); // $NON-NLS-1$
         errorLogging.addActionListener(new ActionListener(){
             @Override
             public void actionPerformed(ActionEvent e) {
                 if (errorLogging.isSelected()) {
                     successOnlyLogging.setSelected(false);
                 }
             }
         });
         successOnlyLogging = new JCheckBox(JMeterUtils.getResString("log_success_only")); // $NON-NLS-1$
         successOnlyLogging.addActionListener(new ActionListener(){
             @Override
             public void actionPerformed(ActionEvent e) {
                 if (successOnlyLogging.isSelected()) {
                     errorLogging.setSelected(false);
                 }
             }
         });
         JButton saveConfigButton = new JButton(JMeterUtils.getResString("config_save_settings")); // $NON-NLS-1$
         saveConfigButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 SavePropertyDialog d = new SavePropertyDialog(
                         GuiPackage.getInstance().getMainFrame(),
                         JMeterUtils.getResString("sample_result_save_configuration"), // $NON-NLS-1$
                         true, collector.getSaveConfig());
                 d.pack();
                 ComponentUtil.centerComponentInComponent(GuiPackage.getInstance().getMainFrame(), d);
                 d.setVisible(true);
             }
         });
 
         filePanel = new FilePanel(JMeterUtils.getResString("file_visualizer_output_file"), EXTS); // $NON-NLS-1$
         filePanel.addChangeListener(this);
         filePanel.add(new JLabel(JMeterUtils.getResString("log_only"))); // $NON-NLS-1$
         filePanel.add(errorLogging);
         filePanel.add(successOnlyLogging);
         filePanel.add(saveConfigButton);
 
     }
 
     @Override
     public boolean isStats() {
         return isStats;
     }
 
     /**
      * Gets the checkbox which selects whether or not only errors should be
      * logged. Subclasses don't normally need to worry about this checkbox,
      * because it is automatically added to the GUI in {@link #makeTitlePanel()},
      * and the behavior is handled in this base class.
      *
      * @return the error logging checkbox
      */
     protected JCheckBox getErrorLoggingCheckbox() {
         return errorLogging;
     }
 
     /**
      * Provides access to the ResultCollector model class for extending
      * implementations. Using this method and setModel(ResultCollector) is only
      * necessary if your visualizer requires a differently behaving
      * ResultCollector. Using these methods will allow maximum reuse of the
      * methods provided by AbstractVisualizer in this event.
      *
      * @return the associated collector
      */
     protected ResultCollector getModel() {
         return collector;
     }
 
     /**
      * Gets the file panel which allows the user to save results to a file.
      * Subclasses don't normally need to worry about this panel, because it is
      * automatically added to the GUI in {@link #makeTitlePanel()}, and the
      * behavior is handled in this base class.
      *
      * @return the file panel allowing users to save results
      */
     protected Component getFilePanel() {
         return filePanel;
     }
 
     /**
      * Sets the filename which results will be saved to. This will set the
      * filename in the FilePanel. Subclasses don't normally need to call this
      * method, because configuration of the FilePanel is handled in this base
      * class.
      *
      * @param filename
      *            the new filename
      *
      * @see #getFilePanel()
      */
     public void setFile(String filename) {
         // TODO: Does this method need to be public? It isn't currently
         // called outside of this class.
         filePanel.setFilename(filename);
     }
 
     /**
      * Gets the filename which has been entered in the FilePanel. Subclasses
      * don't normally need to call this method, because configuration of the
      * FilePanel is handled in this base class.
      *
      * @return the current filename
      *
      * @see #getFilePanel()
      */
     public String getFile() {
         // TODO: Does this method need to be public? It isn't currently
         // called outside of this class.
         return filePanel.getFilename();
     }
 
     /**
      * Invoked when the target of the listener has changed its state. This
      * implementation assumes that the target is the FilePanel, and will update
      * the result collector for the new filename.
      *
      * @param e
      *            the event that has occurred
      */
     @Override
     public void stateChanged(ChangeEvent e) {
         log.debug("getting new collector");
         collector = (ResultCollector) createTestElement();
         collector.loadExistingFile();
     }
 
     /* Implements JMeterGUIComponent.createTestElement() */
     @Override
     public TestElement createTestElement() {
         if (collector == null) {
             collector = new ResultCollector();
         }
         modifyTestElement(collector);
         return (TestElement) collector.clone();
     }
 
     /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
     @Override
     public void modifyTestElement(TestElement c) {
         configureTestElement((AbstractListenerElement) c);
         if (c instanceof ResultCollector) {
             ResultCollector rc = (ResultCollector) c;
             rc.setErrorLogging(errorLogging.isSelected());
             rc.setSuccessOnlyLogging(successOnlyLogging.isSelected());
             rc.setFilename(getFile());
             collector = rc;
         }
     }
 
     /* Overrides AbstractJMeterGuiComponent.configure(TestElement) */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         setFile(el.getPropertyAsString(ResultCollector.FILENAME));
         ResultCollector rc = (ResultCollector) el;
         errorLogging.setSelected(rc.isErrorLogging());
         successOnlyLogging.setSelected(rc.isSuccessOnlyLogging());
         if (collector == null) {
             collector = new ResultCollector();
         }
         collector.setSaveConfig((SampleSaveConfiguration) rc.getSaveConfig().clone());
     }
 
     /**
      * This provides a convenience for extenders when they implement the
      * {@link org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()}
      * method. This method will set the name, gui class, and test class for the
      * created Test Element. It should be called by every extending class when
      * creating Test Elements, as that will best assure consistent behavior.
      *
      * @param mc
      *            the TestElement being created.
      */
     protected void configureTestElement(AbstractListenerElement mc) {
         // TODO: Should the method signature of this method be changed to
         // match the super-implementation (using a TestElement parameter
         // instead of AbstractListenerElement)? This would require an
         // instanceof check before adding the listener (below), but would
         // also make the behavior a bit more obvious for sub-classes -- the
         // Java rules dealing with this situation aren't always intuitive,
         // and a subclass may think it is calling this version of the method
         // when it is really calling the superclass version instead.
         super.configureTestElement(mc);
         mc.setListener(this);
     }
 
     /**
      * Create a standard title section for JMeter components. This includes the
      * title for the component and the Name Panel allowing the user to change
      * the name for the component. The AbstractVisualizer also adds the
      * FilePanel allowing the user to save the results, and the error logging
      * checkbox, allowing the user to choose whether or not only errors should
      * be logged.
      * <p>
      * This method is typically added to the top of the component at the
      * beginning of the component's init method.
      *
      * @return a panel containing the component title, name panel, file panel,
      *         and error logging checkbox
      */
     @Override
     protected Container makeTitlePanel() {
         Container panel = super.makeTitlePanel();
         // Note: the file panel already includes the error logging checkbox,
         // so we don't have to add it explicitly.
         panel.add(getFilePanel());
         return panel;
     }
 
     /**
      * Provides extending classes the opportunity to set the ResultCollector
      * model for the Visualizer. This is useful to allow maximum reuse of the
      * methods from AbstractVisualizer.
      *
      * @param collector {@link ResultCollector} for the visualizer
      */
     protected void setModel(ResultCollector collector) {
         this.collector = collector;
     }
 
     @Override
     public void clearGui(){
         super.clearGui();
         filePanel.clearGui();
     }
 }
