diff --git a/bin/jmeter.properties b/bin/jmeter.properties
index 782c1633f..b0a26ae00 100644
--- a/bin/jmeter.properties
+++ b/bin/jmeter.properties
@@ -1,1297 +1,1306 @@
 ################################################################################
 # Apache JMeter Property file
 ################################################################################
 
 ##   Licensed to the Apache Software Foundation (ASF) under one or more
 ##   contributor license agreements.  See the NOTICE file distributed with
 ##   this work for additional information regarding copyright ownership.
 ##   The ASF licenses this file to You under the Apache License, Version 2.0
 ##   (the "License"); you may not use this file except in compliance with
 ##   the License.  You may obtain a copy of the License at
 ## 
 ##       http://www.apache.org/licenses/LICENSE-2.0
 ## 
 ##   Unless required by applicable law or agreed to in writing, software
 ##   distributed under the License is distributed on an "AS IS" BASIS,
 ##   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ##   See the License for the specific language governing permissions and
 ##   limitations under the License.
 
 ################################################################################
 #
 #                      THIS FILE SHOULD NOT BE MODIFIED
 #
 # This avoids having to re-apply the modifications when upgrading JMeter
 # Instead only user.properties should be modified:
 # 1/ copy the property you want to modify to user.properties from jmeter.properties
 # 2/ Change its value there
 #
 ################################################################################
 
 #Preferred GUI language. Comment out to use the JVM default locale's language.
 #language=en
 
 
 # Additional locale(s) to add to the displayed list.
 # The current default list is: en, fr, de, no, es, tr, ja, zh_CN, zh_TW, pl, pt_BR
 # [see JMeterMenuBar#makeLanguageMenu()]
 # The entries are a comma-separated list of language names
 #locales.add=zu
 
 # Netscape HTTP Cookie file
 cookies=cookies
 
 #---------------------------------------------------------------------------
 # XML Parser
 #---------------------------------------------------------------------------
 
 # XML Reader(Parser) - Must implement SAX 2 specs
 xml.parser=org.apache.xerces.parsers.SAXParser
 
 # Path to a Properties file containing Namespace mapping in the form
 # prefix=Namespace
 # Example:
 # ns=http://biz.aol.com/schema/2006-12-18
 #xpath.namespace.config=
 
 #---------------------------------------------------------------------------
 # SSL configuration
 #---------------------------------------------------------------------------
 
 ## SSL System properties are now in system.properties
 
 # JMeter no longer converts javax.xxx property entries in this file into System properties.
 # These must now be defined in the system.properties file or on the command-line.
 # The system.properties file gives more flexibility.
 
 # By default, SSL session contexts are now created per-thread, rather than being shared.
 # The original behaviour can be enabled by setting the JMeter property:
 #https.sessioncontext.shared=true
 
 # Be aware that https default protocol may vary depending on the version of JVM
 # See https://blogs.oracle.com/java-platform-group/entry/diagnosing_tls_ssl_and_https
 # See https://bz.apache.org/bugzilla/show_bug.cgi?id=58236
 # Default HTTPS protocol level:
 #https.default.protocol=TLS
 # This may need to be changed here (or in user.properties) to:
 #https.default.protocol=SSLv3
 
 # List of protocols to enable. You may have to select only a subset if you find issues with target server.
 # This is needed when server does not support Socket version negotiation, this can lead to:
 # javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
 # java.net.SocketException: Connection reset
 # see https://bz.apache.org/bugzilla/show_bug.cgi?id=54759
 #https.socket.protocols=SSLv2Hello SSLv3 TLSv1
 
 # Control if we allow reuse of cached SSL context between iterations
 # set the value to 'false' to reset the SSL context each iteration
 #https.use.cached.ssl.context=true
 
 # Start and end index to be used with keystores with many entries
 # The default is to use entry 0, i.e. the first
 #https.keyStoreStartIndex=0
 #https.keyStoreEndIndex=0
 
 #---------------------------------------------------------------------------
 # Look and Feel configuration
 #---------------------------------------------------------------------------
 
 #Classname of the Swing default UI
 #
 # The LAF classnames that are available are now displayed as ToolTip text
 # when hovering over the Options/Look and Feel selection list.
 #
 # You can either use a full class name, as shown above,
 # or one of the strings "System" or "CrossPlatform" which means
 #  JMeter will use the corresponding string returned by UIManager.get<name>LookAndFeelClassName()
 
 # LAF can be overridden by os.name (lowercased, spaces replaced by '_')
 # Sample os.name LAF:
 #jmeter.laf.windows_xp=javax.swing.plaf.metal.MetalLookAndFeel
 
 # Failing that, the OS family = os.name, but only up to first space:
 # Sample OS family LAF:
 #jmeter.laf.windows=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
 
 # Mac apparently looks better with the System LAF
 jmeter.laf.mac=System
 
 # Failing that, the JMeter default laf can be defined:
 #jmeter.laf=System
 
 # If none of the above jmeter.laf properties are defined, JMeter uses the CrossPlatform LAF.
 # This is because the CrossPlatform LAF generally looks better than the System LAF.
 # See https://bz.apache.org/bugzilla/show_bug.cgi?id=52026 for details
 # N.B. the laf can be defined in user.properties.
 
 # LoggerPanel display
 # default to false
 #jmeter.loggerpanel.display=false
 
 # Enable LogViewer Panel to receive log event even if closed
 # Enabled since 2.12
 # Note this has some impact on performances, but as GUI mode must
 # not be used for Load Test it is acceptable
 #jmeter.loggerpanel.enable_when_closed=true
 
 # Error/Fatal Log count display
 # defaults to true
 #jmeter.errorscounter.display=true
 
 # Max characters kept in LoggerPanel, default to 80000 chars
 # O means no limit
 #jmeter.loggerpanel.maxlength=80000
 
 # Toolbar display
 # default:
 #jmeter.toolbar.display=true
 # Toolbar icon definitions
 #jmeter.toolbar.icons=org/apache/jmeter/images/toolbar/icons-toolbar.properties
 # Toolbar list
 #jmeter.toolbar=new,open,close,save,save_as_testplan,|,cut,copy,paste,|,expand,collapse,toggle,|,test_start,test_stop,test_shutdown,|,test_start_remote_all,test_stop_remote_all,test_shutdown_remote_all,|,test_clear,test_clear_all,|,search,search_reset,|,function_helper,help
 # Toolbar icons default size: 22x22. Available sizes are: 22x22, 32x32, 48x48
 #jmeter.toolbar.icons.size=22x22
 # Suggest value for HiDPI mode:
 #jmeter.toolbar.icons.size=48x48
 
 # Icon definitions
 # default:
 #jmeter.icons=org/apache/jmeter/images/icon.properties
 # alternate:
 #jmeter.icons=org/apache/jmeter/images/icon_1.properties
 # Historical icon set (deprecated)
 #jmeter.icons=org/apache/jmeter/images/icon_old.properties
 
 # Tree icons default size: 19x19. Available sizes are: 19x19, 24x24, 32x32, 48x48
 # Useful for HiDPI display (see below)
 #jmeter.tree.icons.size=19x19
 # Suggest value for HiDPI screen like 3200x1800:
 #jmeter.tree.icons.size=32x32
 
 # HiDPI mode (default: false)
 # Enable to activate a 'pseudo'-hidpi mode. Allow to increase some UI elements
 # which not correctly manage by JVM with high resolution screen in Linux or Windows
 #jmeter.hidpi.mode=false
 # HiDPI scale factor (default: 1.0, suggest value in HiDPI: 2.0)
 #jmeter.hidpi.scale.factor=1.0
 
 #Components to not display in JMeter GUI (GUI class name or static label)
 # These elements are deprecated and will be removed in next version: MongoDB Script, MongoDB Source Config, Distribution Graph, Spline Visualizer
 not_in_menu=org.apache.jmeter.protocol.mongodb.sampler.MongoScriptSampler, org.apache.jmeter.protocol.mongodb.config.MongoSourceElement, org.apache.jmeter.visualizers.DistributionGraphVisualizer, org.apache.jmeter.visualizers.SplineVisualizer
 
 # Number of items in undo history
 # Feature is disabled by default (0)
 # Set it to a number > 0 (25 can be a good default)
 # The bigger it is, the more it consumes memory
 #undo.history.size=0
 
 # Hotkeys to add JMeter components, will add elements when you press Ctrl+0 .. Ctrl+9 (Command+0 .. Command+9 on Mac)
 gui.quick_0=ThreadGroupGui
 gui.quick_1=HttpTestSampleGui
 gui.quick_2=RegexExtractorGui
 gui.quick_3=AssertionGui
 gui.quick_4=ConstantTimerGui
 gui.quick_5=TestActionGui
 gui.quick_6=JSR223PostProcessor
 gui.quick_7=JSR223PreProcessor
 gui.quick_8=DebugSampler
 gui.quick_9=ViewResultsFullVisualizer
 
 
 #---------------------------------------------------------------------------
 # JMX Backup configuration
 #---------------------------------------------------------------------------
 #Enable auto backups of the .jmx file when a test plan is saved.
 #When enabled, before the .jmx is saved, it will be backed up to the directory pointed
 #by the jmeter.gui.action.save.backup_directory property (see below). Backup file names are built
 #after the jmx file being saved. For example, saving test-plan.jmx will create a test-plan-000012.jmx
 #in the backup directory provided that the last created backup file is test-plan-000011.jmx.
 #Default value is true indicating that auto backups are enabled
 #jmeter.gui.action.save.backup_on_save=true
 
 #Set the backup directory path where JMX backups will be created upon save in the GUI.
 #If not set (what it defaults to) then backup files will be created in
 #a sub-directory of the JMeter base installation. The default directory is ${JMETER_HOME}/backups
 #If set and the directory does not exist, it will be created.
 #jmeter.gui.action.save.backup_directory=
 
 #Set the maximum time (in hours) that backup files should be preserved since the save time.
 #By default no expiration time is set which means we keep backups for ever.
 #jmeter.gui.action.save.keep_backup_max_hours=0
 
 #Set the maximum number of backup files that should be preserved. By default 10 backups will be preserved.
 #Setting this to zero will cause the backups to not being deleted (unless keep_backup_max_hours is set to a non zero value)
 #jmeter.gui.action.save.keep_backup_max_count=10
 
 
 #---------------------------------------------------------------------------
 # Remote hosts and RMI configuration
 #---------------------------------------------------------------------------
 
 # Remote Hosts - comma delimited
 remote_hosts=127.0.0.1
 #remote_hosts=localhost:1099,localhost:2010
 
 # RMI port to be used by the server (must start rmiregistry with same port)
 #server_port=1099
 
 # To change the port to (say) 1234:
 # On the server(s)
 # - set server_port=1234
 # - start rmiregistry with port 1234
 # On Windows this can be done by:
 # SET SERVER_PORT=1234
 # JMETER-SERVER
 #
 # On Unix:
 # SERVER_PORT=1234 jmeter-server
 #
 # On the client:
 # - set remote_hosts=server:1234
 
 # Parameter that controls the RMI port used by the RemoteSampleListenerImpl (The Controler)
 # Default value is 0 which means port is randomly assigned
 # You may need to open Firewall port on the Controller machine
 #client.rmi.localport=0
 
 # When distributed test is starting, there may be several attempts to initialize
 # remote engines. By default, only single try is made. Increase following property
 # to make it retry for additional times
 #client.tries=1
 
 # If there is initialization retries, following property sets delay between attempts
 #client.retries_delay=5000
 
 # When all initialization tries was made, test will fail if some remote engines are failed
 # Set following property to true to ignore failed nodes and proceed with test 
 #client.continue_on_fail=false
 
 # To change the default port (1099) used to access the server:
 #server.rmi.port=1234
 
 # To use a specific port for the JMeter server engine, define
 # the following property before starting the server:
 #server.rmi.localport=4000
 
 # From JMeter 2.3.1, the jmeter server creates the RMI registry as part of the server process.
 # To stop the server creating the RMI registry:
 #server.rmi.create=false
 
 # From JMeter 2.3.1, define the following property to cause JMeter to exit after the first test
 #server.exitaftertest=true
 
 # Prefix used by IncludeController when building file name
 #includecontroller.prefix=
 
 #---------------------------------------------------------------------------
 #         Logging Configuration
 #---------------------------------------------------------------------------
 
 # Note: JMeter uses Avalon (Excalibur) LogKit
 
 # Logging Format
 # see http://excalibur.apache.org/apidocs/org/apache/log/format/PatternFormatter.html
 
 #
 # Default format:
 #log_format=%{time:yyyy/MM/dd HH:mm:ss} %5.5{priority} - %{category}: %{message} %{throwable}
 # \n is automatically added to the end of the string
 #
 # Predefined formats in the JMeter LoggingManager:
 #log_format_type=default
 #log_format_type=thread_prefix
 #log_format_type=thread_suffix
 # default is as above
 # thread_prefix adds the thread name as a prefix to the category
 # thread_suffix adds the thread name as a suffix to the category
 # Note that thread name is not included by default, as it requires extra processing.
 #
 # To change the logging format, define either log_format_type or log_format
 # If both are defined, the type takes precedence
 # Note that these properties cannot be defined using the -J or -D JMeter
 # command-line flags, as the format will have already been determined by then
 # However, they can be defined as JVM properties
 
 #Logging levels for the logging categories in JMeter.  Correct values are FATAL_ERROR, ERROR, WARN, INFO, and DEBUG
 # To set the log level for a package or individual class, use:
 # log_level.[package_name].[classname]=[PRIORITY_LEVEL]
 # But omit "org.apache" from the package name.  The classname is optional.  Further examples below.
 
 log_level.jmeter=INFO
 log_level.jmeter.junit=DEBUG
 #log_level.jmeter.control=DEBUG
 #log_level.jmeter.testbeans=DEBUG
 #log_level.jmeter.engine=DEBUG
 #log_level.jmeter.threads=DEBUG
 #log_level.jmeter.gui=WARN
 #log_level.jmeter.testelement=DEBUG
 #log_level.jmeter.util=WARN
 #log_level.jmeter.protocol.http=DEBUG
 # For CookieManager, AuthManager etc:
 #log_level.jmeter.protocol.http.control=DEBUG
 #log_level.jmeter.protocol.ftp=WARN
 #log_level.jmeter.protocol.jdbc=DEBUG
 #log_level.jmeter.protocol.java=WARN
 #log_level.jmeter.testelements.property=DEBUG
 log_level.jorphan=INFO
 	
 
 #Log file for log messages.
 # You can specify a different log file for different categories via:
 # log_file.[category]=[filename]
 # category is equivalent to the package/class names described above
 
 # Combined log file (for jmeter and jorphan)
 #log_file=jmeter.log
 # To redirect logging to standard output, try the following:
 # (it will probably report an error, but output will be to stdout)
 #log_file=
 
 # Or define separate logs if required:
 #log_file.jorphan=jorphan.log
 #log_file.jmeter=jmeter.log
 
 # If the filename contains  paired single-quotes, then the name is processed
 # as a SimpleDateFormat format applied to the current date, for example:
 #log_file='jmeter_'yyyyMMddHHmmss'.tmp'
 
 # N.B. When JMeter starts, it sets the system property:
 #    org.apache.commons.logging.Log
 # to
 #    org.apache.commons.logging.impl.LogKitLogger
 # if not already set. This causes Apache and Commons HttpClient to use the same logging as JMeter
 
 # Further logging configuration
 # Excalibur logging provides the facility to configure logging using
 # configuration files written in XML. This allows for such features as
 # log file rotation which are not supported directly by JMeter.
 #
 # If such a file specified, it will be applied to the current logging
 # hierarchy when that has been created.
 # 
 #log_config=logkit.xml
 
 #---------------------------------------------------------------------------
 # HTTP Java configuration
 #---------------------------------------------------------------------------
 
 # Number of connection retries performed by HTTP Java sampler before giving up
 # 0 means no retry since version 3.0
 #http.java.sampler.retries=0
 
 #---------------------------------------------------------------------------
 # Commons HTTPClient configuration
 #---------------------------------------------------------------------------
 
 # define a properties file for overriding Commons HttpClient parameters
 # See: http://hc.apache.org/httpclient-3.x/preference-api.html
 # Uncomment this line if you put anything in httpclient.parameters file
 #httpclient.parameters.file=httpclient.parameters
 
 
 # define a properties file for overriding Apache HttpClient parameters
 # See: TBA
 # Uncomment this line if you put anything in hc.parameters file
 #hc.parameters.file=hc.parameters
 
 # Following properties apply to both Commons and Apache HttpClient
 
 # set the socket timeout (or use the parameter http.socket.timeout) 
 # for AJP Sampler and HttpClient3 implementation.
 # Note for HttpClient3 implementation it is better to use GUI to set timeout 
 # or use http.socket.timeout in httpclient.parameters
 # Value is in milliseconds
 #httpclient.timeout=0
 # 0 == no timeout
 
 # Set the http version (defaults to 1.1)
 #httpclient.version=1.1 (or use the parameter http.protocol.version)
 
 # Define characters per second > 0 to emulate slow connections
 #httpclient.socket.http.cps=0
 #httpclient.socket.https.cps=0
 
 #Enable loopback protocol
 #httpclient.loopback=true
 
 # Define the local host address to be used for multi-homed hosts
 #httpclient.localaddress=1.2.3.4
 
 # AuthManager Kerberos configuration
 # Name of application module used in jaas.conf
 #kerberos_jaas_application=JMeter  
 
 # Should ports be stripped from urls before constructing SPNs
 # for spnego authentication
 #kerberos.spnego.strip_port=true
 
 #         Sample logging levels for Commons HttpClient
 #
 # Commons HttpClient Logging information can be found at:
 # http://hc.apache.org/httpclient-3.x/logging.html
 
 # Note that full category names are used, i.e. must include the org.apache.
 # Info level produces no output:
 #log_level.org.apache.commons.httpclient=debug
 # Might be useful:
 #log_level.org.apache.commons.httpclient.Authenticator=trace 
 
 # Show headers only
 #log_level.httpclient.wire.header=debug
 
 # Full wire debug produces a lot of output; consider using separate file:
 #log_level.httpclient.wire=debug
 #log_file.httpclient=httpclient.log
 
 
 #         Apache Commons HttpClient logging examples
 #
 # Enable header wire + context logging - Best for Debugging
 #log_level.org.apache.http=DEBUG
 #log_level.org.apache.http.wire=ERROR
 
 # Enable full wire + context logging
 #log_level.org.apache.http=DEBUG
 
 # Enable context logging for connection management
 #log_level.org.apache.http.impl.conn=DEBUG
 
 # Enable context logging for connection management / request execution
 #log_level.org.apache.http.impl.conn=DEBUG
 #log_level.org.apache.http.impl.client=DEBUG
 #log_level.org.apache.http.client=DEBUG
 
 #---------------------------------------------------------------------------
 # Apache HttpComponents HTTPClient configuration (HTTPClient4)
 #---------------------------------------------------------------------------
 
 # Number of retries to attempt (default 0)
 #httpclient4.retrycount=0
 
-# Idle connection timeout (ms) to apply if the server does not send
+# Idle connection timeout (Milliseconds) to apply if the server does not send
 # Keep-Alive headers (default 0 = no Keep-Alive)
 #httpclient4.idletimeout=0
 # Note: this is currently an experimental fix
 
+# Check connections if the elapsed time (Milliseconds) since the last 
+# use of the connection exceed this value
+#httpclient4.validate_after_inactivity=2000
+
+# TTL (in Milliseconds) represents an absolute value. 
+# No matter what the connection will not be re-used beyond its TTL. 
+#httpclient4.time_to_live=2000
 #---------------------------------------------------------------------------
 # Apache HttpComponents HTTPClient configuration (HTTPClient 3.1)
 #---------------------------------------------------------------------------
 
 # Number of retries to attempt (default 0)
 #httpclient3.retrycount=0
 
 #---------------------------------------------------------------------------
 # HTTP Cache Manager configuration
 #---------------------------------------------------------------------------
 #
 # Space or comma separated list of methods that can be cached
 #cacheable_methods=GET
 # N.B. This property is currently a temporary solution for Bug 56162
 
 # Since 2.12, JMeter does not create anymore a Sample Result with 204 response 
 # code for a resource found in cache which is inline with what browser do.
 #cache_manager.cached_resource_mode=RETURN_NO_SAMPLE
 
 # You can choose between 3 modes:
 # RETURN_NO_SAMPLE (default)
 # RETURN_200_CACHE
 # RETURN_CUSTOM_STATUS
 
 # Those mode have the following behaviours:
 # RETURN_NO_SAMPLE : this mode returns no Sample Result, it has no additional configuration
 # RETURN_200_CACHE : this mode will return Sample Result with response code to 200 and response message to "(ex cache)", you can modify response message by setting 
 # RETURN_200_CACHE.message=(ex cache)
 # RETURN_CUSTOM_STATUS : This mode lets you select what response code and message you want to return, if you use this mode you need to set those properties
 # RETURN_CUSTOM_STATUS.code=
 # RETURN_CUSTOM_STATUS.message=
 
 #---------------------------------------------------------------------------
 # Results file configuration
 #---------------------------------------------------------------------------
 
 # This section helps determine how result data will be saved.
 # The commented out values are the defaults.
 
 # legitimate values: xml, csv, db.  Only xml and csv are currently supported.
 #jmeter.save.saveservice.output_format=csv
 
 
 # true when field should be saved; false otherwise
 
 # assertion_results_failure_message only affects CSV output
 #jmeter.save.saveservice.assertion_results_failure_message=true
 #
 # legitimate values: none, first, all
 #jmeter.save.saveservice.assertion_results=none
 #
 #jmeter.save.saveservice.data_type=true
 #jmeter.save.saveservice.label=true
 #jmeter.save.saveservice.response_code=true
 # response_data is not currently supported for CSV output
 #jmeter.save.saveservice.response_data=false
 # Save ResponseData for failed samples
 #jmeter.save.saveservice.response_data.on_error=false
 #jmeter.save.saveservice.response_message=true
 #jmeter.save.saveservice.successful=true
 #jmeter.save.saveservice.thread_name=true
 #jmeter.save.saveservice.time=true
 #jmeter.save.saveservice.subresults=true
 #jmeter.save.saveservice.assertions=true
 #jmeter.save.saveservice.latency=true
 #jmeter.save.saveservice.connect_time=false
 #jmeter.save.saveservice.samplerData=false
 #jmeter.save.saveservice.responseHeaders=false
 #jmeter.save.saveservice.requestHeaders=false
 #jmeter.save.saveservice.encoding=false
 #jmeter.save.saveservice.bytes=true
 #jmeter.save.saveservice.url=false
 #jmeter.save.saveservice.filename=false
 #jmeter.save.saveservice.hostname=false
 #jmeter.save.saveservice.thread_counts=true
 #jmeter.save.saveservice.sample_count=false
 #jmeter.save.saveservice.idle_time=true
 
 # Timestamp format - this only affects CSV output files
 # legitimate values: none, ms, or a format suitable for SimpleDateFormat
 #jmeter.save.saveservice.timestamp_format=ms
 #jmeter.save.saveservice.timestamp_format=yyyy/MM/dd HH:mm:ss.SSS
 
 # For use with Comma-separated value (CSV) files or other formats
 # where the fields' values are separated by specified delimiters.
 # Default:
 #jmeter.save.saveservice.default_delimiter=,
 # For TAB, since JMeter 2.3 one can use:
 #jmeter.save.saveservice.default_delimiter=\t
 
 # Only applies to CSV format files:
 # Print field names as first line in CSV
 #jmeter.save.saveservice.print_field_names=true
 
 # Optional list of JMeter variable names whose values are to be saved in the result data files.
 # Use commas to separate the names. For example:
 #sample_variables=SESSION_ID,REFERENCE
 # N.B. The current implementation saves the values in XML as attributes,
 # so the names must be valid XML names.
 # Versions of JMeter after 2.3.2 send the variable to all servers
 # to ensure that the correct data is available at the client.
 
 # Optional xml processing instruction for line 2 of the file:
 # Example:
 #jmeter.save.saveservice.xml_pi=<?xml-stylesheet type="text/xsl" href="../extras/jmeter-results-detail-report.xsl"?>
 # Default value:
 #jmeter.save.saveservice.xml_pi=
 
 # Prefix used to identify filenames that are relative to the current base
 #jmeter.save.saveservice.base_prefix=~/
 
 # AutoFlush on each line written in XML or CSV output
 # Setting this to true will result in less test results data loss in case of Crash
 # but with impact on performances, particularly for intensive tests (low or no pauses)
 # Since JMeter 2.10, this is false by default
 #jmeter.save.saveservice.autoflush=false
 
 #---------------------------------------------------------------------------
 # Settings that affect SampleResults
 #---------------------------------------------------------------------------
 
 # Save the start time stamp instead of the end
 # This also affects the timestamp stored in result files
 sampleresult.timestamp.start=true
 
 # Whether to use System.nanoTime() - otherwise only use System.currentTimeMillis()
 #sampleresult.useNanoTime=true
 
 # Use a background thread to calculate the nanoTime offset
 # Set this to <= 0 to disable the background thread
 #sampleresult.nanoThreadSleep=5000
 
 #---------------------------------------------------------------------------
 # Upgrade property
 #---------------------------------------------------------------------------
 
 # File that holds a record of name changes for backward compatibility issues
 upgrade_properties=/bin/upgrade.properties
 
 #---------------------------------------------------------------------------
 # JMeter Test Script recorder configuration
 #
 # N.B. The element was originally called the Proxy recorder, which is why the
 # properties have the prefix "proxy".
 #---------------------------------------------------------------------------
 
 # If the recorder detects a gap of at least 5s (default) between HTTP requests,
 # it assumes that the user has clicked a new URL
 #proxy.pause=5000
 
 # Add numeric prefix to Sampler names (default true)
 #proxy.number.requests=true
 
 # List of URL patterns that will be added to URL Patterns to exclude
 # Separate multiple lines with ;
 #proxy.excludes.suggested=.*\\.(bmp|css|js|gif|ico|jpe?g|png|swf|woff)
 
 # Change the default HTTP Sampler (currently HttpClient4)
 # Java:
 #jmeter.httpsampler=HTTPSampler
 #or
 #jmeter.httpsampler=Java
 #
 # Apache HTTPClient:
 #jmeter.httpsampler=HTTPSampler2
 #or
 #jmeter.httpsampler=HttpClient3.1
 #
 # HttpClient4.x
 #jmeter.httpsampler=HttpClient4
 
 # By default JMeter tries to be more lenient with RFC2616 redirects and allows
 # relative paths.
 # If you want to test strict conformance, set this value to true
 # When the property is true, JMeter follows http://tools.ietf.org/html/rfc3986#section-5.2
 #jmeter.httpclient.strict_rfc2616=false
 
 # Default content-type include filter to use
 #proxy.content_type_include=text/html|text/plain|text/xml
 # Default content-type exclude filter to use
 #proxy.content_type_exclude=image/.*|text/css|application/.*
 
 # Default headers to remove from Header Manager elements
 # (Cookie and Authorization are always removed)
 #proxy.headers.remove=If-Modified-Since,If-None-Match,Host
 
 # Binary content-type handling
 # These content-types will be handled by saving the request in a file:
 #proxy.binary.types=application/x-amf,application/x-java-serialized-object
 # The files will be saved in this directory:
 #proxy.binary.directory=user.dir
 # The files will be created with this file filesuffix:
 #proxy.binary.filesuffix=.binary
 
 #---------------------------------------------------------------------------
 # Test Script Recorder certificate configuration
 #---------------------------------------------------------------------------
 
 #proxy.cert.directory=<JMeter bin directory>
 #proxy.cert.file=proxyserver.jks
 #proxy.cert.type=JKS
 #proxy.cert.keystorepass=password
 #proxy.cert.keypassword=password
 #proxy.cert.factory=SunX509
 # define this property if you wish to use your own keystore
 #proxy.cert.alias=<none>
 # The default validity for certificates created by JMeter
 #proxy.cert.validity=7
 # Use dynamic key generation (if supported by JMeter/JVM)
 # If false, will revert to using a single key with no certificate
 #proxy.cert.dynamic_keys=true
 
 #---------------------------------------------------------------------------
 # Test Script Recorder miscellaneous configuration
 #---------------------------------------------------------------------------
 
 # Whether to attempt disabling of samples that resulted from redirects
 # where the generated samples use auto-redirection
 #proxy.redirect.disabling=true
 
 # SSL configuration
 #proxy.ssl.protocol=TLS
 
 #---------------------------------------------------------------------------
 # JMeter Proxy configuration
 #---------------------------------------------------------------------------
 # use command-line flags for user-name and password
 #http.proxyDomain=NTLM domain, if required by HTTPClient sampler
 
 #---------------------------------------------------------------------------
 # HTTPSampleResponse Parser configuration
 #---------------------------------------------------------------------------
 
 # Space-separated list of parser groups
 HTTPResponse.parsers=htmlParser wmlParser
 # for each parser, there should be a parser.types and a parser.className property
 
 #---------------------------------------------------------------------------
 # HTML Parser configuration
 #---------------------------------------------------------------------------
 
 # Define the HTML parser to be used.
 # Default parser:
 # This new parser (since 2.10) should perform better than all others
 # see https://bz.apache.org/bugzilla/show_bug.cgi?id=55632
 # Do not comment this property
 htmlParser.className=org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser
 
 # Other parsers:
 # Default parser before 2.10
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.JTidyHTMLParser
 # Note that Regexp extractor may detect references that have been commented out.
 # In many cases it will work OK, but you should be aware that it may generate 
 # additional references.
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 # This parser is based on JSoup, it should be the most accurate but less performant
 # than LagartoBasedHtmlParser
 #htmlParser.className=org.apache.jmeter.protocol.http.parser.JsoupBasedHtmlParser
 
 #Used by HTTPSamplerBase to associate htmlParser with content types below 
 htmlParser.types=text/html application/xhtml+xml application/xml text/xml
 
 #---------------------------------------------------------------------------
 # WML Parser configuration
 #---------------------------------------------------------------------------
 
 wmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser
 
 #Used by HTTPSamplerBase to associate wmlParser with content types below 
 wmlParser.types=text/vnd.wap.wml 
 
 #---------------------------------------------------------------------------
 # Remote batching configuration
 #---------------------------------------------------------------------------
 # How is Sample sender implementations configured:
 # - true (default) means client configuration will be used
 # - false means server configuration will be used
 #sample_sender_client_configured=true
 
 # Remote batching support
 # Since JMeter 2.9, default is MODE_STRIPPED_BATCH, which returns samples in
 # batch mode (every 100 samples or every minute by default)
 # Note also that MODE_STRIPPED_BATCH strips response data from SampleResult, so if you need it change to
 # another mode
 # Hold retains samples until end of test (may need lots of memory)
 # Batch returns samples in batches
 # Statistical returns sample summary statistics
 # hold_samples was originally defined as a separate property,
 # but can now also be defined using mode=Hold
 # mode can also be the class name of an implementation of org.apache.jmeter.samplers.SampleSender
 #mode=Standard
 #mode=Batch
 #mode=Hold
 #mode=Statistical
 #Set to true to key statistical samples on threadName rather than threadGroup
 #key_on_threadname=false
 #mode=Stripped
 #mode=StrippedBatch
 #mode=org.example.load.MySampleSender
 #
 #num_sample_threshold=100
 # Value is in milliseconds
 #time_threshold=60000
 #
 # Asynchronous sender; uses a queue and background worker process to return the samples
 #mode=Asynch
 # default queue size
 #asynch.batch.queue.size=100
 # Same as Asynch but strips response data from SampleResult
 #mode=StrippedAsynch
 #
 # DiskStore: as for Hold mode, but serialises the samples to disk, rather than saving in memory
 #mode=DiskStore
 # Same as DiskStore but strips response data from SampleResult
 #mode=StrippedDiskStore
 # Note: the mode is currently resolved on the client; 
 # other properties (e.g. time_threshold) are resolved on the server.
 
 # To set the Monitor Health Visualiser buffer size, enter the desired value
 # monitor.buffer.size=800
 
 #---------------------------------------------------------------------------
 # JDBC Request configuration
 #---------------------------------------------------------------------------
 
 # Max number of PreparedStatements per Connection for PreparedStatement cache
 #jdbcsampler.maxopenpreparedstatements=100
 
 # String used to indicate a null value
 #jdbcsampler.nullmarker=]NULL[
 
 #---------------------------------------------------------------------------
 # OS Process Sampler configuration
 #---------------------------------------------------------------------------
 # Polling to see if process has finished its work, used when a timeout is configured on sampler
 #os_sampler.poll_for_timeout=100
 
 #---------------------------------------------------------------------------
 # TCP Sampler configuration
 #---------------------------------------------------------------------------
 
 # The default handler class
 #tcp.handler=TCPClientImpl
 #
 # eolByte = byte value for end of line
 # set this to a value outside the range -128 to +127 to skip eol checking
 #tcp.eolByte=1000
 #
 # TCP Charset, used by org.apache.jmeter.protocol.tcp.sampler.TCPClientImpl
 # default to Platform defaults charset as returned by Charset.defaultCharset().name()
 #tcp.charset=
 #
 # status.prefix and suffix = strings that enclose the status response code
 #tcp.status.prefix=Status=
 #tcp.status.suffix=.
 #
 # status.properties = property file to convert codes to messages
 #tcp.status.properties=mytestfiles/tcpstatus.properties
 
 # The length prefix used by LengthPrefixedBinaryTCPClientImpl implementation
 # defaults to 2 bytes.
 #tcp.binarylength.prefix.length=2
 
 #---------------------------------------------------------------------------
 # Summariser - Generate Summary Results - configuration (mainly applies to non-GUI mode)
 #---------------------------------------------------------------------------
 #
 # Comment the following property to disable the default non-GUI summariser
 # [or change the value to rename it]
 # (applies to non-GUI mode only)
 summariser.name=summary
 #
 # interval between summaries (in seconds) default 30 seconds
 #summariser.interval=30
 #
 # Write messages to log file
 #summariser.log=true
 #
 # Write messages to System.out
 #summariser.out=true
 
 
 #---------------------------------------------------------------------------
 # Aggregate Report and Aggregate Graph - configuration
 #---------------------------------------------------------------------------
 #
 # Percentiles to display in reports
 # Can be float value between 0 and 100
 # First percentile to display, defaults to 90%
 #aggregate_rpt_pct1=90
 # Second percentile to display, defaults to 95%
 #aggregate_rpt_pct2=95
 # Second percentile to display, defaults to 99%
 #aggregate_rpt_pct3=99
 
 #---------------------------------------------------------------------------
 # Aggregate Report and Aggregate Graph - configuration
 #---------------------------------------------------------------------------
 #
 # Backend metrics sliding window size for Percentiles, Min, Max
 #backend_metrics_window=100
 
 #---------------------------------------------------------------------------
 # BeanShell configuration
 #---------------------------------------------------------------------------
 
 # BeanShell Server properties
 #
 # Define the port number as non-zero to start the http server on that port
 #beanshell.server.port=9000
 # The telnet server will be started on the next port
 
 #
 # Define the server initialisation file
 beanshell.server.file=../extras/startup.bsh
 
 #
 # Define a file to be processed at startup
 # This is processed using its own interpreter.
 #beanshell.init.file=
 
 #
 # Define the intialisation files for BeanShell Sampler, Function and other BeanShell elements
 # N.B. Beanshell test elements do not share interpreters.
 #      Each element in each thread has its own interpreter.
 #      This is retained between samples.
 #beanshell.sampler.init=BeanShellSampler.bshrc
 #beanshell.function.init=BeanShellFunction.bshrc
 #beanshell.assertion.init=BeanShellAssertion.bshrc
 #beanshell.listener.init=etc
 #beanshell.postprocessor.init=etc
 #beanshell.preprocessor.init=etc
 #beanshell.timer.init=etc
 
 # The file BeanShellListeners.bshrc contains sample definitions
 # of Test and Thread Listeners.
 
 #---------------------------------------------------------------------------
 # MailerModel configuration
 #---------------------------------------------------------------------------
 
 # Number of successful samples before a message is sent
 #mailer.successlimit=2
 #
 # Number of failed samples before a message is sent
 #mailer.failurelimit=2
 
 #---------------------------------------------------------------------------
 # CSVRead configuration
 #---------------------------------------------------------------------------
 
 # CSVRead delimiter setting (default ",")
 # Make sure that there are no trailing spaces or tabs after the delimiter
 # characters, or these will be included in the list of valid delimiters
 #csvread.delimiter=,
 #csvread.delimiter=;
 #csvread.delimiter=!
 #csvread.delimiter=~
 # The following line has a tab after the =
 #csvread.delimiter=	
 
 #---------------------------------------------------------------------------
 # __time() function configuration
 #
 # The properties below can be used to redefine the default formats
 #---------------------------------------------------------------------------
 #time.YMD=yyyyMMdd
 #time.HMS=HHmmss
 #time.YMDHMS=yyyyMMdd-HHmmss
 #time.USER1=
 #time.USER2=
 
 #---------------------------------------------------------------------------
 # CSV DataSet configuration
 #---------------------------------------------------------------------------
 
 # String to return at EOF (if recycle not used)
 #csvdataset.eofstring=<EOF>
 
 #---------------------------------------------------------------------------
 # LDAP Sampler configuration
 #---------------------------------------------------------------------------
 # Maximum number of search results returned by a search that will be sorted
 # to guarantee a stable ordering (if more results then this limit are retruned
 # then no sorting is done). Set to 0 to turn off all sorting, in which case
 # "Equals" response assertions will be very likely to fail against search results.
 #
 #ldapsampler.max_sorted_results=1000
  
 # Number of characters to log for each of three sections (starting matching section, diff section,
 #   ending matching section where not all sections will appear for all diffs) diff display when an Equals
 #   assertion fails. So a value of 100 means a maximum of 300 characters of diff text will be displayed
 #   (+ a number of extra characters like "..." and "[[["/"]]]" which are used to decorate it).
 #assertion.equals_section_diff_len=100
 # test written out to log to signify start/end of diff delta
 #assertion.equals_diff_delta_start=[[[
 #assertion.equals_diff_delta_end=]]]
 
 #---------------------------------------------------------------------------
 # Miscellaneous configuration
 #---------------------------------------------------------------------------
 
 # If defined, then start the mirror server on the port
 #mirror.server.port=8081
 
 # ORO PatternCacheLRU size
 #oro.patterncache.size=1000
 
 #TestBeanGui
 #
 #propertyEditorSearchPath=null
 
 # Turn expert mode on/off: expert mode will show expert-mode beans and properties
 #jmeter.expertMode=true
 
 # Maximum redirects to follow in a single sequence (default 5)
 #httpsampler.max_redirects=5
 # Maximum frame/iframe nesting depth (default 5)
 #httpsampler.max_frame_depth=5
-# Maximum await termination timeout (secs) when concurrent download embedded resources (default 60)
-#httpsampler.await_termination_timeout=60
+
 # Revert to BUG 51939 behaviour (no separate container for embedded resources) by setting the following false:
 #httpsampler.separate.container=true
 
 # If embedded resources download fails due to missing resources or other reasons, if this property is true
 # Parent sample will not be marked as failed 
 #httpsampler.ignore_failed_embedded_resources=false
 
+#keep alive time for the parallel download threads (in seconds)
+#httpsampler.parallel_download_thread_keepalive_inseconds=60
+
 # Don't keep the embedded resources response data : just keep the size and the md5
 # default to false
 #httpsampler.embedded_resources_use_md5=false
 
 # List of extra HTTP methods that should be available in select box
 #httpsampler.user_defined_methods=VERSION-CONTROL,REPORT,CHECKOUT,CHECKIN,UNCHECKOUT,MKWORKSPACE,UPDATE,LABEL,MERGE,BASELINE-CONTROL,MKACTIVITY
 
 # The encoding to be used if none is provided (default ISO-8859-1)
 #sampleresult.default.encoding=ISO-8859-1
 
 # Network response size calculation method
 # Use real size: number of bytes for response body return by webserver
 # (i.e. the network bytes received for response)
 # if set to false, the (uncompressed) response data size will used (default before 2.5)
 # Include headers: add the headers size in real size
 #sampleresult.getbytes.body_real_size=true
 #sampleresult.getbytes.headers_size=true
 
 # CookieManager behaviour - should cookies with null/empty values be deleted?
 # Default is true. Use false to revert to original behaviour
 #CookieManager.delete_null_cookies=true
 
 # CookieManager behaviour - should variable cookies be allowed?
 # Default is true. Use false to revert to original behaviour
 #CookieManager.allow_variable_cookies=true
 
 # CookieManager behaviour - should Cookies be stored as variables?
 # Default is false
 #CookieManager.save.cookies=false
 
 # CookieManager behaviour - prefix to add to cookie name before storing it as a variable
 # Default is COOKIE_; to remove the prefix, define it as one or more spaces
 #CookieManager.name.prefix=
  
 # CookieManager behaviour - check received cookies are valid before storing them?
 # Default is true. Use false to revert to previous behaviour
 #CookieManager.check.cookies=true
 
 # Ability to switch to Nashorn as default Javascript Engine used by IfController and __javaScript function
 # JMeter works as following:
 # - JDK < 8 : Rhino
 # - JDK >= 8 and javascript.use_rhino=false: Nashorn
 # If you want to use Nashorn on JDK8, set this property to false
 #javascript.use_rhino=true
 
 # Number of milliseconds to wait for a thread to stop
 #jmeterengine.threadstop.wait=5000
 
 #Whether to invoke System.exit(0) in server exit code after stopping RMI
 #jmeterengine.remote.system.exit=false
 
 # Whether to call System.exit(1) on failure to stop threads in non-GUI mode.
 # This only takes effect if the test was explictly requested to stop.
 # If this is disabled, it may be necessary to kill the JVM externally
 #jmeterengine.stopfail.system.exit=true
 
 # Whether to force call System.exit(0) at end of test in non-GUI mode, even if
 # there were no failures and the test was not explicitly asked to stop.
 # Without this, the JVM may never exit if there are other threads spawned by
 # the test which never exit.
 #jmeterengine.force.system.exit=false
 
 # How long to pause (in ms) in the daemon thread before reporting that the JVM has failed to exit.
 # If the value is <= 0, the JMeter does not start the daemon thread 
 #jmeter.exit.check.pause=2000
 
 # If running non-GUI, then JMeter listens on the following port for a shutdown message.
 # To disable, set the port to 1000 or less.
 #jmeterengine.nongui.port=4445
 #
 # If the initial port is busy, keep trying until this port is reached
 # (to disable searching, set the value less than or equal to the .port property)
 #jmeterengine.nongui.maxport=4455
 
 # How often to check for shutdown during ramp-up (milliseconds)
 #jmeterthread.rampup.granularity=1000
 
 #Should JMeter expand the tree when loading a test plan?
 # default value is false since JMeter 2.7
 #onload.expandtree=false
 
 #JSyntaxTextArea configuration
 #jsyntaxtextarea.wrapstyleword=true
 #jsyntaxtextarea.linewrap=true
 #jsyntaxtextarea.codefolding=true
 # Set 0 to disable undo feature in JSyntaxTextArea
 #jsyntaxtextarea.maxundos=50
 # Change the font on the (JSyntax) Text Areas. (Useful for HiDPI screens)
 #jsyntaxtextarea.font.family=Hack
 #jsyntaxtextarea.font.size=14
 
 # Set this to false to disable the use of JSyntaxTextArea for the Console Logger panel 
 #loggerpanel.usejsyntaxtext=true
 
 # Maximum size of HTML page that can be displayed; default=10 mbytes
 # Set to 0 to disable the size check and display the whole response
 #view.results.tree.max_size=10485760
 
 # Order of Renderers in View Results Tree
 # Note full class names should be used for non jmeter core renderers
 # For JMeter core renderers, class names start with . and are automatically
 # prefixed with org.apache.jmeter.visualizers
 view.results.tree.renderers_order=.RenderAsText,.RenderAsRegexp,.RenderAsCssJQuery,.RenderAsXPath,.RenderAsHTML,.RenderAsHTMLWithEmbedded,.RenderAsDocument,.RenderAsJSON,.RenderAsXML
 
 # Maximum size of Document that can be parsed by Tika engine; defaut=10 * 1024 * 1024 (10MB)
 # Set to 0 to disable the size check
 #document.max_size=0
 
 #JMS options
 # Enable the following property to stop JMS Point-to-Point Sampler from using
 # the properties java.naming.security.[principal|credentials] when creating the queue connection
 #JMSSampler.useSecurity.properties=false
 
 # Set the following value to true in order to skip the delete confirmation dialogue
 #confirm.delete.skip=false
 
 # Used by JSR223 elements
 # Size of compiled scripts cache
 #jsr223.compiled_scripts_cache_size=100
 
 #---------------------------------------------------------------------------
 # Classpath configuration
 #---------------------------------------------------------------------------
 
 # List of paths (separated by ;) to search for additional JMeter plugin classes,
 # for example new GUI elements and samplers.
 # A path item can either be a jar file or a directory.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib/ext directory.
 # Do not use this for utility or plugin dependency jars.
 #search_paths=/app1/lib;/app2/lib
 
 # List of paths that JMeter will search for utility and plugin dependency classes.
 # Use your platform path separator to separate multiple paths.
 # A path item can either be a jar file or a directory.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib directory.
 # All entries will be added to the class path of the system class loader
 # and also to the path of the JMeter internal loader.
 # Paths with spaces may cause problems for the JVM
 #user.classpath=../classes;../lib;../app1/jar1.jar;../app2/jar2.jar
 
 # List of paths (separated by ;) that JMeter will search for utility
 # and plugin dependency classes.
 # A path item can either be a jar file or a directory.
 # Any jar file in such a directory will be automatically included,
 # jar files in sub directories are ignored.
 # The given value is in addition to any jars found in the lib directory
 # or given by the user.classpath property.
 # All entries will be added to the path of the JMeter internal loader only.
 # For plugin dependencies using plugin_dependency_paths should be preferred over
 # user.classpath.
 #plugin_dependency_paths=../dependencies/lib;../app1/jar1.jar;../app2/jar2.jar
 
 # Classpath finder
 # ================
 # The classpath finder currently needs to load every single JMeter class to find
 # the classes it needs.
 # For non-GUI mode, it's only necessary to scan for Function classes, but all classes
 # are still loaded.
 # All current Function classes include ".function." in their name,
 # and none include ".gui." in the name, so the number of unwanted classes loaded can be
 # reduced by checking for these. However, if a valid function class name does not match
 # these restrictions, it will not be loaded. If problems are encountered, then comment
 # or change the following properties:
 classfinder.functions.contain=.functions.
 classfinder.functions.notContain=.gui.
 
 #---------------------------------------------------------------------------
 # Reporting configuration
 #---------------------------------------------------------------------------
 
 # Sets the satisfaction threshold for the APDEX calculation (in milliseconds).
 #jmeter.reportgenerator.apdex_statisfied_threshold=500
 
 # Sets the tolerance threshold for the APDEX calculation (in milliseconds).
 #jmeter.reportgenerator.apdex_tolerated_threshold=1500
 
 # Sets the filter for samples to keep for graphs and statistics generation.
 # A comma separated list of samples names, empty string means no filtering
 #jmeter.reportgenerator.sample_filter=
 
 # Sets the temporary directory used by the generation processus if it needs file I/O operations.
 #jmeter.reportgenerator.temp_dir=temp
 
 # Sets the size of the sliding window used by percentile evaluation.
 # Caution : higher value provides a better accurency but needs more memory.
 #jmeter.reportgenerator.statistic_window = 200000
 
 # Defines the overall granularity for over time graphs
 jmeter.reportgenerator.overall_granularity=60000
 
 # Response Time Percentiles graph definition
 jmeter.reportgenerator.graph.responseTimePercentiles.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimePercentilesGraphConsumer
 jmeter.reportgenerator.graph.responseTimePercentiles.title=Response Time Percentiles
 
 # Response Time Distribution graph definition
 jmeter.reportgenerator.graph.responseTimeDistribution.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimeDistributionGraphConsumer
 jmeter.reportgenerator.graph.responseTimeDistribution.title=Response Time Distribution
 jmeter.reportgenerator.graph.responseTimeDistribution.property.set_granularity=1000
 
 # Active Threads Over Time graph definition
 jmeter.reportgenerator.graph.activeThreadsOverTime.classname=org.apache.jmeter.report.processor.graph.impl.ActiveThreadsGraphConsumer
 jmeter.reportgenerator.graph.activeThreadsOverTime.title=Active Threads Over Time
 jmeter.reportgenerator.graph.activeThreadsOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Response Time Per Sample graph definition
 jmeter.reportgenerator.graph.responseTimePerSample.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimePerSampleGraphConsumer
 jmeter.reportgenerator.graph.responseTimePerSample.title=Response Time Per Sample
 
 # Time VS Threads graph definition
 jmeter.reportgenerator.graph.timeVsThreads.classname=org.apache.jmeter.report.processor.graph.impl.TimeVSThreadGraphConsumer
 jmeter.reportgenerator.graph.timeVsThreads.title=Time VS Threads
 
 # Bytes Throughput Over Time graph definition
 jmeter.reportgenerator.graph.bytesThroughputOverTime.classname=org.apache.jmeter.report.processor.graph.impl.BytesThroughputGraphConsumer
 jmeter.reportgenerator.graph.bytesThroughputOverTime.title=Bytes Throughput Over Time
 jmeter.reportgenerator.graph.bytesThroughputOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Response Time Over Time graph definition
 jmeter.reportgenerator.graph.responseTimesOverTime.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimeOverTimeGraphConsumer
 jmeter.reportgenerator.graph.responseTimesOverTime.title=Response Time Over Time
 jmeter.reportgenerator.graph.responseTimesOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Latencies Over Time graph definition
 jmeter.reportgenerator.graph.latenciesOverTime.classname=org.apache.jmeter.report.processor.graph.impl.LatencyOverTimeGraphConsumer
 jmeter.reportgenerator.graph.latenciesOverTime.title=Latencies Over Time
 jmeter.reportgenerator.graph.latenciesOverTime.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Response Time Vs Request graph definition
 jmeter.reportgenerator.graph.responseTimeVsRequest.classname=org.apache.jmeter.report.processor.graph.impl.ResponseTimeVSRequestGraphConsumer
 jmeter.reportgenerator.graph.responseTimeVsRequest.title=Response Time Vs Request
 jmeter.reportgenerator.graph.responseTimeVsRequest.exclude_controllers=true
 jmeter.reportgenerator.graph.responseTimeVsRequest.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Latencies Vs Request graph definition
 jmeter.reportgenerator.graph.latencyVsRequest.classname=org.apache.jmeter.report.processor.graph.impl.LatencyVSRequestGraphConsumer
 jmeter.reportgenerator.graph.latencyVsRequest.title=Latencies Vs Request
 jmeter.reportgenerator.graph.latencyVsRequest.exclude_controllers=true
 jmeter.reportgenerator.graph.latencyVsRequest.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Hits Per Second graph definition
 jmeter.reportgenerator.graph.hitsPerSecond.classname=org.apache.jmeter.report.processor.graph.impl.HitsPerSecondGraphConsumer
 jmeter.reportgenerator.graph.hitsPerSecond.title=Hits Per Second
 jmeter.reportgenerator.graph.hitsPerSecond.exclude_controllers=true
 jmeter.reportgenerator.graph.hitsPerSecond.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Codes Per Second graph definition
 jmeter.reportgenerator.graph.codesPerSecond.classname=org.apache.jmeter.report.processor.graph.impl.CodesPerSecondGraphConsumer
 jmeter.reportgenerator.graph.codesPerSecond.title=Codes Per Second
 jmeter.reportgenerator.graph.codesPerSecond.exclude_controllers=true
 jmeter.reportgenerator.graph.codesPerSecond.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # Transactions Per Second graph definition
 jmeter.reportgenerator.graph.transactionsPerSecond.classname=org.apache.jmeter.report.processor.graph.impl.TransactionsPerSecondGraphConsumer
 jmeter.reportgenerator.graph.transactionsPerSecond.title=Transactions Per Second
 jmeter.reportgenerator.graph.transactionsPerSecond.property.set_granularity=${jmeter.reportgenerator.overall_granularity}
 
 # HTML Export
 jmeter.reportgenerator.exporter.html.classname=org.apache.jmeter.report.dashboard.HtmlTemplateExporter
 
 # Sets the source directory of templated files from which the html pages are generated.
 #jmeter.reportgenerator.exporter.html.property.template_dir=report-template
 
 # Sets the destination directory for generated html pages.
 # This will be overriden by the command line option -o 
 #jmeter.reportgenerator.exporter.html.property.output_dir=report-output
 
 # Indicates which graph series are filtered (regular expression)
 #jmeter.reportgenerator.exporter.html.series_filter=
 
 # Indicates whether series filter apply only on sample series
 #jmeter.reportgenerator.exporter.html.filters_only_sample_series=false
 
 # Indicates whether only controller samples are displayed on graphs that support it.
 #jmeter.reportgenerator.exporter.html.show_controllers_only=false
 
 #---------------------------------------------------------------------------
 # Additional property files to load
 #---------------------------------------------------------------------------
 
 # Should JMeter automatically load additional JMeter properties?
 # File name to look for (comment to disable)
 user.properties=user.properties
 
 # Should JMeter automatically load additional system properties?
 # File name to look for (comment to disable)
 system.properties=system.properties
 
 # Comma separated list of files that contain reference to templates and their description
 # Path must be relative to jmeter root folder
 #template.files=/bin/templates/templates.xml
diff --git a/src/protocol/http/org/apache/http/impl/conn/JMeterPoolingClientConnectionManager.java b/src/protocol/http/org/apache/http/impl/conn/JMeterPoolingClientConnectionManager.java
new file mode 100644
index 000000000..c54a839a6
--- /dev/null
+++ b/src/protocol/http/org/apache/http/impl/conn/JMeterPoolingClientConnectionManager.java
@@ -0,0 +1,353 @@
+/*
+ * ====================================================================
+ * Licensed to the Apache Software Foundation (ASF) under one
+ * or more contributor license agreements.  See the NOTICE file
+ * distributed with this work for additional information
+ * regarding copyright ownership.  The ASF licenses this file
+ * to you under the Apache License, Version 2.0 (the
+ * "License"); you may not use this file except in compliance
+ * with the License.  You may obtain a copy of the License at
+ *
+ *   http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing,
+ * software distributed under the License is distributed on an
+ * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
+ * KIND, either express or implied.  See the License for the
+ * specific language governing permissions and limitations
+ * under the License.
+ * ====================================================================
+ *
+ * This software consists of voluntary contributions made by many
+ * individuals on behalf of the Apache Software Foundation.  For more
+ * information on the Apache Software Foundation, please see
+ * <http://www.apache.org/>.
+ *
+ */
+package org.apache.http.impl.conn;
+
+import java.io.IOException;
+import java.util.concurrent.ExecutionException;
+import java.util.concurrent.Future;
+import java.util.concurrent.TimeUnit;
+import java.util.concurrent.TimeoutException;
+
+import org.apache.commons.logging.Log;
+import org.apache.commons.logging.LogFactory;
+import org.apache.http.annotation.ThreadSafe;
+import org.apache.http.conn.ClientConnectionManager;
+import org.apache.http.conn.ClientConnectionOperator;
+import org.apache.http.conn.ClientConnectionRequest;
+import org.apache.http.conn.ConnectionPoolTimeoutException;
+import org.apache.http.conn.DnsResolver;
+import org.apache.http.conn.ManagedClientConnection;
+import org.apache.http.conn.routing.HttpRoute;
+import org.apache.http.conn.scheme.SchemeRegistry;
+import org.apache.http.pool.ConnPoolControl;
+import org.apache.http.pool.PoolStats;
+import org.apache.http.util.Args;
+import org.apache.http.util.Asserts;
+
+/**
+ * Copy/Paste of {@link PoolingClientConnectionManager} with a slight modification 
+ * extracted from {@link PoolingHttpClientConnectionManager} to allow using 
+ * better validation mechanism introduced in 4.4
+ * TODO : Remove when full upgrade to new HttpClient 4.5.X API is finished
+ * @deprecated Will be removed in 3.1, DO NOT USE
+ */
+@Deprecated
+@ThreadSafe
+public class JMeterPoolingClientConnectionManager implements ClientConnectionManager, ConnPoolControl<HttpRoute> {
+
+    private static final int VALIDATE_AFTER_INACTIVITY_DEFAULT = 2000;
+
+    private final Log log = LogFactory.getLog(getClass());
+    
+    private final SchemeRegistry schemeRegistry;
+
+    private final HttpConnPool pool;
+
+    private final ClientConnectionOperator operator;
+    
+
+    /** the custom-configured DNS lookup mechanism. */
+    private final DnsResolver dnsResolver;
+
+    public JMeterPoolingClientConnectionManager(final SchemeRegistry schreg) {
+        this(schreg, -1, TimeUnit.MILLISECONDS);
+    }
+
+    public JMeterPoolingClientConnectionManager(final SchemeRegistry schreg,final DnsResolver dnsResolver) {
+        this(schreg, -1, TimeUnit.MILLISECONDS,dnsResolver);
+    }
+
+    public JMeterPoolingClientConnectionManager() {
+        this(SchemeRegistryFactory.createDefault());
+    }
+
+    public JMeterPoolingClientConnectionManager(
+            final SchemeRegistry schemeRegistry,
+            final long timeToLive, final TimeUnit tunit) {
+        this(schemeRegistry, timeToLive, tunit, new SystemDefaultDnsResolver());
+    }
+    
+    public JMeterPoolingClientConnectionManager(
+            final SchemeRegistry schemeRegistry,
+            final long timeToLive, final TimeUnit tunit,
+            final DnsResolver dnsResolver) {
+        this(schemeRegistry, timeToLive, tunit, dnsResolver, VALIDATE_AFTER_INACTIVITY_DEFAULT);
+    }
+
+    public JMeterPoolingClientConnectionManager(final SchemeRegistry schemeRegistry,
+                final long timeToLive, final TimeUnit tunit,
+                final DnsResolver dnsResolver,
+                int validateAfterInactivity) {
+        super();
+        Args.notNull(schemeRegistry, "Scheme registry");
+        Args.notNull(dnsResolver, "DNS resolver");
+        this.schemeRegistry = schemeRegistry;
+        this.dnsResolver  = dnsResolver;
+        this.operator = createConnectionOperator(schemeRegistry);
+        this.pool = new HttpConnPool(this.log, this.operator, 2, 20, timeToLive, tunit);
+        pool.setValidateAfterInactivity(validateAfterInactivity);
+    }
+    
+    /**
+     * @see #setValidateAfterInactivity(int)
+     *
+     * @since 4.5.2
+     */
+    public int getValidateAfterInactivity() {
+        return pool.getValidateAfterInactivity();
+    }
+
+    /**
+     * Defines period of inactivity in milliseconds after which persistent connections must
+     * be re-validated prior to being {@link #leaseConnection(java.util.concurrent.Future,
+     *   long, java.util.concurrent.TimeUnit) leased} to the consumer. Non-positive value passed
+     * to this method disables connection validation. This check helps detect connections
+     * that have become stale (half-closed) while kept inactive in the pool.
+     *
+     * @see #leaseConnection(java.util.concurrent.Future, long, java.util.concurrent.TimeUnit)
+     *
+     * @since 4.5.2
+     */
+    public void setValidateAfterInactivity(final int ms) {
+        pool.setValidateAfterInactivity(ms);
+    }
+
+    @Override
+    protected void finalize() throws Throwable {
+        try {
+            shutdown();
+        } finally {
+            super.finalize();
+        }
+    }
+
+    /**
+     * Hook for creating the connection operator.
+     * It is called by the constructor.
+     * Derived classes can override this method to change the
+     * instantiation of the operator.
+     * The default implementation here instantiates
+     * {@link DefaultClientConnectionOperator DefaultClientConnectionOperator}.
+     *
+     * @param schreg    the scheme registry.
+     *
+     * @return  the connection operator to use
+     */
+    protected ClientConnectionOperator createConnectionOperator(final SchemeRegistry schreg) {
+            return new DefaultClientConnectionOperator(schreg, this.dnsResolver);
+    }
+    @Override
+    public SchemeRegistry getSchemeRegistry() {
+        return this.schemeRegistry;
+    }
+
+    private String format(final HttpRoute route, final Object state) {
+        final StringBuilder buf = new StringBuilder();
+        buf.append("[route: ").append(route).append("]");
+        if (state != null) {
+            buf.append("[state: ").append(state).append("]");
+        }
+        return buf.toString();
+    }
+
+    private String formatStats(final HttpRoute route) {
+        final StringBuilder buf = new StringBuilder();
+        final PoolStats totals = this.pool.getTotalStats();
+        final PoolStats stats = this.pool.getStats(route);
+        buf.append("[total kept alive: ").append(totals.getAvailable()).append("; ");
+        buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
+        buf.append(" of ").append(stats.getMax()).append("; ");
+        buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
+        buf.append(" of ").append(totals.getMax()).append("]");
+        return buf.toString();
+    }
+
+    private String format(final HttpPoolEntry entry) {
+        final StringBuilder buf = new StringBuilder();
+        buf.append("[id: ").append(entry.getId()).append("]");
+        buf.append("[route: ").append(entry.getRoute()).append("]");
+        final Object state = entry.getState();
+        if (state != null) {
+            buf.append("[state: ").append(state).append("]");
+        }
+        return buf.toString();
+    }
+
+    @Override
+    public ClientConnectionRequest requestConnection(
+            final HttpRoute route,
+            final Object state) {
+        Args.notNull(route, "HTTP route");
+        if (this.log.isDebugEnabled()) {
+            this.log.debug("Connection request: " + format(route, state) + formatStats(route));
+        }
+        final Future<HttpPoolEntry> future = this.pool.lease(route, state);
+
+        return new ClientConnectionRequest() {
+            @Override
+            public void abortRequest() {
+                future.cancel(true);
+            }
+            @Override
+            public ManagedClientConnection getConnection(
+                    final long timeout,
+                    final TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
+                return leaseConnection(future, timeout, tunit);
+            }
+
+        };
+
+    }
+
+    ManagedClientConnection leaseConnection(
+            final Future<HttpPoolEntry> future,
+            final long timeout,
+            final TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
+        final HttpPoolEntry entry;
+        try {
+            entry = future.get(timeout, tunit);
+            if (entry == null || future.isCancelled()) {
+                throw new InterruptedException();
+            }
+            Asserts.check(entry.getConnection() != null, "Pool entry with no connection");
+            if (this.log.isDebugEnabled()) {
+                this.log.debug("Connection leased: " + format(entry) + formatStats(entry.getRoute()));
+            }
+            return new ManagedClientConnectionImpl(this, this.operator, entry);
+        } catch (final ExecutionException ex) {
+            Throwable cause = ex.getCause();
+            if (cause == null) {
+                cause = ex;
+            }
+            this.log.error("Unexpected exception leasing connection from pool", cause);
+            // Should never happen
+            throw new InterruptedException();
+        } catch (final TimeoutException ex) {
+            throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
+        }
+    }
+    @Override
+    public void releaseConnection(
+            final ManagedClientConnection conn, final long keepalive, final TimeUnit tunit) {
+
+        Args.check(conn instanceof ManagedClientConnectionImpl, "Connection class mismatch, " +
+            "connection not obtained from this manager");
+        final ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl) conn;
+        Asserts.check(managedConn.getManager() == this, "Connection not obtained from this manager");
+        synchronized (managedConn) {
+            final HttpPoolEntry entry = managedConn.detach();
+            if (entry == null) {
+                return;
+            }
+            try {
+                if (managedConn.isOpen() && !managedConn.isMarkedReusable()) {
+                    try {
+                        managedConn.shutdown();
+                    } catch (final IOException iox) {
+                        if (this.log.isDebugEnabled()) {
+                            this.log.debug("I/O exception shutting down released connection", iox);
+                        }
+                    }
+                }
+                // Only reusable connections can be kept alive
+                if (managedConn.isMarkedReusable()) {
+                    entry.updateExpiry(keepalive, tunit != null ? tunit : TimeUnit.MILLISECONDS);
+                    if (this.log.isDebugEnabled()) {
+                        final String s;
+                        if (keepalive > 0) {
+                            s = "for " + keepalive + " " + tunit;
+                        } else {
+                            s = "indefinitely";
+                        }
+                        this.log.debug("Connection " + format(entry) + " can be kept alive " + s);
+                    }
+                }
+            } finally {
+                this.pool.release(entry, managedConn.isMarkedReusable());
+            }
+            if (this.log.isDebugEnabled()) {
+                this.log.debug("Connection released: " + format(entry) + formatStats(entry.getRoute()));
+            }
+        }
+    }
+    @Override
+    public void shutdown() {
+        this.log.debug("Connection manager is shutting down");
+        try {
+            this.pool.shutdown();
+        } catch (final IOException ex) {
+            this.log.debug("I/O exception shutting down connection manager", ex);
+        }
+        this.log.debug("Connection manager shut down");
+    }
+    @Override
+    public void closeIdleConnections(final long idleTimeout, final TimeUnit tunit) {
+        if (this.log.isDebugEnabled()) {
+            this.log.debug("Closing connections idle longer than " + idleTimeout + " " + tunit);
+        }
+        this.pool.closeIdle(idleTimeout, tunit);
+    }
+    @Override
+    public void closeExpiredConnections() {
+        this.log.debug("Closing expired connections");
+        this.pool.closeExpired();
+    }
+    @Override
+    public int getMaxTotal() {
+        return this.pool.getMaxTotal();
+    }
+    @Override
+    public void setMaxTotal(final int max) {
+        this.pool.setMaxTotal(max);
+    }
+    @Override
+    public int getDefaultMaxPerRoute() {
+        return this.pool.getDefaultMaxPerRoute();
+    }
+    @Override
+    public void setDefaultMaxPerRoute(final int max) {
+        this.pool.setDefaultMaxPerRoute(max);
+    }
+    @Override
+    public int getMaxPerRoute(final HttpRoute route) {
+        return this.pool.getMaxPerRoute(route);
+    }
+    @Override
+    public void setMaxPerRoute(final HttpRoute route, final int max) {
+        this.pool.setMaxPerRoute(route, max);
+    }
+    @Override
+    public PoolStats getTotalStats() {
+        return this.pool.getTotalStats();
+    }
+    @Override
+    public PoolStats getStats(final HttpRoute route) {
+        return this.pool.getStats(route);
+    }
+
+}
+
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
index b522564da..ce722deaf 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/control/CacheManager.java
@@ -1,499 +1,518 @@
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
 
-// For unit tests @see TestCookieManager
-
 package org.apache.jmeter.protocol.http.control;
 
 import java.io.Serializable;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Date;
 import java.util.Map;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.commons.httpclient.HttpMethod;
 import org.apache.commons.httpclient.URIException;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.http.HttpResponse;
 import org.apache.http.client.methods.HttpRequestBase;
 import org.apache.http.client.utils.DateUtils;
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.engine.event.LoopIterationEvent;
 import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
 import org.apache.jmeter.protocol.http.util.HTTPConstants;
 import org.apache.jmeter.testelement.TestIterationListener;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.log.Logger;
 
 /**
  * Handles HTTP Caching
  */
 public class CacheManager extends ConfigTestElement implements TestStateListener, TestIterationListener, Serializable {
 
     private static final Date EXPIRED_DATE = new Date(0L);
 
     private static final long serialVersionUID = 234L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final String[] CACHEABLE_METHODS = JMeterUtils.getPropDefault("cacheable_methods", "GET").split("[ ,]");
 
     static {
         log.info("Will only cache the following methods: "+Arrays.toString(CACHEABLE_METHODS));
     }
 
     //+ JMX attributes, do not change values
     public static final String CLEAR = "clearEachIteration"; // $NON-NLS-1$
     public static final String USE_EXPIRES = "useExpires"; // $NON-NLS-1$
     public static final String MAX_SIZE = "maxSize";  // $NON-NLS-1$
     //-
 
     private transient InheritableThreadLocal<Map<String, CacheEntry>> threadCache;
 
     private transient boolean useExpires; // Cached value
 
     private static final int DEFAULT_MAX_SIZE = 5000;
 
     private static final long ONE_YEAR_MS = 365*24*60*60*1000L;
+    
+    /** used to share the cache between 2 cache managers
+     * @see CacheManager#createCacheManagerProxy() 
+     * @since 3.0 */
+    private transient Map<String, CacheEntry> localCache;
 
     public CacheManager() {
         setProperty(new BooleanProperty(CLEAR, false));
         setProperty(new BooleanProperty(USE_EXPIRES, false));
         clearCache();
         useExpires = false;
     }
+    
+    CacheManager(Map<String, CacheEntry> localCache, boolean useExpires) {
+        this.localCache = localCache;
+        this.useExpires = useExpires;
+    }
 
     /*
      * Holder for storing cache details.
      * Perhaps add original response later?
      */
     // package-protected to allow access by unit-test cases
     static class CacheEntry{
         private final String lastModified;
         private final String etag;
         private final Date expires;
         public CacheEntry(String lastModified, Date expires, String etag){
            this.lastModified = lastModified;
            this.etag = etag;
            this.expires = expires;
        }
         public String getLastModified() {
             return lastModified;
         }
         public String getEtag() {
             return etag;
         }
         @Override
         public String toString(){
             return lastModified+" "+etag;
         }
         public Date getExpires() {
             return expires;
         }
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is cacheable.
      * Version for Java implementation.
      * @param conn connection
      * @param res result
      */
     public void saveDetails(URLConnection conn, HTTPSampleResult res){
         if (isCacheable(res) && !hasVaryHeader(conn)){
             String lastModified = conn.getHeaderField(HTTPConstants.LAST_MODIFIED);
             String expires = conn.getHeaderField(HTTPConstants.EXPIRES);
             String etag = conn.getHeaderField(HTTPConstants.ETAG);
             String url = conn.getURL().toString();
             String cacheControl = conn.getHeaderField(HTTPConstants.CACHE_CONTROL);
             String date = conn.getHeaderField(HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, url, date);
         }
     }
 
     private boolean hasVaryHeader(URLConnection conn) {
         return conn.getHeaderField(HTTPConstants.VARY) != null;
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is
      * cacheable. Version for Commons HttpClient implementation.
      *
      * @param method
      *            {@link HttpMethod} to get header information from
      * @param res
      *            result to decide if result is cacheable
      * @throws URIException
      *             if extraction of the the uri from <code>method</code> fails
      * @deprecated HC3.1 will be dropped in upcoming version
      */
     @Deprecated
     public void saveDetails(HttpMethod method, HTTPSampleResult res) throws URIException{
         if (isCacheable(res) && !hasVaryHeader(method)){
             String lastModified = getHeader(method ,HTTPConstants.LAST_MODIFIED);
             String expires = getHeader(method ,HTTPConstants.EXPIRES);
             String etag = getHeader(method ,HTTPConstants.ETAG);
             String url = method.getURI().toString();
             String cacheControl = getHeader(method, HTTPConstants.CACHE_CONTROL);
             String date = getHeader(method, HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, url, date);
         }
     }
 
     /**
      * @deprecated HC3.1 will be dropped in upcoming version
      */
     @Deprecated
     private boolean hasVaryHeader(HttpMethod method) {
         return getHeader(method, HTTPConstants.VARY) != null;
     }
 
     /**
      * Save the Last-Modified, Etag, and Expires headers if the result is
      * cacheable. Version for Apache HttpClient implementation.
      *
      * @param method
      *            {@link HttpResponse} to extract header information from
      * @param res
      *            result to decide if result is cacheable
      */
     public void saveDetails(HttpResponse method, HTTPSampleResult res) {
         if (isCacheable(res) && !hasVaryHeader(method)){
             String lastModified = getHeader(method ,HTTPConstants.LAST_MODIFIED);
             String expires = getHeader(method ,HTTPConstants.EXPIRES);
             String etag = getHeader(method ,HTTPConstants.ETAG);
             String cacheControl = getHeader(method, HTTPConstants.CACHE_CONTROL);
             String date = getHeader(method, HTTPConstants.DATE);
             setCache(lastModified, cacheControl, expires, etag, res.getUrlAsString(), date); // TODO correct URL?
         }
     }
 
     private boolean hasVaryHeader(HttpResponse method) {
         return getHeader(method, HTTPConstants.VARY) != null;
     }
 
     // helper method to save the cache entry
     private void setCache(String lastModified, String cacheControl, String expires, String etag, String url, String date) {
         if (log.isDebugEnabled()){
             log.debug("setCache("
                   + lastModified + "," 
                   + cacheControl + ","
                   + expires + "," 
                   + etag + ","
                   + url + ","
                   + date
                   + ")");
         }
         Date expiresDate = null; // i.e. not using Expires
         if (useExpires) {// Check that we are processing Expires/CacheControl
             final String MAX_AGE = "max-age=";
             
             if(cacheControl != null && cacheControl.contains("no-store")) {
                 // We must not store an CacheEntry, otherwise a 
                 // conditional request may be made
                 return;
             }
             if (expires != null) {
                 try {
                     expiresDate = org.apache.http.client.utils.DateUtils.parseDate(expires);
                 } catch (IllegalArgumentException e) {
                     if (log.isDebugEnabled()){
                         log.debug("Unable to parse Expires: '"+expires+"' "+e);
                     }
                     expiresDate = CacheManager.EXPIRED_DATE; // invalid dates must be treated as expired
                 }
             }
             // if no-cache is present, ensure that expiresDate remains null, which forces revalidation
-            if(cacheControl != null && !cacheControl.contains("no-cache")) {    
+            if(cacheControl != null && !cacheControl.contains("no-cache")) {
                 // the max-age directive overrides the Expires header,
                 if(cacheControl.contains(MAX_AGE)) {
                     long maxAgeInSecs = Long.parseLong(
                             cacheControl.substring(cacheControl.indexOf(MAX_AGE)+MAX_AGE.length())
                                 .split("[, ]")[0] // Bug 51932 - allow for optional trailing attributes
                             );
                     expiresDate=new Date(System.currentTimeMillis()+maxAgeInSecs*1000);
 
                 } else if(expires==null) { // No max-age && No expires
                     if(!StringUtils.isEmpty(lastModified) && !StringUtils.isEmpty(date)) {
                         try {
                             Date responseDate = DateUtils.parseDate( date );
                             Date lastModifiedAsDate = DateUtils.parseDate( lastModified );
                             // see https://developer.mozilla.org/en/HTTP_Caching_FAQ
                             // see http://www.ietf.org/rfc/rfc2616.txt#13.2.4 
                             expiresDate=new Date(System.currentTimeMillis()
                                     +Math.round((responseDate.getTime()-lastModifiedAsDate.getTime())*0.1));
                         } catch(IllegalArgumentException e) {
                             // date or lastModified may be null or in bad format
                             if(log.isWarnEnabled()) {
                                 log.warn("Failed computing expiration date with following info:"
                                     +lastModified + "," 
                                     + cacheControl + ","
                                     + expires + "," 
                                     + etag + ","
                                     + url + ","
                                     + date);
                             }
                             // TODO Can't see anything in SPEC
-                            expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);                      
+                            expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);
                         }
                     } else {
                         // TODO Can't see anything in SPEC
-                        expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);                      
+                        expiresDate = new Date(System.currentTimeMillis()+ONE_YEAR_MS);
                     }
                 }  
                 // else expiresDate computed in (expires!=null) condition is used
             }
         }
         getCache().put(url, new CacheEntry(lastModified, expiresDate, etag));
     }
 
     /**
      * Helper method to deal with missing headers - Commons HttpClient
      * @param method Http method
      * @param name Header name
      * @return Header value
      * @deprecated HC3.1 will be dropped in upcoming version
      */
     @Deprecated
     private String getHeader(HttpMethod method, String name){
         org.apache.commons.httpclient.Header hdr = method.getResponseHeader(name);
         return hdr != null ? hdr.getValue() : null;
     }
 
     // Apache HttpClient
     private String getHeader(HttpResponse method, String name) {
         org.apache.http.Header hdr = method.getLastHeader(name);
         return hdr != null ? hdr.getValue() : null;
     }
 
     /*
      * Is the sample result OK to cache?
      * i.e is it in the 2xx range, and is it a cacheable method?
      */
     private boolean isCacheable(HTTPSampleResult res){
         final String responseCode = res.getResponseCode();
         return isCacheableMethod(res)
             && "200".compareTo(responseCode) <= 0  // $NON-NLS-1$
             && "299".compareTo(responseCode) >= 0;  // $NON-NLS-1$
     }
 
     private boolean isCacheableMethod(HTTPSampleResult res) {
         final String resMethod = res.getHTTPMethod();
         for(String method : CACHEABLE_METHODS) {
             if (method.equalsIgnoreCase(resMethod)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:
      * <ul>
      * <li>If-Modified-Since</li>
      * <li>If-None-Match</li>
      * </ul>
      * Commons HttpClient version
      * @param url URL to look up in cache
      * @param method where to set the headers
      * @deprecated HC3.1 will be dropped in upcoming version
      */
     @Deprecated
     public void setHeaders(URL url, HttpMethod method) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(method.getName()+"(OACH) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 method.setRequestHeader(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 method.setRequestHeader(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:
      * <ul>
      * <li>If-Modified-Since</li>
      * <li>If-None-Match</li>
      * </ul>
      * Apache HttpClient version.
      * @param url {@link URL} to look up in cache
      * @param request where to set the headers
      */
     public void setHeaders(URL url, HttpRequestBase request) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(request.getMethod()+"(OAH) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 request.setHeader(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 request.setHeader(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, and if there is a match, set the headers:
      * <ul>
      * <li>If-Modified-Since</li>
      * <li>If-None-Match</li>
      * </ul>
      * @param url {@link URL} to look up in cache
      * @param conn where to set the headers
      */
     public void setHeaders(HttpURLConnection conn, URL url) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug(conn.getRequestMethod()+"(Java) "+url.toString()+" "+entry);
         }
         if (entry != null){
             final String lastModified = entry.getLastModified();
             if (lastModified != null){
                 conn.addRequestProperty(HTTPConstants.IF_MODIFIED_SINCE, lastModified);
             }
             final String etag = entry.getEtag();
             if (etag != null){
                 conn.addRequestProperty(HTTPConstants.IF_NONE_MATCH, etag);
             }
         }
     }
 
     /**
      * Check the cache, if the entry has an expires header and the entry has not expired, return true<br>
      * @param url {@link URL} to look up in cache
      * @return <code>true</code> if entry has an expires header and the entry has not expired, else <code>false</code>
      */
     public boolean inCache(URL url) {
         CacheEntry entry = getCache().get(url.toString());
         if (log.isDebugEnabled()){
             log.debug("inCache "+url.toString()+" "+entry);
         }
         if (entry != null){
             final Date expiresDate = entry.getExpires();
             if (expiresDate != null) {
                 if (expiresDate.after(new Date())) {
                     if (log.isDebugEnabled()){
                         log.debug("Expires= " + expiresDate + " (Valid)");
                     }
                     return true;
                 } else {
                     if (log.isDebugEnabled()){
                         log.debug("Expires= " + expiresDate + " (Expired)");
                     }
                 }
             }
         }
         return false;
     }
 
-    private Map<String, CacheEntry> getCache(){
-        return threadCache.get();
+    private Map<String, CacheEntry> getCache() {
+        return localCache != null?localCache:threadCache.get();
     }
 
     public boolean getClearEachIteration() {
         return getPropertyAsBoolean(CLEAR);
     }
 
     public void setClearEachIteration(boolean clear) {
         setProperty(new BooleanProperty(CLEAR, clear));
     }
 
     public boolean getUseExpires() {
         return getPropertyAsBoolean(USE_EXPIRES);
     }
 
     public void setUseExpires(boolean expires) {
         setProperty(new BooleanProperty(USE_EXPIRES, expires));
     }
     
     /**
      * @return int cache max size
      */
     public int getMaxSize() {
         return getPropertyAsInt(MAX_SIZE, DEFAULT_MAX_SIZE);
     }
 
     /**
      * @param size int cache max size
      */
     public void setMaxSize(int size) {
         setProperty(MAX_SIZE, size, DEFAULT_MAX_SIZE);
     }
     
 
     @Override
     public void clear(){
         super.clear();
         clearCache();
     }
 
     private void clearCache() {
         log.debug("Clear cache");
         threadCache = new InheritableThreadLocal<Map<String, CacheEntry>>(){
             @Override
             protected Map<String, CacheEntry> initialValue(){
                 // Bug 51942 - this map may be used from multiple threads
                 @SuppressWarnings("unchecked") // LRUMap is not generic currently
                 Map<String, CacheEntry> map = new LRUMap(getMaxSize());
                 return Collections.<String, CacheEntry>synchronizedMap(map);
             }
         };
     }
+    
+    /**
+     * create a cache manager that share the underlying cache of the current one
+     * it allows to use the same cache in different threads which does not inherit from each other
+     * @return a cache manager that share the underlying cache of the current one
+     * @since 3.0
+     */
+    public CacheManager createCacheManagerProxy() {
+        CacheManager cm = new CacheManager(getCache(), this.useExpires);
+        return cm;
+    }
 
     @Override
     public void testStarted() {
     }
 
     @Override
     public void testEnded() {
     }
 
     @Override
     public void testStarted(String host) {
     }
 
     @Override
     public void testEnded(String host) {
     }
 
     @Override
     public void testIterationStart(LoopIterationEvent event) {
         if (getClearEachIteration()) {
             clearCache();
         }
-        useExpires=getUseExpires(); // cache the value
+        useExpires = getUseExpires(); // cache the value
     }
 
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
index 95ca05d8d..1ebbc61d2 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPHC4Impl.java
@@ -1,1434 +1,1456 @@
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
+    
+    private static final int VALIDITY_AFTER_INACTIVITY_TIMEOUT = JMeterUtils.getPropDefault("httpclient4.validate_after_inactivity", 2000);
+    
+    private static final int TIME_TO_LIVE = JMeterUtils.getPropDefault("httpclient4.time_to_live", 2000);
 
     private static final String CONTEXT_METRICS = "jmeter_metrics"; // TODO hack for metrics related to HTTPCLIENT-1081, to be removed later
 
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
+    
+    private static final String HTTPCLIENT_TOKEN = "__jmeter.HTTPCLIENT_TOKEN__";
 
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
 
             res.setResponseHeaders(getResponseHeaders(httpResponse));
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
+            JMeterContextService.getContext().getSamplerContext().remove(HTTPCLIENT_TOKEN);
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
         
-        HttpClient httpClient = mapHttpClientPerHttpClientKey.get(key);
+        HttpClient httpClient = null;
+        if(this.testElement.isConcurrentDwn()) {
+            httpClient = (HttpClient) JMeterContextService.getContext().getSamplerContext().get(HTTPCLIENT_TOKEN);
+        }
+        
+        if (httpClient == null) {
+            httpClient = mapHttpClientPerHttpClientKey.get(key);
+        }
 
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
-            MeasuringConnectionManager connManager = new MeasuringConnectionManager(createSchemeRegistry(), resolver);
+            MeasuringConnectionManager connManager = new MeasuringConnectionManager(
+                    createSchemeRegistry(), 
+                    resolver, 
+                    TIME_TO_LIVE,
+                    VALIDITY_AFTER_INACTIVITY_TIMEOUT);
             
             // Modern browsers use more connections per host than the current httpclient default (2)
             // when using parallel download the httpclient and connection manager are shared by the downloads threads
             // to be realistic JMeter must set an higher value to DefaultMaxPerRoute
             if(this.testElement.isConcurrentDwn()) {
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
             ((AbstractHttpClient) httpClient).addResponseInterceptor(new ResponseContentEncoding());
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
 
+        if(this.testElement.isConcurrentDwn()) {
+            JMeterContextService.getContext().getSamplerContext().put(HTTPCLIENT_TOKEN, httpClient);
+        }
+
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
      * @return string containing the headers, one per line
      */
     private String getResponseHeaders(HttpResponse response) {
         StringBuilder headerBuf = new StringBuilder();
         Header[] rh = response.getAllHeaders();
         headerBuf.append(response.getStatusLine());// header[0] is not the status line...
         headerBuf.append("\n"); // $NON-NLS-1$
 
         for (Header responseHeader : rh) {
             headerBuf.append(responseHeader.getName());
             headerBuf.append(": "); // $NON-NLS-1$
             headerBuf.append(responseHeader.getValue());
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
             if (portString.matches("^\\d+$")) {
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
             StringBuilder hdrs = new StringBuilder(100);
             Header[] requestHeaders = method.getAllHeaders();
             for (Header requestHeader : requestHeaders) {
                 // Exclude the COOKIE header, since cookie is reported separately in the sample
                 if (!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(requestHeader.getName())) {
                     hdrs.append(requestHeader.getName());
                     hdrs.append(": "); // $NON-NLS-1$
                     hdrs.append(requestHeader.getValue());
                     hdrs.append("\n"); // $NON-NLS-1$
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
                         if (contentEncoding != null) {
                             postedBody.append(new String(bos.toByteArray(), contentEncoding));
                         } else {
                             postedBody.append(new String(bos.toByteArray(), SampleResult.DEFAULT_HTTP_ENCODING));
                         }
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
index 1caddc327..e03a7f22e 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/HTTPSamplerBase.java
@@ -1,2068 +1,1962 @@
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
-import java.util.concurrent.LinkedBlockingQueue;
-import java.util.concurrent.ThreadFactory;
-import java.util.concurrent.ThreadPoolExecutor;
-import java.util.concurrent.TimeUnit;
-import java.util.concurrent.TimeoutException;
 
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
+import org.apache.jmeter.protocol.http.sampler.ResourcesDownloader.AsynSamplerResultHolder;
 import org.apache.jmeter.protocol.http.util.ConversionUtils;
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
     
-    private static final long KEEPALIVETIME = 0; // for Thread Pool for resources but no need to use a special value?
-    
-    private static final long AWAIT_TERMINATION_TIMEOUT = 
-        JMeterUtils.getPropDefault("httpsampler.await_termination_timeout", 60); // $NON-NLS-1$ // default value: 60 secs 
-    
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
 
     private static final int SOURCE_TYPE_DEFAULT = HTTPSamplerBase.SourceType.HOSTNAME.ordinal();
 
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
 
     public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw"; // TODO - belongs elsewhere 
 
     public static final boolean POST_BODY_RAW_DEFAULT = false;
 
     private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
 
     private static final String QRY_SEP = "&"; // $NON-NLS-1$
 
     private static final String QRY_PFX = "?"; // $NON-NLS-1$
 
     protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 5); // $NON-NLS-1$
 
     protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$
 
 
     // Derive the mapping of content types to parsers
     private static final Map<String, String> PARSERS_FOR_CONTENT_TYPE = new HashMap<>();
     // Not synch, but it is not modified after creation
 
     private static final String RESPONSE_PARSERS= // list of parsers
         JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
 
     static{
         String[] parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
         for (final String parser : parsers) {
             String classname = JMeterUtils.getProperty(parser + ".className");//$NON-NLS-1$
             if (classname == null) {
                 log.error("Cannot find .className property for " + parser+", ensure you set property:'"+parser+".className'");
                 continue;
             }
             String typelist = JMeterUtils.getProperty(parser + ".types");//$NON-NLS-1$
             if (typelist != null) {
                 String[] types = JOrphanUtils.split(typelist, " ", true);
                 for (final String type : types) {
                     log.info("Parser for " + type + " is " + classname);
                     PARSERS_FOR_CONTENT_TYPE.put(type, classname);
                 }
             } else {
                 log.warn("Cannot find .types property for " + parser 
                         + ", as a consequence parser will not be used, to make it usable, define property:'"+parser+".types'");
             }
         }
     }
 
     // Bug 49083
     /** Whether to remove '/pathsegment/..' from redirects; default true */
     private static final boolean REMOVESLASHDOTDOT = JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
 
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
         if(getPostBodyRaw()) {
             return true;
         } else {
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
         if(HTTPConstants.POST.equals(getMethod()) && (getDoMultipartPost() || (files.length > 0 && !getSendFileAsPostBody()))) {
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
         if (!fullUrl && (HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod()))) {
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
         if (log.isDebugEnabled()){
             log.debug("adding argument: name: " + name + " value: " + value + " metaData: " + metaData + " contentEncoding: " + contentEncoding);
         }
 
         HTTPArgument arg = null;
         final boolean nonEmptyEncoding = !StringUtils.isEmpty(contentEncoding);
         if(nonEmptyEncoding) {
             arg = new HTTPArgument(name, value, metaData, true, contentEncoding);
         }
         else {
             arg = new HTTPArgument(name, value, metaData, true);
         }
 
         // Check if there are any difference between name and value and their encoded name and value
         String valueEncoded = null;
         if(nonEmptyEncoding) {
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
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTP)  ? HTTPConstants.DEFAULT_HTTP_PORT :
                 protocol.equalsIgnoreCase(HTTPConstants.PROTOCOL_HTTPS) ? HTTPConstants.DEFAULT_HTTPS_PORT :
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
                 (HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTP_PORT) ||
                 (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTPS_PORT)) {
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
             if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(prot)) {
                 return HTTPConstants.DEFAULT_HTTPS_PORT;
             }
             if (!HTTPConstants.PROTOCOL_HTTP.equalsIgnoreCase(prot)) {
                 log.warn("Unexpected protocol: "+prot);
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
 
     public void setArguments(Arguments value) {
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
         return getPropertyAsString(EMBEDDED_URL_RE,"");
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
         res.setResponseCode(NON_HTTP_RESPONSE_CODE+": "+e.getClass().getName());
         res.setResponseMessage(NON_HTTP_RESPONSE_MESSAGE+": "+e.getMessage());
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
          || path.startsWith(HTTPS_PREFIX)){
             return new URL(path);
         }
         String domain = getDomain();
         String protocol = getProtocol();
         if (PROTOCOL_FILE.equalsIgnoreCase(protocol)) {
             domain=null; // allow use of relative file URLs
         } else {
             // HTTP URLs must be absolute, allow file to be relative
             if (!path.startsWith("/")){ // $NON-NLS-1$
                 pathAndQuery.append("/"); // $NON-NLS-1$
             }
         }
         pathAndQuery.append(path);
 
         // Add the query string if it is a HTTP GET or DELETE request
         if(HTTPConstants.GET.equals(getMethod()) || HTTPConstants.DELETE.equals(getMethod())) {
             // Get the query string encoded in specified encoding
             // If no encoding is specified by user, we will get it
             // encoded in UTF-8, which is what the HTTP spec says
             String queryString = getQueryString(getContentEncoding());
             if(queryString.length() > 0) {
                 if (path.contains(QRY_PFX)) {// Already contains a prefix
                     pathAndQuery.append(QRY_SEP);
                 } else {
                     pathAndQuery.append(QRY_PFX);
                 }
                 pathAndQuery.append(queryString);
             }
         }
         // If default port for protocol is used, we do not include port in URL
         if(isProtocolDefaultPort()) {
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
          // Check if the sampler has a specified content encoding
          if(JOrphanUtils.isBlank(contentEncoding)) {
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
 
             String metaData; // records the existance of an equal sign
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
             if(HTTPConstants.POST.equals(getMethod()) || HTTPConstants.PUT.equals(getMethod())) {
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
             if(res != null) {
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
             if (responseData.length > 0){  // Bug 39205
                 final LinkExtractorParser parser = getParser(res);
                 if(parser != null) {
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
             if (re.length()>0){
                 try {
                     pattern = JMeterUtils.getPattern(re);
                     localMatcher = JMeterUtils.getMatcher();// don't fetch unless pattern compiles
                 } catch (MalformedCachePatternException e) {
                     log.warn("Ignoring embedded URL match string: "+e.getMessage());
                 }
             }
             
             // For concurrent get resources
             final List<Callable<AsynSamplerResultHolder>> list = new ArrayList<>();
 
             int maxConcurrentDownloads = CONCURRENT_POOL_SIZE; // init with default value
             boolean isConcurrentDwn = isConcurrentDwn();
             if(isConcurrentDwn) {
-                
                 try {
                     maxConcurrentDownloads = Integer.parseInt(getConcurrentPool());
                 } catch (NumberFormatException nfe) {
                     log.warn("Concurrent download resources selected, "// $NON-NLS-1$
                             + "but pool size value is bad. Use default value");// $NON-NLS-1$
                 }
                 
                 // if the user choose a number of parallel downloads of 1
                 // no need to use another thread, do the sample on the current thread
                 if(maxConcurrentDownloads == 1) {
                     log.warn("Number of parallel downloads set to 1, (sampler name="+getName()+")");
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
-                        String urlStrEnc=escapeIllegalURLCharacters(encodeSpaces(urlstr));
+                        String urlStrEnc = escapeIllegalURLCharacters(encodeSpaces(urlstr));
                         if (!urlstr.equals(urlStrEnc)){// There were some spaces in the URL
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
                         } catch (MalformedURLException|URISyntaxException e) {
                             res.addSubResult(errorResult(new Exception(urlStrEnc + " URI can not be normalized", e), new HTTPSampleResult(res)));
                             setParentSampleSuccess(res, false);
                             continue;
                         }
+
                         if (isConcurrentDwn) {
                             // if concurrent download emb. resources, add to a list for async gets later
                             list.add(new ASyncSample(url, HTTPConstants.GET, false, frameDepth + 1, getCookieManager(), this));
                         } else {
                             // default: serial download embedded resources
                             HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                             res.addSubResult(binRes);
                             setParentSampleSuccess(res, res.isSuccessful() && (binRes != null ? binRes.isSuccessful() : true));
                         }
 
                     }
                 } catch (ClassCastException e) { // TODO can this happen?
                     res.addSubResult(errorResult(new Exception(binURL + " is not a correct URI"), new HTTPSampleResult(res)));
                     setParentSampleSuccess(res, false);
                 }
             }
             
             // IF for download concurrent embedded resources
             if (isConcurrentDwn && !list.isEmpty()) {
 
-                final String parentThreadName = Thread.currentThread().getName();
-                // Thread pool Executor to get resources 
-                // use a LinkedBlockingQueue, note: max pool size doesn't effect
-                final ThreadPoolExecutor exec = new ThreadPoolExecutor(
-                        maxConcurrentDownloads, maxConcurrentDownloads, KEEPALIVETIME, TimeUnit.SECONDS,
-                        new LinkedBlockingQueue<Runnable>(),
-                        new ThreadFactory() {
-                            @Override
-                            public Thread newThread(final Runnable r) {
-                                Thread t = new CleanerThread(new Runnable() {
-                                    @Override
-                                    public void run() {
-                                        try {
-                                            r.run();
-                                        } finally {
-                                            ((CleanerThread)Thread.currentThread()).notifyThreadEnd();
-                                        }
-                                    }
-                                });
-                                t.setName(parentThreadName+"-ResDownload-" + t.getName()); //$NON-NLS-1$
-                                t.setDaemon(true);
-                                return t;
-                            }
-                        });
+                ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
 
-                boolean tasksCompleted = false;
                 try {
-                    // sample all resources with threadpool
-                    final List<Future<AsynSamplerResultHolder>> retExec = exec.invokeAll(list);
-                    // call normal shutdown (wait ending all tasks)
-                    exec.shutdown();
-                    // put a timeout if tasks couldn't terminate
-                    exec.awaitTermination(AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
+                    // sample all resources
+                    final List<Future<AsynSamplerResultHolder>> retExec = resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
                     CookieManager cookieManager = getCookieManager();
                     // add result to main sampleResult
                     for (Future<AsynSamplerResultHolder> future : retExec) {
-                        AsynSamplerResultHolder binRes;
-                        try {
-                            binRes = future.get(1, TimeUnit.MILLISECONDS);
-                            if(cookieManager != null) {
-                                CollectionProperty cookies = binRes.getCookies();
-                                for (JMeterProperty jMeterProperty : cookies) {
-                                    Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
-                                    cookieManager.add(cookie);
-                                }
+                        // this call will not block as the futures return by invokeAllAndAwaitTermination 
+                        //   are either done or cancelled
+                        AsynSamplerResultHolder binRes = future.get();
+                        if(cookieManager != null) {
+                            CollectionProperty cookies = binRes.getCookies();
+                            for (JMeterProperty jMeterProperty : cookies) {
+                                Cookie cookie = (Cookie) jMeterProperty.getObjectValue();
+                                cookieManager.add(cookie);
                             }
-                            res.addSubResult(binRes.getResult());
-                            setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
-                        } catch (TimeoutException e) {
-                            errorResult(e, res);
                         }
+                        res.addSubResult(binRes.getResult());
+                        setParentSampleSuccess(res, res.isSuccessful() && (binRes.getResult() != null ? binRes.getResult().isSuccessful():true));
                     }
-                    tasksCompleted = exec.awaitTermination(1, TimeUnit.MILLISECONDS); // did all the tasks finish?
                 } catch (InterruptedException ie) {
                     log.warn("Interrupted fetching embedded resources", ie); // $NON-NLS-1$
                 } catch (ExecutionException ee) {
                     log.warn("Execution issue when fetching embedded resources", ee); // $NON-NLS-1$
-                } finally {
-                    if (!tasksCompleted) {
-                        exec.shutdownNow(); // kill any remaining tasks
-                    }
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
         if( !StringUtils.isEmpty(parserClassName) ) {
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
             if (!escapedUrl.equals(url)) {
                 if(log.isDebugEnabled()) {
                     log.debug("Url '" + url + "' has been escaped to '" + escapedUrl
                         + "'. Please correct your webpage.");
                 }
             }
             return escapedUrl;
         } catch (Exception e1) {
             log.error("Error escaping URL:'"+url+"', message:"+e1.getMessage());
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
         if(index >=0) {
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
             if(log.isInfoEnabled()) {
                 log.info("No user agent extracted from requestHeaders:"+res);
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
         if(!IGNORE_FAILED_EMBEDDED_RESOURCES) {
             res.setSuccessful(initialValue);
             if(!initialValue) {
                 StringBuilder detailedMessage = new StringBuilder(80);
                 detailedMessage.append("Embedded resource download error:"); //$NON-NLS-1$
                 for (SampleResult subResult : res.getSubResults()) {
                     HTTPSampleResult httpSampleResult = (HTTPSampleResult) subResult;
                     if(!httpSampleResult.isSuccessful()) {
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
             if (!HTTPConstants.HEAD.equalsIgnoreCase(method)) {
                 method = HTTPConstants.GET;
             }
             try {
                 URL url = ConversionUtils.makeRelativeURL(lastRes.getURL(), location);
                 url = ConversionUtils.sanitizeUrl(url).toURL();
                 if (log.isDebugEnabled()) {
                     log.debug("Location as URL: " + url.toString());
                 }
                 HTTPSampleResult tempRes = sample(url, method, true, frameDepth);
                 if(tempRes != null) {
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
                 if(!invalidRedirectUrl) {
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
         if (isImageParser() && (SampleResult.TEXT).equals(res.getDataType()) && res.isSuccessful()) {
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
                 if(!wasRedirected) {
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
             for (HTTPFileArg file : files) {
                 if (file.isNotEmpty()) {
                     fileArgs.addHTTPFileArg(file);
                 }
             }
         }
         setHTTPFileArgs(fileArgs);
     }
 
     public static String[] getValidMethodsAsArray(){
         return METHODLIST.toArray(new String[METHODLIST.size()]);
     }
 
     public static boolean isSecure(String protocol){
         return HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(protocol);
     }
 
     public static boolean isSecure(URL url){
         return isSecure(url.getProtocol());
     }
 
     // Implement these here, to avoid re-implementing for sub-classes
     // (previously these were implemented in all TestElements)
     @Override
     public void threadStarted(){
     }
 
     @Override
     public void threadFinished(){
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
         try {
             byte[] readBuffer = new byte[8192]; // 8kB is the (max) size to have the latency ('the first packet')
             int bufferSize=32;// Enough for MD5
     
             MessageDigest md=null;
             boolean asMD5 = useMD5();
             if (asMD5) {
                 try {
                     md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
                 } catch (NoSuchAlgorithmException e) {
                     log.error("Should not happen - could not find MD5 digest", e);
                     asMD5=false;
                 }
             } else {
                 if (length <= 0) {// may also happen if long value > int.max
                     bufferSize = 4 * 1024;
                 } else {
                     bufferSize = length;
                 }
             }
             ByteArrayOutputStream w = new ByteArrayOutputStream(bufferSize);
             int bytesRead = 0;
             int totalBytes = 0;
             boolean first = true;
             while ((bytesRead = in.read(readBuffer)) > -1) {
                 if (first) {
                     sampleResult.latencyEnd();
                     first = false;
                 }
                 if (asMD5 && md != null) {
                     md.update(readBuffer, 0 , bytesRead);
                     totalBytes += bytesRead;
                 } else {
                     w.write(readBuffer, 0, bytesRead);
                 }
             }
             if (first){ // Bug 46838 - if there was no data, still need to set latency
                 sampleResult.latencyEnd();
             }
             in.close();
             w.flush();
             if (asMD5 && md != null) {
                 byte[] md5Result = md.digest();
                 w.write(JOrphanUtils.baToHexBytes(md5Result)); 
                 sampleResult.setBytes(totalBytes);
             }
             w.close();
             return w.toByteArray();
         } finally {
             IOUtils.closeQuietly(in);
         }
     }
 
     /**
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
         if(oldStyleFile.isNotEmpty()) { // OK, we have an old-style file definition
             allFileArgs.addHTTPFileArg(oldStyleFile); // save it
             // Now deal with any additional file arguments
             if(fileArgs != null) {
                 HTTPFileArg[] infiles = fileArgs.asArray();
                 for (HTTPFileArg infile : infiles) {
                     allFileArgs.addHTTPFileArg(infile);
                 }
             }
         } else {
             if(fileArgs != null) { // for new test plans that don't have FILE/PARAM/MIME properties
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
         return getPropertyAsString(IP_SOURCE,"");
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
         return getPropertyAsString(CONCURRENT_POOL,CONCURRENT_POOL_DEFAULT);
     }
 
     public void setConcurrentPool(String poolSize) {
         setProperty(CONCURRENT_POOL, poolSize, CONCURRENT_POOL_DEFAULT);
     }
 
     
     /**
      * Callable class to sample asynchronously resources embedded
      *
      */
     private static class ASyncSample implements Callable<AsynSamplerResultHolder> {
         final private URL url;
         final private String method;
         final private boolean areFollowingRedirect;
         final private int depth;
         private final HTTPSamplerBase sampler;
         private final JMeterContext jmeterContextOfParentThread;
 
         ASyncSample(URL url, String method,
                 boolean areFollowingRedirect, int depth,  CookieManager cookieManager, HTTPSamplerBase base){
             this.url = url;
             this.method = method;
             this.areFollowingRedirect = areFollowingRedirect;
             this.depth = depth;
             this.sampler = (HTTPSamplerBase) base.clone();
             // We don't want to use CacheManager clone but the parent one, and CacheManager is Thread Safe
             CacheManager cacheManager = base.getCacheManager();
             if (cacheManager != null) {
-                this.sampler.setCacheManagerProperty(cacheManager);
+                this.sampler.setCacheManagerProperty(cacheManager.createCacheManagerProxy());
             }
             
             if(cookieManager != null) {
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
             if(sampler.getCookieManager() != null) {
                 CollectionProperty cookies = sampler.getCookieManager().getCookies();
                 return new AsynSamplerResultHolder(httpSampleResult, cookies);
             } else {
                 return new AsynSamplerResultHolder(httpSampleResult, new CollectionProperty());
             }
         }
     }
     
     /**
-     * Custom thread implementation that allows notification of threadEnd
-     *
-     */
-    private static class CleanerThread extends Thread {
-        private final List<HTTPSamplerBase> samplersToNotify = new ArrayList<>();
-        /**
-         * @param runnable Runnable
-         */
-        public CleanerThread(Runnable runnable) {
-           super(runnable);
-        }
-        
-        /**
-         * Notify of thread end
-         */
-        public void notifyThreadEnd() {
-            for (HTTPSamplerBase samplerBase : samplersToNotify) {
-                samplerBase.threadFinished();
-            }
-            samplersToNotify.clear();
-        }
-
-        /**
-         * Register sampler to be notify at end of thread
-         * @param sampler {@link HTTPSamplerBase}
-         */
-        public void registerSamplerForEndNotification(HTTPSamplerBase sampler) {
-            this.samplersToNotify.add(sampler);
-        }
-    }
-    
-    /**
-     * Holder of AsynSampler result
-     */
-    private static class AsynSamplerResultHolder {
-        private final HTTPSampleResult result;
-        private final CollectionProperty cookies;
-        /**
-         * @param result {@link HTTPSampleResult} to hold
-         * @param cookies cookies to hold
-         */
-        public AsynSamplerResultHolder(HTTPSampleResult result, CollectionProperty cookies) {
-            super();
-            this.result = result;
-            this.cookies = cookies;
-        }
-        /**
-         * @return the result
-         */
-        public HTTPSampleResult getResult() {
-            return result;
-        }
-        /**
-         * @return the cookies
-         */
-        public CollectionProperty getCookies() {
-            return cookies;
-        }
-    }
-    
-    /**
      * @see org.apache.jmeter.samplers.AbstractSampler#applies(org.apache.jmeter.config.ConfigTestElement)
      */
     @Override
     public boolean applies(ConfigTestElement configElement) {
         String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
         return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/MeasuringConnectionManager.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/MeasuringConnectionManager.java
index cd3e14574..318f4cb16 100644
--- a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/MeasuringConnectionManager.java
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/MeasuringConnectionManager.java
@@ -1,292 +1,297 @@
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
 
 import java.io.IOException;
 import java.net.InetAddress;
 import java.net.Socket;
 import java.util.concurrent.TimeUnit;
 
 import javax.net.ssl.SSLSession;
 
 import org.apache.http.HttpConnectionMetrics;
 import org.apache.http.HttpEntityEnclosingRequest;
 import org.apache.http.HttpException;
 import org.apache.http.HttpHost;
 import org.apache.http.HttpRequest;
 import org.apache.http.HttpResponse;
 import org.apache.http.conn.ClientConnectionOperator;
 import org.apache.http.conn.ClientConnectionRequest;
 import org.apache.http.conn.ConnectionPoolTimeoutException;
 import org.apache.http.conn.DnsResolver;
 import org.apache.http.conn.ManagedClientConnection;
 import org.apache.http.conn.routing.HttpRoute;
 import org.apache.http.conn.scheme.SchemeRegistry;
+import org.apache.http.impl.conn.JMeterPoolingClientConnectionManager;
 import org.apache.http.impl.conn.PoolingClientConnectionManager;
 import org.apache.http.params.HttpParams;
 import org.apache.http.protocol.HttpContext;
 import org.apache.jmeter.samplers.SampleResult;
 
 /**
  * Adapter for {@link PoolingClientConnectionManager}
  * that wraps all connection requests into time-measured implementation a private
  * MeasuringConnectionRequest
  */
-public class MeasuringConnectionManager extends PoolingClientConnectionManager {
+public class MeasuringConnectionManager extends JMeterPoolingClientConnectionManager {
 
-    public MeasuringConnectionManager(SchemeRegistry schemeRegistry, DnsResolver resolver) {
-        super(schemeRegistry, resolver);
+    
+    public MeasuringConnectionManager(SchemeRegistry schemeRegistry, 
+            DnsResolver resolver, 
+            int timeToLiveMs,
+            int validateAfterInactivityMs) {
+        super(schemeRegistry, timeToLiveMs, TimeUnit.MILLISECONDS, resolver, validateAfterInactivityMs);
     }
 
     @Override
     public ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
         ClientConnectionRequest res = super.requestConnection(route, state);
         MeasuringConnectionRequest measuredConnection = new MeasuringConnectionRequest(res);
         return measuredConnection;
     }
     
     /**
      * Overriden to use {@link JMeterClientConnectionOperator} and fix SNI issue 
      * @see "https://bz.apache.org/bugzilla/show_bug.cgi?id=57935"
      * @see org.apache.http.impl.conn.PoolingClientConnectionManager#createConnectionOperator(org.apache.http.conn.scheme.SchemeRegistry)
      */
     @Override
     protected ClientConnectionOperator createConnectionOperator(
             SchemeRegistry schreg) {
         return new JMeterClientConnectionOperator(schreg);
     }
 
 
     /**
      * An adapter class to pass {@link SampleResult} into {@link MeasuredConnection}
      */
     private static class MeasuringConnectionRequest implements ClientConnectionRequest {
         private final ClientConnectionRequest handler;
         public MeasuringConnectionRequest(ClientConnectionRequest res) {
             handler = res;
         }
 
         @Override
         public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
             ManagedClientConnection res = handler.getConnection(timeout, tunit);
             return new MeasuredConnection(res);
         }
 
         @Override
         public void abortRequest() {
             handler.abortRequest();
         }
     }
 
     /**
      * An adapter for {@link ManagedClientConnection}
      * that calls SampleResult.connectEnd after calling ManagedClientConnection.open
      */
     private static class MeasuredConnection implements ManagedClientConnection {
         private final ManagedClientConnection handler;
 
         public MeasuredConnection(ManagedClientConnection res) {
             handler = res;
         }
 
         @Override
         public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
             try {
                 handler.open(route, context, params);
             } finally {
                 SampleResult sample = 
                         (SampleResult)context.getAttribute(HTTPHC4Impl.SAMPLER_RESULT_TOKEN);
                 if (sample != null) {
                     sample.connectEnd();
                 }
             }
         }
 
         // ================= all following methods just wraps handler's =================
         @Override
         public boolean isSecure() {
             return handler.isSecure();
         }
 
         @Override
         public HttpRoute getRoute() {
             return handler.getRoute();
         }
 
         @Override
         public SSLSession getSSLSession() {
             return handler.getSSLSession();
         }
 
         @Override
         public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
             handler.tunnelTarget(secure, params);
         }
 
         @Override
         public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
             handler.tunnelProxy(next, secure, params);
         }
 
         @Override
         public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
             handler.layerProtocol(context, params);
         }
 
         @Override
         public void markReusable() {
             handler.markReusable();
         }
 
         @Override
         public void unmarkReusable() {
             handler.unmarkReusable();
         }
 
         @Override
         public boolean isMarkedReusable() {
             return handler.isMarkedReusable();
         }
 
         @Override
         public void setState(Object state) {
             handler.setState(state);
         }
 
         @Override
         public Object getState() {
             return handler.getState();
         }
 
         @Override
         public void setIdleDuration(long duration, TimeUnit unit) {
             handler.setIdleDuration(duration, unit);
         }
 
         @Override
         public void releaseConnection() throws IOException {
             handler.releaseConnection();
         }
 
         @Override
         public void abortConnection() throws IOException {
             handler.abortConnection();
         }
 
         @Override
         public boolean isResponseAvailable(int timeout) throws IOException {
             return handler.isResponseAvailable(timeout);
         }
 
         @Override
         public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
             handler.sendRequestHeader(request);
         }
 
         @Override
         public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
             handler.sendRequestEntity(request);
         }
 
         @Override
         public HttpResponse receiveResponseHeader() throws HttpException, IOException {
             return handler.receiveResponseHeader();
         }
 
         @Override
         public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
             handler.receiveResponseEntity(response);
         }
 
         @Override
         public void flush() throws IOException {
             handler.flush();
         }
 
         @Override
         public InetAddress getLocalAddress() {
             return handler.getLocalAddress();
         }
 
         @Override
         public int getLocalPort() {
             return handler.getLocalPort();
         }
 
         @Override
         public InetAddress getRemoteAddress() {
             return handler.getRemoteAddress();
         }
 
         @Override
         public int getRemotePort() {
             return handler.getRemotePort();
         }
 
         @Override
         public void close() throws IOException {
             handler.close();
         }
 
         @Override
         public boolean isOpen() {
             return handler.isOpen();
         }
 
         @Override
         public boolean isStale() {
             return handler.isStale();
         }
 
         @Override
         public void setSocketTimeout(int timeout) {
             handler.setSocketTimeout(timeout);
         }
 
         @Override
         public int getSocketTimeout() {
             return handler.getSocketTimeout();
         }
 
         @Override
         public void shutdown() throws IOException {
             handler.shutdown();
         }
 
         @Override
         public HttpConnectionMetrics getMetrics() {
             return handler.getMetrics();
         }
 
         @Override
         public void bind(Socket arg0) throws IOException {
             handler.bind(arg0);
         }
 
         @Override
         public String getId() {
             return handler.getId();
         }
 
         @Override
         public Socket getSocket() {
             return handler.getSocket();
         }
     }
 }
diff --git a/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java
new file mode 100644
index 000000000..04165eccd
--- /dev/null
+++ b/src/protocol/http/org/apache/jmeter/protocol/http/sampler/ResourcesDownloader.java
@@ -0,0 +1,245 @@
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
+ *
+ */
+
+package org.apache.jmeter.protocol.http.sampler;
+
+import java.util.ArrayList;
+import java.util.List;
+import java.util.concurrent.Callable;
+import java.util.concurrent.CompletionService;
+import java.util.concurrent.ExecutorCompletionService;
+import java.util.concurrent.Future;
+import java.util.concurrent.SynchronousQueue;
+import java.util.concurrent.ThreadFactory;
+import java.util.concurrent.ThreadPoolExecutor;
+import java.util.concurrent.TimeUnit;
+
+import org.apache.jmeter.testelement.property.CollectionProperty;
+import org.apache.jmeter.util.JMeterUtils;
+import org.apache.jorphan.logging.LoggingManager;
+import org.apache.log.Logger;
+
+/**
+ * Manages the parallel http resources download.<br>
+ * A shared thread pool is used by all the sample.<br>
+ * A sampler will usually do the following
+ * <pre> {@code 
+ *   // list of AsynSamplerResultHolder to download
+ *   List<Callable<AsynSamplerResultHolder>> list = ...
+ *   
+ *   // max parallel downloads
+ *   int maxConcurrentDownloads = ...
+ *   
+ *   // get the singleton instance
+ *   ResourcesDownloader resourcesDownloader = ResourcesDownloader.getInstance();
+ *   
+ *   // schedule the downloads and wait for the completion
+ *   List<Future<AsynSamplerResultHolder>> retExec = resourcesDownloader.invokeAllAndAwaitTermination(maxConcurrentDownloads, list);
+ *   
+ * }</pre>
+ * 
+ * the call to invokeAllAndAwaitTermination will block until the downloads complete or get interrupted<br>
+ * the Future list only contains task that have been scheduled in the threadpool.<br>
+ * The status of those futures are either done or cancelled<br>
+ * <br>
+ *  
+ *  Future enhancements :
+ *  <ul>
+ *  <li>this implementation should be replaced with a NIO async download
+ *   in order to reduce the number of threads needed</li>
+ *  </ul>
+ * @since 3.0
+ */
+public class ResourcesDownloader {
+
+    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    
+    /** this is the maximum time that excess idle threads will wait for new tasks before terminating */
+    private static final long THREAD_KEEP_ALIVE_TIME = JMeterUtils.getPropDefault("httpsampler.parallel_download_thread_keepalive_inseconds", 60L);
+    
+    private static final int MIN_POOL_SIZE = 1;
+    private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
+    
+    private static final ResourcesDownloader INSTANCE = new ResourcesDownloader();
+    
+    public static ResourcesDownloader getInstance() {
+        return INSTANCE;
+    }
+    
+    
+    private ThreadPoolExecutor concurrentExecutor = null;
+
+    private ResourcesDownloader() {
+        init();
+    }
+    
+    
+    private void init() {
+        LOG.info("Creating ResourcesDownloader with keepalive_inseconds:"+THREAD_KEEP_ALIVE_TIME);
+        ThreadPoolExecutor exec = new ThreadPoolExecutor(
+                MIN_POOL_SIZE, MAX_POOL_SIZE, THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
+                new SynchronousQueue<Runnable>(),
+                new ThreadFactory() {
+                    @Override
+                    public Thread newThread(final Runnable r) {
+                        Thread t = new Thread(r);
+                        t.setName("ResDownload-" + t.getName()); //$NON-NLS-1$
+                        t.setDaemon(true);
+                        return t;
+                    }
+                }) {
+
+        };
+        concurrentExecutor = exec;
+    }
+    
+    /**
+     * this method will try to shrink the thread pool size as much as possible
+     * it should be called at the end of a test
+     */
+    public void shrink() {
+        if(concurrentExecutor.getPoolSize() > MIN_POOL_SIZE) {
+            // drain the queue
+            concurrentExecutor.purge();
+            List<Runnable> drainList = new ArrayList<>();
+            concurrentExecutor.getQueue().drainTo(drainList);
+            if(!drainList.isEmpty()) {
+                LOG.warn("the pool executor workqueue is not empty size=" + drainList.size());
+                for (Runnable runnable : drainList) {
+                    if(runnable instanceof Future<?>) {
+                        Future<?> f = (Future<?>) runnable;
+                        f.cancel(true);
+                    }
+                    else {
+                        LOG.warn("Content of workqueue is not an instance of Future");
+                    }
+                }
+            }
+            
+            // this will force the release of the extra threads that are idle
+            // the remaining extra threads will be released with the keepAliveTime of the thread
+            concurrentExecutor.setMaximumPoolSize(MIN_POOL_SIZE);
+            
+            // do not immediately restore the MaximumPoolSize as it will block the release of the threads
+        }
+    }
+    
+    // probablyTheBestMethodNameInTheUniverseYeah!
+    /**
+     * This method will block until the downloads complete or it get interrupted
+     * the Future list returned by this method only contains tasks that have been scheduled in the threadpool.<br>
+     * The status of those futures are either done or cancelled
+     * 
+     * @param maxConcurrentDownloads max concurrent downloads
+     * @param list list of resources to download
+     * @return list tasks that have been scheduled
+     * @throws InterruptedException
+     */
+    public List<Future<AsynSamplerResultHolder>> invokeAllAndAwaitTermination(int maxConcurrentDownloads, List<Callable<AsynSamplerResultHolder>> list) throws InterruptedException {
+        List<Future<AsynSamplerResultHolder>> submittedTasks = new ArrayList<>();
+        
+        // paranoid fast path
+        if(list.isEmpty()) {
+            return submittedTasks;
+        }
+        
+        // restore MaximumPoolSize original value
+        concurrentExecutor.setMaximumPoolSize(MAX_POOL_SIZE);
+        
+        if(LOG.isDebugEnabled()) {
+            LOG.debug("PoolSize=" + concurrentExecutor.getPoolSize()+" LargestPoolSize=" + concurrentExecutor.getLargestPoolSize());
+        }
+        
+        CompletionService<AsynSamplerResultHolder> completionService = new ExecutorCompletionService<>(concurrentExecutor);
+        int remainingTasksToTake = list.size();
+        
+        try {
+            // push the task in the threadpool until <maxConcurrentDownloads> is reached
+            int i = 0;
+            for (i = 0; i < Math.min(maxConcurrentDownloads, list.size()); i++) {
+                Callable<AsynSamplerResultHolder> task = list.get(i);
+                submittedTasks.add(completionService.submit(task));
+            }
+            
+            // push the remaining tasks but ensure we use at most <maxConcurrentDownloads> threads
+            // wait for a previous download to finish before submitting a new one
+            for (; i < list.size(); i++) {
+                Callable<AsynSamplerResultHolder> task = list.get(i);
+                completionService.take();
+                remainingTasksToTake--;
+                submittedTasks.add(completionService.submit(task));
+            }
+            
+            // all the resources downloads are in the thread pool queue
+            // wait for the completion of all downloads
+            while (remainingTasksToTake > 0) {
+                completionService.take();
+                remainingTasksToTake--;
+            }
+        }
+        finally {
+            //bug 51925 : Calling Stop on Test leaks executor threads when concurrent download of resources is on
+            if(remainingTasksToTake > 0) {
+                if(LOG.isDebugEnabled()) {
+                    LOG.debug("Interrupted while waiting for resource downloads : cancelling remaining tasks");
+                }
+                for (Future<AsynSamplerResultHolder> future : submittedTasks) {
+                    if(!future.isDone()) {
+                        future.cancel(true);
+                    }
+                }
+            }
+        }
+        
+        return submittedTasks;
+    }
+    
+    
+    /**
+     * Holder of AsynSampler result
+     */
+    public static class AsynSamplerResultHolder {
+        private final HTTPSampleResult result;
+        private final CollectionProperty cookies;
+        
+        /**
+         * @param result {@link HTTPSampleResult} to hold
+         * @param cookies cookies to hold
+         */
+        public AsynSamplerResultHolder(HTTPSampleResult result, CollectionProperty cookies) {
+            super();
+            this.result = result;
+            this.cookies = cookies;
+        }
+        
+        /**
+         * @return the result
+         */
+        public HTTPSampleResult getResult() {
+            return result;
+        }
+        
+        /**
+         * @return the cookies
+         */
+        public CollectionProperty getCookies() {
+            return cookies;
+        }
+    }
+    
+}
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index ed37ee47b..3f99ec8bf 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,494 +1,495 @@
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
 
 
 <!--  =================== 3.0 =================== -->
 
 <h1>Version 3.0</h1>
 
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
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Since version 3.0, Groovy-2.4.6 is bundled with JMeter (lib folder), ensure you remove old version or referenced versions through properties <code>search_paths</code> or <code>user.classpath</code></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.assertion_results_failure_message</code> property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.print_field_names</code> property value is true, meaning CSV file for results will contain field names as first line in CSV, see <bugzilla>58991</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.idle_time</code> property value is true, meaning CSV/XML result files will contain an additional column containing idle time between samplers, see <bugzilla>57182</bugzilla></li>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 3.0, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 3.0, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. See <bugzilla>58687</bugzilla></li>
     <li>Property <code>jmeterthread.startearlier</code> has been removed. See <bugzilla>58726</bugzilla></li>   
     <li>Property <code>jmeterengine.startlistenerslater</code> has been removed. See <bugzilla>58728</bugzilla></li>   
     <li>Property <code>jmeterthread.reversePostProcessors</code> has been removed. See <bugzilla>58728</bugzilla></li>  
     <li>MongoDB elements (MongoDB Source Config, MongoDB Script) have been deprecated and will be removed in next version of jmeter. They do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. JMeter team advises not to use them anymore. See <bugzilla>58772</bugzilla></li>
     <li>Summariser listener now outputs a formated duration in HH:mm:ss (Hour:Minute:Second), it previously outputed seconds. See <bugzilla>58776</bugzilla></li>
     <li>WebService(SOAP) Request and HTML Parameter Mask which were deprecated in 2.13 version, have now been removed following our <a href="./usermanual/best-practices.html#deprecation">deprecation strategy</a>.
     Classes and properties which were only used by those elements have been dropped:
     <ul>
         <li><code>org.apache.jmeter.protocol.http.util.DOMPool</code></li>
         <li><code>org.apache.jmeter.protocol.http.util.WSDLException</code></li>
         <li><code>org.apache.jmeter.protocol.http.util.WSDLHelper</code></li>
         <li>Property <code>soap.document_cache</code></li>
         <li>JAR soap-2.3.1 has been also removed</li>
     </ul>
     </li>
     <li>org.apache.jmeter.protocol.http.visualizers.RequestViewHTTP.getQueryMap signature has changed, if you use it ensure you update your code. See <bugzilla>58845</bugzilla></li>
     <li><code>__jexl</code> function has been deprecated and will be removed in next version. See <bugzilla>58903</bugzilla></li>
     <li>JMS Subscriber will consider sample in error if number of received messages is not equals to expected number of messages. It previously considerer sample OK if only 1 message was received. See <bugzilla>58980</bugzilla></li>
     <li>Since version 3.0, HTTP(S) Test Script recorder uses default port 8888 as configured when using Recording Template. See <bugzilla>59006</bugzilla></li>
     <li>Since version 3.0, the parser for embedded ressources (replaced since 2.10 by Lagarto based implementation) relying on htmlparser library (HtmlParserHTMLParser) has been dropped as long as its dependencies.</li>
     <li>Since version 3.0, the support for reading old Avalon format JTL (result) files has been removed, see <bugzilla>59064</bugzilla></li>
     <li>Since version 3.0, default property's value <code>http.java.sampler.retries</code> has been switched to 0 (no retry by default) to align it on HttpClient4's behaviour. 
     Note also that its meaning has changed, before 3.0, <code>http.java.sampler.retries=1</code> meant No Retry, since 3.0 it is more litteral, <code>http.java.sampler.retries=1</code> means 1 retry.
     See <bugzilla>59103</bugzilla></li>
     <li>Since 3.0, the following deprecated classes have been dropped
     <ul>
         <li>org.apache.jmeter.protocol.http.modifier.UserParameterXMLContentHandler</li>
         <li>org.apache.jmeter.protocol.http.modifier.UserParameterXMLErrorHandler</li>
         <li>org.apache.jmeter.protocol.http.modifier.UserParameterXMLParser</li>
     </ul>
     </li>
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
     <li><bug>58811</bug>When pasting arguments between http samplers the column "Encode" and "Include Equals" are lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58843</bug>Improve the usable space in the HTTP sampler GUI. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58852</bug>Use less memory for <code>PUT</code> requests. The uploaded data will no longer be stored in the Sampler.
         This is the same behaviour as with <code>POST</code> requests.</li>
     <li><bug>58860</bug>HTTP Request : Add automatic variable generation in HTTP parameters table by right click. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58923</bug>normalize URIs when downloading embedded resources.</li>
     <li><bug>59005</bug>HTTP Sampler : Added WebDAV verb (SEARCH).</li>
     <li><bug>59006</bug>Change Default proxy recording port to 8888 to align it with Recording Template. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>58099</bug>Performance : Lazily initialize HttpClient SSL Context to avoid its initialization even for HTTP only scenarios</li>
     <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources, add property "httpsampler.embedded_resources_use_md5" to only compute md5 and not keep response data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59023</bug>HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59028</bug>Use SystemDefaultDnsResolver singleton. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59036</bug>FormCharSetFinder : Use JSoup instead of deprecated HTMLParser</li>
     <li><bug>59034</bug>Parallel downloads connection management is not realistic. Contributed by Benoit Wiart (benoit dot wiart at gmail.com) and Philippe Mouawad</li>
     <li><bug>59060</bug>HTTP Request GUI : Move File Upload to a new Tab to have more space for parameters and prevent incoherent configuration. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59103</bug>HTTP Request Java Implementation: Change default "http.java.sampler.retries" to align it on HttpClient behaviour and make it meaningful</li>
     <li><bug>59083</bug>HTTP Request : Make Method field editable so that additional methods (Webdav) can be added easily</li>
     <li><bug>59118</bug>Add comment in recorded think time by proxy recorder. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>59116</bug>Add the possibility to setup a prefix to sampler name recorded by proxy. Partly based on a patch by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>59129</bug>HTTP Request : Simplify GUI with simple/advanced Tabs</li>
     <li><bug>59033</bug>Parallel Download : Rework Parser classes hierarchy to allow pluging parsers for different mime types</li>
     <li><bug>59146</bug>MeasuringConnectionManager is not Thread Safe (nightly before 3.0)</li>
+    <li><bug>52073</bug>Embedded Resources Parallel download : Improve performances by avoiding shutdown of ThreadPoolExecutor at each sample </li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>58786</bug>JDBC Sampler : Replace Excalibur DataSource by more up to date library commons-dbcp2</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58989</bug>Record controller gui : add a button to clear all the recorded samples. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 <li><bug>58776</bug>Summariser should display a more readable duration</li>
 <li><bug>58791</bug>Deprecate listeners:Distribution Graph (alpha) and Spline Visualizer</li>
 <li><bug>58849</bug>View Results Tree : Add a search panel to the request http view to be able to search in the parameters table. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58857</bug>View Results Tree : the request view http does not allow to resize the parameters table first column. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58955</bug>Request view http does not correctly display http parameters in multipart/form-data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>55597</bug>View Results Tree: Add a search feature to search in recorded samplers</li>
 <li><bug>59102</bug>View Results Tree: Better default value for "view.results.tree.max_size"</li>
 <li><bug>59099</bug>Backend listener : Add the possibility to consider samplersList as a Regular Expression. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
   <li><bug>58698</bug>Correct parsing of auth-files in HTTP Authorization Manager.</li>
   <li><bug>58756</bug>CookieManager : Cookie Policy select box content must depend on Cookie implementation.</li>
   <li><bug>56358</bug>Cookie manager supports cross port cookies and RFC6265. Thanks to Oleg Kalnichevski (olegk at apache.org)</li>
   <li><bug>58773</bug>TestCacheManager : Add tests for CacheManager that use HttpClient 4</li>
   <li><bug>58742</bug>CompareAssertion : Reset data in TableEditor when switching between different CompareAssertions in gui.
       Based on a patch by Vincent Herilier (vherilier at gmail.com)</li>
   <li><bug>58848</bug>Argument Panel : when adding an argument (add button or from clipboard) scroll the table to the new line. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
   <li><bug>58865</bug>Allow empty default value in the Regular Expression Extractor. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
     <li><bug>58903</bug>Provide __jexl3 function that uses commons-jexl3 and deprecated __jexl (1.1) function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>58736</bug>Add Sample Timeout support</li>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>SVN Revision ID</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie / UbikLoadPack</li>
 <li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58726</bug>Remove the <code>jmeterthread.startearlier</code> parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58728</bug>Drop old behavioural properties</li>
 <li><bug>57319</bug>Upgrade to HttpClient 4.5.2. With the big help from Oleg Kalnichevski (olegk at apache.org) and Gary Gregory (ggregory at apache.org).</li>
 <li><bug>58772</bug>Deprecate MongoDB related elements</li>
 <li><bug>58782</bug>ThreadGroup : Improve ergonomy</li>
 <li><bug>58165</bug>Show the time elapsed since the start of the load test in GUI mode. Partly based on a contribution from Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><bug>58784</bug>Make JMeterUtils#runSafe sync/async awt invocation configurable and change the visualizers to use the async version.</li>
 <li><bug>58790</bug>Issue in CheckDirty and its relation to ActionRouter</li>
 <li><bug>58814</bug>JVM don't recognize option MaxLiveObjectEvacuationRatio; remove from comments</li>
 <li><bug>58810</bug>Config Element Counter (and others): Check Boxes Toggle Area Too Big</li>
 <li><bug>56554</bug>JSR223 Test Element : Generate compilation cache key automatically. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58911</bug>Header Manager : it should be possible to copy/paste between Header Managers. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58864</bug>Arguments Panel : when moving parameter with up / down, ensure that the selection remains visible. Based on a contribution by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58924</bug>Dashboard / report : It should be possible to export the generated graph as image (PNG). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58884</bug>JMeter report generator : need better error message. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58957</bug>Report/Dashboard: HTML Exporter does not create parent directories for output directory. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58968</bug>Add a new template to allow to record script with think time included. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li><bug>58978</bug>Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13)</li>
 <li><bug>58991</bug>Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13)</li>
 <li><bug>57182</bug>Settings defaults : Switch "jmeter.save.saveservice.idle_time" to true (after 2.13)</li>
 <li><bug>58987</bug>Report/Dashboard: Improve error reporting.</li>
 <li><bug>58870</bug>TableEditor: minimum size is too small. Contributed by Vincent Herilier (vherilier at gmail.com)</li>
 <li><bug>59037</bug>Drop HtmlParserHTMLParser and dependencies on htmlparser and htmllexer</li>
 <li><bug>58933</bug>JSyntaxTextArea : Ability to set font.  Contributed by Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li><bug>58793</bug>Create developers page explaining how to build and contribute</li>
 <li><bug>59046</bug>JMeter Gui Replace controller should keep the name and the selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59038</bug>Deprecate HTTPClient 3.1 related elements</li>
 <li><bug>59094</bug>Drop support of old JMX file format</li>
 <li><bug>59082</bug>Remove the "TestCompiler.useStaticSet" parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59093</bug>Option parsing error message can be 'lost'</li>
 <li><bug>58715</bug>Feature request: Bundle groovy-all with JMeter</li>
 <li><bug>59095</bug>Remove UserParameterXMLParser that was deprecated 8 years ago. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58426</bug>Use DPI scaling of interface for high resolution devices (HiDPI support for Windows/Linux) - <i>BETA</i> see hidpi properties in bin/jmeter.properties</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to httpclient, httpmime 4.5.2 (from 4.2.6)</li>
 <li>Updated to tika-core and tika-parsers 1.12 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7.1 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.7.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.3 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.8 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li>Updated to commons-collections-3.2.2 (from 3.2.1)</li>
 <li>Updated to commons-net 3.4 (from 3.3)</li>
 <li>Updated to slf4j 1.7.13 (from 1.7.12)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58705</bug>Make org.apache.jmeter.testelement.property.MultiProperty iterable. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58729</bug>Cleanup extras folder for maintainability</li>
 <li><bug>57110</bug>Fixed spelling+grammar, formatting, removed commented out code etc. Contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Correct instructions on running jmeter in help.txt. Contributed by Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li><bug>58704</bug>Non regression testing : Ant task batchtest fails if tests and run in a non en_EN locale and use a JMX file that uses a Csv DataSet</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58949</bug>Cleanup of ldap code. Based on a patch by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58967</bug>Use junit categories to exclude tests that need a gui. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59003</bug>ClutilTestCase testSingleArg8 and testSingleArg9 are identical</li>
 <li><bug>59064</bug>Remove OldSaveService which supported very old Avalon format JTL (result) files</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
     <li><bug>57804</bug>HTTP Request doesn't reuse cached SSL context when using Client Certificates in HTTPS (only fixed for HttpClient4 implementation)</li>
     <li><bug>58800</bug>proxy.pause default value , fix documentation</li>
     <li><bug>58844</bug>Buttons enable / disable is broken in the arguments panel. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58861</bug>When clicking on up, down or detail while in a cell of the argument panel, newly added content is lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>57935</bug>SSL SNI extension not supported by HttpClient 4.2.6</li>
     <li><bug>59044</bug>Http Sampler : It should not be possible to select the multipart encoding if the method is not POST. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59008</bug>Http Sampler: Infinite recursion SampleResult on frame depth limit reached</li>
     <li><bug>59069</bug>CookieManager : Selected Cookie Policy is always reset to default when saving or switching to another TestElement (nightly build 25th feb 2016)</li>
     <li><bug>58881</bug>HTTP Request : HTTPHC4Impl shows exception when server uses "deflate" compression</li>
     <li><bug>58583</bug>HTTP client fails to close connection if server misbehaves by not sending "connection: close", violating HTTP RFC 2616 / RFC 7230</li>
     <li><bug>58950</bug>NoHttpResponseException when Pause between samplers exceeds keepalive sent by server</li>
     <li><bug>59085</bug>Http file panel : data lost on browse cancellation. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>56141</bug>Application does not behave correctly when using HTTP Recorder. With the help of Dan (java.junkee at yahoo.com)</li>
     <li><bug>59079</bug>"httpsampler.max_redirects" property is not enforced when "Redirect Automatically" is used</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
     <li><bug>58851</bug>Add a dependency to hamcrest-core to allow JUnit tests with annotations to work</li>
     <li><bug>58947</bug>Connect metric is wrong when ConnectException occurs</li>
     <li><bug>58980</bug>JMS Subscriber will return successful as long as 1 message is received. Contributed by Harrison Termotto (harrison dot termotto at stonybrook.edu)</li>
     <li><bug>59051</bug>JDBC Request : Connection is closed by pool if it exceeds the configured lifetime (affects nightly build as of 23 fev 2016).</li>
     <li><bug>59075</bug>JMS Publisher: NumberFormatException is thrown is priority or expiration fields are empty</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58714</bug>Module controller : it should not be possible to add a timer as child. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59067</bug>JMeter fails to iterate over Controllers that are children of a TransactionController having "Generate parent sample" checked after an assertion error occurs on a Thread Group with "Start Next Thread Loop". Contributed by Benoit Wiart(benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug>SampleResultConverter should note that it cannot record non-TEXT data</li>
 <li><bug>58845</bug>Request http view doesn't display all the parameters. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58413</bug>ViewResultsTree : Request HTTP Renderer does not show correctly parameters that contain ampersand (&amp;). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
 <li><bug>58912</bug>Response assertion gui : Deleting more than 1 selected row deletes only one row. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
     <li><bug>58781</bug>Command line option "-?" shows Unknown option</li>
     <li><bug>58795</bug>NPE may occur in GuiPackage#getTestElementCheckSum with some 3rd party plugins</li>
     <li><bug>58913</bug>When closing jmeter should not interpret cancel as "destroy my test plan". Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58952</bug>Report/Dashboard: Generation of aggregated series in graphs does not work. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>58931</bug>New Report/Dashboard : Getting font errors under Firefox and Chrome (not Safari)</li>
     <li><bug>58932</bug>Report / Dashboard: Document clearly and log what report are not generated when saveservice options are not correct. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>59055</bug>JMeter report generator : When generation is not launched from jmeter/bin folder report-template is not found</li>
     <li><bug>58986</bug>Report/Dashboard reuses the same output directory</li>
     <li><bug>59096</bug>Search Feature : Case insensitive search is not really case insensitive</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
 <li>Oleg Kalnichevski (olegk at apache.org)</li>
 <li>Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Florent Sabbe (f dot sabbe at ubik-ingenierie.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Harrison Termotto (harrison dot termotto at stonybrook.edu</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li>Gary Gregory (ggregory at apache.org)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (SHIFT + up/down) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
