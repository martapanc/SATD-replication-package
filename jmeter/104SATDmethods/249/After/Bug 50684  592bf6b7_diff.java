diff --git a/docs/images/screenshots/http-request.png b/docs/images/screenshots/http-request.png
index bec275671..b4cca2d51 100644
Binary files a/docs/images/screenshots/http-request.png and b/docs/images/screenshots/http-request.png differ
diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index 9b1290a4c..ee7885596 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1074 +1,1075 @@
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
 
 # Please add new entries in alphabetical order
 
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
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
 bsf_sampler_title=BSF Sampler
 bsf_script=Script to run (variables: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Script file to run
 bsf_script_language=Scripting language\:
 bsf_script_parameters=Parameters to pass to script/file\:
 bsh_assertion_script=Script (see below for variables that are defined)
 bsh_assertion_script_variables=The following variables are defined for the script:\nRead/Write: Failure, FailureMessage, SampleResult, vars, props, log.\nReadOnly: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=BeanShell Assertion
 bsh_function_expression=Expression to evaluate
 bsh_sampler_title=BeanShell Sampler
 bsh_script=Script (see below for variables that are defined)
 bsh_script_file=Script file
 bsh_script_parameters=Parameters (-> String Parameters and String []bsh.args)
 bsh_script_reset_interpreter=Reset bsh.Interpreter before each call
 bsh_script_variables=The following variables are defined for the script\:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=I'm busy testing, please stop the test before changing settings
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
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
 comparison_differ_content=Responses differ in content
 comparison_differ_time=Responses differ in response time by more than 
 comparison_invalid_node=Invalid Node 
 comparison_regex_string=Regex String
 comparison_regex_substitution=Substitution
 comparison_response_time=Response Time: 
 comparison_unit=\ ms
 comparison_visualizer_title=Comparison Assertion Visualizer
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
 cookies_stored=User-Defined Cookies
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
 ftp_put=put(STOR)
 ftp_remote_file=Remote File:
 ftp_sample_title=FTP Request Defaults
 ftp_save_response_data=Save File in Response ?
 ftp_testing_title=FTP Request
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
 generator_illegal_msg=Could not access the generator class due to IllegalAccessException.
 generator_instantiate_msg=Could not create an instance of the generator parser. Please make sure the generator implements Generator interface.
 get_xml_from_file=File with SOAP XML Data (overrides above text)
 get_xml_from_random=Message Folder
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
 grouping_in_transaction_controllers=Put each group in a new transaction controller
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
 http_implementation=Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_title=HTTP Mirror Server
 id_prefix=ID Prefix
 id_suffix=ID Suffix
 if_controller_evaluate_all=Evaluate for all children?
 if_controller_expression=Interpret Condition as Variable Expression?
 if_controller_label=Condition (default Javascript)
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
 ja=Japanese
 jar_file=Jar Files
 java_request=Java Request
 java_request_defaults=Java Request Defaults
 javascript_expression=JavaScript expression to evaluate
 jexl_expression=JEXL expression to evaluate
 jms_auth_required=Required
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_type=Client
 jms_communication_style=Communication style
 jms_concrete_connection_factory=Concrete Connection Factory
 jms_config=Message source
 jms_config_title=JMS Configuration
 jms_connection_factory=Connection Factory
 jms_correlation_title=Use alternate fields for message correlation
 jms_dest_setup=Setup
 jms_dest_setup_dynamic=Each sample
 jms_dest_setup_static=At startup
 jms_error_msg=Object message should read from an external file. Text input is currently selected, please remember to change it.
 jms_file=File
 jms_initial_context_factory=Initial Context Factory
 jms_itertions=Number of samples to aggregate
 jms_jndi_defaults_title=JNDI Default Configuration
 jms_jndi_props=JNDI Properties
 jms_map_message=Map Message
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
 jms_sample_title=JMS Default Request
 jms_send_queue=JNDI name Request queue
 jms_stop_between_samples=Stop between samples?
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_message=Text Message
 jms_timeout=Timeout (milliseconds)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File
 jms_use_req_msgid_as_correlid=Use Request Message Id
 jms_use_res_msgid_as_correlid=Use Response Message Id
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
 junit_junit4=Search for JUnit 4 annotations (instead of JUnit 3)
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
 ldapext_testing_title=LDAP Extended Request
 library=Library
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
 log_parser_illegal_msg=Could not access the class due to IllegalAccessException.
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
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
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
 menu_threads=Threads (Users)
 menu_fragments=Test Fragment
 menu_listener=Listener
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_tables=Table
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
 module_controller_module_to_run=Module To Run 
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
 monitor_label_prefix=Connection Prefix
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
 pl=Polish
 port=Port\:
 post_thread_group_title=Post Thread Group
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
 protocol=Protocol [http]\:
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
 proxy_daemon_bind_error=Could not create proxy - port in use. Choose another port.
 proxy_daemon_error=Could not create proxy - see log for details
 proxy_headers=Capture HTTP Headers
 proxy_httpsspoofing=Attempt HTTPS Spoofing
 proxy_httpsspoofing_match=Only spoof URLs matching:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP Proxy Server
 pt_br=Portugese (Brazilian)
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
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_render_no_text=Data response result isn't text.
 regexp_tester_title=RegExp Tester
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
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
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
 sample_scope=Apply to:
 sample_scope_all=Main sample and sub-samples
 sample_scope_children=Sub-samples only
 sample_scope_parent=Main sample only
 sample_scope_variable=JMeter Variable
 sampler_label=Label
 sampler_on_error_action=Action to be taken after a Sampler error
 sampler_on_error_continue=Continue
 sampler_on_error_start_next_loop=Start Next Loop
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
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
 save_idletime=Save Idle Time
 save_label=Save Label
 save_latency=Save Latency
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
 save_subresults=Save Sub Results (XML)
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
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_text_msg_not_found=Text not found
 search_text_title_not_found=Not found
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
 setup_thread_group_title=Setup Thread Group
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
 smime_assertion_issuer_dn=Issuer distinguished name
 smime_assertion_not_signed=Message not signed
 smime_assertion_message_position=Execute assertion on message at position
 smime_assertion_signature=Signature
 smime_assertion_signer=Signer certificate
 smime_assertion_signer_by_file=Certificate file
 smime_assertion_signer_constraints=Check values
 smime_assertion_signer_dn=Signer distinguished name
 smime_assertion_signer_email=Signer email address
 smime_assertion_signer_no_check=No check
 smime_assertion_signer_serial=Serial Number
 smime_assertion_title=SMIME Assertion
 smime_assertion_verify_signature=Verify signature
 smtp_additional_settings=Additional Settings
 smtp_attach_file=Attach file(s):
 smtp_attach_file_tooltip=Separate multiple files with ";"
 smtp_auth_settings=Auth settings
 smtp_bcc=Address To BCC:
 smtp_cc=Address To CC:
 smtp_default_port=(Defaults: SMTP:25, SSL:465, StartTLS:587)
 smtp_eml=Send .eml:
 smtp_enabledebug=Enable debug logging?
 smtp_enforcestarttls=Enforce StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in \"/tmp/\", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server_port=Port:
 smtp_server=Server:
 smtp_server_settings=Server settings
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_to=Address To:
 smtp_timestamp=Include timestamp in subject
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_header_add=Add Header
 smtp_header_remove=Remove
 smtp_header_name=Header Name
 smtp_header_value=Header Value
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
 split_function_separator=String to split on. Default is , (comma).
 split_function_string=String to split
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
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_title=Stopping Test
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
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
 tcp_classname=TCPClient classname\:
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
 test_action_stop_now=Stop Now
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
 textbox_cancel=Cancel
 textbox_close=Close
 textbox_save_close=Save & Close
 textbox_title_edit=Edit text
 textbox_title_view=View text
 textbox_tooltip_cell=Double click to view/edit
 thread_delay_properties=Thread Delay Properties
 thread_group_title=Thread Group
 test_fragment_title=Test Fragment
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
 transaction_controller_include_timers=Include timer duration in generated sample
 unbind=Thread Unbind
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
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
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
-use_multipart_for_http_post=Use multipart/form-data for HTTP POST
+use_multipart_for_http_post=Use multipart/form-data for POST
+use_multipart_mode_browser=Browser-compatible headers
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
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_load_time=Load time: 
 view_results_render=Render: 
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download embedded resources)
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
 view_results_size_in_bytes=Size in bytes: 
 view_results_tab_assertion=Assertion result
 view_results_tab_request=Request
 view_results_tab_response=Response data
 view_results_tab_sampler=Sampler result
 view_results_table_fields_key=Additional field
 view_results_table_fields_value=Value
 view_results_table_headers_key=Response header
 view_results_table_headers_value=Value
 view_results_table_request_headers_key=Request header
 view_results_table_request_headers_value=Value
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=Host
 view_results_table_request_http_method=Method
 view_results_table_request_http_nohttp=No HTTP Sample
 view_results_table_request_http_path=Path
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocol
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_testing2_source_ip=Source IP address:
 web_testing2_title=HTTP Request HTTPClient
 web_testing_embedded_url_pattern=Embedded URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources from HTML Files
 web_testing_title=HTTP Request
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
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
 xml_download_dtds=Fetch external DTDs
 xml_namespace_button=Use Namespaces
 xml_tolerant_button=Use Tidy (tolerant parser)
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
 xpath_extractor_fragment=Return entire XPath fragment instead of text content?
 xpath_extractor_query=XPath query:
 xpath_extractor_title=XPath Extractor
 xpath_file_file_name=XML file to get values from 
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
index eea2de2cc..6d16d40ab 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/config/gui/UrlConfigGui.java
@@ -1,514 +1,525 @@
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
 
 package org.apache.jmeter.protocol.http.config.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.FlowLayout;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextField;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
 import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
-import org.apache.jmeter.testelement.AbstractTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledChoice;
 
 /**
  * Basic URL / HTTP Request configuration:
  * - host and port
  * - connect and response timeouts
  * - path, method, encoding, parameters
  * - redirects & keepalive
  */
 public class UrlConfigGui extends JPanel implements ChangeListener {
+
     private static final long serialVersionUID = 240L;
 
     private HTTPArgumentsPanel argsPanel;
 
     private JTextField domain;
 
     private JTextField port;
 
     private JTextField proxyHost;
 
     private JTextField proxyPort;
 
     private JTextField proxyUser;
 
     private JTextField proxyPass;
 
     private JTextField connectTimeOut;
 
     private JTextField responseTimeOut;
 
     private JTextField protocol;
 
     private JTextField contentEncoding;
 
     private JTextField path;
 
     private JCheckBox followRedirects;
 
     private JCheckBox autoRedirects;
 
     private JCheckBox useKeepAlive;
 
     private JCheckBox useMultipartForPost;
 
+    private JCheckBox useBrowserCompatibleMultipartMode;
+
     private JLabeledChoice method;
     
     private JLabeledChoice httpImplementation;
 
     private final boolean notConfigOnly;
     // set this false to suppress some items for use in HTTP Request defaults
     
     private final boolean showImplementation; // Set false for AJP
 
     public UrlConfigGui() {
         this(true);
     }
 
     public UrlConfigGui(boolean value) {
         this(value, true);
     }
 
     public UrlConfigGui(boolean showSamplerFields, boolean showImplementation) {
         notConfigOnly=showSamplerFields;
         this.showImplementation = showImplementation;
         init();
     }
 
     public void clear() {
         domain.setText(""); // $NON-NLS-1$
         if (notConfigOnly){
             followRedirects.setSelected(true);
             autoRedirects.setSelected(false);
             method.setText(HTTPSamplerBase.DEFAULT_METHOD);
             useKeepAlive.setSelected(true);
             useMultipartForPost.setSelected(false);
+            useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
         if (showImplementation) {
             httpImplementation.setText(""); // $NON-NLS-1$
         }
         path.setText(""); // $NON-NLS-1$
         port.setText(""); // $NON-NLS-1$
         proxyHost.setText(""); // $NON-NLS-1$
         proxyPort.setText(""); // $NON-NLS-1$
         proxyUser.setText(""); // $NON-NLS-1$
         proxyPass.setText(""); // $NON-NLS-1$
         connectTimeOut.setText(""); // $NON-NLS-1$
         responseTimeOut.setText(""); // $NON-NLS-1$
         protocol.setText(""); // $NON-NLS-1$
         contentEncoding.setText(""); // $NON-NLS-1$
         argsPanel.clear();
     }
 
     public TestElement createTestElement() {
         ConfigTestElement element = new ConfigTestElement();
 
         element.setName(this.getName());
         element.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
         element.setProperty(TestElement.TEST_CLASS, element.getClass().getName());
         modifyTestElement(element);
         return element;
     }
 
     /**
      * Save the GUI values in the sampler.
      *
      * @param element
      */
     public void modifyTestElement(TestElement element) {
         Arguments args = (Arguments) argsPanel.createTestElement();
 
         HTTPArgument.convertArgumentsToHTTP(args);
         element.setProperty(new TestElementProperty(HTTPSamplerBase.ARGUMENTS, args));
         element.setProperty(HTTPSamplerBase.DOMAIN, domain.getText());
         element.setProperty(HTTPSamplerBase.PORT, port.getText());
         element.setProperty(HTTPSamplerBase.PROXYHOST, proxyHost.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPORT, proxyPort.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYUSER, proxyUser.getText(),"");
         element.setProperty(HTTPSamplerBase.PROXYPASS, proxyPass.getText(),"");
         element.setProperty(HTTPSamplerBase.CONNECT_TIMEOUT, connectTimeOut.getText());
         element.setProperty(HTTPSamplerBase.RESPONSE_TIMEOUT, responseTimeOut.getText());
         element.setProperty(HTTPSamplerBase.PROTOCOL, protocol.getText());
         element.setProperty(HTTPSamplerBase.CONTENT_ENCODING, contentEncoding.getText());
         element.setProperty(HTTPSamplerBase.PATH, path.getText());
         if (notConfigOnly){
             element.setProperty(HTTPSamplerBase.METHOD, method.getText());
             element.setProperty(new BooleanProperty(HTTPSamplerBase.FOLLOW_REDIRECTS, followRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.AUTO_REDIRECTS, autoRedirects.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.USE_KEEPALIVE, useKeepAlive.isSelected()));
             element.setProperty(new BooleanProperty(HTTPSamplerBase.DO_MULTIPART_POST, useMultipartForPost.isSelected()));
+            element.setProperty(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, useBrowserCompatibleMultipartMode.isSelected(),HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
         }
         if (showImplementation) {
             element.setProperty(HTTPSamplerBase.IMPLEMENTATION, httpImplementation.getText(),"");
         }
     }
 
     /**
      * Set the text, etc. in the UI.
      *
      * @param el
      *            contains the data to be displayed
      */
     public void configure(TestElement el) {
         setName(el.getName());
         argsPanel.configure((TestElement) el.getProperty(HTTPSamplerBase.ARGUMENTS).getObjectValue());
         domain.setText(el.getPropertyAsString(HTTPSamplerBase.DOMAIN));
 
         String portString = el.getPropertyAsString(HTTPSamplerBase.PORT);
 
         // Only display the port number if it is meaningfully specified
         if (portString.equals(HTTPSamplerBase.UNSPECIFIED_PORT_AS_STRING)) {
             port.setText(""); // $NON-NLS-1$
         } else {
             port.setText(portString);
         }
         proxyHost.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYHOST));
         proxyPort.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPORT));
         proxyUser.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYUSER));
         proxyPass.setText(el.getPropertyAsString(HTTPSamplerBase.PROXYPASS));
         connectTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.CONNECT_TIMEOUT));
         responseTimeOut.setText(el.getPropertyAsString(HTTPSamplerBase.RESPONSE_TIMEOUT));
         protocol.setText(el.getPropertyAsString(HTTPSamplerBase.PROTOCOL));
         contentEncoding.setText(el.getPropertyAsString(HTTPSamplerBase.CONTENT_ENCODING));
         path.setText(el.getPropertyAsString(HTTPSamplerBase.PATH));
         if (notConfigOnly){
             method.setText(el.getPropertyAsString(HTTPSamplerBase.METHOD));
-            followRedirects.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.FOLLOW_REDIRECTS));
-            autoRedirects.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.AUTO_REDIRECTS));
-            useKeepAlive.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.USE_KEEPALIVE));
-            useMultipartForPost.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.DO_MULTIPART_POST));
+            followRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.FOLLOW_REDIRECTS));
+            autoRedirects.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.AUTO_REDIRECTS));
+            useKeepAlive.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.USE_KEEPALIVE));
+            useMultipartForPost.setSelected(el.getPropertyAsBoolean(HTTPSamplerBase.DO_MULTIPART_POST));
+            useBrowserCompatibleMultipartMode.setSelected(el.getPropertyAsBoolean(
+                    HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART, HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT));
         }
         if (showImplementation) {
             httpImplementation.setText(el.getPropertyAsString(HTTPSamplerBase.IMPLEMENTATION));
         }
     }
 
     private void init() {// called from ctor, so must not be overridable
         this.setLayout(new BorderLayout());
 
         // WEB REQUEST PANEL
         JPanel webRequestPanel = new JPanel();
         webRequestPanel.setLayout(new BorderLayout());
         webRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_request"))); // $NON-NLS-1$
 
         JPanel northPanel = new JPanel();
         northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
         northPanel.add(getProtocolAndMethodPanel());
         northPanel.add(getPathPanel());
 
         webRequestPanel.add(northPanel, BorderLayout.NORTH);
         webRequestPanel.add(getParameterPanel(), BorderLayout.CENTER);
 
         this.add(getWebServerTimeoutPanel(), BorderLayout.NORTH);
         this.add(webRequestPanel, BorderLayout.CENTER);
         this.add(getProxyServerPanel(), BorderLayout.SOUTH);
     }
 
     /**
      * Create a panel containing the webserver (domain+port) and timeouts (connect+request).
      *
      * @return the panel
      */
     protected final JPanel getWebServerTimeoutPanel() {
         // WEB SERVER PANEL
         JPanel webServerPanel = new HorizontalPanel();
         webServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server"))); // $NON-NLS-1$
         final JPanel domainPanel = getDomainPanel();
         final JPanel portPanel = getPortPanel();
         webServerPanel.add(domainPanel, BorderLayout.CENTER);
         webServerPanel.add(portPanel, BorderLayout.EAST);
 
         JPanel timeOut = new HorizontalPanel();
         timeOut.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_server_timeout_title"))); // $NON-NLS-1$
         final JPanel connPanel = getConnectTimeOutPanel();
         final JPanel reqPanel = getResponseTimeOutPanel();
         timeOut.add(connPanel);
         timeOut.add(reqPanel);
 
         JPanel webServerTimeoutPanel = new VerticalPanel();
         webServerTimeoutPanel.add(webServerPanel, BorderLayout.CENTER);
         webServerTimeoutPanel.add(timeOut, BorderLayout.EAST);
 
         JPanel bigPanel = new VerticalPanel();
         bigPanel.add(webServerTimeoutPanel);
         return bigPanel;
     }
 
     /**
      * Create a panel containing the proxy server details
      *
      * @return the panel
      */
     protected final JPanel getProxyServerPanel(){
         JPanel proxyServer = new HorizontalPanel();
         proxyServer.add(getProxyHostPanel(), BorderLayout.CENTER);
         proxyServer.add(getProxyPortPanel(), BorderLayout.EAST);
 
         JPanel proxyLogin = new HorizontalPanel();
         proxyLogin.add(getProxyUserPanel());
         proxyLogin.add(getProxyPassPanel());
 
         JPanel proxyServerPanel = new HorizontalPanel();
         proxyServerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                 JMeterUtils.getResString("web_proxy_server_title"))); // $NON-NLS-1$
         proxyServerPanel.add(proxyServer, BorderLayout.CENTER);
         proxyServerPanel.add(proxyLogin, BorderLayout.EAST);
 
         return proxyServerPanel;
     }
 
     private JPanel getPortPanel() {
         port = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(port);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(port, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getProxyPortPanel() {
         proxyPort = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
         label.setLabelFor(proxyPort);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPort, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getConnectTimeOutPanel() {
         connectTimeOut = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_connect")); // $NON-NLS-1$
         label.setLabelFor(connectTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(connectTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getResponseTimeOutPanel() {
         responseTimeOut = new JTextField(4);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_timeout_response")); // $NON-NLS-1$
         label.setLabelFor(responseTimeOut);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(responseTimeOut, BorderLayout.CENTER);
 
         return panel;
     }
 
     private JPanel getDomainPanel() {
         domain = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(domain);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(domain, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyHostPanel() {
         proxyHost = new JTextField(20);
 
         JLabel label = new JLabel(JMeterUtils.getResString("web_server_domain")); // $NON-NLS-1$
         label.setLabelFor(proxyHost);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyHost, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyUserPanel() {
         proxyUser = new JTextField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("username")); // $NON-NLS-1$
         label.setLabelFor(proxyUser);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyUser, BorderLayout.CENTER);
         return panel;
     }
 
     private JPanel getProxyPassPanel() {
         proxyPass = new JTextField(5);
 
         JLabel label = new JLabel(JMeterUtils.getResString("password")); // $NON-NLS-1$
         label.setLabelFor(proxyPass);
 
         JPanel panel = new JPanel(new BorderLayout(5, 0));
         panel.add(label, BorderLayout.WEST);
         panel.add(proxyPass, BorderLayout.CENTER);
         return panel;
     }
 
     /**
      * This method defines the Panel for the HTTP path, 'Follow Redirects'
      * 'Use KeepAlive', and 'Use multipart for HTTP POST' elements.
      *
      * @return JPanel The Panel for the path, 'Follow Redirects' and 'Use
      *         KeepAlive' elements.
      */
     protected Component getPathPanel() {
         path = new JTextField(15);
 
         JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
         label.setLabelFor(path);
 
         if (notConfigOnly){
             followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects")); // $NON-NLS-1$
             followRedirects.setSelected(true);
             followRedirects.addChangeListener(this);
 
             autoRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects_auto")); //$NON-NLS-1$
             autoRedirects.addChangeListener(this);
             autoRedirects.setSelected(false);// Default changed in 2.3 and again in 2.4
 
             useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
             useKeepAlive.setSelected(true);
 
             useMultipartForPost = new JCheckBox(JMeterUtils.getResString("use_multipart_for_http_post")); // $NON-NLS-1$
             useMultipartForPost.setSelected(false);
+
+            useBrowserCompatibleMultipartMode = new JCheckBox(JMeterUtils.getResString("use_multipart_mode_browser")); // $NON-NLS-1$
+            useBrowserCompatibleMultipartMode.setSelected(HTTPSamplerBase.BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
+
         }
 
         JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
         pathPanel.add(label, BorderLayout.WEST);
         pathPanel.add(path, BorderLayout.CENTER);
         pathPanel.setMinimumSize(pathPanel.getPreferredSize());
 
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(pathPanel);
         if (notConfigOnly){
             JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
             optionPanel.add(autoRedirects);
             optionPanel.add(followRedirects);
             optionPanel.add(useKeepAlive);
-             optionPanel.add(useMultipartForPost);
+            optionPanel.add(useMultipartForPost);
+            optionPanel.add(useBrowserCompatibleMultipartMode);
             optionPanel.setMinimumSize(optionPanel.getPreferredSize());
             panel.add(optionPanel);
         }
 
         return panel;
     }
 
     protected JPanel getProtocolAndMethodPanel() {
 
         // Implementation
         
         if (showImplementation) {
             httpImplementation = new JLabeledChoice(JMeterUtils.getResString("http_implementation"), // $NON-NLS-1$
                     HTTPSamplerFactory.getImplementations());
             httpImplementation.addValue("");
         }
         // PROTOCOL
         protocol = new JTextField(4);
         JLabel protocolLabel = new JLabel(JMeterUtils.getResString("protocol")); // $NON-NLS-1$
         protocolLabel.setLabelFor(protocol);        
         
         // CONTENT_ENCODING
         contentEncoding = new JTextField(10);
         JLabel contentEncodingLabel = new JLabel(JMeterUtils.getResString("content_encoding")); // $NON-NLS-1$
         contentEncodingLabel.setLabelFor(contentEncoding);
 
         if (notConfigOnly){
             method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
                     HTTPSamplerBase.getValidMethodsAsArray());
         }
 
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
 
         if (showImplementation) {
             panel.add(httpImplementation);
         }
         panel.add(protocolLabel);
         panel.add(protocol);
         panel.add(Box.createHorizontalStrut(5));
 
         if (notConfigOnly){
             panel.add(method);
         }
         panel.setMinimumSize(panel.getPreferredSize());
         panel.add(Box.createHorizontalStrut(5));
 
         panel.add(contentEncodingLabel);
         panel.add(contentEncoding);
         panel.setMinimumSize(panel.getPreferredSize());
         return panel;
     }
 
     protected JPanel getParameterPanel() {
         argsPanel = new HTTPArgumentsPanel();
 
         return argsPanel;
     }
 
     // autoRedirects and followRedirects cannot both be selected
     public void stateChanged(ChangeEvent e) {
         if (e.getSource() == autoRedirects){
             if (autoRedirects.isSelected()) {
                 followRedirects.setSelected(false);
             }
         }
         if (e.getSource() == followRedirects){
             if (followRedirects.isSelected()) {
                 autoRedirects.setSelected(false);
             }
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
index f80507e06..ddd916ea6 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPAbstractImpl.java
@@ -1,247 +1,254 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.samplers.Interruptible;
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Base class for HTTP implementations used by the HTTPSamplerProxy sampler.
  */
 public abstract class HTTPAbstractImpl implements Interruptible, HTTPConstantsInterface {
 
     protected final HTTPSamplerBase testElement;
 
     protected HTTPAbstractImpl(HTTPSamplerBase testElement){
         this.testElement = testElement;
     }
 
     protected abstract HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth);
 
     // Allows HTTPSamplerProxy to call threadFinished; subclasses can override if necessary
     protected void threadFinished() {
     }
 
     // Provide access to HTTPSamplerBase methods
     
     /**
      * Invokes {@link HTTPSamplerBase#errorResult(Throwable, HTTPSampleResult)}
      */
     protected HTTPSampleResult errorResult(Throwable t, HTTPSampleResult res) {
         return testElement.errorResult(t, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getArguments()}
      */
     protected Arguments getArguments() {
         return testElement.getArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAuthManager()}
      */
     protected AuthManager getAuthManager() {
         return testElement.getAuthManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getAutoRedirects()}
      */
     protected boolean getAutoRedirects() {
         return testElement.getAutoRedirects();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCacheManager()}
      */
     protected CacheManager getCacheManager() {
         return testElement.getCacheManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getConnectTimeout()}
      */
     protected int getConnectTimeout() {
         return testElement.getConnectTimeout();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getContentEncoding()}
      */
     protected String getContentEncoding() {
         return testElement.getContentEncoding();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getCookieManager()}
      */
     protected CookieManager getCookieManager() {
         return testElement.getCookieManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHeaderManager()}
      */
     protected HeaderManager getHeaderManager() {
         return testElement.getHeaderManager();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getHTTPFiles()}
      */
     protected HTTPFileArg[] getHTTPFiles() {
         return testElement.getHTTPFiles();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getIpSource()}
      */
     protected String getIpSource() {
         return testElement.getIpSource();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyHost()}
      */
     protected String getProxyHost() {
         return testElement.getProxyHost();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPass()}
      */
     protected String getProxyPass() {
         return testElement.getProxyPass();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyPortInt()}
      */
     protected int getProxyPortInt() {
         return testElement.getProxyPortInt();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getProxyUser()}
      */
     protected String getProxyUser() {
         return testElement.getProxyUser();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getResponseTimeout()}
      */
     protected int getResponseTimeout() {
         return testElement.getResponseTimeout();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getSendFileAsPostBody()}
      */
     protected boolean getSendFileAsPostBody() {
         return testElement.getSendFileAsPostBody();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getSendParameterValuesAsPostBody()}
      */
     protected boolean getSendParameterValuesAsPostBody() {
         return testElement.getSendParameterValuesAsPostBody();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseKeepAlive()}
      */
     protected boolean getUseKeepAlive() {
         return testElement.getUseKeepAlive();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#getUseMultipartForPost()}
      */
     protected boolean getUseMultipartForPost() {
         return testElement.getUseMultipartForPost();
     }
 
     /**
+     * Invokes {@link HTTPSamplerBase#getDoBrowserCompatibleMultipart()}
+     */
+    protected boolean getDoBrowserCompatibleMultipart() {
+        return testElement.getDoBrowserCompatibleMultipart();
+    }
+
+    /**
      * Invokes {@link HTTPSamplerBase#hasArguments()}
      */
     protected boolean hasArguments() {
         return testElement.hasArguments();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isMonitor()}
      */
     protected boolean isMonitor() {
         return testElement.isMonitor();
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#isSuccessCode(int)}
      */
     protected boolean isSuccessCode(int errorLevel) {
         return testElement.isSuccessCode(errorLevel);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
      */
     protected byte[] readResponse(SampleResult res, InputStream instream,
             int responseContentLength) throws IOException {
         return testElement.readResponse(res, instream, responseContentLength);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#readResponse(SampleResult, InputStream, int)}
      */
     protected byte[] readResponse(SampleResult res, BufferedInputStream in,
             int contentLength) throws IOException {
         return testElement.readResponse(res, in, contentLength);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#resultProcessing(boolean, int, HTTPSampleResult)}
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect,
             int frameDepth, HTTPSampleResult res) {
         return testElement.resultProcessing(areFollowingRedirect, frameDepth, res);
     }
 
     /**
      * Invokes {@link HTTPSamplerBase#setUseKeepAlive(boolean)}
      */
     protected void setUseKeepAlive(boolean b) {
         testElement.setUseKeepAlive(b);
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
index 8e2d9219b..33f98aef9 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC3Impl.java
@@ -1,1061 +1,1067 @@
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
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.zip.GZIPInputStream;
 
 import org.apache.commons.httpclient.Header;
 import org.apache.commons.httpclient.HostConfiguration;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.HttpConnectionManager;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.HttpMethodBase;
 import org.apache.commons.httpclient.HttpState;
 import org.apache.commons.httpclient.HttpVersion;
 import org.apache.commons.httpclient.NTCredentials;
 import org.apache.commons.httpclient.ProtocolException;
 import org.apache.commons.httpclient.SimpleHttpConnectionManager;
 import org.apache.commons.httpclient.auth.AuthScope;
 import org.apache.commons.httpclient.cookie.CookiePolicy;
 import org.apache.commons.httpclient.methods.DeleteMethod;
 import org.apache.commons.httpclient.methods.FileRequestEntity;
 import org.apache.commons.httpclient.methods.GetMethod;
 import org.apache.commons.httpclient.methods.HeadMethod;
 import org.apache.commons.httpclient.methods.OptionsMethod;
 import org.apache.commons.httpclient.methods.PostMethod;
 import org.apache.commons.httpclient.methods.PutMethod;
 import org.apache.commons.httpclient.methods.StringRequestEntity;
 import org.apache.commons.httpclient.methods.TraceMethod;
 import org.apache.commons.httpclient.methods.multipart.FilePart;
 import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
 import org.apache.commons.httpclient.methods.multipart.Part;
 import org.apache.commons.httpclient.methods.multipart.PartBase;
 import org.apache.commons.httpclient.methods.multipart.StringPart;
 import org.apache.commons.httpclient.params.HttpClientParams;
 import org.apache.commons.httpclient.params.HttpMethodParams;
 import org.apache.commons.httpclient.protocol.Protocol;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.LoopbackHttpClientSocketFactory;
 import org.apache.jmeter.protocol.http.util.SlowHttpClientSocketFactory;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.SSLManager;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 /**
  * HTTP sampler using Apache (Jakarta) Commons HttpClient 3.1.
  */
 public class HTTPHC3Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final boolean canSetPreEmptive; // OK to set pre-emptive auth?
 
     private static final ThreadLocal<Map<HostConfiguration, HttpClient>> httpClients = 
         new ThreadLocal<Map<HostConfiguration, HttpClient>>(){
         @Override
         protected Map<HostConfiguration, HttpClient> initialValue() {
             return new HashMap<HostConfiguration, HttpClient>();
         }
     };
 
     // Needs to be accessible by HTTPSampler2
     volatile HttpClient savedClient;
 
     static {
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             Protocol.registerProtocol(PROTOCOL_HTTP,
                     new Protocol(PROTOCOL_HTTP,new SlowHttpClientSocketFactory(CPS_HTTP),DEFAULT_HTTP_PORT));
         }
 
         // Now done in JsseSSLManager (which needs to register the protocol)
 //        cps =
 //            JMeterUtils.getPropDefault("httpclient.socket.https.cps", 0); // $NON-NLS-1$
 //
 //        if (cps > 0) {
 //            log.info("Setting up HTTPS SlowProtocol, cps="+cps);
 //            Protocol.registerProtocol(PROTOCOL_HTTPS,
 //                    new Protocol(PROTOCOL_HTTPS,new SlowHttpClientSocketFactory(cps),DEFAULT_HTTPS_PORT));
 //        }
 
         // Set default parameters as needed
         HttpClientParams params = new HttpClientParams();
 
         // Process Commons HttpClient parameters file
         String file=JMeterUtils.getProperty("httpclient.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, params);
         }
 
         // If the pre-emptive parameter is undefined, then we can set it as needed
         // otherwise we should do what the user requested.
         canSetPreEmptive =  params.isAuthenticationPreemptive();
 
         // Handle old-style JMeter properties
         try {
             params.setParameter(HttpMethodParams.PROTOCOL_VERSION, HttpVersion.parse("HTTP/"+HTTP_VERSION));
         } catch (ProtocolException e) {
             log.warn("Problem setting protocol version "+e.getLocalizedMessage());
         }
 
         if (SO_TIMEOUT >= 0){
             params.setIntParameter(HttpMethodParams.SO_TIMEOUT, SO_TIMEOUT);
         }
 
         // This must be done last, as must not be overridden
         params.setParameter(HttpMethodParams.COOKIE_POLICY,CookiePolicy.IGNORE_COOKIES);
         // We do our own cookie handling
 
         if (USE_LOOPBACK){
             LoopbackHttpClientSocketFactory.setup();
         }
     }
 
     protected HTTPHC3Impl(HTTPSamplerBase base) {
         super(base);
     }
 
 
     /**
      * Samples the URL passed in and stores the result in
      * <code>HTTPSampleResult</code>, following redirects and downloading
      * page resources as appropriate.
      * <p>
      * When getting a redirect target, redirects are not followed and resources
      * are not downloaded. The caller will take care of this.
      *
      * @param url
      *            URL to sample
      * @param method
      *            HTTP method: GET, POST,...
      * @param areFollowingRedirect
      *            whether we're getting a redirect target
      * @param frameDepth
      *            Depth of this target in the frame structure. Used only to
      *            prevent infinite recursion.
      * @return results of the sampling
      */
     @Override
     protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
 
         String urlStr = url.toString();
 
         log.debug("Start : sample " + urlStr);
         log.debug("method " + method);
 
         HttpMethodBase httpMethod = null;
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(urlStr); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         res.sampleStart(); // Count the retries as well in the time
         try {
             // May generate IllegalArgumentException
             if (method.equals(POST)) {
                 httpMethod = new PostMethod(urlStr);
             } else if (method.equals(PUT)){
                 httpMethod = new PutMethod(urlStr);
             } else if (method.equals(HEAD)){
                 httpMethod = new HeadMethod(urlStr);
             } else if (method.equals(TRACE)){
                 httpMethod = new TraceMethod(urlStr);
             } else if (method.equals(OPTIONS)){
                 httpMethod = new OptionsMethod(urlStr);
             } else if (method.equals(DELETE)){
                 httpMethod = new DeleteMethod(urlStr);
             } else if (method.equals(GET)){
                 httpMethod = new GetMethod(urlStr);
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
 
             final CacheManager cacheManager = getCacheManager();
             if (cacheManager != null && GET.equalsIgnoreCase(method)) {
                if (cacheManager.inCache(url)) {
                    res.sampleEnd();
                    res.setResponseNoContent();
                    res.setSuccessful(true);
                    return res;
                }
             }
 
             // Set any default request headers
             setDefaultRequestHeaders(httpMethod);
 
             // Setup connection
             HttpClient client = setupConnection(url, httpMethod, res);
             savedClient = client;
 
             // Handle the various methods
             if (method.equals(POST)) {
                 String postBody = sendPostData((PostMethod)httpMethod);
                 res.setQueryString(postBody);
             } else if (method.equals(PUT)) {
                 String putBody = sendPutData((PutMethod)httpMethod);
                 res.setQueryString(putBody);
             }
 
             int statusCode = client.executeMethod(httpMethod);
 
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders(httpMethod));
 
             // Request sent. Now get the response:
             InputStream instream = httpMethod.getResponseBodyAsStream();
 
             if (instream != null) {// will be null for HEAD
                 try {
                     Header responseHeader = httpMethod.getResponseHeader(HEADER_CONTENT_ENCODING);
                     if (responseHeader!= null && ENCODING_GZIP.equals(responseHeader.getValue())) {
                         instream = new GZIPInputStream(instream);
                     }
                     res.setResponseData(readResponse(res, instream, (int) httpMethod.getResponseContentLength()));
                 } finally {
                     JOrphanUtils.closeQuietly(instream);
                 }
             }
 
             res.sampleEnd();
             // Done with the sampling proper.
 
             // Now collect the results into the HTTPSampleResult:
 
             res.setSampleLabel(httpMethod.getURI().toString());
             // Pick up Actual path (after redirects)
 
             res.setResponseCode(Integer.toString(statusCode));
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseMessage(httpMethod.getStatusText());
 
             String ct = null;
             Header h = httpMethod.getResponseHeader(HEADER_CONTENT_TYPE);
             if (h != null)// Can be missing, e.g. on redirect
             {
                 ct = h.getValue();
                 res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                 res.setEncodingAndType(ct);
             }
 
             res.setResponseHeaders(getResponseHeaders(httpMethod));
             if (res.isRedirect()) {
                 final Header headerLocation = httpMethod.getResponseHeader(HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 res.setRedirectLocation(headerLocation.getValue());
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 res.setURL(new URL(httpMethod.getURI().toString()));
             }
 
             // Store any cookies received in the cookie manager:
             saveConnectionCookies(httpMethod, res.getURL(), getCookieManager());
 
             // Save cache information
             if (cacheManager != null){
                 cacheManager.saveDetails(httpMethod, res);
             }
 
             // Follow redirects and download page resources if appropriate:
             res = resultProcessing(areFollowingRedirect, frameDepth, res);
 
             log.debug("End : sample");
             return res;
         } catch (IllegalArgumentException e)// e.g. some kinds of invalid URL
         {
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         } catch (IOException e) {
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         } finally {
             savedClient = null;
             if (httpMethod != null) {
                 httpMethod.releaseConnection();
             }
         }
     }
 
     /**
      * Returns an <code>HttpConnection</code> fully ready to attempt
      * connection. This means it sets the request method (GET or POST), headers,
      * cookies, and authorization for the URL request.
      * <p>
      * The request infos are saved into the sample result if one is provided.
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param httpMethod
      *            GET/PUT/HEAD etc
      * @param res
      *            sample result to save request infos to
      * @return <code>HttpConnection</code> ready for .connect
      * @exception IOException
      *                if an I/O Exception occurs
      */
     protected HttpClient setupConnection(URL u, HttpMethodBase httpMethod, HTTPSampleResult res) throws IOException {
 
         String urlStr = u.toString();
 
         org.apache.commons.httpclient.URI uri = new org.apache.commons.httpclient.URI(urlStr,false);
 
         String schema = uri.getScheme();
         if ((schema == null) || (schema.length()==0)) {
             schema = PROTOCOL_HTTP;
         }
 
         if (PROTOCOL_HTTPS.equalsIgnoreCase(schema)){
             SSLManager.getInstance(); // ensure the manager is initialised
             // we don't currently need to do anything further, as this sets the default https protocol
         }
 
         Protocol protocol = Protocol.getProtocol(schema);
 
         String host = uri.getHost();
         int port = uri.getPort();
 
         /*
          *  We use the HostConfiguration as the key to retrieve the HttpClient,
          *  so need to ensure that any items used in its equals/hashcode methods are
          *  not changed after use, i.e.:
          *  host, port, protocol, localAddress, proxy
          *
         */
         HostConfiguration hc = new HostConfiguration();
         hc.setHost(host, port, protocol); // All needed to ensure re-usablility
 
         // Set up the local address if one exists
         if (localAddress != null){
             hc.setLocalAddress(localAddress);
         } else {
             final String ipSource = getIpSource();
             if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
                 InetAddress inetAddr = InetAddress.getByName(ipSource);
                 hc.setLocalAddress(inetAddr);
             }
         }
 
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         if (useDynamicProxy){
             hc.setProxy(proxyHost, proxyPort);
             useStaticProxy = false; // Dynamic proxy overrules static proxy
         } else if (useStaticProxy) {
             if (log.isDebugEnabled()){
                 log.debug("Setting proxy: "+PROXY_HOST+":"+PROXY_PORT);
             }
             hc.setProxy(PROXY_HOST, PROXY_PORT);
         }
 
         Map<HostConfiguration, HttpClient> map = httpClients.get();
         // N.B. HostConfiguration.equals() includes proxy settings in the compare.
         HttpClient httpClient = map.get(hc);
 
         if ( httpClient == null )
         {
             httpClient = new HttpClient(new SimpleHttpConnectionManager());
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
             httpClient.setHostConfiguration(hc);
             map.put(hc, httpClient);
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // Set up any required Proxy credentials
         if (useDynamicProxy){
             String user = getProxyUser();
             if (user.length() > 0){
                 httpClient.getState().setProxyCredentials(
                         new AuthScope(proxyHost,proxyPort,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(user,getProxyPass(),localHost,PROXY_DOMAIN)
                     );
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         } else {
             if (useStaticProxy) {
                 if (PROXY_USER.length() > 0){
                     httpClient.getState().setProxyCredentials(
                         new AuthScope(PROXY_HOST,PROXY_PORT,null,AuthScope.ANY_SCHEME),
                         new NTCredentials(PROXY_USER,PROXY_PASS,localHost,PROXY_DOMAIN)
                     );
                 }
             } else {
                 httpClient.getState().clearProxyCredentials();
             }
         }
 
         int rto = getResponseTimeout();
         if (rto > 0){
             httpMethod.getParams().setSoTimeout(rto);
         }
 
         int cto = getConnectTimeout();
         if (cto > 0){
             httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(cto);
         }
 
 
         // Allow HttpClient to handle the redirects:
         httpMethod.setFollowRedirects(getAutoRedirects());
 
         // a well-behaved browser is supposed to send 'Connection: close'
         // with the last request to an HTTP server. Instead, most browsers
         // leave it to the server to close the connection after their
         // timeout period. Leave it to the JMeter user to decide.
         if (getUseKeepAlive()) {
             httpMethod.setRequestHeader(HEADER_CONNECTION, KEEP_ALIVE);
         } else {
             httpMethod.setRequestHeader(HEADER_CONNECTION, CONNECTION_CLOSE);
         }
 
         setConnectionHeaders(httpMethod, u, getHeaderManager(), getCacheManager());
         String cookies = setConnectionCookie(httpMethod, u, getCookieManager());
 
         setConnectionAuthorization(httpClient, u, getAuthManager());
 
         if (res != null) {
             res.setCookies(cookies);
         }
 
         return httpClient;
     }
 
     /**
      * Set any default request headers to include
      *
      * @param httpMethod the HttpMethod used for the request
      */
     protected void setDefaultRequestHeaders(HttpMethod httpMethod) {
         // Method left empty here, but allows subclasses to override
     }
 
     /**
      * Gets the ResponseHeaders
      *
      * @param method the method used to perform the request
      * @return string containing the headers, one per line
      */
     protected String getResponseHeaders(HttpMethod method) {
         StringBuilder headerBuf = new StringBuilder();
         org.apache.commons.httpclient.Header rh[] = method.getResponseHeaders();
         headerBuf.append(method.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             String key = rh[i].getName();
             headerBuf.append(key);
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
     }
 
     /**
      * Extracts all the required cookies for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in.
      *
      * @param method <code>HttpMethod</code> for the request
      * @param u <code>URL</code> of the request
      * @param cookieManager the <code>CookieManager</code> containing all the cookies
      * @return a String containing the cookie details (for the response)
      * May be null
      */
     private String setConnectionCookie(HttpMethod method, URL u, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(u);
             if (cookieHeader != null) {
                 method.setRequestHeader(HEADER_COOKIE, cookieHeader);
             }
         }
         return cookieHeader;
     }
 
     /**
      * Extracts all the required non-cookie headers for that particular URL request and
      * sets them in the <code>HttpMethod</code> passed in
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @param u
      *            <code>URL</code> of the URL request
      * @param headerManager
      *            the <code>HeaderManager</code> containing all the cookies
      *            for this <code>UrlConfig</code>
      * @param cacheManager the CacheManager (may be null)
      */
     private void setConnectionHeaders(HttpMethod method, URL u, HeaderManager headerManager, CacheManager cacheManager) {
         // Set all the headers from the HeaderManager
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // This helps with SoapSampler hack too
                     // TODO - what other headers are not allowed?
                     if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         method.addRequestHeader(n, v);
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(u, method);
         }
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     protected String getConnectionHeaders(HttpMethod method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getRequestHeaders();
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
     }
 
 
     /**
      * Extracts all the required authorization for that particular URL request
      * and sets it in the <code>HttpMethod</code> passed in.
      *
      * @param client the HttpClient object
      *
      * @param u
      *            <code>URL</code> of the URL request
      * @param authManager
      *            the <code>AuthManager</code> containing all the authorisations for
      *            this <code>UrlConfig</code>
      */
     private void setConnectionAuthorization(HttpClient client, URL u, AuthManager authManager) {
         HttpState state = client.getState();
         if (authManager != null) {
             HttpClientParams params = client.getParams();
             Authorization auth = authManager.getAuthForURL(u);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " >  D="+ username + " D="+domain+" R="+realm);
                     }
                     state.setCredentials(
                             new AuthScope(u.getHost(),u.getPort(),
                                     realm.length()==0 ? null : realm //"" is not the same as no realm
                                     ,AuthScope.ANY_SCHEME),
                             // NT Includes other types of Credentials
                             new NTCredentials(
                                     username,
                                     auth.getPass(),
                                     localHost,
                                     domain
                             ));
                     // We have credentials - should we set pre-emptive authentication?
                     if (canSetPreEmptive){
                         log.debug("Setting Pre-emptive authentication");
                         params.setAuthenticationPreemptive(true);
                     }
             } else {
                 state.clearCredentials();
                 if (canSetPreEmptive){
                     params.setAuthenticationPreemptive(false);
                 }
             }
         } else {
             state.clearCredentials();
         }
     }
 
 
     /*
      * Send POST data from <code>Entry</code> to the open connection.
      *
      * @param connection
      *            <code>URLConnection</code> where POST data should be sent
      * @return a String show what was posted. Will not contain actual file upload content
      * @exception IOException
      *                if an I/O exception occurs
      */
     private String sendPostData(PostMethod post) throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg files[] = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(contentEncoding != null && contentEncoding.length() == 0) {
                 contentEncoding = null;
             }
 
+            final boolean browserCompatible = getDoBrowserCompatibleMultipart();
             // We don't know how many entries will be skipped
             ArrayList<PartBase> partlist = new ArrayList<PartBase>();
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
-               HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
-               String parameterName = arg.getName();
-               if (arg.isSkippable(parameterName)){
-                   continue;
-               }
-               partlist.add(new StringPart(arg.getName(), arg.getValue(), contentEncoding));
+                HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
+                String parameterName = arg.getName();
+                if (arg.isSkippable(parameterName)){
+                    continue;
+                }
+                StringPart part = new StringPart(arg.getName(), arg.getValue(), contentEncoding);
+                if (browserCompatible) {
+                    part.setTransferEncoding(null);
+                    part.setContentType(null);
+                }
+                partlist.add(part);
             }
 
             // Add any files
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 File inputFile = new File(file.getPath());
                 // We do not know the char set of the file to be uploaded, so we set it to null
                 ViewableFilePart filePart = new ViewableFilePart(file.getParamName(), inputFile, file.getMimeType(), null);
                 filePart.setCharSet(null); // We do not know what the char set of the file is
                 partlist.add(filePart);
             }
 
             // Set the multipart for the post
             int partNo = partlist.size();
             Part[] parts = partlist.toArray(new Part[partNo]);
             MultipartRequestEntity multiPart = new MultipartRequestEntity(parts, post.getParams());
             post.setRequestEntity(multiPart);
 
             // Set the content type
             String multiPartContentType = multiPart.getContentType();
             post.setRequestHeader(HEADER_CONTENT_TYPE, multiPartContentType);
 
             // If the Multipart is repeatable, we can send it first to
             // our own stream, without the actual file content, so we can return it
             if(multiPart.isRepeatable()) {
                 // For all the file multiparts, we must tell it to not include
                 // the actual file content
                 for(int i = 0; i < partNo; i++) {
                     if(parts[i] instanceof ViewableFilePart) {
                         ((ViewableFilePart) parts[i]).setHideFileData(true); // .sendMultipartWithoutFileContent(bos);
                     }
                 }
                 // Write the request to our own stream
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 multiPart.writeRequest(bos);
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
                         contentEncoding == null ? "US-ASCII" // $NON-NLS-1$ this is the default used by HttpClient
                         : contentEncoding));
                 bos.close();
 
                 // For all the file multiparts, we must revert the hiding of
                 // the actual file content
                 for(int i = 0; i < partNo; i++) {
                     if(parts[i] instanceof ViewableFilePart) {
                         ((ViewableFilePart) parts[i]).setHideFileData(false);
                     }
                 }
             }
             else {
                 postedBody.append("<Multipart was not repeatable, cannot view what was sent>"); // $NON-NLS-1$
             }
         }
         else {
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             Header contentTypeHeader = post.getRequestHeader(HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(file.getPath()),null);
                 post.setRequestEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             }
             else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 String contentEncoding = getContentEncoding();
                 boolean haveContentEncoding = false;
                 if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                     post.getParams().setContentCharset(contentEncoding);
                     haveContentEncoding = true;
                 } else if (contentEncoding != null && contentEncoding.trim().length() == 0){
                     contentEncoding=null;
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
                             post.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         String value;
                         if (haveContentEncoding){
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     StringRequestEntity requestEntity = new StringRequestEntity(postBody.toString(), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     post.setRequestEntity(requestEntity);
                 }
                 else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setRequestHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
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
                             String urlContentEncoding = contentEncoding;
                             if(urlContentEncoding == null || urlContentEncoding.length() == 0) {
                                 // Use the default encoding for urls
                                 urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                             }
                             parameterName = URLDecoder.decode(parameterName, urlContentEncoding);
                             parameterValue = URLDecoder.decode(parameterValue, urlContentEncoding);
                         }
                         // Add the parameter, httpclient will urlencode it
                         post.addParameter(parameterName, parameterValue);
                     }
 
 /*
 //                    // Alternative implementation, to make sure that HTTPSampler and HTTPSampler2
 //                    // sends the same post body.
 //
 //                    // Only include the content char set in the content-type header if it is not
 //                    // an APPLICATION_X_WWW_FORM_URLENCODED content type
 //                    String contentCharSet = null;
 //                    if(!post.getRequestHeader(HEADER_CONTENT_TYPE).getValue().equals(APPLICATION_X_WWW_FORM_URLENCODED)) {
 //                        contentCharSet = post.getRequestCharSet();
 //                    }
 //                    StringRequestEntity requestEntity = new StringRequestEntity(getQueryString(contentEncoding), post.getRequestHeader(HEADER_CONTENT_TYPE).getValue(), contentCharSet);
 //                    post.setRequestEntity(requestEntity);
 */
                 }
 
                 // If the request entity is repeatable, we can send it first to
                 // our own stream, so we can return it
                 if(post.getRequestEntity().isRepeatable()) {
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     post.getRequestEntity().writeRequest(bos);
                     bos.flush();
                     // We get the posted bytes using the encoding used to create it
                     postedBody.append(new String(bos.toByteArray(),post.getRequestCharSet()));
                     bos.close();
                 }
                 else {
                     postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                 }
             }
         }
         // Set the content length
         post.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(post.getRequestEntity().getContentLength()));
 
         return postedBody.toString();
     }
 
     /**
      * Set up the PUT data
      */
     private String sendPutData(PutMethod put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type for a POST request
         Header contentTypeHeader = put.getRequestHeader(HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
         HTTPFileArg files[] = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileRequestEntity fileRequestEntity = new FileRequestEntity(new File(files[0].getPath()),null);
             put.setRequestEntity(fileRequestEntity);
 
             // We just add placeholder text for file content
             putBody.append("<actual file content, not shown here>");
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
             hasPutBody = true;
 
             // If a content encoding is specified, we set it as http parameter, so that
             // the post body will be encoded in the specified content encoding
             final String contentEncoding = getContentEncoding();
             boolean haveContentEncoding = false;
             if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                 put.getParams().setContentCharset(contentEncoding);
                 haveContentEncoding = true;
             }
 
             // Just append all the parameter values, and use that as the post body
             StringBuilder putBodyContent = new StringBuilder();
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String value = null;
                 if (haveContentEncoding){
                     value = arg.getEncodedValue(contentEncoding);
                 } else {
                     value = arg.getEncodedValue();
                 }
                 putBodyContent.append(value);
             }
             String contentTypeValue = null;
             if(hasContentTypeHeader) {
                 contentTypeValue = put.getRequestHeader(HEADER_CONTENT_TYPE).getValue();
             }
             StringRequestEntity requestEntity = new StringRequestEntity(putBodyContent.toString(), contentTypeValue, put.getRequestCharSet());
             put.setRequestEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasPutBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             if(put.getRequestEntity().isRepeatable()) {
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 put.getRequestEntity().writeRequest(bos);
                 bos.flush();
                 // We get the posted bytes using the charset that was used to create them
                 putBody.append(new String(bos.toByteArray(),put.getRequestCharSet()));
                 bos.close();
             }
             else {
                 putBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
             }
             if(!hasContentTypeHeader) {
                 // Allow the mimetype of the file to control the content type
                 // This is not obvious in GUI if you are not uploading any files,
                 // but just sending the content of nameless parameters
                 // TODO: needs a multiple file upload scenerio
                 HTTPFileArg file = files.length > 0? files[0] : null;
                 if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                     put.setRequestHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                 }
             }
             // Set the content length
             put.setRequestHeader(HEADER_CONTENT_LENGTH, Long.toString(put.getRequestEntity().getContentLength()));
             return putBody.toString();
         }
         return null;
     }
 
     /**
      * Class extending FilePart, so that we can send placeholder text
      * instead of the actual file content
      */
     private static class ViewableFilePart extends FilePart {
         private boolean hideFileData;
 
         public ViewableFilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
             super(name, file, contentType, charset);
             this.hideFileData = false;
         }
 
         public void setHideFileData(boolean hideFileData) {
             this.hideFileData = hideFileData;
         }
 
         @Override
         protected void sendData(OutputStream out) throws IOException {
             // Check if we should send only placeholder text for the
             // file content, or the real file content
             if(hideFileData) {
                 out.write("<actual file content, not shown here>".getBytes());// encoding does not really matter here
             }
             else {
                 super.sendData(out);
             }
         }
     }
 
     /**
      * From the <code>HttpMethod</code>, store all the "set-cookie" key-pair
      * values in the cookieManager of the <code>UrlConfig</code>.
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @param u
      *            <code>URL</code> of the URL request
      * @param cookieManager
      *            the <code>CookieManager</code> containing all the cookies
      */
     protected void saveConnectionCookies(HttpMethod method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header hdr[] = method.getResponseHeaders(HEADER_SET_COOKIE);
             for (int i = 0; i < hdr.length; i++) {
                 cookieManager.addCookieFromHeader(hdr[i].getValue(),u);
             }
         }
     }
 
 
     @Override
     public void threadFinished() {
         log.debug("Thread Finished");
 
         // Does not need to be synchronised, as all access is from same thread
         Map<HostConfiguration, HttpClient> map = httpClients.get();
 
         if ( map != null ) {
             for (HttpClient cl : map.values())
             {
                 // Can cause NPE in HttpClient 3.1
                 //((SimpleHttpConnectionManager)cl.getHttpConnectionManager()).shutdown();// Closes the connection
                 // Revert to original method:
                 cl.getHttpConnectionManager().closeIdleConnections(-1000);// Closes the connection
             }
             map.clear();
         }
     }
 
     /** {@inheritDoc} */
     public boolean interrupt() {
         HttpClient client = savedClient;
         if (client != null) {
             savedClient = null;
             // TODO - not sure this is the best method
             final HttpConnectionManager httpConnectionManager = client.getHttpConnectionManager();
             if (httpConnectionManager instanceof SimpleHttpConnectionManager) {// Should be true
                 ((SimpleHttpConnectionManager)httpConnectionManager).shutdown();
             }
         }
         return client != null;
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index 9376f3859..15b5c704f 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,997 +1,999 @@
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
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.URI;
 import java.net.URL;
 import java.net.URLDecoder;
 import java.nio.charset.Charset;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.http.Header;
 import org.apache.http.HttpEntity;
 import org.apache.http.HttpHost;
 import org.apache.http.HttpRequest;
 import org.apache.http.HttpResponse;
 import org.apache.http.NameValuePair;
 import org.apache.http.StatusLine;
 import org.apache.http.auth.AuthScope;
 import org.apache.http.auth.NTCredentials;
 import org.apache.http.auth.UsernamePasswordCredentials;
 import org.apache.http.client.CredentialsProvider;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpDelete;
 import org.apache.http.client.methods.HttpGet;
 import org.apache.http.client.methods.HttpHead;
 import org.apache.http.client.methods.HttpOptions;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.client.methods.HttpPut;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.methods.HttpTrace;
 import org.apache.http.client.methods.HttpUriRequest;
 import org.apache.http.client.params.ClientPNames;
 import org.apache.http.conn.params.ConnRoutePNames;
 import org.apache.http.conn.scheme.Scheme;
 import org.apache.http.conn.scheme.SchemeRegistry;
 import org.apache.http.entity.FileEntity;
 import org.apache.http.entity.StringEntity;
 import org.apache.http.entity.mime.FormBodyPart;
+import org.apache.http.entity.mime.HttpMultipartMode;
 import org.apache.http.entity.mime.MultipartEntity;
 import org.apache.http.entity.mime.content.FileBody;
 import org.apache.http.entity.mime.content.StringBody;
 import org.apache.http.impl.client.AbstractHttpClient;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.params.BasicHttpParams;
 import org.apache.http.params.CoreConnectionPNames;
 import org.apache.http.params.CoreProtocolPNames;
 import org.apache.http.params.DefaultedHttpParams;
 import org.apache.http.params.HttpParams;
 import org.apache.http.protocol.BasicHttpContext;
 import org.apache.http.protocol.ExecutionContext;
 import org.apache.http.protocol.HttpContext;
 
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.Authorization;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.SlowHC4SocketFactory;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * HTTP Sampler using Apache HttpClient 4.x.
  * 
  *                        INITIAL IMPLEMENTATION - SUBJECT TO CHANGE 
  */
 public class HTTPHC4Impl extends HTTPHCAbstractImpl {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 240L;
 
     private static final ThreadLocal<Map<HttpClientKey, HttpClient>> HTTPCLIENTS = 
         new ThreadLocal<Map<HttpClientKey, HttpClient>>(){
         @Override
         protected Map<HttpClientKey, HttpClient> initialValue() {
             return new HashMap<HttpClientKey, HttpClient>();
         }
     };
 
     // Scheme used for slow sockets. Cannot be set as a default, because must be set on an HttpClient instance.
     private static final Scheme SLOW_HTTP;
     private static final Scheme SLOW_HTTPS;
 
     /*
      * Create a set of default parameters from the ones initially created.
      * This allows the defaults to be overridden if necessary from the properties file.
      */
     private static final HttpParams DEFAULT_HTTP_PARAMS;
     
     static {
         
         // TODO use new setDefaultHttpParams(HttpParams params) static method when 4.1 is available
         final DefaultHttpClient dhc = new DefaultHttpClient();
         DEFAULT_HTTP_PARAMS = dhc.getParams(); // Get the default params
         dhc.getConnectionManager().shutdown(); // Tidy up
         
         // Process Apache HttpClient parameters file
         String file=JMeterUtils.getProperty("hc.parameters.file"); // $NON-NLS-1$
         if (file != null) {
             HttpClientDefaultParameters.load(file, DEFAULT_HTTP_PARAMS);
         }
 
         if (CPS_HTTP > 0) {
             log.info("Setting up HTTP SlowProtocol, cps="+CPS_HTTP);
             SLOW_HTTP = new Scheme(PROTOCOL_HTTP, DEFAULT_HTTP_PORT, new SlowHC4SocketFactory(CPS_HTTP));
         } else {
             SLOW_HTTP = null;
         }
         if (CPS_HTTPS > 0) {
             SLOW_HTTPS = new Scheme(PROTOCOL_HTTPS, DEFAULT_HTTPS_PORT, new SlowHC4SocketFactory(CPS_HTTPS));
         } else {
             SLOW_HTTPS = null;
         }
         if (localAddress != null){
             DEFAULT_HTTP_PARAMS.setParameter(ConnRoutePNames.LOCAL_ADDRESS, localAddress);
         }
         
     }
 
     private volatile HttpUriRequest currentRequest; // Accessed from multiple threads
 
     protected HTTPHC4Impl(HTTPSamplerBase testElement) {
         super(testElement);
     }
 
     @Override
     protected HTTPSampleResult sample(URL url, String method,
             boolean areFollowingRedirect, int frameDepth) {
 
         // TODO cookie handling
         
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
 
         res.setSampleLabel(url.toString()); // May be replaced later
         res.setHTTPMethod(method);
         res.setURL(url);
 
         HttpClient httpClient = setupClient(url);
         
         HttpRequestBase httpRequest = null;
         try {
             URI uri = url.toURI();
             if (method.equals(POST)) {
                 httpRequest = new HttpPost(uri);
             } else if (method.equals(PUT)) {
                 httpRequest = new HttpPut(uri);
             } else if (method.equals(HEAD)) {
                 httpRequest = new HttpHead(uri);
             } else if (method.equals(TRACE)) {
                 httpRequest = new HttpTrace(uri);
             } else if (method.equals(OPTIONS)) {
                 httpRequest = new HttpOptions(uri);
             } else if (method.equals(DELETE)) {
                 httpRequest = new HttpDelete(uri);
             } else if (method.equals(GET)) {
                 httpRequest = new HttpGet(uri);
             } else {
                 throw new IllegalArgumentException("Unexpected method: "+method);
             }
             setupRequest(url, httpRequest, res); // can throw IOException
         } catch (Exception e) {
             res.sampleStart();
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         }
 
         HttpContext localContext = new BasicHttpContext();
 
         res.sampleStart();
 
         final CacheManager cacheManager = getCacheManager();
         if (cacheManager != null && GET.equalsIgnoreCase(method)) {
            if (cacheManager.inCache(url)) {
                res.sampleEnd();
                res.setResponseNoContent();
                res.setSuccessful(true);
                return res;
            }
         }
 
         try {
             currentRequest = httpRequest;
             // Handle the various methods
             if (method.equals(POST)) {
                 String postBody = sendPostData((HttpPost)httpRequest);
                 res.setQueryString(postBody);
             } else if (method.equals(PUT)) {
                 String putBody = sendPutData((HttpPut)httpRequest);
                 res.setQueryString(putBody);
             }
             HttpResponse httpResponse = httpClient.execute(httpRequest, localContext); // perform the sample
 
             // Needs to be done after execute to pick up all the headers
             res.setRequestHeaders(getConnectionHeaders((HttpRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST)));
 
             HttpEntity entity = httpResponse.getEntity();
             if (entity != null) {
                 InputStream instream = entity.getContent();
                 res.setResponseData(readResponse(res, instream, (int) entity.getContentLength()));
                 Header contentType = entity.getContentType();
                 if (contentType != null){
                     String ct = contentType.getValue();
                     res.setContentType(ct);
                     res.setEncodingAndType(ct);                    
                 }
             }
             
             res.sampleEnd(); // Done with the sampling proper.
             currentRequest = null;
 
             // Now collect the results into the HTTPSampleResult:
             StatusLine statusLine = httpResponse.getStatusLine();
             int statusCode = statusLine.getStatusCode();
             res.setResponseCode(Integer.toString(statusCode));
             res.setResponseMessage(statusLine.getReasonPhrase());
             res.setSuccessful(isSuccessCode(statusCode));
 
             res.setResponseHeaders(getResponseHeaders(httpResponse));
             if (res.isRedirect()) {
                 final Header headerLocation = httpResponse.getLastHeader(HEADER_LOCATION);
                 if (headerLocation == null) { // HTTP protocol violation, but avoids NPE
                     throw new IllegalArgumentException("Missing location header");
                 }
                 res.setRedirectLocation(headerLocation.getValue());
             }
 
             // If we redirected automatically, the URL may have changed
             if (getAutoRedirects()){
                 HttpUriRequest req = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);
                 HttpHost target = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
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
             res.sampleEnd();
             HTTPSampleResult err = errorResult(e, res);
             err.setSampleLabel("Error: " + url.toString());
             return err;
         } finally {
             currentRequest = null;
         }
         return res;
     }
 
     /**
      * Holder class for all fields that define an HttpClient instance;
      * used as the key to the ThreadLocal map of HttpClient instances.
      */
     private static final class HttpClientKey {
 
         private final URL url;
         private final boolean hasProxy;
         private final String proxyHost;
         private final int proxyPort;
         private final String proxyUser;
         private final String proxyPass;
         
         private final int hashCode; // Always create hash because we will always need it
 
         public HttpClientKey(URL url, boolean b, String proxyHost,
                 int proxyPort, String proxyUser, String proxyPass) {
             this.url = url;
             this.hasProxy = b;
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
             hash = hash*31 + url.toString().hashCode();
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
             if (obj instanceof HttpClientKey) {
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
                 this.url.toString().equals(other.url.toString());                
             }
             // No proxy, so don't check proxy fields
             return 
                 this.hasProxy == other.hasProxy &&
                 this.url.toString().equals(other.url.toString())
             ;
             
         }
 
         @Override
         public int hashCode(){
             return hashCode;
         }
     }
 
     private HttpClient setupClient(URL url) {
 
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         
         final String host = url.getHost();
         final String proxyHost = getProxyHost();
         final int proxyPort = getProxyPortInt();
 
         boolean useStaticProxy = isStaticProxy(host);
         boolean useDynamicProxy = isDynamicProxy(proxyHost, proxyPort);
 
         // Lookup key - must agree with all the values used to create the HttpClient.
         HttpClientKey key = new HttpClientKey(url, (useStaticProxy || useDynamicProxy), 
                 useDynamicProxy ? proxyHost : PROXY_HOST,
                 useDynamicProxy ? proxyPort : PROXY_PORT,
                 useDynamicProxy ? getProxyUser() : PROXY_USER,
                 useDynamicProxy ? getProxyPass() : PROXY_PASS);
         
         HttpClient httpClient = map.get(key);
 
         if (httpClient == null){
 
             HttpParams clientParams = new DefaultedHttpParams(new BasicHttpParams(), DEFAULT_HTTP_PARAMS);
             
             httpClient = new DefaultHttpClient(clientParams);
             
             if (SLOW_HTTP != null){
                 SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
                 schemeRegistry.register(SLOW_HTTP);
             }
             if (SLOW_HTTPS != null){
                 SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
                 schemeRegistry.register(SLOW_HTTPS);
             }
 
             // Set up proxy details
             if (useDynamicProxy){
                 HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 String proxyUser = getProxyUser();
                 if (proxyUser.length() > 0) {
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(proxyHost, proxyPort),
                             new UsernamePasswordCredentials(proxyUser, getProxyPass()));
                 }
             } else if (useStaticProxy) {
                 HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
                 clientParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                 if (PROXY_USER.length() > 0)
                     ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                             new AuthScope(PROXY_HOST, PROXY_PORT),
                             new UsernamePasswordCredentials(PROXY_USER, PROXY_PASS));
             }
             
             // TODO set up SSL manager etc.
             
             if (log.isDebugEnabled()) {
                 log.debug("Created new HttpClient: @"+System.identityHashCode(httpClient));
             }
 
             map.put(key, httpClient); // save the agent for next time round
         } else {
             if (log.isDebugEnabled()) {
                 log.debug("Reusing the HttpClient: @"+System.identityHashCode(httpClient));
             }
         }
 
         // TODO - should this be done when the client is created?
         // If so, then the details need to be added as part of HttpClientKey
         setConnectionAuthorization(httpClient, url, getAuthManager());
 
         return httpClient;
     }
 
     private void setupRequest(URL url, HttpRequestBase httpRequest, HTTPSampleResult res)
         throws IOException {
 
     HttpParams requestParams = httpRequest.getParams();
     
     // Set up the local address if one exists
     final String ipSource = getIpSource();
     if (ipSource.length() > 0) {// Use special field ip source address (for pseudo 'ip spoofing')
         InetAddress inetAddr = InetAddress.getByName(ipSource);
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
         httpRequest.setHeader(HEADER_CONNECTION, KEEP_ALIVE);
     } else {
         httpRequest.setHeader(HEADER_CONNECTION, CONNECTION_CLOSE);
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
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response) {
         StringBuilder headerBuf = new StringBuilder();
         Header[] rh = response.getAllHeaders();
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (int i = 0; i < rh.length; i++) {
             headerBuf.append(rh[i].getName());
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(rh[i].getValue());
             headerBuf.append("\n"); // $NON-NLS-1$
         }
         return headerBuf.toString();
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
     private String setConnectionCookie(HttpRequest request, URL url, CookieManager cookieManager) {
         String cookieHeader = null;
         if (cookieManager != null) {
             cookieHeader = cookieManager.getCookieHeaderForURL(url);
             if (cookieHeader != null) {
                 request.setHeader(HEADER_COOKIE, cookieHeader);
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
     private void setConnectionHeaders(HttpRequestBase request, URL url, HeaderManager headerManager, CacheManager cacheManager) {
         if (headerManager != null) {
             CollectionProperty headers = headerManager.getHeaders();
             if (headers != null) {
                 PropertyIterator i = headers.iterator();
                 while (i.hasNext()) {
                     org.apache.jmeter.protocol.http.control.Header header
                     = (org.apache.jmeter.protocol.http.control.Header)
                        i.next().getObjectValue();
                     String n = header.getName();
                     // Don't allow override of Content-Length
                     // TODO - what other headers are not allowed?
                     if (! HEADER_CONTENT_LENGTH.equalsIgnoreCase(n)){
                         String v = header.getValue();
                         request.addHeader(n, v);
                     }
                 }
             }
         }
         if (cacheManager != null){
             cacheManager.setHeaders(url, request);
         }
     }
 
     /**
      * Get all the request headers for the <code>HttpMethod</code>
      *
      * @param method
      *            <code>HttpMethod</code> which represents the request
      * @return the headers as a string
      */
     private String getConnectionHeaders(HttpRequest method) {
         // Get all the request headers
         StringBuilder hdrs = new StringBuilder(100);
         Header[] requestHeaders = method.getAllHeaders();
         for(int i = 0; i < requestHeaders.length; i++) {
             // Exclude the COOKIE header, since cookie is reported separately in the sample
             if(!HEADER_COOKIE.equalsIgnoreCase(requestHeaders[i].getName())) {
                 hdrs.append(requestHeaders[i].getName());
                 hdrs.append(": "); // $NON-NLS-1$
                 hdrs.append(requestHeaders[i].getValue());
                 hdrs.append("\n"); // $NON-NLS-1$
             }
         }
 
         return hdrs.toString();
     }
 
     private void setConnectionAuthorization(HttpClient client, URL url, AuthManager authManager) {
         CredentialsProvider credentialsProvider = 
             ((AbstractHttpClient) client).getCredentialsProvider();
         if (authManager != null) {
             Authorization auth = authManager.getAuthForURL(url);
             if (auth != null) {
                     String username = auth.getUser();
                     String realm = auth.getRealm();
                     String domain = auth.getDomain();
                     if (log.isDebugEnabled()){
                         log.debug(username + " > D="+domain+" R="+realm);
                     }
                     credentialsProvider.setCredentials(
                             new AuthScope(url.getHost(), url.getPort(), realm.length()==0 ? null : realm),
                             new NTCredentials(username, auth.getPass(), localHost, domain));
             } else {
                 credentialsProvider.clear();
             }
         } else {
             credentialsProvider.clear();            
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
     private String sendPostData(HttpPost post)  throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
         HTTPFileArg files[] = getHTTPFiles();
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(getUseMultipartForPost()) {
             // If a content encoding is specified, we use that as the
             // encoding of any parameter values
             String contentEncoding = getContentEncoding();
             if(contentEncoding != null && contentEncoding.length() == 0) {
                 contentEncoding = null;
             }
 
             // Write the request to our own stream
-            MultipartEntity multiPart = new MultipartEntity();
+            MultipartEntity multiPart = new MultipartEntity(
+                    getDoBrowserCompatibleMultipart() ? HttpMultipartMode.BROWSER_COMPATIBLE : HttpMultipartMode.STRICT);
             // Create the parts
             // Add any parameters
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                String parameterName = arg.getName();
                if (arg.isSkippable(parameterName)){
                    continue;
                }
                FormBodyPart formPart;
                StringBody stringBody = new StringBody(arg.getValue(),
                        Charset.forName(contentEncoding == null ? "US-ASCII" : contentEncoding));
                formPart = new FormBodyPart(arg.getName(), stringBody);                   
                multiPart.addPart(formPart);
             }
 
             // Add any files
             // Cannot retrieve parts once added to the MultiPartEntity, so have to save them here.
             ViewableFileBody[] fileBodies = new ViewableFileBody[files.length];
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 fileBodies[i] = new ViewableFileBody(new File(file.getPath()), file.getMimeType());
                 multiPart.addPart(file.getParamName(),fileBodies[i]);
             }
 
             post.setEntity(multiPart);
 
             if (multiPart.isRepeatable()){
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = true;
                 }
                 multiPart.writeTo(bos);
                 for(ViewableFileBody fileBody : fileBodies){
                     fileBody.hideFileData = false;
                 }
                 bos.flush();
                 // We get the posted bytes using the encoding used to create it
                 postedBody.append(new String(bos.toByteArray(),
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
             Header contentTypeHeader = post.getFirstHeader(HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
             // If there are no arguments, we can send a file as the body of the request
             // TODO: needs a multiple file upload scenerio
             if(!hasArguments() && getSendFileAsPostBody()) {
                 // If getSendFileAsPostBody returned true, it's sure that file is not null
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         post.setHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
 
                 FileEntity fileRequestEntity = new FileEntity(new File(file.getPath()),null);
                 post.setEntity(fileRequestEntity);
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>");
             } else {
                 // In a post request which is not multipart, we only support
                 // parameters, no file upload is allowed
 
                 // If a content encoding is specified, we set it as http parameter, so that
                 // the post body will be encoded in the specified content encoding
                 String contentEncoding = getContentEncoding();
                 boolean haveContentEncoding = false;
                 if(contentEncoding != null && contentEncoding.trim().length() > 0) {
                     post.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, contentEncoding);
                     haveContentEncoding = true;
                 } else if (contentEncoding != null && contentEncoding.trim().length() == 0){
                     contentEncoding=null;
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
                             post.setHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                              // TODO - is this the correct default?
                             post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBody = new StringBuilder();
                     PropertyIterator args = getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         String value;
                         if (haveContentEncoding){
                             value = arg.getEncodedValue(contentEncoding);
                         } else {
                             value = arg.getEncodedValue();
                         }
                         postBody.append(value);
                     }
                     StringEntity requestEntity = new StringEntity(postBody.toString(), post.getFirstHeader(HEADER_CONTENT_TYPE).getValue(), contentEncoding);
                     post.setEntity(requestEntity);
                     postedBody.append(postBody.toString()); // TODO OK?
                 } else {
                     // It is a normal post request, with parameter names and values
 
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         post.setHeader(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                     // Add the parameters
                     PropertyIterator args = getArguments().iterator();
                     List <NameValuePair> nvps = new ArrayList <NameValuePair>();
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
                         if (contentEncoding != null) {
                             postedBody.append(new String(bos.toByteArray(), contentEncoding));
                         } else {
                             postedBody.append(new String(bos.toByteArray()));
                         }
                         bos.close();
                     }  else {
                         postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
                     }
                 }
 
 //                // If the request entity is repeatable, we can send it first to
 //                // our own stream, so we can return it
 //                if(post.getEntity().isRepeatable()) {
 //                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
 //                    post.getEntity().writeTo(bos);
 //                    bos.flush();
 //                    // We get the posted bytes using the encoding used to create it
 //                    if (contentEncoding != null) {
 //                        postedBody.append(new String(bos.toByteArray(), contentEncoding));
 //                    } else {
 //                        postedBody.append(new String(bos.toByteArray()));
 //                    }
 //                    bos.close();
 //                }
 //                else {
 //                    postedBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
 //                }
             }
         }
         return postedBody.toString();
     }
 
     // TODO - implementation not fully tested
     private String sendPutData(HttpPut put) throws IOException {
         // Buffer to hold the put body, except file content
         StringBuilder putBody = new StringBuilder(1000);
         boolean hasPutBody = false;
 
         // Check if the header manager had a content type header
         // This allows the user to specify his own content-type
         Header contentTypeHeader = put.getFirstHeader(HEADER_CONTENT_TYPE);
         boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.getValue() != null && contentTypeHeader.getValue().length() > 0;
 
         // Check for local contentEncoding override
         final String contentEncoding = getContentEncoding();
         boolean haveContentEncoding = (contentEncoding != null && contentEncoding.trim().length() > 0);
         
         HttpParams putParams = put.getParams();
         HTTPFileArg files[] = getHTTPFiles();
 
         // If there are no arguments, we can send a file as the body of the request
 
         if(!hasArguments() && getSendFileAsPostBody()) {
             hasPutBody = true;
 
             // If getSendFileAsPostBody returned true, it's sure that file is not null
             FileEntity fileRequestEntity = new FileEntity(new File(files[0].getPath()),null);
             put.setEntity(fileRequestEntity);
 
             // We just add placeholder text for file content
             putBody.append("<actual file content, not shown here>");
         }
         // If none of the arguments have a name specified, we
         // just send all the values as the put body
         else if(getSendParameterValuesAsPostBody()) {
             hasPutBody = true;
 
             // If a content encoding is specified, we set it as http parameter, so that
             // the post body will be encoded in the specified content encoding
             if(haveContentEncoding) {
                 putParams.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,contentEncoding);
             }
 
             // Just append all the parameter values, and use that as the post body
             StringBuilder putBodyContent = new StringBuilder();
             PropertyIterator args = getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String value = null;
                 if (haveContentEncoding){
                     value = arg.getEncodedValue(contentEncoding);
                 } else {
                     value = arg.getEncodedValue();
                 }
                 putBodyContent.append(value);
             }
             String contentTypeValue = null;
             if(hasContentTypeHeader) {
                 contentTypeValue = put.getFirstHeader(HEADER_CONTENT_TYPE).getValue();
             }
             StringEntity requestEntity = new StringEntity(putBodyContent.toString(), contentTypeValue, 
                     (String) putParams.getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET));
             put.setEntity(requestEntity);
         }
         // Check if we have any content to send for body
         if(hasPutBody) {
             // If the request entity is repeatable, we can send it first to
             // our own stream, so we can return it
             if(put.getEntity().isRepeatable()) {
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 put.getEntity().writeTo(bos);
                 bos.flush();
                 // We get the posted bytes using the charset that was used to create them
                 putBody.append(new String(bos.toByteArray(),
                         (String) putParams.getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET)));
                 bos.close();
             }
             else {
                 putBody.append("<RequestEntity was not repeatable, cannot view what was sent>");
             }
             if(!hasContentTypeHeader) {
                 // Allow the mimetype of the file to control the content type
                 // This is not obvious in GUI if you are not uploading any files,
                 // but just sending the content of nameless parameters
                 // TODO: needs a multiple file upload scenerio
                 HTTPFileArg file = files.length > 0? files[0] : null;
                 if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                     put.setHeader(HEADER_CONTENT_TYPE, file.getMimeType());
                 }
             }
             return putBody.toString();
         }
         return null;
     }
 
     private void saveConnectionCookies(HttpResponse method, URL u, CookieManager cookieManager) {
         if (cookieManager != null) {
             Header[] hdrs = method.getHeaders(HEADER_SET_COOKIE);
             for (Header hdr : hdrs) {
                 cookieManager.addCookieFromHeader(hdr.getValue(),u);
             }
         }
     }
 
     @Override
     public void threadFinished() {
         log.debug("Thread Finished");
         // Does not need to be synchronised, as all access is from same thread
         Map<HttpClientKey, HttpClient> map = HTTPCLIENTS.get();
         if ( map != null ) {
             for ( HttpClient cl : map.values() ) {
                 cl.getConnectionManager().shutdown();
             }
             map.clear();
         }
     }
 
     public boolean interrupt() {
         HttpUriRequest request = currentRequest;
         if (request != null) {
             currentRequest = null;
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
index 8c71c4318..2caaf8cc8 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,1415 +1,1428 @@
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
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.net.MalformedURLException;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.commons.io.IOUtils;
 import org.apache.jmeter.config.Argument;
 import org.apache.jmeter.config.Arguments;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.control.AuthManager;
 import org.apache.jmeter.protocol.http.control.CacheManager;
 import org.apache.jmeter.protocol.http.control.CookieManager;
 import org.apache.jmeter.protocol.http.control.HeaderManager;
 import org.apache.jmeter.protocol.http.parser.HTMLParseException;
 import org.apache.jmeter.protocol.http.parser.HTMLParser;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
 import org.apache.jmeter.protocol.http.util.EncoderCache;
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.protocol.http.util.HTTPFileArgs;
 import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestListener;
 import org.apache.jmeter.testelement.ThreadListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.IntegerProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.testelement.property.StringProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
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
     implements TestListener, ThreadListener, HTTPConstantsInterface {
 
     private static final long serialVersionUID = 240L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     //+ JMX names - do not change
     public static final String ARGUMENTS = "HTTPsampler.Arguments"; // $NON-NLS-1$
 
     public static final String AUTH_MANAGER = "HTTPSampler.auth_manager"; // $NON-NLS-1$
 
     public static final String COOKIE_MANAGER = "HTTPSampler.cookie_manager"; // $NON-NLS-1$
 
     public static final String CACHE_MANAGER = "HTTPSampler.cache_manager"; // $NON-NLS-1$
 
     public static final String HEADER_MANAGER = "HTTPSampler.header_manager"; // $NON-NLS-1$
 
     public static final String DOMAIN = "HTTPSampler.domain"; // $NON-NLS-1$
 
     public static final String PORT = "HTTPSampler.port"; // $NON-NLS-1$
 
     public static final String PROXYHOST = "HTTPSampler.proxyHost"; // $NON-NLS-1$
 
     public static final String PROXYPORT = "HTTPSampler.proxyPort"; // $NON-NLS-1$
 
     public static final String PROXYUSER = "HTTPSampler.proxyUser"; // $NON-NLS-1$
 
     public static final String PROXYPASS = "HTTPSampler.proxyPass"; // $NON-NLS-1$
 
     public static final String CONNECT_TIMEOUT = "HTTPSampler.connect_timeout"; // $NON-NLS-1$
 
     public static final String RESPONSE_TIMEOUT = "HTTPSampler.response_timeout"; // $NON-NLS-1$
 
     public static final String METHOD = "HTTPSampler.method"; // $NON-NLS-1$
 
     public static final String CONTENT_ENCODING = "HTTPSampler.contentEncoding"; // $NON-NLS-1$
 
     public static final String IMPLEMENTATION = "HTTPSampler.implementation"; // $NON-NLS-1$
 
     public static final String PATH = "HTTPSampler.path"; // $NON-NLS-1$
 
     public static final String FOLLOW_REDIRECTS = "HTTPSampler.follow_redirects"; // $NON-NLS-1$
 
     public static final String AUTO_REDIRECTS = "HTTPSampler.auto_redirects"; // $NON-NLS-1$
 
     public static final String PROTOCOL = "HTTPSampler.protocol"; // $NON-NLS-1$
 
     private static final String PROTOCOL_FILE = "file"; // $NON-NLS-1$
 
     private static final String DEFAULT_PROTOCOL = PROTOCOL_HTTP;
 
     public static final String URL = "HTTPSampler.URL"; // $NON-NLS-1$
 
     /**
      * IP source to use - does not apply to Java HTTP implementation currently
      */
     public static final String IP_SOURCE = "HTTPSampler.ipSource"; // $NON-NLS-1$
+
+    public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
+
+    public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
+
+    public static final String BROWSER_COMPATIBLE_MULTIPART  = "HTTPSampler.BROWSER_COMPATIBLE_MULTIPART"; // $NON-NLS-1$
+
     //- JMX names
+
+    public static final boolean BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT = false; // The default setting to be used (i.e. historic)
     
     
     public static final String DEFAULT_METHOD = GET; // $NON-NLS-1$
     // Supported methods:
     private static final String [] METHODS = {
         DEFAULT_METHOD, // i.e. GET
         POST,
         HEAD,
         PUT,
         OPTIONS,
         TRACE,
         DELETE,
         };
 
     private static final List<String> METHODLIST = Collections.unmodifiableList(Arrays.asList(METHODS));
 
-
-    public static final String USE_KEEPALIVE = "HTTPSampler.use_keepalive"; // $NON-NLS-1$
-
-    public static final String DO_MULTIPART_POST = "HTTPSampler.DO_MULTIPART_POST"; // $NON-NLS-1$
-
     // @see mergeFileProperties
     // Must be private, as the file list needs special handling
     private final static String FILE_ARGS = "HTTPsampler.Files"; // $NON-NLS-1$
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
     private static final String MD5 = "HTTPSampler.md5"; // $NON-NLS-1$
 
     /** A number to indicate that the port has not been set. */
     public static final int UNSPECIFIED_PORT = 0;
     public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$
     // TODO - change to use URL version? Will this affect test plans?
 
     /** If the port is not present in a URL, getPort() returns -1 */
     public static final int URL_UNSPECIFIED_PORT = -1;
     public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$
 
     protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
 
     protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> parsersForType = new HashMap<String, String>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
     static{
         String []parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (int i=0;i<parsers.length;i++){
             final String parser = parsers[i];
             String classname=JMeterUtils.getProperty(parser+".className");//$NON-NLS-1$
             if (classname == null){
                 log.info("Cannot find .className property for "+parser+", using default");
                 classname="";
             }
             String typelist=JMeterUtils.getProperty(parser+".types");//$NON-NLS-1$
             if (typelist != null){
                 String []types=JOrphanUtils.split(typelist, " " , true);
                 for (int j=0;j<types.length;j++){
                     final String type = types[j];
                     log.info("Parser for "+type+" is "+classname);
                     parsersForType.put(type,classname);
                 }
             } else {
                 log.warn("Cannot find .types property for "+parser);
             }
         }
         if (parsers.length==0){ // revert to previous behaviour
             parsersForType.put("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
             log.info("No response parsers defined: text/html only will be scanned for embedded resources");
         }
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
     ////////////////////// Variables //////////////////////
 
     private boolean dynamicPath = false;// Set false if spaces are already encoded
 
 
+
     ////////////////////// Code ///////////////////////////
 
     public HTTPSamplerBase() {
         setArguments(new Arguments());
     }
 
     /**
      * Determine if the file should be sent as the entire Post body,
      * i.e. without any additional wrapping
      *
      * @return true if specified file is to be sent as the body,
      * i.e. FileField is blank
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
      * as the post body
      *
      * @return true if none of the parameters have a name specified
      */
     public boolean getSendParameterValuesAsPostBody() {
         boolean noArgumentsHasName = true;
         PropertyIterator args = getArguments().iterator();
         while (args.hasNext()) {
             HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
             if(arg.getName() != null && arg.getName().length() > 0) {
                 noArgumentsHasName = false;
                 break;
             }
         }
         return noArgumentsHasName;
     }
 
     /**
      * Determine if we should use multipart/form-data or
      * application/x-www-form-urlencoded for the post
      *
      * @return true if multipart/form-data should be used and method is POST
      */
     public boolean getUseMultipartForPost(){
         // We use multipart if we have been told so, or files are present
         // and the files should not be send as the post body
         HTTPFileArg[] files = getHTTPFiles();
         if(POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
             return true;
         }
         return false;
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
         if (protocol == null || protocol.length() == 0 ) {
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
      * Sets the Path attribute of the UrlConfig object Also calls parseArguments
      * to extract and store any query arguments
      *
      * @param path
      *            The new Path value
      * @param contentEncoding
      *            The encoding used for the querystring parameter values
      */
     public void setPath(String path, String contentEncoding) {
         if (GET.equals(getMethod()) || DELETE.equals(getMethod())) {
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
         if (dynamicPath) {
             return encodeSpaces(p);
         }
         return p;
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
 
     public void setContentEncoding(String value) {
         setProperty(CONTENT_ENCODING, value);
     }
 
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
 
+    public void setDoBrowserCompatibleMultipart(boolean value) {
+        setProperty(BROWSER_COMPATIBLE_MULTIPART, value, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
+    }
+
+    public boolean getDoBrowserCompatibleMultipart() {
+        return getPropertyAsBoolean(BROWSER_COMPATIBLE_MULTIPART, BROWSER_COMPATIBLE_MULTIPART_MODE_DEFAULT);
+    }
+
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
      */
     public void addEncodedArgument(String name, String value) {
         this.addEncodedArgument(name, value, ARG_VAL_SEP);
     }
 
     public void addEncodedArgument(String name, String value, String metaData, String contentEncoding) {
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         if(contentEncoding != null) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(contentEncoding != null) {
             try {
                 valueEncoded = arg.getEncodedValue(contentEncoding);
             }
             catch (UnsupportedEncodingException e) {
                 log.warn("Unable to get encoded value using encoding " + contentEncoding);
                 valueEncoded = arg.getEncodedValue();
             }
         }
         else {
             valueEncoded = arg.getEncodedValue();
         }
         // If there is no difference, we mark it as not needing encoding
         if (arg.getName().equals(arg.getEncodedName()) && arg.getValue().equals(valueEncoded)) {
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
     public void clearTestElementChildren(){
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
     public static int getDefaultPort(String protocol,int port){
         if (port==URL_UNSPECIFIED_PORT){
             return
                 protocol.equalsIgnoreCase(PROTOCOL_HTTP)  ? DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(PROTOCOL_HTTPS) ? DEFAULT_HTTPS_PORT :
                     port;
         }
         return port;
     }
 
     /**
      * Get the port number from the port string, allowing for trailing blanks.
      *
      * @return port number or UNSPECIFIED_PORT (== 0)
      */
     public int getPortIfSpecified() {
         String port_s = getPropertyAsString(PORT, UNSPECIFIED_PORT_AS_STRING);
         try {
             return Integer.parseInt(port_s.trim());
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
         if (port == UNSPECIFIED_PORT ||
                 (PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == DEFAULT_HTTP_PORT) ||
                 (PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == DEFAULT_HTTPS_PORT)) {
             return true;
         }
         return false;
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
             if (PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return DEFAULT_HTTPS_PORT;
             }
             if (!PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
                 // TODO - should this return something else?
             }
             return DEFAULT_HTTP_PORT;
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
 
     public void setArguments(Arguments value) {
         setProperty(new TestElementProperty(ARGUMENTS, value));
     }
 
     public Arguments getArguments() {
         return (Arguments) getProperty(ARGUMENTS).getObjectValue();
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
             log.warn("Existing HeaderManager '" + mgr.getName() + "' merged with '" + value.getName() + "'");
             value = mgr.merge(value, true);
             if (log.isDebugEnabled()) {
                 log.debug("HeaderManager merged: " + value.getName());
                 for (int i=0; i < value.getHeaders().size(); i++) {
                     log.debug("    " + value.getHeader(i).getName() + "=" + value.getHeader(i).getValue());
                 }
             }
         }
         setProperty(new TestElementProperty(HEADER_MANAGER, value));
     }
 
     public HeaderManager getHeaderManager() {
         return (HeaderManager) getProperty(HEADER_MANAGER).getObjectValue();
     }
 
     public void setCookieManager(CookieManager value) {
         CookieManager mgr = getCookieManager();
         if (mgr != null) {
             log.warn("Existing CookieManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(COOKIE_MANAGER, value));
     }
 
     public CookieManager getCookieManager() {
         return (CookieManager) getProperty(COOKIE_MANAGER).getObjectValue();
     }
 
     public void setCacheManager(CacheManager value) {
         CacheManager mgr = getCacheManager();
         if (mgr != null) {
             log.warn("Existing CacheManager " + mgr.getName() + " superseded by " + value.getName());
         }
         setProperty(new TestElementProperty(CACHE_MANAGER, value));
     }
 
     public CacheManager getCacheManager() {
         return (CacheManager) getProperty(CACHE_MANAGER).getObjectValue();
     }
 
     public boolean isImageParser() {
         return getPropertyAsBoolean(IMAGE_PARSER);
     }
 
     public void setImageParser(boolean parseImages) {
         setProperty(new BooleanProperty(IMAGE_PARSER, parseImages));
     }
 
     /**
      * Get the regular expression URLs must match.
      *
      * @return regular expression (or empty) string
      */
     public String getEmbeddedUrlRE() {
         return getPropertyAsString(EMBEDDED_URL_RE,"");
     }
 
     public void setEmbeddedUrlRE(String regex) {
         setProperty(new StringProperty(EMBEDDED_URL_RE, regex));
     }
 
     /**
      * Obtain a result that will help inform the user that an error has occured
      * during sampling, and how long it took to detect the error.
      *
      * @param e
      *            Exception representing the error.
      * @param res
      *            SampleResult
      * @return a sampling result useful to inform the user about the exception.
      */
     protected HTTPSampleResult errorResult(Throwable e, HTTPSampleResult res) {
         res.setSampleLabel("Error: " + res.getSampleLabel());
         res.setDataType(SampleResult.TEXT);
         ByteArrayOutputStream text = new ByteArrayOutputStream(200);
         e.printStackTrace(new PrintStream(text));
         res.setResponseData(text.toByteArray());
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
         res.setSuccessful(false);
         res.setMonitor(this.isMonitor());
         return res;
     }
 
     private static final String HTTP_PREFIX = PROTOCOL_HTTP+"://"; // $NON-NLS-1$
     private static final String HTTPS_PREFIX = PROTOCOL_HTTPS+"://"; // $NON-NLS-1$
 
     /**
      * Get the URL, built from its component parts.
      *
      * <p>
      * As a special case, if the path starts with "http[s]://",
      * then the path is assumed to be the entire URL.
      * </p>
      *
      * @return The URL to be requested by this sampler.
      * @throws MalformedURLException
      */
     public URL getUrl() throws MalformedURLException {
         StringBuilder pathAndQuery = new StringBuilder(100);
         String path = this.getPath();
         // Hack to allow entire URL to be provided in host field
         if (path.startsWith(HTTP_PREFIX)
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         if (!path.startsWith("/")){ // $NON-NLS-1$
             pathAndQuery.append("/"); // $NON-NLS-1$
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(GET.equals(getMethod()) || DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.indexOf(QRY_PFX) > -1) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
             return new URL(getProtocol(), getDomain(), pathAndQuery.toString());
         }
         return new URL(getProtocol(), getDomain(), getPort(), pathAndQuery.toString());
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
          // Check if the sampler has a specified content encoding
          if(contentEncoding == null || contentEncoding.trim().length() == 0) {
              // We use the encoding which should be used according to the HTTP spec, which is UTF-8
              contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
          }
         StringBuilder buf = new StringBuilder();
         PropertyIterator iter = getArguments().iterator();
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
                 log.warn("Unexpected argument type: "+objectValue.getClass().getName());
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
             }
             catch(UnsupportedEncodingException e) {
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
      *            the query string
      * @param contentEncoding -
      *            the content encoding of the query string. The query string might
      *            actually be the post body of a http post request.
      */
     public void parseArguments(String queryString, String contentEncoding) {
         String[] args = JOrphanUtils.split(queryString, QRY_SEP);
         for (int i = 0; i < args.length; i++) {
             // need to handle four cases:
             // - string contains name=value
             // - string contains name=
             // - string contains name
             // - empty string
 
             String metaData; // records the existance of an equal sign
             String name;
             String value;
             int length = args[i].length();
             int endOfNameIndex = args[i].indexOf(ARG_VAL_SEP);
             if (endOfNameIndex != -1) {// is there a separator?
                 // case of name=value, name=
                 metaData = ARG_VAL_SEP;
                 name = args[i].substring(0, endOfNameIndex);
                 value = args[i].substring(endOfNameIndex + 1, length);
             } else {
                 metaData = "";
                 name=args[i];
                 value="";
             }
             if (name.length() > 0) {
                 // If we know the encoding, we can decode the argument value,
                 // to make it easier to read for the user
                 if(contentEncoding != null) {
                     addEncodedArgument(name, value, metaData, contentEncoding);
                 }
                 else {
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
             if(POST.equals(getMethod()) || PUT.equals(getMethod())) {
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
             if (PROTOCOL_FILE.equalsIgnoreCase(getProtocol())){
                 res = fileSample(new URI(PROTOCOL_FILE,getPath(),null));
             } else {
                 res = sample(getUrl(), getMethod(), false, 0);
             }
             res.setSampleLabel(getName());
             return res;
         } catch (MalformedURLException e) {
             return errorResult(e, new HTTPSampleResult());
         } catch (IOException e) {
             return errorResult(e, new HTTPSampleResult());
         } catch (URISyntaxException e) {
             return errorResult(e, new HTTPSampleResult());
         }
     }
 
     private HTTPSampleResult fileSample(URI uri) throws IOException {
 
         //String urlStr = uri.toString();
 
 
         HTTPSampleResult res = new HTTPSampleResult();
         res.setMonitor(isMonitor());
         res.setHTTPMethod(GET); // Dummy
         res.setURL(uri.toURL());
         res.setSampleLabel(uri.toString());
         FileInputStream fis = null;
         res.sampleStart();
         try {
             byte[] responseData;
             StringBuilder ctb=new StringBuilder("text/html"); // $NON-NLS-1$
             fis = new FileInputStream(getPath());
             String contentEncoding = getContentEncoding();
             if (contentEncoding.length() > 0) {
                 ctb.append("; charset="); // $NON-NLS-1$
                 ctb.append(contentEncoding);
             }
             responseData = IOUtils.toByteArray(fis);
             res.sampleEnd();
             res.setResponseData(responseData);
             res.setResponseCodeOK();
             res.setResponseMessageOK();
             res.setSuccessful(true);
             String ct = ctb.toString();
             res.setContentType(ct);
             res.setEncodingAndType(ct);
         } finally {
             IOUtils.closeQuietly(fis);
         }
 
         //res.setResponseHeaders("");
 
         return res;
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
      * @return results of the sampling
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
             if (responseData.length > 0){  // Bug 39205
                 String parserName = getParserClass(res);
                 if(parserName != null)
                 {
                     final HTMLParser parser =
                         parserName.length() > 0 ? // we have a name
                         HTMLParser.getParser(parserName)
                         :
                         HTMLParser.getParser(); // we don't; use the default parser
                     urls = parser.getEmbeddedResourceURLs(responseData, res.getURL());
                 }
             }
         } catch (HTMLParseException e) {
             // Don't break the world just because this failed:
             res.addSubResult(errorResult(e, res));
             res.setSuccessful(false);
         }
 
         // Iterate through the URLs and download each image:
         if (urls != null && urls.hasNext()) {
             if (container == null) {
                 container = new HTTPSampleResult(res);
                 container.addRawSubResult(res);
             }
             res = container;
 
             // Get the URL matcher
             String re=getEmbeddedUrlRE();
             Perl5Matcher localMatcher = null;
             Pattern pattern = null;
             if (re.length()>0){
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: "+e.getMessage());
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
                         String urlStrEnc=encodeSpaces(urlstr);
                         if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
                             try {
                                 url = new URL(urlStrEnc);
                             } catch (MalformedURLException e) {
                                 res.addSubResult(errorResult(new Exception(urlStrEnc + " is not a correct URI"), res));
                                 res.setSuccessful(false);
                                 continue;
                             }
                         }
                         // I don't think localMatcher can be null here, but check just in case
                         if (pattern != null && localMatcher != null && !localMatcher.matches(urlStrEnc, pattern)) {
                             continue; // we have a pattern and the URL does not match, so skip it
                         }
                         HTTPSampleResult binRes = sample(url, GET, false, frameDepth + 1);
                         res.addSubResult(binRes);
                         res.setSuccessful(res.isSuccessful() && binRes.isSuccessful());
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), res));
                     res.setSuccessful(false);
                     continue;
                 }
             }
         }
         return res;
     }
 
     /*
      * @param res HTTPSampleResult to check
      * @return parser class name (may be "") or null if entry does not exist
      */
     private String getParserClass(HTTPSampleResult res) {
         final String ct = res.getMediaType();
         return parsersForType.get(ct);
     }
 
     // TODO: make static?
     protected String encodeSpaces(String path) {
         return JOrphanUtils.replaceAllChars(path, ' ', "%20"); // $NON-NLS-1$
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded() {
         dynamicPath = false;
     }
 
     /**
      * {@inheritDoc}
      */
     public void testEnded(String host) {
         testEnded();
     }
 
     /**
      * {@inheritDoc}
      */
     public void testIterationStart(LoopIterationEvent event) {
     }
 
     /**
      * {@inheritDoc}
      */
     public void testStarted() {
         JMeterProperty pathP = getProperty(PATH);
         log.debug("path property is a " + pathP.getClass().getName());
         log.debug("path beginning value = " + pathP.getStringValue());
         if (pathP instanceof StringProperty && pathP.getStringValue().length() > 0) {
             log.debug("Encoding spaces in path");
             pathP.setObjectValue(encodeSpaces(pathP.getStringValue()));
             dynamicPath = false;
         } else {
             log.debug("setting dynamic path to true");
             dynamicPath = true;
         }
         log.debug("path ending value = " + pathP.getStringValue());
     }
 
     /**
      * {@inheritDoc}
      */
     public void testStarted(String host) {
         testStarted();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object clone() {
         HTTPSamplerBase base = (HTTPSamplerBase) super.clone();
         base.dynamicPath = dynamicPath;
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
             // Browsers seem to tolerate Location headers with spaces,
             // replacing them automatically with %20. We want to emulate
             // this behaviour.
             String location = lastRes.getRedirectLocation(); 
             if (REMOVESLASHDOTDOT) {
                 location = ConversionUtils.removeSlashDotDot(location);
             }
             location = encodeSpaces(location);
             try {
                 lastRes = sample(ConversionUtils.makeRelativeURL(lastRes.getURL(), location), GET, true, frameDepth);
             } catch (MalformedURLException e) {
                 lastRes = errorResult(e, lastRes);
                 // The redirect URL we got was not a valid URL
                 invalidRedirectUrl = true;
             }
             if (lastRes.getSubResults() != null && lastRes.getSubResults().length > 0) {
                 SampleResult[] subs = lastRes.getSubResults();
                 for (int i = 0; i < subs.length; i++) {
                     totalRes.addSubResult(subs[i]);
                 }
             } else {
                 // Only add sample if it is a sample of valid url redirect, i.e. that
                 // we have actually sampled the URL
                 if(!invalidRedirectUrl) {
                     totalRes.addSubResult(lastRes);
                 }
             }
 
             if (!lastRes.isRedirect()) {
                 break;
             }
         }
         if (redirect >= MAX_REDIRECTS) {
             lastRes = errorResult(new IOException("Exceeeded maximum number of redirects: " + MAX_REDIRECTS), lastRes);
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
      * Follow redirects and download page resources if appropriate. this works,
      * but the container stuff here is what's doing it. followRedirects() is
      * actually doing the work to make sure we have only one container to make
      * this work more naturally, I think this method - sample() - needs to take
      * an HTTPSamplerResult container parameter instead of a
      * boolean:areFollowingRedirect.
      *
      * @param areFollowingRedirect
      * @param frameDepth
      * @param res
      * @return the sample result
      */
     protected HTTPSampleResult resultProcessing(boolean areFollowingRedirect, int frameDepth, HTTPSampleResult res) {
         boolean wasRedirected = false;
         if (!areFollowingRedirect) {
             if (res.isRedirect()) {
                 log.debug("Location set to - " + res.getRedirectLocation());
 
                 if (getFollowRedirects()) {
                     res = followRedirects(res, frameDepth);
                     areFollowingRedirect = true;
                     wasRedirected = true;
                 }
             }
         }
         if (isImageParser() && (HTTPSampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
             if (frameDepth > MAX_FRAME_DEPTH) {
                 res.addSubResult(errorResult(new Exception("Maximum frame/iframe nesting depth exceeded."), res));
             } else {
                 // Only download page resources if we were not redirected.
                 // If we were redirected, the page resources have already been
                 // downloaded for the sample made for the redirected url
                 if(!wasRedirected) {
                     HTTPSampleResult container = (HTTPSampleResult) (areFollowingRedirect ? res.getParent() : res);
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
      * @return whether in range 200-399 or not
      */
     protected boolean isSuccessCode(int code){
         return (code >= 200 && code <= 399);
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
         if (value.getHTTPFileArgCount() > 0){
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
 
     public int getHTTPFileCount(){
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
             for(int i=0; i < files.length; i++){
                 HTTPFileArg file = files[i];
                 if (file.isNotEmpty()){
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray(){
         return METHODLIST.toArray(new String[0]);
     }
 
     public static boolean isSecure(String protocol){
         return PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java
index 888a4ce9a..0e53d78d1 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/PostWriter.java
@@ -1,447 +1,449 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.UnsupportedEncodingException;
 import java.net.URLConnection;
 
 import org.apache.jmeter.protocol.http.util.HTTPArgument;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.protocol.http.util.HTTPFileArg;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 
 /**
  * Class for setting the necessary headers for a POST request, and sending the
  * body of the POST.
  */
 public class PostWriter {
 
     private static final String DASH_DASH = "--";  // $NON-NLS-1$
     private static final byte[] DASH_DASH_BYTES = {'-', '-'};
 
     /** The bounday string between multiparts */
     protected final static String BOUNDARY = "---------------------------7d159c1302d0y0"; // $NON-NLS-1$
 
     private final static byte[] CRLF = { 0x0d, 0x0A };
 
     public static final String ENCODING = "ISO-8859-1"; // $NON-NLS-1$
 
     /** The form data that is going to be sent as url encoded */
     protected byte[] formDataUrlEncoded;
     /** The form data that is going to be sent in post body */
     protected byte[] formDataPostBody;
     /** The boundary string for multipart */
     private final String boundary;
 
     /**
      * Constructor for PostWriter.
      * Uses the PostWriter.BOUNDARY as the boundary string
      *
      */
     public PostWriter() {
         this(BOUNDARY);
     }
 
     /**
      * Constructor for PostWriter
      *
      * @param boundary the boundary string to use as marker between multipart parts
      */
     public PostWriter(String boundary) {
         this.boundary = boundary;
     }
 
     /**
      * Send POST data from Entry to the open connection.
      *
      * @return the post body sent. Actual file content is not returned, it
      * is just shown as a placeholder text "actual file content"
      */
     public String sendPostData(URLConnection connection, HTTPSamplerBase sampler) throws IOException {
         // Buffer to hold the post body, except file content
         StringBuilder postedBody = new StringBuilder(1000);
 
         HTTPFileArg files[] = sampler.getHTTPFiles();
 
         String contentEncoding = sampler.getContentEncoding();
         if(contentEncoding == null || contentEncoding.length() == 0) {
             contentEncoding = ENCODING;
         }
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(sampler.getUseMultipartForPost()) {
             OutputStream out = connection.getOutputStream();
 
             // Write the form data post body, which we have constructed
             // in the setHeaders. This contains the multipart start divider
             // and any form data, i.e. arguments
             out.write(formDataPostBody);
             // Retrieve the formatted data using the same encoding used to create it
             postedBody.append(new String(formDataPostBody, contentEncoding));
 
             // Add any files
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 // First write the start multipart file
                 byte[] header = file.getHeader().getBytes();  // TODO - charset?
                 out.write(header);
                 // Retrieve the formatted data using the same encoding used to create it
                 postedBody.append(new String(header)); // TODO - charset?
                 // Write the actual file content
                 writeFileToStream(file.getPath(), out);
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>"); // $NON-NLS-1$
                 // Write the end of multipart file
                 byte[] fileMultipartEndDivider = getFileMultipartEndDivider();
                 out.write(fileMultipartEndDivider);
                 // Retrieve the formatted data using the same encoding used to create it
                 postedBody.append(new String(fileMultipartEndDivider, ENCODING));
                 if(i + 1 < files.length) {
                     out.write(CRLF);
                     postedBody.append(new String(CRLF)); // TODO - charset?
                 }
             }
             // Write end of multipart
             byte[] multipartEndDivider = getMultipartEndDivider();
             out.write(multipartEndDivider);
             postedBody.append(new String(multipartEndDivider, ENCODING));
 
             out.flush();
             out.close();
         }
         else {
             // If there are no arguments, we can send a file as the body of the request
             if(sampler.getArguments() != null && !sampler.hasArguments() && sampler.getSendFileAsPostBody()) {
                 OutputStream out = connection.getOutputStream();
                 // we're sure that there is at least one file because of
                 // getSendFileAsPostBody method's return value.
                 HTTPFileArg file = files[0];
                 writeFileToStream(file.getPath(), out);
                 out.flush();
                 out.close();
 
                 // We just add placeholder text for file content
                 postedBody.append("<actual file content, not shown here>"); // $NON-NLS-1$
             }
             else if (formDataUrlEncoded != null){ // may be null for PUT
                 // In an application/x-www-form-urlencoded request, we only support
                 // parameters, no file upload is allowed
                 OutputStream out = connection.getOutputStream();
                 out.write(formDataUrlEncoded);
                 out.flush();
                 out.close();
 
                 postedBody.append(new String(formDataUrlEncoded, contentEncoding));
             }
         }
         return postedBody.toString();
     }
 
     public void setHeaders(URLConnection connection, HTTPSamplerBase sampler) throws IOException {
         // Get the encoding to use for the request
         String contentEncoding = sampler.getContentEncoding();
         if(contentEncoding == null || contentEncoding.length() == 0) {
             contentEncoding = ENCODING;
         }
         long contentLength = 0L;
         HTTPFileArg files[] = sampler.getHTTPFiles();
 
         // Check if we should do a multipart/form-data or an
         // application/x-www-form-urlencoded post request
         if(sampler.getUseMultipartForPost()) {
             // Set the content type
             connection.setRequestProperty(
                     HTTPConstants.HEADER_CONTENT_TYPE,
                     HTTPConstants.MULTIPART_FORM_DATA + "; boundary=" + getBoundary()); // $NON-NLS-1$
 
             // Write the form section
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
             // First the multipart start divider
             bos.write(getMultipartDivider());
             // Add any parameters
             PropertyIterator args = sampler.getArguments().iterator();
             while (args.hasNext()) {
                 HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                 String parameterName = arg.getName();
                 if (arg.isSkippable(parameterName)){
                     continue;
                 }
                 // End the previous multipart
                 bos.write(CRLF);
                 // Write multipart for parameter
-                writeFormMultipart(bos, parameterName, arg.getValue(), contentEncoding);
+                writeFormMultipart(bos, parameterName, arg.getValue(), contentEncoding, sampler.getDoBrowserCompatibleMultipart());
             }
             // If there are any files, we need to end the previous multipart
             if(files.length > 0) {
                 // End the previous multipart
                 bos.write(CRLF);
             }
             bos.flush();
             // Keep the content, will be sent later
             formDataPostBody = bos.toByteArray();
             bos.close();
             contentLength = formDataPostBody.length;
 
             // Now we just construct any multipart for the files
             // We only construct the file multipart start, we do not write
             // the actual file content
             for (int i=0; i < files.length; i++) {
                 HTTPFileArg file = files[i];
                 // Write multipart for file
                 bos = new ByteArrayOutputStream();
                 writeStartFileMultipart(bos, file.getPath(), file.getParamName(), file.getMimeType());
                 bos.flush();
                 String header = bos.toString(contentEncoding);// TODO is this correct?
                 // If this is not the first file we can't write its header now
                 // for simplicity we always save it, even if there is only one file
                 file.setHeader(header);
                 bos.close();
                 contentLength += header.length();
                 // Add also the length of the file content
                 File uploadFile = new File(file.getPath());
                 contentLength += uploadFile.length();
                 // And the end of the file multipart
                 contentLength += getFileMultipartEndDivider().length;
                 if(i+1 < files.length) {
                     contentLength += CRLF.length;
                 }
             }
 
             // Add the end of multipart
             contentLength += getMultipartEndDivider().length;
 
             // Set the content length
             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(contentLength));
 
             // Make the connection ready for sending post data
             connection.setDoOutput(true);
             connection.setDoInput(true);
         }
         else {
             // Check if the header manager had a content type header
             // This allows the user to specify his own content-type for a POST request
             String contentTypeHeader = connection.getRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE);
             boolean hasContentTypeHeader = contentTypeHeader != null && contentTypeHeader.length() > 0;
 
             // If there are no arguments, we can send a file as the body of the request
             if(sampler.getArguments() != null && sampler.getArguments().getArgumentCount() == 0 && sampler.getSendFileAsPostBody()) {
                 // we're sure that there is one file because of
                 // getSendFileAsPostBody method's return value.
                 HTTPFileArg file = files[0];
                 if(!hasContentTypeHeader) {
                     // Allow the mimetype of the file to control the content type
                     if(file.getMimeType() != null && file.getMimeType().length() > 0) {
                         connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                     }
                     else {
                         connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
                 }
                 // Create the content length we are going to write
                 File inputFile = new File(file.getPath());
                 contentLength = inputFile.length();
             }
             else {
                 // We create the post body content now, so we know the size
                 ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
                 // If none of the arguments have a name specified, we
                 // just send all the values as the post body
                 String postBody = null;
                 if(!sampler.getSendParameterValuesAsPostBody()) {
                     // Set the content type
                     if(!hasContentTypeHeader) {
                         connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                     }
 
                     // It is a normal post request, with parameter names and values
                     postBody = sampler.getQueryString(contentEncoding);
                 }
                 else {
                     // Allow the mimetype of the file to control the content type
                     // This is not obvious in GUI if you are not uploading any files,
                     // but just sending the content of nameless parameters
                     // TODO: needs a multiple file upload scenerio
                     if(!hasContentTypeHeader) {
                         HTTPFileArg file = files.length > 0? files[0] : null;
                         if(file != null && file.getMimeType() != null && file.getMimeType().length() > 0) {
                             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, file.getMimeType());
                         }
                         else {
                             // TODO: is this the correct default?
                             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED);
                         }
                     }
 
                     // Just append all the parameter values, and use that as the post body
                     StringBuilder postBodyBuffer = new StringBuilder();
                     PropertyIterator args = sampler.getArguments().iterator();
                     while (args.hasNext()) {
                         HTTPArgument arg = (HTTPArgument) args.next().getObjectValue();
                         postBodyBuffer.append(arg.getEncodedValue(contentEncoding));
                     }
                     postBody = postBodyBuffer.toString();
                 }
 
                 bos.write(postBody.getBytes(contentEncoding));
                 bos.flush();
                 bos.close();
 
                 // Keep the content, will be sent later
                 formDataUrlEncoded = bos.toByteArray();
                 contentLength = bos.toByteArray().length;
             }
 
             // Set the content length
             connection.setRequestProperty(HTTPConstants.HEADER_CONTENT_LENGTH, Long.toString(contentLength));
 
             // Make the connection ready for sending post data
             connection.setDoOutput(true);
         }
     }
 
     /**
      * Get the boundary string, used to separate multiparts
      *
      * @return the boundary string
      */
     protected String getBoundary() {
         return boundary;
     }
 
     /**
      * Get the bytes used to separate multiparts
      * Encoded using ENCODING
      *
      * @return the bytes used to separate multiparts
      * @throws IOException
      */
     private byte[] getMultipartDivider() throws IOException {
         return (DASH_DASH + getBoundary()).getBytes(ENCODING);
     }
 
     /**
      * Get the bytes used to end a file multipart
      * Encoded using ENCODING
      *
      * @return the bytes used to end a file multipart
      * @throws IOException
      */
     private byte[] getFileMultipartEndDivider() throws IOException{
         byte[] ending = getMultipartDivider();
         byte[] completeEnding = new byte[ending.length + CRLF.length];
         System.arraycopy(CRLF, 0, completeEnding, 0, CRLF.length);
         System.arraycopy(ending, 0, completeEnding, CRLF.length, ending.length);
         return completeEnding;
     }
 
     /**
      * Get the bytes used to end the multipart request
      *
      * @return the bytes used to end the multipart request
      */
     private byte[] getMultipartEndDivider(){
         byte[] ending = DASH_DASH_BYTES;
         byte[] completeEnding = new byte[ending.length + CRLF.length];
         System.arraycopy(ending, 0, completeEnding, 0, ending.length);
         System.arraycopy(CRLF, 0, completeEnding, ending.length, CRLF.length);
         return completeEnding;
     }
 
     /**
      * Write the start of a file multipart, up to the point where the
      * actual file content should be written
      */
     private void writeStartFileMultipart(OutputStream out, String filename,
             String nameField, String mimetype)
             throws IOException {
         write(out, "Content-Disposition: form-data; name=\""); // $NON-NLS-1$
         write(out, nameField);
         write(out, "\"; filename=\"");// $NON-NLS-1$
         write(out, (new File(filename).getName()));
         writeln(out, "\""); // $NON-NLS-1$
         writeln(out, "Content-Type: " + mimetype); // $NON-NLS-1$
         writeln(out, "Content-Transfer-Encoding: binary"); // $NON-NLS-1$
         out.write(CRLF);
     }
 
     /**
      * Write the content of a file to the output stream
      *
      * @param filename the filename of the file to write to the stream
      * @param out the stream to write to
      * @throws IOException
      */
     private void writeFileToStream(String filename, OutputStream out) throws IOException {
         byte[] buf = new byte[1024];
         // 1k - the previous 100k made no sense (there's tons of buffers
         // elsewhere in the chain) and it caused OOM when many concurrent
         // uploads were being done. Could be fixed by increasing the evacuation
         // ratio in bin/jmeter[.bat], but this is better.
         InputStream in = new BufferedInputStream(new FileInputStream(filename));
         int read;
         try {
             while ((read = in.read(buf)) > 0) {
                 out.write(buf, 0, read);
             }
         }
         finally {
             in.close();
         }
     }
 
     /**
      * Writes form data in multipart format.
      */
-    private void writeFormMultipart(OutputStream out, String name, String value, String charSet)
+    private void writeFormMultipart(OutputStream out, String name, String value, String charSet, 
+            boolean browserCompatibleMultipart)
         throws IOException {
         writeln(out, "Content-Disposition: form-data; name=\"" + name + "\""); // $NON-NLS-1$ // $NON-NLS-2$
-        writeln(out, "Content-Type: text/plain; charset=" + charSet); // $NON-NLS-1$
-        writeln(out, "Content-Transfer-Encoding: 8bit"); // $NON-NLS-1$
-
+        if (!browserCompatibleMultipart){
+            writeln(out, "Content-Type: text/plain; charset=" + charSet); // $NON-NLS-1$
+            writeln(out, "Content-Transfer-Encoding: 8bit"); // $NON-NLS-1$
+        }
         out.write(CRLF);
         out.write(value.getBytes(charSet));
         out.write(CRLF);
         // Write boundary end marker
         out.write(getMultipartDivider());
     }
 
     private void write(OutputStream out, String value)
     throws UnsupportedEncodingException, IOException
     {
         out.write(value.getBytes(ENCODING));
     }
 
 
     private void writeln(OutputStream out, String value)
     throws UnsupportedEncodingException, IOException
     {
         out.write(value.getBytes(ENCODING));
         out.write(CRLF);
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 6d3ad5c85..28f325c5c 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,202 +1,203 @@
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
 	<author email="dev AT jakarta.apache.org">JMeter developers</author>     
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
 
 <h1>Version 2.4.1</h1>
 
 <h2>Summary of main changes</h2>
 
 <p>
 <ul>
 </ul>
 </p>
 
 
 <!--  ========================= End of summary ===================================== -->
 
 <h2>Known bugs</h2>
 
 <p>
 The Include Controller has some problems in non-GUI mode. 
 In particular, it can cause a NullPointerException if there are two include controllers with the same name.
 </p>
 
 <p>Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</p>
 
 <p>
 The menu item Options / Choose Language does not change all the displayed text to the new language.
 [The behaviour has improved, but language change is still not fully working]
 To override the default local language fully, set the JMeter property "language" before starting JMeter. 
 </p>
 
 <h2>Incompatible changes</h2>
 
 <p>
 Unsupported methods are no longer converted to GET by the Commons HttpClient sampler.
 </p>
 
 <p>
 Removed method public static long currentTimeInMs().
 This has been replaced by the instance method public long currentTimeInMillis().
 </p>
 
 <p>
 ProxyControl.getSamplerTypeName() now returns a String rather than an int.
 This is internal to the workings of the JMeter Proxy &amp; its GUI, so should not affect any user code.
 </p>
 
 <h2>Bug fixes</h2>
 
 <h3>HTTP Samplers and Proxy</h3>
 <ul>
 <li>Bug 50178 - HeaderManager added as child of Thread Group can create concatenated HeaderManager names and OutOfMemoryException</li>
 <li>Bug 50392 - value is trimmed when sending the request in Multipart</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
 <li>Bug 50173 - JDBCSampler discards ResultSet from a PreparedStatement</li>
 <li>Ensure JSR223 Sampler has access to the current SampleResult</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 50032 - Last_Sample_Ok along with other controllers doesnt work correctly when the threadgroup has multiple loops</li>
 <li>Bug 50080 - Transaction controller incorrectly creates samples including timer duration</li>
 <li>Bug 50134 - TransactionController : Reports bad response time when it contains other TransactionControllers</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>Bug 50367 - Clear / Clear all in View results tree does not clear selected element</li>
 </ul>
 
 <h3>Assertions</h3>
 <ul>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 50568 - Function __FileToString(): Could not read file when encoding option is blank/empty</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 49734 - Null pointer exception on stop Threads command (Run>Stop)</li>
 <li>Bug 49666 - CSV Header read as data after EOF</li>
 <li>Bug 45703 - Synchronizing Timer</li>
 <li>Bug 50088 - fix getAvgPageBytes in SamplingStatCalculator so it returns what it should</li>
 <li>Bug 50203 Cannot set property "jmeter.save.saveservice.default_delimiter=\t"</li>
 <li>mirror-server.sh - fix classpath to use : separator (not ;)</li>
 <li>Bug 50286 - URL Re-writing Modifier: extracted jsessionid value is incorrect when is between XML tags</li>
 <li>
 System.nanoTime() tends to drift relative to System.currentTimeMillis().
 Change SampleResult to recalculate offset each time.
 Also enable reversion to using System.currentTimeMillis() only.
 </li>
 <li>Bug 50425 - Remove thread groups from Controller add menu</li>
 <li>
 Bug 50675 - CVS Data Set Config incompatible with Remote Start
 Fixed RMI startup to provide location of JMX file relative to user.dir.
 </li>
 </ul>
 
 <!-- ==================================================== -->
 
 <h2>Improvements</h2>
 
 <h3>HTTP Samplers</h3>
 <ul>
 <li>AJP Sampler now implements Interruptible</li>
 <li>Allow HTTP implementation to be selected at run-time</li>
+<li>Bug 50684 - Optionally disable Content-Type and Transfer-Encoding in Multipart POST</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
 <li>Bug 49622 - Allow sending messages without a subject (SMTP Sampler)</li>
 <li>Bug 49603 - Allow accepting expired certificates on Mail Reader Sampler</li>
 <li>Bug 49775 - Allow sending messages without a body</li>
 <li>Bug 49862 - Improve SMTPSampler Request output.</li>
 <li>Bug 50268 - Adds static and dynamic destinations to JMS Publisher</li>
 <li>JMS Subscriber - Add dynamic destination</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 <li>Bug 50475 - Introduction of a Test Fragment Test Element for a better Include flow</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li>View Results Tree - Add a dialog's text box on "Sampler result tab > Parsed" to display the long value with a double click on cell</li>
 <li>Bug 37156 - Formatted view of Request in Results Tree</li>
 <li>Bug 49365 - Allow result set to be written to file in a path relative to the loaded script</li>
 <li>Bug 50579 - Error count is long, sample count is int. Changed sample count to long.</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li>Bug 48015 - Proposal new icons for pre-processor, post-processor and assertion elements</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li>Bug 49975 - New function returning the name of the current sampler</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li>Bug 30563 - Thread Group should have a start next loop option on Sample Error</li>
 <li>Bug 50347 - Eclipse setup instructions should remind user to download dependent jars</li>
 <li>Bug 50490 - Setup and Post Thread Group enhancements for better test flow.</li>
 <li>All BeansShell test elements now have the script variables "prev" and "Label" defined.</li>
 </ul>
 
 <h2>Non-functional changes</h2>
 <ul>
 <li>Bug 50008 - Allow BatchSampleSender to be subclassed</li>
 <li>Bug 50450 - use System.array copy in jacobi solver as, being native, is more performant.</li>
 <li>Bug 50487 - runSerialTest verifies objects that never need persisting</li>
 </ul>
 
 </section> 
 </body> 
 </document>
diff --git a/xdocs/images/screenshots/http-request.png b/xdocs/images/screenshots/http-request.png
index bec275671..b4cca2d51 100644
Binary files a/xdocs/images/screenshots/http-request.png and b/xdocs/images/screenshots/http-request.png differ
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index 44b158be6..eb8827aa8 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -1,1216 +1,1220 @@
 <?xml version="1.0"?>
 <!-- 
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
  -->
 <!DOCTYPE document
 [
 <!ENTITY sect-num '18'>
 ]>
 <document index="yes" index-level-2="yes" index-numbers="no" colbreak="&sect-num;.4"
   prev="boss.html" next="functions.html" date="$Date$">
 
 <properties>
   <title>User's Manual: Component Reference</title>
 </properties>
 
 <body>
 
 <!--
 	Because this is an XML document, all tags must be properly closed, including ones
 	which are passed unchanged into the HTML output, e.g. <br/>, not just <br>.
 	
 	Unfortunately Java does not currently allow for this - it outputs the trailing > -
 	which messes up the Help display. 
 	To avoid these artefacts, use the form <br></br>, which Java does seem to handle OK.
 
  -->
 <section name="&sect-num;.1 Samplers" anchor="samplers">
 <description>
 	<p>
 	Samplers perform the actual work of JMeter.
 	Each sampler (except Test Action) generates one or more sample results.
 	The sample results have various attributes (success/fail, elapsed time, data size etc) and can be viewed in the various listeners.
 	</p>
 </description>
 <component name="FTP Request" index="&sect-num;.1.1" width="499" height="292" screenshot="ftptest/ftp-request.png">
 <description>
 This controller lets you send an FTP "retrieve file" or "upload file" request to an FTP server.
 If you are going to send multiple requests to the same FTP server, consider
 using a <complink name="FTP Request Defaults"/> Configuration
 Element so you do not have to enter the same information for each FTP Request Generative
 Controller. When downloading a file, it can be stored on disk (Local File) or in the Response Data, or both.
 <p>
 Latency is set to the time it takes to login (versions of JMeter after 2.3.1).
 </p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server Name or IP" required="Yes">Domain name or IP address of the FTP server.</property>
         <property name="Port" required="No">Port to use. If this is  >0, then this specific port is used, otherwise JMeter uses the default FTP port.</property>
         <property name="Remote File:" required="Yes">File to retrieve or name of destination file to upload.</property>
         <property name="Local File:" required="Yes, if uploading (*)">File to upload, or destination for downloads (defaults to remote file name).</property>
         <property name="Local File Contents:" required="Yes, if uploading (*)">Provides the contents for the upload, overrides the Local File property.</property>
         <property name="get(RETR) / put(STOR)" required="Yes">Whether to retrieve or upload a file.</property>
         <property name="Use Binary mode ?" required="Yes">Check this to use Binary mode (default Ascii)</property>
         <property name="Save File in Response ?" required="Yes, if downloading">
         Whether to store contents of retrieved file in response data.
         If the mode is Ascii, then the contents will be visible in the Tree View Listener.
         </property>
         <property name="Username" required="Usually">FTP account username.</property>
         <property name="Password" required="Usually">FTP account password. N.B. This will be visible in the test plan.</property>
 </properties>
 <links>
         <link href="test_plan.html#assertions">Assertions</link>
         <complink name="FTP Request Defaults"/>
         <link href="build-ftp-test-plan.html">Building an FTP Test Plan</link>
 </links>
 
 </component>
 
-<component name="HTTP Request" index="&sect-num;.1.2"  width="826" height="677" screenshot="http-request.png">
+<component name="HTTP Request" index="&sect-num;.1.2"  width="851" height="661" screenshot="http-request.png">
 
 <description>
         <p>This sampler lets you send an HTTP/HTTPS request to a web server.  It
         also lets you control whether or not JMeter parses HTML files for images and
         other embedded resources and sends HTTP requests to retrieve them.
         The following types of embedded resource are retrieved:</p>
         <ul>
         <li>images</li>
         <li>applets</li>
         <li>stylesheets</li>
         <li>external scripts</li>
         <li>frames</li>
         <li>background images (body, table, TD, TR)</li>
         <li>background sound</li>
         </ul>
         <p>
         The default parser is htmlparser.
         This can be changed by using the property "htmlparser.classname" - see jmeter.properties for details.
         </p>
         <p>If you are going to send multiple requests to the same web server, consider
         using an <complink name="HTTP Request Defaults"/>
         Configuration Element so you do not have to enter the same information for each
         HTTP Request.</p>
 
         <p>Or, instead of manually adding HTTP Requests, you may want to use
         JMeter's <complink name="HTTP Proxy Server"/> to create
         them.  This can save you time if you have a lot of HTTP requests or requests with many
         parameters.</p>
 
         <p><b>There are three versions of the sampler:</b>
         <ul>
         <li>HTTP Request - uses the default Java HTTP implementation</li>
         <li>HTTP Request HTTPClient - uses Apache Commons HttpClient</li>
         <li>AJP/1.3 Sampler - uses the Tomcat mod_jk protocol (allows testing of Tomcat in AJP mode without needing Apache httpd)
         The AJP Sampler does not support multiple file upload; only the first file will be used.
         </li>
         </ul>
          </p>
          <p>The default (Java) implementation has some limitations:</p>
          <ul>
          <li>There is no control over how connections are re-used. 
          When a connection is released by JMeter, it may or may not be re-used by the same thread.</li>
          <li>The API is best suited to single-threaded usage - various settings (e.g. proxy) 
          are defined via system properties, and therefore apply to all connections.</li>
          <li>There is a bug in the handling of HTTPS via a Proxy (the CONNECT is not handled correctly).
          See Java bugs 6226610 and 6208335.
          </li>
          </ul>
          <p>Note: the FILE protocol is intended for testing puposes only. 
          It is handled by the same code regardless of which HTTP Sampler is used.</p>
         <p>If the request requires server or proxy login authorization (i.e. where a browser would create a pop-up dialog box),
          you will also have to add an <complink name="HTTP Authorization Manager"/> Configuration Element.
          For normal logins (i.e. where the user enters login information in a form), you will need to work out what the form submit button does,
          and create an HTTP request with the appropriate method (usually POST) 
          and the appropriate parameters from the form definition. 
          If the page uses HTTP, you can use the JMeter Proxy to capture the login sequence.
         </p>
         <p>
         In versions of JMeter up to 2.2, only a single SSL context was used for all threads and samplers.
         This did not generate the proper load for multiple users.
         A separate SSL context is now used for each thread.
         To revert to the original behaviour, set the JMeter property:
 <pre>
 https.sessioncontext.shared=true
 </pre>
         </p>
         <p>
         JMeter defaults to the SSL protocol level TLS.
         If the server needs a different level, e.g. SSLv3, change the JMeter property, for example:
 <pre>
 https.default.protocol=SSLv3
 </pre> 
         </p>
         <p>
         JMeter also allows one to enable additional protocols, by changing the property <tt>https.socket.protocols</tt>.
         </p>
         <p>If the request uses cookies, then you will also need an
         <complink name="HTTP Cookie Manager"/>.  You can
         add either of these elements to the Thread Group or the HTTP Request.  If you have
         more than one HTTP Request that needs authorizations or cookies, then add the
         elements to the Thread Group.  That way, all HTTP Request controllers will share the
         same Authorization Manager and Cookie Manager elements.</p>
 
         <p>If the request uses a technique called "URL Rewriting" to maintain sessions,
         then see section
         <a href="build-adv-web-test-plan.html#session_url_rewriting">6.1 Handling User Sessions With URL Rewriting</a>
         for additional configuration steps.</p>
 </description>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes, unless provided by HTTP Request Defaults">Domain name or IP address of the web server. e.g. www.example.com. [Do not include the http:// prefix.]</property>
         <property name="Port" required="No">Port the web server is listening to. Default: 80</property>
         <property name="Connect Timeout" required="No">Connection Timeout. Number of milliseconds to wait for a connection to open.</property>
         <property name="Response Timeout" required="No">Response Timeout. Number of milliseconds to wait for a response.</property>
         <property name="Server (proxy)" required="No">Hostname or IP address of a proxy server to perform request. [Do not include the http:// prefix.]</property>
         <property name="Port" required="No, unless proxy hostname is specified">Port the proxy server is listening to.</property>
         <property name="Username" required="No">(Optional) username for proxy server.</property>
         <property name="Password" required="No">(Optional) password for proxy server.</property>
         <property name="Implementation" required="No">Java, HttpClient3.1, HttpClient4. 
         If not specified (and not defined by HTTP Request Defaults), the default depends on the value of the JMeter property
         <code>jmeter.httpsampler</code>, failing that, the Java implementation is used.</property>
         <property name="Protocol" required="No">HTTP, HTTPS or FILE. Default: HTTP</property>
         <property name="Method" required="Yes">GET, POST, HEAD, TRACE, OPTIONS, PUT, DELETE</property>
         <property name="Content Encoding" required="No">Content encoding to be used (for POST and FILE)</property>
 		<property name="Redirect Automatically" required="Yes">
 		Sets the underlying http protocol handler to automatically follow redirects,
 		so they are not seen by JMeter, and thus will not appear as samples.
 		Should only be used for GET and HEAD requests.
 		The HttpClient sampler will reject attempts to use it for POST or PUT.
 		<b>Warning: see below for information on cookie and header handling.</b>
         </property>
 		<property name="Follow Redirects" required="Yes">
 		This only has any effect if "Redirect Automatically" is not enabled.
 		If set, the JMeter sampler will check if the response is a redirect and follow it if so.
 		The initial redirect and further responses will appear as additional samples.
         The URL and data fields of the parent sample will be taken from the final (non-redirected)
         sample, but the parent byte count and elapsed time include all samples.
         The latency is taken from the initial response (versions of JMeter after 2.3.4 - previously it was zero).
 		Note that the HttpClient sampler may log the following message:<br/>
 		"Redirect requested but followRedirects is disabled"<br/>
 		This can be ignored.
         <br/>
         In versions after 2.3.4, JMeter will collapse paths of the form '/../segment' in
         both absolute and relative redirect URLs. For example http://host/one/../two => http://host/two.
         If necessary, this behaviour can be suppressed by setting the JMeter property
         <code>httpsampler.redirect.removeslashdotdot=false</code>
 		</property>
 		<property name="Use KeepAlive" required="Yes">JMeter sets the Connection: keep-alive header. This does not work properly with the default HTTP implementation, as connection re-use is not under user-control. 
                   It does work with the Jakarta httpClient implementation.</property>
         <property name="Use multipart/form-data for HTTP POST" required="Yes">
         Use a multipart/form-data or application/x-www-form-urlencoded post request
         </property>
+        <property name="Browser-compatible headers" required="Yes">
+        When using multipart/form-data, this suppresses the Content-Type and 
+        Content-Transfer-Encoding headers; only the Content-Disposition header is sent.
+        </property>
         <property name="Path" required="Yes">The path to resource (for example, /servlets/myServlet). If the
 resource requires query string parameters, add them below in the
 "Send Parameters With the Request" section.
 <b>
 As a special case, if the path starts with "http://" or "https://" then this is used as the full URL.
 </b>
 In this case, the server, port and protocol are ignored; parameters are also ignored for GET and DELETE methods.
 </property>
         <property name="Send Parameters With the Request" required="No">The query string will
         be generated from the list of parameters you provide.  Each parameter has a <i>name</i> and
         <i>value</i>, the options to encode the parameter, and an option to include or exclude an equals sign (some applications
         don't expect an equals when the value is the empty string).  The query string will be generated in the correct fashion, depending on
         the choice of "Method" you made (ie if you chose GET or DELETE, the query string will be
         appended to the URL, if POST or PUT, then it will be sent separately).  Also, if you are
         sending a file using a multipart form, the query string will be created using the
         multipart form specifications.
         <b>See below for some further information on parameter handling.</b>
         <p>
         Additionally, you can specify whether each parameter should be URL encoded.  If you are not sure what this
         means, it is probably best to select it.  If your values contain characters such as &amp;amp; or spaces, or
         question marks, then encoding is usually required.</p></property>
         <property name="File Path:" required="No">Name of the file to send.  If left blank, JMeter
         does not send a file, if filled in, JMeter automatically sends the request as
         a multipart form request.
         <p>
         If it is a POST or PUT request and there is a single file whose 'name' attribute (below) is omitted, 
         then the file is sent as the entire body
         of the request, i.e. no wrappers are added. This allows arbitrary bodies to be sent. This functionality is present for POST requests
         after version 2.2, and also for PUT requests after version 2.3.
         <b>See below for some further information on parameter handling.</b>
         </p>
         </property>
         <property name="Parameter name:" required="No">Value of the "name" web request parameter.</property>
         <property name="MIME Type" required="No">MIME type (for example, text/plain).
         If it is a POST or PUT request and either the 'name' atribute (below) are omitted or the request body is
         constructed from parameter values only, then the value of this field is used as the value of the
         content-type request header.
         </property>
         <property name="Retrieve All Embedded Resources from HTML Files" required="No">Tell JMeter to parse the HTML file
 and send HTTP/HTTPS requests for all images, Java applets, JavaScript files, CSSs, etc. referenced in the file.
         See below for more details.
         </property>
         <property name="Use as monitor" required="Yes">For use with the <complink name="Monitor Results"/> listener.</property>
        <property name="Save response as MD5 hash?" required="Yes">
        If this is selected, then the response is not stored in the sample result.
        Instead, the 32 character MD5 hash of the data is calculated and stored instead.
        This is intended for testing large amounts of data.
        </property>
         <property name="Embedded URLs must match:" required="No">
         If present, this must be a regular expression that is used to match against any embedded URLs found.
         So if you only want to download embedded resources from http://example.com/, use the expression:
         http://example\.com/.*
         </property>
         <property name="Source IP address:" required="No">
         [Only for HTTP Request HTTPClient] 
         Override the default local IP address for this sample.
         The JMeter host must have multiple IP addresses (i.e. IP aliases or network interfaces). 
         If the property <b>httpclient.localaddress</b> is defined, that is used for all HttpClient requests.
         </property>
 </properties>
 <p>
 <b>N.B.</b> when using Automatic Redirection, cookies are only sent for the initial URL.
 This can cause unexpected behaviour for web-sites that redirect to a local server.
 E.g. if www.example.com redirects to www.example.co.uk.
 In this case the server will probably return cookies for both URLs, but JMeter will only see the cookies for the last
 host, i.e. www.example.co.uk. If the next request in the test plan uses www.example.com, 
 rather than www.example.co.uk, it will not get the correct cookies.
 Likewise, Headers are sent for the initial request, and won't be sent for the redirect.
 This is generally only a problem for manually created test plans,
 as a test plan created using a recorder would continue from the redirected URL.
 </p>
 <p>
 <b>Parameter Handling:</b><br></br>
 For the POST and PUT method, if there is no file to send, and the name(s) of the parameter(s) are omitted,
 then the body is created by concatenating all the value(s) of the parameters.
 This allows arbitrary bodies to be sent.
 The values are encoded if the encoding flag is set (versions of JMeter after 2.3).
 See also the MIME Type above how you can control the content-type request header that is sent.
 <br></br>
 For other methods, if the name of the parameter is missing,
 then the parameter is ignored. This allows the use of optional parameters defined by variables.
 (versions of JMeter after 2.3)
 </p>
 <p>
 <b>Method Handling:</b><br></br>
 The POST and PUT request methods work similarly, except that the PUT method does not support multipart requests.
 The PUT method body must be provided as one of the following:
 <ul>
 <li>define the body as a file</li>
 <li>define the body as parameter value(s) with no name</li>
 </ul>
 If you define any parameters with a name in either the sampler or Http
 defaults then nothing is sent.
 The GET and DELETE request methods work similarly to each other.
 </p>
 <p>Upto and including JMeter 2.1.1, only responses with the content-type "text/html" were scanned for
 embedded resources. Other content-types were assumed to be something other than HTML.
 JMeter 2.1.2 introduces the a new property <b>HTTPResponse.parsers</b>, which is a list of parser ids,
  e.g. <b>htmlParser</b> and <b>wmlParser</b>. For each id found, JMeter checks two further properties:</p>
  <ul>
  <li>id.types - a list of content types</li>
  <li>id.className - the parser to be used to extract the embedded resources</li>
  </ul>
  <p>See jmeter.properties file for the details of the settings. 
  If the HTTPResponse.parser property is not set, JMeter reverts to the previous behaviour,
  i.e. only text/html responses will be scanned</p>
 <b>Emulating slow connections (HttpClient only):</b><br></br>
 The HttpClient version of the sampler supports emulation of slow connections; see the following entries in jmeter.properties:
 <pre>
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 </pre>
 <links>
         <link href="test_plan.html#assertions">Assertion</link>
         <link href="build-web-test-plan.html">Building a Web Test Plan</link>
         <link href="build-adv-web-test-plan.html">Building an Advanced Web Test Plan</link>
         <complink name="HTTP Authorization Manager"/>
         <complink name="HTTP Cookie Manager"/>
         <complink name="HTTP Header Manager"/>
         <complink name="HTML Link Parser"/>
         <complink name="HTTP Proxy Server"/>
         <complink name="HTTP Request Defaults"/>
         <link href="build-adv-web-test-plan.html#session_url_rewriting">HTTP Requests and Session ID's: URL Rewriting</link>
 </links>
 
 </component>
 
 <component name="JDBC Request" index="&sect-num;.1.3"  width="427" height="334" screenshot="jdbctest/jdbc-request.png">
 
 <description><p>This sampler lets you send an JDBC Request (an SQL query) to a database.</p>
 <p>Before using this you need to set up a
 <complink name="JDBC Connection Configuration"/> Configuration element
 </p>
 <p>
 If the Variable Names list is provided, then for each row returned by a Select statement, the variables are set up
 with the value of the corresponding column (if a variable name is provided), and the count of rows is also set up.
 For example, if the Select statement returns 2 rows of 3 columns, and the variable list is <code>A,,C</code>,
 then the following variables will be set up:
 <pre>
 A_#=2 (number of rows)
 A_1=column 1, row 1
 A_2=column 1, row 2
 C_#=2 (number of rows)
 C_1=column 3, row 1
 C_2=column 3, row 2
 </pre>
 If the Select statement returns zero rows, then the A_# and C_# variables would be set to 0, and no other variables would be set.
 </p>
 <p>
 Old variables are cleared if necessary - e.g. if the first select retrieves 6 rows and a second select returns only 3 rows,
 the additional variables for rows 4, 5 and 6 will be removed.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 		<property name="Variable Name" required="Yes">
 		Name of the JMeter variable that the connection pool is bound to.
 		This must agree with the 'Variable Name' field of a JDBC Connection Configuration.
 		</property>
 		<property name="Query Type" required="Yes">Set this according to the statement type:
 		    <ul>
 		    <li>Select Statement</li>
 		    <li>Update Statement - use this for Inserts as well</li>
 		    <li>Callable Statement</li>
 		    <li>Prepared Select Statement</li>
 		    <li>Prepared Update Statement - use this for Inserts as well</li>
 		    <li>Commit</li>
 		    <li>Rollback</li>
 		    <li>Autocommit(false)</li>
 		    <li>Autocommit(true)</li>
 		    <li>Edit - this should be a variable reference that evaluates to one of the above</li>
 		    </ul>
 		</property>
         <property name="SQL Query" required="Yes">
         SQL query.
         Do not enter a trailing semi-colon.
         There is generally no need to use { and } to enclose Callable statements;
         however they mey be used if the database uses a non-standard syntax.
         [The JDBC driver automatically converts the statement if necessary when it is enclosed in {}].
         For example:
         <ul>
         <li>select * from t_customers where id=23</li>
         <li>CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (null,?, ?, null, null, null)
         <ul>
         <li>Parameter values: tablename,filename</li>
         <li>Parameter types:  VARCHAR,VARCHAR</li>
         </ul>
         </li>
         The second example assumes you are using Apache Derby.
         </ul>
         </property>
         <property name="Parameter values" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of parameter values. Use ]NULL[ to indicate a NULL parameter.
         (If required, the null string can be changed by defining the property "jdbcsampler.nullmarker".)
         <br></br>
         The list must be enclosed in double-quotes if any of the values contain a comma or double-quote,
         and any embedded double-quotes must be doubled-up, for example:
         <pre>"Dbl-Quote: "" and Comma: ,"</pre>
         There must be as many values as there are placeholders in the statement.
         </property>
         <property name="Parameter types" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of SQL parameter types (e.g. INTEGER, DATE, VARCHAR, DOUBLE).
         These are defined as fields in the class java.sql.Types, see for example:
         <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/sql/Types.html">Javadoc for java.sql.Types</a>.
         [Note: JMeter will use whatever types are defined by the runtime JVM, 
         so if you are running on a different JVM, be sure to check the appropriate document]
         If the callable statement has INOUT or OUT parameters, then these must be indicated by prefixing the
         appropriate parameter types, e.g. instead of "INTEGER", use "INOUT INTEGER".
         If not specified, "IN" is assumed, i.e. "DATE" is the same as "IN DATE".
         <br></br>
         If the type is not one of the fields found in java.sql.Types, versions of JMeter after 2.3.2 also
         accept the corresponding integer number, e.g. since INTEGER == 4, you can use "INOUT 4".
         <br></br>
         There must be as many types as there are placeholders in the statement.
         </property>
         <property name="Variable Names" required="No">Comma-separated list of variable names to hold values returned by Select statements</property>
 </properties>
 
 <links>
         <link href="build-db-test-plan.html">Building a Database Test Plan</link>
         <complink name="JDBC Connection Configuration"/>
 </links>
 <note>Versions of JMeter after 2.3.2 use UTF-8 as the character encoding. Previously the platform default was used.</note>
 </component>
 
 <component name="Java Request" index="&sect-num;.1.4"  width="406" height="307" screenshot="java_request.png">
 
 <description><p>This sampler lets you control a java class that implements the
 <b><code>org.apache.jmeter.protocol.java.sampler.JavaSamplerClient</code></b> interface.
 By writing your own implementation of this interface,
 you can use JMeter to harness multiple threads, input parameter control, and
 data collection.</p>
 <p>The pull-down menu provides the list of all such implementations found by
 JMeter in its classpath.  The parameters can then be specified in the
 table below - as defined by your implementation.  Two simple examples (JavaTest and SleepTest) are provided.
 </p>
 <p>
 The JavaTest example sampler can be useful for checking test plans, because it allows one to set
 values in almost all the fields. These can then be used by Assertions, etc.
 The fields allow variables to be used, so the values of these can readily be seen.
 </p>
 </description>
 
 <note>The Add/Delete buttons don't serve any purpose at present.</note>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="Classname" required="Yes">The specific implementation of
         the JavaSamplerClient interface to be sampled.</property>
         <property name="Send Parameters with Request" required="No">A list of
         arguments that will be passed to the sampled class.  All arguments
         are sent as Strings.</property>
         </properties>
 </component>
 
 <p>The sleep time is calculated as follows:</p>
 <pre>
 SleepTime is in milliseconds
 SleepMask is used to add a "random" element to the time:
 totalSleepTime = SleepTime + (System.currentTimeMillis() % SleepMask)
 </pre>
 <component name="SOAP/XML-RPC Request" index="&sect-num;.1.5"  width="474" height="236" screenshot="soap_sampler.png">
 
 <description><p>This sampler lets you send a SOAP request to a webservice.  It can also be
 used to send XML-RPC over HTTP.  It creates an HTTP POST request, with the specified XML as the
 POST content. 
 To change the "Content-type" from the default of "text/xml", use a HeaderManager. 
 Note that the sampler will use all the headers from the HeaderManager.
 If a SOAP action is specified, that will override any SOAPaction in the HeaderManager.
 The primary difference between the soap sampler and
 webservice sampler, is the soap sampler uses raw post and does not require conformance to
 SOAP 1.1.</p>
 <note>For versions of JMeter later than 2.2, the sampler no longer uses chunked encoding by default.<br/>
 For screen input, it now always uses the size of the data.<br/>
 File input uses the file length as determined by Java.<br/>
 On some OSes this may not work for all files, in which case add a child Header Manager
 with Content-Length set to the actual length of the file.<br/>
 Or set Content-Length to -1 to force chunked encoding.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="URL" required="Yes">The URL to direct the SOAP request to.</property>
         <property name="Send SOAP action" required="No">Send a SOAP action header? (overrides the Header Manager)</property>
         <property name="Soap/XML-RPC Data" required="No">The Soap XML message, or XML-RPC instructions.
         Not used if the filename is provided.
         </property>
         <property name="Filename" required="No">If specified, then the contents of the file are sent, and the Data field is ignored</property>
         </properties>
 
 </component>
 
 <component name="WebService(SOAP) Request" index="&sect-num;.1.6"  width="460" height="781" screenshot="webservice_sampler.png">
 <description><p>This sampler has been tested with IIS Webservice running .NET 1.0 and .NET 1.1.
  It has been tested with SUN JWSDP, IBM webservices, Axis and gSoap toolkit for C/C++.
  The sampler uses Apache SOAP driver to serialize the message and set the header
  with the correct SOAPAction. Right now the sampler doesn't support automatic WSDL
  handling, since Apache SOAP currently does not provide support for it. Both IBM
  and SUN provide WSDL drivers. There are 3 options for the post data: text area,
  external file, or directory. If you want the sampler to randomly select a message,
  use the directory. Otherwise, use the text area or a file. The if either the
  file or path are set, it will not use the message in the text area. If you need
  to test a soap service that uses different encoding, use the file or path. If you
  paste the message in to text area, it will not retain the encoding and will result
  in errors. Save your message to a file with the proper encoding, and the sampler
  will read it as java.io.FileInputStream.</p>
  <p>An important note on the sampler is it will automatically use the proxy host
  and port passed to JMeter from command line, if those fields in the sampler are
  left blank. If a sampler has values in the proxy host and port text field, it
  will use the ones provided by the user. This behavior may not be what users
  expect.</p>
  <p>By default, the webservice sampler sets SOAPHTTPConnection.setMaintainSession
  (true). If you need to maintain the session, add a blank Header Manager. The
  sampler uses the Header Manager to store the SOAPHTTPConnection object, since
  the version of apache soap does not provide a easy way to get and set the cookies.</p>
  <p><b>Note:</b> If you are using CSVDataSet, do not check "Memory Cache". If memory
  cache is checked, it will not iterate to the next value. That means all the requests
  will use the first value.</p>
  <p>Make sure you use &amp;lt;soap:Envelope rather than &amp;lt;Envelope. For example:</p>
  <pre>
 &amp;lt;?xml version="1.0" encoding="utf-8"?>
 &amp;lt;soap:Envelope 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
 xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 &amp;lt;soap:Body>
 &amp;lt;foo xmlns="http://clients-xlmns"/>
 &amp;lt;/soap:Body>
 &amp;lt;/soap:Envelope>
 </pre>
 <note>The SOAP library that is used does not support SOAP 1.2, only SOAP 1.1. 
 Also the library does not provide access to the HTTP response code (e.g. 200) or message (e.g. OK). 
 To get round this, versions of JMeter after 2.3.2 check the returned message length.
 If this is zero, then the request is marked as failed.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="WSDL URL" required="No">The WSDL URL with the service description.
         Versions of JMeter after 2.3.1 support the file: protocol for local WSDL files.
         </property>
         <property name="Web Methods" required="No">Will be populated from the WSDL when the Load WSDL button is pressed.
         Select one of the methods and press the Configure button to populate the Protocol, Server, Port, Path and SOAPAction fields. 
         </property>
         <property name="Protocol" required="Yes">HTTP or HTTPS are acceptable protocol.</property>
         <property name="Server Name or IP" required="Yes">The hostname or IP address.</property>
         <property name="Port Number" required="Yes">Port Number.</property>
         <property name="Path" required="Yes">Path for the webservice.</property>
         <property name="SOAPAction" required="Yes">The SOAPAction defined in the webservice description or WSDL.</property>
         <property name="Soap/XML-RPC Data" required="Yes">The Soap XML message</property>
         <property name="Soap file" required="No">File containing soap message</property>
         <property name="Message Folder" required="No">Folder containing soap files</property>
         <property name="Memory cache" required="Yes">
         When using external files, setting this causes the file to be processed once and caches the result.
         This may use a lot of memory if there are many different large files.
         </property>
         <property name="Use HTTP Proxy" required="No">Check box if http proxy should be used</property>
         <property name="Proxy Host" required="No">Proxy hostname</property>
         <property name="Proxy Port" required="No">Proxy host port</property>
         </properties>
 
 </component>
 
 <component name="LDAP Request" index="&sect-num;.1.7"  width="505" height="476" screenshot="ldap_request.png">
   <description>This Sampler lets you send a different Ldap request(Add, Modify, Delete and Search) to an LDAP server.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> The same way the <complink name="Login Config Element"/> also using for Login and password.
   </description>
 
   <p>There are two ways to create test cases for testing an LDAP Server.</p>
   <ol><li>Inbuilt Test cases.</li>
     <li>User defined Test cases.</li></ol>
 
     <p>There are four test scenarios of testing LDAP. The tests are given below:</p>
     <ol>
       <li>Add Test</li>
       <ol><li>Inbuilt test :
         <p>This will add a pre-defined entry in the LDAP Server and calculate
           the execution time. After execution of the test, the created entry will be
           deleted from the LDAP
           Server.</p></li>
           <li>User defined test :
             <p>This will add the entry in the LDAP Server. User has to enter all the
               attributes in the table.The entries are collected from the table to add. The
               execution time is calculated. The created entry will not be deleted after the
               test.</p></li></ol>
 
               <li>Modify Test</li>
               <ol><li>Inbuilt test :
                 <p>This will create a pre-defined entry first, then will modify the
                   created	entry in the LDAP Server.And calculate the execution time. After
                   execution
                   of the test, the created entry will be deleted from the LDAP Server.</p></li>
                   <li>User defined test
                     <p>This will modify the entry in the LDAP Server. User has to enter all the
                       attributes in the table. The entries are collected from the table to modify.
                       The execution time is calculated. The entry will not be deleted from the LDAP
                       Server.</p></li></ol>
 
                       <li>Search Test</li>
                       <ol><li>Inbuilt test :
                         <p>This will create the entry first, then will search if the attributes
                           are available. It calculates the execution time of the search query. At the
                           end of  the execution,created entry will be deleted from the LDAP Server.</p></li>
                           <li>User defined test
                             <p>This will search the user defined entry(Search filter) in the Search
                               base (again, defined by the user). The entries should be available in the LDAP
                               Server. The execution time is  calculated.</p></li></ol>
 
                               <li>Delete Test</li>
                               <ol><li>Inbuilt test :
                                 <p>This will create a pre-defined entry first, then it will be deleted
                                   from the LDAP Server. The execution time is calculated.</p></li>
 
                                   <li>User defined test
                                     <p>This will delete the user-defined entry in the LDAP Server. The entries
                                       should be available in the LDAP Server. The execution time is calculated.</p></li></ol></ol>
                                       <properties>
                                         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
                                         <property name="Server Name or IP" required="Yes">Domain name or IP address of the LDAP server.
                                           JMeter assumes the LDAP server is listening on the default port(389).</property>
                                           <property name="Port" required="Yes">default port(389).</property>
                                           <property name="root DN" required="Yes">DN for the server to communicate</property>
                                           <property name="Username" required="Usually">LDAP server username.</property>
                                           <property name="Password" required="Usually">LDAP server password.</property>
                                           <property name="Entry DN" required="Yes">the name of the context to create or Modify; may not be empty Example: do you want to add cn=apache,ou=test
                                             you have to add in table name=cn, value=apache
                                           </property>
                                           <property name="Delete" required="Yes">the name of the context to Delete; may not be empty</property>
                                           <property name="Search base" required="Yes">the name of the context or object to search</property>
                                           <property name="Search filter" required="Yes"> the filter expression to use for the search; may not be null</property>
                                           <property name="add test" required="Yes"> this name, value pair to added in the given context object</property>
                                           <property name="modify test" required="Yes"> this name, value pair to add or modify in the given context object</property>
                                       </properties>
 
                                       <links>
                                         <link href="build-ldap-test-plan.html">Building an Ldap Test Plan</link>
                                         <complink name="LDAP Request Defaults"/>
                                       </links>
 
 </component>
 
 <component name="LDAP Extended Request" index="&sect-num;.1.8"  width="595" height="542" screenshot="ldapext_request.png">
   <description>This Sampler can send all 8 different LDAP request to an LDAP server. It is an extended version of the LDAP sampler,
   therefore it is harder to configure, but can be made much closer resembling a real LDAP session.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Extended Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> </description>
 
    <p>There are nine test operations defined. These operations are given below:</p>
     <ol>
       <li><b>Thread bind</b></li>
       <p>Any LDAP request is part of an LDAP session, so the first thing that should be done is starting a session to the LDAP server.
        For starting this session a thread bind is used, which is equal to the LDAP "bind" operation.
        The user is requested to give a username (Distinguished name) and password, 
        which will be used to initiate a session.
        When no password, or the wrong password is specified, an anonymous session is started. Take care,
        omitting the password will not fail this test, a wrong password will. </p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
      <property name="Servername" required="Yes">The name (or IP-address) of the LDAP server.</property>
      <property name="Port" required="No">The port number that the LDAP server is listening to. If this is omitted 
      JMeter assumes the LDAP server is listening on the default port(389).</property>
      <property name="DN" required="No">The distinguished name of the base object that will be used for any subsequent operation. 
      It can be used as a starting point for all operations. You cannot start any operation on a higher level than this DN!</property>
      <property name="Username" required="No">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error and revert to an anonymous bind.</property>
     </properties>
  <br />       
       <li><b>Thread unbind</b></li>
       <p>This is simply the operation to end a session. 
       It is equal to the LDAP "unbind" operation.</p>
      <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     </properties>
      
  <br />       
       <li><b>Single bind/unbind</b></li>
 		<p> This is a combination of the LDAP "bind" and "unbind" operations.
 		It can be used for an authentication request/password check for any user. It will open an new session, just to
 		check the validity of the user/password combination, and end the session again.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Username" required="Yes">Full distinguished name of the user as which you want to bind.</property>
      <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind. 
      If is is incorrect, the sampler will return an error.</property>
      </properties>
 		
  <br />       
       <li><b>Rename entry</b></li>
        <p>This is the LDAP "moddn" operation. It can be used to rename an entry, but 
        also for moving an entry or a complete subtree to a different place in 
        the LDAP tree.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Old entry name" required="Yes">The current distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      <property name="New distinguished name" required="Yes">The new distinguished name of the object you want to rename or move, 
       relative to the given DN in the thread bind operation.</property>
      </properties>
        
  <br />       
         <li><b>Add test</b></li>
        <p>This is the ldap "add" operation. It can be used to add any kind of 
        object to the LDAP server.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">Distinguished name of the object you want to add, relative to the given DN in the thread bind operation.</property>
      <property name="Add test" required="Yes">A list of attributes and their values you want to use for the object.
      If you need to add a multiple value attribute, you need to add the same attribute with their respective 
      values several times to the list.</property>
      </properties>
        
  <br />       
       <li><b>Delete test</b></li>
        <p> This is the LDAP "delete" operation, it can be used to delete an 
        object from the LDAP tree </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Delete" required="Yes">Distinguished name of the object you want to delete, relative to the given DN in the thread bind operation.</property>
       </properties>
        
  <br />       
       <li><b>Search test</b></li>
        <p>This is the LDAP "search" operation, and will be used for defining searches.  </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Search base" required="No">Distinguished name of the subtree you want your 
       search to look in, relative to the given DN in the thread bind operation.</property>
       <property name="Search Filter" required="Yes">searchfilter, must be specified in LDAP syntax.</property>
       <property name="Scope" required="No">Use 0 for baseobject-, 1 for onelevel- and 2 for a subtree search. (Default=0)</property>
       <property name="Size Limit" required="No">Specify the maximum number of results you want back from the server. (default=0, which means no limit.) When the sampler hits the maximum number of results, it will fail with errorcode 4</property>
       <property name="Time Limit" required="No">Specify the maximum amount of (cpu)time (in miliseconds) that the server can spend on your search. Take care, this does not say anything about the responsetime. (default is 0, which means no limit)</property>
       <property name="Attributes" required="No">Specify the attributes you want to have returned, seperated by a semicolon. An empty field will return all attributes</property>
       <property name="Return object" required="No">Whether the object will be returned (true) or not (false). Default=false</property>
       <property name="Dereference aliases" required="No">If true, it will dereference aliases, if false, it will not follow them (default=false)</property>
      </properties>
 
  <br />       
       <li><b>Modification test</b></li>
        <p>This is the LDAP "modify" operation. It can be used to modify an object. It
        can be used to add, delete or replace values of an attribute. </p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry name" required="Yes">Distinguished name of the object you want to modify, relative 
       to the given DN in the thread bind operation</property>
      <property name="Modification test" required="Yes">The attribute-value-opCode triples. The opCode can be any 
      valid LDAP operationCode (add, delete/remove or replace). If you don't specify a value with a delete operation,
      all values of the given attribute will be deleted. If you do specify a value in a delete operation, only 
      the given value will be deleted. If this value is non-existent, the sampler will fail the test.</property>
      </properties>
        
  <br />       
       <li><b>Compare</b></li>
        <p>This is the LDAP "compare" operation. It can be used to compare the value 
        of a given attribute with some already known value. In reality this is mostly 
        used to check whether a given person is a member of some group. In such a case
         you can compare the DN of the user as a given value, with the values in the
          attribute "member" of an object of the type groupOfNames.
          If the compare operation fails, this test fails with errorcode 49.</p>
     <properties>
      <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
       <property name="Entry DN" required="Yes">The current distinguished name of the object of 
       which you want  to compare an attribute, relative to the given DN in the thread bind operation.</property>
      <property name="Compare filter" required="Yes">In the form "attribute=value"</property>
      </properties>
     </ol>
        
     <links>
       <link href="build-ldapext-test-plan.html">Building an LDAP Test Plan</link>
       <complink name="LDAP Extended Request Defaults"/>
     </links>
 
 </component>
 
 
 
 
 <component name="Access Log Sampler" index="&sect-num;.1.9"  width="582" height="301" screenshot="accesslogsampler.png">
 <center><h2>(Alpha Code)</h2></center>
 <description><p>AccessLogSampler was designed to read access logs and generate http requests.
 For those not familiar with the access log, it is the log the webserver maintains of every
 request it accepted. This means the every image and html file. The current implementation
 is complete, but some features have not been enabled. There is a filter for the access
 log parser, but I haven't figured out how to link to the pre-processor. Once I do, changes
 to the sampler will be made to enable that functionality.</p>
 <p>Tomcat uses the common format for access logs. This means any webserver that uses the
 common log format can use the AccessLogSampler. Server that use common log format include:
 Tomcat, Resin, Weblogic, and SunOne. Common log format looks
 like this:</p>
 <p>127.0.0.1 - - [21/Oct/2003:05:37:21 -0500] "GET /index.jsp?%2Findex.jsp= HTTP/1.1" 200 8343</p>
 <p>The current implemenation of the parser only looks at the text within the quotes.
 Everything else is stripped out and igored. For example, the response code is completely
 ignored by the parser. For the future, it might be nice to filter out entries that
 do not have a response code of 200. Extending the sampler should be fairly simple. There
 are two interfaces you have to implement.</p>
 <p>org.apache.jmeter.protocol.http.util.accesslog.LogParser</p>
 <p>org.apache.jmeter.protocol.http.util.accesslog.Generator</p>
 <p>The current implementation of AccessLogSampler uses the generator to create a new
 HTTPSampler. The servername, port and get images are set by AccessLogSampler. Next,
 the parser is called with integer 1, telling it to parse one entry. After that,
 HTTPSampler.sample() is called to make the request.
 <code>
 <pre>
             samp = (HTTPSampler) GENERATOR.generateRequest();
             samp.setDomain(this.getDomain());
             samp.setPort(this.getPort());
             samp.setImageParser(this.isImageParser());
             PARSER.parse(1);
             res = samp.sample();
             res.setSampleLabel(samp.toString());
 </pre>
 </code>
 The required methods in LogParser are: setGenerator(Generator) and parse(int).
 Classes implementing Generator interface should provide concrete implementation
 for all the methods. For an example of how to implement either interface, refer to
 StandardGenerator and TCLogParser.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Log parser class" required="Yes (default provided)">The log parser class is responsible for parsing the logs.</property>
         <property name="Filter" required="No">The filter class is used to filter out certain lines.</property>
         <property name="Location of log file" required="Yes">The location of the access log file.</property>
 </properties>
 <p>
 The TCLogParser processes the access log independently for each thread.
 The SharedTCLogParser and OrderPreservingLogParser share access to the file, 
 i.e. each thread gets the next entry in the log.
 </p>
 <p>
 The SessionFilter is intended to handle Cookies across threads. 
 It does not filter out any entries, but modifies the cookie manager so that the cookies for a given IP are
 processed by a single thread at a time. If two threads try to process samples from the same client IP address,
 then one will be forced to wait until the other has completed.
 </p>
 <p>
 The LogFilter is intended to allow access log entries to be filtered by filename and regex,
 as well as allowing for the replacement of file extensions. However, it is not currently possible
 to configure this via the GUI, so it cannot really be used.
 </p>
 </component>
 
 <component name="BeanShell Sampler" index="&sect-num;.1.10"  width="592" height="303" screenshot="beanshellsampler.png">
 	<description><p>This sampler allows you to write a sampler using the BeanShell scripting language.		
 </p><p>
 <b>For full details on using BeanShell, please see the BeanShell web-site at http://www.beanshell.org/.</b>
 </p>
 <p>
 The test element supports the ThreadListener and TestListener methods.
 These should be defined in the initialisation file.
 See the file BeanShellListeners.bshrc for example definitions.
 </p>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.
     The name is stored in the script variable Label</property>
     <property name="Reset bsh.Interpreter before each call" required="Yes">
     If this option is selected, then the interpreter will be recreated for each sample.
     This may be necessary for some long running scripts. 
     For further information, see <a href="best-practices#bsh_scripting">Best Practices - BeanShell scripting</a>.
     </property>
     <property name="Parameters" required="No">Parameters to pass to the BeanShell script.
     This is intended for use with script files; for scripts defined in the GUI, you can use whatever
     variable and function references you need within the script itself.
 	The parameters are stored in the following variables:
 	<ul>
 		<li>Parameters - string containing the parameters as a single variable</li>
 	    <li>bsh.args - String array containing parameters, split on white-space</li>
 	</ul></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable FileName</property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. 
     The return value (if not null) is stored as the sampler result.</property>
 </properties>
 <p>
 N.B. Each Sampler instance has its own BeanShell interpeter,
 and Samplers are only called from a single thread
 </p><p>
 If the property "beanshell.sampler.init" is defined, it is passed to the Interpreter
 as the name of a sourced file.
 This can be used to define common methods and variables. 
 There is a sample init file in the bin directory: BeanShellSampler.bshrc.
 </p><p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 		<p>Before invoking the script, some variables are set up in the BeanShell interpreter:
 			</p>
 				<p>The contents of the Parameters field is put into the variable "Parameters".
 			The string is also split into separate tokens using a single space as the separator, and the resulting list
 			is stored in the String array bsh.args.</p>
 			<p>The full list of BeanShell variables that is set up is as follows:</p>
 		<ul>
 		<li>log - the Logger</li>
 		<li>Label - the Sampler label</li>
 		<li>FileName - the file name, if any</li>
 		<li>Parameters - text from the Parameters field</li>
 		<li>bsh.args - the parameters, split as described above</li>
 		<li>SampleResult - pointer to the current SampleResult</li>
 			<li>ResponseCode = 200</li>
 			<li>ResponseMessage = "OK"</li>
 			<li>IsSuccess = true</li>
 			<li>ctx - JMeterContext</li>
 			<li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
             <li>props - JMeterProperties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 		</ul>
 		<p>When the script completes, control is returned to the Sampler, and it copies the contents
 			of the following script variables into the corresponding variables in the SampleResult:</p>
 			<ul>
 			<li>ResponseCode - for example 200</li>
 			<li>ResponseMessage - for example "OK"</li>
 			<li>IsSuccess - true/false</li>
 			</ul>
 			<p>The SampleResult ResponseData is set from the return value of the script.
 			Since version 2.1.2, if the script returns null, it can set the response directly, by using the method 
 			SampleResult.setResponseData(data), where data is either a String or a byte array.
 			The data type defaults to "text", but can be set to binary by using the method
 			SampleResult.setDataType(SampleResult.BINARY).
 			</p>
 			<p>The SampleResult variable gives the script full access to all the fields and
 				methods in the SampleResult. For example, the script has access to the methods
 				setStopThread(boolean) and setStopTest(boolean).
 				
 				Here is a simple (not very useful!) example script:</p>
 				
 <pre>
 if (bsh.args[0].equalsIgnoreCase("StopThread")) {
     log.info("Stop Thread detected!");
     SampleResult.setStopThread(true);
 }
 return "Data from sample with Label "+Label;
 //or, since version 2.1.2
 SampleResult.setResponseData("My data");
 return null;
 </pre>
 <p>Another example:<br></br> ensure that the property <b>beanshell.sampler.init=BeanShellSampler.bshrc</b> is defined in jmeter.properties. 
 The following script will show the values of all the variables in the ResponseData field:
 </p>
 <pre>
 return getVariables();
 </pre>
 <p>
 For details on the methods available for the various classes (JMeterVariables, SampleResult etc) please check the Javadoc or the source code.
 Beware however that misuse of any methods can cause subtle faults that may be difficult to find ...
 </p>
 </component>
 
 
 <component name="BSF Sampler" index="&sect-num;.1.11"  width="622" height="267" screenshot="bsfsampler.png">
 	<description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
 		See the <a href="http://jakarta.apache.org/bsf/index.html">Apache Bean Scripting Framework</a>
 		website for details of the languages supported.
 		You may need to download the appropriate jars for the language; they should be put in the JMeter <b>lib</b> directory.
 		</p>
 		<p>By default, JMeter supports the following languages:</p>
 		<ul>
 		<li>javascript</li>
         <li>jexl (JMeter version 2.3.2 and later)</li>
         <li>xslt</li>
 		</ul>
 	</description>
 <properties>
 	<property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 	<property name="Scripting Language" required="Yes">Name of the BSF scripting language to be used.
 	N.B. Not all the languages in the drop-down list are supported by default.
 	The following are supported: jexl, javascript, xslt.
 	Others may be available if the appropriate jar is installed in the JMeter lib directory.
 	</property>
 	<property name="Script File" required="No">Name of a file to be used as a BSF script</property>
 	<property name="Parameters" required="No">List of parameters to be passed to the script file or the script.</property>
 	<property name="Script" required="Yes (unless script file is provided)">Script to be passed to BSF language</property>
 </properties>
 <p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
 <ul>
 <li>log - the Logger</li>
 <li>Label - the Sampler label</li>
 <li>FileName - the file name, if any</li>
 <li>Parameters - text from the Parameters field</li>
 <li>args - the parameters, split as described above</li>
 <li>SampleResult - pointer to the current SampleResult</li>
 <li>sampler - pointer to current Sampler</li>
 <li>ctx - JMeterContext</li>
 <li>vars - JMeterVariables  - e.g. vars.get("VAR1"); vars.put("VAR2","value"); vars.remove("VAR3"); vars.putObject("OBJ1",new Object());</li>
 <li>props - JMeterProperties - e.g. props.get("START.HMS"); props.put("PROP1","1234");</li>
 <li>OUT - System.out - e.g. OUT.println("message")</li>
 </ul>
 <p>
 The SampleResult ResponseData is set from the return value of the script.
 If the script returns null, it can set the response directly, by using the method 
 SampleResult.setResponseData(data), where data is either a String or a byte array.
 The data type defaults to "text", but can be set to binary by using the method
 SampleResult.setDataType(SampleResult.BINARY).
 </p>
 <p>
 The SampleResult variable gives the script full access to all the fields and
 methods in the SampleResult. For example, the script has access to the methods
 setStopThread(boolean) and setStopTest(boolean).
 </p>
 <p>
 Unlike the Beanshell Sampler, the BSF Sampler does not set the ResponseCode, ResponseMessage and sample status via script variables.
 Currently the only way to changes these is via the SampleResult methods:
 <ul>
 <li>SampleResult.setSuccessful(true/false)</li>
 <li>SampleResult.setResponseCode("code")</li>
 <li>SampleResult.setResponseMessage("message")</li>
 </ul>
 </p>
 </component>
 
 <component name="JSR223 Sampler" index="&sect-num;.1.11.1">
 <description>
 <p>
 The JSR223 Sampler allows JSR223 script code to be used to perform a sample.
 For details, see <complink name="BSF Sampler"/>.
 </p>
 </description>
 </component>
 
 <component name="TCP Sampler" index="&sect-num;.1.12"  width="477" height="343" screenshot="tcpsampler.png">
 	<description>
 		<p>
 		The TCP Sampler opens a TCP/IP connection to the specified server.
 		It then sends the text, and waits for a response.
 		<br></br>
 		If "Re-use connection" is selected, connections are shared between Samplers in the same thread,
 		provided that the exact same host name string and port are used. 
 		Different hosts/port combinations will use different connections, as will different threads. 
 		<br></br>
 		If an error is detected - or "Re-use connection" is not selected - the socket is closed. 
 		Another socket will be reopened on the next sample.
 		<br></br>
 		The following properties can be used to control its operation:
 		</p>
 		<ul>
 			<li>tcp.status.prefix - text that precedes a status number</li>
 			<li>tcp.status.suffix - text that follows a status number</li>
 			<li>tcp.status.properties - name of property file to convert status codes to messages</li>
 			<li>tcp.handler - Name of TCP Handler class (default TCPClientImpl) - only used if not specified on the GUI</li>
 		</ul>
 		The class that handles the connection is defined by the GUI, failing that the property tcp.handler. 
 		If not found, the class is then searched for in the package org.apache.jmeter.protocol.tcp.sampler.
 		<p>
 		Users can provide their own implementation.
 		The class must extend org.apache.jmeter.protocol.tcp.sampler.TCPClient.
 		</p>
 		<p>
 		The following implementations are currently provided.
 		<ul>
 		<li>TCPClientImpl</li>
         <li>BinaryTCPClientImpl</li>
         <li>LengthPrefixedBinaryTCPClientImpl</li>
 		</ul>
 		The implementations behave as follows:
 		</p>
 		<p><b>TCPClientImpl</b><br></br>
 		This implementation is fairly basic.
         When reading the response, it reads until the end of line byte, if this is defined
         by setting the property <b>tcp.eolByte</b>, otherwise until the end of the input stream.
         </p>
         <p><b>BinaryTCPClientImpl</b><br></br>
         This implementation converts the GUI input, which must be a hex-encoded string, into binary,
         and performs the reverse when reading the response.
         When reading the response, it reads until the end of message byte, if this is defined
         by setting the property <b>tcp.BinaryTCPClient.eomByte</b>, otherwise until the end of the input stream.
         </p>
         <p><b>LengthPrefixedBinaryTCPClientImpl</b><br></br>
         This implementation extends BinaryTCPClientImpl by prefixing the binary message data with a binary length byte.
         The length prefix defaults to 2 bytes.
         This can be changed by setting the property <b>tcp.binarylength.prefix.length</b>.
         </p>
         <p><b>Timeout handling</b>
         If the timeout is set, the read will be terminated when this expires. 
         So if you are using an eolByte/eomByte, make sure the timeout is sufficiently long,
         otherwise the read will be terminated early.    
 		</p>
 		<p><b>Response handling</b>
 		<br></br>
 		If tcp.status.prefix is defined, then the response message is searched for the text following
 		that up to the suffix. If any such text is found, it is used to set the response code.
 		The response message is then fetched from the properties file (if provided).
 		<br></br>
 		For example, if the prefix = "[" and the suffix = "]", then the following repsonse:
 		<br></br>
 		[J28] XI123,23,GBP,CR
 		<br></br>
 		would have the response code J28.
 		<br></br>
 		Response codes in the range "400"-"499" and "500"-"599" are currently regarded as failures;
 		all others are successful. [This needs to be made configurable!]
 		</p>
 <note>The login name/password are not used by the supplied TCP implementations.</note>
 		<br></br>
 		Sockets are disconnected at the end of a test run.
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="TCPClient classname" required="No">Name of the TCPClient class. Defaults to the property tcp.handler, failing that TCPClientImpl.</property>
   <property name="ServerName or IP" required="Yes">Name or IP of TCP server</property>
   <property name="Port Number" required="Yes">Port to be used</property>
   <property name="Re-use connection" required="Yes">If selected, the connection is kept open. Otherwise it is closed when the data has been read.</property>
   <property name="Timeout (milliseconds)" required="No">Timeout for replies.</property>
   <property name="Set Nodelay" required="Yes">See java.net.Socket.setTcpNoDelay().
   If selected, this will disable Nagle's algorithm, otherwise Nagle's algorithm will be used.</property>
   <property name="Text to Send" required="Yes">Text to be sent</property>
   <property name="Login User" required="No">User Name - not used by default implementation</property>
   <property name="Password" required="No">Password - not used by default implementation</property>
 </properties>
 </component>
 
 <component name="JMS Publisher" index="&sect-num;.1.13" screenshot="jmspublisher.png">
 <note>BETA CODE - the code is still subject to change</note>
 	<description>
 		<p>
 		JMS Publisher will publish messages to a given destination (topic/queue). For those not
 		familiar with JMS, it is the J2EE specification for messaging. There are
 		numerous JMS servers on the market and several open source options.
 		</p>
 		<br></br>
 <note>JMeter does not include any JMS implementation jar; this must be downloaded from the JMS provider and put in the lib directory</note>
 	</description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="use JNDI properties file" required="Yes">use jndi.properties. 
   Note that the file must be on the classpath - e.g. by updating the user.classpath JMeter property.
   If this option is not selected, JMeter uses the "JNDI Initial Context Factory" and "Provider URL" fields
   to create the connection.
   </property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="Yes, unless using jndi.properties">The URL for the jms provider</property>
   <property name="Destination" required="Yes">The message destination (topic or queue name)</property>
   <property name="Setup" required="Yes">The destination setup type. With At startup, the destination name is static (i.e. always same name during the test), with Each sample, the destination name is dynamic and is evaluate at each sample (i.e. the destination name may be a variable)</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password</property>
   <property name="Number of samples to aggregate" required="Yes">Number of samples to aggregate</property>
   <property name="Message source" required="Yes">Where to obtain the message</property>
   <property name="Message type" required="Yes">Text, Map or Object message</property>
 </properties>
 <p>
 For the MapMessage type, JMeter reads the source as lines of text.
 Each line must have 3 fields, delimited by commas.
 The fields are:
 <ul>
 <li>Name of entry</li>
 <li>Object class name, e.g. "String" (assumes java.lang package if not specified)</li>
 <li>Object string value</li>
 </ul>
 For each entry, JMeter adds an Object with the given name.
 The value is derived by creating an instance of the class, and using the valueOf(String) method to convert the value if necessary.
 For example:
 <pre>
 name,String,Example
 size,Integer,1234
 </pre>
 This is a very simple implementation; it is not intended to support all possible object types.
 </p>
 <p>
 <b>Note: </b> the Object message type is not implemented yet.
 </p>
 <p>
 The following table shows some values which may be useful when configuring JMS:
 <table>
 <tr>
 <!-- Anakia does not like th cell without any text -->
 <th>Apache <a href="http://activemq.apache.org/">ActiveMQ</a></th>
 <th>Value(s)</th>
 <th>Comment</th>
 </tr>
 <tr><td>Context Factory</td><td>org.apache.activemq.jndi.ActiveMQInitialContextFactory</td><td>.</td></tr>
 <tr><td>Provider URL</td><td>vm://localhost</td><td></td></tr>
 <tr><td>Provider URL</td><td>vm:(broker:(vm://localhost)?persistent=false)</td><td>Disable persistence</td></tr>
 <tr><td>Queue Reference</td><td>dynamicQueues/QUEUENAME</td>
 <td><a href="http://activemq.apache.org/jndi-support.html#JNDISupport-Dynamicallycreatingdestinations">Dynamically define</a> the QUEUENAME to JNDI</td></tr>
 <tr><td>Topic Reference</td><td>dynamicTopics/TOPICNAME</td>
 <td><a href="http://activemq.apache.org/jndi-support.html#JNDISupport-Dynamicallycreatingdestinations">Dynamically define</a> the TOPICNAME to JNDI</td></tr>
 </table>
 </p>
 </component>
 
 <component name="JMS Subscriber" index="&sect-num;.1.14"  screenshot="jmssubscriber.png">
 <note>BETA CODE - the code is still subject to change</note>
 	<description>
