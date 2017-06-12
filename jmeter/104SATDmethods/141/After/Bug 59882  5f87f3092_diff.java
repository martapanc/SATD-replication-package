diff --git a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
index eca667457..2a350c36c 100644
--- a/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
+++ b/src/core/org/apache/jmeter/testelement/AbstractTestElement.java
@@ -1,654 +1,664 @@
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  */
 public abstract class AbstractTestElement implements TestElement, Serializable, Searchable {
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
+    
+    /**
+     * Null property are wrapped in a {@link NullProperty}
+     * This method avoids this wrapping
+     * for internal use only
+     * @since 3.1
+     */
+    private JMeterProperty getRawProperty(String key) {
+        return propMap.get(key);
+    }
 
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
-        JMeterProperty jmp = getProperty(key);
-        return jmp instanceof NullProperty ? defaultValue : jmp.getIntValue();
+        JMeterProperty jmp = getRawProperty(key);
+        return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getIntValue();
     }
 
     @Override
     public boolean getPropertyAsBoolean(String key) {
         return getProperty(key).getBooleanValue();
     }
 
     @Override
     public boolean getPropertyAsBoolean(String key, boolean defaultVal) {
-        JMeterProperty jmp = getProperty(key);
-        return jmp instanceof NullProperty ? defaultVal : jmp.getBooleanValue();
+        JMeterProperty jmp = getRawProperty(key);
+        return jmp == null || jmp instanceof NullProperty ? defaultVal : jmp.getBooleanValue();
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
-        JMeterProperty jmp = getProperty(key);
-        return jmp instanceof NullProperty ? defaultValue : jmp.getLongValue();
+        JMeterProperty jmp = getRawProperty(key);
+        return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getLongValue();
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
-        JMeterProperty jmp = getProperty(key);
-        return jmp instanceof NullProperty ? defaultValue : jmp.getStringValue();
+        JMeterProperty jmp = getRawProperty(key);
+        return jmp == null || jmp instanceof NullProperty ? defaultValue : jmp.getStringValue();
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
                 log.debug("Property " + prop.getName() + " is temp? " + isTemporary(prop) + " and is a "
                         + prop.getObjectValue());
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
             // log.warn("ThreadContext was not set up - should only happen in
             // JUnit testing..."
             // ,new Throwable("Debug"));
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
diff --git a/src/core/org/apache/jmeter/threads/JMeterThread.java b/src/core/org/apache/jmeter/threads/JMeterThread.java
index d2e29faa9..d2b479ae9 100644
--- a/src/core/org/apache/jmeter/threads/JMeterThread.java
+++ b/src/core/org/apache/jmeter/threads/JMeterThread.java
@@ -1,948 +1,952 @@
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
 import org.apache.jorphan.collections.SearchByClass;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterStopTestException;
 import org.apache.jorphan.util.JMeterStopTestNowException;
 import org.apache.jorphan.util.JMeterStopThreadException;
 import org.apache.log.Logger;
 
 /**
  * The JMeter interface to the sampling process, allowing JMeter to see the
  * timing, add listeners for sampling events and to stop the sampling process.
  *
  */
 public class JMeterThread implements Runnable, Interruptible {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     public static final String PACKAGE_OBJECT = "JMeterThread.pack"; // $NON-NLS-1$
 
     public static final String LAST_SAMPLE_OK = "JMeterThread.last_sample_ok"; // $NON-NLS-1$
 
     private static final String TRUE = Boolean.toString(true); // i.e. "true"
 
     /** How often to check for shutdown during ramp-up, default 1000ms */
     private static final int RAMPUP_GRANULARITY =
             JMeterUtils.getPropDefault("jmeterthread.rampup.granularity", 1000); // $NON-NLS-1$
 
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
         if ((delay >= 0)) {
             running = false;
             log.info("Stopping because end time detected by thread: " + threadName);
         }
     }
 
     /**
      * Wait until the scheduled start time if necessary
      *
      */
     private void startScheduler() {
         long delay = (startTime - System.currentTimeMillis());
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
                         if(log.isDebugEnabled()) {
                             if(onErrorStartNextLoop
                                     && !threadContext.isRestartNextLoop()) {
                                 log.debug("StartNextLoop option is on, Last sample failed, starting next loop");
                             }
                         }
                         
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
                     log.info("Thread is done: " + threadName);
                 }
             }
         }
         // Might be found by contoller.next()
         catch (JMeterStopTestException e) {
             log.info("Stopping Test: " + e.toString());
             stopTest();
         }
         catch (JMeterStopTestNowException e) {
             log.info("Stopping Test Now: " + e.toString());
             stopTestNow();
         } catch (JMeterStopThreadException e) {
             log.info("Stop Thread seen: " + e.toString());
         } catch (Exception e) {
             log.error("Test failed!", e);
         } catch (ThreadDeath e) {
             throw e; // Must not ignore this one
         } catch (Error e) {// Make sure errors are output to the log file
             log.error("Test failed!", e);
         } finally {
             currentSampler = null; // prevent any further interrupts
             try {
                 interruptLock.lock();  // make sure current interrupt is finished, prevent another starting yet
                 threadContext.clear();
                 log.info("Thread finished: " + threadName);
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
         } catch (JMeterStopTestException e) {
             log.info("Stopping Test: " + e.toString());
             stopTest();
         } catch (JMeterStopThreadException e) {
             log.info("Stopping Thread: " + e.toString());
             stopThread();
         } catch (Exception e) {
             if (current != null) {
                 log.error("Error while processing sampler '"+current.getName()+"' :", e);
             } else {
                 log.error("", e);
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
-        for(SampleMonitor monitor : sampleMonitors) {
-            monitor.sampleStarting(sampler);
+        if(!sampleMonitors.isEmpty()) {
+            for(SampleMonitor monitor : sampleMonitors) {
+                monitor.sampleStarting(sampler);
+            }
         }
         SampleResult result = null;
         try {
             result = sampler.sample(null); // TODO: remove this useless Entry parameter
         } finally {
-            for(SampleMonitor monitor : sampleMonitors) {
-                monitor.sampleEnded(sampler);
-            }            
+            if(!sampleMonitors.isEmpty()) {
+                for(SampleMonitor monitor : sampleMonitors) {
+                    monitor.sampleEnded(sampler);
+                }
+            }
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
         log.info("Thread started: " + Thread.currentThread().getName());
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
         }
 
         @Override
         public void processPath() {
         }
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public void stop() { // Called by StandardJMeterEngine, TestAction and AccessLogSampler
         running = false;
         log.info("Stopping: " + threadName);
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean interrupt(){
         try {
             interruptLock.lock();
             Sampler samp = currentSampler; // fetch once; must be done under lock
             if (samp instanceof Interruptible){ // (also protects against null)
                 log.warn("Interrupting: " + threadName + " sampler: " +samp.getName());
                 try {
                     boolean found = ((Interruptible)samp).interrupt();
                     if (!found) {
                         log.warn("No operation pending");
                     }
                     return found;
                 } catch (Exception e) {
                     log.warn("Caught Exception interrupting sampler: "+e.toString());
                 }
             } else if (samp != null){
                 log.warn("Sampler is not Interruptible: "+samp.getName());
             }
         } finally {
             interruptLock.unlock();            
         }
         return false;
     }
 
     private void stopTest() {
         running = false;
         log.info("Stop Test detected by thread: " + threadName);
         if (engine != null) {
             engine.askThreadsToStop();
         }
     }
 
     private void stopTestNow() {
         running = false;
         log.info("Stop Test Now detected by thread: " + threadName);
         if (engine != null) {
             engine.stopTest();
         }
     }
 
     private void stopThread() {
         running = false;
         log.info("Stop Thread detected by thread: " + threadName);
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
         } catch (Error e) {
             log.error("Error processing Assertion ",e);
             assertionResult = new AssertionResult("Assertion failed! See log file.");
             assertionResult.setError(true);
             assertionResult.setFailureMessage(e.toString());
         } catch (Exception e) {
             log.error("Exception processing Assertion ",e);
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
                 log.debug("Running preprocessor: " + ((AbstractTestElement) ex).getName());
             }
             TestBeanHelper.prepare((TestElement) ex);
             ex.process();
         }
     }
 
     private void delay(List<Timer> timers) {
         long sum = 0;
         for (Timer timer : timers) {
             TestBeanHelper.prepare((TestElement) timer);
             sum += timer.delay();
         }
         if (sum > 0) {
             try {
                 TimeUnit.MILLISECONDS.sleep(sum);
             } catch (InterruptedException e) {
                 log.warn("The delay timer was interrupted - probably did not wait as long as intended.");
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
             long now=0;
             long pause = RAMPUP_GRANULARITY;
             while(running && (now = System.currentTimeMillis()) < end) {
                 long togo = end - now;
                 if (togo < pause) {
                     pause = togo;
                 }
                 try {
                     TimeUnit.MILLISECONDS.sleep(pause); // delay between checks
                 } catch (InterruptedException e) {
                     if (running) { // Don't bother reporting stop test interruptions
                         log.warn(type+" delay for "+threadName+" was interrupted. Waited "+(now - start)+" milli-seconds out of "+delay);
                     }
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
 
 }
diff --git a/src/jorphan/org/apache/jorphan/util/JOrphanUtils.java b/src/jorphan/org/apache/jorphan/util/JOrphanUtils.java
index 73aee2708..b5f60059f 100644
--- a/src/jorphan/org/apache/jorphan/util/JOrphanUtils.java
+++ b/src/jorphan/org/apache/jorphan/util/JOrphanUtils.java
@@ -1,612 +1,619 @@
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
 
 package org.apache.jorphan.util;
 
 import java.io.Closeable;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 
 import org.apache.commons.lang3.StringUtils;
 
 /**
  * This class contains frequently-used static utility methods.
  *
  */
 
 // @see TestJorphanUtils for unit tests
 
 public final class JOrphanUtils {
 
     private static final int DEFAULT_CHUNK_SIZE = 4096;
 
     /**
      * Private constructor to prevent instantiation.
      */
     private JOrphanUtils() {
     }
 
     /**
      * This is _almost_ equivalent to the String.split method in JDK 1.4. It is
      * here to enable us to support earlier JDKs.
      *
      * Note that unlike JDK1.4 split(), it optionally ignores leading split Characters,
      * and the splitChar parameter is not a Regular expression
      *
      * <P>
      * This piece of code used to be part of JMeterUtils, but was moved here
      * because some JOrphan classes use it too.
      *
      * @param splittee
      *            String to be split
      * @param splitChar
      *            Character(s) to split the string on, these are treated as a single unit
      * @param truncate
      *            Should adjacent and leading/trailing splitChars be removed?
      *
      * @return Array of all the tokens; empty if the input string is null or the splitChar is null
      *
      * @see #split(String, String, String)
      *
      */
     public static String[] split(String splittee, String splitChar,boolean truncate) {
         if (splittee == null || splitChar == null) {
             return new String[0];
         }
         final String EMPTY_ELEMENT = "";
         int spot;
         final int splitLength = splitChar.length();
         final String adjacentSplit = splitChar + splitChar;
         final int adjacentSplitLength = adjacentSplit.length();
         if(truncate) {
             while ((spot = splittee.indexOf(adjacentSplit)) != -1) {
                 splittee = splittee.substring(0, spot + splitLength)
                         + splittee.substring(spot + adjacentSplitLength, splittee.length());
             }
             if(splittee.startsWith(splitChar)) {
                 splittee = splittee.substring(splitLength);
             }
             if(splittee.endsWith(splitChar)) { // Remove trailing splitter
                 splittee = splittee.substring(0,splittee.length()-splitLength);
             }
         }
         List<String> returns = new ArrayList<>();
         final int length = splittee.length(); // This is the new length
         int start = 0;
         spot = 0;
         while (start < length && (spot = splittee.indexOf(splitChar, start)) > -1) {
             if (spot > 0) {
                 returns.add(splittee.substring(start, spot));
             }
             else
             {
                 returns.add(EMPTY_ELEMENT);
             }
             start = spot + splitLength;
         }
         if (start < length) {
             returns.add(splittee.substring(start));
         } else if (spot == length - splitLength){// Found splitChar at end of line
             returns.add(EMPTY_ELEMENT);
         }
         return returns.toArray(new String[returns.size()]);
     }
 
     public static String[] split(String splittee,String splitChar)
     {
         return split(splittee,splitChar,true);
     }
 
     /**
      * Takes a String and a tokenizer character string, and returns a new array of
      * strings of the string split by the tokenizer character(s).
      *
      * Trailing delimiters are significant (unless the default = null)
      *
      * @param splittee
      *            String to be split.
      * @param delims
      *            Delimiter character(s) to split the string on
      * @param def
      *            Default value to place between two split chars that have
      *            nothing between them. If null, then ignore omitted elements.
      *
      * @return Array of all the tokens.
      *
      * @throws NullPointerException if splittee or delims are null
      *
      * @see #split(String, String, boolean)
      * @see #split(String, String)
      *
      * This is a rewritten version of JMeterUtils.split()
      */
     public static String[] split(String splittee, String delims, String def) {
         StringTokenizer tokens = new StringTokenizer(splittee,delims,def!=null);
         boolean lastWasDelim=false;
         List<String> strList = new ArrayList<>();
         while (tokens.hasMoreTokens()) {
             String tok=tokens.nextToken();
             if (   tok.length()==1 // we have a single character; could be a token
                 && delims.contains(tok)) // it is a token
             {
                 if (lastWasDelim) {// we saw a delimiter last time
                     strList.add(def);// so add the default
                 }
                 lastWasDelim=true;
             } else {
                 lastWasDelim=false;
                 strList.add(tok);
             }
         }
         if (lastWasDelim) {
             strList.add(def);
         }
         return strList.toArray(new String[strList.size()]);
     }
 
 
     private static final String SPACES = "                                 ";
 
     private static final int SPACES_LEN = SPACES.length();
 
     /**
      * Right aligns some text in a StringBuilder N.B. modifies the input buffer
      *
      * @param in
      *            StringBuilder containing some text
      * @param len
      *            output length desired
      * @return input StringBuilder, with leading spaces
      */
     public static StringBuilder rightAlign(StringBuilder in, int len) {
         int pfx = len - in.length();
         if (pfx <= 0) {
             return in;
         }
         if (pfx > SPACES_LEN) {
             pfx = SPACES_LEN;
         }
         in.insert(0, SPACES.substring(0, pfx));
         return in;
     }
 
     /**
      * Left aligns some text in a StringBuilder N.B. modifies the input buffer
      *
      * @param in
      *            StringBuilder containing some text
      * @param len
      *            output length desired
      * @return input StringBuilder, with trailing spaces
      */
     public static StringBuilder leftAlign(StringBuilder in, int len) {
         int sfx = len - in.length();
         if (sfx <= 0) {
             return in;
         }
         if (sfx > SPACES_LEN) {
             sfx = SPACES_LEN;
         }
         in.append(SPACES.substring(0, sfx));
         return in;
     }
 
     /**
      * Convert a boolean to its upper case string representation.
      * Equivalent to Boolean.valueOf(boolean).toString().toUpperCase().
      *
      * @param value
      *            boolean to convert
      * @return "TRUE" or "FALSE"
      */
     public static String booleanToSTRING(boolean value) {
         return value ? "TRUE" : "FALSE";
     }
 
     /**
      * Simple-minded String.replace() for JDK1.3 Should probably be recoded...
      *
      * @param source
      *            input string
      * @param search
      *            string to look for (no regular expressions)
      * @param replace
      *            string to replace the search string
      * @return the output string
      */
     public static String replaceFirst(String source, String search, String replace) {
         int start = source.indexOf(search);
         int len = search.length();
         if (start == -1) {
             return source;
         }
         if (start == 0) {
             return replace + source.substring(len);
         }
         return source.substring(0, start) + replace + source.substring(start + len);
     }
 
     /**
      * Version of String.replaceAll() for JDK1.3
      * See below for another version which replaces strings rather than chars
-     *
+     * and provides a fast path which does not allocate memory
      * @param source
      *            input string
      * @param search
      *            char to look for (no regular expressions)
      * @param replace
      *            string to replace the search string
      * @return the output string
      */
     public static String replaceAllChars(String source, char search, String replace) {
+        int indexOf = source.indexOf(search);
+        if(indexOf == -1) {
+            return source;
+        }
+        
+        int offset = 0;
         char[] chars = source.toCharArray();
         StringBuilder sb = new StringBuilder(source.length()+20);
-        for(char c : chars){
-            if (c == search){
-                sb.append(replace);
-            } else {
-                sb.append(c);
-            }
+        while(indexOf != -1) {
+            sb.append(chars, offset, indexOf-offset);
+            sb.append(replace);
+            offset = indexOf +1;
+            indexOf = source.indexOf(search, offset);
         }
+        sb.append(chars, offset, chars.length- offset);
+        
         return sb.toString();
     }
 
     /**
      * Replace all patterns in a String
      *
      * @see String#replaceAll(String regex,String replacement) - JDK1.4 only
      *
      * @param input - string to be transformed
      * @param pattern - pattern to replace
      * @param sub - replacement
      * @return the updated string
      */
     public static String substitute(final String input, final String pattern, final String sub) {
         StringBuilder ret = new StringBuilder(input.length());
         int start = 0;
         int index = -1;
         final int length = pattern.length();
         while ((index = input.indexOf(pattern, start)) >= start) {
             ret.append(input.substring(start, index));
             ret.append(sub);
             start = index + length;
         }
         ret.append(input.substring(start));
         return ret.toString();
     }
 
     /**
      * Trim a string by the tokens provided.
      *
      * @param input string to trim
      * @param delims list of delimiters
      * @return input trimmed at the first delimiter
      */
     public static String trim(final String input, final String delims){
         StringTokenizer tokens = new StringTokenizer(input,delims);
         return tokens.hasMoreTokens() ? tokens.nextToken() : "";
     }
 
     /**
      * Returns a slice of a byte array.
      *
      * TODO - add bounds checking?
      *
      * @param array -
      *            input array
      * @param begin -
      *            start of slice
      * @param end -
      *            end of slice
      * @return slice from the input array
      */
     public static byte[] getByteArraySlice(byte[] array, int begin, int end) {
         byte[] slice = new byte[(end - begin + 1)];
         System.arraycopy(array, begin, slice, 0, slice.length);
         return slice;
     }
 
     // N.B. Commons IO IOUtils has equivalent methods; these were added before IO was included
     // TODO - perhaps deprecate these in favour of Commons IO?
     /**
      * Close a Closeable with no error thrown
      * @param cl - Closeable (may be null)
      */
     public static void closeQuietly(Closeable cl){
         try {
             if (cl != null) {
                 cl.close();
             }
         } catch (IOException ignored) {
             // NOOP
         }
     }
 
     /**
      * close a Socket with no error thrown
      * @param sock - Socket (may be null)
      */
     public static void closeQuietly(Socket sock){
         try {
             if (sock!= null) {
                 sock.close();
             }
         } catch (IOException ignored) {
             // NOOP
         }
     }
 
     /**
      * close a Socket with no error thrown
      * @param sock - ServerSocket (may be null)
      */
     public static void closeQuietly(ServerSocket sock){
         try {
             if (sock!= null) {
                 sock.close();
             }
         } catch (IOException ignored) {
             // NOOP
         }
     }
 
     /**
      * Check if a byte array starts with the given byte array.
      *
      * @see String#startsWith(String, int)
      *
      * @param target array to scan
      * @param search array to search for
      * @param offset starting offset (&gt;=0)
      * @return true if the search array matches the target at the current offset
      */
     public static boolean startsWith(byte [] target, byte [] search, int offset){
         final int targetLength = target.length;
         final int searchLength = search.length;
         if (offset < 0 || searchLength > targetLength+offset){
             return false;
         }
         for(int i=0;i < searchLength; i++){
             if (target[i+offset] != search[i]){
                 return false;
             }
         }
         return true;
     }
 
     private static final byte[] XML_PFX = {'<','?','x','m','l'};// "<?xml "
 
     /**
      * Detects if some content starts with the standard XML prefix.
      *
      * @param target the content to check
      * @return true if the document starts with the standard XML prefix.
      */
     public static boolean isXML(byte [] target){
         return startsWith(target, XML_PFX,0);
     }
 
     /**
      * Convert binary byte array to hex string.
      *
      * @param ba input binary byte array
      * @return hex representation of binary input
      */
     public static String baToHexString(byte[] ba) {
         StringBuilder sb = new StringBuilder(ba.length*2);
         for (byte b : ba) {
             int j = b & 0xff;
             if (j < 16) {
                 sb.append("0"); // $NON-NLS-1$ add zero padding
             }
             sb.append(Integer.toHexString(j));
         }
         return sb.toString();
     }
 
     /**
      * Convert binary byte array to hex string.
      *
      * @param ba input binary byte array
      * @param separator the separator to be added between pairs of hex digits
      * @return hex representation of binary input
      */
     public static String baToHexString(byte[] ba, char separator) {
         StringBuilder sb = new StringBuilder(ba.length*2);
         for (int i = 0; i < ba.length; i++) {
             if (i > 0 && separator != 0) {
                 sb.append(separator);
             }
             int j = ba[i] & 0xff;
             if (j < 16) {
                 sb.append("0"); // $NON-NLS-1$ add zero padding
             }
             sb.append(Integer.toHexString(j));
         }
         return sb.toString();
     }
 
     /**
      * Convert binary byte array to hex string.
      *
      * @param ba input binary byte array
      * @return hex representation of binary input
      */
     public static byte[] baToHexBytes(byte[] ba) {
         byte[] hb = new byte[ba.length*2];
         for (int i = 0; i < ba.length; i++) {
             byte upper = (byte) ((ba[i] & 0xf0) >> 4);
             byte lower = (byte) (ba[i] & 0x0f);
             hb[2*i]=toHexChar(upper);
             hb[2*i+1]=toHexChar(lower);
         }
         return hb;
     }
 
     private static byte toHexChar(byte in){
         if (in < 10) {
             return (byte) (in+'0');
         }
         return (byte) ((in-10)+'a');
     }
 
     /**
      * Read as much as possible into buffer.
      *
      * @param is the stream to read from
      * @param buffer output buffer
      * @param offset offset into buffer
      * @param length number of bytes to read
      *
      * @return the number of bytes actually read
      * @throws IOException if some I/O errors occur
      */
     public static int read(InputStream is, byte[] buffer, int offset, int length) throws IOException {
         int remaining = length;
         while ( remaining > 0 ) {
             int location = ( length - remaining );
             int count = is.read( buffer, location, remaining );
             if ( -1 == count ) { // EOF
                 break;
             }
             remaining -= count;
         }
         return length - remaining;
     }
 
     /**
      * Display currently running threads on system.out
      * This may be expensive to run.
      * Mainly designed for use at the end of a non-GUI test to check for threads that might prevent the JVM from exiting.
      *
      * @param includeDaemons whether to include daemon threads or not.
      */
     public static void displayThreads(boolean includeDaemons) {
         Map<Thread, StackTraceElement[]> m = Thread.getAllStackTraces();
         String lineSeparator = System.getProperty("line.separator");
         StringBuilder builder = new StringBuilder();
         for(Map.Entry<Thread, StackTraceElement[]> e : m.entrySet()) {
             boolean daemon = e.getKey().isDaemon();
             if (includeDaemons || !daemon){
                 builder.setLength(0);
                 StackTraceElement[] ste = e.getValue();
                 for (StackTraceElement stackTraceElement : ste) {
                     int lineNumber = stackTraceElement.getLineNumber();
                     builder.append(stackTraceElement.getClassName()+"#"+stackTraceElement.getMethodName()+
                             (lineNumber >=0 ? " at line:"+ stackTraceElement.getLineNumber() : "")+lineSeparator);
                 }
                 System.out.println(e.getKey().toString()+((daemon ? " (daemon)" : ""))+", stackTrace:"+ builder.toString());
             }
         }
     }
 
     /**
      * Returns null if input is empty, null or contains spaces
      * @param input String
      * @return String
      */
     public static String nullifyIfEmptyTrimmed(final String input) {
         if (input == null) {
             return null;
         }
         String trimmed = input.trim();
         if (trimmed.length() == 0) {
             return null;
         }
         return trimmed;
     }
 
     /**
      * Check that value is empty (""), null or whitespace only.
      * @param value Value
      * @return true if the String is not empty (""), not null and not whitespace only.
      */
     public static boolean isBlank(final String value) {
         return StringUtils.isBlank(value);
     }
 
     /**
      * Write data to an output stream in chunks with a maximum size of 4K.
      * This is to avoid OutOfMemory issues if the data buffer is very large
      * and the JVM needs to copy the buffer for use by native code.
      *
      * @param data the buffer to be written
      * @param output the output stream to use
      * @throws IOException if there is a problem writing the data
      */
     // Bugzilla 54990
     public static void write(byte[] data, OutputStream output) throws IOException {
         int bytes = data.length;
         int offset = 0;
         while(bytes > 0) {
             int chunk = Math.min(bytes, DEFAULT_CHUNK_SIZE);
             output.write(data, offset, chunk);
             bytes -= chunk;
             offset += chunk;
         }
     }
 
     /**
      * @param elapsedSec long elapsed time in seconds
      * @return String formatted with format HH:mm:ss
      */
     @SuppressWarnings("boxing")
     public static String formatDuration(long elapsedSec) {
         return String.format("%02d:%02d:%02d",
                 elapsedSec / 3600, (elapsedSec % 3600) / 60, elapsedSec % 60);
     }
 
     /**
      * Throw {@link IllegalArgumentException} if folder cannot be written to either:
      * <ul>
      *  <li>Because it exists but is not a folder</li>
      *  <li>Because it exists but is not empty</li>
      *  <li>Because it does not exist but cannot be created</li>
      * </ul>
      * @param folder {@link File}
      * @throws IllegalArgumentException
      */
     public static void canSafelyWriteToFolder(File folder)
             throws IllegalArgumentException {
         if(folder.exists()) {
             if (folder.isFile()) {
                 throw new IllegalArgumentException("Cannot write to '"
                         +folder.getAbsolutePath()+"' as it is an existing file");
             } else {
                 if(folder.listFiles().length > 0) {
                     throw new IllegalArgumentException("Cannot write to '"
                             +folder.getAbsolutePath()+"' as folder is not empty");
                 }
             }
         } else {
             // check we can create it
             if(!folder.getAbsoluteFile().getParentFile().canWrite()) {
                 throw new IllegalArgumentException("Cannot write to '"
                         +folder.getAbsolutePath()+"' as folder does not exist and parent folder is not writable");
             }
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index 147bd1677..e5d9574ea 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,1520 +1,1526 @@
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
 
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.InetAddress;
 import java.net.URI;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.Charset;
 import java.security.PrivilegedActionException;
 import java.security.PrivilegedExceptionAction;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.TimeUnit;
 import java.util.regex.Pattern;
 
 import javax.security.auth.Subject;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.http.Header;
 import org.apache.http.HttpConnection;
 import org.apache.http.HttpConnectionMetrics;
 import org.apache.http.HttpEntity;
 import org.apache.http.HttpException;
 import org.apache.http.HttpHost;
 import org.apache.http.HttpRequest;
 import org.apache.http.HttpRequestInterceptor;
 import org.apache.http.HttpResponse;
 import org.apache.http.HttpResponseInterceptor;
 import org.apache.http.NameValuePair;
 import org.apache.http.StatusLine;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.Credentials;
 import org.apache.http.auth.NTCredentials;
 import org.apache.http.client.ClientProtocolException;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.HttpRequestRetryHandler;
 import org.apache.http.client.config.CookieSpecs;
 import org.apache.http.client.config.RequestConfig;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpHead;
 import org.apache.http.client.methods.HttpOptions;
 import org.apache.http.client.methods.HttpPatch;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.methods.HttpTrace;
 import org.apache.http.client.methods.HttpUriRequest;
 import org.apache.http.client.params.ClientPNames;
 import org.apache.http.client.protocol.HttpClientContext;
 import org.apache.http.client.protocol.ResponseContentEncoding;
 import org.apache.http.conn.ConnectionKeepAliveStrategy;
 import org.apache.http.conn.DnsResolver;
 import org.apache.http.conn.params.ConnRoutePNames;
 import org.apache.http.conn.scheme.PlainSocketFactory;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.entity.ContentType;
 import org.apache.http.entity.FileEntity;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.entity.mime.FormBodyPart;
 import org.apache.http.entity.mime.FormBodyPartBuilder;
 import org.apache.http.entity.mime.MIME;
 import org.apache.http.entity.mime.MultipartEntityBuilder;
 import org.apache.http.entity.mime.content.FileBody;
 import org.apache.http.entity.mime.content.StringBody;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
 import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
 import org.apache.http.impl.conn.SystemDefaultDnsResolver;
 import org.apache.http.message.BasicNameValuePair;
+import org.apache.http.message.BufferedHeader;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.CoreConnectionPNames;
 import org.apache.http.params.CoreProtocolPNames;
 import org.apache.http.params.DefaultedHttpParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.params.SyncBasicHttpParams;
 import org.apache.http.protocol.BasicHttpContext;
 import org.apache.http.protocol.HTTP;
 import org.apache.http.protocol.HttpContext;
 import org.apache.http.protocol.HttpCoreContext;
+import org.apache.http.util.CharArrayBuffer;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.SlowHC4SocketFactory;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.JsseSSLManager;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTP Sampler using Apache HttpClient 4.x.
  *
  */
 public class HTTPHC4Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** retry count to be used (default 0); 0 = disable retries */
     private static final int RETRY_COUNT = JMeterUtils.getPropDefault("httpclient4.retrycount", 0);
 
     /** Idle timeout to be applied to connections if no Keep-Alive header is sent by the server (default 0 = disable) */
     private static final int IDLE_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.idletimeout", 0);
     
     private static final int VALIDITY_AFTER_INACTIVITY_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.validate_after_inactivity", 2000);
     
     private static final int TIME_TO_LIVE = JMeterUtils.getPropDefault("httpclient4.time_to_live", 2000);
 
     private static final String CONTEXT_METRICS = "jmeter_metrics"; // TODO hack for metrics related to HTTPCLIENT-1081, to be removed later
     
     private static final Pattern PORT_PATTERN = Pattern.compile("^\\d+$");
 
     private static final ConnectionKeepAliveStrategy IDLE_STRATEGY = new DefaultConnectionKeepAliveStrategy(){
         @Override
         public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
             long duration = super.getKeepAliveDuration(response, context);
             if (duration <= 0 && IDLE_TIMEOUT > 0) {// none found by the superclass
                 if(log.isDebugEnabled()) {
                     log.debug("Setting keepalive to " + IDLE_TIMEOUT);
                 }
                 return IDLE_TIMEOUT;
             } 
             return duration; // return the super-class value
         }
         
     };
 
     /**
      * Special interceptor made to keep metrics when connection is released for some method like HEAD
      * Otherwise calling directly ((HttpConnection) localContext.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
      * would throw org.apache.http.impl.conn.ConnectionShutdownException
      * See <a href="https://bz.apache.org/jira/browse/HTTPCLIENT-1081">HTTPCLIENT-1081</a>
      */
     private static final HttpResponseInterceptor METRICS_SAVER = new HttpResponseInterceptor(){
         @Override
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             HttpConnectionMetrics metrics = ((HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
             context.setAttribute(CONTEXT_METRICS, metrics);
         }
     };
     private static final HttpRequestInterceptor METRICS_RESETTER = new HttpRequestInterceptor() {
         @Override
         public void process(HttpRequest request, HttpContext context)
                 throws HttpException, IOException {
             HttpConnectionMetrics metrics = ((HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION)).getMetrics();
             metrics.reset();
         }
     };
 
 
     /**
      * Headers to save
      */
     private static final String[] HEADERS_TO_SAVE = new String[]{
                     "content-length",
                     "content-encoding",
                     "content-md5"
             };
     
     /**
      * Custom implementation that backups headers related to Compressed responses 
      * that HC core {@link ResponseContentEncoding} removes after uncompressing
      * See Bug 59401
      */
     private static final HttpResponseInterceptor RESPONSE_CONTENT_ENCODING = new ResponseContentEncoding() {
         @Override
         public void process(HttpResponse response, HttpContext context)
                 throws HttpException, IOException {
             ArrayList<Header[]> headersToSave = null;
             
             final HttpEntity entity = response.getEntity();
             final HttpClientContext clientContext = HttpClientContext.adapt(context);
             final RequestConfig requestConfig = clientContext.getRequestConfig();
             // store the headers if necessary
             if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0) {
                 final Header ceheader = entity.getContentEncoding();
                 if (ceheader != null) {
                     headersToSave = new ArrayList<>(3);
                     for(String name : HEADERS_TO_SAVE) {
                         Header[] hdr = response.getHeaders(name); // empty if none
                         headersToSave.add(hdr);
                     }
                 }
             }
 
             // Now invoke original parent code
             super.process(response, clientContext);
             // Should this be in a finally ? 
             if(headersToSave != null) {
                 for (Header[] headers : headersToSave) {
                     for (Header headerToRestore : headers) {
                         if (response.containsHeader(headerToRestore.getName())) {
                             break;
                         }
                         response.addHeader(headerToRestore);
                     }
                 }
             }
         }
     };
     
     /**
      * 1 HttpClient instance per combination of (HttpClient,HttpClientKey)
      */
     private static final ThreadLocal<Map<HttpClientKey, HttpClient>> HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY = 
         new InheritableThreadLocal<Map<HttpClientKey, HttpClient>>(){
         @Override
         protected Map<HttpClientKey, HttpClient> initialValue() {
             return new HashMap<>();
         }
     };
 
     // Scheme used for slow HTTP sockets. Cannot be set as a default, because must be set on an HttpClient instance.
     private static final Scheme SLOW_HTTP;
     
     /*
      * Create a set of default parameters from the ones initially created.
      * This allows the defaults to be overridden if necessary from the properties file.
      */
     private static final HttpParams DEFAULT_HTTP_PARAMS;
 
     private static final String USER_TOKEN = "__jmeter.USER_TOKEN__"; //$NON-NLS-1$
     
     static final String SAMPLER_RESULT_TOKEN = "__jmeter.SAMPLER_RESULT__"; //$NON-NLS-1$
     
     private static final String HTTPCLIENT_TOKEN = "__jmeter.HTTPCLIENT_TOKEN__";
 
     static {
         log.info("HTTP request retry count = "+RETRY_COUNT);
 
         DEFAULT_HTTP_PARAMS = new SyncBasicHttpParams(); // Could we drop the Sync here?
         DEFAULT_HTTP_PARAMS.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
         DEFAULT_HTTP_PARAMS.setIntParameter(ClientPNames.MAX_REDIRECTS, HTTPSamplerBase.MAX_REDIRECTS);
         DefaultHttpClient.setDefaultHttpParams(DEFAULT_HTTP_PARAMS);
         
         // Process Apache HttpClient parameters file
         String file=JMeterUtils.getProperty("hc.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, DEFAULT_HTTP_PARAMS);
         }
 
         // Set up HTTP scheme override if necessary
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             SLOW_HTTP = new Scheme(HTTPConstants.PROTOCOL_HTTP, HTTPConstants.DEFAULT_HTTP_PORT, new SlowHC4SocketFactory(CPS_HTTP));
         } else {
             SLOW_HTTP = null;
         }
         
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     private volatile boolean resetSSLContext;
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
     }
 
     public static final class HttpDelete extends HttpEntityEnclosingRequestBase {
 
         public HttpDelete(final URI uri) {
             super();
             setURI(uri);
         }
 
         @Override
         public String getMethod() {
             return HTTPConstants.DELETE;
         }
     }
     
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         if (log.isDebugEnabled()) {
             log.debug("Start : sample " + url.toString());
             log.debug("method " + method+ " followingRedirect " + areFollowingRedirect + " depth " + frameDepth);            
         }
 
         HTTPSampleResult res = createSampleResult(url, method);
 
         HttpClient httpClient = setupClient(url, res);
 
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(HTTPConstants.POST)) {
                 httpRequest = new HttpPost(uri);
             } else if (method.equals(HTTPConstants.GET)) {
                 httpRequest = new HttpGet(uri);
             } else if (method.equals(HTTPConstants.PUT)) {
                 httpRequest = new HttpPut(uri);
             } else if (method.equals(HTTPConstants.HEAD)) {
                 httpRequest = new HttpHead(uri);
             } else if (method.equals(HTTPConstants.TRACE)) {
                 httpRequest = new HttpTrace(uri);
             } else if (method.equals(HTTPConstants.OPTIONS)) {
                 httpRequest = new HttpOptions(uri);
             } else if (method.equals(HTTPConstants.DELETE)) {
                 httpRequest = new HttpDelete(uri);
             } else if (method.equals(HTTPConstants.PATCH)) {
                 httpRequest = new HttpPatch(uri);
             } else if (HttpWebdav.isWebdavMethod(method)) {
                 httpRequest = new HttpWebdav(method, uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: '"+method+"'");
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             errorResult(e, res);
             return res;
         }
 
         HttpContext localContext = new BasicHttpContext();
         setupClientContextBeforeSample(localContext);
         
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                return updateSampleResultForResourceInCache(res);
            }
         }
 
         try {
             currentRequest = httpRequest;
             handleMethod(method, res, httpRequest, localContext);
             // store the SampleResult in LocalContext to compute connect time
             localContext.setAttribute(SAMPLER_RESULT_TOKEN, res);
             // perform the sample
             HttpResponse httpResponse = 
                     executeRequest(httpClient, httpRequest, localContext, url);
 
             // Needs to be done after execute to pick up all the headers
             final HttpRequest request = (HttpRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
             extractClientContextAfterSample(localContext);
             // We've finished with the request, so we can add the LocalAddress to it for display
             final InetAddress localAddr = (InetAddress) httpRequest.getParams().getParameter(ConnRoutePNames.LOCAL_ADDRESS);
             if (localAddr != null) {
                 request.addHeader(HEADER_LOCAL_ADDRESS, localAddr.toString());
             }
             res.setRequestHeaders(getConnectionHeaders(request));
 
             Header contentType = httpResponse.getLastHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             if (contentType != null){
                 String ct = contentType.getValue();
                 res.setContentType(ct);
                 res.setEncodingAndType(ct);                    
             }
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 res.setResponseData(readResponse(res, entity.getContent(), (int) entity.getContentLength()));
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse, localContext));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HTTPConstants.HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header in redirect for " + httpRequest.getRequestLine());
                 }
                 String redirectLocation = headerLocation.getValue();
                 res.setRedirectLocation(redirectLocation);
             }
 
             // record some sizes to allow HTTPSampleResult.getBytes() with different options
             HttpConnectionMetrics  metrics = (HttpConnectionMetrics) localContext.getAttribute(CONTEXT_METRICS);
             long headerBytes = 
                 res.getResponseHeaders().length()   // condensed length (without \r)
               + httpResponse.getAllHeaders().length // Add \r for each header
               + 1 // Add \r for initial header
               + 2; // final \r\n before data
             long totalBytes = metrics.getReceivedBytesCount();
             res.setHeadersSize((int) headerBytes);
             res.setBodySize((int)(totalBytes - headerBytes));
             if (log.isDebugEnabled()) {
                 log.debug("ResponseHeadersSize=" + res.getHeadersSize() + " Content-Length=" + res.getBodySize()
                         + " Total=" + (res.getHeadersSize() + res.getBodySize()));
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                 URI redirectURI = req.getURI();
                 if (redirectURI.isAbsolute()){
                     res.setURL(redirectURI.toURL());
                 } else {
                     res.setURL(new URL(new URL(target.toURI()),redirectURI.toString()));
                 }
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpResponse, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpResponse, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
         } catch (IOException e) {
             log.debug("IOException", e);
             if (res.getEndTime() == 0) {
                 res.sampleEnd();
             }
            // pick up headers if failed to execute the request
             if (res.getRequestHeaders() != null) {
                 log.debug("Overwriting request old headers: " + res.getRequestHeaders());
             }
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(HttpCoreContext.HTTP_REQUEST)));
             errorResult(e, res);
             return res;
         } catch (RuntimeException e) {
             log.debug("RuntimeException", e);
             if (res.getEndTime() == 0) {
                 res.sampleEnd();
             }
             errorResult(e, res);
             return res;
         } finally {
             currentRequest = null;
             JMeterContextService.getContext().getSamplerContext().remove(HTTPCLIENT_TOKEN);
         }
         return res;
     }
 
     /**
      * Store in JMeter Variables the UserToken so that the SSL context is reused
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=57804">Bug 57804</a>
      * @param localContext {@link HttpContext}
      */
     private void extractClientContextAfterSample(HttpContext localContext) {
         Object userToken = localContext.getAttribute(HttpClientContext.USER_TOKEN);
         if(userToken != null) {
             if(log.isDebugEnabled()) {
                 log.debug("Extracted from HttpContext user token:"+userToken+", storing it as JMeter variable:"+USER_TOKEN);
             }
             // During recording JMeterContextService.getContext().getVariables() is null
             JMeterVariables jMeterVariables = JMeterContextService.getContext().getVariables();
             if (jMeterVariables != null) {
                 jMeterVariables.putObject(USER_TOKEN, userToken); 
             }
         }
     }
 
     /**
      * Configure the UserToken so that the SSL context is reused
      * See <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=57804">Bug 57804</a>
      * @param localContext {@link HttpContext}
      */
     private void setupClientContextBeforeSample(HttpContext localContext) {
         Object userToken = null;
         // During recording JMeterContextService.getContext().getVariables() is null
         JMeterVariables jMeterVariables = JMeterContextService.getContext().getVariables();
         if(jMeterVariables != null) {
             userToken = jMeterVariables.getObject(USER_TOKEN);            
         }
         if(userToken != null) {
             if(log.isDebugEnabled()) {
                 log.debug("Found user token:"+userToken+" as JMeter variable:"+USER_TOKEN+", storing it in HttpContext");
             }
             localContext.setAttribute(HttpClientContext.USER_TOKEN, userToken);
         } else {
             // It would be better to create a ClientSessionManager that would compute this value
             // for now it can be Thread.currentThread().getName() but must be changed when we would change 
             // the Thread per User model
             String userId = Thread.currentThread().getName();
             if(log.isDebugEnabled()) {
                 log.debug("Storing in HttpContext the user token:"+userId);
             }
             localContext.setAttribute(HttpClientContext.USER_TOKEN, userId);
         }
     }
 
     /**
      * Calls {@link #sendPostData(HttpPost)} if method is <code>POST</code> and
      * {@link #sendEntityData(HttpEntityEnclosingRequestBase)} if method is
      * <code>PUT</code> or <code>PATCH</code>
      * <p>
      * Field HTTPSampleResult#queryString of result is modified in the 2 cases
      * 
      * @param method
      *            String HTTP method
      * @param result
      *            {@link HTTPSampleResult}
      * @param httpRequest
      *            {@link HttpRequestBase}
      * @param localContext
      *            {@link HttpContext}
      * @throws IOException
      *             when posting data fails due to I/O
      */
     protected void handleMethod(String method, HTTPSampleResult result,
             HttpRequestBase httpRequest, HttpContext localContext) throws IOException {
         // Handle the various methods
         if (httpRequest instanceof HttpPost) {
             String postBody = sendPostData((HttpPost)httpRequest);
             result.setQueryString(postBody);
         } else if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
             String entityBody = sendEntityData((HttpEntityEnclosingRequestBase) httpRequest);
             result.setQueryString(entityBody);
         }
     }
 
     /**
      * Create HTTPSampleResult filling url, method and SampleLabel.
      * Monitor field is computed calling isMonitor()
      * @param url URL
      * @param method HTTP Method
      * @return {@link HTTPSampleResult}
      */
     protected HTTPSampleResult createSampleResult(URL url, String method) {
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
         
         return res;
     }
 
     /**
      * Execute request either as is or under PrivilegedAction 
      * if a Subject is available for url
      * @param httpClient the {@link HttpClient} to be used to execute the httpRequest
      * @param httpRequest the {@link HttpRequest} to be executed
      * @param localContext th {@link HttpContext} to be used for execution
      * @param url the target url (will be used to look up a possible subject for the execution)
      * @return the result of the execution of the httpRequest
      * @throws IOException
      * @throws ClientProtocolException
      */
     private HttpResponse executeRequest(final HttpClient httpClient,
             final HttpRequestBase httpRequest, final HttpContext localContext, final URL url)
             throws IOException, ClientProtocolException {
         AuthManager authManager = getAuthManager();
         if (authManager != null) {
             Subject subject = authManager.getSubjectForUrl(url);
             if(subject != null) {
                 try {
                     return Subject.doAs(subject,
                             new PrivilegedExceptionAction<HttpResponse>() {
     
                                 @Override
                                 public HttpResponse run() throws Exception {
                                     return httpClient.execute(httpRequest,
                                             localContext);
                                 }
                             });
                 } catch (PrivilegedActionException e) {
                     log.error(
                             "Can't execute httpRequest with subject:"+subject,
                             e);
                     throw new RuntimeException("Can't execute httpRequest with subject:"+subject, e);
                 }
             }
         }
         return httpClient.execute(httpRequest, localContext);
     }
 
     /**
      * Holder class for all fields that define an HttpClient instance;
      * used as the key to the ThreadLocal map of HttpClient instances.
      */
     private static final class HttpClientKey {
 
         private final String target; // protocol://[user:pass@]host:[port]
         private final boolean hasProxy;
         private final String proxyHost;
         private final int proxyPort;
         private final String proxyUser;
         private final String proxyPass;
         
         private final int hashCode; // Always create hash because we will always need it
 
         /**
          * @param url URL Only protocol and url authority are used (protocol://[user:pass@]host:[port])
          * @param hasProxy has proxy
          * @param proxyHost proxy host
          * @param proxyPort proxy port
          * @param proxyUser proxy user
          * @param proxyPass proxy password
          */
         public HttpClientKey(URL url, boolean hasProxy, String proxyHost,
                 int proxyPort, String proxyUser, String proxyPass) {
             // N.B. need to separate protocol from authority otherwise http://server would match https://erver (<= sic, not typo error)
             // could use separate fields, but simpler to combine them
             this.target = url.getProtocol()+"://"+url.getAuthority();
             this.hasProxy = hasProxy;
             this.proxyHost = proxyHost;
             this.proxyPort = proxyPort;
             this.proxyUser = proxyUser;
             this.proxyPass = proxyPass;
             this.hashCode = getHash();
         }
         
         private int getHash() {
             int hash = 17;
             hash = hash*31 + (hasProxy ? 1 : 0);
             if (hasProxy) {
                 hash = hash*31 + getHash(proxyHost);
                 hash = hash*31 + proxyPort;
                 hash = hash*31 + getHash(proxyUser);
                 hash = hash*31 + getHash(proxyPass);
             }
             hash = hash*31 + target.hashCode();
             return hash;
         }
 
         // Allow for null strings
         private int getHash(String s) {
             return s == null ? 0 : s.hashCode(); 
         }
         
         @Override
         public boolean equals (Object obj){
             if (this == obj) {
                 return true;
             }
             if (!(obj instanceof HttpClientKey)) {
                 return false;
             }
             HttpClientKey other = (HttpClientKey) obj;
             if (this.hasProxy) { // otherwise proxy String fields may be null
                 return 
                 this.hasProxy == other.hasProxy &&
                 this.proxyPort == other.proxyPort &&
                 this.proxyHost.equals(other.proxyHost) &&
                 this.proxyUser.equals(other.proxyUser) &&
                 this.proxyPass.equals(other.proxyPass) &&
                 this.target.equals(other.target);
             }
             // No proxy, so don't check proxy fields
             return 
                 this.hasProxy == other.hasProxy &&
                 this.target.equals(other.target);
         }
 
         @Override
         public int hashCode(){
             return hashCode;
         }
 
         // For debugging
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(target);
             if (hasProxy) {
                 sb.append(" via ");
                 sb.append(proxyUser);
                 sb.append("@");
                 sb.append(proxyHost);
                 sb.append(":");
                 sb.append(proxyPort);
             }
             return sb.toString();
         }
     }
 
     private HttpClient setupClient(URL url, SampleResult res) {
 
         Map<HttpClientKey, HttpClient> mapHttpClientPerHttpClientKey = HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY.get();
         
         final String host = url.getHost();
         String proxyHost = getProxyHost();
         int proxyPort = getProxyPortInt();
         String proxyPass = getProxyPass();
         String proxyUser = getProxyUser();
 
         // static proxy is the globally define proxy eg command line or properties
         boolean useStaticProxy = isStaticProxy(host);
         // dynamic proxy is the proxy defined for this sampler
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
         boolean useProxy = useStaticProxy || useDynamicProxy;
         
         // if both dynamic and static are used, the dynamic proxy has priority over static
         if(!useDynamicProxy) {
             proxyHost = PROXY_HOST;
             proxyPort = PROXY_PORT;
             proxyUser = PROXY_USER;
             proxyPass = PROXY_PASS;
         }
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, useProxy, proxyHost, proxyPort, proxyUser, proxyPass);
         
         HttpClient httpClient = null;
-        if(this.testElement.isConcurrentDwn()) {
+        boolean concurrentDwn = this.testElement.isConcurrentDwn();
+        if(concurrentDwn) {
             httpClient = (HttpClient) JMeterContextService.getContext().getSamplerContext().get(HTTPCLIENT_TOKEN);
         }
         
         if (httpClient == null) {
             httpClient = mapHttpClientPerHttpClientKey.get(key);
         }
 
         if (httpClient != null && resetSSLContext && HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(url.getProtocol())) {
             ((AbstractHttpClient) httpClient).clearRequestInterceptors(); 
             ((AbstractHttpClient) httpClient).clearResponseInterceptors(); 
             httpClient.getConnectionManager().closeIdleConnections(1L, TimeUnit.MICROSECONDS);
             httpClient = null;
             JsseSSLManager sslMgr = (JsseSSLManager) SSLManager.getInstance();
             sslMgr.resetContext();
             resetSSLContext = false;
         }
 
         if (httpClient == null) { // One-time init for this client
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
 
             DnsResolver resolver = this.testElement.getDNSResolver();
             if (resolver == null) {
                 resolver = SystemDefaultDnsResolver.INSTANCE;
             }
             MeasuringConnectionManager connManager = new MeasuringConnectionManager(
                     createSchemeRegistry(), 
                     resolver, 
                     TIME_TO_LIVE,
                     VALIDITY_AFTER_INACTIVITY_TIMEOUT);
             
             // Modern browsers use more connections per host than the current httpclient default (2)
             // when using parallel download the httpclient and connection manager are shared by the downloads threads
             // to be realistic JMeter must set an higher value to DefaultMaxPerRoute
-            if(this.testElement.isConcurrentDwn()) {
+            if(concurrentDwn) {
                 try {
                     int maxConcurrentDownloads = Integer.parseInt(this.testElement.getConcurrentPool());
                     connManager.setDefaultMaxPerRoute(Math.max(maxConcurrentDownloads, connManager.getDefaultMaxPerRoute()));                
                 } catch (NumberFormatException nfe) {
                    // no need to log -> will be done by the sampler
                 }
             }
             
             httpClient = new DefaultHttpClient(connManager, clientParams) {
                 @Override
                 protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
                     return new DefaultHttpRequestRetryHandler(RETRY_COUNT, false); // set retry count
                 }
             };
             
             if (IDLE_TIMEOUT > 0) {
                 ((AbstractHttpClient) httpClient).setKeepAliveStrategy(IDLE_STRATEGY );
             }
             // see https://issues.apache.org/jira/browse/HTTPCORE-397
             ((AbstractHttpClient) httpClient).setReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE);
             ((AbstractHttpClient) httpClient).addResponseInterceptor(RESPONSE_CONTENT_ENCODING);
             ((AbstractHttpClient) httpClient).addResponseInterceptor(METRICS_SAVER); // HACK
             ((AbstractHttpClient) httpClient).addRequestInterceptor(METRICS_RESETTER); 
             
             // Override the default schemes as necessary
             SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
 
             if (SLOW_HTTP != null){
                 schemeRegistry.register(SLOW_HTTP);
             }
 
             // Set up proxy details
             if(useProxy) {
 
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 
                 if (proxyUser.length() > 0) {                   
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new NTCredentials(proxyUser, proxyPass, localHost, PROXY_DOMAIN));
                 }
             }
 
             // Bug 52126 - we do our own cookie handling
             clientParams.setParameter(ClientPNames.COOKIE_POLICY, CookieSpecs.IGNORE_COOKIES);
 
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient) + " " + key.toString());
             }
 
             mapHttpClientPerHttpClientKey.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient) + " " + key.toString());
             }
         }
 
-        if(this.testElement.isConcurrentDwn()) {
+        if(concurrentDwn) {
             JMeterContextService.getContext().getSamplerContext().put(HTTPCLIENT_TOKEN, httpClient);
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager(), key);
 
         return httpClient;
     }
 
     /**
      * Setup LazySchemeSocketFactory
      * @see "https://bz.apache.org/bugzilla/show_bug.cgi?id=58099"
      */
     private static SchemeRegistry createSchemeRegistry() {
         final SchemeRegistry registry = new SchemeRegistry();
         registry.register(
                 new Scheme("http", 80, PlainSocketFactory.getSocketFactory())); //$NON-NLS-1$
         registry.register(
                 new Scheme("https", 443, new LazySchemeSocketFactory())); //$NON-NLS-1$
         return registry;
     }
 
     /**
      * Setup following elements on httpRequest:
      * <ul>
      * <li>ConnRoutePNames.LOCAL_ADDRESS enabling IP-SPOOFING</li>
      * <li>Socket and connection timeout</li>
      * <li>Redirect handling</li>
      * <li>Keep Alive header or Connection Close</li>
      * <li>Calls setConnectionHeaders to setup headers</li>
      * <li>Calls setConnectionCookie to setup Cookie</li>
      * </ul>
      * 
      * @param url
      *            {@link URL} of the request
      * @param httpRequest
      *            http request for the request
      * @param res
      *            sample result to set cookies on
      * @throws IOException
      *             if hostname/ip to use could not be figured out
      */
     protected void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final InetAddress inetAddr = getIpSourceAddress();
     if (inetAddr != null) {// Use special field ip source address (for pseudo 'ip spoofing')
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, inetAddr);
     } else if (localAddress != null){
         requestParams.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
     } else { // reset in case was set previously
         requestParams.removeParameter(ConnRoutePNames.LOCAL_ADDRESS);
     }
 
     int rto = getResponseTimeout();
     if (rto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, rto);
     }
 
     int cto = getConnectTimeout();
     if (cto > 0){
         requestParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, cto);
     }
 
     requestParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, getAutoRedirects());
     
     // a well-behaved browser is supposed to send 'Connection: close'
     // with the last request to an HTTP server. Instead, most browsers
     // leave it to the server to close the connection after their
     // timeout period. Leave it to the JMeter user to decide.
     if (getUseKeepAlive()) {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
     } else {
         httpRequest.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
     }
 
     setConnectionHeaders(httpRequest, url, getHeaderManager(), getCacheManager());
 
     String cookies = setConnectionCookie(httpRequest, url, getCookieManager());
 
     if (res != null) {
         res.setCookies(cookies);
     }
 
 }
 
     
     /**
      * Set any default request headers to include
      *
      * @param request the HttpRequest to be used
      */
     protected void setDefaultRequestHeaders(HttpRequest request) {
      // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param response
      *            containing the headers
      * @param localContext {@link HttpContext}
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response, HttpContext localContext) {
-        StringBuilder headerBuf = new StringBuilder();
+        Header[] rh = response.getAllHeaders();
+
+        StringBuilder headerBuf = new StringBuilder(40 * (rh.length+1));
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
-        Header[] rh = response.getAllHeaders();
         for (Header responseHeader : rh) {
             writeResponseHeader(headerBuf, responseHeader);
         }
         return headerBuf.toString();
     }
 
     /**
-     * Write responseHeader to headerBuffer
+     * Write responseHeader to headerBuffer in an optimized way
      * @param headerBuffer {@link StringBuilder}
      * @param responseHeader {@link Header}
      */
-    private void writeResponseHeader(StringBuilder headerBuffer,
-            Header responseHeader) {
-        headerBuffer.append(responseHeader.getName())
+    private void writeResponseHeader(StringBuilder headerBuffer, Header responseHeader) {
+        if(responseHeader instanceof BufferedHeader) {
+            CharArrayBuffer buffer = ((BufferedHeader)responseHeader).getBuffer();
+            headerBuffer.append(buffer.buffer(), 0, buffer.length()).append("\n"); // $NON-NLS-1$;
+        }
+        else {
+            headerBuffer.append(responseHeader.getName())
             .append(": ") // $NON-NLS-1$
             .append(responseHeader.getValue())
             .append("\n"); // $NON-NLS-1$
+        }
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param request <code>HttpRequest</code> for the request
      * @param url <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     protected String setConnectionCookie(HttpRequest request, URL url, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(url);
             if (cookieHeader != null) {
                 request.setHeader(HTTPConstants.HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
     
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param request
      *            <code>HttpRequest</code> which represents the request
      * @param url
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     protected void setConnectionHeaders(HttpRequestBase request, URL url, HeaderManager headerManager, CacheManager cacheManager) {
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 for (JMeterProperty jMeterProperty : headers) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                             jMeterProperty.getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         if (HTTPConstants.HEADER_HOST.equalsIgnoreCase(n)) {
                             int port = getPortFromHostHeader(v, url.getPort());
                             v = v.replaceFirst(":\\d+$",""); // remove any port specification // $NON-NLS-1$ $NON-NLS-2$
                             if (port != -1) {
                                 if (port == url.getDefaultPort()) {
                                     port = -1; // no need to specify the port if it is the default
                                 }
                             }
                             request.getParams().setParameter(ClientPNames.VIRTUAL_HOST, new HttpHost(v, port));
                         } else {
                             request.addHeader(n, v);
                         }
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(url, request);
         }
     }
 
     /**
      * Get port from the value of the Host header, or return the given
      * defaultValue
      *
      * @param hostHeaderValue
      *            value of the http Host header
      * @param defaultValue
      *            value to be used, when no port could be extracted from
      *            hostHeaderValue
      * @return integer representing the port for the host header
      */
     private int getPortFromHostHeader(String hostHeaderValue, int defaultValue) {
         String[] hostParts = hostHeaderValue.split(":");
         if (hostParts.length > 1) {
             String portString = hostParts[hostParts.length - 1];
             if (PORT_PATTERN.matcher(portString).matches()) {
                 return Integer.parseInt(portString);
             }
         }
         return defaultValue;
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         if(method != null) {
             // Get all the request headers
-            StringBuilder hdrs = new StringBuilder(100);
+            StringBuilder hdrs = new StringBuilder(150);
             Header[] requestHeaders = method.getAllHeaders();
             for (Header requestHeader : requestHeaders) {
                 // Exclude the COOKIE header, since cookie is reported separately in the sample
                 if (!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeader.getName())) {
                     writeResponseHeader(hdrs, requestHeader);
                 }
             }
     
             return hdrs.toString();
         }
         return ""; ////$NON-NLS-1$
     }
 
     /**
      * Setup credentials for url AuthScope but keeps Proxy AuthScope credentials
      * @param client HttpClient
      * @param url URL
      * @param authManager {@link AuthManager}
      * @param key key
      */
     private void setConnectionAuthorization(HttpClient client, URL url, AuthManager authManager, HttpClientKey key) {
         CredentialsProvider credentialsProvider = 
             ((AbstractHttpClient) client).getCredentialsProvider();
         if (authManager != null) {
             if(authManager.hasAuthForURL(url)) {
                 authManager.setupCredentials(client, url, credentialsProvider, localHost);
             } else {
                 credentialsProvider.clear();
             }
         } else {
             Credentials credentials = null;
             AuthScope authScope = null;
             if(key.hasProxy && !StringUtils.isEmpty(key.proxyUser)) {
                 authScope = new AuthScope(key.proxyHost, key.proxyPort);
                 credentials = credentialsProvider.getCredentials(authScope);
             }
             credentialsProvider.clear(); 
             if(credentials != null) {
                 credentialsProvider.setCredentials(authScope, credentials);
             }
         }
     }
 
     // Helper class so we can generate request data without dumping entire file contents
     private static class ViewableFileBody extends FileBody {
         private boolean hideFileData;
         
         public ViewableFileBody(File file, String mimeType) {
             super(file, mimeType);
             hideFileData = false;
         }
 
         @Override
         public void writeTo(final OutputStream out) throws IOException {
             if (hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             } else {
                 super.writeTo(out);
             }
         }
     }
 
     // TODO needs cleaning up
     /**
      * 
      * @param post {@link HttpPost}
      * @return String posted body if computable
      * @throws IOException if sending the data fails due to I/O
      */
     protected String sendPostData(HttpPost post)  throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg[] files = getHTTPFiles();
 
         final String contentEncoding = getContentEncodingOrNull();
         final boolean haveContentEncoding = contentEncoding != null;
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             Charset charset = null;
             if(haveContentEncoding) {
                 charset = Charset.forName(contentEncoding);
             } else {
                 charset = MIME.DEFAULT_CHARSET;
             }
             
             if(log.isDebugEnabled()) {
                 log.debug("Building multipart with:getDoBrowserCompatibleMultipart():"+
                         getDoBrowserCompatibleMultipart()+
                         ", with charset:"+charset+
                         ", haveContentEncoding:"+haveContentEncoding);
             }
             // Write the request to our own stream
             MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                     .setCharset(charset);
             if(getDoBrowserCompatibleMultipart()) {
                 multipartEntityBuilder.setLaxMode();
             } else {
                 multipartEntityBuilder.setStrictMode();
             }
             // Create the parts
             // Add any parameters
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)) {
                     continue;
                 }
                 StringBody stringBody = new StringBody(arg.getValue(), ContentType.create("text/plain", charset));
                 FormBodyPart formPart = FormBodyPartBuilder.create(
                         parameterName, stringBody).build();
                 multipartEntityBuilder.addPart(formPart);
             }
 
             // Add any files
             // Cannot retrieve parts once added to the MultiPartEntity, so have to save them here.
             ViewableFileBody[] fileBodies = new ViewableFileBody[files.length];
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 
                 File reservedFile = FileServer.getFileServer().getResolvedFile(file.getPath());
                 fileBodies[i] = new ViewableFileBody(reservedFile, file.getMimeType());
                 multipartEntityBuilder.addPart(file.getParamName(), fileBodies[i] );
             }
 
             HttpEntity entity = multipartEntityBuilder.build();
             post.setEntity(entity);
 
             if (entity.isRepeatable()){
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = true;
                 }
                 entity.writeTo(bos);
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = false;
                 }
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
-                postedBody.append(new String(bos.toByteArray(),
+                postedBody.append(bos.toString(
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
             } else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
 
 //            // Set the content type TODO - needed?
 //            String multiPartContentType = multiPart.getContentType().getValue();
 //            post.setHeader(HEADER_CONTENT_TYPE, multiPartContentType);
 
         } else { // not multipart
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileEntity fileRequestEntity = new FileEntity(new File(file.getPath()),(ContentType) null);// TODO is null correct?
                 post.setEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             } else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 if(haveContentEncoding) {
                     post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, contentEncoding);
                 }
 
                 // If none of the arguments have a name specified, we
                 // just send all the values as the post body
                 if(getSendParameterValuesAsPostBody()) {
                     // Allow the mimetype of the file to control the content type
                     // This is not obvious in GUI if you are not uploading any files,
                     // but just sending the content of nameless parameters
                     // TODO: needs a multiple file upload scenerio
                     if(!hasContentTypeHeader) {
                         HTTPFileArg file = files.length > 0? files[0] : null;
                         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     for (JMeterProperty jMeterProperty : getArguments()) {
                         HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                         // Note: if "Encoded?" is not selected, arg.getEncodedValue is equivalent to arg.getValue
                         if (haveContentEncoding) {
                             postBody.append(arg.getEncodedValue(contentEncoding));
                         } else {
                             postBody.append(arg.getEncodedValue());
                         }
                     }
                     // Let StringEntity perform the encoding
                     StringEntity requestEntity = new StringEntity(postBody.toString(), contentEncoding);
                     post.setEntity(requestEntity);
                     postedBody.append(postBody.toString());
                 } else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
                     List <NameValuePair> nvps = new ArrayList<>();
                     String urlContentEncoding = contentEncoding;
                     if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                         // Use the default encoding for urls
                         urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                     }
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         // The HTTPClient always urlencodes both name and value,
                         // so if the argument is already encoded, we have to decode
                         // it before adding it to the post request
                         String parameterName = arg.getName();
                         if (arg.isSkippable(parameterName)){
                             continue;
                         }
                         String parameterValue = arg.getValue();
                         if(!arg.isAlwaysEncoded()) {
                             // The value is already encoded by the user
                             // Must decode the value now, so that when the
                             // httpclient encodes it, we end up with the same value
                             // as the user had entered.
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         nvps.add(new BasicNameValuePair(parameterName, parameterValue));
                     }
                     UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, urlContentEncoding);
                     post.setEntity(entity);
                     if (entity.isRepeatable()){
                         ByteArrayOutputStream bos = new ByteArrayOutputStream();
                         post.getEntity().writeTo(bos);
                         bos.flush();
                         // We get the posted bytes using the encoding used to create it
-                        if (contentEncoding != null) {
-                            postedBody.append(new String(bos.toByteArray(), contentEncoding));
-                        } else {
-                            postedBody.append(new String(bos.toByteArray(), SampleResult.DEFAULT_HTTP_ENCODING));
-                        }
+                        postedBody.append(bos.toString(contentEncoding != null?contentEncoding:SampleResult.DEFAULT_HTTP_ENCODING));
+                        
                         bos.close();
                     }  else {
                         postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                     }
                 }
             }
         }
         return postedBody.toString();
     }
 
     // TODO merge put and post methods as far as possible.
     // e.g. post checks for multipart form/files, and if not, invokes sendData(HttpEntityEnclosingRequestBase)
 
 
     /**
      * Creates the entity data to be sent.
      * <p>
      * If there is a file entry with a non-empty MIME type we use that to
      * set the request Content-Type header, otherwise we default to whatever
      * header is present from a Header Manager.
      * <p>
      * If the content charset {@link #getContentEncoding()} is null or empty 
      * we use the HC4 default provided by {@link HTTP#DEF_CONTENT_CHARSET} which is
      * ISO-8859-1.
      * 
      * @param entity to be processed, e.g. PUT or PATCH
      * @return the entity content, may be empty
      * @throws  UnsupportedEncodingException for invalid charset name
      * @throws IOException cannot really occur for ByteArrayOutputStream methods
      */
     protected String sendEntityData( HttpEntityEnclosingRequestBase entity) throws IOException {
         // Buffer to hold the entity body
         StringBuilder entityBody = new StringBuilder(1000);
         boolean hasEntityBody = false;
 
         final HTTPFileArg[] files = getHTTPFiles();
         // Allow the mimetype of the file to control the content type
         // This is not obvious in GUI if you are not uploading any files,
         // but just sending the content of nameless parameters
         final HTTPFileArg file = files.length > 0? files[0] : null;
         String contentTypeValue = null;
         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
             contentTypeValue = file.getMimeType();
             entity.setHeader(HEADER_CONTENT_TYPE, contentTypeValue); // we provide the MIME type here
         }
 
         // Check for local contentEncoding (charset) override; fall back to default for content body
         // we do this here rather so we can use the same charset to retrieve the data
         final String charset = getContentEncoding(HTTP.DEF_CONTENT_CHARSET.name());
 
         // Only create this if we are overriding whatever default there may be
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasEntityBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             File reservedFile = FileServer.getFileServer().getResolvedFile(files[0].getPath());
             FileEntity fileRequestEntity = new FileEntity(reservedFile); // no need for content-type here
             entity.setEntity(fileRequestEntity);
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the entity body
         else if(getSendParameterValuesAsPostBody()) {
             hasEntityBody = true;
 
             // Just append all the parameter values, and use that as the entity body
             StringBuilder entityBodyContent = new StringBuilder();
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 // Note: if "Encoded?" is not selected, arg.getEncodedValue is equivalent to arg.getValue
                 if (charset != null) {
                     entityBodyContent.append(arg.getEncodedValue(charset));
                 } else {
                     entityBodyContent.append(arg.getEncodedValue());
                 }
             }
             StringEntity requestEntity = new StringEntity(entityBodyContent.toString(), charset);
             entity.setEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasEntityBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             final HttpEntity entityEntry = entity.getEntity();
             if(entityEntry.isRepeatable()) {
                 entityBody.append("<actual file content, not shown here>");
             }
             else { // this probably cannot happen
                 entityBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
             }
         }
         return entityBody.toString(); // may be the empty string
     }
 
     /**
      * 
      * @return the value of {@link #getContentEncoding()}; forced to null if empty
      */
     private String getContentEncodingOrNull() {
         return getContentEncoding(null);
     }
 
     /**
      * @param dflt the default to be used
      * @return the value of {@link #getContentEncoding()}; default if null or empty
      */
     private String getContentEncoding(String dflt) {
         String ce = getContentEncoding();
         if (isNullOrEmptyTrimmed(ce)) {
             return dflt;
         } else {
             return ce;
         }
     }
 
     private void saveConnectionCookies(HttpResponse method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header[] hdrs = method.getHeaders(HTTPConstants.HEADER_SET_COOKIE);
             for (Header hdr : hdrs) {
                 cookieManager.addCookieFromHeader(hdr.getValue(),u);
             }
         }
     }
 
     @Override
     protected void notifyFirstSampleAfterLoopRestart() {
         log.debug("notifyFirstSampleAfterLoopRestart");
         resetSSLContext = !USE_CACHED_SSL_CONTEXT;
     }
 
     @Override
     protected void threadFinished() {
         log.debug("Thread Finished");
         closeThreadLocalConnections();
     }
 
     /**
      * 
      */
     private void closeThreadLocalConnections() {
         // Does not need to be synchronised, as all access is from same thread
         Map<HttpClientKey, HttpClient> mapHttpClientPerHttpClientKey = HTTPCLIENTS_CACHE_PER_THREAD_AND_HTTPCLIENTKEY.get();
         if ( mapHttpClientPerHttpClientKey != null ) {
             for ( HttpClient cl : mapHttpClientPerHttpClientKey.values() ) {
                 ((AbstractHttpClient) cl).clearRequestInterceptors(); 
                 ((AbstractHttpClient) cl).clearResponseInterceptors();
                 ((AbstractHttpClient) cl).close();
                 cl.getConnectionManager().shutdown();
             }
             mapHttpClientPerHttpClientKey.clear();
         }
     }
 
     @Override
     public boolean interrupt() {
         HttpUriRequest request = currentRequest;
         if (request != null) {
             currentRequest = null; // don't try twice
             try {
                 request.abort();
             } catch (UnsupportedOperationException e) {
                 log.warn("Could not abort pending request", e);
             }
         }
         return request != null;
     }
     
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
index fbc9bb75f..ef2985720 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1975 +1,2024 @@
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
  */
 package org.apache.jmeter.protocol.http.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
+import java.io.OutputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.Callable;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.Future;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.Cookie;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.DNSCacheManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.BaseParser;
 import org.apache.jmeter.protocol.http.parser.LinkExtractorParseException;
 import org.apache.jmeter.protocol.http.parser.LinkExtractorParser;
 import org.apache.jmeter.protocol.http.sampler.ResourcesDownloader.AsynSamplerResultHolder;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
+import org.apache.jmeter.protocol.http.util.DirectAccessByteArrayOutputStream;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 /**
  * Common constants and methods for HTTP samplers
  *
  */
 public abstract class HTTPSamplerBase extends AbstractSampler
     implements TestStateListener, TestIterationListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 241L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList(
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui",
                     "org.apache.jmeter.protocol.http.gui.HeaderPanel",
                     "org.apache.jmeter.protocol.http.control.DNSCacheManager",
                     "org.apache.jmeter.protocol.http.gui.DNSCachePanel",
                     "org.apache.jmeter.protocol.http.gui.AuthPanel",
                     "org.apache.jmeter.protocol.http.gui.CacheManagerGui",
                     "org.apache.jmeter.protocol.http.gui.CookiePanel"
             ));
 
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DNS_CACHE_MANAGER = "HTTPSampler.dns_cache_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     /** This is the encoding used for the content, i.e. the charset name, not the header "Content-Encoding" */
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = HTTPConstants.PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
 
     public static final String IP_SOURCE_TYPE = "HTTPSampler.ipSourceType"; // $NON-NLS-1$
 
     public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
 
     public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
 
     public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
 
     public static final String CONCURRENT_DWN = "HTTPSampler.concurrentDwn"; // $NON-NLS-1$
 
     public static final String CONCURRENT_POOL = "HTTPSampler.concurrentPool"; // $NON-NLS-1$
 
     public static final int CONCURRENT_POOL_SIZE = 6; // Default concurrent pool size for download embedded resources
 
     private static final String CONCURRENT_POOL_DEFAULT = Integer.toString(CONCURRENT_POOL_SIZE); // default for concurrent pool
 
     private static final String USER_AGENT = "User-Agent"; // $NON-NLS-1$
 
     //- JMX names
 
     public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
 
     private static final boolean IGNORE_FAILED_EMBEDDED_RESOURCES =
             JMeterUtils.getPropDefault("httpsampler.ignore_failed_embedded_resources", false); // $NON-NLS-1$ // default value: false
 
     private static final boolean IGNORE_EMBEDDED_RESOURCES_DATA =
             JMeterUtils.getPropDefault("httpsampler.embedded_resources_use_md5", false); // $NON-NLS-1$ // default value: false
 
     public enum SourceType {
         HOSTNAME("web_testing_source_ip_hostname"), //$NON-NLS-1$
         DEVICE("web_testing_source_ip_device"), //$NON-NLS-1$
         DEVICE_IPV4("web_testing_source_ip_device_ipv4"), //$NON-NLS-1$
         DEVICE_IPV6("web_testing_source_ip_device_ipv6"); //$NON-NLS-1$
 
         public final String propertyName;
         SourceType(String propertyName) {
             this.propertyName = propertyName;
         }
     }
 
     public static final int SOURCE_TYPE_DEFAULT = HTTPSamplerBase.SourceType.HOSTNAME.ordinal();
 
     // Use for ComboBox Source Address Type. Preserve order (specially with localization)
     public static String[] getSourceTypeList() {
         final SourceType[] types = SourceType.values();
         final String[] displayStrings = new String[types.length];
         for(int i = 0; i < types.length; i++) {
             displayStrings[i] = JMeterUtils.getResString(types[i].propertyName);
         }
         return displayStrings;
     }
 
     public static final String DEFAULT_METHOD = HTTPConstants.GET; // $NON-NLS-1$
 
     private static final List<String> METHODLIST;
     static {
         List<String> defaultMethods = new ArrayList<>(Arrays.asList(
             DEFAULT_METHOD, // i.e. GET
             HTTPConstants.POST,
             HTTPConstants.HEAD,
             HTTPConstants.PUT,
             HTTPConstants.OPTIONS,
             HTTPConstants.TRACE,
             HTTPConstants.DELETE,
             HTTPConstants.PATCH,
             HTTPConstants.PROPFIND,
             HTTPConstants.PROPPATCH,
             HTTPConstants.MKCOL,
             HTTPConstants.COPY,
             HTTPConstants.MOVE,
             HTTPConstants.LOCK,
             HTTPConstants.UNLOCK,
             HTTPConstants.REPORT,
             HTTPConstants.MKCALENDAR,
             HTTPConstants.SEARCH
         ));
         String userDefinedMethods = JMeterUtils.getPropDefault(
                 "httpsampler.user_defined_methods", "");
         if (StringUtils.isNotBlank(userDefinedMethods)) {
             defaultMethods.addAll(Arrays.asList(userDefinedMethods.split("\\s*,\\s*")));
         }
         METHODLIST = Collections.unmodifiableList(defaultMethods);
     }
 
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private static final String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
     // MIMETYPE is kept for backward compatibility with old test plans
     private static final String MIMETYPE = "HTTPSampler.mimetype"; // $NON-NLS-1$
     // FILE_NAME is kept for backward compatibility with old test plans
     private static final String FILE_NAME = "HTTPSampler.FILE_NAME"; // $NON-NLS-1$
     /* Shown as Parameter Name on the GUI */
     // FILE_FIELD is kept for backward compatibility with old test plans
     private static final String FILE_FIELD = "HTTPSampler.FILE_FIELD"; // $NON-NLS-1$
 
     public static final String CONTENT_TYPE = "HTTPSampler.CONTENT_TYPE"; // $NON-NLS-1$
 
     // IMAGE_PARSER now really means EMBEDDED_PARSER
     public static final String IMAGE_PARSER = "HTTPSampler.image_parser"; // $NON-NLS-1$
 
     // Embedded URLs must match this RE (if provided)
     public static final String EMBEDDED_URL_RE = "HTTPSampler.embedded_url_re"; // $NON-NLS-1$
 
     public static final String MONITOR = "HTTPSampler.monitor"; // $NON-NLS-1$
 
     // Store MD5 hash instead of storing response
     public static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 20); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> PARSERS_FOR_CONTENT_TYPE = new HashMap<>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS = // list of parsers
             JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
     static {
         String[] parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (final String parser : parsers) {
             String classname = JMeterUtils.getProperty(parser + ".className");//$NON-NLS-1$
             if (classname == null) {
                 log.error("Cannot find .className property for " + parser+", ensure you set property:'" + parser + ".className'");
                 continue;
             }
             String typeList = JMeterUtils.getProperty(parser + ".types");//$NON-NLS-1$
             if (typeList != null) {
                 String[] types = JOrphanUtils.split(typeList, " ", true);
                 for (final String type : types) {
                     log.info("Parser for " + type + " is " + classname);
                     PARSERS_FOR_CONTENT_TYPE.put(type, classname);
                 }
             } else {
                 log.warn("Cannot find .types property for " + parser
                         + ", as a consequence parser will not be used, to make it usable, define property:'"
                         + parser + ".types'");
             }
         }
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT =
             JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Content body,
      * i.e. without any additional wrapping.
      *
      * @return true if specified file is to be sent as the body,
      * i.e. there is a single file entry which has a non-empty path and
      * an empty Parameter name.
      */
     public boolean getSendFileAsPostBody() {
         // If there is one file with no parameter name, the file will
         // be sent as post body.
         HTTPFileArg[] files = getHTTPFiles();
         return (files.length == 1)
                 && (files[0].getPath().length() > 0)
                 && (files[0].getParamName().length() == 0);
     }
 
     /**
      * Determine if none of the parameters have a name, and if that
      * is the case, it means that the parameter values should be sent
      * as the entity body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         if (getPostBodyRaw()) {
             return true;
         } else {
             boolean noArgumentsHasName = true;
             for (JMeterProperty jMeterProperty : getArguments()) {
                 HTTPArgument arg = (HTTPArgument) jMeterProperty.getObjectValue();
                 if (arg.getName() != null && arg.getName().length() > 0) {
                     noArgumentsHasName = false;
                     break;
                 }
             }
             return noArgumentsHasName;
         }
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost() {
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         return HTTPConstants.POST.equals(getMethod())
                 && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()));
     }
 
     public void setProtocol(String value) {
         setProperty(PROTOCOL, value.toLowerCase(java.util.Locale.ENGLISH));
     }
 
     /**
      * Gets the protocol, with default.
      *
      * @return the protocol
      */
     public String getProtocol() {
         String protocol = getPropertyAsString(PROTOCOL);
         if (protocol == null || protocol.length() == 0) {
             return DEFAULT_PROTOCOL;
         }
         return protocol;
     }
 
     /**
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      */
     public void setPath(String path) {
         // We know that URL arguments should always be encoded in UTF-8 according to spec
         setPath(path, EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Sets the PATH property; if the request is a GET or DELETE (and the path
      * does not start with http[s]://) it also calls {@link #parseArguments(String, String)}
      * to extract and store any query arguments.
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         boolean fullUrl = path.startsWith(HTTP_PREFIX) || path.startsWith(HTTPS_PREFIX);
         boolean getOrDelete = HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod());
         if (!fullUrl && getOrDelete) {
             int index = path.indexOf(QRY_PFX);
             if (index > -1) {
                 setProperty(PATH, path.substring(0, index));
                 // Parse the arguments in querystring, assuming specified encoding for values
                 parseArguments(path.substring(index + 1), contentEncoding);
             } else {
                 setProperty(PATH, path);
             }
         } else {
             setProperty(PATH, path);
         }
     }
 
     public String getPath() {
         String p = getPropertyAsString(PATH);
         return encodeSpaces(p);
     }
 
     public void setFollowRedirects(boolean value) {
         setProperty(new BooleanProperty(FOLLOW_REDIRECTS, value));
     }
 
     public boolean getFollowRedirects() {
         return getPropertyAsBoolean(FOLLOW_REDIRECTS);
     }
 
     public void setAutoRedirects(boolean value) {
         setProperty(new BooleanProperty(AUTO_REDIRECTS, value));
     }
 
     public boolean getAutoRedirects() {
         return getPropertyAsBoolean(AUTO_REDIRECTS);
     }
 
     public void setMethod(String value) {
         setProperty(METHOD, value);
     }
 
     public String getMethod() {
         return getPropertyAsString(METHOD);
     }
 
     /**
      * Sets the value of the encoding to be used for the content.
      *
      * @param charsetName the name of the encoding to be used
      */
     public void setContentEncoding(String charsetName) {
         setProperty(CONTENT_ENCODING, charsetName);
     }
 
     /**
      *
      * @return the encoding of the content, i.e. its charset name
      */
     public String getContentEncoding() {
         return getPropertyAsString(CONTENT_ENCODING);
     }
 
     public void setUseKeepAlive(boolean value) {
         setProperty(new BooleanProperty(USE_KEEPALIVE, value));
     }
 
     public boolean getUseKeepAlive() {
         return getPropertyAsBoolean(USE_KEEPALIVE);
     }
 
     public void setDoMultipartPost(boolean value) {
         setProperty(new BooleanProperty(DO_MULTIPART_POST, value));
     }
 
     public boolean getDoMultipartPost() {
         return getPropertyAsBoolean(DO_MULTIPART_POST, false);
     }
 
     public void setDoBrowserCompatibleMultipart(boolean value) {
         setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public boolean getDoBrowserCompatibleMultipart() {
         return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
     }
 
     public void setMonitor(String value) {
         this.setProperty(MONITOR, value);
     }
 
     public void setMonitor(boolean truth) {
         this.setProperty(MONITOR, truth);
     }
 
     public String getMonitor() {
         return this.getPropertyAsString(MONITOR);
     }
 
     public boolean isMonitor() {
         return this.getPropertyAsBoolean(MONITOR);
     }
 
     public void setImplementation(String value) {
         this.setProperty(IMPLEMENTATION, value);
     }
 
     public String getImplementation() {
         return this.getPropertyAsString(IMPLEMENTATION);
     }
 
     public boolean useMD5() {
         return this.getPropertyAsBoolean(MD5, false);
     }
 
     public void setMD5(boolean truth) {
         this.setProperty(MD5, truth, false);
     }
 
     /**
      * Add an argument which has already been encoded
      *
      * @param name name of the argument
      * @param value value of the argument
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     /**
      * Creates an HTTPArgument and adds it to the current set {@link #getArguments()} of arguments.
      *
      * @param name - the parameter name
      * @param value - the parameter value
      * @param metaData - normally just '='
      * @param contentEncoding - the encoding, may be null
      */
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()) {
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if (nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         } else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded;
         if (nonEmptyEncoding) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             } catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         } else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName())
                 && arg.getValue().equals(valueEncoded)) {
             arg.setAlwaysEncoded(false);
         }
         this.getArguments().addArgument(arg);
     }
 
     public void addEncodedArgument(String name, String value, String metaData) {
         this.addEncodedArgument(name, value, metaData, null);
     }
 
     public void addNonEncodedArgument(String name, String value, String metadata) {
         HTTPArgument arg = new HTTPArgument(name, value, metadata, false);
         arg.setAlwaysEncoded(false);
         this.getArguments().addArgument(arg);
     }
 
     public void addArgument(String name, String value) {
         this.getArguments().addArgument(new HTTPArgument(name, value));
     }
 
     public void addArgument(String name, String value, String metadata) {
         this.getArguments().addArgument(new HTTPArgument(name, value, metadata));
     }
 
     public boolean hasArguments() {
         return getArguments().getArgumentCount() > 0;
     }
 
     @Override
     public void addTestElement(TestElement el) {
         if (el instanceof CookieManager) {
             setCookieManager((CookieManager) el);
         } else if (el instanceof CacheManager) {
             setCacheManager((CacheManager) el);
         } else if (el instanceof HeaderManager) {
             setHeaderManager((HeaderManager) el);
         } else if (el instanceof AuthManager) {
             setAuthManager((AuthManager) el);
         } else if (el instanceof DNSCacheManager) {
             setDNSResolver((DNSCacheManager) el);
         } else {
             super.addTestElement(el);
         }
     }
 
     /**
      * {@inheritDoc}
      * <p>
      * Clears the Header Manager property so subsequent loops don't keep merging more elements
      */
     @Override
     public void clearTestElementChildren() {
         removeProperty(HEADER_MANAGER);
     }
 
     public void setPort(int value) {
         setProperty(new IntegerProperty(PORT, value));
     }
 
     /**
      * Get the port number for a URL, applying defaults if necessary.
      * (Called by CookieManager.)
      * @param protocol from {@link URL#getProtocol()}
      * @param port number from {@link URL#getPort()}
      * @return the default port for the protocol
      */
     public static int getDefaultPort(String protocol, int port) {
         if (port == URL_UNSPECIFIED_PORT) {
             if (protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)) {
                 return HTTPConstants.DEFAULT_HTTP_PORT;
             } else if (protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
-        String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
+        String portAsString = getPropertyAsString(PORT);
+        if(portAsString == null || portAsString.isEmpty()) {
+            return UNSPECIFIED_PORT;
+        }
+        
         try {
-            return Integer.parseInt(port_s.trim());
+            return Integer.parseInt(portAsString.trim());
         } catch (NumberFormatException e) {
             return UNSPECIFIED_PORT;
         }
     }
 
     /**
      * Tell whether the default port for the specified protocol is used
      *
      * @return true if the default port number for the protocol is used, false otherwise
      */
     public boolean isProtocolDefaultPort() {
         final int port = getPortIfSpecified();
         final String protocol = getProtocol();
         boolean isDefaultHTTPPort = HTTPConstants.PROTOCOL_HTTP
                 .equalsIgnoreCase(protocol)
                 && port == HTTPConstants.DEFAULT_HTTP_PORT;
         boolean isDefaultHTTPSPort = HTTPConstants.PROTOCOL_HTTPS
                 .equalsIgnoreCase(protocol)
                 && port == HTTPConstants.DEFAULT_HTTPS_PORT;
         return port == UNSPECIFIED_PORT ||
                 isDefaultHTTPPort ||
                 isDefaultHTTPSPort;
     }
 
     /**
      * Get the port; apply the default for the protocol if necessary.
      *
      * @return the port number, with default applied if required.
      */
     public int getPort() {
         final int port = getPortIfSpecified();
         if (port == UNSPECIFIED_PORT) {
             String prot = getProtocol();
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: " + prot);
                 // TODO - should this return something else?
             }
             return HTTPConstants.DEFAULT_HTTP_PORT;
         }
         return port;
     }
 
     public void setDomain(String value) {
         setProperty(DOMAIN, value);
     }
 
     public String getDomain() {
         return getPropertyAsString(DOMAIN);
     }
 
     public void setConnectTimeout(String value) {
         setProperty(CONNECT_TIMEOUT, value, "");
     }
 
     public int getConnectTimeout() {
         return getPropertyAsInt(CONNECT_TIMEOUT, 0);
     }
 
     public void setResponseTimeout(String value) {
         setProperty(RESPONSE_TIMEOUT, value, "");
     }
 
     public int getResponseTimeout() {
         return getPropertyAsInt(RESPONSE_TIMEOUT, 0);
     }
 
     public String getProxyHost() {
         return getPropertyAsString(PROXYHOST);
     }
 
     public int getProxyPortInt() {
         return getPropertyAsInt(PROXYPORT, 0);
     }
 
     public String getProxyUser() {
         return getPropertyAsString(PROXYUSER);
     }
 
     public String getProxyPass() {
         return getPropertyAsString(PROXYPASS);
     }
 
     // gets called from ctor, so has to be final
     public final void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * @param value Boolean that indicates body will be sent as is
      */
     public void setPostBodyRaw(boolean value) {
         setProperty(POST_BODY_RAW, value, POST_BODY_RAW_DEFAULT);
     }
 
     /**
      * @return boolean that indicates body will be sent as is
      */
     public boolean getPostBodyRaw() {
         return getPropertyAsBoolean(POST_BODY_RAW, POST_BODY_RAW_DEFAULT);
     }
 
     public void setAuthManager(AuthManager value) {
         AuthManager mgr = getAuthManager();
         if (mgr != null) {
             log.warn("Existing AuthManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(AUTH_MANAGER, value));
     }
 
     public AuthManager getAuthManager() {
         return (AuthManager) getProperty(AUTH_MANAGER).getObjectValue();
     }
 
     public void setHeaderManager(HeaderManager value) {
         HeaderManager mgr = getHeaderManager();
         if (mgr != null) {
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
                 for (int i = 0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCookieManagerProperty(CookieManager value) {
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCookieManagerProperty(value);
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     // private method to allow AsyncSample to reset the value without performing checks
     private void setCacheManagerProperty(CacheManager value) {
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setCacheManagerProperty(value);
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public DNSCacheManager getDNSResolver() {
         return (DNSCacheManager) getProperty(DNS_CACHE_MANAGER).getObjectValue();
     }
 
     public void setDNSResolver(DNSCacheManager cacheManager) {
         DNSCacheManager mgr = getDNSResolver();
         if (mgr != null) {
             log.warn("Existing DNSCacheManager " + mgr.getName() + " superseded by " + cacheManager.getName());
         }
         setProperty(new TestElementProperty(DNS_CACHE_MANAGER, cacheManager));
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER, false);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(IMAGE_PARSER, parseImages, false);
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE, "");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Populates the provided HTTPSampleResult with details from the Exception.
      * Does not create a new instance, so should not be used directly to add a subsample.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult to be modified
      * @return the modified sampling result containing details of the Exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel(res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": " + e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": " + e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     // Bug 51939
     private static final boolean SEPARATE_CONTAINER =
             JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException if url is malformed
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
                 || path.startsWith(HTTPS_PREFIX)) {
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain = null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")) { // $NON-NLS-1$
                 pathAndQuery.append("/"); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if (queryString.length() > 0) {
                 if (path.contains(QRY_PFX)) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if (isProtocolDefaultPort()) {
             return new URL(protocol, domain, pathAndQuery.toString());
         }
         return new URL(protocol, domain, getPort(), pathAndQuery.toString());
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using
      * UTF-8 to encode the URL
      *
      * @return the QueryString value
      */
     public String getQueryString() {
         // We use the encoding which should be used according to the HTTP spec, which is UTF-8
         return getQueryString(EncoderCache.URL_ARGUMENT_ENCODING);
     }
 
     /**
      * Gets the QueryString attribute of the UrlConfig object, using the
      * specified encoding to encode the parameter values put into the URL
      *
      * @param contentEncoding the encoding to use for encoding parameter values
      * @return the QueryString value
      */
     public String getQueryString(String contentEncoding) {
+        
+        CollectionProperty arguments = getArguments().getArguments();
+        // Optimisation : avoid building useless objects if empty arguments
+        if(arguments.size() == 0) {
+            return "";
+        }
+        
         // Check if the sampler has a specified content encoding
         if (JOrphanUtils.isBlank(contentEncoding)) {
             // We use the encoding which should be used according to the HTTP spec, which is UTF-8
             contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
         }
-        StringBuilder buf = new StringBuilder();
-        PropertyIterator iter = getArguments().iterator();
+        
+        StringBuilder buf = new StringBuilder(arguments.size() * 15);
+        PropertyIterator iter = arguments.iterator();
         boolean first = true;
         while (iter.hasNext()) {
             HTTPArgument item = null;
             /*
              * N.B. Revision 323346 introduced the ClassCast check, but then used iter.next()
              * to fetch the item to be cast, thus skipping the element that did not cast.
              * Reverted to work more like the original code, but with the check in place.
              * Added a warning message so can track whether it is necessary
              */
             Object objectValue = iter.next().getObjectValue();
             try {
                 item = (HTTPArgument) objectValue;
             } catch (ClassCastException e) {
                 log.warn("Unexpected argument type: " + objectValue.getClass().getName());
                 item = new HTTPArgument((Argument) objectValue);
             }
             final String encodedName = item.getEncodedName();
             if (encodedName.length() == 0) {
                 continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
             }
             if (!first) {
                 buf.append(QRY_SEP);
             } else {
                 first = false;
             }
             buf.append(encodedName);
             if (item.getMetaData() == null) {
                 buf.append(ARG_VAL_SEP);
             } else {
                 buf.append(item.getMetaData());
             }
 
             // Encode the parameter value in the specified content encoding
             try {
                 buf.append(item.getEncodedValue(contentEncoding));
             } catch(UnsupportedEncodingException e) {
                 log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
             }
         }
         return buf.toString();
     }
 
     // Mark Walsh 2002-08-03, modified to also parse a parameter name value
     // string, where string contains only the parameter name and no equal sign.
     /**
      * This method allows a proxy server to send over the raw text from a
      * browser's output stream to be parsed and stored correctly into the
      * UrlConfig object.
      *
      * For each name found, addArgument() is called
      *
      * @param queryString -
      *            the query string, might be the post body of a http post request.
      * @param contentEncoding -
      *            the content encoding of the query string;
      *            if non-null then it is used to decode the
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         final boolean isDebug = log.isDebugEnabled();
         for (String arg : args) {
             if (isDebug) {
                 log.debug("Arg: " + arg);
             }
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existence of an equal sign
             String name;
             String value;
             int length = arg.length();
             int endOfNameIndex = arg.indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = arg.substring(0, endOfNameIndex);
                 value = arg.substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name = arg;
                 value = "";
             }
             if (name.length() > 0) {
                 if (isDebug) {
                     log.debug("Name: " + name + " Value: " + value + " Metadata: " + metaData);
                 }
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if (!StringUtils.isEmpty(contentEncoding)) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 } else {
                     // If we do not know the encoding, we just use the encoded value
                     // The browser has already done the encoding, so save the values as is
                     addNonEncodedArgument(name, value, metaData);
                 }
             }
         }
     }
 
     public void parseArguments(String queryString) {
         // We do not know the content encoding of the query string
         parseArguments(queryString, null);
     }
 
     @Override
     public String toString() {
         try {
             StringBuilder stringBuffer = new StringBuilder();
             stringBuffer.append(this.getUrl().toString());
             // Append body if it is a post or put
             if (HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
                 stringBuffer.append("\nQuery Data: ");
                 stringBuffer.append(getQueryString());
             }
             return stringBuffer.toString();
         } catch (MalformedURLException e) {
             return "";
         }
     }
 
     /**
      * Do a sampling and return its results.
      *
      * @param e
      *            <code>Entry</code> to be sampled
      * @return results of the sampling
      */
     @Override
     public SampleResult sample(Entry e) {
         return sample();
     }
 
     /**
      * Perform a sample, and return the results
      *
      * @return results of the sampling
      */
     public SampleResult sample() {
         SampleResult res = null;
         try {
             res = sample(getUrl(), getMethod(), false, 0);
             if (res != null) {
                 res.setSampleLabel(getName());
             }
             return res;
         } catch (Exception e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param u
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param depth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling, can be null if u is in CacheManager
      */
     protected abstract HTTPSampleResult sample(URL u,
             String method, boolean areFollowingRedirect, int depth);
 
     /**
      * Download the resources of an HTML page.
      *
      * @param res
      *            result of the initial request - must contain an HTML response
      * @param container
      *            for storing the results, if any
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return res if no resources exist, otherwise the "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult downloadPageResources(HTTPSampleResult res, HTTPSampleResult container, int frameDepth) {
         Iterator<URL> urls = null;
         try {
             final byte[] responseData = res.getResponseData();
             if (responseData.length > 0) {  // Bug 39205
                 final LinkExtractorParser parser = getParser(res);
                 if (parser != null) {
                     String userAgent = getUserAgent(res);
                     urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                 }
             }
         } catch (LinkExtractorParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, new HTTPSampleResult(res)));
             setParentSampleSuccess(res, false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re = getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length() > 0) {
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: " + e.getMessage());
                 }
             }
 
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> list = new ArrayList<>();
 
             int maxConcurrentDownloads = CONCURRENT_POOL_SIZE; // init with default value
             boolean isConcurrentDwn = isConcurrentDwn();
             if (isConcurrentDwn) {
                 try {
                     maxConcurrentDownloads = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
 
                 // if the user choose a number of parallel downloads of 1
                 // no need to use another thread, do the sample on the current thread
                 if (maxConcurrentDownloads == 1) {
                     log.warn("Number of parallel downloads set to 1, (sampler name=" + getName()+")");
                     isConcurrentDwn = false;
                 }
             }
 
             while (urls.hasNext()) {
                 Object binURL = urls.next(); // See catch clause below
                 try {
                     URL url = (URL) binURL;
                     if (url == null) {
                         log.warn("Null URL detected (should not happen)");
                     } else {
                         String urlstr = url.toString();
                         String urlStrEnc = escapeIllegalURLCharacters(encodeSpaces(urlstr));
                         if (!urlstr.equals(urlStrEnc)) {// There were some spaces in the URL
                             try {
                                 url = new URL(urlStrEnc);
                             } catch (MalformedURLException e) {
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), new HTTPSampleResult(res)));
                                 setParentSampleSuccess(res, false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         try {
                             url = url.toURI().normalize().toURL();
                         } catch (MalformedURLException | URISyntaxException e) {
                             res.addSubResult(errorResult(new Exception(urlStrEnc + " URI can not be normalized", e), new HTTPSampleResult(res)));
                             setParentSampleSuccess(res, false);
                             continue;
                         }
 
                         if (isConcurrentDwn) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             list.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes == null || binRes.isSuccessful()));
                         }
 
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));
                     setParentSampleSuccess(res, false);
                 }
             }
 
             // IF for download concurrent embedded resources
             if (isConcurrentDwn && !list.isEmpty()) {
 
                 ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
 
                 try {
                     // sample all resources
                     final List<Future<AsynSamplerResultHolder>> retExec =
                             resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
                     CookieManager cookieManager = getCookieManager();
                     // add result to main sampleResult
                     for (Future<AsynSamplerResultHolder> future : retExec) {
                         // this call will not block as the futures return by invokeAllAndAwaitTermination
                         //   are either done or cancelled
                         AsynSamplerResultHolder binRes = future.get();
                         if (cookieManager != null) {
                             CollectionProperty cookies = binRes.getCookies();
                             for (JMeterProperty jMeterProperty : cookies) {
                                 Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
                                 cookieManager.add(cookie);
                             }
                         }
                         res.addSubResult(binRes.getResult());
                         setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                     }
                 } catch (InterruptedException ie) {
                     log.warn("Interrupted fetching embedded resources", ie); // $NON-NLS-1$
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
                 }
             }
         }
         return res;
     }
 
     /**
      * Gets parser from {@link HTTPSampleResult#getMediaType()}.
      * Returns null if no parser defined for it
      * @param res {@link HTTPSampleResult}
      * @return {@link LinkExtractorParser}
      * @throws LinkExtractorParseException
      */
     private LinkExtractorParser getParser(HTTPSampleResult res)
             throws LinkExtractorParseException {
         String parserClassName =
                 PARSERS_FOR_CONTENT_TYPE.get(res.getMediaType());
         if (!StringUtils.isEmpty(parserClassName)) {
             return BaseParser.getParser(parserClassName);
         }
         return null;
     }
 
     /**
      * @param url URL to escape
      * @return escaped url
      */
     private String escapeIllegalURLCharacters(String url) {
         if (url == null || url.toLowerCase().startsWith("file:")) {
             return url;
         }
         try {
             String escapedUrl = ConversionUtils.escapeIllegalURLCharacters(url);
             if (!escapedUrl.equals(url) && log.isDebugEnabled()) {
                 log.debug("Url '" + url + "' has been escaped to '"
                         + escapedUrl + "'. Please correct your webpage.");
             }
             return escapedUrl;
         } catch (Exception e1) {
             log.error("Error escaping URL:'" + url + "', message:" + e1.getMessage());
             return url;
         }
     }
 
     /**
      * Extract User-Agent header value
      * @param sampleResult HTTPSampleResult
      * @return User Agent part
      */
     private String getUserAgent(HTTPSampleResult sampleResult) {
         String res = sampleResult.getRequestHeaders();
         int index = res.indexOf(USER_AGENT);
         if (index >= 0) {
             // see HTTPHC3Impl#getConnectionHeaders
             // see HTTPHC4Impl#getConnectionHeaders
             // see HTTPJavaImpl#getConnectionHeaders
             //': ' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
             final String userAgentPrefix = USER_AGENT+": ";
             String userAgentHdr = res.substring(
                     index+userAgentPrefix.length(),
                     res.indexOf('\n',// '\n' is used by JMeter to fill-in requestHeaders, see getConnectionHeaders
                             index+userAgentPrefix.length()+1));
             return userAgentHdr.trim();
         } else {
             if (log.isInfoEnabled()) {
                 log.info("No user agent extracted from requestHeaders:" + res);
             }
             return null;
         }
     }
 
     /**
      * Set parent successful attribute based on IGNORE_FAILED_EMBEDDED_RESOURCES parameter
      * @param res {@link HTTPSampleResult}
      * @param initialValue boolean
      */
     private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
         if (!IGNORE_FAILED_EMBEDDED_RESOURCES) {
             res.setSuccessful(initialValue);
             if (!initialValue) {
                 StringBuilder detailedMessage = new StringBuilder(80);
                 detailedMessage.append("Embedded resource download error:"); //$NON-NLS-1$
                 for (SampleResult subResult : res.getSubResults()) {
                     HTTPSampleResult httpSampleResult = (HTTPSampleResult) subResult;
                     if (!httpSampleResult.isSuccessful()) {
                         detailedMessage.append(httpSampleResult.getURL())
                                 .append(" code:") //$NON-NLS-1$
                                 .append(httpSampleResult.getResponseCode())
                                 .append(" message:") //$NON-NLS-1$
                                 .append(httpSampleResult.getResponseMessage())
                                 .append(", "); //$NON-NLS-1$
                     }
                 }
                 res.setResponseMessage(detailedMessage.toString()); //$NON-NLS-1$
             }
         }
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
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
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         return base;
     }
 
     /**
      * Iteratively download the redirect targets of a redirect response.
      * <p>
      * The returned result will contain one subsample for each request issued,
      * including the original one that was passed in. It will be an
      * HTTPSampleResult that should mostly look as if the final destination of
      * the redirect chain had been obtained in a single shot.
      *
      * @param res
      *            result of the initial request - must be a redirect response
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return "Container" result with one subsample per request issued
      */
     protected HTTPSampleResult followRedirects(HTTPSampleResult res, int frameDepth) {
         HTTPSampleResult totalRes = new HTTPSampleResult(res);
         totalRes.addRawSubResult(res);
         HTTPSampleResult lastRes = res;
 
         int redirect;
         for (redirect = 0; redirect < MAX_REDIRECTS; redirect++) {
             boolean invalidRedirectUrl = false;
             String location = lastRes.getRedirectLocation();
             if (log.isDebugEnabled()) {
                 log.debug("Initial location: " + location);
             }
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             location = encodeSpaces(location);
             if (log.isDebugEnabled()) {
                 log.debug("Location after /. and space transforms: " + location);
             }
             // Change all but HEAD into GET (Bug 55450)
             String method = lastRes.getHTTPMethod();
             method = computeMethodForRedirect(method, res.getResponseCode());
 
             try {
                 URL url = ConversionUtils.makeRelativeURL(lastRes.getURL(), location);
                 url = ConversionUtils.sanitizeUrl(url).toURL();
                 if (log.isDebugEnabled()) {
                     log.debug("Location as URL: " + url.toString());
                 }
                 HTTPSampleResult tempRes = sample(url, method, true, frameDepth);
                 if (tempRes != null) {
                     lastRes = tempRes;
                 } else {
                     // Last url was in cache so tempRes is null
                     break;
                 }
             } catch (MalformedURLException | URISyntaxException e) {
                 errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (SampleResult sub : subs) {
                     totalRes.addSubResult(sub);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if (!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeded maximum number of redirects: " + MAX_REDIRECTS), new HTTPSampleResult(lastRes));
             totalRes.addSubResult(lastRes);
         }
 
         // Now populate the any totalRes fields that need to
         // come from lastRes:
         totalRes.setSampleLabel(totalRes.getSampleLabel() + "->" + lastRes.getSampleLabel());
         // The following three can be discussed: should they be from the
         // first request or from the final one? I chose to do it this way
         // because that's what browsers do: they show the final URL of the
         // redirect chain in the location field.
         totalRes.setURL(lastRes.getURL());
         totalRes.setHTTPMethod(lastRes.getHTTPMethod());
         totalRes.setQueryString(lastRes.getQueryString());
         totalRes.setRequestHeaders(lastRes.getRequestHeaders());
 
         totalRes.setResponseData(lastRes.getResponseData());
         totalRes.setResponseCode(lastRes.getResponseCode());
         totalRes.setSuccessful(lastRes.isSuccessful());
         totalRes.setResponseMessage(lastRes.getResponseMessage());
         totalRes.setDataType(lastRes.getDataType());
         totalRes.setResponseHeaders(lastRes.getResponseHeaders());
         totalRes.setContentType(lastRes.getContentType());
         totalRes.setDataEncoding(lastRes.getDataEncodingNoDefault());
         return totalRes;
     }
 
     /**
      * See <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC2616#section-10.3</a>
      * JMeter conforms currently to HttpClient 4.5.2 supported RFC
      * TODO Update when migrating to HttpClient 5.X
      * @param initialMethod the initial HTTP Method
      * @param responseCode String response code
      * @return the new HTTP Method as per RFC
      */
     private String computeMethodForRedirect(String initialMethod, String responseCode) {
         if (!HTTPConstants.HEAD.equalsIgnoreCase(initialMethod)) {
             return HTTPConstants.GET;
         }
         return initialMethod;
     }
 
     /**
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect flag whether we are getting a redirect target
      * @param frameDepth Depth of this target in the frame structure. Used only to prevent infinite recursion.
      * @param res sample result to process
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect && res.isRedirect()) {
-            log.debug("Location set to - " + res.getRedirectLocation());
+            if(log.isDebugEnabled()) {
+                log.debug("Location set to - " + res.getRedirectLocation());
+            }
 
             if (getFollowRedirects()) {
                 res = followRedirects(res, frameDepth);
                 areFollowingRedirect = true;
                 wasRedirected = true;
             }
         }
-        if (isImageParser() && (SampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
+        
+        if (res.isSuccessful() && SampleResult.TEXT.equals(res.getDataType()) && isImageParser() ) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 HTTPSampleResult errSubResult = new HTTPSampleResult(res);
                 errSubResult.removeSubResults();
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), errSubResult));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 // otherwise, use null so the container is created if necessary unless
                 // the flag is false, in which case revert to broken 2.1 behaviour
                 // Bug 51939 -  https://bz.apache.org/bugzilla/show_bug.cgi?id=51939
                 if (!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (
                             areFollowingRedirect ? res.getParent() : SEPARATE_CONTAINER ? null : res);
                     res = downloadPageResources(res, container, frameDepth);
                 }
             }
         }
         return res;
     }
 
     /**
      * Determine if the HTTP status code is successful or not
      * i.e. in range 200 to 399 inclusive
      *
      * @param code status code to check
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code) {
         return code >= 200 && code <= 399;
     }
 
     protected static String encodeBackSlashes(String value) {
         StringBuilder newValue = new StringBuilder();
         for (int i = 0; i < value.length(); i++) {
             char charAt = value.charAt(i);
             if (charAt == '\\') { // $NON-NLS-1$
                 newValue.append("\\\\"); // $NON-NLS-1$
             } else {
                 newValue.append(charAt);
             }
         }
         return newValue.toString();
     }
 
     /*
      * Method to set files list to be uploaded.
      *
      * @param value
      *   HTTPFileArgs object that stores file list to be uploaded.
      */
     private void setHTTPFileArgs(HTTPFileArgs value) {
         if (value.getHTTPFileArgCount() > 0) {
             setProperty(new TestElementProperty(FILE_ARGS, value));
         } else {
             removeProperty(FILE_ARGS); // no point saving an empty list
         }
     }
 
     /*
      * Method to get files list to be uploaded.
      */
     private HTTPFileArgs getHTTPFileArgs() {
         return (HTTPFileArgs) getProperty(FILE_ARGS).getObjectValue();
     }
 
     /**
      * Get the collection of files as a list.
      * The list is built up from the filename/filefield/mimetype properties,
      * plus any additional entries saved in the FILE_ARGS property.
      *
      * If there are no valid file entries, then an empty list is returned.
      *
      * @return an array of file arguments (never null)
      */
     public HTTPFileArg[] getHTTPFiles() {
         final HTTPFileArgs fileArgs = getHTTPFileArgs();
         return fileArgs == null ? new HTTPFileArg[] {} : fileArgs.asArray();
     }
 
     public int getHTTPFileCount() {
         return getHTTPFiles().length;
     }
     /**
      * Saves the list of files.
      * The first file is saved in the Filename/field/mimetype properties.
      * Any additional files are saved in the FILE_ARGS array.
      *
      * @param files list of files to save
      */
     public void setHTTPFiles(HTTPFileArg[] files) {
         HTTPFileArgs fileArgs = new HTTPFileArgs();
         // Weed out the empty files
         if (files.length > 0) {
             for (HTTPFileArg file : files) {
                 if (file.isNotEmpty()) {
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray() {
         return METHODLIST.toArray(new String[METHODLIST.size()]);
     }
 
     public static boolean isSecure(String protocol) {
         return HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url) {
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     @Override
     public void threadStarted() {
     }
 
     @Override
     public void threadFinished() {
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         // NOOP to provide based empty impl and avoid breaking existing implementations
     }
 
     /**
      * Read response from the input stream, converting to MD5 digest if the useMD5 property is set.
      * <p>
      * For the MD5 case, the result byte count is set to the size of the original response.
      * <p>
      * Closes the inputStream
      *
      * @param sampleResult sample to store information about the response into
      * @param in input stream from which to read the response
      * @param length expected input length or zero
      * @return the response or the MD5 of the response
      * @throws IOException if reading the result fails
      */
     public byte[] readResponse(SampleResult sampleResult, InputStream in, int length) throws IOException {
+        
+        OutputStream w = null;
         try {
             byte[] readBuffer = new byte[8192]; // 8kB is the (max) size to have the latency ('the first packet')
             int bufferSize = 32;// Enough for MD5
 
             MessageDigest md = null;
-            boolean asMD5 = useMD5();
-            if (asMD5) {
+            boolean knownResponseLength = length > 0;// may also happen if long value > int.max
+            if (useMD5()) {
                 try {
                     md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
                 } catch (NoSuchAlgorithmException e) {
                     log.error("Should not happen - could not find MD5 digest", e);
-                    asMD5 = false;
                 }
             } else {
-                if (length <= 0) {// may also happen if long value > int.max
+                if (!knownResponseLength) {
                     bufferSize = 4 * 1024;
                 } else {
                     bufferSize = length;
                 }
             }
-            ByteArrayOutputStream w = new ByteArrayOutputStream(bufferSize);
+            
+            
             int bytesRead = 0;
             int totalBytes = 0;
             boolean first = true;
             while ((bytesRead = in.read(readBuffer)) > -1) {
                 if (first) {
                     sampleResult.latencyEnd();
                     first = false;
+                    if(md == null) {
+                        if(knownResponseLength) {
+                            w = new DirectAccessByteArrayOutputStream(bufferSize);
+                        }
+                        else {
+                            w = new org.apache.commons.io.output.ByteArrayOutputStream(bufferSize);
+                        }
+                    }
                 }
-                if (asMD5 && md != null) {
+                
+                if (md == null) {
+                    w.write(readBuffer, 0, bytesRead);
+                } else {
                     md.update(readBuffer, 0, bytesRead);
                     totalBytes += bytesRead;
-                } else {
-                    w.write(readBuffer, 0, bytesRead);
                 }
             }
+            
             if (first) { // Bug 46838 - if there was no data, still need to set latency
                 sampleResult.latencyEnd();
+                return new byte[0];
             }
-            in.close();
-            w.flush();
-            if (asMD5 && md != null) {
+            
+            if (md != null) {
                 byte[] md5Result = md.digest();
-                w.write(JOrphanUtils.baToHexBytes(md5Result));
                 sampleResult.setBytes(totalBytes);
+                return JOrphanUtils.baToHexBytes(md5Result);
             }
-            w.close();
-            return w.toByteArray();
+            
+            return toByteArray(w);
         } finally {
             IOUtils.closeQuietly(in);
+            IOUtils.closeQuietly(w);
         }
     }
 
     /**
+     * Optimized method to get byte array from {@link OutputStream}
+     * @param w {@link OutputStream}
+     * @return byte array
+     */
+    private byte[] toByteArray(OutputStream w) {
+        if(w instanceof DirectAccessByteArrayOutputStream) {
+            return ((DirectAccessByteArrayOutputStream) w).toByteArray();
+        }
+        
+        if(w instanceof org.apache.commons.io.output.ByteArrayOutputStream) {
+            return ((org.apache.commons.io.output.ByteArrayOutputStream) w).toByteArray();
+        }
+        
+        log.warn("Unknown stream type " + w.getClass());
+        
+        return null;
+    }
+
+    /**
      * JMeter 2.3.1 and earlier only had fields for one file on the GUI:
      * <ul>
      *   <li>FILE_NAME</li>
      *   <li>FILE_FIELD</li>
      *   <li>MIMETYPE</li>
      * </ul>
      * These were stored in their own individual properties.
      * <p>
      * Version 2.3.3 introduced a list of files, each with their own path, name and mimetype.
      * <p>
      * In order to maintain backwards compatibility of test plans, the 3 original properties
      * were retained; additional file entries are stored in an HTTPFileArgs class.
      * The HTTPFileArgs class was only present if there is more than 1 file; this means that
      * such test plans are backward compatible.
      * <p>
      * Versions after 2.3.4 dispense with the original set of 3 properties.
      * Test plans that use them are converted to use a single HTTPFileArgs list.
      *
      * @see HTTPSamplerBaseConverter
      */
     void mergeFileProperties() {
         JMeterProperty fileName = getProperty(FILE_NAME);
         JMeterProperty paramName = getProperty(FILE_FIELD);
         JMeterProperty mimeType = getProperty(MIMETYPE);
         HTTPFileArg oldStyleFile = new HTTPFileArg(fileName, paramName, mimeType);
 
         HTTPFileArgs fileArgs = getHTTPFileArgs();
 
         HTTPFileArgs allFileArgs = new HTTPFileArgs();
         if (oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if (fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (HTTPFileArg infile : infiles) {
                     allFileArgs.addHTTPFileArg(infile);
                 }
             }
         } else {
             if (fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
                 allFileArgs = fileArgs;
             }
         }
         // Updated the property lists
         setHTTPFileArgs(allFileArgs);
         removeProperty(FILE_FIELD);
         removeProperty(FILE_NAME);
         removeProperty(MIMETYPE);
     }
 
     /**
      * set IP source to use - does not apply to Java HTTP implementation currently
      *
      * @param value IP source to use
      */
     public void setIpSource(String value) {
         setProperty(IP_SOURCE, value, "");
     }
 
     /**
      * get IP source to use - does not apply to Java HTTP implementation currently
      *
      * @return IP source to use
      */
     public String getIpSource() {
         return getPropertyAsString(IP_SOURCE, "");
     }
 
     /**
      * set IP/address source type to use
      *
      * @param value type of the IP/address source
      */
     public void setIpSourceType(int value) {
         setProperty(IP_SOURCE_TYPE, value, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * get IP/address source type to use
      *
      * @return address source type
      */
     public int getIpSourceType() {
         return getPropertyAsInt(IP_SOURCE_TYPE, SOURCE_TYPE_DEFAULT);
     }
 
     /**
      * Return if used a concurrent thread pool to get embedded resources.
      *
      * @return true if used
      */
     public boolean isConcurrentDwn() {
         return getPropertyAsBoolean(CONCURRENT_DWN, false);
     }
 
     public void setConcurrentDwn(boolean concurrentDwn) {
         setProperty(CONCURRENT_DWN, concurrentDwn, false);
     }
 
     /**
      * Get the pool size for concurrent thread pool to get embedded resources.
      *
      * @return the pool size
      */
     public String getConcurrentPool() {
         return getPropertyAsString(CONCURRENT_POOL, CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
 
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     private static class ASyncSample implements Callable<AsynSamplerResultHolder> {
         private final URL url;
         private final String method;
         private final boolean areFollowingRedirect;
         private final int depth;
         private final HTTPSamplerBase sampler;
         private final JMeterContext jmeterContextOfParentThread;
 
         ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth,  CookieManager cookieManager, HTTPSamplerBase base) {
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
             this.sampler = (HTTPSamplerBase) base.clone();
             // We don't want to use CacheManager clone but the parent one, and CacheManager is Thread Safe
             CacheManager cacheManager = base.getCacheManager();
             if (cacheManager != null) {
                 this.sampler.setCacheManagerProperty(cacheManager.createCacheManagerProxy());
             }
 
             if (cookieManager != null) {
                 CookieManager clonedCookieManager = (CookieManager) cookieManager.clone();
                 this.sampler.setCookieManagerProperty(clonedCookieManager);
             }
             this.sampler.setMD5(this.sampler.useMD5() || IGNORE_EMBEDDED_RESOURCES_DATA);
             this.jmeterContextOfParentThread = JMeterContextService.getContext();
         }
 
         @Override
         public AsynSamplerResultHolder call() {
             JMeterContextService.replaceContext(jmeterContextOfParentThread);
             HTTPSampleResult httpSampleResult = sampler.sample(url, method, areFollowingRedirect, depth);
             if (sampler.getCookieManager() != null) {
                 CollectionProperty cookies = sampler.getCookieManager().getCookies();
                 return new AsynSamplerResultHolder(httpSampleResult, cookies);
             } else {
                 return new AsynSamplerResultHolder(httpSampleResult, new CollectionProperty());
             }
         }
     }
 
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/DirectAccessByteArrayOutputStream.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/DirectAccessByteArrayOutputStream.java
new file mode 100644
index 000000000..6fdffaa37
--- /dev/null
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/DirectAccessByteArrayOutputStream.java
@@ -0,0 +1,46 @@
+/*
+ * Licensed to the Apache Software Foundation (ASF) under one or more
+ * contributor license agreements.  See the NOTICE file distributed with
+ * this work for additional information regarding copyright ownership.
+ * The ASF licenses this file to You under the Apache License, Version 2.0
+ * (the "License"); you may not use this file except in compliance with
+ * the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+package org.apache.jmeter.protocol.http.util;
+
+import java.io.ByteArrayOutputStream;
+import java.util.Arrays;
+
+/**
+ * this is a non thread-safe specialization of java {@link ByteArrayOutputStream}
+ * it returns the internal buffer if its size matches the byte count
+ * 
+ * @since 3.1
+ */
+public class DirectAccessByteArrayOutputStream extends ByteArrayOutputStream {
+
+    public DirectAccessByteArrayOutputStream(int initialSize) {
+        super(initialSize);
+    }
+
+    @SuppressWarnings("sync-override")
+    @Override
+    public byte[] toByteArray() {
+        // no need to copy the buffer if it has the right size
+        // avoid an unneeded memory allocation
+        if(this.count == this.buf.length) {
+            return this.buf;
+        }
+        
+        return Arrays.copyOf(buf, count);
+    }
+    
+}
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 5ab94dd97..120ed8adc 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,280 +1,280 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 <!ENTITY vellip   "&#x022EE;" >
 ]>
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 3.1 =================== -->
 
 <h1>Version 3.1</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_category>Sample category</ch_category>
 <ch_title>Sample title</ch_title>
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Sample change...</li>
 </ul>
 
 <h3>Deprecated and removed elements</h3>
 <ul>
     <li>Sample removed element</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
-    <li><bug>XXXXX</bug>Sample Bugzilla title</li>
+    <li><bug>59882</bug>Reduce memory allocations for better throughput. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com) through <pr>217</pr></li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><pr>211</pr>Differentiate the timing for JDBC Sampler. Use latency and connect time.
     Contributed by Thomas Peyrard (thomas.peyrard at murex.com)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>59351</bug>Improve log/error/message for IncludeController. Partly contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>59609</bug>Format extracted JSON Objects in JSON Post Processor correctly as JSON.</li>
     <li><bug>59845</bug>Log messages about JSON Path mismatches at <code>debug</code> level instead of <code>error</code>.</li>
     <li><pr>212</pr>Allow multiple selection and delete in HTTP Authorization Manager. Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><bug>59816</bug><pr>213</pr>Allow multiple selection and delete in HTTP Header Manager.
     Based on a patch by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li><pr>214</pr>Add spanish translation for delayed starting of threads. Contributed by Asier Lostalé (asier.lostale at openbravo.com).</li>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>59803</bug>Use <code>isValid()</code> method from jdbc driver, if no validationQuery
     is given in JDBC Connection Configuration.</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li>Updated to tika-core and tika-parsers 1.13 (from 1.12)</li>
     <li><pr>215</pr>Reduce duplicated code by using the newly added method <code>GuiUtils#cancelEditing</code>.
     Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>218</pr>Misc cleanup. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
     <li><pr>216</pr>Re-use pattern when possible. Contributed by Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>58888</bug>HTTP(S) Test Script Recorder (ProxyControl) does not add TestElement's returned by SamplerCreator createChildren ()</li>
  </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>59712</bug>Display original query in RequestView when decoding fails. Based on a patch by
          Teemu Vesala (teemu.vesala at qentinel.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>59400</bug>Get rid of UnmarshalException on stopping when <code>-X</code> option is used.</li>
     <li><bug>59607</bug>JMeter crashes when reading large test plan (greater than 2g). Based on fix by Felix Draxler (felix.draxler at sap.com)</li>
     <li><bug>59621</bug>Error count in report dashboard is one off.</li>
     <li><bug>59657</bug>Only set font in JSyntaxTextArea, when property <code>jsyntaxtextarea.font.family</code> is set.</li>
     <li><bug>59720</bug>Batch test file comparisons fail on Windows as XML files are generated as EOL=LF</li>
     <li>Code cleanups. Patches by Graham Russell (graham at ham1.co.uk)</li>
     <li><bug>59722</bug>Use StandardCharsets to reduce the possibility of misspelling Charset names.</li>
     <li><bug>59723</bug>Use jmeter.properties for testing whenever possible</li>
     <li><bug>59726</bug>Unit test to check that CSV header text and sample format don't change unexpectedly</li>
     <li><bug>59889</bug>Change encoding to UTF-8 in reports for dashboard.</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Felix Draxler (felix.draxler at sap.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Teemu Vesala (teemu.vesala at qentinel.com)</li>
 <li>Asier Lostalé (asier.lostale at openbravo.com)</li>
 <li>Thomas Peyrard (thomas.peyrard at murex.com)</li>
 <li>Benoit Wiart (b.wiart at ubik-ingenierie.com)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <source>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </source>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
