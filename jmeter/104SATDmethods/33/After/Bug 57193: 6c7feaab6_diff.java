diff --git a/src/junit/org/apache/jmeter/protocol/java/sampler/JUnitSampler.java b/src/junit/org/apache/jmeter/protocol/java/sampler/JUnitSampler.java
index dbd40464a..e0108cf37 100644
--- a/src/junit/org/apache/jmeter/protocol/java/sampler/JUnitSampler.java
+++ b/src/junit/org/apache/jmeter/protocol/java/sampler/JUnitSampler.java
@@ -1,718 +1,726 @@
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
 package org.apache.jmeter.protocol.java.sampler;
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.Enumeration;
 
 import junit.framework.AssertionFailedError;
 import junit.framework.Protectable;
 import junit.framework.TestCase;
 import junit.framework.TestFailure;
 import junit.framework.TestResult;
 
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.Test.None;
 
 /**
  *
  * This is a basic implementation that runs a single test method of
  * a JUnit test case. The current implementation will use the string
  * constructor first. If the test class does not declare a string
  * constructor, the sampler will try empty constructor.
  */
 public class JUnitSampler extends AbstractSampler implements ThreadListener {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L; // Remember to change this when the class changes ...
 
     //++ JMX file attributes - do not change
     private static final String CLASSNAME = "junitSampler.classname";
     private static final String CONSTRUCTORSTRING = "junitsampler.constructorstring";
     private static final String METHOD = "junitsampler.method";
     private static final String ERROR = "junitsampler.error";
     private static final String ERRORCODE = "junitsampler.error.code";
     private static final String FAILURE = "junitsampler.failure";
     private static final String FAILURECODE = "junitsampler.failure.code";
     private static final String SUCCESS = "junitsampler.success";
     private static final String SUCCESSCODE = "junitsampler.success.code";
     private static final String FILTER = "junitsampler.pkg.filter";
     private static final String DOSETUP = "junitsampler.exec.setup";
     private static final String APPEND_ERROR = "junitsampler.append.error";
     private static final String APPEND_EXCEPTION = "junitsampler.append.exception";
     private static final String JUNIT4 = "junitsampler.junit4";
     private static final String CREATE_INSTANCE_PER_SAMPLE="junitsampler.createinstancepersample";
     private static final boolean CREATE_INSTANCE_PER_SAMPLE_DEFAULT = false;
     //-- JMX file attributes - do not change
 
     private static final String SETUP = "setUp";
     private static final String TEARDOWN = "tearDown";
 
     // the Method objects for setUp (@Before) and tearDown (@After) methods
     // Will be null if not provided or not required
     private transient Method setUpMethod;
     private transient Method tearDownMethod;
 
     // The TestCase to run
     private transient TestCase testCase;
     // The test object, i.e. the instance of the class containing the test method
     // This is the same as testCase for JUnit3 tests
     // but different for JUnit4 tests which use a wrapper
     private transient Object testObject;
 
     // The method name to be invoked
     private transient String methodName;
     // The name of the class containing the method
     private transient String className;
     // The wrapper used to invoke the method
     private transient Protectable protectable;
 
     public JUnitSampler(){
         super();
     }
 
     /**
      * Method tries to get the setUp and tearDown method for the class
      * @param testObject
      */
     private void initMethodObjects(Object testObject){
         setUpMethod = null;
         tearDownMethod = null;
         if (!getDoNotSetUpTearDown()) {
             setUpMethod = getJunit4() ?
                 getMethodWithAnnotation(testObject, Before.class)
                 :
                 getMethod(testObject, SETUP);
             tearDownMethod = getJunit4() ?
                 getMethodWithAnnotation(testObject, After.class)
                 :
                 getMethod(testObject, TEARDOWN);
         }
     }
 
     /**
      * Sets the Classname attribute of the JavaConfig object
      *
      * @param classname
      *            the new Classname value
      */
     public void setClassname(String classname)
     {
         setProperty(CLASSNAME, classname);
     }
 
     /**
      * Gets the Classname attribute of the JavaConfig object
      *
      * @return  the Classname value
      */
     public String getClassname()
     {
         return getPropertyAsString(CLASSNAME);
     }
 
     /**
      * Set the string label used to create an instance of the
      * test with the string constructor.
-     * @param constr
+     * @param constr the string passed to the constructor
      */
     public void setConstructorString(String constr)
     {
         setProperty(CONSTRUCTORSTRING,constr);
     }
 
     /**
-     * get the string passed to the string constructor
+     * @return the string passed to the string constructor
      */
     public String getConstructorString()
     {
         return getPropertyAsString(CONSTRUCTORSTRING);
     }
 
     /**
-     * Return the name of the method to test
+     * @return the name of the method to test
      */
     public String getMethod(){
         return getPropertyAsString(METHOD);
     }
 
     /**
-     * Method should add the JUnit testXXX method to the list at
+     * Method should add the JUnit <em>testXXX</em> method to the list at
      * the end, since the sequence matters.
-     * @param methodName
+     * @param methodName name of the method to test
      */
     public void setMethod(String methodName){
         setProperty(METHOD,methodName);
     }
 
     /**
-     * get the success message
+     * @return the success message
      */
     public String getSuccess(){
         return getPropertyAsString(SUCCESS);
     }
 
     /**
      * set the success message
-     * @param success
+     * @param success message to be used for success
      */
     public void setSuccess(String success){
         setProperty(SUCCESS,success);
     }
 
     /**
-     * get the success code defined by the user
+     * @return the success code defined by the user
      */
     public String getSuccessCode(){
         return getPropertyAsString(SUCCESSCODE);
     }
 
     /**
-     * set the succes code. the success code should
+     * Set the success code. The success code should
      * be unique.
-     * @param code
+     * @param code unique success code
      */
     public void setSuccessCode(String code){
         setProperty(SUCCESSCODE,code);
     }
 
     /**
-     * get the failure message
+     * @return the failure message
      */
     public String getFailure(){
         return getPropertyAsString(FAILURE);
     }
 
     /**
      * set the failure message
-     * @param fail
+     * @param fail the failure message
      */
     public void setFailure(String fail){
         setProperty(FAILURE,fail);
     }
 
     /**
-     * The failure code is used by other components
+     * @return The failure code that is used by other components
      */
     public String getFailureCode(){
         return getPropertyAsString(FAILURECODE);
     }
 
     /**
      * Provide some unique code to denote a type of failure
-     * @param code
+     * @param code unique code to denote the type of failure
      */
     public void setFailureCode(String code){
         setProperty(FAILURECODE,code);
     }
 
     /**
-     * return the descriptive error for the test
+     * @return the descriptive error for the test
      */
     public String getError(){
         return getPropertyAsString(ERROR);
     }
 
     /**
      * provide a descriptive error for the test method. For
      * a description of the difference between failure and
-     * error, please refer to the following url
-     * http://junit.sourceforge.net/doc/faq/faq.htm#tests_9
-     * @param error
+     * error, please refer to the
+     * <a href="http://junit.sourceforge.net/doc/faq/faq.htm#tests_9">junit faq</a>
+     * @param error the description of the error
      */
     public void setError(String error){
         setProperty(ERROR,error);
     }
 
     /**
-     * return the error code for the test method. it should
+     * @return the error code for the test method. It should
      * be an unique error code.
      */
     public String getErrorCode(){
         return getPropertyAsString(ERRORCODE);
     }
 
     /**
-     * provide an unique error code for when the test
+     * Provide an unique error code for when the test
      * does not pass the assert test.
-     * @param code
+     * @param code unique error code
      */
     public void setErrorCode(String code){
         setProperty(ERRORCODE,code);
     }
 
     /**
-     * return the comma separated string for the filter
+     * @return the comma separated string for the filter
      */
     public String getFilterString(){
         return getPropertyAsString(FILTER);
     }
 
     /**
-     * set the filter string in comman separated format
-     * @param text
+     * set the filter string in comma separated format
+     * @param text comma separated filter
      */
     public void setFilterString(String text){
         setProperty(FILTER,text);
     }
 
     /**
      * if the sample shouldn't call setup/teardown, the
      * method returns true. It's meant for onetimesetup
      * and onetimeteardown.
+     * 
+     * @return flag whether setup/teardown methods should not be called
      */
     public boolean getDoNotSetUpTearDown(){
         return getPropertyAsBoolean(DOSETUP);
     }
 
     /**
      * set the setup/teardown option
-     * @param setup
+     *
+     * @param setup flag whether the setup/teardown methods should not be called
      */
     public void setDoNotSetUpTearDown(boolean setup){
         setProperty(DOSETUP,String.valueOf(setup));
     }
 
     /**
      * If append error is not set, by default it is set to false,
      * which means users have to explicitly set the sampler to
      * append the assert errors. Because of how junit works, there
      * should only be one error
+     *
+     * @return flag whether errors should be appended
      */
     public boolean getAppendError() {
         return getPropertyAsBoolean(APPEND_ERROR,false);
     }
 
     /**
      * Set whether to append errors or not.
      *
      * @param error the setting to apply
      */
     public void setAppendError(boolean error) {
         setProperty(APPEND_ERROR,String.valueOf(error));
     }
 
     /**
-     * If append exception is not set, by default it is set to false.
-     * Users have to explicitly set it to true to see the exceptions
+     * If append exception is not set, by default it is set to <code>false</code>.
+     * Users have to explicitly set it to <code>true</code> to see the exceptions
      * in the result tree.
+     * 
+     * @return flag whether exceptions should be appended to the result tree
      */
     public boolean getAppendException() {
         return getPropertyAsBoolean(APPEND_EXCEPTION,false);
     }
 
     /**
      * Set whether to append exceptions or not.
      *
      * @param exc the setting to apply.
      */
     public void setAppendException(boolean exc) {
         setProperty(APPEND_EXCEPTION,String.valueOf(exc));
     }
 
     /**
      * Check if JUnit4 (annotations) are to be used instead of
      * the JUnit3 style (TestClass and specific method names)
      *
      * @return true if JUnit4 (annotations) are to be used.
      * Default is false.
      */
     public boolean getJunit4() {
         return getPropertyAsBoolean(JUNIT4, false);
     }
 
     /**
      * Set whether to use JUnit4 style or not.
      * @param junit4 true if JUnit4 style is to be used.
      */
     public void setJunit4(boolean junit4) {
         setProperty(JUNIT4, junit4, false);
     }
 
     /** {@inheritDoc} */
     @Override
     public SampleResult sample(Entry entry) {
         if(getCreateOneInstancePerSample()) {
             initializeTestObject();
         }
         SampleResult sresult = new SampleResult();
         sresult.setSampleLabel(getName());// Bug 41522 - don't use rlabel here
         sresult.setSamplerData(className + "." + methodName);
         sresult.setDataType(SampleResult.TEXT);
         // Assume success
         sresult.setSuccessful(true);
         sresult.setResponseMessage(getSuccess());
         sresult.setResponseCode(getSuccessCode());
         if (this.testCase != null){
             // create a new TestResult
             TestResult tr = new TestResult();
             final TestCase theClazz = this.testCase;
             try {
                 if (setUpMethod != null){
                     setUpMethod.invoke(this.testObject,new Object[0]);
                 }
                 sresult.sampleStart();
                 tr.startTest(this.testCase);
                 // Do not use TestCase.run(TestResult) method, since it will
                 // call setUp and tearDown. Doing that will result in calling
                 // the setUp and tearDown method twice and the elapsed time
                 // will include setup and teardown.
                 tr.runProtected(theClazz, protectable);
                 tr.endTest(this.testCase);
                 sresult.sampleEnd();
                 if (tearDownMethod != null){
                     tearDownMethod.invoke(testObject,new Object[0]);
                 }
             } catch (InvocationTargetException e) {
                 Throwable cause = e.getCause();
                 if (cause instanceof AssertionFailedError){
                     tr.addFailure(theClazz, (AssertionFailedError) cause);
                 } else if (cause instanceof AssertionError) {
                     // Convert JUnit4 failure to Junit3 style
                     AssertionFailedError afe = new AssertionFailedError(cause.toString());
                     // copy the original stack trace
                     afe.setStackTrace(cause.getStackTrace());
                     tr.addFailure(theClazz, afe);
                 } else if (cause != null) {
                     tr.addError(theClazz, cause);
                 } else {
                     tr.addError(theClazz, e);
                 }
             } catch (IllegalAccessException e) {
                 tr.addError(theClazz, e);
             } catch (IllegalArgumentException e) {
                 tr.addError(theClazz, e);
             }
             if ( !tr.wasSuccessful() ){
                 sresult.setSuccessful(false);
                 StringBuilder buf = new StringBuilder();
                 StringBuilder buftrace = new StringBuilder();
                 Enumeration<TestFailure> en;
                 if (getAppendError()) {
                     en = tr.failures();
                     if (en.hasMoreElements()){
                         sresult.setResponseCode(getFailureCode());
                         buf.append( getFailure() );
                         buf.append("\n");
                     }
                     while (en.hasMoreElements()){
                         TestFailure item = en.nextElement();
                         buf.append( "Failure -- ");
                         buf.append( item.toString() );
                         buf.append("\n");
                         buftrace.append( "Failure -- ");
                         buftrace.append( item.toString() );
                         buftrace.append("\n");
                         buftrace.append( "Trace -- ");
                         buftrace.append( item.trace() );
                     }
                     en = tr.errors();
                     if (en.hasMoreElements()){
                         sresult.setResponseCode(getErrorCode());
                         buf.append( getError() );
                         buf.append("\n");
                     }
                     while (en.hasMoreElements()){
                         TestFailure item = en.nextElement();
                         buf.append( "Error -- ");
                         buf.append( item.toString() );
                         buf.append("\n");
                         buftrace.append( "Error -- ");
                         buftrace.append( item.toString() );
                         buftrace.append("\n");
                         buftrace.append( "Trace -- ");
                         buftrace.append( item.trace() );
                     }
                 }
                 sresult.setResponseMessage(buf.toString());
                 sresult.setResponseData(buftrace.toString(), null);
             }
         } else {
             // we should log a warning, but allow the test to keep running
             sresult.setSuccessful(false);
             // this should be externalized to the properties
             sresult.setResponseMessage("Failed to create an instance of the class:"+getClassname()
                     +", reasons may be missing both empty constructor and one " 
                     + "String constructor or failure to instantiate constructor," 
                     + " check warning messages in jmeter log file");
             sresult.setResponseCode(getErrorCode());
         }
         return sresult;
     }
 
     /**
      * If the method is not able to create a new instance of the
      * class, it returns null and logs all the exceptions at
      * warning level.
      */
     private static Object getClassInstance(String className, String label){
         Object testclass = null;
         if (className != null){
             Constructor<?> con = null;
             Constructor<?> strCon = null;
             Class<?> theclazz = null;
             Object[] strParams = null;
             Object[] params = null;
             try
             {
                 theclazz =
                     Thread.currentThread().getContextClassLoader().loadClass(className.trim());
             } catch (ClassNotFoundException e) {
                 log.warn("ClassNotFoundException:: " + e.getMessage());
             }
             if (theclazz != null) {
                 // first we see if the class declares a string
                 // constructor. if it is doesn't we look for
                 // empty constructor.
                 try {
                     strCon = theclazz.getDeclaredConstructor(
                             new Class[] {String.class});
                     // we have to check and make sure the constructor is
                     // accessible. if we didn't it would throw an exception
                     // and cause a NPE.
                     if (label == null || label.length() == 0) {
                         label = className;
                     }
                     if (strCon.getModifiers() == Modifier.PUBLIC) {
                         strParams = new Object[]{label};
                     } else {
                         strCon = null;
                     }
                 } catch (NoSuchMethodException e) {
                     log.info("Trying to find constructor with one String parameter returned error: " + e.getMessage());
                 }
                 try {
                     con = theclazz.getDeclaredConstructor(new Class[0]);
                     if (con != null){
                         params = new Object[]{};
                     }
                 } catch (NoSuchMethodException e) {
                     log.info("Trying to find empty constructor returned error: " + e.getMessage());
                 }
                 try {
                     // if the string constructor is not null, we use it.
                     // if the string constructor is null, we use the empty
                     // constructor to get a new instance
                     if (strCon != null) {
                         testclass = strCon.newInstance(strParams);
                     } else if (con != null){
                         testclass = con.newInstance(params);
                     } else {
                         log.error("No empty constructor nor string constructor found for class:"+theclazz);
                     }
                 } catch (InvocationTargetException e) {
                     log.error("Error instantiating class:"+theclazz+":"+e.getMessage(), e);
                 } catch (InstantiationException e) {
                     log.error("Error instantiating class:"+theclazz+":"+e.getMessage(), e);
                 } catch (IllegalAccessException e) {
                     log.error("Error instantiating class:"+theclazz+":"+e.getMessage(), e);
                 }
             }
         }
         return testclass;
     }
 
     /**
      * Get a method.
      * @param clazz the classname (may be null)
      * @param method the method name (may be null)
      * @return the method or null if an error occurred
      * (or either parameter is null)
      */
     private Method getMethod(Object clazz, String method){
         if (clazz != null && method != null){
             try {
                 return clazz.getClass().getMethod(method,new Class[0]);
             } catch (NoSuchMethodException e) {
                 log.warn(e.getMessage());
             }
         }
         return null;
     }
 
     private Method getMethodWithAnnotation(Object clazz, Class<? extends Annotation> annotation) {
         if(null != clazz && null != annotation) {
             for(Method m : clazz.getClass().getMethods()) {
                 if(m.isAnnotationPresent(annotation)) {
                     return m;
                 }
             }
         }
         return null;
     }
 
     /*
      * Wrapper to convert a JUnit4 class into a TestCase
      *
      *  TODO - work out how to convert JUnit4 assertions so they are treated as failures rather than errors
      */
     private class AnnotatedTestCase extends TestCase {
         private final Method method;
         private final Class<? extends Throwable> expectedException;
         private final long timeout;
         public AnnotatedTestCase(Method method, Class<? extends Throwable> expectedException2, long timeout) {
             this.method = method;
             this.expectedException = expectedException2;
             this.timeout = timeout;
         }
 
         @Override
         protected void runTest() throws Throwable {
             try {
                 long start = System.currentTimeMillis();
                 method.invoke(testObject, (Object[])null);
                 if (expectedException != None.class) {
                     throw new AssertionFailedError(
                     "No error was generated for a test case which specifies an error.");
                 }
                 if (timeout > 0){
                     long elapsed = System.currentTimeMillis() - start;
                     if (elapsed > timeout) {
                         throw new AssertionFailedError("Test took longer than the specified timeout.");
                     }
                 }
             } catch (InvocationTargetException e) {
                 Throwable thrown = e.getCause();
                 if (thrown == null) { // probably should not happen
                     throw e;
                 }
                 if (expectedException == None.class){
                     // Convert JUnit4 AssertionError failures to JUnit3 style so
                     // will be treated as failure rather than error.
                     if (thrown instanceof AssertionError && !(thrown instanceof AssertionFailedError)){
                         AssertionFailedError afe = new AssertionFailedError(thrown.toString());
                         // copy the original stack trace
                         afe.setStackTrace(thrown.getStackTrace());
                         throw afe;
                     }
                     throw thrown;
                 }
                 if (!expectedException.isAssignableFrom(thrown.getClass())){
                     throw new AssertionFailedError("The wrong exception was thrown from the test case");
                 }
             }
         }
     }
 
     @Override
     public void threadFinished() {
     }
 
     /**
      * Set up all variables that don't change between samples.
      */
     @Override
     public void threadStarted() {
         testObject = null;
         testCase = null;
         methodName = getMethod();
         className = getClassname();
         protectable = null;
         if(!getCreateOneInstancePerSample()) {
             // NO NEED TO INITIALIZE WHEN getCreateOneInstancePerSample 
             // is true cause it will be done in sample
             initializeTestObject();            
         }
     }
 
     /**
      * Initialize test object
      */
     private void initializeTestObject() {
         String rlabel = getConstructorString();
         if (rlabel.length()== 0) {
             rlabel = JUnitSampler.class.getName();
         }
         this.testObject = getClassInstance(className, rlabel);
         if (this.testObject != null){
             initMethodObjects(this.testObject);
             final Method m = getMethod(this.testObject,methodName);
             if (getJunit4()){
                 Class<? extends Throwable> expectedException = None.class;
                 long timeout = 0;
                 Test annotation = m.getAnnotation(Test.class);
                 if(null != annotation) {
                     expectedException = annotation.expected();
                     timeout = annotation.timeout();
                 }
                 final AnnotatedTestCase at = new AnnotatedTestCase(m, expectedException, timeout);
                 testCase = at;
                 protectable = new Protectable() {
                     @Override
                     public void protect() throws Throwable {
                         at.runTest();
                     }
                 };
             } else {
                 this.testCase = (TestCase) this.testObject;
                 final Object theClazz = this.testObject; // Must be final to create instance
                 protectable = new Protectable() {
                     @Override
                     public void protect() throws Throwable {
                         try {
                             m.invoke(theClazz,new Object[0]);
                         } catch (InvocationTargetException e) {
                             /*
                              * Calling a method via reflection results in wrapping any
                              * Exceptions in ITE; unwrap these here so runProtected can
                              * allocate them correctly.
                              */
                             Throwable t = e.getCause();
                             if (t != null) {
                                 throw t;
                             }
                             throw e;
                         }
                     }
                 };
             }
             if (this.testCase != null){
                 this.testCase.setName(methodName);
             }
         }
     }
 
     /**
      * 
      * @param createOneInstancePerSample
+     *            flag whether a new instance for each call should be created
      */
     public void setCreateOneInstancePerSample(boolean createOneInstancePerSample) {
         this.setProperty(CREATE_INSTANCE_PER_SAMPLE, createOneInstancePerSample, CREATE_INSTANCE_PER_SAMPLE_DEFAULT);
     }
 
     /**
      * 
      * @return boolean create New Instance For Each Call
      */
     public boolean getCreateOneInstancePerSample() {
         return getPropertyAsBoolean(CREATE_INSTANCE_PER_SAMPLE, CREATE_INSTANCE_PER_SAMPLE_DEFAULT);
     }
 }
diff --git a/src/monitor/components/org/apache/jmeter/monitor/util/Stats.java b/src/monitor/components/org/apache/jmeter/monitor/util/Stats.java
index 06373542c..fed70de10 100644
--- a/src/monitor/components/org/apache/jmeter/monitor/util/Stats.java
+++ b/src/monitor/components/org/apache/jmeter/monitor/util/Stats.java
@@ -1,191 +1,193 @@
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
 package org.apache.jmeter.monitor.util;
 
 import org.apache.jmeter.monitor.model.Connector;
 import org.apache.jmeter.monitor.model.Status;
 
 /**
  *
  * Description:
  * <p>
  * Stats is responsible for calculating the load and health of a given server.
  * It uses tomcat's status servlet results. A schema was generated for the XML
  * output and JAXB was used to generate classes.
  * <p>
  * The equations are:
  * <p>
  * memory weight = (int)(50 * (free/max))<br>
  * thread weight = (int)(50 * (current/max))
  * <p>
  * The load factors are stored in the properties files. Simply change the values
  * in the properties to change how load is calculated. The defaults values are
  * memory (50) and threads (50). The sum of the factors must equal 100.
  */
 public class Stats {
 
     public static final int DEAD = 0;
 
     public static final int ACTIVE = 2;
 
     public static final int WARNING = 1;
 
     public static final int HEALTHY = 3;
 
     public static final int DEFAULT_MEMORY_FACTOR = 50;
 
     public static final int DEFAULT_THREAD_FACTOR = 50;
 
     public static final double HEALTHY_PER = 0.00;
 
     public static final double ACTIVE_PER = 0.25;
 
     public static final double WARNING_PER = 0.67;
 
     /**
      * The method is responsible for taking a status object and calculating an
      * int value from 1 to 100. We use a combination of free memory and free
      * threads. The current factor is 50/50.
      * <p>
      *
-     * @param stat
+     * @param stat status information about the server
      * @return calculated load value
      */
     public static int calculateLoad(Status stat) {
         if (stat != null) {
             // equation for calculating the weight
             // w = (int)(33 * (used/max))
             long totMem = stat.getJvm().getMemory().getTotal();
             long freeMem = stat.getJvm().getMemory().getFree();
             long usedMem = totMem - freeMem;
             double memdiv = (double) usedMem / (double) totMem;
             double memWeight = DEFAULT_MEMORY_FACTOR * memdiv;
 
             // changed the logic for BEA Weblogic in the case a
             // user uses Tomcat's status servlet without any
             // modifications. Weblogic will return nothing for
             // the connector, therefore we need to check the size
             // of the list. Peter 12.22.04
             double threadWeight = 0;
             if (stat.getConnector().size() > 0) {
                 Connector cntr = fetchConnector(stat);
                 int maxThread = cntr.getThreadInfo().getMaxThreads();
                 int curThread = cntr.getThreadInfo().getCurrentThreadsBusy();
                 double thdiv = (double) curThread / (double) maxThread;
                 threadWeight = DEFAULT_THREAD_FACTOR * thdiv;
             }
             return (int) (memWeight + threadWeight);
         } else {
             return 0;
         }
     }
 
     /**
      * Method should calculate if the server is: dead, active, warning or
      * healthy. We do this by looking at the current busy threads.
      * <ol>
-     * <li> free &gt; spare is healthy
-     * <li> free &lt; spare is active
-     * <li> busy threads &gt; 75% is warning
-     * <li> none of the above is dead
+     * <li>free &gt; spare is {@link Stats#HEALTHY healthy}</li>
+     * <li>free &lt; spare is {@link Stats#ACTIVE active}</li>
+     * <li>busy threads &gt; 75% is {@link Stats#WARNING warning}</li>
+     * <li>none of the above is {@link Stats#DEAD dead}</li>
      * </ol>
      *
-     * @param stat
-     * @return integer representing the status
+     * @param stat status information about the server
+     * @return integer representing the status (one of {@link Stats#HEALTHY
+     *         HEALTHY}, {@link Stats#ACTIVE ACTIVE}, {@link Stats#WARNING
+     *         WARNING} or {@link Stats#DEAD DEAD})
      */
     public static int calculateStatus(Status stat) {
         if (stat != null && stat.getConnector().size() > 0) {
             Connector cntr = fetchConnector(stat);
             int max = cntr.getThreadInfo().getMaxThreads();
             int current = cntr.getThreadInfo().getCurrentThreadsBusy();
             // int spare = cntr.getThreadInfo().getMaxSpareThreads();
             double per = (double) current / (double) max;
             if (per > WARNING_PER) {
                 return WARNING;
             } else if (per >= ACTIVE_PER && per <= WARNING_PER) {
                 return ACTIVE;
             } else if (per < ACTIVE_PER && per >= HEALTHY_PER) {
                 return HEALTHY;
             } else {
                 return DEAD;
             }
         } else {
             return DEAD;
         }
     }
 
     /**
      * Method will calculate the memory load: used / max = load. The load value
      * is an integer between 1 and 100. It is the percent memory used. Changed
      * this to be more like other system monitors. Peter Lin 2-11-05
      *
-     * @param stat
+     * @param stat status information about the jvm
      * @return memory load
      */
     public static int calculateMemoryLoad(Status stat) {
         double load = 0;
         if (stat != null) {
             double total = stat.getJvm().getMemory().getTotal();
             double free = stat.getJvm().getMemory().getFree();
             double used = total - free;
             load = (used / total);
         }
         return (int) (load * 100);
     }
 
     /**
      * Method will calculate the thread load: busy / max = load. The value is an
      * integer between 1 and 100. It is the percent busy.
      *
-     * @param stat
+     * @param stat status information about the server
      * @return thread load
      */
     public static int calculateThreadLoad(Status stat) {
         int load = 0;
         if (stat != null && stat.getConnector().size() > 0) {
             Connector cntr = fetchConnector(stat);
             double max = cntr.getThreadInfo().getMaxThreads();
             double current = cntr.getThreadInfo().getCurrentThreadsBusy();
             load = (int) ((current / max) * 100);
         }
         return load;
     }
 
     /**
      * Method to get connector to use for calculate server status
      *
      * @param stat
      * @return connector
      */
     private static Connector fetchConnector(Status stat) {
         Connector cntr = null;
         String connectorPrefix = stat.getConnectorPrefix();
         if (connectorPrefix != null && connectorPrefix.length() > 0) {
            // loop to fetch desired connector
            for (int i = 0; i < stat.getConnector().size(); i++) {
                cntr = stat.getConnector().get(i);
                if (cntr.getName().startsWith(connectorPrefix)) {
                    return cntr;
                }
            }
         }
         // default : get first connector
         cntr = stat.getConnector().get(0);
         return cntr;
     }
 
 }
diff --git a/src/monitor/components/org/apache/jmeter/visualizers/MonitorAccumModel.java b/src/monitor/components/org/apache/jmeter/visualizers/MonitorAccumModel.java
index fa9fef63a..7394540f6 100644
--- a/src/monitor/components/org/apache/jmeter/visualizers/MonitorAccumModel.java
+++ b/src/monitor/components/org/apache/jmeter/visualizers/MonitorAccumModel.java
@@ -1,241 +1,248 @@
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
 package org.apache.jmeter.visualizers;
 
 import java.io.Serializable;
 import java.net.URL;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.jmeter.monitor.model.ObjectFactory;
 import org.apache.jmeter.monitor.model.Status;
 import org.apache.jmeter.monitor.util.Stats;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 
 public class MonitorAccumModel implements Clearable, Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private final Map<String, List<MonitorModel>> serverListMap;
 
     /**
      * we use this to set the current monitorModel so that we can save the stats
      * to the resultcolllector.
      */
     private MonitorModel current;
 
     private final List<MonitorListener> listeners;
 
     /**
      * By default, we set the default to 800
      */
     private int defaultBufferSize = 800;
 
     // optional connector name prefix
     private String connectorPrefix = null;
 
     /**
      *
      */
     public MonitorAccumModel() {
         serverListMap = new HashMap<String, List<MonitorModel>>();
         listeners = new LinkedList<MonitorListener>();
     }
 
     public int getBufferSize() {
         return defaultBufferSize;
     }
 
     public void setBufferSize(int buffer) {
         defaultBufferSize = buffer;
     }
 
     public void setPrefix(String prefix) {
         connectorPrefix = prefix;
     }
 
     /**
      * Added this method we that we can save the calculated stats.
      *
      * @return current sample
      */
     public MonitorModel getLastSample() {
         return this.current;
     }
 
     /**
      * Method will look up the server in the map. The MonitorModel will be added
      * to an existing list, or a new one will be created.
      *
-     * @param model
+     * @param model the {@link MonitorModel} to be added
      */
     public void addSample(MonitorModel model) {
         this.current = model;
         if (serverListMap.containsKey(model.getURL())) {
             List<MonitorModel> newlist = updateArray(model, serverListMap.get(model.getURL()));
             serverListMap.put(model.getURL(), newlist);
         } else {
             List<MonitorModel> samples = Collections.synchronizedList(new LinkedList<MonitorModel>());
             samples.add(model);
             serverListMap.put(model.getURL(), samples);
         }
     }
 
     /**
      * We want to keep only 240 entries for each server, so we handle the object
      * array ourselves.
      *
      * @param model
      */
     private List<MonitorModel> updateArray(MonitorModel model, List<MonitorModel> list) {
         if (list.size() < defaultBufferSize) {
             list.add(model);
         } else {
             list.add(model);
             list.remove(0);
         }
         return list;
     }
 
     /**
      * Get all MonitorModels matching the URL.
      *
-     * @param url
+     * @param url to be matched against
      * @return list
      */
     public List<MonitorModel> getAllSamples(String url) {
         if (!serverListMap.containsKey(url)) {
             return Collections.synchronizedList(new LinkedList<MonitorModel>());
         } else {
             return serverListMap.get(url);
         }
     }
 
     /**
      * Get the MonitorModel matching the url.
      *
      * @param url
-     * @return list
+     *            to be matched against
+     * @return the first {@link MonitorModel} registered for this
+     *         <code>url</code>
      */
     public MonitorModel getSample(String url) {
         if (serverListMap.containsKey(url)) {
             return serverListMap.get(url).get(0);
         } else {
             return null;
         }
     }
 
     /**
      * Method will try to parse the response data. If the request was a monitor
      * request, but the response was incomplete, bad or the server refused the
      * connection, we will set the server's health to "dead". If the request was
      * not a monitor sample, the method will ignore it.
      *
      * @param sample
+     *            {@link SampleResult} with the result of the status request
      */
     public void addSample(SampleResult sample) {
         URL surl = null;
         if (sample instanceof HTTPSampleResult) {
             surl = ((HTTPSampleResult) sample).getURL();
             // String rescontent = new String(sample.getResponseData());
             if (sample.isResponseCodeOK() && ((HTTPSampleResult) sample).isMonitor()) {
                 ObjectFactory of = ObjectFactory.getInstance();
                 Status st = of.parseBytes(sample.getResponseData());
                 st.setConnectorPrefix(connectorPrefix);
                 if (surl != null) {// surl can be null if read from a file
                     MonitorStats stat = new MonitorStats(Stats.calculateStatus(st), Stats.calculateLoad(st), 0, Stats
                             .calculateMemoryLoad(st), Stats.calculateThreadLoad(st), surl.getHost(), String.valueOf(surl
                             .getPort()), surl.getProtocol(), System.currentTimeMillis());
                     MonitorModel mo = new MonitorModel(stat);
                     this.addSample(mo);
                     notifyListeners(mo);
                 } 
                 // This part of code throws NullPointerException
                 // Don't think Monitor results can be loaded from files
                 // see https://issues.apache.org/bugzilla/show_bug.cgi?id=51810
 //                else {
 //                    noResponse(surl);
 //                }
             } else if (((HTTPSampleResult) sample).isMonitor()) {
                 noResponse(surl);
             }
         }
     }
 
     /**
      * If there is no response from the server, we create a new MonitorStats
      * object with the current timestamp and health "dead".
      *
      * @param url
+     *            URL from where the status should have come
      */
     public void noResponse(URL url) {
         notifyListeners(createNewMonitorModel(url));
     }
 
     /**
      * Method will return a new MonitorModel object with the given URL. This is
      * used when the server fails to respond fully, or is dead.
      *
      * @param url
+     *            URL from where the status should have come
      * @return new MonitorModel
      */
     public MonitorModel createNewMonitorModel(URL url) {
         MonitorStats stat = new MonitorStats(Stats.DEAD, 0, 0, 0, 0, url.getHost(), String.valueOf(url.getPort()), url
                 .getProtocol(), System.currentTimeMillis());
         return new MonitorModel(stat);
     }
 
     /**
      * Clears everything except the listener. Do not clear the listeners. If we
      * clear listeners, subsequent "run" will not notify the gui of data
      * changes.
      */
     @Override
     public void clearData() {
         for (List<MonitorModel> modelList : this.serverListMap.values()) {
             modelList.clear();
         }
         this.serverListMap.clear();
     }
 
     /**
      * notify the listeners with the MonitorModel object.
      *
      * @param model
+     *            the {@link MonitorModel} that should be sent to the listeners
      */
     public void notifyListeners(MonitorModel model) {
         for (int idx = 0; idx < listeners.size(); idx++) {
             MonitorListener ml = listeners.get(idx);
             ml.addSample(model);
         }
     }
 
     /**
      * Add a listener. When samples are added, the class will notify the
      * listener of the change.
      *
      * @param listener
+     *            the {@link MonitorListener} that should be added
      */
     public void addListener(MonitorListener listener) {
         listeners.add(listener);
     }
 }
diff --git a/src/monitor/components/org/apache/jmeter/visualizers/MonitorStats.java b/src/monitor/components/org/apache/jmeter/visualizers/MonitorStats.java
index 7d16c7a16..9d5b7d783 100644
--- a/src/monitor/components/org/apache/jmeter/visualizers/MonitorStats.java
+++ b/src/monitor/components/org/apache/jmeter/visualizers/MonitorStats.java
@@ -1,167 +1,182 @@
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
 package org.apache.jmeter.visualizers;
 
 import java.io.Serializable;
 
+import org.apache.jmeter.monitor.util.Stats;
 import org.apache.jmeter.testelement.AbstractTestElement;
 
 /*
  *  TODO - convert this into an immutable class using plain variables
  *  The current implementation is quite inefficient, as it stores everything
  *  in properties.
  *
  *  This will require changes to ResultCollector.recordStats()
  *  and SaveService.saveTestElement() which are both currently only used by Monitor classes
  */
 public class MonitorStats extends AbstractTestElement implements Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private static final String HEALTH = "stats.health";
 
     private static final String LOAD = "stats.load";
 
     private static final String CPULOAD = "stats.cpuload";
 
     private static final String MEMLOAD = "stats.memload";
 
     private static final String THREADLOAD = "stats.threadload";
 
     private static final String HOST = "stats.host";
 
     private static final String PORT = "stats.port";
 
     private static final String PROTOCOL = "stats.protocol";
 
     private static final String TIMESTAMP = "stats.timestamp";
 
     /**
      *
      */
     public MonitorStats() {
         super();
     }
 
     /**
      * Default constructor
      *
      * @param health
+     *            Health of the server. Has to be one of {@link Stats#HEALTHY
+     *            HEALTHY}, {@link Stats#ACTIVE ACTIVE}, {@link Stats#WARNING
+     *            WARNING} or {@link Stats#DEAD DEAD}
      * @param load
+     *            load of the server as integer from a range in between 1 and
+     *            100
      * @param cpuload
+     *            cpu load of the server as integer from range between 1 and 100
      * @param memload
+     *            load of the server as integer from a range in between 1 and
+     *            100
      * @param threadload
+     *            thread load of the server as an integer from a range in
+     *            between 1 and 100
      * @param host
+     *            name of the host from which the status was taken
      * @param port
+     *            port from which the status was taken
      * @param protocol
+     *            over which the status was taken
      * @param time
+     *            time in milliseconds when this status was created
      */
     public MonitorStats(int health, int load, int cpuload, int memload, int threadload, String host, String port,
             String protocol, long time) {
         this.setHealth(health);
         this.setLoad(load);
         this.setCpuLoad(cpuload);
         this.setMemLoad(memload);
         this.setThreadLoad(threadload);
         this.setHost(host);
         this.setPort(port);
         this.setProtocol(protocol);
         this.setTimeStamp(time);
     }
 
     /**
      * For convienance, this method returns the protocol, host and port as a
      * URL.
      *
      * @return protocol://host:port
      */
     public String getURL() {
         return this.getProtocol() + "://" + this.getHost() + ":" + this.getPort();
     }
 
     public void setHealth(int health) {
         this.setProperty(HEALTH, String.valueOf(health));
     }
 
     public void setLoad(int load) {
         this.setProperty(LOAD, String.valueOf(load));
     }
 
     public void setCpuLoad(int load) {
         this.setProperty(CPULOAD, String.valueOf(load));
     }
 
     public void setMemLoad(int load) {
         this.setProperty(MEMLOAD, String.valueOf(load));
     }
 
     public void setThreadLoad(int load) {
         this.setProperty(THREADLOAD, String.valueOf(load));
     }
 
     public void setHost(String host) {
         this.setProperty(HOST, host);
     }
 
     public void setPort(String port) {
         this.setProperty(PORT, port);
     }
 
     public void setProtocol(String protocol) {
         this.setProperty(PROTOCOL, protocol);
     }
 
     public void setTimeStamp(long time) {
         this.setProperty(TIMESTAMP, String.valueOf(time));
     }
 
     public int getHealth() {
         return this.getPropertyAsInt(HEALTH);
     }
 
     public int getLoad() {
         return this.getPropertyAsInt(LOAD);
     }
 
     public int getCpuLoad() {
         return this.getPropertyAsInt(CPULOAD);
     }
 
     public int getMemLoad() {
         return this.getPropertyAsInt(MEMLOAD);
     }
 
     public int getThreadLoad() {
         return this.getPropertyAsInt(THREADLOAD);
     }
 
     public String getHost() {
         return this.getPropertyAsString(HOST);
     }
 
     public String getPort() {
         return this.getPropertyAsString(PORT);
     }
 
     public String getProtocol() {
         return this.getPropertyAsString(PROTOCOL);
     }
 
     public long getTimeStamp() {
         return this.getPropertyAsLong(TIMESTAMP);
     }
 }
diff --git a/src/monitor/model/org/apache/jmeter/monitor/parser/MonitorHandler.java b/src/monitor/model/org/apache/jmeter/monitor/parser/MonitorHandler.java
index 5213dd9c6..512fe7834 100644
--- a/src/monitor/model/org/apache/jmeter/monitor/parser/MonitorHandler.java
+++ b/src/monitor/model/org/apache/jmeter/monitor/parser/MonitorHandler.java
@@ -1,364 +1,367 @@
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
 package org.apache.jmeter.monitor.parser;
 
 // import java.util.List;
 import java.util.Stack;
 
 import org.xml.sax.Attributes;
 import org.xml.sax.SAXException;
 import org.xml.sax.helpers.DefaultHandler;
 
 import org.apache.jmeter.monitor.model.ObjectFactory;
 import org.apache.jmeter.monitor.model.Connector;
 import org.apache.jmeter.monitor.model.Jvm;
 import org.apache.jmeter.monitor.model.Memory;
 import org.apache.jmeter.monitor.model.RequestInfo;
 import org.apache.jmeter.monitor.model.Status;
 import org.apache.jmeter.monitor.model.ThreadInfo;
 import org.apache.jmeter.monitor.model.Worker;
 import org.apache.jmeter.monitor.model.Workers;
 import org.apache.jmeter.monitor.model.WorkersImpl;
 
 public class MonitorHandler extends DefaultHandler {
     // private boolean startDoc = false;
     // private boolean endDoc = false;
     private final ObjectFactory factory;
 
     private Stack<Object> stacktree;
 
     private Status status;
 
     private Jvm jvm;
 
     private Memory memory;
 
     private Connector connector;
 
     private ThreadInfo threadinfo;
 
     private RequestInfo requestinfo;
 
     private Worker worker;
 
     private Workers workers;
 
     // private List workerslist;
 
     /**
-     *
+     * @param factory {@link ObjectFactory} to use
      */
     public MonitorHandler(ObjectFactory factory) {
         super();
         this.factory = factory;
     }
 
     @Override
     public void startDocument() throws SAXException {
         // this.startDoc = true;
         // Reset all work variables so reusing the instance starts afresh.
         this.stacktree = new Stack<Object>();
         this.status = null;
         this.jvm = null;
         this.memory = null;
         this.connector = null;
         this.threadinfo = null;
         this.requestinfo = null;
         this.worker = null;
         this.workers = null;   
     }
 
     /** {@inheritDoc} */
     @Override
     public void endDocument() throws SAXException {
         // this.startDoc = false;
         // this.endDoc = true;
     }
 
     /**
      * Receive notification of the start of an element.
      *
      * <p>
      * By default, do nothing. Application writers may override this method in a
      * subclass to take specific actions at the start of each element (such as
      * allocating a new tree node or writing output to a file).
      * </p>
      *
      * @param uri
+     *            The namespace uri, or the empty string, if no namespace is available
      * @param localName
      *            The element type name.
      * @param qName
+     *            The qualified name, or the empty string (must not be <code>null</code>)
      * @param attributes
      *            The specified or defaulted attributes.
      * @exception org.xml.sax.SAXException
      *                Any SAX exception, possibly wrapping another exception.
      * @see org.xml.sax.ContentHandler#startElement
      */
     @Override
     public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (qName.equals(Constants.STATUS)) {
             status = factory.createStatus();
             stacktree.push(status);
         } else if (qName.equals(Constants.JVM)) {
             jvm = factory.createJvm();
             if (stacktree.peek() instanceof Status) {
                 status.setJvm(jvm);
                 stacktree.push(jvm);
             }
         } else if (qName.equals(Constants.MEMORY)) {
             memory = factory.createMemory();
             if (stacktree.peek() instanceof Jvm) {
                 stacktree.push(memory);
                 if (attributes != null) {
                     for (int idx = 0; idx < attributes.getLength(); idx++) {
                         String attr = attributes.getQName(idx);
                         if (attr.equals(Constants.MEMORY_FREE)) {
                             memory.setFree(parseLong(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.MEMORY_TOTAL)) {
                             memory.setTotal(parseLong(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.MEMORY_MAX)) {
                             memory.setMax(parseLong(attributes.getValue(idx)));
                         }
                     }
                 }
                 jvm.setMemory(memory);
             }
         } else if (qName.equals(Constants.CONNECTOR)) {
             connector = factory.createConnector();
             if (stacktree.peek() instanceof Status || stacktree.peek() instanceof Connector) {
                 status.addConnector(connector);
                 stacktree.push(connector);
                 if (attributes != null) {
                     for (int idx = 0; idx < attributes.getLength(); idx++) {
                         String attr = attributes.getQName(idx);
                         if (attr.equals(Constants.ATTRIBUTE_NAME)) {
                             connector.setName(attributes.getValue(idx));
                         }
                     }
                 }
             }
         } else if (qName.equals(Constants.THREADINFO)) {
             threadinfo = factory.createThreadInfo();
             if (stacktree.peek() instanceof Connector) {
                 stacktree.push(threadinfo);
                 connector.setThreadInfo(threadinfo);
                 if (attributes != null) {
                     for (int idx = 0; idx < attributes.getLength(); idx++) {
                         String attr = attributes.getQName(idx);
                         if (attr.equals(Constants.MAXTHREADS)) {
                             threadinfo.setMaxThreads(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.MINSPARETHREADS)) {
                             threadinfo.setMinSpareThreads(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.MAXSPARETHREADS)) {
                             threadinfo.setMaxSpareThreads(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.CURRENTTHREADCOUNT)) {
                             threadinfo.setCurrentThreadCount(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.CURRENTBUSYTHREADS)) {
                             threadinfo.setCurrentThreadsBusy(parseInt(attributes.getValue(idx)));
                         }
                     }
                 }
             }
         } else if (qName.equals(Constants.REQUESTINFO)) {
             requestinfo = factory.createRequestInfo();
             if (stacktree.peek() instanceof Connector) {
                 stacktree.push(requestinfo);
                 connector.setRequestInfo(requestinfo);
                 if (attributes != null) {
                     for (int idx = 0; idx < attributes.getLength(); idx++) {
                         String attr = attributes.getQName(idx);
                         if (attr.equals(Constants.MAXTIME)) {
                             requestinfo.setMaxTime(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.PROCESSINGTIME)) {
                             requestinfo.setProcessingTime(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.REQUESTCOUNT)) {
                             requestinfo.setRequestCount(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.ERRORCOUNT)) {
                             requestinfo.setErrorCount(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.BYTESRECEIVED)) {
                             requestinfo.setBytesReceived(parseLong(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.BYTESSENT)) {
                             requestinfo.setBytesSent(parseLong(attributes.getValue(idx)));
                         }
                     }
                 }
             }
         } else if (qName.equals(Constants.WORKERS)) {
             workers = factory.createWorkers();
             if (stacktree.peek() instanceof Connector) {
                 connector.setWorkers(workers);
                 stacktree.push(workers);
             }
         } else if (qName.equals(Constants.WORKER)) {
             worker = factory.createWorker();
             if (stacktree.peek() instanceof Workers || stacktree.peek() instanceof Worker) {
                 stacktree.push(worker);
                 ((WorkersImpl) workers).addWorker(worker);
                 if (attributes != null) {
                     for (int idx = 0; idx < attributes.getLength(); idx++) {
                         String attr = attributes.getQName(idx);
                         if (attr.equals(Constants.STAGE)) {
                             worker.setStage(attributes.getValue(idx));
                         } else if (attr.equals(Constants.REQUESTPROCESSINGTIME)) {
                             worker.setRequestProcessingTime(parseInt(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.REQUESTBYTESSENT)) {
                             worker.setRequestBytesSent(parseLong(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.REQUESTBYTESRECEIVED)) {
                             worker.setRequestBytesReceived(parseLong(attributes.getValue(idx)));
                         } else if (attr.equals(Constants.REMOTEADDR)) {
                             worker.setRemoteAddr(attributes.getValue(idx));
                         } else if (attr.equals(Constants.VIRTUALHOST)) {
                             worker.setVirtualHost(attributes.getValue(idx));
                         } else if (attr.equals(Constants.METHOD)) {
                             worker.setMethod(attributes.getValue(idx));
                         } else if (attr.equals(Constants.CURRENTURI)) {
                             worker.setCurrentUri(attributes.getValue(idx));
                         } else if (attr.equals(Constants.CURRENTQUERYSTRING)) {
                             worker.setCurrentQueryString(attributes.getValue(idx));
                         } else if (attr.equals(Constants.PROTOCOL)) {
                             worker.setProtocol(attributes.getValue(idx));
                         }
                     }
                 }
             }
         }
     }
 
     /**
      * Receive notification of the end of an element.
      *
      * <p>
      * By default, do nothing. Application writers may override this method in a
      * subclass to take specific actions at the end of each element (such as
      * finalising a tree node or writing output to a file).
      * </p>
      *
      * @param uri
+     *            the namespace uri, or the empty string, if no namespace is available
      * @param localName
      *            The element type name.
      * @param qName
      *            The specified or defaulted attributes.
      * @exception org.xml.sax.SAXException
      *                Any SAX exception, possibly wrapping another exception.
      * @see org.xml.sax.ContentHandler#endElement
      */
     @Override
     public void endElement(String uri, String localName, String qName) throws SAXException {
         if (qName.equals(Constants.STATUS)) {
             if (stacktree.peek() instanceof Status) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.JVM)) {
             if (stacktree.peek() instanceof Jvm) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.MEMORY)) {
             if (stacktree.peek() instanceof Memory) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.CONNECTOR)) {
             if (stacktree.peek() instanceof Connector || stacktree.peek() instanceof Connector) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.THREADINFO)) {
             if (stacktree.peek() instanceof ThreadInfo) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.REQUESTINFO)) {
             if (stacktree.peek() instanceof RequestInfo) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.WORKERS)) {
             if (stacktree.peek() instanceof Workers) {
                 stacktree.pop();
             }
         } else if (qName.equals(Constants.WORKER)) {
             if (stacktree.peek() instanceof Worker || stacktree.peek() instanceof Worker) {
                 stacktree.pop();
             }
         }
     }
 
     /**
      * Receive notification of character data inside an element.
      *
      * <p>
      * By default, do nothing. Application writers may override this method to
      * take specific actions for each chunk of character data (such as adding
      * the data to a node or buffer, or printing it to a file).
      * </p>
      *
      * @param ch
      *            The characters.
      * @param start
      *            The start position in the character array.
      * @param length
      *            The number of characters to use from the character array.
      * @exception org.xml.sax.SAXException
      *                Any SAX exception, possibly wrapping another exception.
      * @see org.xml.sax.ContentHandler#characters
      */
     @Override
     public void characters(char ch[], int start, int length) throws SAXException {
     }
 
     /**
      * Convienance method for parsing long. If the string was not a number, the
      * method returns zero.
      *
-     * @param data
+     * @param data string representation of a {@link Long}
      * @return the value as a long
      */
     public long parseLong(String data) {
         long val = 0;
         if (data.length() > 0) {
             try {
                 val = Long.parseLong(data);
             } catch (NumberFormatException e) {
                 val = 0;
             }
         }
         return val;
     }
 
     /**
      * Convienance method for parsing integers.
      *
-     * @param data
+     * @param data string representation of an {@link Integer}
      * @return the value as an integer
      */
     public int parseInt(String data) {
         int val = 0;
         if (data.length() > 0) {
             try {
                 val = Integer.parseInt(data);
             } catch (NumberFormatException e) {
                 val = 0;
             }
         }
         return val;
     }
 
     /**
      * method returns the status object.
      *
      * @return the status
      */
     public Status getContents() {
         return this.status;
     }
 }
diff --git a/src/monitor/model/org/apache/jmeter/monitor/parser/ParserImpl.java b/src/monitor/model/org/apache/jmeter/monitor/parser/ParserImpl.java
index 328bb439b..60e2e27b3 100644
--- a/src/monitor/model/org/apache/jmeter/monitor/parser/ParserImpl.java
+++ b/src/monitor/model/org/apache/jmeter/monitor/parser/ParserImpl.java
@@ -1,122 +1,125 @@
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
 package org.apache.jmeter.monitor.parser;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.StringReader;
 
 import org.xml.sax.SAXException;
 import org.xml.sax.InputSource;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.parsers.SAXParser;
 import javax.xml.parsers.SAXParserFactory;
 
 import org.apache.jmeter.monitor.model.ObjectFactory;
 import org.apache.jmeter.monitor.model.Status;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public abstract class ParserImpl implements Parser {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private final SAXParser PARSER;
 
     private final MonitorHandler DOCHANDLER;
 
     private final ObjectFactory FACTORY;
 
     /**
-     *
+     * @param factory {@link ObjectFactory} to use
      */
     public ParserImpl(ObjectFactory factory) {
         super();
         this.FACTORY = factory;
         SAXParser parser = null;
         MonitorHandler handler = null;
         try {
             SAXParserFactory parserFactory = SAXParserFactory.newInstance();
             parser = parserFactory.newSAXParser();
             handler = new MonitorHandler(this.FACTORY);
         } catch (SAXException e) {
             log.error("Failed to create the parser",e);
         } catch (ParserConfigurationException e) {
             log.error("Failed to create the parser",e);
         }
         PARSER = parser;
         DOCHANDLER = handler;
     }
 
     /**
      * parse byte array and return Status object
      *
-     * @param bytes
+     * @param bytes bytes to be parsed
      * @return Status
      */
     @Override
     public Status parseBytes(byte[] bytes) {
         try {
             InputSource is = new InputSource();
             is.setByteStream(new ByteArrayInputStream(bytes));
             PARSER.parse(is, DOCHANDLER);
             return DOCHANDLER.getContents();
         } catch (SAXException e) {
             log.error("Failed to parse the bytes",e);
             // let bad input fail silently
             return DOCHANDLER.getContents();
         } catch (IOException e) { // Should never happen
             log.error("Failed to read the bytes",e);
             // let bad input fail silently
             return DOCHANDLER.getContents();
         }
     }
 
     /**
-     * @param content
+     * @param content text to be parsed
      * @return Status
      */
     @Override
     public Status parseString(String content) {
         try {
             InputSource is = new InputSource();
             is.setCharacterStream(new StringReader(content));
             PARSER.parse(is, DOCHANDLER);
             return DOCHANDLER.getContents();
         } catch (SAXException e) {
             log.error("Failed to parse the String",e);
             // let bad input fail silently
             return DOCHANDLER.getContents();
         } catch (IOException e) { // Should never happen
             log.error("Failed to read the String",e);
             // let bad input fail silently
             return DOCHANDLER.getContents();
         }
     }
 
     /**
      * @param result
+     *            {@link SampleResult} out of which the
+     *            {@link SampleResult#getResponseData() reponseData} will be
+     *            used for parsing
      * @return Status
      */
     @Override
     public Status parseSampleResult(SampleResult result) {
         return parseBytes(result.getResponseData());
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
index a9ac915b7..b8dc13465 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/proxy/ProxyControl.java
@@ -1,1517 +1,1519 @@
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
 
 package org.apache.jmeter.protocol.http.proxy;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.InvocationTargetException;
 import java.net.MalformedURLException;
 import java.security.GeneralSecurityException;
 import java.security.KeyStore;
 import java.security.UnrecoverableKeyException;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.prefs.Preferences;
 
 import org.apache.commons.codec.binary.Base64;
 import org.apache.commons.codec.digest.DigestUtils;
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.RandomStringUtils;
 import org.apache.commons.lang3.time.DateUtils;
 import org.apache.http.conn.ssl.AbstractVerifier;
 import org.apache.jmeter.assertions.ResponseAssertion;
 import org.apache.jmeter.assertions.gui.AssertionGui;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.control.GenericController;
 import org.apache.jmeter.control.TransactionController;
 import org.apache.jmeter.control.gui.LogicControllerGui;
 import org.apache.jmeter.control.gui.TransactionControllerGui;
 import org.apache.jmeter.engine.util.ValueReplacer;
 import org.apache.jmeter.exceptions.IllegalUserActionException;
 import org.apache.jmeter.functions.InvalidVariableException;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.gui.tree.JMeterTreeModel;
 import org.apache.jmeter.gui.tree.JMeterTreeNode;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.Header;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.control.RecordingController;
 import org.apache.jmeter.protocol.http.gui.AuthPanel;
 import org.apache.jmeter.protocol.http.gui.HeaderPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.WorkBench;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.threads.AbstractThreadGroup;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.exec.KeyToolUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 
 //For unit tests, @see TestProxyControl
 
 /**
  * Class handles storing of generated samples, etc
  */
 public class ProxyControl extends GenericController {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final String ASSERTION_GUI = AssertionGui.class.getName();
 
 
     private static final String TRANSACTION_CONTROLLER_GUI = TransactionControllerGui.class.getName();
 
     private static final String LOGIC_CONTROLLER_GUI = LogicControllerGui.class.getName();
 
     private static final String HEADER_PANEL = HeaderPanel.class.getName();
 
     private static final String AUTH_PANEL = AuthPanel.class.getName();
 
     private static final String AUTH_MANAGER = AuthManager.class.getName();
 
     public static final int DEFAULT_PORT = 8080;
 
     // and as a string
     public static final String DEFAULT_PORT_S =
         Integer.toString(DEFAULT_PORT);// Used by GUI
 
     //+ JMX file attributes
     private static final String PORT = "ProxyControlGui.port"; // $NON-NLS-1$
 
     private static final String DOMAINS = "ProxyControlGui.domains"; // $NON-NLS-1$
 
     private static final String EXCLUDE_LIST = "ProxyControlGui.exclude_list"; // $NON-NLS-1$
 
     private static final String INCLUDE_LIST = "ProxyControlGui.include_list"; // $NON-NLS-1$
 
     private static final String CAPTURE_HTTP_HEADERS = "ProxyControlGui.capture_http_headers"; // $NON-NLS-1$
 
     private static final String ADD_ASSERTIONS = "ProxyControlGui.add_assertion"; // $NON-NLS-1$
 
     private static final String GROUPING_MODE = "ProxyControlGui.grouping_mode"; // $NON-NLS-1$
 
     private static final String SAMPLER_TYPE_NAME = "ProxyControlGui.sampler_type_name"; // $NON-NLS-1$
 
     private static final String SAMPLER_REDIRECT_AUTOMATICALLY = "ProxyControlGui.sampler_redirect_automatically"; // $NON-NLS-1$
 
     private static final String SAMPLER_FOLLOW_REDIRECTS = "ProxyControlGui.sampler_follow_redirects"; // $NON-NLS-1$
 
     private static final String USE_KEEPALIVE = "ProxyControlGui.use_keepalive"; // $NON-NLS-1$
 
     private static final String SAMPLER_DOWNLOAD_IMAGES = "ProxyControlGui.sampler_download_images"; // $NON-NLS-1$
 
     private static final String REGEX_MATCH = "ProxyControlGui.regex_match"; // $NON-NLS-1$
 
     private static final String CONTENT_TYPE_EXCLUDE = "ProxyControlGui.content_type_exclude"; // $NON-NLS-1$
 
     private static final String CONTENT_TYPE_INCLUDE = "ProxyControlGui.content_type_include"; // $NON-NLS-1$
 
     private static final String NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED = "ProxyControlGui.notify_child_sl_filtered"; // $NON-NLS-1$
 
     private static final String BASIC_AUTH = "Basic"; // $NON-NLS-1$
 
     private static final String DIGEST_AUTH = "Digest"; // $NON-NLS-1$
 
     //- JMX file attributes
 
     // Must agree with the order of entries in the drop-down
     // created in ProxyControlGui.createGroupingPanel()
     //private static final int GROUPING_NO_GROUPS = 0;
     private static final int GROUPING_ADD_SEPARATORS = 1;
     private static final int GROUPING_IN_SIMPLE_CONTROLLERS = 2;
     private static final int GROUPING_STORE_FIRST_ONLY = 3;
     private static final int GROUPING_IN_TRANSACTION_CONTROLLERS = 4;
 
     // Original numeric order (we now use strings)
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_JAVA = "0";
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_HC3_1 = "1";
     private static final String SAMPLER_TYPE_HTTP_SAMPLER_HC4 = "2";
 
     private static final long sampleGap =
         JMeterUtils.getPropDefault("proxy.pause", 5000); // $NON-NLS-1$
     // Detect if user has pressed a new link
 
     // for ssl connection
     private static final String KEYSTORE_TYPE =
         JMeterUtils.getPropDefault("proxy.cert.type", "JKS"); // $NON-NLS-1$ $NON-NLS-2$
 
     // Proxy configuration SSL
     private static final String CERT_DIRECTORY =
         JMeterUtils.getPropDefault("proxy.cert.directory", JMeterUtils.getJMeterBinDir()); // $NON-NLS-1$
 
     private static final String CERT_FILE_DEFAULT = "proxyserver.jks";// $NON-NLS-1$
 
     private static final String CERT_FILE =
         JMeterUtils.getPropDefault("proxy.cert.file", CERT_FILE_DEFAULT); // $NON-NLS-1$
 
     private static final File CERT_PATH = new File(CERT_DIRECTORY, CERT_FILE);
 
     private static final String CERT_PATH_ABS = CERT_PATH.getAbsolutePath();
 
     private static final String DEFAULT_PASSWORD = "password"; // $NON-NLS-1$
 
     // Keys for user preferences
     private static final String USER_PASSWORD_KEY = "proxy_cert_password";
 
     private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ProxyControl.class);
     // Note: Windows user preferences are stored relative to: HKEY_CURRENT_USER\Software\JavaSoft\Prefs
 
     // Whether to use dymanic key generation (if supported)
     private static final boolean USE_DYNAMIC_KEYS = JMeterUtils.getPropDefault("proxy.cert.dynamic_keys", true); // $NON-NLS-1$;
 
     // The alias to be used if dynamic host names are not possible
     static final String JMETER_SERVER_ALIAS = ":jmeter:"; // $NON-NLS-1$
 
     static final int CERT_VALIDITY = JMeterUtils.getPropDefault("proxy.cert.validity", 7); // $NON-NLS-1$
 
     // If this is defined, it is assumed to be the alias of a user-supplied certificate; overrides dynamic mode
     static final String CERT_ALIAS = JMeterUtils.getProperty("proxy.cert.alias"); // $NON-NLS-1$
 
     public static enum KeystoreMode {
         USER_KEYSTORE,   // user-provided keystore
         JMETER_KEYSTORE, // keystore generated by JMeter; single entry
         DYNAMIC_KEYSTORE, // keystore generated by JMeter; dynamic entries
         NONE             // cannot use keystore
     }
 
     static final KeystoreMode KEYSTORE_MODE;
 
     static {
         if (CERT_ALIAS != null) {
             KEYSTORE_MODE = KeystoreMode.USER_KEYSTORE;
             log.info("HTTP(S) Test Script Recorder will use the keystore '"+ CERT_PATH_ABS + "' with the alias: '" + CERT_ALIAS + "'");
         } else {
             if (!KeyToolUtils.haveKeytool()) {
                 KEYSTORE_MODE = KeystoreMode.NONE;
             } else if (KeyToolUtils.SUPPORTS_HOST_CERT && USE_DYNAMIC_KEYS) {
                 KEYSTORE_MODE = KeystoreMode.DYNAMIC_KEYSTORE;
                 log.info("HTTP(S) Test Script Recorder SSL Proxy will use keys that support embedded 3rd party resources in file " + CERT_PATH_ABS);
             } else {
                 KEYSTORE_MODE = KeystoreMode.JMETER_KEYSTORE;
                 log.warn("HTTP(S) Test Script Recorder SSL Proxy will use keys that may not work for embedded resources in file " + CERT_PATH_ABS);
             }
         }
     }
 
     // Whether to use the redirect disabling feature (can be switched off if it does not work)
     private static final boolean ATTEMPT_REDIRECT_DISABLING =
             JMeterUtils.getPropDefault("proxy.redirect.disabling", true); // $NON-NLS-1$
 
     // Although this field is mutable, it is only accessed within the synchronized method deliverSampler()
     private static String LAST_REDIRECT = null;
     /*
      * TODO this assumes that the redirected response will always immediately follow the original response.
      * This may not always be true.
      * Is there a better way to do this?
      */
 
     private transient Daemon server;
 
     private long lastTime = 0;// When was the last sample seen?
 
     private transient KeyStore keyStore;
 
     private volatile boolean addAssertions = false;
 
     private volatile int groupingMode = 0;
 
     private volatile boolean samplerRedirectAutomatically = false;
 
     private volatile boolean samplerFollowRedirects = false;
 
     private volatile boolean useKeepAlive = false;
 
     private volatile boolean samplerDownloadImages = false;
 
     private volatile boolean notifyChildSamplerListenersOfFilteredSamples = true;
 
     private volatile boolean regexMatch = false;// Should we match using regexes?
 
     /**
      * Tree node where the samples should be stored.
      * <p>
      * This property is not persistent.
      */
     private JMeterTreeNode target;
 
     private String storePassword;
 
     private String keyPassword;
 
     public ProxyControl() {
         setPort(DEFAULT_PORT);
         setExcludeList(new HashSet<String>());
         setIncludeList(new HashSet<String>());
         setCaptureHttpHeaders(true); // maintain original behaviour
     }
 
     public void setPort(int port) {
         this.setProperty(new IntegerProperty(PORT, port));
     }
 
     public void setPort(String port) {
         setProperty(PORT, port);
     }
 
     public void setSslDomains(String domains) {
         setProperty(DOMAINS, domains, "");
     }
 
     public String getSslDomains() {
         return getPropertyAsString(DOMAINS,"");
     }
 
     public void setCaptureHttpHeaders(boolean capture) {
         setProperty(new BooleanProperty(CAPTURE_HTTP_HEADERS, capture));
     }
 
     public void setGroupingMode(int grouping) {
         this.groupingMode = grouping;
         setProperty(new IntegerProperty(GROUPING_MODE, grouping));
     }
 
     public void setAssertions(boolean b) {
         addAssertions = b;
         setProperty(new BooleanProperty(ADD_ASSERTIONS, b));
     }
 
     @Deprecated
     public void setSamplerTypeName(int samplerTypeName) {
         setProperty(new IntegerProperty(SAMPLER_TYPE_NAME, samplerTypeName));
     }
 
     public void setSamplerTypeName(String samplerTypeName) {
         setProperty(new StringProperty(SAMPLER_TYPE_NAME, samplerTypeName));
     }
     public void setSamplerRedirectAutomatically(boolean b) {
         samplerRedirectAutomatically = b;
         setProperty(new BooleanProperty(SAMPLER_REDIRECT_AUTOMATICALLY, b));
     }
 
     public void setSamplerFollowRedirects(boolean b) {
         samplerFollowRedirects = b;
         setProperty(new BooleanProperty(SAMPLER_FOLLOW_REDIRECTS, b));
     }
 
     /**
-     * @param b
+     * @param b flag whether keep alive should be used
      */
     public void setUseKeepAlive(boolean b) {
         useKeepAlive = b;
         setProperty(new BooleanProperty(USE_KEEPALIVE, b));
     }
 
     public void setSamplerDownloadImages(boolean b) {
         samplerDownloadImages = b;
         setProperty(new BooleanProperty(SAMPLER_DOWNLOAD_IMAGES, b));
     }
 
     public void setNotifyChildSamplerListenerOfFilteredSamplers(boolean b) {
         notifyChildSamplerListenersOfFilteredSamples = b;
         setProperty(new BooleanProperty(NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED, b));
     }
 
     public void setIncludeList(Collection<String> list) {
         setProperty(new CollectionProperty(INCLUDE_LIST, new HashSet<String>(list)));
     }
 
     public void setExcludeList(Collection<String> list) {
         setProperty(new CollectionProperty(EXCLUDE_LIST, new HashSet<String>(list)));
     }
 
     /**
-     * @param b
+     * @param b flag whether regex matching should be used
      */
     public void setRegexMatch(boolean b) {
         regexMatch = b;
         setProperty(new BooleanProperty(REGEX_MATCH, b));
     }
 
     public void setContentTypeExclude(String contentTypeExclude) {
         setProperty(new StringProperty(CONTENT_TYPE_EXCLUDE, contentTypeExclude));
     }
 
     public void setContentTypeInclude(String contentTypeInclude) {
         setProperty(new StringProperty(CONTENT_TYPE_INCLUDE, contentTypeInclude));
     }
 
     public boolean getAssertions() {
         return getPropertyAsBoolean(ADD_ASSERTIONS);
     }
 
     public int getGroupingMode() {
         return getPropertyAsInt(GROUPING_MODE);
     }
 
     public int getPort() {
         return getPropertyAsInt(PORT);
     }
 
     public String getPortString() {
         return getPropertyAsString(PORT);
     }
 
     public int getDefaultPort() {
         return DEFAULT_PORT;
     }
 
     public boolean getCaptureHttpHeaders() {
         return getPropertyAsBoolean(CAPTURE_HTTP_HEADERS);
     }
 
     public String getSamplerTypeName() {
         // Convert the old numeric types - just in case someone wants to reload the workbench
         String type = getPropertyAsString(SAMPLER_TYPE_NAME);
         if (SAMPLER_TYPE_HTTP_SAMPLER_JAVA.equals(type)){
             type = HTTPSamplerFactory.IMPL_JAVA;
         } else if (SAMPLER_TYPE_HTTP_SAMPLER_HC3_1.equals(type)){
             type = HTTPSamplerFactory.IMPL_HTTP_CLIENT3_1;
         } else if (SAMPLER_TYPE_HTTP_SAMPLER_HC4.equals(type)){
             type = HTTPSamplerFactory.IMPL_HTTP_CLIENT4;
         }
         return type;
     }
 
     public boolean getSamplerRedirectAutomatically() {
         return getPropertyAsBoolean(SAMPLER_REDIRECT_AUTOMATICALLY, false);
     }
 
     public boolean getSamplerFollowRedirects() {
         return getPropertyAsBoolean(SAMPLER_FOLLOW_REDIRECTS, true);
     }
 
     public boolean getUseKeepalive() {
         return getPropertyAsBoolean(USE_KEEPALIVE, true);
     }
 
     public boolean getSamplerDownloadImages() {
         return getPropertyAsBoolean(SAMPLER_DOWNLOAD_IMAGES, false);
     }
 
     public boolean getNotifyChildSamplerListenerOfFilteredSamplers() {
         return getPropertyAsBoolean(NOTIFY_CHILD_SAMPLER_LISTENERS_FILTERED, true);
     }
 
     public boolean getRegexMatch() {
         return getPropertyAsBoolean(REGEX_MATCH, false);
     }
 
     public String getContentTypeExclude() {
         return getPropertyAsString(CONTENT_TYPE_EXCLUDE);
     }
 
     public String getContentTypeInclude() {
         return getPropertyAsString(CONTENT_TYPE_INCLUDE);
     }
 
     public void addConfigElement(ConfigElement config) {
         // NOOP
     }
 
     public void startProxy() throws IOException {
         try {
             initKeyStore(); // TODO display warning dialog as this can take some time
         } catch (GeneralSecurityException e) {
             log.error("Could not initialise key store", e);
             throw new IOException("Could not create keystore", e);
         } catch (IOException e) { // make sure we log the error
             log.error("Could not initialise key store", e);
             throw e;
         }
         notifyTestListenersOfStart();
         try {
             server = new Daemon(getPort(), this);
             server.start();
             GuiPackage.getInstance().register(server);
         } catch (IOException e) {
             log.error("Could not create Proxy daemon", e);
             throw e;
         }
     }
 
     public void addExcludedPattern(String pattern) {
         getExcludePatterns().addItem(pattern);
     }
 
     public CollectionProperty getExcludePatterns() {
         return (CollectionProperty) getProperty(EXCLUDE_LIST);
     }
 
     public void addIncludedPattern(String pattern) {
         getIncludePatterns().addItem(pattern);
     }
 
     public CollectionProperty getIncludePatterns() {
         return (CollectionProperty) getProperty(INCLUDE_LIST);
     }
 
     public void clearExcludedPatterns() {
         getExcludePatterns().clear();
     }
 
     public void clearIncludedPatterns() {
         getIncludePatterns().clear();
     }
 
     /**
      * @return the target controller node
      */
     public JMeterTreeNode getTarget() {
         return target;
     }
 
     /**
      * Sets the target node where the samples generated by the proxy have to be
      * stored.
+     * 
+     * @param target target node to store generated samples
      */
     public void setTarget(JMeterTreeNode target) {
         this.target = target;
     }
 
     /**
      * Receives the recorded sampler from the proxy server for placing in the
      * test tree; this is skipped if the sampler is null (e.g. for recording SSL errors)
      * Always sends the result to any registered sample listeners.
      *
      * @param sampler the sampler, may be null
      * @param subConfigs the configuration elements to be added (e.g. header namager)
      * @param result the sample result, not null
      * TODO param serverResponse to be added to allow saving of the
      * server's response while recording.
      */
     public synchronized void deliverSampler(final HTTPSamplerBase sampler, final TestElement[] subConfigs, final SampleResult result) {
         boolean notifySampleListeners = true;
         if (sampler != null) {
             if (ATTEMPT_REDIRECT_DISABLING && (samplerRedirectAutomatically || samplerFollowRedirects)) {
                 if (result instanceof HTTPSampleResult) {
                     final HTTPSampleResult httpSampleResult = (HTTPSampleResult) result;
                     final String urlAsString = httpSampleResult.getUrlAsString();
                     if (urlAsString.equals(LAST_REDIRECT)) { // the url matches the last redirect
                         sampler.setEnabled(false);
                         sampler.setComment("Detected a redirect from the previous sample");
                     } else { // this is not the result of a redirect
                         LAST_REDIRECT = null; // so break the chain
                     }
                     if (httpSampleResult.isRedirect()) { // Save Location so resulting sample can be disabled
                         if (LAST_REDIRECT == null) {
                             sampler.setComment("Detected the start of a redirect chain");
                         }
                         LAST_REDIRECT = httpSampleResult.getRedirectLocation();
                     } else {
                         LAST_REDIRECT = null;
                     }
                 }
             }
             if (filterContentType(result) && filterUrl(sampler)) {
                 JMeterTreeNode myTarget = findTargetControllerNode();
                 @SuppressWarnings("unchecked") // OK, because find only returns correct element types
                 Collection<ConfigTestElement> defaultConfigurations = (Collection<ConfigTestElement>) findApplicableElements(myTarget, ConfigTestElement.class, false);
                 @SuppressWarnings("unchecked") // OK, because find only returns correct element types
                 Collection<Arguments> userDefinedVariables = (Collection<Arguments>) findApplicableElements(myTarget, Arguments.class, true);
 
                 removeValuesFromSampler(sampler, defaultConfigurations);
                 replaceValues(sampler, subConfigs, userDefinedVariables);
                 sampler.setAutoRedirects(samplerRedirectAutomatically);
                 sampler.setFollowRedirects(samplerFollowRedirects);
                 sampler.setUseKeepAlive(useKeepAlive);
                 sampler.setImageParser(samplerDownloadImages);
 
                 Authorization authorization = createAuthorization(subConfigs, sampler);
                 if (authorization != null) {
                     setAuthorization(authorization, myTarget);
                 }
                 placeSampler(sampler, subConfigs, myTarget);
             } else {
                 if(log.isDebugEnabled()) {
                     log.debug("Sample excluded based on url or content-type: " + result.getUrlAsString() + " - " + result.getContentType());
                 }
                 notifySampleListeners = notifyChildSamplerListenersOfFilteredSamples;
                 result.setSampleLabel("["+result.getSampleLabel()+"]");
             }
         }
         if(notifySampleListeners) {
             // SampleEvent is not passed JMeterVariables, because they don't make sense for Proxy Recording
             notifySampleListeners(new SampleEvent(result, "WorkBench")); // TODO - is this the correct threadgroup name?
         } else {
             log.debug("Sample not delivered to Child Sampler Listener based on url or content-type: " + result.getUrlAsString() + " - " + result.getContentType());
         }
     }
 
     /**
      * Detect Header manager in subConfigs,
      * Find(if any) Authorization header
      * Construct Authentication object
      * Removes Authorization if present 
      *
      * @param subConfigs {@link TestElement}[]
      * @param sampler {@link HTTPSamplerBase}
      * @return {@link Authorization}
      */
     private Authorization createAuthorization(final TestElement[] subConfigs, HTTPSamplerBase sampler) {
         Header authHeader = null;
         Authorization authorization = null;
         // Iterate over subconfig elements searching for HeaderManager
         for (TestElement te : subConfigs) {
             if (te instanceof HeaderManager) {
                 List<TestElementProperty> headers = (ArrayList<TestElementProperty>) ((HeaderManager) te).getHeaders().getObjectValue();
                 for (Iterator<?> iterator = headers.iterator(); iterator.hasNext();) {
                     TestElementProperty tep = (TestElementProperty) iterator
                             .next();
                     if (tep.getName().equals(HTTPConstants.HEADER_AUTHORIZATION)) {
                         //Construct Authorization object from HEADER_AUTHORIZATION
                         authHeader = (Header) tep.getObjectValue();
                         String[] authHeaderContent = authHeader.getValue().split(" ");//$NON-NLS-1$
                         String authType = null;
                         String authCredentialsBase64 = null;
                         if(authHeaderContent.length>=2) {
                             authType = authHeaderContent[0];
                             authCredentialsBase64 = authHeaderContent[1];
                             authorization=new Authorization();
                             try {
                                 authorization.setURL(sampler.getUrl().toExternalForm());
                             } catch (MalformedURLException e) {
                                 log.error("Error filling url on authorization, message:"+e.getMessage(), e);
                                 authorization.setURL("${AUTH_BASE_URL}");//$NON-NLS-1$
                             }
                             // if HEADER_AUTHORIZATION contains "Basic"
                             // then set Mechanism.BASIC_DIGEST, otherwise Mechanism.KERBEROS
                             authorization.setMechanism(
                                     authType.equals(BASIC_AUTH)||authType.equals(DIGEST_AUTH)?
                                     AuthManager.Mechanism.BASIC_DIGEST:
                                     AuthManager.Mechanism.KERBEROS);
                             if(BASIC_AUTH.equals(authType)) {
                                 String authCred= new String(Base64.decodeBase64(authCredentialsBase64));
                                 String[] loginPassword = authCred.split(":"); //$NON-NLS-1$
                                 authorization.setUser(loginPassword[0]);
                                 authorization.setPass(loginPassword[1]);
                             } else {
                                 // Digest or Kerberos
                                 authorization.setUser("${AUTH_LOGIN}");//$NON-NLS-1$
                                 authorization.setPass("${AUTH_PASSWORD}");//$NON-NLS-1$
                                 
                             }
                         }
                         // remove HEADER_AUTHORIZATION from HeaderManager 
                         // because it's useless after creating Authorization object
                         iterator.remove();
                     }
                 }
             }
         }
         return authorization;
     }
 
     public void stopProxy() {
         if (server != null) {
             server.stopServer();
             GuiPackage.getInstance().unregister(server);
             try {
                 server.join(1000); // wait for server to stop
             } catch (InterruptedException e) {
                 //NOOP
             }
             notifyTestListenersOfEnd();
             server = null;
         }
     }
 
     public String[] getCertificateDetails() {
         if (isDynamicMode()) {
             try {
                 X509Certificate caCert = (X509Certificate) keyStore.getCertificate(KeyToolUtils.getRootCAalias());
                 if (caCert == null) {
                     return new String[]{"Could not find certificate"};
                 }
                 return new String[]
                         {
                         caCert.getSubjectX500Principal().toString(),
                         "Fingerprint(SHA1): " + JOrphanUtils.baToHexString(DigestUtils.sha1(caCert.getEncoded()), ' '),
                         "Created: "+ caCert.getNotBefore().toString()
                         };
             } catch (GeneralSecurityException e) {
                 log.error("Problem reading root CA from keystore", e);
                 return new String[]{"Problem with root certificate", e.getMessage()};
             }
         }
         return null; // should not happen
     }
     // Package protected to allow test case access
     boolean filterUrl(HTTPSamplerBase sampler) {
         String domain = sampler.getDomain();
         if (domain == null || domain.length() == 0) {
             return false;
         }
 
         String url = generateMatchUrl(sampler);
         CollectionProperty includePatterns = getIncludePatterns();
         if (includePatterns.size() > 0) {
             if (!matchesPatterns(url, includePatterns)) {
                 return false;
             }
         }
 
         CollectionProperty excludePatterns = getExcludePatterns();
         if (excludePatterns.size() > 0) {
             if (matchesPatterns(url, excludePatterns)) {
                 return false;
             }
         }
 
         return true;
     }
 
     // Package protected to allow test case access
     /**
      * Filter the response based on the content type.
      * If no include nor exclude filter is specified, the result will be included
      *
      * @param result the sample result to check, true means result will be kept
      */
     boolean filterContentType(SampleResult result) {
         String includeExp = getContentTypeInclude();
         String excludeExp = getContentTypeExclude();
         // If no expressions are specified, we let the sample pass
         if((includeExp == null || includeExp.length() == 0) &&
                 (excludeExp == null || excludeExp.length() == 0)
                 )
         {
             return true;
         }
 
         // Check that we have a content type
         String sampleContentType = result.getContentType();
         if(sampleContentType == null || sampleContentType.length() == 0) {
             if(log.isDebugEnabled()) {
                 log.debug("No Content-type found for : " + result.getUrlAsString());
             }
 
             return true;
         }
 
         if(log.isDebugEnabled()) {
             log.debug("Content-type to filter : " + sampleContentType);
         }
 
         // Check if the include pattern is matched
         boolean matched = testPattern(includeExp, sampleContentType, true);
         if(!matched) {
             return false;
         }
 
         // Check if the exclude pattern is matched
         matched = testPattern(excludeExp, sampleContentType, false);
         if(!matched) {
             return false;
         }
 
         return true;
     }
 
     /**
      * Returns true if matching pattern was different from expectedToMatch
      * @param expression Expression to match
      * @param sampleContentType
      * @return boolean true if Matching expression
      */
     private final boolean testPattern(String expression, String sampleContentType, boolean expectedToMatch) {
         if(expression != null && expression.length() > 0) {
             if(log.isDebugEnabled()) {
                 log.debug("Testing Expression : " + expression + " on sampleContentType:"+sampleContentType+", expected to match:"+expectedToMatch);
             }
 
             Pattern pattern = null;
             try {
                 pattern = JMeterUtils.getPatternCache().getPattern(expression, Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
                 if(JMeterUtils.getMatcher().contains(sampleContentType, pattern) != expectedToMatch) {
                     return false;
                 }
             } catch (MalformedCachePatternException e) {
                 log.warn("Skipped invalid content pattern: " + expression, e);
             }
         }
         return true;
     }
 
     /**
      * Find if there is any AuthManager in JMeterTreeModel
      * If there is no one, create and add it to tree
      * Add authorization object to AuthManager
      * @param authorization {@link Authorization}
      * @param target {@link JMeterTreeNode}
      */
     private void setAuthorization(Authorization authorization, JMeterTreeNode target) {
         JMeterTreeModel jmeterTreeModel = GuiPackage.getInstance().getTreeModel();
         List<JMeterTreeNode> authManagerNodes = jmeterTreeModel.getNodesOfType(AuthManager.class);
         if (authManagerNodes.size() == 0) {
             try {
                 log.debug("Creating HTTP Authentication manager for authorization:"+authorization);
                 AuthManager authManager = newAuthorizationManager(authorization);
                 jmeterTreeModel.addComponent(authManager, target);
             } catch (IllegalUserActionException e) {
                 log.error("Failed to add Authorization Manager to target node:" + target.getName(), e);
             }
         } else{
             AuthManager authManager=(AuthManager)authManagerNodes.get(0).getTestElement();
             authManager.addAuth(authorization);
         }
     }
 
     /**
      * Helper method to add a Response Assertion
      * Called from AWT Event thread
      */
     private void addAssertion(JMeterTreeModel model, JMeterTreeNode node) throws IllegalUserActionException {
         ResponseAssertion ra = new ResponseAssertion();
         ra.setProperty(TestElement.GUI_CLASS, ASSERTION_GUI);
         ra.setName(JMeterUtils.getResString("assertion_title")); // $NON-NLS-1$
         ra.setTestFieldResponseData();
         model.addComponent(ra, node);
     }
 
     /**
      * Construct AuthManager
      * @param authorization
      * @return
      * @throws IllegalUserActionException
      */
     private AuthManager newAuthorizationManager(Authorization authorization) throws IllegalUserActionException {
         AuthManager authManager = new AuthManager();
         authManager.setProperty(TestElement.GUI_CLASS, AUTH_PANEL);
         authManager.setProperty(TestElement.TEST_CLASS, AUTH_MANAGER);
         authManager.setName("HTTP Authorization Manager");
         authManager.addAuth(authorization);
         return authManager;
     }
 
     /**
      * Helper method to add a Divider
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      */
     private void addDivider(final JMeterTreeModel model, final JMeterTreeNode node) {
         final GenericController sc = new GenericController();
         sc.setProperty(TestElement.GUI_CLASS, LOGIC_CONTROLLER_GUI);
         sc.setName("-------------------"); // $NON-NLS-1$
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                 try {
                     model.addComponent(sc, node);
                 } catch (IllegalUserActionException e) {
                     log.error("Program error", e);
                     throw new Error(e);
                 }
             }
         });
     }
 
     /**
      * Helper method to add a Simple Controller to contain the samplers.
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      * @param model
      *            Test component tree model
      * @param node
      *            Node in the tree where we will add the Controller
      * @param name
      *            A name for the Controller
      * @throws InvocationTargetException
      * @throws InterruptedException
      */
     private void addSimpleController(final JMeterTreeModel model, final JMeterTreeNode node, String name)
             throws InterruptedException, InvocationTargetException {
         final GenericController sc = new GenericController();
         sc.setProperty(TestElement.GUI_CLASS, LOGIC_CONTROLLER_GUI);
         sc.setName(name);
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                 try {
                     model.addComponent(sc, node);
                 } catch (IllegalUserActionException e) {
                      log.error("Program error", e);
                      throw new Error(e);
                 }
             }
         });
     }
 
     /**
      * Helper method to add a Transaction Controller to contain the samplers.
      * Called from Application Thread that needs to update GUI (JMeterTreeModel)
      * @param model
      *            Test component tree model
      * @param node
      *            Node in the tree where we will add the Controller
      * @param name
      *            A name for the Controller
      * @throws InvocationTargetException
      * @throws InterruptedException
      */
     private void addTransactionController(final JMeterTreeModel model, final JMeterTreeNode node, String name)
             throws InterruptedException, InvocationTargetException {
         final TransactionController sc = new TransactionController();
         sc.setIncludeTimers(false);
         sc.setProperty(TestElement.GUI_CLASS, TRANSACTION_CONTROLLER_GUI);
         sc.setName(name);
         JMeterUtils.runSafe(new Runnable() {
             @Override
             public void run() {
                  try {
                     model.addComponent(sc, node);
                 } catch (IllegalUserActionException e) {
                     log.error("Program error", e);
                     throw new Error(e);
                 }
             }
         });
     }
     /**
      * Helpler method to replicate any timers found within the Proxy Controller
      * into the provided sampler, while replacing any occurences of string _T_
      * in the timer's configuration with the provided deltaT.
      * Called from AWT Event thread
      * @param model
      *            Test component tree model
      * @param node
      *            Sampler node in where we will add the timers
      * @param deltaT
      *            Time interval from the previous request
      */
     private void addTimers(JMeterTreeModel model, JMeterTreeNode node, long deltaT) {
         TestPlan variables = new TestPlan();
         variables.addParameter("T", Long.toString(deltaT)); // $NON-NLS-1$
         ValueReplacer replacer = new ValueReplacer(variables);
         JMeterTreeNode mySelf = model.getNodeOf(this);
         Enumeration<JMeterTreeNode> children = mySelf.children();
         while (children.hasMoreElements()) {
             JMeterTreeNode templateNode = children.nextElement();
             if (templateNode.isEnabled()) {
                 TestElement template = templateNode.getTestElement();
                 if (template instanceof Timer) {
                     TestElement timer = (TestElement) template.clone();
                     try {
                         replacer.undoReverseReplace(timer);
                         model.addComponent(timer, node);
                     } catch (InvalidVariableException e) {
                         // Not 100% sure, but I believe this can't happen, so
                         // I'll log and throw an error:
                         log.error("Program error", e);
                         throw new Error(e);
                     } catch (IllegalUserActionException e) {
                         // Not 100% sure, but I believe this can't happen, so
                         // I'll log and throw an error:
                         log.error("Program error", e);
                         throw new Error(e);
                     }
                 }
             }
         }
     }
 
     /**
      * Finds the first enabled node of a given type in the tree.
      *
      * @param type
      *            class of the node to be found
      *
      * @return the first node of the given type in the test component tree, or
      *         <code>null</code> if none was found.
      */
     private JMeterTreeNode findFirstNodeOfType(Class<?> type) {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         List<JMeterTreeNode> nodes = treeModel.getNodesOfType(type);
         for (JMeterTreeNode node : nodes) {
             if (node.isEnabled()) {
                 return node;
             }
         }
         return null;
     }
 
     /**
      * Finds the controller where samplers have to be stored, that is:
      * <ul>
      * <li>The controller specified by the <code>target</code> property.
      * <li>If none was specified, the first RecordingController in the tree.
      * <li>If none is found, the first AbstractThreadGroup in the tree.
      * <li>If none is found, the Workspace.
      * </ul>
      *
      * @return the tree node for the controller where the proxy must store the
      *         generated samplers.
      */
     public JMeterTreeNode findTargetControllerNode() {
         JMeterTreeNode myTarget = getTarget();
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(RecordingController.class);
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(AbstractThreadGroup.class);
         if (myTarget != null) {
             return myTarget;
         }
         myTarget = findFirstNodeOfType(WorkBench.class);
         if (myTarget != null) {
             return myTarget;
         }
         log.error("Program error: test script recording target not found.");
         return null;
     }
 
     /**
      * Finds all configuration objects of the given class applicable to the
      * recorded samplers, that is:
      * <ul>
      * <li>All such elements directly within the HTTP(S) Test Script Recorder (these have
      * the highest priority).
      * <li>All such elements directly within the target controller (higher
      * priority) or directly within any containing controller (lower priority),
      * including the Test Plan itself (lowest priority).
      * </ul>
      *
      * @param myTarget
      *            tree node for the recording target controller.
      * @param myClass
      *            Class of the elements to be found.
      * @param ascending
      *            true if returned elements should be ordered in ascending
      *            priority, false if they should be in descending priority.
      *
      * @return a collection of applicable objects of the given class.
      */
     // TODO - could be converted to generic class?
     private Collection<?> findApplicableElements(JMeterTreeNode myTarget, Class<? extends TestElement> myClass, boolean ascending) {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         LinkedList<TestElement> elements = new LinkedList<TestElement>();
 
         // Look for elements directly within the HTTP proxy:
         Enumeration<?> kids = treeModel.getNodeOf(this).children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement element = (TestElement) subNode.getUserObject();
                 if (myClass.isInstance(element)) {
                     if (ascending) {
                         elements.addFirst(element);
                     } else {
                         elements.add(element);
                     }
                 }
             }
         }
 
         // Look for arguments elements in the target controller or higher up:
         for (JMeterTreeNode controller = myTarget; controller != null; controller = (JMeterTreeNode) controller
                 .getParent()) {
             kids = controller.children();
             while (kids.hasMoreElements()) {
                 JMeterTreeNode subNode = (JMeterTreeNode) kids.nextElement();
                 if (subNode.isEnabled()) {
                     TestElement element = (TestElement) subNode.getUserObject();
                     if (myClass.isInstance(element)) {
                         log.debug("Applicable: " + element.getName());
                         if (ascending) {
                             elements.addFirst(element);
                         } else {
                             elements.add(element);
                         }
                     }
 
                     // Special case for the TestPlan's Arguments sub-element:
                     if (element instanceof TestPlan) {
                         TestPlan tp = (TestPlan) element;
                         Arguments args = tp.getArguments();
                         if (myClass.isInstance(args)) {
                             if (ascending) {
                                 elements.addFirst(args);
                             } else {
                                 elements.add(args);
                             }
                         }
                     }
                 }
             }
         }
 
         return elements;
     }
 
     private void placeSampler(final HTTPSamplerBase sampler, final TestElement[] subConfigs,
             JMeterTreeNode myTarget) {
         try {
             final JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
 
             boolean firstInBatch = false;
             long now = System.currentTimeMillis();
             long deltaT = now - lastTime;
             int cachedGroupingMode = groupingMode;
             if (deltaT > sampleGap) {
                 if (!myTarget.isLeaf() && cachedGroupingMode == GROUPING_ADD_SEPARATORS) {
                     addDivider(treeModel, myTarget);
                 }
                 if (cachedGroupingMode == GROUPING_IN_SIMPLE_CONTROLLERS) {
                     addSimpleController(treeModel, myTarget, sampler.getName());
                 }
                 if (cachedGroupingMode == GROUPING_IN_TRANSACTION_CONTROLLERS) {
                     addTransactionController(treeModel, myTarget, sampler.getName());
                 }
                 firstInBatch = true;// Remember this was first in its batch
             }
             if (lastTime == 0) {
                 deltaT = 0; // Decent value for timers
             }
             lastTime = now;
 
             if (cachedGroupingMode == GROUPING_STORE_FIRST_ONLY) {
                 if (!firstInBatch) {
                     return; // Huh! don't store this one!
                 }
 
                 // If we're not storing subsequent samplers, we'll need the
                 // first sampler to do all the work...:
                 sampler.setFollowRedirects(true);
                 sampler.setImageParser(true);
             }
 
             if (cachedGroupingMode == GROUPING_IN_SIMPLE_CONTROLLERS ||
                     cachedGroupingMode == GROUPING_IN_TRANSACTION_CONTROLLERS) {
                 // Find the last controller in the target to store the
                 // sampler there:
                 for (int i = myTarget.getChildCount() - 1; i >= 0; i--) {
                     JMeterTreeNode c = (JMeterTreeNode) myTarget.getChildAt(i);
                     if (c.getTestElement() instanceof GenericController) {
                         myTarget = c;
                         break;
                     }
                 }
             }
             final long deltaTFinal = deltaT;
             final boolean firstInBatchFinal = firstInBatch;
             final JMeterTreeNode myTargetFinal = myTarget;
             JMeterUtils.runSafe(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         final JMeterTreeNode newNode = treeModel.addComponent(sampler, myTargetFinal);
                         if (firstInBatchFinal) {
                             if (addAssertions) {
                                 addAssertion(treeModel, newNode);
                             }
                             addTimers(treeModel, newNode, deltaTFinal);
                         }
 
                         for (int i = 0; subConfigs != null && i < subConfigs.length; i++) {
                             if (subConfigs[i] instanceof HeaderManager) {
                                 final TestElement headerManager = subConfigs[i];
                                 headerManager.setProperty(TestElement.GUI_CLASS, HEADER_PANEL);
                                 treeModel.addComponent(headerManager, newNode);
                             }
                         }
                     } catch (IllegalUserActionException e) {
                         JMeterUtils.reportErrorToUser(e.getMessage());
                     }
                 }
             });
         } catch (Exception e) {
             JMeterUtils.reportErrorToUser(e.getMessage());
         }
     }
 
     /**
      * Remove from the sampler all values which match the one provided by the
      * first configuration in the given collection which provides a value for
      * that property.
      *
      * @param sampler
      *            Sampler to remove values from.
      * @param configurations
      *            ConfigTestElements in descending priority.
      */
     private void removeValuesFromSampler(HTTPSamplerBase sampler, Collection<ConfigTestElement> configurations) {
         for (PropertyIterator props = sampler.propertyIterator(); props.hasNext();) {
             JMeterProperty prop = props.next();
             String name = prop.getName();
             String value = prop.getStringValue();
 
             // There's a few properties which are excluded from this processing:
             if (name.equals(TestElement.ENABLED) || name.equals(TestElement.GUI_CLASS) || name.equals(TestElement.NAME)
                     || name.equals(TestElement.TEST_CLASS)) {
                 continue; // go on with next property.
             }
 
             for (Iterator<ConfigTestElement> configs = configurations.iterator(); configs.hasNext();) {
                 ConfigTestElement config = configs.next();
 
                 String configValue = config.getPropertyAsString(name);
 
                 if (configValue != null && configValue.length() > 0) {
                     if (configValue.equals(value)) {
                         sampler.setProperty(name, ""); // $NON-NLS-1$
                     }
                     // Property was found in a config element. Whether or not
                     // it matched the value in the sampler, we're done with
                     // this property -- don't look at lower-priority configs:
                     break;
                 }
             }
         }
     }
 
     private String generateMatchUrl(HTTPSamplerBase sampler) {
         StringBuilder buf = new StringBuilder(sampler.getDomain());
         buf.append(':'); // $NON-NLS-1$
         buf.append(sampler.getPort());
         buf.append(sampler.getPath());
         if (sampler.getQueryString().length() > 0) {
             buf.append('?'); // $NON-NLS-1$
             buf.append(sampler.getQueryString());
         }
         return buf.toString();
     }
 
     private boolean matchesPatterns(String url, CollectionProperty patterns) {
         PropertyIterator iter = patterns.iterator();
         while (iter.hasNext()) {
             String item = iter.next().getStringValue();
             Pattern pattern = null;
             try {
                 pattern = JMeterUtils.getPatternCache().getPattern(item, Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
                 if (JMeterUtils.getMatcher().matches(url, pattern)) {
                     return true;
                 }
             } catch (MalformedCachePatternException e) {
                 log.warn("Skipped invalid pattern: " + item, e);
             }
         }
         return false;
     }
 
     /**
      * Scan all test elements passed in for values matching the value of any of
      * the variables in any of the variable-holding elements in the collection.
      *
      * @param sampler
      *            A TestElement to replace values on
      * @param configs
      *            More TestElements to replace values on
      * @param variables
      *            Collection of Arguments to use to do the replacement, ordered
      *            by ascending priority.
      */
     private void replaceValues(TestElement sampler, TestElement[] configs, Collection<Arguments> variables) {
         // Build the replacer from all the variables in the collection:
         ValueReplacer replacer = new ValueReplacer();
         for (Iterator<Arguments> vars = variables.iterator(); vars.hasNext();) {
             final Map<String, String> map = vars.next().getArgumentsAsMap();
             for (Iterator<String> vals = map.values().iterator(); vals.hasNext();){
                final Object next = vals.next();
                if ("".equals(next)) {// Drop any empty values (Bug 45199)
                    vals.remove();
                }
             }
             replacer.addVariables(map);
         }
 
         try {
             boolean cachedRegexpMatch = regexMatch;
             replacer.reverseReplace(sampler, cachedRegexpMatch);
             for (int i = 0; i < configs.length; i++) {
                 if (configs[i] != null) {
                     replacer.reverseReplace(configs[i], cachedRegexpMatch);
                 }
             }
         } catch (InvalidVariableException e) {
             log.warn("Invalid variables included for replacement into recorded " + "sample", e);
         }
     }
 
     /**
      * This will notify sample listeners directly within the Proxy of the
      * sampling that just occured -- so that we have a means to record the
      * server's responses as we go.
      *
      * @param event
      *            sampling event to be delivered
      */
     private void notifySampleListeners(SampleEvent event) {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         JMeterTreeNode myNode = treeModel.getNodeOf(this);
         Enumeration<JMeterTreeNode> kids = myNode.children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement testElement = subNode.getTestElement();
                 if (testElement instanceof SampleListener) {
                     ((SampleListener) testElement).sampleOccurred(event);
                 }
             }
         }
     }
 
     /**
      * This will notify test listeners directly within the Proxy that the 'test'
      * (here meaning the proxy recording) has started.
      */
     private void notifyTestListenersOfStart() {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         JMeterTreeNode myNode = treeModel.getNodeOf(this);
         Enumeration<JMeterTreeNode> kids = myNode.children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement testElement = subNode.getTestElement();
                 if (testElement instanceof TestStateListener) {
                     ((TestStateListener) testElement).testStarted();
                 }
             }
         }
     }
 
     /**
      * This will notify test listeners directly within the Proxy that the 'test'
      * (here meaning the proxy recording) has ended.
      */
     private void notifyTestListenersOfEnd() {
         JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
         JMeterTreeNode myNode = treeModel.getNodeOf(this);
         Enumeration<JMeterTreeNode> kids = myNode.children();
         while (kids.hasMoreElements()) {
             JMeterTreeNode subNode = kids.nextElement();
             if (subNode.isEnabled()) {
                 TestElement testElement = subNode.getTestElement();
                 if (testElement instanceof TestStateListener) { // TL - TE
                     ((TestStateListener) testElement).testEnded();
                 }
             }
         }
     }
 
     @Override
     public boolean canRemove() {
         return null == server;
     }
 
     private void initKeyStore() throws IOException, GeneralSecurityException {
         switch(KEYSTORE_MODE) {
         case DYNAMIC_KEYSTORE:
             storePassword = getPassword();
             keyPassword = getPassword();
             initDynamicKeyStore();
             break;
         case JMETER_KEYSTORE:
             storePassword = getPassword();
             keyPassword = getPassword();
             initJMeterKeyStore();
             break;
         case USER_KEYSTORE:
             storePassword = JMeterUtils.getPropDefault("proxy.cert.keystorepass", DEFAULT_PASSWORD); // $NON-NLS-1$;
             keyPassword = JMeterUtils.getPropDefault("proxy.cert.keypassword", DEFAULT_PASSWORD); // $NON-NLS-1$;
             log.info("HTTP(S) Test Script Recorder will use the keystore '"+ CERT_PATH_ABS + "' with the alias: '" + CERT_ALIAS + "'");
             initUserKeyStore();
             break;
         case NONE:
             throw new IOException("Cannot find keytool application and no keystore was provided");
         default:
             throw new IllegalStateException("Impossible case: " + KEYSTORE_MODE);
         }
     }
 
     /**
      * Initialise the user-provided keystore
      */
     private void initUserKeyStore() {
         try {
             keyStore = getKeyStore(storePassword.toCharArray());
             X509Certificate  caCert = (X509Certificate) keyStore.getCertificate(CERT_ALIAS);
             if (caCert == null) {
                 log.error("Could not find key with alias " + CERT_ALIAS);
                 keyStore = null;
             } else {
                 caCert.checkValidity(new Date(System.currentTimeMillis()+DateUtils.MILLIS_PER_DAY));
             }
         } catch (Exception e) {
             keyStore = null;
             log.error("Could not open keystore or certificate is not valid " + CERT_PATH_ABS + " " + e.getMessage());
         }
     }
 
     /**
      * Initialise the dynamic domain keystore
      */
     private void initDynamicKeyStore() throws IOException, GeneralSecurityException {
         if (storePassword  != null) { // Assume we have already created the store
             try {
                 keyStore = getKeyStore(storePassword.toCharArray());
                 for(String alias : KeyToolUtils.getCAaliases()) {
                     X509Certificate  caCert = (X509Certificate) keyStore.getCertificate(alias);
                     if (caCert == null) {
                         keyStore = null; // no CA key - probably the wrong store type.
                         break; // cannot continue
                     } else {
                         caCert.checkValidity(new Date(System.currentTimeMillis()+DateUtils.MILLIS_PER_DAY));
                         log.info("Valid alias found for " + alias);
                     }
                 }
             } catch (IOException e) { // store is faulty, we need to recreate it
                 keyStore = null; // if cert is not valid, flag up to recreate it
                 if (e.getCause() instanceof UnrecoverableKeyException) {
                     log.warn("Could not read key store " + e.getMessage() + "; cause: " + e.getCause().getMessage());
                 } else {
                     log.warn("Could not open/read key store " + e.getMessage()); // message includes the file name
                 }
             } catch (GeneralSecurityException e) {
                 keyStore = null; // if cert is not valid, flag up to recreate it
                 log.warn("Problem reading key store: " + e.getMessage());
             }
         }
         if (keyStore == null) { // no existing file or not valid
             storePassword = RandomStringUtils.randomAlphanumeric(20); // Alphanum to avoid issues with command-line quoting
             keyPassword = storePassword; // we use same password for both
             setPassword(storePassword);
             log.info("Creating Proxy CA in " + CERT_PATH_ABS);
             KeyToolUtils.generateProxyCA(CERT_PATH, storePassword, CERT_VALIDITY);
             log.info("Created keystore in " + CERT_PATH_ABS);
             keyStore = getKeyStore(storePassword.toCharArray()); // This should now work
         }
         final String sslDomains = getSslDomains().trim();
         if (sslDomains.length() > 0) {
             final String[] domains = sslDomains.split(",");
             // The subject may be either a host or a domain
             for(String subject : domains) {
                 if (isValid(subject)) {
                     if (!keyStore.containsAlias(subject)) {
                         log.info("Creating entry " + subject + " in " + CERT_PATH_ABS);
                         KeyToolUtils.generateHostCert(CERT_PATH, storePassword, subject, CERT_VALIDITY);
                         keyStore = getKeyStore(storePassword.toCharArray()); // reload to pick up new aliases
                         // reloading is very quick compared with creating an entry currently
                     }
                 } else {
                     log.warn("Attempt to create an invalid domain certificate: " + subject);
                 }
             }
         }
     }
 
     private boolean isValid(String subject) {
         String parts[] = subject.split("\\.");
         if (!parts[0].endsWith("*")) { // not a wildcard
             return true;
         }
         return parts.length >= 3 && AbstractVerifier.acceptableCountryWildcard(subject);
     }
 
     // This should only be called for a specific host
     KeyStore updateKeyStore(String port, String host) throws IOException, GeneralSecurityException {
         synchronized(CERT_PATH) { // ensure Proxy threads cannot interfere with each other
             if (!keyStore.containsAlias(host)) {
                 log.info(port + "Creating entry " + host + " in " + CERT_PATH_ABS);
                 KeyToolUtils.generateHostCert(CERT_PATH, storePassword, host, CERT_VALIDITY);
             }
             keyStore = getKeyStore(storePassword.toCharArray()); // reload after adding alias
         }
         return keyStore;
     }
 
     /**
      * Initialise the single key JMeter keystore (original behaviour)
      */
     private void initJMeterKeyStore() throws IOException, GeneralSecurityException {
         if (storePassword  != null) { // Assume we have already created the store
             try {
                 keyStore = getKeyStore(storePassword.toCharArray());
                 X509Certificate  caCert = (X509Certificate) keyStore.getCertificate(JMETER_SERVER_ALIAS);
                 caCert.checkValidity(new Date(System.currentTimeMillis()+DateUtils.MILLIS_PER_DAY));
             } catch (Exception e) { // store is faulty, we need to recreate it
                 keyStore = null; // if cert is not valid, flag up to recreate it
                 log.warn("Could not open expected file or certificate is not valid " + CERT_PATH_ABS  + " " + e.getMessage());
             }
         }
         if (keyStore == null) { // no existing file or not valid
             storePassword = RandomStringUtils.randomAlphanumeric(20); // Alphanum to avoid issues with command-line quoting
             keyPassword = storePassword; // we use same password for both
             setPassword(storePassword);
             log.info("Generating standard keypair in " + CERT_PATH_ABS);
             CERT_PATH.delete(); // safer to start afresh
             KeyToolUtils.genkeypair(CERT_PATH, JMETER_SERVER_ALIAS, storePassword, CERT_VALIDITY, null, null);
             keyStore = getKeyStore(storePassword.toCharArray()); // This should now work
         }
     }
 
     private KeyStore getKeyStore(char[] password) throws GeneralSecurityException, IOException {
         InputStream in = null;
         try {
             in = new BufferedInputStream(new FileInputStream(CERT_PATH));
             log.debug("Opened Keystore file: " + CERT_PATH_ABS);
             KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
             ks.load(in, password);
             log.debug("Loaded Keystore file: " + CERT_PATH_ABS);
             return ks;
         } finally {
             IOUtils.closeQuietly(in);
         }
     }
 
     private String getPassword() {
         return PREFERENCES.get(USER_PASSWORD_KEY, null);
     }
 
     private void setPassword(String password) {
         PREFERENCES.put(USER_PASSWORD_KEY, password);
     }
 
     // the keystore for use by the Proxy
     KeyStore getKeyStore() {
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java
index 1bbac902c..3174802fc 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/LogFilter.java
@@ -1,434 +1,445 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 
 // For JUnit tests, @see TestLogFilter
 
 /**
  * Description:<br>
  * <br>
  * LogFilter is a basic implementation of Filter interface. This implementation
  * will keep a record of the filtered strings to avoid repeating the process
  * unnecessarily.
  * <p>
  * The current implementation supports replacing the file extension. The reason
  * for supporting this is from first hand experience porting an existing website
  * to Tomcat + JSP. Later on we may want to provide the ability to replace the
  * whole filename. If the need materializes, we can add it later.
  * <p>
  * Example of how to use it is provided in the main method. An example is
  * provided below.
  * <p>
  *
  * <pre>
  * testf = new LogFilter();
  * String[] incl = { &quot;hello.html&quot;, &quot;index.html&quot;, &quot;/index.jsp&quot; };
  * String[] thefiles = { &quot;/test/hello.jsp&quot;, &quot;/test/one/hello.html&quot;, &quot;hello.jsp&quot;, &quot;hello.htm&quot;, &quot;/test/open.jsp&quot;,
  *      &quot;/test/open.html&quot;, &quot;/index.jsp&quot;, &quot;/index.jhtml&quot;, &quot;newindex.jsp&quot;, &quot;oldindex.jsp&quot;, &quot;oldindex1.jsp&quot;,
  *      &quot;oldindex2.jsp&quot;, &quot;oldindex3.jsp&quot;, &quot;oldindex4.jsp&quot;, &quot;oldindex5.jsp&quot;, &quot;oldindex6.jsp&quot;, &quot;/test/index.htm&quot; };
  * testf.excludeFiles(incl);
  * System.out.println(&quot; ------------ exclude test -------------&quot;);
  * for (int idx = 0; idx &lt; thefiles.length; idx++) {
  *  boolean fl = testf.isFiltered(thefiles[idx]);
  *  String line = testf.filter(thefiles[idx]);
  *  if (line != null) {
  *     System.out.println(&quot;the file: &quot; + line);
  *  }
  * }
  * </pre>
  *
  * As a general note. Both isFiltered and filter() have to be called. Calling
  * either one will not produce the desired result. isFiltered(string) will tell
  * you if a string should be filtered. The second step is to filter the string,
  * which will return null if it is filtered and replace any part of the string
  * that should be replaced.
  * <p>
  *
  */
 
 public class LogFilter implements Filter, Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     /** protected members used by class to filter * */
     protected boolean CHANGEEXT = false;
 
     protected String OLDEXT = null;
 
     protected String NEWEXT = null;
 
     protected String[] INCFILE = null;
 
     protected String[] EXCFILE = null;
 
     protected boolean FILEFILTER = false;
 
     protected boolean USEFILE = true;
 
     protected String[] INCPTRN = null;
 
     protected String[] EXCPTRN = null;
 
     protected boolean PTRNFILTER = false;
 
     protected ArrayList<Pattern> EXCPATTERNS = new ArrayList<Pattern>();
 
     protected ArrayList<Pattern> INCPATTERNS = new ArrayList<Pattern>();
 
     protected String NEWFILE = null;
 
     /**
      * The default constructor is empty
      */
     public LogFilter() {
         super();
     }
 
     /**
      * The method will replace the file extension with the new one. You can
      * either provide the extension without the period ".", or with. The method
      * will check for period and add it if it isn't present.
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#setReplaceExtension(java.lang.String,
      *      java.lang.String)
      */
     @Override
     public void setReplaceExtension(String oldext, String newext) {
         if (oldext != null && newext != null) {
             this.CHANGEEXT = true;
             if (oldext.indexOf('.') < 0 && newext.indexOf('.') < 0) {
                 this.OLDEXT = "." + oldext;
                 this.NEWEXT = "." + newext;
             } else {
                 this.OLDEXT = oldext;
                 this.NEWEXT = newext;
             }
         }
     }
 
     /**
      * Give the filter a list of files to include
      *
      * @param filenames
+     *            list of files to include
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#includeFiles(java.lang.String[])
      */
     @Override
     public void includeFiles(String[] filenames) {
         if (filenames != null && filenames.length > 0) {
             INCFILE = filenames;
             this.FILEFILTER = true;
         }
     }
 
     /**
      * Give the filter a list of files to exclude
      *
      * @param filenames
+     *            list of files to exclude
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#excludeFiles(java.lang.String[])
      */
     @Override
     public void excludeFiles(String[] filenames) {
         if (filenames != null && filenames.length > 0) {
             EXCFILE = filenames;
             this.FILEFILTER = true;
         }
     }
 
     /**
      * Give the filter a set of regular expressions to filter with for
      * inclusion. This method hasn't been fully implemented and test yet. The
      * implementation is not complete.
      *
      * @param regexp
+     *            list of regular expressions
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#includePattern(String[])
      */
     @Override
     public void includePattern(String[] regexp) {
         if (regexp != null && regexp.length > 0) {
             INCPTRN = regexp;
             this.PTRNFILTER = true;
             // now we create the compiled pattern and
             // add it to the arraylist
             for (int idx = 0; idx < INCPTRN.length; idx++) {
                 this.INCPATTERNS.add(this.createPattern(INCPTRN[idx]));
             }
         }
     }
 
     /**
      * Give the filter a set of regular expressions to filter with for
      * exclusion. This method hasn't been fully implemented and test yet. The
      * implementation is not complete.
      *
      * @param regexp
+     *            list of regular expressions
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#excludePattern(String[])
      */
     @Override
     public void excludePattern(String[] regexp) {
         if (regexp != null && regexp.length > 0) {
             EXCPTRN = regexp;
             this.PTRNFILTER = true;
             // now we create the compiled pattern and
             // add it to the arraylist
             for (int idx = 0; idx < EXCPTRN.length; idx++) {
                 this.EXCPATTERNS.add(this.createPattern(EXCPTRN[idx]));
             }
         }
     }
 
     /**
      * In the case of log filtering the important thing is whether the log entry
      * should be used. Therefore, the method will only return true if the entry
      * should be used. Since the interface defines both inclusion and exclusion,
      * that means by default inclusion filtering assumes all entries are
-     * excluded unless it matches. In the case of exlusion filtering, it assumes
+     * excluded unless it matches. In the case of exclusion filtering, it assumes
      * all entries are included unless it matches, which means it should be
      * excluded.
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#isFiltered(String, TestElement)
-     * @param path
-     * @return boolean
+     * @param path path to be tested
+     * @return <code>true</code> if entry should be excluded
      */
     @Override
     public boolean isFiltered(String path,TestElement el) {
         // we do a quick check to see if any
         // filters are set. If not we just
         // return false to be efficient.
         if (this.FILEFILTER || this.PTRNFILTER || this.CHANGEEXT) {
             if (this.FILEFILTER) {
                 return filterFile(path);
             } else if (this.PTRNFILTER) {
                 return filterPattern(path);
             } else {
                 return false;
             }
         } else {
             return false;
         }
     }
 
     /**
      * Filter the file. The implementation performs the exclusion first before
      * the inclusion. This means if a file name is in both string arrays, the
      * exclusion will take priority. Depending on how users expect this to work,
      * we may want to change the priority so that inclusion is performed first
      * and exclusion second. Another possible alternative is to perform both
      * inclusion and exclusion. Doing so would make the most sense if the method
      * throws an exception and tells the user the same filename is in both the
      * include and exclude array.
      *
      * @param file
      * @return boolean
      */
     protected boolean filterFile(String file) {
         // double check this logic make sure it
         // makes sense
         if (this.EXCFILE != null) {
             return excFile(file);
         } else if (this.INCFILE != null) {
             return !incFile(file);
         }
         return false;
     }
 
     /**
      * Method implements the logic for filtering file name inclusion. The method
      * iterates through the array and uses indexOf. Once it finds a match, it
      * won't bother with the rest of the filenames in the array.
      *
      * @param text
+     *            name of the file to tested (must not be <code>null</code>)
      * @return boolean include
      */
     public boolean incFile(String text) {
         // inclusion filter assumes most of
         // the files are not wanted, therefore
         // usefile is set to false unless it
         // matches.
         this.USEFILE = false;
         for (int idx = 0; idx < this.INCFILE.length; idx++) {
             if (text.indexOf(this.INCFILE[idx]) > -1) {
                 this.USEFILE = true;
                 break;
             }
         }
         return this.USEFILE;
     }
 
     /**
      * Method implements the logic for filtering file name exclusion. The method
      * iterates through the array and uses indexOf. Once it finds a match, it
      * won't bother with the rest of the filenames in the array.
      *
      * @param text
+     *            name of the file to be tested (must not be null)
      * @return boolean exclude
      */
     public boolean excFile(String text) {
         // exclusion filter assumes most of
         // the files are used, therefore
         // usefile is set to true, unless
         // it matches.
         this.USEFILE = true;
         boolean exc = false;
         for (int idx = 0; idx < this.EXCFILE.length; idx++) {
             if (text.indexOf(this.EXCFILE[idx]) > -1) {
                 exc = true;
                 this.USEFILE = false;
                 break;
             }
         }
         return exc;
     }
 
     /**
-     * The current implemenation assumes the user has checked the regular
+     * The current implementation assumes the user has checked the regular
      * expressions so that they don't cancel each other. The basic assumption is
      * the method will return true if the text should be filtered. If not, it
      * will return false, which means it should not be filtered.
      *
      * @param text
      * @return boolean
      */
     protected boolean filterPattern(String text) {
         if (this.INCPTRN != null) {
             return !incPattern(text);
         } else if (this.EXCPTRN != null) {
             return excPattern(text);
         }
         return false;
     }
 
     /**
      * By default, the method assumes the entry is not included, unless it
      * matches. In that case, it will return true.
      *
      * @param text
      * @return true if text is included
      */
     protected boolean incPattern(String text) {
         this.USEFILE = false;
         for (int idx = 0; idx < this.INCPATTERNS.size(); idx++) {
             if (JMeterUtils.getMatcher().contains(text, this.INCPATTERNS.get(idx))) {
                 this.USEFILE = true;
                 break;
             }
         }
         return this.USEFILE;
     }
 
     /**
      * The method assumes by default the text is not excluded. If the text
      * matches the pattern, it will then return true.
      *
      * @param text
      * @return true if text is excluded
      */
     protected boolean excPattern(String text) {
         this.USEFILE = true;
         boolean exc = false;
         for (int idx = 0; idx < this.EXCPATTERNS.size(); idx++) {
             if (JMeterUtils.getMatcher().contains(text, this.EXCPATTERNS.get(idx))) {
                 exc = true;
                 this.USEFILE = false;
                 break;
             }
         }
         return exc;
     }
 
     /**
-     * Method uses indexOf to replace the old extension with the new extesion.
+     * Method uses indexOf to replace the old extension with the new extension.
      * It might be good to use regular expression, but for now this is a simple
      * method. The method isn't designed to replace multiple instances of the
      * text, since that isn't how file extensions work. If the string contains
      * more than one instance of the old extension, only the first instance will
      * be replaced.
      *
      * @param text
-     * @return boolean
+     *            name of the file in which the extension should be replaced
+     *            (must not be null)
+     * @return <code>true</code> if the extension could be replaced,
+     *         <code>false</code> otherwise
      */
     public boolean replaceExtension(String text) {
         int pt = text.indexOf(this.OLDEXT);
         if (pt > -1) {
             int extsize = this.OLDEXT.length();
             this.NEWFILE = text.substring(0, pt) + this.NEWEXT + text.substring(pt + extsize);
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * The current implementation checks the boolean if the text should be used
      * or not. isFilter( string) has to be called first.
      *
      * @see org.apache.jmeter.protocol.http.util.accesslog.Filter#filter(java.lang.String)
      */
     @Override
     public String filter(String text) {
         if (this.CHANGEEXT) {
             if (replaceExtension(text)) {
                 return this.NEWFILE;
             } else {
                 return text;
             }
         } else if (this.USEFILE) {
             return text;
         } else {
             return null;
         }
     }
 
     /**
      * create a new pattern object from the string.
      *
      * @param pattern
-     * @return Pattern
+     *            string representation of the perl5 compatible regex pattern
+     * @return compiled Pattern, or <code>null</code> if no pattern could be
+     *         compiled
      */
     public Pattern createPattern(String pattern) {
         try {
             return JMeterUtils.getPatternCache().getPattern(pattern,
                     Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
         } catch (MalformedCachePatternException exception) {
             log.error("Problem with pattern: "+pattern,exception);
             return null;
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void reset() {
 
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/NVPair.java b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/NVPair.java
index 04aedd903..c38d656a9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/NVPair.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/util/accesslog/NVPair.java
@@ -1,84 +1,84 @@
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
 
 package org.apache.jmeter.protocol.http.util.accesslog;
 
 /**
  * Description:<br>
  * <br>
  *
  * @version $Revision$
  */
 
 public class NVPair {
 
     protected String NAME = "";
 
     protected String VALUE = "";
 
     public NVPair() {
     }
 
     /**
      * The constructor takes a name and value which represent HTTP request
      * parameters.
      *
-     * @param name
-     * @param value
+     * @param name name of the request parameter
+     * @param value value of the request parameter
      */
     public NVPair(String name, String value) {
         this.NAME = name;
         this.VALUE = value;
     }
 
     /**
      * Set the name
      *
-     * @param name
+     * @param name name of the request parameter
      */
     public void setName(String name) {
         this.NAME = name;
     }
 
     /**
      * Set the value
      *
-     * @param value
+     * @param value value of the request parameter
      */
     public void setValue(String value) {
         this.VALUE = value;
     }
 
     /**
      * Return the name
      *
      * @return name
      */
     public String getName() {
         return this.NAME;
     }
 
     /**
      * Return the value
      *
      * @return value
      */
     public String getValue() {
         return this.VALUE;
     }
 }
diff --git a/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java b/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java
index 8897931ff..bff7fa230 100644
--- a/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java
+++ b/src/protocol/mail/org/apache/jmeter/protocol/mail/sampler/MailReaderSampler.java
@@ -1,615 +1,615 @@
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
 package org.apache.jmeter.protocol.mail.sampler;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.UnsupportedEncodingException;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 
 import javax.mail.Address;
 import javax.mail.BodyPart;
 import javax.mail.Flags;
 import javax.mail.Folder;
 import javax.mail.Header;
 import javax.mail.Message;
 import javax.mail.MessagingException;
 import javax.mail.Session;
 import javax.mail.Store;
 import javax.mail.internet.MimeMultipart;
 import javax.mail.internet.MimeUtility;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.protocol.smtp.sampler.gui.SecuritySettingsPanel;
 import org.apache.jmeter.protocol.smtp.sampler.protocol.LocalTrustStoreSSLSocketFactory;
 import org.apache.jmeter.protocol.smtp.sampler.protocol.TrustAllSSLSocketFactory;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Sampler that can read from POP3 and IMAP mail servers
  */
 public class MailReaderSampler extends AbstractSampler implements Interruptible {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<String>(
             Arrays.asList(new String[]{
                     "org.apache.jmeter.config.gui.SimpleConfigGui"}));
 
     //+ JMX attributes - do not change the values
     private static final String SERVER_TYPE = "host_type"; // $NON-NLS-1$
     private static final String SERVER = "host"; // $NON-NLS-1$
     private static final String PORT = "port"; // $NON-NLS-1$
     private static final String USERNAME = "username"; // $NON-NLS-1$
     private static final String PASSWORD = "password"; // $NON-NLS-1$
     private static final String FOLDER = "folder"; // $NON-NLS-1$
     private static final String DELETE = "delete"; // $NON-NLS-1$
     private static final String NUM_MESSAGES = "num_messages"; // $NON-NLS-1$
     private static final String NEW_LINE = "\n"; // $NON-NLS-1$
     private static final String STORE_MIME_MESSAGE = "storeMimeMessage"; // $NON-NLS-1$
     private static final String HEADER_ONLY = "headerOnly"; // $NON-NLS-1$
     private static final boolean HEADER_ONLY_DEFAULT = false;
     //-
 
     private static final String RFC_822_DEFAULT_ENCODING = "iso-8859-1"; // RFC 822 uses ascii per default
 
     public static final String DEFAULT_PROTOCOL = "pop3";  // $NON-NLS-1$
 
     // Use the actual class so the name must be correct.
     private static final String TRUST_ALL_SOCKET_FACTORY = TrustAllSSLSocketFactory.class.getName();
 
     private static final String FALSE = "false";  // $NON-NLS-1$
 
     private static final String TRUE = "true";  // $NON-NLS-1$
 
     public boolean isUseLocalTrustStore() {
         return getPropertyAsBoolean(SecuritySettingsPanel.USE_LOCAL_TRUSTSTORE);
     }
 
     public String getTrustStoreToUse() {
         return getPropertyAsString(SecuritySettingsPanel.TRUSTSTORE_TO_USE);
     }
 
 
     public boolean isUseSSL() {
         return getPropertyAsBoolean(SecuritySettingsPanel.USE_SSL);
     }
 
 
     public boolean isUseStartTLS() {
         return getPropertyAsBoolean(SecuritySettingsPanel.USE_STARTTLS);
     }
 
 
     public boolean isTrustAllCerts() {
         return getPropertyAsBoolean(SecuritySettingsPanel.SSL_TRUST_ALL_CERTS);
     }
 
 
     public boolean isEnforceStartTLS() {
         return getPropertyAsBoolean(SecuritySettingsPanel.ENFORCE_STARTTLS);
 
     }
 
     public static final int ALL_MESSAGES = -1; // special value
 
     private volatile boolean busy;
 
     public MailReaderSampler() {
         setServerType(DEFAULT_PROTOCOL);
         setFolder("INBOX");  // $NON-NLS-1$
         setNumMessages(ALL_MESSAGES);
         setDeleteMessages(false);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public SampleResult sample(Entry e) {
         SampleResult parent = new SampleResult();
         boolean isOK = false; // Did sample succeed?
         final boolean deleteMessages = getDeleteMessages();
         final String serverProtocol = getServerType();
 
         parent.setSampleLabel(getName());
 
         String samplerString = toString();
         parent.setSamplerData(samplerString);
 
         /*
          * Perform the sampling
          */
         parent.sampleStart(); // Start timing
         try {
             // Create empty properties
             Properties props = new Properties();
 
             if (isUseStartTLS()) {
                 props.setProperty(mailProp(serverProtocol, "starttls.enable"), TRUE);  // $NON-NLS-1$
                 if (isEnforceStartTLS()){
                     // Requires JavaMail 1.4.2+
                     props.setProperty(mailProp(serverProtocol, "starttls.require"), TRUE);  // $NON-NLS-1$
                 }
             }
 
             if (isTrustAllCerts()) {
                 if (isUseSSL()) {
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.class"), TRUST_ALL_SOCKET_FACTORY);  // $NON-NLS-1$
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 } else if (isUseStartTLS()) {
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.class"), TRUST_ALL_SOCKET_FACTORY);  // $NON-NLS-1$
                     props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 }
             } else if (isUseLocalTrustStore()){
                 File truststore = new File(getTrustStoreToUse());
                 log.info("load local truststore - try to load truststore from: "+truststore.getAbsolutePath());
                 if(!truststore.exists()){
                     log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath());
                     truststore = new File(FileServer.getFileServer().getBaseDir(), getTrustStoreToUse());
                     log.info("load local truststore -Attempting to read truststore from:  "+truststore.getAbsolutePath());
                     if(!truststore.exists()){
                         log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath() + ". Local truststore not available, aborting execution.");
                         throw new IOException("Local truststore file not found. Also not available under : " + truststore.getAbsolutePath());
                     }
                 }
                 if (isUseSSL()) {
                     // Requires JavaMail 1.4.2+
                     props.put(mailProp(serverProtocol, "ssl.socketFactory"),   // $NON-NLS-1$ 
                             new LocalTrustStoreSSLSocketFactory(truststore));
                     props.put(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 } else if (isUseStartTLS()) {
                     // Requires JavaMail 1.4.2+
                     props.put(mailProp(serverProtocol, "ssl.socketFactory"),  // $NON-NLS-1$
                             new LocalTrustStoreSSLSocketFactory(truststore));
                     props.put(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                 }
             }
 
             // Get session
             Session session = Session.getInstance(props, null);
 
             // Get the store
             Store store = session.getStore(serverProtocol);
             store.connect(getServer(), getPortAsInt(), getUserName(), getPassword());
 
             // Get folder
             Folder folder = store.getFolder(getFolder());
             if (deleteMessages) {
                 folder.open(Folder.READ_WRITE);
             } else {
                 folder.open(Folder.READ_ONLY);
             }
 
             final int messageTotal = folder.getMessageCount();
             int n = getNumMessages();
             if (n == ALL_MESSAGES || n > messageTotal) {
                 n = messageTotal;
             }
 
             // Get directory
             Message messages[] = folder.getMessages(1,n);
             StringBuilder pdata = new StringBuilder();
             pdata.append(messages.length);
             pdata.append(" messages found\n");
             parent.setResponseData(pdata.toString(),null);
             parent.setDataType(SampleResult.TEXT);
             parent.setContentType("text/plain"); // $NON-NLS-1$
 
             final boolean headerOnly = getHeaderOnly();
             busy = true;
             for (Message message : messages) {
                 StringBuilder cdata = new StringBuilder();
                 SampleResult child = new SampleResult();
                 child.sampleStart();
 
                 cdata.append("Message "); // $NON-NLS-1$
                 cdata.append(message.getMessageNumber());
                 child.setSampleLabel(cdata.toString());
                 child.setSamplerData(cdata.toString());
                 cdata.setLength(0);
 
                 final String contentType = message.getContentType();
                 child.setContentType(contentType);// Store the content-type
                 child.setDataEncoding(RFC_822_DEFAULT_ENCODING); // RFC 822 uses ascii per default
                 child.setEncodingAndType(contentType);// Parse the content-type
 
                 if (isStoreMimeMessage()) {
                     // Don't save headers - they are already in the raw message
                     ByteArrayOutputStream bout = new ByteArrayOutputStream();
                     message.writeTo(bout);
                     child.setResponseData(bout.toByteArray()); // Save raw message
                     child.setDataType(SampleResult.TEXT);
                 } else {
                     @SuppressWarnings("unchecked") // Javadoc for the API says this is OK
                     Enumeration<Header> hdrs = message.getAllHeaders();
                     while(hdrs.hasMoreElements()){
                         Header hdr = hdrs.nextElement();
                         String value = hdr.getValue();
                         try {
                             value = MimeUtility.decodeText(value);
                         } catch (UnsupportedEncodingException uce) {
                             // ignored
                         }
                         cdata.append(hdr.getName()).append(": ").append(value).append("\n");
                     }
                     child.setResponseHeaders(cdata.toString());
                     cdata.setLength(0);
                     if (!headerOnly) {
                         appendMessageData(child, message);
                     }
                 }
 
                 if (deleteMessages) {
                     message.setFlag(Flags.Flag.DELETED, true);
                 }
                 child.setResponseOK();
                 if (child.getEndTime()==0){// Avoid double-call if addSubResult was called.
                     child.sampleEnd();
                 }
                 parent.addSubResult(child);
             }
 
             // Close connection
             folder.close(true);
             store.close();
 
             parent.setResponseCodeOK();
             parent.setResponseMessageOK();
             isOK = true;
         } catch (NoClassDefFoundError ex) {
             log.debug("",ex);// No need to log normally, as we set the status
             parent.setResponseCode("500"); // $NON-NLS-1$
             parent.setResponseMessage(ex.toString());
         } catch (MessagingException ex) {
             log.debug("", ex);// No need to log normally, as we set the status
             parent.setResponseCode("500"); // $NON-NLS-1$
             parent.setResponseMessage(ex.toString() + "\n" + samplerString); // $NON-NLS-1$
         } catch (IOException ex) {
             log.debug("", ex);// No need to log normally, as we set the status
             parent.setResponseCode("500"); // $NON-NLS-1$
             parent.setResponseMessage(ex.toString());
         } finally {
             busy = false;
         }
 
         if (parent.getEndTime()==0){// not been set by any child samples
             parent.sampleEnd();
         }
         parent.setSuccessful(isOK);
         return parent;
     }
 
     private void appendMessageData(SampleResult child, Message message)
             throws MessagingException, IOException {
         StringBuilder cdata = new StringBuilder();
         cdata.append("Date: "); // $NON-NLS-1$
         cdata.append(message.getSentDate());// TODO - use a different format here?
         cdata.append(NEW_LINE);
 
         cdata.append("To: "); // $NON-NLS-1$
         Address[] recips = message.getAllRecipients(); // may be null
         for (int j = 0; recips != null && j < recips.length; j++) {
             cdata.append(recips[j].toString());
             if (j < recips.length - 1) {
                 cdata.append("; "); // $NON-NLS-1$
             }
         }
         cdata.append(NEW_LINE);
 
         cdata.append("From: "); // $NON-NLS-1$
         Address[] from = message.getFrom(); // may be null
         for (int j = 0; from != null && j < from.length; j++) {
             cdata.append(from[j].toString());
             if (j < from.length - 1) {
                 cdata.append("; "); // $NON-NLS-1$
             }
         }
         cdata.append(NEW_LINE);
 
         cdata.append("Subject: "); // $NON-NLS-1$
         cdata.append(message.getSubject());
         cdata.append(NEW_LINE);
 
         cdata.append(NEW_LINE);
         Object content = message.getContent();
         if (content instanceof MimeMultipart) {
             appendMultiPart(child, cdata, (MimeMultipart) content);
         } else if (content instanceof InputStream){
             child.setResponseData(IOUtils.toByteArray((InputStream) content));
         } else {
             cdata.append(content);
             child.setResponseData(cdata.toString(),child.getDataEncodingNoDefault());
         }
     }
 
     private void appendMultiPart(SampleResult child, StringBuilder cdata,
             MimeMultipart mmp) throws MessagingException, IOException {
         String preamble = mmp.getPreamble();
         if (preamble != null ){
             cdata.append(preamble);
         }
         child.setResponseData(cdata.toString(),child.getDataEncodingNoDefault());
         int count = mmp.getCount();
         for (int j=0; j<count;j++){
             BodyPart bodyPart = mmp.getBodyPart(j);
             final Object bodyPartContent = bodyPart.getContent();
             final String contentType = bodyPart.getContentType();
             SampleResult sr = new SampleResult();
             sr.setSampleLabel("Part: "+j);
             sr.setContentType(contentType);
             sr.setDataEncoding(RFC_822_DEFAULT_ENCODING);
             sr.setEncodingAndType(contentType);
             sr.sampleStart();
             if (bodyPartContent instanceof InputStream){
                 sr.setResponseData(IOUtils.toByteArray((InputStream) bodyPartContent));
             } else if (bodyPartContent instanceof MimeMultipart){
                 appendMultiPart(sr, cdata, (MimeMultipart) bodyPartContent);
             } else {
                 sr.setResponseData(bodyPartContent.toString(),sr.getDataEncodingNoDefault());
             }
             sr.setResponseOK();
             if (sr.getEndTime()==0){// not been set by any child samples
                 sr.sampleEnd();
             }
             child.addSubResult(sr);
         }
     }
 
     /**
      * Sets the type of protocol to use when talking with the remote mail
      * server. Either MailReaderSampler.TYPE_IMAP[S] or
      * MailReaderSampler.TYPE_POP3[S]. Default is MailReaderSampler.TYPE_POP3.
      *
-     * @param serverType
+     * @param serverType protocol to use
      */
     public void setServerType(String serverType) {
         setProperty(SERVER_TYPE, serverType);
     }
 
     /**
      * Returns the type of the protocol set to use when talking with the remote
      * server. Either MailReaderSampler.TYPE_IMAP[S] or
      * MailReaderSampler.TYPE_POP3[S].
      *
      * @return Server Type
      */
     public String getServerType() {
         return getPropertyAsString(SERVER_TYPE);
     }
 
     /**
      * @param server -
      *            The name or address of the remote server.
      */
     public void setServer(String server) {
         setProperty(SERVER, server);
     }
 
     /**
      * @return The name or address of the remote server.
      */
     public String getServer() {
         return getPropertyAsString(SERVER);
     }
 
     public String getPort() {
         return getPropertyAsString(PORT);
     }
 
     private int getPortAsInt() {
         return getPropertyAsInt(PORT, -1);
     }
 
     public void setPort(String port) {
         setProperty(PORT, port, "");
     }
 
     /**
      * @param username -
      *            The username of the mail account.
      */
     public void setUserName(String username) {
         setProperty(USERNAME, username);
     }
 
     /**
      * @return The username of the mail account.
      */
     public String getUserName() {
         return getPropertyAsString(USERNAME);
     }
 
     /**
-     * @param password
+     * @param password the password to use
      */
     public void setPassword(String password) {
         setProperty(PASSWORD, password);
     }
 
     /**
      * @return password
      */
     public String getPassword() {
         return getPropertyAsString(PASSWORD);
     }
 
     /**
      * @param folder -
      *            Name of the folder to read emails from. "INBOX" is the only
      *            acceptable value if the server type is POP3.
      */
     public void setFolder(String folder) {
         setProperty(FOLDER, folder);
     }
 
     /**
      * @return folder
      */
     public String getFolder() {
         return getPropertyAsString(FOLDER);
     }
 
     /**
      * @param num_messages -
      *            The number of messages to retrieve from the mail server. Set
      *            this value to -1 to retrieve all messages.
      */
     public void setNumMessages(int num_messages) {
         setProperty(new IntegerProperty(NUM_MESSAGES, num_messages));
     }
 
     /**
      * @param num_messages -
      *            The number of messages to retrieve from the mail server. Set
      *            this value to -1 to retrieve all messages.
      */
     public void setNumMessages(String num_messages) {
         setProperty(new StringProperty(NUM_MESSAGES, num_messages));
     }
 
     /**
      * @return The number of messages to retrieve from the mail server.
      *         -1 denotes get all messages.
      */
     public int getNumMessages() {
         return getPropertyAsInt(NUM_MESSAGES);
     }
 
     /**
      * @return The number of messages to retrieve from the mail server.
      *         -1 denotes get all messages.
      */
     public String getNumMessagesString() {
         return getPropertyAsString(NUM_MESSAGES);
     }
 
     /**
      * @param delete -
      *            Whether or not to delete the read messages from the folder.
      */
     public void setDeleteMessages(boolean delete) {
         setProperty(new BooleanProperty(DELETE, delete));
     }
 
     /**
      * @return Whether or not to delete the read messages from the folder.
      */
     public boolean getDeleteMessages() {
         return getPropertyAsBoolean(DELETE);
     }
 
     /**
      * @return Whether or not to store the retrieved message as MIME message in
      *         the sample result
      */
     public boolean isStoreMimeMessage() {
         return getPropertyAsBoolean(STORE_MIME_MESSAGE, false);
     }
 
     /**
      * @param storeMimeMessage
      *            Whether or not to store the retrieved message as MIME message in the
      *            sample result
      */
     public void setStoreMimeMessage(boolean storeMimeMessage) {
         setProperty(STORE_MIME_MESSAGE, storeMimeMessage, false);
     }
 
     @Override
     public String toString(){
         StringBuilder sb = new StringBuilder();
         sb.append(getServerType());
         sb.append("://");
         String name = getUserName();
         if (name.length() > 0){
             sb.append(name);
             sb.append("@");
         }
         sb.append(getServer());
         int port=getPortAsInt();
         if (port != -1){
             sb.append(":").append(port);
         }
         sb.append("/");
         sb.append(getFolder());
         sb.append("[");
         sb.append(getNumMessages());
         sb.append("]");
         return sb.toString();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean interrupt() {
         boolean wasbusy = busy;
         busy = false;
         return wasbusy;
     }
 
     /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 
     public boolean getHeaderOnly() {
         return getPropertyAsBoolean(HEADER_ONLY, HEADER_ONLY_DEFAULT);
     }
 
     public void setHeaderOnly(boolean selected) {
         setProperty(HEADER_ONLY, selected, HEADER_ONLY_DEFAULT);
     }
 
     /**
      * Build a property name of the form "mail.pop3s.starttls.require"
      *
      * @param protocol the protocol, i.e. "pop3s" in the example
      * @param propname the property name suffix, i.e. "starttls.require" in the example
      * @return the constructed name
      */
     private String mailProp(String protocol, String propname) {
         StringBuilder sb = new StringBuilder();
         sb.append("mail.").append(protocol).append(".");
         sb.append(propname);
         return sb.toString();
     }
 }
