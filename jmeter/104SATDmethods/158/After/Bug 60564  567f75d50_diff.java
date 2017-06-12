diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java
index 53d8a34bf..e14ef9617 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/Utils.java
@@ -1,253 +1,257 @@
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
 
 package org.apache.jmeter.protocol.jms;
 
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Map;
 
 import javax.jms.Connection;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageConsumer;
 import javax.jms.MessageProducer;
 import javax.jms.Session;
 import javax.naming.Context;
 import javax.naming.NamingException;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.jms.sampler.JMSProperties;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Utility methods for JMS protocol.
  * WARNING - the API for this class is likely to change!
  */
 public final class Utils {
     // By default priority is 4
     // http://docs.oracle.com/javaee/6/tutorial/doc/bncfu.html
     public static final String DEFAULT_PRIORITY_4 = "4"; // $NON-NLS-1$
 
     // By default a message never expires
     // http://docs.oracle.com/javaee/6/tutorial/doc/bncfu.html
     public static final String DEFAULT_NO_EXPIRY = "0"; // $NON-NLS-1$
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Utils.class);
 
     public static void close(MessageConsumer closeable, Logger log){
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static void close(Session closeable, Logger log) {
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static void close(Connection closeable, Logger log) {
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
+    /**
+     * @param closeable {@link MessageProducer}
+     * @param log {@link Logger}
+     */
     public static void close(MessageProducer closeable, Logger log) {
         if (closeable != null){
             try {
                 closeable.close();
             } catch (JMSException e) {
                 log.error("Error during close: ", e);
             }
         }
     }
 
     public static String messageProperties(Message msg){
         return messageProperties(new StringBuilder(), msg).toString();
     }
 
     public static StringBuilder messageProperties(StringBuilder sb, Message msg){
         requestHeaders(sb, msg);
         sb.append("Properties:\n");
         Enumeration<?> rme;
         try {
             rme = msg.getPropertyNames();
             while(rme.hasMoreElements()){
                 String name=(String) rme.nextElement();
                 sb.append(name).append('\t');
                 String value=msg.getStringProperty(name);
                 sb.append(value).append('\n');
             }
         } catch (JMSException e) {
             sb.append("\nError: "+e.toString());
         }
         return sb;
     }
     
     public static StringBuilder requestHeaders(StringBuilder sb, Message msg){
         try {
             sb.append("JMSCorrelationId ").append(msg.getJMSCorrelationID()).append('\n');
             sb.append("JMSMessageId     ").append(msg.getJMSMessageID()).append('\n');
             sb.append("JMSTimestamp     ").append(msg.getJMSTimestamp()).append('\n');
             sb.append("JMSType          ").append(msg.getJMSType()).append('\n');
             sb.append("JMSExpiration    ").append(msg.getJMSExpiration()).append('\n');
             sb.append("JMSPriority      ").append(msg.getJMSPriority()).append('\n');
             sb.append("JMSDestination   ").append(msg.getJMSDestination()).append('\n');
         } catch (JMSException e) {
             sb.append("\nError: "+e.toString());
         }
         return sb;
     }
 
     /**
      * Method will lookup a given destination (topic/queue) using JNDI.
      *
      * @param context
      *            context to use for lookup
      * @param name
      *            the destination name
      * @return the destination, never null
      * @throws NamingException
      *             if the name cannot be found as a Destination
      */
     public static Destination lookupDestination(Context context, String name) throws NamingException {
         Object o = context.lookup(name);
         if (o instanceof Destination){
             return (Destination) o;
         }
         throw new NamingException("Found: "+name+"; expected Destination, but was: "+(o!=null ? o.getClass().getName() : "null"));
     }
 
     /**
      * Get value from Context environment taking into account non fully
      * compliant JNDI implementations
      *
      * @param context
      *            context to use
      * @param key
      *            key to lookup in contexts environment
      * @return String or <code>null</code> if context.getEnvironment() is not compliant
      * @throws NamingException
      *             if a naming problem occurs while getting the environment
      */
     public static String getFromEnvironment(Context context, String key) throws NamingException {
         try {
             Hashtable<?,?> env = context.getEnvironment();
             if(env != null) {
                 return (String) env.get(key);
             } else {
                 log.warn("context.getEnvironment() returned null (should not happen according to javadoc but non compliant implementation can return this)");
                 return null;
             }
         } catch (javax.naming.OperationNotSupportedException ex) {
             // Some JNDI implementation can return this
             log.warn("context.getEnvironment() not supported by implementation ");
             return null;
         }        
     }
 
     /**
      * Obtain the queue connection from the context and factory name.
      * 
      * @param ctx
      *            context to use
      * @param factoryName
      *            name of the object factory to look up in <code>context</code>
      * @return the queue connection
      * @throws JMSException
      *             when creation of the connection fails
      * @throws NamingException
      *             when lookup in context fails
      */
     public static Connection getConnection(Context ctx, String factoryName) throws JMSException, NamingException {
         Object objfac = null;
         try {
             objfac = ctx.lookup(factoryName);
         } catch (NoClassDefFoundError e) {
             throw new NamingException("Lookup failed: "+e.toString());
         }
         if (objfac instanceof javax.jms.ConnectionFactory) {
             String username = getFromEnvironment(ctx, Context.SECURITY_PRINCIPAL);
             if(username != null) {
                 String password = getFromEnvironment(ctx, Context.SECURITY_CREDENTIALS);
                 return ((javax.jms.ConnectionFactory) objfac).createConnection(username, password);                
             }
             else {
                 return ((javax.jms.ConnectionFactory) objfac).createConnection();
             }
         }
         throw new NamingException("Expected javax.jms.ConnectionFactory, found "+(objfac != null ? objfac.getClass().getName(): "null"));
     }
     
     /**
      * Set JMS Properties to msg
      * @param msg Message to operate on
      * @param map Map of Properties to be set on the message
      * @throws JMSException when <code>msg</code> throws a {@link JMSException} while the properties get set
      */
     public static void addJMSProperties(Message msg, Map<String, Object> map) throws JMSException {
         if (map == null) {
             return;
         }
         for (Map.Entry<String, Object> me : map.entrySet()) {
             String name = me.getKey();
             Object value = me.getValue();
             if (log.isDebugEnabled()) {
                 log.debug("Adding property [" + name + "=" + value + "]");
             }
 
             // WebsphereMQ does not allow corr. id. to be set using setStringProperty()
             if ("JMSCorrelationID".equalsIgnoreCase(name)) { // $NON-NLS-1$
                 msg.setJMSCorrelationID((String)value);
             } else {
                 msg.setObjectProperty(name, value);
             }
         }
     }
 
 
     /**
      * Converts {@link Arguments} to {@link JMSProperties} defaulting to String type
      * Used to convert version &lt;= 2.10 test plans
      * @param args {@link Arguments} to be converted
      * @return jmsProperties The converted {@link JMSProperties}
      */
     public static JMSProperties convertArgumentsToJmsProperties(Arguments args) {
         JMSProperties jmsProperties = new JMSProperties();
         Map<String,String>  map = args.getArgumentsAsMap();
         for (Map.Entry<String, String> entry : map.entrySet()) {
             jmsProperties.addJmsProperty(entry.getKey(), entry.getValue());
         }
         return jmsProperties;
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java
index 73482b8e6..bfece1430 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/InitialContextFactory.java
@@ -1,174 +1,174 @@
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
 
 package org.apache.jmeter.protocol.jms.client;
 
 import java.util.Properties;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.naming.NamingException;
 
 import org.apache.commons.lang3.StringUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * InitialContextFactory is responsible for getting an instance of the initial context.
  */
 public class InitialContextFactory {
 
     private static final ConcurrentHashMap<String, Context> MAP = new ConcurrentHashMap<>();
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(InitialContextFactory.class);
 
     /**
      * Look up the context from the local cache, creating it if necessary.
      * 
      * @param initialContextFactory used to set the property {@link Context#INITIAL_CONTEXT_FACTORY}
      * @param providerUrl used to set the property {@link Context#PROVIDER_URL}
      * @param useAuth set <code>true</code> if security is to be used.
      * @param securityPrincipal used to set the property {@link Context#SECURITY_PRINCIPAL}
      * @param securityCredentials used to set the property {@link Context#SECURITY_CREDENTIALS}
      * @return the context, never <code>null</code>
      * @throws NamingException when creation of the context fails
      */
     public static Context lookupContext(String initialContextFactory, 
             String providerUrl, boolean useAuth, String securityPrincipal, String securityCredentials) throws NamingException {
         String cacheKey = createKey(Thread.currentThread().getId(),initialContextFactory ,providerUrl, securityPrincipal, securityCredentials);
         Context ctx = MAP.get(cacheKey);
         if (ctx == null) {
             Properties props = new Properties();
             props.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
             props.setProperty(Context.PROVIDER_URL, providerUrl);
             if (useAuth && securityPrincipal != null && securityCredentials != null
                     && securityPrincipal.length() > 0 && securityCredentials.length() > 0) {
                 props.setProperty(Context.SECURITY_PRINCIPAL, securityPrincipal);
                 props.setProperty(Context.SECURITY_CREDENTIALS, securityCredentials);
                 log.info("authentication properties set");
             }
             try {
                 ctx = new InitialContext(props);
             } catch (NoClassDefFoundError | Exception e){
                 throw new NamingException(e.toString());
             }
             // we want to return the context that is actually in the map
             // if it's the first put we will have a null result
             Context oldCtx = MAP.putIfAbsent(cacheKey, ctx);
             if(oldCtx != null) {
                 // There was an object in map, destroy the temporary and return one in map (oldCtx)
                 try {
                     ctx.close();
                 } catch (Exception e) {
                     // NOOP
                 }
                 ctx = oldCtx;
             }
             // else No object in Map, ctx is the one
         }
         return ctx;
     }
 
     /**
      * Create cache key
      * @param threadId Thread Id
      * @param initialContextFactory
      * @param providerUrl
      * @param securityPrincipal
      * @param securityCredentials
      * @return the cache key
      */
     private static String createKey(
             long threadId,
             String initialContextFactory,
             String providerUrl, String securityPrincipal,
             String securityCredentials) {
        StringBuilder builder = new StringBuilder();
        builder.append(threadId);
        builder.append("#");
        builder.append(initialContextFactory);
        builder.append("#");
        builder.append(providerUrl);
        builder.append("#");
        if(!StringUtils.isEmpty(securityPrincipal)) {
            builder.append(securityPrincipal);
            builder.append("#");
        }
        if(!StringUtils.isEmpty(securityCredentials)) {
            builder.append(securityCredentials);
        }
        return builder.toString();
     }
 
     /**
      * Initialize the JNDI initial context
      *
      * @param useProps
      *            if true, create a new InitialContext; otherwise use the other
      *            parameters to call
      *            {@link #lookupContext(String, String, boolean, String, String)}
      * @param initialContextFactory
      *            name of the initial context factory (ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param providerUrl
      *            url of the provider to use (ignored if <code>useProps</code>
      *            is <code>true</code>)
      * @param useAuth
      *            <code>true</code> if auth should be used, <code>false</code>
      *            otherwise (ignored if <code>useProps</code> is
      *            <code>true</code>)
      * @param securityPrincipal
      *            name of the principal to (ignored if <code>useProps</code> is
      *            <code>true</code>)
      * @param securityCredentials
      *            credentials for the principal (ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @return the context, never <code>null</code>
      * @throws NamingException
      *             when creation of the context fails
      */
     public static Context getContext(boolean useProps, 
             String initialContextFactory, String providerUrl, 
             boolean useAuth, String securityPrincipal, String securityCredentials) throws NamingException {
         if (useProps) {
             try {
                 return new InitialContext();
             } catch (NoClassDefFoundError | Exception e){
                 throw new NamingException(e.toString());
             }
         } else {
             return lookupContext(initialContextFactory, providerUrl, useAuth, securityPrincipal, securityCredentials);
         }
     }
     
     /**
      * clear all the InitialContext objects.
      */
     public static void close() {
         for (Context ctx : MAP.values()) {
             try {
                 ctx.close();
             } catch (NamingException e) {
                 log.error(e.getMessage());
             }
         }
         MAP.clear();
         log.info("InitialContextFactory.close() called and Context instances cleaned up");
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/Publisher.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/Publisher.java
index f83e5c138..5e5e658b3 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/Publisher.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/Publisher.java
@@ -1,210 +1,210 @@
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
 
 package org.apache.jmeter.protocol.jms.client;
 
 import java.io.Closeable;
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Map.Entry;
 
 import javax.jms.BytesMessage;
 import javax.jms.Connection;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.MapMessage;
 import javax.jms.Message;
 import javax.jms.MessageProducer;
 import javax.jms.ObjectMessage;
 import javax.jms.Session;
 import javax.jms.TextMessage;
 import javax.naming.Context;
 import javax.naming.NamingException;
 
 import org.apache.jmeter.protocol.jms.Utils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 public class Publisher implements Closeable {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Publisher.class);
 
     private final Connection connection;
 
     private final Session session;
 
     private final MessageProducer producer;
     
     private final Context ctx;
     
     private final boolean staticDest;
 
     /**
      * Create a publisher using either the jndi.properties file or the provided
      * parameters. Uses a static destination and persistent messages(for
      * backward compatibility)
      *
      * @param useProps
      *            true if a jndi.properties file is to be used
      * @param initialContextFactory
      *            the (ignored if useProps is true)
      * @param providerUrl
      *            (ignored if useProps is true)
      * @param connfactory
      *            name of the object factory to look up in context
      * @param destinationName
      *            name of the destination to use
      * @param useAuth
      *            (ignored if useProps is true)
      * @param securityPrincipal
      *            (ignored if useProps is true)
      * @param securityCredentials
      *            (ignored if useProps is true)
      * @throws JMSException
      *             if the context could not be initialised, or there was some
      *             other error
      * @throws NamingException
      *             when creation of the publisher fails
      */
     public Publisher(boolean useProps, String initialContextFactory, String providerUrl, 
             String connfactory, String destinationName, boolean useAuth,
             String securityPrincipal, String securityCredentials) throws JMSException, NamingException {
         this(useProps, initialContextFactory, providerUrl, connfactory,
                 destinationName, useAuth, securityPrincipal,
                 securityCredentials, true);
     }
     
     
     /**
      * Create a publisher using either the jndi.properties file or the provided
      * parameters
      *
      * @param useProps
      *            true if a jndi.properties file is to be used
      * @param initialContextFactory
      *            the (ignored if useProps is true)
      * @param providerUrl
      *            (ignored if useProps is true)
      * @param connfactory
      *            name of the object factory to lookup in context
      * @param destinationName
      *            name of the destination to use
      * @param useAuth
      *            (ignored if useProps is true)
      * @param securityPrincipal
      *            (ignored if useProps is true)
      * @param securityCredentials
      *            (ignored if useProps is true)
      * @param staticDestination
      *            true if the destination is not to change between loops
      * @throws JMSException
      *             if the context could not be initialised, or there was some
      *             other error
      * @throws NamingException
      *             when creation of the publisher fails
      */
     public Publisher(boolean useProps, String initialContextFactory, String providerUrl, 
             String connfactory, String destinationName, boolean useAuth,
             String securityPrincipal, String securityCredentials,
             boolean staticDestination) throws JMSException, NamingException {
         super();
         boolean initSuccess = false;
         try{
             ctx = InitialContextFactory.getContext(useProps, initialContextFactory, 
                     providerUrl, useAuth, securityPrincipal, securityCredentials);
             connection = Utils.getConnection(ctx, connfactory);
             session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             staticDest = staticDestination;
             if (staticDest) {
                 Destination dest = Utils.lookupDestination(ctx, destinationName);
                 producer = session.createProducer(dest);
             } else {
                 producer = session.createProducer(null);
             }
             initSuccess = true;
         } finally {
             if(!initSuccess) {
                 close();
             }
         }
     }
     
     public Message publish(String text, String destinationName, Map<String, Object> properties, int deliveryMode, int priority, long expiration)
             throws JMSException, NamingException {
         TextMessage msg = session.createTextMessage(text);
         return setPropertiesAndSend(destinationName, properties, msg, deliveryMode, priority, expiration);
     }
     
     public Message publish(Serializable contents, String destinationName, Map<String, Object> properties, int deliveryMode, int priority, long expiration)
             throws JMSException, NamingException {
         ObjectMessage msg = session.createObjectMessage(contents);
         return setPropertiesAndSend(destinationName, properties, msg, deliveryMode, priority, expiration);
     }
     
     public Message publish(byte[] bytes, String destinationName, Map<String, Object> properties, int deliveryMode, int priority, long expiration)
             throws JMSException, NamingException {
         BytesMessage msg = session.createBytesMessage();
         msg.writeBytes(bytes);
         return setPropertiesAndSend(destinationName, properties, msg, deliveryMode, priority, expiration);
     }
     
     public MapMessage publish(Map<String, Object> map, String destinationName, Map<String, Object> properties, 
             int deliveryMode, int priority, long expiration)
             throws JMSException, NamingException {
         MapMessage msg = session.createMapMessage();
         for (Entry<String, Object> me : map.entrySet()) {
             msg.setObject(me.getKey(), me.getValue());
         }
         return (MapMessage)setPropertiesAndSend(destinationName, properties, msg, deliveryMode, priority, expiration);
     }
 
     /**
      * @param destinationName 
      * @param properties Map<String, String>
      * @param msg Message
      * @param deliveryMode
      * @param priority
      * @param expiration
      * @return Message
      * @throws JMSException
      * @throws NamingException
      */
     private Message setPropertiesAndSend(String destinationName,
             Map<String, Object> properties, Message msg,
             int deliveryMode, int priority, long expiration)
             throws JMSException, NamingException {
         Utils.addJMSProperties(msg, properties);
         if (staticDest || destinationName == null) {
             producer.send(msg, deliveryMode, priority, expiration);
         } else {
             Destination dest = Utils.lookupDestination(ctx, destinationName);
             producer.send(dest, msg, deliveryMode, priority, expiration);
         }
         return msg;
     }
 
     /**
      * Close will close the session
      */
     @Override
     public void close() {
         Utils.close(producer, log);
         Utils.close(session, log);
         Utils.close(connection, log);
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java
index 45f9921f1..84f00cc95 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ReceiveSubscriber.java
@@ -1,384 +1,384 @@
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
 
 package org.apache.jmeter.protocol.jms.client;
 
 import java.io.Closeable;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.TimeUnit;
 
 import javax.jms.Connection;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageConsumer;
 import javax.jms.MessageListener;
 import javax.jms.Session;
 import javax.jms.Topic;
 import javax.naming.Context;
 import javax.naming.NamingException;
 
 import org.apache.jmeter.protocol.jms.Utils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Generic MessageConsumer class, which has two possible strategies.
  * <ul>
  * <li>Use MessageConsumer.receive(timeout) to fetch messages.</li>
  * <li>Use MessageListener.onMessage() to cache messages in a local queue.</li>
  * </ul>
  * In both cases, the {@link #getMessage(long)} method is used to return the next message,
  * either directly using receive(timeout) or from the queue using poll(timeout).
  */
 public class ReceiveSubscriber implements Closeable, MessageListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ReceiveSubscriber.class);
 
     private final Connection connection;
 
     private final Session session;
 
     private final MessageConsumer subscriber;
 
     /*
      * We use a LinkedBlockingQueue (rather than a ConcurrentLinkedQueue) because it has a
      * poll-with-wait method that avoids the need to use a polling loop.
      */
     private final LinkedBlockingQueue<Message> queue;
 
     /**
      * No need for volatile as this variable is only accessed by a single thread
      */
     private boolean connectionStarted;
 
     /**
      * Constructor takes the necessary JNDI related parameters to create a
      * connection and prepare to begin receiving messages. <br>
      * The caller must then invoke {@link #start()} to enable message reception.
      *
      * @param useProps
      *            if <code>true</code>, use <em>jndi.properties</em> instead of
      *            <code>initialContextFactory</code>, <code>providerUrl</code>,
      *            <code>securityPrincipal</code>,
      *            <code>securityCredentials</code>
      * @param initialContextFactory
      *            name of the initial context factory (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param providerUrl
      *            url of the provider (will be ignored if <code>useProps</code>
      *            is <code>true</code>)
      * @param connfactory
      *            name of the object factory to look up in context
      * @param destinationName
      *            name of the destination
      * @param durableSubscriptionId
      *            id for a durable subscription (if empty or <code>null</code>
      *            no durable subscription will be done)
      * @param clientId
      *            client id to use (may be empty or <code>null</code>)
      * @param jmsSelector
      *            Message Selector
      * @param useAuth
      *            flag whether auth should be used (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param securityPrincipal
      *            name of the principal to use for auth (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param securityCredentials
      *            credentials for the principal (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @throws JMSException
      *             if could not create context or other problem occurred.
      * @throws NamingException
      *             when lookup of context or destination fails
      */
     public ReceiveSubscriber(boolean useProps, 
             String initialContextFactory, String providerUrl, String connfactory, String destinationName,
             String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
             String securityPrincipal, String securityCredentials) throws NamingException, JMSException {
         this(0, useProps, 
                 initialContextFactory, providerUrl, connfactory, destinationName,
                 durableSubscriptionId, clientId, jmsSelector, useAuth, 
                 securityPrincipal, securityCredentials, false);
     }
 
     /**
      * Constructor takes the necessary JNDI related parameters to create a
      * connection and create an onMessageListener to prepare to begin receiving
      * messages. <br>
      * The caller must then invoke {@link #start()} to enable message reception.
      *
      * @param queueSize
      *            maximum queue size, where a <code>queueSize</code> &lt;=0
      *            means no limit
      * @param useProps
      *            if <code>true</code>, use <em>jndi.properties</em> instead of
      *            <code>initialContextFactory</code>, <code>providerUrl</code>,
      *            <code>securityPrincipal</code>,
      *            <code>securityCredentials</code>
      * @param initialContextFactory
      *            name of the initial context factory (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param providerUrl
      *            url of the provider (will be ignored if <code>useProps</code>
      *            is <code>true</code>)
      * @param connfactory
      *            name of the object factory to look up in context
      * @param destinationName
      *            name of the destination
      * @param durableSubscriptionId
      *            id for a durable subscription (if empty or <code>null</code>
      *            no durable subscription will be done)
      * @param clientId
      *            client id to use (may be empty or <code>null</code>)
      * @param jmsSelector
      *            Message Selector
      * @param useAuth
      *            flag whether auth should be used (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param securityPrincipal
      *            name of the principal to use for auth (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param securityCredentials
      *            credentials for the principal (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @throws JMSException
      *             if could not create context or other problem occurred.
      * @throws NamingException
      *             when lookup of context or destination fails
      */
     public ReceiveSubscriber(int queueSize, boolean useProps, 
             String initialContextFactory, String providerUrl, String connfactory, String destinationName,
             String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
             String securityPrincipal, String securityCredentials) throws NamingException, JMSException {
         this(queueSize,  useProps, 
              initialContextFactory, providerUrl, connfactory, destinationName,
              durableSubscriptionId, clientId, jmsSelector, useAuth, 
              securityPrincipal,  securityCredentials, true);
     }
     
     
     /**
      * Constructor takes the necessary JNDI related parameters to create a
      * connection and create an onMessageListener to prepare to begin receiving
      * messages. <br/>
      * The caller must then invoke {@link #start()} to enable message reception.
      *
      * @param queueSize
      *            maximum queue, where a queueSize &lt;=0 means no limit
      * @param useProps
      *            if <code>true</code>, use <em>jndi.properties</em> instead of
      *            <code>initialContextFactory</code>, <code>providerUrl</code>,
      *            <code>securityPrincipal</code>,
      *            <code>securityCredentials</code>
      * @param initialContextFactory
      *            name of the initial context factory (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param providerUrl
      *            url of the provider (will be ignored if <code>useProps</code>
      *            is <code>true</code>)
      * @param connfactory
      *            name of the object factory to look up in context
      * @param destinationName
      *            name of the destination
      * @param durableSubscriptionId
      *            id for a durable subscription (if empty or <code>null</code>
      *            no durable subscription will be done)
      * @param clientId
      *            client id to use (may be empty or <code>null</code>)
      * @param jmsSelector
      *            Message Selector
      * @param useAuth
      *            flag whether auth should be used (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param securityPrincipal
      *            name of the principal to use for auth (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param securityCredentials
      *            credentials for the principal (will be ignored if
      *            <code>useProps</code> is <code>true</code>)
      * @param useMessageListener
      *            if <code>true</code> create an onMessageListener to prepare to
      *            begin receiving messages, otherwise queue will be
      *            <code>null</code>
      * @throws JMSException
      *             if could not create context or other problem occurred.
      * @throws NamingException
      *             when lookup of context or destination fails
      */
     private ReceiveSubscriber(int queueSize, boolean useProps, 
             String initialContextFactory, String providerUrl, String connfactory, String destinationName,
             String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
             String securityPrincipal, String securityCredentials, boolean useMessageListener) throws NamingException, JMSException {
         boolean initSuccess = false;
         try{
             Context ctx = InitialContextFactory.getContext(useProps, 
                     initialContextFactory, providerUrl, useAuth, securityPrincipal, securityCredentials);
             connection = Utils.getConnection(ctx, connfactory);
             if(!isEmpty(clientId)) {
                 connection.setClientID(clientId);
             }
             session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             Destination dest = Utils.lookupDestination(ctx, destinationName);
             subscriber = createSubscriber(session, dest, durableSubscriptionId, jmsSelector);
             if(useMessageListener) {
                 if (queueSize <=0) {
                     queue = new LinkedBlockingQueue<>();
                 } else {
                     queue = new LinkedBlockingQueue<>(queueSize);
                 }
                 subscriber.setMessageListener(this);
             } else {
                 queue = null;
             }
             log.debug("<init> complete");
             initSuccess = true;
         }
         finally {
             if(!initSuccess) {
                 close();
             }
         }
     }
     
     /**
      * Return a simple MessageConsumer or a TopicSubscriber (as a durable subscription)
      * @param session
      *                 JMS session
      * @param destination
      *                 JMS destination, can be either topic or queue
      * @param durableSubscriptionId 
      *                 If neither empty nor null, this means that a durable 
      *                 subscription will be used
      * @param jmsSelector JMS Selector
      * @return the message consumer
      * @throws JMSException
      */
     private MessageConsumer createSubscriber(Session session, 
             Destination destination, String durableSubscriptionId, 
             String jmsSelector) throws JMSException {
         if (isEmpty(durableSubscriptionId)) {
             if(isEmpty(jmsSelector)) {
                 return session.createConsumer(destination);
             } else {
                 return session.createConsumer(destination, jmsSelector);
             }
         } else {
             if(isEmpty(jmsSelector)) {
                 return session.createDurableSubscriber((Topic) destination, durableSubscriptionId); 
             } else {
                 return session.createDurableSubscriber((Topic) destination, durableSubscriptionId, jmsSelector, false);                 
             }
         }
     }
 
     /**
      * Calls Connection.start() to begin receiving inbound messages.
      * @throws JMSException when starting the context fails
      */
     public void start() throws JMSException {
         log.debug("start()");
         connection.start();
         connectionStarted=true;
     }
 
     /**
      * Calls Connection.stop() to stop receiving inbound messages.
      * @throws JMSException when stopping the context fails
      */
     public void stop() throws JMSException {
         log.debug("stop()");
         connection.stop();
         connectionStarted=false;
     }
 
     /**
      * Get the next message or <code>null</code>.
      * <p>
      * Never blocks for longer than the specified timeout.
      * 
      * @param timeout in milliseconds
      * @return the next message or <code>null</code>
      * 
      * @throws JMSException when receiving the message fails
      */
     public Message getMessage(long timeout) throws JMSException {
         Message message = null;
         if (queue != null) { // Using onMessage Listener
             try {
                 if (timeout < 10) { // Allow for short/negative times
                     message = queue.poll();                    
                 } else {
                     message = queue.poll(timeout, TimeUnit.MILLISECONDS);
                 }
             } catch (InterruptedException e) {
                 // Ignored
                 Thread.currentThread().interrupt();
             }
             return message;
         }
         if (timeout < 10) { // Allow for short/negative times
             message = subscriber.receiveNoWait();                
         } else {
             message = subscriber.receive(timeout);
         }
         return message;
     }
     /**
      * close() will stop the connection first. 
      * Then it closes the subscriber, session and connection.
      */
     @Override
     public void close() { // called by SubscriberSampler#threadFinished()
         log.debug("close()");
         try {
             if(connection != null && connectionStarted) {
                 connection.stop();
                 connectionStarted = false;
             }
         } catch (JMSException e) {
-            log.warn("Stopping connection throws exception, message:"+e.getMessage(), e);
+            log.warn("Stopping connection throws exception, message: {}", e.getMessage(), e);
         }
         Utils.close(subscriber, log);
         Utils.close(session, log);
         Utils.close(connection, log);
     }
 
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void onMessage(Message message) {
         if (!queue.offer(message)){
             log.warn("Could not add message to queue");
         }
     }
     
     
     /**
      * Checks whether string is empty
      * 
      * @param s1
      * @return True if input is null, an empty string, or a white space-only string
      */
     private boolean isEmpty(String s1) {
         return s1 == null || s1.trim().isEmpty();
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPropertiesPanel.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPropertiesPanel.java
index feabab276..50c10cfda 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPropertiesPanel.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPropertiesPanel.java
@@ -1,353 +1,352 @@
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
 
 package org.apache.jmeter.protocol.jms.control.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import javax.swing.BorderFactory;
 import javax.swing.DefaultCellEditor;
 import javax.swing.JButton;
 import javax.swing.JComboBox;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.table.AbstractTableModel;
 import javax.swing.table.TableColumn;
 
 import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
+import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.protocol.jms.sampler.JMSProperties;
 import org.apache.jmeter.protocol.jms.sampler.JMSProperty;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Handles input for Jms Properties
  * @since 2.11
  */
 public class JMSPropertiesPanel extends JPanel implements ActionListener {
 
     private static final long serialVersionUID = -2893899384410289131L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(JMSPropertiesPanel.class);
 
     private static final String ADD_COMMAND = "Add"; //$NON-NLS-1$
 
     private static final String DELETE_COMMAND = "Delete"; //$NON-NLS-1$
 
     private static final int COL_NAME = 0;
     private static final int COL_VALUE = 1;
     private static final int COL_TYPE = 2;
 
     private InnerTableModel tableModel;
 
     private JTable jmsPropertiesTable;
 
     private JButton addButton;
 
     private JButton deleteButton;
 
 
     /**
      * Default Constructor.
      */
     public JMSPropertiesPanel() {
         tableModel = new InnerTableModel();
         init();
     }
 
     public TestElement createTestElement() {
         JMSProperties jmsProperties = tableModel.jmsProperties;
         return (TestElement) jmsProperties.clone();
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @param el
      *            the test element to modify
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     public void modifyTestElement(TestElement el) {
         GuiUtils.stopTableEditing(jmsPropertiesTable);
         JMSProperties jmsProperties = (JMSProperties) el;
         jmsProperties.clear();
         jmsProperties.addTestElement((TestElement) tableModel.jmsProperties.clone());
     }
 
     /**
      * Clear GUI
      */
     public void clearGui() {
         tableModel.clearData();
         deleteButton.setEnabled(false);
     }
 
     /**
      * Configures GUI from el
      * @param el {@link TestElement}
      */
     public void configure(TestElement el) {
         tableModel.jmsProperties.clear();
         tableModel.jmsProperties.addTestElement((JMSProperties) el.clone());
         if (tableModel.getRowCount() != 0) {
             deleteButton.setEnabled(true);
         }
     }
 
     /**
      * Shows the main properties panel for this object.
      */
     private void init() {// called from ctor, so must not be overridable
         setLayout(new BorderLayout());
         setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
         add(createPropertiesPanel(), BorderLayout.CENTER);
     }
 
     @Override
     public void actionPerformed(ActionEvent e) {
         String action = e.getActionCommand();
 
         if (action.equals(DELETE_COMMAND)) {
             if (tableModel.getRowCount() > 0) {
                 // If a table cell is being edited, we must cancel the editing
                 // before deleting the row.
                 GuiUtils.cancelEditing(jmsPropertiesTable);
 
                 int rowSelected = jmsPropertiesTable.getSelectedRow();
 
                 if (rowSelected != -1) {
                     tableModel.removeRow(rowSelected);
                     tableModel.fireTableDataChanged();
 
                     // Disable the DELETE and SAVE buttons if no rows remaining
                     // after delete.
                     if (tableModel.getRowCount() == 0) {
                         deleteButton.setEnabled(false);
                     }
 
                     // Table still contains one or more rows, so highlight
                     // (select) the appropriate one.
                     else {
                         int rowToSelect = rowSelected;
 
                         if (rowSelected >= tableModel.getRowCount()) {
                             rowToSelect = rowSelected - 1;
                         }
 
                         jmsPropertiesTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                     }
                 }
             }
         } else if (action.equals(ADD_COMMAND)) {
             // If a table cell is being edited, we should accept the current
             // value and stop the editing before adding a new row.
             GuiUtils.stopTableEditing(jmsPropertiesTable);
 
             tableModel.addNewRow();
             tableModel.fireTableDataChanged();
 
             // Enable the DELETE and SAVE buttons if they are currently
             // disabled.
             if (!deleteButton.isEnabled()) {
                 deleteButton.setEnabled(true);
             }
 
             // Highlight (select) the appropriate row.
             int rowToSelect = tableModel.getRowCount() - 1;
             jmsPropertiesTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         } 
     }
 
     public JPanel createPropertiesPanel() {
         // create the JTable that holds JMSProperty per row
         jmsPropertiesTable = new JTable(tableModel);
         JMeterUtils.applyHiDPI(jmsPropertiesTable);
         jmsPropertiesTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
         jmsPropertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         jmsPropertiesTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
 
         
         TableColumn mechanismColumn = jmsPropertiesTable.getColumnModel().getColumn(COL_TYPE);
         mechanismColumn.setCellEditor(new TypeCellEditor());
 
         JPanel panel = new JPanel(new BorderLayout(0, 5));
         panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("jms_props"))); //$NON-NLS-1$
         panel.add(new JScrollPane(jmsPropertiesTable));
         panel.add(createButtonPanel(), BorderLayout.SOUTH);
         return panel;
     }
 
     private JButton createButton(String resName, char mnemonic, String command, boolean enabled) {
         JButton button = new JButton(JMeterUtils.getResString(resName));
         button.setMnemonic(mnemonic);
         button.setActionCommand(command);
         button.setEnabled(enabled);
         button.addActionListener(this);
         return button;
     }
 
     private JPanel createButtonPanel() {
         boolean tableEmpty = tableModel.getRowCount() == 0;
 
         addButton = createButton("add", 'A', ADD_COMMAND, true); //$NON-NLS-1$
         deleteButton = createButton("delete", 'D', DELETE_COMMAND, !tableEmpty); //$NON-NLS-1$
        
         // Button Panel
         JPanel buttonPanel = new JPanel();
         buttonPanel.add(addButton);
         buttonPanel.add(deleteButton);
         return buttonPanel;
     }
 
     private static class InnerTableModel extends AbstractTableModel {
         private static final long serialVersionUID = 4638155137475747946L;
         final JMSProperties jmsProperties;
 
         public InnerTableModel() {
             jmsProperties = new JMSProperties();
         }
 
         public void addNewRow() {
             jmsProperties.addJmsProperty(new JMSProperty("","",String.class.getName()));
         }
 
         public void clearData() {
             jmsProperties.clear();
             fireTableDataChanged();
         }
 
         public void removeRow(int row) {
             jmsProperties.removeJmsProperty(row);
         }
 
         @Override
         public boolean isCellEditable(int row, int column) {
             // all table cells are editable
             return true;
         }
 
         @Override
         public Class<?> getColumnClass(int column) {
             return getValueAt(0, column).getClass();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public int getRowCount() {
             return jmsProperties.getJmsPropertyCount();
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public int getColumnCount() {
             return 3;
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public String getColumnName(int column) {
             switch(column) {
                 case COL_NAME:
                     return "name";
                 case COL_VALUE:
                     return "value";
                 case COL_TYPE:
                     return "jms_properties_type";
                 default:
                     return null;
             }
         }
 
         /**
          * Required by table model interface.
          */
         @Override
         public Object getValueAt(int row, int column) {
             JMSProperty property = jmsProperties.getJmsProperty(row);
 
             switch (column){
                 case COL_NAME:
                     return property.getName();
                 case COL_VALUE:
                     return property.getValue();
                 case COL_TYPE:
                     return property.getType();
                 default:
                     return null;
             }
         }
 
         @Override
         public void setValueAt(Object value, int row, int column) {
             JMSProperty property = jmsProperties.getJmsProperty(row);
-            if(log.isDebugEnabled()) {
-                log.debug("Setting jms property value: " + value);
-            }
+            log.debug("Setting jms property value: {}", value);
             switch (column){
                 case COL_NAME:
                     property.setName((String)value);
                     break;
                 case COL_VALUE:
                     property.setValue((String) value);
                     break;
                 case COL_TYPE:
                     property.setType((String) value);
                     break;
                 default:
                     break;
             }
         }
     }
     
     private static class TypeCellEditor extends DefaultCellEditor {
 
         /**
          * 
          */
         private static final long serialVersionUID = 1L;
 
         public TypeCellEditor() {
             super(new JComboBox<>(new Object[]{
                     Boolean.class.getName(),
                     Byte.class.getName(),
                     Short.class.getName(),
                     Integer.class.getName(),
                     Long.class.getName(),
                     Float.class.getName(),
                     Double.class.getName(),
                     String.class.getName()
             }));
         }
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java
index cd02fdd86..9eb3e61b6 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java
@@ -1,415 +1,416 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.Date;
 import java.util.function.Predicate;
 import java.util.regex.Pattern;
 
 import javax.jms.DeliveryMode;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 
 import org.apache.commons.lang3.StringUtils;
+import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  * BaseJMSSampler is an abstract class which provides implementation for common
  * properties. Rather than duplicate the code, it's contained in the base class.
  */
 public abstract class BaseJMSSampler extends AbstractSampler {
 
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
     
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass(); 
+    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJMSSampler.class);
 
     //++ These are JMX file attribute names and must not be changed
     private static final String JNDI_INITIAL_CONTEXT_FAC = "jms.initial_context_factory"; // $NON-NLS-1$
 
     private static final String PROVIDER_URL = "jms.provider_url"; // $NON-NLS-1$
 
     private static final String CONN_FACTORY = "jms.connection_factory"; // $NON-NLS-1$
 
     // N.B. Cannot change value, as that is used in JMX files
     private static final String DEST = "jms.topic"; // $NON-NLS-1$
 
     private static final String PRINCIPAL = "jms.security_principle"; // $NON-NLS-1$
 
     private static final String CREDENTIALS = "jms.security_credentials"; // $NON-NLS-1$
 
     /*
      * The number of samples to aggregate
      */
     private static final String ITERATIONS = "jms.iterations"; // $NON-NLS-1$
 
     private static final String USE_AUTH = "jms.authenticate"; // $NON-NLS-1$
 
     private static final String USE_PROPERTIES_FILE = "jms.jndi_properties"; // $NON-NLS-1$
 
     /*
      * If true, store the response in the sampleResponse
      * (N.B. do not change the value, as it is used in JMX files)
      */
     private static final String STORE_RESPONSE = "jms.read_response"; // $NON-NLS-1$
 
     // Is Destination setup static? else dynamic
     private static final String DESTINATION_STATIC = "jms.destination_static"; // $NON-NLS-1$
     private static final boolean DESTINATION_STATIC_DEFAULT = true; // default to maintain compatibility
 
     /** Property name for regex of error codes which force reconnection **/
     private static final String ERROR_RECONNECT_ON_CODES = "jms_error_reconnect_on_codes"; // $NON-NLS-1$
     private transient Predicate<String> isReconnectErrorCode = e -> false;
 
     //-- End of JMX file attribute names
 
     // See BUG 45460. We need to keep the resource in order to interpret existing files
     private static final String REQUIRED = JMeterUtils.getResString("jms_auth_required"); // $NON-NLS-1$
 
     public BaseJMSSampler() {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public SampleResult sample(Entry e) {
         return this.sample();
     }
 
     public abstract SampleResult sample();
 
     // ------------- get/set properties ----------------------//
     /**
      * set the initial context factory
      *
      * @param icf the initial context factory
      */
     public void setJNDIIntialContextFactory(String icf) {
         setProperty(JNDI_INITIAL_CONTEXT_FAC, icf);
     }
 
     /**
      * method returns the initial context factory for jndi initial context
      * lookup.
      *
      * @return the initial context factory
      */
     public String getJNDIInitialContextFactory() {
         return getPropertyAsString(JNDI_INITIAL_CONTEXT_FAC);
     }
 
     /**
      * set the provider user for jndi
      *
      * @param url the provider URL
      */
     public void setProviderUrl(String url) {
         setProperty(PROVIDER_URL, url);
     }
 
     /**
      * method returns the provider url for jndi to connect to
      *
      * @return the provider URL
      */
     public String getProviderUrl() {
         return getPropertyAsString(PROVIDER_URL);
     }
 
     /**
      * set the connection factory for
      *
      * @param factory the connection factory
      */
     public void setConnectionFactory(String factory) {
         setProperty(CONN_FACTORY, factory);
     }
 
     /**
      * return the connection factory parameter used to lookup the connection
      * factory from the JMS server
      *
      * @return the connection factory
      */
     public String getConnectionFactory() {
         return getPropertyAsString(CONN_FACTORY);
     }
 
     /**
      * set the destination (topic or queue name)
      *
      * @param dest the destination
      */
     public void setDestination(String dest) {
         setProperty(DEST, dest);
     }
 
     /**
      * return the destination (topic or queue name)
      *
      * @return the destination
      */
     public String getDestination() {
         return getPropertyAsString(DEST);
     }
 
     /**
      * set the username to login into the jms server if needed
      *
      * @param user the name of the user
      */
     public void setUsername(String user) {
         setProperty(PRINCIPAL, user);
     }
 
     /**
      * return the username used to login to the jms server
      *
      * @return the username used to login to the jms server
      */
     public String getUsername() {
         return getPropertyAsString(PRINCIPAL);
     }
 
     /**
      * Set the password to login to the jms server
      *
      * @param pwd the password to use for login on the jms server
      */
     public void setPassword(String pwd) {
         setProperty(CREDENTIALS, pwd);
     }
 
     /**
      * return the password used to login to the jms server
      *
      * @return the password used to login to the jms server
      */
     public String getPassword() {
         return getPropertyAsString(CREDENTIALS);
     }
 
     /**
      * set the number of iterations the sampler should aggregate
      *
      * @param count the number of iterations
      */
     public void setIterations(String count) {
         setProperty(ITERATIONS, count);
     }
 
     /**
      * get the number of samples to aggregate
      *
      * @return String containing the number of samples to aggregate
      */
     public String getIterations() {
         return getPropertyAsString(ITERATIONS);
     }
 
     /**
      * get the number of samples to aggregate
      *
      * @return int containing the number of samples to aggregate
      */
     public int getIterationCount() {
         return getPropertyAsInt(ITERATIONS);
     }
 
     /**
      * Set whether authentication is required for JNDI
      *
      * @param useAuth flag whether to use authentication
      */
     public void setUseAuth(boolean useAuth) {
         setProperty(USE_AUTH, useAuth);
     }
 
     /**
      * return whether jndi requires authentication
      *
      * @return whether jndi requires authentication
      */
     public boolean isUseAuth() {
         final String useAuth = getPropertyAsString(USE_AUTH);
         return useAuth.equalsIgnoreCase("true") || useAuth.equals(REQUIRED); // $NON-NLS-1$
     }
 
     /**
      * set whether the sampler should store the response or not
      *
      * @param read whether the sampler should store the response or not
      */
     public void setReadResponse(String read) {
         setProperty(STORE_RESPONSE, read);
     }
 
     /**
      * return whether the sampler should store the response
      *
      * @return whether the sampler should store the response
      */
     public String getReadResponse() {
         return getPropertyAsString(STORE_RESPONSE);
     }
 
     /**
      * return whether the sampler should store the response
      *
      * @return boolean: whether the sampler should read the response
      */
     public boolean getReadResponseAsBoolean() {
         return getPropertyAsBoolean(STORE_RESPONSE);
     }
 
     /**
      * if the sampler should use jndi.properties file, call the method with the string "true"
      *
      * @param properties flag whether to use <em>jndi.properties</em> file
      */
     public void setUseJNDIProperties(String properties) {
         setProperty(USE_PROPERTIES_FILE, properties);
     }
 
     /**
      * return whether the sampler should use properties file instead of UI
      * parameters.
      *
      * @return the string "true" when the sampler should use properties file
      *         instead of UI parameters, the string "false" otherwise.
      */
     public String getUseJNDIProperties() {
         return getPropertyAsString(USE_PROPERTIES_FILE);
     }
 
     /**
      * return the properties as boolean true/false.
      *
      * @return whether the sampler should use properties file instead of UI parameters.
      */
     public boolean getUseJNDIPropertiesAsBoolean() {
         return getPropertyAsBoolean(USE_PROPERTIES_FILE);
     }
 
     /**
      * if the sampler should use a static destination, call the method with true
      *
      * @param isStatic flag whether the destination is a static destination
      */
     public void setDestinationStatic(boolean isStatic) {
         setProperty(DESTINATION_STATIC, isStatic, DESTINATION_STATIC_DEFAULT);
     }
 
     /**
      * return whether the sampler should use a static destination.
      *
      * @return  whether the sampler should use a static destination.
      */
     public boolean isDestinationStatic(){
         return getPropertyAsBoolean(DESTINATION_STATIC, DESTINATION_STATIC_DEFAULT);
     }
 
     /**
      * Returns a String with the JMS Message Header values.
      *
      * @param message JMS Message
      * @return String with message header values.
      */
     public static String getMessageHeaders(Message message) {
         final StringBuilder response = new StringBuilder(256);
         try {
             response.append("JMS Message Header Attributes:");
             response.append("\n   Correlation ID: ");
             response.append(message.getJMSCorrelationID());
 
             response.append("\n   Delivery Mode: ");
             if (message.getJMSDeliveryMode() == DeliveryMode.PERSISTENT) {
                 response.append("PERSISTANT");
             } else {
                 response.append("NON-PERSISTANT");
             }
 
             final Destination destination = message.getJMSDestination();
 
             response.append("\n   Destination: ");
             response.append(destination == null ? null : destination
                 .toString());
 
             response.append("\n   Expiration: ");
             response.append(new Date(message.getJMSExpiration()));
 
             response.append("\n   Message ID: ");
             response.append(message.getJMSMessageID());
 
             response.append("\n   Priority: ");
             response.append(message.getJMSPriority());
 
             response.append("\n   Redelivered: ");
             response.append(message.getJMSRedelivered());
 
             final Destination replyTo = message.getJMSReplyTo();
             response.append("\n   Reply to: ");
             response.append(replyTo == null ? null : replyTo.toString());
 
             response.append("\n   Timestamp: ");
             response.append(new Date(message.getJMSTimestamp()));
 
             response.append("\n   Type: ");
             response.append(message.getJMSType());
 
             response.append("\n\n");
 
         } catch (JMSException e) {
             LOGGER.warn(
                     "Can't extract message headers", e);
         }
 
         return response.toString();
     }
 
     public String getReconnectionErrorCodes() {
         return getPropertyAsString(ERROR_RECONNECT_ON_CODES);
     }
 
     public void setReconnectionErrorCodes(String reconnectionErrorCodes) {
         setProperty(ERROR_RECONNECT_ON_CODES, reconnectionErrorCodes);
     }
 
     public Predicate<String> getIsReconnectErrorCode() {
         return isReconnectErrorCode;
     }
 
     /**
      * 
      */
     protected void configureIsReconnectErrorCode() {
         String regex = StringUtils.trimToEmpty(getReconnectionErrorCodes());
         if (regex.isEmpty()) {
             isReconnectErrorCode = e -> false;
         } else {
             isReconnectErrorCode = Pattern.compile(regex).asPredicate();
         }
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/FixedQueueExecutor.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/FixedQueueExecutor.java
index b7ac5b37d..c51235a65 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/FixedQueueExecutor.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/FixedQueueExecutor.java
@@ -1,114 +1,113 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageProducer;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Request/reply executor with a fixed reply queue. <br>
  *
  * Used by JMS Sampler (Point to Point)
  *
  */
 public class FixedQueueExecutor implements QueueExecutor {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FixedQueueExecutor.class);
 
     /** Sender. */
     private final MessageProducer producer;
 
     /** Timeout used for waiting on message. */
     private final int timeout;
 
     private final boolean useReqMsgIdAsCorrelId;
 
     /**
      * Constructor.
      *
      * @param producer
      *            the queue to send the message on
      * @param timeout
      *            timeout to use for the return message
      * @param useReqMsgIdAsCorrelId
      *            whether to use the request message id as the correlation id
      */
     public FixedQueueExecutor(MessageProducer producer, int timeout, boolean useReqMsgIdAsCorrelId) {
         this.producer = producer;
         this.timeout = timeout;
         this.useReqMsgIdAsCorrelId = useReqMsgIdAsCorrelId;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Message sendAndReceive(Message request, 
             int deliveryMode, 
             int priority, 
             long expiration) throws JMSException {
         String id = request.getJMSCorrelationID();
         if(id == null && !useReqMsgIdAsCorrelId){
             throw new IllegalArgumentException("Correlation id is null. Set the JMSCorrelationID header.");
         }
         final CountDownLatch countDownLatch = new CountDownLatch(1);
         final MessageAdmin admin = MessageAdmin.getAdmin();
         if(useReqMsgIdAsCorrelId) {// msgId not available until after send() is called
             // Note: there is only one admin object which is shared between all threads
             synchronized (admin) {// interlock with Receiver
                 producer.send(request, deliveryMode, priority, expiration);
                 id=request.getJMSMessageID();
                 admin.putRequest(id, request, countDownLatch);
             }
         } else {
             admin.putRequest(id, request, countDownLatch);            
             producer.send(request, deliveryMode, priority, expiration);
         }
 
         try {
-            if (log.isDebugEnabled()) {
-                log.debug(Thread.currentThread().getName()+" will wait for reply " + id + " started on " + System.currentTimeMillis());
-            }
+            log.debug("{} will wait for reply {} started on {}", 
+                    Thread.currentThread().getName(), id, System.currentTimeMillis());
+            
             // This used to be request.wait(timeout_ms), where 0 means forever
             // However 0 means return immediately for the latch
             if (timeout == 0){
                 countDownLatch.await(); //
             } else {
                 if(!countDownLatch.await(timeout, TimeUnit.MILLISECONDS)) {
                     log.debug("Timeout reached before getting a reply message");
                 }
             }
-            if (log.isDebugEnabled()) {
-                log.debug(Thread.currentThread().getName()+" done waiting for " + id + " on "+request+" ended on " + System.currentTimeMillis());
-            }
-
+            log.debug("{} done waiting for {} on {} ended on {}",
+                    Thread.currentThread().getName(), 
+                    id, request, System.currentTimeMillis());
         } catch (InterruptedException e) {
             log.warn("Interrupt exception caught", e);
             Thread.currentThread().interrupt();
         }
         return admin.get(id);
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSSampler.java
index db2e3c716..c16fdc247 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/JMSSampler.java
@@ -1,561 +1,551 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.Date;
 import java.util.Hashtable;
 import java.util.Map;
 
 import javax.jms.DeliveryMode;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.Queue;
 import javax.jms.QueueConnection;
 import javax.jms.QueueConnectionFactory;
 import javax.jms.QueueSender;
 import javax.jms.QueueSession;
 import javax.jms.Session;
 import javax.jms.TextMessage;
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.naming.NamingException;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class implements the JMS Point-to-Point sampler
  *
  */
 public class JMSSampler extends AbstractSampler implements ThreadListener {
 
-    private static final Logger LOGGER = LoggingManager.getLoggerForClass();
+    private static final Logger LOGGER = LoggerFactory.getLogger(JMSSampler.class);
 
     private static final long serialVersionUID = 233L;
 
     private static final int DEFAULT_TIMEOUT = 2000;
     private static final String DEFAULT_TIMEOUT_STRING = Integer.toString(DEFAULT_TIMEOUT);
 
     //++ These are JMX names, and must not be changed
     private static final String JNDI_INITIAL_CONTEXT_FACTORY = "JMSSampler.initialContextFactory"; // $NON-NLS-1$
 
     private static final String JNDI_CONTEXT_PROVIDER_URL = "JMSSampler.contextProviderUrl"; // $NON-NLS-1$
 
     private static final String JNDI_PROPERTIES = "JMSSampler.jndiProperties"; // $NON-NLS-1$
 
     private static final String TIMEOUT = "JMSSampler.timeout"; // $NON-NLS-1$
 
     private static final String JMS_PRIORITY = "JMSSampler.priority"; // $NON-NLS-1$
 
     private static final String JMS_EXPIRATION = "JMSSampler.expiration"; // $NON-NLS-1$
 
     private static final String JMS_SELECTOR = "JMSSampler.jmsSelector"; // $NON-NLS-1$
 
     private static final String JMS_SELECTOR_DEFAULT = ""; // $NON-NLS-1$
 
     private static final String IS_ONE_WAY = "JMSSampler.isFireAndForget"; // $NON-NLS-1$
 
     private static final String JMS_PROPERTIES = "arguments"; // $NON-NLS-1$
 
     private static final String RECEIVE_QUEUE = "JMSSampler.ReceiveQueue"; // $NON-NLS-1$
 
     private static final String XML_DATA = "HTTPSamper.xml_data"; // $NON-NLS-1$
 
     private static final String SEND_QUEUE = "JMSSampler.SendQueue"; // $NON-NLS-1$
 
     private static final String QUEUE_CONNECTION_FACTORY_JNDI = "JMSSampler.queueconnectionfactory"; // $NON-NLS-1$
 
     private static final String IS_NON_PERSISTENT = "JMSSampler.isNonPersistent"; // $NON-NLS-1$
 
     private static final String USE_REQ_MSGID_AS_CORRELID = "JMSSampler.useReqMsgIdAsCorrelId"; // $NON-NLS-1$
 
     private static final String USE_RES_MSGID_AS_CORRELID = "JMSSampler.useResMsgIdAsCorrelId"; // $NON-NLS-1$
 
     private static final boolean USE_RES_MSGID_AS_CORRELID_DEFAULT = false; // Default to be applied
 
 
     //--
 
     // Should we use java.naming.security.[principal|credentials] to create the QueueConnection?
     private static final boolean USE_SECURITY_PROPERTIES =
         JMeterUtils.getPropDefault("JMSSampler.useSecurity.properties", true); // $NON-NLS-1$
 
     //
     // Member variables
     //
     /** Queue for receiving messages (if applicable). */
     private transient Queue receiveQueue;
 
     /** The session with the queueing system. */
     private transient QueueSession session;
 
     /** Connection to the queueing system. */
     private transient QueueConnection connection;
 
     /** The executor for (pseudo) synchronous communication. */
     private transient QueueExecutor executor;
 
     /** Producer of the messages. */
     private transient QueueSender producer;
 
     private transient Receiver receiverThread = null;
 
     private transient Throwable thrown = null;
 
     private transient Context context = null;
 
     /**
      * {@inheritDoc}
      */
     @Override
     public SampleResult sample(Entry entry) {
         SampleResult res = new SampleResult();
         res.setSampleLabel(getName());
         res.setSamplerData(getContent());
         res.setSuccessful(false); // Assume failure
         res.setDataType(SampleResult.TEXT);
         res.sampleStart();
 
         try {
             TextMessage msg = createMessage();
             if (isOneway()) {
                 int deliveryMode = isNonPersistent() ? 
                         DeliveryMode.NON_PERSISTENT:DeliveryMode.PERSISTENT;
                 producer.send(msg, deliveryMode, Integer.parseInt(getPriority()), 
                         Long.parseLong(getExpiration()));
                 res.setRequestHeaders(Utils.messageProperties(msg));
                 res.setResponseOK();
                 res.setResponseData("Oneway request has no response data", null);
             } else {
                 if (!useTemporyQueue()) {
                     msg.setJMSReplyTo(receiveQueue);
                 }
                 Message replyMsg = executor.sendAndReceive(msg,
                         isNonPersistent() ? DeliveryMode.NON_PERSISTENT : DeliveryMode.PERSISTENT, 
                         Integer.parseInt(getPriority()), 
                         Long.parseLong(getExpiration()));
                 res.setRequestHeaders(Utils.messageProperties(msg));
                 if (replyMsg == null) {
                     res.setResponseMessage("No reply message received");
                 } else {
                     if (replyMsg instanceof TextMessage) {
                         res.setResponseData(((TextMessage) replyMsg).getText(), null);
                     } else {
                         res.setResponseData(replyMsg.toString(), null);
                     }
                     res.setResponseHeaders(Utils.messageProperties(replyMsg));
                     res.setResponseOK();
                 }
             }
         } catch (Exception e) {
             LOGGER.warn(e.getLocalizedMessage(), e);
             if (thrown != null){
                 res.setResponseMessage(thrown.toString());
             } else {                
                 res.setResponseMessage(e.getLocalizedMessage());
             }
         }
         res.sampleEnd();
         return res;
     }
 
     private TextMessage createMessage() throws JMSException {
         if (session == null) {
             throw new IllegalStateException("Session may not be null while creating message");
         }
         TextMessage msg = session.createTextMessage();
         msg.setText(getContent());
         addJMSProperties(msg);
         return msg;
     }
 
     private void addJMSProperties(TextMessage msg) throws JMSException {
         Utils.addJMSProperties(msg, getJMSProperties().getJmsPropertysAsMap());
     }
 
     /** 
      * @return {@link JMSProperties} JMS Properties
      */
     public JMSProperties getJMSProperties() {
         Object o = getProperty(JMS_PROPERTIES).getObjectValue();
         JMSProperties jmsProperties = null;
         // Backward compatibility with versions <= 2.10
         if(o instanceof Arguments) {
             jmsProperties = Utils.convertArgumentsToJmsProperties((Arguments)o);
         } else {
             jmsProperties = (JMSProperties) o;
         }
         if(jmsProperties == null) {
             jmsProperties = new JMSProperties();
             setJMSProperties(jmsProperties);
         }
         return jmsProperties;
     }
     
     /**
      * @param jmsProperties JMS Properties
      */
     public void setJMSProperties(JMSProperties jmsProperties) {
         setProperty(new TestElementProperty(JMS_PROPERTIES, jmsProperties));
     }
 
     public Arguments getJNDIProperties() {
         return getArguments(JMSSampler.JNDI_PROPERTIES);
     }
 
     public void setJNDIProperties(Arguments args) {
         setProperty(new TestElementProperty(JMSSampler.JNDI_PROPERTIES, args));
     }
 
     public String getQueueConnectionFactory() {
         return getPropertyAsString(QUEUE_CONNECTION_FACTORY_JNDI);
     }
 
     public void setQueueConnectionFactory(String qcf) {
         setProperty(QUEUE_CONNECTION_FACTORY_JNDI, qcf);
     }
 
     public String getSendQueue() {
         return getPropertyAsString(SEND_QUEUE);
     }
 
     public void setSendQueue(String name) {
         setProperty(SEND_QUEUE, name);
     }
 
     public String getReceiveQueue() {
         return getPropertyAsString(RECEIVE_QUEUE);
     }
 
     public void setReceiveQueue(String name) {
         setProperty(RECEIVE_QUEUE, name);
     }
 
     public String getContent() {
         return getPropertyAsString(XML_DATA);
     }
 
     public void setContent(String content) {
         setProperty(XML_DATA, content);
     }
 
     public boolean isOneway() {
         return getPropertyAsBoolean(IS_ONE_WAY);
     }
 
     public boolean isNonPersistent() {
         return getPropertyAsBoolean(IS_NON_PERSISTENT);
     }
 
     /**
      * Which request field to use for correlation?
      * 
      * @return true if correlation should use the request JMSMessageID rather than JMSCorrelationID
      */
     public boolean isUseReqMsgIdAsCorrelId() {
         return getPropertyAsBoolean(USE_REQ_MSGID_AS_CORRELID);
     }
 
     /**
      * Which response field to use for correlation?
      * 
      * @return true if correlation should use the response JMSMessageID rather than JMSCorrelationID
      */
     public boolean isUseResMsgIdAsCorrelId() {
         return getPropertyAsBoolean(USE_RES_MSGID_AS_CORRELID, USE_RES_MSGID_AS_CORRELID_DEFAULT);
     }
 
     public String getInitialContextFactory() {
         return getPropertyAsString(JMSSampler.JNDI_INITIAL_CONTEXT_FACTORY);
     }
 
     public String getContextProvider() {
         return getPropertyAsString(JMSSampler.JNDI_CONTEXT_PROVIDER_URL);
     }
 
     public void setIsOneway(boolean isOneway) {
         setProperty(new BooleanProperty(IS_ONE_WAY, isOneway));
     }
 
     public void setNonPersistent(boolean value) {
         setProperty(new BooleanProperty(IS_NON_PERSISTENT, value));
     }
 
     public void setUseReqMsgIdAsCorrelId(boolean value) {
         setProperty(new BooleanProperty(USE_REQ_MSGID_AS_CORRELID, value));
     }
 
     public void setUseResMsgIdAsCorrelId(boolean value) {
         setProperty(USE_RES_MSGID_AS_CORRELID, value, USE_RES_MSGID_AS_CORRELID_DEFAULT);
     }
 
     @Override
     public String toString() {
         return getQueueConnectionFactory() + ", queue: " + getSendQueue();
     }
 
     @Override
     public void threadStarted() {
         logThreadStart();
 
         thrown = null;
         try {
             context = getInitialContext();
             Object obj = context.lookup(getQueueConnectionFactory());
             if (!(obj instanceof QueueConnectionFactory)) {
                 String msg = "QueueConnectionFactory expected, but got "
                     + (obj != null ? obj.getClass().getName() : "null");
-                LOGGER.fatalError(msg);
+                LOGGER.error(msg);
                 throw new IllegalStateException(msg);
             }
             QueueConnectionFactory factory = (QueueConnectionFactory) obj;
             Queue sendQueue = (Queue) context.lookup(getSendQueue());
 
             if (!useTemporyQueue()) {
                 receiveQueue = (Queue) context.lookup(getReceiveQueue());
                 receiverThread = Receiver.createReceiver(factory, receiveQueue, Utils.getFromEnvironment(context, Context.SECURITY_PRINCIPAL), 
                         Utils.getFromEnvironment(context, Context.SECURITY_CREDENTIALS)
                         , isUseResMsgIdAsCorrelId(), getJMSSelector());
             }
 
             String principal = null;
             String credentials = null;
             if (USE_SECURITY_PROPERTIES){
                 principal = Utils.getFromEnvironment(context, Context.SECURITY_PRINCIPAL);
                 credentials = Utils.getFromEnvironment(context, Context.SECURITY_CREDENTIALS);
             }
             if (principal != null && credentials != null) {
                 connection = factory.createQueueConnection(principal, credentials);
             } else {
                 connection = factory.createQueueConnection();
             }
 
             session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
 
-            if (LOGGER.isDebugEnabled()) {
-                LOGGER.debug("Session created");
-            }
+            LOGGER.debug("Session created");
 
             if (isOneway()) {
                 producer = session.createSender(sendQueue);
                 if (isNonPersistent()) {
                     producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                 }
                 producer.setPriority(Integer.parseInt(getPriority()));
                 producer.setTimeToLive(Long.parseLong(getExpiration()));
             } else {
 
                 if (useTemporyQueue()) {
                     executor = new TemporaryQueueExecutor(session, sendQueue);
                 } else {
                     producer = session.createSender(sendQueue);
                     executor = new FixedQueueExecutor(producer, getTimeoutAsInt(), isUseReqMsgIdAsCorrelId());
                 }
             }
-            if (LOGGER.isDebugEnabled()) {
-                LOGGER.debug("Starting connection");
-            }
+            LOGGER.debug("Starting connection");
 
             connection.start();
 
-            if (LOGGER.isDebugEnabled()) {
-                LOGGER.debug("Connection started");
-            }
+            LOGGER.debug("Connection started");
         } catch (Exception | NoClassDefFoundError e) {
             thrown = e;
             LOGGER.error(e.getLocalizedMessage(), e);
         }
     }
 
     private Context getInitialContext() throws NamingException {
         Hashtable<String, String> table = new Hashtable<>();
 
         if (getInitialContextFactory() != null && getInitialContextFactory().trim().length() > 0) {
-            if (LOGGER.isDebugEnabled()) {
-                LOGGER.debug("Using InitialContext [" + getInitialContextFactory() + "]");
-            }
+            LOGGER.debug("Using InitialContext [{}]", getInitialContextFactory());
             table.put(Context.INITIAL_CONTEXT_FACTORY, getInitialContextFactory());
         }
         if (getContextProvider() != null && getContextProvider().trim().length() > 0) {
-            if (LOGGER.isDebugEnabled()) {
-                LOGGER.debug("Using Provider [" + getContextProvider() + "]");
-            }
+            LOGGER.debug("Using Provider [{}]", getContextProvider());
             table.put(Context.PROVIDER_URL, getContextProvider());
         }
         Map<String, String> map = getArguments(JMSSampler.JNDI_PROPERTIES).getArgumentsAsMap();
         if (LOGGER.isDebugEnabled()) {
             if (map.isEmpty()) {
                 LOGGER.debug("Empty JNDI properties");
             } else {
-                LOGGER.debug("Number of JNDI properties: " + map.size());
+                LOGGER.debug("Number of JNDI properties: {}", map.size());
             }
         }
         for (Map.Entry<String, String> me : map.entrySet()) {
             table.put(me.getKey(), me.getValue());
         }
 
         Context context = new InitialContext(table);
         if (LOGGER.isDebugEnabled()) {
             printEnvironment(context);
         }
         return context;
     }
 
     private void printEnvironment(Context context) throws NamingException {
         try {
             Hashtable<?,?> env = context.getEnvironment();
             if(env != null) {
                 LOGGER.debug("Initial Context Properties");
                 for (Map.Entry<?, ?> entry : env.entrySet()) {
-                    LOGGER.debug(entry.getKey() + "=" + entry.getValue());
+                    LOGGER.debug("{}={}", entry.getKey(), entry.getValue());
                 }
             } else {
                 LOGGER.warn("context.getEnvironment() returned null (should not happen according to javadoc but non compliant implementation can return this)");
             }
         } catch (javax.naming.OperationNotSupportedException ex) {
             // Some JNDI implementation can return this
             LOGGER.warn("context.getEnvironment() not supported by implementation ");
         }
     }
 
     private void logThreadStart() {
         if (LOGGER.isDebugEnabled()) {
             LOGGER.debug("Thread started " + new Date());
             LOGGER.debug("JMSSampler: [" + Thread.currentThread().getName() + "], hashCode=[" + hashCode() + "]");
             LOGGER.debug("QCF: [" + getQueueConnectionFactory() + "], sendQueue=[" + getSendQueue() + "]");
             LOGGER.debug("Timeout             = " + getTimeout() + "]");
             LOGGER.debug("Use temporary queue =" + useTemporyQueue() + "]");
             LOGGER.debug("Reply queue         =" + getReceiveQueue() + "]");
         }
     }
 
     private int getTimeoutAsInt() {
         if (getPropertyAsInt(TIMEOUT) < 1) {
             return DEFAULT_TIMEOUT;
         }
         return getPropertyAsInt(TIMEOUT);
     }
 
     public String getTimeout() {
         return getPropertyAsString(TIMEOUT, DEFAULT_TIMEOUT_STRING);
     }
     
     public String getExpiration() {
         String expiration = getPropertyAsString(JMS_EXPIRATION);
         if (expiration.length() == 0) {
             return Utils.DEFAULT_NO_EXPIRY;
         } else {
             return expiration;
         }
     }
 
     public String getPriority() {
         String priority = getPropertyAsString(JMS_PRIORITY);
         if (priority.length() == 0) {
             return Utils.DEFAULT_PRIORITY_4;
         } else {
             return priority;
         }
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public void threadFinished() {
-        LOGGER.debug("Thread ended " + new Date());
+        LOGGER.debug("Thread ended {}", new Date());
 
         if (context != null) {
             try {
                 context.close();
             } catch (NamingException ignored) {
                 // ignore
             }
         }
         Utils.close(session, LOGGER);
         Utils.close(connection, LOGGER);
         if (receiverThread != null) {
             receiverThread.deactivate();
         }
     }
 
     private boolean useTemporyQueue() {
         String recvQueue = getReceiveQueue();
         return recvQueue == null || recvQueue.trim().length() == 0;
     }
 
     public void setArguments(Arguments args) {
         setProperty(new TestElementProperty(JMSSampler.JMS_PROPERTIES, args));
     }
 
     public Arguments getArguments(String name) {
         return (Arguments) getProperty(name).getObjectValue();
     }
 
     public void setTimeout(String s) {
         setProperty(JMSSampler.TIMEOUT, s);
     }
     
     public void setPriority(String s) {
         setProperty(JMSSampler.JMS_PRIORITY, s, Utils.DEFAULT_PRIORITY_4);
     }
     
     public void setExpiration(String s) {
         setProperty(JMSSampler.JMS_EXPIRATION, s, Utils.DEFAULT_NO_EXPIRY);
     }
 
     /**
      * @return String JMS Selector
      */
     public String getJMSSelector() {
         return getPropertyAsString(JMSSampler.JMS_SELECTOR, JMS_SELECTOR_DEFAULT);
     }
 
     /**
      * @param selector String selector
      */
     public void setJMSSelector(String selector) {
         setProperty(JMSSampler.JMS_SELECTOR, selector, JMS_SELECTOR_DEFAULT);
     }
     
     /**
      * @param string name of the initial context factory to use
      */
     public void setInitialContextFactory(String string) {
         setProperty(JNDI_INITIAL_CONTEXT_FACTORY, string);
 
     }
 
     /**
      * @param string url of the provider
      */
     public void setContextProvider(String string) {
         setProperty(JNDI_CONTEXT_PROVIDER_URL, string);
 
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/MessageAdmin.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/MessageAdmin.java
index 93fdc1387..00fb5e9ad 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/MessageAdmin.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/MessageAdmin.java
@@ -1,163 +1,154 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.CountDownLatch;
 
 import javax.jms.Message;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
+
 
 /**
  * Administration of messages.
  *
  */
 public class MessageAdmin {
 
     private static final class PlaceHolder {
         private final CountDownLatch latch;
         private final Object request;
     
         private Object reply;
     
         PlaceHolder(Object original, CountDownLatch latch) {
             this.request = original;
             this.latch = latch;
         }
     
         void setReply(Object reply) {
             this.reply = reply;
         }
     
         public Object getReply() {
             return reply;
         }
     
         public Object getRequest() {
             return request;
         }
     
         boolean hasReply() {
             return reply != null;
         }
     
         @Override
         public String toString() {
             return "request=" + request + ", reply=" + reply;
         }
     
         /**
          * @return the latch
          */
         public CountDownLatch getLatch() {
             return latch;
         }
     }
 
     private static final MessageAdmin SINGLETON = new MessageAdmin();
 
     private final Map<String, PlaceHolder> table = new ConcurrentHashMap<>();
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(MessageAdmin.class);
 
     private MessageAdmin() {
     }
 
     /**
      * Get the singleton MessageAdmin object
      * 
      * @return singleton instance
      */
     public static MessageAdmin getAdmin() {
         return SINGLETON;
     }
 
     /**
      * Store a request under the given id, so that an arriving reply can be
      * associated with this request and the waiting party can be signaled by
      * means of a {@link CountDownLatch}
      *
      * @param id
      *            id of the request
      * @param request
      *            request object to store under id
      * @param latch
      *            communication latch to signal when a reply for this request
      *            was received
      */
     public void putRequest(String id, Message request, CountDownLatch latch) {
-        if (log.isDebugEnabled()) {
-            log.debug("REQ_ID [" + id + "]");
-        }
+        log.debug("REQ_ID [{}]", id);
         table.put(id, new PlaceHolder(request, latch));
     }
 
     /**
      * Try to associate a reply to a previously stored request. If a matching
      * request is found, the owner of the request will be notified with the
      * registered {@link CountDownLatch}
      * 
      * @param id
      *            id of the request
      * @param reply
      *            object with the reply
      */
     public void putReply(String id, Message reply) {
         PlaceHolder holder = table.get(id);
-        if (log.isDebugEnabled()) {
-            log.debug("RPL_ID [" + id + "] for holder " + holder);
-        }
+        log.debug("RPL_ID [{}] for holder {}", id, holder);
         if (holder != null) {
             holder.setReply(reply);
             CountDownLatch latch = holder.getLatch();
-            if (log.isDebugEnabled()) {
-                log.debug(Thread.currentThread().getName()+" releasing latch : " + latch);
-            }
+            log.debug("{} releasing latch : {}", Thread.currentThread().getName(), latch);
             latch.countDown();
-            if (log.isDebugEnabled()) {
-                log.debug(Thread.currentThread().getName()+" released latch : " + latch);
-            }
+            log.debug("{} released latch : {}", Thread.currentThread().getName(), latch);
         } else {
             if (log.isDebugEnabled()) {
-                log.debug("Failed to match reply: " + reply);
+                log.debug("Failed to match reply: {}", reply);
             }
         }
     }
 
     /**
      * Get the reply message.
      *
      * @param id
      *            the id of the message
      * @return the received message or <code>null</code>
      */
     public Message get(String id) {
         PlaceHolder holder = table.remove(id);
-        if (log.isDebugEnabled()) {
-            log.debug("GET_ID [" + id + "] for " + holder);
-        }
+        log.debug("GET_ID [{}] for {}", id, holder);
         if (holder == null || !holder.hasReply()) {
-            log.debug("Message with " + id + " not found.");
+            log.debug("Message with {} not found.", id);
         }
         return holder==null ? null : (Message) holder.getReply();
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java
index 93b12e10c..267e9c40d 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java
@@ -1,624 +1,624 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.InputStream;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.io.StringWriter;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Objects;
 import java.util.Optional;
 
 import javax.jms.DeliveryMode;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.naming.NamingException;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.protocol.jms.client.ClientPool;
 import org.apache.jmeter.protocol.jms.client.InitialContextFactory;
 import org.apache.jmeter.protocol.jms.client.Publisher;
 import org.apache.jmeter.protocol.jms.control.gui.JMSPublisherGui;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.io.TextFile;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.XStream;
 
 /**
  * This class implements the JMS Publisher sampler.
  */
 public class PublisherSampler extends BaseJMSSampler implements TestStateListener {
 
     private static final long serialVersionUID = 233L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(PublisherSampler.class);
 
     //++ These are JMX file names and must not be changed
     private static final String INPUT_FILE = "jms.input_file"; //$NON-NLS-1$
 
     private static final String RANDOM_PATH = "jms.random_path"; //$NON-NLS-1$
 
     private static final String TEXT_MSG = "jms.text_message"; //$NON-NLS-1$
 
     private static final String CONFIG_CHOICE = "jms.config_choice"; //$NON-NLS-1$
 
     private static final String MESSAGE_CHOICE = "jms.config_msg_type"; //$NON-NLS-1$
     
     private static final String NON_PERSISTENT_DELIVERY = "jms.non_persistent"; //$NON-NLS-1$
     
     private static final String JMS_PROPERTIES = "jms.jmsProperties"; // $NON-NLS-1$
 
     private static final String JMS_PRIORITY = "jms.priority"; // $NON-NLS-1$
 
     private static final String JMS_EXPIRATION = "jms.expiration"; // $NON-NLS-1$
 
     //--
 
     // Does not need to be synch. because it is only accessed from the sampler thread
     // The ClientPool does access it in a different thread, but ClientPool is fully synch.
     private transient Publisher publisher = null;
 
     private static final FileServer FSERVER = FileServer.getFileServer();
 
     // Cache for file. Only used by sample() in a single thread
     private String file_contents = null;
     // Cache for object-message, only used when parsing from a file because in text-area
     // property replacement might have been used
     private Serializable object_msg_file_contents = null;
     // Cache for bytes-message, only used when parsing from a file 
     private byte[] bytes_msg_file_contents = null;
 
     // Cached file name
     private String cachedFileName;
 
     public PublisherSampler() {
     }
 
     /**
      * the implementation calls testStarted() without any parameters.
      */
     @Override
     public void testStarted(String test) {
         testStarted();
     }
 
     /**
      * the implementation calls testEnded() without any parameters.
      */
     @Override
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * endTest cleans up the client
      */
     @Override
     public void testEnded() {
         log.debug("PublisherSampler.testEnded called");
         ClientPool.clearClient();
         InitialContextFactory.close();
     }
 
     @Override
     public void testStarted() {
     }
 
     /**
      * initialize the Publisher client.
      * @throws JMSException 
      * @throws NamingException 
      *
      */
     private void initClient() throws JMSException, NamingException {
         configureIsReconnectErrorCode();
         publisher = new Publisher(getUseJNDIPropertiesAsBoolean(), getJNDIInitialContextFactory(), 
                 getProviderUrl(), getConnectionFactory(), getDestination(), isUseAuth(), getUsername(),
                 getPassword(), isDestinationStatic());
         ClientPool.addClient(publisher);
         log.debug("PublisherSampler.initClient called");
     }
 
     /**
      * The implementation will publish n messages within a for loop. Once n
      * messages are published, it sets the attributes of SampleResult.
      *
      * @return the populated sample result
      */
     @Override
     public SampleResult sample() {
         SampleResult result = new SampleResult();
         result.setSampleLabel(getName());
         result.setSuccessful(false); // Assume it will fail
         result.setResponseCode("000"); // ditto $NON-NLS-1$
         if (publisher == null) {
             try {
                 initClient();
             } catch (JMSException | NamingException e) {
                 handleError(result, e, false);
                 return result;
             }
         }
         StringBuilder buffer = new StringBuilder();
         StringBuilder propBuffer = new StringBuilder();
         int loop = getIterationCount();
         result.sampleStart();
         String type = getMessageChoice();
         
         try {
             Map<String, Object> msgProperties = getJMSProperties().getJmsPropertysAsMap();
             int deliveryMode = getUseNonPersistentDelivery() ? DeliveryMode.NON_PERSISTENT : DeliveryMode.PERSISTENT; 
             int priority = Integer.parseInt(getPriority());
             long expiration = Long.parseLong(getExpiration());
             
             for (int idx = 0; idx < loop; idx++) {
                 if (JMSPublisherGui.TEXT_MSG_RSC.equals(type)){
                     String tmsg = getMessageContent();
                     Message msg = publisher.publish(tmsg, getDestination(), msgProperties, deliveryMode, priority, expiration);
                     buffer.append(tmsg);
                     Utils.messageProperties(propBuffer, msg);
                 } else if (JMSPublisherGui.MAP_MSG_RSC.equals(type)){
                     Map<String, Object> m = getMapContent();
                     Message msg = publisher.publish(m, getDestination(), msgProperties, deliveryMode, priority, expiration);
                     Utils.messageProperties(propBuffer, msg);
                 } else if (JMSPublisherGui.OBJECT_MSG_RSC.equals(type)){
                     Serializable omsg = getObjectContent();
                     Message msg = publisher.publish(omsg, getDestination(), msgProperties, deliveryMode, priority, expiration);
                     Utils.messageProperties(propBuffer, msg);
                 } else if (JMSPublisherGui.BYTES_MSG_RSC.equals(type)){
                     byte[] bmsg = getBytesContent();
                     Message msg = publisher.publish(bmsg, getDestination(), msgProperties, deliveryMode, priority, expiration);
                     Utils.messageProperties(propBuffer, msg);
                 } else {
                     throw new JMSException(type+ " is not recognised");                    
                 }
             }
             result.setResponseCodeOK();
             result.setResponseMessage(loop + " messages published");
             result.setSuccessful(true);
             result.setSamplerData(buffer.toString());
             result.setSampleCount(loop);
             result.setRequestHeaders(propBuffer.toString());
         } catch (JMSException e) {
             handleError(result, e, true);
         } catch (Exception e) {
             handleError(result, e, false);
         } finally {
             result.sampleEnd();            
         }
         return result;
     }
 
     /**
      * Fills in result and decide wether to reconnect or not depending on checkForReconnect 
      * and underlying {@link JMSException#getErrorCode()}
      * @param result {@link SampleResult}
      * @param e {@link Exception}
      * @param checkForReconnect if true and exception is a {@link JMSException}
      */
     private void handleError(SampleResult result, Exception e, boolean checkForReconnect) {
         result.setSuccessful(false);
         result.setResponseMessage(e.toString());
 
         if (e instanceof JMSException) {
             JMSException jms = (JMSException)e;
 
             String errorCode = Optional.ofNullable(jms.getErrorCode()).orElse("");
             if (checkForReconnect && publisher != null 
                     && getIsReconnectErrorCode().test(errorCode)) {
                 ClientPool.removeClient(publisher);
                 IOUtils.closeQuietly(publisher);
                 publisher = null;
             }
 
             result.setResponseCode(errorCode);
         }
 
         StringWriter writer = new StringWriter();
         e.printStackTrace(new PrintWriter(writer)); // NOSONAR We're getting it to put it in ResponseData 
         result.setResponseData(writer.toString(), "UTF-8");
     }
 
     private Map<String, Object> getMapContent() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
         Map<String,Object> m = new HashMap<>();
         String text = getMessageContent();
         String[] lines = text.split("\n");
         for (String line : lines){
             String[] parts = line.split(",",3);
             if (parts.length != 3) {
                 throw new IllegalArgumentException("line must have 3 parts: "+line);
             }
             String name = parts[0];
             String type = parts[1];
             if (!type.contains(".")){// Allow shorthand names
                 type = "java.lang."+type;
             }
             String value = parts[2];
             Object obj;
             if (type.equals("java.lang.String")){
                 obj = value;
             } else {
                 Class <?> clazz = Class.forName(type);
                 Method method = clazz.getMethod("valueOf", String.class);
                 obj = method.invoke(clazz, value);                
             }
             m.put(name, obj);
         }
         return m;
     }
 
     /**
      * Method will check the setting and get the contents for the message.
      *
      * @return the contents for the message
      */
     private String getMessageContent() {
         if (getConfigChoice().equals(JMSPublisherGui.USE_FILE_RSC)) {
             // in the case the test uses a file, we set it locally and
             // prevent loading the file repeatedly
             // if the file name changes we reload it
             if (file_contents == null || !Objects.equals(cachedFileName, getInputFile())) {
                 cachedFileName = getInputFile();
                 file_contents = getFileContent(getInputFile());
             }
             return file_contents;
         } else if (getConfigChoice().equals(JMSPublisherGui.USE_RANDOM_RSC)) {
             // Maybe we should consider creating a global cache for the
             // random files to make JMeter more efficient.
             String fname = FSERVER.getRandomFile(getRandomPath(), new String[] { ".txt", ".obj" })
                     .getAbsolutePath();
             return getFileContent(fname);
         } else {
             return getTextMessage();
         }
     }
 
     /**
      * The implementation uses TextFile to load the contents of the file and
      * returns a string.
      *
      * @param path path to the file to read in
      * @return the contents of the file
      */
     public String getFileContent(String path) {
         TextFile tf = new TextFile(path);
         return tf.getText();
     }
 
     /**
      * This method will load the contents for the JMS Object Message.
      * The contents are either loaded from file (might be cached), random file
      * or from the GUI text-area.
      * 
      * @return Serialized object as loaded from the specified input file
      */
     private Serializable getObjectContent() {
         if (getConfigChoice().equals(JMSPublisherGui.USE_FILE_RSC)) {
             // in the case the test uses a file, we set it locally and
             // prevent loading the file repeatedly
             // if the file name changes we reload it
             if (object_msg_file_contents == null || !Objects.equals(cachedFileName, getInputFile())) {
                 cachedFileName = getInputFile();
                 object_msg_file_contents = getFileObjectContent(getInputFile());
             }
 
             return object_msg_file_contents;
         } else if (getConfigChoice().equals(JMSPublisherGui.USE_RANDOM_RSC)) {
             // Maybe we should consider creating a global cache for the
             // random files to make JMeter more efficient.
             final String fname = FSERVER.getRandomFile(getRandomPath(), new String[] {".txt", ".obj"})
                 .getAbsolutePath();
 
             return getFileObjectContent(fname);
         } else {
             final String xmlMessage = getTextMessage();
             return transformXmlToObjectMessage(xmlMessage);
         }
     }
     
     /**
      * This method will load the contents for the JMS BytesMessage.
      * The contents are either loaded from file (might be cached), random file
      * 
      * @return byte[] as loaded from the specified input file
      * @since 2.9
      */
     private  byte[] getBytesContent() {
         if (getConfigChoice().equals(JMSPublisherGui.USE_FILE_RSC)) {
             // in the case the test uses a file, we set it locally and
             // prevent loading the file repeatedly
             // if the file name changes we reload it
             if (bytes_msg_file_contents == null || !Objects.equals(cachedFileName, getInputFile())) {
                 cachedFileName = getInputFile();
                 bytes_msg_file_contents = getFileBytesContent(getInputFile());
             }
 
             return bytes_msg_file_contents;
         } else if (getConfigChoice().equals(JMSPublisherGui.USE_RANDOM_RSC)) {
             final String fname = FSERVER.getRandomFile(getRandomPath(), new String[] {".dat"})
                 .getAbsolutePath();
 
             return getFileBytesContent(fname);
         } else {
             throw new IllegalArgumentException("Type of input not handled:" + getConfigChoice());
         }
     }
     
     /**
      * Try to load an object from a provided file, so that it can be used as body
      * for a JMS message.
      * An {@link IllegalStateException} will be thrown if loading the object fails.
      * 
      * @param path Path to the file that will be serialized
      * @return byte[]  instance
      * @since 2.9
      */
     private static byte[] getFileBytesContent(final String path) {
         InputStream inputStream = null;
         try {
             File file = new File(path);
             inputStream = new BufferedInputStream(new FileInputStream(file));
             return IOUtils.toByteArray(inputStream, (int)file.length());
         } catch (Exception e) {
             log.error(e.getLocalizedMessage(), e);
             throw new IllegalStateException("Unable to load file:'"+path+"'", e);
         } finally {
             JOrphanUtils.closeQuietly(inputStream);
         }
     }
     
     /**
      * Try to load an object from a provided file, so that it can be used as body
      * for a JMS message.
      * An {@link IllegalStateException} will be thrown if loading the object fails.
      * 
      * @param path Path to the file that will be serialized
      * @return Serialized object instance
      */
     private static Serializable getFileObjectContent(final String path) {
       Serializable readObject = null;
       InputStream inputStream = null;
       try {
           inputStream = new BufferedInputStream(new FileInputStream(path));
           XStream xstream = new XStream();
         readObject = (Serializable) xstream.fromXML(inputStream, readObject);
       } catch (Exception e) {
           log.error(e.getLocalizedMessage(), e);
           throw new IllegalStateException("Unable to load object instance from file:'"+path+"'", e);
       } finally {
           JOrphanUtils.closeQuietly(inputStream);
       }
       return readObject;
     }
     
     /**
      * Try to load an object via XStream from XML text, so that it can be used as body
      * for a JMS message.
      * An {@link IllegalStateException} will be thrown if transforming the XML to an object fails.
      *
      * @param xmlMessage String containing XML text as input for the transformation
      * @return Serialized object instance
      */
     private static Serializable transformXmlToObjectMessage(final String xmlMessage) {
       Serializable readObject = null;
       try {
           XStream xstream = new XStream();
           readObject = (Serializable) xstream.fromXML(xmlMessage, readObject);
       } catch (Exception e) {
           log.error(e.getLocalizedMessage(), e);
           throw new IllegalStateException("Unable to load object instance from text", e);
       }
       return readObject;
     }
     
     // ------------- get/set properties ----------------------//
     /**
      * set the source of the message
      *
      * @param choice
      *            source of the messages. One of
      *            {@link JMSPublisherGui#USE_FILE_RSC},
      *            {@link JMSPublisherGui#USE_RANDOM_RSC} or
      *            JMSPublisherGui#USE_TEXT_RSC
      */
     public void setConfigChoice(String choice) {
         setProperty(CONFIG_CHOICE, choice);
     }
 
     // These static variables are only used to convert existing files
     private static final String USE_FILE_LOCALNAME = JMeterUtils.getResString(JMSPublisherGui.USE_FILE_RSC);
     private static final String USE_RANDOM_LOCALNAME = JMeterUtils.getResString(JMSPublisherGui.USE_RANDOM_RSC);
 
     /**
      * return the source of the message
      * Converts from old JMX files which used the local language string
      *
      * @return source of the messages
      */
     public String getConfigChoice() {
         // Allow for the old JMX file which used the local language string
         String config = getPropertyAsString(CONFIG_CHOICE);
         if (config.equals(USE_FILE_LOCALNAME) 
          || config.equals(JMSPublisherGui.USE_FILE_RSC)){
             return JMSPublisherGui.USE_FILE_RSC;
         }
         if (config.equals(USE_RANDOM_LOCALNAME)
          || config.equals(JMSPublisherGui.USE_RANDOM_RSC)){
             return JMSPublisherGui.USE_RANDOM_RSC;
         }
         return config; // will be the 3rd option, which is not checked specifically
     }
 
     /**
      * set the type of the message
      *
      * @param choice type of the message (Text, Object, Map)
      */
     public void setMessageChoice(String choice) {
         setProperty(MESSAGE_CHOICE, choice);
     }
 
     /**
      * @return the type of the message (Text, Object, Map)
      *
      */
     public String getMessageChoice() {
         return getPropertyAsString(MESSAGE_CHOICE);
     }
 
     /**
      * set the input file for the publisher
      *
      * @param file input file for the publisher
      */
     public void setInputFile(String file) {
         setProperty(INPUT_FILE, file);
     }
 
     /**
      * @return the path of the input file
      *
      */
     public String getInputFile() {
         return getPropertyAsString(INPUT_FILE);
     }
 
     /**
      * set the random path for the messages
      *
      * @param path random path for the messages
      */
     public void setRandomPath(String path) {
         setProperty(RANDOM_PATH, path);
     }
 
     /**
      * @return the random path for messages
      *
      */
     public String getRandomPath() {
         return getPropertyAsString(RANDOM_PATH);
     }
 
     /**
      * set the text for the message
      *
      * @param message text for the message
      */
     public void setTextMessage(String message) {
         setProperty(TEXT_MSG, message);
     }
 
     /**
      * @return the text for the message
      *
      */
     public String getTextMessage() {
         return getPropertyAsString(TEXT_MSG);
     }
 
     public String getExpiration() {
         String expiration = getPropertyAsString(JMS_EXPIRATION);
         if (expiration.length() == 0) {
             return Utils.DEFAULT_NO_EXPIRY;
         } else {
             return expiration;
         }
     }
 
     public String getPriority() {
         String priority = getPropertyAsString(JMS_PRIORITY);
         if (priority.length() == 0) {
             return Utils.DEFAULT_PRIORITY_4;
         } else {
             return priority;
         }
     }
     
     public void setPriority(String s) {
         // Bug 59173
         if (Utils.DEFAULT_PRIORITY_4.equals(s)) {
             s = ""; // $NON-NLS-1$ make sure the default is not saved explicitly
         }
         setProperty(JMS_PRIORITY, s); // always need to save the field
     }
     
     public void setExpiration(String s) {
         // Bug 59173
         if (Utils.DEFAULT_NO_EXPIRY.equals(s)) {
             s = ""; // $NON-NLS-1$ make sure the default is not saved explicitly
         }
         setProperty(JMS_EXPIRATION, s); // always need to save the field
     }
     
     /**
      * @param value boolean use NON_PERSISTENT
      */
     public void setUseNonPersistentDelivery(boolean value) {
         setProperty(NON_PERSISTENT_DELIVERY, value, false);
     }
     
     /**
      * @return true if NON_PERSISTENT delivery must be used
      */
     public boolean getUseNonPersistentDelivery() {
         return getPropertyAsBoolean(NON_PERSISTENT_DELIVERY, false);
     }
 
     /** 
      * @return {@link JMSProperties} JMS Properties
      */
     public JMSProperties getJMSProperties() {
         Object o = getProperty(JMS_PROPERTIES).getObjectValue();
         JMSProperties jmsProperties = null;
         // Backward compatibility with versions <= 2.10
         if(o instanceof Arguments) {
             jmsProperties = Utils.convertArgumentsToJmsProperties((Arguments)o);
         } else {
             jmsProperties = (JMSProperties) o;
         }
         if(jmsProperties == null) {
             jmsProperties = new JMSProperties();
             setJMSProperties(jmsProperties);
         }
         return jmsProperties;
     }
     
     /**
      * @param jmsProperties JMS Properties
      */
     public void setJMSProperties(JMSProperties jmsProperties) {
         setProperty(new TestElementProperty(JMS_PROPERTIES, jmsProperties));
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java
index 08aaea2b8..e61a60a14 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/Receiver.java
@@ -1,154 +1,152 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import javax.jms.Connection;
 import javax.jms.ConnectionFactory;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 import javax.jms.MessageConsumer;
 import javax.jms.Session;
 
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.jms.Utils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Receiver of pseudo-synchronous reply messages.
  *
  */
 public final class Receiver implements Runnable {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(Receiver.class);
 
     private volatile boolean active;
 
     private final Session session;
 
     private final MessageConsumer consumer;
 
     private final Connection conn;
 
     private final boolean useResMsgIdAsCorrelId;
 
 
     /**
      * Constructor
      * @param factory
      * @param receiveQueue Receive Queue
      * @param principal Username
      * @param credentials Password
      * @param useResMsgIdAsCorrelId
      * @param jmsSelector JMS Selector
      * @throws JMSException
      */
     private Receiver(ConnectionFactory factory, Destination receiveQueue, String principal, String credentials, boolean useResMsgIdAsCorrelId, String jmsSelector) throws JMSException {
         if (null != principal && null != credentials) {
-            log.info("creating receiver WITH authorisation credentials. UseResMsgId="+useResMsgIdAsCorrelId);
+            log.info("creating receiver WITH authorisation credentials. UseResMsgId={}", useResMsgIdAsCorrelId);
             conn = factory.createConnection(principal, credentials);
         }else{
-            log.info("creating receiver without authorisation credentials. UseResMsgId="+useResMsgIdAsCorrelId);
+            log.info("creating receiver without authorisation credentials. UseResMsgId={}", useResMsgIdAsCorrelId);
             conn = factory.createConnection(); 
         }
         session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
-        if(log.isDebugEnabled()) {
-            log.debug("Receiver - ctor. Creating consumer with JMS Selector:"+jmsSelector);
-        }
+        log.debug("Receiver - ctor. Creating consumer with JMS Selector:{}", jmsSelector);
         if(StringUtils.isEmpty(jmsSelector)) {
             consumer = session.createConsumer(receiveQueue);
         } else {
             consumer = session.createConsumer(receiveQueue, jmsSelector);
         }
         this.useResMsgIdAsCorrelId = useResMsgIdAsCorrelId;
         log.debug("Receiver - ctor. Starting connection now");
         conn.start();
         log.info("Receiver - ctor. Connection to messaging system established");
     }
 
     /**
      * Create a receiver to process responses.
      *
      * @param factory
      *            connection factory to use
      * @param receiveQueue
      *            name of the receiving queue
      * @param principal
      *            user name to use for connecting to the queue
      * @param credentials
      *            credentials to use for connecting to the queue
      * @param useResMsgIdAsCorrelId
      *            <code>true</code> if should use JMSMessageId,
      *            <code>false</code> if should use JMSCorrelationId
      * @param jmsSelector
      *            JMS selector
      * @return the Receiver which will process the responses
      * @throws JMSException
      *             when creating the receiver fails
      */
     public static Receiver createReceiver(ConnectionFactory factory, Destination receiveQueue,
             String principal, String credentials, boolean useResMsgIdAsCorrelId, String jmsSelector)
             throws JMSException {
         Receiver receiver = new Receiver(factory, receiveQueue, principal, credentials, useResMsgIdAsCorrelId, jmsSelector);
         Thread thread = new Thread(receiver, Thread.currentThread().getName()+"-JMS-Receiver");
         thread.start();
         return receiver;
     }
 
     @Override
     public void run() {
         active = true;
         Message reply;
 
         while (active) {
             reply = null;
             try {
                 reply = consumer.receive(5000);
                 if (reply != null) {
                     String messageKey;
                     final MessageAdmin admin = MessageAdmin.getAdmin();
                     if (useResMsgIdAsCorrelId){
                         messageKey = reply.getJMSMessageID();
                         synchronized (admin) {// synchronize with FixedQueueExecutor
                             admin.putReply(messageKey, reply);                            
                         }
                     } else {
                         messageKey = reply.getJMSCorrelationID();
                         if (messageKey == null) {// JMSMessageID cannot be null
                             log.warn("Received message with correlation id null. Discarding message ...");
                         } else {
                             admin.putReply(messageKey, reply);
                         }
                     }
                 }
 
             } catch (JMSException e1) {
                 log.error("Error handling receive",e1);
             }
         }
         Utils.close(consumer, log);
         Utils.close(session, log);
         Utils.close(conn, log);
     }
 
     public void deactivate() {
         active = false;
     }
 
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
index 1a1faa822..b56732b5e 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
@@ -1,564 +1,564 @@
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
 
 package org.apache.jmeter.protocol.jms.sampler;
 
 import java.util.Enumeration;
 import java.util.Optional;
 
 import javax.jms.BytesMessage;
 import javax.jms.JMSException;
 import javax.jms.MapMessage;
 import javax.jms.Message;
 import javax.jms.ObjectMessage;
 import javax.jms.TextMessage;
 import javax.naming.NamingException;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.protocol.jms.Utils;
 import org.apache.jmeter.protocol.jms.client.InitialContextFactory;
 import org.apache.jmeter.protocol.jms.client.ReceiveSubscriber;
 import org.apache.jmeter.protocol.jms.control.gui.JMSSubscriberGui;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class implements the JMS Subscriber sampler.
  * It supports both receive and onMessage strategies via the ReceiveSubscriber class.
  * 
  */
 // TODO: do we need to implement any kind of connection pooling?
 // If so, which connections should be shared?
 // Should threads share connections to the same destination?
 // What about cross-thread sharing?
 
 // Note: originally the code did use the ClientPool to "share" subscribers, however since the
 // key was "this" and each sampler is unique - nothing was actually shared.
 
 public class SubscriberSampler extends BaseJMSSampler implements Interruptible, ThreadListener, TestStateListener {
 
     private static final long serialVersionUID = 240L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SubscriberSampler.class);
 
     // Default wait (ms) for a message if timeouts are not enabled
     // This is the maximum time the sampler can be blocked.
     private static final long DEFAULT_WAIT = 500L;
 
     // No need to synch/ - only used by sampler
     // Note: not currently added to the ClientPool
     private transient ReceiveSubscriber SUBSCRIBER = null;
 
     private transient volatile boolean interrupted = false;
 
     private transient long timeout;
     
     private transient boolean useReceive;
 
     // This will be null if initialization succeeds.
     private transient Exception exceptionDuringInit;
 
     // If true, start/stop subscriber for each sample
     private transient boolean stopBetweenSamples;
 
     // Don't change the string, as it is used in JMX files
     private static final String CLIENT_CHOICE = "jms.client_choice"; // $NON-NLS-1$
     private static final String TIMEOUT = "jms.timeout"; // $NON-NLS-1$
     private static final String TIMEOUT_DEFAULT = ""; // $NON-NLS-1$
     private static final String DURABLE_SUBSCRIPTION_ID = "jms.durableSubscriptionId"; // $NON-NLS-1$
     private static final String CLIENT_ID = "jms.clientId"; // $NON-NLS-1$
     private static final String JMS_SELECTOR = "jms.selector"; // $NON-NLS-1$
     private static final String DURABLE_SUBSCRIPTION_ID_DEFAULT = "";
     private static final String CLIENT_ID_DEFAULT = ""; // $NON-NLS-1$
     private static final String JMS_SELECTOR_DEFAULT = ""; // $NON-NLS-1$
     private static final String STOP_BETWEEN = "jms.stop_between_samples"; // $NON-NLS-1$
     private static final String SEPARATOR = "jms.separator"; // $NON-NLS-1$
     private static final String SEPARATOR_DEFAULT = ""; // $NON-NLS-1$
     private static final String ERROR_PAUSE_BETWEEN = "jms_error_pause_between"; // $NON-NLS-1$
     private static final String ERROR_PAUSE_BETWEEN_DEFAULT = ""; // $NON-NLS-1$
 
     
     private transient boolean START_ON_SAMPLE = false;
 
     private transient String separator;
 
     public SubscriberSampler() {
         super();
     }
 
     /**
      * Create the OnMessageSubscriber client and set the sampler as the message
      * listener.
      * @throws JMSException 
      * @throws NamingException 
      *
      */
     private void initListenerClient() throws JMSException, NamingException {
         SUBSCRIBER = new ReceiveSubscriber(0, getUseJNDIPropertiesAsBoolean(), getJNDIInitialContextFactory(),
                     getProviderUrl(), getConnectionFactory(), getDestination(), getDurableSubscriptionId(),
                     getClientId(), getJmsSelector(), isUseAuth(), getUsername(), getPassword());
         log.debug("SubscriberSampler.initListenerClient called");
     }
 
     /**
      * Create the ReceiveSubscriber client for the sampler.
      * @throws NamingException 
      * @throws JMSException 
      */
     private void initReceiveClient() throws NamingException, JMSException {
         SUBSCRIBER = new ReceiveSubscriber(getUseJNDIPropertiesAsBoolean(),
                 getJNDIInitialContextFactory(), getProviderUrl(), getConnectionFactory(), getDestination(),
                 getDurableSubscriptionId(), getClientId(), getJmsSelector(), isUseAuth(), getUsername(), getPassword());
         log.debug("SubscriberSampler.initReceiveClient called");
     }
 
     /**
      * sample method will check which client it should use and call the
      * appropriate client specific sample method.
      *
      * @return the appropriate sample result
      */
     // TODO - should we call start() and stop()?
     @Override
     public SampleResult sample() {
         // run threadStarted only if Destination setup on each sample
         if (!isDestinationStatic()) {
             threadStarted(true);
         }
         SampleResult result = new SampleResult();
         result.setDataType(SampleResult.TEXT);
         result.setSampleLabel(getName());
         result.sampleStart();
         if (exceptionDuringInit != null) {
             result.sampleEnd();
             result.setSuccessful(false);
             result.setResponseCode("000");
             result.setResponseMessage(exceptionDuringInit.toString());
             handleErrorAndAddTemporize(true);
             return result; 
         }
         if (stopBetweenSamples){ // If so, we need to start collection here
             try {
                 SUBSCRIBER.start();
             } catch (JMSException e) {
                 log.warn("Problem starting subscriber", e);
             }
         }
         StringBuilder buffer = new StringBuilder();
         StringBuilder propBuffer = new StringBuilder();
         
         int loop = getIterationCount();
         int read = 0;
         
         long until = 0L;
         long now = System.currentTimeMillis();
         if (timeout > 0) {
             until = timeout + now; 
         }
         while (!interrupted
                 && (until == 0 || now < until)
                 && read < loop) {
             Message msg;
             try {
                 msg = SUBSCRIBER.getMessage(calculateWait(until, now));
                 if (msg != null){
                     read++;
                     extractContent(buffer, propBuffer, msg, read == loop);
                 }
             } catch (JMSException e) {
                 String errorCode = Optional.ofNullable(e.getErrorCode()).orElse("");
-                log.warn(String.format("Error [%s] %s", errorCode, e.toString()), e);
+                log.warn("Error [{}] {}", errorCode, e.toString(), e);
 
                 handleErrorAndAddTemporize(getIsReconnectErrorCode().test(errorCode));
             }
             now = System.currentTimeMillis();
         }
         result.sampleEnd();
         if (getReadResponseAsBoolean()) {
             result.setResponseData(buffer.toString().getBytes()); // TODO - charset?
         } else {
             result.setBytes((long)buffer.toString().length());
         }
         result.setResponseHeaders(propBuffer.toString());
         if (read == 0) {
             result.setResponseCode("404"); // Not found
             result.setSuccessful(false);
         } else if (read < loop) { // Not enough messages found
             result.setResponseCode("500"); // Server error
             result.setSuccessful(false);
         } else { 
             result.setResponseCodeOK();
             result.setSuccessful(true);
         }
         result.setResponseMessage(read + " message(s) received successfully of " + loop + " expected");
         result.setSamplerData(loop + " messages expected");
         result.setSampleCount(read);
         
         if (stopBetweenSamples){
             try {
                 SUBSCRIBER.stop();
             } catch (JMSException e) {
                 log.warn("Problem stopping subscriber", e);
             }
         }
         // run threadFinished only if Destination setup on each sample (stop Listen queue)
         if (!isDestinationStatic()) {
             threadFinished(true);
         }
         return result;
     }
 
     /**
      * Try to reconnect if configured to or temporize if not or an exception occured
      * @param reconnect
      */
     private void handleErrorAndAddTemporize(boolean reconnect) {
         if (reconnect) {
             cleanup();
             initClient();
         }
 
         if (!reconnect || exceptionDuringInit != null) {
             try {
                 long pause = getPauseBetweenErrorsAsLong();
                 if(pause > 0) {
                     Thread.sleep(pause);
                 }
             } catch (InterruptedException ie) {
-                log.warn(String.format("Interrupted %s", ie.toString()), ie);
+                log.warn("Interrupted {}", ie.toString(), ie);
                 Thread.currentThread().interrupt();
                 interrupted = true;
             }
         }
     }
 
     /**
      * 
      */
     private void cleanup() {
         IOUtils.closeQuietly(SUBSCRIBER);
     }
 
     /**
      * Calculate the wait time, will never be more than DEFAULT_WAIT.
      * 
      * @param until target end time or 0 if timeouts not active
      * @param now current time
      * @return wait time
      */
     private long calculateWait(long until, long now) {
         if (until == 0) {
             return DEFAULT_WAIT; // Timeouts not active
         }
         long wait = until - now; // How much left
         return wait > DEFAULT_WAIT ? DEFAULT_WAIT : wait;
     }
 
     private void extractContent(StringBuilder buffer, StringBuilder propBuffer,
             Message msg, boolean isLast) {
         if (msg != null) {
             try {
                 if (msg instanceof TextMessage){
                     buffer.append(((TextMessage) msg).getText());
                 } else if (msg instanceof ObjectMessage){
                     ObjectMessage objectMessage = (ObjectMessage) msg;
                     if(objectMessage.getObject() != null) {
                         buffer.append(objectMessage.getObject().getClass());
                     } else {
                         buffer.append("object is null");
                     }
                 } else if (msg instanceof BytesMessage){
                     BytesMessage bytesMessage = (BytesMessage) msg;
                     buffer.append(bytesMessage.getBodyLength() + " bytes received in BytesMessage");
                 } else if (msg instanceof MapMessage){
                     MapMessage mapm = (MapMessage) msg;
                     @SuppressWarnings("unchecked") // MapNames are Strings
                     Enumeration<String> enumb = mapm.getMapNames();
                     while(enumb.hasMoreElements()){
                         String name = enumb.nextElement();
                         Object obj = mapm.getObject(name);
                         buffer.append(name);
                         buffer.append(",");
                         buffer.append(obj.getClass().getCanonicalName());
                         buffer.append(",");
                         buffer.append(obj);
                         buffer.append("\n");
                     }
                 }
                 Utils.messageProperties(propBuffer, msg);
                 if(!isLast && !StringUtils.isEmpty(separator)) {
                     propBuffer.append(separator);
                     buffer.append(separator);
                 }
             } catch (JMSException e) {
                 log.error(e.getMessage());
             }
         }
     }
 
     /**
      * Initialise the thread-local variables.
      * <br>
      * {@inheritDoc}
      */
     @Override
     public void threadStarted() {
         configureIsReconnectErrorCode();
 
         // Disabled thread start if listen on sample choice
         if (isDestinationStatic() || START_ON_SAMPLE) {
             timeout = getTimeoutAsLong();
             interrupted = false;
             exceptionDuringInit = null;
             useReceive = getClientChoice().equals(JMSSubscriberGui.RECEIVE_RSC);
             stopBetweenSamples = isStopBetweenSamples();
             setupSeparator();
             initClient();
         }
     }
 
     private void initClient() {
         exceptionDuringInit = null;
         try {
             if(useReceive) {
                 initReceiveClient();
             } else {
                 initListenerClient();
             }
             if (!stopBetweenSamples) { // Don't start yet if stop between
                                        // samples
                 SUBSCRIBER.start();
             }
         } catch (NamingException | JMSException e) {
             exceptionDuringInit = e;
         }
         
         if (exceptionDuringInit != null) {
             log.error("Could not initialise client", exceptionDuringInit);
         }
     }
 
     public void threadStarted(boolean wts) {
         if (wts) {
             START_ON_SAMPLE = true; // listen on sample 
         }
         threadStarted();
     }
 
     /**
      * Close subscriber.
      * <br>
      * {@inheritDoc}
      */
     @Override
     public void threadFinished() {
         if (SUBSCRIBER != null){ // Can be null if init fails
             cleanup();
         }
     }
     
     public void threadFinished(boolean wts) {
         if (wts) {
             START_ON_SAMPLE = false; // listen on sample
         }
         threadFinished();
     }
 
     /**
      * Handle an interrupt of the test.
      */
     @Override
     public boolean interrupt() {
         boolean oldvalue = interrupted;
         interrupted = true;   // so we break the loops in SampleWithListener and SampleWithReceive
         return !oldvalue;
     }
 
     // ----------- get/set methods ------------------- //
     /**
      * Set the client choice. There are two options: ReceiveSusbscriber and
      * OnMessageSubscriber.
      *
      * @param choice
      *            the client to use. One of {@link JMSSubscriberGui#RECEIVE_RSC
      *            RECEIVE_RSC} or {@link JMSSubscriberGui#ON_MESSAGE_RSC
      *            ON_MESSAGE_RSC}
      */
     public void setClientChoice(String choice) {
         setProperty(CLIENT_CHOICE, choice);
     }
 
     /**
      * Return the client choice.
      *
      * @return the client choice, either {@link JMSSubscriberGui#RECEIVE_RSC
      *         RECEIVE_RSC} or {@link JMSSubscriberGui#ON_MESSAGE_RSC
      *         ON_MESSAGE_RSC}
      */
     public String getClientChoice() {
         String choice = getPropertyAsString(CLIENT_CHOICE);
         // Convert the old test plan entry (which is the language dependent string) to the resource name
         if (choice.equals(RECEIVE_STR)){
             choice = JMSSubscriberGui.RECEIVE_RSC;
         } else if (!choice.equals(JMSSubscriberGui.RECEIVE_RSC)){
             choice = JMSSubscriberGui.ON_MESSAGE_RSC;
         }
         return choice;
     }
 
     public String getTimeout(){
         return getPropertyAsString(TIMEOUT, TIMEOUT_DEFAULT);
     }
 
     public long getTimeoutAsLong(){
         return getPropertyAsLong(TIMEOUT, 0L);
     }
 
     public void setTimeout(String timeout){
         setProperty(TIMEOUT, timeout, TIMEOUT_DEFAULT);        
     }
     
     public String getDurableSubscriptionId(){
         return getPropertyAsString(DURABLE_SUBSCRIPTION_ID);
     }
     
     /**
      * @return JMS Client ID
      */
     public String getClientId() {
         return getPropertyAsString(CLIENT_ID, CLIENT_ID_DEFAULT);
     }
     
     /**
      * @return JMS selector
      */
     public String getJmsSelector() {
         return getPropertyAsString(JMS_SELECTOR, JMS_SELECTOR_DEFAULT);
     }
 
     public void setDurableSubscriptionId(String durableSubscriptionId){
         setProperty(DURABLE_SUBSCRIPTION_ID, durableSubscriptionId, DURABLE_SUBSCRIPTION_ID_DEFAULT);        
     }
 
     /**
      * @param clientId JMS CLient id
      */
     public void setClientID(String clientId) {
         setProperty(CLIENT_ID, clientId, CLIENT_ID_DEFAULT);
     }
    
     /**
      * @param jmsSelector JMS Selector
      */
     public void setJmsSelector(String jmsSelector) {
         setProperty(JMS_SELECTOR, jmsSelector, JMS_SELECTOR_DEFAULT);
     }
 
     /**
      * @return Separator for sampler results
      */
     public String getSeparator() {
         return getPropertyAsString(SEPARATOR, SEPARATOR_DEFAULT);
     }
     
     /**
      * Separator for sampler results
      *
      * @param text
      *            separator to use for sampler results
      */
     public void setSeparator(String text) {
         setProperty(SEPARATOR, text, SEPARATOR_DEFAULT);
     }
     
     // This was the old value that was checked for
     private static final String RECEIVE_STR = JMeterUtils.getResString(JMSSubscriberGui.RECEIVE_RSC); // $NON-NLS-1$
 
     public boolean isStopBetweenSamples() {
         return getPropertyAsBoolean(STOP_BETWEEN, false);
     }
 
     public void setStopBetweenSamples(boolean selected) {
         setProperty(STOP_BETWEEN, selected, false);                
     }
 
     public void setPauseBetweenErrors(String pause) {
         setProperty(ERROR_PAUSE_BETWEEN, pause, ERROR_PAUSE_BETWEEN_DEFAULT);
     }
 
     public String getPauseBetweenErrors() {
         return getPropertyAsString(ERROR_PAUSE_BETWEEN, ERROR_PAUSE_BETWEEN_DEFAULT);
     }
 
     public long getPauseBetweenErrorsAsLong() {
         return getPropertyAsLong(ERROR_PAUSE_BETWEEN, DEFAULT_WAIT);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testEnded() {
         InitialContextFactory.close();
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
         testStarted("");
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void testStarted(String host) {
         // NOOP
     }
 
     /**
      * 
      */
     private void setupSeparator() {
         separator = getSeparator();
         separator = separator.replace("\\t", "\t");
         separator = separator.replace("\\n", "\n");
         separator = separator.replace("\\r", "\r");
     }
 
     private Object readResolve(){
         setupSeparator();
         exceptionDuringInit=null;
         return this;
     }
 }
