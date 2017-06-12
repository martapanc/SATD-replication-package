diff --git a/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java b/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
index d471ebe42..06458f4a8 100644
--- a/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/BeanShellAssertion.java
@@ -1,176 +1,176 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.IOException;
 import java.io.Serializable;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.threads.JMeterContext;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jmeter.threads.JMeterVariables;
 import org.apache.jmeter.util.BeanShellInterpreter;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterException;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * A sampler which understands BeanShell
  * 
  * @version $Revision$ Updated on: $Date$
  */
 public class BeanShellAssertion extends AbstractTestElement implements Serializable, Assertion {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	public static final String FILENAME = "BeanShellAssertion.filename"; //$NON-NLS-1$
 
 	public static final String SCRIPT = "BeanShellAssertion.query"; //$NON-NLS-1$
 
 	public static final String PARAMETERS = "BeanShellAssertion.parameters"; //$NON-NLS-1$
 
 	// Not serialised - recreated as needed
 	transient private BeanShellInterpreter bshInterpreter = null;
 
 	// can be specified in jmeter.properties
 	public static final String INIT_FILE = "beanshell.assertion.init"; //$NON-NLS-1$
 
 	public BeanShellAssertion() {
 		init();
 	}
 
 	// Ensure deserialisation works in server
 	private Object readResolve(){
 		init();
 		return this;
 	}
 
 	private void init(){
 		try {
 			bshInterpreter = new BeanShellInterpreter();
 			String init = JMeterUtils.getProperty(INIT_FILE);
 			try {
 				bshInterpreter.init(init, log);
 			} catch (IOException e) {
 				log.warn("Could not initialise interpreter", e);
 			} catch (JMeterException e) {
 				log.warn("Could not initialise interpreter", e);
 			}
 		} catch (ClassNotFoundException e) {
 			log.error("Could not establish BeanShellInterpreter: " + e);
 		}		
 	}
 	public String getScript() {
 		return getPropertyAsString(SCRIPT);
 	}
 
 	public String getFilename() {
 		return getPropertyAsString(FILENAME);
 	}
 
 	public String getParameters() {
 		return getPropertyAsString(PARAMETERS);
 	}
 
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see org.apache.jmeter.assertions.Assertion#getResult(org.apache.jmeter.samplers.SampleResult)
 	 */
 	public AssertionResult getResult(SampleResult response) {
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 
 		if (bshInterpreter == null) {
 			result.setFailure(true);
 			result.setError(true);
 			result.setFailureMessage("BeanShell Interpreter not found");
 			return result;
 		}
 		try {
 			String request = getScript();
 			String fileName = getFilename();
 
 			bshInterpreter.set("FileName", getFilename());//$NON-NLS-1$
 			// Set params as a single line
 			bshInterpreter.set("Parameters", getParameters()); // $NON-NLS-1$
 			bshInterpreter.set("bsh.args",//$NON-NLS-1$
 					JOrphanUtils.split(getParameters(), " "));//$NON-NLS-1$
 
 			// Add SamplerData for consistency with BeanShell Sampler
 			bshInterpreter.set("SampleResult", response); //$NON-NLS-1$
 			bshInterpreter.set("Response", response); //$NON-NLS-1$
 			bshInterpreter.set("ResponseData", response.getResponseData());//$NON-NLS-1$
 			bshInterpreter.set("ResponseCode", response.getResponseCode());//$NON-NLS-1$
 			bshInterpreter.set("ResponseMessage", response.getResponseMessage());//$NON-NLS-1$
 			bshInterpreter.set("ResponseHeaders", response.getResponseHeaders());//$NON-NLS-1$
 			bshInterpreter.set("RequestHeaders", response.getRequestHeaders());//$NON-NLS-1$
 			bshInterpreter.set("SampleLabel", response.getSampleLabel());//$NON-NLS-1$
 			bshInterpreter.set("SamplerData", response.getSamplerData());//$NON-NLS-1$
 			bshInterpreter.set("Successful", response.isSuccessful());//$NON-NLS-1$
 
 			// The following are used to set the Result details on return from
 			// the script:
 			bshInterpreter.set("FailureMessage", "");//$NON-NLS-1$ //$NON-NLS-2$
 			bshInterpreter.set("Failure", false);//$NON-NLS-1$
 
 			// Add variables for access to context and variables
 			JMeterContext jmctx = JMeterContextService.getContext();
 			JMeterVariables vars = jmctx.getVariables();
 			bshInterpreter.set("ctx", jmctx);//$NON-NLS-1$
 			bshInterpreter.set("vars", vars);//$NON-NLS-1$
 
 			// Object bshOut;
 
 			if (fileName.length() == 0) {
 				// bshOut =
 				bshInterpreter.eval(request);
 			} else {
 				// bshOut =
 				bshInterpreter.source(fileName);
 			}
 
 			result.setFailureMessage(bshInterpreter.get("FailureMessage").toString());//$NON-NLS-1$
 			result.setFailure(Boolean.valueOf(bshInterpreter.get("Failure") //$NON-NLS-1$
 					.toString()).booleanValue());
 			result.setError(false);
 		}
 		/*
 		 * To avoid class loading problems when the BSH jar is missing, we don't
 		 * try to catch this error separately catch (bsh.EvalError ex) {
 		 * log.debug("",ex); result.setError(true);
 		 * result.setFailureMessage(ex.toString()); }
 		 */
 		// but we do trap this error to make tests work better
 		catch (NoClassDefFoundError ex) {
 			log.error("BeanShell Jar missing? " + ex.toString());
 			result.setError(true);
 			result.setFailureMessage("BeanShell Jar missing? " + ex.toString());
 			response.setStopThread(true); // No point continuing
 		} catch (Exception ex) // Mainly for bsh.EvalError
 		{
 			result.setError(true);
 			result.setFailureMessage(ex.toString());
 			log.warn(ex.toString());
 		}
 
 		return result;
 	}
 }
\ No newline at end of file
diff --git a/src/components/org/apache/jmeter/assertions/DurationAssertion.java b/src/components/org/apache/jmeter/assertions/DurationAssertion.java
index b5513f953..7733a395e 100644
--- a/src/components/org/apache/jmeter/assertions/DurationAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/DurationAssertion.java
@@ -1,72 +1,72 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Checks if an Sample is sampled within a specified time-frame. If the duration
  * is larger than the timeframe the Assertion is considered a failure.
  * 
  * author <a href="mailto:wolfram.rittmeyer@web.de">Wolfram Rittmeyer</a>
  * @version $Revision$, $Date$
  */
 public class DurationAssertion extends AbstractTestElement implements Serializable, Assertion {
 	/** Key for storing assertion-informations in the jmx-file. */
 	public static final String DURATION_KEY = "DurationAssertion.duration"; // $NON-NLS-1$
 
 	/**
 	 * Returns the result of the Assertion. Here it checks wether the Sample
 	 * took to long to be considered successful. If so an AssertionResult
 	 * containing a FailureMessage will be returned. Otherwise the returned
 	 * AssertionResult will reflect the success of the Sample.
 	 */
 	public AssertionResult getResult(SampleResult response) {
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		result.setFailure(false);
 		long duration=getAllowedDuration();
 		if (duration > 0) {
 			long responseTime=response.getTime();
 		// has the Sample lasted too long?
 			if ( responseTime > duration) {
 				result.setFailure(true);
 				Object[] arguments = { new Long(responseTime), new Long(duration) };
 				String message = MessageFormat.format(
 						JMeterUtils.getResString("duration_assertion_failure") // $NON-NLS-1$
 						, arguments);
 				result.setFailureMessage(message);
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Returns the duration to be asserted. A duration of 0 indicates this
 	 * assertion is to be ignored.
 	 */
 	private long getAllowedDuration() {
 		return getPropertyAsLong(DURATION_KEY);
 	}
 
 }
\ No newline at end of file
diff --git a/src/components/org/apache/jmeter/assertions/HTMLAssertion.java b/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
index 91697e3b4..71981ffeb 100644
--- a/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/HTMLAssertion.java
@@ -1,370 +1,370 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.io.Serializable;
 import java.io.StringWriter;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.w3c.tidy.Node;
 import org.w3c.tidy.Tidy;
 
 /**
  * Assertion to validate the response of a Sample with Tidy.
  */
 public class HTMLAssertion extends AbstractTestElement implements Serializable, Assertion {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
 	public static final String DEFAULT_DOCTYPE = "omit"; //$NON-NLS-1$
 
 	public static final String DOCTYPE_KEY = "html_assertion_doctype"; //$NON-NLS-1$
 
 	public static final String ERRORS_ONLY_KEY = "html_assertion_errorsonly"; //$NON-NLS-1$
 
 	public static final String ERROR_THRESHOLD_KEY = "html_assertion_error_threshold"; //$NON-NLS-1$
 
 	public static final String WARNING_THRESHOLD_KEY = "html_assertion_warning_threshold"; //$NON-NLS-1$
 
 	public static final String FORMAT_KEY = "html_assertion_format"; //$NON-NLS-1$
 
 	public static final String FILENAME_KEY = "html_assertion_filename"; //$NON-NLS-1$
 
 	/**
 	 * 
 	 */
 	public HTMLAssertion() {
 		log.debug("HTMLAssertion(): called");
 	}
 
 	/**
 	 * Returns the result of the Assertion. If so an AssertionResult containing
 	 * a FailureMessage will be returned. Otherwise the returned AssertionResult
 	 * will reflect the success of the Sample.
 	 */
 	public AssertionResult getResult(SampleResult inResponse) {
 		log.debug("HTMLAssertions.getResult() called");
 
 		// no error as default
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 
 		if (inResponse.getResponseData().length == 0) {
 			return result.setResultForNull();
 		}
 
 		result.setFailure(false);
 
 		// create parser
 		Tidy tidy = null;
 		try {
 			log.debug("HTMLAssertions.getResult(): Setup tidy ...");
 			log.debug("doctype: " + getDoctype());
 			log.debug("errors only: " + isErrorsOnly());
 			log.debug("error threshold: " + getErrorThreshold());
 			log.debug("warning threshold: " + getWarningThreshold());
 			log.debug("html mode: " + isHTML());
 			log.debug("xhtml mode: " + isXHTML());
 			log.debug("xml mode: " + isXML());
 			tidy = new Tidy();
 			tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
 			tidy.setQuiet(false);
 			tidy.setShowWarnings(true);
 			tidy.setOnlyErrors(isErrorsOnly());
 			tidy.setDocType(getDoctype());
 			if (isXHTML()) {
 				tidy.setXHTML(true);
 			} else if (isXML()) {
 				tidy.setXmlTags(true);
 			}
 			log.debug("err file: " + getFilename());
 			tidy.setErrfile(getFilename());
 
 			if (log.isDebugEnabled()) {
 				log.debug("getParser : tidy parser created - " + tidy);
 			}
 			log.debug("HTMLAssertions.getResult(): Tidy instance created!");
 
 		} catch (Exception e) {//TODO replace with proper Exception
 			log.error("Unable to instantiate tidy parser", e);
 			result.setFailure(true);
 			result.setFailureMessage("Unable to instantiate tidy parser");
 			// return with an error
 			return result;
 		}
 
 		/*
 		 * Run tidy.
 		 */
 		try {
 			log.debug("HTMLAssertions.getResult(): start parsing with tidy ...");
 
 			StringWriter errbuf = new StringWriter();
 			tidy.setErrout(new PrintWriter(errbuf));
 			// Node node = tidy.parseDOM(new
 			// ByteArrayInputStream(response.getResponseData()), null);
 			ByteArrayOutputStream os = new ByteArrayOutputStream();
 			log.debug("Start : parse");
 			Node node = tidy.parse(new ByteArrayInputStream(inResponse.getResponseData()), os);
 			if (log.isDebugEnabled()) {
 				log.debug("node : " + node);
 			}
 			log.debug("End   : parse");
 			log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
 			log.debug("Output: " + os.toString());
 
 			// write output to file
 			writeOutput(errbuf.toString());
 
 			// evaluate result
 			if ((tidy.getParseErrors() > getErrorThreshold())
 					|| (!isErrorsOnly() && (tidy.getParseWarnings() > getWarningThreshold()))) {
 				log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
 				log.debug(errbuf.toString());
 				result.setFailure(true);
 				result.setFailureMessage(MessageFormat.format("Tidy Parser errors:   " + tidy.getParseErrors()
 						+ " (allowed " + getErrorThreshold() + ") " + "Tidy Parser warnings: "
 						+ tidy.getParseWarnings() + " (allowed " + getWarningThreshold() + ")", new Object[0]));
 				// return with an error
 
 			} else if ((tidy.getParseErrors() > 0) || (tidy.getParseWarnings() > 0)) {
 				// return with no error
 				log.debug("HTMLAssertions.getResult(): there were errors/warnings but threshold to high");
 				result.setFailure(false);
 			} else {
 				// return with no error
 				log.debug("HTMLAssertions.getResult(): no errors/warnings detected:");
 				result.setFailure(false);
 			}
 
 		} catch (Exception e) {//TODO replace with proper Exception
 			// return with an error
 			log.warn("Cannot parse result content", e);
 			result.setFailure(true);
 			result.setFailureMessage(e.getMessage());
 		}
 		return result;
 	}
 
 	/**
 	 * Writes the output of tidy to file.
 	 * 
 	 * @param inOutput
 	 */
 	private void writeOutput(String inOutput) {
 		String lFilename = getFilename();
 
 		// check if filename defined
 		if ((lFilename != null) && (!"".equals(lFilename.trim()))) {
 			FileWriter lOutputWriter = null;
 			try {
 
 				// open file
 				lOutputWriter = new FileWriter(lFilename, false);
 
 				// write to file
 				lOutputWriter.write(inOutput);
 
 				// flush
 				lOutputWriter.flush();
 
 				log.debug("writeOutput() -> output successfully written to file " + lFilename);
 
 			} catch (IOException ex) {
 				log.warn("writeOutput() -> could not write output to file " + lFilename, ex);
 			} finally {
 				// close file
 				if (lOutputWriter != null) {
 					try {
 						lOutputWriter.close();
 					} catch (IOException e) {
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Gets the doctype
 	 * 
 	 * @return the documemt type
 	 */
 	public String getDoctype() {
 		return getPropertyAsString(DOCTYPE_KEY);
 	}
 
 	/**
 	 * Check if errors will be reported only
 	 * 
 	 * @return boolean - report errors only?
 	 */
 	public boolean isErrorsOnly() {
 		return getPropertyAsBoolean(ERRORS_ONLY_KEY);
 	}
 
 	/**
 	 * Gets the threshold setting for errors
 	 * 
 	 * @return long error threshold
 	 */
 	public long getErrorThreshold() {
 		return getPropertyAsLong(ERROR_THRESHOLD_KEY);
 	}
 
 	/**
 	 * Gets the threshold setting for warnings
 	 * 
 	 * @return long warning threshold
 	 */
 	public long getWarningThreshold() {
 		return getPropertyAsLong(WARNING_THRESHOLD_KEY);
 	}
 
 	/**
 	 * Sets the doctype setting
 	 * 
 	 * @param inDoctype
 	 */
 	public void setDoctype(String inDoctype) {
 		if ((inDoctype == null) || (inDoctype.trim().equals(""))) {
 			setProperty(new StringProperty(DOCTYPE_KEY, DEFAULT_DOCTYPE));
 		} else {
 			setProperty(new StringProperty(DOCTYPE_KEY, inDoctype));
 		}
 	}
 
 	/**
 	 * Sets if errors shoud be tracked only
 	 * 
 	 * @param inErrorsOnly
 	 */
 	public void setErrorsOnly(boolean inErrorsOnly) {
 		setProperty(new BooleanProperty(ERRORS_ONLY_KEY, inErrorsOnly));
 	}
 
 	/**
 	 * Sets the threshold on error level
 	 * 
 	 * @param inErrorThreshold
 	 */
 	public void setErrorThreshold(long inErrorThreshold) {
 		if (inErrorThreshold < 0L) {
 			throw new IllegalArgumentException(JMeterUtils.getResString("argument_must_not_be_negative")); //$NON-NLS-1$
 		}
 		if (inErrorThreshold == Long.MAX_VALUE) {
 			setProperty(new LongProperty(ERROR_THRESHOLD_KEY, 0));
 		} else {
 			setProperty(new LongProperty(ERROR_THRESHOLD_KEY, inErrorThreshold));
 		}
 	}
 
 	/**
 	 * Sets the threshold on warning level
 	 * 
 	 * @param inWarningThreshold
 	 */
 	public void setWarningThreshold(long inWarningThreshold) {
 		if (inWarningThreshold < 0L) {
 			throw new IllegalArgumentException(JMeterUtils.getResString("argument_must_not_be_negative")); //$NON-NLS-1$
 		}
 		if (inWarningThreshold == Long.MAX_VALUE) {
 			setProperty(new LongProperty(WARNING_THRESHOLD_KEY, 0));
 		} else {
 			setProperty(new LongProperty(WARNING_THRESHOLD_KEY, inWarningThreshold));
 		}
 	}
 
 	/**
 	 * Enables html validation mode
 	 */
 	public void setHTML() {
 		setProperty(new LongProperty(FORMAT_KEY, 0));
 	}
 
 	/**
 	 * Check if html validation mode is set
 	 * 
 	 * @return boolean
 	 */
 	public boolean isHTML() {
 		return getPropertyAsLong(FORMAT_KEY) == 0;
 	}
 
 	/**
 	 * Enables xhtml validation mode
 	 */
 	public void setXHTML() {
 		setProperty(new LongProperty(FORMAT_KEY, 1));
 	}
 
 	/**
 	 * Check if xhtml validation mode is set
 	 * 
 	 * @return boolean
 	 */
 	public boolean isXHTML() {
 		return getPropertyAsLong(FORMAT_KEY) == 1;
 	}
 
 	/**
 	 * Enables xml validation mode
 	 */
 	public void setXML() {
 		setProperty(new LongProperty(FORMAT_KEY, 2));
 	}
 
 	/**
 	 * Check if xml validation mode is set
 	 * 
 	 * @return boolean
 	 */
 	public boolean isXML() {
 		return getPropertyAsLong(FORMAT_KEY) == 2;
 	}
 
 	/**
 	 * Sets the name of the file where tidy writes the output to
 	 * 
 	 * @return name of file
 	 */
 	public String getFilename() {
 		return getPropertyAsString(FILENAME_KEY);
 	}
 
 	/**
 	 * Sets the name of the tidy output file
 	 * 
 	 * @param inName
 	 */
 	public void setFilename(String inName) {
 		setProperty(FILENAME_KEY, inName);
 	}
 }
diff --git a/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java b/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java
index 43c747a32..353aa867e 100644
--- a/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/MD5HexAssertion.java
@@ -1,122 +1,122 @@
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
 
 /**
  * MD5HexAssertion class creates an MD5 checksum from the response <br/>
  * and matches it with the MD5 hex provided.
  * The assertion will fail when the expected hex is different from the <br/>
  * one calculated from the response OR when the expected hex is left empty.
  * 
  * @author	<a href="mailto:jh@domek.be">Jorg Heymans</a>
  * @version $Revision$ last updated $Date$
  */
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 public class MD5HexAssertion extends AbstractTestElement implements Serializable, Assertion {
 
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	/** Key for storing assertion-informations in the jmx-file. */
 	private static final String MD5HEX_KEY = "MD5HexAssertion.size";
 
 	/*
 	 * @param response @return
 	 */
 	public AssertionResult getResult(SampleResult response) {
 
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		result.setFailure(false);
 		byte[] resultData = response.getResponseData();
 
 		if (resultData.length == 0) {
 			result.setError(false);
 			result.setFailure(true);
 			result.setFailureMessage("Response was null");
 			return result;
 		}
 
 		// no point in checking if we don't have anything to compare against
 		if (getAllowedMD5Hex().equals("")) {
 			result.setError(false);
 			result.setFailure(true);
 			result.setFailureMessage("MD5Hex to test against is empty");
 			return result;
 		}
 
 		String md5Result = baMD5Hex(resultData);
 
 		// String md5Result = DigestUtils.md5Hex(resultData);
 
 		if (!md5Result.equalsIgnoreCase(getAllowedMD5Hex())) {
 			result.setFailure(true);
 
 			Object[] arguments = { md5Result, getAllowedMD5Hex() };
 			String message = MessageFormat.format(JMeterUtils.getResString("md5hex_assertion_failure"), arguments);
 			result.setFailureMessage(message);
 
 		}
 
 		return result;
 	}
 
 	public void setAllowedMD5Hex(String hex) {
 		setProperty(new StringProperty(MD5HexAssertion.MD5HEX_KEY, hex));
 	}
 
 	public String getAllowedMD5Hex() {
 		return getPropertyAsString(MD5HexAssertion.MD5HEX_KEY);
 	}
 
 	// package protected so can be accessed by test class
 	static String baToHex(byte ba[]) {
 		StringBuffer sb = new StringBuffer(32);
 		for (int i = 0; i < ba.length; i++) {
 			int j = ba[i] & 0xff;
 			if (j < 16)
 				sb.append("0");
 			sb.append(Integer.toHexString(j));
 		}
 		return sb.toString();
 	}
 
 	// package protected so can be accessed by test class
 	static String baMD5Hex(byte ba[]) {
 		byte[] md5Result = {};
 
 		try {
 			MessageDigest md;
 			md = MessageDigest.getInstance("MD5");
 			md5Result = md.digest(ba);
 		} catch (NoSuchAlgorithmException e) {
 			log.error("", e);
 		}
 		return baToHex(md5Result);
 	}
 }
diff --git a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
index d850f81e7..ce232320f 100644
--- a/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/ResponseAssertion.java
@@ -1,477 +1,477 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 import org.apache.oro.text.MalformedCachePatternException;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 
 // @see org.apache.jmeter.assertions.PackageTest for unit tests
 
 /**
  * 
  * @author Michael Stover
  * @author <a href="mailto:jacarlco@katun.com">Jonathan Carlson</a>
  */
 public class ResponseAssertion extends AbstractTestElement implements Serializable, Assertion {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private final static String TEST_FIELD = "Assertion.test_field";  // $NON-NLS-1$
 
 	// Values for TEST_FIELD
 	// N.B. we cannot change the text value as it is in test plans
 	private final static String SAMPLE_URL = "Assertion.sample_label"; // $NON-NLS-1$
 
 	private final static String RESPONSE_DATA = "Assertion.response_data"; // $NON-NLS-1$
 
 	private final static String RESPONSE_CODE = "Assertion.response_code"; // $NON-NLS-1$
 
 	private final static String RESPONSE_MESSAGE = "Assertion.response_message"; // $NON-NLS-1$
 
 	private final static String ASSUME_SUCCESS = "Assertion.assume_success"; // $NON-NLS-1$
 
 	private final static String TEST_STRINGS = "Asserion.test_strings"; // $NON-NLS-1$
 
 	private final static String TEST_TYPE = "Assertion.test_type"; // $NON-NLS-1$
 
 	/*
 	 * Mask values for TEST_TYPE TODO: remove either MATCH or CONTAINS - they
 	 * are mutually exckusive
 	 */
 	private final static int MATCH = 1 << 0;
 
 	final static int CONTAINS = 1 << 1;
 
 	private final static int NOT = 1 << 2;
 
 	private final static int EQUALS = 1 << 3;
 
     private static final int  EQUALS_SECTION_DIFF_LEN
             = JMeterUtils.getPropDefault("assertion.equals_section_diff_len", 100);
 
     /** Signifies truncated text in diff display. */
     private static final String EQUALS_DIFF_TRUNC = "...";
 
     private static final String RECEIVED_STR = "****** received  : ";
     private static final String COMPARISON_STR = "****** comparison: ";
     private static final String DIFF_DELTA_START
             = JMeterUtils.getPropDefault("assertion.equals_diff_delta_start", "[[[");
     private static final String DIFF_DELTA_END
             = JMeterUtils.getPropDefault("assertion.equals_diff_delta_end", "]]]");
 
 	public ResponseAssertion() {
 		setProperty(new CollectionProperty(TEST_STRINGS, new ArrayList()));
 	}
 
 	public void clear() {
 		super.clear();
 		setProperty(new CollectionProperty(TEST_STRINGS, new ArrayList()));
 	}
 
 	private void setTestField(String testField) {
 		setProperty(TEST_FIELD, testField);
 	}
 
 	public void setTestFieldURL(){
 		setTestField(SAMPLE_URL);
 	}
 
 	public void setTestFieldResponseCode(){
 		setTestField(RESPONSE_CODE);
 	}
 
 	public void setTestFieldResponseData(){
 		setTestField(RESPONSE_DATA);
 	}
 
 	public void setTestFieldResponseMessage(){
 		setTestField(RESPONSE_MESSAGE);
 	}
 
 	public boolean isTestFieldURL(){
 		return SAMPLE_URL.equals(getTestField());
 	}
 
 	public boolean isTestFieldResponseCode(){
 		return RESPONSE_CODE.equals(getTestField());
 	}
 
 	public boolean isTestFieldResponseData(){
 		return RESPONSE_DATA.equals(getTestField());
 	}
 
 	public boolean isTestFieldResponseMessage(){
 		return RESPONSE_MESSAGE.equals(getTestField());
 	}
 
 	private void setTestType(int testType) {
 		setProperty(new IntegerProperty(TEST_TYPE, testType));
 	}
 
 	public void addTestString(String testString) {
 		getTestStrings().addProperty(new StringProperty(String.valueOf(testString.hashCode()), testString));
 	}
 
 	public void clearTestStrings() {
 		getTestStrings().clear();
 	}
 
 	public AssertionResult getResult(SampleResult response) {
 		AssertionResult result;
 
 		// None of the other Assertions check the response status, so remove
 		// this check
 		// for the time being, at least...
 		// if (!response.isSuccessful())
 		// {
 		// result = new AssertionResult();
 		// result.setError(true);
 		// byte [] ba = response.getResponseData();
 		// result.setFailureMessage(
 		// ba == null ? "Unknown Error (responseData is empty)" : new String(ba)
 		// );
 		// return result;
 		// }
 
 		result = evaluateResponse(response);
 		return result;
 	}
 
 	/***************************************************************************
 	 * !ToDoo (Method description)
 	 * 
 	 * @return !ToDo (Return description)
 	 **************************************************************************/
 	public String getTestField() {
 		return getPropertyAsString(TEST_FIELD);
 	}
 
 	/***************************************************************************
 	 * !ToDoo (Method description)
 	 * 
 	 * @return !ToDo (Return description)
 	 **************************************************************************/
 	public int getTestType() {
 		JMeterProperty type = getProperty(TEST_TYPE);
 		if (type instanceof NullProperty) {
 			return CONTAINS;
 		}
 		return type.getIntValue();
 	}
 
 	/***************************************************************************
 	 * !ToDoo (Method description)
 	 * 
 	 * @return !ToDo (Return description)
 	 **************************************************************************/
 	public CollectionProperty getTestStrings() {
 		return (CollectionProperty) getProperty(TEST_STRINGS);
 	}
 
 	public boolean isEqualsType() {
 		return (getTestType() & EQUALS) > 0;
 	}
 
 	public boolean isContainsType() {
 		return (getTestType() & CONTAINS) > 0;
 	}
 
 	public boolean isMatchType() {
 		return (getTestType() & MATCH) > 0;
 	}
 
 	public boolean isNotType() {
 		return (getTestType() & NOT) > 0;
 	}
 
 	public void setToContainsType() {
 		setTestType((getTestType() | CONTAINS) & ~(MATCH | EQUALS));
 	}
 
 	public void setToMatchType() {
 		setTestType((getTestType() | MATCH) & ~(CONTAINS | EQUALS));
 	}
 
 	public void setToEqualsType() {
 		setTestType((getTestType() | EQUALS) & ~(MATCH | CONTAINS));
 	}
 
 	public void setToNotType() {
 		setTestType((getTestType() | NOT));
 	}
 
 	public void unsetNotType() {
 		setTestType(getTestType() & ~NOT);
 	}
 
 	public boolean getAssumeSuccess() {
 		return getPropertyAsBoolean(ASSUME_SUCCESS, false);
 	}
 
 	public void setAssumeSuccess(boolean b) {
 		setProperty(ASSUME_SUCCESS, JOrphanUtils.booleanToString(b));
 	}
 
 	/**
 	 * Make sure the response satisfies the specified assertion requirements.
 	 * 
 	 * @param response
 	 *            an instance of SampleResult
 	 * @return an instance of AssertionResult
 	 */
 	AssertionResult evaluateResponse(SampleResult response) {
 		boolean pass = true;
 		boolean not = (NOT & getTestType()) > 0;
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		String toCheck = ""; // The string to check (Url or data)
 
 		if (getAssumeSuccess()) {
 			response.setSuccessful(true);// Allow testing of failure codes
 		}
 
 		// What are we testing against?
 		if (isTestFieldResponseData()) {
 			// TODO treat header separately from response? (would not apply to
 			// all samplers)
 			String data = response.getResponseDataAsString(); // (bug25052)
 			toCheck = new StringBuffer(response.getResponseHeaders()).append(data).toString();
 		} else if (isTestFieldResponseCode()) {
 			toCheck = response.getResponseCode();
 		} else if (isTestFieldResponseMessage()) {
 			toCheck = response.getResponseMessage();
 		} else { // Assume it is the URL
 			toCheck = response.getSamplerData(); // TODO - is this where the URL is stored?
 			if (toCheck == null)
 				toCheck = "";
 		}
 
 		if (toCheck.length() == 0) {
 			return result.setResultForNull();
 		}
 
 		result.setFailure(false);
 		result.setError(false);
 
 		boolean contains = isContainsType(); // do it once outside loop
 		boolean equals = isEqualsType();
 		boolean debugEnabled = log.isDebugEnabled();
 		if (debugEnabled){
 			log.debug("Type:" + (contains?"Contains":"Match") + (not? "(not)": ""));
 		}
 		
 		try {
 			// Get the Matcher for this thread
 			Perl5Matcher localMatcher = JMeterUtils.getMatcher();
 			PropertyIterator iter = getTestStrings().iterator();
 			while (iter.hasNext()) {
 				String stringPattern = iter.next().getStringValue();
 				Pattern pattern = JMeterUtils.getPatternCache().getPattern(stringPattern, Perl5Compiler.READ_ONLY_MASK);
 				boolean found;
 				if (contains) {
 					found = localMatcher.contains(toCheck, pattern);
                 } else if (equals) {
                     found = toCheck.equals(stringPattern);
 				} else {
 					found = localMatcher.matches(toCheck, pattern);
 				}
 				pass = not ? !found : found;
 				if (!pass) {
 					if (debugEnabled){log.debug("Failed: "+pattern);}
 					result.setFailure(true);
 					result.setFailureMessage(getFailText(stringPattern,toCheck));
 					break;
 				}
 				if (debugEnabled){log.debug("Passed: "+pattern);}
 			}
 		} catch (MalformedCachePatternException e) {
 			result.setError(true);
 			result.setFailure(false);
 			result.setFailureMessage("Bad test configuration " + e);
 		}
 		return result;
 	}
 
 	/**
 	 * Generate the failure reason from the TestType
 	 * 
 	 * @param stringPattern
 	 * @return the message for the assertion report 
 	 */
 	// TODO strings should be resources
 	private String getFailText(String stringPattern, String toCheck) {
 		
 		StringBuffer sb = new StringBuffer(200);
 		sb.append("Test failed, ");
 
 		if (ResponseAssertion.RESPONSE_DATA.equals(getTestField())) {
 			sb.append("text");
 		} else if (ResponseAssertion.RESPONSE_CODE.equals(getTestField())) {
 			sb.append("code");
 		} else if (ResponseAssertion.RESPONSE_MESSAGE.equals(getTestField())) {
 			sb.append("message");
 		} else // Assume it is the URL
 		{
 			sb.append("URL");
 		}
 
 		switch (getTestType()) {
 		case CONTAINS:
 			sb.append(" expected to contain ");
 			break;
 		case NOT | CONTAINS:
 			sb.append(" expected not to contain ");
 			break;
 		case MATCH:
 			sb.append(" expected to match ");
 			break;
 		case NOT | MATCH:
 			sb.append(" expected not to match ");
 			break;
 		case EQUALS:
 			sb.append(" expected to equal ");
 			break;
 		case NOT | EQUALS:
 			sb.append(" expected not to equal ");
 			break;
 		default:// should never happen...
 			sb.append(" expected something using ");
 		}
 
 		sb.append("/");
 		
 		if (isEqualsType()){
 			sb.append(equalsComparisonText(toCheck, stringPattern));
 		} else {
 			sb.append(stringPattern);
 		}
 		
 		sb.append("/");
 		
 		return sb.toString();
 	}
 
 
     private static String trunc(final boolean right, final String str)
     {
         if (str.length() <= EQUALS_SECTION_DIFF_LEN)
             return str;
         else if (right)
             return str.substring(0, EQUALS_SECTION_DIFF_LEN) + EQUALS_DIFF_TRUNC;
         else
             return EQUALS_DIFF_TRUNC + str.substring(str.length() - EQUALS_SECTION_DIFF_LEN, str.length());
     }
 
     /**
      *   Returns some helpful logging text to determine where equality between two strings
      * is broken, with one pointer working from the front of the strings and another working
      * backwards from the end.
      *
      * @param received      String received from sampler.
      * @param comparison    String specified for "equals" response assertion.
      * @return  Two lines of text separated by newlines, and then forward and backward pointers
      *      denoting first position of difference.
      */
     private static StringBuffer equalsComparisonText(final String received, final String comparison)
     {
         final StringBuffer      text;
         int                     firstDiff;
         int                     lastRecDiff = -1;
         int                     lastCompDiff = -1;
         final int               recLength = received.length();
         final int               compLength = comparison.length();
         final int               minLength = Math.min(recLength, compLength);
         final String            startingEqSeq;
         String                  recDeltaSeq = "";
         String                  compDeltaSeq = "";
         String                  endingEqSeq = "";
         final StringBuffer      pad;
 
 
         text = new StringBuffer(Math.max(recLength, compLength) * 2);
         for (firstDiff = 0; firstDiff < minLength; firstDiff++)
             if (received.charAt(firstDiff) != comparison.charAt(firstDiff))
                 break;
         if (firstDiff == 0)
             startingEqSeq = "";
         else
             startingEqSeq = trunc(false, received.substring(0, firstDiff));
 
         lastRecDiff = recLength - 1;
         lastCompDiff = compLength - 1;
 
         while ((lastRecDiff > firstDiff) && (lastCompDiff > firstDiff)
                 && received.charAt(lastRecDiff) == comparison.charAt(lastCompDiff))
         {
             lastRecDiff--;
             lastCompDiff--;
         }
         endingEqSeq = trunc(true, received.substring(lastRecDiff + 1, recLength));
         if (endingEqSeq.length() == 0)
         {
             recDeltaSeq = trunc(true, received.substring(firstDiff, recLength));
             compDeltaSeq = trunc(true, comparison.substring(firstDiff, compLength));
         }
         else
         {
             recDeltaSeq = trunc(true, received.substring(firstDiff, lastRecDiff + 1));
             compDeltaSeq = trunc(true, comparison.substring(firstDiff, lastCompDiff + 1));
         }
         pad = new StringBuffer(Math.abs(recDeltaSeq.length() - compDeltaSeq.length()));
         for (int i = 0; i < pad.capacity(); i++)
             pad.append(' ');
         if (recDeltaSeq.length() > compDeltaSeq.length())
             compDeltaSeq += pad.toString();
         else
             recDeltaSeq += pad.toString();
 
         text.append("\n\n");
         text.append(RECEIVED_STR);
         text.append(startingEqSeq);
         text.append(DIFF_DELTA_START);
         text.append(recDeltaSeq);
         text.append(DIFF_DELTA_END);
         text.append(endingEqSeq);
         text.append("\n\n");
         text.append(COMPARISON_STR);
         text.append(startingEqSeq);
         text.append(DIFF_DELTA_START);
         text.append(compDeltaSeq);
         text.append(DIFF_DELTA_END);
         text.append(endingEqSeq);
         text.append("\n\n");
         return text;
     }
 
 }
diff --git a/src/components/org/apache/jmeter/assertions/SizeAssertion.java b/src/components/org/apache/jmeter/assertions/SizeAssertion.java
index 861f5af4d..4bd7b80a2 100644
--- a/src/components/org/apache/jmeter/assertions/SizeAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/SizeAssertion.java
@@ -1,170 +1,170 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 import java.text.MessageFormat;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.LongProperty;
 import org.apache.jmeter.util.JMeterUtils;
 
 /**
  * Checks if the results of a Sample matches a particular size.
  * 
  * @author <a href="mailto:wolfram.rittmeyer@web.de">Wolfram Rittmeyer</a>
  * @version $Revision$, $Date$
  */
 public class SizeAssertion extends AbstractTestElement implements Serializable, Assertion {
 
 	private String comparatorErrorMessage = "ERROR!";
 
 	// * Static int to signify the type of logical comparitor to assert
 	public final static int EQUAL = 1;
 
 	public final static int NOTEQUAL = 2;
 
 	public final static int GREATERTHAN = 3;
 
 	public final static int LESSTHAN = 4;
 
 	public final static int GREATERTHANEQUAL = 5;
 
 	public final static int LESSTHANEQUAL = 6;
 
 	/** Key for storing assertion-informations in the jmx-file. */
 	private static final String SIZE_KEY = "SizeAssertion.size";
 
 	private static final String OPERATOR_KEY = "SizeAssertion.operator";
 
 	byte[] resultData;
 
 	/**
 	 * Returns the result of the Assertion. Here it checks wether the Sample
 	 * took to long to be considered successful. If so an AssertionResult
 	 * containing a FailureMessage will be returned. Otherwise the returned
 	 * AssertionResult will reflect the success of the Sample.
 	 */
 	public AssertionResult getResult(SampleResult response) {
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		result.setFailure(false);
 		resultData = response.getResponseData();
 		long resultSize = resultData.length;
 		if (resultSize==0) {
 			return result.setResultForNull();
 		}
 		// is the Sample the correct size?
 		if ((!(compareSize(resultSize)) && (getAllowedSize() > 0))) {
 			result.setFailure(true);
 			Object[] arguments = { new Long(resultSize), comparatorErrorMessage, new Long(getAllowedSize()) };
 			String message = MessageFormat.format(JMeterUtils.getResString("size_assertion_failure"), arguments);
 			result.setFailureMessage(message);
 		}
 		return result;
 	}
 
 	/**
 	 * Returns the size in bytes to be asserted. A duration of 0 indicates this
 	 * assertion is to be ignored.
 	 */
 	public long getAllowedSize() {
 		return getPropertyAsLong(SIZE_KEY);
 	}
 
 	/***************************************************************************
 	 * set the Operator
 	 **************************************************************************/
 	public void setCompOper(int operator) {
 		setProperty(new IntegerProperty(OPERATOR_KEY, operator));
 
 	}
 
 	/**
 	 * Returns the operator to be asserted. EQUAL = 1, NOTEQUAL = 2 GREATERTHAN =
 	 * 3,LESSTHAN = 4,GREATERTHANEQUAL = 5,LESSTHANEQUAL = 6
 	 */
 
 	public int getCompOper() {
 		return getPropertyAsInt(OPERATOR_KEY);
 	}
 
 	/**
 	 * Set the size that shall be asserted.
 	 * 
 	 * @param size -
 	 *            a number of bytes. Is not allowed to be negative. Use
 	 *            Long.MAX_VALUE to indicate illegal or empty inputs. This will
 	 *            result in not checking the assertion.
 	 * 
 	 * @throws IllegalArgumentException
 	 *             If <code>size</code> is negative.
 	 */
 	public void setAllowedSize(long size) throws IllegalArgumentException {
 		if (size < 0L) {
 			throw new IllegalArgumentException(JMeterUtils.getResString("argument_must_not_be_negative"));
 		}
 		if (size == Long.MAX_VALUE) {
 			setProperty(new LongProperty(SIZE_KEY, 0));
 		} else {
 			setProperty(new LongProperty(SIZE_KEY, size));
 		}
 	}
 
 	/**
 	 * Compares the the size of a return result to the set allowed size using a
 	 * logical comparator set in setLogicalComparator().
 	 * 
 	 * Possible values are: equal, not equal, greater than, less than, greater
 	 * than eqaul, less than equal, .
 	 * 
 	 */
 	private boolean compareSize(long resultSize) {
 		boolean result = false;
 		int comp = getCompOper();
 		switch (comp) {
 		case EQUAL:
 			result = (resultSize == getAllowedSize());
 			comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_equal");
 			break;
 		case NOTEQUAL:
 			result = (resultSize != getAllowedSize());
 			comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_notequal");
 			break;
 		case GREATERTHAN:
 			result = (resultSize > getAllowedSize());
 			comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_greater");
 			break;
 		case LESSTHAN:
 			result = (resultSize < getAllowedSize());
 			comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_less");
 			break;
 		case GREATERTHANEQUAL:
 			result = (resultSize >= getAllowedSize());
 			comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_greaterequal");
 			break;
 		case LESSTHANEQUAL:
 			result = (resultSize <= getAllowedSize());
 			comparatorErrorMessage = JMeterUtils.getResString("size_assertion_comparator_error_lessequal");
 			break;
 		}
 		return result;
 	}
 }
diff --git a/src/components/org/apache/jmeter/assertions/XMLAssertion.java b/src/components/org/apache/jmeter/assertions/XMLAssertion.java
index d65e1cecb..c4a23c8f2 100644
--- a/src/components/org/apache/jmeter/assertions/XMLAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XMLAssertion.java
@@ -1,108 +1,108 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.io.StringReader;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.jdom.JDOMException;
 import org.jdom.input.SAXBuilder;
 
 /**
  * Checks if the result is a well-formed XML content using jdom
  * 
  * @author <a href="mailto:gottfried@szing.at">Gottfried Szing</a>
  * @version $Revision$, $Date$
  */
 public class XMLAssertion extends AbstractTestElement implements Serializable, Assertion {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	// one builder for all requests in a thread
     private static ThreadLocal myBuilder = new ThreadLocal() {
         protected Object initialValue() {
             return new SAXBuilder();
         }
     };
 
 	/**
 	 * Returns the result of the Assertion. Here it checks wether the Sample
 	 * took to long to be considered successful. If so an AssertionResult
 	 * containing a FailureMessage will be returned. Otherwise the returned
 	 * AssertionResult will reflect the success of the Sample.
 	 */
 	public AssertionResult getResult(SampleResult response) {
 		// no error as default
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		byte[] responseData = response.getResponseData();
 		if (responseData.length == 0) {
 			return result.setResultForNull();
 		}
 		result.setFailure(false);
 
 		// the result data
 		String resultData = new String(getResultBody(responseData));
 
         SAXBuilder builder = (SAXBuilder) myBuilder.get();
 
 		try {
 			builder.build(new StringReader(resultData));
 		} catch (JDOMException e) {
 			log.debug("Cannot parse result content", e); // may well happen
 			result.setFailure(true);
 			result.setFailureMessage(e.getMessage());
 		} catch (IOException e) {
             log.error("Cannot read result content", e); // should never happen
             result.setError(true);
             result.setFailureMessage(e.getMessage());
         }
 
 		return result;
 	}
 
 	/**
 	 * Return the body of the http return.
 	 */
 	private byte[] getResultBody(byte[] resultData) {
 		for (int i = 0; i < (resultData.length - 1); i++) {
 			if (resultData[i] == '\n' && resultData[i + 1] == '\n') {
 				return getByteArraySlice(resultData, (i + 2), resultData.length - 1);
 			}
 		}
 		return resultData;
 	}
 
 	/**
 	 * Return a slice of a byte array
 	 */
 	private byte[] getByteArraySlice(byte[] array, int begin, int end) {
 		byte[] slice = new byte[(end - begin + 1)];
 		int count = 0;
 		for (int i = begin; i <= end; i++) {
 			slice[count] = array[i];
 			count++;
 		}
 
 		return slice;
 	}
 }
diff --git a/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java b/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
index 025c9d8c8..df55efe1e 100644
--- a/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
@@ -1,224 +1,224 @@
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
 package org.apache.jmeter.assertions;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.io.StringReader;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 // import org.w3c.dom.Document;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 // See Bug 34383
 
 /**
  * XMLSchemaAssertion.java Validate response against an XML Schema author <a
  * href="mailto:d.maung@mdl.com">Dave Maung</a>
  * 
  */
 public class XMLSchemaAssertion extends AbstractTestElement implements Serializable, Assertion {
 	public static final String FILE_NAME_IS_REQUIRED = "FileName is required";
 
 	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
 
 	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
 
 	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
 
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	public static final String XSD_FILENAME_KEY = "xmlschema_assertion_filename";
 
 	// private StringBuffer failureMessage = new StringBuffer();
 
 	/**
 	 * getResult
 	 * 
 	 */
 	public AssertionResult getResult(SampleResult response) {
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		// Note: initialised with error = failure = false
 
 		byte data[] = response.getResponseData();
 		if (data.length == 0) {
 			return result.setResultForNull();
 		}
 		String resultData = new String(getResultBody(data));
 
 		String xsdFileName = getXsdFileName();
 		if (log.isDebugEnabled()) {
 			log.debug("xmlString: " + resultData);
 			log.debug("xsdFileName: " + xsdFileName);
 		}
 		if (xsdFileName == null || xsdFileName.length() == 0) {
 			result.setResultForFailure(FILE_NAME_IS_REQUIRED);
 		} else {
 			setSchemaResult(result, resultData, xsdFileName);
 		}
 		return result;
 	}
 
 	/*
 	 * TODO move to SampleResult class? Return the body of the http return.
 	 */
 	private byte[] getResultBody(byte[] resultData) {
 		for (int i = 0; i < (resultData.length - 1); i++) {
 			if (resultData[i] == '\n' && resultData[i + 1] == '\n') {
 				return JOrphanUtils.getByteArraySlice(resultData, (i + 2), resultData.length - 1);
 			}
 		}
 		return resultData;
 	}
 
 	public void setXsdFileName(String xmlSchemaFileName) throws IllegalArgumentException {
 		setProperty(XSD_FILENAME_KEY, xmlSchemaFileName);
 	}
 
 	public String getXsdFileName() {
 		return getPropertyAsString(XSD_FILENAME_KEY);
 	}
 
 	/**
 	 * set Schema result
 	 * 
 	 * @param result
 	 * @param xmlStr
 	 * @param xsdFileName
 	 */
 	private void setSchemaResult(AssertionResult result, String xmlStr, String xsdFileName) {
 		try {
 			// boolean toReturn = true;
 
 			// Document doc = null;
 			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
 			parserFactory.setValidating(true);
 			parserFactory.setNamespaceAware(true);
 			parserFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
 			parserFactory.setAttribute(JAXP_SCHEMA_SOURCE, xsdFileName);
 
 			// create a parser:
 			DocumentBuilder parser = parserFactory.newDocumentBuilder();
 			parser.setErrorHandler(new SAXErrorHandler(result));
 
 			// doc =
 			parser.parse(new InputSource(new StringReader(xmlStr)));
 			// if everything went fine then xml schema validation is valid
 		} catch (SAXParseException e) {
 
 			// Only set message if error not yet flagged
 			if (!result.isError() && !result.isFailure()) {
 				result.setError(true);
 				result.setFailureMessage(errorDetails(e));
 			}
 
 		} catch (SAXException e) {
 
 			log.warn(e.toString());
 			result.setResultForFailure(e.getMessage());
 
 		} catch (IOException e) {
 
 			log.warn("IO error", e);
 			result.setResultForFailure(e.getMessage());
 
 		} catch (ParserConfigurationException e) {
 
 			log.warn("Problem with Parser Config", e);
 			result.setResultForFailure(e.getMessage());
 
 		}
 
 	}
 
 	// Helper method to construct SAX error details
 	private static String errorDetails(SAXParseException spe) {
 		StringBuffer str = new StringBuffer(80);
 		int i;
 		i = spe.getLineNumber();
 		if (i != -1) {
 			str.append("line=");
 			str.append(i);
 			str.append(" col=");
 			str.append(spe.getColumnNumber());
 			str.append(" ");
 		}
 		str.append(spe.getLocalizedMessage());
 		return str.toString();
 	}
 
 	/**
 	 * SAXErrorHandler class
 	 */
 	private static class SAXErrorHandler implements ErrorHandler {
 		private AssertionResult result;
 
 		public SAXErrorHandler(AssertionResult result) {
 			this.result = result;
 		}
 
 		/*
 		 * Can be caused by: - failure to read XSD file - xml does not match XSD
 		 */
 		public void error(SAXParseException exception) throws SAXParseException {
 
 			String msg = "error: " + errorDetails(exception);
 			log.debug(msg);
 			result.setFailureMessage(msg);
 			result.setError(true);
 			throw exception;
 		}
 
 		/*
 		 * Can be caused by: - premature end of file - non-whitespace content
 		 * after trailer
 		 */
 		public void fatalError(SAXParseException exception) throws SAXParseException {
 
 			String msg = "fatal: " + errorDetails(exception);
 			log.debug(msg);
 			result.setFailureMessage(msg);
 			result.setError(true);
 			throw exception;
 		}
 
 		/*
 		 * Not clear what can cause this ? conflicting versions perhaps
 		 */
 		public void warning(SAXParseException exception) throws SAXParseException {
 
 			String msg = "warning: " + errorDetails(exception);
 			log.debug(msg);
 			result.setFailureMessage(msg);
 			// result.setError(true); // TODO is this the correct strategy?
 			// throw exception; // allow assertion to pass
 
 		}
 	}
 }
\ No newline at end of file
diff --git a/src/components/org/apache/jmeter/assertions/XPathAssertion.java b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
index 7344b190b..7920a481b 100644
--- a/src/components/org/apache/jmeter/assertions/XPathAssertion.java
+++ b/src/components/org/apache/jmeter/assertions/XPathAssertion.java
@@ -1,247 +1,247 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.Serializable;
 
 import javax.xml.parsers.ParserConfigurationException;
 import javax.xml.transform.TransformerException;
 
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.util.XPathUtil;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.apache.xpath.XPathAPI;
 import org.w3c.dom.Document;
 import org.w3c.dom.NodeList;
 import org.xml.sax.SAXException;
 
 /**
  * Checks if the result is a well-formed XML content and whether it matches an
  * XPath
  * 
  * author <a href="mailto:jspears@astrology.com">Justin Spears </a>
  */
 public class XPathAssertion extends AbstractTestElement implements Serializable, Assertion {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	// private static XPathAPI xpath = null;
 
 	private static final String XPATH_KEY = "XPath.xpath";
 
 	private static final String WHITESPACE_KEY = "XPath.whitespace";
 
 	private static final String VALIDATE_KEY = "XPath.validate";
 
 	private static final String TOLERANT_KEY = "XPath.tolerant";
 
 	private static final String NEGATE_KEY = "XPath.negate";
 
 	private static final String NAMESPACE_KEY = "XPath.namespace";
 
 	public static final String DEFAULT_XPATH = "/";
 
 	/**
 	 * Returns the result of the Assertion. Checks if the result is well-formed
 	 * XML, and that the XPath expression is matched (or not, as the case may
 	 * be)
 	 */
 	public AssertionResult getResult(SampleResult response) {
 		// no error as default
-		AssertionResult result = new AssertionResult();
+		AssertionResult result = new AssertionResult(getName());
 		byte[] responseData = response.getResponseData();
 		if (responseData.length == 0) {
 			return result.setResultForNull();
 		}
 		result.setFailure(false);
 		result.setFailureMessage("");
 
 		if (log.isDebugEnabled()) {
 			log.debug(new StringBuffer("Validation is set to ").append(isValidating()).toString());
 			log.debug(new StringBuffer("Whitespace is set to ").append(isWhitespace()).toString());
 			log.debug(new StringBuffer("Tolerant is set to ").append(isTolerant()).toString());
 		}
 
 		Document doc = null;
 
 		try {
 			doc = XPathUtil.makeDocument(new ByteArrayInputStream(responseData), isValidating(),
 					isWhitespace(), isNamespace(), isTolerant());
 		} catch (SAXException e) {
 			log.debug("Caught sax exception: " + e);
 			result.setError(true);
 			result.setFailureMessage(new StringBuffer("SAXException: ").append(e.getMessage()).toString());
 			return result;
 		} catch (IOException e) {
 			log.warn("Cannot parse result content", e);
 			result.setError(true);
 			result.setFailureMessage(new StringBuffer("IOException: ").append(e.getMessage()).toString());
 			return result;
 		} catch (ParserConfigurationException e) {
 			log.warn("Cannot parse result content", e);
 			result.setError(true);
 			result.setFailureMessage(new StringBuffer("ParserConfigurationException: ").append(e.getMessage())
 					.toString());
 			return result;
 		}
 
 		if (doc == null || doc.getDocumentElement() == null) {
 			result.setError(true);
 			result.setFailureMessage("Document is null, probably not parsable");
 			return result;
 		}
 
 		NodeList nodeList = null;
 
 		try {
 			nodeList = XPathAPI.selectNodeList(doc, getXPathString());
 		} catch (TransformerException e) {
 			result.setError(true);
 			result.setFailureMessage(new StringBuffer("TransformerException: ").append(e.getMessage()).toString());
 			return result;
 		}
 
 		if (nodeList == null || nodeList.getLength() == 0) {
 			log.debug(new StringBuffer("nodeList null no match  ").append(getXPathString()).toString());
 			result.setFailure(!isNegated());
 			result.setFailureMessage("No Nodes Matched " + getXPathString());
 			return result;
 		}
 		log.debug("nodeList length " + nodeList.getLength());
 		if (log.isDebugEnabled() & !isNegated()) {
 			for (int i = 0; i < nodeList.getLength(); i++)
 				log.debug(new StringBuffer("nodeList[").append(i).append("] ").append(nodeList.item(i)).toString());
 		}
 		result.setFailure(isNegated());
 		if (isNegated())
 			result.setFailureMessage("Specified XPath was found... Turn off negate if this is not desired");
 		return result;
 	}
 
 	/**
 	 * Get The XPath String that will be used in matching the document
 	 * 
 	 * @return String xpath String
 	 */
 	public String getXPathString() {
 		return getPropertyAsString(XPATH_KEY, DEFAULT_XPATH);
 	}
 
 	/**
 	 * Set the XPath String this will be used as an xpath
 	 * 
 	 * @param xpath
 	 *            String
 	 */
 	public void setXPathString(String xpath) {
 		setProperty(new StringProperty(XPATH_KEY, xpath));
 	}
 
 	/**
 	 * Set whether to ignore element whitespace
 	 * 
 	 * @param whitespace
 	 */
 	public void setWhitespace(boolean whitespace) {
 		setProperty(new BooleanProperty(WHITESPACE_KEY, whitespace));
 	}
 
 	/**
 	 * Set use validation
 	 * 
 	 * @param validate
 	 */
 	public void setValidating(boolean validate) {
 		setProperty(new BooleanProperty(VALIDATE_KEY, validate));
 	}
 
 	/**
 	 * Set whether this is namespace aware
 	 * 
 	 * @param namespace
 	 */
 	public void setNamespace(boolean namespace) {
 		setProperty(new BooleanProperty(NAMESPACE_KEY, namespace));
 	}
 
 	/**
 	 * Set tolerant mode if required
 	 * 
 	 * @param tolerant
 	 *            true/false
 	 */
 	public void setTolerant(boolean tolerant) {
 		setProperty(new BooleanProperty(TOLERANT_KEY, tolerant));
 	}
 
 	public void setNegated(boolean negate) {
 		setProperty(new BooleanProperty(NEGATE_KEY, negate));
 	}
 
 	/**
 	 * Is this whitepsace ignored.
 	 * 
 	 * @return boolean
 	 */
 	public boolean isWhitespace() {
 		return getPropertyAsBoolean(WHITESPACE_KEY, false);
 	}
 
 	/**
 	 * Is this validating
 	 * 
 	 * @return boolean
 	 */
 	public boolean isValidating() {
 		return getPropertyAsBoolean(VALIDATE_KEY, false);
 	}
 
 	/**
 	 * Is this namespace aware?
 	 * 
 	 * @return boolean
 	 */
 	public boolean isNamespace() {
 		return getPropertyAsBoolean(NAMESPACE_KEY, false);
 	}
 
 	/**
 	 * Is this using tolerant mode?
 	 * 
 	 * @return boolean
 	 */
 	public boolean isTolerant() {
 		return getPropertyAsBoolean(TOLERANT_KEY, false);
 	}
 
 	/**
 	 * Negate the XPath test, that is return true if something is not found.
 	 * 
 	 * @return boolean negated
 	 */
 	public boolean isNegated() {
 		return getPropertyAsBoolean(NEGATE_KEY, false);
 	}
 
 }
\ No newline at end of file
diff --git a/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java b/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
index 669165b45..7c798c203 100644
--- a/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/AssertionVisualizer.java
@@ -1,115 +1,116 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Dimension;
 
 import javax.swing.Box;
 import javax.swing.JLabel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 
 /**
  * 
  * @version $Revision$ on $Date$
  */
 public class AssertionVisualizer extends AbstractVisualizer implements Clearable {
 
 	private JTextArea textArea;
 
 	public AssertionVisualizer() {
 		init();
 		setName(getStaticLabel());
 	}
 
 	public String getLabelResource() {
 		return "assertion_visualizer_title"; // $NON-NLS-1$
 	}
 
 	public void add(SampleResult sample) {
 		StringBuffer sb = new StringBuffer(100);
 		sb.append(sample.getSampleLabel());
 		sb.append(getAssertionResult(sample));
 		sb.append("\n"); // $NON-NLS-1$
 		synchronized (textArea) {
 			textArea.append(sb.toString());
 		}
 	}
 
 	public void clear() {
 		textArea.setText(""); // $NON-NLS-1$
 	}
 
 	private String getAssertionResult(SampleResult res) {
 		if (res != null) {
 			StringBuffer display = new StringBuffer();
 			AssertionResult assertionResults[] = res.getAssertionResults();
 			for (int i = 0; i < assertionResults.length; i++) {
 				AssertionResult item = assertionResults[i];
 
 				if (item.isFailure() || item.isError()) {
 					display.append("\n\t"); // $NON-NLS-1$
+					display.append(item.getName() != null ? item.getName() + " : " : "");
 					display.append(item.getFailureMessage());
 				}
 			}
 			return display.toString();
 		}
 		return "";
 	}
 
 	private void init() {
 		this.setLayout(new BorderLayout());
 
 		// MAIN PANEL
 		Border margin = new EmptyBorder(10, 10, 5, 10);
 
 		this.setBorder(margin);
 
 		// NAME
 		this.add(makeTitlePanel(), BorderLayout.NORTH);
 
 		// TEXTAREA LABEL
 		JLabel textAreaLabel = 
             new JLabel(JMeterUtils.getResString("assertion_textarea_label")); // $NON-NLS-1$
 		Box mainPanel = Box.createVerticalBox();
 		mainPanel.add(textAreaLabel);
 
 		// TEXTAREA
 		textArea = new JTextArea();
 		textArea.setEditable(false);
 		textArea.setLineWrap(false);
 		JScrollPane areaScrollPane = new JScrollPane(textArea);
 
 		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
 		areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 
         areaScrollPane.setPreferredSize(new Dimension(mainPanel.getWidth(),mainPanel.getHeight()));
 		mainPanel.add(areaScrollPane);
 		this.add(mainPanel, BorderLayout.CENTER);
 	}
 }
diff --git a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
index d6cb6ceb5..33b6b3ca7 100644
--- a/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
+++ b/src/components/org/apache/jmeter/visualizers/ViewResultsFullVisualizer.java
@@ -1,914 +1,997 @@
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
 
 package org.apache.jmeter.visualizers;
 
 import java.awt.BorderLayout;
 import java.awt.Color;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.IOException;
 import java.io.StringReader;
 import java.io.UnsupportedEncodingException;
 import java.util.Date;
 
 import javax.swing.BorderFactory;
 import javax.swing.ButtonGroup;
 import javax.swing.Icon;
 import javax.swing.ImageIcon;
 import javax.swing.JCheckBox;
 import javax.swing.JEditorPane;
 import javax.swing.JLabel;
 import javax.swing.JOptionPane;
 import javax.swing.JPanel;
 import javax.swing.JRadioButton;
 import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JTabbedPane;
 import javax.swing.JTextArea;
 import javax.swing.JTextPane;
 import javax.swing.JTree;
 import javax.swing.ToolTipManager;
 import javax.swing.event.TreeSelectionEvent;
 import javax.swing.event.TreeSelectionListener;
 import javax.swing.text.BadLocationException;
 import javax.swing.text.ComponentView;
 import javax.swing.text.Document;
 import javax.swing.text.EditorKit;
 import javax.swing.text.Element;
 import javax.swing.text.Style;
 import javax.swing.text.StyleConstants;
 import javax.swing.text.StyledDocument;
 import javax.swing.text.View;
 import javax.swing.text.ViewFactory;
 import javax.swing.text.html.HTML;
 import javax.swing.text.html.HTMLEditorKit;
 import javax.swing.tree.DefaultMutableTreeNode;
 import javax.swing.tree.DefaultTreeCellRenderer;
 import javax.swing.tree.DefaultTreeModel;
 import javax.swing.tree.TreePath;
 import javax.swing.tree.TreeSelectionModel;
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 
+import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 /**
  * Allows the tester to view the textual response from sampling an Entry. This
  * also allows to "single step through" the sampling process via a nice
  * "Continue" button.
  * 
  * Created 2001/07/25
  */
 public class ViewResultsFullVisualizer extends AbstractVisualizer implements ActionListener, TreeSelectionListener,
 		Clearable {
 	private static final Logger log = LoggingManager.getLoggerForClass();
 
 	private static final String XML_PFX = "<?xml "; // $NON-NLS-1$
 
 	public final static Color SERVER_ERROR_COLOR = Color.red;
 
 	public final static Color CLIENT_ERROR_COLOR = Color.blue;
 
 	public final static Color REDIRECT_COLOR = Color.green;
 
 	private static final String DOWNLOAD_LABEL = "Download embedded resources";
 
 	private static final String HTML_BUTTON_LABEL = "Render HTML";
 
 	private static final String XML_BUTTON_LABEL = "Render XML";
 
 	private static final String TEXT_BUTTON_LABEL = "Show Text";
 
 	private static final String TEXT_HTML = "text/html"; // $NON-NLS-1$
 
 	private static final String HTML_COMMAND = "html"; // $NON-NLS-1$
 
 	private static final String XML_COMMAND = "xml"; // $NON-NLS-1$
 
 	private static final String TEXT_COMMAND = "text"; // $NON-NLS-1$
 
 	private static final String STYLE_SERVER_ERROR = "ServerError"; // $NON-NLS-1$
 
 	private static final String STYLE_CLIENT_ERROR = "ClientError"; // $NON-NLS-1$
 
 	private static final String STYLE_REDIRECT = "Redirect"; // $NON-NLS-1$
 
 	private boolean textMode = true;
 
 	// set default command to Text
 	private String command = TEXT_COMMAND;
 
 	// Keep copies of the two editors needed
 	private static EditorKit customisedEditor = new LocalHTMLEditorKit();
 
 	private static EditorKit defaultHtmlEditor = JEditorPane.createEditorKitForContentType(TEXT_HTML);
 
 	private DefaultMutableTreeNode root;
 
 	private DefaultTreeModel treeModel;
 
 	private JTextPane stats;
 
 	private JEditorPane results;
 
 	private JScrollPane resultsScrollPane;
 
+	private JPanel resultsPane;
+
 	private JLabel imageLabel;
 
 	private JTextArea sampleDataField;
+	
+	private JPanel requestPane;
 
 	private JRadioButton textButton;
 
 	private JRadioButton htmlButton;
 
 	private JRadioButton xmlButton;
 
 	private JCheckBox downloadAll;
 
 	private JTree jTree;
 
+	private JTabbedPane rightSide;
+	
 	private static final ImageIcon imageSuccess = JMeterUtils.getImage(
 	        JMeterUtils.getPropDefault("viewResultsTree.success", "icon_success_sml.gif"));
 
 	private static final ImageIcon imageFailure = JMeterUtils.getImage(
 			JMeterUtils.getPropDefault("viewResultsTree.failure", "icon_warning_sml.gif"));
 	
 	public ViewResultsFullVisualizer() {
 		super();
 		log.debug("Start : ViewResultsFullVisualizer1");
 		init();
 		log.debug("End : ViewResultsFullVisualizer1");
 	}
 
 	public void add(SampleResult res) {
 		updateGui(res);
 	}
 
 	public String getLabelResource() {
 		return "view_results_tree_title"; // $NON-NLS-1$
 	}
 
 	/**
 	 * Update the visualizer with new data.
 	 */
 	public synchronized void updateGui(SampleResult res) {
 		log.debug("Start : updateGui1");
 		if (log.isDebugEnabled()) {
 			log.debug("updateGui1 : sample result - " + res);
 		}
+		// Add sample
 		DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(res);
 		treeModel.insertNodeInto(currNode, root, root.getChildCount());
 		addSubResults(currNode, res);
+		// Add any assertion that failed as children of the sample node
+		AssertionResult assertionResults[] = res.getAssertionResults();
+		int assertionIndex = 0;
+		for (int j = 0; j < assertionResults.length; j++) {
+			AssertionResult item = assertionResults[j];
+			
+			if (item.isFailure() || item.isError()) {
+				DefaultMutableTreeNode assertionNode = new DefaultMutableTreeNode(item);
+				treeModel.insertNodeInto(assertionNode, currNode, assertionIndex++);
+			}
+		}			
 
 		if (root.getChildCount() == 1) {
 			jTree.expandPath(new TreePath(root));
 		}
 		log.debug("End : updateGui1");
 	}
 
 	private void addSubResults(DefaultMutableTreeNode currNode, SampleResult res) {
 		SampleResult[] subResults = res.getSubResults();
 
 		int leafIndex = 0;
 
 		for (int i = 0; i < subResults.length; i++) {
 			SampleResult child = subResults[i];
 
 			if (log.isDebugEnabled()) {
 				log.debug("updateGui1 : child sample result - " + child);
 			}
 			DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(child);
 
 			treeModel.insertNodeInto(leafNode, currNode, leafIndex++);
 			addSubResults(leafNode, child);
 		}
 	}
 
 	/**
 	 * Clears the visualizer.
 	 */
 	public void clear() {
 		log.debug("Start : clear1");
 
 		if (log.isDebugEnabled()) {
 			log.debug("clear1 : total child - " + root.getChildCount());
 		}
 		while (root.getChildCount() > 0) {
 			// the child to be removed will always be 0 'cos as the nodes are
 			// removed the nth node will become (n-1)th
 			treeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(0));
 		}
 
 		results.setText("");// Response Data // $NON-NLS-1$
 		sampleDataField.setText("");// Request Data // $NON-NLS-1$
 		log.debug("End : clear1");
 	}
 
 	/**
 	 * Returns the description of this visualizer.
 	 * 
 	 * @return description of this visualizer
 	 */
 	public String toString() {
 		String desc = "Shows the text results of sampling in tree form";
 
 		if (log.isDebugEnabled()) {
 			log.debug("toString1 : Returning description - " + desc);
 		}
 		return desc;
 	}
 
 	/**
 	 * Sets the right pane to correspond to the selected node of the left tree.
 	 */
 	public void valueChanged(TreeSelectionEvent e) {
 		log.debug("Start : valueChanged1");
 		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
 		if (log.isDebugEnabled()) {
 			log.debug("valueChanged : selected node - " + node);
 		}
 
 		StyledDocument statsDoc = stats.getStyledDocument();
 		try {
 			statsDoc.remove(0, statsDoc.getLength());
 			sampleDataField.setText(""); // $NON-NLS-1$
 			results.setText(""); // $NON-NLS-1$
 			if (node != null) {
-				SampleResult res = (SampleResult) node.getUserObject();
+				Object userObject = node.getUserObject();
+				if(userObject instanceof SampleResult) {					
+					SampleResult res = (SampleResult) userObject;
+					
+					// We are displaying a SampleResult
+					setupTabPaneForSampleResult();
+
+					if (log.isDebugEnabled()) {
+						log.debug("valueChanged1 : sample result - " + res);
+					}
 
-				if (log.isDebugEnabled()) {
-					log.debug("valueChanged1 : sample result - " + res);
-				}
+					if (res != null) {
+						// load time label
+
+						log.debug("valueChanged1 : load time - " + res.getTime());
+						String sd = res.getSamplerData();
+						if (sd != null) {
+							String rh = res.getRequestHeaders();
+							if (rh != null) {
+								StringBuffer sb = new StringBuffer(sd.length() + rh.length()+20);
+								sb.append(sd);
+								sb.append("\nRequest Headers:\n");
+								sb.append(rh);
+								sd = sb.toString();
+							}
+							sampleDataField.setText(sd);
+						}
 
-				if (res != null) {
-					// load time label
-
-					log.debug("valueChanged1 : load time - " + res.getTime());
-                    String sd=res.getSamplerData();
-					if (sd != null) {
-						String rh = res.getRequestHeaders();
-						if (rh != null) {
-                            StringBuffer sb = new StringBuffer(sd.length()+rh.length()+20);
-                            sb.append(sd);
-                            sb.append("\nRequest Headers:\n");
-                            sb.append(rh);
-							sd = sb.toString();
-                        }
-						sampleDataField.setText(sd);
-                    }
-
-					statsDoc.insertString(statsDoc.getLength(), "Thread Name: "+res.getThreadName()+"\n", null);
-                    String startTime = new Date(res.getStartTime()).toString();
-                    statsDoc.insertString(statsDoc.getLength(), "Sample Start: "+startTime+"\n", null);
-					statsDoc.insertString(statsDoc.getLength(), "Load time: " + res.getTime() + "\n", null);
-
-					String responseCode = res.getResponseCode();
-					log.debug("valueChanged1 : response code - " + responseCode);
-
-					int responseLevel = 0;
-					if (responseCode != null) {
-						try {
-							responseLevel = Integer.parseInt(responseCode) / 100;
-						} catch (NumberFormatException numberFormatException) {
-							// no need to change the foreground color
+						statsDoc.insertString(statsDoc.getLength(), "Thread Name: " + res.getThreadName() + "\n", null);
+						String startTime = new Date(res.getStartTime()).toString();
+						statsDoc.insertString(statsDoc.getLength(), "Sample Start: " + startTime + "\n", null);
+						statsDoc.insertString(statsDoc.getLength(), "Load time: " + res.getTime() + "\n", null);
+
+						String responseCode = res.getResponseCode();
+						log.debug("valueChanged1 : response code - " + responseCode);
+
+						int responseLevel = 0;
+						if (responseCode != null) {
+							try {
+								responseLevel = Integer.parseInt(responseCode) / 100;
+							} catch (NumberFormatException numberFormatException) {
+								// no need to change the foreground color
+							}
 						}
-					}
 
-					Style style = null;
-					switch (responseLevel) {
-					case 3:
-						style = statsDoc.getStyle(STYLE_REDIRECT);
-						break;
-					case 4:
-						style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
-						break;
-					case 5:
-						style = statsDoc.getStyle(STYLE_SERVER_ERROR);
-						break;
-					}
-					statsDoc.insertString(statsDoc.getLength(), "HTTP response code: " + responseCode + "\n", style);
-
-					// response message label
-					String responseMsgStr = res.getResponseMessage();
-
-					log.debug("valueChanged1 : response message - " + responseMsgStr);
-					statsDoc
-							.insertString(statsDoc.getLength(), "HTTP response message: " + responseMsgStr + "\n", null);
-
-					statsDoc.insertString(statsDoc.getLength(), "\nHTTP response headers:\n" + res.getResponseHeaders()
-							+ "\n", null);
-
-					// get the text response and image icon
-					// to determine which is NOT null
-					if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null)
-																		// is OK
-					{
-						String response = getResponseAsString(res);
-						if (command.equals(TEXT_COMMAND)) {
-							showTextResponse(response);
-						} else if (command.equals(HTML_COMMAND)) {
-							showRenderedResponse(response, res);
-						} else if (command.equals(XML_COMMAND)) {
-							showRenderXMLResponse(response);
+						Style style = null;
+						switch (responseLevel) {
+						case 3:
+							style = statsDoc.getStyle(STYLE_REDIRECT);
+							break;
+						case 4:
+							style = statsDoc.getStyle(STYLE_CLIENT_ERROR);
+							break;
+						case 5:
+							style = statsDoc.getStyle(STYLE_SERVER_ERROR);
+							break;
 						}
-					} else {
-						byte[] responseBytes = res.getResponseData();
-						if (responseBytes != null) {
-							showImage(new ImageIcon(responseBytes));
+						statsDoc.insertString(statsDoc.getLength(), "HTTP response code: " + responseCode + "\n", style);
+
+						// response message label
+						String responseMsgStr = res.getResponseMessage();
+
+						log.debug("valueChanged1 : response message - " + responseMsgStr);
+						statsDoc.insertString(statsDoc.getLength(), "HTTP response message: " + responseMsgStr + "\n", null);
+
+						statsDoc.insertString(statsDoc.getLength(), "\nHTTP response headers:\n" + res.getResponseHeaders() + "\n", null);
+
+						// get the text response and image icon
+						// to determine which is NOT null
+						if ((SampleResult.TEXT).equals(res.getDataType())) // equals(null)
+																			// is OK
+						{
+							String response = getResponseAsString(res);
+							if (command.equals(TEXT_COMMAND)) {
+								showTextResponse(response);
+							} else if (command.equals(HTML_COMMAND)) {
+								showRenderedResponse(response, res);
+							} else if (command.equals(XML_COMMAND)) {
+								showRenderXMLResponse(response);
+							}
+						} else {
+							byte[] responseBytes = res.getResponseData();
+							if (responseBytes != null) {
+								showImage(new ImageIcon(responseBytes));
+							}
 						}
 					}
 				}
+				else if(userObject instanceof AssertionResult) {
+					AssertionResult res = (AssertionResult) userObject;
+					
+					// We are displaying an AssertionResult
+					setupTabPaneForAssertionResult();
+					
+					if (log.isDebugEnabled()) {
+						log.debug("valueChanged1 : sample result - " + res);
+					}
+
+					if (res != null) {
+						statsDoc.insertString(statsDoc.getLength(),
+								"Assertion error: " + res.isError() + "\n",
+								null);
+						statsDoc.insertString(statsDoc.getLength(),
+								"Assertion failure: " + res.isFailure() + "\n",
+								null);
+						statsDoc.insertString(statsDoc.getLength(),
+								"Assertion failure message : " + res.getFailureMessage() + "\n",
+								null);
+					}
+				}
 			}
 		} catch (BadLocationException exc) {
 			log.error("Error setting statistics text", exc);
 			stats.setText("");
 		}
 		log.debug("End : valueChanged1");
 	}
 
 	private void showImage(Icon image) {
 		imageLabel.setIcon(image);
 		resultsScrollPane.setViewportView(imageLabel);
 		textButton.setEnabled(false);
 		htmlButton.setEnabled(false);
 		xmlButton.setEnabled(false);
 	}
 
 	protected void showTextResponse(String response) {
 		results.setContentType("text/plain"); // $NON-NLS-1$
 		results.setText(response == null ? "" : response); // $NON-NLS-1$
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	transient SAXErrorHandler saxErrorHandler = new SAXErrorHandler();
 
 	private void showRenderXMLResponse(String response) {
 		String parsable="";
 		if (response == null) {
 			results.setText(""); // $NON-NLS-1$
 			parsable = ""; // $NON-NLS-1$
 		} else {
 			results.setText(response);
 			int start = response.indexOf(XML_PFX);
 			if (start > 0) {
 			    parsable = response.substring(start);				
 			} else {
 			    parsable=response;
 			}
 		}
 		results.setContentType("text/xml"); // $NON-NLS-1$
 		results.setCaretPosition(0);
 
 		Component view = results;
 
 		// there is duplicate Document class. Therefore I needed to declare the
 		// specific
 		// class that I want
 		org.w3c.dom.Document document = null;
 
 		try {
 
 			DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
 			parserFactory.setValidating(false);
 			parserFactory.setNamespaceAware(false);
 
 			// create a parser:
 			DocumentBuilder parser = parserFactory.newDocumentBuilder();
 
 			parser.setErrorHandler(saxErrorHandler);
 			document = parser.parse(new InputSource(new StringReader(parsable)));
 
 			JPanel domTreePanel = new DOMTreePanel(document);
 
 			document.normalize();
 
 			view = domTreePanel;
 		} catch (SAXParseException e) {
 			showErrorMessageDialog(saxErrorHandler.getErrorMessage(), saxErrorHandler.getMessageType());
 			log.debug(e.getMessage());
 		} catch (SAXException e) {
 			showErrorMessageDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE);
 			log.debug(e.getMessage());
 		} catch (IOException e) {
 			showErrorMessageDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE);
 			log.debug(e.getMessage());
 		} catch (ParserConfigurationException e) {
 			showErrorMessageDialog(e.getMessage(), JOptionPane.ERROR_MESSAGE);
 			log.debug(e.getMessage());
 		}
 		resultsScrollPane.setViewportView(view);
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	private static String getResponseAsString(SampleResult res) {
 
 		byte[] responseBytes = res.getResponseData();
 		String response = null;
 		if ((SampleResult.TEXT).equals(res.getDataType())) {
 			try {
 				// Showing large strings can be VERY costly, so we will avoid
 				// doing so if the response
 				// data is larger than 200K. TODO: instead, we could delay doing
 				// the result.setText
 				// call until the user chooses the "Response data" tab. Plus we
 				// could warn the user
 				// if this happens and revert the choice if he doesn't confirm
 				// he's ready to wait.
 				if (responseBytes.length > 200 * 1024) {
 					response = ("Response too large to be displayed (" + responseBytes.length + " bytes).");
 					log.warn("Response too large to display.");
 				} else {
 					response = new String(responseBytes, res.getDataEncoding());
 				}
 			} catch (UnsupportedEncodingException err) {
 				log.warn("Could not decode response " + err);
 				response = new String(responseBytes);// Try the default
 														// encoding instead
 			}
 		}
 		return response;
 	}
 
 	/**
 	 * Display the response as text or as rendered HTML. Change the text on the
 	 * button appropriate to the current display.
 	 * 
 	 * @param e
 	 *            the ActionEvent being processed
 	 */
 	public void actionPerformed(ActionEvent e) {
 		command = e.getActionCommand();
 
 		if (command != null
 				&& (command.equals(TEXT_COMMAND) || command.equals(HTML_COMMAND) || command.equals(XML_COMMAND))) {
 
 			textMode = command.equals(TEXT_COMMAND);
 
 			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
 
 			if (node == null) {
 				results.setText("");
 				return;
 			}
 
 			SampleResult res = (SampleResult) node.getUserObject();
 			String response = getResponseAsString(res);
 
 			if (command.equals(TEXT_COMMAND)) {
 				showTextResponse(response);
 			} else if (command.equals(HTML_COMMAND)) {
 				showRenderedResponse(response, res);
 			} else if (command.equals(XML_COMMAND)) {
 				showRenderXMLResponse(response);
 			}
 		}
 	}
 
 	protected void showRenderedResponse(String response, SampleResult res) {
 		if (response == null) {
 			results.setText("");
 			return;
 		}
 
 		int htmlIndex = response.indexOf("<HTML"); // could be <HTML lang=""> // $NON-NLS-1$
 
 		// Look for a case variation
 		if (htmlIndex < 0) {
 			htmlIndex = response.indexOf("<html"); // ditto // $NON-NLS-1$
 		}
 
 		// If we still can't find it, just try using all of the text
 		if (htmlIndex < 0) {
 			htmlIndex = 0;
 		}
 
 		String html = response.substring(htmlIndex);
 
 		/*
 		 * To disable downloading and rendering of images and frames, enable the
 		 * editor-kit. The Stream property can then be
 		 */
 
 		// Must be done before setContentType
 		results.setEditorKitForContentType(TEXT_HTML, downloadAll.isSelected() ? defaultHtmlEditor : customisedEditor);
 
 		results.setContentType(TEXT_HTML);
 
 		if (downloadAll.isSelected()) {
 			// Allow JMeter to render frames (and relative images)
 			// Must be done after setContentType [Why?]
 			results.getDocument().putProperty(Document.StreamDescriptionProperty, res.getURL());
 		}
 
 		/*
 		 * Get round problems parsing <META http-equiv='content-type'
 		 * content='text/html; charset=utf-8'> See
 		 * http://issues.apache.org/bugzilla/show_bug.cgi?id=23315
 		 * 
 		 * Is this due to a bug in Java?
 		 */
 		results.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE); // $NON-NLS-1$
 
 		results.setText(html);
 		results.setCaretPosition(0);
 		resultsScrollPane.setViewportView(results);
 
 		textButton.setEnabled(true);
 		htmlButton.setEnabled(true);
 		xmlButton.setEnabled(true);
 	}
 
 	// TODO this method changed because Render XML button added
 	// Could probably be private anyway, because it's only used locally
 	protected Component createHtmlOrTextPane() {
 		ButtonGroup group = new ButtonGroup();
 
 		textButton = new JRadioButton(TEXT_BUTTON_LABEL);
 		textButton.setActionCommand(TEXT_COMMAND);
 		textButton.addActionListener(this);
 		textButton.setSelected(textMode);
 		group.add(textButton);
 
 		htmlButton = new JRadioButton(HTML_BUTTON_LABEL);
 		htmlButton.setActionCommand(HTML_COMMAND);
 		htmlButton.addActionListener(this);
 		htmlButton.setSelected(!textMode);
 		group.add(htmlButton);
 
 		xmlButton = new JRadioButton(XML_BUTTON_LABEL);
 		xmlButton.setActionCommand(XML_COMMAND);
 		xmlButton.addActionListener(this);
 		xmlButton.setSelected(!textMode);
 		group.add(xmlButton);
 
 		downloadAll = new JCheckBox(DOWNLOAD_LABEL);
 
 		JPanel pane = new JPanel();
 		pane.add(textButton);
 		pane.add(htmlButton);
 		pane.add(xmlButton);
 		pane.add(downloadAll);
 		return pane;
 	}
 
 	/**
 	 * Initialize this visualizer
 	 */
 	protected void init() {
 		setLayout(new BorderLayout(0, 5));
 		setBorder(makeBorder());
 
 		add(makeTitlePanel(), BorderLayout.NORTH);
 
 		Component leftSide = createLeftPanel();
-		JTabbedPane rightSide = new JTabbedPane();
-
+		rightSide = new JTabbedPane();
+		// Add the common tab
 		rightSide.addTab(JMeterUtils.getResString("view_results_tab_sampler"), createResponseMetadataPanel()); // $NON-NLS-1$
-		rightSide.addTab(JMeterUtils.getResString("view_results_tab_request"), createRequestPanel()); // $NON-NLS-1$
-		rightSide.addTab(JMeterUtils.getResString("view_results_tab_response"), createResponseDataPanel()); // $NON-NLS-1$
+		// Create the panels for the other tabs
+		requestPane = createRequestPanel();
+		resultsPane = createResponseDataPanel();
 
 		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
 		add(mainSplit, BorderLayout.CENTER);
 	}
+	
+	private void setupTabPaneForSampleResult() {
+		// Set the title for the first tab
+		rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_sampler"));
+		// Add the other tabs if not present
+		if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")) < 0) { // $NON-NLS-1$
+			rightSide.addTab(JMeterUtils.getResString("view_results_tab_request"), requestPane); // $NON-NLS-1$
+		}
+		if(rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")) < 0) { // $NON-NLS-1$
+			rightSide.addTab(JMeterUtils.getResString("view_results_tab_response"), resultsPane); // $NON-NLS-1$
+		}
+	}
+	
+	private void setupTabPaneForAssertionResult() {
+		// Set the title for the first tab
+		rightSide.setTitleAt(0, JMeterUtils.getResString("view_results_tab_assertion"));
+		// Remove the other tabs if present
+		int requestTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_request")); // $NON-NLS-1$
+		if(requestTabIndex >= 0) {
+			rightSide.removeTabAt(requestTabIndex);
+		}
+		int responseTabIndex = rightSide.indexOfTab(JMeterUtils.getResString("view_results_tab_response")); // $NON-NLS-1$
+		if(responseTabIndex >= 0) {
+			rightSide.removeTabAt(responseTabIndex);
+		}
+	}
 
 	private Component createLeftPanel() {
 		SampleResult rootSampleResult = new SampleResult();
 		rootSampleResult.setSampleLabel("Root");
 		rootSampleResult.setSuccessful(true);
 		root = new DefaultMutableTreeNode(rootSampleResult);
 
 		treeModel = new DefaultTreeModel(root);
 		jTree = new JTree(treeModel);
 		jTree.setCellRenderer(new ResultsNodeRenderer());
 		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
 		jTree.addTreeSelectionListener(this);
 		jTree.setRootVisible(false);
 		jTree.setShowsRootHandles(true);
 
 		JScrollPane treePane = new JScrollPane(jTree);
 		treePane.setPreferredSize(new Dimension(200, 300));
 		return treePane;
 	}
 
 	private Component createResponseMetadataPanel() {
 		stats = new JTextPane();
 		stats.setEditable(false);
 		stats.setBackground(getBackground());
 
 		// Add styles to use for different types of status messages
 		StyledDocument doc = (StyledDocument) stats.getDocument();
 
 		Style style = doc.addStyle(STYLE_REDIRECT, null);
 		StyleConstants.setForeground(style, REDIRECT_COLOR);
 
 		style = doc.addStyle(STYLE_CLIENT_ERROR, null);
 		StyleConstants.setForeground(style, CLIENT_ERROR_COLOR);
 
 		style = doc.addStyle(STYLE_SERVER_ERROR, null);
 		StyleConstants.setForeground(style, SERVER_ERROR_COLOR);
 
 		JScrollPane pane = makeScrollPane(stats);
 		pane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
 		return pane;
 	}
 
-	private Component createRequestPanel() {
+	private JPanel createRequestPanel() {
 		sampleDataField = new JTextArea();
 		sampleDataField.setEditable(false);
 		sampleDataField.setLineWrap(true);
 		sampleDataField.setWrapStyleWord(true);
 
 		JPanel pane = new JPanel(new BorderLayout(0, 5));
 		pane.add(makeScrollPane(sampleDataField));
 		return pane;
 	}
 
-	private Component createResponseDataPanel() {
+	private JPanel createResponseDataPanel() {
 		results = new JEditorPane();
 		results.setEditable(false);
 
 		resultsScrollPane = makeScrollPane(results);
 		imageLabel = new JLabel();
 
 		JPanel resultsPane = new JPanel(new BorderLayout());
 		resultsPane.add(resultsScrollPane, BorderLayout.CENTER);
 		resultsPane.add(createHtmlOrTextPane(), BorderLayout.SOUTH);
 
 		return resultsPane;
 	}
 
 	private static class ResultsNodeRenderer extends DefaultTreeCellRenderer {
 		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
 				boolean leaf, int row, boolean focus) {
 			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
-			if (!((SampleResult) ((DefaultMutableTreeNode) value).getUserObject()).isSuccessful()) {
+			boolean failure = true;
+			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
+			if(userObject instanceof SampleResult) {
+				failure = !(((SampleResult) userObject).isSuccessful());
+			}
+			else if(userObject instanceof AssertionResult) {
+				AssertionResult assertion = (AssertionResult) userObject;
+				failure =  assertion.isError() || assertion.isFailure();
+			}
+			
+			// Set the status for the node
+			if (failure) {
 				this.setForeground(Color.red);
 				this.setIcon(imageFailure);
 			} else {
 				this.setIcon(imageSuccess);
 			}
 			return this;
 		}
 	}
 
 	private static class LocalHTMLEditorKit extends HTMLEditorKit {
 
 		private static final ViewFactory defaultFactory = new LocalHTMLFactory();
 
 		public ViewFactory getViewFactory() {
 			return defaultFactory;
 		}
 
 		private static class LocalHTMLFactory extends javax.swing.text.html.HTMLEditorKit.HTMLFactory {
 			/*
 			 * Provide dummy implementations to suppress download and display of
 			 * related resources: - FRAMEs - IMAGEs TODO create better dummy
 			 * displays TODO suppress LINK somehow
 			 */
 			public View create(Element elem) {
 				Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
 				if (o instanceof HTML.Tag) {
 					HTML.Tag kind = (HTML.Tag) o;
 					if (kind == HTML.Tag.FRAME) {
 						return new ComponentView(elem);
 					} else if (kind == HTML.Tag.IMG) {
 						return new ComponentView(elem);
 					}
 				}
 				return super.create(elem);
 			}
 		}
 	}
 
 	/**
 	 * 
 	 * A Dom tree panel for to display response as tree view author <a
 	 * href="mailto:d.maung@mdl.com">Dave Maung</a> TODO implement to find any
 	 * nodes in the tree using TreePath.
 	 * 
 	 */
 	private static class DOMTreePanel extends JPanel {
 
 		private JTree domJTree;
 
 		public DOMTreePanel(org.w3c.dom.Document document) {
 			super(new GridLayout(1, 0));
 			try {
 				Node firstElement = getFirstElement(document);
 				DefaultMutableTreeNode top = new XMLDefaultMutableTreeNode(firstElement);
 				domJTree = new JTree(top);
 
 				domJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
 				domJTree.setShowsRootHandles(true);
 				JScrollPane domJScrollPane = new JScrollPane(domJTree);
 				domJTree.setAutoscrolls(true);
 				this.add(domJScrollPane);
 				ToolTipManager.sharedInstance().registerComponent(domJTree);
 				domJTree.setCellRenderer(new DomTreeRenderer());
 				this.setPreferredSize(new Dimension(800, 600));
 			} catch (SAXException e) {
 				log.warn("", e);
 			}
 
 		}
 
 		/**
 		 * Skip all DTD nodes, all prolog nodes. They dont support in tree view
 		 * We let user to insert them however in DOMTreeView, we dont display it
 		 * 
 		 * @param root
 		 * @return
 		 */
 		private Node getFirstElement(Node parent) {
 			NodeList childNodes = parent.getChildNodes();
 			Node toReturn = null;
 			for (int i = 0; i < childNodes.getLength(); i++) {
 				Node childNode = childNodes.item(i);
 				toReturn = childNode;
 				if (childNode.getNodeType() == Node.ELEMENT_NODE)
 					break;
 
 			}
 			return toReturn;
 		}
 
 		/**
 		 * This class is to view as tooltext. This is very useful, when the
 		 * contents has long string and does not fit in the view. it will also
 		 * automatically wrap line for each 100 characters since tool tip
 		 * support html. author <a href="mailto:d.maung@mdl.com">Dave Maung</a>
 		 */
 		private static class DomTreeRenderer extends DefaultTreeCellRenderer {
 			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
 					boolean leaf, int row, boolean phasFocus) {
 				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, phasFocus);
 
 				DefaultMutableTreeNode valueTreeNode = (DefaultMutableTreeNode) value;
 				setToolTipText(getHTML(valueTreeNode.toString(), "<br>", 100)); // $NON-NLS-1$
 				return this;
 			}
 
 			/**
 			 * get the html
 			 * 
 			 * @param str
 			 * @param separator
 			 * @param maxChar
 			 * @return
 			 */
 			private String getHTML(String str, String separator, int maxChar) {
 				StringBuffer strBuf = new StringBuffer("<html><body bgcolor=\"yellow\"><b>"); // $NON-NLS-1$
 				char[] chars = str.toCharArray();
 				for (int i = 0; i < chars.length; i++) {
 
 					if (i % maxChar == 0 && i != 0)
 						strBuf.append(separator);
 					strBuf.append(encode(chars[i]));
 
 				}
 				strBuf.append("</b></body></html>"); // $NON-NLS-1$
 				return strBuf.toString();
 
 			}
 
 			private String encode(char c) {
 				String toReturn = String.valueOf(c);
 				switch (c) {
 				case '<': // $NON-NLS-1$
 					toReturn = "&lt;"; // $NON-NLS-1$
 					break;
 				case '>': // $NON-NLS-1$
 					toReturn = "&gt;"; // $NON-NLS-1$
 					break;
 				case '\'': // $NON-NLS-1$
 					toReturn = "&apos;"; // $NON-NLS-1$
 					break;
 				case '\"': // $NON-NLS-1$
 					toReturn = "&quot;"; // $NON-NLS-1$
 					break;
 
 				}
 				return toReturn;
 			}
 		}
 	}
 
 	private static void showErrorMessageDialog(String message, int messageType) {
 		JOptionPane.showMessageDialog(null, message, "Error", messageType);
 	}
 
 	// Helper method to construct SAX error details
 	private static String errorDetails(SAXParseException spe) {
 		StringBuffer str = new StringBuffer(80);
 		int i;
 		i = spe.getLineNumber();
 		if (i != -1) {
 			str.append("line=");
 			str.append(i);
 			str.append(" col=");
 			str.append(spe.getColumnNumber());
 			str.append(" ");
 		}
 		str.append(spe.getLocalizedMessage());
 		return str.toString();
 	}
 
 	private static class SAXErrorHandler implements ErrorHandler {
 		private String msg;
 
 		private int messageType;
 
 		public SAXErrorHandler() {
 			msg = ""; // $NON-NLS-1$
 
 		}
 
 		public void error(SAXParseException exception) throws SAXParseException {
 			msg = "error: " + errorDetails(exception);
 
 			log.debug(msg);
 			messageType = JOptionPane.ERROR_MESSAGE;
 			throw exception;
 		}
 
 		/*
 		 * Can be caused by: - premature end of file - non-whitespace content
 		 * after trailer
 		 */
 		public void fatalError(SAXParseException exception) throws SAXParseException {
 
 			msg = "fatal: " + errorDetails(exception);
 			messageType = JOptionPane.ERROR_MESSAGE;
 			log.debug(msg);
 
 			throw exception;
 		}
 
 		/*
 		 * Not clear what can cause this ? conflicting versions perhaps
 		 */
 		public void warning(SAXParseException exception) throws SAXParseException {
 			msg = "warning: " + errorDetails(exception);
 			log.debug(msg);
 			messageType = JOptionPane.WARNING_MESSAGE;
 		}
 
 		/**
 		 * get the JOptionPaneMessage Type
 		 * 
 		 * @return
 		 */
 		public int getMessageType() {
 			return messageType;
 		}
 
 		/**
 		 * get error message
 		 * 
 		 * @return
 		 */
 		public String getErrorMessage() {
 			return msg;
 		}
 	}
 
 }
diff --git a/src/core/org/apache/jmeter/assertions/AssertionResult.java b/src/core/org/apache/jmeter/assertions/AssertionResult.java
index 8f3b8fb9a..cdde7b063 100644
--- a/src/core/org/apache/jmeter/assertions/AssertionResult.java
+++ b/src/core/org/apache/jmeter/assertions/AssertionResult.java
@@ -1,135 +1,169 @@
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
 
 package org.apache.jmeter.assertions;
 
 import java.io.Serializable;
 
 /**
  * @author Michael Stover
- * @version $Revision$
  */
 public class AssertionResult implements Serializable {
-	public static final String RESPONSE_WAS_NULL = "Response was null";
+	public static final String RESPONSE_WAS_NULL = "Response was null"; // $NON-NLS-1$
 
+	/** Name of the assertion. */
+	private String name;
+	
 	/** True if the assertion failed. */
 	private boolean failure;
 
 	/** True if there was an error checking the assertion. */
 	private boolean error;
 
 	/** A message describing the failure. */
 	private String failureMessage;
 
 	/**
 	 * Create a new Assertion Result. The result will indicate no failure or
 	 * error.
+	 * @deprecated - use the named constructor
 	 */
-	public AssertionResult() {
+	AssertionResult() {
+	}
+	
+	/**
+	 * Create a new Assertion Result. The result will indicate no failure or
+	 * error.
+	 * 
+	 * @param name the name of the assertion
+	 */
+	public AssertionResult(String name) {
+		setName(name);
+	}
+	
+	/**
+	 * Get the name of the assertion
+	 * 
+	 * @return the name of the assertion
+	 */
+	public String getName() {
+		return name;
 	}
 
 	/**
+	 * Set the name of the assertion
+	 * 
+	 * @param name the name of the assertion
+	 */
+	public void setName(String name) {
+		this.name = name;
+	}
+	
+	/**
 	 * Check if the assertion failed. If it failed, the failure message may give
 	 * more details about the failure.
 	 * 
 	 * @return true if the assertion failed, false if the sample met the
 	 *         assertion criteria
 	 */
 	public boolean isFailure() {
 		return failure;
 	}
 
 	/**
 	 * Check if an error occurred while checking the assertion. If an error
 	 * occurred, the failure message may give more details about the error.
 	 * 
 	 * @return true if an error occurred while checking the assertion, false
 	 *         otherwise.
 	 */
 	public boolean isError() {
 		return error;
 	}
 
 	/**
 	 * Get the message associated with any failure or error. This method may
 	 * return null if no message was set.
 	 * 
 	 * @return a failure or error message, or null if no message has been set
 	 */
 	public String getFailureMessage() {
 		return failureMessage;
 	}
 
 	/**
 	 * Set the flag indicating whether or not an error occurred.
 	 * 
 	 * @param e
 	 *            true if an error occurred, false otherwise
 	 */
 	public void setError(boolean e) {
 		error = e;
 	}
 
 	/**
 	 * Set the flag indicating whether or not a failure occurred.
 	 * 
 	 * @param f
 	 *            true if a failure occurred, false otherwise
 	 */
 	public void setFailure(boolean f) {
 		failure = f;
 	}
 
 	/**
 	 * Set the failure message giving more details about a failure or error.
 	 * 
 	 * @param message
 	 *            the message to set
 	 */
 	public void setFailureMessage(String message) {
 		failureMessage = message;
 	}
 
 	/**
 	 * Convenience method for setting up failed results
 	 * 
 	 * @param message
 	 *            the message to set
 	 * @return this
 	 * 
 	 */
 	public AssertionResult setResultForFailure(String message) {
 		error = false;
 		failure = true;
 		failureMessage = message;
 		return this;
 	}
 
 	/**
 	 * Convenience method for setting up results where the response was null
 	 * 
 	 * @return assertion result with appropriate fields set up
 	 */
 	public AssertionResult setResultForNull() {
 		error = false;
 		failure = true;
 		failureMessage = RESPONSE_WAS_NULL;
 		return this;
 	}
 
+	public String toString() {
+		return getName() != null ? getName() : super.toString();
+	}
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index c0291c8ef..da87c730e 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,816 +1,817 @@
 # Warning: JMeterUtils.getResString() replaces space with '_'
 # and converts keys to lowercase before lookup
 # => All keys in this file must also be lower case or they won't match
 #
 about=About Apache JMeter
 add=Add
 add_as_child=Add as Child
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_column=Column
 aggregate_graph_display=Display Graph
 aggregate_graph_height=Height
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label
 aggregate_graph_ms=Milliseconds
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_user_title=Title for Graph
 aggregate_graph_width=Width
 aggregate_report=Aggregate Report
 aggregate_report_90=90%
 aggregate_report_90%_line=90% Line
 aggregate_report_bandwidth=KB/sec
 aggregate_report_count=# Samples
 aggregate_report_error=Error
 aggregate_report_error%=Error %
 aggregate_report_max=Max
 aggregate_report_median=Median
 aggregate_report_min=Min
 aggregate_report_rate=Throughput
 aggregate_report_stddev=Std. Dev.
 aggregate_report_total_label=TOTAL
 als_message=Note\: The Access Log Parser is generic in design and allows you to plugin
 als_message2=your own parser. To do so, implement the LogParser, add the jar to the
 als_message3=/lib directory and enter the class in the sampler.
 analyze=Analyze Data File...
 anchor_modifier_title=HTML Link Parser
 appearance=Look and Feel
 argument_must_not_be_negative=The Argument must not be negative\!
 assertion_assume_success=Ignore Status
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_resp_field=Response Field to Test
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 bind=Thread Bind
 browse=Browse...
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (variables\: SampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName)
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cookies_per_iter=Clear cookies each iteration?
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 compare=Compare
 comparefilt=Compare filter
 config_element=Config Element
 config_save_settings=Configure
 configure_wsdl=Configure
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_manager_policy=Cookie Policy
 cookie_manager_title=HTTP Cookie Manager
 cookies_stored=Cookies Stored in the Cookie Manager
 copy=Copy
 corba_config_title=CORBA Sampler Config
 corba_input_data_file=Input Data File\:
 corba_methods=Choose method to invoke\:
 corba_name_server=Name Server\:
 corba_port=Port Number\:
 corba_request_data=Input Data
 corba_sample_title=CORBA Sampler
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 countlim=Size limit
 csvread_file_file_name=CSV file to get values from | *alias
 cut=Cut
 cut_paste_function=Copy and paste function string
 database_conn_pool_max_usage=Max Usage For Each Connection\:
 database_conn_pool_props=Database Connection Pool
 database_conn_pool_size=Number of Connections in Pool\:
 database_conn_pool_title=JDBC Database Connection Pool Defaults
 database_driver_class=Driver Class\:
 database_login_title=JDBC Database Login Defaults
 database_sql_query_string=SQL Query String\:
 database_sql_query_title=JDBC SQL Query Defaults
 database_testing_title=JDBC Request
 database_url=JDBC URL\:
 database_url_jdbc_props=Database URL and JDBC Driver
 ddn=DN
 de=German
 debug_off=Disable debug
 debug_on=Enable debug
 default_parameters=Default Parameters
 default_value_field=Default Value\:
 delay=Startup delay (seconds)
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=deletion test
 deref=Dereference aliases
 disable=Disable
 distribution_graph_title=Distribution Graph (alpha)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 domain=Domain
 done=Done
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Activate
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 error_loading_help=Error loading help page
 error_occurred=Error Occurred
 es=Spanish
 example_data=Sample Data
 example_title=Example Sampler
 exit=Exit
 expiration=Expiration
 field_name=Field name
 file=File
 file_already_in_use=That file is already in use
 file_to_retrieve=File to Retrieve From Server\:
 file_visualizer_append=Append to Existing Data File
 file_visualizer_auto_flush=Automatically Flush After Each Data Sample
 file_visualizer_browse=Browse...
 file_visualizer_close=Close
 file_visualizer_file_options=File Options
 file_visualizer_filename=Filename
 file_visualizer_flush=Flush
 file_visualizer_missing_filename=No output filename specified.
 file_visualizer_open=Open
 file_visualizer_output_file=Write All Data to a File
 file_visualizer_submit_data=Include Submitted Data
 file_visualizer_title=File Reporter
 file_visualizer_verbose=Verbose Output
 filename=File Name
 follow_redirects=Follow Redirects
 follow_redirects_auto=Redirect Automatically
 foreach_controller_title=ForEach Controller
 foreach_input=Input variable prefix
 foreach_output=Output variable name
 foreach_use_separator=Add "_" before number ?
 format=Number format
 fr=French
 ftp_sample_title=FTP Request Defaults
 ftp_testing_title=FTP Request
 function_dialog_menu_item=Function Helper Dialog
 function_helper_title=Function Helper
 function_name_param=Name of variable in which to store the result
 function_name_paropt=Name of variable in which to store the result (optional)
 function_params=Function Parameters
 functional_mode=Functional Test Mode
 functional_mode_explanation=Select functional test mode only if you need \nto record to file the data received from the server for each request.  \n\nSelecting this option impacts performance considerably.
 gaussian_timer_delay=Constant Delay Offset (in milliseconds)\:
 gaussian_timer_memo=Adds a random delay with a gaussian distribution
 gaussian_timer_range=Deviation (in milliseconds)\:
 gaussian_timer_title=Gaussian Random Timer
 generate=Generate
 generator=Name of Generator class
 generator_cnf_msg=Could not find the generator class. Please make sure you place your jar file in the /lib directory.
 generator_illegal_msg=Could not access the generator class due to IllegalAcessException.
 generator_instantiate_msg=Could not create an instance of the generator parser. Please make sure the generator implements Generator interface.
 get_xml_from_file=File with SOAP XML Data (overrides above text)
 get_xml_from_random=Message Folder
 get_xml_message=Note\: Parsing XML is CPU intensive. Therefore, do not set the thread count
 get_xml_message2=too high. In general, 10 threads will consume 100% of the CPU on a 900mhz
 get_xml_message3=Pentium 3. On a pentium 4 2.4ghz cpu, 50 threads is the upper limit. Your
 get_xml_message4=options for increasing the number of clients is to increase the number of
 get_xml_message5=machines or use multi-cpu systems.
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 help=Help
 help_node=What's this node?
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_parameter_mask=HTML Parameter Mask
 http_implementation=HTTP Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_title=HTTP Mirror Server
 id_prefix=ID Prefix
 id_suffix=ID Suffix
 if_controller_label=Condition
 if_controller_title=If Controller
 ignore_subcontrollers=Ignore sub-controller blocks
 include_controller=Include Controller
 include_equals=Include Equals?
 include_path=Include Test Plan
 increment=Increment
 infinite=Forever
 initial_context_factory=Initial Context Factory
 insert_after=Insert After
 insert_before=Insert Before
 insert_parent=Insert Parent
 interleave_control_title=Interleave Controller
 intsum_param_1=First int to add.
 intsum_param_2=Second int to add - further ints can be summed by adding further arguments.
 invalid_data=Invalid data
 invalid_mail=Error occurred sending the e-mail
 invalid_mail_address=One or more invalid e-mail addresses detected
 invalid_mail_server=Problem contacting the e-mail server (see JMeter log file)
 iteration_counter_arg_1=TRUE, for each user to have own counter, FALSE for a global counter
 iterator_num=Loop Count\:
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 jms_auth_not_required=Not Required
 jms_auth_required=Required
 jms_authentication=Authentication
 jms_client_caption=Receive client uses TopicSubscriber.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Configuration
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_error_msg=Object message should read from an external file. Text input is currently selected, please remember to change it.
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_message_title=Message properties
 jms_message_type=Message Type
 jms_msg_content=Content
 jms_object_message=Object Message
 jms_point_to_point=JMS Point-to-Point
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Random File
 jms_read_response=Read Response
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title= JMS Default Request
 jms_send_queue=JNDI name Request queue
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use TopicSubscriber.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title= Messaging Request
 jms_text_message=Text Message
 jms_timeout=Timeout (milliseconds)
 jms_topic=Topic
 jms_use_file=From file
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
 jms_use_text=Textarea
 jms_user=User
 jndi_config_title=JNDI Configuration
 jndi_lookup_name=Remote Interface
 jndi_lookup_title=JNDI Lookup Configuration
 jndi_method_button_invoke=Invoke
 jndi_method_button_reflect=Reflect
 jndi_method_home_name=Home Method Name
 jndi_method_home_parms=Home Method Parameters
 jndi_method_name=Method Configuration
 jndi_method_remote_interface_list=Remote Interfaces
 jndi_method_remote_name=Remote Method Name
 jndi_method_remote_parms=Remote Method Parameters
 jndi_method_title=Remote Method Configuration
 jndi_testing_title=JNDI Request
 jndi_url_jndi_props=JNDI Properties
 jp=Japanese
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_do_setup_teardown=Do not call setUp and tearDown
 junit_error_code=Error Code
 junit_error_default_code=9999
 junit_error_default_msg=An unexpected error occured
 junit_error_msg=Error Message
 junit_failure_code=Failure Code
 junit_failure_default_code=0001
 junit_failure_default_msg=Test failed
 junit_failure_msg=Failure Message
 junit_pkg_filter=Package Filter
 junit_request=JUnit Request
 junit_request_defaults=JUnit Request Defaults
 junit_success_code=Success Code
 junit_success_default_code=1000
 junit_success_default_msg=Test successful
 junit_success_msg=Success Message
 junit_test_config=JUnit Test Parameters
 junit_test_method=Test Method
 ldap_argument_list=LDAPArgument List
 ldap_connto=Connection timeout (in milliseconds)
 ldap_parse_results=Parse the search results ?
 ldap_sample_title=LDAP Request Defaults
 ldap_search_baseobject=Perform baseobject search
 ldap_search_onelevel=Perform onelevel search
 ldap_search_subtree=Perform subtree search
 ldap_secure=Use Secure LDAP Protocol ?
 ldap_testing_title=LDAP Request
 ldapext_sample_title=LDAP Extended Request Defaults
 ldapext_testing_title= LDAP Extended Request
 load=Load
 load_wsdl=Load WSDL
 log_errors_only=Log Errors Only
 log_file=Location of log File
 log_parser=Name of Log Parser class
 log_parser_cnf_msg=Could not find the class. Please make sure you place your jar file in the /lib directory.
 log_parser_illegal_msg=Could not access the class due to IllegalAcessException.
 log_parser_instantiate_msg=Could not create an instance of the log parser. Please make sure the parser implements LogParser interface.
 log_sampler=Tomcat Access Log Sampler
 logic_controller_title=Simple Controller
 login_config=Login Configuration
 login_config_element=Login Config Element
 loop_controller_title=Loop Controller
 looping_control=Looping Control
 lower_bound=Lower Bound
 mail_reader_account=Username:
 mail_reader_all_messages=All
 mail_reader_delete=Delete messages from the server
 mail_reader_folder=Folder:
 mail_reader_imap=IMAP
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_pop3=POP3
 mail_reader_server=Server:
 mail_reader_server_type=Server Type:
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_attributes_panel=Mailing attributes
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\:
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 memory_cache=Memory Cache
 menu_assertions=Assertions
 menu_close=Close
 menu_config_element=Config Element
 menu_edit=Edit
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_timer=Timer
 metadata=MetaData
 method=Method\:
 mimetype=Mimetype
 minimum_param=The minimum value allowed for a range of values
 minute=minute
 modddn=Old entry name
 modification_controller_title=Modification Controller
 modification_manager_title=Modification Manager
 modify_test=Modify Test
 modtest=Modification test
 module_controller_title=Module Controller
 module_controller_warning=Could not find module: 
 monitor_equation_active=Active:  (busy/max) > 25%
 monitor_equation_dead=Dead:  no response
 monitor_equation_healthy=Healthy:  (busy/max) < 25%
 monitor_equation_load=Load:  ( (busy / max) * 50) + ( (used memory / max memory) * 50)
 monitor_equation_warning=Warning:  (busy/max) > 67%
 monitor_health_tab_title=Health
 monitor_health_title=Monitor Results
 monitor_is_title=Use as Monitor
 monitor_label_left_bottom=0 %
 monitor_label_left_middle=50 %
 monitor_label_left_top=100 %
 monitor_label_right_active=Active
 monitor_label_right_dead=Dead
 monitor_label_right_healthy=Healthy
 monitor_label_right_warning=Warning
 monitor_legend_health=Health
 monitor_legend_load=Load
 monitor_legend_memory_per=Memory % (used/total)
 monitor_legend_thread_per=Thread % (busy/max)
 monitor_load_factor_mem=50
 monitor_load_factor_thread=50
 monitor_performance_servers=Servers
 monitor_performance_tab_title=Performance
 monitor_performance_title=Performance Graph
 name=Name\:
 new=New
 newdn=New distinguished name
 no=Norwegian
 number_of_threads=Number of Threads (users)\:
 obsolete_test_element=This test element is obsolete
 once_only_controller_title=Once Only Controller
 opcode=opCode
 open=Open...
 option=Options
 optional_tasks=Optional Tasks
 paramtable=Send Parameters With the Request\:
 password=Password
 paste=Paste
 paste_insert=Paste As Insert
 path=Path\:
 path_extension_choice=Path Extension (use ";" as separator)
 path_extension_dont_use_equals=Do not use equals in path extension (Intershop Enfinity compatibility)
 path_extension_dont_use_questionmark=Do not use questionmark in path extension (Intershop Enfinity compatibility)
 patterns_to_exclude=Patterns to Exclude
 patterns_to_include=Patterns to Include
 pkcs12_desc=PKCS 12 Key (*.p12)
 port=Port\:
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip={0}\: {1}
 property_undefined=Undefined
 property_value_param=Value of property
 protocol=Protocol (default http)\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_headers=Capture HTTP Headers
 proxy_httpsspoofing=Attempt https Spoofing
 proxy_regex=Regex matching
 proxy_separators=Add Separators
 proxy_target=Target Controller\:
 proxy_title=HTTP Proxy Server
 proxy_usekeepalive=Set Keep-Alive
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 read_response_message=Read response is not checked. To see the response, please check the box in the sampler.
 read_response_note=If read response is unchecked, the sampler will not read the response
 read_response_note2=or set the SampleResult. This improves performance, but it means
 read_response_note3=the response content won't be logged.
 read_soap_response=Read SOAP Response
 realm=Realm
 record_controller_title=Recording Controller
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_source=Response Field to check
 regex_src_body=Body
 regex_src_hdrs=Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search results from previous request
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
 rename=Rename entry
 report=Report
 report_bar_chart=Bar Chart
 report_bar_graph_url=URL
 report_base_directory=Base Directory
 report_chart_caption=Chart Caption
 report_chart_x_axis=X Axis
 report_chart_x_axis_label=Label for X Axis
 report_chart_y_axis=Y Axis
 report_chart_y_axis_label=Label for Y Axis
 report_line_graph=Line Graph
 report_line_graph_urls=Include URLs
 report_output_directory=Output Directory for Report
 report_page=Report Page
 report_page_element=Page Element
 report_page_footer=Page Footer
 report_page_header=Page Header
 report_page_index=Create Page Index
 report_page_intro=Page Introduction
 report_page_style_url=Stylesheet url
 report_page_title=Page Title
 report_pie_chart=Pie Chart
 report_plan=Report Plan
 report_select=Select
 report_summary=Report Summary
 report_table=Report Table
 report_writer=Report Writer
 report_writer_html=HTML Report Writer
 request_data=Request Data
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_errors=Save Failed Responses only
 resultsaver_prefix=Filename prefix\:
 resultsaver_title=Save Responses to a file
 retobj=Return object
 reuseconnection=Re-use connection
 root=Root
 root_title=Root
 run=Run
 running_test=Running test
 runtime_controller_title=Runtime Controller
 runtime_seconds=Runtime (seconds)
 sample_result_save_configuration=Sample Result Save Configuration
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_thread=Stop Thread
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save As...
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_assertionresultsfailuremessage=Save Assertion Results Failure Message
 save_assertions=Save Assertion Results
 save_asxml=Save As XML
 save_bytes=Save byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_label=Save Label
 save_latency=Save Latency
 save_message=Save Response Message
 save_requestheaders=Save Request Headers
 save_responsedata=Save Response Data
 save_responseheaders=Save Response Headers
 save_samplerdata=Save Sampler Data
 save_subresults=Save Sub Results
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search_base=Search base
 search_filter=Search Filter
 search_test=Search Test
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send a File With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=Filename\:
 send_file_mime_label=MIME Type\:
 send_file_param_name_label=Value for "name" attribute\:
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
 should_save=You should save your test plan before running it.  \nIf you are using supporting data files (ie, for CSV Data Set or _StringFromFile), \nthen it is particularly important to first save your test script. \nDo you want to save your test plan first?
 shutdown=Shutdown
 simple_config_element=Simple Config Element
 simple_data_writer_title=Simple Data Writer
 size_assertion_comparator_error_equal=been equal to
 size_assertion_comparator_error_greater=been greater than
 size_assertion_comparator_error_greaterequal=been greater or equal to
 size_assertion_comparator_error_less=been less than
 size_assertion_comparator_error_lessequal=been less than or equal to
 size_assertion_comparator_error_notequal=not been equal to
 size_assertion_comparator_label=Type of Comparison
 size_assertion_failure=The result was the wrong size\: It was {0} bytes, but should have {1} {2} bytes.
 size_assertion_input_error=Please enter a valid positive integer.
 size_assertion_label=Size in bytes\:
 size_assertion_size_test=Size to Assert
 size_assertion_title=Size Assertion
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 spline_visualizer_average=Average
 spline_visualizer_incoming=Incoming
 spline_visualizer_maximum=Maximum
 spline_visualizer_minimum=Minimum
 spline_visualizer_title=Spline Visualizer
 spline_visualizer_waitingmessage=Waiting for samples
 ssl_alias_prompt=Please type your preferred alias
 ssl_alias_select=Select your alias for the test
 ssl_alias_title=Client Alias
 ssl_error_title=Key Store Problem
 ssl_pass_prompt=Please type your password
 ssl_pass_title=KeyStore Password
 ssl_port=SSL Port
 sslmanager=SSL Manager
 start=Start
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads.  Please be patient.
 stopping_test_title=Stopping Test
 string_from_file_file_name=Enter full path to file
 string_from_file_seq_final=Final file sequence number
 string_from_file_seq_start=Start file sequence number
 success?=Success?
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 table_visualizer_bytes=Bytes
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_start_time=Start Time
 table_visualizer_thread_name=Thread Name
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 template_field=Template\:
 test=Test
 test_action_action=Action
 test_action_duration=Duration
 test_action_pause=Pause
 test_action_stop=Stop
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_plan=Test Plan
 test_plan_classpath_browse=Add directory or jar to classpath
 testconfiguration=Test Configuration
 testplan.serialized=Run each Thread Group separately (i.e. run one group before starting the next)
 testplan_comments=Comments\:
 testt=Test
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 transaction_controller_title=Transaction Controller
 unbind=Thread Unbind
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 update_per_iter=Update Once Per Iteration
 upload=File Upload
 upper_bound=Upper Bound
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocol\:
 url_config_title=HTTP Request Defaults
 url_full_config_title=UrlFull Sample
 url_multipart_config_title=HTTP Multipart Request Defaults
 use_keepalive=Use KeepAlive
 use_recording_controller=Use Recording Controller
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 value=Value
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_in_table=View Results in Table
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
+view_results_tab_assertion=Assertion result
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_request=HTTP Request
 web_server=Web Server
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 webservice_proxy_host=Proxy Host
 webservice_proxy_note=If Use HTTP Proxy is checked, but no host or port are provided, the sampler
 webservice_proxy_note2=will look at command line options. If no proxy host or port are provided by
 webservice_proxy_note3=either, it will fail silently.
 webservice_proxy_port=Proxy Port
 webservice_sampler_title=WebService(SOAP) Request
 webservice_soap_action=SOAPAction
 webservice_timeout=Timeout:
 webservice_use_proxy=Use HTTP Proxy
 while_controller_label=Condition
 while_controller_title=While Controller
 workbench_title=WorkBench
 wsdl_helper_error=The WSDL was not valid, please double check the url.
 wsdl_url=WSDL URL
 wsdl_url_error=The WSDL was emtpy.
 xml_assertion_title=XML Assertion
 xml_namespace_button=Use Namespaces
 xml_tolerant_button=Tolerant XML/HTML Parser
 xml_validate_button=Validate XML
 xml_whitespace_button=Ignore Whitespace
 xmlschema_assertion_label=File Name:
 xmlschema_assertion_title=XML Schema Assertion
 xpath_assertion_button=Validate
 xpath_assertion_check=Check XPath Expression
 xpath_assertion_error=Error with XPath
 xpath_assertion_failed=Invalid XPath Expression
 xpath_assertion_label=XPath
 xpath_assertion_negate=True if nothing matches
 xpath_assertion_option=XML Parsing Options
 xpath_assertion_test=XPath Assertion 
 xpath_assertion_tidy=Try and tidy up the input
 xpath_assertion_title=XPath Assertion
 xpath_assertion_valid=Valid XPath Expression
 xpath_assertion_validation=Validate the XML against the DTD
 xpath_assertion_whitespace=Ignore whitespace
 xpath_expression=XPath expression to match against
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_extractor_tolerant=Use Tidy ?
 xpath_file_file_name=XML file to get values from 
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
 # Please add new entries in alphabetical order
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 67f35eb3d..779384678 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,834 +1,835 @@
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
 <document>   
 <properties>     
 	<author email="jmeter-dev AT jakarta.apache.org">JMeter developers</author>     
 	<title>History of Changes</title>   
 </properties> 
 <body> 
 <section name="History of Changes"> 
 <p><b>Changes sections are chronologically ordered from top (most recent) to bottom 
 (least recent)</b></p>  
 
 <!--  ===================  -->
 
 <h3>Version 2.2.1</h3>
 <h4>Summary of changes (for more details, see below)</h4>
 <p>
 Some of the main enhancements are:
 </p>
 <ul>
 <li>Htmlparser 2.0 now used for parsing</li>
 <li>HTTP Authorisation now supports domain and realm</li>
 <li>HttpClient options can be specified via httpclient.parameters file</li>
 <li>HttpClient now behaves the same as Java Http for SSL certificates</li>
 <li>HTTP Mirror Server to allow local testing of HTTP samplers</li>
 <li>HTTP Proxy supports XML-RPC recording</li>
 <li>__V() function allows support of nested variable references</li>
 <li>LDAP Ext sampler optionally parses result sets and supports secure mode</li>
 </ul>
 <p>
 The main bug fixes are:
 </p>
 <ul>
 <li>HTTPS (SSL) handling now much improved</li>
 <li>Various Remote mode bugs fixed</li>
 <li>Control+C and Control+V now work in the test tree</li>
 <li>Latency and Encoding now available in CSV log output</li>
 </ul>
 <h4>Known problems:</h4>
 <p>Thread active counts are always zero in CSV and XML files when running remote tests.
 </p>
 <p>The property file_format.testlog=2.1 is treated the same as 2.2.
 However JMeter does honour the 3 testplan versions.</p>
 <p>
 Bug 22510 - JMeter always uses the first entry in the keystore.
 </p>
 <p>
 Remote mode does not work if JMeter is installed in a directory where the path name contains spaces.
 </p>
 <p>
 BeanShell test elements leak memory.
 This can be reduced by using a file instead of including the script in the test element.
 </p>
 <h4>Incompatible changes (usage):</h4>
 <p>
 The LDAP Extended Sampler now uses the same panel for both Thread Bind and Single-Bind tests.
 This means that any tests using the Single-bind test will need to be updated to set the username and password.
 </p>
 <p>
 Bug 41104: JMeterThread behaviour was changed so that PostProcessors are run in forward order
 (as they appear in the test plan) rather than reverse order as previously.
 The original behaviour can be restored by setting the following JMeter property:
 <br/>
 jmeterthread.reversePostProcessors=true
 </p>
 <p>
 The HTTP Authorisation Manager now has extra columns for domain and realm, 
 so the temporary work-round of using '\' and '@' in the username to delimit the domain and realm
 has been removed.
 </p>
 <h4>Incompatible changes (development):</h4>
 <p>
 Calulator and SamplingStatCalculator classes no longer provide any formatting of their data.
 Formatting should now be done using the jorphan.gui Renderer classes.
 </p>
 <p>
 Removed deprecated method JMeterUtils.split() - use JOrphanUtils version instead.
 </p>
 
 <h4>New functionality:</h4>
 <ul>
 <li>Added httpclient.parameters.file to allow HttpClient parameters to be defined</li>
 <li>Added beanshell.init.file property to run a BeanShell script at startup</li>
 <li>Added timeout for WebService (SOAP) Sampler</li>
 <li>Bug 40804 - Change Counter default to max = Long.MAX_VALUE</li>
 <li>BeanShell Post-Processor no longer ignores samples with zero-length result data</li>
 <li>Use property jmeter.home (if present) to override user.dir when starting JMeter</li>
 <li>Bug 41457 - Add TCP Sampler option to not re-use connections</li>
 <li>Bug 41522 - Use JUnit sampler name in sample results</li>
 <li>HttpClient now behaves the same as the JDK http sampler for invalid certificates etc</li>
 <li>Add Domain and Realm support to HTTP Authorisation Manager</li>
 <li>Bug 33964 - send file as entire post body if name/type are omitted</li>
 <li>HTTP Mirror Server Workbench element</li>
 <li>Bug 41253 - extend XPathExtractor to work with non-NodeList XPath expressions</li>
 <li>Bug 39717 - use icons in the results tree</li>
 <li>Added __V variable function to resolve nested variable names</li>
 <li>Bug 41707 - HTTP Proxy XML-RPC support</li>
 <li>Bug 40381 - LDAP: more descriptive strings</li>
 <li>Bug 40369 (partial) Equals Response Assertion</li>
 <li>Bug 41705 - add content-encoding option to HTTP samplers for POST requests</li>
 <li>Bug 40933, 40945 - optional matching of embedded resource URLs</li>
 <li>Bug 41704 - Allow charset encoding to be specified for CSV DataSet</li>
 <li>Bug 40103 - various LDAP enhancements</li>
 <li>Bug 39864 - BeanShell init files now found from currrent or bin directory</li>
 <li>New -j option to easily change jmeter log file</li>
 <li>Bug 41259 - Comment field added to all test elements</li>
 <li>Add standard deviation to Summary Report</li>
+<li>Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer</li>
 </ul>
 
 <h4>Non-functional improvements:</h4>
 <ul>
 <li>Functor calls can now be unit tested</li>
 <li>Replace com.sun.net classes with javax.net</li>
 <li>Extract external jar definitions into build.properties file</li>
 <li>Use specific jar names in build classpaths so errors are detected sooner</li>
 <li>Tidied up ORO calls; now only one cache, size given by oro.patterncache.size, default 1000</li>
 </ul>
 
 <h4>External jar updates:</h4>
 <ul>
 <li>Htmlparser 2.0-20060923</li>
 <li>xstream 1.2.1/xpp3_min-1.1.3.4.O</li>
 <li>Batik 1.6</li>
 <li>BSF 2.4.0</li>
 <li>commons-collections 3.2</li>
 <li>commons-jexl 1.1</li>
 <li>commons-lang-2.3 (added)</li>
 <li>velocity 1.5</li>
 <li></li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 39773 - NTLM now needs local host name - fix other call</li>
 <li>Bug 40438 - setting "httpclient.localaddress" has no effect</li>
 <li>Bug 40419 - Chinese messages translation fix</li>
 <li>Bug 39861 - fix typo</li>
 <li>Bug 40562 - redirects no longer invoke RE post processors</li>
 <li>Bug 40451 - set label if not set by sampler</li>
 <li>Fix NPE in CounterConfig.java in Remote mode</li>
 <li>Bug 40791 - Calculator used by Summary Report</li>
 <li>Bug 40772 - correctly parse missing fields in CSV log files</li>
 <li>Bug 40773 - XML log file timestamp not parsed correctly</li>
 <li>Bug 41029 - JMeter -t fails to close input JMX file</li>
 <li>Bug 40954 - Statistical mode in distributed testing shows wrong results</li>
 <li>Fix ClassCast Exception when using sampler that returns null, e..g TestAction</li>
 <li>Bug 41277 - add Latency and Encoding to CSV output</li>
 <li>Bug 41414 - Mac OS X may add extra item to -jar classpath</li>
 <li>Fix NPE when saving thread counts in remote testing</li>
 <li>Bug 34261 - NPE in HtmlParser (allow for missing attributes)</li>
 <li>Bug 40100 - check FileServer type before calling close</li>
 <li>Bug 39887 - jmeter.util.SSLManager: Couldn't load keystore error message</li>
 <li>Bug 41543 - exception when webserver returns "500 Internal Server Error" and content-length is 0</li>
 <li>Bug 41416 - don't use chunked input for text-box input in SOAP-RPC sampler</li>
 <li>Bug 39827 - SOAP Sampler content length for files</li>
 <li>Fix Class cast exception in Clear.java</li>
 <li>Bug 40383 - don't set content-type if already set</li>
 <li>Mailer Visualiser test button now works if test plan has not yet been saved</li>
 <li>Bug 36959 - Shortcuts "ctrl c" and "ctrl v" don't work on the tree elements</li>
 <li>Bug 40696 - retrieve embedded resources from STYLE URL() attributes</li>
 <li>Bug 41568 - Problem when running tests remotely when using a 'Counter'</li>
 <li>Fixed various classes that assumed timestamps were always end time stamps:
 <ul>
 <li>SamplingStatCalculator</li>
 <li>JTLData</li>
 <li>RunningSample</li>
 </ul>
 </li>
 <li>Bug 40325 - allow specification of proxyuser and proxypassword for WebServiceSampler</li>
 <li>Change HttpClient proxy definition to use NTCredentials; added http.proxyDomain property for this</li>
 <li>Bug 40371 - response assertion "pattern to test" scrollbar problem</li>
 </ul>
 
 <h3>Version 2.2</h3>
 
 <h4>Incompatible changes:</h4>
 <p>
 The time stamp is now set to the sampler start time (it was the end).
 To revert to the previous behaviour, change the property <b>sampleresult.timestamp.start</b> to false (or comment it)
 </p>
 <p>The JMX output format has been simplified and files are not backwards compatible</p>
 <p>
 The JMeter.BAT file no longer changes directory to JMeter home, but runs from the current working directory.
 The jmeter-n.bat and jmeter-t.bat files change to the directory containing the input file.
 </p>
 <p>
 Listeners are now started slightly later in order to allow variable names to be used.
 This may cause some problems; if so define the following in jmeter.properties:
 <br/>
 jmeterengine.startlistenerslater=false
 </p>
 
 <h4>Known problems:</h4>
 <ul>
 <li>Post-processors run in reverse order (see bug 41140)</li>
 <li>Module Controller does not work in non-GUI mode</li>
 <li>Aggregate Report and some other listeners use increasing amounts of memory as a test progresses</li>
 <li>Does not always handle non-default encoding properly</li>
 <li>Spaces in the installation path cause problems for client-server mode</li>
 <li>Change of Language does not propagate to all test elements</li>
 <li>SamplingStatCalculator keeps a List of all samples for calculation purposes; 
 this can cause memory exhaustion in long-running tests</li>
 <li>Does not properly handle server certificates if they are expired or not installed locally</li>
 </ul>
 
 <h4>New functionality:</h4>
 <ul>
 <li>Report function</li>
 <li>XPath Extractor Post-Processor. Handles single and multiple matches.</li>
 <li>Simpler JMX file format (2.2)</li>
 <li>BeanshellSampler code can update ResponseData directly</li>
 <li>Bug 37490 - Allow UDV as delay in Duration Assertion</li>
 <li>Slow connection emulation for HttpClient</li>
 <li>Enhanced JUnitSampler so that by default assert errors and exceptions are not appended to the error message. 
 Users must explicitly check append in the sampler</li>
 <li>Enhanced the documentation for webservice sampler to explain how it works with CSVDataSet</li>
 <li>Enhanced the documentation for javascript function to explain escaping comma</li>
 <li>Allow CSV Data Set file names to be absolute</li>
 <li>Report Tree compiler errors better</li>
 <li>Don't reset Regex Extractor variable if default is empty</li>
 <li>includecontroller.prefix property added</li>
 <li>Regular Expression Extractor sets group count</li>
 <li>Can now save entire screen as an image, not just the right-hand pane</li>
 <li>Bug 38901 - Add optional SOAPAction header to SOAP Sampler</li>
 <li>New BeanShell test elements: Timer, PreProcessor, PostProcessor, Listener</li>
 <li>__split() function now clears next variable, so it can be used with ForEach Controller</li>
 <li>Bug 38682 - add CallableStatement functionality to JDBC Sampler</li>
 <li>Make it easier to change the RMI/Server port</li>
 <li>Add property jmeter.save.saveservice.xml_pi to provide optional xml processing instruction in JTL files</li>
 <li>Add bytes and URL to items that can be saved in sample log files (XML and CSV)</li>
 <li>The Post-Processor "Save Responses to a File" now saves the generated file name with the
 sample, and the file name can be included in the sample log file.
 </li>
 <li>Change jmeter.bat DOS script so it works from any directory</li>
 <li>New -N option to define nonProxyHosts from command-line</li>
 <li>New -S option to define system properties from input file</li>
 <li>Bug 26136 - allow configuration of local address</li>
 <li>Expand tree by default when loading a test plan - can be disabled by setting property onload.expandtree=false</li>
 <li>Bug 11843 - URL Rewriter can now cache the session id</li>
 <li>Counter Pre-Processor now supports formatted numbers</li>
 <li>Add support for HEAD PUT OPTIONS TRACE and DELETE methods</li>
 <li>Allow default HTTP implementation to be changed</li>
 <li>Optionally save active thread counts (group and all) to result files</li>
 <li>Variables/functions can now be used in Listener file names</li>
 <li>New __time() function; define START.MS/START.YMD/START.HMS properties and variables</li>
 <li>Add Thread Name to Tree and Table Views</li>
 <li>Add debug functions: What class, debug on, debug off</li>
 <li>Non-caching Calculator - used by Table Visualiser to reduce memory footprint</li>
 <li>Summary Report - similar to Aggregate Report, but uses less memory</li>
 <li>Bug 39580 - recycle option for CSV Dataset</li>
 <li>Bug 37652 - support for Ajp Tomcat protocol</li>
 <li>Bug 39626 - Loading SOAP/XML-RPC requests from file</li>
 <li>Bug 39652 - Allow truncation of labels on AxisGraph</li>
 <li>Allow use of htmlparser 1.6</li>
 <li>Bug 39656 - always use SOAP action if it is provided</li>
 <li>Automatically include properties from user.properties file</li>
 <li>Add __jexl() function - evaluates Commons JEXL expressions</li>
 <li>Optionally load JMeter properties from user.properties and system properties from system.properties.</li>
 <li>Bug 39707 - allow Regex match against URL</li>
 <li>Add start time to Table Visualiser</li>
 <li>HTTP Samplers can now extract embedded resources for any required media types</li>
 </ul>
 
 <h4>Bug fixes:</h4>
 <ul>
 <li>Fix NPE when no module selected in Module Controller</li>
 <li>Fix NPE in XStream when no ResponseData present</li>
 <li>Remove ?xml prefix when running with Java 1.5 and no x-jars</li>
 <li>Bug 37117 - setProperty() function should return ""; added optional return of original setting</li>
 <li>Fix CSV output time format</li>
 <li>Bug 37140 - handle encoding better in RegexFunction</li>
 <li>Load all cookies, not just the first; fix class cast exception</li>
 <li>Fix default Cookie path name (remove page name)</li>
 <li>Fixed resultcode attribute name</li>
 <li>Bug 36898 - apply encoding to RegexExtractor</li>
 <li>Add properties for saving subresults, assertions, latency, samplerData, responseHeaders, requestHeaders &amp; encoding</li>
 <li>Bug 37705 - Synch Timer now works OK after run is stopped</li>
 <li>Bug 37716 - Proxy request now handles file Post correctly</li>
 <li>HttpClient Sampler now saves latency</li>
 <li>Fix NPE when using JavaScript function on Test Plan</li>
 <li>Fix Base Href parsing in htmlparser</li>
 <li>Bug 38256 - handle cookie with no path</li>
 <li>Bug 38391 - use long when accumulating timer delays</li>
 <li>Bug 38554 - Random function now uses long numbers</li>
 <li>Bug 35224 - allow duplicate attributes for LDAP sampler</li>
 <li>Bug 38693 - Webservice sampler can now use https protocol</li>
 <li>Bug 38646 - Regex Extractor now clears old variables on match failure</li>
 <li>Bug 38640 - fix WebService Sampler pooling</li>
 <li>Bug 38474 - HTML Link Parser doesn't follow frame links</li>
 <li>Bug 36430 - Counter now uses long rather than int to increase the range</li>
 <li>Bug 38302 - fix XPath function</li>
 <li>Bug 38748 - JDBC DataSourceElement fails with remote testing</li>
 <li>Bug 38902 - sometimes -1 seems to be returned unnecessarily for response code</li>
 <li>Bug 38840 - make XML Assertion thread-safe</li>
 <li>Bug 38681 - Include controller now works in non-GUI mode</li>
 <li>Add write(OS,IS) implementation to TCPClientImpl</li>
 <li>Sample Result converter saves response code as "rc". Previously it saved as "rs" but read with "rc"; it will now also read with "rc".
 The XSL stylesheets also now accept either "rc" or "rs"</li>
 <li>Fix counter function so each counter instance is independent (previously the per-user counters were shared between instances of the function)</li>
 <li>Fix TestBean Examples so that they work</li>
 <li>Fix JTidy parser so it does not skip body tags with background images</li>
 <li>Fix HtmlParser parser so it catches all background images</li>
 <li>Bug 39252 set SoapSampler sample result from XML data</li>
 <li>Bug 38694 - WebServiceSampler not setting data encoding correctly</li>
 <li>Result Collector now closes input files read by listeners</li>
 <li>Bug 25505 - First HTTP sampling fails with "HTTPS hostname wrong: should be 'localhost'"</li>
 <li>Bug 25236 - remove double scrollbar from Assertion Result Listener</li>
 <li>Bug 38234 - Graph Listener divide by zero problem</li>
 <li>Bug 38824 - clarify behaviour of Ignore Status</li>
 <li>Bug 38250 - jmeter.properties "language" now supports country suffix, for zh_CN and zh_TW etc</li>
 <li>jmeter.properties file is now closed after it has been read</li>
 <li>Bug 39533 - StatCalculator added wrong items</li>
 <li>Bug 39599 - ConcurrentModificationException</li>
 <li>HTTPSampler2 now handles Auto and Follow redirects correctly</li>
 <li>Bug 29481 - fix reloading sample results so subresults not counted twice</li>
 <li>Bug 30267 - handle AutoRedirects properly</li>
 <li>Bug 39677 - allow for space in JMETER_BIN variable</li>
 <li>Use Commons HttpClient cookie parsing and management. Fix various problems with cookie handling.</li>
 <li>Bug 39773 - NTCredentials needs host name</li>
 </ul>	
 	
 <h4>Other changes</h4>
 <ul>
 <li>Updated to HTTPClient 3.0 (from 2.0)</li>
 <li>Updated to Commons Collections 3.1</li>
 <li>Improved formatting of Request Data in Tree View</li>
 <li>Expanded user documentation</li>
 <li>Added MANIFEST, NOTICE and LICENSE to all jars</li>
 <li>Extract htmlparser interface into separate jarfile to make it possible to replace the parser</li>
 <li>Removed SQL Config GUI as no longer needed (or working!)</li>
 <li>HTTPSampler no longer logs a warning for Page not found (404)</li>
 <li>StringFromFile now callable as __StringFromFile (as well as _StringFromFile)</li>
 <li>Updated to Commons Logging 1.1</li>
 </ul>
 
 <!--  ===================  -->
 
 
 <hr/>
 <h3>Version 2.1.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Include Controller allows a test plan to reference an external jmx file</li>
 <li>New JUnitSampler added for using JUnit Test classes</li>
 <li>New Aggregate Graph listener is capable of graphing aggregate statistics</li>
 <li>Can provide additional classpath entries using the property user.classpath and on the Test Plan element</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>AccessLog Sampler and JDBC test elements populated correctly from 2.0 test plans</li>
 <li>BSF Sampler now populates filename and parameters from saved test plan</li>
 <li>Bug 36500 - handle missing data more gracefully in WebServiceSampler</li>
 <li>Bug 35546 - add merge to right-click menu</li>
 <li>Bug 36642 - Summariser stopped working in 2.1</li>
 <li>Bug 36618 - CSV header line did not match saved data</li>
 <li>JMeter should now run under JVM 1.3 (but does not build with 1.3)</li>
 </ul>	
 	
 
 <!--  ===================  -->
 
 <h3>Version 2.1</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>New Test Script file format - smaller, more compact, more readable</li>
 <li>New Sample Result file format - smaller, more compact</li>
 <li>XSchema Assertion</li>
 <li>XML Tree display</li>
 <li>CSV DataSet Config item</li>
 <li>New JDBC Connection Pool Config Element</li>
 <li>Synchronisation Timer</li>
 <li>setProperty function</li>
 <li>Save response data on error</li>
 <li>Ant JMeter XSLT now optionally shows failed responses and has internal links</li>
 <li>Allow JavaScript variable name to be omitted</li>
 <li>Changed following Samplers to set sample label from sampler name</li>
 <li>All Test elements can be saved as a graphics image to a file</li>
 <li>Bug 35026 - add RE pattern matching to Proxy</li>
 <li>Bug 34739 - Enhance constant Throughput timer</li>
 <li>Bug 25052 - use response encoding to create comparison string in Response Assertion</li>
 <li>New optional icons</li>
 <li>Allow icons to be defined via property files</li>
 <li>New stylesheets for 2.1 format XML test output</li>
 <li>Save samplers, config element and listeners as PNG</li>
 <li>Enhanced support for WSDL processing</li>
 <li>New JMS sampler for topic and queue messages</li>
 <li>How-to for JMS samplers</li>
 <li>Bug 35525 - Added Spanish localisation</li>
 <li>Bug 30379 - allow server.rmi.port to be overridden</li>
 <li>enhanced the monitor listener to save the calculated stats</li>
 <li>Functions and variables now work at top level of test plan</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Bug 34586 - XPath always remained as /</li>
 <li>BeanShellInterpreter did not handle null objects properly</li>
 <li>Fix Chinese resource bundle names</li>
 <li>Save field names if required to CSV files</li>
 <li>Ensure XML file is closed</li>
 <li>Correct icons now displayed for TestBean components</li>
 <li>Allow for missing optional jar(s) in creating menus</li>
 <li>Changed Samplers to set sample label from sampler name as was the case for HTTP</li>
 <li>Fix various samplers to avoid NPEs when incomplete data is provided</li>
 <li>Fix Cookie Manager to use seconds; add debug</li>
 <li>Bug 35067 - set up filename when using -t option</li>
 <li>Don't substitute TestElement.* properties by UDVs in Proxy</li>
 <li>Bug 35065 - don't save old extensions in File Saver</li>
 <li>Bug 25413 - don't enable Restart button unnecessarily</li>
 <li>Bug 35059 - Runtime Controller stopped working</li>
 <li>Clear up any left-over connections created by LDAP Extended Sampler</li>
 <li>Bug 23248 - module controller didn't remember stuff between save and reload</li>
 <li>Fix Chinese locales</li>
 <li>Bug 29920 - change default locale if necessary to ensure default properties are picked up when English is selected.</li>
 <li>Bug fixes for Tomcat monitor captions</li> 
 <li>Fixed webservice sampler so it works with user defined variables</li>
 <li>Fixed screen borders for LDAP config GUI elements</li>
 <li>Bug 31184 - make sure encoding is specified in JDBC sampler</li>
 <li>TCP sampler - only share sockets with same host:port details; correct the manual</li>
 <li>Extract src attribute for embed tags in JTidy and Html Parsers</li>
 </ul>	
 
 <!--  ===================  -->
 
 <h3>Version 2.0.3</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>XPath Assertion and XPath Function</li>
 <li>Switch Controller</li>
 <li>ForEach Controller can now loop through sets of groups</li>
 <li>Allow CSVRead delimiter to be changed (see jmeter.properties)</li>
 <li>Bug 33920 - allow additional property files</li>
 <li>Bug 33845 - allow direct override of Home dir</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>Regex Extractor nested constant not put in correct place (32395)</li>
 <li>Start time reset to now if necessary so that delay works OK.</li>
 <li>Missing start/end times in scheduler are assumed to be now, not 1970</li>
 <li>Bug 28661 - 304 responses not appearing in listeners</li>
 <li>DOS scripts now handle different disks better</li>
 <li>Bug 32345 - HTTP Rewriter does not work with HTTP Request default</li>
 <li>Catch Runtime Exceptions so an error in one Listener does not affect others</li>
 <li>Bug 33467 - __threadNum() extracted number wrongly </li>
 <li>Bug 29186,33299 - fix CLI parsing of "-" in second argument</li>
 <li>Fix CLI parse bug: -D arg1=arg2. Log more startup parameters.</li>
 <li>Fix JTidy and HTMLParser parsers to handle form src= and link rel=stylesheet</li>
 <li>JMeterThread now logs Errors to jmeter.log which were appearing on console</li>
 <li>Ensure WhileController condition is dynamically checked</li>
 <li>Bug 32790 ensure If Controller condition is re-evaluated each time</li>
 <li>Bug 30266 - document how to display proxy recording responses</li>
 <li>Bug 33921 - merge should not change file name</li>
 <li>Close file now gives chance to save changes</li>
 <li>Bug 33559 - fixes to Runtime Controller</li>
 </ul>
 <h4>Other changes:</h4>
 <ul>
 <li>To help with variable evaluation, JMeterThread sets "sampling started" a bit earlier (see jmeter.properties)</li>
 <li>Bug 33796 - delete cookies with null/empty values</li>
 <li>Better checking of parameter count in JavaScript function</li>
 <li>Thread Group now defaults to 1 loop instead of forever</li>
 <li>All Beanshell access is now via a single class; only need BSH jar at run-time</li>
 <li>Bug 32464 - document Direct Draw settings in jmeter.bat</li>
 <li>Bug 33919 - increase Counter field sizes</li>
 <li>Bug 32252 - ForEach was not initialising counters</li>
 </ul>
 
 <!--  ===================  -->
 
 <h3>Version 2.0.2</h3>
 <h4>New functionality:</h4>
 <ul>
 <li>While Controller</li>
 <li>BeanShell intilisation scripts</li>
 <li>Result Saver can optionally save failed results only</li>
 <li>Display as HTML has option not to download frames and images etc</li>
 <li>Multiple Tree elements can now be enabled/disabled/copied/pasted at once</li>
 <li>__split() function added</li>
 <li>(28699) allow Assertion to regard unsuccessful responses - e.g. 404 - as successful</li>
 <li>(29075) Regex Extractor can now extract data out of http response header as well as the body</li>
 <li>__log() functions can now write to stdout and stderr</li>
 <li>URL Modifier can now optionally ignore query parameters</li>
 </ul>
 <h4>Bug fixes:</h4>
 <ul>
 <li>If controller now works after the first false condition (31390)</li>
 <li>Regex GUI was losing track of Header/Body checkbox (29853)</li>
 <li>Display as HTML now handles frames and relative images</li>
 <li>Right-click open replaced by merge</li>
 <li>Fix some drag and drop problems</li>
 <li>Fixed foreach demo example so it works</li>
 <li>(30741) SSL password prompt now works again </li>
 <li>StringFromFile now closes files at end of test; start and end now optional as intended</li>
 <li>(31342) Fixed text of SOAP Sampler headers</li>
 <li>Proxy must now be stopped before it can be removed (25145)</li>
 <li>Link Parser now supports BASE href (25490)</li>
 <li>(30917) Classfinder ignores duplicate names</li>
 <li>(22820) Allow Counter value to be cleared</li>
 <li>(28230) Fix NPE in HTTP Sampler retrieving embedded resources</li>
 <li>Improve handling of StopTest; catch and log some more errors</li>
 <li>ForEach Controller no longer runs any samples if first variable is not defined</li>
 <li>(28663) NPE in remote JDBC execution</li>
 <li>(30110) Deadlock in stopTest processing</li>
 <li>(31696) Duration not working correctly when using Scheduler</li>
 <li>JMeterContext now uses ThreadLocal - should fix some potential NPE errors</li>
 </ul>
 <h3>Version 2.0.1</h3>
 <p>Bug fix release. TBA.</p>
 <h3>Version 2.0</h3>
 <ul>
 	<li>HTML parsing improved; now has choice of 3 parsers, and most embedded elements can now be detected and downloaded.</li>
 <li>Redirects can now be delegated to URLConnection by defining the JMeter property HTTPSamper.delegateRedirects=true (default is false) </li>
 <li>Stop Thread and Stop Test methods added for Samplers and Assertions etc. Samplers can call setStopThread(true) or setStopTest(true) if they detect an error that needs to stop the thread of the test after the sample has been processed </li>
 <li>Thread Group Gui now has an extra pane to specify what happens after a Sampler error: Continue (as now), Stop Thread or Stop Test. 
     This needs to be extended to a lower level at some stage. </li>
 <li>Added Shutdown to Run Menu. This is the same as Stop except that it lets the Threads finish normally (i.e. after the next sample has been completed) </li>
 <li>Remote samples can be cached until the end of a test by defining the property hold_samples=true when running the server.
 More work is needed to be able to control this from the GUI </li>
 <li>Proxy server has option to skip recording browser headers </li>
 <li>Proxy restart works better (stop waits for daemon to finish) </li>
 <li>Scheduler ignores start if it has already passed </li>
 <li>Scheduler now has delay function </li>
 <li>added Summariser test element (mainly for non-GUI) testing. This prints summary statistics to System.out and/or the log file every so oftem (3 minutes by default). Multiple summarisers can be used; samples are accumulated by summariser name. </li>
 <li>Extra Proxy Server options: 
 Create all samplers with keep-alive disabled 
 Add Separator markers between sets of samples 
 Add Response Assertion to first sampler in each set </li>
 <li>Test Plan has a comment field</li>
 	
 	<li>Help Page can now be pushed to background</li>
 	<li>Separate Function help page</li>
 	<li>New / amended functions</li>
 	<ul>
 	  <li>__property() and __P() functions</li>
 	  <li>__log() and __logn() - for writing to the log file</li>
       <li>_StringFromFile can now process a sequence of files, e.g. dir/file01.txt, dir/file02.txt etc </li>
       <li>_StringFromFile() funtion can now use a variable or function for the file name </li>
 	</ul>
 	<li>New / amended Assertions</li>
 	<ul>
         <li>Response Assertion now works for URLs, and it handles null data better </li>
         <li>Response Assertion can now match on Response Code and Response message as well </li>
 		<li>HTML Assertion using JTidy to check for well-formed HTML</li>
 	</ul>
 	<li>If Controller (not fully functional yet)</li>
 	<li>Transaction Controller (aggregates the times of its children)</li>
 	<li>New Samplers</li>
 		<ul>
 			<li>Basic BSF Sampler (optional)</li>
 			<li>BeanShell Sampler (optional, needs to be downloaded from www.beanshell.org</li>
 			<li>Basic TCP Sampler</li>
 		</ul>
      <li>Optionally start BeanShell server (allows remote access to JMeter variables and methods) </li>
 </ul>
 <h3>Version 1.9.1</h3>
 <p>TBA</p>
 <h3>Version 1.9</h3>
 <ul>
 <li>Sample result log files can now be in CSV or XML format</li>
 <li>New Event model for notification of iteration events during test plan run</li>
 <li>New Javascript function for executing arbitrary javascript statements</li>
 <li>Many GUI improvements</li>
 <li>New Pre-processors and Post-processors replace Modifiers and Response-Based Modifiers. </li>
 <li>Compatible with jdk1.3</li>
 <li>JMeter functions are now fully recursive and universal (can use functions as parameters to functions)</li>
 <li>Integrated help window now supports hypertext links</li>
 <li>New Random Function</li>
 <li>New XML Assertion</li>
 <li>New LDAP Sampler (alpha code)</li>
 <li>New Ant Task to run JMeter (in extras folder)</li>
 <li>New Java Sampler test implementation (to assist developers)</li>
 <li>More efficient use of memory, faster loading of .jmx files</li>
 <li>New SOAP Sampler (alpha code)</li>
 <li>New Median calculation in Graph Results visualizer</li>
 <li>Default config element added for developer benefit</li>
 <li>Various performance enhancements during test run</li>
 <li>New Simple File recorder for minimal GUI overhead during test run</li>
 <li>New Function: StringFromFile - grabs values from a file</li>
 <li>New Function: CSVRead - grabs multiple values from a file</li>
 <li>Functions now longer need to be encoded - special values should be escaped 
 with "\" if they are literal values</li>
 <li>New cut/copy/paste functionality</li>
 <li>SSL testing should work with less user-fudging, and in non-gui mode</li>
 <li>Mailer Model works in non-gui mode</li>
 <li>New Througput Controller</li>
 <li>New Module Controller</li>
 <li>Tests can now be scheduled to run from a certain time till a certain time</li>
 <li>Remote JMeter servers can be started from a non-gui client.  Also, in gui mode, all remote servers can be started with a single click</li>
 <li>ThreadGroups can now be run either serially or in parallel (default)</li>
 <li>New command line options to override properties</li>
 <li>New Size Assertion</li>
 
 </ul>
 
 <h3>Version 1.8.1</h3>
 <ul>
 <li>Bug Fix Release.  Many bugs were fixed.</li>
 <li>Removed redundant "Root" node from test tree.</li>
 <li>Re-introduced Icons in test tree.</li>
 <li>Some re-organization of code to improve build process.</li>
 <li>View Results Tree has added option to view results as web document (still buggy at this point).</li>
 <li>New Total line in Aggregate Listener (still buggy at this point).</li>
 <li>Improvements to ability to change JMeter's Locale settings.</li>
 <li>Improvements to SSL Manager.</li>
 </ul>
 
 <h3>Version 1.8</h3>
 <ul>
 <li>Improvement to Aggregate report's calculations.</li>
 <li>Simplified application logging.</li>
 <li>New Duration Assertion.</li>
 <li>Fixed and improved Mailer Visualizer.</li>
 <li>Improvements to HTTP Sampler's recovery of resources (sockets and file handles).</li>
 <li>Improving JMeter's internal handling of test start/stop.</li>
 <li>Fixing and adding options to behavior of Interleave and Random Controllers.</li>
 <li>New Counter config element.</li>
 <li>New User Parameters config element.</li>
 <li>Improved performance of file opener.</li>
 <li>Functions and other elements can access global variables.</li>
 <li>Help system available within JMeter's GUI.</li>
 <li>Test Elements can be disabled.</li>
 <li>Language/Locale can be changed while running JMeter (mostly).</li>
 <li>View Results Tree can be configured to record only errors.</li>
 <li>Various bug fixes.</li>
 </ul>
 
 <b>Changes: for more info, contact <a href="mailto:mstover1@apache.org">Michael Stover</a></b>
 <h3>Version 1.7.3</h3>
 <ul>
 <li>New Functions that provide more ability to change requests dynamically during test runs.</li>
 <li>New language translations in Japanese and German.</li>
 <li>Removed annoying Log4J error messages.</li>
 <li>Improved support for loading JMeter 1.7 version test plan files (.jmx files).</li>
 <li>JMeter now supports proxy servers that require username/password authentication.</li>
 <li>Dialog box indicating test stopping doesn't hang JMeter on problems with stopping test.</li>
 <li>GUI can run multiple remote JMeter servers (fixes GUI bug that prevented this).</li>
 <li>Dialog box to help created function calls in GUI.</li>
 <li>New Keep-alive switch in HTTP Requests to indicate JMeter should or should not use Keep-Alive for sockets.</li>
 <li>HTTP Post requests can have GET style arguments in Path field.  Proxy records them correctly now.</li>
 <li>New User-defined test-wide static variables.</li>
 <li>View Results Tree now displays more information, including name of request (matching the name
 in the test tree) and full request and POST data.</li>
 <li>Removed obsolete View Results Visualizer (use View Results Tree instead).</li>
 <li>Performance enhancements.</li>
 <li>Memory use enhancements.</li>
 <li>Graph visualizer GUI improvements.</li>
 <li>Updates and fixes to Mailer Visualizer.</li>
 </ul>
  
 <h3>Version 1.7.2</h3>
 <ul>
 <li>JMeter now notifies user when test has stopped running.</li>
 <li>HTTP Proxy server records HTTP Requests with re-direct turned off.</li>
 <li>HTTP Requests can be instructed to either follow redirects or ignore them.</li>
 <li>Various GUI improvements.</li>
 <li>New Random Controller.</li>
 <li>New SOAP/XML-RPC Sampler.</li>
 </ul>
 
 <h3>Version 1.7.1</h3>
 <ul>
 <li>JMeter's architecture revamped for a more complete separation between GUI code and
 test engine code.</li>
 <li>Use of Avalon code to save test plans to XML as Configuration Objects</li>
 <li>All listeners can save data to file and load same data at later date.</li>
 </ul>
 
 <h3>Version 1.7Beta</h3> 
 <ul> 
 	<li>Better XML support for special characters (Tushar Bhatia) </li> 
 	<li>Non-GUI functioning  &amp; Non-GUI test plan execution  (Tushar Bhatia)</li> 
 	<li>Removing Swing dependence from base JMeter classes</li> 
 	<li>Internationalization (Takashi Okamoto)</li> 
 	<li>AllTests bug fix (neth6@atozasia.com)</li> 
 	<li>ClassFinder bug fix (neth6@atozasia.com)</li> 
 	<li>New Loop Controller</li> 
 	<li>Proxy Server records HTTP samples from browser 
 		(and documented in the user manual)</li> <li>Multipart Form support</li> 
 	<li>HTTP Header class for Header customization</li> 
 	<li>Extracting HTTP Header information from responses (Jamie Davidson)</li> 
 	<li>Mailer Visualizer re-added to JMeter</li> 
 	<li>JMeter now url encodes parameter names and values</li> 
 	<li>listeners no longer give exceptions if their gui's haven't been initialized</li> 
 	<li>HTTPS and Authorization working together</li> 
 	<li>New Http sampling that automatically parses HTML response 
 		for images to download, and includes the downloading of these 
 		images in total time for request (Neth neth6@atozasia.com) </li> 
 	<li>HTTP responses from server can be parsed for links and forms, 
 		and dynamic data can be extracted and added to test samples 
 		at run-time (documented)</li>  
 	<li>New Ramp-up feature (Jonathan O'Keefe)</li> 
 	<li>New visualizers (Neth)</li> 
 	<li>New Assertions for functional testing</li> 
 </ul>  
 
 <h3>Version 1.6.1</h3> 
 <ul> 
 	<li>Fixed saving and loading of test scripts (no more extra lines)</li> 
 	<li>Can save and load special characters (such as &quot;&amp;&quot; and &quot;&lt;&quot;).</li> 
 	<li>Can save and load timers and listeners.</li> 
 	<li>Minor bug fix for cookies (if you cookie value 
 		contained an &quot;=&quot;, then it broke).</li> 
 	<li>URL's can sample ports other than 80, and can test HTTPS, 
 		provided you have the necessary jars (JSSE)</li> 
 </ul> 
 
 <h3>Version 1.6 Alpha</h3> 
 <ul> 
 	<li>New UI</li> 
 	<li>Separation of GUI and Logic code</li> 	
 	<li>New Plug-in framework for new modules</li> 
 	<li>Enhanced performance</li> 
 	<li>Layering of test logic for greater flexibility</li> 
 	<li>Added support for saving of test elements</li> 
 	<li>Added support for distributed testing using a single client</li> 
 
 </ul> 
 <h3>Version 1.5.1</h3> 
 <ul> 
 	<li>Fixed bug that caused cookies not to be read if header name case not as expected.</li> 
 	<li>Clone entries before sending to sampler - prevents relocations from messing up 
 		information across threads</li> 
 	<li>Minor bug fix to convenience dialog for adding paramters to test sample.  
 		Bug prevented entries in dialog from appearing in test sample.</li> 
 	<li>Added xerces.jar to distribution</li> 
 	<li>Added junit.jar to distribution and created a few tests.</li> 
 	<li>Started work on new framework.  New files in cvs, but do not effect program yet.</li> 
 	<li>Fixed bug that prevent HTTPJMeterThread from delaying according to chosen timer.</li> 
 </ul>  
 <p> 
 <h3>Version 1.5</h3> 
 <ul>   
 	<li>Abstracted out the concept of the Sampler, SamplerController, and TestSample.   
 		A Sampler represents code that understands a protocol (such as HTTP, 
 		or FTP, RMI,   SMTP, etc..).  It is the code that actually makes the 
 		connection to whatever is   being tested.   A SamplerController 
 		represents code that understands how to organize and run a group   
 		of test samples.  It is what binds together a Sampler and it's test 
 		samples and runs them.   A TestSample represents code that understands 
 		how to gather information from the   user about a particular test.  
 		For a website, it would represent a URL and any   information to be sent 
 		with the URL.</li>   
 	<li>The UI has been updated to make entering test samples more convenient.</li>   
 	<li>Thread groups have been added, allowing a user to setup multiple test to run   
 		concurrently, and to allow sharing of test samples between those tests.</li>   
 	<li>It is now possible to save and load test samples.</li>   
 	<li>....and many more minor changes/improvements...</li> 
 </ul> 
 </p> 
 <p> 
 <b>Apache JMeter 1.4.1-dev</b> (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Cleaned up URLSampler code after tons of patches for better readability. (SM)</li>
    <li>Made JMeter send a special &quot;user-agent&quot; identifier. (SM)</li>
    <li>Fixed problems with redirection not sending cookies and authentication info and removed
      a warning with jikes compilation. Thanks to <a href="mailto:wtanaka@yahoo.com">Wesley
      Tanaka</a> for the patches (SM)</li>
    <li>Fixed a bug in the URLSampler that caused to skip one URL when testing lists of URLs and
      a problem with Cookie handling. Thanks to <a
      href="mailto:gjohnson@investlearning.com">Graham Johnson</a> for the patches (SM)</li>
    <li>Fixed a problem with POST actions. Thanks to <a href="mailto:sschaub@bju.edu">Stephen
      Schaub</a> for the patch (SM)</li>
  </ul>
  </p>
  <p>
  <b>Apache JMeter 1.4</b> - Jul 11 1999 (<a href="mailto:cimjpno@be.ibm.com">Jean-Pierre Norguet</a>,
  <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)
   <ul>
    <li>Fixed a problem with POST actions. Thanks to <a href="mailto:bburns@labs.gte.com">Brendan
      Burns</a> for the patch (SM)</li>
    <li>Added close button to the About box for those window managers who don't provide it.
      Thanks to Jan-Henrik Haukeland for pointing it out. (SM)</li>
    <li>Added the simple Spline sample visualizer (JPN)</li> 
 </ul> </p>
   <p><b>Apache JMeter 1.3</b> - Apr 16 1999
   (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>,
  <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>)
 <ul>
    <li>Run the Garbage Collector and run finalization before starting to sampling to ensure
      same state every time (SM)</li>
    <li>Fixed some NullPointerExceptions here and there (SM)</li>
    <li>Added HTTP authentication capabilities (RL)</li>
    <li>Added windowed sample visualizer (SM)</li>
    <li>Fixed stupid bug for command line arguments. Thanks to <a
      href="mailto:jbracer@infoneers.com">Jorge Bracer</a> for pointing this out (SM)</li> 
 </ul> </p>
   <p><b>Apache JMeter 1.2</b> - Mar 17 1999 (<a href="mailto:sdowd@arcmail.com">Sean Dowd</a>, 
 <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Integrated cookie capabilities with JMeter (SM)</li>
    <li>Added the Cookie manager and Netscape file parser (SD)</li>
    <li>Fixed compilation error for JDK 1.1 (SD)</li> </ul> </p>  
 <p> <b>Apache JMeter 1.1</b> - Feb 24 1999 (<a href="mailto:sdowd@arcmail.com">Sean Dowd</a>, 
 <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Created the opportunity to create URL aliasing from the properties file as well as the
      ability to associate aliases to URL sequences instead of single URLs (SM) Thanks to <a
      href="mailto:chatfield@evergreen.com">Simon Chatfield</a> for the very nice suggestions
      and code examples.</li>
    <li>Removed the TextVisualizer and replaced it with the much more useful FileVisualizer (SM)</li>
    <li>Added the known bug list (SM)</li>
    <li>Removed the Java Apache logo (SM)</li>
    <li>Fixed a couple of typos (SM)</li>
    <li>Added UNIX makefile (SD)</li> </ul> </p> 
 <p> <b>Apache JMeter 1.0.1</b> - Jan 25 1999 (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>)  
 <ul>
    <li>Removed pending issues doc issues (SM)</li>
    <li>Fixed the unix script (SM)</li>
    <li>Added the possibility of running the JAR directly using &quot;java -jar
      ApacheJMeter.jar&quot; with Java 2 (SM)</li>
    <li>Some small updates: fixed Swing location after Java 2(tm) release, license update and
      small cleanups (SM)</li> 
 </ul> </p> 
 <p> <b>Apache JMeter 1.0</b> - Dec 15 1998 (<a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>) 
 <ul>
    <li>Initial version. (SM)</li> 
 </ul> </p> 
 </section> 
 </body> 
 </document>
