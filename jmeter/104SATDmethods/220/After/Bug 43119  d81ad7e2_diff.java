diff --git a/docs/images/screenshots/savetofile.png b/docs/images/screenshots/savetofile.png
index f39677595..899586e9d 100644
Binary files a/docs/images/screenshots/savetofile.png and b/docs/images/screenshots/savetofile.png differ
diff --git a/src/core/org/apache/jmeter/reporters/ResultSaver.java b/src/core/org/apache/jmeter/reporters/ResultSaver.java
index 515e269a9..909722abe 100644
--- a/src/core/org/apache/jmeter/reporters/ResultSaver.java
+++ b/src/core/org/apache/jmeter/reporters/ResultSaver.java
@@ -1,230 +1,241 @@
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
 
 package org.apache.jmeter.reporters;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.Serializable;
 
 import org.apache.commons.lang.text.StrBuilder;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.threads.JMeterContextService;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * Save Result responseData to a set of files
  *
  *
  * This is mainly intended for validation tests
  *
  */
 // TODO - perhaps save other items such as headers?
 public class ResultSaver extends AbstractTestElement implements Serializable, SampleListener, Clearable {
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // File name sequence number
     //@GuardedBy("this")
     private static long sequenceNumber = 0;
 
     public static final String FILENAME = "FileSaver.filename"; // $NON-NLS-1$
 
     public static final String VARIABLE_NAME = "FileSaver.variablename"; // $NON-NLS-1$
 
     public static final String ERRORS_ONLY = "FileSaver.errorsonly"; // $NON-NLS-1$
 
     public static final String SUCCESS_ONLY = "FileSaver.successonly"; // $NON-NLS-1$
 
+    public static final String SKIP_AUTO_NUMBER = "FileSaver.skipautonumber"; // $NON-NLS-1$
+
     private synchronized long nextNumber() {
         return ++sequenceNumber;
     }
 
     /*
      * Constructor is initially called once for each occurrence in the test plan
      * For GUI, several more instances are created Then clear is called at start
      * of test Called several times during test startup The name will not
      * necessarily have been set at this point.
      */
     public ResultSaver() {
         super();
         // log.debug(Thread.currentThread().getName());
         // System.out.println(">> "+me+" "+this.getName()+"
         // "+Thread.currentThread().getName());
     }
 
     /*
      * Constructor for use during startup (intended for non-GUI use) @param name
      * of summariser
      */
     public ResultSaver(String name) {
         this();
         setName(name);
     }
 
     /*
      * This is called once for each occurrence in the test plan, before the
      * start of the test. The super.clear() method clears the name (and all
      * other properties), so it is called last.
      */
     // TODO: is this clearData, clearGui or TestElement.clear() ?
     public void clear() {
         // System.out.println("-- "+me+this.getName()+"
         // "+Thread.currentThread().getName());
         super.clear();
         synchronized(this){
             sequenceNumber = 0; // TODO is this the right thing to do?
         }
     }
 
     // TODO - is this the same as the above?
     public void clearData() {
     }
 
     /**
      * Saves the sample result (and any sub results) in files
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
      */
     public void sampleOccurred(SampleEvent e) {
       processSample(e.getResult(), new Counter());
    }
 
    /**
     * Recurse the whole (sub)result hierarchy.
     *
     * @param s Sample result
     * @param c sample counter
     */
    private void processSample(SampleResult s, Counter c) {
         saveSample(s, c.num++);
         SampleResult[] sr = s.getSubResults();
         for (int i = 0; i < sr.length; i++) {
             processSample(sr[i], c);
         }
     }
 
     /**
      * @param s SampleResult to save
      * @param num number to append to variable (if >0)
      */
     private void saveSample(SampleResult s, int num) {
         // Should we save the sample?
         if (s.isSuccessful()){
             if (getErrorsOnly()){
                 return;
             }
         } else {
             if (getSuccessOnly()){
                 return;
             }
         }
 
-        String fileName = makeFileName(s.getContentType());
+        String fileName = makeFileName(s.getContentType(), getSkipAutoNumber());
         log.debug("Saving " + s.getSampleLabel() + " in " + fileName);
         s.setResultFileName(fileName);// Associate sample with file name
         String variable = getVariableName();
         if (variable.length()>0){
             if (num > 0) {
                 StrBuilder sb = new StrBuilder(variable);
                 sb.append(num);
                 variable=sb.toString();
             }
             JMeterContextService.getContext().getVariables().put(variable, fileName);
         }
         File out = new File(fileName);
         FileOutputStream pw = null;
         try {
             pw = new FileOutputStream(out);
             pw.write(s.getResponseData());
         } catch (FileNotFoundException e1) {
             log.error("Error creating sample file for " + s.getSampleLabel(), e1);
         } catch (IOException e1) {
             log.error("Error saving sample " + s.getSampleLabel(), e1);
         } finally {
             JOrphanUtils.closeQuietly(pw);
         }
     }
 
     /**
      * @return fileName composed of fixed prefix, a number, and a suffix derived
      *         from the contentType e.g. Content-Type:
      *         text/html;charset=ISO-8859-1
      */
-    private String makeFileName(String contentType) {
+    private String makeFileName(String contentType, boolean skipAutoNumber) {
         String suffix = "unknown";
         if (contentType != null) {
-            int i = contentType.indexOf("/");
+            int i = contentType.indexOf("/"); // $NON-NLS-1$
             if (i != -1) {
-                int j = contentType.indexOf(";");
+                int j = contentType.indexOf(";"); // $NON-NLS-1$
                 if (j != -1) {
                     suffix = contentType.substring(i + 1, j);
                 } else {
                     suffix = contentType.substring(i + 1);
                 }
             }
         }
-        return getFilename() + nextNumber() + "." + suffix;
+        if (skipAutoNumber) {
+            return getFilename() + "." + suffix; // $NON-NLS-1$
+        }
+        else {
+            return getFilename() + nextNumber() + "." + suffix; // $NON-NLS-1$
+        }
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleStarted(org.apache.jmeter.samplers.SampleEvent)
      */
     public void sampleStarted(SampleEvent e) {
         // not used
     }
 
     /*
      * (non-Javadoc)
      *
      * @see org.apache.jmeter.samplers.SampleListener#sampleStopped(org.apache.jmeter.samplers.SampleEvent)
      */
     public void sampleStopped(SampleEvent e) {
         // not used
     }
 
     private String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     private String getVariableName() {
         return getPropertyAsString(VARIABLE_NAME,""); // $NON-NLS-1$
     }
 
     private boolean getErrorsOnly() {
         return getPropertyAsBoolean(ERRORS_ONLY);
     }
 
+    private boolean getSkipAutoNumber() {
+        return getPropertyAsBoolean(SKIP_AUTO_NUMBER);
+    }
+
     private boolean getSuccessOnly() {
         return getPropertyAsBoolean(SUCCESS_ONLY);
     }
 
     // Mutable int to keep track of sample count
     private static class Counter{
         int num;
     }
 }
diff --git a/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java b/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
index 6ab82f461..140b5a42f 100644
--- a/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
+++ b/src/core/org/apache/jmeter/reporters/gui/ResultSaverGui.java
@@ -1,157 +1,164 @@
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
 
 package org.apache.jmeter.reporters.gui;
 
 import java.awt.BorderLayout;
 
 import javax.swing.Box;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 
 import org.apache.jmeter.reporters.ResultSaver;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
 
 /**
  * Create a ResultSaver test element, which saves the sample information in set
  * of files
  *
  */
 public class ResultSaverGui extends AbstractListenerGui implements Clearable {
 
     private JTextField filename;
 
     private JTextField variableName;
 
     private JCheckBox errorsOnly;
 
     private JCheckBox successOnly;
 
+    private JCheckBox skipAutoNumber;
+
     public ResultSaverGui() {
         super();
         init();
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
      */
     public String getLabelResource() {
         return "resultsaver_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(TestElement)
      */
     public void configure(TestElement el) {
         super.configure(el);
         filename.setText(el.getPropertyAsString(ResultSaver.FILENAME));
         errorsOnly.setSelected(el.getPropertyAsBoolean(ResultSaver.ERRORS_ONLY));
         successOnly.setSelected(el.getPropertyAsBoolean(ResultSaver.SUCCESS_ONLY));
+        skipAutoNumber.setSelected(el.getPropertyAsBoolean(ResultSaver.SKIP_AUTO_NUMBER));
         variableName.setText(el.getPropertyAsString(ResultSaver.VARIABLE_NAME,""));
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     public TestElement createTestElement() {
         ResultSaver resultSaver = new ResultSaver();
         modifyTestElement(resultSaver);
         return resultSaver;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     public void modifyTestElement(TestElement te) {
         super.configureTestElement(te);
         te.setProperty(ResultSaver.FILENAME, filename.getText());
         te.setProperty(ResultSaver.ERRORS_ONLY, errorsOnly.isSelected());
+        te.setProperty(ResultSaver.SKIP_AUTO_NUMBER, skipAutoNumber.isSelected());
         te.setProperty(ResultSaver.SUCCESS_ONLY, successOnly.isSelected());
         AbstractTestElement at = (AbstractTestElement) te;
         at.setProperty(ResultSaver.VARIABLE_NAME, variableName.getText(),""); //$NON-NLS-1$
     }
 
     /**
      * Implements JMeterGUIComponent.clearGui
      */
     public void clearGui() {
         super.clearGui();
 
+        skipAutoNumber.setSelected(false);
         filename.setText(""); //$NON-NLS-1$
         errorsOnly.setSelected(false);
         successOnly.setSelected(false);
         variableName.setText(""); //$NON-NLS-1$
     }
 
     private void init() {
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         Box box = Box.createVerticalBox();
         box.add(makeTitlePanel());
         box.add(createFilenamePrefixPanel());
         box.add(createVariableNamePanel());
         errorsOnly = new JCheckBox(JMeterUtils.getResString("resultsaver_errors")); // $NON-NLS-1$
         box.add(errorsOnly);
         successOnly = new JCheckBox(JMeterUtils.getResString("resultsaver_success")); // $NON-NLS-1$
         box.add(successOnly);
+        skipAutoNumber = new JCheckBox(JMeterUtils.getResString("resultsaver_skipautonumber")); // $NON-NLS-1$
+        box.add(skipAutoNumber);
         add(box, BorderLayout.NORTH);
     }
 
     private JPanel createFilenamePrefixPanel()
     {
         JLabel label = new JLabel(JMeterUtils.getResString("resultsaver_prefix")); // $NON-NLS-1$
 
         filename = new JTextField(10);
         filename.setName(ResultSaver.FILENAME);
         label.setLabelFor(filename);
 
         JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
         filenamePanel.add(label, BorderLayout.WEST);
         filenamePanel.add(filename, BorderLayout.CENTER);
         return filenamePanel;
     }
 
 
     private JPanel createVariableNamePanel()
     {
         JLabel label = new JLabel(JMeterUtils.getResString("resultsaver_variable")); // $NON-NLS-1$
 
         variableName = new JTextField(10);
         variableName.setName(ResultSaver.VARIABLE_NAME);
         label.setLabelFor(variableName);
 
         JPanel filenamePanel = new JPanel(new BorderLayout(5, 0));
         filenamePanel.add(label, BorderLayout.WEST);
         filenamePanel.add(variableName, BorderLayout.CENTER);
         return filenamePanel;
     }
 
 
     // Needed to avoid Class cast error in Clear.java
     public void clearData() {
     }
 
 }
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index e8513674e..fa6a40342 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,916 +1,917 @@
 #   Licensed to the Apache Software Foundation (ASF) under one or more
 #   contributor license agreements.  See the NOTICE file distributed with
 #   this work for additional information regarding copyright ownership.
 #   The ASF licenses this file to You under the Apache License, Version 2.0
 #   (the "License"); you may not use this file except in compliance with
 #   the License.  You may obtain a copy of the License at
 # 
 #       http://www.apache.org/licenses/LICENSE-2.0
 # 
 #   Unless required by applicable law or agreed to in writing, software
 #   distributed under the License is distributed on an "AS IS" BASIS,
 #   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #   See the License for the specific language governing permissions and
 #   limitations under the License.
 
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
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_use_group_name=Include group name in label?
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
 ajp_sampler_title=AJP/1.3 Sampler
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
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_not=Not
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_resp_field=Response Field to Test
 assertion_substring=Substring
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
 bsf_script=Script to run (variables: log, Label, FileName, Parameters, args[], SampleResult, ctx, vars, props, OUT)
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script (see below for variables that are defined)
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, vars, props, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (see below for variables that are defined)
 bsh_script_variables=The following variables are defined for the script:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 bsh_script_reset_interpreter=Reset bsh.Interpreter before each call
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 char_value=Unicode character number (decimal or 0xhex)
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_per_iter=Clear cache each iteration?
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
 deltest=Deletion test
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
 enable=Enable
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 error_loading_help=Error loading help page
 error_occurred=Error Occurred
 error_title=Error
 es=Spanish
 escape_html_string=String to escape
 eval_name_param=Text containing variable and function references
 evalvar_name_param=Name of variable
 example_data=Sample Data
 example_title=Example Sampler
 exit=Exit
 expiration=Expiration
 field_name=Field name
 file=File
 file_already_in_use=That file is already in use
 file_visualizer_append=Append to Existing Data File
 file_visualizer_auto_flush=Automatically Flush After Each Data Sample
 file_visualizer_browse=Browse...
 file_visualizer_close=Close
 file_visualizer_file_options=File Options
 file_visualizer_filename=Filename
 file_visualizer_flush=Flush
 file_visualizer_missing_filename=No output filename specified.
 file_visualizer_open=Open
 file_visualizer_output_file=Write results to file / Read from file
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
 ftp_binary_mode=Use Binary mode ?
 ftp_get=get(RETR)
 ftp_local_file=Local File:
 ftp_local_file_contents=Local File Contents:
 ftp_remote_file=Remote File:
 ftp_sample_title=FTP Request Defaults
 ftp_save_response_data=Save File in Response ?
 ftp_testing_title=FTP Request
 ftp_put=put(STOR)
 function_dialog_menu_item=Function Helper Dialog
 function_helper_title=Function Helper
 function_name_param=Name of variable in which to store the result (required)
 function_name_paropt=Name of variable in which to store the result (optional)
 function_params=Function Parameters
 functional_mode=Functional Test Mode (i.e. save Response Data and Sampler Data)
 functional_mode_explanation=Selecting Functional Test Mode may adversely affect performance.
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
 html_assertion_file=Write JTidy report to file
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
 if_controller_evaluate_all=Evaluate for all children?
 if_controller_label=Condition (Javascript)
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
 invalid_variables=Invalid variables
 iteration_counter_arg_1=TRUE, for each user to have own counter, FALSE for a global counter
 iterator_num=Loop Count\:
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 javascript_expression=JavaScript expression to evaluate
 jexl_expression=JEXL expression to evaluate
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
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
 jms_use_req_msgid_as_correlid=Use Request Message Id As Correlation Id
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
 log_errors_only=Errors
 log_file=Location of log File
 log_function_comment=Additional comment (optional)
 log_function_level=Log level (default INFO) or OUT or ERR
 log_function_string=String to be logged
 log_function_string_ret=String to be logged (and returned)
 log_function_throwable=Throwable text (optional)
 log_only=Log/Display Only:
 log_parser=Name of Log Parser class
 log_parser_cnf_msg=Could not find the class. Please make sure you place your jar file in the /lib directory.
 log_parser_illegal_msg=Could not access the class due to IllegalAcessException.
 log_parser_instantiate_msg=Could not create an instance of the log parser. Please make sure the parser implements LogParser interface.
 log_sampler=Tomcat Access Log Sampler
 log_success_only=Successes
 logic_controller_title=Simple Controller
 login_config=Login Configuration
 login_config_element=Login Config Element
 longsum_param_1=First long to add
 longsum_param_2=Second long to add - further longs can be summed by adding further arguments.
 loop_controller_title=Loop Controller
 looping_control=Looping Control
 lower_bound=Lower Bound
 mail_reader_account=Username:
 mail_reader_all_messages=All
 mail_reader_delete=Delete messages from the server
 mail_reader_folder=Folder:
 mail_reader_imap=IMAP
 mail_reader_imaps=IMAPS
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_pop3=POP3
 mail_reader_pop3s=POP3S
 mail_reader_server=Server:
 mail_reader_server_type=Server Type:
 mail_reader_storemime=Store the message using MIME
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
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
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
 module_controller_module_to_run=Module To Run 
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
 patterns_to_exclude=URL Patterns to Exclude
 patterns_to_include=URL Patterns to Include
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
 property_visualiser_title=Property Display
 protocol=Protocol (default http)\:
 protocol_java_border=Java class
 protocol_java_classname=Classname\:
 protocol_java_config_tile=Configure Java Sample
 protocol_java_test_title=Java Testing
 provider_url=Provider URL
 proxy_assertions=Add Assertions
 proxy_cl_error=If specifying a proxy server, host and port must be given
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_headers=Capture HTTP Headers
 proxy_httpsspoofing=Attempt HTTPS Spoofing
 proxy_httpsspoofing_match=Optional URL match string:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP Proxy Server
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
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
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
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_errors=Save Failed Responses only
 resultsaver_prefix=Filename prefix\:
 resultsaver_success=Save Successful Responses only
+resultsaver_skipautonumber=Don\'t add number to prefix
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
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
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save byte count
 save_code=Save Response Code
 save_datatype=Save Data Type
 save_encoding=Save Encoding
 save_fieldnames=Save Field Names (CSV)
 save_filename=Save Response Filename
 save_graphics=Save Graph
 save_hostname=Save Hostname
 save_label=Save Label
 save_latency=Save Latency
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplerdata=Save Sampler Data (XML)
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_samplecount=Save Sample and Error Counts
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
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path\:
 send_file_mime_label=MIME Type\:
 send_file_param_name_label=Parameter Name\:
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
 split_function_string=String to split
 split_function_separator=String to split on. Default is , (comma).
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
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 table_visualizer_bytes=Bytes
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 template_field=Template\:
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
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
 testplan.serialized=Run Thread Groups consecutively (i.e. run groups one at a time)
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
 tr=Turkish
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 unbind=Thread Unbind
 unescape_string=String containing Java escapes
 unescape_html_string=String to unescape
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
 use_multipart_for_http_post=Use multipart/form-data for HTTP POST
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
 view_results_render_embedded=Download embedded resources
 view_results_render_html=Render HTML
 view_results_render_json=Render JSON
 view_results_render_text=Show Text
 view_results_render_xml=Render XML
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_tab_assertion=Assertion result
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 web_testing2_title=HTTP Request HTTPClient
 webservice_proxy_host=Proxy Host
 webservice_proxy_note=If Use HTTP Proxy is checked, but no host or port are provided, the sampler
 webservice_proxy_note2=will look at command line options. If no proxy host or port are provided by
 webservice_proxy_note3=either, it will fail silently.
 webservice_proxy_port=Proxy Port
 webservice_sampler_title=WebService(SOAP) Request
 webservice_soap_action=SOAPAction
 webservice_timeout=Timeout:
 webservice_use_proxy=Use HTTP Proxy
 while_controller_label=Condition (function or variable)
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
 xpath_extractor_namespace=Use Namespaces?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_extractor_tolerant=Use Tidy ?
 xpath_file_file_name=XML file to get values from 
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
 # Please add new entries in alphabetical order
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index ee49e1136..b2ead067d 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,147 +1,148 @@
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
 	<title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 <!--  ===================  -->
 
 <h2>Version 2.3.3</h2>
 
 <h3>Summary of main changes</h3>
 
 <h4>Bug fixes</h4>
 <p>
 <ul>
 </ul>
 </p>
 
 <h4>Improvements</h4>
 
 <p>
 <ul>
 </ul>
 
 </p>
 
 <!--  ========================= End of summary ===================================== -->
 
 <h3>Known bugs</h3>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves OK under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 To override the default local language, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h3>Incompatible changes</h3>
 <p>
 The test elements "Save Results to a file" and "Generate Summary Results" are now shown as Listeners.
 They were previously shown as Post-Processors, even though they are implemented as Listeners.
 </p>
 <p>
 The Counter element is now shown as a Configuration element.
 It was previously shown as a Pre-Processor, even though it is implemented as a Config item.
 </p>
 <p>
 The above changes only affect the icons that are displayed and the locations in the GUI pop-up menus.
 They do not affect test plans or test behaviour.
 </p>
 <p>
 The PreProcessors are now invoked directly by the JMeterThread class,
 rather than by the TestCompiler#configureSampler() method. (JMeterThread handles the PostProcessors).
 This does not affect test plans or behaviour, but could perhaps affect 3rd party add-ons (very unlikely).
 </p>
 <p>
 Moved the Scoping Rules sub-section from Section 3. "Building a Test Plan"  to Section 4. "Elements of a test plan"
 </p>
 
 <h3>Bug fixes</h3>
 <ul>
 <li>Bug 45199 - don't try to replace blank variables in Proxy recording</li>
 <li>The "prev" and "sampler" objects are now defined for BSF test elements</li>
 <li>Prompt to overwrite an existing file when first saving a new test plan</li>
 <li>The test element "Save Results to a file" is now shown as a Listener</li>
 <li>Amend TestBeans to show the correct popup menu for Listeners</li>
 <li>Fix NPE when using nested Transaction Controllers with parent samples</li>
 <li>Bug 45185 - CSV dataset blank delimiter causes OOM</li>
 <li>Bug 43791 - ensure QueueReceiver is closed</li>
 <li>Fix NPE (in DataSourceElement) when using JDBC in client-server mode</li>
 <li>Bug 45749 - Response Assertion does not work with a substring that is not a valid RE</li>
 <li>Mailer Visualizer documentation now agrees with code i.e. failure/success counts need to be exceeded.</li>
 <li>Mailer Visualizer now shows the failure count</li>
 <li>Fix incorrect GUI classifications: 
 "Save Results to a file" and "Generate Summary Results" are now shown as Listeners.
 "Counter" is now shown as a Configuration element.
 </li>
 <li>HTTPSamplers can now use variables in POSTed file names</li>
 <li>Bug 45831 - WS Sampler reports incorrect throughput if SOAP packet creation fails</li>
 <li>Bug 45887 - TCPSampler: timeout property incorrectly set</li>
 <li>Bug 45928 - AJP/1.3 Sampler doesn't retrieve his label from messages.properties</li>
 <li>Bug 45904 - Allow 'Not' Response Assertion to succeed with null sample</li>
 <li>HTTP and SOAP sampler character encodings updated to be more consistent</li>
 </ul>
 
 <h3>Improvements</h3>
 <ul>
 <li>LDAP result data now formatted with line breaks</li>
 <li>Add OUT variable to jexl function</li>
 <li>Save Responses to a file can save the generated filename(s) to variables.</li>
 <li>Add BSF Listener element</li>
 <li>Bug 45200 - MailReaderSampler: store the whole MIME message in the SamplerResult</li>
 <li>Added __char() function: allows arbitrary Unicode characters to be entered in fields.</li>
 <li>Added __unescape() function: allows Java-escaped strings to be used.</li>
 <li>Add Body (unescaped) source option to Regular Expression Extractor.</li>
 <li>Added __unescapeHtml() function: decodes Html-encoded text.</li>
 <li>Added __escapeHtml() function: encodes text using Html-encoding.</li>
 <li>Allow spaces in JMeter path names (apply work-round for Java bug 4496398)</li>
 <li>Bug 45694 - Support GZIP compressed logs</li>
 <li>Random Variable - new configuration element to create random numeric variables</li>
 <li>Bug 45929 - improved French translations</li>
 <li>Bug 45571 - JMS Sampler correlation enhancement</li>
 <li>Bug 45479 - Support for multiple HTTP Header Manager nodes</li>
+<li>Bug 43119 - Save Responses to file: optionally omit the file number</li>
 </ul>
 
 <h3>Non-functional changes</h3>
 <ul>
 <li>Introduce AbstractListenerGui class to make it easier to create Listeners with no visual output</li>
 <li>Assertions run after PostProcessors; change order of pop-up menus accordingly</li>
 <li>Remove unnecessary clone() methods from function classes</li>
 <li>Moved PreProcessor invocation to JMeterThread class</li>
 <li>Made HashTree Map field final</li>
 </ul>
 </section> 
 </body> 
 </document>
diff --git a/xdocs/images/screenshots/savetofile.png b/xdocs/images/screenshots/savetofile.png
index f39677595..899586e9d 100644
Binary files a/xdocs/images/screenshots/savetofile.png and b/xdocs/images/screenshots/savetofile.png differ
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index 80533bb96..44b9a965f 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -1174,2029 +1174,2032 @@ public class myTestCase {&lt;br>
   }&lt;br>
 }&lt;br>
 By default, Jmeter will provide some default values for the success/failure code and message. Users should define a set of unique success and failure codes and use them uniformly across all tests.&lt;br>
 General Guidelines<br></br>
 If you use setUp and tearDown, make sure the methods are declared public. If you do not, the test may not run properly.
 <br></br>
 Here are some general guidelines for writing Junit tests so they work well with Jmeter. Since Jmeter runs multi-threaded, it is important to keep certain things in mind.&lt;br>
 &lt;br>
 1. Write the setUp and tearDown methods so they are thread safe. This generally means avoid using static memebers.&lt;br>
 2. Make the test methods discrete units of work and not long sequences of actions. By keeping the test method to a descrete operation, it makes it easier to combine test methods to create new test plans.&lt;br>
 3. Avoid making test methods depend on each other. Since Jmeter allows arbitrary sequencing of test methods, the runtime behavior is different than the default Junit behavior.&lt;br>
 4. If a test method is configurable, be careful about where the properties are stored. Reading the properties from the Jar file is recommended.&lt;br>
 5. Each sampler creates an instance of the test class, so write your test so the setup happens in oneTimeSetUp and oneTimeTearDown.
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Package filter" required="">Comma separated list of packages to show. Example, org.apache.jmeter,junit.framework.</property>
   <property name="Class name" required="">Fully qualified name of the JUnit test class.</property>
   <property name="Constructor string" required="">String pass to the string constructor. If a string is set, the sampler will use the
    string constructor instead of the empty constructor.</property>
   <property name="Test method" required="">The method to test.</property>
   <property name="Success message" required="">A descriptive message indicating what success means.</property>
   <property name="Success code" required="">An unique code indicating the test was successful.</property>
   <property name="Failure message" required="">A descriptive message indicating what failure means.</property>
   <property name="Failure code" required="">An unique code indicating the test failed.</property>
   <property name="Error message" required="">A description for errors.</property>
   <property name="Error code" required="">Some code for errors. Does not need to be unique.</property>
   <property name="Do not call setUp and tearDown" required="">Set the sampler not to call setUp and tearDown.
    By default, setUp and tearDown should be called. Not calling those methods could affect the test and make it inaccurate.
     This option should only be used with calling oneTimeSetUp and oneTimeTearDown. If the selected method is oneTimeSetUp or oneTimeTearDown,
      this option should be checked.</property>
 </properties>
 </component>
 
 <component name="Mail Reader Sampler"  index="&sect-num;.1.17"  width="340" height="365" screenshot="mailreader_sampler.png">
 <description>
 <p>
 The Mail Reader Sampler can read (and optionally delete) mail messages using POP3(S) or IMAP(S) protocols.
 </p>
 <note>
 The sampler requires the JavaMail and JAF jars to be available on the classpath.
 To use POP3S or IMAPS requires a recent version of JavaMail (e.g. JavaMail 1.4.1 and JAF 1.1.1).
 </note>
 </description>
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="Server Type" required="Yes">The protocol used by the server: POP3, POP3S, IMAP, IMAPS</property>
 <property name="Server" required="Yes">Hostname or IP address of the server</property>
 <property name="Username" required="">User login name</property>
 <property name="Password" required="">User login password (N.B. this is stored unencrypted in the test plan)</property>
 <property name="Folder" required="Yes, if using IMAP(S)">The IMAP(S) folder to use</property>
 <property name="Number of messages to retrieve" required="Yes">Set this to retrieve all or some messages</property>
 <property name="Delete messages from the server" required="Yes">If set, messages will be deleted after retrieval</property>
 <property name="Store the message using MIME" required="Yes">Whether to store the message as MIME. If not, fewer headers are stored (Date, To, From, Subject).</property>
 </properties>
 </component>
 
 <component name="Test Action" index="&sect-num;.1.18"  width="351" height="182" screenshot="test_action.png">
 <description>
 The Test Action sampler is a sampler that is intended for use in a conditional controller.
 Rather than generate a sample, the test element eithers pauses - or stops the selected target.
 <p>This sampler can also be useful in conjunction with the Transaction Controller, as it allows
 pauses to be included without needing to generate a sample. 
 For variable delays, set the pause time to zero, and add a Timer as a child.</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Target" required="Yes">Current Thread / All Threads (ignored for Pause)</property>
   <property name="Action" required="Yes">Pause / Stop</property>
   <property name="Duration" required="Yes">How long to pause for (milliseconds)</property>
 </properties>
 </component><a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.2 Logic Controllers" anchor="logic_controllers">
 <description>
 	<br>Logic Controllers determine the order in which Samplers are processed.</br>
 </description>
 
 <component name="Simple Controller" index="&sect-num;.2.1" anchor="simple_controller"  width="390" height="62" screenshot="logic-controller/simple-controller.gif">
 <description>
 <p>The Simple Logic Controller lets  you organize your Samplers and other
 Logic Controllers.  Unlike other Logic Controllers, this controller provides no functionality beyond that of a
 storage device.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 <example title="Using the Simple Controller" anchor="simple_controller_example">
 <p><a href="../demos/SimpleTestPlan.jmx">Download</a> this example (see Figure 6).
 In this example, we created a Test Plan that sends two Ant HTTP requests and two
 Log4J HTTP requests.  We grouped the Ant and Log4J requests by placing them inside
 Simple Logic Controllers.  Remember, the Simple Logic Controller has no effect on how JMeter
 processes the controller(s) you add to it.  So, in this example, JMeter sends the requests in the
 following order: Ant Home Page, Ant News Page, Log4J Home Page, Log4J History Page.
 Note, the File Reporter
 is configured to store the results in a file named "simple-test.dat" in the current directory.</p>
 <figure width="337" height="233" image="logic-controller/simple-example.gif">Figure 6 Simple Controller Example</figure>
 
 </example>
 </component>
 
 <component name="Loop Controller" index="&sect-num;.2.2" anchor="loop"  width="397" height="111" screenshot="logic-controller/loop-controller.gif">
 <description><p>If you add Generative or Logic Controllers to a Loop Controller, JMeter will
 loop through them a certain number of times, in addition to the loop value you
 specified for the Thread Group.  For example, if you add one HTTP Request to a
 Loop Controller with a loop count of two, and configure the Thread Group loop
 count to three, JMeter will send a total of 2 * 3 = 6 HTTP Requests.
 </p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Loop Count" required="Yes, unless &quot;Forever&quot; is checked">
                 The number of times the subelements of this controller will be iterated each time
                 through a test run.
                 <p><b>Special Case:</b> The Loop Controller embedded in the <a href="test_plan.html#thread_group">Thread Group</a>
                 element behaves slightly differently.  Unless set to forever, it stops the test after
                 the given number of iterations have been done.</p></property>
 </properties>
 
 <example title="Looping Example" anchor="loop_example">
 
 <p><a href="../demos/LoopTestPlan.jmx">Download</a> this example (see Figure 4).
 In this example, we created a Test Plan that sends a particular HTTP Request
 only once and sends another HTTP Request five times.</p>
 
 <figure width="362" height="178" image="logic-controller/loop-example.gif">Figure 4 - Loop Controller Example</figure>
 
 <p>We configured the Thread Group for a single thread and a loop count value of
 one. Instead of letting the Thread Group control the looping, we used a Loop
 Controller.  You can see that we added one HTTP Request to the Thread Group and
 another HTTP Request to a Loop Controller.  We configured the Loop Controller
 with a loop count value of five.</p>
 <p>JMeter will send the requests in the following order: Home Page, News Page,
 News Page, News Page, News Page, and News Page. Note, the File Reporter
 is configured to store the results in a file named "loop-test.dat" in the current directory.</p>
 
 </example>
 
 </component>
 
 <component name="Once Only Controller" index="&sect-num;.2.3" anchor="once_only_controller"  width="390" height="62" screenshot="logic-controller/once-only-controller.gif">
 <description>
 <p>The Once Only Logic Controller tells JMeter to process the controller(s) inside it only once, and pass over any requests under it
 during further iterations through the test plan.</p>
 
 <p>The Once Only Controller will now execute always during the first iteration of any looping parent controller.  Thus, if the Once Only Controller is placed under a Loop Controller specified to loop 5 times, then the Once Only Controller will execute only on the first iteration through the Loop Controller (ie, every 5 times).  Note this means the Once Only Controller will still behave as previously expected if put under a Thread Group (runs only once per test), but now the user has more flexibility in the use of the Once Only Controller.</p>
 
 <p>For testing that requires a login, consider placing the login request in this controller since each thread only needs
 to login once to establish a session.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 <example title="Once Only Example" anchor="once_only_example">
 <p><a href="../demos/OnceOnlyTestPlan.jmx">Download</a> this example (see Figure 5).
 In this example, we created a Test Plan that has two threads that send HTTP request.
 Each thread sends one request to the Home Page, followed by three requests to the Bug Page.
 Although we configured the Thread Group to iterate three times, each JMeter thread only
 sends one request to the Home Page because this request lives inside a Once Only Controller.</p>
 <figure width="348" height="131" image="logic-controller/once-only-example.png">Figure 5. Once Only Controller Example</figure>
 <p>Each JMeter thread will send the requests in the following order: Home Page, Bug Page,
 Bug Page, Bug Page. Note, the File Reporter is configured to store the results in a file named "loop-test.dat" in the current directory.</p>
 
 </example>
 <note>The behaviour of the Once Only controller under anything other than the 
 Thread Group or a Loop Controller is not currently defined. Odd things may happen.</note>
 </component>
 
 <component name="Interleave Controller" index="&sect-num;.2.4"  width="219" height="90" screenshot="logic-controller/interleave-controller.png">
 <description><p>If you add Generative or Logic Controllers to an Interleave Controller, JMeter will alternate among each of the
 other controllers for each loop iteration. </p>
 </description>
 <properties>
         <property name="name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="ignore sub-controller blocks" required="No">If checked, the interleave controller will treat sub-controllers like single request elements and only allow one request per controller at a time.  </property>
 </properties>
 
 <!--
 For example, if you
 add three HTTP Requests to an Interleave Controller and configure the Thread
 Group to loop, here is the sequence in which JMeter sends the requests:
 </p>
 <table border="1" cellspacing="0" cellpadding="4">
 <tr valign="top"><th>Loop Iteration</th><th>Description</th></tr>
 <tr valign="top"><td>1</td><td>JMeter sends the first HTTP Request.</td></tr>
 <tr valign="top"><td>2</td><td>JMeter sends the second HTTP Request.</td></tr>
 <tr valign="top"><td>3</td><td>JMeter sends the third HTTP Request.</td></tr>
 <tr valign="top"><td>4</td><td>Because there are no more requests in controller, JMeter start over and sends the first HTTP Request.</td></tr>
 <tr valign="top"><td>5</td><td>JMeter sends the second HTTP Request.</td></tr>
 <tr valign="top"><td>(and so on)</td><td>...</td></tr>
 </table>
 -->
 <example title="Simple Interleave Example" anchor="simple_interleave_example">
 
 <p><a href="../demos/InterleaveTestPlan.jmx">Download</a> this example (see Figure 1).  In this example,
 we configured the Thread Group to have two threads and a loop count of five, for a total of ten
 requests per thread. See the table below for the sequence JMeter sends the HTTP Requests.</p>
 
 <figure width="336" height="153" image="logic-controller/interleave.png">Figure 1 - Interleave Controller Example 1</figure>
 
 <table border="1" cellspacing="0" cellpadding="4">
 <tr valign="top"><th>Loop Iteration</th><th>Each JMeter Thread Sends These HTTP Requests</th></tr>
 <tr valign="top"><td>1</td><td>News Page</td></tr>
 <tr valign="top"><td>1</td><td>Log Page</td></tr>
 <tr valign="top"><td>2</td><td>FAQ Page</td></tr>
 <tr valign="top"><td>2</td><td>Log Page</td></tr>
 <tr valign="top"><td>3</td><td>Gump Page</td></tr>
 <tr valign="top"><td>3</td><td>Log Page</td></tr>
 <tr valign="top"><td>4</td><td>Because there are no more requests in the controller,<br> </br> JMeter starts over and sends the first HTTP Request, which is the News Page.</td></tr>
 <tr valign="top"><td>4</td><td>Log Page</td></tr>
 <tr valign="top"><td>5</td><td>FAQ Page</td></tr>
 <tr valign="top"><td>5</td><td>Log Page</td></tr>
 </table>
 
 
 </example>
 
 <example title="Useful Interleave Example" anchor="useful_interleave_example">
 
 <p><a href="../demos/InterleaveTestPlan2.jmx">Download</a> another example (see Figure 2).  In this
 example, we configured the Thread Group
 to have a single thread and a loop count of eight.  Notice that the Test Plan has an outer Interleave Controller with
 two Interleave Controllers inside of it.</p>
 
 <figure width="207" height="249" image="logic-controller/interleave2.png">
         Figure 2 - Interleave Controller Example 2
 </figure>
 
 <p>The outer Interleave Controller alternates between the
 two inner ones.  Then, each inner Interleave Controller alternates between each of the HTTP Requests.  Each JMeter
 thread will send the requests in the following order: Home Page, Interleaved, Bug Page, Interleaved, CVS Page, Interleaved, and FAQ Page, Interleaved.
 Note, the File Reporter is configured to store the results in a file named "interleave-test2.dat" in the current directory.</p>
 
 <figure width="204" height="247" image="logic-controller/interleave3.png">
         Figure 3 - Interleave Controller Example 3
 </figure>
 <p>If the two interleave controllers under the main interleave controller were instead simple controllers, then the order would be: Home Page, CVS Page, Interleaved, Bug Page, FAQ Page, Interleaved.  However, if "ignore sub-controller blocks" was checked on the main interleave controller, then the order would be: Home Page, Interleaved, Bug Page, Interleaved, CVS Page, Interleaved, and FAQ Page, Interleaved.</p>
 </example>
 </component>
 
 <component name="Random Controller" index="&sect-num;.2.5"  width="238" height="84" screenshot="logic-controller/random-controller.gif">
 <description>
 <p>The Random Logic Controller acts similarly to the Interleave Controller, except that
 instead of going in order through its sub-controllers and samplers, it picks one
 at random at each pass.</p>
 <note>Interactions between multiple controllers can yield complex behavior.
 This is particularly true of the Random Controller.  Experiment before you assume
 what results any given interaction will give</note>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 </component>
 
 
 
 <component name="Random Order Controller" index="&sect-num;.2.6"  width="358" height="131" screenshot="randomordercontroller.png">
 	<description>
 		<p>The Random Order Controller is much like a Simple Controller in that it will execute each child
 		 element at most once, but the order of execution of the nodes will be random.</p>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 </component>
 
 <component name="Throughput Controller" index="&sect-num;.2.7"  width="223" height="148" screenshot="throughput_controller.png">
 <description>
 <p>
 <b>This controller is badly named, as it does not control throughput.</b>
 Please refer to the <complink name="Constant Throughput Timer"/> for an element that can be used to adjust the throughput.
 </p>
 <p>The Throughput Controller allows the user to control how often it is executed.  There are two modes - percent execution and total executions.  Percent executions causes the controller to execute a certain percentage of the iterations through the test plan.  Total
 executions causes the controller to stop executing after a certain number of executions have occurred.  Like the Once Only Controller, this
 setting is reset when a parent Loop Controller restarts.
 </p>
 </description>
 <note>The Throughput Controller can yield very complex behavior when combined with other controllers - in particular with interleave or random controllers as parents (also very useful).</note>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Execution Style" required="Yes">Whether the controller will run in percent executions or total executions mode.</property>
         <property name="Throughput" required="Yes">A number.  for percent execution mode, a number from 0-100 that indicates the percentage of times the controller will execute.  "50" means the controller will execute during half the iterations throught the test plan.  for total execution mode, the number indicates the total number of times the controller will execute.</property>
         <property name="Per User" required="No">If checked, per user will cause the controller to calculate whether it should execute on a per user (per thread) basis.  if unchecked, then the calculation will be global for all users.  for example, if using total execution mode, and uncheck "per user", then the number given for throughput will be the total number of executions made.  if "per user" is checked, then the total number of executions would be the number of users times the number given for throughput.</property>
 </properties>
 
 </component>
 
 <component name="Runtime Controller" index="&sect-num;.2.8"  width="358" height="131" screenshot="runtimecontroller.png">
 	<description>
 		<p>The Runtime Controller controls how long its children are allowed to run.
 		</p>
 	</description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Runtime (seconds)" required="Yes">Desired runtime in seconds</property>
 </properties>
 </component>
 
 <component name="If Controller" index="&sect-num;.2.9"  width="358" height="131" screenshot="ifcontroller.png">
 	<description>
 		<p>The If Controller allows the user to control whether the test elements below it (its children) are run or not.</p>
 		<p>
 		Prior to JMeter 2.3RC3, the condition was evaluated for every runnable element contained in the controller.
 		This sometimes caused unexpected behaviour, so 2.3RC3 was changed to evaluate the condition only once on initial entry.
 		However, the original behaviour is also useful, so versions of JMeter after 2.3RC4 have an additional
 		option to select the original behaviour.
 		</p>
 	</description>
 <properties>
     <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Condition" required="Yes"><b>Javascript</b> code that returns "true" or "false"</property>
 	<property name="Evaluate for all children" required="Yes">
 	Should condition be evaluated for all children?
 	If not checked, then the condition is only evaluated on entry.
 	</property>
 </properties>
 	<p>Examples:
 		<ul>
 			<li>${COUNT} &lt; 10</li>
 			<li>"${VAR}" == "abcd"</li>
 			<li>${JMeterThread.last_sample_ok} (check if last sample succeeded)</li>
 		</ul>
 		If there is an error interpreting the code, the condition is assumed to be false, and a message is logged in jmeter.log.
 	</p>
 </component>
 
 
 
 
 
 
 
 
 <component name="While Controller" index="&sect-num;.2.10"  width="358" height="131" screenshot="whilecontroller.png">
 	<description>
 <p>
 The While Controller runs its children until the condition is "false".
 </p>
 
 <p>Possible condition values:</p>
 <ul>
 <li>blank - exit loop when last sample in loop fails</li>
 <li>LAST - exit loop when last sample in loop fails. 
 If the last sample just before the loop failed, don't enter loop.</li>
 <li>Otherwise - exit (or don't enter) the loop when the condition is equal to the string "false"</li>
 </ul>
 <note>In contrast to the IfController, the condition is not evaluated as a JavaScript expression.
 The condition can be any variable or function that eventually evaluates to the string "false". 
 This allows the use of JavaScript, BeanShell, properties or variables as needed.
 </note>
 <br></br>
 For example:
 <ul>
 	<li>${VAR} - where VAR is set to false by some other test element</li>
 	<li>${__javaScript(${C}==10,dummy)}</li>
 	<li>${__javaScript("${VAR2}"=="abcd",dummy)}</li>
 	<li>${_P(property)} - where property is set to "false" somewhere else</li>
 </ul>
 	</description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Condition" required="Yes">blank, LAST, or variable/function</property>
 </properties>
 </component>
 
 <component name="Switch Controller" index="&sect-num;.2.11"  width="358" height="131" screenshot="switchcontroller.png">
 	<description>
 <p>
 The Switch Controller acts like the <complink name="Interleave Controller"/> 
 in that it runs one of the subordinate elements on each iteration, but rather than
 run them in sequence, the controller runs the element defined by the switch value.
 </p>
 <p>
 Note: In versions of JMeter after 2.3.1, the switch value can also be a name.
 </p>
 <p>If the switch value is out of range, it will run the zeroth element, 
 which therefore acts as the default for the numeric case.
 It also runs the zeroth element if the value is the empty string.</p>
 <p>
 If the value is non-numeric (and non-empty), then the Switch Controller looks for the
 element with the same name (case is significant).
 If none of the names match, then the element named "default" (case not significant) is selected.
 If there is no default, then no element is selected, and the controller will not run anything.
 </p>
 </description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Switch Value" required="Yes">The number (or name) of the subordinate element to be invoked. Elements are numbered from 0.</property>
 </properties>
 </component>
 
 <component name="ForEach Controller" index="&sect-num;.2.12" anchor="loop"  width="410" height="153" screenshot="logic-controller/foreach-controller.png">
 <description><p>A ForEach controller loops through the values of a set of related variables. 
 When you add samplers (or controllers) to a ForEach controller, every sample sample (or controller)
 is executed one or more times, where during every loop the variable has a new value.
 The input should consist of several variables, each extended with an underscore and a number.
 Each such variable must have a value.
 So for example when the input variable has the name inputVar, the following variables should have been defined:
 		<ul>
 		<li>inputVar_1 = wendy</li>
 		<li>inputVar_2 = charles</li>
 		<li>inputVar_3 = peter</li>
 		<li>inputVar_4 = john</li>
 		</ul>
 		<p>Note: the "_" separator is now optional.</p>
 When the return variable is given as "returnVar", the collection of samplers and controllers under the ForEach controller will be executed 4 consecutive times,
 with the return variable having the respective above values, which can then be used in the samplers.
 </p>
 <p>
 It is especially suited for running with the regular expression post-processor. 
 This can "create" the necessary input variables out of the result data of a previous request.
 By omitting the "_" separator, the ForEach Controller can be used to loop through the groups by using
 the input variable refName_g, and can also loop through all the groups in all the matches
 by using an input variable of the form refName_${C}_g, where C is a counter variable.
 </p>
 <note>The ForEach Controller does not run any samples if inputVar_1 is null.
 This would be the case if the Regular Expression returned no matches.</note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Input variable prefix" required="Yes">Prefix for the variable names to be used as input.</property>
         <property name="Output variable" required="Yes">
                 The name of the variable which can be used in the loop for replacement in the samplers</property>
 		<property required="Yes" name="Use Separator">If not checked, the "_" separator is omitted.</property>
 </properties>
 
 <example title="ForEach Example" anchor="foreach_example">
 
 <p><a href="../demos/forEachTestPlan.jmx">Download</a> this example (see Figure 7).
 In this example, we created a Test Plan that sends a particular HTTP Request
 only once and sends another HTTP Request to every link that can be found on the page.</p>
 
 <figure width="246" height="154" image="logic-controller/foreach-example.png">Figure 7 - ForEach Controller Example</figure>
 
 <p>We configured the Thread Group for a single thread and a loop count value of
 one. You can see that we added one HTTP Request to the Thread Group and
 another HTTP Request to the ForEach Controller.</p>
 <p>After the first HTTP request, a regular expression extractor is added, which extracts all the html links
 out of the return page and puts them in the inputVar variable</p>
 <p>In the ForEach loop, a HTTP sampler is added which requests all the links that were extracted from the first returned HTML page.
 </p></example>
 <example title="ForEach Example" anchor="foreach_example2">
 <p>Here is <a href="../demos/ForEachTest2.jmx">another example</a> you can download. 
 This has two Regular Expressions and ForEach Controllers.
 The first RE matches, but the second does not match, 
 so no samples are run by the second ForEach Controller</p>
 <figure width="198" height="253" image="logic-controller/foreach-example2.png">Figure 8 - ForEach Controller Example 2</figure>
 <p>The Thread Group has a single thread and a loop count of two.
 </p><p>
 Sample 1 uses the JavaTest Sampler to return the string "a b c d".
 </p><p>The Regex Extractor uses the expression <b>(\w)\s</b> which matches a letter followed by a space,
 and returns the letter (not the space). Any matches are prefixed with the string "inputVar".
 </p><p>The ForEach Controller extracts all variables with the prefix "inputVar_", and executes its
 sample, passing the value in the variable "returnVar". In this case it will set the variable to the values "a" "b" and "c" in turn.
 </p><p>The For 1 Sampler is another Java Sampler which uses the return variable "returnVar" as part of the sample Label
 and as the sampler Data.
 </p><p>Sample 2, Regex 2 and For 2 are almost identical, except that the Regex has been changed to "(\w)\sx",
 which clearly won't match. Thus the For 2 Sampler will not be run.
 </p>
 </example>
 </component>
 
 <component name="Module Controller" index="&sect-num;.2.13"  width="409" height="255" screenshot="module_controller.png">
 <description>
 <p>
 The Module Controller provides a mechanism for substituting test plan fragments into the current test plan at run-time.
 </p>
 <p>
 A test plan fragment consists of a Controller and all the test elements (samplers etc) contained in it. 
 The fragment can be located in any Thread Group, or on the <complink name="WorkBench" />.
 If the fragment is located in a Thread Group, then its Controller can be disabled to prevent the fragment being run
 except by the Module Controller. 
 Or you can store the fragments in a dummy Thread Group, and disable the entire Thread Group. 
 </p>
 <p>
 There can be multiple fragments, each with a different series of
 samplers under them.  The module controller can then be used to easily switch between these multiple test cases simply by choosing
 the appropriate controller in its drop down box.  This provides convenience for running many alternate test plans quickly and easily.
 </p>
 <p>
 A fragment name is made up of the Controller name and all its parent names.
 For example:
 <pre>
 Test Plan / Protocol: JDBC / Control / Interleave Controller
 </pre>
 Any fragments used by the Module Controller must have a unique name,
 as the name is used to find the target controller when a test plan is reloaded.
 </p>
 </description>
 <note>The Module Controller should not be used with remote testing or non-gui testing in conjunction with Workbench components since the Workbench test elements are not part of test plan .jmx files.  Any such test will fail.</note>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Module to Run" required="Yes">The module controller provides a list of all controllers loaded into the gui.  Select
         the one you want to substitute in at runtime.</property>
 </properties>
 
 </component>
 
 <component name="Include Controller" index="&sect-num;.2.14"  width="419" height="118" screenshot="includecontroller.png">
 	<description>
 <p>
 The include controller is designed to use an external jmx file. To use it, add
 samples to a simple controller, then save the simple controller as a jmx file.
 The file can then be used in a test plan.
 </p>
 <note>
 This element does not support variables/functions in the filename field.<br></br>
 However, if the property <b>includecontroller.prefix</b> is defined, 
 the contents are used to prefix the pathname.
 </note>
 </description>
 <properties>
 	<property name="Filename" required="Yes">The file to include.</property>
 </properties>
 </component>
 
 <component name="Transaction Controller" index="&sect-num;.2.15"  width="258" height="125" screenshot="transactioncontroller.png">
 	<description>
 	    <p>
 	    The Transaction Comntroller is used to group samplers by generating an additional
 	    sample which totals the nested samples.
 	    For JMeter versions after 2.3, there are two modes of operation
 	    <ul>
 	    <li>additional sample is added after the nested samples</li>
 	    <li>additional sample is added as a parent of the nested samples</li>
 	    </ul>
 	    </p>
 		<p>
 		The generated sample time includes all the times for the nested samplers, and any timers etc.
 		Depending on the clock resolution, it may be slightly longer than the sum of the individual samplers plus timers.
 		The clock might tick after the controller recorded the start time but before the first sample starts.
 		Similarly at the end.
 		</p>
 		<p>The generated sample is only regarded as successful if all its sub-samples are successful.</p>
 		<p>
 		In parent mode, the individual samples can still be seen in the Tree View Listener,
 		but no longer appear as separate entries in other Listeners.
 		Also, the sub-samples do not appear in CSV log files, but they can be saved to XML files.
 		</p>
 		<note>
 		In parent mode, Assertions (etc) can be added to the Transaction Controller.
 		However by default they will be applied to both the individual samples and the overall transaction sample.
 		To limit the scope of the Assertions, use a Simple Controller to contain the samples, and add the Assertions
 		to the Simple Controller.
 		Parent mode controllers do not currently properly support nested transaction controllers of either type.
 		</note>
 	</description>
 <properties>
 	<property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
 	<property name="Generate Parent Sample" required="Yes">
 	If checked, then the sample is generated as a parent of the other samples,
 	otherwise the sample is generated as an independent sample.
 	</property>
 </properties>
 </component>
 
 <component name="Recording Controller" index="&sect-num;.2.16"  width="417" height="70" screenshot="logic-controller/recording-controller.gif">
 <description>
 <p>The Recording Controller is a place holder indicating where the proxy server should
 record samples to.  During test run, it has no effect, similar to the Simple Controller.  But during
 recording using the <complink name="HTTP Proxy Server" />, all recorded samples will by default
 be saved under the Recording Controller.</p>
 
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 
 </component>
 
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.3 Listeners" anchor="listeners">
 <description>
 	<br></br>
 Most of the listeners perform several roles in addition to "listening"
 to the test results.  
 They also provide means to view, save, and read saved test results.
 <p>Note that Listeners are processed at the end of the scope in which they are found.</p>
 <p>
 The saving and reading of test results is generic.  The various
 listeners have a panel whereby one can specify the file to
 which the results will be written (or read from).
 By default, the results are stored as XML
 files, typically with a ".jtl" extension.
 Storing as CSV is the most efficient option, but is less detailed than XML (the other available option).
 </p>
 <p>
 Listeners do not process sample data in non-GUI mode, but the raw data will be saved if an output
 file has been configured.
 In order to analyse the data generated by a non-GUI test run, you need to load the file into the appropriate
 Listener.
 </p>
 <note>To read existing results and display them, use the file panel Browse button to open the file.
 </note>
 <p>Results can be read from XML or CSV format files.
 When reading from CSV results files, the header (if present) is used to determine which fields are present.
 <b>In order to interpret a header-less CSV file correctly, the appropriate properties must be set in jmeter.properties.</b>
 </p>
 <note>
 The file name can contain function and/or variable references.
 However variable references do not work in client-server mode (functions work OK).
 </note>
 <p><b>Listeners can use a lot of memory if there are a lot of samples.</b>
 Most of the listeners currently keep a copy of every sample in their scope, apart from:
 </p>
 <ul>
 <li>Simple Data Writer</li>
 <li>BeanShell Listener</li>
 <li>Assertion Results</li>
 <li>Mailer Visualizer</li>
 <li>Monitor Results</li>
 <li>Summary Report</li>
 </ul>
 <p>To minimise the amount of memory needed, use the Simple Data Writer, and use the CSV format.</p>
 <p>
 <note>
 Versions of JMeter after 2.3.1 allow JMeter variables to be saved to the output files.
 This can only be specified using a property.
 See the <a href="listeners.html#sample_variables">Listener Sample Variables</a> for details
 </note>
 For full details on setting up the default items to be saved
 see the <a href="listeners.html#defaults">Listener Default Configuration</a> documentation.
 For details of the contents of the output files,
 see the <a href="listeners.html#csvlogformat">CSV log</a> format or
 the <a href="listeners.html#xmlformat2.1">XML log</a> format.
 </p>
 <note>The entries in jmeter.properties are used to define the defaults; 
 these can be overriden for individual listeners by using the Configure button,
 as shown below. 
 The settings in jmeter.properties also apply to the listener that is added
 by using the -l command-line flag.
 </note>
 <p>
 	The figure below shows an example of the result file configuration panel
 <figure width="786" height="145" image="simpledatawriter.png">Result file configuration panel</figure>
 </p>
 <properties>
         <property name="File Name" required="No">Name of the file containing sample results</property>
         <property name="Browse..." required="No">File Browse Button</property>
         <property name="Errors" required="No">Select this to write/read only results with errors</property>
         <property name="Successes" required="No">Select this to write/read only results without errors.
         If neither Errors nor Successes is selected, then all results are processed.</property>
         <property name="Configure" required="No">Configure Button, see below</property>
 </properties>
 </description>
 
 <component name="Sample Result Save Configuration" index="&sect-num;.3.1"  width="629" height="300" screenshot="sample_result_config.png">
 <description>
 <p>
 Listeners can be configured to save different items to the result log files (JTL) by using the Config popup as shown below.
 The defaults are defined as described in  the <a href="listeners.html#defaults">Listener Default Configuration</a> documentation.
 Items with (CSV) after the name only apply to the CSV format; items with (XML) only apply to XML format.
 CSV format cannot currently be used to save any items that include line-breaks.
 </p>
 <p>
 Note that cookies, method and the query string are saved as part of the "Sampler Data" option.
 </p>
 </description>
 </component>
 
 
 
 <component name="Graph Full Results" index="&sect-num;.3.2"  width="672" height="316" screenshot="graphfullresults.png">
 <description>No Description</description>
 </component>
 
 <component name="Graph Results" index="&sect-num;.3.3"  width="605" height="435" screenshot="graph_results.png">
 <description><p>The Graph Results listener generates a simple graph that plots all sample times.  Along
 the bottom of the graph, the current sample (black), the current average of all samples(blue), the
 current standard deviation (red), and the current throughput rate (green) are displayed in milliseconds.</p>
 <p>The throughput number represents the actual number of requests/minute the server handled.  This calculation
 includes any delays you added to your test and JMeter's own internal processing time.  The advantage
 of doing the calculation like this is that this number represents something
 real - your server in fact handled that many requests per minute, and you can increase the number of threads
 and/or decrease the delays to discover your server's maximum throughput.  Whereas if you made calculations
 that factored out delays and JMeter's processing, it would be unclear what you could conclude from that
 number.</p></description>
 <p>The following table briefly describes the items on the graph. 
 Further details on the precise meaning of the statistical terms can be found on the web
  - e.g. Wikipedia - or by consulting a book on statistics.
  </p>
  <ul>
  <li>Data - plot the actual data values</li>
  <li>Average - plot the Average</li>
  <li>Median - plot the Median (midway value)</li>
  <li>Deviation - plot the Standard Deviation (a measure of the variation)</li>
  <li>Throughput - plot the number of samples per unit of time</li>
  </ul>
  <p>The individual figures at the bottom of the display are the current values. 
  "Latest Sample" is the current elapsed sample time, shown on the graph as "Data".</p>
 </component>
 
 <component name="Spline Visualizer" index="&sect-num;.3.4"  width="581" height="440" screenshot="spline_visualizer.png">
 <description><p>The Spline Visualizer provides a view of all sample times from the start
 of the test till the end, regardless of how many samples have been taken.  The spline
 has 10 points, each representing 10% of the samples, and connected using spline
 logic to show a single continuous line.</p></description>
 </component>
 
 <component name="Assertion Results" index="&sect-num;.3.5"  width="658" height="277" screenshot="assertion_results.png">
 <description><p>The Assertion Results visualizer shows the Label of each sample taken.
 It also reports failures of any <a href="test_plan.html#assertions">Assertions</a> that
 are part of the test plan.</p></description>
 
 <links>
         <complink name="Response Assertion"/>
 </links>
 </component>
 
 <component name="View Results Tree" index="&sect-num;.3.6"  width="791" height="506" screenshot="view_results_tree.png">
 <description>The View Results Tree shows a tree of all sample responses, allowing you to view the
 response for any sample.  In addition to showing the response, you can see the time it took to get
 this response, and some response codes.
 Note that the Request panel only shows the headers added by JMeter.
 It does not show any headers (such as Host) that may be added by the HTTP protocol implementation.
 <p>
 There are several ways to view the response, selectable by a radio button.</p>
 <ul>
 <li>Show text</li>
 <li>Render HTML</li>
 <li>Render XML</li>
 <li>Render JSON</li>
 </ul>
 <p>
 The default "Show text" view shows all of the text contained in the
 response. 
 Note that this will only work if the response content-type is considered to be text.
 If the content-type begins with any of the following, it is considered as text,
 otherwise it is considered to be binary.
 <pre>
 text/
 application/javascript
 application/json
 application/soap+xml
 application/vnd.wap.xhtml+xml
 application/xhtml+xml
 application/xml
 </pre>
 Additional types can be added by defining the JMeter property
 <b>content-type_text</b> 
 as a comma-separated list of the content-type prefixes to be matched.
 </p>
 <p>If the response data is larger than 200K, then it won't be displayed.
 To change this limit, set the JMeter property <b>view.results.tree.max_size</b>.
 You can also use save the entire response to a file using
 <complink name="Save Responses to a file"/>.
 </p>
 <p>The HTML view attempts to render the response as
 HTML.  The rendered HTML is likely to compare poorly to the view one
 would get in any web browser; however, it does provide a quick
 approximation that is helpful for initial result evaluation. 
 If the "Download embedded resources" check-box is selected, the renderer
 may download images and style-sheets etc referenced by the HTML. 
 If the checkbox is not selected, the renderer will not download images etc.
 </p>
 <p>The Render XML view will show response in tree style. 
 Any DTD nodes or Prolog nodes will not show up in tree; however, response may contain those nodes.
 </p>
 <p>The Render JSON view will show the response in tree style (also handles JSON embedded in JavaScript).</p>
 </description>
 <p>
 	The Control Panel (above) shows an example of an HTML display.
 	Figure 9 (below) shows an example of an XML display.
 <figure width="751" height="461" image="view_results_tree_xml.png">Figure 9 Sample XML display</figure>
 </p>
 </component>
 
 <component name="Aggregate Report" index="&sect-num;.3.7"  width="784" height="287" screenshot="aggregate_report.png">
 <description>The aggregate report creates a table row for each differently named request in your
 test.  For each request, it totals the response information and provides request count, min, max,
 average, error rate, approximate throughput (request/second) and Kilobytes per second throughput.
 Once the test is done, the throughput is the actual through for the duration of the entire test.
 <p>
 The thoughput is calculated from the point of view of the sampler target 
 (e.g. the remote server in the case of HTTP samples).
 JMeter takes into account the total time over which the requests have been generated.
 If other samplers and timers are in the same thread, these will increase the total time,
 and therefore reduce the throughput value. 
 So two identical samplers with different names will have half the throughput of two samplers with the same name.
 It is important to choose the sampler names correctly to get the best results from
 the Aggregate Report.
 </p>
 <note>
 Calculation of the Median and 90% Line (90<sup>th</sup> <a href="glossary.html#Percentile">percentile</a>) values requires a lot of memory as details of every Sample have to be saved.
 See the <complink name="Summary Report"/> for a similar Listener that does not need so much memory.
 </note>
 <ul>
 <li>Label - The label of the sample.
 If "Include group name in label?" is selected, then the name of the thread group is added as a prefix.
 This allows identical labels from different thread groups to be collated separately if required.
 </li>
 <li># Samples - The number of samples with the same label</li>
 <li>Average - The average time of a set of results</li>
 <li>Median - The <a href="glossary.html#Median">median</a> is the time in the middle of a set of results.
 50% of the samples took no more than this time; the remainder took at least as long.</li>
 <li>90% Line - 90% of the samples took no more than this time.
 The remaining samples at least as long as this. (90<sup>th</sup> <a href="glossary.html#Percentile">percentile</a>)</li>
 <li>Min - The shortest time for the samples with the same label</li>
 <li>Max - The longest time for the samples with the same label</li>
 <li>Error % - Percent of requests with errors</li>
 <li>Throughput - Throughput measured in requests per second/minute/hour</li>
 <li>Kb/sec - The throughput measured in Kilobytes per second</li>
 </ul>
 <p>Times are in milliseonds.</p>
 </description>
 <div align="center">
 <p>
 	The figure below shows an example of selecting the "Include group name" checkbox.
 <figure width="784" height="287" image="aggregate_report_grouped.png">Sample "Include group name" display</figure>
 </p>
 </div>
 </component>
 
 <component name="View Results in Table" index="&sect-num;.3.8"  width="658" height="700" screenshot="table_results.png">
 <description>This visualizer creates a row for every sample result.  
 Like the <complink name="View Results Tree"/>, this visualizer uses a lot of memory.
 </description>
 </component>
 
 <component name="Simple Data Writer" index="&sect-num;.3.9"  width="786" height="145" screenshot="simpledatawriter.png">
 <description>This listener can record results to a file
 but not to the UI.  It is meant to provide an efficient means of
 recording data by eliminating GUI overhead.
 When running in non-GUI mode, the -l flag can be used to create a data file.
 The fields to save are defined by JMeter properties.
 See the jmeter.properties file for details.
 </description>
 </component>
 
 <component name="Monitor Results" index="&sect-num;.3.10"  width="762" height="757" screenshot="monitor_screencap.png">
 <description>
 <p>Monitor Results is a new Visualizer for displaying server
 status. It is designed for Tomcat 5, but any servlet container
 can port the status servlet and use this monitor. There are two primary
 tabs for the monitor. The first is the "Health" tab, which will show the
 status of one or more servers. The second tab labled "Performance" shows
 the performance for one server for the last 1000 samples. The equations
 used for the load calculation is included in the Visualizer.</p>
 <p>Currently, the primary limitation of the monitor is system memory. A
 quick benchmark of memory usage indicates a buffer of 1000 data points for
 100 servers would take roughly 10Mb of RAM. On a 1.4Ghz centrino
 laptop with 1Gb of ram, the monitor should be able to handle several
 hundred servers.</p>
 <p>As a general rule, monitoring production systems should take care to
 set an appropriate interval. Intervals shorter than 5 seconds are too
 aggressive and have a potential of impacting the server. With a buffer of
 1000 data points at 5 second intervals, the monitor would check the server
 status 12 times a minute or 720 times a hour. This means the buffer shows
 the performance history of each machine for the last hour.</p>
 <note>
 The monitor requires Tomcat 5 or above. 
 Use a browser to check that you can access the Tomcat status servlet OK.
 </note>
 </description>
 </component>
 
 <component name="Distribution Graph (alpha)" index="&sect-num;.3.11"  width="655" height="501" screenshot="distribution_graph.png">
 <description>
 <p>The distribution graph will display a bar for every unique response time. Since the
 granularity of System.currentTimeMillis() is 10 milliseconds, the 90% threshold should be
 within the width of the graph. The graph will draw two threshold lines: 50% and 90%.
 What this means is 50% of the response times finished between 0 and the line. The same
 is true of 90% line. Several tests with Tomcat were performed using 30 threads for 600K
 requests. The graph was able to display the distribution without any problems and both
 the 50% and 90% line were within the width of the graph. A performant application will
 generally produce results that clump together. A poorly written application that has
 memory leaks may result in wild fluctuations. In those situations, the threshold lines
 may be beyond the width of the graph. The recommended solution to this specific problem
 is fix the webapp so it performs well. If your test plan produces distribution graphs
 with no apparent clumping or pattern, it may indicate a memory leak. The only way to
 know for sure is to use a profiling tool.</p>
 </description>
 </component>
 
 <component name="Aggregate Graph" index="&sect-num;.3.12"  width="839" height="770" screenshot="aggregate_graph.png">
 <description>The aggregate graph is similar to the aggregate report. The primary
 difference is the aggregate graph provides an easy way to generate bar graphs and save
 the graph as a PNG file. By default, the aggregate graph will generate a bar chart
 450 x 250 pixels.</description>
 </component>
 
 <component name="Mailer Visualizer" index="&sect-num;.3.13"  width="645" height="345" screenshot="mailervisualizer.png">
 <description><p>The mailer visualizer can be set up to send email if a test run receives too many
 failed responses from the server.</p></description>
 <note>
 The Mailer Visualizer requires the optional Javamail jars.
 If these are not present in the lib directory, the element will not appear in the menus.
 </note>
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
         <property name="From" required="Yes">Email address to send messages from.</property>
         <property name="Addressie(s)" required="Yes">Email address to send messages to.</property>
         <property name="SMTP Host" required="No">IP address or host name of SMTP (email redirector)
         server.</property>
         <property name="Failure Subject" required="No">Email subject line for fail messages.</property>
         <property name="Success Subject" required="No">Email subject line for success messages.</property>
         <property name="Failure Limit" required="Yes">Once this number of failed responses is exceeded, a failure
         email is sent - i.e. set the count to 0 to send an e-mail on the first failure.</property>
         <property name="Success Limit" required="Yes">Once this number of successful responses is exceeded
         <strong>after previously reaching the failure limit</strong>, a success email
         is sent.  The mailer will thus only send out messages in a sequence of failed-succeeded-failed-succeeded, etc.</property>
         <property name="Test Mail" required="No">Press this button to send a test mail</property>
         <property name="Failures" required="No">A field that keeps a running total of number
         of failures so far received.</property>
 </properties>
 </component>
 
 <component name="BeanShell Listener"  index="&sect-num;.3.14"  width="597" height="303" screenshot="beanshell_listener.png">
 <description>
 <p>
 The BeanShell Listener allows the use of BeanShell for processing samples for saving etc.
 </p>
 <p>
 		<b>Please note that the BeanShell jar file is not included with JMeter; it needs to be separately downloaded.
 		<br></br>
         For full details on using BeanShell, please see the BeanShell web-site at http://www.beanshell.org/.</b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener methods.
 These should be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 </description>
 <properties>
     <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
 	The parameters are stored in the following variables:
 	<ul>
 		<li>Parameters - string containing the parameters as a single variable</li>
 	    <li>bsh.args - String array containing parameters, split on white-space</li>
 	</ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run</property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. The return value is ignored.</property>
 </properties>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li>log - (Logger) - can be used to write to the log file</li>
 <li>ctx - (JMeterContext) - gives access to the context</li>
 <li>vars - (JMeterVariables) - gives read/write access to variables: vars.get(key); vars.put(key,val); vars.putObject("OBJ1",new Object());</li>
 <li>props - JMeter Properties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 <li>sampleResult - (SampleResult) - gives access to the previous SampleResult</li>
 <li>sampleEvent (SampleEvent) gives access to the current sample event</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 <p>If the property <b>beanshell.listener.init</b> is defined, this is used to load an initialisation file, which can be used to define methods etc for use in the BeanShell script.</p>
 </component>
 
 <component name="Summary Report" index="&sect-num;.3.15"  width="784" height="287" screenshot="summary_report.png">
 <description>The summary report creates a table row for each differently named request in your
 test. This is similar to the <complink name="Aggregate Report"/> , except that it uses less memory.
 <p>
 The thoughput is calculated from the point of view of the sampler target 
 (e.g. the remote server in the case of HTTP samples).
 JMeter takes into account the total time over which the requests have been generated.
 If other samplers and timers are in the same thread, these will increase the total time,
 and therefore reduce the throughput value. 
 So two identical samplers with different names will have half the throughput of two samplers with the same name.
 It is important to choose the sampler labels correctly to get the best results from
 the Report.
 </p>
 <ul>
 <li>Label - The label of the sample.
 If "Include group name in label?" is selected, then the name of the thread group is added as a prefix.
 This allows identical labels from different thread groups to be collated separately if required.
 </li>
 <li># Samples - The number of samples with the same label</li>
 <li>Average - The average elapsed time of a set of results</li>
 <li>Min - The lowest elapsed time for the samples with the same label</li>
 <li>Max - The longest elapsed time for the samples with the same label</li>
 <li>Std. Dev. - the standard deviation of the sample elapsed time</li>
 <li>Error % - Percent of requests with errors</li>
 <li>Throughput - Throughput measured in requests per second/minute/hour</li>
 <li>Kb/sec - The throughput measured in Kilobytes per second</li>
 <li>Avg. Bytes - average size of the sample response in bytes. (in JMeter 2.2 it wrongly showed the value in kB)</li>
 </ul>
 <p>Times are in milliseonds.</p>
 </description>
 <div align="center">
 <p>
 	The figure below shows an example of selecting the "Include group name" checkbox.
 <figure width="784" height="287" image="summary_report_grouped.png">Sample "Include group name" display</figure>
 </p>
 </div>
 </component>
 
-<component name="Save Responses to a file" index="&sect-num;.3.16"  width="361" height="178" screenshot="savetofile.png">
+<component name="Save Responses to a file" index="&sect-num;.3.16"  width="359" height="202" screenshot="savetofile.png">
     <description>
         <p>
         This test element can be placed anywhere in the test plan.
         For each sample in its scope, it will create a file of the response Data.
         The primary use for this is in creating functional tests, but it can also
         be useful where the response is too large to be displayed in the 
         <complink name="View Results Tree"/> Listener.
-        The file name is created from the specified prefix, plus a number.
+        The file name is created from the specified prefix, plus a number (unless this is disabled, see below).
         The file extension is created from the document type, if known.
         If not known, the file extension is set to 'unknown'.
         The generated file name is stored in the sample response, and can be saved
         in the test log output file if required.
         </p>
         <p>
         The current sample is saved first, followed by any sub-samples (child samples).
+        If a variable name is provided, then the names of the files are saved in the order
+        that the sub-samples appear. See below. 
         </p>
     </description>
  <properties>
  <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
  <property name="Filename Prefix" required="Yes">Prefix for the generated file names; this can include a directory name.</property>
  <property name="Variable Name" required="No">
  Name of a variable in which to save the generated file name (so it can be used later in the test plan).
  If there are sub-samples then a numeric suffix is added to the variable name.
  E.g. if the variable name is FILENAME, then the parent sample file name is saved in the variable FILENAME, 
  and the filenames for the child samplers are saved in FILENAME1, FILENAME2 etc.
  </property>
  <property name="Save Failed Responses only" required="Yes">If selected, then only failed responses are saved</property>
  <property name="Save Successful Responses only" required="Yes">If selected, then only successful responses are saved</property>
+ <property name="Don't add number to prefix" required="Yes">If selected, then no number is added to the prefix. If you select this option, make sure that the prefix is unique or the file may be overwritten.</property>
  </properties>
 </component>
 
 <component name="BSF Listener" index="&sect-num;.3.17"  width="736" height="369" screenshot="bsf_listener.png">
 <description>
 <p>
 The BSF Listener allows BSF script code to be applied to sample results.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li>Parameters - string containing the parameters as a single variable</li>
         <li>args - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run.</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the BSFEngine.exec() method, which does not return a value.
 </p>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li>log - (Logger) - can be used to write to the log file</li>
 <li>Label - the String Label</li>
 <li>Filename - the script file name (if any)</li>
 <li>Parameters - the parameters (as a String)</li>
 <li>args[] - the parameters as a String array (split on whitespace)</li>
 <li>ctx - (JMeterContext) - gives access to the context</li>
 <li>vars - (JMeterVariables) - gives read/write access to variables: vars.get(key); vars.put(key,val); vars.putObject("OBJ1",new Object()); vars.getObject("OBJ2");</li>
 <li>props - JMeter Properties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 <li>sampleResult, prev - (SampleResult) - gives access to the SampleResult</li>
 <li>sampleEvent - (SampleEvent) - gives access to the SampleEvent</li>
 <li>sampler - (Sampler)- gives access to the last sampler</li>
 <li>OUT - System.out - e.g. OUT.println("message")</li>
 </ul>
 <p>For details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <component name="Generate Summary Results" index="&sect-num;.3.18"  width="358" height="131" screenshot="summary.png">
     <description>This test element can be placed anywhere in the test plan.
 Generates a summary of the test run so far to the log file and/or 
 standard output. Both running and differential totals are shown.
 Output is generated every n seconds (default 3 minutes) on the appropriate
 time boundary, so that multiple test runs on the same time will be synchronised.
 The interval is defined by the property "summariser.interval" - see jmeter.properties.
 This element is mainly intended for batch (non-GUI) runs.
 The output looks like the following:
 <pre>
 label +   171 in  20.3s =    8.4/s Avg:  1129 Min:  1000 Max:  1250 Err:     0 (0.00%)
 label +   263 in  31.3s =    8.4/s Avg:  1138 Min:  1000 Max:  1250 Err:     0 (0.00%)
 label =   434 in  50.4s =    8.6/s Avg:  1135 Min:  1000 Max:  1250 Err:     0 (0.00%)
 label +   263 in  31.0s =    8.5/s Avg:  1138 Min:  1000 Max:  1250 Err:     0 (0.00%)
 label =   697 in  80.3s =    8.7/s Avg:  1136 Min:  1000 Max:  1250 Err:     0 (0.00%)
 label +   109 in  12.4s =    8.8/s Avg:  1092 Min:    47 Max:  1250 Err:     0 (0.00%)
 label =   806 in  91.6s =    8.8/s Avg:  1130 Min:    47 Max:  1250 Err:     0 (0.00%)
 </pre>
 The "label" is the the name of the element.
 The "+" means that the line is a delta line, i.e. shows the changes since the last output.
 The "=" means that the line is a totals line, i.e. it shows the running total.
 Entries in the jmeter log file also include time-stamps.
 The example "806 in  91.6s =    8.8/s" means that there were 806 samples recorded in 91.6 seconds,
 and that works out at 8.8 samples per second.
 The Avg (Average), Min(imum) and Max(imum) times are in milliseonds.
 "Err" means number of errors (also shown as percentage).
 The last two lines will appear at the end of a test.
 They will not be synchronised to the appropriate time boundary.
 Note that the initial and final deltas may be for less than the interval (in the example above this is 30 seconds).
 The first delta will generally be lower, as JMeter synchronises to the interval boundary.
 The last delta will be lower, as the test will generally not finish on an exact interval boundary.
 <p>
 The label is used to group sample results together. 
 So if you have multiple Thread Groups and want to summarize across them all, then use the same label
  - or add the summariser to the Test Plan (so all thread groups are in scope).
 Different summary groupings can be implemented
 by using suitable labels and adding the summarisers to appropriate parts of the test plan.
 </p>
 
     </description>
  <properties>
  <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.
  It appears as the "label" in the output. Details for all elements with the same label will be added together.
  </property>
  </properties>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.4 Configuration Elements" anchor="config_elements">
 <description>
 	<br></br>
 	Configuration elements can be used to set up defaults and variables for later use by samplers.
 	Note that these elements are processed at the start of the scope in which they are found, 
 	i.e. before any samplers in the same scope.
 	<br></br>
 </description>
 
 <component name="CSV Data Set Config" index="&sect-num;.4.1"  width="396" height="301" screenshot="csvdatasetconfig.png">
 <description>
     <p>
 	CSV Data Set Config is used to read lines from a file, and split them into variables.
 	It is easier to use than the __CSVRead() and _StringFromFile() functions.
 	Versions of JMeter after 2.3.1 allow variables to be quoted; this allows the value to contain a delimiter.
 	Previously it was necessary to choose a delimiter that was not used in any values.
 	</p>
 	<p>
 	By default, the file is only opened once, and each thread will use a different line from the file.
 	See the description of the Share mode below for additional options (JMeter 2.3.2+).
 	Lines are read at the start of each test iteration.
     The file name and mode are resolved in the first iteration.
 	</p>
 	<note>CSV Dataset variables are defined at the start of each test iteration.
 	As this is after configuration processing is completed,
 	they cannot be used for some configuration items - such as JDBC Config - 
 	that process their contents at configuration time (see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=40934">Bug 40394 </a>)
 	However the variables do work in the HTTP Auth Manager, as the username etc are processed at run-time.
 	</note>
 	<p>
 	As a special case, the string "\t" (without quotes) in the delimiter field is treated as a Tab.
 	</p>
 	<p>
 	When the end of file (EOF) is reached, and the recycle option is true, reading starts again with the first line of the file.
 	</p>
 	<p>
 	If the recycle option is false, and stopThread is false, then all the variables are set to <b>&amp;lt;EOF&gt;</b> when the end of file is reached.
 	This value can be changed by setting the JMeter property <b>csvdataset.eofstring</b>.
 	</p>
 	<p>
 	If the Recycle option is false, and Stop Thread is true, then reaching EOF will cause the thread to be stopped.
 	</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Filename" required="Yes">Name of the file to be read. 
   <b>Relative file names are resolved with respect to the path of the active test plan.</b>
   Absolute file names are also supported, but note that they are unlikely to work in remote mode, 
   unless the remote server has the same directory structure.
   If the same physical file is referenced in two different ways - e.g. csvdata.txt and ./csvdata.txt -
   then these are treated as different files.
   If the OS does not distinguish between upper and lower case, csvData.TXT would also be opened separately. 
   </property>
   <property name="File Encoding" required="No">The encoding to be used to read the file, if not the platform default.</property>
   <property name="Variable Names" required="Yes">List of variable names (comma-delimited)</property>
   <property name="Delimiter" required="Yes">Delimiter to be used to split the records in the file.
   If there are fewer values on the line than there are variables the remaining variables are not updated -
   so they will retain their previous value (if any).</property>
   <property name="Allow quoted data?" required="Yes">Should the CSV file allow values to be quoted?</property>
   <property name="Recycle on EOF?" required="Yes">Should the file be re-read from the beginning on reaching EOF? (default is true)</property>
   <property name="Stop thread on EOF?" required="Yes">Should the thread be stopped on EOF, if Recycle is false? (default is false)</property>
   <property name="Sharing mode" required="Yes">
   <ul>
   <li>All threads - (the default) the file is shared between all the threads.</li>
   <li>Current thread group - each file is opened once for each thread group in which the element appears</li>
   <li>Current thread - each file is opened separately for each thread</li>
   <li>Identifier - all threads sharing the same identifier share the same file.
   So for example if you have 4 thread groups, you could use a common id for two or more of the groups
   to share the file between them.
   Or you could use the thread number to share the file between the same thread numbers in different thread groups.
   </li>
   </ul>
   </property>
 </properties>
 </component>
 
 <component name="FTP Request Defaults" index="&sect-num;.4.2"  width="490" height="198" screenshot="ftp-config/ftp-request-defaults.png">
 <description></description>
 </component>
 
 <component name="HTTP Authorization Manager" index="&sect-num;.4.3"  width="490" height="253" screenshot="http-config/http-auth-manager.png">
 <note>If there is more than one Authorization Manager in the scope of a Sampler,
 there is currently no way to sepcify which one is to be used.</note>
 
 <description>
 <p>The Authorization Manager lets you specify one or more user logins for web pages that are
 restricted using server authentication.  You see this type of authentication when you use
 your browser to access a restricted page, and your browser displays a login dialog box.  JMeter
 transmits the login information when it encounters this type of page.</p>
 <p>
 The Authorisation headers are not shown in the Tree View Listener.
 </p>
 <p>
 In versions of JMeter after 2.2, the HttpClient sampler defaults to pre-emptive authentication
 if the setting has not been defined. To disable this, set the values as below, in which case
 authentication will only be performed in response to a challenge.
 <pre>
 jmeter.properties:
 httpclient.parameters.file=httpclient.parameters
 
 httpclient.parameters:
 http.authentication.preemptive$Boolean=false
 </pre>
 Note: the above settings only apply to the HttpClient sampler (and the SOAP samplers, which use Httpclient).
 </p>
 <note>
 When looking for a match against a URL, JMeter checks each entry in turn, and stops when it finds the first match.
 Thus the most specific URLs should appear first in the list, followed by less specific ones.
 Duplicate URLs will be ignored.
 If you want to use different usernames/passwords for different threads, you can use variables.
 These can be set up using a <complink name="CSV Data Set Config"/> Element (for example).
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Base URL" required="Yes">A partial or complete URL that matches one or more HTTP Request URLs.  As an example,
 say you specify a Base URL of "http://jakarta.apache.org/restricted/" with a username of "jmeter" and
 a password of "jmeter".  If you send an HTTP request to the URL
 "http://jakarta.apache.org/restricted/ant/myPage.html", the Authorization Manager sends the login
 information for the user named, "jmeter".</property>
   <property name="Username" required="Yes">The username to authorize.</property>
   <property name="Password" required="Yes">The password for the user.</property>
   <property name="Domain" required="No">The domain to use for NTLM.</property>
   <property name="Realm" required="No">The realm to use for NTLM.</property>
 </properties>
 <note>
 The Realm only applies to the HttpClient sampler.
 In JMeter 2.2, the domain and realm did not have separate columns, and were encoded as part of
 the user name in the form: [domain\]username[@realm].
 This was an experimental feature and has been removed.
 </note>
 <b>Controls:</b>
 <ul>
   <li>Add Button - Add an entry to the authorization table.</li>
   <li>Delete Button - Delete the currently selected table entry.</li>
   <li>Load Button - Load a previously saved authorization table and add the entries to the existing
 authorization table entries.</li>
   <li>Save As Button - Save the current authorization table to a file.</li>
 </ul>
 
 <note>When you save the Test Plan, JMeter automatically saves all of the authorization
 table entries - including any passwords, which are not encrypted.</note>
 
 <example title="Authorization Example" anchor="authorization_example">
 
 <p><a href="../demos/AuthManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan on a local server that sends three HTTP requests, two requiring a login and the
 other is open to everyone.  See figure 10 to see the makeup of our Test Plan.  On our server, we have a restricted
 directory named, "secret", which contains two files, "index.html" and "index2.html".  We created a login id named, "kevin",
 which has a password of "spot".  So, in our Authorization Manager, we created an entry for the restricted directory and
 a username and password (see figure 11).  The two HTTP requests named "SecretPage1" and "SecretPage2" make requests
 to "/secret/index.html" and "/secret/index2.html".  The other HTTP request, named "NoSecretPage" makes a request to
 "/index.html".</p>
 
 <figure width="289" height="201" image="http-config/auth-manager-example1a.gif">Figure 10 - Test Plan</figure>
 <figure width="553" height="243" image="http-config/auth-manager-example1b.png">Figure 11 - Authorization Manager Control Panel</figure>
 
 <p>When we run the Test Plan, JMeter looks in the Authorization table for the URL it is requesting.  If the Base URL matches
 the URL, then JMeter passes this information along with the request.</p>
 
 <note>You can download the Test Plan, but since it is built as a test for our local server, you will not
 be able to run it.  However, you can use it as a reference in constructing your own Test Plan.</note>
 </example>
 
 </component>
 
 <component name="HTTP Cache Manager" index="&sect-num;.4.4"  width="267" height="132" screenshot="http-config/http-cache-manager.png">
 
 <note>This is a new element, and is liable to change</note>
 
 <description>
 <p>
 The HTTP Cache Manager is used to add caching functionality to HTTP requests within its scope.
 </p>
 <p>
 If a sample is successful (i.e. has response code 2xx) then the Last-Modified and Etag values are saved for the URL.
 Before executing the next sample, the sampler checks to see if there is an entry in the cache, 
 and if so, the If-Last-Modified and If-None-Match conditional headers are set for the request.
 </p>
 <p>
 If the requested document has not changed since it was cached, then the response body will be empty.
 This may cause problems for Assertions.
 </p>
 
 </description>
 </component>
 
 <component name="HTTP Cookie Manager" index="&sect-num;.4.4"  width="548" height="319" screenshot="http-config/http-cookie-manager.png">
 
 <note>If there is more than one Cookie Manager in the scope of a Sampler,
 there is currently no way to specify which one is to be used.</note>
 
 <description><p>The Cookie Manager element has two functions:<br></br>
 First, it stores and sends cookies just like a web browser. If you have an HTTP Request and
 the response contains a cookie, the Cookie Manager automatically stores that cookie and will
 use it for all future requests to that particular web site.  Each JMeter thread has its own
 "cookie storage area".  So, if you are testing a web site that uses a cookie for storing
 session information, each JMeter thread will have its own session.</p>
 <p>Received Cookies are stored as JMeter thread variables. 
 Thus the value of a cookie with the name TEST can be referred to as ${TEST}</p>
 <p>Second, you can manually add a cookie to the Cookie Manager.  However, if you do this,
 the cookie will be shared by all JMeter threads.</p>
 <p>Note that such Cookies are created with an Expiration time far in the future</p>
 <p>
 Since version 2.0.3, cookies with null values are ignored by default.
 This can be changed by setting the JMeter property: CookieManager.delete_null_cookies=false.
 Note that this also applies to manually defined cookies - any such cookies will be removed from the display when it is updated.
 Note also that the cookie name must be unique - if a second cookie is defined with the same name, it will replace the first.
 </p>
 </description>
 <properties>
   <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Clear Cookies each Iteration" required="Yes">If selected, all server-defined cookies are cleared each time the main Thread Group loop is executed.
   In JMeter versions after 2.3, any cookies defined in the GUI are not cleared.</property>
   <property name="Cookie Policy" required="Yes">The cookie policy that will be used to manage the cookies. 
   "compatibility" is the default, and should work in most cases.
   See http://jakarta.apache.org/httpcomponents/httpclient-3.x/cookies.html and 
   http://jakarta.apache.org/httpcomponents/httpclient-3.x/apidocs/org/apache/commons/httpclient/cookie/CookiePolicy.html
   [Note: "ignoreCookies" is equivalent to omitting the CookieManager.]
     </property>
   <property name="Cookies Stored in the Cookie Manager" required="No (discouraged, unless you know what you're doing)">This
   gives you the opportunity to use hardcoded cookies that will be used by all threads during the test execution.
   <br></br>
   The "domain" is the hostname of the server (without http://); the port is currently ignored.
   </property>
   <property name="Add Button" required="N/A">Add an entry to the cookie table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved cookie table and add the entries to the existing
 cookie table entries.</property>
   <property name="Save As Button" required="N/A">
   Save the current cookie table to a file (does not save any cookies extracted from HTTP Responses).
   </property>
 </properties>
 
 </component>
 
 <component name="HTTP Request Defaults" index="&sect-num;.4.5" 
          width="581" height="385" screenshot="http-config/http-request-defaults.png">
 <description><p>This element lets you set default values that your HTTP Request controllers use.  For example, if you are
 creating a Test Plan with 25 HTTP Request controllers and all of the requests are being sent to the same server,
 you could add a single HTTP Request Defaults element with the "Server Name or IP" field filled in.  Then, when
 you add the 25 HTTP Request controllers, leave the "Server Name or IP" field empty.  The controllers will inherit
 this field value from the HTTP Request Defaults element.</p>
 <note>
 In JMeter 2.2 and earlier, port 80 was treated specially - it was ignored if the sampler used the https protocol.
 JMeter 2.3 and later treat all port values equally; a sampler that does not specify a port will use the HTTP Request Defaults port, if one is provided.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="No">Domain name or IP address of the web server. e.g. www.example.com. [Do not include the http:// prefix.</property>
         <property name="Port" required="No">Port the web server is listening to.</property>
         <property name="Protocol" required="Yes">HTTP or HTTPS.</property>
         <property name="Method" required="No">HTTP GET or HTTP POST.</property>
         <property name="Path" required="No">The path to resource (for example, /servlets/myServlet). If the
         resource requires query string parameters, add them below in the "Send Parameters With the Request" section.
         Note that the path is the default for the full path, not a prefix to be applied to paths
         specified on the HTTP Request screens.
         </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>.  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (ie if you chose GET, the query string will be
         appended to the URL, if POST, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.</property>
 </properties>
 </component>
 
 <component name="HTTP Header Manager" index="&sect-num;.4.6"  width="" height="" screenshot="http-config/http-header-manager.gif">
 <note>If there is more than one Header Manager in the scope of a Sampler,
 there is currently no way to sepcify which one is to be used.</note>
 
 <description><p>The Header Manager lets you add or override HTTP request headers.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Name (Header)" required="No (You should have at least one, however)">Name of the request header.
         Two common request headers you may want to experiment with
 are "User-Agent" and "Referer".</property>
   <property name="Value" required="No (You should have at least one, however)">Request header value.</property>
   <property name="Add Button" required="N/A">Add an entry to the header table.</property>
   <property name="Delete Button" required="N/A">Delete the currently selected table entry.</property>
   <property name="Load Button" required="N/A">Load a previously saved header table and add the entries to the existing
 header table entries.</property>
   <property name="Save As Button" required="N/A">Save the current header table to a file.</property>
 </properties>
 
 <example title="Header Manager example" anchor="header_manager_example">
 
 <p><a href="../demos/HeaderManagerTestPlan.jmx">Download</a> this example.  In this example, we created a Test Plan
 that tells JMeter to override the default "User-Agent" request header and use a particular Internet Explorer agent string
 instead. (see figures 9 and 10).</p>
 
 <figure width="203" height="141" image="http-config/header-manager-example1a.gif">Figure 12 - Test Plan</figure>
 <figure width="573" height="334" image="http-config/header-manager-example1b.gif">Figure 13 - Header Manager Control Panel</figure>
 </example>
 
 </component>
 
 <component name="Java Request Defaults" index="&sect-num;.4.7"  width="454" height="283" screenshot="java_defaults.png">
 <description><p>The Java Request Defaults component lets you set default values for Java testing.  See the <complink name="Java Request" />.</p>
 </description>
 
 </component>
 
 <component name="JDBC Connection Configuration" index="&sect-num;.4.8" 
                  width="369" height="443" screenshot="jdbc-config/jdbc-conn-config.png">
 	<description>Creates a database connection pool (used by <complink name="JDBC Request"/>Sampler)
 	 with JDBC Connection settings.
 	</description>
 	<properties>
 		<property name="Name" required="No">Descriptive name for the connection pool that is shown in the tree.</property>
 		<property name="Variable Name" required="Yes">The name of the variable the connection pool is tied to.  
 		Multiple connection pools can be used, each tied to a different variable, allowing JDBC Samplers
 		to select the pool to draw connections from.
 		<b>Each pool name must be different. If there are two configuration elements using the same pool name,
 		only one will be saved. JMeter versions after 2.3 log a message if a duplicate name is detected.</b>
 		</property>
 		<property name="Max Number of Connections" required="Yes">Maximum number of connections allowed in the pool.
 		To ensure that threads don't have to wait for connections, set the max count to the same as	the number of threads.
 		In versions of JMeter after 2.3, the value "0" is treated specially.
 		Instead of sharing the pool between all threads in the test plan, a pool containing a single connection
 		is created for each thread. This ensures that the same connection can be re-used for multiple samplers
 		in the same thread.
 		Multiple such pools can be used - e.g. for connecting to different databases - just give them different names.
 		</property>
 		<property name="Pool timeout" required="Yes">Pool throws an error if the timeout period is exceeded in the 
 		process of trying to retrieve a connection</property>
 		<property name="Idle Cleanup Interval (ms)" required="Yes">Uncertain what exactly this does.</property>
 		<property name="Auto Commit" required="Yes">Turn auto commit on or off for the connections.</property>
 		<property name="Keep-alive" required="Yes">Uncertain what exactly this does.</property>
 		<property name="Max Connection Age (ms)" required="Yes">Uncertain what exactly this does.</property>
 		<property name="Validation Query" required="Yes">A simple query used to determine if the database is still
 		responding.</property>
 		<property name="Database URL" required="Yes">JDBC Connection string for the database.</property>
 		<property name="JDBC Driver class" required="Yes">Fully qualified name of driver class. (Must be in
 		JMeter's classpath - easiest to copy .jar file into JMeter's /lib directory).</property>
 		<property name="Username" required="No">Name of user to connect as.</property>
 		<property name="Password" required="No">Password to connect with.</property>
 	</properties>
 <p>Different databases and JDBC drivers require different JDBC settings. 
 The Database URL and JDBC Driver class are defined by the provider of the JDBC implementation.</p>
 <p>Some possible settings are shown below. Please check the exact details in the JDBC driver documentation.</p>
 
 <table>
 <tr><th>Database</th><th>Driver class</th><th>Database URL</th></tr>
 <tr><td>MySQL</td><td>com.mysql.jdbc.Driver</td><td>jdbc:mysql://host[:port]/dbname</td></tr>
 <tr><td>PostgreSQL</td><td>org.postgresql.Driver</td><td>jdbc:postgresql:{dbname}</td></tr>
 <tr><td>Oracle</td><td>oracle.jdbc.driver.OracleDriver</td><td>jdbc:oracle:thin:user/pass@//host:port/service</td></tr>
 <tr><td>Ingres (2006)</td><td>ingres.jdbc.IngresDriver</td><td>jdbc:ingres://host:port/db[;attr=value]</td></tr>
 <tr><td>SQL Server (MS JDBC driver)</td><td>com.microsoft.sqlserver.jdbc.SQLServerDriver</td><td>jdbc:sqlserver://host:port;DatabaseName=dbname</td></tr>
 <tr><td>Apache Derby</td><td>org.apache.derby.jdbc.ClientDriver</td><td>jdbc:derby://server[:port]/databaseName[;URLAttributes=value[;...]]</td></tr>
 </table>
 <note>The above may not be correct - please check the relevant JDBC driver documentation.</note>
 </component>
 
 
 <component name="Login Config Element" index="&sect-num;.4.9"  width="352" height="112" screenshot="login-config.png">
 <description><p>The Login Config Element lets you add or override username and password settings in samplers that use username and password as part of their setup.</p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this element that is shown in the tree. </property>
   <property name="Username" required="No">The default username to use.</property>
   <property name="Password" required="No">The default password to use.</property>
 </properties>
 
 </component>
 
 
 
 <component name="LDAP Request Defaults" index="&sect-num;.4.10"  width="465" height="375" screenshot="ldap_defaults.png">
 <description><p>The LDAP Request Defaults component lets you set default values for LDAP testing.  See the <complink name="LDAP Request"/>.</p>
 </description>
 
 </component>
 
 <component name="LDAP Extended Request Defaults" index="&sect-num;.4.11"  width="597" height="545" screenshot="ldapext_defaults.png">
 <description><p>The LDAP Extended Request Defaults component lets you set default values for extended LDAP testing.  See the <complink name="LDAP Extended Request"/>.</p>
 </description>
 
 </component>
 
 <component name="TCP Sampler Config" index="&sect-num;.4.12"  width="645" height="256" screenshot="tcpsamplerconfig.png">
 	<note>ALPHA CODE</note>
 <description>
         <p>
 	The TCP Sampler Config provides default data for the TCP Sampler
 	</p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="ServerName or IP" required="">Name or IP of TCP server</property>
   <property name="Port Number" required="">Port to be used</property>
   <property name="Timeout (milliseconds)" required="">Timeout for replies</property>
   <property name="Set Nodelay" required="">Should the nodelay property be set?</property>
   <property name="Text to Send" required="">Text to be sent</property>
 </properties>
 </component>
 
 <component name="User Defined Variables" index="&sect-num;.4.13"  width="690" height="394" screenshot="user_defined_variables.png">
 <description><p>The User Defined Variables lets you define variables for use in other test elements, just as in the <complink name="Test Plan" />.
 The variables in User Defined Variables components will take precedence over those defined closer to the tree root -- including those defined in the Test Plan.
 Note that all the UDV elements in a test plan - no matter where they are - are processed at the start.
 </p>
 <p>
 For simplicity, it is suggested that UDVs are placed only at the start of a Thread Group
 (or perhaps under the Test Plan itself).
 </p>
 <p>
 If a runtime element such as a User Parameters Pre-Processor or Regular Expression Extractor defines a variable
 with the same name as one of the global variables, then other test elements will see the local
 local value of the variable.
 The global value is not affected.
 </p>
 </description>
 <note>
 If you have more than one Thread Group, make sure you use different names for different values, as UDVs are shared between Thread Groups.
 Also, the variables are not available for use until after the element has been processed, 
 so you cannot use variables that are defined in the same element.
 You can reference variables defined in earlier UDVs or on the Test Plan. 
 </note>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="User Defined Variables" required="">Variable name/value pairs. The string under the "Name"
   	column is what you'll need to place inside the brackets in ${...} constructs to use the variables later on. The
   	whole ${...} will then be replaced by the string in the "Value" column.</property>
 </properties>
 </component>
 
 <component name="Random Variable" index="&sect-num;.4.14"  width="411" height="306" screenshot="random_variable.png">
 <description>
 <p>
 The Random Variable Config Element is used to generate random numeric strings and store them in variable for use later.
 It's simpler than using <complink name="User Defined Variables"/> together with the __Random() function.
 </p>
 <p>
 The output variable is constructed by using the random number generator,
 and then the resulting number is formatted using the format string.
 The number is calculated using the formula <code>minimum+Random.nextInt(maximum-minimum+1)</code>.
 Random.nextInt() requires a positive integer.
 This means that maximum-minimum - i.e. the range - must be less than 2147483647,
 however the minimum and maximum values can be any long values so long as the range is OK.
 </p>
 </description>
 
 <properties>
   <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree.</property>
   <property name="Variable Name" required="Yes">The name of the variable in which to store the random string.</property>
   <property name="Format String" required="No">The java.text.DecimalFormat format string to be used. 
   For example "000" which will generate numbers with at least 3 digits, 
   or "USER_000" which will generate output of the form USER_nnn. 
   If not specified, the default is to generate the number using Long.toString()</property>
   <property name="Minimum Value" required="Yes">The minimum value (long) of the generated random number.</property>
   <property name="Maximum Value" required="Yes">The maximum value (long) of the generated random number.</property>
   <property name="Random Seed" required="No">The seed for the random number generator. Default is the current time in milliseconds.</property>
   <property name="Per Thread(User)?" required="Yes">If False, the generator is shared between all threads in the thread group.
   If True, then each thread has its own random generator.</property>
 </properties>
 
 </component>
 
 <component name="Counter" index="&sect-num;.4.15"  width="399" height="244" screenshot="counter.png">
 <description><p>Allows the user to create a counter that can be referenced anywhere
 in the Thread Group.  The counter config lets the user configure a starting point, a maximum,
 and the increment.  The counter will loop from the start to the max, and then start over
 with the start, continuing on like that until the test is ended.  </p>
 <p>From version 2.1.2, the counter now uses a long to store the value, so the range is from -2^63 to 2^63-1.</p>
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Start" required="Yes">The starting number for the counter.  The counter will equal this
         number during the first iteration.</property>
         <property name="Increment" required="Yes">How much to increment the counter by after each
         iteration.</property>
         <property name="Maximum" required="No">If the counter exceeds the maximum, then it is reset to the Start value.
         For versions after 2.2 the default is Long.MAX_VALUE (previously it was 0).
         </property>
         <property name="Format" required="No">Optional format, e.g. 000 will format as 001, 002 etc. 
         This is passed to DecimalFormat, so any valid formats can be used.
         If there is a problem interpreting the format, then it is ignored.
     [The default format is generated using Long.toString()]
         </property>
         <property name="Reference Name" required="Yes">This controls how you refer to this value in other elements.  Syntax is
         as in <a href="functions.html">user-defined values</a>: <code>$(reference_name}</code>.</property>
         <property name="Track Counter Independently for each User" required="No">In other words, is this a global counter, or does each user get their
         own counter?  If unchecked, the counter is global (ie, user #1 will get value "1", and user #2 will get value "2" on
         the first iteration).  If checked, each user has an independent counter.</property>
 </properties>
 </component>
 
 <component name="Simple Config Element" index="&sect-num;.4.16"  width="393" height="245" screenshot="simple_config_element.png">
 <description><p>The Simple Config Element lets you add or override arbitrary values in samplers.  You can choose the name of the value
 and the value itself.  Although some adventurous users might find a use for this element, it's here primarily for developers as a basic
 GUI that they can use while developing new JMeter components.</p>
 </description>
 
 <properties>
         <property name="Name" required="Yes">Descriptive name for this element that is shown in the tree. </property>
   <property name="Parameter Name" required="Yes">The name of each parameter.  These values are internal to JMeter's workings and
   are not generally documented.  Only those familiar with the code will know these values.</property>
   <property name="Parameter Value" required="Yes">The value to apply to that parameter.</property>
 </properties>
 
 </component>
 
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.5 Assertions" anchor="assertions">
 <description>
 	<p>
 	Assertions are used to perform additional checks on samplers, and are processed after <b>every sampler</b>
 	in the same scope.
 	To ensure that an Assertion is applied only to a particular sampler, add it as a child of the sampler.
 	</p>
     <p>
     Note: Unless documented otherwise, Assertions are not applied to sub-samples (child samples) -
     only to the parent sample.
     In the case of BSF and BeanShell Assertions, the script can retrieve sub-samples using the method
     <code>prev.getSubResults()</code> which returns an array of SampleResults.
     The array will be empty if there are none.
     </p>
 	<note>
 	The variable <b>JMeterThread.last_sample_ok</b> is set to
 	"true" or "false" after all assertions for a sampler have been run.
 	 </note>
 </description>
 <component name="Response Assertion" index="&sect-num;.5.1" anchor="basic_assertion"  width="762" height="374" screenshot="assertion/assertion.png">
 
 <description><p>The response assertion control panel lets you add pattern strings to be compared against various
 	fields of the response.
 	The pattern strings are:
 	<ul>
 	<li>Contains, Matches: Perl5-style regular expressions</li>
 	<li>Equals, Substring: plain text, case-sensitive</li>
 	</ul>
 	</p>
 	<p>
 	A summary of the pattern matching characters can be found at <a href="http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html">http://jakarta.apache.org/oro/api/org/apache/oro/text/regex/package-summary.html</a>
 	</p>
 	<p>You can also choose whether the strings will be expected
 to <b>match</b> the entire response, or if the response is only expected to <b>contain</b> the
 pattern. You can attach multiple assertions to any controller for additional flexibility.</p>
 <p>Note that the pattern string should not include the enclosing delimiters, 
 	i.e. use <b>Price: \d+</b> not <b>/Price: \d+/</b>.
 	</p>
 	<p>
 	By default, the pattern is in multi-line mode, which means that the "." meta-character does not match newline.
     In multi-line mode, "^" and "$" match the start or end of any line anywhere within the string 
     - not just the start and end of the entire string. Note that \s does match new-line.
 	Case is also significant. To override these settings, one can use the <i>extended regular expression</i> syntax.
 	For example:
 </p>
 <pre>
 	(?i) - ignore case
 	(?s) - treat target as single line, i.e. "." matches new-line
 	(?is) - both the above
     These can be used anywhere within the expression and remain in effect until overriden.  e.g.
     (?i)apple(?-i) Pie - matches "ApPLe Pie", but not "ApPLe pIe"
     (?s)Apple.+?Pie - matches Apple followed by Pie, which may be on a subsequent line.
     Apple(?s).+?Pie - same as above, but it's probably clearer to use the (?s) at the start.  
 </pre>
 
 </description>
 <properties>
         <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Response Field to Test" required="Yes">Instructs JMeter which field of the Response to test.
         <ul>
         <li>Text Response - the response text from the server, i.e. the body, excluing any HTTP headers.</li>
         <li>URL sampled</li>
         <li>Response Code - e.g. 200</li>
         <li>Response Message - e.g. OK</li>
         <li>Response Headers (JMeter version 2.3RC3 and earlier included the headers with the Text)</li>
         </ul>
                 <p>
                 The overall success of the sample is determined by combining the result of the
                 assertion with the existing Response status.
                 When the Ignore Status checkbox is selected, the Response status is forced
                 to successful before evaluating the Assertion.
                 </p>
                 HTTP Responses with statuses in the 4xx and 5xx ranges are normally
                 regarded as unsuccessful. 
                 The "Ignore status" checkbox can be used to set the status successful before performing further checks.
                 Note that this will have the effect of clearing any previous assertion failures,
                 so make sure that this is only set on the first assertion.
                 </property>
         <property name="Pattern Matching Rules" required="Yes">Indicates how the text being tested
         is checked against the pattern.
         <ul>
         <li>Contains - true if the text contains the regular expression pattern</li>
         <li>Matches - true if the whole text matches the regular expression pattern</li>
         <li>Equals - true if the whole text equals the pattern string (case-sensitive)</li>
         <li>Substring - true if the text contains the pattern string (case-sensitive)</li>
         </ul>
         Equals and Substring patterns are plain strings, not regular expressions.
         NOT may also be selected to invert the result of the check.</property>
         <property name="Patterns to Test" required="Yes">A list of patterns to
         be tested.  
         Each pattern is tested separately. 
         If a pattern fails, then further patterns are not checked.
         There is no difference between setting up
         one Assertion with multiple patterns and setting up multiple Assertions with one
         pattern each (assuming the other options are the same).
         <b>However, when the Ignore Status checkbox is selected, this has the effect of cancelling any
         previous assertion failures - so make sure that the Ignore Status checkbox is only used on
         the first Assertion.</b>
         </property>
 </properties>
 <p>
 	The pattern is a Perl5-style regular expression, but without the enclosing brackets.
 </p>
 <example title="Assertion Examples" anchor="assertion_examples">
 <figure width="187" height="94" image="assertion/example1a.png">Figure 14 - Test Plan</figure>
 <figure width="629" height="333" image="assertion/example1b.png">Figure 15 - Assertion Control Panel with Pattern</figure>
 <figure width="474" height="265" image="assertion/example1c-pass.gif">Figure 16 - Assertion Listener Results (Pass)</figure>
 <figure width="474" height="265" image="assertion/example1c-fail.gif">Figure 17 - Assertion Listener Results (Fail)</figure>
 </example>
 
 
 </component>
 
 <component name="Duration Assertion" index="&sect-num;.5.2"  width="391" height="147" screenshot="duration_assertion.png">
 <description><p>The Duration Assertion tests that each response was received within a given amount
 of time.  Any response that takes longer than the given number of milliseconds (specified by the
 user) is marked as a failed response.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Duration in Milliseconds" required="Yes">The maximum number of milliseconds
         each response is allowed before being marked as failed.</property>
 
 </properties>
 </component>
 
 <component name="Size Assertion" index="&sect-num;.5.3"  width="331" height="346" screenshot="size_assertion.png">
 <description><p>The Size Assertion tests that each response contains the right number of bytes in it.  You can specify that
 the size be equal to, greater than, less than, or not equal to a given number of bytes.</p>
 <note>Since JMeter 2.3RC3, an empty response is treated as being 0 bytes rather than reported as an error.</note>
 </description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
         <property name="Size in bytes" required="Yes">The number of bytes to use in testing the size of the response.</property>
         <property name="Type of Comparison" required="Yes">Whether to test that the response is equal to, greater than, less than,
         or not equal to, the number of bytes specified.</property>
 
 </properties>
 </component>
 
 <component name="XML Assertion" index="&sect-num;.5.4"  width="303" height="196" screenshot="xml_assertion.png">
 <description><p>The XML Assertion tests that the response data consists of a formally correct XML document.  It does not
 validate the XML based on a DTD or schema or do any further validation.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 
 </properties>
 </component>
 
 <component name="BeanShell Assertion" index="&sect-num;.5.5"  width="597" height="303" screenshot="bsh_assertion.png">
 <description><p>The BeanShell Assertion allows the user to perform assertion checking using a BeanShell script.
 </p>
 <p>
 <b>Please note that the BeanShell jar file is not included with JMeter;
 it needs to be downloaded separately and placed in the lib directory.
 <br></br>
 For full details on using BeanShell, please see the BeanShell web-site at http://www.beanshell.org/.</b>
 </p><p>
 Note that a different Interpreter is used for each independent occurence of the assertion
 in each thread in a test script, but the same Interpreter is used for subsequent invocations.
 This means that variables persist across calls to the assertion.
 </p>
 <p>
 All Assertions are called from the same thread as the sampler.
 </p>
 <p>
 If the property "beanshell.assertion.init" is defined, it is passed to the Interpreter
 as the name of a sourced file. This can be used to define common methods and variables.
 There is a sample init file in the bin directory: BeanShellAssertion.bshrc
 </p>
 <p>
 The test element supports the ThreadListener and TestListener methods.
 These should be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 </description>
 
 <properties>
     <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
 	The parameters are stored in the following variables:
 	<ul>
 		<li>Parameters - string containing the parameters as a single variable</li>
 	    <li>bsh.args - String array containing parameters, split on white-space</li>
 	</ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run. This overrides the script.</property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. The return value is ignored.</property>
 </properties>
 <p>There's a <a href="../demos/BeanShellAssertion.bsh">sample script</a> you can try.</p>
 <p>The following variables are defined to the script. 
 These are strings unless otherwise noted:
 <ul>
 <li>log - the Logger Object. (e.g.) log.warn("Message"[,Throwable])</li>
 	<li>SampleResult - the SampleResult Object; read-write</li>
 	<li>Response - the response Object; read-write</li>
 	<li>Failure - boolean; read-write; used to set the Assertion status</li>
 	<li>FailureMessage - String; read-write; used to set the Assertion message</li>
 	<li>ResponseData - the response body (byte [])</li>
 	<li>ResponseCode - e.g. 200</li>
 	<li>ResponseMessage - e.g. OK</li>
 	<li>ResponseHeaders - contains the HTTP headers</li>
 	<li>RequestHeaders - contains the HTTP headers sent to the server</li>
 	<li>SampleLabel</li>
 	<li>SamplerData - data that was sent to the server</li>
 	<li>ctx - JMeterContext</li>
 	<li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.putObject("OBJ1",new Object());</li>
     <li>props - JMeter Properties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 </ul>
 </p>
 <p>The following methods of the Response object may be useful:
 <ul>
 	<li>setStopThread(boolean)</li>
 	<li>setStopTest(boolean)</li>
 	<li>String getSampleLabel()</li>
 	<li>setSampleLabel(String)</li>
 </ul></p>
 </component>
 
 <component name="MD5Hex Assertion" index="&sect-num;.5.6" width="411" height="149" screenshot="assertion/MD5HexAssertion.png">
 <description><p>The MD5Hex Assertion allows the user to check the MD5 hash of the response data.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="MD5 sum" required="Yes">32 hex digits representing the MD5 hash (case not significant)</property>
 
 </properties>
 </component>
 
 <component name="HTML Assertion" index="&sect-num;.5.7"  width="464" height="384" screenshot="assertion/HTMLAssertion.png">
 <description><p>The HTML Assertion allows the user to check the HTML syntax of the response data using JTidy.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="doctype" required="Yes">omit/auto/strict/loose</property>
 <property name="Format" required="Yes">HTML, XHTML or XML</property>
 <property name="Errors only" required="Yes">Only take note of errors?</property>
 <property name="Error threshold" required="Yes">Number of errors allowed before classing the response as failed</property>
 <property name="Warning threshold" required="Yes">Number of warnings allowed before classing the response as failed</property>
 <property name="Filename" required="No">Name of file to which report is written</property>
 
 </properties>
 </component>
 <component name="XPath Assertion" index="&sect-num;.5.8"  width="872" height="266" screenshot="xpath_assertion.png">
 <description><p>The XPath Assertion tests a document for well formedness, has the option
 of validating against a DTD, or putting the document through JTidy and testing for an
 XPath.  If that XPath exists, the Assertion is true.  Using "/" will match any well-formed
 document, and is the default XPath Expression. 
 The assertion also supports boolean expressions, such as "count(//*error)=2".
 See <a href="http://www.w3.org/TR/xpath">http://www.w3.org/TR/xpath</a> for more information
 on XPath.
 </p></description>
 
 <properties>
 <property name="Name"		required="No">Descriptive name for this element that is shown in the tree.</property>
 <property name="Tolerant Parser"	required="Yes">Be tolerant of XML/HTML errors (i.e. use Tidy)</property>
 <property name="Quiet"	required="If tolerant is selected">Sets the Tidy Quiet flag</property>
 <property name="Report Errors"	required="If tolerant is selected">If a Tidy error occurs, then set the Assertion accordingly</property>
 <property name="Show warnings"	required="If tolerant is selected">Sets the Tidy showWarnings option</property>
 <property name="Use Namespaces"	required="No">Should namespaces be honoured?</property>
 <property name="Validate XML"	required="No">Check the document against its schema.</property>
 <property name="XPath Assertion"		required="Yes">XPath to match in the document.</property>
 <property name="Ignore Whitespace"	required="No">Ignore Element Whitespace.</property>
 <property name="True if nothing matches"	required="No">True if a XPath expression is not matched</property>
 </properties>
 <note>
 The non-tolerant parser can be quite slow, as it may need to download the DTD etc.
 </note>
 </component>
 <component name="XML Schema Assertion" index="&sect-num;.5.9"  width="771" height="171" screenshot="assertion/XMLSchemaAssertion.png">
 <description><p>The XML Schema Assertion allows the user to validate a response against an XML Schema.</p></description>
 
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="File Name" required="Yes">Specify XML Schema File Name</property>
 </properties>
 </component>
 
 <component name="BSF Assertion" index="&sect-num;.5.10"  width="529" height="382" screenshot="bsf_assertion.png">
 <description>
 <p>
 The BSF Assertion allows BSF script code to be used to check the status of the previous sample.
 </p>
 </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this element that is shown in the tree.</property>
     <property name="Language" required="Yes">The BSF language to be used</property>
     <property name="Parameters" required="No">Parameters to pass to the script.
     The parameters are stored in the following variables:
     <ul>
         <li>Parameters - string containing the parameters as a single variable</li>
         <li>args - String array containing parameters, split on white-space</li>
     </ul></property>
     <property name="Script file" required="No">A file containing the script to run.</property>
     <property name="Script" required="Yes (unless script file is provided)">The script to run.</property>
 </properties>
 <p>
 The script (or file) is processed using the BSFEngine.exec() method, which does not return a value.
 </p>
 <p>The following variables are set up for use by the script:</p>
 <ul>
 <li>log - (Logger) - can be used to write to the log file</li>
 <li>Label - the String Label</li>
 <li>Filename - the script file name (if any)</li>
 <li>Parameters - the parameters (as a String)</li>
 <li>args[] - the parameters as a String array (split on whitespace)</li>
 <li>ctx - (JMeterContext) - gives access to the context</li>
 <li>vars - (JMeterVariables) - gives read/write access to variables: vars.get(key); vars.put(key,val); vars.putObject("OBJ1",new Object()); vars.getObject("OBJ2");</li>
 <li>props - JMeter Properties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 <li>prev - (SampleResult) - gives access to the previous SampleResult (if any)</li>
 <li>sampler - (Sampler)- gives access to the current sampler</li>
 <li>OUT - System.out - e.g. OUT.println("message")</li>
 <li>SampleResult - the current sample result (same as prev)</li>
 <li>AssertionResult - the assertion result</li>
 </ul>
 <p>
 The script can check various aspects of the SampleResult.
 If an error is detected, the script should use AssertionResult.setFailureMessage("message") and AssertionResult.setFailure(true).
 </p>
 <p>For futher details of all the methods available on each of the above variables, please check the Javadoc</p>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.6 Timers" anchor="timers">
 <description>
 	<br></br>
 	<p>
 	Note that timers are processed <b>before</b> each sampler in the scope in which they are found;
 	if there are several timers in the same scope, <b>all</b> the timers will be processed <b>before
 	each</b> sampler.
 	<br></br>
 	Timers are only processed in conjunction with a sampler.
 	A timer which is not in the same scope as a sampler will not be processed at all.
 	<br></br>
 	To apply a timer to a single sampler, add the timer as a child element of the sampler.
 	The timer will be applied before the sampler is executed.
 	To apply a timer after a sampler, either add it to the next sampler, or add it as the
 	child of a <complink name="Test Action"/> Sampler.
 	</p>
 </description>
 <component name="Constant Timer" index="&sect-num;.6.1" anchor="constant"  width="390" height="100" screenshot="timers/constant_timer.gif">
 <description>
 <p>If you want to have each thread pause for the same amount of time between
 requests, use this timer.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree.</property>
         <property name="Thread Delay" required="Yes">Number of milliseconds to pause.</property>
 </properties>
 </component>
 
 <component name="Gaussian Random Timer" index="&sect-num;.6.2"  width="390" height="182" screenshot="timers/gauss_random_timer.gif">
 
 <description><p>This timer pauses each thread request for a random amount of time, with most
 of the time intervals ocurring near a particular value.  The total delay is the
 sum of the Gaussian distributed value (with mean 0.0 and standard deviation 1.0) times
 the deviation value you specify, and the offset value.</p></description>
 
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree</property>
   <property name="Deviation" required="Yes">Deviation in milliseconds.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
 <component name="Uniform Random Timer" index="&sect-num;.6.3"  width="390" height="182" screenshot="timers/uniform_random_timer.gif">
 
 <description><p>This timer pauses each thread request for a random amount of time, with
 each time interval having the same probability of occurring. The total delay
 is the sum of the random value and the offset value.</p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this timer that is shown in the tree. </property>
   <property name="Random Delay Maximum" required="Yes">Maxium random number of milliseconds to
 pause.</property>
   <property name="Constant Delay Offset" required="Yes">Number of milliseconds to pause in addition
 to the random delay.</property>
 </properties>
 
 </component>
 
