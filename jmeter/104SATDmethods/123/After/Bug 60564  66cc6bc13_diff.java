diff --git a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java
index 2bf0c7b7b..878c5c542 100644
--- a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java
+++ b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPExtSampler.java
@@ -1,1068 +1,1068 @@
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
 
 package org.apache.jmeter.protocol.ldap.sampler;
 
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.NamingEnumeration;
 import javax.naming.NamingException;
 import javax.naming.directory.Attribute;
 import javax.naming.directory.Attributes;
 import javax.naming.directory.BasicAttribute;
 import javax.naming.directory.BasicAttributes;
 import javax.naming.directory.DirContext;
 import javax.naming.directory.ModificationItem;
 import javax.naming.directory.SearchResult;
 
 import org.apache.commons.lang3.StringEscapeUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.protocol.ldap.config.gui.LDAPArgument;
 import org.apache.jmeter.protocol.ldap.config.gui.LDAPArguments;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.XMLBuffer;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /*******************************************************************************
  * Ldap Sampler class is main class for the LDAP test. This will control all the
  * test available in the LDAP Test.
  ******************************************************************************/
 public class LDAPExtSampler extends AbstractSampler implements TestStateListener {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LDAPExtSampler.class);
 
     private static final long serialVersionUID = 240L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList(
                     "org.apache.jmeter.protocol.ldap.config.gui.LdapConfigGui",
                     "org.apache.jmeter.protocol.ldap.config.gui.LdapExtConfigGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui"
             ));
 
     /*
      * The following strings are used in the test plan, and the values must not be changed
      * if test plans are to be upwardly compatible.
      */
     public static final String SERVERNAME = "servername"; // $NON-NLS-1$
 
     public static final String PORT = "port"; // $NON-NLS-1$
 
     public static final String SECURE = "secure"; // $NON-NLS-1$
 
     public static final String ROOTDN = "rootdn"; // $NON-NLS-1$
 
     public static final String TEST = "test"; // $NON-NLS-1$
 
     // These are values for the TEST attribute above
     public static final String ADD = "add"; // $NON-NLS-1$
 
     public static final String MODIFY = "modify"; // $NON-NLS-1$
 
     public static final String BIND = "bind"; // $NON-NLS-1$
 
     public static final String UNBIND = "unbind"; // $NON-NLS-1$
 
     public static final String DELETE = "delete"; // $NON-NLS-1$
 
     public static final String SEARCH = "search"; // $NON-NLS-1$
     // end of TEST values
 
     public static final String SEARCHBASE = "search"; // $NON-NLS-1$
 
     public static final String SEARCHFILTER = "searchfilter"; // $NON-NLS-1$
 
     public static final String ARGUMENTS = "arguments"; // $NON-NLS-1$
 
     public static final String LDAPARGUMENTS = "ldaparguments"; // $NON-NLS-1$
 
     public static final String BASE_ENTRY_DN = "base_entry_dn"; // $NON-NLS-1$
 
     public static final String SCOPE = "scope"; // $NON-NLS-1$
 
     public static final String COUNTLIM = "countlimit"; // $NON-NLS-1$
 
     public static final String TIMELIM = "timelimit"; // $NON-NLS-1$
 
     public static final String ATTRIBS = "attributes"; // $NON-NLS-1$
 
     public static final String RETOBJ = "return_object"; // $NON-NLS-1$
 
     public static final String DEREF = "deref_aliases"; // $NON-NLS-1$
 
     public static final String USERDN = "user_dn"; // $NON-NLS-1$
 
     public static final String USERPW = "user_pw"; // $NON-NLS-1$
 
     public static final String SBIND = "sbind"; // $NON-NLS-1$
 
     public static final String COMPARE = "compare"; // $NON-NLS-1$
 
     public static final String CONNTO = "connection_timeout"; // $NON-NLS-1$
 
     public static final String COMPAREDN = "comparedn"; // $NON-NLS-1$
 
     public static final String COMPAREFILT = "comparefilt"; // $NON-NLS-1$
 
     public static final String PARSEFLAG = "parseflag"; // $NON-NLS-1$
 
     public static final String RENAME = "rename"; // $NON-NLS-1$
 
     public static final String MODDDN = "modddn"; // $NON-NLS-1$
 
     public static final String NEWDN = "newdn"; // $NON-NLS-1$
 
     private static final String SEMI_COLON = ";"; // $NON-NLS-1$
 
 
     private static final ConcurrentHashMap<String, DirContext> ldapContexts =
             new ConcurrentHashMap<>();
 
     private static final int MAX_SORTED_RESULTS =
         JMeterUtils.getPropDefault("ldapsampler.max_sorted_results", 1000); // $NON-NLS-1$
 
     /***************************************************************************
      * !ToDo (Constructor description)
      **************************************************************************/
     public LDAPExtSampler() {
     }
 
     public void setConnTimeOut(String connto) {
         setProperty(new StringProperty(CONNTO, connto));
     }
 
     public String getConnTimeOut() {
         return getPropertyAsString(CONNTO);
     }
 
     public void setSecure(String sec) {
         setProperty(new StringProperty(SECURE, sec));
     }
 
     public boolean isSecure() {
         return getPropertyAsBoolean(SECURE);
     }
 
 
     public boolean isParseFlag() {
         return getPropertyAsBoolean(PARSEFLAG);
     }
 
     public void setParseFlag(String parseFlag) {
         setProperty(new StringProperty(PARSEFLAG, parseFlag));
     }
 
     /***************************************************************************
      * Gets the username attribute of the LDAP object
      *
      * @return The username
      **************************************************************************/
     public String getUserDN() {
         return getPropertyAsString(USERDN);
     }
 
     /***************************************************************************
      * Sets the username attribute of the LDAP object
      *
      * @param newUserDN
      *            distinguished name of the user
      **************************************************************************/
     public void setUserDN(String newUserDN) {
         setProperty(new StringProperty(USERDN, newUserDN));
     }
 
     /***************************************************************************
      * Gets the password attribute of the LDAP object
      *
      * @return The password
      **************************************************************************/
     public String getUserPw() {
         return getPropertyAsString(USERPW);
     }
 
     /***************************************************************************
      * Sets the password attribute of the LDAP object
      *
      * @param newUserPw
      *            password of the user
      **************************************************************************/
     public void setUserPw(String newUserPw) {
         setProperty(new StringProperty(USERPW, newUserPw));
     }
 
     /***************************************************************************
      * Sets the Servername attribute of the ServerConfig object
      *
      * @param servername
      *            The new servername value
      **************************************************************************/
     public void setServername(String servername) {
         setProperty(new StringProperty(SERVERNAME, servername));
     }
 
     /***************************************************************************
      * Sets the Port attribute of the ServerConfig object
      *
      * @param port
      *            The new Port value
      **************************************************************************/
     public void setPort(String port) {
         setProperty(new StringProperty(PORT, port));
     }
 
     /***************************************************************************
      * Gets the servername attribute of the LDAPSampler object
      *
      * @return The Servername value
      **************************************************************************/
 
     public String getServername() {
         return getPropertyAsString(SERVERNAME);
     }
 
     /***************************************************************************
      * Gets the Port attribute of the LDAPSampler object
      *
      * @return The Port value
      **************************************************************************/
     public String getPort() {
         return getPropertyAsString(PORT);
     }
 
     /***************************************************************************
      * Sets the Rootdn attribute of the LDAPSampler object
      *
      * @param newRootdn
      *            The new rootdn value
      **************************************************************************/
     public void setRootdn(String newRootdn) {
         this.setProperty(ROOTDN, newRootdn);
     }
 
     /***************************************************************************
      * Gets the Rootdn attribute of the LDAPSampler object
      *
      * @return The Rootdn value
      **************************************************************************/
     public String getRootdn() {
         return getPropertyAsString(ROOTDN);
     }
 
     /***************************************************************************
      * Gets the search scope attribute of the LDAPSampler object
      *
      * @return The scope value
      **************************************************************************/
     public String getScope() {
         return getPropertyAsString(SCOPE);
     }
 
     public int getScopeAsInt() {
         return getPropertyAsInt(SCOPE);
     }
 
     /***************************************************************************
      * Sets the search scope attribute of the LDAPSampler object
      *
      * @param newScope
      *            The new scope value
      **************************************************************************/
     public void setScope(String newScope) {
         this.setProperty(SCOPE, newScope);
     }
 
     /***************************************************************************
      * Gets the size limit attribute of the LDAPSampler object
      *
      * @return The size limit
      **************************************************************************/
     public String getCountlim() {
         return getPropertyAsString(COUNTLIM);
     }
 
     public long getCountlimAsLong() {
         return getPropertyAsLong(COUNTLIM);
     }
 
     /***************************************************************************
      * Sets the size limit attribute of the LDAPSampler object
      *
      * @param newClim
      *            The new size limit value
      **************************************************************************/
     public void setCountlim(String newClim) {
         this.setProperty(COUNTLIM, newClim);
     }
 
     /***************************************************************************
      * Gets the time limit attribute of the LDAPSampler object
      *
      * @return The time limit
      **************************************************************************/
     public String getTimelim() {
         return getPropertyAsString(TIMELIM);
     }
 
     public int getTimelimAsInt() {
         return getPropertyAsInt(TIMELIM);
     }
 
     /***************************************************************************
      * Sets the time limit attribute of the LDAPSampler object
      *
      * @param newTlim
      *            The new time limit value
      **************************************************************************/
     public void setTimelim(String newTlim) {
         this.setProperty(TIMELIM, newTlim);
     }
 
     /***************************************************************************
      * Gets the return objects attribute of the LDAPSampler object
      *
      * @return if the object(s) are to be returned
      **************************************************************************/
     public boolean isRetobj() {
         return getPropertyAsBoolean(RETOBJ);
     }
 
     /***************************************************************************
      * Sets the return objects attribute of the LDAPSampler object
      *
      * @param newRobj
      *            whether the objects should be returned
      **************************************************************************/
     public void setRetobj(String newRobj) {
         this.setProperty(RETOBJ, newRobj);
     }
 
     /***************************************************************************
      * Gets the deref attribute of the LDAPSampler object
      *
      * @return if dereferencing is required
      **************************************************************************/
     public boolean isDeref() {
         return getPropertyAsBoolean(DEREF);
     }
 
     /***************************************************************************
      * Sets the deref attribute of the LDAPSampler object
      *
      * @param newDref
      *            The new deref value
      **************************************************************************/
     public void setDeref(String newDref) {
         this.setProperty(DEREF, newDref);
     }
 
     /***************************************************************************
      * Sets the Test attribute of the LdapConfig object
      *
      * @param newTest
      *            The new test value(Add,Modify,Delete and search)
      **************************************************************************/
     public void setTest(String newTest) {
         this.setProperty(TEST, newTest);
     }
 
     /***************************************************************************
      * Gets the test attribute of the LDAPSampler object
      *
      * @return The test value (Add,Modify,Delete and search)
      **************************************************************************/
     public String getTest() {
         return getPropertyAsString(TEST);
     }
 
     /***************************************************************************
      * Sets the attributes of the LdapConfig object
      *
      * @param newAttrs
      *            The new attributes value
      **************************************************************************/
     public void setAttrs(String newAttrs) {
         this.setProperty(ATTRIBS, newAttrs);
     }
 
     /***************************************************************************
      * Gets the attributes of the LDAPSampler object
      *
      * @return The attributes
      **************************************************************************/
     public String getAttrs() {
         return getPropertyAsString(ATTRIBS);
     }
 
     /***************************************************************************
      * Sets the Base Entry DN attribute of the LDAPSampler object
      *
      * @param newbaseentry
      *            The new Base entry DN value
      **************************************************************************/
     public void setBaseEntryDN(String newbaseentry) {
         setProperty(new StringProperty(BASE_ENTRY_DN, newbaseentry));
     }
 
     /***************************************************************************
      * Gets the BaseEntryDN attribute of the LDAPSampler object
      *
      * @return The Base entry DN value
      **************************************************************************/
     public String getBaseEntryDN() {
         return getPropertyAsString(BASE_ENTRY_DN);
     }
 
     /***************************************************************************
      * Sets the Arguments attribute of the LdapConfig object This will collect
      * values from the table for user defined test case
      *
      * @param value
      *            The arguments
      **************************************************************************/
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     /***************************************************************************
      * Gets the Arguments attribute of the LdapConfig object
      *
      * @return The arguments user defined test case
      **************************************************************************/
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /***************************************************************************
      * Sets the Arguments attribute of the LdapConfig object This will collect
      * values from the table for user defined test case
      *
      * @param value
      *            The arguments
      **************************************************************************/
     public void setLDAPArguments(LDAPArguments value) {
         setProperty(new TestElementProperty(LDAPARGUMENTS, value));
     }
 
     /***************************************************************************
      * Gets the LDAPArguments attribute of the LdapConfig object
      *
      * @return The LDAParguments user defined modify test case
      **************************************************************************/
     public LDAPArguments getLDAPArguments() {
         return (LDAPArguments) getProperty(LDAPARGUMENTS).getObjectValue();
     }
 
     /***************************************************************************
      * Collect all the values from the table (Arguments), using this create the
      * Attributes, this will create the Attributes for the User
      * defined TestCase for Add Test
      *
      * @return The Attributes
      **************************************************************************/
     private Attributes getUserAttributes() {
         Attributes attrs = new BasicAttributes(true);
         Attribute attr;
 
         for (JMeterProperty jMeterProperty : getArguments()) {
             Argument item = (Argument) jMeterProperty.getObjectValue();
             attr = attrs.get(item.getName());
             if (attr == null) {
                 attr = getBasicAttribute(item.getName(), item.getValue());
             } else {
                 attr.add(item.getValue());
             }
             attrs.put(attr);
         }
         return attrs;
     }
 
     /***************************************************************************
      * Collect all the value from the table (Arguments), using this create the
      * basicAttributes This will create the Basic Attributes for the User
      * defined TestCase for Modify test
      *
      * @return The BasicAttributes
      **************************************************************************/
     private ModificationItem[] getUserModAttributes() {
         ModificationItem[] mods = new ModificationItem[getLDAPArguments().getArguments().size()];
         BasicAttribute attr;
         PropertyIterator iter = getLDAPArguments().iterator();
         int count = 0;
         while (iter.hasNext()) {
             LDAPArgument item = (LDAPArgument) iter.next().getObjectValue();
             if ((item.getValue()).length()==0) {
                 attr = new BasicAttribute(item.getName());
             } else {
                 attr = getBasicAttribute(item.getName(), item.getValue());
             }
 
             final String opcode = item.getOpcode();
             if ("add".equals(opcode)) { // $NON-NLS-1$
                 mods[count++] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
             } else if ("delete".equals(opcode) // $NON-NLS-1$
                    ||  "remove".equals(opcode)) { // $NON-NLS-1$
                     mods[count++] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
             } else if("replace".equals(opcode)) { // $NON-NLS-1$
                     mods[count++] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
             } else {
                     log.warn("Invalid opCode: "+opcode);
             }
         }
         return mods;
     }
 
     /***************************************************************************
      * Collect all the value from the table (Arguments), using this create the
      * Attributes This will create the Basic Attributes for the User defined
      * TestCase for search test
      *
      * @return The BasicAttributes
      **************************************************************************/
     private String[] getRequestAttributes(String reqAttr) {
         int index;
         String[] mods;
         int count = 0;
         if (reqAttr.length() == 0) {
             return null;
         }
         if (!reqAttr.endsWith(SEMI_COLON)) {
             reqAttr = reqAttr + SEMI_COLON;
         }
         String attr = reqAttr;
 
         while (attr.length() > 0) {
             index = attr.indexOf(SEMI_COLON);
             count += 1;
             attr = attr.substring(index + 1);
         }
         if (count > 0) {
             mods = new String[count];
             attr = reqAttr;
             count = 0;
             while (attr.length() > 0) {
                 index = attr.indexOf(SEMI_COLON);
                 mods[count] = attr.substring(0, index);
                 count += 1;
                 attr = attr.substring(index + 1);
             }
         } else {
             mods = null;
         }
         return mods;
     }
 
     /***************************************************************************
      * This will create the Basic Attribute for the give name value pair
      *
      * @return The BasicAttribute
      **************************************************************************/
     private BasicAttribute getBasicAttribute(String name, String value) {
         return new BasicAttribute(name, value);
     }
 
     /**
      * Returns a formatted string label describing this sampler Example output:
      *
      * @return a formatted string label describing this sampler
      */
     public String getLabel() {
         return "ldap://" + this.getServername()  //$NON-NLS-1$
                 + ":" + getPort()                 //$NON-NLS-1$
                 + "/" + this.getRootdn();        //$NON-NLS-1$
     }
 
     /***************************************************************************
      * This will do the add test for the User defined TestCase
      *
      **************************************************************************/
     private void addTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             DirContext ctx = LdapExtClient.createTest(dirContext, getUserAttributes(), getBaseEntryDN());
             ctx.close(); // the createTest() method creates an extra context which needs to be closed
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the delete test for the User defined TestCase
      *
      **************************************************************************/
     private void deleteTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             LdapExtClient.deleteTest(dirContext, getPropertyAsString(DELETE));
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the modify test for the User defined TestCase
      *
      **************************************************************************/
     private void modifyTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             LdapExtClient.modifyTest(dirContext, getUserModAttributes(), getBaseEntryDN());
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the bind for the User defined Thread, this bind is used for
      * the whole context
      *
      **************************************************************************/
     private void bindOp(SampleResult res) throws NamingException {
         DirContext ctx = ldapContexts.remove(getThreadName());
         if (ctx != null) {
             log.warn("Closing previous context for thread: " + getThreadName());
             ctx.close();
         }
         try {
             res.sampleStart();
             ctx = LdapExtClient.connect(getServername(), getPort(), getRootdn(), getUserDN(), getUserPw(),getConnTimeOut(),isSecure());
         } finally {
             res.sampleEnd();
         }
         ldapContexts.put(getThreadName(), ctx);
     }
 
     /***************************************************************************
      * This will do the bind and unbind for the User defined TestCase
      *
      **************************************************************************/
     private void singleBindOp(SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             DirContext ctx = LdapExtClient.connect(getServername(), getPort(), getRootdn(), getUserDN(), getUserPw(),getConnTimeOut(),isSecure());
             LdapExtClient.disconnect(ctx);
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do a moddn Opp for the User new DN defined
      *
      **************************************************************************/
     private void renameTest(DirContext dirContext, SampleResult res) throws NamingException {
         try {
             res.sampleStart();
             LdapExtClient.moddnOp(dirContext, getPropertyAsString(MODDDN), getPropertyAsString(NEWDN));
         } finally {
             res.sampleEnd();
         }
     }
 
     /***************************************************************************
      * This will do the unbind for the User defined TestCase as well as inbuilt
      * test case
      *
      **************************************************************************/
     private void unbindOp(DirContext dirContext, SampleResult res) {
         try {
             res.sampleStart();
             LdapExtClient.disconnect(dirContext);
         } finally {
             res.sampleEnd();
         }
         ldapContexts.remove(getThreadName());
         log.info("context and LdapExtClients removed");
     }
 
     @Override
     public SampleResult sample(Entry e) {
         XMLBuffer xmlBuffer = new XMLBuffer();
         xmlBuffer.openTag("ldapanswer"); // $NON-NLS-1$
         SampleResult res = new SampleResult();
         res.setResponseData("successfull", null);
         res.setResponseMessage("Success"); // $NON-NLS-1$
         res.setResponseCode("0"); // $NON-NLS-1$
         res.setContentType("text/xml");// $NON-NLS-1$
         boolean isSuccessful = true;
         res.setSampleLabel(getName());
         DirContext dirContext = ldapContexts.get(getThreadName());
 
         try {
             xmlBuffer.openTag("operation"); // $NON-NLS-1$
             final String testType = getTest();
             xmlBuffer.tag("opertype", testType); // $NON-NLS-1$
             log.debug("performing test: " + testType);
             if (testType.equals(UNBIND)) {
                 res.setSamplerData("Unbind");
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("binddn",getUserDN()); // $NON-NLS-1$
                 unbindOp(dirContext, res);
             } else if (testType.equals(BIND)) {
                 res.setSamplerData("Bind as "+getUserDN());
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("binddn",getUserDN()); // $NON-NLS-1$
                 xmlBuffer.tag("connectionTO",getConnTimeOut()); // $NON-NLS-1$
                 bindOp(res);
             } else if (testType.equals(SBIND)) {
                 res.setSamplerData("SingleBind as "+getUserDN());
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("binddn",getUserDN()); // $NON-NLS-1$
                 xmlBuffer.tag("connectionTO",getConnTimeOut()); // $NON-NLS-1$
                 singleBindOp(res);
             } else if (testType.equals(COMPARE)) {
                 res.setSamplerData("Compare "+getPropertyAsString(COMPAREFILT) + " "
                                 + getPropertyAsString(COMPAREDN));
                 xmlBuffer.tag("comparedn",getPropertyAsString(COMPAREDN)); // $NON-NLS-1$
                 xmlBuffer.tag("comparefilter",getPropertyAsString(COMPAREFILT)); // $NON-NLS-1$
                 NamingEnumeration<SearchResult> cmp=null;
                 try {
                     res.sampleStart();
                     cmp = LdapExtClient.compare(dirContext, getPropertyAsString(COMPAREFILT),
                             getPropertyAsString(COMPAREDN));
                     if (!cmp.hasMore()) {
                         res.setResponseCode("5"); // $NON-NLS-1$
                         res.setResponseMessage("compareFalse");
                         isSuccessful = false;
                     }
                 } finally {
                     res.sampleEnd();
                     if (cmp != null) {
                         cmp.close();
                     }
                 }
             } else if (testType.equals(ADD)) {
                 res.setSamplerData("Add object " + getBaseEntryDN());
                 xmlBuffer.tag("attributes",getArguments().toString()); // $NON-NLS-1$
                 xmlBuffer.tag("dn",getBaseEntryDN()); // $NON-NLS-1$
                 addTest(dirContext, res);
             } else if (testType.equals(DELETE)) {
                 res.setSamplerData("Delete object " + getBaseEntryDN());
                 xmlBuffer.tag("dn",getBaseEntryDN()); // $NON-NLS-1$
                 deleteTest(dirContext, res);
             } else if (testType.equals(MODIFY)) {
                 res.setSamplerData("Modify object " + getBaseEntryDN());
                 xmlBuffer.tag("dn",getBaseEntryDN()); // $NON-NLS-1$
                 xmlBuffer.tag("attributes",getLDAPArguments().toString()); // $NON-NLS-1$
                 modifyTest(dirContext, res);
             } else if (testType.equals(RENAME)) {
                 res.setSamplerData("ModDN object " + getPropertyAsString(MODDDN) + " to " + getPropertyAsString(NEWDN));
                 xmlBuffer.tag("dn",getPropertyAsString(MODDDN)); // $NON-NLS-1$
                 xmlBuffer.tag("newdn",getPropertyAsString(NEWDN)); // $NON-NLS-1$
                 renameTest(dirContext, res);
             } else if (testType.equals(SEARCH)) {
                 final String scopeStr = getScope();
                 final int scope = getScopeAsInt();
                 final String searchFilter = getPropertyAsString(SEARCHFILTER);
                 final String searchBase = getPropertyAsString(SEARCHBASE);
                 final String timeLimit = getTimelim();
                 final String countLimit = getCountlim();
 
                 res.setSamplerData("Search with filter " + searchFilter);
                 xmlBuffer.tag("searchfilter", StringEscapeUtils.escapeXml10(searchFilter)); // $NON-NLS-1$
                 xmlBuffer.tag("baseobj",getRootdn()); // $NON-NLS-1$
                 xmlBuffer.tag("searchbase",searchBase);// $NON-NLS-1$
                 xmlBuffer.tag("scope" , scopeStr); // $NON-NLS-1$
                 xmlBuffer.tag("countlimit",countLimit); // $NON-NLS-1$
                 xmlBuffer.tag("timelimit",timeLimit); // $NON-NLS-1$
 
                 NamingEnumeration<SearchResult> srch=null;
                 try {
                     res.sampleStart();
                     srch = LdapExtClient.searchTest(
                             dirContext, searchBase, searchFilter,
                             scope, getCountlimAsLong(),
                             getTimelimAsInt(),
                             getRequestAttributes(getAttrs()),
                             isRetobj(),
                             isDeref());
                     if (isParseFlag()) {
                         try {
                             xmlBuffer.openTag("searchresults"); // $NON-NLS-1$
                             writeSearchResults(xmlBuffer, srch);
                         } finally {
                             xmlBuffer.closeTag("searchresults"); // $NON-NLS-1$
                         }
                     } else {
                         xmlBuffer.tag("searchresults", // $NON-NLS-1$
                                 "hasElements="+srch.hasMoreElements()); // $NON-NLS-1$
                     }
                 } finally {
                     if (srch != null){
                         srch.close();
                     }
                     res.sampleEnd();
                 }
 
             }
 
         } catch (NamingException ex) {
             // TODO: tidy this up
             String returnData = ex.toString();
             final int indexOfLDAPErrCode = returnData.indexOf("LDAP: error code");
             if (indexOfLDAPErrCode >= 0) {
                 res.setResponseMessage(returnData.substring(indexOfLDAPErrCode + 21, returnData
                         .indexOf(']'))); // $NON-NLS-1$
                 res.setResponseCode(returnData.substring(indexOfLDAPErrCode + 17, indexOfLDAPErrCode + 19));
             } else {
                 res.setResponseMessage(returnData);
                 res.setResponseCode("800"); // $NON-NLS-1$
             }
             isSuccessful = false;
         } finally {
             xmlBuffer.closeTag("operation"); // $NON-NLS-1$
             xmlBuffer.tag("responsecode",res.getResponseCode()); // $NON-NLS-1$
             xmlBuffer.tag("responsemessage",res.getResponseMessage()); // $NON-NLS-1$
             res.setResponseData(xmlBuffer.toString(), null);
             res.setDataType(SampleResult.TEXT);
             res.setSuccessful(isSuccessful);
         }
         return res;
     }
 
     /*
      *   Write out search results in a stable order (including order of all subelements which might
      * be reordered like attributes and their values) so that simple textual comparison can be done,
      * unless the number of results exceeds {@link #MAX_SORTED_RESULTS} in which case just stream
      * the results out without sorting.
      */
     private void writeSearchResults(final XMLBuffer xmlb, final NamingEnumeration<SearchResult> srch)
             throws NamingException
     {
 
         final ArrayList<SearchResult> sortedResults = new ArrayList<>(
                 MAX_SORTED_RESULTS);
         final String searchBase = getPropertyAsString(SEARCHBASE);
         final String rootDn = getRootdn();
 
         // read all sortedResults into memory so we can guarantee ordering
         try {
             while (srch.hasMore() && (sortedResults.size() < MAX_SORTED_RESULTS)) {
                 final SearchResult sr = srch.next();
 
                     // must be done prior to sorting
                 normaliseSearchDN(sr, searchBase, rootDn);
                 sortedResults.add(sr);
             }
         } finally { // show what we did manage to retrieve
 
             sortResults(sortedResults);
 
             for (final SearchResult sr : sortedResults) {
                 writeSearchResult(sr, xmlb);
             }
         }
 
         while (srch.hasMore()) { // If there's anything left ...
             final SearchResult sr = srch.next();
 
             normaliseSearchDN(sr, searchBase, rootDn);
             writeSearchResult(sr, xmlb);
         }
     }
 
     private void writeSearchResult(final SearchResult sr, final XMLBuffer xmlb)
             throws NamingException
     {
         final Attributes attrs = sr.getAttributes();
         final int size = attrs.size();
         final ArrayList<Attribute> sortedAttrs = new ArrayList<>(size);
 
         xmlb.openTag("searchresult"); // $NON-NLS-1$
         xmlb.tag("dn", sr.getName()); // $NON-NLS-1$
         xmlb.tag("returnedattr",Integer.toString(size)); // $NON-NLS-1$
         xmlb.openTag("attributes"); // $NON-NLS-1$
 
         try {
             for (NamingEnumeration<? extends Attribute> en = attrs.getAll(); en.hasMore();)
             {
                 final Attribute attr = en.next();
                 sortedAttrs.add(attr);
             }
             sortAttributes(sortedAttrs);
             for (final Attribute attr : sortedAttrs) {
                 StringBuilder sb = new StringBuilder();
                 if (attr.size() == 1) {
                     sb.append(getWriteValue(attr.get()));
                 } else {
                     final ArrayList<String> sortedVals = new ArrayList<>(attr.size());
                     boolean first = true;
 
                     for (NamingEnumeration<?> ven = attr.getAll(); ven.hasMore();)
                     {
                         final Object value = getWriteValue(ven.next());
                         sortedVals.add(value.toString());
                     }
 
                     Collections.sort(sortedVals);
 
                     for (final String value : sortedVals)
                     {
                         if (first) {
                             first = false;
                         } else {
                             sb.append(", "); // $NON-NLS-1$
                         }
                         sb.append(value);
                     }
                 }
                 xmlb.tag(attr.getID(),sb);
             }
         } finally {
             xmlb.closeTag("attributes"); // $NON-NLS-1$
             xmlb.closeTag("searchresult"); // $NON-NLS-1$
         }
     }
 
     private void sortAttributes(final List<Attribute> sortedAttrs) {
         sortedAttrs.sort((o1, o2) -> {
             String nm1 = o1.getID();
             String nm2 = o2.getID();
 
             return nm1.compareTo(nm2);
         });
     }
 
     private void sortResults(final List<SearchResult> sortedResults) {
         sortedResults.sort(new Comparator<SearchResult>() {
             private int compareToReverse(final String s1, final String s2) {
                 int len1 = s1.length();
                 int len2 = s2.length();
                 int s1i = len1 - 1;
                 int s2i = len2 - 1;
 
                 for (; (s1i >= 0) && (s2i >= 0); s1i--, s2i--) {
                     char c1 = s1.charAt(s1i);
                     char c2 = s2.charAt(s2i);
 
                     if (c1 != c2) {
                         return c1 - c2;
                     }
                 }
                 return len1 - len2;
             }
 
             @Override
             public int compare(SearchResult o1, SearchResult o2) {
                 String nm1 = o1.getName();
                 String nm2 = o2.getName();
 
                 if (nm1 == null) {
                     nm1 = "";
                 }
                 if (nm2 == null) {
                     nm2 = "";
                 }
                 return compareToReverse(nm1, nm2);
             }
         });
     }
 
     private String normaliseSearchDN(final SearchResult sr, final String searchBase, final String rootDn)
     {
         String srName = sr.getName();
 
         if (!srName.endsWith(searchBase))
         {
             if (srName.length() > 0) {
                 srName = srName + ',';
             }
             srName = srName + searchBase;
         }
         if ((rootDn.length() > 0) && !srName.endsWith(rootDn))
         {
             if (srName.length() > 0) {
                 srName = srName + ',';
             }
             srName = srName + rootDn;
         }
         sr.setName(srName);
         return srName;
     }
 
     private String getWriteValue(final Object value)
     {
         if (value instanceof String) {
             // assume it's senstive data
             return StringEscapeUtils.escapeXml10((String)value);
         }
         if (value instanceof byte[]) {
             return StringEscapeUtils.escapeXml10(new String((byte[])value, StandardCharsets.UTF_8));
         }
         return StringEscapeUtils.escapeXml10(value.toString());
     }
 
     @Override
     public void testStarted() {
         testStarted(""); // $NON-NLS-1$
     }
 
     @Override
     public void testEnded() {
         testEnded(""); // $NON-NLS-1$
     }
 
     @Override
     public void testStarted(String host) {
         // ignored
     }
 
     // Ensure any remaining contexts are closed
     @Override
     public void testEnded(String host) {
         for (Map.Entry<String, DirContext> entry : ldapContexts.entrySet()) {
             DirContext dc = entry.getValue();
             try {
                 log.warn("Tidying old Context for thread: " + entry.getKey());
                 dc.close();
             } catch (NamingException ignored) {
                 // ignored
             }
         }
         ldapContexts.clear();
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
diff --git a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPSampler.java b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPSampler.java
index 70d10b8f1..99ffdc519 100644
--- a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPSampler.java
+++ b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LDAPSampler.java
@@ -1,493 +1,493 @@
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
 
 package org.apache.jmeter.protocol.ldap.sampler;
 
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import javax.naming.NamingException;
 import javax.naming.directory.Attribute;
 import javax.naming.directory.BasicAttribute;
 import javax.naming.directory.BasicAttributes;
 import javax.naming.directory.DirContext;
 import javax.naming.directory.ModificationItem;
 
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Ldap Sampler class is main class for the LDAP test. This will control all the
  * test available in the LDAP Test.
  *
  */
 public class LDAPSampler extends AbstractSampler {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LDAPSampler.class);
 
     private static final long serialVersionUID = 240L;
 
     private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
             Arrays.asList(
                     "org.apache.jmeter.config.gui.LoginConfigGui",
                     "org.apache.jmeter.protocol.ldap.config.gui.LdapConfigGui",
                     "org.apache.jmeter.config.gui.SimpleConfigGui"
             ));
     
     public static final String SERVERNAME = "servername"; //$NON-NLS-1$
 
     public static final String PORT = "port"; //$NON-NLS-1$
 
     public static final String ROOTDN = "rootdn"; //$NON-NLS-1$
 
     public static final String TEST = "test"; //$NON-NLS-1$
 
     public static final String ADD = "add"; //$NON-NLS-1$
 
     public static final String MODIFY = "modify"; //$NON-NLS-1$
 
     public static final String DELETE = "delete"; //$NON-NLS-1$
 
     public static final String SEARCHBASE = "search"; //$NON-NLS-1$
 
     public static final String SEARCHFILTER = "searchfilter"; //$NON-NLS-1$
 
     public static final String USER_DEFINED = "user_defined"; //$NON-NLS-1$
 
     public static final String ARGUMENTS = "arguments"; //$NON-NLS-1$
 
     public static final String BASE_ENTRY_DN = "base_entry_dn"; //$NON-NLS-1$
 
     // For In build test case using this counter
     // create the new entry in the server
     private static AtomicInteger COUNTER = new AtomicInteger(0);
 
     private boolean searchFoundEntries;// TODO turn into parameter?
 
     public LDAPSampler() {
     }
 
     /**
      * Gets the username attribute of the LDAP object.
      *
      * @return the username
      */
     public String getUsername() {
         return getPropertyAsString(ConfigTestElement.USERNAME);
     }
 
     /**
      * Gets the password attribute of the LDAP object.
      *
      * @return the password
      */
     public String getPassword() {
         return getPropertyAsString(ConfigTestElement.PASSWORD);
     }
 
     /**
      * Sets the Servername attribute of the ServerConfig object.
      *
      * @param servername
      *            the new servername value
      */
     public void setServername(String servername) {
         setProperty(new StringProperty(SERVERNAME, servername));
     }
 
     /**
      * Sets the Port attribute of the ServerConfig object.
      *
      * @param port
      *            the new Port value
      */
     public void setPort(String port) {
         setProperty(new StringProperty(PORT, port));
     }
 
     /**
      * Gets the servername attribute of the LDAPSampler object.
      *
      * @return the Servername value
      */
     public String getServername() {
         return getPropertyAsString(SERVERNAME);
     }
 
     /**
      * Gets the Port attribute of the LDAPSampler object.
      *
      * @return the Port value
      */
     public String getPort() {
         return getPropertyAsString(PORT);
     }
 
     /**
      * Sets the Rootdn attribute of the LDAPSampler object.
      *
      * @param newRootdn
      *            the new rootdn value
      */
     public void setRootdn(String newRootdn) {
         this.setProperty(ROOTDN, newRootdn);
     }
 
     /**
      * Gets the Rootdn attribute of the LDAPSampler object.
      *
      * @return the Rootdn value
      */
     public String getRootdn() {
         return getPropertyAsString(ROOTDN);
     }
 
     /**
      * Sets the Test attribute of the LdapConfig object.
      *
      * @param newTest
      *            the new test value(Add,Modify,Delete and search)
      */
     public void setTest(String newTest) {
         this.setProperty(TEST, newTest);
     }
 
     /**
      * Gets the test attribute of the LDAPSampler object.
      *
      * @return the test value (Add, Modify, Delete and search)
      */
     public String getTest() {
         return getPropertyAsString(TEST);
     }
 
     /**
      * Sets the UserDefinedTest attribute of the LDAPSampler object.
      *
      * @param value
      *            the new UserDefinedTest value
      */
     public void setUserDefinedTest(boolean value) {
         setProperty(new BooleanProperty(USER_DEFINED, value));
     }
 
     /**
      * Gets the UserDefinedTest attribute of the LDAPSampler object.
      *
      * @return the test value true or false. If true it will do the
      *         UserDefinedTest else our own inbuild test case.
      */
     public boolean getUserDefinedTest() {
         return getPropertyAsBoolean(USER_DEFINED);
     }
 
     /**
      * Sets the Base Entry DN attribute of the LDAPSampler object.
      *
      * @param newbaseentry
      *            the new Base entry DN value
      */
     public void setBaseEntryDN(String newbaseentry) {
         setProperty(new StringProperty(BASE_ENTRY_DN, newbaseentry));
     }
 
     /**
      * Gets the BaseEntryDN attribute of the LDAPSampler object.
      *
      * @return the Base entry DN value
      */
     public String getBaseEntryDN() {
         return getPropertyAsString(BASE_ENTRY_DN);
     }
 
     /**
      * Sets the Arguments attribute of the LdapConfig object. This will collect
      * values from the table for user defined test case.
      *
      * @param value
      *            the arguments
      */
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     /**
      * Gets the Arguments attribute of the LdapConfig object.
      *
      * @return the arguments. User defined test case.
      */
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
     }
 
     /**
      * Collect all the value from the table (Arguments), using this create the
      * basicAttributes. This will create the Basic Attributes for the User
      * defined TestCase for Add Test.
      *
      * @return the BasicAttributes
      */
     private BasicAttributes getUserAttributes() {
         BasicAttribute basicattribute = new BasicAttribute("objectclass"); //$NON-NLS-1$
         basicattribute.add("top"); //$NON-NLS-1$
         basicattribute.add("person"); //$NON-NLS-1$
         basicattribute.add("organizationalPerson"); //$NON-NLS-1$
         basicattribute.add("inetOrgPerson"); //$NON-NLS-1$
         BasicAttributes attrs = new BasicAttributes(true);
         attrs.put(basicattribute);
         BasicAttribute attr;
 
         for (JMeterProperty jMeterProperty : getArguments()) {
             Argument item = (Argument) jMeterProperty.getObjectValue();
             attr = getBasicAttribute(item.getName(), item.getValue());
             attrs.put(attr);
         }
         return attrs;
     }
 
     /**
      * Collect all the value from the table (Arguments), using this create the
      * basicAttributes. This will create the Basic Attributes for the User
      * defined TestCase for Modify test.
      *
      * @return the BasicAttributes
      */
     private ModificationItem[] getUserModAttributes() {
         ModificationItem[] mods = new ModificationItem[getArguments().getArguments().size()];
         BasicAttribute attr;
         PropertyIterator iter = getArguments().iterator();
         int count = 0;
         while (iter.hasNext()) {
             Argument item = (Argument) iter.next().getObjectValue();
             attr = getBasicAttribute(item.getName(), item.getValue());
             mods[count] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
             count = +1;
         }
         return mods;
     }
 
     /**
      * This will create the Basic Attributes for the Inbuilt TestCase for Modify
      * test.
      *
      * @return the BasicAttributes
      */
     private ModificationItem[] getModificationItem() {
         ModificationItem[] mods = new ModificationItem[2];
         // replace (update) attribute
         Attribute mod0 = new BasicAttribute("userpassword", "secret"); //$NON-NLS-1$ //$NON-NLS-2$
         // add mobile phone number attribute
         Attribute mod1 = new BasicAttribute("mobile", "123-456-1234"); //$NON-NLS-1$ //$NON-NLS-2$
 
         mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
         mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod1);
 
         return mods;
     }
 
     /**
      * This will create the Basic Attributes for the In build TestCase for Add
      * Test.
      *
      * @return the BasicAttributes
      */
     private BasicAttributes getBasicAttributes() {
         BasicAttributes basicattributes = new BasicAttributes();
         BasicAttribute basicattribute = new BasicAttribute("objectclass"); //$NON-NLS-1$
         basicattribute.add("top"); //$NON-NLS-1$
         basicattribute.add("person"); //$NON-NLS-1$
         basicattribute.add("organizationalPerson"); //$NON-NLS-1$
         basicattribute.add("inetOrgPerson"); //$NON-NLS-1$
         basicattributes.put(basicattribute);
         String s1 = "User"; //$NON-NLS-1$
         String s3 = "Test"; //$NON-NLS-1$
         String s5 = "user"; //$NON-NLS-1$
         String s6 = "test"; //$NON-NLS-1$
         COUNTER.incrementAndGet();
         basicattributes.put(new BasicAttribute("givenname", s1)); //$NON-NLS-1$
         basicattributes.put(new BasicAttribute("sn", s3)); //$NON-NLS-1$
         basicattributes.put(new BasicAttribute("cn", "TestUser" + COUNTER.get())); //$NON-NLS-1$ //$NON-NLS-2$
         basicattributes.put(new BasicAttribute("uid", s5)); //$NON-NLS-1$
         basicattributes.put(new BasicAttribute("userpassword", s6)); //$NON-NLS-1$
         setProperty(new StringProperty(ADD, "cn=TestUser" + COUNTER.get())); //$NON-NLS-1$
         return basicattributes;
     }
 
     /**
      * This will create the Basic Attribute for the given name value pair.
      *
      * @return the BasicAttribute
      */
     private BasicAttribute getBasicAttribute(String name, String value) {
         BasicAttribute attr = new BasicAttribute(name, value);
         return attr;
     }
 
     /**
      * Returns a formatted string label describing this sampler
      *
      * @return a formatted string label describing this sampler
      */
     public String getLabel() {
         return "ldap://" + this.getServername() + ":" + getPort() + "/" + this.getRootdn();
     }
 
     /**
      * This will do the add test for the User defined TestCase as well as
      * inbuilt test case.
      *
      */
     private void addTest(LdapClient ldap, SampleResult res) throws NamingException {
         if (getPropertyAsBoolean(USER_DEFINED)) {
             res.sampleStart();
             ldap.createTest(getUserAttributes(), getPropertyAsString(BASE_ENTRY_DN));
             res.sampleEnd();
         } else {
             res.sampleStart();
             ldap.createTest(getBasicAttributes(), getPropertyAsString(ADD));
             res.sampleEnd();
             ldap.deleteTest(getPropertyAsString(ADD));
         }
     }
 
     /**
      * This will do the delete test for the User defined TestCase as well as
      * inbuilt test case.
      *
      */
     private void deleteTest(LdapClient ldap, SampleResult res) throws NamingException {
         if (!getPropertyAsBoolean(USER_DEFINED)) {
             ldap.createTest(getBasicAttributes(), getPropertyAsString(ADD));
             setProperty(new StringProperty(DELETE, getPropertyAsString(ADD)));
         }
         res.sampleStart();
         ldap.deleteTest(getPropertyAsString(DELETE));
         res.sampleEnd();
     }
 
     /**
      * This will do the search test for the User defined TestCase as well as
      * inbuilt test case.
      *
      */
     private void searchTest(LdapClient ldap, SampleResult res) throws NamingException {
         if (!getPropertyAsBoolean(USER_DEFINED)) {
             ldap.createTest(getBasicAttributes(), getPropertyAsString(ADD));
             setProperty(new StringProperty(SEARCHBASE, getPropertyAsString(ADD)));
             setProperty(new StringProperty(SEARCHFILTER, getPropertyAsString(ADD)));
         }
         res.sampleStart();
         searchFoundEntries = ldap.searchTest(getPropertyAsString(SEARCHBASE), getPropertyAsString(SEARCHFILTER));
         res.sampleEnd();
         if (!getPropertyAsBoolean(USER_DEFINED)) {
             ldap.deleteTest(getPropertyAsString(ADD));
         }
     }
 
     /**
      * This will do the search test for the User defined TestCase as well as
      * inbuilt test case.
      *
      */
     private void modifyTest(LdapClient ldap, SampleResult res) throws NamingException {
         if (getPropertyAsBoolean(USER_DEFINED)) {
             res.sampleStart();
             ldap.modifyTest(getUserModAttributes(), getPropertyAsString(BASE_ENTRY_DN));
             res.sampleEnd();
         } else {
             ldap.createTest(getBasicAttributes(), getPropertyAsString(ADD));
             setProperty(new StringProperty(MODIFY, getPropertyAsString(ADD)));
             res.sampleStart();
             ldap.modifyTest(getModificationItem(), getPropertyAsString(MODIFY));
             res.sampleEnd();
             ldap.deleteTest(getPropertyAsString(ADD));
         }
     }
 
     @Override
     public SampleResult sample(Entry e) {
         SampleResult res = new SampleResult();
         boolean isSuccessful = false;
         res.setSampleLabel(getName());
         res.setSamplerData(getPropertyAsString(TEST));// TODO improve this
         LdapClient ldap = new LdapClient();
 
         try {
             ldap.connect(getServername(), getPort(), getRootdn(), getUsername(), getPassword());
 
             if (getPropertyAsString(TEST).equals(ADD)) {
                 addTest(ldap, res);
             } else if (getPropertyAsString(TEST).equals(DELETE)) {
                 deleteTest(ldap, res);
             } else if (getPropertyAsString(TEST).equals(MODIFY)) {
                 modifyTest(ldap, res);
             } else if (getPropertyAsString(TEST).equals(SEARCHBASE)) {
                 searchTest(ldap, res);
             }
 
             // TODO - needs more work ...
             if (getPropertyAsString(TEST).equals(SEARCHBASE) && !searchFoundEntries) {
                 res.setResponseCode("201");// TODO is this a sensible number? //$NON-NLS-1$
                 res.setResponseMessage("OK - no results");
                 res.setResponseData("successful - no results", null);
             } else {
                 res.setResponseCodeOK();
                 res.setResponseMessage("OK"); //$NON-NLS-1$
                 res.setResponseData("successful", null);
             }
             res.setDataType(SampleResult.TEXT);
             isSuccessful = true;
         } catch (Exception ex) {
             log.error("Ldap client - ", ex);
             // Could time this
             // res.sampleEnd();
             // if sampleEnd() is not called, elapsed time will remain zero
             res.setResponseCode("500");// TODO distinguish errors better //$NON-NLS-1$
             res.setResponseMessage(ex.toString());
             isSuccessful = false;
         } finally {
             ldap.disconnect();
         }
 
         // Set if we were successful or not
         res.setSuccessful(isSuccessful);
         return res;
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
diff --git a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapClient.java b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapClient.java
index 3798988cb..10ca6ef93 100644
--- a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapClient.java
+++ b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapClient.java
@@ -1,154 +1,153 @@
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
 
 package org.apache.jmeter.protocol.ldap.sampler;
 
 import java.util.Hashtable;
 
 import javax.naming.Context;
 import javax.naming.NamingEnumeration;
 import javax.naming.NamingException;
 // import javax.naming.directory.Attributes;
 import javax.naming.directory.BasicAttributes;
 import javax.naming.directory.DirContext;
 import javax.naming.directory.InitialDirContext;
 import javax.naming.directory.ModificationItem;
 import javax.naming.directory.SearchControls;
-// import javax.naming.directory.SearchResult;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Ldap Client class is main class to create, modify, search and delete all the
  * LDAP functionality available.
  *
  */
 public class LdapClient {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LdapClient.class);
 
     private DirContext dirContext = null;
 
     /**
      * Constructor for the LdapClient object.
      */
     public LdapClient() {
     }
 
     /**
      * Connect to server.
      * 
      * @param host
      *            name of the ldap server
      * @param port
      *            port of the ldap server
      * @param rootdn
      *            base dn to start ldap operations from
      * @param username
      *            user name to use for binding
      * @param password
      *            password to use for binding
      * @throws NamingException
      *             if {@link InitialDirContext} can not be build using the above
      *             parameters
      */
     public void connect(String host, String port, String rootdn, String username, String password)
             throws NamingException {
         Hashtable<String, String> env = new Hashtable<>();
         env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); //$NON-NLS-1$
         env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port + "/" + rootdn); //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
         env.put(Context.REFERRAL, "throw"); //$NON-NLS-1$
         env.put(Context.SECURITY_CREDENTIALS, password);
         env.put(Context.SECURITY_PRINCIPAL, username);
         dirContext = new InitialDirContext(env);
     }
 
     /**
      * Disconnect from the server.
      */
     public void disconnect() {
         try {
             if (dirContext != null) {
                 dirContext.close();
                 dirContext = null;
             }
         } catch (NamingException e) {
             log.error("Ldap client - ", e);
         }
     }
 
     /**
      * Filter the data in the ldap directory for the given search base.
      *
      * @param searchBase
      *            where the search should start
      * @param searchFilter
      *            filter this value from the base
      * @return <code>true</code> when the search yields results,
      *         <code>false</code> otherwise
      * @throws NamingException
      *             when searching fails
      */
     public boolean searchTest(String searchBase, String searchFilter) throws NamingException {
         SearchControls searchcontrols = new SearchControls(SearchControls.SUBTREE_SCOPE,
                 1L, // count limit
                 0, // time limit
                 null,// attributes (null = all)
                 false,// return object ?
                 false);// dereference links?
         NamingEnumeration<?> ne = dirContext.search(searchBase, searchFilter, searchcontrols);
         return ne.hasMore();
     }
 
     /**
      * Modify the attribute in the ldap directory for the given string.
      *
      * @param mods
      *            list of all {@link ModificationItem}s to apply
      * @param string
      *            dn of the object to modify
      * @throws NamingException when modification fails
      */
     public void modifyTest(ModificationItem[] mods, String string) throws NamingException {
         dirContext.modifyAttributes(string, mods);
     }
 
     /**
      * Create the attribute in the ldap directory for the given string.
      *
      * @param basicattributes
      *            add all the entry in to the basicattribute
      * @param string
      *            the string (dn) value
      * @throws NamingException when creating subcontext fails
      */
     public void createTest(BasicAttributes basicattributes, String string) throws NamingException {
         dirContext.createSubcontext(string, basicattributes);
     }
 
     /**
      * Delete the attribute from the ldap directory.
      *
      * @param string
      *            the string (dn) value
      * @throws NamingException when destroying sub context fails
      */
     public void deleteTest(String string) throws NamingException {
         dirContext.destroySubcontext(string);
     }
 }
diff --git a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapExtClient.java b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapExtClient.java
index 9ea107eaf..671eb93d3 100644
--- a/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapExtClient.java
+++ b/src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapExtClient.java
@@ -1,285 +1,284 @@
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
 
 package org.apache.jmeter.protocol.ldap.sampler;
 
 import java.util.Hashtable;
 
 import javax.naming.Context;
-import javax.naming.NamingException;
 import javax.naming.NamingEnumeration;
+import javax.naming.NamingException;
 import javax.naming.directory.Attributes;
 import javax.naming.directory.DirContext;
 import javax.naming.directory.InitialDirContext;
 import javax.naming.directory.ModificationItem;
 import javax.naming.directory.SearchControls;
 import javax.naming.directory.SearchResult;
 
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /*******************************************************************************
  *
  * Ldap Client class is main class to create ,modify, search and delete all the
  * LDAP functionality available
  *
  * author Dolf Smits(Dolf.Smits@Siemens.com) created Aug 09 2003 11:00 AM
  * company Siemens Netherlands N.V..
  *
  * Based on the work of: author T.Elanjchezhiyan(chezhiyan@siptech.co.in)
- * created Apr 29 2003 11:00 AM company Sip Technologies and Exports Ltd.
  *
  ******************************************************************************/
 public class LdapExtClient {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(LdapExtClient.class);
 
     private static final String CONTEXT_IS_NULL = "Context is null";
 
     /**
      * Constructor for the LdapClient object
      */
     public LdapExtClient() {
     }
 
     /**
      * connect to server
      *
      * @param host
      *            name of the server to connect
      * @param port
      *            port of the server to connect
      * @param rootdn
      *            base of the tree to operate on
      * @param username
      *            name of the user to use for binding
      * @param password
      *            password to use for binding
      * @param connTimeOut
      *            connection timeout for connecting the server see
      *            "com.sun.jndi.ldap.connect.timeout"
      * @param secure
      *            flag whether ssl should be used
      * @return newly created {@link DirContext}
      * @exception NamingException
      *                when creating the {@link DirContext} fails
      */
     public static DirContext connect(String host, String port, String rootdn, String username, String password, String connTimeOut, boolean secure)
             throws NamingException {
         DirContext dirContext;
         Hashtable<String, String> env = new Hashtable<>();
         env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // $NON-NLS-1$
         StringBuilder sb = new StringBuilder(80);
         if (secure) {
             sb.append("ldaps://"); // $NON-NLS-1$
         } else {
             sb.append("ldap://"); // $NON-NLS-1$
         }
         sb.append(host);
         if (port.length()>0){
             sb.append(":"); // $NON-NLS-1$
             sb.append(port);
         }
         sb.append("/"); // $NON-NLS-1$
         sb.append(rootdn);
         env.put(Context.PROVIDER_URL,sb.toString());
         log.info("prov_url= " + env.get(Context.PROVIDER_URL)); // $NON-NLS-1$
         if (connTimeOut.length()> 0) {
             env.put("com.sun.jndi.ldap.connect.timeout", connTimeOut); // $NON-NLS-1$
         }
         env.put(Context.REFERRAL, "throw"); // $NON-NLS-1$
         env.put("java.naming.batchsize", "0"); // $NON-NLS-1$  // $NON-NLS-2$
         env.put(Context.SECURITY_CREDENTIALS, password);
         env.put(Context.SECURITY_PRINCIPAL, username);
         dirContext = new InitialDirContext(env);
         return dirContext;
     }
 
     /**
      * disconnect from the server
      *
      * @param dirContext
      *            context do disconnect (may be <code>null</code>)
      */
     public static void disconnect(DirContext dirContext) {
         if (dirContext == null) {
             log.info("Cannot disconnect null context");
             return;
         }
 
         try {
             dirContext.close();
         } catch (NamingException e) {
             log.warn("Ldap client disconnect - ", e);
         }
     }
 
     /***************************************************************************
      * Filter the data in the ldap directory for the given search base
      * 
      * @param dirContext
      *            context to perform the search on
      *
      * @param searchBase
      *            base where the search should start
      * @param searchFilter
      *            filter this value from the base
      * @param scope
      *            scope for search. May be one of
      *            {@link SearchControls#OBJECT_SCOPE},
      *            {@link SearchControls#ONELEVEL_SCOPE} or
      *            {@link SearchControls#SUBTREE_SCOPE}
      * @param countlim
      *            max number of results to get, <code>0</code> for all entries
      * @param timelim
      *            max time to wait for entries (in milliseconds), <code>0</code>
      *            for unlimited time
      * @param attrs
      *            list of attributes to return. If <code>null</code> all
      *            attributes will be returned. If empty, none will be returned
      * @param retobj
      *            flag whether the objects should be returned
      * @param deref
      *            flag whether objects should be dereferenced
      * @return result of the search
      * @throws NamingException
      *             when searching fails
      **************************************************************************/
     public static NamingEnumeration<SearchResult> searchTest(DirContext dirContext, String searchBase, String searchFilter, int scope, long countlim,
             int timelim, String[] attrs, boolean retobj, boolean deref) throws NamingException {
         if (dirContext == null) {
             throw new NamingException(CONTEXT_IS_NULL);
         }
         if (log.isDebugEnabled()){
             log.debug(
                     "searchBase=" + searchBase +
                     " scope=" + scope +
                     " countlim=" + countlim +
                     " timelim=" + timelim +
                     " attrs=" + JMeterUtils.unsplit(attrs,",") +
                     " retobj=" + retobj +
                     " deref=" + deref +
                     " filter=" + searchFilter
                       );
         }
         SearchControls searchcontrols = new SearchControls(scope, countlim,
                 timelim, attrs, retobj, deref);
         return dirContext.search(searchBase, searchFilter, searchcontrols);
     }
 
     /***************************************************************************
      * Filter the data in the ldap directory
      *
      * @param dirContext
      *            the context to operate on
      * @param filter
      *            filter this value from the base
      * @param entrydn
      *            distinguished name of entry to compare
      * @return result of the search
      * @throws NamingException
      *             when searching fails
      **************************************************************************/
     public static NamingEnumeration<SearchResult> compare(DirContext dirContext, String filter, String entrydn) throws NamingException {
         if (dirContext == null) {
             throw new NamingException(CONTEXT_IS_NULL);
         }
         SearchControls searchcontrols = new SearchControls(0, 1, 0, new String[0], false, false);
         return dirContext.search(entrydn, filter, searchcontrols);
     }
 
     /***************************************************************************
      * ModDN the data in the ldap directory for the given search base
      *
      * @param dirContext
      *            context to operate on
      * @param ddn
      *            distinguished name of object to rename
      * @param newdn
      *            new distinguished name of object
      * @throws NamingException
      *             when renaming fails
      *
      **************************************************************************/
     public static void moddnOp(DirContext dirContext, String ddn, String newdn) throws NamingException {
         log.debug("ddn and newDn= " + ddn + "@@@@" + newdn);
         if (dirContext == null) {
             throw new NamingException(CONTEXT_IS_NULL);
         }
         dirContext.rename(ddn, newdn);
     }
 
     /***************************************************************************
      * Modify the attribute in the ldap directory for the given string
      *
      * @param dirContext
      *            context to operate on
      * @param mods
      *            list of all the {@link ModificationItem}s to apply on
      *            <code>string</code>
      * @param string
      *            distinguished name of the object to modify
      * @throws NamingException
      *             when modification fails
      **************************************************************************/
     public static void modifyTest(DirContext dirContext, ModificationItem[] mods, String string) throws NamingException {
         if (dirContext == null) {
             throw new NamingException(CONTEXT_IS_NULL);
         }
         dirContext.modifyAttributes(string, mods);
 
     }
 
     /***************************************************************************
      * Create the entry in the ldap directory for the given string
      *
      * @param dirContext
      *            context to operate on
      * @param attributes
      *            add all the attributes and values from the attributes object
      * @param string
      *            distinguished name of the subcontext to create
      * @return newly created subcontext
      * @throws NamingException
      *             when creating subcontext fails
      **************************************************************************/
     public static DirContext createTest(DirContext dirContext, Attributes attributes, String string)
             throws NamingException {
         if (dirContext == null) {
             throw new NamingException(CONTEXT_IS_NULL);
         }
         return dirContext.createSubcontext(string, attributes);
     }
 
     /***************************************************************************
      * Delete the attribute from the ldap directory
      *
      * @param dirContext
      *            context to operate on
      * @param string
      *            distinguished name of the subcontext to destroy
      * @throws NamingException
      *             when destroying the subcontext fails
      **************************************************************************/
     public static void deleteTest(DirContext dirContext, String string) throws NamingException {
         if (dirContext == null) {
             throw new NamingException(CONTEXT_IS_NULL);
         }
         dirContext.destroySubcontext(string);
     }
 }
