diff --git a/src/core/org/apache/jmeter/resources/messages.properties b/src/core/org/apache/jmeter/resources/messages.properties
index bc5492e47..922e5a7d3 100644
--- a/src/core/org/apache/jmeter/resources/messages.properties
+++ b/src/core/org/apache/jmeter/resources/messages.properties
@@ -1,1347 +1,1349 @@
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
 active_threads_tooltip=Running threads
 add=Add
 add_as_child=Add as Child
 add_from_clipboard=Add from Clipboard
 add_from_suggested_excludes=Add suggested Excludes
 add_parameter=Add Variable
 add_pattern=Add Pattern\:
 add_test=Add Test
 add_user=Add User
 add_value=Add Value
 addtest=Add test
 aggregate_graph=Statistical Graphs
 aggregate_graph_choose_color=Choose color
 aggregate_graph_choose_foreground_color=Foreground color
 aggregate_graph_color_bar=Color\:
 aggregate_graph_column=Column\:
 aggregate_graph_column_selection=Column label selection\:
 aggregate_graph_column_settings=Column settings
 aggregate_graph_columns_to_display=Columns to display\:
 aggregate_graph_dimension=Graph size
 aggregate_graph_display=Display Graph
 aggregate_graph_draw_outlines=Draw outlines bar?
 aggregate_graph_dynamic_size=Dynamic graph size
 aggregate_graph_font=Font\:
 aggregate_graph_height=Height\:
 aggregate_graph_increment_scale=Increment scale\:
 aggregate_graph_legend=Legend
 aggregate_graph_legend.placement.bottom=Bottom
 aggregate_graph_legend.placement.left=Left
 aggregate_graph_legend.placement.right=Right
 aggregate_graph_legend.placement.top=Top
 aggregate_graph_legend_placement=Placement\:
 aggregate_graph_max_length_xaxis_label=Max length of x-axis label\:
 aggregate_graph_ms=Milliseconds
 aggregate_graph_no_values_to_graph=No values to graph
 aggregate_graph_number_grouping=Show number grouping?
 aggregate_graph_response_time=Response Time
 aggregate_graph_save=Save Graph
 aggregate_graph_save_table=Save Table Data
 aggregate_graph_save_table_header=Save Table Header
 aggregate_graph_size=Size\:
 aggregate_graph_style=Style\:
 aggregate_graph_sync_with_name=Synchronize with name
 aggregate_graph_tab_graph=Graph
 aggregate_graph_tab_settings=Settings
 aggregate_graph_title=Aggregate Graph
 aggregate_graph_title_group=Title
 aggregate_graph_use_group_name=Include group name in label?
 aggregate_graph_user_title=Graph title\:
 aggregate_graph_value_font=Value font\:
 aggregate_graph_value_labels_vertical=Value labels vertical?
 aggregate_graph_width=Width\:
 aggregate_graph_xaxis_group=X Axis
 aggregate_graph_yaxis_group=Y Axis (milli-seconds)
 aggregate_graph_yaxis_max_value=Scale maximum value\:
 aggregate_report=Aggregate Report
 aggregate_report_xx_pct1_line={0}% Line
 aggregate_report_xx_pct2_line={0}% Line
 aggregate_report_xx_pct3_line={0}% Line
 aggregate_report_90=90%
 aggregate_report_bandwidth=Received KB/sec
 aggregate_report_sent_bytes_per_sec=Sent KB/sec
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
 arguments_panel_title=Command parameters
 assertion_assume_success=Ignore Status
 assertion_body_resp=Response Body
 assertion_code_resp=Response Code
 assertion_contains=Contains
 assertion_equals=Equals
 assertion_headers=Response Headers
 assertion_matches=Matches
 assertion_message_resp=Response Message
 assertion_network_size=Full Response
 assertion_not=Not
 assertion_or=Or
 assertion_pattern_match_rules=Pattern Matching Rules
 assertion_patterns_to_test=Patterns to Test
 assertion_regex_empty_default_value=Use empty default value
 assertion_resp_field=Response Field to Test
 assertion_resp_size_field=Response Size Field to Test
 assertion_substring=Substring
 assertion_text_document=Document (text)
 assertion_text_resp=Text Response
 assertion_textarea_label=Assertions\:
 assertion_title=Response Assertion
 assertion_url_samp=URL Sampled
 assertion_visualizer_title=Assertion Results
 attribute=Attribute
 attribute_field=Attribute\:
 attrs=Attributes
 auth_base_url=Base URL
 auth_manager_clear_per_iter=Clear auth on each iteration?
 auth_manager_options=Options
 auth_manager_title=HTTP Authorization Manager
 auths_stored=Authorizations Stored in the Authorization Manager
 average=Average
 average_bytes=Avg. Bytes
 backend_listener=Backend Listener
 backend_listener_classname=Backend Listener implementation
 backend_listener_paramtable=Parameters
 backend_listener_queue_size=Async Queue size
 bind=Thread Bind
 bouncy_castle_unavailable_message=The jars for bouncy castle are unavailable, please add them to your classpath.
 browse=Browse...
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
 cache_manager_size=Max Number of elements in cache
 cache_manager_title=HTTP Cache Manager
 cache_session_id=Cache Session Id?
 cancel=Cancel
 cancel_exit_to_save=There are test items that have not been saved.  Do you wish to save before exiting?
 cancel_new_from_template=There are test items that have not been saved.  Do you wish to save before creating a test plan from selected template?
 cancel_new_to_save=There are test items that have not been saved.  Do you wish to save before clearing the test plan?
 cancel_revert_project=There are test items that have not been saved.  Do you wish to revert to the previously saved test plan?
 change_parent=Change Controller
 char_value=Unicode character number (decimal or 0xhex)
 check_return_code_title=Check Return Code
 choose_function=Choose a function
 choose_language=Choose Language
 clear=Clear
 clear_all=Clear All
 clear_cache_each_iteration=Clear cache each iteration
 clear_cache_per_iter=Clear cache each iteration?
 clear_cookies_per_iter=Clear cookies each iteration?
 clipboard_node_read_error=An error occurred while copying node
 close=Close
 closeconnection=Close connection
 collapse_tooltip=Click to open / collapse
 column_delete_disallowed=Deleting this column is not permitted
 column_number=Column number of CSV file | next | *alias
 command_config_box_title=Command to Execute
 command_config_std_streams_title=Standard streams (files)
 command_field_title=Command:
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
 confirm=Confirm
 constant_throughput_timer_memo=Add a delay between sampling to attain constant throughput
 constant_timer_delay=Thread Delay (in milliseconds)\:
 constant_timer_memo=Add a constant delay between sampling
 constant_timer_title=Constant Timer
 content_encoding=Content encoding\:
 controller=Controller
 cookie_implementation_choose=Implementation:
 cookie_manager_policy=Cookie Policy:
 cookie_manager_title=HTTP Cookie Manager
 cookie_options=Options
 cookies_stored=User-Defined Cookies
 copy=Copy
 counter_config_title=Counter
 counter_per_user=Track counter independently for each user
 counter_reset_per_tg_iteration=Reset counter on each Thread Group Iteration
 countlim=Size limit
 critical_section_controller_label=Lock name
 critical_section_controller_title=Critical Section Controller
 cssjquery_attribute=Attribute\:
 cssjquery_empty_default_value=Use empty default value
 cssjquery_tester_error=An error occured evaluating expression:{0}, error:{1}
 cssjquery_impl=CSS/JQuery implementation\:
 cssjquery_render_no_text=Data response result isn't text.
 cssjquery_tester_button_test=Test
 cssjquery_tester_field=Selector\:
 cssjquery_tester_title=CSS/JQuery Tester
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
 delayed_start=Delay Thread creation until needed
 delete=Delete
 delete_parameter=Delete Variable
 delete_test=Delete Test
 delete_user=Delete User
 deltest=Deletion test
 deref=Dereference aliases
 description=Description
 detail=Detail
 directory_field_title=Working directory:
 disable=Disable
 distribution_graph_title=Distribution Graph (DEPRECATED)
 distribution_note1=The graph will update every 10 samples
 dn=DN
 dns_cache_manager_title=DNS Cache Manager
 dns_hostname_or_ip=Hostname or IP address
 dns_servers=DNS Servers
 domain=Domain
 done=Done
 down=Down
 duplicate=Duplicate
 duration=Duration (seconds)
 duration_assertion_duration_test=Duration to Assert
 duration_assertion_failure=The operation lasted too long\: It took {0} milliseconds, but should not have lasted longer than {1} milliseconds.
 duration_assertion_input_error=Please enter a valid positive integer.
 duration_assertion_label=Duration in milliseconds\:
 duration_assertion_title=Duration Assertion
 duration_tooltip=Elapsed time of current running Test
 edit=Edit
 email_results_title=Email Results
 en=English
 enable=Enable
 encode=URL Encode
 encode?=Encode?
 encoded_value=URL Encoded Value
 endtime=End Time  
 entry_dn=Entry DN
 entrydn=Entry DN
 environment_panel_title=Environment Variables
 eolbyte=End of line(EOL) byte value: 
 error_indicator_tooltip=Show the number of errors in log, click to open Log Viewer panel
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
 find_target_element=Find target element
 expected_return_code_title=Expected Return Code: 
 expiration=Expiration
 expression_field=CSS/JQuery expression\:
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
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Bold
 fontstyle.italic=Italic
 fontstyle.normal=Normal
 foreach_controller_title=ForEach Controller
 foreach_end_index=End index for loop (inclusive)
 foreach_input=Input variable prefix
 foreach_output=Output variable name
 foreach_start_index=Start index for loop (exclusive)
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
 graph_apply_filter=Apply filter
 graph_choose_graphs=Graphs to Display
 graph_full_results_title=Graph Full Results
 graph_pointshape_circle=Circle
 graph_pointshape_diamond=Diamond
 graph_pointshape_none=None
 graph_pointshape_square=Square
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms):
 graph_resp_time_interval_reload=Apply interval
 graph_resp_time_not_enough_data=Unable to graph, not enough data
 graph_resp_time_series_selection=Sampler label selection:
 graph_resp_time_settings_line=Line settings
 graph_resp_time_settings_pane=Graph settings
 graph_resp_time_shape_label=Shape point:
 graph_resp_time_stroke_width=Stroke width:
 graph_resp_time_title=Response Time Graph
 graph_resp_time_title_label=Graph title:
 graph_resp_time_xaxis_time_format=Time format (SimpleDateFormat):
 graph_results_average=Average
 graph_results_data=Data
 graph_results_deviation=Deviation
 graph_results_latest_sample=Latest Sample
 graph_results_median=Median
 graph_results_ms=ms
 graph_results_no_samples=No of Samples
 graph_results_throughput=Throughput
 graph_results_title=Graph Results
 groovy_function_expression=Expression to evaluate
 grouping_add_separators=Add separators between groups
 grouping_in_controllers=Put each group in a new controller
 grouping_in_transaction_controllers=Put each group in a new transaction controller
 grouping_mode=Grouping\:
 grouping_no_groups=Do not group samplers
 grouping_store_first_only=Store 1st sampler of each group only
 header_manager_title=HTTP Header Manager
 headers_stored=Headers Stored in the Header Manager
 heap_dump=Create a heap dump
 help=Help
 help_node=What's this node?
 html_assertion_file=Write JTidy report to file
 html_assertion_label=HTML Assertion
 html_assertion_title=HTML Assertion
 html_extractor_title=CSS/JQuery Extractor
 html_extractor_type=CSS/JQuery Extractor Implementation
 http_implementation=Implementation:
 http_response_code=HTTP response code
 http_url_rewriting_modifier_title=HTTP URL Re-writing Modifier
 http_user_parameter_modifier=HTTP User Parameter Modifier
 httpmirror_max_pool_size=Max number of Threads:
 httpmirror_max_queue_size=Max queue size:
 httpmirror_settings=Settings
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
 interleave_accross_threads=Interleave accross threads
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
 jms_bytes_message=Bytes Message
 jms_client_caption=Receiver client uses MessageConsumer.receive() to listen for message.
 jms_client_caption2=MessageListener uses onMessage(Message) interface to listen for new messages.
 jms_client_id=Client ID
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
 jms_durable_subscription_id=Durable Subscription ID
+jms_error_reconnect_on_codes=Reconnect on error codes (regex)
+jms_error_pause_between=Pause between errors (ms)
 jms_expiration=Expiration (ms)
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
 jms_priority=Priority (0-9)
 jms_properties=JMS Properties
 jms_properties_name=Name
 jms_properties_title=JMS Properties
 jms_properties_type=Class of value
 jms_properties_value=Value
 jms_props=JMS Properties
 jms_provider_url=Provider URL
 jms_publisher=JMS Publisher
 jms_pwd=Password
 jms_queue=Queue
 jms_queue_connection_factory=QueueConnection Factory
 jms_queueing=JMS Resources
 jms_random_file=Path of folder containing random files suffixed with .dat for bytes messages, .txt or .obj for text and Object messages
 jms_receive_queue=JNDI name Receive queue
 jms_request=Request Only
 jms_requestreply=Request Response
 jms_sample_title=JMS Default Request
 jms_selector=JMS Selector
 jms_send_queue=JNDI name Request queue
 jms_separator=Separator
 jms_stop_between_samples=Stop between samples?
 jms_store_response=Store Response
 jms_subscriber_on_message=Use MessageListener.onMessage()
 jms_subscriber_receive=Use MessageConsumer.receive()
 jms_subscriber_title=JMS Subscriber
 jms_testing_title=Messaging Request
 jms_text_area=Text Message or Object Message serialized to XML by XStream
 jms_text_message=Text Message
 jms_timeout=Timeout (ms)
 jms_topic=Destination
 jms_use_auth=Use Authorization?
 jms_use_file=From file
 jms_use_non_persistent_delivery=Use non-persistent delivery mode?
 jms_use_properties_file=Use jndi.properties file
 jms_use_random_file=Random File from folder specified below
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
 jsonpath_renderer=JSON Path Tester
 jsonpath_tester_title=JSON Path Tester
 jsonpath_tester_field=JSON Path Expression
 jsonpath_tester_button_test=Test
 jsonpath_render_no_text=No Text
 json_post_processor_title=JSON Extractor
 jsonpp_variable_names=Variable names
 jsonpp_json_path_expressions=JSON Path expressions
 jsonpp_default_values=Default Values
 jsonpp_match_numbers=Match Numbers
 jsonpp_compute_concat=Compute concatenation var (suffix _ALL)
 jsonpp_error_number_arguments_mismatch_error=Mismatch between number of variables, json expressions and default values
 junit_append_error=Append assertion errors
 junit_append_exception=Append runtime exceptions
 junit_constructor_error=Unable to create an instance of the class
 junit_constructor_string=Constructor String Label
 junit_create_instance_per_sample=Create a new instance per sample
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
 action_check_message=A Test is currently running, stop or shutdown test to execute this command
 action_check_title=Test Running
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
 mail_reader_header_only=Fetch headers only
 mail_reader_num_messages=Number of messages to retrieve:
 mail_reader_password=Password:
 mail_reader_port=Server Port (optional):
 mail_reader_server=Server Host:
 mail_reader_server_type=Protocol (e.g. pop3, imaps):
 mail_reader_storemime=Store the message using MIME (raw)
 mail_reader_title=Mail Reader Sampler
 mail_sent=Mail sent successfully
 mailer_addressees=Addressee(s): 
 mailer_attributes_panel=Mailing attributes
 mailer_connection_security=Connection security: 
 mailer_error=Couldn't send mail. Please correct any misentries.
 mailer_failure_limit=Failure Limit: 
 mailer_failure_subject=Failure Subject: 
 mailer_failures=Failures: 
 mailer_from=From: 
 mailer_host=Host: 
 mailer_login=Login: 
 mailer_msg_title_error=Error
 mailer_msg_title_information=Information
 mailer_password=Password: 
 mailer_port=Port: 
 mailer_string=E-Mail Notification
 mailer_success_limit=Success Limit: 
 mailer_success_subject=Success Subject: 
 mailer_test_mail=Test Mail
 mailer_title_message=Message
 mailer_title_settings=Mailer settings
 mailer_title_smtpserver=SMTP server
 mailer_visualizer_title=Mailer Visualizer
 match_num_field=Match No. (0 for Random)\: 
 max=Maximum
 maximum_param=The maximum value allowed for a range of values
 md5hex_assertion_failure=Error asserting MD5 sum : got {0} but should have been {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex to Assert
 md5hex_assertion_title=MD5Hex Assertion
 mechanism=Mechanism
 menu_assertions=Assertions
 menu_close=Close
 menu_collapse_all=Collapse All
 menu_config_element=Config Element
 menu_edit=Edit
 menu_expand_all=Expand All
 menu_fragments=Test Fragment
 menu_generative_controller=Sampler
 menu_listener=Listener
 menu_logger_panel=Log Viewer 
 menu_logic_controller=Logic Controller
 menu_merge=Merge
 menu_modifiers=Modifiers
 menu_non_test_elements=Non-Test Elements
 menu_open=Open
 menu_post_processors=Post Processors
 menu_pre_processors=Pre Processors
 menu_response_based_modifiers=Response Based Modifiers
 menu_search=Search
 menu_search_reset=Reset Search
 menu_tables=Table
 menu_threads=Threads (Users)
 menu_timer=Timer
 menu_toolbar=Toolbar
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
 name=Name\:
 new=New
 newdn=New distinguished name
 next=Next
 no=Norwegian
 notify_child_listeners_fr=Notify Child Listeners of filtered samplers
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
 poisson_timer_delay=Constant Delay Offset (in milliseconds)\:
 poisson_timer_memo=Adds a random delay with a poisson distribution
 poisson_timer_range=Lambda (in milliseconds)\:
 poisson_timer_title=Poisson Random Timer
 port=Port\:
 post_as_parameters=Parameters
 post_body=Body Data
 post_body_raw=Body Data
 post_files_upload=Files Upload
 post_thread_group_title=tearDown Thread Group
 previous=Previous
 property_as_field_label={0}\:
 property_default_param=Default value
 property_edit=Edit
 property_editor.value_is_invalid_message=The text you just entered is not a valid value for this property.\nThe property will be reverted to its previous value.
 property_editor.value_is_invalid_title=Invalid input
 property_name_param=Name of property
 property_returnvalue_param=Return Original Value of property (default false) ?
 property_tool_tip=<html>{0}</html>
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
 proxy_cl_wrong_target_cl=Target Controller is configured to "Use Recording Controller" but no such controller exists, \nensure you add a Recording Controller as child of Thread Group node to start recording correctly
 proxy_content_type_exclude=Exclude\:
 proxy_content_type_filter=Content-type filter
 proxy_content_type_include=Include\:
 proxy_daemon_bind_error=Could not create script recorder - port in use. Choose another port.
 proxy_daemon_error=Could not create script recorder - see log for details
 proxy_daemon_error_from_clipboard=from clipboard
 proxy_daemon_error_not_retrieve=Could not add retrieve
 proxy_daemon_error_read_args=Could not add read arguments from clipboard\:
 proxy_daemon_msg_check_details=Please check the details below when installing the certificate in the browser
 proxy_daemon_msg_created_in_bin=created in JMeter bin directory
 proxy_daemon_msg_install_as_in_doc=You can install it following instructions in Component Reference documentation (see Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Root CA certificate\:
 proxy_domains=HTTPS Domains \:
 proxy_domains_dynamic_mode_tooltip=List of domain names for HTTPS url, ex. jmeter.apache.org or wildcard domain like *.apache.org. Use comma as separator. 
 proxy_domains_dynamic_mode_tooltip_java6=To activate this field, use a Java 7+ runtime environment
 proxy_general_settings=Global Settings
 proxy_headers=Capture HTTP Headers
 proxy_prefix_http_sampler_name=Prefix\:
 proxy_regex=Regex matching
 proxy_sampler_settings=HTTP Sampler settings
 proxy_sampler_type=Type\:
 proxy_separators=Add Separators
 proxy_settings_port_error_digits=Only digits allowed
 proxy_settings_port_error_invalid_data=Invalid data
 proxy_target=Target Controller\:
 proxy_test_plan_content=Test plan content
 proxy_title=HTTP(S) Test Script Recorder
 pt_br=Portugese (Brazilian)
 ramp_up=Ramp-Up Period (in seconds)\:
 random_control_title=Random Controller
 random_order_control_title=Random Order Controller
 random_multi_result_source_variable=Source Variable(s) (use | as separator)
 random_multi_result_target_variable=Target Variable
 random_string_chars_to_use=Chars to use for random string generation
 random_string_length=Random string length
 realm=Realm
 record_controller_clear_samples=Clear all the recorded samples
 record_controller_title=Recording Controller
 redo=Redo
 ref_name_field=Reference Name\:
 regex_extractor_title=Regular Expression Extractor
 regex_field=Regular Expression\:
 regex_params_names_field=Parameter names regexp group number
 regex_params_ref_name_field=Regular Expression Reference Name
 regex_params_title=RegEx User Parameters
 regex_params_values_field=Parameter values regex group number
 regex_source=Field to check
 regex_src_body=Body
 regex_src_body_as_document=Body as a Document
 regex_src_body_unescaped=Body (unescaped)
 regex_src_hdrs=Response Headers
 regex_src_hdrs_req=Request Headers
 regex_src_url=URL
 regexfunc_param_1=Regular expression used to search previous sample - or variable.
 regexfunc_param_2=Template for the replacement string, using groups from the regular expression.  Format is $[group]$.  Example $1$.
 regexfunc_param_3=Which match to use.  An integer 1 or greater, RAND to indicate JMeter should randomly choose, A float, or ALL indicating all matches should be used ([1])
 regexfunc_param_4=Between text.  If ALL is selected, the between text will be used to generate the results ([""])
 regexfunc_param_5=Default text.  Used instead of the template if the regular expression finds no matches ([""])
 regexfunc_param_7=Input variable name containing the text to be parsed ([previous sample])
 regexp_render_no_text=Data response result isn't text.
 regexp_tester_button_test=Test
 regexp_tester_field=Regular expression\:
 regexp_tester_title=RegExp Tester
 remote_error_init=Error initialising remote server
 remote_error_starting=Error starting remote server
 remote_exit=Remote Exit
 remote_exit_all=Remote Exit All
 remote_shut=Remote Shutdown
 remote_shut_all=Remote Shutdown All
 remote_start=Remote Start
 remote_start_all=Remote Start All
 remote_stop=Remote Stop
 remote_stop_all=Remote Stop All
 remove=Remove
 remove_confirm_msg=Are you sure you want remove the selected element(s)?
 remove_confirm_title=Confirm remove?
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
 reportgenerator_top5_error_count=#Errors
 reportgenerator_top5_error_label=Error
 reportgenerator_top5_label=Sample
 reportgenerator_top5_sample_count=#Samples
 reportgenerator_top5_total=Total
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Label
 reportgenerator_summary_apdex_satisfied=T (Toleration threshold)  
 reportgenerator_summary_apdex_tolerated=F (Frustration threshold)
 reportgenerator_summary_errors_count=Number of errors
 reportgenerator_summary_errors_rate_all=% in all samples
 reportgenerator_summary_errors_rate_error=% in errors
 reportgenerator_summary_errors_type=Type of error
 reportgenerator_summary_statistics_count=#Samples
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=Error %
 reportgenerator_summary_statistics_kbytes=Received KB/sec
 reportgenerator_summary_statistics_sent_kbytes=Sent KB/sec
 reportgenerator_summary_statistics_label=Label
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Average response time
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%dth pct
 reportgenerator_summary_statistics_throughput=Throughput
 reportgenerator_summary_total=Total
 request_data=Request Data
 reset=Reset
 reset_gui=Reset Gui
 response_save_as_md5=Save response as MD5 hash?
 response_time_distribution_satisfied_label=Requests having \\nresponse time <= {0}ms
 response_time_distribution_tolerated_label= Requests having \\nresponse time > {0}ms and <= {1}ms
 response_time_distribution_untolerated_label=Requests having \\nresponse time > {0}ms
 response_time_distribution_failed_label=Requests in error
 restart=Restart
 resultaction_title=Result Status Action Handler
 resultsaver_addtimestamp=Add timestamp
 resultsaver_errors=Save Failed Responses only
 resultsaver_numberpadlen=Minimum Length of sequence number
 resultsaver_prefix=Filename prefix\:
 resultsaver_skipautonumber=Don't add number to prefix
 resultsaver_skipsuffix=Don't add suffix
 resultsaver_success=Save Successful Responses only
 resultsaver_title=Save Responses to a file
 resultsaver_variable=Variable Name:
 retobj=Return object
 return_code_config_box_title=Return Code Configuration
 reuseconnection=Re-use connection
 revert_project=Revert
 revert_project?=Revert project?
 root=Root
 root_title=Root
 run=Run
 run_threadgroup=Start
 run_threadgroup_no_timers=Start no pauses
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
 sampler_on_error_start_next_loop=Start Next Thread Loop
 sampler_on_error_stop_test=Stop Test
 sampler_on_error_stop_test_now=Stop Test Now
 sampler_on_error_stop_thread=Stop Thread
 sample_timeout_memo=Interrupt the sampler if it times out
 sample_timeout_timeout=Sample timeout (in milliseconds)\:
 sample_timeout_title=Sample Timeout
 save=Save
 save?=Save?
 save_all_as=Save Test Plan as
 save_as=Save Selection As...
 save_as_error=More than one item selected!
 save_as_image=Save Node As Image
 save_as_image_all=Save Screen As Image
 save_as_test_fragment=Save as Test Fragment
 save_as_test_fragment_error=One of the selected nodes cannot be put inside a Test Fragment
 save_assertionresultsfailuremessage=Save Assertion Failure Message
 save_assertions=Save Assertion Results (XML)
 save_asxml=Save As XML
 save_bytes=Save received byte count
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
 save_connecttime=Save Connect Time
 save_message=Save Response Message
 save_overwrite_existing_file=The selected file already exists, do you want to overwrite it?
 save_requestheaders=Save Request Headers (XML)
 save_responsedata=Save Response Data (XML)
 save_responseheaders=Save Response Headers (XML)
 save_samplecount=Save Sample and Error Counts
 save_samplerdata=Save Sampler Data (XML)
 save_sentbytes=Save sent byte count
 save_subresults=Save Sub Results (XML)
 save_success=Save Success
 save_threadcounts=Save Active Thread Counts
 save_threadname=Save Thread Name
 save_time=Save Elapsed Time
 save_timestamp=Save Time Stamp
 save_url=Save URL
 save_workbench=Save WorkBench
 sbind=Single bind/unbind
 scheduler=Scheduler
 scheduler_configuration=Scheduler Configuration
 scope=Scope
 search=Search
 search_base=Search base
 search_expand=Search & Expand
 search_filter=Search Filter
 search_replace_all=Replace All
 search_test=Search Test
 search_text_button_close=Close
 search_text_button_find=Find
 search_text_button_next=Find next
 search_text_chkbox_case=Case sensitive
 search_text_chkbox_regexp=Regular exp.
 search_text_field=Search: 
 search_text_msg_not_found=Text not found
 search_text_replace=Replace by
 search_text_title_not_found=Not found
 search_tree_title=Search Tree
 searchbase=Search base
 searchfilter=Search Filter
 searchtest=Search test
 second=second
 secure=Secure
 send_file=Send Files With the Request\:
 send_file_browse=Browse...
 send_file_filename_label=File Path
 send_file_mime_label=MIME Type
 send_file_param_name_label=Parameter Name
 server=Server Name or IP\:
 servername=Servername \:
 session_argument_name=Session Argument Name
 setup_thread_group_title=setUp Thread Group
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
 smime_assertion_message_position=Execute assertion on message at position
 smime_assertion_not_signed=Message not signed
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
 smtp_enforcestarttls_tooltip=<html><b>Enforces</b> the server to use StartTLS.<br />If not selected and the SMTP-Server doesn't support StartTLS, <br />a normal SMTP-Connection will be used as fallback instead. <br /><i>Please note</i> that this checkbox creates a file in "/tmp/", <br />so this will cause problems under windows.</html>
 smtp_from=Address From:
 smtp_header_add=Add Header
 smtp_header_name=Header Name
 smtp_header_remove=Remove
 smtp_header_value=Header Value
 smtp_mail_settings=Mail settings
 smtp_message=Message:
 smtp_message_settings=Message settings
 smtp_messagesize=Calculate message size
 smtp_password=Password:
 smtp_plainbody=Send plain body (i.e. not multipart/mixed)
 smtp_replyto=Address Reply-To:
 smtp_sampler_title=SMTP Sampler
 smtp_security_settings=Security settings
 smtp_server=Server:
 smtp_server_connection_timeout=Connection timeout:
 smtp_server_port=Port:
 smtp_server_settings=Server settings
 smtp_server_timeout=Read timeout:
 smtp_server_timeouts_settings=Timeouts (milliseconds)
 smtp_subject=Subject:
 smtp_suppresssubj=Suppress Subject Header
 smtp_timestamp=Include timestamp in subject
 smtp_to=Address To:
 smtp_trustall=Trust all certificates
 smtp_trustall_tooltip=<html><b>Enforces</b> JMeter to trust all certificates, whatever CA it comes from.</html>
 smtp_truststore=Local truststore:
 smtp_truststore_tooltip=<html>The pathname of the truststore.<br />Relative paths are resolved against the current directory.<br />Failing that, against the directory containing the test script (JMX file)</html>
 smtp_useauth=Use Auth
 smtp_usenone=Use no security features
 smtp_username=Username:
 smtp_usessl=Use SSL
 smtp_usestarttls=Use StartTLS
 smtp_usetruststore=Use local truststore
 smtp_usetruststore_tooltip=<html>Allows JMeter to use a local truststore.</html>
 soap_action=Soap Action
 soap_data_title=Soap/XML-RPC Data
 soap_sampler_file_invalid=Filename references a missing or unreadable file\:
 soap_sampler_title=SOAP/XML-RPC Request
 soap_send_action=Send SOAPAction: 
 solinger=SO_LINGER:
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
 start_no_timers=Start no pauses
 starttime=Start Time
 stop=Stop
 stopping_test=Shutting down all test threads. You can see number of active threads in the upper right corner of GUI. Please be patient. 
 stopping_test_failed=One or more test threads won't exit; see log file.
 stopping_test_host=Host
 stopping_test_title=Stopping Test
 string_from_file_encoding=File encoding if not the platform default (opt)
 string_from_file_file_name=Enter path (absolute or relative) to file
 string_from_file_seq_final=Final file sequence number (opt)
 string_from_file_seq_start=Start file sequence number (opt)
 summariser_title=Generate Summary Results
 summary_report=Summary Report
 switch_controller_label=Switch Value
 switch_controller_title=Switch Controller
 system_sampler_stderr=Standard error (stderr):
 system_sampler_stdin=Standard input (stdin):
 system_sampler_stdout=Standard output (stdout):
 system_sampler_title=OS Process Sampler
 table_visualizer_bytes=Bytes
 table_visualizer_latency=Latency
 table_visualizer_connect=Connect Time(ms)
 table_visualizer_sample_num=Sample #
 table_visualizer_sample_time=Sample Time(ms)
 table_visualizer_sent_bytes=Sent Bytes
 table_visualizer_start_time=Start Time
 table_visualizer_status=Status
 table_visualizer_success=Success
 table_visualizer_thread_name=Thread Name
 table_visualizer_warning=Warning
 target_server=Target Server
 tcp_classname=TCPClient classname\:
 tcp_config_title=TCP Sampler Config
 tcp_nodelay=Set NoDelay
 tcp_port=Port Number\:
 tcp_request_data=Text to send
 tcp_sample_title=TCP Sampler
 tcp_timeout=Timeout (milliseconds)\:
 teardown_on_shutdown=Run tearDown Thread Groups after shutdown of main threads
 template_choose=Select Template
 template_create_from=Create
 template_field=Template\:
 template_load?=Load template ?
 template_menu=Templates...
 template_merge_from=Merge
 template_reload=Reload templates
 template_title=Templates
 test=Test
 test_action_action=Action
 test_action_duration=Duration (milliseconds)
 test_action_pause=Pause
 test_action_restart_next_loop=Go to next loop iteration
 test_action_stop=Stop
 test_action_stop_now=Stop Now
 test_action_target=Target
 test_action_target_test=All Threads
 test_action_target_thread=Current Thread
 test_action_title=Test Action
 test_configuration=Test Configuration
 test_fragment_title=Test Fragment
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
 thread_properties=Thread Properties
 threadgroup=Thread Group
 throughput_control_bynumber_label=Total Executions
 throughput_control_bypercent_label=Percent Executions
 throughput_control_perthread_label=Per User
 throughput_control_title=Throughput Controller
 throughput_control_tplabel=Throughput
 time_format=Format string for SimpleDateFormat (optional)
 timelim=Time limit
 timeout_config_box_title=Timeout configuration
 timeout_title=Timeout (ms)
 toggle=Toggle
 toolbar_icon_set_not_found=The file description of toolbar icon set is not found. See logs.
 total_threads_tooltip=Total number of threads to run
 tr=Turkish
 transaction_controller_include_timers=Include duration of timer and pre-post processors in generated sample
 transaction_controller_parent=Generate parent sample
 transaction_controller_title=Transaction Controller
 transform_into_variable=Replace values with variables
 unbind=Thread Unbind
 undo=Undo
 unescape_html_string=String to unescape
 unescape_string=String containing Java escapes
 uniform_timer_delay=Constant Delay Offset (in milliseconds)\:
 uniform_timer_memo=Adds a random delay with a uniform distribution
 uniform_timer_range=Random Delay Maximum (in milliseconds)\:
 uniform_timer_title=Uniform Random Timer
 up=Up
 update=Update
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
 urldecode_string=String with URL encoded chars to decode
 urlencode_string=String to encode in URL encoded chars
 use_custom_dns_resolver=Use custom DNS resolver
 use_expires=Use Cache-Control/Expires header when processing GET requests
 use_keepalive=Use KeepAlive
 use_multipart_for_http_post=Use multipart/form-data for POST
 use_multipart_mode_browser=Browser-compatible headers
 use_recording_controller=Use Recording Controller
 use_system_dns_resolver=Use system DNS resolver
 user=User
 user_defined_test=User Defined Test
 user_defined_variables=User Defined Variables
 user_param_mod_help_note=(Do not change this.  Instead, modify the file of that name in JMeter's /bin directory)
 user_parameters_table=Parameters
 user_parameters_title=User Parameters
 userdn=Username
 username=Username
 userpw=Password
 validate_threadgroup=Validate
 value=Value
 value_to_quote_meta=Value to escape from ORO Regexp meta chars
 var_name=Reference Name
 variable_name_param=Name of variable (may include variable and function references)
 view_graph_tree_title=View Graph Tree
 view_results_assertion_error=Assertion error: 
 view_results_assertion_failure=Assertion failure: 
 view_results_assertion_failure_message=Assertion failure message: 
 view_results_autoscroll=Scroll automatically?
 view_results_childsamples=Child samples?
 view_results_datatype=Data type ("text"|"bin"|""): 
 view_results_desc=Shows the text results of sampling in tree form
 view_results_error_count=Error Count: 
 view_results_fields=fields:
 view_results_in_table=View Results in Table
 view_results_latency=Latency: 
 view_results_connect_time=Connect Time: 
 view_results_load_time=Load time: 
 view_results_render=Render:
 view_results_render_browser=Browser
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML (download resources)
 view_results_render_html_formatted=HTML Source Formatted
 view_results_render_json=JSON
 view_results_render_text=Text
 view_results_render_xml=XML
 view_results_request_headers=Request Headers:
 view_results_response_code=Response code: 
 view_results_response_headers=Response headers:
 view_results_response_message=Response message: 
 view_results_response_missing_tika=Missing tika-app.jar in classpath. Unable to convert to plain text this kind of document.\nDownload the tika-app-x.x.jar file from http://tika.apache.org/download.html\nAnd put the file in <JMeter>/lib directory.
 view_results_response_partial_message=Start of message:
 view_results_response_too_large_message=Response too large to be displayed. Size: 
 view_results_sample_count=Sample Count: 
 view_results_sample_start=Sample Start: 
 view_results_search_pane=Search pane
 view_results_sent_bytes=Sent bytes:
 view_results_size_body_in_bytes=Body size in bytes: 
 view_results_size_headers_in_bytes=Headers size in bytes: 
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
 view_results_table_request_params_key=Parameter name
 view_results_table_request_params_value=Value
 view_results_table_request_raw_nodata=No data to display
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Raw
 view_results_table_result_tab_parsed=Parsed
 view_results_table_result_tab_raw=Raw
 view_results_thread_name=Thread Name: 
 view_results_title=View Results
 view_results_tree_title=View Results Tree
 warning=Warning!
 web_cannot_convert_parameters_to_raw=Cannot convert parameters to Body Data \nbecause one of the parameters has a name
 web_cannot_switch_tab=You cannot switch because data cannot be converted\n to target Tab data, empty data to switch
 web_parameters_lost_message=Switching to Body Data will convert the parameters.\nParameter table will be cleared when you select\nanother node or save the test plan.\nOK to proceeed?
 web_proxy_server_title=Proxy Server
 web_request=HTTP Request
 web_server=Web Server
 web_server_client=Client implementation:
 web_server_domain=Server Name or IP\:
 web_server_port=Port Number\:
 web_server_timeout_connect=Connect:
 web_server_timeout_response=Response:
 web_server_timeout_title=Timeouts (milliseconds)
 web_testing2_title=HTTP Request HTTPClient
 web_testing_basic=Basic
 web_testing_advanced=Advanced
 web_testing_concurrent_download=Parallel downloads. Number:
 web_testing_embedded_url_pattern=URLs must match\:
 web_testing_retrieve_images=Retrieve All Embedded Resources
 web_testing_retrieve_title=Embedded Resources from HTML Files
 web_testing_source_ip=Source address
 web_testing_source_ip_device=Device
 web_testing_source_ip_device_ipv4=Device IPv4
 web_testing_source_ip_device_ipv6=Device IPv6
 web_testing_source_ip_hostname=IP/Hostname
 web_testing_title=HTTP Request
 while_controller_label=Condition (function or variable)
 while_controller_title=While Controller
 workbench_title=WorkBench
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
 xpath_tester=XPath Tester
 xpath_tester_button_test=Test
 xpath_tester_field=XPath expression
 xpath_tester_fragment=Return entire XPath fragment instead of text content?
 xpath_tester_no_text=Data response result isn't text.
 xpath_tester_title=XPath Tester
 xpath_tidy_quiet=Quiet
 xpath_tidy_report_errors=Report errors
 xpath_tidy_show_warnings=Show warnings
 you_must_enter_a_valid_number=You must enter a valid number
 zh_cn=Chinese (Simplified)
 zh_tw=Chinese (Traditional)
\ No newline at end of file
diff --git a/src/core/org/apache/jmeter/resources/messages_fr.properties b/src/core/org/apache/jmeter/resources/messages_fr.properties
index 239e20a88..2a0f7b90c 100644
--- a/src/core/org/apache/jmeter/resources/messages_fr.properties
+++ b/src/core/org/apache/jmeter/resources/messages_fr.properties
@@ -1,1337 +1,1339 @@
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
 
 #Stored by I18NEdit, may be edited!
 about=A propos de JMeter
 active_threads_tooltip=Unit\u00E9s actives
 add=Ajouter
 add_as_child=Ajouter en tant qu'enfant
 add_from_clipboard=Ajouter depuis Presse-papier
 add_from_suggested_excludes=Ajouter exclusions propos\u00E9es
 add_parameter=Ajouter un param\u00E8tre
 add_pattern=Ajouter un motif \:
 add_test=Ajout
 add_user=Ajouter un utilisateur
 add_value=Ajouter valeur
 addtest=Ajout
 aggregate_graph=Graphique des statistiques
 aggregate_graph_choose_color=Choisir couleur
 aggregate_graph_choose_foreground_color=Couleur valeur
 aggregate_graph_color_bar=Couleur \:
 aggregate_graph_column=Colonne
 aggregate_graph_column_selection=S\u00E9lection de colonnes par libell\u00E9 \:
 aggregate_graph_column_settings=Param\u00E8tres colonne
 aggregate_graph_columns_to_display=Colonnes \u00E0 afficher \:
 aggregate_graph_dimension=Taille graphique
 aggregate_graph_display=G\u00E9n\u00E9rer le graphique
 aggregate_graph_draw_outlines=Bordure de barre ?
 aggregate_graph_dynamic_size=Taille de graphique dynamique
 aggregate_graph_font=Police \:
 aggregate_graph_height=Hauteur \:
 aggregate_graph_increment_scale=Intervalle \u00E9chelle \:
 aggregate_graph_legend=L\u00E9gende
 aggregate_graph_legend.placement.bottom=Bas
 aggregate_graph_legend.placement.left=Gauche
 aggregate_graph_legend.placement.right=Droite
 aggregate_graph_legend.placement.top=Haut
 aggregate_graph_legend_placement=Position \:
 aggregate_graph_max_length_xaxis_label=Longueur maximum du libell\u00E9 de l'axe des abscisses \:
 aggregate_graph_ms=Millisecondes
 aggregate_graph_no_values_to_graph=Pas de valeurs pour le graphique
 aggregate_graph_number_grouping=S\u00E9parateur de milliers ?
 aggregate_graph_response_time=Temps de r\u00E9ponse
 aggregate_graph_save=Enregistrer le graphique
 aggregate_graph_save_table=Enregistrer le tableau de donn\u00E9es
 aggregate_graph_save_table_header=Inclure l'ent\u00EAte du tableau
 aggregate_graph_size=Taille \:
 aggregate_graph_style=Style \:
 aggregate_graph_sync_with_name=Synchroniser avec nom
 aggregate_graph_tab_graph=Graphique
 aggregate_graph_tab_settings=Param\u00E8tres
 aggregate_graph_title=Graphique agr\u00E9g\u00E9
 aggregate_graph_title_group=Titre
 aggregate_graph_use_group_name=Ajouter le nom du groupe aux libell\u00E9s
 aggregate_graph_user_title=Titre du graphique \:
 aggregate_graph_value_font=Police de la valeur \:
 aggregate_graph_value_labels_vertical=Libell\u00E9 de valeurs vertical ?
 aggregate_graph_width=Largeur \:
 aggregate_graph_xaxis_group=Abscisses
 aggregate_graph_yaxis_group=Ordonn\u00E9es (milli-secondes)
 aggregate_graph_yaxis_max_value=Echelle maximum \:
 aggregate_report=Rapport agr\u00E9g\u00E9
 aggregate_report_bandwidth=Ko/sec re\u00e7us
 aggregate_report_sent_bytes_per_sec=KB/sec \u00E9mis
 aggregate_report_count=\# Echantillons
 aggregate_report_error=Erreur
 aggregate_report_error%=% Erreur
 aggregate_report_max=Max
 aggregate_report_median=M\u00E9diane
 aggregate_report_min=Min
 aggregate_report_rate=D\u00E9bit
 aggregate_report_stddev=Ecart type
 aggregate_report_total_label=TOTAL
 aggregate_report_xx_pct1_line={0}% centile
 aggregate_report_xx_pct2_line={0}% centile
 aggregate_report_xx_pct3_line={0}% centile
 ajp_sampler_title=Requ\u00EAte AJP/1.3
 als_message=Note \: Le parseur de log d'acc\u00E8s est g\u00E9n\u00E9rique et vous permet de se brancher \u00E0 
 als_message2=votre propre parseur. Pour se faire, impl\u00E9menter le LogParser, ajouter le jar au 
 als_message3=r\u00E9pertoire /lib et entrer la classe (fichier .class) dans l'\u00E9chantillon (sampler).
 analyze=En train d'analyser le fichier de donn\u00E9es
 anchor_modifier_title=Analyseur de lien HTML
 appearance=Apparence
 argument_must_not_be_negative=L'argument ne peut pas \u00EAtre n\u00E9gatif \!
 arguments_panel_title=Param\u00E8tres de commande
 assertion_assume_success=Ignorer le statut
 assertion_body_resp=Corps de r\u00E9ponse
 assertion_code_resp=Code de r\u00E9ponse
 assertion_contains=Contient (exp. r\u00E9guli\u00E8re)
 assertion_equals=Est \u00E9gale \u00E0 (texte brut)
 assertion_headers=Ent\u00EAtes de r\u00E9ponse
 assertion_matches=Correspond \u00E0 (exp. r\u00E9guli\u00E8re)
 assertion_message_resp=Message de r\u00E9ponse
 assertion_network_size=R\u00E9ponse compl\u00E8te
 assertion_not=Inverser
 assertion_or=Ou
 assertion_pattern_match_rules=Type de correspondance du motif
 assertion_patterns_to_test=Motifs \u00E0 tester
 assertion_regex_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 assertion_resp_field=Section de r\u00E9ponse \u00E0 tester
 assertion_resp_size_field=Taille \u00E0 v\u00E9rifier sur
 assertion_substring=Contient (texte brut)
 assertion_text_document=Document (texte)
 assertion_text_resp=Texte de r\u00E9ponse
 assertion_textarea_label=Assertions \:
 assertion_title=Assertion R\u00E9ponse
 assertion_url_samp=URL Echantillon
 assertion_visualizer_title=R\u00E9cepteur d'assertions
 attribute=Attribut \:
 attribute_field=Attribut \:
 attrs=Attributs
 auth_base_url=URL de base
 auth_manager_clear_per_iter=R\u00E9authentifier \u00E0 chaque it\u00E9ration ?
 auth_manager_options=Options
 auth_manager_title=Gestionnaire d'autorisation HTTP
 auths_stored=Autorisations stock\u00E9es
 average=Moyenne
 average_bytes=Moy. octets
 backend_listener=R\u00E9cepteur asynchrone
 backend_listener_classname=Impl\u00E9mentation du r\u00E9cepteur asynchrone
 backend_listener_paramtable=Param\u00E8tres
 backend_listener_queue_size=Taille de la queue
 bind=Connexion de l'unit\u00E9
 bouncy_castle_unavailable_message=Les jars de bouncycastle sont indisponibles, ajoutez les au classpath.
 browse=Parcourir...
 bsf_sampler_title=Echantillon BSF
 bsf_script=Script \u00E0 lancer (variables\: ctx vars props SampleResult sampler log Label FileName Parameters args[] OUT)
 bsf_script_file=Fichier script \u00E0 lancer \:
 bsf_script_language=Langage de script \:
 bsf_script_parameters=Param\u00E8tres \u00E0 passer au script/fichier \:
 bsh_assertion_script=Script (IO\: Failure[Message], Response. IN\: Response[Data|Code|Message|Headers], RequestHeaders, Sample[Label|rData])
 bsh_assertion_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nEn lecture/\u00E9criture \: Failure, FailureMessage, SampleResult, vars, props, log.\nEn lecture seule \: Response[Data|Code|Message|Headers], RequestHeaders, SampleLabel, SamplerData, ctx
 bsh_assertion_title=Assertion BeanShell
 bsh_function_expression=Expression \u00E0 \u00E9valuer
 bsh_sampler_title=Echantillon BeanShell
 bsh_script=Script (voir ci-dessous pour les variables qui sont d\u00E9finies)
 bsh_script_file=Fichier script \:
 bsh_script_parameters=Param\u00E8tres  (-> String Parameters et String []bsh.args)
 bsh_script_reset_interpreter=R\u00E9initialiser l'interpr\u00E9teur bsh avant chaque appel
 bsh_script_variables=Les variables suivantes sont d\u00E9finies pour le script \:\nSampleResult, ResponseCode, ResponseMessage, IsSuccess, Label, FileName, ctx, vars, props, log
 busy_testing=Je suis occup\u00E9 \u00E0 tester, veuillez arr\u00EAter le test avant de changer le param\u00E8trage
 cache_manager_size=Nombre maximum d'\u00E9l\u00E9ments dans le cache
 cache_manager_title=Gestionnaire de cache HTTP
 cache_session_id=Identifiant de session de cache ?
 cancel=Annuler
 cancel_exit_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de sortir ?
 cancel_new_from_template=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de charger le mod\u00E8le ?
 cancel_new_to_save=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Voulez-vous enregistrer avant de nettoyer le plan de test ?
 cancel_revert_project=Il y a des \u00E9l\u00E9ments qui n'ont pas \u00E9t\u00E9 sauv\u00E9s. Annuler les changements et revenir \u00E0 la derni\u00E8re sauvegarde du plan de test ?
 change_parent=Changer le contr\u00F4leur
 char_value=Caract\u00E8re num\u00E9rique Unicode (d\u00E9cimal or 0xhex)
 check_return_code_title=V\u00E9rifier le code retour
 choose_function=Choisir une fonction
 choose_language=Choisir une langue
 clear=Nettoyer
 clear_all=Nettoyer tout
 clear_cache_each_iteration=Vider le cache \u00E0 chaque it\u00E9ration ?
 clear_cache_per_iter=Nettoyer le cache \u00E0 chaque it\u00E9ration ?
 clear_cookies_per_iter=Nettoyer les cookies \u00E0 chaque it\u00E9ration ?
 clipboard_node_read_error=Une erreur est survenue lors de la copie du noeud
 close=Fermer
 closeconnection=Fermer la connexion
 collapse_tooltip=Cliquer pour ouvrir / r\u00E9duire
 column_delete_disallowed=Supprimer cette colonne n'est pas possible
 column_number=Num\u00E9ro de colonne du fichier CSV | next | *alias
 command_config_box_title=Commande \u00E0 ex\u00E9cuter
 command_config_std_streams_title=Flux standard (fichiers)
 command_field_title=Commande \:
 compare=Comparaison
 comparefilt=Filtre de comparaison
 comparison_differ_content=Le contenu des r\u00E9ponses est diff\u00E9rent.
 comparison_differ_time=La diff\u00E9rence du temps de r\u00E9ponse diff\u00E8re de plus de 
 comparison_invalid_node=Noeud invalide 
 comparison_regex_string=Expression r\u00E9guli\u00E8re
 comparison_regex_substitution=Substitution
 comparison_response_time=Temps de r\u00E9ponse \: 
 comparison_unit=ms
 comparison_visualizer_title=R\u00E9cepteur d'assertions de comparaison
 config_element=El\u00E9ment de configuration
 config_save_settings=Configurer
 confirm=Confirmer
 constant_throughput_timer_memo=Ajouter un d\u00E9lai entre les \u00E9chantillions pour obtenir un d\u00E9bit constant
 constant_timer_delay=D\u00E9lai d'attente (en millisecondes) \:
 constant_timer_memo=Ajouter un d\u00E9lai fixe entre les \u00E9chantillions de test
 constant_timer_title=Compteur de temps fixe
 content_encoding=Encodage contenu \:
 controller=Contr\u00F4leur
 cookie_implementation_choose=Impl\u00E9mentation \:
 cookie_manager_policy=Politique des cookies \:
 cookie_manager_title=Gestionnaire de cookies HTTP
 cookie_options=Options
 cookies_stored=Cookies stock\u00E9s
 copy=Copier
 counter_config_title=Compteur
 counter_per_user=Suivre le compteur ind\u00E9pendamment pour chaque unit\u00E9 de test
 counter_reset_per_tg_iteration=R\u00E9initialiser le compteur \u00E0 chaque it\u00E9ration du groupe d'unit\u00E9s
 countlim=Limiter le nombre d'\u00E9l\u00E9ments retourn\u00E9s \u00E0
 critical_section_controller_label=Nom du verrou
 critical_section_controller_title=Contr\u00F4leur Section critique
 cssjquery_attribute=Attribut
 cssjquery_empty_default_value=Utiliser la cha\u00EEne vide comme valeur par d\u00E9faut
 cssjquery_impl=Impl\u00E9mentation CSS/JQuery\:
 cssjquery_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 cssjquery_tester_button_test=Tester
 cssjquery_tester_error=Une erreur s''est produite lors de l''\u00E9valuation de l''expression\:{0}, erreur\:{1}
 cssjquery_tester_field=S\u00E9lecteur\:
 cssjquery_tester_title=Testeur CSS/JQuery
 csvread_file_file_name=Fichier CSV pour obtenir les valeurs de | *alias
 cut=Couper
 cut_paste_function=Fonction de copier/coller de cha\u00EEne de caract\u00E8re
 database_conn_pool_max_usage=Utilisation max pour chaque connexion\:
 database_conn_pool_props=Pool de connexions \u221A\u2020 la base de donn\u221A\u00A9es
 database_conn_pool_size=Nombre de Connexions dans le Pool\:
 database_conn_pool_title=Valeurs par d\u00E9faut du Pool de connexions JDBC
 database_driver_class=Classe du Driver\:
 database_login_title=Valeurs par d\u00E9faut de la base de donn\u221A\u00A9es JDBC
 database_sql_query_string=Requ\u00EAte SQL \:
 database_sql_query_title=Requ\u00EAte SQL JDBC par d\u00E9faut
 database_testing_title=Requ\u221A\u2122te JDBC
 database_url=URL JDBC\:
 database_url_jdbc_props=URL et driver JDBC de la base de donn\u221A\u00A9es
 ddn=DN \:
 de=Allemand
 debug_off=D\u00E9sactiver le d\u00E9bogage
 debug_on=Activer le d\u00E9bogage
 default_parameters=Param\u00E8tres par d\u00E9faut
 default_value_field=Valeur par d\u00E9faut \:
 delay=D\u00E9lai avant d\u00E9marrage (secondes) \:
 delayed_start=Cr\u00E9er les unit\u00E9s seulement quand n\u00E9cessaire
 delete=Supprimer
 delete_parameter=Supprimer le param\u00E8tre
 delete_test=Suppression
 delete_user=Supprimer l'utilisateur
 deltest=Suppression
 deref=D\u00E9r\u00E9f\u00E9rencement des alias
 description=Description
 detail=D\u00E9tail
 directory_field_title=R\u00E9pertoire d'ex\u00E9cution \:
 disable=D\u00E9sactiver
 distribution_graph_title=Graphique de distribution (DEPRECATED)
 distribution_note1=Ce graphique se mettra \u00E0 jour tous les 10 \u00E9chantillons
 dn=Racine DN \:
 dns_cache_manager_title=Gestionnaire de cache DNS
 dns_hostname_or_ip=Nom de machine ou adresse IP
 dns_servers=Serveurs DNS
 domain=Domaine \:
 done=Fait
 down=Descendre
 duplicate=Dupliquer
 duration=Dur\u00E9e (secondes) \:
 duration_assertion_duration_test=Dur\u00E9e maximale \u00E0 v\u00E9rifier
 duration_assertion_failure=L''op\u00E9ration a dur\u00E9e trop longtemps\: cela a pris {0} millisecondes, mais n''aurait pas d\u00FB durer plus de {1} millisecondes.
 duration_assertion_input_error=Veuillez entrer un entier positif valide.
 duration_assertion_label=Dur\u00E9e en millisecondes \:
 duration_assertion_title=Assertion Dur\u00E9e
 duration_tooltip=Temps pass\u00E9 depuis le d\u00E9but du test en cours
 edit=Editer
 email_results_title=R\u00E9sultat d'email
 en=Anglais
 enable=Activer
 encode=URL Encoder
 encode?=Encodage
 encoded_value=Valeur de l'URL encod\u00E9e
 endtime=Date et heure de fin \:
 entry_dn=Entr\u00E9e DN \:
 entrydn=Entr\u00E9e DN
 environment_panel_title=Variables d'environnement
 eolbyte=Valeur byte de l'indicateur de fin de ligne (EOL)\: 
 error_indicator_tooltip=Affiche le nombre d'erreurs dans le journal(log), cliquer pour afficher la console.
 error_loading_help=Erreur au chargement de la page d'aide
 error_occurred=Une erreur est survenue
 error_title=Erreur
 es=Espagnol
 escape_html_string=Cha\u00EEne d'\u00E9chappement
 eval_name_param=Variable contenant du texte et r\u00E9f\u00E9rences de fonctions
 evalvar_name_param=Nom de variable
 example_data=Exemple de donn\u00E9e
 example_title=Echantillon exemple
 exit=Quitter
 expected_return_code_title=Code retour attendu \: 
 expiration=Expiration
 expression_field=Expression CSS/JQuery \:
 field_name=Nom du champ
 file=Fichier
 file_already_in_use=Ce fichier est d\u00E9j\u00E0 utilis\u00E9
 file_visualizer_append=Concat\u00E9ner au fichier de donn\u00E9es existant
 file_visualizer_auto_flush=Vider automatiquement apr\u00E8s chaque echantillon de donn\u00E9es
 file_visualizer_browse=Parcourir...
 file_visualizer_close=Fermer
 file_visualizer_file_options=Options de fichier
 file_visualizer_filename=Nom du fichier \: 
 file_visualizer_flush=Vider
 file_visualizer_missing_filename=Aucun fichier de sortie sp\u00E9cifi\u00E9.
 file_visualizer_open=Ouvrir...
 file_visualizer_output_file=\u00C9crire les r\u00E9sultats dans un fichier ou lire les r\u00E9sultats depuis un fichier CSV / JTL
 file_visualizer_submit_data=Inclure les donn\u00E9es envoy\u00E9es
 file_visualizer_title=Rapporteur de fichier
 file_visualizer_verbose=Sortie verbeuse
 filename=Nom de fichier \: 
 find_target_element=Trouver l'\u00E9l\u00E9ment cible
 follow_redirects=Suivre les redirect.
 follow_redirects_auto=Rediriger automat.
 font.sansserif=Sans Serif
 font.serif=Serif
 fontstyle.bold=Gras
 fontstyle.italic=Italique
 fontstyle.normal=Normal
 foreach_controller_title=Contr\u00F4leur Pour chaque (ForEach)
 foreach_end_index=Indice de fin de la boucle (inclus)
 foreach_input=Pr\u00E9fixe de la variable d'entr\u00E9e \:
 foreach_output=Nom de la variable de sortie \:
 foreach_start_index=Indice de d\u00E9but de la boucle(exclus)
 foreach_use_separator=Ajouter un soulign\u00E9 "_" avant le nombre ?
 format=Format du nombre \:
 fr=Fran\u00E7ais
 ftp_binary_mode=Utiliser le mode binaire ?
 ftp_get=R\u00E9cup\u00E9rer (get)
 ftp_local_file=Fichier local \:
 ftp_local_file_contents=Contenus fichier local \:
 ftp_put=D\u00E9poser (put)
 ftp_remote_file=Fichier distant \:
 ftp_sample_title=Param\u00E8tres FTP par d\u00E9faut
 ftp_save_response_data=Enregistrer le fichier dans la r\u00E9ponse ?
 ftp_testing_title=Requ\u00EAte FTP
 function_dialog_menu_item=Assistant de fonctions
 function_helper_title=Assistant de fonctions
 function_name_param=Nom de la fonction. Utilis\u00E9 pour stocker les valeurs \u00E0 utiliser ailleurs dans la plan de test
 function_name_paropt=Nom de variable dans laquelle le r\u00E9sultat sera stock\u00E9 (optionnel)
 function_params=Param\u00E8tres de la fonction
 functional_mode=Mode de test fonctionnel
 functional_mode_explanation=S\u00E9lectionner le mode de test fonctionnel uniquement si vous avez besoin\nd'enregistrer les donn\u00E9es re\u00E7ues du serveur dans un fichier \u00E0 chaque requ\u00EAte. \n\nS\u00E9lectionner cette option affecte consid\u00E9rablement les performances.
 gaussian_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 gaussian (en millisecondes) \:
 gaussian_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution gaussienne
 gaussian_timer_range=D\u00E9viation (en millisecondes) \:
 gaussian_timer_title=Compteur de temps al\u00E9atoire gaussien
 generate=G\u00E9n\u00E9rer
 generator=Nom de la classe g\u00E9n\u00E9ratrice
 generator_cnf_msg=N'a pas p\u00FB trouver la classe g\u00E9n\u00E9ratrice. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 generator_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classes g\u00E9n\u00E9ratrice \u00E0 cause d'une IllegalAccessException.
 generator_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur g\u00E9n\u00E9rateur. Assurez-vous que le g\u00E9n\u00E9rateur impl\u00E9mente l'interface Generator.
 graph_apply_filter=Appliquer le filtre
 graph_choose_graphs=Graphique \u00E0 afficher
 graph_full_results_title=Graphique de r\u00E9sultats complets
 graph_pointshape_circle=Cercle
 graph_pointshape_diamond=Diamant
 graph_pointshape_none=Aucun
 graph_pointshape_square=Carr\u00E9
 graph_pointshape_triangle=Triangle
 graph_resp_time_interval_label=Interval (ms) \:
 graph_resp_time_interval_reload=Appliquer l'interval
 graph_resp_time_not_enough_data=Impossible de dessiner le graphique, pas assez de donn\u00E9es
 graph_resp_time_series_selection=S\u00E9lection des \u00E9chantillons par libell\u00E9 \:
 graph_resp_time_settings_line=Param\u00E9tres de la courbe
 graph_resp_time_settings_pane=Param\u00E9tres du graphique
 graph_resp_time_shape_label=Forme de la jonction \:
 graph_resp_time_stroke_width=Largeur de ligne \:
 graph_resp_time_title=Graphique \u00E9volution temps de r\u00E9ponses
 graph_resp_time_title_label=Titre du graphique \:  
 graph_resp_time_xaxis_time_format=Formatage heure (SimpleDateFormat) \:
 graph_results_average=Moyenne
 graph_results_data=Donn\u00E9es
 graph_results_deviation=Ecart type
 graph_results_latest_sample=Dernier \u00E9chantillon
 graph_results_median=M\u00E9diane
 graph_results_ms=ms
 graph_results_no_samples=Nombre d'\u00E9chantillons
 graph_results_throughput=D\u00E9bit
 graph_results_title=Graphique de r\u00E9sultats
 groovy_function_expression=Expression \u00E0 \u00E9valuer
 grouping_add_separators=Ajouter des s\u00E9parateurs entre les groupes
 grouping_in_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur
 grouping_in_transaction_controllers=Mettre chaque groupe dans un nouveau contr\u00F4leur de transaction
 grouping_mode=Grouper \:
 grouping_no_groups=Ne pas grouper les \u00E9chantillons
 grouping_store_first_only=Stocker le 1er \u00E9chantillon pour chaque groupe uniquement
 header_manager_title=Gestionnaire d'ent\u00EAtes HTTP
 headers_stored=Ent\u00EAtes stock\u00E9es
 heap_dump=Cr\u00E9er une image disque de la m\u00E9moire (heap dump)
 help=Aide
 help_node=Quel est ce noeud ?
 html_assertion_file=Ecrire un rapport JTidy dans un fichier
 html_assertion_label=Assertion HTML
 html_assertion_title=Assertion HTML
 html_extractor_title=Extracteur CSS/JQuery
 html_extractor_type=Impl\u00E9mentation de l'extracteur CSS/JQuery
 http_implementation=Impl\u00E9mentation \:
 http_response_code=Code de r\u00E9ponse HTTP
 http_url_rewriting_modifier_title=Transcripteur d'URL HTTP
 http_user_parameter_modifier=Modificateur de param\u00E8tre utilisateur HTTP
 httpmirror_max_pool_size=Taille maximum du pool d'unit\u00E9s \:
 httpmirror_max_queue_size=Taille maximum de la file d'attente \:
 httpmirror_settings=Param\u00E8tres
 httpmirror_title=Serveur HTTP miroir
 id_prefix=Pr\u00E9fixe d'ID
 id_suffix=Suffixe d'ID
 if_controller_evaluate_all=Evaluer pour tous les fils ?
 if_controller_expression=Interpr\u00E9ter la condition comme une expression
 if_controller_label=Condition (d\u00E9faut Javascript) \:
 if_controller_title=Contr\u00F4leur Si (If)
 ignore_subcontrollers=Ignorer les sous-blocs de contr\u00F4leurs
 include_controller=Contr\u00F4leur Inclusion
 include_equals=Inclure \u00E9gale ?
 include_path=Plan de test \u00E0 inclure
 increment=Incr\u00E9ment \:
 infinite=Infini
 initial_context_factory=Fabrique de contexte initiale
 insert_after=Ins\u00E9rer apr\u00E8s
 insert_before=Ins\u00E9rer avant
 insert_parent=Ins\u00E9rer en tant que parent
 interleave_control_title=Contr\u00F4leur Interleave
 interleave_accross_threads=Alterne en prenant en compte toutes les unit\u00E9s
 intsum_param_1=Premier entier \u00E0 ajouter
 intsum_param_2=Deuxi\u00E8me entier \u00E0 ajouter - les entier(s) suivants peuvent \u00EAtre ajout\u00E9(s) avec les arguments suivants.
 invalid_data=Donn\u00E9e invalide
 invalid_mail=Une erreur est survenue lors de l'envoi de l'email
 invalid_mail_address=Une ou plusieurs adresse(s) invalide(s) ont \u00E9t\u00E9 d\u00E9tect\u00E9e(s)
 invalid_mail_server=Le serveur de mail est inconnu (voir le fichier de journalisation JMeter)
 invalid_variables=Variables invalides
 iteration_counter_arg_1=TRUE, pour que chaque utilisateur ait son propre compteur, FALSE pour un compteur global
 iterator_num=Nombre d'it\u00E9rations \:
 ja=Japonais
 jar_file=Fichiers .jar
 java_request=Requ\u00EAte Java
 java_request_defaults=Requ\u00EAte Java par d\u00E9faut
 javascript_expression=Expression JavaScript \u00E0 \u00E9valuer
 jexl_expression=Expression JEXL \u00E0 \u00E9valuer
 jms_auth_required=Obligatoire
 jms_bytes_message=Message binaire
 jms_client_caption=Le client r\u00E9cepteur utilise MessageConsumer.receive () pour \u00E9couter les messages.
 jms_client_caption2=MessageListener utilise l'interface onMessage(Message) pour \u00E9couter les nouveaux messages.
 jms_client_id=ID du Client
 jms_client_type=Client
 jms_communication_style=Type de communication \: 
 jms_concrete_connection_factory=Fabrique de connexion 
 jms_config=Source du message \:
 jms_config_title=Configuration JMS
 jms_connection_factory=Fabrique de connexion
 jms_correlation_title=Champs alternatifs pour la correspondance de message
 jms_dest_setup=Evaluer
 jms_dest_setup_dynamic=A chaque \u00E9chantillon
 jms_dest_setup_static=Au d\u00E9marrage
 jms_durable_subscription_id=ID d'abonnement durable
+jms_error_reconnect_on_codes=Se reconnecter pour les codes d'erreurs (regex)
+jms_error_pause_between=Temporisation entre erreurs (ms)
 jms_expiration=Expiration (ms)
 jms_file=Fichier
 jms_initial_context_factory=Fabrique de connexion initiale
 jms_itertions=Nombre d'\u00E9chantillons \u00E0 agr\u00E9ger
 jms_jndi_defaults_title=Configuration JNDI par d\u00E9faut
 jms_jndi_props=Propri\u00E9t\u00E9s JNDI
 jms_map_message=Message Map
 jms_message_title=Propri\u00E9t\u00E9s du message
 jms_message_type=Type de message \: 
 jms_msg_content=Contenu
 jms_object_message=Message Object
 jms_point_to_point=Requ\u00EAte JMS Point-\u00E0-point
 jms_priority=Priorit\u00E9 (0-9)
 jms_properties=Propri\u00E9t\u00E9s JMS
 jms_properties_name=Nom
 jms_properties_title=Propri\u00E9t\u00E9s JMS
 jms_properties_type=Classe de la Valeur
 jms_properties_value=Valeur
 jms_props=Propri\u00E9t\u00E9s JMS
 jms_provider_url=URL du fournisseur
 jms_publisher=Requ\u00EAte JMS Publication
 jms_pwd=Mot de passe
 jms_queue=File
 jms_queue_connection_factory=Fabrique QueueConnection
 jms_queueing=Ressources JMS
 jms_random_file=Dossier contenant des fichiers al\u00E9atoires (suffix\u00E9s par .dat pour un message binaire, .txt ou .obj pour un message texte ou un objet)
 jms_receive_queue=Nom JNDI de la file d'attente Receive 
 jms_request=Requ\u00EAte seule
 jms_requestreply=Requ\u00EAte R\u00E9ponse
 jms_sample_title=Requ\u00EAte JMS par d\u00E9faut
 jms_selector=S\u00E9lecteur JMS
 jms_send_queue=Nom JNDI de la file d'attente Request
 jms_separator=S\u00E9parateur
 jms_stop_between_samples=Arr\u00EAter entre les \u00E9chantillons ?
 jms_store_response=Stocker la r\u00E9ponse
 jms_subscriber_on_message=Utiliser MessageListener.onMessage()
 jms_subscriber_receive=Utiliser MessageConsumer.receive()
 jms_subscriber_title=Requ\u00EAte JMS Abonnement
 jms_testing_title=Messagerie Request
 jms_text_area=Message texte ou Message Objet s\u00E9rialis\u00E9 en XML par XStream
 jms_text_message=Message texte
 jms_timeout=D\u00E9lai (ms)
 jms_topic=Destination
 jms_use_auth=Utiliser l'authentification ?
 jms_use_file=Depuis un fichier
 jms_use_non_persistent_delivery=Utiliser un mode de livraison non persistant ?
 jms_use_properties_file=Utiliser le fichier jndi.properties
 jms_use_random_file=Fichier al\u00E9atoire
 jms_use_req_msgid_as_correlid=Utiliser l'ID du message Request
 jms_use_res_msgid_as_correlid=Utiliser l'ID du message Response
 jms_use_text=Zone de texte (ci-dessous)
 jms_user=Utilisateur
 jndi_config_title=Configuration JNDI
 jndi_lookup_name=Interface remote
 jndi_lookup_title=Configuration Lookup JNDI 
 jndi_method_button_invoke=Invoquer
 jndi_method_button_reflect=R\u00E9flection
 jndi_method_home_name=Nom de la m\u00E9thode home
 jndi_method_home_parms=Param\u00E8tres de la m\u00E9thode home
 jndi_method_name=Configuration m\u00E9thode
 jndi_method_remote_interface_list=Interfaces remote
 jndi_method_remote_name=Nom m\u00E9thodes remote
 jndi_method_remote_parms=Param\u00E8tres m\u00E9thode remote
 jndi_method_title=Configuration m\u00E9thode remote
 jndi_testing_title=Requ\u00EAte JNDI
 jndi_url_jndi_props=Propri\u00E9t\u00E9s JNDI
 json_post_processor_title=Extracteur JSON
 jsonpath_render_no_text=Pas de Texte
 jsonpath_renderer=Testeur JSON Path
 jsonpath_tester_button_test=Tester
 jsonpath_tester_field=Expression JSON Path
 jsonpath_tester_title=Testeur JSON Path
 jsonpp_compute_concat=Calculer la variable de concat\u00E9nation (suffix _ALL)
 jsonpp_default_values=Valeure par d\u00E9fault
 jsonpp_error_number_arguments_mismatch_error=D\u00E9calage entre nombre de variables, expressions et valeurs par d\u00E9faut
 jsonpp_json_path_expressions=Expressions JSON Path
 jsonpp_match_numbers=Nombre de correspondances
 jsonpp_variable_names=Noms des variables
 junit_append_error=Concat\u00E9ner les erreurs d'assertion
 junit_append_exception=Concat\u00E9ner les exceptions d'ex\u00E9cution
 junit_constructor_error=Impossible de cr\u00E9er une instance de la classe
 junit_constructor_string=Libell\u00E9 de cha\u00EEne Constructeur
 junit_create_instance_per_sample=Cr\u00E9er une nouvelle instance pour chaque \u00E9chantillon
 junit_do_setup_teardown=Ne pas appeler setUp et tearDown
 junit_error_code=Code d'erreur
 junit_error_default_msg=Une erreur inattendue est survenue
 junit_error_msg=Message d'erreur
 junit_failure_code=Code d'\u00E9chec
 junit_failure_default_msg=Test \u00E9chou\u00E9
 junit_failure_msg=Message d'\u00E9chec
 junit_junit4=Rechercher les annotations JUnit 4 (au lieu de JUnit 3)
 junit_pkg_filter=Filtre de paquets
 junit_request=Requ\u00EAte JUnit
 junit_request_defaults=Requ\u00EAte par d\u00E9faut JUnit
 junit_success_code=Code de succ\u00E8s
 junit_success_default_msg=Test r\u00E9ussi
 junit_success_msg=Message de succ\u00E8s
 junit_test_config=Param\u00E8tres Test JUnit
 junit_test_method=M\u00E9thode de test
 action_check_message=Un test est en cours, arr\u00EAtez le avant d''utiliser cette commande
 action_check_title=Test en cours
 ldap_argument_list=Liste d'arguments LDAP
 ldap_connto=D\u00E9lai d'attente de connexion (millisecondes)
 ldap_parse_results=Examiner les r\u00E9sultats de recherche ?
 ldap_sample_title=Requ\u00EAte LDAP par d\u00E9faut
 ldap_search_baseobject=Effectuer une recherche 'baseobject'
 ldap_search_onelevel=Effectuer une recherche 'onelevel'
 ldap_search_subtree=Effectuer une recherche 'subtree'
 ldap_secure=Utiliser le protocole LDAP s\u00E9curis\u00E9 (ldaps) ?
 ldap_testing_title=Requ\u00EAte LDAP
 ldapext_sample_title=Requ\u00EAte LDAP \u00E9tendue par d\u00E9faut
 ldapext_testing_title=Requ\u00EAte LDAP \u00E9tendue
 library=Librairie
 load=Charger
 log_errors_only=Erreurs
 log_file=Emplacement du fichier de journal (log)
 log_function_comment=Commentaire (facultatif)
 log_function_level=Niveau de journalisation (INFO par d\u00E9faut), OUT ou ERR
 log_function_string=Cha\u00EEne \u00E0 tracer
 log_function_string_ret=Cha\u00EEne \u00E0 tracer (et \u00E0 retourner)
 log_function_throwable=Texte de l'exception Throwable (optionnel)
 log_only=Uniquement \:
 log_parser=Nom de la classe de parseur des journaux (log)
 log_parser_cnf_msg=N'a pas p\u00FB trouver cette classe. Assurez-vous que vous avez plac\u00E9 votre fichier jar dans le r\u00E9pertoire /lib
 log_parser_illegal_msg=N'a pas p\u00FB acc\u00E9der \u00E0 la classe \u00E0 cause d'une exception IllegalAccessException.
 log_parser_instantiate_msg=N'a pas p\u00FB cr\u00E9er une instance du parseur de log. Assurez-vous que le parseur impl\u00E9mente l'interface LogParser.
 log_sampler=Echantillon Journaux d'acc\u00E8s Tomcat
 log_success_only=Succ\u00E8s
 logic_controller_title=Contr\u00F4leur Simple
 login_config=Configuration Identification
 login_config_element=Configuration Identification 
 longsum_param_1=Premier long \u221A\u2020 ajouter
 longsum_param_2=Second long \u221A\u2020 ajouter - les autres longs pourront \u221A\u2122tre cumul\u221A\u00A9s en ajoutant d'autres arguments.
 loop_controller_title=Contr\u00F4leur Boucle
 looping_control=Contr\u00F4le de boucle
 lower_bound=Borne Inf\u00E9rieure
 mail_reader_account=Nom utilisateur \:
 mail_reader_all_messages=Tous
 mail_reader_delete=Supprimer les messages du serveur
 mail_reader_folder=Dossier \:
 mail_reader_header_only=R\u00E9cup\u00E9rer seulement les ent\u00EAtes
 mail_reader_num_messages=Nombre de message \u00E0 r\u00E9cup\u00E9rer \:
 mail_reader_password=Mot de passe \:
 mail_reader_port=Port (optionnel) \:
 mail_reader_server=Serveur \:
 mail_reader_server_type=Protocole (ex. pop3, imaps) \:
 mail_reader_storemime=Stocker le message en utilisant MIME (brut)
 mail_reader_title=Echantillon Lecteur d'email
 mail_sent=Email envoy\u00E9 avec succ\u00E8s
 mailer_addressees=Destinataire(s) \: 
 mailer_attributes_panel=Attributs de courrier
 mailer_connection_security=S\u00E9curit\u00E9 connexion \: 
 mailer_error=N'a pas p\u00FB envoyer l'email. Veuillez corriger les erreurs de saisie.
 mailer_failure_limit=Limite d'\u00E9chec \: 
 mailer_failure_subject=Sujet Echec \: 
 mailer_failures=Nombre d'\u00E9checs \: 
 mailer_from=Exp\u00E9diteur \: 
 mailer_host=Serveur \: 
 mailer_login=Identifiant \: 
 mailer_msg_title_error=Erreur
 mailer_msg_title_information=Information
 mailer_password=Mot de passe \: 
 mailer_port=Port \: 
 mailer_string=Notification d'email
 mailer_success_limit=Limite de succ\u00E8s \: 
 mailer_success_subject=Sujet Succ\u00E8s \: 
 mailer_test_mail=Tester email
 mailer_title_message=Message
 mailer_title_settings=Param\u00E8tres
 mailer_title_smtpserver=Serveur SMTP
 mailer_visualizer_title=R\u00E9cepteur Notification Email
 match_num_field=R\u00E9cup\u00E9rer la Ni\u00E8me corresp. (0 \: Al\u00E9atoire) \: 
 max=Maximum \:
 maximum_param=La valeur maximum autoris\u00E9e pour un \u00E9cart de valeurs
 md5hex_assertion_failure=Erreur de v\u00E9rification de la somme MD5 \: obtenu {0} mais aurait d\u00FB \u00EAtre {1}
 md5hex_assertion_label=MD5Hex
 md5hex_assertion_md5hex_test=MD5Hex \u00E0 v\u00E9rifier
 md5hex_assertion_title=Assertion MD5Hex
 mechanism=M\u00E9canisme
 menu_assertions=Assertions
 menu_close=Fermer
 menu_collapse_all=R\u00E9duire tout
 menu_config_element=Configurations
 menu_edit=Editer
 menu_expand_all=Etendre tout
 menu_fragments=Fragment d'\u00E9l\u00E9ments
 menu_generative_controller=Echantillons
 menu_listener=R\u00E9cepteurs
 menu_logger_panel=Afficher la console 
 menu_logic_controller=Contr\u00F4leurs Logiques
 menu_merge=Fusionner...
 menu_modifiers=Modificateurs
 menu_non_test_elements=El\u00E9ments hors test
 menu_open=Ouvrir...
 menu_post_processors=Post-Processeurs
 menu_pre_processors=Pr\u00E9-Processeurs
 menu_response_based_modifiers=Modificateurs bas\u00E9s sur la r\u00E9ponse
 menu_search=Rechercher
 menu_search_reset=Effacer la recherche
 menu_tables=Table
 menu_threads=Moteurs d'utilisateurs
 menu_timer=Compteurs de temps
 menu_toolbar=Barre d'outils
 metadata=M\u00E9ta-donn\u00E9es
 method=M\u00E9thode \:
 mimetype=Type MIME
 minimum_param=La valeur minimale autoris\u00E9e pour l'\u00E9cart de valeurs
 minute=minute
 modddn=Ancienne valeur
 modification_controller_title=Contr\u00F4leur Modification
 modification_manager_title=Gestionnaire Modification
 modify_test=Modification
 modtest=Modification
 module_controller_module_to_run=Module \u00E0 ex\u00E9cuter \:
 module_controller_title=Contr\u00F4leur Module
 module_controller_warning=Ne peut pas trouver le module \:
 name=Nom \:
 new=Nouveau
 newdn=Nouveau DN
 next=Suivant
 no=Norv\u00E9gien
 notify_child_listeners_fr=Notifier les r\u00E9cepteurs fils des \u00E9chantillons filtr\u00E9s
 number_of_threads=Nombre d'unit\u00E9s (utilisateurs) \:
 obsolete_test_element=Cet \u00E9l\u00E9ment de test est obsol\u00E8te
 once_only_controller_title=Contr\u00F4leur Ex\u00E9cution unique
 opcode=Code d'op\u00E9ration
 open=Ouvrir...
 option=Options
 optional_tasks=T\u00E2ches optionnelles
 paramtable=Envoyer les param\u00E8tres avec la requ\u00EAte \:
 password=Mot de passe \:
 paste=Coller
 paste_insert=Coller ins\u00E9rer
 path=Chemin \:
 path_extension_choice=Extension de chemin (utiliser ";" comme separateur)
 path_extension_dont_use_equals=Ne pas utiliser \u00E9gale dans l'extension de chemin (Compatibilit\u00E9 Intershop Enfinity)
 path_extension_dont_use_questionmark=Ne pas utiliser le point d'interrogation dans l'extension du chemin (Compatiblit\u00E9 Intershop Enfinity)
 patterns_to_exclude=URL \: motifs \u00E0 exclure
 patterns_to_include=URL \: motifs \u00E0 inclure
 pkcs12_desc=Clef PKCS 12 (*.p12)
 pl=Polonais
 poisson_timer_delay=D\u00E9lai de d\u00E9calage bas\u00E9 sur la loi de poisson (en millisecondes) \:
 poisson_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution de type Poisson
 poisson_timer_range=D\u00E9viation (en millisecondes) \:
 poisson_timer_title=Compteur de temps al\u00E9atoire selon la loi de Poisson 
 port=Port \:
 post_as_parameters=Param\u00E8tres
 post_body=Corps de la requ\u00EAte
 post_body_raw=Donn\u00E9es de la requ\u00EAte
 post_files_upload=T\u00E9l\u00E9chargement de fichiers
 post_thread_group_title=Groupe d'unit\u00E9s de fin
 previous=Pr\u00E9c\u00E9dent
 property_as_field_label={0}\:
 property_default_param=Valeur par d\u00E9faut
 property_edit=Editer
 property_editor.value_is_invalid_message=Le texte que vous venez d'entrer n'a pas une valeur valide pour cette propri\u00E9t\u00E9.\nLa propri\u00E9t\u00E9 va revenir \u00E0 sa valeur pr\u00E9c\u00E9dente.
 property_editor.value_is_invalid_title=Texte saisi invalide
 property_name_param=Nom de la propri\u00E9t\u00E9
 property_returnvalue_param=Revenir \u00E0 la valeur originale de la propri\u00E9t\u00E9 (d\u00E9faut non) ?
 property_tool_tip=<html>{0}</html>
 property_undefined=Non d\u00E9fini
 property_value_param=Valeur de propri\u00E9t\u00E9
 property_visualiser_title=Afficheur de propri\u00E9t\u00E9s
 protocol=Protocole [http] \:
 protocol_java_border=Classe Java
 protocol_java_classname=Nom de classe \:
 protocol_java_config_tile=Configurer \u00E9chantillon Java
 protocol_java_test_title=Test Java
 provider_url=Provider URL
 proxy_assertions=Ajouter une Assertion R\u00E9ponse
 proxy_cl_error=Si un serveur proxy est sp\u00E9cifi\u00E9, h\u00F4te et port doivent \u00EAtre donn\u00E9
 proxy_cl_wrong_target_cl=Le contr\u00F4leur cible est configur\u00E9 en mode "Utiliser un contr\u00F4leur enregistreur" \nmais aucun contr\u00F4leur de ce type n'existe, assurez vous de l'ajouter comme fils \nde Groupe d'unit\u00E9s afin de pouvoir d\u00E9marrer l'enregisteur
 proxy_content_type_exclude=Exclure \:
 proxy_content_type_filter=Filtre de type de contenu
 proxy_content_type_include=Inclure \:
 proxy_daemon_bind_error=Impossible de lancer le serveur proxy, le port est d\u00E9j\u00E0 utilis\u00E9. Choisissez un autre port.
 proxy_daemon_error=Impossible de lancer le serveur proxy, voir le journal pour plus de d\u00E9tails
 proxy_daemon_error_from_clipboard=depuis le presse-papier
 proxy_daemon_error_not_retrieve=Impossible d'ajouter
 proxy_daemon_error_read_args=Impossible de lire les arguments depuis le presse-papiers \:
 proxy_daemon_msg_check_details=Svp, v\u00E9rifier les d\u00E9tails ci-dessous lors de l'installation du certificat dans le navigateur
 proxy_daemon_msg_created_in_bin=cr\u00E9\u00E9 dans le r\u00E9pertoire bin de JMeter
 proxy_daemon_msg_install_as_in_doc=Vous pouvez l'installer en suivant les instructions de la documentation Component Reference (voir Installing the JMeter CA certificate for HTTPS recording paragraph)
 proxy_daemon_msg_rootca_cert=Certificat AC ra\u00E7ine \:
 proxy_domains=Domaines HTTPS \:
 proxy_domains_dynamic_mode_tooltip=Liste de noms de domaine pour les url HTTPS, ex. jmeter.apache.org ou les domaines wildcard comme *.apache.org. Utiliser la virgule comme s\u00E9parateur. 
 proxy_domains_dynamic_mode_tooltip_java6=Pour activer ce champ, utiliser un environnement d'ex\u00E9cution Java 7+
 proxy_general_settings=Param\u00E8tres g\u00E9n\u00E9raux
 proxy_headers=Capturer les ent\u00EAtes HTTP
 proxy_prefix_http_sampler_name=Pr\u00E9fixe \:
 proxy_regex=Correspondance des variables par regex ?
 proxy_sampler_settings=Param\u00E8tres Echantillon HTTP
 proxy_sampler_type=Type \:
 proxy_separators=Ajouter des s\u00E9parateurs
 proxy_settings_port_error_digits=Seuls les chiffres sont autoris\u00E9s.
 proxy_settings_port_error_invalid_data=Donn\u00E9es invalides
 proxy_target=Contr\u00F4leur Cible \:
 proxy_test_plan_content=Param\u00E8tres du plan de test
 proxy_title=Enregistreur script de test HTTP(S)
 pt_br=Portugais (Br\u00E9sil)
 ramp_up=Dur\u00E9e de mont\u00E9e en charge (en secondes) \:
 random_control_title=Contr\u00F4leur Al\u00E9atoire
 random_order_control_title=Contr\u00F4leur d'Ordre al\u00E9atoire
 random_multi_result_source_variable=Variable(s) source (separateur |)
 random_multi_result_target_variable=Variable cible
 random_string_chars_to_use=Caract\u00E8res \u00E0 utiliser pour la g\u00E9n\u00E9ration de la cha\u00EEne al\u00E9atoire
 random_string_length=Longueur de cha\u00EEne al\u00E9atoire
 realm=Univers (realm)
 record_controller_clear_samples=Supprimer tous les \u00E9chantillons
 record_controller_title=Contr\u00F4leur Enregistreur
 redo=R\u00E9tablir
 ref_name_field=Nom de r\u00E9f\u00E9rence \:
 regex_extractor_title=Extracteur Expression r\u00E9guli\u00E8re
 regex_field=Expression r\u00E9guli\u00E8re \:
 regex_params_names_field=Num\u00E9ro du groupe de la Regex pour les noms des param\u00E8tres
 regex_params_ref_name_field=Nom de la r\u00E9f\u00E9rence de la Regex
 regex_params_title=Param\u00E8tres utilisateurs bas\u00E9s sur RegEx
 regex_params_values_field=Num\u00E9ro du groupe de la Regex pour les valeurs des param\u00E8tres
 regex_source=Port\u00E9e
 regex_src_body=Corps
 regex_src_body_as_document=Corps en tant que Document
 regex_src_body_unescaped=Corps (non \u00E9chapp\u00E9)
 regex_src_hdrs=Ent\u00EAtes (R\u00E9ponse)
 regex_src_hdrs_req=Ent\u00EAtes (Requ\u00EAte)
 regex_src_url=URL
 regexfunc_param_1=Expression r\u00E9guli\u00E8re utilis\u00E9e pour chercher les r\u00E9sultats de la requ\u00EAte pr\u00E9c\u00E9dente.
 regexfunc_param_2=Canevas pour la ch\u00EEne de caract\u00E8re de remplacement, utilisant des groupes d'expressions r\u00E9guli\u00E8res. Le format est  $[group]$.  Exemple $1$.
 regexfunc_param_3=Quelle correspondance utiliser. Un entier 1 ou plus grand, RAND pour indiquer que JMeter doit choisir al\u00E9atoirement , A d\u00E9cimal, ou ALL indique que toutes les correspondances doivent \u00EAtre utilis\u00E9es
 regexfunc_param_4=Entre le texte. Si ALL est s\u00E9lectionn\u00E9, l'entre-texte sera utilis\u00E9 pour g\u00E9n\u00E9rer les r\u00E9sultats ([""])
 regexfunc_param_5=Text par d\u00E9faut. Utilis\u00E9 \u00E0 la place du canevas si l'expression r\u00E9guli\u00E8re ne trouve pas de correspondance
 regexfunc_param_7=Variable en entr\u221A\u00A9e contenant le texte \u221A\u2020 parser ([\u221A\u00A9chantillon pr\u221A\u00A9c\u221A\u00A9dent])
 regexp_render_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 regexp_tester_button_test=Tester
 regexp_tester_field=Expression r\u00E9guli\u00E8re \:
 regexp_tester_title=Testeur de RegExp
 remote_error_init=Erreur lors de l'initialisation du serveur distant
 remote_error_starting=Erreur lors du d\u221A\u00A9marrage du serveur distant
 remote_exit=Sortie distante
 remote_exit_all=Sortie distante de tous
 remote_shut=Extinction \u00E0 distance
 remote_shut_all=Extinction \u00E0 distance de tous
 remote_start=D\u00E9marrage distant
 remote_start_all=D\u00E9marrage distant de tous
 remote_stop=Arr\u00EAt distant
 remote_stop_all=Arr\u00EAt distant de tous
 remove=Supprimer
 remove_confirm_msg=Etes-vous s\u00FBr de vouloir supprimer ce(s) \u00E9l\u00E9ment(s) ?
 remove_confirm_title=Confirmer la suppression ?
 rename=Renommer une entr\u00E9e
 report=Rapport
 report_bar_chart=Graphique \u221A\u2020 barres
 report_bar_graph_url=URL
 report_base_directory=R\u221A\u00A9pertoire de Base
 report_chart_caption=L\u221A\u00A9gende du graph
 report_chart_x_axis=Axe X
 report_chart_x_axis_label=Libell\u221A\u00A9 de l'Axe X
 report_chart_y_axis=Axe Y
 report_chart_y_axis_label=Libell\u221A\u00A9 de l'Axe Y
 report_line_graph=Graphique Lin\u221A\u00A9aire
 report_line_graph_urls=Inclure les URLs
 report_output_directory=R\u221A\u00A9pertoire de sortie du rapport
 report_page=Page de Rapport
 report_page_element=Page Element
 report_page_footer=Pied de page
 report_page_header=Ent\u221A\u2122te de Page
 report_page_index=Cr\u221A\u00A9er la Page d'Index
 report_page_intro=Page d'Introduction
 report_page_style_url=Url de la feuille de style
 report_page_title=Titre de la Page
 report_pie_chart=Camembert
 report_plan=Plan du rapport
 report_select=Selectionner
 report_summary=Rapport r\u221A\u00A9sum\u221A\u00A9
 report_table=Table du Rapport
 report_writer=R\u221A\u00A9dacteur du Rapport
 report_writer_html=R\u221A\u00A9dacteur de rapport HTML
 reportgenerator_top5_error_count=#Erreurs
 reportgenerator_top5_error_label=Erreur
 reportgenerator_top5_label=Echantillon
 reportgenerator_top5_sample_count=#Echantillons
 reportgenerator_top5_total=Total
 reportgenerator_summary_apdex_apdex=Apdex
 reportgenerator_summary_apdex_samplers=Libell\u00E9
 reportgenerator_summary_apdex_satisfied=T (Seuil de tol\u00E9rance)
 reportgenerator_summary_apdex_tolerated=F (Seuil de frustration)
 reportgenerator_summary_errors_count=Nombre d'erreurs
 reportgenerator_summary_errors_rate_all=% de tous les \u00E9chantillons
 reportgenerator_summary_errors_rate_error=% des erreurs
 reportgenerator_summary_errors_type=Type d'erreur
 reportgenerator_summary_statistics_count=\#Echantillons
 reportgenerator_summary_statistics_error_count=KO
 reportgenerator_summary_statistics_error_percent=% Erreur
 reportgenerator_summary_statistics_kbytes=Ko re\u00e7ues / sec
 reportgenerator_summary_statistics_sent_kbytes=Ko envoy\u00e9s / sec
 reportgenerator_summary_statistics_label=Libell\u00E9
 reportgenerator_summary_statistics_max=Max
 reportgenerator_summary_statistics_mean=Temps moyen
 reportgenerator_summary_statistics_min=Min
 reportgenerator_summary_statistics_percentile_fmt=%d%% centile
 reportgenerator_summary_statistics_throughput=D\u00E9bit
 reportgenerator_summary_total=Total
 request_data=Donn\u00E9e requ\u00EAte
 reset=R\u00E9initialiser
 reset_gui=R\u00E9initialiser l'\u00E9l\u00E9ment
 response_save_as_md5=R\u00E9ponse en empreinte MD5
 response_time_distribution_satisfied_label=Requ\u00EAtes \\ntemps de r\u00E9ponse <= {0}ms
 response_time_distribution_tolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms et <= {1}ms
 response_time_distribution_untolerated_label=Requ\u00EAtes \\ntemps de r\u00E9ponse > {0}ms
 response_time_distribution_failed_label=Requ\u00EAtes en erreur
 restart=Red\u00E9marrer
 resultaction_title=Op\u00E9rateur R\u00E9sultats Action
 resultsaver_addtimestamp=Ajouter un timestamp
 resultsaver_errors=Enregistrer seulement les r\u00E9ponses en \u00E9checs
 resultsaver_numberpadlen=Taille minimale du num\u00E9ro de s\u00E9quence
 resultsaver_prefix=Pr\u00E9fixe du nom de fichier \: 
 resultsaver_skipautonumber=Ne pas ajouter de nombre au pr\u00E9fixe
 resultsaver_skipsuffix=Ne pas ajouter de suffixe
 resultsaver_success=Enregistrer seulement les r\u00E9ponses en succ\u00E8s
 resultsaver_title=Sauvegarder les r\u00E9ponses vers un fichier
 resultsaver_variable=Nom de variable \:
 retobj=Retourner les objets
 return_code_config_box_title=Configuration du code retour
 reuseconnection=R\u00E9-utiliser la connexion
 revert_project=Annuler les changements
 revert_project?=Annuler les changements sur le projet ?
 root=Racine
 root_title=Racine
 run=Lancer
 run_threadgroup=Lancer
 run_threadgroup_no_timers=Lancer sans pauses
 running_test=Lancer test
 runtime_controller_title=Contr\u00F4leur Dur\u00E9e d'ex\u00E9cution
 runtime_seconds=Temps d'ex\u00E9cution (secondes) \:
 sample_result_save_configuration=Sauvegarder la configuration de la sauvegarde des \u00E9chantillons
 sample_scope=Appliquer sur
 sample_scope_all=L'\u00E9chantillon et ses ressources li\u00E9es
 sample_scope_children=Les ressources li\u00E9es
 sample_scope_parent=L'\u00E9chantillon
 sample_scope_variable=Une variable \:
 sample_timeout_memo=Interrompre l'\u00E9chantillon si le d\u00E9lai est d\u00E9pass\u00E9
 sample_timeout_timeout=D\u00E9lai d'attente avant interruption (en millisecondes) \: 
 sample_timeout_title=Compteur Interruption
 sampler_label=Libell\u00E9
 sampler_on_error_action=Action \u00E0 suivre apr\u00E8s une erreur d'\u00E9chantillon
 sampler_on_error_continue=Continuer
 sampler_on_error_start_next_loop=D\u00E9marrer it\u00E9ration suivante
 sampler_on_error_stop_test=Arr\u00EAter le test
 sampler_on_error_stop_test_now=Arr\u00EAter le test imm\u00E9diatement
 sampler_on_error_stop_thread=Arr\u00EAter l'unit\u00E9
 save=Enregistrer le plan de test
 save?=Enregistrer ?
 save_all_as=Enregistrer le plan de test sous...
 save_as=Enregistrer sous...
 save_as_error=Au moins un \u00E9l\u00E9ment doit \u00EAtre s\u00E9lectionn\u00E9 \!
 save_as_image=Enregistrer en tant qu'image sous...
 save_as_image_all=Enregistrer l'\u00E9cran en tant qu'image...
 save_as_test_fragment=Enregistrer comme Fragment de Test
 save_as_test_fragment_error=Au moins un \u00E9l\u00E9ment ne peut pas \u00EAtre plac\u00E9 sous un Fragment de Test
 save_assertionresultsfailuremessage=Messages d'erreur des assertions
 save_assertions=R\u00E9sultats des assertions (XML)
 save_asxml=Enregistrer au format XML
 save_bytes=Nombre d'octets re\u00e7us
 save_code=Code de r\u00E9ponse HTTP
 save_connecttime=Temps \u00E9tablissement connexion
 save_datatype=Type de donn\u00E9es
 save_encoding=Encodage
 save_fieldnames=Libell\u00E9 des colonnes (CSV)
 save_filename=Nom de fichier de r\u00E9ponse
 save_graphics=Enregistrer le graphique
 save_hostname=Nom d'h\u00F4te
 save_idletime=Temps d'inactivit\u00E9
 save_label=Libell\u00E9
 save_latency=Latence
 save_message=Message de r\u00E9ponse
 save_overwrite_existing_file=Le fichier s\u00E9lectionn\u00E9 existe d\u00E9j\u00E0, voulez-vous l'\u00E9craser ?
 save_requestheaders=Ent\u00EAtes de requ\u00EAte (XML)
 save_responsedata=Donn\u00E9es de r\u00E9ponse (XML)
 save_responseheaders=Ent\u00EAtes de r\u00E9ponse (XML)
 save_samplecount=Nombre d'\u00E9chantillon et d'erreur
 save_samplerdata=Donn\u00E9es d'\u00E9chantillon (XML)
 save_sentbytes=Nombre d'octets envoy\u00E9s
 save_subresults=Sous r\u00E9sultats (XML)
 save_success=Succ\u00E8s
 save_threadcounts=Nombre d'unit\u00E9s actives
 save_threadname=Nom d'unit\u00E9
 save_time=Temps \u00E9coul\u00E9
 save_timestamp=Horodatage
 save_url=URL
 save_workbench=Sauvegarder le plan de travail
 sbind=Simple connexion/d\u00E9connexion
 scheduler=Programmateur de d\u00E9marrage
 scheduler_configuration=Configuration du programmateur
 scope=Port\u00E9e
 search=Rechercher
 search_base=Base de recherche
 search_expand=Rechercher & D\u00E9plier
 search_filter=Filtre de recherche
 search_replace_all=Tout remplacer
 search_test=Recherche
 search_text_button_close=Fermer
 search_text_button_find=Rechercher
 search_text_button_next=Suivant
 search_text_chkbox_case=Consid\u00E9rer la casse
 search_text_chkbox_regexp=Exp. reguli\u00E8re
 search_text_field=Rechercher \:
 search_text_msg_not_found=Texte non trouv\u00E9
 search_text_replace=Remplacer par
 search_text_title_not_found=Pas trouv\u00E9
 search_tree_title=Rechercher dans l'arbre
 searchbase=Base de recherche
 searchfilter=Filtre de recherche
 searchtest=Recherche
 second=seconde
 secure=S\u00E9curis\u00E9 \:
 send_file=Envoyer un fichier avec la requ\u00EAte \:
 send_file_browse=Parcourir...
 send_file_filename_label=Chemin du fichier
 send_file_mime_label=Type MIME
 send_file_param_name_label=Nom du param\u00E8tre
 server=Nom ou IP du serveur \:
 servername=Nom du serveur \:
 session_argument_name=Nom des arguments de la session
 setup_thread_group_title=Groupe d'unit\u00E9s de d\u00E9but
 should_save=Vous devez enregistrer le plan de test avant de le lancer.  \nSi vous utilisez des fichiers de donn\u00E9es (i.e. Source de donn\u00E9es CSV ou la fonction _StringFromFile), \nalors c'est particuli\u00E8rement important d'enregistrer d'abord votre script de test. \nVoulez-vous enregistrer maintenant votre plan de test ?
 shutdown=Eteindre
 simple_config_element=Configuration Simple
 simple_data_writer_title=Enregistreur de donn\u00E9es
 size_assertion_comparator_error_equal=est \u00E9gale \u00E0
 size_assertion_comparator_error_greater=est plus grand que
 size_assertion_comparator_error_greaterequal=est plus grand ou \u00E9gale \u00E0
 size_assertion_comparator_error_less=est inf\u00E9rieur \u00E0
 size_assertion_comparator_error_lessequal=est inf\u00E9rieur ou \u00E9gale \u00E0
 size_assertion_comparator_error_notequal=n'est pas \u00E9gale \u00E0
 size_assertion_comparator_label=Type de comparaison
 size_assertion_failure=Le r\u00E9sultat n''a pas la bonne taille \: il \u00E9tait de {0} octet(s), mais aurait d\u00FB \u00EAtre de {1} {2} octet(s).
 size_assertion_input_error=Entrer un entier positif valide svp.
 size_assertion_label=Taille en octets \:
 size_assertion_size_test=Taille \u00E0 v\u00E9rifier
 size_assertion_title=Assertion Taille
 smime_assertion_issuer_dn=Nom unique de l'\u00E9metteur \: 
 smime_assertion_message_position=V\u00E9rifier l'assertion sur le message \u00E0 partir de la position
 smime_assertion_not_signed=Message non sign\u00E9
 smime_assertion_signature=Signature
 smime_assertion_signer=Certificat signataire
 smime_assertion_signer_by_file=Fichier du certificat \: 
 smime_assertion_signer_constraints=V\u00E9rifier les valeurs \:
 smime_assertion_signer_dn=Nom unique du signataire \: 
 smime_assertion_signer_email=Adresse courriel du signataire \: 
 smime_assertion_signer_no_check=Pas de v\u00E9rification
 smime_assertion_signer_serial=Num\u00E9ro de s\u00E9rie \: 
 smime_assertion_title=Assertion SMIME
 smime_assertion_verify_signature=V\u00E9rifier la signature
 smtp_additional_settings=Param\u00E8tres suppl\u00E9mentaires
 smtp_attach_file=Fichier(s) attach\u00E9(s) \:
 smtp_attach_file_tooltip=S\u00E9parer les fichiers par le point-virgule ";"
 smtp_auth_settings=Param\u00E8tres d'authentification
 smtp_bcc=Adresse en copie cach\u00E9e (Bcc) \:
 smtp_cc=Adresse en copie (CC) \:
 smtp_default_port=(D\u00E9fauts \: SMTP \: 25, SSL \: 465, StartTLS \: 587)
 smtp_eml=Envoyer un message .eml \:
 smtp_enabledebug=Activer les traces de d\u00E9bogage ?
 smtp_enforcestarttls=Forcer le StartTLS
 smtp_enforcestarttls_tooltip=<html><b>Force</b> le serveur a utiliser StartTLS.<br />Si il n'est pas s\u00E9lectionn\u00E9 et que le serveur SMTP ne supporte pas StartTLS, <br />une connexion SMTP normale sera utilis\u00E9e \u00E0 la place. <br /><i>Merci de noter</i> que la case \u00E0 cocher cr\u00E9\u00E9e un fichier dans /tmp/, <br />donc cela peut poser des probl\u00E8mes sous Windows.</html>
 smtp_from=Adresse exp\u00E9diteur (From) \:
 smtp_header_add=Ajouter une ent\u00EAte
 smtp_header_name=Nom d'ent\u00EAte
 smtp_header_remove=Supprimer
 smtp_header_value=Valeur d'ent\u00EAte
 smtp_mail_settings=Param\u00E8tres du courriel
 smtp_message=Message \:
 smtp_message_settings=Param\u00E8tres du message
 smtp_messagesize=Calculer la taille du message
 smtp_password=Mot de passe \:
 smtp_plainbody=Envoyer le message en texte (i.e. sans multipart/mixed)
 smtp_replyto=Adresse de r\u00E9ponse (Reply-To) \:
 smtp_sampler_title=Requ\u00EAte SMTP
 smtp_security_settings=Param\u00E8tres de s\u00E9curit\u00E9
 smtp_server=Serveur \:
 smtp_server_connection_timeout=D\u00E9lai d'attente de connexion \:
 smtp_server_port=Port \:
 smtp_server_settings=Param\u00E8tres du serveur
 smtp_server_timeout=D\u00E9lai d'attente de r\u00E9ponse \:
 smtp_server_timeouts_settings=D\u00E9lais d'attente (milli-secondes)
 smtp_subject=Sujet \:
 smtp_suppresssubj=Supprimer l'ent\u00EAte Sujet (Subject)
 smtp_timestamp=Ajouter un horodatage dans le sujet
 smtp_to=Adresse destinataire (To) \:
 smtp_trustall=Faire confiance \u00E0 tous les certificats
 smtp_trustall_tooltip=<html><b>Forcer</b> JMeter \u00E0 faire confiance \u00E0 tous les certificats, quelque soit l'autorit\u00E9 de certification du certificat.</html>
 smtp_truststore=Coffre de cl\u00E9s local \:
 smtp_truststore_tooltip=<html>Le chemin du coffre de confiance.<br />Les chemins relatifs sont d\u00E9termin\u00E9s \u00E0 partir du r\u00E9pertoire courant.<br />En cas d'\u00E9chec, c'est le r\u00E9pertoire contenant le script JMX qui est utilis\u00E9.</html>
 smtp_useauth=Utiliser l'authentification
 smtp_usenone=Pas de fonctionnalit\u00E9 de s\u00E9curit\u00E9
 smtp_username=Identifiant \:
 smtp_usessl=Utiliser SSL
 smtp_usestarttls=Utiliser StartTLS
 smtp_usetruststore=Utiliser le coffre de confiance local
 smtp_usetruststore_tooltip=<html>Autoriser JMeter \u00E0 utiliser le coffre de confiance local.</html>
 soap_action=Action Soap
 soap_data_title=Donn\u00E9es Soap/XML-RPC
 soap_sampler_file_invalid=Le nom de fichier r\u00E9f\u00E9rence un fichier absent ou sans droits de lecture\:
 soap_sampler_title=Requ\u00EAte SOAP/XML-RPC
 soap_send_action=Envoyer l'action SOAP \:
 solinger=SO_LINGER\:
 split_function_separator=S\u00E9parateur utilis\u00E9 pour scinder le texte. Par d\u00E9faut , (virgule) est utilis\u00E9.
 split_function_string=Texte \u00E0 scinder
 ssl_alias_prompt=Veuillez entrer votre alias pr\u00E9f\u00E9r\u00E9
 ssl_alias_select=S\u00E9lectionner votre alias pour le test
 ssl_alias_title=Alias du client
 ssl_error_title=Probl\u00E8me de KeyStore
 ssl_pass_prompt=Entrer votre mot de passe
 ssl_pass_title=Mot de passe KeyStore
 ssl_port=Port SSL
 sslmanager=Gestionnaire SSL
 start=Lancer
 start_no_timers=Lancer sans pauses
 starttime=Date et heure de d\u00E9marrage \:
 stop=Arr\u00EAter
 stopping_test=Arr\u00EAt de toutes les unit\u00E9s de tests en cours. Le nombre d'unit\u00E9s actives est visible dans le coin haut droit de l'interface. Soyez patient, merci. 
 stopping_test_failed=Au moins une unit\u00E9 non arr\u00EAt\u00E9e; voir le journal.
 stopping_test_host=H\u00F4te
 stopping_test_title=En train d'arr\u00EAter le test
 string_from_file_encoding=Encodage du fichier (optionnel)
 string_from_file_file_name=Entrer le chemin (absolu ou relatif) du fichier
 string_from_file_seq_final=Nombre final de s\u00E9quence de fichier
 string_from_file_seq_start=D\u00E9marer le nombre de s\u00E9quence de fichier
 summariser_title=G\u00E9n\u00E9rer les resultats consolid\u00E9s
 summary_report=Rapport consolid\u00E9
 switch_controller_label=Aller vers le num\u00E9ro d'\u00E9l\u00E9ment (ou nom) subordonn\u00E9 \:
 switch_controller_title=Contr\u00F4leur Aller \u00E0
 system_sampler_stderr=Erreur standard (stderr) \:
 system_sampler_stdin=Entr\u00E9e standard (stdin) \:
 system_sampler_stdout=Sortie standard (stdout) \:
 system_sampler_title=Appel de processus syst\u00E8me
 table_visualizer_bytes=Octets
 table_visualizer_connect=\u00C9tabl. Conn.(ms)
 table_visualizer_latency=Latence
 table_visualizer_sample_num=Echantillon \#
 table_visualizer_sample_time=Temps (ms)
 table_visualizer_sent_bytes=Octets envoy\u00E9s
 table_visualizer_start_time=Heure d\u00E9but
 table_visualizer_status=Statut
 table_visualizer_success=Succ\u00E8s
 table_visualizer_thread_name=Nom d'unit\u00E9
 table_visualizer_warning=Alerte
 target_server=Serveur cible
 tcp_classname=Nom de classe TCPClient \:
 tcp_config_title=Param\u00E8tres TCP par d\u00E9faut
 tcp_nodelay=D\u00E9finir aucun d\u00E9lai (NoDelay)
 tcp_port=Num\u00E9ro de port \:
 tcp_request_data=Texte \u00E0 envoyer \:
 tcp_sample_title=Requ\u00EAte TCP
 tcp_timeout=Expiration (millisecondes) \:
 teardown_on_shutdown=Ex\u00E9cuter le Groupe d'unit\u00E9s de fin m\u00EAme apr\u00E8s un arr\u00EAt manuel des Groupes d'unit\u00E9s principaux
 template_choose=Choisir le mod\u00E8le
 template_create_from=Cr\u00E9er
 template_field=Canevas \:
 template_load?=Charger le mod\u00E8le ?
 template_menu=Mod\u00E8les...
 template_merge_from=Fusionner
 template_reload=Recharger les mod\u00E8les
 template_title=Mod\u00E8les
 test=Test
 test_action_action=Action \:
 test_action_duration=Dur\u00E9e (millisecondes) \:
 test_action_pause=Mettre en pause
 test_action_restart_next_loop=Passer \u00E0 l'it\u00E9ration suivante de la boucle
 test_action_stop=Arr\u00EAter
 test_action_stop_now=Arr\u00EAter imm\u00E9diatement
 test_action_target=Cible \:
 test_action_target_test=Toutes les unit\u00E9s
 test_action_target_thread=Unit\u00E9 courante
 test_action_title=Action test
 test_configuration=Type de test
 test_fragment_title=Fragment d'\u00E9l\u00E9ments
 test_plan=Plan de test
 test_plan_classpath_browse=Ajouter un r\u00E9pertoire ou un fichier 'jar' au 'classpath'
 testconfiguration=Tester la configuration
 testplan.serialized=Lancer les groupes d'unit\u00E9s en s\u00E9rie (c'est-\u00E0-dire \: lance un groupe \u00E0 la fois)
 testplan_comments=Commentaires \:
 testt=Test
 textbox_cancel=Annuler
 textbox_close=Fermer
 textbox_save_close=Enregistrer & Fermer
 textbox_title_edit=Editer texte
 textbox_title_view=Voir texte
 textbox_tooltip_cell=Double clic pour voir/editer
 thread_delay_properties=Propri\u00E9t\u00E9s de temporisation de l'unit\u00E9
 thread_group_title=Groupe d'unit\u00E9s
 thread_properties=Propri\u00E9t\u00E9s du groupe d'unit\u00E9s
 threadgroup=Groupe d'unit\u00E9s
 throughput_control_bynumber_label=Ex\u00E9cutions totales
 throughput_control_bypercent_label=Pourcentage d'ex\u00E9cution
 throughput_control_perthread_label=Par utilisateur
 throughput_control_title=Contr\u00F4leur D\u00E9bit
 throughput_control_tplabel=D\u00E9bit \:
 time_format=Chaine de formatage sur le mod\u00E8le SimpleDateFormat (optionnel)
 timelim=Limiter le temps de r\u00E9ponses \u00E0 (ms)
 timeout_config_box_title=Configuration du d\u00E9lai d'expiration
 timeout_title=D\u00E9lai expiration (ms)
 toggle=Permuter
 toolbar_icon_set_not_found=Le fichier de description des ic\u00F4nes de la barre d'outils n'est pas trouv\u00E9. Voir les journaux.
 total_threads_tooltip=Nombre total d'Unit\u00E9s \u00E0 lancer
 tr=Turc
 transaction_controller_include_timers=Inclure la dur\u00E9e des compteurs de temps et pre/post processeurs dans le calcul du temps
 transaction_controller_parent=G\u00E9n\u00E9rer en \u00E9chantillon parent
 transaction_controller_title=Contr\u00F4leur Transaction
 transform_into_variable=Remplacer les valeurs par des variables
 unbind=D\u00E9connexion de l'unit\u00E9
 undo=Annuler
 unescape_html_string=Cha\u00EEne \u00E0 \u00E9chapper
 unescape_string=Cha\u00EEne de caract\u00E8res contenant des\u00E9chappements Java
 uniform_timer_delay=D\u00E9lai de d\u00E9calage constant (en millisecondes) \:
 uniform_timer_memo=Ajoute un d\u00E9lai al\u00E9atoire avec une distribution uniforme
 uniform_timer_range=D\u00E9viation al\u00E9atoire maximum (en millisecondes) \:
 uniform_timer_title=Compteur de temps al\u00E9atoire uniforme
 up=Monter
 update=Mettre \u00E0 jour
 update_per_iter=Mettre \u00E0 jour une fois par it\u00E9ration
 upload=Fichier \u00E0 uploader
 upper_bound=Borne sup\u00E9rieure
 url=URL
 url_config_get=GET
 url_config_http=HTTP
 url_config_https=HTTPS
 url_config_post=POST
 url_config_protocol=Protocole \:
 url_config_title=Param\u00E8tres HTTP par d\u00E9faut
 url_full_config_title=Echantillon d'URL complet
 url_multipart_config_title=Requ\u00EAte HTTP Multipart par d\u00E9faut
 urldecode_string=Cha\u00EEne de style URL \u00E0 d\u00E9coder
 urlencode_string=Cha\u00EEne de caract\u00E8res \u00E0 encoder en style URL
 use_custom_dns_resolver=Utiliser un r\u00E9solveur DNS personnalis\u00E9
 use_expires=Utiliser les ent\u00EAtes Cache-Control/Expires lors du traitement des requ\u00EAtes GET
 use_keepalive=Connexion persist.
 use_multipart_for_http_post=Multipart/form-data
 use_multipart_mode_browser=Ent\u00EAtes compat. navigateur
 use_recording_controller=Utiliser un contr\u00F4leur enregistreur
 use_system_dns_resolver=Utiliser le r\u00E9solveur DNS syst\u00E8me (JVM)
 user=Utilisateur
 user_defined_test=Test d\u00E9fini par l'utilisateur
 user_defined_variables=Variables pr\u00E9-d\u00E9finies
 user_param_mod_help_note=(Ne pas changer. A la place, modifier le fichier de ce nom dans le r\u00E9pertoire /bin de JMeter)
 user_parameters_table=Param\u00E8tres
 user_parameters_title=Param\u00E8tres Utilisateur
 userdn=Identifiant
 username=Nom d'utilisateur \:
 userpw=Mot de passe
 validate_threadgroup=Valider
 value=Valeur \:
 value_to_quote_meta=Valeur \u00E0 \u00E9chapper des caract\u00E8res sp\u00E9ciaux utilis\u00E8s par ORO Regexp
 var_name=Nom de r\u00E9f\u00E9rence \:
 variable_name_param=Nom de variable (peut inclure une r\u00E9f\u00E9rence de variable ou fonction)
 view_graph_tree_title=Voir le graphique en arbre
 view_results_assertion_error=Erreur d'assertion \: 
 view_results_assertion_failure=Echec d'assertion \: 
 view_results_assertion_failure_message=Message d'\u00E9chec d'assertion \: 
 view_results_autoscroll=D\u00E9filement automatique ?
 view_results_childsamples=Echantillons enfants?
 view_results_connect_time=Temps \u00E9tablissement connexion \: 
 view_results_datatype=Type de donn\u00E9es ("text"|"bin"|"")\: 
 view_results_desc=Affiche les r\u00E9sultats d'un \u00E9chantillon dans un arbre de r\u00E9sultats
 view_results_error_count=Compteur erreur\: 
 view_results_fields=champs \:
 view_results_in_table=Tableau de r\u00E9sultats
 view_results_latency=Latence \: 
 view_results_load_time=Temps de r\u00E9ponse \: 
 view_results_render=Rendu \:
 view_results_render_browser=Navigateur
 view_results_render_document=Document
 view_results_render_html=HTML
 view_results_render_html_embedded=HTML et ressources
 view_results_render_html_formatted=Code source HTML Format\u00E9
 view_results_render_json=JSON
 view_results_render_text=Texte brut
 view_results_render_xml=XML
 view_results_request_headers=Ent\u00EAtes de requ\u00EAte \:
 view_results_response_code=Code de retour \: 
 view_results_response_headers=Ent\u00EAtes de r\u00E9ponse \:
 view_results_response_message=Message de retour \: 
 view_results_response_missing_tika=Manque l'archive tika-app.jar dans le classpath. Impossible de convertir en texte ce type de document.\nT\u00E9l\u00E9charger le fichier tika-app-x.x.jar depuis http\://tika.apache.org/download.html\nPuis ajouter ce fichier dans le r\u00E9pertoire <JMeter>/lib
 view_results_response_partial_message=D\u00E9but du message\:
 view_results_response_too_large_message=R\u00E9ponse d\u00E9passant la taille maximale d'affichage. Taille \: 
 view_results_sample_count=Compteur \u00E9chantillon \: 
 view_results_sample_start=Date d\u00E9but \u00E9chantillon \: 
 view_results_search_pane=Volet recherche 
 view_results_sent_bytes=Octets envoy\u00E9s:
 view_results_size_body_in_bytes=Taille du corps en octets \: 
 view_results_size_headers_in_bytes=Taille de l'ent\u00EAte en octets \: 
 view_results_size_in_bytes=Taille en octets \: 
 view_results_tab_assertion=R\u00E9sultats d'assertion
 view_results_tab_request=Requ\u00EAte
 view_results_tab_response=Donn\u00E9es de r\u00E9ponse
 view_results_tab_sampler=R\u00E9sultat de l'\u00E9chantillon
 view_results_table_fields_key=Champ suppl\u00E9mentaire
 view_results_table_fields_value=Valeur
 view_results_table_headers_key=Ent\u00EAtes de r\u00E9ponse
 view_results_table_headers_value=Valeur
 view_results_table_request_headers_key=Ent\u00EAtes de requ\u00EAte
 view_results_table_request_headers_value=Valeur
 view_results_table_request_http_cookie=Cookie
 view_results_table_request_http_host=H\u00F4te
 view_results_table_request_http_method=M\u00E9thode
 view_results_table_request_http_nohttp=N'est pas un \u00E9chantillon HTTP
 view_results_table_request_http_path=Chemin
 view_results_table_request_http_port=Port
 view_results_table_request_http_protocol=Protocole
 view_results_table_request_params_key=Nom de param\u00E8tre
 view_results_table_request_params_value=Valeur
 view_results_table_request_raw_nodata=Pas de donn\u00E9es \u00E0 afficher
 view_results_table_request_tab_http=HTTP
 view_results_table_request_tab_raw=Brut
 view_results_table_result_tab_parsed=D\u00E9cod\u00E9
 view_results_table_result_tab_raw=Brut
 view_results_thread_name=Nom d'unit\u00E9 \: 
 view_results_title=Voir les r\u00E9sultats
 view_results_tree_title=Arbre de r\u00E9sultats
 warning=Attention \!
 web_cannot_convert_parameters_to_raw=Ne peut pas convertir les param\u00E8tres en Donn\u00E9es POST brutes\ncar l'un des param\u00E8tres a un nom.
 web_cannot_switch_tab=Vous ne pouvez pas basculer car ces donn\u00E9es ne peuvent \u00EAtre converties.\nVider les donn\u00E9es pour basculer.
 web_parameters_lost_message=Basculer vers les Donn\u00E9es POST brutes va convertir en format brut\net perdre le format tabulaire quand vous s\u00E9lectionnerez un autre noeud\nou \u00E0 la sauvegarde du plan de test, \u00EAtes-vous s\u00FBr ?
 web_proxy_server_title=Requ\u00EAte via un serveur proxy
 web_request=Requ\u00EAte HTTP
 web_server=Serveur web
 web_server_client=Impl\u00E9mentation client \:
 web_server_domain=Nom ou adresse IP \:
 web_server_port=Port \:
 web_server_timeout_connect=Connexion \:
 web_server_timeout_response=R\u00E9ponse \:
 web_server_timeout_title=D\u00E9lai expiration (ms)
 web_testing2_title=Requ\u00EAte HTTP HTTPClient
 web_testing_advanced=Avanc\u00E9e
 web_testing_basic=Basique
 web_testing_concurrent_download=T\u00E9l\u00E9chargements en parall\u00E8le. Nombre \:
 web_testing_embedded_url_pattern=Les URL \u00E0 inclure doivent correspondre \u00E0 \:
 web_testing_retrieve_images=R\u00E9cup\u00E9rer les ressources incluses
 web_testing_retrieve_title=Ressources incluses dans les pages HTML
 web_testing_source_ip=Adresse source
 web_testing_source_ip_device=Interface
 web_testing_source_ip_device_ipv4=Interface IPv4
 web_testing_source_ip_device_ipv6=Interface IPv6
 web_testing_source_ip_hostname=IP/Nom d'h\u00F4te
 web_testing_title=Requ\u00EAte HTTP
 while_controller_label=Condition (fonction ou variable) \:
 while_controller_title=Contr\u00F4leur Tant Que
 workbench_title=Plan de travail
 xml_assertion_title=Assertion XML
 xml_download_dtds=R\u00E9cup\u00E9rer les DTD externes
 xml_namespace_button=Utiliser les espaces de noms
 xml_tolerant_button=Utiliser Tidy (analyseur tol\u00E9rant)
 xml_validate_button=Validation XML
 xml_whitespace_button=Ignorer les espaces
 xmlschema_assertion_label=Nom de fichier \: 
 xmlschema_assertion_title=Assertion Sch\u00E9ma XML
 xpath_assertion_button=Valider
 xpath_assertion_check=V\u00E9rifier l'expression XPath
 xpath_assertion_error=Erreur avec XPath
 xpath_assertion_failed=Expression XPath invalide
 xpath_assertion_label=XPath
 xpath_assertion_negate=Vrai si aucune correspondance trouv\u00E9e
 xpath_assertion_option=Options d'analyse XML
 xpath_assertion_test=V\u00E9rificateur XPath
 xpath_assertion_tidy=Essayer et nettoyer l'entr\u00E9e
 xpath_assertion_title=Assertion XPath
 xpath_assertion_valid=Expression XPath valide
 xpath_assertion_validation=Valider le code XML \u00E0 travers le fichier DTD
 xpath_assertion_whitespace=Ignorer les espaces
 xpath_expression=Expression XPath de correspondance
 xpath_extractor_fragment=Retourner le fragment XPath entier au lieu du contenu
 xpath_extractor_query=Requ\u00EAte XPath \:
 xpath_extractor_title=Extracteur XPath
 xpath_file_file_name=Fichier XML contenant les valeurs
 xpath_tester=Testeur XPath
 xpath_tester_button_test=Tester
 xpath_tester_field=Expression XPath
 xpath_tester_fragment=Retourner le fragment XPath entier au lieu du contenu ?
 xpath_tester_no_text=Les donn\u00E9es de r\u00E9ponse ne sont pas du texte.
 xpath_tester_title=Testeur XPath
 xpath_tidy_quiet=Silencieux
 xpath_tidy_report_errors=Rapporter les erreurs
 xpath_tidy_show_warnings=Afficher les alertes
 you_must_enter_a_valid_number=Vous devez entrer un nombre valide
 zh_cn=Chinois (simplifi\u00E9)
 zh_tw=Chinois (traditionnel)
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ClientPool.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ClientPool.java
index 399ea9335..0253179d2 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ClientPool.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/client/ClientPool.java
@@ -1,83 +1,91 @@
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
 
 package org.apache.jmeter.protocol.jms.client;
 
 import java.io.Closeable;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 /**
  *
  * ClientPool holds the client instances in an ArrayList. The main purpose of
  * this is to make it easier to clean up all the instances at the end of a test.
  * If we didn't do this, threads might become zombie.
  * 
  * N.B. This class needs to be fully synchronized as it is called from sample threads
  * and the thread that runs testEnded() methods.
  */
 public class ClientPool {
 
     //GuardedBy("this")
     private static final ArrayList<Closeable> clients = new ArrayList<>();
 
     //GuardedBy("this")
     private static final Map<Object, Object> client_map = new ConcurrentHashMap<>();
 
     /**
      * Add a ReceiveClient to the ClientPool. This is so that we can make sure
      * to close all clients and make sure all threads are destroyed.
      *
      * @param client the ReceiveClient to add
      */
     public static synchronized void addClient(Closeable client) {
         clients.add(client);
     }
 
     /**
      * Clear all the clients created by either Publish or Subscribe sampler. We
      * need to do this to make sure all the threads creatd during the test are
      * destroyed and cleaned up. In some cases, the client provided by the
      * manufacturer of the JMS server may have bugs and some threads may become
      * zombie. In those cases, it is not the responsibility of JMeter for those
      * bugs.
      */
     public static synchronized void clearClient() {
         for (Closeable client : clients) {
             try {
                 client.close();
             } catch (IOException e) {
                 // Ignored
             }
             client = null;
         }
         clients.clear();
         client_map.clear();
     }
 
     // TODO Method with 0 reference, really useful ?
     public static void put(Object key, Object client) {
         client_map.put(key, client);
     }
 
     // TODO Method with 0 reference, really useful ?
     public static Object get(Object key) {
         return client_map.get(key);
     }
+
+    /**
+     * Remove publisher from clients
+     * @param publisher {@link Publisher}
+     */
+    public static synchronized void removeClient(Publisher publisher) {
+        clients.remove(publisher);
+    }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java
index 9e28e2e9c..e68370b04 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSPublisherGui.java
@@ -1,388 +1,395 @@
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
 
 import javax.swing.Box;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.gui.util.FilePanel;
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.JLabeledRadioI18N;
 import org.apache.jmeter.gui.util.JSyntaxTextArea;
 import org.apache.jmeter.gui.util.JTextScrollPane;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.jms.sampler.JMSProperties;
 import org.apache.jmeter.protocol.jms.sampler.PublisherSampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledPasswordField;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 /**
  * This is the GUI for JMS Publisher
  *
  */
 public class JMSPublisherGui extends AbstractSamplerGui implements ChangeListener {
 
     private static final long serialVersionUID = 241L;
 
     //++ These names are used in the JMX files, and must not be changed
     /** Take source from the named file */
     public static final String USE_FILE_RSC   = "jms_use_file"; //$NON-NLS-1$
     /** Take source from a random file */
     public static final String USE_RANDOM_RSC = "jms_use_random_file"; //$NON-NLS-1$
     /** Take source from the text area */
     private static final String USE_TEXT_RSC   = "jms_use_text"; //$NON-NLS-1$
 
     /** Create a TextMessage */
     public static final String TEXT_MSG_RSC = "jms_text_message"; //$NON-NLS-1$
     /** Create a MapMessage */
     public static final String MAP_MSG_RSC = "jms_map_message"; //$NON-NLS-1$
     /** Create an ObjectMessage */
     public static final String OBJECT_MSG_RSC = "jms_object_message"; //$NON-NLS-1$
     /** Create a BytesMessage */
     public static final String BYTES_MSG_RSC = "jms_bytes_message"; //$NON-NLS-1$
     //-- End of names used in JMX files
 
     // Button group resources when Bytes Message is selected
     private static final String[] CONFIG_ITEMS_BYTES_MSG = { USE_FILE_RSC, USE_RANDOM_RSC};
 
     // Button group resources
     private static final String[] CONFIG_ITEMS = { USE_FILE_RSC, USE_RANDOM_RSC, USE_TEXT_RSC };
 
     private static final String[] MSGTYPES_ITEMS = { TEXT_MSG_RSC, MAP_MSG_RSC, OBJECT_MSG_RSC, BYTES_MSG_RSC };
 
     private final JCheckBox useProperties = new JCheckBox(JMeterUtils.getResString("jms_use_properties_file"), false); //$NON-NLS-1$
 
     private final JLabeledRadioI18N configChoice = new JLabeledRadioI18N("jms_config", CONFIG_ITEMS, USE_TEXT_RSC); //$NON-NLS-1$
 
     private final JLabeledTextField jndiICF = new JLabeledTextField(JMeterUtils.getResString("jms_initial_context_factory")); //$NON-NLS-1$
 
     private final JLabeledTextField urlField = new JLabeledTextField(JMeterUtils.getResString("jms_provider_url")); //$NON-NLS-1$
 
     private final JLabeledTextField jndiConnFac = new JLabeledTextField(JMeterUtils.getResString("jms_connection_factory")); //$NON-NLS-1$
 
     private final JLabeledTextField jmsDestination = new JLabeledTextField(JMeterUtils.getResString("jms_topic")); //$NON-NLS-1$
 
     private final JLabeledTextField expiration = new JLabeledTextField(JMeterUtils.getResString("jms_expiration"),10); //$NON-NLS-1$
 
+    private final JLabeledTextField jmsErrorReconnectOnCodes =
+            new JLabeledTextField(JMeterUtils.getResString("jms_error_reconnect_on_codes")); // $NON-NLS-1$
+
     private final JLabeledTextField priority = new JLabeledTextField(JMeterUtils.getResString("jms_priority"),1); //$NON-NLS-1$
 
     private final JCheckBox useAuth = new JCheckBox(JMeterUtils.getResString("jms_use_auth"), false); //$NON-NLS-1$
 
     private final JLabeledTextField jmsUser = new JLabeledTextField(JMeterUtils.getResString("jms_user")); //$NON-NLS-1$
 
     private final JLabeledTextField jmsPwd = new JLabeledPasswordField(JMeterUtils.getResString("jms_pwd")); //$NON-NLS-1$
 
     private final JLabeledTextField iterations = new JLabeledTextField(JMeterUtils.getResString("jms_itertions")); //$NON-NLS-1$
 
     private final FilePanel messageFile = new FilePanel(JMeterUtils.getResString("jms_file")); //$NON-NLS-1$
 
     private final FilePanel randomFile = new FilePanel(JMeterUtils.getResString("jms_random_file"), true); //$NON-NLS-1$
 
     private final JSyntaxTextArea textMessage = JSyntaxTextArea.getInstance(10, 50); // $NON-NLS-1$
 
     private final JLabeledRadioI18N msgChoice = new JLabeledRadioI18N("jms_message_type", MSGTYPES_ITEMS, TEXT_MSG_RSC); //$NON-NLS-1$
     
     private final JCheckBox useNonPersistentDelivery = new JCheckBox(JMeterUtils.getResString("jms_use_non_persistent_delivery"),false); //$NON-NLS-1$
 
     // These are the names of properties used to define the labels
     private static final String DEST_SETUP_STATIC = "jms_dest_setup_static"; // $NON-NLS-1$
 
     private static final String DEST_SETUP_DYNAMIC = "jms_dest_setup_dynamic"; // $NON-NLS-1$
     // Button group resources
     private static final String[] DEST_SETUP_ITEMS = { DEST_SETUP_STATIC, DEST_SETUP_DYNAMIC };
 
     private final JLabeledRadioI18N destSetup =
         new JLabeledRadioI18N("jms_dest_setup", DEST_SETUP_ITEMS, DEST_SETUP_STATIC); // $NON-NLS-1$
 
     private JMSPropertiesPanel jmsPropertiesPanel;
 
     public JMSPublisherGui() {
         init();
     }
 
     /**
      * the name of the property for the JMSPublisherGui is jms_publisher.
      */
     @Override
     public String getLabelResource() {
         return "jms_publisher"; //$NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
       PublisherSampler sampler = new PublisherSampler();
       setupSamplerProperties(sampler);
 
       return sampler;
   }
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement s) {
         PublisherSampler sampler = (PublisherSampler) s;
         setupSamplerProperties(sampler);
         sampler.setDestinationStatic(destSetup.getText().equals(DEST_SETUP_STATIC));
     }
 
     /**
      * Initialize the provided {@link PublisherSampler} with all the values as configured in the GUI.
      * 
      * @param sampler {@link PublisherSampler} instance
      */
     private void setupSamplerProperties(final PublisherSampler sampler) {
       super.configureTestElement(sampler);
       sampler.setUseJNDIProperties(String.valueOf(useProperties.isSelected()));
       sampler.setJNDIIntialContextFactory(jndiICF.getText());
       sampler.setProviderUrl(urlField.getText());
       sampler.setConnectionFactory(jndiConnFac.getText());
       sampler.setDestination(jmsDestination.getText());
       sampler.setExpiration(expiration.getText());
+      sampler.setReconnectionErrorCodes(jmsErrorReconnectOnCodes.getText());
       sampler.setPriority(priority.getText());
       sampler.setUsername(jmsUser.getText());
       sampler.setPassword(jmsPwd.getText());
       sampler.setTextMessage(textMessage.getText());
       sampler.setInputFile(messageFile.getFilename());
       sampler.setRandomPath(randomFile.getFilename());
       sampler.setConfigChoice(configChoice.getText());
       sampler.setMessageChoice(msgChoice.getText());
       sampler.setIterations(iterations.getText());
       sampler.setUseAuth(useAuth.isSelected());
       sampler.setUseNonPersistentDelivery(useNonPersistentDelivery.isSelected());
      
       JMSProperties args = (JMSProperties) jmsPropertiesPanel.createTestElement();
       sampler.setJMSProperties(args);
     }
 
     /**
      * init() adds jndiICF to the mainPanel. The class reuses logic from
      * SOAPSampler, since it is common.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel mainPanel = new VerticalPanel();
         add(mainPanel, BorderLayout.CENTER);
         
         mainPanel.add(useProperties);
         mainPanel.add(jndiICF);
         mainPanel.add(urlField);
         mainPanel.add(jndiConnFac);
         mainPanel.add(createDestinationPane());
         mainPanel.add(createAuthPane());
         mainPanel.add(createPriorityAndExpiration());
+        mainPanel.add(jmsErrorReconnectOnCodes);
         mainPanel.add(iterations);
 
         jmsPropertiesPanel = new JMSPropertiesPanel(); //$NON-NLS-1$
         mainPanel.add(jmsPropertiesPanel);
 
         configChoice.setLayout(new BoxLayout(configChoice, BoxLayout.X_AXIS));
         mainPanel.add(configChoice);
         msgChoice.setLayout(new BoxLayout(msgChoice, BoxLayout.X_AXIS));
         mainPanel.add(msgChoice);
         mainPanel.add(messageFile);
         mainPanel.add(randomFile);
 
         JPanel messageContentPanel = new JPanel(new BorderLayout());
         messageContentPanel.add(new JLabel(JMeterUtils.getResString("jms_text_area")), BorderLayout.NORTH);
         messageContentPanel.add(JTextScrollPane.getInstance(textMessage), BorderLayout.CENTER);
 
         mainPanel.add(messageContentPanel);
         useProperties.addChangeListener(this);
         useAuth.addChangeListener(this);
         configChoice.addChangeListener(this);
         msgChoice.addChangeListener(this);
     }
 
     @Override
     public void clearGui(){
         super.clearGui();
         useProperties.setSelected(false);
         jndiICF.setText(""); // $NON-NLS-1$
         urlField.setText(""); // $NON-NLS-1$
         jndiConnFac.setText(""); // $NON-NLS-1$
         jmsDestination.setText(""); // $NON-NLS-1$
         expiration.setText(""); // $NON-NLS-1$
+        jmsErrorReconnectOnCodes.setText("");
         priority.setText(""); // $NON-NLS-1$
         jmsUser.setText(""); // $NON-NLS-1$
         jmsPwd.setText(""); // $NON-NLS-1$
         textMessage.setInitialText(""); // $NON-NLS-1$
         messageFile.setFilename(""); // $NON-NLS-1$
         randomFile.setFilename(""); // $NON-NLS-1$
         msgChoice.setText(""); // $NON-NLS-1$
         configChoice.setText(USE_TEXT_RSC);
         updateConfig(USE_TEXT_RSC);
         msgChoice.setText(TEXT_MSG_RSC);
         iterations.setText("1"); // $NON-NLS-1$
         useAuth.setSelected(false);
         jmsUser.setEnabled(false);
         jmsPwd.setEnabled(false);
         destSetup.setText(DEST_SETUP_STATIC);
         useNonPersistentDelivery.setSelected(false);
         jmsPropertiesPanel.clearGui();
     }
 
     /**
      * the implementation loads the URL and the soap action for the request.
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         PublisherSampler sampler = (PublisherSampler) el;
         useProperties.setSelected(sampler.getUseJNDIPropertiesAsBoolean());
         jndiICF.setText(sampler.getJNDIInitialContextFactory());
         urlField.setText(sampler.getProviderUrl());
         jndiConnFac.setText(sampler.getConnectionFactory());
         jmsDestination.setText(sampler.getDestination());
         jmsUser.setText(sampler.getUsername());
         jmsPwd.setText(sampler.getPassword());
         textMessage.setInitialText(sampler.getTextMessage());
         textMessage.setCaretPosition(0);
         messageFile.setFilename(sampler.getInputFile());
         randomFile.setFilename(sampler.getRandomPath());
         configChoice.setText(sampler.getConfigChoice());
         msgChoice.setText(sampler.getMessageChoice());
         iterations.setText(sampler.getIterations());
         expiration.setText(sampler.getExpiration());
+        jmsErrorReconnectOnCodes.setText(sampler.getReconnectionErrorCodes());
         priority.setText(sampler.getPriority());
         useAuth.setSelected(sampler.isUseAuth());
         jmsUser.setEnabled(useAuth.isSelected());
         jmsPwd.setEnabled(useAuth.isSelected());
         destSetup.setText(sampler.isDestinationStatic() ? DEST_SETUP_STATIC : DEST_SETUP_DYNAMIC);
         useNonPersistentDelivery.setSelected(sampler.getUseNonPersistentDelivery());
         jmsPropertiesPanel.configure(sampler.getJMSProperties());
         updateChoice(msgChoice.getText());
         updateConfig(sampler.getConfigChoice());
     }
 
     /**
      * When a widget state changes, it will notify this class so we can
      * enable/disable the correct items.
      */
     @Override
     public void stateChanged(ChangeEvent event) {
         if (event.getSource() == configChoice) {
             updateConfig(configChoice.getText());
         } else if (event.getSource() == msgChoice) {
             updateChoice(msgChoice.getText());
         } else if (event.getSource() == useProperties) {
             final boolean isUseProperties = useProperties.isSelected();
             jndiICF.setEnabled(!isUseProperties);
             urlField.setEnabled(!isUseProperties);
             useAuth.setEnabled(!isUseProperties);
         } else if (event.getSource() == useAuth) {
             jmsUser.setEnabled(useAuth.isSelected() && useAuth.isEnabled());
             jmsPwd.setEnabled(useAuth.isSelected()  && useAuth.isEnabled());
         }
     }
     /**
      * Update choice contains the actual logic for hiding or showing Textarea if Bytes message
      * is selected
      *
      * @param command
      * @since 2.9
      */
     private void updateChoice(String command) {
         String oldChoice = configChoice.getText();
         if (BYTES_MSG_RSC.equals(command)) {
             String newChoice = USE_TEXT_RSC.equals(oldChoice) ? 
                     USE_FILE_RSC : oldChoice;
             configChoice.resetButtons(CONFIG_ITEMS_BYTES_MSG, newChoice);
             textMessage.setEnabled(false);
         } else {
             configChoice.resetButtons(CONFIG_ITEMS, oldChoice);
             textMessage.setEnabled(true);
         }
         validate();
     }
     /**
      * Update config contains the actual logic for enabling or disabling text
      * message, file or random path.
      *
      * @param command
      */
     private void updateConfig(String command) {
         if (command.equals(USE_TEXT_RSC)) {
             textMessage.setEnabled(true);
             messageFile.enableFile(false);
             randomFile.enableFile(false);
         } else if (command.equals(USE_RANDOM_RSC)) {
             textMessage.setEnabled(false);
             messageFile.enableFile(false);
             randomFile.enableFile(true);
         } else {
             textMessage.setEnabled(false);
             messageFile.enableFile(true);
             randomFile.enableFile(false);
         }
     }
     
     /**
      * @return JPanel that contains destination infos
      */
     private JPanel createDestinationPane() {
         JPanel pane = new JPanel(new BorderLayout(3, 0));
         pane.add(jmsDestination, BorderLayout.WEST);
         destSetup.setLayout(new BoxLayout(destSetup, BoxLayout.X_AXIS));
         pane.add(destSetup, BorderLayout.CENTER);
         pane.add(useNonPersistentDelivery, BorderLayout.EAST);
         return pane;
     }
     
     /**
      * @return JPanel Panel with checkbox to choose auth , user and password
      */
     private JPanel createAuthPane() {
         JPanel pane = new JPanel();
         pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
         pane.add(useAuth);
         pane.add(Box.createHorizontalStrut(10));
         pane.add(jmsUser);
         pane.add(Box.createHorizontalStrut(10));
         pane.add(jmsPwd);
         return pane;
     }
 
     /**
      * @return JPanel Panel for priority and expiration
      */
     private JPanel createPriorityAndExpiration() {
         JPanel panel = new HorizontalPanel();
         panel.add(expiration);
         panel.add(priority);
         return panel;
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSubscriberGui.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSubscriberGui.java
index a86ad212d..02b6e2aad 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSubscriberGui.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/control/gui/JMSSubscriberGui.java
@@ -1,286 +1,301 @@
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
 
 import javax.naming.Context;
 import javax.swing.BoxLayout;
 import javax.swing.JCheckBox;
 import javax.swing.JPanel;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 
 import org.apache.jmeter.gui.util.HorizontalPanel;
 import org.apache.jmeter.gui.util.JLabeledRadioI18N;
 import org.apache.jmeter.gui.util.VerticalPanel;
 import org.apache.jmeter.protocol.jms.sampler.SubscriberSampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.JLabeledPasswordField;
 import org.apache.jorphan.gui.JLabeledTextField;
 
 /**
  * This is the GUI for JMS Subscriber <br>
  *
  */
 public class JMSSubscriberGui extends AbstractSamplerGui implements ChangeListener {
 
     private static final long serialVersionUID = 240L;
 
     private final JCheckBox useProperties =
         new JCheckBox(JMeterUtils.getResString("jms_use_properties_file"), false); // $NON-NLS-1$
 
     private final JLabeledTextField jndiICF =
         new JLabeledTextField(JMeterUtils.getResString("jms_initial_context_factory")); // $NON-NLS-1$
 
     private final JLabeledTextField urlField =
         new JLabeledTextField(JMeterUtils.getResString("jms_provider_url")); // $NON-NLS-1$
 
     private final JLabeledTextField jndiConnFac =
         new JLabeledTextField(JMeterUtils.getResString("jms_connection_factory")); // $NON-NLS-1$
 
     private final JLabeledTextField jmsDestination =
         new JLabeledTextField(JMeterUtils.getResString("jms_topic")); // $NON-NLS-1$
     
     private final JLabeledTextField jmsDurableSubscriptionId =
         new JLabeledTextField(JMeterUtils.getResString("jms_durable_subscription_id")); // $NON-NLS-1$
 
     private final JLabeledTextField jmsClientId =
         new JLabeledTextField(JMeterUtils.getResString("jms_client_id")); // $NON-NLS-1$
 
     private final JLabeledTextField jmsSelector =
         new JLabeledTextField(JMeterUtils.getResString("jms_selector")); // $NON-NLS-1$
 
     private final JLabeledTextField jmsUser =
         new JLabeledTextField(JMeterUtils.getResString("jms_user")); // $NON-NLS-1$
 
     private final JLabeledTextField jmsPwd =
         new JLabeledPasswordField(JMeterUtils.getResString("jms_pwd")); // $NON-NLS-1$
 
     private final JLabeledTextField samplesToAggregate =
         new JLabeledTextField(JMeterUtils.getResString("jms_itertions")); // $NON-NLS-1$
 
     private final JCheckBox useAuth =
         new JCheckBox(JMeterUtils.getResString("jms_use_auth"), false); //$NON-NLS-1$
 
     private final JCheckBox storeResponse =
         new JCheckBox(JMeterUtils.getResString("jms_store_response"), true); // $NON-NLS-1$
 
     private final JLabeledTextField timeout = 
         new JLabeledTextField(JMeterUtils.getResString("jms_timeout")); //$NON-NLS-1$
 
+    private final JLabeledTextField jmsErrorPauseBetween =
+        new JLabeledTextField(JMeterUtils.getResString("jms_error_pause_between")); // $NON-NLS-1$
+
+    private final JLabeledTextField jmsErrorReconnectOnCodes =
+        new JLabeledTextField(JMeterUtils.getResString("jms_error_reconnect_on_codes")); // $NON-NLS-1$
+
     private final JLabeledTextField separator = 
         new JLabeledTextField(JMeterUtils.getResString("jms_separator")); //$NON-NLS-1$
 
     //++ Do not change these strings; they are used in JMX files to record the button settings
     public static final String RECEIVE_RSC = "jms_subscriber_receive"; // $NON-NLS-1$
 
     public static final String ON_MESSAGE_RSC = "jms_subscriber_on_message"; // $NON-NLS-1$
     //--
 
     // Button group resources
     private static final String[] CLIENT_ITEMS = { RECEIVE_RSC, ON_MESSAGE_RSC };
 
     private final JLabeledRadioI18N clientChoice =
         new JLabeledRadioI18N("jms_client_type", CLIENT_ITEMS, RECEIVE_RSC); // $NON-NLS-1$
 
     private final JCheckBox stopBetweenSamples =
         new JCheckBox(JMeterUtils.getResString("jms_stop_between_samples"), true); // $NON-NLS-1$
     
     // These are the names of properties used to define the labels
     private static final String DEST_SETUP_STATIC = "jms_dest_setup_static"; // $NON-NLS-1$
 
     private static final String DEST_SETUP_DYNAMIC = "jms_dest_setup_dynamic"; // $NON-NLS-1$
     // Button group resources
     private static final String[] DEST_SETUP_ITEMS = { DEST_SETUP_STATIC, DEST_SETUP_DYNAMIC };
 
     private final JLabeledRadioI18N destSetup =
         new JLabeledRadioI18N("jms_dest_setup", DEST_SETUP_ITEMS, DEST_SETUP_STATIC); // $NON-NLS-1$
     
     public JMSSubscriberGui() {
         init();
     }
 
     @Override
     public String getLabelResource() {
         return "jms_subscriber_title"; // $NON-NLS-1$
     }
 
     /**
      * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
      */
     @Override
     public TestElement createTestElement() {
         SubscriberSampler sampler = new SubscriberSampler();
         modifyTestElement(sampler);
         return sampler;
     }
 
     /**
      * Modifies a given TestElement to mirror the data in the gui components.
      *
      * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
      */
     @Override
     public void modifyTestElement(TestElement s) {
         SubscriberSampler sampler = (SubscriberSampler) s;
         super.configureTestElement(sampler);
         sampler.setUseJNDIProperties(String.valueOf(useProperties.isSelected()));
         sampler.setJNDIIntialContextFactory(jndiICF.getText());
         sampler.setProviderUrl(urlField.getText());
         sampler.setConnectionFactory(jndiConnFac.getText());
         sampler.setDestination(jmsDestination.getText());
         sampler.setDurableSubscriptionId(jmsDurableSubscriptionId.getText());
         sampler.setClientID(jmsClientId.getText());
         sampler.setJmsSelector(jmsSelector.getText());
         sampler.setUsername(jmsUser.getText());
         sampler.setPassword(jmsPwd.getText());
         sampler.setUseAuth(useAuth.isSelected());
         sampler.setIterations(samplesToAggregate.getText());
         sampler.setReadResponse(String.valueOf(storeResponse.isSelected()));
         sampler.setClientChoice(clientChoice.getText());
         sampler.setStopBetweenSamples(stopBetweenSamples.isSelected());
         sampler.setTimeout(timeout.getText());
+        sampler.setReconnectionErrorCodes(jmsErrorReconnectOnCodes.getText());
+        sampler.setPauseBetweenErrors(jmsErrorPauseBetween.getText());
         sampler.setDestinationStatic(destSetup.getText().equals(DEST_SETUP_STATIC));
         sampler.setSeparator(separator.getText());
     }
 
     /**
      * init() adds jndiICF to the mainPanel. The class reuses logic from
      * SOAPSampler, since it is common.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new BorderLayout());
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         JPanel mainPanel = new VerticalPanel();
         add(mainPanel, BorderLayout.CENTER);
         
         jndiICF.setToolTipText(Context.INITIAL_CONTEXT_FACTORY);
         urlField.setToolTipText(Context.PROVIDER_URL);
         jmsUser.setToolTipText(Context.SECURITY_PRINCIPAL);
         jmsPwd.setToolTipText(Context.SECURITY_CREDENTIALS);
         mainPanel.add(useProperties);
         mainPanel.add(jndiICF);
         mainPanel.add(urlField);
         mainPanel.add(jndiConnFac);
         mainPanel.add(createDestinationPane());
         mainPanel.add(jmsDurableSubscriptionId);
         mainPanel.add(jmsClientId);
         mainPanel.add(jmsSelector);
         mainPanel.add(useAuth);
         mainPanel.add(jmsUser);
         mainPanel.add(jmsPwd);
         mainPanel.add(samplesToAggregate);
 
         mainPanel.add(storeResponse);
         mainPanel.add(timeout);
         
         JPanel choice = new HorizontalPanel();
         choice.add(clientChoice);
         choice.add(stopBetweenSamples);
         mainPanel.add(choice);
         mainPanel.add(separator);
         
+        mainPanel.add(jmsErrorReconnectOnCodes);
+        mainPanel.add(jmsErrorPauseBetween);
+
         useProperties.addChangeListener(this);
         useAuth.addChangeListener(this);
     }
 
     /**
      * the implementation loads the URL and the soap action for the request.
      */
     @Override
     public void configure(TestElement el) {
         super.configure(el);
         SubscriberSampler sampler = (SubscriberSampler) el;
         useProperties.setSelected(sampler.getUseJNDIPropertiesAsBoolean());
         jndiICF.setText(sampler.getJNDIInitialContextFactory());
         urlField.setText(sampler.getProviderUrl());
         jndiConnFac.setText(sampler.getConnectionFactory());
         jmsDestination.setText(sampler.getDestination());
         jmsDurableSubscriptionId.setText(sampler.getDurableSubscriptionId());
         jmsClientId.setText(sampler.getClientId());
         jmsSelector.setText(sampler.getJmsSelector());
         jmsUser.setText(sampler.getUsername());
         jmsPwd.setText(sampler.getPassword());
         samplesToAggregate.setText(sampler.getIterations());
         useAuth.setSelected(sampler.isUseAuth());
         jmsUser.setEnabled(useAuth.isSelected());
         jmsPwd.setEnabled(useAuth.isSelected());
         storeResponse.setSelected(sampler.getReadResponseAsBoolean());
         clientChoice.setText(sampler.getClientChoice());
         stopBetweenSamples.setSelected(sampler.isStopBetweenSamples());
         timeout.setText(sampler.getTimeout());
         separator.setText(sampler.getSeparator());
         destSetup.setText(sampler.isDestinationStatic() ? DEST_SETUP_STATIC : DEST_SETUP_DYNAMIC);
+        jmsErrorReconnectOnCodes.setText(sampler.getReconnectionErrorCodes());
+        jmsErrorPauseBetween.setText(sampler.getPauseBetweenErrors());
     }
 
     @Override
     public void clearGui(){
         super.clearGui();
         useProperties.setSelected(false); // $NON-NLS-1$
         jndiICF.setText(""); // $NON-NLS-1$
         urlField.setText(""); // $NON-NLS-1$
         jndiConnFac.setText(""); // $NON-NLS-1$
         jmsDestination.setText(""); // $NON-NLS-1$
         jmsDurableSubscriptionId.setText(""); // $NON-NLS-1$
         jmsClientId.setText(""); // $NON-NLS-1$
         jmsSelector.setText(""); // $NON-NLS-1$
         jmsUser.setText(""); // $NON-NLS-1$
         jmsPwd.setText(""); // $NON-NLS-1$
         samplesToAggregate.setText("1"); // $NON-NLS-1$
         timeout.setText(""); // $NON-NLS-1$
         separator.setText(""); // $NON-NLS-1$
         useAuth.setSelected(false);
         jmsUser.setEnabled(false);
         jmsPwd.setEnabled(false);
         storeResponse.setSelected(true);
         clientChoice.setText(RECEIVE_RSC);
         stopBetweenSamples.setSelected(false);
         destSetup.setText(DEST_SETUP_STATIC);
+        jmsErrorReconnectOnCodes.setText("");
+        jmsErrorPauseBetween.setText("");
     }
 
     /**
      * When the state of a widget changes, it will notify the gui. the method
      * then enables or disables certain parameters.
      */
     @Override
     public void stateChanged(ChangeEvent event) {
         if (event.getSource() == useProperties) {
             final boolean isUseProperties = useProperties.isSelected();
             jndiICF.setEnabled(!isUseProperties);
             urlField.setEnabled(!isUseProperties);
             useAuth.setEnabled(!isUseProperties);
         } else if (event.getSource() == useAuth) {
             jmsUser.setEnabled(useAuth.isSelected() && useAuth.isEnabled());
             jmsPwd.setEnabled(useAuth.isSelected()  && useAuth.isEnabled());
         }
     }
     
     private JPanel createDestinationPane() {
         JPanel pane = new JPanel(new BorderLayout(3, 0));
         pane.add(jmsDestination, BorderLayout.CENTER);
         destSetup.setLayout(new BoxLayout(destSetup, BoxLayout.X_AXIS));
         pane.add(destSetup, BorderLayout.EAST);
         return pane;
     }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java
index 32b0088a3..cd02fdd86 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/BaseJMSSampler.java
@@ -1,384 +1,415 @@
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
+import java.util.function.Predicate;
+import java.util.regex.Pattern;
 
 import javax.jms.DeliveryMode;
 import javax.jms.Destination;
 import javax.jms.JMSException;
 import javax.jms.Message;
 
+import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.samplers.AbstractSampler;
 import org.apache.jmeter.samplers.Entry;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  *
  * BaseJMSSampler is an abstract class which provides implementation for common
  * properties. Rather than duplicate the code, it's contained in the base class.
  */
 public abstract class BaseJMSSampler extends AbstractSampler {
 
     private static final long serialVersionUID = 240L;
     
     private static final Logger LOGGER = LoggingManager.getLoggerForClass(); 
 
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
 
+    /** Property name for regex of error codes which force reconnection **/
+    private static final String ERROR_RECONNECT_ON_CODES = "jms_error_reconnect_on_codes"; // $NON-NLS-1$
+    private transient Predicate<String> isReconnectErrorCode = e -> false;
+
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
+
+    public String getReconnectionErrorCodes() {
+        return getPropertyAsString(ERROR_RECONNECT_ON_CODES);
+    }
+
+    public void setReconnectionErrorCodes(String reconnectionErrorCodes) {
+        setProperty(ERROR_RECONNECT_ON_CODES, reconnectionErrorCodes);
+    }
+
+    public Predicate<String> getIsReconnectErrorCode() {
+        return isReconnectErrorCode;
+    }
+
+    /**
+     * 
+     */
+    protected void configureIsReconnectErrorCode() {
+        String regex = StringUtils.trimToEmpty(getReconnectionErrorCodes());
+        if (regex.isEmpty()) {
+            isReconnectErrorCode = e -> false;
+        } else {
+            isReconnectErrorCode = Pattern.compile(regex).asPredicate();
+        }
+    }
 }
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java
index 53725446b..c398c2400 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/PublisherSampler.java
@@ -1,591 +1,624 @@
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
+import java.io.PrintWriter;
 import java.io.Serializable;
+import java.io.StringWriter;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Objects;
+import java.util.Optional;
 
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.XStream;
 
 /**
  * This class implements the JMS Publisher sampler.
  */
 public class PublisherSampler extends BaseJMSSampler implements TestStateListener {
 
     private static final long serialVersionUID = 233L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
+        configureIsReconnectErrorCode();
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
-            } catch (JMSException e) {
-                result.setResponseMessage(e.toString());
-                return result;
-            } catch (NamingException e) {
-                result.setResponseMessage(e.toString());
+            } catch (JMSException | NamingException e) {
+                handleError(result, e, false);
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
+        } catch (JMSException e) {
+            handleError(result, e, true);
         } catch (Exception e) {
-            result.setResponseMessage(e.toString());
+            handleError(result, e, false);
         } finally {
             result.sampleEnd();            
         }
         return result;
     }
 
+    /**
+     * Fills in result and decide wether to reconnect or not depending on checkForReconnect 
+     * and underlying {@link JMSException#getErrorCode()}
+     * @param result {@link SampleResult}
+     * @param e {@link Exception}
+     * @param checkForReconnect if true and exception is a {@link JMSException}
+     */
+    private void handleError(SampleResult result, Exception e, boolean checkForReconnect) {
+        result.setSuccessful(false);
+        result.setResponseMessage(e.toString());
+
+        if (e instanceof JMSException) {
+            JMSException jms = (JMSException)e;
+
+            String errorCode = Optional.ofNullable(jms.getErrorCode()).orElse("");
+            if (checkForReconnect && publisher != null 
+                    && getIsReconnectErrorCode().test(errorCode)) {
+                ClientPool.removeClient(publisher);
+                IOUtils.closeQuietly(publisher);
+                publisher = null;
+            }
+
+            result.setResponseCode(errorCode);
+        }
+
+        StringWriter writer = new StringWriter();
+        e.printStackTrace(new PrintWriter(writer)); // NOSONAR We're getting it to put it in ResponseData 
+        result.setResponseData(writer.toString(), "UTF-8");
+    }
+
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
                 Method method = clazz.getMethod("valueOf", new Class<?>[]{String.class});
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
diff --git a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
index 9c7529947..1a1faa822 100644
--- a/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
+++ b/src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
@@ -1,512 +1,564 @@
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
+import java.util.Optional;
 
 import javax.jms.BytesMessage;
 import javax.jms.JMSException;
 import javax.jms.MapMessage;
 import javax.jms.Message;
 import javax.jms.ObjectMessage;
 import javax.jms.TextMessage;
 import javax.naming.NamingException;
 
+import org.apache.commons.io.IOUtils;
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
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
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
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
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
+    private static final String ERROR_PAUSE_BETWEEN = "jms_error_pause_between"; // $NON-NLS-1$
+    private static final String ERROR_PAUSE_BETWEEN_DEFAULT = ""; // $NON-NLS-1$
 
     
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
-        setupSeparator();
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
-        setupSeparator();
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
+            handleErrorAndAddTemporize(true);
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
-                log.warn("Error "+e.toString());
+                String errorCode = Optional.ofNullable(e.getErrorCode()).orElse("");
+                log.warn(String.format("Error [%s] %s", errorCode, e.toString()), e);
+
+                handleErrorAndAddTemporize(getIsReconnectErrorCode().test(errorCode));
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
+     * Try to reconnect if configured to or temporize if not or an exception occured
+     * @param reconnect
+     */
+    private void handleErrorAndAddTemporize(boolean reconnect) {
+        if (reconnect) {
+            cleanup();
+            initClient();
+        }
+
+        if (!reconnect || exceptionDuringInit != null) {
+            try {
+                long pause = getPauseBetweenErrorsAsLong();
+                if(pause > 0) {
+                    Thread.sleep(pause);
+                }
+            } catch (InterruptedException ie) {
+                log.warn(String.format("Interrupted %s", ie.toString()), ie);
+                Thread.currentThread().interrupt();
+                interrupted = true;
+            }
+        }
+    }
+
+    /**
+     * 
+     */
+    private void cleanup() {
+        IOUtils.closeQuietly(SUBSCRIBER);
+    }
+
+    /**
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
+        configureIsReconnectErrorCode();
+
         // Disabled thread start if listen on sample choice
         if (isDestinationStatic() || START_ON_SAMPLE) {
             timeout = getTimeoutAsLong();
             interrupted = false;
             exceptionDuringInit = null;
             useReceive = getClientChoice().equals(JMSSubscriberGui.RECEIVE_RSC);
             stopBetweenSamples = isStopBetweenSamples();
-            if (useReceive) {
-                try {
-                    initReceiveClient();
-                    if (!stopBetweenSamples){ // Don't start yet if stop between samples
-                        SUBSCRIBER.start();
-                    }
-                } catch (NamingException | JMSException e) {
-                    exceptionDuringInit = e;
-                }
+            setupSeparator();
+            initClient();
+        }
+    }
+
+    private void initClient() {
+        exceptionDuringInit = null;
+        try {
+            if(useReceive) {
+                initReceiveClient();
             } else {
-                try {
-                    initListenerClient();
-                    if (!stopBetweenSamples){ // Don't start yet if stop between samples
-                        SUBSCRIBER.start();
-                    }
-                } catch (JMSException | NamingException e) {
-                    exceptionDuringInit = e;
-                }
+                initListenerClient();
             }
-            if (exceptionDuringInit != null){
-                log.error("Could not initialise client",exceptionDuringInit);
+            if (!stopBetweenSamples) { // Don't start yet if stop between
+                                       // samples
+                SUBSCRIBER.start();
             }
+        } catch (NamingException | JMSException e) {
+            exceptionDuringInit = e;
+        }
+        
+        if (exceptionDuringInit != null) {
+            log.error("Could not initialise client", exceptionDuringInit);
         }
     }
-    
+
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
-            SUBSCRIBER.close();
+            cleanup();
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
 
+    public void setPauseBetweenErrors(String pause) {
+        setProperty(ERROR_PAUSE_BETWEEN, pause, ERROR_PAUSE_BETWEEN_DEFAULT);
+    }
+
+    public String getPauseBetweenErrors() {
+        return getPropertyAsString(ERROR_PAUSE_BETWEEN, ERROR_PAUSE_BETWEEN_DEFAULT);
+    }
+
+    public long getPauseBetweenErrorsAsLong() {
+        return getPropertyAsLong(ERROR_PAUSE_BETWEEN, DEFAULT_WAIT);
+    }
+
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b1f8cd252..7090a6ad1 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,309 +1,310 @@
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
 <!ENTITY rarr     "&#x02192;" >
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
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <ch_section>IMPORTANT CHANGE</ch_section>
 <p>
 Fill in some detail.
 </p>
 
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>Fill in improvements</li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>Documentation review and improvements for easier startup</li>
 <li>New <a href="usermanual/properties_reference.html">properties reference</a> documentation section</li>
 </ul>
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a JAVA 8 version to run.</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> 2 protected static fields (localhost and nonProxyHostSuffixSize) have been renamed to (LOCALHOST and NON_PROXY_HOST_SUFFIX_SIZE) 
         to follow static fields naming convention</li>
 </ul>
 
 <h3>Deprecated and removed elements or functions</h3>
 <p><note>These elements do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. The JMeter team advises not to use them anymore and migrate to their replacement.</note></p>
 <ul>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
+    <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
-    <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux@gmail.com).</li>
+    <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a <pr>245</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
-    <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux@gmail.com).</li>
+    <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to xxx-1.1 (from 0.2)</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr>Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of TestHTTPMirrorThread#testSleep(). Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
 <li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
-<li>Maxime Chassagneux (maxime.chassagneux@gmail.com)</li>
+<li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
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
diff --git a/xdocs/usermanual/component_reference.xml b/xdocs/usermanual/component_reference.xml
index 0b2eea86a..dea8413f9 100644
--- a/xdocs/usermanual/component_reference.xml
+++ b/xdocs/usermanual/component_reference.xml
@@ -464,2123 +464,2126 @@ You may encounter the following error: <code>java.security.cert.CertificateExcep
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like <code>md2WithRSAEncryption</code>) or with a SSL certificate with a size lower than 1024 bits.
 </p><p>
 This error is related to increased security in Java 8.
 </p><p>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 </p><p>
 This property is in this file:</p>
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 <p>See  <bugzilla>56357</bugzilla> for details.
 </p>
 <links>
         <link href="test_plan.html#assertions">Assertion</link>
         <link href="build-web-test-plan.html">Building a Web Test Plan</link>
         <link href="build-adv-web-test-plan.html">Building an Advanced Web Test Plan</link>
         <complink name="HTTP Authorization Manager"/>
         <complink name="HTTP Cookie Manager"/>
         <complink name="HTTP Header Manager"/>
         <complink name="HTML Link Parser"/>
         <complink name="HTTP(S) Test Script Recorder"/>
         <complink name="HTTP Request Defaults"/>
         <link href="build-adv-web-test-plan.html#session_url_rewriting">HTTP Requests and Session ID's: URL Rewriting</link>
 </links>
 
 </component>
 
 <component name="JDBC Request" index="&sect-num;.1.3"  width="710" height="629" screenshot="jdbctest/jdbc-request.png">
 
 <description><p>This sampler lets you send a JDBC Request (an SQL query) to a database.</p>
 <p>Before using this you need to set up a
 <complink name="JDBC Connection Configuration"/> Configuration element
 </p>
 <p>
 If the Variable Names list is provided, then for each row returned by a Select statement, the variables are set up
 with the value of the corresponding column (if a variable name is provided), and the count of rows is also set up.
 For example, if the Select statement returns 2 rows of 3 columns, and the variable list is <code>A,,C</code>,
 then the following variables will be set up:</p>
 <source>
 A_#=2 (number of rows)
 A_1=column 1, row 1
 A_2=column 1, row 2
 C_#=2 (number of rows)
 C_1=column 3, row 1
 C_2=column 3, row 2
 </source>
 <p>
 If the Select statement returns zero rows, then the <code>A_#</code> and <code>C_#</code> variables would be set to <code>0</code>, and no other variables would be set.
 </p>
 <p>
 Old variables are cleared if necessary - e.g. if the first select retrieves six rows and a second select returns only three rows,
 the additional variables for rows four, five and six will be removed.
 </p>
 <note>The latency time is set from the time it took to acquire a connection.</note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Variable Name" required="Yes">
         Name of the JMeter variable that the connection pool is bound to.
         This must agree with the '<code>Variable Name</code>' field of a <complink name="JDBC Connection Configuration"/>.
         </property>
         <property name="Query Type" required="Yes">Set this according to the statement type:
             <ul>
             <li>Select Statement</li>
             <li>Update Statement - use this for Inserts and Deletes as well</li>
             <li>Callable Statement</li>
             <li>Prepared Select Statement</li>
             <li>Prepared Update Statement - use this for Inserts and Deletes as well</li>
             <li>Commit</li>
             <li>Rollback</li>
             <li>Autocommit(false)</li>
             <li>Autocommit(true)</li>
             <li>Edit - this should be a variable reference that evaluates to one of the above</li>
             </ul>
         </property>
         <property name="SQL Query" required="Yes">
         SQL query.
         <note>Do not enter a trailing semi-colon.</note>
         There is generally no need to use <code>{</code> and <code>}</code> to enclose Callable statements;
         however they may be used if the database uses a non-standard syntax.
         <note>The JDBC driver automatically converts the statement if necessary when it is enclosed in <code>{}</code>.</note>
         For example:
         <ul>
         <li><code>select * from t_customers where id=23</code></li>
         <li><code>CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (null, ?, ?, null, null, null)</code>
         <ul>
         <li>Parameter values: <code>tablename</code>,<code>filename</code></li>
         <li>Parameter types:  <code>VARCHAR</code>,<code>VARCHAR</code></li>
         </ul>
         </li>
         </ul>
         The second example assumes you are using Apache Derby.
         </property>
         <property name="Parameter values" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of parameter values. Use <code>]NULL[</code> to indicate a <code>NULL</code> parameter.
         (If required, the null string can be changed by defining the property "<code>jdbcsampler.nullmarker</code>".)
         <br></br>
         The list must be enclosed in double-quotes if any of the values contain a comma or double-quote,
         and any embedded double-quotes must be doubled-up, for example:
         <source>"Dbl-Quote: "" and Comma: ,"</source>
         <note>There must be as many values as there are placeholders in the statement even if your parameters are <code>OUT</code> ones.
         Be sure to set a value even if the value will not be used (for example in a CallableStatement).</note>
         </property>
         <property name="Parameter types" required="Yes, if a prepared or callable statement has parameters">
         Comma-separated list of SQL parameter types (e.g. <code>INTEGER</code>, <code>DATE</code>, <code>VARCHAR</code>, <code>DOUBLE</code>) or integer values of Constants. Those integer values can be used, when you use custom database types proposed by driver (For example <code>OracleTypes.CURSOR</code> could be represented by its integer value <code>-10</code>).<br/>
         These are defined as fields in the class <code>java.sql.Types</code>, see for example:<br/>
         <a href="http://docs.oracle.com/javase/8/docs/api/java/sql/Types.html">Javadoc for java.sql.Types</a>.<br/>
         <note>Note: JMeter will use whatever types are defined by the runtime JVM,
         so if you are running on a different JVM, be sure to check the appropriate documentation</note>
         <b>If the callable statement has <code>INOUT</code> or <code>OUT</code> parameters, then these must be indicated by prefixing the
         appropriate parameter types, e.g. instead of "<code>INTEGER</code>", use "<code>INOUT INTEGER</code>".</b> <br/>
         If not specified, "<code>IN</code>" is assumed, i.e. "<code>DATE</code>" is the same as "<code>IN DATE</code>".
         <br></br>
         If the type is not one of the fields found in <code>java.sql.Types</code>, JMeter also
         accepts the corresponding integer number, e.g. since <code>OracleTypes.CURSOR == -10</code>, you can use "<code>INOUT -10</code>".
         <br></br>
         There must be as many types as there are placeholders in the statement.
         </property>
         <property name="Variable Names" required="No">Comma-separated list of variable names to hold values returned by Select statements, Prepared Select Statements or CallableStatement. 
         Note that when used with CallableStatement, list of variables must be in the same sequence as the <code>OUT</code> parameters returned by the call.
         If there are less variable names than <code>OUT</code> parameters only as many results shall be stored in the thread-context variables as variable names were supplied.
         If more variable names than <code>OUT</code> parameters exist, the additional variables will be ignored</property>
         <property name="Result Variable Name" required="No">
         If specified, this will create an Object variable containing a list of row maps.
         Each map contains the column name as the key and the column data as the value. Usage:<br></br>
         <source>columnValue = vars.getObject("resultObject").get(0).get("Column Name");</source>
         </property>
         <property name="Handle ResultSet" required="No">Defines how ResultSet returned from callable statements be handled:
             <ul>
                 <li><code>Store As String</code> (default) - All variables on Variable Names list are stored as strings, will not iterate through a <code>ResultSet</code> when present on the list. <code>CLOB</code>s will be converted to Strings. <code>BLOB</code>s will be converted to Strings as if they were an UTF-8 encoded byte-array. Both <code>CLOB</code>s and <code>BLOB</code>s will be cut off after <code>jdbcsampler.max_retain_result_size</code> bytes.</li>
                 <li><code>Store As Object</code> - Variables of <code>ResultSet</code> type on Variables Names list will be stored as Object and can be accessed in subsequent tests/scripts and iterated, will not iterate through the <code>ResultSet</code>. <code>CLOB</code>s will be handled as if <code>Store As String</code> was selected. <code>BLOBs</code> will be stored as a byte array. Both <code>CLOB</code>s and <code>BLOB</code>s will be cut off after <code>jdbcsampler.max_retain_result_size</code> bytes.</li>
                 <li><code>Count Records</code> - Variables of <code>ResultSet</code> types will be iterated through showing the count of records as result. Variables will be stored as Strings. For <code>BLOB</code>s the size of the object will be stored.</li>
             </ul>
         </property>
 </properties>
 
 <links>
         <link href="build-db-test-plan.html">Building a Database Test Plan</link>
         <complink name="JDBC Connection Configuration"/>
 </links>
 <note>Current Versions of JMeter use UTF-8 as the character encoding. Previously the platform default was used.</note>
 <note>Ensure Variable Name is unique across Test Plan.</note>
 </component>
 
 <component name="Java Request" index="&sect-num;.1.4"  width="628" height="365" screenshot="java_request.png">
 
 <description><p>This sampler lets you control a java class that implements the
 <code>org.apache.jmeter.protocol.java.sampler.JavaSamplerClient</code> interface.
 By writing your own implementation of this interface,
 you can use JMeter to harness multiple threads, input parameter control, and
 data collection.</p>
 <p>The pull-down menu provides the list of all such implementations found by
 JMeter in its classpath.  The parameters can then be specified in the
 table below - as defined by your implementation.  Two simple examples (<code>JavaTest</code> and <code>SleepTest</code>) are provided.
 </p>
 <p>
 The <code>JavaTest</code> example sampler can be useful for checking test plans, because it allows one to set
 values in almost all the fields. These can then be used by Assertions, etc.
 The fields allow variables to be used, so the values of these can readily be seen.
 </p>
 </description>
 
 <note>If the method <code>teardownTest</code> is not overridden by a subclass of <code><a href="../api/org/apache/jmeter/protocol/java/sampler/AbstractJavaSamplerClient.html">AbstractJavaSamplerClient</a></code>, its <code>teardownTest</code> method will not be called.
 This reduces JMeter memory requirements.
 This will not have any impact on existing Test plans.
 </note>
 <note>The Add/Delete buttons don't serve any purpose at present.</note>
 
     <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="Classname" required="Yes">The specific implementation of
         the JavaSamplerClient interface to be sampled.</property>
         <property name="Send Parameters with Request" required="No">A list of
         arguments that will be passed to the sampled class.  All arguments
         are sent as Strings. See below for specific settings.</property>
     </properties>
 
     <p>The following parameters apply to the <code>SleepTest</code> and <code>JavaTest</code> implementations:</p>
 
     <properties>
         <property name="Sleep_time" required="Yes">How long to sleep for (ms)</property>
         <property name="Sleep_mask" required="Yes">How much "randomness" to add:<br></br>
             The sleep time is calculated as follows:
             <source>totalSleepTime = SleepTime + (System.currentTimeMillis() % SleepMask)</source>
         </property>
     </properties>
 
     <p>The following parameters apply additionally to the <code>JavaTest</code> implementation:</p>
 
     <properties>
         <property name="Label" required="No">The label to use. If provided, overrides <code>Name</code></property>
         <property name="ResponseCode" required="No">If provided, sets the SampleResult ResponseCode.</property>
         <property name="ResponseMessage" required="No">If provided, sets the SampleResult ResponseMessage.</property>
         <property name="Status" required="No">If provided, sets the SampleResult Status. If this equals "<code>OK</code>" (ignoring case) then the status is set to success, otherwise the sample is marked as failed.</property>
         <property name="SamplerData" required="No">If provided, sets the SampleResult SamplerData.</property>
         <property name="ResultData" required="No">If provided, sets the SampleResult ResultData.</property>
     </properties>
 </component>
 
 <component name="SOAP/XML-RPC Request" index="&sect-num;.1.5"  width="426" height="276" screenshot="soap_sampler.png">
 <note>
 See <a href="build-ws-test-plan.html">Building a WebService Test Plan</a> for up to date way of test SOAP and REST Webservices
 </note>
 <description><p>This sampler lets you send a SOAP request to a webservice.  It can also be
 used to send XML-RPC over HTTP.  It creates an HTTP POST request, with the specified XML as the
 POST content.
 To change the "<code>Content-type</code>" from the default of "<code>text/xml</code>", use a <complink name="HTTP Header Manager" />.
 Note that the sampler will use all the headers from the <complink name="HTTP Header Manager"/>.
 If a SOAP action is specified, that will override any <code>SOAPaction</code> in the <complink name="HTTP Header Manager"/>.
 The primary difference between the soap sampler and
 webservice sampler, is the soap sampler uses raw post and does not require conformance to
 SOAP 1.1.</p>
 <note>The sampler no longer uses chunked encoding by default.<br/>
 For screen input, it now always uses the size of the data.<br/>
 File input uses the file length as determined by Java.<br/>
 On some OSes this may not work for all files, in which case add a child <complink name="HTTP Header Manager"/>
 with <code>Content-Length</code> set to the actual length of the file.<br/>
 Or set <code>Content-Length</code> to <code>-1</code> to force chunked encoding.
 </note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler
          that is shown in the tree.</property>
         <property name="URL" required="Yes">The URL to direct the SOAP request to.</property>
         <property name="Send SOAP action" required="No">Send a SOAP action header? (overrides the <complink name="HTTP Header Manager"/>)</property>
         <property name="Use KeepAlive" required="No">If set, sends <code>Connection: keep-alive</code>, else sends <code>Connection: close</code></property>
         <property name="Soap/XML-RPC Data" required="No">The Soap XML message, or XML-RPC instructions.
         Not used if the filename is provided.
         </property>
         <property name="Filename" required="No">If specified, then the contents of the file are sent, and the Data field is ignored</property>
         </properties>
 </component>
 
 <component name="LDAP Request" index="&sect-num;.1.7" width="621" height="462" screenshot="ldap_request.png">
   <description>This Sampler lets you send a different Ldap request(<code>Add</code>, <code>Modify</code>, <code>Delete</code> and <code>Search</code>) to an LDAP server.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> The same way the <complink name="Login Config Element"/> also using for Login and password.
   </description>
 
   <p>There are two ways to create test cases for testing an LDAP Server.</p>
   <ol>
     <li>Inbuilt Test cases.</li>
     <li>User defined Test cases.</li>
   </ol>
 
   <p>There are four test scenarios of testing LDAP. The tests are given below:</p>
   <ol>
     <li>Add Test
       <ol>
         <li>Inbuilt test:
           <p>This will add a pre-defined entry in the LDAP Server and calculate
           the execution time. After execution of the test, the created entry will be
           deleted from the LDAP
           Server.</p>
         </li>
         <li>User defined test:
           <p>This will add the entry in the LDAP Server. User has to enter all the
           attributes in the table.The entries are collected from the table to add. The
           execution time is calculated. The created entry will not be deleted after the
           test.</p>
         </li>
       </ol>
     </li>
     <li>Modify Test
       <ol>
         <li>Inbuilt test:
           <p>This will create a pre-defined entry first, then will modify the
           created entry in the LDAP Server.And calculate the execution time. After
           execution
           of the test, the created entry will be deleted from the LDAP Server.</p>
         </li>
         <li>User defined test:
           <p>This will modify the entry in the LDAP Server. User has to enter all the
           attributes in the table. The entries are collected from the table to modify.
           The execution time is calculated. The entry will not be deleted from the LDAP
           Server.</p>
         </li>
       </ol>
     </li>
     <li>Search Test
       <ol>
         <li>Inbuilt test:
           <p>This will create the entry first, then will search if the attributes
           are available. It calculates the execution time of the search query. At the
           end of  the execution,created entry will be deleted from the LDAP Server.</p>
         </li>
         <li>User defined test:
           <p>This will search the user defined entry(Search filter) in the Search
           base (again, defined by the user). The entries should be available in the LDAP
           Server. The execution time is  calculated.</p>
         </li>
       </ol>
     </li>
     <li>Delete Test
       <ol>
         <li>Inbuilt test:
           <p>This will create a pre-defined entry first, then it will be deleted
           from the LDAP Server. The execution time is calculated.</p>
         </li>
         <li>User defined test:
           <p>This will delete the user-defined entry in the LDAP Server. The entries
           should be available in the LDAP Server. The execution time is calculated.</p>
         </li>
       </ol>
     </li>
   </ol>
   <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     <property name="Server Name or IP" required="Yes">Domain name or IP address of the LDAP server.
       JMeter assumes the LDAP server is listening on the default port (<code>389</code>).</property>
     <property name="Port" required="Yes">Port to connect to (default is <code>389</code>).</property>
     <property name="root DN" required="Yes">Base DN to use for ldap operations</property>
     <property name="Username" required="Usually">LDAP server username.</property>
     <property name="Password" required="Usually">LDAP server password. (N.B. this is stored unencrypted in the test plan)</property>
     <property name="Entry DN" required="Yes, if User Defined Test and Add Test or Modify Test is selected">the name of the context to create or Modify; may not be empty.
      <note>You have to set the right attributes of the object yourself. So if you want to add <code>cn=apache,ou=test</code>
       you have to add in the table <code>name</code> and <code>value</code> to <code>cn</code> and <code>apache</code>.
      </note>
     </property>
     <property name="Delete" required="Yes, if User Defined Test and Delete Test is selected">the name of the context to Delete; may not be empty</property>
     <property name="Search base" required="Yes, if User Defined Test and Search Test is selected">the name of the context or object to search</property>
     <property name="Search filter" required="Yes, if User Defined Test and Search Test is selected"> the filter expression to use for the search; may not be null</property>
     <property name="add test" required="Yes, if User Defined Test and add Test is selected">Use these <code>name</code>, <code>value</code> pairs for creation of the new object in the given context</property>
     <property name="modify test" required="Yes, if User Defined Test and Modify Test is selected">Use these <code>name</code>, <code>value</code> pairs for modification of the given context object</property>
   </properties>
 
   <links>
     <link href="build-ldap-test-plan.html">Building an Ldap Test Plan</link>
     <complink name="LDAP Request Defaults"/>
   </links>
 
 </component>
 
 <component name="LDAP Extended Request" index="&sect-num;.1.8" width="619" height="371" screenshot="ldapext_request.png">
   <description>This Sampler can send all 8 different LDAP requests to an LDAP server. It is an extended version of the LDAP sampler,
   therefore it is harder to configure, but can be made much closer resembling a real LDAP session.
     <p>If you are going to send multiple requests to the same LDAP server, consider
       using an <complink name="LDAP Extended Request Defaults"/>
       Configuration Element so you do not have to enter the same information for each
       LDAP Request.</p> </description>
 
    <p>There are nine test operations defined. These operations are given below:</p>
     <dl>
       <dt><b>Thread bind</b></dt>
       <dd>
         <p>Any LDAP request is part of an LDAP session, so the first thing that should be done is starting a session to the LDAP server.
         For starting this session a thread bind is used, which is equal to the LDAP "<code>bind</code>" operation.
         The user is requested to give a <code>username</code> (Distinguished name) and <code>password</code>,
         which will be used to initiate a session.
         When no password, or the wrong password is specified, an anonymous session is started. Take care,
         omitting the password will not fail this test, a wrong password will.
         (N.B. this is stored unencrypted in the test plan)</p>
         <properties>
           <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
           <property name="Servername" required="Yes">The name (or IP-address) of the LDAP server.</property>
           <property name="Port" required="No">The port number that the LDAP server is listening to. If this is omitted
             JMeter assumes the LDAP server is listening on the default port(389).</property>
           <property name="DN" required="No">The distinguished name of the base object that will be used for any subsequent operation.
             It can be used as a starting point for all operations. You cannot start any operation on a higher level than this DN!</property>
           <property name="Username" required="No">Full distinguished name of the user as which you want to bind.</property>
           <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind.
             If it is incorrect, the sampler will return an error and revert to an anonymous bind. (N.B. this is stored unencrypted in the test plan)</property>
         </properties>
       </dd>
       <dt><b>Thread unbind</b></dt>
       <dd>
         <p>This is simply the operation to end a session.
         It is equal to the LDAP "<code>unbind</code>" operation.</p>
         <properties>
           <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         </properties>
       </dd>
       <dt><b>Single bind/unbind</b></dt>
       <dd>
         <p> This is a combination of the LDAP "<code>bind</code>" and "<code>unbind</code>" operations.
         It can be used for an authentication request/password check for any user. It will open a new session, just to
         check the validity of the user/password combination, and end the session again.</p>
         <properties>
           <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
           <property name="Username" required="Yes">Full distinguished name of the user as which you want to bind.</property>
           <property name="Password" required="No">Password for the above user. If omitted it will result in an anonymous bind.
             If it is incorrect, the sampler will return an error. (N.B. this is stored unencrypted in the test plan)</property>
         </properties>
       </dd>
       <dt><b>Rename entry</b></dt>
       <dd>
        <p>This is the LDAP "<code>moddn</code>" operation. It can be used to rename an entry, but
        also for moving an entry or a complete subtree to a different place in
        the LDAP tree.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Old entry name" required="Yes">The current distinguished name of the object you want to rename or move,
            relative to the given DN in the thread bind operation.</property>
          <property name="New distinguished name" required="Yes">The new distinguished name of the object you want to rename or move,
            relative to the given DN in the thread bind operation.</property>
        </properties>
      </dd>
      <dt><b>Add test</b></dt>
      <dd>
        <p>This is the ldap "<code>add</code>" operation. It can be used to add any kind of
        object to the LDAP server.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Entry DN" required="Yes">Distinguished name of the object you want to add, relative to the given DN in the thread bind operation.</property>
          <property name="Add test" required="Yes">A list of attributes and their values you want to use for the object.
            If you need to add a multiple value attribute, you need to add the same attribute with their respective
            values several times to the list.</property>
        </properties>
      </dd>
      <dt><b>Delete test</b></dt>
      <dd>
        <p> This is the LDAP "<code>delete</code>" operation, it can be used to delete an
        object from the LDAP tree</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Delete" required="Yes">Distinguished name of the object you want to delete, relative to the given DN in the thread bind operation.</property>
        </properties>
      </dd>
      <dt><b>Search test</b></dt>
      <dd>
        <p>This is the LDAP "<code>search</code>" operation, and will be used for defining searches.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Search base" required="No">Distinguished name of the subtree you want your
            search to look in, relative to the given DN in the thread bind operation.</property>
          <property name="Search Filter" required="Yes">searchfilter, must be specified in LDAP syntax.</property>
          <property name="Scope" required="No">Use <code>0</code> for baseobject-, <code>1</code> for onelevel- and <code>2</code> for a subtree search. (Default=<code>0</code>)</property>
          <property name="Size Limit" required="No">Specify the maximum number of results you want back from the server. (default=<code>0</code>, which means no limit.) When the sampler hits the maximum number of results, it will fail with errorcode <code>4</code></property>
          <property name="Time Limit" required="No">Specify the maximum amount of (cpu)time (in milliseconds) that the server can spend on your search. Take care, this does not say anything about the response time. (default is <code>0</code>, which means no limit)</property>
          <property name="Attributes" required="No">Specify the attributes you want to have returned, separated by a semicolon. An empty field will return all attributes</property>
          <property name="Return object" required="No">Whether the object will be returned (<code>true</code>) or not (<code>false</code>). Default=<code>false</code></property>
          <property name="Dereference aliases" required="No">If <code>true</code>, it will dereference aliases, if <code>false</code>, it will not follow them (default=<code>false</code>)</property>
          <property name="Parse the search results?" required="No">If <code>true</code>, the search results will be added to the response data. If <code>false</code>, a marker - whether results where found or not - will be added to the response data.</property>
        </properties>
      </dd>
      <dt><b>Modification test</b></dt>
      <dd>
        <p>This is the LDAP "<code>modify</code>" operation. It can be used to modify an object. It
        can be used to add, delete or replace values of an attribute. </p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Entry name" required="Yes">Distinguished name of the object you want to modify, relative 
            to the given DN in the thread bind operation</property>
          <property name="Modification test" required="Yes">The attribute-value-opCode triples. The opCode can be any
            valid LDAP operationCode (<code>add</code>, <code>delete</code>/<code>remove</code> or <code>replace</code>).
            If you don't specify a value with a <code>delete</code> operation,
            all values of the given attribute will be deleted. If you do specify a value in a <code>delete</code> operation, only
            the given value will be deleted. If this value is non-existent, the sampler will fail the test.</property>
        </properties>
      </dd>
      <dt><b>Compare</b></dt>
      <dd>
        <p>This is the LDAP "<code>compare</code>" operation. It can be used to compare the value
        of a given attribute with some already known value. In reality this is mostly
        used to check whether a given person is a member of some group. In such a case
        you can compare the DN of the user as a given value, with the values in the
        attribute "<code>member</code>" of an object of the type <code>groupOfNames</code>.
        If the compare operation fails, this test fails with errorcode <code>49</code>.</p>
        <properties>
          <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
          <property name="Entry DN" required="Yes">The current distinguished name of the object of
            which you want  to compare an attribute, relative to the given DN in the thread bind operation.</property>
          <property name="Compare filter" required="Yes">In the form "<code>attribute=value</code>"</property>
        </properties>
      </dd>
    </dl>
    <links>
      <link href="build-ldapext-test-plan.html">Building an LDAP Test Plan</link>
      <complink name="LDAP Extended Request Defaults"/>
    </links>
 </component>
 
 
 
 
 <component name="Access Log Sampler" index="&sect-num;.1.9"  width="702" height="305" screenshot="accesslogsampler.png">
 <center><h2>(Beta Code)</h2></center>
 <description><p>AccessLogSampler was designed to read access logs and generate http requests.
 For those not familiar with the access log, it is the log the webserver maintains of every
 request it accepted. This means every image, css file, javascript file, html file, &hellip;
 The current implementation is complete, but some features have not been enabled. 
 There is a filter for the access log parser, but I haven't figured out how to link to the pre-processor. 
 Once I do, changes to the sampler will be made to enable that functionality.</p>
 <p>Tomcat uses the common format for access logs. This means any webserver that uses the
 common log format can use the AccessLogSampler. Server that use common log format include:
 Tomcat, Resin, Weblogic, and SunOne. Common log format looks
 like this:</p>
 <source>127.0.0.1 - - [21/Oct/2003:05:37:21 -0500] "GET /index.jsp?%2Findex.jsp= HTTP/1.1" 200 8343</source>
 <note>The current implementation of the parser only looks at the text within the quotes that contains one of the HTTP protocol methods (<code>GET</code>, <code>PUT</code>, <code>POST</code>, <code>DELETE</code>, &hellip;).
 Everything else is stripped out and ignored. For example, the response code is completely
 ignored by the parser. </note>
 <p>For the future, it might be nice to filter out entries that
 do not have a response code of <code>200</code>. Extending the sampler should be fairly simple. There
 are two interfaces you have to implement:</p>
 <ul>
 <li><code>org.apache.jmeter.protocol.http.util.accesslog.LogParser</code></li>
 <li><code>org.apache.jmeter.protocol.http.util.accesslog.Generator</code></li>
 </ul>
 <p>The current implementation of AccessLogSampler uses the generator to create a new
 HTTPSampler. The servername, port and get images are set by AccessLogSampler. Next,
 the parser is called with integer <code>1</code>, telling it to parse one entry. After that,
 <code>HTTPSampler.sample()</code> is called to make the request.</p>
 <source>
 samp = (HTTPSampler) GENERATOR.generateRequest();
 samp.setDomain(this.getDomain());
 samp.setPort(this.getPort());
 samp.setImageParser(this.isImageParser());
 PARSER.parse(1);
 res = samp.sample();
 res.setSampleLabel(samp.toString());
 </source>
 The required methods in <code>LogParser</code> are:
 <ul>
 <li><code>setGenerator(Generator)</code></li>
 <li><code>parse(int)</code></li> 
 </ul>
 <p>
 Classes implementing <code>Generator</code> interface should provide concrete implementation
 for all the methods. For an example of how to implement either interface, refer to
 <code>StandardGenerator</code> and <code>TCLogParser</code>.
 </p>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="Server" required="Yes">Domain name or IP address of the web server.</property>
         <property name="Protocol" required="No (defaults to http">Scheme</property>
         <property name="Port" required="No (defaults to 80)">Port the web server is listening to.</property>
         <property name="Log parser class" required="Yes (default provided)">The log parser class is responsible for parsing the logs.</property>
         <property name="Filter" required="No">The filter class is used to filter out certain lines.</property>
         <property name="Location of log file" required="Yes">The location of the access log file.</property>
 </properties>
 <p>
 The <code>TCLogParser</code> processes the access log independently for each thread.
 The <code>SharedTCLogParser</code> and <code>OrderPreservingLogParser</code> share access to the file, 
 i.e. each thread gets the next entry in the log.
 </p>
 <p>
 The <code>SessionFilter</code> is intended to handle Cookies across threads. 
 It does not filter out any entries, but modifies the cookie manager so that the cookies for a given IP are
 processed by a single thread at a time. If two threads try to process samples from the same client IP address,
 then one will be forced to wait until the other has completed.
 </p>
 <p>
 The <code>LogFilter</code> is intended to allow access log entries to be filtered by filename and regex,
 as well as allowing for the replacement of file extensions. However, it is not currently possible
 to configure this via the GUI, so it cannot really be used.
 </p>
 </component>
 
 <component name="BeanShell Sampler" index="&sect-num;.1.10"  width="848" height="566" screenshot="beanshellsampler.png">
     <description><p>This sampler allows you to write a sampler using the BeanShell scripting language.
 </p><p>
 <b>For full details on using BeanShell, please see the <a href="http://www.beanshell.org/">BeanShell website.</a></b>
 </p>
 <p>
 The test element supports the <code>ThreadListener</code> and <code>TestListener</code> interface methods.
 These must be defined in the initialisation file.
 See the file <code>BeanShellListeners.bshrc</code> for example definitions.
 </p>
 <p>
 The BeanShell sampler also supports the <code>Interruptible</code> interface.
 The <code>interrupt()</code> method can be defined in the script or the init file.
 </p>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.
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
     <dl>
         <dt><code>Parameters</code></dt><dd>string containing the parameters as a single variable</dd>
         <dt><code>bsh.args</code></dt><dd>String array containing parameters, split on white-space</dd>
     </dl></property>
     <property name="Script file" required="No">A file containing the BeanShell script to run.
     The file name is stored in the script variable <code>FileName</code></property>
     <property name="Script" required="Yes (unless script file is provided)">The BeanShell script to run. 
     The return value (if not <code>null</code>) is stored as the sampler result.</property>
 </properties>
 <note>
 N.B. Each Sampler instance has its own BeanShell interpreter,
 and Samplers are only called from a single thread
 </note><p>
 If the property "<code>beanshell.sampler.init</code>" is defined, it is passed to the Interpreter
 as the name of a sourced file.
 This can be used to define common methods and variables. 
 There is a sample init file in the bin directory: <code>BeanShellSampler.bshrc</code>.
 </p><p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <note>
 JMeter processes function and variable references before passing the script field to the interpreter,
 so the references will only be resolved once.
 Variable and function references in script files will be passed
 verbatim to the interpreter, which is likely to cause a syntax error.
 In order to use runtime variables, please use the appropriate props methods,
 e.g.<code>props.get("START.HMS"); props.put("PROP1","1234");</code>
 <br/>
 BeanShell does not currently support Java 5 syntax such as generics and the enhanced for loop.
 </note>
         <p>Before invoking the script, some variables are set up in the BeanShell interpreter:</p>
         <p>The contents of the Parameters field is put into the variable "<code>Parameters</code>".
             The string is also split into separate tokens using a single space as the separator, and the resulting list
             is stored in the String array <code>bsh.args</code>.</p>
         <p>The full list of BeanShell variables that is set up is as follows:</p>
         <ul>
         <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a></li>
         <li><code>Label</code> - the Sampler label</li>
         <li><code>FileName</code> - the file name, if any</li>
         <li><code>Parameters</code> - text from the Parameters field</li>
         <li><code>bsh.args</code> - the parameters, split as described above</li>
         <li><code>SampleResult</code> - pointer to the current <a href="../api/org/apache/jmeter/samplers/SampleResult.html"><code>SampleResult</code></a></li>
             <li><code>ResponseCode</code> defaults to <code>200</code></li>
             <li><code>ResponseMessage</code> defaults to "<code>OK</code>"</li>
             <li><code>IsSuccess</code> defaults to <code>true</code></li>
             <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
             <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
                <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.remove("VAR3");
 vars.putObject("OBJ1",new Object());</source></li>
             <li><code>props</code> - JMeterProperties (class <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html"><code>java.util.Properties</code></a>) - e.g.
                 <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
         </ul>
         <p>When the script completes, control is returned to the Sampler, and it copies the contents
             of the following script variables into the corresponding variables in the <a href="../api/org/apache/jmeter/samplers/SampleResult.html"><code>SampleResult</code></a>:</p>
             <ul>
             <li><code>ResponseCode</code> - for example <code>200</code></li>
             <li><code>ResponseMessage</code> - for example "<code>OK</code>"</li>
             <li><code>IsSuccess</code> - <code>true</code> or <code>false</code></li>
             </ul>
             <p>The SampleResult ResponseData is set from the return value of the script.
             If the script returns null, it can set the response directly, by using the method 
             <code>SampleResult.setResponseData(data)</code>, where data is either a String or a byte array.
             The data type defaults to "<code>text</code>", but can be set to binary by using the method
             <code>SampleResult.setDataType(SampleResult.BINARY)</code>.
             </p>
             <p>The <code>SampleResult</code> variable gives the script full access to all the fields and
                 methods in the <code>SampleResult</code>. For example, the script has access to the methods
                 <code>setStopThread(boolean)</code> and <code>setStopTest(boolean)</code>.
 
                 Here is a simple (not very useful!) example script:</p>
 
 <source>
 if (bsh.args[0].equalsIgnoreCase("StopThread")) {
     log.info("Stop Thread detected!");
     SampleResult.setStopThread(true);
 }
 return "Data from sample with Label "+Label;
 //or
 SampleResult.setResponseData("My data");
 return null;
 </source>
 <p>Another example:<br></br> ensure that the property <code>beanshell.sampler.init=BeanShellSampler.bshrc</code> is defined in <code>jmeter.properties</code>. 
 The following script will show the values of all the variables in the <code>ResponseData</code> field:
 </p>
 <source>
 return getVariables();
 </source>
 <p>
 For details on the methods available for the various classes (<a href="../api/org/apache/jmeter/threads/JMeterVariables.html"><code>JMeterVariables</code></a>, <a href="../api/org/apache/jmeter/samplers/SampleResult.html"><code>SampleResult</code></a> etc.) please check the Javadoc or the source code.
 Beware however that misuse of any methods can cause subtle faults that may be difficult to find.
 </p>
 </component>
 
 
 <component name="BSF Sampler (DEPRECATED)" index="&sect-num;.1.11"  width="848" height="590" screenshot="bsfsampler.png">
     <description><p>This sampler allows you to write a sampler using a BSF scripting language.<br></br>
         See the <a href="http://commons.apache.org/bsf/index.html">Apache Bean Scripting Framework</a>
         website for details of the languages supported.
         You may need to download the appropriate jars for the language; they should be put in the JMeter <code>lib</code> directory.
         </p>
         <note>
         The BSF API has been largely superseded by JSR-223, which is included in Java 6 onwards.
         Most scripting languages now include support for JSR-223; please use the JSR223 Sampler instead.
         The BSF Sampler should only be needed for supporting legacy languages/test scripts.
         </note>
         <p>By default, JMeter supports the following languages:</p>
         <ul>
         <li>javascript</li>
         <li>xslt</li>
         </ul>
         <note>Unlike the BeanShell sampler, the interpreter is not saved between invocations.</note>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     <property name="Scripting Language" required="Yes">Name of the BSF scripting language to be used.
       <note>N.B. Not all the languages in the drop-down list are supported by default.
         The following are supported: javascript, xslt.
         Others may be available if the appropriate jar is installed in the JMeter lib directory.
       </note>
     </property>
     <property name="Script File" required="No">Name of a file to be used as a BSF script, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Parameters" required="No">List of parameters to be passed to the script file or the script.</property>
     <property name="Script" required="Yes (unless script file is provided)">Script to be passed to BSF language</property>
 </properties>
 <p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 <note>
 JMeter processes function and variable references before passing the script field to the interpreter,
 so the references will only be resolved once.
 Variable and function references in script files will be passed
 verbatim to the interpreter, which is likely to cause a syntax error.
 In order to use runtime variables, please use the appropriate props methods,
 e.g.<code>props.get("START.HMS"); props.put("PROP1","1234");</code> 
 </note>
 <p>
 Before invoking the script, some variables are set up.
 Note that these are BSF variables - i.e. they can be used directly in the script.
 </p>
 <ul>
 <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a></li>
 <li><code>Label</code> - the Sampler label</li>
 <li><code>FileName</code> - the file name, if any</li>
 <li><code>Parameters</code> - text from the Parameters field</li>
 <li><code>args</code> - the parameters, split as described above</li>
 <li><code>SampleResult</code> - pointer to the current <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a></li>
 <li><code>sampler</code> - <a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a> - pointer to current Sampler</li>
 <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
 <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
   <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.remove("VAR3");
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - JMeterProperties  (class <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html"><code>java.util.Properties</code></a>) - e.g. 
   <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>
 The <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> ResponseData is set from the return value of the script.
 If the script returns <code>null</code>, it can set the response directly, by using the method 
 <code>SampleResult.setResponseData(data)</code>, where data is either a String or a byte array.
 The data type defaults to "<code>text</code>", but can be set to binary by using the method
 <code>SampleResult.setDataType(SampleResult.BINARY)</code>.
 </p>
 <p>
 The SampleResult variable gives the script full access to all the fields and
 methods in the SampleResult. For example, the script has access to the methods
 <code>setStopThread(boolean)</code> and <code>setStopTest(boolean)</code>.
 </p>
 <p>
 Unlike the BeanShell Sampler, the BSF Sampler does not set the <code>ResponseCode</code>, <code>ResponseMessage</code> and sample status via script variables.
 Currently the only way to change these is via the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> methods:
 </p>
 <ul>
 <li><code>SampleResult.setSuccessful(true/false)</code></li>
 <li><code>SampleResult.setResponseCode("code")</code></li>
 <li><code>SampleResult.setResponseMessage("message")</code></li>
 </ul>
 </component>
 
 <component name="JSR223 Sampler" index="&sect-num;.1.11.1" width="861" height="502" screenshot="jsr223-sampler.png">
 <description>
 <p>
 The JSR223 Sampler allows JSR223 script code to be used to perform a sample.
 </p>
 <p>
 The JSR223 test elements have a feature (compilation) that can significantly increase performance.
 To benefit from this feature:
 </p>
 <ul>
     <li>Use Script files instead of inlining them. This will make JMeter compile them if this feature is available on ScriptEngine and cache them.</li>
     <li>Or Use Script Text and check <code>Cache compiled script if available</code> property.
     <note>When using this feature, ensure your script code does not use JMeter variables directly in script code as caching would only cache first replacement. Instead use script parameters.</note>
     <note>To benefit from caching and compilation, the language engine used for scripting must implement JSR223 <code><a href="https://docs.oracle.com/javase/8/docs/api/javax/script/Compilable.html">Compilable</a></code> interface (Groovy is one of these, java, beanshell and javascript are not)</note>
     <note>When using Groovy as scripting language and not checking <code>Cache compiled script if available</code> (while caching is recommended), you should set this JVM Property <code>-Dgroovy.use.classvalue=true</code>
         due to a Groovy Memory leak as of version 2.4.6, see:
         <ul>
             <li><a href="https://issues.apache.org/jira/browse/GROOVY-7683" >GROOVY-7683</a></li>
             <li><a href="https://issues.apache.org/jira/browse/GROOVY-7591">GROOVY-7591</a></li>
             <li><a href="https://bugs.openjdk.java.net/browse/JDK-8136353">JDK-8136353</a></li>
         </ul> 
     </note>
     </li>
 </ul>
 Cache size is controlled by the following jmeter property (<code>jmeter.properties</code>):
 <source>jsr223.compiled_scripts_cache_size=100</source>
 <note>Unlike the <complink name="BeanShell Sampler" />, the interpreter is not saved between invocations.</note>
 <note>
 JSR223 Test Elements using Script file or Script text + checked <code>Cache compiled script if available</code> are now compiled if ScriptEngine supports this feature, this enables great performance enhancements.
 </note>
 </description>
 <note>
 JMeter processes function and variable references before passing the script field to the interpreter,
 so the references will only be resolved once.
 Variable and function references in script files will be passed
 verbatim to the interpreter, which is likely to cause a syntax error.
 In order to use runtime variables, please use the appropriate props methods,
 e.g. <source>props.get("START.HMS");
 props.put("PROP1","1234");</source>
 </note>
 <properties>
     <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
     <property name="Scripting Language" required="Yes">Name of the JSR223 scripting language to be used.
       <note>There are other languages supported than those that appear in the drop-down list.
         Others may be available if the appropriate jar is installed in the JMeter lib directory.
       </note>
     </property>
     <property name="Script File" required="No">Name of a file to be used as a JSR223 script, if a relative file path is used, then it will be relative to directory referenced by "<code>user.dir</code>" System property</property>
     <property name="Parameters" required="No">List of parameters to be passed to the script file or the script.</property>
     <property name="Cache compiled script if available" required="No">If checked (advised) and the language used supports <code><a href="https://docs.oracle.com/javase/8/docs/api/javax/script/Compilable.html">Compilable</a></code> interface (Groovy is one of these, java, beanshell and javascript are not), JMeter will compile the Script and cache it using it's MD5 hash as unique cache key</property>
     <property name="Script" required="Yes (unless script file is provided)">Script to be passed to JSR223 language</property>
 </properties>
 <p>
 If a script file is supplied, that will be used, otherwise the script will be used.</p>
 
 <p>
 Before invoking the script, some variables are set up.
 Note that these are JSR223 variables - i.e. they can be used directly in the script.
 </p>
 <ul>
 <li><code>log</code> - the <a href="http://excalibur.apache.org/apidocs/org/apache/log/Logger.html">Logger</a></li>
 <li><code>Label</code> - the Sampler label</li>
 <li><code>FileName</code> - the file name, if any</li>
 <li><code>Parameters</code> - text from the Parameters field</li>
 <li><code>args</code> - the parameters, split as described above</li>
 <li><code>SampleResult</code> - pointer to the current <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a></li>
 <li><code>sampler</code> - (<a href="../api/org/apache/jmeter/samplers/Sampler.html">Sampler</a>) - pointer to current Sampler</li>
 <li><code>ctx</code> - <a href="../api/org/apache/jmeter/threads/JMeterContext.html">JMeterContext</a></li>
 <li><code>vars</code> - <a href="../api/org/apache/jmeter/threads/JMeterVariables.html">JMeterVariables</a>  - e.g. 
   <source>vars.get("VAR1");
 vars.put("VAR2","value");
 vars.remove("VAR3");
 vars.putObject("OBJ1",new Object());</source></li>
 <li><code>props</code> - JMeterProperties  (class <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html"><code>java.util.Properties</code></a>) - e.g. 
   <source>props.get("START.HMS");
 props.put("PROP1","1234");</source></li>
 <li><code>OUT</code> - System.out - e.g. <code>OUT.println("message")</code></li>
 </ul>
 <p>
 The <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> ResponseData is set from the return value of the script.
 If the script returns <code>null</code>, it can set the response directly, by using the method 
 <code>SampleResult.setResponseData(data)</code>, where data is either a String or a byte array.
 The data type defaults to "<code>text</code>", but can be set to binary by using the method
 <code>SampleResult.setDataType(SampleResult.BINARY)</code>.
 </p>
 <p>
 The SampleResult variable gives the script full access to all the fields and
 methods in the SampleResult. For example, the script has access to the methods
 <code>setStopThread(boolean)</code> and <code>setStopTest(boolean)</code>.
 </p>
 <p>
 Unlike the BeanShell Sampler, the JSR223 Sampler does not set the <code>ResponseCode</code>, <code>ResponseMessage</code> and sample status via script variables.
 Currently the only way to changes these is via the <a href="../api/org/apache/jmeter/samplers/SampleResult.html">SampleResult</a> methods:
 </p>
 <ul>
 <li><code>SampleResult.setSuccessful(true/false)</code></li>
 <li><code>SampleResult.setResponseCode("code")</code></li>
 <li><code>SampleResult.setResponseMessage("message")</code></li>
 </ul>
 </component>
 
 <component name="TCP Sampler" index="&sect-num;.1.12"  width="827" height="521" screenshot="tcpsampler.png">
     <description>
         <p>
         The TCP Sampler opens a TCP/IP connection to the specified server.
         It then sends the text, and waits for a response.
         </p><p>
         If "<code>Re-use connection</code>" is selected, connections are shared between Samplers in the same thread,
         provided that the exact same host name string and port are used. 
         Different hosts/port combinations will use different connections, as will different threads. 
         If both of "<code>Re-use connection</code>" and "<code>Close connection</code>" are selected, the socket will be closed after running the sampler. 
         On the next sampler, another socket will be created. You may want to close a socket at the end of each thread loop.
         </p><p>
         If an error is detected - or "<code>Re-use connection</code>" is not selected - the socket is closed.
         Another socket will be reopened on the next sample.
         </p><p>
         The following properties can be used to control its operation:
         </p>
         <dl>
             <dt><code>tcp.status.prefix</code></dt><dd>text that precedes a status number</dd>
             <dt><code>tcp.status.suffix</code></dt><dd>text that follows a status number</dd>
             <dt><code>tcp.status.properties</code></dt><dd>name of property file to convert status codes to messages</dd>
             <dt><code>tcp.handler</code></dt><dd>Name of TCP Handler class (default <code>TCPClientImpl</code>) - only used if not specified on the GUI</dd>
         </dl>
         The class that handles the connection is defined by the GUI, failing that the property <code>tcp.handler</code>.
         If not found, the class is then searched for in the package <code>org.apache.jmeter.protocol.tcp.sampler</code>.
         <p>
         Users can provide their own implementation.
         The class must extend <code>org.apache.jmeter.protocol.tcp.sampler.TCPClient</code>.
         </p>
         <p>
         The following implementations are currently provided.
         </p>
         <ul>
         <li><code>TCPClientImpl</code></li>
         <li><code>BinaryTCPClientImpl</code></li>
         <li><code>LengthPrefixedBinaryTCPClientImpl</code></li>
         </ul>
         The implementations behave as follows:
         <dl>
         <dt><code>TCPClientImpl</code></dt>
         <dd>
         This implementation is fairly basic.
         When reading the response, it reads until the end of line byte, if this is defined
         by setting the property <code>tcp.eolByte</code>, otherwise until the end of the input stream.
         You can control charset encoding by setting <code>tcp.charset</code>, which will default to Platform default encoding.
         </dd>
         <dt><code>BinaryTCPClientImpl</code></dt>
         <dd>
         This implementation converts the GUI input, which must be a hex-encoded string, into binary,
         and performs the reverse when reading the response.
         When reading the response, it reads until the end of message byte, if this is defined
         by setting the property <code>tcp.BinaryTCPClient.eomByte</code>, otherwise until the end of the input stream.
         </dd>
         <dt><code>LengthPrefixedBinaryTCPClientImpl</code></dt>
         <dd>
         This implementation extends BinaryTCPClientImpl by prefixing the binary message data with a binary length byte.
         The length prefix defaults to 2 bytes.
         This can be changed by setting the property <code>tcp.binarylength.prefix.length</code>.
         </dd>
         <dt><b>Timeout handling</b></dt>
         <dd>
         If the timeout is set, the read will be terminated when this expires. 
         So if you are using an <code>eolByte</code>/<code>eomByte</code>, make sure the timeout is sufficiently long,
         otherwise the read will be terminated early.
         </dd>
         <dt><b>Response handling</b></dt>
         <dd>
         If <code>tcp.status.prefix</code> is defined, then the response message is searched for the text following
         that up to the suffix. If any such text is found, it is used to set the response code.
         The response message is then fetched from the properties file (if provided).
         <example title="Usage of pre- and suffix" anchor="tcp-prefix-example">
         For example, if the prefix = "<code>[</code>" and the suffix = "<code>]</code>", then the following response:
         <source>[J28] XI123,23,GBP,CR</source>
         would have the response code <code>J28</code>.
         </example>
         Response codes in the range "<code>400</code>"-"<code>499</code>" and "<code>500</code>"-"<code>599</code>" are currently regarded as failures;
         all others are successful. [This needs to be made configurable!]
         </dd>
         </dl>
 <note>The login name/password are not used by the supplied TCP implementations.</note>
         <br></br>
         Sockets are disconnected at the end of a test run.
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="TCPClient classname" required="No">Name of the TCPClient class. Defaults to the property <code>tcp.handler</code>, failing that <code>TCPClientImpl</code>.</property>
   <property name="ServerName or IP" required="Yes">Name or IP of TCP server</property>
   <property name="Port Number" required="Yes">Port to be used</property>
   <property name="Re-use connection" required="Yes">If selected, the connection is kept open. Otherwise it is closed when the data has been read.</property>
   <property name="Close connection" required="Yes">If selected, the connection will be closed after running the sampler.</property>
   <property name="SO_LINGER" required="No">Enable/disable <code>SO_LINGER</code> with the specified linger time in seconds when a socket is created. If you set "<code>SO_LINGER</code>" value as <code>0</code>, you may prevent large numbers of sockets sitting around with a <code>TIME_WAIT</code> status.</property>
   <property name="End of line(EOL) byte value" required="No">Byte value for end of line, set this to a value outside the range <code>-128</code> to <code>+127</code> to skip <code>eol</code> checking. You may set this in <code>jmeter.properties</code> file as well with <code>eolByte</code> property. If you set this in TCP Sampler Config and in <code>jmeter.properties</code> file at the same time, the setting value in the TCP Sampler Config will be used.</property>
   <property name="Connect Timeout" required="No">Connect Timeout (milliseconds, <code>0</code> disables).</property>
   <property name="Response Timeout" required="No">Response Timeout (milliseconds, <code>0</code> disables).</property>
   <property name="Set NoDelay" required="Yes">See <code>java.net.Socket.setTcpNoDelay()</code>.
   If selected, this will disable Nagle's algorithm, otherwise Nagle's algorithm will be used.</property>
   <property name="Text to Send" required="Yes">Text to be sent</property>
   <property name="Login User" required="No">User Name - not used by default implementation</property>
   <property name="Password" required="No">Password - not used by default implementation (N.B. this is stored unencrypted in the test plan)</property>
 </properties>
 </component>
 
 <component name="JMS Publisher" index="&sect-num;.1.13" width="854" height="796" screenshot="jmspublisher.png">
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
   <property name="use JNDI properties file" required="Yes">use <code>jndi.properties</code>. 
   Note that the file must be on the classpath - e.g. by updating the <code>user.classpath</code> JMeter property.
   If this option is not selected, JMeter uses the "<code>JNDI Initial Context Factory</code>" and "<code>Provider URL</code>" fields
   to create the connection.
   </property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="Yes, unless using jndi.properties">The URL for the jms provider</property>
   <property name="Destination" required="Yes">The message destination (topic or queue name)</property>
   <property name="Setup" required="Yes">The destination setup type. With <code>At startup</code>, the destination name is static (i.e. always same name during the test), with <code>Each sample</code>, the destination name is dynamic and is evaluate at each sample (i.e. the destination name may be a variable)</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password (N.B. this is stored unencrypted in the test plan)</property>
   <property name="Expiration" required="No">
       The expiration time (in milliseconds) of the message before it becomes obsolete.
       If you do not specify an expiration time, the default value is <code>0</code> (never expires). 
   </property>
   <property name="Priority" required="No">
       The priority level of the message. There are ten priority levels from <code>0</code> (lowest) to <code>9</code> (highest). 
       If you do not specify a priority level, the default level is <code>4</code>. 
   </property>
+  <property name="Reconnect on error codes (regex)" required="No">Regular expression for JMSException error codes which force reconnection. If empty no reconnection will be done</property>
   <property name="Number of samples to aggregate" required="Yes">Number of samples to aggregate</property>
   <property name="Message source" required="Yes">Where to obtain the message:
   <dl>
     <dt><code>From File</code></dt><dd>means the referenced file will be read and reused by all samples. If file name changes it is reloaded since JMeter 3.0</dd>
     <dt><code>Random File from folder specified below</code></dt><dd>means a random file will be selected from folder specified below, this folder must contain either files with extension <code>.dat</code> for Bytes Messages, or files with extension <code>.txt</code> or <code>.obj</code> for Object or Text messages</dd>
     <dt><code>Text area</code></dt><dd>The Message to use either for Text or Object message</dd>
   </dl>
   </property>
   <property name="Message type" required="Yes">Text, Map, Object message or Bytes Message</property>
   <property name="Use non-persistent delivery mode?" required="No">
       Whether to set <code>DeliveryMode.NON_PERSISTENT</code> (defaults to <code>false</code>)
   </property>
   <property name="JMS Properties" required="No">
       The JMS Properties are properties specific for the underlying messaging system.
       You can setup the name, the value and the class (type) of value. Default type is <code>String</code>.
       For example: for WebSphere 5.1 web services you will need to set the JMS Property targetService to test
       webservices through JMS.
   </property>
 
 </properties>
 <p>
 For the MapMessage type, JMeter reads the source as lines of text.
 Each line must have 3 fields, delimited by commas.
 The fields are:</p>
 <ul>
 <li>Name of entry</li>
 <li>Object class name, e.g. "<code>String</code>" (assumes <code>java.lang</code> package if not specified)</li>
 <li>Object string value</li>
 </ul>
 For each entry, JMeter adds an Object with the given name.
 The value is derived by creating an instance of the class, and using the <code>valueOf(String)</code> method to convert the value if necessary.
 For example:
 <source>
 name,String,Example
 size,Integer,1234
 </source>
 This is a very simple implementation; it is not intended to support all possible object types.
 <note> 
 The Object message is implemented and works as follow:
 <ul>
 <li>Put the JAR that contains your object and its dependencies in <code>jmeter_home/lib/</code> folder</li>
 <li>Serialize your object as XML using XStream</li>
 <li>Either put result in a file suffixed with <code>.txt</code> or <code>.obj</code> or put XML content directly in Text Area</li>  
 </ul>
 Note that if message is in a file, replacement of properties will not occur while it will if you use Text Area.
 </note>
 
 <p>
 The following table shows some values which may be useful when configuring JMS:
 </p>
 <table>
 <tr>
 <!-- Anakia does not like th cell without any text -->
 <th>Apache <a href="http://activemq.apache.org/">ActiveMQ</a></th>
 <th>Value(s)</th>
 <th>Comment</th>
 </tr>
 <tr><td>Context Factory</td><td><code>org.apache.activemq.jndi.ActiveMQInitialContextFactory</code></td><td>.</td></tr>
 <tr><td>Provider URL</td><td><code>vm://localhost</code></td><td></td></tr>
 <tr><td>Provider URL</td><td><code>vm:(broker:(vm://localhost)?persistent=false)</code></td><td>Disable persistence</td></tr>
 <tr><td>Queue Reference</td><td><code>dynamicQueues/QUEUENAME</code></td>
 <td><a href="http://activemq.apache.org/jndi-support.html#JNDISupport-Dynamicallycreatingdestinations">Dynamically define</a> the QUEUENAME to JNDI</td></tr>
 <tr><td>Topic Reference</td><td><code>dynamicTopics/TOPICNAME</code></td>
 <td><a href="http://activemq.apache.org/jndi-support.html#JNDISupport-Dynamicallycreatingdestinations">Dynamically define</a> the TOPICNAME to JNDI</td></tr>
 </table>
 </component>
 
 <component name="JMS Subscriber" index="&sect-num;.1.14"  width="739" height="607" screenshot="jmssubscriber.png">
 <note>BETA CODE - the code is still subject to change</note>
     <description>
         <p>
         JMS Publisher will subscribe to messages in a given destination (topic or queue). For those not
         familiar with JMS, it is the J2EE specification for messaging. There are
         numerous JMS servers on the market and several open source options.
         </p>
         <br></br>
 <note>JMeter does not include any JMS implementation jar; this must be downloaded from the JMS provider and put in the lib directory</note>
     </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="use JNDI properties file" required="Yes">use <code>jndi.properties</code>. 
   Note that the file must be on the classpath - e.g. by updating the <code>user.classpath</code> JMeter property.
   If this option is not selected, JMeter uses the "<code>JNDI Initial Context Factory</code>" and "<code>Provider URL</code>" fields
   to create the connection.
   </property>
   <property name="JNDI Initial Context Factory" required="No">Name of the context factory</property>
   <property name="Provider URL" required="No">The URL for the jms provider</property>
   <property name="Destination" required="Yes">the message destination (topic or queue name)</property>
   <property name="Durable Subscription ID" required="No">The ID to use for a durable subscription. On first 
   use the respective queue will automatically be generated by the JMS provider if it does not exist yet.</property>
   <property name="Client ID" required="No">The Client ID to use when you use a durable subscription. 
   Be sure to add a variable like <code>${__threadNum}</code> when you have more than one Thread.</property>
   <property name="JMS Selector" required="No">Message Selector as defined by JMS specification to extract only 
   messages that respect the Selector condition. Syntax uses subpart of SQL 92.</property>
   <property name="Setup" required="Yes">The destination setup type. With <code>At startup</code>, the destination name is static (i.e. always same name during the test), with <code>Each sample</code>, the destination name is dynamic and is evaluate at each sample (i.e. the destination name may be a variable)</property>
   <property name="Authentication" required="Yes">Authentication requirement for the JMS provider</property>
   <property name="User" required="No">User Name</property>
   <property name="Password" required="No">Password (N.B. this is stored unencrypted in the test plan)</property>
   <property name="Number of samples to aggregate" required="Yes">number of samples to aggregate</property>
   <property name="Save response" required="Yes">should the sampler store the response. If not, only the response length is returned.</property>
   <property name="Timeout" required="Yes">Specify the timeout to be applied, in milliseconds. <code>0</code>=none. 
   This is the overall aggregate timeout, not per sample.</property>
   <property name="Client" required="Yes">Which client implementation to use.
   Both of them create connections which can read messages. However they use a different strategy, as described below:
   <dl>
   <dt><code>MessageConsumer.receive()</code></dt><dd>calls <code>receive()</code> for every requested message. 
   Retains the connection between samples, but does not fetch messages unless the sampler is active.
   This is best suited to Queue subscriptions. 
   </dd>
   <dt><code>MessageListener.onMessage()</code></dt><dd>establishes a Listener that stores all incoming messages on a queue. 
   The listener remains active after the sampler completes.
   This is best suited to Topic subscriptions.</dd>
   </dl>
   </property>
   <property name="Stop between samples?" required="Yes">
   If selected, then JMeter calls <code>Connection.stop()</code> at the end of each sample (and calls <code>start()</code> before each sample).
   This may be useful in some cases where multiple samples/threads have connections to the same queue.
   If not selected, JMeter calls <code>Connection.start()</code> at the start of the thread, and does not call <code>stop()</code> until the end of the thread.
   </property>
   <property name="Separator" required="No">
   Separator used to separate messages when there is more than one (related to setting Number of samples to aggregate).
   Note that <code>\n</code>, <code>\r</code>, <code>\t</code> are accepted.
   </property>
+  <property name="Reconnect on error codes (regex)" required="No">Regular expression for JMSException error codes which force reconnection. If empty no reconnection will be done</property>
+  <property name="Pause between errors (ms)" required="No">Pause in milliseconds that Subscriber will make when an error occurs</property>
 </properties>
 </component>
 
 <component name="JMS Point-to-Point" index="&sect-num;.1.15"  width="882" height="804" screenshot="jms/JMS_Point-to-Point.png">
 <note>BETA CODE - the code is still subject to change</note>
     <description>
         <p>
         This sampler sends and optionally receives JMS Messages through point-to-point connections (queues).
         It is different from pub/sub messages and is generally used for handling transactions.
         </p>
         <p>
         <code>Request Only</code> will typically be used to put load on a JMS System.<br></br>
         <code>Request Response</code> will be used when you want to test response time of a JMS service that processes messages sent to the Request Queue as this mode will wait for the response on the Reply queue sent by this service.<br></br>
         </p>
         <p>
         JMeter use the properties <code>java.naming.security.[principal|credentials]</code> - if present -
         when creating the Queue Connection. If this behaviour is not desired, set the JMeter property
         <code>JMSSampler.useSecurity.properties=false</code>
         </p>
         <br></br>
 <note>JMeter does not include any JMS implementation jar; this must be downloaded from the JMS provider and put in the lib directory</note>
     </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="QueueConnection Factory" required="Yes">
     The JNDI name of the queue connection factory to use for connecting to the messaging system.
   </property>
   <property name="JNDI Name Request queue" required="Yes">
     This is the JNDI name of the queue to which the messages are sent.
   </property>
   <property name="JNDI Name Reply queue" required="No">
     The JNDI name of the receiving queue. If a value is provided here and the communication style is <code>Request Response</code>
     this queue will be monitored for responses to the requests sent.
   </property>
   <property name="JMS Selector" required="No">
     Message Selector as defined by JMS specification to extract only 
     messages that respect the Selector condition. Syntax uses subpart of SQL 92.
   </property>
   <property name="Communication style" required="Yes">
     The Communication style can be <code>Request Only</code> (also known as Fire and Forget) or <code>Request Response</code>:
     <dl>
     <dt><code>Request Only</code></dt><dd> will only send messages and will not monitor replies. As such it can be used to put load on a system.</dd>
     <dt><code>Request Response</code></dt><dd> will send messages and monitor the replies it receives. Behaviour depends on the value of the JNDI Name Reply Queue.
     If JNDI Name Reply Queue has a value, this queue is used to monitor the results. Matching of request and reply is done with
     the message id of the request and the correlation id of the reply. If the JNDI Name Reply Queue is empty, then
     temporary queues will be used for the communication between the requestor and the server.
     This is very different from the fixed reply queue. With temporary queues the sending thread will block until the reply message has been received.
     With <code>Request Response</code> mode, you need to have a Server that listens to messages sent to Request Queue and sends replies to 
     queue referenced by <code>message.getJMSReplyTo()</code>.</dd>
     </dl>
   </property>
   <property name="Use alternate fields for message correlation" required="Yes">
     These check-boxes select the fields which will be used for matching the response message with the original request.
     <dl>
     <dt><code>Use Request Message Id</code></dt><dd>if selected, the request JMSMessageID will be used, 
     otherwise the request JMSCorrelationID will be used. 
     In the latter case the correlation id must be specified in the request.</dd>
     <dt><code>Use Response Message Id</code></dt><dd>if selected, the response JMSMessageID will be used, 
     otherwise the response JMSCorrelationID will be used.
     </dd>
     </dl>
     There are two frequently used JMS Correlation patterns:
     <dl>
     <dt>JMS Correlation ID Pattern</dt>
     <dd> i.e. match request and response on their correlation Ids
     => deselect both checkboxes, and provide a correlation id.</dd>
     <dt>JMS Message ID Pattern</dt>
     <dd>i.e. match request message id with response correlation id
     => select "Use Request Message Id" only.
     </dd>
     </dl>
     In both cases the JMS application is responsible for populating the correlation ID as necessary.
     <note>if the same queue is used to send and receive messages, 
     then the response message will be the same as the request message.
     In which case, either provide a correlation id and clear both checkboxes;
     or select both checkboxes to use the message Id for correlation.
     This can be useful for checking raw JMS throughput.</note>
   </property>
   <property name="Timeout" required="Yes">
       The timeout in milliseconds for the reply-messages. If a reply has not been received within the specified
       time, the specific testcase fails and the specific reply message received after the timeout is discarded.
       Default value is <code>2000</code> ms.
   </property>
   <property name="Expiration" required="No">
       The expiration time (in milliseconds) of the message before it becomes obsolete.
       If you do not specify an expiration time, the default value is <code>0</code> (never expires). 
   </property>
   <property name="Priority" required="No">
       The priority level of the message. There are ten priority levels from <code>0</code> (lowest) to <code>9</code> (highest). 
       If you do not specify a priority level, the default level is <code>4</code>. 
   </property>
   <property name="Use non-persistent delivery mode?" required="Yes">
       Whether to set <code>DeliveryMode.NON_PERSISTENT</code>.
   </property>
   <property name="Content" required="No">
       The content of the message.
   </property>
   <property name="JMS Properties" required="No">
       The JMS Properties are properties specific for the underlying messaging system.
       You can setup the name, the value and the class (type) of value. Default type is <code>String</code>.
       For example: for WebSphere 5.1 web services you will need to set the JMS Property targetService to test
       webservices through JMS.
   </property>
   <property name="Initial Context Factory" required="No">
     The Initial Context Factory is the factory to be used to look up the JMS Resources.
   </property>
   <property name="JNDI properties" required="No">
      The JNDI Properties are the specific properties for the underlying JNDI implementation.
   </property>
   <property name="Provider URL" required="No">
     The URL for the jms provider.
   </property>
 </properties>
 </component>
 
 
 
 <component name="JUnit Request" index="&sect-num;.1.16"  width="397" height="536" screenshot="junit_sampler.png">
 <description>
 The current implementation supports standard JUnit convention and extensions. It also
 includes extensions like <code>oneTimeSetUp</code> and <code>oneTimeTearDown</code>. The sampler works like the
 <complink name="Java Request" /> with some differences.
 <ul>
 <li>rather than use Jmeter's test interface, it scans the jar files for classes extending JUnit's <code>TestCase</code> class. That includes any class or subclass.</li>
 <li>JUnit test jar files should be placed in <code>jmeter/lib/junit</code> instead of <code>/lib</code> directory.
 You can also use the "<code>user.classpath</code>" property to specify where to look for <code>TestCase</code> classes.</li>
 <li>JUnit sampler does not use name/value pairs for configuration like the <complink name="Java Request" />. The sampler assumes <code>setUp</code> and <code>tearDown</code> will configure the test correctly.</li>
 <li>The sampler measures the elapsed time only for the test method and does not include <code>setUp</code> and <code>tearDown</code>.</li>
 <li>Each time the test method is called, Jmeter will pass the result to the listeners.</li>
 <li>Support for <code>oneTimeSetUp</code> and <code>oneTimeTearDown</code> is done as a method. Since Jmeter is multi-threaded, we cannot call <code>oneTimeSetUp</code>/<code>oneTimeTearDown</code> the same way Maven does it.</li>
 <li>The sampler reports unexpected exceptions as errors.
 There are some important differences between standard JUnit test runners and JMeter's
 implementation. Rather than make a new instance of the class for each test, JMeter
 creates 1 instance per sampler and reuses it.
 This can be changed with checkbox "<code>Create a new instance per sample</code>".</li>
 </ul>
 The current implementation of the sampler will try to create an instance using the string constructor first. If the test class does not declare a string constructor, the sampler will look for an empty constructor. Example below:
 <example title="JUnit Constructors" anchor="junit_constructor_example">
 Empty Constructor:
 <source>
 public class myTestCase {
   public myTestCase() {}
 }
 </source>
 String Constructor:
 <source>
 public class myTestCase {
   public myTestCase(String text) {
     super(text);
   }
 }
 </source>
 </example>
 By default, Jmeter will provide some default values for the success/failure code and message. Users should define a set of unique success and failure codes and use them uniformly across all tests.
 <note>
 <h3>General Guidelines</h3>
 If you use <code>setUp</code> and <code>tearDown</code>, make sure the methods are declared public. If you do not, the test may not run properly.
 <br></br>
 Here are some general guidelines for writing JUnit tests so they work well with Jmeter. Since Jmeter runs multi-threaded, it is important to keep certain things in mind.
 <ul>
 <li>Write the <code>setUp</code> and <code>tearDown</code> methods so they are thread safe. This generally means avoid using static members.</li>
 <li>Make the test methods discrete units of work and not long sequences of actions. By keeping the test method to a discrete operation, it makes it easier to combine test methods to create new test plans.</li>
 <li>Avoid making test methods depend on each other. Since Jmeter allows arbitrary sequencing of test methods, the runtime behavior is different than the default JUnit behavior.</li>
 <li>If a test method is configurable, be careful about where the properties are stored. Reading the properties from the Jar file is recommended.</li>
 <li>Each sampler creates an instance of the test class, so write your test so the setup happens in <code>oneTimeSetUp</code> and <code>oneTimeTearDown</code>.</li>
 </ul>
 </note>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Search for JUnit4 annotations" required="Yes">Select this to search for JUnit4 tests (<code>@Test</code> annotations)</property>
   <property name="Package filter" required="">Comma separated list of packages to show. Example, <code>org.apache.jmeter</code>,<code>junit.framework</code>.</property>
   <property name="Class name" required="Yes">Fully qualified name of the JUnit test class.</property>
   <property name="Constructor string" required="">String pass to the string constructor. If a string is set, the sampler will use the
    string constructor instead of the empty constructor.</property>
   <property name="Test method" required="Yes">The method to test.</property>
   <property name="Success message" required="">A descriptive message indicating what success means.</property>
   <property name="Success code" required="">An unique code indicating the test was successful.</property>
   <property name="Failure message" required="">A descriptive message indicating what failure means.</property>
   <property name="Failure code" required="">An unique code indicating the test failed.</property>
   <property name="Error message" required="">A description for errors.</property>
   <property name="Error code" required="">Some code for errors. Does not need to be unique.</property>
   <property name="Do not call setUp and tearDown" required="Yes">Set the sampler not to call <code>setUp</code> and <code>tearDown</code>.
    By default, <code>setUp</code> and <code>tearDown</code> should be called. Not calling those methods could affect the test and make it inaccurate.
     This option should only be used with calling <code>oneTimeSetUp</code> and <code>oneTimeTearDown</code>. If the selected method is <code>oneTimeSetUp</code> or <code>oneTimeTearDown</code>,
      this option should be checked.</property>
   <property name="Append assertion errors" required="Yes">Whether or not to append assertion errors to the response message.</property>
   <property name="Append runtime exceptions" required="Yes">Whether or not to append runtime exceptions to the response message. Only applies if "<code>Append assertion errors</code>" is not selected.</property>
   <property name="Create a new Instance per sample" required="Yes">Whether or not to create a new JUnit instance for each sample. Defaults to false, meaning JUnit <code>TestCase</code> is created one and reused.</property>
 </properties>
 <p>
 The following JUnit4 annotations are recognised:
 </p>
 <dl>
 <dt><code>@Test</code></dt><dd>used to find test methods and classes. The "<code>expected</code>" and "<code>timeout</code>" attributes are supported.</dd>
 <dt><code>@Before</code></dt><dd>treated the same as <code>setUp()</code> in JUnit3</dd>
 <dt><code>@After</code></dt><dd>treated the same as <code>tearDown()</code> in JUnit3</dd>
 <dt><code>@BeforeClass</code>, <code>@AfterClass</code></dt><dd>treated as test methods so they can be run independently as required</dd>
 </dl>
 <note>
 Note that JMeter currently runs the test methods directly, rather than leaving it to JUnit.
 This is to allow the <code>setUp</code>/<code>tearDown</code> methods to be excluded from the sample time.
 </note>
 </component>
 
 <component name="Mail Reader Sampler"  index="&sect-num;.1.17"  width="595" height="413" screenshot="mailreader_sampler.png">
 <description>
 <p>
 The Mail Reader Sampler can read (and optionally delete) mail messages using POP3(S) or IMAP(S) protocols.
 </p>
 </description>
 <properties>
 <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
 <property name="Server Type" required="Yes">The protocol used by the provider: e.g. <code>pop3</code>, <code>pop3s</code>, <code>imap</code>, <code>imaps</code>.
 or another string representing the server protocol.
 For example <code>file</code> for use with the read-only mail file provider.
 The actual provider names for POP3 and IMAP are <code>pop3</code> and <code>imap</code>
 </property>
 <property name="Server" required="Yes">Hostname or IP address of the server. See below for use with <code>file</code> protocol.</property>
 <property name="Port" required="No">Port to be used to connect to the server (optional)</property>
 <property name="Username" required="">User login name</property>
 <property name="Password" required="">User login password (N.B. this is stored unencrypted in the test plan)</property>
 <property name="Folder" required="Yes, if using IMAP(S)">The IMAP(S) folder to use. See below for use with <code>file</code> protocol.</property>
 <property name="Number of messages to retrieve" required="Yes">Set this to retrieve all or some messages</property>
 <property name="Fetch headers only" required="Yes">If selected, only the message headers will be retrieved.</property>
 <property name="Delete messages from the server" required="Yes">If set, messages will be deleted after retrieval</property>
 <property name="Store the message using MIME" required="Yes">Whether to store the message as MIME. 
 If so, then the entire raw message is stored in the Response Data; the headers are not stored as they are available in the data. 
 If not, the message headers are stored as Response Headers. 
 A few headers are stored (<code>Date</code>, <code>To</code>, <code>From</code>, <code>Subject</code>) in the body.
 </property>
 <property name="Use no security features" required="">Indicates that the connection to the server does not use any security protocol.</property>
 <property name="Use SSL" required="">Indicates that the connection to the server must use the SSL protocol.</property>
 <property name="Use StartTLS" required="">Indicates that the connection to the server should attempt to start the TLS protocol.</property>
 <property name="Enforce StartTLS" required="">If the server does not start the TLS protocol the connection will be terminated.</property>
 <property name="Trust All Certificates" required="">When selected it will accept all certificates independent of the CA.</property>
 <property name="Use local truststore" required="">When selected it will only accept certificates that are locally trusted.</property>
 <property name="Local truststore" required="">Path to file containing the trusted certificates.
 Relative paths are resolved against the current directory.
 <br />Failing that, against the directory containing the test script (JMX file).
 </property>
 </properties>
 <p>
 Messages are stored as subsamples of the main sampler.
 Multipart message parts are stored as subsamples of the message.
 </p>
 <p>
 <b>Special handling for "<code>file</code>" protocol:</b><br></br>
 The <code>file</code> JavaMail provider can be used to read raw messages from files.
 The <code>server</code> field is used to specify the path to the parent of the <code>folder</code>.
 Individual message files should be stored with the name <code>n.msg</code>,
 where <code>n</code> is the message number.
 Alternatively, the <code>server</code> field can be the name of a file which contains a single message.
 The current implementation is quite basic, and is mainly intended for debugging purposes. 
 </p>
 </component>
 
 <component name="Test Action" index="&sect-num;.1.18" width="467" height="184" screenshot="test_action.png">
 <description>
 The Test Action sampler is a sampler that is intended for use in a conditional controller.
 Rather than generate a sample, the test element either pauses or stops the selected target.
 <p>This sampler can also be useful in conjunction with the Transaction Controller, as it allows
 pauses to be included without needing to generate a sample. 
 For variable delays, set the pause time to zero, and add a Timer as a child.</p>
 <p>
 The "<code>Stop</code>" action stops the thread or test after completing any samples that are in progress.
 The "<code>Stop Now</code>" action stops the test without waiting for samples to complete; it will interrupt any active samples.
 If some threads fail to stop within the 5 second time-limit, a message will be displayed in GUI mode.
 You can try using the <code>Stop</code> command to see if this will stop the threads, but if not, you should exit JMeter.
 In non-GUI mode, JMeter will exit if some threads fail to stop within the 5 second time limit.
 <note>The time to wait can be changed using the JMeter property <code>jmeterengine.threadstop.wait</code>. The time is given in milliseconds.</note>
 </p>
 </description>
 <properties>
   <property name="Name" required="">Descriptive name for this element that is shown in the tree.</property>
   <property name="Target" required="Yes"><code>Current Thread</code> / <code>All Threads</code> (ignored for Pause)</property>
   <property name="Action" required="Yes"><code>Pause</code> / <code>Stop</code> / <code>Stop Now</code> / <code>Go to next loop iteration</code></property>
   <property name="Duration" required="Yes, if Pause is selected">How long to pause for (milliseconds)</property>
 </properties>
 </component>
 
 
 <component name="SMTP Sampler"  index="&sect-num;.1.19"  width="825" height="728" screenshot="smtp_sampler.png">
 <description>
 <p>
 The SMTP Sampler can send mail messages using SMTP/SMTPS protocol. 
 It is possible to set security protocols for the connection (SSL and TLS), as well as user authentication. 
 If a security protocol is used a verification on the server certificate will occur. <br></br>
 Two alternatives to handle this verification are available:<br></br>
 </p>
 <dl>
 <dt><code>Trust all certificates</code></dt><dd>This will ignore certificate chain verification</dd>
 <dt><code>Use a local truststore</code></dt><dd>With this option the certificate chain will be validated against the local truststore file.</dd>
 </dl>
 </description>
 <properties>
 <property name="Server" required="Yes">Hostname or IP address of the server. See below for use with <code>file</code> protocol.</property>
 <property name="Port" required="No">Port to be used to connect to the server.
 Defaults are: SMTP=25, SSL=465, StartTLS=587
 </property>
 <property name="Connection timeout" required="No">Connection timeout value in milliseconds (socket level). Default is infinite timeout.</property>
 <property name="Read timeout" required="No">Read timeout value in milliseconds (socket level). Default is infinite timeout.</property>
 <property name="Address From" required="Yes">The from address that will appear in the e-mail</property>
 <property name="Address To" required="Yes, unless CC or BCC is specified">The destination e-mail address (multiple values separated by "<code>;</code>")</property>
 <property name="Address To CC" required="No">Carbon copy destinations e-mail address (multiple values separated by "<code>;</code>")</property>
 <property name="Address To BCC" required="No">Blind carbon copy destinations e-mail address (multiple values separated by "<code>;</code>")</property>
 <property name="Address Reply-To" required="No">Alternate Reply-To address (multiple values separated by "<code>;</code>")</property>
 <property name="Use Auth" required="">Indicates if the SMTP server requires user authentication</property>
 <property name="Username" required="">User login name</property>
 <property name="Password" required="">User login password (N.B. this is stored unencrypted in the test plan)</property>
 <property name="Use no security features" required="">Indicates that the connection to the SMTP server does not use any security protocol.</property>
 <property name="Use SSL" required="">Indicates that the connection to the SMTP server must use the SSL protocol.</property>
 <property name="Use StartTLS" required="">Indicates that the connection to the SMTP server should attempt to start the TLS protocol.</property>
 <property name="Enforce StartTLS" required="">If the server does not start the TLS protocol the connection will be terminated.</property>
 <property name="Trust All Certificates" required="">When selected it will accept all certificates independent of the CA.</property>
 <property name="Use local truststore" required="">When selected it will only accept certificates that are locally trusted.</property>
 <property name="Local truststore" required="">Path to file containing the trusted certificates.
 Relative paths are resolved against the current directory.
 <br />Failing that, against the directory containing the test script (JMX file).
 </property>
 <property name="Subject" required="">The e-mail message subject.</property>
 <property name="Suppress Subject Header" required="">If selected, the "<code>Subject:</code>" header is omitted from the mail that is sent. 
 This is different from sending an empty "<code>Subject:</code>" header, though some e-mail clients may display it identically.</property>
 <property name="Include timestamp in subject" required="">Includes the <code>System.currentTimemillis()</code> in the subject line.</property>
 <property name="Add Header" required="No">Additional headers can be defined using this button.</property>
 <property name="Message" required="">The message body.</property>
 <property name="Send plain body (i.e. not multipart/mixed)">
 If selected, then send the body as a plain message, i.e. not <code>multipart/mixed</code>, if possible.
 If the message body is empty and there is a single file, then send the file contents as the message body.
 <note>
 Note: If the message body is not empty, and there is at least one attached file, then the body is sent as <code>multipart/mixed</code>.
 </note>
 </property>
 <property name="Attach files" required="">Files to be attached to the message.</property>
 <property name="Send .eml" required="">If set, the <code>.eml</code> file will be sent instead of the entries in the <code>Subject</code>, <code>Message</code>, and <code>Attach file(s)</code> fields</property>
 <property name="Calculate message size" required="">Calculates the message size and stores it in the sample result.</property>
 <property name="Enable debug logging?" required="">If set, then the "<code>mail.debug</code>" property is set to "<code>true</code>"</property>
 </properties>
 </component>
 
 <component name="OS Process Sampler"  index="&sect-num;.1.20"  width="683" height="582" screenshot="os_process_sampler.png">
 <description>
 <p>
 The OS Process Sampler is a sampler that can be used to execute commands on the local machine.<br></br>
 It should allow execution of any command that can be run from the command line.<br></br>
 Validation of the return code can be enabled, and the expected return code can be specified.<br></br>
 </p>
 <p>
 Note that OS shells generally provide command-line parsing. 
 This varies between OSes, but generally the shell will split parameters on white-space.
 Some shells expand wild-card file names; some don't.
 The quoting mechanism also varies between OSes.
 The sampler deliberately does not do any parsing or quote handling.
 The command and its parameters must be provided in the form expected by the executable.
 This means that the sampler settings will not be portable between OSes.
 </p>
 <p>
 Many OSes have some built-in commands which are not provided as separate executables. 
 For example the Windows <code>DIR</code> command is part of the command interpreter (<code>CMD.EXE</code>).
 These built-ins cannot be run as independent programs, but have to be provided as arguments to the appropriate command interpreter.
 </p>
 <p>
 For example, the Windows command-line: <code>DIR C:\TEMP</code> needs to be specified as follows:
 </p>
 <dl>
 <dt>Command:</dt><dd><code>CMD</code></dd>
 <dt>Param 1:</dt><dd><code>/C</code></dd>
 <dt>Param 2:</dt><dd><code>DIR</code></dd>
 <dt>Param 3:</dt><dd><code>C:\TEMP</code></dd>
 </dl>
 </description>
 <properties>
 <property name="Command" required="Yes">The program name to execute.</property>
 <property name="Working directory" required="No">Directory from which command will be executed, defaults to folder referenced by "<code>user.dir</code>" System property</property>
 <property name="Command Parameters" required="No">Parameters passed to the program name.</property>
 <property name="Environment Parameters" required="No">Key/Value pairs added to environment when running command.</property>
 <property name="Standard input (stdin)" required="No">Name of file from which input is to be taken (<code>STDIN</code>).</property>
 <property name="Standard output (stdout" required="No">Name of output file for standard output (<code>STDOUT</code>). 
 If omitted, output is captured and returned as the response data.</property>
 <property name="Standard error (stderr)" required="No">Name of output file for standard error (<code>STDERR</code>). 
 If omitted, output is captured and returned as the response data.</property>
 <property name="Check Return Code" required="No">If checked, sampler will compare return code with <code>Expected Return Code</code>.</property>
 <property name="Expected Return Code" required="No">Expected return code for System Call, required if "<code>Check Return Code</code>" is checked. Note 500 is used as an error indicator in JMeter so you should not use it.</property>
 <property name="Timeout" required="No">Timeout for command in milliseconds, defaults to <code>0</code>, which means <em>no</em> timeout.
 If the timeout expires before the command finishes, JMeter will attempt to kill the OS process.
 </property>
 </properties>
 </component>
 
 <component name="MongoDB Script (DEPRECATED)" index="&sect-num;.1.21" width="847" height="635" screenshot="mongodb-script.png">
 <description><p>This sampler lets you send a Request to a MongoDB.</p>
 <p>Before using this you need to set up a
 <complink name="MongoDB Source Config"/> Configuration element
 </p>
 <note>This Element currently uses <code>com.mongodb.DB#eval</code> which takes a global write lock causing a performance impact on the database, see <a href="http://docs.mongodb.org/manual/reference/method/db.eval/"><code>db.eval()</code></a>.
 So it is better to avoid using this element for load testing and use JSR223+Groovy scripting using <a href="../api/org/apache/jmeter/protocol/mongodb/config/MongoDBHolder.html">MongoDBHolder</a> instead.
 MongoDB Script is more suitable for functional testing or test setup (setup/teardown threads)</note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this sampler that is shown in the tree.</property>
         <property name="MongoDB Source" required="Yes">
         Name of the JMeter variable that the MongoDB connection is bound to.
         This must agree with the '<code>MongoDB Source</code>' field of a MongoDB Source Config.
         </property>
         <property name="Database Name" required="Yes">Database Name, will be used in your script
         </property>
         <property name="Username" required="No">
         </property>
         <property name="Password" required="No">
         </property>        
         <property name="Script" required="Yes">
         Mongo script as it would be used in MongoDB shell
         </property>        
 </properties>
 
 <links>
         <complink name="MongoDB Source Config"/>
 </links>
 <note>Ensure Variable Name is unique across Test Plan.</note>
 </component>
 
 <a href="#">^</a>
 
 </section>
 
 <section name="&sect-num;.2 Logic Controllers" anchor="logic_controllers">
 <description>
     <br>Logic Controllers determine the order in which Samplers are processed.</br>
 </description>
 
 <component name="Simple Controller" index="&sect-num;.2.1" anchor="simple_controller" width="330" height="77" screenshot="logic-controller/simple-controller.png">
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
 following order: Ant Home Page, Ant News Page, Log4J Home Page, Log4J History Page.</p>
 <p>
 Note, the File Reporter
 is configured to store the results in a file named "<code>simple-test.dat</code>" in the current directory.</p>
 <figure width="585" height="213" image="logic-controller/simple-example.png">Figure 6 Simple Controller Example</figure>
 
 </example>
 </component>
 
 <component name="Loop Controller" index="&sect-num;.2.2" anchor="loop" width="326" height="114" screenshot="logic-controller/loop-controller.png">
 <description><p>If you add Generative or Logic Controllers to a Loop Controller, JMeter will
 loop through them a certain number of times, in addition to the loop value you
 specified for the Thread Group.  For example, if you add one HTTP Request to a
 Loop Controller with a loop count of two, and configure the Thread Group loop
 count to three, JMeter will send a total of <code>2 * 3 = 6</code> HTTP Requests.
 </p></description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Loop Count" required="Yes, unless &quot;Forever&quot; is checked">
                 The number of times the subelements of this controller will be iterated each time
                 through a test run.
                 <p>The value <code>-1</code> is equivalent to checking the <code>Forever</code> toggle.</p>
                 <p><b>Special Case:</b> The Loop Controller embedded in the <a href="test_plan.html#thread_group">Thread Group</a>
                 element behaves slightly different.  Unless set to forever, it stops the test after
                 the given number of iterations have been done.</p>
                 <note>When using a function in this field, be aware it may be evaluated multiple times.
                 Example using <code>__Random</code> will evaluate it to a different value for each child samplers of Loop Controller and result into unwanted behaviour.</note></property>
 </properties>
 
 <example title="Looping Example" anchor="loop_example">
 
 <p><a href="../demos/LoopTestPlan.jmx">Download</a> this example (see Figure 4).
 In this example, we created a Test Plan that sends a particular HTTP Request
 only once and sends another HTTP Request five times.</p>
 
 <figure width="506" height="158" image="logic-controller/loop-example.png">Figure 4 - Loop Controller Example</figure>
 
 <p>We configured the Thread Group for a single thread and a loop count value of
 one. Instead of letting the Thread Group control the looping, we used a Loop
 Controller.  You can see that we added one HTTP Request to the Thread Group and
 another HTTP Request to a Loop Controller.  We configured the Loop Controller
 with a loop count value of five.</p>
 <p>JMeter will send the requests in the following order: Home Page, News Page,
 News Page, News Page, News Page, and News Page.</p>
 <note>Note, the File Reporter
 is configured to store the results in a file named "<code>loop-test.dat</code>" in the current directory.</note>
 
 </example>
 
 </component>
 
 <component name="Once Only Controller" index="&sect-num;.2.3" anchor="once_only_controller" width="330" height="78" screenshot="logic-controller/once-only-controller.png">
 <description>
 <p>The Once Only Logic Controller tells JMeter to process the controller(s) inside it only once per Thread, and pass over any requests under it
 during further iterations through the test plan.</p>
 
 <p>The Once Only Controller will now execute always during the first iteration of any looping parent controller.  
 Thus, if the Once Only Controller is placed under a Loop Controller specified to loop 5 times, then the Once Only Controller will execute only on the first iteration through the Loop Controller 
 (i.e. every 5 times).</p>
 <p>
 Note this means the Once Only Controller will still behave as previously expected if put under a Thread Group (runs only once per test per Thread), 
 but now the user has more flexibility in the use of the Once Only Controller.</p>
 
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
 <figure width="233" height="138" image="logic-controller/once-only-example.png">Figure 5. Once Only Controller Example</figure>
 <p>Each JMeter thread will send the requests in the following order: Home Page, Bug Page,
 Bug Page, Bug Page.</p>
 <p>Note, the File Reporter is configured to store the results in a file named "<code>loop-test.dat</code>" in the current directory.</p>
 
 </example>
 <note>The behaviour of the Once Only controller under anything other than the
 Thread Group or a Loop Controller is not currently defined. Odd things may happen.</note>
 </component>
 
 <component name="Interleave Controller" index="&sect-num;.2.4"  width="626" height="127" screenshot="logic-controller/interleave-controller.png">
 <description><p>If you add Generative or Logic Controllers to an Interleave Controller, JMeter will alternate among each of the
 other controllers for each loop iteration. </p>
 </description>
 <properties>
         <property name="name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="ignore sub-controller blocks" required="No">If checked, the interleave controller will treat sub-controllers like single request elements and only allow one request per controller at a time.  </property>
         <property name="Interleave accross threads" required="No">If checked, the interleave controller will alternate among each of its children controllers for each loop iteration but accross all threads, for example in a 
         configuration with 4 threads and 3 child controllers, on first iteration
         thread 1 will run first child, thread 2 second child, thread 3 third child, thread 4 first child, on next iteration each thread will run the following child controller</property>
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
 <tr valign="top"><td>(and so on)</td><td>&hellip;</td></tr>
 </table>
 -->
 <example title="Simple Interleave Example" anchor="simple_interleave_example">
 
 <p><a href="../demos/InterleaveTestPlan.jmx">Download</a> this example (see Figure 1).  In this example,
 we configured the Thread Group to have two threads and a loop count of five, for a total of ten
 requests per thread. See the table below for the sequence JMeter sends the HTTP Requests.</p>
 
 <figure width="231" height="153" image="logic-controller/interleave.png">Figure 1 - Interleave Controller Example 1</figure>
 
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
 
 <figure width="251" height="250" image="logic-controller/interleave2.png">
         Figure 2 - Interleave Controller Example 2
 </figure>
 
 <p>The outer Interleave Controller alternates between the
 two inner ones.  Then, each inner Interleave Controller alternates between each of the HTTP Requests.  Each JMeter
 thread will send the requests in the following order: Home Page, Interleaved, Bug Page, Interleaved, CVS Page, Interleaved, and FAQ Page, Interleaved.</p>
 <p>Note, the File Reporter is configured to store the results in a file named "<code>interleave-test2.dat</code>" in the current directory.</p>
 
 <figure width="257" height="253" image="logic-controller/interleave3.png">
         Figure 3 - Interleave Controller Example 3
 </figure>
 <p>If the two interleave controllers under the main interleave controller were instead simple controllers, then the order would be: Home Page, CVS Page, Interleaved, Bug Page, FAQ Page, Interleaved.</p>
 <p>However, if "<code>ignore sub-controller blocks</code>" was checked on the main interleave controller, then the order would be: Home Page, Interleaved, Bug Page, Interleaved, CVS Page, Interleaved, and FAQ Page, Interleaved.</p>
 </example>
 </component>
 
 <component name="Random Controller" index="&sect-num;.2.5" width="328" height="100" screenshot="logic-controller/random-controller.png">
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
         <property name="ignore sub-controller blocks" required="No">If checked, the interleave controller will treat sub-controllers like single request elements and only allow one request per controller at a time.  </property>
 </properties>
 
 </component>
 
 
 
 <component name="Random Order Controller" index="&sect-num;.2.6" width="328" height="76" screenshot="randomordercontroller.png">
     <description>
         <p>The Random Order Controller is much like a Simple Controller in that it will execute each child
          element at most once, but the order of execution of the nodes will be random.</p>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
 </properties>
 </component>
 
 <component name="Throughput Controller" index="&sect-num;.2.7" width="329" height="167" screenshot="throughput_controller.png">
 <description>
 <p>The Throughput Controller allows the user to control how often it is executed.  
 There are two modes:
 <ul>
 <li>percent execution</li>
 <li>total executions</li>
 </ul>
 <dl>
   <dt><code>Percent executions</code></dt><dd>causes the controller to execute a certain percentage of the iterations through the test plan.</dd>
   <dt><code>Total executions</code></dt><dd>causes the controller to stop executing after a certain number of executions have occurred.</dd>
 </dl>
 Like the Once Only Controller, this setting is reset when a parent Loop Controller restarts.
 </p>
 <note>This controller is badly named, as it does not control throughput.
 Please refer to the <complink name="Constant Throughput Timer"/> for an element that can be used to adjust the throughput.
 </note>
 </description>
 <note>The Throughput Controller can yield very complex behavior when combined with other controllers - in particular with interleave or random controllers as parents (also very useful).</note>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Execution Style" required="Yes">Whether the controller will run in percent executions or total executions mode.</property>
         <property name="Throughput" required="Yes">A number.  For percent execution mode, a number from <code>0</code>-<code>100</code> that indicates the percentage of times the controller will execute.  "<code>50</code>" means the controller will execute during half the iterations through the test plan.  For total execution mode, the number indicates the total number of times the controller will execute.</property>
         <property name="Per User" required="No">If checked, per user will cause the controller to calculate whether it should execute on a per user (per thread) basis.  If unchecked, then the calculation will be global for all users.  For example, if using total execution mode, and uncheck "<code>per user</code>", then the number given for throughput will be the total number of executions made.  If "<code>per user</code>" is checked, then the total number of executions would be the number of users times the number given for throughput.</property>
 </properties>
 
 </component>
 
 <component name="Runtime Controller" index="&sect-num;.2.8" width="328" height="100" screenshot="runtimecontroller.png">
     <description>
         <p>The Runtime Controller controls how long its children are allowed to run.
         </p>
     </description>
 <properties>
     <property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
     <property name="Runtime (seconds)" required="Yes">Desired runtime in seconds</property>
 </properties>
 </component>
 
 <component name="If Controller" index="&sect-num;.2.9" width="497" height="131" screenshot="ifcontroller.png">
     <description>
         <p>The If Controller allows the user to control whether the test elements below it (its children) are run or not.</p>
         <p>
         By default, the condition is evaluated only once on initial entry, but you have the option to have it evaluated for every runnable element contained in the controller.
         </p>
         <p>
         The script can be processed as a variable expression, rather than requiring Javascript.
         It was always possible to use functions and variables in the Javascript condition, so long as they evaluated to "<code>true</code>" or "<code>false</code>";
         now this can be done without the overhead of using Javascript as well. For example, previously one could use the condition:
         <code>${__jexl3(${VAR} == 23)}</code> and this would be evaluated as <code>true</code>/<code>false</code>, the result would then be passed to Javascript
         which would then return <code>true</code>/<code>false</code>. If the Variable Expression option is selected, then the expression is evaluated
         and compared with "<code>true</code>", without needing to use Javascript.
         Also, variable expressions can return any value, whereas the
         Javascript condition must return "<code>true</code>"/"<code>false</code>" or an error is logged.
         </p>
         <note>
         No variables are made available to the script when the condition is interpreted as Javascript.
         If you need access to such variables, then select "<code>Interpret Condition as Variable Expression?</code>" and use
         a <code>__jexl3</code>, <code>__groovy</code> or <code>__javaScript()</code> (discouraged for performance) function call. You can then use the objects "<code>vars</code>", "<code>log</code>", "<code>ctx</code>" etc. in the script.
         </note>
         <note>
         To test if a variable is undefined (or null) do the following, suppose var is named <code>myVar</code>, expression will be:
         <source>"${myVar}" == "\${myVar}"</source>
         Or use:
         <source>"${myVar}" != "\${myVar}"</source>
         to test if a variable is defined and is not null.
         </note>
     </description>
 <properties>
     <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
     <property name="Condition (default Javascript)" required="Yes">By default the condition is interpreted as <b>Javascript</b> code that returns "<code>true</code>" or "<code>false</code>",
     but this can be overridden (see below)</property>
     <property name="Interpret Condition as Variable Expression?" required="Yes">If this is selected, then the condition must be an expression that evaluates to "<code>true</code>" (case is ignored).
     For example, <code>${FOUND}</code> or <code>${__jexl3(${VAR} &gt; 100)}</code>.
     Unlike the Javascript case, the condition is only checked to see if it matches "<code>true</code>" (case is ignored).
     </property>
     <property name="Evaluate for all children" required="Yes">
     Should condition be evaluated for all children?
     If not checked, then the condition is only evaluated on entry.
     </property>
 </properties>
     <example title="Examples (Javascript)" anchor="example_if_javascript">
         <ul>
             <li><code>${COUNT} &lt; 10</code></li>
             <li><code>"${VAR}" == "abcd"</code></li>
         </ul>
         If there is an error interpreting the code, the condition is assumed to be <code>false</code>, and a message is logged in <code>jmeter.log</code>.
         <note>Note it is advised to avoid using Javascript mode for performances</note>
     </example>
     <example title="Examples (Variable Expression)" anchor="example_if_variable">
         <ul>
             <li><code>${__jexl3(${COUNT} &lt; 10)}</code></li>
             <li><code>${RESULT}</code></li>
             <li><code>${JMeterThread.last_sample_ok}</code> (check if the last sample succeeded)</li>
         </ul>
     </example>
 </component>
 
 
 
 
 
 
 
 
 <component name="While Controller" index="&sect-num;.2.10" width="362" height="102" screenshot="whilecontroller.png">
     <description>
 <p>
 The While Controller runs its children until the condition is "<code>false</code>".
 </p>
 
 <p>Possible condition values:</p>
 <ul>
 <li>blank - exit loop when last sample in loop fails</li>
 <li><code>LAST</code> - exit loop when last sample in loop fails.
 If the last sample just before the loop failed, don't enter loop.</li>
 <li>Otherwise - exit (or don't enter) the loop when the condition is equal to the string "<code>false</code>"</li>
 </ul>
 <note>
 The condition can be any variable or function that eventually evaluates to the string "<code>false</code>".
 This allows the use of JavaScript, BeanShell, properties or variables as needed.
 </note>
 <br></br>
 <note>
 Note that the condition is evaluated twice, once before starting sampling children and once at end of children sampling, so putting
 non idempotent functions in Condition (like <code>__counter</code>) can introduce issues.
 </note>
 <br></br>
 For example:
 <ul>
     <li><code>${VAR}</code> - where <code>VAR</code> is set to false by some other test element</li>
     <li><code>${__javaScript(${C}==10)}</code></li>
     <li><code>${__javaScript("${VAR2}"=="abcd")}</code></li>
     <li><code>${_P(property)}</code> - where property is set to "<code>false</code>" somewhere else</li>
 </ul>
     </description>
 <properties>
     <property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
     <property name="Condition" required="Yes">blank, <code>LAST</code>, or variable/function</property>
 </properties>
 </component>
 
 <component name="Switch Controller" index="&sect-num;.2.11" width="361" height="106" screenshot="switchcontroller.png">
     <description>
 <p>
 The Switch Controller acts like the <complink name="Interleave Controller"/> 
 in that it runs one of the subordinate elements on each iteration, but rather than
 run them in sequence, the controller runs the element defined by the switch value.
 </p>
 <note>
 The switch value can also be a name.
 </note>
 <p>If the switch value is out of range, it will run the zeroth element, 
 which therefore acts as the default for the numeric case.
 It also runs the zeroth element if the value is the empty string.</p>
 <p>
 If the value is non-numeric (and non-empty), then the Switch Controller looks for the
 element with the same name (case is significant).
 If none of the names match, then the element named "<code>default</code>" (case not significant) is selected.
 If there is no default, then no element is selected, and the controller will not run anything.
 </p>
 </description>
 <properties>
     <property name="Name" required="Yes">Descriptive name for this controller that is shown in the tree, and used to name the transaction.</property>
     <property name="Switch Value" required="Yes">The number (or name) of the subordinate element to be invoked. Elements are numbered from 0.</property>
 </properties>
 </component>
 
 <component name="ForEach Controller" index="&sect-num;.2.12" anchor="loop" width="342" height="193" screenshot="logic-controller/foreach-controller.png">
 <description><p>A ForEach controller loops through the values of a set of related variables. 
 When you add samplers (or controllers) to a ForEach controller, every sample (or controller)
 is executed one or more times, where during every loop the variable has a new value.
 The input should consist of several variables, each extended with an underscore and a number.
 Each such variable must have a value.
 So for example when the input variable has the name <code>inputVar</code>, the following variables should have been defined:</p>
         <ul>
         <li><code>inputVar_1 = wendy</code></li>
         <li><code>inputVar_2 = charles</code></li>
         <li><code>inputVar_3 = peter</code></li>
         <li><code>inputVar_4 = john</code></li>
         </ul>
         <p>Note: the "<code>_</code>" separator is now optional.</p>
 <p>
 When the return variable is given as "<code>returnVar</code>", the collection of samplers and controllers under the ForEach controller will be executed <code>4</code> consecutive times,
 with the return variable having the respective above values, which can then be used in the samplers.
 </p>
 <p>
 It is especially suited for running with the regular expression post-processor. 
 This can "create" the necessary input variables out of the result data of a previous request.
 By omitting the "<code>_</code>" separator, the ForEach Controller can be used to loop through the groups by using
 the input variable <code>refName_g</code>, and can also loop through all the groups in all the matches
 by using an input variable of the form <code>refName_${C}_g</code>, where <code>C</code> is a counter variable.
 </p>
 <note>The ForEach Controller does not run any samples if <code>inputVar_1</code> is <code>null</code>.
 This would be the case if the Regular Expression returned no matches.</note>
 </description>
 
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Input variable prefix" required="Yes">Prefix for the variable names to be used as input.</property>
         <property name="Start index for loop" required="No">Start index (exclusive) for loop over variables (first element is at start index + 1)</property>
         <property name="End index for loop" required="No">End index (inclusive) for loop over variables</property>
         <property name="Output variable" required="Yes">
                 The name of the variable which can be used in the loop for replacement in the samplers</property>
         <property required="Yes" name="Use Separator">If not checked, the "<code>_</code>" separator is omitted.</property>
 </properties>
 
 <example title="ForEach Example" anchor="foreach_example">
 
 <p><a href="../demos/forEachTestPlan.jmx">Download</a> this example (see Figure 7).
 In this example, we created a Test Plan that sends a particular HTTP Request
 only once and sends another HTTP Request to every link that can be found on the page.</p>
 
 <figure width="300" height="158" image="logic-controller/foreach-example.png">Figure 7 - ForEach Controller Example</figure>
 
 <p>We configured the Thread Group for a single thread and a loop count value of
 one. You can see that we added one HTTP Request to the Thread Group and
 another HTTP Request to the ForEach Controller.</p>
 <p>After the first HTTP request, a regular expression extractor is added, which extracts all the html links
 out of the return page and puts them in the <code>inputVar</code> variable</p>
 <p>In the ForEach loop, a HTTP sampler is added which requests all the links that were extracted from the first returned HTML page.
 </p></example>
 <example title="ForEach Example" anchor="foreach_example2">
 <p>Here is <a href="../demos/ForEachTest2.jmx">another example</a> you can download. 
 This has two Regular Expressions and ForEach Controllers.
 The first RE matches, but the second does not match, 
 so no samples are run by the second ForEach Controller</p>
 <figure width="237" height="249" image="logic-controller/foreach-example2.png">Figure 8 - ForEach Controller Example 2</figure>
 <p>The Thread Group has a single thread and a loop count of two.
 </p><p>
 Sample 1 uses the JavaTest Sampler to return the string "<code>a b c d</code>".
 </p><p>The Regex Extractor uses the expression <code>(\w)\s</code> which matches a letter followed by a space,
 and returns the letter (not the space). Any matches are prefixed with the string "<code>inputVar</code>".
 </p><p>The ForEach Controller extracts all variables with the prefix "<code>inputVar_</code>", and executes its
 sample, passing the value in the variable "<code>returnVar</code>". In this case it will set the variable to the values "<code>a</code>" "<code>b</code>" and "<code>c</code>" in turn.
 </p><p>The <code>For 1</code> Sampler is another Java Sampler which uses the return variable "<code>returnVar</code>" as part of the sample Label
 and as the sampler Data.
 </p><p><code>Sample 2</code>, <code>Regex 2</code> and <code>For 2</code> are almost identical, except that the Regex has been changed to "<code>(\w)\sx</code>",
 which clearly won't match. Thus the <code>For 2</code> Sampler will not be run.
 </p>
 </example>
 </component>
 
 <component name="Module Controller" index="&sect-num;.2.13" width="526" height="318" screenshot="module_controller.png">
 <description>
 <p>
 The Module Controller provides a mechanism for substituting test plan fragments into the current test plan at run-time.
 </p>
 <p>
 A test plan fragment consists of a Controller and all the test elements (samplers etc.) contained in it. 
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
 </p>
 <pre>
 Test Plan / Protocol: JDBC / Control / Interleave Controller (Module1)
 </pre>
 <p>
 Any <b>fragments used by the Module Controller must have a unique name</b>,
 as the name is used to find the target controller when a test plan is reloaded.
 For this reason it is best to ensure that the Controller name is changed from the default
 - as shown in the example above -
 otherwise a duplicate may be accidentally created when new elements are added to the test plan. 
 </p>
 </description>
 <note>The Module Controller should not be used with remote testing or non-gui testing in conjunction with Workbench components since the Workbench test elements are not part of test plan <code>.jmx</code> files.  Any such test will fail.</note>
 <properties>
         <property name="Name" required="No">Descriptive name for this controller that is shown in the tree.</property>
         <property name="Module to Run" required="Yes">The module controller provides a list of all controllers loaded into the gui.  Select
         the one you want to substitute in at runtime.</property>
 </properties>
 </component>
 
 <component name="Include Controller" index="&sect-num;.2.14" width="417" height="130" screenshot="includecontroller.png">
     <description>
 <p>
 The include controller is designed to use an external jmx file. To use it, create a Test Fragment 
 underneath the Test Plan and add any desired samplers, controllers etc. below it.
 Then save the Test Plan.  The file is now ready to be included as part of other Test Plans.
 </p>
 <p>
 For convenience, a <complink name="Thread Group" /> can also be added in the external JMX file for debugging purposes.
 A <complink name="Module Controller" /> can be used to reference the Test Fragment.  The <complink name="Thread Group" /> will be ignored during the 
 include process.
 </p>
 <p>
 If the test uses a Cookie Manager or User Defined Variables, these should be placed in the top-level
 test plan, not the included file, otherwise they are not guaranteed to work.
 </p>
 <note>
 This element does not support variables/functions in the filename field.<br></br>
 However, if the property <code>includecontroller.prefix</code> is defined, 
 the contents are used to prefix the pathname.
 </note>
 <note>
 When using Include Controller and including the same JMX file, ensure you name the Include Controller differently to avoid facing known issue <bugzilla>50898</bugzilla>.
 </note>
 <p>
 If the file cannot be found at the location given by <code>prefix</code>+<code>Filename</code>, then the controller
 attempts to open the <code>Filename</code> relative to the JMX launch directory.
 </p>
 </description>
 <properties>
     <property name="Filename" required="Yes">The file to include.</property>
 </properties>
 </component>
 
 <component name="Transaction Controller" index="&sect-num;.2.15" width="622" height="140" screenshot="transactioncontroller.png">
     <description>
         <p>
         The Transaction Controller generates an additional
         sample which measures the overall time taken to perform the nested test elements.
         </p>
         <note>
         Note: when the check box "<code>Include duration of timer and pre-post processors in generated sample</code>" is checked,
         the time includes all processing within the controller scope, not just the samples.
         </note>
         <p>
         There are two modes of operation:
         </p>
         <ul>
         <li>additional sample is added after the nested samples</li>
         <li>additional sample is added as a parent of the nested samples</li>
         </ul>
         <p>
         The generated sample time includes all the times for the nested samplers excluding by default (since 2.11) timers and processing time of pre/post processors 
         unless checkbox "<code>Include duration of timer and pre-post processors in generated sample</code>" is checked.
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
         In parent mode, Assertions (etc.) can be added to the Transaction Controller.
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
     <property name="Include duration of timer and pre-post processors in generated sample" required="Yes">
